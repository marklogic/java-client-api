package com.marklogic.javaclient;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import org.custommonkey.xmlunit.XMLUnit;

import javax.xml.namespace.QName;

import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.Format;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.StringHandle;

import org.junit.*;
public class TestBug19443 extends BasicJavaClientREST {

	private static String dbName = "TestBug19443DB";
	private static String [] fNames = {"TestBug19443DB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}

@After
	public  void testCleanUp() throws Exception
	{
		clearDB(restPort);
		System.out.println("Running clear script");
	}

@SuppressWarnings("deprecation")
@Test
	public void testBug19443() throws Exception
	{	
		System.out.println("Running testBug19443");
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        // build query options
        handle.withConstraints(builder.constraint("geoElemChild", builder.geospatial(builder.elementChildGeospatialIndex(new QName("foo"), new QName("bar")), "type=long-lat-point")));
               
        // write query options
        optionsMgr.writeOptions("ElementChildGeoSpatialIndex", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.XML);
		optionsMgr.readOptions("ElementChildGeoSpatialIndex", readHandle);
	    String output = readHandle.get();
	    
	    System.out.println(output);
	    
	    String actual = 
	    		"<search:options xmlns:search=\"http://marklogic.com/appservices/search\">" + 
	    		  "<search:constraint name=\"geoElemChild\">" + 
	    		    "<search:geo-elem>" + 
	      		      "<search:element name=\"bar\" ns=\"\"/>" + 
	    		      "<search:geo-option>type=long-lat-point</search:geo-option>" + 
	    		      "<search:parent name=\"foo\" ns=\"\"/>" +
	    		    "</search:geo-elem>" + 
	    		  "</search:constraint>" +
	    		"</search:options>";

	    XMLUnit.setIgnoreWhitespace(true);	    
	    assertXMLEqual("testBug19443", actual, output);
 
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
