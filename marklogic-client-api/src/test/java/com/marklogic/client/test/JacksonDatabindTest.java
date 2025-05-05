/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonDatabindHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.test.BulkReadWriteTest.CityWriter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;

public class JacksonDatabindTest {
  private static final String CITIES_FILE = "cities_above_300K.txt";
  private static final int MAX_TO_WRITE = 10;
  private static final String DIRECTORY = "/databindTest/";
  private static DatabaseClient client;

  @BeforeAll
  public static void beforeClass() {
    // demonstrate our ability to set advanced configuration on a mapper
    ObjectMapper mapper = new ObjectMapper();
    // in this case, we're saying wrap our serialization with the name of the pojo class
    mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT);
    // register a JacksonDatabindHandleFactory ready to marshall any City object to/from json
    // this enables the writeAs method below
    DatabaseClientFactory.getHandleRegistry().register(
      JacksonDatabindHandle.newFactory(mapper, City.class)
    );
    // we cannot use the singleton DatabaseClient here because this test requires
    // a DatabaseClient created after calling getHandleRegistry().register() with City.class
    client = Common.newClient();
  }

  @AfterAll
  public static void afterClass() {
    cleanUp();
  }

  /** Here we're trying to keep it simple and demonstrate how you would use Jackson
   * via JacksonDatabindHandle to do the most common-case databinding to serialize your
   * pojos to json.  JacksonDatabindHandle is created under the hood by the factory registered
   * above in the beforeClass() method. To reuse existing code we're letting BulkReadWriteTest load
   * records from a csv file and populate our City pojos.  We just manage the
   * serialization and persistence logic.  We're also demonstrating how to register
   * a factory so you can use the convenient writeAs method.
   **/
  public class JsonCityWriter implements CityWriter {
    private int numCities = 0;
    private JSONDocumentManager docMgr = client.newJSONDocumentManager();

    @Override
    public void addCity(City city) {
      if ( numCities >= MAX_TO_WRITE ) return;
      docMgr.writeAs(DIRECTORY + "/jsonCities/" + city.getGeoNameId() + ".json", city);
      numCities++;
    }
    @Override
    public void finishBatch() {}
    @Override
    public void setNumRecords(int numRecords) {}
  }

  @Test
  public void testDatabind() throws Exception {
    BulkReadWriteTest.loadCities(new JsonCityWriter());
    // we can add assertions later, for now this test just serves as example code and
    // ensures no exceptions are thrown
  }

  /** We're going to demonstrate the versitility of Jackson by using and XmlMapper
   * to serialize instead of the default JsonMapper to serialize to json.  Most
   * importantly, this points to the ability with JacksonHandle or JacksonDatabindHandle
   * to bring your own mapper and all the power that comes with it.  We're also
   * demonstrating the Bulk read/write api this time to write the documents.
   **/
  public static class XmlCityWriter implements CityWriter {
    private int numCities = 0;
    private XMLDocumentManager docMgr = client.newXMLDocumentManager();
    private DocumentWriteSet writeSet = docMgr.newWriteSet();
    private static XmlMapper mapper = new XmlMapper();
    static {
      mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
      mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
    }

    @Override
    public void addCity(City city) {
      if ( numCities >= MAX_TO_WRITE ) return;
      JacksonDatabindHandle handle = new JacksonDatabindHandle(city);
      // NOTICE: We've set the mapper to an XmlMapper, showing the versitility of Jackson
      handle.setMapper(mapper);
      handle.setFormat(Format.XML);
      writeSet.add(DIRECTORY + "/xmlCities/" + city.getGeoNameId() + ".xml", handle);
      numCities++;
    }
    @Override
    public void finishBatch() {
      if ( writeSet.size() > 0 ) {
        docMgr.write(writeSet);
        // while this test is usually just 10 records so no more than one write set,
        // we're ready to do more batches if we want to do performance testing here
        writeSet = docMgr.newWriteSet();
      }
    }
    @Override
    public void setNumRecords(int numRecords) {}
  }

  @Test
  public void testXmlDatabind() throws Exception {
    BulkReadWriteTest.loadCities(new XmlCityWriter());
    // we can add assertions later, for now this test just serves as example code and
    // ensures no exceptions are thrown
  }

  /* The following fields are in the data but not the third-party pojo */
  @JsonIgnoreProperties({"asciiName", "countryCode2", "dem", "timezoneCode", "lastModified"})
  class ToponymMixIn1 {
  }

  /* The following fields are either not in the third party pojo or not in the data so I don't want them serialized*/
  @JsonIgnoreProperties({
    "asciiName", "countryCode2", "dem", "timezoneCode", "lastModified",
    "featureClassName", "featureCodeName", "countryName", "adminName1", "adminName2",
    "elevation", "timezone", "style"
  })
  @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_OBJECT)
  class ToponymMixIn2 {
  }

  /** Demonstrate using Jackson's CSV mapper directly to simplify reading in data, populating a
   * third-party pojo (one we cannot annotate) then writing it out
   * via JacksonDatabindHandle with configuration provided by mix-in annotations.
   **/
  @Test
  public void testDatabindingThirdPartyPojoWithMixinAnnotations() throws IOException {
    CsvSchema schema = CsvSchema.builder()
      .setColumnSeparator('\t')
      .addColumn("geoNameId")
		.addColumn("name")
		.addColumn("asciiName")
		.addColumn("alternateNames")
		.addColumn("latitude", CsvSchema.ColumnType.NUMBER)
		.addColumn("longitude", CsvSchema.ColumnType.NUMBER)
		.addColumn("featureClass")
		.addColumn("featureCode")
		.addColumn("countryCode")
		.addColumn("countryCode2")
		.addColumn("adminCode1")
		.addColumn("adminCode2")
		.addColumn("adminCode3")
		.addColumn("adminCode4")
		.addColumn("population")
		.addColumn("elevation", CsvSchema.ColumnType.NUMBER)
		.addColumn("dem", CsvSchema.ColumnType.NUMBER)
		.addColumn("timezoneCode")
		.addColumn("lastModified")
		.build();
    CsvMapper mapper = new CsvMapper();
    mapper.addMixInAnnotations(Toponym.class, ToponymMixIn1.class);
    ObjectReader reader = mapper.reader(Toponym.class).with(schema);
    try (BufferedReader cityReader = new BufferedReader(Common.testFileToReader(CITIES_FILE))) {
      GenericDocumentManager docMgr = client.newDocumentManager();
      DocumentWriteSet set = docMgr.newWriteSet();
      String line;
      for (int numWritten = 0; numWritten < MAX_TO_WRITE && (line = cityReader.readLine()) != null; numWritten++ ) {
        Toponym city = reader.readValue(line);
        JacksonDatabindHandle handle = new JacksonDatabindHandle(city);
        handle.getMapper().addMixInAnnotations(Toponym.class, ToponymMixIn2.class);
        set.add(DIRECTORY + "/thirdPartyJsonCities/" + city.geoNameId + ".json", handle);
      }   docMgr.write(set);
      // we can add assertions later, for now this test just serves as example code and
      // ensures no exceptions are thrown
    }
  }

  public static void cleanUp() {
    QueryManager queryMgr = client.newQueryManager();
    DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
    deleteQuery.setDirectory(DIRECTORY);
    queryMgr.delete(deleteQuery);
  }

	// Copy of the class from the org.geonames dependency; copied here to avoid a Black Duck warning on the
	// transitive jdom dependency.
	private static class Toponym {
		private int geoNameId;
		private String name;
		private String alternateNames;
		private String countryCode;
		private String countryName;
		private Integer population;
		private Integer elevation;
		private String featureClass;
		private String featureClassName;
		private String featureCode;
		private String featureCodeName;
		private double latitude;
		private double longitude;
		private String adminCode1;
		private String adminName1;
		private String adminCode2;
		private String adminName2;
		private String adminCode3;
		private String adminCode4;
		private String timezone;
		private String style;

	  public String getFeatureClass() {
		  return featureClass;
	  }

	  public void setFeatureClass(String featureClass) {
		  this.featureClass = featureClass;
	  }

	  public String getTimezone() {
		  return timezone;
	  }

	  public void setTimezone(String timezone) {
		  this.timezone = timezone;
	  }

	  public String getStyle() {
		  return style;
	  }

	  public void setStyle(String style) {
		  this.style = style;
	  }

	  public int getGeoNameId() {
		  return geoNameId;
	  }

	  public void setGeoNameId(int geoNameId) {
		  this.geoNameId = geoNameId;
	  }

	  public String getName() {
		  return name;
	  }

	  public void setName(String name) {
		  this.name = name;
	  }

	  public String getAlternateNames() {
		  return alternateNames;
	  }

	  public void setAlternateNames(String alternateNames) {
		  this.alternateNames = alternateNames;
	  }

	  public String getCountryCode() {
		  return countryCode;
	  }

	  public void setCountryCode(String countryCode) {
		  this.countryCode = countryCode;
	  }

	  public String getCountryName() {
		  return countryName;
	  }

	  public void setCountryName(String countryName) {
		  this.countryName = countryName;
	  }

	  public Integer getPopulation() {
		  return population;
	  }

	  public void setPopulation(Integer population) {
		  this.population = population;
	  }

	  public Integer getElevation() {
		  return elevation;
	  }

	  public void setElevation(Integer elevation) {
		  this.elevation = elevation;
	  }

	  public String getFeatureClassName() {
		  return featureClassName;
	  }

	  public void setFeatureClassName(String featureClassName) {
		  this.featureClassName = featureClassName;
	  }

	  public String getFeatureCode() {
		  return featureCode;
	  }

	  public void setFeatureCode(String featureCode) {
		  this.featureCode = featureCode;
	  }

	  public String getFeatureCodeName() {
		  return featureCodeName;
	  }

	  public void setFeatureCodeName(String featureCodeName) {
		  this.featureCodeName = featureCodeName;
	  }

	  public double getLatitude() {
		  return latitude;
	  }

	  public void setLatitude(double latitude) {
		  this.latitude = latitude;
	  }

	  public double getLongitude() {
		  return longitude;
	  }

	  public void setLongitude(double longitude) {
		  this.longitude = longitude;
	  }

	  public String getAdminCode1() {
		  return adminCode1;
	  }

	  public void setAdminCode1(String adminCode1) {
		  this.adminCode1 = adminCode1;
	  }

	  public String getAdminName1() {
		  return adminName1;
	  }

	  public void setAdminName1(String adminName1) {
		  this.adminName1 = adminName1;
	  }

	  public String getAdminCode2() {
		  return adminCode2;
	  }

	  public void setAdminCode2(String adminCode2) {
		  this.adminCode2 = adminCode2;
	  }

	  public String getAdminName2() {
		  return adminName2;
	  }

	  public void setAdminName2(String adminName2) {
		  this.adminName2 = adminName2;
	  }

	  public String getAdminCode3() {
		  return adminCode3;
	  }

	  public void setAdminCode3(String adminCode3) {
		  this.adminCode3 = adminCode3;
	  }

	  public String getAdminCode4() {
		  return adminCode4;
	  }

	  public void setAdminCode4(String adminCode4) {
		  this.adminCode4 = adminCode4;
	  }
  }
}
