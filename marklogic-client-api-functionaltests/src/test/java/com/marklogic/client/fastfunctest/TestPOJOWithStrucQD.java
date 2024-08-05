/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.functionaltest.Artifact;
import com.marklogic.client.functionaltest.ArtifactIndexedOnString;
import com.marklogic.client.functionaltest.Company;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoQueryDefinition;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.*;
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class TestPOJOWithStrucQD extends AbstractFunctionalTest {

  @BeforeEach
  public void setUp() throws Exception {
    client = getDatabaseClient("rest-admin", "x", getConnType());
  }

  public Artifact getArtifact(int counter) {

    Artifact cogs = new Artifact();
    cogs.setId(counter);
    if (counter % 5 == 0) {
      cogs.setName("Cogs special");
      if (counter % 2 == 0) {
        Company acme = new Company();
        acme.setName("Acme special, Inc.");
        acme.setWebsite("http://www.acme special.com");
        acme.setLatitude(41.998 + counter);
        acme.setLongitude(-87.966 + counter);
        cogs.setManufacturer(acme);

      } else {
        Company widgets = new Company();
        widgets.setName("Widgets counter Inc.");
        widgets.setWebsite("http://www.widgets counter.com");
        widgets.setLatitude(41.998 + counter);
        widgets.setLongitude(-87.966 + counter);
        cogs.setManufacturer(widgets);
      }
    } else {
      cogs.setName("Cogs " + counter);
      if (counter % 2 == 0) {
        Company acme = new Company();
        acme.setName("Acme " + counter + ", Inc.");
        acme.setWebsite("http://www.acme" + counter + ".com");
        acme.setLatitude(41.998 + counter);
        acme.setLongitude(-87.966 + counter);
        cogs.setManufacturer(acme);

      } else {
        Company widgets = new Company();
        widgets.setName("Widgets " + counter + ", Inc.");
        widgets.setWebsite("http://www.widgets" + counter + ".com");
        widgets.setLatitude(41.998 + counter);
        widgets.setLongitude(-87.966 + counter);
        cogs.setManufacturer(widgets);
      }
    }
    cogs.setInventory(1000 + counter);
    return cogs;
  }

  public void validateArtifact(Artifact art)
  {
    assertNotNull( art);
    assertNotNull( art.id);
    assertTrue( art.getInventory() > 1000);
  }

  public void loadSimplePojos(PojoRepository products)
  {
    for (int i = 1; i < 111; i++) {
      if (i % 2 == 0) {
        products.write(this.getArtifact(i), "even", "numbers");
      }
      else {
        products.write(this.getArtifact(i), "odd", "numbers");
      }
    }
  }

  public ArtifactIndexedOnString getArtifactIndexedOnString(int counter) {

    ArtifactIndexedOnString cogs = new ArtifactIndexedOnString();
    cogs.setId(counter);
    if (counter % 5 == 0) {
      cogs.setName("Cogs special");
      if (counter % 2 == 0) {
        Company acme = new Company();
        acme.setName("Acme special, Inc.");
        cogs.setManufacturer(acme);

      } else {
        Company widgets = new Company();
        widgets.setName("Widgets counter Inc.");
        cogs.setManufacturer(widgets);
      }
    } else {
      cogs.setName("Cogs " + counter);
      if (counter % 2 == 0) {
        Company acme = new Company();
        acme.setName("Acme " + counter + ", Inc.");
        cogs.setManufacturer(acme);

      } else {
        Company widgets = new Company();
        widgets.setName("Widgets " + counter + ", Inc.");
        cogs.setManufacturer(widgets);
      }
    }
    cogs.setInventory(1000 + counter);
    return cogs;
  }

  @Test
  public void testPOJOSearchWithoutSearchHandle() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);

    StructuredQueryBuilder qb = new StructuredQueryBuilder();
    StructuredQueryDefinition q1 = qb.andNot(qb.term("cogs"), qb.term("special"));
    StructuredQueryDefinition qd = qb.and(q1, qb.collection("odd"));
    products.setPageLength(11);
    p = products.search(qd, 1);
    assertEquals( 4, p.getTotalPages());
    // System.out.println(p.getTotalPages());
    long pageNo = 1, count = 0;
    do {
      count = 0;

      p = products.search(qd, pageNo);

      while (p.iterator().hasNext()) {
        Artifact a = p.iterator().next();
        validateArtifact(a);
        assertFalse( a.getId() % 5 == 0);
        assertTrue( a.getId() % 2 != 0);
        assertTrue( a.getManufacturer().getName().contains("Widgets"));
        count++;
        // System.out.println(a.getId()+" "+a.getManufacturer().getName()
        // +"  "+count);
      }
      assertEquals( count, p.size());
      pageNo = pageNo + p.getPageSize();
    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    assertEquals( 4, p.getPageNumber());
    assertEquals( 4, p.getTotalPages());
  }

  @Test
  public void testPOJOSearchWithSearchHandle() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);

    StructuredQueryBuilder qb = new StructuredQueryBuilder();
    StructuredQueryDefinition q1 = qb.range(qb.pathIndex("com.marklogic.client.functionaltest.Artifact/inventory"), "xs:long", Operator.GT, 1010);
    PojoQueryDefinition qd = qb.and(q1, qb.range(qb.pathIndex("com.marklogic.client.functionaltest.Artifact/inventory"), "xs:long", Operator.LE, 1110), qb.collection("even"));
    SearchHandle results = new SearchHandle();
    products.setPageLength(10);
    p = products.search(qd, 1, results);
    assertEquals( 5, p.getTotalPages());
    System.out.println(p.getTotalPages());
    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(qd, pageNo, results);

      while (p.iterator().hasNext()) {
        Artifact a = p.iterator().next();
        validateArtifact(a);
        assertTrue( a.getInventory() > 1010 && a.getInventory() <= 1110);
        assertTrue( a.getId() % 2 == 0);
        assertTrue( a.getManufacturer().getName().contains("Acme"));
        count++;
        // System.out.println(a.getId()+" "+a.getManufacturer().getName()
        // +"  "+count);
      }
      assertEquals( count, p.size());
      pageNo = pageNo + p.getPageSize();
      MatchDocumentSummary[] mds = results.getMatchResults();
      assertEquals( 10, mds.length);
      for (MatchDocumentSummary md : mds) {
        assertTrue( md.getUri().contains("Artifact"));
      }
      String[] facetNames = results.getFacetNames();
      for (String fname : facetNames) {
        System.out.println(fname);
      }
      assertEquals( 0, results.getFacetNames().length);
      assertEquals( 50, results.getTotalResults());
      assertNull(results.getMetrics());
    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    assertEquals( 41, p.getStart());
    assertEquals( 5, p.getPageNumber());
    assertEquals( 5, p.getTotalPages());
  }

  @Test
  public void testPOJOSearchWithJacksonHandle() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);

    StructuredQueryBuilder qb = new StructuredQueryBuilder();
    StructuredQueryDefinition q1 = qb.containerQuery(qb.jsonProperty("name"), qb.term("special"));
    PojoQueryDefinition qd = qb.and(q1, qb.word(qb.jsonProperty("name"), "acme"));
    JacksonHandle results = new JacksonHandle();
    p = products.search(qd, 1, results);
    products.setPageLength(11);
    assertEquals( 1, p.getTotalPages());
    System.out.println(p.getTotalPages() + results.get().toString());
    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(qd, pageNo, results);

      while (p.iterator().hasNext()) {
        Artifact a = p.iterator().next();
        validateArtifact(a);
        count++;
        assertTrue( a.getManufacturer().getName().contains("Acme"));
        assertTrue( a.getName().contains("special"));

      }
      assertEquals( count, p.size());
      pageNo = pageNo + p.getPageSize();

      assertEquals( results.get().get("start").asLong(), p.getStart());
      assertEquals( "json", results.get().withArray("results").get(1).path("format").asText());
      assertTrue( results.get().withArray("results").get(1).path("uri").asText().contains("Artifact"));
      // System.out.println(results.get().toString());
    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    assertFalse( results.get().has("metrics"));
    assertEquals( 11, results.get().get("total").asInt());
    assertEquals( 1, p.getPageNumber());
    assertEquals( 1, p.getTotalPages());
  }

  // Searching for Id as Number in JSON using value query
  @Test
  public void testPOJOSearchWithStringHandle() throws KeyManagementException, NoSuchAlgorithmException, JsonProcessingException, IOException {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);
    StructuredQueryBuilder qb = new StructuredQueryBuilder();
    PojoQueryDefinition qd = qb.value(qb.jsonProperty("id"), 5, 10, 15, 20, 25, 30);
    // StructuredQueryDefinition qd =
    // qb.and(q1,qb.range(qb.pathIndex("com.marklogic.client.functionaltest.Artifact/inventory"),
    // "xs:long",Operator.LE, 1110),qb.collection("even"));

    StringHandle results = new StringHandle();
    JacksonHandle jh = new JacksonHandle();
    p = products.search(qd, 1, jh);

    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(qd, pageNo, results.withFormat(Format.JSON));

      while (p.iterator().hasNext()) {
        Artifact a = p.iterator().next();
        validateArtifact(a);
        count++;
      }
      assertEquals( count, p.getTotalSize());
      pageNo = pageNo + p.getPageSize();
      // System.out.println(results.get().toString());
    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    assertFalse( results.get().isEmpty());
    assertTrue( results.get().contains("results"));
    assertTrue( results.get().contains("\"format\":\"json\""));
    // String expected= jh.get().toString();
    // System.out.println(results.get().contains("\"format\":\"json\"")+
    // expected);
    ObjectMapper mapper = new ObjectMapper();
    // String expected=
    // "{\"snippet-format\":\"snippet\",\"total\":1,\"start\":1,\"page-length\":50,\"results\":[{\"index\":1,\"uri\":\"com.marklogic.client.functionaltest.Artifact/2.json\",\"path\":\"fn:doc(\\\"com.marklogic.client.functionaltest.Artifact/2.json\\\")\",\"score\":55936,\"confidence\":0.4903799,\"fitness\":0.8035046,\"href\":\"/v1/documents?uri=com.marklogic.client.functionaltest.Artifact%2F2.json\",\"mimetype\":\"application/json\",\"format\":\"json\",\"matches\":[{\"path\":\"fn:doc(\\\"com.marklogic.client.functionaltest.Artifact/2.json\\\")\",\"match-text\":[]}]}],\"qtext\":\"cogs 2\",\"metrics\":{\"query-resolution-time\":\"PT0.004S\",\"snippet-resolution-time\":\"PT0S\",\"total-time\":\"PT0.005S\"}}";
    // JsonNode expNode =
    // mapper.readTree(expected).get("results").iterator().next().get("matches");
    JsonNode actNode = mapper.readTree(results.get()).get("total");
    // System.out.println(expNode.equals(actNode)+"\n"+
    // expNode.toString()+"\n"+actNode.toString());

    assertEquals( 6, actNode.asInt());
  }

  /*
   * Searching for string in JSON using value query. Purpose: To validate
   * QueryBuilder's new value methods. We need to use PathIndexProperty on
   * String for StructuredQueryBuilder.JSONProperty.Therefore use
   * ArtifactIndexedOnString.class here. Method used :
   * value(StructuredQueryBuilder.TextIndex index, String... values)
   */

  @Test
  public void testQueryBuilderValueWithString() throws KeyManagementException, NoSuchAlgorithmException, JsonProcessingException, IOException {

    PojoRepository<ArtifactIndexedOnString, String> products = client.newPojoRepository(ArtifactIndexedOnString.class, String.class);
    PojoPage<ArtifactIndexedOnString> p;
    StructuredQueryBuilder qb = new StructuredQueryBuilder();

    for (int i = 1; i < 111; i++) {
      if (i % 2 == 0) {
        products.write(this.getArtifactIndexedOnString(i), "even", "numbers");
      }
      else {
        products.write(this.getArtifactIndexedOnString(i), "odd", "numbers");
      }
    }

    PojoQueryDefinition qd = qb.value(qb.jsonProperty("name"), "Cogs 11", "Cogs 22", "Cogs 33", "Cogs 44", "Cogs 55", "Cogs 66", "Cogs 77");

    StringHandle results = new StringHandle();
    JacksonHandle jh = new JacksonHandle();
    p = products.search(qd, 1, jh);

    long pageNo = 1, count = 0;

    do {
      count = 0;
      p = products.search(qd, pageNo, results.withFormat(Format.JSON));

      while (p.iterator().hasNext()) {
        ArtifactIndexedOnString a = p.iterator().next();
        //validateArtifact(a);
        count++;
      }
      assertEquals( count, p.getTotalSize());
      pageNo = pageNo + p.getPageSize();

    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    assertFalse( results.get().isEmpty());
    assertTrue( results.get().contains("results"));
    assertTrue( results.get().contains("\"format\":\"json\""));

    ObjectMapper mapper = new ObjectMapper();
    JsonNode actNode = mapper.readTree(results.get()).get("total");
    assertEquals( 6, actNode.asInt());
  }

  @Test
  public void testPOJOSearchWithRawXMLStructQD() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);

    QueryManager queryMgr = client.newQueryManager();
    String rawXMLQuery =
        "<search:query " +
            "xmlns:search='http://marklogic.com/appservices/search'>" +
            " <search:and-query><search:term-query>" +
            "<search:text>special</search:text>" +
            "</search:term-query>" +
            "<search:term-query>" +
            "<search:text>Acme</search:text>" +
            "</search:term-query> </search:and-query>" +
            "</search:query>";
    StringHandle rh = new StringHandle(rawXMLQuery);

    StringQueryDefinition qd = queryMgr.newStringDefinition();
    qd.setCriteria("special AND Acme");
    JacksonHandle results = new JacksonHandle();
    p = products.search(qd, 1, results);
    products.setPageLength(11);
    assertEquals( 1, p.getTotalPages());
    System.out.println(p.getTotalPages() + results.get().toString());
    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(qd, pageNo, results);

      while (p.iterator().hasNext()) {
        Artifact a = p.iterator().next();
        validateArtifact(a);
        count++;
        assertTrue( a.getManufacturer().getName().contains("Acme"));
        assertTrue( a.getName().contains("special"));

      }
      assertEquals( count, p.size());
      pageNo = pageNo + p.getPageSize();

      assertEquals( results.get().get("start").asLong(), p.getStart());
      assertEquals( "json", results.get().withArray("results").get(1).path("format").asText());
      assertTrue( results.get().withArray("results").get(1).path("uri").asText().contains("Artifact"));
      // System.out.println(results.get().toString());
    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    assertFalse( results.get().has("metrics"));
    assertEquals( 11, results.get().get("total").asInt());
    assertEquals( 1, p.getPageNumber());
    assertEquals( 1, p.getTotalPages());
  }

  @Test
  public void testPOJOSearchWithRawJSONStructQD() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);

    QueryManager queryMgr = client.newQueryManager();
    JacksonHandle jh = new JacksonHandle();
    ObjectMapper mapper = new ObjectMapper();
    // constructing JSON representation of Raw JSON Structured Query

    ObjectNode txtNode = mapper.createObjectNode();
    txtNode.putArray("text").add("special");
    ObjectNode termQNode = mapper.createObjectNode();
    termQNode.set("term-query", txtNode);
    ObjectNode queriesArrayNode = mapper.createObjectNode();
    queriesArrayNode.putArray("queries").add(termQNode);

    ObjectNode txtNode2 = mapper.createObjectNode();
    txtNode2.putArray("text").add("Widgets");
    ObjectNode termQNode2 = mapper.createObjectNode();
    termQNode2.set("term-query", txtNode2);
    queriesArrayNode.withArray("queries").add(termQNode2);

    ObjectNode orQueryNode = mapper.createObjectNode();
    orQueryNode.set("and-query", queriesArrayNode);

    ObjectNode queryArrayNode = mapper.createObjectNode();
    queryArrayNode.putArray("queries").add(orQueryNode);
    ObjectNode mainNode = mapper.createObjectNode();
    mainNode.set("query", queryArrayNode);
    jh.set(mainNode);
    /*
     * PojoQueryDefinition qd =
     * (PojoQueryDefinition)queryMgr.newRawStructuredQueryDefinition(jh);
     */
    StringQueryDefinition qd = queryMgr.newStringDefinition();
    qd.setCriteria("special AND Widgets");

    JacksonHandle results = new JacksonHandle();
    p = products.search(qd, 1, results);
    products.setPageLength(11);
    assertEquals( 1, p.getTotalPages());
    System.out.println(p.getTotalPages() + results.get().toString());
    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(qd, pageNo, results);

      while (p.iterator().hasNext()) {
        Artifact a = p.iterator().next();
        validateArtifact(a);
        count++;
        assertTrue( a.getManufacturer().getName().contains("Widgets"));
        assertTrue( a.getName().contains("special"));

      }
      assertEquals( count, p.size());
      pageNo = pageNo + p.getPageSize();

      assertEquals( results.get().get("start").asLong(), p.getStart());
      assertEquals( "json", results.get().withArray("results").get(1).path("format").asText());
      assertTrue( results.get().withArray("results").get(1).path("uri").asText().contains("Artifact"));
      System.out.println(results.get().toString());
    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    assertFalse( results.get().has("metrics"));
    assertEquals( 11, results.get().get("total").asInt());
    assertEquals( 1, p.getPageNumber());
    assertEquals( 1, p.getTotalPages());
  }

}
