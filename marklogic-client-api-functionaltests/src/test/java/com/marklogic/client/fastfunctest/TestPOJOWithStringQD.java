/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.functionaltest.Artifact;
import com.marklogic.client.functionaltest.Company;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class TestPOJOWithStringQD extends AbstractFunctionalTest {
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
    assertNotNull( art.getInventory() > 1000);
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

  @Test
  public void testPOJOSearchWithoutSearchHandle() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);
    QueryManager queryMgr = client.newQueryManager();
    StringQueryDefinition qd = queryMgr.newStringDefinition();
    qd.setCriteria("Widgets");

    products.setPageLength(11);
    p = products.search(qd, 1);
    assertEquals( 5, p.getTotalPages());
    System.out.println(p.getTotalPages());
    long pageNo = 1, count = 0;
    do {
      count = 0;

      p = products.search(qd, pageNo);

      while (p.iterator().hasNext()) {
        Artifact a = p.iterator().next();
        validateArtifact(a);
        assertNotNull( a.getId() % 2 != 0);
        assertNotNull( a.getManufacturer().getName().contains("Widgets"));
        count++;
        // System.out.println(a.getId()+" "+a.getManufacturer().getName()
        // +"  "+count);
      }
      assertEquals( count, p.size());
      pageNo = pageNo + p.getPageSize();
    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    assertEquals( 5, p.getPageNumber());
    assertEquals( 5, p.getTotalPages());
  }

  @Test
  public void testPOJOSearchWithSearchHandle() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);
    QueryManager queryMgr = client.newQueryManager();
    StringQueryDefinition qd = queryMgr.newStringDefinition();
    qd.setCriteria("Acme");
    SearchHandle results = new SearchHandle();
    products.setPageLength(11);
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
        assertNotNull( a.getId() % 2 == 0);
        assertNotNull( a.getManufacturer().getName().contains("Acme"));
        count++;
        // System.out.println(a.getId()+" "+a.getManufacturer().getName()
        // +"  "+count);
      }
      assertEquals( count, p.size());
      pageNo = pageNo + p.getPageSize();
      MatchDocumentSummary[] mds = results.getMatchResults();
      assertEquals( 11, mds.length);
      for (MatchDocumentSummary md : mds) {
        assertNotNull( md.getUri().contains("Artifact"));
      }
      String[] facetNames = results.getFacetNames();
      for (String fname : facetNames) {
        System.out.println(fname);
      }
      assertEquals( 55, results.getTotalResults());
      // assertNotNull(results.getMetrics().getTotalTime()>0);
    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    assertEquals( 45, p.getStart());
    assertEquals( 5, p.getPageNumber());
    assertEquals( 5, p.getTotalPages());
  }

  @Test
  public void testPOJOSearchWithJacksonHandle() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);
    QueryManager queryMgr = client.newQueryManager();
    StringQueryDefinition qd = queryMgr.newStringDefinition();
    qd.setCriteria("cogs");
    JacksonHandle results = new JacksonHandle();
    p = products.search(qd, 1, results);
    products.setPageLength(11);
    assertEquals( 3, p.getTotalPages());
    // System.out.println(p.getTotalPages()+results.get().toString());
    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(qd, pageNo, results);

      while (p.iterator().hasNext()) {
        Artifact a = p.iterator().next();
        validateArtifact(a);
        count++;
        // System.out.println(a.getId()+" "+a.getManufacturer().getName()
        // +"  "+count);
      }
      assertEquals( count, p.size());
      pageNo = pageNo + p.getPageSize();

      assertEquals( results.get().get("start").asLong(), p.getStart());
      assertEquals( "json", results.get().withArray("results").get(1).path("format").asText());
      assertNotNull( results.get().withArray("results").get(1).path("uri").asText().contains("Artifact"));
      // System.out.println(results.get().toString());
    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    // assertNotNull(results.get().has("metrics"));
    assertEquals( "cogs", results.get().path("qtext").asText());
    assertEquals( 110, results.get().get("total").asInt());
    assertEquals( 10, p.getPageNumber());
    assertEquals( 10, p.getTotalPages());
  }

  // Searching for Id as Number in JSON using string should not return any
  // results
  @Test
  public void testPOJOSearchWithStringHandle() throws KeyManagementException, NoSuchAlgorithmException, JsonProcessingException, IOException {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);
    QueryManager queryMgr = client.newQueryManager();
    StringQueryDefinition qd = queryMgr.newStringDefinition();
    qd.setCriteria("5");
    StringHandle results = new StringHandle();
    JacksonHandle jh = new JacksonHandle();
    p = products.search(qd, 1, jh);

    long pageNo = 1;
    do {
      p = products.search(qd, pageNo, results.withFormat(Format.JSON));

      while (p.iterator().hasNext()) {
        Artifact a = p.iterator().next();
        validateArtifact(a);
      }
      assertEquals( 0, p.getTotalSize());
      pageNo = pageNo + p.getPageSize();
      // System.out.println(results.get().toString());
    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    assertFalse(results.get().isEmpty());
    assertNotNull( results.get().contains("results"));
    assertFalse(results.get().contains("\"format\":\"json\""));

    ObjectMapper mapper = new ObjectMapper();
    JsonNode actNode = mapper.readTree(results.get()).get("total");
    // System.out.println(expNode.equals(actNode)+"\n"+
    // expNode.toString()+"\n"+actNode.toString());

    assertEquals( actNode.asInt(), 0);
  }
}
