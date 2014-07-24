package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import com.marklogic.client.query.QueryManager;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.io.Format;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
public class TestSearchOnJSON extends BasicJavaClientREST {

	private static String dbName = "TestSearchOnJSONDB";
	private static String [] fNames = {"TestSearchOnJSONDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

@BeforeClass public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0],restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testRoundtrippingQueryOption() throws IOException, ParserConfigurationException, SAXException, XpathException
	{	
		System.out.println("Running testRoundtrippingQueryOption");
		
		String queryOptionName = "valueConstraintWildCardOpt.xml";
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// create a manager for writing query options
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// create handle
		ReaderHandle handle = new ReaderHandle();
		
		// write the files
		BufferedReader docStream = new BufferedReader(new FileReader("src/junit/com/marklogic/javaclient/queryoptions/" + queryOptionName));
		handle.set(docStream);
		
		// write the query options to the database
		optionsMgr.writeOptions(queryOptionName, handle);		    

		System.out.println("Write " + queryOptionName + " to database");	
		
		// read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions(queryOptionName, readHandle);
		
		String output = readHandle.get();
		
		System.out.println(output);
		
		String expectedOutput = "{\"options\":{\"return-metrics\":false, \"return-qtext\":false, \"debug\":true, \"transform-results\":{\"apply\":\"raw\"}, \"constraint\":[{\"name\":\"id\", \"value\":{\"element\":{\"ns\":\"\", \"name\":\"id\"}}}]}}";
		
		assertEquals("query option in JSON is difference", expectedOutput, output);
		
		// create handle to write back option in json
		String queryOptionNameJson = queryOptionName.replaceAll(".xml", ".json");
		StringHandle writeHandle = new StringHandle();
		writeHandle.set(output);
		writeHandle.setFormat(Format.JSON);
		optionsMgr.writeOptions(queryOptionNameJson, writeHandle);
		System.out.println("Write " + queryOptionNameJson + " to database");
		
		// release client
	    client.release();	
	}
	

@SuppressWarnings("deprecation")
@Test	public void testWithAllConstraintSearchResultinJSON() throws IOException, ParserConfigurationException, SAXException, XpathException
	{	
		System.out.println("Running testWithAllConstraintSearchResultinJSON");
		
		String filename1 = "constraint1.xml";
		String filename2 = "constraint2.xml";
		String filename3 = "constraint3.xml";
		String filename4 = "constraint4.xml";
		String filename5 = "constraint5.xml";
		String queryOptionName = "appservicesConstraintCombinationOpt.xml";

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
	    writeDocumentUsingInputStreamHandle(client, filename1, "/all-constraint-json/", metadataHandle1, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename2, "/all-constraint-json/", metadataHandle2, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename3, "/all-constraint-json/", metadataHandle3, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename4, "/all-constraint-json/", metadataHandle4, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename5, "/all-constraint-json/", metadataHandle5, "XML");
	    
		// create a manager for writing query options
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// create handle
		ReaderHandle handle = new ReaderHandle();
		
		// write the files
		BufferedReader docStream = new BufferedReader(new FileReader("src/junit/com/marklogic/javaclient/queryoptions/" + queryOptionName));
		handle.set(docStream);
		
		// write the query options to the database
		optionsMgr.writeOptions(queryOptionName, handle);		    

		System.out.println("Write " + queryOptionName + " to database");	
		
		// read query option
		InputStreamHandle readHandle = new InputStreamHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions(queryOptionName, readHandle);
		
		InputStream output = readHandle.get();
				
		// create handle to write back option in json
		String queryOptionNameJson = queryOptionName.replaceAll(".xml", ".json");
		InputStreamHandle writeHandle = new InputStreamHandle();
		writeHandle.set(output);
		writeHandle.setFormat(Format.JSON);
		optionsMgr.writeOptions(queryOptionNameJson, writeHandle);
		System.out.println("Write " + queryOptionNameJson + " to database");
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionNameJson);
		querydef.setCriteria("(coll:set1 AND coll:set5) AND -intitle:memex AND (pop:high OR pop:medium) AND price:low AND id:**11 AND date:2005-01-01 AND (para:Bush AND -para:memex)");

		// create handle
		StringHandle resultsHandle = new StringHandle();
		resultsHandle.setFormat(Format.JSON);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		String resultString = resultsHandle.get();
		
		boolean isTotalCorrect = resultString.contains("\"total\":1");
		boolean isUriCorrect = resultString.contains("\"uri\":\"/all-constraint-json/constraint1.xml\"");
		boolean isTitleCorrect = resultString.contains("Vannevar Bush");
		assertTrue("Returned total document is incorrect", isTotalCorrect);
		assertTrue("Returned document URI is incorrect", isUriCorrect);
		assertTrue("Returned document title is incorrect", isTitleCorrect);
				
		// release client
		client.release();		
	}

@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}
}
