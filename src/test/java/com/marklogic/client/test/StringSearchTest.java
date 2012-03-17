package com.marklogic.client.test;

import com.marklogic.client.QueryManager;
import com.marklogic.client.config.search.MatchDocumentSummary;
import com.marklogic.client.config.search.MatchLocation;
import com.marklogic.client.config.search.QueryDefinition;
import com.marklogic.client.config.search.SearchResults;
import com.marklogic.client.config.search.StringQueryDefinition;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 3/14/12
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringSearchTest {
    @BeforeClass
    public static void beforeClass() {
        Common.connect();
    }
    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void testReadWrite() throws IOException {
        QueryManager queryMgr = Common.client.newQueryManager();
        StringQueryDefinition qdef = queryMgr.newStringCriteria(null);
        qdef.setCriteria("and");
        /*
        Document results = queryMgr.searchAsXml(new DOMHandle(), qdef).get();
        */
        SearchResults results = queryMgr.search(qdef);
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
