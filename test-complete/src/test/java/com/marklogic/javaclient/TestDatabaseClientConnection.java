package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.IOException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.javaclient.BasicJavaClientREST;
import org.junit.*;

public class TestDatabaseClientConnection extends BasicJavaClientREST{
	
	private static String dbName = "DatabaeClientConnectionDB";
	private static String [] fNames = {"DatabaeClientConnectionDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	@BeforeClass
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
	
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testReleasedClient() throws IOException
	{
		System.out.println("Running testReleasedClient");
		
		String filename = "facebook-10443244874876159931";
		
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// write doc
		writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
		
		// release client
		client.release();
		
		String stringException = "";
		
		// write doc on released client
		try
		{
			writeDocumentUsingStringHandle(client, filename, "/write-txt-doc-released-client/", "Text");
		} 
		catch (Exception e) 
		{
			stringException = "Client is not available - " + e;
		}
		
		String expectedException = "Client is not available - java.lang.NullPointerException";
		assertEquals("Exception is not thrown", expectedException, stringException);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testDatabaseClientConnectionExist()
	{
		System.out.println("Running testDatabaseClientConnectionExist");
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-reader", "x", Authentication.DIGEST);
		String[] stringClient = client.toString().split("@");
		assertEquals("Object does not exist", "com.marklogic.client.impl.DatabaseClientImpl", stringClient[0]);
		
		// release client
		client.release();
	}


	@SuppressWarnings("deprecation")
	@Test	public void testDatabaseClientConnectionInvalidPort() throws IOException
	{
		System.out.println("Running testDatabaseClientConnectionInvalidPort");
		
		String filename = "facebook-10443244874876159931";
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8033, "rest-reader", "x", Authentication.DIGEST);
		
		String expectedException = "com.sun.jersey.api.client.ClientHandlerException: org.apache.http.conn.HttpHostConnectException: Connection to http://localhost:8033 refused";
		String exception = "";
		
		// write doc
		try
		{
			writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
		}
		catch (Exception e) { exception = e.toString(); }
		
		assertEquals("Exception is not thrown", expectedException, exception);
		
		// release client
		client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testDatabaseClientConnectionInvalidUser() throws IOException
	{
		System.out.println("Running testDatabaseClientConnectionInvalidUser");
		
		String filename = "facebook-10443244874876159931";
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "foo-the-bar", "x", Authentication.DIGEST);
		
		String expectedException = "com.marklogic.client.FailedRequestException: Local message: write failed: Unauthorized. Server Message: Unauthorized";
		String exception = "";
		
		// write doc
		try
		{
			writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
		}
		catch (Exception e) { exception = e.toString(); }
		
		//System.out.println(exception);
		
	    boolean exceptionIsThrown = exception.contains(expectedException);
	    assertTrue("Exception is not thrown", exceptionIsThrown);
		
		// release client
		client.release();
	}


	@SuppressWarnings("deprecation")
	@Test	public void testDatabaseClientConnectionInvalidPassword() throws IOException
	{
		System.out.println("Running testDatabaseClientConnectionInvalidPassword");
		
		String filename = "facebook-10443244874876159931";
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "foobar", Authentication.DIGEST);
		
		String expectedException = "com.marklogic.client.FailedRequestException: Local message: write failed: Unauthorized. Server Message: Unauthorized";
		String exception = "";
		
		// write doc
		try
		{
			writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
		}
		catch (Exception e) { exception = e.toString(); }
		
		//System.out.println(exception);
		
		boolean exceptionIsThrown = exception.contains(expectedException);
	    assertTrue("Exception is not thrown", exceptionIsThrown);
		
		// release client
		client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testDatabaseClientConnectionInvalidHost() throws IOException
	{
		System.out.println("Running testDatabaseClientConnectionInvalidHost");
		
		String filename = "facebook-10443244874876159931";
		
		DatabaseClient client = DatabaseClientFactory.newClient("foobarhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		//String expectedException = "com.sun.jersey.api.client.ClientHandlerException: java.net.UnknownHostException: foobarhost: Name or service not known";
		String expectedException = "UnknownHostException";
		
		String exception = "";
		
		// write doc
		try
		{
			writeDocumentUsingStringHandle(client, filename, "/write-text-doc/", "Text");
		}
		catch (Exception e) { exception = e.toString(); }
		
		System.out.println(exception);
		
		assertTrue("Exception is not thrown", exception.contains(expectedException));
		
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

