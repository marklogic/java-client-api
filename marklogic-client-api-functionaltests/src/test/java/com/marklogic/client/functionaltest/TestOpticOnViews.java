/*
 * Copyright 2014-2019 MarkLogic Corporation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.DigestAuthContext;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.AccessPlan;
import com.marklogic.client.expression.PlanBuilder.ExportablePlan;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.type.PlanColumn;
import com.marklogic.client.type.PlanExprColSeq;
import com.marklogic.client.type.PlanParamExpr;
import com.marklogic.client.type.PlanSortKeySeq;
import com.marklogic.client.type.PlanSystemColumn;
import com.marklogic.client.type.XsStringVal;

public class TestOpticOnViews extends BasicJavaClientREST {

  private static String dbName = "TestOpticOnViewsDB";
  private static String schemadbName = "TestOpticOnViewsSchemaDB";
  private static String[] fNames = { "TestOpticOnViewsDB-1" };
  private static String[] schemafNames = { "TestOpticOnViewsSchemaDB-1" };
  
  private static DatabaseClient client;
  private static String datasource = "src/test/java/com/marklogic/client/functionaltest/data/optics/";

  @BeforeClass
  public static void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception
  {
    System.out.println("In TestOpticOnViews setup");
    DatabaseClient schemaDBclient = null;

    configureRESTServer(dbName, fNames);

    // Add new range elements into this array
    String[][] rangeElements = {
        // { scalar-type, namespace-uri, localname, collation,
        // range-value-positions, invalid-values }
        // If there is a need to add additional fields, then add them to the end
        // of each array
        // and pass empty strings ("") into an array where the additional field
        // does not have a value.
        // For example : as in namespace, collections below.
        // Add new RangeElementIndex as an array below.
        { "string", "", "city", "http://marklogic.com/collation/", "false", "reject" },
        { "int", "", "popularity", "", "false", "reject" },
        { "double", "", "distance", "", "false", "reject" },
        { "date", "", "date", "", "false", "reject" },
        { "string", "", "cityName", "http://marklogic.com/collation/", "false", "reject" },
        { "string", "", "cityTeam", "http://marklogic.com/collation/", "false", "reject" },
        { "long", "", "cityPopulation", "", "false", "reject" }
    };

    // Insert the range indices
    addRangeElementIndex(dbName, rangeElements);

    // Insert word lexicon.
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode mainNode = mapper.createObjectNode();
    ObjectNode wordLexicon = mapper.createObjectNode();
    ArrayNode childArray = mapper.createArrayNode();
    ObjectNode childNodeObject = mapper.createObjectNode();

    childNodeObject.put("namespace-uri", "http://marklogic.com/collation/");
    childNodeObject.put("localname", "city");
    childNodeObject.put("collation", "http://marklogic.com/collation/");
    childArray.add(childNodeObject);
    mainNode.withArray("element-word-lexicon").add(childArray);

    setDatabaseProperties(dbName, "element-word-lexicon", mainNode);

    // Add geo element index.
    addGeospatialElementIndexes(dbName, "latLonPoint", "", "wgs84", "point", false, "reject");
    // Enable triple index.
    enableTripleIndex(dbName);
    waitForServerRestart();
    // Enable collection lexicon.
    enableCollectionLexicon(dbName);
    // Enable uri lexicon.
    setDatabaseProperties(dbName, "uri-lexicon", true);

    // Create schema database
    createDB(schemadbName);
    createForest(schemafNames[0], schemadbName);
    // Set the schemadbName database as the Schema database.
    setDatabaseProperties(dbName, "schema-database", schemadbName);

    createUserRolesWithPrevilages("opticRole", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createRESTUser("opticUser", "0pt1c", "tde-admin", "tde-view", "opticRole", "rest-admin", "rest-writer", 
    		                             "rest-reader", "rest-extension-user", "manage-user");    

    if (IsSecurityEnabled()) {
        schemaDBclient = getDatabaseClientOnDatabase(getRestServerHostName(), getRestServerPort(), schemadbName, "opticUser", "0pt1c", getConnType());
        client = getDatabaseClient("opticUser", "0pt1c", getConnType());
    }
    else {
        schemaDBclient = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), schemadbName, new DigestAuthContext("opticUser", "0pt1c"));
        client = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), new DigestAuthContext("opticUser", "0pt1c"));
    }

    // Install the TDE templates into schemadbName DB
    // loadFileToDB(client, filename, docURI, collection, document format)
    loadFileToDB(schemaDBclient, "masterDetail.tdex", "/optic/view/test/masterDetail.tdex", "XML", new String[] { "http://marklogic.com/xdmp/tde" });
    loadFileToDB(schemaDBclient, "masterDetail2.tdej", "/optic/view/test/masterDetail2.tdej", "JSON", new String[] { "http://marklogic.com/xdmp/tde" });
    loadFileToDB(schemaDBclient, "masterDetail3.tdej", "/optic/view/test/masterDetail3.tdej", "JSON", new String[] { "http://marklogic.com/xdmp/tde" });

    // Load XML data files.
    loadFileToDB(client, "masterDetail.xml", "/optic/view/test/masterDetail.xml", "XML", new String[] { "/optic/view/test" });
    loadFileToDB(client, "playerTripleSet.xml", "/optic/triple/test/playerTripleSet.xml", "XML", new String[] { "/optic/player/triple/test" });
    loadFileToDB(client, "teamTripleSet.xml", "/optic/triple/test/teamTripleSet.xml", "XML", new String[] { "/optic/team/triple/test" });
    loadFileToDB(client, "otherPlayerTripleSet.xml", "/optic/triple/test/otherPlayerTripleSet.xml", "XML", new String[] { "/optic/other/player/triple/test" });
    loadFileToDB(client, "doc4.xml", "/optic/lexicon/test/doc4.xml", "XML", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "doc5.xml", "/optic/lexicon/test/doc5.xml", "XML", new String[] { "/optic/lexicon/test" });

    // Load JSON data files.
    loadFileToDB(client, "masterDetail2.json", "/optic/view/test/masterDetail2.json", "JSON", new String[] { "/optic/view/test" });
    loadFileToDB(client, "masterDetail3.json", "/optic/view/test/masterDetail3.json", "JSON", new String[] { "/optic/view/test" });

    loadFileToDB(client, "doc1.json", "/optic/lexicon/test/doc1.json", "JSON", new String[] { "/other/coll1", "/other/coll2" });
    loadFileToDB(client, "doc2.json", "/optic/lexicon/test/doc2.json", "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "doc3.json", "/optic/lexicon/test/doc3.json", "JSON", new String[] { "/optic/lexicon/test" });

    loadFileToDB(client, "city1.json", "/optic/lexicon/test/city1.json", "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "city2.json", "/optic/lexicon/test/city2.json", "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "city3.json", "/optic/lexicon/test/city3.json", "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "city4.json", "/optic/lexicon/test/city4.json", "JSON", new String[] { "/optic/lexicon/test" });
    loadFileToDB(client, "city5.json", "/optic/lexicon/test/city5.json", "JSON", new String[] { "/optic/lexicon/test" });
    Thread.sleep(10000);
    schemaDBclient.release();
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

  public static void loadFileToDB(DatabaseClient client, String filename, String uri, String type, String[] collections) throws IOException, ParserConfigurationException,
      SAXException
  {
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
  public static DocumentManager documentMgrSelector(DatabaseClient client, DocumentManager docMgr, String type) {
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

  /*
   * This test checks a simple Schema and View ordered by id.
   * 
   * The query should be returning 6 results ordered by id. Test asserts only on
   * the first node results. Uses JacksonHandle
   */
  @Test
  public void testnamedSchemaAndView() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testnamedSchemaAndView method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan = p.fromView("opticFunctionalTest", "detail")
        .orderBy(p.col( "id"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 6 nodes returned.
    assertEquals("Six nodes not returned from testnamedSchemaAndView method ", 6, jsonBindingsNodes.size());

    // Verify result 1's values.
    assertEquals("Element 1 opticFunctionalTest.detail.id type value incorrect", "xs:integer", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.id").path("type")
        .asText());
    assertEquals("Element 1 opticFunctionalTest.detail.id value is incorrect", "1", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.id").path("value").asText());

    assertEquals("Element 1 opticFunctionalTest.detail.name type is incorrect", "xs:string", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.name").path("type")
        .asText());
    assertEquals("Element 1 opticFunctionalTest.detail.name value is incorrect", "Detail 1", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.name").path("value")
        .asText());

    assertEquals("Element 1 opticFunctionalTest.detail.masterId type value incorrect", "xs:integer",
        jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.masterId").path("type").asText());
    assertEquals("Element 1 opticFunctionalTest.detail.masterId value is incorrect", "1", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.masterId").path("value")
        .asText());

    assertEquals("Element 1 opticFunctionalTest.detail.amount type is incorrect", "xs:double", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.amount").path("type")
        .asText());
    assertEquals("Element 1 opticFunctionalTest.detail.amount value is incorrect", "10.01", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.amount").path("value")
        .asText());

    assertEquals("Element 1 opticFunctionalTest.detail.color type is incorrect", "xs:string", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.color").path("type")
        .asText());
    assertEquals("Element 1 opticFunctionalTest.detail.color value is incorrect", "blue", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.color").path("value").asText());
    // Verify only the name value of other nodes in the array results.

    assertEquals("Element 2 opticFunctionalTest.detail.name value is incorrect", "Detail 2", jsonBindingsNodes.path(1).path("opticFunctionalTest.detail.name").path("value")
        .asText());
    assertEquals("Element 3 opticFunctionalTest.detail.name value is incorrect", "Detail 3", jsonBindingsNodes.path(2).path("opticFunctionalTest.detail.name").path("value")
        .asText());
    assertEquals("Element 4 opticFunctionalTest.detail.name value is incorrect", "Detail 4", jsonBindingsNodes.path(3).path("opticFunctionalTest.detail.name").path("value")
        .asText());
    assertEquals("Element 5 opticFunctionalTest.detail.name value is incorrect", "Detail 5", jsonBindingsNodes.path(4).path("opticFunctionalTest.detail.name").path("value")
        .asText());
    assertEquals("Element 6 opticFunctionalTest.detail.name value is incorrect", "Detail 6", jsonBindingsNodes.path(5).path("opticFunctionalTest.detail.name").path("value")
        .asText());
  }

  /*
   * This test checks a simple Schema and view with a qualifier ordered by id.
   * 
   * The query should be returning 6 results ordered by id. Test asserts only on
   * the first node results. Uses JacksonHandle
   */
  @Test
  public void testnamedSchemaViewWithQualifier() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testnamedSchemaViewWithQualifier method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    AccessPlan plan = p.fromView("opticFunctionalTest", "detail", "MarkLogicQAQualifier");

    plan.orderBy(p.sortKeySeq(p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.masterId"),
        p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.color"),
        p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.amount")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 6 nodes returned.
    assertEquals("Six nodes not returned from testnamedSchemaViewWithQualifier method ", 6, jsonBindingsNodes.size());
  }

  /*
   * This test checks group by with a view. Should return 3 items. Tested
   * arrayAggregate, union, groupby methods Uses schemaCol for Columns selection
   */
  @Test
  public void testgroupBy() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testgroupBy method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
        .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));

    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
        .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.union(plan2)
        .select(p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
            p.schemaCol("opticFunctionalTest", "master", "date"),
            p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
            p.col( "amount"),
            p.col( "color")
        )
        .groupBy(p.col("MasterName"), p.arrayAggregate("arrayDetail", "DetailName"))
        .orderBy(p.desc(p.col("MasterName")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 3 nodes returned.
    assertEquals("Three nodes not returned from testgroupBy method ", 3, jsonBindingsNodes.size());
    assertEquals("Element 1 testGroupBy MasterName value incorrect", "Master 2", jsonBindingsNodes.get(0).path("MasterName").path("value").asText());
    assertEquals("Element 1 testGroupBy arrayDetail size incorrect", 0, jsonBindingsNodes.get(0).path("arrayDetail").path("value").size());
    assertEquals("Element 2 testGroupBy MasterName value incorrect", "Master 1", jsonBindingsNodes.get(1).path("MasterName").path("value").asText());
    assertEquals("Element 2 testGroupBy arrayDetail size incorrect", 0, jsonBindingsNodes.get(1).path("arrayDetail").path("value").size());
    assertEquals("Element 3 testGroupBy arrayDetail size incorrect", 6, jsonBindingsNodes.get(2).path("arrayDetail").path("value").size());
    // Verify arrayDetail array
    assertEquals("Element 3 testGroupBy arrayDetail value at index 1 incorrect", "Detail 1", jsonBindingsNodes.get(2).path("arrayDetail").path("value").get(0).asText());
    assertEquals("Element 3 testGroupBy arrayDetail value at index 2 incorrect", "Detail 2", jsonBindingsNodes.get(2).path("arrayDetail").path("value").get(1).asText());
    assertEquals("Element 3 testGroupBy arrayDetail value at index 6 incorrect", "Detail 6", jsonBindingsNodes.get(2).path("arrayDetail").path("value").get(5).asText());
  }

  /*
   * This test checks join inner with keymatch. Should return 6 items.
   */
  @Test
  public void testjoinInnerKeyMatch() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testjoinInnerKeyMatch method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
        .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
        .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinInner(plan2)
        .where(
            p.eq(p.schemaCol("opticFunctionalTest", "master", "id"),
                p.col( "masterId")
                )
        )
        .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail", "id")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 6 nodes returned.
    assertEquals("Six nodes not returned from testjoinInnerKeyMatch method ", 6, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);

    assertEquals("Element 1 opticFunctionalTest.detail.id value incorrect", "1", first.path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals("Element 1 opticFunctionalTest.master.id value incorrect", "1", first.path("opticFunctionalTest.master.id").path("value").asText());
    assertEquals("Element 1 opticFunctionalTest.detail.masterId value incorrect", "1", first.path("opticFunctionalTest.detail.masterId").path("value").asText());
    assertEquals("Element 1 opticFunctionalTest.detail.name value incorrect", "Detail 1", first.path("opticFunctionalTest.detail.name").path("value").asText());

    // Verify sixth node.
    JsonNode sixth = jsonBindingsNodes.path(5);
    assertEquals("Element 6 opticFunctionalTest.detail.id value incorrect", "6", sixth.path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals("Element 6 opticFunctionalTest.master.id value incorrect", "2", sixth.path("opticFunctionalTest.master.id").path("value").asText());
    assertEquals("Element 6 opticFunctionalTest.detail.masterId value incorrect", "2", sixth.path("opticFunctionalTest.detail.masterId").path("value").asText());
    assertEquals("Element 6 opticFunctionalTest.detail.name value incorrect", "Detail 6", sixth.path("opticFunctionalTest.detail.name").path("value").asText());
  }

  /*
   * This test checks join inner with keymatch with select. Should return 6
   * items.
   */
  @Test
  public void testjoinInnerKeyMatchWithSelect() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testjoinInnerKeyMatchWithSelect method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
        .orderBy(p.col( "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
        .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinInner(plan2)
        .where(
            p.eq(
                p.schemaCol("opticFunctionalTest", "master", "id"),
                p.col( "masterId")
                )
        )
        .select(
            p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
            p.schemaCol("opticFunctionalTest", "master", "date"),
            p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
            p.col( "amount"),
            p.col( "color")
        )
        .orderBy(p.desc(p.col("DetailName")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 6 nodes returned.
    assertEquals("Three nodes not returned from testjoinInnerOffsetAndLimit method ", 6, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);

    assertEquals("Element 1 MasterName value incorrect", "Master 2", first.path("MasterName").path("value").asText());
    assertEquals("Element 1 DetailName value incorrect", "Detail 6", first.path("DetailName").path("value").asText());
    assertEquals("Element 1 opticFunctionalTest.detail.amount value incorrect", "60.06", first.path("opticFunctionalTest.detail.amount").path("value").asText());
    // Verify sixth node.
    JsonNode sixth = jsonBindingsNodes.path(5);
    assertEquals("Element 6 MasterName value incorrect", "Master 1", sixth.path("MasterName").path("value").asText());
    assertEquals("Element 6 DetailName value incorrect", "Detail 1", sixth.path("DetailName").path("value").asText());
    assertEquals("Element 6 opticFunctionalTest.detail.color value incorrect", "blue", sixth.path("opticFunctionalTest.detail.color").path("value").asText());
  }

  /*
   * This test checks join inner with keymatch with select. Should return 2
   * items.
   */
  @Test
  public void testjoinInnerGroupBy() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testjoinInnerGroupBy method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
        .orderBy(p.col( "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
        .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinInner(plan2)
        .where(
            p.eq(
                p.schemaCol("opticFunctionalTest", "master", "id"),
                p.col( "masterId")
                )
        )
        .groupBy(p.schemaCol("opticFunctionalTest", "master", "name"), p.sum(p.col("DetailSum"), p.col( "amount")))
        .orderBy(p.desc(p.col("DetailSum")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 2 nodes returned.
    assertEquals("Two nodes not returned from testjoinInnerGroupBy method ", 2, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);

    assertEquals("Element 1 opticFunctionalTest.master.name value incorrect", "Master 2", first.path("opticFunctionalTest.master.name").path("value").asText());
    assertEquals("Element 1 DetailSum value incorrect", "120.12", first.path("DetailSum").path("value").asText());
    // Verify second node.
    JsonNode second = jsonBindingsNodes.path(1);
    assertEquals("Element 2 opticFunctionalTest.master.name value incorrect", "Master 1", second.path("opticFunctionalTest.master.name").path("value").asText());
    assertEquals("Element 2 DetailSum value incorrect", "90.09", second.path("DetailSum").path("value").asText());
  }

  /*
   * This test checks join left outer with select. Should return 2 items.
   */
  @Test
  public void testjoinLeftOuterWithSelect() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testjoinLeftOuterWithSelect method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
        .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
        .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinLeftOuter(plan2)
        .select(
            p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
            p.schemaCol("opticFunctionalTest", "master", "date"),
            p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
            p.col( "amount"),
            p.col( "color")
        )
        .orderBy(p.sortKeySeq(p.desc(p.col("DetailName")), p.desc(p.col("MasterName"))));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 12 nodes returned.
    assertEquals("Twelve nodes not returned from testjoinLeftOuterWithSelect method ", 12, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);

    assertEquals("Element 1 MasterName value incorrect", "Master 2", first.path("MasterName").path("value").asText());
    assertEquals("Element 1 DetailName value incorrect", "Detail 6", first.path("DetailName").path("value").asText());

    // Verify second node.
    JsonNode second = jsonBindingsNodes.path(1);
    assertEquals("Element 2 MasterName value incorrect", "Master 1", second.path("MasterName").path("value").asText());
    assertEquals("Element 2 DetailName value incorrect", "Detail 6", second.path("DetailName").path("value").asText());

    // Verify twelveth node.
    JsonNode twelve = jsonBindingsNodes.path(11);
    assertEquals("Element 12 MasterName value incorrect", "Master 1", twelve.path("MasterName").path("value").asText());
    assertEquals("Element 12 DetailName value incorrect", "Detail 1", twelve.path("DetailName").path("value").asText());
  }

  /*
   * This test checks join cross product. Should return 12 items.
   */
  @Test
  public void testjoinCrossProduct() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testjoinCrossProduct method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
        .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
        .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinCrossProduct(plan2)
        .select(
            p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
            p.schemaCol("opticFunctionalTest", "master", "date"),
            p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
            p.col( "amount"),
            p.col( "color")
        )
        .orderBy(p.desc(p.col("DetailName")))
        .orderBy(p.asc(p.col("MasterName")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 12 nodes returned.
    assertEquals("Twelve nodes not returned from testjoinCrossProduct method ", 12, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals("Element 1 MasterName value incorrect", "Master 1", first.path("MasterName").path("value").asText());
    assertEquals("Element 1 DetailName value incorrect", "Detail 6", first.path("DetailName").path("value").asText());
    // Verify second node.
    JsonNode second = jsonBindingsNodes.path(1);
    assertEquals("Element 2 MasterName value incorrect", "Master 1", second.path("MasterName").path("value").asText());
    assertEquals("Element 2 DetailName value incorrect", "Detail 5", second.path("DetailName").path("value").asText());
    // Verify second node.
    JsonNode twelve = jsonBindingsNodes.path(11);
    assertEquals("Element 12 MasterName value incorrect", "Master 2", twelve.path("MasterName").path("value").asText());
    assertEquals("Element 12 DetailName value incorrect", "Detail 1", twelve.path("DetailName").path("value").asText());
  }

  /*
   * This test checks inner join with accessor plan and on. Verifies
   * joinInner(), on(), offset() orderBy() desc(),limits(), RowSet and RowRecord
   * iterator with a view Should return 3 items.
   */
  @Test
  public void testjoinInnerAccessorPlanAndOn() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testjoinInnerAccessorPlanAndOn method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", "myDetail");

    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master", "myMaster");
    PlanColumn masterIdCol1 = p.viewCol("myDetail", "masterId");
    PlanColumn masterIdCol2 = p.viewCol("myMaster", "id");
    PlanColumn detailIdCol = p.viewCol("myDetail", "id");
    PlanColumn detailNameCol = p.viewCol("myDetail", "name");
    PlanColumn masterNameCol = p.viewCol("myMaster", "name");

    ModifyPlan plan3 = plan1.joinInner(plan2, p.on(masterIdCol1, masterIdCol2), p.ge(detailIdCol, p.xs.intVal(3)))
        .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
        .orderBy(p.desc(detailNameCol))
        .offsetLimit(1, 100);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 3 nodes returned.
    assertEquals("Three nodes not returned from testjoinInnerAccessorPlanAndOn method ", 3, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals("Element 1 myMaster.id value incorrect", "1", first.path("myMaster.id").path("value").asText());
    assertEquals("Element 1 myMaster.name value incorrect", "Master 1", first.path("myMaster.name").path("value").asText());
    assertEquals("Element 1 myDetail.id value incorrect", "5", first.path("myDetail.id").path("value").asText());
    assertEquals("Element 1 myDetail.name value incorrect", "Detail 5", first.path("myDetail.name").path("value").asText());
    // Verify second node.
    JsonNode second = jsonBindingsNodes.path(1);
    assertEquals("Element 2 myMaster.id value incorrect", "2", second.path("myMaster.id").path("value").asText());
    assertEquals("Element 2 myMaster.name value incorrect", "Master 2", second.path("myMaster.name").path("value").asText());
    assertEquals("Element 2 myDetail.id value incorrect", "4", second.path("myDetail.id").path("value").asText());
    assertEquals("Element 2 myDetail.name value incorrect", "Detail 4", second.path("myDetail.name").path("value").asText());
    // Verify third node.
    JsonNode third = jsonBindingsNodes.path(2);
    assertEquals("Element 3 myMaster.id value incorrect", "1", third.path("myMaster.id").path("value").asText());
    assertEquals("Element 3 myMaster.name value incorrect", "Master 1", third.path("myMaster.name").path("value").asText());
    assertEquals("Element 3 myDetail.id value incorrect", "3", third.path("myDetail.id").path("value").asText());
    assertEquals("Element 3 myDetail.name value incorrect", "Detail 3", third.path("myDetail.name").path("value").asText());

    // Verify RowSet and RowRecord.
    RowSet<RowRecord> rowSet = rowMgr.resultRows(plan3);
    String[] colNames = rowSet.getColumnNames();
    Arrays.sort(colNames);

    String[] exptdColumnNames = { "myMaster.id", "myMaster.name", "myDetail.id", "myDetail.name" };
    Arrays.sort(exptdColumnNames);
    // Verify if all columns are available.
    assertTrue(Arrays.equals(colNames, exptdColumnNames));

    // Verify RowRecords using Iterator
    Iterator<RowRecord> rowItr = rowSet.iterator();

    RowRecord record = null;
    if (rowItr.hasNext()) {
      record = rowItr.next();
      assertEquals("Element 1 RowSet Iterator value incorrect", 1, record.getInt("myMaster.id"));
      assertEquals("Element 1 RowSet Iterator value incorrect", 5, record.getInt("myDetail.id"));
      assertEquals("Element 1 RowSet Iterator value incorrect", "Detail 5", record.getString("myDetail.name"));
      assertEquals("Element 1 RowSet Iterator value incorrect", "Master 1", record.getString("myMaster.name"));

      XsStringVal str = record.getValueAs("myMaster.name", XsStringVal.class);
      assertEquals("Element 1 RowSet Iterator value incorrect", "Master 1", str.getString());
    }
    else {
      fail("Could not traverse Iterator<RowRecord> in testjoinInnerOffsetAndLimit method");
    }
  }

  /*
   * This test checks join inner with null schema. Should return 3 nodes
   */
  @Test
  public void testjoinInnerWithNullSchema() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testjoinInnerWithNullSchema method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView(null, "detail3");
    ModifyPlan plan2 = p.fromView(null, "master3");
    ModifyPlan plan3 = plan1.joinInner(plan2)
        .where(
            p.eq(
                p.schemaCol(null, "master3", "id"),
                p.schemaCol(null, "detail3", "masterId")
                )
        )
        .orderBy(p.asc(p.schemaCol(null, "detail3", "id")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 3 node3 returned.
    assertEquals("Three nodes not returned from testExportPlan method ", 3, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals("Element 1 opticFunctionalTest3.detail3.id value incorrect", "7", first.path("opticFunctionalTest3.detail3.id").path("value").asText());
    assertEquals("Element 1 opticFunctionalTest3.master3.date value incorrect", "2016-03-01", first.path("opticFunctionalTest3.master3.date").path("value").asText());

    JsonNode second = jsonBindingsNodes.path(1);
    assertEquals("Element 2 opticFunctionalTest3.detail3.name value incorrect", "Detail 8", second.path("opticFunctionalTest3.detail3.name").path("value").asText());
    assertEquals("Element 2 opticFunctionalTest3.detail3.amount value incorrect", "89.36", second.path("opticFunctionalTest3.detail3.amount").path("value").asText());

    JsonNode third = jsonBindingsNodes.path(2);
    assertEquals("Element 3 opticFunctionalTest3.detail3.name value incorrect", "Detail 11", third.path("opticFunctionalTest3.detail3.name").path("value").asText());
    assertEquals("Element 3 opticFunctionalTest3.detail3.color value incorrect", "green", third.path("opticFunctionalTest3.detail3.color").path("value").asText());
  }

  /*
   * This test checks when we export plan. Should return 1 item.
   */
  @Test
  public void testExportPlanWithPlanCol() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testExportPlanWithPlanCol method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    JacksonHandle exportHandle = new JacksonHandle();

    AccessPlan plan1 = p.fromView("opticFunctionalTest", "detail");
    AccessPlan plan2 = p.fromView("opticFunctionalTest", "master");

    PlanColumn masterIdCol1 = plan1.col("masterId");
    PlanColumn masterIdCol2 = plan2.col("id");
    PlanColumn idCol1 = plan2.col("id");
    PlanColumn idCol2 = plan1.col("id");
    PlanColumn detailNameCol = plan1.col("name");
    
    ModifyPlan exportedPlan = plan1.joinInner(plan2, p.on(masterIdCol1, masterIdCol2), p.on(idCol1, idCol2))
        .orderBy(p.desc(detailNameCol))
        .offsetLimit(1, 100);
    ModifyPlan exportedPlan2 = plan1.joinInner(plan2, p.on(masterIdCol1, masterIdCol2), p.on(idCol1, idCol2))
        .orderBy(p.desc(detailNameCol))
        .offsetLimit(1, 100);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(exportedPlan, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 1 node returned.
    assertEquals("One node not returned from testExportPlan method ", 1, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals("Element 1 opticFunctionalTest.master.id value incorrect", "1", first.path("opticFunctionalTest.master.id").path("value").asText());
    assertEquals("Element 1 opticFunctionalTest.master.name value incorrect", "Master 1", first.path("opticFunctionalTest.master.name").path("value").asText());
    assertEquals("Element 1 opticFunctionalTest.detail.id value incorrect", "1", first.path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals("Element 1 opticFunctionalTest.detail.name value incorrect", "Detail 1", first.path("opticFunctionalTest.detail.name").path("value").asText());
    assertEquals("Element 1 opticFunctionalTest.detail.masterId value incorrect", "1", first.path("opticFunctionalTest.detail.masterId").path("value").asText());
    assertEquals("Element 1 opticFunctionalTest.master.date value incorrect", "2015-12-01", first.path("opticFunctionalTest.master.date").path("value").asText());
    assertEquals("Element 1 opticFunctionalTest.detail.amount value incorrect", "10.01", first.path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals("Element 1 opticFunctionalTest.detail.color value incorrect", "blue", first.path("opticFunctionalTest.detail.color").path("value").asText());

    // Export the Plan to a handle.
    exportedPlan.export(exportHandle);
    JsonNode exportNode = exportHandle.get();
    // verify parts of the Exported Plan String.
    assertEquals("Plan export incorrect", "from-view", exportNode.path("$optic").path("args").get(0).path("fn").asText());
    assertEquals("Plan export incorrect", "join-inner", exportNode.path("$optic").path("args").get(1).path("fn").asText());
    assertEquals("Plan export incorrect", "from-view", exportNode.path("$optic").path("args").get(1).path("args").get(0).path("args").get(0).path("fn").asText());
    assertEquals("Plan export incorrect", "order-by", exportNode.path("$optic").path("args").get(2).path("fn").asText());
    assertEquals("Plan export incorrect", "offset-limit", exportNode.path("$optic").path("args").get(3).path("fn").asText());

    // ExportAs the Plan to a handle.
    String strJackHandleAs = exportedPlan.exportAs(String.class);
    JsonNode JsonNodeAs = exportedPlan2.exportAs(JsonNode.class);

    // verify parts of the Exported Plan String.
    ObjectMapper mapper = new ObjectMapper();
    JsonNode exportedAs = mapper.readTree(strJackHandleAs);
    assertEquals("Plan exportAs incorrect", "from-view", exportedAs.path("$optic").path("args").get(0).path("fn").asText());
    assertEquals("Plan exportAs incorrect", "join-inner", exportedAs.path("$optic").path("args").get(1).path("fn").asText());
    assertEquals("Plan exportAs incorrect", "from-view", exportedAs.path("$optic").path("args").get(1).path("args").get(0).path("args").get(0).path("fn").asText());
    assertEquals("Plan exportAs incorrect", "order-by", exportedAs.path("$optic").path("args").get(2).path("fn").asText());
    assertEquals("Plan exportAs incorrect", "offset-limit", exportedAs.path("$optic").path("args").get(3).path("fn").asText());

    // Verify with exportAs to JsonNode
    assertEquals("Plan exportAs incorrect", "from-view", JsonNodeAs.path("$optic").path("args").get(0).path("fn").asText());
    assertEquals("Plan exportAs incorrect", "join-inner", JsonNodeAs.path("$optic").path("args").get(1).path("fn").asText());
    assertEquals("Plan exportAs incorrect", "from-view", JsonNodeAs.path("$optic").path("args").get(1).path("args").get(0).path("args").get(0).path("fn").asText());
    assertEquals("Plan exportAs incorrect", "order-by", JsonNodeAs.path("$optic").path("args").get(2).path("fn").asText());
    assertEquals("Plan exportAs incorrect", "offset-limit", JsonNodeAs.path("$optic").path("args").get(3).path("fn").asText());

    // Export a plan with error / incorrect column
    exportHandle = new JacksonHandle();
    exportedPlan.export(exportHandle);
    JsonNode exportNodedAA = exportHandle.get();

    assertEquals("Plan exportAs incorrect", "from-view", exportNodedAA.path("$optic").path("args").get(0).path("fn").asText());
    assertEquals("Plan exportAs incorrect", "join-inner", exportedAs.path("$optic").path("args").get(1).path("fn").asText());
    assertEquals("Plan exportAs incorrect", "order-by", exportedAs.path("$optic").path("args").get(2).path("fn").asText());
  }

  /*
   * This test checks offset with positive value, negative value and zero.
   * Should return 3 items, null, 6 items and exception
   */
  @Test
  public void testoffsetVales() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testoffsetVales method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", "myDetail");
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master", "myMaster");
    PlanColumn masterIdCol1 = p.viewCol("myDetail", "masterId");
    PlanColumn masterIdCol2 = p.viewCol("myMaster", "id");
    PlanColumn detailIdCol = p.viewCol("myDetail", "id");
    PlanColumn detailNameCol = p.viewCol("myDetail", "name");
    PlanColumn masterNameCol = p.viewCol("myMaster", "name");
    ModifyPlan plan3 = plan1.joinInner(plan2)
        .where(p.eq(masterIdCol1, masterIdCol2))
        .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
        .orderBy(p.desc(detailNameCol))
        .offset(1)
        .limit(3);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 3 node returned.
    assertEquals("Three nodes not returned from testoffsetVales method ", 3, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals("Element 1 myMaster.id value incorrect", "1", first.path("myMaster.id").path("value").asText());
    assertEquals("Element 1 myMaster.name value incorrect", "Master 1", first.path("myMaster.name").path("value").asText());
    assertEquals("Element 1 myDetail.id value incorrect", "5", first.path("myDetail.id").path("value").asText());
    assertEquals("Element 1 myDetail.name value incorrect", "Detail 5", first.path("myDetail.name").path("value").asText());
    JsonNode second = jsonBindingsNodes.path(1);
    assertEquals("Element 2 myMaster.id value incorrect", "2", second.path("myMaster.id").path("value").asText());
    assertEquals("Element 2 myMaster.name value incorrect", "Master 2", second.path("myMaster.name").path("value").asText());
    assertEquals("Element 2 myDetail.id value incorrect", "4", second.path("myDetail.id").path("value").asText());
    assertEquals("Element 2 myDetail.name value incorrect", "Detail 4", second.path("myDetail.name").path("value").asText());

    JsonNode third = jsonBindingsNodes.path(2);
    assertEquals("Element 3 myMaster.id value incorrect", "1", third.path("myMaster.id").path("value").asText());
    assertEquals("Element 3 myMaster.name value incorrect", "Master 1", third.path("myMaster.name").path("value").asText());
    assertEquals("Element 3 myDetail.id value incorrect", "3", third.path("myDetail.id").path("value").asText());
    assertEquals("Element 3 myDetail.name value incorrect", "Detail 3", third.path("myDetail.name").path("value").asText());

    // offset with out of bound value
    ModifyPlan plan4 = plan1.joinInner(plan2)
        .where(
            p.eq(masterIdCol1, masterIdCol2)
        )
        .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
        .orderBy(p.desc(detailNameCol))
        .offset(10);
    JacksonHandle jacksonHandle10 = new JacksonHandle();
    jacksonHandle10.setMimetype("application/json");

    rowMgr.resultDoc(plan4, jacksonHandle10);
    JsonNode jsonResults10 = jacksonHandle10.get();

    // Should be null.
    assertNull("No nodes should have returned from testoffsetVales method ", jsonResults10);

    // offset with 0 bound value
    ModifyPlan plan5 = plan1.joinInner(plan2)
        .where(p.eq(masterIdCol1, masterIdCol2))
        .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
        .orderBy(p.desc(detailNameCol))
        .offset(0);
    JacksonHandle jacksonHandleZOffset = new JacksonHandle();
    jacksonHandleZOffset.setMimetype("application/json");

    rowMgr.resultDoc(plan5, jacksonHandleZOffset);
    JsonNode jsonResultsZOffset = jacksonHandleZOffset.get();
    JsonNode jsonBindingsNodesZOffset = jsonResultsZOffset.path("rows");
    // Should have 6 node returned.
    assertEquals("Six nodes not returned from testoffsetVales method ", 6, jsonBindingsNodesZOffset.size());

    // offset with negative bound value
    StringBuilder str = new StringBuilder();
    try {
      ModifyPlan plan6 = plan1.joinInner(plan2)
          .where(p.eq(masterIdCol1, masterIdCol2))
          .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
          .orderBy(p.desc(detailNameCol))
          .offset(-2);
      JacksonHandle jacksonHandleNegOffset = new JacksonHandle();
      jacksonHandleNegOffset.setMimetype("application/json");

      rowMgr.resultDoc(plan6, jacksonHandleNegOffset);
      JsonNode jsonResultsNegOffset = jacksonHandleNegOffset.get();
    } catch (Exception ex) {
      str.append(ex.getMessage());
    }
    assertTrue("Exception message incorrect", str.toString().contains("Invalid arguments: offset must be a non-negative number: -2"));
  }

  /*
   * This test checks limit with positive value, negative value and zero. Should
   * return 3 items, null, 6 items and exception.
   */
  @Test
  public void testlimitValues() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testlimitVales method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", "myDetail");
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master", "myMaster");
    PlanColumn masterIdCol1 = p.viewCol("myDetail", "masterId");
    PlanColumn masterIdCol2 = p.viewCol("myMaster", "id");
    PlanColumn detailIdCol = p.viewCol("myDetail", "id");
    PlanColumn detailNameCol = p.viewCol("myDetail", "name");
    PlanColumn masterNameCol = p.viewCol("myMaster", "name");
    ModifyPlan plan3 = plan1.joinInner(plan2)
        .where(
            p.eq(masterIdCol1, masterIdCol2)
        )
        .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
        .orderBy(p.desc(detailNameCol))
        .offset(1)
        .limit(3);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 3 node returned.
    assertEquals("Three nodes not returned from testlimitValues method ", 3, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);
    assertEquals("Element 1 myMaster.id value incorrect", "1", first.path("myMaster.id").path("value").asText());
    assertEquals("Element 1 myMaster.name value incorrect", "Master 1", first.path("myMaster.name").path("value").asText());
    assertEquals("Element 1 myDetail.id value incorrect", "5", first.path("myDetail.id").path("value").asText());
    assertEquals("Element 1 myDetail.name value incorrect", "Detail 5", first.path("myDetail.name").path("value").asText());
    JsonNode second = jsonBindingsNodes.path(1);
    assertEquals("Element 2 myMaster.id value incorrect", "2", second.path("myMaster.id").path("value").asText());
    assertEquals("Element 2 myMaster.name value incorrect", "Master 2", second.path("myMaster.name").path("value").asText());
    assertEquals("Element 2 myDetail.id value incorrect", "4", second.path("myDetail.id").path("value").asText());
    assertEquals("Element 2 myDetail.name value incorrect", "Detail 4", second.path("myDetail.name").path("value").asText());

    JsonNode third = jsonBindingsNodes.path(2);
    assertEquals("Element 3 myMaster.id value incorrect", "1", third.path("myMaster.id").path("value").asText());
    assertEquals("Element 3 myMaster.name value incorrect", "Master 1", third.path("myMaster.name").path("value").asText());
    assertEquals("Element 3 myDetail.id value incorrect", "3", third.path("myDetail.id").path("value").asText());
    assertEquals("Element 3 myDetail.name value incorrect", "Detail 3", third.path("myDetail.name").path("value").asText());

    // Limit with large value
    ModifyPlan plan4 = plan1.joinInner(plan2)
        .where(
            p.eq(masterIdCol1, masterIdCol2)
        )
        .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
        .orderBy(p.desc(detailNameCol))
        .limit(10000);

    JacksonHandle jacksonHandleLarge = new JacksonHandle();
    jacksonHandleLarge.setMimetype("application/json");

    rowMgr.resultDoc(plan4, jacksonHandleLarge);
    JsonNode jsonResultsLarge = jacksonHandleLarge.get();
    JsonNode jsonBindingsNodesLarge = jsonResultsLarge.path("rows");
    // Should have 6 node returned.
    assertEquals("Six nodes not returned from testlimitValues method ", 6, jsonBindingsNodesLarge.size());

    // Limit with 0 value
    StringBuilder strZ = new StringBuilder();
    try {
      // Throw exception
      ModifyPlan plan5 = plan1.joinInner(plan2)
          .where(
              p.eq(masterIdCol1, masterIdCol2)
          )
          .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
          .orderBy(p.desc(detailNameCol))
          .limit(0);

      JacksonHandle jacksonHandleZ = new JacksonHandle();
      jacksonHandleZ.setMimetype("application/json");

      rowMgr.resultDoc(plan5, jacksonHandleZ);
      JsonNode jsonResultsZ = jacksonHandleLarge.get();
      JsonNode jsonBindingsNodesZ = jsonResultsZ.path("rows");
    } catch (Exception ex) {
      strZ.append(ex.getMessage());
    }
    assertTrue("Exception message incorrect", strZ.toString().contains("Invalid arguments: limit must be a positive number: 0"));

    // Limit with negative value
    StringBuilder strNeg = new StringBuilder();
    try {
      // Throw exception
      ModifyPlan plan6 = plan1.joinInner(plan2)
          .where(p.eq(masterIdCol1, masterIdCol2))
          .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
          .orderBy(p.desc(detailNameCol))
          .limit(-2);

      JacksonHandle jacksonHandleNeg = new JacksonHandle();
      jacksonHandleNeg.setMimetype("application/json");

      rowMgr.resultDoc(plan6, jacksonHandleNeg);
      JsonNode jsonResultsNeg = jacksonHandleNeg.get();
      JsonNode jsonBindingsNodesNeg = jsonResultsNeg.path("rows");
    } catch (Exception ex) {
      strNeg.append(ex.getMessage());
    }
    assertTrue("Exception message incorrect", strNeg.toString().contains("Invalid arguments: limit must be a positive number: -2"));
  }

  /*
   * This test checks joinInner() with where disctinct Should return 3 items.
   * Uses schemaCol and viewCol for Columns selection
   */
  @Test
  public void testjoinInnerWhereDisctinct() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testjoinInnerWhereDisctinct method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
        .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
        .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinInner(plan2)
        .where(
            p.eq(
                p.schemaCol("opticFunctionalTest", "master", "id"),
                p.col( "masterId")
                )
        )
        .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "master", "id")))
        .select(p.col( "color"))
        .whereDistinct()
        .orderBy(p.desc(p.col( "color")));
    // Using Jackson to traverse the list.
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 2 nodes returned.
    assertEquals("Twelve nodes not returned from testjoinInnerWhereDisctinct method ", 2, jsonBindingsNodes.size());
    assertEquals("Detail.color first node value incorrect", "green", jsonBindingsNodes.get(0).path("opticFunctionalTest.detail.color").path("value").asText());
    assertEquals("Detail.color second node value incorrect", "blue", jsonBindingsNodes.get(1).path("opticFunctionalTest.detail.color").path("value").asText());
  }

  /*
   * This test checks joinLeftOuter with a view. Should return 12 items. Uses
   * schemaCol and viewCol for Columns selection Processing Each Row As a
   * Separate JSON
   */
  @SuppressWarnings("deprecation")
  @Test
  public void testJoinLeftOuter() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testJoinLeftOuter method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
        .orderBy(p.col( "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
        .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinLeftOuter(plan2)
        .select(
            p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
            p.schemaCol("opticFunctionalTest", "master", "date"),
            p.as("DetailName",p.schemaCol("opticFunctionalTest", "detail", "name")),
            p.col( "amount"),
            p.col( "color")
        )
        .orderBy(p.sortKeySeq(p.desc(p.col("DetailName")), p.desc(p.schemaCol("opticFunctionalTest", "master", "date"))));

    RowSet<RowRecord> rowSet = rowMgr.resultRows(plan3);
    // Verify RowRecords using Iterator
    Iterator<RowRecord> rowItr = rowSet.iterator();

    RowRecord record = null;
    if (rowItr.hasNext()) {
      record = rowItr.next();
      assertEquals("Date from RowSet Iterator first node value incorrect", "2015-12-02", record.getString("opticFunctionalTest.master.date"));

      assertEquals("Master Name RowSet Iterator first node value incorrect", "Master 2", record.getString("MasterName"));
      assertEquals("Detail Name RowSet Iterator first node value incorrect", "Detail 6", record.getString("DetailName"));
      assertEquals("Color Name RowSet Iterator first node value incorrect", "green", record.getString("color"));
      assertEquals(60.06, record.getDouble("opticFunctionalTest.detail.amount"), 0.00);
    }
    else {
      fail("Could not traverse Iterator<RowRecord> in testJoinLeftOuter method");
    }

    // Using Jackson to traverse the list.
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 12 nodes returned.
    assertEquals("Twelve nodes not returned from testJoinLeftOuter method ", 12, jsonBindingsNodes.size());
    // Verify first node
    assertEquals("Element 1 MasterName value incorrect", "Master 2", jsonBindingsNodes.get(0).path("MasterName").path("value").asText());
    assertEquals("Element 1 Master Date value incorrect", "2015-12-02", jsonBindingsNodes.get(0).path("opticFunctionalTest.master.date").path("value").asText());
    assertEquals("Element 1 DetailName value incorrect", "Detail 6", jsonBindingsNodes.get(0).path("DetailName").path("value").asText());
    assertEquals(60.06, jsonBindingsNodes.get(0).path("opticFunctionalTest.detail.amount").path("value").asDouble(), 0.00d);
    assertEquals("Element 1 Detail Color value incorrect", "green", jsonBindingsNodes.get(0).path("opticFunctionalTest.detail.color").path("value").asText());

    // Verify twelveth node
    assertEquals("Element 12 MasterName value incorrect", "Master 1", jsonBindingsNodes.get(11).path("MasterName").path("value").asText());
    assertEquals("Element 12 Master Date value incorrect", "2015-12-01", jsonBindingsNodes.get(11).path("opticFunctionalTest.master.date").path("value").asText());
    assertEquals("Element 12 DetailName value incorrect", "Detail 1", jsonBindingsNodes.get(11).path("DetailName").path("value").asText());
    assertEquals(10.01, jsonBindingsNodes.get(11).path("opticFunctionalTest.detail.amount").path("value").asDouble(), 0.00d);
    assertEquals("Element 12 Detail Color value incorrect", "blue", jsonBindingsNodes.get(11).path("opticFunctionalTest.detail.color").path("value").asText());
  }

  /*
   * This test checks union and except with a view. Should return 8 items. Uses
   * schemaCol for Columns selection
   */
  @Test
  public void testUnion() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testUnion method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 =
        p.fromView("opticFunctionalTest", "master");
    ModifyPlan plan2 =
        p.fromView("opticFunctionalTest2", "master");
    ModifyPlan plan3 =
        p.fromView("opticFunctionalTest2", "detail");
    ModifyPlan plan4 =
        p.fromView("opticFunctionalTest", "detail");
    ModifyPlan output =
        plan3.select(p.as("unionId", p.schemaCol("opticFunctionalTest2", "detail", "id")))
            .union(plan4.select(p.as("unionId", p.col( "id"))))
            .except(
                plan1.select(p.as("unionId", p.schemaCol("opticFunctionalTest", "master", "id")))
                    .union(plan2.select(p.as("unionId", p.schemaCol("opticFunctionalTest2", "master", "id"))))
            )
            .orderBy(p.col("unionId"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 8 nodes returned.
    assertEquals("Eight nodes not returned from testUnion method ", 8, jsonBindingsNodes.size());
    assertEquals("Element 1 union id value incorrect", "5", jsonBindingsNodes.get(0).path("unionId").path("value").asText());
    assertEquals("Element 1 union id value incorrect", "6", jsonBindingsNodes.get(1).path("unionId").path("value").asText());
    assertEquals("Element 1 union id value incorrect", "7", jsonBindingsNodes.get(2).path("unionId").path("value").asText());
    assertEquals("Element 1 union id value incorrect", "8", jsonBindingsNodes.get(3).path("unionId").path("value").asText());
    assertEquals("Element 1 union id value incorrect", "9", jsonBindingsNodes.get(4).path("unionId").path("value").asText());

    assertEquals("Element 1 union id value incorrect", "12", jsonBindingsNodes.get(7).path("unionId").path("value").asText());
  }

  /*
   * This test checks intersect on different schemas with a view. Should return
   * 4 items. Uses schemaCol for Columns selection Methods used - intersect
   */
  @Test
  public void testIntersectDiffSchemas() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testIntersectDiffSchemas method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "master");
    ModifyPlan plan2 = p.fromView("opticFunctionalTest2", "master");
    ModifyPlan plan3 = p.fromView("opticFunctionalTest", "detail");

    ModifyPlan plan4 = plan1.select(p.as("unionId", p.schemaCol("opticFunctionalTest", "master", "id")))
        .union(plan2.select(p.as("unionId", p.schemaCol("opticFunctionalTest2", "master", "id"))))
        .intersect(
            plan3.select(p.as("unionId", p.col( "id")))
        )
        .orderBy(p.col("unionId"));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan4, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 4 nodes returned.
    assertEquals("Four nodes not returned from testIntersectDiffSchemas method ", 4, jsonBindingsNodes.size());
    assertEquals("Element 1 union id value incorrect", "1", jsonBindingsNodes.get(0).path("unionId").path("value").asText());
    assertEquals("Element 1 union id value incorrect", "2", jsonBindingsNodes.get(1).path("unionId").path("value").asText());
    assertEquals("Element 1 union id value incorrect", "3", jsonBindingsNodes.get(2).path("unionId").path("value").asText());
    assertEquals("Element 1 union id value incorrect", "4", jsonBindingsNodes.get(3).path("unionId").path("value").asText());
  }

  /*
   * This test checks arithmetic operations with a view. Should return 6 items.
   * Uses schemaCol for Columns selection Math fns - add, subtract, modulo,
   * divide, multiply
   */
  @Test
  public void testArithmeticOperations() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testArithmeticOperations method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
        .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
        .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinInner(plan2)
        .where(p.eq(p.schemaCol("opticFunctionalTest", "master", "id"),
            p.col( "masterId"))
        )
        .select(p.as("added", p.add(p.col("amount"), p.col( "masterId"))),
            p.as("substracted", p.subtract(p.col("amount"), p.viewCol("master", "id"))),
            p.as("modulo", p.modulo(p.col("amount"), p.viewCol("master", "id"))),
            p.as("invSubstract", p.subtract(p.col("amount"), p.viewCol("master", "id"))),
            p.as("divided", p.divide(p.col("amount"), p.multiply(p.col("amount"), p.viewCol("detail", "id"))))
        )
        .orderBy(p.asc("substracted"));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 6 nodes returned.
    assertEquals("Six nodes not returned from testArithmeticOperations method ", 6, jsonBindingsNodes.size());
    // Verify first node
    assertEquals(11.01, jsonBindingsNodes.get(0).path("added").path("value").asDouble(), 0.00d);
    assertEquals(9.01, jsonBindingsNodes.get(0).path("substracted").path("value").asDouble(), 0.00d);
    assertEquals(0.00999999999999979, jsonBindingsNodes.get(0).path("modulo").path("value").asDouble(), 0.00d);
    assertEquals(1, jsonBindingsNodes.get(0).path("divided").path("value").asDouble(), 0.00d);
    // Verify sixth node
    assertEquals(62.06, jsonBindingsNodes.get(5).path("added").path("value").asDouble(), 0.00d);
    assertEquals(58.06, jsonBindingsNodes.get(5).path("substracted").path("value").asDouble(), 0.00d);
    assertEquals(0.0600000000000023, jsonBindingsNodes.get(5).path("modulo").path("value").asDouble(), 0.00d);
    assertEquals(0.166666666666667, jsonBindingsNodes.get(5).path("divided").path("value").asDouble(), 0.00d);
  }

  /*
   * This test checks MarkLogic buit-in function with a view. Should return 3
   * items. Uses schemaCol for Columns selection Math fns - median
   */
  @Test
  public void testBuiltinFuncs() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testBuiltinFuncs method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
        .orderBy(p.col( "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
        .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinInner(plan2)
        .where(p.gt(
            p.col( "amount"), p.math.median(p.xs.doubleSeq(10.0, 40.0, 50.0, 30.0, 60.0, 0.0, 100.0))
            )
        )
        .select(p.as("myAmount", p.viewCol("detail", "amount")))
        .whereDistinct()
        .orderBy(p.asc("myAmount"));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");

    // Should have 3 nodes returned.
    assertEquals("Three nodes not returned from testBuiltinFuncs method ", 3, jsonBindingsNodes.size());
    // Verify nodes
    assertEquals(40.04, jsonBindingsNodes.get(0).path("myAmount").path("value").asDouble(), 0.00d);
    assertEquals(50.05, jsonBindingsNodes.get(1).path("myAmount").path("value").asDouble(), 0.00d);
    assertEquals(60.06, jsonBindingsNodes.get(2).path("myAmount").path("value").asDouble(), 0.00d);
  }

  /*
   * This test checks inner join with accessor plan and on, diff data types.
   * Should return 3 items or exceptions.
   */
  @Test
  public void testjoinInnerWithDataTypes() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testjoinInnerWithDataTypes method");
    StringBuilder str = new StringBuilder();

    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", "myDetail");

    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master", "myMaster");
    PlanColumn masterIdCol1 = p.viewCol("myDetail", "masterId");
    PlanColumn masterIdCol2 = p.viewCol("myMaster", "id");
    PlanColumn detailIdCol = p.viewCol("myDetail", "id");
    PlanColumn detailNameCol = p.viewCol("myDetail", "name");
    PlanColumn masterNameCol = p.viewCol("myMaster", "name");
    // Pass a double
    ModifyPlan plan3 = plan1.joinInner(plan2, p.on(masterIdCol1, masterIdCol2), p.ge(detailIdCol, p.xs.doubleVal(3.0)))
        .where(p.eq(masterIdCol1, masterIdCol2))
        .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
        .orderBy(p.desc(detailNameCol))
        .offsetLimit(1, 100);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 3 nodes returned.
    assertEquals("Three nodes not returned from testjoinInnerWithDataTypes method ", 3, jsonBindingsNodes.size());

    // Pass a String
    ModifyPlan plan4 = plan1.joinInner(plan2, p.on(masterIdCol1, masterIdCol2), p.ge(detailIdCol, p.xs.string("3")))
        .where(p.eq(masterIdCol1, masterIdCol2))
        .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
        .orderBy(p.desc(detailNameCol))
        .offsetLimit(1, 100);

    JacksonHandle jacksonHandleStr = new JacksonHandle();
    jacksonHandleStr.setMimetype("application/json");

    rowMgr.resultDoc(plan4, jacksonHandleStr);
    JsonNode jsonResultsStr = jacksonHandleStr.get();
    // Should have 3 nodes returned.
    assertEquals("Three nodes not returned from testjoinInnerWithDataTypes method ", 3, jsonResultsStr.path("rows").size());

    // Pass as a date
    try {
      ModifyPlan plan5 = plan1.joinInner(plan2, p.on(masterIdCol1, masterIdCol2), p.ge(detailIdCol, p.xs.date("3")))
          .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
          .orderBy(p.desc(detailNameCol))
          .offsetLimit(1, 100);

      JacksonHandle jacksonHandleDate = new JacksonHandle();
      jacksonHandleDate.setMimetype("application/json");

      rowMgr.resultDoc(plan5, jacksonHandleDate);
      JsonNode jsonResultsDate = jacksonHandleDate.get();
    } catch (Exception ex) {
      str.append(ex.toString());
    }
    assertTrue("Exception Message incorrect", str.toString().contains("java.lang.IllegalArgumentException: 3"));

    // Pass as a decimal

    ModifyPlan plan6 = plan1.joinInner(plan2, p.on(masterIdCol1, masterIdCol2), p.ge(detailIdCol, p.xs.decimal(3.0)))
        .where(p.eq(masterIdCol1, masterIdCol2))
        .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
        .orderBy(p.desc(detailNameCol))
        .offsetLimit(1, 100);

    JacksonHandle jacksonHandleDecimal = new JacksonHandle();
    jacksonHandleDecimal.setMimetype("application/json");

    rowMgr.resultDoc(plan6, jacksonHandleDecimal);
    JsonNode jsonResultsDecimal = jacksonHandleDecimal.get();
    JsonNode jsonBindingsNodesDecimal = jsonResultsDecimal.path("rows");
    // Should have 3 nodes returned.
    assertEquals("Three nodes not returned from testjoinInnerWithDataTypes method ", 3, jsonBindingsNodesDecimal.size());
  }

  /*
   * This test checks plan builder with invlid Schema and view names.
   */
  @Test
  public void testinValidNamedSchemaView() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testinValidNamedSchemaView method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    // Verify for invalid schema name
    AccessPlan planInvalidSchema = p.fromView("opticFunctionalTestInvalid", "detail", "MarkLogicQAQualifier");
    planInvalidSchema.orderBy(p.sortKeySeq(p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.id")));

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    String exceptionSch = null;
    try {
      rowMgr.resultDoc(planInvalidSchema, jacksonHandle);
    } catch (Exception ex) {
      exceptionSch = ex.getMessage();
      System.out.println("Exception message is " + exceptionSch.toString());
    }
    assertTrue("Exception not thrown or invalid message",
        exceptionSch.contains("SQL-TABLENOTFOUND: plan.view(\"opticFunctionalTestInvalid\", \"detail\", null, \"MarkLogicQAQualifier\")") &&
            exceptionSch.contains("Unknown table: Table 'opticFunctionalTestInvalid.detail' not found"));

    // Verify for invalid view name
    AccessPlan planInvalidView = p.fromView("opticFunctionalTest", "detailInvalid", "MarkLogicQAQualifier");
    planInvalidView.orderBy(p.sortKeySeq(p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.id")));

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    String exceptionVw = null;
    try {
      rowMgr.resultDoc(planInvalidView, jacksonHandle);
    } catch (Exception ex) {
      exceptionVw = ex.getMessage();
      System.out.println("Exception message is " + exceptionVw.toString());
    }
    assertTrue("Exception not thrown or invalid message",
        exceptionVw.contains("SQL-TABLENOTFOUND: plan.view(\"opticFunctionalTest\", \"detailInvalid\", null, \"MarkLogicQAQualifier\")") &&
            exceptionVw.contains("Unknown table: Table 'opticFunctionalTest.detailInvalid' not found"));

    // Verify for empty view name
    AccessPlan planEmptyView = p.fromView("opticFunctionalTest", "", "MarkLogicQAQualifier");
    planInvalidView.orderBy(p.sortKeySeq(p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.id")));

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    String exceptionNoView = null;
    try {
      rowMgr.resultDoc(planEmptyView, jacksonHandle);
    } catch (Exception ex) {
      exceptionNoView = ex.getMessage();
      System.out.println("Exception message is " + exceptionNoView.toString());
    }
    assertTrue("Exception not thrown or invalid message", exceptionNoView.contains("OPTIC-INVALARGS") &&
        exceptionNoView.contains("Invalid arguments: cannot specify fromView() without view name"));

    // Verify for empty Schma name
    AccessPlan planEmptySch = p.fromView("", "detail", "MarkLogicQAQualifier");
    planInvalidView.orderBy(p.sortKeySeq(p.viewCol("opticFunctionalTest", "MarkLogicQAQualifier.id")));

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    String exceptionNoySch = null;
    try {
      rowMgr.resultDoc(planEmptySch, jacksonHandle);
    } catch (Exception ex) {
      exceptionNoySch = ex.getMessage();
      System.out.println("Exception message is " + exceptionNoySch.toString());
    }
    assertTrue("Exception not thrown or invalid message", exceptionNoySch.contains("OPTIC-INVALARGS") &&
        exceptionNoySch.contains("Invalid arguments: cannot specify fromView() with invalid schema name"));
  }

  /*
   * This test checks select ambiguous columns. Should return exceptions.
   */
  @Test
  public void testselectAmbiguousColumns() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testselectAmbiguousColumns method");
    StringBuilder str = new StringBuilder();
    try {
      RowManager rowMgr = client.newRowManager();
      PlanBuilder p = rowMgr.newPlanBuilder();
      ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail");
      ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master");
      ModifyPlan plan3 = plan1.joinInner(plan2)
          .where(
              p.eq(
                  p.schemaCol("opticFunctionalTest", "master", "id"),
                  p.col( "masterId")
                  )
          )
          .orderBy(p.asc(p.col( "id")))
          .select(p.colSeq("id"));

      JacksonHandle jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(plan3, jacksonHandle);
    } catch (Exception ex) {
      str.append(ex.getMessage());
      System.out.println("Exception message is " + str.toString());
    }
    // Should have SQL-AMBCOLUMN exceptions.
    assertTrue("Exceptions not found", str.toString().contains("SQL-AMBCOLUMN"));
    assertTrue("Exceptions not found", str.toString().contains("Ambiguous column reference: found opticFunctionalTest.master.id and opticFunctionalTest.detail.id"));
  }

  /*
   * This test checks invalid schema name, view name and column name on
   * schemaCol. Should return exceptions.
   */
  @Test
  public void testinValidSchemaViewColOnSchemaCol() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testinValidSchemaViewColOnSchemaCol method");
    StringBuilder strSchema = new StringBuilder();
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail");
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master");
    try {
      // Invalid schema name on schemaCol
      ModifyPlan plan3 = plan1.joinInner(plan2)
          .where(
              p.eq(
                  p.schemaCol("opticFunctionalTest", "master", "id"),
                  p.col( "masterId")
                  )
          )
          .orderBy(p.asc(p.schemaCol("opticFunctionalTest_invalid", "detail", "id")));

      JacksonHandle jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(plan3, jacksonHandle);
    } catch (Exception ex) {
      strSchema.append(ex.getMessage());
      System.out.println("Exception message is " + strSchema.toString());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue("Exceptions not found", strSchema.toString().contains("SQL-NOCOLUMN"));
    assertTrue("Exceptions not found", strSchema.toString().contains("Column not found: opticFunctionalTest_invalid.detail.id"));

    // Invalid View name on schemaCol
    StringBuilder strView = new StringBuilder();
    try {
      ModifyPlan plan4 = plan1.joinInner(plan2)
          .where(
              p.eq(
                  p.schemaCol("opticFunctionalTest", "master", "id"),
                  p.col( "masterId")
                  )
          )
          .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail_invalid", "id")));

      JacksonHandle jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(plan4, jacksonHandle);
    } catch (Exception ex) {
      strView.append(ex.getMessage());
      System.out.println("Exception message is " + strView.toString());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue("Exceptions not found", strView.toString().contains("SQL-NOCOLUMN"));
    assertTrue("Exceptions not found", strView.toString().contains("Column not found: opticFunctionalTest.detail_invalid.id"));

    // Invalid Column name on schemaCol
    StringBuilder strCol = new StringBuilder();
    try {
      ModifyPlan plan5 = plan1.joinInner(plan2)
          .where(
              p.eq(
                  p.schemaCol("opticFunctionalTest", "master", "id"),
                  p.col( "masterId")
                  )
          )
          .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail", "id_invalid")));

      JacksonHandle jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(plan5, jacksonHandle);
    } catch (Exception ex) {
      strCol.append(ex.getMessage());
      System.out.println("Exception message is " + strCol.toString());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue("Exceptions not found", strCol.toString().contains("SQL-NOCOLUMN"));
    assertTrue("Exceptions not found", strCol.toString().contains("Column not found: opticFunctionalTest.detail.id_invalid"));

    // Invalid column in where
    StringBuilder strWhereCol = new StringBuilder();
    try {
      ModifyPlan plan6 = plan1.joinInner(plan2)
          .where(
              p.eq(
                  p.schemaCol("opticFunctionalTest", "master", "id_invalid"),
                  p.col( "masterId")
                  )
          )
          .orderBy(p.asc(p.col( "id")));

      JacksonHandle jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(plan6, jacksonHandle);
    } catch (Exception ex) {
      strWhereCol.append(ex.getMessage());
      System.out.println("Exception message is " + strWhereCol.toString());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue("Exceptions not found", strWhereCol.toString().contains("SQL-NOCOLUMN"));
    assertTrue("Exceptions not found", strWhereCol.toString().contains("Column not found: opticFunctionalTest.master.id_invalid"));

    // Invalid column in viewCol
    StringBuilder strViewCol = new StringBuilder();
    try {
      ModifyPlan plan7 = plan1.joinInner(plan2)
          .where(
              p.eq(
                  p.schemaCol("opticFunctionalTest", "master", "id"),
                  p.col( "masterId")
                  )
          )
          .orderBy(p.asc(p.viewCol("detail_invalid", "id")));
      JacksonHandle jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");
      rowMgr.resultDoc(plan7, jacksonHandle);
    } catch (Exception ex) {
      strViewCol.append(ex.getMessage());
      System.out.println("Exception message is " + strViewCol.toString());
    }
    // Should have SQL-NOCOLUMN exceptions.
    assertTrue("Exceptions not found", strViewCol.toString().contains("SQL-NOCOLUMN"));
    assertTrue("Exceptions not found", strViewCol.toString().contains("Column not found: detail_invalid.id"));
  }

  /*
   * This test checks different number of columns. 1) intersect with different
   * number of columns 2) except with different number of columns Should return
   * exceptions.
   */
  @Test
  public void testDifferentColumns() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testDifferentColumns method");
    StringBuilder strIntersect = new StringBuilder();
    JacksonHandle jacksonHandle = null;

    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "master")
        .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "detail")
        .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    // intersect with different number of columns
    try {
      ModifyPlan plan3 = plan1.select(p.schemaCol("opticFunctionalTest", "master", "id"))
          .intersect(
              plan2.select(
            		  p.schemaCol("opticFunctionalTest", "detail", "id"),
                  p.schemaCol("opticFunctionalTest", "master", "masterId")
                  )
          )
          .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail", "id")));

      jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(plan3, jacksonHandle);
    } catch (Exception ex) {
      strIntersect.append(ex.getMessage());
      System.out.println("Exception message is " + strIntersect.toString());
    }
    JsonNode jsonBindingsNodes = jacksonHandle.get();
    assertTrue("Handle should be null from testDifferentColumns method ", jsonBindingsNodes == null);
    // except with different number of columns
    StringBuilder strExcept = new StringBuilder();
    try {
      ModifyPlan plan4 = plan1.select(
          p.schemaCol("opticFunctionalTest", "master", "id")
          )
          .except(
              plan2.select(
                  p.col( "id"),
                  p.col( "masterId")
                  )
          )
          .orderBy(p.asc(p.col("id")));
      jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(plan4, jacksonHandle);
    } catch (Exception ex) {
      strExcept.append(ex.getMessage());
      System.out.println("Exception message is " + strExcept.toString());
    }
    jsonBindingsNodes = jacksonHandle.get();
    assertEquals("Two nodes not returned from testFragmentId method ", 2, jsonBindingsNodes.size());
  }

  /*
   * Test explain plan on / with 1) Valid plan 2) Use of StringHandle 3) Invalid
   * plan
   */
  @Test
  public void testExplainPlan() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testExplainPlan method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanSystemColumn fIdCol1 = p.fragmentIdCol("fragIdCol1");
    PlanSystemColumn fIdCol2 = p.fragmentIdCol("fragIdCol2");

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", null, fIdCol1)
        .orderBy(p.col( "id"));

    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master", null, fIdCol2)
        .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));

    ModifyPlan output = plan1.joinInner(plan2)
        .where(
            p.eq(
                p.schemaCol("opticFunctionalTest", "master", "id"),
                p.col( "masterId")
                )
        )
        .select(
            p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
            p.schemaCol("opticFunctionalTest", "master", "date"),
            p.as("DetailName", p.col( "name")),
            p.col( "amount"),
            p.col( "color"),
            fIdCol1,
            fIdCol2
        )
        .orderBy(p.desc(p.col("DetailName")));
    JsonNode explainNode = rowMgr.explain(output, new JacksonHandle()).get();
    // Making sure explain() does not blow up for a valid plan.
    assertEquals("Explain of plan incorrect", explainNode.path("node").asText(), "plan");
    assertEquals("Explain of plan incorrect", explainNode.path("expr").path("columns").get(0).path("column").asText(), "DetailName");
    // Invalid string - Use txt instead of json or xml
    String explainNodetxt = rowMgr.explain(output, new StringHandle()).get();
    System.out.println(explainNodetxt);
    assertTrue("Explain of plan incorrect", explainNodetxt.contains("\"node\":\"plan\""));
    // Invalid Plan
    ModifyPlan plan3 = p.fromView("opticFunctionalTest", "master")
        .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan4 = p.fromView("opticFunctionalTest", "detail")
        .orderBy(p.col( "id"));
    // intersect with different number of columns
    JsonNode explainNodeInv = null;
    try {
      ModifyPlan outputInv = plan3.select(p.schemaCol("opticFunctionalTest", "master", "id"))
          .intersect(
              plan4.select(
                  p.col( "id"),
                  p.col( "masterId")
                  )
          )
          .orderBy(p.asc(p.col("id")));

      explainNodeInv = rowMgr.explain(outputInv, new JacksonHandle()).get();
      assertEquals("Explain of Invalid plan incorrect", explainNodeInv.path("node").asText(), "plan");
      assertTrue("Explain of Invalid plan incorrect", explainNodeInv.path("expr").path("dplan").asBoolean());
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      fail("Explain of Invalid plan has Exceptions");
    }
  }

  /*
   * Test on fromViews when fragment id is used
   */
  @Test
  public void testFragmentId() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testFragmentId method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanSystemColumn fIdCol1 = p.fragmentIdCol("fragIdCol1");
    PlanSystemColumn fIdCol2 = p.fragmentIdCol("fragIdCol2");

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", null, fIdCol1)
        .orderBy(p.col( "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master", null, fIdCol2)
        .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));

    ModifyPlan output = plan1.joinInner(plan2).where(
        p.eq(
            p.schemaCol("opticFunctionalTest", "master", "id"),
            p.col( "masterId")
            )
        )
        .select(
            p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
            p.schemaCol("opticFunctionalTest", "master", "date"),
            p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
            p.col( "amount"),
            p.col( "color"),
            fIdCol1,
            fIdCol2
        )
        .orderBy(p.desc(p.col("DetailName")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
    JsonNode node = jsonBindingsNodes.get(0);

    // Should have 6 nodes returned.
    assertEquals("Six nodes not returned from testFragmentId method ", 6, jsonBindingsNodes.size());
    // Verify nodes
    assertEquals("Element 1 MasterName value incorrect", "Master 2", node.path("MasterName").path("value").asText());
    assertEquals("Element 1 DetailName value incorrect", "Detail 6", node.path("DetailName").path("value").asText());
    assertEquals("Element 1 opticFunctionalTest.detail.amount value incorrect", "60.06", node.path("opticFunctionalTest.detail.amount").path("value").asText());
    node = jsonBindingsNodes.get(5);
    assertEquals("Element 6 MasterName value incorrect", "Master 1", node.path("MasterName").path("value").asText());
    assertEquals("Element 6 DetailName value incorrect", "Detail 1", node.path("DetailName").path("value").asText());
    assertEquals("Element 6 opticFunctionalTest.detail.amount value incorrect", "10.01", node.path("opticFunctionalTest.detail.amount").path("value").asText());
  }

  /*
   * This test checks the bind params on a where clause; params as PlanParamExpr. Should return 6 items.
   */
  @Test
  public void testjoinInnerWithBind() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testjoinInnerWithBind method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
        .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
        .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    PlanParamExpr idParam = p.param("ID");
    ModifyPlan plan3 = plan1.joinInner(plan2)
        .where(
            p.eq(p.schemaCol("opticFunctionalTest", "master", "id"), idParam)
        )
        .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail", "id")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3.bindParam(idParam, p.xs.intVal(1)), jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 6 nodes returned.
    assertEquals("Six nodes not returned from testjoinInnerWithBind method ", 6, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);

    assertEquals("Row 1 opticFunctionalTest.detail.id value incorrect", "1", first.path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals("Row 1 opticFunctionalTest.master.id value incorrect", "1", first.path("opticFunctionalTest.master.id").path("value").asText());
    assertEquals("Row 1 opticFunctionalTest.detail.masterId value incorrect", "1", first.path("opticFunctionalTest.detail.masterId").path("value").asText());
    assertEquals("Row 1 opticFunctionalTest.detail.name value incorrect", "Detail 1", first.path("opticFunctionalTest.detail.name").path("value").asText());

    // Verify sixth node.
    JsonNode sixth = jsonBindingsNodes.path(5);
    assertEquals("Row 6 opticFunctionalTest.detail.id value incorrect", "6", sixth.path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals("Row 6 opticFunctionalTest.master.id value incorrect", "1", sixth.path("opticFunctionalTest.master.id").path("value").asText());
    assertEquals("Row 6 opticFunctionalTest.detail.masterId value incorrect", "2", sixth.path("opticFunctionalTest.detail.masterId").path("value").asText());
    assertEquals("Row 6 opticFunctionalTest.detail.name value incorrect", "Detail 6", sixth.path("opticFunctionalTest.detail.name").path("value").asText());

    // Verify with negative value.
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(plan3.bindParam(idParam, -1), jacksonHandle);
    jsonResults = jacksonHandle.get();
    // Should have null returned.
    assertTrue("No nodes should returned. But found some.", jsonResults == null);

    // Verify with double value.
    PlanParamExpr amtParam = p.param("AMT");
    ModifyPlan planAmt = p.fromView("opticFunctionalTest", "detail")
        .where(p.gt(p.col( "amount"), amtParam)
        )
        .orderBy(p.col( "id"));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(planAmt.bindParam(amtParam, 10.1), jacksonHandle);
    jsonResults = jacksonHandle.get().path("rows");
    // Should have 5 rows returned.
    assertEquals("Five rows not returned from testjoinInnerWithBind method ", 5, jsonResults.size());

    assertEquals("Row 1 opticFunctionalTest.detail.amount value incorrect", "20.02", jsonResults.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals("Row 2 opticFunctionalTest.detail.amount value incorrect", "30.03", jsonResults.path(1).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals("Row 3 opticFunctionalTest.detail.amount value incorrect", "40.04", jsonResults.path(2).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals("Row 4 opticFunctionalTest.detail.amount value incorrect", "50.05", jsonResults.path(3).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals("Row 5 opticFunctionalTest.detail.amount value incorrect", "60.06", jsonResults.path(4).path("opticFunctionalTest.detail.amount").path("value").asText());

    // verify for Strings.
    PlanParamExpr detNameParam = p.param("DETAILNAME");
    ModifyPlan planStringBind = p.fromView("opticFunctionalTest", "detail")
        .where(p.eq(p.col( "name"), detNameParam)
        )
        .orderBy(p.col( "id"));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(planStringBind.bindParam(detNameParam, p.xs.string("Detail 6")), jacksonHandle);
    jsonResults = jacksonHandle.get().path("rows");
    assertEquals("One row not returned from testjoinInnerWithBind method ", 1, jsonResults.size());
    assertEquals("Row 1 opticFunctionalTest.detail.amount value incorrect", "60.06", jsonResults.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());

    // Verify with different types in multiple places.
    ModifyPlan planMultiBind = p.fromView("opticFunctionalTest", "detail")
        .where(p.and(p.eq(p.col( "name"), detNameParam),
            p.eq(p.col( "id"), idParam)
            )
        )
        .orderBy(p.col( "id"));

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(planMultiBind.bindParam(detNameParam, p.xs.string("Detail 6")).bindParam("ID", 6), jacksonHandle);
    jsonResults = jacksonHandle.get().path("rows");
    // Should have 1 node returned.
    assertEquals("One row not returned from testjoinInnerWithBind method ", 1, jsonResults.size());
    assertEquals("Row 1 opticFunctionalTest.detail.amount value incorrect", "60.06", jsonResults.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());
  }
  
  /*
   * This test checks the bind params on a where clause; params as String. Should return 6 items.
   */
  @Test
  public void testjoinInnerWithBindParamsAsString() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testjoinInnerWithBindParamsAsString method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
        .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
        .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    PlanParamExpr idParam = p.param("ID");
    ModifyPlan plan3 = plan1.joinInner(plan2)
        .where(
            p.eq(p.schemaCol("opticFunctionalTest", "master", "id"), idParam)
        )
        .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail", "id")));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(plan3.bindParam("ID", 1), jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    // Should have 6 nodes returned.
    assertEquals("Six nodes not returned from testjoinInnerWithBind method ", 6, jsonBindingsNodes.size());
    // Verify first node.
    JsonNode first = jsonBindingsNodes.path(0);

    assertEquals("Row 1 opticFunctionalTest.detail.id value incorrect", "1", first.path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals("Row 1 opticFunctionalTest.master.id value incorrect", "1", first.path("opticFunctionalTest.master.id").path("value").asText());
    assertEquals("Row 1 opticFunctionalTest.detail.masterId value incorrect", "1", first.path("opticFunctionalTest.detail.masterId").path("value").asText());
    assertEquals("Row 1 opticFunctionalTest.detail.name value incorrect", "Detail 1", first.path("opticFunctionalTest.detail.name").path("value").asText());

    // Verify sixth node.
    JsonNode sixth = jsonBindingsNodes.path(5);
    assertEquals("Row 6 opticFunctionalTest.detail.id value incorrect", "6", sixth.path("opticFunctionalTest.detail.id").path("value").asText());
    assertEquals("Row 6 opticFunctionalTest.master.id value incorrect", "1", sixth.path("opticFunctionalTest.master.id").path("value").asText());
    assertEquals("Row 6 opticFunctionalTest.detail.masterId value incorrect", "2", sixth.path("opticFunctionalTest.detail.masterId").path("value").asText());
    assertEquals("Row 6 opticFunctionalTest.detail.name value incorrect", "Detail 6", sixth.path("opticFunctionalTest.detail.name").path("value").asText());

    // Verify with negative value.
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(plan3.bindParam("ID", -1), jacksonHandle);
    jsonResults = jacksonHandle.get();
    // Should have null returned.
    assertTrue("No nodes should returned. But found some.", jsonResults == null);

    // Verify with double value.
    PlanParamExpr amtParam = p.param("AMT");
    ModifyPlan planAmt = p.fromView("opticFunctionalTest", "detail")
        .where(p.gt(p.col( "amount"), amtParam)
        )
        .orderBy(p.col( "id"));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(planAmt.bindParam("AMT", 10.1), jacksonHandle);
    jsonResults = jacksonHandle.get().path("rows");
    // Should have 5 rows returned.
    assertEquals("Five rows not returned from testjoinInnerWithBind method ", 5, jsonResults.size());

    assertEquals("Row 1 opticFunctionalTest.detail.amount value incorrect", "20.02", jsonResults.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals("Row 2 opticFunctionalTest.detail.amount value incorrect", "30.03", jsonResults.path(1).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals("Row 3 opticFunctionalTest.detail.amount value incorrect", "40.04", jsonResults.path(2).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals("Row 4 opticFunctionalTest.detail.amount value incorrect", "50.05", jsonResults.path(3).path("opticFunctionalTest.detail.amount").path("value").asText());
    assertEquals("Row 5 opticFunctionalTest.detail.amount value incorrect", "60.06", jsonResults.path(4).path("opticFunctionalTest.detail.amount").path("value").asText());

    // verify for Strings.
    PlanParamExpr detNameParam = p.param("DETAILNAME");
    ModifyPlan planStringBind = p.fromView("opticFunctionalTest", "detail")
        .where(p.eq(p.col( "name"), detNameParam)
        )
        .orderBy(p.col( "id"));
    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(planStringBind.bindParam(detNameParam, p.xs.string("Detail 6")), jacksonHandle);
    jsonResults = jacksonHandle.get().path("rows");
    assertEquals("One row not returned from testjoinInnerWithBind method ", 1, jsonResults.size());
    assertEquals("Row 1 opticFunctionalTest.detail.amount value incorrect", "60.06", jsonResults.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());

    // Verify with different types in multiple places.
    ModifyPlan planMultiBind = p.fromView("opticFunctionalTest", "detail")
        .where(p.and(p.eq(p.col( "name"), detNameParam),
            p.eq(p.col( "id"), idParam)
            )
        )
        .orderBy(p.col( "id"));

    jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(planMultiBind.bindParam(detNameParam, p.xs.string("Detail 6")).bindParam("ID", 6), jacksonHandle);
    jsonResults = jacksonHandle.get().path("rows");
    // Should have 1 node returned.
    assertEquals("One row not returned from testjoinInnerWithBind method ", 1, jsonResults.size());
    assertEquals("Row 1 opticFunctionalTest.detail.amount value incorrect", "60.06", jsonResults.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());
  }
  

  // Test row serialization as Object. Other tests handle array and Iterator
  @Test
  public void testRowRecordAsObject() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testRowRecordAsObject method");
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", "myDetail");
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master", "myMaster");
    PlanColumn masterIdCol1 = p.viewCol("myDetail", "masterId");
    PlanColumn masterIdCol2 = p.viewCol("myMaster", "id");
    PlanColumn detailIdCol = p.viewCol("myDetail", "id");
    PlanColumn detailNameCol = p.viewCol("myDetail", "name");
    PlanColumn masterNameCol = p.viewCol("myMaster", "name");
    ModifyPlan plan3 = plan1.joinInner(plan2)
        .where(
            p.eq(masterIdCol1, masterIdCol2)
        )
        .select(masterIdCol2, masterNameCol, detailIdCol, detailNameCol)
        .orderBy(p.desc(detailNameCol))
        .offset(1)
        .limit(1);

    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    RowSet<JsonNode> jsonResults = rowMgr.resultRowsAs(plan3, JsonNode.class);
    ArrayList<String> actual = new ArrayList<String>();
    ArrayList<String> expected = new ArrayList<String>();

    String[] colNames = jsonResults.getColumnNames();
    // Verify column names available in the object.
    for (String cols : colNames)
      actual.add(cols);
    Collections.sort(actual);

    expected.add("myMaster.id");
    expected.add("myMaster.name");
    expected.add("myDetail.id");
    expected.add("myDetail.name");
    Collections.sort(expected);

    assertTrue("Column names not equal from JsonNode.class", expected.equals(actual));

    Iterator<JsonNode> jsonRowItr = jsonResults.iterator();
    JsonNode first = null;
    if (jsonRowItr.hasNext()) {
      first = jsonRowItr.next();
      System.out.println("Row iterated using JsonNode.class" + first.toString());
    }
    else {
      fail("No JsonNodes available when JsonNode.class used");
    }
    assertEquals("Element 1 myMaster.id value incorrect", "1", first.path("myMaster.id").path("value").asText());
    assertEquals("Element 1 myMaster.name value incorrect", "Master 1", first.path("myMaster.name").path("value").asText());
    assertEquals("Element 1 myDetail.id value incorrect", "5", first.path("myDetail.id").path("value").asText());
    assertEquals("Element 1 myDetail.name value incorrect", "Detail 5", first.path("myDetail.name").path("value").asText());

    jsonResults.close();

    RowSet<Document> xmlResults = rowMgr.resultRowsAs(plan3, Document.class);

    ArrayList<String> actualXmlCol = new ArrayList<String>();
    String[] colNamesXML = xmlResults.getColumnNames();

    // Verify column names available in the object.
    for (String cols : colNamesXML)
      actualXmlCol.add(cols);
    Collections.sort(actualXmlCol);
    assertTrue("Column names not equal from Document.class", expected.equals(actualXmlCol));

    Iterator<Document> xmlRowItr = xmlResults.iterator();
    DOMHandle firstXML = new DOMHandle();
    Document firstXMLDoc = null;
    if (xmlRowItr.hasNext()) {
      firstXMLDoc = xmlRowItr.next();
      String rowContents = firstXML.with(firstXMLDoc).toString();
      System.out.println("Row iterated using Document.class" + rowContents);
      assertTrue("Row contents incorrect", rowContents.contains("<t:cell name=\"myMaster.id\" type=\"xs:integer\">1</t:cell>"));
      assertTrue("Row contents incorrect", rowContents.contains("<t:cell name=\"myMaster.name\" type=\"xs:string\">Master 1</t:cell>"));
      assertTrue("Row contents incorrect", rowContents.contains("<t:cell name=\"myDetail.id\" type=\"xs:integer\">5</t:cell>"));
      assertTrue("Row contents incorrect", rowContents.contains("<t:cell name=\"myDetail.name\" type=\"xs:string\">Detail 5</t:cell>"));
    }
    else {
      fail("No JsonNodes available when Document.class used");
    }
  }
  
//fromsql TEST 1 - join inner with keymatch
 @Test
 public void testFromSqljoinInnerWithkeymatch() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
 {
	  System.out.println("In testFromSqljoinInnerWithkeymatch method");
	  RowManager rowMgr = client.newRowManager();
	  PlanBuilder p = rowMgr.newPlanBuilder();
	  ModifyPlan plan1 = p.fromSql("SELECT * from opticFunctionalTest.detail");

	  ModifyPlan plan2 = p.fromSql("SELECT * from opticFunctionalTest.master");

	  ModifyPlan plan3 = plan1.joinInner(plan2)
			  .where(
					  p.eq(
							  p.schemaCol("opticFunctionalTest", "master" , "id"),
							  p.col( "masterId")
							  )
					  )
			  .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail" , "id")));
	  JacksonHandle jacksonHandle = new JacksonHandle();
	  jacksonHandle.setMimetype("application/json");

	  rowMgr.resultDoc(plan3, jacksonHandle);
	  JsonNode jsonResults = jacksonHandle.get();
	  jsonResults = jacksonHandle.get().path("rows");
	  // Should have 6 nodes returned.
	  assertEquals("Six rows not returned from testFromSqljoinInnerWithkeymatch method ", 6, jsonResults.size());
	  assertEquals("Row 1 opticFunctionalTest.detail.id value incorrect", 1, jsonResults.path(0).path("opticFunctionalTest.detail.id").path("value").asInt());
	  assertEquals("Row 1 opticFunctionalTest.master.id value incorrect", 1, jsonResults.path(0).path("opticFunctionalTest.master.id").path("value").asInt());
	  assertEquals("Row 1 opticFunctionalTest.detail.masterId value incorrect", 1, jsonResults.path(0).path("opticFunctionalTest.detail.masterId").path("value").asInt());
	  assertEquals("Row 1 opticFunctionalTest.detail.name value incorrect","Detail 1", jsonResults.path(0).path("opticFunctionalTest.detail.name").path("value").asText());
	  assertEquals("Row 6 opticFunctionalTest.detail.id value incorrect", 6, jsonResults.path(5).path("opticFunctionalTest.detail.id").path("value").asInt());
	  assertEquals("Row 6 opticFunctionalTest.master.id value incorrect", 2, jsonResults.path(5).path("opticFunctionalTest.master.id").path("value").asInt());
	  assertEquals("Row 6 opticFunctionalTest.detail.masterId value incorrect", 2, jsonResults.path(5).path("opticFunctionalTest.detail.masterId").path("value").asInt());
	  assertEquals("Row 6 opticFunctionalTest.detail.name value incorrect","Detail 6", jsonResults.path(5).path("opticFunctionalTest.detail.name").path("value").asText());
 }

 //fromsql TEST 2 - join inner with keymatch and select
 @Test
 public void testFromSqljoinInnerWithkeymatchSelect() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
 {
	  System.out.println("In testFromSqljoinInnerWithkeymatchSelect method");
	  RowManager rowMgr = client.newRowManager();
	  PlanBuilder p = rowMgr.newPlanBuilder();
	  ModifyPlan plan1 = p.fromSql("SELECT opticFunctionalTest.master.name AS MasterName, opticFunctionalTest.master.date, opticFunctionalTest.detail.name AS DetailName, opticFunctionalTest.detail.amount,  opticFunctionalTest.detail.color" + 
			  " FROM opticFunctionalTest.detail"  + 
			  " INNER JOIN opticFunctionalTest.master ON opticFunctionalTest.master.id = opticFunctionalTest.detail.masterId" + 
			  " ORDER BY DetailName DESC");

	  JacksonHandle jacksonHandle = new JacksonHandle();
	  jacksonHandle.setMimetype("application/json");

	  rowMgr.resultDoc(plan1, jacksonHandle);
	  JsonNode jsonResults = jacksonHandle.get();
	  jsonResults = jacksonHandle.get().path("rows");
	  // Should have 6 nodes returned.
	  assertEquals("Six rows not returned from testFromSqljoinInnerWithkeymatchSelect method ", 6, jsonResults.size());
	  assertEquals("Row 1 MasterName value incorrect", "Master 2", jsonResults.path(0).path("MasterName").path("value").asText());
	  assertEquals("Row 1 DetailName value incorrect", "Detail 6", jsonResults.path(0).path("DetailName").path("value").asText());
	  assertEquals("Row 1 opticFunctionalTest.detail.amount value incorrect", "60.06", jsonResults.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());
	  assertEquals("Row 6 MasterName value incorrect", "Master 1", jsonResults.path(5).path("MasterName").path("value").asText());
	  assertEquals("Row 6 DetailName value incorrect", "Detail 1", jsonResults.path(5).path("DetailName").path("value").asText());
	  assertEquals("Row 6 opticFunctionalTest.detail.color value incorrect", "blue", jsonResults.path(5).path("opticFunctionalTest.detail.color").path("value").asText());
 }

 //fromsql TEST 4 - sql group by
 @Test
 public void testFromSqlGroupBy() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
 {
	  System.out.println("In testFromSqlGroupBy method");
	  RowManager rowMgr = client.newRowManager();
	  PlanBuilder p = rowMgr.newPlanBuilder();
	  ModifyPlan plan1 = p.fromSql("SELECT opticFunctionalTest.master.name, opticFunctionalTest.detail.name AS DetailName, opticFunctionalTest.detail.color, SUM(amount) AS DetailSum " + 
			  "          FROM opticFunctionalTest.detail " + 
			  "          INNER JOIN opticFunctionalTest.master ON opticFunctionalTest.master.id = opticFunctionalTest.detail.masterId " + 
			  "          GROUP BY opticFunctionalTest.master.name " + 
			  "          ORDER BY DetailSum DESC");

	  JacksonHandle jacksonHandle = new JacksonHandle();
	  jacksonHandle.setMimetype("application/json");

	  rowMgr.resultDoc(plan1, jacksonHandle);
	  JsonNode jsonResults = jacksonHandle.get();
	  jsonResults = jacksonHandle.get().path("rows");
	  // Should have 2 nodes returned.
	  assertEquals("Two rows not returned from testFromSqlGroupBy method ", 2, jsonResults.size());
	  assertEquals("Row 1 opticFunctionalTest.master.name value incorrect", "Master 2", jsonResults.path(0).path("opticFunctionalTest.master.name").path("value").asText());
	  assertEquals("Row 1 DetailSum value incorrect", "120.12", jsonResults.path(0).path("DetailSum").path("value").asText());
	  assertEquals("Row 2 opticFunctionalTest.master.name value incorrect", "Master 1", jsonResults.path(1).path("opticFunctionalTest.master.name").path("value").asText());
	  assertEquals("Row 2 DetailSum value incorrect", "90.09", jsonResults.path(1).path("DetailSum").path("value").asText());
 }
 
 //fromsql TEST 8 - select with empty string qualifier and as
 @Test
 public void testFromSqlSelectEmptyAs() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
 {
	  System.out.println("In testFromSqlSelectEmptyAs method");
	  RowManager rowMgr = client.newRowManager();
	  PlanBuilder p = rowMgr.newPlanBuilder();
	  
	  PlanExprColSeq colSeqSel = p.colSeq(
			  p.schemaCol("opticFunctionalTest", "detail", "id"),
			  p.schemaCol("opticFunctionalTest", "detail", "color"),
             p.as("masterName", p.schemaCol("opticFunctionalTest", "master", "name"))
			  );
	 
	  ModifyPlan plan1 = p.fromSql("select * FROM opticFunctionalTest.detail INNER JOIN opticFunctionalTest.master WHERE opticFunctionalTest.detail.masterId = opticFunctionalTest.master.id")		     
			  	.select(colSeqSel)
		        .orderBy(p.asc(p.schemaCol("opticFunctionalTest", "detail", "id")));
		          
	  JacksonHandle jacksonHandle = new JacksonHandle();
	  jacksonHandle.setMimetype("application/json");

	  rowMgr.resultDoc(plan1, jacksonHandle);
	  JsonNode jsonResults = jacksonHandle.get();
	  jsonResults = jacksonHandle.get().path("rows");
	  // Should have 6 nodes returned.
	  assertEquals("Six rows not returned from testFromSqlSelectEmptyAs method ", 6, jsonResults.size());
	  assertEquals("Row 1 opticFunctionalTest.detail.id value incorrect", "1", jsonResults.path(0).path("opticFunctionalTest.detail.id").path("value").asText());
	  assertEquals("Row 1 opticFunctionalTest.detail.color value incorrect", "blue", jsonResults.path(0).path("opticFunctionalTest.detail.color").path("value").asText());
	  assertEquals("Row 1 masterName value incorrect", "Master 1", jsonResults.path(0).path("masterName").path("value").asText());
	  assertEquals("Row 5 opticFunctionalTest.detail.id value incorrect", "6", jsonResults.path(5).path("opticFunctionalTest.detail.id").path("value").asText());
	  assertEquals("Row 5 opticFunctionalTest.detail.color value incorrect", "green", jsonResults.path(5).path("opticFunctionalTest.detail.color").path("value").asText());
	  assertEquals("Row 5 masterName value incorrect", "Master 2", jsonResults.path(5).path("masterName").path("value").asText());
 }

//fromsql TEST 12 - arithmetic operations
 @Test
 public void testFromSqlArithmetic() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
 {
	  System.out.println("In testFromSqlArithmetic method");
	  RowManager rowMgr = client.newRowManager();
	  PlanBuilder p = rowMgr.newPlanBuilder();
	  
	  ModifyPlan plan1 = p.fromSql("SELECT "
	  		         + " (opticFunctionalTest.detail.amount + opticFunctionalTest.detail.masterId) AS added, "
			         + " (opticFunctionalTest.detail.amount - opticFunctionalTest.master.id) AS substracted, "
	  		         + " (opticFunctionalTest.detail.amount % opticFunctionalTest.master.id) AS modulo, "
			         + " (opticFunctionalTest.detail.amount / (opticFunctionalTest.detail.amount * opticFunctionalTest.detail.id)) AS divided "
			         + " FROM opticFunctionalTest.detail INNER JOIN opticFunctionalTest.master WHERE opticFunctionalTest.detail.masterId = opticFunctionalTest.master.id"
	  		         )
			  .orderBy(p.asc("substracted"));		      
	  JacksonHandle jacksonHandle = new JacksonHandle();
	  jacksonHandle.setMimetype("application/json");

	  rowMgr.resultDoc(plan1, jacksonHandle);
	  JsonNode jsonResults = jacksonHandle.get();
	  jsonResults = jacksonHandle.get().path("rows");
	  // Should have 6 nodes returned.
	  assertEquals("Six rows not returned from testFromSqlArithmetic method ", 6, jsonResults.size());
	  assertEquals("Row 1 added value incorrect", "11.01", jsonResults.path(0).path("added").path("value").asText());
	  assertEquals("Row 1 substracted value incorrect", "9.01", jsonResults.path(0).path("substracted").path("value").asText());
	  assertEquals("Row 1 modulo value incorrect", "0.00999999999999979", jsonResults.path(0).path("modulo").path("value").asText());
	  assertEquals("Row 1 divided value incorrect", "1", jsonResults.path(0).path("divided").path("value").asText());
	  assertEquals("Row 6 added value incorrect", "62.06", jsonResults.path(5).path("added").path("value").asText());
	  assertEquals("Row 6 substracted value incorrect", "58.06", jsonResults.path(5).path("substracted").path("value").asText());
	  assertEquals("Row 6 modulo value incorrect", "0.0600000000000023", jsonResults.path(5).path("modulo").path("value").asText());
	  assertEquals("Row 6 divided value incorrect", "0.166666666666667", jsonResults.path(5).path("divided").path("value").asText());
 }
 
//fromsql TEST 19 - sql between with sql condition
@Test
public void testFromSqlBetweenAndSqlCondition() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
{
	  System.out.println("In testFromSqlBetweenAndSqlCondition method");
	  RowManager rowMgr = client.newRowManager();
	  PlanBuilder p = rowMgr.newPlanBuilder();
	  
	  PlanExprColSeq colSeqSel = p.colSeq(
			  p.col("id"),
			  p.col("name"),
			  p.col("masterId"),
			  p.col("amount"),
			  p.col("color")		  
			  );
	  
	  ModifyPlan plan1 = p.fromSql("SELECT * FROM opticFunctionalTest.detail WHERE opticFunctionalTest.detail.amount BETWEEN 10 AND 40", "NewDetail")
			  			.where(p.sqlCondition("name <> 'Detail 1'"))
			  			.select(colSeqSel)
			  			.orderBy(p.asc("id"));		      
	  JacksonHandle jacksonHandle = new JacksonHandle();
	  jacksonHandle.setMimetype("application/json");

	  rowMgr.resultDoc(plan1, jacksonHandle);
	  JsonNode jsonResults = jacksonHandle.get();
	  jsonResults = jacksonHandle.get().path("rows");
	  // Should have 2 nodes returned.
	  assertEquals("Two rows not returned from testFromSqlBetweenAndSqlCondition method ", 2, jsonResults.size());
	  assertEquals("Row 1 NewDetail.amount value incorrect", "20.02", jsonResults.path(0).path("NewDetail.amount").path("value").asText());
	  assertEquals("Row 2 NewDetail.amount value incorrect", "30.03", jsonResults.path(1).path("NewDetail.amount").path("value").asText());
}

 
//fromsql TEST 27 - union with select, orderby, limit, and offset
 @Test
 public void testFromSqlUnionSelectOrderbyLimitOffset() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
 {
	  System.out.println("In testFromSqlUnionSelectOrderbyLimitOffset method");
	  RowManager rowMgr = client.newRowManager();
	  PlanBuilder p = rowMgr.newPlanBuilder();
	  
	  ModifyPlan plan1 = p.fromSql(
			  "SELECT opticFunctionalTest.detail.id, opticFunctionalTest.detail.name FROM opticFunctionalTest.detail ORDER BY name " 
					  + " UNION " 
					  + " SELECT opticFunctionalTest.master.id, opticFunctionalTest.master.name FROM opticFunctionalTest.master ORDER BY name"
			  )
			  .orderBy(p.desc("id"))
			  .limit(3)
			  .offset(1)
			  .select(p.as("myName", p.col("name")));		      
	  JacksonHandle jacksonHandle = new JacksonHandle();
	  jacksonHandle.setMimetype("application/json");

	  rowMgr.resultDoc(plan1, jacksonHandle);
	  JsonNode jsonResults = jacksonHandle.get();
	  jsonResults = jacksonHandle.get().path("rows");
	  // Should have 2 nodes returned.
	  assertEquals("Two rows not returned from testFromSqlUnionSelectOrderbyLimitOffset method ", 2, jsonResults.size());
	  assertEquals("Row 1 myName value incorrect", "Detail 5", jsonResults.path(0).path("myName").path("value").asText());
	  assertEquals("Row 2 myName value incorrect", "Detail 4", jsonResults.path(1).path("myName").path("value").asText());
 }
 
 
//fromsql TEST 28 - union with qualifier and offsetLimit
 @Test
 public void testFromSqlUnionOffsetLimit() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
 {
	  System.out.println("In testFromSqlUnionOffsetLimit method");
	  RowManager rowMgr = client.newRowManager();
	  PlanBuilder p = rowMgr.newPlanBuilder();
	  
	  ModifyPlan plan1 = p.fromSql(
			  "SELECT opticFunctionalTest.detail.id, opticFunctionalTest.detail.name FROM opticFunctionalTest.detail ORDER BY name " 
					  + " UNION " 
					  + " SELECT opticFunctionalTest.master.id, opticFunctionalTest.master.name FROM opticFunctionalTest.master ORDER BY name",
					  "myPlan"
			  )
			  .orderBy(p.desc("id"))
		      .offsetLimit(1, 3);		      
	  JacksonHandle jacksonHandle = new JacksonHandle();
	  jacksonHandle.setMimetype("application/json");

	  rowMgr.resultDoc(plan1, jacksonHandle);
	  JsonNode jsonResults = jacksonHandle.get();
	  jsonResults = jacksonHandle.get().path("rows");
	  // Should have 3 nodes returned.
	  assertEquals("Three rows not returned from testFromSqlUnionOffsetLimit method ", 3, jsonResults.size());
	  assertEquals("Row 1 myPlan value incorrect", "Detail 5", jsonResults.path(0).path("myPlan.name").path("value").asText());
	  assertEquals("Row 2 myPlan value incorrect", "Detail 4", jsonResults.path(1).path("myPlan.name").path("value").asText());
	  assertEquals("Row 3 myPlan value incorrect", "Detail 3", jsonResults.path(2).path("myPlan.name").path("value").asText());
 }
 
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    System.out.println("In tear down");
    // Delete the temp schema DB after resetting the Schema DB on content DB.
    // Else delete fails.
    deleteUserRole("opticRole");
    deleteRESTUser("opticUser");
    setDatabaseProperties(dbName, "schema-database", dbName);
    deleteDB(schemadbName);
    deleteForest(schemafNames[0]);
    // release client
    client.release();
    cleanupRESTServer(dbName, fNames);
  }
}
