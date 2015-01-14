/*
 * Copyright 2012-2015 MarkLogic Corporation
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.util.EditableNamespaceContext;
import com.marklogic.client.io.DOMHandle;

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
    public void testStringSearch()
    throws IOException, ParserConfigurationException, XPathExpressionException {
        // This test really just exists to show how to get search results in other formats.
        QueryManager queryMgr = Common.client.newQueryManager();
        StringQueryDefinition qdef = queryMgr.newStringDefinition(null);
        qdef.setCriteria("leaf3");

        DOMHandle responseHandle = queryMgr.search(qdef, new DOMHandle());
        Document doc = responseHandle.get();
        assertNotNull("null response document", doc);

        // configure namespace bindings for the XPath processor
        EditableNamespaceContext namespaces = new EditableNamespaceContext();
        namespaces.setNamespaceURI("search", "http://marklogic.com/appservices/search");
        responseHandle.getXPathProcessor().setNamespaceContext(namespaces);

        // string expression against the document
        String total = responseHandle.evaluateXPath("string(/search:response/@total)", String.class);
        assertNotNull("null total", total);

        // compiled expression against the document
        XPathExpression resultsExpression = responseHandle.compileXPath("/search:response/search:result");
        NodeList resultList = responseHandle.evaluateXPath(resultsExpression, NodeList.class);
        assertNotNull("null result list", resultList);
        assertEquals("result count mismatch", resultList.getLength(), Integer.parseInt(total));

        // string expression against an element
        Element firstResult = responseHandle.evaluateXPath("/search:response/search:result[1]", Element.class);
        assertNotNull("null first result", firstResult);

        String firstResultUri = responseHandle.evaluateXPath("string(@uri)", firstResult, String.class);
        String firstItemUri   = ((Element) resultList.item(0)).getAttribute("uri");
        assertEquals("first uri mismatch", firstResultUri, firstItemUri);
    }
}
