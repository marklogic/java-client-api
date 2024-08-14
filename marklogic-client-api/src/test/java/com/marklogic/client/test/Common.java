/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientBuilder;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import org.springframework.util.FileCopyUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.X509Certificate;

public class Common {

	final public static String USER = "rest-writer";
	final public static String PASS = "x";
	final public static String REST_ADMIN_USER = "rest-admin";
	final public static String SERVER_ADMIN_USER = "admin";
	final public static String SERVER_ADMIN_PASS = System.getProperty("TEST_ADMIN_PASSWORD", "admin");
	final public static String EVAL_USER = "rest-evaluator";
	final public static String READ_ONLY_USER = "rest-reader";

	final public static String HOST = System.getProperty("TEST_HOST", "localhost");

	final public static boolean USE_REVERSE_PROXY_SERVER = Boolean.parseBoolean(System.getProperty("TEST_USE_REVERSE_PROXY_SERVER", "false"));
	final public static int PORT = USE_REVERSE_PROXY_SERVER ? 8020 : Integer.parseInt(System.getProperty("TEST_PORT", "8012"));
	final public static String AUTH_TYPE = USE_REVERSE_PROXY_SERVER ? "basic" : System.getProperty("TEST_AUTH_TYPE", "digest");
	final public static String BASE_PATH = USE_REVERSE_PROXY_SERVER ? "test/marklogic/unit" : System.getProperty("TEST_BASE_PATH", null);
	final public static boolean WITH_WAIT = Boolean.parseBoolean(System.getProperty("TEST_WAIT", "false"));
	final public static int PROPERTY_WAIT = Integer.parseInt(System.getProperty("TEST_PROPERTY_WAIT", WITH_WAIT ? "8200" : "0"));

	final public static String SERVER_NAME = "java-unittest";

	final public static DatabaseClient.ConnectionType CONNECTION_TYPE =
		DatabaseClient.ConnectionType.valueOf(System.getProperty("TEST_CONNECT_TYPE", "DIRECT"));

	public final static X509TrustManager TRUST_ALL_MANAGER = new X509TrustManager() {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	};

	public static DatabaseClient client;
	public static DatabaseClient restAdminClient;
	public static DatabaseClient serverAdminClient;
	public static DatabaseClient evalClient;
	public static DatabaseClient readOnlyClient;

	public static DatabaseClient connect() {
		if (client == null)
			client = newClient();
		return client;
	}

	public static DatabaseClient connectRestAdmin() {
		if (restAdminClient == null)
			restAdminClient = newRestAdminClient();
		return restAdminClient;
	}

	public static DatabaseClient connectServerAdmin() {
		if (serverAdminClient == null)
			serverAdminClient = newServerAdminClient();
		return serverAdminClient;
	}

	public static DatabaseClient connectEval() {
		if (evalClient == null)
			evalClient = newEvalClient();
		return evalClient;
	}

	public static DatabaseClient connectReadOnly() {
		if (readOnlyClient == null) {
			readOnlyClient = newClientBuilder().withUsername(READ_ONLY_USER).build();
		}
		return readOnlyClient;
	}

	public static DatabaseClient newClient() {
		return newClientBuilder().build();
	}

	public static DatabaseClientFactory.SecurityContext newSecurityContext(String username, String password) {
		if ("basic".equalsIgnoreCase(AUTH_TYPE)) {
			return new DatabaseClientFactory.BasicAuthContext(username, password);
		}
		return new DatabaseClientFactory.DigestAuthContext(username, password);
	}

	public static DatabaseClientBuilder newClientBuilder() {
		return new DatabaseClientBuilder()
			.withHost(HOST)
			.withPort(PORT)
			.withBasePath(BASE_PATH)
			.withUsername(USER)
			.withPassword(PASS) // Most of the test users all have the same password, so we can use a default one here
			.withAuthType(AUTH_TYPE)
			.withConnectionType(CONNECTION_TYPE);
	}

