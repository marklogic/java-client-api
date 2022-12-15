/*
 * Copyright (c) 2022 MarkLogic Corporation
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
import com.marklogic.client.pojo.PojoQueryDefinition;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class TestPOJOwithQBEQueryDef extends AbstractFunctionalTest {

  @Before
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
    assertNotNull("Artifact object should never be Null", art);
    assertNotNull("Id should never be Null", art.id);
    assertTrue("Inventry is always greater than 1000", art.getInventory() > 1000);
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

  @Test(expected = ClassCastException.class)
  public void testPOJOqbeSearchWithoutSearchHandle() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);
    QueryManager queryMgr = client.newQueryManager();
    String queryAsString =
        "{\"$query\":{"
            + "\"$and\":[{\"name\":{\"$word\":\"cogs\",\"$exact\": false}}]"
            + ",\"$not\":[{\"name\":{\"$word\":\"special\",\"$exact\": false}}]"
            + "}}";

    PojoQueryDefinition qd = (PojoQueryDefinition) queryMgr.newRawQueryByExampleDefinition(new StringHandle(queryAsString).withFormat(Format.JSON));
    qd.setCollections("odd");
    products.setPageLength(11);
    p = products.search(qd, 1);
    assertEquals("total no of pages", 4, p.getTotalPages());
    // System.out.println(p.getTotalPages());
    long pageNo = 1, count = 0;
    do {
      count = 0;

      p = products.search(qd, pageNo);

      while (p.iterator().hasNext()) {
        Artifact a = p.iterator().next();
        validateArtifact(a);
        assertFalse("Verifying document with special is not there", a.getId() % 5 == 0);
        assertTrue("Artifact Id is odd", a.getId() % 2 != 0);
        assertTrue("Company name contains widgets", a.getManufacturer().getName().contains("Widgets"));
        count++;
        // System.out.println(a.getId()+" "+a.getManufacturer().getName()
        // +"  "+count);
      }
      assertEquals("Page size", count, p.size());
      pageNo = pageNo + p.getPageSize();
    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    assertEquals("page number after the loop", 4, p.getPageNumber());
    assertEquals("total no of pages", 4, p.getTotalPages());
  }

  @Test(expected = ClassCastException.class)
  public void testPOJOqbeSearchWithSearchHandle() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);

    QueryManager queryMgr = client.newQueryManager();
    String queryAsString =
        "{\"$query\":{"
            + "\"$and\":[{\"inventory\":{\"$gt\":1010}},{\"inventory\":{\"$le\":1110}}]"
            + ",\"$filtered\": true}}";
    System.out.println(queryAsString);
    PojoQueryDefinition qd = (PojoQueryDefinition) queryMgr.newRawQueryByExampleDefinition(new StringHandle(queryAsString).withFormat(Format.JSON));
    qd.setCollections("even");
    SearchHandle results = new SearchHandle();
    products.setPageLength(10);
    p = products.search(qd, 1, results);
    assertEquals("total no of pages", 5, p.getTotalPages());
    System.out.println(p.getTotalPages());
    // System.out.println(results.getMetrics().getQueryResolutionTime());
    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(qd, pageNo, results);

      while (p.iterator().hasNext()) {
        Artifact a = p.iterator().next();
        validateArtifact(a);
        assertTrue("Enventory lies between 1010 to 1110", a.getInventory() > 1010 && a.getInventory() <= 1110);
        assertTrue("Artifact Id is even", a.getId() % 2 == 0);
        assertTrue("Company name contains Acme", a.getManufacturer().getName().contains("Acme"));
        count++;
        // System.out.println(a.getId()+" "+a.getManufacturer().getName()
        // +"  "+count);
      }
      assertEquals("Page size", count, p.size());
      pageNo = pageNo + p.getPageSize();
      MatchDocumentSummary[] mds = results.getMatchResults();
      assertEquals("Size of the results summary", 10, mds.length);
      for (MatchDocumentSummary md : mds) {
        assertTrue("every uri should contain the class name", md.getUri().contains("Artifact"));
      }
      String[] facetNames = results.getFacetNames();
      for (String fname : facetNames) {
        System.out.println(fname);
      }
      // assertEquals("Total results from search handle ",50,results.getTotalResults());
      // assertTrue("Search Handle metric results ",results.getMetrics().getTotalTime()>0);
    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    assertEquals("Page start check", 41, p.getStart());
    assertEquals("page number after the loop", 5, p.getPageNumber());
    assertEquals("total no of pages", 5, p.getTotalPages());
  }

  @Test(expected = ClassCastException.class)
  public void testPOJOCombinedSearchWithJacksonHandle() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);
    QueryManager queryMgr = client.newQueryManager();
    String queryAsString = "{\"search\":{\"query\":{\"and-query\":["
        + "{\"word-constraint-query\":{\"constraint-name\":\"pojo-name-field\", \"text\":\"Acme\"}},"
        + "{\"word-constraint-query\":{\"constraint-name\":\"pojo-name-field\", \"text\":\"special\"}}]},"
        + "\"options\":{\"constraint\":{\"name\":\"pojo-name-field\", \"word\":{\"json-property\":\"name\"}}}"
        + "}}";

    PojoQueryDefinition qd = (PojoQueryDefinition) queryMgr.newRawCombinedQueryDefinition(new StringHandle(queryAsString).withFormat(Format.JSON));
    JacksonHandle results = new JacksonHandle();
    p = products.search(qd, 1, results);
    products.setPageLength(11);
    assertEquals("total no of pages", 1, p.getTotalPages());
    // System.out.println(p.getTotalPages()+results.get().toString());
    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(qd, pageNo, results);

      while (p.iterator().hasNext()) {
        Artifact a = p.iterator().next();
        validateArtifact(a);
        count++;
        assertTrue("Manufacture name starts with acme", a.getManufacturer().getName().contains("Acme"));
        assertTrue("Artifact name contains", a.getName().contains("special"));

      }
      assertEquals("Page size", count, p.size());
      pageNo = pageNo + p.getPageSize();

      assertEquals("Page start from search handls vs page methods", results.get().get("start").asLong(), p.getStart());
      assertEquals("Format in the search handle", "json", results.get().withArray("results").get(1).path("format").asText());
      assertTrue("Uri in search handle contains Artifact", results.get().withArray("results").get(1).path("uri").asText().contains("Artifact"));
      // System.out.println(results.get().toString());
    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    assertFalse("search handle has metrics", results.get().has("metrics"));
    assertEquals("Total from search handle", 11, results.get().get("total").asInt());
    assertEquals("page number after the loop", 1, p.getPageNumber());
    assertEquals("total no of pages", 1, p.getTotalPages());
  }

  // Searching for Id as Number in JSON using range query
  @Test(expected = ClassCastException.class)
  public void testPOJOcombinedSearchforNumberWithStringHandle() throws KeyManagementException, NoSuchAlgorithmException, JsonProcessingException, IOException {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);

    QueryManager queryMgr = client.newQueryManager();
    String queryAsString = "{\"search\":{\"query\":{"
        + "\"range-constraint-query\":{\"constraint-name\":\"id\", \"value\":[5,10,15,20,25,30]}},"
        + "\"options\":{\"return-metrics\":false, \"constraint\":{\"name\":\"id\", \"range\":{\"type\": \"xs:long\",\"json-property\":\"id\"}}}"
        + "}}";
    PojoQueryDefinition qd = (PojoQueryDefinition) queryMgr.newRawCombinedQueryDefinition(new StringHandle(queryAsString).withFormat(Format.JSON));

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
      assertEquals("Page total results", count, p.getTotalSize());
      pageNo = pageNo + p.getPageSize();
      System.out.println(results.get().toString());
    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    assertFalse("String handle is not empty", results.get().isEmpty());
    assertTrue("String handle contains results", results.get().contains("results"));
    assertTrue("String handle contains format", results.get().contains("\"format\":\"json\""));
    ObjectMapper mapper = new ObjectMapper();
    JsonNode actNode = mapper.readTree(results.get()).get("total");
    assertEquals("Total search results resulted are ", 6, actNode.asInt());
  }

}
