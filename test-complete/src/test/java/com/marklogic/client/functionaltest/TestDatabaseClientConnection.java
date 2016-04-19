/*
* Copyright 2014-2016 MarkLogic Corporation
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

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.Transaction;
import com.marklogic.client.alerting.RuleDefinition;
import com.marklogic.client.alerting.RuleDefinitionList;
import com.marklogic.client.alerting.RuleManager;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.TuplesHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.io.ValuesListHandle;
import com.marklogic.client.query.AggregateResult;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.SuggestDefinition;
import com.marklogic.client.query.ValuesDefinition;
import com.marklogic.client.query.ValuesListDefinition;

public class TestDatabaseClientConnection extends BasicJavaClientREST {
	
	private static String dbName = "DatabaseClientConnectionDB";
	private static String [] fNames = {"DatabaseClientConnectionDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	
	// These members are used to test Git Issue 332.
	private static String UberdbName = "UberDatabaseClientConnectionDB";
	private static String UberDefaultdbName = "Documents";
	private static String [] UberfNames = {"UberDatabaseClientConnectionDB-1"};
	private static int Uberport = 8000;
	private static String UberrestServerName = "App-Services";
	
	@BeforeClass
	public static void setUp() throws Exception 
	{
		System.out.println("In setup");	
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
		
		/*
		 * Only users with the http://marklogic.com/xdmp/privileges/xdmp-eval-in (xdmp:eval-in) or equivalent privilege can 
		 * send request parameter that enables the request to be evaluated against a content database other than the default 
		 * database associated with the REST API instances
		 */
		createUserRolesWithPrevilages("test-eval","xdbc:eval", "xdbc:eval-in","xdmp:eval-in","any-uri","xdbc:invoke");
	    createRESTUser("eval-user", "x", "test-eval","rest-admin","rest-writer","rest-reader","rest-extension-user","manage-user");
	    
	     // Create a database and forest for use on uber port (8000). Do not associate the REST server (on 8000) with an DB.
		createDB(UberdbName);
		createForest(UberfNames[0], UberdbName);
		
		setupAppServicesConstraint(UberdbName);
		addRangeElementIndex(UberdbName, "string", "http://action/", "title", "http://marklogic.com/collation/");
		addRangeElementIndex(UberdbName, "string", "http://noun/", "title", "http://marklogic.com/collation/");		
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testReleasedClient() throws IOException
	{
		System.out.println("Running testReleasedClient");
		
		String filename = "facebook-10443244874876159931";
		
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// write doc
		writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
		
		// release client
		client.release();
		
		String stringException = "";
		
		// write doc on released client
		try {
			writeDocumentUsingStringHandle(client, filename, "/write-txt-doc-released-client/", "Text");
		} 
		catch (Exception e) {
			stringException = "Client is not available - " + e;
		}
		
		String expectedException = "Client is not available - java.lang.IllegalStateException: You cannot use this connected object anymore--connection has already been released";
		assertEquals("Exception is not thrown", expectedException, stringException);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testDatabaseClientConnectionExist()
	{
		System.out.println("Running testDatabaseClientConnectionExist");
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-reader", "x", Authentication.DIGEST);
		String[] stringClient = client.toString().split("@");
		assertEquals("Object does not exist", "com.marklogic.client.impl.DatabaseClientImpl", stringClient[0]);
		
		// release client
		client.release();
	}

	@SuppressWarnings("deprecation")
	@Test	public void testDatabaseClientConnectionInvalidPort() throws IOException
	{
		System.out.println("Running testDatabaseClientConnectionInvalidPort");
		
		String filename = "facebook-10443244874876159931";
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8033, "rest-reader", "x", Authentication.DIGEST);
		
		String expectedException = "com.sun.jersey.api.client.ClientHandlerException: org.apache.http.conn.HttpHostConnectException: Connection to http://localhost:8033 refused";
		String exception = "";
		
		// write doc
		try {
			writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
		}
		catch (Exception e) { exception = e.toString(); }
		
		assertEquals("Exception is not thrown", expectedException, exception);
		
		// release client
		client.release();
	}
	
	@SuppressWarnings("deprecation")
	@Test	public void testDatabaseClientConnectionInvalidUser() throws IOException
	{
		System.out.println("Running testDatabaseClientConnectionInvalidUser");
		
		String filename = "facebook-10443244874876159931";
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "foo-the-bar", "x", Authentication.DIGEST);
		
		String expectedException = "com.marklogic.client.FailedRequestException: Local message: write failed: Unauthorized. Server Message: Unauthorized";
		String exception = "";
		
		// write doc
		try {
			writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
		}
		catch (Exception e) { exception = e.toString(); }
		
		//System.out.println(exception);
		
	    boolean exceptionIsThrown = exception.contains(expectedException);
	    assertTrue("Exception is not thrown", exceptionIsThrown);
		
		// release client
		client.release();
	}

	@SuppressWarnings("deprecation")
	@Test	public void testDatabaseClientConnectionInvalidPassword() throws IOException
	{
		System.out.println("Running testDatabaseClientConnectionInvalidPassword");
		
		String filename = "facebook-10443244874876159931";
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "foobar", Authentication.DIGEST);
		
		String expectedException = "com.marklogic.client.FailedRequestException: Local message: write failed: Unauthorized. Server Message: Unauthorized";
		String exception = "";
		
		// write doc
		try {
			writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
		}
		catch (Exception e) { exception = e.toString(); }
		
		//System.out.println(exception);
		
		boolean exceptionIsThrown = exception.contains(expectedException);
	    assertTrue("Exception is not thrown", exceptionIsThrown);
		
		// release client
		client.release();
	}
	
	@SuppressWarnings("deprecation")
	@Test	public void testDatabaseClientConnectionInvalidHost() throws IOException
	{
		System.out.println("Running testDatabaseClientConnectionInvalidHost");
		
		String filename = "facebook-10443244874876159931";
		
		DatabaseClient client = DatabaseClientFactory.newClient("foobarhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		//String expectedException = "com.sun.jersey.api.client.ClientHandlerException: java.net.UnknownHostException: foobarhost: Name or service not known";
		String expectedException = "UnknownHostException";
		
		String exception = "";
		
		// write doc
		try {
			writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
		}
		catch (Exception e) { exception = e.toString(); }
		
		System.out.println(exception);
		
		assertTrue("Exception is not thrown", exception.contains(expectedException));
		
		// release client
		client.release();
	}
	
	// Trying to access database without specifying the database name.
	@SuppressWarnings("deprecation")
	@Test	public void testDBClientUsingWithoutDatabaseName() throws IOException, SAXException, ParserConfigurationException
	{
		System.out.println("Running testDBClientUsingWithoutDatabaseName");
		
		String filename = "xml-original-test.xml";
		String uri = "/write-xml-string/";		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", Uberport, "eval-user", "x", Authentication.DIGEST);				
		String exception = "";
			
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
	@SuppressWarnings("deprecation")
	@Test	public void testDBClientUsingWithDatabaseName() throws IOException, SAXException, ParserConfigurationException
	{
		System.out.println("Running testDBClientUsingWithDatabaseName");
		
		String filename = "xml-original-test.xml";
		String uri = "/write-xml-string/";		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", Uberport, UberdbName, "eval-user", "x", Authentication.DIGEST);				
		String exception = "";
				
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

		String[] filenames = {"multibyte1.xml", "multibyte2.xml", "multibyte3.xml"};
		String queryOptionName = "suggestionOpt.xml";
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", Uberport, UberdbName, "eval-user", "x", Authentication.DIGEST);			
		// write docs
		for(String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/ss/", "XML");
		}

		setQueryOption(client, queryOptionName);
		QueryManager queryMgr = client.newQueryManager();
		SuggestDefinition def = queryMgr.newSuggestDefinition("上海", queryOptionName);

		String[] suggestions = queryMgr.suggest(def);

		for(int i = 0; i < suggestions.length; i++) {
			System.out.println(suggestions[i]);
		}
		
		assertTrue("suggestion is wrong", suggestions[0].contains("上海"));
		// release client		
		client.release();
	}
	
	@Test
	public void testQueryManagerTuples() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testQueryManagerTuples");
		
		String[] filenames = {"aggr1.xml", "aggr2.xml", "aggr3.xml", "aggr4.xml", "aggr5.xml"};
		String queryOptionName = "aggregatesOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", Uberport, UberdbName, "eval-user", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames) {
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
        assertEquals("Invalid length", 2, agg.length);
        double correlation = agg[0].get("xs:double", Double.class);
        double covariance = agg[1].get("xs:double", Double.class);
        
        DecimalFormat df = new DecimalFormat("###.##");
        String roundedCorrelation = df.format(correlation);
        String roundedCovariance = df.format(covariance);
        
        System.out.println(roundedCorrelation);
        System.out.println(roundedCovariance);
        
        ValuesListDefinition vdef = queryMgr.newValuesListDefinition("aggregatesOpt.xml");
        ValuesListHandle results = queryMgr.valuesList(vdef, new ValuesListHandle());
        // Get the Map of lexicons sorted.
        HashMap<String,String> lexiconMap = results.getValuesMap();
        TreeMap<String, String> treeMap = new TreeMap<String,String>(lexiconMap);
        assertEquals("Map should contain three keys", treeMap.size(), 3);
        assertEquals("First key incorrect",treeMap.firstKey(), "pop-aggr");
        assertEquals("Last key incorrect",treeMap.lastKey(), "score-aggr");
               		
		// release client
		client.release();	
	}
	
	@Test
	public void testValuesOccurences() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testValuesOccurences");
		
		String[] filenames = {"aggr1.xml", "aggr2.xml", "aggr3.xml", "aggr4.xml"};
		String queryOptionName = "aggregatesOpt5Occ.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", Uberport, UberdbName, "eval-user", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames) {
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
	public void testTransactionReadStatus() throws Exception {
		
		System.out.println("Running testTransactionReadStatus");
		
		String docId[] = {"/foo/test/transactionURIFoo1.txt","/foo/test/transactionURIFoo2.txt","/foo/test/transactionURIFoo3.txt"};
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", Uberport, UberdbName, "eval-user", "x", Authentication.DIGEST);
		Transaction transaction = client.openTransaction();
		try {
			TextDocumentManager docMgr = client.newTextDocumentManager();
			docMgr.setMetadataCategories(Metadata.ALL);
			DocumentWriteSet writeset = docMgr.newWriteSet();
			
			DocumentMetadataHandle mhRead = new DocumentMetadataHandle();		
			
			writeset.add(docId[0], new StringHandle().with("This is so transactionURIFoo 1"));
			writeset.add(docId[1], new StringHandle().with("This is so transactionURIFoo 2"));
			writeset.add(docId[2], new StringHandle().with("This is so transactionURIFoo 3"));
			docMgr.write(writeset, transaction);
			StringHandle wrteTransHandle = new StringHandle();
			transaction.readStatus(wrteTransHandle);
			assertTrue("Transaction readStatus during write does not contain Database name. ", (wrteTransHandle.get()).contains(UberrestServerName));
			assertTrue("Transaction readStatus during write does not contain Database name. ", (wrteTransHandle.get()).contains("App-Services"));
			transaction.commit();
			
			transaction = client.openTransaction();

			DocumentPage page = docMgr.read(transaction, docId[0], docId[1], docId[2]);
			assertTrue("DocumentPage Size did not return expected value:: returned==  "+page.size(), page.size() == 3 );
			StringHandle readTransHandle = new StringHandle();
			transaction.readStatus(readTransHandle);
			assertTrue("Transaction readStatus during read does not contain Database name. ", (readTransHandle.get()).contains(UberrestServerName));
			assertTrue("Transaction readStatus during read does not contain Database name. ", (readTransHandle.get()).contains("App-Services"));
			
		}
		catch(Exception exp) {
			System.out.println(exp.getMessage());
			throw exp;
		}
		finally {
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
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		client = DatabaseClientFactory.newClient("localhost", Uberport, UberdbName, "eval-user", "x", Authentication.DIGEST);
		// write docs
		for(String filename : filenames) {
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
		assertTrue("Rule Manager readRule name method asserts", ruleName1.equalsIgnoreCase(ruleReadDef1.getName()));
		assertTrue("Rule Manager readRule description method asserts", ruleReadDef1.getDescription().equalsIgnoreCase("rule for test1"));
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
		assertTrue("Rule Manager readRuleAs name method asserts", srtBuf.toString().contains(ruleName2));
		assertTrue("Rule Manager readRuleAs description method asserts", srtBuf.toString().contains("rule for test2"));		
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		finally {
			if (bufInputStream != null)
			bufInputStream.close();
		}

		// release client
		client.release();		
	}
	
	@Test	
	public void testRMMatchQDAndDocIds() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{
		System.out.println("Running testRMMatchQDAndDocIds");		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String[] docIds = new String[5];
		String[] candidateRules = {"RULE-TEST-1", "RULE-TEST-2"};
		int i=0;		
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
			docIds[i++] = new String("/raw-alert/"+filename);
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
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		querydef.setCriteria(criteria);

		// create a manager for matching rules
		RuleManager ruleMatchMgr = client.newRuleManager();

		// match the rules against the documents qualified by the criteria
		RuleDefinitionList matchedRulesDefList = new RuleDefinitionList();		
		RuleDefinitionList matchedRules = ruleMatchMgr.match(querydef, 0,10, candidateRules, matchedRulesDefList);

		System.out.println(matchedRules.size());

		String expected = "";

		// iterate over the matched rules
		Iterator<RuleDefinition> ruleItr = matchedRules.iterator();
		while (ruleItr.hasNext()) {
			RuleDefinition rule = ruleItr.next();
			System.out.println(
					"document criteria "+criteria+" matched rule "+
							rule.getName()+" with metadata "+rule.getMetadata()
					);
			expected = expected + rule.getName() + " - " + rule.getMetadata() + " | ";
		}

		System.out.println(expected);
		assertTrue("incorrect rules", expected.contains("RULE-TEST-1 - {rule-number=one}")&& expected.contains("RULE-TEST-2 - {rule-number=two}"));

		// release client
		client.release();	
	}
	
	@Test	
	public void testRMMatchAsWithCandidates() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{
		System.out.println("Running testRMMatchAsWithCandidates");		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String ruleName1 = "RULE-TEST-1";
		String ruleName2 = "RULE-TEST-2";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames) {
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

		// create a manager for matching rules
		RuleManager ruleMatchMgr = client.newRuleManager();

		// match the rules against the documents qualified by the criteria
		RuleDefinitionList matchedRulesDefList = new RuleDefinitionList();
		
		InputStream inputStreamMatch = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/data/constraint1.xml");
		RuleDefinitionList matchedRules = ruleMatchMgr.matchAs(inputStreamMatch, new String[] {"RULE-TEST-1","RULE-TEST-2"}, matchedRulesDefList);		

		System.out.println(matchedRules.size());

		String expected = "";

		// iterate over the matched rules
		Iterator<RuleDefinition> ruleItr = matchedRules.iterator();
		while (ruleItr.hasNext()) {
			RuleDefinition rule = ruleItr.next();
			System.out.println(
					"document criteria matched rule "+
							rule.getName()+" with metadata "+rule.getMetadata()
					);
			expected = expected + rule.getName() + " - " + rule.getMetadata() + " | ";
		}

		System.out.println(expected);
		assertTrue("incorrect rules", expected.contains("RULE-TEST-1 - {rule-number=one}")&& expected.contains("RULE-TEST-2 - {rule-number=two}"));

		// release client
		client.release();	
	}
	
	@Test
	public void testRuleManagerMatchAs() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{
		System.out.println("Running testRuleManagerMatchAs");		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String[] rules = new String[] {"RULE-TEST-1","RULE-TEST-2"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);		

		// write docs
		for(String filename : filenames) {
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

		// create a manager for document search criteria
		QueryManager queryMgr = client.newQueryManager();

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
					"document criteria matched rule "+
							rule.getName()+" with metadata "+rule.getMetadata()
					);
			expected = expected + rule.getName() + " - " + rule.getMetadata() + " | ";
		}
		System.out.println(expected);
		assertTrue("incorrect rules", expected.contains("RULE-TEST-1 - {rule-number=one}")&& expected.contains("RULE-TEST-2 - {rule-number=two}"));

		// release client
		client.release();	
	}
	
	// Test to validate that addAs with a java.io.object in DocumentWriteSet writes the document.
	@Test
	public void testAddAs() throws Exception {

		System.out.println("Running testAddAs");

		String[] docId = {"aggr1.xml", "aggr2.xml", "aggr3.xml"};
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", Uberport, UberdbName, "eval-user", "x", Authentication.DIGEST);
		Transaction transaction = client.openTransaction();

		try {
			TextDocumentManager docMgr = client.newTextDocumentManager();
			docMgr.setMetadataCategories(Metadata.ALL);
			DocumentWriteSet writeset = docMgr.newWriteSet();

			DocumentMetadataHandle mhRead = new DocumentMetadataHandle();		
			InputStream inputStream1 = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/data/" + docId[0]);
			InputStream inputStream2 = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/data/" + docId[1]);
			InputStream inputStream3 = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/data/" + docId[2]);
			writeset.addAs(docId[0], inputStream1);
			writeset.addAs(docId[1], inputStream2);
			writeset.addAs(docId[2], inputStream3);

			docMgr.write(writeset, transaction);
			StringHandle wrteTransHandle = new StringHandle();
			transaction.readStatus(wrteTransHandle);
			assertTrue("Transaction readStatus during write does not contain Database name. ", (wrteTransHandle.get()).contains(UberrestServerName));
			assertTrue("Transaction readStatus during write does not contain Database name. ", (wrteTransHandle.get()).contains("App-Services"));
			transaction.commit();

			transaction = client.openTransaction();
			String txId = transaction.getTransactionId();

			DocumentPage page = docMgr.read(transaction, docId[0], docId[1], docId[2]);
			assertTrue("DocumentPage Size did not return expected value returned==  "+page.size(), page.size() == 3 );
			// Read back the doc contents to make sure that write succeeded.
			String strDocContent1 = docMgr.read(docId[0], new StringHandle()).get();
			assertTrue("Text document write difference", strDocContent1.contains("Vannevar Bush wrote an article for The Atlantic Monthly"));
			String strDocContent2 = docMgr.read(docId[1], new StringHandle()).get();
			assertTrue("Text document write difference", strDocContent2.contains("The Bush article described a device called a Memex."));
			String strDocContent3 = docMgr.read(docId[2], new StringHandle()).get();
			assertTrue("Text document write difference", strDocContent3.contains("For 1945, the thoughts expressed in The Atlantic Monthly were groundbreaking."));

			StringHandle readTransHandle = new StringHandle();
			transaction.readStatus(readTransHandle);
			assertTrue("Transaction readStatus during read does not contain App Server name. ", (readTransHandle.get()).contains(UberrestServerName));
			assertTrue("Transaction readStatus during read does not contain Database name. ", (readTransHandle.get()).contains(dbName));
			assertTrue("Transaction readStatus during read does not contain Transaction Id name. ", (readTransHandle.get()).contains(txId));
		}
		catch(Exception exp) {
			System.out.println(exp.getMessage());
			throw exp;
		}
		finally {
			transaction.rollback();
		}
	}
						
	@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		
		setAuthentication("digest",restServerName);
		setDefaultUser("nobody",restServerName);				
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		
		deleteRESTUser("eval-user");
		deleteUserRole("test-eval");
		deleteDB(UberdbName);
		deleteForest(UberfNames[0]);
	}
}
