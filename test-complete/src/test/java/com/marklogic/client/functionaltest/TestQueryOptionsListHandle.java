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

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.QueryOptionsListHandle;

import org.junit.*;

public class TestQueryOptionsListHandle extends BasicJavaClientREST {

	private static String dbName = "QueryOptionsListHandleDB";
	private static String [] fNames = {"QueryOptionsListHandleDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

	@BeforeClass	
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	}

	@Test	
	public void testNPE() throws IOException, SAXException, ParserConfigurationException
	{		
		System.out.println("Running testNPE");

		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);

		QueryOptionsListHandle handle = new QueryOptionsListHandle();

		HashMap map = handle.getValuesMap();

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
