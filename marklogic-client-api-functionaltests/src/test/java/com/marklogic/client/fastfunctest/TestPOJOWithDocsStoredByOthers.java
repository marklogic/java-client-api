/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.pojo.annotation.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class TestPOJOWithDocsStoredByOthers extends AbstractFunctionalTest {

  /*
   * This class is used as a POJO class to read documents stored as a JSON.
   * Class member name has been annotated with @Id. annotated.
   */
  public static class SmallArtifactIdInSuper {
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
   * This class is used as a POJO class to read documents stored as a POJO.
   * Class member name has been annotated with @Id in the super class. Has an
   * additional string member.
   */
  public static class SmallArtifactNoId extends SmallArtifactIdInSuper {
    private String originCountry;

    public String getOriginCountry() {
      return originCountry;
    }

    public void setOriginCountry(String originCountry) {
      this.originCountry = originCountry;
    }

  }

  /*
   * This class is used as a POJO class to read documents stored as a POJO.
   * Class member name has been annotated with @Id in the super class and also
   * in this class. Has an additional string member.
   */
  public static class SmallArtifactIdInSuperAndSub extends SmallArtifactIdInSuper {
    @Id
    public String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    private String originCountry;

    public String getOriginCountry() {
      return originCountry;
    }

    public void setOriginCountry(String originCountry) {
      this.originCountry = originCountry;
    }

  }

  /*
   * This class is used as a POJO class to read documents stored as a JSON. Used
   * to test different access specifiers Private Class member name has been
   * annotated with @Id. annotated.
   */
  public static class SmallArtifactPrivate {
    private String name;
    private long id;
    private int inventory;

    @Id
    public String getName() {
      return name;
    }

    @Id
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
   * This class is used as a POJO class to read documents stored as a JSON. Used
   * to test different access specifiers Private Class member name has been
   * annotated with @Id. annotated.
   */
  public static class SmallArtifactPublic extends SmallArtifactPrivate {
    public String name;
    private String originCountry;

    @Id
    public String getName() {
      return name;
    }

    @Id
    public void setName(String name) {
      this.name = name;
    }

    public String getOriginCountry() {
      return originCountry;
    }

    public void setOriginCountry(String originCountry) {
      this.originCountry = originCountry;
    }
  }

  @BeforeEach
  public void setUp() throws Exception {
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
   * This method is used when there is a need to validate SmallArtifact.
   */
  public void validateSmallArtifact(SmallArtifactIdInSuper artifact) {
    assertNotNull( artifact);
    assertNotNull( artifact.id);
    assertEquals( -99, artifact.getId());
    assertEquals( "SmallArtifact",
        artifact.getName());
    assertEquals( 1000,
        artifact.getInventory());
  }

  /*
   * This method is used when there is a need to validate SmallArtifactSuper to
   * test annotation only in super class.
   */
  public void validateSmallArtifactSuper(SmallArtifactNoId artifact) {
    assertNotNull( artifact);
    assertNotNull( artifact.getId());
    assertEquals( 0, artifact.getId());
    assertEquals( "SmallArtifactInSuperOnly",
        artifact.getName());
    assertEquals( 1000,
        artifact.getInventory());
    assertEquals( "USA",
        artifact.getOriginCountry());
  }

  /*
   * This method is used when there is a need to validate
   * SmallArtifactSuperAndSub to test annotation in super class and in sub
   * class.
   */
  public void validateSmallArtifactSuperAndSub(SmallArtifactIdInSuperAndSub artifact) {
    assertNotNull( artifact);
    assertNotNull( artifact.getId());
    assertEquals( -100, artifact.getId());
    assertEquals( "SmallArtifactInSuperAndSub",
        artifact.getName());
    assertEquals( 1000,
        artifact.getInventory());
    assertEquals( "USA",
        artifact.getOriginCountry());
  }

  /*
   * This method is used when there is a need to validate SmallArtifactPublic to
   * test annotation in super class and in sub class.
   */
  public void validateSmallArtifactDiffAccessSpec(SmallArtifactPublic artifact) {
    assertNotNull( artifact);
    assertNotNull( artifact.getId());
    assertEquals( -100, artifact.getId());
    assertEquals( "SmallArtifactDiffAccess",
        artifact.getName());
    assertEquals( 1000,
        artifact.getInventory());
    assertEquals( "USA",
        artifact.getOriginCountry());
  }

  /*
   * This method is used when there is a need to validate SmallArtifactIdInSuper
   * to test annotation in super class and in sub class.
   */
  public void validateSubObjReferencedbySuperClassvariable(SmallArtifactIdInSuper artifact) {
    assertNotNull( artifact);
    assertNotNull( artifact.getId());
    assertEquals( -100, artifact.getId());
    assertEquals( "SmallArtifactNoId",
        artifact.getName());
    assertEquals( 1000,
        artifact.getInventory());
  }

  /*
   * This method is used when there is a need to validate
   * SmallArtifactIdInSuperAndSub to test annotation in super class and in sub
   * class.
   */
  public void validateSubObjReferencedbySuperClassvariableOne(SmallArtifactIdInSuperAndSub artifact) {
    assertNotNull( artifact);
    assertNotNull( artifact.getId());
    assertEquals( -100, artifact.getId());
    assertEquals( "SmallArtifactIdInSuperAndSub",
        artifact.getName());
    assertEquals( 1000,
        artifact.getInventory());
  }

  /*
   * Purpose : This test is to validate read documents with valid POJO specific
   * URI and has invalid POJO collection Uses SmallArtifact class which has @Id
   * on the name methods. Test result expectations are: read should return a
   * null since the POJO internal content and document's are different.
   *
   * Current results (10/13/2014) are: java.lang.IllegalArgumentException:
   * Invalid type id 'junk' (for id type 'Id.class'): no such class found Issue
   * 136 might solve this also.
   */

	@Test
  public void testPOJOReadDocStoredWithInvalidContent() throws Exception {

    String docId[] = { "com.marklogic.client.fastfunctest.TestPOJOWithDocsStoredByOthers$SmallArtifactIdInSuper/SmallArtifactIdInSuper.json" };
    String json1 = new String(
        "{\"junk\":"
            + "{\"name\": \"SmallArtifactIdInSuper\",\"id\": -99, \"inventory\": 1000}}");
    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);
    DocumentWriteSet writeset = docMgr.newWriteSet();
    // put meta-data
    DocumentMetadataHandle mh = setMetadata();

    ObjectMapper mapper = new ObjectMapper();

    JacksonHandle jacksonHandle1 = new JacksonHandle();

    JsonNode junkNode = mapper.readTree(json1);
    jacksonHandle1.set(junkNode);
    jacksonHandle1.withFormat(Format.JSON);

    writeset.addDefault(mh);
    writeset.add(docId[0], jacksonHandle1);

    docMgr.write(writeset);

    PojoRepository<SmallArtifactIdInSuper, String> pojoReposSmallArtifact = client
        .newPojoRepository(SmallArtifactIdInSuper.class, String.class);
    String artifactName = new String("SmallArtifactIdInSuper");

    assertThrows(MarkLogicIOException.class, () -> pojoReposSmallArtifact.read(artifactName));
  }

  /*
   * Purpose : This test is to validate read documents with valid POJO specific
   * URI, and type and has missing POJO fields Uses SmallArtifact class which
   * has @Id on the name methods. Inventory bean property is missing from
   * document insert. Test result expectations: The POJO object returned should
   * be defaulting to Java default for the type (int) for inventory field.
   */
  @Test
  public void testPOJOReadDocStoredWithNoBeanProperty() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    String docId[] = { "com.marklogic.client.fastfunctest.TestPOJOWithDocsStoredByOthers$SmallArtifactIdInSuper/SmallArtifactIdInSuper.json" };
    String json1 = new String(
        "{\"com.marklogic.client.fastfunctest.TestPOJOWithDocsStoredByOthers$SmallArtifactIdInSuper\":"
            + "{\"name\": \"SmallArtifactIdInSuper\",\"id\": -99}}");

    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);
    DocumentWriteSet writeset = docMgr.newWriteSet();
    // put meta-data
    DocumentMetadataHandle mh = setMetadata();

    ObjectMapper mapper = new ObjectMapper();

    JacksonHandle jacksonHandle1 = new JacksonHandle();

    JsonNode noInventoryNode = mapper.readTree(json1);
    jacksonHandle1.set(noInventoryNode);
    jacksonHandle1.withFormat(Format.JSON);

    writeset.addDefault(mh);
    writeset.add(docId[0], jacksonHandle1);

    docMgr.write(writeset);

    PojoRepository<SmallArtifactIdInSuper, String> pojoReposSmallArtifact = client
        .newPojoRepository(SmallArtifactIdInSuper.class, String.class);
    String artifactName = new String("SmallArtifactIdInSuper");

    // Validate the SmallArtifactIdInSuper read back.
    SmallArtifactIdInSuper artifact = pojoReposSmallArtifact.read(artifactName);
    assertNotNull( artifact);
    assertNotNull( artifact.id);
    assertEquals( -99, artifact.getId());
    assertEquals( "SmallArtifactIdInSuper",
        artifact.getName());
    assertEquals( 0, artifact.getInventory());
  }

  /*
   * Purpose : This test is to validate read documents with valid POJO specific
   * URI and has invalid data-types for one of the bean property. Uses
   * SmallArtifact class which has @Id on the name methods. Test result
   * expectations are: read should return ResourceNotFoundException exception.
   * Field inventory has a String
   */
	@Test
  public void testPOJOReadDocStoredWithInvalidDataType() throws Exception {

    String docId[] = { "com.marklogic.client.fastfunctest.TestPOJOWithDocsStoredByOthers$SmallArtifact/SmallArtifact.json" };
    String json1 = new String(
        "{\"com.marklogic.client.fastfunctest.TestPOJOWithDocsStoredByOthers$SmallArtifact\":"
            + "{\"name\": \"SmallArtifact\",\"id\": -99, \"inventory\": \"String\"}}");
    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);
    DocumentWriteSet writeset = docMgr.newWriteSet();
    // put meta-data
    DocumentMetadataHandle mh = setMetadata();
    // add to POJO URI collection.
    mh.getCollections().addAll("com.marklogic.client.fastfunctest.TestPOJOWithDocsStoredByOthers$SmallArtifact/SmallArtifact.json",
        "com.marklogic.client.fastfunctest.TestPOJOWithDocsStoredByOthers$SmallArtifact/SmallArtifact.json");

    ObjectMapper mapper = new ObjectMapper();

    JacksonHandle jacksonHandle1 = new JacksonHandle();

    JsonNode invalidDataTypeNode = mapper.readTree(json1);
    jacksonHandle1.set(invalidDataTypeNode);
    jacksonHandle1.withFormat(Format.JSON);

    writeset.addDefault(mh);
    writeset.add(docId[0], jacksonHandle1);

    docMgr.write(writeset);

    PojoRepository<SmallArtifactIdInSuper, String> pojoReposSmallArtifact = client
        .newPojoRepository(SmallArtifactIdInSuper.class, String.class);
    String artifactName = "SmallArtifact";
    assertThrows(ResourceNotFoundException.class, () -> pojoReposSmallArtifact.read(artifactName));
  }

  /*
   * Purpose : This test is to validate Test creating an @id in super class only
   * SmallArtifactSuper class which has @Id only on the name class member of the
   * super class SmallArtifact.
   *
   * Current results (10/13/2014) are: java.lang.IllegalArgumentException: Your
   * class com.marklogic.client.fastfunctest.
   * TestPOJOWithDocsStoredByOthers$SmallArtifactSuper does not have a method or
   * field annotated with com.marklogic.client.pojo.annotation.Id
   */
  @Test
  public void testPOJOWriteReadSuper() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    PojoRepository<SmallArtifactNoId, String> pojoReposSmallArtifact = client
        .newPojoRepository(SmallArtifactNoId.class, String.class);
    String artifactName = new String("SmallArtifactInSuperOnly");

    SmallArtifactNoId art = new SmallArtifactNoId();
    art.setId(0);
    art.setInventory(1000);
    art.setName(artifactName);
    art.setOriginCountry("USA");

    // Load the object into database
    pojoReposSmallArtifact.write(art, "com.marklogic.client.fastfunctest.TestPOJOMissingIdGetSetMethod$SmallArtifactSuper/SmallArtifactSuper.json");

    SmallArtifactNoId artifact1 = pojoReposSmallArtifact.read(artifactName);
    validateSmallArtifactSuper(artifact1);
  }

  /*
   * Purpose : This test is to validate Test creating an @id in super class and
   * sub class. Both SmallArtifactSuperAndSub and SmallArtifact class have a
   * similar @Id on the name class member.
   *
   * Current results (10/13/2014) are: Read works fine.
   */
  @Test
  public void testPOJOWriteReadSuperAndSub() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    PojoRepository<SmallArtifactIdInSuperAndSub, String> pojoReposSmallArtifact = client
        .newPojoRepository(SmallArtifactIdInSuperAndSub.class, String.class);
    String artifactName = new String("SmallArtifactInSuperAndSub");

    SmallArtifactIdInSuperAndSub art = new SmallArtifactIdInSuperAndSub();
    art.setId(-100);
    art.setInventory(1000);
    art.setName(artifactName);
    art.setOriginCountry("USA");

    // Load the object into database
    pojoReposSmallArtifact.write(art, "com.marklogic.client.fastfunctest.TestPOJOMissingIdGetSetMethod$SmallArtifactSuper/SmallArtifactSuper.json");

    SmallArtifactIdInSuperAndSub artifact1 = pojoReposSmallArtifact.read(artifactName);
    validateSmallArtifactSuperAndSub(artifact1);
  }

  /*
   * Purpose : This test is to validate creating an @id in super class and sub
   * class that has different access specifiers. Both SmallArtifactSuperAndSub
   * and SmallArtifact class have a similar @Id on the name class member.
   *
   * Current results (10/13/2014) are: Read works fine.
   */
  @Test
  public void testPOJOWriteReadDiffAccessSpecifiers() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    PojoRepository<SmallArtifactPublic, String> pojoReposSmallArtifact = client
        .newPojoRepository(SmallArtifactPublic.class, String.class);
    String artifactName = new String("SmallArtifactDiffAccess");

    SmallArtifactPublic art = new SmallArtifactPublic();
    art.setId(-100);
    art.setInventory(1000);
    art.setName(artifactName);
    art.setOriginCountry("USA");

    // Load the object into database
    pojoReposSmallArtifact.write(art, "com.marklogic.client.fastfunctest.TestPOJOMissingIdGetSetMethod$SmallArtifactSuper/SmallArtifactSuper.json");

    SmallArtifactPublic artifact1 = pojoReposSmallArtifact.read(artifactName);
    validateSmallArtifactDiffAccessSpec(artifact1);
  }

  /*
   * Purpose : This test is to validate creating an sub class which is
   * referenced by a Super class variable type. Both SmallArtifactIdInSuper and
   * SmallArtifactNoId classes are used. POJO repository cannot read back the
   * sub class.
   *
   * PojoRepository is on the super class.
   */

  @Test
  public void testPOJOSubObjReferencedBySuperClassVariable() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    PojoRepository<SmallArtifactIdInSuper, String> pojoReposSmallArtifact = client
        .newPojoRepository(SmallArtifactIdInSuper.class, String.class);
    String artifactName = new String("SmallArtifactNoId");

    SmallArtifactIdInSuper art = new SmallArtifactNoId();
    art.setId(-100);
    art.setInventory(1000);
    art.setName(artifactName);

    // Load the object into database
    pojoReposSmallArtifact.write(art, "SubClassObjectReferencedBySuperClassVariable");

    // POJO repository cannot read back the sub class. Compiler complains.
    SmallArtifactIdInSuper artifact1 = pojoReposSmallArtifact.read(artifactName);
    validateSubObjReferencedbySuperClassvariable(artifact1);
  }

  /*
   * Purpose : This test is to validate creating an sub class which is
   * referenced by a Super class variable type. Both SmallArtifactIdInSuper and
   * SmallArtifactNoId classes are used. This is a variation of
   * testPOJOSubObjReferencedBySuperClassVariable()
   *
   * PojoRepository is on the sub class.
   */

  @Test
  public void testPOJOSubObjReferencedBySuperClassVariableOne() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    PojoRepository<SmallArtifactIdInSuperAndSub, String> pojoReposSmallArtifact = client
        .newPojoRepository(SmallArtifactIdInSuperAndSub.class, String.class);
    String artifactName = new String("SmallArtifactIdInSuperAndSub");

    SmallArtifactIdInSuper art = new SmallArtifactIdInSuperAndSub();
    art.setId(-100);
    art.setInventory(1000);
    art.setName(artifactName);

    // Load the object into database
    // POJO repository cannot write using super class reference class. Needs an
    // explicit cast else compiler complains.
    pojoReposSmallArtifact.write((SmallArtifactIdInSuperAndSub) art, "SubClassObjectReferencedBySuperClassVariableOne");

    SmallArtifactIdInSuperAndSub artifact1 = pojoReposSmallArtifact.read(artifactName);
    validateSubObjReferencedbySuperClassvariableOne(artifact1);
  }

}
