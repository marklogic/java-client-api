/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.marklogic.client.DatabaseClient;
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

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PojoFacadeTest {
    private static final int MAX_TO_WRITE = 100;
    private static PojoRepository<City, Integer> cities;

    @BeforeClass
    public static void beforeClass() {
        Common.connect();
        cities = Common.client.newPojoRepository(City.class, Integer.class);
        //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
    }
    @AfterClass
    public static void afterClass() {
        cleanUp();
        Common.release();
    }

    public class PojoCityWriter implements CityWriter {
        private int numCities = 0;

        public void addCity(City city) {
            if ( numCities > MAX_TO_WRITE && !"Chittagong".equals(city.getName()) ) return;
            cities.write(city);
            numCities++;
        }
        public void finishBatch() {
        }
        public void setNumRecords(int numRecords) {
            assertEquals("Number of records not expected", numRecords, BulkReadWriteTest.RECORDS_EXPECTED);
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
        assertEquals("Student id", student1.getStudId(), stud.getStudId());
        assertEquals("Zip code", student1.getAddress().zip, stud.getAddress().zip);
        assertEquals("class status", student1.getclassStatus(), stud.getclassStatus());
    } 

    @Test
    public void testA_LoadPojos() throws Exception {
        BulkReadWriteTest.loadCities(new PojoCityWriter());
        assertEquals("total docs = MAX_TO_WRITE + Chittigong", MAX_TO_WRITE + 1, cities.count());
    }

    @Test
    public void testB_ReadPojos() throws Exception {
        PojoPage<City> page = cities.read(new Integer[]{1185098, 2239076, 1205733});
        try {
            int numRead = 0;
            for ( City city : page ) {
                validateCity(city);
                numRead++;
            }
            assertEquals("Failed to read number of records expected", 3, numRead);
            assertEquals("PojoPage failed to report number of records expected", numRead, page.size());
        } finally {
            page.close();
        }

        // test reading a valid plus a non-existent document
        page = cities.read(new Integer[]{1185098, -1});
        try {
            assertEquals("Should have results", true, page.hasContent());
            assertEquals("Failed to report number of records expected", 1, page.size());
            assertEquals("Wrong only doc", 1185098, page.next().getGeoNameId());
        } finally {
            page.close();
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
        assertTrue("ResourceNotFoundException should have been thrown", exceptionThrown);

        // test reading multiple non-existent documents, https://github.com/marklogic/java-client-api/issues/185
        // since this is a bulk API, we are consistent with search and no errors are thrown
        // on non-matching uris
        exceptionThrown = false;
        try {
            page = cities.read(new Integer[]{-1, -2});
            page.close();
            assertEquals(0, page.size());
        } catch (ResourceNotFoundException e) {
            exceptionThrown = true;
            e.printStackTrace();
        }
        assertFalse("ResourceNotFoundException should not have been thrown", exceptionThrown);
    }

    @SuppressWarnings("unused")
	@Test
    // the geo queries below currently don't work yet because underlying layers are not yet ready
    public void testC_QueryPojos() throws Exception {
		// first test a search that matches nothing
        StringQueryDefinition stringQuery = Common.client.newQueryManager().newStringDefinition();
        stringQuery.setCriteria("nonExistentStrangeWord");
        PojoPage<City> page = cities.search(stringQuery, 1);
        try {
            int numRead = 0;
            for ( City city : page ) numRead++;
            assertEquals("Failed to find number of records expected", 0, numRead);
            assertEquals("PojoPage failed to report number of records expected", numRead, page.size());
        } finally {
            page.close();
        }

        stringQuery = Common.client.newQueryManager().newStringDefinition();
        stringQuery.setCriteria("Tungi OR Dalatando OR Chittagong");
        page = cities.search(stringQuery, 1);
        try {
            int numRead = 0;
            for ( City city : page ) numRead++;
            assertEquals("Failed to find number of records expected", 3, numRead);
            assertEquals("PojoPage failed to report number of records expected", numRead, page.size());
        } finally {
            page.close();
        }


        PojoQueryBuilder<City> qb = cities.getQueryBuilder();
        PojoQueryDefinition query = qb.term("Tungi", "Dalatando", "Chittagong");
        page = cities.search(query, 1);
        try {
            int numRead = 0;
            for ( City city : page ) numRead++;
            assertEquals("Failed to find number of records expected", 3, numRead);
            assertEquals("PojoPage failed to report number of records expected", numRead, page.size());
        } finally {
            page.close();
        }

        // the default options are unfiltered, which only produce accurate results
        // when all necessary indexes are in place and we avoid queries that can't resolve directly from indexes
        // wildcarded queries will produce inaccurate results without a wildcard index in place
        //  - first lets's see it produce inaccurate results
        query = qb.word("asciiName", new String[] {"wildcarded"}, 1, "Chittagong*");
        page = cities.search(query, 1);
        page.close();
        assertEquals("The estimate number should match everything", 101, page.getTotalSize());

        // - the recommended way to deal with it is to enable indexes
        //   but assuming you can't or don't want to enable certain indexes
        //   (wildcard indexes for instance can have a speed impact even on non-wildcard queries)
        //   it may be better to just filter individual queries
        // - here we show the new recommended way to run individual queries filtered
        query = qb.filteredQuery(qb.word("asciiName", new String[] {"wildcarded"}, 1, "Chittagong*"));
        page = cities.search(query, 1);
        try {
            int numRead = 0;
            for ( City city : page ) numRead++;
            assertEquals("Failed to find number of records expected", 1, numRead);
            assertEquals("PojoPage failed to report number of records expected", numRead, page.size());
        } finally {
            page.close();
        }

        // - then let's show the old work-around using stored options
        QueryOptionsManager queryOptionsMgr =
            Common.newAdminClient().newServerConfigManager().newQueryOptionsManager();
        queryOptionsMgr.writeOptions("filtered", 
            new StringHandle("{\"options\":{\"search-option\":\"filtered\"}}").withFormat(Format.JSON));
        StructuredQueryBuilder sqb = Common.client.newQueryManager().newStructuredQueryBuilder("filtered");
        query = sqb.and(qb.word("asciiName", new String[] {"wildcarded"}, 1, "Chittagong*"));
        page = cities.search(query, 1);
        try {
            int numRead = 0;
            for ( City city : page ) numRead++;
            assertEquals("Failed to find number of records expected", 1, numRead);
            assertEquals("PojoPage failed to report number of records expected", numRead, page.size());
        } finally {
            page.close();
        }

        query = qb.value("continent", "AF");
        page = cities.search(query, 1);
        try {
            int numRead = 0;
            for ( City city : page ) {
                assertEquals("Wrong continent", "AF", city.getContinent());
                numRead++;
            }
            assertEquals("Failed to find number of records expected", 7, numRead);
            assertEquals("PojoPage failed to report number of records expected", numRead, page.size());
        } finally {
            page.close();
        }

        query = qb.containerQuery("alternateNames", qb.term("San", "Santo"));
        page = cities.search(query, 1);
        try {
            int numRead = 0;
            for ( City city : page ) {
                String alternateNames = Arrays.asList(city.getAlternateNames()).toString();
                assertTrue("Should contain San", alternateNames.contains("San"));
                numRead++;
            }
            assertEquals("Failed to find number of records expected", 11, numRead);
            assertEquals("PojoPage failed to report number of records expected", numRead, page.size());
        } finally {
            page.close();
        }

        // test numeric (integer) values
        query = qb.value("population", 374801);
        page = cities.search(query, 1);
        try {
            int numRead = 0;
            for ( City city : page ) {
                assertEquals("Wrong City", "Tirana", city.getName());
                numRead++;
            }
            assertEquals("Failed to find number of records expected", 1, numRead);
            assertEquals("PojoPage failed to report number of records expected", numRead, page.size());
        } finally {
            page.close();
        }

        // test numeric (fractional) values
        query = qb.and(qb.value("latitude", -34.72418), qb.value("longitude", -58.25265));
        page = cities.search(query, 1);
        try {
            int numRead = 0;
            for ( City city : page ) {
                assertEquals("Wrong City", "Quilmes", city.getName());
                numRead++;
            }
            assertEquals("Failed to find number of records expected", 1, numRead);
            assertEquals("PojoPage failed to report number of records expected", numRead, page.size());
        } finally {
            page.close();
        }

        // test null values
        query = qb.value("country", new String[] {null});
        page = cities.search(query, 1);
        try {
            int numRead = 0;
            for ( City city : page ) numRead++;
            assertEquals("Failed to find number of records expected", 50, numRead);
            assertEquals("PojoPage failed to report number of records expected", numRead, page.size());
        } finally {
            page.close();
        }

        query = qb.range("population", Operator.LT, 350000);
        page = cities.search(query, 1);
        try {
            int numRead = 0;
            for ( City city : page ) numRead++;
            assertEquals("Failed to find number of records expected", 21, numRead);
            assertEquals("PojoPage failed to report number of records expected", numRead, page.size());
        } finally {
            page.close();
        }

        query = qb.geospatial(
            qb.geoPath("latLong"),
            qb.circle(-34, -58, 100)
        );
        page = cities.search(query, 1);
        try {
            int numRead = 0;
            for ( City city : page ) numRead++;
            assertEquals("Failed to find number of records expected", 4, numRead);
            assertEquals("PojoPage failed to report number of records expected", numRead, page.size());
        } finally {
            page.close();
        }

        query = qb.geospatial(
            qb.geoPair("latitude", "longitude"),
            qb.circle(-34, -58, 100)
        );
        page = cities.search(query, 1);
        try {
            int numRead = 0;
            for ( City city : page ) numRead++;
            assertEquals("Failed to find number of records expected", 4, numRead);
            assertEquals("PojoPage failed to report number of records expected", numRead, page.size());
        } finally {
            page.close();
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
        PojoPage page = cities.search(query, 1);
        page.close();
        assertEquals("Should not find any countries", 0, page.getTotalSize());

        query = countriesQb.value("continent", "AS");
        page = cities.search(query, 1);
        page.close();
        assertEquals("Should find two cities", 2, page.getTotalSize());

        query = countriesQb.range("continent", Operator.EQ, "AS");
        page = cities.search(query, 1);
        page.close();
        assertEquals("Should find two cities", 2, page.getTotalSize());

        // all countries containing the term SA
        query = countriesQb.term("SA");
        page = cities.search(query, 1);
        page.close();
        assertEquals("Should find one city", 1, page.getTotalSize());

        // all cities containing the term SA
        query = qb.term("SA");
        page = cities.search(query, 1);
        page.close();
        assertEquals("Should find sixty one cities", 61, page.getTotalSize());

        // all countries containing the property "currencyName" with the term "peso"
        query = countriesQb.word("currencyName", "peso");
        page = cities.search(query, 1);
        page.close();
        assertEquals("Should find one city", 1, page.getTotalSize());
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
        PojoPage<Product1> page1 = products1.search(query, 1);
        page1.close();
        assertEquals("Should not find the product by id", 0, page1.getTotalSize());

        // though, of course, we can search on string field
        query = Common.client.newQueryManager().newStringDefinition();
        query.setCriteria("widget1");
        PojoPage<Product1> page2 = products1.search(query, 1);
        try {
            assertEquals("Should find the product by name", 1, page2.getTotalSize());
            assertEquals("Should find the right product id", 1001, page2.next().id);
        } finally {
            page2.close();
        }

        // with the JsonSerialize annotation, the id is indexed as a string and therefore searchable
        query = Common.client.newQueryManager().newStringDefinition();
        query.setCriteria("2001");
        PojoPage<Product2> page3 = products2.search(query, 1);
        try {
            assertEquals("Should find the product by id", 1, page3.getTotalSize());
            assertEquals("Should find the right product id", 2001, page3.next().id);
        } finally {
            page3.close();
        }
    }

    public static class TimeTest {     
        @Id public String id;
        public Calendar timeTest;

        public TimeTest() {}
        public TimeTest(String id, Calendar timeTest) {
            this.id = id;
            this.timeTest = timeTest;
        }
    }

    @Test
    public void testF_DateTime() {
        PojoRepository<TimeTest, String> times = Common.client.newPojoRepository(TimeTest.class, String.class);

        GregorianCalendar septFirst = new GregorianCalendar(TimeZone.getTimeZone("CET"));
        septFirst.set(2014, Calendar.SEPTEMBER, 1, 12, 0, 0);

        TimeTest timeTest1 = new TimeTest("1", septFirst);
        times.write(timeTest1);

        TimeTest timeTest1FromDb = times.read("1");
        assertEquals("Times should be equal", timeTest1.timeTest.getTime().getTime(), 
            timeTest1FromDb.timeTest.getTime().getTime());

        GregorianCalendar septFirstGMT = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        septFirstGMT.set(2014, Calendar.SEPTEMBER, 1, 12, 0, 0);

        TimeTest timeTest2 = new TimeTest("2", septFirstGMT);
        times.write(timeTest2);

        TimeTest timeTest2FromDb = times.read("2");
        assertEquals("Times should be equal", timeTest2.timeTest, timeTest2FromDb.timeTest);
    }

    @Test
    public void testG_DeletePojos() throws Exception {
        cities.delete(1185098, 2239076);
        StringQueryDefinition query = Common.client.newQueryManager().newStringDefinition();
        query.setCriteria("Tungi OR Dalatando OR Chittagong");
        PojoPage<City> page = cities.search(query, 1);
        page.close();
        assertEquals("Failed to read number of records expected", 1, page.getTotalSize());

        // now delete them all
        cities.deleteAll();
        long count = cities.count();
        assertEquals("Failed to read number of records expected", 0, count);
    }

    private void validateCity(City city) {
        assertNotNull("City should never be null", city);
        assertNotNull("GeoNamId should never be null", city.getGeoNameId());
        if ( "Chittagong".equals(city.getName()) ) {
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
