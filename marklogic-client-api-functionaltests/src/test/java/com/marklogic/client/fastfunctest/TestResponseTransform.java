/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;



public class TestResponseTransform extends AbstractFunctionalTest {

  @Test
  public void testResponseTransform() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testResponseTransform");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/response-transform/", "XML");
    }

    // set the transform
    // create a manager for transform extensions
    TransformExtensionsManager transMgr = client.newServerConfigManager().newTransformExtensionsManager();

    // specify metadata about the transform extension
    ExtensionMetadata metadata = new ExtensionMetadata();
    metadata.setTitle("Search-Response-TO-HTML XSLT Transform");
    metadata.setDescription("This plugin transforms a Search Response document to HTML");
    metadata.setProvider("MarkLogic");
    metadata.setVersion("0.1");

    // get the transform file
    File transformFile = new File("src/test/java/com/marklogic/client/functionaltest/transforms/search2html.xsl");

    FileHandle transformHandle = new FileHandle(transformFile);

    // write the transform
    transMgr.writeXSLTransform("search2html", transformHandle, metadata);

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOption.xml");

    String combinedQuery = convertFileToString(file);

    // create a handle for the search criteria
    StringHandle rawHandle = new StringHandle(combinedQuery);

    QueryManager queryMgr = client.newQueryManager();

    // create a search definition based on the handle
    RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);
    querydef.setResponseTransform(new ServerTransform("search2html"));

    // create result handle
    StringHandle resultsHandle = new StringHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    String resultDoc = resultsHandle.get();

    System.out.println(resultDoc);

    assertTrue( resultDoc.contains("<title>Custom Search Results</title>"));
    assertTrue( resultDoc.contains("MyURI"));
    assertTrue( resultDoc.contains("<td align=\"left\">/response-transform/constraint5.xml</td>"));

    transMgr.deleteTransform("search2html");

    // release client
    client.release();
  }

  @Test
  public void testResponseTransformInvalid() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testResponseTransformInvalid");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/response-transform/", "XML");
    }

    // set the transform
    // create a manager for transform extensions
    TransformExtensionsManager transMgr = client.newServerConfigManager().newTransformExtensionsManager();

    // specify metadata about the transform extension
    ExtensionMetadata metadata = new ExtensionMetadata();
    metadata.setTitle("Search-Response-TO-HTML XSLT Transform");
    metadata.setDescription("This plugin transforms a Search Response document to HTML");
    metadata.setProvider("MarkLogic");
    metadata.setVersion("0.1");

    // get the transform file
    File transformFile = new File("src/test/java/com/marklogic/client/functionaltest/transforms/search2html.xsl");

    FileHandle transformHandle = new FileHandle(transformFile);

    // write the transform
    transMgr.writeXSLTransform("search2html", transformHandle, metadata);

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOption.xml");

    String combinedQuery = convertFileToString(file);

    // create a handle for the search criteria
    StringHandle rawHandle = new StringHandle(combinedQuery);

    QueryManager queryMgr = client.newQueryManager();

    // create a search definition based on the handle
    RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);
    querydef.setResponseTransform(new ServerTransform("foo"));

    // create result handle
    StringHandle resultsHandle = new StringHandle();

    String exception = "";

    try
    {
      queryMgr.search(querydef, resultsHandle);
    } catch (Exception e)
    {
      exception = e.toString();
      System.out.println(exception);
    }

    String expectedException = "Local message: search failed: Bad Request. Server Message: RESTAPI-INVALIDREQ: (err:FOER0000)";
    assertTrue( exception.contains(expectedException));
    // bug 22356
    assertTrue( resultsHandle.get() == null);

    transMgr.deleteTransform("search2html");

    // release client
    client.release();
  }
}
