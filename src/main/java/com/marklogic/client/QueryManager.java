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
package com.marklogic.client;

import com.marklogic.client.config.KeyValueQueryDefinition;
import com.marklogic.client.config.MatchDocumentSummary;
import com.marklogic.client.config.QueryDefinition;
import com.marklogic.client.config.StringQueryDefinition;
import com.marklogic.client.config.StructuredQueryBuilder;
import com.marklogic.client.config.StructuredQueryDefinition;
import com.marklogic.client.config.ValuesDefinition;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.ValuesReadHandle;

import javax.xml.namespace.QName;
import java.util.Set;

public interface QueryManager {
    // constants that can be used as values for pagination
    static final public long DEFAULT_PAGE_LENGTH = 10;
    static final public long START = 1;

    public interface ResponseViews extends Set<QueryView> {
    }

    public enum QueryView {
        SEARCH, FACETS, METRICS;
    }

    public long getPageLength();
    public void setPageLength(long length);

    public ResponseViews getViews();
    public void setViews(ResponseViews views);
    public void setViews(QueryView... views);

    public StringQueryDefinition newStringDefinition(String optionsName);
    public KeyValueQueryDefinition newKeyValueDefinition(String optionsName);
    public StructuredQueryBuilder newStructuredQueryBuilder(String optionsName);

    public ValuesDefinition newValuesDefinition(String optionsName);

    public ElementLocator newElementLocator(QName element);
    public ElementLocator newElementLocator(QName element, QName attribute);
    public KeyLocator newKeyLocator(String key);

    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle);
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle, long start);
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle, Transaction transaction);
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle, long start, Transaction transaction);

    public <T extends ValuesReadHandle> T values(ValuesDefinition valdef, T valueHandle);
    public <T extends ValuesReadHandle> T values(ValuesDefinition valdef, T valueHandle, Transaction transaction);

    public MatchDocumentSummary findOne(QueryDefinition querydef);
    public MatchDocumentSummary findOne(QueryDefinition querydef, Transaction transaction);

    public void startLogging(RequestLogger logger);
    public void stopLogging();
}
