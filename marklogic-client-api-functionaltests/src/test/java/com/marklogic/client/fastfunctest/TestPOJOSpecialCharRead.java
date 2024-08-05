/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.functionaltest.Artifact;
import com.marklogic.client.functionaltest.Company;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.query.StringQueryDefinition;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Iterator;

/*
 * Purpose : To test the following special cases for PojoRepository class methods
 * POJO instance @Id field value with Negative numbers.
 * POJO instance @Id field value with default values.
 * POJO instance @Id field value with nulls.
 * POJO instance @Id field value with special characters.
 * POJO instance @Id field value with Unicode characters.
 */

public class TestPOJOSpecialCharRead extends AbstractFunctionalTest {
  private long negativeId = -1L;

  /*
   * This class is similar to the Artifact class. It is used to test special
   * characters using the name field which has been annotated with @Id. Note:
   * Artifact class has id field annotated.
   */
  public static class SpecialArtifact {
    @Id
    public String name;
    public long id;
    private Company manufacturer;
    private int inventory;

    public long getId() {
      return id;
    }

    public SpecialArtifact setId(long id) {
      this.id = id;
      return this;
    }

    public String getName() {
      return name;
    }

    public SpecialArtifact setName(String name) {
      this.name = name;
      return this;
    }

    public Company getManufacturer() {
      return manufacturer;
    }

    public SpecialArtifact setManufacturer(Company manufacturer) {
      this.manufacturer = manufacturer;
      return this;
    }

    public int getInventory() {
      return inventory;
    }

    public SpecialArtifact setInventory(int inventory) {
      this.inventory = inventory;
      return this;
    }
  }

  /*
   * This class is similar to the SpecialArtifact class. It is used to test
   * special characters using the getter and setter methods which has been
   * annotated with @Id.
   */
  public static class SpArtifactWithGetSetId {

    private String name;
    private long id;
    private Company manufacturer;
    private int inventory;

    @Id
    public long getId() {
      return id;
    }

    @Id
    public SpArtifactWithGetSetId setId(long id) {
      this.id = id;
      return this;
    }

    public String getName() {
      return name;
    }

    public SpArtifactWithGetSetId setName(String name) {
      this.name = name;
      return this;
    }

    public Company getManufacturer() {
      return manufacturer;
    }

    public SpArtifactWithGetSetId setManufacturer(Company manufacturer) {
      this.manufacturer = manufacturer;
      return this;
    }

    public int getInventory() {
      return inventory;
    }

    public SpArtifactWithGetSetId setInventory(int inventory) {
      this.inventory = inventory;
      return this;
    }
  }

  /*
   * This class is used to test writing and reading byte array Class member id
   * has been annotated with @Id.
   */
  public static class ByteArrayId {
    @Id
    public byte id;
    public byte[] byteName;

    public byte getId() {
      return id;
    }

    public void setId(byte id) {
      this.id = id;
    }

    public byte[] getByteName() {
      return byteName;
    }

    public void setByteName(byte[] byteName) {
      this.byteName = byteName;
    }
  }

  /*
   * This class is used to test writing and reading byte array Class member
   * byte[] byteName has been annotated with @Id.
   */
  public static class AnnotateByteArray {
    public byte id;
    @Id
    public byte[] byteName;

    public byte getId() {
      return id;
    }

    public void setId(byte id) {
      this.id = id;
    }

    public byte[] getByteName() {
      return byteName;
    }

    public void setByteName(byte[] byteName) {
      this.byteName = byteName;
    }
  }

