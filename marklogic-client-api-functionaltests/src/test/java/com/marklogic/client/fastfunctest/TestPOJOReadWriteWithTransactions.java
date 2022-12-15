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

import com.marklogic.client.Transaction;
import com.marklogic.client.functionaltest.Artifact;
import com.marklogic.client.functionaltest.Company;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPOJOReadWriteWithTransactions extends AbstractFunctionalTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    createUserRolesWithPrevilages("pojoRole", "xdmp:eval", "xdmp:eval-in", "xdbc:eval", "xdbc:eval-in", "any-uri", "xdbc:invoke", "xdbc:invoke-in", "xdmp:invoke", "xdmp:invoke-in");
    createRESTUser("pojoUser", "pojoUser", "tde-admin", "tde-view", "pojoRole", "rest-admin", "rest-writer",
            "rest-reader", "rest-extension-user", "manage-user", "query-view-admin");
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    deleteUserRole("pojoRole");
    deleteRESTUser("pojoUser");
  }

  @Before
  public void setUp() throws Exception {
    client = getDatabaseClient("pojoUser", "pojoUser", getConnType());
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

  // This test is to persist a simple design model objects in ML, read from ML,
  // delete all
  // Issue 104 for unable to have transaction in count,exists, delete methods
  @Test
  public void test0POJOWriteWithTransaction() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    Transaction t = client.openTransaction();
    // Load more than 100 objects
    try {
      for (int i = 1; i < 112; i++) {
        products.write(this.getArtifact(i), t);
      }
      assertEquals("Total number of object recods", 111, products.count(t));
      for (long i = 1; i < 112; i++) {
        assertTrue("Product id " + i + " does not exist", products.exists(i, t));
        this.validateArtifact(products.read(i, t));
      }

    } catch (Exception e) {
      throw e;
    } finally {
      t.rollback();
    }

    for (long i = 1; i < 112; i++) {
      assertFalse("Product id exists ?", products.exists(i));
    }
  }

  // This test is to persist objects into different collections, read documents
  // based on Id and delete single object based on Id
  @Test
  public void test1POJOWriteWithTransCollection() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    // Load more than 110 objects into different collections
    Transaction t = client.openTransaction();
    try {
      for (int i = 112; i < 222; i++) {
        if (i % 2 == 0) {
          products.write(this.getArtifact(i), t, "even", "numbers");
        }
        else {
          products.write(this.getArtifact(i), t, "odd", "numbers");
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      t.commit();
    }
    assertEquals("Total number of object recods", 110, products.count("numbers"));
    assertEquals("Collection even count", 55, products.count("even"));
    assertEquals("Collection odd count", 55, products.count("odd"));
    for (long i = 112; i < 222; i++) {
      // validate all the records inserted are readable
      assertTrue("Product id " + i + " does not exist", products.exists(i));
      this.validateArtifact(products.read(i));
    }
    Transaction t2 = client.openTransaction();
    try {
      Long[] ids = { (long) 112, (long) 113 };
      products.delete(ids, t2);
      assertFalse("Product id 112 exists ?", products.exists((long) 112, t2));
      // assertTrue("Product id 112 exists ?",products.exists((long)112));
      products.deleteAll(t2);
      for (long i = 112; i < 222; i++) {
        assertFalse("Product id " + i + " exists ?", products.exists(i, t2));
        // assertTrue("Product id "+i+" exists ?",products.exists(i));
      }
    } catch (Exception e) {
      throw e;
    } finally {
      t2.commit();
    }
    // see any document exists
    for (long i = 112; i < 222; i++) {
      assertFalse("Product id " + i + " exists ?", products.exists(i));
    }
    // see if it complains when there are no records
    products.delete((long) 112);
    products.deleteAll();
  }

  // This test is to read objects into pojo page based on Ids
  // until #103 is resolved
  @Test
  public void test2POJOWriteWithPojoPage() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    // Load more than 110 objects into different collections
    products.deleteAll();
    Long[] ids = new Long[111];
    int j = 0;
    for (int i = 222; i < 333; i++) {
      ids[j] = (long) i;
      j++;
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
    products.setPageLength(100);
    assertEquals("explicit setting for page length", 100, products.getPageLength());
    PojoPage<Artifact> p = products.search(1, "numbers");
    // test for page methods
    assertEquals("Number of records", 100, p.size());
    // System.out.println("Page size"+p.size());
    assertEquals("Starting record in first page ", 1, p.getStart());
    // System.out.println("Starting record in first page "+p.getStart());

    assertEquals("Total number of estimated results:", 111, p.getTotalSize());
    // System.out.println("Total number of estimated results:"+p.getTotalSize());
    assertEquals("Total number of estimated pages :", 2, p.getTotalPages());
    // System.out.println("Total number of estimated pages :"+p.getTotalPages());
    System.out.println("is this firstPage or LastPage:" + p.isFirstPage() + "  " + p.isLastPage() + "has  previous page" + p.hasPreviousPage());
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
      Iterator<Artifact> itr = p.iterator();
      while (itr.hasNext()) {
        this.validateArtifact(p.iterator().next());
        count++;
      }
      // assertEquals("document count", p.size(),count);
      System.out.println("Is this Last page :" + p.hasContent() + p.isLastPage() + p.getPageNumber());
      pageNo = pageNo + p.getPageSize();
    } while (pageNo < p.getTotalSize());
    assertTrue("Page has previous page ?", p.hasPreviousPage());
    assertEquals("page size", 11, p.size());
    assertEquals("document count", 111, p.getTotalSize());
    assertTrue("Page has any records ?", p.hasContent());

    products.deleteAll();
    // see any document exists
    for (long i = 112; i < 222; i++) {
      assertFalse("Product id " + i + " exists ?", products.exists(i));
    }
    // see if it complains when there are no records
  }

  @Test
  public void test3POJOWriteWithPojoPageReadAll() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    // Load more than 110 objects into different collections
    products.deleteAll();
    Transaction t = client.openTransaction();
    PojoPage<Artifact> p;
    try {
      for (int i = 222; i < 333; i++) {
        if (i % 2 == 0) {
          products.write(this.getArtifact(i), t, "even", "numbers");
        }
        else {
          products.write(this.getArtifact(i), t, "odd", "numbers");
        }
      }

      // System.out.println("Default Page length setting on docMgr :"+products.getPageLength());
      assertEquals("Default setting for page length", 50, products.getPageLength());
      products.setPageLength(25);
      assertEquals("explicit setting for page length", 25, products.getPageLength());
      p = products.readAll(1, t);
      // test for page methods
      assertEquals("Number of records", 25, p.size());
      System.out.println("Page size" + p.size());
      assertEquals("Starting record in first page ", 1, p.getStart());
      System.out.println("Starting record in first page " + p.getStart());

      assertEquals("Total number of estimated results:", 111, p.getTotalSize());
      System.out.println("Total number of estimated results:" + p.getTotalSize());
      assertEquals("Total number of estimated pages :", 5, p.getTotalPages());
      System.out.println("Total number of estimated pages :" + p.getTotalPages());
      assertTrue("Is this First page :", p.isFirstPage());
      assertFalse("Is this Last page :", p.isLastPage());
      assertTrue("Is this First page has content:", p.hasContent());
      // Need the Issue #75 to be fixed
      assertFalse("Is first page has previous page ?", p.hasPreviousPage());
      long pageNo = 1, count = 0;
      do {
        count = 0;
        p = products.readAll(pageNo, t);

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

      assertTrue("Page has previous page ?", p.hasPreviousPage());
      assertEquals("page size", 11, p.size());
      assertEquals("document count", 111, p.getTotalSize());
    } catch (Exception e) {
      throw e;
    } finally {
      t.rollback();
    }
    p = products.readAll(1);
    assertFalse("Page has any records ?", p.hasContent());

  }

  // @Test
  public void test4POJOSearchWithCollectionsandTransaction() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    products.deleteAll();
    Transaction t = client.openTransaction();
    // Load more than 111 objects into different collections
    try {
      for (int i = 1; i < 112; i++) {
        if (i % 2 == 0) {
          products.write(this.getArtifact(i), t, "even", "numbers");
        }
        else {
          products.write(this.getArtifact(i), "odd", "numbers");
        }
      }
      assertEquals("Total number of object recods", 56, products.count("numbers"));
      assertEquals("Collection even count", 0, products.count("even"));
      assertEquals("Collection odd count", 56, products.count("odd"));

      products.setPageLength(10);
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
      assertEquals("total no of pages", 0, p.getTotalPages());
      do {
        count = 0;
        p = products.search(pageNo, t, "even");
        while (p.iterator().hasNext()) {
          Artifact a = p.iterator().next();
          validateArtifact(a);
          assertTrue("Artifact Id is even", a.getId() % 2 == 0);
          count++;
        }
        assertEquals("Page size", count, p.size());
        pageNo = pageNo + p.getPageSize();
      } while (!p.isLastPage() && pageNo < p.getTotalSize());
      assertEquals("total no of pages", 6, p.getTotalPages());

      pageNo = 1;
      do {
        count = 0;
        p = products.search(1, t, "odd");
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

      assertEquals("Total no of documents left", 0, products.count());
    } catch (Exception e) {
      throw e;
    } finally {
      t.rollback();
    }
    // see any document exists
    assertFalse("all the documents are deleted", products.exists((long) 12));
  }

  @Test
  public void test5POJOSearchWithQueryDefinitionandTransaction() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> p;
    products.deleteAll();
    Transaction t = client.openTransaction();
    // Load more than 111 objects into different collections
    try {
      for (int i = 1; i < 112; i++) {
        if (i % 2 == 0) {
          products.write(this.getArtifact(i), t, "even", "numbers");
        }
        else {
          products.write(this.getArtifact(i), "odd", "numbers");
        }
      }
      assertEquals("Total number of object recods", 56, products.count("numbers"));
      assertEquals("Collection even count", 0, products.count("even"));
      assertEquals("Collection odd count", 56, products.count("odd"));

      products.setPageLength(10);
      QueryManager queryMgr = client.newQueryManager();
      StringQueryDefinition qd = queryMgr.newStringDefinition();
      qd.setCriteria("Acme");
      long pageNo = 1, count = 0;
      do {
        count = 0;
        p = products.search(qd, pageNo);
        while (p.iterator().hasNext()) {
          Artifact a = p.iterator().next();
          validateArtifact(a);
          count++;
        }
        assertEquals("Page size", count, p.size());
        pageNo = pageNo + p.getPageSize();
      } while (!p.isLastPage() && pageNo < p.getTotalSize());
      assertEquals("total no of pages", 6, p.getTotalPages());
      do {
        count = 0;
        p = products.search(qd, pageNo, t);
        while (p.iterator().hasNext()) {
          Artifact a = p.iterator().next();
          validateArtifact(a);
          count++;
        }
        assertEquals("Page size", count, p.size());
        pageNo = pageNo + p.getPageSize();
      } while (!p.isLastPage() && pageNo < p.getTotalSize());
      assertEquals("total no of pages", 12, p.getTotalPages());
      SearchHandle results = new SearchHandle();
      pageNo = 1;
      do {
        count = 0;
        p = products.search(qd, pageNo, results, t);
        while (p.iterator().hasNext()) {
          Artifact a = p.iterator().next();
          validateArtifact(a);
          // products.delete(a.getId());
          count++;
        }
        assertEquals("Page size", count, p.size());
        pageNo = pageNo + p.getPageSize();

      } while (!p.isLastPage());
      System.out.println(results.getTotalResults());
      assertEquals("Total no of documents ", 56, products.count());
    } catch (Exception e) {
      throw e;
    } finally {
      t.rollback();
    }
    // see any document exists
    assertFalse("all the documents are deleted", products.exists((long) 12));
  }

}
