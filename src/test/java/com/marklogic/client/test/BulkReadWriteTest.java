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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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

/** Loads data from cities15000.txt which contains every city above 15000 people, and adds
 * data from countryInfo.txt.
 **/
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BulkReadWriteTest {
    private static int BATCH_SIZE = 100;
    private static String DIRECTORY = "/cities/";
    private static String COUNTRIES_FILE = "countryInfo.txt";
    private static String CITIES_FILE = "cities_above_300K.txt";
    private static int RECORDS_EXPECTED = 1363;
    private static JAXBContext context = null;

    @BeforeClass
    public static void beforeClass() throws JAXBException {
        Common.connect();
        context = JAXBContext.newInstance(City.class);
    }
    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    static public class Country {
        private String name, continent, currencyCode, currencyName, isoCode;

        public String getIsoCode() {
            return isoCode;
        }

        public Country setIsoCode(String isoCode) {
            this.isoCode = isoCode;
            return this;
        }

        public String getName() {
            return name;
        }

        public Country setName(String name) {
            this.name = name;
            return this;
        }

        public String getContinent() {
            return continent;
        }

        public Country setContinent(String continent) {
            this.continent = continent;
            return this;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public Country setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public String getCurrencyName() {
            return currencyName;
        }

        public Country setCurrencyName(String currencyName) {
            this.currencyName = currencyName;
            return this;
        }
    }

    @XmlRootElement
    static public class City {
        private int geoNameId;
        private String name;
        private String asciiName;
        private String[] alternateNames;
        private double latitude;
        private double longitude;
        private String countryIsoCode;
        private String countryName;
        private String continent;
        private String currencyCode;
        private String currencyName;
        private long population;
        private int elevation;

        public int getGeoNameId() {
            return geoNameId;
        }

        public City setGeoNameId(int geoNameId) {
            this.geoNameId = geoNameId;
            return this;
        }

        public String getName() {
            return name;
        }

        public City setName(String name) {
            this.name = name;
            return this;
        }

        public String getAsciiName() {
            return asciiName;
        }

        public City setAsciiName(String asciiName) {
            this.asciiName = asciiName;
            return this;
        }

        public String[] getAlternateNames() {
            return alternateNames;
        }

        public City setAlternateNames(String[] alternateNames) {
            this.alternateNames = alternateNames;
            return this;
        }

        public double getLatitude() {
            return latitude;
        }

        public City setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public double getLongitude() {
            return longitude;
        }

        public City setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public String getCountryIsoCode() {
            return countryIsoCode;
        }

        public City setCountryIsoCode(String countryIsoCode) {
            this.countryIsoCode = countryIsoCode;
            return this;
        }

        public String getCountryName() {
            return countryName;
        }

        public City setCountryName(String countryName) {
            this.countryName = countryName;
            return this;
        }

        public String getContinent() {
            return continent;
        }

        public City setContinent(String continent) {
            this.continent = continent;
            return this;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public City setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public String getCurrencyName() {
            return currencyName;
        }

        public City setCurrencyName(String currencyName) {
            this.currencyName = currencyName;
            return this;
        }

        public long getPopulation() {
            return population;
        }

        public City setPopulation(long population) {
            this.population = population;
            return this;
        }

        public int getElevation() {
            return elevation;
        }

        public City setElevation(int elevation) {
            this.elevation = elevation;
            return this;
        }
    }

    @Test
    public void testBulkLoad() throws IOException, JAXBException {
        // register the POJO class
        DatabaseClientFactory.getHandleRegistry().register(
            JAXBHandle.newFactory(City.class)
        );
        XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

        JAXBContext context = JAXBContext.newInstance(City.class);

        // load all the countries into a HashMap (this isn't the big data set)
        // we'll attach country info to each city (that's the big data set)
        Map<String, Country> countries = new HashMap<String, Country>();
        System.out.println("1:" + BulkReadWriteTest.class.getClassLoader().getResourceAsStream(COUNTRIES_FILE));
        BufferedReader countryReader = new BufferedReader(Common.testFileToReader(COUNTRIES_FILE));
        String line;
        while ((line = countryReader.readLine()) != null ) {
            addCountry(line, countries);
        }
        countryReader.close();

        // write batches of cities combined with their country info
        DocumentWriteSet writeSet = docMgr.newWriteSet();
        System.out.println(BulkReadWriteTest.class.getClassLoader().getResourceAsStream(CITIES_FILE));
        BufferedReader cityReader = new BufferedReader(Common.testFileToReader(CITIES_FILE));
        line = null;
        long numWritten = 0;
        while ((line = cityReader.readLine()) != null ) {

            // instantiate the POJO for this city
            City city = newCity(line, countries);

            // set the handle to the POJO instance
            JAXBHandle<City> handle = new JAXBHandle<City>(context);
            handle.set(city);
            writeSet.add( DIRECTORY + city.getGeoNameId() + ".xml", handle );

            // when we have a full batch, write it out
            if ( ++numWritten % BATCH_SIZE == 0 ) {
                docMgr.write(writeSet);
                writeSet = docMgr.newWriteSet();
            }
        }
        // if there are any leftovers, let's write this last batch
        if ( numWritten % BATCH_SIZE > 0 ) {
            docMgr.write(writeSet);
        }
        cityReader.close();
        

        assertEquals("Number of records not expected", numWritten, RECORDS_EXPECTED);
    }

    @Test
    public void testBulkRead() throws IOException, JAXBException {
        XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

        DocumentPage page = docMgr.read(DIRECTORY + "1016670.xml", DIRECTORY + "108410.xml", DIRECTORY + "1205733.xml");
        int numRead = 0;
        while ( page.hasNext() ) {
            DocumentRecord record = page.next();
            validateRecord(record);
            numRead++;
        }
        assertEquals("Failed to read number of records expected", 3, numRead);
    }

    @Test
    public void testBulkSearch() throws IOException, JAXBException {
        XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

        SearchHandle searchHandle = new SearchHandle();
        int pageLength = 100;
        docMgr.setPageLength(pageLength);
        DocumentPage page = docMgr.search(new StructuredQueryBuilder().directory(1, DIRECTORY), 1, searchHandle);
        //DocumentPage page = docMgr.search(new StructuredQueryBuilder().directory(1, DIRECTORY), 1);
        while ( page.hasNext() ) {
            DocumentRecord record = page.next();
            validateRecord(record);
        }
        assertEquals("Failed to find number of records expected", RECORDS_EXPECTED, page.getTotalSize());
        assertEquals("SearchHandle failed to report number of records expected", RECORDS_EXPECTED, searchHandle.getTotalResults());
        assertEquals("SearchHandle failed to report pageLength expected", pageLength, searchHandle.getPageLength());
        cleanUp();
    }

    public void validateRecord(DocumentRecord record) {
        JAXBHandle<City> handle = new JAXBHandle<City>(context);
        assertNotNull("DocumentRecord should never be null", record);
        assertNotNull("Document uri should never be null", record.getUri());
        assertTrue("Document uri should start with " + DIRECTORY, record.getUri().startsWith(DIRECTORY));
        assertEquals("All records are expected to be XML format", Format.XML, record.getFormat());
        /*
        assertEquals("All records are expected to be mimetype application/xml", "application/xml", 
          record.getMimetype());
        */
        if ( record.getUri().equals(DIRECTORY + "1205733.xml") ) {
            City chittagong = record.getContent(handle).get();
            assertEquals("City name doesn't match", "Chittagong", chittagong.getName());
            assertEquals("City latitude doesn't match", 22.3384, chittagong.getLatitude(), 0);
            assertEquals("City longitude doesn't match", 91.83168, chittagong.getLongitude(), 0);
            assertEquals("City population doesn't match", 3920222, chittagong.getPopulation());
            assertEquals("City elevation doesn't match", 15, chittagong.getElevation());
            assertEquals("Currency code doesn't match", "BDT", chittagong.getCurrencyCode());
            assertEquals("Currency name doesn't match", "Taka", chittagong.getCurrencyName());
        }
    }

    private static void addCountry(String line, Map<String, Country> countries) {
        // skip comment lines
        if ( line.startsWith("#") ) return;

        // otherwise split on tabs and populate a country object 
        String[] fields = line.split("	");
        String isoCode = fields[0];
        countries.put(isoCode, new Country()
          .setIsoCode( isoCode )
          .setName( fields[4] )
          .setContinent( fields[8] )
          .setCurrencyCode( fields[10] )
          .setCurrencyName( fields[11] )
        );
    }

    private static Country getCountry(String isoCode, Map<String, Country> countries) {
        return countries.get(isoCode);
    }

    private static City newCity(String line, Map<String, Country> countries) {
        String[] fields = line.split("	");
        try {
            City city = new City()
              .setGeoNameId( Integer.parseInt(fields[0]) )
              .setName( fields[1] )
              .setAsciiName( fields[2] )
              .setAlternateNames( fields[3].split(",") );
            if ( !fields[4].equals("") ) city.setLatitude( Double.parseDouble(fields[4]) );
            if ( !fields[5].equals("") ) city.setLongitude( Double.parseDouble(fields[5]) );
            if ( !fields[14].equals("") ) city.setPopulation( Long.parseLong(fields[14]) );
            if ( !fields[16].equals("") ) city.setElevation( Integer.parseInt(fields[16]) );
            if ( !fields[8].equals("") ) {
                String isoCode = fields[8];
                Country country = getCountry(isoCode, countries);
                city.setCountryIsoCode(isoCode);
                city.setCountryName( country.getName()) ;
                city.setContinent( country.getContinent() );
                city.setCurrencyCode( country.getCurrencyCode() );
                city.setCurrencyName( country.getCurrencyName() );
            }
            return city;
        } catch (Throwable e) {
            System.err.println("Error parsing line:[" + line + "]");
            throw new IllegalStateException(e);
        }
    }

    private void cleanUp() {
        QueryManager queryMgr = Common.client.newQueryManager();
        DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
        deleteQuery.setDirectory("/cities/");
        queryMgr.delete(deleteQuery);
    }
}
