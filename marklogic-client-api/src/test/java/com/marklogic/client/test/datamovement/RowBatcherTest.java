/*
 * Copyright (c) 2020 MarkLogic Corporation
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
package com.marklogic.client.test.datamovement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.RowBatcher;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.test.Common;
import com.marklogic.client.type.PlanColumn;
import com.marklogic.client.type.PlanSystemColumn;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class RowBatcherTest {
    private final static String TEST_DIR = "/test/rowbatch/unit/";
    private final static String TEST_COLLECTION = TEST_DIR+"codes";

    private final static int TEST_DOC_COUNT = 120;

    private static DatabaseClient      db      = Common.connect();
    private static JSONDocumentManager docMgr  = db.newJSONDocumentManager();
    private static DataMovementManager moveMgr = db.newDataMovementManager();

    private static ObjectMapper mapper = new ObjectMapper();

    private static Set<String>   expectedFull = new HashSet<>();
    private static Set<JsonNode> expectedDoc  = new HashSet<>();

    @BeforeClass
    public static void beforeClass() throws Exception {
        setupIndex();
        setupDocuments();
    }
    private static void setupIndex() {
        final String tdeUri = TEST_COLLECTION+".tdex";

        DatabaseClient schemasDB = Common.newServerAdminClient("Schemas");
        XMLDocumentManager schemaMgr = schemasDB.newXMLDocumentManager();

        if (schemaMgr.exists(tdeUri) == null) {
            DocumentMetadataHandle tdeMetaHndl = new DocumentMetadataHandle();
            tdeMetaHndl.getPermissions().add("rest-reader", DocumentMetadataHandle.Capability.READ);
            tdeMetaHndl.getPermissions().add("rest-writer", DocumentMetadataHandle.Capability.UPDATE);
            tdeMetaHndl.getCollections().add("http://marklogic.com/xdmp/tde");

            StringBuilder tdeBldr = new StringBuilder();
            tdeBldr.append("<template xmlns=\"http://marklogic.com/xdmp/tde\">\n");
            tdeBldr.append("<directories><directory>");
            tdeBldr.append(TEST_DIR);
            tdeBldr.append("</directory></directories>\n");
            tdeBldr.append("<collections><collection>");
            tdeBldr.append(TEST_COLLECTION);
            tdeBldr.append("</collection></collections>\n");
            tdeBldr.append("<context>/code</context>\n");
            tdeBldr.append("<rows>\n");
            tdeBldr.append("<row>\n");
            tdeBldr.append("<schema-name>rowBatcherUnitTest</schema-name>\n");
            tdeBldr.append("<view-name>code</view-name>\n");
            tdeBldr.append("<columns>\n");
            tdeBldr.append("<column><name>full</name><scalar-type>string</scalar-type><val>full</val></column>\n");
            tdeBldr.append("<column><name>field1</name><scalar-type>int</scalar-type><val>field1</val></column>\n");
            tdeBldr.append("<column><name>field2</name><scalar-type>int</scalar-type><val>field2</val></column>\n");
            tdeBldr.append("<column><name>field3</name><scalar-type>int</scalar-type><val>field3</val></column>\n");
            tdeBldr.append("<column><name>field4</name><scalar-type>int</scalar-type><val>field4</val></column>\n");
            tdeBldr.append("</columns>\n");
            tdeBldr.append("</row>\n");
            tdeBldr.append("</rows>\n");
            tdeBldr.append("</template>\n");

            // System.out.println(tdeBldr.toString());

            schemaMgr.write(tdeUri, tdeMetaHndl, new StringHandle(tdeBldr.toString()));
        }

        schemasDB.release();
    }
    private static void setupDocuments() throws Exception {
        DocumentWriteSet writeSet = newWriteSet(docMgr);

        for (int i=0; i < TEST_DOC_COUNT;) {
            String doc = makeDocContent(i);
            expectedDoc.add(mapper.readValue(doc, ObjectNode.class));
            writeSet.add(makeDocUri(i), new StringHandle(doc));
            i++;
            if ((i % 120) == 0 || i == TEST_DOC_COUNT) {
                docMgr.write(writeSet);
                if (i < TEST_DOC_COUNT) {
                    writeSet = newWriteSet(docMgr);
                } else {
                    break;
                }
            }
        }
    }
    private static String makeDocUri(int i) {
        return TEST_DIR+"code"+i+".json";
    }
    private static String makeDocContent(int i) {
        StringBuilder docBldr = new StringBuilder();
        int mod2 = i % 2;
        int mod3 = i % 3;
        int mod5 = i % 5;
        String full = i + "-" + mod2 + "-" + mod3 + "-" + mod5;
        if (i < TEST_DOC_COUNT) {
            expectedFull.add(full);
        }
        docBldr.append("{\"code\":{\n");
        docBldr.append("\"full\":\"");docBldr.append( full );docBldr.append("\",\n");
        docBldr.append("\"field1\":");docBldr.append( i    );docBldr.append(",\n");
        docBldr.append("\"field2\":");docBldr.append( mod2 );docBldr.append(",\n");
        docBldr.append("\"field3\":");docBldr.append( mod3 );docBldr.append(",\n");
        docBldr.append("\"field4\":");docBldr.append( mod5 );docBldr.append("\n");
        docBldr.append("}}\n");
        return docBldr.toString();
    }
    private static DocumentWriteSet newWriteSet(JSONDocumentManager docMgr) {
        DocumentWriteSet writeSet = docMgr.newWriteSet();
        writeSet.addDefault(newDocumentMetadataHandle());
        return writeSet;
    }
    private static DocumentMetadataHandle newDocumentMetadataHandle() {
        DocumentMetadataHandle docMetaHndl = new DocumentMetadataHandle();
        docMetaHndl.getPermissions().add("rest-reader", DocumentMetadataHandle.Capability.READ);
        docMetaHndl.getPermissions().add("rest-writer", DocumentMetadataHandle.Capability.UPDATE);
        docMetaHndl.getCollections().add(TEST_COLLECTION);
        return docMetaHndl;
    }
    @AfterClass
    public static void afterClass() {
        QueryManager queryMgr = db.newQueryManager();
        DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
        deleteQuery.setCollections(TEST_COLLECTION);
        queryMgr.delete(deleteQuery);
    }

    @Test
    public void testRows1Thread() throws Exception {
        runRowsTest(jsonBatcher(1));
    }
    @Test
    public void testRows3Threads() throws Exception {
        runRowsTest(jsonBatcher(3));
    }
    @Test
    public void testRowsForest2Threads() throws Exception {
        runRowsTest(jsonBatcher(moveMgr.readForestConfig().listForests().length * 2));
    }
    @Test
    public void testDocs1Thread() throws Exception {
        runDocsTest(jsonBatcher(1));
    }
    @Test
    public void testDocs3Threads() throws Exception {
        runDocsTest(jsonBatcher(3));
    }
    @Test
    public void testDocsForest2Threads() throws Exception {
        runDocsTest(jsonBatcher(moveMgr.readForestConfig().listForests().length * 2));
    }
    @Test
    public void testRows3ThreadsPointInTime() throws Exception {
        runRowsTest(jsonBatcher(3), true);
    }
    private RowBatcher<JsonNode> jsonBatcher(int threads) {
        return moveMgr.newRowBatcher(new JacksonHandle())
                        .withBatchSize(30)
                        .withThreadCount(threads);
    }
    /* TODO: formats, styles
     */
    private void runRowsTest(RowBatcher<JsonNode> rowBatcher) throws Exception {
        runRowsTest(rowBatcher, false);
    }
    private void runRowsTest(RowBatcher<JsonNode> rowBatcher, boolean consistentSnapshot) throws Exception {
        if (consistentSnapshot) {
            rowBatcher.withConsistentSnapshot();
        }
        String addedDocUri = consistentSnapshot ? makeDocUri(TEST_DOC_COUNT) : null;

        RowManager rowMgr = rowBatcher.getRowManager();
        rowMgr.setDatatypeStyle(RowManager.RowSetPart.HEADER);

        PlanBuilder planBuilder = rowMgr.newPlanBuilder();
        PlanBuilder.ModifyPlan plan =
                planBuilder.fromView("rowBatcherUnitTest", "code", "");

        AtomicBoolean addedDoc = consistentSnapshot ? new AtomicBoolean(false) : null;

        Set<String> actual = new ConcurrentSkipListSet<>();
        AtomicBoolean failed = new AtomicBoolean(false);
        rowBatcher.withBatchView(plan)
                .onSuccess(event -> {
                    /* System.out.println("succeeded batch="+event.getJobBatchNumber()+
                            " from "+event.getLowerBound()+" through "+event.getUpperBound()+
                            ((event.getJobBatchNumber() == 1) ? "\n"+event.getRowsDoc() : "")); */
                    try {
                        if (consistentSnapshot) {
                            if (addedDoc.compareAndSet(false, true)) {
                                docMgr.write(addedDocUri, newDocumentMetadataHandle(),
                                        new StringHandle(makeDocContent(TEST_DOC_COUNT)));
                            }
                        }

                        JsonNode rows = event.getRowsDoc().get("rows");
                        if (rows == null || !rows.isArray()) {
                            System.out.println("no rows for batch="+event.getJobBatchNumber()+
                                " from "+event.getLowerBound()+" through "+event.getUpperBound());
                            failed.set(true);
                            return;
                        }
                        // System.out.println("starting batch="+event.getJobBatchNumber()+" for "+rows.size()+" rows");
                        for (JsonNode row: rows) {
                            // System.out.println(row);
                            String full = row.get("full").asText();
                            if (actual.contains(full)) {
                                System.out.println("already found full="+full);
                                failed.set(true);
                                return;
                            } else {
                                for (int i=1; i <= 4; i++) {
                                    String fieldName = "field"+i;
                                    if (!row.has(fieldName)) {
                                        System.out.println("row missing field="+fieldName);
                                        failed.set(true);
                                        return;
                                    }
                                }
                                actual.add(full);
                            }
                        }
                        // System.out.println("finish batch="+event.getJobBatchNumber());
                    } catch (Throwable e) {
                        failed.set(true);
                        System.out.println(e.getMessage());
                        e.printStackTrace(System.out);
                    }
                })
                .onFailure((event, throwable) -> {
                    failed.set(true);
                    System.out.println("failed batch="+event.getJobBatchNumber()+
                            " from "+event.getLowerBound()+" through "+event.getUpperBound());
                });

        moveMgr.startJob(rowBatcher);
        rowBatcher.awaitCompletion();
        // System.out.println("stopped="+rowBatcher.isStopped());

        if (consistentSnapshot) {
            docMgr.delete(addedDocUri);
        }

        assertEquals("test execution failed", false, failed.get());
        assertEquals("mismatch on row estimate", expectedFull.size(), rowBatcher.getRowEstimate());
        assertEquals("mismatch on actual rows size", expectedFull.size(), actual.size());
        assertEquals("mismatch on actual rows", expectedFull, actual);
    }
    private void runDocsTest(RowBatcher<JsonNode> rowBatcher) throws Exception {
        RowManager rowMgr = rowBatcher.getRowManager();
        rowMgr.setDatatypeStyle(RowManager.RowSetPart.HEADER);

        PlanBuilder planBuilder = rowMgr.newPlanBuilder();
        PlanSystemColumn docId = planBuilder.fragmentIdCol("docId");
        PlanColumn doc = planBuilder.col("doc");
        PlanBuilder.ModifyPlan plan =
                planBuilder.fromView("rowBatcherUnitTest", "code", "", docId)
                            .joinDoc(doc, docId)
                            .select(doc);


        Set<JsonNode> actual = Collections.synchronizedSet(new HashSet<>());
        AtomicBoolean failed = new AtomicBoolean(false);
        rowBatcher.withBatchView(plan)
                .onSuccess(event -> {
                    /* System.out.println("succeeded batch="+event.getJobBatchNumber()+
                            " from "+event.getLowerBound()+" through "+event.getUpperBound()+
                            ((event.getJobBatchNumber() == 1) ? "\n"+event.getRowsDoc() : "")); */
                    try {
                        JsonNode rows = event.getRowsDoc().get("rows");
                        if (rows == null || !rows.isArray()) {
                            System.out.println("no rows for batch="+event.getJobBatchNumber()+
                                    " from "+event.getLowerBound()+" through "+event.getUpperBound());
                            failed.set(true);
                            return;
                        }
                        // System.out.println("starting batch="+event.getJobBatchNumber()+" for "+rows.size()+" rows");
                        for (JsonNode row: rows) {
                            JsonNode docNode = row.get("doc");
                            // System.out.println(docNode);
                            if (actual.contains(docNode)) {
                                System.out.println("already found doc="+docNode.toPrettyString());
                                failed.set(true);
                                return;
                            } else {
                                actual.add(docNode);
                            }
                        }
                        // System.out.println("finish batch="+event.getJobBatchNumber());
                    } catch (Throwable e) {
                        failed.set(true);
                        System.out.println(e.getMessage());
                        e.printStackTrace(System.out);
                    }
                })
                .onFailure((event, throwable) -> {
                    failed.set(true);
                    System.out.println("failed batch="+event.getJobBatchNumber()+
                            " from "+event.getLowerBound()+" through "+event.getUpperBound());
                });

        moveMgr.startJob(rowBatcher);
        rowBatcher.awaitCompletion();
        // System.out.println("stopped="+rowBatcher.isStopped());

        assertEquals("test failed with exception", false, failed.get());
        assertEquals("mismatch on row estimate", expectedDoc.size(), rowBatcher.getRowEstimate());
        assertEquals("mismatch on actual rows size", expectedDoc.size(), actual.size());
        assertEquals("mismatch on actual docs", expectedDoc, actual);
    }
}