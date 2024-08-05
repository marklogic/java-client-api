/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.*;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;




public class TestSearchOnJSON extends AbstractFunctionalTest {

  @AfterEach
  public void testCleanUp() throws Exception
  {
    deleteDocuments(connectAsAdmin());
  }

  @Test
  public void testRoundtrippingQueryOption() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testRoundtrippingQueryOption");

    String queryOptionName = "valueConstraintWildCardOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create a manager for writing query options
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create handle
    ReaderHandle handle = new ReaderHandle();

    // write the files
    BufferedReader docStream = new BufferedReader(new FileReader("src/test/java/com/marklogic/client/functionaltest/queryoptions/" + queryOptionName));
    handle.set(docStream);

    // write the query options to the database
    optionsMgr.writeOptions(queryOptionName, handle);

    System.out.println("Write " + queryOptionName + " to database");

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.JSON);
    optionsMgr.readOptions(queryOptionName, readHandle);

    String output = readHandle.get();

    System.out.println(output);

    String expectedOutput = "{\"options\":{\"return-metrics\":false, \"return-qtext\":false, \"debug\":true, \"transform-results\":{\"apply\":\"raw\"}, \"constraint\":[{\"name\":\"id\", \"value\":{\"element\":{\"ns\":\"\", \"name\":\"id\"}}}]}}";

    assertEquals( expectedOutput, output.trim());

    // create handle to write back option in json
    String queryOptionNameJson = queryOptionName.replaceAll(".xml", ".json");
    StringHandle writeHandle = new StringHandle();
    writeHandle.set(output);
    writeHandle.setFormat(Format.JSON);
    optionsMgr.writeOptions(queryOptionNameJson, writeHandle);
    System.out.println("Write " + queryOptionNameJson + " to database");

    // release client
    client.release();
  }

  @Test
  public void testWithAllConstraintSearchResultinJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      XpathException
  {
    System.out.println("Running testWithAllConstraintSearchResultinJSON");

    String filename1 = "constraint1.xml";
    String filename2 = "constraint2.xml";
    String filename3 = "constraint3.xml";
    String filename4 = "constraint4.xml";
    String filename5 = "constraint5.xml";
    String queryOptionName = "appservicesConstraintCombinationOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();

    // set the metadata
    metadataHandle1.getCollections().addAll("http://test.com/set1");
    metadataHandle1.getCollections().addAll("http://test.com/set5");
    metadataHandle2.getCollections().addAll("http://test.com/set1");
    metadataHandle3.getCollections().addAll("http://test.com/set3");
    metadataHandle4.getCollections().addAll("http://test.com/set3/set3-1");
    metadataHandle5.getCollections().addAll("http://test.com/set1");
    metadataHandle5.getCollections().addAll("http://test.com/set5");

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename1, "/all-constraint-json/", metadataHandle1, "XML");
    writeDocumentUsingInputStreamHandle(client, filename2, "/all-constraint-json/", metadataHandle2, "XML");
    writeDocumentUsingInputStreamHandle(client, filename3, "/all-constraint-json/", metadataHandle3, "XML");
    writeDocumentUsingInputStreamHandle(client, filename4, "/all-constraint-json/", metadataHandle4, "XML");
    writeDocumentUsingInputStreamHandle(client, filename5, "/all-constraint-json/", metadataHandle5, "XML");

    // create a manager for writing query options
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create handle
    ReaderHandle handle = new ReaderHandle();

    // write the files
    BufferedReader docStream = new BufferedReader(new FileReader("src/test/java/com/marklogic/client/functionaltest/queryoptions/" + queryOptionName));
    handle.set(docStream);

    // write the query options to the database
    optionsMgr.writeOptions(queryOptionName, handle);

    System.out.println("Write " + queryOptionName + " to database");

    // read query option
    InputStreamHandle readHandle = new InputStreamHandle();
    readHandle.setFormat(Format.JSON);
    optionsMgr.readOptions(queryOptionName, readHandle);

    InputStream output = readHandle.get();

    // create handle to write back option in json
    String queryOptionNameJson = queryOptionName.replaceAll(".xml", ".json");
    InputStreamHandle writeHandle = new InputStreamHandle();
    writeHandle.set(output);
    writeHandle.setFormat(Format.JSON);
    optionsMgr.writeOptions(queryOptionNameJson, writeHandle);
    System.out.println("Write " + queryOptionNameJson + " to database");

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionNameJson);
    querydef.setCriteria("(coll:set1 AND coll:set5) AND -intitle:memex AND (pop:high OR pop:medium) AND price:low AND id:**11 AND date:2005-01-01 AND (para:Bush AND -para:memex)");

    SearchHandle results = queryMgr.search(querydef, new SearchHandle());
    assertEquals(1, results.getTotalResults());
    assertEquals("/all-constraint-json/constraint1.xml", results.getMatchResults()[0].getUri());

    // release client
    client.release();
  }
}
