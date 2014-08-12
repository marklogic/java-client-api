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
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.ServerConfigurationManager.Policy;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.document.XMLDocumentManager;
import org.junit.*;
public class TestBug18920 extends BasicJavaClientREST{

	private static String dbName = "Test18920DB";
	private static String [] fNames = {"Test18920DB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	@BeforeClass
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0],  restServerName,8011);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testBug18920() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testBug18920");
		
		String filename = "xml-original.xml";
		String uri = "/bug18920/";
		String docId = uri + filename;
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// create a manager for the server configuration
		ServerConfigurationManager configMgr = client.newServerConfigManager();

		// read the server configuration from the database
		configMgr.readConfiguration();

		// require content versions for updates and deletes
		// use Policy.OPTIONAL to allow but not require versions
		configMgr.setContentVersionRequests(Policy.REQUIRED);

		// write the server configuration to the database
		configMgr.writeConfiguration();

		System.out.println("set optimistic locking to required");
		
		// create document manager
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		// create document descriptor
		DocumentDescriptor desc = docMgr.newDescriptor(docId);
		
		// write doc
		docMgr.write(desc, handle);
				
		String docUri = desc.getUri();
		System.out.println(docUri);
		
		String exception = "";
		String expectedException = "com.marklogic.client.FailedRequestException: Local message: Content version required to write document. Server Message: You do not have permission to this method and URL";
		
		// update document with no content version
		try 
		{
			docMgr.write(docUri, handle);
		} catch (FailedRequestException e) { exception = e.toString(); }
		
		boolean isExceptionThrown = exception.contains(expectedException);
		assertTrue("Exception is not thrown", isExceptionThrown);
		
		// set content version back to none
		configMgr.setContentVersionRequests(Policy.NONE);

		// write the server configuration to the database
		configMgr.writeConfiguration();

				
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
