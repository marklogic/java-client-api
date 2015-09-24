package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFTypes;
import com.marklogic.client.semantics.SPARQLBindings;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;

public class SPARQLQueryDefinitionTest {
    
    private static SPARQLQueryManager smgr;
    private static GraphManager gmgr;

    private static String TEST_TRIG = "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> . "
            + "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> . "
            + "@prefix : <http://example.org/> . "
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

    @BeforeClass
    public static void beforeClass() {
        Common.connect();
        //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
        gmgr = Common.client.newGraphManager();
        gmgr.replaceGraphs(new StringHandle(TEST_TRIG)
                .withMimetype("text/trig"));
        smgr = Common.client.newSPARQLQueryManager();
    }

    @AfterClass
    public static void afterClass() {
        Common.connect();
        gmgr = Common.client.newGraphManager();
        gmgr.deleteGraphs();
    }

    private ArrayNode executeAndExtractBindings(SPARQLQueryDefinition qdef) {
        JacksonHandle handle = smgr.executeSelect(qdef, new JacksonHandle());
        JsonNode results = handle.get();

        ArrayNode bindings = (ArrayNode) results.findPath("results").findPath(
                "bindings");
        return bindings;
    }

    @Test
    public void testBindings() {
        String ask = "ASK FROM <http://example.org/g1> " +
            "WHERE { <http://example.org/r1> <http://example.org/p3> ?o }";
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
        qdef.setBaseUri("http://example.org/test/");
        JsonNode rdf = smgr.executeConstruct(qdef, new JacksonHandle()).get();

        String subject = rdf.fieldNames().next();
        assertEquals("base uri plus relative subject uri",
            "http://example.org/test/relative1", subject);

        String predicate = rdf.get(subject).fieldNames().next();
        assertEquals("base uri plus relative predicate uri",
            "http://example.org/test/relative2", predicate);

        JsonNode objects = rdf.get(subject).get(predicate);
        assertEquals(1, objects.size());
        assertEquals("base uri plus relative uri",
            "http://example.org/test/relative3", objects.path(0).path("value").asText());
    }

    @Test
    public void testDefaultURI() {
        // verify default graph
        String defGraphQuery = "SELECT ?s WHERE { ?s a ?o }";
        SPARQLQueryDefinition qdef = smgr.newQueryDefinition(defGraphQuery);
        qdef.setIncludeDefaultRulesets(false);
        qdef.setOptimzeLevel(1);
        // this only tests whether optimize level is set, not sent to server
        // I'm not sure how to test that
        assertEquals(1, qdef.getOptimizeLevel());
        ArrayNode bindings = executeAndExtractBindings(qdef);
        assertEquals("Union of all graphs has three class assertions", 3,
                bindings.size());

        qdef.setDefaultGraphUris("http://example.org/g4");
        bindings = executeAndExtractBindings(qdef);
        assertEquals("Single graphs has one assertion", 1, bindings.size());

        qdef.setDefaultGraphUris("http://example.org/g4",
                "http://example.org/g2");
        bindings = executeAndExtractBindings(qdef);
        assertEquals("Union two default graphs has two assertions", 2,
                bindings.size());
    }

    @Test
    public void testNamedGraphUris() {
        SPARQLQueryDefinition qdef = smgr
                .newQueryDefinition("SELECT ?s where { GRAPH ?g { ?s a ?o } }");
        qdef.setIncludeDefaultRulesets(false);
        qdef.setNamedGraphUris("http://example.org/g3");
        ArrayNode bindings = executeAndExtractBindings(qdef);
        assertEquals("From named 0 result assertions", 0, bindings.size());

        qdef.setNamedGraphUris("http://example.org/g4");
        bindings = executeAndExtractBindings(qdef);
        assertEquals("From named 1 result assertions", 1, bindings.size());

        qdef.setNamedGraphUris("http://example.org/g4", "http://example.org/g2");
        bindings = executeAndExtractBindings(qdef);
        assertEquals("From named 1 result assertions", 2, bindings.size());
    }

    @Test
    public void testUsingURI() {
        // verify default graph
        String defGraphQuery = "INSERT { GRAPH <http://example.org/g3> { <http://example.org/r3> <http://example.org/p3> <http://example.org/o3> } } WHERE { <http://example.org/r1> <http://example.org/p3> ?o }";
        String defCheckQuery = "ASK WHERE { <http://example.org/r3> <http://example.org/p3> <http://example.org/o3> }";
        SPARQLQueryDefinition qdef = smgr.newQueryDefinition(defGraphQuery);
        qdef.setUsingGraphUris("http://example.org/g1");
        smgr.executeUpdate(qdef);
        SPARQLQueryDefinition checkDef = smgr.newQueryDefinition(defCheckQuery);
        checkDef.setDefaultGraphUris("http://example.org/g3");
        assertTrue(smgr.executeAsk(checkDef));

        // clean up 
        smgr.executeUpdate(smgr.newQueryDefinition("DROP GRAPH <http://example.org/g3>"));
        assertFalse(smgr.executeAsk(checkDef));
    }

    @Test
    public void testUsingNamedURI() {
        // verify default graph
        String defGraphQuery = "INSERT { GRAPH <http://example.org/g65> { <http://example.org/r3> <http://example.org/p3> <http://example.org/o3> } } WHERE { GRAPH ?g { <http://example.org/r1> <http://example.org/p3> ?o } }";
        String checkQuery = "ASK WHERE { <http://example.org/r3> <http://example.org/p3> <http://example.org/o3> }";
        SPARQLQueryDefinition qdef = smgr.newQueryDefinition(defGraphQuery);
        
        // negative, no insert
        qdef.setUsingNamedGraphUris("http://example.org/baloney");
        smgr.executeUpdate(qdef);

        SPARQLQueryDefinition checkDef = smgr.newQueryDefinition(checkQuery);
        checkDef.setDefaultGraphUris("http://example.org/g65");
        assertFalse(smgr.executeAsk(checkDef));
        
        // positive
        qdef.setUsingNamedGraphUris("http://example.org/g1");
        smgr.executeUpdate(qdef);

        checkDef.setDefaultGraphUris("http://example.org/g65");
        assertTrue(smgr.executeAsk(checkDef));
    }

    @Test
    public void testIncludeDefaultInference() {
        // install default inference.
        // TODO installDefaultInference("rdfs.rules");
        // check query with and without
        // uninstall default inference.
        SPARQLQueryDefinition qdef = smgr
                .newQueryDefinition("select ?o where {?s a ?o . filter (?s = <http://example.org/r4> )}");
        qdef.setIncludeDefaultRulesets(false);

        assertFalse(qdef.getIncludeDefaultRulesets());
        JacksonHandle handle = smgr.executeSelect(qdef, new JacksonHandle());
        JsonNode results = handle.get();
        assertEquals("Size of results with no inference", 1,
                results.get("results").get("bindings").size());

        qdef.setIncludeDefaultRulesets(true);
        handle = smgr.executeSelect(qdef, new JacksonHandle());
        results = handle.get();
        assertEquals("Size of results with default inference", 3,
                results.get("results").get("bindings").size());

        qdef = smgr
                .newQueryDefinition(
                        "select ?o where {?s a ?o . filter (?s = <http://example.org/r4> )}")
                .withIncludeDefaultRulesets(false);

        // TODO removeDefaultInference();
    }
}
