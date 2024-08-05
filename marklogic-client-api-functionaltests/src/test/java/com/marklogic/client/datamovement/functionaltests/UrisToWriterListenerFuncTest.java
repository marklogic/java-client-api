/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.datamovement.functionaltests;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.functionaltest.Artifact;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.functionaltest.Company;
import com.marklogic.client.functionaltest.Product;
import com.marklogic.client.io.*;
import com.marklogic.client.io.marker.ContentHandleFactory;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

public class UrisToWriterListenerFuncTest extends BasicJavaClientREST {

  private static String dbName = "UrisToWriterListenerFuncTestDB";
  private static String[] fNames = { "UrisToWriterListenerFuncTestDB-1", "UrisToWriterListenerFuncTestDB-2", "UrisToWriterListenerFuncTestDB-3" };

  private static String restServerName = null;
  private static String dataConfigDirPath = null;
  private static int restServerPort = 0;
  private static DatabaseClient clientQHB = null;
  private static DataMovementManager dmManager = null;
  private static DatabaseClient clientQHBTmp = null;
  private static DataMovementManager dmManagerTmp = null;
  private static String uriFile2 = "testMultipleOutputListeners2.txt";
  private static FileWriter writer2 = null;

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    loadGradleProperties();
    restServerPort = getRestAppServerPort();

    restServerName = getRestAppServerName();
    dataConfigDirPath = getDataConfigDirPath();

    setupJavaRESTServer(dbName, fNames[0], restServerName, restServerPort);
    setupAppServicesConstraint(dbName);

    createUserRolesWithPrevilages("test-eval", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createRESTUser("eval-user", "x", "test-eval", "rest-admin", "rest-writer", "rest-reader", "rest-extension-user", "manage-user");

    // For use with QueryHostBatcher
    clientQHB =getDatabaseClient("eval-user", "x", getConnType());
    dmManager = clientQHB.newDataMovementManager();

    clientQHBTmp = getDatabaseClient("eval-user", "x", getConnType());
    dmManagerTmp = clientQHBTmp.newDataMovementManager();
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    System.out.println("In tearDownAfterClass");
    // Release clients
    clientQHB.release();
    associateRESTServerWithDB(restServerName, "Documents");
    deleteRESTUser("eval-user");
    deleteUserRole("test-eval");
    detachForest(dbName, fNames[0]);

    deleteDB(dbName);
    deleteForest(fNames[0]);
  }

  @AfterEach
  public void tearDown() throws Exception {
    System.out.println("In tearDown");
    clearDB(restServerPort);
  }

