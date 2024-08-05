/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("deprecation")
public class QueryOptionsManagerTest {
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(QueryOptionsManagerTest.class);

  @BeforeAll
  public static void beforeClass() {
    Common.connectRestAdmin();
  }
  @AfterAll
  public static void afterClass() {
  }

  @Test
  public void testQueryOptionsManager()
    throws JAXBException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
  {
    QueryOptionsManager mgr =
      Common.restAdminClient.newServerConfigManager().newQueryOptionsManager();
    assertNotNull( mgr);

    mgr.writeOptions("testempty", new StringHandle("{\"options\":{}}").withFormat(Format.JSON));

    String optionsResult = mgr.readOptions("testempty", new StringHandle()).get();
    logger.debug("Empty options from server {}", optionsResult);
    assertTrue(optionsResult.contains("options"));
    assertTrue(optionsResult.contains("\"http://marklogic.com/appservices/search\"/>"));

    mgr.deleteOptions("testempty");
  };

  @Test
  public void testXMLDocsAsSearchOptions()
    throws ParserConfigurationException, SAXException, IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
  {
    String optionsName = "invalid";

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(false);
    DocumentBuilder documentBldr = factory.newDocumentBuilder();

    Document domDocument = documentBldr.newDocument();
    Element root = domDocument.createElementNS("http://marklogic.com/appservices/search","options");
    Element rf = domDocument.createElementNS("http://marklogic.com/appservices/search","return-facets");
    rf.setTextContent("true");
    root.appendChild(rf);
    root.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:lang", "en");  // MarkLogic adds this if I don't
    domDocument.appendChild(root);

    QueryOptionsManager queryOptionsMgr =
      Common.restAdminClient.newServerConfigManager().newQueryOptionsManager();

    queryOptionsMgr.writeOptions(optionsName, new DOMHandle(domDocument));

    String domString =
            ((DOMImplementationLS) documentBldr.getDOMImplementation()).createLSSerializer().writeToString(domDocument);

    String optionsString = queryOptionsMgr.readOptions(optionsName, new StringHandle()).get();
    assertNotNull(optionsString);
    logger.debug("Two XML Strings {} and {}", domString, optionsString);

    Document readDoc = queryOptionsMgr.readOptions(optionsName, new DOMHandle()).get();
    assertNotNull(readDoc);

  }

  @Test
  public void testJSONOptions()
    throws JAXBException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
  {
    QueryOptionsManager mgr =
      Common.restAdminClient.newServerConfigManager().newQueryOptionsManager();
    assertNotNull( mgr);

    FileHandle jsonHandle = new FileHandle(new File("src/test/resources/json-config.json"));
    jsonHandle.setFormat(Format.JSON);
    mgr.writeOptions("jsonoptions", jsonHandle);

    JsonNode options = mgr.readOptions("jsonoptions", new JacksonHandle()).get();

    assertEquals( options.findPath("constraint").get(0).get("name").textValue(), "decade");


    StringHandle jsonStringHandle = new StringHandle();
    jsonStringHandle.setFormat(Format.JSON);
    mgr.readOptions("jsonoptions", jsonStringHandle);
    assertTrue( jsonStringHandle.get().startsWith("{\"options\":"));

    mgr.deleteOptions("jsonoptions");
  };
}
