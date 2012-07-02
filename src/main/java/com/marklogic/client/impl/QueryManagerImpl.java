/*
 * Copyright 2012 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;

import com.marklogic.client.ElementLocator;
import com.marklogic.client.Format;
import com.marklogic.client.KeyLocator;
import com.marklogic.client.QueryManager;
import com.marklogic.client.Transaction;
import com.marklogic.client.config.DeleteQueryDefinition;
import com.marklogic.client.config.KeyValueQueryDefinition;
import com.marklogic.client.config.MatchDocumentSummary;
import com.marklogic.client.config.QueryDefinition;
import com.marklogic.client.config.StringQueryDefinition;
import com.marklogic.client.config.StructuredQueryBuilder;
import com.marklogic.client.config.ValuesDefinition;
import com.marklogic.client.config.ValuesListDefinition;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.io.marker.QueryOptionsListReadHandle;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.io.marker.ValuesListReadHandle;
import com.marklogic.client.io.marker.ValuesReadHandle;

public class QueryManagerImpl extends AbstractLoggingManager implements QueryManager {
    private RESTServices services = null;
    private long pageLen = -1;
    private ResponseViews views = new ResponseViewsImpl();

    public QueryManagerImpl(RESTServices services) {
        this.services = services;
    }

    public long getPageLength() {
        return pageLen;
    }

    public void setPageLength(long length) {
        pageLen = length;
    }

    public ResponseViews getViews() {
        return views;
    }

    public void setViews(ResponseViews views) {
        this.views = views;
    }

    public void setViews(QueryView... views) {
        this.views = new ResponseViewsImpl();
        for (QueryView view : views) {
            this.views.add(view);
        }
    }

    @Override
    public StringQueryDefinition newStringDefinition(String optionsName) {
        return new StringQueryDefinitionImpl(optionsName);
    }

    @Override
    public KeyValueQueryDefinition newKeyValueDefinition(String optionsName) {
        return new KeyValueQueryDefinitionImpl(optionsName);
    }

    @Override
    public StructuredQueryBuilder newStructuredQueryBuilder(String optionsName) {
        return new StructuredQueryBuilder(optionsName);
    }

    @Override
    public DeleteQueryDefinition newDeleteDefinition() {
        return new DeleteQueryDefinitionImpl();
    }

    @Override
    public ElementLocator newElementLocator(QName element) {
        return new ElementLocatorImpl(element);
    }

    @Override
    public ElementLocator newElementLocator(QName element, QName attribute) {
        return new ElementLocatorImpl(element, attribute);
    }

    @Override
    public KeyLocator newKeyLocator(String key) {
        return new KeyLocatorImpl(key);
    }

    @Override
    public ValuesDefinition newValuesDefinition(String optionsName) {
        return new ValuesDefinitionImpl(optionsName);
    }

    @Override
    public ValuesListDefinition newValuesListDefinition(String optionsName) {
        return new ValuesListDefinitionImpl(optionsName);
    }

    @Override
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle) {
        return search(querydef, searchHandle, 1, null);
    }

    @Override
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle, long start) {
        return search(querydef, searchHandle, start, null);
    }

    @Override
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle, Transaction transaction) {
        return search(querydef, searchHandle, 1, transaction);
    }

    @Override
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle, long start, Transaction transaction) {
		HandleImplementation searchBase = HandleAccessor.checkHandle(searchHandle, "search");

        if (searchHandle instanceof SearchHandle) {
            ((SearchHandle) searchHandle).setQueryCriteria(querydef);
        }

        Format searchFormat = searchBase.getFormat();
        switch(searchFormat) {
        case UNKNOWN:
        	searchFormat = Format.XML;
        	break;
        case JSON:
        case XML:
        	break;
        default:
            throw new UnsupportedOperationException("Only XML and JSON search results are possible.");
        }

        String mimetype = searchFormat.getDefaultMimetype();

        String tid = transaction == null ? null : transaction.getTransactionId();
        searchBase.receiveContent(services.search(searchBase.receiveAs(), querydef, mimetype, start, pageLen, views, tid));
        return searchHandle;
    }

    @Override
    public void delete(DeleteQueryDefinition querydef) {
        delete(querydef, null);
    }

    @Override
    public void delete(DeleteQueryDefinition querydef, Transaction transaction) {
        String tid = transaction == null ? null : transaction.getTransactionId();
        services.deleteSearch(querydef, tid);
    }

    @Override
    public <T extends ValuesReadHandle> T values(ValuesDefinition valdef, T valueHandle) {
        return values(valdef, valueHandle, null);
    }

    @Override
    public <T extends ValuesReadHandle> T values(ValuesDefinition valdef, T valuesHandle, Transaction transaction) {
    	HandleImplementation valuesBase = HandleAccessor.checkHandle(valuesHandle, "values");

        if (valuesHandle instanceof ValuesHandle) {
            ((ValuesHandle) valuesHandle).setQueryCriteria(valdef);
        }

        Format valuesFormat = valuesBase.getFormat();
        switch(valuesFormat) {
        case UNKNOWN:
        	valuesFormat = Format.XML;
        	break;
        case XML:
        	break;
        default:
            throw new UnsupportedOperationException("Only XML values results are possible.");
        }

        String mimetype = valuesFormat.getDefaultMimetype();

        String tid = transaction == null ? null : transaction.getTransactionId();
        valuesBase.receiveContent(services.values(valuesBase.receiveAs(), valdef, mimetype, tid));
        return valuesHandle;
    }

    @Override
    public <T extends ValuesListReadHandle> T valuesList(ValuesListDefinition valdef, T valueHandle) {
        return valuesList(valdef, valueHandle, null);
    }

    @Override
    public <T extends ValuesListReadHandle> T valuesList(ValuesListDefinition valdef, T valuesHandle, Transaction transaction) {
    	HandleImplementation valuesBase = HandleAccessor.checkHandle(valuesHandle, "valueslist");

        Format valuesFormat = valuesBase.getFormat();
        switch(valuesFormat) {
        case UNKNOWN:
        	valuesFormat = Format.XML;
        	break;
        case XML:
        	break;
        default:
            throw new UnsupportedOperationException("Only XML values list results are possible.");
        }

        String mimetype = valuesFormat.getDefaultMimetype();

        String tid = transaction == null ? null : transaction.getTransactionId();
        valuesBase.receiveContent(services.valuesList(valuesBase.receiveAs(), valdef, mimetype, tid));
        return valuesHandle;
    }

    @Override
    public <T extends QueryOptionsListReadHandle> T optionsList(T optionsHandle) {
        return optionsList(optionsHandle, null);
    }

    @Override
    public <T extends QueryOptionsListReadHandle> T optionsList(T optionsHandle, Transaction transaction) {
    	HandleImplementation optionsBase = HandleAccessor.checkHandle(optionsHandle, "optionslist");

        Format optionsFormat = optionsBase.getFormat();
        switch(optionsFormat) {
        case UNKNOWN:
        	optionsFormat = Format.XML;
        	break;
        case XML:
        	break;
        default:
            throw new UnsupportedOperationException("Only XML options list results are possible.");
        }

        String mimetype = optionsFormat.getDefaultMimetype();

        String tid = transaction == null ? null : transaction.getTransactionId();
        optionsBase.receiveContent(services.optionsList(optionsBase.receiveAs(), mimetype, tid));
        return optionsHandle;
    }

    @Override
    public MatchDocumentSummary findOne(QueryDefinition querydef) {
        SearchHandle results = search(querydef, new SearchHandle());
        MatchDocumentSummary[] summaries = results.getMatchResults();
        if (summaries.length > 0) {
            return summaries[0];
        } else {
            return null;
        }
    }

    @Override
    public MatchDocumentSummary findOne(QueryDefinition querydef, Transaction transaction) {
        SearchHandle results = search(querydef, new SearchHandle(), transaction);
        MatchDocumentSummary[] summaries = results.getMatchResults();
        if (summaries.length > 0) {
            return summaries[0];
        } else {
            return null;
        }
    }

    private class ResponseViewsImpl implements ResponseViews {
        private ArrayList<QueryView> views = new ArrayList<QueryView>();

        @Override
        public int size() {
            return views.size();
        }

        @Override
        public boolean isEmpty() {
            return views.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return views.contains(o);
        }

        @Override
        public Iterator<QueryView> iterator() {
            return views.iterator();
        }

        @Override
        public Object[] toArray() {
            return views.toArray(new QueryView[0]);
        }

        @Override
        public <T> T[] toArray(T[] ts) {
            return (T[]) views.toArray(new QueryView[0]);
        }

        @Override
        public boolean add(QueryView queryView) {
            if (!contains(queryView)) {
                return views.add(queryView);
            } else {
                return false;
            }
        }

        @Override
        public boolean remove(Object o) {
            return views.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> objects) {
            return views.containsAll(objects);
        }

        @Override
        public boolean addAll(Collection<? extends QueryView> queryViews) {
            return views.addAll(queryViews);
        }

        @Override
        public boolean retainAll(Collection<?> objects) {
            return views.retainAll(objects);
        }

        @Override
        public boolean removeAll(Collection<?> objects) {
            return views.removeAll(objects);
        }

        @Override
        public void clear() {
            views.clear();
        }
    }
}
