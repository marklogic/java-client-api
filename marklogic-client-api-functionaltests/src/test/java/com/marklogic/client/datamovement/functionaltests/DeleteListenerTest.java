/*
 * Copyright (c) 2022 MarkLogic Corporation
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.*;
import com.marklogic.client.query.StructuredQueryBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteListenerTest extends BasicJavaClientREST {

  private static String dbName = "DeleteListener";
  private static DataMovementManager dmManager = null;
  private static final String TEST_DIR_PREFIX = "/WriteHostBatcher-testdata/";

  private static DatabaseClient dbClient;
  private static String user = "admin";
  private static int port = 8000;
  private static String password = "admin";
  private static String server = "App-Services";
  private static JacksonHandle jacksonHandle;
  private static StringHandle stringHandle;
  private static FileHandle fileHandle;

  private static DocumentMetadataHandle meta;

  private static String stringTriple;
  private static File fileJson;
  private static JsonNode jsonNode;
  private static final String query1 = "fn:count(fn:doc())";
  private static String[] hostNames;
  private static int forestCount = 1;

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    loadGradleProperties();
    server = getRestAppServerName();
    port = getRestAppServerPort();

    hostNames = getHosts();
    createDB(dbName);
    Thread.currentThread().sleep(500L);
  //Ensure DB has at-least one forest
	createForestonHost(dbName + "-" + forestCount, dbName, hostNames[0]);
	forestCount++;
	for (String forestHost : hostNames) {
		for(int i = 0; i < new Random().nextInt(3); i++) {
			createForestonHost(dbName + "-" + forestCount, dbName, forestHost);
			forestCount++;
		}
		Thread.currentThread().sleep(500L);
	}
    // Create App Server if needed.
 	createRESTServerWithDB(server, port);

    assocRESTServer(server, dbName, port);
    if (IsSecurityEnabled()) {
		enableSecurityOnRESTServer(server, dbName);
	}

    dbClient = getDatabaseClient(user, password, getConnType());
    dmManager = dbClient.newDataMovementManager();

    // JacksonHandle
    jsonNode = new ObjectMapper().readTree("{\"k1\":\"v1\"}");
    jacksonHandle = new JacksonHandle();
    jacksonHandle.set(jsonNode);

    meta = new DocumentMetadataHandle().withCollections("DeleteListener");

    // StringHandle
    stringTriple = "<abc>xml</abc>";
    stringHandle = new StringHandle(stringTriple);
    stringHandle.setFormat(Format.XML);

    // FileHandle
    fileJson = FileUtils.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "dir.json"));
    fileHandle = new FileHandle(fileJson);
    fileHandle.setFormat(Format.JSON);
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    associateRESTServerWithDB(server, "Documents");
    for (int i = 0; i < forestCount -1; i++) {
      System.out.println(dbName + "-" + (i + 1));
      detachForest(dbName, dbName + "-" + (i + 1));
      deleteForest(dbName + "-" + (i + 1));
    }

    deleteDB(dbName);
  }

  @BeforeEach
  public void setUp() throws Exception {
    Thread.currentThread().sleep(1000L);
    WriteBatcher ihb2 = dmManager.newWriteBatcher();
    ihb2.withBatchSize(27).withThreadCount(10);
    dmManager.startJob(ihb2);
    for (int j = 0; j < 2000; j++) {
      String uri = "/local/json-" + j;
      ihb2.add(uri, meta, jacksonHandle);
    }

    ihb2.flushAndWait();
    assertEquals(2000, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
  }

  @AfterEach
  public void tearDown() throws Exception {
    clearDB(port);
  }

  @Test
  public void massDeleteSingleThread() throws Exception {
    HashSet<String> urisList = new HashSet<>();

    assertTrue(urisList.isEmpty());
    assertEquals(2000, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());

    QueryBatcher queryBatcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("DeleteListener"))
        .withBatchSize(11, 1)
        //.withThreadCount(1)
        .onUrisReady(batch -> {
          synchronized (this) {
            urisList.addAll(Arrays.asList(batch.getItems()));
          }
        })
        .onQueryFailure(throwable -> {
          System.out.println("Exceptions thrown from callback onQueryFailure");
          throwable.printStackTrace();

        });

    JobTicket ticket = dmManager.startJob(queryBatcher);
    queryBatcher.awaitCompletion();
    dmManager.stopJob(ticket);

    Thread.currentThread().sleep(2000L);
    assertEquals(2000, urisList.size());

    AtomicInteger successDocs = new AtomicInteger();
    HashSet<String> uris2 = new HashSet<>();
    StringBuffer failures2 = new StringBuffer();

    QueryBatcher deleteBatcher = dmManager.newQueryBatcher(urisList.iterator())
        .withBatchSize(23)
        .withThreadCount(1)
        .onUrisReady(new DeleteListener())
        .onUrisReady(batch -> successDocs.addAndGet(batch.getItems().length))
        .onUrisReady(batch -> uris2.addAll(Arrays.asList(batch.getItems())))
        .onQueryFailure(throwable -> {
          throwable.printStackTrace();
          failures2.append("ERROR:[" + throwable + "]\n");
        });

    JobTicket delTicket = dmManager.startJob(deleteBatcher);
    deleteBatcher.awaitCompletion();
    dmManager.stopJob(delTicket);

    if (failures2.length() > 0)
      fail(failures2.toString());
    assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
  }

  @Test
  public void massDeleteMultipleThreads() throws Exception {

    HashSet<String> urisList = new HashSet<>();

    QueryBatcher queryBatcher = dmManager.newQueryBatcher(
        new StructuredQueryBuilder().collection("DeleteListener"))
        .withBatchSize(11)
        .withThreadCount(11)
        .onUrisReady(batch -> {
          synchronized (this) {
            urisList.addAll(Arrays.asList(batch.getItems()));
          }

        })
        .onQueryFailure(throwable -> {
          System.out.println("Exceptions thrown from callback onQueryFailure");
          throwable.printStackTrace();

        });

    JobTicket ticket = dmManager.startJob(queryBatcher);
    queryBatcher.awaitCompletion();
    dmManager.stopJob(ticket);

    assertEquals(2000, urisList.size());

    AtomicInteger successDocs = new AtomicInteger();
    HashSet<String> uris2 = new HashSet<>();
    StringBuffer failures2 = new StringBuffer();

    QueryBatcher deleteBatcher = dmManager.newQueryBatcher(urisList.iterator())
        .withBatchSize(119)
        .withThreadCount(11)
        .onUrisReady(new DeleteListener())
        .onUrisReady(batch -> successDocs.addAndGet(batch.getItems().length))
        .onUrisReady(batch -> uris2.addAll(Arrays.asList(batch.getItems())))
        .onQueryFailure(throwable -> {
          throwable.printStackTrace();
          failures2.append("ERROR:[" + throwable + "]\n");
        });

    JobTicket delTicket = dmManager.startJob(deleteBatcher);
    deleteBatcher.awaitCompletion();
    dmManager.stopJob(delTicket);

    if (failures2.length() > 0)
      fail(failures2.toString());
    assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
  }

  @Test
  public void massDeleteConsistentSnapShot() throws Exception {
    Map<String, String> props = new HashMap<String, String>();
    props.put("merge-timestamp", "-6000000000");
    changeProperty(props, "/manage/v2/databases/" + dbName + "/properties");
    Thread.currentThread().sleep(5000L);

    QueryBatcher queryBatcher = dmManager.newQueryBatcher(
        new StructuredQueryBuilder().collection("DeleteListener"))
        .withBatchSize(7)
        .withConsistentSnapshot()
        .withThreadCount(5)
        .onUrisReady(new DeleteListener())
        .onQueryFailure(throwable -> {
          System.out.println("Exceptions thrown from callback onQueryFailure");
          throwable.printStackTrace();

        });

    JobTicket ticket = dmManager.startJob(queryBatcher);
    queryBatcher.awaitCompletion();
    dmManager.stopJob(ticket);

    props.put("merge-timestamp", "0");
    changeProperty(props, "/manage/v2/databases/" + dbName + "/properties");
    // if ( failures2.length() > 0 ) fail(failures2.toString());
    assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
  }

  @Test
  public void deleteNonExistentDoc() throws Exception {

    HashSet<String> urisList = new HashSet<>();
    // add a non existent doc uri
    urisList.add("/abc/nonexistent");

    AtomicInteger successDocs = new AtomicInteger();
    HashSet<String> uris2 = new HashSet<>();
    StringBuffer failures2 = new StringBuffer();

    QueryBatcher deleteBatcher = dmManager.newQueryBatcher(urisList.iterator())
        .withBatchSize(11)
        .withThreadCount(11)
        .onUrisReady(new DeleteListener())
        .onUrisReady(batch -> successDocs.addAndGet(batch.getItems().length))
        .onUrisReady(batch -> uris2.addAll(Arrays.asList(batch.getItems())))
        .onQueryFailure(throwable -> {
          throwable.printStackTrace();
          failures2.append("ERROR:[" + throwable + "]\n");
        });

    JobTicket delTicket = dmManager.startJob(deleteBatcher);
    deleteBatcher.awaitCompletion();
    dmManager.stopJob(delTicket);

    if (failures2.length() > 0)
      fail(failures2.toString());
    assertEquals(2000, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
  }

  // ISSUE 94
  @Test
  public void deleteServerFile() throws Exception {

    Map<String, String> props = new HashMap<String, String>();
    props.put("merge-timestamp", "-6000000000");
    changeProperty(props, "/manage/v2/databases/" + dbName + "/properties");
    Thread.currentThread().sleep(5000L);

    class MyRunnable implements Runnable {
      @Override
      public void run() {
        for (int j = 1999; j >= 200; j--) {
          dbClient.newDocumentManager().delete("/local/json-" + j);
        }
      }
    }
    Thread t1;
    t1 = new Thread(new MyRunnable());

    Set<String> urisList = Collections.synchronizedSet(new HashSet<>());

    QueryBatcher queryBatcher = dmManager.newQueryBatcher(
        new StructuredQueryBuilder().collection("DeleteListener"))
        .withBatchSize(11)
        .withThreadCount(4)
        .withConsistentSnapshot()
        .onUrisReady(batch -> {
          for (String s : batch.getItems()) {
            urisList.add(s);
          }
        })
        .onQueryFailure(throwable -> {
          System.out.println("Exceptions thrown from callback onQueryFailure");
          throwable.printStackTrace();

        });

    t1.start();
    JobTicket ticket = dmManager.startJob(queryBatcher);

    queryBatcher.awaitCompletion();
    t1.join();
    dmManager.stopJob(ticket);

    System.out.println("URI's size " + urisList.size());
    AtomicInteger successDocs = new AtomicInteger();
    Set<String> uris2 = Collections.synchronizedSet(new HashSet<>());
    StringBuffer failures2 = new StringBuffer();

    QueryBatcher deleteBatcher = dmManager.newQueryBatcher(urisList.iterator())
        .withBatchSize(13)
        .withThreadCount(5)
        .onUrisReady(new DeleteListener())
        .onUrisReady(batch -> successDocs.addAndGet(batch.getItems().length))
        .onUrisReady(batch -> uris2.addAll(Arrays.asList(batch.getItems())))
        .onQueryFailure(throwable -> {
          throwable.printStackTrace();
          failures2.append("ERROR:[" + throwable + "]\n");
        });

    JobTicket delTicket = dmManager.startJob(deleteBatcher);
    deleteBatcher.awaitCompletion();
    dmManager.stopJob(delTicket);

    if (failures2.length() > 0)
      fail(failures2.toString());

    assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());

    DocumentPage page = dbClient.newDocumentManager().read("/local/json-1998");
    JacksonHandle dh = new JacksonHandle();
    while (page.hasNext()) {
      DocumentRecord rec = page.next();
      rec.getContent(dh);
      System.out.println("Results are: " + dh.get().get("k1").asText());

    }

    props.put("merge-timestamp", "0");
    changeProperty(props, "/manage/v2/databases/" + dbName + "/properties");
  }

  @Test
  public void deleteEmptyIterator() {

    HashSet<String> urisList = new HashSet<>();

    AtomicInteger successDocs = new AtomicInteger();
    StringBuffer failures2 = new StringBuffer();

    QueryBatcher deleteBatcher = dmManager.newQueryBatcher(urisList.iterator())
        .withBatchSize(11)
        .withThreadCount(11)
        .onUrisReady(new DeleteListener())
        .onUrisReady(batch -> successDocs.addAndGet(batch.getItems().length))
        .onQueryFailure(throwable -> {
          throwable.printStackTrace();
          failures2.append("ERROR:[" + throwable + "]\n");
        });

    JobTicket delTicket = dmManager.startJob(deleteBatcher);
    deleteBatcher.awaitCompletion();
    dmManager.stopJob(delTicket);

    if (failures2.length() > 0)
      fail(failures2.toString());
    assertEquals(0, successDocs.intValue());
    assertEquals(2000, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
  }

  @Test
  public void deleteOnDiskUris() throws Exception {
    String pathname = "uriCache.txt";
    assertEquals(2000, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());

    ArrayList<String> foundUris = getUris();
    ArrayList<String> diskUris = writeUrisToDisk();

    assertEquals(foundUris.size(), diskUris.size());

    File file = new File(pathname);
    assertTrue(file.exists());

    ArrayList<String> deletedUris = deleteDocuments(pathname);

    assertEquals(foundUris.size(), deletedUris.size());
    assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
    file.delete();
  }

  public ArrayList<String> getUris() {

    Set<String> uris = Collections.synchronizedSet(new HashSet<>());

    QueryBatcher getUris = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("DeleteListener"))
        .withBatchSize(5000)
        .onUrisReady(batch -> uris.addAll(Arrays.asList(batch.getItems())))
        .onQueryFailure(exception -> exception.printStackTrace());
    JobTicket getUrisTicket = dmManager.startJob(getUris);
    getUris.awaitCompletion();
    dmManager.stopJob(getUrisTicket);
    return new ArrayList<String>(uris);
  }

  public ArrayList<String> writeUrisToDisk() throws IOException {
    Set<String> uris = Collections.synchronizedSet(new HashSet<>());

    FileWriter writer = new FileWriter("uriCache.txt");
    QueryBatcher getUris = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("DeleteListener"))
        .withBatchSize(100)
        .onUrisReady(new UrisToWriterListener(writer))
        .onUrisReady(batch -> uris.addAll(Arrays.asList(batch.getItems())))
        .onQueryFailure(exception -> exception.printStackTrace());
    JobTicket getUrisTicket = dmManager.startJob(getUris);
    getUris.awaitCompletion();
    dmManager.stopJob(getUrisTicket);
    writer.flush();
    writer.close();

    return new ArrayList<String>(uris);
  }

  public ArrayList<String> deleteDocuments(String file) throws Exception {
    Set<String> uris = Collections.synchronizedSet(new HashSet<String>());

    BufferedReader reader = new BufferedReader(new FileReader(file));
    Iterator<String> itr = reader.lines().iterator();

    QueryBatcher performDelete = dmManager.newQueryBatcher(itr)
        .withThreadCount(5)
        .withBatchSize(99)
        .withConsistentSnapshot()
        .onUrisReady(new DeleteListener())
        .onUrisReady((batch) -> {
          uris.addAll(Arrays.asList(batch.getItems()));
        }
        ).onQueryFailure(exception -> exception.printStackTrace());

    JobTicket ticket = dmManager.startJob(performDelete);
    performDelete.awaitCompletion();

    dmManager.stopJob(ticket);
    reader.close();
    return new ArrayList<String>(uris);
  }
}
