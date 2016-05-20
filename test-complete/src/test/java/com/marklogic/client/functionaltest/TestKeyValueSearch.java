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

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.Transaction;
import com.marklogic.client.admin.NamespacesManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryManager;
public class TestKeyValueSearch extends BasicJavaClientREST {
	private static String dbName = "TestKeyValueSearchDB";
	private static String [] fNames = {"TestKeyValueSearchDB-1"};
	
	private static int restPort=8011;

	@BeforeClass
	public static void setUp() throws Exception 
	{
		System.out.println("In setup");
		configureRESTServer(dbName, fNames);
		setupAppServicesConstraint(dbName);
	}

	@After
	public  void testCleanUp() throws Exception
	{
		clearDB();
		System.out.println("Running clear script");
	}

	@Test
	public void testKeyValueSearch() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
	{	
		System.out.println("Running testKeyValueSearch");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "valueConstraintWithoutIndexSettingsAndNSOpt.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// create transaction
		Transaction transaction1 = client.openTransaction();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/key-value-search/", transaction1, "XML");
		}

		// commit transaction
		transaction1.commit();

		// create transaction
		Transaction transaction2 = client.openTransaction();

		setQueryOption(client, queryOptionName);

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition
		KeyValueQueryDefinition querydef = queryMgr.newKeyValueDefinition(queryOptionName);
		querydef.put(queryMgr.newElementLocator(new QName("id")), "0012");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle, transaction2);

		// commit transaction
		transaction2.commit();

		// get the result
		Document resultDoc = resultsHandle.get();

		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);

		String expectedSearchReport = "(cts:search(fn:collection(), cts:element-value-query(fn:QName(\"\",\"id\"), \"0012\", (\"case-sensitive\",\"diacritic-sensitive\",\"punctuation-sensitive\",\"whitespace-sensitive\",\"unstemmed\",\"unwildcarded\",\"lang=en\"), 1), (\"score-logtfidf\",cts:score-order(\"descending\")), 1))[1 to 10]";

		assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);

		// release client
		client.release();		
	}

	@Test	
	public void testKeyValueSearchWithNS() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testKeyValueSearchWithNS");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		//String queryOptionName = "valueConstraintWithoutIndexSettingsAndNSOpt.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// setup namespaces to test kv with namespaces
		NamespacesManager nsMgr = client.newServerConfigManager().newNamespacesManager();
		nsMgr.updatePrefix("dt","http://purl.org/dc/elements/1.1/");

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/key-value-search-ns/", "XML");
		}
		QueryManager queryMgr = client.newQueryManager();

		// create a search definition
		KeyValueQueryDefinition querydef = queryMgr.newKeyValueDefinition();
		querydef.put(queryMgr.newElementLocator(new QName("dt:date")), "2005-01-01"); 

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/key-value-search-ns/constraint1.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);

		// release client
		client.release();		
	}

	@Test	
	public void testKeyValueSearchJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
	{	
		System.out.println("testKeyValueSearchJSON");

		String[] filenames = {"json-original.json", "json-updated.json"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/key-value-search-json/", "JSON");
		}

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition
		KeyValueQueryDefinition querydef = queryMgr.newKeyValueDefinition();
		querydef.put(queryMgr.newKeyLocator("firstName"), "Aries");

		SearchHandle results = queryMgr.search(querydef, new SearchHandle());

		MatchDocumentSummary[] summaries = results.getMatchResults();

		for (MatchDocumentSummary summary : summaries) 
		{
			MatchLocation[] locations = summary.getMatchLocations();
			for (MatchLocation location : locations) 
			{
				System.out.println(location.getAllSnippetText());
				assertEquals("Invalid value", "Aries", location.getAllSnippetText());
			}
		}

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
