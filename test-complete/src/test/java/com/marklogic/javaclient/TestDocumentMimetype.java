package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.io.FileHandle;
import org.junit.*;
public class TestDocumentMimetype extends BasicJavaClientREST {
	
	private static String dbName = "TestDocumentMimetypeDB";
	private static String [] fNames = {"TestDocumentMimetypeDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	@BeforeClass
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testMatchedMimetypeOnXML() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testMatchedMimetypeOnXML");
		
		String filename = "flipper.xml";
		String uri = "/xml-mimetype/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setMimetype("application/xml");
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
    	
    	// read document format
    	docMgr.read(docId, handle);
    	String format = handle.getFormat().name();
    	String expectedFormat = "XML";
    	
    	assertEquals("Format does not match", expectedFormat, format);
		
	    // release the client
	    client.release();
	}

	@SuppressWarnings("deprecation")
	@Test	
	public void testUnknownMimetypeOnXML() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testUnknownMimetypeOnXML");
		
		String filename = "flipper.xml";
		String uri = "/xml-mimetype/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setMimetype("application/x-excel");
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
    	
    	// read document mimetype
    	docMgr.read(docId, handle);
    	String format = handle.getFormat().name();
    	String expectedFormat = "XML";
    	
    	assertEquals("Format does not match", expectedFormat, format);
		
	    // release the client
	    client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testUnmatchedMimetypeOnXML() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testUnmatchedMimetypeOnXML");
		
		String filename = "flipper.xml";
		String uri = "/xml-mimetype/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setMimetype("image/svg+xml");
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
    	
    	// read document mimetype
    	docMgr.read(docId, handle);
    	String format = handle.getFormat().name();
    	String expectedFormat = "XML";
    	
    	assertEquals("Format does not match", expectedFormat, format);
		
	    // release the client
	    client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testUnsupportedMimetypeOnXML() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testUnsupportedMimetypeOnXML");
		
		String filename = "flipper.xml";
		String uri = "/xml-mimetype/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setMimetype("application/vnd.nokia.configuration-message");
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
    	
    	// read document mimetype
    	docMgr.read(docId, handle);
    	String format = handle.getFormat().name();
    	String expectedFormat = "XML";
    	
    	assertEquals("Format does not match", expectedFormat, format);
		
	    // release the client
	    client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testMatchedMimetypeOnJSON() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testMatchedMimetypeOnJSON");
		
		String filename = "json-original.json";
		String uri = "/json-mimetype/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setMimetype("application/json");
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
    	
    	// read document format
    	docMgr.read(docId, handle);
    	String format = handle.getFormat().name();
    	String expectedFormat = "JSON";
    	
    	assertEquals("Format does not match", expectedFormat, format);
		
	    // release the client
	    client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testUnknownMimetypeOnJSON() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testUnknownMimetypeOnJSON");
		
		String filename = "json-original.json";
		String uri = "/json-mimetype/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setMimetype("image/jpeg");
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
    	
    	// read document mimetype
    	docMgr.read(docId, handle);
    	String format = handle.getFormat().name();
    	String expectedFormat = "BINARY";
    	
    	assertEquals("Format does not match", expectedFormat, format);
		
