/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.test.datamovement;

import com.marklogic.client.datamovement.JSONSplitter;
import com.marklogic.client.datamovement.LineSplitter;
import com.marklogic.client.datamovement.PathSplitter;
import com.marklogic.client.datamovement.XMLSplitter;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.Format;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PathSplitterTest {

    static final private String baseDirectory = "src/test/resources";
    static final private String dataDiretory = "data/pathSplitter/";

    @Test
    public void PathTestDefault() throws Exception {
        Stream<Path> paths = Files.list(Paths.get(baseDirectory, dataDiretory));
        PathSplitter pathSplitter = new PathSplitter().withDocumentUriAfter(Paths.get(dataDiretory));
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
        Stream<Path> paths = Files.list(Paths.get(baseDirectory, dataDiretory));
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
        Stream<Path> paths = Files.list(Paths.get(baseDirectory, dataDiretory));
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
        Stream<Path> paths = Files.list(Paths.get(baseDirectory, dataDiretory));
        PathSplitter pathSplitter = new PathSplitter().withDocumentUriAfter(Paths.get(baseDirectory));
        LineSplitter splitter = new LineSplitter();
        splitter.setFormat(Format.JSON);
        pathSplitter.getSplitters().put("gz", splitter);
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
        Stream<Path> paths = Files.list(Paths.get(baseDirectory, dataDiretory));
        PathSplitter pathSplitter = new PathSplitter().withDocumentUriAfter(Paths.get(baseDirectory));
        LineSplitter splitter = new LineSplitter();
        splitter.setFormat(Format.TEXT);
        pathSplitter.getSplitters().put("txt", splitter);
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
        Stream<Path> paths = Files.list(Paths.get(baseDirectory, dataDiretory));
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
