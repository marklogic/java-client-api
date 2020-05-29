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
package com.marklogic.client.test.datamovement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

import com.marklogic.client.datamovement.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteOperation.OperationType;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.opencsv.CSVReader;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class JacksonCSVSplitterTest {
    
    static final private String csvFile = "src/test/resources/data/pathSplitter" + File.separator + "test.csv";
    private DatabaseClient client;
    private DataMovementManager moveMgr;
    
    @Before
    public void setUp() throws Exception {
        client = DatabaseClientFactory.newClient("localhost", 8012,
                new DatabaseClientFactory.DigestAuthContext("rest-admin", "x"));
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
        Stream<DocumentWriteOperation> contentStream = splitter.splitWriteOperations(new FileInputStream(csvFile));
        Splitter.UriMaker uriMaker = splitter.getUriMaker();
        uriMaker.setInputAfter("/SystemPath/");
        uriMaker.setInputName("NewCSV.json");
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
    public void testCSVSplitterWriteOperationWithName() throws Exception {

        JacksonCSVSplitter splitter = new JacksonCSVSplitter();
        Stream<DocumentWriteOperation> contentStream = splitter.splitWriteOperations(new FileInputStream(csvFile), "csv.json");
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
    public void testCSVSplitterWriteOperationWithReader() throws Exception {

        JacksonCSVSplitter splitter = new JacksonCSVSplitter();
        Stream<DocumentWriteOperation> contentStream = splitter.splitWriteOperations(new FileReader(csvFile), "csv.json");
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
    
    @After
    public void closeSetUp() {
        client.release();
    }
}
