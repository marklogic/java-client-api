package com.marklogic.javaclient;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.javaclient.BasicJavaClientREST;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.*;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.*;
public class TestDOMHandle extends BasicJavaClientREST {
	
	
	private static String dbName = "DOMHandleDB";
	private static String [] fNames = {"DOMHandleDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	@BeforeClass
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0],  restServerName,8011);
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testXmlCRUD() throws IOException, SAXException, ParserConfigurationException
	{	
		String filename = "xml-original-test.xml";
		String uri = "/write-xml-domhandle/";
		
		System.out.println("Running testXmlCRUD");
		
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setNormalizeWhitespace(true);
		
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// write docs
		 writeDocumentUsingDOMHandle(client, filename, uri, "XML");
				
		// read docs
		DOMHandle contentHandle = readDocumentUsingDOMHandle(client, uri + filename, "XML");
		
		Document readDoc = contentHandle.get();
		
		// get xml document for expected result
		Document expectedDoc = expectedXMLDocument(filename);
			    	    
	    assertXMLEqual("Write XML difference", expectedDoc, readDoc);
				
	    // update the doc
	    // acquire the content for update
	    String updateFilename = "xml-updated-test.xml";
	    updateDocumentUsingDOMHandle(client, updateFilename, uri + filename, "XML");
	    
	    // read the document
	    DOMHandle updateHandle = readDocumentUsingDOMHandle(client, uri + filename, "XML");
	 
	    Document readDocUpdate = updateHandle.get();
	    
		// get xml document for expected result
		Document expectedDocUpdate = expectedXMLDocument(updateFilename);
		
	    assertXMLEqual("Write XML difference", expectedDocUpdate, readDocUpdate);
	 		 	
		// delete the document
	    deleteDocument(client, uri + filename, "XML");
	    
		// read the deleted document
	    String exception = "";
	    try
	    {
	    	InputStreamHandle deleteHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "XML");
	    } catch (Exception e) { exception = e.toString(); }
	    
	    String expectedException = "Could not read non-existent document";
	    boolean documentIsDeleted = exception.contains(expectedException);
	    assertTrue("Document is not deleted", documentIsDeleted);

	    //assertFalse("Document is not deleted", isDocumentExist(client, uri + filename, "XML"));
	    
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
