/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.fastfunctest.datamovement;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.datamovement.ApplyTransformListener.ApplyResult;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.fastfunctest.AbstractFunctionalTest;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.*;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

public class QueryBatcherJobReportTest extends AbstractFunctionalTest {

	private static DataMovementManager dmManager = null;
	private static final String TEST_DIR_PREFIX = "/WriteHostBatcher-testdata/";

	private static DatabaseClient dbClient;

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
	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		dbClient = connectAsAdmin();
		dmManager = dbClient.newDataMovementManager();

		hostNames = getHosts();

		// FileHandle
		fileJson = FileUtils.toFile(BasicJavaClientREST.class.getResource(TEST_DIR_PREFIX + "dir.json"));
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
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 2000);

		for (int j = 0; j < 2000; j++) {
			String uri = "/local/string-" + j;
			ihb2.add(uri, meta2, stringHandle);
		}

		ihb2.flushAndWait();
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 4000);

		// Xquery transformation
		TransformExtensionsManager transMgr = dbClient.newServerConfigManager().newTransformExtensionsManager();
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Adding attribute xquery Transform");
		metadata.setDescription("This plugin transforms an XML document by adding attribute to root node");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");
		// get the transform file
		File transformFile = FileUtils
				.toFile(BasicJavaClientREST.class.getResource(TEST_DIR_PREFIX + "add-attr-xquery-transform.xqy"));
		FileHandle transformHandle = new FileHandle(transformFile);
		transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle, metadata);

		// JS Transformation
		File transformFile1 = FileUtils
				.toFile(BasicJavaClientREST.class.getResource(TEST_DIR_PREFIX + "javascript_transform.sjs"));
		FileHandle transformHandle1 = new FileHandle(transformFile1);
		transMgr.writeJavascriptTransform("jsTransform", transformHandle1);
	}

	@Test
	public void jobReport() throws Exception {
		System.out.println("In jobReport method");
		AtomicLong count3 = new AtomicLong(0);
		String jobId = UUID.randomUUID().toString();

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(500).withThreadCount(20).withJobId(jobId).withJobName("XmlTransform");

		batcher.onUrisReady(batch -> {
			System.out.println("Yes");
		});
		batcher.onQueryFailure(throwable -> throwable.printStackTrace());
		queryTicket = dmManager.startJob(batcher);
		assertTrue(jobId.equalsIgnoreCase(queryTicket.getJobId()));
		assertTrue(batcher.getJobName().trim().equalsIgnoreCase("XmlTransform"));
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(queryTicket);
		System.out.println("Number of success batches " + dmManager.getJobReport(queryTicket).getSuccessBatchesCount());
		// Account for cluster env and number of forests; and/or single node env test runs.
		assertTrue(dmManager.getJobReport(queryTicket).getSuccessBatchesCount() >= 4);

		batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(500)
				.withThreadCount(20);
		batcher.onUrisReady(batch -> {
			System.out.println("Yes");
		});
		batcher.onQueryFailure(throwable -> throwable.printStackTrace());
		queryTicket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(queryTicket);
		assertEquals(dmManager.getJobReport(queryTicket).getSuccessEventsCount(), 2000);

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

		assertEquals(0, dmManager.getJobReport(queryTicket).getFailureEventsCount());
		assertEquals(dmManager.getJobReport(queryTicket).getSuccessBatchesCount(), count3.get());
	}

	@Test
	public void testNullQdef() {
		System.out.println("In testNullQdef method");
		JsonNode node = null;

		WriteBatcher wbatcher = dmManager.newWriteBatcher().withBatchSize(32).withThreadCount(20);
		try {
			wbatcher.addAs("/nulldoc", node);
			fail("Exception was not thrown, when it should have been");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().equals("content must not be null"));
		}

		try {
			dmManager.newQueryBatcher((StringQueryDefinition) null).withBatchSize(32).withThreadCount(20);
			fail("Exception was not thrown, when it should have been");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().equals("query must not be null"));
		}
	}

	@Test
	public void queryFailures() throws Exception {
		// Insert documents to query
		String jsonDoc = "{" + "\"employees\": [" + "{ \"firstName\":\"John\" , \"lastName\":\"Doe\" },"
				+ "{ \"firstName\":\"Ann\" , \"lastName\":\"Smith\" },"
				+ "{ \"firstName\":\"Bob\" , \"lastName\":\"Foo\" }]" + "}";
		WriteBatcher writeBatcher = dmManager.newWriteBatcher();
		writeBatcher.withBatchSize(100).withThreadCount(8);
		StringHandle handle = new StringHandle();
		handle.set(jsonDoc);
		for (int i = 0; i < 6000; i++) {
			String uri = "/firstName" + i + ".json";
			writeBatcher.add(uri, handle);
		}
		writeBatcher.flushAndWait();

		// Construct a query to return the docs inserted above
		QueryManager queryMgr = dbClient.newQueryManager();
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		querydef.setCriteria("John AND Bob");

		AtomicInteger successfulBatchCount = new AtomicInteger(0);
		QueryBatcher batcher = dmManager.newQueryBatcher(querydef).withBatchSize(100).withThreadCount(3);
		batcher.onUrisReady(batch -> successfulBatchCount.incrementAndGet());

		queryTicket = dmManager.startJob(batcher);

		// Run a thread to disable the database, and then sleep, and then enable the database
		Thread t1 = new Thread(new DisabledDBRunnable());
		t1.setName("Status Check -1");
		t1.start();
		t1.join();

		batcher.awaitCompletion();

		// Verify that the QueryBatcher was able to recover after the database was re-enabled
		assertEquals(6000, dmManager.getJobReport(queryTicket).getSuccessEventsCount());
		assertEquals(successfulBatchCount.intValue(), dmManager.getJobReport(queryTicket).getSuccessBatchesCount());
	}

	class DisabledDBRunnable implements Runnable {
		Map<String, String> properties = new HashMap<>();
		private final Logger logger = LoggerFactory.getLogger(getClass());

		@Override
		public void run() {
			properties.put("enabled", "false");
			logger.info("Disabling the java-functest database; successful events so far: "
				+ dmManager.getJobReport(queryTicket).getSuccessEventsCount());
			changeProperty(properties, "/manage/v2/databases/java-functest/properties");

			// TODO Figure out why 5s was chosen here
			logger.info("Sleeping before re-enabling the java-functest database; successful events since database " +
				"was disabled: " + dmManager.getJobReport(queryTicket).getSuccessEventsCount());
			try {
				Thread.currentThread().sleep(5000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			properties.put("enabled", "true");
			changeProperty(properties, "/manage/v2/databases/java-functest/properties");
			logger.info("Re-enabled the java-functest database");
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

		assertTrue(dmManager.getJobReport(queryTicket).getSuccessEventsCount() > 40);
		assertTrue(dmManager.getJobReport(queryTicket).getSuccessEventsCount() < 1000);
		assertTrue(batchCount.get() == dmManager.getJobReport(queryTicket).getSuccessBatchesCount());
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

		assertTrue(dmManager.getJobReport(queryTicket).getSuccessEventsCount() > 40);
		assertTrue(dmManager.getJobReport(queryTicket).getSuccessEventsCount() < 1000);
		assertTrue(batchCount.get() == dmManager.getJobReport(queryTicket).getSuccessBatchesCount());
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
			success.set(true);

		}).onUrisReady(listener).onQueryFailure((throwable) -> throwable.printStackTrace());

		queryTicket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(queryTicket);
		assertTrue(success.get());
		assertEquals(dmManager.getJobReport(queryTicket).getSuccessEventsCount(), 2000);
		assertEquals(dmManager.getJobReport(queryTicket).getSuccessEventsCount(), successCount.get());
		assertEquals(batchCount.get(), dmManager.getJobReport(queryTicket).getSuccessBatchesCount());

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

		assertEquals(2000, doccount.get());
	}

	@Test
	public void stopTransformJobTest() throws Exception {
		System.out.println("In stopTransformJobTest method");
		ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "French");
		List<String> skippedUris = new ArrayList<>();
		List<String> successUris = new ArrayList<>();
		List<String> failedUris = new ArrayList<>();

		AtomicLong skippedApplyTransCount = new AtomicLong(0L);

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE).onSuccess(batch -> {
					List<String> batchList = Arrays.asList(batch.getItems());
					successUris.addAll(batchList);
				}).onSkipped(batch -> {
					List<String> batchList = Arrays.asList(batch.getItems());
					skippedUris.addAll(batchList);
					System.out.println("stopTransformJobTest : Skipped: " + batch.getItems()[0]);
				}).onFailure((batch, throwable) -> {
					List<String> batchList = Arrays.asList(batch.getItems());
					failedUris.addAll(batchList);
					System.out.println("stopTransformJobTest: Failed: " + batch.getItems()[0]);
				});

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(1).withThreadCount(1);

		AtomicLong successCount = new AtomicLong(0L);

		batcher = batcher.onUrisReady((batch) -> {
			successCount.set(dmManager.getJobReport(queryTicket).getSuccessEventsCount());
		}).onUrisReady(listener);
		queryTicket = dmManager.startJob(batcher);
		// Wait an amount of time that should result in some docs being transformed but not all
		Thread.currentThread().sleep(500L);
		dmManager.stopJob(queryTicket);

		AtomicInteger transformedCount = new AtomicInteger(0);
		QueryBatcher resultBatcher = dmManager
				.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(25).withThreadCount(5)
				.onUrisReady((batch)->{
					DocumentPage page = batch.getClient().newDocumentManager().read(batch.getItems());
					DOMHandle dh = new DOMHandle();
					while (page.hasNext()) {
						DocumentRecord rec = page.next();
						rec.getContent(dh);
						if (dh.get().getElementsByTagName("foo").item(0).hasAttributes()) {
							String appliedTransStrValue = dh.get().getElementsByTagName("foo").item(0).getAttributes().item(0).getNodeValue();
							if (appliedTransStrValue.equalsIgnoreCase("French")) {
								transformedCount.incrementAndGet();
								System.out.println("stopTransformJobTest: Did not get skipped in server" + rec.getUri());
							}
						}else {
							skippedApplyTransCount.incrementAndGet();
						}
					}

				});
		dmManager.startJob(resultBatcher);
		resultBatcher.awaitCompletion();

		System.out.println("stopTransformJobTest: Success: " + successUris.size());
		System.out.println("stopTransformJobTest: Skipped Apply Transform count : " + skippedApplyTransCount.get());
		System.out.println("stopTransformJobTest: Skipped: " + skippedUris.size());
		System.out.println("stopTransformJobTest : Applied Trans count " + transformedCount.get());

		System.out.println("stopTransformJobTest : successCount.get() " + successCount.get());
		// This fails intermittently when the successBatch size is one less than the appliedTranscount. Interestingly,
		// a potentially similar off-by-one error occurs with the stopTransformJob test in ApplyTransformTest
		assertTrue(
			transformedCount.get() <= (successUris.size() + failedUris.size()),
			"Number of docs transformed must be <= number of docs selected; " +
				"applied count: " + transformedCount.get() + "; success batch size: " + successUris.size() + "; failed count: " + failedUris.size());
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
			assertTrue(docCnt == 50000);

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
			 * With thread count > 1 on QueryBatcher, batchResults size is greater than 24420.
			 */
			assertTrue(batchResults.size() >= 24420);

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

			assertTrue(batchResults2.size() > 0);
			assertTrue((batchResults2.size()>initialUrisSize && batchResults2.size()<= 2436));
		}
		finally {
			// This shouldn't be necessary - i.e. tests should perform cleanup before they start, not after - but
			// leaving this in case one of the older functional tests runs after this that doesn't yet cleanup the
			// database before it runs.
			QueryBatcher deleteBatcher = dmManager.newQueryBatcher(urisList.iterator())
			        .onUrisReady(new DeleteListener())
			        .withThreadCount(10);
			dmManager.startJob(deleteBatcher);
			deleteBatcher.awaitCompletion();
			dmManager.stopJob(deleteBatcher);
		}
	}
}
