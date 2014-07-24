package com.marklogic.javaclient;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.*;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
public class TestRawCombinedQueryGeo extends BasicJavaClientREST {

	private static String dbName = "TestRawCombinedQueryGeoDB";
	private static String [] fNames = {"TestRawCombinedQueryGeoDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

@BeforeClass	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	 setupJavaRESTServer(dbName, fNames[0],restServerName,8011);
	  setupAppServicesGeoConstraint(dbName);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testRawCombinedQueryGeo() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryGeo");

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		loadGeoData();
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOptionGeo.xml");
		
		// create a handle for the search criteria
        FileHandle rawHandle = new FileHandle(file); // bug 21107
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
        RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);
		
		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("karl_kara 12,5 12,5 12 5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testRawCombinedQueryGeoJSON() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryGeoJSON");

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		loadGeoData();
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOptionGeoJSON.json");
        
        String combinedQuery = convertFileToString(file);
		
		// create a handle for the search criteria
		StringHandle rawHandle = new StringHandle(combinedQuery);
		rawHandle.setFormat(Format.JSON);		
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
        RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);
		
		// create result handle
		StringHandle resultsHandle = new StringHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		String resultDoc = resultsHandle.get();
		
		System.out.println(resultDoc);
		
		assertTrue("total document returned is incorrect", resultDoc.contains("\"total\":1"));
		assertTrue("returned doc is incorrect", resultDoc.contains("\"uri\":\"/geo-constraint/geo-constraint1.xml\""));
		assertTrue("matched text is incorrect", resultDoc.contains("\"match-text\":[\"karl_kara 12,5 12,5 12 5\"]"));
			
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testRawCombinedQueryGeoBoxAndWordJSON() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryGeoBoxAndWordJSON");

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		loadGeoData();
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOptionGeoBoxAndWordJSON.json");
        
        String combinedQuery = convertFileToString(file);
		
		// create a handle for the search criteria
		StringHandle rawHandle = new StringHandle(combinedQuery);
		rawHandle.setFormat(Format.JSON);		
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
        RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);
		
		// create result handle
		StringHandle resultsHandle = new StringHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		String resultDoc = resultsHandle.get();
		
		System.out.println(resultDoc);

		assertTrue("total document returned is incorrect", resultDoc.contains("\"total\":1"));
		assertTrue("returned doc is incorrect", resultDoc.contains("\"uri\":\"/geo-constraint/geo-constraint20.xml\""));	
			
		// release client
		client.release();		
	}


@SuppressWarnings("deprecation")
@Test	public void testRawCombinedQueryGeoCircle() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("testRawCombinedQueryGeoCircle");
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		loadGeoData();
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOptionGeoCircle.xml");
		
		// create a handle for the search criteria
        FileHandle rawHandle = new FileHandle(file); // bug 21107
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
        RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);
		
		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("5", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("karl_kara 12,-5 12,-5 12 -5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
		assertXpathEvaluatesTo("jack_kara 11,-5 11,-5 11 -5", "string(//*[local-name()='result'][2]//*[local-name()='match'])", resultDoc);
		assertXpathEvaluatesTo("karl_jill 12,-4 12,-4 12 -4", "string(//*[local-name()='result'][3]//*[local-name()='match'])", resultDoc);
		assertXpathEvaluatesTo("bill_kara 13,-5 13,-5 13 -5", "string(//*[local-name()='result'][4]//*[local-name()='match'])", resultDoc);
		assertXpathEvaluatesTo("karl_gale 12,-6 12,-6 12 -6", "string(//*[local-name()='result'][5]//*[local-name()='match'])", resultDoc);
		
		// release client
		client.release();		
	}


@SuppressWarnings("deprecation")
@Test	public void testRawCombinedQueryGeoBox() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("testRawCombinedQueryGeoBox");

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		loadGeoData();
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOptionGeoBox.xml");
		
		// create a handle for the search criteria
        FileHandle rawHandle = new FileHandle(file); // bug 21107
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
        RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);
		
		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("karl_kara 12,-5 12,-5 12 -5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
		assertXpathEvaluatesTo("jack_kara 11,-5 11,-5 11 -5", "string(//*[local-name()='result'][2]//*[local-name()='match'])", resultDoc);
		assertXpathEvaluatesTo("karl_jill 12,-4 12,-4 12 -4", "string(//*[local-name()='result'][3]//*[local-name()='match'])", resultDoc);
		
		// release client
		client.release();		
	}


@SuppressWarnings("deprecation")
@Test	public void testRawCombinedQueryGeoBoxAndWord() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryGeoBoxAndWord");

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		loadGeoData();
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOptionGeoBoxAndWord.xml");
		
		// create a handle for the search criteria
        FileHandle rawHandle = new FileHandle(file); // bug 21107
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
        RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);
		
		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/geo-constraint/geo-constraint20.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testRawCombinedQueryGeoPointAndWord() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryGeoPointAndWord");

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(int i = 1; i <= 9; i++)
		{
			writeDocumentUsingInputStreamHandle(client, "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
		}
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOptionGeoPointAndWord.xml");
		
		// create a handle for the search criteria
        FileHandle rawHandle = new FileHandle(file); // bug 21107
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
        RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);
		
		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/geo-constraint/geo-constraint8.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
		
		// release client
		client.release();		
	}
	
@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,  restServerName);
	}
}
