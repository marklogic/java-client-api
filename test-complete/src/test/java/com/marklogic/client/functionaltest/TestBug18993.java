/*
 * Copyright 2014-2016 MarkLogic Corporation
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

import static org.junit.Assert.*;

import java.io.IOException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.StringHandle;

import org.junit.*;
public class TestBug18993 extends BasicJavaClientREST {
	
	private static String dbName = "Bug18993DB";
	private static String [] fNames = {"Bug18993DB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	
	@BeforeClass
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
		loadBug18993();
	}

@After
	public  void testCleanUp() throws Exception
	{
		clearDB(restPort);
		System.out.println("Running clear script");
	}

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

