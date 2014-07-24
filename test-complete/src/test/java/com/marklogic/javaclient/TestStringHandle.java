package com.marklogic.javaclient;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.javaclient.BasicJavaClientREST;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.*;

import org.junit.*;
public class TestStringHandle extends BasicJavaClientREST {
	
	private static String dbName = "StringDB";
	private static String [] fNames = {"StringDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	
@BeforeClass	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testXmlCRUD() throws IOException, SAXException, ParserConfigurationException
	{	
		String filename = "xml-original-test.xml";
		String uri = "/write-xml-string/";
		
		System.out.println("Running testXmlCRUD");
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// write docs
		 writeDocumentUsingStringHandle(client, filename, uri, "XML");
				
		// read docs
		StringHandle contentHandle = readDocumentUsingStringHandle(client, uri + filename, "XML");
		
		// get the contents
	//	File fileRead = contentHandle.get();
		
		String readContent = contentHandle.get();

		// get xml document for expected result
		Document expectedDoc = expectedXMLDocument(filename);
		
		// convert actual string to xml doc
		Document readDoc = convertStringToXMLDocument(readContent);
	    	    
	    assertXMLEqual("Write XML difference", expectedDoc, readDoc);
				
	    // update the doc
	    // acquire the content for update
	    String updateFilename = "xml-updated-test.xml";
	    updateDocumentUsingStringHandle(client, updateFilename, uri + filename, "XML");
	    
	    // read the document
	    StringHandle updateHandle = readDocumentUsingStringHandle(client, uri + filename, "XML");
	 
	    // get the contents
	    String readContentUpdate = updateHandle.get();
	 	
//	    String readContentUpdate = convertFileToString(fileReadUpdate);

	    // get xml document for expected result
	    Document expectedDocUpdate = expectedXMLDocument(updateFilename);
		
	    // convert actual string to xml doc
	    Document readDocUpdate = convertStringToXMLDocument(readContentUpdate);
	    	    
	    assertXMLEqual("Write XML difference", expectedDocUpdate, readDocUpdate);
	 		 	
	    // delete the document
	    deleteDocument(client, uri + filename, "XML");
	    
	    // read the deleted document
	    String exception = "";
	    try
	    {
	    	FileHandle deleteHandle = readDocumentUsingFileHandle(client, uri + filename, "XML");
	    } catch (Exception e) { exception = e.toString(); }
	    
            String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /write-xml-string/xml-original-test.xml";
	    assertEquals("Document is not deleted", expectedException, exception);
	    
	    //assertFalse("Document is not deleted", isDocumentExist(client, uri + filename, "XML"));
	    
	    // release client
	    client.release();
	}
	

@SuppressWarnings("deprecation")
@Test	public void testTextCRUD() throws IOException
	{	
	    String filename = "text-original.txt";
	    String uri = "/write-text-stringhandle/";
		
	    System.out.println("Running testTextCRUD");
		
	    // connect the client
	    DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
	    // write docs
	    writeDocumentUsingStringHandle(client, filename, uri, "Text");
				
	    // read docs
	    StringHandle contentHandle = readDocumentUsingStringHandle(client, uri + filename, "Text");
		
	    // get the contents
//	    File fileRead = contentHandle.get();
		
	    String readContent = contentHandle.get();
		
	    String expectedContent = "hello world, welcome to java API";
						
	    assertEquals("Write Text difference", expectedContent.trim(), readContent.trim());

	    // update the doc
	    // acquire the content for update
	    String updateFilename = "text-updated.txt";
	    updateDocumentUsingStringHandle(client, updateFilename, uri + filename, "Text");
	    
	    // read the document
	    StringHandle updateHandle = readDocumentUsingStringHandle(client, uri + filename, "Text");
		
		// get the contents
//		File fileReadUpdate = updateHandle.get();
		
		String readContentUpdate = updateHandle.get();
		
		String expectedContentUpdate = "hello world, welcome to java API after new updates";
		
		assertEquals("Write Text difference", expectedContentUpdate.trim(), readContentUpdate.toString().trim());

		// delete the document
	    deleteDocument(client, uri + filename, "Text");

		// read the deleted document
	    //assertFalse("Document is not deleted", isDocumentExist(client, uri + filename, "Text"));
	    
	    String exception = "";
	    try
	    {
	    	FileHandle deleteHandle = readDocumentUsingFileHandle(client, uri + filename, "Text");
	    } catch (Exception e) { exception = e.toString(); }
	    
	    String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /write-text-stringhandle/text-original.txt";
	    assertEquals("Document is not deleted", expectedException, exception);

		
		// release client
		client.release();
	}


@SuppressWarnings("deprecation")
@Test	public void testJsonCRUD() throws IOException
	{	
		String filename = "json-original.json";
		String uri = "/write-json-stringhandle/";
		
		System.out.println("Running testJsonCRUD");
		
		ObjectMapper mapper = new ObjectMapper();
		
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// write docs
		writeDocumentUsingStringHandle(client, filename, uri, "JSON");
				
		// read docs
		StringHandle contentHandle = readDocumentUsingStringHandle(client, uri + filename, "JSON");
		
		// get the contents
//		File fileRead = contentHandle.get();
		
//		JsonNode readContent = mapper.readTree(fileRead);
                JsonNode readContent = mapper.readValue(contentHandle.get(),JsonNode.class);

				
		// get expected contents
		JsonNode expectedContent = expectedJSONDocument(filename);
				
		assertTrue("Write JSON document difference", readContent.equals(expectedContent));		
		
	    // update the doc
	    // acquire the content for update
	    String updateFilename = "json-updated.json";
	    updateDocumentUsingFileHandle(client, updateFilename, uri + filename, "JSON");
	    
	    // read the document
	    FileHandle updateHandle = readDocumentUsingFileHandle(client, uri + filename, "JSON");
		
		// get the contents
		File fileReadUpdate = updateHandle.get();
				
		JsonNode readContentUpdate = mapper.readTree(fileReadUpdate);
		
		// get expected contents
		JsonNode expectedContentUpdate = expectedJSONDocument(updateFilename);
				
		assertTrue("Write JSON document difference", readContentUpdate.equals(expectedContentUpdate));		

		// delete the document
	    deleteDocument(client, uri + filename, "JSON");

		// read the deleted document
	    //assertFalse("Document is not deleted", isDocumentExist(client, uri + filename, "JSON"));
	    
	    String exception = "";
	    try
	    {
	    	FileHandle deleteHandle = readDocumentUsingFileHandle(client, uri + filename, "JSON");
	    } catch (Exception e) { exception = e.toString(); }
	    
	    String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /write-json-stringhandle/json-original.json";
	    assertEquals("Document is not deleted", expectedException, exception);

		
		// release client
		client.release();
	}

@SuppressWarnings("deprecation")
@Test	public void testBug22356() throws IOException, SAXException, ParserConfigurationException
	{	
		System.out.println("Running testBug22356");
		
		String filename = "xml-original-test.xml";
		String uri = "/write-xml-string/";
		
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
			
		// read docs
		StringHandle contentHandle = null;
		try
	    {
		// get the contents
		String readContent = contentHandle.get();
	    } 
		catch (NullPointerException e) { 
			System.out.println("Null pointer Exception is expected noy an Empty Value");
			e.toString(); 
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
