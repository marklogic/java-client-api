package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;

import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.alerting.RuleDefinition;
import com.marklogic.client.alerting.RuleDefinitionList;
import com.marklogic.client.alerting.RuleManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
public class TestRawAlert extends BasicJavaClientREST {

	private static String dbName = "TestRawAlertDB";
	private static String [] fNames = {"TestRawAlertDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

@BeforeClass	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0],  restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testRawAlert() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawAlert");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
		}
		
		// create a manager for configuring rules
		RuleManager ruleMgr = client.newRuleManager();
				
		// get the rule
        File file = new File("src/junit/com/marklogic/javaclient/rules/alertRule1.xml");
		
		// create a handle for the rule
        FileHandle writeHandle = new FileHandle(file);
        
		// write the rule to the database
		ruleMgr.writeRule("RULE-TEST-1", writeHandle);
		
		// create a manager for document search criteria
		QueryManager queryMgr = client.newQueryManager();
		
		// specify the search criteria for the documents
		String criteria = "atlantic";
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		querydef.setCriteria(criteria);
		
		// create a manager for matching rules
		RuleManager ruleMatchMgr = client.newRuleManager();

        // match the rules against the documents qualified by the criteria
		RuleDefinitionList matchedRules = ruleMatchMgr.match(querydef, new RuleDefinitionList());
		
		System.out.println(matchedRules.size());
		
		String expected = "";
		
        // iterate over the matched rules
		Iterator<RuleDefinition> ruleItr = matchedRules.iterator();
		while (ruleItr.hasNext()) 
		{
			RuleDefinition rule = ruleItr.next();
			System.out.println(
					"document criteria "+criteria+" matched rule "+
					rule.getName()+" with metadata "+rule.getMetadata()
					);
			
			expected = expected + rule.getName() + " - " + rule.getMetadata() + " | ";
		}
		
		System.out.println(expected);
		
		assertTrue("incorrect rule", expected.contains("RULE-TEST-1 - {rule-number=one} |"));
		
		// release client
		client.release();		
	}

	

@SuppressWarnings("deprecation")
@Test	public void testRawAlertUnmatched() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawAlertUnmatched");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
		}
		
		// create a manager for configuring rules
		RuleManager ruleMgr = client.newRuleManager();
				
		// get the rule
        File file = new File("src/junit/com/marklogic/javaclient/rules/alertRule1.xml");
		
		// create a handle for the rule
        FileHandle writeHandle = new FileHandle(file);
        
		// write the rule to the database
		ruleMgr.writeRule("RULE-TEST-1", writeHandle);
		
		// create a manager for document search criteria
		QueryManager queryMgr = client.newQueryManager();
		
		// specify the search criteria for the documents
		String criteria = "Memex"; // test case for unmatched rule
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		querydef.setCriteria(criteria);
		
		// create a manager for matching rules
		RuleManager ruleMatchMgr = client.newRuleManager();

        // match the rules against the documents qualified by the criteria
		RuleDefinitionList matchedRules = ruleMatchMgr.match(querydef, new RuleDefinitionList());
		
		System.out.println(matchedRules.size());
		
		assertEquals("incorrect matching rule", 0, matchedRules.size());
				
		// release client
		client.release();		
	}
	 	

