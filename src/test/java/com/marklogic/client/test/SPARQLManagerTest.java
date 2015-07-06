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
import com.marklogic.client.semantics.SPARQLTuple;
import com.marklogic.client.semantics.SPARQLTupleResults;

public class SPARQLManagerTest {
    private static String graphUri = "http://example.org/g1";
    private static String triple1 = "<http://example.org/s1> <http://example.org/p1> <http://example.org/o1>.";
    private static String triple2 = "<http://example.org/s2> <http://example.org/p2> <http://example.org/o2>.";
    private static ObjectMapper mapper = new ObjectMapper()
        .configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        .configure(Feature.ALLOW_SINGLE_QUOTES, true);


    @BeforeClass
    public static void beforeClass() {
        Common.connect();
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
        GraphManager gmgr = Common.client.newGraphManager();
        String nTriples = triple1 + "\n" + triple2;
        gmgr.write(graphUri, new StringHandle(nTriples).withMimetype("application/n-triples"));
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void testSPARQL() throws Exception {
        SPARQLQueryManager smgr = Common.client.newSPARQLQueryManager();
        SPARQLQueryDefinition qdef1 = smgr.newQueryDefinition("select ?s ?p ?o { ?s ?p ?o } limit 1");
        JsonNode jsonResults = smgr.executeQuery(qdef1, new JacksonHandle()).get();
        String expected =
            "{s:{value:'http://example.org/s1', type:'uri'}," +
            "p:{value:'http://example.org/p1', type:'uri'}," +
            "o:{value:'http://example.org/o1', type:'uri'}}";
        int numResults = jsonResults.path("results").path("bindings").size();
        // because we said 'limit 1' we should only get one result
        //assertEquals(1, numResults);
        JsonNode firstResult = jsonResults.path("results").path("bindings").path(0);
        assertEquals(mapper.readTree(expected), firstResult);

        SPARQLQueryDefinition qdef2 = smgr.newQueryDefinition("select ?s ?p ?o { ?s ?p ?o } limit 100");
        // here I'll test Jena or Sesame
        SPARQLTupleResults results = smgr.executeSelect(qdef2);
        String[] bindingNames = results.getBindingNames();
        int i=0;
        for ( SPARQLTuple tuple : results ) {
            i++;
            System.out.println("Result number " + i);
            for ( String bindingName: bindingNames ) {
                SPARQLBinding binding = tuple.get(bindingName);
                if ( "s".equals(bindingName) ) {
                    String expectedValue = i == 1 ? "http://example.org/s1" : "http://example.org/s2";
                    assertEquals(expectedValue, binding.getValue());
                }
                if ( "p".equals(bindingName) ) {
                    String expectedValue = i == 1 ? "http://example.org/p1" : "http://example.org/p2";
                    assertEquals(expectedValue, binding.getValue());
                }
                if ( "o".equals(bindingName) ) {
                    String expectedValue = i == 1 ? "http://example.org/o1" : "http://example.org/o2";
                    assertEquals(expectedValue, binding.getValue());
                }
                assertEquals("uri", binding.getType());
                assertEquals(null, binding.getLanguageTag());
            }
        };

        SPARQLQueryDefinition qdef3 = smgr.newQueryDefinition("construct { ?s ?p ?o } where  { <subjectExample0> ?p ?o } ");
        StringHandle results1 = smgr.executeConstruct(qdef3, new StringHandle());

        SPARQLQueryDefinition qdef4 = smgr.newQueryDefinition("select ?s ?p ?o { ?s ?p ?o . filter (?s = ?b) }");
        SPARQLBindings bindings = qdef4.getBindings();
        bindings.bind("b", "http://example.org/s1");
        qdef4.setBindings(bindings);

        // or use a builder
        qdef4 = qdef4.withBinding("c", "http://example.org/o2").withBinding("d", "http://example.org/o3");

        /*
        // to configure inference
        qdef4 = qdef4.withRuleset(SPARQLRuleset.RDFS_PLUS);
        // or a custom ruleset
        qdef4 = qdef4.withRuleset(SPARQLRuleset.ruleset("custom.rules"));
        */

        // use a start and page length, and no transaction
        SPARQLTupleResults results2 = smgr.executeSelect(qdef4, 1, 100, null);

        // To invoke an update
        SPARQLQueryDefinition qdef5 = smgr.newQueryDefinition("insert data { ... }");
        qdef5.setUpdatePermissions(smgr.permission("rest-reader", Capability.UPDATE));

        // or
        SPARQLQueryDefinition qdef6 = smgr.newQueryDefinition("insert data { ... }").withUpdatePermission("rest-reader", Capability.UPDATE);

        QueryDefinition structuredQuery = new StructuredQueryBuilder().term("test");

        SPARQLQueryDefinition qdef7 = smgr.newQueryDefinition("insert data { ... }").withUpdatePermission("rest-reader", Capability.UPDATE).withStructuredQuery(structuredQuery);

    }
}
