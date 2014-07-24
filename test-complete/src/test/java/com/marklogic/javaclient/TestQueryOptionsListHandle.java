package com.marklogic.javaclient;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.QueryOptionsListHandle;
import com.marklogic.javaclient.BasicJavaClientREST;
import org.junit.*;
public class TestQueryOptionsListHandle extends BasicJavaClientREST {
	
	private static String dbName = "QueryOptionsListHandleDB";
	private static String [] fNames = {"QueryOptionsListHandleDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	
@BeforeClass	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testNPE() throws IOException, SAXException, ParserConfigurationException
	{		
		System.out.println("Running testNPE");
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		QueryOptionsListHandle handle = new QueryOptionsListHandle();
		
		HashMap map = handle.getValuesMap();
		    
		// release client
		client.release();
	}
		
@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}
}
