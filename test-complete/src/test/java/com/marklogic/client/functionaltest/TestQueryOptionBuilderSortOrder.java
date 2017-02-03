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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.json.JSONException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

/*
 * All the tests in the class are being converted to negative test cases since the Git Issue 347 will not be fixed.
 * ML version 8.0-4
 * Java client API version 3.0-4
 * 08/31/2015
 */

public class TestQueryOptionBuilderSortOrder extends BasicJavaClientREST {

	private static String dbName = "TestQueryOptionBuilderSortOrderDB";
	private static String [] fNames = {"TestQueryOptionBuilderSortOrderDB-1"};
	
	
	// Additional port to test for Uber port
	private static int uberPort = 8000;

	@BeforeClass	
	public static void setUp() throws Exception {
		System.out.println("In setup");
		configureRESTServer(dbName, fNames);
		setupAppServicesConstraint(dbName);
		
		createUserRolesWithPrevilages("test-eval","xdbc:eval", "xdbc:eval-in","xdmp:eval-in","any-uri","xdbc:invoke");
	    createRESTUser("eval-user", "x", "test-eval","rest-admin","rest-writer","rest-reader");
	}

	@After
	public  void testCleanUp() throws Exception {
		clearDB();
		System.out.println("Running clear script");
	}

	@Test(expected = com.marklogic.client.FailedRequestException.class)	
	public void testSortOrderDescendingScore() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testSortOrderDescendingScore");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", uberPort, dbName,"eval-user", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/sort-desc-score/", "XML");
		}

		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// create query options
		String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
			    "<search:return-metrics>false</search:return-metrics>" +
			    "<search:return-qtext>false</search:return-qtext>" +
			    "<search:sort-order direction='descending'>" +
			        "<search:score/>" +
			    "</search:sort-order>" +
			    "<search:transform-results apply='raw'/>" +
			"</search:options>";

		// create query options handle
		StringHandle handle = new StringHandle(opts1);

		// write query options
		optionsMgr.writeOptions("SortOrderDescendingScore", handle);

		// read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.XML);
		optionsMgr.readOptions("SortOrderDescendingScore", readHandle);
		String output = readHandle.get();
		System.out.println(output);

		// create query manager
		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("SortOrderDescendingScore");
		querydef.setCriteria("bush OR memex");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();

		assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][3]//*[local-name()='id'])", resultDoc);

		// release client
		client.release();	
	}

	@Test(expected = com.marklogic.client.FailedRequestException.class)	
	public void testSortOrderPrimaryDescScoreSecondaryAscDate() throws FileNotFoundException, XpathException, TransformerException, IOException, JSONException
	{	
		System.out.println("Running testSortOrderPrimaryDescScoreSecondaryAscDate");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", uberPort, dbName, "eval-user", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/sort-desc-score-asc-date/", "XML");
		}

		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// create query options
		String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
			    "<search:return-metrics>false</search:return-metrics>" +
			    "<search:return-qtext>false</search:return-qtext>" +
			    "<search:sort-order direction='descending'>" +
			        "<search:score/>" +
			    "</search:sort-order>" +
			    "<search:sort-order direction='ascending' type='xs:date'>" +
			        "<search:element name='date' ns='http://purl.org/dc/elements/1.1/'/>" +
			    "</search:sort-order>" +
			    "<search:transform-results apply='raw'/>" +
			"</search:options>";

		// create query options handle
		StringHandle handle = new StringHandle(opts1);

		// write query options
		optionsMgr.writeOptions("SortOrderPrimaryDescScoreSecondaryAscDate", handle);

		// read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions("SortOrderPrimaryDescScoreSecondaryAscDate", readHandle);
		String output = readHandle.get();
		System.out.println(output + "testSortOrderPrimaryDescScoreSecondaryAscDate");
		String expected = "{\"options\":{\"return-metrics\":false, \"return-qtext\":false, \"sort-order\":[{\"direction\":\"descending\", \"score\":null}, {\"direction\":\"ascending\", \"type\":\"xs:date\", \"element\":{\"name\":\"date\", \"ns\":\"http://purl.org/dc/elements/1.1/\"}}], \"transform-results\":{\"apply\":\"raw\"}}}";
				
		JSONAssert.assertEquals(expected, output, false);
		
		// ----------- Validate search using options node created ----------------------
		// create query manager
		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("SortOrderPrimaryDescScoreSecondaryAscDate");
		querydef.setCriteria("bush OR memex");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();

		assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][3]//*[local-name()='id'])", resultDoc);

		// release client
		client.release();	
	}

	@Test(expected = com.marklogic.client.FailedRequestException.class)	
	public void testMultipleSortOrder() throws FileNotFoundException, XpathException, TransformerException, IOException
	{	
		System.out.println("Running testMultipleSortOrder");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", uberPort, dbName, "eval-user", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/mult-sort-order/", "XML");
		}

		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// create query options
		String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
			    "<search:return-metrics>false</search:return-metrics>" +
			    "<search:return-qtext>false</search:return-qtext>" +
			    "<search:sort-order direction='descending'>" +
			        "<search:score/>" +
			    "</search:sort-order>" +
			    "<search:sort-order direction='ascending' type='xs:int'>" +
			        "<search:element name='popularity' ns=''/>" +
			    "</search:sort-order>" +
			    "<search:sort-order direction='descending' type='xs:string'>" +
			        "<search:element name='title' ns=''/>" +
			    "</search:sort-order>" +
			    "<search:transform-results apply='raw'/>" +
			"</search:options>";

		// create query options handle
		StringHandle handle = new StringHandle(opts1);

		// write query options
		optionsMgr.writeOptions("MultipleSortOrder", handle);

		// read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions("MultipleSortOrder", readHandle);
		String output = readHandle.get();
		System.out.println(output);

		// ----------- Validate options node ----------------------
		ObjectMapper mapper = new ObjectMapper();
		JsonNode optionsContent = mapper.readTree(output);
		assertNotNull(optionsContent);
		System.out.println("JSON output: " + optionsContent);	    

		JsonNode optionsNode = optionsContent.get("options");
		assertNotNull(optionsNode);

		JsonNode metrics = optionsNode.get("return-metrics");
		assertNotNull(metrics);
		assertEquals(metrics.booleanValue(), false);

		JsonNode qtext = optionsNode.get("return-qtext");
		assertNotNull(qtext);
		assertEquals(qtext.booleanValue(), false);

		JsonNode sortOrders = optionsNode.get("sort-order");
		assertNotNull(sortOrders);
		System.out.println(optionsContent.get("options").get("sort-order"));

		assertTrue(sortOrders.isArray());
		for (final JsonNode sortOrder : sortOrders) {
			assertNotNull(sortOrder.get("direction"));
			String direction = sortOrder.get("direction").textValue();

			if (direction.equals("descending")) {
				if (sortOrder.has("score")) {
					assertTrue(sortOrder.get("score").isNull());
				}
				else if (sortOrder.has("type")) {
					assertEquals(sortOrder.get("type").textValue(), "xs:string");

					JsonNode element = sortOrder.get("element");
					assertNotNull(element);

					JsonNode elementName = element.get("name");
					assertNotNull(elementName);
					assertEquals(elementName.textValue(), "title");

					JsonNode elementNS = element.get("ns");
					assertNotNull(elementNS);
					assertEquals(elementNS.textValue(), "");
				}
				else {
					assertTrue("Found an unexpected object", false);
				}

			}
			else if (direction.equals("ascending")) {
				assertTrue(sortOrder.has("type"));
				assertEquals(sortOrder.get("type").textValue(), "xs:int");

				JsonNode element = sortOrder.get("element");
				assertNotNull(element);

				JsonNode elementName = element.get("name");
				assertNotNull(elementName);
				assertEquals(elementName.textValue(), "popularity");

				JsonNode elementNS = element.get("ns");
				assertNotNull(elementNS);
				assertEquals(elementNS.textValue(), "");
			}
			else {
				assertTrue("Found an unexpected object", false);
			}
		}

		JsonNode transformResults = optionsNode.get("transform-results");
		assertNotNull(transformResults);

		JsonNode apply = transformResults.get("apply");
		assertNotNull(apply);
		assertEquals(apply.textValue(), "raw");	

		// ----------- Search based on options node inserted ----------------------
		// create query manager
		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("MultipleSortOrder");
		querydef.setCriteria("Vannevar OR memex");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("4", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0024", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][3]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][4]//*[local-name()='id'])", resultDoc);

		// release client
		client.release();	
	} 

	@Test(expected = com.marklogic.client.FailedRequestException.class)	
	public void testSortOrderAttribute() throws FileNotFoundException, XpathException, TransformerException, IOException
	{	
		System.out.println("Running testSortOrderAttribute");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", uberPort, dbName, "eval-user", "x", Authentication.DIGEST);

		// write docs
		for(String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/attr-sort-order/", "XML");
		}

		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// create query options
		String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
			    "<search:return-metrics>false</search:return-metrics>" +
			    "<search:return-qtext>false</search:return-qtext>" +
			    "<search:sort-order direction='descending'>" +
			        "<search:score/>" +
			    "</search:sort-order>" +
			    "<search:sort-order direction='ascending' type='xs:decimal'>" +
			        "<search:attribute name='amt' ns=''/>" +
			        "<search:element name='price' ns='http://cloudbank.com'/>" +
			    "</search:sort-order>" +
			    "<search:transform-results apply='raw'/>" +
			"</search:options>";

		// create query options handle
		StringHandle handle = new StringHandle(opts1);

		// write query options
		optionsMgr.writeOptions("SortOrderAttribute", handle);

		// read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions("SortOrderAttribute", readHandle);
		String output = readHandle.get();
		System.out.println(output);

		// ----------- Validate options node ----------------------
		ObjectMapper mapper = new ObjectMapper();
		JsonNode optionsContent = mapper.readTree(output);
		assertNotNull(optionsContent);
		System.out.println("JSON output: " + optionsContent);	    

		JsonNode optionsNode = optionsContent.get("options");
		assertNotNull(optionsNode);

		JsonNode metrics = optionsNode.get("return-metrics");
		assertNotNull(metrics);
		assertEquals(metrics.booleanValue(), false);

		JsonNode qtext = optionsNode.get("return-qtext");
		assertNotNull(qtext);
		assertEquals(qtext.booleanValue(), false);

		JsonNode sortOrders = optionsNode.get("sort-order");
		assertNotNull(sortOrders);
		System.out.println(optionsContent.get("options").get("sort-order"));

		assertTrue(sortOrders.isArray());
		for (final JsonNode sortOrder : sortOrders) {
			assertNotNull(sortOrder.get("direction"));
			String direction = sortOrder.get("direction").textValue();

			if (direction.equals("descending")) {
				assertTrue(sortOrder.has("score"));
				assertTrue(sortOrder.get("score").isNull());				
			}
			else if (direction.equals("ascending")) {
				assertTrue(sortOrder.has("type"));
				assertEquals(sortOrder.get("type").textValue(), "xs:decimal");

				JsonNode attribute = sortOrder.get("attribute");
				assertNotNull(attribute);

				JsonNode attributeName = attribute.get("name");
				assertNotNull(attributeName);
				assertEquals(attributeName.textValue(), "amt");

				JsonNode attributeNS = attribute.get("ns");
				assertNotNull(attributeNS);
				assertEquals(attributeNS.textValue(), "");

				JsonNode element = sortOrder.get("element");
				assertNotNull(element);

				JsonNode elementName = element.get("name");
				assertNotNull(elementName);
				assertEquals(elementName.textValue(), "price");

				JsonNode elementNS = element.get("ns");
				assertNotNull(elementNS);
				assertEquals(elementNS.textValue(), "http://cloudbank.com");
			}
			else {
				assertTrue("Found an unexpected object", false);
			}
		}

		JsonNode transformResults = optionsNode.get("transform-results");
		assertNotNull(transformResults);

		JsonNode apply = transformResults.get("apply");
		assertNotNull(apply);
		assertEquals(apply.textValue(), "raw");	

		// ----------- Validate search using options node created ----------------------
		// create query manager
		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("SortOrderAttribute");
		querydef.setCriteria("Bush OR Memex");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][3]//*[local-name()='id'])", resultDoc);

		// release client
		client.release();	
	}

	@AfterClass	
	public static void tearDown() throws Exception {
		System.out.println("In tear down");
		cleanupRESTServer(dbName, fNames);
		deleteRESTUser("eval-user");
		deleteUserRole("test-eval");
	}
}
