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

package com.marklogic.client.test.datamovement;

import com.marklogic.client.datamovement.JSONSplitter;
import com.marklogic.client.datamovement.LineSplitter;
import com.marklogic.client.datamovement.PathSplitter;
import com.marklogic.client.datamovement.XMLSplitter;
import com.marklogic.client.document.DocumentWriteOperation;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class PathSplitterTest {

    static final private String baseDirectory = "src/test/resources/data" + File.separator + "/pathSplitter/";

    @Test
    public void PathTestDefault() throws Exception {
        Stream<Path> paths = Files.list(Paths.get(baseDirectory));
        PathSplitter pathSplitter = new PathSplitter().withDocumentUriAfter(Paths.get(baseDirectory));
        Stream<DocumentWriteOperation> contentStream = pathSplitter.splitDocumentWriteOperations(paths);
        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        int i = 0;
        while (itr.hasNext()) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertNotNull(docOp.getUri());

            assertNotNull(docOp.getContent());
            String docOpContent = docOp.getContent().toString();
            i++;
        }

        assertEquals(30, i);
    }

    @Test
    public void PathTestXML() throws Exception {
        Stream<Path> paths = Files.list(Paths.get(baseDirectory));
        PathSplitter pathSplitter = new PathSplitter().withDocumentUriAfter(Paths.get(baseDirectory));
        pathSplitter.getSplitters().put("xml", XMLSplitter.makeSplitter("http://www.marklogic.com/people/", "person"));
        Stream<DocumentWriteOperation> contentStream = pathSplitter.splitDocumentWriteOperations(paths);
        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        int i = 0;
        while (itr.hasNext()) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertNotNull(docOp.getUri());

            assertNotNull(docOp.getContent());
            String docOpContent = docOp.getContent().toString();
            i++;
        }

        assertEquals(32, i);
    }

    @Test
    public void PathTestJSON() throws Exception {
        Stream<Path> paths = Files.list(Paths.get(baseDirectory));
        PathSplitter pathSplitter = new PathSplitter().withDocumentUriAfter(Paths.get(baseDirectory));
        pathSplitter.getSplitters().put("json", JSONSplitter.makeArraySplitter());
        Stream<DocumentWriteOperation> contentStream = pathSplitter.splitDocumentWriteOperations(paths);
        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        int i = 0;
        while (itr.hasNext()) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri(); //the count will continue in one splitter for different files.
            assertNotNull(docOp.getUri());

            assertNotNull(docOp.getContent());
            String docOpContent = docOp.getContent().toString();
            i++;
        }

        assertEquals(39, i);
    }

    @Test
    public void PathTestGZ() throws Exception {
        Stream<Path> paths = Files.list(Paths.get(baseDirectory));
        PathSplitter pathSplitter = new PathSplitter().withDocumentUriAfter(Paths.get(baseDirectory));
        pathSplitter.getSplitters().put("gz", new LineSplitter());
        Stream<DocumentWriteOperation> contentStream = pathSplitter.splitDocumentWriteOperations(paths);
        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        int i = 0;
        while (itr.hasNext()) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertNotNull(docOp.getUri());

            assertNotNull(docOp.getContent());
            String docOpContent = docOp.getContent().toString();
            i++;
        }

        assertEquals(38, i);
    }

    @Test
    public void PathTestTXT() throws Exception {
        Stream<Path> paths = Files.list(Paths.get(baseDirectory));
        PathSplitter pathSplitter = new PathSplitter().withDocumentUriAfter(Paths.get(baseDirectory));
        pathSplitter.getSplitters().put("txt", new LineSplitter());
        Stream<DocumentWriteOperation> contentStream = pathSplitter.splitDocumentWriteOperations(paths);
        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        int i = 0;
        while (itr.hasNext()) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertNotNull(docOp.getUri());

            assertNotNull(docOp.getContent());
            String docOpContent = docOp.getContent().toString();
            i++;
        }

        assertEquals(33, i);
    }

    @Test
    public void PathTestNull() throws Exception {
        Stream<Path> paths = Files.list(Paths.get(baseDirectory));
        PathSplitter pathSplitter = new PathSplitter().withDocumentUriAfter(Paths.get(baseDirectory));
        pathSplitter.getSplitters().put("default", null);
        Stream<DocumentWriteOperation> contentStream = pathSplitter.splitDocumentWriteOperations(paths);
        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        int i = 0;
        while (itr.hasNext()) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertNotNull(docOp.getUri());

            assertNotNull(docOp.getContent());
            String docOpContent = docOp.getContent().toString();
            i++;
        }

        assertEquals(23, i);
    }
}
