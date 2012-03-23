package com.marklogic.client.test;

import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.GenericDocumentManager;
import com.marklogic.client.QueryManager;
import com.marklogic.client.TextDocumentManager;
import com.marklogic.client.config.search.MatchDocumentSummary;
import com.marklogic.client.config.search.MatchLocation;
import com.marklogic.client.config.search.StringQueryDefinition;
import com.marklogic.client.config.search.StructuredQueryBuilder;
import com.marklogic.client.config.search.StructuredQueryDefinition;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/14/12
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredSearchTest {
    @BeforeClass
    public static void beforeClass() {
        Common.connect();
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void testStructuredSearch() throws IOException {
        QueryManager queryMgr = Common.client.newQueryManager();
        StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(null);
        StructuredQueryDefinition t = qb.term("leaf3");

        SearchHandle results = queryMgr.search(new SearchHandle(), t);
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

    @Test
    public void testStructuredSearch1() throws IOException {
        QueryManager queryMgr = Common.client.newQueryManager();
        StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(null);
        StructuredQueryDefinition t = qb.term("leaf3");

        MatchDocumentSummary summary = queryMgr.findOne(t);
        if (summary != null) {
            GenericDocumentManager docMgr = Common.client.newDocumentManager();
            assertTrue("Document exists", docMgr.exists(summary));
        }
    }
}
