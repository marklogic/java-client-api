/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest.datamovement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

import com.marklogic.client.fastfunctest.AbstractFunctionalTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.datamovement.ExportListener;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class ExportListenerTest extends AbstractFunctionalTest {

  private static DataMovementManager dmManager = null;
  private static DatabaseClient dbClient;

  private static final String query1 = "fn:count(fn:doc())";

  @BeforeEach
  public void setUp() throws Exception {
	  dbClient = client;
	  dmManager = client.newDataMovementManager();
	  deleteDocuments(client);

    String jsonDoc = "{" +
        "\"employees\": [" +
        "{ \"firstName\":\"John\" , \"lastName\":\"Doe\" }," +
        "{ \"firstName\":\"Ann\" , \"lastName\":\"Smith\" }," +
        "{ \"firstName\":\"Bob\" , \"lastName\":\"Foo\" }]" +
        "}";

    WriteBatcher wbatcher = dmManager.newWriteBatcher();
    StringHandle handle = new StringHandle();
    handle.set(jsonDoc);
    String uri = null;
    for (int i = 0; i < 100; i++) {
      uri = "firstName" + i + ".json";
      wbatcher.add(uri, handle);
    }
    wbatcher.flushAndWait();
  }

  /*
   * This test verifies that DMSDK supports PointInTime query and export using
   * ExportListener. The result returned is deterministic, since we have a
   * delete operation running when 1) query batcher is setup with
   * ConsistentSnapshot and 2) Listener is also setup with ConsistentSnapshot.
   * 3) Second query batcher after delete listener should return 0 uris.
   */
  @Test
  public void testPointInTimeQueryDeterministicSet() {
    List<String> docExporterList = Collections.synchronizedList(new ArrayList<String>());
    List<String> batcherList2 = Collections.synchronizedList(new ArrayList<String>());

    QueryManager queryMgr = dbClient.newQueryManager();
    StringQueryDefinition querydef = queryMgr.newStringDefinition();
    querydef.setCriteria("John AND Bob");

    StringQueryDefinition querydef2 = queryMgr.newStringDefinition();
    querydef2.setCriteria("John AND Bob");
    StringBuffer batchResults = new StringBuffer();

      // Listener IS setup with withConsistentSnapshot()
      ExportListener exportListener = new ExportListener();
      exportListener.withConsistentSnapshot()
          .onDocumentReady(doc -> {
            String uriOfDoc = doc.getUri();
            System.out.println("URIs from Export " + uriOfDoc);
            // Make sure the docs are available. Not deleted from DB.
              docExporterList.add(uriOfDoc);
            }
          );

      QueryBatcher exportBatcher = dmManager.newQueryBatcher(querydef)
          .withConsistentSnapshot()
          .withBatchSize(10)
          .onUrisReady(exportListener)
          .onUrisReady(new DeleteListener())
          .onQueryFailure(exception -> {
            System.out.println("Exceptions thrown from exportBatcher callback onQueryFailure");
            exception.printStackTrace();
          });
      dmManager.startJob(exportBatcher);
      exportBatcher.awaitCompletion();

      ExportListener exportListener2 = new ExportListener();
      exportListener2.withConsistentSnapshot()
          .onDocumentReady(doc -> {
            String uriOfDoc = doc.getUri();
            batcherList2.add(uriOfDoc);
          }
          );

      // Run a second batcher, note that DeleteListener has done its work.
      // Currently the DB snapshot is at a point, where we do not have any docs.
      QueryBatcher batcher2 = dmManager.newQueryBatcher(querydef2)
          .withConsistentSnapshot()
          .withBatchSize(100)
          .onUrisReady(exportListener2)
          .onQueryFailure(exception -> {
            System.out.println("Exceptions thrown from batcher2 callback onQueryFailure");
            exception.printStackTrace();
          });

      dmManager.startJob(batcher2);
      batcher2.awaitCompletion();

	  System.out.println("Batch" + batchResults.toString());

    assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
    System.out.println("List size from second QueryBatcher is " + batcherList2.size());
    System.out.println("List size from Export Listener is " + docExporterList.size());

    assertEquals(0, batcherList2.size());
    assertEquals(100, docExporterList.size());
  }

  /*
   * This test verifies that DMSDK supports PointInTime query and export using
   * ExportListener. The result returned is deterministic, since we have a
   * delete operation running when 1) query batcher is setup with
   * ConsistentSnapshot and 2) Listener is setup with ConsistentSnapshot.
   */
  @Test
  public void testWithSnapshots() {
    List<String> docExporterList = Collections.synchronizedList(new ArrayList<String>());
    List<String> batcherList = Collections.synchronizedList(new ArrayList<String>());

    QueryManager queryMgr = dbClient.newQueryManager();
    StringQueryDefinition querydef = queryMgr.newStringDefinition();
    querydef.setCriteria("John AND Bob");
    StringBuffer batchResults = new StringBuffer();

    try {
      // Listener is setup with withConsistentSnapshot()
      ExportListener exportListener = new ExportListener();
      exportListener
          .withConsistentSnapshot()
          .onDocumentReady(doc -> {
            String uriOfDoc = doc.getUri();
            // Make sure the docs are available. Not deleted from DB.
            System.out.println("Document exported is " + uriOfDoc);
              docExporterList.add(uriOfDoc);
            }
          );

      QueryBatcher exportBatcher = dmManager.newQueryBatcher(querydef)
          .withConsistentSnapshot()
          .withBatchSize(10)
          .onUrisReady(exportListener)
          .onUrisReady(batch -> {
            if (batch.getJobBatchNumber() == 1) {
              // Verifying getServerTimestamp with withConsistentSnapshot - Git
              // Issue 629.
              System.out.println("Server Time from Batch 1 in exportBatcher is " + batch.getServerTimestamp());
              assertTrue(batch.getServerTimestamp() > 0);
            }
            for (String u : batch.getItems()) {
              batchResults.append(u);
              batcherList.add(u);
            }
            batchResults.append("|");
            System.out.println("Batch Numer is " + batch.getJobBatchNumber());
            if (batch.getJobBatchNumber() == 1) {
              // Attempt to read firstName11 from database
              DocumentManager docMgr = dbClient.newJSONDocumentManager();
              JacksonHandle jacksonhandle = new JacksonHandle();
              docMgr.read("firstName11.json", jacksonhandle);
              JsonNode node = jacksonhandle.get();
              // 3 nodes available
              assertEquals(3, node.path("employees").size());
              assertEquals("John", node.path("employees").get(0).path("firstName").asText());
              assertEquals("Ann", node.path("employees").get(1).path("firstName").asText());
              assertEquals("Bob", node.path("employees").get(2).path("firstName").asText());
            }
			// This test used to sleep for 5 seconds for unknown reasons.
          })
          .onQueryFailure(exception -> {
            System.out.println("Exceptions thrown from testPointInTimeQueryDeterministicSet callback onQueryFailure");
            exception.printStackTrace();
          });
      dmManager.startJob(exportBatcher);

      QueryBatcher deleteBatcher = dmManager.newQueryBatcher(querydef)
          .withConsistentSnapshot()
          .withBatchSize(100)
          .onUrisReady(batch -> {
              if (batch.getJobBatchNumber() == 1) {
                System.out.println("Server Time from Batch 1 in deleteBatcher is " + batch.getServerTimestamp());
                assertTrue(batch.getServerTimestamp() > 0);
              }
            }
          )
          .onUrisReady(new DeleteListener());

      dmManager.startJob(deleteBatcher);
      deleteBatcher.awaitCompletion();

      exportBatcher.awaitCompletion();
    } catch (Exception ex) {
      System.out.println("Exceptions from testPointInTimeQueryDeterministicSet method is" + ex.getMessage());
    }

    System.out.println("Batch" + batchResults.toString());

    System.out.println("List size from QueryBatcher is " + batcherList.size());
    System.out.println("List size from Export Listener is " + docExporterList.size());

    assertEquals(100, batcherList.size());
    assertEquals(100, docExporterList.size());

    // Doc count should be zero after both batchers are done.
    assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
  }

  // Verify getServerTimestamp on the batch with no ConsistentSnapshot - Git
  // Issue 629
  @Test
  public void testServerTimestampNoSnapshots() {
    List<String> docExporterList = Collections.synchronizedList(new ArrayList<String>());

    QueryManager queryMgr = dbClient.newQueryManager();
    StringQueryDefinition querydef = queryMgr.newStringDefinition();
    querydef.setCriteria("John AND Bob");

      // Listener is setup with no withConsistentSnapshot()
      ExportListener exportListener = new ExportListener();
      exportListener
          .onDocumentReady(doc -> {
            String uriOfDoc = doc.getUri();
            // Make sure the docs are available. Not deleted from DB.
          docExporterList.add(uriOfDoc);
        }
          );
      // Batcher is setup with no withConsistentSnapshot()
      QueryBatcher exportBatcher = dmManager.newQueryBatcher(querydef)
          .withBatchSize(50)
          .onUrisReady(exportListener)
          .onUrisReady(batch -> {
            System.out.println("Batch # is " + batch.getJobBatchNumber());
            System.out.println("Server Time from Batch is " + batch.getServerTimestamp());
            if (batch.getJobBatchNumber() == 1) {
              assertTrue(batch.getServerTimestamp() < 0);
            }
          })
          .onQueryFailure(exception -> {
            System.out.println("Exceptions thrown from testPointInTimeQueryDeterministicSet callback onQueryFailure");
            exception.printStackTrace();
          });
      dmManager.startJob(exportBatcher);
      exportBatcher.awaitCompletion();
  }

  /*
   * This test verifies that DMSDK supports PointInTime query and export using
   * ExportListener. The result returned is non-deterministic, since we have a
   * delete operation running when 1) query batcher is setup with
   * ConsistentSnapshot and 2) Listener is NOT setup with ConsistentSnapshot.
   */
  @Test
  public void testNoSnapshotOnListener() {
    List<String> docExporterList = new ArrayList<String>();
    List<String> batcherList = new ArrayList<String>();

    QueryManager queryMgr = dbClient.newQueryManager();
    StringQueryDefinition querydef = queryMgr.newStringDefinition();
    querydef.setCriteria("John AND Bob");
    StringBuffer batchResults = new StringBuffer();

      // Listener is NOT setup with withConsistentSnapshot()
      ExportListener exportListener = new ExportListener();
      exportListener.onDocumentReady(doc -> {
        String uriOfDoc = doc.getUri();
        docExporterList.add(uriOfDoc);
      });

      QueryBatcher exportBatcher = dmManager.newQueryBatcher(querydef)
          .withConsistentSnapshot()
          .withBatchSize(10, 1)
          .onUrisReady(exportListener)
          .onUrisReady(batch -> {
            for (String u : batch.getItems()) {
              batchResults.append(u);
              batcherList.add(u);
            }
            batchResults.append("|");
            System.out.println("Batch Numer is " + batch.getJobBatchNumber());
            if (batch.getJobBatchNumber() == 1) {
              // Attempt to read firstName11 from database
              DocumentManager docMgr = dbClient.newJSONDocumentManager();
              JacksonHandle jacksonhandle = new JacksonHandle();
              docMgr.read("firstName11.json", jacksonhandle);
              JsonNode node = jacksonhandle.get();
              // 3 nodes available
              assertEquals(3, node.path("employees").size());
              assertEquals("John", node.path("employees").get(0).path("firstName").asText());
              assertEquals("Ann", node.path("employees").get(1).path("firstName").asText());
              assertEquals("Bob", node.path("employees").get(2).path("firstName").asText());
            }
			// Not known why this is needed.
			waitFor(1000);
          })
          .onQueryFailure(exception -> {
            System.out.println("Exceptions thrown from exportBatcher callback onQueryFailure");
            exception.printStackTrace();
          });
      dmManager.startJob(exportBatcher);

      QueryBatcher deleteBatcher = dmManager.newQueryBatcher(querydef)
          .withConsistentSnapshot()
          .withBatchSize(100, 1)
          .onUrisReady(new DeleteListener());

      dmManager.startJob(deleteBatcher);
      deleteBatcher.awaitCompletion();

      exportBatcher.awaitCompletion();

    System.out.println("Batch" + batchResults.toString());

    System.out.println("List size from QueryBatcher is " + batcherList.size());
    System.out.println("List size from Export Listener is " + docExporterList.size());

    assertEquals(100, batcherList.size());
    assertTrue(docExporterList.size() != 100);

    // Doc count should be zero after both batchers are done.
    assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
  }

  /*
   * This test verifies that DMSDK supports PointInTime query and export using
   * ExportListener. The result returned is non-deterministic, since we have a
   * delete operation running when query batcher is setup with no
   * ConsistentSnapshot. 1) Start query batcher 1 with a export listener. 2)
   * Query batcher 1 waits for 5 seconds 3) Start a second query batcher with
   * delete listener Results should be non deterministic.
   */
  @Test
  public void testPointInTimeQueryNonDeterministicSet() {
    List<String> docExporterList = new ArrayList<String>();
    List<String> batcherList = new ArrayList<String>();

    QueryManager queryMgr = dbClient.newQueryManager();
    StringQueryDefinition querydef = queryMgr.newStringDefinition();
    querydef.setCriteria("John AND Bob");
    StringBuffer batchResults = new StringBuffer();

      ExportListener exportListener = new ExportListener();
      exportListener.onDocumentReady(doc -> {
        String uriOfDoc = doc.getUri();
        // Make sure the docs are available. Not deleted from DB.
          docExporterList.add(uriOfDoc);
        }
          );

      QueryBatcher exportBatcher = dmManager.newQueryBatcher(querydef)
          .withBatchSize(10, 1)
          .onUrisReady(exportListener)
          .onUrisReady(batch -> {
            for (String u : batch.getItems()) {
              batchResults.append(u);
              batcherList.add(u);
            }
            batchResults.append("|");
            System.out.println("Batch Number is " + batch.getJobBatchNumber());
			// Not known why this is needed.
			waitFor(1000);
          })
          .onQueryFailure(exception -> {
            System.out.println("Exceptions thrown from testPointInTimeQueryDeterministicSet callback onQueryFailure");
            exception.printStackTrace();
          });
      dmManager.startJob(exportBatcher);

      QueryBatcher deleteBatcher = dmManager.newQueryBatcher(querydef)
          .withConsistentSnapshot()
          .withBatchSize(100, 1)
          .onUrisReady(new DeleteListener());

      dmManager.startJob(deleteBatcher);
      deleteBatcher.awaitCompletion();

      exportBatcher.awaitCompletion();

    System.out.println("Batch" + batchResults.toString());

    assertTrue(batcherList.size() != 100);
    assertTrue(docExporterList.size() != 100);

    // Doc count should be zero after both batchers are done.
    assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
  }
}
