/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.*;
import com.marklogic.client.document.DocumentPatchBuilder.PathLanguage;
import com.marklogic.client.functionaltest.Product;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.*;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.skyscreamer.jsonassert.JSONAssert;

import jakarta.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class TestMetadata extends AbstractFunctionalTest {

  @BeforeAll
  public static void setUp() throws Exception
  {
    createRoleWithNodeUpdate("elsrole", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createRESTUser("elsuser", "x", "elsrole", "rest-admin", "rest-writer", "rest-reader", "rest-extension-user", "manage-user");
  }

  @Test
  public void testBinaryMetadataBytesHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testBinaryMetadataBytesHandle");

    String filename = "Simple_ScanTe.png";

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());
    StringBuffer calProperty = new StringBuffer("myCalendar:").append(Calendar.getInstance().get(Calendar.YEAR));
    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
    // put metadata
    metadataHandle.getCollections().addAll("my-collection");
    metadataHandle.getCollections().addAll("another-collection");
    metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
    metadataHandle.getProperties().put("reviewed", true);
    metadataHandle.getProperties().put("myString", "foo");
    metadataHandle.getProperties().put("myInteger", 10);
    metadataHandle.getProperties().put("myDecimal", 34.56678);
    metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
    metadataHandle.setQuality(23);
    // Put key, value metadata
    metadataHandle.getMetadataValues().add("Name", "kiran");
    metadataHandle.getMetadataValues().put("Company", "MarkLogic");
    metadataHandle.getMetadataValues().put("Location", "SanCarlos");
    metadataHandle.getMetadataValues().add("zip", "95050");
    metadataHandle.getMetadataValues().add("Locations", "10");

    // write the doc with the metadata
    writeDocumentUsingBytesHandle(client, filename, "/write-bin-byteshandle-metadata/", metadataHandle, "Binary");

    // create handle to read metadata
    DocumentMetadataHandle readMetadataHandle = new DocumentMetadataHandle();

    // read metadata
    readMetadataHandle = readMetadataFromDocument(client, "/write-bin-byteshandle-metadata/" + filename, "Binary");

    // get metadata values
    DocumentProperties properties = readMetadataHandle.getProperties();
    DocumentPermissions permissions = readMetadataHandle.getPermissions();
    DocumentCollections collections = readMetadataHandle.getCollections();
    DocumentMetadataValues metadatavalues = readMetadataHandle.getMetadataValues();

    // Properties
    String actualProperties = getDocumentPropertiesString(properties);
    System.out.println("Returned properties: " + actualProperties);

    assertTrue( actualProperties.contains("size:5"));
    assertTrue( actualProperties.contains("reviewed:true"));
    assertTrue( actualProperties.contains("myInteger:10"));
    assertTrue( actualProperties.contains("myDecimal:34.56678"));
    assertTrue( actualProperties.contains(calProperty.toString()));
    assertTrue( actualProperties.contains("myString:foo"));

    // Permissions
    String actualPermissions = getDocumentPermissionsString(permissions);
    System.out.println("Returned permissions: " + actualPermissions);

    assertTrue( actualPermissions.contains("size:5"));
    assertTrue( actualPermissions.contains("harmonized-updater:[UPDATE]"));
    assertTrue( actualPermissions.contains("harmonized-reader:[READ]"));
    assertTrue( actualPermissions.contains("rest-reader:[READ]"));
    assertTrue( actualPermissions.contains("rest-writer:[UPDATE]"));
    assertTrue(
        (actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions.contains("app-user:[READ, UPDATE]")));

    // Collections
    String actualCollections = getDocumentCollectionsString(collections);
    System.out.println("Returned collections: " + actualCollections);

    assertTrue( actualCollections.contains("size:2"));
    assertTrue( actualCollections.contains("another-collection"));
    assertTrue( actualCollections.contains("my-collection"));

    // Test MetaData Values
    assertTrue( metadatavalues.containsKey("Name"));
    assertTrue( metadatavalues.containsValue("kiran"));
    assertTrue( metadatavalues.containsKey("Company"));
    assertTrue( metadatavalues.containsValue("MarkLogic"));
    assertFalse( metadatavalues.isEmpty());
    // Update MetaData Values
    metadataHandle.getMetadataValues().remove("Name");
    metadataHandle.getMetadataValues().replace("Company", "MarkLogic", "marklogicians");
    metadataHandle.getMetadataValues().remove("Location", "SanCarlos");
    metadataHandle.withMetadataValue("HQ", "USA");
    metadataHandle.getMetadataValues().merge("HQ", "CA", String::concat);
    writeDocumentUsingBytesHandle(client, filename, "/write-bin-byteshandle-metadata/", metadataHandle, "Binary");
    readMetadataHandle = readMetadataFromDocument(client, "/write-bin-byteshandle-metadata/" + filename, "Binary");
    metadatavalues = readMetadataHandle.getMetadataValues();
    assertTrue( metadatavalues.containsKey("HQ"));
    assertTrue( metadatavalues.containsValue("USACA"));
    assertFalse( metadatavalues.containsKey("Name"));
    assertFalse( metadatavalues.containsValue("MarkLogic"));
    assertTrue( metadatavalues.containsValue("marklogicians"));
    assertFalse( metadatavalues.containsValue("SanCarlos"));

    // patch MetaData Values
    XMLDocumentManager docManager = client.newXMLDocumentManager();
    DocumentPatchBuilder patchBuilder = docManager.newPatchBuilder();
    DocumentPatchHandle patchHandle = patchBuilder.addMetadataValue("State", "California")
        .deleteMetadataValue("HQ")
        .replaceMetadataValue("Company", "test-patch")
        .replaceMetadataValueApply("Locations", patchBuilder.call().add(5))
        .build();
    docManager.patch("/write-bin-byteshandle-metadata/" + filename, patchHandle);
    readMetadataHandle = docManager.readMetadata("/write-bin-byteshandle-metadata/" + filename, readMetadataHandle);
    metadatavalues = readMetadataHandle.getMetadataValues();
    BytesHandle contentHandle = new BytesHandle();
    assertTrue(metadatavalues.containsValue("test-patch"));
    assertTrue( metadatavalues.containsValue("15"));
    assertFalse( metadatavalues.containsKey("HQ"));

    // read metadata values using BinaryDocumentManager ReadAs
    DocumentMetadataHandle metaHandle = new DocumentMetadataHandle();
    DocumentMetadataHandle readmetadata = new DocumentMetadataHandle();
    BinaryDocumentManager docman = client.newBinaryDocumentManager();
    docman.readAs("/write-bin-byteshandle-metadata/" + filename, metaHandle, byte[].class);
    docman.read("/write-bin-byteshandle-metadata/" + filename, readmetadata, contentHandle, 0, 100);
    metadatavalues = metaHandle.getMetadataValues();
    assertTrue(metadatavalues.containsValue("test-patch"));
    assertTrue(metadatavalues.containsValue("test-patch"));

    // release the client
    client.release();
  }

  @Test
  public void testMetadataValuesTransaction() throws Exception {
    String filename = "facebook-10443244874876159931";
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
    DocumentMetadataHandle readMetadataHandle = new DocumentMetadataHandle();
    DocumentMetadataValues metadatavalues = readMetadataHandle.getMetadataValues();
    Transaction t1 = null;
    Transaction t2 = null;
    metadataHandle.getMetadataValues().add("key1", "value1");
    metadataHandle.getMetadataValues().add("key2", "value2");
    metadataHandle.getMetadataValues().add("key3", "value3");

    TextDocumentManager docMgr = client.newTextDocumentManager();
    String uri = "/trx-jsonhandle-metadatavalues/";
    String docId = uri + filename;
    FileInputStream fis = null;
    Scanner scanner = null;
    String readContent;
    File file = null;

	try {
		file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);
		fis = new FileInputStream(file);
		scanner = new Scanner(fis).useDelimiter("\\Z");
		readContent = scanner.next();
	} finally {
		fis.close();
	    scanner.close();
	}
    StringHandle contentHandle = new StringHandle();
    contentHandle.set(readContent);
    // write the doc
    docMgr.writeAs(docId, metadataHandle, contentHandle);
    DocumentUriTemplate template = docMgr.newDocumentUriTemplate("Text").withDirectory("/trx-jsonhandle-metadatavalues-template/");

    try {
      // Trx with metadata values rollback scenario
      t1 = client.openTransaction();
      metadataHandle.getMetadataValues().add("keyTrx1", "valueTrx1");
      docMgr.writeMetadata(docId, metadataHandle, t1);
      docMgr.readMetadata(docId, readMetadataHandle, t1);
      assertTrue( metadatavalues.containsValue("valueTrx1"));
      t1.rollback();
      docMgr.readMetadata(docId, readMetadataHandle);
      metadatavalues = readMetadataHandle.getMetadataValues();
      assertFalse( metadatavalues.containsValue("valueTrx1"));

      // Trx with metadata values commit scenario
      t2 = client.openTransaction();
      metadataHandle.getMetadataValues().add("keyTrx2", "valueTrx2");
      DocumentDescriptor desc = docMgr.create(template, metadataHandle, contentHandle, t2);
      String docId1 = desc.getUri();
      docMgr.read(docId1, readMetadataHandle, contentHandle, t2);
      assertTrue( metadatavalues.containsValue("valueTrx2"));
      t2.commit();
      docMgr.readAs(docId1, readMetadataHandle, String.class);
      metadatavalues = readMetadataHandle.getMetadataValues();
      assertTrue(metadatavalues.containsValue("valueTrx2"));
      waitForPropertyPropagate();

      t1 = t2 = null;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (t1 != null) {
        t1.rollback();
        t1 = null;

      } else if (t2 != null) {
        t2.rollback();
        t2 = null;
      }
    }
  }

  @Test
  public void testTextMetadataStringHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testTextMetadataStringHandle");

    String filename = "facebook-10443244874876159931";
    StringBuffer calProperty = new StringBuffer("myCalendar:").append(Calendar.getInstance().get(Calendar.YEAR));

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

    // put metadata
    metadataHandle.getCollections().addAll("my-collection");
    metadataHandle.getCollections().addAll("another-collection");
    metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
    metadataHandle.getProperties().put("reviewed", true);
    metadataHandle.getProperties().put("myString", "foo");
    metadataHandle.getProperties().put("myInteger", 10);
    metadataHandle.getProperties().put("myDecimal", 34.56678);
    metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
    metadataHandle.getProperties().put("emptyProperty", "");
    metadataHandle.setQuality(23);

    // write the doc with the metadata
    writeDocumentUsingStringHandle(client, filename, "/write-text-stringhandle-metadata/", metadataHandle, "Text");

    // create handle to read metadata
    DocumentMetadataHandle readMetadataHandle = new DocumentMetadataHandle();

    // read metadata
    readMetadataHandle = readMetadataFromDocument(client, "/write-text-stringhandle-metadata/" + filename, "Text");

    // get metadata values
    DocumentProperties properties = readMetadataHandle.getProperties();
    DocumentPermissions permissions = readMetadataHandle.getPermissions();
    DocumentCollections collections = readMetadataHandle.getCollections();

    // Properties
    String actualProperties = getDocumentPropertiesString(properties);
    System.out.println("Returned properties: " + actualProperties);

    assertTrue( actualProperties.contains("size:6"));
    assertTrue( actualProperties.contains("reviewed:true"));
    assertTrue( actualProperties.contains("myInteger:10"));
    assertTrue( actualProperties.contains("myDecimal:34.56678"));
    assertTrue( actualProperties.contains(calProperty.toString()));
    assertTrue( actualProperties.contains("emptyProperty:null"));

    // Permissions
    String actualPermissions = getDocumentPermissionsString(permissions);
    System.out.println("Returned permissions: " + actualPermissions);

    assertTrue( actualPermissions.contains("size:5"));
    assertTrue( actualPermissions.contains("harmonized-updater:[UPDATE]"));
    assertTrue( actualPermissions.contains("harmonized-reader:[READ]"));
    assertTrue( actualPermissions.contains("rest-reader:[READ]"));
    assertTrue( actualPermissions.contains("rest-writer:[UPDATE]"));
    assertTrue(
        (actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions.contains("app-user:[READ, UPDATE]")));

    // Collections
    String actualCollections = getDocumentCollectionsString(collections);
    System.out.println("Returned collections: " + actualCollections);

    assertTrue( actualCollections.contains("size:2"));
    assertTrue( actualCollections.contains("another-collection"));
    assertTrue( actualCollections.contains("my-collection"));

    // Write into empty props and read again.
    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();

    DocumentProperties properties1 = metadataHandle1.getProperties();
    properties1.put("emptyProperty", "Not Empty");
    writeDocumentUsingStringHandle(client, filename, "/write-text-stringhandle-metadata/", metadataHandle1, "Text");

    // create handle to read metadata
    DocumentMetadataHandle readMetadataHandle1 = new DocumentMetadataHandle();

    // read metadata
    readMetadataHandle1 = readMetadataFromDocument(client, "/write-text-stringhandle-metadata/" + filename, "Text");

    // get metadata values
    DocumentProperties propertiesRead1 = readMetadataHandle1.getProperties();
    // Properties
    String actualProperties1 = getDocumentPropertiesString(propertiesRead1);
    System.out.println("Returned properties: " + actualProperties1);
    assertTrue( actualProperties1.contains("emptyProperty:Not Empty"));
    // release the client
    client.release();
  }

  @Test
  public void testXMLMetadataJAXBHandle() throws KeyManagementException, NoSuchAlgorithmException, JAXBException, IOException
  {
    System.out.println("Running testXMLMetadataJAXBHandle");
    StringBuffer calProperty = new StringBuffer("myCalendar:").append(Calendar.getInstance().get(Calendar.YEAR));

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    Product product1 = new Product();
    product1.setName("iPad");
    product1.setIndustry("Hardware");
    product1.setDescription("Very cool device");

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

    // put metadata
    metadataHandle.getCollections().addAll("my-collection");
    metadataHandle.getCollections().addAll("another-collection");
    metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
    metadataHandle.getProperties().put("reviewed", true);
    metadataHandle.getProperties().put("myString", "foo");
    metadataHandle.getProperties().put("myInteger", 10);
    metadataHandle.getProperties().put("myDecimal", 34.56678);
    metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
    metadataHandle.setQuality(23);
    metadataHandle.getMetadataValues().add("key1", "value1");
    metadataHandle.getMetadataValues().add("key2", "value2");
    metadataHandle.getMetadataValues().add("key3", "value3");

    // write the doc with the metadata
    writeDocumentUsingJAXBHandle(client, product1, "/write-xml-jaxbhandle-metadata/", metadataHandle, "XML");

    // create handle to read metadata
    DocumentMetadataHandle readMetadataHandle = new DocumentMetadataHandle();

    // read metadata
    readMetadataHandle = readMetadataFromDocument(client, "/write-xml-jaxbhandle-metadata/" + product1.getName() + ".xml", "XML");

    // get metadata values
    DocumentProperties properties = readMetadataHandle.getProperties();
    DocumentPermissions permissions = readMetadataHandle.getPermissions();
    DocumentCollections collections = readMetadataHandle.getCollections();
    DocumentMetadataValues metadatavalues = readMetadataHandle.getMetadataValues();
    // Properties
    String actualProperties = getDocumentPropertiesString(properties);
    System.out.println("Returned properties: " + actualProperties);

    assertTrue( actualProperties.contains("size:5"));
    assertTrue( actualProperties.contains("reviewed:true"));
    assertTrue( actualProperties.contains("myInteger:10"));
    assertTrue( actualProperties.contains("myDecimal:34.56678"));
    assertTrue( actualProperties.contains(calProperty.toString()));
    assertTrue( actualProperties.contains("myString:foo"));

    // Permissions
    String actualPermissions = getDocumentPermissionsString(permissions);
    System.out.println("Returned permissions: " + actualPermissions);

    assertTrue( actualPermissions.contains("size:5"));
    assertTrue( actualPermissions.contains("harmonized-updater:[UPDATE]"));
    assertTrue( actualPermissions.contains("harmonized-reader:[READ]"));
    assertTrue( actualPermissions.contains("rest-reader:[READ]"));
    assertTrue( actualPermissions.contains("rest-writer:[UPDATE]"));
    assertTrue(
        (actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions.contains("app-user:[READ, UPDATE]")));

    // Collections
    String actualCollections = getDocumentCollectionsString(collections);
    System.out.println("Returned collections: " + actualCollections);

    assertTrue( actualCollections.contains("size:2"));
    assertTrue( actualCollections.contains("another-collection"));
    assertTrue( actualCollections.contains("my-collection"));

    // metadata Values
    assertTrue( metadatavalues.containsKey("key1"));

    // release the client
    client.release();
  }

  @Test
  public void testJSONMetadataOutputStreamHandle() throws KeyManagementException, NoSuchAlgorithmException, JAXBException, IOException
  {
    System.out.println("Running testJSONMetadataOutputStreamHandle");

    String filename = "myJSONFile.json";
    StringBuffer calProperty = new StringBuffer("myCalendar:").append(Calendar.getInstance().get(Calendar.YEAR));

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

    // put metadata
    metadataHandle.getCollections().addAll("my-collection");
    metadataHandle.getCollections().addAll("another-collection");
    metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
    metadataHandle.getProperties().put("reviewed", true);
    metadataHandle.getProperties().put("myString", "foo");
    metadataHandle.getProperties().put("myInteger", 10);
    metadataHandle.getProperties().put("myDecimal", 34.56678);
    metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
    metadataHandle.setQuality(23);
    metadataHandle.getMetadataValues().add("key11", "value11");
    metadataHandle.getMetadataValues().add("key22", "value22");

    // write the doc with the metadata
    writeDocumentUsingOutputStreamHandle(client, filename, "/write-json-outputstreamhandle-metadata/", metadataHandle, "JSON");

    // create handle to read metadata
    DocumentMetadataHandle readMetadataHandle = new DocumentMetadataHandle();

    // read metadata
    readMetadataHandle = readMetadataFromDocument(client, "/write-json-outputstreamhandle-metadata/" + filename, "JSON");

    // get metadata values
    DocumentProperties properties = readMetadataHandle.getProperties();
    DocumentPermissions permissions = readMetadataHandle.getPermissions();
    DocumentCollections collections = readMetadataHandle.getCollections();
    DocumentMetadataValues metadatavalues = readMetadataHandle.getMetadataValues();

    // Properties
    String actualProperties = getDocumentPropertiesString(properties);
    System.out.println("Returned properties: " + actualProperties);

    assertTrue( actualProperties.contains("size:5"));
    assertTrue( actualProperties.contains("reviewed:true"));
    assertTrue( actualProperties.contains("myInteger:10"));
    assertTrue( actualProperties.contains("myDecimal:34.56678"));
    assertTrue( actualProperties.contains(calProperty.toString()));
    assertTrue( actualProperties.contains("myString:foo"));

    // Permissions
    String actualPermissions = getDocumentPermissionsString(permissions);
    System.out.println("Returned permissions: " + actualPermissions);

    assertTrue( actualPermissions.contains("size:5"));
    assertTrue( actualPermissions.contains("harmonized-updater:[UPDATE]"));
    assertTrue( actualPermissions.contains("harmonized-reader:[READ]"));
    assertTrue( actualPermissions.contains("rest-reader:[READ]"));
    assertTrue( actualPermissions.contains("rest-writer:[UPDATE]"));
    assertTrue(
        (actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions.contains("app-user:[READ, UPDATE]")));

    // Collections
    String actualCollections = getDocumentCollectionsString(collections);
    System.out.println("Returned collections: " + actualCollections);

    assertTrue( actualCollections.contains("size:2"));
    assertTrue( actualCollections.contains("another-collection"));
    assertTrue( actualCollections.contains("my-collection"));

    // Metadata values
    assertTrue( metadatavalues.containsValue("value22"));

    // release the client
    client.release();
  }

  @Test
  public void testJSONMetadataQName() throws KeyManagementException, NoSuchAlgorithmException, JAXBException, IOException
  {
    System.out.println("Running testJSONMetadataQName");

    String filename = "myJSONFile.json";

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

    // put metadata
    metadataHandle.getProperties().put(new QName("http://www.example.com", "foo"), "bar");

    // write the doc with the metadata
    writeDocumentUsingOutputStreamHandle(client, filename, "/write-json-outputstreamhandle-metadata/", metadataHandle, "JSON");

    // create handle to read metadata
    DocumentMetadataHandle readMetadataHandle = new DocumentMetadataHandle();

    // read metadata
    readMetadataHandle = readMetadataFromDocument(client, "/write-json-outputstreamhandle-metadata/" + filename, "JSON");

    // get metadata values
    DocumentProperties properties = readMetadataHandle.getProperties();

    // Properties
    String expectedProperties = "size:1|{http://www.example.com}foo:bar|";
    String actualProperties = getDocumentPropertiesString(properties);
    System.out.println(actualProperties);
    assertEquals( expectedProperties, actualProperties);

    // release the client
    client.release();
  }

  /*
   * For Git Issue #550 Write a document with metadata that says a permission
   * gives a specific role the node-update capability. Read the metadata for the
   * document and see that it has that permission with that roles assigned
   * node-update capability Use the new role to update the document Use the new
   * role to change document metadata.
   */
  @Test
  public void testNodeUpdateOnJSON() throws IOException, JSONException, KeyManagementException, NoSuchAlgorithmException
  {
    System.out.println("Running testNodeUpdateOnJSON");
    DatabaseClient client = null, elsclient = null;
    try {

      String[] filenames = { "json-original.json" };

      client = getDatabaseClient("rest-writer", "x", getConnType());
      DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

      // write docs
      for (String filename : filenames) {
        writeDocumentUsingInputStreamHandle(client, filename, "/nodeUpdate/", "JSON");
      }

      String docId = "/nodeUpdate/json-original.json";
      JSONDocumentManager docMgr = client.newJSONDocumentManager();
      // Validate the meta-data.
      docMgr.readMetadata(docId, mhRead);

      // Collections
      DocumentCollections collections = mhRead.getCollections();
      DocumentPermissions permissions = mhRead.getPermissions();
      String actualCollections = getDocumentCollectionsString(collections);
      System.out.println("Returned collections: " + actualCollections);

      // Permissions
      String actualPermissions = getDocumentPermissionsString(permissions);
      System.out.println("Returned permissions: " + actualPermissions);

      assertTrue( actualCollections.contains("size:0"));
      assertTrue( actualPermissions.contains("rest-reader:[READ]"));
      assertTrue( actualPermissions.contains("rest-writer:[UPDATE]"));

      DocumentMetadataPatchBuilder patchBldr = docMgr.newPatchBuilder(Format.JSON);

      // Adding the initial meta-data, since there are none.
      patchBldr.addCollection("JSONPatch1", "JSONPatch3");
      // For verifying Git #550
      patchBldr.addPermission("elsrole", DocumentMetadataHandle.Capability.NODE_UPDATE,
          DocumentMetadataHandle.Capability.READ,
          DocumentMetadataHandle.Capability.EXECUTE);

      DocumentMetadataPatchBuilder.PatchHandle patchHandle = patchBldr.build();
      docMgr.patch(docId, patchHandle);

      String content = docMgr.read(docId, new StringHandle()).get();

      System.out.println(content);
      String exp = "{\"employees\": [{\"firstName\":\"John\", \"lastName\":\"Doe\"}," +
          "{\"firstName\":\"Ann\", \"lastName\":\"Smith\"}," +
          "{\"lastName\":\"Foo\"}]}";
      JSONAssert.assertEquals(exp, content, false);

      // Validate the changed meta-data.
      docMgr.readMetadata(docId, mhRead);

      // Collections
      DocumentCollections afterPatchcoll = mhRead.getCollections();
      DocumentPermissions afterPatchperms = mhRead.getPermissions();
      String patchCollStr = getDocumentCollectionsString(afterPatchcoll);
      System.out.println("After patch collections: " + patchCollStr);

      // Permissions
      String patchPermStr = getDocumentPermissionsString(afterPatchperms);
      System.out.println("After patch permissions: " + patchPermStr);

      assertTrue( patchCollStr.contains("size:2"));
      assertTrue( patchCollStr.contains("JSONPatch1"));
      assertTrue( patchCollStr.contains("JSONPatch3"));

      assertTrue( patchPermStr.contains("size:5"));
      assertTrue( actualPermissions.contains("harmonized-updater:[UPDATE]"));
      assertTrue( actualPermissions.contains("harmonized-reader:[READ]"));
      assertTrue( patchPermStr.contains("rest-reader:[READ]"));
      assertTrue( patchPermStr.contains("rest-writer:[UPDATE]"));

      // Should contain elsrole
      assertTrue( patchPermStr.contains("elsrole"));
      // Verify elsrole capability
      String[] elsPerms = patchPermStr.split("elsrole:\\[")[1].split("\\]")[0].split(",");
      assertTrue(
          elsPerms[0].contains("NODE-UPDATE") || elsPerms[1].contains("NODE-UPDATE") || elsPerms[2].contains("NODE-UPDATE"));
      assertTrue(
          elsPerms[0].contains("EXECUTE") || elsPerms[1].contains("EXECUTE") || elsPerms[2].contains("EXECUTE"));
      assertTrue(
          elsPerms[0].contains("READ") || elsPerms[1].contains("READ") || elsPerms[2].contains("READ"));

      // Now use elsrole to do an document update
      elsclient = getDatabaseClient("elsuser", "x", getConnType());

      JSONDocumentManager docMgrEls = elsclient.newJSONDocumentManager();
      DocumentPatchBuilder patchBldrEls = docMgrEls.newPatchBuilder();
      patchBldrEls.pathLanguage(PathLanguage.JSONPATH);

      patchBldrEls.delete("$.employees[2].firstName", DocumentMetadataPatchBuilder.Cardinality.ZERO_OR_MORE);

      DocumentPatchHandle patchHandleEls = patchBldrEls.build();
      docMgr.patch(docId, patchHandleEls);

      String contentEls = docMgrEls.read(docId, new StringHandle()).get();

      System.out.println(contentEls);

      String expEls = "{\"employees\": [{\"firstName\":\"John\", \"lastName\":\"Doe\"}," +
          "{\"firstName\":\"Ann\", \"lastName\":\"Smith\"}," +
          "{\"lastName\":\"Foo\"}]}";
      JSONAssert.assertEquals(expEls, contentEls, false);

      // Try to update properties
      DocumentPatchBuilder patchBldrEls2 = docMgrEls.newPatchBuilder();

      // Adding the new meta-data.
      patchBldrEls2.addCollection("NodeUpdate");

      DocumentMetadataPatchBuilder.PatchHandle patchHandleEls2 = patchBldrEls2.build();
      docMgrEls.patch(docId, patchHandleEls2);
      // Validate the meta-data.
      docMgrEls.readMetadata(docId, mhRead);

      // Collections
      DocumentCollections afterPatchcollTwo = mhRead.getCollections();
      DocumentPermissions afterPatchpermsTwo = mhRead.getPermissions();
      String patchCollStr2 = getDocumentCollectionsString(afterPatchcollTwo);
      System.out.println("ELS collections: " + patchCollStr2);

      assertTrue( patchCollStr2.contains("size:3"));
      assertTrue( patchCollStr2.contains("JSONPatch1"));
      assertTrue( patchCollStr2.contains("JSONPatch3"));
      assertTrue( patchCollStr2.contains("NodeUpdate"));

      // Permissions
      String patchPermStr2 = getDocumentPermissionsString(afterPatchpermsTwo);
      System.out.println("ELS permissions: " + patchPermStr2);

      // Should contain elsrole
      assertTrue( patchPermStr2.contains("elsrole"));
      // Verify elsrole capability
      String[] elsPerms2 = patchPermStr2.split("elsrole:\\[")[1].split("\\]")[0].split(",");
      assertTrue(
          elsPerms2[0].contains("NODE-UPDATE") || elsPerms2[1].contains("NODE-UPDATE") || elsPerms2[2].contains("NODE-UPDATE"));
      assertTrue(
          elsPerms2[0].contains("EXECUTE") || elsPerms2[1].contains("EXECUTE") || elsPerms2[2].contains("EXECUTE"));
      assertTrue(
          elsPerms2[0].contains("READ") || elsPerms2[1].contains("READ") || elsPerms2[2].contains("READ"));
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    } finally {
      // release clients
      client.release();
      if (elsclient != null) {
		elsclient.release();
	}
    }
  }

  @AfterAll
  public static void tearDown() throws Exception
  {
    deleteRESTUser("elsuser");
    deleteUserRole("elsrole");
  }
}
