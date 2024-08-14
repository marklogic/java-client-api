/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.DigestAuthContext;
import com.marklogic.client.Transaction;
import com.marklogic.client.bitemporal.TemporalDocumentManager.ProtectionLevel;
import com.marklogic.client.document.*;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.io.*;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.*;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFMimeTypes;
import com.marklogic.client.type.CtsReferenceExpr;
import com.marklogic.client.type.XsStringSeqVal;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import jakarta.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.jupiter.api.Assertions.*;

@Disabled("Ignored because it was previously ignored in build.gradle though without explanation")
public class TestSandBox extends BasicJavaClientREST {

  private static String dbName = "TestSandBox";
  private static String[] fNames = { "TestSandBox-1" };
  private static String schemadbName = "TestSandBoxSchemaDB";
  private static String[] schemafNames = { "TestSandBoxSchemaDB-1" };

  private DatabaseClient writerClient = null;
  private static GraphManager gmWriter;

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
  private static String datasource = null;

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    appServerHostname = getRestAppServerHostName();
    restPort = getRestServerPort();
    datasource = getDataConfigDirPath() + "/data/optics/";

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
    addRangeElementAttributeIndex(dbName, "decimal", "http://cloudbank.com",
        "price", "", "amt", "http://marklogic.com/collation/");

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

    String[][] rangeElements = {
        // { scalar-type, namespace-uri, localname, collation,
        // range-value-positions, invalid-values }
        // If there is a need to add additional fields, then add them to the end
        // of each array
        // and pass empty strings ("") into an array where the additional field
        // does not have a value.
        // For example : as in namespace, collections below.
        // Add new RangeElementIndex as an array below.
        { "string", "", "city", "http://marklogic.com/collation/", "false",
            "reject" },
        { "int", "", "popularity", "", "false", "reject" },
        { "int", "", "id", "", "false", "reject" },
        { "double", "", "distance", "", "false", "reject" },
        { "date", "", "date", "", "false", "reject" },
        { "string", "", "cityName", "http://marklogic.com/collation/", "false",
            "reject" },
        { "string", "", "cityTeam", "http://marklogic.com/collation/", "false",
            "reject" }, { "long", "", "cityPopulation", "", "false", "reject" } };

    // Insert the range indices
    addRangeElementIndex(dbName, rangeElements);

    // Insert word lexicon.
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode mainNode = mapper.createObjectNode();
    ArrayNode childArray = mapper.createArrayNode();
    ObjectNode childNodeObject = mapper.createObjectNode();

    childNodeObject.put("namespace-uri", "");
    childNodeObject.put("localname", "city");
    childNodeObject.put("collation", "http://marklogic.com/collation/");
    childArray.add(childNodeObject);
    mainNode.withArray("element-word-lexicon").add(childArray);

    setDatabaseProperties(dbName, "element-word-lexicon", mainNode);
    setupAppServicesGeoConstraint(dbName);

    // Add geo element index.
    addGeospatialElementIndexes(dbName, "latLonPoint", "", "wgs84", "point",
        false, "reject");
    // Enable triple index.
    enableTripleIndex(dbName);
    enableTrailingWildcardSearches(dbName);
    // Enable collection lexicon.
    enableCollectionLexicon(dbName);
    // Enable uri lexicon.
    setDatabaseProperties(dbName, "uri-lexicon", true);
    // Create schema database
    createDB(schemadbName);
    createForest(schemafNames[0], schemadbName);
    // Set the schemadbName database as the Schema database.
    setDatabaseProperties(dbName, "schema-database", schemadbName);

    DatabaseClient schemaDBclient = DatabaseClientFactory.newClient(
        appServerHostname, restPort, schemadbName, new DigestAuthContext(
            "admin", "admin"));

    // You can enable the triple positions index for faster near searches using
    // cts:triple-range-query.
    DatabaseClient client = DatabaseClientFactory.newClient(appServerHostname,
        restPort, new DigestAuthContext("admin", "admin"));

    // Install the TDE templates
    // loadFileToDB(client, filename, docURI, collection, document format)
    loadFileToDB(schemaDBclient, "masterDetail.tdex",
        "/optic/view/test/masterDetail.tdex", "XML",
        new String[] { "http://marklogic.com/xdmp/tde" });
    loadFileToDB(schemaDBclient, "masterDetail2.tdej",
        "/optic/view/test/masterDetail2.tdej", "JSON",
        new String[] { "http://marklogic.com/xdmp/tde" });
    loadFileToDB(schemaDBclient, "masterDetail3.tdej",
        "/optic/view/test/masterDetail3.tdej", "JSON",
        new String[] { "http://marklogic.com/xdmp/tde" });
    loadFileToDB(schemaDBclient, "masterDetail4.tdej",
        "/optic/view/test/masterDetail4.tdej", "JSON",
        new String[] { "http://marklogic.com/xdmp/tde" });