	public static DatabaseClient makeNewClient(String host, int port, DatabaseClientFactory.SecurityContext securityContext) {
		return newClientBuilder()
			.withHost(host)
			.withPort(port)
			.withSecurityContext(securityContext)
			.build();
	}

	public static DatabaseClient newRestAdminClient() {
		return newClientBuilder().withUsername(REST_ADMIN_USER).build();
	}

	public static DatabaseClient newServerAdminClient() {
		return newClientBuilder()
			.withUsername(Common.SERVER_ADMIN_USER)
			.withPassword(Common.SERVER_ADMIN_PASS)
			.build();
	}

	public static DatabaseClient newEvalClient() {
		return newEvalClient(null);
	}

	public static DatabaseClient newEvalClient(String databaseName) {
		return newClientBuilder()
			.withDatabase(databaseName)
			.withUsername(Common.EVAL_USER)
			.build();
	}

	public static MarkLogicVersion getMarkLogicVersion() {
		String version = newServerAdminClient().newServerEval().javascript("xdmp.version()").evalAs(String.class);
		return new MarkLogicVersion(version);
	}

	public static byte[] streamToBytes(InputStream is) throws IOException {
		return FileCopyUtils.copyToByteArray(is);
	}

	public static String readerToString(Reader r) throws IOException {
		return FileCopyUtils.copyToString(r);
	}

	// the testFile*() methods get a file in the src/test/resources directory
	public static String testFileToString(String filename) throws IOException {
		return testFileToString(filename, null);
	}

	public static String testFileToString(String filename, String encoding) throws IOException {
		return readerToString(testFileToReader(filename, encoding));
	}

	public static Reader testFileToReader(String filename) {
		return testFileToReader(filename, null);
	}

	public static URI getResourceUri(String filename) throws URISyntaxException {
		return Common.class.getClassLoader().getResource(filename).toURI();
	}

	public static Reader testFileToReader(String filename, String encoding) {
		try {
			return (encoding != null) ?
				new InputStreamReader(testFileToStream(filename), encoding) :
				new InputStreamReader(testFileToStream(filename));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static InputStream testFileToStream(String filename) {
		return Common.class.getClassLoader().getResourceAsStream(filename);
	}

	public static String testDocumentToString(Document document) {
		try {
			return ((DOMImplementationLS) DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.getDOMImplementation()
			).createLSSerializer().writeToString(document);
		} catch (DOMException e) {
			throw new RuntimeException(e);
		} catch (LSException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public static Document testStringToDocument(String document) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(false);
			return factory.newDocumentBuilder().parse(
				new InputSource(new StringReader(document)));
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public static void propertyWait() {
		waitFor(PROPERTY_WAIT);
	}

	public static void waitFor(int milliseconds) {
		if (milliseconds > 0) {
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
				e.printStackTrace(System.out);
			}
		}
	}

	public static ManageClient newManageClient() {
		return new ManageClient(new ManageConfig(HOST, 8002, SERVER_ADMIN_USER, SERVER_ADMIN_PASS));
	}

	public static ObjectNode newServerPayload() {
		ObjectNode payload = new ObjectMapper().createObjectNode();
		payload.put("server-name", SERVER_NAME);
		payload.put("group-name", "Default");
		return payload;
	}

	public static void deleteUrisWithPattern(String pattern) {
		Common.connectServerAdmin().newServerEval()
			.xquery(String.format("cts:uri-match('%s') ! xdmp:document-delete(.)", pattern))
			.evalAs(String.class);
	}

	/**
	 * Convenience method for constructing metadata with a default set of permissions that the
	 * test user - "rest-writer" - can both read and update.
	 */
	public static DocumentMetadataHandle newDefaultMetadata() {
		return new DocumentMetadataHandle()
			.withPermission("rest-reader", DocumentMetadataHandle.Capability.READ)
			.withPermission("test-rest-writer", DocumentMetadataHandle.Capability.UPDATE);
	}
}
