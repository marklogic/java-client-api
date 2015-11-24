/*
 * Copyright 2014-2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marklogic.client.functionaltest;

import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.StringHandle;
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
	    System.out.println("Expected is :  \n");
	    System.out.println(actual);
	    System.out.println("Output is :  \n");
	    System.out.println(output);

	    assertTrue( "Element geo-option not available", actual.contains("<search:geo-option>type=long-lat-point</search:geo-option>") );
 
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
