/*
 * Copyright 2012-2018 MarkLogic Corporation
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.DigestAuthContext;

public class Common {
  final public static String USER= "rest-writer";
  final public static String PASS= "x";
  final public static String REST_ADMIN_USER= "rest-admin";
  final public static String REST_ADMIN_PASS= "x";
  final public static String SERVER_ADMIN_USER= "admin";
  final public static String SERVER_ADMIN_PASS= "admin";
  final public static String EVAL_USER= "rest-evaluator";
  final public static String EVAL_PASS= "x";
  final public static String READ_ONLY_USER= "rest-reader";
  final public static String READ_ONLY_PASS= "x";
  final public static String READ_PRIVILIGED_USER = "read-privileged";
  final public static String READ_PRIVILIGED_PASS = "x";
  final public static String WRITE_PRIVILIGED_USER = "write-privileged";
  final public static String WRITE_PRIVILIGED_PASS = "x";

  final public static String  HOST          = System.getProperty("TEST_HOST", "localhost");

  final public static int     PORT          = Integer.parseInt(System.getProperty("TEST_PORT", "8012"));
  final public static boolean WITH_WAIT     = Boolean.parseBoolean(System.getProperty("TEST_WAIT", "false"));
  final public static int     MODULES_WAIT  = Integer.parseInt(System.getProperty("TEST_MODULES_WAIT",  WITH_WAIT ? "1200" : "0"));
  final public static int     PROPERTY_WAIT = Integer.parseInt(System.getProperty("TEST_PROPERTY_WAIT", WITH_WAIT ? "8200" : "0"));

  final public static DatabaseClient.ConnectionType CONNECTION_TYPE =
      DatabaseClient.ConnectionType.valueOf(System.getProperty("TEST_CONNECT_TYPE", "DIRECT"));

  final public static boolean BALANCED = Boolean.parseBoolean(System.getProperty("TEST_BALANCED", "false"));

  public static DatabaseClient client;
  public static DatabaseClient adminClient;
  public static DatabaseClient serverAdminClient;
  public static DatabaseClient evalClient;
  public static DatabaseClient readOnlyClient;

  public static DatabaseClient connect() {
    if (client != null) return client;
    client = newClient();
    return client;
  }
  public static DatabaseClient connectAdmin() {
    if (adminClient != null) return adminClient;
    adminClient = newAdminClient();
    return adminClient;
  }
  public static DatabaseClient connectServerAdmin() {
    if (serverAdminClient != null) return serverAdminClient;
    serverAdminClient = newServerAdminClient();
    return serverAdminClient;
  }
  public static DatabaseClient connectEval() {
    if (evalClient != null) return evalClient;
    evalClient = newEvalClient();
    return evalClient;
  }
  public static DatabaseClient connectReadOnly() {
    if (readOnlyClient != null) return readOnlyClient;
    readOnlyClient = newReadOnlyClient();
    return readOnlyClient;
  }
  public static DatabaseClient newClient() {
    return newClient(null);
  }
  public static DatabaseClient newClient(String databaseName) {
    return DatabaseClientFactory.newClient(Common.HOST, Common.PORT, databaseName,
      new DatabaseClientFactory.DigestAuthContext(Common.USER, Common.PASS),
          CONNECTION_TYPE);
  }
  public static DatabaseClient newAdminClient() {
    return DatabaseClientFactory.newClient(
      Common.HOST, Common.PORT, new DigestAuthContext(Common.REST_ADMIN_USER, Common.REST_ADMIN_PASS),
          CONNECTION_TYPE);
  }
  public static DatabaseClient newServerAdminClient() {
    return DatabaseClientFactory.newClient(
      Common.HOST, Common.PORT, new DigestAuthContext(Common.SERVER_ADMIN_USER, Common.SERVER_ADMIN_PASS),
          CONNECTION_TYPE);
  }
  public static DatabaseClient newEvalClient() {
    return newEvalClient(null);
  }
  public static DatabaseClient newEvalClient(String databaseName) {
    return DatabaseClientFactory.newClient(
      Common.HOST, Common.PORT, databaseName, new DigestAuthContext(Common.EVAL_USER, Common.EVAL_PASS),
          CONNECTION_TYPE);
  }
  public static DatabaseClient newReadOnlyClient() {
    return DatabaseClientFactory.newClient(
      Common.HOST, Common.PORT, new DigestAuthContext(Common.READ_ONLY_USER, Common.READ_ONLY_PASS),
          CONNECTION_TYPE);
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
      return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
        new InputSource(new StringReader(document)));
    } catch (SAXException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }
  public static void modulesWait() {
    waitFor(MODULES_WAIT);
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
}
