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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.query.StructuredQueryDefinition;

public class TestRawStructuredQuery extends BasicJavaClientREST {
	private static String dbName = "TestRawStructuredQueryDB";
	private static String [] fNames = {"TestRawStructuredQueryDB-1"};
	
	@BeforeClass	
	public static void setUp() throws Exception {
		System.out.println("In setup");
		configureRESTServer(dbName, fNames);
		setupAppServicesConstraint(dbName);
		addRangeElementAttributeIndex(dbName, "decimal", "http://cloudbank.com", "price", "", "amt", "http://marklogic.com/collation/");
		addFieldExcludeRoot(dbName, "para");
		includeElementFieldWithWeight(dbName, "para", "", "p", 5,"","","");
	}

	@After
	public  void testCleanUp() throws Exception {
		clearDB();
		System.out.println("Running clear script");
	}

	@Test	
	public void testRawStructuredQueryXML() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryXML");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOption.xml");

		// create a handle for the search criteria
		FileHandle rawHandle = new FileHandle(file);

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle);

		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();

		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);

		// release client
		client.release();		
	}

	@Test	
	public void testRawStructuredQueryJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryJSON");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionJSON.json");

		String combinedQuery = convertFileToString(file);

		// create a handle for the search criteria
		StringHandle rawHandle = new StringHandle(combinedQuery);
		rawHandle.setFormat(Format.JSON);

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle);

		// create result handle
		StringHandle resultsHandle = new StringHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		String resultDoc = resultsHandle.get();

		System.out.println(resultDoc);

		assertTrue("document is not returned", resultDoc.contains("/raw-combined-query/constraint5.xml"));

		// release client
		client.release();		
	}

	@Test	
	public void testRawStructuredQueryXMLWithOptions() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryXMLWithOptions");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "valueConstraintWithoutIndexSettingsAndNSOpt.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}

		setQueryOption(client, queryOptionName);

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryNoOption.xml");

		// create a handle for the search criteria
		FileHandle rawHandle = new FileHandle(file);

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle, queryOptionName);

		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();

		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);

		// release client
		client.release();		
	}

	@Test	
	public void testRawStructuredQueryJSONWithOverwriteOptions() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryJSONWithOverwriteOptions");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "valueConstraintWithoutIndexSettingsAndNSOpt.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}

		setQueryOption(client, queryOptionName);

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionJSONOverwrite.json");

		String combinedQuery = convertFileToString(file);

		// create a handle for the search criteria
		StringHandle rawHandle = new StringHandle(combinedQuery);
		rawHandle.setFormat(Format.JSON);

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle, queryOptionName);

		// create result handle
		StringHandle resultsHandle = new StringHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		String resultDoc = resultsHandle.get();

		System.out.println(resultDoc);

		assertTrue("document is not returned", resultDoc.contains("/raw-combined-query/constraint1.xml"));

		// release client
		client.release();		
	}

	@Test	
	public void testRawStructuredQueryCollection() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryCollection");

		String filename1 = "constraint1.xml";
		String filename2 = "constraint2.xml";
		String filename3 = "constraint3.xml";
		String filename4 = "constraint4.xml";
		String filename5 = "constraint5.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// create and initialize a handle on the metadata
		DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
		DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
		DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
		DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
		DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();

		// set the metadata
		metadataHandle1.getCollections().addAll("http://test.com/set1");
		metadataHandle1.getCollections().addAll("http://test.com/set5");
		metadataHandle2.getCollections().addAll("http://test.com/set1");
		metadataHandle3.getCollections().addAll("http://test.com/set3");
		metadataHandle4.getCollections().addAll("http://test.com/set3/set3-1");
		metadataHandle5.getCollections().addAll("http://test.com/set1");
		metadataHandle5.getCollections().addAll("http://test.com/set5");

		// write docs
		writeDocumentUsingInputStreamHandle(client, filename1, "/collection-constraint/", metadataHandle1, "XML");
		writeDocumentUsingInputStreamHandle(client, filename2, "/collection-constraint/", metadataHandle2, "XML");
		writeDocumentUsingInputStreamHandle(client, filename3, "/collection-constraint/", metadataHandle3, "XML");
		writeDocumentUsingInputStreamHandle(client, filename4, "/collection-constraint/", metadataHandle4, "XML");
		writeDocumentUsingInputStreamHandle(client, filename5, "/collection-constraint/", metadataHandle5, "XML");

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionCollection.xml");

		// create a handle for the search criteria
		FileHandle rawHandle = new FileHandle(file); // bug 21107

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle);

		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();

		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("Vannevar Bush", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);

		// release client
		client.release();		
	}

	@Test	
	public void testRawStructuredQueryCombo() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryCombo");

		String filename1 = "constraint1.xml";
		String filename2 = "constraint2.xml";
		String filename3 = "constraint3.xml";
		String filename4 = "constraint4.xml";
		String filename5 = "constraint5.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// create and initialize a handle on the metadata
		DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
		DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
		DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
		DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
		DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();

		// set the metadata
		metadataHandle1.getCollections().addAll("http://test.com/set1");
		metadataHandle1.getCollections().addAll("http://test.com/set5");
		metadataHandle2.getCollections().addAll("http://test.com/set1");
		metadataHandle3.getCollections().addAll("http://test.com/set3");
		metadataHandle4.getCollections().addAll("http://test.com/set3/set3-1");
		metadataHandle5.getCollections().addAll("http://test.com/set1");
		metadataHandle5.getCollections().addAll("http://test.com/set5");

		// write docs
		writeDocumentUsingInputStreamHandle(client, filename1, "/collection-constraint/", metadataHandle1, "XML");
		writeDocumentUsingInputStreamHandle(client, filename2, "/collection-constraint/", metadataHandle2, "XML");
		writeDocumentUsingInputStreamHandle(client, filename3, "/collection-constraint/", metadataHandle3, "XML");
		writeDocumentUsingInputStreamHandle(client, filename4, "/collection-constraint/", metadataHandle4, "XML");
		writeDocumentUsingInputStreamHandle(client, filename5, "/collection-constraint/", metadataHandle5, "XML");

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionCombo.xml");

		// create a handle for the search criteria
		FileHandle rawHandle = new FileHandle(file); // bug 21107

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle);

		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();

		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("Vannevar Bush", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);

		// release client
		client.release();		
	}

	@Test	
	public void testRawStructuredQueryField() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryField");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/field-constraint/", "XML");
		}

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionField.xml");

		// create a handle for the search criteria
		FileHandle rawHandle = new FileHandle(file); // bug 21107

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle);

		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();

		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("memex", "string(//*[local-name()='result'][1]//*[local-name()='match'][1]//*[local-name()='highlight'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='match'][2]//*[local-name()='highlight'])", resultDoc);
		assertXpathEvaluatesTo("Memex", "string(//*[local-name()='result'][1]//*[local-name()='match'][3]//*[local-name()='highlight'])", resultDoc);
		assertXpathEvaluatesTo("Bush", "string(//*[local-name()='result'][2]//*[local-name()='match'][1]//*[local-name()='highlight'])", resultDoc);

		// release client
		client.release();		
	}

	@Test	
	public void testRawStructuredQueryPathIndex() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawStructuredQueryPathIndex");

		String[] filenames = {"pathindex1.xml", "pathindex2.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/path-index-raw/", "XML");
		}		

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionPathIndex.xml");

		// create a handle for the search criteria
		FileHandle rawHandle = new FileHandle(file); // bug 21107

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle);

		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();

		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/path-index-raw/pathindex2.xml", "string(//*[local-name()='result'][1]//@*[local-name()='uri'])", resultDoc);
		assertXpathEvaluatesTo("/path-index-raw/pathindex1.xml", "string(//*[local-name()='result'][2]//@*[local-name()='uri'])", resultDoc);

		// release client
		client.release();		
	}
	
	/*
	 * Create a RawStructuredQueryDefinition and add a string query by calling setCriteria and withCriteria
	 * Make sure a query using those query definitions selects only documents that match both the query definition and the string query
	 * 
	 * Uses setCriteria. Uses combinedQueryOption.xml options file
	 * QD and string query should return 1 URI in the response. constraint5.xml positive case
	 */

	@Test	
	public void testRSQuerySetCriteria() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRSQuerySetCriteria");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOption.xml");

		// create a handle for the search criteria
		FileHandle rawHandle = new FileHandle(file);
		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawStructuredQueryDefinition querydef1 = queryMgr.newRawStructuredQueryDefinition(rawHandle);
		querydef1.setCriteria("Memex OR automated");

		// create result handle
		JacksonHandle resultsHandle = new JacksonHandle();
		JacksonHandle results = queryMgr.search(querydef1, resultsHandle);

		JsonNode node = results.get();
		// Return 1 node
		assertEquals("Number of results returned incorrect in response", "1", node.path("total").asText());
		assertTrue("Results returned incorrect in response", node.path("results").get(0).path("uri").asText().contains("/raw-combined-query/constraint5.xml"));
		
		// Multiple setCriteria on query def
		RawStructuredQueryDefinition querydef2 = queryMgr.newRawStructuredQueryDefinition(rawHandle);
		querydef2.setCriteria("Memex");
		querydef2.setCriteria("automated");

		// create result handle
		JacksonHandle resultsHandle2 = new JacksonHandle();
		JacksonHandle results2 = queryMgr.search(querydef2, resultsHandle2);

		JsonNode node2 = results2.get();
		// Return 1 node
		assertEquals("Number of results returned incorrect in response", "1", node2.path("total").asText());
		assertTrue("Results returned incorrect in response", node2.path("results").get(0).path("uri").asText().contains("/raw-combined-query/constraint5.xml"));
		
		// setCriteria on query def - negative
		RawStructuredQueryDefinition querydefNeg = queryMgr.newRawStructuredQueryDefinition(rawHandle);
		querydefNeg.setCriteria("England");

		// create result handle
		JacksonHandle resultsHandleNeg = new JacksonHandle();
		JacksonHandle resultsNeg = queryMgr.search(querydefNeg, resultsHandleNeg);

		JsonNode nodeNeg = resultsNeg.get();
		// Return 0 nodes
		assertEquals("Number of results returned incorrect in response", "0", nodeNeg.path("total").asText());
		
		// Multiple setCriteria on query def - negative
		RawStructuredQueryDefinition querydefNeg2 = queryMgr.newRawStructuredQueryDefinition(rawHandle);
		querydefNeg2.setCriteria("Memex");
		querydefNeg2.setCriteria("England");

		// create result handle
		JacksonHandle resultsHandleNeg2 = new JacksonHandle();
		JacksonHandle resultsNeg2 = queryMgr.search(querydefNeg2, resultsHandleNeg2);

		JsonNode nodeNeg2 = resultsNeg2.get();
		// Return 0 node
		assertEquals("Number of results returned incorrect in response", "0", nodeNeg2.path("total").asText());
		
		// release client
		client.release();		
	}
	
	/*
	 * Create a RawStructuredQueryDefinition and add a string query by calling setCriteria and withCriteria
	 * Make sure a query using those query definitions selects only documents that match both the query definition and the string query
	 * 
	 * Uses withCriteria. Uses combinedQueryPopularityOption.xml options file
	 * 
	 */

	@Test	
	public void testRSQueryWithCriteria() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRSQueryWithCriteria");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryPopularityOption.xml");

		// create a handle for the search criteria
		FileHandle rawHandle = new FileHandle(file);
		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawStructuredQueryDefinition querydef1 = (queryMgr.newRawStructuredQueryDefinition(rawHandle)).withCriteria("Vannevar");
	
		// create handle
		JacksonHandle strHandle = new JacksonHandle();
		JacksonHandle results = queryMgr.search(querydef1, strHandle.withFormat(Format.JSON));

		JsonNode node = results.get();
		assertEquals("Number of results returned incorrect in response", "2", node.path("total").asText());
		assertTrue("Results returned incorrect in response", node.path("results").get(0).path("uri").asText().contains("/raw-combined-query/constraint1.xml")||
				node.path("results").get(1).path("uri").asText().contains("/raw-combined-query/constraint1.xml") );
		assertTrue("Results returned incorrect in response", node.path("results").get(0).path("uri").asText().contains("/raw-combined-query/constraint4.xml")||
				node.path("results").get(1).path("uri").asText().contains("/raw-combined-query/constraint4.xml") );
		
		/*This is an invalid scenario--you can only specify one string criteria.
		 * Calling withCriteria subsequent times just overwrites the string criteria value.
		 */
		RawStructuredQueryDefinition rawquerydefPos = (queryMgr.newRawStructuredQueryDefinition(rawHandle)).withCriteria("Vannevar").withCriteria("Atlantic").withCriteria("intellectual");

		// create handle
		JacksonHandle strHandlePos = new JacksonHandle();
		JacksonHandle resultsPos = queryMgr.search(rawquerydefPos, strHandlePos.withFormat(Format.JSON));

		JsonNode nodePos = results.get();
		// Return 2 nodes.
		assertEquals("Number of results returned incorrect in response", "2", nodePos.path("total").asText());
		assertTrue("Results returned incorrect in response", nodePos.path("results").get(0).path("uri").asText().contains("/raw-combined-query/constraint1.xml")||
				nodePos.path("results").get(1).path("uri").asText().contains("/raw-combined-query/constraint1.xml") );
		assertTrue("Results returned incorrect in response", nodePos.path("results").get(0).path("uri").asText().contains("/raw-combined-query/constraint4.xml")||
				nodePos.path("results").get(1).path("uri").asText().contains("/raw-combined-query/constraint4.xml") );

		/*With multiple withCriteria - negative
		 *This is an invalid scenario--you can only specify one string criteria.
		 * Calling withCriteria subsequent times just overwrites the string criteria value.
		 */
		RawStructuredQueryDefinition rawquerydefNeg = (queryMgr.newRawStructuredQueryDefinition(rawHandle)).withCriteria("Vannevar").withCriteria("England");

		// create handle
		JacksonHandle strHandleNeg = new JacksonHandle();
		JacksonHandle resultsNeg = queryMgr.search(rawquerydefNeg, strHandleNeg.withFormat(Format.JSON));

		JsonNode nodeNeg = resultsNeg.get();
		// Return 0 nodes.
		assertEquals("Number of results returned incorrect in response", "0", nodeNeg.path("total").asText());

		// create query def2 with both criteria methods and check fluent return

		RawStructuredQueryDefinition rawquerydef2 = (queryMgr.newRawStructuredQueryDefinition(rawHandle));
		rawquerydef2.withCriteria("Vannevar").setCriteria("Bush");

		// create handle
		JacksonHandle strHandle2 = new JacksonHandle();
		JacksonHandle results2 = queryMgr.search(rawquerydef2, strHandle2.withFormat(Format.JSON));

		JsonNode node2 = results2.get();
		// Returns 1 node. constraint1.xml
		assertEquals("Number of results returned incorrect in response", "1", node2.path("total").asText());
		assertEquals("Result returned incorrect in response", "/raw-combined-query/constraint1.xml", node2.path("results").get(0).path("uri").asText());
			
		// release client
		client.release();		
	}

	@AfterClass	
	public static void tearDown() throws Exception {
		System.out.println("In tear down");
		cleanupRESTServer(dbName, fNames);
	}
}
