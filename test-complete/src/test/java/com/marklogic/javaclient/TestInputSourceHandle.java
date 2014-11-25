package com.marklogic.javaclient;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.InputSourceHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.javaclient.BasicJavaClientREST;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.*;

import org.junit.*;
public class TestInputSourceHandle extends BasicJavaClientREST {
	
	
	private static String dbName = "InputSourceHandleDB";
	private static String [] fNames = {"InputSourceHandleDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	@BeforeClass
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	}
	
	@Test	public void testXmlCRUD() throws IOException, SAXException, ParserConfigurationException, TransformerException
	{	
		String filename = "xml-original-test.xml";
		String uri = "/write-xml-inputsourcehandle/";
		
		System.out.println("Running testXmlCRUD");
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// write docs
		 writeDocumentUsingInputStreamHandle(client, filename, uri, "XML");
				
		// read docs
		InputSourceHandle contentHandle = readDocumentUsingInputSourceHandle(client, uri + filename, "XML");
		
		// get the contents
		InputSource fileRead = contentHandle.get();
		
		String readContent = convertInputSourceToString(fileRead);
		System.out.println(readContent);

		// get xml document for expected result
		Document expectedDoc = expectedXMLDocument(filename);
		
		// convert actual string to xml doc
		Document readDoc = convertStringToXMLDocument(readContent);
	    	    
	    assertXMLEqual("Write XML difference", expectedDoc, readDoc);
				
	    // update the doc
	    // acquire the content for update
	    String updateFilename = "xml-updated-test.xml";
	    updateDocumentUsingInputStreamHandle(client, updateFilename, uri + filename, "XML");
	    
	    // read the document
	    InputSourceHandle updateHandle = readDocumentUsingInputSourceHandle(client, uri + filename, "XML");
	 
	    // get the contents
	    InputSource fileReadUpdate = updateHandle.get();
	 	
	    String readContentUpdate = convertInputSourceToString(fileReadUpdate);

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
	    	readDocumentUsingInputStreamHandle(client, uri + filename, "XML");
	    } catch (Exception e) { exception = e.toString(); }
	    
	    String expectedException = "Could not read non-existent document";
	    boolean documentIsDeleted = exception.contains(expectedException);
	    assertTrue("Document is not deleted", documentIsDeleted);
	    
		// release client
		client.release();
	}
	@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,  restServerName);
	
	}
}