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
import static org.junit.Assert.*;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;

public class TestAppServicesGeoElementConstraint extends BasicJavaClientREST {

//	private String serverName = "";
	private static String dbName = "AppServicesGeoConstraintDB";
	private static String [] fNames = {"AppServicesGeoConstraintDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
//	  super.setUp();
//	  serverName = getConnectedServerName();
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesGeoConstraint(dbName);
	}
@After
public  void testCleanUp() throws Exception
{
	clearDB(8011);
	System.out.println("Running clear script");
}

@Test
	public void testPointPositiveLangLat() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testPointPositiveLangLat");
		
		String queryOptionName = "geoConstraintOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(int i = 1; i <= 7; i++)
		{
			writeDocumentUsingInputStreamHandle(client, "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("geo-elem:\"12,5\"");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("karl_kara 12,5 12,5 12 5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
		
		// release client
		client.release();		
	}

@Test
	public void testPointNegativeLangLat() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testPointNegativeLangLat");
		
		String queryOptionName = "geoConstraintOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(int i = 1; i <= 7; i++)
		{
			writeDocumentUsingInputStreamHandle(client, "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("geo-elem:\"-12,-5\"");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("karl_kara -12,-5 -12,-5 -12 -5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
		
		// release client
		client.release();		
	}


@Test
	public void testNegativePointInvalidValue() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testNegativePointInvalidValue");
		
		String queryOptionName = "geoConstraintOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(int i = 1; i <= 7; i++)
		{
			writeDocumentUsingInputStreamHandle(client, "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("geo-elem:\"12,A\"");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		
		String result = "";
		
		try
		{
			queryMgr.search(querydef, resultsHandle);
			Document resultDoc = resultsHandle.get();
			result = convertXMLDocumentToString(resultDoc).toString();
			System.out.println("Result : "+result);
		} catch (Exception e) { e.toString(); }
		
		assertTrue("Expected Warning message is not thrown", result.contains("<search:warning id=\"SEARCH-IGNOREDQTEXT\">[Invalid text, cannot parse geospatial point from '12,A'.]</search:warning>"));
		
				
		// release client
		client.release();		
	}


@Test	
	public void testCirclePositiveLangLat() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("testCirclePositiveLangLat");
		
		String queryOptionName = "geoConstraintOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(int i = 1; i <= 7; i++)
		{
			writeDocumentUsingInputStreamHandle(client, "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("geo-elem:\"@70 12,5\"");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("5", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("karl_kara 12,5 12,5 12 5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
		assertXpathEvaluatesTo("bill_kara 13,5 13,5 13 5", "string(//*[local-name()='result'][2]//*[local-name()='match'])", resultDoc);
		assertXpathEvaluatesTo("karl_gale 12,6 12,6 12 6", "string(//*[local-name()='result'][3]//*[local-name()='match'])", resultDoc);
		assertXpathEvaluatesTo("jack_kara 11,5 11,5 11 5", "string(//*[local-name()='result'][4]//*[local-name()='match'])", resultDoc);
		assertXpathEvaluatesTo("karl_jill 12,4 12,4 12 4", "string(//*[local-name()='result'][5]//*[local-name()='match'])", resultDoc);
		
		// release client
		client.release();		
	}

@Test
	public void testBoxPositiveLangLat() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("testBoxPositiveLangLat");
		
		String queryOptionName = "geoConstraintOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(int i = 1; i <= 7; i++)
		{
			writeDocumentUsingInputStreamHandle(client, "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("geo-elem:\"[11,4,12,5]\"");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("4", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("karl_kara 12,5 12,5 12 5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
		assertXpathEvaluatesTo("jack_jill 11,4 11,4 11 4", "string(//*[local-name()='result'][2]//*[local-name()='match'])", resultDoc);
		assertXpathEvaluatesTo("jack_kara 11,5 11,5 11 5", "string(//*[local-name()='result'][3]//*[local-name()='match'])", resultDoc);
		assertXpathEvaluatesTo("karl_jill 12,4 12,4 12 4", "string(//*[local-name()='result'][4]//*[local-name()='match'])", resultDoc);
		
		// release client
		client.release();		
	}

@Test
	public void testPointAndWord() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testPointAndWord");
		
		String queryOptionName = "geoConstraintOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(int i = 1; i <= 9; i++)
		{
			writeDocumentUsingInputStreamHandle(client, "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("geo-elem:\"150,-140\" AND john");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/geo-constraint/geo-constraint8.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
		
		// release client
		client.release();		
	}
	@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,  restServerName);
//		super.tearDown();
	}
}
