package com.marklogic.javaclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.document.DocumentMetadataPatchBuilder;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import com.sun.jersey.api.client.ClientHandlerException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.*;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.junit.*;
public class TestQueryByExample extends BasicJavaClientREST {

	private static String dbName = "TestQueryByExampleDB";
	private static String [] fNames = {"TestQueryByExampleDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}

@SuppressWarnings("deprecation")
@Test
	public void testQueryByExampleXML() throws IOException, TransformerException, XpathException
	{	
		System.out.println("Running testQueryByExampleXML");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "XML");
		}
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/qbe/qbe1.xml");
        
        String qbeQuery = convertFileToString(file);
        StringHandle qbeHandle = new StringHandle(qbeQuery);
        qbeHandle.setFormat(Format.XML);
        
        QueryManager queryMgr = client.newQueryManager();
        
        RawQueryByExampleDefinition qbyex = queryMgr.newRawQueryByExampleDefinition(qbeHandle);
        
        Document resultDoc = queryMgr.search(qbyex, new DOMHandle()).get();
        
        System.out.println("XML Result"+convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);

		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testQueryByExampleXMLnew() throws IOException, TransformerException, XpathException
	{	
		System.out.println("Running testQueryByExampleXML");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "XML");
		}
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/qbe/qbe1.xml");
        
        String qbeQuery = convertFileToString(file);
        StringHandle qbeHandle = new StringHandle(qbeQuery);
        qbeHandle.setFormat(Format.XML);
        
        
        QueryManager queryMgr = client.newQueryManager();
        
        RawQueryByExampleDefinition qbyex = queryMgr.newRawQueryByExampleDefinition(qbeHandle);
        Document resultDoc = queryMgr.search(qbyex, new DOMHandle()).get();
        
        System.out.println("XML Result"+convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);

		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testQueryByExampleJSON() throws IOException
	{	
		System.out.println("Running testQueryByExampleJSON");
		
		String[] filenames = {"constraint1.json", "constraint2.json", "constraint3.json", "constraint4.json", "constraint5.json"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "JSON");
		}
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/qbe/qbe1.json");
        
        String qbeQuery = convertFileToString(file);
        StringHandle qbeHandle = new StringHandle(qbeQuery);
        qbeHandle.setFormat(Format.JSON);
        
        QueryManager queryMgr = client.newQueryManager();
        
        RawQueryByExampleDefinition qbyex = queryMgr.newRawQueryByExampleDefinition(qbeHandle);
        
        String resultDoc = queryMgr.search(qbyex, new StringHandle()).get();
        
        System.out.println(resultDoc);

        assertTrue("total result is not correct", resultDoc.contains("\"total\":1"));
        assertTrue("doc returned is not correct", resultDoc.contains("{\"snippet-format\":\"snippet\",\"total\":1,\"start\":1,\"page-length\":10,\"results\":[{\"index\":1,\"uri\":\"/qbe/constraint1.json\",\"path\":\"fn:doc(\\\"/qbe/constraint1.json\\\")\",\"score\":28672,\"confidence\":0.6951694,\"fitness\":0.6951694,\"href\":\"/v1/documents?uri=%2Fqbe%2Fconstraint1.json\",\"mimetype\":\"application/json\",\"format\":\"json\",\"matches\":[{\"path\":\"fn:doc(\\\"/qbe/constraint1.json\\\")/*:json/*:root/*:id\",\"match-text\":[{\"highlight\":11}]}],\"metadata\":[{\"title\":\"Vannevar Bush\",\"metadata-type\":\"element\"},{\"id\":11,\"metadata-type\":\"element\"},{\"p\":\"Vannevar Bush wrote an article for The Atlantic Monthly\",\"metadata-type\":\"element\"},{\"popularity\":5,\"metadata-type\":\"element\"}]}]"));
        
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testBug22179() throws IOException
	{	
		System.out.println("Running testBug22179");
		
		String[] filenames = {"constraint1.json", "constraint2.json", "constraint3.json", "constraint4.json", "constraint5.json"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "JSON");
		}
		
		  ServerConfigurationManager confMgr = client.newServerConfigManager();
		  confMgr.setQueryValidation(true);
		
		  String combinedCriteria ="{\"search\":{\"options\":{\"constraint\":[{\"name\":\"para\", \"word\":{\"term-option\":[\"case-insensitive\"], \"field\":{\"name\":\"para\"}}},{\"name\":\"id\", \"value\":{\"element\":{\"ns\":\"\", \"name\":\"id\"}}}], \"return-metrics\":false, \"debug\":true, \"return-qtext\":false, \"transform-results\":{\"apply\":\"snippet\"}}, \"query\":{\"queries\":[{\"or-query\":{\"queries\":[{\"and-query\":{\"queries\":[{\"word-constraint-query\":{\"text\":[\"Bush\"], \"constraint-name\":\"para\"}},{\"not-query\":{\"word-constraint-query\":{\"text\":[\"memex\"], \"constraint-name\":\"para\"}}}]}},{\"and-query\":{\"queries\":[{\"value-constraint-query\":{\"text\":[\"0026\"], \"constraint-name\":\"id\"}},{\"term-query\":{\"text\":[\"memex\"]}}]}}]}}]}}}";
		  QueryManager queryMgr = client.newQueryManager();
		   
		  StringHandle combinedHandle = new StringHandle(combinedCriteria).withFormat(Format.JSON);
		  RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(combinedHandle);
		  String output = queryMgr.search(querydef, new StringHandle()).get();
		  System.out.println(output);
		  assertTrue(output.contains("(cts:search(fn:collection(), cts:or-query((cts:and-query((cts:field-word-query"));
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testQueryByExampleXMLPayload() throws IOException, TransformerException, XpathException
	{	
		System.out.println("Running testQueryByExampleXMLPayload");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "XML");
		}
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/qbe/qbe1.xml");
        FileHandle fileHandle = new FileHandle(file);
        QueryManager queryMgr = client.newQueryManager();
        
        RawQueryByExampleDefinition rw = queryMgr.newRawQueryByExampleDefinition(fileHandle.withFormat(Format.XML));
        SearchHandle results = queryMgr.search(rw, new SearchHandle());
        
        for (MatchDocumentSummary result : results.getMatchResults()) 
		{
			System.out.println(result.getUri()+ ": Uri");
			assertEquals("Wrong Document Searched",result.getUri() , "/qbe/constraint1.xml");
		} 
		
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testQueryByExampleJSONPayload() throws IOException, Exception
	{	
		System.out.println("Running testQueryByExampleJSONPayload");
		
		String[] filenames = {"constraint1.json", "constraint2.json", "constraint3.json", "constraint4.json", "constraint5.json"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "JSON");
		}
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/qbe/qbe1.json");
        FileHandle fileHandle = new FileHandle(file);

        QueryManager queryMgr = client.newQueryManager();
        RawQueryByExampleDefinition qbyex = queryMgr.newRawQueryByExampleDefinition(fileHandle.withFormat(Format.JSON));
        String resultDoc = queryMgr.search(qbyex, new StringHandle()).get();
        System.out.println(resultDoc);
        assertTrue("Result is not proper", resultDoc.contains("/qbe/constraint1.json"));
        
        
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testQueryByExampleXMLPermission() throws IOException, TransformerException, XpathException
	{	
		System.out.println("Running testQueryByExampleXMLPermission");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "XML");
		}
		
		// get the combined query
		try{
        File file = new File("src/junit/com/marklogic/javaclient/qbe/qbe2.xml");
     
        FileHandle fileHandle = new FileHandle(file);
        QueryManager queryMgr = client.newQueryManager();
        
        RawQueryByExampleDefinition rw = queryMgr.newRawQueryByExampleDefinition(fileHandle.withFormat(Format.XML));
        SearchHandle results = queryMgr.search(rw, new SearchHandle());
        
        for (MatchDocumentSummary result : results.getMatchResults()) 
		{
			System.out.println(result.getUri()+ ": Uri");
			assertEquals("Wrong Document Searched",result.getUri() , "/qbe/constraint1.xml");
		} 
		}catch(ClientHandlerException e){
			System.out.println("Negative Test Passed of executing nonreadable file");			
		}
		// release client
		client.release();		
		
	}

