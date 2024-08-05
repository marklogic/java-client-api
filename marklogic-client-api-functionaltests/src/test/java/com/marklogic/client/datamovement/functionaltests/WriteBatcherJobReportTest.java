/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement.functionaltests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.*;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

public class WriteBatcherJobReportTest extends BasicJavaClientREST {

	private static String dbName = "WriteBatcherJobReport";
	private static DataMovementManager dmManager = null;
	private static final String TEST_DIR_PREFIX = "/WriteHostBatcher-testdata/";

	private static DatabaseClient dbClient;
	private static String host = null;
	private static String user = "admin";
	private static int port = 8000;
	private static String password = "admin";
	private static String server = "App-Services";

	private static JacksonHandle jacksonHandle;
	private static StringHandle stringHandle;
	private static FileHandle fileHandle;
	private static DOMHandle domHandle;
	private static DocumentMetadataHandle docMeta1;
	private static DocumentMetadataHandle docMeta2;

	private static WriteBatcher ihbMT;
	private static String[] hostNames;

	private static String stringTriple;
	private static File fileJson;
	private static Document docContent;

	private static JsonNode jsonNode;

	private static JobTicket writeTicket;

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		loadGradleProperties();
		server = getRestAppServerName();
	    port = getRestAppServerPort();

		host = getRestAppServerHostName();
		hostNames = getHosts();
		createDB(dbName);
		Thread.currentThread().sleep(500L);
		int count = 1;
		for (String forestHost : hostNames) {
			createForestonHost(dbName + "-" + count, dbName, forestHost);
			count++;
			Thread.currentThread().sleep(500L);
		}
		// Create App Server if needed.
		createRESTServerWithDB(server, port);

		associateRESTServerWithDB(server, dbName);
		if (IsSecurityEnabled()) {
			enableSecurityOnRESTServer(server, dbName);
		}

		dbClient = getDatabaseClient(user, password, getConnType());
		dmManager = dbClient.newDataMovementManager();

		// JacksonHandle
		jsonNode = new ObjectMapper().readTree("{\"k1\":\"v1\"}");
		jacksonHandle = new JacksonHandle();
		jacksonHandle.set(jsonNode);

		// StringHandle
		stringTriple = "<abc>xml</abc>";
		stringHandle = new StringHandle(stringTriple);
		stringHandle.setFormat(Format.XML);

