package com.marklogic.client.datamovement.functionaltests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.RowBatcher;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.type.CtsReferenceExpr;
import com.marklogic.client.type.PlanParamExpr;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class RowBatcherFuncTest extends BasicJavaClientREST {
    private static String dbName = "RowBatcherFuncDB";
    private static String[] fNames = { "RowBatcherFuncDB-1", "RowBatcherFuncDB-2", "RowBatcherFuncDB-3" };

    private static String schemadbName = "RowBatcherFuncSchemaDB";
    private static String[] schemafNames = { "RowBatcherFuncSchemaDB-1" };

    private static String datasource = "src/test/java/com/marklogic/client/functionaltest/data/optics/";

    private static DataMovementManager dmManager = null;
    private static String restServerName = null;
    private static int restServerPort = 0;
    private static DatabaseClient client = null;
    private static String dataConfigDirPath = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        loadGradleProperties();
        restServerPort = getRestAppServerPort();

        restServerName = getRestAppServerName();
        // Points to top level of all QA data folder
        dataConfigDirPath = getDataConfigDirPath();
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
            schemaDBclient = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), schemadbName, new DatabaseClientFactory.DigestAuthContext("opticUser", "0pt1c"));
            client = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), new DatabaseClientFactory.DigestAuthContext("opticUser", "0pt1c"));
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
        dmManager = client.newDataMovementManager();
    }

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

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        System.out.println("In tearDownAfterClass");
        // Release clients
        client.release();
        associateRESTServerWithDB(restServerName, "Documents");
        deleteRESTUser("eval-user");
        deleteUserRole("test-eval");
        detachForest(dbName, fNames[0]);

        deleteDB(dbName);
        deleteForest(fNames[0]);
    }

    @Test
    public void testRowBatcherWithJackson() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException {
        System.out.println("In testRowBatcherWithJackson method");
        StringBuilder failedBuf = null;
        StringBuilder successBuf = null;

        RowBatcher<JsonNode> rowsBatcherOfJsonObj = dmManager.newRowBatcher(new JacksonHandle())
                                                       .withBatchSize(1)
                                                       .withThreadCount(2)
                                                       .withJobName("Export As Rows of JsonNodes");
        RowManager rowMgr = rowsBatcherOfJsonObj.getRowManager();
        rowMgr.setDatatypeStyle(RowManager.RowSetPart.HEADER);

        PlanBuilder p = rowMgr.newPlanBuilder();
        PlanBuilder.ModifyPlan plan = p.fromView("opticFunctionalTest", "detail");
        rowsBatcherOfJsonObj.withBatchView(plan);
        ArrayList<Double> exptdAmt = new ArrayList(Arrays.asList(10.01,20.02,30.03,40.04,50.05,60.06));
        ArrayList<Double> resultAmt = new ArrayList<>();

        rowsBatcherOfJsonObj.onSuccess(e ->{
            JsonNode resDoc = e.getRowsDoc().get("rows");

            if (resDoc == null)
                failedBuf.append("No rows returned in batch from " + e.getLowerBound() + "to" + e.getUpperBound());
            else {
                     for (int i=0; i<resDoc.size(); i++) {
                         //System.out.println("Thread id : " + Thread.currentThread().getId() + " is named as " + Thread.currentThread().getName());
                         System.out.println("row : "+ resDoc.get(i).get("opticFunctionalTest.detail.amount").asText());
                         resultAmt.add(resDoc.get(i).get("opticFunctionalTest.detail.amount").asDouble());
                     }
                }
        })
        .onFailure((fevt, mythrows)-> {
            failedBuf.append("Batch Failures in " + fevt.getJobBatchNumber() + "batch from "+ fevt.getLowerBound() + "to" + fevt.getUpperBound());
                }
        );
        dmManager.startJob(rowsBatcherOfJsonObj);
        rowsBatcherOfJsonObj.awaitCompletion();
        for (Double d : resultAmt) {
            assertTrue(exptdAmt.contains(d.doubleValue()));
        }
    }

    @Test
    public void testJoinInner() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException {
        System.out.println("In testJoinInner method");
        StringBuilder failedBuf = null;
        StringBuilder successBuf = null;

        RowBatcher<JsonNode> rowsBatcherOfJsonObj = dmManager.newRowBatcher(new JacksonHandle())
                .withBatchSize(1)
                .withThreadCount(2)
                .withJobName("JoinInner");
        RowManager rowMgr = rowsBatcherOfJsonObj.getRowManager();
        rowMgr.setDatatypeStyle(RowManager.RowSetPart.HEADER);

        PlanBuilder p = rowMgr.newPlanBuilder();
        PlanBuilder.ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
                .orderBy(p.col( "id"));
        PlanBuilder.ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
                .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
        PlanBuilder.ModifyPlan plan3 = plan1.joinInner(plan2)
                .select(
                        p.as("MasterName", p.schemaCol("opticFunctionalTest", "master", "name")),
                        p.schemaCol("opticFunctionalTest", "master", "date"),
                        p.as("DetailName",p.schemaCol("opticFunctionalTest", "detail", "name")),
                        p.col( "amount"),
                        p.col( "color")
                );
        rowsBatcherOfJsonObj.withBatchView(plan3);
        ArrayList<Double> exptdAmt = new ArrayList(Arrays.asList(10.01,20.02,30.03,40.04,50.05,60.06));
        ArrayList<Double> resultAmt = new ArrayList<>();

        rowsBatcherOfJsonObj.onSuccess(e ->{
            JsonNode resDoc = e.getRowsDoc().get("rows");

            if (resDoc == null)
                failedBuf.append("No rows returned in batch from " + e.getLowerBound() + "to" + e.getUpperBound());
            else {
                for (int i=0; i<resDoc.size(); i++) {
                    //System.out.println("Thread id : " + Thread.currentThread().getId() + " is named as " + Thread.currentThread().getName());
                    System.out.println("row : "+ resDoc.get(i).get("opticFunctionalTest.detail.amount").asText());
                    resultAmt.add(resDoc.get(i).get("opticFunctionalTest.detail.amount").asDouble());
                }
            }
        })
                .onFailure((fevt, mythrows)-> {
                            failedBuf.append("Batch Failures in " + fevt.getJobBatchNumber() + "batch from "+ fevt.getLowerBound() + "to" + fevt.getUpperBound());
                        }
                );
        dmManager.startJob(rowsBatcherOfJsonObj);
        rowsBatcherOfJsonObj.awaitCompletion();
        for (Double d : resultAmt) {
            assertTrue(exptdAmt.contains(d.doubleValue()));
        }
    }

    @Test
    public void testWithExpressioncolumns() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException {
        System.out.println("In testWithExpressioncolumns method");
        StringBuilder failedBuf = null;
        StringBuilder successBuf = null;

        RowBatcher<JsonNode> rowsBatcherOfJsonObj = dmManager.newRowBatcher(new JacksonHandle())
                .withBatchSize(1)
                .withThreadCount(2)
                .withJobName("ExpressionColumns");
        RowManager rowMgr = rowsBatcherOfJsonObj.getRowManager();
        rowMgr.setDatatypeStyle(RowManager.RowSetPart.HEADER);

        PlanBuilder p = rowMgr.newPlanBuilder();
        PlanBuilder.ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail")
                .orderBy(p.schemaCol("opticFunctionalTest", "detail", "id"));
        PlanBuilder.ModifyPlan plan2 = p.fromView("opticFunctionalTest", "master")
                .orderBy(p.schemaCol("opticFunctionalTest", "master", "id"));
        PlanBuilder.ModifyPlan plan3 = plan1.joinInner(plan2)
                .where(p.eq(p.schemaCol("opticFunctionalTest", "master", "id"),
                        p.col("masterId"))
                )
                .select(p.as("added", p.add(p.col("amount"), p.col("masterId"))),
                        p.as("substracted", p.subtract(p.col("amount"), p.viewCol("master", "id"))),
                        p.as("modulo", p.modulo(p.col("amount"), p.viewCol("master", "id"))),
                        p.as("invSubstract", p.subtract(p.col("amount"), p.viewCol("master", "id"))),
                        p.as("divided", p.divide(p.col("amount"), p.multiply(p.col("amount"), p.viewCol("detail", "id"))))
                );
        rowsBatcherOfJsonObj.withBatchView(plan3);
        ArrayList<Double> exptdAmt = new ArrayList(Arrays.asList(11.01, 22.02, 31.03, 42.04, 51.05, 62.06));
        ArrayList<Double> resultAmt = new ArrayList<>();

        rowsBatcherOfJsonObj.onSuccess(e -> {
            JsonNode resDoc = e.getRowsDoc().get("rows");

            if (resDoc == null)
                failedBuf.append("No rows returned in batch from " + e.getLowerBound() + "to" + e.getUpperBound());
            else {
                for (int i = 0; i < resDoc.size(); i++) {
                    System.out.println("Thread id : " + Thread.currentThread().getId() + " is named as " + Thread.currentThread().getName());
                    System.out.println("row : " + resDoc.get(i).get("added").asText());
                    resultAmt.add(resDoc.get(i).get("added").asDouble());
                }
            }
        })
                .onFailure((fevt, mythrows) -> {
                            failedBuf.append("Batch Failures in " + fevt.getJobBatchNumber() + "batch from " + fevt.getLowerBound() + "to" + fevt.getUpperBound());
                        }
                );
        dmManager.startJob(rowsBatcherOfJsonObj);
        rowsBatcherOfJsonObj.awaitCompletion();
        for (Double d : resultAmt) {
            assertTrue(exptdAmt.contains(d.doubleValue()));
        }
    }

    @Test
    public void testJoinfromViewfronLexicons() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException {
        System.out.println("In testJoinfromViewfronLexicons method");
        StringBuilder failedBuf = null;
        StringBuilder successBuf = null;

        RowBatcher<JsonNode> rowsBatcherOfJsonObj = dmManager.newRowBatcher(new JacksonHandle())
                .withBatchSize(1)
                .withThreadCount(1)
                .withJobName("JoinfromViewfronLexicons");
        RowManager rowMgr = rowsBatcherOfJsonObj.getRowManager();
        rowMgr.setDatatypeStyle(RowManager.RowSetPart.HEADER);

        PlanBuilder p = rowMgr.newPlanBuilder();

        Map<String, CtsReferenceExpr> indexes = new HashMap<String, CtsReferenceExpr>();
        indexes.put("uri", p.cts.uriReference());
        indexes.put("city", p.cts.jsonPropertyReference("city"));
        indexes.put("popularity", p.cts.jsonPropertyReference("popularity"));
        indexes.put("date", p.cts.jsonPropertyReference("date"));
        indexes.put("distance", p.cts.jsonPropertyReference("distance"));
        indexes.put("point", p.cts.jsonPropertyReference("latLonPoint"));

        // plan1 - fromView
        PlanBuilder.ModifyPlan plan1 = p.fromView("opticFunctionalTest", "detail", "myDetail");
        // plan2 - fromLexicons
        PlanBuilder.ModifyPlan plan2 = p.fromLexicons(indexes, "myCity");

        PlanBuilder.ModifyPlan output = plan1.joinFullOuter(plan2).orderBy(p.col("id")).offsetLimit(0, 2);
        rowsBatcherOfJsonObj.withBatchView(output);

        ArrayList<String> exptdCity = new ArrayList(Arrays.asList("beijing", "cape town"));
        ArrayList<String> resultCity = new ArrayList<>();

        rowsBatcherOfJsonObj.onSuccess(e -> {
            JsonNode resDoc = e.getRowsDoc().get("rows");

            if (resDoc == null)
                failedBuf.append("No rows returned in batch from " + e.getLowerBound() + "to" + e.getUpperBound());
            else {
                System.out.println("resDoc.get(0) : " + resDoc.get(0).get("myCity.city").asText());
                System.out.println("resDoc.get(1) : " + resDoc.get(1).get("myCity.city").asText());
                System.out.println("Thread id : " + Thread.currentThread().getId() + " is named as " + Thread.currentThread().getName());
                resultCity.add(resDoc.get(0).get("myCity.city").asText());
                resultCity.add(resDoc.get(1).get("myCity.city").asText());
            }
            }).onFailure((fevt, mythrows) -> {
                failedBuf.append("Batch Failures in " + fevt.getJobBatchNumber() + "batch from " + fevt.getLowerBound() + "to" + fevt.getUpperBound());
            } );
        dmManager.startJob(rowsBatcherOfJsonObj);
        rowsBatcherOfJsonObj.awaitCompletion();
        for (String s : resultCity) {
            assertTrue(exptdCity.contains(s));
        }
    }

    @Test
    public void testMultipleSuccessListener() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException {
        System.out.println("In testMultipleSuccessListener method");
        StringBuilder failedBuf = null;
        StringBuilder successBuf = null;

        RowBatcher<JsonNode> rowsBatcherOfJsonObj = dmManager.newRowBatcher(new JacksonHandle())
                .withBatchSize(1)
                .withThreadCount(2)
                .withJobName("MultipleSuccessListener");
        RowManager rowMgr = rowsBatcherOfJsonObj.getRowManager();
        rowMgr.setDatatypeStyle(RowManager.RowSetPart.HEADER);

        PlanBuilder p = rowMgr.newPlanBuilder();
        PlanBuilder.ModifyPlan plan = p.fromView("opticFunctionalTest", "detail");
        rowsBatcherOfJsonObj.withBatchView(plan);
        ArrayList<Double> exptdAmt = new ArrayList(Arrays.asList(10.01,20.02,30.03,40.04,50.05,60.06));
        ArrayList<Double> resultAmt1 = new ArrayList<>();
        ArrayList<Double> resultAmt2 = new ArrayList<>();
        rowsBatcherOfJsonObj.onSuccess(e ->{
            JsonNode resDoc = e.getRowsDoc().get("rows");

            if (resDoc == null)
                failedBuf.append("No rows returned in batch from " + e.getLowerBound() + "to" + e.getUpperBound());
            else {
                for (int i=0; i<resDoc.size(); i++) {
                    //System.out.println("Thread id : " + Thread.currentThread().getId() + " is named as " + Thread.currentThread().getName());
                    System.out.println("row1 : "+ resDoc.get(i).get("opticFunctionalTest.detail.amount").asText());
                    resultAmt1.add(resDoc.get(i).get("opticFunctionalTest.detail.amount").asDouble());
                }
            }
        }).onSuccess(e ->{
            JsonNode resDoc = e.getRowsDoc().get("rows");

            if (resDoc == null)
                failedBuf.append("No rows returned in batch from " + e.getLowerBound() + "to" + e.getUpperBound());
            else {
                for (int i=0; i<resDoc.size(); i++) {
                    //System.out.println("Thread id : " + Thread.currentThread().getId() + " is named as " + Thread.currentThread().getName());
                    System.out.println("row2 : "+ resDoc.get(i).get("opticFunctionalTest.detail.amount").asText());
                    resultAmt2.add(resDoc.get(i).get("opticFunctionalTest.detail.amount").asDouble());
                }
            }
        }).onFailure((fevt, mythrows)-> {
            failedBuf.append("Batch Failures in " + fevt.getJobBatchNumber() + "batch from "+ fevt.getLowerBound() + "to" + fevt.getUpperBound());
        });
        dmManager.startJob(rowsBatcherOfJsonObj);
        rowsBatcherOfJsonObj.awaitCompletion();
        for (Double d : resultAmt1) {
            System.out.println("Results from first success Listener : "+ d);
            assertTrue(exptdAmt.contains(d.doubleValue()));
        }
        for (Double d : resultAmt2) {
            System.out.println("Results from second success Listener : "+ d);
            assertTrue(exptdAmt.contains(d.doubleValue()));
        }
    }

    // Negative - Plan that starts with a fromLexicons() - should be rejected
    @Test
    public void testfromLexicons() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException {
        System.out.println("In fromLexicons method");
        StringBuilder failedBuf = null;
        String exBuf = null;

        RowBatcher<JsonNode> rowsBatcherOfJsonObj = dmManager.newRowBatcher(new JacksonHandle())
                .withBatchSize(1)
                .withThreadCount(2)
                .withJobName("FromLexicons");
        RowManager rowMgr = rowsBatcherOfJsonObj.getRowManager();
        rowMgr.setDatatypeStyle(RowManager.RowSetPart.HEADER);

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

        // plan1
        PlanBuilder.ModifyPlan plan1 = p.fromLexicons(index1, "myCity");
        // plan2
        PlanBuilder.ModifyPlan plan2 = p.fromLexicons(index2, "myTeam");

        // plan3
        PlanBuilder.ModifyPlan plan3 = plan1.joinInner(plan2)
                .where(p.eq(p.viewCol("myCity", "city"), p.col("cityName")))
                .joinDoc(p.col("doc"), p.col("uri2"));
        try {
            rowsBatcherOfJsonObj.withBatchView(plan3);
            rowsBatcherOfJsonObj.onSuccess(e -> {
            }).onFailure((fevt, mythrows) -> {
                                failedBuf.append("Batch Failures in " + fevt.getJobBatchNumber() + "batch from " + fevt.getLowerBound() + "to" + fevt.getUpperBound());
            });
            dmManager.startJob(rowsBatcherOfJsonObj);
            rowsBatcherOfJsonObj.awaitCompletion();
        } catch (Exception ex) {
            exBuf = ex.getMessage();
        }
        finally {
            assertTrue("Exception message incorrect", exBuf.contains("First operation in Optic plan must be fromView()"));
        }
    }

    @Test
    public void testExceptionInSecondSuccessListener() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException {
        System.out.println("In testExceptionInSecondSuccessListener method");
        StringBuilder failedBuf = null;
        StringBuilder successBuf = null;
        RowBatcher<JsonNode> rowsBatcherOfJsonObj = dmManager.newRowBatcher(new JacksonHandle())
                    .withBatchSize(1)
                    .withThreadCount(2)
                    .withJobName("ExceptionInSecondSuccessListener");
        RowManager rowMgr = rowsBatcherOfJsonObj.getRowManager();
        rowMgr.setDatatypeStyle(RowManager.RowSetPart.HEADER);

        PlanBuilder p = rowMgr.newPlanBuilder();
        PlanBuilder.ModifyPlan plan = p.fromView("opticFunctionalTest", "detail");
        rowsBatcherOfJsonObj.withBatchView(plan);
        ArrayList<Double> exptdAmt = new ArrayList(Arrays.asList(10.01, 20.02, 30.03, 40.04, 50.05, 60.06));
        ArrayList<Double> resultAmt1 = new ArrayList<>();

        rowsBatcherOfJsonObj.onSuccess(e -> {
                JsonNode resDoc = e.getRowsDoc().get("rows");

                if (resDoc == null)
                    failedBuf.append("No rows returned in batch from 1st listener" + e.getLowerBound() + "to" + e.getUpperBound());
                else {
                    for (int i = 0; i < resDoc.size(); i++) {
                        //System.out.println("Thread id : " + Thread.currentThread().getId() + " is named as " + Thread.currentThread().getName());
                        System.out.println("row1 : " + resDoc.get(i).get("opticFunctionalTest.detail.amount").asText());
                        resultAmt1.add(resDoc.get(i).get("opticFunctionalTest.detail.amount").asDouble());
                    }
                }
        }).onSuccess(e -> {
                JsonNode resDoc = e.getRowsDoc().get("rows");

                if (resDoc == null)
                    failedBuf.append("No rows returned in batch from 2nd listener" + e.getLowerBound() + "to" + e.getUpperBound());
                else {
                    // Generate a NPE purposefully.
                    String s = null;
                    s.length();
                    //System.out.println("Thread id : " + Thread.currentThread().getId() + " is named as " + Thread.currentThread().getName());
                }
        }).onFailure((fevt, mythrows) -> {
                failedBuf.append("Batch Failures in " + fevt.getJobBatchNumber() + "batch from " + fevt.getLowerBound() + "to" + fevt.getUpperBound());
        });
        dmManager.startJob(rowsBatcherOfJsonObj);
        rowsBatcherOfJsonObj.awaitCompletion();
        for (Double d : resultAmt1) {
            System.out.println("Results from first success Listener : " + d);
            assertTrue(exptdAmt.contains(d.doubleValue()));
        }
    }
   }
