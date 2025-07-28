/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
import com.marklogic.client.io.*;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RawQueryDSLPlan;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.test.Common;
import com.marklogic.client.type.PlanColumn;
import com.marklogic.client.type.PlanSystemColumn;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class RowBatcherTest {
    private final static String TEST_DIR = "/test/rowbatch/unit/";
    private final static String TEST_COLLECTION = TEST_DIR+"codes";
    private final static String TABLE_NS_URI = "http://marklogic.com/table";

    private final static int TEST_DOC_COUNT = 120;

    private static DatabaseClient      db      = Common.connect();
    private static JSONDocumentManager docMgr  = db.newJSONDocumentManager();
    private static DataMovementManager moveMgr = db.newDataMovementManager();

    private static ObjectMapper mapper = new ObjectMapper();

    private static Set<String>   expectedFull = new HashSet<>();
    private static Set<JsonNode> expectedDoc  = new HashSet<>();

    @BeforeAll
    public static void beforeClass() throws Exception {
        setupIndex();
        setupDocuments();
    }
    private static void setupIndex() {
        final String tdeUri = TEST_COLLECTION+".tdex";

        DatabaseClient schemasDB = Common.newClientBuilder()
			.withDatabase("java-unittest-schemas")
			.withUsername(Common.SERVER_ADMIN_USER)
			.withPassword(Common.SERVER_ADMIN_PASS)
			.build();
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
    @AfterAll
    public static void afterClass() {
        QueryManager queryMgr = db.newQueryManager();
        DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
        deleteQuery.setCollections(TEST_COLLECTION);
        queryMgr.delete(deleteQuery);
    }

    @Test
    public void testJsonRows1Thread() throws Exception {
        runJsonRowsTest(jsonBatcher(1));
    }
    @Test
    public void testJsonRows1ThreadForDB() throws Exception {
        runJsonRowsTest(jsonBatcher(
                Common.newClientBuilder().withDatabase("java-unittest").build().newDataMovementManager(),
                1));
    }
    @Test
    public void testJsonRows3Threads() throws Exception {
        runJsonRowsTest(jsonBatcher(3));
    }
    @Test
    public void testJsonRowsForest2Threads() throws Exception {
        runJsonRowsTest(jsonBatcher(moveMgr.readForestConfig().listForests().length * 2));
    }
    @Test
    public void testJsonDocs1Thread() throws Exception {
        runDocsTest(jsonBatcher(1));
    }

	@Test
	void noRowsReturned() {
		RowBatcher<JsonNode> rowBatcher = jsonBatcher(1);
		RowManager rowMgr = rowBatcher.getRowManager();
		RawQueryDSLPlan plan = rowMgr.newRawQueryDSLPlan(
			new StringHandle("op.fromView('rowBatcherUnitTest', 'code').where(op.eq(op.col('field1'), 12345))"));

		List<JsonNode> results = new ArrayList<>();
		rowBatcher.withBatchView(plan).onSuccess(batch -> results.add(batch.getRowsDoc()));
		moveMgr.startJob(rowBatcher);
		rowBatcher.awaitCompletion();
		moveMgr.stopJob(rowBatcher);

		assertEquals(0, results.size(), "Expecting no results as the Optic query shouldn't match any rows; " +
			"also expecting no error to occur.");
	}

    @Test
    public void testJsonDocs3Threads() throws Exception {
        runDocsTest(jsonBatcher(3));
    }
    @Test
    public void testJsonDocsForest2Threads() throws Exception {
        runDocsTest(jsonBatcher(moveMgr.readForestConfig().listForests().length * 2));
    }
    @Test
    public void testJsonRows3ThreadsPointInTime() throws Exception {
        runJsonRowsTest(jsonBatcher(3), true);
    }
    @Test
    public void testJsonRows1ThreadRawAST() throws Exception {
        runJsonRowsTest(jsonBatcher(1), RawPlanDefinition.class);
    }
    @Test
    public void testJsonRows1ThreadRawQueryDSL() throws Exception {
        runJsonRowsTest(jsonBatcher(1), RawQueryDSLPlan.class);
    }
    @Test
    public void testXmlRows3Threads() throws Exception {
        runXmlRowsTest(xmlBatcher(3));
    }
    @Test
    public void testCsvRows3Threads() throws Exception {
        runCsvRowsTest(csvBatcher(3));
    }
    /* TODO: style tests
     */
    private RowBatcher<JsonNode> jsonBatcher(int threads) {
        return jsonBatcher(moveMgr, threads);
    }
    private RowBatcher<JsonNode> jsonBatcher(DataMovementManager moveMgr, int threads) {
        return moveMgr.newRowBatcher(new JacksonHandle())
                        .withBatchSize(30)
                        .withThreadCount(threads);
    }
    private RowBatcher<Document> xmlBatcher(int threads) {
		// Bumped the batch size to MAX_VALUE to ensure that at least one test for RowBatcher uses a single batch. This
		// ensures that a change made to RowBatcherImpl to only log the batch size when batch count is > 1 works
		// properly when there's a single batch.
        return moveMgr.newRowBatcher(new DOMHandle())
                .withBatchSize(Integer.MAX_VALUE)
                .withThreadCount(threads);
    }
    private RowBatcher<String> csvBatcher(int threads) {
        return moveMgr.newRowBatcher(new StringHandle().withFormat(Format.TEXT).withMimetype("text/csv"))
                .withBatchSize(30)
                .withThreadCount(threads);
    }
    private void runJsonRowsTest(RowBatcher<JsonNode> rowBatcher) throws Exception {
        runJsonRowsTest(rowBatcher, false, PlanBuilder.ModifyPlan.class);
    }
    private void runJsonRowsTest(RowBatcher<JsonNode> rowBatcher, boolean consistentSnapshot) throws Exception {
        runJsonRowsTest(rowBatcher, consistentSnapshot, PlanBuilder.ModifyPlan.class);
    }
    private void runJsonRowsTest(RowBatcher<JsonNode> rowBatcher, Class<? extends PlanBuilder.Plan> planType) throws Exception {
        runJsonRowsTest(rowBatcher, false, planType);
    }
    private void runJsonRowsTest(
            RowBatcher<JsonNode> rowBatcher, boolean consistentSnapshot, Class<? extends PlanBuilder.Plan> planType
    ) {
        if (consistentSnapshot) {
            rowBatcher.withConsistentSnapshot();
        }
        String addedDocUri = consistentSnapshot ? makeDocUri(TEST_DOC_COUNT) : null;

        RowManager rowMgr = rowBatcher.getRowManager();
        rowMgr.setDatatypeStyle(RowManager.RowSetPart.HEADER);

        if (planType == null || PlanBuilder.ModifyPlan.class.isAssignableFrom(planType)) {
            rowBatcher.withBatchView(
                    rowMgr.newPlanBuilder()
                          .fromView("rowBatcherUnitTest", "code", "")
            );
        } else if (RawPlanDefinition.class.isAssignableFrom(planType)) {
            rowBatcher.withBatchView(
                    rowMgr.newRawPlanDefinition(
                            rowMgr.newPlanBuilder()
                                  .fromView("rowBatcherUnitTest", "code", "")
                                  .export(new StringHandle())
                    ));
        } else if (RawQueryDSLPlan.class.isAssignableFrom(planType)) {
            rowBatcher.withBatchView(
                    rowMgr.newRawQueryDSLPlan(
                            new StringHandle("op.fromView('rowBatcherUnitTest', 'code', '')")
                    ));
        } else {
            throw new IllegalArgumentException("unknown plan type: "+planType.getName());
        }

        AtomicBoolean addedDoc = consistentSnapshot ? new AtomicBoolean(false) : null;

        Set<String> actual = new ConcurrentSkipListSet<>();
        AtomicBoolean failed = new AtomicBoolean(false);
        rowBatcher.onSuccess(event -> {
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
                });

        moveMgr.startJob(rowBatcher);
        rowBatcher.awaitCompletion();
        // System.out.println("stopped="+rowBatcher.isStopped());

        if (consistentSnapshot) {
            assertNotNull(rowBatcher.getServerTimestamp(),
				"The RowBatcher should make the timestamp available so that the client can perform " +
					"additional operations using that timestamp after the job has completed. For example, " +
					"the client may wish to lookup the highest dateTime value in an index at the timestamp " +
					"so that a future job can constrain to documents with a dateTime greater than the " +
					"looked-up value, thus allowing for a 'Only process new records' feature.");
            docMgr.delete(addedDocUri);
        } else {
            assertNull(rowBatcher.getServerTimestamp(),
				"If withConsistentSnapshot is not used, getServerTimestamp() should return null since no " +
					"server timestamp would have been captured");
        }

        assertEquals( false, failed.get());
        assertEquals( expectedFull.size(), rowBatcher.getRowEstimate());
        assertEquals( expectedFull.size(), actual.size());
        assertEquals( expectedFull, actual);
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
                });

        moveMgr.startJob(rowBatcher);
        rowBatcher.awaitCompletion();
        // System.out.println("stopped="+rowBatcher.isStopped());

        assertEquals( false, failed.get());
        assertEquals( expectedDoc.size(), rowBatcher.getRowEstimate());
        assertEquals( expectedDoc.size(), actual.size());
        assertEquals( expectedDoc, actual);
    }
    private void runXmlRowsTest(RowBatcher<Document> rowBatcher) throws Exception {
        RowManager rowMgr = rowBatcher.getRowManager();
        rowMgr.setDatatypeStyle(RowManager.RowSetPart.HEADER);

        PlanBuilder planBuilder = rowMgr.newPlanBuilder();
        PlanBuilder.ModifyPlan plan =
                planBuilder.fromView("rowBatcherUnitTest", "code", "");

        Set<String> actual = new ConcurrentSkipListSet<>();
        AtomicBoolean failed = new AtomicBoolean(false);
        rowBatcher.withBatchView(plan)
                .onSuccess(event -> {
                    /* System.out.println("succeeded batch="+event.getJobBatchNumber()+
                            " from "+event.getLowerBound()+" through "+event.getUpperBound()+
                            ((event.getJobBatchNumber() == 1) ? "\n"+Common.testDocumentToString(event.getRowsDoc()) : "")); */
                    try {
                        NodeList rows = event.getRowsDoc().getDocumentElement().getElementsByTagNameNS(TABLE_NS_URI, "rows");
                        if (rows == null || rows.getLength() < 1) {
                            System.out.println("no rows for batch="+event.getJobBatchNumber()+
                                    " from "+event.getLowerBound()+" through "+event.getUpperBound());
                            failed.set(true);
                            return;
                        }
                        // System.out.println("starting batch="+event.getJobBatchNumber()+" for "+rows.size()+" rows");
                        for (int i=0; i < rows.getLength(); i++) {
                            NodeList cells = ((Element) rows.item(i)).getElementsByTagNameNS(TABLE_NS_URI, "cell");
                            if (cells == null || cells.getLength() < 1) {
                                System.out.println("no cells for row "+i+
                                        " in batch="+event.getJobBatchNumber()+
                                        " from "+event.getLowerBound()+" through "+event.getUpperBound());
                                failed.set(true);
                                return;
                            }
                            boolean[] fields = new boolean[4];
                            Arrays.fill(fields, false);
                            for (int j=0; j < cells.getLength(); j++) {
                                Element cell = (Element) cells.item(j);
                                String cellName = cell.getAttribute("name");
                                switch (cellName) {
                                case "full":
                                    String cellText = cell.getTextContent();
                                    if (actual.contains(cellText)) {
                                        System.out.println("already found full="+cellText);
                                        failed.set(true);
                                        return;
                                    } else {
                                        actual.add(cellText);
                                    }
                                    break;
                                case "field1":
                                case "field2":
                                case "field3":
                                case "field4":
                                    int k = Integer.parseInt(cellName.substring(cellName.length() - 1)) - 1;
                                    if (k < fields.length) {
                                        fields[k] = true;
                                    } else {
                                        System.out.println("field mismatch "+cellName+" in row "+i+
                                                " of batch="+event.getJobBatchNumber()+
                                                " from "+event.getLowerBound()+" through "+event.getUpperBound());
                                        failed.set(true);
                                        return;
                                    }
                                    break;
                                case "rowid":
                                    System.out.println("has rowid column");
                                    failed.set(true);
                                    break;
                                default:
                                    System.out.println("unknown cell "+cellName+" in row "+i+" of batch="+event.getJobBatchNumber()+
                                            " from "+event.getLowerBound()+" through "+event.getUpperBound());
                                    failed.set(true);
                                    return;
                                }
                            }
                            for (int j=0; j < fields.length; j++) {
                                if (!(fields[j])) {
                                    System.out.println("failed to find field"+j+" cell in row "+i+
                                            " of batch="+event.getJobBatchNumber()+
                                            " from "+event.getLowerBound()+" through "+event.getUpperBound());
                                    failed.set(true);
                                    return;
                                }
                            }
                        }
                        // System.out.println("finish batch="+event.getJobBatchNumber());
                    } catch (Throwable e) {
                        failed.set(true);
                        System.out.println(e.getMessage());
                        e.printStackTrace(System.out);
                    }
                });

        moveMgr.startJob(rowBatcher);
        rowBatcher.awaitCompletion();
        // System.out.println("stopped="+rowBatcher.isStopped());

        assertEquals( false, failed.get());
        assertEquals( expectedFull.size(), rowBatcher.getRowEstimate());
        assertEquals( expectedFull.size(), actual.size());
        assertEquals( expectedFull, actual);
    }
    private void runCsvRowsTest(RowBatcher<String> rowBatcher) throws Exception {
        RowManager rowMgr = rowBatcher.getRowManager();
        rowMgr.setDatatypeStyle(RowManager.RowSetPart.HEADER);

        PlanBuilder planBuilder = rowMgr.newPlanBuilder();
        PlanBuilder.ModifyPlan plan =
                planBuilder.fromView("rowBatcherUnitTest", "code", "");

        Pattern commaSplitter = Pattern.compile(",");

        Set<String> actual = new ConcurrentSkipListSet<>();
        AtomicBoolean failed = new AtomicBoolean(false);
        rowBatcher.withBatchView(plan)
                .onSuccess(event -> {
                    /* System.out.println("succeeded batch="+event.getJobBatchNumber()+
                            " from "+event.getLowerBound()+" through "+event.getUpperBound()+
                            ((event.getJobBatchNumber() == 1) ? "\n"+event.getRowsDoc() : "")); */
                    try {
                        BufferedReader reader = new BufferedReader(new StringReader(event.getRowsDoc()));
                        String firstLine = reader.readLine();
                        // System.out.println(firstLine);
                        String[] columns = commaSplitter.split(firstLine);
                        if (columns.length != 5) {
                            System.out.println("unexpected number of columns "+columns.length+
                                    " for batch="+event.getJobBatchNumber()+
                                    " from "+event.getLowerBound()+" through "+event.getUpperBound());
                        }
                        AtomicInteger fullColNum = new AtomicInteger(-1);
                        for (int i=0; i < columns.length; i++) {
                            String colName = columns[i];
                            switch (colName) {
                            case "full":
                                fullColNum.set(i);
                                break;
                            case "field1":
                            case "field2":
                            case "field3":
                            case "field4":
                                break;
                            case "rowid":
                                System.out.println("has rowid column");
                                failed.set(true);
                                break;
                            default:
                                System.out.println("unknown column "+colName+" of batch="+event.getJobBatchNumber()+
                                        " from "+event.getLowerBound()+" through "+event.getUpperBound());
                                failed.set(true);
                                return;
                            }
                        }
                        // System.out.println("starting batch="+event.getJobBatchNumber());
                        reader.lines().forEach(line -> {
                            // System.out.println(line);
                            String[] cells = commaSplitter.split(line);
                            if (cells.length < columns.length) {
                                System.out.println("unexpected number of cells "+cells.length+
                                        " in row for for batch="+event.getJobBatchNumber()+
                                        " from "+event.getLowerBound()+" through "+event.getUpperBound());
                            }
                            String full = cells[fullColNum.get()];
                            if (actual.contains(full)) {
                                System.out.println("already found full="+full);
                                failed.set(true);
                                return;
                            } else {
                                actual.add(full);
                            }
                        });
                        // System.out.println("finish batch="+event.getJobBatchNumber());
                    } catch (Throwable e) {
                        failed.set(true);
                        System.out.println(e.getMessage());
                        e.printStackTrace(System.out);
                    }
                });

        moveMgr.startJob(rowBatcher);
        rowBatcher.awaitCompletion();
        // System.out.println("stopped="+rowBatcher.isStopped());

        assertEquals( false, failed.get());
        assertEquals( expectedFull.size(), rowBatcher.getRowEstimate());
        assertEquals( expectedFull.size(), actual.size());
        assertEquals( expectedFull, actual);
    }
}
