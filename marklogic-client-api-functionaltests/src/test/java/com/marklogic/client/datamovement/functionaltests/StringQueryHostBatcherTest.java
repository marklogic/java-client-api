/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement.functionaltests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import com.marklogic.client.expression.CtsQueryBuilder;
import com.marklogic.client.query.*;
import com.marklogic.client.type.CtsQueryExpr;
import com.marklogic.client.util.EditableNamespaceContext;
import org.junit.jupiter.api.*;
import org.w3c.dom.DOMException;
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
import com.marklogic.client.query.StructuredQueryBuilder.Operator;

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
  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    loadGradleProperties();
    restServerPort = getRestAppServerPort();

    restServerName = getRestAppServerName();
    // Points to top level of all QA data folder
    dataConfigDirPath = getDataConfigDirPath();

    setupJavaRESTServer(dbName, fNames[0], restServerName, restServerPort);
    setupAppServicesConstraint(dbName);
    String[][] namespacePaths = {
            // { prefix, namespace-uri }
            // If there is a need to add additional fields, then add them to
            // the end
            // of each array
            // and pass empty strings ("") into an array where the
            // additional field
            // does not have a value.
            // For example : as in namespace, collections below.
            { "ns1", "http://www.example1.com" },
            { "ns2", "http://www.example2.com" },
            { "nsdate", "http://purl.org/dc/elements/1.1/" }
            // Add new namespace Paths as an array below.
    };
    // Insert the namespaces path
    addPathNamespace(dbName, namespacePaths);

    // Add additional range path indices with namespaces.
    String[][] rangePaths = {
            {"int","/ns1:root/ns1:popularity","","ignore","false"},
            {"string","/ns2:root/ns2:status","http://marklogic.com/collation/","ignore","false"},
            {"date","//nsdate:date","","ignore","false"}
    };
    addRangePathIndex(dbName, rangePaths);

    createUserRolesWithPrevilages("test-eval", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createRESTUser("eval-user", "x", "test-eval", "rest-admin", "rest-writer", "rest-reader", "rest-extension-user", "manage-user");
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterAll
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

  @BeforeEach
  public void setUp() throws Exception  {
    System.out.println("In setup");
    client = getDatabaseClient("eval-user", "x", getConnType());
    dmManager = client.newDataMovementManager();
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterEach
  public void tearDown() throws Exception {
    System.out.println("In tearDown");
    client.release();
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
      assertTrue( contents.contains("Vannevar served"));
      assertTrue( contents.contains("12.34"));

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
      StringBuilder batchIllegalState = new StringBuilder();

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

      dmManager.startJob(queryBatcher1);
      boolean bJobFinished = queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);

      if (bJobFinished) {
        // Verify the batch results now.
        String[] res = batchResults.toString().split("\\|");

        assertTrue( res[0].contains("/abs-range-constraint/batcher-contraints4.xml"));
        // Verify Forest Name.
        assertTrue( forestResults.toString().contains(fNames[0]));
      }
      else {
    	  fail("testAndWordQuery test failed");
      }

      try {
        // Verify Git # 1290
        QueryBatcher qb = dmManager.newQueryBatcher(client.newQueryManager().newStringDefinition().withCriteria("")).withBatchSize(10);

        qb.onQueryFailure(throwable -> {
          throwable.printStackTrace();
          batchIllegalState.append(throwable.getMessage());
        });
        dmManager.startJob(qb);
        qb.awaitCompletion();
      } catch (Exception ex) {
        batchIllegalState.append(ex.getMessage());
        System.out.println("Exceptions buffer from empty withCriteria : " + batchIllegalState.toString());
        assertTrue( batchIllegalState.toString().contains("Criteria cannot be an empty string"));
      }
      } catch (Exception e) {
      System.out.print(e.getMessage());
    } finally {
    	clearDB();
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
    DatabaseClient clientTmp = null;
    DataMovementManager dmManagerTmp = null;

    try {
      System.out.println("Running testRawCombinedQueryXMLWithWriteOptions");

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

      dmManagerTmp.startJob(queryBatcher1);
      boolean bJobFinished = queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);

      if (bJobFinished) {

        if (!batchFailResults.toString().isEmpty() && batchFailResults.toString().contains("Exceptions")) {
          fail("Test failed due to exceptions");
        }

        // Verify the batch results now.
        String[] res = batchResults.toString().split("\\|");
        assertEquals(1, res.length);
        assertTrue( res[0].contains(filenames[4]));

        // Read the document and assert on the value
        DOMHandle contentHandle = new DOMHandle();
        contentHandle = readDocumentUsingDOMHandle(clientTmp, filenames[4], "XML");
        Document readDoc = contentHandle.get();
        System.out.println(convertXMLDocumentToString(readDoc));

        assertTrue( readDoc.getElementsByTagName("id").item(0).getTextContent().contains("0026"));
        assertTrue( readDoc.getElementsByTagName("title").item(0).getTextContent().contains("The memex"));
        assertTrue( readDoc.getElementsByTagName("date").item(0).getTextContent().contains("2009-05-05"));
      }
    } catch (Exception e) {
      System.out.println("Exceptions thrown from testRawCombinedQueryXMLWithWriteOptions");
      System.out.println(e.getMessage());
      fail("testRawCombinedQueryXMLWithWriteOptions method failed");
    } finally {
      clientTmp.release();
      clearDB();
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
    DatabaseClient clientTmp = null;
    DataMovementManager dmManagerTmp = null;

    try {
      System.out.println("Running testRawCombinedQueryJSON");

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

      wbatcher.flushAndWait();

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

      dmManagerTmp.startJob(queryBatcher1);
      boolean bJobFinished = queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);

      if (bJobFinished) {

        if (!batchFailResults.toString().isEmpty() && batchFailResults.toString().contains("Exceptions")) {
          fail("Test failed due to exceptions");
        }

        // Verify the batch results now.
        String[] res = batchResults.toString().split("\\|");
        assertEquals(1, res.length);
        assertTrue( res[0].contains(filenames[4]));

        // Read the document and assert on the value
        DOMHandle contentHandle = new DOMHandle();
        contentHandle = readDocumentUsingDOMHandle(clientTmp, filenames[4], "XML");
        Document readDoc = contentHandle.get();
        System.out.println(convertXMLDocumentToString(readDoc));

        assertTrue( readDoc.getElementsByTagName("id").item(0).getTextContent().contains("0026"));
        assertTrue( readDoc.getElementsByTagName("title").item(0).getTextContent().contains("The memex"));
        assertTrue( readDoc.getElementsByTagName("date").item(0).getTextContent().contains("2009-05-05"));
      }
    } catch (Exception e) {
      System.out.println("Exceptions thrown from testRawCombinedQueryJSONWithWriteOptions");
      System.out.println(e.getMessage());
      fail("testRawCombinedQueryJSON mathos failed");
    } finally {
      clientTmp.release();
      clearDB();
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
    DatabaseClient clientTmp = null;
    DataMovementManager dmManagerTmp = null;

    try {
      System.out.println("Running testRawCombinedQueryPathIndex");

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

      wbatcher.flushAndWait();
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
    	  try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

      dmManagerTmp.startJob(queryBatcher1);
      boolean bJobFinished = queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);

      if (bJobFinished) {
        if (!batchFailResults.toString().isEmpty() && batchFailResults.toString().contains("Exceptions")) {
          fail("Test failed due to exceptions");
        }

        // Verify the batch results now.
        String[] res = batchResults.toString().split("\\|");
        assertEquals(2, res.length);
        assertTrue( res[0].contains("pathindex1.xml") ? true : (res[1].contains("pathindex1.xml") ? true : false));
        assertTrue( res[0].contains("pathindex2.xml") ? true : (res[1].contains("pathindex2.xml") ? true : false));
      }
    } catch (Exception e) {
      System.out.println("Exceptions thrown from testRawCombinedQueryPathIndex");
      System.out.println(e.getMessage());
      fail("testRawCombinedQueryPathIndex method failed");
    } finally {
      clientTmp.release();
      clearDB();
    }
  }

  @Test
  public void testRawCtsQuery() throws IOException, InterruptedException {
    System.out.println("Running testRawCtsQuery");

    Document readDoc = null;
    try {
      String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
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

      dmManager.startJob(queryBatcher1);
      queryBatcher1.awaitCompletion(1, TimeUnit.MINUTES);

      System.out.println("Batch Results are : " + batchResults.toString());
      System.out.println("File name is : " + filenames[4]);
      assertTrue( batchResults.toString().contains("cts-" + filenames[4]));

      // Read the document and assert on the value
      DOMHandle contentHandle = new DOMHandle();
      contentHandle = readDocumentUsingDOMHandle(client, "cts-" + filenames[4], "XML");
      readDoc = contentHandle.get();
      try {
        System.out.println(convertXMLDocumentToString(readDoc));
      } catch (TransformerException e) {
        e.printStackTrace();
      }

      assertTrue( readDoc.getElementsByTagName("id").item(0).getTextContent().contains("0026"));
      assertTrue( readDoc.getElementsByTagName("title").item(0).getTextContent().contains("The memex"));

      assertTrue( readDoc.getElementsByTagName("date").item(0).getTextContent().contains("2009-05-05"));

      // Run a QueryBatcher with CtsQueryBuilder.
      CtsQueryBuilder ctsQueryBuilder = queryMgr.newCtsSearchBuilder();
      StringBuilder batchResults2 = new StringBuilder();

      CtsQueryExpr ctsQueryExpr = ctsQueryBuilder.cts.andQuery(ctsQueryBuilder.cts.wordQuery("unfortunately"));
      CtsQueryDefinition qd = ctsQueryBuilder.newCtsQueryDefinition(ctsQueryExpr);
      QueryBatcher queryBatcher2 = dmManager.newQueryBatcher(qd);

      queryBatcher2.onUrisReady(batch -> {

        for (String str : batch.getItems()) {
          batchResults2.append(str).append('|');
        }

        batchResults2.append(batch.getJobResultsSoFar())
                .append('|')
                .append(batch.getForest().getForestName())
                .append('|')
                .append(batch.getJobBatchNumber())
                .append('|');

      })
              .onQueryFailure(throwable -> {
                System.out.println("Exceptions thrown from callback onQueryFailure" + throwable.getMessage());
              });

      dmManager.startJob(queryBatcher2);
      queryBatcher2.awaitCompletion(1, TimeUnit.MINUTES);

      System.out.println("Batch Results are : " + batchResults2.toString());
      System.out.println("File name is : " + filenames[4]);
      assertTrue( batchResults2.toString().contains("cts-" + filenames[4]));

      // Read the document and assert on the value
      Document readDoc2 = null;
      DOMHandle contentHandleCts = new DOMHandle();
      contentHandleCts = readDocumentUsingDOMHandle(client, "cts-" + filenames[4], "XML");
      readDoc2 = contentHandleCts.get();
      try {
        System.out.println(convertXMLDocumentToString(readDoc2));
      } catch (TransformerException e) {
        e.printStackTrace();
      }

      assertTrue( readDoc2.getElementsByTagName("id").item(0).getTextContent().contains("0026"));
      assertTrue( readDoc2.getElementsByTagName("title").item(0).getTextContent().contains("The memex"));

      assertTrue( readDoc2.getElementsByTagName("date").item(0).getTextContent().contains("2009-05-05"));
    } catch (DOMException e) {
      e.printStackTrace();
      fail("testRawCtsQuery method failed");
    }
	finally {
		try {
			clearDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
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
  @Disabled
  public void testQueryBatcherQueryFailures() throws IOException, InterruptedException
  {
    System.out.println("Running testQueryBatcherQueryFailures");

    try {
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
		      .append(forest.getForestName())
		      .append('|')
		      .append(forest.getHost())
		      .append('|')
		      .append(forest.getDatabaseName())
		      .append('|')
		      .append(forest.isUpdateable());
		});
		dmManager.startJob(queryBatcher1);
		boolean isStopped = queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);

		if (isStopped) {
		  if (!batchFailResults.toString().isEmpty() && batchFailResults.toString().contains("Exceptions")) {
		    // Write out and assert on query failures.
		    System.out.println("Exception Buffer contents on Query Exceptions received from callback onQueryFailure");
		    System.out.println(batchFailResults.toString());
		    // Remove this failure once there are no NPEs and doa asserts on various
		    // counters in failure scenario.
		    fail("Test failed due to exceptions");
		  }
		}
	} catch (Exception e) {
		e.printStackTrace();
		fail("testQueryBatcherQueryFailures method failed");
	}
    finally {
    	try {
			clearDB();
		} catch (Exception e) {
			e.printStackTrace();
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

    try {
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
		dmManager.startJob(queryBatcher1);
		boolean isStopped = queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);

		if (isStopped) {
		  if (!batchFailResults.toString().isEmpty() && batchFailResults.toString().contains("Exceptions")) {
		    // Write out and assert on query failures.
		    System.out.println("Exception Buffer contents on Query Exceptions received from callback onQueryFailure");
		    System.out.println(batchFailResults.toString());
		    fail("Test failed due to exceptions");
		  }
		  System.out.println("Contents from the callback are : " + ccBuf.toString());
		  // Verify the Callback contents.
		  assertTrue( ccBuf.toString().contains(expectedStr));
		}
		else {
			fail("testQueryBatcherCallbackClient method failed");
		}
	} catch (Exception e) {
		e.printStackTrace();
		fail("testQueryBatcherCallbackClient method failed");
	}
    finally {
		try {
			clearDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
    try {
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
		dmManager.startJob(queryBatcherNoResult);
		boolean isstopped = queryBatcherNoResult.awaitCompletion(30, TimeUnit.SECONDS);

		if (isstopped) {
		  assertTrue( batchNoResults.toString().isEmpty());
		}
		else {
			fail("testQueryBatcherWithNoData method failed");
		}
	} catch (Exception e) {
		e.printStackTrace();
		fail("testQueryBatcherWithNoData method failed");
	}
    finally {
    	try {
			clearDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
  }

  /*
   * To test query with WriteBatcher and QueryBatcher 1) Verify batch
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

    try {
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
		dmManager.startJob(queryBatcherbatchSize);
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

		  assertEquals(batchResults.toString().split("\\|").length, 50);
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
		dmManager.startJob(queryBatcherSmallTimeout);
		queryBatcherSmallTimeout.awaitCompletion(5, TimeUnit.MILLISECONDS);
		if (queryBatcherSmallTimeout.isStopped()) {
		  System.out.println(batchResults.toString());
		  assertNotEquals(batchResults.toString().split("\\|").length, 5);
		}
		if (batchFailResults != null && !batchFailResults.toString().isEmpty()) {
		  assertTrue( batchFailResults.toString().contains("Test has Exceptions"));
		}
	} catch (Exception e) {
		e.printStackTrace();
		fail("testQueryBatcherBatchSize method failed");
	}
    finally {
    	try {
			clearDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

    try {
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

		dmManager.startJob(queryBatcherAwait);
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
		    assertTrue( quertJobTimeoutValue >= 30 && quertJobTimeoutValue < 35);
		  } else if (quertJobTimeoutValue > 35) {
		    fail("Job termination with awaitTermination failed");
		  }
		}
	} catch (Exception e) {
		e.printStackTrace();
		fail("testQueryBatcherFailures method failed");
	}
    finally {
    	try {
			clearDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
  }

  @Test
  public void testServerXQueryTransform() throws IOException, ParserConfigurationException, SAXException, TransformerException, InterruptedException, XPathExpressionException
  {
    System.out.println("Running testServerXQueryTransform");
    try {
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
		handleFoo.setFormat(Format.XML);

		StringHandle handleBar = new StringHandle();
		handleBar.set(xmlStr2);
		handleBar.setFormat(Format.XML);
		String uri = null;
		dmManager.startJob(batcher);

		// Insert 10 documents
		for (int i = 0; i < 10; i++) {
		  uri = "foo" + i + ".xml";
		  batcher.addAs(uri, handleFoo);
		}

		for (int i = 0; i < 10; i++) {
		  uri = "bar" + i + ".xml";
		  batcher.addAs(uri, handleBar);
		}
		// Flush
		batcher.flushAndWait();
		dmManager.stopJob(batcher);

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
		dmManager.startJob(queryBatcher1);
		queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);
        while (!queryBatcher1.isStopped()) {
        // Do nothing. Wait for batcher to complete.
        }

		if (queryBatcher1.isStopped()) {
		  // Verify the batch results now.
		  String[] res = batchResults.toString().split("\\|");
		  assertEquals(res.length, 20);

		  // Get a random URI, since the URIs returned are not ordered. Get the 3rd
		  // URI.
		  assertTrue( res[2].contains("foo") || res[2].contains("bar"));

		  // do a lookup with the first URI using the client to verify transforms
		  // are done.
		  DOMHandle readHandle = readDocumentUsingDOMHandle(client, res[0], "XML");
		  String contents = readHandle.evaluateXPath("/foo/text()", String.class);
		  String attribute = readHandle.evaluateXPath("/foo/@Lang", String.class);
		  // Verify that the contents are of xmlStr1 or xmlStr2.

		  System.out.println("Contents are : " + contents);
		  System.out.println("Contents are : " + attribute);
		  assertTrue( xmlStr1.contains(contents) || xmlStr2.contains(contents));
		  assertTrue( attribute.equalsIgnoreCase("English"));
		}
		else {
			fail("testServerXQueryTransform method failed");
		}
	} catch (Exception e) {
		e.printStackTrace();
		fail("testServerXQueryTransform method failed");
	}
    finally {
    	try {
			clearDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

      dmManager.startJob(queryBatcherAddForest);
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
          assertTrue( batchResults.toString().contains("10"));
        }
        if (batchFailResults != null && !batchFailResults.toString().isEmpty()) {
          System.out.print("Results from onQueryFailure === ");
          System.out.print(batchFailResults.toString());
          assertTrue( batchFailResults.toString().contains("Test has Exceptions"));
        }
      }

      // Remove a forest.
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

      dmManager.startJob(queryBatcherRemoveForest);
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
          assertTrue( batchResultsRem.toString().contains("10"));
        }
        if (batchFailResultsRem != null && !batchFailResultsRem.toString().isEmpty()) {
          System.out.print("Results from onQueryFailure === ");
          System.out.print(batchFailResultsRem.toString());
          assertTrue( batchFailResultsRem.toString().contains("Test has Exceptions"));
        }
      }
    } catch (Exception e) {
      System.out.print(e.getMessage());
      fail("testQueryBatcherWithForestRemoveAndAdd method failed");
    }

    finally {
      // Associate back the original DB.
      try {
        associateRESTServerWithDB(restServerName, dbName);
      } catch (Exception e) {
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
   * To test QueryBatcher's callback support with long lookup time for the client
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
      dmManager.startJob(queryBatcherAddForest);
      queryBatcherAddForest.awaitCompletion(30, TimeUnit.SECONDS);

      if (queryBatcherAddForest.isStopped()) {

        if (batchFailResults != null && !batchFailResults.toString().isEmpty()) {
          System.out.print("Results from onQueryFailure === ");
          System.out.print(batchFailResults.toString());
          assertTrue( batchFailResults.toString().contains("Test has Exceptions"));
        }
      }
      assertTrue( batchResults.toString().isEmpty());
    } catch (Exception e) {
      System.out.print(e.getMessage());
      fail("testBatchClientLookupTimeout method failed");
    } finally {
      // Associate back the original DB.
      try {
        associateRESTServerWithDB(restServerName, dbName);
      } catch (Exception e) {
        e.printStackTrace();
      }
      detachForest(testMultipleDB, testMultipleForest[0]);
      deleteDB(testMultipleDB);
      deleteForest(testMultipleForest[0]);
      Thread.sleep(10000);
    }
  }

  /*
   * To test QueryBatcher when WriteBatcher writes same document. Simulate a
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
   * assertTrue(
   * res[0].contains("/batcher-contraints1.json"));
   * assertEquals("Bytes Moved","0", res[1]); assertEquals("Batch Number","0",
   * res[3]); } } }
   */

  @Test
  public void testQueryBatcherJobDetails() throws Exception
  {
    DatabaseClient clientTmp = null;
    DataMovementManager dmManagerTmp = null;

    try {
      System.out.println("Running testQueryBatcherJobDetails");

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

      assertTrue( !jobId.isEmpty());
      assertTrue( jobName.equalsIgnoreCase("QUERY_BATCHER"));

      boolean bJobFinished = queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);

      if (bJobFinished) {
        if (!batchFailResults.toString().isEmpty() && batchFailResults.toString().contains("Exceptions")) {
          fail("Test failed due to exceptions");
        }

        // Verify the batch results now.
        String[] res = batchResults.toString().split("\\|");
        assertEquals(1, res.length);
        assertTrue( res[0].contains("/abs-range-constraint/batcher-contraints2.xml"));

        // verify the Job and batch get method values.
        String[] batchDetailsArray = batchDetails.toString().split("\\|");

        assertTrue( Long.parseLong(batchDetailsArray[0]) > 0);
        assertTrue( Long.parseLong(batchDetailsArray[1]) > 0);
        assertTrue( Long.parseLong(batchDetailsArray[2]) > 0);

        // verify the forest get method values.
        String[] forestDetailsArray = forestDetails.toString().split("\\|");
        assertTrue( forestDetailsArray[0].equalsIgnoreCase(dbName));
        assertTrue( forestDetailsArray[2].contains(dbName));

      }
    } catch (Exception e) {
      System.out.println("Exceptions thrown from Test testAndWordQueryWithMultipleForests");
      System.out.println(e.getMessage());
      fail("testQueryBatcherJobDetails method failed");
    } finally {
      clientTmp.release();
      clearDB();
    }
  }

  /*
   * These are test methods that verify that different query types work. Testing
   * - Word query range query value query
   */
  @Test
  public void testDifferentQueryTypes() throws Exception
  {
    DatabaseClient clientTmp = null;
    DataMovementManager dmManagerTmp = null;

    try {
      System.out.println("Running testDifferentQueryTypes");

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
      dmManagerTmp.startJob(queryBatcher1);
      boolean bJobFinished = queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);

      if (bJobFinished) {

        if (!batchWordFailResults.toString().isEmpty() && batchWordFailResults.toString().contains("Exceptions")) {
          fail("Test failed due to exceptions in testDifferentQueryTypes - Word Query");
        }

        // Verify the batch results now.
        String[] res = batchWordResults.toString().split("\\|");
        assertEquals(1, res.length);
        assertTrue( res[0].contains("/abs-range-constraint/batcher-contraints5.xml"));
      }

      // Run a range query.
      StructuredQueryDefinition queryRangedef = qb.range(qb.element("popularity"), "xs:int", Operator.GE, 4);
      QueryBatcher queryBatcher2 = dmManagerTmp.newQueryBatcher(queryRangedef);
      // StringBuilder batchRangeResults = new StringBuilder();
      List<String> batchRangeResults = new ArrayList<String>();
      StringBuilder batchRangeFailResults = new StringBuilder();

      queryBatcher2.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchRangeResults.add(str);
        }
      });
      queryBatcher2.onQueryFailure(throwable -> {
        System.out.println("Exceptions thrown from callback onQueryFailure in testDifferentQueryTypes");
        throwable.printStackTrace();
        batchRangeFailResults.append("Test has Exceptions");
      });
      dmManagerTmp.startJob(queryBatcher2);
      bJobFinished = queryBatcher2.awaitCompletion(3, TimeUnit.MINUTES);

      if (bJobFinished) {
        if (!batchRangeFailResults.toString().isEmpty() && batchRangeFailResults.toString().contains("Exceptions")) {
          fail("Test failed due to exceptions in testDifferentQueryTypes - Word Query");
        }

        // Verify the batch results now.
        assertTrue( batchRangeResults.size() == 4);
        assertTrue( batchRangeResults.contains("/abs-range-constraint/batcher-contraints1.xml"));
        assertTrue( batchRangeResults.contains("/abs-range-constraint/batcher-contraints2.xml"));
        assertTrue( batchRangeResults.contains("/abs-range-constraint/batcher-contraints4.xml"));
        assertTrue( batchRangeResults.contains("/abs-range-constraint/batcher-contraints5.xml"));
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
      dmManagerTmp.startJob(queryBatcher3);
      bJobFinished = queryBatcher3.awaitCompletion(3, TimeUnit.MINUTES);

      if (bJobFinished) {
        if (!batchvalueFailResults.toString().isEmpty() && batchvalueFailResults.toString().contains("Exceptions")) {
          fail("Test failed due to exceptions in testDifferentQueryTypes - Word Query");
        }

        // Verify the batch results now.
        assertTrue( batchValueResults.size() == 1);
        assertTrue( batchRangeResults.contains("/abs-range-constraint/batcher-contraints1.xml"));
      }
    } catch (Exception e) {
      System.out.println("Exceptions thrown from Test testDifferentQueryTypes");
      System.out.println(e.getMessage());
      fail("testDifferentQueryTypes method failed");
    } finally {
      clientTmp.release();
      clearDB();
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
			  String[] hostNames = new String[10];
			  client = getDatabaseClient("eval-user", "x", getConnType());
			  dmManagerTmp = client.newDataMovementManager();

			  QueryManager queryMgr = client.newQueryManager();
			  StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();

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
			  dmManagerTmp.startJob(queryBatcher);
		  }
		  catch(IllegalArgumentException ex) {
			  batchFailResults.append(ex.getMessage());
		  }
		  finally {
			  client.release();
			  System.out.println("Exception Message is " + batchFailResults.toString());
			  assertTrue( batchFailResults.toString().contains("numHosts must be less than or equal to the number of hosts in the cluster"));
			  clearDB();
		  }
	  }
  }

  @Test
  public void testProgressListener() throws Exception {
	  DatabaseClient clientTmp = null;
	  try {
		  System.out.println("Running testProgressListener");
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
          Set<String> progressSet = Collections.synchronizedSet(new HashSet<>());
		  QueryBatcher batcher6000 = dmManager.newQueryBatcher(querydef).withBatchSize(6000).withThreadCount(1);
		  batcher6000.onUrisReady(

				  new ProgressListener()
				  .onProgressUpdate(progressUpdate -> {
					  System.out.println("From ProgressListener (Batch 6000): " + progressUpdate.getProgressAsString());
					  int index = progressUpdate.getProgressAsString().indexOf(";");
                      progressSet.add(progressUpdate.getProgressAsString().substring(0, index));
				  }));
		  batcher6000.onQueryFailure((throwable) -> {
			  System.out.println("queryFailures 6000: ");
			  try {
				  Thread.sleep(7000L);
			  } catch (Exception e) {
				  e.printStackTrace();
			  }
		  });

		  dmManager.startJob(batcher6000);
		  batcher6000.awaitCompletion();
          assertTrue( progressSet.toString().contains("Progress: 6000 results"));

		  // Read in smaller batches and monitor progress
          Set<String> progressSet60 = Collections.synchronizedSet(new HashSet<>());
		  QueryBatcher batcher60 = dmManager.newQueryBatcher(querydef).withBatchSize(60, 1);
                  //.withThreadCount(1);
		  batcher60.onUrisReady(
				  new ProgressListener()
				  .onProgressUpdate(progressUpdate -> {
					  System.out.println("From ProgressListener (From Batch 60): " + progressUpdate.getProgressAsString());
                      int index = progressUpdate.getProgressAsString().indexOf(";");
                      progressSet60.add(progressUpdate.getProgressAsString().substring(0, index));
				  }));
		  batcher60.onQueryFailure((throwable) -> {
			  System.out.println("queryFailures 60: ");
			  try {
				  Thread.sleep(7000L);
			  } catch (Exception e) {
				  e.printStackTrace();
			  }
		  });

		  dmManager.startJob(batcher60);
		  batcher60.awaitCompletion();

		  // Make sure all updates are available
          assertTrue( progressSet60.toString().contains("Progress: 60 results"));
		  assertTrue( progressSet60.toString().contains("Progress: 5940 results"));
		  assertTrue( progressSet60.toString().contains("Progress: 6000 results"));
		  // Batches read are uneven and with multiple threads

          Set<String> progressSet33 = Collections.synchronizedSet(new HashSet<>());
		  QueryBatcher batcher33 = dmManager.newQueryBatcher(querydef).withBatchSize(33, 1).withThreadCount(3);
		  batcher33.onUrisReady(
				  new ProgressListener()
				  .onProgressUpdate(progressUpdate -> {
					  System.out.println("From ProgressListener (From Batch 33): " + progressUpdate.getProgressAsString());
                      int index = progressUpdate.getProgressAsString().indexOf(";");
                      progressSet33.add(progressUpdate.getProgressAsString().substring(0, index));
				  }));
		  batcher33.onQueryFailure((throwable) -> {
			  System.out.println("queryFailures 33: ");
			  try {
				  Thread.sleep(7000L);
			  } catch (Exception e) {
				  e.printStackTrace();
			  }
		  });
		  dmManager.startJob(batcher33);
		  batcher33.awaitCompletion();

		  // Make sure all updates are available
		  assertTrue( progressSet33.toString().contains("Progress: 33 results"));
		  assertTrue( progressSet33.toString().contains("Progress: 5973 results"));
		  assertTrue( progressSet33.toString().contains("Progress: 6000 results"));

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
				  Thread.sleep(7000L);
			  } catch (Exception e) {
				  e.printStackTrace();
			  }
		  });

		  dmManager.startJob(batcherErr);
		  batcherErr.awaitCompletion();

		  System.out.println("From buffer Err: " + strErr.toString());
		  // No updates are available
		  assertTrue( strErr.toString().isEmpty());
	  }
	  catch (Exception ex) {
		  ex.printStackTrace();
		  fail("testProgressListener method failed");
	  }
	  finally {
		  clientTmp.release();
		  clearDB();
	  }
  }

  @Test
  public void testQueryBatcherWithIterator() throws Exception {
	  DatabaseClient clientTmp = null;
	  try {
		  System.out.println("Running testQueryBatcherWithIterator");
		  DataMovementManager dmManager = null;
		  String uri = null;
		  Collection<String> uris = new LinkedHashSet<String>();
		  Collection<String> uris20 = new LinkedHashSet<String>();
		  Collection<String> batchValueResults = new LinkedHashSet<String>();
		  AtomicInteger count = new AtomicInteger(0);

		  clientTmp = getDatabaseClient("eval-user", "x", getConnType());
		  dmManager = clientTmp.newDataMovementManager();

		  String jsonDoc = "{" + "\"employees\": [" + "{ \"firstName\":\"Juan\" , \"lastName\":\"Baptiste\" },"
				  + "{ \"firstName\":\"Tress\" , \"lastName\":\"Bossmann\" },"
				  + "{ \"firstName\":\"Micheal\" , \"lastName\":\"Fox\" }]" + "}";
		  WriteBatcher wbatcher = dmManager.newWriteBatcher();
		  wbatcher.withBatchSize(600);
		  wbatcher.onBatchFailure((batch, throwable) -> throwable.printStackTrace());
		  StringHandle handle = new StringHandle();
		  handle.set(jsonDoc);

		  // Insert 6 K documents
		  for (int i = 0; i < 6000; i++) {
			  uri = "/QBIteratorTest" + i + ".json";
			  wbatcher.add(uri, handle);
			  uris.add(uri);
			  if (i < 20) {
				  uris20.add(uri);
			  }
		  }

		  wbatcher.flushAndWait();
		  // Read all 6000 docs in a batch using an iterator.

		  QueryBatcher queryBatcher = dmManager.newQueryBatcher(uris.iterator())
				                               .withBatchSize(20)
				                               .withThreadCount(1);

		  queryBatcher.onUrisReady(batch -> {
			  System.out.println("Batch results are: " + batch.getJobResultsSoFar());
			  // Get 1 batch only for comparison.
			  if (count.get() == 0) {
				  for (String str : batch.getItems()) {
			          batchValueResults.add(str);
			        }
			  count.incrementAndGet();
			  }
		  }
		)
		.onQueryFailure(throwable -> {
			  System.out.println("Exceptions thrown from callback onQueryFailure in testMinHostWithHostAvailabilityListener");
			  throwable.printStackTrace();

		  });

		  dmManager.startJob(queryBatcher);
		  queryBatcher.awaitCompletion();
		  System.out.println("First batch comprisions");

		  assertTrue( batchValueResults.equals(uris20));
	  }
	  catch (Exception ex) {
	  ex.printStackTrace();
	  fail("testQueryBatcherWithIterator method failed");
  }
  finally {
	  clientTmp.release();
	  clearDB();
  }
  }

  // Verify UTF-8 char in URI. Refer to Git Issue 1163.

  @Test
  public void testUTF8InUri() throws Exception
  {
        System.out.println("Running testUTF8InUri");
        DatabaseClient clientTmp = null;
        DataMovementManager dmManagerTmp = null;

        try {

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
          wbatcher.add("/CXXXX_Ü_testqa.xml", contentHandle5);

          wbatcher.flushAndWait();

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

          dmManagerTmp.startJob(queryBatcher1);
          boolean bJobFinished = queryBatcher1.awaitCompletion(3, TimeUnit.MINUTES);

          if (bJobFinished) {

            if (!batchFailResults.toString().isEmpty() && batchFailResults.toString().contains("Exceptions")) {
              fail("Test failed due to exceptions");
            }

            // Verify the batch results now.
            String[] res = batchResults.toString().split("\\|");
            assertEquals(1, res.length);
            assertTrue( res[0].trim().contains("/CXXXX_Ü_testqa.xml"));

            // Read the document and assert on the value
            DOMHandle contentHandle = new DOMHandle();
            contentHandle = readDocumentUsingDOMHandle(clientTmp, "/CXXXX_Ü_testqa.xml", "XML");
            Document readDoc = contentHandle.get();
            System.out.println(convertXMLDocumentToString(readDoc));

            assertTrue( readDoc.getElementsByTagName("id").item(0).getTextContent().contains("0026"));
            assertTrue( readDoc.getElementsByTagName("title").item(0).getTextContent().contains("The memex"));
            assertTrue( readDoc.getElementsByTagName("date").item(0).getTextContent().contains("2009-05-05"));
          }
        } catch (Exception e) {
          System.out.println("Exceptions thrown from testUTF8InUri");
          System.out.println(e.getMessage());
          fail("testUTF8InUri mathod failed");
        } finally {
          clientTmp.release();
          clearDB();
        }
      }

  // Verify namespaces in query. Refer to Git Issue 1283.

  @Test
  public void testPathNameSpacesInQuery() throws Exception {
    System.out.println("Running testPathNameSpacesInQuery");
    DatabaseClient clientTmp = null;
    DataMovementManager dmManagerTmp = null;

    try {
      // Insert docs
      StringBuilder doc1 = new StringBuilder();
      doc1.append("<root xmlns=\"http://www.example1.com\">");
      doc1.append("<title>Vannevar Bush</title>");
      doc1.append("<popularity>5</popularity>");
      doc1.append("<id>0011</id>");
      doc1.append("<date xmlns=\"http://purl.org/dc/elements/1.1/\">2005-01-01</date>");
      doc1.append("<price xmlns=\"http://cloudbank.com\" amt=\"0.1\"/>");
      doc1.append("<p>Vannevar Bush wrote an article for The Atlantic Monthly</p>");
      doc1.append("<status>active</status>");
      doc1.append("<g-elem-point>12,5</g-elem-point>");
      doc1.append("<g-elem-child-parent><g-elem-child-point>12,5</g-elem-child-point></g-elem-child-parent>");
      doc1.append("<g-elem-pair><lat>12</lat><long>5</long></g-elem-pair>");
      doc1.append("<g-attr-pair lat=\"12\" long=\"5\"/>");
      doc1.append("</root>");

      StringBuilder doc2 = new StringBuilder();
      doc2.append("<root xmlns=\"http://www.example2.com\">");
      doc2.append("<title>The Bush article</title>");
      doc2.append("<popularity>4</popularity>");
      doc2.append("<id>0012</id>");
      doc2.append("<date xmlns=\"http://purl.org/dc/elements/1.1/\">2006-02-02</date>");
      doc2.append("<price xmlns=\"http://cloudbank.com\" amt=\"0.12\"/>");
      doc2.append("<p>The Bush article described a device called a Memex.</p>");
      doc2.append("<status>active</status>");
      doc2.append("</root>");

      StringBuilder doc3 = new StringBuilder();
      doc3.append("<root xmlns=\"http://www.example2.com\">");
      doc3.append("<title>For 1945</title>");
      doc3.append("<popularity>3</popularity>");
      doc3.append("<id>0113</id>");
      doc3.append("<date xmlns=\"http://purl.org/dc/elements/1.1/\">2007-03-03</date>");
      doc3.append("<price xmlns=\"http://cloudbank.com\" amt=\"1.22\"/>");
      doc3.append("<p>For 1945, the thoughts expressed in The Atlantic Monthly were groundbreaking.</p>");
      doc3.append("<status>pending</status>");
      doc3.append("</root>");

      StringBuilder doc4 = new StringBuilder();
      doc4.append("<root xmlns=\"http://www.example2.com\">");
      doc4.append("<title>Vannevar served</title>");
      doc4.append("<popularity>5</popularity>");
      doc4.append("<id>0024</id>");
      doc4.append("<date xmlns=\"http://purl.org/dc/elements/1.1/\">2008-04-04</date>");
      doc4.append("<price xmlns=\"http://cloudbank.com\" amt=\"12.34\"/>");
      doc4.append("<p>Vannevar served as a prominent policymaker and public intellectual.</p>");
      doc4.append("<status>active</status>");
      doc4.append("</root>");

      StringBuilder doc5 = new StringBuilder();
      doc5.append("<root xmlns=\"http://www.example2.com\">");
      doc5.append("<title>The memex</title>");
      doc5.append("<popularity>5</popularity>");
      doc5.append("<id>0026</id>");
      doc5.append("<date xmlns=\"http://purl.org/dc/elements/1.1/\">2009-05-05</date>");
      doc5.append("<price xmlns=\"http://cloudbank.com\" amt=\"123.45\"/>");
      doc5.append("<p>The Memex, unfortunately, had no automated search feature.</p>");
      doc5.append("<status>pending</status>");
      doc5.append("</root>");

      clientTmp = getDatabaseClient("eval-user", "x", getConnType());
      dmManager = clientTmp.newDataMovementManager();
      WriteBatcher batcher = dmManager.newWriteBatcher();
      dmManager.startJob(batcher);
      batcher.add("/testdoc/doc1,xml", new StringHandle(doc1.toString()));
      batcher.add("/testdoc/doc2.xml", new StringHandle(doc2.toString()));
      batcher.add("/testdoc/doc3.xml", new StringHandle(doc3.toString()));
      batcher.add("/testdoc/doc4.xml", new StringHandle(doc4.toString()));
      batcher.add("/testdoc/doc5.xml", new StringHandle(doc5.toString()));

      batcher.flushAndWait();
      QueryManager queryManager = clientTmp.newQueryManager();

      StringBuilder resultUris = new StringBuilder();
      StringBuilder failStr = new StringBuilder();
      AtomicInteger docCnt = new AtomicInteger(0);

      StructuredQueryBuilder queryBuilder = queryManager.newStructuredQueryBuilder();
      EditableNamespaceContext namespaceContext = new EditableNamespaceContext();
      namespaceContext.put("nsdate", "http://purl.org/dc/elements/1.1/");
      namespaceContext.put("ns1", "http://www.example1.com");
      namespaceContext.put("ns2", "http://www.example2.com");
      queryBuilder.setNamespaces(namespaceContext);

      StructuredQueryDefinition qd = queryBuilder.range(
              queryBuilder.pathIndex("//nsdate:date"),
              "xs:date", StructuredQueryBuilder.Operator.GT, "2007-01-01");

      QueryBatcher qb = dmManager.newQueryBatcher(qd)
              .onUrisReady(batch -> {
                System.out.println("Items: " + Arrays.asList(batch.getItems()));
                for(String s:batch.getItems()) {
                  resultUris.append(s);
                  resultUris.append("|");
                  docCnt.incrementAndGet();
                }
              })
              .onQueryFailure(failure -> {
                System.out.println("Failure: " + failure.getMessage());
                failStr.append(failure.getMessage());
              });
      try {
        dmManager.startJob(qb);
        qb.awaitCompletion();
      } catch (Exception e) {
        System.out.println("Exceptions thrown from Query Batcher job");
      }
      finally {
        dmManager.stopJob(qb);
        int ndocs = docCnt.get();
        if (! failStr.toString().isEmpty()) {
          fail("QueryBatcher failed to query required docs.");
        }
        assertEquals(3, ndocs);
        String res = resultUris.toString();
        assertTrue( res.contains("/testdoc/doc3.xml"));
        assertTrue( res.contains("/testdoc/doc4.xml"));
        assertTrue( res.contains("/testdoc/doc5.xml"));
      }

    } catch (Exception e) {
      System.out.println("Exceptions thrown from testPathNameSpacesInQuery");
      System.out.println(e.getMessage());
      fail("testPathNameSpacesInQuery mathod failed");
    } finally {
      clientTmp.release();
      clearDB();
    }

  }
}
