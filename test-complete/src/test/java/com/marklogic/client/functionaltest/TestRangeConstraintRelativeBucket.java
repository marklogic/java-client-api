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

import static org.junit.Assert.*;

import java.io.IOException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.SearchHandle;

import org.junit.*;

public class TestRangeConstraintRelativeBucket extends BasicJavaClientREST {
	static String filenames[] = {"bbq1.xml", "bbq2.xml", "bbq3.xml", "bbq4.xml", "bbq5.xml"};
	static String queryOptionName = "rangeRelativeBucketConstraintOpt.xml"; 
	private static String dbName = "RangeConstraintRelBucketDB";
	private static String [] fNames = {"RangeConstraintRelBucketDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

	@BeforeClass	
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
		addRangeElementAttributeIndex(dbName, "dateTime", "http://example.com", "entry", "", "date");
	}

	@Test	
	public void testRangeConstraintRelativeBucket() throws IOException
	{
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);

		// write docs
		for(String filename:filenames)
		{
			writeDocumentReaderHandle(client, filename, "/range-constraint-rel-bucket/", "XML");
		}

		// write the query options to the database
		setQueryOption(client, queryOptionName);

		// run the search
		SearchHandle resultsHandle = runSearch(client, queryOptionName, "date:older");

		// search result
		String result = "Matched "+resultsHandle.getTotalResults();
		String expectedResult = "Matched 5";
		assertEquals("Document match difference", expectedResult, result);

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
