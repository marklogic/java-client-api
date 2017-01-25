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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
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
import com.marklogic.client.expression.PlanBuilder.PreparePlan;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.type.ArrayNodeExpr;
import com.marklogic.client.type.CtsReferenceExpr;
import com.marklogic.client.type.PlanColumn;
/* The tests here are for sanity checks when we have plans from different sources
 * such as fromLexicons and fromtriples.
 */
import com.marklogic.client.type.TextNodeExpr;

public class TestOpticOnMixedViews extends BasicJavaClientREST {
	
	private static String dbName = "TestOpticOnMixedViewsDB";
	private static String modulesdbName = "TestOpticOnMixedViewsDB";
	private static String [] fNames = {"TestOpticOnMixedViewsDB-1"};
	private static String [] modulesfNames = {"TestOpticOnMixedViewsModulesDB-1"};
	
	private static int restPort=8011;
	private static DatabaseClient client;
	
	private static String newline;
	private static String datasource = "src/test/java/com/marklogic/client/functionaltest/data/optics/";
	
	@BeforeClass
	public static void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception
	{
		System.out.println("In TestOpticOnMixedViews setup");
		
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
	
	/* Checks for Plan Builder's fromLexicon and fromLiterals method.
	 * plan1 uses fromLexicon
	 * plan2 use fromLiterals
	 * 
	*/
	@Test
	public void testfromLexiconsAndLiterals() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testfromLexiconsAndLiterals method");

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

		Map<String, Object>[] literals1 = new HashMap[5];
		Map<String, Object> row = new HashMap<>();			
		row.put("rowId", 1); row.put("popularity", 1); row.put("desc", "item");
		literals1[0] = row;

		row = new HashMap<>();
		row.put("rowId", 2); row.put("popularity", 2); row.put("desc", "item");
		literals1[1] = row;

		row = new HashMap<>();
		row.put("rowId", 3); row.put("popularity", 1); row.put("desc", "item");
		literals1[2] = row;

		row = new HashMap<>();
		row.put("rowId", 4); row.put("popularity", 1); row.put("desc", "item");
		literals1[3] = row;

		row = new HashMap<>();
		row.put("rowId", 5); row.put("popularity", 5); row.put("desc", "item");
		literals1[4] = row;

		// plan1 - fromLexicons
		ModifyPlan plan1 = p.fromLexicons(indexes, "myCity");
		// plan2 - fromLiterals
		ModifyPlan plan2 = p.fromLiterals(literals1);

		ModifyPlan output = plan1.joinInner(plan2).offsetLimit(0, 3).orderBy(p.viewCol("myCity", "city"));

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();

		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		assertTrue("Number of Elements after plan execution is incorrect. Should be 3", 3 == jsonBindingsNodes.size());
	}
	
	/*
	 * Test join inner with joinInnerDoc
	 * pan1 uses fromLexicon
	 * plan2 use fromLiterals
	 */
	@Test
	public void testJoinInnerWithInnerDocMixed() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testJoinInnerWithInnerDocMixed method");

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

		Map<String, Object>[] literals1 = new HashMap[5];
		Map<String, Object> row = new HashMap<>();			
		row.put("rowId", 1); row.put("popularity", 1); row.put("desc", "item");
		literals1[0] = row;

		row = new HashMap<>();
		row.put("rowId", 2); row.put("popularity", 2); row.put("desc", "item");
		literals1[1] = row;

		row = new HashMap<>();
		row.put("rowId", 3); row.put("popularity", 1); row.put("desc", "item");
		literals1[2] = row;

		row = new HashMap<>();
		row.put("rowId", 4); row.put("popularity", 1); row.put("desc", "item");
		literals1[3] = row;

		row = new HashMap<>();
		row.put("rowId", 5); row.put("popularity", 5); row.put("desc", "item");
		literals1[4] = row;

		// plan1 - fromLexicons
		ModifyPlan plan1 = p.fromLexicons(indexes, "myCity");
		// plan2 - fromLiterals
		ModifyPlan plan2 = p.fromLiterals(literals1);

