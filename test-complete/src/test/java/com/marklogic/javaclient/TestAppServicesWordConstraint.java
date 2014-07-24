package com.marklogic.javaclient;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import com.marklogic.client.query.QueryManager;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.io.DOMHandle;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
public class TestAppServicesWordConstraint extends BasicJavaClientREST {

	private static String dbName = "AppServicesWordConstraintDB";
	private static String [] fNames = {"AppServicesWordConstraintDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0],restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}
@After
public  void testCleanUp() throws Exception
{
	clearDB(8011);
	System.out.println("Running clear script");
}
@SuppressWarnings("deprecation")
@Test
	public void testWithElementAndAttributeIndex() throws IOException, ParserConfigurationException, SAXException, XpathException
	{	
		System.out.println("Running testWithElementAndAttributeIndex");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "wordConstraintWithElementAndAttributeIndexOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/word-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("intitle:1945 OR inprice:12");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("For 1945", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("The Bush article", "string(//*[local-name()='result'][2]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("Vannevar served", "string(//*[local-name()='result'][3]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("1.23", "string(//*[local-name()='result'][1]//@*[local-name()='amt'])", resultDoc);
		assertXpathEvaluatesTo("0.12", "string(//*[local-name()='result'][2]//@*[local-name()='amt'])", resultDoc);
		assertXpathEvaluatesTo("12.34", "string(//*[local-name()='result'][3]//@*[local-name()='amt'])", resultDoc);
	    
		//String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:element-range-query(fn:QName(\"http://purl.org/dc/elements/1.1/\", \"date\"), \"=\", xs:date(\"2006-02-02\"), (), 1), cts:word-query(\"policymaker\", (\"lang=en\"), 1))), (\"score-logtfidf\"), 1))[1 to 10]";
		
		//assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test
	public void testWithNormalWordQuery() throws IOException, ParserConfigurationException, SAXException, XpathException
	{	
		System.out.println("Running testWithNormalWordQuery");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "wordConstraintWithNormalWordQueryOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/word-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("Memex  OR inprice:.12");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("The Bush article", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("The memex", "string(//*[local-name()='result'][2]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("0.12", "string(//*[local-name()='result'][1]//@*[local-name()='amt'])", resultDoc);
		assertXpathEvaluatesTo("123.45", "string(//*[local-name()='result'][2]//@*[local-name()='amt'])", resultDoc);
	    
		String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:word-query(\"Memex\", (\"lang=en\"), 1), cts:element-attribute-word-query(fn:QName(\"http://cloudbank.com\", \"price\"), fn:QName(\"\", \"amt\"), \".12\", (\"lang=en\"), 1))), (\"score-logtfidf\"), 1))[1 to 10]";
		
		assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test
	public void testWithTermOptionCaseInsensitive() throws IOException, ParserConfigurationException, SAXException, XpathException
	{	
		System.out.println("Running testWithTermOptionCaseInsensitive");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "wordConstraintWithTermOptionCaseInsensitiveOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/word-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("intitle:for  OR price:0.12");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("For 1945", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("The Bush article", "string(//*[local-name()='result'][2]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("1.23", "string(//*[local-name()='result'][1]//@*[local-name()='amt'])", resultDoc);
		assertXpathEvaluatesTo("0.12", "string(//*[local-name()='result'][2]//@*[local-name()='amt'])", resultDoc);
	    
		String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:element-word-query(fn:QName(\"\", \"title\"), \"for\", (\"case-insensitive\",\"lang=en\"), 1), cts:element-attribute-range-query(fn:QName(\"http://cloudbank.com\", \"price\"), fn:QName(\"\", \"amt\"), \"=\", 0.12, (), 1))), (\"score-logtfidf\",\"faceted\"), 1))[1 to 10]";
		
		assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}
@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		
	}
}
