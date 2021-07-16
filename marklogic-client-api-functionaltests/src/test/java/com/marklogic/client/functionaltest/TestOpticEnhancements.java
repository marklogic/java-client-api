/*
 * Copyright (c) 2019 MarkLogic Corporation
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.DigestAuthContext;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.AccessPlan;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.io.*;
import com.marklogic.client.row.RawQueryDSLPlan;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.type.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class TestOpticEnhancements extends BasicJavaClientREST {

  private static String dbName = "TestOpticOnEnhanceDB";
  private static String schemadbName = "TestOpticOnEnhanceSchemaDB";
  private static String[] fNames = {"TestOpticOnEnhanceDB-1"};
  private static String[] schemafNames = {"TestOpticOnEnhanceSchemaDB-1"};

  private static DatabaseClient client;
  private static String datasource = "src/test/java/com/marklogic/client/functionaltest/data/optics/";
  private static Map<String, Object>[] literals1 = new HashMap[10];
  private static Map<String, Object>[] literals2 = new HashMap[4];
  private static Map<String, Object>[] storeInformation = new HashMap[4];
  private static Map<String, Object>[] internetSales = new HashMap[4];

  @BeforeClass
  public static void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    System.out.println("In TestOpticEnhance setup");
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
          {"string", "", "city", "http://marklogic.com/collation/", "false", "reject"},
          {"int", "", "popularity", "", "false", "reject"},
          {"double", "", "distance", "", "false", "reject"},
          {"date", "", "date", "", "false", "reject"},
          {"string", "", "cityName", "http://marklogic.com/collation/", "false", "reject"},
          {"string", "", "cityTeam", "http://marklogic.com/collation/", "false", "reject"},
          {"long", "", "cityPopulation", "", "false", "reject"}
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
          "rest-reader", "rest-extension-user", "manage-user", "query-view-admin");

    if (IsSecurityEnabled()) {
      schemaDBclient = getDatabaseClientOnDatabase(getRestServerHostName(), getRestServerPort(), schemadbName, "opticUser", "0pt1c", getConnType());
      client = getDatabaseClient("opticUser", "0pt1c", getConnType());
    } else {
      schemaDBclient = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), schemadbName, new DigestAuthContext("opticUser", "0pt1c"));
      client = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), new DigestAuthContext("opticUser", "0pt1c"));
    }

  // Install the TDE templates into schemadbName DB
  // loadFileToDB(client, filename, docURI, collection, document format)
  loadFileToDB(schemaDBclient, "masterDetail.tdex", "/optic/view/test/masterDetail.tdex", "XML", new String[]{"http://marklogic.com/xdmp/tde"});
  loadFileToDB(schemaDBclient, "masterDetail2.tdej", "/optic/view/test/masterDetail2.tdej", "JSON", new String[]{"http://marklogic.com/xdmp/tde"});
  loadFileToDB(schemaDBclient, "masterDetail3.tdej", "/optic/view/test/masterDetail3.tdej", "JSON", new String[]{"http://marklogic.com/xdmp/tde"});

  // Load XML data files.
  loadFileToDB(client, "masterDetail.xml", "/optic/view/test/masterDetail.xml", "XML", new String[]{"/optic/view/test"});
  loadFileToDB(client, "playerTripleSet.xml", "/optic/triple/test/playerTripleSet.xml", "XML", new String[]{"/optic/player/triple/test"});
  loadFileToDB(client, "teamTripleSet.xml", "/optic/triple/test/teamTripleSet.xml", "XML", new String[]{"/optic/team/triple/test"});
  loadFileToDB(client, "otherPlayerTripleSet.xml", "/optic/triple/test/otherPlayerTripleSet.xml", "XML", new String[]{"/optic/other/player/triple/test"});
  loadFileToDB(client, "doc4.xml", "/optic/lexicon/test/doc4.xml", "XML", new String[]{"/optic/lexicon/test"});
  loadFileToDB(client, "doc5.xml", "/optic/lexicon/test/doc5.xml", "XML", new String[]{"/optic/lexicon/test"});

  // Load JSON data files.
  loadFileToDB(client, "masterDetail2.json", "/optic/view/test/masterDetail2.json", "JSON", new String[]{"/optic/view/test"});
  loadFileToDB(client, "masterDetail3.json", "/optic/view/test/masterDetail3.json", "JSON", new String[]{"/optic/view/test"});

  loadFileToDB(client, "doc1.json", "/optic/lexicon/test/doc1.json", "JSON", new String[]{"/other/coll1", "/other/coll2"});
  loadFileToDB(client, "doc2.json", "/optic/lexicon/test/doc2.json", "JSON", new String[]{"/optic/lexicon/test"});
  loadFileToDB(client, "doc3.json", "/optic/lexicon/test/doc3.json", "JSON", new String[]{"/optic/lexicon/test"});

  loadFileToDB(client, "city1.json", "/optic/lexicon/test/city1.json", "JSON", new String[]{"/optic/lexicon/test"});
  loadFileToDB(client, "city2.json", "/optic/lexicon/test/city2.json", "JSON", new String[]{"/optic/lexicon/test"});
  loadFileToDB(client, "city3.json", "/optic/lexicon/test/city3.json", "JSON", new String[]{"/optic/lexicon/test"});
  loadFileToDB(client, "city4.json", "/optic/lexicon/test/city4.json", "JSON", new String[]{"/optic/lexicon/test"});
  loadFileToDB(client, "city5.json", "/optic/lexicon/test/city5.json", "JSON", new String[]{"/optic/lexicon/test"});
  Thread.sleep(10000);

    schemaDBclient.release();

    Map<String, Object> row = new HashMap<>();

    row.put("rowId", 1);
    row.put("colorId", 1);
    row.put("desc", "ball");
    literals1[0] = row;

    row = new HashMap<>();
    row.put("rowId", 2);
    row.put("colorId", 2);
    row.put("desc", "square");
    literals1[1] = row;

    row = new HashMap<>();
    row.put("rowId", 3);
    row.put("colorId", 1);
    row.put("desc", "box");
    literals1[2] = row;

    row = new HashMap<>();
    row.put("rowId", 4);
    row.put("colorId", 1);
    row.put("desc", "hoop");
    literals1[3] = row;

    row = new HashMap<>();
    row.put("rowId", 5);
    row.put("colorId", 5);
    row.put("desc", "circle");
    literals1[4] = row;

    row = new HashMap<>();
    row.put("rowId", 6);
    row.put("colorId", 3);
    row.put("desc", "hOop");
    literals1[5] = row;

    row = new HashMap<>();
    row.put("rowId", 7);
    row.put("colorId", 2);
    row.put("desc", "hooP");
    literals1[6] = row;

    row = new HashMap<>();
    row.put("rowId", 8);
    row.put("colorId", 4);
    row.put("desc", "Mainframe");
    literals1[7] = row;

    row = new HashMap<>();
    row.put("rowId", 9);
    row.put("colorId", 5);
    row.put("desc", "JavaScript");
    literals1[8] = row;

    row = new HashMap<>();
    row.put("colorId", 1);
    row.put("colorDesc", "red");
    literals2[0] = row;

    row = new HashMap<>();
    row.put("colorId", 2);
    row.put("colorDesc", "blue");
    literals2[1] = row;

    row = new HashMap<>();
    row.put("colorId", 3);
    row.put("colorDesc", "black");
    literals2[2] = row;

    row = new HashMap<>();
    row.put("colorId", 4);
    row.put("colorDesc", "yellow");
    literals2[3] = row;

    // Fill up storeInformation Map
    row = new HashMap<>();
    row.put("storeName", "Los Angeles");
    row.put("sales", 1500);
    row.put("txnDate", "Jan-05-1999");
    storeInformation[0] = row;

    row = new HashMap<>();
    row.put("storeName", "San Diego");
    row.put("sales", 250);
    row.put("txnDate", "Jan-07-1999");
    storeInformation[1] = row;

    row = new HashMap<>();
    row.put("storeName", "Los Angeles");
    row.put("sales", 300);
    row.put("txnDate", "Jan-08-1999");
    storeInformation[2] = row;

    row = new HashMap<>();
    row.put("storeName", "Boston");
    row.put("sales", 700);
    row.put("txnDate", "Jan-08-1999");
    storeInformation[3] = row;

    // Fill up internetSales Map
    row = new HashMap<>();
    row.put("txnDate", "Jan-07-1999");
    row.put("sales", 250);
    row.put("sales", 1500);
    internetSales[0] = row;
    row = new HashMap<>();
    row.put("txnDate", "Jan-10-1999");
    row.put("sales", 535);
    row.put("storeName", "San Diego");
    internetSales[1] = row;
    row = new HashMap<>();
    row.put("txnDate", "Jan-11-1999");
    row.put("sales", 320);
    row.put("storeName", "Los Angeles");
    internetSales[2] = row;
    row = new HashMap<>();
    row.put("txnDate", "Jan-12-1999");
    row.put("sales", 750);
    row.put("storeName", "Boston");
    internetSales[3] = row;
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
          SAXException {
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
    @Test
    public void testRedactRegexJoinInner() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
    {
      System.out.println("In testRedactRegexJoinInner method");

      // Create a new Plan.
      RowManager rowMgr = client.newRowManager();
      PlanBuilder p = rowMgr.newPlanBuilder();

      Map<String,Object> hoops = new HashMap<>();
      hoops.put("pattern",     "(h)([A-Z])(op)");
      hoops.put("replacement", "h=$2=");
      Map<String,Object> mf = new HashMap<>();
      mf.put("pattern", "(Main)([a-z])(rame)");
      mf.put("replacement", "$3Obsolate$2$1");
      Map<String,Object> blackColor = new HashMap<>();
      blackColor.put("pattern",     "bl(ac)k");
      blackColor.put("replacement", "AC");
      Map<String,Object> redColor = new HashMap<>();
      redColor.put("pattern",     "red");
      redColor.put("replacement", "RED");

      // plans from literals
      ModifyPlan plan1 = p.fromLiterals(literals1);
      ModifyPlan plan2 = p.fromLiterals(literals2);
      ModifyPlan output = plan1.joinInner(plan2).orderBy(p.asc(p.col("rowId")))
          .bind(p.rdt.redactRegex(p.col("desc"), hoops))
          .bind(p.rdt.redactRegex(p.col("desc"), mf))
          .bind(p.rdt.redactRegex(p.col("colorDesc"), blackColor))
          .bind(p.rdt.redactRegex(p.col("colorDesc"), redColor));
;
      JacksonHandle jacksonHandle = new JacksonHandle();
      jacksonHandle.setMimetype("application/json");

      rowMgr.resultDoc(output, jacksonHandle);
      JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
      //System.out.println(jsonBindingsNodes);
      // Should have 7 nodes returned.
      assertEquals("Seven nodes not returned from testRedactRegexJoinInner method", 7, jsonBindingsNodes.size());
      JsonNode node = jsonBindingsNodes.path(0);
      assertEquals("Row 1 rowId value incorrect", "1", node.path("rowId").path("value").asText());
      assertEquals("Row 1 desc value incorrect", "ball", node.path("desc").path("value").asText());
      assertEquals("Row 1 colorDesc value incorrect", "RED", node.path("colorDesc").path("value").asText());
      node = jsonBindingsNodes.path(3);
      assertEquals("Row 4 rowId value incorrect", "4", node.path("rowId").path("value").asText());
      assertEquals("Row 4 desc value incorrect", "hoop", node.path("desc").path("value").asText());
      assertEquals("Row 4 colorDesc value incorrect", "RED", node.path("colorDesc").path("value").asText());
      node = jsonBindingsNodes.path(4);
      assertEquals("Row 5 colorDesc value incorrect", "h=O=", node.path("desc").path("value").asText());
      node = jsonBindingsNodes.path(6);
      assertEquals("Row 7 colorDesc value incorrect", "rameObsolatefMain", node.path("desc").path("value").asText());
    }

  @Test
  public void testRedactDatetime() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException {
    System.out.println("In testRedactDatetime method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    Map<String, Object>[] literals1 = new HashMap[2];
    Map<String, Object> row = new HashMap<>();

    row.put("id", 1);
    row.put("name", "Master 1");
    row.put("date", "2021-12-31T23:59:59");
    literals1[0] = row;

    row = new HashMap<>();
    row.put("id", 2);
    row.put("name", "Master 2");
    row.put("date", "2020-02-29T00:00:59");
    literals1[1] = row;

    Map<String,Object> dateOpts = new HashMap<>();
    dateOpts.put("level",   "parsed");
    dateOpts.put("picture", "[M01]/[D01]/[Y0001]");
    dateOpts.put("format",  "Month=[M01] Day=[D01]/xx [H01]:[m01]:[s01]");

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literals1);

    ModifyPlan output = plan1.orderBy(p.asc(p.col("id"))).bind(p.rdt.redactDatetime(p.col("date"), dateOpts));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
    //System.out.println(jsonBindingsNodes);
    assertEquals("Two node not returned from testRedactDatetime method", 2, jsonBindingsNodes.size());
    assertEquals("Row 1 id value incorrect", "1", jsonBindingsNodes.path(0).path("id").path("value").asText());
    assertEquals("Row 1 date value incorrect", "Month=05 Day=12/xx 00:00:00", jsonBindingsNodes.path(0).path("date").path("value").asText());
    assertEquals("Row 2 id value incorrect", "2", jsonBindingsNodes.path(1).path("id").path("value").asText());
    assertEquals("Row 2 date value incorrect", "Month=04 Day=02/xx 00:00:00", jsonBindingsNodes.path(1).path("date").path("value").asText());
  }

  @Test
  public void testMaskDeterministic() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException {
    System.out.println("In testMaskDeterministic method");

    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();

    Map<String, Object> detailOpts = new HashMap<>();
    detailOpts.put("character", "lowerCase");
    detailOpts.put("maxLength", 10);

    Map<String, Object> masterOpts = new HashMap<>();
    masterOpts.put("character", "mixedCaseNumeric");
    masterOpts.put("maxLength", 33);

    ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
            .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
    ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
            .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
    ModifyPlan plan3 = plan1.joinInner(plan2)
            .where(
                    p.eq(p.schemaCol("opticFunctionalTest", "master", "id"),
                            p.schemaCol("opticFunctionalTest", "detail", "masterId")
                    )
            )
            .select(
                    p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
                    p.schemaCol("opticFunctionalTest", "master", "date"),
                    p.as("DetailName", p.schemaCol("opticFunctionalTest", "detail", "name")),
                    p.schemaCol("opticFunctionalTest", "detail", "amount"),
                    p.schemaCol("opticFunctionalTest", "detail", "color")
            )
            .orderBy(p.asc(p.col("Amount")))
            .bind(p.rdt.maskDeterministic(p.col("DetailName"), detailOpts))
            .bind(p.rdt.maskDeterministic(p.col("MasterName"), masterOpts));
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(plan3, jacksonHandle);
    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
    //System.out.println(jsonBindingsNodes);

    Pattern patternMaster = Pattern.compile("[a-z][A-Z][0-9]");
    Pattern patternDetail = Pattern.compile("[a-z]");

    assertEquals("Six node not returned from testMaskDeterministic method", 6, jsonBindingsNodes.size());

    String rowOneMasterName = jsonBindingsNodes.path(0).path("MasterName").path("value").asText();
    String rowOneDetailName = jsonBindingsNodes.path(0).path("DetailName").path("value").asText();
    assertEquals("Row 1 masterName mask length incorrect", 33, rowOneMasterName.length());
    assertTrue("Row 1 masterName mask incorrect", patternMaster.matcher(rowOneMasterName).find());
    assertEquals("Row 1 detailName mask length incorrect", 10, rowOneDetailName.length());
    assertTrue("Row 1 detailName mask incorrect", patternDetail.matcher(rowOneDetailName).find());

    assertEquals("Row 1 amount incorrect", "10.01", jsonBindingsNodes.path(0).path("opticFunctionalTest.detail.amount").path("value").asText());
  }

  @Test
  public void testMaskRandom() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException {
    System.out.println("In testMaskRandom method");

    Map<String, Object> hoops = new HashMap<>();
    hoops.put("character", "mixedCase");
    hoops.put("maxLength", 10);

    Map<String, Object> colorOpts = new HashMap<>();
    colorOpts.put("character", "lowerCase");
    colorOpts.put("maxLength", 20);
    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    JacksonHandle jacksonHandle = new JacksonHandle();

    // plans from literals
    ModifyPlan plan1 = p.fromLiterals(literals1);
    ModifyPlan plan2 = p.fromLiterals(literals2);
    ModifyPlan output = plan1.joinInner(plan2).orderBy(p.asc(p.col("rowId")))
            .bind(p.rdt.maskRandom(p.col("desc"), hoops))
            .bind(p.rdt.maskRandom(p.col("colorId"), colorOpts));
    rowMgr.resultDoc(output, jacksonHandle);

    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
    //System.out.println(jsonBindingsNodes);

    Pattern patternDesc = Pattern.compile("[a-z][A-Z]");
    Pattern patternColorId = Pattern.compile("[a-z]");

    // Should have 7 nodes returned.
    assertEquals("Seven nodes not returned from testRedactRegexJoinInner method", 7, jsonBindingsNodes.size());
    JsonNode node = jsonBindingsNodes.path(0);
    String rowOneDescName = jsonBindingsNodes.path(0).path("desc").path("value").asText();
    assertTrue("Row 1 desc mask incorrect", patternDesc.matcher(rowOneDescName).find());

    node = jsonBindingsNodes.path(3);
    String rowFourcolorId = jsonBindingsNodes.path(0).path("colorId").path("value").asText();
    assertTrue("Row 4 color Id mask incorrect", patternColorId.matcher(rowFourcolorId).find());
  }

  @Test
  public void testRedactNumber() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
  {
    System.out.println("In testRedactNumber method");
    // Create a new Plan.
    RowManager rowMgr = client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanPrefixer players = p.prefixer("http://marklogic.com/baseball/players");
    PlanPrefixer team = p.prefixer("http://marklogic.com/mlb/team/");

    PlanColumn playerAgeCol = p.col("player_age");
    PlanColumn playerIdCol = p.col("player_id");
    PlanColumn playerNameCol = p.col("player_name");
    PlanColumn playerTeamCol = p.col("player_team");
    PlanColumn teamIdCol = p.col("team_id");
    PlanColumn teamNameCol = p.col("team_name");
    PlanColumn teamCityCol = p.col("team_city");

    Map<String, Object> numbOpts = new HashMap<>();
    numbOpts.put("character", "mixedCase");
    numbOpts.put("min", 1000);
    numbOpts.put("max", 100000);
    numbOpts.put("format", "000,000");

    Map<String, Object> alphaOpts = new HashMap<>();
    alphaOpts.put("character", "lowerCase");
    alphaOpts.put("maxLength", 20);

    ModifyPlan player_plan = p.fromTriples(
            p.pattern(playerIdCol, players.iri("age"), playerAgeCol),
            p.pattern(playerIdCol, players.iri("name"), playerNameCol),
            p.pattern(playerIdCol, players.iri("team"), playerTeamCol)
    );
    ModifyPlan team_plan = p.fromTriples(
            p.pattern(teamIdCol, team.iri("name"), teamNameCol),
            p.pattern(teamIdCol, team.iri("city"), teamCityCol)
    );
    ModifyPlan output = player_plan.joinInner(team_plan,
            p.on(playerTeamCol, teamIdCol),
            p.and(
                    p.gt(playerAgeCol, p.xs.intVal(27)), p.eq(teamNameCol, p.xs.string("Giants")))
    )
            .orderBy(p.asc(playerAgeCol))
            .select(
                    p.as("PlayerName", playerNameCol),
                    p.as("PlayerAge", playerAgeCol),
                    p.as("TeamName", p.fn.concat(teamCityCol, p.xs.string(" "), teamNameCol))
            )
            .bind(p.rdt.redactNumber(p.col("PlayerAge"), numbOpts))
            .bind(p.rdt.maskDeterministic(p.col("TeamName"), alphaOpts)
		  );
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");

    rowMgr.resultDoc(output, jacksonHandle);
    JsonNode jsonResults = jacksonHandle.get();
    JsonNode jsonBindingsNodes = jsonResults.path("rows");
    System.out.println(jsonBindingsNodes);

    Pattern patternName = Pattern.compile("[a-z]");
    Pattern patternAge = Pattern.compile("(\\d)+,(\\d)+");

    // Should have 2 nodes returned.
    assertEquals("Two nodes not returned from testJoinInnerWithCondition method ", 2, jsonBindingsNodes.size());
    JsonNode first = jsonBindingsNodes.path(0);

    String rowOneTeamName = jsonBindingsNodes.path(0).path("TeamName").path("value").asText();
    String rowOnePlayerAge = jsonBindingsNodes.path(0).path("PlayerAge").path("value").asText();

    assertEquals("Row 1 PlayerName value incorrect", "Josh Ream", first.path("PlayerName").path("value").asText());
    assertTrue("Row 1  Team value mask incorrect",  patternName.matcher(rowOneTeamName).find());
    assertTrue("Row 1 PlayerAge mask incorrect", patternAge.matcher(rowOnePlayerAge).find());

    JsonNode second = jsonBindingsNodes.path(1);
    String rowTwoTeamName = jsonBindingsNodes.path(1).path("TeamName").path("value").asText();
    String rowTwoPlayerAge = jsonBindingsNodes.path(1).path("PlayerAge").path("value").asText();

    assertEquals("Row 2 PlayerName value incorrect", "John Doe", second.path("PlayerName").path("value").asText());
    assertTrue("Row 2  Team value mask value incorrect",  patternName.matcher(rowTwoTeamName).find());
    assertTrue("Row 2 PlayerAge mask incorrect", patternAge.matcher(rowTwoPlayerAge).find());
  }

  //@AfterClass
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
