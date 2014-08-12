package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.XMLStreamReaderHandle;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
public class TestBug21183 extends BasicJavaClientREST {

	private static String dbName = "TestBug21183DB";
	private static String [] fNames = {"TestBug21183DB-1"};
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
	public void testBug21183() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testBug21183");
		
		String[] filenames = {"bug21183.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/bug-21183/", "XML");
		}
		
		// set query option
		setQueryOption(client, "bug21183Opt.xml");
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("bug21183Opt.xml");
		querydef.setCriteria("a");
		
		// create result handle
		SearchHandle resultsHandle = queryMgr.search(querydef, new SearchHandle()); 
		
		String resultDoc1 = "";
		
		// get the result
		for (MatchDocumentSummary result : resultsHandle.getMatchResults()) 
		{
			for (Document s : result.getSnippets())
			resultDoc1 = convertXMLDocumentToString(s);	
			System.out.println(resultDoc1);
			//Commenting as per Update from Bug 23788
			//assertTrue("Returned doc from SearchHandle has no namespace", resultDoc1.contains("<test xmlns:myns=\"http://mynamespace.com\" xmlns:search=\"http://marklogic.com/appservices/search\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">"));
			assertTrue("Returned doc from SearchHandle has no attribute", resultDoc1.contains("<txt att=\"1\">a</txt>"));
			System.out.println();
		} 
		
		XMLStreamReaderHandle shandle = queryMgr.search(querydef, new XMLStreamReaderHandle());
		String resultDoc2 = shandle.toString();
		System.out.println(resultDoc2);
		assertTrue("Returned doc from XMLStreamReaderHandle has no namespace", resultDoc2.contains("<test xmlns:myns=\"http://mynamespace.com\">"));
		assertTrue("Returned doc from XMLStreamReaderHandle has no attribute", resultDoc2.contains("<txt att=\"1\">a</txt>"));
			    		
		// release client
		client.release();		
	}
	
	public void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	
	}
}