    // Load XML data files.
    loadFileToDB(client, "masterDetail.xml",
        "/optic/view/test/masterDetail.xml", "XML",
        new String[] { "/optic/view/test" });
    loadFileToDB(client, "playerTripleSet.xml",
        "/optic/triple/test/playerTripleSet.xml", "XML",
        new String[] { "/optic/player/triple/test" });
    loadFileToDB(client, "teamTripleSet.xml",
        "/optic/triple/test/teamTripleSet.xml", "XML",
        new String[] { "/optic/team/triple/test" });
    loadFileToDB(client, "otherPlayerTripleSet.xml",
        "/optic/triple/test/otherPlayerTripleSet.xml", "XML",
        new String[] { "/optic/other/player/triple/test" });
    loadFileToDB(client, "doc4.xml", "/optic/lexicon/test/doc4.xml", "XML",
        new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "doc5.xml", "/optic/lexicon/test/doc5.xml", "XML",
        new String[] { "/optic/lexicon/test" });

    // Load JSON data files.
    loadFileToDB(client, "masterDetail2.json",
        "/optic/view/test/masterDetail2.json", "JSON",
        new String[] { "/optic/view/test" });
    loadFileToDB(client, "masterDetail3.json",
        "/optic/view/test/masterDetail3.json", "JSON",
        new String[] { "/optic/view/test" });
    loadFileToDB(client, "masterDetail4.json",
        "/optic/view/test/masterDetail4.json", "JSON",
        new String[] { "/optic/view/test" });
    loadFileToDB(client, "masterDetail5.json",
        "/optic/view/test/masterDetail5.json", "JSON",
        new String[] { "/optic/view/test" });

    loadFileToDB(client, "doc1.json", "/optic/lexicon/test/doc1.json", "JSON",
        new String[] { "/other/coll1", "/other/coll2" });
    loadFileToDB(client, "doc2.json", "/optic/lexicon/test/doc2.json", "JSON",
        new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "doc3.json", "/optic/lexicon/test/doc3.json", "JSON",
        new String[] { "/optic/lexicon/test" });

    loadFileToDB(client, "city1.json", "/optic/lexicon/test/city1.json",
        "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "city2.json", "/optic/lexicon/test/city2.json",
        "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "city3.json", "/optic/lexicon/test/city3.json",
        "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "city4.json", "/optic/lexicon/test/city4.json",
        "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "city5.json", "/optic/lexicon/test/city5.json",
        "JSON", new String[] { "/optic/lexicon/test" });
    Thread.sleep(10000);
    schemaDBclient.release();
    client.release();
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    System.out.println("In tear down");

    // Delete database first. Otherwise axis and collection cannot be deleted
    cleanupRESTServer(dbName, fNames);
    deleteRESTUser("eval-user");
    deleteRESTUser("eval-readeruser");
    deleteUserRole("test-eval");

