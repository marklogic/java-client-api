package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import org.junit.*;
public class TestDocumentFormat extends BasicJavaClientREST {
	
	private static String dbName = "TestDocumentFormatDB";
	private static String [] fNames = {"TestDocumentFormat-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	@BeforeClass
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testXMLFormatOnXML() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testXMLFormatOnXML");
		
		String filename = "flipper.xml";
		String uri = "/xml-format-xml-file/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setFormat(Format.XML);
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
		
	    // release the client
	    client.release();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testJSONFormatOnXML() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testJSONFormatOnXML");
		
		String filename = "flipper.xml";
		String uri = "/json-format-xml-file/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setFormat(Format.JSON);
		
		// create docId
		String docId = uri + filename;
		
		String exception = "";
		String expectedException = "write failed: Bad Request. Server Message: XDMP-JSONCHAR";
		
		try
		{
			docMgr.write(docId, handle);
		} catch (Exception e) { exception = e.toString(); }
		
		boolean isExceptionThrown = exception.contains(expectedException);
		assertTrue("Exception is not thrown", isExceptionThrown);
		
	    // release the client
	    client.release();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testBinaryFormatOnXML() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testBinaryFormatOnXML");
		
		String filename = "flipper.xml";
		String uri = "/bin-format-xml-file/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setFormat(Format.BINARY);
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);

    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);

	    // release the client
	    client.release();
	}

	@SuppressWarnings("deprecation")
	@Test	
	public void testTextFormatOnXML() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testTextFormatOnXML");
		
		String filename = "flipper.xml";
		String uri = "/txt-format-xml-file/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setFormat(Format.TEXT);
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);

    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);

	    // release the client
	    client.release();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testJSONFormatOnJSON() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testJSONFormatOnJSON");
		
		String filename = "json-original.json";
		String uri = "/json-format-json-file/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setFormat(Format.JSON);
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);

    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);

	    // release the client
	    client.release();
	}	

	@SuppressWarnings("deprecation")
	@Test
	public void testXMLFormatOnJSON() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testXMLFormatOnJSON");
		
		String filename = "json-original.json";
		String uri = "/xml-format-json-file/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setFormat(Format.XML);
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);

    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);

	    // release the client
	    client.release();
	}	

	@SuppressWarnings("deprecation")
	@Test	
	public void testBinaryFormatOnJSON() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testBinaryFormatOnJSON");
		
		String filename = "json-original.json";
		String uri = "/bin-format-json-file/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setFormat(Format.BINARY);
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);

    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);

	    // release the client
	    client.release();
	}	

	@SuppressWarnings("deprecation")
	@Test
	public void testTextFormatOnJSON() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testTextFormatOnJSON");
		
		String filename = "json-original.json";
		String uri = "/txt-format-json-file/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setFormat(Format.TEXT);
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);

    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);

	    // release the client
	    client.release();
	}	


	@SuppressWarnings("deprecation")
	@Test	public void testBinaryFormatOnBinary() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testBinaryFormatOnBinary");
		
		String filename = "Pandakarlino.jpg";
		String uri = "/bin-format-bin-file/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setFormat(Format.BINARY);
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);

    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);

	    // release the client
	    client.release();
	}	


	@SuppressWarnings("deprecation")
	@Test	public void testXMLFormatOnBinary() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testXMLFormatOnBinary");
		
		String filename = "Pandakarlino.jpg";
		String uri = "/xml-format-bin-file/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setFormat(Format.XML);
		
		// create docId
		String docId = uri + filename;
		
		String exception = "";
		String expectedException = "Local message: write failed: Bad Request. Server Message: XDMP-DOCUTF8SEQ";
		
		try
		{
			docMgr.write(docId, handle);
		} catch (Exception e) { exception = e.toString(); }
		
		boolean isExceptionThrown = exception.contains(expectedException);
		assertTrue("Exception is not thrown", isExceptionThrown);
		
	    // release the client
	    client.release();
	}	


	@SuppressWarnings("deprecation")
	@Test	public void testJSONFormatOnBinary() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testJSONFormatOnBinary");
		
		String filename = "Pandakarlino.jpg";
		String uri = "/json-format-bin-file/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setFormat(Format.JSON);
		
		// create docId
		String docId = uri + filename;
		
		String exception = "";
		String expectedException = "Local message: write failed: Bad Request. Server Message: XDMP-DOCUTF8SEQ";
		
		try
		{
			docMgr.write(docId, handle);
		} catch (Exception e) { exception = e.toString(); }
		
		boolean isExceptionThrown = exception.contains(expectedException);
		assertTrue("Exception is not thrown", isExceptionThrown);
		
	    // release the client
	    client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testTextFormatOnBinary() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testTextFormatOnBinary");
		
		String filename = "Pandakarlino.jpg";
		String uri = "/bin-format-bin-file/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setFormat(Format.TEXT);
		
		// create docId
		String docId = uri + filename;
		
		String exception = "";
		String expectedException = "Local message: write failed: Bad Request. Server Message: XDMP-DOCUTF8SEQ";
		
		try
		{
			docMgr.write(docId, handle);
		} catch (Exception e) { exception = e.toString(); }
		
		boolean isExceptionThrown = exception.contains(expectedException);
		assertTrue("Exception is not thrown", isExceptionThrown);

	    // release the client
	    client.release();
	}	


	@SuppressWarnings("deprecation")
	@Test	public void testTextFormatOnText() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testTextFormatOnText");
		
		String filename = "text-original.txt";
		String uri = "/txt-format-txt-file/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setFormat(Format.TEXT);
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);

    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);

	    // release the client
	    client.release();
	}	
	

	@SuppressWarnings("deprecation")
	@Test	public void testXMLFormatOnText() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testXMLFormatOnText");
		
		String filename = "text-original.txt";
		String uri = "/xml-format-txt-file/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setFormat(Format.XML);
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);

    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);

	    // release the client
	    client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testJSONFormatOnText() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testJSONFormatOnText");
		
		String filename = "text-original.txt";
		String uri = "/json-format-txt-file/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setFormat(Format.JSON);
		
		// create docId
		String docId = uri + filename;
		
		String exception = "";
		String expectedException = "write failed: Bad Request. Server Message: XDMP-JSONCHAR";
		
		try
		{
			docMgr.write(docId, handle);
		} catch (Exception e) { exception = e.toString(); }
		
		boolean isExceptionThrown = exception.contains(expectedException);
		assertTrue("Exception is not thrown", isExceptionThrown);

	    // release the client
	    client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testBinaryFormatOnText() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testBinaryFormatOnText");
		
		String filename = "text-original.txt";
		String uri = "/bin-format-txt-file/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setFormat(Format.BINARY);
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);

    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);

	    // release the client
	    client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testNegativeJSONFormatWithDOMHandle() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testNegativeJSONFormatWithDOMHandle");
		
		String filename = "xml-original.xml";
		String uri = "/negative-format/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		Document readDoc = expectedXMLDocument(filename);
		
		//File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		DOMHandle handle = new DOMHandle();
		handle.set(readDoc);

		String exception = "";
		String expectedException = "java.lang.IllegalArgumentException: DOMHandle supports the XML format only";
		
		try
		{
			handle.setFormat(Format.JSON);
		} catch (IllegalArgumentException e) { exception = e.toString(); }
				
		boolean isExceptionThrown = exception.contains(expectedException);
		assertTrue("Wrong exception", isExceptionThrown);

	    // release the client
	    client.release();
	}
@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}
}
