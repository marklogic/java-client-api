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
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.SearchHandle;

import org.junit.*;
public class TestFieldConstraint extends BasicJavaClientREST {
	static String filenames[] = {"bbq1.xml", "bbq2.xml", "bbq3.xml", "bbq4.xml", "bbq5.xml"};
	static String queryOptionName = "fieldConstraintOpt.xml";
	private static String dbName = "FieldConstraintDB";
	private static String [] fNames = {"FieldConstraintDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort=8011;
	
	@BeforeClass
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
		addField(dbName, "bbqtext");
		includeElementField(dbName, "bbqtext", "http://example.com", "title");
		includeElementField(dbName, "bbqtext", "http://example.com", "abstract");
	}
	 
	 @After
	public  void testCleanUp() throws Exception
	{
		clearDB(restPort);
		System.out.println("Running clear script");
	}

	@Test
	public void testFieldConstraint() throws IOException
	{
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename:filenames)
		{
			writeDocumentReaderHandle(client, filename, "/field-constraint/", "XML");
		}
							
		// write the query options to the database
		setQueryOption(client, queryOptionName);
							
		// run the search
		SearchHandle resultsHandle = runSearch(client, queryOptionName, "summary:Louisiana AND summary:sweet");
		
		// search result
		String matchResult = "Matched "+resultsHandle.getTotalResults();
		String expectedMatchResult = "Matched 1";
		assertEquals("Match results difference", expectedMatchResult, matchResult);
		
		String result = returnSearchResult(resultsHandle);
		String expectedResult = "|Matched 3 locations in /field-constraint/bbq3.xml";
		
		assertEquals("Results difference", expectedResult, result);
						
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
