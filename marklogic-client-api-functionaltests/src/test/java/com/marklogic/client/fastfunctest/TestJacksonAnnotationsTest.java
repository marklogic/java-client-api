/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.pojo.annotation.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

/*
 * This test is designed to to test available Jackson Annotations.
 * Separate POJO classes with aJackson annotation will be used.
 * refer to each static class for further description of its purpose.
 */

public class TestJacksonAnnotationsTest extends AbstractFunctionalTest {

  private long negativeId = -1L;

  /*
   * This class is similar to the SmallArtifact class. It is used to test
   * Jackson Annotation
   *
   * @JsonIgnoreProperties on inventory field. The field is ignored at class
   * level.
   *
   * Class member name has been annotated with @Id. Expected result: Field
   * inventory should not be serialized.
   */
  @JsonIgnoreProperties(value = { "inventory" })
  public static class IgnoreProperties {

    @Id
    public String name;
    private long id;
    private int inventory;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public long getId() {
      return id;
    }

    public void setId(long id) {
      this.id = id;
    }

    public int getInventory() {
      return inventory;
    }

    public void setInventory(int inventory) {
      this.inventory = inventory;
    }
  }

  /*
   * This class is similar to the IgnoreProperties class. It is used to test
   * Jackson Annotation
   *
   * @JsonIgnore on inventory field. The field is ignored at field level.
   *
   * Class member name has been annotated with @Id. Expected result: Field
   * inventory should not be serialized.
   */

  public static class IgnoreFields {

    @Id
    public String name;
    private long id;
    @JsonIgnore
    private int inventory;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public long getId() {
      return id;
    }

    public void setId(long id) {
      this.id = id;
    }

    public int getInventory() {
      return inventory;
    }

    public void setInventory(int inventory) {
      this.inventory = inventory;
    }
  }

  /*
   * This class is similar to the IgnoreProperties class. It is used to test
   * Jackson Annotation
   *
   * @JsonProperty on different fields.
   *
   * Class member name has been annotated with @Id. property to serialize when
   * applied to getter method. property to de-serialize when applied to setter
   * method. Expected result: Id field is not serialized / de-serialized.
   * Expected Results: @JsonProperty overrides @JsonIgnore. Refer to inventory
   * field.
   */

  public static class JsonPropertyCheck {

    @Id
    public String name;
    @JsonIgnore
    private long id;
    @JsonIgnore
    private int inventory;
    private String batch;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public long getId() {
      return id;
    }

    public void setId(long id) {
      this.id = id;
    }

    // property to serialize when applied to getter method.
    @JsonProperty
    public int getInventory() {
      return inventory;
    }

    // property to de-serialize when applied to setter method.
    @JsonProperty
    public void setInventory(int inventory) {
      this.inventory = inventory;
    }

    // property to serialize when applied to getter method.
    @JsonProperty
    public String getBatch() {
      return batch;
    }

    // property to de-serialize when applied to setter method.
    @JsonProperty
    public void setBatch(String batch) {
      this.batch = batch;
    }
  }

  /*
   * This class is similar to the JsonPropertyCheck class. It is used to test
   * Jackson Annotation
   *
   * @JsonValue on a method whose return String value is used to produce JSON
   * value serialization.
   *
   * Class member name has been annotated with @Id. property to serialize when
   * applied to getter method. property to de-serialize when applied to setter
   * method. Expected result: Id field is not serialized / de-serialized.
   * Expected Results: @JsonProperty overrides @JsonIgnore. Refer to inventory
   * field.
   */

  public static class JsonValueCheck {

    @Id
    public String name;
    private long id;
    private int inventory;
    private String batch;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public long getId() {
      return id;
    }

    public void setId(long id) {
      this.id = id;
    }

    // property to serialize when applied to getter method.

    public int getInventory() {
      return inventory;
    }

    public void setInventory(int inventory) {
      this.inventory = inventory;
    }

    public String getBatch() {
      return batch;
    }

    public void setBatch(String batch) {
      this.batch = batch;
    }

    @JsonValue
    public String jsonValueSerialized() {
      return new String(
          "JsonValueCheck class is a customized serialied one");
    }
  }

  public long getNegativeId() {
    return negativeId;
  }

  public void setNegativeId(long longNegativeId) {
    negativeId = longNegativeId;
  }

