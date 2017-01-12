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
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.GeospatialOperator;
import com.marklogic.client.query.StructuredQueryDefinition;

public class TestDoublePrecisionGeoOps extends BasicJavaClientREST {

	private static String dbName = "TestDoublePrecisionGeoOps";
	private static String [] fNames = {"TestDoublePrecisionGeoOps-1"};
	private static String datasource = "src/test/java/com/marklogic/client/functionaltest/data/geodouble/";
	
	static DatabaseClient client = null;

	@BeforeClass 
	public static void setUp() throws Exception 
	{
		System.out.println("In setup");
		configureRESTServer(dbName, fNames);
				
		// Setup additional indices for double precision.		
		addGeospatialElementIndexes(dbName, "point", "", "wgs84/double", "point", false, "reject");
		
		addGeoSpatialElementChildIndexes(dbName,"","ele-child-point", "", "pos" , "wgs84/double", "point", true, "reject");			
		addGeospatialElementPairIndexes(dbName, "", "ele-pair-point", "", "ele-pair-point-lat", "", "ele-pair-point-long", "wgs84/double", false, "reject");
		addGeospatialElementAttributePairIndexes(dbName, "", "ele-attr-pair-point", "", "lat", "", "lon", "wgs84/double", true, "reject");
		addGeospatialPathIndexes(dbName, "/root/lat-long", "wgs84/double", "point", false, "reject");
		addGeospatialPathIndexes(dbName, "/root/item/point", "wgs84/double", "point", false, "reject");
	
		// Add /double to the coordinate system once Git Issue 466 is fixed.
		addGeospatialRegionPathIndexes(dbName, "/root/item/point", "wgs84", "2", "reject");
		addGeospatialRegionPathIndexes(dbName, "/root/item/circle", "wgs84", "2", "reject");
		addGeospatialRegionPathIndexes(dbName, "/root/item/box", "wgs84", "2", "reject");
		addGeospatialRegionPathIndexes(dbName, "/root/item/polygon", "wgs84", "3", "reject");
		
		//Load all necessary data / docs.
		String[] xmlFiles = {"Tropic-of-Capricorn.xml", "Tropic-of-Cancer.xml", "South-Pole.xml", "South-More.xml", "South-East.xml",
				             "Prime-Meridian.xml", "North-West.xml", "North-Pole.xml", "International-Date-Line.xml", "Equator.xml" };
		String[] jsonFiles = {"Tropic-of-Capricorn-json.json", "Tropic-of-Cancer-json.json", "South-Pole-json.json", "South-More-json.json", "South-East-json.json",
	             "Prime-Meridian-json.json", "North-West-json.json", "North-Pole-json.json", "International-Date-Line-json.json", "Equator-json.json" };
		
		client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : xmlFiles) {
			writeGeoDoubleFilesToDB(client, filename, "/" + filename, "XML");
		}
		
		for(String filename : jsonFiles) {
			writeGeoDoubleFilesToDB(client, filename, "/" + filename, "JSON");
		}		
	}

	/*
	 * Point contains Point
	 */
	@Test	
	public void testPointContainsPoint() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testPointContainsPoint");
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();		
		StructuredQueryDefinition t = qb.geospatial(qb.geoPath(qb.pathIndex("/root/item/point")), qb.point(0, -66.09375));
		// create handle
		JacksonHandle resultsHandle = new JacksonHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		JsonNode resultNode = resultsHandle.get();
        JsonNode jsonPointNodes = resultNode.path("results");
		
		// Should have 2 nodes returned.
