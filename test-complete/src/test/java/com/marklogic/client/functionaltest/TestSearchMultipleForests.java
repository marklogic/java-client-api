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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
public class TestSearchMultipleForests extends BasicJavaClientREST {

	private static String dbName = "TestSearchMultipleForestsDB";
	private static String [] fNames = {"TestSearchMultipleForestsDB-1", "TestSearchMultipleForestsDB-2"};
	

	@BeforeClass 
	public static void setUp() throws Exception 
	{
		System.out.println("In setup");
		configureRESTServer(dbName, fNames);
		createForest(fNames[1],dbName);
		setupAppServicesGeoConstraint(dbName);
	}

	@Test	
	public void testSearchMultipleForest() throws KeyManagementException, NoSuchAlgorithmException, IOException,  SAXException, ParserConfigurationException, TransformerException
	{	
		System.out.println("Running testSearchMultipleForest");
		// connect the client
		DatabaseClient client = getDatabaseClient("rest-writer", "x", Authentication.DIGEST);

		DocumentManager docMgr = client.newDocumentManager();
		StringHandle writeHandle1 = new StringHandle();
		for(int a = 1; a <= 10; a++ ) {
			writeHandle1.set("<root>hello</root>");
			docMgr.write("/forest-A/file" + a + ".xml", writeHandle1);
		}
		
		StringHandle writeHandle2 = new StringHandle();
		for(int b = 1; b <= 10; b++ ) {
			writeHandle2.set("<root>hello</root>");
			docMgr.write("/forest-B/file" + b + ".xml", writeHandle1);
		}

		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("");
		querydef.setCriteria("");

		SearchHandle sHandle = new SearchHandle();
		// New handles to search for documents on individual forests.
		SearchHandle sHandleOnForest1 = new SearchHandle();
		SearchHandle sHandleOnForest2 = new SearchHandle();
		
		queryMgr.search(querydef, sHandle);
		queryMgr.search(querydef, sHandleOnForest1, fNames[0]);
		System.out.println("Documents available on Forest 1 is " + sHandleOnForest1.getTotalResults());
		
		queryMgr.search(querydef, sHandleOnForest2, fNames[1]);
		System.out.println("Documents available on Forest 2 is " + sHandleOnForest2.getTotalResults());
		
		// Assert on the total from individual forest counts. Round robin scheme yeilds 10 on F1 and 10 on F2.
		// Future assignments schemes?
		assertTrue("Document count is incorrect", sHandleOnForest1.getTotalResults() + sHandleOnForest2.getTotalResults() == 20);
		
		System.out.println(sHandle.getTotalResults());
		assertTrue("Document count is incorrect", sHandle.getTotalResults() == 20);

		client.release();
	}

	@AfterClass	
	public static void tearDown() throws Exception {
		System.out.println("In tear down");
		cleanupRESTServer(dbName, fNames);
	}
}