  // Decrement and return the current negative Id.
  public long getOneNegativeLongId() {
    long lTemp = getNegativeId();
    setNegativeId(lTemp - 1);
    return lTemp == -1 ? -1 : lTemp;
  }

  @BeforeEach
  public void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    client = getDatabaseClient("rest-admin", "x", getConnType());
  }

  public DocumentMetadataHandle setMetadata() {
    // create and initialize a handle on the meta-data
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
    metadataHandle.getCollections().addAll("my-collection1",
        "my-collection2");
    metadataHandle.getPermissions().add("app-user", Capability.UPDATE,
        Capability.READ);
    metadataHandle.getProperties().put("reviewed", true);
    metadataHandle.getProperties().put("myString", "foo");
    metadataHandle.getProperties().put("myInteger", 10);
    metadataHandle.getProperties().put("myDecimal", 34.56678);
    metadataHandle.getProperties().put("myCalendar",
        Calendar.getInstance().get(Calendar.YEAR));
    metadataHandle.setQuality(23);
    return metadataHandle;
  }

  /*
   * This method is used when there is a need to validate IgnoreProperties.
   */
  public void validateIgnoreProperties(IgnoreProperties artifact, long temp) {
    assertNotNull(artifact);
    assertNotNull(artifact.id);
    assertEquals(temp, artifact.getId());
    assertEquals( "IgnoreProperties", artifact.getName());
    assertEquals(0, artifact.getInventory());
  }

  /*
   * This method is used when there is a need to validate IgnoreFields.
   */
  public void validateIgnoreFields(IgnoreFields artifact, long temp) {
    assertNotNull( artifact);
    assertNotNull( artifact.id);
    assertEquals( temp, artifact.getId());
    assertEquals( "IgnoreFields",
        artifact.getName());
    assertEquals( 0, artifact.getInventory());
  }

  /*
   * This method is used when there is a need to validate JsonPropertyCheck. Id
   * field is not serialized and hence its default return value should be 0
   */
  public void validateJsonPropertyCheck(JsonPropertyCheck artifact, long temp) {
    assertNotNull( artifact);
    assertNotNull( artifact.id);
    assertEquals( 0, artifact.getId());
    assertEquals( "JsonProperty",
        artifact.getName());
    assertEquals( "Batch One",
        artifact.getBatch());
    assertEquals( 1000,
        artifact.getInventory());
  }

  /*
   * Purpose : This test is to validate @JsonIgnoreProperties with valid POJO.
   * Uses IgnoreProperties class which has @Id on the name methods. Test result
   * expectations are: Java Default 0L is read back into the object.
   */

  @Test
  public void testPOJOJsonIgnoreProperties() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    String artifactName = "IgnoreProperties";

    PojoRepository<IgnoreProperties, String> pojoReposIgnoreProperties = client
        .newPojoRepository(IgnoreProperties.class, String.class);

    IgnoreProperties artifactOrig = new IgnoreProperties();
    long tempId = getOneNegativeLongId();

    artifactOrig.setId(tempId);
    artifactOrig.setInventory(1000);
    artifactOrig.setName(artifactName);

    // Write the object to the database.
    pojoReposIgnoreProperties.write(artifactOrig, "IgnoreProperties");

    IgnoreProperties artifact1 = pojoReposIgnoreProperties
        .read(artifactName);
    validateIgnoreProperties(artifact1, tempId);
  }

  /*
   * Purpose : This test is to validate @JsonIgnoreProperties with valid POJO
   * instance. Uses IgnoreProperties class which has @Id on the name methods.
   * Test result expectations are: Mapper's write should not contain field -
   * inventory.
   *
   * Test method testPOJOJsonIgnoreProperties does a database store and then a
   * read back and in that the Java Default 0 is read back into the object.
   */
  @Test
  public void testPOJOJsonIgnorePropertiesObject() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    ObjectMapper mapper = new ObjectMapper();

    IgnoreProperties artifact1 = new IgnoreProperties();
    String artifactName = mapper.writeValueAsString(artifact1);
    // Field inventory should not be available.
    assertFalse(artifactName.contains("inventory"));

  }

  /*
   * Purpose : This test is to validate @JsonIgnore with valid POJO. Uses
   * IgnorFields class which has @Id on the name methods. Test result
   * expectations are: Java Default 0L is read back into the object.
   */

  @Test
  public void testPOJOJsonIgnoreField() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    String artifactName = "IgnoreFields";

    PojoRepository<IgnoreFields, String> pojoReposIgnoreProperties = client
        .newPojoRepository(IgnoreFields.class, String.class);

    IgnoreFields artifactOrig = new IgnoreFields();
    long tempId = getOneNegativeLongId();

    artifactOrig.setId(tempId);
    artifactOrig.setInventory(1000);
    artifactOrig.setName(artifactName);

    // Write the object to the database.
    pojoReposIgnoreProperties.write(artifactOrig, "IgnoreFields");

    IgnoreFields artifact1 = pojoReposIgnoreProperties.read(artifactName);
    validateIgnoreFields(artifact1, tempId);
  }

  /*
   * Purpose : This test is to validate @JsonIgnore with valid POJO instance.
   * Uses IgnoreFields class which has @Id on the name methods. Test result
   * expectations are: Mapper's write should not contain field - inventory.
   *
   * Test method testPOJOJsonIgnoreFields does a database store and then a read
   * back and in that the Java Default 0 is read back into the object.
   */
  @Test
  public void testPOJOJsonIgnoreFieldsObject() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    ObjectMapper mapper = new ObjectMapper();

    IgnoreFields artifact1 = new IgnoreFields();
    String artifactName = mapper.writeValueAsString(artifact1);
    // Field inventory should not be available.
    assertFalse(artifactName.contains("inventory"));
  }

  /*
   * Purpose : This test is to validate @JsonProperty with valid POJO. Uses
   * JsonPropertyCheck class which has @Id on the name methods. Test result
   * expectations are: Java Default 0L is read back into the object.
   *
   * Field id is not annotation. Its Getter/Setter are also not annotated. Hence
   * that field is not serialized nor de-serialized.
   */

  @Test
  public void testPOJOJsonProperty() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    String artifactName = "JsonProperty";

    PojoRepository<JsonPropertyCheck, String> pojoReposIgnoreProperties = client
        .newPojoRepository(JsonPropertyCheck.class, String.class);

    JsonPropertyCheck artifactOrig = new JsonPropertyCheck();
    long tempId = getOneNegativeLongId();

    artifactOrig.setId(tempId);
    // Id is not serilized.
    artifactOrig.setInventory(1000);
    artifactOrig.setName(artifactName);
    artifactOrig.setBatch("Batch One");

    // Write the object to the database.
    pojoReposIgnoreProperties.write(artifactOrig, "JasonProperty");

    JsonPropertyCheck artifact1 = pojoReposIgnoreProperties
        .read(artifactName);
    validateJsonPropertyCheck(artifact1, tempId);
  }

  /*
   * Purpose : This test is to validate @JsonIgnore with valid POJO instance.
   * Uses JsonPropertyCheck class which has @Id on the name methods. Test result
   * expectations are: Mapper's write should not contain field - id.
   *
   * Test method testPOJOJsonIgnoreFields does a database store and then a read
   * back and in that the Java Default 0 is read back into the object.
   */
  @Test
  public void testPOJOJsonJsonPropertyObject() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    ObjectMapper mapper = new ObjectMapper();
    String instanceName = "JsonProperty";
    long tempId = getOneNegativeLongId();

    JsonPropertyCheck artifactOrig = new JsonPropertyCheck();
    artifactOrig.setId(tempId);
    artifactOrig.setInventory(1000);
    artifactOrig.setName(instanceName);
    artifactOrig.setBatch("Batch One");

    String artifactName = mapper.writeValueAsString(artifactOrig);
    // Field inventory should not be available.
    assertFalse(artifactName.contains("id"));
  }

  /*
   * Purpose : This test is to validate @JsonValue with valid POJO instance.
   * Uses JsonValueCheck class which has @Id on the name methods. Test result
   * expectations are: Mapper's write should serialize customized strings as
   * JSON.
   */
  @Test
  public void testPOJOJsonJsonValueObject() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    ObjectMapper mapper = new ObjectMapper();
    String instanceName = "JsonValue";
    long tempId = getOneNegativeLongId();

    JsonValueCheck artifactOrig = new JsonValueCheck();
    artifactOrig.setId(tempId);
    artifactOrig.setInventory(1000);
    artifactOrig.setName(instanceName);
    artifactOrig.setBatch("Batch One");

    String artifactName = mapper.writeValueAsString(artifactOrig);
    // Assert that the customized string is serialized JSON string.
    assertTrue(artifactName
        .contains("JsonValueCheck class is a customized serialied one"));
  }
}
