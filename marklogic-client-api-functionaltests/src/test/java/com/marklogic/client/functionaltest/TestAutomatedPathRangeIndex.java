/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.pojo.PojoQueryBuilder.Operator;
import com.marklogic.client.pojo.PojoQueryDefinition;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.pojo.util.GenerateIndexConfig;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Purpose : To test the following data type range path index can be created in a database.
 *
 * int - Verifies that the configuration file is generated, which is then used by MarkLogic Admin user to generate range index.
 * String - Verifies that the configuration file is generated.
 * dateTime- Verifies that the configuration file is generated.
 * daytimeduration* - This needs Java SE 8. Not tested in this class
 * URI- Verifies that the configuration file is generated.
 * Numerals as Strings* - Verifies that the configuration file is generated.
 * Integer - This class has additional range index search test methods for Integer type (Git issue # 222).
 * Float - Same as for Integer type. This class has additional range index search test methods for Float type (Git issue # 222).
 */

public class TestAutomatedPathRangeIndex extends BasicJavaClientREST {

  private static String dbName = "TestAutomatedPathRangeIndexDB";
  private static String[] fNames = { "TestAutomatedPathRangeIndexDB-1" };

  private DatabaseClient client;

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);

    BasicJavaClientREST.addRangePathIndex(dbName, "long", "com.marklogic.client.functionaltest.ArtifactIndexedOnInteger/inventory", "", "reject", true);
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    System.out.println("In tear down");
    cleanupRESTServer(dbName);
  }

  @BeforeEach
  public void setUp() throws Exception {
    client = getDatabaseClient("admin", "admin", getConnType());
  }

  @AfterEach
  public void tearDown() throws Exception {
    // release client
    client.release();
  }

	public static void validateRangePathIndexInDatabase(String propName, String propValue) throws IOException {
		ManageClient manageClient = newManageClient();
		String jsonResponse = new DatabaseManager(manageClient).getPropertiesAsJson(dbName);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jnode = mapper.readTree(jsonResponse);

		if (jnode.has(propName)) {
			List<JsonNode> jsonStringList = jnode.findValues(propName);

			String pathExpressValue = (jsonStringList.get(0)).findValues("path-expression").toString();
			String propValueJsonDecorated = "[\"" + propValue + "\"]";

			boolean contains = pathExpressValue.contains(propValueJsonDecorated);
			String propertyAvailable = contains == true ? (" contains " + propValue) : (" does not contain " + propValue);
			assertTrue(contains, new StringBuffer("Database : " + dbName + propertyAvailable).toString());
		}
	}

	public static void validateMultipleRangePathIndexInDatabase(String propName, String[] propValue) throws IOException {
		ManageClient manageClient = newManageClient();
		String jsonResponse = new DatabaseManager(manageClient).getPropertiesAsJson(dbName);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jnode = mapper.readTree(jsonResponse);

		if (jnode.has(propName)) {
			List<JsonNode> jsonStringList1 = jnode.findValues(propName);
			// Validate the first propValue
			String pathExpressValue1 = jsonStringList1.get(0).findValues("path-expression").toString();
			String propValueJsonDecorated1 = "[\"" + propValue[0];
			assertTrue(pathExpressValue1.contains(propValueJsonDecorated1));

			// Validate the second propValue
			String pathExpressValue2 = jsonStringList1.get(0).findValues("path-expression").toString();
			String propValueJsonDecorated2 = propValue[1] + "\"]";
			assertTrue(pathExpressValue2.contains(propValueJsonDecorated2));
		}
	}

  @Test
  public void testArtifactIndexedOnInt() throws Exception {
    boolean succeeded = false;
    File jsonFile = null;
    try {
      GenerateIndexConfig.main(new String[] { "-classes",
          "com.marklogic.client.functionaltest.ArtifactIndexedOnInt",
          "-file", "TestAutomatedPathRangeIndexInt.json" });

      jsonFile = new File("TestAutomatedPathRangeIndexInt.json");
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

      if (!jnode.isNull()) {
        setPathRangeIndexInDatabase(dbName, jnode);
        succeeded = true;
        validateRangePathIndexInDatabase("range-path-index", "com.marklogic.client.functionaltest.ArtifactIndexedOnInt/inventory");
      } else {
        assertTrue(succeeded,
            "testArtifactIndexedOnInt - No Json node available to insert into database");
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        jsonFile.delete();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * This method validates configuration file creation and also the search. Git
   * Issue #238
   */

  @Test
  public void testArtifactIndexedOnInteger() {
    boolean succeeded = false;
    File jsonFile = null;
    try {
      GenerateIndexConfig.main(new String[] { "-classes",
          "com.marklogic.client.functionaltest.ArtifactIndexedOnInteger",
          "-file", "TestAutomatedPathRangeIndexInteger.json" });

      jsonFile = new File("TestAutomatedPathRangeIndexInteger.json");
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

      if (!jnode.isNull()) {
        setPathRangeIndexInDatabase(dbName, jnode);
        succeeded = true;
        validateRangePathIndexInDatabase("range-path-index", "com.marklogic.client.functionaltest.ArtifactIndexedOnInteger/inventory");
      } else {
        assertTrue(succeeded, "testArtifactIndexedOnInteger - No Json node available to insert into database");
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    PojoRepository<ArtifactIndexedOnInteger, String> products = client.newPojoRepository(ArtifactIndexedOnInteger.class, String.class);
    PojoPage<ArtifactIndexedOnInteger> pojoPage;

    // Load POJO into database.
    loadSimplePojos(products);

    PojoQueryBuilder<ArtifactIndexedOnInteger> qb = products.getQueryBuilder();
    try {
      PojoQueryDefinition qd = qb.range("inventory", Operator.GE, 1055);

      JacksonHandle jh = new JacksonHandle();
      products.setPageLength(56);

      // The sleep below is a temporary fix.
      /*
       * The following exception was seen when there was no sleep. (broken into
       * multiple lines for easy reading):
       *
       * com.marklogic.client.FailedRequestException: Local message: search
       * failed: Bad Request. Server Message: XDMP-PATHRIDXNOTFOUND:
       * cts:search(fn:collection(), cts:and-query((cts:path-range-query(
       * "com.marklogic.client.functionaltest.ArtifactIndexedOnInteger/inv...",
       * ">=", xs:int("1055"), (), 1), cts:collection-query(
       * "com.marklogic.client.functionaltest.ArtifactIndexedOnInteger")), ()),
       * ("unfiltered", cts:score-order("descending")), xs:double("0"), ()) --
       * No int path range index for
       * com.marklogic.client.functionaltest.ArtifactIndexedOnInteger/inventory
       * Thread.sleep(5000);
       */

      pojoPage = products.search(qd, 1, jh);

      assertEquals(1, pojoPage.getTotalPages());

      long pageNo = 1, count = 0;
      do {
        count = 0;
        pojoPage = products.search(qd, pageNo);
        while (pojoPage.hasNext()) {
          ArtifactIndexedOnInteger a = pojoPage.next();
          validateArtifact(a);
          assertTrue(a.getId() >= 55);
          count++;
        }
        assertEquals(count, pojoPage.size());
        pageNo = pageNo + pojoPage.getPageSize();
      } while (!pojoPage.isLastPage() && pageNo <= pojoPage.getTotalSize());

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        jsonFile.delete();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void testArtifactIndexedOnString() throws Exception {
    boolean succeeded = false;
    File jsonFile = null;
    try {
      GenerateIndexConfig.main(new String[] { "-classes",
          "com.marklogic.client.functionaltest.ArtifactIndexedOnString",
          "-file", "TestAutomatedPathRangeIndexString.json" });

      jsonFile = new File("TestAutomatedPathRangeIndexString.json");
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

      if (!jnode.isNull()) {
        setPathRangeIndexInDatabase(dbName, jnode);
        succeeded = true;
        validateRangePathIndexInDatabase("range-path-index", "com.marklogic.client.functionaltest.ArtifactIndexedOnString/name");
      } else {
        assertTrue(succeeded, "testArtifactIndexedOnString - No Json node available to insert into database");
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        jsonFile.delete();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void testArtifactIndexedOnDateTime() throws Exception {
    boolean succeeded = false;
    File jsonFile = null;
    try {
      GenerateIndexConfig.main(new String[] { "-classes",
          "com.marklogic.client.functionaltest.ArtifactIndexedOnDateTime",
          "-file", "TestAutomatedPathRangeIndexDateTime.json" });

      jsonFile = new File("TestAutomatedPathRangeIndexDateTime.json");
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

      if (!jnode.isNull()) {
        setPathRangeIndexInDatabase(dbName, jnode);
        succeeded = true;
        validateRangePathIndexInDatabase("range-path-index", "com.marklogic.client.functionaltest.ArtifactIndexedOnDateTime/expiryDate");
      } else {
        assertTrue(succeeded, "testArtifactIndexedOnString - No Json node available to insert into database");
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        jsonFile.delete();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void testArtifactIndexedOnFloat() throws Exception {
    boolean succeeded = false;
    File jsonFile = null;
    try {
      GenerateIndexConfig.main(new String[] { "-classes",
          "com.marklogic.client.functionaltest.ArtifactIndexedOnFloat",
          "-file", "TestAutomatedPathRangeIndexFloat.json" });

      jsonFile = new File("TestAutomatedPathRangeIndexFloat.json");
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

      if (!jnode.isNull()) {
        setPathRangeIndexInDatabase(dbName, jnode);
        succeeded = true;
        validateRangePathIndexInDatabase("range-path-index", "com.marklogic.client.functionaltest.ArtifactIndexedOnFloat/price");
      } else {
        assertTrue(succeeded, "testArtifactIndexedOFloat - No Json node available to insert into database");
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        jsonFile.delete();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void testArtifactIndexedOnAnyURI() throws Exception {
    boolean succeeded = false;
    File jsonFile = null;
    try {
      GenerateIndexConfig.main(new String[] { "-classes",
          "com.marklogic.client.functionaltest.ArtifactIndexedOnUri",
          "-file", "TestAutomatedPathRangeIndexUri.json" });

      jsonFile = new File("TestAutomatedPathRangeIndexUri.json");
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

      if (!jnode.isNull()) {
        setPathRangeIndexInDatabase(dbName, jnode);
        succeeded = true;
        validateRangePathIndexInDatabase("range-path-index", "com.marklogic.client.functionaltest.ArtifactIndexedOnUri/artifactUri");
      } else {
        assertTrue(succeeded, "testArtifactIndexedOnUri - No Json node available to insert into database");
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        jsonFile.delete();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void testArtifactIndexedOnIntAsString() throws Exception {
    boolean succeeded = false;
    File jsonFile = null;
    try {
      GenerateIndexConfig.main(new String[] { "-classes",
          "com.marklogic.client.functionaltest.ArtifactIndexedOnIntAsString",
          "-file", "TestAutomatedPathRangeIndexIntAsString.json" });

      jsonFile = new File("TestAutomatedPathRangeIndexIntAsString.json");
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

      if (!jnode.isNull()) {
        setPathRangeIndexInDatabase(dbName, jnode);
        succeeded = true;
        validateRangePathIndexInDatabase("range-path-index", "com.marklogic.client.functionaltest.ArtifactIndexedOnIntAsString/inventory");
      } else {
        assertTrue(succeeded, "testArtifactIndexedOnIntAsString - No Json node available to insert into database");
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        jsonFile.delete();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void testArtifactIndexedOnMultipleFields() throws Exception {
    boolean succeeded = false;
    File jsonFile = null;
    try {
      GenerateIndexConfig.main(new String[] { "-classes",
          "com.marklogic.client.functionaltest.ArtifactIndexedOnMultipleFields",
          "-file", "TestAutomatedPathRangeIndexMultipleFields.json" });

      jsonFile = new File("TestAutomatedPathRangeIndexMultipleFields.json");
      // Array to hold the range path index values. Refer to the class for the
      // annotated class members.
      String[] propValueStrArray = { "com.marklogic.client.functionaltest.ArtifactIndexedOnMultipleFields/name",
          "com.marklogic.client.functionaltest.ArtifactIndexedOnMultipleFields/inventory" };
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

      if (!jnode.isNull()) {
        setPathRangeIndexInDatabase(dbName, jnode);
        succeeded = true;
        validateMultipleRangePathIndexInDatabase("range-path-index", propValueStrArray);
      } else {
        assertTrue(succeeded, "testArtifactIndexedOnIntAsString - No Json node available to insert into database");
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        jsonFile.delete();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void testArtifactIndexedOnStringInSuperClass() throws Exception {
    boolean succeeded = false;
    File jsonFile = null;
    try {
      GenerateIndexConfig.main(new String[] { "-classes",
          "com.marklogic.client.functionaltest.ArtifactIndexedOnStringSub",
          "-file", "TestAutomatedPathRangeIndexStringInSuperClass.json" });

      jsonFile = new File("TestAutomatedPathRangeIndexStringInSuperClass.json");
      // Array to hold the range path index values. Refer to the class for the
      // annotated class members.
      String[] propValueStrArray = { "com.marklogic.client.functionaltest.ArtifactIndexedOnStringSub/name",
          "com.marklogic.client.functionaltest.ArtifactIndexedOnStringSub/artifactWeight" };
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

      if (!jnode.isNull()) {
        setPathRangeIndexInDatabase(dbName, jnode);
        succeeded = true;
        validateMultipleRangePathIndexInDatabase("range-path-index", propValueStrArray);
      } else {
        assertTrue(succeeded, "testArtifactIndexedOnStringInSuperClass - No Json node available to insert into database");
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        jsonFile.delete();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void testArtifactMultipleIndexedOnInt() throws Exception {
    boolean succeeded = false;
    File jsonFile = null;
    try {
      GenerateIndexConfig.main(new String[] { "-classes",
          "com.marklogic.client.functionaltest.ArtifactMultipleIndexedOnInt",
          "-file", "TestAutomatedPathRangeMultipleIndexOnInt.json" });

      jsonFile = new File("TestAutomatedPathRangeMultipleIndexOnInt.json");
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

      if (!jnode.isNull()) {
        setPathRangeIndexInDatabase(dbName, jnode);
        succeeded = true;
        validateRangePathIndexInDatabase("range-path-index", "com.marklogic.client.functionaltest.ArtifactMultipleIndexedOnInt/inventory");
      } else {
        assertTrue(succeeded, "testArtifactMultipleIndexedOnInt - No Json node available to insert into database");
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        jsonFile.delete();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * Test for unsupported data type: This was meant as a negative test case. The
   * annotation declared on user defined data type will ONLY accept types from
   * scalarType class. Assigning a scalarType.Company to the class member in the
   * annotation like below, WILL NOT COMPILE:
   *
   * @PathIndexProperty(scalarType = ScalarType.Company) public Company
   * manufacture;
   *
   * However, for the following, we can declare for example for class property
   * Company (which is user defined type), the scalarType.STRING in the class.
   *
   * @PathIndexProperty(scalarType = ScalarType.STRING) public Company
   * manufacture;
   *
   * MarkLogic server accepts this range path index and this was verified
   * manually through the MarkLogic administration GUI on the database also.
   */

  @Test
  public void testArtifactIndexedOnUnSupportedAsString() throws Exception {
    boolean succeeded = false;
    File jsonFile = null;
    try {
      GenerateIndexConfig.main(new String[] { "-classes",
          "com.marklogic.client.functionaltest.ArtifactIndexedUnSupportedDataType",
          "-file", "TestArtifactIndexedOnUnSupportedAsString.json" });

      jsonFile = new File("TestArtifactIndexedOnUnSupportedAsString.json");
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

      if (!jnode.isNull()) {
        setPathRangeIndexInDatabase(dbName, jnode);
        succeeded = true;
        validateRangePathIndexInDatabase("range-path-index", "com.marklogic.client.functionaltest.ArtifactIndexedUnSupportedDataType/manufacturer");
      } else {
        assertTrue(succeeded, "testArtifactIndexedOnUnSupportedAsString - No Json node available to insert into database");
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        jsonFile.delete();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * The following three methods are used to validate "search on range index"
   * when POJO class ArtifactIndexedOnInteger has been annotated for path range
   * index as below:
   *
   * @PathIndexProperty(scalarType = ScalarType.INT) private Integer inventory;
   *
   * These methods are used to validate Git Issue # 222.
   */
  public void loadSimplePojos(PojoRepository<ArtifactIndexedOnInteger, String> products)
  {
    for (int i = 1; i < 111; i++) {
      if (i % 2 == 0) {
        products.write(this.getArtifact(i), "even", "numbers");
      }
      else {
        products.write(this.getArtifact(i), "odd", "numbers");
      }
    }
  }

  public ArtifactIndexedOnInteger getArtifact(int counter) {

    ArtifactIndexedOnInteger cogs = new ArtifactIndexedOnInteger();
    cogs.setId(counter);
    if (counter % 5 == 0) {
      cogs.setName("Cogs special");
      if (counter % 2 == 0) {
        Company acme = new Company();
        acme.setName("Acme special, Inc.");
        acme.setWebsite("http://www.acme special.com");
        acme.setLatitude(41.998 + counter);
        acme.setLongitude(-87.966 + counter);
        cogs.setManufacturer(acme);

      } else {
        Company widgets = new Company();
        widgets.setName("Widgets counter Inc.");
        widgets.setWebsite("http://www.widgets counter.com");
        widgets.setLatitude(41.998 + counter);
        widgets.setLongitude(-87.966 + counter);
        cogs.setManufacturer(widgets);
      }
    } else {
      cogs.setName("Cogs " + counter);
      if (counter % 2 == 0) {
        Company acme = new Company();
        acme.setName("Acme " + counter + ", Inc.");
        acme.setWebsite("http://www.acme" + counter + ".com");
        acme.setLatitude(41.998 + counter);
        acme.setLongitude(-87.966 + counter);
        cogs.setManufacturer(acme);

      } else {
        Company widgets = new Company();
        widgets.setName("Widgets " + counter + ", Inc.");
        widgets.setWebsite("http://www.widgets" + counter + ".com");
        widgets.setLatitude(41.998 + counter);
        widgets.setLongitude(-87.966 + counter);
        cogs.setManufacturer(widgets);
      }
    }
    cogs.setInventory(1000 + counter);
    cogs.setInventory1(1000 + counter);
    return cogs;
  }

  public void validateArtifact(ArtifactIndexedOnInteger art)
  {
    assertNotNull(art);
    assertNotNull(art.id);
    assertTrue(art.getInventory() > 1000);
  }

}
