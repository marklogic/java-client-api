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
            + ":g1 { :r1 :p3 \"1\"^^xsd:int .  } "
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