  @BeforeEach
  public void setUp() throws Exception {
    client = getDatabaseClient("rest-admin", "x", getConnType());
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

  public Artifact setArtifact(long counter) {
    Company acme = new Company();
    acme.setName("Acme Inc.");
    acme.setWebsite("http://www.acme.com");
    acme.setLatitude(41.998);
    acme.setLongitude(-87.966);
    Artifact cogs = new Artifact();
    cogs.setId(counter);
    cogs.setName("Cogs");
    cogs.setManufacturer(acme);
    cogs.setInventory(1000);

    return cogs;
  }

  // Sets the SpecialArtifact POJO object based on the @Id on name field
  public SpecialArtifact setSpecialArtifact(long counter) {
    Company acme = new Company();
    acme.setName("Acme Inc.");
    acme.setWebsite("http://www.acme.com");
    acme.setLatitude(41.998);
    acme.setLongitude(-87.966);
    SpecialArtifact cogs = new SpecialArtifact();
    cogs.setId(counter);
    cogs.setName("Cogs");
    cogs.setManufacturer(acme);
    cogs.setInventory(1000);

    return cogs;
  }

  /*
   * This method is used to test POJO Repository read / search with default
   * values. This method does not set the Id field in the Artifact class
   * explicitly. Hence Id field should be holding 0L as the default value. Test
   * should pass 0L and POJORepository read should handle this request just
   * fine.
   */
  public Artifact setArtifactWithDefault() {
    Company acme = new Company();
    acme.setName("Acme Inc.");
    acme.setWebsite("http://www.acme.com");
    acme.setLatitude(41.998);
    acme.setLongitude(-87.966);
    Artifact cogs = new Artifact();
    // cogs.setId(counter);
    cogs.setName("Cogs");
    cogs.setManufacturer(acme);
    cogs.setInventory(1000);

    return cogs;
  }

  /*
   * This method is used to test POJO Repository read / search with null values.
   * This method set the manufacturer field in the Artifact class to null
   * explicitly. Cannot set any of the Id fields to null due to Java language
   * defaults.
   */
  public Artifact setArtifactWithNull(long counter) {

    Artifact cogs = new Artifact();
    cogs.setId(counter);
    cogs.setName("Cogs");
    cogs.setManufacturer(null);
    cogs.setInventory(1000);

    return cogs;
  }

  /*
   * This method is used to test POJO Repository read / search with special
   * characters in the SpecialArtifact name. Test should pass in the Name field
   * and POJORepository read should handle this request just fine.
   */
  public SpecialArtifact setSpecialArtifactWithSpecialCharacter(long counter, String specialArtifactName) {
    Company acme = new Company();
    acme.setName(specialArtifactName);
    acme.setWebsite("http://www.acme.com");
    acme.setLatitude(41.998);
    acme.setLongitude(-87.966);
    SpecialArtifact cogs = new SpecialArtifact();
    cogs.setId(counter);
    cogs.setName(specialArtifactName);
    cogs.setManufacturer(acme);
    cogs.setInventory(1000);

    return cogs;
  }

  // Sets the SpecialArtifact POJO object based on the @Id on get and set
  // methods
  public SpArtifactWithGetSetId setSpArtifactonMethod(long counter, String specialArtifactName) {
    Company acme = new Company();
    acme.setName(specialArtifactName);
    acme.setWebsite("http://www.acme.com");
    acme.setLatitude(41.998);
    acme.setLongitude(-87.966);
    SpArtifactWithGetSetId cogs = new SpArtifactWithGetSetId();
    cogs.setId(counter);
    cogs.setName(specialArtifactName);
    cogs.setManufacturer(acme);
    cogs.setInventory(1000);

    return cogs;
  }

  /*
   * This method is used when there is a need to validate one read and search.
   */
  public void validateArtifact(Artifact artifact, long longId) {
    assertNotNull( artifact);
    assertNotNull( artifact.id);
    assertEquals( longId, artifact.getId());
    assertEquals( "Cogs", artifact.getName());
    assertEquals( 1000, artifact.getInventory());
    assertEquals( "Acme Inc.", artifact.getManufacturer().getName());
    assertEquals( "http://www.acme.com", artifact.getManufacturer().getWebsite());
    assertEquals(-87.966, artifact.getManufacturer().getLongitude(), 0.00);
    assertEquals(41.998, artifact.getManufacturer().getLatitude(), 0.00);
  }

  /*
   * This method is used when there is a need to validate one read and search
   * with default Id field value. Verify that artifact.getId() returns 0L.
   */
  public void validateArtifactWithDefault(Artifact artifact) {
    assertNotNull( artifact);
    assertNotNull( artifact.id);
    assertEquals( 0, artifact.getId());
    assertEquals( "Cogs", artifact.getName());
    assertEquals( 1000, artifact.getInventory());
    assertEquals( "Acme Inc.", artifact.getManufacturer().getName());
    assertEquals( "http://www.acme.com", artifact.getManufacturer().getWebsite());
    assertEquals(-87.966, artifact.getManufacturer().getLongitude(), 0.00);
    assertEquals(41.998, artifact.getManufacturer().getLatitude(), 0.00);
  }

  /*
   * This method is used when there is a need to validate one read and search
   * with null. This test verifies that the POJORepository can write an object
   * that has some parts not yet created/defined etc..
   */
  public void validateArtifactWithNull(Artifact artifact, long longId) {
    assertNotNull( artifact);
    assertNotNull( artifact.id);
    assertEquals( longId, artifact.getId());
    assertEquals( "Cogs", artifact.getName());
    assertEquals( 1000, artifact.getInventory());
    assertNull(artifact.getManufacturer());
  }

  /*
   * This method is used when there is a need to validate one read and search
   * with special characters in SpecialArtifact Name field.
   */
  public void validateSpecialArtifactWithSpecialCharacter(SpecialArtifact artifact, String artifactName, long longId) {
    assertNotNull( artifact);
    assertNotNull( artifact.id);
    assertEquals( longId, artifact.getId());
    assertEquals( artifactName, artifact.getName());
    assertEquals( 1000, artifact.getInventory());
    assertEquals( artifactName, artifact.getManufacturer().getName());
    assertEquals( "http://www.acme.com", artifact.getManufacturer().getWebsite());
    assertEquals(-87.966, artifact.getManufacturer().getLongitude(), 0.00);
    assertEquals(41.998, artifact.getManufacturer().getLatitude(), 0.00);
  }

  /*
   * This method is used when there is a need to validate one read and search
   * with special characters in SpArtifactWithGetSetId Name field.
   */
  public void validateSpArtifactWithSpecialCharacter(SpArtifactWithGetSetId artifact, String artifactName, long longId) {
    assertNotNull( artifact);
    assertNotNull( artifact.id);
    assertEquals( longId, artifact.getId());
    assertEquals( artifactName, artifact.getName());
    assertEquals( 1000, artifact.getInventory());
    assertEquals( artifactName, artifact.getManufacturer().getName());
    assertEquals( "http://www.acme.com", artifact.getManufacturer().getWebsite());
    assertEquals(-87.966, artifact.getManufacturer().getLongitude(), 0.00);
    assertEquals(41.998, artifact.getManufacturer().getLatitude(), 0.00);
  }

  /*
   * This method is used when there is a need to validate multiple reads and
   * searches.
   */
  public void validateArtifactTwo(Artifact artifact, long longId) {
    assertNotNull( artifact);
    assertNotNull( artifact.id);
    assertEquals( longId, artifact.getId());
    assertEquals( "Cogs", artifact.getName());
    assertEquals( 1000, artifact.getInventory());
    assertEquals( "Acme Inc.", artifact.getManufacturer().getName());
    assertEquals( "http://www.acme.com", artifact.getManufacturer().getWebsite());
    assertEquals(-87.966, artifact.getManufacturer().getLongitude(), 0.00);
    assertEquals(41.998, artifact.getManufacturer().getLatitude(), 0.00);
  }

  /*
   * This method is used when there is a need to validate byte array reads using
   * @Id on id field.
   */
  public void validateByteArray(ByteArrayId artifact, byte[] arrayOrig, byte bTest) {
    assertNotNull( artifact);
    assertNotNull( artifact.id);
    assertTrue(Arrays.equals(artifact.getByteName(), arrayOrig));
    assertEquals( bTest, artifact.getId());
  }

  /*
   * This method is used when there is a need to validate byte array reads using
   * @Id on a byte[] array class member.
   */
  public void validateAnnotatedByteArray(AnnotateByteArray artifact, byte[] arrayOrig, byte bTest) {
    assertNotNull( artifact);
    assertNotNull( artifact.id);
    assertTrue(Arrays.equals(artifact.getByteName(), arrayOrig));
    assertEquals( bTest, artifact.getId());
  }

  /*
   * Purpose : This test is to validate exist method with Negative number POJO
   * instance @Id field value with Negative numbers.
   */

  @Test
  public void testPOJORepoExistWithNegativeId() {
    PojoRepository<Artifact, Long> pojoReposProducts = client.newPojoRepository(Artifact.class, Long.class);
    long longId = getOneNegativeLongId();
    // Load two objects into database
    pojoReposProducts.write(this.setArtifact(longId), "odd", "numbers");

    assertTrue(pojoReposProducts.exists(longId));
  }

  /*
   * Purpose : This test is to validate read documents using read(Id) POJO
   * instance @Id field value with Negative numbers.
   */

  @Test
  public void testPOJORepoReadWithNegativeId() {
    PojoRepository<Artifact, Long> pojoReposProducts = client.newPojoRepository(Artifact.class, Long.class);

    // Load two objects into database
    long longId = getOneNegativeLongId();
    pojoReposProducts.write(this.setArtifact(longId), "odd", "numbers");

    // Validate the artifact read back.
    Artifact artifact = pojoReposProducts.read(longId);
    validateArtifact(artifact, longId);
  }

  /*
   * Purpose : This test is to validate delete documents using delete(Id) POJO
   * instance @Id field value with Negative numbers. Expect
   * ResourceNotFoundException when there are no URI found. As per Git # 188.
   */
	@Test
  public void testPOJORepoDeleteWithNegativeId() {
    PojoRepository<Artifact, Long> pojoReposProducts = client.newPojoRepository(Artifact.class, Long.class);

    // Load two objects into database
    long longId = getOneNegativeLongId();
    pojoReposProducts.write(this.setArtifact(longId), "odd", "numbers");

    // Delete the object
    pojoReposProducts.delete(longId);
    assertThrows(ResourceNotFoundException.class, () -> pojoReposProducts.read(longId));
  }

  /*
   * Purpose : This test is to validate delete documents using delete(ID....)
   * POJO instance @Id field value with Negative numbers. Expect
   * ResourceNotFoundException when there are no URI found. As per Git # 188.
   */

  @Test
  public void testPOJORepoDeleteWithNegativeIdArray() {
    long longId1 = getOneNegativeLongId();
    long longId2 = getOneNegativeLongId();

    Long[] pojoReposProductsIdLongArray = { longId1, longId2 };

    PojoRepository<Artifact, Long> pojoReposProducts = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> pojoArtifactPage;

    // Load the object into database
    pojoReposProducts.write(this.setArtifact(longId1), "odd", "numbers");
    pojoReposProducts.write(this.setArtifact(longId2), "even", "numbers");

    // Validate the artifacts read back.
    pojoArtifactPage = pojoReposProducts.read(pojoReposProductsIdLongArray);

    assertEquals( 2, pojoArtifactPage.size());

    pojoReposProducts.delete(longId1, longId2);

    pojoArtifactPage = pojoReposProducts.read(pojoReposProductsIdLongArray);

    assertEquals( 0, pojoArtifactPage.size());

    System.out.println("The count of items in this page " + pojoArtifactPage.size());

    // Validate the artifacts read back is zero.
    pojoArtifactPage = pojoReposProducts.read(pojoReposProductsIdLongArray);
  }

  /*
   * Purpose : This test is to validate read documents using read(ID[]) POJO
   * instance @Id field value with Negative numbers.
   */

  @Test
  public void testPOJORepoReadWithNegativeIdArray() {

    long longId1 = getOneNegativeLongId();
    long longId2 = getOneNegativeLongId();

    Long[] pojoReposProductsIdLongArray = { longId1, longId2 };

    PojoRepository<Artifact, Long> pojoReposProducts = client.newPojoRepository(Artifact.class, Long.class);
    PojoPage<Artifact> pojoArtifactPage;

    // Load the object into database
    pojoReposProducts.write(this.setArtifact(longId1), "odd", "numbers");
    pojoReposProducts.write(this.setArtifact(longId2), "even", "numbers");

    // Validate the artifacts read back.
    pojoArtifactPage = pojoReposProducts.read(pojoReposProductsIdLongArray);

    System.out.println("The count of items in this page " + pojoArtifactPage.size());
    assertEquals( 2, pojoArtifactPage.size());

    System.out.println("The number of pages covering all possible items " + pojoArtifactPage.getTotalPages());
    assertEquals( 1, pojoArtifactPage.getTotalPages());

    System.out.println("The page number within the count of all possible pages " + pojoArtifactPage.getPageNumber());
    assertEquals( 1, pojoArtifactPage.getPageNumber());

    System.out.println("The page size which is the maximum number of items allowed in one page " + pojoArtifactPage.getPageSize());
    assertEquals( 2, pojoArtifactPage.getPageSize());

    System.out.println("The start position of this page within all possible items " + pojoArtifactPage.getStart());
    assertEquals( 1, pojoArtifactPage.getStart());

    System.out.println("The total count (potentially an estimate) of all possible items in the set " + pojoArtifactPage.getTotalSize());
    assertEquals(2, pojoArtifactPage.getTotalSize());

    Iterator<Artifact> itr = pojoArtifactPage.iterator();
    Artifact artifact = null;
    while (itr.hasNext()) {
      // To validate read of multiple objects returned we need to branch based
      // on Artifact ID.
      artifact = pojoArtifactPage.iterator().next();
      if (artifact.getId() == longId1)
        validateArtifact(artifact, longId1);
      else if (artifact.getId() == longId2)
        validateArtifactTwo(artifact, longId2);
    }

    System.out.println("Is this Last page :" + pojoArtifactPage.hasContent() + pojoArtifactPage.isLastPage());
  }

  /*
   * Purpose : This test is to validate read documents using read(ID) with
   * default value. POJO instance @Id field value with Negative numbers.
   * Artifact class instance's Id field should be holding the 0L value.
   */
  @Test
  public void testPOJORepoReadWithDefaultId() {
    PojoRepository<Artifact, Long> pojoReposProducts = client.newPojoRepository(Artifact.class, Long.class);

    // Load the object into database
    pojoReposProducts.write(this.setArtifactWithDefault(), "odd", "numbers");

    // Validate the artifact read back.
    Artifact artifact = pojoReposProducts.read(0L);
    validateArtifactWithDefault(artifact);
  }

  /*
   * Purpose : This test is to validate read documents using read(ID) with null
   * value. POJO instance @Id field value with Negative numbers. Artifact class
   * instance's Id field should be holding a valid value. The manufacturer value
   * is set to null. Id field values cannot set to null due to Java language
   * defaults.
   */
  @Test
  public void testPOJORepoReadWithNull() {
    PojoRepository<Artifact, Long> pojoReposProducts = client.newPojoRepository(Artifact.class, Long.class);

    // Load the object into database
    long longId = getOneNegativeLongId();
    pojoReposProducts.write(this.setArtifactWithNull(longId), "odd", "numbers");

    // Validate the artifact read back.
    Artifact artifact = pojoReposProducts.read(longId);
    validateArtifactWithNull(artifact, longId);
  }

  /*
   * Purpose : This test is to validate read documents using read(Id) POJO
   * instance @Id field value with Special Characters - Single quote.
   */

  @Test
  public void testPOJORepoReadWithSingleQuotes() {
    PojoRepository<SpecialArtifact, String> pojoReposProductsString = client.newPojoRepository(SpecialArtifact.class, String.class);

    // Load the object into database
    long longId = getOneNegativeLongId();
    String artifactName = new String("Acme\'s Inc.");
    pojoReposProductsString.write(this.setSpecialArtifactWithSpecialCharacter(longId, artifactName), "odd", "numbers");

    // Validate the artifact read back.
    SpecialArtifact artifact1 = pojoReposProductsString.read(artifactName);
    validateSpecialArtifactWithSpecialCharacter(artifact1, artifactName, longId);
  }

  /*
   * Purpose : This test is to validate read documents using read(Id) POJO
   * instance @Id field value with Special Characters - Double quote.
   */

  @Test
  public void testPOJORepoReadWithDoubleQuotes() {
    PojoRepository<SpecialArtifact, String> pojoReposProductsString = client.newPojoRepository(SpecialArtifact.class, String.class);

    // Load the object into database
    long longId = getOneNegativeLongId();
    String artifactName = new String("Acme\"s Inc.");
    pojoReposProductsString.write(this.setSpecialArtifactWithSpecialCharacter(longId, artifactName), "odd", "numbers");

    // Validate the artifact read back.
    SpecialArtifact artifact1 = pojoReposProductsString.read(artifactName);
    validateSpecialArtifactWithSpecialCharacter(artifact1, artifactName, longId);
  }

  /*
   * Purpose : This test is to validate read documents using read(Id) POJO
   * instance @Id field value with Special Characters - Double Asteriks.
   */

  @Test
  public void testPOJORepoReadWithDoubleAsteriks() {
    PojoRepository<SpecialArtifact, String> pojoReposProductsString = client.newPojoRepository(SpecialArtifact.class, String.class);

    // Load the object into database
    long longId = getOneNegativeLongId();
    String artifactName = new String("Acmes ** Inc.");
    pojoReposProductsString.write(this.setSpecialArtifactWithSpecialCharacter(longId, artifactName), "odd", "numbers");

    // Validate the artifact read back.
    SpecialArtifact artifact1 = pojoReposProductsString.read(artifactName);
    validateSpecialArtifactWithSpecialCharacter(artifact1, artifactName, longId);
  }

  /*
   * Purpose : This test is to validate exist and read documents using exist(id)
   * and read(Id) POJO instance @Id field value with Special Characters - UTF-8.
   * Uses SpecialArtifact class which has @Id on the name methods.
   */

  @Test
  public void testPOJORepoReadWithUTF8() {
    PojoRepository<SpecialArtifact, String> pojoReposProductsString = client.newPojoRepository(SpecialArtifact.class, String.class);

    // Load the object into database
    long longId = getOneNegativeLongId();
    String artifactName = new String("mult-title:万里长城");
    pojoReposProductsString.write(this.setSpecialArtifactWithSpecialCharacter(longId, artifactName), "odd", "numbers");

    assertTrue(pojoReposProductsString.exists(artifactName));

    // Validate the artifact read back.
    SpecialArtifact artifact1 = pojoReposProductsString.read(artifactName);
    validateSpecialArtifactWithSpecialCharacter(artifact1, artifactName, longId);
  }

  /*
   * Purpose : This test is to validate read documents using read(Id) POJO
   * instance @Id on getter and setter methods instead of @Id on a property with
   * Negative numbers. Uses SpArtifactWithGetSetId class which has private
   * members with public get and set methods. The @Id annotation is on these get
   * and set method.
   */

  @Test
  public void testPOJORepoReadWithNegativeIdOnMethods() {
    PojoRepository<SpArtifactWithGetSetId, Long> pojoReposProducts = client.newPojoRepository(SpArtifactWithGetSetId.class, Long.class);

    // Load two objects into database
    long longId = getOneNegativeLongId();
    String artifactName = new String("mult-title:万里长城");
    pojoReposProducts.write(this.setSpArtifactonMethod(longId, artifactName), "odd", "numbers");

    // Validate the artifact read back.
    SpArtifactWithGetSetId artifact = pojoReposProducts.read(longId);
    validateSpArtifactWithSpecialCharacter(artifact, artifactName, longId);
  }

  /*
   * Purpose : This test is to validate search documents using Special
   * Characters. search(QueryDefinition query, long start) Special Characters -
   * UTF-8 is used. Uses SpecialArtifact class which has @Id on the name
   * methods.
   */

  @Test
  public void testPOJORepoSearchWithUTF8NoTransaction() {
    PojoRepository<SpecialArtifact, String> pojoReposProductsString = client.newPojoRepository(SpecialArtifact.class, String.class);

    SpecialArtifact searchSpArtifact = null;

    // Load the object into database
    long longId = getOneNegativeLongId();
    String artifactName = new String("万里长城" + longId);
    pojoReposProductsString.write(this.setSpecialArtifactWithSpecialCharacter(longId, artifactName), "odd", "numbers");

    assertTrue(pojoReposProductsString.exists(artifactName));

    // Validate the artifact search.
    StringQueryDefinition query = client.newQueryManager().newStringDefinition();
    query.setCriteria(artifactName);

    PojoPage<SpecialArtifact> pojoSpecialArtifactPage = pojoReposProductsString.search(query, 1);
    Iterator<SpecialArtifact> iter = pojoSpecialArtifactPage.iterator();
    while (iter.hasNext()) {
      searchSpArtifact = iter.next();
      validateSpecialArtifactWithSpecialCharacter(searchSpArtifact, artifactName, longId);
    }
  }

  /*
   * Purpose : This test is to validate write and read byte array Uses Id class
   * member which has @Id.
   */

  @Test
  public void testPOJORepoReadWriteByteArray() {
    PojoRepository<ByteArrayId, Byte> pojoReposProductsString = client.newPojoRepository(ByteArrayId.class, Byte.class);

    // Load the object into database
    String artifactName = new String("Byte Array");
    byte[] testByteArray = artifactName.getBytes();
    ByteArrayId byteArrayIdObj = new ByteArrayId();
    byte b = 8;

    byteArrayIdObj.setByteName(testByteArray);
    byteArrayIdObj.setId((byte) 8);
    pojoReposProductsString.write(byteArrayIdObj, "odd", "numbers");

    // Validate the artifact read back.
    ByteArrayId artifact1 = pojoReposProductsString.read(b);
    validateByteArray(artifact1, testByteArray, b);
  }

  /*
   * Purpose : This test is to validate write and read byte array with
   * Annotation Uses byte[] byteName class member which has @Id.
   */

  @Test
  public void testPOJORepoReadWriteAnnotatedByteArray() {
    PojoRepository<AnnotateByteArray, byte[]> pojoReposProductsString = client.newPojoRepository(AnnotateByteArray.class, byte[].class);

    // Load the object into database
    String artifactName = new String("Byte Array");
    byte[] testByteArray = artifactName.getBytes();
    AnnotateByteArray byteArrayIdObj = new AnnotateByteArray();
    byte b = 8;

    byteArrayIdObj.setByteName(testByteArray);
    byteArrayIdObj.setId((byte) 8);
    pojoReposProductsString.write(byteArrayIdObj, "odd", "numbers");

    // Validate the artifact read back.
    AnnotateByteArray artifact1 = pojoReposProductsString.read(testByteArray);
    validateAnnotatedByteArray(artifact1, testByteArray, b);
  }

  @Test
  public void testPageClose() {
    PojoRepository<AnnotateByteArray, byte[]> pojoReposProductsString = client.newPojoRepository(AnnotateByteArray.class, byte[].class);
    pojoReposProductsString.setPageLength(5);
    // Load the object into database
    for (int i = 1; i < 501; i++) {
      String artifactName = new String("Byte Array test " + i);
      byte[] testByteArray = artifactName.getBytes();
      AnnotateByteArray byteArrayIdObj = new AnnotateByteArray();
      byte b = (byte) i;

      byteArrayIdObj.setByteName(testByteArray);
      byteArrayIdObj.setId(b);
      pojoReposProductsString.write(byteArrayIdObj, "odd", "numbers");
    }
    // Validate the artifact read back.
    PojoPage<AnnotateByteArray> page;
    int count = 1, i;
    for (i = 1; count < 501; i++) {
      page = pojoReposProductsString.readAll(count);
      while (page.hasNext()) {
        String artifactName = new String("Byte Array test " + count);
        byte[] testByteArray = artifactName.getBytes();
        assertNotNull( page.next());
        count++;
      }
      page.close();
    }
    assertEquals( 101, i);
  }
}
