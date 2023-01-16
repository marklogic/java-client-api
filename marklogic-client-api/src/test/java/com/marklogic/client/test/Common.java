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
package com.marklogic.client.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
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
  final public static String USER= "rest-writer";
  final public static String PASS= "x";
  final public static String REST_ADMIN_USER= "rest-admin";
  final public static String REST_ADMIN_PASS= "x";
  final public static String SERVER_ADMIN_USER= "admin";
  final public static String SERVER_ADMIN_PASS = System.getProperty("TEST_ADMIN_PASSWORD", "admin");
  final public static String EVAL_USER= "rest-evaluator";
  final public static String EVAL_PASS= "x";
  final public static String READ_ONLY_USER= "rest-reader";
  final public static String READ_ONLY_PASS= "x";
  final public static String READ_PRIVILIGED_USER = "read-privileged";
  final public static String READ_PRIVILIGED_PASS = "x";
  final public static String WRITE_PRIVILIGED_USER = "write-privileged";
  final public static String WRITE_PRIVILIGED_PASS = "x";

  final public static String  HOST          = System.getProperty("TEST_HOST", "localhost");

  final public static boolean USE_REVERSE_PROXY_SERVER = Boolean.parseBoolean(System.getProperty("TEST_USE_REVERSE_PROXY_SERVER", "false"));
  final public static int     PORT          = USE_REVERSE_PROXY_SERVER ? 8020 : Integer.parseInt(System.getProperty("TEST_PORT", "8012"));
  final public static String SECURITY_CONTEXT_TYPE = USE_REVERSE_PROXY_SERVER ? "basic" : System.getProperty("TEST_SECURITY_CONTEXT_TYPE", "digest");
  final public static String BASE_PATH = USE_REVERSE_PROXY_SERVER ? "test/marklogic/unit" : System.getProperty("TEST_BASE_PATH", null);
  final public static boolean WITH_WAIT     = Boolean.parseBoolean(System.getProperty("TEST_WAIT", "false"));
  final public static int     PROPERTY_WAIT = Integer.parseInt(System.getProperty("TEST_PROPERTY_WAIT", WITH_WAIT ? "8200" : "0"));

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
		readOnlyClient = makeNewClient(Common.HOST, Common.PORT, newSecurityContext(Common.READ_ONLY_USER, Common.READ_ONLY_PASS));
	}
    return readOnlyClient;
  }
  public static DatabaseClient newClient() {
    return newClient(null);
  }
  public static DatabaseClient newClient(String databaseName) {
	  return makeNewClient(Common.HOST, Common.PORT, databaseName, newSecurityContext(Common.USER, Common.PASS), null);
  }
  public static DatabaseClient newClientAsUser(String username) {
	  return makeNewClient(Common.HOST, Common.PORT, null, newSecurityContext(username, Common.PASS), null);
  }

  public static DatabaseClientFactory.SecurityContext newSecurityContext(String username, String password) {
    if ("basic".equalsIgnoreCase(SECURITY_CONTEXT_TYPE)) {
      return new DatabaseClientFactory.BasicAuthContext(username, password);
    }
    return new DatabaseClientFactory.DigestAuthContext(username, password);
  }

  public static DatabaseClient makeNewClient(String host, int port, DatabaseClientFactory.SecurityContext securityContext) {
    return makeNewClient(host, port, null, securityContext, null);
  }

  /**
   * Intent is to route every call to this method so that changes to how newClient works can easily be made in the
   * future.
   */
  public static DatabaseClient makeNewClient(String host, int port, String database,
                                             DatabaseClientFactory.SecurityContext securityContext,
                                             DatabaseClient.ConnectionType connectionType) {
    System.out.println("Connecting to: " + Common.HOST + ":" + port + "; basePath: " + BASE_PATH  + "; auth: " + securityContext.getClass().getSimpleName());
    return DatabaseClientFactory.newClient(host, port, BASE_PATH, database, securityContext, connectionType);
  }

  public static DatabaseClient newRestAdminClient() {
	  return makeNewClient(Common.HOST, Common.PORT, null,
		  newSecurityContext(Common.REST_ADMIN_USER, Common.REST_ADMIN_PASS), CONNECTION_TYPE);
  }
  public static DatabaseClient newServerAdminClient() {
    return newServerAdminClient(null);
  }
  public static DatabaseClient newServerAdminClient(String databaseName) {
    return makeNewClient(Common.HOST, Common.PORT, databaseName,
       newSecurityContext(Common.SERVER_ADMIN_USER, Common.SERVER_ADMIN_PASS), CONNECTION_TYPE);
  }
  public static DatabaseClient newEvalClient() {
    return newEvalClient(null);
  }
  public static DatabaseClient newEvalClient(String databaseName) {
    return makeNewClient(Common.HOST, Common.PORT, databaseName,
        newSecurityContext(Common.EVAL_USER, Common.EVAL_PASS), CONNECTION_TYPE);
  }

  public static MarkLogicVersion getMarkLogicVersion() {
    String version = newServerAdminClient().newServerEval().javascript("xdmp.version()").evalAs(String.class);
    return new MarkLogicVersion(version);
  }

  public static byte[] streamToBytes(InputStream is) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] b = new byte[1000];
    int len = 0;
    while (((len=is.read(b)) != -1)) {
      baos.write(b, 0, len);
    }
    return baos.toByteArray();
  }
  public static String readerToString(Reader r) throws IOException {
    StringWriter w = new StringWriter();
    char[] cbuf = new char[1000];
    int len = 0;
    while (((len=r.read(cbuf)) != -1)) {
      w.write(cbuf, 0, len);
    }
    r.close();
    String result = w.toString();
    w.close();
    return result;
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

}
