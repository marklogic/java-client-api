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

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathNotExists;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

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
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.query.StringQueryDefinition;
public class TestSearchOptions extends BasicJavaClientREST {


	private static String dbName = "TestSearchOptionsDB";
	private static String [] fNames = {"TestSearchOptionsDB-1"};
	

	@BeforeClass	
	public static void setUp() throws Exception 
	{
		System.out.println("In setup");
		configureRESTServer(dbName, fNames);
		setupAppServicesConstraint(dbName);
	}

	@After
	public void testCleanUp() throws Exception
	{
		clearDB();
		System.out.println("Running clear script");
	}	

	@Test	
	public void testReturnResultsFalse() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testReturnResultsFalse");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "searchReturnResultsFalseOpt.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/return-results-false/", "XML");
		}

		setQueryOption(client, queryOptionName);

		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("intitle:1945");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("1", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
		assertXpathNotExists("//*[local-name()='result']", resultDoc);

		// release client
		client.release();		
	}

	@Test	
	public void testSetViewMetadata() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testSetViewMetadata");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "setViewOpt.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// create and initialize a handle on the metadata
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

		// put metadata
		metadataHandle.getCollections().addAll("my-collection");
		metadataHandle.getCollections().addAll("another-collection");
		metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/return-setview-meta/", metadataHandle, "XML");
		}

		setQueryOption(client, queryOptionName);

		QueryManager queryMgr = client.newQueryManager();
		queryMgr.setView(QueryView.METADATA);

		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("pop:high");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("1", "string(//*[local-name()='response']//@*[local-name()='start'])", resultDoc);
		assertXpathExists("//*[local-name()='metrics']", resultDoc);

		// release client
		client.release();		
	}

	@Test	
	public void testSetViewResults() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testSetViewResults");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "setViewOpt.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/return-setview-results/", "XML");
		}

		setQueryOption(client, queryOptionName);

		QueryManager queryMgr = client.newQueryManager();
		queryMgr.setView(QueryView.RESULTS);

		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("pop:high");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("3", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
		assertXpathExists("//*[local-name()='result']", resultDoc);

		// release client
		client.release();		
	}

	@Test	
	public void testSetViewFacets() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testSetViewFacets");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "setViewOpt.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/return-setview-facets/", "XML");
		}

		setQueryOption(client, queryOptionName);

		QueryManager queryMgr = client.newQueryManager();
		queryMgr.setView(QueryView.FACETS);

		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("pop:high");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("3", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
		assertXpathExists("//*[local-name()='facet']", resultDoc);

		// release client
		client.release();		
	}

	@Test	
	public void testSetViewDefault() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testSetViewDefault");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "setViewOpt.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/return-setview-all/", "XML");
		}

		setQueryOption(client, queryOptionName);

		QueryManager queryMgr = client.newQueryManager();
		queryMgr.setView(QueryView.DEFAULT);

		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("pop:high");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("3", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
		assertXpathExists("//*[local-name()='result']", resultDoc);
		assertXpathExists("//*[local-name()='facet']", resultDoc);

		// release client
		client.release();		
	}

	@Test	
	public void testSetViewAll() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testSetViewAll");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "setViewOpt.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/return-setview-all/", "XML");
		}

		setQueryOption(client, queryOptionName);

		QueryManager queryMgr = client.newQueryManager();
		queryMgr.setView(QueryView.ALL);

		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("pop:high");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("3", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
		assertXpathExists("//*[local-name()='result']", resultDoc);
		assertXpathExists("//*[local-name()='facet']", resultDoc);
		assertXpathExists("//*[local-name()='metrics']", resultDoc);

		// release client
		client.release();		
	}

	@AfterClass	
	public static  void tearDown() throws Exception
	{
		System.out.println("In tear down");
		cleanupRESTServer(dbName, fNames);
	}
}
