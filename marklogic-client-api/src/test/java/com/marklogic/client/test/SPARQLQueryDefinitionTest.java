/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.semantics.*;
import com.marklogic.client.test.junit5.RequiresML11;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

public class SPARQLQueryDefinitionTest {

  private static SPARQLQueryManager smgr;
  private static GraphManager gmgr;

  private static String TEST_TRIG = "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> . "
    + "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> . "
    + "@prefix : <http://marklogic.com/SPARQLQDefTest/> . "
    + "{ :r1 a :c1 ; "
    + "       :p1 \"string value 0\" ; "
    + "       :p2 \"string value 1\" . "
    + "  :r2 :p1 \"string value 2\" ; "
    + "       :p2 \"string value 3\"@en .  } "
    + ":g1 { :r1 :p3 \"1\"^^xsd:" + RDFTypes.STRING              + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.BOOLEAN             + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.DECIMAL             + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.INTEGER             + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.DOUBLE              + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.FLOAT               + " . "
    + "      :r1 :p3 \"00:01:01\"^^xsd:" + RDFTypes.TIME         + " . "
    + "      :r1 :p3 \"2014-09-01\"^^xsd:" + RDFTypes.DATE + " ."
    + "      :r1 :p3 \"2014-09-01T00:00:00+02:00\"^^xsd:" + RDFTypes.DATETIME + " ."
    + "      :r1 :p3 \"2001\"^^xsd:" + RDFTypes.GYEAR            + " . "
    + "      :r1 :p3 \"--01\"^^xsd:" + RDFTypes.GMONTH           + " . "
    + "      :r1 :p3 \"---01\"^^xsd:" + RDFTypes.GDAY            + " . "
    + "      :r1 :p3 \"2001-01\"^^xsd:" + RDFTypes.GYEARMONTH    + " . "
    + "      :r1 :p3 \"--01-01\"^^xsd:" + RDFTypes.GMONTHDAY     + " . "
    + "      :r1 :p3 \"P100D\"^^xsd:" + RDFTypes.DURATION        + " . "
    + "      :r1 :p3 \"P1M\"^^xsd:" + RDFTypes.YEARMONTHDURATION + " . "
    + "      :r1 :p3 \"P100D\"^^xsd:" + RDFTypes.DAYTIMEDURATION + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.BYTE                + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.SHORT               + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.INT                 + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.LONG                + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.UNSIGNEDBYTE        + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.UNSIGNEDSHORT       + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.UNSIGNEDINT         + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.UNSIGNEDLONG        + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.POSITIVEINTEGER     + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.NONNEGATIVEINTEGER  + " . "
    + "      :r1 :p3 \"-1\"^^xsd:" + RDFTypes.NEGATIVEINTEGER    + " . "
    + "      :r1 :p3 \"-1\"^^xsd:" + RDFTypes.NONPOSITIVEINTEGER + " . "
    + "      :r1 :p3 \"010203\"^^xsd:" + RDFTypes.HEXBINARY      + " . "
    + "      :r1 :p3 \"AQID\"^^xsd:" + RDFTypes.BASE64BINARY     + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.ANYURI              + " . "
    + "      :r1 :p3 \"en\"^^xsd:" + RDFTypes.LANGUAGE           + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.NORMALIZEDSTRING    + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.TOKEN               + " . "
    + "      :r1 :p3 \"1\"^^xsd:" + RDFTypes.NMTOKEN             + " . "
    + "      :r1 :p3 \"A\"^^xsd:" + RDFTypes.NAME                + " . "
    + "      :r1 :p3 \"A\"^^xsd:" + RDFTypes.NCNAME              + " . } "
    + ":g2 { :r2 :p4 \"p4 string value\" .  :r3 a :c2 .  } "
    + ":g3 { :r3 :p5 :r4 .  } "
    + ":g4 { :r4 a :c3 .  } "
    + ":o1 { :p1 rdfs:domain :c1 .  } "
    + ":o2 { :c2 rdfs:subClassOf :c1 .  } "
    + ":o3 { :p4 rdfs:subPropertyOf :p1 .  } "
    + ":o4 { :p5 rdfs:range :c2 .  } ";

  @BeforeAll
  public static void beforeClass() {
    Common.connect();
    gmgr = Common.client.newGraphManager();
    gmgr.mergeGraphs(new StringHandle(TEST_TRIG)
      .withMimetype("text/trig"));
    smgr = Common.client.newSPARQLQueryManager();
  }

