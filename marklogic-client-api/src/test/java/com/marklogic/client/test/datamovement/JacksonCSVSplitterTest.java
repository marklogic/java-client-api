/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.JacksonCSVSplitter;
import com.marklogic.client.datamovement.Splitter;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteOperation.OperationType;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.test.Common;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class JacksonCSVSplitterTest {

    static final private String csvFile = "src/test/resources/data/pathSplitter" + File.separator + "test.csv";
    private DatabaseClient client;
    private DataMovementManager moveMgr;

    @BeforeEach
    public void setUp() {
        client = Common.makeNewClient(Common.HOST, Common.PORT, Common.newSecurityContext("rest-admin", "x"));
        moveMgr = client.newDataMovementManager();
    }

    @Test
    public void testSplitter() throws Exception {

        JacksonCSVSplitter splitter = new JacksonCSVSplitter();
        Stream<JacksonHandle> contentStream = splitter.split(new FileInputStream(csvFile));
        assertNotNull(contentStream);

        JacksonHandle[] result = contentStream.toArray(size -> new JacksonHandle[size]);
        assertNotNull(result);
        assertTrue(result.length == (Files.lines(Paths.get(csvFile)).count()-1));

        FileReader fileReader = new FileReader(csvFile);
        CSVReader csvReader = new CSVReader(fileReader);
        String[] headerValues = csvReader.readNext();

        for(int i=0; i<result.length; i++) {
            assertNotNull(result[i].get());
            assertNotNull(result[i].get().fields());
            for(int j=0; j<headerValues.length;j++) {
                assertNotNull(result[i].get().findValue(headerValues[j]));
            }
        }
        fileReader.close();
        csvReader.close();
    }

    @Test
    public void testCSVSplitterWriteOperation() throws Exception {

        JacksonCSVSplitter splitter = new JacksonCSVSplitter();
        Stream<DocumentWriteOperation> contentStream = splitter.splitWriteOperations(new FileInputStream(csvFile));
        assertNotNull(contentStream);

        Iterator<DocumentWriteOperation> itr = contentStream.iterator();

        FileReader fileReader = new FileReader(csvFile);
        CSVReader csvReader = new CSVReader(fileReader);
        String[] headerValues = csvReader.readNext();

        int i = 0;
        while (itr.hasNext()) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertNotNull(docOp.getUri());
            assertTrue(docOp.getUri().startsWith("/" + (i+1)));
            assertTrue(docOp.getUri().endsWith(".json"));

            assertNotNull(docOp.getContent());
            JacksonHandle docOpContent = (JacksonHandle) docOp.getContent();
            for(int j=0; j<headerValues.length;j++) {
                assertNotNull(docOpContent.get().findValue(headerValues[j]));
            }
            i++;
        }

        assertEquals(11, splitter.getCount());
        fileReader.close();
        csvReader.close();
    }

    @Test
    public void testCSVSplitterWriteOperationSetName() throws Exception {

        JacksonCSVSplitter splitter = new JacksonCSVSplitter();
        Stream<DocumentWriteOperation> contentStream = splitter.splitWriteOperations(new FileInputStream(csvFile), "NewCSV.csv");
        assertNotNull(contentStream);

        Iterator<DocumentWriteOperation> itr = contentStream.iterator();

        FileReader fileReader = new FileReader(csvFile);
        CSVReader csvReader = new CSVReader(fileReader);
        String[] headerValues = csvReader.readNext();

        int i = 0;
        while (itr.hasNext()) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertNotNull(docOp.getUri());
            assertTrue(docOp.getUri().startsWith("NewCSV" + (i+1)));
            assertTrue(docOp.getUri().endsWith(".json"));

            assertNotNull(docOp.getContent());
            JacksonHandle docOpContent = (JacksonHandle) docOp.getContent();
            for(int j=0; j<headerValues.length;j++) {
                assertNotNull(docOpContent.get().findValue(headerValues[j]));
            }
            i++;
        }

        assertEquals(11, splitter.getCount());
        fileReader.close();
        csvReader.close();
    }

    @Test
    public void testCSVSplitterWriteOperationWithCustomeUriMaker() throws Exception {

        JacksonCSVSplitter splitter = new JacksonCSVSplitter();
        JacksonCSVSplitter.UriMaker uriMaker = new TestUriMaker();
        uriMaker.setInputAfter("/Directory/");
        uriMaker.setSplitFilename("NewCsv");
        splitter.setUriMaker(uriMaker);
        Stream<DocumentWriteOperation> contentStream = splitter.splitWriteOperations(new FileInputStream(csvFile));
        assertNotNull(contentStream);

        Iterator<DocumentWriteOperation> itr = contentStream.iterator();

        FileReader fileReader = new FileReader(csvFile);
        CSVReader csvReader = new CSVReader(fileReader);
        String[] headerValues = csvReader.readNext();

        int i = 0;
        while (itr.hasNext()) {
            i++;
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertEquals(docOp.getUri(), "/Directory/NewCsv" + i + "_abcd.json");

            assertNotNull(docOp.getContent());
            JacksonHandle docOpContent = (JacksonHandle) docOp.getContent();
            for(int j=0; j<headerValues.length;j++) {
                assertNotNull(docOpContent.get().findValue(headerValues[j]));
            }
        }

        assertEquals(11, splitter.getCount());
        fileReader.close();
        csvReader.close();
    }

    public class TestUriMaker implements JacksonCSVSplitter.UriMaker {
        String inputName;
        String inputAfter;

        @Override
        public String makeUri(long num, JacksonHandle handle) {
            StringBuilder uri = new StringBuilder();
            String randomUUIDForTest = "abcd";

            if (getInputAfter() != null && getInputAfter().length() != 0) {
                uri.append(getInputAfter());
            }

            if (getSplitFilename() != null && getSplitFilename().length() != 0) {
                uri.append(getSplitFilename());
            }

            uri.append(num).append("_").append(randomUUIDForTest).append(".json");
            return uri.toString();
        }

        @Override
        public String getInputAfter() {
            return this.inputAfter;
        }

        @Override
        public void setInputAfter(String base) {
            this.inputAfter = base;
        }

        @Override
        public String getSplitFilename() {
            return this.inputName;
        }

        @Override
        public void setSplitFilename(String name) {
            this.inputName = name;
        }
    }

    @Test
    public void testCSVSplitterWriteOperationWithReader() throws Exception {

        JacksonCSVSplitter splitter = new JacksonCSVSplitter();
        Stream<DocumentWriteOperation> contentStream = splitter.splitWriteOperations(new FileReader(csvFile), "NewCSV.csv");
        Splitter.UriMaker uriMaker = splitter.getUriMaker();
        assertNotNull(contentStream);

        Iterator<DocumentWriteOperation> itr = contentStream.iterator();

        FileReader fileReader = new FileReader(csvFile);
        CSVReader csvReader = new CSVReader(fileReader);
        String[] headerValues = csvReader.readNext();

        int i = 0;
        while (itr.hasNext()) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertNotNull(docOp.getUri());
            assertTrue(docOp.getUri().startsWith("NewCSV" + (i+1)));
            assertTrue(docOp.getUri().endsWith(".json"));

            assertNotNull(docOp.getContent());
            JacksonHandle docOpContent = (JacksonHandle) docOp.getContent();
            for(int j=0; j<headerValues.length;j++) {
                assertNotNull(docOpContent.get().findValue(headerValues[j]));
            }
            i++;
        }

        assertEquals(11, splitter.getCount());
        fileReader.close();
        csvReader.close();
    }

    @Test
    public void testDocumentWriteOperation() throws Exception {
        Stream<StringHandle> strStream = Stream.of(new StringHandle("first"), new StringHandle("second"));

        Stream<DocumentWriteOperation> documentStream =  DocumentWriteOperation.from(
                strStream, DocumentWriteOperation.uriMaker("/sample/directory/%s.json")
                );

        assertNotNull(documentStream);
        String[] stringHandleValues = {"first", "second"};
        Iterator<DocumentWriteOperation> itr = documentStream.iterator();
        for(int i=0; itr.hasNext(); i++) {
            DocumentWriteOperation docOp = itr.next();
            assertNotNull(docOp.getUri());
            assertNotNull(docOp.getContent());
            assertTrue(docOp.getContent().toString().equals(stringHandleValues[i]));
        }
    }

    @Test
    public void testBatcher() throws Exception {
        try {
            DocumentMetadataHandle documentMetadata = new DocumentMetadataHandle();
            WriteBatcher batcher = moveMgr.newWriteBatcher().withDefaultMetadata(documentMetadata);

            assertTrue(batcher.getDocumentMetadata() == documentMetadata);
            DocumentWriteOperation docWriteImpl = new DocumentWriteOperationImpl(OperationType.DOCUMENT_WRITE,
                    "/sample/test/test1.txt", documentMetadata, new StringHandle().with("Test1"));
            DocumentWriteOperation docWriteImpl1 = new DocumentWriteOperationImpl(OperationType.DOCUMENT_WRITE,
                    "/sample/test/test2.txt", documentMetadata, new StringHandle().with("Test2"));
            Stream<DocumentWriteOperation> docSteam = Stream.of(docWriteImpl, docWriteImpl1);

            moveMgr.startJob(batcher);
            batcher.addAll(docSteam);
            batcher.flushAndWait();
        } finally {
            QueryManager queryMgr = client.newQueryManager();
            DeleteQueryDefinition deleteDef = queryMgr.newDeleteDefinition();
            deleteDef.setDirectory("/sample/test/");
            queryMgr.delete(deleteDef);
        }
    }

    @AfterEach
    public void closeSetUp() {
        client.release();
    }
}
