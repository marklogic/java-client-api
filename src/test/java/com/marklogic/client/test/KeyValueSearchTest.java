package com.marklogic.client.test;

import com.marklogic.client.QueryManager;
import com.marklogic.client.config.search.KeyValueQueryDefinition;
import com.marklogic.client.config.search.MatchDocumentSummary;
import com.marklogic.client.config.search.MatchLocation;
import com.marklogic.client.config.search.SearchResults;
import com.marklogic.client.config.search.StringQueryDefinition;
import com.marklogic.client.io.SearchHandle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.namespace.QName;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/14/12
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
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
        KeyValueQueryDefinition qdef = queryMgr.newKeyValueCriteria(null);

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
