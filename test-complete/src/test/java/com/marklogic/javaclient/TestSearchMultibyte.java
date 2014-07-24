package com.marklogic.javaclient;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

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
public class TestSearchMultibyte extends BasicJavaClientREST {

	private static String dbName = "TestSearchMultibyteDB";
	private static String [] fNames = {"TestSearchMultibyteDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

@BeforeClass	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testSearchString() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testSearchString");
		
		String[] filenames = {"multibyte1.xml", "multibyte2.xml", "multibyte3.xml"};
		String queryOptionName = "multibyteSearchOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/multibyte-search/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("mult-title:万里长城");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/multibyte-search/multibyte1.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testSearchStringWithBucket() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testSearchStringWithBucket");
		
		String[] filenames = {"multibyte1.xml", "multibyte2.xml", "multibyte3.xml"};
		String queryOptionName = "multibyteSearchOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/multibyte-search/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("mult-pop:medium");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/multibyte-search/multibyte2.xml", "string(//*[local-name()='result'][1]//@*[local-name()='uri'])", resultDoc);
		assertXpathEvaluatesTo("/multibyte-search/multibyte1.xml", "string(//*[local-name()='result'][2]//@*[local-name()='uri'])", resultDoc);
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testSearchStringWithBucketAndWord() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testSearchStringWithBucketAndWord");
		
		String[] filenames = {"multibyte1.xml", "multibyte2.xml", "multibyte3.xml"};
		String queryOptionName = "multibyteSearchOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/multibyte-search/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("mult-pop:medium AND mult-title:上海");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/multibyte-search/multibyte2.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
		
		// release client
		client.release();		
	}

@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,restServerName);
	}
}