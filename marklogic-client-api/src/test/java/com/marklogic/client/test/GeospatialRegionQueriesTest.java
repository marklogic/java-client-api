/*
 * Copyright 2012-2018 MarkLogic Corporation
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
package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.CoordinateSystem;
import com.marklogic.client.query.StructuredQueryBuilder.GeospatialOperator;
import com.marklogic.client.query.StructuredQueryDefinition;

public class GeospatialRegionQueriesTest {

  @AfterClass
  public static void teardown() {
    deleteEnvironment();
  }

  private static void deleteEnvironment() {
    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
    docMgr.delete("usa.xml", "cuba.xml", "mexico.xml", "p1.xml", "p2.xml","newpolygon.xml");
    Common.adminClient.newServerConfigManager().setQueryValidation(false);

    Common.propertyWait();
  }

  @BeforeClass
  public static void setup() {
    Common.connect();
    Common.connectAdmin();
    try {
      buildEnvironment();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
  }

  private static void buildEnvironment() throws ParserConfigurationException {
    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
    DocumentWriteSet writeset =docMgr.newWriteSet();
    Document domDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element root = domDocument.createElement("country");
    Element name = domDocument.createElement("name");
    name.appendChild(domDocument.createTextNode("USA"));
    root.appendChild(name);
    Element region = domDocument.createElement("region");
    region.appendChild(domDocument.createTextNode(
      "POLYGON((-126.73828125 48.78370458652897,-126.5625 32.07140409016531,-93.69140625 23.692823164376453,-94.04296875 27.652392306189306,-85.078125 28.273423646227233,-81.38671875 24.174821089781986,-77.6953125 24.174821089781986,-80.15625 30.872054770218686,-74.53125 35.1450666673043,-74.53125 37.272304747204885,-72.421875 40.420187991163814,-64.3359375 42.65658626887776,-68.203125 48.55152406860365,-80.5078125 43.68217491019519,-82.265625 46.7759883492146,-90 49.70174697005207,-124.453125 49.58792450200692,-126.73828125 48.78370458652897))"));
    root.appendChild(region);
    domDocument.appendChild(root);
    writeset.add("usa.xml", new DOMHandle().with(domDocument));

    domDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    root = domDocument.createElement("country");
    name = domDocument.createElement("name");
    name.appendChild(domDocument.createTextNode("Cuba"));
    root.appendChild(name);
    region = domDocument.createElement("region");
    region.appendChild(domDocument.createTextNode(
      "POLYGON((-85.341796875 22.585611426563773,-84.990234375 21.158287646331765,-82.353515625 21.975651300416885,-78.22265625 20.418776334798284,-78.4423828125 19.551508181341603,-73.8720703125 19.551508181341603,-73.7841796875 20.830063552944146,-80.2001953125 23.636472841274003,-84.1552734375 23.354359981207487,-85.341796875 22.585611426563773))"));
    root.appendChild(region);
    domDocument.appendChild(root);
    writeset.add("cuba.xml", new DOMHandle().with(domDocument));

    domDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    root = domDocument.createElement("country");
    name = domDocument.createElement("name");
    name.appendChild(domDocument.createTextNode("Mexico"));
    root.appendChild(name);
    region = domDocument.createElement("region");
    region.appendChild(domDocument.createTextNode(
      "POLYGON((0 0,0 10,10.00000001 10.00000001,10 0))"));
    root.appendChild(region);
    domDocument.appendChild(root);
    writeset.add("newpolygon.xml", new DOMHandle().with(domDocument));

    domDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    root = domDocument.createElement("country");
    name = domDocument.createElement("name");
    name.appendChild(domDocument.createTextNode("Mexico"));
    root.appendChild(name);
    region = domDocument.createElement("region");
    region.appendChild(domDocument.createTextNode(
      "POLYGON((-117.685546875 32.754017587673665,-113.73046875 33.34337733922461,-109.072265625 31.78795141483827,-108.017578125 32.309419326658585,-105.46875 32.235106018011905,-103.623046875 29.749116163754085,-101.513671875 30.509269143657455,-98.701171875 26.965161650744008,-96.240234375 26.25794987008959,-96.943359375 21.681930968539966,-94.74609375 19.211577365366374,-91.93359375 19.62603045380816,-90.439453125 22.17112652665051,-86.1328125 22.17112652665051,-86.923828125 17.543485716453013,-90.263671875 17.207966664544365,-89.736328125 15.775337196867119,-91.494140625 15.097581122595743,-92.28515625 13.564832686963,-94.21875 15.521438530421982,-96.767578125 14.842858452712312,-105.99609375 18.9624015719782,-105.8203125 21.272993875313244,-107.841796875 23.38663086930968,-110.654296875 21.763580088836456,-116.103515625 27.66796011611725,-117.685546875 32.754017587673665))"));
    root.appendChild(region);
    domDocument.appendChild(root);
    writeset.add("mexico.xml", new DOMHandle().with(domDocument));

    domDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    root = domDocument.createElement("point");
    Element lat = domDocument.createElement("lat");
    lat.appendChild(domDocument.createTextNode("5"));
    root.appendChild(lat);
    Element lon = domDocument.createElement("lon");
    lon.appendChild(domDocument.createTextNode("5"));
    root.appendChild(lon);
    domDocument.appendChild(root);
    writeset.add("p1.xml", new DOMHandle().with(domDocument));

    domDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    root = domDocument.createElement("point");
    lat = domDocument.createElement("lat");
    lat.appendChild(domDocument.createTextNode("10.0000001"));
    root.appendChild(lat);
    lon = domDocument.createElement("lon");
    lon.appendChild(domDocument.createTextNode("5"));
    root.appendChild(lon);
    domDocument.appendChild(root);
    writeset.add("p2.xml", new DOMHandle().with(domDocument));

    docMgr.write(writeset);
    Common.adminClient.newServerConfigManager().setQueryValidation(true);

    Common.propertyWait();
  }

  @Ignore
  public void testGeospatialRegionQuery() {
    QueryManager queryMgr = Common.client.newQueryManager();
    StructuredQueryBuilder qb = new StructuredQueryBuilder();
    StructuredQueryDefinition qdef;
    qdef = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/country/region")), GeospatialOperator.CONTAINS,
      qb.point(19.429297983081977, -99.140625));

    SearchHandle results = queryMgr.search(qdef, new SearchHandle());

    MatchDocumentSummary[] summaries = results.getMatchResults();
    for (MatchDocumentSummary summary : summaries) {
      assertEquals("mexico.xml", summary.getUri());
    }
  }

  @Ignore
  public void testFloatPrecision() {
    QueryManager queryMgr = Common.client.newQueryManager();
    StructuredQueryBuilder qb = new StructuredQueryBuilder();
    String options[] = new String[1];
    options[0] = "precision=float";
    StructuredQueryDefinition qdef;
    qdef = qb.geospatial(qb.geoElementPair(qb.element("point"), qb.element("lat"), qb.element("lon")), null,
      options, qb.box(0, 0, 10, 10));
    SearchHandle results = queryMgr.search(qdef, new SearchHandle());

    MatchDocumentSummary[] summaries = results.getMatchResults();
    assertEquals(2, summaries.length);
  }

  @Ignore
  public void testDoublePrecision() {
    QueryManager queryMgr = Common.client.newQueryManager();
    StructuredQueryBuilder qb = new StructuredQueryBuilder();
    String options[] = new String[1];
    options[0] = "precision=double";
    StructuredQueryDefinition qdef;
    qdef = qb.geospatial(qb.geoElementPair(qb.element("point"), qb.element("lat"), qb.element("lon")), null,
      options, qb.box(0, 0, 10, 10));
    SearchHandle results = queryMgr.search(qdef, new SearchHandle());

    MatchDocumentSummary[] summaries = results.getMatchResults();
    assertEquals(1, summaries.length);
  }

  @Ignore
  public void testGeospatialRegionDoubleQuery() {
    String options = "<options xmlns=\"http://marklogic.com/appservices/search\">"
                   + "    <search-option>filtered</search-option>"
                   + "    <debug>true</debug>"
                   + "</options>";
    StringHandle writeHandle = new StringHandle(options);

    QueryOptionsManager optionsMgr = Common.adminClient.newServerConfigManager().newQueryOptionsManager();
    optionsMgr.writeOptions("geodoubleoptions", writeHandle);

    QueryManager queryMgr = Common.client.newQueryManager();
    StructuredQueryBuilder qb = new StructuredQueryBuilder("geodoubleoptions");

    StructuredQueryDefinition qdef;
    qdef = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/country/region"), CoordinateSystem.WGS84DOUBLE), GeospatialOperator.COVERS,
      qb.point(10.00000003, 10.00000003));
    SearchHandle results = queryMgr.search(qdef, new SearchHandle());
    MatchDocumentSummary[] summaries = results.getMatchResults();
    assertEquals(0, summaries.length);

    qdef = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/country/region"), CoordinateSystem.WGS84), GeospatialOperator.COVERS,
      qb.point(10.00000003, 10.00000003));
    results = queryMgr.search(qdef, new SearchHandle());
    summaries = results.getMatchResults();
    assertEquals(1, summaries.length);

    qdef = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/country/region"), CoordinateSystem.getOther("wgs84", true)), GeospatialOperator.COVERS,
      qb.point(10.00000003, 10.00000003));
    results = queryMgr.search(qdef, new SearchHandle());
    summaries = results.getMatchResults();
    assertEquals(0, summaries.length);

    qdef = qb.geospatial(qb.geoRegionPath(qb.pathIndex("/country/region"), CoordinateSystem.getOther("wgs84")), GeospatialOperator.COVERS,
      qb.point(10.00000003, 10.00000003));
    results = queryMgr.search(qdef, new SearchHandle());
    summaries = results.getMatchResults();
    assertEquals(1, summaries.length);
    optionsMgr.deleteOptions("geodoubleoptions");
  }

  @Ignore
  public void testFloatPrecisionCoordinateSystem() {
    QueryManager queryMgr = Common.client.newQueryManager();
    StructuredQueryBuilder qb = new StructuredQueryBuilder();
    String options[] = new String[1];
    options[0] = "coordinate-system=wgs84";
    StructuredQueryDefinition qdef;
    qdef = qb.geospatial(qb.geoElementPair(qb.element("point"), qb.element("lat"), qb.element("lon")), null,
      options, qb.box(0, 0, 10, 10));
    SearchHandle results = queryMgr.search(qdef, new SearchHandle());

    MatchDocumentSummary[] summaries = results.getMatchResults();
    assertEquals(2, summaries.length);
  }

  @Ignore
  public void testDoublePrecisionCoordinateSystem() {
    QueryManager queryMgr = Common.client.newQueryManager();
    StructuredQueryBuilder qb = new StructuredQueryBuilder();
    String options[] = new String[1];
    options[0] = "coordinate-system=wgs84/double";
    StructuredQueryDefinition qdef;
    qdef = qb.geospatial(qb.geoElementPair(qb.element("point"), qb.element("lat"), qb.element("lon")), null,
      options, qb.box(0, 0, 10, 10));
    SearchHandle results = queryMgr.search(qdef, new SearchHandle());

    MatchDocumentSummary[] summaries = results.getMatchResults();
    assertEquals(1, summaries.length);
  }

  @Ignore
  public void testGeospatialRegionQueryConstraint() {
    QueryManager queryMgr = Common.client.newQueryManager();
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition qdef;
    String options = "<options xmlns=\"http://marklogic.com/appservices/search\">"
                   + "  <constraint name='geo'>"
                   + "    <geo-region-path coord='wgs84'>"
                   + "      <path-index>/country/region</path-index>"
                   + "    </geo-region-path>"
                   + "  </constraint>"
                   + "</options>";

    qdef = qb.geospatialRegionConstraint("geo", GeospatialOperator.CONTAINS,
      qb.point(19.429297983081977, -99.140625));

    String combinedQuery =
      "<search xmlns=\"http://marklogic.com/appservices/search\">" +
      qdef.serialize() + options +
      "</search>";
    RawCombinedQueryDefinition query = queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedQuery));

    SearchHandle results = queryMgr.search(query, new SearchHandle());
    MatchDocumentSummary[] summaries = results.getMatchResults();
    assertEquals(1, summaries.length);
    for (MatchDocumentSummary summary : summaries) {
      assertEquals("mexico.xml", summary.getUri());
    }
  }

  @Ignore
  public void testGeospatialRegionQueryOptionsConstraint() {
    String options = "<options xmlns=\"http://marklogic.com/appservices/search\">"
                   + "  <constraint name='geoo'>"
                   + "    <geo-region-path coord='wgs84'>"
                   + "      <path-index>/country/region</path-index>"
                   + "    </geo-region-path>"
                   + "  </constraint>"
                   + "</options>";

    // create a handle to send the query options
    StringHandle writeHandle = new StringHandle(options);

    QueryOptionsManager optionsMgr = Common.adminClient.newServerConfigManager().newQueryOptionsManager();
    optionsMgr.writeOptions("geooptions", writeHandle);

    QueryManager queryMgr = Common.client.newQueryManager();
    StructuredQueryBuilder qb = new StructuredQueryBuilder("geooptions");
    StructuredQueryDefinition qdef;
    qdef = qb.geospatialRegionConstraint("geoo", GeospatialOperator.DISJOINT,
      qb.point(19.429297983081977, -99.140625));

    SearchHandle results = queryMgr.search(qdef, new SearchHandle());
    MatchDocumentSummary[] summaries = results.getMatchResults();
    assertEquals(3, summaries.length);
    qdef = qb.geospatialRegionConstraint("geoo", GeospatialOperator.CONTAINS,
      qb.point(21.884239, -78.164978));
    results = queryMgr.search(qdef, new SearchHandle());
    summaries = results.getMatchResults();
    assertEquals(1, summaries.length);
    for (MatchDocumentSummary summary : summaries) {
      assertEquals("cuba.xml", summary.getUri());
    }
    optionsMgr.deleteOptions("geooptions");
  }

}