@SuppressWarnings("deprecation")
@Test	public void testRawAlertMultipleRules() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawAlertMultipleRules");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
		}
		
		// create a manager for configuring rules
		RuleManager ruleMgr = client.newRuleManager();
		
		// create handle
        InputStreamHandle ruleHandle1 = new InputStreamHandle();
        InputStreamHandle ruleHandle2 = new InputStreamHandle();

		// get the rule file
		InputStream inputStream1 = new FileInputStream("src/junit/com/marklogic/javaclient/rules/alertRule1.xml");
		InputStream inputStream2 = new FileInputStream("src/junit/com/marklogic/javaclient/rules/alertRule2.xml");
				
		ruleHandle1.set(inputStream1);
		ruleHandle2.set(inputStream2);
        
		// write the rule to the database
		ruleMgr.writeRule("RULE-TEST-1", ruleHandle1);
		ruleMgr.writeRule("RULE-TEST-2", ruleHandle2);
		
		// create a manager for document search criteria
		QueryManager queryMgr = client.newQueryManager();
		
		// specify the search criteria for the documents
		String criteria = "atlantic";
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		querydef.setCriteria(criteria);
		
		// create a manager for matching rules
		RuleManager ruleMatchMgr = client.newRuleManager();

        // match the rules against the documents qualified by the criteria
		RuleDefinitionList matchedRules = ruleMatchMgr.match(querydef, new RuleDefinitionList());
		
		System.out.println(matchedRules.size());
		
		String expected = "";
		
        // iterate over the matched rules
		Iterator<RuleDefinition> ruleItr = matchedRules.iterator();
		while (ruleItr.hasNext()) 
		{
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


@SuppressWarnings("deprecation")
@Test	public void testRawAlertUnmatchingRuleName() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawAlertUnmatchingRuleName");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
		}
		
		// create a manager for configuring rules
		RuleManager ruleMgr = client.newRuleManager();
		
		// create handle
        InputStreamHandle ruleHandle1 = new InputStreamHandle();

		// get the rule file
		InputStream inputStream1 = new FileInputStream("src/junit/com/marklogic/javaclient/rules/alertRule1.xml");
				
		ruleHandle1.set(inputStream1);
        
		String exception = "";
		
		// write the rule to the database
		try
		{
			ruleMgr.writeRule("RULE-TEST-A", ruleHandle1); // test case for non-matching rule name
		} catch(Exception e)
		{
			exception = e.toString();
		}
		
		String expectedException = "Invalid content: If provided, rule name in payload must match rule name in URL";
		
		assertTrue("Exception is not thrown", exception.contains(expectedException));
		
		// release client
		client.release();		
	}
	
	

@SuppressWarnings("deprecation")
@Test	public void testRawAlertJSON() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawAlertJSON");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
		}
		
		// create a manager for configuring rules
		RuleManager ruleMgr = client.newRuleManager();
				
		// get the rule
        File file = new File("src/junit/com/marklogic/javaclient/rules/alertRule1.json");
		
		String ruleInJson = convertFileToString(file);
		
		// create a handle for the rule
		StringHandle ruleHandle = new StringHandle(ruleInJson);
		ruleHandle.setFormat(Format.JSON);
        //FileHandle writeHandle = new FileHandle(file);
        
		// write the rule to the database
		ruleMgr.writeRule("RULE-TEST-1-JSON", ruleHandle);
		
		// create a manager for document search criteria
		QueryManager queryMgr = client.newQueryManager();
		
		// specify the search criteria for the documents
		String criteria = "atlantic";
		StringQueryDefinition querydef = queryMgr.newStringDefinition(); // test case with string def
		querydef.setCriteria(criteria);
		
		// create query def
		//StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(); // tes case with structured query
		//StructuredQueryDefinition termQuery1 = qb.term("Atlantic");
		
		// create a manager for matching rules
		RuleManager ruleMatchMgr = client.newRuleManager();

        // match the rules against the documents qualified by the criteria
		RuleDefinitionList matchedRules = ruleMatchMgr.match(querydef, new RuleDefinitionList());
		
		System.out.println(matchedRules.size());
		
		String expected= "";
		
        // iterate over the matched rules
		Iterator<RuleDefinition> ruleItr = matchedRules.iterator();
		while (ruleItr.hasNext()) 
		{
			RuleDefinition rule = ruleItr.next();
			System.out.println(
					"document criteria matched rule "+
					rule.getName()+" with metadata "+rule.getMetadata()
					);
			expected = expected + rule.getName() + " - " + rule.getMetadata() + " | ";
		}
		
		System.out.println(expected);
		
		assertTrue("rule is not correct", expected.contains("RULE-TEST-1-JSON - {{http://marklogic.com/rest-api}rule-number=one json}"));
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testRawAlertStructuredQuery() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawAlertStructuredQuery");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
		}
		
		// create a manager for configuring rules
		RuleManager ruleMgr = client.newRuleManager();
				
		// get the rule
        File file = new File("src/junit/com/marklogic/javaclient/rules/alertRule1.xml");
				
		// create a handle for the rule
        FileHandle writeHandle = new FileHandle(file);
        
		// write the rule to the database
		ruleMgr.writeRule("RULE-TEST-1", writeHandle);
		
		// create a manager for document search criteria
		QueryManager queryMgr = client.newQueryManager();
		
		// specify the search criteria for the documents
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition termQuery1 = qb.term("Atlantic");
		
		// create a manager for matching rules
		RuleManager ruleMatchMgr = client.newRuleManager();

        // match the rules against the documents qualified by the criteria
		RuleDefinitionList matchedRules = ruleMatchMgr.match(termQuery1, new RuleDefinitionList());
		
		System.out.println(matchedRules.size()); // bug, should return 1
		
		assertEquals("result count is not correct", 1, matchedRules.size());
		
        // iterate over the matched rules
		Iterator<RuleDefinition> ruleItr = matchedRules.iterator();
		while (ruleItr.hasNext()) 
		{
			RuleDefinition rule = ruleItr.next();
			System.out.println(
					"document criteria matched rule "+
					rule.getName()+" with metadata "+rule.getMetadata()
					);
		}
		
		// release client
		client.release();		
	}
		

