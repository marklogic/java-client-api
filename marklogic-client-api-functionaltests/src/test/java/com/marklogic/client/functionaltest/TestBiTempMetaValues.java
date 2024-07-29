/*
 * Copyright (c) 2023 MarkLogic Corporation
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

package com.marklogic.client.functionaltest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.Transaction;
import com.marklogic.client.bitemporal.TemporalDocumentManager.ProtectionLevel;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.*;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonDatabindHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.TemporalOperator;
import com.marklogic.client.query.StructuredQueryDefinition;
import org.junit.jupiter.api.*;

import jakarta.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeFactory;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

public class TestBiTempMetaValues extends BasicJavaClientREST {

  private static String dbName = "TestBiTempMetaValues";
  private static String[] fNames = { "TestBiTempMetaValues-1" };
  private static String schemadbName = "TestBiTempMetaValuesSchemaDB";
  private static String[] schemafNames = { "TestBiTempMetaValuesSchemaDB-1" };

  private static DatabaseClient writerClient = null;

  private final static String dateTimeDataTypeString = "dateTime";

  private final static String systemStartERIName = "javaSystemStartERI";
  private final static String systemEndERIName = "javaSystemEndERI";
  private final static String validStartERIName = "javaValidStartERI";
  private final static String validEndERIName = "javaValidEndERI";

  private final static String axisSystemName = "javaERISystemAxis";
  private final static String axisValidName = "javaERIValidAxis";

  private final static String temporalCollectionName = "javaERITemporalCollection";
  private final static String bulktemporalCollectionName = "bulkjavaERITemporalCollection";
  private final static String temporalLsqtCollectionName = "javaERILsqtTemporalCollection";

  private final static String systemNodeName = "System";
  private final static String validNodeName = "Valid";
  private final static String addressNodeName = "Address";
  private final static String uriNodeName = "uri";

  private static String appServerHostname = null;
  private static int restPort = 0;

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);

    ConnectedRESTQA.addRangeElementIndex(dbName, dateTimeDataTypeString, "",
        systemStartERIName);
    ConnectedRESTQA.addRangeElementIndex(dbName, dateTimeDataTypeString, "",
        systemEndERIName);
    ConnectedRESTQA.addRangeElementIndex(dbName, dateTimeDataTypeString, "",
        validStartERIName);
    ConnectedRESTQA.addRangeElementIndex(dbName, dateTimeDataTypeString, "",
        validEndERIName);
    createDB(schemadbName);
    createForest(schemafNames[0], schemadbName);
    // Set the schemadbName database as the Schema database.
    setDatabaseProperties(dbName, "schema-database", schemadbName);

    // Temporal axis must be created before temporal collection associated with
    // those axes is created
    ConnectedRESTQA.addElementRangeIndexTemporalAxis(dbName, axisSystemName,
        "", systemStartERIName, "", systemEndERIName);
    ConnectedRESTQA.addElementRangeIndexTemporalAxis(dbName, axisValidName, "",
        validStartERIName, "", validEndERIName);
    ConnectedRESTQA.addElementRangeIndexTemporalCollection(dbName,
        temporalCollectionName, axisSystemName, axisValidName);
    ConnectedRESTQA.addElementRangeIndexTemporalCollection(dbName,
        bulktemporalCollectionName, axisSystemName, axisValidName);
    ConnectedRESTQA.addElementRangeIndexTemporalCollection(dbName,
        temporalLsqtCollectionName, axisSystemName, axisValidName);
    ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
        temporalLsqtCollectionName, true);
    appServerHostname = getRestAppServerHostName();
    restPort = getRestServerPort();

    createUserRolesWithPrevilages("test-eval-bitemp", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke", "temporal:statement-set-system-time",
            "temporal-document-protect", "temporal-document-wipe");
    createUserRolesWithPrevilages("replaceRoleTest", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");

    createRESTUser("eval-bitemp-meta-user", "x", "test-eval-bitemp", "replaceRoleTest", "rest-admin", "rest-writer", "rest-reader", "temporal-admin");
    createRESTUser("eval-readeruser", "x", "rest-reader");

    writerClient = getDatabaseClientOnDatabase(appServerHostname, restPort, dbName, "eval-bitemp-meta-user", "x", getConnType());
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    System.out.println("In tear down");

    // Delete database first. Otherwise axis and collection cannot be deleted
    cleanupRESTServer(dbName, fNames);
    deleteRESTUser("eval-bitemp-meta-user");
    deleteRESTUser("eval-readeruser");
    deleteUserRole("test-eval-bitemp");
    deleteUserRole("replaceRoleTest");

    // Temporal collection needs to be deleted before temporal axis associated
    // with it can be deleted
    ConnectedRESTQA.deleteElementRangeIndexTemporalCollection(dbName,
        temporalLsqtCollectionName);
    ConnectedRESTQA.deleteElementRangeIndexTemporalCollection(dbName,
        temporalCollectionName);
    ConnectedRESTQA.deleteElementRangeIndexTemporalCollection(dbName,
        bulktemporalCollectionName);
    /*ConnectedRESTQA.deleteElementRangeIndexTemporalAxis(dbName,
        axisValidName);
    ConnectedRESTQA.deleteElementRangeIndexTemporalAxis(dbName,
        axisSystemName);*/
    deleteDB(schemadbName);
    deleteForest(schemafNames[0]);
  }

  @AfterEach
  public void tearDown() throws Exception {
    clearDB();
  }

  public DocumentMetadataHandle setMetadata(boolean update) {
    // create and initialize a handle on the meta-data
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

    if (update) {
      metadataHandle.getCollections().addAll("updateCollection");
      metadataHandle.getProperties().put("published", true);

      metadataHandle.getPermissions().add("app-user", Capability.UPDATE,
          Capability.READ);

      metadataHandle.setQuality(99);
    } else {
      metadataHandle.getCollections().addAll("insertCollection");
      metadataHandle.getProperties().put("reviewed", true);

      metadataHandle.getPermissions().add("app-user", Capability.UPDATE,
          Capability.READ, Capability.EXECUTE);

      metadataHandle.setQuality(11);
    }

    metadataHandle.getProperties().put("myString", "foo");
    metadataHandle.getProperties().put("myInteger", 10);
    metadataHandle.getProperties().put("myDecimal", 34.56678);
    metadataHandle.getProperties().put("myCalendar",
        Calendar.getInstance().get(Calendar.YEAR));

    return metadataHandle;
  }

  private JacksonDatabindHandle<ObjectNode> getJSONDocumentHandle(
      String startValidTime, String endValidTime, String address, String uri)
      throws Exception {

    // Setup for JSON document
    /**
     *
     { "System": { systemStartERIName : "", systemEndERIName : "", }, "Valid":
     * { validStartERIName: "2001-01-01T00:00:00", validEndERIName:
     * "2011-12-31T23:59:59" }, "Address": "999 Skyway Park", "uri":
     * "javaSingleDoc1.json" }
     */

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();

    // Set system time values
    ObjectNode system = mapper.createObjectNode();

    system.put(systemStartERIName, "");
    system.put(systemEndERIName, "");
    rootNode.set(systemNodeName, system);

    // Set valid time values
    ObjectNode valid = mapper.createObjectNode();

    valid.put(validStartERIName, startValidTime);
    valid.put(validEndERIName, endValidTime);
    rootNode.set(validNodeName, valid);

    // Set Address
    rootNode.put(addressNodeName, address);

    // Set uri
    rootNode.put(uriNodeName, uri);

    System.out.println(rootNode.toString());

    JacksonDatabindHandle<ObjectNode> handle = new JacksonDatabindHandle<>(
        ObjectNode.class).withFormat(Format.JSON);
    handle.set(rootNode);

    return handle;
  }

  @Test
  // Test bitemporal patchbuilder add Metadata Value works with a JSON document
  public void testPatchWithAddMetaData() throws Exception {

    System.out.println("Inside testPatchWithAddMetaData");
    ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
        temporalLsqtCollectionName, true);

    Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");

    String docId = "javaSingleJSONDoc.json";
    JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00",
        "2011-12-31T23:59:59",
        "999 Skyway Park - JSON",
        docId
        );

    JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();

    // put meta-data
    docMgr.setMetadataCategories(Metadata.ALL);
    DocumentMetadataHandle mh = setMetadata(false);
    docMgr.write(docId, mh, handle, null, null, temporalLsqtCollectionName, insertTime);

    // Apply the patch
    XMLDocumentManager xmlDocMgr = writerClient.newXMLDocumentManager();

    DocumentMetadataPatchBuilder patchBldrXML = xmlDocMgr.newPatchBuilder(Format.XML);
    patchBldrXML.addMetadataValue("MLVersion", "MarkLogic 9.0");
    patchBldrXML.addCollection("/document/collection3");
    patchBldrXML.addPermission("replaceRoleTest", Capability.READ);
    patchBldrXML.addPropertyValue("Hello", "Hi");
    DocumentPatchHandle patchHandleXML = patchBldrXML.build();

    xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML);
    waitForPropertyPropagate();

    String contentMetadataXML = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println(" After Changing " + contentMetadataXML);

    // Verify that patch succeeded.
    assertTrue(contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion\">MarkLogic 9.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
    assertTrue(contentMetadataXML.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
    assertTrue(contentMetadataXML.contains("<rapi:collection>/document/collection3</rapi:collection>"));

    // Add the new meta data with JSON
    JSONDocumentManager jsonDocMgr = writerClient.newJSONDocumentManager();

    DocumentMetadataPatchBuilder patchBldrJson = jsonDocMgr.newPatchBuilder(Format.JSON);
    patchBldrJson.addMetadataValue("MLVersionJson", "MarkLogic 9.0 Json");
    patchBldrJson.addCollection("/document/collection3Json");

    DocumentPatchHandle patchHandleJSON = patchBldrJson.build();
    jsonDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleJSON);
    waitForPropertyPropagate();

    String contentMetadataJson = jsonDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println(" After Changing " + contentMetadataJson);

    // Verify the first patch's contents.

    assertTrue(contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion\">MarkLogic 9.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
    assertTrue(contentMetadataXML.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
    assertTrue(contentMetadataXML.contains("<rapi:collection>/document/collection3</rapi:collection>"));

    // Verify that second patch with JSON succeeded.
    assertTrue(contentMetadataJson.contains("<rapi:metadata-value key=\"MLVersionJson\">MarkLogic 9.0 Json</rapi:metadata-value>"));
    assertTrue(contentMetadataJson.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
    assertTrue(contentMetadataJson.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
    assertTrue(contentMetadataJson.contains("<rapi:collection>/document/collection3Json</rapi:collection>"));

    // Add multiple values at the same time.
    DocumentMetadataPatchBuilder patchBldrXMLMul = xmlDocMgr.newPatchBuilder(Format.XML);
    patchBldrXMLMul.addMetadataValue("MlClientProg1", "Java");
    patchBldrXMLMul.addMetadataValue("MlClientProg2", "Node/SJS");

    DocumentPatchHandle patchHandleXMLMul = patchBldrXMLMul.build();

    xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXMLMul);
    waitForPropertyPropagate();

    String contentMetadataXMLMul = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println(" After Changing " + contentMetadataXMLMul);

    // Verify that patch succeeded.
    assertTrue(contentMetadataXMLMul.contains("<rapi:metadata-value key=\"MLVersion\">MarkLogic 9.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXMLMul.contains("<rapi:metadata-value key=\"MlClientProg1\">Java</rapi:metadata-value>"));
    assertTrue(contentMetadataXMLMul.contains("<rapi:metadata-value key=\"MlClientProg2\">Node/SJS</rapi:metadata-value>"));
    assertTrue(contentMetadataXMLMul.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
    assertTrue(contentMetadataXMLMul.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
    assertTrue(contentMetadataXMLMul.contains("<rapi:collection>/document/collection3</rapi:collection>"));
  }

  @Test
  // Test bitemporal patchbuilder add Metadata Value works with a JSON document
  public void testPatchWithTransaction() throws Exception {

    System.out.println("Inside testPatchWithTransaction");
    ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
        temporalLsqtCollectionName, true);

    Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");

    String docId = "javaSingleJSONDoc.json";
    JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00",
        "2011-12-31T23:59:59",
        "999 Skyway Park - JSON",
        docId
        );

    JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();

    Transaction t = writerClient.openTransaction();

    // put meta-data
    docMgr.setMetadataCategories(Metadata.ALL);
    DocumentMetadataHandle mh = setMetadata(false);
    docMgr.write(docId, mh, handle, null, t, temporalLsqtCollectionName, insertTime);

    // Apply the patch
    XMLDocumentManager xmlDocMgr = writerClient.newXMLDocumentManager();
    t.commit();

    Transaction transPatch = writerClient.openTransaction();

    DocumentMetadataPatchBuilder patchBldrXML = xmlDocMgr.newPatchBuilder(Format.XML);
    patchBldrXML.addMetadataValue("MLVersion", "MarkLogic 9.0");
    patchBldrXML.addCollection("/document/collection3");
    patchBldrXML.addPermission("replaceRoleTest", Capability.READ);
    patchBldrXML.addPropertyValue("Hello", "Hi");
    DocumentPatchHandle patchHandleXML = patchBldrXML.build();

    xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML, transPatch);
    waitForPropertyPropagate();

    String contentMetadataXML = xmlDocMgr.readMetadata(docId, new StringHandle(), transPatch).get();
    System.out.println(" After Changing " + contentMetadataXML);
    // Verify that patch succeeded.
    assertTrue(contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion\">MarkLogic 9.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
    assertTrue(contentMetadataXML.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
    assertTrue(contentMetadataXML.contains("<rapi:collection>/document/collection3</rapi:collection>"));

    // Roll back the transaction
    transPatch.rollback();

    String contentMetadataRollXML = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println(" After Changing " + contentMetadataRollXML);
    assertFalse( contentMetadataRollXML.contains("<rapi:metadata-value key=\"MLVersion\">MarkLogic 9.0</rapi:metadata-value>"));
    assertFalse( contentMetadataRollXML.contains("<rapi:role-name>admin</rapi:role-name>"));
    assertFalse( contentMetadataRollXML.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
    assertFalse( contentMetadataRollXML.contains("<rapi:collection>/document/collection3</rapi:collection>"));
  }

  /*
   * Meta Data key and value have same string. Add same key value in another
   * patch with new key value.
   */
  @Test
  public void testPatchWithAddMetaDataNeg() throws Exception {

    System.out.println("Inside testPatchWithAddMetaDataNeg");
    ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
        temporalLsqtCollectionName, true);

    Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");

    String docId = "javaSingleJSONDoc.json";
    JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00",
        "2011-12-31T23:59:59",
        "999 Skyway Park - JSON",
        docId
        );

    JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();

    // put meta-data
    docMgr.setMetadataCategories(Metadata.ALL);
    DocumentMetadataHandle mh = setMetadata(false);
    docMgr.write(docId, mh, handle, null, null, temporalLsqtCollectionName, insertTime);

    // Apply the patch
    XMLDocumentManager xmlDocMgr = writerClient.newXMLDocumentManager();

    DocumentMetadataPatchBuilder patchBldrXML = xmlDocMgr.newPatchBuilder(Format.XML);
    patchBldrXML.addMetadataValue("MLVersion", "MLVersion");
    patchBldrXML.addCollection("/document/collection3");
    patchBldrXML.addPermission("replaceRoleTest", Capability.READ);
    patchBldrXML.addPropertyValue("Hello", "Hi");
    DocumentPatchHandle patchHandleXML = patchBldrXML.build();

    xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML);
    waitForPropertyPropagate();

    String contentMetadataXML = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println(" After Changing " + contentMetadataXML);

    // Verify that patch succeeded.
    assertTrue(contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion\">MLVersion</rapi:metadata-value>"));
    assertTrue(contentMetadataXML.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
    assertTrue(contentMetadataXML.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
    assertTrue(contentMetadataXML.contains("<rapi:collection>/document/collection3</rapi:collection>"));

    DocumentMetadataPatchBuilder patchBldrXML2 = xmlDocMgr.newPatchBuilder(Format.XML);

    // Do an add with same Key with another key value.
    patchBldrXML2.addMetadataValue("MLVersion", "MLVersionNew");
    DocumentPatchHandle patchHandleXML2 = patchBldrXML2.build();
    xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML2);
    waitForPropertyPropagate();
    String contentMetadataXML2 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println(" After Changing " + contentMetadataXML2);

    // Verify that patch succeeded. Seems to work. Replaces the key value.
    assertTrue(contentMetadataXML2.contains("<rapi:metadata-value key=\"MLVersion\">MLVersionNew</rapi:metadata-value>"));
    assertTrue(contentMetadataXML2.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
  }

  /*
   * Delete Meta Data key. Delete non-existing key. Delete multiple keys.
   */
  @Test
  // Test bitemporal patchbuilder add Metadata Value works with a JSON document
  public void testPatchWithDelete() throws Exception {

    System.out.println("Inside testPatchWithDelete");
    ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
        temporalLsqtCollectionName, true);

    Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");

    String docId = "javaSingleJSONDoc.json";
    JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00",
        "2011-12-31T23:59:59",
        "999 Skyway Park - JSON",
        docId
        );

    JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();

    // put meta-data
    docMgr.setMetadataCategories(Metadata.ALL);
    DocumentMetadataHandle mh = setMetadata(false);
    docMgr.write(docId, mh, handle, null, null, temporalLsqtCollectionName, insertTime);

    // Apply the patch
    XMLDocumentManager xmlDocMgr = writerClient.newXMLDocumentManager();

    DocumentMetadataPatchBuilder patchBldrXML = xmlDocMgr.newPatchBuilder(Format.XML);
    patchBldrXML.addMetadataValue("MLVersion", "9.0");
    patchBldrXML.addCollection("/document/collection3");
    patchBldrXML.addPermission("replaceRoleTest", Capability.READ);
    patchBldrXML.addPropertyValue("Hello", "Hi");
    DocumentPatchHandle patchHandleXML = patchBldrXML.build();

    xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML);
    waitForPropertyPropagate();

    String contentMetadataXML = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println("After Changing " + contentMetadataXML);

    // Verify that patch succeeded.
    assertTrue(contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion\">9.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
    assertTrue(contentMetadataXML.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
    assertTrue(contentMetadataXML.contains("<rapi:collection>/document/collection3</rapi:collection>"));

    DocumentMetadataPatchBuilder patchBldrXML2 = xmlDocMgr.newPatchBuilder(Format.XML);
    // Do an delete with key value.
    patchBldrXML2.deleteMetadataValue("MLVersion");
    DocumentPatchHandle patchHandleXML2 = patchBldrXML2.build();
    xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML2);
    waitForPropertyPropagate();
    String contentMetadataXML2 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println("After Changing 2 " + contentMetadataXML2);

    // Verify that patch succeeded. Seems to work. Replaces the key value.
    assertFalse( contentMetadataXML2.contains("<rapi:metadata-value key=\"MLVersion\">9.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML2.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));

    // Add back the same key
    DocumentMetadataPatchBuilder patchBldrXML3 = xmlDocMgr.newPatchBuilder(Format.XML);
    patchBldrXML3.addMetadataValue("MLVersion", "10.0");
    patchBldrXML3.addMetadataValue("MLVersion11", "11.0");
    patchBldrXML3.addMetadataValue("MLVersion12", "12.0");
    DocumentPatchHandle patchHandleXML3 = patchBldrXML3.build();
    xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML3);
    waitForPropertyPropagate();
    String contentMetadataXML3 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println("After Changing 3 " + contentMetadataXML3);

    // Verify that patch succeeded.
    assertTrue(contentMetadataXML3.contains("<rapi:metadata-value key=\"MLVersion\">10.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML3.contains("<rapi:metadata-value key=\"MLVersion11\">11.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML3.contains("<rapi:metadata-value key=\"MLVersion12\">12.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML3.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
    assertTrue(contentMetadataXML3.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
    assertTrue(contentMetadataXML3.contains("<rapi:collection>/document/collection3</rapi:collection>"));

    // Delete non existent key
    DocumentMetadataPatchBuilder patchBldrXML4 = xmlDocMgr.newPatchBuilder(Format.XML);
    patchBldrXML4.deleteMetadataValue("notfound");
    DocumentPatchHandle patchHandleXML4 = patchBldrXML4.build();
    xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML4);
    waitForPropertyPropagate();
    String contentMetadataXML4 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println("After Changing 4 " + contentMetadataXML4);

    // Verify that patch did not delete existing values..
    assertTrue(contentMetadataXML4.contains("<rapi:metadata-value key=\"MLVersion\">10.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML4.contains("<rapi:metadata-value key=\"MLVersion11\">11.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML4.contains("<rapi:metadata-value key=\"MLVersion12\">12.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML4.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
    assertTrue(contentMetadataXML4.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
    assertTrue(contentMetadataXML4.contains("<rapi:collection>/document/collection3</rapi:collection>"));

    // Delete multiple keys.
    DocumentMetadataPatchBuilder patchBldrXML5 = xmlDocMgr.newPatchBuilder(Format.XML);
    patchBldrXML5.deleteMetadataValue("MLVersion11");
    patchBldrXML5.deleteMetadataValue("MLVersion12");
    DocumentPatchHandle patchHandleXML5 = patchBldrXML5.build();
    xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML5);
    waitForPropertyPropagate();
    String contentMetadataXML5 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println("After Changing 5 " + contentMetadataXML5);
    assertTrue(contentMetadataXML5.contains("<rapi:metadata-value key=\"MLVersion\">10.0</rapi:metadata-value>"));
    assertFalse( contentMetadataXML5.contains("<rapi:metadata-value key=\"MLVersion11\">11.0</rapi:metadata-value>"));
    assertFalse( contentMetadataXML5.contains("<rapi:metadata-value key=\"MLVersion12\">12.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML5.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
    assertTrue(contentMetadataXML5.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
    assertTrue(contentMetadataXML5.contains("<rapi:collection>/document/collection3</rapi:collection>"));
  }

  /*
   * Replace Meta Data key. Replace non-existing key. Replace multiple keys.
   * Perform add new, replace in same patch
   */
  @Test
  // Test bitemporal patchbuilder add Metadata Value works with a JSON document
  public void testPatchWithReplace() throws Exception {

    System.out.println("Inside testPatchWithReplace");
    ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
        temporalLsqtCollectionName, true);

    Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");

    String docId = "javaSingleJSONDoc.json";
    JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00",
        "2011-12-31T23:59:59",
        "999 Skyway Park - JSON",
        docId
        );

    JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();

    // put meta-data
    docMgr.setMetadataCategories(Metadata.ALL);
    DocumentMetadataHandle mh = setMetadata(false);
    docMgr.write(docId, mh, handle, null, null, temporalLsqtCollectionName, insertTime);

    // Apply the patch
    XMLDocumentManager xmlDocMgr = writerClient.newXMLDocumentManager();

    DocumentMetadataPatchBuilder patchBldrXML = xmlDocMgr.newPatchBuilder(Format.XML);
    patchBldrXML.addMetadataValue("MLVersion", "9.0");
    patchBldrXML.addMetadataValue("MLVersion10", "9.0");
    patchBldrXML.addMetadataValue("MLVersion11", "9.0");
    patchBldrXML.addMetadataValue("MLVersion12", "12.0");
    patchBldrXML.addCollection("/document/collection3");
    patchBldrXML.addPermission("replaceRoleTest", Capability.READ);
    patchBldrXML.addPropertyValue("Hello", "Hi");
    DocumentPatchHandle patchHandleXML = patchBldrXML.build();

    xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML);
    waitForPropertyPropagate();

    String contentMetadataXML = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println(" After Changing " + contentMetadataXML);

    // Verify that patch succeeded.
    assertTrue(contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion\">9.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion10\">9.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion11\">9.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion12\">12.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
    assertTrue(contentMetadataXML.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
    assertTrue(contentMetadataXML.contains("<rapi:collection>/document/collection3</rapi:collection>"));

    DocumentMetadataPatchBuilder patchBldrXML2 = xmlDocMgr.newPatchBuilder(Format.XML);
    // Do a multiple replace with key value.
    patchBldrXML2.replaceMetadataValue("MLVersion10", "10.0");
    patchBldrXML2.replaceMetadataValue("MLVersion11", "11.0");
    DocumentPatchHandle patchHandleXML2 = patchBldrXML2.build();
    xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML2);
    waitForPropertyPropagate();
    String contentMetadataXML2 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println(" After Changing 2 " + contentMetadataXML2);

    // Verify that patch succeeded.
    assertTrue(contentMetadataXML2.contains("<rapi:metadata-value key=\"MLVersion\">9.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML2.contains("<rapi:metadata-value key=\"MLVersion10\">10.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML2.contains("<rapi:metadata-value key=\"MLVersion11\">11.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML2.contains("<rapi:metadata-value key=\"MLVersion12\">12.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML2.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
    assertTrue(contentMetadataXML2.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
    assertTrue(contentMetadataXML2.contains("<rapi:collection>/document/collection3</rapi:collection>"));

    // Replace non existent key
    DocumentMetadataPatchBuilder patchBldrXML3 = xmlDocMgr.newPatchBuilder(Format.XML);
    patchBldrXML3.replaceMetadataValue("notfound", "unknown");
    DocumentPatchHandle patchHandleXML3 = patchBldrXML3.build();
    xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML3);
    waitForPropertyPropagate();
    String contentMetadataXML3 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println(" After Changing 3 " + contentMetadataXML3);

    // Verify that none of the other values are incorrect or affected.
    assertTrue(contentMetadataXML3.contains("<rapi:metadata-value key=\"MLVersion\">9.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML3.contains("<rapi:metadata-value key=\"MLVersion10\">10.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML3.contains("<rapi:metadata-value key=\"MLVersion11\">11.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML3.contains("<rapi:metadata-value key=\"MLVersion12\">12.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML3.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
    assertTrue(contentMetadataXML3.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
    assertTrue(contentMetadataXML3.contains("<rapi:collection>/document/collection3</rapi:collection>"));

    // Perform add new, replace in same patch
    DocumentMetadataPatchBuilder patchBldrXML4 = xmlDocMgr.newPatchBuilder(Format.XML);
    patchBldrXML4.addMetadataValue("NewAndReplace", "Added");
    patchBldrXML4.replaceMetadataValue("NewAndReplace", "Added and Replaced");
    DocumentPatchHandle patchHandleXML4 = patchBldrXML4.build();
    xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML4);
    waitForPropertyPropagate();

    String contentMetadataXML4 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
    System.out.println(" After Changing 4 " + contentMetadataXML4);

    // Verify that none of the other values are incorrect or affected.
    assertTrue(contentMetadataXML4.contains("<rapi:metadata-value key=\"MLVersion\">9.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML4.contains("<rapi:metadata-value key=\"MLVersion10\">10.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML4.contains("<rapi:metadata-value key=\"MLVersion11\">11.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML4.contains("<rapi:metadata-value key=\"MLVersion12\">12.0</rapi:metadata-value>"));
    assertTrue(contentMetadataXML4.contains("<rapi:metadata-value key=\"NewAndReplace\">Added</rapi:metadata-value>"));
    assertTrue(contentMetadataXML4.contains("<rapi:role-name>replaceRoleTest</rapi:role-name>"));
    assertTrue(contentMetadataXML4.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
    assertTrue(contentMetadataXML4.contains("<rapi:collection>/document/collection3</rapi:collection>"));
  }

  /*
   * insertFragement. Insert Fragments in JSON and XML BiTemporal Docs,
   */
  @Test
  // Test bitemporal patchbuilder add Metadata Value works with a JSON document
  public void testInsertFragement() throws Exception {

    // TODO Once inertFragment is implemented Git Issue 540 - write tests

    /*
     * System.out.println("Inside testInsertFragement");
     * ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
     * temporalLsqtCollectionName, true);
     *
     * Calendar insertTime =
     * DatatypeConverter.parseDateTime("2005-01-01T00:00:01");
     *
     * String docId = "javaSingleJSONDoc.json";
     * JacksonDatabindHandle<ObjectNode> handle =
     * getJSONDocumentHandle("2001-01-01T00:00:00", "2011-12-31T23:59:59",
     * "999 Skyway Park - JSON", docId );
     *
     * JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();
     *
     * // put meta-data docMgr.setMetadataCategories(Metadata.ALL);
     * DocumentMetadataHandle mh = setMetadata(false); docMgr.write(docId, mh,
     * handle, null, null, temporalLsqtCollectionName, insertTime);
     *
     * // Apply the patch XMLDocumentManager xmlDocMgr =
     * writerClient.newXMLDocumentManager();
     *
     * DocumentMetadataPatchBuilder patchBldrXML =
     * xmlDocMgr.newPatchBuilder(Format.XML);
     */

  }

  @Test
  // Test bitemporal protections
  public void testProtectDelete() throws Exception {

    System.out.println("Inside testProtectDelete");
    ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
        temporalLsqtCollectionName, true);

    Calendar insertTime = DatatypeConverter.parseDateTime("2001-01-01T00:00:11");
    Calendar deleteTime = DatatypeConverter.parseDateTime("2011-01-01T00:00:31");
    Calendar updateTime1 = DatatypeConverter.parseDateTime("2007-01-01T00:00:21");
    Calendar updateTime2 = DatatypeConverter.parseDateTime("2009-01-01T00:00:21");

    String docId = "javaSingleJSONDoc.json";
    JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00Z",
        "2011-12-30T23:59:59Z",
        "999 Skyway Park - JSON",
        docId
        );

    JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();
    docMgr.write(docId, null, handle, null, null, temporalLsqtCollectionName, insertTime);

    // Protect document for 40 sec from delete and update. Use Duration.
    docMgr.protect(docId, temporalLsqtCollectionName, ProtectionLevel.NODELETE, DatatypeFactory.newInstance().newDuration("PT40S"));

    StringBuilder str = new StringBuilder();
    try {
      docMgr.delete(docId, null, temporalLsqtCollectionName, deleteTime);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception when delete within 30 sec is " + str.toString());
    }
    assertTrue(str.toString().contains("The document javaSingleJSONDoc.json is protected noDelete"));
    str = null;
    // Added the word "Updated" to doc from 2007-01-01T00:00:00 to
    // 2008-12-30T23:59:59
    JacksonDatabindHandle<ObjectNode> handleUpd = getJSONDocumentHandle(
        "2007-01-01T00:00:00Z", "2008-12-30T23:59:59Z",
        "1999 Skyway Park - Updated - JSON", docId);

    // Can the document be updated with 40 sec with ProtectionLevel.NODELETE and
    // 40 secs?
    docMgr.write(docId, null, handleUpd, null, null, temporalLsqtCollectionName, updateTime1);

    // Remove the word "Updated" from 2008-12-31T00:00:00 to 2011-12-30T23:59:59
    JacksonDatabindHandle<ObjectNode> handleUpd2 = getJSONDocumentHandle(
        "2008-12-31T00:00:00Z", "2011-12-30T23:59:59Z",
        "1999 Skyway Park - JSON", docId);
    docMgr.write(docId, null, handleUpd2, null, null, temporalLsqtCollectionName, updateTime2);

    // Search for the "Updated" term in doc and try to delete that document
    // within 40 sec (should throw exception) and again delete that version
    // after 40 secs.
    QueryManager queryMgr = writerClient.newQueryManager();
    StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();

    StructuredQueryDefinition termQuery = sqb.term("Updated");

    StructuredQueryBuilder.Axis validAxis = sqb.axis(axisValidName);
    Calendar start1 = DatatypeConverter.parseDateTime("2007-02-01T00:00:00Z");
    Calendar end1 = DatatypeConverter.parseDateTime("2007-12-31T23:59:59Z");
    StructuredQueryBuilder.Period period1 = sqb.period(start1, end1);

    StructuredQueryDefinition periodQuery = sqb.and(termQuery,
        sqb.temporalPeriodRange(validAxis, TemporalOperator.ALN_CONTAINS, period1));
    long startOffset = 1;
    DocumentPage termQueryResults = docMgr.search(periodQuery, startOffset);

    String toDeleteURI = null;
    while (termQueryResults.hasNext()) {
      DocumentRecord record = termQueryResults.next();
      System.out.println("URI = " + record.getUri());
      JacksonDatabindHandle<ObjectNode> recordHandle = new JacksonDatabindHandle<>(
          ObjectNode.class);
      record.getContent(recordHandle);
      System.out.println("Content = " + recordHandle.toString());
      if (recordHandle.toString().contains("Updated")) {
        toDeleteURI = record.getUri();
      }
    }
    str = new StringBuilder();
    // Delete this (searched) doc within 40 sec.
    try {
      docMgr.delete(toDeleteURI, null, temporalLsqtCollectionName,
          deleteTime);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception when delete within 40 sec is " + str.toString());
    }
    String docDelMessage = "The document " + toDeleteURI + " is protected noDelete";
    assertTrue(str.toString().contains(docDelMessage));
    // Sleep for 40 secs and try to delete the same docId.
    Thread.sleep(40000);
    docMgr.delete(docId, null, temporalLsqtCollectionName, deleteTime);
    Thread.sleep(5000);
    // Make sure that bi temporal doc is not deleted.
    JSONDocumentManager jsonDocMgr = writerClient.newJSONDocumentManager();
    DocumentPage readResults = jsonDocMgr.read(docId);
    System.out.println("Number of results = " + readResults.size());
    assertEquals(1, readResults.size());
  }

  @Test
  /*
   * Test bitemporal protections - NOUPDATE Without transaction.
   */
  public void testProtectUpdateNoTransaction() throws Exception {

    System.out.println("Inside testProtectUpdateNoTransaction");
    ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
        temporalLsqtCollectionName, true);

    Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");
    Calendar updateTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:11");

    String docId = "javaSingleJSONDoc.json";
    JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00",
        "2011-12-31T23:59:59",
        "999 Skyway Park - JSON",
        docId
        );

    JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();
    docMgr.write("javaSingleJSONDocV1.json", docId, null, handle, null, null, temporalLsqtCollectionName, insertTime);

    // Protect document for 30 sec from delete and update. Use Duration.
    docMgr.protect(docId, temporalLsqtCollectionName, ProtectionLevel.NOUPDATE, DatatypeFactory.newInstance().newDuration("PT30S"));
    JacksonDatabindHandle<ObjectNode> handleUpd = getJSONDocumentHandle(
        "2003-01-01T00:00:00", "2008-12-31T23:59:59",
        "1999 Skyway Park - Updated - JSON", docId);
    StringBuilder str = new StringBuilder();
    try {

      docMgr.write(docId, null, handleUpd, null, null, temporalLsqtCollectionName, updateTime);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception when update within 30 sec is " + str.toString());
    }
    assertTrue(str.toString().contains("The document javaSingleJSONDoc.json is protected noUpdate"));

    // Sleep for 40 secs and try to update the same docId.
    Thread.sleep(40000);
    docMgr.write(docId, null, handleUpd, null, null, temporalLsqtCollectionName, updateTime);
    Thread.sleep(5000);

    JSONDocumentManager jsonDocMgr = writerClient.newJSONDocumentManager();
    DocumentPage readResults = jsonDocMgr.read(docId);
    System.out.println("Number of results = " + readResults.size());
    assertEquals(1, readResults.size());

    QueryManager queryMgr = writerClient.newQueryManager();

    StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition termQuery = sqb.collection(temporalLsqtCollectionName);

    long start = 1;
    DocumentPage termQueryResults = docMgr.search(termQuery, start);
    System.out
        .println("Number of results = " + termQueryResults.getTotalSize());
    assertEquals(4, termQueryResults.getTotalSize());
  }

  @Test
  /*
   * Test bitemporal protections - NOUPDATE With transaction.
   */
  public void testProtectUpdateInTransaction() throws Exception {

    System.out.println("Inside testProtectUpdateInTransaction");
    ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
        temporalLsqtCollectionName, true);

    Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");
    Calendar updateTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:11");

    String docId = "javaSingleJSONDoc.json";
    JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00",
        "2011-12-31T23:59:59",
        "999 Skyway Park - JSON",
        docId
        );

    JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();
    Transaction t1 = writerClient.openTransaction();
    Transaction t2 = null;
    docMgr.write("javaSingleJSONDocV1.json", docId, null, handle, null, t1, temporalLsqtCollectionName, insertTime);

    // Protect document for 30 sec from delete and update. Use Duration.
    docMgr.protect(docId, temporalLsqtCollectionName, ProtectionLevel.NOUPDATE, DatatypeFactory.newInstance().newDuration("PT30S"), t1);
    JacksonDatabindHandle<ObjectNode> handleUpd = getJSONDocumentHandle(
        "2003-01-01T00:00:00", "2008-12-31T23:59:59",
        "1999 Skyway Park - Updated - JSON", docId);
    StringBuilder str = new StringBuilder();
    try {

      docMgr.write(docId, null, handleUpd, null, t1, temporalLsqtCollectionName, updateTime);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception when update within 30 sec is " + str.toString());
    }
    assertTrue(str.toString().contains("The document javaSingleJSONDoc.json is protected noUpdate"));
    try {
      // Sleep for 40 secs and try to update the same docId.
      Thread.sleep(40000);
      docMgr.write(docId, null, handleUpd, null, t1, temporalLsqtCollectionName, updateTime);
      Thread.sleep(5000);

      JSONDocumentManager jsonDocMgr = writerClient.newJSONDocumentManager();
      DocumentPage readResults = jsonDocMgr.read(t1, docId);
      System.out.println("Number of results = " + readResults.size());
      assertEquals(1, readResults.size());

      QueryManager queryMgr = writerClient.newQueryManager();

      StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
      StructuredQueryDefinition termQuery = sqb.collection(temporalLsqtCollectionName);
      t1.commit();

      long start = 1;
      t2 = writerClient.openTransaction();
      DocumentPage termQueryResults = docMgr.search(termQuery, start, t2);
      System.out
          .println("Number of results = " + termQueryResults.getTotalSize());
      assertEquals(4, termQueryResults.getTotalSize());
    } catch (Exception e) {
      System.out.println("Exception when update within 30 sec is " + e.getMessage());
    } finally {
      if (t2 != null)
        t2.rollback();
    }
  }

  @Disabled
  /*
   * Test bitemporal protections - NOUPDATE With different transactions. Write
   * doc in T1. Protect in T2. Update doc in T1 within 30 sec duration. TODO
   * Wait for Git #542
   */
  public void testProtectUpdateInDiffTransactions() throws Exception {

    System.out.println("Inside testProtectUpdateInDiffTransactions");
    ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
        temporalLsqtCollectionName, true);

    Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");
    Calendar updateTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:11");
    Transaction t1 = writerClient.openTransaction();
    Transaction t2 = writerClient.openTransaction();

    String docId = "javaSingleJSONDoc.json";
    JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00",
        "2011-12-31T23:59:59",
        "999 Skyway Park - JSON",
        docId
        );

    JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();

    docMgr.write("javaSingleJSONDocV1.json", docId, null, handle, null, t1, temporalLsqtCollectionName, insertTime);

    // Protect document for 30 sec from delete and update. Use Duration. Use T2
    docMgr.protect(docId, temporalLsqtCollectionName, ProtectionLevel.NOUPDATE, DatatypeFactory.newInstance().newDuration("PT30S"), t2);
    JacksonDatabindHandle<ObjectNode> handleUpd = getJSONDocumentHandle(
        "2003-01-01T00:00:00", "2008-12-31T23:59:59",
        "1999 Skyway Park - Updated - JSON", docId);

    StringBuilder str = new StringBuilder();
    try {
      // Use t1 to write
      docMgr.write(docId, null, handleUpd, null, t1, temporalLsqtCollectionName, updateTime);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception when update within 30 sec is " + str.toString());
    }
    // TODO Yet to know what exception message will be. #542
    // assertTrue("Doc should not be updated",
    // str.toString().contains("The document javaSingleJSONDoc.json is protected noUpdate"));

    // Sleep for 40 secs and try to update the same docId.
    Thread.sleep(40000);
    docMgr.write(docId, null, handleUpd, null, t1, temporalLsqtCollectionName, updateTime);
    Thread.sleep(5000);

    JSONDocumentManager jsonDocMgr = writerClient.newJSONDocumentManager();
    DocumentPage readResults = jsonDocMgr.read(t1, docId);
    System.out.println("Number of results = " + readResults.size());
    assertEquals(1, readResults.size());

    QueryManager queryMgr = writerClient.newQueryManager();

    StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition termQuery = sqb.collection(temporalLsqtCollectionName);

    long start = 1;
    DocumentPage termQueryResults = docMgr.search(termQuery, start, t1);
    System.out
        .println("Number of results = " + termQueryResults.getTotalSize());
    assertEquals(4, termQueryResults.getTotalSize());
    t1.rollback();
    t2.rollback();
  }

  @Test
  /*
   * Test bitemporal protections - NOUPDATE With different transactions. Write
   * doc in T1 with transaction timeout 2 minutes Protect in T2 with transaction
   * timeout of 30 secs and Protect duration of 30 sec. Update doc in T1 within
   * 30 sec duration. Timeout t2 transaction. Should be available update in t1
   * transaction now. Commit and read.
   */
  public void testProtectDiffTransactionsTimeouts() throws Exception {
    Transaction t1 = null;
    Transaction t2 = null;
    JSONDocumentManager docMgr = null;
    String docId = "javaSingleJSONDoc.json";
    Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");
    Calendar updateTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:11");
    JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00",
        "2011-12-31T23:59:59",
        "999 Skyway Park - JSON",
        docId
        );
    JacksonDatabindHandle<ObjectNode> handleUpd = getJSONDocumentHandle(
        "2003-01-01T00:00:00", "2008-12-31T23:59:59",
        "1999 Skyway Park - Updated - JSON", docId);
    try {

      System.out.println("Inside testProtectDiffTransactionsTimeouts");
      ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
          temporalLsqtCollectionName, true);

      docMgr = writerClient.newJSONDocumentManager();
      t1 = writerClient.openTransaction("T1", 120);
      docMgr.write("javaSingleJSONDocV1.json", docId, null, handle, null, t1, temporalLsqtCollectionName, insertTime);
      t2 = writerClient.openTransaction("T2", 30);
      // Protect document for 30 sec from delete and update. Use Duration.
      docMgr.protect(docId, temporalLsqtCollectionName, ProtectionLevel.NOUPDATE, DatatypeFactory.newInstance().newDuration("PT30S"), t2);

      StringBuilder str = new StringBuilder();
      try {
        // Use t1 to write
        docMgr.write(docId, null, handleUpd, null, t1, temporalLsqtCollectionName, updateTime);
      } catch (Exception ex) {
        str.append(ex.getMessage());
        System.out.println("Exception when update within 30 sec is " + str.toString());
      }
      // Time out t2 transaction.
      Thread.sleep(40000);
    }

    catch (Exception ex) {
      System.out.println("Exceptions:" + ex.getMessage());
    } finally {
      if (t1 != null) {
        // Try to update when T2 has timed out
        docMgr.write(docId, null, handleUpd, null, t1, temporalLsqtCollectionName, updateTime);
        Thread.sleep(5000);

        JSONDocumentManager jsonDocMgr = writerClient.newJSONDocumentManager();
        DocumentPage readResults = jsonDocMgr.read(t1, docId);
        System.out.println("Number of results = " + readResults.size());
        assertEquals(1, readResults.size());
        t1.commit();

        QueryManager queryMgr = writerClient.newQueryManager();

        StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
        StructuredQueryDefinition termQuery = sqb.collection(temporalLsqtCollectionName);

        long start = 1;
        DocumentPage termQueryResults = docMgr.search(termQuery, start);
        System.out
            .println("Number of results = " + termQueryResults.getTotalSize());
        assertEquals(4, termQueryResults.getTotalSize());
      }
    }
  }

  @Test
  /*
   * Test bitemporal protections - NOWIPE With transaction.
   */
  public void testProtectWipeWithoutPermission() throws Exception {

    System.out.println("Inside testProtectWipeWithoutPermission");
    DatabaseClient adminClient = getDatabaseClientOnDatabase(appServerHostname, restPort, dbName, "eval-readeruser", "x", getConnType());
    ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
        temporalLsqtCollectionName, true);

    Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");

    String docId = "javaSingleJSONDoc.json";
    JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00",
        "2011-12-31T23:59:59",
        "999 Skyway Park - JSON",
        docId
        );

    JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();
    JSONDocumentManager docMgrProtect = adminClient.newJSONDocumentManager();

    docMgr.write(docId, null, handle, null, null, temporalLsqtCollectionName, insertTime);
    Thread.sleep(10000);
    StringBuilder str = new StringBuilder();
    try {
      // Protect document for 30 sec. Use Duration.
      docMgrProtect.protect(docId, temporalLsqtCollectionName, ProtectionLevel.NOWIPE, DatatypeFactory.newInstance().newDuration("PT30S"));
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception not thrown when user does not have permissions" + str.toString());
    }
    assertTrue(str.toString().contains("User is not allowed to protect resource"));
  }

  @Test
  /*
   * Test bitemporal protections - NOWIPE With transaction.
   */
  public void testProtectWipe() throws Exception {

    System.out.println("Inside testProtectWipe");
    ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
        temporalLsqtCollectionName, true);

    Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");
    Calendar updateTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:11");

    String docId = "javaSingleJSONDoc.json";
    JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00",
        "2011-12-31T23:59:59",
        "999 Skyway Park - JSON",
        docId
        );

    JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();

    docMgr.write(docId, null, handle, null, null, temporalLsqtCollectionName, insertTime);
    Thread.sleep(5000);

    // Protect document for 60 sec. Use Duration.
    docMgr.protect(docId, temporalLsqtCollectionName, ProtectionLevel.NOWIPE, DatatypeFactory.newInstance().newDuration("PT60S"));
    JacksonDatabindHandle<ObjectNode> handleUpd = getJSONDocumentHandle(
        "2003-01-01T00:00:00", "2012-12-31T23:59:59",
        "1999 Skyway Park - Updated - JSON", docId);
    docMgr.write(docId, null, handleUpd, null, null, temporalLsqtCollectionName, updateTime);
    StringBuilder str = new StringBuilder();
    try {
      docMgr.wipe(docId, temporalLsqtCollectionName);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception when delete within 60 sec is " + str.toString());
    }

    assertTrue(str.toString().contains("TEMPORAL-PROTECTED"),
		"Did not receive Expected Exception, Expecting TEMPORAL-PROTECTED, received " + str);
    JSONDocumentManager jsonDocMgr = writerClient.newJSONDocumentManager();
    DocumentPage readResults = jsonDocMgr.read(docId);
    String content = jsonDocMgr.read(docId, new StringHandle()).get();
    assertTrue(content.contains("1999 Skyway Park - Updated - JSON"));

    // Sleep for 60 secs and try to delete the same docId.
    Thread.sleep(60000);
    Transaction t1 = writerClient.openTransaction();
    // TODO replace get with search and verify the system end time for the
    // document \
    // temporal document will not be deleted from DB and using get will only
    // return the latest docuemnt.
    docMgr.wipe(docId, t1, temporalLsqtCollectionName);
    Thread.sleep(5000);
    t1.commit();

    readResults = jsonDocMgr.read(docId);
    System.out.println("Number of results = " + readResults.size());
    assertEquals(0, readResults.size());
  }
}
