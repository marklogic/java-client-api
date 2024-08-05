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
import com.marklogic.client.datamovement.ApplyTransformListener.ApplyResult;
import com.marklogic.client.datamovement.functionaltests.WriteHostBatcherTest;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.fastfunctest.AbstractFunctionalTest;
import com.marklogic.client.io.*;
import com.marklogic.client.query.StructuredQueryBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a "fast" test but stopTransformJobTest is fragile and we don't want it impacting PR builds.
 */
public class ApplyTransformTest extends AbstractFunctionalTest {

	private static DataMovementManager dmManager = null;
	private static final String TEST_DIR_PREFIX = "/WriteHostBatcher-testdata/";

	private static DatabaseClient dbClient;

	private static JacksonHandle jacksonHandle;
	private static JacksonHandle jacksonHandle1;
	private static StringHandle stringHandle;
	private static FileHandle fileHandle;

	private static DocumentMetadataHandle meta1;
	private static DocumentMetadataHandle meta2;
	private static DocumentMetadataHandle meta3;
	private static DocumentMetadataHandle meta4;
	private static DocumentMetadataHandle meta5;
	private static DocumentMetadataHandle meta6;
	private static List<String> outputList;
	private static String stringTriple;
	private static File fileJson;
	private static JsonNode jsonNode;
	private static JsonNode jsonNode1;
	private static final String query1 = "fn:count(fn:doc())";
	private static String[] hostNames;

	// Number of documents to insert in each test collection
	private final static int DOC_COUNT = 1000;

	/**
	 * @throws Exception
	 */
	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		hostNames = getHosts();
		dbClient = connectAsAdmin();
       	dmManager = dbClient.newDataMovementManager();

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

		// JacksonHandle
		jsonNode = new ObjectMapper().readTree("{\"c\":\"v1\"}");
		jacksonHandle = new JacksonHandle();
		jacksonHandle.set(jsonNode);
		meta3 = new DocumentMetadataHandle().withCollections("Single Match");

		// JacksonHandle
		jsonNode1 = new ObjectMapper().readTree("{\"k1\":\"v1\"}");
		jacksonHandle1 = new JacksonHandle();
		jacksonHandle1.set(jsonNode1);
		meta4 = new DocumentMetadataHandle().withCollections("No Match");
		meta5 = new DocumentMetadataHandle().withCollections("Replace Snapshot");
		meta6 = new DocumentMetadataHandle().withCollections("Skipped");

		// XQuery transformation
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

		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		ihb2.withBatchSize(27).withThreadCount(10);
		ihb2.onBatchSuccess(batch -> {

		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();
		});

