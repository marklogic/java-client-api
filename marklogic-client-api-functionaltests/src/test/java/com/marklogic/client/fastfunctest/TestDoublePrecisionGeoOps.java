/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.CoordinateSystem;
import com.marklogic.client.query.StructuredQueryBuilder.GeospatialOperator;
import com.marklogic.client.query.StructuredQueryDefinition;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;




public class TestDoublePrecisionGeoOps extends AbstractFunctionalTest {

  private static String dbName = "java-functest";
  private static String datasource = "src/test/java/com/marklogic/client/functionaltest/data/geodouble/";

  static DatabaseClient client = null;

  @BeforeAll
  public static void setUp() throws Exception
  {
    // Load all necessary data / docs.
    String[] xmlFiles = { "Tropic-of-Capricorn.xml", "Tropic-of-Cancer.xml", "South-Pole.xml", "South-More.xml", "South-East.xml",
        "Prime-Meridian.xml", "North-West.xml", "North-Pole.xml", "International-Date-Line.xml", "Equator.xml" };
    String[] jsonFiles = { "Tropic-of-Capricorn-json.json", "Tropic-of-Cancer-json.json", "South-Pole-json.json", "South-More-json.json", "South-East-json.json",
        "Prime-Meridian-json.json", "North-West-json.json", "North-Pole-json.json", "International-Date-Line-json.json", "Equator-json.json" };

    client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : xmlFiles) {
      writeGeoDoubleFilesToDB(client, filename, "/" + filename, "XML");
    }

