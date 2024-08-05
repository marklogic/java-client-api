/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.util.EditableNamespaceContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DOMSearchResultTest {
  @BeforeAll
  public static void beforeClass() {
    Common.connect();
  }

  @AfterAll
  public static void afterClass() {
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
    assertNotNull( doc);

    // configure namespace bindings for the XPath processor
    EditableNamespaceContext namespaces = new EditableNamespaceContext();
    namespaces.setNamespaceURI("search", "http://marklogic.com/appservices/search");
    responseHandle.getXPathProcessor().setNamespaceContext(namespaces);

    // string expression against the document
    String total = responseHandle.evaluateXPath("string(/search:response/@total)", String.class);
    assertNotNull( total);

    // compiled expression against the document
    XPathExpression resultsExpression = responseHandle.compileXPath("/search:response/search:result");
    NodeList resultList = responseHandle.evaluateXPath(resultsExpression, NodeList.class);
    assertNotNull( resultList);
    assertEquals( resultList.getLength(), Integer.parseInt(total));

    // string expression against an element
    Element firstResult = responseHandle.evaluateXPath("/search:response/search:result[1]", Element.class);
    assertNotNull( firstResult);

    String firstResultUri = responseHandle.evaluateXPath("string(@uri)", firstResult, String.class);
    String firstItemUri   = ((Element) resultList.item(0)).getAttribute("uri");
    assertEquals( firstResultUri, firstItemUri);
  }
}
