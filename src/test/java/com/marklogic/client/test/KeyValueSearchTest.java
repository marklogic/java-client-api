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
package com.marklogic.client.test;

import com.marklogic.client.QueryManager;
import com.marklogic.client.config.KeyValueQueryDefinition;
import com.marklogic.client.config.MatchDocumentSummary;
import com.marklogic.client.config.MatchLocation;
import com.marklogic.client.io.SearchHandle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.namespace.QName;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class KeyValueSearchTest {
    @BeforeClass
    public static void beforeClass() {
        Common.connect();
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void testKVSearch() throws IOException {
        QueryManager queryMgr = Common.client.newQueryManager();
        KeyValueQueryDefinition qdef = queryMgr.newKeyValueDefinition(null);

        qdef.put(queryMgr.newElementLocator(new QName("leaf")), "leaf3");
        SearchHandle results = queryMgr.search(qdef, new SearchHandle());
        assertNotNull(results);
        assertFalse(results.getMetrics().getTotalTime() == -1);

        MatchDocumentSummary[] summaries = results.getMatchResults();
        for (MatchDocumentSummary summary : summaries) {
            MatchLocation[] locations = summary.getMatchLocations();
            for (MatchLocation location : locations) {
                assertNotNull(location.getAllSnippetText());
            }
        }
        
        assertNotNull(summaries);
    }
}
