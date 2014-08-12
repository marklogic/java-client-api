package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.IOException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.javaclient.BasicJavaClientREST;
import org.junit.*;
public class TestBug18993 extends BasicJavaClientREST {
	
	private static String dbName = "Bug18993DB";
	private static String [] fNames = {"Bug18993DB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	@BeforeClass
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
		loadBug18993();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testBug18993() throws IOException
	{
		System.out.println("Running testBug18993");
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
				
		StringHandle readHandle = new StringHandle();
		 
		String uris[] = {"/a b"};
		
		String expectedXML = "<foo>a space b</foo>";
		
	    for (String uri : uris) 
	    {
	        System.out.println("uri = " + uri);
	        docMgr.read(uri, readHandle);
	        System.out.println();
	        String strXML = readHandle.toString();
	        System.out.print(readHandle.toString());
	        assertTrue("Document is not returned", strXML.contains(expectedXML));
	        System.out.println();
	    } 
		
		// release client
		client.release();
	}
	@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,restServerName);
		
	}
}

