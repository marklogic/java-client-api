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
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.semantics.Capability;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFMimeTypes;
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
    private static String expectedFirstResult =
            "{s:{value:'http://example.org/s1', type:'uri'}," +
            "p:{value:'http://example.org/p1', type:'uri'}," +
            "o:{value:'http://example.org/o1', type:'uri'}}";
    private static ObjectMapper mapper = new ObjectMapper()
        .configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        .configure(Feature.ALLOW_SINGLE_QUOTES, true);
    private static SPARQLQueryManager smgr;
    private static GraphManager gmgr;

    @BeforeClass
    public static void beforeClass() {
        Common.connect();
        //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
        gmgr = Common.client.newGraphManager();
        String nTriples = triple1 + "\n" + triple2;
        gmgr.write(graphUri, new StringHandle(nTriples).withMimetype("application/n-triples"));
        smgr = Common.client.newSPARQLQueryManager();
    }

    @AfterClass
    public static void afterClass() {
        gmgr.delete(graphUri);
        Common.release();
    }

    @Test
    public void testSPARQLWithLimit() throws Exception {
        SPARQLQueryDefinition qdef1 = smgr.newQueryDefinition("select ?s ?p ?o { ?s ?p ?o } limit 1");
        qdef1.setIncludeDefaultRulesets(false);
        qdef1.setCollections(graphUri);
        JsonNode jsonResults = smgr.executeSelect(qdef1, new JacksonHandle()).get();
        int numResults = jsonResults.path("results").path("bindings").size();
        // because we said 'limit 1' we should only get one result
        assertEquals(1, numResults);
        JsonNode firstResult = jsonResults.path("results").path("bindings").path(0);
        assertEquals(mapper.readTree(expectedFirstResult), firstResult);
    }

    @Test
    public void testSPARQLWithTwoResults() throws Exception {
        SPARQLQueryDefinition qdef2 = smgr.newQueryDefinition("select ?s ?p ?o { ?s ?p ?o } limit 100");
        qdef2.setIncludeDefaultRulesets(false);
        qdef2.setCollections(graphUri);
        JsonNode jsonResults = smgr.executeSelect(qdef2, new JacksonHandle()).get();
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
    }

    @Test
    public void testSPARQLWithBindings() throws Exception {
        SPARQLQueryDefinition qdef3 = smgr.newQueryDefinition("construct { ?s ?p ?o } where  { <subjectExample0> ?p ?o } ");
        qdef3.setIncludeDefaultRulesets(false);
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
    }

    @Test
    public void testPagination() throws Exception {
        SPARQLQueryDefinition qdef1 = smgr.newQueryDefinition(
            "SELECT ?s ?p ?o FROM <" + graphUri + "> { ?s ?p ?o }");
        qdef1.setIncludeDefaultRulesets(false);
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
        gmgr.write("/ontology", new StringHandle(ontology).withMimetype("application/n-triples"));
        SPARQLQueryDefinition qdef = smgr.newQueryDefinition(
                "SELECT ?s { ?s a <http://example.org/C1>  }");
        qdef.setIncludeDefaultRulesets(false);
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
    
    @Test
    public void testTransactions() {
        GraphManager graphManagerWriter = Common.client.newGraphManager();
        graphManagerWriter.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
        DatabaseClient readOnlyClient = DatabaseClientFactory.newClient(
                Common.HOST, Common.PORT, "rest-reader", "x",
                Authentication.DIGEST);
        SPARQLQueryManager sparqlManagerReader = readOnlyClient.newSPARQLQueryManager();
        String q1 = "INSERT DATA { GRAPH <newGraph> { <s1> <p1> <o1> . } }";
        String q2 = "INSERT DATA { GRAPH <newGraph> { <s2> <p2> <o2> . } }";
        String d1 = "DROP GRAPH <newGraph>";

        // write in a transaction
        Transaction tx = null;
        try {
            tx = Common.client.openTransaction();
            smgr.executeUpdate(smgr.newQueryDefinition(q1), tx);
            // reader can't see it outside transaction
			StringHandle handle = sparqlManagerReader
					.executeSelect(
							sparqlManagerReader
									.newQueryDefinition("select ?o where { <s1> <p1> ?o }"),
							new StringHandle());
			assertEquals("Empty result outside transaction", "{\"head\":{\"vars\":[]},\"results\":{\"bindings\":[]}}", handle.get());

			// and can inside (with writer user)
			handle = smgr
					.executeSelect(
							sparqlManagerReader
									.newQueryDefinition("select ?o where { <s1> <p1> ?o }"),
							new StringHandle(), tx);
			assertEquals("writer must see effects within transaction.", "{\"head\":{\"vars\":[\"o\"]},\"results\":{\"bindings\":[{\"o\":{\"type\":\"uri\",\"value\":\"o1\"}}]}}", handle.get());

            tx.rollback();
            tx = null;

            handle = sparqlManagerReader
					.executeSelect(
							sparqlManagerReader
									.newQueryDefinition("select ?o where { <s1> <p1> ?o }"),
							new StringHandle());
			assertEquals("Empty result after rollback", "{\"head\":{\"vars\":[]},\"results\":{\"bindings\":[]}}", handle.get());
			
            // new tx
            tx = Common.client.openTransaction();
            // write a graph in transaction
            smgr.executeUpdate(smgr.newQueryDefinition(q1), tx);
            smgr.executeUpdate(smgr.newQueryDefinition(q2), tx);
            
            tx.commit();
            tx = null;
            // graph is now there.  No failure.
            handle = sparqlManagerReader
					.executeSelect(
							sparqlManagerReader
									.newQueryDefinition("select ?o where { <s1> <p1> ?o }"),
							new StringHandle());
			assertEquals("update has been committed", "{\"head\":{\"vars\":[\"o\"]},\"results\":{\"bindings\":[{\"o\":{\"type\":\"uri\",\"value\":\"o1\"}}]}}", handle.get());

            // new transaction
            tx = Common.client.openTransaction();
            // ddrop a graph in transaction
            smgr.executeUpdate(smgr.newQueryDefinition(d1), tx);

            // must be gone, inside and outside transaction.
            handle = smgr
					.executeSelect(
							smgr.newQueryDefinition("select ?o where { <s1> <p1> ?o }"),
							new StringHandle(), tx);
			assertEquals("Empty result after delete, within tx", "{\"head\":{\"vars\":[]},\"results\":{\"bindings\":[]}}", handle.get());
			
			tx.commit();
            tx = null;

        } finally {
            if (tx != null) {
                tx.rollback();
                tx = null;
            }
            // always try to delete graph
            try {
                graphManagerWriter.delete(
                        "newGraph");
            } catch (Exception e) {
                // nop
            }

        }
    }
}
