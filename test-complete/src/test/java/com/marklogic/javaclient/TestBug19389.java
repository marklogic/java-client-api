package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
public class TestBug19389 extends BasicJavaClientREST {

	private static String dbName = "Bug19389DB";
	private static String [] fNames = {"Bug19389DB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}

@SuppressWarnings("deprecation")
@Test
	public void testBug19389() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testBug19389");

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// set error format to JSON
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.setErrorFormat(Format.JSON);
		srvMgr.writeConfiguration();
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		        
        // read non-existent query option
     	StringHandle readHandle = new StringHandle();
     	readHandle.setFormat(Format.XML);
     	
        String expectedException = "com.marklogic.client.ResourceNotFoundException: Could not get /config/query/NonExistentOpt";
		
		String exception = "";
     	
		try
		{
			optionsMgr.readOptions("NonExistentOpt", readHandle);
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
