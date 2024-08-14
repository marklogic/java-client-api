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
import com.marklogic.client.datamovement.impl.WriteJobReportListener;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteOperation.OperationType;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.*;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.query.StructuredQueryBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class WriteHostBatcherTest extends BasicJavaClientREST {

	private static String dbName = "WriteHostBatcher";
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
	private static BytesHandle bytesHandle;
	private static InputStreamHandle isHandle;
	private static ReaderHandle readerHandle;
	private static OutputStreamHandle osHandle;
	private static DocumentMetadataHandle docMeta1;
	private static DocumentMetadataHandle docMeta2;
	private static ReaderHandle readerHandle1;
	private static OutputStreamHandle osHandle1;
	private static WriteBatcher ihbMT;
	private static String[] hostNames;

	private static String stringTriple;
	private static File fileJson;
	private static Document docContent;

	private static FileInputStream inputStream;
	private static OutputStreamSender sender;
	private static OutputStreamSender sender1;
	private static BufferedReader docStream;
	private static BufferedReader docStream1;
	private static byte[] bytesJson;
	private static JsonNode jsonNode;
	private static JobTicket writeTicket;
	private static JobTicket testBatchJobTicket;
	private static int forestCount = 1;

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		loadGradleProperties();

		server = getRestAppServerName();
		port = getRestAppServerPort();

        host = getRestAppServerHostName();
		hostNames = getHosts();

		createDB(dbName);
		Thread.currentThread().sleep(500L);
		//Ensure db has atleast one forest
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
		for (int i = 0; i < forestCount -1 ; i++) {
			System.out.println(dbName + "-" + (i + 1));
			detachForest(dbName, dbName + "-" + (i + 1));
			deleteForest(dbName + "-" + (i + 1));
		}

		deleteDB(dbName);
	}

	@BeforeEach
	public void setUp() throws Exception {
		if (getDocumentCount(dbName) != 0) {
			clearDB(port);
		}
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
		if (s.trim().equals("false")) {
			props.put("server-name", server);
			props.put("group-name", "Default");
			props.put("enabled", "true");
			changeProperty(props, "/manage/v2/servers/" + server + "/properties");
		}

		clearDB(port);
	}

	private void replenishStream() throws Exception {

		// InputStreamHandle
		isHandle = new InputStreamHandle();
		inputStream = new FileInputStream(
				WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "myJSONFile.json").getPath());
		isHandle.withFormat(Format.JSON);
		isHandle.set(inputStream);

		// OutputStreamHandle
		sender = new OutputStreamSender() {
			// the callback receives the output stream
			public void write(OutputStream out) throws IOException {
				// acquire the content
				InputStream docStreamwrongjson = null;
				try {
					 docStreamwrongjson = new FileInputStream(
							WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "WrongFormat.json").getPath());

					// copy content to the output stream
					byte[] buf = new byte[1024];
					int byteCount = 0;
					while ((byteCount = docStreamwrongjson.read(buf)) != -1) {
						out.write(buf, 0, byteCount);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					docStreamwrongjson.close();
				}

			}
		};

		// create the handle
		osHandle = new OutputStreamHandle(sender);
		osHandle.withFormat(Format.JSON);

		sender1 = new OutputStreamSender() {
			// the callback receives the output stream
			@Override
			public void write(OutputStream out) throws IOException {
				// copy content to the output stream
				try ( // acquire the content
						InputStream docStreamwrongjson1 = new FileInputStream(WriteHostBatcherTest.class
								.getResource(TEST_DIR_PREFIX + "product-apple.json").getPath())) {
					// copy content to the output stream
					byte[] buf = new byte[1024];
					int byteCount = 0;
					while ((byteCount = docStreamwrongjson1.read(buf)) != -1) {
						out.write(buf, 0, byteCount);
					}
				}
			}
		};

		// create the handle
		osHandle1 = new OutputStreamHandle(sender1);
		osHandle1.withFormat(Format.JSON);

		// ReaderHandle
		docStream = new BufferedReader(
				new FileReader(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "WrongFormat.xml").getPath()));
		readerHandle = new ReaderHandle();
		readerHandle.withFormat(Format.XML);
		readerHandle.set(docStream);

		docStream1 = new BufferedReader(
				new FileReader(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "employee.xml").getPath()));
		readerHandle1 = new ReaderHandle();
		readerHandle1.withFormat(Format.XML);
		readerHandle1.set(docStream1);

		// BytesHandle
		File file = FileUtils.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "dir.json"));

		try (FileInputStream fis = new FileInputStream(file); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			byte[] buf = new byte[1024];
			for (int readNum; (readNum = fis.read(buf)) != -1;) {
				bos.write(buf, 0, readNum);
			}
			bytesJson = bos.toByteArray();
		}

		bytesHandle = new BytesHandle();
		bytesHandle.setFormat(Format.JSON);
		bytesHandle.set(bytesJson);
	}

	// ISSUE 45
	@Test
	public void testAdd() throws Exception {
		System.out.println("In testAdd method");
		final StringBuffer successBatch = new StringBuffer();
		final StringBuffer failureBatch = new StringBuffer();
		final String query1 = "fn:count(fn:doc())";

		// Test 1 few failures with add (batchSize =1)
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		replenishStream();
		WriteBatcher ihb1 = dmManager.newWriteBatcher();
		ihb1.withBatchSize(1);
		ihb1.onBatchSuccess(batch -> {
			for (WriteEvent w : batch.getItems()) {
				successBatch.append(w.getTargetUri() + ":");
			}

		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();
			for (WriteEvent w : batch.getItems()) {

				failureBatch.append(w.getTargetUri() + ":");
			}

		});
		dmManager.startJob(ihb1);
		ihb1.add("/doc/jackson", jacksonHandle).add("/doc/reader_wrongxml", readerHandle)
				.add("/doc/string", docMeta1, stringHandle).add("/doc/file", docMeta2, fileHandle)
				.add("/doc/is", isHandle).add("/doc/os_wrongjson", docMeta2, osHandle)
				.add("/doc/bytes", docMeta1, bytesHandle).add("/doc/dom", domHandle);

		ihb1.flushAndWait();

		System.out.println("Success URI's: " + successBatch.toString());
		System.out.println("Failure URI's: " + failureBatch.toString());

		assertTrue(uriExists(failureBatch.toString(), "/doc/os_wrongjson"));
		assertTrue(uriExists(failureBatch.toString(), "/doc/reader_wrongxml"));

		DocumentMetadataHandle mHandle = readMetadataFromDocument(dbClient, "/doc/string", "XML");
		assertEquals("Sample Collection 1", mHandle.getCollections().iterator().next());
		assertTrue(mHandle.getCollections().size() == 1);
		System.out.println("Quality of /doc/string is " + mHandle.getQuality());
		assertEquals(1, mHandle.getQuality());

		DocumentMetadataHandle mHandle1 = readMetadataFromDocument(dbClient, "/doc/file", "XML");
		assertEquals(0, mHandle1.getQuality());
		assertEquals("Sample Collection 2", mHandle1.getCollections().iterator().next());
		assertTrue(mHandle1.getCollections().size() == 1);

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 6);

		successBatch.delete(0, successBatch.length());
		failureBatch.delete(0, failureBatch.length());
		clearDB(port);

		// ISSUE # 38
		// Test 2 All failure with add (batchSize =8)
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		replenishStream();
		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		ihb2.withBatchSize(8);
		ihb2.onBatchSuccess(batch -> {
			for (WriteEvent w : batch.getItems()) {
				successBatch.append(w.getTargetUri() + ":");
			}

		}).onBatchFailure((batch, throwable) -> {
			for (WriteEvent w : batch.getItems()) {
				failureBatch.append(w.getTargetUri() + ":");
			}

		});
		dmManager.startJob(ihb2);
		ihb2.add("/doc/jackson", jacksonHandle).add("/doc/reader_wrongxml", readerHandle)
				.add("/doc/string", docMeta1, stringHandle).add("/doc/file", docMeta2, fileHandle)
				.add("/doc/is", isHandle).add("/doc/os_wrongjson", docMeta2, osHandle)
				.add("/doc/bytes", docMeta1, bytesHandle).add("/doc/dom", domHandle);

		ihb2.flushAndWait();
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		assertTrue(uriExists(failureBatch.toString(), "/doc/reader_wrongxml"));

		successBatch.delete(0, successBatch.length());
		failureBatch.delete(0, failureBatch.length());
		clearDB(port);

		// Test 3 All success with add (batchSize =8)
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		replenishStream();
		WriteBatcher ihb3 = dmManager.newWriteBatcher();
		ihb3.withBatchSize(8);
		ihb3.onBatchSuccess(batch -> {
			for (WriteEvent w : batch.getItems()) {
				successBatch.append(w.getTargetUri() + ":");

			}

		}).onBatchFailure((batch, throwable) -> {
			for (WriteEvent w : batch.getItems()) {

				failureBatch.append(w.getTargetUri() + ":");
			}
		});
		dmManager.startJob(ihb3);

		ihb3.add("/doc/jackson", docMeta2, jacksonHandle).add("/doc/reader_xml", docMeta1, readerHandle1)
				.add("/doc/string", stringHandle).add("/doc/file", fileHandle).add("/doc/is", docMeta2, isHandle)
				.add("/doc/os_json", osHandle1).add("/doc/bytes", bytesHandle).add("/doc/dom", docMeta1, domHandle);

		ihb3.flushAndWait();

		System.out.println("Size is " + dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 8);

		DocumentMetadataHandle mHandle2 = readMetadataFromDocument(dbClient, "/doc/reader_xml", "XML");
		assertEquals(1, mHandle2.getQuality());
		assertEquals("Sample Collection 1", mHandle2.getCollections().iterator().next());
		assertTrue(mHandle2.getCollections().size() == 1);

		DocumentMetadataHandle mHandle3 = readMetadataFromDocument(dbClient, "/doc/jackson", "XML");
		assertEquals(0, mHandle3.getQuality());
		assertEquals("Sample Collection 2", mHandle3.getCollections().iterator().next());
		assertTrue(mHandle3.getCollections().size() == 1);

		assertTrue(uriExists(successBatch.toString(), "/doc/os_json"));
		assertFalse(uriExists(successBatch.toString(), "/doc/reader_wrongxml"));

		successBatch.delete(0, successBatch.length());
		failureBatch.delete(0, failureBatch.length());
		clearDB(port);

		// Test 4 All failures in 2 batches
		Thread.currentThread().sleep(1500L);
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		replenishStream();
		WriteBatcher ihb4 = dmManager.newWriteBatcher();
		ihb4.withBatchSize(4);
		ihb4.onBatchSuccess(batch -> {
			for (WriteEvent w : batch.getItems()) {
				successBatch.append(w.getTargetUri() + ":");

			}

		}).onBatchFailure((batch, throwable) -> {
			for (WriteEvent w : batch.getItems()) {

				failureBatch.append(w.getTargetUri() + ":");
			}
		});
		dmManager.startJob(ihb4);

		ihb4.add("/doc/jackson", docMeta2, jacksonHandle).add("/doc/reader_wrongxml", docMeta1, readerHandle)
				.add("/doc/string", stringHandle).add("/doc/file", fileHandle);
		ihb4.flushAndWait();

		ihb4.add("/doc/is", docMeta2, isHandle).add("/doc/os_wrongjson", osHandle).add("/doc/bytes", bytesHandle)
				.add("/doc/dom", docMeta1, domHandle);
		ihb4.flushAndWait();

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		assertTrue(uriExists(failureBatch.toString(), "/doc/reader_wrongxml"));
		assertTrue(uriExists(failureBatch.toString(), "/doc/os_wrongjson"));
	}

	// ISSUE 60
	@Test
	public void testAddAs() throws Exception {
		System.out.println("In testAddAs method");
		final StringBuffer successBatch = new StringBuffer();
		final StringBuffer failureBatch = new StringBuffer();
		final String query1 = "fn:count(fn:doc())";

		// Test 1 All success with addAs (batchSize =8)
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		replenishStream();
		WriteBatcher ihb3 = dmManager.newWriteBatcher();
		ihb3.withBatchSize(8);
		ihb3.onBatchSuccess(batch -> {
			for (WriteEvent w : batch.getItems()) {
				successBatch.append(w.getTargetUri() + ":");

			}

		}).onBatchFailure((batch, throwable) -> {
			for (WriteEvent w : batch.getItems()) {

				failureBatch.append(w.getTargetUri() + ":");
			}
		});
		dmManager.startJob(ihb3);

		ihb3.addAs("/doc/jackson", docMeta2, jsonNode).addAs("/doc/reader_xml", docMeta1, docStream1)
				.addAs("/doc/string", stringTriple).addAs("/doc/dom", docMeta1, docContent);

		ihb3.flushAndWait();
		System.out.println("Size is " + dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 4);

		DocumentMetadataHandle mHandle2 = readMetadataFromDocument(dbClient, "/doc/reader_xml", "XML");
		assertEquals(1, mHandle2.getQuality());
		assertEquals("Sample Collection 1", mHandle2.getCollections().iterator().next());
		assertTrue(mHandle2.getCollections().size() == 1);

		DocumentMetadataHandle mHandle3 = readMetadataFromDocument(dbClient, "/doc/jackson", "XML");
		assertEquals(0, mHandle3.getQuality());
		assertEquals("Sample Collection 2", mHandle3.getCollections().iterator().next());
		assertTrue(mHandle3.getCollections().size() == 1);

		assertTrue(uriExists(successBatch.toString(), "/doc/string"));

		successBatch.delete(0, successBatch.length());
		failureBatch.delete(0, failureBatch.length());
		clearDB(port);
	}

	// ISSUE 60
	@Test
	public void testAddandAddAs() throws Exception {
		System.out.println("In testAddandAddAs method");
		final StringBuffer successBatch = new StringBuffer();
		final StringBuffer failureBatch = new StringBuffer();
		final String query1 = "fn:count(fn:doc())";

		// Test 1 few failures with addAs and add(batchSize =1)
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		replenishStream();
		WriteBatcher ihb1 = dmManager.newWriteBatcher();
		ihb1.withBatchSize(1);
		ihb1.onBatchSuccess(batch -> {
			for (WriteEvent w : batch.getItems()) {
				successBatch.append(w.getTargetUri() + ":");
			}

		}).onBatchFailure((batch, throwable) -> {
			for (WriteEvent w : batch.getItems()) {
				failureBatch.append(w.getTargetUri() + ":");
			}
		});
		dmManager.startJob(ihb1);
		ihb1.addAs("/doc/jackson", jsonNode).add("/doc/reader_wrongxml", readerHandle)
				.addAs("/doc/string", docMeta1, stringTriple).add("/doc/file", docMeta2, fileHandle)
				.add("/doc/is", isHandle).add("/doc/os_wrongjson", docMeta2, osHandle)
				.add("/doc/bytes", docMeta1, bytesHandle).addAs("/doc/dom", domHandle);

		ihb1.flushAndWait();

		assertTrue(uriExists(failureBatch.toString(), "/doc/os_wrongjson"));
		assertTrue(uriExists(failureBatch.toString(), "/doc/reader_wrongxml"));

		DocumentMetadataHandle mHandle = readMetadataFromDocument(dbClient, "/doc/string", "XML");
		assertEquals(1, mHandle.getQuality());
		assertEquals("Sample Collection 1", mHandle.getCollections().iterator().next());
		assertTrue(mHandle.getCollections().size() == 1);

		DocumentMetadataHandle mHandle1 = readMetadataFromDocument(dbClient, "/doc/file", "XML");
		assertEquals(0, mHandle1.getQuality());
		assertEquals("Sample Collection 2", mHandle1.getCollections().iterator().next());
		assertTrue(mHandle1.getCollections().size() == 1);

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 6);

		successBatch.delete(0, successBatch.length());
		failureBatch.delete(0, failureBatch.length());
		clearDB(port);

		// ISSUE # 38
		// Test 2 All failure with addAs and add(batchSize =8)
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		replenishStream();
		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		ihb2.withBatchSize(8);
		ihb2.onBatchSuccess(batch -> {
			for (WriteEvent w : batch.getItems()) {
				successBatch.append(w.getTargetUri() + ":");
			}

		}).onBatchFailure((batch, throwable) -> {
			for (WriteEvent w : batch.getItems()) {
				failureBatch.append(w.getTargetUri() + ":");
			}
		});
		dmManager.startJob(ihb2);
		ihb2.add("/doc/jackson", jacksonHandle).add("/doc/reader_wrongxml", readerHandle)
				.add("/doc/string", docMeta1, stringHandle).addAs("/doc/file", docMeta2, fileJson)
				.add("/doc/is", isHandle).add("/doc/os_wrongjson", docMeta2, osHandle)
				.addAs("/doc/bytes", docMeta1, bytesJson).addAs("/doc/dom", domHandle);

		ihb2.flushAndWait();
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		assertTrue(uriExists(failureBatch.toString(), "/doc/reader_wrongxml"));

		successBatch.delete(0, successBatch.length());
		failureBatch.delete(0, failureBatch.length());
		clearDB(port);

		// Test 3 All success with addAs and add(batchSize =8)
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		replenishStream();
		WriteBatcher ihb3 = dmManager.newWriteBatcher();
		ihb3.withBatchSize(8);
		ihb3.onBatchSuccess(batch -> {
			for (WriteEvent w : batch.getItems()) {
				successBatch.append(w.getTargetUri() + ":");

			}

		}).onBatchFailure((batch, throwable) -> {
			for (WriteEvent w : batch.getItems()) {

				failureBatch.append(w.getTargetUri() + ":");
			}
		});
		dmManager.startJob(ihb3);

		ihb3.addAs("/doc/jackson", docMeta2, jsonNode).add("/doc/reader_xml", docMeta1, osHandle1)
				.addAs("/doc/string", stringTriple).add("/doc/file", fileHandle).add("/doc/is", docMeta2, isHandle)
				.add("/doc/os_json", osHandle1).add("/doc/bytes", bytesHandle).addAs("/doc/dom", docMeta1, docContent);

		ihb3.flushAndWait();
		System.out.println("Size is " + dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 8);

		DocumentMetadataHandle mHandle2 = readMetadataFromDocument(dbClient, "/doc/reader_xml", "XML");
		assertEquals(1, mHandle2.getQuality());
		assertEquals("Sample Collection 1", mHandle2.getCollections().iterator().next());
		assertTrue(mHandle2.getCollections().size() == 1);

		DocumentMetadataHandle mHandle3 = readMetadataFromDocument(dbClient, "/doc/jackson", "XML");
		assertEquals(0, mHandle3.getQuality());
		assertEquals("Sample Collection 2", mHandle3.getCollections().iterator().next());
		assertTrue(mHandle3.getCollections().size() == 1);

		assertTrue(uriExists(successBatch.toString(), "/doc/os_json"));
		assertFalse(uriExists(successBatch.toString(), "/doc/reader_wrongxml"));

		successBatch.delete(0, successBatch.length());
		failureBatch.delete(0, failureBatch.length());
		clearDB(port);

		// Test 4 All failures in 2 batches
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		replenishStream();
		WriteBatcher ihb4 = dmManager.newWriteBatcher();
		ihb4.withBatchSize(4);
		ihb4.onBatchSuccess(batch -> {
			for (WriteEvent w : batch.getItems()) {
				successBatch.append(w.getTargetUri() + ":");

			}

		}).onBatchFailure((batch, throwable) -> {
			for (WriteEvent w : batch.getItems()) {

				failureBatch.append(w.getTargetUri() + ":");
			}
		});
		dmManager.startJob(ihb4);

		ihb4.addAs("/doc/jackson", docMeta2, jsonNode).add("/doc/reader_wrongxml", docMeta1, readerHandle)
				.add("/doc/string", stringHandle).addAs("/doc/file", fileJson);
		ihb4.flushAndWait();

		ihb4.add("/doc/is", docMeta2, isHandle).add("/doc/os_wrongjson", osHandle).add("/doc/bytes", bytesHandle)
				.addAs("/doc/dom", docMeta1, docContent);
		ihb4.flushAndWait();

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		assertTrue(uriExists(failureBatch.toString(), "/doc/reader_wrongxml"));
		assertTrue(uriExists(failureBatch.toString(), "/doc/os_wrongjson"));
	}

	private boolean uriExists(String s, String in) {
		return s.contains(in);
	}

	// Immutability of WriteBatcher- ISSUE # 26
	@Test
	public void testHostBatcherImmutability() throws Exception {
		System.out.println("In testHostBatcherImmutability method");

		WriteBatcher ihb = dmManager.newWriteBatcher();
		ServerTransform transform = null;
		ForestConfiguration forestConfig = dmManager.readForestConfig();

		ihb.withJobName(null);
		ihb.withBatchSize(2);
		ihb.withBatchSize(5);
		ihb.withTransform(transform);
		ihb.withThreadCount(5);
		ihb.withForestConfig(forestConfig);

		dmManager.startJob(ihb);
		try {
			ihb.withJobName("Job 2");
			fail("Expection should have been thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}

		try {
			ihb.withBatchSize(1);
			fail("Expection should have been thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}
		ihb.add("/local/triple", stringHandle);
		try {
			ihb.withJobName("Job 2");
			fail("Expection should have been thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}

		try {
			ihb.withBatchSize(1);
			fail("Expection should have been thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}

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

		transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "English");

		try {
			ihb.withTransform(transform);
			fail("Expection should have been thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}

	}

	// ISSUE # 38
	@Test
	public void testNumberofBatches() throws Exception {
		System.out.println("In testNumberofBatches method");

		final AtomicInteger numberOfSuccessFulBatches = new AtomicInteger(0);
		final AtomicBoolean state = new AtomicBoolean(true);

		WriteBatcher ihb1 = dmManager.newWriteBatcher();
		ihb1.withBatchSize(5);
		ihb1.onBatchSuccess(batch -> {
			numberOfSuccessFulBatches.incrementAndGet();

		}).onBatchFailure((batch, throwable) -> {
			state.set(false);

		});
		dmManager.startJob(ihb1);

		for (int i = 0; i < 101; i++) {
			String uri = "/local/json-" + i;
			ihb1.add(uri, jacksonHandle);
		}

		ihb1.flushAndWait();
		assertTrue(state.get());
		System.out.println(numberOfSuccessFulBatches.intValue());
		assertTrue(numberOfSuccessFulBatches.intValue() == 21);

	}

	// ISSUE # 39, 40, 589
	@Test
	public void testClientObject() throws Exception {
		System.out.println("In testClientObject method");

		final StringBuffer successHost = new StringBuffer();
		final StringBuffer successDb = new StringBuffer();
		final StringBuffer successPort = new StringBuffer();

		final StringBuffer failureHost = new StringBuffer();
		final StringBuffer failureDb = new StringBuffer();
		final StringBuffer failurePort = new StringBuffer();

		WriteBatcher ihb1 = dmManager.newWriteBatcher();
		ihb1.withBatchSize(1);
		ihb1.onBatchSuccess(batch -> {
			successHost.append(batch.getClient().getHost() + ":");
			successPort.append(batch.getClient().getPort() + ":");
			successDb.append(batch.getClient().getDatabase() + ":");

		}).onBatchFailure((batch, throwable) -> {
			failureHost.append(batch.getClient().getHost() + ":");
			failurePort.append(batch.getClient().getPort() + ":");
			failureDb.append(batch.getClient().getDatabase() + ":");
		});
		dmManager.startJob(ihb1);

		for (int i = 0; i < 10; i++) {
			String uri = "/local/json-" + i;
			ihb1.add(uri, stringHandle);
		}

		for (int i = 0; i < 5; i++) {
			ihb1.add("", stringHandle);
		}
		ihb1.flushAndWait();
		System.out.println(successHost.toString());
		System.out.println(successPort.toString());
		System.out.println(successDb.toString());

		assertTrue(count(successPort.toString(), String.valueOf(port)) == 10);
		if (hostNames.length > 1 && !isLBHost()) {
			assertTrue(count(successHost.toString(), String.valueOf(host)) != 10);
		}

		assertTrue(count(failurePort.toString(), String.valueOf(port)) == 5);
		if (hostNames.length > 1 && !isLBHost()) {
			assertTrue(count(failureHost.toString(), String.valueOf(host)) != 5);
		}

	}

	private int count(String s, String in) {
		int i = 0;
		Pattern p = Pattern.compile(in);
		Matcher m = p.matcher(s);
		while (m.find()) {
			i++;
		}
		return i;
	}

	// ISSUE 549
	@Test
	public void testBatchObject() throws Exception {
		System.out.println("In testBatchObject method");

		WriteBatcher ihb1 = dmManager.newWriteBatcher();

		AtomicBoolean succObj = new AtomicBoolean(false);
		AtomicBoolean failObj = new AtomicBoolean(false);

		ihb1.withBatchSize(10);
		ihb1.onBatchSuccess(batch -> {
			System.out.println("Success");
			if (batch.getJobTicket() == testBatchJobTicket) {
				succObj.set(true);
			}
			if (batch.getJobBatchNumber() == 1 && succObj.get()) {
				System.out.println(batch.getJobBatchNumber());
				succObj.set(true);
			} else {
				succObj.set(false);
			}
			if (batch.getJobWritesSoFar() == 10 && succObj.get()) {
				System.out.println(batch.getJobWritesSoFar());
				succObj.set(true);
			} else {
				succObj.set(false);
			}
			// assuming that comparison takes place on the same day as document
			// write

			if (Math.abs(batch.getTimestamp().get(Calendar.DAY_OF_MONTH)
					- Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) == 0 && succObj.get()) {
				succObj.set(true);
			} else {
				succObj.set(false);
			}

		}).onBatchFailure((batch, throwable) -> {
			System.out.println("Failure");
			if (batch.getJobTicket() == testBatchJobTicket) {
				failObj.set(true);
			}
			if (batch.getJobBatchNumber() == 2 && failObj.get()) {
				System.out.println(batch.getJobBatchNumber());
				failObj.set(true);
			} else {
				failObj.set(false);
			}
			if (batch.getJobWritesSoFar() == 10 && failObj.get()) {
				System.out.println(batch.getJobWritesSoFar());
				failObj.set(true);
			} else {
				failObj.set(false);
			}
			// assuming that comparison takes place on the same day as
			// document write
			if (Math.abs(batch.getTimestamp().get(Calendar.DAY_OF_MONTH)
					- Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) == 0 && failObj.get()) {
				failObj.set(true);
			} else {
				failObj.set(false);
			}

		});
		testBatchJobTicket = dmManager.startJob(ihb1);

		for (int i = 0; i < 10; i++) {
			String uri = "/local/json-" + i;
			ihb1.add(uri, stringHandle);
		}

		for (int i = 0; i < 5; i++) {
			ihb1.add("", stringHandle);
		}
		ihb1.flushAndWait();
		dmManager.stopJob(testBatchJobTicket);
		assertTrue(succObj.get());
		assertTrue(failObj.get());

	}

	// ISSUE # 28- expected to fail in ea2
	@Test
	public void testWithInvalidValues() throws Exception {
		System.out.println("In testWithInvalidValues method");

		final String query1 = "fn:count(fn:doc())";
		WriteBatcher ihb1 = dmManager.newWriteBatcher();

		try {
			ihb1.withBatchSize(-20);
			fail("Expection should have been thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		try {
			ihb2.withBatchSize(0);
			fail("Expection should have been thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);

		WriteBatcher ihb3 = dmManager.newWriteBatcher();
		try {
			ihb3.withThreadCount(-4);
			fail("Expection should have been thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			ihb3.withThreadCount(0);
			fail("Expection should have been thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void testflushAsync() throws Exception {
		System.out.println("In testflushAsync method");

		final String query1 = "fn:count(fn:doc())";
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		WriteBatcher ihb1 = dmManager.newWriteBatcher();
		ihb1.withBatchSize(25);
		ihb1.onBatchSuccess(batch -> {

		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();
		});

		for (int i = 0; i < 1000; i++) {
			String uri = "/local/json-" + i;
			ihb1.add(uri, stringHandle);
		}

		ihb1.flushAndWait();
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 1000);
		clearDB(port);
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		for (int i = 0; i < 1500; i++) {
			String uri = "/local/json-" + i;
			ihb1.add(uri, stringHandle);
		}
		ihb1.flushAsync();
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() < 1500);
		System.out.println(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		ihb1.awaitCompletion();
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 1500);
	}

	@Test
	public void testInsertoReadOnlyForest() throws Exception {
		System.out.println("In testInsertoReadOnlyForest method");

		Map<String, String> properties = new HashMap<>();
		properties.put("updates-allowed", "read-only");
		for (int i = 0; i < forestCount; i++)
			changeProperty(properties, "/manage/v2/forests/" + dbName + "-" + (i + 1) + "/properties");
		final String query1 = "fn:count(fn:doc())";

		final AtomicInteger successCount = new AtomicInteger(0);

		final AtomicBoolean failState = new AtomicBoolean(false);
		final AtomicInteger failCount = new AtomicInteger(0);

		for (int i = 0; i < forestCount; i++)
			changeProperty(properties, "/manage/v2/forests/" + dbName + "-" + (i + 1) + "/properties");

		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		ihb2.withBatchSize(25);
		ihb2.onBatchSuccess(batch -> {

			successCount.addAndGet(batch.getItems().length);
		}).onBatchFailure((batch, throwable) -> {
			failState.set(true);
			failCount.addAndGet(batch.getItems().length);
		});

		dmManager.startJob(ihb2);
		for (int j = 0; j < 20; j++) {
			String uri = "/local/json-" + j;
			ihb2.addAs(uri, stringHandle);
		}

		ihb2.flushAndWait();

		properties.put("updates-allowed", "all");
		for (int i = 0; i < forestCount; i++)
			changeProperty(properties, "/manage/v2/forests/" + dbName + "-" + (i + 1) + "/properties");

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);

		assertTrue(failState.get());

		assertTrue(successCount.intValue() == 0);
		assertTrue(failCount.intValue() == 20);
	}

	@Test
	public void testInsertoDisabledDB() throws Exception {
		System.out.println("In testInsertoDisabledDB method");

		Map<String, String> properties = new HashMap<>();
		properties.put("enabled", "false");
		final String query1 = "fn:count(fn:doc())";

		final AtomicInteger successCount = new AtomicInteger(0);

		final AtomicBoolean failState = new AtomicBoolean(false);
		final AtomicInteger failCount = new AtomicInteger(0);

		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		ihb2.withBatchSize(30);
		dmManager.startJob(ihb2);

		for (int j = 0; j < 20; j++) {
			String uri = "/local/json-" + j;
			ihb2.add(uri, stringHandle);
		}

		ihb2.onBatchSuccess(batch -> {

			successCount.getAndAdd(batch.getItems().length);

		}).onBatchFailure((batch, throwable) -> {
			failState.set(true);
			failCount.getAndAdd(batch.getItems().length);
		});

		changeProperty(properties, "/manage/v2/databases/" + dbName + "/properties");

		ihb2.flushAndWait();

		properties.put("enabled", "true");
		changeProperty(properties, "/manage/v2/databases/" + dbName + "/properties");

		System.out.println("Fail : " + failCount.intValue());
		System.out.println("Success : " + successCount.intValue());
		System.out.println("Count : " + dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		assertTrue(failState.get());

		assertTrue(failCount.intValue() == 20);
	}

	@Test
	public void testServerXQueryTransformSuccess() throws Exception {
		System.out.println("In testServerXQueryTransformSuccess method");

		final String query1 = "fn:count(fn:doc())";
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		final AtomicInteger successCount = new AtomicInteger(0);

		final AtomicBoolean failState = new AtomicBoolean(false);
		final AtomicInteger failCount = new AtomicInteger(0);
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

		ServerTransform transform = null;
		transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "English");

		String xmlStr1 = "<?xml  version=\"1.0\" encoding=\"UTF-8\"?><foo>This is so foo</foo>";
		String xmlStr2 = "<?xml  version=\"1.0\" encoding=\"UTF-8\"?><foo>This is so bar</foo>";

		// Use WriteBatcher to write the same files.
		WriteBatcher ihb1 = dmManager.newWriteBatcher();
		ihb1.withBatchSize(2);
		ihb1.withTransform(transform);
		ihb1.onBatchSuccess(batch -> {

			successCount.getAndAdd(batch.getItems().length);

		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();
			failState.set(true);
			failCount.getAndAdd(batch.getItems().length);
		});
		dmManager.startJob(ihb1);
		StringHandle handleFoo = new StringHandle();
		handleFoo.set(xmlStr1);
		handleFoo.setFormat(Format.XML);

		StringHandle handleBar = new StringHandle();
		handleBar.set(xmlStr2);
		handleBar.setFormat(Format.XML);

		List<String> uris = new ArrayList<String>();
		String uri1 = null;
		String uri2 = null;

		for (int i = 0; i < 4; i++) {
			uri1 = "foo" + i + ".xml";
			uri2 = "bar" + i + ".xml";
			uris.add(uri1);
			uris.add(uri2);
			ihb1.addAs(uri1, handleFoo).addAs(uri2, handleBar);
		}
		ihb1.flushAndWait();
		dmManager.stopJob(ihb1);
		String[] uriArr = new String[8];
		uris.toArray(uriArr);
		int count = 0;
		DocumentPage page = dbClient.newDocumentManager().read(uriArr);
		DOMHandle dh = new DOMHandle();
		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			if (dh.get().getElementsByTagName("foo").item(0).hasAttributes()) {
				System.out.println(dh.get().getElementsByTagName("foo").item(0).getTextContent());
				System.out.println(count);
				assertEquals("English", dh.get().getElementsByTagName("foo").item(0).getAttributes().item(0).getNodeValue());
				count++;
			}
		}

		assertFalse(failState.get());
		assertTrue(successCount.intValue() == 8);
		assertTrue(count == 8);
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 8);
	}

	@Test
	public void testServerXQueryTransformFailure() throws Exception {
		System.out.println("In testServerXQueryTransformFailure method");

		final String query1 = "fn:count(fn:doc())";
		final AtomicInteger successCount = new AtomicInteger(0);

		final AtomicBoolean failState = new AtomicBoolean(false);
		final AtomicInteger failCount = new AtomicInteger(0);
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

		// Use WriteBatcher to write the same files.
		WriteBatcher ihb1 = dmManager.newWriteBatcher();
		ihb1.withBatchSize(1);
		ihb1.withTransform(transform);
		ihb1.onBatchSuccess(batch -> {

			successCount.getAndAdd(batch.getItems().length);

		}).onBatchFailure((batch, throwable) -> {
			failState.set(true);
			failCount.getAndAdd(batch.getItems().length);
		});

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
			ihb1.add(uri1, handleFoo).add(uri2, handleBar);
			;
		}
		// Flush
		ihb1.flushAndWait();
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 4);
		assertTrue(failState.get());
		assertTrue(successCount.intValue() == 4);
		assertTrue(failCount.intValue() == 4);

		clearDB(port);
		failCount.set(0);
		successCount.set(0);
		failState.set(false);

		// with non-existent transform

		ServerTransform transform1 = new ServerTransform("abcd");
		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		ihb2.withBatchSize(1);
		ihb2.withTransform(transform1);
		ihb2.onBatchSuccess(batch -> {

			successCount.getAndAdd(batch.getItems().length);

		}).onBatchFailure((batch, throwable) -> {
			failState.set(true);
			failCount.getAndAdd(batch.getItems().length);
		});
		for (int i = 0; i < 4; i++) {
			uri1 = "foo" + i + ".xml";
			ihb2.add(uri1, handleFoo);
		}
		// Flush
		ihb2.flushAndWait();
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		assertTrue(failState.get());
		assertTrue(successCount.intValue() == 0);
		assertTrue(failCount.intValue() == 4);
	}

	// Multiple threads writing to same WHB object with unique uri's
	@Test
	public void testAddMultiThreadedSuccess() throws Exception {
		System.out.println("In testAddMultiThreadedSuccess method");

		final String query1 = "fn:count(fn:doc())";
		ihbMT = dmManager.newWriteBatcher();
		ihbMT.withBatchSize(100);
		ihbMT.onBatchSuccess(batch -> {
		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();

		});
		dmManager.startJob(ihbMT);

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
		System.out.println(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		assertEquals(300, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
	}

	// ISSUE 48
	@Test
	public void testAddMultiThreadedFailureEventCount() throws Exception {
		System.out.println("In testAddMultiThreadedFailureEventCount method");

		final AtomicInteger eventCount = new AtomicInteger(0);
		ihbMT = dmManager.newWriteBatcher();
		ihbMT.withBatchSize(105);
		ihbMT.onBatchSuccess(batch -> {
			synchronized (eventCount) {
				eventCount.getAndAdd(batch.getItems().length);
			}
		}).onBatchFailure((batch, throwable) -> {
			synchronized (eventCount) {
				eventCount.getAndAdd(batch.getItems().length);
			}

		});
		dmManager.startJob(ihbMT);

		class MyRunnable implements Runnable {

			@Override
			public void run() {

				for (int j = 0; j < 100; j++) {
					String uri = "/local/json-" + j;
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
		System.out.println(eventCount.intValue());
		assertTrue(eventCount.intValue() == 300);

	}

	// ISSUE 85
	// Multiple threads writing to same WHB object with unique uri's and with
	// thread count =10 and txsize =3
	@Test
	public void testAddMultiThreadedwithTransactionsizeSuccess() throws Exception {
		System.out.println("In testAddMultiThreadedwithTransactionsizeSuccess method");

		final String query1 = "fn:count(fn:doc())";
		final AtomicInteger count = new AtomicInteger(0);

		ihbMT = dmManager.newWriteBatcher();
		ihbMT.withBatchSize(99);
		ihbMT.withThreadCount(10);
		// ihbMT.withTransactionSize(3);

		ihbMT.onBatchSuccess(batch -> {

		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();

		});
		dmManager.startJob(ihbMT);

		class MyRunnable implements Runnable {

			@Override
			public void run() {

				for (int j = 0; j < 5000; j++) {
					String uri = "/local/json-" + j + "-" + Thread.currentThread().getId();
					System.out.println("Thread name: " + Thread.currentThread().getName() + "  URI:" + uri);
					ihbMT.add(uri, fileHandle);
				}
				ihbMT.flushAndWait();
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

		Thread t1, t2, t3;
		t1 = new Thread(new MyRunnable());
		t2 = new Thread(new MyRunnable());
		t3 = new Thread(new MyRunnable());

		countT.start();
		t1.start();
		t2.start();
		t3.start();

		countT.join();

		t1.join();
		t2.join();
		t3.join();
		// Verify more than 1 thread is spawned
		assertTrue(count.intValue() > 1);
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 15000);
		clearDB(port);
	}

	// Multiple threads writing to same WHB object with unique uri's and with
	// thread count =10 and txsize =3 but with small number of docs
	// Failing intermittently with rewriteWHB branch
	@Disabled
	public void testAddMultiThreadedLessDocsSuccess() throws Exception {
		System.out.println("In testAddMultiThreadedLessDocsSuccess method");

		final String query1 = "fn:count(fn:doc())";
		final AtomicInteger count = new AtomicInteger(0);

		ihbMT = dmManager.newWriteBatcher();
		ihbMT.withBatchSize(99);
		ihbMT.withThreadCount(10);
		// ihbMT.withTransactionSize(3);

		ihbMT.onBatchSuccess(batch -> {

		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();

		});
		dmManager.startJob(ihbMT);

		class MyRunnable implements Runnable {

			@Override
			public void run() {

				for (int j = 0; j < 15; j++) {
					String uri = "/local/json-" + j + "-" + Thread.currentThread().getId();
					System.out.println("Thread name: " + Thread.currentThread().getName() + "  URI:" + uri);
					ihbMT.add(uri, fileHandle);
				}
				ihbMT.flushAndWait();
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

		Thread t1, t2, t3;
		t1 = new Thread(new MyRunnable());
		t2 = new Thread(new MyRunnable());
		t3 = new Thread(new MyRunnable());

		countT.start();
		t1.start();
		t2.start();
		t3.start();

		countT.join();

		t1.join();
		t2.join();
		t3.join();

		// assertTrue(count.intValue()==10);
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 45);
		clearDB(port);
	}

	// Multiple threads writing to same WHB object with duplicate uri's and with
	// thread count =10 and txsize =3
	// currently causing deadlock and at the completion of the test, clearDB()
	// causes forests to go to middle closing state when run against reWriteHB
	// branch
	// Git Issue # 62
	@Disabled
	public void testAddMultiThreadedwithThreadCountFailure() throws Exception {
		System.out.println("In testAddMultiThreadedwithThreadCountFailure method");

		final AtomicInteger count = new AtomicInteger(0);
		final AtomicInteger eventCount = new AtomicInteger(0);

		ihbMT = dmManager.newWriteBatcher();
		ihbMT.withBatchSize(99);
		ihbMT.withThreadCount(10);
		// ihbMT.withTransactionSize(3);

		ihbMT.onBatchSuccess(batch -> {
			synchronized (eventCount) {
				eventCount.getAndAdd(batch.getItems().length);
			}

		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();
			synchronized (eventCount) {
				eventCount.getAndAdd(batch.getItems().length);
			}

		});
		dmManager.startJob(ihbMT);

		class MyRunnable implements Runnable {

			@Override
			public void run() {

				for (int j = 0; j < 5000; j++) {
					String uri = "/local/json-" + j;
					ihbMT.add(uri, fileHandle);
				}
				ihbMT.flushAndWait();
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
						count.incrementAndGet();

				}

			}

		}
		Thread countT;
		countT = new Thread(new CountRunnable());

		Thread t1, t2, t3;
		t1 = new Thread(new MyRunnable());
		t2 = new Thread(new MyRunnable());
		t3 = new Thread(new MyRunnable());

		countT.start();
		t1.start();
		t2.start();
		t3.start();

		countT.join();

		t1.join();
		t2.join();
		t3.join();

		assertTrue(count.intValue() == 15000);
		assertTrue(eventCount.intValue() == 10);

	}

	// ISSUE # 58
	@Disabled
	public void testTransactionSize() throws Exception {
		System.out.println("In testTransactionSize method");

		try {
			final String query1 = "fn:count(fn:doc())";

			final AtomicInteger successCount = new AtomicInteger(0);

			final AtomicBoolean failState = new AtomicBoolean(false);
			final AtomicInteger failCount = new AtomicInteger(0);

			WriteBatcher ihb2 = dmManager.newWriteBatcher();
			ihb2.withBatchSize(480);
			// ihb2.withTransactionSize(5);
			dmManager.startJob(ihb2);

			ihb2.onBatchSuccess(batch -> {

				successCount.getAndAdd(batch.getItems().length);
				System.out.println("Success Batch size " + batch.getItems().length);
				for (WriteEvent w : batch.getItems()) {
					System.out.println("Success " + w.getTargetUri());
				}

			}).onBatchFailure((batch, throwable) -> {
				throwable.printStackTrace();
				System.out.println("Failure Batch size " + batch.getItems().length);
				for (WriteEvent w : batch.getItems()) {
					System.out.println("Failure " + w.getTargetUri());
				}
				failState.set(true);
				failCount.getAndAdd(batch.getItems().length);
			});
			for (int j = 0; j < 500; j++) {
				String uri = "/local/ABC-" + j;
				ihb2.add(uri, stringHandle);
			}

			ihb2.flushAndWait();

			System.out.println("Fail : " + failCount.intValue());
			System.out.println("Success : " + successCount.intValue());
			System.out
					.println("Count : " + dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
			assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 500);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Adding 25000 docs with thread count = 20
	@Test
	public void testThreadSize() throws Exception {
		System.out.println("In testThreadSize method");

		try {
			final String query1 = "fn:count(fn:doc())";

			final AtomicInteger successCount = new AtomicInteger(0);

			final AtomicBoolean failState = new AtomicBoolean(false);
			final AtomicInteger failCount = new AtomicInteger(0);
			final AtomicBoolean count = new AtomicBoolean(false);

			WriteBatcher ihb2 = dmManager.newWriteBatcher();
			ihb2.withBatchSize(5);
			// ihb2.withTransactionSize(2);
			ihb2.withThreadCount(20);
			dmManager.startJob(ihb2);

			ihb2.onBatchSuccess(batch -> {

				successCount.getAndAdd(batch.getItems().length);
			}).onBatchFailure((batch, throwable) -> {
				throwable.printStackTrace();
				System.out.println("Failure Batch size " + batch.getItems().length);
				for (WriteEvent w : batch.getItems()) {
					System.out.println("Failure " + w.getTargetUri());
				}
				failState.set(true);
				failCount.getAndAdd(batch.getItems().length);
			});

			class MyRunnable implements Runnable {

				@Override
				public void run() {
					try {
						// Sleep for 4 seconds so that the threads are spawned
						Thread.currentThread().sleep(4000L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Set<Thread> threads = Thread.getAllStackTraces().keySet();
					Iterator<Thread> iter = threads.iterator();
					Map<String, Integer> threadMap = new HashMap<>();
					while (iter.hasNext()) {
						Thread t = iter.next();
						String threadName = t.getName();
						if (threadName.contains("pool")) {
							int i = threadName.indexOf('-', 1 + threadName.indexOf('-'));
							String poolname = threadName.substring(0, i);
							System.out.println("poolname: " + poolname);
							if (!threadMap.containsKey(poolname)) {
								threadMap.put(poolname, 1);
							} else {
								threadMap.put(poolname, new Integer(threadMap.get(poolname) + 1));
							}
						}
					}
					Iterator<Entry<String, Integer>> it = threadMap.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, Integer> pair = (Entry<String, Integer>) it.next();
						System.out.println("Thread pool: " + pair.getKey() + " = " + pair.getValue() + " Threads");
						if (pair.getValue() == 20) {
							count.set(true);
						}
						it.remove();
					}

				}

			}
			Thread t1;
			t1 = new Thread(new MyRunnable());
			t1.start();

			for (int j = 0; j < 25000; j++) {
				String uri = "/local/ABC-" + j;
				ihb2.add(uri, stringHandle);
			}

			ihb2.flushAndWait();
			t1.join();

			System.out.println("Fail : " + failCount.intValue());
			System.out.println("Success : " + successCount.intValue());
			System.out
					.println("Count : " + dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
			// Confirms that 20 threads were spawned
			assertTrue(count.get());

			// Confirms that the number of docs inserted = 50000
			assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 25000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNPESuccessCallBack() throws Exception {
		System.out.println("In testNPESuccessCallBack method");

		final String query1 = "fn:count(fn:doc())";

		final AtomicInteger successCount = new AtomicInteger(0);

		final AtomicBoolean failState = new AtomicBoolean(false);
		final AtomicInteger failCount = new AtomicInteger(0);
		final AtomicBoolean failureListenerNpe = new AtomicBoolean(false);

		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		ihb2.withBatchSize(5);
		dmManager.startJob(ihb2);

		ihb2.onBatchSuccess(batch -> {

			System.out.println("Success host : " + batch.getClient().getHost());
			System.out.println(batch.getItems().length);
			System.out.println("Job writes " + batch.getJobWritesSoFar());
			successCount.getAndAdd(batch.getItems().length);
			String s = null;
			// s.length is called purposefully to set off NEE.
			s.length();

		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();
			if (throwable.getMessage().contains("java.lang.NullPointerException")) {
				failureListenerNpe.set(true);
			}
			failState.set(true);
			failCount.getAndAdd(batch.getItems().length);
		});

		for (int j = 0; j < 30; j++) {
			String uri = "/local/json-" + j;
			ihb2.add(uri, stringHandle);
		}
		for (int i = 0; i < 5; i++) {
			ihb2.add("", stringHandle);
		}
		ihb2.flushAndWait();
		assertTrue(failState.get());
		assertFalse(failureListenerNpe.get());
		assertEquals(5, failCount.get());
		assertEquals(30, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());

	}

	@Test
	public void testNPEFailureCallBack() throws Exception {
		System.out.println("In testNPEFailureCallBack method");

		final String query1 = "fn:count(fn:doc())";

		final AtomicInteger successCount = new AtomicInteger(0);

		final AtomicBoolean failState = new AtomicBoolean(false);
		final AtomicInteger failCount = new AtomicInteger(0);
		final AtomicBoolean failureListenerNpe = new AtomicBoolean(false);

		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		ihb2.withBatchSize(6);
		dmManager.startJob(ihb2);

		ihb2.onBatchSuccess(batch -> {
			successCount.getAndAdd(batch.getItems().length);
		}).onBatchFailure((batch, throwable) -> {
			System.out.println("Failure:  " + batch.getJobWritesSoFar());
			System.out.println("Failure: " + throwable.getMessage());
			if (throwable.getMessage().contains("java.lang.NullPointerException")) {
				failureListenerNpe.set(true);
			}
			failState.set(true);
			failCount.getAndAdd(batch.getItems().length);
			String s = null;
			s.length();
		});

		for (int i = 0; i < 5; i++) {
			ihb2.add("", stringHandle);
		}
		ihb2.flushAndWait();
		assertTrue(failState.get());
		assertFalse(failureListenerNpe.get());
		assertEquals(5, failCount.get());
		assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());

	}

	@Test
	public void testEmptyFlush() throws Exception {
		System.out.println("In testEmptyFlush method");

		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		ihb2.withBatchSize(5);

		ihb2.onBatchSuccess(batch -> {
		}).onBatchFailure((batch, throwable) -> {

		});
		try {
			ihb2.flushAndWait();
			fail("Expection should have been thrown");
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof IllegalStateException);
		}

		JobTicket job = dmManager.startJob(ihb2);
		ihb2.flushAsync();
		ihb2.flushAndWait();
		ihb2.add("/new", fileHandle);
		dmManager.stopJob(job);
		final String query1 = "fn:count(fn:doc())";
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
	}

	@Test
	public void testAddMultiThreadedStopJob() throws Exception {
		System.out.println("In testAddMultiThreadedStopJob method");

		final String query1 = "fn:count(fn:doc())";
		WriteJobReportListener jrl = new WriteJobReportListener();

		ihbMT = dmManager.newWriteBatcher();
		ihbMT.withBatchSize(7).withThreadCount(60);

		ihbMT.setBatchSuccessListeners(jrl);
		ihbMT.setBatchFailureListeners(jrl);

		ihbMT.onBatchSuccess(batch -> {

			if (jrl.getSuccessEventsCount() > 6) {
				dmManager.stopJob(writeTicket);
				System.out.println("Job stopped");
			}
		})

				.onBatchFailure((batch, throwable) -> {
					throwable.printStackTrace();

				});
		writeTicket = dmManager.startJob(ihbMT);

		class MyRunnable implements Runnable {

			@Override
			public void run() {
				for (int j = 0; j < 3000; j++) {
					String uri = "/local/multi-" + j + "-" + Thread.currentThread().getId();
					ihbMT.add(uri, fileHandle);
				}
				System.out.println("Finished executing thread: " + Thread.currentThread().getName());
			}

		}
		Thread t1, t2;
		t1 = new Thread(new MyRunnable());
		t2 = new Thread(new MyRunnable());
		t1.setName("First Thread");
		t2.setName("Second Thread");

		t1.start();
		t2.start();

		t1.join();
		t2.join();

		try {
			ihbMT.flushAndWait();
			fail("Exception should have been thrown");
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains("This instance has been stopped"));

		}

		try {
			ihbMT.add("/new", fileHandle);
			fail("Exception should have been thrown");
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains("This instance has been stopped"));

		}

		ihbMT.awaitCompletion();

		int count = dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue();
		System.out.println(count);
		assertTrue(count >= 80);
		// This is a arbitrary number less than 6000 confirming the job was
		// stopped
		// midway.
		assertTrue(count <= 1000);
	}

	@Test
	public void testAddMultiStartJob() throws Exception {
		System.out.println("In testAddMultiStartJob method");

		final String query1 = "fn:count(fn:doc())";
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		ihbMT = dmManager.newWriteBatcher();
		ihbMT.withBatchSize(11);
		ihbMT.onBatchSuccess(batch -> {
			System.out.println("Batch size " + batch.getItems().length);
			for (WriteEvent w : batch.getItems()) {
				System.out.println("Success " + w.getTargetUri());
			}
		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();
			for (WriteEvent w : batch.getItems()) {
				System.out.println("Failure " + w.getTargetUri());
			}
		});

		class MyRunnable implements Runnable {

			@Override
			public void run() {
				writeTicket = dmManager.startJob(ihbMT);

				for (int j = 0; j < 100; j++) {
					String uri = "/local/multi-" + j + "-" + Thread.currentThread().getId();
					System.out.println("Thread name: " + Thread.currentThread().getName() + "  URI:" + uri);
					ihbMT.add(uri, fileHandle);
					if (j == 80) {
						dmManager.startJob(ihbMT);
						ihbMT.flushAndWait();
					}
				}
				ihbMT.flushAndWait();
			}

		}
		Thread t1, t2;
		t1 = new Thread(new MyRunnable());
		t2 = new Thread(new MyRunnable());

		t1.start();
		t2.start();

		t1.join();
		t2.join();
		System.out.println(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		assertEquals(200, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		dmManager.stopJob(writeTicket);
	}

	// test flushAsync()
	@Test
	public void testInserttoDisabledAppServer() throws Exception {
		System.out.println("In testInserttoDisabledAppServer method");

		final String query1 = "fn:count(fn:doc())";
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		Map<String, String> properties = new HashMap<>();

		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		ihb2.withBatchSize(3000);

		ihb2.onBatchSuccess(batch -> {
			System.out.println("Success Batch size " + batch.getItems().length);
			for (WriteEvent w : batch.getItems()) {
				System.out.println("Success " + w.getTargetUri());
			}
		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();
			System.out.println("Failure Batch size " + batch.getItems().length);
			for (WriteEvent w : batch.getItems()) {
				System.out.println("Failure " + w.getTargetUri());
			}
		});

		dmManager.startJob(ihb2);
		for (int j = 0; j < 200; j++) {
			String uri = "/local/json-" + j;
			ihb2.add(uri, stringHandle);
		}

		properties.put("server-name", server);
		properties.put("group-name", "Default");
		properties.put("enabled", "false");
		changeProperty(properties, "/manage/v2/servers/" + server + "/properties");
		Thread.currentThread().sleep(2000L);
		ihb2.flushAsync();
		Thread.currentThread().sleep(25000L);

		properties.put("enabled", "true");
		changeProperty(properties, "/manage/v2/servers/" + server + "/properties");
		ihb2.awaitCompletion();
		System.out.println("testInserttoDisabledAppServer: Size is "
				+ dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 200);

	}

	// ISSUE 588
	@Test
	public void testRetry() throws Exception {
		System.out.println("In testRetry method");

		final String query1 = "fn:count(fn:doc())";
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		AtomicBoolean successCalled = new AtomicBoolean(false);
		Map<String, String> properties = new HashMap<>();

		ihbMT = dmManager.newWriteBatcher();
		ihbMT.withBatchSize(3000);

		ihbMT.onBatchSuccess(batch -> {
			successCalled.set(true);
			System.out.println("Success Batch size " + batch.getItems().length);
			for (WriteEvent w : batch.getItems()) {
				System.out.println("Success " + w.getTargetUri());
			}
		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();
			System.out.println("Failure Batch size " + batch.getItems().length);
			for (WriteEvent w : batch.getItems()) {
				System.out.println("Failure " + w.getTargetUri());
			}
			try {
				Thread.currentThread().sleep(20000L);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ihbMT.retry(batch);
		});

		dmManager.startJob(ihbMT);
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
		System.out.println(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 200);
		assertTrue(successCalled.get());
	}

	// ea3
	@Test
	public void testDisableAppServerDuringInsert() throws Exception {
		System.out.println("In testDisableAppServerDuringInsert method");

		final String query1 = "fn:count(fn:doc())";
		Thread t1 = new Thread(new StopServerRunnable());
		t1.setName("Status Check");

		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		ihb2.withBatchSize(10);

		ihb2.onBatchSuccess(batch -> {
			System.out.println("Success Batch size " + batch.getItems().length);
			for (WriteEvent w : batch.getItems()) {
				System.out.println("Success " + w.getTargetUri());
			}
		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();
			System.out.println("Failure Batch size " + batch.getItems().length);
			for (WriteEvent w : batch.getItems()) {
				System.out.println("Failure " + w.getTargetUri());
			}
		});

		writeTicket = dmManager.startJob(ihb2);
		t1.start();

		for (int j = 0; j < 2000; j++) {
			String uri = "/local/json-" + j;
			ihb2.add(uri, fileHandle);
		}

		ihb2.flushAndWait();
		t1.join();
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 2000);

	}

	class StopServerRunnable implements Runnable {
		final String query1 = "fn:count(fn:doc())";
		Map<String, String> properties = new HashMap<>();

		@Override
		public void run() {
			properties.put("server-name", server);
			properties.put("group-name", "Default");
			properties.put("enabled", "false");
			boolean state = true;
			while (state) {
				long count = dmManager.getJobReport(writeTicket).getSuccessEventsCount();
				if (count >= 100L) {
					changeProperty(properties, "/manage/v2/servers/" + server + "/properties");
					System.out.println("Server disabled");
					state = false;
				}
			}
			try {
				Thread.currentThread().sleep(10000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			properties.put("server-name", server);
			properties.put("group-name", "Default");
			properties.put("enabled", "true");
			changeProperty(properties, "/manage/v2/servers/" + server + "/properties");
		}

	}

	// EA 3 - We need a better way of getting to know that DB is disabled and
	// how
	// to assert on counts.
	@Disabled
	public void testDisableDBDuringInsert() throws Exception {
		System.out.println("In testDisableDBDuringInsert method");

		Thread t1 = new Thread(new DisabledDBRunnable());
		AtomicBoolean failCheck = new AtomicBoolean(false);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);

		t1.setName("Status Check");
		Map<String, String> properties = new HashMap<>();
		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		ihb2.withBatchSize(5);

		ihb2.onBatchSuccess(batch -> {
			successCount.getAndAdd(batch.getItems().length);
		}).onBatchFailure((batch, throwable) -> {
			failCheck.set(true);
			failureCount.getAndAdd(batch.getItems().length);
			throwable.printStackTrace();
		});
		dmManager.startJob(ihb2);
		t1.start();

		for (int j = 0; j < 1000; j++) {
			String uri = "/local/json-" + j;
			ihb2.add(uri, fileHandle);
		}

		ihb2.flushAndWait();
		t1.join();
		properties.put("enabled", "true");
		changeProperty(properties, "/manage/v2/databases/" + dbName + "/properties");
		assertTrue(failCheck.get());
		assertTrue(successCount.intValue() >= 100);
		assertTrue(successCount.intValue() < 1000);
		assertTrue(failureCount.intValue() <= 900);
	}

	class DisabledDBRunnable implements Runnable {
		final String query1 = "fn:count(fn:doc())";
		Map<String, String> properties = new HashMap<>();

		@Override
		public void run() {

			properties.put("enabled", "false");
			boolean state = true;
			while (state) {
				int count = dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue();
				System.out.println("Count is " + count);
				if (count >= 100) {
					changeProperty(properties, "/manage/v2/databases/" + dbName + "/properties");

					state = false;
				}

			}
		}

	}

	@Test
	public void testNoHost() throws Exception {
		Assumptions.assumeTrue(!isLBHost());
		Assumptions.assumeTrue(hostNames.length > 1);
		System.out.println("In testNoHost method");

		final String query1 = "fn:count(fn:doc())";

		try {
			DocumentMetadataHandle meta6 = new DocumentMetadataHandle().withCollections("NoHost").withQuality(0);
			assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);

			ihbMT = dmManager.newWriteBatcher();

			ihbMT.withBatchSize(50);

			ihbMT.onBatchSuccess(batch -> {
			}).onBatchFailure((batch, throwable) -> {
				throwable.printStackTrace();

			});

			for (int j = 0; j < 1000; j++) {
				String uri = "/local/string-" + j;
				ihbMT.addAs(uri, meta6, jsonNode);
			}

			ihbMT.flushAndWait();

			assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 1000);

			Set<String> uris = Collections.synchronizedSet(new HashSet<String>());
			QueryBatcher getUris = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("NoHost"));
			FilteredForestConfiguration forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig())
					.withRenamedHost(hostNames[0], hostNames[1]).withWhiteList(hostNames[0]);

			try {
				getUris.withForestConfig(forestConfig);
				assertTrue(false);
			} catch (Exception e) {
				assertEquals("White list or black list rules are too restrictive: no valid hosts are left",
						e.getMessage());
			}

			forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig()).withWhiteList("asdf");
			try {
				getUris.withForestConfig(forestConfig);
				assertTrue(false);
			} catch (Exception e) {
				assertEquals("White list or black list rules are too restrictive: no valid hosts are left",
						e.getMessage());
			}
			forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig()).withWhiteList(hostNames[1]);
			getUris.withBatchSize(500).withThreadCount(2).onUrisReady((batch -> {
				uris.addAll(Arrays.asList(batch.getItems()));
			})).withForestConfig(forestConfig).onQueryFailure(exception -> exception.printStackTrace());

			dmManager.startJob(getUris);

			getUris.awaitCompletion();

			assertTrue(uris.size() == 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testNullConfig() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);
		System.out.println("In testNullConfig method");

		final String query1 = "fn:count(fn:doc())";

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);

		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		QueryBatcher qb2 = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("NoHost"));

		FilteredForestConfiguration forestConfig = null;

		try {
			ihb2.withBatchSize(50).withForestConfig(forestConfig);
			assertTrue(false);
		} catch (IllegalArgumentException e) {
			assertEquals("forestConfig must not be null", e.getMessage());
		}

		try {
			qb2.withBatchSize(50).withForestConfig(forestConfig);
			assertTrue(false);
		} catch (IllegalArgumentException e) {
			assertEquals("forestConfig must not be null", e.getMessage());
		}
	}

	@Test
	public void testQBWhiteList() throws Exception {
		Assumptions.assumeTrue(!isLBHost());
		Assumptions.assumeTrue(hostNames.length > 1);
		System.out.println("In testQBWhiteList method");

		final String query1 = "fn:count(fn:doc())";

		FilteredForestConfiguration forestConfig = null;
		DocumentMetadataHandle meta6 = new DocumentMetadataHandle().withCollections("NoHost").withQuality(0);

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);

		ihbMT = dmManager.newWriteBatcher();
		ihbMT.withBatchSize(50);

		ihbMT.onBatchSuccess(batch -> {
		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();

		});
		for (int j = 0; j < 1000; j++) {
			String uri = "/local/string-" + j;
			ihbMT.addAs(uri, meta6, jsonNode);
		}

		ihbMT.flushAndWait();

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 1000);

		Set<String> uris = Collections.synchronizedSet(new HashSet<String>());
		QueryBatcher qb = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("NoHost")).withBatchSize(25)
				.withThreadCount(10);

		qb.onUrisReady(batch -> {
			uris.addAll(Arrays.asList(batch.getItems()));
		});
		qb.onQueryFailure(throwable -> throwable.printStackTrace());

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			String[] hostNames = null;
			new FilteredForestConfiguration(dmManager.readForestConfig()).withWhiteList(hostNames);
		});
		assertEquals("hostNames must not be null", ex.getMessage());

		try {
			forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig()).withWhiteList("asdf");
			qb.withBatchSize(50).withForestConfig(forestConfig);
			assertTrue(false);

		} catch (IllegalStateException e) {
			assertEquals("White list or black list rules are too restrictive: no valid hosts are left",
					e.getMessage());
		}

		forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig())
				.withRenamedHost("localhost", "127.0.0.1").withWhiteList(hostNames[hostNames.length - 1], null);
		qb.withForestConfig(forestConfig);
		dmManager.startJob(qb);
		qb.awaitCompletion();
		assertEquals(1000, uris.size());
	}

	@Test
	public void testWBWhiteList() throws Exception {
		Assumptions.assumeTrue(!isLBHost());
		Assumptions.assumeTrue(hostNames.length > 1);
		System.out.println("In testWBWhiteList method");

		final String query1 = "fn:count(fn:doc())";

		FilteredForestConfiguration forestConfig = null;
		DocumentMetadataHandle meta6 = new DocumentMetadataHandle().withCollections("NoHost").withQuality(0);

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);

		WriteBatcher ihb2 = dmManager.newWriteBatcher();

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			String[] hostNames = null;
			new FilteredForestConfiguration(dmManager.readForestConfig()).withWhiteList(hostNames);
		});
		assertEquals("hostNames must not be null", ex.getMessage());

		try {
			forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig()).withWhiteList("asdf");
			ihb2.withBatchSize(50).withForestConfig(forestConfig);
			assertTrue(false);

		} catch (IllegalStateException e) {
			assertEquals("White list or black list rules are too restrictive: no valid hosts are left",
					e.getMessage());
		}

		forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig())
				.withRenamedHost("localhost", "127.0.0.1").withWhiteList(hostNames[hostNames.length - 1], null);

		ihb2.withBatchSize(50).withForestConfig(forestConfig);

		ihb2.onBatchSuccess(batch -> {
		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();

		});
		for (int j = 0; j < 1000; j++) {
			String uri = "/local/string-" + j;
			ihb2.addAs(uri, meta6, jsonNode);
		}

		ihb2.flushAndWait();

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 1000);
	}

	@Test
	public void testQBBlackList() {
		Assumptions.assumeTrue(!isLBHost());
		Assumptions.assumeTrue(hostNames.length > 1);
		System.out.println("In testQBBlackList method");

		final String query1 = "fn:count(fn:doc())";

		FilteredForestConfiguration forestConfig = null;
		DocumentMetadataHandle meta6 = new DocumentMetadataHandle().withCollections("NoHost").withQuality(0);

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);

		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		ihb2.withBatchSize(50);

		ihb2.onBatchSuccess(batch -> {
		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();

		});
		for (int j = 0; j < 1000; j++) {
			String uri = "/local/string-" + j;
			ihb2.addAs(uri, meta6, jsonNode);
		}

		ihb2.flushAndWait();

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 1000);

		Set<String> uris = Collections.synchronizedSet(new HashSet<String>());
		QueryBatcher qb = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("NoHost")).withBatchSize(25)
				.withThreadCount(10);

		qb.onUrisReady(batch -> {
			uris.addAll(Arrays.asList(batch.getItems()));
		});
		qb.onQueryFailure(throwable -> throwable.printStackTrace());

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			String[] hostNames = null;
			new FilteredForestConfiguration(dmManager.readForestConfig()).withBlackList(hostNames);
		});
		assertEquals("hostNames must not be null", ex.getMessage());

		try {
			forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig()).withBlackList(hostNames)
					.withBlackList("localhost");
			qb.withBatchSize(50).withForestConfig(forestConfig);
			assertTrue(false);

		} catch (IllegalStateException e) {
			assertEquals("White list or black list rules are too restrictive: no valid hosts are left",
					e.getMessage());
		}

		forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig())
				.withRenamedHost("localhost", "127.0.0.1").withBlackList(hostNames[hostNames.length - 1], null, "asdf");
		qb.withForestConfig(forestConfig);
		dmManager.startJob(qb);
		qb.awaitCompletion();
		assertEquals(1000, uris.size());
	}

	@Test
	public void testWBBlackList() {
		Assumptions.assumeTrue(!isLBHost());
		Assumptions.assumeTrue(hostNames.length > 1);
		System.out.println("In testWBBlackList method");

		final String query1 = "fn:count(fn:doc())";

		FilteredForestConfiguration forestConfig = null;
		DocumentMetadataHandle meta6 = new DocumentMetadataHandle().withCollections("NoHost").withQuality(0);

		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);

		WriteBatcher ihb2 = dmManager.newWriteBatcher();

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			String[] hostNames = null;
			new FilteredForestConfiguration(dmManager.readForestConfig()).withWhiteList(hostNames);
		});
		assertEquals("hostNames must not be null", ex.getMessage());

		forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig()).withBlackList("asdf");
		ihb2.withBatchSize(50).withForestConfig(forestConfig);

		try {
			forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig()).withBlackList(hostNames)
					.withBlackList("localhost");
			ihb2.withBatchSize(50).withForestConfig(forestConfig);
			assertTrue(false);

		} catch (IllegalStateException e) {
			e.printStackTrace();
			assertEquals("White list or black list rules are too restrictive: no valid hosts are left",
					e.getMessage());
		}

		forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig())
				.withRenamedHost("localhost", "127.0.0.1").withBlackList(hostNames[hostNames.length - 1], null);
		ihb2.withBatchSize(50).withForestConfig(forestConfig);

		ihb2.onBatchSuccess(batch -> {
		}).onBatchFailure((batch, throwable) -> {
			throwable.printStackTrace();

		});
		for (int j = 0; j < 1000; j++) {
			String uri = "/local/string-" + j;
			ihb2.addAs(uri, meta6, jsonNode);
		}

		ihb2.flushAndWait();
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 1000);

	}

	@Test
	public void testBlackWhiteList() throws Exception {
		Assumptions.assumeTrue(!isLBHost());
		System.out.println("In testBlackWhiteList method");
		FilteredForestConfiguration forestConfig = null;

		WriteBatcher ihb2 = dmManager.newWriteBatcher();

		try {
			forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig()).withWhiteList("localhost")
					.withBlackList("localhost");
			ihb2.withBatchSize(50).withForestConfig(forestConfig);
			assertTrue(false);

		} catch (IllegalStateException e) {
			e.printStackTrace();
			assertEquals("whiteList already initialized", e.getMessage());
		}

		try {
			forestConfig = new FilteredForestConfiguration(dmManager.readForestConfig()).withBlackList("localhost")
					.withWhiteList("localhost");
			ihb2.withBatchSize(50).withForestConfig(forestConfig);
			assertTrue(false);

		} catch (IllegalStateException e) {
			e.printStackTrace();
			assertEquals("blackList already initialized", e.getMessage());
		}
	}

	@Test
	public void testNoServer() throws Exception {
		Assumptions.assumeTrue(hostNames.length > 1);
		Assumptions.assumeTrue(!isLBHost());
		System.out.println("In testNoServer method");

		final String query1 = "fn:count(fn:doc())";
		Map<String, String> properties = new HashMap<>();

		try {
			DocumentMetadataHandle meta6 = new DocumentMetadataHandle().withCollections("NoServer").withQuality(0);

			assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);

			properties.put("group-name", "test");
			postRequest(properties, "/manage/v2/groups");

			properties.clear();

			properties.put("host-name", hostNames[hostNames.length - 1]);
			properties.put("group", "test");

			changeProperty(properties, "/manage/v2/hosts/" + hostNames[hostNames.length - 1] + "/properties");

			Thread.currentThread().sleep(5000L);

			DatabaseClient dbClient = getDatabaseClient(user, password, getConnType());
			DataMovementManager dmManager = dbClient.newDataMovementManager();

			WriteBatcher ihb2 = dmManager.newWriteBatcher();
			ihb2.withBatchSize(50);

			ihb2.onBatchSuccess(batch -> {
			}).onBatchFailure((batch, throwable) -> {
				throwable.printStackTrace();

			});
			for (int j = 0; j < 1000; j++) {
				String uri = "/local/string-" + j;
				ihb2.addAs(uri, meta6, jsonNode);
			}

			ihb2.flushAndWait();
			System.out.println(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
			assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 1000);

			Set<String> uris = Collections.synchronizedSet(new HashSet<String>());
			QueryBatcher getUris = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("NoServer"))
					.withBatchSize(500).withThreadCount(2).onUrisReady((batch -> {
						uris.addAll(Arrays.asList(batch.getItems()));
					})).onQueryFailure(exception -> exception.printStackTrace());

			dmManager.startJob(getUris);

			getUris.awaitCompletion();

			assertTrue(uris.size() == 1000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			properties.clear();

			properties.put("host-name", hostNames[hostNames.length - 1]);
			properties.put("group", "Default");
			changeProperty(properties, "/manage/v2/hosts/" + hostNames[hostNames.length - 1] + "/properties");

			Thread.currentThread().sleep(5000L);
		}

	}

	public static void postRequest(Map<String, String> properties, String endpoint) {
		try {
			StringBuffer xmlBuff = new StringBuffer();
			// xmlBuff.append("<forest-properties
			// xmlns=\"http://marklogic.com/manage\">");
			xmlBuff.append("{");
			Iterator<Map.Entry<String, String>> it = properties.entrySet().iterator();
			int size = properties.size();
			int j = 0;
			while (it.hasNext()) {
				Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
				xmlBuff.append("\"").append(pair.getKey()).append("\":");
				if (j == (size - 1))
					xmlBuff.append("\"").append(pair.getValue()).append("\"");
				else
					xmlBuff.append("\"").append(pair.getValue()).append("\",");

				j++;

			}
			xmlBuff.append('}');
			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(host, 8002),
					new UsernamePasswordCredentials("admin", "admin"));

			HttpPost post = new HttpPost("http://" + host + ":8002" + endpoint);
			post.addHeader("Content-type", "application/json");
			post.setEntity(new StringEntity(xmlBuff.toString()));

			HttpResponse response = client.execute(post);
			HttpEntity respEntity = response.getEntity();

			if (respEntity != null) {
				// EntityUtils to get the response content
				String content = EntityUtils.toString(respEntity);
				System.out.println(content);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		}
	}

	@Test
	public void addWithMetadata() throws Exception {
		System.out.println("In addWithMetadata method");

		final String query1 = "fn:count(fn:doc())";
		DatabaseClient dbClientTmp = getDatabaseClient(user, password, getConnType());
		try {

			DocumentMetadataHandle meta6 = new DocumentMetadataHandle().withCollections("Sample Collection 1")
					.withProperty("docMeta-1", "true").withQuality(1);
			meta6.setFormat(Format.XML);
			assertTrue(dbClientTmp.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);

			Thread.currentThread().sleep(5000L);

			DataMovementManager dmManager = dbClientTmp.newDataMovementManager();

			WriteBatcher ihb2 = dmManager.newWriteBatcher();
			ihb2.withBatchSize(50).withThreadCount(1);

			ihb2.onBatchSuccess(batch -> {
			}).onBatchFailure((batch, throwable) -> {
				throwable.printStackTrace();

			});
			for (int j = 0; j < 1000; j++) {
				String uri = "/local/string-" + j;
				ihb2.addAs(uri, meta6, jsonNode);
			}

			ihb2.flushAndWait();

			assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 1000);

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			dbClientTmp.release();
		}
	}

	@Test
	public void testDocumentWriteOperationAdd() throws Exception {
		System.out.println("In testDocumentWriteOperationAdd method");

		final StringBuffer successBatch = new StringBuffer();
		final StringBuffer failureBatch = new StringBuffer();
		final String query1 = "fn:count(fn:doc())";

		 String docId[] = { "/foo/test/myFoo1.txt", "/foo/test/myFoo2.txt", "/foo/test/myFoo3.txt",
			        "/foo/test/myFoo4.txt", "/foo/test/myFoo5.txt", "/foo/test/myFoo6.txt",
			        "/foo/test/myFoo7.txt", "/foo/test/myFoo8.txt", "/foo/test/myFoo9.txt" };

		// Test 1 All success with addAs (batchSize =8)
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		replenishStream();
		WriteBatcher ihb3 = dmManager.newWriteBatcher();
		ihb3.withBatchSize(8);
		ihb3.onBatchSuccess(batch -> {
			for (WriteEvent w : batch.getItems()) {
				successBatch.append(w.getTargetUri() + ":");

			}

		}).onBatchFailure((batch, throwable) -> {
			for (WriteEvent w : batch.getItems()) {

				failureBatch.append(w.getTargetUri() + ":");
			}
		});
		dmManager.startJob(ihb3);
		// Use DocumentWriteOperation Impl class to test Git #647

		// get the original metadata
	    Document docMetadata = getXMLMetadata("metadata-original.xml");
	    // create handle to write metadata
	    DOMHandle writeMetadataHandle = new DOMHandle();
	    writeMetadataHandle.set(docMetadata);
	    // get the content
	    StringHandle contHandle1 = new StringHandle().with("This is so foo1");
	    StringHandle contHandle2 = new StringHandle().with("This is so foo2");
	    StringHandle contHandle3 = new StringHandle().with("This is so foo3");

	    StringHandle contHandle4 = new StringHandle().with("This is so foo4");
	    StringHandle contHandle5 = new StringHandle().with("This is so foo5");
	    // Generate a series of DocumentWriteOperation instances.
	    DocumentWriteOperationImpl docWriteOpsImp1 = new DocumentWriteOperationImpl(
	    		OperationType.DOCUMENT_WRITE,
	    		docId[0],
	    		writeMetadataHandle,
	    		contHandle1
	    		);
	    DocumentWriteOperationImpl docWriteOpsImp2 = new DocumentWriteOperationImpl(
	    		OperationType.DOCUMENT_WRITE,
	    		docId[1],
	    		writeMetadataHandle,
	    		contHandle2
	    		);
	    DocumentWriteOperationImpl docWriteOpsImp3 = new DocumentWriteOperationImpl(
	    		OperationType.DOCUMENT_WRITE,
	    		docId[2],
	    		writeMetadataHandle,
	    		contHandle3
	    		);
	    // Use only DocumentWriteOperation

		ihb3.add(docWriteOpsImp1).add(docWriteOpsImp2).add(docWriteOpsImp3);

		ihb3.flushAndWait();
		System.out.println("Size is " + dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 3);

		DocumentMetadataHandle mHandle1 = readMetadataFromDocument(dbClient, docId[0], "XML");
		assertEquals(0, mHandle1.getQuality());
		assertTrue(mHandle1.getCollections().iterator().next().matches("coll[1|2]"));
		assertTrue(mHandle1.getCollections().size() == 2);

		String actualProperties = getDocumentPropertiesString(mHandle1.getProperties());
		System.out.println("Returned properties: " + actualProperties);
		assertTrue( actualProperties.contains("size:6"));
	    assertTrue( actualProperties.contains("Company:Mark Logic Corporation"));
	    assertTrue( actualProperties.contains("filter-capabilities:text subfiles HD-HTML"));
	    assertTrue( actualProperties.contains("popularity:5"));
	    assertTrue( actualProperties.contains("Author:MarkLogic"));
	    assertTrue( actualProperties.contains("content-type:application/msword"));
	    assertTrue( actualProperties.contains("AppName:Microsoft Office Word"));

	    // Add to write batcher again mixture of ContentHandle and DocumentWriteOperation with default metadata instance
	    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
	    metadataHandle.getCollections().addAll("my-collection1", "my-collection2");
	    metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
	    metadataHandle.getProperties().put("reviewed", true);
	    metadataHandle.getProperties().put("myString", "foo");
	    metadataHandle.getProperties().put("myInteger", 10);
	    metadataHandle.getProperties().put("myDecimal", 34.56678);

	    metadataHandle.setQuality(23);
	    DocumentWriteOperationImpl docWriteOpsImp4 = new DocumentWriteOperationImpl(
	    		OperationType.DOCUMENT_WRITE,
	    		docId[4],
	    		metadataHandle,
	    		contHandle5
	    		);

	    ihb3.add(docId[3], contHandle4).add(docWriteOpsImp4);

	    ihb3.flushAndWait();
		System.out.println("Size is " + dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 5);

		// Read back document from second  DocumentWriteOperation to verify meta-data.
		DocumentMetadataHandle mHandle2 = readMetadataFromDocument(dbClient, docId[4], "XML");

		DocumentProperties properties = mHandle2.getProperties();
	    DocumentCollections collections = mHandle2.getCollections();

	    // Properties
	    String actualProperties2 = getDocumentPropertiesString(properties);
	    System.out.println("Returned properties: " + actualProperties);

	    assertTrue( actualProperties2.contains("size:4"));
	    assertTrue( actualProperties2.contains("reviewed:true"));
	    assertTrue( actualProperties2.contains("myInteger:10"));
	    assertTrue( actualProperties2.contains("myDecimal:34.56678"));
	    assertTrue( actualProperties2.contains("myString:foo"));

	    // Collections
	    String actualCollections = getDocumentCollectionsString(collections);
	    System.out.println("Returned collections: " + actualCollections);

	    assertTrue( actualCollections.contains("size:2"));
	    assertTrue( actualCollections.contains("my-collection1"));
	    assertTrue( actualCollections.contains("my-collection2"));

	    // Read the first document from the second batch. No Properties or collection available

	 	DocumentMetadataHandle mHandle3 = readMetadataFromDocument(dbClient, docId[3], "XML");
	 	assertTrue( mHandle3.getProperties().size()==0);
	 	assertTrue( mHandle3.getCollections().size()==0);

		clearDB(port);
	}
}
