package com.marklogic.javaclient;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.io.DOMHandle;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.*;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;

public class TestAppServicesRangeConstraint extends BasicJavaClientREST {

//	private String serverName = "";
	private static String dbName = "AppServicesRangeConstraintDB";
	private static String [] fNames = {"AppServicesRangeConstraintDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
//	  super.setUp();
//	  serverName = getConnectedServerName();
	  setupJavaRESTServer(dbName, fNames[0],  restServerName,8011);
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
	public void testWithWordSearch() throws IOException, ParserConfigurationException, SAXException, XpathException
	{	
		System.out.println("Running testWithWordSearch");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "rangeConstraintWithWordSearchOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/range-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("date:2006-02-02 OR policymaker");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("2008-04-04", "string(//*[local-name()='result'][1]//*[local-name()='date'])", resultDoc);
		assertXpathEvaluatesTo("2006-02-02", "string(//*[local-name()='result'][2]//*[local-name()='date'])", resultDoc);
		assertXpathEvaluatesTo("Vannevar served as a prominent policymaker and public intellectual.", "string(//*[local-name()='result'][1]//*[local-name()='p'])", resultDoc);
		assertXpathEvaluatesTo("The Bush article described a device called a Memex.", "string(//*[local-name()='result'][2]//*[local-name()='p'])", resultDoc);
	    
		String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:element-range-query(fn:QName(\"http://purl.org/dc/elements/1.1/\",\"date\"), \"=\", xs:date(\"2006-02-02\"), (), 1), cts:word-query(\"policymaker\", (\"lang=en\"), 1))), (\"score-logtfidf\"), 1))[1 to 10]";
		
		assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}

	/*public void testNegativeWithoutIndexSettings() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testNegativeWithoutIndexSettings");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "rangeConstraintNegativeWithoutIndexSettingsOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/range-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("title:Bush");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		
		String exception = "";
		
		// run search
		try
		{
			queryMgr.search(querydef, resultsHandle);
		} 
		catch (Exception e) { exception = e.toString(); }
		
		String expectedException = "com.marklogic.client.FailedRequestException: Local message: search failed: Internal Server ErrorServer Message: XDMP-ELEMRIDXNOTFOUND";
		
		//assertEquals("Wrong exception", expectedException, exception);
	    boolean exceptionIsThrown = exception.contains(expectedException);
	    assertTrue("Exception is not thrown", exceptionIsThrown);
		
		// release client
		client.release();		
	}*/

@SuppressWarnings("deprecation")
@Test
	public void testNegativeTypeMismatch() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testNegativeTypeMismatch");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "rangeConstraintNegativeTypeMismatchOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/range-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("date:2006-02-02");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		
		String exception = "";
		
		// run search
		try
		{
			queryMgr.search(querydef, resultsHandle);
		} 
		catch (Exception e) { exception = e.toString(); }
		
		String expectedException = "com.marklogic.client.FailedRequestException: Local message: search failed: Bad Request. Server Message: XDMP-ELEMRIDXNOTFOUND";
		
		//assertEquals("Wrong exception", expectedException, exception);
		boolean exceptionIsThrown = exception.contains(expectedException);
	    assertTrue("Exception is not thrown", exceptionIsThrown);
		
		// release client
		client.release();		
	}
@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
//		super.tearDown();
	}
}