		ModifyPlan output = plan1.joinInner(plan2)
				.joinDoc(p.col("doc"), p.col("uri"))
				.select("city", "uri", "rowId", "doc")
				.orderBy("rowId", "city")
				.offsetLimit(0, 5);

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();

		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		assertTrue("Number of Elements after plan execution is incorrect. Should be 5", 5 == jsonBindingsNodes.size());
	}
	
	/*
	 * Test join inner between view and lexicon
	 * pan1 uses fromView
	 * plan2 use fromLexicons
	 */
	@Test
	public void testJoinfromViewfronLexicons() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testJoinInnerWithInnerDocMixed method");

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

		// plan1 - fromView
		ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", "myDetail");
		// plan2 - fromLexicons
		ModifyPlan plan2 = p.fromLexicons(indexes, "myCity");
		
		ModifyPlan output = plan1.joinInner(plan2).offsetLimit(0, 2);

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();

		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		assertTrue("Number of Elements after plan execution is incorrect. Should be 2", 2 == jsonBindingsNodes.size());
	}
	
	/*
	 * Test join between triples and literals
	 * pan1 uses fromTriples
	 * plan2 use fromLiterals
	 */
	@Test
	public void testJoinfromTriplesfromLiterals() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testJoinfromTriplesfromLiterals method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		PlanBuilder.Prefixer  bb = p.prefixer("http://marklogic.com/baseball/players");
		
		PlanColumn playerAgeCol = p.col("player_age");
		PlanColumn playerIdCol = p.col("player_id");
		PlanColumn playerNameCol = p.col("player_name");
		PlanColumn playerTeamCol = p.col("player_team");
		
		Map<String, Object>[] literals2 = new HashMap[5];
		Map<String, Object> row = new HashMap<>();			
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

		// plan1 - fromTriples
		ModifyPlan plan1 = p.fromTriples(
				                          p.pattern(playerIdCol, bb.iri("age"), playerAgeCol),
		                                  p.pattern(playerIdCol, bb.iri("name"), playerNameCol),
		                                  p.pattern(playerIdCol, bb.iri("team"), playerTeamCol)
		                                 );
		// plan2 - fromLiterals
		ModifyPlan plan2 = p.fromLiterals(literals2);
		
		ModifyPlan output = plan1.joinLeftOuter(plan2).where
				                 (
		                          p.and
		                          (
		                           p.eq(playerNameCol, p.xs.string("Matt Rose")),
		                           p.or(p.gt(p.col("colorId"), p.xs.intVal(3)), p.lt(p.col("colorId"), p.xs.intVal(2)))
		                          )
		                         )
		                         .orderBy(p.col("colorId"));

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();

		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		assertTrue("Number of Elements after plan execution is incorrect. Should be 2", 2 == jsonBindingsNodes.size());
		assertEquals("Row 1 player_name value incorrect", "Matt Rose", jsonBindingsNodes.path(0).path("player_name").path("value").asText());
		assertEquals("Row 1 colorDesc value incorrect", "red", jsonBindingsNodes.path(0).path("colorDesc").path("value").asText());
		assertEquals("Row 2 player_name value incorrect", "Matt Rose", jsonBindingsNodes.path(1).path("player_name").path("value").asText());
		assertEquals("Row 2 colorDesc value incorrect", "yellow", jsonBindingsNodes.path(1).path("colorDesc").path("value").asText());
	}
	
	/*
	 * Test basic Constructors
	 * plan1 uses fromTriples
	 * plan2 use fromLiterals
	 */
	@Test
	public void testConstructor() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testJoinfromTriplesfromLiterals method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		PlanBuilder.Prefixer  bb = p.prefixer("http://marklogic.com/baseball/players");
		
		PlanColumn playerAgeCol = p.col("player_age");
		PlanColumn playerIdCol = p.col("player_id");
		PlanColumn playerNameCol = p.col("player_name");
		PlanColumn playerTeamCol = p.col("player_team");
		
		Map<String, Object>[] literals2 = new HashMap[5];
		Map<String, Object> row = new HashMap<>();			
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

		// plan1 - fromTriples
		ModifyPlan plan1 = p.fromTriples(
				                          p.pattern(playerIdCol, bb.iri("age"), playerAgeCol),
		                                  p.pattern(playerIdCol, bb.iri("name"), playerNameCol),
		                                  p.pattern(playerIdCol, bb.iri("team"), playerTeamCol)
		                                 );
		// plan2 - fromLiterals
		ModifyPlan plan2 = p.fromLiterals(literals2);
		
		ModifyPlan output = plan1.joinLeftOuter(plan2).where
				                 (
		                          p.and
		                          (
		                           p.eq(playerNameCol, p.xs.string("Matt Rose")),
		                           p.or(p.gt(p.col("colorId"), p.xs.intVal(3)), p.lt(p.col("colorId"), p.xs.intVal(2)))
		                          )
		                         )
		                         .orderBy(p.col("colorId"))
		                         .select(p.as("myResults", 
		                        		 p.jsonDocument(p.jsonObject(p.prop("myRows", p.jsonArray(p.jsonString(p.col("colorId")))))))
                                        );

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();

		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		
		assertTrue("Number of Elements after plan execution is incorrect. Should be 2", 2 == jsonBindingsNodes.size());		
		assertEquals("Row 1 colorDesc value incorrect", "1", jsonBindingsNodes.path(0).path("myResults").path("value").path("myRows").path(0).asText());
		assertEquals("Row 1 colorDesc value incorrect", "4", jsonBindingsNodes.path(1).path("myResults").path("value").path("myRows").path(0).asText());		
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down");
		// release client
		client.release();
		cleanupRESTServer(dbName, fNames);		
	}
}