    // Temporal collection needs to be deleted before temporal axis associated
    // with it can be deleted
    ConnectedRESTQA.deleteElementRangeIndexTemporalCollection(dbName,
        temporalLsqtCollectionName);
    ConnectedRESTQA.deleteElementRangeIndexTemporalCollection(dbName,
        temporalCollectionName);
    ConnectedRESTQA.deleteElementRangeIndexTemporalCollection(dbName,
        bulktemporalCollectionName);
    ConnectedRESTQA.deleteElementRangeIndexTemporalAxis(dbName, axisValidName);
    ConnectedRESTQA.deleteElementRangeIndexTemporalAxis(dbName, axisSystemName);
    deleteDB(schemadbName);
    deleteForest(schemafNames[0]);
  }

  @BeforeEach
  public void setUp() throws Exception {
    createUserRolesWithPrevilages("test-eval", "xdbc:eval", "xdbc:eval-in",
        "xdmp:eval-in", "any-uri", "xdbc:invoke",
        "temporal:statement-set-system-time", "temporal-document-protect",
        "temporal-document-wipe");

    createRESTUser("eval-user", "x", "test-eval", "rest-admin", "rest-writer",
        "rest-reader", "temporal-admin");
    createRESTUser("eval-readeruser", "x", "rest-reader");
    writerClient = getDatabaseClientOnDatabase(appServerHostname, restPort,
        dbName, "eval-user", "x", getConnType());
  }

  @AfterEach
  public void tearDown() throws Exception {
    //clearDB();
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
  /*
   * Test bitemporal protections - NOUPDATE With transaction.
   */
  public void testProtectUpdateInTransaction() throws Exception {

    System.out.println("Inside testProtectUpdateInTransaction");
    ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
        temporalLsqtCollectionName, true);

    Calendar insertTime = DatatypeConverter
        .parseDateTime("2005-01-01T00:00:01");
    Calendar updateTime = DatatypeConverter
        .parseDateTime("2005-01-01T00:00:11");

    String docId = "javaSingleJSONDoc.json";
    JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle(
        "2001-01-01T00:00:00", "2011-12-31T23:59:59", "999 Skyway Park - JSON",
        docId);

    JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();
    Transaction t1 = writerClient.openTransaction();
    Transaction t2 = null;
    docMgr.write("javaSingleJSONDocV1.json", docId, null, handle, null, t1,
        temporalLsqtCollectionName, insertTime);

    // Protect document for 30 sec from delete and update. Use Duration.
    docMgr.protect(docId, temporalLsqtCollectionName, ProtectionLevel.NOUPDATE,
        DatatypeFactory.newInstance().newDuration("PT30S"), t1);
    JacksonDatabindHandle<ObjectNode> handleUpd = getJSONDocumentHandle(
        "2003-01-01T00:00:00", "2008-12-31T23:59:59",
        "1999 Skyway Park - Updated - JSON", docId);
    StringBuilder str = new StringBuilder();
    try {
      docMgr.write(docId, null, handleUpd, null, t1,
          temporalLsqtCollectionName, updateTime);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception when update within 30 sec is "
          + str.toString());
    }
    assertTrue(
        str.toString().contains(
            "The document javaSingleJSONDoc.json is protected noUpdate"));
    try {
      // Sleep for 40 secs and try to update the same docId.
      Thread.sleep(40000);
      docMgr.write(docId, null, handleUpd, null, t1,
          temporalLsqtCollectionName, updateTime);
      Thread.sleep(5000);

      JSONDocumentManager jsonDocMgr = writerClient.newJSONDocumentManager();
      DocumentPage readResults = jsonDocMgr.read(t1, docId);
      System.out.println("Number of results = " + readResults.size());
      assertEquals( 1, readResults.size());

      QueryManager queryMgr = writerClient.newQueryManager();

      StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
      StructuredQueryDefinition termQuery = sqb
          .collection(temporalLsqtCollectionName);
      t1.commit();

      long start = 1;
      t2 = writerClient.openTransaction();
      DocumentPage termQueryResults = docMgr.search(termQuery, start, t2);
      System.out.println("Number of results = "
          + termQueryResults.getTotalSize());
      assertEquals( 4,
          termQueryResults.getTotalSize());
    } catch (Exception e) {
      System.out.println("Exception when update within 30 sec is "
          + e.getMessage());
    } finally {
      if (t2 != null)
        t2.rollback();
      writerClient.release();
    }
  }

  @Test
  public void testQueryByExampleXML() throws KeyManagementException,
      NoSuchAlgorithmException, IOException, TransformerException,
      XpathException {
    System.out.println("Running testQueryByExampleXML");

    String[] filenames = { "constraint1.xml", "constraint2.xml",
        "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    DatabaseClient client = null;
    try {

      client = getDatabaseClient("rest-writer", "x", getConnType());

      // write docs
      for (String filename : filenames) {
        writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "XML");
      }

      // get the combined query
      File file = new File("src/test/java/com/marklogic/client/functionaltest/qbe/qbe1.xml");

      String qbeQuery = convertFileToString(file);
      StringHandle qbeHandle = new StringHandle(qbeQuery);
      qbeHandle.setFormat(Format.XML);

      QueryManager queryMgr = client.newQueryManager();

      RawQueryByExampleDefinition qbyex = queryMgr.newRawQueryByExampleDefinition(qbeHandle);

      Document resultDoc = queryMgr.search(qbyex, new DOMHandle()).get();

      System.out.println("XML Result" + convertXMLDocumentToString(resultDoc));

      assertXpathEvaluatesTo("1",
          "string(//*[local-name()='result'][last()]//@*[local-name()='index'])",
          resultDoc);
      assertXpathEvaluatesTo("0011",
          "string(//*[local-name()='result'][1]//*[local-name()='id'])",
          resultDoc);
    }
    catch(Exception ex) {
      System.out.println("Exceptions" + ex.getStackTrace());
    }
    finally {
      client.release();
    }
  }

  @Test
  public void testPointAndWord() throws KeyManagementException,
      NoSuchAlgorithmException, IOException, ParserConfigurationException,
      SAXException, XpathException, TransformerException {
    System.out.println("Running testPointAndWord");

    String queryOptionName = "geoConstraintOpt.xml";
    DatabaseClient client = null;
    try {

      client = getDatabaseClient("rest-admin", "x", getConnType());

      // write docs
      for (int i = 1; i <= 9; i++) {
        writeDocumentUsingInputStreamHandle(client,
            "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
      }

      setQueryOption(client, queryOptionName);

      QueryManager queryMgr = client.newQueryManager();

      // create query def
      StringQueryDefinition querydef = queryMgr
          .newStringDefinition(queryOptionName);
      querydef.setCriteria("geo-elem:\"150,-140\" AND john");

      // create handle
      DOMHandle resultsHandle = new DOMHandle();
      queryMgr.search(querydef, resultsHandle);

      // get the result
      Document resultDoc = resultsHandle.get();

      assertXpathEvaluatesTo("1",
          "string(//*[local-name()='result'][last()]//@*[local-name()='index'])",
          resultDoc);
      assertXpathEvaluatesTo("/geo-constraint/geo-constraint8.xml",
          "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);

    }
    catch(Exception ex) {
      System.out.println("Exceptions" + ex.getStackTrace());
    }
    finally {
      client.release();
    }
  }

  @Test
  public void testSearchOnPropertiesBucketAndWord()
      throws KeyManagementException, NoSuchAlgorithmException, IOException,
      ParserConfigurationException, SAXException, XpathException,
      TransformerException {
    System.out.println("Running testSearchOnPropertiesBucketAndWord");

    String filename1 = "property1.xml";
    String filename2 = "property2.xml";
    String filename3 = "property3.xml";
    String queryOptionName = "propertiesSearchWordOpt.xml";
    DatabaseClient client = null;

    try {

      client = getDatabaseClient("rest-admin", "x", getConnType());

      // create and initialize a handle on the metadata
      DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
      DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
      DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();

      // set metadata properties
      metadataHandle1.getProperties().put("popularity", 5);
      metadataHandle2.getProperties().put("popularity", 9);
      metadataHandle3.getProperties().put("popularity", 1);
      metadataHandle1.getProperties().put("city", "Shanghai is a good one");
      metadataHandle2.getProperties().put("city", "Tokyo is hot in the summer");
      metadataHandle3.getProperties().put("city",
          "The food in Seoul is similar in Shanghai");

      // write docs
      writeDocumentUsingInputStreamHandle(client, filename1,
          "/properties-search/", metadataHandle1, "XML");
      writeDocumentUsingInputStreamHandle(client, filename2,
          "/properties-search/", metadataHandle2, "XML");
      writeDocumentUsingInputStreamHandle(client, filename3,
          "/properties-search/", metadataHandle3, "XML");

      setQueryOption(client, queryOptionName);

      QueryManager queryMgr = client.newQueryManager();

      // create query def
      StringQueryDefinition querydef = queryMgr
          .newStringDefinition(queryOptionName);
      querydef.setCriteria("pop:medium AND city-property:Shanghai");

      // create handle
      DOMHandle resultsHandle = new DOMHandle();
      queryMgr.search(querydef, resultsHandle);

      // get the result
      Document resultDoc = resultsHandle.get();

      assertXpathEvaluatesTo("1",
          "string(//*[local-name()='result'][last()]//@*[local-name()='index'])",
          resultDoc);
      assertXpathEvaluatesTo("/properties-search/property1.xml",
          "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);

    }
    catch(Exception ex) {
      System.out.println("Exceptions" + ex.getStackTrace());
    }
    finally {
      client.release();
    }
  }

  /*
   * Checks for cts queries with options on fromLexicons TEST 14
   */
  @Test
  public void testCtsQueriesWithOptions() throws KeyManagementException,
      NoSuchAlgorithmException, IOException, SAXException,
      ParserConfigurationException {
    System.out.println("In testCtsQueriesWithOptions method");
    DatabaseClient client = null;

    try {
      // Create a new Plan.
      client = getDatabaseClient("rest-admin", "x", getConnType());
      RowManager rowMgr = client.newRowManager();
      PlanBuilder p = rowMgr.newPlanBuilder();
      Map<String, CtsReferenceExpr> index1 = new HashMap<String, CtsReferenceExpr>();
      index1.put("uri1", p.cts.uriReference());
      index1.put("city", p.cts.jsonPropertyReference("city"));
      index1.put("popularity", p.cts.jsonPropertyReference("popularity"));
      index1.put("date", p.cts.jsonPropertyReference("date"));
      index1.put("distance", p.cts.jsonPropertyReference("distance"));
      index1.put("point", p.cts.jsonPropertyReference("latLonPoint"));

      Map<String, CtsReferenceExpr> index2 = new HashMap<String, CtsReferenceExpr>();
      index2.put("uri2", p.cts.uriReference());
      index2.put("cityName", p.cts.jsonPropertyReference("cityName"));
      index2.put("cityTeam", p.cts.jsonPropertyReference("cityTeam"));

      // plan1 - fromLexicons
      ModifyPlan plan1 = p.fromLexicons(index1, "myCity",
          p.fragmentIdCol("fragId1"));
      // plan2 - fromLexicons
      ModifyPlan plan2 = p.fromLexicons(index2, "myTeam",
          p.fragmentIdCol("fragId2"));

      XsStringSeqVal propertyName = p.xs.string("city");
      XsStringSeqVal value = p.xs.string("*k");

      XsStringSeqVal options = p.xs.stringSeq("wildcarded", "case-sensitive");

      // ModifyPlan output = plan1.where(p.cts.jsonPropertyWordQuery(propertyName,
      // value, options))
      ModifyPlan output = plan1
          .where(
              p.cts.jsonPropertyWordQuery("city", "*k", "wildcarded",
                  "case-sensitive")).joinInner(plan2)
                  .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
                  .orderBy(p.asc(p.col("date")));

      JacksonHandle jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(output, jacksonHandle);
      JsonNode jsonResults = jacksonHandle.get();

      JsonNode jsonBindingsNodes = jsonResults.path("rows");
      assertTrue(
          1 == jsonBindingsNodes.size());
      assertEquals( "new york",
          jsonBindingsNodes.path(0).path("myCity.city").path("value").asText());
    }
    catch(Exception ex) {
      System.out.println("Exceptions" + ex.getStackTrace());
    }
    finally {
      client.release();
    }
  }

  @Test
  public void testWriteMultiJSONFilesDefaultMetadata() throws KeyManagementException, NoSuchAlgorithmException, Exception
  {
    DatabaseClient client = null;
    try {
      client = getDatabaseClient("rest-admin", "x", getConnType());
      String docId[] = { "/original.json", "/updated.json", "/constraint1.json" };
      String jsonFilename1 = "json-original.json";
      String jsonFilename2 = "json-updated.json";
      String jsonFilename3 = "constraint1.json";

      File jsonFile1 = new File("src/test/java/com/marklogic/client/functionaltest/data/" + jsonFilename1);
      File jsonFile2 = new File("src/test/java/com/marklogic/client/functionaltest/data/" + jsonFilename2);
      File jsonFile3 = new File("src/test/java/com/marklogic/client/functionaltest/data/" + jsonFilename3);

      JSONDocumentManager docMgr = client.newJSONDocumentManager();
      docMgr.setMetadataCategories(Metadata.ALL);
      DocumentWriteSet writeset = docMgr.newWriteSet();
      // put meta-data
      DocumentMetadataHandle mh = setMetadata();
      DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

      ObjectMapper mapper = new ObjectMapper();

      JacksonHandle jacksonHandle1 = new JacksonHandle();
      JacksonHandle jacksonHandle2 = new JacksonHandle();
      JacksonHandle jacksonHandle3 = new JacksonHandle();

      JsonNode originalNode = mapper.readTree(jsonFile1);
      jacksonHandle1.set(originalNode);
      jacksonHandle1.withFormat(Format.JSON);

      JsonNode updatedNode = mapper.readTree(jsonFile2);
      jacksonHandle2.set(updatedNode);
      jacksonHandle2.withFormat(Format.JSON);

      JsonNode constraintNode = mapper.readTree(jsonFile3);
      jacksonHandle3.set(constraintNode);
      jacksonHandle3.withFormat(Format.JSON);

      writeset.addDefault(mh);
      writeset.add(docId[0], jacksonHandle1);
      writeset.add(docId[1], jacksonHandle2);
      writeset.add(docId[2], jacksonHandle3);

      docMgr.write(writeset);

      DocumentPage page = docMgr.read(docId);

      while (page.hasNext()) {
        DocumentRecord rec = page.next();
        docMgr.readMetadata(rec.getUri(), mhRead);
        System.out.println(rec.getUri());
        validateMetadata(mhRead);
      }
      validateMetadata(mhRead);
      mhRead = null;
    }
    catch(Exception ex) {
      System.out.println("Exceptions" + ex.getStackTrace());
    }
    finally {
      client.release();
    }
  }

  /*
   * Write Triples of Type JSON Merge NTriples into the same graph and validate
   * mergeGraphs with transactions.
   *
   * Merge within same write transaction Write and merge Triples within
   * different transactions. Commit the merge transaction Write and merge
   * Triples within different transactions. Rollback the merge transaction Write
   * and merge Triples within different transactions. Rollback the merge
   * transaction and then commit.
   */
  @Test
  public void testMergeGraphWithTransaction() throws InterruptedException,
      KeyManagementException, NoSuchAlgorithmException, IOException {
    String uri = "http://test.sem.quads/json-quads";
    DatabaseClient writerClient = getDatabaseClientOnDatabase(
        appServerHostname, restPort, dbName, "rest-writer", "x",
        getConnType());
    Transaction trxIn = writerClient.openTransaction();
    gmWriter = writerClient.newGraphManager();
    Transaction trxInMergeGraph = null;
    Transaction trxDelIn = null;
    try {
      String ntriple6 = "<http://example.org/s6> <http://example.com/mergeQuadP> <http://example.org/o2> <http://test.sem.quads/json-quads>.";
      File file = new File("src/test/java/com/marklogic/client/functionaltest/data/semantics/bug25348.json");
      FileHandle filehandle = new FileHandle();
      filehandle.set(file);

      // Using client write and merge Triples within same transaction.
      gmWriter.write(uri, filehandle.withMimetype(RDFMimeTypes.RDFJSON), trxIn);
      // Merge Graphs inside the transaction.
      gmWriter.mergeGraphs(
          new StringHandle(ntriple6).withMimetype(RDFMimeTypes.NQUADS), trxIn);
      trxIn.commit();
      FileHandle handle = gmWriter.read(uri, new FileHandle());
      File readFile = handle.get();
      String expectedContent = convertFileToString(readFile);
      assertTrue(
          expectedContent.contains("<http://example.com/ns/person#firstName"));
      assertTrue(
          expectedContent.contains("<http://example.com/mergeQuadP"));
      trxIn = null;
      handle = null;
      readFile = null;
      expectedContent = null;

      // Delete Graphs inside the transaction.
      trxDelIn = writerClient.openTransaction();
      gmWriter.delete(uri, trxDelIn);
      trxDelIn.commit();
      trxDelIn = null;

      // Using client write and merge Triples within different transactions.
      // Commit the merge transaction
      trxIn = writerClient.openTransaction();
      trxInMergeGraph = writerClient.openTransaction();

      gmWriter.write(uri, filehandle.withMimetype(RDFMimeTypes.RDFJSON), trxIn);
      trxIn.commit();
      // Make sure that original triples are available.
      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);
      assertTrue(
          expectedContent.contains("<http://example.com/ns/person#firstName"));
      handle = null;
      readFile = null;
      expectedContent = null;

      // Merge Graphs inside another transaction.
      gmWriter.mergeGraphs(
          new StringHandle(ntriple6).withMimetype(RDFMimeTypes.NQUADS),
          trxInMergeGraph);
      trxInMergeGraph.commit();

      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);
      assertTrue(
          expectedContent.contains("<http://example.com/ns/person#firstName"));
      assertTrue(
          expectedContent.contains("<http://example.com/mergeQuadP"));

      trxIn = null;
      trxInMergeGraph = null;
      handle = null;
      readFile = null;
      expectedContent = null;
      trxDelIn = null;

      // Delete Graphs inside the transaction.
      trxDelIn = writerClient.openTransaction();
      gmWriter.delete(uri, trxDelIn);
      trxDelIn.commit();
      trxDelIn = null;

      // Using client write and merge Triples within different transaction.
      // Rollback the merge transaction.
      trxIn = writerClient.openTransaction();
      trxInMergeGraph = writerClient.openTransaction();

      gmWriter.write(uri, filehandle.withMimetype(RDFMimeTypes.RDFJSON), trxIn);
      trxIn.commit();

      // Make sure that original triples are available.
      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);
      assertTrue(
          expectedContent.contains("<http://example.com/ns/person#firstName"));
      handle = null;
      readFile = null;
      expectedContent = null;
      // Merge Graphs inside the transaction.
      gmWriter.mergeGraphs(
          new StringHandle(ntriple6).withMimetype(RDFMimeTypes.NQUADS),
          trxInMergeGraph);
      trxInMergeGraph.rollback();
      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);

      // Verify if original quad is available.
      assertTrue(
          expectedContent.contains("<http://example.com/ns/person#firstName"));
      assertFalse(
          expectedContent.contains("<http://example.com/mergeQuadP"));

      trxIn = null;
      trxInMergeGraph = null;
      handle = null;
      readFile = null;
      expectedContent = null;

      // Delete Graphs inside the transaction.
      trxDelIn = writerClient.openTransaction();
      gmWriter.delete(uri, trxDelIn);
      trxDelIn.commit();
      trxDelIn = null;

      // Using client write and merge Triples within different transaction.
      // Rollback the merge transaction and then commit.
      trxIn = writerClient.openTransaction();
      trxInMergeGraph = writerClient.openTransaction();

      gmWriter.write(uri, filehandle.withMimetype(RDFMimeTypes.RDFJSON), trxIn);
      trxIn.commit();

      // Make sure that original triples are available.
      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);
      assertTrue(
          expectedContent.contains("<http://example.com/ns/person#firstName"));
      handle = null;
      readFile = null;
      expectedContent = null;

      // Merge Graphs inside the transaction.
      gmWriter.mergeGraphs(
          new StringHandle(ntriple6).withMimetype(RDFMimeTypes.NQUADS),
          trxInMergeGraph);
      // Rollback the merge.
      trxInMergeGraph.rollback();
      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);

      // Verify if original quad is available.
      assertTrue(
          expectedContent.contains("<http://example.com/ns/person#firstName"));
      assertFalse(
          expectedContent.contains("<http://example.com/mergeQuadP"));

      trxIn = null;
      trxInMergeGraph = null;
      handle = null;
      readFile = null;
      expectedContent = null;
      trxInMergeGraph = writerClient.openTransaction();
      gmWriter.mergeGraphs(
          new StringHandle(ntriple6).withMimetype(RDFMimeTypes.NQUADS),
          trxInMergeGraph);
      // Commit the merge.
      trxInMergeGraph.commit();

      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);
      // Verify if original quad is available.
      assertTrue(
          expectedContent.contains("<http://example.com/ns/person#firstName"));
      assertTrue(
          expectedContent.contains("<http://example.com/mergeQuadP"));

      // Delete Graphs inside the transaction.
      trxDelIn = writerClient.openTransaction();
      gmWriter.delete(uri, trxDelIn);
      trxDelIn.commit();
      trxDelIn = null;
      trxIn = null;
      trxInMergeGraph = null;
      handle = null;
      readFile = null;
      expectedContent = null;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (trxIn != null) {
        trxIn.rollback();
        trxIn = null;
      }
      if (trxDelIn != null) {
        trxDelIn.rollback();
        trxDelIn = null;
      }
      if (trxInMergeGraph != null) {
        trxInMergeGraph.rollback();
        trxInMergeGraph = null;
      }
    }
  }

  @Test
  public void testPOJOSearchWithJacksonHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException {
    DatabaseClient client = null;
    try {

      client = getDatabaseClient("rest-admin", "x", getConnType());

      PojoRepository<Artifact, Long> products = client.newPojoRepository(Artifact.class, Long.class);
      PojoPage<Artifact> p;
      this.loadSimplePojos(products);
      QueryManager queryMgr = client.newQueryManager();
      StringQueryDefinition qd = queryMgr.newStringDefinition();
      qd.setCriteria("cogs");
      JacksonHandle results = new JacksonHandle();
      p = products.search(qd, 1, results);
      products.setPageLength(11);
      assertEquals( 3, p.getTotalPages());
      // System.out.println(p.getTotalPages()+results.get().toString());
      long pageNo = 1, count = 0;
      do {
        count = 0;
        p = products.search(qd, pageNo, results);

        while (p.iterator().hasNext()) {
          Artifact a = p.iterator().next();
          validateArtifact(a);
          count++;
          // System.out.println(a.getId()+" "+a.getManufacturer().getName()
          // +"  "+count);
        }
        assertEquals( count, p.size());
        pageNo = pageNo + p.getPageSize();

        assertEquals( results.get().get("start").asLong(), p.getStart());
        assertEquals( "json", results.get().withArray("results").get(1).path("format").asText());
        assertTrue( results.get().withArray("results").get(1).path("uri").asText().contains("Artifact"));
        // System.out.println(results.get().toString());
      } while (!p.isLastPage() && pageNo < p.getTotalSize());
      // assertTrue(results.get().has("metrics"));
      assertEquals( "cogs", results.get().path("qtext").asText());
      assertEquals( 110, results.get().get("total").asInt());
      assertEquals( 10, p.getPageNumber());
      assertEquals( 10, p.getTotalPages());
    }
    catch(Exception ex) {
      System.out.println("Exceptions" + ex.getStackTrace());
    }
    finally {
      client.release();
    }
  }

  /**
   * Write document using DOMHandle
   *
   * @param client
   * @param filename
   * @param uri
   * @param type
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws SAXException
   */

  public static void loadFileToDB(DatabaseClient client, String filename,
      String uri, String type, String[] collections) throws IOException,
      ParserConfigurationException, SAXException {
    // create doc manager
    DocumentManager docMgr = null;
    docMgr = documentMgrSelector(client, docMgr, type);

    File file = new File(datasource + filename);
    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
    for (String coll : collections)
      metadataHandle.getCollections().addAll(coll.toString());

    // write the document content
    DocumentWriteSet writeset = docMgr.newWriteSet();
    writeset.addDefault(metadataHandle);
    writeset.add(uri, handle);

    docMgr.write(writeset);

    System.out.println("Write " + uri + " to database");
  }

  /**
   * Function to select and create document manager based on the type
   *
   * @param client
   * @param docMgr
   * @param type
   * @return
   */
  public static DocumentManager documentMgrSelector(DatabaseClient client,
      DocumentManager docMgr, String type) {
    // create doc manager
    switch (type) {
      case "XML":
        docMgr = client.newXMLDocumentManager();
        break;
      case "Text":
        docMgr = client.newTextDocumentManager();
        break;
      case "JSON":
        docMgr = client.newJSONDocumentManager();
        break;
      case "Binary":
        docMgr = client.newBinaryDocumentManager();
        break;
      case "JAXB":
        docMgr = client.newXMLDocumentManager();
        break;
      default:
        System.out.println("Invalid type");
        break;
    }
    return docMgr;
  }

  public DocumentMetadataHandle setMetadata() {
    // create and initialize a handle on the meta-data
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
    metadataHandle.getCollections().addAll("my-collection1", "my-collection2");
    metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
    metadataHandle.getProperties().put("reviewed", true);
    metadataHandle.getProperties().put("myString", "foo");
    metadataHandle.getProperties().put("myInteger", 10);
    metadataHandle.getProperties().put("myDecimal", 34.56678);
    metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
    metadataHandle.setQuality(23);
    return metadataHandle;
  }

  public void validateMetadata(DocumentMetadataHandle mh) {
    // get meta-data values
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

    assertTrue( actualPermissions.contains("size:5"));
    // assertTrue(
    // actualPermissions.contains("flexrep-eval:[READ]"));
    assertTrue( actualPermissions.contains("rest-reader:[READ]"));
    assertTrue( actualPermissions.contains("rest-writer:[UPDATE]"));
    assertTrue(
        (actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions.contains("app-user:[READ, UPDATE]")));

    // Collections
    String actualCollections = getDocumentCollectionsString(collections);
    System.out.println(collections);

    assertTrue( actualCollections.contains("size:2"));
    assertTrue( actualCollections.contains("my-collection1"));
    assertTrue( actualCollections.contains("my-collection2"));
  }

  public void validateArtifact(Artifact art)
  {
    assertNotNull(art);
    assertNotNull(art.id);
    assertTrue( art.getInventory() > 1000);
  }

  public void loadSimplePojos(PojoRepository products)
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

  public Artifact getArtifact(int counter) {

    Artifact cogs = new Artifact();
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
    return cogs;
  }
}
