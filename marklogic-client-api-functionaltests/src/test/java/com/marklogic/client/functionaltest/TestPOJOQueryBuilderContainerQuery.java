/*
 * Copyright 2014-2019 MarkLogic Corporation
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

package com.marklogic.client.functionaltest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.pojo.PojoQueryBuilder.Operator;
import com.marklogic.client.pojo.PojoQueryDefinition;
import com.marklogic.client.pojo.PojoRepository;

public class TestPOJOQueryBuilderContainerQuery extends BasicJavaClientREST {

  private static String dbName = "TestPOJOQueryBuilderContainerQuerySearchDB";
  private static String[] fNames = { "TestPOJOQueryBuilderContainserQuerySearchDB-1" };

  private DatabaseClient client;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    // BasicJavaClientREST.setDatabaseProperties(dbName,
    // "trailing-wildcard-searches", true);
    // BasicJavaClientREST.setDatabaseProperties(dbName, "word-positions",
    // true);
    // BasicJavaClientREST.setDatabaseProperties(dbName,
    // "element-word-positions", true);
    BasicJavaClientREST.addRangePathIndex(dbName, "long", "com.marklogic.client.functionaltest.Artifact/inventory", "", "reject", true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }

  @Before
  public void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    client = getDatabaseClient("rest-admin", "x", getConnType());

  }

  @After
  public void tearDown() throws Exception {
    // release client
    client.release();

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

  // Below scenario is to test the ContainerQuery with term query
  @Test
  public void testPOJOContainerQuerySearchWithWord() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);
    String[] searchOptions = { "case-sensitive", "wildcarded", "min-occurs=2" };
    PojoQueryBuilder qb = products.getQueryBuilder();
    PojoQueryDefinition qd = qb.containerQuery("manufacturer", qb.term("counter"));
    JacksonHandle jh = new JacksonHandle();
    products.setPageLength(5);
    p = products.search(qd, 1, jh);
    assertEquals("total no of pages", 3, p.getTotalPages());
    System.out.println(jh.get().toString());

    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(qd, pageNo);
      while (p.hasNext()) {
        Artifact a = p.next();
        validateArtifact(a);
        assertTrue("Verifying document id is part of the search ids", a.getId() % 5 == 0);
        assertTrue("Verifying Manufacurer has term counter", a.getManufacturer().getName().contains("counter"));
        count++;
        System.out.println(a.getManufacturer().getName());
      }
      assertEquals("Page size", count, p.size());
      pageNo = pageNo + p.getPageSize();
    } while (!p.isLastPage() && pageNo <= p.getTotalSize());
    assertEquals("page number after the loop", 3, p.getPageNumber());
    assertEquals("total no of pages", 3, p.getTotalPages());
    assertEquals("page length from search handle", 5, jh.get().path("page-length").asInt());
    assertEquals("Total results from search handle", 11, jh.get().path("total").asInt());

  }

  // Below scenario is to test container query builder with wild card options in
  // word query
  @Test
  public void testPOJOwordSearchWithContainerQueryBuilder() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);
    String[] searchOptions = { "case-sensitive", "wildcarded", "max-occurs=1" };
    PojoQueryBuilder qb = products.getQueryBuilder();
    String[] searchNames = { "special" };
    PojoQueryDefinition qd = qb.filteredQuery(qb.containerQueryBuilder("manufacturer", Company.class).word("name", searchOptions, 1.0, searchNames));

    JacksonHandle jh = new JacksonHandle();
    products.setPageLength(11);
    p = products.search(qd, 1, jh);
    assertEquals("page number after the loop", 1, p.getPageNumber());
    // assertEquals("total no of pages",1,p.getTotalPages());
    long pageNo = 1, count = 0, total = 0;
    do {
      count = 0;
      p = products.search(qd, pageNo);
      while (p.hasNext()) {
        Artifact a = p.next();
        validateArtifact(a);
        assertTrue("Verifying document id is part of the search ids", a.getId() % 5 == 0);
        assertTrue("Verifying name is part of the search", a.getManufacturer().getName().contains("special"));
        count++;
        total++;
        System.out.println(a.getName());
      }
      assertEquals("Page size", count, p.size());
      pageNo = pageNo + p.getPageSize();
    } while (!p.isLastPage() && pageNo <= p.getTotalSize());
    // assertEquals("page number after the loop",2,p.getPageNumber());
    // assertEquals("total no of pages",0,p.getTotalPages());
    assertEquals("page length from search handle", 11, jh.get().path("page-length").asInt());
    assertEquals("Total results from search handle", 11, total);
  }

  // Below scenario is verifying range query from PojoBuilder

  @Test
  public void testPOJORangeSearch() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);
    PojoQueryBuilder qb = products.getQueryBuilder();
    PojoQueryDefinition qd = qb.range("inventory", Operator.GE, 1055);
    JacksonHandle jh = new JacksonHandle();
    products.setPageLength(56);
    p = products.search(qd, 1, jh);
    assertEquals("total no of pages", 1, p.getTotalPages());

    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(qd, pageNo);
      while (p.hasNext()) {
        Artifact a = p.next();
        validateArtifact(a);
        assertTrue("Verifying document id is part of the search ids", a.getId() >= 55);
        count++;
      }
      assertEquals("Page size", count, p.size());
      pageNo = pageNo + p.getPageSize();
    } while (!p.isLastPage() && pageNo <= p.getTotalSize());
    assertEquals("page number after the loop", 1, p.getPageNumber());
    assertEquals("total no of pages", 1, p.getTotalPages());
    assertEquals("page length from search handle", 56, jh.get().path("page-length").asInt());
    assertEquals("Total results from search handle", 56, jh.get().path("total").asInt());
  }

  // Below scenario is to test range query with options
  @Test
  public void testPOJORangeQuerySearchWithOptions() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);
    String[] searchOptions = { "uncached", "min-occurs=1" };
    PojoQueryBuilder qb = products.getQueryBuilder();
    String[] searchNames = { "counter", "special" };
    PojoQueryDefinition qd = qb.range("inventory", searchOptions, Operator.LE, 1054);
    JacksonHandle jh = new JacksonHandle();
    products.setPageLength(55);
    p = products.search(qd, 1, jh);
    System.out.println(jh.get().toString());
    assertEquals("total no of pages", 1, p.getTotalPages());

    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(qd, pageNo);
      while (p.hasNext()) {
        Artifact a = p.next();
        validateArtifact(a);
        assertTrue("Verifying document id is part of the search ids", a.getId() <= 54);
        count++;
      }
      assertEquals("Page size", count, p.size());
      pageNo = pageNo + p.getPageSize();
    } while (!p.isLastPage() && pageNo <= p.getTotalSize());
    assertEquals("page number after the loop", 1, p.getPageNumber());
    assertEquals("total no of pages", 1, p.getTotalPages());
    assertEquals("page length from search handle", 55, jh.get().path("page-length").asInt());
    assertEquals("Total results from search handle", 54, jh.get().path("total").asInt());
  }

  // Below scenario is verifying and query with all pojo builder methods

  @Test
  public void testPOJOWordSearchWithOptions() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    this.loadSimplePojos(products);

    String[] searchOptions = { "case-sensitive", "wildcarded", "max-occurs=1" };
    String[] rangeOptions = { "uncached", "min-occurs=1" };
    PojoQueryBuilder qb = products.getQueryBuilder();
    String[] searchNames = { "Acm*" };
    PojoQueryDefinition qd = qb.filteredQuery(qb.and(qb.andNot(qb.word("name", searchOptions, 1.0, searchNames),
        qb.containerQueryBuilder("manufacturer", Company.class).value("name", "Acme special, Inc.")),
        qb.range("inventory", rangeOptions, Operator.LT, 1101)));

    JacksonHandle jh = new JacksonHandle();
    products.setPageLength(25);
    p = products.search(qd, 1, jh);
    System.out.println(jh.get().toString());
    long pageNo = 1, count = 0, total = 0;
    do {
      count = 0;
      p = products.search(qd, pageNo);
      while (p.hasNext()) {
        Artifact a = p.next();
        validateArtifact(a);
        assertTrue("Verifying document id is part of the search ids", a.getId() < 1101);
        assertFalse("Verifying document has word counter", a.getManufacturer().getName().contains("special"));
        assertTrue("Verifying document has Name Acme", a.getManufacturer().getName().contains("Acme"));
        count++;
        total++;

      }
      assertEquals("Page size", count, p.size());

      if (p.size() <= 0) {
        System.out.println(p.getTotalSize() + " " + p.isLastPage() + "page size" + p.size());
        break;
      }
      pageNo = pageNo + p.getPageSize();
    } while (p.size() > 0 && p.hasNextPage());
    System.out.println(pageNo);
    assertEquals("page has results", 25, jh.get().path("results").size());
    assertEquals("Page no after the loop", 51, pageNo);
    assertEquals("page length from search handle", 25, jh.get().path("page-length").asInt());
    assertEquals("Total results from search handle", 40, total);
  }

}
