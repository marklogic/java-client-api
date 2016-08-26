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

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.DigestAuthContext;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.ExportablePlan;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.expression.PlanBuilder.QualifiedPlan;
import com.marklogic.client.expression.PlanBuilder.ViewPlan;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.type.PlanColumnSeq;
import com.marklogic.client.type.PlanJoinKeySeq;
import com.marklogic.client.type.PlanSortKeySeq;
import com.marklogic.client.type.PlanTriplePattern;
import com.marklogic.client.type.CtsReferenceExpr;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.type.XsStringParam;
import com.marklogic.client.type.XsStringVal;

public class TestOpticPlanBuilder extends BasicJavaClientREST {
	
	private static String dbName = "TestOpticDB";
	private static String modulesdbName = "TesOpticModulesDB";
	private static String [] fNames = {"TestOpticFunctionalDB-1"};
	private static String [] modulesfNames = {"TesOpticModulesDB-1"};
	
	private static int restPort=8011;
	private static DatabaseClient client;
	private static DatabaseClient writeclient;
	private static DatabaseClient readclient;
	private static DatabaseClient modulesclient;
	
	private static String newline;
	private static String datasource = "src/test/java/com/marklogic/client/functionaltest/data/optics/";
	

	@BeforeClass
	public static void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception
	{
		System.out.println("In TestOpticPlanBuilder setup");
		
		newline = System.getProperty("line.separator");
		
		
		
		//System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		configureRESTServer(dbName, fNames);
		
		// Add new range elements into this array
		String[][] rangeElements = {
				//{ scalar-type, namespace-uri, localname, collation, range-value-positions, invalid-values }
				// If there is a need to add additional fields, then add them to the end of each array
				// and pass empty strings ("") into an array where the additional field does not have a value.
				// For example : as in namespace, collections below.
				// Add new RangeElementIndex as an array below.
				{"string", "", "city", "http://marklogic.com/collation/", "false", "reject"},				
				{"int", "", "popularity", "", "false", "reject"},
				{"double", "", "distance", "", "false", "reject"},
				{"date", "", "date", "", "false", "reject"},
				{"string", "", "cityName", "http://marklogic.com/collation/", "false", "reject"},
				{"string", "", "cityTeam", "http://marklogic.com/collation/", "false", "reject"},
				{ "long", "", "cityPopulation", "", "false", "reject"}
		};

		
		// Insert the range indices		
		addRangeElementIndex(dbName, rangeElements);
		
		// Insert word lexicon.
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode mainNode = mapper.createObjectNode();
		ObjectNode wordLexicon = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();
		
		childNodeObject.put("namespace-uri", "http://marklogic.com/collation/");
		childNodeObject.put("localname", "city");
		childNodeObject.put("collation", "http://marklogic.com/collation/");
		childArray.add(childNodeObject);
		mainNode.withArray("element-word-lexicon").add(childArray);
				
		setDatabaseProperties(dbName,"element-word-lexicon", mainNode);
		
		// Add geo element index.
		addGeospatialElementIndexes(dbName,"latLonPoint","","wgs84","point",false,"reject");
		//Enable triple index.
		enableTripleIndex(dbName);
		waitForServerRestart();
		// Enable collection lexicon.
		enableCollectionLexicon(dbName);
		// Enable uri lexicon.
		setDatabaseProperties(dbName,"uri-lexicon",true );
		//Set the same database as the Schema database. "Schemas" have authorization issues - TBD later
		setDatabaseProperties(dbName, "schema-database", dbName);
				
		//You can enable the triple positions index for faster near searches using cts:triple-range-query.
		writeclient = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), new DigestAuthContext("rest-writer", "x"));		
		readclient = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), new DigestAuthContext("rest-reader", "x"));
		client = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), new DigestAuthContext("admin", "admin") );
		
		// Install the TDE templates
		// loadFileToDB(client, filename, docURI, collection, document format)
		loadFileToDB(client, "masterDetail.tdex", "/optic/view/test/masterDetail.tdex", "XML", new String[] {"http://marklogic.com/xdmp/tde"});
		loadFileToDB(client, "masterDetail2.tdej", "/optic/view/test/masterDetail2.tdej", "JSON",  new String[] {"http://marklogic.com/xdmp/tde"});
		loadFileToDB(client, "masterDetail3.tdej", "/optic/view/test/masterDetail3.tdej", "JSON",  new String[] {"http://marklogic.com/xdmp/tde"});
		
		// Load XML data files.
		loadFileToDB(client, "masterDetail.xml", "/optic/view/test/masterDetail.xml", "XML",  new String[] {"/optic/view/test"});
		loadFileToDB(client, "playerTripleSet.xml", "/optic/triple/test/playerTripleSet.xml", "XML",  new String[] {"/optic/player/triple/test"});
		loadFileToDB(client, "teamTripleSet.xml", "/optic/triple/test/teamTripleSet.xml", "XML",  new String[] {"/optic/team/triple/test"});
		loadFileToDB(client, "otherPlayerTripleSet.xml", "/optic/triple/test/otherPlayerTripleSet.xml", "XML",  new String[] {"/optic/other/player/triple/test"});
		loadFileToDB(client, "doc4.xml", "/optic/lexicon/test/doc4.xml", "XML",  new String[] {"/optic/lexicon/test"});
		loadFileToDB(client, "doc5.xml", "/optic/lexicon/test/doc5.xml", "XML",  new String[] {"/optic/lexicon/test"});
		
		// Load JSON data files.
		loadFileToDB(client, "masterDetail2.json", "/optic/view/test/masterDetail2.json", "JSON",  new String[] {"/optic/view/test"});
		loadFileToDB(client, "masterDetail3.json", "/optic/view/test/masterDetail3.json", "JSON",  new String[] {"/optic/view/test"});
		
		loadFileToDB(client, "doc1.json", "/optic/lexicon/test/doc1.json", "JSON",  new String[] {"/other/coll1", "/other/coll2"});
		loadFileToDB(client, "doc2.json", "/optic/lexicon/test/doc2.json", "JSON",  new String[] {"/optic/lexicon/test"});
		loadFileToDB(client, "doc3.json", "/optic/lexicon/test/doc3.json", "JSON",  new String[] {"/optic/lexicon/test"});
		
		loadFileToDB(client, "city1.json", "/optic/lexicon/test/city1.json", "JSON",  new String[] {"/optic/lexicon/test"});
		loadFileToDB(client, "city2.json", "/optic/lexicon/test/city2.json", "JSON",  new String[] {"/optic/lexicon/test"});
		loadFileToDB(client, "city3.json", "/optic/lexicon/test/city3.json", "JSON",  new String[] {"/optic/lexicon/test"});
		loadFileToDB(client, "city4.json", "/optic/lexicon/test/city4.json", "JSON",  new String[] {"/optic/lexicon/test"});
		loadFileToDB(client, "city5.json", "/optic/lexicon/test/city5.json", "JSON",  new String[] {"/optic/lexicon/test"});
	
	}
	
	
	/**
	 * Write document using DOMHandle
	 * @param client
	 * @param filename
	 * @param uri
	 * @param type
	 * @throws IOException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	
	public static void loadFileToDB(DatabaseClient client, String filename, String uri, String type, String[] collections) throws IOException, ParserConfigurationException, SAXException
	{   
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentMgrSelector(client, docMgr, type);
				
		File file = new File(datasource + filename);
	    // create a handle on the content
	    FileHandle handle = new FileHandle(file);
	    handle.set(file);
	    
	    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
	    for(String coll:collections)
		metadataHandle.getCollections().addAll(coll.toString());
	    
	    // write the document content
		DocumentWriteSet writeset = docMgr.newWriteSet();
		writeset.addDefault(metadataHandle);
		writeset.add(uri, handle);
    
	    docMgr.write(writeset);
	    
	    System.out.println("Write " + uri + " to database");	
	}
	
	/**
	 * Function to select and create document manager based on the type
	 * @param client
	 * @param docMgr
	 * @param type
	 * @return
	 */
	public static DocumentManager documentMgrSelector(DatabaseClient client, DocumentManager docMgr, String type) {
		// create doc manager
		switch(type) {
		case "XML" :
			docMgr = client.newXMLDocumentManager();
			break;
		case "Text" :
			docMgr = client.newTextDocumentManager();
			break;
		case "JSON" :
			docMgr = client.newJSONDocumentManager();
			break;
		case "Binary" :
			docMgr = client.newBinaryDocumentManager();
			break;
		case "JAXB" :
			docMgr = client.newXMLDocumentManager();
			break;
		default :
			System.out.println("Invalid type");
			break;
		}
		return docMgr;
	}
	
	// VIEWS START
	
	/* This test checks a simple Schema and View ordered by id.
	 * 
	 * The query should be returning 6 results ordered by id. Test asserts only on the first node results.
     * Uses JacksonHandle
	 * 
	 */
	@Test
	public void testNamedSchemaAndView() throws KeyManagementException, NoSuchAlgorithmException, IOException,  SAXException, ParserConfigurationException
	{	
		System.out.println("In testNamedSchemaAndView method");
		
		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		
		ViewPlan plan = p.fromView("opticFunctionalTest", "detail");
		plan.orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		
		rowMgr.resultDoc(plan, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		JsonNode jsonBindingsNodes = jsonResults.path("results").path("bindings");
		
		// Should have 6 nodes returned.
		assertEquals("Six nodes not returned from testNamedSchemaAndView method ", 6, jsonBindingsNodes.size());
		
		// Verify first node.
		Iterator<JsonNode>  nameNodesItr = jsonBindingsNodes.elements();
		JsonNode jsonNameNode = null;
		if(nameNodesItr.hasNext()) {			
			jsonNameNode = nameNodesItr.next();					
			// Verify result 1's values.
			assertEquals("Element 1 opticFunctionalTest.detail.id datatype value incorrect", "http://www.w3.org/2001/XMLSchema#int", jsonNameNode.path("opticFunctionalTest.detail.id").path("datatype").asText());
			assertEquals("Element 1 opticFunctionalTest.detail.id type is incorrect", "literal", jsonNameNode.path("opticFunctionalTest.detail.id").path("type").asText());		
			assertEquals("Element 1 opticFunctionalTest.detail.id value is incorrect", "1", jsonNameNode.path("opticFunctionalTest.detail.id").path("value").asText());
				
			assertEquals("Element 1 opticFunctionalTest.detail.name type is incorrect", "literal", jsonNameNode.path("opticFunctionalTest.detail.name").path("type").asText());		
			assertEquals("Element 1 opticFunctionalTest.detail.name value is incorrect", "Detail 1", jsonNameNode.path("opticFunctionalTest.detail.name").path("value").asText());
			
			assertEquals("Element 1 opticFunctionalTest.detail.masterId datatype value incorrect", "http://www.w3.org/2001/XMLSchema#int", jsonNameNode.path("opticFunctionalTest.detail.masterId").path("datatype").asText());
			assertEquals("Element 1 opticFunctionalTest.detail.masterId type is incorrect", "literal", jsonNameNode.path("opticFunctionalTest.detail.masterId").path("type").asText());		
			assertEquals("Element 1 opticFunctionalTest.detail.masterId value is incorrect", "1", jsonNameNode.path("opticFunctionalTest.detail.masterId").path("value").asText());
			
			assertEquals("Element 1 opticFunctionalTest.detail.amount datatype value incorrect", "http://www.w3.org/2001/XMLSchema#double", jsonNameNode.path("opticFunctionalTest.detail.amount").path("datatype").asText());
			assertEquals("Element 1 opticFunctionalTest.detail.amount type is incorrect", "literal", jsonNameNode.path("opticFunctionalTest.detail.amount").path("type").asText());		
			assertEquals("Element 1 opticFunctionalTest.detail.amount value is incorrect", "10.01", jsonNameNode.path("opticFunctionalTest.detail.amount").path("value").asText());
			
			assertEquals("Element 1 opticFunctionalTest.detail.color type is incorrect", "literal", jsonNameNode.path("opticFunctionalTest.detail.color").path("type").asText());		
			assertEquals("Element 1 opticFunctionalTest.detail.color value is incorrect", "blue", jsonNameNode.path("opticFunctionalTest.detail.color").path("value").asText());
			// Verify only the name value of other nodes in the array results.
			jsonNameNode = nameNodesItr.next();	
			assertEquals("Element 2 opticFunctionalTest.detail.name value is incorrect", "Detail 2", jsonNameNode.path("opticFunctionalTest.detail.name").path("value").asText());
		
			jsonNameNode = nameNodesItr.next();	
			assertEquals("Element 3 opticFunctionalTest.detail.name value is incorrect", "Detail 3", jsonNameNode.path("opticFunctionalTest.detail.name").path("value").asText());
			jsonNameNode = nameNodesItr.next();	
			assertEquals("Element 4 opticFunctionalTest.detail.name value is incorrect", "Detail 4", jsonNameNode.path("opticFunctionalTest.detail.name").path("value").asText());
			jsonNameNode = nameNodesItr.next();	
			assertEquals("Element 5 opticFunctionalTest.detail.name value is incorrect", "Detail 5", jsonNameNode.path("opticFunctionalTest.detail.name").path("value").asText());
			jsonNameNode = nameNodesItr.next();	
			assertEquals("Element 6 opticFunctionalTest.detail.name value is incorrect", "Detail 6", jsonNameNode.path("opticFunctionalTest.detail.name").path("value").asText());}
	}
	
	/* This test checks a simple Schema and view with a qualifier ordered by id.
	 * 
	 * The query should be returning 6 results ordered by id. Test asserts only on the first node results.
     * Uses JacksonHandle
	 */
	@Test
	public void testNamedSchemaViewWithQualifier() throws KeyManagementException, NoSuchAlgorithmException, IOException,  SAXException, ParserConfigurationException
	{	
		System.out.println("In testNamedSchemaViewWithQualifier method");
		
		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
				
		ViewPlan plan = p.fromView("opticFunctionalTest", "detail", "MarkLogicQAQualifier" );
		plan.orderBy(p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.masterId"), 
				     p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.color"),
				     p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.amount"));
		
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		
		rowMgr.resultDoc(plan, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		JsonNode jsonBindingsNodes = jsonResults.path("results").path("bindings");
		
		// Should have 2 nodes returned.
		assertEquals("Six nodes not returned from testNamedSchemaViewWithQualifier method ", 6, jsonBindingsNodes.size());
	}
	
	/* This test checks plan builder with invlid Schema and view names.
	 * 
	 * 
	 */
	@Test
	public void testInvalidNamedSchemaView() throws KeyManagementException, NoSuchAlgorithmException, IOException,  SAXException, ParserConfigurationException
	{	
		System.out.println("In testInvalidNamedSchemaView method");
		
		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		
		// Verify for invalid schema name
		ViewPlan planInvalidSchema = p.fromView("opticFunctionalTestInvalid", "detail", "MarkLogicQAQualifier" );
		planInvalidSchema.orderBy("opticFunctionalTest", "detail","id");
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		String exceptionSch = null;
		try {
			rowMgr.resultDoc(planInvalidSchema, jacksonHandle);
		}
		catch (Exception ex) {
			exceptionSch = ex.getMessage();
		}
		assertTrue("Exception not thrown or invalid message", exceptionSch.contains("Internal Server Error. Server Message: SQL-TABLENOTFOUND") &&
				exceptionSch.contains("Unknown table: Table 'opticFunctionalTestInvalid.detail' not found"));
		
		// Verify for invalid view name
		ViewPlan planInvalidView = p.fromView("opticFunctionalTest", "detailInvalid", "MarkLogicQAQualifier" );
		planInvalidView.orderBy("opticFunctionalTest", "detail","id");
		
		jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		String exceptionVw = null;
		try {
		rowMgr.resultDoc(planInvalidView, jacksonHandle);
		}
		catch (Exception ex) {
			exceptionVw = ex.getMessage();
		}
		assertTrue("Exception not thrown or invalid message", exceptionVw.contains("Internal Server Error. Server Message: SQL-TABLENOTFOUND") &&
				exceptionVw.contains("Unknown table: Table 'opticFunctionalTest.detailInvalid' not found"));
		
		// Verify for empty view name
		ViewPlan planEmptyView = p.fromView("opticFunctionalTest", "", "MarkLogicQAQualifier" );
		planInvalidView.orderBy("opticFunctionalTest", "detail","id");

		jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		String exceptionNoView = null;
		try {
			rowMgr.resultDoc(planEmptyView, jacksonHandle);
		}
		catch (Exception ex) {
			exceptionNoView = ex.getMessage();
		}
		assertTrue("Exception not thrown or invalid message", exceptionNoView.contains("OPTIC-INVALARGS") &&
				exceptionNoView.contains("Invalid arguments: cannot specify fromView() without view name"));
		
		// Verify for empty Schma name
		ViewPlan planEmptySch = p.fromView("", "detail", "MarkLogicQAQualifier" );
		planInvalidView.orderBy("opticFunctionalTest", "detail","id");

		jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		String exceptionNoySch = null;
		try {
			rowMgr.resultDoc(planEmptySch, jacksonHandle);
		}
		catch (Exception ex) {
			exceptionNoySch = ex.getMessage();
		}
		assertTrue("Exception not thrown or invalid message", exceptionNoySch.contains("OPTIC-INVALARGS") &&
				exceptionNoySch.contains("Invalid arguments: cannot specify fromView() with invalid schema name"));
	}
		
	/* This test checks group by with a view.
	 * Should retrun 3 items.
	 * Tested arrayAggregate, union, groupby methods
	 * Uses schemaCol for Columns selection
	 *.
	 * 
	 */
	@Test
	public void testGroupByFromView() throws KeyManagementException, NoSuchAlgorithmException, IOException,  SAXException, ParserConfigurationException
	{	
		System.out.println("In testGroupByFromView method");
		
		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		
		ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
				            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
		      
		ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
	            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
		ModifyPlan plan3 = plan1.union(plan2)
		     .select(p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
		    		 p.schemaCol("opticFunctionalTest", "master", "date"),
		    		 p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
		    		 p.schemaCol("opticFunctionalTest", "detail", "amount"),
		    		 p.schemaCol("opticFunctionalTest", "detail", "color")
		    		 )
		    .groupBy(p.col("MasterName"), p.arrayAggregate("arrayDetail", "DetailName"))
		    .orderBy(p.desc(p.col("MasterName")));
		 		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		
		rowMgr.resultDoc(plan3, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		JsonNode jsonBindingsNodes = jsonResults.path("results").path("bindings");
			
		// Should have 3 nodes returned.
		assertEquals("Three nodes not returned from testGroupByFromView method ", 3, jsonBindingsNodes.size());
		assertEquals("Element 1 testGroupByFromView MasterName value incorrect", "Master 2", jsonBindingsNodes.get(0).path("MasterName").path("value").asText());
		assertEquals("Element 1 testGroupByFromView arrayDetail size incorrect", 0, jsonBindingsNodes.get(0).path("arrayDetail").path("value").size());
		assertEquals("Element 2 testGroupByFromView MasterName value incorrect", "Master 1", jsonBindingsNodes.get(1).path("MasterName").path("value").asText());
		assertEquals("Element 2 testGroupByFromView arrayDetail size incorrect", 0, jsonBindingsNodes.get(1).path("arrayDetail").path("value").size());
		assertEquals("Element 3 testGroupByFromView arrayDetail size incorrect", 6, jsonBindingsNodes.get(2).path("arrayDetail").path("value").size());
		// Verify arrayDetail array
		assertEquals("Element 3 testGroupByFromView arrayDetail value at index 1 incorrect", "Detail 1", jsonBindingsNodes.get(2).path("arrayDetail").path("value").get(0).asText());
		assertEquals("Element 3 testGroupByFromView arrayDetail value at index 2 incorrect", "Detail 2", jsonBindingsNodes.get(2).path("arrayDetail").path("value").get(1).asText());
		assertEquals("Element 3 testGroupByFromView arrayDetail value at index 6 incorrect", "Detail 6", jsonBindingsNodes.get(2).path("arrayDetail").path("value").get(5).asText());
	}
	
	/* This test checks joinInner(), on(), offset() orderBy() desc(),limits(), RoeSet and RowRecord iterator with a view.
	 * Should retrun 3 items.
	 * Uses schemaCol and viewCol for Columns selection
	 * 
	 */
	@Test
	public void testjoinInnerOffsetAndLimitFromView() throws KeyManagementException, NoSuchAlgorithmException, IOException,  SAXException, ParserConfigurationException
	{	
		System.out.println("In testjoinInnerOffsetAndLimitFromView method");
		
		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		ExportablePlan plan1 = p.fromView("opticFunctionalTest", "detail")
				                .joinInner(p.fromView("opticFunctionalTest", "master"), 
						                   p.on(p.schemaCol("opticFunctionalTest", "detail", "masterId"), p.schemaCol("opticFunctionalTest", "master","id"))
						                  )
				                 .select(p.schemaCol("opticFunctionalTest", "detail", "id"), 
						                 p.schemaCol("opticFunctionalTest", "detail", "name"), 
						                 p.schemaCol("opticFunctionalTest", "master", "id"), 
						                 p.schemaCol("opticFunctionalTest", "master", "name")
						                 )
				                 .orderBy(p.desc(p.schemaCol("opticFunctionalTest", "detail", "name")))
				                 .offset(1)
				                 .limit(3);
				
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		
		rowMgr.resultDoc(plan1, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		JsonNode jsonBindingsNodes = jsonResults.path("results").path("bindings");
			
		// Should have 3 nodes returned.
		assertEquals("Three nodes not returned from testjoinInnerOffsetAndLimitFromView method ", 3, jsonBindingsNodes.size());

		// Verify first node.
		Iterator<JsonNode>  nameNodesItr = jsonBindingsNodes.elements();
		JsonNode jsonNameNode = null;
		if(nameNodesItr.hasNext()) {			
			jsonNameNode = nameNodesItr.next();					
			// Verify result 1's values.
			assertEquals("Element 1 opticFunctionalTest.detail.id value incorrect", "5", jsonNameNode.path("opticFunctionalTest.detail.id").path("value").asText());
			assertEquals("Element 1 opticFunctionalTest.detail.name value is incorrect", "Detail 5", jsonNameNode.path("opticFunctionalTest.detail.name").path("value").asText());		
			assertEquals("Element 1 opticFunctionalTest.master.id value incorrect", "1", jsonNameNode.path("opticFunctionalTest.master.id").path("value").asText());
			assertEquals("Element 1 opticFunctionalTest.master.name value is incorrect", "Master 1", jsonNameNode.path("opticFunctionalTest.master.name").path("value").asText());

			// Second node
			jsonNameNode = nameNodesItr.next();
			assertEquals("Element 2 opticFunctionalTest.detail.id value incorrect", "4", jsonNameNode.path("opticFunctionalTest.detail.id").path("value").asText());
			assertEquals("Element 2 opticFunctionalTest.detail.name value is incorrect", "Detail 4", jsonNameNode.path("opticFunctionalTest.detail.name").path("value").asText());		
			assertEquals("Element 2 opticFunctionalTest.master.id value incorrect", "2", jsonNameNode.path("opticFunctionalTest.master.id").path("value").asText());
			assertEquals("Element 2 opticFunctionalTest.master.name value is incorrect", "Master 2", jsonNameNode.path("opticFunctionalTest.master.name").path("value").asText());

			// Third node
			jsonNameNode = nameNodesItr.next();
			assertEquals("Element 3 opticFunctionalTest.detail.id value incorrect", "3", jsonNameNode.path("opticFunctionalTest.detail.id").path("value").asText());
			assertEquals("Element 3 opticFunctionalTest.detail.name value is incorrect", "Detail 3", jsonNameNode.path("opticFunctionalTest.detail.name").path("value").asText());		
			assertEquals("Element 3 opticFunctionalTest.master.id value incorrect", "1", jsonNameNode.path("opticFunctionalTest.master.id").path("value").asText());
			assertEquals("Element 3 opticFunctionalTest.master.name value is incorrect", "Master 1", jsonNameNode.path("opticFunctionalTest.master.name").path("value").asText());

		}
		else {
			fail("Could not traverse the three nodes in testjoinInnerOffsetAndLimitFromView method");
		}
		
		//Verify using viewCol() the same plan.
		ExportablePlan plan2 = p.fromView("opticFunctionalTest", "detail", "qadetail")
                .joinInner(p.fromView("opticFunctionalTest", "master", "qamaster"), 
		                   p.on(p.viewCol("qadetail", "masterId"), p.viewCol("qamaster", "id"))
		                  )
                 .select(p.viewCol("qadetail", "id"), 
		                 p.viewCol("qadetail", "name"), 
		                 p.viewCol("qamaster", "id"), 
		                 p.viewCol("qamaster", "name")
		                 )
                 .orderBy(p.desc(p.viewCol("qadetail", "name")))
                 .offset(1)
                 .limit(3);

		JacksonHandle jacksonVColHandle = new JacksonHandle();
		jacksonVColHandle.setMimetype("application/json");

		rowMgr.resultDoc(plan2, jacksonVColHandle);
		JsonNode jsonVColResults = jacksonVColHandle.get();
		JsonNode jsonVColBindingsNodes = jsonVColResults.path("results").path("bindings");
		// Verify first node.
		nameNodesItr = jsonVColBindingsNodes.elements();
		
		if(nameNodesItr.hasNext()) {			
			jsonNameNode = nameNodesItr.next();					
			// Verify result 1's values.
			assertEquals("Element 1 opticFunctionalTest.detail.id value incorrect", "5", jsonNameNode.path("qadetail.id").path("value").asText());
			assertEquals("Element 1 opticFunctionalTest.detail.name value is incorrect", "Detail 5", jsonNameNode.path("qadetail.name").path("value").asText());		
			assertEquals("Element 1 opticFunctionalTest.master.id value incorrect", "1", jsonNameNode.path("qamaster.id").path("value").asText());
			assertEquals("Element 1 opticFunctionalTest.master.name value is incorrect", "Master 1", jsonNameNode.path("qamaster.name").path("value").asText());

			// Second node
			jsonNameNode = nameNodesItr.next();
			assertEquals("Element 2 opticFunctionalTest.detail.id value incorrect", "4", jsonNameNode.path("qadetail.id").path("value").asText());
			assertEquals("Element 2 opticFunctionalTest.detail.name value is incorrect", "Detail 4", jsonNameNode.path("qadetail.name").path("value").asText());		
			assertEquals("Element 2 opticFunctionalTest.master.id value incorrect", "2", jsonNameNode.path("qamaster.id").path("value").asText());
			assertEquals("Element 2 opticFunctionalTest.master.name value is incorrect", "Master 2", jsonNameNode.path("qamaster.name").path("value").asText());

			// Third node
			jsonNameNode = nameNodesItr.next();
			assertEquals("Element 3 opticFunctionalTest.detail.id value incorrect", "3", jsonNameNode.path("qadetail.id").path("value").asText());
			assertEquals("Element 3 opticFunctionalTest.detail.name value is incorrect", "Detail 3", jsonNameNode.path("qadetail.name").path("value").asText());		
			assertEquals("Element 3 opticFunctionalTest.master.id value incorrect", "1", jsonNameNode.path("qamaster.id").path("value").asText());
			assertEquals("Element 3 opticFunctionalTest.master.name value is incorrect", "Master 1", jsonNameNode.path("qamaster.name").path("value").asText());

		}
		else {
			fail("Could not traverse the three nodes in testjoinInnerOffsetAndLimitFromView method");
		}
		
		// Verify RowSet and RowRecord.		
		RowSet<RowRecord> rowSet = rowMgr.resultRows(plan2);
	    String[] colNames = rowSet.getColumnNames();
	    Arrays.sort(colNames);
	    String[] exptdColumnNames = {"qamaster.id", "qadetail.id", "qadetail.name", "qamaster.name"};
	    Arrays.sort(exptdColumnNames);
	    // Verify if all columns are available.
	    assertTrue(Arrays.equals(colNames, exptdColumnNames));
		
		// Verify RowRecords using Iterator
		Iterator<RowRecord> rowItr = rowSet.iterator();
		
		RowRecord record = null;
		if(rowItr.hasNext()) {			
			record = rowItr.next();	
			assertEquals("Element 1 RowSet Iterator value incorrect", 1, record.getInt("qamaster.id"));
			assertEquals("Element 1 RowSet Iterator value incorrect", 5, record.getInt("qadetail.id"));
			assertEquals("Element 1 RowSet Iterator value incorrect", "Detail 5", record.getString("qadetail.name"));
			assertEquals("Element 1 RowSet Iterator value incorrect", "Master 1", record.getString("qamaster.name"));
			
			XsStringVal str = record.getValueAs("qamaster.name", XsStringVal.class);
			assertEquals("Element 1 RowSet Iterator value incorrect", "Master 1", str.getString());
		}
		else {
			fail("Could not traverse Iterator<RowRecord> in testjoinInnerOffsetAndLimitFromView method");
		}
	}
	
	/* This test checks joinLeftOuter with a view.
	 * Should retrun 12 items.
	 * Uses schemaCol and viewCol for Columns selection
	 * Processing Each Row As a Separate JSON
	 * 
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testJoinLeftOuterFromView() throws KeyManagementException, NoSuchAlgorithmException, IOException,  SAXException, ParserConfigurationException
	{	
		System.out.println("In testJoinLeftOuterFromView method");
		
		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
				                .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
		ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
                .orderBy(p.schemaCol("opticFunctionalTest", "master","id"));
		ModifyPlan plan3 = plan1.joinLeftOuter(plan2)
				.select(
				         p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")), 
		                 p.schemaCol("opticFunctionalTest", "master", "date"), 
		                 p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")), 
		                 p.schemaCol("opticFunctionalTest", "detail", "amount"), 
		                 p.schemaCol("opticFunctionalTest", "detail", "color")
				       )
				.orderBy(p.desc(p.col("DetailName")), p.desc(p.schemaCol("opticFunctionalTest", "master", "date")));
		
		RowSet<RowRecord> rowSet = rowMgr.resultRows(plan3);
		// Verify RowRecords using Iterator
		Iterator<RowRecord> rowItr = rowSet.iterator();
		
		RowRecord record = null;
		if(rowItr.hasNext()) {			
			record = rowItr.next();	
			assertEquals("Date from RowSet Iterator first node value incorrect", "2015-12-02", record.getString("opticFunctionalTest.master.date"));
			
			assertEquals("Master Name RowSet Iterator first node value incorrect", "Master 2", record.getString("MasterName"));
			assertEquals("Detail Name RowSet Iterator first node value incorrect", "Detail 6", record.getString("DetailName"));
			assertEquals("Color Name RowSet Iterator first node value incorrect", "green", record.getString("opticFunctionalTest.detail.color"));
			assertEquals(60.06, record.getDouble("opticFunctionalTest.detail.amount"), 0.00);
		}
		else {
			fail("Could not traverse Iterator<RowRecord> in testJoinLeftOuterFromView method");
		}
		
		// Using Jackson to traverse the list.
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		
		rowMgr.resultDoc(plan3, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		JsonNode jsonBindingsNodes = jsonResults.path("results").path("bindings");
			
		// Should have 12 nodes returned.
		assertEquals("Twelve nodes not returned from testJoinLeftOuterFromView method ", 12, jsonBindingsNodes.size());
		// Verify first node
		assertEquals("Element 1 MasterName value incorrect", "Master 2", jsonBindingsNodes.get(0).path("MasterName").path("value").asText());
		assertEquals("Element 1 Master Date value incorrect", "2015-12-02", jsonBindingsNodes.get(0).path("opticFunctionalTest.master.date").path("value").asText());
		assertEquals("Element 1 DetailName value incorrect", "Detail 6", jsonBindingsNodes.get(0).path("DetailName").path("value").asText());
		assertEquals(60.06, jsonBindingsNodes.get(0).path("opticFunctionalTest.detail.amount").path("value").asDouble(),0.00d);
		assertEquals("Element 1 Detail Color value incorrect", "green", jsonBindingsNodes.get(0).path("opticFunctionalTest.detail.color").path("value").asText());
		
		// Verify twelveth node
		assertEquals("Element 12 MasterName value incorrect", "Master 1", jsonBindingsNodes.get(11).path("MasterName").path("value").asText());
		assertEquals("Element 12 Master Date value incorrect", "2015-12-01", jsonBindingsNodes.get(11).path("opticFunctionalTest.master.date").path("value").asText());
		assertEquals("Element 12 DetailName value incorrect", "Detail 1", jsonBindingsNodes.get(11).path("DetailName").path("value").asText());
		assertEquals(10.01, jsonBindingsNodes.get(11).path("opticFunctionalTest.detail.amount").path("value").asDouble(),0.00d);
		assertEquals("Element 12 Detail Color value incorrect", "blue", jsonBindingsNodes.get(11).path("opticFunctionalTest.detail.color").path("value").asText());		
	}
	
	/* This test checks union and except with a view.
	 * Should return 8 items.
	 * Uses schemaCol for Columns selection
	 * 
	 */
	@Test
	public void testUnionFromView() throws KeyManagementException, NoSuchAlgorithmException, IOException,  SAXException, ParserConfigurationException
	{	
		System.out.println("In testUnionFromView method");
		
		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		
		ExportablePlan plan1 = p.fromView("opticFunctionalTest2", "detail")
				                .union(p.fromView("opticFunctionalTest", "detail"))
				                .select(
				                		p.as("unionId", p.schemaCol("opticFunctionalTest2", "detail", "id")),
				                		p.as("unionId", p.schemaCol("opticFunctionalTest", "detail", "id"))
				                	   )
				                	   
				                 .except(p.fromView("opticFunctionalTest", "master")
				                		  .union(p.fromView("opticFunctionalTest2", "master"))
				                		  .select(
				                				  p.as("unionId", p.schemaCol("opticFunctionalTest", "master", "id")),
							                	  p.as("unionId", p.schemaCol("opticFunctionalTest2", "master", "id"))
				                		         ))
				                .orderBy("unionId");	
				
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		
		rowMgr.resultDoc(plan1, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		JsonNode jsonBindingsNodes = jsonResults.path("results").path("bindings");
			
		// Should have 8 nodes returned.
		assertEquals("Eight nodes not returned from testUnionFromView method ", 8, jsonBindingsNodes.size());
		assertEquals("Element 1 union id value incorrect", "5", jsonBindingsNodes.get(0).path("unionId").path("value").asText());
		assertEquals("Element 1 union id value incorrect", "6", jsonBindingsNodes.get(1).path("unionId").path("value").asText());
		assertEquals("Element 1 union id value incorrect", "7", jsonBindingsNodes.get(2).path("unionId").path("value").asText());
		assertEquals("Element 1 union id value incorrect", "8", jsonBindingsNodes.get(3).path("unionId").path("value").asText());
		assertEquals("Element 1 union id value incorrect", "9", jsonBindingsNodes.get(4).path("unionId").path("value").asText());
		
		assertEquals("Element 1 union id value incorrect", "12", jsonBindingsNodes.get(7).path("unionId").path("value").asText());
	}
	
	/* This test checks intersect on different schemas with a view.
	 * Should return 4 items.
	 * Uses schemaCol for Columns selection
	 * Methods used - intersect
	 */
	@Test
	public void testIntersectDiffSchemasFromView() throws KeyManagementException, NoSuchAlgorithmException, IOException,  SAXException, ParserConfigurationException
	{	
		System.out.println("In testIntersectDiffSchemasFromView method");
		
		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		
		ModifyPlan plan1 = p.fromView("opticFunctionalTest", "master");
		ModifyPlan plan2 = p.fromView("opticFunctionalTest2", "master");
		ModifyPlan plan3 = p.fromView("opticFunctionalTest", "detail");
		
		ModifyPlan plan4 = plan1.union(plan2)
				                .select(p.as("unionId", p.schemaCol("opticFunctionalTest", "master", "id")),
                		                p.as("unionId", p.schemaCol("opticFunctionalTest2", "master", "id"))
                	                   )
                	            .intersect(plan3.select(p.as("unionId", p.schemaCol("opticFunctionalTest", "detail", "id"))))
                	            .orderBy("unionId");	
				
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		
		rowMgr.resultDoc(plan4, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		JsonNode jsonBindingsNodes = jsonResults.path("results").path("bindings");
			
		// Should have 4 nodes returned.
		assertEquals("Four nodes not returned from testIntersectDiffSchemasFromView method ", 4, jsonBindingsNodes.size());
		assertEquals("Element 1 union id value incorrect", "1", jsonBindingsNodes.get(0).path("unionId").path("value").asText());
		assertEquals("Element 1 union id value incorrect", "2", jsonBindingsNodes.get(1).path("unionId").path("value").asText());
		assertEquals("Element 1 union id value incorrect", "3", jsonBindingsNodes.get(2).path("unionId").path("value").asText());
		assertEquals("Element 1 union id value incorrect", "4", jsonBindingsNodes.get(3).path("unionId").path("value").asText());
	}
	
	/* This test checks arithmetic operations with a view.
	 * Should retrun 6 items.
	 * Uses schemaCol for Columns selection
	 * Math fns - add, subtract, modulo, divide, multiply
	 * 
	 */
	@Test
	public void testArithmeticOperationsFromView() throws KeyManagementException, NoSuchAlgorithmException, IOException,  SAXException, ParserConfigurationException
	{	
		System.out.println("In testArithmeticOperationsFromView method");
		
		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
				            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
		ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
                            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
		ModifyPlan plan3 = plan1.joinInner(plan2)
				                .where(p.eq(p.schemaCol("opticFunctionalTest", "master" , "id"),
				                	        p.schemaCol("opticFunctionalTest", "detail", "masterId"))
				                	  )
				                .select(p.as("added", p.add(p.col("amount"), p.schemaCol("opticFunctionalTest", "detail", "masterId"))),
				                        p.as("substracted", p.subtract(p.col("amount"), p.viewCol("master", "id"))),
				                	    p.as("modulo", p.modulo(p.col("amount"), p.viewCol("master", "id"))),
				                	    p.as("invSubstract", p.subtract(p.col("amount"), p.viewCol("master", "date"))),
				                	    p.as("divided", p.divide(p.col("amount"), p.multiply(p.col("amount"), p.viewCol("detail", "id"))))
				                		)
				                .orderBy(p.asc("substracted"));
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		
		rowMgr.resultDoc(plan3, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		JsonNode jsonBindingsNodes = jsonResults.path("results").path("bindings");
			
		// Should have 6 nodes returned.
		assertEquals("Six nodes not returned from testArithmeticOperationsFromView method ", 6, jsonBindingsNodes.size());
		// Verify first node
		assertEquals(11.01, jsonBindingsNodes.get(0).path("added").path("value").asDouble(), 0.00d);
		assertEquals(9.01, jsonBindingsNodes.get(0).path("substracted").path("value").asDouble(), 0.00d);
		assertEquals(0.00999999999999979, jsonBindingsNodes.get(0).path("modulo").path("value").asDouble(), 0.00d);
		assertEquals(1, jsonBindingsNodes.get(0).path("divided").path("value").asDouble(), 0.00d);
		// Verify sixth node
		assertEquals(62.06, jsonBindingsNodes.get(5).path("added").path("value").asDouble(), 0.00d);
		assertEquals(58.06, jsonBindingsNodes.get(5).path("substracted").path("value").asDouble(), 0.00d);
		assertEquals(0.0600000000000023, jsonBindingsNodes.get(5).path("modulo").path("value").asDouble(), 0.00d);
		assertEquals(0.166666666666667, jsonBindingsNodes.get(5).path("divided").path("value").asDouble(), 0.00d);
	}
	
	/* This test checks MarkLogic buit-in function with a view.
	 * Should retrun 3 items.
	 * Uses schemaCol for Columns selection
	 * Math fns - add, subtract, modulo, divide, multiply
	 * 
	 */
	@Test
	public void testBuiltinFuncsFromView() throws KeyManagementException, NoSuchAlgorithmException, IOException,  SAXException, ParserConfigurationException
	{	
		System.out.println("In testArithmeticOperationsFromView method");
		
		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		
		double[] numbers = {10.0, 40.0, 50.0, 30.0, 60.0, 0.0, 100.0};
		ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
				            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
		ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
			                .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
		ModifyPlan plan3 = plan1.joinInner(plan2)
		     .where(p.gt(
		    		     p.schemaCol("opticFunctionalTest", "detail", "amount"), p.math.median(numbers)
                        )
                    )
             .select(p.as("myAmount", p.viewCol("detail", "amount")))
             .whereDistinct()
             .orderBy(p.asc("myAmount"));
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		
		rowMgr.resultDoc(plan3, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		JsonNode jsonBindingsNodes = jsonResults.path("results").path("bindings");
			
		// Should have 3 nodes returned.
		assertEquals("Three nodes not returned from testBuiltinFuncsFromView method ", 3, jsonBindingsNodes.size());
		// Verify nodes
		assertEquals(40.04, jsonBindingsNodes.get(0).path("myAmount").path("value").asDouble(), 0.00d);
		assertEquals(50.05, jsonBindingsNodes.get(1).path("myAmount").path("value").asDouble(), 0.00d);
		assertEquals(60.06, jsonBindingsNodes.get(2).path("myAmount").path("value").asDouble(), 0.00d);
	}
	// VIEWS END
	
	// TRIPLES START\
	@Test
	public void testfromTriples() throws KeyManagementException, NoSuchAlgorithmException, IOException,  SAXException, ParserConfigurationException
	{
		System.out.println("In testBuiltinFuncsFromView method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		ExportablePlan  plan1 = p.fromTriples(p.pattern(p.col("id"), p.sem.iri("http://marklogic.com/baseball/players/age"), p.col("age")))
				                 .orderBy(p.col("age"));
		
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		
		rowMgr.resultDoc(plan1, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		JsonNode jsonBindingsNodes = jsonResults.path("results").path("bindings");
		
		// Verify first node.
		Iterator<JsonNode>  nameNodesItr = jsonBindingsNodes.elements();
		// Should have 8 nodes returned.
		assertEquals("Eight nodes not returned from testfromTriples method ", 8, jsonBindingsNodes.size());
		JsonNode jsonNameNode = null;
		if(nameNodesItr.hasNext()) {			
			jsonNameNode = nameNodesItr.next();					
			// Verify result 1's values.
			assertEquals("Element 1 age value incorrect", "19", jsonNameNode.path("age").path("value").asText());
			// Verify the last node's age value
			assertEquals("Element 8 age value incorrect", "34",jsonBindingsNodes.get(7).path("age").path("value").asText());
		}
		else {
			fail("Could not traverse the Eight Triplesin testfromTriples method");
		}
	}
	
	
	// TRIPLES END
	

	// LEXICONS START
	/* Checks for Plan Builder's fromLexicon method.
	 * 1 plan1 uses strings as col names, with date ordered and using intval
	 * 2 plan2 use cols() on select method
	 * 3 plan3 use strings on select
	 * 
	*/
		
	
	@Test
	public void testfromLexicons() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In TestOpticPlanBuilder testfromLexicons method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		Map<String, CtsReferenceExpr>indexes = new HashMap<String, CtsReferenceExpr>();
		indexes.put("uri", p.cts.uriReference());
		indexes.put("city",  p.cts.jsonPropertyReference("city"));
		indexes.put("popularity", p.cts.jsonPropertyReference("popularity"));
		indexes.put("date",  p.cts.jsonPropertyReference("date"));
		indexes.put("distance", p.cts.jsonPropertyReference("distance"));
		indexes.put("point", p.cts.jsonPropertyReference("latLonPoint"));
		
		// plan1 - Use strings as col names, with date ordered and using intval
		ExportablePlan plan1 = p.fromLexicons(indexes)				
				                         .where(p.gt(p.col("popularity"), p.xs.intVal(2)))
				                         .orderBy(p.sortKeys("date"))
				                         .select(p.col("city"), p.col("popularity"), p.col("date"), p.col("distance"), p.col("point"));
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(plan1, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		
		JsonNode jsonBindingsNodes = jsonResults.path("results").path("bindings");
		assertTrue("Number of Elements after plan execution is incorrect. Should be 4", 4 == jsonBindingsNodes.size());
		  // Verify first node.
		Iterator<JsonNode>  nameNodesItr = jsonBindingsNodes.elements();
		
		JsonNode jsonNameNode = null;

		jsonNameNode = nameNodesItr.next();					
		// Verify result values are ordered by date. We will verify all elements here.
		assertEquals("Element 1 (city) in Column Strings incorrect", "beijing", jsonNameNode.path("city").path("value").asText());
		assertEquals("Element 1 (popularity) in Column Strings incorrect", "5", jsonNameNode.path("popularity").path("value").asText());		
		assertEquals("Element 1 (date) in Column Strings incorrect", "1981-11-09", jsonNameNode.path("date").path("value").asText());
		assertEquals("Element 1 (distance) in Column Strings incorrect", "134.5", jsonNameNode.path("distance").path("value").asText());		
		assertEquals("Element 1 (point) in Column Strings incorrect", "39.900002,116.4", jsonNameNode.path("point").path("value").asText());

		jsonNameNode = nameNodesItr.next();
		assertEquals("Element 2 (city) in Column Strings incorrect", "cape town", jsonNameNode.path("city").path("value").asText());
		assertEquals("Element 2 (popularity) in Column Strings incorrect", "3", jsonNameNode.path("popularity").path("value").asText());		
		assertEquals("Element 2 (date) in Column Strings incorrect", "1999-04-22", jsonNameNode.path("date").path("value").asText());
		assertEquals("Element 2 (distance) in Column Strings incorrect", "377.9", jsonNameNode.path("distance").path("value").asText());		
		assertEquals("Element 2 (point) in Column Strings incorrect", "-33.91,18.42", jsonNameNode.path("point").path("value").asText());

		jsonNameNode = nameNodesItr.next();
		assertEquals("Element 3 (city) in Column Strings incorrect", "new york", jsonNameNode.path("city").path("value").asText());
		assertEquals("Element 3 (popularity) in Column Strings incorrect", "5", jsonNameNode.path("popularity").path("value").asText());		
		assertEquals("Element 3 (date) in Column Strings incorrect", "2006-06-23", jsonNameNode.path("date").path("value").asText());
		assertEquals("Element 3 (distance) in Column Strings incorrect", "23.3", jsonNameNode.path("distance").path("value").asText());		
		assertEquals("Element 3 (point) in Column Strings incorrect", "40.709999,-74.009995", jsonNameNode.path("point").path("value").asText());

		jsonNameNode = nameNodesItr.next();
		assertEquals("Element 4 (city) in Column Strings incorrect", "london", jsonNameNode.path("city").path("value").asText());
		assertEquals("Element 4 (popularity) in Column Strings incorrect", "5", jsonNameNode.path("popularity").path("value").asText());		
		assertEquals("Element 4 (date) in Column Strings incorrect", "2007-01-01", jsonNameNode.path("date").path("value").asText());
		assertEquals("Element 4 (distance) in Column Strings incorrect", "50.4", jsonNameNode.path("distance").path("value").asText());		
		assertEquals("Element 4 (point) in Column Strings incorrect", "51.5,-0.12", jsonNameNode.path("point").path("value").asText());

		System.out.println("Bindings after execution of Plan 1 is" + jsonBindingsNodes);
		
		//use cols() on select
		ExportablePlan plan2 = p.fromLexicons(indexes)				
                .where(p.eq(p.col("popularity"), p.xs.intVal(5)))
                .orderBy(p.sortKeys("date"))
                .select(p.cols("city", "popularity", "date", "distance", "point"));
		
		JacksonHandle jacksonHandle2 = new JacksonHandle();
		jacksonHandle2.setMimetype("application/json");

		rowMgr.resultDoc(plan2, jacksonHandle2);
		JsonNode jsonResults2 = jacksonHandle2.get();
		
		JsonNode jsonBindingsNodes2 = jsonResults2.path("results").path("bindings");
		assertTrue("Number of Elements after plan execution is incorrect. Should be 3", 3 == jsonBindingsNodes2.size());
		
		//use strings on select
		ExportablePlan plan3 = p.fromLexicons(indexes)				
				.where(p.eq(p.col("popularity"), p.xs.intVal(5)))
				.orderBy(p.sortKeys("date"))
				.select("city", "popularity", "date", "distance", "point");

		JacksonHandle jacksonHandle3 = new JacksonHandle();
		jacksonHandle3.setMimetype("application/json");

		rowMgr.resultDoc(plan3, jacksonHandle3);
		JsonNode jsonResults3 = jacksonHandle3.get();

		JsonNode jsonBindingsNodes3 = jsonResults3.path("results").path("bindings");
		assertTrue("Number of Elements after plan execution is incorrect. Should be 3", 3 == jsonBindingsNodes3.size());	
	}
	
	
	// LEXICONS END
	
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		System.out.println("In tear down");
		// release client
		writeclient.release();
		readclient.release();
		client.release();
		cleanupRESTServer(dbName, fNames);		
	}
}
