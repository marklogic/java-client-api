package com.marklogic.javaclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.XMLEventReaderHandle;
import com.marklogic.javaclient.BasicJavaClientREST;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.*;

import org.junit.*;
public class TestXMLEventReaderHandle extends BasicJavaClientREST {
	
	private static String dbName = "XMLEventReaderHandleDB";
	private static String [] fNames = {"XMLEventReaderHandleDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	
@BeforeClass	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	}
	
@SuppressWarnings("deprecation")
@Test	public void testXmlCRUD() throws IOException, SAXException, ParserConfigurationException, TransformerException, XMLStreamException
	{	
		String filename = "xml-original-test.xml";
		String uri = "/write-xml-XMLEventReaderHandle/";
		
		System.out.println("Running testXmlCRUD");
				
		// connect the client
	    DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
			
	    // write the doc
	    writeDocumentReaderHandle(client, filename, uri, "XML");
	    
	    // read the document
	    XMLEventReaderHandle readHandle = readDocumentUsingXMLEventReaderHandle(client, uri + filename, "XML");
	    
	    // access the document content
	    XMLEventReader fileRead = readHandle.get();
	    	    
	    String readContent = convertXMLEventReaderToString(fileRead);
	    String readContentCrop = readContent.substring(0, readContent.length()-11);  
	    System.out.println(readContentCrop);

	    // get xml document for expected result
		Document expectedDoc = expectedXMLDocument(filename);
		
		String expectedContent = convertXMLDocumentToString(expectedDoc);
		System.out.println(expectedContent);
		// convert actual string to xml doc
		Document readDoc = convertStringToXMLDocument(readContentCrop);

		assertXMLEqual("Write XML difference", expectedDoc,readDoc);
	    
		// update the doc
	    // acquire the content for update
	    String updateFilename = "xml-updated-test.xml";
	    updateDocumentReaderHandle(client, updateFilename, uri + filename, "XML");
	    
	    // read the document
	    XMLEventReaderHandle updateHandle = readDocumentUsingXMLEventReaderHandle(client, uri + filename, "XML");
	    
	    // access the document content
	    XMLEventReader fileReadUpdate = updateHandle.get();
	        
	    String readContentUpdate = convertXMLEventReaderToString(fileReadUpdate);
	    String readContentUpdateCrop = readContentUpdate.substring(0,readContentUpdate.length()-11);
		// get xml document for expected result
		Document expectedDocUpdate = expectedXMLDocument(updateFilename);
		
		// convert actual string to xml doc
		Document readDocUpdate = convertStringToXMLDocument(readContentUpdateCrop);

		assertXMLEqual("Write XML difference", expectedDocUpdate, readDocUpdate);


		// delete the document
	    deleteDocument(client, uri + filename, "XML");
		
		// read the deleted document
	    //assertFalse("Document is not deleted", isDocumentExist(client, "/write-xml-readerhandle/" + filename, "XML"));
	    
	    String exception = "";
	    try
	    {
	    	ReaderHandle deleteHandle = readDocumentReaderHandle(client, uri + filename, "XML");
	    } catch (Exception e) { exception = e.toString(); }
    
	    String expectedException = "Could not read non-existent document";
	    boolean documentIsDeleted = exception.contains(expectedException);
	    assertTrue("Document is not deleted", documentIsDeleted);


	        
	    // release the client
	    client.release();
	}
	
@AfterClass	public static  void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		
	}
}