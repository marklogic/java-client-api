/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.FragmentScope;
import com.marklogic.client.query.StructuredQueryDefinition;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;


public class TestStandaloneGeoQuery extends AbstractFunctionalTest {

  @Test
  public void testBug22184() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
  {
    System.out.println("Running testBug22184");

    String[] filenames = { "geo-constraint1.xml", "geo-constraint2.xml", "geo-constraint3.xml", "geo-constraint4.xml", "geo-constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/standalone-geo-query/", "XML");
    }

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoElement(qb.element("g-elem-pair")));

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    String result = convertXMLDocumentToString(resultDoc);
    System.out.println(result);
    assertTrue( result.contains("start=\"1\" total=\"5\""));

    // release client
    client.release();
  }

  @Test
  public void testStandaloneGeoElemQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testStandaloneGeoElemQuery");

    String[] filenames = { "geo-constraint1.xml", "geo-constraint2.xml", "geo-constraint3.xml", "geo-constraint4.xml", "geo-constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/standalone-geo-query/", "XML");
    }

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoElement(qb.element("g-elem-pair")), qb.point(12, 5));

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("5", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testStandaloneGeoElemPairQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testStandaloneGeoElemPairQuery");

    String[] filenames = { "geo-constraint1.xml", "geo-constraint2.xml", "geo-constraint3.xml", "geo-constraint4.xml", "geo-constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/standalone-geo-query/", "XML");
    }

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoElementPair(qb.element("g-elem-pair"), qb.element("lat"), qb.element("long")), qb.point(12, 5));

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testStandaloneGeoElemPairQueryEnhanced() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      XpathException, TransformerException
  {
    System.out.println("Running testStandaloneGeoElemPairQueryEnhanced");

    String[] filenames = { "geo-constraint1.xml", "geo-constraint2.xml", "geo-constraint3.xml", "geo-constraint4.xml", "geo-constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/standalone-geo-query/", "XML");
    }

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    // StructuredQueryDefinition t =
    // qb.geospatial(qb.geoElementPair(qb.element("g-elem-pair"),
    // qb.element("lat"), qb.element("long")), qb.point(12, 5));
    String[] options = { "coordinate-system=wgs84", "units=miles" };
    StructuredQueryDefinition t = qb.geospatial(qb.geoElementPair(qb.element("g-elem-pair"), qb.element("lat"), qb.element("long")), FragmentScope.DOCUMENTS, options,
        qb.point(12, 5));
    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testStandaloneGeoElemChildQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testStandaloneGeoElemQuery");

    String[] filenames = { "geo-constraint1.xml", "geo-constraint2.xml", "geo-constraint3.xml", "geo-constraint4.xml", "geo-constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/standalone-geo-query/", "XML");
    }

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoElement(qb.element("g-elem-child-parent"), qb.element("g-elem-child-point")), qb.point(12, 5));

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testStandaloneGeoElemChildQueryEnhanced() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      XpathException, TransformerException
  {
    System.out.println("Running testStandaloneGeoElemQueryEnhanced");

    String[] filenames = { "geo-constraint1.xml", "geo-constraint2.xml", "geo-constraint3.xml", "geo-constraint4.xml", "geo-constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/standalone-geo-query/", "XML");
    }

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    String[] options = { "coordinate-system=wgs84", "units=miles" };
    StructuredQueryDefinition t = qb.geospatial(qb.geoElement(qb.element("g-elem-child-parent"), qb.element("g-elem-child-point")), FragmentScope.DOCUMENTS, options,
        qb.point(12, 5));

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testStandaloneGeoAttrPairQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testStandaloneGeoAttrPairQuery");

    String[] filenames = { "geo-constraint1.xml", "geo-constraint2.xml", "geo-constraint3.xml", "geo-constraint4.xml", "geo-constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/standalone-geo-query/", "XML");
    }

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoAttributePair(qb.element("g-attr-pair"), qb.attribute("lat"), qb.attribute("long")), qb.point(12, 5));

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(t, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testStandaloneGeoAttrPairQueryBox() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testStandaloneGeoAttrPairQueryBox");

    String[] filenames = { "geo-constraint1.xml", "geo-constraint2.xml", "geo-constraint3.xml", "geo-constraint4.xml", "geo-constraint5.xml", "element-attribute-pair-geo-data.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/standalone-geo-query/", "XML");
    }

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.geospatial(qb.geoAttributePair(qb.element("point"), qb.attribute("latitude"), qb.attribute("longitude")), qb.box(52, 172, 55, -163));
    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(t, resultsHandle);
    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println("Results of Box :" + convertXMLDocumentToString(resultDoc));
    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertTrue( convertXMLDocumentToString(resultDoc).contains("beijing city in china bangkok city in thailand norh pole place where Santa lives"));

    // Circle Query
    QueryManager queryMgr1 = client.newQueryManager();
    // create query def
    StructuredQueryBuilder qb1 = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t1 = qb1.geospatial(qb1.geoAttributePair(qb1.element("point"), qb1.attribute("latitude"), qb1.attribute("longitude")),
        qb1.circle(qb1.point(53.90, -166.70), 3));
    // create handle
    DOMHandle resultsHandle1 = new DOMHandle();
    queryMgr1.search(t1, resultsHandle1);
    // get the result
    Document resultDoc1 = resultsHandle1.get();
    System.out.println("Results of Circle :" + convertXMLDocumentToString(resultDoc1));
    assertTrue( convertXMLDocumentToString(resultDoc1).contains("beijing city in china bangkok city in thailand norh pole place where Santa lives"));
    // Polygon Query
    QueryManager queryMgr2 = client.newQueryManager();
    // create query def
    StructuredQueryBuilder qb2 = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t2 = qb2.geospatial(qb2.geoAttributePair(qb2.element("point"), qb2.attribute("latitude"), qb2.attribute("longitude")),
        qb2.polygon(qb2.point(54, -165), qb2.point(52, -167), qb2.point(53, 167), qb2.point(54, -165)));
    // create handle
    DOMHandle resultsHandle2 = new DOMHandle();
    queryMgr2.search(t2, resultsHandle2);
    // get the result
    Document resultDoc2 = resultsHandle2.get();
    System.out.println("Results of Polygon :" + convertXMLDocumentToString(resultDoc2));
    assertTrue( convertXMLDocumentToString(resultDoc2).contains("beijing city in china bangkok city in thailand norh pole place where Santa lives"));
    // release client
    client.release();
  }

  @Test
  public void testStandaloneGeoAttrPairQueryWithOrAndNear() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      XpathException, TransformerException
  {
    System.out.println("Running testStandaloneGeoAttrPairQueryWithOr");

    String[] filenames = { "geo-constraint1.xml", "geo-constraint2.xml", "geo-constraint3.xml", "geo-constraint4.xml", "geo-constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/standalone-geo-query/", "XML");
    }

    QueryManager queryMgr = client.newQueryManager();

    // create OR query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition x = qb.geospatial(qb.geoAttributePair(qb.element("g-attr-pair"), qb.attribute("lat"), qb.attribute("long")), qb.point(12, 5));
    StructuredQueryDefinition y = qb.word(qb.element("name"), "karl_gale");
    StructuredQueryDefinition z = qb.or(x, y);

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(z, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println("Result of OR Query" + convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);

    // create AND query def
    StructuredQueryDefinition p = qb.geospatial(qb.geoAttributePair(qb.element("g-attr-pair"), qb.attribute("lat"), qb.attribute("long")), qb.point(12, 6));
    StructuredQueryDefinition q = qb.word(qb.element("name"), "karl_gale");
    StructuredQueryDefinition r = qb.and(p, q);

    // create handle
    DOMHandle resultsHandle1 = new DOMHandle();
    queryMgr.search(r, resultsHandle1);

    // get the result
    Document resultDoc1 = resultsHandle1.get();
    System.out.println("Results of AND Query" + convertXMLDocumentToString(resultDoc1));

    assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    // release client
    // create NEAR query def
    StructuredQueryDefinition c = qb.near(z, r);

    // create handle
    DOMHandle resultsHandle2 = new DOMHandle();
    queryMgr.search(c, resultsHandle2);

    // get the result
    Document resultDoc2 = resultsHandle2.get();
    System.out.println("Results of NEAR Query" + convertXMLDocumentToString(resultDoc2));

    client.release();
  }
}
