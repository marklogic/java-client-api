/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.functionaltest.Artifact;
import com.marklogic.client.functionaltest.ArtifactIndexedOnCalendar;
import com.marklogic.client.functionaltest.Company;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.pojo.PojoQueryBuilder.Operator;
import com.marklogic.client.pojo.PojoQueryDefinition;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.pojo.annotation.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.TimeZone;

public class TestPOJOReadWrite1 extends AbstractFunctionalTest {
  Calendar calOne = null;
  Calendar calTwo = null;

  /*
   * @Id annotation on String type
   */
  public static class StringPOJO {
    @Id
    public String name;

    public String getName() {
      return name;
    }

    public StringPOJO setName(String name) {
      this.name = name;
      return this;
    }
  }

  /*
   * @Id annotation on Integer type.
   */
  public static class IntegerPOJO {
    @Id
    public Integer id;

    public Integer getId() {
      return id;
    }

    public IntegerPOJO setId(Integer id) {
      this.id = id;
      return this;
    }
  }

  /*
   * @Id annotation on Calender type.
   */
  public static class CalendarPOJO {
    @Id
    public Calendar dateId;

    public Calendar getDateId() {
      return dateId;
    }

    public CalendarPOJO setDateId(Calendar dateId) {
      this.dateId = dateId;
      return this;
    }
  }

  /*
   * @Id annotation on double type.
   */
  public static class DoublePOJO {
    @Id
    public double doubleId;

    public double getDoubleId() {
      return doubleId;
    }

    public DoublePOJO setDoubleId(double doubleId) {
      this.doubleId = doubleId;
      return this;
    }
  }

  public static class NumberPOJO {
    @Id
    public Number nId;

    public Number getNumberId() {
      return nId;
    }

    public NumberPOJO setNumberId(Number nId) {
      this.nId = nId;
      return this;
    }
  }

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

  public void validateArtifact(Artifact art) {
    assertNotNull( art);
    assertNotNull( art.id);
    assertTrue( art.getInventory() > 1000);
  }

