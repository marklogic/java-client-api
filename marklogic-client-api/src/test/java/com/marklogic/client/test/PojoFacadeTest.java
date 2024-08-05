/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.impl.PojoRepositoryImpl;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.pojo.PojoQueryBuilder.Operator;
import com.marklogic.client.pojo.PojoQueryDefinition;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.test.BulkReadWriteTest.CityWriter;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class PojoFacadeTest {
  private static final int MAX_TO_WRITE = 100;
  private static PojoRepository<City, Integer> cities;

  @BeforeAll
  public static void beforeClass() {
    Common.connect();
    cities = Common.client.newPojoRepository(City.class, Integer.class);
  }
  @AfterAll
  public static void afterClass() {
    cleanUp();
  }

  public class PojoCityWriter implements CityWriter {
    private int numCities = 0;

    @Override
    public void addCity(City city) {
      if ( numCities > MAX_TO_WRITE && !"Chittagong".equals(city.getName()) ) return;
      cities.write(city);
      numCities++;
    }
    @Override
    public void finishBatch() {
    }
    @Override
    public void setNumRecords(int numRecords) {
      assertEquals( numRecords, BulkReadWriteTest.RECORDS_EXPECTED);
    }
  }

  public interface Citizen{
    public String getNationality();
    public void setNationality(String countryName);
  }
  public static class Address {
    public String streetName,state,country;
    public long zip;
    public double lattitude,longitude;
    public void setStreetName(String stName){
      this.streetName=stName;
    }
    public void setState(String stateName){
      this.state=stateName;
    }
    public void setCountry(String countryName){
      this.country=countryName;
    }
    public void setZip(long zipCode){
      this.zip=zipCode;
    }
    public void setLattitude(double lat){
      this.lattitude=lat;
    }
    public void setLongitude(double longitude){
      this.longitude=longitude;
    }
  }
  public static class Person implements Citizen {
    String name,emailId,nationality;
    long phone;
    Address address;
    public void setName(String name){this.name=name;}
    public void setAddress(Address address){this.address = address;}
    public void setEmailId(String emailId){ this.emailId = emailId;}
    public void setPhone(long ph){this.phone=ph;}

    @Override
    public void setNationality(String countryName){
      this.nationality= countryName;
    }
    @Override
    public String getNationality(){
      return this.nationality;
    }
    public String getName(){
      return this.name;
    }
    public Address getAddress(){
      return this.address;
    }
    public String getEmailId(){
      return this.emailId;
    }
    public long getPhone(){
      return this.phone;
    }
  }
  public enum std_status{senior,junior,fresher };
  public static class Student extends Person{
    @Id
    public long studId;
    std_status classStatus;

    public void setStudId(long id){
      this.studId=id;
    }
    public void setclassStatus(std_status classStatus){
      this.classStatus = classStatus;
    }
    public std_status getclassStatus(){
      return this.classStatus;
    }
    public long getStudId(){
      return this.studId;
    }
  }

  @Test
  public void testIssue_93() throws Exception {
    PojoRepository<Student,Long> students = Common.client.newPojoRepository(Student.class, Long.class);
    long id=1;
    Student stud = new Student();
    stud.setName("Student1");
    stud.setStudId(id);
    Address adr = new Address();
    adr.setCountry("USA");
    adr.setState("CA");
    adr.setZip(94070);
    adr.setStreetName("1 tassman");
    stud.setAddress(adr);
    stud.setEmailId("stud@gmail.com");
    stud.setPhone(6602345);
    stud.setNationality("Indian");
    stud.setclassStatus(std_status.junior);

    @SuppressWarnings("rawtypes")
    ObjectMapper objectMapper = ((PojoRepositoryImpl) students).getObjectMapper();
    String value = objectMapper.writeValueAsString(stud);
    objectMapper.readValue(value, Student.class);
    students.write(stud, "students");

    Student student1 = students.read(id);
    assertEquals( student1.getStudId(), stud.getStudId());
    assertEquals( student1.getAddress().zip, stud.getAddress().zip);
    assertEquals( student1.getclassStatus(), stud.getclassStatus());
  }

  @Test
  public void testA_LoadPojos() throws Exception {
    BulkReadWriteTest.loadCities(new PojoCityWriter());
    assertEquals(MAX_TO_WRITE + 1, cities.count());
  }

  @Test
  public void testB_ReadPojos() throws Exception {
    try ( PojoPage<City> page = cities.read(new Integer[]{1185098, 2239076, 1205733}) ) {
      int numRead = 0;
      for ( City city : page ) {
        validateCity(city);
        numRead++;
      }
      assertEquals( 3, numRead);
      assertEquals( numRead, page.size());
    }

    // test reading a valid plus a non-existent document
    try ( PojoPage<City> page = cities.read(new Integer[]{1185098, -1}) ) {
      assertEquals( true, page.hasContent());
      assertEquals( 1, page.size());
      assertEquals( 1185098, page.next().getGeoNameId());
    }

    // test reading only a non-existent document
    // since this is single document read, we are consistent with search and an error is thrown
    // on non-matching uri
    boolean exceptionThrown = false;
    try {
      cities.read(-1);
    } catch (ResourceNotFoundException e) {
      exceptionThrown = true;
    }
    assertTrue( exceptionThrown);

    // test reading multiple non-existent documents, https://github.com/marklogic/java-client-api/issues/185
    // since this is a bulk API, we are consistent with search and no errors are thrown
    // on non-matching uris
    exceptionThrown = false;
    try ( PojoPage<City> page = cities.read(new Integer[]{-1, -2}) ) {
      assertEquals(0, page.size());
    } catch (ResourceNotFoundException e) {
      exceptionThrown = true;
      e.printStackTrace();
    }
    assertFalse( exceptionThrown);
  }

  @SuppressWarnings("unused")
  @Test
  // the geo queries below currently don't work yet because underlying layers are not yet ready
  public void testC_QueryPojos() throws Exception {
    // first test a search that matches nothing
    StringQueryDefinition stringQuery = Common.client.newQueryManager().newStringDefinition();
    stringQuery.setCriteria("nonExistentStrangeWord");
    try ( PojoPage<City> page = cities.search(stringQuery, 1) ) {
      int numRead = 0;
      for ( City city : page ) numRead++;
      assertEquals( 0, numRead);
      assertEquals( numRead, page.size());
      assertEquals(
        numRead, cities.count(stringQuery));
    }

    stringQuery = Common.client.newQueryManager().newStringDefinition();
    stringQuery.setCriteria("Tungi OR Dalatando OR Chittagong");
    try ( PojoPage<City> page = cities.search(stringQuery, 1) ) {
      int numRead = 0;
      for ( City city : page ) numRead++;
      assertEquals( 3, numRead);
      assertEquals( numRead, page.size());
      assertEquals(
        numRead, cities.count(stringQuery));
    }


    PojoQueryBuilder<City> qb = cities.getQueryBuilder();
    PojoQueryDefinition query = qb.term("Tungi", "Dalatando", "Chittagong");
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      int numRead = 0;
      for ( City city : page ) numRead++;
      assertEquals( 3, numRead);
      assertEquals( numRead, page.size());
      assertEquals(
        numRead, cities.count(query));
    }


    query = cities.getQueryBuilder()
      .term("Tungi", "Dalatando", "Chittagong")
      .withCriteria("Chittagong");
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      int numRead = 0;
      for ( City city : page ) numRead++;
      assertEquals( 1, numRead);
      assertEquals( numRead, page.size());
      assertEquals(
        numRead, cities.count(query));
    }

    // the default options are unfiltered, which only produce accurate results
    // when all necessary indexes are in place and we avoid queries that can't resolve directly from indexes
    // wildcarded queries will produce inaccurate results without a wildcard index in place
    //  - first lets's see it produce inaccurate results
    query = qb.word("asciiName", new String[] {"wildcarded"}, 1, "Chittagong*");
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      assertEquals( 101, page.getTotalSize());
      assertEquals(
        101, cities.count(query));
    }

    // - the recommended way to deal with it is to enable indexes
    //   but assuming you can't or don't want to enable certain indexes
    //   (wildcard indexes for instance can have a speed impact even on non-wildcard queries)
    //   it may be better to just filter individual queries
    // - here we show the new recommended way to run individual queries filtered
    query = qb.filteredQuery(qb.word("asciiName", new String[] {"wildcarded"}, 1, "Chittagong*"));
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      int numRead = 0;
      for ( City city : page ) numRead++;
      assertEquals( 1, numRead);
      assertEquals( numRead, page.size());
      assertEquals(
        1, page.getTotalSize());
      assertEquals(
        101, cities.count(query));
    }

    long defaultPageLength = cities.getPageLength();
    cities.setPageLength(0);
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      assertEquals(
        101, page.getTotalSize());
    }
    cities.setPageLength(defaultPageLength);

    // - then let's show the old work-around using stored options
    QueryOptionsManager queryOptionsMgr =
      Common.newRestAdminClient().newServerConfigManager().newQueryOptionsManager();
    queryOptionsMgr.writeOptions("filtered",
      new StringHandle("{\"options\":{\"search-option\":\"filtered\"}}").withFormat(Format.JSON));

    StructuredQueryBuilder sqb = Common.client.newQueryManager().newStructuredQueryBuilder("filtered");
    query = sqb.and(qb.word("asciiName", new String[] {"wildcarded"}, 1, "Chittagong*"));
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      int numRead = 0;
      for ( City city : page ) numRead++;
      assertEquals( 1, numRead);
      assertEquals( numRead, page.size());
      assertEquals(
        101, cities.count(query));
    }

    query = qb.value("continent", "AF");
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      int numRead = 0;
      for ( City city : page ) {
        assertEquals( "AF", city.getContinent());
        numRead++;
      }
      assertEquals( 7, numRead);
      assertEquals( numRead, page.size());
    }

    query = qb.containerQuery("alternateNames", qb.term("San", "Santo"));
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      int numRead = 0;
      for ( City city : page ) {
        String alternateNames = Arrays.asList(city.getAlternateNames()).toString();
        assertTrue( alternateNames.contains("San"));
        numRead++;
      }
      assertEquals( 11, numRead);
      assertEquals( numRead, page.size());
      assertEquals(
        numRead, cities.count(query));
    }


    // test numeric (integer) values
    query = qb.value("population", 374801);
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      int numRead = 0;
      for ( City city : page ) {
        assertEquals( "Tirana", city.getName());
        numRead++;
      }
      assertEquals( 1, numRead);
      assertEquals( numRead, page.size());
    }

    // test numeric (fractional) values
    query = qb.and(qb.value("latitude", -34.72418), qb.value("longitude", -58.25265));
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      int numRead = 0;
      for ( City city : page ) {
        assertEquals( "Quilmes", city.getName());
        numRead++;
      }
      assertEquals( 1, numRead);
      assertEquals( numRead, page.size());
    }

    // test null values
    query = qb.value("country", new String[] {null});
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      int numRead = 0;
      for ( City city : page ) numRead++;
      assertEquals( 50, numRead);
      assertEquals( numRead, page.size());
    }

    query = qb.range("population", Operator.LT, 350000);
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      int numRead = 0;
      for ( City city : page ) numRead++;
      assertEquals( 21, numRead);
      assertEquals( numRead, page.size());
    }

    query = qb.geospatial(
      qb.geoPath("latLong"),
      qb.circle(-34, -58, 100)
    );
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      int numRead = 0;
      for ( City city : page ) numRead++;
      assertEquals( 4, numRead);
      assertEquals( numRead, page.size());
    }

    query = qb.geospatial(
      qb.geoPair("latitude", "longitude"),
      qb.circle(-34, -58, 100)
    );
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      int numRead = 0;
      for ( City city : page ) numRead++;
      assertEquals( 4, numRead);
      assertEquals( numRead, page.size());
    }
  }

  @Test
  public void testD_PojosWithChildren() throws Exception {
    City dubai = cities.read(292223);
    City abuDhabi = cities.read(292968);
    City buenosAires = cities.read(3435910);

    // test that we're generating the uris we expect
    assertEquals("com.marklogic.client.test.City/292223.json",
      cities.getDocumentUri(dubai));

    Country ae = new Country()
      .setIsoCode("AE")
      .setName("United Arab Emirates")
      .setContinent("AS")
      .setCurrencyCode("AED")
      .setCurrencyName("Dirham");
    dubai.setCountry( ae );
    abuDhabi.setCountry( ae );

    Country argentina = new Country()
      .setIsoCode("AR")
      .setName("Argentina")
      .setContinent("SA")
      .setCurrencyCode("ARS")
      .setCurrencyName("Peso");
    buenosAires.setCountry( argentina );

    cities.write(dubai);
    cities.write(abuDhabi);
    cities.write(buenosAires);

    PojoQueryBuilder<City> qb = cities.getQueryBuilder();
    PojoQueryBuilder<Country> countriesQb = qb.containerQueryBuilder("country", Country.class);
    PojoQueryDefinition query = countriesQb.value("continent", "EU");
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      assertEquals( 0, page.getTotalSize());
    }

    query = countriesQb.value("continent", "AS");
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      assertEquals( 2, page.getTotalSize());
    }

    query = countriesQb.range("continent", Operator.EQ, "AS");
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      assertEquals( 2, page.getTotalSize());
    }

    // all countries containing the term SA
    query = countriesQb.term("SA");
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      assertEquals( 1, page.getTotalSize());
    }

    // all cities containing the term SA
    query = qb.term("SA");
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      assertEquals( 61, page.getTotalSize());
    }

    // all countries containing the property "currencyName" with the term "peso"
    query = countriesQb.word("currencyName", "peso");
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      assertEquals( 1, page.getTotalSize());
    }
  }

  static public class Product1 {
    @Id
    public int id;
    public String name;
  }

  static public class Product2 {
    @Id
    @JsonSerialize(using=ToStringSerializer.class)
    public int id;
    public String name;
  }

  @Test
  public void testE_IndexNumberAsString() throws Exception {
    // without the JsonSerialize annotation, this id indexes as a nubmer and is not searchable
    Product1 widget1 = new Product1();
    widget1.id = 1001;
    widget1.name = "widget1";
    PojoRepository<Product1, Integer> products1 = Common.client.newPojoRepository(Product1.class, Integer.class);
    products1.write(widget1);

    Product2 widget2 = new Product2();
    widget2.id = 2001;
    widget2.name = "widget2";
    PojoRepository<Product2, Integer> products2 = Common.client.newPojoRepository(Product2.class, Integer.class);
    products2.write(widget2);

    StringQueryDefinition query = Common.client.newQueryManager().newStringDefinition();
    query.setCriteria("1001");
    try ( PojoPage<Product1> page = products1.search(query, 1) ) {
      assertEquals( 0, page.getTotalSize());
    }

    // though, of course, we can search on string field
    query = Common.client.newQueryManager().newStringDefinition();
    query.setCriteria("widget1");
    try ( PojoPage<Product1> page = products1.search(query, 1) ) {
      assertEquals( 1, page.getTotalSize());
      assertEquals( 1001, page.next().id);
    }

    // with the JsonSerialize annotation, the id is indexed as a string and therefore searchable
    query = Common.client.newQueryManager().newStringDefinition();
    query.setCriteria("2001");
    try ( PojoPage<Product2> page = products2.search(query, 1) ) {
      assertEquals( 1, page.getTotalSize());
      assertEquals( 2001, page.next().id);
    }
  }

  @Test
  public void testF_DateTime() {
    PojoRepository<TimeTest, String> times = Common.client.newPojoRepository(TimeTest.class, String.class);

    GregorianCalendar septFirstUTC = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    septFirstUTC.set(2014, Calendar.SEPTEMBER, 1, 12, 0, 0);

    TimeTest timeTest1 = new TimeTest("1", septFirstUTC);
    times.write(timeTest1);

    TimeTest timeTest1FromDb = times.read("1");
    assertEquals( timeTest1.calendarTest,
      timeTest1FromDb.calendarTest);
    assertEquals( timeTest1.dateTest,
      timeTest1FromDb.dateTest);
    assertEquals( timeTest1.dateTest.getTime(),
      timeTest1FromDb.dateTest.getTime());

    GregorianCalendar septThirdCET = new GregorianCalendar(TimeZone.getTimeZone("CET"));
    septThirdCET.set(2014, Calendar.SEPTEMBER, 3, 12, 0, 0);

    TimeTest timeTest2 = new TimeTest("2", septThirdCET);
    times.write(timeTest2);

    TimeTest timeTest2FromDb = times.read("2");
        /* Jackson 2.8.3 converts all Date/Calendar to UTC, so we can't get the object in the correct timezone
        assertEquals( timeTest2.calendarTest,
            timeTest2FromDb.calendarTestCet);
        */
    assertEquals( timeTest2.calendarTest.getTime().getTime(),
      timeTest2FromDb.calendarTestCet.getTime().getTime());
    assertEquals( timeTest2.dateTest,
      timeTest2FromDb.dateTest);
    assertEquals( timeTest2.calendarTest.getTime().getTime(),
      timeTest2FromDb.calendarTest.getTime().getTime());

    // let's try to test serializing back to CET time zone
        /* nevermind, it turns out Jackson 2.8.3 doesn't yet support this--it converts all dates to UTC

        // start with the ISO 8601 format compatible with xs:dateTime and thus MarkLogic
        SimpleDateFormat cetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        cetDateFormat.setTimeZone(TimeZone.getTimeZone("CET"));
        // modify the objectMapper for this PojoRepository instance
        ((PojoRepositoryImpl) times).getObjectMapper().setDateFormat(cetDateFormat);
        // re-read the object with the modified objectMapper
        timeTest2FromDb = times.read("2");
        // now validate that the object has everything including the time zone equal
        assertEquals( timeTest2.calendarTest,
            timeTest2FromDb.calendarTest);
        */

    // let's test a range query that should only match record "2"
    GregorianCalendar septSecondUTC = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    septSecondUTC.set(2014, Calendar.SEPTEMBER, 2, 12, 0, 0);

    PojoQueryBuilder<TimeTest> qb = times.getQueryBuilder();
    for ( String jsonProperty : new String[] {"calendarTest", "calendarTestCet", "dateTest"} ) {
      PojoQueryDefinition query = qb.range(jsonProperty, Operator.GT, septSecondUTC);
      try ( PojoPage<TimeTest> page = times.search(query, 1) ) {
        int numRead = 0;
        for ( TimeTest time : page ) {
          numRead++;
          assertEquals( "2", time.id);
        }
        assertEquals( 1, numRead);
        assertEquals( numRead, page.size());
      }
    }

  }

    /* TODO: uncomment when we have a fix for https://github.com/marklogic/java-client-api/issues/383
    @Test
    public void testG_GithubIssue383() {
        PojoQueryBuilder<City> qb = cities.getQueryBuilder();
        PojoQueryDefinition query = qb.range("alternateNames", Operator.EQ, "San", "Santo");
        try ( PojoPage<City> page = cities.search(query, 1) ) {
            int numRead = 0;
            for ( City city : page ) numRead++;
            assertEquals( 11, numRead);
            assertEquals( numRead, page.size());
        }

    }
    */

  @Test
  public void testH_DeletePojos() throws Exception {
    cities.delete(1185098, 2239076);
    StringQueryDefinition query = Common.client.newQueryManager().newStringDefinition();
    query.setCriteria("Tungi OR Dalatando OR Chittagong");
    try ( PojoPage<City> page = cities.search(query, 1) ) {
      assertEquals( 1, page.getTotalSize());
    }

    // now delete them all
    cities.deleteAll();
    long count = cities.count();
    assertEquals( 0, count);
  }

  private void validateCity(City city) {
    assertNotNull( city);
    assertNotNull( city.getGeoNameId());
    if ( "Chittagong".equals(city.getName()) ) {
      assertEquals( 1205733, cities.getId(city).intValue());
      BulkReadWriteTest.validateChittagong(city);
    }
  }

  private static void cleanUp() {
    PojoRepository<Product1, Integer> products1 = Common.client.newPojoRepository(Product1.class, Integer.class);
    PojoRepository<Product2, Integer> products2 = Common.client.newPojoRepository(Product2.class, Integer.class);
    products1.deleteAll();
    products2.deleteAll();
    PojoRepository<TimeTest, String> timeTests = Common.client.newPojoRepository(TimeTest.class, String.class);
    timeTests.deleteAll();
    PojoRepository<Student, Long> students = Common.client.newPojoRepository(Student.class, Long.class);
    students.deleteAll();
    cities.deleteAll();
  }
}
