package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import javax.xml.namespace.QName;

import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.Format;
import org.custommonkey.xmlunit.exceptions.XpathException;

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
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}

@SuppressWarnings("deprecation")
@Test
	public void testBug19443() throws FileNotFoundException, XpathException
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
	    
	    String outputTrimmed = output.replaceAll("\n", "");
	    outputTrimmed = outputTrimmed.replaceAll(" ", "");
	    
	    System.out.println(output);
	    
	    assertTrue("option is not correct", outputTrimmed.contains("<ns2:optionsxmlns:ns2=\"http://marklogic.com/appservices/search\"><ns2:constraintname=\"geoElemChild\"><ns2:geo-elem><ns2:elementns=\"\"name=\"bar\"/><ns2:geo-option>type=long-lat-point</ns2:geo-option><ns2:parentns=\"\"name=\"foo\"/></ns2:geo-elem></ns2:constraint></ns2:options>") || outputTrimmed.contains("<search:optionsxmlns:search=\"http://marklogic.com/appservices/search\"><search:constraintname=\"geoElemChild\"><search:geo-elem><search:elementns=\"\"name=\"bar\"/><search:geo-option>type=long-lat-point</search:geo-option><search:parentns=\"\"name=\"foo\"/></search:geo-elem></search:constraint></search:options>"));
	                    
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
