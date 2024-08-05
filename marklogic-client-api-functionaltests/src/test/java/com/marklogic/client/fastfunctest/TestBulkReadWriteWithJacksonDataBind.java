/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.functionaltest.Product;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonDatabindHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;



/*
 * This test is designed to to test all of bulk reads and write of JSON  with JacksonDataBindHandle Manager by passing set of uris
 * and also by descriptors.
 */

public class TestBulkReadWriteWithJacksonDataBind extends AbstractFunctionalTest {
  private static final String DIRECTORY = "/";

  @BeforeEach
  public void testSetup() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    // create new connection for each test below
    client = getDatabaseClient("rest-admin", "x", getConnType());
  }

  @AfterEach
  public void testCleanUp() throws Exception {
    System.out.println("Running CleanUp script");
    // release client
    client.release();
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

  public void validateMetadata(DocumentMetadataHandle mh) {
    // get metadata values
    DocumentProperties properties = mh.getProperties();
    DocumentPermissions permissions = mh.getPermissions();
    DocumentCollections collections = mh.getCollections();

    // Properties
    // String expectedProperties =
    // "size:5|reviewed:true|myInteger:10|myDecimal:34.56678|myCalendar:2014|myString:foo|";
    String actualProperties = getDocumentPropertiesString(properties);
    boolean result = actualProperties.contains("size:5|");
    assertTrue( result);

    // Permissions
    String actualPermissions = getDocumentPermissionsString(permissions);
    System.out.println(actualPermissions);

    assertTrue(
        actualPermissions.contains("size:6"));
    // assertTrue(
    // "Document permissions difference in flexrep-eval permission",
    // actualPermissions.contains("flexrep-eval:[READ]"));
    assertTrue(
        actualPermissions.contains("rest-reader:[READ]"));
    assertTrue(
        actualPermissions.contains("rest-writer:[UPDATE]"));
    assertTrue(
        (actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions
            .contains("app-user:[READ, UPDATE]")));
    assertTrue( actualPermissions.contains("harmonized-updater:[UPDATE]"));
    assertTrue( actualPermissions.contains("harmonized-reader:[READ]"));

    // Collections
    String actualCollections = getDocumentCollectionsString(collections);
    System.out.println(collections);

    assertTrue(
        actualCollections.contains("size:2"));
    assertTrue(
        actualCollections.contains("my-collection1"));
    assertTrue(
        actualCollections.contains("my-collection2"));
  }

  public void validateDefaultMetadata(DocumentMetadataHandle mh) {
    // get metadata values
    DocumentProperties properties = mh.getProperties();
    DocumentPermissions permissions = mh.getPermissions();
    DocumentCollections collections = mh.getCollections();

    // Properties
    String actualProperties = getDocumentPropertiesString(properties);
    boolean result = actualProperties.contains("size:0|");
    System.out.println(actualProperties + result);
    assertTrue( result);

    // Permissions
    String actualPermissions = getDocumentPermissionsString(permissions);

    assertTrue( actualPermissions.contains("size:2"));
    // assertTrue(
    // actualPermissions.contains("flexrep-eval:[READ]"));
    assertTrue( actualPermissions.contains("rest-reader:[READ]"));
    assertTrue( actualPermissions.contains("rest-writer:[UPDATE]"));

    // Collections
    String expectedCollections = "size:0|";
    String actualCollections = getDocumentCollectionsString(collections);

    assertEquals( expectedCollections, actualCollections);
  }

  /*
   * This method is place holder to test JacksonDatabindHandle handling file
   * writing / reading (Streams)
   */
  @Test
  public void testWriteMultipleJSONFiles() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    String docId = "/";

    // These files need to be in
    // src/test/java/com/marklogic/client/functionaltest/data/ folder.
    String jsonFilename1 = "product-apple.json";
    String jsonFilename2 = "product-microsoft.json";
    String jsonFilename3 = "product-hp.json";

    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);
    // put meta-data
    DocumentMetadataHandle mh = setMetadata();

    // Read from file system 3 files and write them into the database.
    writeDocumentUsingOutputStreamHandle(client, jsonFilename1, docId, mh, "JSON");
    writeDocumentUsingOutputStreamHandle(client, jsonFilename2, docId, mh, "JSON");
    writeDocumentUsingOutputStreamHandle(client, jsonFilename3, docId, mh, "JSON");

    // Read it back into JacksonDatabindHandle Product
    JacksonDatabindHandle<Product> handleRead = new JacksonDatabindHandle<Product>(Product.class);

    // Read into JacksonDatabindHandle
    docMgr.read(docId + jsonFilename1, handleRead);
    Product product1 = (Product) handleRead.get();

    docMgr.read(docId + jsonFilename2, handleRead);
    Product product2 = (Product) handleRead.get();

    docMgr.read(docId + jsonFilename3, handleRead);
    Product product3 = (Product) handleRead.get();

    assertTrue( product1.getName().equalsIgnoreCase("iPhone 6"));
    assertTrue( product1.getIndustry().equalsIgnoreCase("Mobile Hardware"));
    assertTrue( product1.getDescription().equalsIgnoreCase("Bending Iphone"));

    assertTrue( product2.getName().equalsIgnoreCase("Windows 10"));
    assertTrue( product2.getIndustry().equalsIgnoreCase("Software"));
    assertTrue( product2.getDescription().equalsIgnoreCase("OS Server"));

    assertTrue( product3.getName().equalsIgnoreCase("Elite Book"));
    assertTrue( product3.getIndustry().equalsIgnoreCase("PC Hardware"));
    assertTrue( product3.getDescription().equalsIgnoreCase("Very cool laptop"));
  }

  @Test
  public void testWriteMultipleJSONDocsFromStrings() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    String docId[] = { "/iphone.json", "/imac.json", "/ipad.json" };
    String json1 = new String("{ \"name\":\"iPhone 6\" , \"industry\":\"Mobile Phone\" , \"description\":\"New iPhone 6\"}");
    String json2 = new String("{ \"name\":\"iMac\" , \"industry\":\"Desktop\", \"description\":\"Air Book OS X\" }");
    String json3 = new String("{ \"name\":\"iPad\" , \"industry\":\"Tablet\", \"description\":\"iPad Mini\" }");

    DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);
    DocumentWriteSet writeset = docMgr.newWriteSet();
    // put meta-data
    DocumentMetadataHandle mh = setMetadata();

    JacksonDatabindHandle<String> handle1 = new JacksonDatabindHandle<String>(String.class);
    JacksonDatabindHandle<String> handle2 = new JacksonDatabindHandle<String>(String.class);
    JacksonDatabindHandle<String> handle3 = new JacksonDatabindHandle<String>(String.class);

    writeset.addDefault(mh);
    handle1.set(json1);
    handle2.set(json2);
    handle3.set(json3);

    writeset.add(docId[0], handle1);
    writeset.add(docId[1], handle2);
    writeset.add(docId[2], handle3);

    docMgr.write(writeset);

    // Read it back into JacksonDatabindHandle Product
    JacksonDatabindHandle<Product> jacksonDBReadHandle = new JacksonDatabindHandle<Product>(Product.class);
    docMgr.read(docId[0], jacksonDBReadHandle);
    Product product1 = (Product) jacksonDBReadHandle.get();

    assertTrue( product1.getName().equalsIgnoreCase("iPhone 6"));
    assertTrue( product1.getIndustry().equalsIgnoreCase("Mobile Phone"));
    assertTrue( product1.getDescription().equalsIgnoreCase("New iPhone 6"));

    docMgr.readMetadata(docId[0], mhRead);
    validateMetadata(mhRead);

    docMgr.read(docId[1], jacksonDBReadHandle);
    Product product2 = (Product) jacksonDBReadHandle.get();
    assertTrue( product2.getName().equalsIgnoreCase("iMac"));
    assertTrue( product2.getIndustry().equalsIgnoreCase("Desktop"));
    assertTrue( product2.getDescription().equalsIgnoreCase("Air Book OS X"));

    docMgr.readMetadata(docId[1], mhRead);
    validateMetadata(mhRead);

    docMgr.read(docId[2], jacksonDBReadHandle);
    Product product3 = (Product) jacksonDBReadHandle.get();
    assertTrue( product3.getName().equalsIgnoreCase("iPad"));
    assertTrue( product3.getIndustry().equalsIgnoreCase("Tablet"));
    assertTrue( product3.getDescription().equalsIgnoreCase("iPad Mini"));

    docMgr.readMetadata(docId[2], mhRead);
    validateMetadata(mhRead);
  }

  /*
   * Purpose: To test newFactory method with custom Pojo instances.
   */
  @Test
  public void testJacksonDataBindHandleFromFactory() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    String docId[] = { "/iphone.json", "/imac.json", "/ipad.json" };

    // Create three custom POJO instances

    Product newProduct1 = new Product();
    newProduct1.setName("iPhone 6");
    newProduct1.setIndustry("Mobile Phone");
    newProduct1.setDescription("New iPhone 6");

    Product newProduct2 = new Product();
    newProduct2.setName("iMac");
    newProduct2.setIndustry("Desktop");
    newProduct2.setDescription("Air Book OS X");

    Product newProduct3 = new Product();
    newProduct3.setName("iPad");
    newProduct3.setIndustry("Tablet");
    newProduct3.setDescription("iPad Mini");

    // Create a content Factory from JacksonDatabindHandle that will handle POJO
    // class type.
    ContentHandleFactory ch = JacksonDatabindHandle.newFactory(Product.class);

    // Instantiate a handle for each POJO instance.
    JacksonDatabindHandle<Product> handle1 = (JacksonDatabindHandle<Product>) ch.newHandle(Product.class);
    JacksonDatabindHandle<Product> handle2 = (JacksonDatabindHandle<Product>) ch.newHandle(Product.class);
    JacksonDatabindHandle<Product> handle3 = (JacksonDatabindHandle<Product>) ch.newHandle(Product.class);

    // Assigns the custom POJO as the content.
    handle1.set(newProduct1);
    handle2.set(newProduct2);
    handle3.set(newProduct3);

    // Specifies the format of the content.
    handle1.withFormat(Format.JSON);
    handle2.withFormat(Format.JSON);
    handle3.withFormat(Format.JSON);

    DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);
    DocumentWriteSet writeset = docMgr.newWriteSet();
    // put meta-data
    DocumentMetadataHandle mh = setMetadata();

    writeset.addDefault(mh);

    writeset.add(docId[0], handle1);
    writeset.add(docId[1], handle2);
    writeset.add(docId[2], handle3);

    docMgr.write(writeset);

    // Read it back into JacksonDatabindHandle Product
    JacksonDatabindHandle<Product> jacksonDBReadHandle = new JacksonDatabindHandle<Product>(Product.class);
    docMgr.read(docId[0], jacksonDBReadHandle);
    Product product1 = (Product) jacksonDBReadHandle.get();

    assertTrue( product1.getName().equalsIgnoreCase("iPhone 6"));
    assertTrue( product1.getIndustry().equalsIgnoreCase("Mobile Phone"));
    assertTrue( product1.getDescription().equalsIgnoreCase("New iPhone 6"));

    docMgr.readMetadata(docId[0], mhRead);
    validateMetadata(mhRead);

    docMgr.read(docId[1], jacksonDBReadHandle);
    Product product2 = (Product) jacksonDBReadHandle.get();
    assertTrue( product2.getName().equalsIgnoreCase("iMac"));
    assertTrue( product2.getIndustry().equalsIgnoreCase("Desktop"));
    assertTrue( product2.getDescription().equalsIgnoreCase("Air Book OS X"));

    docMgr.readMetadata(docId[1], mhRead);
    validateMetadata(mhRead);

    docMgr.read(docId[2], jacksonDBReadHandle);
    Product product3 = (Product) jacksonDBReadHandle.get();
    assertTrue( product3.getName().equalsIgnoreCase("iPad"));
    assertTrue( product3.getIndustry().equalsIgnoreCase("Tablet"));
    assertTrue( product3.getDescription().equalsIgnoreCase("iPad Mini"));

    docMgr.readMetadata(docId[2], mhRead);
    validateMetadata(mhRead);
  }

  /*
   * Purpose: To test Git Issue # 89. Issue Description: If you read more than
   * 100 JSON objects, the Client API stops reading them.
   *
   * Use one Jackson Handles instance.
   */
  @Test
  public void testSingleJacksonHandlerHundredJsonDocs() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    JacksonHandle jh = new JacksonHandle();
    jh.withFormat(Format.JSON);
    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);
    docMgr.setNonDocumentFormat(Format.JSON);
    DocumentWriteSet writeset = docMgr.newWriteSet();
    // put meta-data
    DocumentMetadataHandle mh = setMetadata();
    writeset.addDefault(mh);

    JacksonDatabindHandle<String> handle1 = new JacksonDatabindHandle<>(String.class);

    Map<String, String> jsonMap = new HashMap<>();
    String[] uris = new String[150];

    String dir = new String("/");
    String mapDocId = null;
    StringBuffer mapDocContent = new StringBuffer();
    for (int i = 0; i < 102; i++)
    {
      mapDocId = dir + Integer.toString(i);
      mapDocContent.append("{\"content\":\"");
      mapDocContent.append(Integer.toString(i));
      mapDocContent.append("\"}");

      jsonMap.put(mapDocId, mapDocContent.toString());

      handle1.set(mapDocContent.toString());
      writeset.add(mapDocId, handle1);

      uris[i] = mapDocId;

      mapDocContent.setLength(0);
      mapDocId = null;
      docMgr.write(writeset);
      writeset.clear();
    }

    int count = 0;

    DocumentPage page = docMgr.read(uris);
    DocumentRecord rec;

    while (page.hasNext()) {
      rec = page.next();

      assertNotNull( rec);
      assertNotNull( rec.getUri());
      assertTrue(rec.getUri().startsWith(DIRECTORY));

      rec.getContent(jh);
      // Verify the contents: comparing Map with JacksonHandle's.
      assertEquals( jsonMap.get(rec.getUri()), jh.get().toString());
      count++;
    }
    assertEquals( 102, count);
    // Issue #294 DocumentPage.size() should return correct size
    assertTrue(page.size() == 102);

  }

  /*
   * Purpose: To test Git Issue # 89. Issue Description: If you read more than
   * 100 JSON objects, the Client API stops reading them.
   *
   * Use multiple Jackson Handle instances.
   */
  @Test
  public void testMultipleJacksonHandleHundredJsonDocs1() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);
    docMgr.setNonDocumentFormat(Format.JSON);
    DocumentWriteSet writeset = docMgr.newWriteSet();
    // put meta-data
    DocumentMetadataHandle mh = setMetadata();
    writeset.addDefault(mh);

    JacksonDatabindHandle<String> handle1 = new JacksonDatabindHandle<>(String.class);

    Map<String, String> jsonMap = new HashMap<>();
    String[] uris = new String[150];

    String dir = new String("/");
    String mapDocId = null;
    StringBuffer mapDocContent = new StringBuffer();
    for (int i = 0; i < 102; i++)
    {
      mapDocId = dir + Integer.toString(i);
      mapDocContent.append("{\"content\":\"");
      mapDocContent.append(Integer.toString(i));
      mapDocContent.append("\"}");

      jsonMap.put(mapDocId, mapDocContent.toString());

      handle1.set(mapDocContent.toString());
      writeset.add(mapDocId, handle1);

      uris[i] = mapDocId;

      mapDocContent.setLength(0);
      mapDocId = null;
      docMgr.write(writeset);
      writeset.clear();
    }

    int count = 0;

    DocumentPage page = docMgr.read(uris);
    DocumentRecord rec;

    JacksonHandle jh = new JacksonHandle();
    jh.withFormat(Format.JSON);
    while (page.hasNext()) {
      rec = page.next();

      assertNotNull( rec);
      assertNotNull( rec.getUri());
      assertTrue(rec.getUri().startsWith(DIRECTORY));

      rec.getContent(jh);
      // Verify the contents: comparing Map with JacksonHandle's.
      assertEquals( jsonMap.get(rec.getUri()), jh.get().toString());
      count++;
    }
    assertEquals( 102, count);

  }
}
