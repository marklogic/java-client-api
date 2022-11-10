/*
 * Copyright (c) 2019 MarkLogic Corporation
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

import com.marklogic.client.functionaltest.Artifact;
import com.marklogic.client.functionaltest.Company;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoRepository;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestPOJOBasicSearch extends AbstractFunctionalTest {
  @Before
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
    assertNotNull("Artifact object should never be Null", art);
    assertNotNull("Id should never be Null", art.id);
    assertTrue("Inventry is always greater than 1000", art.getInventory() > 1000);
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
    assertEquals("Total number of object recods", 111, products.count("numbers"));
    assertEquals("Collection even count", 55, products.count("even"));
    assertEquals("Collection odd count", 56, products.count("odd"));

    products.setPageLength(5);
    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(pageNo, "even");
      while (p.iterator().hasNext()) {
        Artifact a = p.iterator().next();
        validateArtifact(a);
        assertTrue("Artifact Id is even", a.getId() % 2 == 0);
        count++;
      }
      assertEquals("Page size", count, p.size());
      pageNo = pageNo + p.getPageSize();
    } while (!p.isLastPage() && pageNo < p.getTotalSize());
    assertEquals("total no of pages", 11, p.getTotalPages());
    pageNo = 1;
    do {
      count = 0;
      p = products.search(1, "odd");
      while (p.iterator().hasNext()) {
        Artifact a = p.iterator().next();
        assertTrue("Artifact Id is even", a.getId() % 2 != 0);
        validateArtifact(a);
        products.delete(a.getId());
        count++;
      }
      // assertEquals("Page size",count,p.size());
      pageNo = pageNo + p.getPageSize();

    } while (!p.isLastPage());

    assertEquals("Total no of documents left", 55, products.count());
    products.deleteAll();
    // see any document exists
    assertFalse("all the documents are deleted", products.exists((long) 12));
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
    assertEquals("Total number of object recods", 111, products.count("numbers"));
    assertEquals("Collection even count", 56, products.count("even"));
    assertEquals("Collection odd count", 55, products.count("odd"));

    System.out.println("Default Page length setting on docMgr :" + products.getPageLength());
    assertEquals("Default setting for page length", 50, products.getPageLength());
    products.setPageLength(1);
    assertEquals("explicit setting for page length", 1, products.getPageLength());
    PojoPage<Artifact> p = products.search(1, "even", "odd");
    // test for page methods
    assertEquals("Number of records", 1, p.size());
    System.out.println("Page size" + p.size());
    assertEquals("Starting record in first page ", 1, p.getStart());
    System.out.println("Starting record in first page " + p.getStart());

    assertEquals("Total number of estimated results:", 111, p.getTotalSize());
    System.out.println("Total number of estimated results:" + p.getTotalSize());
    assertEquals("Total number of estimated pages :", 111, p.getTotalPages());
    System.out.println("Total number of estimated pages :" + p.getTotalPages());
    assertTrue("Is this First page :", p.isFirstPage());// this is bug
    assertFalse("Is this Last page :", p.isLastPage());
    assertTrue("Is this First page has content:", p.hasContent());
    // Need the Issue #75 to be fixed
    assertFalse("Is first page has previous page ?", p.hasPreviousPage());
    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.search(pageNo, "numbers");

      if (pageNo > 1) {
        assertFalse("Is this first Page", p.isFirstPage());
        assertTrue("Is page has previous page ?", p.hasPreviousPage());
      }

      while (p.iterator().hasNext()) {
        this.validateArtifact(p.iterator().next());
        count++;
      }
      assertEquals("document count", p.size(), count);

      pageNo = pageNo + p.getPageSize();
    } while (!(p.isLastPage()) && pageNo < p.getTotalSize());
    // assertTrue("page count is 111 ",pageNo == p.getTotalPages());
    assertTrue("Page has previous page ?", p.hasPreviousPage());
    assertEquals("page size", 1, p.getPageSize());
    assertEquals("document count", 111, p.getTotalSize());

    products.deleteAll();
    p = products.readAll(1);
    assertFalse("Page has any records ?", p.hasContent());

  }
}
