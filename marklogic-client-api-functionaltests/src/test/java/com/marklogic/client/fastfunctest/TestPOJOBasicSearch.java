/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.functionaltest.Artifact;
import com.marklogic.client.functionaltest.Company;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;



public class TestPOJOBasicSearch extends AbstractFunctionalTest {
  @BeforeEach
  public void setUp() throws Exception {
    client = getDatabaseClient("rest-admin", "x", getConnType());
  }

  public Artifact getArtifact(int counter) {
    Company acme = new Company();
    acme.setName("Acme " + counter + ", Inc.");
    acme.setWebsite("http://www.acme" + counter + ".com");
    acme.setLatitude(41.998 + counter);
    acme.setLongitude(-87.966 + counter);
    Artifact cogs = new Artifact();
    cogs.setId(counter);
    cogs.setName("Cogs " + counter);
    cogs.setManufacturer(acme);
    cogs.setInventory(1000 + counter);

    return cogs;
  }

  public void validateArtifact(Artifact art)
  {
    assertNotNull( art);
    assertNotNull( art.id);
    assertTrue( art.getInventory() > 1000);
  }

  // This test is to search objects under different collections, read documents
  // to validate
  @Test
  public void testPOJOSearchWithCollections() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    // Load more than 111 objects into different collections
    for (int i = 1; i < 112; i++) {
      if (i % 2 == 0) {
        products.write(this.getArtifact(i), "even", "numbers");
      }
      else {
        products.write(this.getArtifact(i), "odd", "numbers");
      }
    }
    assertEquals( 111, products.count("numbers"));
    assertEquals( 55, products.count("even"));
    assertEquals( 56, products.count("odd"));

    products.setPageLength(5);
    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(pageNo, "even");
      while (p.iterator().hasNext()) {
        Artifact a = p.iterator().next();
        validateArtifact(a);
        assertTrue( a.getId() % 2 == 0);
        count++;
      }
      assertEquals( count, p.size());
      pageNo = pageNo + p.getPageSize();
    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    assertEquals( 11, p.getTotalPages());
    pageNo = 1;
    do {
      count = 0;
      p = products.search(1, "odd");
      while (p.iterator().hasNext()) {
        Artifact a = p.iterator().next();
        assertTrue( a.getId() % 2 != 0);
        validateArtifact(a);
        products.delete(a.getId());
        count++;
      }
      // assertEquals(count,p.size());
      pageNo = pageNo + p.getPageSize();

    } while (!p.isLastPage());

    assertEquals( 55, products.count());
    products.deleteAll();
    // see any document exists
    assertFalse( products.exists((long) 12));
  }

  @Test
  public void testPOJOWriteWithPojoPageReadAll() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    // Load more than 110 objects into different collections
    products.deleteAll();
    for (int i = 222; i < 333; i++) {
      if (i % 2 == 0) {
        products.write(this.getArtifact(i), "even", "numbers");
      }
      else {
        products.write(this.getArtifact(i), "odd", "numbers");
      }
    }
    assertEquals( 111, products.count("numbers"));
    assertEquals( 56, products.count("even"));
    assertEquals( 55, products.count("odd"));

    System.out.println("Default Page length setting on docMgr :" + products.getPageLength());
    assertEquals( 50, products.getPageLength());
    products.setPageLength(1);
    assertEquals( 1, products.getPageLength());
    PojoPage<Artifact> p = products.search(1, "even", "odd");
    // test for page methods
    assertEquals( 1, p.size());
    System.out.println("Page size" + p.size());
    assertEquals( 1, p.getStart());
    System.out.println("Starting record in first page " + p.getStart());

    assertEquals( 111, p.getTotalSize());
    System.out.println("Total number of estimated results:" + p.getTotalSize());
    assertEquals( 111, p.getTotalPages());
    System.out.println("Total number of estimated pages :" + p.getTotalPages());
    assertTrue( p.isFirstPage());// this is bug
    assertFalse( p.isLastPage());
    assertTrue( p.hasContent());
    // Need the Issue #75 to be fixed
    assertFalse( p.hasPreviousPage());
    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(pageNo, "numbers");

      if (pageNo > 1) {
        assertFalse( p.isFirstPage());
        assertTrue( p.hasPreviousPage());
      }

      while (p.iterator().hasNext()) {
        this.validateArtifact(p.iterator().next());
        count++;
      }
      assertEquals( p.size(), count);

      pageNo = pageNo + p.getPageSize();
    } while (!(p.isLastPage()) && pageNo < p.getTotalSize());
    // assertTrue(pageNo == p.getTotalPages());
    assertTrue( p.hasPreviousPage());
    assertEquals( 1, p.getPageSize());
    assertEquals( 111, p.getTotalSize());

    products.deleteAll();
    p = products.readAll(1);
    assertFalse( p.hasContent());

  }
}
