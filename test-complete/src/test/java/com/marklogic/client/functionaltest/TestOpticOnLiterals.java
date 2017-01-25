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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.DigestAuthContext;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.MethodType;
import com.marklogic.client.admin.ResourceExtensionsManager;
import com.marklogic.client.admin.ExtensionMetadata.ScriptLanguage;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.ExportablePlan;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.expression.PlanBuilder.QualifiedPlan;
import com.marklogic.client.expression.PlanBuilder.ViewPlan;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.type.PlanAggregateColSeq;
import com.marklogic.client.type.PlanAggregateOptions;
import com.marklogic.client.type.PlanColumn;
import com.marklogic.client.type.PlanExprColSeq;
import com.marklogic.client.type.PlanFunction;
import com.marklogic.client.type.PlanParam;
import com.marklogic.client.type.SqlGenericDateTimeExpr;
import com.marklogic.client.type.XsDateExpr;
import com.marklogic.client.type.XsIntegerExpr;
import com.marklogic.client.type.XsTimeExpr;

public class TestOpticOnLiterals extends BasicJavaClientREST {
	
	private static String dbName = "TestOpticOnLiteralsDB";
	private static String modulesdbName = "TestOpticOnLiteralsDB";
	private static String [] fNames = {"TestOpticOnLiteralsDB-1"};
	private static String [] modulesfNames = {"TestOpticOnLiteralsModulesDB-1"};
	
	private static int restPort=8011;
	private static DatabaseClient client;	
	private static String newline;
	private static String datasource = "src/test/java/com/marklogic/client/functionaltest/data/optics/";
	private static Map<String, Object>[] literals1 = new HashMap[5];
	private static Map<String, Object>[] literals2 = new HashMap[4];
	private static Map<String, Object>[] storeInformation = new HashMap[4];
	private static Map<String, Object>[] internetSales = new HashMap[4];
	private static ResourceExtensionsManager resourceMgr;
	
