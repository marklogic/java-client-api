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
import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.test.BulkReadWriteTest;
import com.marklogic.client.test.BulkReadWriteTest.City;
import com.marklogic.client.test.BulkReadWriteTest.CityWriter;

import static com.marklogic.client.test.BulkReadWriteTest.DIRECTORY;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PojoFacadeTest {
    private static final int MAX_TO_WRITE = 100;
    private static PojoRepository<City, Integer> repository;

    @BeforeClass
    public static void beforeClass() {
        Common.connect();
        repository = Common.client.newPojoRepository(City.class, Integer.class);
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
    }
    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    public class PojoCityWriter implements CityWriter {
        private int numCities = 0;

        public void addCity(City city) {
            if ( numCities > MAX_TO_WRITE && !"Chittagong".equals(city.getName()) ) return;
            repository.write(city);
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
        //PojoPage<City> page = repository.read(1185098, 2239076, 1205733);
        StringQueryDefinition query = Common.client.newQueryManager().newStringDefinition();
        query.setCriteria("Tungi OR Dalatando OR Chittagong");
        PojoPage<City> page = repository.search(query, 1);
        Iterator<City> iterator = page.iterator();
        int numRead = 0;
        while ( iterator.hasNext() ) {
            City city = iterator.next();
            validateCity(city);
            numRead++;
        }
        assertEquals("Failed to read number of records expected", 3, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.getTotalSize());
    }

    @Test
    public void testC_QueryPojos() throws Exception {
        PojoQueryBuilder<City> qb = repository.getQueryBuilder();
        QueryDefinition query = qb.word("Tungi", "Dalatando", "Chittagong");
        PojoPage<City> page = repository.search(query, 1);
        Iterator<City> iterator = page.iterator();
        int numRead = 0;
        while ( iterator.hasNext() ) {
            numRead++;
        }
        assertEquals("Failed to find number of records expected", 3, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());

        qb = repository.getQueryBuilder();
        query = qb.value("continent", "AF");
        page = repository.search(query, 1);
        iterator = page.iterator();
        numRead = 0;
        while ( iterator.hasNext() ) {
            City city = iterator.next();
            assertEquals("Wrong continent", "AF", city.getContinent());
            numRead++;
        }
        assertEquals("Failed to find number of records expected", 7, numRead);
        assertEquals("PojoPage failed to report number of records expected", numRead, page.size());

        query = qb.containerQuery("alternateNames", qb.word("San", "Santo"));
        page = repository.search(query, 1);
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
    }

    @Test
    public void testD_DeletePojos() throws Exception {
        repository.delete(1185098, 2239076);
        StringQueryDefinition query = Common.client.newQueryManager().newStringDefinition();
        query.setCriteria("Tungi OR Dalatando OR Chittagong");
        PojoPage<City> page = repository.search(query, 1);
        assertEquals("Failed to read number of records expected", 1, page.getTotalSize());

        // now delete them all
        //repository.delete((String)null);
        long count = repository.count((String) null);
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
