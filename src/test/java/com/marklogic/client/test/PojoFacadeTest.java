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

import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import com.marklogic.client.pojo.PojoQueryBuilder;
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
            City city = iterator.next();
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
            City city = iterator.next();
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
            City city = iterator.next();
            numRead++;
        }
        assertEquals("Failed to find number of records expected", 50, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());

        query = qb.range("population", Operator.LT, 350000);
        page = cities.search(query, 1);
        iterator = page.iterator();
        numRead = 0;
        while ( iterator.hasNext() ) {
            City city = iterator.next();
            numRead++;
        }
        assertEquals("Failed to find number of records expected", 21, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());

        // TODO: uncomment tests below once geospatial on JSON is implemented in server
        /*
        query = qb.geospatial(
            qb.geoField("latLong"),
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
        // this currently doesn't work even in the cts:search layer
        // when this works we'll find out how many we expect
        assertEquals("Failed to find number of records expected", -1, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());

        query = qb.geospatial(
            qb.geoPath("latLong"),
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
        // this currently doesn't work even in the cts:search layer
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
        // this currently doesn't work even in the cts:search layer
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

        PojoQueryBuilder qb = cities.getQueryBuilder();
        PojoQueryBuilder countriesQb = qb.containerQuery("country");
        QueryDefinition query = countriesQb.value("continent", "EU");
        assertEquals("Should not find any countries", 0, cities.search(query, 1).getTotalSize());

        query = countriesQb.value("continent", "AS");
        assertEquals("Should find two cities", 2, cities.search(query, 1).getTotalSize());

        query = countriesQb.range("continent", Operator.EQ, "AS");
        assertEquals("Should find two cities", 2, cities.search(query, 1).getTotalSize());

        // all countries containing the term SA
        query = countriesQb.containerQuery(countriesQb.term("SA"));
        assertEquals("Should find one city", 1, cities.search(query, 1).getTotalSize());

        // all cities containing the term SA
        query = qb.containerQuery(qb.term("SA"));
        assertEquals("Should find two cities", 61, cities.search(query, 1).getTotalSize());

        // all countries containing the field "currencyName" with the term "peso"
        query = countriesQb.word("currencyName", "peso");
        assertEquals("Should find one city", 1, cities.search(query, 1).getTotalSize());
    }

    @Test
    public void testE_DeletePojos() throws Exception {
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
}
