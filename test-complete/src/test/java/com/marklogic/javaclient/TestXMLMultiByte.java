package com.marklogic.javaclient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.javaclient.BasicJavaClientREST;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.*;
public class TestXMLMultiByte extends BasicJavaClientREST {
	
	private static String dbName = "XMLMultiByteDB";
	private static String [] fNames = {"XMLMultiByteDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	
@BeforeClass	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	}
	
@SuppressWarnings("deprecation")
@Test	public void testXmlMultibyte() throws IOException, SAXException, ParserConfigurationException, TransformerException
	{	
		String filename = "multibyte-original.xml";
		String uri = "/write-xml-multibyte/";
		
		System.out.println("Running testXmlMultibyte");
		
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setNormalizeWhitespace(true);
		
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// write docs
		writeDocumentUsingInputStreamHandle(client, filename, uri, "XML");
				
		// read docs
		DOMHandle contentHandle = readDocumentUsingDOMHandle(client, uri + filename, "XML");
		 
		// get the contents
		Document readDoc = contentHandle.get(); 

		// get xml document for expected result
		Document expectedDoc = expectedXMLDocument(filename);
			    	    
	    assertXMLEqual("Write XML difference", expectedDoc, readDoc);
				
	    // update the doc
	    // acquire the content for update
	    String updateFilename = "multibyte-updated.xml";
	    updateDocumentUsingInputStreamHandle(client, updateFilename, uri + filename, "XML");
	    
	    // read the document
	    DOMHandle updateHandle = readDocumentUsingDOMHandle(client, uri + filename, "XML");
	    
	    // get the contents
	    Document readDocUpdate = updateHandle.get();

		// get xml document for expected result
		Document expectedDocUpdate = expectedXMLDocument(updateFilename);
			    	    
	    assertXMLEqual("Write XML difference", expectedDocUpdate, readDocUpdate);
	 		 	
		// delete the document
	    deleteDocument(client, uri + filename, "XML");
		
		// read the deleted document
	    //assertFalse("Document is not deleted", isDocumentExist(client, uri + filename, "XML"));
	    
		// release client
		client.release();
	}
		
@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,restServerName);
	}
}
