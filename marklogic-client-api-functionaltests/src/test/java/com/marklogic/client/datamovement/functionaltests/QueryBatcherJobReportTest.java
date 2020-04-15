/*
 * Copyright (c) 2019 MarkLogic Corporation
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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.datamovement.ApplyTransformListener;
import com.marklogic.client.datamovement.ApplyTransformListener.ApplyResult;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;

public class QueryBatcherJobReportTest extends BasicJavaClientREST {

	private static String dbName = "QueryBatcherJobReport";
	private static DataMovementManager dmManager = null;
	private static final String TEST_DIR_PREFIX = "/WriteHostBatcher-testdata/";

	private static DatabaseClient dbClient;
	private static String user = "admin";
	private static int port = 8000;
	private static String password = "admin";
	private static String server = "App-Services";
	
	private static StringHandle stringHandle;
	private static FileHandle fileHandle;

	private static DocumentMetadataHandle meta1;
	private static DocumentMetadataHandle meta2;

	private static String stringTriple;
	private static File fileJson;

	private static final String query1 = "fn:count(fn:doc())";
	private static String[] hostNames;

	private static JobTicket queryTicket;

	/**
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		loadGradleProperties();
		server = getRestAppServerName();
	    port = getRestAppServerPort();
	    
		getRestAppServerHostName();
		dbClient = getDatabaseClient(user, password, getConnType());
		dmManager = dbClient.newDataMovementManager();
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
		assocRESTServer(server, dbName, port);
		if (IsSecurityEnabled()) {
			enableSecurityOnRESTServer(server, dbName);
		}

		// FileHandle
		fileJson = FileUtils.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "dir.json"));
		fileHandle = new FileHandle(fileJson);
		fileHandle.setFormat(Format.JSON);
		meta1 = new DocumentMetadataHandle().withCollections("JsonTransform");

		// StringHandle
		stringTriple = "<?xml  version=\"1.0\" encoding=\"UTF-8\"?><foo>This is so foo</foo>";
		stringHandle = new StringHandle(stringTriple);
		stringHandle.setFormat(Format.XML);
		meta2 = new DocumentMetadataHandle().withCollections("XmlTransform");

		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		ihb2.withBatchSize(27).withThreadCount(10);
		ihb2.onBatchSuccess(batch -> {

		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();
		});

		dmManager.startJob(ihb2);
		for (int j = 0; j < 2000; j++) {
			String uri = "/local/json-" + j;
			ihb2.add(uri, meta1, fileHandle);
		}

		ihb2.flushAndWait();
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 2000);

		for (int j = 0; j < 2000; j++) {
			String uri = "/local/string-" + j;
			ihb2.add(uri, meta2, stringHandle);
		}

		ihb2.flushAndWait();
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 4000);

		// Xquery transformation
		TransformExtensionsManager transMgr = dbClient.newServerConfigManager().newTransformExtensionsManager();
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Adding attribute xquery Transform");
		metadata.setDescription("This plugin transforms an XML document by adding attribute to root node");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");
		// get the transform file
		File transformFile = FileUtils
				.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "add-attr-xquery-transform.xqy"));
		FileHandle transformHandle = new FileHandle(transformFile);
		transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle, metadata);

		// JS Transformation
		File transformFile1 = FileUtils
				.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "javascript_transform.sjs"));
		FileHandle transformHandle1 = new FileHandle(transformFile1);
		transMgr.writeJavascriptTransform("jsTransform", transformHandle1);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		associateRESTServerWithDB(server, "Documents");
		for (int i = 0; i < hostNames.length; i++) {
			detachForest(dbName, dbName + "-" + (i + 1));
			deleteForest(dbName + "-" + (i + 1));
		}
		deleteDB(dbName);
	}

	@Test
	public void jobReport() throws Exception {
		System.out.println("In jobReport method");
		AtomicInteger batchCount = new AtomicInteger(0);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicLong count1 = new AtomicLong(0);
		AtomicLong count2 = new AtomicLong(0);
		AtomicLong count3 = new AtomicLong(0);
		String jobId = UUID.randomUUID().toString();

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(500).withThreadCount(20).withJobId(jobId).withJobName("XmlTransform");

		batcher.onUrisReady(batch -> {
			System.out.println("Yes");
			if (dmManager.getJobReport(queryTicket).getSuccessBatchesCount() == batchCount.incrementAndGet()) {
				count1.incrementAndGet();
			}
		});
		batcher.onQueryFailure(throwable -> throwable.printStackTrace());
		queryTicket = dmManager.startJob(batcher);
		Assert.assertTrue("Job Id incorrect", jobId.equalsIgnoreCase(queryTicket.getJobId()));
		Assert.assertTrue("Job Name incorrect", batcher.getJobName().trim().equalsIgnoreCase("XmlTransform"));
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(queryTicket);

		batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(500)
				.withThreadCount(20);
		batcher.onUrisReady(batch -> {
			if (dmManager.getJobReport(queryTicket).getSuccessEventsCount() == successCount
					.addAndGet(batch.getItems().length)) {
				count2.incrementAndGet();
			}

		});
		batcher.onQueryFailure(throwable -> throwable.printStackTrace());
		queryTicket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(queryTicket);

		batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(500)
				.withThreadCount(20);
		batcher.onUrisReady(batch -> {
			if (Math.abs(dmManager.getJobReport(queryTicket).getReportTimestamp().getTime().getTime()
					- Calendar.getInstance().getTime().getTime()) < 10000) {
				count3.incrementAndGet();
			}

		});
		batcher.onQueryFailure(throwable -> throwable.printStackTrace());

		queryTicket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(queryTicket);

		Assert.assertEquals(0, dmManager.getJobReport(queryTicket).getFailureEventsCount());
		Assert.assertEquals(dmManager.getJobReport(queryTicket).getSuccessBatchesCount(), batchCount.get());
		Assert.assertEquals(dmManager.getJobReport(queryTicket).getSuccessEventsCount(), successCount.get());
		Assert.assertEquals(dmManager.getJobReport(queryTicket).getSuccessBatchesCount(), count1.get());
		Assert.assertEquals(dmManager.getJobReport(queryTicket).getSuccessBatchesCount(), count2.get());
		Assert.assertEquals(dmManager.getJobReport(queryTicket).getSuccessBatchesCount(), count3.get());
	}

	@Test
	public void testNullQdef() throws IOException, InterruptedException {
		System.out.println("In testNullQdef method");
		JsonNode node = null;
		JacksonHandle jacksonHandle = null;

		WriteBatcher wbatcher = dmManager.newWriteBatcher().withBatchSize(32).withThreadCount(20);
		try {
			wbatcher.addAs("/nulldoc", node);
			Assert.assertFalse("Exception was not thrown, when it should have been", 1 < 2);
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(e.getMessage().equals("content must not be null"));
		}

		try {
			wbatcher.add("/nulldoc", jacksonHandle);
			Assert.assertFalse("Exception was not thrown, when it should have been", 1 < 2);
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(e.getMessage().equals("contentHandle must not be null"));
		}

		QueryManager queryMgr = dbClient.newQueryManager();
		StringQueryDefinition querydef = queryMgr.newStringDefinition();

		querydef = null;

		try {
			QueryBatcher batcher = dmManager.newQueryBatcher(querydef).withBatchSize(32).withThreadCount(20);
			Assert.assertFalse("Exception was not thrown, when it should have been", 1 < 2);
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(e.getMessage().equals("query must not be null"));
		}
	}

	@Test
	public void queryFailures() throws Exception {
		System.out.println("In queryFailures method");
		Thread t1 = new Thread(new DisabledDBRunnable());
		t1.setName("Status Check -1");

		QueryManager queryMgr = dbClient.newQueryManager();
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		querydef.setCriteria("John AND Bob");
		AtomicInteger batches = new AtomicInteger(0);

		String jsonDoc = "{" + "\"employees\": [" + "{ \"firstName\":\"John\" , \"lastName\":\"Doe\" },"
				+ "{ \"firstName\":\"Ann\" , \"lastName\":\"Smith\" },"
				+ "{ \"firstName\":\"Bob\" , \"lastName\":\"Foo\" }]" + "}";
		WriteBatcher wbatcher = dmManager.newWriteBatcher();
		wbatcher.withBatchSize(6000);
		wbatcher.onBatchFailure((batch, throwable) -> throwable.printStackTrace());
		StringHandle handle = new StringHandle();
		handle.set(jsonDoc);
		String uri = null;

		// Insert 10 K documents
		for (int i = 0; i < 6000; i++) {
			uri = "/firstName" + i + ".json";
			wbatcher.add(uri, handle);
		}

		wbatcher.flushAndWait();

		AtomicInteger failureCnts = new AtomicInteger(0);
		QueryBatcher batcher = dmManager.newQueryBatcher(querydef).withBatchSize(10).withThreadCount(3);
		batcher.onUrisReady(batch -> {
			batches.incrementAndGet();
		});
		batcher.onQueryFailure((throwable) -> {
			System.out.println("queryFailures: ");
			failureCnts.incrementAndGet();
			System.out.println("DB disabled for Forest " + throwable.getForest().getForestName());
			try {
				Thread.currentThread().sleep(7000L);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			throwable.getBatcher().retry(throwable);
			// We need an NullPointerException. Hence these statements.
			String s = null;
			s.length();

		});

		queryTicket = dmManager.startJob(batcher);
		t1.start();

		t1.join();

		batcher.awaitCompletion();

		Assert.assertEquals(6000, dmManager.getJobReport(queryTicket).getSuccessEventsCount());
		Assert.assertEquals(batches.intValue(), dmManager.getJobReport(queryTicket).getSuccessBatchesCount());
		if(!isLBHost()) {
			System.out.println("Method queryFailure hostNames.length " + hostNames.length);
			System.out.println("Method queryFailure getFailureEventsCount() " + dmManager.getJobReport(queryTicket).getFailureEventsCount());
			
			Assert.assertEquals(failureCnts.get(), dmManager.getJobReport(queryTicket).getFailureEventsCount());
			Assert.assertEquals(failureCnts.get(), dmManager.getJobReport(queryTicket).getFailureBatchesCount());
		}
	}

	class DisabledDBRunnable implements Runnable {
		Map<String, String> properties = new HashMap<>();

		@Override
		public void run() {
			properties.put("enabled", "false");
			boolean state = true;
			while (state) {
				System.out.println(dmManager.getJobReport(queryTicket).getSuccessEventsCount());
				if (dmManager.getJobReport(queryTicket).getSuccessEventsCount() >= 0) {

					changeProperty(properties, "/manage/v2/databases/" + dbName + "/properties");
					System.out.println("DB disabled");
					state = false;
				}

			}
			try {
				Thread.currentThread().sleep(5000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			properties.put("enabled", "true");
			changeProperty(properties, "/manage/v2/databases/" + dbName + "/properties");
		}
	}

	@Test
	public void jobReportStopJob() throws Exception {
		System.out.println("In jobReportStopJob method");
		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(20).withThreadCount(20);
		AtomicInteger batchCount = new AtomicInteger(0);
		Set<String> uris = Collections.synchronizedSet(new HashSet<String>());
		batcher.onUrisReady(batch -> {
			uris.addAll(Arrays.asList(batch.getItems()));
			batchCount.incrementAndGet();
			if (dmManager.getJobReport(queryTicket).getSuccessEventsCount() > 40) {
				dmManager.stopJob(queryTicket);
			}

		});
		batcher.onQueryFailure(throwable -> throwable.printStackTrace());

		queryTicket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);

		queryTicket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);

		System.out.println("Success event: " + dmManager.getJobReport(queryTicket).getSuccessEventsCount());
		System.out.println("Success batch: " + dmManager.getJobReport(queryTicket).getSuccessBatchesCount());
		System.out.println("Failure event: " + dmManager.getJobReport(queryTicket).getFailureEventsCount());
		System.out.println("Failure batch: " + dmManager.getJobReport(queryTicket).getFailureBatchesCount());

		Assert.assertTrue(dmManager.getJobReport(queryTicket).getSuccessEventsCount() > 40);
		Assert.assertTrue(dmManager.getJobReport(queryTicket).getSuccessEventsCount() < 1000);
		Assert.assertTrue(batchCount.get() == dmManager.getJobReport(queryTicket).getSuccessBatchesCount());
	}

	// Making sure we can stop jobs based on the JobId.
	@Test
	public void stopJobUsingJobId() throws Exception {
		System.out.println("In stopJobUsingJobId method");
		String jobId = UUID.randomUUID().toString();

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(20).withThreadCount(20).withJobId(jobId);

		AtomicInteger batchCount = new AtomicInteger(0);

		Set<String> uris = Collections.synchronizedSet(new HashSet<String>());

		batcher.onUrisReady(batch -> {
			uris.addAll(Arrays.asList(batch.getItems()));
			batchCount.incrementAndGet();
			if (dmManager.getJobReport(queryTicket).getSuccessEventsCount() > 40) {
				// Using JobId here to stop Job
				dmManager.stopJob(dmManager.getActiveJob(jobId));
			}

		});
		batcher.onQueryFailure(throwable -> throwable.printStackTrace());

		queryTicket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);

		queryTicket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);

		System.out.println("Success event: " + dmManager.getJobReport(queryTicket).getSuccessEventsCount());
		System.out.println("Success batch: " + dmManager.getJobReport(queryTicket).getSuccessBatchesCount());
		System.out.println("Failure event: " + dmManager.getJobReport(queryTicket).getFailureEventsCount());
		System.out.println("Failure batch: " + dmManager.getJobReport(queryTicket).getFailureBatchesCount());

		Assert.assertTrue(dmManager.getJobReport(queryTicket).getSuccessEventsCount() > 40);
		Assert.assertTrue(dmManager.getJobReport(queryTicket).getSuccessEventsCount() < 1000);
		Assert.assertTrue(batchCount.get() == dmManager.getJobReport(queryTicket).getSuccessBatchesCount());
	}

	@Test
	public void jsMasstransformReplace() throws Exception {
		System.out.println("In jsMasstransformReplace method");
		ServerTransform transform = new ServerTransform("jsTransform");
		transform.put("newValue", "new Value");

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE);

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("JsonTransform"))
				.withBatchSize(20).withThreadCount(20);
		AtomicBoolean success = new AtomicBoolean(false);

		AtomicInteger batchCount = new AtomicInteger(0);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger count = new AtomicInteger(0);

		batcher = batcher.onUrisReady((batch) -> {
			successCount.addAndGet(batch.getItems().length);
			batchCount.incrementAndGet();
			if (dmManager.getJobReport(queryTicket).getSuccessEventsCount() == successCount.get()) {
				success.set(true);
				count.incrementAndGet();
			}

		}).onUrisReady(listener).onQueryFailure((throwable) -> throwable.printStackTrace());

		queryTicket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(queryTicket);

		AtomicInteger doccount = new AtomicInteger(0);
		QueryBatcher resultBatcher = dmManager
				.newQueryBatcher(new StructuredQueryBuilder().collection("JsonTransform"))
				.withBatchSize(25).withThreadCount(5)
				.onUrisReady((batch)->{
					DocumentPage page = batch.getClient().newDocumentManager().read(batch.getItems());
					JacksonHandle dh = new JacksonHandle();
					while (page.hasNext()) {
						DocumentRecord rec = page.next();
						rec.getContent(dh);
						if(dh.get().get("c").asText().equals("new Value"))
							doccount.incrementAndGet();
					}
					
				});
		dmManager.startJob(resultBatcher);				
		resultBatcher.awaitCompletion();

		assertEquals("document count", 2000, doccount.get());
		Assert.assertTrue(success.get());
		Assert.assertEquals(batchCount.get(), dmManager.getJobReport(queryTicket).getSuccessBatchesCount());
		Assert.assertEquals(batchCount.get(), count.get());
		Assert.assertEquals(2000, dmManager.getJobReport(queryTicket).getSuccessEventsCount());

	}

	@Test
	public void stopTransformJobTest() throws Exception {
		System.out.println("In stopTransformJobTest method");
		ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "French");
		List<String> skippedBatch = new ArrayList<>();
		List<String> successBatch = new ArrayList<>();
		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE).onSuccess(batch -> {
					List<String> batchList = Arrays.asList(batch.getItems());
					successBatch.addAll(batchList);
					System.out.println("stopTransformJobTest: Success: " + batch.getItems()[0]);
					System.out.println("stopTransformJobTest: Success: " + dbClient.newServerEval()
							.xquery("fn:doc(\"" + batch.getItems()[0] + "\")").eval().next().getString());
				}).onSkipped(batch -> {
					List<String> batchList = Arrays.asList(batch.getItems());
					skippedBatch.addAll(batchList);
					System.out.println("stopTransformJobTest : Skipped: " + batch.getItems()[0]);
				}).onFailure((batch, throwable) -> {
					List<String> batchList = Arrays.asList(batch.getItems());
					throwable.printStackTrace();
					System.out.println("stopTransformJobTest: Failed: " + batch.getItems()[0]);

				});

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(1).withThreadCount(1);

		AtomicLong successCount = new AtomicLong(0L);

		batcher = batcher.onUrisReady((batch) -> {
			successCount.set(dmManager.getJobReport(queryTicket).getSuccessEventsCount());

		}).onUrisReady(listener);
		queryTicket = dmManager.startJob(batcher);
		Thread.currentThread().sleep(4000L);
		dmManager.stopJob(queryTicket);
		
		AtomicInteger count = new AtomicInteger(0);
		QueryBatcher resultBatcher = dmManager
				.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(25).withThreadCount(5)
				.onUrisReady((batch)->{
					DocumentPage page = batch.getClient().newDocumentManager().read(batch.getItems());
					DOMHandle dh = new DOMHandle();
					while (page.hasNext()) {
						DocumentRecord rec = page.next();
						rec.getContent(dh);
						if (dh.get().getElementsByTagName("foo").item(0).getAttributes().item(0) == null) {
							count.incrementAndGet();
							System.out.println("stopTransformJobTest: skipped in server" + rec.getUri());
						}
					}
					
				});
		dmManager.startJob(resultBatcher);				
		resultBatcher.awaitCompletion();

		System.out.println("stopTransformJobTest: Success: " + successBatch.size());
		System.out.println("stopTransformJobTest: Skipped: " + skippedBatch.size());
		System.out.println("stopTransformJobTest : count " + count);
		
		System.out.println("stopTransformJobTest : successCount.get() " + successCount.get());
		Assert.assertEquals(successCount.get(), successBatch.size());
	}
	
	/* Test 1 setMaxBatches(2035) - maximum specified in advance
	 * Test 2 setMaxBatches() -- the uris collected thus far during a job
	 * 
	 */
	@Test
	public void testStopBeforeListenerisComplete() throws Exception {
		ArrayList<String> urisList = new ArrayList<String>();
		final String qMaxBatches = "fn:count(cts:uri-match('/setMaxBatches*'))";
		try {
			
			System.out.println("In testStopBeforeListenerisComplete method");
		
			final AtomicInteger count = new AtomicInteger(0);
			final AtomicInteger failedBatch = new AtomicInteger(0);
			final AtomicInteger successBatch = new AtomicInteger(0);
			
			final AtomicInteger failedBatch2 = new AtomicInteger(0);
			final AtomicInteger successBatch2 = new AtomicInteger(0);
			
			String jsonDoc = "{" +
				    "\"employees\": [" +
				    "{ \"firstName\":\"John\" , \"lastName\":\"Doe\" }," +
				    "{ \"firstName\":\"Ann\" , \"lastName\":\"Smith\" }," +
				    "{ \"firstName\":\"Bob\" , \"lastName\":\"Foo\" }]" +
				    "}";
			StringHandle handle = new StringHandle();
			handle.setFormat(Format.JSON);
			handle.set(jsonDoc);

			WriteBatcher batcher = dmManager.newWriteBatcher();
			batcher.withBatchSize(99);
			batcher.withThreadCount(10);

			batcher.onBatchSuccess(batch -> {

			}).onBatchFailure((batch, throwable) -> {
				throwable.printStackTrace();

			});
			dmManager.startJob(batcher);

			class writeDocsThread implements Runnable {

				@Override
				public void run() {

					for (int j = 0; j < 50000; j++) {
						String uri = "/setMaxBatches-" + j + "-" + Thread.currentThread().getId();
						//System.out.println("Thread name: " + Thread.currentThread().getName() + "  URI:" + uri);
						urisList.add(uri);
						batcher.add(uri, handle);
					}
					batcher.flushAndWait();
				}
			}

			class CountRunnable implements Runnable {

				@Override
				public void run() {
					try {
						Thread.currentThread().sleep(15000L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Set<Thread> threads = Thread.getAllStackTraces().keySet();
					Iterator<Thread> iter = threads.iterator();
					while (iter.hasNext()) {
						Thread t = iter.next();
						if (t.getName().contains("pool-1-thread-"))
							System.out.println(t.getName());
						count.incrementAndGet();
					}
				}
			}
			Thread countT;
			countT = new Thread(new CountRunnable());

			Thread t1;
			t1 = new Thread(new writeDocsThread());

			countT.start();
			t1.start();
			countT.join();

			t1.join();
			
			int docCnt = dbClient.newServerEval().xquery(qMaxBatches).eval().next().getNumber().intValue();
			System.out.println("Doc count is " + docCnt);
			Assert.assertTrue(docCnt == 50000);

			Collection<String> batchResults = new LinkedHashSet<String>();
			QueryBatcher qb = dmManager.newQueryBatcher(urisList.iterator())
					.withBatchSize(12)
					.withThreadCount(1)
					.withJobId("ListenerCompletionTest")	            
					.onUrisReady((QueryBatch batch) -> {

						for (String str : batch.getItems()) {            		
							batchResults.add(str);	                    
						}
						successBatch.addAndGet(1);
					})
					.onQueryFailure(throwable-> {
						failedBatch.addAndGet(1);                
					});
			// Test 1 - Set max uris that can be collected in advance of the job.
			qb.setMaxBatches(2035);

			class MaxBatchesThread implements Runnable {

				@Override
				public void run() {
					try {
						Thread.currentThread().sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					dmManager.stopJob(qb);			
				}			
			}

			dmManager.startJob(qb);

			Thread tMBStop = new Thread(new MaxBatchesThread());

			tMBStop.start();
			tMBStop.join();

			// Validate Test 1 setMaxBatches(2035)
			System.out.println("Max URIs size is : " + batchResults.size());
			/* 12 times 2035 equals 24420 with one Thread on QueryBatcher.
			 * With thread count > 1 on QueryBatcher, batchResults size is less than 24420. 
			 */
			assertTrue("Stop QueryBatcher with setMaxBatches set to 2035 is incorrect", batchResults.size() == 24420);

			/* Test 2 setMaxBatches()
			 */
			Collection<String> batchResults2 = new LinkedHashSet<String>();
			QueryBatcher qb2 = dmManager.newQueryBatcher(urisList.iterator())
					.withBatchSize(12)
					.withThreadCount(20)
					.withJobId("ListenerCompletionTest2")	            
					.onUrisReady((QueryBatch batch) -> {

						for (String str : batch.getItems()) {            		
							batchResults2.add(str);	                    
						}
						successBatch2.addAndGet(1);
					})
					.onQueryFailure(throwable-> {
						failedBatch2.addAndGet(1);                
					});
			qb2.setMaxBatches(203);
			
			class BatchesSoFarThread implements Runnable {

				@Override
				public void run() {
					// Test 2
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					qb2.setMaxBatches();					
				}			
			}

			Thread tMBStop2 = new Thread(new BatchesSoFarThread());
			// Wait for the stop thread to initialize before starting DMSDK Job.
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			dmManager.startJob(qb2);
			
			int initialUrisSize = batchResults2.size();
			
			tMBStop2.start();
			qb2.awaitCompletion();
			dmManager.stopJob(qb2);
			
			System.out.println("Doc count in initialUrisSize " + initialUrisSize);
			System.out.println("Doc count after setMaxBatches() is called " +  batchResults2.size());
			
			assertTrue("Batches of URIs collected so far", batchResults2.size() > 0);
			assertTrue("Number of Uris collected does not fall in the range", (batchResults2.size()>initialUrisSize && batchResults2.size()< 2436));
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			// Delete all uris.
			QueryBatcher deleteBatcher = dmManager.newQueryBatcher(urisList.iterator())
			        .onUrisReady(new DeleteListener())
			        .onUrisReady(batch -> {
			          //System.out.println("Items in batch " + batch.getItems().length);
			        }
			        )
			        .onQueryFailure(throwable -> {
			          System.out.println("Query Failed");
			          throwable.printStackTrace();
			        })
			        .withBatchSize(5000)
			        .withThreadCount(10);
			dmManager.startJob(deleteBatcher);
			deleteBatcher.awaitCompletion(2, TimeUnit.MINUTES);
			int docCnt = dbClient.newServerEval().xquery(qMaxBatches).eval().next().getNumber().intValue();
			System.out.println("All setMaxBatches docs should have been deleted. Count after DeleteListener job is " + docCnt);
			Assert.assertTrue(docCnt == 0);
		}
	}
}
