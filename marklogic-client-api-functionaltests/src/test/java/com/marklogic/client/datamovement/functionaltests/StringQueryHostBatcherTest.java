/*
 * Copyright 2014-2019 MarkLogic Corporation
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
package com.marklogic.client.datamovement.functionaltests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.HostAvailabilityListener;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.ProgressListener;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.UrisToWriterListener;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawCtsQueryDefinition;
import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import com.marklogic.client.query.StructuredQueryDefinition;

/**
 * @author ageorge Purpose : Test String Queries - On multiple documents using
 *         Java Client DocumentManager Write method and WriteBatcher. - On
 *         meta-data. - On non-existent document. Verify error message. - With
 *         invalid string query. Verify error message.
 *
 */
public class StringQueryHostBatcherTest extends BasicJavaClientREST {
  private static String dbName = "StringQueryHostBatcherDB";
  private static String[] fNames = { "StringQueryHostBatcherDB-1", "StringQueryHostBatcherDB-2", "StringQueryHostBatcherDB-3" };
  private static DataMovementManager dmManager = null;
  private static String restServerName = null;
  private static int restServerPort = 0;
  private static DatabaseClient client = null;
  private static String dataConfigDirPath = null;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    loadGradleProperties();
    restServerPort = getRestAppServerPort();

    restServerName = getRestAppServerName();
    // Points to top level of all QA data folder
    dataConfigDirPath = getDataConfigDirPath();

    setupJavaRESTServer(dbName, fNames[0], restServerName, restServerPort);
    setupAppServicesConstraint(dbName);

    createUserRolesWithPrevilages("test-eval", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createRESTUser("eval-user", "x", "test-eval", "rest-admin", "rest-writer", "rest-reader", "rest-extension-user", "manage-user");

    // For use with Java/REST Client API
    client = getDatabaseClient("eval-user", "x", getConnType());
    dmManager = client.newDataMovementManager();
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    System.out.println("In tearDownAfterClass");
    // Release clients
    client.release();
    associateRESTServerWithDB(restServerName, "Documents");
    deleteRESTUser("eval-user");
    deleteUserRole("test-eval");
    detachForest(dbName, fNames[0]);

    deleteDB(dbName);
    deleteForest(fNames[0]);
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    System.out.println("In tearDown");
    clearDB(restServerPort);
  }

