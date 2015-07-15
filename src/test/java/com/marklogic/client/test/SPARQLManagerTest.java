/*
 * Copyright 2012-2015 MarkLogic Corporation
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
import static org.junit.Assert.assertNotEquals;

import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.semantics.Capability;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.SPARQLBinding;
import com.marklogic.client.semantics.SPARQLBindings;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;
import com.marklogic.client.semantics.SPARQLRuleset;

public class SPARQLManagerTest {
    private static String graphUri = "http://marklogic.com/java/SPARQLManagerTest";
    private static String triple1 = "<http://example.org/s1> <http://example.org/p1> <http://example.org/o1>.";
    private static String triple2 = "<http://example.org/s2> <http://example.org/p2> <http://example.org/o2>.";
    private static String ontology = 
            "<http://example.org/p1> <http://www.w3.org/2000/01/rdf-schema#range> <http://example.org/C1> . \n" +
            "<http://example.org/p2> <http://www.w3.org/2000/01/rdf-schema#domain> <http://example.org/C1> .";

    ;
    
    private static ObjectMapper mapper = new ObjectMapper()
        .configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        .configure(Feature.ALLOW_SINGLE_QUOTES, true);
    private static SPARQLQueryManager smgr;
    private static GraphManager gmgr;
    

    @BeforeClass
    public static void beforeClass() {
        Common.connect();
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
        gmgr = Common.client.newGraphManager();
        gmgr.deleteGraphs();
        String nTriples = triple1 + "\n" + triple2;
        gmgr.write(graphUri, new StringHandle(nTriples).withMimetype("application/n-triples"));
        smgr = Common.client.newSPARQLQueryManager();
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void testSPARQL() throws Exception {
        SPARQLQueryDefinition qdef1 = smgr.newQueryDefinition("select ?s ?p ?o { ?s ?p ?o } limit 1");
        JsonNode jsonResults = smgr.executeSelect(qdef1, new JacksonHandle()).get();
        String expectedFirstResult =
            "{s:{value:'http://example.org/s1', type:'uri'}," +
            "p:{value:'http://example.org/p1', type:'uri'}," +
            "o:{value:'http://example.org/o1', type:'uri'}}";
        int numResults = jsonResults.path("results").path("bindings").size();
        // because we said 'limit 1' we should only get one result
        assertEquals(1, numResults);
        JsonNode firstResult = jsonResults.path("results").path("bindings").path(0);
        assertEquals(mapper.readTree(expectedFirstResult), firstResult);

        SPARQLQueryDefinition qdef2 = smgr.newQueryDefinition("select ?s ?p ?o { ?s ?p ?o } limit 100");
        jsonResults = smgr.executeSelect(qdef2, new JacksonHandle()).get();
        JsonNode tuples = jsonResults.path("results").path("bindings");
        // loop through the "bindings" array (we would call each row a tuple)
        for ( int i=0; i < tuples.size(); i++ ) {
            JsonNode tuple = tuples.get(i);
            // loop through the fields or columns for each row
            Iterator<String> fieldNames = tuple.fieldNames();
            while ( fieldNames.hasNext() ) {
                String bindingName = fieldNames.next();
                JsonNode binding = tuple.get(bindingName);
                if ( "s".equals(bindingName) ) {
                    String expectedValue = (i == 0) ? "http://example.org/s1" : "http://example.org/s2";
                    assertEquals(expectedValue, binding.get("value").asText());
                }
                if ( "p".equals(bindingName) ) {
                    String expectedValue = (i == 0) ? "http://example.org/p1" : "http://example.org/p2";
                    assertEquals(expectedValue, binding.get("value").asText());
                }
                if ( "o".equals(bindingName) ) {
                    String expectedValue = (i == 0) ? "http://example.org/o1" : "http://example.org/o2";
                    assertEquals(expectedValue, binding.get("value").asText());
                }
            }
        }

        SPARQLQueryDefinition qdef3 = smgr.newQueryDefinition("construct { ?s ?p ?o } where  { <subjectExample0> ?p ?o } ");
        StringHandle results1 = smgr.executeConstruct(qdef3, new StringHandle());

        SPARQLQueryDefinition qdef4 = smgr.newQueryDefinition("select ?s ?p ?o { ?s ?p ?o . filter (?s = ?b) }");
        SPARQLBindings bindings = qdef4.getBindings();
        bindings.bind("b", "http://example.org/s1");
        qdef4.setBindings(bindings);

        // or use a builder
        qdef4 = qdef4.withBinding("c", "http://example.org/o2").withBinding("d", "http://example.org/o3");

        JsonNode jsonResults2 = smgr.executeSelect(qdef4, new JacksonHandle()).get();

        int numResults2 = jsonResults2.path("results").path("bindings").size();
        // because we said 'filter (?s = ?b)' we should only get one result
        assertEquals(1, numResults2);
        JsonNode firstResult2 = jsonResults2.path("results").path("bindings").path(0);
        assertEquals(mapper.readTree(expectedFirstResult), firstResult2);

        /*
        // to configure inference
        qdef4 = qdef4.withRuleset(SPARQLRuleset.RDFS_PLUS);
        // or a custom ruleset
        qdef4 = qdef4.withRuleset(SPARQLRuleset.ruleset("custom.rules"));
        // use a start and page length, and no transaction
        JsonNode results2 = smgr.executeSelect(qdef4, 1, 100, null);
        */

        // To invoke an update
        SPARQLQueryDefinition qdef5 = smgr.newQueryDefinition("insert data { ... }");
        qdef5.setUpdatePermissions(smgr.permission("rest-reader", Capability.UPDATE));

        // or
        SPARQLQueryDefinition qdef6 = smgr.newQueryDefinition("insert data { ... }").withUpdatePermission("rest-reader", Capability.UPDATE);

        QueryDefinition structuredQuery = new StructuredQueryBuilder().term("test");

        SPARQLQueryDefinition qdef7 = smgr.newQueryDefinition("insert data { ... }").withUpdatePermission("rest-reader", Capability.UPDATE).withStructuredQuery(structuredQuery);

    }

    @Test
    public void testPagination() throws Exception {
        SPARQLQueryDefinition qdef1 = smgr.newQueryDefinition(
            "SELECT ?s ?p ?o FROM <" + graphUri + "> { ?s ?p ?o }");
        long start = 1;
        long pageLength = 1;
        JsonNode results = smgr.executeSelect(qdef1, new JacksonHandle(), start, pageLength).get();
        JsonNode bindings = results.path("results").path("bindings");
        // because we set pageLength to 1 we should only get one result
        assertEquals(pageLength, bindings.size());
        String uri1 = bindings.get(0).get("s").get("value").asText();

        pageLength = 2;
        results = smgr.executeSelect(qdef1, new JacksonHandle(), start, pageLength).get();
        assertEquals(pageLength, results.path("results").path("bindings").size());

        start = 2;
        pageLength = 2;
        results = smgr.executeSelect(qdef1, new JacksonHandle(), start, pageLength).get();
        bindings = results.path("results").path("bindings");
        // because we skipped the first result (by setting start=2) there are not enough 
        // results for a full page, so size() only returns 1
        assertEquals(1, bindings.size());
        String uri2 = bindings.get(0).get("s").get("value").asText();
        assertNotEquals(uri1, uri2);
    }
    
    @Test
    public void testInference() throws Exception {
        //private static String triple1 = "<http://example.org/s1> <http://example.org/p1> <http://example.org/o1>.";
        //private static String triple2 = "<http://example.org/s2> <http://example.org/p2> <http://example.org/o2>.";
        // private static String ontology = "<http://example.org/C1> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://example.org/C2> . <http://example.org/s1> <http://www.w3.org/2000/01/rdf-schema#range> <http://example.org/C1> .";
        gmgr.write("/ontology", new StringHandle(ontology).withMimetype("application/n-triples"));
        SPARQLQueryDefinition qdef = smgr.newQueryDefinition(
                "SELECT ?s { ?s a <http://example.org/C1>  }");
        JsonNode results = smgr.executeSelect(qdef, new JacksonHandle()).get();
        assertEquals(0, results.path("results").path("bindings").size());
        qdef.setRulesets(SPARQLRuleset.RANGE);
        results = smgr.executeSelect(qdef, new JacksonHandle()).get();
        assertEquals(1, results.path("results").path("bindings").size());
        
        qdef.setRulesets(SPARQLRuleset.RANGE, SPARQLRuleset.DOMAIN);
        results = smgr.executeSelect(qdef, new JacksonHandle()).get();
        assertEquals(2, results.path("results").path("bindings").size());
        
        gmgr.delete("/ontology");
    }
    
}
