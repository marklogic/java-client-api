/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.*;
import com.marklogic.client.query.*;
import com.marklogic.client.semantics.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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

  @BeforeAll
  public static void beforeClass() {
    Common.connect();
    gmgr = Common.client.newGraphManager();
    String nTriples = triple1 + "\n" + triple2;
    gmgr.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
    gmgr.write(graphUri, new StringHandle(nTriples));
    smgr = Common.client.newSPARQLQueryManager();
  }

  @AfterAll
  public static void afterClass() {
    gmgr.delete(graphUri);
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
      String s = tuple.path("s").path("value").asText();
      String p = tuple.path("p").path("value").asText();
      String o = tuple.path("o").path("value").asText();
      if ( "http://example.org/s1".equals(s) ) {
        assertEquals("http://example.org/p1", p);
        assertEquals("http://example.org/o1", o);
      } else if ( "http://example.org/s2".equals(s) ) {
        assertEquals("http://example.org/p2", p);
        assertEquals("http://example.org/o2", o);
      } else {
        fail("Unexpected value for s:[" + s + "]");
      }
    }
  }

  @Test
  public void testDescribe() {
    // verify base has expected effect
    String relativeConstruct = "DESCRIBE <http://example.org/s1>";
    SPARQLQueryDefinition qdef = smgr.newQueryDefinition(relativeConstruct);
    Document rdf = smgr.executeConstruct(qdef, new DOMHandle()).get();

    Node description = rdf.getFirstChild().getFirstChild();
    assertNotNull(description.getAttributes());
    assertEquals("http://example.org/s1", description.getAttributes().item(0).getTextContent());
    assertNotNull(description.getFirstChild());
    assertEquals("p1", description.getFirstChild().getNodeName());
    assertEquals("http://example.org/", description.getFirstChild().getNamespaceURI());
    NamedNodeMap attrs = description.getFirstChild().getAttributes();
    assertNotNull(attrs);
    assertNotNull(attrs.getNamedItemNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource"));
    assertEquals("http://example.org/o1", attrs.getNamedItemNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource").getTextContent());
  }

  @Test
  public void issue_357() {
    // open transaction
    Transaction transaction = Common.client.openTransaction();
    try {

      // insert some data under transaction
      String insertString="BASE <http://example.org/addressbook>\n " +
        "INSERT DATA { <http://example.org/id#3333> <#email> \"jonny@ramone.com\"}";
      SPARQLQueryDefinition insertDef = smgr.newQueryDefinition(insertString);
      smgr.executeUpdate(insertDef, transaction);

      // ask for it w/ transaction
      String queryString =
        "ASK WHERE { <http://example.org/id#3333> <http://example.org/addressbook#email> ?o }";
      SPARQLQueryDefinition booleanDef = smgr.newQueryDefinition(queryString);
      assertTrue(smgr.executeAsk(booleanDef,transaction));

    } finally {
      transaction.rollback();
    }
  }

  @Test
  public void testConstrainingQueries() throws Exception {
    // insert two triples for the tests below
    String localGraphUri = "SPARQLManagerTest.testConstrainingQueries";

    // the first triple is a managed triple so GraphManager can write it
    String triple1 = "<http://example.org/s1> <http://example.org/p1> 'test1'.";
    gmgr.writeAs(localGraphUri, triple1);

    // the second triple is an embeded triple so we need a DocumentManager to write it
    // we're using an embeded triple so we can have other fields on which to query
    String embededTriple =
      "<xml>" +
        "<test2>testValue</test2>" +
        "<sem:triples xmlns:sem='http://marklogic.com/semantics'>" +
          "<sem:triple>" +
            "<sem:subject>http://example.org/s2</sem:subject>" +
            "<sem:predicate>http://example.org/p2</sem:predicate>" +
            "<sem:object datatype='http://www.w3.org/2001/XMLSchema#string'>" +
              "test2</sem:object>" +
          "</sem:triple>" +
        "</sem:triples>" +
      "</xml>";
    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
    QueryManager queryMgr = Common.client.newQueryManager();
    docMgr.writeAs(localGraphUri + "/embededTriple.xml",
      new DocumentMetadataHandle().withCollections(localGraphUri),
      embededTriple);

    // test StringQueryDefinition
    SPARQLQueryDefinition qdef = smgr.newQueryDefinition("select ?s ?p ?o { ?s ?p ?o } limit 100");
    qdef.setIncludeDefaultRulesets(false);
    qdef.setCollections(localGraphUri);
    qdef.setConstrainingQueryDefinition(queryMgr.newStringDefinition().withCriteria("test1"));
    JsonNode jsonResults = smgr.executeSelect(qdef, new JacksonHandle()).get();
    JsonNode tuples = jsonResults.path("results").path("bindings");
    assertEquals(1, tuples.size());
    String value = tuples.path(0).path("o").path("value").asText();
    assertEquals("test1", value);

    // test StructuredQueryDefinition
    StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition sqdef =
      sqb.and(
        sqb.term("test2"),
        sqb.value(sqb.element("test2"), "testValue")
      );
    qdef.setConstrainingQueryDefinition(sqdef);
    jsonResults = smgr.executeSelect(qdef, new JacksonHandle()).get();
    tuples = jsonResults.path("results").path("bindings");
    assertEquals(1, tuples.size());
    value = tuples.path(0).path("o").path("value").asText();
    assertEquals("test2", value);

    // test XML RawStructuredQueryDefinition
    String rawXMLStructuredQuery =
      "<query>" +
        "<term-query><text>test1</text></term-query>" +
      "</query>";
    StringHandle handle = new StringHandle(rawXMLStructuredQuery).withFormat(Format.XML);
    RawStructuredQueryDefinition rawStructuredQDef = queryMgr.newRawStructuredQueryDefinition(handle);
    qdef.setConstrainingQueryDefinition(rawStructuredQDef);
    jsonResults = smgr.executeSelect(qdef, new JacksonHandle()).get();
    tuples = jsonResults.path("results").path("bindings");
    assertEquals(1, tuples.size());
    value = tuples.path(0).path("o").path("value").asText();
    assertEquals("test1", value);

    // test JSON RawStructuredQueryDefinition
    String rawJSONStructuredQuery =
      "{ query:{" +
        "term-query:{text:'test2'}" +
      "}}";
    handle = new StringHandle(rawJSONStructuredQuery).withFormat(Format.JSON);
    rawStructuredQDef = queryMgr.newRawStructuredQueryDefinition(handle);
    qdef.setConstrainingQueryDefinition(rawStructuredQDef);
    jsonResults = smgr.executeSelect(qdef, new JacksonHandle()).get();
    tuples = jsonResults.path("results").path("bindings");
    assertEquals(1, tuples.size());
    value = tuples.path(0).path("o").path("value").asText();
    assertEquals("test2", value);

    // test RawCombinedQueryDefinition
    String rawCombinedQuery =
      "<search xmlns='http://marklogic.com/appservices/search'>" +
        "<sparql>select ?s ?p ?o { ?s ?p ?o } limit 50</sparql>" +
        "<options>" +
          "<constraint name='test2'>" +
            "<value type='string'><element ns='' name='test2'/></value>" +
          "</constraint>" +
        "</options>" +
        "<query>" +
          "<and-query>" +
            "<term-query><text>test2</text></term-query>" +
            "<value-constraint-query>" +
              "<constraint-name>test2</constraint-name>" +
              "<text>testValue</text>" +
            "</value-constraint-query>" +
          "</and-query>" +
        "</query>" +
      "</search>";
    handle = new StringHandle(rawCombinedQuery).withFormat(Format.XML);
    RawCombinedQueryDefinition rawCombinedQDef = queryMgr.newRawCombinedQueryDefinition(handle);
    qdef.setConstrainingQueryDefinition(rawCombinedQDef);
    jsonResults = smgr.executeSelect(qdef, new JacksonHandle()).get();
    tuples = jsonResults.path("results").path("bindings");
    assertEquals(1, tuples.size());
    value = tuples.path(0).path("o").path("value").asText();
    assertEquals("test2", value);

    // this one has no <sparql>, so we'll insert it.
    String rawCombinedQuery2 =
        "<search xmlns='http://marklogic.com/appservices/search'>" +
          "<options>" +
            "<constraint name='test2'>" +
              "<value type='string'><element ns='' name='test2'/></value>" +
            "</constraint>" +
          "</options>" +
          "<query>" +
            "<and-query>" +
              "<term-query><text>test2</text></term-query>" +
              "<value-constraint-query>" +
                "<constraint-name>test2</constraint-name>" +
                "<text>testValue</text>" +
              "</value-constraint-query>" +
            "</and-query>" +
          "</query>" +
        "</search>";
    handle = new StringHandle(rawCombinedQuery2).withFormat(Format.XML);
    RawCombinedQueryDefinition rawCombinedQDef2 = queryMgr.newRawCombinedQueryDefinition(handle);
    qdef.setConstrainingQueryDefinition(rawCombinedQDef2);
    jsonResults = smgr.executeSelect(qdef, new JacksonHandle()).get();
    tuples = jsonResults.path("results").path("bindings");
    assertEquals(1, tuples.size());
    value = tuples.path(0).path("o").path("value").asText();
    assertEquals("test2", value);


    String rawCombinedJson =
        "{\"search\" : " +
          "{\"sparql\":\"select ?s ?p ?o { ?s ?p ?o } limit 100\"," +
          "\"qtext\":\"testValue\"}}";
    handle = new StringHandle(rawCombinedJson).withFormat(Format.JSON);
    RawCombinedQueryDefinition rawCombinedJsonDef = queryMgr.newRawCombinedQueryDefinition(handle);
    qdef.setConstrainingQueryDefinition(rawCombinedJsonDef);
    jsonResults = smgr.executeSelect(qdef, new JacksonHandle()).get();
    tuples = jsonResults.path("results").path("bindings");
    assertEquals(1, tuples.size());
    value = tuples.path(0).path("o").path("value").asText();
    assertEquals("test2", value);

    rawCombinedJson =
      "{\"search\" : " +
        "{\"options\" : " +
          "{\"constraint\": " +
            "{\"name\":\"test2\", " +
            " \"value\": "+
            " { \"type\":\"string\", "+
            "   \"element\" : { \"ns\":\"\", \"name\":\"test2\" } } } }  " +
        "," +
        "\"query\" : " +
          "{\"and-query\" : " +
            "[{\"term-query\": {\"text\": \"test2\"}}," +
            " {\"value-constraint-query\" : "  +
              "{\"constraint-name\": \"test2\"," +
               "\"text\":\"testValue\"}" +
            "}]" +
          "}"+
        "}"+
      "}";
    handle = new StringHandle(rawCombinedJson).withFormat(Format.JSON);
    rawCombinedJsonDef = queryMgr.newRawCombinedQueryDefinition(handle);
    qdef.setConstrainingQueryDefinition(rawCombinedJsonDef);
    jsonResults = smgr.executeSelect(qdef, new JacksonHandle()).get();
    tuples = jsonResults.path("results").path("bindings");
    assertEquals(1, tuples.size());
    value = tuples.path(0).path("o").path("value").asText();
    assertEquals("test2", value);


    // clean up the data for this method
    docMgr.delete(localGraphUri + "/embededTriple.xml");
    gmgr.delete(localGraphUri);
  }

  @Test
  public void testSPARQLWithBindings() throws Exception {
    SPARQLQueryDefinition qdef3 = smgr.newQueryDefinition("construct { ?s ?p ?o } where  { <subjectExample0> ?p ?o } ");
    qdef3.setIncludeDefaultRulesets(false);
    qdef3.setCollections(graphUri);
    StringHandle results1 = smgr.executeConstruct(qdef3, new StringHandle());

    SPARQLQueryDefinition qdef4 = smgr.newQueryDefinition("select ?s ?p ?o { ?s ?p ?o . filter (?s = ?b) }");
    qdef4.setCollections(graphUri);
    SPARQLBindings bindings = qdef4.getBindings();
    bindings.bind("b", "http://example.org/s1");
    qdef4.setBindings(bindings);

    // or use a builder
    qdef4 = qdef4.withBinding("c", "http://example.org/o2").withBinding("d", "http://example.org/o3");

    DOMHandle handle = new DOMHandle();
    handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
    Document jsonResults2 = smgr.executeSelect(qdef4, handle).get();

    NodeList results = jsonResults2.getDocumentElement().getLastChild().getChildNodes();
    // the number of children of the element "bindings"
    int numResults2 = results.getLength();
    // because we said 'filter (?s = ?b)' we should only get one result
    assertEquals(1, numResults2);
    Node s = results.item(0).getChildNodes().item(0);
    Node o = results.item(0).getChildNodes().item(1);
    Node p = results.item(0).getChildNodes().item(2);
    assertEquals("http://example.org/s1", s.getTextContent());
    assertEquals("http://example.org/p1", o.getTextContent());
    assertEquals("http://example.org/o1", p.getTextContent());
  }

  @Test
  public void testPagination() {
	  if (Common.getMarkLogicVersion().getMajor() >= 12) {
		  // Disabled until MLE-12708 is fixed.
		  return;
	  }
    SPARQLQueryDefinition qdef1 = smgr.newQueryDefinition(
      "SELECT ?s ?p ?o FROM <" + graphUri + "> { ?s ?p ?o }");
    qdef1.setIncludeDefaultRulesets(false);
    qdef1.setCollections(graphUri);
    long start = 1;
    smgr.setPageLength(1);
    JacksonHandle handle = new JacksonHandle();
    handle.setMimetype(SPARQLMimeTypes.SPARQL_JSON);
    JsonNode results = smgr.executeSelect(qdef1, handle, start).get();
    JsonNode bindings = results.path("results").path("bindings");
    // because we set pageLength to 1 we should only get one result
    assertEquals(1, bindings.size());
    String uri1 = bindings.get(0).get("s").get("value").asText();

    smgr.setPageLength(2);
    results = smgr.executeSelect(qdef1, new JacksonHandle(), start).get();
    // because we set pageLength to 2 we should get two results
    assertEquals(2, results.path("results").path("bindings").size());

    start = 2;
    results = smgr.executeSelect(qdef1, new JacksonHandle(), start).get();
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
    StringHandle handle = new StringHandle().withMimetype(SPARQLMimeTypes.SPARQL_CSV);
    String results = smgr.executeSelect(qdef, handle).get();
    assertEquals("%0D%0A", URLEncoder.encode(results, "utf8"));

    qdef.setRulesets(SPARQLRuleset.RANGE);
    results = smgr.executeSelect(qdef, handle).get();
    assertEquals(1, countLines(parseCsv(results)));

    qdef.setRulesets(SPARQLRuleset.RANGE, SPARQLRuleset.DOMAIN);
    results = smgr.executeSelect(qdef, handle).get();
    MappingIterator<Map<String,String>> csvRows = parseCsv(results);
    assertTrue(csvRows.hasNext());
    Map<String,String> row = csvRows.next();
    assertEquals("http://example.org/o1", row.get("s"));
    assertTrue(csvRows.hasNext());
    row = csvRows.next();
    assertEquals("http://example.org/s2", row.get("s"));
    assertFalse(csvRows.hasNext());

    gmgr.delete("/ontology");
  }

  private MappingIterator<Map<String,String>> parseCsv(String csv) throws JsonProcessingException, IOException {
    return new CsvMapper().reader(Map.class)
      .with(CsvSchema.emptySchema().withHeader()) // use first row as header
      .readValues(csv);
  }

  private int countLines(MappingIterator<?> iter) {
    int numLines = 0;
    while (iter.hasNext()) {
      iter.next();
      numLines++;
    }
    return numLines;
  }

  @Test
  public void testTransactions() {
    GraphManager graphManagerWriter = Common.client.newGraphManager();
    graphManagerWriter.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
    DatabaseClient readOnlyClient = Common.connectReadOnly();
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
      assertEquals( "{\"head\":{\"vars\":[]},\"results\":{\"bindings\":[]}}", handle.get());

      // and can inside (with writer user)
      handle = smgr
        .executeSelect(
          sparqlManagerReader
            .newQueryDefinition("select ?o where { <s1> <p1> ?o }"),
          new StringHandle(), tx);
      assertEquals( "{\"head\":{\"vars\":[\"o\"]},\"results\":{\"bindings\":[{\"o\":{\"type\":\"uri\",\"value\":\"o1\"}}]}}", handle.get());

      tx.rollback();
      tx = null;

      handle = sparqlManagerReader
        .executeSelect(
          sparqlManagerReader
            .newQueryDefinition("select ?o where { <s1> <p1> ?o }"),
          new StringHandle());
      assertEquals( "{\"head\":{\"vars\":[]},\"results\":{\"bindings\":[]}}", handle.get());

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
      assertEquals( "{\"head\":{\"vars\":[\"o\"]},\"results\":{\"bindings\":[{\"o\":{\"type\":\"uri\",\"value\":\"o1\"}}]}}", handle.get());

      // new transaction
      tx = Common.client.openTransaction();
      // ddrop a graph in transaction
      smgr.executeUpdate(smgr.newQueryDefinition(d1), tx);

      // must be gone, inside and outside transaction.
      handle = smgr
        .executeSelect(
          smgr.newQueryDefinition("select ?o where { <s1> <p1> ?o }"),
          new StringHandle(), tx);
      assertEquals( "{\"head\":{\"vars\":[]},\"results\":{\"bindings\":[]}}", handle.get());

      tx.commit();
      tx = null;

    } finally {
      if (tx != null) {
        try { tx.rollback(); } catch (Exception e) {}
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
