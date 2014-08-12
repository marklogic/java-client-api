package com.marklogic.javaclient;

import java.io.IOException;
import java.io.InputStream;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.javaclient.BasicJavaClientREST;

import org.junit.*;

import static org.junit.Assert.*;

public class TestDatabaseAuthentication extends BasicJavaClientREST{
	
	
	private static String dbName = "DatabaseAuthenticationDB";
	private static String [] fNames = {"DatabaseAuthenticationDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort=8011;
	
	 @BeforeClass
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
	       setupAppServicesConstraint(dbName);	  

	}
	
	@Test public void testAuthenticationNone() throws IOException
	{
		setAuthentication("application-level");
		setDefaultUser("rest-admin");

		System.out.println("Running testAuthenticationNone");
		
		String filename = "text-original.txt";
		
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011);
		
		// write doc
	    writeDocumentUsingStringHandle(client, filename, "/write-text-doc-app-level/", "Text");
	 
	    // read docs
	 	InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, "/write-text-doc-app-level/" + filename, "Text");
	 		
	 	// get the contents
	 	InputStream fileRead = contentHandle.get();
	 		
	 	String readContent = convertInputStreamToString(fileRead);
	 		
	 	String expectedContent = "hello world, welcome to java API";
	 						
	 	assertEquals("Write Text difference", expectedContent.trim(), readContent.trim());
		
		// release client
		client.release();
		
		setAuthentication("digest");
		setDefaultUser("nobody");
	}
	
@Test	public void testAuthenticationBasic() throws IOException
	{
		setAuthentication("basic");
		setDefaultUser("rest-writer");
		
		System.out.println("Running testAuthenticationBasic");
		
		String filename = "text-original.txt";
		
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.BASIC);
		
		// write doc
	    writeDocumentUsingStringHandle(client, filename, "/write-text-doc-basic/", "Text");
	    
	    // read docs
	 	InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, "/write-text-doc-basic/" + filename, "Text");
	 		
	 	// get the contents
	 	InputStream fileRead = contentHandle.get();
	 		
	 	String readContent = convertInputStreamToString(fileRead);
	 		
	 	String expectedContent = "hello world, welcome to java API";
	 						
	 	assertEquals("Write Text difference", expectedContent.trim(), readContent.trim());
		
		// release client
		client.release();
		
		setAuthentication("digest");
		setDefaultUser("nobody");
	}

	public void tearDown() throws Exception
	{
		System.out.println("In tear down" );
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}
}

