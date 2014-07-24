package com.marklogic.javaclient;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawStructuredQueryDefinition;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.*;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
import org.junit.Assert;
public class TestRawStructuredQuery extends BasicJavaClientREST {

	
	private static String dbName = "TestRawStructuredQueryDB";
	private static String [] fNames = {"TestRawStructuredQueryDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

@BeforeClass	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0],  restServerName,8011);
	  setupAppServicesConstraint(dbName);
	  System.out.println("after setup");
	}
	


@Test	public void testRawStructuredQueryXML() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryXML");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOption.xml");
		
		// create a handle for the search criteria
	    FileHandle rawHandle = new FileHandle(file);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
        RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle);
		
		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
	    		
		// release client
		client.release();		
	}
	


@Test	public void testRawStructuredQueryJSON() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryJSON");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOptionJSON.json");
		
		String combinedQuery = convertFileToString(file);
		
		// create a handle for the search criteria
		StringHandle rawHandle = new StringHandle(combinedQuery);
        //FileHandle rawHandle = new FileHandle(file);
        //rawHandle.setMimetype("application/xml");
		rawHandle.setFormat(Format.JSON);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
		RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle);
		
		// create result handle
		StringHandle resultsHandle = new StringHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		String resultDoc = resultsHandle.get();
		
		System.out.println(resultDoc);
		
		assertTrue("document is not returned", resultDoc.contains("/raw-combined-query/constraint5.xml"));
				
		// release client
		client.release();		
	}
	


@Test	public void testRawStructuredQueryXMLWithOptions() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryXMLWithOptions");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "valueConstraintWithoutIndexSettingsAndNSOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
				
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryNoOption.xml");
		
		// create a handle for the search criteria
        FileHandle rawHandle = new FileHandle(file);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
        RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle, queryOptionName);
		
		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
	    		
		// release client
		client.release();		
	}
	


@Test	public void testRawStructuredQueryJSONWithOverwriteOptions() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryJSONWithOverwriteOptions");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "valueConstraintWithoutIndexSettingsAndNSOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOptionJSONOverwrite.json");
		
		String combinedQuery = convertFileToString(file);
		
		// create a handle for the search criteria
		StringHandle rawHandle = new StringHandle(combinedQuery);
        //FileHandle rawHandle = new FileHandle(file);
        //rawHandle.setMimetype("application/xml");
		rawHandle.setFormat(Format.JSON);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
        RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle, queryOptionName);
		
		// create result handle
		StringHandle resultsHandle = new StringHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		String resultDoc = resultsHandle.get();
		
		System.out.println(resultDoc);
		
		assertTrue("document is not returned", resultDoc.contains("/raw-combined-query/constraint1.xml"));
				
		// release client
		client.release();		
	}
	


@Test	public void testRawStructuredQueryCollection() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryCollection");
		
		String filename1 = "constraint1.xml";
		String filename2 = "constraint2.xml";
		String filename3 = "constraint3.xml";
		String filename4 = "constraint4.xml";
		String filename5 = "constraint5.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);

	    // create and initialize a handle on the metadata
	    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();
		
	    // set the metadata
	    metadataHandle1.getCollections().addAll("http://test.com/set1");
	    metadataHandle1.getCollections().addAll("http://test.com/set5");
	    metadataHandle2.getCollections().addAll("http://test.com/set1");
	    metadataHandle3.getCollections().addAll("http://test.com/set3");
	    metadataHandle4.getCollections().addAll("http://test.com/set3/set3-1");
	    metadataHandle5.getCollections().addAll("http://test.com/set1");
	    metadataHandle5.getCollections().addAll("http://test.com/set5");

	    // write docs
	    writeDocumentUsingInputStreamHandle(client, filename1, "/collection-constraint/", metadataHandle1, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename2, "/collection-constraint/", metadataHandle2, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename3, "/collection-constraint/", metadataHandle3, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename4, "/collection-constraint/", metadataHandle4, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename5, "/collection-constraint/", metadataHandle5, "XML");
	    
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOptionCollection.xml");
		
		// create a handle for the search criteria
        FileHandle rawHandle = new FileHandle(file); // bug 21107
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
		RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle);
		
		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("Vannevar Bush", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
		
		// release client
		client.release();		
	}
	
	


