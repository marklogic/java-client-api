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
import com.marklogic.client.type.CtsReferenceExpr;
import com.marklogic.client.type.PlanColumn;
import com.marklogic.client.type.PlanSystemColumn;

public class TestOpticOnLexicons extends BasicJavaClientREST {
	
	private static String dbName = "TestOpticOnLexiconsDB";
	private static String modulesdbName = "TestOpticOnLexiconsDB";
	private static String [] fNames = {"TestOpticOnLexiconsDB-1"};
	private static String [] modulesfNames = {"TestOpticOnLexiconsModulesDB-1"};
	
	private static int restPort=8011;
	private static DatabaseClient client;
	
	private static String newline;
	private static String datasource = "src/test/java/com/marklogic/client/functionaltest/data/optics/";
	
	@BeforeClass
	public static void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception
	{
		System.out.println("In TestOpticOnLexicons setup");
		
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
	
	/* Checks for Plan Builder's fromLexicon method.
	 * 1 plan1 uses strings as col names, with date ordered and using intval
	 * 2 plan2 use cols() on select method
	 * 3 plan3 use strings on select
	 * 
	*/
	@Test
	public void testfromLexicons() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testfromLexicons method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		Map<String, CtsReferenceExpr>indexes = new HashMap<String, CtsReferenceExpr>();
		indexes.put("uri", p.cts.uriReference());
		indexes.put("city", p.cts.jsonPropertyReference("city"));
		indexes.put("popularity", p.cts.jsonPropertyReference("popularity"));
		indexes.put("date", p.cts.jsonPropertyReference("date"));
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
		
		JsonNode jsonBindingsNodes = jsonResults.path("rows");
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
		
		JsonNode jsonBindingsNodes2 = jsonResults2.path("rows");
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

		JsonNode jsonBindingsNodes3 = jsonResults3.path("rows");
		assertTrue("Number of Elements after plan execution is incorrect. Should be 3", 3 == jsonBindingsNodes3.size());	
	}
	
	/*
	 * Test join inner with joinInnerDoc
	 */
	@Test
	public void testJoinInnerWithInnerDocfromLexicons() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testJoinInnerWithInnerDocfromLexicons method");

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
	
		// plan1
		ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
		// plan2
		ModifyPlan plan2 = p.fromLexicons(index2, "myTeam");
		