	    // release the client
	    client.release();
	}


	@SuppressWarnings("deprecation")
	@Test	public void testUnmatchedMimetypeOnJSON() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testUnmatchedMimetypeOnJSON");
		
		String filename = "json-original.json";
		String uri = "/json-mimetype/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setMimetype("text/html");
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
    	
    	// read document mimetype
    	docMgr.read(docId, handle);
    	String format = handle.getFormat().name();
    	String expectedFormat = "TEXT";
    	
    	assertEquals("Format does not match", expectedFormat, format);
		
	    // release the client
	    client.release();
	}


	@SuppressWarnings("deprecation")
	@Test	public void testUnsupportedMimetypeOnJSON() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testUnsupportedMimetypeOnJSON");
		
		String filename = "json-original.json";
		String uri = "/json-mimetype/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setMimetype("application/vnd.nokia.configuration-message");
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
    	
    	// read document mimetype
    	docMgr.read(docId, handle);
    	String format = handle.getFormat().name();
    	String expectedFormat = "JSON";
    	
    	assertEquals("Format does not match", expectedFormat, format);
		
	    // release the client
	    client.release();
	}


	@SuppressWarnings("deprecation")
	@Test	public void testMatchedMimetypeOnBinary() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testMatchedMimetypeOnBinary");
		
		String filename = "Pandakarlino.jpg";
		String uri = "/bin-mimetype/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setMimetype("image/jpeg");
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
    	
    	// read document format
    	docMgr.read(docId, handle);
    	String format = handle.getFormat().name();
    	String expectedFormat = "BINARY";
    	
    	assertEquals("Format does not match", expectedFormat, format);
		
	    // release the client
	    client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testUnknownMimetypeOnBinary() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testUnknownMimetypeOnBinary");
		
		String filename = "Pandakarlino.jpg";
		String uri = "/bin-mimetype/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setMimetype("application/rtf");
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
    	
    	// read document mimetype
    	docMgr.read(docId, handle);
    	String format = handle.getFormat().name();
    	String expectedFormat = "BINARY";
    	
    	assertEquals("Format does not match", expectedFormat, format);
		
	    // release the client
	    client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testUnmatchedMimetypeOnBinary() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testUnmatchedMimetypeOnBinary");
		
		String filename = "Pandakarlino.jpg";
		String uri = "/bin-mimetype/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setMimetype("text/rtf");
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
    	
    	// read document mimetype
    	docMgr.read(docId, handle);
    	String format = handle.getFormat().name();
    	String expectedFormat = "TEXT";
    	
    	assertEquals("Format does not match", expectedFormat, format);
		
	    // release the client
	    client.release();
	}


	@SuppressWarnings("deprecation")
	@Test	public void testUnsupportedMimetypeOnBinary() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testUnsupportedMimetypeOnBinary");
		
		String filename = "Pandakarlino.jpg";
		String uri = "/bin-mimetype/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setMimetype("application/vnd.nokia.configuration-message");
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
    	
    	// read document mimetype
    	docMgr.read(docId, handle);
    	String format = handle.getFormat().name();
    	String expectedFormat = "BINARY";
    	
    	assertEquals("Format does not match", expectedFormat, format);
		
	    // release the client
	    client.release();
	}


	@SuppressWarnings("deprecation")
	@Test	public void testMatchedMimetypeOnText() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testMatchedMimetypeOnText");
		
		String filename = "text-original.txt";
		String uri = "/txt-mimetype/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setMimetype("text/plain");
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
    	
    	// read document format
    	docMgr.read(docId, handle);
    	String format = handle.getFormat().name();
    	String expectedFormat = "TEXT";
    	
    	assertEquals("Format does not match", expectedFormat, format);
		
	    // release the client
	    client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testUnknownMimetypeOnText() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testUnknownMimetypeOnText");
		
		String filename = "text-original.txt";
		String uri = "/txt-mimetype/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setMimetype("application/rtf");
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
    	
    	// read document mimetype
    	docMgr.read(docId, handle);
    	String format = handle.getFormat().name();
    	String expectedFormat = "TEXT";
    	
    	assertEquals("Format does not match", expectedFormat, format);
		
	    // release the client
	    client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testUnmatchedMimetypeOnText() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testUnmatchedMimetypeOnText");
		
		String filename = "text-original.txt";
		String uri = "/txt-mimetype/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setMimetype("image/jpeg");
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
    	
    	// read document mimetype
    	docMgr.read(docId, handle);
    	String format = handle.getFormat().name();
    	String expectedFormat = "BINARY";
    	
    	assertEquals("Format does not match", expectedFormat, format);
		
	    // release the client
	    client.release();
	}


	@SuppressWarnings("deprecation")
	@Test	public void testUnsupportedMimetypeOnText() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testUnsupportedMimetypeOnText");
		
		String filename = "text-original.txt";
		String uri = "/txt-mimetype/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		DocumentManager docMgr = client.newDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		handle.setMimetype("application/vnd.nokia.configuration-message");
		
		// create docId
		String docId = uri + filename;
		
		docMgr.write(docId, handle);
		
    	String expectedUri = uri + filename;
    	String docUri = docMgr.exists(expectedUri).getUri();
    	assertEquals("URI is not found", expectedUri, docUri);
    	
    	// read document mimetype
    	docMgr.read(docId, handle);
    	String format = handle.getFormat().name();
    	String expectedFormat = "TEXT";
    	
    	assertEquals("Format does not match", expectedFormat, format);
		
	    // release the client
	    client.release();
	}
@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,  restServerName);
		
	}
}