		// FileHandle
		fileJson = FileUtils.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "dir.json"));
		fileHandle = new FileHandle(fileJson);
		fileHandle.setFormat(Format.JSON);

		// DomHandle
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		docContent = docBuilder.parse(
				FileUtils.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "xml-original-test.xml")));

		domHandle = new DOMHandle();
		domHandle.set(docContent);

		docMeta1 = new DocumentMetadataHandle().withCollections("Sample Collection 1").withProperty("docMeta-1", "true")
				.withQuality(1);
		docMeta1.setFormat(Format.XML);

		docMeta2 = new DocumentMetadataHandle().withCollections("Sample Collection 2").withProperty("docMeta-2", "true")
				.withQuality(0);
		docMeta2.setFormat(Format.XML);
	}

	@AfterAll
	public static void tearDownAfterClass() throws Exception {
		associateRESTServerWithDB(server, "Documents");
		for (int i = 0; i < hostNames.length; i++) {
			System.out.println(dbName + "-" + (i + 1));
			detachForest(dbName, dbName + "-" + (i + 1));
			deleteForest(dbName + "-" + (i + 1));
		}

		deleteDB(dbName);
	}

	@AfterEach
	public void tearDown() throws Exception {

		Map<String, String> props = new HashMap<>();
		props.put("group-id", "Default");
		props.put("view", "status");

		JsonNode output = getState(props, "/manage/v2/servers/" + server).path("server-status")
				.path("status-properties");
		props.clear();
		String s = output.findValue("enabled").get("value").asText();
		System.out.println("S is " + s);
		if (s.trim().equals("false")) {
			props.put("server-name", server);
			props.put("group-name", "Default");
			props.put("enabled", "true");
			changeProperty(props, "/manage/v2/servers/" + server + "/properties");
		}

		clearDB(port);
	}

	@Test
	public void testJobReport() throws Exception {

		final String query1 = "fn:count(fn:doc())";
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);

		WriteBatcher ihb1 = dmManager.newWriteBatcher();

		AtomicBoolean success = new AtomicBoolean(false);
		AtomicBoolean failure = new AtomicBoolean(false);

		ihb1.withBatchSize(10);
		ihb1.onBatchSuccess(batch -> {
			if (dmManager.getJobReport(writeTicket).getSuccessEventsCount() == 10) {
				if (dmManager.getJobReport(writeTicket).getSuccessBatchesCount() == 1) {
					if (dmManager.getJobReport(writeTicket).getFailureEventsCount() == 10) {
						if (dmManager.getJobReport(writeTicket).getFailureBatchesCount() == 1) {
							if (Math.abs(dmManager.getJobReport(writeTicket).getReportTimestamp().getTime().getTime()
									- Calendar.getInstance().getTime().getTime()) < 200) {
								System.out.println(dmManager.getJobReport(writeTicket).getReportTimestamp());
								success.set(true);
							}

						}
					}
				}
			}
		});
		ihb1.onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();
			if (dmManager.getJobReport(writeTicket).getSuccessEventsCount() == 0) {
				if (dmManager.getJobReport(writeTicket).getSuccessBatchesCount() == 0) {
					if (dmManager.getJobReport(writeTicket).getFailureEventsCount() == 10) {
						if (dmManager.getJobReport(writeTicket).getFailureBatchesCount() == 1) {
							if (Math.abs(dmManager.getJobReport(writeTicket).getReportTimestamp().getTime().getTime()
									- Calendar.getInstance().getTime().getTime()) < 200) {
								System.out.println(dmManager.getJobReport(writeTicket).getReportTimestamp());
								failure.set(true);
							}

						}
					}
				}
			}
		});

		writeTicket = dmManager.startJob(ihb1);

		for (int i = 0; i < 10; i++) {
			ihb1.add("", jacksonHandle);
		}

		ihb1.flushAsync();
		ihb1.awaitCompletion();
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);

		for (int i = 0; i < 10; i++) {
			String uri = "/local/json-" + i;
			ihb1.add(uri, jacksonHandle);
		}

		ihb1.flushAsync();
		ihb1.awaitCompletion();

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 10);
		assertTrue(success.get());
		assertTrue(failure.get());

		dmManager.stopJob(ihb1);
		Thread.currentThread().sleep(2000L);

		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		writeTicket = dmManager.startJob(ihb2);

		dmManager.stopJob(ihb2);
	}

	@Test
	public void testJobReportStopJob() throws Exception {

		final String query1 = "fn:count(fn:doc())";
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);

		WriteBatcher ihb1 = dmManager.newWriteBatcher();

		AtomicInteger batchCount = new AtomicInteger(0);

		ihb1.withBatchSize(10);
		ihb1.onBatchSuccess(batch -> {
			batchCount.incrementAndGet();
			if (dmManager.getJobReport(writeTicket).getSuccessEventsCount() > 40) {
				dmManager.stopJob(writeTicket);
			}

		});
		ihb1.onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();

		});

		writeTicket = dmManager.startJob(ihb1);

		for (int i = 0; i < 2000; i++) {
			String uri = "/local/json-" + i;
			try {
				ihb1.add(uri, jacksonHandle);
			} catch (IllegalStateException e) {

			}

		}

		ihb1.awaitCompletion();

		writeTicket = dmManager.startJob(ihb1);
		ihb1.awaitCompletion();

		System.out.println(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == dmManager
				.getJobReport(writeTicket).getSuccessEventsCount());

		dmManager.stopJob(ihb1);
	}

	@Test
	public void testJobReportTimes() throws Exception {

		final String query1 = "fn:count(fn:doc())";
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);

		WriteBatcher ihb1 = dmManager.newWriteBatcher();

		AtomicInteger batchCount = new AtomicInteger(0);

		ihb1.withBatchSize(10);
		ihb1.onBatchSuccess(batch -> {
			batchCount.incrementAndGet();
		});
		ihb1.onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();

		});

		writeTicket = dmManager.startJob(ihb1);

		for (int i = 0; i < 20000; i++) {
			String uri = "/local/json-" + i;
			try {
				ihb1.add(uri, jacksonHandle);
			} catch (IllegalStateException e) {

			}
		}

		ihb1.awaitCompletion();

		writeTicket = dmManager.startJob(ihb1);
		ihb1.awaitCompletion();
		Thread.sleep(5000);

		System.out.println(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());

		dmManager.stopJob(ihb1);
		Calendar startTime1 = ihb1.getJobStartTime();
		System.out.println("Job start time (In Millis) is " + startTime1.getTimeInMillis());

		Calendar endTime1 = ihb1.getJobEndTime();
		System.out.println("Job end time (In Millis) is " + endTime1.getTimeInMillis());

		long diff = endTime1.getTimeInMillis() - startTime1.getTimeInMillis();
		System.out.println("Job time Diff is " + diff );
		assertTrue(diff>0);
		//Verify through Job Report with Job Ticket
		assertEquals(dmManager.getJobReport(writeTicket).getJobStartTime().getTimeInMillis(), startTime1.getTimeInMillis());

		// Test for a QueryBatcher's delete listener job with query def
		QueryManager queryMgr = dbClient.newQueryManager();
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		querydef.setCriteria("k and v");

		AtomicInteger successDocs = new AtomicInteger();
		StringBuffer failures = new StringBuffer();

		QueryBatcher deleteBatcher = dmManager.newQueryBatcher(querydef)
				.withBatchSize(5)
				.withThreadCount(1)
				.onUrisReady(new DeleteListener())
				.onUrisReady(batch -> successDocs.addAndGet(batch.getItems().length))
				.onQueryFailure(throwable -> {
					throwable.printStackTrace();
					failures.append("ERROR:[" + throwable + "]\n");
				});

		JobTicket delTicket = dmManager.startJob(deleteBatcher);

		Calendar delStartTime = deleteBatcher.getJobStartTime();
		System.out.println("Delete Listener Job start time (In Millis) is " + delStartTime.getTimeInMillis());

		deleteBatcher.awaitCompletion();
		dmManager.stopJob(delTicket);

		Calendar delEndTime = deleteBatcher.getJobEndTime();
		System.out.println("Delete Listener Job end time (In Millis) is " + delEndTime.getTimeInMillis());

		long delDiff = delEndTime.getTimeInMillis() - delStartTime.getTimeInMillis();
		System.out.println("Job time Diff is " + delDiff );
		assertTrue(delDiff>0);
	}

	@Test
	public void failureCallback() throws Exception {

		String stringContent1 = "<abc>xml</abc>";
		StringHandle strh1 = new StringHandle(stringContent1);
		strh1.setFormat(Format.BINARY);

		AtomicBoolean succException = new AtomicBoolean(false);
		AtomicBoolean failException = new AtomicBoolean(false);

		final String query1 = "fn:count(fn:doc())";

		WriteBatcher ihb2 = dmManager.newWriteBatcher();

		ihb2.setBatchFailureListeners((batch, throwable) -> {
			try {
				System.out.println("Failure: " + getSummaryReport(getSummaryReport(writeTicket)));
			} catch (IllegalStateException e) {
				failException.set(true);
			}
		});
		ihb2.setBatchSuccessListeners(batch -> {
			try {
				System.out.println("Failure: " + getSummaryReport(getSummaryReport(writeTicket)));
			} catch (IllegalStateException e) {
				succException.set(true);
			}

		});

		ihb2.withBatchSize(10);
		writeTicket = dmManager.startJob(ihb2);

		for (int j = 0; j < 10; j++) {
			String uri = "/global/abc-" + j;
			ihb2.add(uri, strh1);
		}
		for (int j = 0; j < 10; j++) {
			ihb2.add("", strh1);
		}
		ihb2.flushAndWait();

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 10);
		assertTrue(succException.get());
		assertTrue(failException.get());
	}

	@Test
	public void testServerXQueryTransformSuccess() throws Exception {
		final String query1 = "fn:count(fn:doc())";
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);

		TransformExtensionsManager transMgr = dbClient.newServerConfigManager().newTransformExtensionsManager();
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Adding attribute xquery Transform");
		metadata.setDescription("This plugin transforms an XML document by adding attribute to root node");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");
		// get the transform file from add-attr-xquery-transform.xqy
		File transformFile = FileUtils
				.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "add-attr-xquery-transform.xqy"));
		FileHandle transformHandle = new FileHandle(transformFile);
		transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle, metadata);

		ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "English");

		String xmlStr1 = "<?xml  version=\"1.0\" encoding=\"UTF-8\"?><foo>This is so foo</foo>";
		String xmlStr2 = "<?xml  version=\"1.0\" encoding=\"UTF-8\"?><foo>This is so bar</foo>";

		// Use WriteBatcher to write the same files.

		WriteBatcher ihb1 = dmManager.newWriteBatcher();

		AtomicBoolean success = new AtomicBoolean(false);
		AtomicBoolean failure = new AtomicBoolean(false);

		ihb1.withBatchSize(10);
		ihb1.onBatchSuccess(batch -> {
			if (dmManager.getJobReport(writeTicket).getSuccessEventsCount() == 8) {
				if (dmManager.getJobReport(writeTicket).getSuccessBatchesCount() == 1) {
					if (dmManager.getJobReport(writeTicket).getFailureEventsCount() == 0) {
						if (dmManager.getJobReport(writeTicket).getFailureBatchesCount() == 0) {
							if (Math.abs(dmManager.getJobReport(writeTicket).getReportTimestamp().getTime().getTime()
									- Calendar.getInstance().getTime().getTime()) < 200) {
								System.out.println(dmManager.getJobReport(writeTicket).getReportTimestamp());
								success.set(true);
							}

						}
					}
				}
			}
		});
		ihb1.onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();
			failure.set(true);
		});

		writeTicket = dmManager.startJob(ihb1);

		StringHandle handleFoo = new StringHandle();
		handleFoo.set(xmlStr1);
		handleFoo.setFormat(Format.XML);

		StringHandle handleBar = new StringHandle();
		handleBar.set(xmlStr2);
		handleBar.setFormat(Format.XML);

		String uri1 = null;
		String uri2 = null;

		for (int i = 0; i < 4; i++) {
			uri1 = "foo" + i + ".xml";
			uri2 = "bar" + i + ".xml";
			ihb1.addAs(uri1, handleFoo).addAs(uri2, handleBar);
		}
		// Flush
		ihb1.flushAndWait();
		assertTrue(success.get());
		assertFalse(failure.get());
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 8);
	}

	// Multiple threads writing to same WHB object with unique uri's
	@Test
	public void testAddMultiThreadedSuccess() throws Exception {

		final String query1 = "fn:count(fn:doc())";

		ihbMT = dmManager.newWriteBatcher();
		ihbMT.withBatchSize(39);
		// ihbMT.withThreadCount(100);
		ihbMT.onBatchSuccess(batch -> {
			System.out.println("Success Event: " + dmManager.getJobReport(writeTicket).getSuccessEventsCount());
			System.out.println("Success Batch count:" + dmManager.getJobReport(writeTicket).getSuccessBatchesCount());

		});
		ihbMT.onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();

		});
		writeTicket = dmManager.startJob(ihbMT);

		class MyRunnable implements Runnable {

			@Override
			public void run() {

				for (int j = 0; j < 100; j++) {
					String uri = "/local/json-" + j + "-" + Thread.currentThread().getId();
					ihbMT.add(uri, fileHandle);
				}

				ihbMT.flushAndWait();
			}

		}
		Thread t1, t2, t3;
		t1 = new Thread(new MyRunnable());
		t2 = new Thread(new MyRunnable());
		t3 = new Thread(new MyRunnable());
		t1.start();
		t2.start();
		t3.start();

		t1.join();
		t2.join();
		t3.join();

		assertTrue(dmManager.getJobReport(writeTicket).getSuccessEventsCount() == 300);
		assertTrue(dmManager.getJobReport(writeTicket).getFailureEventsCount() == 0);
		assertTrue(dmManager.getJobReport(writeTicket).getFailureBatchesCount() == 0);
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 300);
	}

	// ISSUE 48
	@Test
	public void testAddMultiThreadedFailureEventCount() throws Exception {

		AtomicInteger succEvent = new AtomicInteger(0);
		AtomicInteger failEvent = new AtomicInteger(0);

		ihbMT = dmManager.newWriteBatcher();
		ihbMT.withBatchSize(105);
		ihbMT.onBatchSuccess(batch -> {
			succEvent.set((int) (dmManager.getJobReport(writeTicket).getSuccessEventsCount()));

		}).onBatchFailure((batch, throwable) -> {
			failEvent.set((int) (dmManager.getJobReport(writeTicket).getFailureEventsCount()));

		});
		writeTicket = dmManager.startJob(ihbMT);

		class MyRunnable implements Runnable {

			@Override
			public void run() {

				for (int j = 0; j < 100; j++) {
					String uri = "/local/json-" + j;
					// System.out.println("Thread name:
					// "+Thread.currentThread().getName()+" URI:"+
					// uri);
					ihbMT.add(uri, fileHandle);
				}
				ihbMT.flushAndWait();
			}

		}
		Thread t1, t2, t3;
		t1 = new Thread(new MyRunnable());
		t2 = new Thread(new MyRunnable());
		t3 = new Thread(new MyRunnable());
		t1.start();

		t2.start();

		t3.start();

		t1.join();
		t2.join();
		t3.join();
		System.out.println(succEvent.intValue());
		System.out.println(failEvent.intValue());
		assertTrue(succEvent.intValue() + failEvent.intValue() == 300);
	}

	@Test
	public void testRetry() throws Exception {

		final String query1 = "fn:count(fn:doc())";
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		AtomicBoolean successCalled = new AtomicBoolean(false);
		Map<String, String> properties = new HashMap<>();
		AtomicInteger successEvents = new AtomicInteger(0);
		AtomicInteger failureEvents = new AtomicInteger(0);

		ihbMT = dmManager.newWriteBatcher();
		ihbMT.withBatchSize(3000);

		ihbMT.onBatchSuccess(batch -> {
			successCalled.set(true);
			System.out.println("Success Batch count " + dmManager.getJobReport(writeTicket).getSuccessBatchesCount());
			System.out.println("Success Events " + dmManager.getJobReport(writeTicket).getSuccessEventsCount());
			successEvents.addAndGet((int) dmManager.getJobReport(writeTicket).getSuccessEventsCount());
		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();
			System.out.println("Failure Batch count " + dmManager.getJobReport(writeTicket).getFailureBatchesCount());
			System.out.println("Failure Events " + dmManager.getJobReport(writeTicket).getFailureEventsCount());
			failureEvents.addAndGet((int) dmManager.getJobReport(writeTicket).getFailureEventsCount());
			try {
				Thread.currentThread().sleep(20000L);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ihbMT.retry(batch);
		});

		writeTicket = dmManager.startJob(ihbMT);
		for (int j = 0; j < 200; j++) {
			String uri = "/local/json-" + j;
			ihbMT.add(uri, stringHandle);
		}

		properties.put("enabled", "false");
		changeProperty(properties, "/manage/v2/databases/" + dbName + "/properties");
		Thread.currentThread().sleep(2000L);
		ihbMT.flushAsync();
		Thread.currentThread().sleep(10000L);

		properties.put("enabled", "true");
		changeProperty(properties, "/manage/v2/databases/" + dbName + "/properties");
		ihbMT.awaitCompletion();
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 200);
		assertTrue(successEvents.get() == 200);
		assertTrue(failureEvents.get() == 200);
		assertTrue(successCalled.get());
	}

	@Test
	public void testServerXQueryTransformFailure() throws Exception {
		final String query1 = "fn:count(fn:doc())";
		TransformExtensionsManager transMgr = dbClient.newServerConfigManager().newTransformExtensionsManager();
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Adding attribute xquery Transform");
		metadata.setDescription("This plugin transforms an XML document by adding attribute to root node");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");
		// get the transform file from add-attr-xquery-transform.xqy
		File transformFile = FileUtils
				.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "add-attr-xquery-transform.xqy"));
		FileHandle transformHandle = new FileHandle(transformFile);
		transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle, metadata);

		ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "English");

		String xmlStr1 = "<?xml  version=\"1.0\" encoding=\"UTF-8\"?><foo>This is so foo</foo>";
		String xmlStr2 = "<?xml  version=\"1.0\" encoding=\"UTF-8\"?><foo>This is so bar</foo";

		AtomicLong succEvents = new AtomicLong(0L);
		AtomicLong succBatches = new AtomicLong(0L);

		AtomicLong failEvents = new AtomicLong(0L);
		AtomicLong failBatches = new AtomicLong(0L);

		AtomicBoolean success = new AtomicBoolean(false);
		AtomicBoolean failure = new AtomicBoolean(false);

		// Use WriteBatcher to write the same files.
		WriteBatcher ihb1 = dmManager.newWriteBatcher();
		ihb1.withBatchSize(1);
		ihb1.withTransform(transform);
		ihb1.onBatchSuccess(batch -> {

			succEvents.incrementAndGet();
			if (dmManager.getJobReport(writeTicket).getSuccessEventsCount() == succEvents.get()) {

				succBatches.incrementAndGet();
				if (dmManager.getJobReport(writeTicket).getSuccessBatchesCount() == succBatches.get()) {
					if (dmManager.getJobReport(writeTicket).getFailureEventsCount() == 0) {
						if (dmManager.getJobReport(writeTicket).getFailureBatchesCount() == 0) {
							if (Math.abs(dmManager.getJobReport(writeTicket).getReportTimestamp().getTime().getTime()
									- Calendar.getInstance().getTime().getTime()) < 200) {
								System.out.println(dmManager.getJobReport(writeTicket).getReportTimestamp());
								success.set(true);
							}
						}
					}
				}
			}
		});
		ihb1.onBatchFailure((batch, throwable) -> {
			System.out.println(dmManager.getJobReport(writeTicket).getSuccessEventsCount());
			if (dmManager.getJobReport(writeTicket).getSuccessEventsCount() == 4) {
				if (dmManager.getJobReport(writeTicket).getSuccessBatchesCount() == 4) {
					failEvents.incrementAndGet();
					if (dmManager.getJobReport(writeTicket).getFailureEventsCount() == failEvents.get()) {
						failBatches.incrementAndGet();
						if (dmManager.getJobReport(writeTicket).getFailureBatchesCount() == failBatches.get()) {
							if (Math.abs(dmManager.getJobReport(writeTicket).getReportTimestamp().getTime().getTime()
									- Calendar.getInstance().getTime().getTime()) < 200) {
								System.out.println(dmManager.getJobReport(writeTicket).getReportTimestamp());
								failure.set(true);
							}
						}
					}
				}
			}

		});

		writeTicket = dmManager.startJob(ihb1);

		StringHandle handleFoo = new StringHandle();
		handleFoo.set(xmlStr1);
		handleFoo.setFormat(Format.XML);

		StringHandle handleBar = new StringHandle();
		handleBar.set(xmlStr2);
		handleBar.setFormat(Format.XML);

		String uri1 = null;
		String uri2 = null;

		for (int i = 0; i < 4; i++) {
			uri1 = "foo" + i + ".xml";
			ihb1.add(uri1, handleFoo);
		}
		Thread.currentThread().sleep(5000L);
		for (int i = 0; i < 4; i++) {
			uri2 = "bar" + i + ".xml";
			ihb1.add(uri2, handleBar);
		}
		// Flush
		ihb1.flushAndWait();
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 4);

		// Account for a delta in the events
		assertTrue(Math.abs(succEvents.intValue()-4) <= 2);
		assertTrue(Math.abs(succBatches.intValue()-4) <= 2);
		assertTrue(Math.abs(failEvents.intValue()-4) <= 2);
		assertTrue(Math.abs(failBatches.intValue()-4) <= 2);

	}

	// Adding 25000 docs with thread count = 20
	@Test
	public void testThreadSize() throws Exception {
		try {
			final String query1 = "fn:count(fn:doc())";

			WriteBatcher ihb2 = dmManager.newWriteBatcher();
			ihb2.withBatchSize(200);
			ihb2.withThreadCount(60);
			ihb2.onBatchSuccess(batch -> {
			}).onBatchFailure((batch, throwable) -> {

				throwable.printStackTrace();

			});
			writeTicket = dmManager.startJob(ihb2);

			for (int j = 0; j < 25000; j++) {
				String uri = "/local/ABC-" + j;
				ihb2.add(uri, stringHandle);
			}

			ihb2.flushAndWait();

			assertTrue(dmManager.getJobReport(writeTicket).getSuccessEventsCount() == 25000);
			assertTrue(dmManager.getJobReport(writeTicket).getFailureBatchesCount() == 0);
			assertTrue(dmManager.getJobReport(writeTicket).getFailureBatchesCount() == 0);
			assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 25000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddMultiThreadedStopJob() throws Exception {

		final String query1 = "fn:count(fn:doc())";
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);

		ihbMT = dmManager.newWriteBatcher();
		ihbMT.withBatchSize(11).withThreadCount(5);
		ihbMT.onBatchSuccess(batch -> {

			if (dmManager.getJobReport(writeTicket).getSuccessEventsCount() > 80) {
				dmManager.stopJob(writeTicket);
				System.out.println("Job stopped");
			}
		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();

		});
		writeTicket = dmManager.startJob(ihbMT);

		class MyRunnable implements Runnable {

			@Override
			public void run() {

				for (int j = 0; j < 200; j++) {
					String uri = "/local/multi-" + j + "-" + Thread.currentThread().getId();
					ihbMT.add(uri, fileHandle);
					System.out.println(uri);

				}
				ihbMT.flushAndWait();
			}

		}
		Thread t1, t2;
		t1 = new Thread(new MyRunnable());
		t2 = new Thread(new MyRunnable());
		t1.setName("First Thread");
		t2.setName("Second Thread");

		t1.start();
		t2.start();
		ihbMT.awaitCompletion();

		t1.join();
		t2.join();
		try {
			ihbMT.add("/new", fileHandle);
			fail("Exception should have been thrown");
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains("This instance has been stopped"));

		}
		try {
			ihbMT.flushAndWait();
			fail("Exception should have been thrown");
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains("This instance has been stopped"));

		}

		ihbMT.awaitCompletion();

		int count = dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue();
		System.out.println(count);
		assertTrue(count == dmManager.getJobReport(writeTicket).getSuccessEventsCount());
	}

	public static Object[] getSummaryReport(JobTicket ticket) {
		JobReport report = dmManager.getJobReport(ticket);
		Object[] reportArr = new Object[5];
		reportArr[0] = report.getSuccessBatchesCount();
		reportArr[1] = report.getSuccessEventsCount();
		reportArr[2] = report.getFailureEventsCount();
		reportArr[3] = report.getFailureBatchesCount();
		return reportArr;
	}

	public String getSummaryReport(Object[] report) {

		return ("batches: " + report[0] + ", docs: " + report[1] + ", failed docs: " + report[2] + ", failed batches: "
				+ report[3] + ", timestamp: " + report[4]);
	}
}
