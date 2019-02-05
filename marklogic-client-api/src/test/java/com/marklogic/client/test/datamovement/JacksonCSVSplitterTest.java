/*
 * Copyright 2019 MarkLogic Corporation
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.JacksonCSVSplitter;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteOperation.OperationType;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;

public class JacksonCSVSplitterTest {
    
    static final private String csvFile = "src/test/resources" + File.separator + "OrderLines.csv";
    private DatabaseClient client;
    private DataMovementManager moveMgr;
    private Stream<JacksonHandle> contentStream;
    
    @Before
    public void setUp() throws Exception {
        client = DatabaseClientFactory.newClient("localhost", 8012,
                new DatabaseClientFactory.DigestAuthContext("rest-admin", "x"));
        moveMgr = client.newDataMovementManager();
    }
    
    @Test
    public void run() throws Exception  {
        try {
            testSplitter();
            testDocumentWriteOperation();
            testBatcher();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("test exception");
        } finally {
                
        }
    }
    
    @Test
    public void testSplitter() throws Exception {

        JacksonCSVSplitter splitter = new JacksonCSVSplitter();
        contentStream = splitter.split(new FileInputStream(csvFile));
        assertNotNull(contentStream);
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
        while(itr.hasNext()) {
            DocumentWriteOperation docOp = itr.next();
            assertNotNull(docOp.getUri());
            assertNotNull(docOp.getContent());
            
            assertTrue(Arrays.stream(stringHandleValues).anyMatch(docOp.getContent().toString()::equals));
        }
    }
    
    @Test
    public void testBatcher() throws Exception {
        
        DocumentMetadataHandle documentMetadata = new DocumentMetadataHandle();
        WriteBatcher batcher = moveMgr.newWriteBatcher().withDefaultMetadata(documentMetadata);
        
        assertTrue(batcher.getDocumentMetadata() == documentMetadata);
        DocumentWriteOperation docWriteImpl = new DocumentWriteOperationImpl(
                OperationType.DOCUMENT_WRITE,
                "/sample/test1.txt",
                documentMetadata,
                new StringHandle().with("Test1")
                );
        DocumentWriteOperation docWriteImpl1 = new DocumentWriteOperationImpl(OperationType.DOCUMENT_WRITE, "/sample/test2.txt", documentMetadata, new StringHandle().with("Test2"));
        Stream<DocumentWriteOperation> docSteam = Stream.of(docWriteImpl, docWriteImpl1);
        batcher.addAll(docSteam);
        
        batcher.flushAndWait();
    }
    
    @After
    public void closeSetUp() {
        client.release();
    }
}
