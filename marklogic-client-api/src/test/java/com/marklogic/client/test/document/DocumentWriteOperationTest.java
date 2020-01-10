/*
 * Copyright 2020 MarkLogic Corporation
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
package com.marklogic.client.test.document;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.test.Common;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DocumentWriteOperationTest {

    static TextDocumentManager textDocumentManager;
    static String collectionName = "DocumentWriteOperation";

    StringHandle doc1 = new StringHandle(
            "Document - 1").withFormat(Format.TEXT);
    StringHandle doc2 = new StringHandle(
            "Document - 2").withFormat(Format.TEXT);
    StringHandle doc3 = new StringHandle(
            "Document - 3").withFormat(Format.TEXT);
    StringHandle doc4 = new StringHandle(
            "Document - 4").withFormat(Format.TEXT);
    StringHandle doc5 = new StringHandle(
            "Document - 5").withFormat(Format.TEXT);
    StringHandle doc6 = new StringHandle(
            "Document - 6").withFormat(Format.TEXT);
    StringHandle doc7 = new StringHandle(
            "Document - 7").withFormat(Format.TEXT);
    StringHandle doc8 = new StringHandle(
            "Document - 8").withFormat(Format.TEXT);

    DocumentMetadataHandle defaultMetadata1 =
            new DocumentMetadataHandle().withQuality(1).withCollections(collectionName);
    DocumentMetadataHandle defaultMetadata2 =
            new DocumentMetadataHandle().withQuality(2).withCollections(collectionName);

    @BeforeClass
    public static void setup() {
        Common.connect();
        textDocumentManager = Common.client.newTextDocumentManager();
    }

    @Test
    public void DocumentWriteSetWithNoMetadataTest() {

        DocumentWriteSet batch = textDocumentManager.newWriteSet();

        batch.add("doc8.txt", doc8);
        batch.add("doc5.txt", doc5);
        batch.add("doc6.txt", doc6);
        batch.add("doc4.txt", doc4);
        batch.add("doc3.txt", doc3);
        batch.add("doc1.txt", doc1);
        batch.add("doc2.txt", doc2);
        batch.add("doc7.txt", doc7);

        List<String> list = new ArrayList<>();

        list.add("doc1.txt");
        list.add("doc2.txt");
        list.add("doc3.txt");
        list.add("doc4.txt");
        list.add("doc5.txt");
        list.add("doc6.txt");
        list.add("doc7.txt");
        list.add("doc8.txt");
        int i = 0;

        Iterator<DocumentWriteOperation> itr = batch.iterator();
          while(itr.hasNext()){
              assertEquals("Document uri not as expected", itr.next().getUri(), list.get(i));
              i++;
          }
        textDocumentManager.write(batch);
    }

    @Test
    public void DocumentWriteSetWithTwoMetadataTest() {


        DocumentWriteSet batch = textDocumentManager.newWriteSet();

        batch.add("doc8.txt", doc8);
        batch.add("doc5.txt", doc5);
        batch.add("doc6.txt", doc6);
        batch.add("doc4.txt", doc4);
        batch.addDefault(defaultMetadata1);
        batch.add("doc3.txt", doc3);
        batch.add("doc1.txt", doc1);
        batch.addDefault(defaultMetadata2);
        batch.add("doc2.txt", doc2);
        batch.add("doc7.txt", doc7);

        List<String> list = new ArrayList<>();
        list.add("doc8.txt");
        list.add("doc5.txt");
        list.add("doc6.txt");
        list.add("doc4.txt");
        list.add(null);
        list.add("doc3.txt");
        list.add("doc1.txt");
        list.add(null);
        list.add("doc2.txt");
        list.add("doc7.txt");

        int i = 0;

        Iterator<DocumentWriteOperation> itr = batch.iterator();
        while(itr.hasNext()){
            assertEquals("Document uri not as expected", itr.next().getUri(), list.get(i));
            i++;
        }
        textDocumentManager.write(batch);

        assertEquals("Doc 1 metadata not as expected",1,
                textDocumentManager.readMetadata("doc1.txt", new DocumentMetadataHandle()).getQuality());

        assertEquals("Doc 2 metadata not as expected",2,
                textDocumentManager.readMetadata("doc2.txt", new DocumentMetadataHandle()).getQuality());

        assertEquals("Doc 3 metadata not as expected",1,
                textDocumentManager.readMetadata("doc3.txt", new DocumentMetadataHandle()).getQuality());

        assertEquals("Doc 4 metadata not as expected",0,
                textDocumentManager.readMetadata("doc4.txt", new DocumentMetadataHandle()).getQuality());

        assertEquals("Doc 5 metadata not as expected",0,
                textDocumentManager.readMetadata("doc5.txt", new DocumentMetadataHandle()).getQuality());

        assertEquals("Doc 6 metadata not as expected",0,
                textDocumentManager.readMetadata("doc6.txt", new DocumentMetadataHandle()).getQuality());

        assertEquals("Doc 7 metadata not as expected",2,
                textDocumentManager.readMetadata("doc7.txt", new DocumentMetadataHandle()).getQuality());

        assertEquals("Doc 8 metadata not as expected",0,
                textDocumentManager.readMetadata("doc8.txt", new DocumentMetadataHandle()).getQuality());
    }

    @Test
    public void DocumentWriteSetWithOneMetadataTest() {

        DocumentWriteSet batch = textDocumentManager.newWriteSet();

        batch.addDefault(defaultMetadata1);
        batch.add("doc8.txt", doc8);
        batch.add("doc5.txt", doc5);
        batch.add("doc6.txt", doc6);
        batch.add("doc4.txt", doc4);
        batch.add("doc3.txt", doc3);
        batch.add("doc1.txt", doc1);
        batch.add("doc2.txt", doc2);
        batch.add("doc7.txt", doc7);

        List<String> list = new ArrayList<>();
        list.add(null);
        list.add("doc1.txt");
        list.add("doc2.txt");
        list.add("doc3.txt");
        list.add("doc4.txt");
        list.add("doc5.txt");
        list.add("doc6.txt");
        list.add("doc7.txt");
        list.add("doc8.txt");
        int i = 0;

        Iterator<DocumentWriteOperation> itr = batch.iterator();
        while(itr.hasNext()){
            assertEquals("Document uri not as expected", itr.next().getUri(), list.get(i));
            i++;
        }
        textDocumentManager.write(batch);
    }

    @Test
    public void DocumentWriteSetWithOneMetadataTest_2() {

        DocumentWriteSet batch = textDocumentManager.newWriteSet();

        batch.add("doc8.txt", doc8);
        batch.add("doc5.txt", doc5);
        batch.add("doc6.txt", doc6);
        batch.addDefault(defaultMetadata1);
        batch.add("doc4.txt", doc4);
        batch.add("doc3.txt", doc3);
        batch.add("doc1.txt", doc1);
        batch.add("doc2.txt", doc2);
        batch.add("doc7.txt", doc7);

        List<String> list = new ArrayList<>();

        list.add("doc8.txt");
        list.add("doc5.txt");
        list.add("doc6.txt");
        list.add(null);
        list.add("doc4.txt");
        list.add("doc3.txt");
        list.add("doc1.txt");
        list.add("doc2.txt");
        list.add("doc7.txt");
        int i = 0;

        Iterator<DocumentWriteOperation> itr = batch.iterator();
        while(itr.hasNext()){
            assertEquals("Document uri not as expected", itr.next().getUri(), list.get(i));
            i++;
        }
        textDocumentManager.write(batch);
    }

    @Test
    public void DocumentWriteSetWithDisableMetadataTest() {

        DocumentWriteSet batch = textDocumentManager.newWriteSet();

        batch.disableDefault();
        batch.add("doc8.txt", doc8);
        batch.add("doc5.txt", doc5);
        batch.add("doc6.txt", doc6);
        batch.add("doc4.txt", doc4);
        batch.add("doc3.txt", doc3);
        batch.add("doc1.txt", doc1);
        batch.add("doc2.txt", doc2);
        batch.add("doc7.txt", doc7);

        List<String> list = new ArrayList<>();

        list.add(null);
        list.add("doc1.txt");
        list.add("doc2.txt");
        list.add("doc3.txt");
        list.add("doc4.txt");
        list.add("doc5.txt");
        list.add("doc6.txt");
        list.add("doc7.txt");
        list.add("doc8.txt");
        int i=0;

        Iterator<DocumentWriteOperation> itr = batch.iterator();
        while(itr.hasNext()){
            assertEquals("Document uri not as expected", itr.next().getUri(), list.get(i));
            i++;
        }
        textDocumentManager.write(batch);
    }

    @Test
    public void DocumentWriteSetWithDisableMetadataTest_2() {

        DocumentWriteSet batch = textDocumentManager.newWriteSet();


        batch.add("doc8.txt", doc8);
        batch.add("doc5.txt", doc5);
        batch.add("doc6.txt", doc6);
        batch.add("doc4.txt", doc4);
        batch.disableDefault();
        batch.add("doc3.txt", doc3);
        batch.add("doc1.txt", doc1);
        batch.add("doc2.txt", doc2);
        batch.add("doc7.txt", doc7);

        List<String> list = new ArrayList<>();


        list.add("doc8.txt");
        list.add("doc5.txt");
        list.add("doc6.txt");
        list.add("doc4.txt");
        list.add(null);
        list.add("doc3.txt");
        list.add("doc1.txt");
        list.add("doc2.txt");
        list.add("doc7.txt");
        int i=0;

        Iterator<DocumentWriteOperation> itr = batch.iterator();
        while(itr.hasNext()){
            assertEquals("Document uri not as expected", itr.next().getUri(), list.get(i));
            i++;
        }
        textDocumentManager.write(batch);
    }

    @AfterClass
    public static  void cleanup() {
        QueryManager queryManager = Common.client.newQueryManager();
        DeleteQueryDefinition deletedef = queryManager.newDeleteDefinition();
        deletedef.setCollections(collectionName);
        queryManager.delete(deletedef);
    }
}