		// plan3
		ModifyPlan plan3 = plan1.joinInner(plan2)
				                .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
			                    .orderBy(p.asc(p.col("date")))
			                    .joinInnerDoc("doc", "uri2");
		
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(plan3, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		
		StringHandle strHandle = new StringHandle();
		strHandle.setMimetype("application/json");

		rowMgr.resultDoc(plan3, strHandle);
		String strContent = strHandle.get();
		
		JsonNode jsonInnerDocNodes = jsonResults.path("rows");
		assertTrue("Number of Elements after plan execution is incorrect. Should be 5", 5 == jsonInnerDocNodes.size());
		//Verify first result
		assertEquals("Element 1 (myCity) in date incorrect", "1971-12-23", jsonInnerDocNodes.get(0).path("myCity.date").path("value").asText());
		assertEquals("Element 1 (myCity) in URI1 incorrect", "/optic/lexicon/test/doc3.json", jsonInnerDocNodes.get(0).path("myCity.uri1").path("value").asText());
		assertEquals("Element 1 (myCity) in distance incorrect", "12.9", jsonInnerDocNodes.get(0).path("myCity.distance").path("value").asText());
		assertEquals("Element 1 (myCity) in city incorrect", "new jersey", jsonInnerDocNodes.get(0).path("myCity.city").path("value").asText());
		assertEquals("Element 1 (myCity) in popularity incorrect", "2", jsonInnerDocNodes.get(0).path("myCity.popularity").path("value").asText());
		assertEquals("Element 1 (myCity) in point incorrect", "40.720001,-74.07", jsonInnerDocNodes.get(0).path("myCity.point").path("value").asText());
		
		assertEquals("Element 1 (myTeam) in URI2 incorrect", "/optic/lexicon/test/city3.json", jsonInnerDocNodes.get(0).path("myTeam.uri2").path("value").asText());
		assertEquals("Element 1 (myTeam) in city incorrect", "new jersey", jsonInnerDocNodes.get(0).path("myTeam.cityName").path("value").asText());
		assertEquals("Element 1 (myTeam) in team incorrect", "nets", jsonInnerDocNodes.get(0).path("myTeam.cityTeam").path("value").asText());
		
		assertEquals("Element 1 (doc) in city incorrect", "new jersey", jsonInnerDocNodes.get(0).path("doc").path("value").path("cityName").asText());
		assertEquals("Element 1 (doc) in population incorrect", "3000000", jsonInnerDocNodes.get(0).path("doc").path("value").path("cityPopulation").asText());
		assertEquals("Element 1 (doc) in team incorrect", "nets", jsonInnerDocNodes.get(0).path("doc").path("value").path("cityTeam").asText());
		
		assertEquals("Element 2 (myCity) in date incorrect", "1981-11-09", jsonInnerDocNodes.get(1).path("myCity.date").path("value").asText());
		assertEquals("Element 3 (myCity) in date incorrect", "1999-04-22", jsonInnerDocNodes.get(2).path("myCity.date").path("value").asText());
		assertEquals("Element 4 (myCity) in date incorrect", "2006-06-23", jsonInnerDocNodes.get(3).path("myCity.date").path("value").asText());
		
		//Verify lasst result, since records are ordered.
		assertEquals("Element 5 (myCity) in date incorrect", "2007-01-01", jsonInnerDocNodes.get(4).path("myCity.date").path("value").asText());
		assertEquals("Element 5 (myCity) in URI1 incorrect", "/optic/lexicon/test/doc1.json", jsonInnerDocNodes.get(4).path("myCity.uri1").path("value").asText());
		assertEquals("Element 5 (myCity) in distance incorrect", "50.4", jsonInnerDocNodes.get(4).path("myCity.distance").path("value").asText());
		assertEquals("Element 5 (myCity) in city incorrect", "london", jsonInnerDocNodes.get(4).path("myCity.city").path("value").asText());
		assertEquals("Element 5 (myCity) in popularity incorrect", "5", jsonInnerDocNodes.get(4).path("myCity.popularity").path("value").asText());
		assertEquals("Element 5 (myCity) in point incorrect", "51.5,-0.12", jsonInnerDocNodes.get(4).path("myCity.point").path("value").asText());
		
		assertEquals("Element 5 (myTeam) in URI2 incorrect", "/optic/lexicon/test/city1.json", jsonInnerDocNodes.get(4).path("myTeam.uri2").path("value").asText());
		assertEquals("Element 5 (myTeam) in city incorrect", "london", jsonInnerDocNodes.get(4).path("myTeam.cityName").path("value").asText());
		assertEquals("Element 5 (myTeam) in team incorrect", "arsenal", jsonInnerDocNodes.get(4).path("myTeam.cityTeam").path("value").asText());
		
		assertEquals("Element 5 (doc) in city incorrect", "london", jsonInnerDocNodes.get(4).path("doc").path("value").path("cityName").asText());
		assertEquals("Element 5 (doc) in population incorrect", "2000000", jsonInnerDocNodes.get(4).path("doc").path("value").path("cityPopulation").asText());
		assertEquals("Element 5 (doc) in team incorrect", "arsenal", jsonInnerDocNodes.get(4).path("doc").path("value").path("cityTeam").asText());
		
		// Validate RowRecord.
		// Validate the document content, Kind and MimeType.
		RowSet<RowRecord> recordRowSet = rowMgr.resultRows(plan3);
		Iterator<RowRecord> recordRowItr = recordRowSet.iterator();
		RowRecord recordRow = recordRowItr.next();
				
		assertEquals("Element 1 (myCity) in date incorrect", "1971-12-23", recordRow.getString("myCity.date"));
		assertEquals("Element 1 (myCity) in URI1 incorrect", "/optic/lexicon/test/doc3.json",  recordRow.getString("myCity.uri1"));
		assertEquals(12.9, recordRow.getFloat("myCity.distance"), 0.1);
		assertEquals("Element 1 (myCity) in city incorrect", "new jersey", recordRow.getString("myCity.city"));
		assertEquals("Element 1 (myCity) in popularity incorrect", 2, recordRow.getInt("myCity.popularity"));
		assertEquals("Element 1 (myCity) in point incorrect", "40.720001,-74.07", recordRow.getString("myCity.point"));
				
		// Use a handle different from Jackson.		
		StringHandle strDocHandle = new StringHandle();
		recordRow.getContent("doc", strDocHandle);
		String docAsaString = strDocHandle.get();
		
		// Validate the document returned.		
		assertTrue("Document does not have correct cityName value", docAsaString.contains("new jersey"));
		assertTrue("Document does not have cityname field", docAsaString.contains("cityName"));
		assertTrue("Document does not have correct cityName value", docAsaString.contains("3000000"));
		assertTrue("Document does not have cityname field", docAsaString.contains("cityPopulation"));
		assertTrue("Document does not have correct cityName value", docAsaString.contains("nets"));
		assertTrue("Document does not have cityname field", docAsaString.contains("cityTeam"));
		
		// Validate the format and Mime-type.
		assertTrue("Document format incorrect", recordRow.getContentFormat("doc") == Format.JSON);
		assertTrue("Document Mime-type incorrect", recordRow.getContentMimetype("doc").contains("application/json"));		
	}
		
	/*
	 * Test join inner with keymatch, viewCol, and date sort
	 */
	@Test
	public void testJoinInnerKeymatchDateSort() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testJoinInnerKeymatchDateSort method");

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

		ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
		ModifyPlan plan2 = p.fromLexicons(index2, "myTeam");
		