@Test	public void testRawStructuredQueryCombo() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryCombo");
		
		String filename1 = "constraint1.xml";
		String filename2 = "constraint2.xml";
		String filename3 = "constraint3.xml";
		String filename4 = "constraint4.xml";
		String filename5 = "constraint5.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);

	    // create and initialize a handle on the metadata
	    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();
		
	    // set the metadata
	    metadataHandle1.getCollections().addAll("http://test.com/set1");
	    metadataHandle1.getCollections().addAll("http://test.com/set5");
	    metadataHandle2.getCollections().addAll("http://test.com/set1");
	    metadataHandle3.getCollections().addAll("http://test.com/set3");
	    metadataHandle4.getCollections().addAll("http://test.com/set3/set3-1");
	    metadataHandle5.getCollections().addAll("http://test.com/set1");
	    metadataHandle5.getCollections().addAll("http://test.com/set5");

	    // write docs
	    writeDocumentUsingInputStreamHandle(client, filename1, "/collection-constraint/", metadataHandle1, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename2, "/collection-constraint/", metadataHandle2, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename3, "/collection-constraint/", metadataHandle3, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename4, "/collection-constraint/", metadataHandle4, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename5, "/collection-constraint/", metadataHandle5, "XML");
	    
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOptionCombo.xml");
		
		// create a handle for the search criteria
        FileHandle rawHandle = new FileHandle(file); // bug 21107
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
		RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle);
		
		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("Vannevar Bush", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
		
		// release client
		client.release();		
	}
	


@Test	public void testRawStructuredQueryField() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryField");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/field-constraint/", "XML");
		}
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOptionField.xml");
		
		// create a handle for the search criteria
        FileHandle rawHandle = new FileHandle(file); // bug 21107
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
		RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle);
		
		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("memex", "string(//*[local-name()='result'][1]//*[local-name()='match'][1]//*[local-name()='highlight'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='match'][2]//*[local-name()='highlight'])", resultDoc);
		assertXpathEvaluatesTo("Memex", "string(//*[local-name()='result'][1]//*[local-name()='match'][3]//*[local-name()='highlight'])", resultDoc);
		assertXpathEvaluatesTo("Bush", "string(//*[local-name()='result'][2]//*[local-name()='match'][1]//*[local-name()='highlight'])", resultDoc);
			   	
		// release client
		client.release();		
	}
	


@Test	public void testRawStructuredQueryPathIndex() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryPathIndex");
		
		String[] filenames = {"pathindex1.xml", "pathindex2.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/path-index-raw/", "XML");
		}		
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOptionPathIndex.xml");
		
		// create a handle for the search criteria
        FileHandle rawHandle = new FileHandle(file); // bug 21107
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
		RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle);
		
		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/path-index-raw/pathindex2.xml", "string(//*[local-name()='result'][1]//@*[local-name()='uri'])", resultDoc);
		assertXpathEvaluatesTo("/path-index-raw/pathindex1.xml", "string(//*[local-name()='result'][2]//@*[local-name()='uri'])", resultDoc);
			   	
		// release client
		client.release();		
	}
	
	/*public void testRawStructuredQueryComboJSON() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryComboJSON");
		
		String filename1 = "constraint1.xml";
		String filename2 = "constraint2.xml";
		String filename3 = "constraint3.xml";
		String filename4 = "constraint4.xml";
		String filename5 = "constraint5.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);

	    // create and initialize a handle on the metadata
	    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();
		
	    // set the metadata
	    metadataHandle1.getCollections().addAll("http://test.com/set1");
	    metadataHandle1.getCollections().addAll("http://test.com/set5");
	    metadataHandle2.getCollections().addAll("http://test.com/set1");
	    metadataHandle3.getCollections().addAll("http://test.com/set3");
	    metadataHandle4.getCollections().addAll("http://test.com/set3/set3-1");
	    metadataHandle5.getCollections().addAll("http://test.com/set1");
	    metadataHandle5.getCollections().addAll("http://test.com/set5");

	    // write docs
	    writeDocumentUsingInputStreamHandle(client, filename1, "/collection-constraint/", metadataHandle1, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename2, "/collection-constraint/", metadataHandle2, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename3, "/collection-constraint/", metadataHandle3, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename4, "/collection-constraint/", metadataHandle4, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename5, "/collection-constraint/", metadataHandle5, "XML");		

	    // set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
						
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOptionComboJSON.json");
		
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
		
		//assertTrue("document is not returned", resultDoc.contains("/raw-combined-query/constraint5.xml"));
				
		// release client
		client.release();		
	}*/
	
@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,restServerName);
	}
}