@SuppressWarnings("deprecation")
@Test	public void testQueryByExampleWrongXML() throws IOException, TransformerException, XpathException
	{	
		System.out.println("Running testQueryByExampleXMLPayload");
		
		String filename = "WrongFormat.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		try{
			// write docs
			writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "XML");
			
			
			// get the combined query
        	File file = new File("src/junit/com/marklogic/javaclient/qbe/qbe1.xml");
        	FileHandle fileHandle = new FileHandle(file);
        	QueryManager queryMgr = client.newQueryManager();
        	
        	RawQueryByExampleDefinition rw = queryMgr.newRawQueryByExampleDefinition(fileHandle.withFormat(Format.XML));
        	SearchHandle results = queryMgr.search(rw, new SearchHandle());
        	
        	for (MatchDocumentSummary result : results.getMatchResults()) 
			{
				System.out.println(result.getUri()+ ": Uri");
			//	assertEquals("Wrong Document Searched",result.getUri() , "/qbe/constraint1.xml");
			} 
		}catch(FailedRequestException e){
				System.out.println("Negative test passed as XML with invalid structure gave FailedRequestException ");
			}
		
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testQueryByExampleWrongJSON() throws IOException
	{	
		System.out.println("Running testQueryByExampleJSON");
		
		String filename = "WrongFormat.json";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		try{	
		// write docs
			writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "JSON");
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/qbe/qbe1.json");
        
        String qbeQuery = convertFileToString(file);
        StringHandle qbeHandle = new StringHandle(qbeQuery);
        qbeHandle.setFormat(Format.JSON);
        
        QueryManager queryMgr = client.newQueryManager();
        
        RawQueryByExampleDefinition qbyex = queryMgr.newRawQueryByExampleDefinition(qbeHandle);
        
        String resultDoc = queryMgr.search(qbyex, new StringHandle()).get();
        
        System.out.println(resultDoc);

        assertTrue("total result is not correct", resultDoc.contains("\"total\":1"));
        assertTrue("doc returned is not correct", resultDoc.contains("\"metadata\":[{\"title\":\"Vannevar Bush\"},{\"id\":11},{\"p\":\"Vannevar Bush wrote an article for The Atlantic Monthly\"},{\"popularity\":5}]"));
		}catch(FailedRequestException e){
			System.out.println("Negative test passed as JSON with invalid structure gave FailedRequestException ");
		}
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testQueryByExampleXMLWrongQuery() throws IOException, TransformerException, XpathException
	{	
		System.out.println("Running testQueryByExampleXMLWrongQuery");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "XML");
		}
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/qbe/qbe1.xml");
        FileHandle fileHandle = new FileHandle(file);
        QueryManager queryMgr = client.newQueryManager();
        
        RawQueryByExampleDefinition rw = queryMgr.newRawQueryByExampleDefinition(fileHandle.withFormat(Format.XML));
        SearchHandle results = queryMgr.search(rw, new SearchHandle());
        
        for (MatchDocumentSummary result : results.getMatchResults()) 
		{
			System.out.println(result.getUri()+ ": Uri");
			assertEquals("Wrong Document Searched",result.getUri() , "/qbe/constraint1.xml");
		} 
        try{
        File wrongFile = new File("src/junit/com/marklogic/javaclient/qbe/WrongQbe.xml");
        FileHandle wrongFileHandle = new FileHandle(wrongFile);
        QueryManager newQueryMgr = client.newQueryManager();
        
        RawQueryByExampleDefinition newRw = newQueryMgr.newRawQueryByExampleDefinition(wrongFileHandle.withFormat(Format.XML));
        SearchHandle newResults = queryMgr.search(newRw, new SearchHandle());
        
        for (MatchDocumentSummary result : newResults.getMatchResults()) 
		{
			System.out.println(result.getUri()+ ": Uri");
			assertEquals("Wrong Document Searched",result.getUri() , "/qbe/constraint1.xml");
		} 
        }catch(FailedRequestException e){
        	System.out.println("Negative test passed as Query with improper Xml format gave FailedRequestException ");	
        }
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testQueryByExampleJSONWrongQuery() throws IOException
	{	
		System.out.println("Running testQueryByExampleJSONWrongQuery");
		
		String[] filenames = {"constraint1.json", "constraint2.json", "constraint3.json", "constraint4.json", "constraint5.json"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "JSON");
		}
		
		// get the Correct query
        File file = new File("src/junit/com/marklogic/javaclient/qbe/qbe1.json");
        
        String qbeQuery = convertFileToString(file);
        StringHandle qbeHandle = new StringHandle(qbeQuery);
        qbeHandle.setFormat(Format.JSON);
        
        QueryManager queryMgr = client.newQueryManager();
        
        RawQueryByExampleDefinition qbyex = queryMgr.newRawQueryByExampleDefinition(qbeHandle);
        
        String resultDoc = queryMgr.search(qbyex, new StringHandle()).get();
        
        System.out.println("Result of Correct Query"+ resultDoc);

        //assertTrue("total result is not correct", resultDoc.contains("\"total\":1"));
      //  assertTrue("doc returned is not correct", resultDoc.contains("\"metadata\":[{\"title\":\"Vannevar Bush\"},{\"id\":11},{\"p\":\"Vannevar Bush wrote an article for The Atlantic Monthly\"},{\"popularity\":5}]"));
        
     // get the query with Wrong Format
        
        File wrongFile = new File("src/junit/com/marklogic/javaclient/qbe/WrongQbe.json");
        
        String wrongQbeQuery = convertFileToString(wrongFile);
        StringHandle newQbeHandle = new StringHandle(wrongQbeQuery);
        newQbeHandle.setFormat(Format.JSON);
        
        QueryManager newQueryMgr = client.newQueryManager();
        
        RawQueryByExampleDefinition newQbyex = newQueryMgr.newRawQueryByExampleDefinition(newQbeHandle);
        try{
        String newResultDoc = newQueryMgr.search(newQbyex, new StringHandle()).get();
        
        System.out.println("Result of Wrong Query"+newResultDoc);

        assertTrue("total result is not correct", resultDoc.contains("\"total\":1"));
        assertTrue("doc returned is not correct", resultDoc.contains("\"metadata\":[{\"title\":\"Vannevar Bush\"},{\"id\":11},{\"p\":\"Vannevar Bush wrote an article for The Atlantic Monthly\"},{\"popularity\":5}]"));
        }catch(FailedRequestException e){
        	System.out.println("Negative test passed as Query with improper JSON format gave FailedRequestException ");
        }
		// release client
		client.release();		
	}
@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}
}
