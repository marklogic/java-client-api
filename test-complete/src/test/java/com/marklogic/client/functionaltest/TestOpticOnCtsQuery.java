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
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.type.CtsQueryExpr;
import com.marklogic.client.type.CtsQuerySeqExpr;
import com.marklogic.client.type.CtsReferenceExpr;
import com.marklogic.client.type.PlanPrefixer;
import com.marklogic.client.type.XsStringSeqVal;

/* The tests here are for checks on cts queries.
 */

public class TestOpticOnCtsQuery extends BasicJavaClientREST {
	
	private static String dbName = "TestOpticOnCtsQueryDB";
	private static String modulesdbName = "TestOpticOnCtsQueryDB";
	private static String [] fNames = {"TestOpticOnCtsQueryDB-1"};
	private static String [] modulesfNames = {"TestOpticOnCtsQueryModulesDB-1"};
	
	private static int restPort=8011;
	private static DatabaseClient client;
	
	private static String newline;
	private static String datasource = "src/test/java/com/marklogic/client/functionaltest/data/optics/";
	
	@BeforeClass
	public static void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception
	{
		System.out.println("In TestOpticOnCtsQuery setup");
		
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
				{"int", "", "id", "", "false", "reject"},
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
	
	/* Checks for Plan Builder's jsonPropertyWordQuery
	 * TEST 9 - jsonPropertyWordQuery on fromViews.
	 * plan1 uses fromView
	 * plan2 use fromView
	 * 
	 * 
	*/
	@Test
	public void testJsonPropertyWordQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testJsonPropertyWordQuery method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		// plan1 - fromView
		ModifyPlan plan1 = p.fromView("opticFunctionalTest4", "detail4", null, null)
				            .where(p.cts.jsonPropertyWordQuery("name", "Detail 100"));
		// plan2 - fromView
		ModifyPlan plan2 = p.fromView("opticFunctionalTest4", "master4");
		
		ModifyPlan output = plan1.joinInner(plan2, p.on(p.schemaCol("opticFunctionalTest4", "detail4", "masterId"), p.schemaCol("opticFunctionalTest4", "master4", "id")))
		        .orderBy(p.schemaCol("opticFunctionalTest4", "detail4", "id"));

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();

		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		assertTrue("Number of Elements after plan execution is incorrect. Should be 3", 3 == jsonBindingsNodes.size());
		assertEquals("Row 1 opticFunctionalTest4.detail4.id value incorrect", "100", jsonBindingsNodes.path(0).path("opticFunctionalTest4.detail4.id").path("value").asText());
		assertEquals("Row 1 opticFunctionalTest4.master4.name value incorrect", "Master 100", jsonBindingsNodes.path(0).path("opticFunctionalTest4.master4.name").path("value").asText());
		assertEquals("Row 2 opticFunctionalTest4.detail4.name value incorrect", "Detail 200", jsonBindingsNodes.path(1).path("opticFunctionalTest4.detail4.name").path("value").asText());
		assertEquals("Row 2 opticFunctionalTest4.master4.date value incorrect", "2016-04-02", jsonBindingsNodes.path(1).path("opticFunctionalTest4.master4.date").path("value").asText());
		assertEquals("Row 3 opticFunctionalTest4.detail4.amount value incorrect", "72.9", jsonBindingsNodes.path(2).path("opticFunctionalTest4.detail4.amount").path("value").asText());
		assertEquals("Row 3 opticFunctionalTest4.detail4.color value incorrect", "yellow", jsonBindingsNodes.path(2).path("opticFunctionalTest4.detail4.color").path("value").asText());		
	}
	
	/*
	 * Test join inner with joinInnerDoc - TEST 2
	 * plan1 uses fromLexicon
	 * plan2 use fromLexicons
	 */
	@Test
	public void testjsonPropertyWordAndValueQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testjsonPropertyWordAndValueQuery method");

