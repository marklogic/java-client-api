package com.marklogic.client.test;

import com.marklogic.client.QueryManager;
import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.config.MatchDocumentSummary;
import com.marklogic.client.config.MatchLocation;
import com.marklogic.client.config.StringQueryDefinition;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
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
public class DOMSearchResultTest {
    @BeforeClass
    public static void beforeClass() {
        Common.connect();
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void testStringSearch() throws IOException, ParserConfigurationException {
        // This test really just exists to show how to get search results in other formats.
        QueryManager queryMgr = Common.client.newQueryManager();
        StringQueryDefinition qdef = queryMgr.newStringDefinition(null);
        qdef.setCriteria("leaf3");

        DOMHandle results = queryMgr.search(qdef, new DOMHandle());
        Document doc = results.get();
        assertNotNull(doc);
    }
}