  // This test is to persist a simple design model objects in ML, read from ML,
  // delete all
  @Test
  public void testPOJOWrite() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    products.deleteAll();
    // Load more than 100 objects
    for (int i = 1; i < 112; i++) {
      products.write(this.getArtifact(i));
    }
    assertEquals( 111, products.count());
    for (long i = 1; i < 112; i++) {
      assertTrue(products.exists(i));
      this.validateArtifact(products.read(i));
    }
    products.deleteAll();
    for (long i = 1; i < 112; i++) {
      assertFalse(products.exists(i));
    }
  }

  // Issue 192 describes the use case
	@Test
  public void testPOJOReadInvalidId() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    products.deleteAll();
    // Load more than 100 objects
    for (int i = 1; i < 112; i++) {
      products.write(this.getArtifact(i));
    }
    assertEquals( 111, products.count());
	assertThrows(ResourceNotFoundException.class, () -> this.validateArtifact(products.read(1143l)));
    products.deleteAll();
  }

  // This test is to persist objects into different collections, read documents
  // based on Id and delete single object based on Id
  @Test
  public void testPOJOWriteWithCollection() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    // Load more than 110 objects into different collections
    products.deleteAll();
    for (int i = 112; i < 222; i++) {
      if (i % 2 == 0) {
        products.write(this.getArtifact(i), "even", "numbers");
      }
      else {
        products.write(this.getArtifact(i), "odd", "numbers");
      }
    }
    assertEquals( 110, products.count("numbers"));
    assertEquals( 55, products.count("even"));
    assertEquals( 55, products.count("odd"));
    for (long i = 112; i < 222; i++) {
      // validate all the records inserted are readable
      assertTrue(products.exists(i));
      this.validateArtifact(products.read(i));
    }
    products.delete((long) 112);
    assertFalse( products.exists((long) 112));
    products.deleteAll();
    // see any document exists
    for (long i = 112; i < 222; i++) {
      assertFalse(products.exists(i));
    }
    // see if it complains when there are no records
    products.delete((long) 112);
    products.deleteAll();
  }

  // This test is to read objects into pojo page based on Ids ,it has a scenario
  // for Issue 192
  // until #103 is resolved
  @Test
  public void testPOJOWriteWithPojoPage() {
    PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
    // Load more than 110 objects into different collections
    products.deleteAll();
    Long[] ids = new Long[112];
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
    ids[j] = (long) 1234234;
    j++;
    assertEquals( 111, products.count("numbers"));
    assertEquals( 56, products.count("even"));
    assertEquals( 55, products.count("odd"));

    System.out.println("Default Page length setting on docMgr :" + products.getPageLength());
    assertEquals( 50, products.getPageLength());

    PojoPage<Artifact> p = products.read(ids);
    // test for page methods
    System.out.println("Total number of estimated results:" + p.getTotalSize() + ids.length);
    System.out.println("Total number of estimated pages :" + p.getTotalPages());
    long count = 0;
    while (p.hasNext()) {
      this.validateArtifact(p.next());
      count++;
    }
    assertEquals( 111, count);
    products.deleteAll();
    // see any document exists
    for (long i = 112; i < 222; i++) {
      assertFalse(products.exists(i));
    }
    // see if it complains when there are no records
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
    PojoPage<Artifact> p = products.readAll(1);
    // test for page methods
    assertEquals( 1, p.size());
    System.out.println("Page size" + p.size());
    assertEquals( 1, p.getStart());
    System.out.println("Starting record in first page " + p.getStart());

    assertEquals(111, p.getTotalSize());
    System.out.println("Total number of estimated results:" + p.getTotalSize());
    assertEquals(111, p.getTotalPages());
    System.out.println("Total number of estimated pages :" + p.getTotalPages());
    assertTrue(p.isFirstPage());
    assertFalse(p.isLastPage());
    assertTrue(p.hasContent());
    // Need the Issue #75 to be fixed
    assertFalse( p.hasPreviousPage());
    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = products.readAll(pageNo);

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
    assertTrue( pageNo == p.getTotalPages());
    assertTrue( p.hasPreviousPage());
    assertEquals( 1, p.getPageSize());
    assertEquals( 111, p.getTotalSize());

    products.deleteAll();
    p = products.readAll(1);
    assertFalse( p.hasContent());
  }

  @Test
  public void testPOJOgetDocumentUri() {
    PojoRepository<StringPOJO, String> strPojoRepo = client.newPojoRepository(StringPOJO.class, String.class);
    StringPOJO sobj = new StringPOJO();

    sobj.setName("StringUri");
    strPojoRepo.write(sobj);
    System.out.println(strPojoRepo.getDocumentUri(sobj));
    assertEquals("com.marklogic.client.fastfunctest.TestPOJOReadWrite1$StringPOJO/StringUri.json", strPojoRepo.getDocumentUri(sobj));

    PojoRepository<IntegerPOJO, Integer> intPojoRepo = client.newPojoRepository(IntegerPOJO.class, Integer.class);
    IntegerPOJO iobj = new IntegerPOJO();
    iobj.setId(-12);
    intPojoRepo.write(iobj);
    System.out.println(intPojoRepo.getDocumentUri(iobj));
    assertEquals("com.marklogic.client.fastfunctest.TestPOJOReadWrite1$IntegerPOJO/-12.json", intPojoRepo.getDocumentUri(iobj));

    PojoRepository<CalendarPOJO, Calendar> calPojoRepo = client.newPojoRepository(CalendarPOJO.class, Calendar.class);
    CalendarPOJO cobj = new CalendarPOJO();
    Calendar cal = Calendar.getInstance();
    cobj.setDateId(cal);
    calPojoRepo.write(cobj);
    System.out.println(calPojoRepo.getDocumentUri(cobj));
    assertTrue(calPojoRepo.getDocumentUri(cobj).contains("com.marklogic.client.fastfunctest.TestPOJOReadWrite1$CalendarPOJO"));

    PojoRepository<DoublePOJO, Double> dPojoRepo = client.newPojoRepository(DoublePOJO.class, Double.class);
    DoublePOJO dobj = new DoublePOJO();
    double dvar = 2.015;
    dobj.setDoubleId(dvar);
    dPojoRepo.write(dobj);
    System.out.println(dPojoRepo.getDocumentUri(dobj));
    assertEquals("com.marklogic.client.fastfunctest.TestPOJOReadWrite1$DoublePOJO/2.015.json", dPojoRepo.getDocumentUri(dobj));

    PojoRepository<NumberPOJO, Number> nPojoRepo = client.newPojoRepository(NumberPOJO.class, Number.class);
    NumberPOJO nobj = new NumberPOJO();
    Number nvar = 99;
    nvar.intValue();
    nobj.setNumberId(nvar);
    nPojoRepo.write(nobj);
    System.out.println(nPojoRepo.getDocumentUri(nobj));
    assertEquals("com.marklogic.client.fastfunctest.TestPOJOReadWrite1$NumberPOJO/99.json", nPojoRepo.getDocumentUri(nobj));
  }

  // Below scenario is verifying range query from PojoQueryBuilder on Calendar
  // type.
  @Test
  public void testPOJORangeQueryOnCalendar() throws Exception {
    System.out.println("Running testPOJORangeQueryOnCalendar");

    PojoRepository<ArtifactIndexedOnCalendar, String> rangeQryRepos = client.newPojoRepository(ArtifactIndexedOnCalendar.class, String.class);
    PojoPage<ArtifactIndexedOnCalendar> p;

    loadSimplePojos(rangeQryRepos);
    PojoQueryBuilder qb = rangeQryRepos.getQueryBuilder();

    // Range query on equality.
    PojoQueryDefinition qdEQ = qb.range("expiryDate", Operator.EQ, calOne);
    JacksonHandle jh = new JacksonHandle();
    rangeQryRepos.setPageLength(56);
    p = rangeQryRepos.search(qdEQ, 1, jh);
    assertEquals( 1, p.getTotalPages());

    long pageNo = 1, count = 0;
    do {
      count = 0;
      p = rangeQryRepos.search(qdEQ, pageNo);
      while (p.hasNext()) {
        ArtifactIndexedOnCalendar a = p.next();

        assertTrue( a.getExpiryDate().getTime().equals(calOne.getTime()));
        assertTrue( a.getId() == 57);
        assertTrue( a.getInventory() == 1057);
        assertTrue( a.getName().equalsIgnoreCase("Cogs special 57"));
        assertTrue( a.getManufacturer().getLatitude() == 98.998);
        assertTrue( a.getManufacturer().getLongitude() == -30.966);
        assertTrue( a.getManufacturer().getName().equalsIgnoreCase("Acme special, Inc."));
        assertTrue( a.getManufacturer().getWebsite().equalsIgnoreCase("http://www.acme special.com"));

        count++;
      }
      assertEquals( count, p.size());
      pageNo = pageNo + p.getPageSize();
    } while (!p.isLastPage() && pageNo <= p.getTotalSize());
    assertEquals( 1, p.getPageNumber());
    assertEquals( 1, p.getTotalPages());

    // Range query on Greater than equal.
    PojoQueryDefinition qdGE = qb.range("expiryDate", Operator.GE, calTwo);
    JacksonHandle jhGE = new JacksonHandle();

    p = rangeQryRepos.search(qdGE, 1, jhGE);
    assertEquals( 1, p.getTotalPages());

    pageNo = 1;
    count = 0;
    boolean bFound = false;
    do {
      count = 0;
      p = rangeQryRepos.search(qdGE, pageNo);
      while (p.hasNext()) {
        ArtifactIndexedOnCalendar a = p.next();

        if (a.getId() == 97) {
          assertTrue( a.getExpiryDate().getTime().equals(calTwo.getTime()));
          assertTrue( a.getInventory() == 1097);
          assertTrue( a.getName().equalsIgnoreCase("Cogs special 97"));
          assertTrue( a.getManufacturer().getLatitude() == 138.998);
          assertTrue( a.getManufacturer().getLongitude() == 9.03400000000001);
          assertTrue( a.getManufacturer().getName().equalsIgnoreCase("Acme special, Inc."));
          assertTrue( a.getManufacturer().getWebsite().equalsIgnoreCase("http://www.acme special.com"));

          bFound = true;
        }
        count++;
      }
      // Assert if id = 97 is not found.
      if (!bFound) {
        fail("Range Query on Calendar type failed when greater than or equal used.");
      }
      assertEquals( 14, count);
      assertEquals( count, p.size());
      pageNo = pageNo + p.getPageSize();
    } while (!p.isLastPage() && pageNo <= p.getTotalSize());
    assertEquals( 1, p.getPageNumber());
    assertEquals( 1, p.getTotalPages());

    // Range query on Greater than. count should be 1 less than previous count.
    PojoQueryDefinition qdGT = qb.range("expiryDate", Operator.GT, calTwo);
    JacksonHandle jhGT = new JacksonHandle();

    p = rangeQryRepos.search(qdGT, 1, jhGT);
    assertEquals( 1, p.getTotalPages());

    pageNo = 1;
    count = 0;

    do {
      count = 0;
      p = rangeQryRepos.search(qdGT, pageNo);
      while (p.hasNext()) {
        ArtifactIndexedOnCalendar a = p.next();
        // Assert if id = 97 is found.
        if (a.getId() == 97) {
          fail("Range Query on Calendar type failed when greater than 97 used.");
        }
        count++;
      }

      assertEquals( 13, count);
      assertEquals( count, p.size());
      pageNo = pageNo + p.getPageSize();
    } while (!p.isLastPage() && pageNo <= p.getTotalSize());
    assertEquals( 1, p.getPageNumber());
    assertEquals( 1, p.getTotalPages());
  }

  public void loadSimplePojos(PojoRepository rangeQueryRepos) {
    for (int i = 1; i < 111; i++) {
      if (i % 2 == 0) {
        rangeQueryRepos.write(this.getArtifactIndexedOnCalendar(i), "even", "numbers");
      }
      else {
        rangeQueryRepos.write(this.getArtifactIndexedOnCalendar(i), "odd", "numbers");
      }
    }
  }

  public ArtifactIndexedOnCalendar getArtifactIndexedOnCalendar(int counter) {
    StringBuilder str = new StringBuilder();
    str.append("Cogs special");
    str.append(counter);

    ArtifactIndexedOnCalendar cogs = new ArtifactIndexedOnCalendar();
    cogs.setId(counter);
    cogs.setInventory(1000 + counter);

    // Time zone is UTC. Server time when POJO written is UTC. Without timezone
    // on client side
    // there will be difference on times between client and ML server values.
    Calendar expiryDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    if (counter % 57 == 0) {
      calOne = expiryDate;

      Company acme = new Company();
      acme.setName("Acme special, Inc.");
      acme.setWebsite("http://www.acme special.com");
      acme.setLatitude(41.998 + counter);
      acme.setLongitude(-87.966 + counter);
      cogs.setManufacturer(acme);
      cogs.setExpiryDate(expiryDate);
      cogs.setName("Cogs special 57");
    }
    else if (counter % 97 == 0) {
      calTwo = expiryDate;

      Company acme = new Company();
      acme.setName("Acme special, Inc.");
      acme.setWebsite("http://www.acme special.com");
      acme.setLatitude(41.998 + counter);
      acme.setLongitude(-87.966 + counter);
      cogs.setManufacturer(acme);
      cogs.setExpiryDate(expiryDate);
      cogs.setName("Cogs special 97");
    }
    else {
      Company widgets = new Company();
      widgets.setName(str.toString());
      widgets.setWebsite("http://www.widgets counter.com");
      widgets.setLatitude(41.998 + counter);
      widgets.setLongitude(-87.966 + counter);
      cogs.setManufacturer(widgets);
      cogs.setExpiryDate(expiryDate);
      cogs.setName(str.toString());
    }
    return cogs;
  }
}