@SuppressWarnings("deprecation")
@Test	public void testRawAlertStructuredQueryTransform() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawAlertStructuredQueryTransform");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
		}
		
		// create a manager for configuring rules
		RuleManager ruleMgr = client.newRuleManager();
				
		// get the rule
        File file = new File("src/junit/com/marklogic/javaclient/rules/alertRule1.xml");
				
		// create a handle for the rule
        FileHandle writeHandle = new FileHandle(file);
        
		// write the rule to the database
		ruleMgr.writeRule("RULE-TEST-1", writeHandle);
		
		// create a manager for document search criteria
		QueryManager queryMgr = client.newQueryManager();
		
		// specify the search criteria for the documents
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition termQuery1 = qb.term("Atlantic");
		
		// Write the rule in Modules database of Server
		TransformExtensionsManager transformManager= client.newServerConfigManager().newTransformExtensionsManager();
		
		File ruleTransform = new File("src/junit/com/marklogic/javaclient/rules/rule-transform.xqy");
		transformManager.writeXQueryTransform("ruleTransform", new FileHandle(ruleTransform));
		
		// create a manager for matching rules
		RuleManager ruleMatchMgr = client.newRuleManager();	
		
	    ServerTransform transform = new ServerTransform("ruleTransform");
	    RuleDefinitionList matchedRules = ruleMatchMgr.match(termQuery1, 0L, QueryManager.DEFAULT_PAGE_LENGTH, new String[] {}, new RuleDefinitionList(), transform);
        		
		System.out.println(matchedRules.size()); // bug, should return 1
		
		assertEquals("result count is not correct", 1, matchedRules.size());
		
        // iterate over the matched rules
		Iterator<RuleDefinition> ruleItr = matchedRules.iterator();
		while (ruleItr.hasNext()) 
		{
			RuleDefinition rule = ruleItr.next();
			System.out.println(
					"document criteria matched rule "+
					rule.getName()+" with metadata "+rule.getMetadata()
					);
		}
		
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testRawAlertCandidateRules() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawAlertCandidateRules");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
		}
		
		// create a manager for configuring rules
		RuleManager ruleMgr = client.newRuleManager();
						
		// create handle
        InputStreamHandle ruleHandle1 = new InputStreamHandle();
        InputStreamHandle ruleHandle2 = new InputStreamHandle();
        InputStreamHandle ruleHandle3 = new InputStreamHandle();

		// get the rule file
		InputStream inputStream1 = new FileInputStream("src/junit/com/marklogic/javaclient/rules/alertRule1.xml");
		InputStream inputStream2 = new FileInputStream("src/junit/com/marklogic/javaclient/rules/alertRule2.xml");
		InputStream inputStream3 = new FileInputStream("src/junit/com/marklogic/javaclient/rules/alertRule3.xml");
				
		ruleHandle1.set(inputStream1);
		ruleHandle2.set(inputStream2);
		ruleHandle3.set(inputStream3);
        
		// write the rule to the database
		ruleMgr.writeRule("RULE-TEST-1", ruleHandle1);
		ruleMgr.writeRule("RULE-TEST-2", ruleHandle2);
		ruleMgr.writeRule("RULE-TEST-3", ruleHandle3);
		
		// get the json rule
        File file = new File("src/junit/com/marklogic/javaclient/rules/alertRule3.json");
		
		String ruleInJson = convertFileToString(file);
		
		// create a handle for the rule
		StringHandle ruleHandle4 = new StringHandle(ruleInJson);
		ruleHandle4.setFormat(Format.JSON);
        
		// write the rule to the database
		ruleMgr.writeRule("RULE-TEST-3-JSON", ruleHandle4);
		
		// create a manager for document search criteria
		QueryManager queryMgr = client.newQueryManager();
		
		// specify the search criteria for the documents
		String criteria = "memex";
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		querydef.setCriteria(criteria);
		
		// create a manager for matching rules
		RuleManager ruleMatchMgr = client.newRuleManager();

		String[] candidateRules = {"RULE-TEST-1", "RULE-TEST-2", "RULE-TEST-3", "RULE-TEST-3-JSON"};
		
        // match the rules against the documents qualified by the criteria
		RuleDefinitionList matchedRules = ruleMatchMgr.match(querydef, 1,2, candidateRules, new RuleDefinitionList());
		
		System.out.println(matchedRules.size());
		
		String expected = "";
		
        // iterate over the matched rules
		Iterator<RuleDefinition> ruleItr = matchedRules.iterator();
		while (ruleItr.hasNext()) 
		{
			RuleDefinition rule = ruleItr.next();
			System.out.println(
					"document criteria "+criteria+" matched rule "+
					rule.getName()+" with metadata "+rule.getMetadata()
					);
			expected = expected + rule.getName() + " - " + rule.getMetadata() + " | ";
		}
		
		System.out.println(expected);
		
		if(expected.equals("RULE-TEST-3 - {rule-number=three} | RULE-TEST-3-JSON - {{http://marklogic.com/rest-api}rule-number=three json} | "))
		{
			assertTrue("rule is incorrect", expected.contains("RULE-TEST-3 - {rule-number=three} | RULE-TEST-3-JSON - {{http://marklogic.com/rest-api}rule-number=three json}"));
		}
		else if(expected.equals("RULE-TEST-3-JSON - {{http://marklogic.com/rest-api}rule-number=three json} | RULE-TEST-3 - {rule-number=three} | "))
		{
			assertTrue("rule is incorrect", expected.contains("RULE-TEST-3-JSON - {{http://marklogic.com/rest-api}rule-number=three json} | RULE-TEST-3 - {rule-number=three}"));
		}
		else
		{
			assertTrue("there is no matching rule", false);
		}
		
		// release client
		client.release();		
	}