  /*
   * To test UriToWriterListener simple use case with multiple file types and
   * get URIs into an output file.
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
  public void testMultipleFileTypes() throws IOException, ParserConfigurationException, SAXException, InterruptedException
  {
    System.out.println("Running testMultipleFileTypes");
    StringBuilder wbatchResults = new StringBuilder();
    StringBuilder wbatchFailResults = new StringBuilder();
    // file name to hold uris written out
    String uriFile = "testMultipleFileTypes.txt";
    String collection = "MultipleFileTypes";
    // Move to individual data sub folders.
    String dataFileDir = dataConfigDirPath + "/data/";

    FileWriter writer = null;
    FileReader freader = null;
    BufferedReader UriReaderTxt = null;
    try {
      // Use a collection to querydef for QueryBatcher.
      DocumentMetadataHandle metadata = new DocumentMetadataHandle()
          .withCollections(collection)
          // .withProperty("SomeCollection", "true")
          .withQuality(100);
      // Use WriteBatcher to write files.
      WriteBatcher wbatcher = dmManager.newWriteBatcher();
      wbatcher.withBatchSize(2).withThreadCount(1);

      wbatcher.onBatchSuccess(
          batch -> {

            for (WriteEvent w : batch.getItems()) {
              wbatchResults.append(w.getTargetUri() + ":");
            }
          }
          )
          .onBatchFailure(
              (batch, throwable) -> {
                throwable.printStackTrace();
                for (WriteEvent w : batch.getItems()) {
                  System.out.println("Failed URI's from Writebatcher are" + w.getTargetUri());
                  wbatchFailResults.append(w.getTargetUri() + ":");
                }
              });
      dmManager.startJob(wbatcher);
      // Add multiple files to batcher

      InputStreamHandle contentHandle1 = new InputStreamHandle();
      contentHandle1.set(new FileInputStream(new File(dataFileDir + "binary.jpg"
          )
          )
          );
      wbatcher.add("/binary.jpg", metadata, contentHandle1);
      InputStreamHandle contentHandle2 = new InputStreamHandle();
      contentHandle2.set(new FileInputStream(new File(dataFileDir + "constraint1.xml"
          )
          )
          );
      wbatcher.add("/constraint1.xml", metadata, contentHandle2);
      InputStreamHandle contentHandle3 = new InputStreamHandle();
      contentHandle3.set(new FileInputStream(new File(dataFileDir + "employee-stylesheet.xsl"
          )
          )
          );
      wbatcher.add("/employee-stylesheet.xsl", metadata, contentHandle3);
      InputStreamHandle contentHandle4 = new InputStreamHandle();
      contentHandle4.set(new FileInputStream(new File(dataFileDir + "product-microsoft.json"
          )
          )
          );
      wbatcher.add("/product-microsoft.json", metadata, contentHandle4);
      InputStreamHandle contentHandle5 = new InputStreamHandle();
      contentHandle5.set(new FileInputStream(new File(dataFileDir + "xqueries.txt"
          )
          )
          );
      wbatcher.add("/xqueries.txt", metadata, contentHandle5);
      InputStreamHandle contentHandle6 = new InputStreamHandle();
      contentHandle6.set(new FileInputStream(new File(dataFileDir + "multibyte1.xml"
          )
          )
          );
      wbatcher.add("/multibyte1.xml", metadata, contentHandle6);
      wbatcher.flushAndWait();

      Thread.sleep(5000);
      ;

      writer = new FileWriter(uriFile);
      StructuredQueryDefinition querydef = new StructuredQueryBuilder().collection(collection);
      // Run a QueryBatcher on the new URIs.
      StringBuilder batchResults = new StringBuilder();
      StringBuilder batchFailResults = new StringBuilder();

      QueryBatcher qBatcher = dmManager.newQueryBatcher(querydef);
      qBatcher.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchResults.append(str)
              .append('|');
        }
      }).onUrisReady(new UrisToWriterListener(writer))
          .onQueryFailure(throwable -> {
            System.out.println("Exceptions thrown from callback onQueryFailure");
            throwable.printStackTrace();
            batchFailResults.append("Test has Exceptions");
          });
      JobTicket qBatcherJob = dmManager.startJob(qBatcher);
      // Wait for query Batcher to complete and stop Job.
      qBatcher.awaitCompletion();
      dmManager.stopJob(qBatcherJob);
      writer.flush();

      // Verify the writer (file) succeeded.
      freader = new FileReader(uriFile);
      UriReaderTxt = new BufferedReader(freader);
      TreeMap<String, String> expectedMap = new TreeMap<String, String>();
      TreeMap<String, String> uriMap = new TreeMap<String, String>();
      expectedMap.put("/binary.jpg", "URI");
      expectedMap.put("/constraint1.xml", "URI");
      expectedMap.put("/employee-stylesheet.xsl", "URI");
      expectedMap.put("/multibyte1.xml", "URI");
      expectedMap.put("/product-microsoft.json", "URI");
      expectedMap.put("/xqueries.txt", "URI");

      String line = null;
      while ((line = UriReaderTxt.readLine()) != null) {
        System.out.println("Line read from file with URIS is" + line);
        uriMap.put(line, "URI");
      }
      assertTrue(expectedMap.equals(uriMap));
    } catch (Exception ex) {
      System.out.println("Exceptions in testMultipleFileTypes method" + ex.getMessage());
    } finally {
      try {
        if (writer != null)
          writer.close();
        if (UriReaderTxt != null)
          UriReaderTxt.close();
        if (freader != null)
          freader.close();
        // Delete the file on JVM exit
        File file = new File(uriFile);
        file.deleteOnExit();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * To test if URI from UriToWriterListener writer can be used to read
   * document.
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
  public void testReadbacks() throws IOException, ParserConfigurationException, SAXException, InterruptedException
  {
    System.out.println("Running testReadbacks");
    StringBuilder wbatchResults = new StringBuilder();
    StringBuilder wbatchFailResults = new StringBuilder();
    // File name to hold uris written out
    String uriFile = "testReadbacks.txt";
    String docId = null;
    String collection = "MultipleFileTypes";
    String dataFileDir = dataConfigDirPath + "/data/";

    FileWriter writer = null;
    FileReader freader = null;
    BufferedReader UriReaderTxt = null;

    try {
      // Use a collection to querydef for QueryBatcher.
      DocumentMetadataHandle metadata = new DocumentMetadataHandle()
          .withCollections(collection)
          // .withProperty("SomeCollection", "true")
          .withQuality(100);
      // Use WriteBatcher to write files.
      WriteBatcher wbatcher = dmManager.newWriteBatcher();
      wbatcher.withBatchSize(2).withThreadCount(1);

      wbatcher.onBatchSuccess(
          batch -> {
            for (WriteEvent w : batch.getItems()) {
              wbatchResults.append(w.getTargetUri() + ":");
            }
          }
          )
          .onBatchFailure(
              (batch, throwable) -> {
                throwable.printStackTrace();
                for (WriteEvent w : batch.getItems()) {
                  System.out.println("Failed URI's from Writebatcher are" + w.getTargetUri());
                  wbatchFailResults.append(w.getTargetUri() + ":");
                }
              });
      dmManager.startJob(wbatcher);
      // Add file to batcher
      InputStreamHandle contentHandle1 = new InputStreamHandle();
      contentHandle1.set(new FileInputStream(new File(dataFileDir + "product-microsoft.json"
          )
          )
          );
      wbatcher.add("/product-microsoft.json", metadata, contentHandle1);

      wbatcher.flushAndWait();
      wbatcher.awaitCompletion();
      if (wbatchFailResults.length() > 0 || wbatchResults.toString().split(":").length != 1) {
        System.out.println("Success URI's from Write batcher : " + wbatchResults.toString());
        System.out.println("Failure URI's from Write batcher : " + wbatchFailResults.toString());
        fail("Test failed due to errors in write batcher");
      }
      writer = new FileWriter(uriFile);
      StructuredQueryDefinition querydef = new StructuredQueryBuilder().collection(collection);
      // Run a QueryBatcher on the new URIs.
      StringBuilder batchResults = new StringBuilder();
      StringBuilder batchFailResults = new StringBuilder();

      QueryBatcher qBatcher = dmManager.newQueryBatcher(querydef);
      qBatcher.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchResults.append(str)
              .append('|');
        }
      }).onUrisReady(new UrisToWriterListener(writer))
          .onQueryFailure(throwable -> {
            System.out.println("Exceptions thrown from callback onQueryFailure");
            throwable.printStackTrace();
            batchFailResults.append("Test has Exceptions");
          });
      JobTicket qBatcherJob = dmManager.startJob(qBatcher);
      // Wait for query Batcher to complete and stop Job.
      qBatcher.awaitCompletion();
      dmManager.stopJob(qBatcherJob);
      writer.flush();

      // Verify the writer (file) succeeded.
      freader = new FileReader(uriFile);
      UriReaderTxt = new BufferedReader(freader);
      String line = "";
      while ((line = UriReaderTxt.readLine()) != null) {
        System.out.println("Line read from file with URIS is" + line);
        docId = line.trim();
      }
      assertTrue(docId.contains("product-microsoft.json"));
      // Verify the URI from UrisToWriterListener writer by reading in the
      // document.

      JSONDocumentManager docMgr = clientQHB.newJSONDocumentManager();
      JacksonHandle jacksonhandle = new JacksonHandle();
      docMgr.read(docId, jacksonhandle);
      JsonNode resultNode = jacksonhandle.get();

      assertEquals("Windows 10", resultNode.path("name").asText());
      assertEquals("Software", resultNode.path("industry").asText());
      assertEquals("OS Server", resultNode.path("description").asText());
    } catch (Exception ex) {
      System.out.println("Exceptions thrown from testReadbacks method" + ex.getMessage());
    } finally {
      try {
        if (writer != null)
          writer.close();
        if (UriReaderTxt != null)
          UriReaderTxt.close();
        if (freader != null)
          freader.close();
        // Delete the file on JVM exit
        File file = new File(uriFile);
        file.deleteOnExit();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * To test UriToWriterListener - write to an output file twice after a file
   * close. Write 4 docs Query for 4 docs in collection1 Verify writer - line
   * count Close writer Write 2 more docs into collection2 Query for 2 docs in
   * collection2 Open writer in append mode Verify writer contents
   */
  @Test
  public void testWriteMultipleTimes() throws IOException, ParserConfigurationException, SAXException, InterruptedException
  {
    System.out.println("Running testMultipleFileTypes");
    StringBuilder wbatchResults1 = new StringBuilder();
    StringBuilder wbatchFailResults1 = new StringBuilder();
    // file name to hold uris written out
    String uriFile = "testMultipleFileTypes.txt";
    String collection1 = "MultipleFileTypes1";
    String collection2 = "MultipleFileTypes2";
    String dataFileDir = dataConfigDirPath + "/data/";

    FileWriter writer = null, writer1 = null;
    FileReader filereader1 = null, freader2 = null;
    BufferedReader reader1 = null, UriReaderTxt2 = null;
    try {
      // Use a collection1 to querydef for QueryBatcher.
      DocumentMetadataHandle metadata1 = new DocumentMetadataHandle()
          .withCollections(collection1)
          // .withProperty("SomeCollection", "true")
          .withQuality(100);
      // Use WriteBatcher1 to write files.
      WriteBatcher wbatcher1 = dmManager.newWriteBatcher();
      wbatcher1.withBatchSize(2).withThreadCount(1);

      wbatcher1.onBatchSuccess(
          batch -> {
            for (WriteEvent w : batch.getItems()) {
              wbatchResults1.append(w.getTargetUri() + ":");
            }
          }
          )
          .onBatchFailure(
              (batch, throwable) -> {
                throwable.printStackTrace();
                for (WriteEvent w : batch.getItems()) {
                  System.out.println("Failed URI's from Writebatcher are" + w.getTargetUri());
                  wbatchFailResults1.append(w.getTargetUri() + ":");
                }
              });
      dmManager.startJob(wbatcher1);
      // Add multiple files to batcher1

      InputStreamHandle contentHandle1 = new InputStreamHandle();
      contentHandle1.set(new FileInputStream(new File(dataFileDir + "binary.jpg"
          )
          )
          );
      wbatcher1.add("/binary.jpg", metadata1, contentHandle1);
      InputStreamHandle contentHandle2 = new InputStreamHandle();
      contentHandle2.set(new FileInputStream(new File(dataFileDir + "constraint1.xml"
          )
          )
          );
      wbatcher1.add("/constraint1.xml", metadata1, contentHandle2);
      InputStreamHandle contentHandle3 = new InputStreamHandle();
      contentHandle3.set(new FileInputStream(new File(dataFileDir + "employee-stylesheet.xsl"
          )
          )
          );
      wbatcher1.add("/employee-stylesheet.xsl", metadata1, contentHandle3);
      InputStreamHandle contentHandle4 = new InputStreamHandle();
      contentHandle4.set(new FileInputStream(new File(dataFileDir + "product-microsoft.json"
          )
          )
          );
      wbatcher1.add("/product-microsoft.json", metadata1, contentHandle4);

      DocumentMetadataHandle metadata2 = new DocumentMetadataHandle()
          .withCollections(collection2)
          // .withProperty("SomeCollection", "true")
          .withQuality(100);
      wbatcher1.flushAndWait();
      wbatcher1.awaitCompletion();
      if (wbatchFailResults1.length() > 0 || wbatchResults1.toString().split(":").length != 4) {
        System.out.println("Success URI's from Write batcher 1: " + wbatchResults1.toString());
        System.out.println("Failure URI's from Write batcher 1: " + wbatchFailResults1.toString());
        fail("Test failed due to errors in write batcher 1");
      }
      writer = new FileWriter(uriFile);
      StructuredQueryDefinition querydef1 = new StructuredQueryBuilder().collection(collection1);
      // Run a QueryBatcher on the new URIs.
      StringBuilder batchResults1 = new StringBuilder();
      StringBuilder batchFailResults1 = new StringBuilder();

      QueryBatcher qBatcher1 = dmManager.newQueryBatcher(querydef1);
      qBatcher1.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchResults1.append(str)
              .append('|');
        }
      }).onUrisReady(new UrisToWriterListener(writer))
          .onQueryFailure(throwable -> {
            System.out.println("Exceptions thrown from callback onQueryFailure 1");
            throwable.printStackTrace();
            batchFailResults1.append("Test has Exceptions from query 1");
          });
      JobTicket qBatcherJob1 = dmManager.startJob(qBatcher1);
      // Wait for query Batcher to complete and stop Job.
      qBatcher1.awaitCompletion();
      dmManager.stopJob(qBatcherJob1);
      writer.flush();

      // Verify writer first time
      int lnCnt = 0;
      filereader1 = new FileReader(uriFile);
      reader1 = new BufferedReader(filereader1);
      while (reader1.readLine() != null) {
        lnCnt++;
      }
      assertEquals(4, lnCnt);
      reader1.close();
      filereader1.close();
      // Use WriteBatcher2 to write files.
      WriteBatcher wbatcher2 = dmManager.newWriteBatcher();
      StringBuilder wbatchResults2 = new StringBuilder();
      StringBuilder wbatchFailResults2 = new StringBuilder();
      wbatcher2.withBatchSize(2).withThreadCount(1);

      wbatcher2.onBatchSuccess(
          batch -> {
            for (WriteEvent w : batch.getItems()) {
              wbatchResults2.append(w.getTargetUri() + ":");
            }
          }
          )
          .onBatchFailure(
              (batch, throwable) -> {
                throwable.printStackTrace();
                for (WriteEvent w : batch.getItems()) {
                  System.out.println("Failed URI's from Writebatcher are" + w.getTargetUri());
                  wbatchFailResults2.append(w.getTargetUri() + ":");
                }
              });
      dmManager.startJob(wbatcher2);
      InputStreamHandle contentHandle5 = new InputStreamHandle();
      contentHandle5.set(new FileInputStream(new File(dataFileDir + "xqueries.txt"
          )
          )
          );
      wbatcher2.add("/xqueries.txt", metadata2, contentHandle5);
      InputStreamHandle contentHandle6 = new InputStreamHandle();
      contentHandle6.set(new FileInputStream(new File(dataFileDir + "multibyte1.xml"
          )
          )
          );
      wbatcher2.add("/multibyte1.xml", metadata2, contentHandle6);
      wbatcher2.flushAndWait();
      wbatcher2.awaitCompletion();
      if (wbatchFailResults2.length() > 0 || wbatchResults2.toString().split(":").length != 2) {
        System.out.println("Success URI's from Write batcher 2: " + wbatchResults2.toString());
        System.out.println("Failure URI's from Write batcher 2: " + wbatchFailResults2.toString());
        fail("Test failed due to errors in write batcher 2");
      }

      writer1 = new FileWriter(uriFile, true);
      StructuredQueryDefinition querydef2 = new StructuredQueryBuilder().collection(collection2);
      // Run a QueryBatcher on the new URIs.
      StringBuilder batchResults2 = new StringBuilder();
      StringBuilder batchFailResults2 = new StringBuilder();

      QueryBatcher qBatcher2 = dmManager.newQueryBatcher(querydef2);
      qBatcher2.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchResults2.append(str)
              .append('|');
        }
      }).onUrisReady(new UrisToWriterListener(writer1))
          .onQueryFailure(throwable -> {
            System.out.println("Exceptions thrown from callback onQueryFailure");
            throwable.printStackTrace();
            batchFailResults2.append("Test has Exceptions");
          });
      JobTicket qBatcherJob2 = dmManager.startJob(qBatcher2);
      // Wait for query Batcher to complete and stop Job.
      qBatcher2.awaitCompletion();
      dmManager.stopJob(qBatcherJob2);
      writer1.flush();

      // Verify the writer (file) succeeded.
      freader2 = new FileReader(uriFile);
      UriReaderTxt2 = new BufferedReader(freader2);
      TreeMap<String, String> expectedMap = new TreeMap<String, String>();
      TreeMap<String, String> uriMap = new TreeMap<String, String>();
      expectedMap.put("/binary.jpg", "URI");
      expectedMap.put("/constraint1.xml", "URI");
      expectedMap.put("/employee-stylesheet.xsl", "URI");
      expectedMap.put("/multibyte1.xml", "URI");
      expectedMap.put("/product-microsoft.json", "URI");
      expectedMap.put("/xqueries.txt", "URI");

      String line = null;
      while ((line = UriReaderTxt2.readLine()) != null) {
        System.out.println("Line read from file with URIS is" + line);
        uriMap.put(line, "URI");
      }
      assertTrue(expectedMap.equals(uriMap));
    } catch (Exception ex) {
      System.out.println("Exceptions thrown from testWriteMultipleTimes method " + ex.getMessage());
    } finally {
      try {
        if (writer != null)
          writer.close();
        if (writer1 != null)
          writer1.close();

        if (filereader1 != null)
          filereader1.close();
        if (reader1 != null)
          reader1.close();
        if (freader2 != null)
          freader2.close();
        if (UriReaderTxt2 != null)
          UriReaderTxt2.close();
        // Delete the file on JVM exit
        File file = new File(uriFile);
        file.deleteOnExit();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * To test UriToWriterListener with POJO objects' URIs POJOs are stored in
   * Database using regular Java Client API, not using WriteBatcher
   */
  @Test
  public void testPOJOUris() throws IOException, ParserConfigurationException, SAXException, InterruptedException
  {
    System.out.println("Running testPOJOUris");
    String uriFile = "testPOJOUris.txt";
    TreeMap<String, String> expectedMap = new TreeMap<String, String>();
    PojoRepository<Artifact, Long> products = clientQHB.newPojoRepository(Artifact.class, Long.class);
    FileWriter writer1 = null;
    FileReader freader2 = null;
    BufferedReader UriReaderTxt2 = null;
    try {
      // Populate POJOs in database and get expected URIs into the map for the
      // assert.
      String popStr = null;
      for (int i = 1; i < 111; i++) {
        if (i % 2 == 0) {
          products.write(this.getArtifact(i), "even", "numbers");
        }
        else {
          products.write(this.getArtifact(i), "odd", "numbers");
        }
        popStr = "com.marklogic.client.functionaltest.Artifact/" + i + ".json";
        expectedMap.put(popStr, "URI");
      }
      QueryManager queryMgr = clientQHB.newQueryManager();
      StringQueryDefinition qd = queryMgr.newStringDefinition();
      qd.setCriteria("cogs");

      // Use QueryBatcher and UrisToWriterListener
      writer1 = new FileWriter(uriFile, true);

      // Run a QueryBatcher on the new URIs.
      StringBuilder batchResults2 = new StringBuilder();
      StringBuilder batchFailResults2 = new StringBuilder();

      QueryBatcher qBatcher2 = dmManager.newQueryBatcher(qd);
      qBatcher2.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchResults2.append(str)
              .append('|');
        }
      }).onUrisReady(new UrisToWriterListener(writer1))
          .onQueryFailure(throwable -> {
            System.out.println("Exceptions thrown from callback onQueryFailure");
            throwable.printStackTrace();
            batchFailResults2.append("Test has Exceptions");
          });
      JobTicket qBatcherJob2 = dmManager.startJob(qBatcher2);
      // Wait for query Batcher to complete and stop Job.
      qBatcher2.awaitCompletion();
      dmManager.stopJob(qBatcherJob2);
      writer1.flush();

      TreeMap<String, String> uriMap = new TreeMap<String, String>();

      String line = null;
      // Verify the writer (file) succeeded.
      freader2 = new FileReader(uriFile);
      UriReaderTxt2 = new BufferedReader(freader2);
      while ((line = UriReaderTxt2.readLine()) != null) {
        System.out.println("Line read from file with URIS is" + line);
        uriMap.put(line, "URI");
      }
      assertTrue(expectedMap.equals(uriMap));
    } catch (Exception ex) {
      System.out.println("Exceptions from testPOJOUris method is" + ex.getMessage());
    } finally {
      try {
        if (writer1 != null)
          writer1.close();
        if (UriReaderTxt2 != null)
          UriReaderTxt2.close();
        if (freader2 != null)
          freader2.close();
        // Delete the file on JVM exit
        File file = new File(uriFile);
        file.deleteOnExit();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * To test UriToWriterListener with POJO objects' URIs POJOs are stored in
   * Database using JacksonDataBindHandle Read the POJO rep from DB back into
   * object and validate the object. Use the URI from map. Similar to
   * testPOJOUris method
   */
  @Test
  public void testPOJOUrisUsingDataBindHandle() throws IOException, ParserConfigurationException, SAXException, InterruptedException
  {
    System.out.println("Running testPOJOUrisUsingDataBindHandle");
    String uriFile = "testPOJOUrisUsingDataBindHandle.txt";
    String collection = "testPOJOUrisUsingDataBindHandle";
    String docId[] = { "/iphone.json", "/imac.json", "/ipad.json" };

    StringBuilder wbatchResults = new StringBuilder();
    StringBuilder wbatchFailResults = new StringBuilder();

    TreeMap<String, String> expectedMap = new TreeMap<String, String>();
    FileWriter writer = null;
    BufferedReader UriReaderTxt = null;
    FileReader freader = null;

    try {
      Product newProduct1 = new Product();
      newProduct1.setName("iPhone 6");
      newProduct1.setIndustry("Mobile Phone");
      newProduct1.setDescription("New iPhone 6");

      Product newProduct2 = new Product();
      newProduct2.setName("iMac");
      newProduct2.setIndustry("Desktop");
      newProduct2.setDescription("Air Book OS X");

      Product newProduct3 = new Product();
      newProduct3.setName("iPad");
      newProduct3.setIndustry("Tablet");
      newProduct3.setDescription("iPad Mini");

      // Create a content Factory from JacksonDatabindHandle that will handle
      // POJO class type.
      ContentHandleFactory ch = JacksonDatabindHandle.newFactory(Product.class);

      // Instantiate a handle for each POJO instance.
      JacksonDatabindHandle<Product> handle1 = (JacksonDatabindHandle<Product>) ch.newHandle(Product.class);
      JacksonDatabindHandle<Product> handle2 = (JacksonDatabindHandle<Product>) ch.newHandle(Product.class);
      JacksonDatabindHandle<Product> handle3 = (JacksonDatabindHandle<Product>) ch.newHandle(Product.class);

      // Assigns the custom POJO as the content.
      handle1.set(newProduct1);
      handle2.set(newProduct2);
      handle3.set(newProduct3);

      // Specifies the format of the content.
      handle1.withFormat(Format.JSON);
      handle2.withFormat(Format.JSON);
      handle3.withFormat(Format.JSON);
      // Use a collection1 to querydef for QueryBatcher.
      DocumentMetadataHandle metadata = new DocumentMetadataHandle()
          .withCollections(collection)
          .withQuality(100);

      // Use WriteBatcher to write files.
      WriteBatcher wbatcher = dmManager.newWriteBatcher();
      wbatcher.withBatchSize(2).withThreadCount(1);

      wbatcher.onBatchSuccess(
          batch -> {
            for (WriteEvent w : batch.getItems()) {
              wbatchResults.append(w.getTargetUri() + ":");
            }
          }
          )
          .onBatchFailure(
              (batch, throwable) -> {
                throwable.printStackTrace();
                for (WriteEvent w : batch.getItems()) {
                  System.out.println("Failed URI's from Writebatcher are" + w.getTargetUri());
                  wbatchFailResults.append(w.getTargetUri() + ":");
                }
              });
      wbatcher.add(docId[0], metadata, handle1);
      wbatcher.add(docId[1], metadata, handle2);
      wbatcher.add(docId[2], metadata, handle3);
      dmManager.startJob(wbatcher);
      wbatcher.flushAndWait();
      wbatcher.awaitCompletion();
      if (wbatchFailResults.length() > 0 || wbatchResults.toString().split(":").length != 3) {
        System.out.println("Success URI's from Write batcher: " + wbatchResults.toString());
        System.out.println("Failure URI's from Write batcher: " + wbatchFailResults.toString());
        fail("Test failed due to errors in write batcher");
      }
      writer = new FileWriter(uriFile);
      StructuredQueryDefinition querydef1 = new StructuredQueryBuilder().collection(collection);

      // Run a QueryBatcher on the new URIs.
      StringBuilder batchResults = new StringBuilder();
      StringBuilder batchFailResults = new StringBuilder();

      QueryBatcher qBatcher = dmManager.newQueryBatcher(querydef1);
      qBatcher.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchResults.append(str)
              .append('|');
        }
      }).onUrisReady(new UrisToWriterListener(writer))
          .onQueryFailure(throwable -> {
            System.out.println("Exceptions thrown from callback onQueryFailure");
            throwable.printStackTrace();
            batchFailResults.append("Test has Exceptions");
          });
      JobTicket qBatcherJob = dmManager.startJob(qBatcher);
      // Wait for query Batcher to complete and stop Job.
      qBatcher.awaitCompletion();
      dmManager.stopJob(qBatcherJob);
      writer.flush();

      expectedMap.put(docId[0], "URI");
      expectedMap.put(docId[1], "URI");
      expectedMap.put(docId[2], "URI");

      TreeMap<String, String> uriMap = new TreeMap<String, String>();

      String line = null;
      // Verify the writer (file) succeeded.
      freader = new FileReader(uriFile);
      UriReaderTxt = new BufferedReader(freader);
      while ((line = UriReaderTxt.readLine()) != null) {
        System.out.println("Line read from file with URIS is" + line);
        uriMap.put(line, "URI");
      }
      assertTrue(expectedMap.equals(uriMap));
      JSONDocumentManager docMgr = clientQHB.newJSONDocumentManager();
      // Read it back into JacksonDatabindHandle Product
      JacksonDatabindHandle<Product> jacksonDBReadHandle = new JacksonDatabindHandle<Product>(Product.class);
      docMgr.read(uriMap.firstKey().trim(), jacksonDBReadHandle);
      Product product2 = (Product) jacksonDBReadHandle.get();

      // Validate the first POJO
      assertTrue(product2.getName().equalsIgnoreCase("iMac"));
      assertTrue(product2.getIndustry().equalsIgnoreCase("Desktop"));
      assertTrue(product2.getDescription().equalsIgnoreCase("Air Book OS X"));
    } catch (Exception ex) {
      System.out.println("Exceptions from testPOJOUrisUsingDataBindHandle method is" + ex.getMessage());
    } finally {
      try {
        if (writer != null)
          writer.close();
        if (UriReaderTxt != null)
          UriReaderTxt.close();
        if (freader != null)
          freader.close();
        // Delete the file on JVM exit
        File file = new File(uriFile);
        file.deleteOnExit();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * To test if UriToWriterListener accepts OutputListener Verify if multiple
   * listeners do not overwrite previously registered listeners. Tests Git Issue
   * # 573
   */
  @Test
  public void testMultipleOutputListeners() throws IOException, ParserConfigurationException, SAXException, InterruptedException
  {
    System.out.println("Running testMultipleOutputListeners");
    String uriFile1 = "testMultipleOutputListeners1.txt";

    TreeMap<String, String> expectedMap1 = new TreeMap<String, String>();
    TreeMap<String, String> expectedMap2 = new TreeMap<String, String>();
    PojoRepository<Artifact, Long> products = clientQHB.newPojoRepository(Artifact.class, Long.class);
    FileWriter writer1 = null;

    FileReader freader1 = null, freader2 = null;
    BufferedReader UriReaderTxt1 = null, UriReaderTxt2 = null;
    try {
      // Populate POJOs in database and get expected URIs into the map for the
      // assert.
      String popStr = null;
      for (int i = 1; i < 11; i++) {
        if (i % 2 == 0) {
          products.write(this.getArtifact(i), "even", "numbers");
        }
        else {
          products.write(this.getArtifact(i), "odd", "numbers");
        }
        popStr = "com.marklogic.client.functionaltest.Artifact/" + i + ".json";
        expectedMap1.put(popStr, "URI");
        expectedMap2.put("QA Func " + popStr, "URI");
      }
      QueryManager queryMgr = clientQHB.newQueryManager();
      StringQueryDefinition qd = queryMgr.newStringDefinition();
      qd.setCriteria("cogs");

      // Use QueryBatcher and UrisToWriterListener
      writer1 = new FileWriter(uriFile1);
      // Initialize write for MyOutputListener
      writer2 = new FileWriter(uriFile2);

      // Run a QueryBatcher on the new URIs.
      StringBuilder batchResults2 = new StringBuilder();
      StringBuilder batchFailResults2 = new StringBuilder();

      QueryBatcher qBatcher1 = dmManager.newQueryBatcher(qd);
      qBatcher1.withBatchSize(1); // batch > 1 makes MyOutput listener write
                                  // garbled uris into the file.
      UrisToWriterListener listener1 = new UrisToWriterListener(writer1);

      qBatcher1.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchResults2.append(str)
              .append('|');
        }
      }).onUrisReady(listener1.onGenerateOutput(new MyOutputListener()))
          .onQueryFailure(throwable -> {
            System.out.println("Exceptions thrown from callback onQueryFailure");
            throwable.printStackTrace();
            batchFailResults2.append("Test has Exceptions");
          });

      JobTicket qBatcherJob2 = dmManager.startJob(qBatcher1);
      // Wait for query Batcher to complete and stop Job.
      qBatcher1.awaitCompletion();
      dmManager.stopJob(qBatcherJob2);
      writer1.flush();
      writer2.flush();

      TreeMap<String, String> uriMap1 = new TreeMap<String, String>();
      TreeMap<String, String> uriMap2 = new TreeMap<String, String>();
      String line = null;
      // Verify the writer1 (file) succeeded.
      freader1 = new FileReader(uriFile1);
      UriReaderTxt1 = new BufferedReader(freader1);
      while ((line = UriReaderTxt1.readLine()) != null) {
        System.out.println("Line read from file with URIS is " + line);
        uriMap1.put(line, "URI");
      }
      assertTrue(expectedMap1.equals(uriMap1));

      // Verify the MyOutputListener (file) succeeded.
      freader2 = new FileReader(uriFile2);
      UriReaderTxt2 = new BufferedReader(freader2);
      while ((line = UriReaderTxt2.readLine()) != null) {
        System.out.println("Line read from file with MyOutputListener URIS is " + line);
        uriMap2.put(line, "URI");
      }
      assertTrue(expectedMap2.equals(uriMap2));
    } catch (Exception ex) {
      System.out.println("Exceptions from testMultipleOutputListeners method is " + ex.getMessage());
    } finally {
      try {
        if (writer1 != null)
          writer1.close();
        if (writer2 != null)
          writer2.close();
        if (UriReaderTxt1 != null)
          UriReaderTxt1.close();
        if (UriReaderTxt2 != null)
          UriReaderTxt2.close();
        if (freader1 != null)
          freader1.close();
        if (freader2 != null)
          freader2.close();
        // Delete the file on JVM exit
        File file1 = new File(uriFile1);
        file1.deleteOnExit();
        File file2 = new File(uriFile2);
        file2.deleteOnExit();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * To test Prefix and suffix on UriToWriterListener writer.
   */
  @Test
  public void testPrefixAndSuffix() throws IOException, ParserConfigurationException, SAXException, InterruptedException
  {
    System.out.println("Running testPrefixAndSuffix");
    StringBuilder wbatchResults = new StringBuilder();
    StringBuilder wbatchFailResults = new StringBuilder();
    // file name to hold uris written out
    String uriFile = "testPrefixAndSuffix.txt";
    String docId = null;
    String collection = "MultipleFileTypes";
    String dataFileDir = dataConfigDirPath + "/data/";

    FileWriter writer = null;
    FileReader freader = null;
    BufferedReader UriReaderTxt = null;

    try {
      // Use a collection to querydef for QueryBatcher.
      DocumentMetadataHandle metadata = new DocumentMetadataHandle()
          .withCollections(collection)
          // .withProperty("SomeCollection", "true")
          .withQuality(100);
      // Use WriteBatcher to write files.
      WriteBatcher wbatcher = dmManager.newWriteBatcher();
      wbatcher.withBatchSize(2).withThreadCount(1);

      wbatcher.onBatchSuccess(
          batch -> {
            for (WriteEvent w : batch.getItems()) {
              wbatchResults.append(w.getTargetUri() + ":");
            }
          }
          )
          .onBatchFailure(
              (batch, throwable) -> {
                throwable.printStackTrace();
                for (WriteEvent w : batch.getItems()) {
                  System.out.println("Failed URI's from Writebatcher are" + w.getTargetUri());
                  wbatchFailResults.append(w.getTargetUri() + ":");
                }
              });
      dmManager.startJob(wbatcher);
      // Add file to batcher
      InputStreamHandle contentHandle1 = new InputStreamHandle();
      contentHandle1.set(new FileInputStream(new File(dataFileDir + "product-microsoft.json"
          )
          )
          );
      wbatcher.add("/product-microsoft.json", metadata, contentHandle1);

      wbatcher.flushAndWait();
      wbatcher.awaitCompletion();
      if (wbatchFailResults.length() > 0 || wbatchResults.toString().split(":").length != 1) {
        System.out.println("Success URI's from Write batcher : " + wbatchResults.toString());
        System.out.println("Failure URI's from Write batcher : " + wbatchFailResults.toString());
        fail("Test failed due to errors in write batcher");
      }
      writer = new FileWriter(uriFile);
      StructuredQueryDefinition querydef = new StructuredQueryBuilder().collection(collection);
      // Run a QueryBatcher on the new URIs.
      StringBuilder batchResults = new StringBuilder();
      StringBuilder batchFailResults = new StringBuilder();

      QueryBatcher qBatcher = dmManager.newQueryBatcher(querydef);
      qBatcher.onUrisReady(batch -> {
        for (String str : batch.getItems()) {
          batchResults.append(str)
              .append('|');
        }
      }).onUrisReady(new UrisToWriterListener(writer).withRecordPrefix("ML9").withRecordSuffix("Great"))
          .onQueryFailure(throwable -> {
            System.out.println("Exceptions thrown from callback onQueryFailure");
            throwable.printStackTrace();
            batchFailResults.append("Test has Exceptions");
          });
      JobTicket qBatcherJob = dmManager.startJob(qBatcher);
      // Wait for query Batcher to complete and stop Job.
      qBatcher.awaitCompletion();
      dmManager.stopJob(qBatcherJob);
      writer.flush();

      // Verify the writer (file) succeeded.
      freader = new FileReader(uriFile);
      UriReaderTxt = new BufferedReader(freader);
      String line = "";
      while ((line = UriReaderTxt.readLine()) != null) {
        System.out.println("Line read from file with URIS is" + line);
        docId = line.trim();
      }
      assertTrue(docId.contains("ML9/product-microsoft.jsonGreat"));
    } catch (Exception ex) {
      System.out.println("Exceptions thrown from testPrefixAndSuffix method" + ex.getMessage());
    } finally {
      try {
        if (writer != null)
          writer.close();
        if (UriReaderTxt != null)
          UriReaderTxt.close();
        if (freader != null)
          freader.close();
        // Delete the file on JVM exit
        File file = new File(uriFile);
        file.deleteOnExit();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * To test if URI from UriToWriterListener writer OnBatchfailure. Call sleep
   * on OnGenerate Get rid of URIListener's writer while URIListener is sleeping
   * Failure to write will trigger OnBatchFailure callback assert on failure
   * buffer contents.
   */
  @Test()
  public void testOnBatchFailure() throws IOException, ParserConfigurationException, SAXException, InterruptedException
  {
    System.out.println("Running testOnBatchFailure");
    StringBuilder wbatchResults = new StringBuilder();
    StringBuilder wbatchFailResults = new StringBuilder();

    StringBuilder batchResults = new StringBuilder();
    StringBuilder batchFailResults = new StringBuilder();

    // File name to hold uris written out
    String uriFile = "testOnBatchfailure.txt";

    String jsonDoc = "{" +
        "\"employees\": [" +
        "{ \"firstName\":\"John\" , \"lastName\":\"Doe\" }," +
        "{ \"firstName\":\"Ann\" , \"lastName\":\"Smith\" }," +
        "{ \"firstName\":\"Bob\" , \"lastName\":\"Foo\" }]" +
        "}";
    // create query def
    try {
      QueryManager queryMgr = clientQHBTmp.newQueryManager();
      StringQueryDefinition querydef = queryMgr.newStringDefinition();
      querydef.setCriteria("John AND Bob");

      WriteBatcher wbatcher = dmManagerTmp.newWriteBatcher();
      wbatcher.withBatchSize(100)
          .onBatchSuccess(
              batch -> {
                for (WriteEvent w : batch.getItems()) {
                  wbatchResults.append(w.getTargetUri() + ":");
                }
              }
          )
          .onBatchFailure(
              (batch, throwable) -> {
                throwable.printStackTrace();
                for (WriteEvent w : batch.getItems()) {
                  System.out.println("Failed URI's from Writebatcher are" + w.getTargetUri());
                  wbatchFailResults.append(w.getTargetUri() + ":");
                }
              });
      StringHandle handle = new StringHandle();
      handle.set(jsonDoc);
      String uri = null;

      // Insert 100 documents
      for (int i = 0; i < 100; i++) {
        uri = "/firstName" + i + ".json";
        wbatcher.add(uri, handle);
      }
      dmManagerTmp.startJob(wbatcher);
      wbatcher.awaitCompletion();
      if (wbatchFailResults.length() > 0) {
        fail("Test failed due to errors in write batcher");
      }

      FileWriter writer = new FileWriter(uriFile);

      QueryBatcher qBatcher = dmManagerTmp.newQueryBatcher(querydef);
      qBatcher.withBatchSize(100)
          .onUrisReady(new UrisToWriterListener(writer)
              .onFailure((batch, throwable) -> {
                batchFailResults.append("QA OnBatchFailure Event");
                System.out.println("Exception thrown from onBatchFailure == " + throwable.getMessage());
              })
              .onGenerateOutput(record -> {
                try {
                  Thread.sleep(5000);
                } catch (Exception e) {
                  e.printStackTrace();
                }
                return "";
              }))
          .onUrisReady(batch -> {
            for (String str : batch.getItems()) {
              batchResults.append(str)
                  .append('|');
            }
          })
          .onQueryFailure(throwable -> {
            System.out.println("Exceptions thrown from callback onQueryFailure");
            throwable.printStackTrace();
            batchFailResults.append(throwable.toString());
            System.out.println("Contents of batchFailResults is  " + batchFailResults.toString());
          });
      JobTicket qBatcherJob = dmManagerTmp.startJob(qBatcher);
      // Get rid of the writer to trigger on batch failure
      writer.close();

      // Wait for query Batcher to complete and stop Job.
      qBatcher.awaitCompletion();
      dmManagerTmp.stopJob(qBatcherJob);
    } catch (Exception e) {
      System.out.println(e.toString());
    } finally {
      try {
        // Delete the file on JVM exit
        File file = new File(uriFile);
        file.deleteOnExit();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    System.out.println("Batch Failure contents are " + batchFailResults.toString());
    assertTrue(batchFailResults.toString().contains("QA OnBatchFailure Event"));
  }

  public Artifact getArtifact(int counter) {

    Artifact cogs = new Artifact();
    cogs.setId(counter);
    if (counter % 5 == 0) {
      cogs.setName("Cogs special");
      if (counter % 2 == 0) {
        Company acme = new Company();
        acme.setName("Acme special, Inc.");
        acme.setWebsite("http://www.acme special.com");
        acme.setLatitude(41.998 + counter);
        acme.setLongitude(-87.966 + counter);
        cogs.setManufacturer(acme);

      } else {
        Company widgets = new Company();
        widgets.setName("Widgets counter Inc.");
        widgets.setWebsite("http://www.widgets counter.com");
        widgets.setLatitude(41.998 + counter);
        widgets.setLongitude(-87.966 + counter);
        cogs.setManufacturer(widgets);
      }
    } else {
      cogs.setName("Cogs " + counter);
      if (counter % 2 == 0) {
        Company acme = new Company();
        acme.setName("Acme " + counter + ", Inc.");
        acme.setWebsite("http://www.acme" + counter + ".com");
        acme.setLatitude(41.998 + counter);
        acme.setLongitude(-87.966 + counter);
        cogs.setManufacturer(acme);

      } else {
        Company widgets = new Company();
        widgets.setName("Widgets " + counter + ", Inc.");
        widgets.setWebsite("http://www.widgets" + counter + ".com");
        widgets.setLatitude(41.998 + counter);
        widgets.setLongitude(-87.966 + counter);
        cogs.setManufacturer(widgets);
      }
    }
    cogs.setInventory(1000 + counter);
    return cogs;
  }

  class MyOutputListener implements UrisToWriterListener.OutputListener {
    @Override
    public String generateOutput(String uri) {
      // Make sure this listener is ALSO getting called in addition to use of
      // writer (writing URIs to a file)
      // Do some thing with the URI.
      String modUri = "QA Func " + uri;
      String lineSep = System.getProperty("line.separator");
      System.out.println("Output from MyListener is " + modUri);
      try {
        // Write modified URI into second file.
        writer2.append(modUri);
        writer2.append(lineSep);
      } catch (IOException e) {
        System.out.println("IO Exception from MyListener is " + e.getMessage());
      }
      return uri;
    }
  }
}
