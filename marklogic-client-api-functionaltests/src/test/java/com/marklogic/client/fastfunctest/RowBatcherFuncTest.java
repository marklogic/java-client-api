package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.RowBatcher;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.type.CtsReferenceExpr;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RowBatcherFuncTest extends AbstractFunctionalTest {

    private static DataMovementManager dmManager = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // Install the TDE templates into schemadbName DB
        // loadFileToDB(client, filename, docURI, collection, document format)
        loadFileToDB(schemasClient, "masterDetail.tdex", "/optic/view/test/masterDetail.tdex", "XML", new String[] { "http://marklogic.com/xdmp/tde" });
        loadFileToDB(schemasClient, "masterDetail2.tdej", "/optic/view/test/masterDetail2.tdej", "JSON", new String[] { "http://marklogic.com/xdmp/tde" });
        loadFileToDB(schemasClient, "masterDetail3.tdej", "/optic/view/test/masterDetail3.tdej", "JSON", new String[] { "http://marklogic.com/xdmp/tde" });

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
        dmManager = client.newDataMovementManager();
    }

    @Test
    public void testRowBatcherWithJackson() {
        System.out.println("In testRowBatcherWithJackson method");
        StringBuilder failedBuf = null;

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
    public void testJoinInner() {
        System.out.println("In testJoinInner method");
        StringBuilder failedBuf = null;

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
    public void testWithExpressioncolumns() {
        System.out.println("In testWithExpressioncolumns method");
        StringBuilder failedBuf = null;

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

    /**
     * This test previously applied an offsetLimit to only get 2 rows back, but that did not have consistent results
     * between ML 10 and 11. The point of this test does not appear to be testing that offsetLimit works, but rather
     * that a join between a view and lexicons returns all the expected rows.
     */
    @Test
    public void testJoinFromViewFromLexicons() {
        System.out.println("In testJoinfromViewfronLexicons method");
        StringBuilder failedBuf = null;

        RowBatcher<JsonNode> rowsBatcherOfJsonObj = dmManager.newRowBatcher(new JacksonHandle())
                .withBatchSize(8)
                .withThreadCount(4)
                .withJobName("JoinfromViewfronLexicons");
        RowManager rowMgr = rowsBatcherOfJsonObj.getRowManager();
        rowMgr.setDatatypeStyle(RowManager.RowSetPart.HEADER);

        PlanBuilder p = rowMgr.newPlanBuilder();

        Map<String, CtsReferenceExpr> indexes = new HashMap<>();
        indexes.put("uri", p.cts.uriReference());
        indexes.put("city", p.cts.jsonPropertyReference("city"));
        indexes.put("popularity", p.cts.jsonPropertyReference("popularity"));
        indexes.put("date", p.cts.jsonPropertyReference("date"));
        indexes.put("distance", p.cts.jsonPropertyReference("distance"));
        indexes.put("point", p.cts.jsonPropertyReference("latLonPoint"));

        PlanBuilder.ModifyPlan output = p
            .fromView("opticFunctionalTest", "detail", "myDetail")
            .joinFullOuter(p.fromLexicons(indexes, "myCity"));

        rowsBatcherOfJsonObj.withBatchView(output);

        Set<String> citiesFound = Collections.synchronizedSet(new HashSet<>());

        rowsBatcherOfJsonObj.onSuccess(e -> {
            JsonNode rows = e.getRowsDoc().get("rows");

            if (rows == null)
                failedBuf.append("No rows returned in batch from " + e.getLowerBound() + "to" + e.getUpperBound());
            else {
                rows.forEach(row -> citiesFound.add(row.get("myCity.city").asText()));
            }
            }).onFailure((fevt, mythrows) -> {
                failedBuf.append("Batch Failures in " + fevt.getJobBatchNumber() + "batch from " + fevt.getLowerBound() + "to" + fevt.getUpperBound());
            } );
        dmManager.startJob(rowsBatcherOfJsonObj);
        rowsBatcherOfJsonObj.awaitCompletion();

        Stream.of("new jersey", "cape town", "beijing", "new york", "london").forEach(city -> {
            assertTrue("Did not find " + city + " in " + citiesFound, citiesFound.contains(city));
        });
        assertEquals(5, citiesFound.size());
    }

    @Test
    public void testMultipleSuccessListener() {
        System.out.println("In testMultipleSuccessListener method");
        StringBuilder failedBuf = null;

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
    public void testfromLexicons() {
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
    public void testExceptionInSecondSuccessListener() {
        System.out.println("In testExceptionInSecondSuccessListener method");
        StringBuilder failedBuf = null;
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