@SuppressWarnings("deprecation")
@Test	public void testRawAlertCandidateRulesUnmatched() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawAlertCandidateRulesUnmatched");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
		}
		
		// create a manager for configuring rules
		RuleManager ruleMgr = client.newRuleManager();
						
		// create handle
        InputStreamHandle ruleHandle1 = new InputStreamHandle();
        InputStreamHandle ruleHandle2 = new InputStreamHandle();
        InputStreamHandle ruleHandle3 = new InputStreamHandle();

		// get the rule file
		InputStream inputStream1 = new FileInputStream("src/junit/com/marklogic/javaclient/rules/alertRule1.xml");
		InputStream inputStream2 = new FileInputStream("src/junit/com/marklogic/javaclient/rules/alertRule2.xml");
		InputStream inputStream3 = new FileInputStream("src/junit/com/marklogic/javaclient/rules/alertRule3.xml");
				
		ruleHandle1.set(inputStream1);
		ruleHandle2.set(inputStream2);
		ruleHandle3.set(inputStream3);
        
		// write the rule to the database
		ruleMgr.writeRule("RULE-TEST-1", ruleHandle1);
		ruleMgr.writeRule("RULE-TEST-2", ruleHandle2);
		ruleMgr.writeRule("RULE-TEST-3", ruleHandle3);
		
		// get the json rule
        File file = new File("src/junit/com/marklogic/javaclient/rules/alertRule3.json");
		
		String ruleInJson = convertFileToString(file);
		
		// create a handle for the rule
		StringHandle ruleHandle4 = new StringHandle(ruleInJson);
		ruleHandle4.setFormat(Format.JSON);
        
		// write the rule to the database
		ruleMgr.writeRule("RULE-TEST-3-JSON", ruleHandle4);
		
		// create a manager for document search criteria
		QueryManager queryMgr = client.newQueryManager();
		
		// specify the search criteria for the documents
		String criteria = "atlantic";
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		querydef.setCriteria(criteria);
		
		// create a manager for matching rules
		RuleManager ruleMatchMgr = client.newRuleManager();

		String[] candidateRules = {"gar", "bar", "foo"};
		
        // match the rules against the documents qualified by the criteria
		
		RuleDefinitionList matchedRules = ruleMatchMgr.match(querydef, 1, 2, candidateRules, new RuleDefinitionList());
		
		System.out.println(matchedRules.size());
		
		assertEquals("match rule is incorrect", 0, matchedRules.size());
				
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testRawAlertDocUris() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawAlertDocUris");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
		}
		
		// create a manager for configuring rules
		RuleManager ruleMgr = client.newRuleManager();
				
		// get the rule
        File file = new File("src/junit/com/marklogic/javaclient/rules/alertRule1.xml");
		
		//String combinedQuery = convertFileToString(file);
		
		// create a handle for the rule
		//StringHandle rawHandle = new StringHandle(combinedQuery);
        FileHandle writeHandle = new FileHandle(file);
        
		// write the rule to the database
		ruleMgr.writeRule("RULE-TEST-1", writeHandle);
				
		// specify the search criteria for the documents
		String[] docUris = {"/raw-alert/constraint1.xml", 
				"/raw-alert/constraint2.xml", 
				"/raw-alert/constraint3.xml", 
				"/raw-alert/constraint4.xml", 
				"/raw-alert/constraint5.xml"};
		
		// create a manager for matching rules
		RuleManager ruleMatchMgr = client.newRuleManager();

        // match the rules against the documents qualified by the criteria
		RuleDefinitionList matchedRules = ruleMatchMgr.match(docUris, new RuleDefinitionList());
		
		System.out.println(matchedRules.size());
		
		String expected = "";
		
        // iterate over the matched rules
		Iterator<RuleDefinition> ruleItr = matchedRules.iterator();
		while (ruleItr.hasNext()) 
		{
			RuleDefinition rule = ruleItr.next();
			System.out.println(
					"document criteria matched rule "+
					rule.getName()+" with metadata "+rule.getMetadata()
					);
			expected = expected + rule.getName() + " - " + rule.getMetadata() + " | ";
		}
		
		System.out.println(expected);
		
		assertTrue("rule is incorrect", expected.contains("RULE-TEST-1 - {rule-number=one}"));
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testRawAlertDocPayload() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawAlertDocPayload");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
						
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
		}
		
		// create a manager for configuring rules
		RuleManager ruleMgr = client.newRuleManager();
				
		// get the rule
        File file = new File("src/junit/com/marklogic/javaclient/rules/alertRule2.xml");
				
		// create a handle for the rule
        FileHandle writeHandle = new FileHandle(file);
        
		// write the rule to the database
		ruleMgr.writeRule("RULE-TEST-2", writeHandle);
				
		String filename = "constraint1.xml";
		
		// get the file
        File doc = new File("src/test/java/com/marklogic/javaclient/data/" + filename);
        String docContent = convertFileToString(doc);
        StringHandle handle = new StringHandle(docContent);
	    
		// create a manager for matching rules
		RuleManager ruleMatchMgr = client.newRuleManager();

        // match the rules against the documents qualified by the criteria
		RuleDefinitionList matchedRules = ruleMatchMgr.match(handle, new RuleDefinitionList());
		
		System.out.println(matchedRules.size());
		
		String expected = "";
		
        // iterate over the matched rules
		Iterator<RuleDefinition> ruleItr = matchedRules.iterator();
		while (ruleItr.hasNext()) 
		{
			RuleDefinition rule = ruleItr.next();
			System.out.println(
					"document criteria matched rule "+
					rule.getName()+" with metadata "+rule.getMetadata()
					);
			expected = expected + rule.getName() + " - " + rule.getMetadata() + " | ";
		}

		System.out.println(expected);
		
		assertTrue("rule is incorrect", expected.contains("RULE-TEST-2 - {rule-number=two}"));

		// release client
		client.release();		
	}


@AfterClass	public static  void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,  restServerName);
	}
}
