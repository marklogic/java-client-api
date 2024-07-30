/*
 * Copyright (c) 2023 MarkLogic Corporation
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

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.Transaction;
import com.marklogic.client.alerting.RuleDefinition;
import com.marklogic.client.alerting.RuleDefinitionList;
import com.marklogic.client.alerting.RuleManager;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.*;
import com.marklogic.client.query.*;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDatabaseClientConnection extends AbstractFunctionalTest {

  @Test
  public void testReleasedClient() throws IOException, KeyManagementException, NoSuchAlgorithmException
  {
    System.out.println("Running testReleasedClient");

    String filename = "facebook-10443244874876159931";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write doc
    writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");

    // release client
    client.release();

    String stringException = "";

    // write doc on released client
    try {
      writeDocumentUsingStringHandle(client, filename, "/write-txt-doc-released-client/", "Text");
    } catch (Exception e) {
      stringException = "Client is not available - " + e;
    }

    String expectedException = "Client is not available - java.lang.IllegalStateException: You cannot use this connected object anymore--connection has already been released";
    assertEquals( expectedException, stringException);
  }

  @Test
  public void testDatabaseClientConnectionExist() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testDatabaseClientConnectionExist");

    DatabaseClient client = getDatabaseClient("rest-reader", "x", getConnType());
    String[] stringClient = client.toString().split("@");
    assertEquals( "com.marklogic.client.impl.DatabaseClientImpl", stringClient[0]);

    // release client
    client.release();
  }

  // To test getters of SecurityContext
  @Test
  public void testDatabaseClientGetters() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testDatabaseClientGetters");

    DatabaseClient client = null;
	SSLContext sslcontext = null;
	SecurityContext secContext = newSecurityContext("rest-reader", "x");

		try {
			sslcontext = getSslContext();
		} catch (UnrecoverableKeyException | KeyStoreException | CertificateException e) {
			e.printStackTrace();
		}

		secContext.withSSLContext(sslcontext, new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
				// nothing to do
			}

			public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
				// nothing to do
			}

			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		})
		.withSSLHostnameVerifier(SSLHostnameVerifier.ANY);

		client = newDatabaseClientBuilder().withSecurityContext(secContext).build();
	SecurityContext readSecContext = client.getSecurityContext();
	String verifier = readSecContext.getSSLHostnameVerifier().toString();
	String protocol = readSecContext.getSSLContext().getProtocol();
	boolean needClient = readSecContext.getSSLContext().getSupportedSSLParameters().getNeedClientAuth();

    assertTrue(verifier.contains("Builtin"));
    assertTrue(protocol.contains("TLSv1.2"));
    assertTrue(needClient == false);
    // release client
    client.release();
  }


	@Test
	void invalidPort() {
		int assumedInvalidPort = 60123;
		DatabaseClient client = newDatabaseClientBuilder().withPort(assumedInvalidPort).build();

		MarkLogicIOException ex = Assertions.assertThrows(MarkLogicIOException.class, () -> client.checkConnection());
		String expected = "Error occurred while calling http://localhost:60123/v1/ping; java.net.ConnectException: " +
							  "Failed to connect to localhost/127.0.0.1:60123 ; possible reasons for the error include " +
							  "that a MarkLogic app server may not be listening on the port, or MarkLogic was stopped " +
							  "or restarted during the request; check the MarkLogic server logs for more information.";
		assertEquals(expected, ex.getMessage());
	}

  @Test
  public void testDatabaseClientConnectionInvalidUser() throws IOException, KeyManagementException, NoSuchAlgorithmException
  {
    System.out.println("Running testDatabaseClientConnectionInvalidUser");

    String filename = "facebook-10443244874876159931";

    DatabaseClient client = getDatabaseClient("foo-the-bar", "x", getConnType());

    String expectedException = "com.marklogic.client.FailedRequestException: Local message: write failed: Unauthorized. Server Message: Unauthorized";
    String exception = "";

    // write doc
    try {
      writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
    } catch (Exception e) {
      exception = e.toString();
    }

    // System.out.println(exception);

    boolean exceptionIsThrown = exception.contains(expectedException);
    assertTrue(exceptionIsThrown);

    // release client
    client.release();
  }

  @Test
  public void testDatabaseClientConnectionInvalidPassword() throws IOException, KeyManagementException, NoSuchAlgorithmException
  {
    System.out.println("Running testDatabaseClientConnectionInvalidPassword");

    String filename = "facebook-10443244874876159931";

    DatabaseClient client = getDatabaseClient("rest-writer", "foobar", getConnType());

    String expectedException = "com.marklogic.client.FailedRequestException: Local message: write failed: Unauthorized. Server Message: Unauthorized";
    String exception = "";

    // write doc
    try {
      writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
    } catch (Exception e) {
      exception = e.toString();
    }

    // System.out.println(exception);

    boolean exceptionIsThrown = exception.contains(expectedException);
    assertTrue(exceptionIsThrown);

    // release client
    client.release();
  }

  @Test
  public void testDatabaseClientConnectionInvalidHost()
  {
    System.out.println("Running testDatabaseClientConnectionInvalidHost");

    String filename = "facebook-10443244874876159931";

    DatabaseClient client = newDatabaseClientBuilder().withHost("badhost").build();

    // String expectedException =
    // "com.sun.jersey.api.client.ClientHandlerException: java.net.UnknownHostException: foobarhost: Name or service not known";
    String expectedException = "UnknownHostException";

    String exception = "";

    // write doc
    try {
      writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
    } catch (Exception e) {
      exception = e.toString();
    }

    System.out.println(exception);

    assertTrue(exception.contains(expectedException));

    // release client
    client.release();
  }

  /*
   * These tests are specifically to validate Git Issue 332.
   * https://github.com/marklogic/java-client-api/issues/332
   */

  // Trying to access database without specifying the database name.
  @Test
  public void testDBClientUsingWithoutDatabaseName() throws IOException, SAXException, ParserConfigurationException
  {
    System.out.println("Running testDBClientUsingWithoutDatabaseName");

    String filename = "xml-original-test.xml";
    String uri = "/write-xml-string/";
    DatabaseClient client = newDatabaseClientBuilder().build();

    // write doc
    writeDocumentUsingStringHandle(client, filename, uri, "XML");
    // read docs
    StringHandle contentHandle = readDocumentUsingStringHandle(client, uri + filename, "XML");
    String readContent = contentHandle.get();

    // get xml document for expected result
    Document expectedDoc = expectedXMLDocument(filename);

    // convert actual string to xml doc
    Document readDoc = convertStringToXMLDocument(readContent);

    assertXMLEqual("Write XML difference", expectedDoc, readDoc);

    // release client
    client.release();
  }

  // Trying to access database by specifying the database name.
  @Test
  public void testDBClientUsingWithDatabaseName() throws IOException, SAXException, ParserConfigurationException
  {
    System.out.println("Running testDBClientUsingWithDatabaseName");

    String filename = "xml-original-test.xml";
    String uri = "/write-xml-string/";
    DatabaseClient client = newDatabaseClientBuilder().withDatabase(DB_NAME).build();

    // write doc
    writeDocumentUsingStringHandle(client, filename, uri, "XML");
    // read docs
    StringHandle contentHandle = readDocumentUsingStringHandle(client, uri + filename, "XML");
    String readContent = contentHandle.get();

    // get xml document for expected result
    Document expectedDoc = expectedXMLDocument(filename);

    // convert actual string to xml doc
    Document readDoc = convertStringToXMLDocument(readContent);

    assertXMLEqual("Write XML difference", expectedDoc, readDoc);

    // release client
    client.release();
  }

  @Test
  public void testUberSearchSuggestionMultiByte() throws FileNotFoundException
  {
    System.out.println("Running testUberSearchSuggestionMultiByte");

    String[] filenames = { "multibyte1.xml", "multibyte2.xml", "multibyte3.xml" };
    String queryOptionName = "suggestionOpt.xml";

    DatabaseClient client = newDatabaseClientBuilder().build();
    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/ss/", "XML");
    }

    setQueryOption(client, queryOptionName);
    QueryManager queryMgr = client.newQueryManager();
    SuggestDefinition def = queryMgr.newSuggestDefinition("上海", queryOptionName);

    String[] suggestions = queryMgr.suggest(def);

    for (int i = 0; i < suggestions.length; i++) {
      System.out.println(suggestions[i]);
    }

    assertTrue(suggestions[0].contains("上海"));
    // release client
    client.release();
  }

  @Test
  public void testQueryManagerTuples() throws IOException
  {
    System.out.println("Running testQueryManagerTuples");
    String[] filenames = { "aggr1.xml", "aggr2.xml", "aggr3.xml", "aggr4.xml", "aggr5.xml" };
    String queryOptionName = "aggregatesOpt.xml";

    DatabaseClient client = newDatabaseClientBuilder().build();
	  deleteDocuments(client);

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/tuples-aggr/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    ValuesDefinition queryDef = queryMgr.newValuesDefinition("popularity", "aggregatesOpt.xml");
    queryDef.setAggregate("correlation", "covariance");
    queryDef.setName("pop-rate-tups");

    // create tuples handle
    TuplesHandle tuplesHandle = new TuplesHandle();
    queryMgr.tuples(queryDef, tuplesHandle);

    AggregateResult[] agg = tuplesHandle.getAggregates();
    System.out.println(agg.length);
    assertEquals(2, agg.length);
    double correlation = agg[0].get("xs:double", Double.class);
    double covariance = agg[1].get("xs:double", Double.class);

    DecimalFormat df = new DecimalFormat("###.##");
    String roundedCorrelation = df.format(correlation);
    String roundedCovariance = df.format(covariance);

    System.out.println(roundedCorrelation);
    System.out.println(roundedCovariance);

    assertEquals( "0.26", roundedCorrelation);
    assertEquals( "0.35", roundedCovariance);

    ValuesListDefinition vdef = queryMgr.newValuesListDefinition("aggregatesOpt.xml");
    ValuesListHandle results = queryMgr.valuesList(vdef, new ValuesListHandle());
    // Get the Map of lexicons sorted.
    Map<String, String> lexiconMap = results.getValuesMap();
    TreeMap<String, String> treeMap = new TreeMap<>(lexiconMap);
    assertEquals(treeMap.size(), 3);
    assertEquals(treeMap.firstKey(), "pop-aggr");
    assertEquals(treeMap.lastKey(), "score-aggr");

    // release client
    client.release();
  }

  @Test
  public void testValuesOccurrences() throws IOException
  {
    System.out.println("Running testValuesOccurences");

    String[] filenames = { "aggr1.xml", "aggr2.xml", "aggr3.xml", "aggr4.xml" };
    String queryOptionName = "aggregatesOpt5Occ.xml";

    DatabaseClient client = newDatabaseClientBuilder().withDatabase(DB_NAME).build();

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/values-aggr/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    ValuesDefinition queryDef = queryMgr.newValuesDefinition("title", "aggregatesOpt5Occ.xml");
    queryDef.setAggregate("count");
    queryDef.setName("title-val");

    // create handle
    ValuesHandle valuesHandle = new ValuesHandle();
    queryMgr.values(queryDef, valuesHandle);

    AggregateResult[] agg = valuesHandle.getAggregates();
    System.out.println(agg.length);
    System.out.println(agg[0].getValue());

    // release client
    client.release();
  }

  @Test
  public void testTransactionReadStatus() {

    System.out.println("Running testTransactionReadStatus");

    String docId[] = { "/foo/test/transactionURIFoo1.txt", "/foo/test/transactionURIFoo2.txt", "/foo/test/transactionURIFoo3.txt" };
    DatabaseClient client =  newDatabaseClientBuilder().build();
    Transaction transaction = client.openTransaction();
    try {
      TextDocumentManager docMgr = client.newTextDocumentManager();
      docMgr.setMetadataCategories(Metadata.ALL);
      DocumentWriteSet writeset = docMgr.newWriteSet();

      writeset.add(docId[0], new StringHandle().with("This is so transactionURIFoo 1"));
      writeset.add(docId[1], new StringHandle().with("This is so transactionURIFoo 2"));
      writeset.add(docId[2], new StringHandle().with("This is so transactionURIFoo 3"));
      docMgr.write(writeset, transaction);
      StringHandle wrteTransHandle = new StringHandle();
      transaction.readStatus(wrteTransHandle);
      assertTrue((wrteTransHandle.get()).contains(getRestServerName()));
      transaction.commit();

      transaction = client.openTransaction();

      DocumentPage page = docMgr.read(transaction, docId[0], docId[1], docId[2]);
      assertTrue(page.size() == 3);
      StringHandle readTransHandle = new StringHandle();
      transaction.readStatus(readTransHandle);
      assertTrue((readTransHandle.get()).contains(getRestServerName()));

    } catch (Exception exp) {
      System.out.println(exp.getMessage());
      throw exp;
    } finally {
      transaction.rollback();
    }
  }

  @Test
  public void testRMMatchQDAndDocIds() throws IOException, KeyManagementException, NoSuchAlgorithmException
  {
    System.out.println("Running testRMMatchQDAndDocIds");
    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String[] docIds = new String[5];
    String[] candidateRules = { "RULE-TEST-1", "RULE-TEST-2" };
    int i = 0;

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
      docIds[i++] = new String("/raw-alert/" + filename);
    }

    // create a manager for configuring rules
    RuleManager ruleMgr = client.newRuleManager();

    // create handle
    InputStreamHandle ruleHandle1 = new InputStreamHandle();
    InputStreamHandle ruleHandle2 = new InputStreamHandle();

    // get the rule file
    InputStream inputStream1 = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/rules/alertRule1.xml");
    InputStream inputStream2 = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/rules/alertRule2.xml");

    ruleHandle1.set(inputStream1);
    ruleHandle2.set(inputStream2);

    // write the rule to the database
    ruleMgr.writeRule(candidateRules[0], ruleHandle1);
    ruleMgr.writeRule(candidateRules[1], ruleHandle2);

    // create a manager for document search criteria
    QueryManager queryMgr = client.newQueryManager();

    // specify the search criteria for the documents
    String criteria = "atlantic";
    // StringQueryDefinition querydef = queryMgr.newStringDefinition();
    StringQueryDefinition querydef = queryMgr.newStringDefinition();
    querydef.setCriteria(criteria);

    // create a manager for matching rules
    RuleManager ruleMatchMgr = client.newRuleManager();

    // match the rules against the documents qualified by the criteria
    RuleDefinitionList matchedRulesDefList = new RuleDefinitionList();
    // String[] candidateRules = {"RULE-TEST-1", "RULE-TEST-2"};
    RuleDefinitionList matchedRules = ruleMatchMgr.match(querydef, 0, 10, candidateRules, matchedRulesDefList);

    System.out.println(matchedRules.size());

    String expected = "";

    // iterate over the matched rules
    Iterator<RuleDefinition> ruleItr = matchedRules.iterator();
    while (ruleItr.hasNext()) {
      RuleDefinition rule = ruleItr.next();
      System.out.println(
          "document criteria " + criteria + " matched rule " +
              rule.getName() + " with metadata " + rule.getMetadata()
          );
      expected = expected + rule.getName() + " - " + rule.getMetadata() + " | ";
    }

    System.out.println(expected);

    assertTrue(expected.contains("RULE-TEST-1 - {rule-number=one}") && expected.contains("RULE-TEST-2 - {rule-number=two}"));

    // release client
    client.release();
  }

  // Test to validate that addAs with a java.io.object in DocumentWriteSet
  // writes the document.
  @Test
  public void testAddAs() throws Exception {

    System.out.println("Running testAddAs");

    String[] docId = { "aggr1.xml", "aggr2.xml", "aggr3.xml" };
    DatabaseClient client = newDatabaseClientBuilder().build();
    Transaction transaction = client.openTransaction();

    try {
      TextDocumentManager docMgr = client.newTextDocumentManager();
      docMgr.setMetadataCategories(Metadata.ALL);
      DocumentWriteSet writeset = docMgr.newWriteSet();

      InputStream inputStream1 = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/data/" + docId[0]);
      InputStream inputStream2 = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/data/" + docId[1]);
      InputStream inputStream3 = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/data/" + docId[2]);
      writeset.addAs(docId[0], inputStream1);
      writeset.addAs(docId[1], inputStream2);
      writeset.addAs(docId[2], inputStream3);

      docMgr.write(writeset, transaction);
      StringHandle wrteTransHandle = new StringHandle();
      transaction.readStatus(wrteTransHandle);
		System.out.println(wrteTransHandle.get());
      assertTrue((wrteTransHandle.get()).contains(getRestServerName()));
      transaction.commit();

      transaction = client.openTransaction();
      String txId = transaction.getTransactionId();

      DocumentPage page = docMgr.read(transaction, docId[0], docId[1], docId[2]);
      assertTrue(page.size() == 3);
      // Read back the doc contents to make sure that write succeeded.
      String strDocContent1 = docMgr.read(docId[0], new StringHandle()).get();
      assertTrue(strDocContent1.contains("Vannevar Bush wrote an article for The Atlantic Monthly"));
      String strDocContent2 = docMgr.read(docId[1], new StringHandle()).get();
      assertTrue(strDocContent2.contains("The Bush article described a device called a Memex."));
      String strDocContent3 = docMgr.read(docId[2], new StringHandle()).get();
      assertTrue(strDocContent3.contains("For 1945, the thoughts expressed in The Atlantic Monthly were groundbreaking."));

      StringHandle readTransHandle = new StringHandle();
      transaction.readStatus(readTransHandle);
      assertTrue((readTransHandle.get()).contains(getRestServerName()));
      assertTrue((readTransHandle.get()).contains(DB_NAME));
      assertTrue((readTransHandle.get()).contains(txId));
    } catch (Exception exp) {
      System.out.println(exp.getMessage());
      throw exp;
    } finally {
      transaction.rollback();
    }
  }

  @Test
  public void testRuleManagerReadAs() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
  {
    System.out.println("Running testRuleManagerReadAs");

    String ruleName1 = "RULE-TEST-1";
    String ruleName2 = "RULE-TEST-2";
    BufferedReader bufInputStream = null;
    DatabaseClient client = null;
    try {
      String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

      client = newDatabaseClientBuilder().build();
      // write docs
      for (String filename : filenames) {
        writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
      }

      // create a manager for configuring rules
      RuleManager ruleMgr = client.newRuleManager();

      // create handle
      InputStreamHandle ruleHandle1 = new InputStreamHandle();
      InputStreamHandle ruleHandle2 = new InputStreamHandle();

      // get the rule file
      InputStream inputStream1 = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/rules/alertRule1.xml");
      InputStream inputStream2 = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/rules/alertRule2.xml");

      ruleHandle1.set(inputStream1);
      ruleHandle2.set(inputStream2);

      // write the rule to the database
      ruleMgr.writeRule(ruleName1, ruleHandle1);
      ruleMgr.writeRule(ruleName2, ruleHandle2);

      // create a manager for document search criteria
      QueryManager queryMgr = client.newQueryManager();

      // specify the search criteria for the documents
      String criteria = "atlantic";
      StringQueryDefinition querydef = queryMgr.newStringDefinition();
      querydef.setCriteria(criteria);

      // create a manager for reading rules
      RuleManager ruleReadMgr = client.newRuleManager();

      // Test for readRule
      RuleDefinition ruleReadDef1 = ruleReadMgr.readRule(ruleName1, new RuleDefinition());
      assertTrue(ruleName1.equalsIgnoreCase(ruleReadDef1.getName()));
      assertTrue(ruleReadDef1.getDescription().equalsIgnoreCase("rule for test1"));
      // End of Test for readRule

      // Test for readRuleAs
      String bufCurrentLine = null;
      StringBuffer srtBuf = new StringBuffer();
      File rule2ReadAsFile = ruleReadMgr.readRuleAs(ruleName2, File.class);

      bufInputStream = new BufferedReader(new FileReader(rule2ReadAsFile));
      while ((bufCurrentLine = bufInputStream.readLine()) != null) {
        srtBuf.append(bufCurrentLine);
        System.out.println(bufCurrentLine);
      }
      assertTrue(srtBuf.toString().contains(ruleName2));
      assertTrue(srtBuf.toString().contains("rule for test2"));
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      if (bufInputStream != null)
        bufInputStream.close();
    }

    // release client
    client.release();
  }

  @Test
  public void testRMMatchAsWithCandidates() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException, KeyManagementException,
      NoSuchAlgorithmException
  {
    System.out.println("Running testRMMatchAsWithCandidates");
    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String ruleName1 = "RULE-TEST-1";
    String ruleName2 = "RULE-TEST-2";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());
    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
    }

    // create a manager for configuring rules
    RuleManager ruleMgr = client.newRuleManager();

    // create handle
    InputStreamHandle ruleHandle1 = new InputStreamHandle();
    InputStreamHandle ruleHandle2 = new InputStreamHandle();

    // get the rule file
    InputStream inputStream1 = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/rules/alertRule1.xml");
    InputStream inputStream2 = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/rules/alertRule2.xml");

    ruleHandle1.set(inputStream1);
    ruleHandle2.set(inputStream2);

    // write the rule to the database
    ruleMgr.writeRule(ruleName1, ruleHandle1);
    ruleMgr.writeRule(ruleName2, ruleHandle2);

    // create a manager for matching rules
    RuleManager ruleMatchMgr = client.newRuleManager();

    // match the rules against the documents qualified by the criteria
    RuleDefinitionList matchedRulesDefList = new RuleDefinitionList();

    InputStream inputStreamMatch = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/data/constraint1.xml");
    RuleDefinitionList matchedRules = ruleMatchMgr.matchAs(inputStreamMatch, new String[] { "RULE-TEST-1", "RULE-TEST-2" }, matchedRulesDefList);

    System.out.println(matchedRules.size());

    String expected = "";

    // iterate over the matched rules
    Iterator<RuleDefinition> ruleItr = matchedRules.iterator();
    while (ruleItr.hasNext())
    {
      RuleDefinition rule = ruleItr.next();
      System.out.println(
          "document criteria matched rule " +
              rule.getName() + " with metadata " + rule.getMetadata()
          );
      expected = expected + rule.getName() + " - " + rule.getMetadata() + " | ";
    }

    System.out.println(expected);
    assertTrue(expected.contains("RULE-TEST-1 - {rule-number=one}") && expected.contains("RULE-TEST-2 - {rule-number=two}"));

    // release client
    client.release();
  }

  @Test
  public void testRuleManagerMatchAs() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException, KeyManagementException,
      NoSuchAlgorithmException
  {
    System.out.println("Running testRuleManagerMatchAs");
    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String[] rules = new String[] { "RULE-TEST-1", "RULE-TEST-2" };

    // DatabaseClient client =
    // DatabaseClientFactory.newClient(appServerHostname, 8011, "rest-admin",
    // "x", getConnType());
    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
    }

    // create a manager for configuring rules
    RuleManager ruleMgr = client.newRuleManager();

    // create handle
    InputStreamHandle ruleHandle1 = new InputStreamHandle();
    InputStreamHandle ruleHandle2 = new InputStreamHandle();

    // get the rule file
    InputStream inputStream1 = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/rules/alertRule1.xml");
    InputStream inputStream2 = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/rules/alertRule2.xml");

    ruleHandle1.set(inputStream1);
    ruleHandle2.set(inputStream2);

    // write the rule to the database
    ruleMgr.writeRule(rules[0], ruleHandle1);
    ruleMgr.writeRule(rules[1], ruleHandle2);

    // create a manager for matching rules
    RuleManager ruleMatchMgr = client.newRuleManager();

    // match the rules against the documents qualified by the criteria
    RuleDefinitionList matchedRulesDefList = new RuleDefinitionList();
    InputStream inputStreamMatch = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/data/constraint1.xml");
    RuleDefinitionList matchedRules = ruleMatchMgr.matchAs(inputStreamMatch, matchedRulesDefList);

    System.out.println(matchedRules.size());
    String expected = "";

    // iterate over the matched rules
    Iterator<RuleDefinition> ruleItr = matchedRules.iterator();
    while (ruleItr.hasNext()) {
      RuleDefinition rule = ruleItr.next();
      System.out.println(
          "document criteria matched rule " +
              rule.getName() + " with metadata " + rule.getMetadata()
          );
      expected = expected + rule.getName() + " - " + rule.getMetadata() + " | ";
    }
    System.out.println(expected);
    assertTrue( expected.contains("RULE-TEST-1 - {rule-number=one}") && expected.contains("RULE-TEST-2 - {rule-number=two}"));

    // release client
    client.release();
  }
}