		/*// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		Map<String, CtsReferenceExpr>index1 = new HashMap<String, CtsReferenceExpr>();
		index1.put("uri1", p.cts.uriReference());
		index1.put("city", p.cts.jsonPropertyReference("city"));
		index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
		index1.put("date", p.cts.jsonPropertyReference("date"));
		index1.put("distance", p.cts.jsonPropertyReference("distance"));
		index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));

		Map<String, CtsReferenceExpr>index2 = new HashMap<String, CtsReferenceExpr>();
		index2.put("uri2", p.cts.uriReference());
		index2.put("cityName", p.cts.jsonPropertyReference("cityName"));
		index2.put("cityTeam", p.cts.jsonPropertyReference("cityTeam"));		
		// TEST 2 - jsonPropertyValueQuery on fromLexicons
		 // plan1 - fromLexicons		 
		ModifyPlan plan1 = p.fromLexicons(index1, "myCity", p.fragmentIdCol("fragId1"), p.cts.jsonPropertyWordQuery("city", "new"));
		// plan2 - fromLexicons
		ModifyPlan plan2 = p.fromLexicons(index2, "myTeam", p.fragmentIdCol("fragId2"), p.cts.jsonPropertyValueQuery("cityTeam", p.xs.string("yankee")));
		
		ModifyPlan output2 = plan1.joinInner(plan2)
        .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
        .orderBy(p.asc(p.col("date")));
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		rowMgr.resultDoc(output2, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();

		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		
		assertTrue("Number of Elements after plan execution is incorrect. Should be 1", 1 == jsonBindingsNodes.size());
		assertEquals("Row 1 myCity.city value incorrect", "new york", jsonBindingsNodes.path(0).path("myCity.city").path("value").asText());
		assertEquals("Row 1 myCity.point value incorrect", "40.709999,-74.009995", jsonBindingsNodes.path(0).path("myCity.point").path("value").asText());
		assertEquals("Row 1 myTeam.uri2 value incorrect", "/optic/lexicon/test/city2.json", jsonBindingsNodes.path(0).path("myTeam.uri2").path("value").asText());*/
	}
	
	/* Checks for jsonPropertyGeospatialQuery with circle on fromLexicons
	 * TEST 4 - jsonPropertyWordQuery on fromLexicons with circle	
	 * 
	*/
	@Test
	public void testJsonPropertyGeospatialQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{/*
		System.out.println("In testJsonPropertyGeospatialQuery method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		Map<String, CtsReferenceExpr>index1 = new HashMap<String, CtsReferenceExpr>();
		index1.put("uri1", p.cts.uriReference());
		index1.put("city", p.cts.jsonPropertyReference("city"));
		index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
		index1.put("date", p.cts.jsonPropertyReference("date"));
		index1.put("distance", p.cts.jsonPropertyReference("distance"));
		index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));

		Map<String, CtsReferenceExpr>index2 = new HashMap<String, CtsReferenceExpr>();
		index2.put("uri2", p.cts.uriReference());
		index2.put("cityName", p.cts.jsonPropertyReference("cityName"));
		index2.put("cityTeam", p.cts.jsonPropertyReference("cityTeam"));
		
		// plan1 - fromLexicons
		PlanSystemColumn fIdCol1 = p.fragmentIdCol("fragId1");
		PlanSystemColumn fIdCol2 = p.fragmentIdCol("fragId2");
		QualifiedPlan plan1 =  p.fromLexicons(index1, "myCity", fIdCol1, p.cts.jsonPropertyGeospatialQuery("latLonPoint", p.cts.box(49.16, -13.41, 60.85, 1.76)));
		// plan2 - fromLexicons
		QualifiedPlan plan2 = p.fromLexicons(index2, "myTeam", fIdCol2, null);
		
		ModifyPlan output = plan1.joinInner(plan2)
		                         .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
		                         .orderBy(p.asc(p.col("date")));

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();

		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		assertTrue("Number of Elements after plan execution is incorrect. Should be 1", 1 == jsonBindingsNodes.size());
		assertEquals("Row 1 myCity.city value incorrect", "london", jsonBindingsNodes.path(0).path("myCity.city").path("value").asText());
		assertEquals("Row 1 myCity.uri1 value incorrect", "/optic/lexicon/test/doc1.json", jsonBindingsNodes.path(0).path("myCity.uri1").path("value").asText());
		assertEquals("Row 1 myTeam.uri2 value incorrect", "/optic/lexicon/test/city1.json", jsonBindingsNodes.path(0).path("myTeam.uri2").path("value").asText());
	*/}
	
	/*
	 * Test testWordQueryPropertyValueQueryFromViews
	 * plan1 uses fromView
	 * plan2 use fromView
	 */
	@Test
	public void testWordQueryPropertyValueQueryFromViews() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{/*
		System.out.println("In testWordQueryPropertyValueQueryFromViews method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		PlanPrefixer  bb = p.prefixer("http://marklogic.com/baseball/players");
		
		// plan1 - fromView
		ModifyPlan plan1 = p.fromView("opticFunctionalTest4", "detail4", null, null, 
				                       p.cts.jsonPropertyValueQuery("id", p.xs.string("600"))
				                     );
		// plan2 - fromView
		ModifyPlan  plan2 = p.fromView("opticFunctionalTest4", "master4", null, null,
				                        p.cts.wordQuery("Master 100")
				                      );
		
		ModifyPlan output = plan1.joinInner(plan2, p.on(p.schemaCol("opticFunctionalTest4", "detail4", "masterId"),
				                                   p.schemaCol("opticFunctionalTest4", "master4", "id")))
		                         .orderBy(p.schemaCol("opticFunctionalTest4", "detail4", "id"));

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();

		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		assertTrue("Number of Elements after plan execution is incorrect. Should be 3", 3 == jsonBindingsNodes.size());
		assertEquals("Row 1 opticFunctionalTest4.detail4.id value incorrect", "400", jsonBindingsNodes.path(0).path("opticFunctionalTest4.detail4.id").path("value").asText());
		assertEquals("Row 1 opticFunctionalTest4.master4.name value incorrect", "Master 200", jsonBindingsNodes.path(0).path("opticFunctionalTest4.master4.name").path("value").asText());
		assertEquals("Row 3 opticFunctionalTest4.detail4.id value incorrect", "600", jsonBindingsNodes.path(2).path("opticFunctionalTest4.detail4.id").path("value").asText());
		assertEquals("Row 3 opticFunctionalTest4.master4.name value incorrect", "Master 100", jsonBindingsNodes.path(2).path("opticFunctionalTest4.master4.name").path("value").asText());
	*/}
	
	/* Checks for nearQuery on fromLexicons
	 * TEST 13	
	 * 
	*/
	@Test
	public void testNearQueryFromLexicons() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testNearQueryFromLexicons method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		Map<String, CtsReferenceExpr>index1 = new HashMap<String, CtsReferenceExpr>();
		index1.put("uri1", p.cts.uriReference());
		index1.put("city", p.cts.jsonPropertyReference("city"));
		index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
		index1.put("date", p.cts.jsonPropertyReference("date"));
		index1.put("distance", p.cts.jsonPropertyReference("distance"));
		index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));

		Map<String, CtsReferenceExpr>index2 = new HashMap<String, CtsReferenceExpr>();
		index2.put("uri2", p.cts.uriReference());
		index2.put("cityName", p.cts.jsonPropertyReference("cityName"));
		index2.put("cityTeam", p.cts.jsonPropertyReference("cityTeam"));
		
		// plan1 - fromLexicons
		// Create an and query that is equivalent to array of queries
		
		CtsQuerySeqExpr andQuery = p.cts.andQuery(p.cts.wordQuery("near"), p.cts.wordQuery("Thames"));
		ModifyPlan plan1 =  p.fromLexicons(index1, "myCity", p.fragmentIdCol("fragId1"))
				             .where(p.cts.nearQuery(andQuery, p.xs.doubleVal(3)));
		// plan2 - fromLexicons
		ModifyPlan plan2 = p.fromLexicons(index2, "myTeam", p.fragmentIdCol("fragId2"));
		
		ModifyPlan output = plan1.joinInner(plan2)
		                         .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
		                         .orderBy(p.asc(p.col("date")));

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();

		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		assertTrue("Number of Elements after plan execution is incorrect. Should be 1", 1 == jsonBindingsNodes.size());
		assertEquals("Row 1 myCity.city value incorrect", "london", jsonBindingsNodes.path(0).path("myCity.city").path("value").asText());
		assertEquals("Row 1 myCity.uri1 value incorrect", "/optic/lexicon/test/doc1.json", jsonBindingsNodes.path(0).path("myCity.uri1").path("value").asText());
		assertEquals("Row 1 myTeam.uri2 value incorrect", "/optic/lexicon/test/city1.json", jsonBindingsNodes.path(0).path("myTeam.uri2").path("value").asText());
		assertEquals("Row 1 myCity.fragId1 type value incorrect", "sem:iri", jsonBindingsNodes.path(0).path("myCity.fragId1").path("type").asText());
		assertEquals("Row 1 myTeam.fragId2 value incorrect", "sem:iri", jsonBindingsNodes.path(0).path("myTeam.fragId2").path("type").asText());		
	}
	
	/* Checks for cts queries with options on fromLexicons
	 * TEST 14
	 * 
	 * TODO when https://github.com/marklogic/java-client-api/issues/633 is fixed.	
	 * 
	*/
	@Test
	public void testCtsQueriesWithOptions() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testCtsQueriesWithOptions method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		Map<String, CtsReferenceExpr>index1 = new HashMap<String, CtsReferenceExpr>();
		index1.put("uri1", p.cts.uriReference());
		index1.put("city", p.cts.jsonPropertyReference("city"));
		index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
		index1.put("date", p.cts.jsonPropertyReference("date"));
		index1.put("distance", p.cts.jsonPropertyReference("distance"));
		index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));		

		Map<String, CtsReferenceExpr>index2 = new HashMap<String, CtsReferenceExpr>();
		index2.put("uri2", p.cts.uriReference());
		index2.put("cityName", p.cts.jsonPropertyReference("cityName"));
		index2.put("cityTeam", p.cts.jsonPropertyReference("cityTeam"));
		
		// plan1 - fromLexicons		
		ModifyPlan plan1 =  p.fromLexicons(index1, "myCity", p.fragmentIdCol("fragId1"));
		// plan2 - fromLexicons
		ModifyPlan plan2 = p.fromLexicons(index2, "myTeam", p.fragmentIdCol("fragId2"));
		
		XsStringSeqVal propertyName = p.xs.string("city");
		XsStringSeqVal value = p.xs.string("*k");
		
		XsStringSeqVal options = p.xs.stringSeq("wildcarded", "case-sensitive");
				
		ModifyPlan output = plan1.where(p.cts.jsonPropertyWordQuery(propertyName, value, options))
								 .joinInner(plan2)
		                         .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
		                         .orderBy(p.asc(p.col("date")));

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();

		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		// Git Issue 633
		assertTrue("Number of Elements after plan execution is incorrect. Should be 1", 1 == jsonBindingsNodes.size());		
		assertEquals("Row 1 myCity.city value incorrect", "new york", jsonBindingsNodes.path(1).path("myCity.city").path("value").asText());
	}
	
	/*
	 * Test jsonPropertyRangeQuery
	 * plan1 uses fromView
	 * plan2 use fromView
	 */
	@Test
	public void testJsonPropertyRangeQueryFromViews() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testJsonPropertyRangeQueryFromViews method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		PlanPrefixer  bb = p.prefixer("http://marklogic.com/baseball/players");
		
		// plan1 - fromView
		ModifyPlan plan1 = p.fromView("opticFunctionalTest4", "detail4");
				                     
		// plan2 - fromLiterals
		ModifyPlan  plan2 = p.fromView("opticFunctionalTest4", "master4");
		
		ModifyPlan output = plan1.where(p.cts.jsonPropertyRangeQuery("id", ">", "300"))
				                 .joinInner(plan2, p.on(
				                                         p.schemaCol("opticFunctionalTest4", "detail4", "masterId"),
				                                         p.schemaCol("opticFunctionalTest4", "master4", "id")
				                                       )
				                           )
		                         .orderBy(p.schemaCol("opticFunctionalTest4", "detail4", "id"));

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();

		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		assertTrue("Number of Elements after plan execution is incorrect. Should be 3", 3 == jsonBindingsNodes.size());
		assertEquals("Row 1 opticFunctionalTest4.detail4.id value incorrect", "400", jsonBindingsNodes.path(0).path("opticFunctionalTest4.detail4.id").path("value").asText());
		assertEquals("Row 1 opticFunctionalTest4.master4.name value incorrect", "Master 200", jsonBindingsNodes.path(0).path("opticFunctionalTest4.master4.name").path("value").asText());
		assertEquals("Row 3 opticFunctionalTest4.detail4.id value incorrect", "600", jsonBindingsNodes.path(2).path("opticFunctionalTest4.detail4.id").path("value").asText());
		assertEquals("Row 3 opticFunctionalTest4.master4.name value incorrect", "Master 100", jsonBindingsNodes.path(2).path("opticFunctionalTest4.master4.name").path("value").asText());
	}
	
	/*
	 * Test export and import on more complex queries - TEST 18
	 * plan1 uses fromLexicon
	 * plan2 use fromLexicons
	 */
	@Test
	public void testQNameExport() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testjsonPropertyWordAndValueQuery method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		Map<String, CtsReferenceExpr>index1 = new HashMap<String, CtsReferenceExpr>();
		index1.put("uri1", p.cts.uriReference());
		index1.put("city", p.cts.jsonPropertyReference("city"));
		index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
		index1.put("date", p.cts.jsonPropertyReference("date"));
		index1.put("distance", p.cts.jsonPropertyReference("distance"));
		index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));

		Map<String, CtsReferenceExpr>index2 = new HashMap<String, CtsReferenceExpr>();
		index2.put("uri2", p.cts.uriReference());
		index2.put("cityName", p.cts.jsonPropertyReference("cityName"));
		index2.put("cityTeam", p.cts.jsonPropertyReference("cityTeam"));
		
		// export of the plan as String.
		
		ModifyPlan plan1 = p.fromLexicons(index1, "myCity",
				                          p.fragmentIdCol("fragId1")); 				                          
		// plan2 - fromLexicons
		ModifyPlan plan2 = p.fromLexicons(index2, "myTeam", p.fragmentIdCol("fragId2"));
		
		ModifyPlan output = plan1.where(p.cts.orQuery(p.cts.collectionQuery("/other/coll1"), p.cts.elementValueQuery("metro", "true")))
								 .joinInner(plan2)
		                         .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
		                         .orderBy(p.asc(p.col("date")));
		StringHandle strHandle = new StringHandle();
		
		// Export the plan.
		String str = output.export(strHandle).get();
		System.out.println("Export of plan as String " + str);

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		assertTrue("Number of Elements after plan execution is incorrect. Should be 3", 3 == jsonBindingsNodes.size());
		assertEquals("Row 1 myCity.city value incorrect", "beijing", jsonBindingsNodes.path(0).path("myCity.city").path("value").asText());
		assertEquals("Row 1 myCity.point value incorrect", "39.900002,116.4", jsonBindingsNodes.path(0).path("myCity.point").path("value").asText());
		assertEquals("Row 2 myCity.city value incorrect", "cape town", jsonBindingsNodes.path(1).path("myCity.city").path("value").asText());
		assertEquals("Row 2 myCity.point value incorrect", "-33.91,18.42", jsonBindingsNodes.path(1).path("myCity.point").path("value").asText());
		assertEquals("Row 3 myCity.city value incorrect", "london", jsonBindingsNodes.path(2).path("myCity.city").path("value").asText());
		assertEquals("Row 3 myCity.point value incorrect", "51.5,-0.12", jsonBindingsNodes.path(2).path("myCity.point").path("value").asText());
		
		// Verify exported string with QNAME - with random checks
		assertTrue("Function not available fromLexicons in exported plan", str.contains("\"fn\":\"from-lexicons\""));	
		assertTrue("Function not available fromLexicons in exported plan", str.contains("\"fn\":\"fragment-id-col\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"fragId1\"]"));
		assertTrue("Function not available fromLexicons in exported plan", str.contains("\"fn\":\"QName\", \"args\":[\"metro\"]"));
	}
	
	/*
	 * Test cts queries with options and empty results on fromLexicons - TEST 15 and 16
	 * plan1 uses fromLexicon
	 * plan2 use fromLexicons
	 * TODO when 633 is fixed.
	 */
	@Test
	public void testEmptyAndInvalidResults() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{/*
		System.out.println("In testEmptyAndInvalidResults method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		Map<String, CtsReferenceExpr>index1 = new HashMap<String, CtsReferenceExpr>();
		index1.put("uri1", p.cts.uriReference());
		index1.put("city", p.cts.jsonPropertyReference("city"));
		index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
		index1.put("date", p.cts.jsonPropertyReference("date"));
		index1.put("distance", p.cts.jsonPropertyReference("distance"));
		index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));

		Map<String, CtsReferenceExpr>index2 = new HashMap<String, CtsReferenceExpr>();
		index2.put("uri2", p.cts.uriReference());
		index2.put("cityName", p.cts.jsonPropertyReference("cityName"));
		index2.put("cityTeam", p.cts.jsonPropertyReference("cityTeam"));
		
		// TODO Ask Eric about query options.
				
		ModifyPlan plan1 = p.fromLexicons(index1, "myCity",
				                         p.fragmentIdCol("fragId1"), p.cts.jsonPropertyWordQuery("city", "London", "case-sensitive"));
		// plan2 - fromLexicons
		ModifyPlan plan2 = p.fromLexicons(index2, "myTeam", p.fragmentIdCol("fragId2"), null);
		
		ModifyPlan outputEmpty = plan1.joinInner(plan2)
				                 .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
		                         .orderBy(p.asc(p.col("date")));
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		rowMgr.resultDoc(outputEmpty, jacksonHandle);
		JsonNode node = null;
		StringBuilder strNull = new StringBuilder();
		int nSize = 0;  
		try {
			node = jacksonHandle.get();
			nSize = node.size();
		}
		catch(Exception ex) {
			strNull.append(ex.getMessage());
			System.out.println("Exception message is " + strNull.toString());
		}
		// Should have NullPointerException.
		assertTrue("Exceptions not found", strNull.toString().contains("null"));
		
		// Invalid operation.
		ModifyPlan plan3 = p.fromView("opticFunctionalTest4", "detail4", null, null, p.cts.jsonPropertyRangeQuery("id", "#", p.xs.intVal(300)));
		ModifyPlan plan4 = p.fromView("opticFunctionalTest4", "master4");

		ModifyPlan outputInvalid = plan3.joinInner(plan4, 
				                                   p.on(p.schemaCol("opticFunctionalTest4", "detail4", "masterId"),
				                                   p.schemaCol("opticFunctionalTest4", "master4", "id")))
				                         .orderBy(p.schemaCol("opticFunctionalTest4", "detail4", "id"));
		JacksonHandle jacksonHandleInval = new JacksonHandle();
		jacksonHandleInval.setMimetype("application/json");
		StringBuilder strInv = new StringBuilder();
		
		try {
			rowMgr.resultDoc(outputInvalid, jacksonHandleInval);
		}
		catch(Exception ex) {
			strInv.append(ex.getMessage());
			System.out.println("Exception message is " + strInv.toString());
		}
		// Should have Internal Server Error. Server Message: JS-JAVASCRIPT. Checking part of exception message.
		assertTrue("Exceptions not found", strInv.toString().contains("XDMP-ARG: cts.jsonPropertyRangeQuery(\"id\", \"#\", xs.int(\"300\")) -- op is invalid"));
		
	*/}
	

	/*
	 * Test multiple queries linearly.
	 * plan1 uses fromView
	 * plan2 use fromView
	 */
	@Test
	public void testMultipleQuriesLinear() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testMultipleQuriesLinear method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		PlanPrefixer  bb = p.prefixer("http://marklogic.com/baseball/players");
		
		// plan1 - fromView
		CtsQueryExpr andQuery1 = p.cts.andQuery(p.cts.jsonPropertyWordQuery("id", "400"), 
				                        	    p.cts.jsonPropertyWordQuery("name", "Detail 400")				                         
						                       );
		CtsQueryExpr andQuery2 = p.cts.andQuery(p.cts.jsonPropertyWordQuery("id", "500"), 
     	                                        p.cts.jsonPropertyWordQuery("name", "Detail 500")				                         
                                               );
		CtsQueryExpr andQuery3 = p.cts.andQuery(p.cts.jsonPropertyWordQuery("id", "600"), 
                                                p.cts.jsonPropertyWordQuery("name", "Detail 600")				                         
                                               );
		CtsQueryExpr orQuery = p.cts.orQuery(andQuery1, andQuery2, andQuery3);
		
		ModifyPlan plan1 = p.fromView("opticFunctionalTest4", "detail4");
				                     
		// plan2 - fromLiterals
		ModifyPlan  plan2 = p.fromView("opticFunctionalTest4", "master4");
		
		ModifyPlan output = plan1.where(orQuery).joinInner(plan2, p.on(
				                                         p.schemaCol("opticFunctionalTest4", "detail4", "masterId"),
				                                         p.schemaCol("opticFunctionalTest4", "master4", "id")
				                                       )
				                           )
		                         .orderBy(p.schemaCol("opticFunctionalTest4", "detail4", "id"));

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();

		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		assertTrue("Number of Elements after plan execution is incorrect. Should be 3", 3 == jsonBindingsNodes.size());
		assertEquals("Row 1 opticFunctionalTest4.detail4.id value incorrect", "400", jsonBindingsNodes.path(0).path("opticFunctionalTest4.detail4.id").path("value").asText());
		assertEquals("Row 1 opticFunctionalTest4.master4.name value incorrect", "Master 200", jsonBindingsNodes.path(0).path("opticFunctionalTest4.master4.name").path("value").asText());
		assertEquals("Row 3 opticFunctionalTest4.detail4.id value incorrect", "600", jsonBindingsNodes.path(2).path("opticFunctionalTest4.detail4.id").path("value").asText());
		assertEquals("Row 3 opticFunctionalTest4.master4.name value incorrect", "Master 100", jsonBindingsNodes.path(2).path("opticFunctionalTest4.master4.name").path("value").asText());
	}
	
	/*
	 * Test multiple queries nested.
	 * plan1 uses fromView
	 * 
	 */
	@Test
	public void testMultipleQuriesNested() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testMultipleQuriesNested method");
		System.out.println("In testMultipleQuriesLinear method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		PlanPrefixer  bb = p.prefixer("http://marklogic.com/baseball/players");
		
		// plan1 - fromView
		CtsQueryExpr andQuery3 = p.cts.jsonPropertyWordQuery("id", "1");
		CtsQueryExpr andQuery4 = p.cts.jsonPropertyWordQuery("amount", "20.2");
		CtsQueryExpr andQuery7 = p.cts.jsonPropertyWordQuery("id", "4");
		CtsQueryExpr andQuery8 = p.cts.jsonPropertyWordQuery("name", "Detail 6");		
		CtsQueryExpr andQuery5 = p.cts.jsonPropertyWordQuery("name", "Master 1");			                         
				
		CtsQueryExpr andQuery6 = p.cts.jsonPropertyWordQuery("name", "Master 2");
		
		CtsQueryExpr andQuery1 = p.cts.andQuery(andQuery3, andQuery4);
		CtsQueryExpr andQuery2 = p.cts.andQuery(andQuery7, andQuery8);
		CtsQueryExpr andQuery9 = p.cts.andQuery(andQuery5, andQuery6);
		
		CtsQueryExpr orQuery = p.cts.orQuery(andQuery1, andQuery2);
		CtsQueryExpr andQuery10 = p.cts.andQuery(orQuery, andQuery9);
		
		ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail").where(andQuery10);
        
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(plan1, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();

		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		// Make sure that nested and queries do not blow up the plan.
		assertTrue("Number of Elements after plan execution is incorrect. Should be 6", 6 == jsonBindingsNodes.size());
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