    for (String filename : jsonFiles) {
      writeGeoDoubleFilesToDB(client, filename, "/" + filename, "JSON");
    }
  }

  /*
   * Linestring contains Point(point is the endpoint of linestring
   */
  @Test
  public void testLinestringContainsPoint() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testLinestringContainsPoint");

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/linestring"), CoordinateSystem.WGS84DOUBLE),
        GeospatialOperator.CONTAINS, qb.point(2.85526278436658, -80.5078125));

    // create handle
    JacksonHandle resultsJsonHandle = new JacksonHandle();
    queryMgr.search(t, resultsJsonHandle);

    // get the result
    JsonNode resultJsonNode = resultsJsonHandle.get();
    JsonNode jsonPointNodes = resultJsonNode.path("results");

    // Should have 2 nodes returned.
    assertEquals( 2, resultJsonNode.path("total").asInt());
    assertTrue( jsonPointNodes.get(0).path("uri").asText().contains("/Equator-json.json") ||
        jsonPointNodes.get(1).path("uri").asText().contains("/Equator-json.json"));
    assertTrue( jsonPointNodes.get(0).path("uri").asText().contains("/Equator.xml") ||
        jsonPointNodes.get(1).path("uri").asText().contains("/Equator.xml"));

    // Verify snippets returned are correct
    SearchHandle resultsHandle = new SearchHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    MatchDocumentSummary matches[] = resultsHandle.getMatchResults();

    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    TreeMap<String, String> expectedMap = new TreeMap<String, String>();
    TreeMap<String, String> readMap = new TreeMap<String, String>();
    String linedesc = null;

    // Making sure that we proper results. Snippets are not complete. Taking in
    // what appears in summary.
    expectedMap.put("/Equator-json.json", "Json Linestring In Equator - South America");
    expectedMap.put("/Equator.xml", "Linestring In Equator - South America");

    for (MatchDocumentSummary summary : matches) {
      String docUri = summary.getUri();
      System.out.println("docURI is " + docUri);
      System.out.println("Snippet from Summary is " + summary.getFirstSnippetText());

      if (summary.getFormat().name().equalsIgnoreCase("XML")) {
        DOMHandle contentHandle = readDocumentUsingDOMHandle(client, docUri, "XML");
        Document readDoc = contentHandle.get();
        linedesc = readDoc.getElementsByTagName("line-desc").item(0).getFirstChild().getNodeValue().trim();

        System.out.println("Line desc from XML file is " + linedesc);

        // Verify that snippets from qb and read contain the same.
        assertTrue( linedesc.contains("Linestring In Equator - South America"));

        readDoc = null;
        contentHandle = null;
      }
      else if (summary.getFormat().name().equalsIgnoreCase("JSON")) {
        JacksonHandle jacksonhandle = new JacksonHandle();
        docMgr.read(docUri, jacksonhandle);
        JsonNode resultNode = jacksonhandle.get();
        linedesc = resultNode.path("root").path("item").path("line-desc").asText().trim();

        System.out.println("Snippet line-desc from JSON file is " + linedesc);

        // Verify that snippets from qb and read contain the same.
        assertTrue( linedesc.contains("Json Linestring In Equator - South America"));
        jacksonhandle = null;
        resultNode = null;
      }
      // Get the results into the map.
      readMap.put(docUri, linedesc);
    }
    assertTrue( expectedMap.equals(readMap));
  }

  /*
   * Circle contains circle
   */
  @Test
  public void testCircleContainsCircle() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testCircleContainsCircle");

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/circle"), CoordinateSystem.WGS84DOUBLE), GeospatialOperator.CONTAINS,
        qb.circle(qb.point(0, -66.09375), 6.897));
    // create handle
    JacksonHandle resultsHandle = new JacksonHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    JsonNode resultNode = resultsHandle.get();
    JsonNode jsonPointNodes = resultNode.path("results");

    // Should have 2 nodes returned.
    assertEquals( 2, resultNode.path("total").asInt());
    assertTrue( jsonPointNodes.get(0).path("uri").asText().contains("/Equator-json.json") ||
        jsonPointNodes.get(1).path("uri").asText().contains("/Equator-json.json"));
    assertTrue( jsonPointNodes.get(0).path("uri").asText().contains("/Equator.xml") ||
        jsonPointNodes.get(1).path("uri").asText().contains("/Equator.xml"));
  }

  /*
   * Box contains Polygon
   */
  @Test
  public void testBoxContainsPolygon() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testBoxContainsPolygon");

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/box"), CoordinateSystem.WGS84DOUBLE), GeospatialOperator.CONTAINS,
        qb.polygon(qb.point(-5, -70), qb.point(4, -70), qb.point(3, -60), qb.point(-3, -65), qb.point(-5, -70)));
    // create handle
    JacksonHandle resultsHandle = new JacksonHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    JsonNode resultNode = resultsHandle.get();
    JsonNode jsonPointNodes = resultNode.path("results");

    // Should have 2 nodes returned.
    assertEquals( 2, resultNode.path("total").asInt());
    assertTrue( jsonPointNodes.get(0).path("uri").asText().contains("/Equator-json.json") ||
        jsonPointNodes.get(1).path("uri").asText().contains("/Equator-json.json"));
    assertTrue( jsonPointNodes.get(0).path("uri").asText().contains("/Equator.xml") ||
        jsonPointNodes.get(1).path("uri").asText().contains("/Equator.xml"));
  }

  /*
   * Circle intersects Point
   */
  @Test
  public void testCircleIntersectsPoint() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testCircleIntersectsPoint");

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/circle"), CoordinateSystem.WGS84DOUBLE), GeospatialOperator.INTERSECTS,
        qb.point(0, -66.09375));
    // create handle
    JacksonHandle resultsHandle = new JacksonHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    JsonNode resultNode = resultsHandle.get();
    JsonNode jsonPointNodes = resultNode.path("results");

    // Should have 2 nodes returned.
    assertEquals( 2, resultNode.path("total").asInt());
    assertTrue( jsonPointNodes.get(0).path("uri").asText().contains("/Equator-json.json") ||
        jsonPointNodes.get(1).path("uri").asText().contains("/Equator-json.json"));
    // Verify match text in either of the two nodes returned. This is the Json
    // Point.
    assertTrue( jsonPointNodes.get(0).path("matches")
        .get(0).path("match-text").get(0).path("highlight").asText().contains("6.897 0,-66.09375") ||
        jsonPointNodes.get(1).path("matches")
            .get(0).path("match-text").get(0).path("highlight").asText().contains("6.897 0,-66.09375"));

    assertTrue( jsonPointNodes.get(0).path("uri").asText().contains("/Equator.xml") ||
        jsonPointNodes.get(1).path("uri").asText().contains("/Equator.xml"));
    // Verify match text in either of the two nodes returned. XML point.
    assertTrue( jsonPointNodes.get(0).path("matches")
        .get(0).path("match-text").get(0).path("highlight").asText().contains("6.897 0,-66.09375") ||
        jsonPointNodes.get(1).path("matches")
            .get(0).path("match-text").get(0).path("highlight").asText().contains("6.897 0,-66.09375"));
  }

  /*
   * linestring covered-by Polygon
   */
  @Test
  public void testcircleCoveredByPolygon() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testcircleCoveredByPolygon");

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/circle"), CoordinateSystem.WGS84DOUBLE),
        GeospatialOperator.COVEREDBY,
        qb.polygon(qb.point(40.13, -52.96), qb.point(40.45, -36.61), qb.point(38.16, -13.48),
            qb.point(21.09, -1.1), qb.point(0.0, -17.46), qb.point(-17.38, -12.52),
            qb.point(-33.08, -20.47), qb.point(-51.46, -28.61), qb.point(-65.33, -44.77),
            qb.point(-66.8, -66.0), qb.point(-53.99, -83.54), qb.point(-54.35, -105.49),
            qb.point(-24.59, -99.85), qb.point(-14.58, -110.86), qb.point(-0.0, -127.42),
            qb.point(13.44, -107.37), qb.point(35.78, -115.25), qb.point(53.21, -104.66),
            qb.point(48.69, -81.82), qb.point(55.37, -66.0), qb.point(40.13, -52.96)
            )
        );
    // create handle
    JacksonHandle resultsHandle = new JacksonHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    JsonNode resultNode = resultsHandle.get();
    JsonNode jsonPointNodes = resultNode.path("results");

    // Should have 2 nodes returned.
    assertEquals( 2, resultNode.path("total").asInt());

    // Verify match text in either of the two nodes returned. This is the Json
    // Point.
    assertTrue( jsonPointNodes.get(0).path("matches")
        .get(0).path("match-text").get(0).path("highlight").asText().contains("6.897 0,-66.09375") ||
        jsonPointNodes.get(1).path("matches")
            .get(0).path("match-text").get(0).path("highlight").asText().contains("6.897 0,-66.09375"));

    assertTrue( jsonPointNodes.get(0).path("uri").asText().contains("/Equator.xml") ||
        jsonPointNodes.get(1).path("uri").asText().contains("/Equator.xml"));
    // Verify match text in either of the two nodes returned. XML point.
    assertTrue( jsonPointNodes.get(0).path("matches")
        .get(0).path("match-text").get(0).path("highlight").asText().contains("6.897 0,-66.09375") ||
        jsonPointNodes.get(1).path("matches")
            .get(0).path("match-text").get(0).path("highlight").asText().contains("6.897 0,-66.09375"));
  }

  /*
   * Box covers Polygon
   */
  @Test
  public void testBoxCoversPolygon() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testBoxCoversPolygon");

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/box"), CoordinateSystem.WGS84DOUBLE),
        GeospatialOperator.COVERS,
        qb.polygon(qb.point(-5, -70), qb.point(4, -70), qb.point(3, -60),
            qb.point(-3, -65), qb.point(-5, -70)
            )
        );
    // create handle
    JacksonHandle resultsHandleJs = new JacksonHandle();
    queryMgr.search(t, resultsHandleJs);

    // get the result
    JsonNode resultNodeJs = resultsHandleJs.get();

    // Should have 2 nodes returned.
    assertEquals( 2, resultNodeJs.path("total").asInt());

    // Verify snippets returned are correct
    SearchHandle resultsHandle = new SearchHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    MatchDocumentSummary matches[] = resultsHandle.getMatchResults();

    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    TreeMap<String, String> expectedMap = new TreeMap<String, String>();
    TreeMap<String, String> readMap = new TreeMap<String, String>();
    String boxdesc = null;

    String snippetFromSummary = null;
    String snippetFromRead = null;

    // Making sure that we proper results
    expectedMap.put("/Equator-json.json", "Json Box In Equator - South America");
    expectedMap.put("/Equator.xml", "Box In Equator - South America");

    for (MatchDocumentSummary summary : matches) {
      String docUri = summary.getUri();
      System.out.println("docURI is " + docUri);
      System.out.println("Snippet Text is " + summary.getFirstSnippetText());
      // Get only the numbers. Strip away others. Refer to data files.
      snippetFromSummary = summary.getFirstSnippetText().split("\\[")[1].split("\\]")[0];

      if (summary.getFormat().name().equalsIgnoreCase("XML")) {
        DOMHandle contentHandle = readDocumentUsingDOMHandle(client, docUri, "XML");
        Document readDoc = contentHandle.get();
        boxdesc = readDoc.getElementsByTagName("box-desc").item(0).getFirstChild().getNodeValue();
        snippetFromRead = readDoc.getElementsByTagName("box").item(0).getFirstChild().getNodeValue();
        System.out.println("boxdesc from XML file is " + boxdesc);
        System.out.println("Snippet from XML file is " + snippetFromRead);

        // Verify that snippets from qb and read contain the same.
        assertTrue( snippetFromRead.contains(snippetFromSummary));

        readDoc = null;
        contentHandle = null;
      }
      else if (summary.getFormat().name().equalsIgnoreCase("JSON")) {
        JacksonHandle jacksonhandle = new JacksonHandle();
        docMgr.read(docUri, jacksonhandle);
        JsonNode resultNode = jacksonhandle.get();
        boxdesc = resultNode.path("root").path("item").path("box-desc").asText();
        snippetFromRead = resultNode.path("root").path("item").path("box").asText();
        System.out.println("boxdesc from JSON file is " + boxdesc);
        System.out.println("Snippet from JSON file is " + snippetFromRead);

        // Verify that snippets from qb and read contain the same.
        assertTrue( snippetFromRead.contains(snippetFromSummary));
        jacksonhandle = null;
        resultNode = null;
      }
      // Get the results into the map.
      readMap.put(docUri, boxdesc);
    }
    assertTrue( expectedMap.equals(readMap));
  }

  /*
   * Linestring crosses Box.
   */
  @Test
  public void testLinestringCrossesBox() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testLinestringCrossesBox");

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/linestring"), CoordinateSystem.WGS84DOUBLE),
        GeospatialOperator.CROSSES,
        qb.box(-5.45, -76.35643, 5.35, -54.636)
        );
    // create handle
    JacksonHandle resultsHandle = new JacksonHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    JsonNode resultNode = resultsHandle.get();

    // Should have 2 nodes returned.
    assertEquals( 2, resultNode.path("total").asInt());

  }

  /*
   * Box overlaps Circle
   */
  @Test
  public void testBoxOverlapsCircle() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testBoxOverlapsCircle");

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/box"), CoordinateSystem.WGS84DOUBLE),
        GeospatialOperator.OVERLAPS,
        qb.circle(qb.point(25.234, 85.2345), 10)
        );
    // create handle
    JacksonHandle resultsHandleJs = new JacksonHandle();
    queryMgr.search(t, resultsHandleJs);

    // get the result
    JsonNode resultNodeJs = resultsHandleJs.get();

    // Should have 2 nodes returned.
    assertEquals( 2, resultNodeJs.path("total").asInt());

    // Verify snippets returned are correct
    SearchHandle resultsHandle = new SearchHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    MatchDocumentSummary matches[] = resultsHandle.getMatchResults();

    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    TreeMap<String, String> expectedMap = new TreeMap<String, String>();
    TreeMap<String, String> readMap = new TreeMap<String, String>();
    String boxdesc = null;

    String snippetFromSummary = null;
    String snippetFromRead = null;

    // Making sure that we proper results
    expectedMap.put("/Tropic-of-Cancer-json.json", "Json Box In Tropic of Cancer - Delhi");
    expectedMap.put("/Tropic-of-Cancer.xml", "Box In Tropic of Cancer - Delhi");

    for (MatchDocumentSummary summary : matches) {
      String docUri = summary.getUri();
      System.out.println("docURI is " + docUri);
      System.out.println("Snippet Text is " + summary.getFirstSnippetText());
      // Get only the numbers. Strip away others. Refer to data files.
      snippetFromSummary = summary.getFirstSnippetText().split("\\[")[1].split("\\]")[0];

      if (summary.getFormat().name().equalsIgnoreCase("XML")) {
        DOMHandle contentHandle = readDocumentUsingDOMHandle(client, docUri, "XML");
        Document readDoc = contentHandle.get();
        boxdesc = readDoc.getElementsByTagName("box-desc").item(0).getFirstChild().getNodeValue();
        snippetFromRead = readDoc.getElementsByTagName("box").item(0).getFirstChild().getNodeValue();
        System.out.println("boxdesc from XML file is " + boxdesc);
        System.out.println("Snippet from XML file is " + snippetFromRead);

        // Verify that snippets from qb and read contain the same.
        assertTrue( snippetFromRead.contains(snippetFromSummary));

        readDoc = null;
        contentHandle = null;
      }
      else if (summary.getFormat().name().equalsIgnoreCase("JSON")) {
        JacksonHandle jacksonhandle = new JacksonHandle();
        docMgr.read(docUri, jacksonhandle);
        JsonNode resultNode = jacksonhandle.get();
        boxdesc = resultNode.path("root").path("item").path("box-desc").asText();
        snippetFromRead = resultNode.path("root").path("item").path("box").asText();
        System.out.println("boxdesc from JSON file is " + boxdesc);
        System.out.println("Snippet from JSON file is " + snippetFromRead);

        // Verify that snippets from qb and read contain the same.
        assertTrue( snippetFromRead.contains(snippetFromSummary));
        jacksonhandle = null;
        resultNode = null;
      }
      // Get the results into the map.
      readMap.put(docUri, boxdesc);
    }
    assertTrue( expectedMap.equals(readMap));
  }

  /*
   * Polygon disjoint Polygon - polygon near south east
   */
  @Test
  public void testPolygonDisjointPolygon() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testPolygonDisjointPolygon");

    QueryManager queryMgr = client.newQueryManager();
    queryMgr.setPageLength(50);

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/polygon"), CoordinateSystem.WGS84),
        GeospatialOperator.DISJOINT,
        qb.polygon(
            qb.point(-90, 131),
            qb.point(-85, 133),
            qb.point(-87, 134),
            qb.point(-88, 135),
            qb.point(-90, 131))
        );
    // create handle
    JacksonHandle resultsHandlejh = new JacksonHandle();
    queryMgr.search(t, resultsHandlejh);

    // get the result
    JsonNode resultNodejh = resultsHandlejh.get();

    // Should have 20 nodes returned.
    assertEquals( 20, resultNodejh.path("total").asInt());
  }

  /*
   * Box disjoint Box--except Tropic of Capricorn-Australia
   */
  @Test
  public void testBoxDisjointBox() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testBoxDisjointBox");

    QueryManager queryMgr = client.newQueryManager();
    queryMgr.setPageLength(50);

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/box"), CoordinateSystem.WGS84DOUBLE),
        GeospatialOperator.DISJOINT,
        qb.box(-40.234, 100.4634, -20.345, 140.45230)
        );
    // create handle
    SearchHandle resultsHandle = new SearchHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    MatchDocumentSummary matches[] = resultsHandle.getMatchResults();

    // Should have 18 nodes returned.
    assertEquals( 18, matches.length);

    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    TreeMap<String, String> expectedMap = new TreeMap<String, String>();
    TreeMap<String, String> readMap = new TreeMap<String, String>();
    String boxDesc = null;

    // Making sure that we proper results
    expectedMap.put("/Equator-json.json", "Json Box In Equator - South America");
    expectedMap.put("/Equator.xml", "Box In Equator - South America");
    expectedMap.put("/International-Date-Line-json.json", "Json Box In the Date line");
    expectedMap.put("/International-Date-Line.xml", "Box In the Date line");
    expectedMap.put("/North-Pole-json.json", "Json Box In North Pole");
    expectedMap.put("/North-Pole.xml", "Box In North Pole");
    expectedMap.put("/North-West-json.json", "Json Box In North West part");
    expectedMap.put("/North-West.xml", "Box In North West part");
    expectedMap.put("/Prime-Meridian-json.json", "Json Box On Prime-Meridian");
    expectedMap.put("/Prime-Meridian.xml", "Box On Prime-Meridian");
    expectedMap.put("/South-East-json.json", "Json Box In South East part");
    expectedMap.put("/South-East.xml", "Box In South East part");
    expectedMap.put("/South-More-json.json", "Json Box more towards South Pole");
    expectedMap.put("/South-More.xml", "Box more towards South Pole");
    expectedMap.put("/South-Pole-json.json", "Json Box In South Pole - Antarctic circle");
    expectedMap.put("/South-Pole.xml", "Box In South Pole - Antarctic circle");
    expectedMap.put("/Tropic-of-Cancer-json.json", "Json Box In Tropic of Cancer - Delhi");
    expectedMap.put("/Tropic-of-Cancer.xml", "Box In Tropic of Cancer - Delhi");

    for (MatchDocumentSummary summary : matches) {
      String docUri = summary.getUri();
      System.out.println("docURI is " + docUri);
      if (summary.getFormat().name().equalsIgnoreCase("XML")) {
        DOMHandle contentHandle = readDocumentUsingDOMHandle(client, docUri, "XML");
        Document readDoc = contentHandle.get();
        boxDesc = readDoc.getElementsByTagName("box-desc").item(0).getFirstChild().getNodeValue();
        System.out.println("boxDesc from XML file is " + boxDesc);

        readDoc = null;
        contentHandle = null;
      }
      else if (summary.getFormat().name().equalsIgnoreCase("JSON")) {
        JacksonHandle jacksonhandle = new JacksonHandle();
        docMgr.read(docUri, jacksonhandle);
        JsonNode resultNode = jacksonhandle.get();
        boxDesc = resultNode.path("root").path("item").path("box-desc").asText();
        System.out.println("boxDesc from JSON file is " + boxDesc);

        jacksonhandle = null;
        resultNode = null;
      }
      // Get the results into the map.
      readMap.put(docUri, boxDesc);
    }
    assertTrue( expectedMap.equals(readMap));
  }

  /*
   * Point equals Point
   */

  @Test
  public void testPointEqualsPoint() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testPointEqualsPoint");

    QueryManager queryMgr = client.newQueryManager();
    queryMgr.setPageLength(50);

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/point"), CoordinateSystem.WGS84),
        GeospatialOperator.EQUALS,
        qb.point(0, -66.09375)
        );
    // create handle
    JacksonHandle resultsHandlejh = new JacksonHandle();
    queryMgr.search(t, resultsHandlejh);

    // get the result
    JsonNode resultNodejh = resultsHandlejh.get();

    // Should have 2 nodes returned.
    assertEquals( 2, resultNodejh.path("total").asInt());

    // Verify snippets returned are correct
    SearchHandle resultsHandle = new SearchHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    MatchDocumentSummary matches[] = resultsHandle.getMatchResults();

    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    TreeMap<String, String> expectedMap = new TreeMap<String, String>();
    TreeMap<String, String> readMap = new TreeMap<String, String>();
    String pointdesc = null;

    String snippetFromSummary = null;
    String snippetFromRead = null;

    // Making sure that we proper results
    expectedMap.put("/Equator-json.json", "Json Point In Equator - South America");
    expectedMap.put("/Equator.xml", "Point In Equator - South America");

    for (MatchDocumentSummary summary : matches) {
      String docUri = summary.getUri();
      System.out.println("docURI is " + docUri);
      System.out.println("Snippet Text is " + summary.getFirstSnippetText());
      // Get only the numbers. Strip away others. Refer to data files.
      snippetFromSummary = summary.getFirstSnippetText().split("POINT\\(")[1].split("\\)")[0];

      if (summary.getFormat().name().equalsIgnoreCase("XML")) {
        DOMHandle contentHandle = readDocumentUsingDOMHandle(client, docUri, "XML");
        Document readDoc = contentHandle.get();
        pointdesc = readDoc.getElementsByTagName("point-desc").item(0).getFirstChild().getNodeValue();
        snippetFromRead = readDoc.getElementsByTagName("point").item(0).getFirstChild().getNodeValue();
        System.out.println("boxdesc from XML file is " + pointdesc);
        System.out.println("Snippet from XML file is " + snippetFromRead);

        // Verify that snippets from qb and read contain the same.
        assertTrue( snippetFromRead.contains(snippetFromSummary));

        readDoc = null;
        contentHandle = null;
      }
      else if (summary.getFormat().name().equalsIgnoreCase("JSON")) {
        JacksonHandle jacksonhandle = new JacksonHandle();
        docMgr.read(docUri, jacksonhandle);
        JsonNode resultNode = jacksonhandle.get();
        pointdesc = resultNode.path("root").path("item").path("point-desc").asText();
        snippetFromRead = resultNode.path("root").path("item").path("point").asText();
        System.out.println("boxdesc from JSON file is " + pointdesc);
        System.out.println("Snippet from JSON file is " + snippetFromRead);

        // Verify that snippets from qb and read contain the same.
        assertTrue( snippetFromRead.contains(snippetFromSummary));
        jacksonhandle = null;
        resultNode = null;
      }
      // Get the results into the map.
      readMap.put(docUri, pointdesc);
    }
    assertTrue( expectedMap.equals(readMap));
  }

  /*
   * Polygon touches Point
   */

  @Test
  public void testPolygonTouchesPoint() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testPolygonTouchesPoint");

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/polygon")),
        GeospatialOperator.TOUCHES,
        qb.point(-26.0, 100.89)
        );
    // create handle
    JacksonHandle resultsHandle = new JacksonHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    JsonNode resultNode = resultsHandle.get();
    JsonNode jsonPointNodes = resultNode.path("results");

    // Should have 2 nodes returned.
    assertEquals( 2, resultNode.path("total").asInt());

    String exptdString = "POLYGON((153.65 -8.35,170.57 -26.0,162.52 -52.52,136.0 -56.35,111.0 -51.0,100.89 -26.0,108.18 1.82,136.0 10.26,153.65 -8.35))";
    String polygondesc1 = jsonPointNodes.get(0).path("matches").get(0).path("match-text").get(0).path("highlight").asText();
    String polygondesc2 = jsonPointNodes.get(1).path("matches").get(0).path("match-text").get(0).path("highlight").asText();

    assertTrue( exptdString.equalsIgnoreCase(polygondesc1));
    assertTrue( exptdString.equalsIgnoreCase(polygondesc2));
  }

  /*
   * Circle within Box
   */

  @Test
  public void testCircleWithinBox() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testCircleWithinBox");

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/root/item/circle"), CoordinateSystem.WGS84DOUBLE),
        GeospatialOperator.WITHIN,
        qb.box(-6, 30, 100, 150)
        );
    // create handle
    JacksonHandle resultsHandlejh = new JacksonHandle();
    queryMgr.search(t, resultsHandlejh);

    // get the result
    JsonNode resultNodejh = resultsHandlejh.get();

    // Should have 2 nodes returned.
    assertEquals( 2, resultNodejh.path("total").asInt());

    // Verify snippets returned are correct
    SearchHandle resultsHandle = new SearchHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    MatchDocumentSummary matches[] = resultsHandle.getMatchResults();

    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    TreeMap<String, String> expectedMap = new TreeMap<String, String>();
    TreeMap<String, String> readMap = new TreeMap<String, String>();
    String circledesc = null;

    String snippetFromSummary = null;
    String snippetFromRead = null;

    // Making sure that we proper results
    expectedMap.put("/Tropic-of-Cancer-json.json", "Json Circle In Tropic of Cancer - Delhi");
    expectedMap.put("/Tropic-of-Cancer.xml", "Circle In Tropic of Cancer - Delhi");

    for (MatchDocumentSummary summary : matches) {
      String docUri = summary.getUri();
      System.out.println("docURI is " + docUri);
      System.out.println("Snippet Text is " + summary.getFirstSnippetText());
      // Get only the numbers. Strip away others. Refer to data files.
      snippetFromSummary = summary.getFirstSnippetText().trim();
      if (summary.getFormat().name().equalsIgnoreCase("XML")) {
        DOMHandle contentHandle = readDocumentUsingDOMHandle(client, docUri, "XML");
        Document readDoc = contentHandle.get();
        circledesc = readDoc.getElementsByTagName("circle-desc").item(0).getFirstChild().getNodeValue();
        snippetFromRead = readDoc.getElementsByTagName("circle").item(0).getFirstChild().getNodeValue();
        System.out.println("circledesc from XML file is " + circledesc);
        System.out.println("Snippet from XML file is " + snippetFromRead);

        // Verify that snippets from qb and read contain the same.
        assertTrue( snippetFromRead.contains(snippetFromSummary));

        readDoc = null;
        contentHandle = null;
      }
      else if (summary.getFormat().name().equalsIgnoreCase("JSON")) {
        JacksonHandle jacksonhandle = new JacksonHandle();
        docMgr.read(docUri, jacksonhandle);
        JsonNode resultNode = jacksonhandle.get();
        circledesc = resultNode.path("root").path("item").path("circle-desc").asText();
        snippetFromRead = resultNode.path("root").path("item").path("circle").asText();
        System.out.println("circledesc from JSON file is " + circledesc);
        System.out.println("Snippet from JSON file is " + snippetFromRead);

        // Verify that snippets from qb and read contain the same.
        assertTrue( snippetFromRead.contains(snippetFromSummary));
        jacksonhandle = null;
        resultNode = null;
      }
      // Get the results into the map.
      readMap.put(docUri, circledesc);
    }
    assertTrue( expectedMap.equals(readMap));
  }

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
    switch (type) {
      case "XML":
        docMgr = client.newXMLDocumentManager();
        break;
      case "Text":
        docMgr = client.newTextDocumentManager();
        break;
      case "JSON":
        docMgr = client.newJSONDocumentManager();
        break;
      case "Binary":
        docMgr = client.newBinaryDocumentManager();
        break;
      case "JAXB":
        docMgr = client.newXMLDocumentManager();
        break;
      default:
        System.out.println("Invalid type");
        break;
    }

    return docMgr;
  }
}
