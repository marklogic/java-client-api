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

package com.marklogic.client.functionaltest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import org.junit.jupiter.api.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("Ignored because it was previously ignored in build.gradle though without explanation")
public class TestSSLConnection extends BasicJavaClientREST {

  private static String dbName = "TestSSLConnectionDB";
  private static String[] fNames = { "TestSSLConnectionDB-1" };
  private static String restServerName = "REST-Java-Client-API-SSL-Server";
  private static String appServerHostname = null;

  @BeforeAll
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    setupJavaRESTServer(dbName, fNames[0], restServerName, 8012);
    setupAppServicesConstraint(dbName);
    addRangeElementAttributeIndex(dbName, "decimal", "http://cloudbank.com", "price", "", "amt", "http://marklogic.com/collation/");
    addFieldExcludeRoot(dbName, "para");
    includeElementFieldWithWeight(dbName, "para", "", "p", 5, "", "", "");
    loadTestProperties();
    appServerHostname = getRestAppServerHostName();
  }

  /*
   *
   *
   * @SuppressWarnings("deprecation")
   *
   * @Test public void testSSLConnection() throws KeyManagementException,
   * NoSuchAlgorithmException, NoSuchAlgorithmException, KeyManagementException,
   * FileNotFoundException, XpathException {
   * System.out.println("Running testSSLConnection");
   *
   * // create a trust manager // (note: a real application should verify
   * certificates) TrustManager naiveTrustMgr = new X509TrustManager() {
   *
   * @Override public void checkClientTrusted(X509Certificate[] chain, String
   * authType) { }
   *
   * @Override public void checkServerTrusted(X509Certificate[] chain, String
   * authType) { }
   *
   * @Override public X509Certificate[] getAcceptedIssuers() { return new
   * X509Certificate[0]; } };
   *
   * // create an SSL context SSLContext sslContext =
   * SSLContext.getInstance("SSLv3"); sslContext.init(null, new TrustManager[] {
   * naiveTrustMgr }, null);
   *
   * String filename1 = "constraint1.xml"; String filename2 = "constraint2.xml";
   * String filename3 = "constraint3.xml"; String filename4 = "constraint4.xml";
   * String filename5 = "constraint5.xml";
   *
   * // create the client // (note: a real application should use a COMMON,
   * STRICT, or implemented hostname verifier) DatabaseClient client =
   * DatabaseClientFactory.newClient("appServerHostname", 8012, "rest-admin",
   * "x", getConnType(), sslContext, SSLHostnameVerifier.ANY);
   *
   * // create and initialize a handle on the metadata DocumentMetadataHandle
   * metadataHandle1 = new DocumentMetadataHandle(); DocumentMetadataHandle
   * metadataHandle2 = new DocumentMetadataHandle(); DocumentMetadataHandle
   * metadataHandle3 = new DocumentMetadataHandle(); DocumentMetadataHandle
   * metadataHandle4 = new DocumentMetadataHandle(); DocumentMetadataHandle
   * metadataHandle5 = new DocumentMetadataHandle();
   *
   * // set the metadata
   * metadataHandle1.getCollections().addAll("http://test.com/set1");
   * metadataHandle1.getCollections().addAll("http://test.com/set5");
   * metadataHandle2.getCollections().addAll("http://test.com/set1");
   * metadataHandle3.getCollections().addAll("http://test.com/set3");
   * metadataHandle4.getCollections().addAll("http://test.com/set3/set3-1");
   * metadataHandle5.getCollections().addAll("http://test.com/set1");
   * metadataHandle5.getCollections().addAll("http://test.com/set5");
   *
   * // write docs writeDocumentUsingInputStreamHandle(client, filename1,
   * "/ssl-connection/", metadataHandle1, "XML");
   * writeDocumentUsingInputStreamHandle(client, filename2, "/ssl-connection/",
   * metadataHandle2, "XML"); writeDocumentUsingInputStreamHandle(client,
   * filename3, "/ssl-connection/", metadataHandle3, "XML");
   * writeDocumentUsingInputStreamHandle(client, filename4, "/ssl-connection/",
   * metadataHandle4, "XML"); writeDocumentUsingInputStreamHandle(client,
   * filename5, "/ssl-connection/", metadataHandle5, "XML");
   *
   * // create query options manager QueryOptionsManager optionsMgr =
   * client.newServerConfigManager().newQueryOptionsManager();
   *
   * // create query options builder QueryOptionsBuilder builder = new
   * QueryOptionsBuilder();
   *
   * // create query options handle QueryOptionsHandle handle = new
   * QueryOptionsHandle();
   *
   * // build query options
   *
   * handle.build( builder.returnMetrics(false), builder.returnQtext(false),
   * builder.debug(true), builder.transformResults("raw"),
   * builder.constraint("id", builder.value(builder.element("id"))),
   * builder.constraint("date", builder.range(false, new QName("xs:date"),
   * builder.element("http://purl.org/dc/elements/1.1/", "date"))),
   * builder.constraint("coll", builder.collection(true, "http://test.com/")),
   * builder.constraint("para", builder.word(builder.field("para"),
   * builder.termOption("case-insensitive"))), builder.constraint("intitle",
   * builder.word(builder.element("title"))), builder.constraint("price",
   * builder.range(false, new QName("xs:decimal"),
   * builder.element("http://cloudbank.com", "price"), builder.attribute("amt"),
   * builder.bucket("high", "High", "120", null), builder.bucket("medium",
   * "Medium", "3", "14"), builder.bucket("low", "Low", "0", "2"))),
   * builder.constraint("pop", builder.range(true, new QName("xs:int"),
   * builder.element("popularity"), builder.bucket("high", "High", "5", null),
   * builder.bucket("medium", "Medium", "3", "5"), builder.bucket("low", "Low",
   * "1", "3"))) );
   *
   *
   * // write query options
   * optionsMgr.writeOptions("AllConstraintsWithStructuredSearch", handle);
   *
   * // read query option StringHandle readHandle = new StringHandle();
   * readHandle.setFormat(Format.XML);
   * optionsMgr.readOptions("AllConstraintsWithStructuredSearch", readHandle);
   * String output = readHandle.get(); System.out.println(output);
   *
   * // create query manager QueryManager queryMgr = client.newQueryManager();
   *
   * // create query def StructuredQueryBuilder qb =
   * queryMgr.newStructuredQueryBuilder("AllConstraintsWithStructuredSearch");
   * StructuredQueryDefinition query1 = qb.and(qb.collectionConstraint("coll",
   * "set1"), qb.collectionConstraint("coll", "set5"));
   * StructuredQueryDefinition query2 = qb.not(qb.wordConstraint("intitle",
   * "memex")); StructuredQueryDefinition query3 = qb.valueConstraint("id",
   * "**11"); StructuredQueryDefinition query4 = qb.rangeConstraint("date",
   * StructuredQueryBuilder.Operator.EQ, "2005-01-01");
   * StructuredQueryDefinition query5 = qb.and(qb.wordConstraint("para",
   * "Bush"), qb.not(qb.wordConstraint("para", "memex")));
   * StructuredQueryDefinition query6 = qb.rangeConstraint("price",
   * StructuredQueryBuilder.Operator.EQ, "low"); StructuredQueryDefinition
   * query7 = qb.or(qb.rangeConstraint("pop",
   * StructuredQueryBuilder.Operator.EQ, "high"), qb.rangeConstraint("pop",
   * StructuredQueryBuilder.Operator.EQ, "medium")); StructuredQueryDefinition
   * queryFinal = qb.and(query1, query2, query3, query4, query5, query6,
   * query7);
   *
   * // create handle DOMHandle resultsHandle = new DOMHandle();
   * queryMgr.search(queryFinal, resultsHandle);
   *
   * // get the result Document resultDoc = resultsHandle.get();
   *
   * assertXpathEvaluatesTo("1",
   * "string(//*[local-name()='result'][last()]//@*[local-name()='index'])",
   * resultDoc); assertXpathEvaluatesTo("Vannevar Bush",
   * "string(//*[local-name()='result'][1]//*[local-name()='title'])",
   * resultDoc);
   *
   * // release client client.release(); }
   */

  @Test
  public void testSSLConnectionInvalidPort() throws KeyManagementException, NoSuchAlgorithmException, IOException, NoSuchAlgorithmException, KeyManagementException
  {
    System.out.println("Running testSSLConnectionInvalidPort");

    String filename = "facebook-10443244874876159931";

    // create a trust manager
    // (note: a real application should verify certificates)
    TrustManager naiveTrustMgr = new X509TrustManager() {
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

    // create an SSL context
    SSLContext sslContext = SSLContext.getInstance("SSLv3");
    sslContext.init(null, new TrustManager[] { naiveTrustMgr }, null);

    // create the client
    // (note: a real application should use a COMMON, STRICT, or implemented
    // hostname verifier)
    SecurityContext secContext = newSecurityContext("rest-admin", "x").withSSLContext(sslContext, new X509TrustManager() {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}}).withSSLHostnameVerifier(SSLHostnameVerifier.ANY);
    DatabaseClient client = DatabaseClientFactory.newClient(appServerHostname, 8033, secContext, getConnType());

    String expectedException = "com.sun.jersey.api.client.ClientHandlerException: org.apache.http.conn.HttpHostConnectException";
    String exception = "";

    // write doc
    try
    {
      writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
    } catch (Exception e) {
      exception = e.toString();
    }

    assertTrue(exception.contains(expectedException));

    // release client
    client.release();
  }

  @Test
  public void testSSLConnectionNonSSLServer() throws KeyManagementException, NoSuchAlgorithmException, IOException, NoSuchAlgorithmException, KeyManagementException
  {
    System.out.println("Running testSSLConnectionNonSSLServer");

    String filename = "facebook-10443244874876159931";

    // create a trust manager
    // (note: a real application should verify certificates)
    TrustManager naiveTrustMgr = new X509TrustManager() {
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

    // create an SSL context
    SSLContext sslContext = SSLContext.getInstance("SSLv3");
    sslContext.init(null, new TrustManager[] { naiveTrustMgr }, null);

    // create the client
    // (note: a real application should use a COMMON, STRICT, or implemented
    // hostname verifier)
    SecurityContext secContext = newSecurityContext("rest-admin", "x").withSSLContext(sslContext, new X509TrustManager() {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}}).withSSLHostnameVerifier(SSLHostnameVerifier.ANY);
    DatabaseClient client = DatabaseClientFactory.newClient(appServerHostname, 8014, secContext, getConnType());

    String expectedException = "com.sun.jersey.api.client.ClientHandlerException: javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated";
    String exception = "";

    // write doc
    try
    {
      writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
    } catch (Exception e) {
      exception = e.toString();
    }

    assertEquals(expectedException, exception);

    // release client
    client.release();
  }

  @Test
  public void testSSLConnectionInvalidPassword() throws KeyManagementException, NoSuchAlgorithmException, IOException, NoSuchAlgorithmException, KeyManagementException
  {
    System.out.println("Running testSSLConnectionInvalidPassword");

    String filename = "facebook-10443244874876159931";

    // create a trust manager
    // (note: a real application should verify certificates)
    TrustManager naiveTrustMgr = new X509TrustManager() {
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

    // create an SSL context
    SSLContext sslContext = SSLContext.getInstance("SSLv3");
    sslContext.init(null, new TrustManager[] { naiveTrustMgr }, null);

    // create the client
    // (note: a real application should use a COMMON, STRICT, or implemented
    // hostname verifier)
    SecurityContext secContext = newSecurityContext("rest-admin", "foo").withSSLContext(sslContext, new X509TrustManager() {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}}).withSSLHostnameVerifier(SSLHostnameVerifier.ANY);
    DatabaseClient client = DatabaseClientFactory.newClient(appServerHostname, 8012, secContext, getConnType());

    String expectedException = "FailedRequestException: Local message: write failed: Unauthorized";
    String exception = "";

    // write doc
    try
    {
      writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
    } catch (Exception e) {
      exception = e.toString();
    }

    System.out.println("Actual exception: " + exception);
    boolean isExceptionThrown = exception.contains(expectedException);

    assertTrue(isExceptionThrown);

    // release client
    client.release();
  }

  @Test
  public void testSSLConnectionInvalidUser() throws KeyManagementException, NoSuchAlgorithmException, IOException, NoSuchAlgorithmException, KeyManagementException
  {
    System.out.println("Running testSSLConnectionInvalidUser");

    String filename = "facebook-10443244874876159931";

    // create a trust manager
    // (note: a real application should verify certificates)
    TrustManager naiveTrustMgr = new X509TrustManager() {
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

    // create an SSL context
    SSLContext sslContext = SSLContext.getInstance("SSLv3");
    sslContext.init(null, new TrustManager[] { naiveTrustMgr }, null);

    // create the client
    // (note: a real application should use a COMMON, STRICT, or implemented
    // hostname verifier)
    SecurityContext secContext = newSecurityContext("MyFooUser", "x");
    secContext.withSSLContext(sslContext, new X509TrustManager() {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}}).withSSLHostnameVerifier(SSLHostnameVerifier.ANY);
    DatabaseClient client = DatabaseClientFactory.newClient(appServerHostname, 8012, secContext, getConnType());

    String expectedException = "FailedRequestException: Local message: write failed: Unauthorized";
    String exception = "";

    // write doc
    try
    {
      writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
    } catch (Exception e) {
      exception = e.toString();
    }

    boolean isExceptionThrown = exception.contains(expectedException);

    assertTrue(isExceptionThrown);

    // release client
    client.release();
  }

  @AfterAll
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}
