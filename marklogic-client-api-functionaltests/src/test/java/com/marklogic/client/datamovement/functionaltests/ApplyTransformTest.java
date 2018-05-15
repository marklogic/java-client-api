/*
 * Copyright 2014-2018 MarkLogic Corporation
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.datamovement.ApplyTransformListener;
import com.marklogic.client.datamovement.ApplyTransformListener.ApplyResult;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.datamovement.FilteredForestConfiguration;
import com.marklogic.client.datamovement.HostAvailabilityListener;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.StructuredQueryBuilder;

public class ApplyTransformTest extends BasicJavaClientREST {

	private static String dbName = "ApplyTransform";
	private static DataMovementManager dmManager = null;
	private static final String TEST_DIR_PREFIX = "/WriteHostBatcher-testdata/";

	private static DatabaseClient dbClient;
	private static String host = null;
	private static String user = "admin";
	private static int port = 8000;
	private static String password = "admin";
	private static String server = "App-Services";

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

	private static String stringTriple;
	private static File fileJson;
	private static JsonNode jsonNode;
	private static JsonNode jsonNode1;
	private static final String query1 = "fn:count(fn:doc())";
	private static String[] hostNames;

	/**
	 * @throws Exception
	 */
	@BeforeClass
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
		assocRESTServer(server, dbName, port);
		if (IsSecurityEnabled()) {
			enableSecurityOnRESTServer(server, dbName);
		}

		dbClient = getDatabaseClient(user, password, Authentication.DIGEST);
        DatabaseClient adminClient = DatabaseClientFactory.newClient(host, 8000, user, password, Authentication.DIGEST);
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

		ihb2.add("/local/quality", meta3, jacksonHandle);
		ihb2.flushAndWait();
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 4001);

		ihb2.add("/local/nomatch", meta4, jacksonHandle1);
		ihb2.flushAndWait();
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 4002);

		for (int j = 0; j < 100; j++) {
			String uri = "/local/snapshot-" + j;
			ihb2.add(uri, meta5, fileHandle);
		}
		ihb2.flushAndWait();
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 4102);

		for (int j = 0; j < 2000; j++) {
			String uri = "/local/skipped-" + j;
			ihb2.add(uri, meta6, stringHandle);
		}

		ihb2.flushAndWait();
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 6102);

		ihb2.add("/local/nonexistent-1", stringHandle);
		ihb2.flushAndWait();
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 6103);
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

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {

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
				}).onBatchFailure((batch, throwable) -> {
					throwable.printStackTrace();
				}).onSkipped(batch -> {
					skipped.addAndGet(batch.getItems().length);

				});

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.onUrisReady(listener);
		JobTicket ticket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(ticket);

		String uris[] = new String[2000];
		for (int i = 0; i < 2000; i++) {
			uris[i] = "/local/string-" + i;
		}
		int count = 0;
		DocumentPage page = dbClient.newDocumentManager().read(uris);
		DOMHandle dh = new DOMHandle();
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			assertTrue("Element has attribure ? :", dh.get().getElementsByTagName("foo").item(0).hasAttributes());
			assertEquals("Attribute value should be English", "English",
					dh.get().getElementsByTagName("foo").item(0).getAttributes().item(0).getNodeValue());
			count++;
		}

		assertEquals("document count", 2000, count);
		assertEquals("document count", 2000, success.intValue());
		assertEquals("document count", 0, skipped.intValue());
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
				}).onBatchFailure((batch, throwable) -> {
					failure.addAndGet(batch.getItems().length);
					throwable.printStackTrace();
				}).onSkipped(batch -> {
					skipped.addAndGet(batch.getItems().length);
				});

		Set<String> urisList = new HashSet<>();
		urisList.add("/local/nonexistent");
		urisList.add("/local/nonexistent-1");
		QueryBatcher batcher = dmManager.newQueryBatcher(urisList.iterator()).withBatchSize(2).onUrisReady(listener);
		JobTicket ticket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(ticket);

		assertEquals("success count", 1, success.intValue());
		assertEquals("skipped count", 1, skipped.intValue());
		assertEquals("failure count", 0, failure.intValue());

		DocumentPage page = dbClient.newDocumentManager()
				.read(new String[] { "/local/nonexistent-1", "/local/nonexistent" });
		DOMHandle dh = new DOMHandle();
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			assertTrue("Element has attribure ? :", dh.get().getElementsByTagName("foo").item(0).hasAttributes());
			assertEquals("Attribute value should be English", "English",
					dh.get().getElementsByTagName("foo").item(0).getAttributes().item(0).getNodeValue());

		}
	}

	@Test
	public void jsMasstransformReplace() throws Exception {

		ServerTransform transform = new ServerTransform("jsTransform");
		transform.put("newValue", "new Value");

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE);
		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("JsonTransform"))
				.onUrisReady(listener);
		JobTicket ticket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(ticket);

		String uris[] = new String[2000];
		for (int i = 0; i < 2000; i++) {
			uris[i] = "/local/json-" + i;
		}
		int count = 0;
		DocumentPage page = dbClient.newDocumentManager().read(uris);
		JacksonHandle dh = new JacksonHandle();
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			assertEquals("Attribute value should be new Value", "new Value", dh.get().get("c").asText());
			count++;
		}

		assertEquals("document count", 2000, count);
	}

	@Test
	public void jstransformReplace() throws Exception {

		ServerTransform transform = new ServerTransform("jsTransform");
		transform.put("newValue", "JSON");

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE);
		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("Single Match"))
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
			assertEquals("Attribute value should be JSON1", "JSON", dh.get().get("c").asText());
			Assert.assertNotNull(dh.get().get("c"));
			count++;
		}

		assertEquals("match count", 1, count);
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

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("Single Match"))
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
		assertEquals("Values should match", beforeTransform, afterTransform);
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

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("Single Match"))
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
		assertEquals("Size should be 1", 1, urisList.size());
		assertEquals("Values should match", beforeTransform, afterTransform);
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
			assertTrue("Element has no attribures:", !dh.get().getElementsByTagName("foo").item(0).hasAttributes());
		}
		Set<String> urisList = new HashSet<>();

		ServerTransform transform = new ServerTransform("jsTransform");
		transform.put("newValue", "failed");

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE).onSuccess(batch -> {
					List<String> batchList = Arrays.asList(batch.getItems());
					successBatch.addAll(batchList);
				}).onBatchFailure((batch, throwable) -> {
					failCount.addAndGet(1);
					List<String> batchList = Arrays.asList(batch.getItems());
					failedBatch.addAll(batchList);
					throwable.printStackTrace();
					System.out.println("Failure batch " + batch.getItems().length);
					String s = null;
					s.charAt(0);
				}).onSkipped(batch -> {
					List<String> batchList = Arrays.asList(batch.getItems());
					skippedBatch.addAll(batchList);
				});

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("FailTransform"))
				.withBatchSize(2).onUrisReady(listener).onUrisReady(batch -> {

					System.out.println(batch.getClient().getHost());
					System.out.println(batch.getForest().getHost());
					urisList.addAll(Arrays.asList(batch.getItems()));
				});
		Map<String, String> properties = new HashMap<>();
		properties.put("locking", "strict");
		changeProperty(properties, "/manage/v2/databases/" + dbName + "/properties");

		String insertQuery = "xdmp:document-insert(\"/local/failed\", <foo>This is so foo</foo>, (), \"FailTransform\", 0, xdmp:forest(\"ApplyTransform-1\") );xdmp:document-insert(\"/local/failed-1\", object-node {\"c\":\"v1\"}, (), \"FailTransform\", 0, xdmp:forest(\"ApplyTransform-1\") )";

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
			assertTrue("Element has no attribures:", !dh.get().getElementsByTagName("foo").item(0).hasAttributes());
		}

		uri = new String("/local/failed-1");
		page = dbClient.newDocumentManager().read(uri);
		JacksonHandle jh = new JacksonHandle();
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(jh);
			assertEquals("Attribute value should be v1", "v1", jh.get().get("c").asText());

		}
		assertEquals("Size should be 2", 2, urisList.size());
		assertEquals("Size should be 2", 2, failedBatch.size());
		assertEquals("Size should be 1", 1, failCount.get());
		assertEquals("Size should be 0", 0, successBatch.size());
		assertEquals("Size should be 0", 0, skippedBatch.size());

	}

	// ISSUE # 569
	@Test
	public void jsMasstransformReplaceDelete() throws Exception {
		Assume.assumeTrue(hostNames.length > 1);

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

				}).onBatchFailure((batch, throwable) -> {
					isFailureCalled.set(true);
					throwable.printStackTrace();

				}).withTransform(transform);

		// Query collection "Replace Snapshot", a listener forTransform and
		// another
		// for Deletion are attached
		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("Replace Snapshot"))
				.withBatchSize(1).onUrisReady(listener).withConsistentSnapshot().onUrisReady(new DeleteListener())
				.onQueryFailure(throwable -> {
					throwable.printStackTrace();

				});
		batcher.setQueryFailureListeners(new HostAvailabilityListener(dmManager)
				.withSuspendTimeForHostUnavailable(Duration.ofSeconds(30)).withMinHosts(2));
		JobTicket ticket = dmManager.startJob(batcher);
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(ticket);

		assertFalse(isClientNull.get());
		assertEquals("document count", 100, count.intValue());
		assertFalse(isFailureCalled.get());
		assertTrue(flag.get());

		Set<String> urisList = Collections.synchronizedSet(new HashSet<String>());

		assertTrue(urisList.isEmpty());
		QueryBatcher queryBatcher = dmManager
				.newQueryBatcher(new StructuredQueryBuilder().collection("Replace Snapshot")).withBatchSize(11)
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
			Assert.assertEquals("/local/nomatch", batch.getItems()[0]);
		}).onSkipped(batch -> {

		});
		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("No Match"))
				.onUrisReady(listener).onUrisReady(batch -> {
					Assert.assertEquals(1, batch.getItems().length);
					Assert.assertEquals("/local/nomatch", batch.getItems()[0]);
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
			assertEquals("Attribute value should be v1", "v1", dh.get().get("k1").asText());
			Assert.assertNotNull(dh.get().get("c"));
			assertEquals("Attribute value should be new Value", "new Value", dh.get().get("c").asText());
			count++;
		}

		assertEquals("match count", 1, count);
	}

	// ISSUE # 106
	@Test
	public void stopTransformJobTest() throws Exception {

		ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "French");

		Set<String> skippedBatch = Collections.synchronizedSet(new HashSet<String>());
		Set<String> successBatch = Collections.synchronizedSet(new HashSet<String>());

		ApplyTransformListener listener = new ApplyTransformListener().withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE).onSuccess(batch -> {
					successBatch.addAll(Arrays.asList(batch.getItems()));
				}).onSkipped(batch -> {
					skippedBatch.addAll(Arrays.asList(batch.getItems()));
					System.out.println("stopTransformJobTest : Skipped: " + batch.getItems()[0]);

				}).onBatchFailure((batch, throwable) -> {
					throwable.printStackTrace();
					System.out.println("stopTransformJobTest: Failed: " + batch.getItems()[0]);

				});
		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("Skipped"))
				.onUrisReady(listener).withBatchSize(1).withThreadCount(1);
		JobTicket ticket = dmManager.startJob(batcher);
		Thread.currentThread().sleep(4000L);
		dmManager.stopJob(ticket);
		batcher.awaitCompletion();

		String uris[] = new String[2000];
		for (int i = 0; i < 2000; i++) {
			uris[i] = "/local/skipped-" + i;
		}
		int count = 0;
		DocumentPage page = dbClient.newDocumentManager().read(uris);
		DOMHandle dh = new DOMHandle();
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			if (dh.get().getElementsByTagName("foo").item(0).getAttributes().item(0) == null) {
				count++;
			}

		}
		System.out.println("stopTransformJobTest: Success: " + successBatch.size());
		System.out.println("stopTransformJobTest: Skipped: " + skippedBatch.size());
		System.out.println("stopTransformJobTest : count " + count);
		Assert.assertEquals(2000, successBatch.size() + skippedBatch.size() + count);
		Assert.assertEquals(2000 - count, successBatch.size());

	}

	@Test
	public void jsMasstransformReplaceFiltered() throws Exception {

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
				.withRenamedHost(host, "127.0.0.1").withWhiteList("127.0.0.1");

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
			System.out.println("OUtput URI is " + rec.getUri() + "  Currently Node's value " +  dh.get().get("c").asText());
			assertEquals("Attribute value should be new Value", "new Value", dh.get().get("c").asText());
			count++;
		}

		assertEquals("document count", 1000, count);
	}
}