  @AfterAll
  public static void afterClass() {
    Common.connect();
    gmgr = Common.client.newGraphManager();
    gmgr.delete(GraphManager.DEFAULT_GRAPH);
    gmgr.delete("http://marklogic.com/SPARQLQDefTest/g1");
    gmgr.delete("http://marklogic.com/SPARQLQDefTest/g2");
    gmgr.delete("http://marklogic.com/SPARQLQDefTest/g4");
    gmgr.delete("http://marklogic.com/SPARQLQDefTest/g65");
    gmgr.delete("http://marklogic.com/SPARQLQDefTest/o1");
    gmgr.delete("http://marklogic.com/SPARQLQDefTest/o2");
    gmgr.delete("http://marklogic.com/SPARQLQDefTest/o3");
    gmgr.delete("http://marklogic.com/SPARQLQDefTest/o4");
  }

  private ArrayNode executeAndExtractBindings(SPARQLQueryDefinition qdef) {
    JacksonHandle handle = smgr.executeSelect(qdef, new JacksonHandle());
    JsonNode results = handle.get();

    ArrayNode bindings = (ArrayNode) results.findPath("results").findPath(
      "bindings");
    return bindings;
  }

	/**
	 * This is oddly failing on Jenkins - but not locally - with an error of:
	 *
	 * com.marklogic.client.FailedRequestException: Local message: failed to apply resource at /graphs/sparql: Internal Server Error.
	 * Server Message: XDMP-CORRUPT: <http://marklogic.com/SPARQLQDefTest/r1> <http://marklogic.com/SPARQLQDefTest/p3> ?o . --
	 * read /space/Forests/java-unittest-2/000004b1/TripleValueData: File corrupt, bad RDFValue::decodeSimpleV2, tag=32 .
	 */
	@Test
  public void testBindings() {
    String ask = "ASK FROM <http://marklogic.com/SPARQLQDefTest/g1> " +
      "WHERE { <http://marklogic.com/SPARQLQDefTest/r1> <http://marklogic.com/SPARQLQDefTest/p3> ?o }";
    SPARQLQueryDefinition askQuery = smgr.newQueryDefinition(ask);

    SPARQLBindings bindings = askQuery.getBindings();
    bindings.bind("o", "1", RDFTypes.STRING);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    bindings.bind("o", "foo", RDFTypes.STRING);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.BOOLEAN);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "0", RDFTypes.BOOLEAN);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.DECIMAL);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.DECIMAL);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.INTEGER);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.INTEGER);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.DOUBLE);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.DOUBLE);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.FLOAT);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.FLOAT);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "00:01:01", RDFTypes.TIME);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "00:01:02", RDFTypes.TIME);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "2014-09-01", RDFTypes.DATE);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2014-09-02", RDFTypes.DATE);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "2014-09-01T00:00:00+02:00", RDFTypes.DATETIME);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2014-09-02T00:00:00+02:00", RDFTypes.DATETIME);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "2001", RDFTypes.GYEAR);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2002", RDFTypes.GYEAR);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "--01", RDFTypes.GMONTH);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "--02", RDFTypes.GMONTH);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "---01", RDFTypes.GDAY);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "---02", RDFTypes.GDAY);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "2001-01", RDFTypes.GYEARMONTH);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2002-02", RDFTypes.GYEARMONTH);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "--01-01", RDFTypes.GMONTHDAY);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "--02-02", RDFTypes.GMONTHDAY);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "P100D", RDFTypes.DURATION);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "P200D", RDFTypes.DURATION);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "P1M", RDFTypes.YEARMONTHDURATION);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "P2M", RDFTypes.YEARMONTHDURATION);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "P100D", RDFTypes.DAYTIMEDURATION);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "P200D", RDFTypes.DAYTIMEDURATION);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.BYTE);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.BYTE);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.SHORT);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.SHORT);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.INT);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.INT);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.LONG);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.LONG);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.UNSIGNEDBYTE);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.UNSIGNEDBYTE);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.UNSIGNEDSHORT);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.UNSIGNEDSHORT);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.UNSIGNEDINT);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.UNSIGNEDINT);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.UNSIGNEDLONG);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.UNSIGNEDLONG);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.POSITIVEINTEGER);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.POSITIVEINTEGER);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.NONNEGATIVEINTEGER);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.NONNEGATIVEINTEGER);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "-1", RDFTypes.NEGATIVEINTEGER);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "-2", RDFTypes.NEGATIVEINTEGER);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "-1", RDFTypes.NONPOSITIVEINTEGER);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "-2", RDFTypes.NONPOSITIVEINTEGER);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "010203", RDFTypes.HEXBINARY);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "020202", RDFTypes.HEXBINARY);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "AQID", RDFTypes.BASE64BINARY);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "AQIE", RDFTypes.BASE64BINARY);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.ANYURI);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.ANYURI);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "en", RDFTypes.LANGUAGE);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "es", RDFTypes.LANGUAGE);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.NORMALIZEDSTRING);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.NORMALIZEDSTRING);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.TOKEN);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.TOKEN);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "1", RDFTypes.NMTOKEN);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "2", RDFTypes.NMTOKEN);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "A", RDFTypes.NAME);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "B", RDFTypes.NAME);
    assertFalse(smgr.executeAsk(askQuery));
    bindings.clear();

    askQuery.withBinding("o", "A", RDFTypes.NCNAME);
    assertTrue(smgr.executeAsk(askQuery));
    bindings.clear();
    askQuery.withBinding("o", "B", RDFTypes.NCNAME);
    assertFalse(smgr.executeAsk(askQuery));
  }

  @Test
  public void testBaseUri() {
    // verify base has expected effect
    String relativeConstruct = "CONSTRUCT { <relative1> <relative2> <relative3> } \n" +
      "WHERE { ?s ?p ?o . } LIMIT 1";
    SPARQLQueryDefinition qdef = smgr.newQueryDefinition(relativeConstruct);
    qdef.setBaseUri("http://marklogic.com/SPARQLQDefTest/");
    JsonNode rdf = smgr.executeConstruct(qdef, new JacksonHandle()).get();

    String subject = rdf.fieldNames().next();
    assertEquals(
      "http://marklogic.com/SPARQLQDefTest/relative1", subject);

    String predicate = rdf.get(subject).fieldNames().next();
    assertEquals(
      "http://marklogic.com/SPARQLQDefTest/relative2", predicate);

    JsonNode objects = rdf.get(subject).get(predicate);
    assertEquals(1, objects.size());
    assertEquals(
      "http://marklogic.com/SPARQLQDefTest/relative3", objects.path(0).path("value").asText());
  }

  @Test
  public void testDefaultURI() {
    // verify default graph
    String defGraphQuery = "SELECT ?s WHERE { ?s a ?o }";
    SPARQLQueryDefinition qdef = smgr.newQueryDefinition(defGraphQuery);
    qdef.setIncludeDefaultRulesets(false);
    qdef.setOptimizeLevel(1);
    // this only tests whether optimize level is set, not sent to server
    // I'm not sure how to test that
    assertEquals(1, qdef.getOptimizeLevel());
    ArrayNode bindings = executeAndExtractBindings(qdef);

    qdef.setDefaultGraphUris("http://marklogic.com/SPARQLQDefTest/g4");
    bindings = executeAndExtractBindings(qdef);
    assertEquals( 1, bindings.size());

    qdef.setDefaultGraphUris("http://marklogic.com/SPARQLQDefTest/g4",
      "http://marklogic.com/SPARQLQDefTest/g2");
    bindings = executeAndExtractBindings(qdef);
    assertEquals( 2,
      bindings.size());
  }

  @Test
  public void testNamedGraphUris() {
    SPARQLQueryDefinition qdef = smgr
      .newQueryDefinition("SELECT ?s where { GRAPH ?g { ?s a ?o } }");
    qdef.setIncludeDefaultRulesets(false);
    qdef.setNamedGraphUris("http://marklogic.com/SPARQLQDefTest/g3");
    ArrayNode bindings = executeAndExtractBindings(qdef);
    assertEquals( 0, bindings.size());

    qdef.setNamedGraphUris("http://marklogic.com/SPARQLQDefTest/g4");
    bindings = executeAndExtractBindings(qdef);
    assertEquals( 1, bindings.size());

    qdef.setNamedGraphUris("http://marklogic.com/SPARQLQDefTest/g4", "http://marklogic.com/SPARQLQDefTest/g2");
    bindings = executeAndExtractBindings(qdef);
    assertEquals( 2, bindings.size());
  }

  @Test
  public void testUsingURI() {
    // verify default graph
    String defGraphQuery = "INSERT { GRAPH <http://marklogic.com/SPARQLQDefTest/g3> " +
      "{ <http://marklogic.com/SPARQLQDefTest/r3> " +
        "<http://marklogic.com/SPARQLQDefTest/p3> " +
        "<http://marklogic.com/SPARQLQDefTest/o3> } } " +
      "WHERE { <http://marklogic.com/SPARQLQDefTest/r1> <http://marklogic.com/SPARQLQDefTest/p3> ?o }";
    String defCheckQuery =
      "ASK WHERE { <http://marklogic.com/SPARQLQDefTest/r3> <http://marklogic.com/SPARQLQDefTest/p3> <http://marklogic.com/SPARQLQDefTest/o3> }";
    SPARQLQueryDefinition qdef = smgr.newQueryDefinition(defGraphQuery);
    qdef.setUsingGraphUris("http://marklogic.com/SPARQLQDefTest/g1");
    smgr.executeUpdate(qdef);
    SPARQLQueryDefinition checkDef = smgr.newQueryDefinition(defCheckQuery);
    checkDef.setDefaultGraphUris("http://marklogic.com/SPARQLQDefTest/g3");
    assertTrue(smgr.executeAsk(checkDef));

    // clean up
    smgr.executeUpdate(smgr.newQueryDefinition("DROP GRAPH <http://marklogic.com/SPARQLQDefTest/g3>"));
    assertFalse(smgr.executeAsk(checkDef));
  }

  @Test
  public void testUsingNamedURI() {
    // verify default graph
    String defGraphQuery = "INSERT { GRAPH <http://marklogic.com/SPARQLQDefTest/g65> " +
      "{ <http://marklogic.com/SPARQLQDefTest/r3> " +
        "<http://marklogic.com/SPARQLQDefTest/p3> " +
        "<http://marklogic.com/SPARQLQDefTest/o3> } } " +
      "WHERE { GRAPH ?g { <http://marklogic.com/SPARQLQDefTest/r1> <http://marklogic.com/SPARQLQDefTest/p3> ?o } }";
    String checkQuery =
      "ASK WHERE { <http://marklogic.com/SPARQLQDefTest/r3> <http://marklogic.com/SPARQLQDefTest/p3> <http://marklogic.com/SPARQLQDefTest/o3> }";
    SPARQLQueryDefinition qdef = smgr.newQueryDefinition(defGraphQuery);

    // negative, no insert
    qdef.setUsingNamedGraphUris("http://marklogic.com/SPARQLQDefTest/baloney");
    smgr.executeUpdate(qdef);

    SPARQLQueryDefinition checkDef = smgr.newQueryDefinition(checkQuery);
    checkDef.setDefaultGraphUris("http://marklogic.com/SPARQLQDefTest/g65");
    assertFalse(smgr.executeAsk(checkDef));

    // positive
    qdef.setUsingNamedGraphUris("http://marklogic.com/SPARQLQDefTest/g1");
    smgr.executeUpdate(qdef);

    checkDef.setDefaultGraphUris("http://marklogic.com/SPARQLQDefTest/g65");
    assertTrue(smgr.executeAsk(checkDef));
  }

  @Test
  public void testIncludeDefaultInference() {
    // install default inference.
    // TODO installDefaultInference("rdfs.rules");
    // check query with and without
    // uninstall default inference.
    SPARQLQueryDefinition qdef = smgr
      .newQueryDefinition("select ?o where {?s a ?o . filter (?s = <http://marklogic.com/SPARQLQDefTest/r4> )}");
    qdef.setIncludeDefaultRulesets(false);

    assertFalse(qdef.getIncludeDefaultRulesets());
    JacksonHandle handle = smgr.executeSelect(qdef, new JacksonHandle());
    JsonNode results = handle.get();
    assertEquals( 1,
      results.get("results").get("bindings").size());

    qdef.setIncludeDefaultRulesets(true);
    handle = smgr.executeSelect(qdef, new JacksonHandle());
    results = handle.get();
    assertEquals( 3,
      results.get("results").get("bindings").size());

    qdef = smgr
      .newQueryDefinition(
        "select ?o where {?s a ?o . filter (?s = <http://marklogic.com/SPARQLQDefTest/r4> )}")
      .withIncludeDefaultRulesets(false);

    // TODO removeDefaultInference();
  }
}
