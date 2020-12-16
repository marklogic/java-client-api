package com.marklogic.client.datamovement.functionaltests;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCtsQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class TestSplitters  extends BasicJavaClientREST {
    private static String dbName = "TestSplittersDB";
    private static String[] fNames = {"TestSplittersDB-1"};
    private static DatabaseClient client = null;
    private static DataMovementManager dmManager = null;
    private static String datasource = "src/test/java/com/marklogic/client/functionaltest/data/splitter/";

    private static DatabaseClient dbClient;
    private static String host = null;
    private static String user = "admin";
    private static String password = "admin";
    private static String delim;

    @BeforeClass
    public static void setUp() throws Exception {
        System.out.println("In setup");
        delim = System.lineSeparator();
        configureRESTServer(dbName, fNames);
        setupAppServicesConstraint(dbName);
        createUserRolesWithPrevilages("test-eval", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
        createRESTUser("eval-user", "x", "test-eval", "rest-admin", "rest-writer", "rest-reader", "rest-extension-user", "manage-user");

        client = getDatabaseClient("eval-user", "x", getConnType());
        dmManager = client.newDataMovementManager();
    }

    @AfterClass
    public static void testCleanUp() throws Exception {
        associateRESTServerWithDB(getRestServerName(), "Documents");
        deleteDB(dbName);
        deleteForest(fNames[0]);
        System.out.println("Running clear script");
    }

    @Test
    public void testBasicLineSplitInJson() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException, KeyManagementException, NoSuchAlgorithmException {
        System.out.println("Running testBasicLineSplitInJson");
        FileInputStream jsonfs = null;
        String fileName1 = "json-splitter-1.json";
        String docIdPrefix = "/LS-DocMgr-";
        String dmsdkIdPrefix = "/LS-DMDSK-";
        AtomicInteger id = new AtomicInteger(0);
        AtomicInteger cnt1 = new AtomicInteger(0);
        DocumentManager docMgr = client.newJSONDocumentManager();
        DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
        DocumentMetadataHandle dmsdkMeta = new DocumentMetadataHandle().withCollections("LS-DMSDK").withProperty("docMeta-1", "true");

        WriteBatcher wbatcher = dmManager.newWriteBatcher();
        wbatcher.withBatchSize(1);
        metadataHandle1.getCollections().addAll("LS-DocMgr");
        docMgr.setContentFormat(Format.JSON);
        try {
            jsonfs = new FileInputStream(datasource + fileName1);
            LineSplitter splitter = new LineSplitter();
            Stream<StringHandle> contentStream = splitter.split(jsonfs);
            WriteBatcher batcher = dmManager.newWriteBatcher();
            batcher.withBatchSize(5);
            contentStream.forEach(s -> {
                // Doc Manager write
                //System.out.println("handle is " + s);
                docMgr.write(docIdPrefix + id.addAndGet(1), metadataHandle1, s);
                // Batcher add
                batcher.add(dmsdkIdPrefix + id.get(), dmsdkMeta, s);
            });
            dmManager.startJob(batcher);
            batcher.flushAndWait();
            // Verify docs
            QueryBatcher queryBatcherdMgr = dmManager.newQueryBatcher(
                    new StructuredQueryBuilder().collection("LS-DMSDK"))
                    .withBatchSize(20)
                    .withThreadCount(1)
                    .onUrisReady((batch) -> {
                        // Should be getting all docs in a batch
                        cnt1.set(batch.getItems().length);
                    });
            dmManager.startJob(queryBatcherdMgr);
            queryBatcherdMgr.awaitCompletion();
            assertEquals(7, cnt1.get());
        } catch (Exception ex) {
            System.out.println("Exceptions thrown from testBasicLineSplitInJson " + ex.getMessage());
        } finally {
            try {
                clearDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
            jsonfs.close();
        }
    }

    @Test
    public void testLargeJsonLines() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException, KeyManagementException, NoSuchAlgorithmException {
        System.out.println("Running testLargeJsonLines");
        File tempJsonFile = null;
        FileInputStream jsonfs = null;
        FileInputStream jsonfs2 = null;
        String docIdPrefix = "/LS-DocMgrLarge-";
        String collectionName = "LS-LARGE";
        int nDocs = 4096;

        AtomicInteger id = new AtomicInteger(0);
        AtomicInteger cnt1 = new AtomicInteger(0);
        AtomicInteger cnt2 = new AtomicInteger(0);
        try {
            tempJsonFile = File.createTempFile("TestLargeJsons", ".json");
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempJsonFile));
            DocumentManager docMgr = client.newJSONDocumentManager();
            DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle().withCollections(collectionName).withProperty("docMeta-1", "true");

            String beg = "{\"animal\":\"dog";
            String end = "\", \"says\":\"woof\"}";
            for (int i = 0; i < nDocs; i++) {
                String line = new String(beg + i + end);
                //System.out.println("Line is " + line);
                bw.write(line);
                bw.write(delim);
            }
            bw.close();

            jsonfs = new FileInputStream(tempJsonFile);
            LineSplitter splitter = new LineSplitter();
            Charset cs = Charset.forName("UTF-8");
            Stream<StringHandle> contentStream = splitter.split(jsonfs, cs);
            contentStream.forEach(s -> {
                // Doc Manager write
                //System.out.println("handle is " + s);
                docMgr.write(docIdPrefix + id.addAndGet(1), metadataHandle1, s);
            });
            // Verify docs
            QueryBatcher queryBatcherdMgr = dmManager.newQueryBatcher(
                    new StructuredQueryBuilder().collection(collectionName))
                    .withBatchSize(2000)
                    .withThreadCount(2)
                    .onUrisReady((batch) -> {
                        cnt1.set(cnt1.get() + batch.getItems().length);
                    });
            dmManager.startJob(queryBatcherdMgr);
            queryBatcherdMgr.awaitCompletion();
            assertEquals(nDocs, cnt1.get());

            // Verify splitWriteOperations. Use WriteBatcher
            jsonfs2 = new FileInputStream(tempJsonFile);
            LineSplitter splitter2 = new LineSplitter();

            Stream<DocumentWriteOperation> docsStream = splitter2.splitWriteOperations(jsonfs2, "splitted.json");
            WriteBatcher batcher = dmManager.newWriteBatcher();
            String woCollectionStr = "From-WriteOps";
            DocumentMetadataHandle meta2 = new DocumentMetadataHandle().withCollections(woCollectionStr).withQuality(8);
            batcher.withBatchSize(2000).withDefaultMetadata(meta2);

            dmManager.startJob(batcher);
            batcher.addAll(docsStream);
            batcher.flushAndWait();

            // Verify docs
            QueryBatcher queryBatcherdMgr1 = dmManager.newQueryBatcher(
                    new StructuredQueryBuilder().collection(woCollectionStr))
                    .withBatchSize(2000)
                    .withThreadCount(2)
                    .onUrisReady((batch) -> {
                        cnt2.set(cnt2.get() + batch.getItems().length);
                    });
            dmManager.startJob(queryBatcherdMgr1);
            queryBatcherdMgr1.awaitCompletion();
            assertEquals(nDocs, cnt2.get());
        } catch (Exception ex) {
            System.out.println("Exceptions thrown from testLargeJsonLines " + ex.getMessage());
        } finally {
            if (jsonfs != null)
                jsonfs.close();
            if (jsonfs2 != null)
                jsonfs2.close();
            tempJsonFile.deleteOnExit();
            try {
                clearDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testCharSetWithJson() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException, KeyManagementException, NoSuchAlgorithmException {
        System.out.println("Running testCharSetWithJson");
        FileInputStream jsonfs = null;
        String fileName1 = "json-splitter-2.json";
        String docIdPrefix = "/LS-DocMgr-Charset-";

        AtomicInteger id = new AtomicInteger(0);
        AtomicInteger cnt1 = new AtomicInteger(0);
        DocumentManager docMgr = client.newJSONDocumentManager();
        DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle().withCollections("LS-CHARSET").withProperty("docMeta-1", "true");
        docMgr.setContentFormat(Format.JSON);
        try {
            jsonfs = new FileInputStream(datasource + fileName1);
            LineSplitter splitter = new LineSplitter();
            Charset cs = Charset.forName("UTF-8");
            Stream<StringHandle> contentStream = splitter.split(jsonfs, cs);
            contentStream.forEach(s -> {
                // Doc Manager write
                //System.out.println("handle is " + s);
                docMgr.write(docIdPrefix + id.addAndGet(1), metadataHandle1, s);
            });
            // Verify docs
            QueryBatcher queryBatcherdMgr = dmManager.newQueryBatcher(
                    new StructuredQueryBuilder().collection("LS-CHARSET"))
                    .withBatchSize(20)
                    .withThreadCount(1)
                    .onUrisReady((batch) -> {
                        // Should be getting all docs in a batch
                        cnt1.set(batch.getItems().length);
                    });
            dmManager.startJob(queryBatcherdMgr);
            queryBatcherdMgr.awaitCompletion();
            assertEquals(8, cnt1.get());
        } catch (Exception ex) {
            System.out.println("Exceptions thrown from testCharSetWithJson " + ex.getMessage());
        } finally {
            jsonfs.close();
            try {
                clearDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // LineSplitter and multiple threads
    @Test
    public void testSplitterWithMultipleThreads() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException, KeyManagementException, NoSuchAlgorithmException {
        System.out.println("Running testSplitterWithMultipleThreads");

        File tempJsonFile1 = null;
        File tempJsonFile2 = null;
        String docIdPrefix = "/LS-Multi-Thread-";
        String collectionName = "LS-Multi-Thread";

        AtomicInteger cnt1 = new AtomicInteger(0);

        FileInputStream jsonfs1;
        final FileInputStream jsonfs2;

        final int nDocs = 4096;
        final AtomicInteger id1 = new AtomicInteger(0);
        final AtomicInteger id2 = new AtomicInteger(8193);

        try {
            tempJsonFile1 = File.createTempFile("TestLargeJsons1", ".json");
            BufferedWriter bwJson1 = new BufferedWriter(new FileWriter(tempJsonFile1));
            DocumentManager docMgrJson1 = client.newJSONDocumentManager();
            DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle().withCollections(collectionName).withProperty("docMeta-1", "true");

            String beg = "{\"animal\":\"dog";
            String end = "\", \"says\":\"woof\"}";
            for (int i = 0; i < nDocs; i++) {
                String line = new String(beg + i + end);
                bwJson1.write(line);
                bwJson1.write(delim);
            }
            bwJson1.close();

            tempJsonFile2 = File.createTempFile("TestLargeJsons2", ".json");
            BufferedWriter bwJson2 = new BufferedWriter(new FileWriter(tempJsonFile2));

            for (int i = 0; i < nDocs; i++) {
                String line = new String("{\"animal\":\"parrot" + i + "\", \"says\":\"Hello\"}");
                bwJson2.write(line);
                bwJson2.write(delim);
            }
            bwJson2.close();

            jsonfs1 = new FileInputStream(tempJsonFile1);
            jsonfs2 = new FileInputStream(tempJsonFile2);

            LineSplitter splitter = new LineSplitter();

            SplitRunnable sr1 = new SplitRunnable();
            sr1.setDocMgrJson(docMgrJson1);
            sr1.setFileStream(jsonfs1);
            sr1.setSplitter(splitter);
            sr1.setId(id1);
            sr1.setDocIdPrefix(docIdPrefix);
            sr1.setMetadataHandle(metadataHandle1);

            SplitRunnable sr2 = new SplitRunnable();
            sr2.setDocMgrJson(docMgrJson1);
            sr2.setFileStream(jsonfs2);
            sr2.setSplitter(splitter);
            sr2.setId(id2);
            sr2.setDocIdPrefix(docIdPrefix);
            sr2.setMetadataHandle(metadataHandle1);

            Thread t1 = new Thread(sr1);
            t1.setName("ThreadOne");
            Thread t2 = new Thread(sr2);
            t2.setName("ThreadTwo");

            t1.start();
            t2.start();

            t1.join();
            t2.join();

            Thread.sleep(1000);
            // Verify docs
            QueryBatcher queryBatcherdMgr = dmManager.newQueryBatcher(
                    new StructuredQueryBuilder().collection(collectionName))
                    .withBatchSize(2000)
                    .withThreadCount(2)
                    .onUrisReady((batch) -> {
                        cnt1.set(cnt1.get() + batch.getItems().length);
                    });
            dmManager.startJob(queryBatcherdMgr);
            queryBatcherdMgr.awaitCompletion();
            assertEquals(2 * nDocs, cnt1.get());
            if (jsonfs1 != null)
                jsonfs1.close();
            if (jsonfs2 != null)
                jsonfs2.close();
            docMgrJson1 = null;
        } catch (Exception ex) {
            System.out.println("Exceptions thrown from testSplitterWithMultipleThreads " + ex.getMessage());
        } finally {
            if (tempJsonFile1 != null)
                tempJsonFile1.deleteOnExit();
            if (tempJsonFile2 != null)
                tempJsonFile2.deleteOnExit();
            try {
                clearDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testCharSetWithXml() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException, KeyManagementException, NoSuchAlgorithmException {
        System.out.println("Running testCharSetWithXml");
        FileInputStream jsonfs = null;
        String fileName1 = "xml-splitter-1.txt";
        String docIdPrefix = "/LS-DocMgr-Charset-xml-";

        AtomicInteger id = new AtomicInteger(0);
        AtomicInteger cnt1 = new AtomicInteger(0);
        DocumentManager docMgr = client.newXMLDocumentManager();
        DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle().withCollections("LS-CHARSET-XML").withProperty("docMeta-1", "true");

        try {
            jsonfs = new FileInputStream(datasource + fileName1);
            LineSplitter splitter = new LineSplitter();
            Charset cs = Charset.forName("UTF-8");
            Stream<StringHandle> contentStream = splitter.split(jsonfs, cs);
            contentStream.forEach(s -> {
                // Doc Manager write
                //System.out.println("handle is " + s);
                docMgr.write(docIdPrefix + id.addAndGet(1), metadataHandle1, s);
            });
            // Verify docs
            QueryBatcher queryBatcherdMgr = dmManager.newQueryBatcher(
                    new StructuredQueryBuilder().collection("LS-CHARSET-XML"))
                    .withBatchSize(20)
                    .withThreadCount(1)
                    .onUrisReady((batch) -> {
                        // Should be getting all docs in a batch
                        cnt1.set(batch.getItems().length);
                    });
            dmManager.startJob(queryBatcherdMgr);
            queryBatcherdMgr.awaitCompletion();
            assertEquals(5, cnt1.get());
        } catch (Exception ex) {
            System.out.println("Exceptions thrown from testCharSetWithXml " + ex.getMessage());
        } finally {
            jsonfs.close();
            try {
                clearDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Negative cases
    @Test
    public void testIncorrectCharSetWithJson() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException, KeyManagementException, NoSuchAlgorithmException {
        System.out.println("Running testIncorrectCharSetWithJson");
        FileInputStream jsonfs = null;
        String fileName1 = "json-splitter-2.json";
        String docIdPrefix = "/LS-DocMgr-Bad-Charset-";
        String msg = null;

        AtomicInteger id = new AtomicInteger(0);
        DocumentManager docMgr = client.newJSONDocumentManager();
        DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle().withCollections("LS-Bad-CHARSET").withProperty("docMeta-1", "true");
        docMgr.setContentFormat(Format.JSON);
        try {
            jsonfs = new FileInputStream(datasource + fileName1);
            LineSplitter splitter = new LineSplitter();
            Charset cs = Charset.forName("UTF-16");
            Stream<StringHandle> contentStream = splitter.split(jsonfs, cs);
            contentStream.forEach(s -> {
                // Doc Manager write
                //System.out.println("handle is " + s);
                docMgr.write(docIdPrefix + id.addAndGet(1), metadataHandle1, s);
            });
        } catch (Exception ex) {
            msg = ex.getMessage();
        } finally {
            System.out.println("Exceptions thrown from testIncorrectCharSetWithJson " + msg);
            assertTrue(msg.contains("XDMP-JSONDOC: Document is not JSON"));
            jsonfs.close();
            try {
                clearDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // To test SplitterWriteOperations with multiple threads and verify name
    @Test
    public void testWriteOpsMultipleThreads() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException, KeyManagementException, NoSuchAlgorithmException {
        System.out.println("Running testSplitterWithMultipleThreads");

        File tempJsonFile1 = null;
        File tempJsonFile2 = null;
        String collectionName = "LS-Multi-Thread-WriteOps";

        AtomicInteger cnt1 = new AtomicInteger(0);

        FileInputStream jsonfs1;
        final FileInputStream jsonfs2;

        final int nDocs = 4096;
        final AtomicInteger id1 = new AtomicInteger(0);
        final AtomicInteger id2 = new AtomicInteger(8193);

        try {
            tempJsonFile1 = File.createTempFile("TestWriteOps1", ".json");
            BufferedWriter bwJson1 = new BufferedWriter(new FileWriter(tempJsonFile1));
            DocumentManager docMgrJson1 = client.newJSONDocumentManager();
            dmManager = client.newDataMovementManager();
            WriteBatcher wbatcher1 = dmManager.newWriteBatcher();

            DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle().withCollections(collectionName).withProperty("docMeta-1", "true");
            wbatcher1.withDefaultMetadata(metadataHandle1);

            String beg1 = "{\"animal\":\"camel";
            String end1 = "\", \"says\":\"brey\"}";
            for (int i = 0; i < nDocs; i++) {
                String line = new String(beg1+ i + end1);
                bwJson1.write(line);
                bwJson1.write(delim);
            }
            bwJson1.close();

            tempJsonFile2 = File.createTempFile("TestWriteOps2", ".json");
            BufferedWriter bwJson2 = new BufferedWriter(new FileWriter(tempJsonFile2));

            String beg2 = "{\"animal\":\"sunbird";
            String end2 = "\", \"says\":\"chirps\"}";
            for (int i = 0; i < nDocs; i++) {
                String line = new String(beg2 + i + end2);
                bwJson2.write(line);
                bwJson2.write(delim);
            }
            bwJson2.close();

            jsonfs1 = new FileInputStream(tempJsonFile1);
            jsonfs2 = new FileInputStream(tempJsonFile2);

            LineSplitter splitter = new LineSplitter();

            SplitWriteOpsRunnable sr1 = new SplitWriteOpsRunnable();
            sr1.setFileStream(jsonfs1);
            sr1.setBatcher(wbatcher1);
            sr1.setSplitter(splitter);

            SplitWriteOpsRunnable sr2 = new SplitWriteOpsRunnable();
            sr2.setFileStream(jsonfs2);
            sr2.setBatcher(wbatcher1);
            sr2.setSplitter(splitter);

            Thread t1 = new Thread(sr1);
            t1.setName("ThreadOne");
            Thread t2 = new Thread(sr2);
            t2.setName("ThreadTwo");

            t1.start();
            t2.start();

            t1.join();
            t2.join();

            Thread.sleep(1000);
            // Verify docs count
            QueryBatcher queryBatcherdMgr = dmManager.newQueryBatcher(
                    new StructuredQueryBuilder().collection(collectionName))
                    .withBatchSize(20)
                    //.withThreadCount(1)
                    .onUrisReady((batch) -> {
                        cnt1.set(cnt1.get() + batch.getItems().length);
                    });
            dmManager.startJob(queryBatcherdMgr);
            queryBatcherdMgr.awaitCompletion();
            assertEquals(2 * nDocs, cnt1.get());
            // Verify the doc uri.
            QueryManager queryMgr = client.newQueryManager();
            JSONDocumentManager jsonDocMgr = client.newJSONDocumentManager();

            String wordQuery = "<cts:word-query xmlns:cts=\"http://marklogic.com/cts\">" +
                    "<cts:text>sunbird1048</cts:text></cts:word-query>";
            StringHandle handle = new StringHandle().with(wordQuery);
            RawCtsQueryDefinition querydef = queryMgr.newRawCtsQueryDefinition(handle);

            // create result handle
            JacksonHandle resultsHandle = new JacksonHandle();
            queryMgr.search(querydef, resultsHandle);
            JsonNode result = resultsHandle.get();

            String uri1 = result.path("results").get(0).path("uri").asText();
            assertTrue(uri1.contains("WriteOpsRunnable"));
            String text = jsonDocMgr.read(uri1, new StringHandle()).get();
            assertTrue(text.contains("{\"animal\":\"sunbird1048\", \"says\":\"chirps\"}"));

            if (jsonfs1 != null)
                jsonfs1.close();
            if (jsonfs2 != null)
                jsonfs2.close();
            docMgrJson1 = null;
        } catch (Exception ex) {
            System.out.println("Exceptions thrown testSplitterWithMultipleThreads " + ex.getMessage());
        } finally {
            if (tempJsonFile1 != null)
                tempJsonFile1.deleteOnExit();
            if (tempJsonFile2 != null)
                tempJsonFile2.deleteOnExit();
            try {
                clearDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ////////////// JsonSplitter Tests //////////////
    @Test
    public void testJsonSplitterWriteOperations() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException, KeyManagementException, NoSuchAlgorithmException {
        System.out.println("Running testJsonSplitterWriteOperations");

        String fileName1 = "json-custom-1.json";
        String collectionName = "LS-Customized";
        String msg;
        FileInputStream fileInputStream1 = null;
        try {

        JSONSplitter splitter1 = JSONSplitter.makeArraySplitter();
        fileInputStream1 = new FileInputStream(new File(datasource + fileName1));
        Stream<DocumentWriteOperation> contentStream1 = splitter1.splitWriteOperations(fileInputStream1, "TestJson.json");

        DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle().withCollections(collectionName).withProperty("Author-ratings", "10.0");

        WriteBatcher batcher = dmManager.newWriteBatcher();
        batcher.withBatchSize(5).withDefaultMetadata(metadataHandle1);

        batcher.addAll(contentStream1);
        dmManager.startJob(batcher);
        batcher.flushAndWait();

        // create a search definition for assert
        QueryManager queryMgr = client.newQueryManager();
        JSONDocumentManager jsonDocMgr = client.newJSONDocumentManager();

        String wordQuery = "<cts:word-query xmlns:cts=\"http://marklogic.com/cts\">" +
                "<cts:text>first</cts:text></cts:word-query>";
        StringHandle handle = new StringHandle().with(wordQuery);
        RawCtsQueryDefinition querydef = queryMgr.newRawCtsQueryDefinition(handle);

        JacksonHandle resultsHandle = new JacksonHandle();
        queryMgr.search(querydef, resultsHandle);

        JsonNode result = resultsHandle.get();
        String uri1 = result.path("results").get(0).path("uri").asText();
        assertTrue(uri1.contains("TestJson1"));

        String text = jsonDocMgr.read(uri1, new StringHandle()).get();
        assertTrue(text.contains("{\"record\":\"first record\"}"));
        //System.out.println("text is " + text);
        String wordQuery1 = "<cts:word-query xmlns:cts=\"http://marklogic.com/cts\">" +
                    "<cts:text>Moby</cts:text></cts:word-query>";
        StringHandle handle1 = new StringHandle();
        handle1.with(wordQuery1);
        RawCtsQueryDefinition querydef1 = queryMgr.newRawCtsQueryDefinition(handle1);

        // create result handle
        JacksonHandle resultsHandle1 = new JacksonHandle();
        queryMgr.search(querydef1, resultsHandle1);

        // get the result
        JsonNode result1 = resultsHandle1.get();

        uri1 = result1.path("results").get(0).path("uri").asText();
        System.out.println("URI is " + uri1);
        assertTrue(uri1.contains("TestJson6"));
        StringHandle resH =  new StringHandle();
        String text1 = jsonDocMgr.read(uri1, resH).get();
        System.out.println("text is " + text1);
        assertTrue(text1.contains("{\"Herman\":\"Moby-Dick\"}"));

    }
    catch (Exception ex) {
        msg = ex.getMessage();
        System.out.println("Exceptions thrown from testJsonSplitterWriteOperations " + msg);
    } finally {
            if(fileInputStream1 != null)
             fileInputStream1.close();
            try {
                clearDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testJsonSplitterWithVisitor() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException, KeyManagementException, NoSuchAlgorithmException {
        System.out.println("Running testJsonSplitterWithVisitor");

        String fileName1 = "json-custom-2.json";
        String collectionName = "LS-Visitor";
        String docIdPrefix = "Visitor-";
        String msg;
        AtomicInteger id = new AtomicInteger(0);
        FileInputStream fileInputStream2 = null;
        JSONDocumentManager jsonDocMgr = client.newJSONDocumentManager();
        try {
            QAKeyVisitor visitor2 = new QAKeyVisitor("publish");
            JSONSplitter splitter2 = new JSONSplitter(visitor2);
            fileInputStream2 = new FileInputStream(new File(datasource + fileName1));
            Stream<StringHandle> contentStream2 = splitter2.split(fileInputStream2);

            DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle().withCollections(collectionName).withProperty("Author-ratings", "10.0");
            contentStream2.forEach(s -> {
                //System.out.println("Thread name " + Thread.currentThread().getName() + " has content " + s);
                jsonDocMgr.write(docIdPrefix + id.addAndGet(1), metadataHandle2, s);
            });
            // create a search definition for assert
            QueryManager queryMgr = client.newQueryManager();

            String wordQuery = "<cts:word-query xmlns:cts=\"http://marklogic.com/cts\">" +
                    "<cts:text>Blechtrommel</cts:text></cts:word-query>";
            StringHandle handle = new StringHandle().with(wordQuery);
            RawCtsQueryDefinition querydef = queryMgr.newRawCtsQueryDefinition(handle);

            JacksonHandle resultsHandle = new JacksonHandle();
            queryMgr.search(querydef, resultsHandle);

            JsonNode result = resultsHandle.get();
            String uri1 = result.path("results").get(0).path("uri").asText();
            assertTrue(uri1.contains("Visitor-1"));

            String text = jsonDocMgr.read(uri1, new StringHandle()).get();
            assertTrue(text.contains("{\"Grass\":\"Die Blechtrommel\"}"));
            System.out.println("text is " + text);
        }
        catch (Exception ex) {
            msg = ex.getMessage();
            System.out.println("Exceptions thrown testJsonSplitterWithVisitor " + msg);
        }
        finally {
            if(fileInputStream2 != null)
                fileInputStream2.close();
            try {
                clearDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testWithNonExistentVisitor() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException, KeyManagementException, NoSuchAlgorithmException {
        System.out.println("Running testWithNonExistentVisitor");

        String fileName1 = "json-custom-1.json";
        String collectionName = "LS-Non-Exists";
        String msg;
        FileInputStream fileInputStream1 = null;
        try {
            // Have non-existent property name
            QAKeyVisitor visitor1 = new QAKeyVisitor("Java");
            JSONSplitter splitter1 = new JSONSplitter(visitor1);
            fileInputStream1 = new FileInputStream(new File(datasource + fileName1));
            Stream<DocumentWriteOperation> contentStream1 = splitter1.split(fileInputStream1);
            long cnt = contentStream1.count();
            System.out.println("total is  " + cnt);
            assertEquals(0, cnt);
        }
        catch (Exception ex) {
            System.out.println("Exceptions thrown from testWithNonExistentVisitor " + ex.toString());
        }
        finally {
            if(fileInputStream1 != null)
                fileInputStream1.close();
            try {
                clearDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ////////////// XmlSplitter Tests //////////////

    @Test
    public void testXMLSplitterBasic() throws Exception {
        System.out.println("Running testXMLSplitterBasic");
        String fileName1 = "xml-splitter-2.xml";

        FileInputStream fileInputStream = null, fileInputStream1 = null, fileInputStream2 = null;
        try {
            String[] expected = new String[]{
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ml:OrderedDate xmlns:ml=\"http://www.mlqa.com/\">2020-07-10</ml:OrderedDate>",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ml:OrderedDate xmlns:ml=\"http://www.mlqa.com/\"/>",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ml:OrderedDate xmlns:ml=\"http://www.mlqa.com/\"/>",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ml:OrderedDate xmlns:ml=\"http://www.mlqa.com/\"> </ml:OrderedDate>",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ml:OrderedDate xmlns:ml=\"http://www.mlqa.com/\">2020-06-21</ml:OrderedDate>"
            };
            // Normal local name
            XMLSplitter splitter = XMLSplitter.makeSplitter("http://www.mlqa.com/", "OrderedDate");
            fileInputStream = new FileInputStream(new File(datasource + fileName1));
            Stream<StringHandle> contentStream = splitter.split(fileInputStream);

            StringHandle[] result = contentStream.toArray(size -> new StringHandle[size]);
            assertEquals(5, splitter.getCount());

            for (int i = 0; i < result.length; i++) {
                String row = result[i].get();
                //System.out.println("element is " + row);
                assertEquals(expected[i], row);
            }

            // Not a normal local name
            XMLSplitter splitter1 = XMLSplitter.makeSplitter("http://www.mlqa.com/", "ml:OrderedDate");
            fileInputStream1 = new FileInputStream(new File(datasource + fileName1));
            Stream<StringHandle> contentStream1 = splitter1.split(fileInputStream1);

            StringHandle[] result1 = contentStream1.toArray(size -> new StringHandle[size]);
            assertEquals(0, result1.length);

            // Normal local name, but no namespace URI
            String[] expected2 = new String[]{
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><OrderedDate>2000-10-01        </OrderedDate>",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><OrderedDate>2000-12-16</OrderedDate>",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><OrderedDate>            <year>2008</year>            <month>01</month>            <day>10</day>        </OrderedDate>"
            };
            XMLSplitter splitter2 = XMLSplitter.makeSplitter(null, "OrderedDate");
            fileInputStream2 = new FileInputStream(new File(datasource + fileName1));
            Stream<StringHandle> contentStream2 = splitter2.split(fileInputStream2);

            StringHandle[] result2 = contentStream2.toArray(size -> new StringHandle[size]);
            assertEquals(3, result2.length); // 3 results should be back
            for (int i = 0; i < result2.length; i++) {
                String row = result2[i].get().replace("\n", "").replace("\r", "");
                System.out.println("result2 element is " + row);
                assertTrue(row.equalsIgnoreCase(expected2[i]));
            }
        }
        catch (Exception ex) {
            System.out.println("Exception thrown from testXMLSplitterBasic " + ex.getMessage());
        }
        finally {
            if (fileInputStream != null) fileInputStream.close();
            if (fileInputStream1 != null) fileInputStream1.close();
            if (fileInputStream2 != null) fileInputStream2.close();
            try {
                clearDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testXMLSplitterAttr() throws Exception {
        System.out.println("Running testXMLSplitterAttr");
        try {
            String fileName1 = "xml-splitter-2.xml";

            FileInputStream fileInputStream = new FileInputStream(new File(datasource + fileName1));
            OrderedDateAttrVisitor visitor = new OrderedDateAttrVisitor("http://www.mlqa.com/",
                    "Item",
                    "PartNumber",
                    "SY-650-ANI");

            XMLSplitter splitter = new XMLSplitter(visitor);

            Stream<StringHandle> contentStream = splitter.split(fileInputStream);
            assertNotNull(contentStream);

            StringHandle[] result = contentStream.toArray(size -> new StringHandle[size]);
            assertEquals(1, splitter.getCount());
            assertNotNull(result);

            for (int i = 0; i < result.length; i++) {
                String row = result[i].get();
                //System.out.println("result element is " + row);
                assertTrue(row.contains("<ml:ProductName>Sony BLU-RAY</ml:ProductName>"));
                assertTrue(row.contains("<ml:USPrice>523.45</ml:USPrice>"));
            }
        } catch (Exception ex) {
            System.out.println("Exception thrown from testXMLSplitterAttr" + ex.toString());
        }
        finally {
            clearDB();
        }
    }

    ////////////// CSVSplitter Test for WriteOperation method //////////////
    @Test
    public void testCSVSplitterWriteOperationAndUriMaker() throws Exception {
        System.out.println("Running testCSVSplitterWriteOperationAndUriMaker");
        try {
            String fileName1 = "comma-sep-1.csv";
            String collectionName = "csvSplitter";

            FileInputStream fileInputStream = new FileInputStream(new File(datasource + fileName1));
            JacksonCSVSplitter splitter = new JacksonCSVSplitter();
            JacksonCSVSplitter.UriMaker uriMaker = new QADocUriFromHandle();
            uriMaker.setInputAfter("/QAFolder/");
            uriMaker.setSplitFilename("SacMetroHomeSale");
            splitter.setUriMaker(uriMaker);
            Stream<DocumentWriteOperation> contentStream1 = splitter.splitWriteOperations(fileInputStream);

            DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle().withCollections(collectionName).withProperty("Author-ratings", "10.0");

            WriteBatcher batcher = dmManager.newWriteBatcher();
            batcher.withBatchSize(5).withDefaultMetadata(metadataHandle1);

            batcher.addAll(contentStream1);
            dmManager.startJob(batcher);
            batcher.flushAndWait();

            // create a search definition for assert
            QueryManager queryMgr = client.newQueryManager();
            JSONDocumentManager jsonDocMgr = client.newJSONDocumentManager();

            String wordQuery = "<cts:word-query xmlns:cts=\"http://marklogic.com/cts\">" +
                    "<cts:text>TRINITY</cts:text></cts:word-query>";
            StringHandle handle = new StringHandle().with(wordQuery);
            RawCtsQueryDefinition querydef = queryMgr.newRawCtsQueryDefinition(handle);

            JacksonHandle resultsHandle = new JacksonHandle();
            queryMgr.search(querydef, resultsHandle);

            JsonNode result = resultsHandle.get();
            String uri1 = result.path("results").get(0).path("uri").asText();
            assertTrue(uri1.contains("38.621188-121.270555"));

            String text = jsonDocMgr.read(uri1, new StringHandle()).get();
            assertTrue(text.contains("11150 TRINITY RIVER DR Unit 114"));
            //System.out.println("text is " + text);
        } catch (Exception ex) {
            System.out.println("Execeptions thrown from testCSVSplitterWriteOperationAndUriMaker" + ex.toString());
        }
        finally {
            clearDB();
        }
    }
}

class QADocUriFromHandle implements JacksonCSVSplitter.UriMaker {
    String uriDirectory;
    String uriBaseName;
    AtomicInteger homeId = new AtomicInteger(0);

    @Override
    // Testing here if handle receives contents of stream (.csv file)
    public String makeUri(long num, JacksonHandle handle) {
        StringBuilder docUri = new StringBuilder();

        if (this.getInputAfter() != null &&  this.getInputAfter().length() > 0)
            docUri.append(this.getInputAfter());
        if (this.getSplitFilename() != null &&  this.getSplitFilename().length() > 0)
            docUri.append(this.getSplitFilename());

        // Append latitude and longitude with uriDirectory and uriBaseName
        //System.out.println("Handle is " + handle.toString());
        JsonNode lo = null, li = null;

        homeId.set(homeId.addAndGet(1));
        double longitude =  (lo = handle.get().path("longitude")) != null? Math.abs(lo.asDouble()) : 0.0;
        double latitude = (li = handle.get().path("latitude")) != null? Math.abs(li.asDouble()) : 0.0;

        docUri.append("-").append(latitude).append("-").append(longitude).append("-").append(homeId.get()).append(".json");

        return docUri.toString();
    }

    @Override
    public String getInputAfter() {
        return uriDirectory;
    }

    @Override
    public void setInputAfter(String base) {
        uriDirectory = base;
    }

    @Override
    public String getSplitFilename() {
        return uriBaseName;
    }

    @Override
    public void setSplitFilename(String name) {
        uriBaseName = name;
    }
}

class QAKeyVisitor extends JSONSplitter.ArrayVisitor {

    private String key;

    public QAKeyVisitor(String key) {
        this.key = key;
    }

    @Override
    public NodeOperation startObject(String containerKey) {
        int nLen = getArrayDepth();

        if (nLen == 2 && containerKey.equals(key)) {
            return NodeOperation.PROCESS;
        } else if (nLen > 0 && nLen < 2 && !containerKey.equals(key)) {
            return NodeOperation.SKIP;
        }

        return NodeOperation.DESCEND;
    }

    @Override
    public NodeOperation startArray(String containerKey) {
        incrementArrayDepth();
        if (getArrayDepth() > 0 && containerKey.equals(key)) {
            return NodeOperation.PROCESS;
        }
        return NodeOperation.DESCEND;
    }
}

class SplitRunnable implements Runnable {
    private AtomicInteger id;
    private DocumentManager docMgrJson;
    private DocumentMetadataHandle metadataHandle;
    private LineSplitter splitter;
    private FileInputStream fileStream;
    private String docIdPrefix;

    public LineSplitter getSplitter() {
        return splitter;
    }
    public void setSplitter(LineSplitter splitter) {
        this.splitter = splitter;
    }

    public DocumentManager getDocMgrJson() {
        return docMgrJson;
    }
    public void setDocMgrJson(DocumentManager docMgrJson) {
        this.docMgrJson = docMgrJson;
    }

    public AtomicInteger getId() {
        return id;
    }
    public void setId(AtomicInteger id) {
        this.id = id;
    }

    public String getDocIdPrefix() {
        return docIdPrefix;
    }
    public void setDocIdPrefix(String docIdPrefix) {
        this.docIdPrefix = docIdPrefix;
    }

    public DocumentMetadataHandle getMetadataHandle() {
        return metadataHandle;
    }
    public void setMetadataHandle(DocumentMetadataHandle metadataHandle) {
        this.metadataHandle = metadataHandle;
    }

    public FileInputStream getFileStream() {
        return fileStream;
    }
    public void setFileStream(FileInputStream fileStream) {
        this.fileStream = fileStream;
    }

    @Override
    public void run() {
        Stream<StringHandle> contentStream = null;
        Charset cs = Charset.forName("UTF-8");
        try {
            contentStream = splitter.split(fileStream, cs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        contentStream.forEach(s -> {
            //System.out.println("Thread name " + Thread.currentThread().getName() + " has content " + s);
            docMgrJson.write(docIdPrefix + id.addAndGet(1), metadataHandle, s);
        });
    }
}

class SplitWriteOpsRunnable implements Runnable {
    private WriteBatcher batcher;
    private LineSplitter splitter;
    private FileInputStream fileStream;
    private Stream<DocumentWriteOperation> contentStream;

    public LineSplitter getSplitter() {
        return splitter;
    }
    public void setSplitter(LineSplitter splitter) {
        this.splitter = splitter;
    }
    public FileInputStream getFileStream() {
        return fileStream;
    }
    public void setFileStream(FileInputStream fileStream) {
        this.fileStream = fileStream;
    }
    public WriteBatcher getBatcher() {
        return batcher;
    }

    public void setBatcher(WriteBatcher batcher) {
        this.batcher = batcher;
    }

    @Override
    public void run() {
        String eachSplitBaseName = "WriteOpsRunnable.json";
        try {
            contentStream = splitter.splitWriteOperations(fileStream, eachSplitBaseName);
            batcher.addAll(contentStream);
            // Any batch failures should be reported in caller as asserts
            batcher.flushAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class OrderedDateAttrVisitor extends XMLSplitter.Visitor<StringHandle> {
    private String nsUri, localName, attrName, attrValue;

    public OrderedDateAttrVisitor(String nsUri,
                                  String localName,
                                  String attrName,
                                  String attrValue) {
        this.nsUri = nsUri;
        this.localName = localName;
        this.attrName = attrName;
        this.attrValue = attrValue;
    }

    @Override
    public NodeOperation startElement(XMLSplitter.StartElementReader startElementReader) {
        if (startElementReader.getNamespaceURI() != null) {
            if (startElementReader.getNamespaceURI().equals(this.nsUri) &&
                    startElementReader.getLocalName().equals(this.localName)) {

                int i;
                for (i = 0; i < startElementReader.getAttributeCount(); i++) {
                    String localAttrName = startElementReader.getAttributeLocalName(i);
                    String localAttrValue = startElementReader.getAttributeValue(i);

                    if (localAttrName.equals(this.attrName) && localAttrValue.equals(this.attrValue)) {
                        return NodeOperation.PROCESS;
                    }
                }
            }
        }
        else {
            if (startElementReader.getLocalName().equals(this.localName)) {
                int i;
                for (i = 0; i < startElementReader.getAttributeCount(); i++) {
                    String localAttrName = startElementReader.getAttributeLocalName(i);
                    String localAttrValue = startElementReader.getAttributeValue(i);

                    if (localAttrName.equals(this.attrName) && localAttrValue.equals(this.attrValue)) {
                        return NodeOperation.PROCESS;
                    }
                }
            }
        }
        return NodeOperation.DESCEND;
    }

    @Override
    public StringHandle makeBufferedHandle(XMLStreamReader elementBranchReader) {
        String content = serialize(elementBranchReader);
        return new StringHandle(content).withFormat(Format.XML);
    }
}