		ExportablePlan output = plan1.joinInner(plan2)
		                             .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
		                             .orderBy(p.asc(p.col("date")));

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(output, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		// Should have 5 nodes returned.
		assertEquals("Five nodes not returned from testJoinInnerKeymatchDateSort method ", 5, jsonBindingsNodes.size());
		JsonNode first = jsonBindingsNodes.path(0);
		assertEquals("Row 1 myCity.city value incorrect", "new jersey", first.path("myCity.city").path("value").asText());
		assertEquals("Row 1 myCity.date value incorrect", "1971-12-23", first.path("myCity.date").path("value").asText());
		assertEquals("Row 1 myTeam.cityName value incorrect", "new jersey", first.path("myTeam.cityName").path("value").asText());
		assertEquals("Row 1 myTeam.cityTeam value incorrect", "nets", first.path("myTeam.cityTeam").path("value").asText());
		assertEquals("Row 1 myTeam.uri2 value incorrect", "/optic/lexicon/test/city3.json", first.path("myTeam.uri2").path("value").asText());
		JsonNode five = jsonBindingsNodes.path(4);
		assertEquals("Row 5 myCity.city value incorrect", "london", five.path("myCity.city").path("value").asText());
		assertEquals("Row 5 myCity.date value incorrect", "2007-01-01", five.path("myCity.date").path("value").asText());
		assertEquals("Row 5 myTeam.cityName value incorrect", "london", five.path("myTeam.cityName").path("value").asText());
		assertEquals("Row 5 myTeam.cityTeam value incorrect", "arsenal", five.path("myTeam.cityTeam").path("value").asText());
		assertEquals("Row 5 myTeam.uri2 value incorrect", "/optic/lexicon/test/city1.json", five.path("myTeam.uri2").path("value").asText());
 
		PlanColumn uriCol1 = p.col("uri1");
		PlanColumn cityCol = p.col("city");
		PlanColumn popCol = p.col("popularity");
		PlanColumn dateCol = p.col("date");
		PlanColumn distCol = p.col("distance");
		PlanColumn pointCol = p.col("point");
		PlanColumn uriCol2 = p.col("uri2");
		
		PlanColumn cityNameCol = p.col("cityName");
		PlanColumn cityTeamCol = p.col("cityTeam");
		//using element reference and viewname
		ModifyPlan outputNullVname = plan1.joinInner(plan2, p.on(p.viewCol("myCity", "city"), p.viewCol("myTeam", "cityName")))
			                              .orderBy(p.desc("uri2"))
			                              .joinInnerDoc("doc", "uri1")
			                              .select(uriCol1, cityCol, popCol, dateCol, distCol, pointCol, p.as("nodes", p.xpath("doc", "//city")), uriCol2, cityNameCol, cityTeamCol)
			                              .where(p.isDefined(p.col("nodes")));
		jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(outputNullVname, jacksonHandle);
		jsonResults = jacksonHandle.get();
		jsonBindingsNodes = jsonResults.path("rows");
		// Should have 5 nodes returned.
		assertEquals("Five nodes not returned from testJoinInnerKeymatchDateSort method ", 5, jsonBindingsNodes.size());
		JsonNode node = jsonBindingsNodes.path(0);
		assertEquals("Row 1 myCity.city value incorrect", "cape town", node.path("myCity.city").path("value").asText());
		assertEquals("Row 1 myCity.distance value incorrect", "377.9", node.path("myCity.distance").path("value").asText());
		assertEquals("Row 1 myTeam.cityName value incorrect", "cape town", node.path("myTeam.cityName").path("value").asText());
		assertEquals("Row 1 myTeam.cityTeam value incorrect", "pirates", node.path("myTeam.cityTeam").path("value").asText());
		assertEquals("Row 1 myCity.popularity value incorrect", "3", node.path("myCity.popularity").path("value").asText());
		
		node = jsonBindingsNodes.path(1);
		assertEquals("Row 2 myCity.city value incorrect", "beijing", node.path("myCity.city").path("value").asText());
		assertEquals("Row 2 myCity.distance value incorrect", "134.5", node.path("myCity.distance").path("value").asText());
		assertEquals("Row 2 myTeam.cityName value incorrect", "beijing", node.path("myTeam.cityName").path("value").asText());
		assertEquals("Row 2 myTeam.cityTeam value incorrect", "ducks", node.path("myTeam.cityTeam").path("value").asText());
		assertEquals("Row 2 myCity.popularity value incorrect", "5", node.path("myCity.popularity").path("value").asText());
		node = jsonBindingsNodes.path(4);
		assertEquals("Row 5 myCity.city value incorrect", "london", node.path("myCity.city").path("value").asText());
		assertEquals("Row 5 myCity.distance value incorrect", "50.4", node.path("myCity.distance").path("value").asText());
		assertEquals("Row 5 myTeam.cityName value incorrect", "london", node.path("myTeam.cityName").path("value").asText());
		assertEquals("Row 5 myTeam.cityTeam value incorrect", "arsenal", node.path("myTeam.cityTeam").path("value").asText());
		assertEquals("Row 5 myCity.popularity value incorrect", "5", node.path("myCity.popularity").path("value").asText());
		
		//TEST 4 - join inner with condition, joinInnerDoc and xpath
		
		ExportablePlan outputCondXpath = plan1.joinInner(plan2, 
		          p.on(p.viewCol("myCity", "city"), p.viewCol("myTeam", "cityName")),
		          p.ne(p.col("popularity"), p.xs.intVal(3))
		        )
		        .joinInnerDoc("doc", "uri1")
		        .select(uriCol1, cityCol, popCol, dateCol, distCol, pointCol, p.as("nodes", p.xpath("doc", "//latLonPair/lat/number()")), uriCol2, cityNameCol, cityTeamCol)
		        .where(p.isDefined(p.col("nodes")))
		        .orderBy(p.desc(p.col("distance")));
		        
		jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(outputCondXpath, jacksonHandle);
		jsonResults = jacksonHandle.get();
		jsonBindingsNodes = jsonResults.path("rows");
		// Should have 4 nodes returned.
		assertEquals("Four nodes not returned from testJoinInnerKeymatchDateSort method ", 4, jsonBindingsNodes.size());
		node = jsonBindingsNodes.path(0);
		assertEquals("Row 1 myCity.city value incorrect", "beijing", node.path("myCity.city").path("value").asText());
		assertEquals("Row 1 myCity.distance value incorrect", "134.5", node.path("myCity.distance").path("value").asText());
		assertEquals("Row 1 myTeam.cityName value incorrect", "beijing", node.path("myTeam.cityName").path("value").asText());
		assertEquals("Row 1 myTeam.cityTeam value incorrect", "ducks", node.path("myTeam.cityTeam").path("value").asText());
		assertEquals("Row 1 myCity.popularity value incorrect", "5", node.path("myCity.popularity").path("value").asText());
		assertEquals("Row 1 myCity.point value incorrect", "39.900002,116.4", node.path("myCity.point").path("value").asText());
		assertEquals("Row 1 nodes value incorrect", "39.9", node.path("nodes").path("value").asText());
		
		node = jsonBindingsNodes.path(3);
		assertEquals("Row 4 myCity.city value incorrect", "new jersey", node.path("myCity.city").path("value").asText());
		assertEquals("Row 4 myCity.distance value incorrect", "12.9", node.path("myCity.distance").path("value").asText());
		assertEquals("Row 4 myTeam.cityName value incorrect", "new jersey", node.path("myTeam.cityName").path("value").asText());
		assertEquals("Row 4 myTeam.cityTeam value incorrect", "nets", node.path("myTeam.cityTeam").path("value").asText());
		assertEquals("Row 4 myCity.popularity value incorrect", "2", node.path("myCity.popularity").path("value").asText());
		assertEquals("Row 4 myCity.point value incorrect", "40.720001,-74.07", node.path("myCity.point").path("value").asText());
		assertEquals("Row 4 nodes value incorrect", "40.72", node.path("nodes").path("value").asText());
		
		// TEST 20 - join inner with joinInnerDoc and xpath
		ExportablePlan innerJoinInnerDocXPath = plan1.joinInner(plan2)
                                                     .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
                                                     .orderBy(p.asc(p.col("date")))
                                                     .joinInnerDoc("doc", "uri2")
                                                     .select(
                                                    		 uriCol1, cityCol, popCol, dateCol, distCol, pointCol, 
                                                    		 p.viewCol("myCity", "__docId"), 
                                                    		 uriCol2, cityNameCol, cityTeamCol, 
                                                    		 p.viewCol("myTeam", "__docId"), 
                                                    		 p.as("nodes", p.xpath("doc", "/cityTeam"))
                                                    		)
                                                     .where(p.isDefined(p.col("nodes")));
        
		jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(innerJoinInnerDocXPath, jacksonHandle);
		jsonResults = jacksonHandle.get();
		jsonBindingsNodes = jsonResults.path("rows");
		
		// Should have 5 nodes returned.
		assertEquals("Five nodes not returned from testJoinInnerKeymatchDateSort method ", 5, jsonBindingsNodes.size());

		node = jsonBindingsNodes.path(0);
		assertEquals("Row 1 myCity.city value incorrect", "new jersey", node.path("myCity.city").path("value").asText());
		assertEquals("Row 1 myCity.distance value incorrect", "12.9", node.path("myCity.distance").path("value").asText());
		assertEquals("Row 1 myTeam.cityName value incorrect", "new jersey", node.path("myTeam.cityName").path("value").asText());
		assertEquals("Row 1 myTeam.cityTeam value incorrect", "nets", node.path("myTeam.cityTeam").path("value").asText());
		assertEquals("Row 1 myCity.popularity value incorrect", "2", node.path("myCity.popularity").path("value").asText());
		assertEquals("Row 1 myCity.point value incorrect", "40.720001,-74.07", node.path("myCity.point").path("value").asText());	
		assertEquals("Row 1 myCity.uri1 value incorrect", "/optic/lexicon/test/doc3.json", node.path("myCity.uri1").path("value").asText());
		assertEquals("Row 1 myTeam.uri2 value incorrect", "/optic/lexicon/test/city3.json", node.path("myTeam.uri2").path("value").asText());
		assertTrue("Row 1 myCity.__docid value incorrect", node.path("myCity.__docid").path("value").asText().startsWith("http://marklogic.com/fragment/"));
		assertTrue("Row 1 myTeam.__docid value incorrect", node.path("myTeam.__docid").path("value").asText().startsWith("http://marklogic.com/fragment/"));
		
		node = jsonBindingsNodes.path(4);
		assertEquals("Row 5 myCity.city value incorrect", "london", node.path("myCity.city").path("value").asText());
		assertEquals("Row 5 myCity.distance value incorrect", "50.4", node.path("myCity.distance").path("value").asText());
		assertEquals("Row 5 myTeam.cityName value incorrect", "london", node.path("myTeam.cityName").path("value").asText());
		assertEquals("Row 5 myTeam.cityTeam value incorrect", "arsenal", node.path("myTeam.cityTeam").path("value").asText());
		assertEquals("Row 5 myCity.popularity value incorrect", "5", node.path("myCity.popularity").path("value").asText());
		assertEquals("Row 5 myCity.point value incorrect", "51.5,-0.12", node.path("myCity.point").path("value").asText());
		assertEquals("Row 5 myCity.uri1 value incorrect", "/optic/lexicon/test/doc1.json", node.path("myCity.uri1").path("value").asText());
		assertEquals("Row 5 myTeam.uri2 value incorrect", "/optic/lexicon/test/city1.json", node.path("myTeam.uri2").path("value").asText());
		assertTrue("Row 5 myCity.__docid value incorrect", node.path("myCity.__docid").path("value").asText().startsWith("http://marklogic.com/fragment/"));
		assertTrue("Row 5 myTeam.__docid value incorrect", node.path("myTeam.__docid").path("value").asText().startsWith("http://marklogic.com/fragment/"));
	}
	
