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
import java.util.List;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.marklogic.client.Transaction;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.TuplesHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.io.marker.QueryOptionsListReadHandle;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.TuplesReadHandle;
import com.marklogic.client.io.marker.ValuesListReadHandle;
import com.marklogic.client.io.marker.ValuesReadHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.ElementLocator;
import com.marklogic.client.query.KeyLocator;
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawQueryDefinition;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.SuggestDefinition;
import com.marklogic.client.query.ValuesDefinition;
import com.marklogic.client.query.ValuesListDefinition;

public class QueryManagerImpl extends AbstractLoggingManager implements QueryManager {
    private RESTServices services = null;
    private long pageLen = -1;
    private QueryView view = QueryView.DEFAULT;

    public QueryManagerImpl(RESTServices services) {
        this.services = services;
    }

    @Override
    public long getPageLength() {
        return pageLen;
    }
    @Override
    public void setPageLength(long length) {
        pageLen = length;
    }

    @Override
    public QueryView getView() {
        return view;
    }
    @Override
    public void setView(QueryView view) {
    	this.view = (view == null) ? QueryView.DEFAULT : view;
    }

    @Override
    public StringQueryDefinition newStringDefinition() {
        return new StringQueryDefinitionImpl(null);
    }
    @Override
    public StringQueryDefinition newStringDefinition(String optionsName) {
        return new StringQueryDefinitionImpl(optionsName);
    }

    @Override
    public KeyValueQueryDefinition newKeyValueDefinition() {
        return new KeyValueQueryDefinitionImpl(null);
    }

    @Override
    public KeyValueQueryDefinition newKeyValueDefinition(String optionsName) {
        return new KeyValueQueryDefinitionImpl(optionsName);
    }

    @Override
    public StructuredQueryBuilder newStructuredQueryBuilder() {
        return new StructuredQueryBuilder(null);
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
    public ValuesDefinition newValuesDefinition(String name) {
        return new ValuesDefinitionImpl(name, null);
    }

    @Override
    public ValuesDefinition newValuesDefinition(String name, String optionsName) {
        return new ValuesDefinitionImpl(name, optionsName);
    }

    @Override
    public ValuesListDefinition newValuesListDefinition() {
        return new ValuesListDefinitionImpl(null);
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
    @SuppressWarnings({ "rawtypes", "unchecked" })
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
        searchBase.receiveContent(services.search(searchBase.receiveAs(), querydef, mimetype, start, pageLen, view, tid));
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
    @SuppressWarnings({ "rawtypes", "unchecked" })
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
        case JSON:
        case XML:
        	break;
        default:
            throw new UnsupportedOperationException("Only XML and JSON values results are possible.");
        }

        String mimetype = valuesFormat.getDefaultMimetype();

        String tid = transaction == null ? null : transaction.getTransactionId();
        valuesBase.receiveContent(services.values(valuesBase.receiveAs(), valdef, mimetype, tid));
        return valuesHandle;
    }

    @Override
    public <T extends TuplesReadHandle> T tuples(ValuesDefinition valdef, T tupleHandle) {
        return tuples(valdef, tupleHandle, null);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T extends TuplesReadHandle> T tuples(ValuesDefinition valdef, T tuplesHandle, Transaction transaction) {
        HandleImplementation valuesBase = HandleAccessor.checkHandle(tuplesHandle, "values");

        if (tuplesHandle instanceof TuplesHandle) {
            ((TuplesHandle) tuplesHandle).setQueryCriteria(valdef);
        }

        Format valuesFormat = valuesBase.getFormat();
        switch(valuesFormat) {
            case UNKNOWN:
                valuesFormat = Format.XML;
                break;
            case JSON:
            case XML:
                break;
            default:
                throw new UnsupportedOperationException("Only XML and JSON values results are possible.");
        }

        String mimetype = valuesFormat.getDefaultMimetype();

        String tid = transaction == null ? null : transaction.getTransactionId();
        valuesBase.receiveContent(services.values(valuesBase.receiveAs(), valdef, mimetype, tid));
        return tuplesHandle;
    }

    @Override
    public <T extends ValuesListReadHandle> T valuesList(ValuesListDefinition valdef, T valueHandle) {
        return valuesList(valdef, valueHandle, null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public <T extends ValuesListReadHandle> T valuesList(ValuesListDefinition valdef, T valuesHandle, Transaction transaction) {
    	HandleImplementation valuesBase = HandleAccessor.checkHandle(valuesHandle, "valueslist");

        Format valuesFormat = valuesBase.getFormat();
        switch(valuesFormat) {
        case UNKNOWN:
        	valuesFormat = Format.XML;
        	break;
        case JSON:
        case XML:
        	break;
        default:
            throw new UnsupportedOperationException("Only XML and JSON values list results are possible.");
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public <T extends QueryOptionsListReadHandle> T optionsList(T optionsHandle, Transaction transaction) {
    	HandleImplementation optionsBase = HandleAccessor.checkHandle(optionsHandle, "optionslist");

        Format optionsFormat = optionsBase.getFormat();
        switch(optionsFormat) {
        case UNKNOWN:
        	optionsFormat = Format.XML;
        	break;
        case JSON:
        case XML:
        	break;
        default:
            throw new UnsupportedOperationException("Only XML and JSON options list results are possible.");
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

	@Override
	public String[] suggest(SuggestDefinition suggestionDef) {
		DOMHandle handle = new DOMHandle();
		HandleImplementation suggestBase = HandleAccessor.checkHandle(handle, "suggest");

        Format optionsFormat = suggestBase.getFormat();
      
		suggestBase.receiveContent(services.suggest(suggestBase.receiveAs(), suggestionDef));
        
		Document doc = handle.get();
        NodeList nodeList = doc.getDocumentElement().getChildNodes();
        List<String> suggestions = new ArrayList<String>();
        for (int i=0; i <nodeList.getLength(); i++) {
        	suggestions.add(nodeList.item(i).getTextContent());
        }
        return suggestions.toArray(new String[suggestions.size()]);
	}

	@Override
	public SuggestDefinition newSuggestionDefinition() {
		SuggestDefinition def = new SuggestDefinitionImpl();
		return def;
	}
			

	@Override
	public SuggestDefinition newSuggestionDefinition(String optionsName) {
		SuggestDefinition def = new SuggestDefinitionImpl();
		def.setStringCriteria("");
		def.setOptionsName(optionsName);
		return def;
	}

	@Override
	public SuggestDefinition newSuggestionDefinition(String suggestionString,
			String optionsName) {
		SuggestDefinition def = new SuggestDefinitionImpl();
		def.setStringCriteria(suggestionString);
		def.setOptionsName(optionsName);
		return def;
	}

	@Override
	public RawQueryDefinition newRawDefinition(StructureWriteHandle handle) {
		RawQueryDefinitionImpl impl = new RawQueryDefinitionImpl();
		impl.setHandle(handle);
		return impl;
	}
	
	@Override
	public RawQueryDefinition newRawDefinition(StructureWriteHandle handle, String optionsName) {
		RawQueryDefinitionImpl impl = new RawQueryDefinitionImpl();
		impl.setOptionsName(optionsName);
		impl.setHandle(handle);
		return impl;
	}
}