/*		assertEquals("Two nodes not returned from testPointContainsPoint method ", 2, resultNode.path("total").asInt());
		assertTrue("URI returned from testPointContainsPoint method is incorrect", jsonPointNodes.get(0).path("uri").asText().contains("/Equator-json.json") ||
				jsonPointNodes.get(1).path("uri").asText().contains("/Equator-json.json"));
		assertTrue("URI returned from testPointContainsPoint method is incorrect", jsonPointNodes.get(0).path("uri").asText().contains("/Equator.xml") ||
				jsonPointNodes.get(1).path("uri").asText().contains("/Equator.xml"));*/
	}
	
	/*
	 * Circle contains circle
	 */
	@Test	
	public void testCircleContainsCircle() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testCircleContainsCircle");
			
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();		
		StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/circle")), GeospatialOperator.CONTAINS, qb.circle(qb.point(0,-66.09375), 6.897));
		// create handle
		JacksonHandle resultsHandle = new JacksonHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		JsonNode resultNode = resultsHandle.get();
        JsonNode jsonPointNodes = resultNode.path("results");
		
		// Should have 2 nodes returned.
		/*assertEquals("Two nodes not returned from testCircleContainsCircle method ", 2, resultNode.path("total").asInt());
		assertTrue("URI returned from testCircleContainsCircle method is incorrect", jsonPointNodes.get(0).path("uri").asText().contains("/Equator-json.json") ||
				jsonPointNodes.get(1).path("uri").asText().contains("/Equator-json.json"));
		assertTrue("URI returned from testCircleContainsCircle method is incorrect", jsonPointNodes.get(0).path("uri").asText().contains("/Equator.xml") ||
				jsonPointNodes.get(1).path("uri").asText().contains("/Equator.xml"));*/
	}
	
	/*
	 * Box contains Polygon
	 */
	@Test	
	public void testBoxContainsPolygon() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testBoxContainsPolygon");
			
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/box")), GeospatialOperator.CONTAINS, 
				qb.polygon(qb.point(-5,-70), qb.point(4, -70), qb.point(3, -60), qb.point(-3, -65), qb.point(-5,-70)));
		// create handle
		JacksonHandle resultsHandle = new JacksonHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		JsonNode resultNode = resultsHandle.get();
        JsonNode jsonPointNodes = resultNode.path("results");
		
		// Should have 2 nodes returned.
		assertEquals("Two nodes not returned from testCircleContainsCircle method ", 2, resultNode.path("total").asInt());
		assertTrue("URI returned from testCircleContainsCircle method is incorrect", jsonPointNodes.get(0).path("uri").asText().contains("/Equator-json.json") ||
				jsonPointNodes.get(1).path("uri").asText().contains("/Equator-json.json"));
		assertTrue("URI returned from testCircleContainsCircle method is incorrect", jsonPointNodes.get(0).path("uri").asText().contains("/Equator.xml") ||
				jsonPointNodes.get(1).path("uri").asText().contains("/Equator.xml"));	
	}
	
	/*
	 * Circle intersects Point
	 */
	@Test	
	public void testCircleIntersectsPoint() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testCircleIntersectsPoint");
				
		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/box")), GeospatialOperator.INTERSECTS, 
				qb.point(0,-66.09375));
		// create handle
		JacksonHandle resultsHandle = new JacksonHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		JsonNode resultNode = resultsHandle.get();
		JsonNode jsonPointNodes = resultNode.path("results");

		// Should have 2 nodes returned.
		/*assertEquals("Two nodes not returned from testCircleIntersectsPoint method ", 2, resultNode.path("total").asInt());
		assertTrue("URI returned from testCircleContainsCircle method is incorrect", jsonPointNodes.get(0).path("uri").asText().contains("/Equator-json.json") ||
				jsonPointNodes.get(1).path("uri").asText().contains("/Equator-json.json"));
		// Verify match text in either of the two nodes returned. This is the Json Point.
		assertTrue("Match text returned from testCircleIntersectsPoint method is incorrect", jsonPointNodes.get(0).path("matches")
				   .get(0).path("match-text").get(0).asText().contains("POINT(-66.09375 0) Json Point In Equator - South America") ||
				   jsonPointNodes.get(1).path("matches")
				   .get(0).path("match-text").get(0).asText().contains("POINT(-66.09375 0) Json Point In Equator - South America"));
		
		
		assertTrue("URI returned from testCircleIntersectsPoint method is incorrect", jsonPointNodes.get(0).path("uri").asText().contains("/Equator.xml") ||
				jsonPointNodes.get(1).path("uri").asText().contains("/Equator.xml"));
		// Verify match text in either of the two nodes returned. XML point.
		assertTrue("Match text returned from testCircleIntersectsPoint method is incorrect", jsonPointNodes.get(0).path("matches")
				   .get(0).path("match-text").get(0).asText().contains("POINT(-66.09375 0) Point In Equator - South America") ||
				   jsonPointNodes.get(1).path("matches")
				   .get(0).path("match-text").get(0).asText().contains("POINT(-66.09375 0) Point In Equator - South America"));*/
	}
	
	/*
	 * Point covered-by Polygon
	 */
	@Test	
	public void testPointCoveredByPolygon() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testPointCoveredByPolygon");
				
		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/point")), 
				                                       GeospatialOperator.COVEREDBY, 
				                                       qb.polygon(qb.point(40.13, -52.96),  qb.point(40.45,-36.61),   qb.point(38.16,-13.48),
				                                    		      qb.point(21.09,-1.1),     qb.point(0.0,-17.46),     qb.point(-17.38,-12.52),
				                                    		      qb.point(-33.08,-20.47),  qb.point(-51.46,-28.61),  qb.point(-65.33,-44.77), 
				                                    		      qb.point(-66.8,-66.0),    qb.point(-53.99,-83.54),  qb.point(-54.35,-105.49), 
				                                    		      qb.point(-24.59,-99.85),  qb.point(-14.58,-110.86), qb.point(-0.0,-127.42),
				                                    		      qb.point(13.44,-107.37),  qb.point(35.78,-115.25),  qb.point(53.21,-104.66),
				                                    		      qb.point(48.69,-81.82),   qb.point(55.37,-66.0),    qb.point(40.13,-52.96)
				                                    		     )
				                                   );
		// create handle
		JacksonHandle resultsHandle = new JacksonHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		JsonNode resultNode = resultsHandle.get();
		JsonNode jsonPointNodes = resultNode.path("results");

		// Should have 2 nodes returned.
		//assertEquals("Two nodes not returned from testPointCoveredByPolygon method ", 2, resultNode.path("total").asInt());
		
		// Work on the asserts once server bugs are fixed. 
		
		/*assertTrue("URI returned from testPointCoveredByPolygon method is incorrect", jsonPointNodes.get(0).path("uri").asText().contains("/Equator-json.json") ||
				jsonPointNodes.get(1).path("uri").asText().contains("/Equator-json.json"));
		// Verify match text in either of the two nodes returned. This is the Json Point.
		assertTrue("Match text returned from testPointCoveredByPolygon method is incorrect", jsonPointNodes.get(0).path("matches")
				   .get(0).path("match-text").get(0).asText().contains("POINT(-66.09375 0) Json Point In Equator - South America") ||
				   jsonPointNodes.get(1).path("matches")
				   .get(0).path("match-text").get(0).asText().contains("POINT(-66.09375 0) Json Point In Equator - South America"));
		
		
		assertTrue("URI returned from testPointCoveredByPolygon method is incorrect", jsonPointNodes.get(0).path("uri").asText().contains("/Equator.xml") ||
				jsonPointNodes.get(1).path("uri").asText().contains("/Equator.xml"));
		// Verify match text in either of the two nodes returned. XML point.
		assertTrue("Match text returned from testPointCoveredByPolygon method is incorrect", jsonPointNodes.get(0).path("matches")
				   .get(0).path("match-text").get(0).asText().contains("POINT(-66.09375 0) Point In Equator - South America") ||
				   jsonPointNodes.get(1).path("matches")
				   .get(0).path("match-text").get(0).asText().contains("POINT(-66.09375 0) Point In Equator - South America"));*/
	}
	
	/*
	 * Polygon covers Box
	 */
	@Test	
	public void testPolygonCoversBox() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testPolygonCoversBox");
				
		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/polygon")), 
				                                       GeospatialOperator.COVERS, 
				                                       qb.box(-5.45, -76.35643, 5.35, -54.636)
				                                   );
		// create handle
		JacksonHandle resultsHandle = new JacksonHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		JsonNode resultNode = resultsHandle.get();
		JsonNode jsonPointNodes = resultNode.path("results");

		// Should have 2 nodes returned.
		//assertEquals("Two nodes not returned from testPolygonCoversBox method ", 2, resultNode.path("total").asInt());
		
		// Work on the asserts once server bugs are fixed. 
		
		/*assertTrue("URI returned from testPolygonCoversBox method is incorrect", jsonPointNodes.get(0).path("uri").asText().contains("/Equator-json.json") ||
				jsonPointNodes.get(1).path("uri").asText().contains("/Equator-json.json"));
		// Verify match text in either of the two nodes returned. This is the Json Point.
		assertTrue("Match text returned from testPolygonCoversBox method is incorrect", jsonPointNodes.get(0).path("matches")
				   .get(0).path("match-text").get(0).asText().contains("POINT(-66.09375 0) Json Point In Equator - South America") ||
				   jsonPointNodes.get(1).path("matches")
				   .get(0).path("match-text").get(0).asText().contains("POINT(-66.09375 0) Json Point In Equator - South America"));
		
		
		assertTrue("URI returned from testPolygonCoversBox method is incorrect", jsonPointNodes.get(0).path("uri").asText().contains("/Equator.xml") ||
				jsonPointNodes.get(1).path("uri").asText().contains("/Equator.xml"));
		// Verify match text in either of the two nodes returned. XML point.
		assertTrue("Match text returned from testPolygonCoversBox method is incorrect", jsonPointNodes.get(0).path("matches")
				   .get(0).path("match-text").get(0).asText().contains("POINT(-66.09375 0) Point In Equator - South America") ||
				   jsonPointNodes.get(1).path("matches")
				   .get(0).path("match-text").get(0).asText().contains("POINT(-66.09375 0) Point In Equator - South America"));*/
	}
	
	/*
	 * Polygon crosses Point
	 */
	@Test	
	public void testPolygonCrossesPoint() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testPolygonCrossesPoint");
				
		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/polygon")), 
				                                       GeospatialOperator.CROSSES, 
				                                       qb.point(-26.797920, 136.406250)
				                                   );
		// create handle
		JacksonHandle resultsHandle = new JacksonHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		JsonNode resultNode = resultsHandle.get();
		JsonNode jsonPointNodes = resultNode.path("results");

		// Should have 2 nodes returned.
		//assertEquals("Two nodes not returned from testPolygonCoversBox method ", 2, resultNode.path("total").asInt());
		
		// Work on the asserts once server bugs are fixed. 
	}
	
	/*
	 * Box overlaps Circle
	 */
	@Test	
	public void testBoxOverlapsCircle() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testPolygonCrossesPoint");
			
		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/box")), 
				                                       GeospatialOperator.OVERLAPS, 
				                                       qb.circle(qb.point(25.234,85.2345), 10)
				                                   );
		// create handle
		JacksonHandle resultsHandle = new JacksonHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		JsonNode resultNode = resultsHandle.get();
		JsonNode jsonPointNodes = resultNode.path("results");

		// Should have 2 nodes returned.
		//assertEquals("Two nodes not returned from testPolygonCoversBox method ", 2, resultNode.path("total").asInt());
		
		// Work on the asserts once server bugs are fixed. 
	}
		
	/*
	 * Polygon disjoint Polygon - polygon near south east
	 */
	@Test	
	public void testPolygonDisjointPolygon() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testPolygonDisjointPolygon");
				
		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/polygon")), 
				                                       GeospatialOperator.DISJOINT, 
				                                       qb.polygon(
				                                    		   qb.point(-90, 131),
				                                    		   qb.point(-85, 133),
				                                    		   qb.point(-87, 134),
				                                    		   qb.point(-88, 135),
				                                    		   qb.point(-90, 131))
				                                   );
		// create handle
		JacksonHandle resultsHandle = new JacksonHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		JsonNode resultNode = resultsHandle.get();
		JsonNode jsonPointNodes = resultNode.path("results");

		// Should have 2 nodes returned.
		//assertEquals("Two nodes not returned from testPolygonCoversBox method ", 2, resultNode.path("total").asInt());
		
		// Work on the asserts once server bugs are fixed. 
	}
		
	/*
	 * Box disjoint Box--except Tropic of Capricorn-Australia
	 */
	@Test	
	public void testBoxDisjointBox() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testPolygonDisjointPolygon");
			
		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/box")), 
				                                       GeospatialOperator.DISJOINT, 
				                                       qb.box(-40.234, 100.4634, -20.345, 140.45230)
				                                   );
		// create handle
		JacksonHandle resultsHandle = new JacksonHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		JsonNode resultNode = resultsHandle.get();
		JsonNode jsonPointNodes = resultNode.path("results");

		// Should have 2 nodes returned.
		//assertEquals("Two nodes not returned from testPolygonCoversBox method ", 2, resultNode.path("total").asInt());
		
		// Work on the asserts once server bugs are fixed. 
	}
		
	/*
	 * Point equals Point
	 */
	
	@Test	
	public void testPointEqualsPoint() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testPointEqualsPoint");
						
		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/point")), 
				                                       GeospatialOperator.EQUALS, 
				                                       qb.point(0, -66.09375)
				                                   );
		// create handle
		JacksonHandle resultsHandle = new JacksonHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		JsonNode resultNode = resultsHandle.get();
		JsonNode jsonPointNodes = resultNode.path("results");

		// Should have 2 nodes returned.
		//assertEquals("Two nodes not returned from testPolygonCoversBox method ", 2, resultNode.path("total").asInt());
		
		// Work on the asserts once server bugs are fixed. 
	}
	
	/*
	 * Polygon touches Point
	 */
	
	@Test	
	public void testPolygonTouchesPoint() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testPolygonTouchesPoint");
		
		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/polygon")), 
				                                       GeospatialOperator.TOUCHES, 
				                                       qb.point(-26.797920, 136.406250)
				                                   );
		// create handle
		JacksonHandle resultsHandle = new JacksonHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		JsonNode resultNode = resultsHandle.get();
		JsonNode jsonPointNodes = resultNode.path("results");

		// Should have 2 nodes returned.
		//assertEquals("Two nodes not returned from testPolygonCoversBox method ", 2, resultNode.path("total").asInt());
		
		// Work on the asserts once server bugs are fixed. 
	}
	
	/*
	 * Circle within Box
	 */
	
	@Test	
	public void testCircleWithinBox() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testCircleWithinBox");
						
		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/circle")), 
				                                       GeospatialOperator.WITHIN, 
				                                       qb.box(-6, 30, 100, 150)
				                                   );
		// create handle
		JacksonHandle resultsHandle = new JacksonHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		JsonNode resultNode = resultsHandle.get();
		JsonNode jsonPointNodes = resultNode.path("results");

		// Should have 2 nodes returned.
		//assertEquals("Two nodes not returned from testPolygonCoversBox method ", 2, resultNode.path("total").asInt());
		
		// Work on the asserts once server bugs are fixed. 
	}
	
	
	
	
	// TODO - 1) Add negative cases here for each of the operations.
	// TODO - 2) Usage of other QB methods.
	
	public static void writeGeoDoubleFilesToDB(DatabaseClient client, String filename, String uri, String type) throws IOException, ParserConfigurationException, SAXException
	{   
		// create doc manager
		DocumentManager docMgr = null;
		
		docMgr = documentMgrSelect(client, docMgr, type);				
		File file = new File(datasource + filename);
	    // create a handle on the content
	    FileHandle handle = new FileHandle(file);
	    handle.set(file);
	    // write the document content
		DocumentWriteSet writeset = docMgr.newWriteSet();
		writeset.add(uri, handle);
    
	    docMgr.write(writeset);
	    
	    System.out.println("Write " + uri + " to database");	
	}
	
	public static DocumentManager documentMgrSelect(DatabaseClient client, DocumentManager docMgr, String type)
	{
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

	@AfterClass	
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		// release client
		client.release();
		cleanupRESTServer(dbName, fNames);
	}
}
