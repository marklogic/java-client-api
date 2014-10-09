/*
 * Copyright 2012-2014 MarkLogic Corporation
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.pojo.PojoQueryBuilder.Operator;
import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.test.BulkReadWriteTest;
import com.marklogic.client.test.BulkReadWriteTest.CityWriter;

import static com.marklogic.client.test.BulkReadWriteTest.DIRECTORY;

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
    
    @Test
    public void testA_LoadPojos() throws Exception {
        BulkReadWriteTest.loadCities(new PojoCityWriter());
    }

    @Test
    public void testB_ReadPojos() throws Exception {
        PojoPage<City> page = cities.read(new Integer[]{1185098, 2239076, 1205733});
        Iterator<City> iterator = page.iterator();
        int numRead = 0;
        while ( iterator.hasNext() ) {
            City city = iterator.next();
            validateCity(city);
            numRead++;
        }
        assertEquals("Failed to read number of records expected", 3, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());
    }

    @Test
    // the geo queries below currently don't work yet because underlying layers are not yet ready
    public void testC_QueryPojos() throws Exception {
        StringQueryDefinition stringQuery = Common.client.newQueryManager().newStringDefinition();
        stringQuery.setCriteria("Tungi OR Dalatando OR Chittagong");
        PojoPage<City> page = cities.search(stringQuery, 1);
        Iterator<City> iterator = page.iterator();
        int numRead = 0;
        while ( iterator.hasNext() ) {
            iterator.next();
            numRead++;
        }
        assertEquals("Failed to find number of records expected", 3, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());


        PojoQueryBuilder<City> qb = cities.getQueryBuilder();
        QueryDefinition query = qb.term("Tungi", "Dalatando", "Chittagong");
        page = cities.search(query, 1);
        iterator = page.iterator();
        numRead = 0;
        while ( iterator.hasNext() ) {
            iterator.next();
            numRead++;
        }
        assertEquals("Failed to find number of records expected", 3, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());

        query = qb.value("continent", "AF");
        page = cities.search(query, 1);
        iterator = page.iterator();
        numRead = 0;
        while ( iterator.hasNext() ) {
            City city = iterator.next();
            assertEquals("Wrong continent", "AF", city.getContinent());
            numRead++;
        }
        assertEquals("Failed to find number of records expected", 7, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());

        query = qb.containerQuery("alternateNames", qb.term("San", "Santo"));
        page = cities.search(query, 1);
        iterator = page.iterator();
        numRead = 0;
        while ( iterator.hasNext() ) {
            City city = iterator.next();
            String alternateNames = Arrays.asList(city.getAlternateNames()).toString();
            assertTrue("Should contain San", alternateNames.contains("San"));
            numRead++;
        }
        assertEquals("Failed to find number of records expected", 11, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());

        // test numeric (integer) values
        query = qb.value("population", 374801);
        page = cities.search(query, 1);
        iterator = page.iterator();
        numRead = 0;
        while ( iterator.hasNext() ) {
            City city = iterator.next();
            assertEquals("Wrong City", "Tirana", city.getName());
            numRead++;
        }
        assertEquals("Failed to find number of records expected", 1, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());

        // test numeric (fractional) values
        query = qb.and(qb.value("latitude", -34.72418), qb.value("longitude", -58.25265));
        page = cities.search(query, 1);
        iterator = page.iterator();
        numRead = 0;
        while ( iterator.hasNext() ) {
            City city = iterator.next();
            assertEquals("Wrong City", "Quilmes", city.getName());
            numRead++;
        }
        assertEquals("Failed to find number of records expected", 1, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());

        // test null values
        query = qb.value("country", new String[] {null});
        page = cities.search(query, 1);
        iterator = page.iterator();
        numRead = 0;
        while ( iterator.hasNext() ) {
            iterator.next();
            numRead++;
        }
        assertEquals("Failed to find number of records expected", 50, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());

        query = qb.range("population", Operator.LT, 350000);
        page = cities.search(query, 1);
        iterator = page.iterator();
        numRead = 0;
        while ( iterator.hasNext() ) {
            iterator.next();
            numRead++;
        }
        assertEquals("Failed to find number of records expected", 21, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());

        // the default options are unfiltered, which I don't want in this case, so the 
        // work-around is to use stored options which are filtered
        StructuredQueryBuilder sqb = Common.client.newQueryManager().newStructuredQueryBuilder("facets");
        query = sqb.geospatial(
            qb.geoPath("latLong"),
            qb.circle(-34, -58, 100)
        );
        page = cities.search(query, 1);
        iterator = page.iterator();
        numRead = 0;
        while ( iterator.hasNext() ) {
            iterator.next();
            numRead++;
        }
        assertEquals("Failed to find number of records expected", 4, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());

        // TODO: uncomment tests below once https://bugtrack.marklogic.com/29731 is fixed
        /*
        query = qb.geospatial(
            qb.geoProperty("latLong"),
            qb.circle(-34, -58, 1)
        );
        page = cities.search(query, 1);
        iterator = page.iterator();
        numRead = 0;
        while ( iterator.hasNext() ) {
            @SuppressWarnings("unused")
            City city = iterator.next();
            numRead++;
        }
        // this currently doesn't work in the search:search layer
        // when this works we'll find out how many we expect
        assertEquals("Failed to find number of records expected", -1, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());


        query = qb.geospatial(
            qb.geoPair("latitude", "longitude"),
            qb.circle(-34, -58, 100)
        );
        page = cities.search(query, 1);
        iterator = page.iterator();
        numRead = 0;
        while ( iterator.hasNext() ) {
            @SuppressWarnings("unused")
            City city = iterator.next();
            numRead++;
        }
        // this currently doesn't work even in the search:search layer
        // when this works we'll find out how many we expect
        assertEquals("Failed to find number of records expected", -1, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());
        */
    }

    @Test
    public void testD_PojosWithChildren() throws Exception {
        City dubai = cities.read(292223);
        City abuDhabi = cities.read(292968);
        City buenosAires = cities.read(3435910);

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
        QueryDefinition query = countriesQb.value("continent", "EU");
        assertEquals("Should not find any countries", 0, cities.search(query, 1).getTotalSize());

        query = countriesQb.value("continent", "AS");
        assertEquals("Should find two cities", 2, cities.search(query, 1).getTotalSize());

        query = countriesQb.range("continent", Operator.EQ, "AS");
        assertEquals("Should find two cities", 2, cities.search(query, 1).getTotalSize());

        // all countries containing the term SA
        query = countriesQb.term("SA");
        assertEquals("Should find one city", 1, cities.search(query, 1).getTotalSize());

        // all cities containing the term SA
        query = qb.term("SA");
        assertEquals("Should find sixty one cities", 61, cities.search(query, 1).getTotalSize());

        // all countries containing the property "currencyName" with the term "peso"
        query = countriesQb.word("currencyName", "peso");
        assertEquals("Should find one city", 1, cities.search(query, 1).getTotalSize());
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
        assertEquals("Should not find the product by id", 0, page1.getTotalSize());

        // though, of course, we can search on string field
        query = Common.client.newQueryManager().newStringDefinition();
        query.setCriteria("widget1");
        PojoPage<Product1> page2 = products1.search(query, 1);
        assertEquals("Should find the product by name", 1, page2.getTotalSize());
        assertEquals("Should find the right product id", 1001, page2.next().id);

        // with the JsonSerialize annotation, the id is indexed as a string and therefore searchable
        query = Common.client.newQueryManager().newStringDefinition();
        query.setCriteria("2001");
        PojoPage<Product2> page3 = products2.search(query, 1);
        assertEquals("Should find the product by id", 1, page3.getTotalSize());
        assertEquals("Should find the right product id", 2001, page3.next().id);
    }


    @Test
    public void testF_DeletePojos() throws Exception {
        cities.delete(1185098, 2239076);
        StringQueryDefinition query = Common.client.newQueryManager().newStringDefinition();
        query.setCriteria("Tungi OR Dalatando OR Chittagong");
        PojoPage<City> page = cities.search(query, 1);
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
        cities.deleteAll();
    }
}