  /*
   * To test String query with Document Manager (Java Client API write method)
   * and WriteBatcher.
   * 
   * @throws IOException
   * 
   * @throws ParserConfigurationException
   * 
   * @throws SAXException
   * 
   * @throws XpathException
   */
  @Test
  public void testAndWordQuery() throws Exception
  {
    System.out.println("Running testAndWordQuery");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "absRangeConstraintWithVariousGrammarAndWordQueryOpt.xml";
    addRangeElementAttributeIndex(dbName, "decimal", "http://cloudbank.com", "price", "", "amt", "http://marklogic.com/collation/");
    try {
      // write docs using Java Client API
      for (String filename : filenames) {
        writeDocumentUsingInputStreamHandle(client, filename, "/abs-range-constraint/", "XML");
      }

      setQueryOption(client, queryOptionName);

      QueryManager queryMgr = client.newQueryManager();

      // create query def
      StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
      querydef.setCriteria("(pop:high OR pop:medium) AND price:medium AND intitle:served");

      // create handle to search using Java Client API.
      JacksonHandle jh = new JacksonHandle();
      JsonNode jsonResults = queryMgr.search(querydef, jh).get();

      // Verify the results.
      JsonNode searchResult = jsonResults.get("results").get(0);
      assertEquals(1, searchResult.get("index").asInt());
      assertEquals("/abs-range-constraint/constraint4.xml", searchResult.get("uri").asText());
      String contents = searchResult.get("content").asText();
      assertTrue("Expected String not available", contents.contains("Vannevar served"));
      assertTrue("Expected amt not available", contents.contains("12.34"));

      // Use WriteBatcher to write the same files.
      WriteBatcher batcher = dmManager.newWriteBatcher();
      // Move to individual data sub folders.
      String dataFileDir = dataConfigDirPath + "/data/";

      batcher.withBatchSize(2);
      InputStreamHandle contentHandle1 = new InputStreamHandle();
      contentHandle1.set(new FileInputStream(dataFileDir + filenames[0]));
      InputStreamHandle contentHandle2 = new InputStreamHandle();
      contentHandle2.set(new FileInputStream(dataFileDir + filenames[1]));
      InputStreamHandle contentHandle3 = new InputStreamHandle();
      contentHandle3.set(new FileInputStream(dataFileDir + filenames[2]));
      InputStreamHandle contentHandle4 = new InputStreamHandle();
      contentHandle4.set(new FileInputStream(dataFileDir + filenames[3]));
      InputStreamHandle contentHandle5 = new InputStreamHandle();
      contentHandle5.set(new FileInputStream(dataFileDir + filenames[4]));

      batcher.add("/abs-range-constraint/batcher-contraints1.xml", contentHandle1);
      batcher.add("/abs-range-constraint/batcher-contraints2.xml", contentHandle2);
      batcher.add("/abs-range-constraint/batcher-contraints3.xml", contentHandle3);
      batcher.add("/abs-range-constraint/batcher-contraints4.xml", contentHandle4);
      batcher.add("/abs-range-constraint/batcher-contraints5.xml", contentHandle5);

      // Verify if the batch flushes when batch size is reached.
      // Flush
      batcher.flushAndWait();
      // Hold for asserting the callbacks batch contents, since callback are on
      // different threads than the main JUnit thread.
      // JUnit can not assert on different threads; other than the main one.
      StringBuilder batchResults = new StringBuilder();
      StringBuilder forestResults = new StringBuilder();
      StringBuilder batchFailResults = new StringBuilder();

      // Run a QueryBatcher on the new URIs.
      QueryBatcher queryBatcher1 = dmManager.newQueryBatcher(querydef);

      queryBatcher1.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchResults.append(str)
              .append('|');
        }

        batchResults.append(batch.getForest().getForestName())
            .append('|')
            .append(batch.getJobBatchNumber())
            .append('|');
        forestResults.append(batch.getForest().getForestName());

      });
      queryBatcher1.onQueryFailure(throwable -> {
        System.out.println("Exceptions thrown from callback onQueryFailure");
        throwable.printStackTrace();
        batchFailResults.append("Test has Exceptions");
      });

      JobTicket jobTicket = dmManager.startJob(queryBatcher1);
      boolean bJobFinished = queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);

      if (queryBatcher1.isStopped()) {
        // Verify the batch results now.
        String[] res = batchResults.toString().split("\\|");

        assertTrue("URI returned not correct", res[0].contains("/abs-range-constraint/batcher-contraints4.xml"));
        // Verify Forest Name.
        assertTrue("Forest name not correct", forestResults.toString().contains(fNames[0]));
      }
    } catch (Exception e) {
      System.out.print(e.getMessage());
    } finally {

    }
  }

  /*
   * To test query by example with WriteBatcher and QueryBatcher.
   * 
   * @throws IOException
   * 
   * @throws InterruptedException
   */

  /*
   * public void testQueryByExample() throws IOException, InterruptedException {
   * System.out.println("Running testQueryByExample");
   * 
   * String[] filenames = {"constraint1.json", "constraint2.json",
   * "constraint3.json", "constraint4.json", "constraint5.json"}; WriteBatcher
   * batcher = dmManager.newWriteBatcher();
   * 
   * batcher.withBatchSize(2);
   * 
   * InputStreamHandle contentHandle1 = new InputStreamHandle();
   * contentHandle1.set(new FileInputStream(dataFileDir + filenames[0]));
   * InputStreamHandle contentHandle2 = new InputStreamHandle();
   * contentHandle2.set(new FileInputStream(dataFileDir + filenames[1]));
   * InputStreamHandle contentHandle3 = new InputStreamHandle();
   * contentHandle3.set(new FileInputStream(dataFileDir + filenames[2]));
   * InputStreamHandle contentHandle4 = new InputStreamHandle();
   * contentHandle4.set(new FileInputStream(dataFileDir + filenames[3]));
   * InputStreamHandle contentHandle5 = new InputStreamHandle();
   * contentHandle5.set(new FileInputStream(dataFileDir + filenames[4]));
   * 
   * StringBuffer writebatchResults = new StringBuffer();
   * batcher.add("/batcher-contraints1.json", contentHandle1);
   * batcher.add("/batcher-contraints2.json", contentHandle2);
   * batcher.add("/batcher-contraints3.json", contentHandle3);
   * batcher.add("/batcher-contraints4.json", contentHandle4);
   * batcher.add("/batcher-contraints5.json", contentHandle5);
   * 
   * // Flush batcher.flushAndWait();
   * 
   * StringBuilder querybatchResults = new StringBuilder(); StringBuilder
   * querybatchFailResults = new StringBuilder();
   * 
   * // get the query File file = new File(dataConfigDirPath + "qbe1.json");
   * FileHandle fileHandle = new FileHandle(file);
   * 
   * QueryManager queryMgr = client.newQueryManager();
   * RawQueryByExampleDefinition qbyexDef =
   * queryMgr.newRawQueryByExampleDefinition
   * (fileHandle.withFormat(Format.JSON));
   * 
   * // Run a QueryBatcher. QueryBatcher queryBatcher1 =
   * dmManager.newQueryBatcher(qbyexDef); queryBatcher1.onUrisReady(batch-> {
   * 
   * for (String str : batch.getItems()) { querybatchResults.append(str)
   * .append('|'); }
   * 
   * querybatchResults.append(batch.getJobResultsSoFar()) .append('|')
   * .append(batch.getForest().getForestName()) .append('|')
   * .append(batch.getJobBatchNumber()) .append('|');
   * 
   * }); queryBatcher1.onQueryFailure(throwable-> {
   * System.out.println("Exceptions thrown from callback onQueryFailure");
   * throwable.printStackTrace();
   * querybatchFailResults.append("Test has Exceptions"); } ); JobTicket
   * jobTicket = dmManager.startJob(queryBatcher1); boolean bJobFinished =
   * queryBatcher1.awaitTermination(30, TimeUnit.SECONDS);
   * 
   * if (queryBatcher1.isStopped()) {
   * 
   * if (!querybatchFailResults.toString().isEmpty() &&
   * querybatchFailResults.toString().contains("Exceptions")) {
   * fail("Test failed due to exceptions"); }
   * 
   * // Verify the batch results now. String[] res =
   * querybatchResults.toString().split("\\|");
   * 
   * assertTrue("URI returned not correct",
   * res[0].contains("/batcher-contraints1.json"));
   * assertEquals("Bytes Moved","0", res[1]); assertEquals("Batch Number","0",
   * res[3]); } }
   */

  /*
   * To test that RawStructuredQueryDefinition can be mixed in with a
   * StructuredQueryBuilder
   * 
   * @throws Exception
   * 
   * TODO modify this test for Git 591, once 591 is fixed/addressed.
   */
  @Ignore
  public void testRawStructuredQDWithQueryBuilder() throws Exception
  {
    String testMultipleDB = "RawStrutdQDWithQBuilderDB";
    String[] testMultipleForest = { "RawStrutdQDWithQBuilderDB-1", "RawStrutdQDWithQBuilderDB-2", "RawStrutdQDWithQBuilderDB-3" };
    DatabaseClient clientTmp = null;
    DataMovementManager dmManagerTmp = null;
    FileWriter writer = null;
    BufferedReader UriReaderTxt = null;
    FileReader freader = null;
    String fileName = "RawStrutdQDWithQBuilderDB.txt";

    try {
      System.out.println("Running testRawStructuredQDWithQueryBuilder");

      // Setup a separate database/
      createDB(testMultipleDB);
      createForest(testMultipleForest[0], testMultipleDB);
      createForest(testMultipleForest[1], testMultipleDB);
      associateRESTServerWithDB(restServerName, testMultipleDB);

      setupAppServicesConstraint(testMultipleDB);
      Thread.sleep(10000);

      String[] filenames = { "curbappeal.xml", "flipper.xml", "justintime.xml" };

      clientTmp = getDatabaseClient("eval-user", "x", getConnType());
      dmManagerTmp = clientTmp.newDataMovementManager();

      QueryManager queryMgr = clientTmp.newQueryManager();
      String dataFileDir = dataConfigDirPath + "/data/";

      // Use WriteBatcher to write the same files.
      WriteBatcher wbatcher = dmManagerTmp.newWriteBatcher();

      wbatcher.withBatchSize(2);
      InputStreamHandle contentHandle1 = new InputStreamHandle();
      contentHandle1.set(new FileInputStream(dataFileDir + filenames[0]));
      InputStreamHandle contentHandle2 = new InputStreamHandle();
      contentHandle2.set(new FileInputStream(dataFileDir + filenames[1]));
      InputStreamHandle contentHandle3 = new InputStreamHandle();
      contentHandle3.set(new FileInputStream(dataFileDir + filenames[2]));

      wbatcher.add(filenames[0], contentHandle1);
      wbatcher.add(filenames[1], contentHandle2);
      wbatcher.add(filenames[2], contentHandle3);

      // Verify if the batch flushes when batch size is reached.
      // Flush
      wbatcher.flushAndWait();
      wbatcher.awaitCompletion();

      StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
      String options =
          "<options xmlns=\"http://marklogic.com/appservices/search\">" +
              "<constraint name='industry'>" +
              "<value>" +
              "<element name='industry' ns=''/>" +
              "</value>" +
              "</constraint>" +
              "</options>";

      RawStructuredQueryDefinition rsq = qb.build(qb.term("neighborhoods"),
          qb.valueConstraint("industry", "Real Estate"));
      String comboquery = "<search xmlns=\"http://marklogic.com/appservices/search\">" +
          rsq.toString() + options +
          "</search>";
      RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition((new StringHandle(comboquery)).withFormat(Format.XML));

      StringBuilder batchFailResults = new StringBuilder();

      // Run a QueryBatcher on the new URIs.
      QueryBatcher queryBatcher1 = dmManagerTmp.newQueryBatcher(querydef);
      queryBatcher1.withBatchSize(1);
      writer = new FileWriter(fileName);

      queryBatcher1.onUrisReady(new UrisToWriterListener(writer))
          .onQueryFailure(throwable -> {
            System.out.println("Exceptions thrown from callback onQueryFailure");
            throwable.printStackTrace();
            batchFailResults.append("Test has Exceptions");
          });

      JobTicket jobTicket = dmManagerTmp.startJob(queryBatcher1);
      boolean bJobFinished = queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);
      writer.flush();

      if (!batchFailResults.toString().isEmpty() && batchFailResults.toString().contains("Exceptions")) {
        fail("Test failed due to exceptions");
      }

      // Verify the batch results now.
      freader = new FileReader(fileName);
      UriReaderTxt = new BufferedReader(freader);
      TreeMap<String, String> expectedMap = new TreeMap<String, String>();
      TreeMap<String, String> uriMap = new TreeMap<String, String>();
      expectedMap.put(filenames[0], "URI");
      expectedMap.put(filenames[1], "URI");
      String line = null;

      while ((line = UriReaderTxt.readLine()) != null) {
        System.out.println("Line read from file with URIS is" + line);
        uriMap.put(line, "URI");
      }
      assertTrue("URIs not read correctly from testRawStructuredQDWithQueryBuilder method ", expectedMap.equals(uriMap));
    } catch (Exception e) {
      System.out.println("Exceptions thrown from Test testRawStructuredQDWithQueryBuilder");
      System.out.println(e.getMessage());
    } finally {
      // Associate back the original DB.
      associateRESTServerWithDB(restServerName, dbName);
      detachForest(testMultipleDB, testMultipleForest[0]);
      detachForest(testMultipleDB, testMultipleForest[1]);
      deleteDB(testMultipleDB);

      deleteForest(testMultipleForest[0]);
      deleteForest(testMultipleForest[1]);
      Thread.sleep(10000);
      try {
        if (writer != null)
          writer.close();
        if (UriReaderTxt != null)
          UriReaderTxt.close();
        if (freader != null)
          freader.close();
        // Delete the file on JVM exit
        File file = new File(fileName);
        file.deleteOnExit();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      clientTmp.release();
    }
  }

  /*
   * To test that RawStructuredQueryDefinition can be used withQueryBatcher
   * Store options from a file to server. Read a query from a file into a handle
   * Create a RawCombinedQueryDefinition from handle and options, to be used in
   * QueryBatcher Job.
   * 
   * @throws Exception
   */
  @Test
  public void testRawCombinedQueryXMLWithWriteOptions() throws Exception
  {
    String testMultipleDB = "RawCombinedQueryXMLDB";
    String[] testMultipleForest = { "RawCombinedQueryXMLDB-1", "RawCombinedQueryXMLDB-2", "RawCombinedQueryXMLDB-3" };
    DatabaseClient clientTmp = null;
    DataMovementManager dmManagerTmp = null;

    try {
      System.out.println("Running testRawCombinedQueryXMLWithWriteOptions");

      // Setup a separate database/
      createDB(testMultipleDB);
      createForest(testMultipleForest[0], testMultipleDB);
      createForest(testMultipleForest[1], testMultipleDB);
      associateRESTServerWithDB(restServerName, testMultipleDB);
      setupAppServicesConstraint(testMultipleDB);
      Thread.sleep(10000);

      String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
      String queryOptionFileName = "valueConstraintWithoutIndexSettingsAndNSOpt.xml";

      String queryName = "combinedQueryNoOption.xml";

      clientTmp = getDatabaseClient("eval-user", "x", getConnType());
      dmManagerTmp = clientTmp.newDataMovementManager();

      QueryManager queryMgr = clientTmp.newQueryManager();
      String dataFileDir = dataConfigDirPath + "/data/";
      String combQueryFileDir = dataConfigDirPath + "/combined/";

      // Use WriteBatcher to write the same files.
      WriteBatcher wbatcher = dmManagerTmp.newWriteBatcher();

      wbatcher.withBatchSize(2);
      InputStreamHandle contentHandle1 = new InputStreamHandle();
      contentHandle1.set(new FileInputStream(dataFileDir + filenames[0]));
      InputStreamHandle contentHandle2 = new InputStreamHandle();
      contentHandle2.set(new FileInputStream(dataFileDir + filenames[1]));
      InputStreamHandle contentHandle3 = new InputStreamHandle();
      contentHandle3.set(new FileInputStream(dataFileDir + filenames[2]));
      InputStreamHandle contentHandle4 = new InputStreamHandle();
      contentHandle4.set(new FileInputStream(dataFileDir + filenames[3]));
      InputStreamHandle contentHandle5 = new InputStreamHandle();
      contentHandle5.set(new FileInputStream(dataFileDir + filenames[4]));

      wbatcher.add(filenames[0], contentHandle1);
      wbatcher.add(filenames[1], contentHandle2);
      wbatcher.add(filenames[2], contentHandle3);
      wbatcher.add(filenames[3], contentHandle4);
      wbatcher.add(filenames[4], contentHandle5);

      // Verify if the batch flushes when batch size is reached.
      // Flush

      wbatcher.flushAndWait();
      wbatcher.awaitCompletion();

      setQueryOption(clientTmp, queryOptionFileName);
      // get the combined query
      File file = new File(combQueryFileDir + queryName);

      // create a handle for the search criteria
      FileHandle rawHandle = (new FileHandle(file)).withFormat(Format.XML);
      // create a search definition based on the handle
      RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle, queryOptionFileName);

      StringBuilder batchResults = new StringBuilder();
      StringBuilder batchFailResults = new StringBuilder();

      // Run a QueryBatcher on the new URIs.
      QueryBatcher queryBatcher1 = dmManagerTmp.newQueryBatcher(querydef);

      queryBatcher1.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchResults.append(str)
              .append('|');
        }
      });
      queryBatcher1.onQueryFailure(throwable -> {
        System.out.println("Exceptions thrown from callback onQueryFailure");
        throwable.printStackTrace();
        batchFailResults.append("Test has Exceptions");
      });

      JobTicket jobTicket = dmManagerTmp.startJob(queryBatcher1);
      boolean bJobFinished = queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);

      if (queryBatcher1.isStopped()) {

        if (!batchFailResults.toString().isEmpty() && batchFailResults.toString().contains("Exceptions")) {
          fail("Test failed due to exceptions");
        }

        // Verify the batch results now.
        String[] res = batchResults.toString().split("\\|");
        assertEquals("Number of reults returned is incorrect", 1, res.length);
        assertTrue("URI returned not correct", res[0].contains(filenames[4]));

        // Read the document and assert on the value
        DOMHandle contentHandle = new DOMHandle();
        contentHandle = readDocumentUsingDOMHandle(clientTmp, filenames[4], "XML");
        Document readDoc = contentHandle.get();
        System.out.println(convertXMLDocumentToString(readDoc));

        assertTrue("Document content returned not correct", readDoc.getElementsByTagName("id").item(0).getTextContent().contains("0026"));
        assertTrue("Document content returned not correct", readDoc.getElementsByTagName("title").item(0).getTextContent().contains("The memex"));
        assertTrue("Document content returned not correct", readDoc.getElementsByTagName("date").item(0).getTextContent().contains("2009-05-05"));
      }
    } catch (Exception e) {
      System.out.println("Exceptions thrown from testRawCombinedQueryXMLWithWriteOptions");
      System.out.println(e.getMessage());
    } finally {
      // Associate back the original DB.
      associateRESTServerWithDB(restServerName, dbName);
      detachForest(testMultipleDB, testMultipleForest[0]);
      detachForest(testMultipleDB, testMultipleForest[1]);
      deleteDB(testMultipleDB);

      deleteForest(testMultipleForest[0]);
      deleteForest(testMultipleForest[1]);
      Thread.sleep(10000);
      clientTmp.release();
    }
  }

  /*
   * To test that RawStructuredQueryDefinition can be used withQueryBatcher -
   * JSON file Read a query from a combined file into a handle.
   * combinedQueryOptionJSON.json contains query, options in JSON format.
   * 
   * @throws Exception
   */
  @Test
  public void testRawCombinedQueryJSON() throws Exception
  {
    String testMultipleDB = "RawCombinedRangeJsonDB";
    String[] testMultipleForest = { "RawCombinedRangeJsonDB-1", "RawCombinedRangeJsonDB-2", "RawCombinedRangeJsonDB-3" };
    DatabaseClient clientTmp = null;
    DataMovementManager dmManagerTmp = null;

    try {
      System.out.println("Running testRawCombinedQueryJSON");

      // Setup a separate database/
      createDB(testMultipleDB);
      createForest(testMultipleForest[0], testMultipleDB);
      createForest(testMultipleForest[1], testMultipleDB);
      associateRESTServerWithDB(restServerName, testMultipleDB);
      setupAppServicesConstraint(testMultipleDB);
      Thread.sleep(10000);

      String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
      String combinedQueryFileName = "combinedQueryOptionJSON.json";

      clientTmp = getDatabaseClient("eval-user", "x", getConnType());
      dmManagerTmp = clientTmp.newDataMovementManager();

      QueryManager queryMgr = clientTmp.newQueryManager();
      String dataFileDir = dataConfigDirPath + "/data/";
      String combQueryFileDir = dataConfigDirPath + "/combined/";

      // Use WriteBatcher to write the same files.
      WriteBatcher wbatcher = dmManagerTmp.newWriteBatcher();

      wbatcher.withBatchSize(2);
      InputStreamHandle contentHandle1 = new InputStreamHandle();
      contentHandle1.set(new FileInputStream(dataFileDir + filenames[0]));
      InputStreamHandle contentHandle2 = new InputStreamHandle();
      contentHandle2.set(new FileInputStream(dataFileDir + filenames[1]));
      InputStreamHandle contentHandle3 = new InputStreamHandle();
      contentHandle3.set(new FileInputStream(dataFileDir + filenames[2]));
      InputStreamHandle contentHandle4 = new InputStreamHandle();
      contentHandle4.set(new FileInputStream(dataFileDir + filenames[3]));
      InputStreamHandle contentHandle5 = new InputStreamHandle();
      contentHandle5.set(new FileInputStream(dataFileDir + filenames[4]));

      wbatcher.add(filenames[0], contentHandle1);
      wbatcher.add(filenames[1], contentHandle2);
      wbatcher.add(filenames[2], contentHandle3);
      wbatcher.add(filenames[3], contentHandle4);
      wbatcher.add(filenames[4], contentHandle5);

      // Verify if the batch flushes when batch size is reached.
      // Flush

      wbatcher.flushAndWait();
      wbatcher.awaitCompletion();

      // get the combined query
      File file = new File(combQueryFileDir + combinedQueryFileName);

      // create a handle for the search criteria
      FileHandle rawHandle = (new FileHandle(file)).withFormat(Format.JSON);
      // create a search definition based on the handle
      RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

      StringBuilder batchResults = new StringBuilder();
      StringBuilder batchFailResults = new StringBuilder();

      // Run a QueryBatcher on the new URIs.
      QueryBatcher queryBatcher1 = dmManagerTmp.newQueryBatcher(querydef);

      queryBatcher1.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchResults.append(str)
              .append('|');
        }
      });
      queryBatcher1.onQueryFailure(throwable -> {
        System.out.println("Exceptions thrown from callback onQueryFailure");
        throwable.printStackTrace();
        batchFailResults.append("Test has Exceptions");
      });

      JobTicket jobTicket = dmManagerTmp.startJob(queryBatcher1);
      boolean bJobFinished = queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);

      if (queryBatcher1.isStopped()) {

        if (!batchFailResults.toString().isEmpty() && batchFailResults.toString().contains("Exceptions")) {
          fail("Test failed due to exceptions");
        }

        // Verify the batch results now.
        String[] res = batchResults.toString().split("\\|");
        assertEquals("Number of reults returned is incorrect", 1, res.length);
        assertTrue("URI returned not correct", res[0].contains(filenames[4]));

        // Read the document and assert on the value
        DOMHandle contentHandle = new DOMHandle();
        contentHandle = readDocumentUsingDOMHandle(clientTmp, filenames[4], "XML");
        Document readDoc = contentHandle.get();
        System.out.println(convertXMLDocumentToString(readDoc));

        assertTrue("Document content returned not correct", readDoc.getElementsByTagName("id").item(0).getTextContent().contains("0026"));
        assertTrue("Document content returned not correct", readDoc.getElementsByTagName("title").item(0).getTextContent().contains("The memex"));
        assertTrue("Document content returned not correct", readDoc.getElementsByTagName("date").item(0).getTextContent().contains("2009-05-05"));
      }
    } catch (Exception e) {
      System.out.println("Exceptions thrown from testRawCombinedQueryJSONWithWriteOptions");
      System.out.println(e.getMessage());
    } finally {
      // Associate back the original DB.
      associateRESTServerWithDB(restServerName, dbName);
      detachForest(testMultipleDB, testMultipleForest[0]);
      detachForest(testMultipleDB, testMultipleForest[1]);
      deleteDB(testMultipleDB);

      deleteForest(testMultipleForest[0]);
      deleteForest(testMultipleForest[1]);
      Thread.sleep(10000);
      clientTmp.release();
    }
  }

  /*
   * To test that RawStructuredQueryDefinition can be used withQueryBatcher -
   * Combined file Read a query from a combined file into a handle. Create a
   * RawCombinedQueryDefinition from handle, to be used in QueryBatcher Job.
   * 
   * @throws Exception
   */
  @Test
  public void testRawCombinedQueryPathIndex() throws Exception
  {
    String testMultipleDB = "RawCombinedRangePathDB";
    String[] testMultipleForest = { "RawCombinedRangePathDB-1", "RawCombinedRangePathDB-2", "RawCombinedRangePathDB-3" };
    DatabaseClient clientTmp = null;
    DataMovementManager dmManagerTmp = null;

    try {
      System.out.println("Running testRawCombinedQueryPathIndex");

      // Setup a separate database/
      createDB(testMultipleDB);
      createForest(testMultipleForest[0], testMultipleDB);
      createForest(testMultipleForest[1], testMultipleDB);
      associateRESTServerWithDB(restServerName, testMultipleDB);
      setupAppServicesConstraint(testMultipleDB);
      Thread.sleep(60000);

      String[] filenames = { "pathindex1.xml", "pathindex2.xml" };
      String combinedQueryFileName = "combinedQueryOptionPathIndex.xml";

      clientTmp = getDatabaseClient("eval-user", "x", getConnType());
      dmManagerTmp = clientTmp.newDataMovementManager();

      QueryManager queryMgr = clientTmp.newQueryManager();
      String dataFileDir = dataConfigDirPath + "/data/";
      String combQueryFileDir = dataConfigDirPath + "/combined/";

      // Use WriteBatcher to write the same files.
      WriteBatcher wbatcher = dmManagerTmp.newWriteBatcher();

      wbatcher.withBatchSize(2);
      InputStreamHandle contentHandle1 = new InputStreamHandle();
      contentHandle1.set(new FileInputStream(dataFileDir + filenames[0]));
      InputStreamHandle contentHandle2 = new InputStreamHandle();
      contentHandle2.set(new FileInputStream(dataFileDir + filenames[1]));

      wbatcher.add(filenames[0], contentHandle1);
      wbatcher.add(filenames[1], contentHandle2);

      // Verify if the batch flushes when batch size is reached.
      // Flush

      wbatcher.flushAndWait();
      wbatcher.awaitCompletion();

      // get the combined query
      File file = new File(combQueryFileDir + combinedQueryFileName);

      // create a handle for the search criteria
      FileHandle rawHandle = new FileHandle(file);
      rawHandle.withFormat(Format.XML);
      // create a search definition based on the handle
      RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

      StringBuilder batchResults = new StringBuilder();
      StringBuilder batchFailResults = new StringBuilder();

      // Run a QueryBatcher on the new URIs.
      QueryBatcher queryBatcher1 = dmManagerTmp.newQueryBatcher(querydef);

      queryBatcher1.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchResults.append(str)
              .append('|');
        }
      });
      queryBatcher1.onQueryFailure(throwable -> {
        System.out.println("Exceptions thrown from callback onQueryFailure");
        throwable.printStackTrace();
        batchFailResults.append("Test has Exceptions");
      });

      JobTicket jobTicket = dmManagerTmp.startJob(queryBatcher1);
      boolean bJobFinished = queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);

      if (queryBatcher1.isStopped()) {

        if (!batchFailResults.toString().isEmpty() && batchFailResults.toString().contains("Exceptions")) {
          fail("Test failed due to exceptions");
        }

        // Verify the batch results now.
        String[] res = batchResults.toString().split("\\|");
        assertEquals("Number of reults returned is incorrect", 2, res.length);
        assertTrue("URI returned not correct", res[0].contains("pathindex1.xml") ? true : (res[1].contains("pathindex1.xml") ? true : false));
        assertTrue("URI returned not correct", res[0].contains("pathindex2.xml") ? true : (res[1].contains("pathindex2.xml") ? true : false));
      }
    } catch (Exception e) {
      System.out.println("Exceptions thrown from testRawCombinedQueryPathIndex");
      System.out.println(e.getMessage());
    } finally {
      // Associate back the original DB.
      associateRESTServerWithDB(restServerName, dbName);
      detachForest(testMultipleDB, testMultipleForest[0]);
      detachForest(testMultipleDB, testMultipleForest[1]);
      deleteDB(testMultipleDB);

      deleteForest(testMultipleForest[0]);
      deleteForest(testMultipleForest[1]);
      Thread.sleep(10000);
      clientTmp.release();
    }
  }
  
  @Test
  public void testRawCtsQuery() throws IOException, InterruptedException
  {
    System.out.println("Running testRawCtsQuery");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    WriteBatcher batcher = dmManager.newWriteBatcher();

    batcher.withBatchSize(2);
    // Move to individual data sub folders.
    String dataFileDir = dataConfigDirPath + "/data/";

    InputStreamHandle contentHandle1 = new InputStreamHandle();
    contentHandle1.set(new FileInputStream(dataFileDir + filenames[0]));
    InputStreamHandle contentHandle2 = new InputStreamHandle();
    contentHandle2.set(new FileInputStream(dataFileDir + filenames[1]));
    InputStreamHandle contentHandle3 = new InputStreamHandle();
    contentHandle3.set(new FileInputStream(dataFileDir + filenames[2]));
    InputStreamHandle contentHandle4 = new InputStreamHandle();
    contentHandle4.set(new FileInputStream(dataFileDir + filenames[3]));
    InputStreamHandle contentHandle5 = new InputStreamHandle();
    contentHandle5.set(new FileInputStream(dataFileDir + filenames[4]));

    batcher.add("cts-constraint1.xml", contentHandle1);
    batcher.add("cts-constraint2.xml", contentHandle2);
    batcher.add("cts-constraint3.xml", contentHandle3);
    batcher.add("cts-constraint4.xml", contentHandle4);
    batcher.add("cts-constraint5.xml", contentHandle5);

    // Flush
    batcher.flushAndWait();
    StringBuilder batchResults = new StringBuilder();
    StringBuilder batchFailResults = new StringBuilder();
    
    // create a search definition
    QueryManager queryMgr = client.newQueryManager();
    
    String wordQuery = "<cts:word-query xmlns:cts=\"http://marklogic.com/cts\">" +
                       "<cts:text>unfortunately</cts:text></cts:word-query>";
    StringHandle handle = new StringHandle().with(wordQuery);
    RawCtsQueryDefinition querydef = queryMgr.newRawCtsQueryDefinition(handle);

    // Run a QueryBatcher.
    QueryBatcher queryBatcher1 = dmManager.newQueryBatcher(querydef);
    queryBatcher1.onUrisReady(batch -> {

      for (String str : batch.getItems()) {
        batchResults.append(str).append('|');
      }

      batchResults.append(batch.getJobResultsSoFar())
          .append('|')
          .append(batch.getForest().getForestName())
          .append('|')
          .append(batch.getJobBatchNumber())
          .append('|');

    })
    .onQueryFailure(throwable -> {
        System.out.println("Exceptions thrown from callback onQueryFailure" + throwable.getMessage());
    });
    
    JobTicket jobTicket = dmManager.startJob(queryBatcher1);
    queryBatcher1.awaitCompletion(1, TimeUnit.MINUTES);
    
    System.out.println("Batch Results are : " + batchResults.toString());
    System.out.println("File name is : " + filenames[4]);
    assertTrue("URI returned not correct", batchResults.toString().contains("cts-" +filenames[4]));

    // Read the document and assert on the value
    DOMHandle contentHandle = new DOMHandle();
    contentHandle = readDocumentUsingDOMHandle(client, "cts-"+filenames[4], "XML");
    Document readDoc = contentHandle.get();
    try {
		System.out.println(convertXMLDocumentToString(readDoc));
	} catch (TransformerException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

    assertTrue("Document content returned not correct", readDoc.getElementsByTagName("id").item(0).getTextContent().contains("0026"));
    assertTrue("Document content returned not correct", readDoc.getElementsByTagName("title").item(0).getTextContent().contains("The memex"));
    assertTrue("Document content returned not correct", readDoc.getElementsByTagName("date").item(0).getTextContent().contains("2009-05-05"));
  }
  
  /*
   * To test query by example with WriteBatcher and QueryBatcher with Query
   * Failure (incorrect query syntax).
   * 
   * @throws IOException
   * 
   * @throws InterruptedException
   */
  // EA 3 Modify the test for batch failure results. Remove the fail.
  @Ignore
  public void testQueryBatcherQueryFailures() throws IOException, InterruptedException
  {
    System.out.println("Running testQueryBatcherQueryFailures");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    WriteBatcher batcher = dmManager.newWriteBatcher();

    batcher.withBatchSize(2);
    // Move to individual data sub folders.
    String dataFileDir = dataConfigDirPath + "/data/";

    InputStreamHandle contentHandle1 = new InputStreamHandle();
    contentHandle1.set(new FileInputStream(dataFileDir + filenames[0]));
    InputStreamHandle contentHandle2 = new InputStreamHandle();
    contentHandle2.set(new FileInputStream(dataFileDir + filenames[1]));
    InputStreamHandle contentHandle3 = new InputStreamHandle();
    contentHandle3.set(new FileInputStream(dataFileDir + filenames[2]));
    InputStreamHandle contentHandle4 = new InputStreamHandle();
    contentHandle4.set(new FileInputStream(dataFileDir + filenames[3]));
    InputStreamHandle contentHandle5 = new InputStreamHandle();
    contentHandle5.set(new FileInputStream(dataFileDir + filenames[4]));

    batcher.add("/fail-contraints1.xml", contentHandle1);
    batcher.add("/fail-contraints2.xml", contentHandle2);
    batcher.add("/fail-contraints3.xml", contentHandle3);
    batcher.add("/fail-contraints4.xml", contentHandle4);
    batcher.add("/fail-contraints5.xml", contentHandle5);

    // Flush
    batcher.flushAndWait();
    StringBuilder batchResults = new StringBuilder();
    StringBuilder batchFailResults = new StringBuilder();
    // create query def
    String combinedQuery = "{\"search\":" +
        "{\"query\":{\"value-constraint-query\":{\"constraint-name\":\"id\", \"text\":\"0026\"}}," +
        "\"options\":{\"return-metrcs\":false, \"return-qtext\":false, \"debug\":true, \"transorm-results\":{\"apply\":\"raw\"}," +
        "\"constraint\":{\"name\":\"id\", \"value\":{\"element\":{\"ns\":\"\", \"name\":\"id\"}}}}}}";

    System.out.println("QUERY IS : " + combinedQuery);
    // create a handle for the search criteria
    StringHandle rawHandle = new StringHandle(combinedQuery);

    rawHandle.setFormat(Format.JSON);

    QueryManager queryMgr = client.newQueryManager();

    // create a search definition based on the handle
    RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

    // Run a QueryBatcher.
    QueryBatcher queryBatcher1 = dmManager.newQueryBatcher(querydef);
    queryBatcher1.onUrisReady(batch -> {

      for (String str : batch.getItems()) {
        batchResults.append(str).append('|');
      }

      batchResults.append(batch.getJobResultsSoFar())
          .append('|')
          .append(batch.getForest().getForestName())
          .append('|')
          .append(batch.getJobBatchNumber())
          .append('|');

    });
    queryBatcher1.onQueryFailure(throwable -> {
      System.out.println("Exceptions thrown from callback onQueryFailure");
      Forest forest = throwable.getForest();
      batchFailResults.append("Test has Exceptions")
          .append('|')
          .append(throwable.getForestResultsSoFar())
          .append('|')
          /*
           * .append(throwable. getJobRecordNumber()) .append('|')
           * .append(throwable.getBatchRecordNumber()) .append('|')
           * .append(throwable.getSourceUri()) .append('|')
           * .append(throwable.getMimetype()) .append('|')
           */
          .append(forest.getForestName())
          .append('|')
          .append(forest.getHost())
          .append('|')
          .append(forest.getDatabaseName())
          .append('|')
          /*
           * .append(forest.isDeleteOnly()) .append('|')
           */
          .append(forest.isUpdateable());
    });
    JobTicket jobTicket = dmManager.startJob(queryBatcher1);
    queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);

    if (queryBatcher1.isStopped()) {

      if (!batchFailResults.toString().isEmpty() && batchFailResults.toString().contains("Exceptions")) {
        // Write out and assert on query failures.
        System.out.println("Exception Buffer contents on Query Exceptions received from callback onQueryFailure");
        System.out.println(batchFailResults.toString());
        // Remove this failure once there are no NPEs and doa asserts on various
        // counters in failure scenario.
        fail("Test failed due to exceptions");
      }
    }
  }

  /*
   * To test QueryBatcher's callback support by invoking the client object to do
   * a lookup Insert only one document to validate the functionality
   * 
   * @throws IOException
   * 
   * @throws InterruptedException
   */
  @Test
  public void testQueryBatcherCallbackClient() throws IOException, InterruptedException
  {
    System.out.println("Running testQueryBatcherCallbackClient");

    String[] filenames = { "constraint1.json" };
    WriteBatcher batcher = dmManager.newWriteBatcher();

    batcher.withBatchSize(2);
    // Move to individual data sub folders.
    String dataFileDir = dataConfigDirPath + "/data/";

    InputStreamHandle contentHandle1 = new InputStreamHandle();
    contentHandle1.set(new FileInputStream(dataFileDir + filenames[0]));
    batcher.add("contraints1.json", contentHandle1);

    // Flush
    batcher.flushAndWait();
    StringBuffer batchFailResults = new StringBuffer();
    String expectedStr = "Vannevar Bush wrote an article for The Atlantic Monthly";

    QueryManager queryMgr = client.newQueryManager();
    StringQueryDefinition querydef = queryMgr.newStringDefinition();
    querydef.setCriteria("Vannevar");

    // Run a QueryBatcher.
    QueryBatcher queryBatcher1 = dmManager.newQueryBatcher(querydef);

    queryBatcher1.withBatchSize(1000);
    // Hold for contents read back from callback client.
    StringBuffer ccBuf = new StringBuffer();

    queryBatcher1.onUrisReady(batch -> {
      // Do a lookup back into the database with the client and batch content.
      // Want to verify if the client object can be utilized from a Callback.
        JSONDocumentManager docMgr = batch.getClient().newJSONDocumentManager();
        JacksonHandle jh = new JacksonHandle();
        docMgr.read(batch.getItems()[0], jh);
        System.out.println("JH Contents is " + jh.get().toString());
        System.out.println("Batch Contents is " + batch.getItems()[0]);

        ccBuf.append(jh.get().toString());
      });
    queryBatcher1.onQueryFailure(throwable -> {
      System.out.println("Exceptions thrown from callback onQueryFailure");

      batchFailResults.append("Test has Exceptions").append('|');
    });
    JobTicket jobTicket = dmManager.startJob(queryBatcher1);
    queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);

    if (queryBatcher1.isStopped()) {

      if (!batchFailResults.toString().isEmpty() && batchFailResults.toString().contains("Exceptions")) {
        // Write out and assert on query failures.
        System.out.println("Exception Buffer contents on Query Exceptions received from callback onQueryFailure");
        System.out.println(batchFailResults.toString());
        fail("Test failed due to exceptions");
      }
      System.out.println("Contents from the callback are : " + ccBuf.toString());
      // Verify the Callback contents.
      assertTrue("Lookup for a document from Callback using the client failed", ccBuf.toString().contains(expectedStr));
    }
  }

  /*
   * Test to validate QueryBatcher when there is no data. No search results are
   * returned.
   */
  @Test
  public void testQueryBatcherWithNoData() throws IOException, InterruptedException
  {
    System.out.println("Running testQueryBatcherWithNoData");

    String jsonDoc = "{" +
        "\"employees\": [" +
        "{ \"firstName\":\"John\" , \"lastName\":\"Doe\" }," +
        "{ \"firstName\":\"Ann\" , \"lastName\":\"Smith\" }," +
        "{ \"firstName\":\"Bob\" , \"lastName\":\"Foo\" }]" +
        "}";
    // create query def
    QueryManager queryMgr = client.newQueryManager();

    StringQueryDefinition querydef = queryMgr.newStringDefinition();
    querydef.setCriteria("John AND Bob");

    // Run a QueryBatcher when no results are returned.
    QueryBatcher queryBatcherNoResult = dmManager.newQueryBatcher(querydef);

    StringBuilder batchNoResults = new StringBuilder();
    StringBuilder batchNoFailResults = new StringBuilder();

    queryBatcherNoResult.onUrisReady(batch -> {
      for (String str : batch.getItems()) {
        batchNoResults.append(str).append('|');
      }
    });
    queryBatcherNoResult.onQueryFailure(throwable -> {
      System.out.println("Exceptions thrown from callback onQueryFailure when no results returned");
      // Should be empty in a successful run. Else fill the buffer to report
      // error.
        batchNoFailResults.append("Test has Exceptions");
        batchNoFailResults.append("|");
      });
    JobTicket jobTicketNoRes = dmManager.startJob(queryBatcherNoResult);
    queryBatcherNoResult.awaitCompletion(30, TimeUnit.SECONDS);

    if (queryBatcherNoResult.isStopped()) {
      assertTrue("Query returned no results when there is no data", batchNoResults.toString().isEmpty());
    }
  }

  /*
   * To test query by example with WriteBatcher and QueryBatcher 1) Verify batch
   * size on QueryBatcher.
   * 
   * 
   * @throws IOException
   * 
   * @throws InterruptedException
   */
  @Test
  public void testQueryBatcherBatchSize() throws IOException, InterruptedException
  {
    System.out.println("Running testQueryBatcherBatchSize");

    String jsonDoc = "{" +
        "\"employees\": [" +
        "{ \"firstName\":\"John\" , \"lastName\":\"Doe\" }," +
        "{ \"firstName\":\"Ann\" , \"lastName\":\"Smith\" }," +
        "{ \"firstName\":\"Bob\" , \"lastName\":\"Foo\" }]" +
        "}";
    // create query def
    QueryManager queryMgr = client.newQueryManager();

    StringQueryDefinition querydef = queryMgr.newStringDefinition();
    querydef.setCriteria("John AND Bob");

    WriteBatcher batcher = dmManager.newWriteBatcher();
    batcher.withBatchSize(1000);
    StringHandle handle = new StringHandle();
    handle.set(jsonDoc);
    String uri = null;

    // Insert 1K documents
    for (int i = 0; i < 1000; i++) {
      uri = "/firstName" + i + ".json";
      batcher.add(uri, handle);
    }

    // Flush
    batcher.flushAndWait();
    StringBuffer batchResults = new StringBuffer();
    StringBuffer batchFailResults = new StringBuffer();

    // Run a QueryBatcher with a large AwaitTermination.
    QueryBatcher queryBatcherbatchSize = dmManager.newQueryBatcher(querydef);
    queryBatcherbatchSize.withBatchSize(20);

    Calendar calBef = Calendar.getInstance();
    long before = calBef.getTimeInMillis();

    queryBatcherbatchSize.onUrisReady(batch -> {
      batchResults.append(batch.getJobBatchNumber()).append('|');
      System.out.println("Batch Numer is " + batch.getJobBatchNumber());

    });
    queryBatcherbatchSize.onQueryFailure(throwable -> {
      System.out.println("Exceptions thrown from callback onQueryFailure");

      batchFailResults.append("Test has Exceptions")
          .append('|');
    });
    JobTicket jobTicket = dmManager.startJob(queryBatcherbatchSize);
    // Make sure to modify TimeUnit.TIMEUNIT.Method(duration) below before the
    // assert
    queryBatcherbatchSize.awaitCompletion(3, TimeUnit.MINUTES);

    Calendar calAft;
    long after = 0L;
    long duration = 0L;
    long queryJobTimeoutValue = 0L;

    while (!queryBatcherbatchSize.isStopped()) {
      // do nothing.
    }
    // Check the time of termination
    calAft = Calendar.getInstance();
    after = calAft.getTimeInMillis();
    duration = after - before;
    queryJobTimeoutValue = TimeUnit.MINUTES.toSeconds(duration);

    if (queryBatcherbatchSize.isStopped()) {
      System.out.println("Duration is ===== " + queryJobTimeoutValue);
      System.out.println(batchResults.toString());

      assertEquals("Number of batches should have been 50", batchResults.toString().split("\\|").length, 50);
    }
    // Clear the contents for next query host batcher object results.
    batchResults.delete(0, (batchResults.capacity() - 1));
    batchFailResults.delete(0, (batchFailResults.capacity() - 1));
    // Run a QueryBatcher with a small AwaitTermination.
    QueryBatcher queryBatcherSmallTimeout = dmManager.newQueryBatcher(querydef);
    queryBatcherSmallTimeout.withBatchSize(1000);

    queryBatcherSmallTimeout.onUrisReady(batch -> {
      batchResults.append(batch.getJobBatchNumber()).append('|');
      System.out.println("QueryBatcher with 1000 batch size - Batch Numer is " + batch.getJobBatchNumber());

    });
    queryBatcherSmallTimeout.onQueryFailure(throwable -> {
      System.out.println("Exceptions thrown from callback onQueryFailure");

      batchFailResults.append("Test has Exceptions").append('|');
      batchFailResults.append(throwable.getJobBatchNumber());
    });
    JobTicket jobTicketTimeout = dmManager.startJob(queryBatcherSmallTimeout);
    queryBatcherSmallTimeout.awaitCompletion(5, TimeUnit.MILLISECONDS);
    if (queryBatcherSmallTimeout.isStopped()) {
      System.out.println(batchResults.toString());
      assertNotEquals("Number of batches should not have been 1", batchResults.toString().split("\\|").length, 5);
    }
    if (batchFailResults != null && !batchFailResults.toString().isEmpty()) {
      assertTrue("Exceptions not found when query time out value reached", batchFailResults.toString().contains("Test has Exceptions"));
    }

  }

  /*
   * To test query by example with WriteBatcher and QueryBatcher 1) Verify
   * awaitTermination method on QueryBatcher.
   * 
   * 
   * @throws IOException
   * 
   * @throws InterruptedException
   */
  @Test
  public void testQueryBatcherFailures() throws IOException, InterruptedException
  {
    System.out.println("Running testQueryBatcherFailures");

    String jsonDoc = "{" +
        "\"employees\": [" +
        "{ \"firstName\":\"John\" , \"lastName\":\"Doe\" }," +
        "{ \"firstName\":\"Ann\" , \"lastName\":\"Smith\" }," +
        "{ \"firstName\":\"Bob\" , \"lastName\":\"Foo\" }]" +
        "}";
    // create query def
    QueryManager queryMgr = client.newQueryManager();

    StringQueryDefinition querydef = queryMgr.newStringDefinition();
    querydef.setCriteria("John AND Bob");

    WriteBatcher batcher = dmManager.newWriteBatcher();
    batcher.withBatchSize(1000);
    StringHandle handle = new StringHandle();
    handle.set(jsonDoc);
    String uri = null;

    // Insert 10 K documents
    for (int i = 0; i < 10000; i++) {
      uri = "/firstName" + i + ".json";
      batcher.add(uri, handle);
    }

    // Flush
    batcher.flushAndWait();
    StringBuilder batchResults = new StringBuilder();
    StringBuilder batchFailResults = new StringBuilder();

    // Run a QueryBatcher with AwaitTermination.
    QueryBatcher queryBatcherAwait = dmManager.newQueryBatcher(querydef);

    Calendar calBef = Calendar.getInstance();
    long before = calBef.getTimeInMillis();

    JobTicket jobTicket = dmManager.startJob(queryBatcherAwait);
    // Make sure to modify TimeUnit.MILLISECONDS.Method(duration) below before
    // the assert
    queryBatcherAwait.awaitCompletion(30, TimeUnit.SECONDS);

    queryBatcherAwait.onUrisReady(batch -> {
      for (String str : batch.getItems()) {
        batchResults.append(str)
            .append('|');
      }
    });
    queryBatcherAwait.onQueryFailure(throwable -> {
      System.out.println("Exceptions thrown from callback onQueryFailure");

      batchFailResults.append("Test has Exceptions")
          .append('|');
    });

    Calendar calAft;
    long after = 0L;
    long duration = 0L;
    long quertJobTimeoutValue = 0L;

    while (!queryBatcherAwait.isStopped()) {
      // do nothing
    }
    // Check the time of termination
    calAft = Calendar.getInstance();
    after = calAft.getTimeInMillis();
    duration = after - before;
    quertJobTimeoutValue = TimeUnit.MILLISECONDS.toSeconds(duration);

    if (queryBatcherAwait.isStopped()) {
      System.out.println("Duration is " + quertJobTimeoutValue);
      if (quertJobTimeoutValue >= 30 && quertJobTimeoutValue < 35) {
        assertTrue("Job termination with awaitTermination passed within specified time", quertJobTimeoutValue >= 30 && quertJobTimeoutValue < 35);
      } else if (quertJobTimeoutValue > 35) {
        fail("Job termination with awaitTermination failed");
      }
    }
  }

  @Test
  public void testServerXQueryTransform() throws IOException, ParserConfigurationException, SAXException, TransformerException, InterruptedException, XPathExpressionException
  {
    System.out.println("Running testServerXQueryTransform");
    String transformFileDir = dataConfigDirPath + "/transforms/";
    TransformExtensionsManager transMgr =
        client.newServerConfigManager().newTransformExtensionsManager();
    ExtensionMetadata metadata = new ExtensionMetadata();
    metadata.setTitle("Adding attribute xquery Transform");
    metadata.setDescription("This plugin transforms an XML document by adding attribute to root node");
    metadata.setProvider("MarkLogic");
    metadata.setVersion("0.1");
    // get the transform file from add-attr-xquery-transform.xqy
    File transformFile = new File(transformFileDir + "add-attr-xquery-transform.xqy");
    FileHandle transformHandle = new FileHandle(transformFile);
    transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle, metadata);
    ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
    transform.put("name", "Lang");
    transform.put("value", "English");

    String xmlStr1 = "<?xml  version=\"1.0\" encoding=\"UTF-8\"?><foo>This is so foo</foo>";
    String xmlStr2 = "<?xml  version=\"1.0\" encoding=\"UTF-8\"?><foo>This is so bar</foo>";

    // Use WriteBatcher to write the same files.
    WriteBatcher batcher = dmManager.newWriteBatcher();

    batcher.withBatchSize(5);
    batcher.withTransform(transform);
    StringHandle handleFoo = new StringHandle();
    handleFoo.set(xmlStr1);

    StringHandle handleBar = new StringHandle();
    handleBar.set(xmlStr2);
    String uri = null;

    // Insert 10 documents
    for (int i = 0; i < 10; i++) {
      uri = "foo" + i + ".xml";
      batcher.add(uri, handleFoo);
    }

    for (int i = 0; i < 10; i++) {
      uri = "bar" + i + ".xml";
      batcher.add(uri, handleBar);
    }
    // Flush
    batcher.flushAndWait();

    StringBuffer batchResults = new StringBuffer();
    StringBuffer batchFailResults = new StringBuffer();

    // create query def
    QueryManager queryMgr = client.newQueryManager();
    StringQueryDefinition querydef = queryMgr.newStringDefinition();
    querydef.setCriteria("foo OR bar");

    // Run a QueryBatcher on the new URIs.
    QueryBatcher queryBatcher1 = dmManager.newQueryBatcher(querydef);
    queryBatcher1.withBatchSize(5);

    queryBatcher1.onUrisReady(batch -> {
      for (String str : batch.getItems()) {
        batchResults.append(str);
        batchResults.append("|");
      }
    });
    queryBatcher1.onQueryFailure(throwable -> {
      System.out.println("Exceptions thrown from callback onQueryFailure");
      throwable.printStackTrace();
      batchFailResults.append("Test has Exceptions");
    });
    JobTicket jobTicket = dmManager.startJob(queryBatcher1);

    if (queryBatcher1.isStopped()) {
      // Verify the batch results now.
      String[] res = batchResults.toString().split("\\|");
      assertEquals("Query results URI list length returned after transformation incorrect", res.length, 20);

      // Get a random URI, since the URIs returned are not ordered. Get the 3rd
      // URI.
      assertTrue("URI returned not correct", res[2].contains("foo") || res[2].contains("bar"));

      // do a lookup with the first URI using the client to verify transforms
      // are done.
      DOMHandle readHandle = readDocumentUsingDOMHandle(client, res[0], "XML");
      String contents = readHandle.evaluateXPath("/foo/text()", String.class);
      // Verify that the contents are of xmlStr1 or xmlStr2.

      System.out.println("Contents are : " + contents);
      assertTrue("Lookup for a document from Callback using the client failed", xmlStr1.contains(contents) || xmlStr2.contains(contents));
    }
  }

  /*
   * To test QueryBatcher functionality (errors if any) when a Forest is being
   * removed and added during a start job.
   * 
   * @throws IOException
   * 
   * @throws InterruptedException
   */
  @Test
  public void testQueryBatcherWithForestRemoveAndAdd() throws IOException, InterruptedException
  {
    System.out.println("Running testQueryBatcherWithForestRemoveAndAdd");
    String testMultipleDB = "QBMultipleForestDB";
    String[] testMultipleForest = { "QBMultipleForestDB-1", "QBMultipleForestDB-2", "QBMultipleForestDB-3" };

    try {
      // Setup a separate database/
      createDB(testMultipleDB);
      createForest(testMultipleForest[0], testMultipleDB);
      setupAppServicesConstraint(testMultipleDB);

      associateRESTServerWithDB(restServerName, testMultipleDB);

      String jsonDoc = "{" +
          "\"employees\": [" +
          "{ \"firstName\":\"John\" , \"lastName\":\"Doe\" }," +
          "{ \"firstName\":\"Ann\" , \"lastName\":\"Smith\" }," +
          "{ \"firstName\":\"Bob\" , \"lastName\":\"Foo\" }]" +
          "}";
      // create query def
      QueryManager queryMgr = client.newQueryManager();

      StringQueryDefinition querydef = queryMgr.newStringDefinition();
      querydef.setCriteria("John AND Bob");

      WriteBatcher batcher = dmManager.newWriteBatcher();
      batcher.withBatchSize(1000);
      StringHandle handle = new StringHandle();
      handle.set(jsonDoc);
      String uri = null;

      // Insert 20K documents to have a sufficient large query seek time
      for (int i = 0; i < 20000; i++) {
        uri = "/firstName" + i + ".json";
        batcher.add(uri, handle);
      }

      // Flush
      batcher.flushAndWait();
      StringBuffer batchResults = new StringBuffer();
      StringBuffer batchFailResults = new StringBuffer();

      // Run a QueryBatcher with AwaitTermination.
      QueryBatcher queryBatcherAddForest = dmManager.newQueryBatcher(querydef);
      queryBatcherAddForest.withBatchSize(2000);

      queryBatcherAddForest.onUrisReady(batch -> {
        batchResults.append(batch.getJobBatchNumber())
            .append('|');
        System.out.println("Batch Numer is " + batch.getJobBatchNumber());
      });
      queryBatcherAddForest.onQueryFailure(throwable -> {
        System.out.println("Exceptions thrown from callback onQueryFailure");

        batchFailResults.append("Test has Exceptions")
            .append('|')
            .append(throwable.getMessage());
      });

      JobTicket jobTicket = dmManager.startJob(queryBatcherAddForest);
      queryBatcherAddForest.awaitCompletion(3, TimeUnit.MINUTES);

      // Now add a Forests to the database.
      createForest(testMultipleForest[1], testMultipleDB);
      createForest(testMultipleForest[2], testMultipleDB);
      while (!queryBatcherAddForest.isStopped()) {
        // Do nothing. Wait for batcher to complete.
      }

      if (queryBatcherAddForest.isStopped()) {
        if (batchResults != null && !batchResults.toString().isEmpty()) {
          System.out.print("Results from onUrisReady === ");
          System.out.print(batchResults.toString());
          // We should be having 10 batches numbered 1 to 10.
          assertTrue("Batches not complete in results", batchResults.toString().contains("10"));
        }
        if (batchFailResults != null && !batchFailResults.toString().isEmpty()) {
          System.out.print("Results from onQueryFailure === ");
          System.out.print(batchFailResults.toString());
          assertTrue("Exceptions not found when forest added", batchFailResults.toString().contains("Test has Exceptions"));
        }
      }

      // Reomove a forest.
      StringBuffer batchResultsRem = new StringBuffer();
      StringBuffer batchFailResultsRem = new StringBuffer();

      // Run a QueryBatcher with AwaitTermination.
      QueryBatcher queryBatcherRemoveForest = dmManager.newQueryBatcher(querydef);
      queryBatcherRemoveForest.withBatchSize(2000);

      queryBatcherRemoveForest.onUrisReady(batch -> {
        batchResultsRem.append(batch.getJobBatchNumber())
            .append('|');
        System.out.println("Batch Numer is " + batch.getJobBatchNumber());
      });
      queryBatcherRemoveForest.onQueryFailure(throwable -> {
        System.out.println("Exceptions thrown from callback onQueryFailure");

        batchFailResultsRem.append("Test has Exceptions");
        batchFailResultsRem.append("|");
        batchFailResultsRem.append(throwable.getMessage());
      });

      JobTicket jobTicketRem = dmManager.startJob(queryBatcherRemoveForest);
      queryBatcherRemoveForest.awaitCompletion(3, TimeUnit.MINUTES);

      // Now remove a Forest from the database.
      detachForest(testMultipleDB, testMultipleForest[2]);
      deleteForest(testMultipleForest[2]);
      while (!queryBatcherRemoveForest.isStopped()) {
        // Do nothing. Wait for batcher to complete.
      }

      if (queryBatcherRemoveForest.isStopped()) {
        if (batchResultsRem != null && !batchResultsRem.toString().isEmpty()) {
          System.out.print("Results from onUrisReady === ");
          // We should be having 10 batches numbered 1 to 10.
          // TODO Add rest of the validations when feature complete.
          System.out.print(batchResultsRem.toString());
          assertTrue("Batches not complete in results when forest removed", batchResultsRem.toString().contains("10"));
        }
        if (batchFailResultsRem != null && !batchFailResultsRem.toString().isEmpty()) {
          System.out.print("Results from onQueryFailure === ");
          System.out.print(batchFailResultsRem.toString());
          assertTrue("Exceptions not found when forest removed", batchFailResultsRem.toString().contains("Test has Exceptions"));
        }
      }
    } catch (Exception e) {
      System.out.print(e.getMessage());
    }

    finally {
      // Associate back the original DB.
      try {
        associateRESTServerWithDB(restServerName, dbName);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      detachForest(testMultipleDB, testMultipleForest[0]);
      detachForest(testMultipleDB, testMultipleForest[1]);
      // In case something asserts
      detachForest(testMultipleDB, testMultipleForest[2]);
      deleteDB(testMultipleDB);

      deleteForest(testMultipleForest[0]);
      deleteForest(testMultipleForest[1]);
      deleteForest(testMultipleForest[2]);
      Thread.sleep(10000);
    }
  }

  /*
   * To test QueryBatcher's callback support with long lookup timefor the client
   * object to do a lookup Insert documents to validate the functionality.
   * Induce a long pause which exceeds awaitTermination time.
   * 
   * @throws IOException
   * 
   * @throws InterruptedException
   */
  @Test
  public void testBatchClientLookupTimeout() throws IOException, InterruptedException
  {
    System.out.println("Running testBatchClientLookupTimeout");
    String testMultipleDB = "QBMultipleForestDB";
    String[] testMultipleForest = { "QBMultipleForestDB-1" };

    try {
      // Setup a separate database/
      createDB(testMultipleDB);
      createForest(testMultipleForest[0], testMultipleDB);

      associateRESTServerWithDB(restServerName, testMultipleDB);
      setupAppServicesConstraint(testMultipleDB);

      String jsonDoc = "{" +
          "\"employees\": [" +
          "{ \"firstName\":\"John\" , \"lastName\":\"Doe\" }," +
          "{ \"firstName\":\"Ann\" , \"lastName\":\"Smith\" }," +
          "{ \"firstName\":\"Bob\" , \"lastName\":\"Foo\" }]" +
          "}";
      // create query def
      QueryManager queryMgr = client.newQueryManager();

      StringQueryDefinition querydef = queryMgr.newStringDefinition();
      querydef.setCriteria("John AND Bob");

      WriteBatcher batcher = dmManager.newWriteBatcher();
      batcher.withBatchSize(1000);
      StringHandle handle = new StringHandle();
      handle.set(jsonDoc);
      String uri = null;

      // Insert 20K documents to have a sufficient large query seek time
      for (int i = 0; i < 20000; i++) {
        uri = "/firstName" + i + ".json";
        batcher.add(uri, handle);
      }

      // Flush
      batcher.flushAndWait();
      StringBuilder batchResults = new StringBuilder();
      StringBuffer batchFailResults = new StringBuffer();
      StringBuilder ccBuf = new StringBuilder();

      // Run a QueryBatcher with AwaitTermination.
      QueryBatcher queryBatcherAddForest = dmManager.newQueryBatcher(querydef);
      queryBatcherAddForest.withBatchSize(200);

      queryBatcherAddForest.onUrisReady(batch -> {
        // Check only once
          if (ccBuf.toString().isEmpty())
          {
            JSONDocumentManager docMgr = batch.getClient().newJSONDocumentManager();
            JacksonHandle jh = new JacksonHandle();
            docMgr.read(batch.getItems()[0], jh);
            try {
              // Simulate a large time in reading back the results
              Thread.sleep(40000);
            } catch (Exception e) {
              // TODO Auto-generated catch block
              System.out.println(e.getMessage());
            }
            ccBuf.append(jh.get().toString().trim());
            // The first read should exhaust the awaitTermination timeout.
            // Buffer contains only one result.
            System.out.println("JH Contents is " + jh.get().toString());
            System.out.println("Batch Contents is " + batch.getItems()[0]);
          }

          batchResults.append(batch.getJobBatchNumber());
          batchResults.append("|");
          System.out.println("Batch Numer is " + batch.getJobBatchNumber());
        });
      queryBatcherAddForest.onQueryFailure(throwable -> {
        System.out.println("Exceptions thrown from callback onQueryFailure");

        batchFailResults.append("Test has Exceptions");
        batchFailResults.append("|");
        batchFailResults.append(throwable.getMessage());
      });
      // Have a small awaitCompletion timeout for the batcher.
      JobTicket jobTicket = dmManager.startJob(queryBatcherAddForest);
      queryBatcherAddForest.awaitCompletion(30, TimeUnit.SECONDS);

      if (queryBatcherAddForest.isStopped()) {

        if (batchFailResults != null && !batchFailResults.toString().isEmpty()) {
          System.out.print("Results from onQueryFailure === ");
          System.out.print(batchFailResults.toString());
          assertTrue("Exceptions not found when forest added", batchFailResults.toString().contains("Test has Exceptions"));
        }
      }
      assertTrue("Batches are available in results when they should not be.", batchResults.toString().isEmpty());
    } catch (Exception e) {
      System.out.print(e.getMessage());
    } finally {
      // Associate back the original DB.
      try {
        associateRESTServerWithDB(restServerName, dbName);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      detachForest(testMultipleDB, testMultipleForest[0]);
      deleteDB(testMultipleDB);
      deleteForest(testMultipleForest[0]);
      Thread.sleep(10000);
    }
  }

  /*
   * To test QQueryBatcher when WriteBatcher writes same document. Simulate a
   * deadlock / resource contention.
   * 
   * @throws IOException
   * 
   * @throws InterruptedException
   */

  /*
   * public void testSimultaneousBothBatcherAccess() throws IOException,
   * InterruptedException {
   * System.out.println("Running testSimultaneousBothBatcherAccess");
   * clearDB(restServerPort);
   * 
   * String[] filenames = {"constraint1.json", "constraint2.json",
   * "constraint3.json", "constraint4.json", "constraint5.json"}; WriteBatcher
   * batcher = dmManager.newWriteBatcher();
   * 
   * batcher.withBatchSize(2);
   * 
   * InputStreamHandle contentHandle1 = new InputStreamHandle();
   * contentHandle1.set(new FileInputStream(dataFileDir + filenames[0]));
   * InputStreamHandle contentHandle2 = new InputStreamHandle();
   * contentHandle2.set(new FileInputStream(dataFileDir + filenames[1]));
   * InputStreamHandle contentHandle3 = new InputStreamHandle();
   * contentHandle3.set(new FileInputStream(dataFileDir + filenames[2]));
   * InputStreamHandle contentHandle4 = new InputStreamHandle();
   * contentHandle4.set(new FileInputStream(dataFileDir + filenames[3]));
   * InputStreamHandle contentHandle5 = new InputStreamHandle();
   * contentHandle5.set(new FileInputStream(dataFileDir + filenames[4]));
   * 
   * StringBuilder writebatchResults = new StringBuilder();
   * batcher.add("/batcher-contraints1.json", contentHandle1);
   * batcher.add("/batcher-contraints2.json", contentHandle2);
   * batcher.add("/batcher-contraints3.json", contentHandle3);
   * batcher.add("/batcher-contraints4.json", contentHandle4);
   * batcher.add("/batcher-contraints5.json", contentHandle5);
   * 
   * // Flush batcher.flushAndWait();
   * 
   * StringBuffer querybatchResults = new StringBuffer(); StringBuilder
   * querybatchFailResults = new StringBuilder();
   * 
   * // get the query File file = new File(dataConfigDirPath + "qbe1.json");
   * FileHandle fileHandle = new FileHandle(file);
   * 
   * QueryManager queryMgr = client.newQueryManager();
   * RawQueryByExampleDefinition qbyexDef =
   * queryMgr.newRawQueryByExampleDefinition
   * (fileHandle.withFormat(Format.JSON));
   * 
   * // Run a QueryBatcher. QueryBatcher queryBatcher1 =
   * dmManager.newQueryBatcher(qbyexDef); queryBatcher1.onUrisReady(batch-> {
   * 
   * for (String str : batch.getItems()) { querybatchResults.append(str)
   * .append('|'); }
   * 
   * querybatchResults.append(batch.getForestResultsSoFar()) .append('|')
   * .append(batch.getForest().getForestName()) .append('|')
   * .append(batch.getJobBatchNumber()) .append('|');
   * 
   * }); queryBatcher1.onQueryFailure(throwable-> {
   * System.out.println("Exceptions thrown from callback onQueryFailure");
   * throwable.printStackTrace();
   * querybatchFailResults.append("Test has Exceptions");
   * querybatchFailResults.append(throwable.getMessage()); } );
   * 
   * // Trying to use a WriteBatcher on the same docId. WriteBatcher batcherTwo
   * = dmManager.newWriteBatcher(); String jsonDoc = "{" + "\"employees\": [" +
   * "{ \"firstName\":\"John\" , \"lastName\":\"Doe\" }," +
   * "{ \"firstName\":\"Ann\" , \"lastName\":\"Smith\" }," +
   * "{ \"firstName\":\"Bob\" , \"lastName\":\"Foo\" }]" + "}"; StringHandle
   * handle = new StringHandle(); handle.set(jsonDoc);
   * 
   * // Update contents to same doc uri. batcherTwo.withBatchSize(1);
   * batcherTwo.add("/batcher-contraints11.json", handle);
   * batcherTwo.flushAndWait();
   * 
   * JobTicket jobTicketWriteTwo = dmManager.startJob(batcherTwo);
   * 
   * JobTicket jobTicket = dmManager.startJob(queryBatcher1);
   * queryBatcher1.awaitTermination(1, TimeUnit.MINUTES);
   * 
   * if (queryBatcher1.isStopped()) {
   * 
   * if( !querybatchFailResults.toString().isEmpty() &&
   * querybatchFailResults.toString().contains("Exceptions")) {
   * System.out.println("Query Batch Failed - Buffer Contents are:" +
   * querybatchFailResults.toString()); fail("Test failed due to exceptions"); }
   * if( querybatchResults != null && !querybatchResults.toString().isEmpty()) {
   * // Verify the batch results now. String[] res =
   * querybatchResults.toString().split("\\|");
   * 
   * assertTrue("URI returned not correct",
   * res[0].contains("/batcher-contraints1.json"));
   * assertEquals("Bytes Moved","0", res[1]); assertEquals("Batch Number","0",
   * res[3]); } } }
   */

  @Test
  public void testQueryBatcherJobDetails() throws Exception
  {
    String testMultipleDB = "QHBJobDetaitDB";
    String[] testMultipleForest = { "QHBJobDetaitDB-1" };
    DatabaseClient clientTmp = null;
    DataMovementManager dmManagerTmp = null;

    try {
      System.out.println("Running testQueryBatcherJobDetails");

      // Setup a separate database/
      createDB(testMultipleDB);
      createForest(testMultipleForest[0], testMultipleDB);
      associateRESTServerWithDB(restServerName, testMultipleDB);

      setupAppServicesConstraint(testMultipleDB);
      addRangeElementAttributeIndex(dbName, "decimal", "http://cloudbank.com", "price", "", "amt", "http://marklogic.com/collation/");
      Thread.sleep(10000);

      String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
      String queryOptionName = "absRangeConstraintWithVariousGrammarAndWordQueryOpt.xml";
      clientTmp = getDatabaseClient("eval-user", "x", getConnType());
      dmManagerTmp = clientTmp.newDataMovementManager();

      setQueryOption(clientTmp, queryOptionName);

      QueryManager queryMgr = clientTmp.newQueryManager();

      StringQueryDefinition querydef = queryMgr.newStringDefinition();
      querydef.setCriteria("0012");

      // Use WriteBatcher to write the same files.
      WriteBatcher batcher = dmManagerTmp.newWriteBatcher();

      batcher.withBatchSize(2);
      // Move to individual data sub folders.
      String dataFileDir = dataConfigDirPath + "/data/";
      InputStreamHandle contentHandle1 = new InputStreamHandle();
      contentHandle1.set(new FileInputStream(dataFileDir + filenames[0]));
      InputStreamHandle contentHandle2 = new InputStreamHandle();
      contentHandle2.set(new FileInputStream(dataFileDir + filenames[1]));
      InputStreamHandle contentHandle3 = new InputStreamHandle();
      contentHandle3.set(new FileInputStream(dataFileDir + filenames[2]));
      InputStreamHandle contentHandle4 = new InputStreamHandle();
      contentHandle4.set(new FileInputStream(dataFileDir + filenames[3]));
      InputStreamHandle contentHandle5 = new InputStreamHandle();
      contentHandle5.set(new FileInputStream(dataFileDir + filenames[4]));

      batcher.add("/abs-range-constraint/batcher-contraints1.xml", contentHandle1);
      batcher.add("/abs-range-constraint/batcher-contraints2.xml", contentHandle2);
      batcher.add("/abs-range-constraint/batcher-contraints3.xml", contentHandle3);
      batcher.add("/abs-range-constraint/batcher-contraints4.xml", contentHandle4);
      batcher.add("/abs-range-constraint/batcher-contraints5.xml", contentHandle5);

      // Flush
      batcher.flushAndWait();

      StringBuilder batchResults = new StringBuilder();
      StringBuilder batchDetails = new StringBuilder();
      StringBuilder forestDetails = new StringBuilder();
      StringBuilder jobDetails = new StringBuilder();
      StringBuilder batchFailResults = new StringBuilder();

      // Run a QueryBatcher on the new URIs.
      QueryBatcher queryBatcher1 = dmManagerTmp.newQueryBatcher(querydef);

      queryBatcher1.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchResults.append(str)
              .append('|');
          // Batch details
          batchDetails.append(batch.getJobBatchNumber())
              .append('|')
              .append(batch.getJobResultsSoFar())
              .append('|')
              .append(batch.getForestBatchNumber());

          // Get the Forest details
          Forest forest = batch.getForest();
          forestDetails.append(forest.getDatabaseName())
              .append('|')
              .append(forest.getHost())
              .append('|')
              .append(forest.getForestName());

          // Get the Job details
          /*
           * jobDetails.append(batch.getJobTicket().getJobId()) .append('|')
           * .append(batch.getJobTicket().getJobType().name());
           */
        }
      });
      queryBatcher1.onQueryFailure(throwable -> {
        System.out.println("Exceptions thrown from callback onQueryFailure");
        throwable.printStackTrace();
        batchFailResults.append("Test has Exceptions");
      });

      JobTicket jobTicket = dmManagerTmp.startJob(queryBatcher1);
      String jobId = jobTicket.getJobId();
      String jobName = jobTicket.getJobType().name();

      boolean bJobFinished = queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);

      if (queryBatcher1.isStopped()) {

        if (!batchFailResults.toString().isEmpty() && batchFailResults.toString().contains("Exceptions")) {
          fail("Test failed due to exceptions");
        }

        // Verify the batch results now.
        String[] res = batchResults.toString().split("\\|");
        assertEquals("Number of reults returned is incorrect", 1, res.length);
        assertTrue("URI returned not correct", res[0].contains("/abs-range-constraint/batcher-contraints2.xml"));

        // verify the Job and batch get method values.
        String[] batchDetailsArray = batchDetails.toString().split("\\|");

        assertTrue("Job Batch Number not correct", Long.parseLong(batchDetailsArray[0]) > 0);
        assertTrue("Job Results So Far Number not correct", Long.parseLong(batchDetailsArray[1]) > 0);
        assertTrue("Forest Batch Number not correct", Long.parseLong(batchDetailsArray[2]) > 0);
        // Git Isue 124. For bytesMoved.
        assertTrue("Job Bytes Moved not correct", Long.parseLong(batchDetailsArray[3]) == 0);

        // verify the forest get method values.
        String[] forestDetailsArray = forestDetails.toString().split("\\|");
        assertTrue("Database name returned from batch is not correct", forestDetailsArray[0].equalsIgnoreCase(dbName));
        assertTrue("Forest name returned from batch is not correct", forestDetailsArray[2].equalsIgnoreCase(testMultipleForest[0]) ||
            forestDetailsArray[2].equalsIgnoreCase(testMultipleForest[1]));
        // verify the job ticket get method values. This needs to be
        // implemented.
        /*
         * String[] jobTicketArray = jobDetails.toString().split("\\|");
         * assertTrue("Job Id returned from batch is not correct",
         * jobTicketArray[0].equalsIgnoreCase(jobId));
         * assertTrue("Job Type name returned from batch is not correct",
         * forestDetailsArray[2].equalsIgnoreCase(jobName));
         */
      }
    } catch (Exception e) {
      System.out.println("Exceptions thrown from Test testAndWordQueryWithMultipleForests");
      System.out.println(e.getMessage());
    } finally {
      // Associate back the original DB.
      associateRESTServerWithDB(restServerName, dbName);
      detachForest(testMultipleDB, testMultipleForest[0]);
      deleteDB(testMultipleDB);

      deleteForest(testMultipleForest[0]);
      Thread.sleep(10000);
      clientTmp.release();
    }
  }

  /*
   * These are test methods that verify that different query types work. Testing
   * - Word query range query value query
   */
  @Test
  public void testDifferentQueryTypes() throws Exception
  {
    String testMultipleDB = "QBtestDifferentQueryTypesDB";
    String[] testMultipleForest = { "QBtestDifferentQueryTypesDB-1" };
    DatabaseClient clientTmp = null;
    DataMovementManager dmManagerTmp = null;

    try {
      System.out.println("Running testDifferentQueryTypes");

      // Setup a separate database/
      createDB(testMultipleDB);
      createForest(testMultipleForest[0], testMultipleDB);
      associateRESTServerWithDB(restServerName, testMultipleDB);

      // Setup constraints on DB and wait for indexes to setup.
      setupAppServicesConstraint(testMultipleDB);
      Thread.sleep(10000);

      String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
      ServerConfigurationManager srvMgr = client.newServerConfigManager();
      srvMgr.readConfiguration();
      srvMgr.setQueryOptionValidation(true);
      srvMgr.writeConfiguration();

      clientTmp = getDatabaseClient("eval-user", "x", getConnType());
      dmManagerTmp = clientTmp.newDataMovementManager();

      QueryManager queryMgr = clientTmp.newQueryManager();

      // create query def
      StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
      StructuredQueryDefinition queryWorddef = qb.word(qb.element("id"), "0026");

      // Use WriteBatcher to write the some files.
      WriteBatcher batcher = dmManagerTmp.newWriteBatcher();

      batcher.withBatchSize(2);
      // Move to individual data sub folders.
      String dataFileDir = dataConfigDirPath + "/data/";
      InputStreamHandle contentHandle1 = new InputStreamHandle();
      contentHandle1.set(new FileInputStream(dataFileDir + filenames[0]));
      InputStreamHandle contentHandle2 = new InputStreamHandle();
      contentHandle2.set(new FileInputStream(dataFileDir + filenames[1]));
      InputStreamHandle contentHandle3 = new InputStreamHandle();
      contentHandle3.set(new FileInputStream(dataFileDir + filenames[2]));
      InputStreamHandle contentHandle4 = new InputStreamHandle();
      contentHandle4.set(new FileInputStream(dataFileDir + filenames[3]));
      InputStreamHandle contentHandle5 = new InputStreamHandle();
      contentHandle5.set(new FileInputStream(dataFileDir + filenames[4]));

      batcher.add("/abs-range-constraint/batcher-contraints1.xml", contentHandle1);
      batcher.add("/abs-range-constraint/batcher-contraints2.xml", contentHandle2);
      batcher.add("/abs-range-constraint/batcher-contraints3.xml", contentHandle3);
      batcher.add("/abs-range-constraint/batcher-contraints4.xml", contentHandle4);
      batcher.add("/abs-range-constraint/batcher-contraints5.xml", contentHandle5);

      // Flush
      batcher.flushAndWait();
      // batcher.awaitCompletion();

      StringBuilder batchWordResults = new StringBuilder();
      StringBuilder batchWordFailResults = new StringBuilder();

      // Run a QueryBatcher on the new URIs.
      QueryBatcher queryBatcher1 = dmManagerTmp.newQueryBatcher(queryWorddef);

      queryBatcher1.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchWordResults.append(str)
              .append('|');
        }
      });
      queryBatcher1.onQueryFailure(throwable -> {
        System.out.println("Exceptions thrown from callback onQueryFailure in testDifferentQueryTypes");
        throwable.printStackTrace();
        batchWordFailResults.append("Test has Exceptions");
      });
      JobTicket jobTicket = dmManagerTmp.startJob(queryBatcher1);
      boolean bJobFinished = queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);
      while (!queryBatcher1.isStopped()) {
        // do nothing.
      }
      if (queryBatcher1.isStopped()) {

        if (!batchWordFailResults.toString().isEmpty() && batchWordFailResults.toString().contains("Exceptions")) {
          fail("Test failed due to exceptions in testDifferentQueryTypes - Word Query");
        }

        // Verify the batch results now.
        String[] res = batchWordResults.toString().split("\\|");
        assertEquals("Number of reults returned is incorrect", 1, res.length);
        assertTrue("URI returned not correct", res[0].contains("/abs-range-constraint/batcher-contraints5.xml"));
      }

      // Run a range query.

      StructuredQueryDefinition queryRangedef = qb.range(qb.element("popularity"), "xs:integer", Operator.GE, 4);
      QueryBatcher queryBatcher2 = dmManagerTmp.newQueryBatcher(queryRangedef);
      // StringBuilder batchRangeResults = new StringBuilder();
      List<String> batchRangeResults = new ArrayList<String>();
      StringBuilder batchRangeFailResults = new StringBuilder();

      queryBatcher2.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchRangeResults.add(str);
          // batchRangeResults.append(str)
          // .append('|');
        }
      });
      queryBatcher2.onQueryFailure(throwable -> {
        System.out.println("Exceptions thrown from callback onQueryFailure in testDifferentQueryTypes");
        throwable.printStackTrace();
        batchRangeFailResults.append("Test has Exceptions");
      });
      jobTicket = dmManagerTmp.startJob(queryBatcher2);
      bJobFinished = queryBatcher2.awaitCompletion(3, TimeUnit.MINUTES);
      while (!queryBatcher2.isStopped()) {
        // do nothing.
      }

      if (queryBatcher2.isStopped()) {

        if (!batchRangeFailResults.toString().isEmpty() && batchRangeFailResults.toString().contains("Exceptions")) {
          fail("Test failed due to exceptions in testDifferentQueryTypes - Word Query");
        }

        // Verify the batch results now.
        assertTrue("No of documents returned in range query not correct", batchRangeResults.size() == 4);
        assertTrue("URI returned not correct", batchRangeResults.contains("/abs-range-constraint/batcher-contraints1.xml"));
        assertTrue("URI returned not correct", batchRangeResults.contains("/abs-range-constraint/batcher-contraints2.xml"));
        assertTrue("URI returned not correct", batchRangeResults.contains("/abs-range-constraint/batcher-contraints4.xml"));
        assertTrue("URI returned not correct", batchRangeResults.contains("/abs-range-constraint/batcher-contraints5.xml"));
      }

      // Run a ValueQueryOnAttribute query.

      StructuredQueryDefinition valuequeyDef = qb.value(qb.elementAttribute(qb.element(new QName("http://cloudbank.com", "price")), qb.attribute("amt")), "0.1");
      QueryBatcher queryBatcher3 = dmManagerTmp.newQueryBatcher(valuequeyDef);
      List<String> batchValueResults = new ArrayList<String>();
      StringBuilder batchvalueFailResults = new StringBuilder();

      queryBatcher3.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchValueResults.add(str);
        }
      });
      queryBatcher3.onQueryFailure(throwable -> {
        System.out.println("Exceptions thrown from callback onQueryFailure in testDifferentQueryTypes");
        throwable.printStackTrace();
        batchvalueFailResults.append("Test has Exceptions");
      });
      jobTicket = dmManagerTmp.startJob(queryBatcher3);
      bJobFinished = queryBatcher3.awaitCompletion(3, TimeUnit.MINUTES);
      while (!queryBatcher3.isStopped()) {
        // do nothing.
      }

      if (queryBatcher3.isStopped()) {

        if (!batchvalueFailResults.toString().isEmpty() && batchvalueFailResults.toString().contains("Exceptions")) {
          fail("Test failed due to exceptions in testDifferentQueryTypes - Word Query");
        }

        // Verify the batch results now.
        assertTrue("No of documents returned in range query not correct", batchValueResults.size() == 1);
        assertTrue("URI returned not correct", batchRangeResults.contains("/abs-range-constraint/batcher-contraints1.xml"));
      }
    } catch (Exception e) {
      System.out.println("Exceptions thrown from Test testDifferentQueryTypes");
      System.out.println(e.getMessage());
    } finally {
      // Associate back the original DB.
      associateRESTServerWithDB(restServerName, dbName);
      detachForest(testMultipleDB, testMultipleForest[0]);
      deleteDB(testMultipleDB);
      deleteForest(testMultipleForest[0]);
      clientTmp.release();
      Thread.sleep(10000);
    }
  }
  
  @Test
  public void testMinHostWithHostAvailabilityListener() throws Exception
  {
	  DatabaseClient client = null;
	  DataMovementManager dmManagerTmp = null;
	  StringBuilder batchFailResults = new StringBuilder();

	  if(!isLBHost()) {
		  try {
			  System.out.println("Running testMinHostWithHostAvailabilityListener");
			  String[] hostNames = null;
			  client = getDatabaseClient("eval-user", "x", getConnType());
			  dmManagerTmp = client.newDataMovementManager();

			  QueryManager queryMgr = client.newQueryManager();
			  StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
			  StructuredQueryDefinition queryWorddef = qb.word(qb.element("id"), "0026");

			  hostNames = getHosts();
			  StructuredQueryDefinition queryRangedef = qb.range(qb.element("popularity"), "xs:integer", Operator.GE, 4);
			  QueryBatcher queryBatcher = dmManagerTmp.newQueryBatcher(queryRangedef);

			  queryBatcher.setQueryFailureListeners(
					  new HostAvailabilityListener(dmManagerTmp)
					  .withSuspendTimeForHostUnavailable(Duration.ofSeconds(15))
					  .withMinHosts(5)
					  );
			  queryBatcher.onUrisReady(batch -> {
				  System.out.println("Batch results are: " + batch.getJobResultsSoFar());
			  }
					  );
			  queryBatcher.onQueryFailure(throwable -> {
				  System.out.println("Exceptions thrown from callback onQueryFailure in testMinHostWithHostAvailabilityListener");
				  throwable.printStackTrace();

			  });
			  JobTicket jobTicket = dmManagerTmp.startJob(queryBatcher);
		  }
		  catch(IllegalArgumentException ex) {
			  batchFailResults.append(ex.getMessage());
		  }
		  finally {
			  client.release();
			  System.out.println("Exception Message is " + batchFailResults.toString());
			  assertTrue("Exception incorrect", batchFailResults.toString().contains("numHosts must be less than or equal to the number of hosts in the cluster"));
		  }
	  }
  }
  
  @Test
  public void testProgressListener() throws Exception {
	  DatabaseClient clientTmp = null;
	  try {
		  DataMovementManager dmManager = null;

		  clientTmp = getDatabaseClient("eval-user", "x", getConnType());
		  dmManager = clientTmp.newDataMovementManager();
		  QueryManager queryMgr = clientTmp.newQueryManager();
		  StringQueryDefinition querydef = queryMgr.newStringDefinition();
		  querydef.setCriteria("John AND Bob");

		  String jsonDoc = "{" + "\"employees\": [" + "{ \"firstName\":\"John\" , \"lastName\":\"Doe\" },"
				  + "{ \"firstName\":\"Ann\" , \"lastName\":\"Smith\" },"
				  + "{ \"firstName\":\"Bob\" , \"lastName\":\"Foo\" }]" + "}";
		  WriteBatcher wbatcher = dmManager.newWriteBatcher();
		  wbatcher.withBatchSize(600);
		  wbatcher.onBatchFailure((batch, throwable) -> throwable.printStackTrace());
		  StringHandle handle = new StringHandle();
		  handle.set(jsonDoc);
		  String uri = null;

		  // Insert 6 K documents
		  for (int i = 0; i < 6000; i++) {
			  uri = "/firstName" + i + ".json";
			  wbatcher.add(uri, handle);
		  }

		  wbatcher.flushAndWait();
		  // Read all 6000 docs in a batch and monitor progress.
		  StringBuilder str6000 = new StringBuilder();
		  QueryBatcher batcher6000 = dmManager.newQueryBatcher(querydef).withBatchSize(6000).withThreadCount(1);
		  batcher6000.onUrisReady(

				  new ProgressListener()
				  .onProgressUpdate(progressUpdate -> {
					  System.out.println("From ProgressListener (Batch 6000): " + progressUpdate.getProgressAsString());
					  str6000.append(progressUpdate.getProgressAsString());
				  }));
		  batcher6000.onQueryFailure((throwable) -> {
			  System.out.println("queryFailures 6000: ");
			  try {
				  Thread.currentThread().sleep(7000L);
			  } catch (Exception e) {
				  // TODO Auto-generated catch block
				  e.printStackTrace();
			  }
		  });

		  JobTicket queryTicket6000 = dmManager.startJob(batcher6000);
		  batcher6000.awaitCompletion();
		  System.out.println("From buffer 6000: " + str6000.toString());
		  assertTrue("Progress Update incorrect", str6000.toString().contains("Progress: 6000 results"));

		  // Read in smaller batches and monitor progress
		  StringBuilder str60 = new StringBuilder();
		  QueryBatcher batcher60 = dmManager.newQueryBatcher(querydef).withBatchSize(60).withThreadCount(1);
		  batcher60.onUrisReady(
				  new ProgressListener()
				  .onProgressUpdate(progressUpdate -> {
					  System.out.println("From ProgressListener (From Batch 60): " + progressUpdate.getProgressAsString());
					  str60.append(progressUpdate.getProgressAsString());
				  }));
		  batcher60.onQueryFailure((throwable) -> {
			  System.out.println("queryFailures 60: ");
			  try {
				  Thread.currentThread().sleep(7000L);
			  } catch (Exception e) {
				  // TODO Auto-generated catch block
				  e.printStackTrace();
			  }
		  });

		  JobTicket queryTicket60 = dmManager.startJob(batcher60);
		  batcher60.awaitCompletion();

		  System.out.println("From buffer 60: " + str60.toString());
		  // Make sure all updates are available
		  assertTrue("Progress Update Batch 1 incorrect", str60.toString().contains("Progress: 60 results"));
		  assertTrue("Progress Update Batch 5940 incorrect", str60.toString().contains("Progress: 5940 results"));
		  assertTrue("Progress Update incorrect", str60.toString().contains("Progress: 6000 results"));
		  // Batches read are uneven and with multiple threads
		  StringBuilder str33 = new StringBuilder();
		  QueryBatcher batcher33 = dmManager.newQueryBatcher(querydef).withBatchSize(33).withThreadCount(3);
		  batcher33.onUrisReady(
				  new ProgressListener()
				  .onProgressUpdate(progressUpdate -> {
					  System.out.println("From ProgressListener (From Batch 33): " + progressUpdate.getProgressAsString());
					  str33.append(progressUpdate.getProgressAsString());
				  }));
		  batcher33.onQueryFailure((throwable) -> {
			  System.out.println("queryFailures 33: ");
			  try {
				  Thread.currentThread().sleep(7000L);
			  } catch (Exception e) {
				  // TODO Auto-generated catch block
				  e.printStackTrace();
			  }
		  });

		  JobTicket queryTicket33 = dmManager.startJob(batcher33);
		  batcher33.awaitCompletion();

		  System.out.println("From buffer 33: " + str33.toString());
		  // Make sure all updates are available
		  assertTrue("Progress Update Batch 1 incorrect", str33.toString().contains("Progress: 33 results"));
		  assertTrue("Progress Update Batch 5973 incorrect", str33.toString().contains("Progress: 5973 results"));
		  assertTrue("Progress Update incorrect", str33.toString().contains("Progress: 6000 results"));

		  // Batches read errors
		  StringBuilder strErr = new StringBuilder();	  
		  StringQueryDefinition querydefErr = queryMgr.newStringDefinition();
		  querydefErr.setCriteria("Jhn AND BAA");

		  QueryBatcher batcherErr = dmManager.newQueryBatcher(querydefErr).withBatchSize(100).withThreadCount(3);
		  batcherErr.onUrisReady(
				  new ProgressListener()
				  .onProgressUpdate(progressUpdate -> {
					  System.out.println("From ProgressListener (From Batch Err): " + progressUpdate.getProgressAsString());
					  strErr.append(progressUpdate.getProgressAsString());	  
				  }))
		  .onQueryFailure((throwable) -> {
			  System.out.println("queryFailures Err: ");
			  try {
				  Thread.currentThread().sleep(7000L);
			  } catch (Exception e) {
				  // TODO Auto-generated catch block
				  e.printStackTrace();
			  }
		  });

		  JobTicket queryTicketErr = dmManager.startJob(batcherErr);
		  batcherErr.awaitCompletion();

		  System.out.println("From buffer Err: " + strErr.toString());
		  // No updates are available
		  assertTrue("Progress Update Batch 1 incorrect", strErr.toString().isEmpty());	  
	  }
	  catch (Exception ex) {
		  ex.printStackTrace();
	  }
	  finally {
		  clientTmp.release();
	  }
  }
}