	/*
	 * Test prepare plan
	 */
	@Test
	public void testPreparePlan() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testPreparePlan method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		Map<String, CtsReferenceExpr>index1 = new HashMap<String, CtsReferenceExpr>();
		index1.put("uri", p.cts.uriReference());
		index1.put("city", p.cts.jsonPropertyReference("city"));
		index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
		index1.put("date", p.cts.jsonPropertyReference("date"));
		index1.put("distance", p.cts.jsonPropertyReference("distance"));
		index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));
		
		PlanColumn popCol = p.col("popularity");
		PlanColumn dateCol = p.col("date");
		
		// prepare = 0
		ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
		PreparePlan output1 = plan1.where(p.gt(popCol, p.xs.intVal(2)))
		                             .orderBy(p.asc("city"))
		                             .select("city", "popularity", "date", "distance", "point")
		                             .prepare(0);

		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		rowMgr.resultDoc(output1, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		// Should have 4 nodes returned.
		assertEquals("Four nodes not returned from testPreparePlan method ", 4, jsonBindingsNodes.size());
		JsonNode first = jsonBindingsNodes.path(0);
		assertEquals("Row 1 myCity.city value incorrect", "beijing", first.path("myCity.city").path("value").asText());
		assertEquals("Row 1 myCity.point value incorrect", "39.900002,116.4", first.path("myCity.point").path("value").asText());
		first = jsonBindingsNodes.path(3);
		assertEquals("Row 4 myCity.city value incorrect", "new york", first.path("myCity.city").path("value").asText());
		assertEquals("Row 4 myCity.point value incorrect", "40.709999,-74.009995", first.path("myCity.point").path("value").asText());
		
		// prepare = 2
		PreparePlan output2 = plan1.where(p.gt(popCol, p.xs.intVal(2)))
                .orderBy(p.asc("city"))
                .select("city", "popularity", "date", "distance", "point")
                .prepare(2);

		jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		rowMgr.resultDoc(output2, jacksonHandle);
		jsonResults = jacksonHandle.get();
		jsonBindingsNodes = jsonResults.path("rows");
		// Should have 4 nodes returned.
		assertEquals("Four nodes not returned from testPreparePlan method ", 4, jsonBindingsNodes.size());
		first = jsonBindingsNodes.path(0);
		assertEquals("Row 1 myCity.city value incorrect", "beijing", first.path("myCity.city").path("value").asText());
		assertEquals("Row 1 myCity.point value incorrect", "39.900002,116.4", first.path("myCity.point").path("value").asText());
		first = jsonBindingsNodes.path(3);
		assertEquals("Row 4 myCity.city value incorrect", "new york", first.path("myCity.city").path("value").asText());
		assertEquals("Row 4 myCity.point value incorrect", "40.709999,-74.009995", first.path("myCity.point").path("value").asText());
		
		// prepare = 5
		PreparePlan output3 = plan1.where(p.gt(popCol, p.xs.intVal(2)))
				.orderBy(p.asc("city"))
				.select("city", "popularity", "date", "distance", "point")
				.prepare(5);

		jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		rowMgr.resultDoc(output3, jacksonHandle);
		jsonResults = jacksonHandle.get();
		jsonBindingsNodes = jsonResults.path("rows");
		// Should have 4 nodes returned.
		assertEquals("Four nodes not returned from testPreparePlan method ", 4, jsonBindingsNodes.size());
		first = jsonBindingsNodes.path(0);
		assertEquals("Row 1 myCity.city value incorrect", "beijing", first.path("myCity.city").path("value").asText());
		assertEquals("Row 1 myCity.point value incorrect", "39.900002,116.4", first.path("myCity.point").path("value").asText());
		first = jsonBindingsNodes.path(3);
		assertEquals("Row 4 myCity.city value incorrect", "new york", first.path("myCity.city").path("value").asText());
		assertEquals("Row 4 myCity.point value incorrect", "40.709999,-74.009995", first.path("myCity.point").path("value").asText());
		
		// prepare = -3
		PreparePlan output4 = plan1.where(p.gt(popCol, p.xs.intVal(2)))
				.orderBy(p.asc("city"))
				.select("city", "popularity", "date", "distance", "point")
				.prepare(-3);

		jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");
		StringBuilder str = new StringBuilder();
		try {
		rowMgr.resultDoc(output4, jacksonHandle);
		}
		catch(Exception ex) {
			str.append(ex.getMessage());
			System.out.println("Exception message is " + str.toString());
		}		
		// Should have XDMP-OPTION exceptions.
		assertTrue("Exceptions not found", str.toString().contains("XDMP-OPTION"));
		assertTrue("Exceptions not found", str.toString().contains("Invalid option \"optimize=-3\""));
	}
	
	/*
	 * Test join inner with system col
	 */
	@Test
	public void testJoinInnerWithSystemCol() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testJoinInnerWithSystemCol method");

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
		
		PlanSystemColumn fragIdCol1 = p.fragmentIdCol("fragId1");
		PlanSystemColumn fragIdCol2 = p.fragmentIdCol("fragId2");
	
		// plan1
		ModifyPlan plan1 = p.fromLexicons(index1, "myCity", fragIdCol1, null);
		// plan2
		ModifyPlan plan2 = p.fromLexicons(index2, "myTeam", fragIdCol2, null);
		
		// plan3
		ModifyPlan plan3 =  plan1.joinInner(plan2)
		                         .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
		                         .orderBy(p.asc(p.col("date")));
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(plan3, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		// Should have 5 nodes returned.
		assertEquals("Five nodes not returned from testJoinInnerWithSystemCol method ", 5, jsonBindingsNodes.size());
		JsonNode node = jsonBindingsNodes.path(0);
		
		assertEquals("Row 1 myCity.city value incorrect", "new jersey", node.path("myCity.city").path("value").asText());
		assertEquals("Row 1 myCity.distance value incorrect", "12.9", node.path("myCity.distance").path("value").asText());
		assertEquals("Row 1 myTeam.cityName value incorrect", "new jersey", node.path("myTeam.cityName").path("value").asText());
		assertEquals("Row 1 myTeam.cityTeam value incorrect", "nets", node.path("myTeam.cityTeam").path("value").asText());
		assertEquals("Row 1 myCity.popularity value incorrect", "2", node.path("myCity.popularity").path("value").asText());
		assertEquals("Row 1 myCity.point value incorrect", "40.720001,-74.07", node.path("myCity.point").path("value").asText());	
		assertEquals("Row 1 myCity.uri1 value incorrect", "/optic/lexicon/test/doc3.json", node.path("myCity.uri1").path("value").asText());
		assertEquals("Row 1 myTeam.uri2 value incorrect", "/optic/lexicon/test/city3.json", node.path("myTeam.uri2").path("value").asText());
		assertTrue("Row 1 myCity.fragId1 value incorrect", node.path("myCity.fragId1").path("value").asText().startsWith("http://marklogic.com/fragment/"));
		assertTrue("Row 1 myTeam.fragId2 value incorrect", node.path("myTeam.fragId2").path("value").asText().startsWith("http://marklogic.com/fragment/"));
		
		node = jsonBindingsNodes.path(4);
		assertEquals("Row 5 myCity.city value incorrect", "london", node.path("myCity.city").path("value").asText());
		assertEquals("Row 5 myCity.distance value incorrect", "50.4", node.path("myCity.distance").path("value").asText());
		assertEquals("Row 5 myTeam.cityName value incorrect", "london", node.path("myTeam.cityName").path("value").asText());
		assertEquals("Row 5 myTeam.cityTeam value incorrect", "arsenal", node.path("myTeam.cityTeam").path("value").asText());
		assertEquals("Row 5 myCity.popularity value incorrect", "5", node.path("myCity.popularity").path("value").asText());
		assertEquals("Row 5 myCity.point value incorrect", "51.5,-0.12", node.path("myCity.point").path("value").asText());
		assertEquals("Row 5 myCity.uri1 value incorrect", "/optic/lexicon/test/doc1.json", node.path("myCity.uri1").path("value").asText());
		assertEquals("Row 5 myTeam.uri2 value incorrect", "/optic/lexicon/test/city1.json", node.path("myTeam.uri2").path("value").asText());
		assertTrue("Row 5 myCity.fragId1 value incorrect", node.path("myCity.fragId1").path("value").asText().startsWith("http://marklogic.com/fragment/"));
		assertTrue("Row 5 myTeam.fragId2 value incorrect", node.path("myTeam.fragId2").path("value").asText().startsWith("http://marklogic.com/fragment/"));		
	}
	
	/*
	 * Test prepared plan and multiple order by and export.
	 */
	@Test
	public void testPreparedPlanMultipleOrderBy() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testPreparedPlanMultipleOrderBy method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		PlanColumn popCol = p.col("popularity");
		PlanColumn dateCol = p.col("date");

		Map<String, CtsReferenceExpr>index1 = new HashMap<String, CtsReferenceExpr>();
		index1.put("uri1", p.cts.uriReference());
		index1.put("city", p.cts.jsonPropertyReference("city"));
		index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
		index1.put("date", p.cts.jsonPropertyReference("date"));
		index1.put("distance", p.cts.jsonPropertyReference("distance"));
		index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));
		
		// plan1
		ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
		
		PreparePlan preparedPlan = plan1.where(p.gt(popCol, p.xs.intVal(2)))
		                                .orderBy(p.asc("popularity"), p.desc("date"))
		                                .select("city", "popularity", "date", "distance", "point")
		                                .prepare(0);
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		rowMgr.resultDoc(preparedPlan, jacksonHandle);
		JsonNode jsonResults = jacksonHandle.get();
		JsonNode jsonBindingsNodes = jsonResults.path("rows");
		// Should have 4 nodes returned.
		assertEquals("Four nodes not returned from testPreparedPlanMultipleOrderBy method", 4, jsonBindingsNodes.size());
		JsonNode node = jsonBindingsNodes.path(0);
		assertEquals("Row 1 myCity.city value incorrect", "cape town", node.path("myCity.city").path("value").asText());
		assertEquals("Row 1 myCity.popularity value incorrect", "3", node.path("myCity.popularity").path("value").asText());
		node = jsonBindingsNodes.path(1);
		assertEquals("Row 2 myCity.popularity value incorrect", "5", node.path("myCity.popularity").path("value").asText());
		node = jsonBindingsNodes.path(3);
		assertEquals("Row 4 myCity.popularity value incorrect", "5", node.path("myCity.popularity").path("value").asText());
		assertEquals("Row 4 myCity.date value incorrect", "1981-11-09", node.path("myCity.date").path("value").asText());
		
		StringHandle strHandle = new StringHandle();
		// Export the plan.
		String str = preparedPlan.export(strHandle).get();
		assertTrue("Function not available fromLexicons in exported plan", str.contains("\"fn\":\"from-lexicons\""));		
		assertTrue("Prepare from fromLexicons not in exported plan", str.contains("\"fn\":\"prepare\""));
	}
	
	/*
	 * Test False entry in where clause
	 */	
	@Test
	public void testFalseWhereClause() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testFalseWhereClause method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		PlanColumn popCol = p.col("popularity");
		PlanColumn dateCol = p.col("date");
		
		Map<String, CtsReferenceExpr>index1 = new HashMap<String, CtsReferenceExpr>();
		index1.put("uri", p.cts.uriReference());
		index1.put("city", p.cts.jsonPropertyReference("city"));
		index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
		index1.put("date", p.cts.jsonPropertyReference("date"));
		index1.put("distance", p.cts.jsonPropertyReference("distance"));
		index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));
		
		// plan1
		ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
		PreparePlan output = plan1.where(p.gt(popCol, p.xs.string("blah")))
		        .orderBy(p.asc("date"))
		        .select("city", "popularity", "date", "distance", "point");
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		StringBuilder str = new StringBuilder();
		try {
			rowMgr.resultDoc(output, jacksonHandle);
		}
		catch(Exception ex) {
			str.append(ex.getMessage());
		}
		// Should have XDMP-CAST exceptions.
		assertTrue("Exceptions not found", str.toString().contains("XDMP-CAST: (err:FORG0001) \"blah\" cast as xs:int* -- Invalid cast: \"blah\" cast as xs:int"));
	}
	
	/*
	 * Test Invalid range index- date
	 * 
	 */	
	@Test
	public void testInvalidRangeIndexDate() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testInvalidRangeIndexDate method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		PlanColumn popCol = p.col("popularity");
		
		Map<String, CtsReferenceExpr>index1 = new HashMap<String, CtsReferenceExpr>();
		index1.put("uri", p.cts.uriReference());
		index1.put("city", p.cts.jsonPropertyReference("city"));
		index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
		index1.put("date", p.cts.jsonPropertyReference("date"));
		index1.put("distance", p.cts.jsonPropertyReference("distance"));
		index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));
		
		// plan1
		ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
		PreparePlan output = plan1.where(p.gt(popCol, p.xs.intVal(2)))
		        .orderBy(p.asc("date_invalid"))
		        .select("city", "popularity", "date", "distance", "point");
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
		assertTrue("Exceptions not found", str.toString().contains("Column not found: date_invalid"));
	}
	
	/*
	 * Test Invalid refferance- city
	 */
	@Test
	public void testInvalidRefferance() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testInvalidRefferance method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		PlanColumn popCol = p.col("popularity");
		
		Map<String, CtsReferenceExpr>index1 = new HashMap<String, CtsReferenceExpr>();
		index1.put("uri", p.cts.uriReference());
		index1.put("city", p.cts.jsonPropertyReference("city_invalid"));
		index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
		index1.put("date", p.cts.jsonPropertyReference("date"));
		index1.put("distance", p.cts.jsonPropertyReference("distance"));
		index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));
		
		// plan1
		ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
		PreparePlan output = plan1.where(p.gt(popCol, p.xs.intVal(2)))
		        .orderBy(p.asc("date_invalid"))
		        .select("city", "popularity", "date", "distance", "point");
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
		assertTrue("Exceptions not found", str.toString().contains("XDMP-ELEMRIDXNOTFOUND: cts.jsonPropertyReference(\"city_invalid\") -- No  element range index for city_invalid collation"));
	}
	
	/*
	 * Test Invalid refferance- qualifier
	 * 
	 */	
	@Test
	public void testInvalidRefferanceQualifier() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testInvalidRefferanceQualifier method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		PlanColumn popCol = p.col("popularity");	

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
		
		// plan1
		ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
		ModifyPlan plan2 = p.fromLexicons(index2, "myTeam");
		ModifyPlan output = plan1.joinInner(plan2)
		                         .where(p.eq(p.viewCol("myCity_invalid", "city"), p.col("cityName")))
		                         .orderBy(p.asc(p.col("date")));
		          
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
		assertTrue("Exceptions not found", str.toString().contains("Column not found: myCity_invalid.city"));
	}
	
	/*
	 * Test Invalid viewCol
	 * 
	 */	
	@Test
	public void testInvalidViewCol() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testInvalidViewCol method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		PlanColumn popCol = p.col("popularity");	

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
		
		// plan1
		ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
		ModifyPlan plan2 = p.fromLexicons(index2, "myTeam"); 
		ModifyPlan output = plan1.joinInner(plan2)
		        .where(p.eq(p.viewCol("invalid_view", "city"), p.col("cityName")))
		        .orderBy(p.asc(p.col("date")));
		          
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
		assertTrue("Exceptions not found", str.toString().contains("Column not found: invalid_view.city"));
	}
	
	/*
	 * Test
	 * 1) invalid uri on join inner doc - TEST 10
	 * 2) null uri on join inner doc - TEST 11
	 * 3) invalid doc on join inner doc
	 * 
	 */	
	@Test
	public void testInvalidInnerDocElements() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testInvalidInnerDocElements method");

		// Create a new Plan.
		RowManager rowMgr = client.newRowManager();
		PlanBuilder p = rowMgr.newPlanBuilder();
		
		Map<String, CtsReferenceExpr>index1 = new HashMap<String, CtsReferenceExpr>();
		index1.put("uri", p.cts.uriReference());
		index1.put("city", p.cts.jsonPropertyReference("city"));
		index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
		index1.put("date", p.cts.jsonPropertyReference("date"));
		index1.put("distance", p.cts.jsonPropertyReference("distance"));
		index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));
		
		// plan1
		ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
		
		//invalid uri on join inner doc
		ModifyPlan outputInvalidURI = plan1.joinInnerDoc("doc", "/foo/bar").orderBy(p.asc("uri"));
		          
		JacksonHandle jacksonHandle = new JacksonHandle();
		jacksonHandle.setMimetype("application/json");

		StringBuilder str = new StringBuilder();
		try {
			rowMgr.resultDoc(outputInvalidURI, jacksonHandle);
		}
		catch(Exception ex) {
			str.append(ex.getMessage());
			System.out.println("Exception message is " + str.toString());
		}
		// Should have SQL-NOCOLUMN exceptions.
		assertTrue("Exceptions not found", str.toString().contains("SQL-NOCOLUMN"));
		assertTrue("Exceptions not found", str.toString().contains("Column not found: /foo/bar"));
		
		//null uri on join inner doc
		try {
			ModifyPlan outputNullURI = plan1.joinInnerDoc("doc", null).orderBy(p.asc("uri"));
			str = new StringBuilder();
			rowMgr.resultDoc(outputNullURI, jacksonHandle);
		}
		catch(Exception ex) {
			str.append(ex.getMessage());
		}
		// Should have java.lang.IllegalArgumentException exceptions.
		assertTrue("Exceptions not found", str.toString().contains("cannot take null value"));
		
		//invalid doc on join inner doc
		try {
			ModifyPlan outputNullURI = plan1.joinInnerDoc("doc", "{foo: bar}").orderBy(p.asc("uri"));
			str = new StringBuilder();
			rowMgr.resultDoc(outputNullURI, jacksonHandle);
		}
		catch(Exception ex) {
			str.append(ex.getMessage());
		}
		// Should have SQL-NOCOLUMN exceptions.
		assertTrue("Exceptions not found", str.toString().contains("SQL-NOCOLUMN"));		
		assertTrue("Exceptions not found", str.toString().contains("Column not found: {foo: bar}"));
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