		dmManager.startJob(ihb2);
		outputList = new ArrayList<>();
		for (int j = 0; j < DOC_COUNT; j++) {
			String uri = "/local/json-" + j;
			ihb2.add(uri, meta1, fileHandle);
		}
		for (int j = 0; j < DOC_COUNT; j++) {
			String uri = "/local/string-" + j;
			outputList.add(uri);
			ihb2.add(uri, meta2, stringHandle);
		}
		ihb2.add("/local/quality", meta3, jacksonHandle);
		ihb2.add("/local/nomatch", meta4, jacksonHandle1);
		for (int j = 0; j < 100; j++) {
			String uri = "/local/snapshot-" + j;
			ihb2.add(uri, meta5, fileHandle);
		}
		for (int j = 0; j < DOC_COUNT; j++) {
			String uri = "/local/skipped-" + j;
			ihb2.add(uri, meta6, stringHandle);
		}
		ihb2.add("/local/nonexistent-1", stringHandle);
		ihb2.flushAndWait();
	}

	@Test
	public void xQueryMasstransformReplace() throws Exception {

		ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "English");

		AtomicInteger skipped = new AtomicInteger(0);
		AtomicInteger success = new AtomicInteger(0);

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE).onSuccess(batch -> {
					success.addAndGet(batch.getItems().length);
				}).onFailure((batch, throwable) -> {
					throwable.printStackTrace();
				}).onSkipped(batch -> {
					skipped.addAndGet(batch.getItems().length);

				});

		QueryBatcher batcher = dmManager
				.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.onUrisReady(listener);

		JobTicket ticket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(ticket);

		AtomicInteger count = new AtomicInteger(0);
		QueryBatcher resultBatcher =
				dmManager.newQueryBatcher(outputList.iterator())
				.withBatchSize(25).withThreadCount(5)
				.onUrisReady((batch)->{
					DocumentPage page = batch.getClient().newDocumentManager().read(batch.getItems());
					DOMHandle dh = new DOMHandle();
					while (page.hasNext()) {
						DocumentRecord rec = page.next();
						rec.getContent(dh);
						if(dh.get().getElementsByTagName("foo").item(0).getAttributes()
								.item(0).getNodeValue().equals("English"));
							count.incrementAndGet();
					}
				});
		dmManager.startJob(resultBatcher);
		resultBatcher.awaitCompletion();
		assertEquals(DOC_COUNT, count.get());
		assertEquals(DOC_COUNT, success.intValue());
		assertEquals(0, skipped.intValue());

	}

	@Test
	public void nonExistentDocsTransform() throws Exception {

		ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "English");

		AtomicInteger skipped = new AtomicInteger(0);
		AtomicInteger success = new AtomicInteger(0);
		AtomicInteger failure = new AtomicInteger(0);

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE).onSuccess(batch -> {
					success.addAndGet(batch.getItems().length);
				}).onFailure((batch, throwable) -> {
					failure.addAndGet(batch.getItems().length);
					throwable.printStackTrace();
				}).onSkipped(batch -> {
					skipped.addAndGet(batch.getItems().length);
				});

		Set<String> urisList = new HashSet<>();
		urisList.add("/local/nonexistent");
		urisList.add("/local/nonexistent-1");
		QueryBatcher batcher =
				dmManager.newQueryBatcher(urisList.iterator()).withBatchSize(2).onUrisReady(listener);
		JobTicket ticket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(ticket);

		assertEquals(1, success.intValue());
		assertEquals(1, skipped.intValue());
		assertEquals(0, failure.intValue());

		DocumentPage page = dbClient.newDocumentManager()
				.read(new String[] { "/local/nonexistent-1", "/local/nonexistent" });
		DOMHandle dh = new DOMHandle();
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			assertTrue(dh.get().getElementsByTagName("foo").item(0).hasAttributes());
			assertEquals("English",
					dh.get().getElementsByTagName("foo").item(0).getAttributes().item(0).getNodeValue());

		}
	}

	@Test
	public void jsMasstransformReplace() throws Exception {

		ServerTransform transform = new ServerTransform("jsTransform");
		transform.put("newValue", "new Value");

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE);
		QueryBatcher batcher = dmManager
				.newQueryBatcher(new StructuredQueryBuilder().collection("JsonTransform"))
				.onUrisReady(listener);
		JobTicket ticket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(ticket);

		AtomicInteger count = new AtomicInteger(0);
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
							count.incrementAndGet();
					}

				});
		dmManager.startJob(resultBatcher);
		resultBatcher.awaitCompletion();
		assertEquals(DOC_COUNT, count.get());
	}

	@Test
	public void jstransformReplace() throws Exception {

		ServerTransform transform = new ServerTransform("jsTransform");
		transform.put("newValue", "JSON");

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE);
		QueryBatcher batcher = dmManager
				.newQueryBatcher(new StructuredQueryBuilder().collection("Single Match"))
				.onUrisReady(listener).onUrisReady(batch -> {
				});
		JobTicket ticket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(ticket);
		String uri = new String("/local/quality");
		DocumentPage page = dbClient.newDocumentManager().read(uri);
		JacksonHandle dh = new JacksonHandle();
		int count = 0;
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			assertEquals("JSON", dh.get().get("c").asText());
			assertNotNull(dh.get().get("c"));
			count++;
		}

		assertEquals(1, count);
	}

	@Test
	public void notransformTest() throws Exception {

		String beforeTransform = null;
		String afterTransform = null;

		String uri = new String("/local/quality");
		DocumentPage page = dbClient.newDocumentManager().read(uri);
		JacksonHandle dh = new JacksonHandle();
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			beforeTransform = dh.get().get("c").asText();

		}

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(null);

		QueryBatcher batcher = dmManager
				.newQueryBatcher(new StructuredQueryBuilder().collection("Single Match"))
				.onUrisReady(listener).onUrisReady(batch -> {
					System.out.println("notransformTest: URI " + batch.getItems()[0]);
				});
		JobTicket ticket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(ticket);

		page = dbClient.newDocumentManager().read(uri);
		dh = new JacksonHandle();
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			afterTransform = dh.get().get("c").asText();

		}
		assertEquals(beforeTransform, afterTransform);
	}

	@Test
	public void ignoreTransformTest() throws Exception {

		String beforeTransform = null;
		String afterTransform = null;

		String uri = new String("/local/quality");
		DocumentPage page = dbClient.newDocumentManager().read(uri);
		JacksonHandle dh = new JacksonHandle();
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			beforeTransform = dh.get().get("c").asText();

		}
		Set<String> urisList = new HashSet<>();

		ServerTransform transform = new ServerTransform("jsTransform");
		transform.put("newValue", "ignore");

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.IGNORE);

		QueryBatcher batcher = dmManager
				.newQueryBatcher(new StructuredQueryBuilder().collection("Single Match"))
				.onUrisReady(listener).onUrisReady(batch -> {
					System.out.println("ignoreTransformTest: URI " + batch.getItems()[0]);
					urisList.addAll(Arrays.asList(batch.getItems()));
				});
		JobTicket ticket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(ticket);

		page = dbClient.newDocumentManager().read(uri);
		dh = new JacksonHandle();
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			afterTransform = dh.get().get("c").asText();

		}
		assertEquals(1, urisList.size());
		assertEquals(beforeTransform, afterTransform);
	}

	@Test
	public void failedTransformTest() throws Exception {

		List<String> successBatch = new ArrayList<>();
		List<String> failedBatch = new ArrayList<>();
		List<String> skippedBatch = new ArrayList<>();
		AtomicInteger failCount = new AtomicInteger(0);

		String uri = new String("/local/failed");
		DocumentPage page = dbClient.newDocumentManager().read(uri);
		DOMHandle dh = new DOMHandle();
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			assertTrue(!dh.get().getElementsByTagName("foo").item(0).hasAttributes());
		}
		Set<String> urisList = new HashSet<>();

		ServerTransform transform = new ServerTransform("jsTransform");
		transform.put("newValue", "failed");

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE).onSuccess(batch -> {
					List<String> batchList = Arrays.asList(batch.getItems());
					successBatch.addAll(batchList);
				}).onFailure((batch, throwable) -> {
					failCount.addAndGet(1);
					List<String> batchList = Arrays.asList(batch.getItems());
					failedBatch.addAll(batchList);
					throwable.printStackTrace();
					System.out.println("Failure batch " + batch.getItems().length);
					// String set to null on purpose, to get an Exception.
					String s = null;
					s.charAt(0);
				}).onSkipped(batch -> {
					List<String> batchList = Arrays.asList(batch.getItems());
					skippedBatch.addAll(batchList);
				});

		QueryBatcher batcher = dmManager
				.newQueryBatcher(new StructuredQueryBuilder().collection("FailTransform"))
				.withBatchSize(2).onUrisReady(listener).onUrisReady(batch -> {

					System.out.println(batch.getClient().getHost());
					System.out.println(batch.getForest().getHost());
					urisList.addAll(Arrays.asList(batch.getItems()));
				});

		String insertQuery = "xdmp:document-insert(\"/local/failed\", "
				+ "<foo>This is so foo</foo>, (), \"FailTransform\", 0 )"
				+ ";xdmp:document-insert(\"/local/failed-1\", object-node {\"c\":\"v1\"}, (),"
				+ " \"FailTransform\","
				+ " 0 )";

		String response = dbClient.newServerEval().xquery(insertQuery).evalAs(String.class);
		System.out.println(response);

		JobTicket ticket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(ticket);

		page = dbClient.newDocumentManager().read(uri);
		dh = new DOMHandle();
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			assertTrue(!dh.get().getElementsByTagName("foo").item(0).hasAttributes());
		}

		uri = new String("/local/failed-1");
		page = dbClient.newDocumentManager().read(uri);
		JacksonHandle jh = new JacksonHandle();
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(jh);
			assertEquals("v1", jh.get().get("c").asText());

		}
		assertEquals(2, urisList.size());
		assertEquals(2, failedBatch.size());
		assertEquals(1, failCount.get());
		assertEquals(0, successBatch.size());
		assertEquals(0, skippedBatch.size());

	}

	// ISSUE # 569
	@Test
	public void jsMasstransformReplaceDelete() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);

		// transform
		ServerTransform transform = new ServerTransform("jsTransform");
		transform.put("newValue", "Value");

		AtomicInteger count = new AtomicInteger();
		AtomicBoolean flag = new AtomicBoolean(true);
		AtomicBoolean isClientNull = new AtomicBoolean(false);
		AtomicBoolean isFailureCalled = new AtomicBoolean(false);

		ApplyTransformListener listener = new ApplyTransformListener().withApplyResult(ApplyResult.REPLACE)
				.onSuccess(batch -> {
					if (batch.getClient() == null) {
						isClientNull.set(true);
					}

					DocumentPage page = batch.getClient().newDocumentManager().read(batch.getItems());
					JacksonHandle dh = new JacksonHandle();
					while (page.hasNext()) {
						DocumentRecord rec = page.next();
						rec.getContent(dh);
						System.out.println(dh.get().get("c").asText().trim());
						if (!dh.get().get("c").asText().trim().equals("Value")) {
							flag.set(false);
						}

						count.incrementAndGet();
					}
					String s = null;
					s.charAt(0);

				}).onFailure((batch, throwable) -> {
					isFailureCalled.set(true);
					throwable.printStackTrace();

				}).withTransform(transform);

		// Query collection "Replace Snapshot", a listener forTransform and
		// another
		// for Deletion are attached
		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder()
				.collection("Replace Snapshot"))
				.withBatchSize(1).onUrisReady(listener).withConsistentSnapshot()
				.onUrisReady(new DeleteListener())
				.onQueryFailure(throwable -> {
					throwable.printStackTrace();

				});
		HostAvailabilityListener hal = new HostAvailabilityListener(dmManager)
		.withSuspendTimeForHostUnavailable(Duration.ofSeconds(30));
		if(!isLBHost()) {
			hal = hal.withMinHosts(2);
		}
		batcher.setQueryFailureListeners(hal);
		JobTicket ticket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(ticket);

		assertFalse(isClientNull.get());
		assertEquals(100, count.intValue());
		assertFalse(isFailureCalled.get());
		assertTrue(flag.get());

		Set<String> urisList = Collections.synchronizedSet(new HashSet<String>());

		assertTrue(urisList.isEmpty());
		QueryBatcher queryBatcher = dmManager.newQueryBatcher(new StructuredQueryBuilder()
				.collection("Replace Snapshot"))
				.withBatchSize(11)
				.onUrisReady(batch -> {
					urisList.addAll(Arrays.asList(batch.getItems()));
				}).onQueryFailure(throwable -> {
					throwable.printStackTrace();

				});

		JobTicket ticket1 = dmManager.startJob(queryBatcher);
		queryBatcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(ticket1);
		for (String uri : urisList) {
			System.out.println("Uris: " + uri);
		}
		assertTrue(urisList.isEmpty());
	}

	@Test
	public void noMatchReplace() throws Exception {

		ServerTransform transform = new ServerTransform("jsTransform");
		transform.put("newValue", "new Value");

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE);
		listener.onSuccess(batch -> {
			assertEquals("/local/nomatch", batch.getItems()[0]);
		}).onSkipped(batch -> {

		});
		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("No Match"))
				.onUrisReady(listener).onUrisReady(batch -> {
					assertEquals(1, batch.getItems().length);
					assertEquals("/local/nomatch", batch.getItems()[0]);
				});
		JobTicket ticket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(ticket);

		String uri = new String("/local/nomatch");
		DocumentPage page = dbClient.newDocumentManager().read(uri);
		JacksonHandle dh = new JacksonHandle();
		int count = 0;
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			assertEquals("v1", dh.get().get("k1").asText());
			assertNotNull(dh.get().get("c"));
			assertEquals("new Value", dh.get().get("c").asText());
			count++;
		}

		assertEquals(1, count);
	}

	// ISSUE # 106
	@Test
	public void stopTransformJobTest() throws Exception {

		ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "French");

		Set<String> skippedUris = Collections.synchronizedSet(new HashSet<>());
		Set<String> successUris = Collections.synchronizedSet(new HashSet<>());
		Set<String> failedUris = Collections.synchronizedSet(new HashSet<>());

		List<Throwable> failures = new Vector<>();

		ApplyTransformListener listener = new ApplyTransformListener()
			.withTransform(transform)
			.withApplyResult(ApplyResult.REPLACE).onSuccess(batch -> {
				successUris.addAll(Arrays.asList(batch.getItems()));
			}).onSkipped(batch -> {
				skippedUris.addAll(Arrays.asList(batch.getItems()));
			}).onFailure((batch, throwable) -> {
				failures.add(throwable);
				failedUris.addAll(Arrays.asList(batch.getItems()));
			});

		for (Throwable failure : failures) {
			if (!(failure instanceof InterruptedException)) {
				fail("Unexpected batch failure, only expecting an InterruptedException from the batcher being interrupted: " + failure);
			}
		}

		QueryBatcher batcher = dmManager
			.newQueryBatcher(new StructuredQueryBuilder().collection("Skipped"))
			.onUrisReady(listener)
			.withBatchSize(10)
			.withThreadCount(1);

		JobTicket ticket = dmManager.startJob(batcher);
		// Wait an amount of time that should result in some docs being transformed but not all
		Thread.currentThread().sleep(200L);
		dmManager.stopJob(ticket);
		batcher.awaitCompletion();

		// Find how many documents were transformed
		AtomicInteger notTransformedCount = new AtomicInteger(0);
		QueryBatcher resultBatcher = dmManager
				.newQueryBatcher(new StructuredQueryBuilder().collection("Skipped"))
				.withBatchSize(25).withThreadCount(5)
				.onUrisReady((batch)->{
					DocumentPage page = batch.getClient().newDocumentManager().read(batch.getItems());
					DOMHandle dh = new DOMHandle();
					while (page.hasNext()) {
						DocumentRecord rec = page.next();
						rec.getContent(dh);
						if (dh.get().getElementsByTagName("foo").item(0).getAttributes().item(0) == null) {
							notTransformedCount.incrementAndGet();
						}
					}
				});
		dmManager.startJob(resultBatcher);
		resultBatcher.awaitCompletion();

		int successCount = successUris.size();
		int skippedCount = skippedUris.size();
		int failedCount = failedUris.size();

		System.out.println("SUCCESS: " + successCount);
		System.out.println("SKIPPED: " + skippedCount);
		System.out.println("FAILED: " + failedCount);
		System.out.println("NOT TRANSFORMED: " + notTransformedCount.get());

		assertEquals(
			DOC_COUNT, successCount + skippedCount + failedCount + notTransformedCount.get(),
			"Unexpected count; success: " + successCount + "; skipped: " + skippedCount + "; failed: " + failedCount);
		assertEquals(
			DOC_COUNT - notTransformedCount.get() - failedCount, successCount,
			"Unexpected count; success: " + successCount + "; failed: " + failedCount + "; not transformed: " + notTransformedCount.get());
	}

	@Test
	public void jsMasstransformReplaceFiltered() throws Exception {
		Assumptions.assumeFalse(isLBHost());
		ServerTransform transform = new ServerTransform("jsTransform");
		transform.put("newValue", "new Value");

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE);

		DocumentMetadataHandle meta6 = new DocumentMetadataHandle().withCollections("NoHost").withQuality(0);

		WriteBatcher ihb2 = dmManager.newWriteBatcher();

		ihb2.withBatchSize(50);

		ihb2.onBatchSuccess(batch -> {
		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();

		});
		for (int j = 0; j < 1000; j++) {
			String uri = "/local/nohost-" + j;
			ihb2.addAs(uri, meta6, jsonNode);
		}

		ihb2.flushAndWait();

		FilteredForestConfiguration forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig())
				.withRenamedHost("localhost", "127.0.0.1").withWhiteList("127.0.0.1");

		Set<String> uris = Collections.synchronizedSet(new HashSet<String>());
		QueryBatcher getUris = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("NoHost"));
		getUris.withForestConfig(forestConfig);

		getUris.withBatchSize(500).withThreadCount(2).onUrisReady((batch -> {
			uris.addAll(Arrays.asList(batch.getItems()));
		})).onUrisReady(listener).onQueryFailure(exception -> exception.printStackTrace());

		dmManager.startJob(getUris);

		getUris.awaitCompletion();

		String docuris[] = new String[1000];
		for (int i = 0; i < 1000; i++) {
			docuris[i] = "/local/nohost-" + i;
		}
		int count = 0;
		DocumentPage page = dbClient.newDocumentManager().read(docuris);
		JacksonHandle dh = new JacksonHandle();
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			assertEquals("new Value", dh.get().get("c").asText());
			count++;
		}

		assertEquals(1000, count);
	}
}