	@BeforeClass
	public static void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception
	{
		System.out.println("In TestOpticOnLiterals setup");
		
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
		client = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), new DigestAuthContext("admin", "admin") );
		// Install the TDE templates
		// loadFileToDB(client, filename, docURI, collection, document format)
		loadFileToDB(client, "masterDetail.tdex", "/optic/view/test/masterDetail.tdex", "XML", new String[] {"http://marklogic.com/xdmp/tde"});
		loadFileToDB(client, "masterDetail2.tdej", "/optic/view/test/masterDetail2.tdej", "JSON",  new String[] {"http://marklogic.com/xdmp/tde"});
		loadFileToDB(client, "masterDetail3.tdej", "/optic/view/test/masterDetail3.tdej", "JSON",  new String[] {"http://marklogic.com/xdmp/tde"});
		loadFileToDB(client, "masterDetail4.tdej", "/optic/view/test/masterDetail4.tdej", "JSON",  new String[] {"http://marklogic.com/xdmp/tde"});

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
		loadFileToDB(client, "masterDetail4.json", "/optic/view/test/masterDetail4.json", "JSON",  new String[] {"/optic/view/test"});
		loadFileToDB(client, "masterDetail5.json", "/optic/view/test/masterDetail5.json", "JSON",  new String[] {"/optic/view/test"});

		loadFileToDB(client, "doc1.json", "/optic/lexicon/test/doc1.json", "JSON",  new String[] {"/other/coll1", "/other/coll2"});
		loadFileToDB(client, "doc2.json", "/optic/lexicon/test/doc2.json", "JSON",  new String[] {"/optic/lexicon/test"});
		loadFileToDB(client, "doc3.json", "/optic/lexicon/test/doc3.json", "JSON",  new String[] {"/optic/lexicon/test"});

		loadFileToDB(client, "city1.json", "/optic/lexicon/test/city1.json", "JSON",  new String[] {"/optic/lexicon/test"});
		loadFileToDB(client, "city2.json", "/optic/lexicon/test/city2.json", "JSON",  new String[] {"/optic/lexicon/test"});
		loadFileToDB(client, "city3.json", "/optic/lexicon/test/city3.json", "JSON",  new String[] {"/optic/lexicon/test"});
		loadFileToDB(client, "city4.json", "/optic/lexicon/test/city4.json", "JSON",  new String[] {"/optic/lexicon/test"});
		loadFileToDB(client, "city5.json", "/optic/lexicon/test/city5.json", "JSON",  new String[] {"/optic/lexicon/test"});	

		Map<String, Object> row = new HashMap<>();			
		row.put("rowId", 1); row.put("colorId", 1); row.put("desc", "ball");
		literals1[0] = row;
		
		row = new HashMap<>();
        row.put("rowId", 2); row.put("colorId", 2); row.put("desc", "square");
        literals1[1] = row;
        
        row = new HashMap<>();
        row.put("rowId", 3); row.put("colorId", 1); row.put("desc", "box");
        literals1[2] = row;
        
        row = new HashMap<>();
        row.put("rowId", 4); row.put("colorId", 1); row.put("desc", "hoop");
        literals1[3] = row;
        
        row = new HashMap<>();
        row.put("rowId", 5); row.put("colorId", 5); row.put("desc", "circle");
        literals1[4] = row;
        		
		row = new HashMap<>();			
		row.put("colorId", 1); row.put("colorDesc", "red");
		literals2[0] = row;
		
		row = new HashMap<>();
        row.put("colorId", 2); row.put("colorDesc", "blue");
        literals2[1] = row;
        
        row = new HashMap<>();
        row.put("colorId", 3); row.put("colorDesc", "black");
        literals2[2] = row;
        
        row = new HashMap<>();
        row.put("colorId", 4); row.put("colorDesc", "yellow");
        literals2[3] = row;

        // Fill up storeInformation Map
        row = new HashMap<>();
        row.put("storeName", "Los Angeles"); row.put("sales", 1500); row.put("txnDate", "Jan-05-1999");
        storeInformation[0] = row;
        
        row = new HashMap<>();
        row.put("storeName", "San Diego"); row.put("sales", 250); row.put("txnDate", "Jan-07-1999");
        storeInformation[1] = row;
        
        row = new HashMap<>();
        row.put("storeName", "Los Angeles"); row.put("sales", 300); row.put("txnDate", "Jan-08-1999");
        storeInformation[2] = row;
        
        row = new HashMap<>();
        row.put("storeName", "Boston"); row.put("sales", 700); row.put("txnDate", "Jan-08-1999");
        storeInformation[3] = row;
        
         // Fill up internetSales Map         
        row = new HashMap<>();
        row.put("txnDate", "Jan-07-1999"); row.put("sales", 250);row.put("sales", 1500);
        internetSales[0] = row;
        row = new HashMap<>();
        row.put("txnDate", "Jan-10-1999"); row.put("sales", 535);row.put("storeName", "San Diego");
        internetSales[1] = row;
        row = new HashMap<>();
        row.put("txnDate", "Jan-11-1999"); row.put("sales", 320); row.put("storeName", "Los Angeles");
        internetSales[2] = row;
        row = new HashMap<>();
        row.put("txnDate", "Jan-12-1999"); row.put("sales", 750);row.put("storeName", "Boston");
        internetSales[3] = row;
        
        // Install JavaScript modules into REST Server module database to support Map and Reduce tests.
        createUserRolesWithPrevilages("test-eval","xdbc:eval", "xdbc:eval-in","xdmp:eval-in","any-uri","xdbc:invoke");
		createRESTUser("eval-user", "x", "test-eval","rest-admin","rest-writer","rest-reader");

		DatabaseClient clientRes = DatabaseClientFactory.newClient(getRestAppServerHostName(), getRestServerPort(), dbName, "eval-user", "x", Authentication.DIGEST);		
		resourceMgr = clientRes.newServerConfigManager().newResourceExtensionsManager();
		ExtensionMetadata resextMetadata = new ExtensionMetadata();
		resextMetadata.setTitle("BasicJSTest");
		resextMetadata.setDescription("Testing resource extension for java script");
		System.out.println(resextMetadata.getScriptLanguage());
		resextMetadata.setScriptLanguage(ScriptLanguage.JAVASCRIPT);
		System.out.println(resextMetadata.getScriptLanguage());
		resextMetadata.setVersion("1.0");
		MethodParameters getParams = new MethodParameters(MethodType.GET);
		getParams.add("row", "xs:string?");
		FileInputStream myStream = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/data/OpticsTestJSResource.js");
		InputStreamHandle handle = new InputStreamHandle(myStream);
		handle.set (myStream);
		resourceMgr.writeServices("OpticsJSResourceModule", handle, resextMetadata,getParams);  
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
	
	/* 
	 * 1) Test join inner
	 * 2) Test join inner with qualifier
	 * 3) Test join and multiple order by
	 * 4) Test multple inner joins
	*/
	@Test
	public void testJoinInner() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testJoinInner method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
	
		// plans from literals
		ModifyPlan plan1 = p.fromLiterals(literals1);
		ModifyPlan plan2 = p.fromLiterals(literals2);
		ModifyPlan output = plan1.joinInner(plan2).orderBy(p.asc(p.col("rowId")));
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
		
		// Should have 4 nodes returned.
		assertEquals("Four nodes not returned from testJoinInner method", 4, jsonBindingsNodes.size());
		JsonNode node = jsonBindingsNodes.path(0);
		assertEquals("Row 1 rowId value incorrect", "1", node.path("rowId").path("value").asText());
		assertEquals("Row 1 desc value incorrect", "ball", node.path("desc").path("value").asText());
		assertEquals("Row 1 colorDesc value incorrect", "red", node.path("colorDesc").path("value").asText());
		node = jsonBindingsNodes.path(3);
		assertEquals("Row 4 rowId value incorrect", "4", node.path("rowId").path("value").asText());
		assertEquals("Row 4 desc value incorrect", "hoop", node.path("desc").path("value").asText());
		assertEquals("Row 4 colorDesc value incorrect", "red", node.path("colorDesc").path("value").asText());
		
		// Join inner with qualifier		
		ModifyPlan plan3 = p.fromLiterals(literals1, "table1");
		ModifyPlan plan4 = p.fromLiterals(literals2, "table2");
		ModifyPlan outputQualifier = plan3.joinInner(plan4)
                                          .where(p.eq(p.viewCol("table1", "colorId"), p.viewCol("table2", "colorId")))
                                          .orderBy(p.desc(p.viewCol("table1", "rowId")));
		jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(outputQualifier, jacksonHandle);
		jsonBindingsNodes = jacksonHandle.get().path("rows");
		node = jsonBindingsNodes.path(3);
		assertEquals("Row 1 table1.rowId value incorrect", "1", node.path("table1.rowId").path("value").asText());
		assertEquals("Row 1 table1.desc value incorrect", "ball", node.path("table1.desc").path("value").asText());
		assertEquals("Row 1 table2.colorDesc value incorrect", "red", node.path("table2.colorDesc").path("value").asText());
		node = jsonBindingsNodes.path(0);
		assertEquals("Row 4 table1.rowId value incorrect", "4", node.path("table1.rowId").path("value").asText());
		assertEquals("Row 4 table1.desc value incorrect", "hoop", node.path("table1.desc").path("value").asText());
		assertEquals("Row 4 table2.colorDesc value incorrect", "red", node.path("table2.colorDesc").path("value").asText());
		
		// Test join and multiple order by
		ModifyPlan plan5 = p.fromLiterals(literals1, "myItem");
		ModifyPlan plan6 = p.fromLiterals(literals2, "myColor");

		PlanColumn descCol = p.viewCol("myItem","desc");

		PlanColumn itemColorIdCol = p.viewCol("myItem", "colorId");
		PlanColumn colorIdCol =  p.viewCol("myColor", "colorId");
		ModifyPlan outputMultiOrder = plan5.joinInner(plan6, p.on(itemColorIdCol, colorIdCol))
				                          .select(descCol, colorIdCol)
				                          .orderBy(colorIdCol, descCol);
		jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(outputMultiOrder, jacksonHandle);
		jsonBindingsNodes = jacksonHandle.get().path("rows");
		// Should have 4 nodes returned.
		assertEquals("Four nodes not returned from testJoinInner method - multiple order by", 4, jsonBindingsNodes.size());
		node = jsonBindingsNodes.path(0);
		assertEquals("Row 1 myItem.desc value incorrect", "ball", node.path("myItem.desc").path("value").asText());
		assertEquals("Row 1 myColor.colorId value incorrect", "1", node.path("myColor.colorId").path("value").asText());
		node = jsonBindingsNodes.path(1);
		assertEquals("Row 2 myItem.desc value incorrect", "box", node.path("myItem.desc").path("value").asText());
		assertEquals("Row 2 myColor.colorId value incorrect", "1", node.path("myColor.colorId").path("value").asText());
		node = jsonBindingsNodes.path(2);
		assertEquals("Row 3 myItem.desc value incorrect", "hoop", node.path("myItem.desc").path("value").asText());
		assertEquals("Row 3 myColor.colorId value incorrect", "1", node.path("myColor.colorId").path("value").asText());
		node = jsonBindingsNodes.path(3);
		assertEquals("Row 4 myItem.desc value incorrect", "square", node.path("myItem.desc").path("value").asText());
		assertEquals("Row 4 myColor.colorId value incorrect", "2", node.path("myColor.colorId").path("value").asText());
		
		// Test multple inner joins
		Map<String, Object>[] literals3 = new HashMap[4];
		Map<String, Object> row = new HashMap<>();			
		
		row = new HashMap<>();
		row.put("color", "red"); row.put("ref", "rose");
		literals3[0] = row;
		row = new HashMap<>();
		row.put("color", "blue"); row.put("ref", "water");
		literals3[1] = row;
		row = new HashMap<>();
		row.put("color", "black"); row.put("ref", "bag");
		literals3[2] = row;
		row = new HashMap<>();
		row.put("color", "yellow"); row.put("ref", "moon");
		literals3[3] = row;
		
		ModifyPlan plan7 = p.fromLiterals(literals3, "myRef");
		
		PlanColumn colorDescCol = p.viewCol("myColor", "colorDesc");
		PlanColumn refColorCol = p.viewCol("myRef", "color");
		PlanColumn refCol = p.viewCol("myRef", "ref");
		
		ModifyPlan outputMultiJoin = plan5.joinInner(plan6, p.on(itemColorIdCol, colorIdCol))
				                          .joinInner(plan7, p.on(colorDescCol, refColorCol))
				                          .select(descCol, colorIdCol, refCol)
				                          .orderBy(colorIdCol, descCol);
		jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(outputMultiJoin, jacksonHandle);
		jsonBindingsNodes = jacksonHandle.get().path("rows");
		// Should have 4 nodes returned.
		assertEquals("Four nodes not returned from testJoinInner method - multiple joins", 4, jsonBindingsNodes.size());
		node = jsonBindingsNodes.path(0);
		assertEquals("Row 1 myItem.desc value incorrect", "ball", node.path("myItem.desc").path("value").asText());
		assertEquals("Row 1 myColor.colorId value incorrect", "1", node.path("myColor.colorId").path("value").asText());
		assertEquals("Row 1 myRef.ref value incorrect", "rose", node.path("myRef.ref").path("value").asText());
		node = jsonBindingsNodes.path(1);
		assertEquals("Row 2 myItem.desc value incorrect", "box", node.path("myItem.desc").path("value").asText());
		assertEquals("Row 2 myColor.colorId value incorrect", "1", node.path("myColor.colorId").path("value").asText());
		assertEquals("Row 2 myRef.ref value incorrect", "rose", node.path("myRef.ref").path("value").asText());
		node = jsonBindingsNodes.path(2);
		assertEquals("Row 3 myItem.desc value incorrect", "hoop", node.path("myItem.desc").path("value").asText());
		assertEquals("Row 3 myColor.colorId value incorrect", "1", node.path("myColor.colorId").path("value").asText());
		assertEquals("Row 3 myRef.ref value incorrect", "rose", node.path("myRef.ref").path("value").asText());
		node = jsonBindingsNodes.path(3);
		assertEquals("Row 4 myItem.desc value incorrect", "square", node.path("myItem.desc").path("value").asText());
		assertEquals("Row 4 myColor.colorId value incorrect", "2", node.path("myColor.colorId").path("value").asText());
		assertEquals("Row 4 myRef.ref value incorrect", "water", node.path("myRef.ref").path("value").asText());
	}
	
	/* 
	 * Test join left outer
	*/
	@Test
	public void testJoinLeftOuter() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testJoinLeftOuter method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
	
		// plans from literals
		ModifyPlan plan1 = p.fromLiterals(literals1);
		ModifyPlan plan2 = p.fromLiterals(literals2);
		
		ModifyPlan output = plan1.joinLeftOuter(plan2)
			                     .orderBy(p.col("rowId"));
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
		
		// Should have 5 nodes returned.
		assertEquals("Five nodes not returned from testJoinLeftOuter method", 5, jsonBindingsNodes.size());
		JsonNode node = jsonBindingsNodes.path(0);
		assertEquals("Row 1 rowId value incorrect", "1", node.path("rowId").path("value").asText());
		assertEquals("Row 1 desc value incorrect", "ball", node.path("desc").path("value").asText());
		assertEquals("Row 1 colorDesc value incorrect", "red", node.path("colorDesc").path("value").asText());
		node = jsonBindingsNodes.path(3);
		assertEquals("Row 4 rowId value incorrect", "4", node.path("rowId").path("value").asText());
		assertEquals("Row 4 desc value incorrect", "hoop", node.path("desc").path("value").asText());
		assertEquals("Row 4 colorDesc value incorrect", "red", node.path("colorDesc").path("value").asText());
		node = jsonBindingsNodes.path(4);
		assertEquals("Row 5 rowId value incorrect", "5", node.path("rowId").path("value").asText());
		assertEquals("Row 5 desc value incorrect", "circle", node.path("desc").path("value").asText());
		assertFalse("Row 5 colorDesc should be null", node.path("colorDesc").isNull());
	}
	
	/* 
	 * Test group-by max
	*/
	@Test
	public void testGroupByMax() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testGroupByMax method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
	
		// plans from literals
		ModifyPlan plan1 = p.fromLiterals(literals1);
		ModifyPlan plan2 = p.fromLiterals(literals2);
		
		ModifyPlan output = plan1.groupBy(p.col("colorId"), p.max("new_color_id", "colorId"));
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
		
		// Should have 3 nodes returned.
		assertEquals("Three nodes not returned from testJoinLeftOuter method", 3, jsonBindingsNodes.size());
		JsonNode node = jsonBindingsNodes.path(0);
		assertEquals("Row 1 colorId value incorrect", "1", node.path("colorId").path("value").asText());
		assertEquals("Row 1 new_color_id value incorrect", "1", node.path("new_color_id").path("value").asText());
		node = jsonBindingsNodes.path(1);
		assertEquals("Row 2 colorId value incorrect", "2", node.path("colorId").path("value").asText());
		assertEquals("Row 2 new_color_id value incorrect", "2", node.path("new_color_id").path("value").asText());
		node = jsonBindingsNodes.path(2);
		assertEquals("Row 3 colorId value incorrect", "5", node.path("colorId").path("value").asText());
		assertEquals("Row 3 new_color_id value incorrect", "5", node.path("new_color_id").path("value").asText());
	}
	
	/* 
	 * Test where limit and select
	 * Test limit with 0 length
	*/
	@Test
	public void testOffsetAndLimit() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testOffsetAndLimit method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
	
		// plans from literals
		ModifyPlan plan1 = p.fromLiterals(literals1);
		ModifyPlan plan2 = p.fromLiterals(literals2);
		
		ModifyPlan output = plan1.joinInner(plan2)
		                         .where(p.eq(p.col("colorId"), p.xs.intVal(1)))
		                         .offsetLimit(1, 3)
		                         .select("rowId", "desc", "colorId", "colorDesc");
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
		
		// Should have 2 nodes returned.
		assertEquals("Two nodes not returned from testOffsetAndLimit method", 2, jsonBindingsNodes.size());
		JsonNode node = jsonBindingsNodes.path(0);
		assertEquals("Row 1 rowId value incorrect", "3", node.path("rowId").path("value").asText());
		assertEquals("Row 1 desc value incorrect", "box", node.path("desc").path("value").asText());
		assertEquals("Row 1 colorId value incorrect", "1", node.path("colorId").path("value").asText());
		assertEquals("Row 1 colorDesc value incorrect", "red", node.path("colorDesc").path("value").asText());
		node = jsonBindingsNodes.path(1);
		assertEquals("Row 2 rowId value incorrect", "4", node.path("rowId").path("value").asText());
		assertEquals("Row 2 desc value incorrect", "hoop", node.path("desc").path("value").asText());
		assertEquals("Row 2 colorId value incorrect", "1", node.path("colorId").path("value").asText());
		assertEquals("Row 2 colorDesc value incorrect", "red", node.path("colorDesc").path("value").asText());
		
		//limit with 0 length
		ModifyPlan outputLimit = plan1.joinInner(plan2)
                                      .where(p.eq(p.col("colorId"), p.xs.intVal(1)))
                                      .limit(0)
                                      .select("rowId", "desc", "colorId", "colorDesc");
		jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		StringBuilder str = new StringBuilder();
		try {
			rowMgr.resultDoc(outputLimit, jacksonHandle);
		}
		catch(Exception ex) {
			str.append(ex.getMessage());
			System.out.println("Exception message is " + str.toString());
		}
		// Should have OPTIC-INVALARGS exceptions.
		assertTrue("Exceptions not found", str.toString().contains("OPTIC-INVALARGS"));
		assertTrue("Exceptions not found", str.toString().contains("Invalid arguments: limit must be a positive number: 0"));
	}
	
	/* 
	 * Test arithmetic expression
	*/
	@Test
	public void testArithmeticExpression() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testArithmeticExpression method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
	
		// plans from literals
		ModifyPlan plan1 = p.fromLiterals(literals1);
		ModifyPlan plan2 = p.fromLiterals(literals2);
		
		ModifyPlan output  =
		        plan1.joinInner(plan2)
		        .select(
		          p.col("rowId"), 
		          p.col("desc"), 
		          p.col("colorId"), 
		          p.col("colorDesc"),
		          p.as("added", p.add(p.col("rowId"), p.col("colorId"), p.xs.intVal(10))),
		          p.as("subtracted", p.subtract(p.col("colorId"), p.col("rowId"))),
		          p.as("negSubtracted", p.subtract(p.col("colorId"), p.col("desc"))),
		          p.as("divided", p.divide(p.col("colorId"), p.col("rowId"))),
		          p.as("multiplied", p.multiply(p.col("colorId"), p.col("rowId"), p.xs.floatVal(0.6f))),
		          p.as("colDefined", p.isDefined(p.col("added"))),
		          p.as("colNotDefined", p.isDefined(p.col("negSubtracted"))),
		          p.as("colNotDefinedNegate", p.not(p.isDefined(p.col("negSubtracted")))),
		          p.as("caseExpr", 
		            p.caseExpr(
		              p.when(p.eq(p.col("rowId"), p.xs.intVal(1)), p.xs.string("foo")), 
		              p.when(p.eq(p.col("rowId"),  p.xs.intVal(2)), p.xs.string("baz")),
		              p.when(p.eq(p.col("rowId"),  p.xs.intVal(3)), p.xs.string("rat")),
		              p.elseExpr(p.xs.string("bar"))
		            )
		          )
		        );
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
		
		// Should have 4 nodes returned.
		assertEquals("Four nodes not returned from testArithmeticExpression method", 4, jsonBindingsNodes.size());
		JsonNode node = jsonBindingsNodes.path(0);
		
		assertEquals("Row 1 rowId value incorrect", "1", node.path("rowId").path("value").asText());
		assertEquals("Row 1 colorDesc value incorrect", "red", node.path("colorDesc").path("value").asText());
		
		assertEquals("Row 1 added value incorrect", "12", node.path("added").path("value").asText());
		assertEquals("Row 1 subtracted value incorrect", "0", node.path("subtracted").path("value").asText());
		assertEquals("Row 1 divided value incorrect", "1", node.path("divided").path("value").asText());
		assertEquals("Row 1 multiplied value incorrect", "0.6", node.path("multiplied").path("value").asText());
		assertEquals("Row 1 caseExpr value incorrect", "foo", node.path("caseExpr").path("value").asText());
		
		node = jsonBindingsNodes.path(1);
		assertEquals("Row 2 caseExpr value incorrect", "baz", node.path("caseExpr").path("value").asText());
		
		node = jsonBindingsNodes.path(2);
		assertEquals("Row 3 caseExpr value incorrect", "rat", node.path("caseExpr").path("value").asText());
		
		node = jsonBindingsNodes.path(3);
		assertEquals("Row 4 rowId value incorrect", "4", node.path("rowId").path("value").asText());
		assertEquals("Row 4 colorDesc value incorrect", "red", node.path("colorDesc").path("value").asText());
		
		assertEquals("Row 4 added value incorrect", "15", node.path("added").path("value").asText());
		assertEquals("Row 4 subtracted value incorrect", "-3", node.path("subtracted").path("value").asText());
		assertEquals("Row 4 divided value incorrect", "0.25", node.path("divided").path("value").asText());
		assertEquals("Row 4 multiplied value incorrect", "2.4", node.path("multiplied").path("value").asText());
		assertEquals("Row 4 caseExpr value incorrect", "bar", node.path("caseExpr").path("value").asText());
	}
	
	/* 
	 * Test join inner doc on json and xml documents
	*/
	@Test
	public void testJoinInnerDocOnJson() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testJoinInnerDocOnJson method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		
		Map<String, Object>[] literals3 = new HashMap[4];
		Map<String, Object> row = new HashMap<>();			
		row.put("id", 1); row.put("val", 2); row.put("uri", "/optic/lexicon/test/doc1.json");
		literals3[0] = row;
		
		row = new HashMap<>();
		row.put("id", 2); row.put("val", 4); row.put("uri", "/optic/test/not/a/real/doc.nada");
		literals3[1] = row;
		
		row = new HashMap<>();
		row.put("id", 3); row.put("val", 6); row.put("uri", "/optic/lexicon/test/doc3.json");
		literals3[2] = row;
		
		row = new HashMap<>();
		row.put("id", 4); row.put("val", 8); row.put("uri", "/optic/lexicon/test/doc4.xml");
		literals3[3] = row;
		
		// TEST 16 - join inner doc with xpath traversing down and multiple values on json
		ModifyPlan output = p.fromLiterals(literals3).orderBy(p.asc("id"))
		                                            .joinDoc(p.col("doc"), p.col("uri"))
		                                            .select(
		                                            		p.col("id"),
		                                            		p.col("val"), 
		                                            		p.col("uri"),
		                                            		p.as("nodes", p.xpath("doc", "/location/latLonPair/(lat|long)")))
                                                    .where(p.isDefined(p.col("nodes")));
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
		
		// Should have 2 nodes returned.
		assertEquals("Two nodes not returned from testJoinInnerDocOnJson method", 2, jsonBindingsNodes.size());
		JsonNode node = jsonBindingsNodes.path(0);
		assertEquals("Row 1 id value incorrect", "1", node.path("id").path("value").asText());
		assertEquals("Row 1 val value incorrect", "2", node.path("val").path("value").asText());		
		assertEquals("Row 1 uri value incorrect", "/optic/lexicon/test/doc1.json", node.path("uri").path("value").asText());
		node = jsonBindingsNodes.path(1);
		assertEquals("Row 2 id value incorrect", "3", node.path("id").path("value").asText());
		assertEquals("Row 2 val value incorrect", "6", node.path("val").path("value").asText());
		assertEquals("Row 2 uri value incorrect", "/optic/lexicon/test/doc3.json", node.path("uri").path("value").asText());
		
		// Verify TEST 17 - join inner doc with xpath accessing attribute on xml
		ModifyPlan output17 = p.fromLiterals(literals3).orderBy(p.asc("id"))
				.joinDoc(p.col("doc"), p.col("uri"))
				.select(
						p.col("id"),
						p.col("val"), 
						p.col("uri"),
						p.as("nodes", p.xpath("doc", "/doc/distance/@direction")))
						.where(p.isDefined(p.col("nodes")));
		JacksonHandle jacksonHandle17 = new JacksonHandle();
		jacksonHandle17.setMimetype("application/json");
		rowMgr.resultDoc(output17, jacksonHandle17);
		JsonNode jsonBindingsNodes17 = jacksonHandle17.get().path("rows");

		// Should have 1 node returned.
		assertEquals("One node not returned from testJoinInnerDocOnJson method", 1, jsonBindingsNodes17.size());
		JsonNode node17 = jsonBindingsNodes17.path(0);
		assertEquals("Row 1 id value incorrect", "4", node17.path("id").path("value").asText());
		assertEquals("Row 1 val value incorrect", "8", node17.path("val").path("value").asText());
		assertEquals("Row 1 uri value incorrect", "/optic/lexicon/test/doc4.xml", node17.path("uri").path("value").asText());
		assertEquals("Row 1 nodes value incorrect", "east", node17.path("nodes").path("value").asText());
		
		// Verify TEST 18 - join inner doc with xpath traversing down and multiple values on xml
		ModifyPlan output18 = p.fromLiterals(literals3).orderBy(p.asc("id"))
				.joinDoc(p.col("doc"), p.col("uri"))
				.select(
						p.col("id"),
						p.col("val"), 
						p.col("uri"),
						p.as("nodes", p.xpath("doc", "/doc/location/latLonPair/(lat|long)/number()")))
						.where(p.isDefined(p.col("nodes")));
		JacksonHandle jacksonHandle18 = new JacksonHandle();
		jacksonHandle18.setMimetype("application/json");
		rowMgr.resultDoc(output18, jacksonHandle18);
		JsonNode jsonBindingsNodes18 = jacksonHandle18.get().path("rows");

		// Should have 1 node returned.
		assertEquals("One node not returned from testJoinInnerDocOnJson method", 1, jsonBindingsNodes18.size());
		JsonNode node18 = jsonBindingsNodes18.path(0);
		assertEquals("Row 1 id value incorrect", "4", node18.path("id").path("value").asText());
		assertEquals("Row 1 val value incorrect", "8", node18.path("val").path("value").asText());
		assertEquals("Row 1 uri value incorrect", "/optic/lexicon/test/doc4.xml", node18.path("uri").path("value").asText());
		assertEquals("Row 1 nodes value incorrect", "39.9", node18.path("nodes").path("value").get(0).asText());
		assertEquals("Row 1 nodes value incorrect", "116.4", node18.path("nodes").path("value").get(1).asText());
		
		// Verify TEST 19 - join inner doc with traversing up xpath on json
		ModifyPlan output19 = p.fromLiterals(literals3).orderBy(p.asc("id"))
				.joinDoc(p.col("doc"), p.col("uri"))
				.select(
						p.col("id"),
						p.col("val"), 
						p.col("uri"),
						p.as("nodes", p.xpath("doc", "/location/latLonPair/../../city")))
						.where(p.isDefined(p.col("nodes")));
		JacksonHandle jacksonHandle19 = new JacksonHandle();
		jacksonHandle19.setMimetype("application/json");
		rowMgr.resultDoc(output19, jacksonHandle19);
		JsonNode jsonBindingsNodes19 = jacksonHandle19.get().path("rows");

		// Should have 2 nodes returned.
		assertEquals("Two nodes not returned from testJoinInnerDocOnJson method", 2, jsonBindingsNodes19.size());
		JsonNode node19 = jsonBindingsNodes19.path(0);
		assertEquals("Row 1 id value incorrect", "1", node19.path("id").path("value").asText());
		assertEquals("Row 1 val value incorrect", "2", node19.path("val").path("value").asText());
		assertEquals("Row 1 uri value incorrect", "/optic/lexicon/test/doc1.json", node19.path("uri").path("value").asText());
		assertEquals("Row 1 nodes value incorrect", "london", node19.path("nodes").path("value").asText());
		
		node19 = jsonBindingsNodes19.path(1);
		assertEquals("Row 2 id value incorrect", "3", node19.path("id").path("value").asText());
		assertEquals("Row 2 val value incorrect", "6", node19.path("val").path("value").asText());
		assertEquals("Row 2 uri value incorrect", "/optic/lexicon/test/doc3.json", node19.path("uri").path("value").asText());
		assertEquals("Row 2 nodes value incorrect", "new jersey", node19.path("nodes").path("value").asText());
		
		// Verify TEST 20 - join inner doc with traversing up xpath on xml
		ModifyPlan output20 = p.fromLiterals(literals3).orderBy(p.asc("id"))
				.joinDoc(p.col("doc"), p.col("uri"))
				.select(
						p.col("id"),
						p.col("val"), 
						p.col("uri"),
						p.as("nodes", p.xpath("doc", "/doc/location/latLonPair/../../city")))
						.where(p.isDefined(p.col("nodes")));
		JacksonHandle jacksonHandle20 = new JacksonHandle();
		jacksonHandle20.setMimetype("application/json");
		rowMgr.resultDoc(output20, jacksonHandle20);
		JsonNode jsonBindingsNodes20 = jacksonHandle20.get().path("rows");

		// Should have 1 node returned.
		assertEquals("One node not returned from testJoinInnerDocOnJson method", 1, jsonBindingsNodes20.size());
		JsonNode node20 = jsonBindingsNodes20.path(0);
		assertEquals("Row 1 id value incorrect", "4", node20.path("id").path("value").asText());
		assertEquals("Row 1 val value incorrect", "8", node20.path("val").path("value").asText());
		assertEquals("Row 1 uri value incorrect", "/optic/lexicon/test/doc4.xml", node20.path("uri").path("value").asText());
		assertEquals("Row 1 nodes value incorrect", "<city>beijing</city>", node20.path("nodes").path("value").asText());
		
		// Verify TEST 21 - join inner doc with traversing deep xpath on json and xml
		ModifyPlan output21 = p.fromLiterals(literals3).orderBy(p.asc("id"))
				.joinDoc(p.col("doc"), p.col("uri"))
				.select(
						p.col("id"),
						p.col("val"), 
						p.col("uri"),
						p.as("nodes", p.xpath("doc", "//lat/number()")))
						.where(p.isDefined(p.col("nodes")));
		JacksonHandle jacksonHandle21 = new JacksonHandle();
		jacksonHandle21.setMimetype("application/json");
		rowMgr.resultDoc(output21, jacksonHandle21);
		JsonNode jsonBindingsNodes21 = jacksonHandle21.get().path("rows");

		// Should have 3 nodes returned.
		assertEquals("Three nodes not returned from testJoinInnerDocOnJson method", 3, jsonBindingsNodes21.size());
		JsonNode node21 = jsonBindingsNodes21.path(0);
		assertEquals("Row 1 id value incorrect", "1", node21.path("id").path("value").asText());
		assertEquals("Row 1 val value incorrect", "2", node21.path("val").path("value").asText());
		assertEquals("Row 1 uri value incorrect", "/optic/lexicon/test/doc1.json", node21.path("uri").path("value").asText());
		assertEquals("Row 1 nodes value incorrect", "51.5", node21.path("nodes").path("value").asText());
		node21 = jsonBindingsNodes21.path(1);
		assertEquals("Row 1 id value incorrect", "3", node21.path("id").path("value").asText());
		assertEquals("Row 1 val value incorrect", "6", node21.path("val").path("value").asText());
		assertEquals("Row 1 uri value incorrect", "/optic/lexicon/test/doc3.json", node21.path("uri").path("value").asText());
		assertEquals("Row 1 nodes value incorrect", "40.72", node21.path("nodes").path("value").asText());
		node21 = jsonBindingsNodes21.path(2);
		assertEquals("Row 1 id value incorrect", "4", node21.path("id").path("value").asText());
		assertEquals("Row 1 val value incorrect", "8", node21.path("val").path("value").asText());
		assertEquals("Row 1 uri value incorrect", "/optic/lexicon/test/doc4.xml", node21.path("uri").path("value").asText());
		assertEquals("Row 1 nodes value incorrect", "39.9", node21.path("nodes").path("value").asText());
	}
	
	/* 
	 * Test group-by without aggregate
	*/
	@Test
	public void testGroupbyWithoutAggregate() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testGroupbyWithoutAggregate method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();

		// plans from literals
		ModifyPlan plan1 = p.fromLiterals(literals1);
		
		ModifyPlan output = plan1.groupBy("colorId").orderBy("colorId");

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

		// Should have 3 nodes returned.
		assertEquals("Three nodes not returned from testGroupbyWithoutAggregate method", 3, jsonBindingsNodes.size());
		assertEquals("Row 1 colorId value incorrect", "1", jsonBindingsNodes.path(0).path("colorId").path("value").asText());
		assertEquals("Row 2 colorId value incorrect", "2", jsonBindingsNodes.path(1).path("colorId").path("value").asText());
		assertEquals("Row 3 colorId value incorrect", "5", jsonBindingsNodes.path(2).path("colorId").path("value").asText());
	}
	
	/* 
	 * Test union with whereDistinct
	*/
	@Test
	public void testUnionWithWhereDistinct() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testUnionWithWhereDistinct method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
	
		// plans from literals
		ModifyPlan plan1 = p.fromLiterals(literals1);
		ModifyPlan plan2 = p.fromLiterals(literals2);
		
		ModifyPlan output = plan1.union(plan2).whereDistinct().orderBy("rowId");
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
		
		// Should have 9 nodes returned.
		assertEquals("Nine nodes not returned from testUnionWithWhereDistinct method", 9, jsonBindingsNodes.size());
		
		assertEquals("Row 1 colorId value incorrect", "1", jsonBindingsNodes.path(0).path("colorId").path("value").asText());
		assertEquals("Row 1 colorDesc value incorrect", "red", jsonBindingsNodes.path(0).path("colorDesc").path("value").asText());
		assertEquals("Row 2 colorId value incorrect", "2", jsonBindingsNodes.path(1).path("colorId").path("value").asText());
		assertEquals("Row 2 colorDesc value incorrect", "blue", jsonBindingsNodes.path(1).path("colorDesc").path("value").asText());
		assertEquals("Row 6 desc value incorrect", "square", jsonBindingsNodes.path(5).path("desc").path("value").asText());
		assertEquals("Row 6 rowId value incorrect", "2", jsonBindingsNodes.path(5).path("rowId").path("value").asText());
		assertEquals("Row 9 desc value incorrect", "circle", jsonBindingsNodes.path(8).path("desc").path("value").asText());
		assertEquals("Row 9 colorId value incorrect", "5", jsonBindingsNodes.path(8).path("colorId").path("value").asText());
	}
	
	/* 
	 * Test intersect
	*/
	@Test
	public void testIntersect() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testIntersect method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
	
		// plans from literals
		ModifyPlan plan1 = p.fromLiterals(storeInformation);
		ModifyPlan plan2 = p.fromLiterals(internetSales);
		
		ModifyPlan output = plan1.select("txnDate")
		                         .intersect(plan2.select("txnDate"));
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
		
		// Should have 1 node returned.
		assertEquals("One node not returned from testIntersect method", 1, jsonBindingsNodes.size());		
		assertEquals("Row 1 txnDate value incorrect", "Jan-07-1999", jsonBindingsNodes.path(0).path("txnDate").path("value").asText());
	}
	
	/* 
	 * Test except
	*/
	@Test
	public void testExcept() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testExcept method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
	
		// plans from literals
		ModifyPlan plan1 = p.fromLiterals(storeInformation);
		ModifyPlan plan2 = p.fromLiterals(internetSales);
		
		ModifyPlan output = plan1.select("txnDate")
		                         .except(plan2.select("txnDate"));
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
		
		// Should have 3 nodes returned.
		assertEquals("Three nodes not returned from testExcept method", 3, jsonBindingsNodes.size());		
		assertEquals("Row 1 txnDate value incorrect", "Jan-05-1999", jsonBindingsNodes.path(0).path("txnDate").path("value").asText());
		assertEquals("Row 2 txnDate value incorrect", "Jan-08-1999", jsonBindingsNodes.path(1).path("txnDate").path("value").asText());
		assertEquals("Row 3 txnDate value incorrect", "Jan-08-1999", jsonBindingsNodes.path(2).path("txnDate").path("value").asText());
	}
	
	/* 
	 * Test arrayAggregate, sequenceAggregate and aggregate with distinct and duplicate options
	*/
	@Test
	public void testAggregates() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testAggregates method");
	
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		// Create a new Map for literals.
		Map<String, Object> row = new HashMap<>();	
		Map<String, Object>[] fooLiteral = new HashMap[6];
		Map<String, Object>[] purpleLiteral = new HashMap[6];
		
		Map<String, Object>[] ballLiteral = new HashMap[6];
		
		row.put("rowId", 1); row.put("colorId", 1); row.put("desc", "ball");
		fooLiteral[0] = row;
		
		row = new HashMap<>();
		row.put("rowId", 1); row.put("colorId", 3); row.put("desc", "foo");
		 fooLiteral[1] = row;
		 
		row = new HashMap<>();
        row.put("rowId", 2); row.put("colorId", 2); row.put("desc", "square");
        fooLiteral[2] = row;
        
        row = new HashMap<>();
        row.put("rowId", 3); row.put("colorId", 1); row.put("desc", "box");
        fooLiteral[3] = row;
        
        row = new HashMap<>();
        row.put("rowId", 4); row.put("colorId", 1); row.put("desc", "hoop");
        fooLiteral[4] = row;
        
        row = new HashMap<>();
        row.put("rowId", 5); row.put("colorId", 5); row.put("desc", "circle");
        fooLiteral[5] = row;
        
        row = new HashMap<>();			
		row.put("colorId", 1); row.put("colorDesc", "red");
		purpleLiteral[0] = row;
		// Add another red color
		purpleLiteral[1] = row;
		
		row = new HashMap<>();
        row.put("colorId", 1); row.put("colorDesc", "purple");
        purpleLiteral[2] = row;
		
		row = new HashMap<>();
        row.put("colorId", 2); row.put("colorDesc", "blue");
        purpleLiteral[3] = row;
        
        row = new HashMap<>();
        row.put("colorId", 3); row.put("colorDesc", "black");
        purpleLiteral[4] = row;
        
        row = new HashMap<>();
        row.put("colorId", 4); row.put("colorDesc", "yellow");
        purpleLiteral[5] = row;
        
        row = new HashMap<>();			
		row.put("rowId", 1); row.put("colorId", 1); row.put("desc", "ball");
		ballLiteral[0] = row;
		row = new HashMap<>();			
		row.put("rowId", 1); row.put("colorId", 3); row.put("desc", "ball");
		ballLiteral[1] = row;
		
		row = new HashMap<>();
        row.put("rowId", 2); row.put("colorId", 2); row.put("desc", "square");
        ballLiteral[2] = row;
        
        row = new HashMap<>();
        row.put("rowId", 3); row.put("colorId", 1); row.put("desc", "box");
        ballLiteral[3] = row;
        
        row = new HashMap<>();
        row.put("rowId", 4); row.put("colorId", 1); row.put("desc", "hoop");
        ballLiteral[4] = row;
        
        row = new HashMap<>();
        row.put("rowId", 5); row.put("colorId", 5); row.put("desc", "circle");
        ballLiteral[5] = row;
                
		// plans from literals
		ModifyPlan plan1 = p.fromLiterals(fooLiteral, "myItem");
		ModifyPlan plan2 = p.fromLiterals(purpleLiteral, "myColor");
		
		PlanColumn rowIdExp = p.viewCol("myItem", "rowId");
		PlanColumn itemColorIdCol =  p.viewCol("myItem", "colorId");
		PlanColumn colorIdCol =  p.viewCol("myColor", "colorId");
		
		ModifyPlan outputAgg = plan1.joinInner(plan2, p.on(itemColorIdCol, colorIdCol))
		                         .groupBy(rowIdExp, p.arrayAggregate("colorIdArray", "colorDesc"))
		                         .orderBy("rowId");
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(outputAgg, jacksonHandle);
		JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
		
		// Should have 4 nodes returned.
		assertEquals("Four nodes not returned from testAggregates method", 4, jsonBindingsNodes.size());		
		assertEquals("Row 1 myItem.rowId value incorrect", "1", jsonBindingsNodes.path(0).path("myItem.rowId").path("value").asText());
		assertEquals("Row 1 colorIdArray length value incorrect", 4, jsonBindingsNodes.path(0).path("colorIdArray").path("value").size());
		assertEquals("Row 2 colorIdArray array 1 value incorrect", "blue", jsonBindingsNodes.path(1).path("colorIdArray").path("value").path(0).asText());
		
		// sequenceAggregate
		
		ModifyPlan outputSeqAgg = plan1.joinInner(plan2, p.on(itemColorIdCol, colorIdCol))
                                       .groupBy(rowIdExp, p.sequenceAggregate("colorIdArray", "colorDesc"))
                                       .orderBy("rowId");
		
		jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(outputSeqAgg, jacksonHandle);
		jsonBindingsNodes = jacksonHandle.get().path("rows");
		
		// Should have 4 nodes returned.
		assertEquals("Four nodes not returned from testAggregates method", 4, jsonBindingsNodes.size());
		assertEquals("Row 1 myItem.rowId value incorrect", "1", jsonBindingsNodes.path(0).path("myItem.rowId").path("value").asText());
		assertEquals("Row 1 colorIdArray length value incorrect", 4, jsonBindingsNodes.path(0).path("colorIdArray").path("value").size());
		assertEquals("Row 2 colorIdArray array 1 value incorrect", "blue", jsonBindingsNodes.path(1).path("colorIdArray").path("value").asText());
		
		//TEST 37 - aggregate with distinct option
		// plans from literals
		ModifyPlan plan3 = p.fromLiterals(ballLiteral);
		ModifyPlan plan4 = p.fromLiterals(literals2);
		
		// Verify DISTINCT
		PlanAggregateOptions options = p.aggregateOptions(PlanBuilder.PlanValues.DISTINCT);
		
		PlanAggregateColSeq aggColSeq = p.aggregates(p.count("descAgg", "desc", options));
		PlanExprColSeq colSeq = p.cols("rowId");
		
		ModifyPlan outputCountDist = plan3.joinInner(plan4)
		                                  .groupBy(colSeq, aggColSeq)
		                                  .orderBy("rowId");
		jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(outputCountDist, jacksonHandle);
		jsonBindingsNodes = jacksonHandle.get().path("rows");
		
		// Should have 4 nodes returned.
		assertEquals("Four node not returned from testAggregates method", 4, jsonBindingsNodes.size());
		assertEquals("Row 1 rowId value incorrect", "1", jsonBindingsNodes.path(0).path("rowId").path("value").asText());
		assertEquals("Row 1 descAgg value incorrect", 1, jsonBindingsNodes.path(0).path("descAgg").path("value").asInt());
		
		assertEquals("Row 1 rowId value incorrect", "4", jsonBindingsNodes.path(3).path("rowId").path("value").asText());
		assertEquals("Row 1 descAgg value incorrect", 1, jsonBindingsNodes.path(3).path("descAgg").path("value").asInt());
		
		// aggregate with duplicate option
		
		PlanAggregateOptions optionsDup = p.aggregateOptions(PlanBuilder.PlanValues.DUPLICATE);
		
		PlanAggregateColSeq aggColSeqDup = p.aggregates(p.count("descAgg", "desc", optionsDup));
		PlanExprColSeq colSeqDup = p.cols("rowId");
		
		ModifyPlan outputCountDup = plan3.joinInner(plan4)
		                                  .groupBy(colSeqDup, aggColSeqDup)
		                                  .orderBy("rowId");
		jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(outputCountDup, jacksonHandle);
		JsonNode jsonBindingsNodesDup = jacksonHandle.get().path("rows");
		
		// Should have 4 nodes returned. Duplicate values are included.
		assertEquals("Four node not returned from testAggregates method", 4, jsonBindingsNodesDup.size());
		assertEquals("Row 1 rowId value incorrect", "1", jsonBindingsNodesDup.path(0).path("rowId").path("value").asText());
		assertEquals("Row 1 descAgg value incorrect", 2, jsonBindingsNodesDup.path(0).path("descAgg").path("value").asInt());
		
		assertEquals("Row 1 rowId value incorrect", "4", jsonBindingsNodesDup.path(3).path("rowId").path("value").asText());
		assertEquals("Row 1 descAgg value incorrect", 1, jsonBindingsNodesDup.path(3).path("descAgg").path("value").asInt());
	}
	
	/* 
	 * Test date time builtin functions
	*/
	@Ignore
	public void testBuiltInFns() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testBuiltInFns method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
	
		// plans from literals
		ModifyPlan plan1 = p.fromLiterals(literals1, "myItem");
		ModifyPlan plan2 = p.fromLiterals(literals2, "myColor");
		
		PlanColumn descCol = p.viewCol("myItem", "desc");
		PlanColumn itemColorIdCol = p.viewCol("myItem", "colorId");
		PlanColumn colorIdCol = p.viewCol("myColor", "colorId");
	      
	     /* XsDateExpr curDate = p.fn.currentDate();
	      XsTimeExpr curTime = p.fn.currentTime();
	      SqlGenericDateTimeExpr sqlcurTime = (SqlGenericDateTimeExpr) curTime;
	      double sqlHr = p.sql.hours(sqlcurTime);
	      
	      PlanColumn log10 = p.math.log10(sqlHr);
	      ModifyPlan output =
	        plan1.joinInner(plan2, p.on(itemColorIdCol, colorIdCol))
	        .select(
	          p.as("currentDate", p.fn.currentDate()),
	          p.as("curDate", curDate), 
	          p.as("currentTime", p.fn.currentTime()),
	          p.as("curTime", curTime),
	          p.as("upperCase", p.fn.upperCase(p.col("desc"))),
	          p.as("semNum", p.sem.isLiteral(p.col("desc"))),
	          p.as("sqlHours", p.sql.hours(sqlcurTime)),
	          p.as("sqlHr", sqlHr),
	          p.as("mathLog10", p.math.log10(p.col("sqlHours"))),
	          p.as("log10", log10)
	        )
	        .orderBy(p.col("upperCase"));*/
		// TODO Wait until Erik H comes back on this to discuss the recent changes to SQL types.
	      
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		//rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
		
		// Should have 2 nodes returned.
		assertEquals("Two nodes not returned from testExcept method", 3, jsonBindingsNodes.size());
	}
	
	/* 
	 * Test invalid rowdef
	*/
	@Test
	public void testInvalidDefinitions() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testInvalidDefinitions method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
	
		// plans from literals
		ModifyPlan plan1 = p.fromLiterals(literals1);
		ModifyPlan plan2 = p.fromLiterals(literals2);
		
		ModifyPlan output = plan1.joinInner(plan2)
		        .where(p.eq(p.col("colorId"), p.xs.intVal(1)))
		        .select("rowIdRef", "desc", "colorId", "colorDesc");
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		StringBuilder str = new StringBuilder();
		try {
			rowMgr.resultDoc(output, jacksonHandle);
		}
		catch(Exception ex) {
			str.append(ex.getMessage());
			System.out.println("Exception message is " + str.toString());
		}
		// Should have SQL-NOCOLUMN exceptions.
		assertTrue("Exceptions not found", str.toString().contains("SQL-NOCOLUMN"));
		assertTrue("Exceptions not found", str.toString().contains("Column not found: rowIdRef"));
		
		// invalid viewCol
		ModifyPlan outputExtCol = plan1.joinInner(plan2)
		                               .where(p.eq(p.viewCol("invalid_view", "colorId"), p.xs.intVal(1)))
		                               .offsetLimit(1, 3);
		str = new StringBuilder();
		try {
			rowMgr.resultDoc(outputExtCol, jacksonHandle);
		}
		catch(Exception ex) {
			str.append(ex.getMessage());
			System.out.println("Exception message is " + str.toString());
		}
		// Should have SQL-NOCOLUMN exceptions.
		assertTrue("Exceptions not found", str.toString().contains("SQL-NOCOLUMN"));
		assertTrue("Exceptions not found", str.toString().contains("Column not found: invalid_view.colorId"));		
	}
	
	/* 
	 * Test offsetLimit with negative offset
	*/
	@Test
	public void testNegativeOffset() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testNegativeOffset method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
	
		// plans from literals
		ModifyPlan plan1 = p.fromLiterals(literals1);
		ModifyPlan plan2 = p.fromLiterals(literals2);
		
		ModifyPlan output = plan1.joinInner(plan2)
		                         .where(p.eq(p.col("colorId"), p.xs.intVal(1)))
		                         .offsetLimit(-1, 1)
		                         .select("rowId", "desc", "colorId", "colorDesc");
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		StringBuilder str = new StringBuilder();
		try {
			rowMgr.resultDoc(output, jacksonHandle);
		}
		catch(Exception ex) {
			str.append(ex.getMessage());
			System.out.println("Exception message is " + str.toString());
		}
		// Should have OPTIC-INVALARGS exceptions.
		assertTrue("Exceptions not found", str.toString().contains("OPTIC-INVALARGS"));
		assertTrue("Exceptions not found", str.toString().contains("Invalid arguments: offset must be a non-negative number: -1"));
	}
	
	/* 
	 * Test invalid qualifier
	*/
	@Test
	public void testInvalidQualifier() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testInvalidQualifier method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
	
		// plans from literals
		ModifyPlan plan1 = p.fromLiterals(literals1, "table1");
		ModifyPlan plan2 = p.fromLiterals(literals2, "table2");
		
		ModifyPlan output = plan1.joinInner(plan2)
		                         .where(p.eq(p.viewCol("table1_Invalid", "colorId"), p.viewCol("table2", "colorId")))
		                         .orderBy(p.desc(p.viewCol("table1", "rowId")));
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		StringBuilder str = new StringBuilder();
		try {
			rowMgr.resultDoc(output, jacksonHandle);
		}
		catch(Exception ex) {
			str.append(ex.getMessage());
			System.out.println("Exception message is " + str.toString());
		}
		// Should have OPTIC-INVALARGS exceptions.
		assertTrue("Exceptions not found", str.toString().contains("SQL-NOCOLUMN"));
		assertTrue("Exceptions not found", str.toString().contains("Column not found: table1_Invalid.colorId"));
	}
	
	/* 
	 * Test Map function
	 * Uses JS function colorIdMapper installed in the REST server module database.
	*/
	@Test
	public void testMapFunction() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testMapFunction method");
		Map<String, Object>[] literalsM1 = new HashMap[5];
		Map<String, Object>[] literalsM2 = new HashMap[4];
		
		Map<String, Object> row = new HashMap<>();			
		row.put("rowId", 1); row.put("colorId_shape", 1); row.put("desc", "ball");
		literalsM1[0] = row;
		
		row = new HashMap<>();
        row.put("rowId", 2); row.put("colorId_shape", 2); row.put("desc", "square");
        literalsM1[1] = row;
        
        row = new HashMap<>();
        row.put("rowId", 3); row.put("colorId_shape", 1); row.put("desc", "box");
        literalsM1[2] = row;
        
        row = new HashMap<>();
        row.put("rowId", 4); row.put("colorId_shape", 1); row.put("desc", "hoop");
        literalsM1[3] = row;
        
        row = new HashMap<>();
        row.put("rowId", 5); row.put("colorId_shape", 5); row.put("desc", "circle");
        literalsM1[4] = row;
        	
        // Assemble literalsM2
		row = new HashMap<>();			
		row.put("colorId", 1); row.put("colorDesc", "red");
		literalsM2[0] = row;
		
		row = new HashMap<>();
        row.put("colorId", 2); row.put("colorDesc", "blue");
        literalsM2[1] = row;
        
        row = new HashMap<>();
        row.put("colorId", 3); row.put("colorDesc", "black");
        literalsM2[2] = row;
        
        row = new HashMap<>();
        row.put("colorId", 4); row.put("colorDesc", "yellow");
        literalsM2[3] = row;
        
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		// plans from literals
		ModifyPlan plan1 = p.fromLiterals(literalsM1);
		ModifyPlan plan2 = p.fromLiterals(literalsM2);
				
		ExportablePlan output = plan1.joinInner(plan2, p.on(p.col("colorId_shape"), p.col("colorId")))
		                         .select(
		                                 p.as("rowId", p.col("rowId")),
		                                 p.as("description", p.col("desc")), 
		                                 p.as("myColorId", p.col("colorId"))
		                                 
		                               )
		        .orderBy(p.asc("rowId"))
		        .map(p.resolveFunction(p.xs.QName("colorIdMapper"), "/marklogic.rest.resource/OpticsJSResourceModule/assets/resource.sjs"));

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

		// Should have 4 node returned.
		assertEquals("Four nodes not returned from testMapFunction method", 4, jsonBindingsNodes.size());
		assertEquals("Row 1 rowId value incorrect", "1", jsonBindingsNodes.path(0).path("rowId").path("value").asText());
		assertEquals("Row 1 myColorId value incorrect", "RED ROBIN", jsonBindingsNodes.path(0).path("myColorId").path("value").asText());
		assertEquals("Row 1 description value incorrect", "ball", jsonBindingsNodes.path(0).path("description").path("value").asText());
		
		assertEquals("Row 2 rowId value incorrect", "2", jsonBindingsNodes.path(1).path("rowId").path("value").asText());
		assertEquals("Row 2 myColorId value incorrect", "BLUE JAY", jsonBindingsNodes.path(1).path("myColorId").path("value").asText());
		assertEquals("Row 2 description value incorrect", "square", jsonBindingsNodes.path(1).path("description").path("value").asText());
		
		assertEquals("Row 3 rowId value incorrect", "4", jsonBindingsNodes.path(3).path("rowId").path("value").asText());
		assertEquals("Row 3 myColorId value incorrect", "RED ROBIN", jsonBindingsNodes.path(3).path("myColorId").path("value").asText());
		assertEquals("Row 3 description value incorrect", "hoop", jsonBindingsNodes.path(3).path("description").path("value").asText());
	}
	
	/* 
	 * Test Reduce function
	 * Uses JS function fibReducer installed in the REST server module database.
	*/
	@Test
	public void testReduceFunction() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testReduceFunction method");
		Map<String, Object>[] literalsM1 = new HashMap[5];
		Map<String, Object>[] literalsM2 = new HashMap[4];
		
		Map<String, Object> row = new HashMap<>();			
		row.put("rowId", 1); row.put("colorId_shape", 1); row.put("desc", "ball");
		literalsM1[0] = row;
		
		row = new HashMap<>();
        row.put("rowId", 2); row.put("colorId_shape", 2); row.put("desc", "square");
        literalsM1[1] = row;
        
        row = new HashMap<>();
        row.put("rowId", 3); row.put("colorId_shape", 1); row.put("desc", "box");
        literalsM1[2] = row;
        
        row = new HashMap<>();
        row.put("rowId", 4); row.put("colorId_shape", 1); row.put("desc", "hoop");
        literalsM1[3] = row;
        
        row = new HashMap<>();
        row.put("rowId", 5); row.put("colorId_shape", 5); row.put("desc", "circle");
        literalsM1[4] = row;
        	
        // Assemble literalsM2
		row = new HashMap<>();			
		row.put("colorId", 1); row.put("colorDesc", "red");
		literalsM2[0] = row;
		
		row = new HashMap<>();
        row.put("colorId", 2); row.put("colorDesc", "blue");
        literalsM2[1] = row;
        
        row = new HashMap<>();
        row.put("colorId", 3); row.put("colorDesc", "black");
        literalsM2[2] = row;
        
        row = new HashMap<>();
        row.put("colorId", 4); row.put("colorDesc", "yellow");
        literalsM2[3] = row;
        
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		// plans from literals
		ModifyPlan plan1 = p.fromLiterals(literalsM1);
		ModifyPlan plan2 = p.fromLiterals(literalsM2);
				
		ExportablePlan output = plan1.joinInner(plan2, p.on(p.col("colorId_shape"), p.col("colorId")))
		                         .select(
		                                 p.as("myRowId", p.col("rowId")) 
		                               )
		        .orderBy(p.asc("myRowId"))
		        .reduce(p.resolveFunction(p.xs.QName("fibReducer"), "/marklogic.rest.resource/OpticsJSResourceModule/assets/resource.sjs"));
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");

		// Should have 4 node returned.
		assertEquals("Four nodes not returned from testMapFunction method", 4, jsonBindingsNodes.size());
		assertEquals("Row 1 rowId value incorrect", "1", jsonBindingsNodes.path(0).path("myRowId").path("value").asText());
		assertEquals("Row 1 myColorId value incorrect", "0", jsonBindingsNodes.path(0).path("i").path("value").asText());
		assertEquals("Row 1 description value incorrect", "0", jsonBindingsNodes.path(0).path("fib").path("value").asText());
		
		assertEquals("Row 2 rowId value incorrect", "2", jsonBindingsNodes.path(1).path("myRowId").path("value").asText());
		assertEquals("Row 2 myColorId value incorrect", "1", jsonBindingsNodes.path(1).path("i").path("value").asText());
		assertEquals("Row 2 description value incorrect", "1", jsonBindingsNodes.path(1).path("fib").path("value").asText());
		
		assertEquals("Row 3 rowId value incorrect", "4", jsonBindingsNodes.path(3).path("myRowId").path("value").asText());
		assertEquals("Row 3 myColorId value incorrect", "3", jsonBindingsNodes.path(3).path("i").path("value").asText());
		assertEquals("Row 3 description value incorrect", "2", jsonBindingsNodes.path(3).path("fib").path("value").asText());
	}
	
			
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		System.out.println("In tear down");
		// release client		
		client.release();
		cleanupRESTServer(dbName, fNames);		
	}
}
