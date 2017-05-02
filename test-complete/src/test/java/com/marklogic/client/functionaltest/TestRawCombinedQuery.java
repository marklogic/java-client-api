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
import static org.junit.Assert.*;

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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
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
import com.marklogic.client.query.RawCombinedQueryDefinition;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestRawCombinedQuery extends BasicJavaClientREST {
	private static String dbName = "TestRawCombinedQueryDB";
	private static String [] fNames = {"TestRawCombinedQueryDB-1"};
	
	
	
	@BeforeClass	
	public static void setUp() throws Exception 
	{
		System.out.println("In setup");
		configureRESTServer(dbName, fNames);
		setupAppServicesConstraint(dbName);
		addRangeElementAttributeIndex(dbName, "decimal", "http://cloudbank.com", "price", "", "amt", "http://marklogic.com/collation/");
		addFieldExcludeRoot(dbName, "para");
		includeElementFieldWithWeight(dbName, "para", "", "p", 5,"","","");
	}
	@After
	public void testCleanUp() throws Exception
	{
		clearDB();
		System.out.println("Running clear script");
	}
	
	@Test	
	public void testBug22353() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testBug22353");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOption.xml");

		String combinedQuery = convertFileToString(file);

		// create a handle for the search criteria
		StringHandle rawHandle = new StringHandle(combinedQuery);

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);
		queryMgr.newStringDefinition("LinkResultDocumentsOpt.xml");

		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println("Mime Type : "+resultsHandle.getMimetype());
		System.out.println(convertXMLDocumentToString(resultDoc));

		assertEquals("application/xml",resultsHandle.getMimetype());
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);

		// release client
		client.release();		
	}

	@Test	
	public void test1RawCombinedQueryXML() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryXML");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOption.xml");

		String combinedQuery = convertFileToString(file);

		// create a handle for the search criteria
		StringHandle rawHandle = new StringHandle(combinedQuery);
		//FileHandle rawHandle = new FileHandle(file); // bug 21107
		//rawHandle.setMimetype("application/xml");

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

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
	public void test2RawCombinedQueryXMLWithOptions() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryXMLWithOptions");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "valueConstraintWithoutIndexSettingsAndNSOpt.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.setQueryValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}

		setQueryOption(client, queryOptionName);

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryNoOption.xml");

		// create a handle for the search criteria
		FileHandle rawHandle = new FileHandle(file);

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle, queryOptionName);

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
	public void test3RawCombinedQueryXMLWithOverwriteOptions() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryXMLWithOverwriteOptions");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "valueConstraintWithoutIndexSettingsAndNSOpt.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}

		setQueryOption(client, queryOptionName);

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionCollectionOverwrite.xml");

		// create a handle for the search criteria
		FileHandle rawHandle = new FileHandle(file);

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle, queryOptionName);

		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();

		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);

		// release client
		client.release();		
	}

	@Test	
	public void test4RawCombinedQueryJSONWithOverwriteOptions() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryJSONWithOverwriteOptions");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "valueConstraintWithoutIndexSettingsAndNSOpt.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}

		setQueryOption(client, queryOptionName);

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionJSONOverwrite.json");

		String combinedQuery = convertFileToString(file);

		// create a handle for the search criteria
		StringHandle rawHandle = new StringHandle(combinedQuery);
		//FileHandle rawHandle = new FileHandle(file);
		//rawHandle.setMimetype("application/xml");
		rawHandle.setFormat(Format.JSON);

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle, queryOptionName);

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
	public void test5RawCombinedQueryJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryJSON");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionJSON.json");

		String combinedQuery = convertFileToString(file);

		// create a handle for the search criteria
		StringHandle rawHandle = new StringHandle(combinedQuery);
		//FileHandle rawHandle = new FileHandle(file);
		//rawHandle.setMimetype("application/xml");
		rawHandle.setFormat(Format.JSON);

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

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
	public void test6RawCombinedQueryWildcard() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryWildcard");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionWildcard.xml");

		String combinedQuery = convertFileToString(file);

		// create a handle for the search criteria
		StringHandle rawHandle = new StringHandle(combinedQuery);
		//FileHandle rawHandle = new FileHandle(file); // bug 21107
		//rawHandle.setMimetype("application/xml");

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

		// create result handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();

		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);

		// release client
		client.release();		
	}

	@Test	
	public void test7RawCombinedQueryCollection() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryCollection");

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
		RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

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
	public void test8RawCombinedQueryCombo() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryCombo");

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
		RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

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
	public void test9RawCombinedQueryField() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryField");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/field-constraint/", "XML");
		}

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionField.xml");

		// create a handle for the search criteria
		FileHandle rawHandle = new FileHandle(file); // bug 21107

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

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
	public void test10RawCombinedQueryPathIndex() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryPathIndex");

		String[] filenames = {"pathindex1.xml", "pathindex2.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/path-index-raw/", "XML");
		}		

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionPathIndex.xml");

		// create a handle for the search criteria
		FileHandle rawHandle = new FileHandle(file); // bug 21107

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

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

	@Test	
	public void test11RawCombinedQueryComboJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryComboJSON");

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

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.setQueryValidation(false);
		srvMgr.writeConfiguration();

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionComboJSON.json");

		String combinedQuery = convertFileToString(file);

		// create a handle for the search criteria
		StringHandle rawHandle = new StringHandle(combinedQuery);
		rawHandle.setFormat(Format.JSON);

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

		// create result handle
		StringHandle resultsHandle = new StringHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		String resultDoc = resultsHandle.get();

		System.out.println(resultDoc);
		assertTrue("Returned result is not correct", resultDoc.contains("<search:result index=\"1\" uri=\"/collection-constraint/constraint1.xml\" path=\"fn:doc(&quot;/collection-constraint/constraint1.xml&quot;)\" score=\"28672\" confidence=\"0.6951694\" fitness=\"0.9213213\" href=\"/v1/documents?uri=%2Fcollection-constraint%2Fconstraint1.xml\" mimetype=\"application/xml\" format=\"xml\">"));

		// release client
		client.release();		
	}

	@Test	
	public void test12RawCombinedQueryFieldJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawCombinedQueryFieldJSON");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/field-constraint/", "XML");
		}

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.setQueryValidation(false);
		srvMgr.writeConfiguration();

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryOptionFieldJSON.json");

		String combinedQuery = convertFileToString(file);

		// create a handle for the search criteria
		StringHandle rawHandle = new StringHandle(combinedQuery);
		rawHandle.setFormat(Format.JSON);		
		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);

		// create result handle
		StringHandle resultsHandle = new StringHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		String resultDoc = resultsHandle.get();

		System.out.println(resultDoc);

		assertTrue("total document returned is incorrect", resultDoc.contains("total=\"2\""));
		assertTrue("returned doc is incorrect", resultDoc.contains("uri=\"/field-constraint/constraint5.xml\""));
		assertTrue("returned doc is incorrect", resultDoc.contains("uri=\"/field-constraint/constraint1.xml\""));

		// release client
		client.release();		
	}
	
	/*
	 * When searching using a structured query, the <search:metrics/> element in the response used to include 
	 * an empty <search:extract-resolution-time/>
	 * This test makes sure that the response does not have 
	 * <search:extract-resolution-time>
	 * element
	 */
	
	@Test	
	public void testBug43940() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		// Negative test
		System.out.println("Running testBug43940");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
		}

		// get the combined query
		File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/combinedQueryMetricsTrue.xml");

		String combinedQuery = convertFileToString(file);

		// create a handle for the search criteria
		StringHandle rawHandle = new StringHandle(combinedQuery);

		QueryManager queryMgr = client.newQueryManager();

		// create a search definition based on the handle
		RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);
		queryMgr.newStringDefinition("LinkResultDocumentsOpt.xml");

		// create result handle
		JacksonHandle resultsHandle = new JacksonHandle();
		queryMgr.search(querydef, resultsHandle);

		// get the result
		//Document resultDoc = resultsHandle.get();
		JsonNode resultNode = resultsHandle.get().get("metrics");
		System.out.println("Mime Type : "+resultsHandle.getMimetype());
		System.out.println(resultNode);
		// Make sure response does not have extract-resolution-time property in the metrics
		assertNull(resultNode.get("extract-resolution-time"));
		// End of Negative test
		
		// Start of positive tests
		
		
		//End of positive tests
		
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
