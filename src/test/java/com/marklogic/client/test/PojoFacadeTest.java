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

/*
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
*/

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.StringQueryDefinition;
/*
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
*/
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
        //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
        //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");    
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

    /*
    @Test
    public void testC_DeletePojos() throws Exception {
        repository.delete((String)null);
    }
    */

    private void validateCity(City city) {
        assertNotNull("City should never be null", city);
        assertNotNull("GeoNamId should never be null", city.getGeoNameId());
        if ( "Chittagong".equals(city.getName()) ) {
            BulkReadWriteTest.validateChittagong(city);
        }
    }
}
