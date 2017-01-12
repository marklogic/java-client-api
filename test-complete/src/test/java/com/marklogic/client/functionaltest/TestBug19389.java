/*
 * Copyright 2014-2017 MarkLogic Corporation
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

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
public class TestBug19389 extends BasicJavaClientREST {

	private static String dbName = "Bug19389DB";
	private static String [] fNames = {"Bug19389DB-1"};
	
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  configureRESTServer(dbName, fNames);
	  setupAppServicesConstraint(dbName);
	}

@Test
	public void testBug19389() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testBug19389");

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);
				
		// set error format to JSON
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
//	depricated
//		srvMgr.setErrorFormat(Format.JSON);
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
		cleanupRESTServer(dbName, fNames);
		
	}
}
