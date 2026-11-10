/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */

package com.marklogic.client.test.datamovement;

import com.marklogic.client.datamovement.JSONSplitter;
import com.marklogic.client.datamovement.NodeOperation;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class JSONSplitterTest {
    static final private String jsonObjectFile = "src/test/resources/data" + File.separator + "pathSplitter/JsonSplitterObject.json";
    static final private String jsonArrayFile = "src/test/resources/data" + File.separator + "pathSplitter/JsonSplitterArray.json";
    static final private String jsonMultiArrayFile = "src/test/resources/data" + File.separator + "pathSplitter/JsonMultiDiArray.json";
    static final private String jsonCustMultiArrayFile = "src/test/resources/data" + File.separator + "pathSplitter/jsonCustMultiArray.json";
    static final private String[] expectedArray = new String[]{
            "{\"record\":\"first record\"}",
            "{\"record\":\"second record\"}",
            "{\"record\":\"third record\"}",
            "[{\"record\":\"forth record\"},{\"record\":\"fifth record\"}]",
            "{\"context\":[{\"record\":\"sixth record\"},{\"record\":\"seventh record\"}]}"
    };

    @Test
    public void testJSONSplitterArray() throws Exception {
        JSONSplitter<StringHandle> splitter = JSONSplitter.makeArraySplitter();
        FileInputStream fileInputStream = new FileInputStream(new File(jsonArrayFile));
        Stream<StringHandle> contentStream = splitter.split(fileInputStream);
        assertNotNull(contentStream);

        StringHandle[] result = contentStream.toArray(size -> new StringHandle[size]);
        assertEquals(5, splitter.getCount());
        assertNotNull(result);

        for (int i = 0; i < result.length; i++) {
            String element = result[i].get();
            assertNotNull(element);
            assertEquals(expectedArray[i], element);
        }
    }

    @Test
    public void testJSONSplitterObject() throws Exception {

        JSONSplitter<StringHandle> splitter = JSONSplitter.makeArraySplitter();
        FileInputStream fileInputStream = new FileInputStream(jsonObjectFile);
        Stream<StringHandle> contentStream = splitter.split(fileInputStream);
        assertNotNull(contentStream);

        StringHandle[] result = contentStream.toArray(size -> new StringHandle[size]);
        assertEquals(5, splitter.getCount());
        assertNotNull(result);

        for (int i = 0; i < result.length; i++) {
            String element = result[i].get();
            assertNotNull(element);
            assertEquals(expectedArray[i], element);
        }
    }

    @Test
    public void testJSONSplitterMultiArray() throws Exception {

        JSONSplitter<StringHandle> splitter = JSONSplitter.makeArraySplitter();
        FileInputStream fileInputStream = new FileInputStream(jsonMultiArrayFile);
        Stream<StringHandle> contentStream = splitter.split(fileInputStream);
        assertNotNull(contentStream);

        StringHandle[] result = contentStream.toArray(size -> new StringHandle[size]);
        assertEquals(2, splitter.getCount());
        assertNotNull(result);

        String[] expected = new String[] {
                "[{\"context\":[[{\"record\":\"first record\"},{\"record\":\"second record\"}]]}]",
                "{\"record\":\"third record\"}"
        };

        for (int i = 0; i < result.length; i++) {
            String element = result[i].get();
            assertNotNull(element);
            assertEquals(expected[i], element);
        }
    }

    @Test
    public void testJSONSplitterWriteWithCustomUriMaker() throws Exception {

        String[] expectedURIs = {
                "/SystemPath/NewTestJson1_abcd.json",
                "/SystemPath/NewTestJson2_abcd.json",
                "/SystemPath/NewTestJson3_abcd.json",
                "/SystemPath/NewTestJson4_abcd.json",
                "/SystemPath/NewTestJson5_abcd.json"
        };
        JSONSplitter<StringHandle> splitter = JSONSplitter.makeArraySplitter();
        FileInputStream fileInputStream = new FileInputStream(jsonArrayFile);
        JSONSplitter.UriMaker uriMaker = new UriMakerTest();
        uriMaker.setInputAfter("/SystemPath/");
        uriMaker.setSplitFilename("NewTestJson");
        splitter.setUriMaker(uriMaker);
        Stream<DocumentWriteOperation> contentStream = splitter.splitWriteOperations(fileInputStream);
        assertNotNull(contentStream);

        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        int i = 0;
        while (itr.hasNext()) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertEquals(docOp.getUri(), expectedURIs[i]);

            assertNotNull(docOp.getContent());
            String docOpContent = docOp.getContent().toString();
            assertEquals(docOpContent, expectedArray[i]);
            i++;
        }

        assertEquals(5, splitter.getCount());
    }

    private class UriMakerTest implements JSONSplitter.UriMaker {
        private String inputAfter;
        private String inputName;

        @Override
        public String makeUri(long num, JSONWriteHandle handle) {
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
    public void testJSONSplitterWriteWithInputName() throws Exception {

        JSONSplitter<StringHandle> splitter = JSONSplitter.makeArraySplitter();
        FileInputStream fileInputStream = new FileInputStream(jsonArrayFile);
        Stream<DocumentWriteOperation> contentStream = splitter.splitWriteOperations(fileInputStream, "TestJson.json");
        assertNotNull(contentStream);

        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        int i = 0;
        while (itr.hasNext()) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertNotNull(docOp.getUri());
            assertTrue(docOp.getUri().startsWith("TestJson" + (i+1)));
            assertTrue(docOp.getUri().endsWith(".json"));

            assertNotNull(docOp.getContent());
            String docOpContent = docOp.getContent().toString();
            assertEquals(docOpContent, expectedArray[i]);
            i++;
        }

        assertEquals(5, splitter.getCount());

    }

    @Test
    public void testJSONSplitterWriteWithoutInputName() throws Exception {

        JSONSplitter<StringHandle> splitter = JSONSplitter.makeArraySplitter();
        FileInputStream fileInputStream = new FileInputStream(jsonArrayFile);
        Stream<DocumentWriteOperation> contentStream = splitter.splitWriteOperations(fileInputStream);
        assertNotNull(contentStream);

        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        int i = 0;
        while (itr.hasNext()) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertNotNull(docOp.getUri());
            assertTrue(docOp.getUri().startsWith("/" + (i+1)));
            assertTrue(docOp.getUri().endsWith(".json"));

            assertNotNull(docOp.getContent());
            String docOpContent = docOp.getContent().toString();
            assertEquals(docOpContent, expectedArray[i]);
            i++;
        }

        assertEquals(5, splitter.getCount());

    }

    @Test
    public void testCustomMultiArray() throws Exception {

        MultiArrayVisitor visitor = new MultiArrayVisitor();
        JSONSplitter<StringHandle> splitter = new JSONSplitter<>(visitor);
        FileInputStream fileInputStream = new FileInputStream(jsonCustMultiArrayFile);
        Stream<StringHandle> contentStream = splitter.split(fileInputStream);
        assertNotNull(contentStream);

        StringHandle[] result = contentStream.toArray(size -> new StringHandle[size]);
        assertEquals(2, splitter.getCount());
        assertNotNull(result);

        String[] expected = new String[] {
                "{\"record\":\"first record\"}",
                "{\"record\":\"second record\"}"
        };

        for (int i = 0; i < result.length; i++) {
            String element = result[i].get();
            assertNotNull(element);
            assertEquals(expected[i], element);
        }
    }

    static public class MultiArrayVisitor extends JSONSplitter.ArrayVisitor {

        @Override
        public NodeOperation startArray(String containerKey) {
            incrementArrayDepth();
            return NodeOperation.DESCEND;
        }
    }

    @Test
    public void testCustomKey() throws Exception {

        KeyVisitor visitor = new KeyVisitor("context2");
        JSONSplitter<StringHandle> splitter = new JSONSplitter<>(visitor);
        FileInputStream fileInputStream = new FileInputStream(jsonObjectFile);
        Stream<StringHandle> contentStream = splitter.split(fileInputStream);
        assertNotNull(contentStream);

        StringHandle[] result = contentStream.toArray(size -> new StringHandle[size]);
        assertEquals(3, splitter.getCount());
        assertNotNull(result);

        String[] expected = new String[] {
                "{\"record\":\"third record\"}",
                "[{\"record\":\"forth record\"},{\"record\":\"fifth record\"}]",
                "{\"context\":[{\"record\":\"sixth record\"},{\"record\":\"seventh record\"}]}"
        };

        for (int i = 0; i < result.length; i++) {
            String element = result[i].get();
            assertNotNull(element);
            assertEquals(expected[i], element);
        }
    }

    static public class KeyVisitor extends JSONSplitter.ArrayVisitor {

        private String key;

        public KeyVisitor(String key) {
            this.key = key;
        }

        @Override
        public NodeOperation startObject(String containerKey) {
            if (getArrayDepth() > 0 && !key.equals(containerKey)) {
                return NodeOperation.SKIP;
            }

            if (getArrayDepth() > 0 && containerKey.equals(key)) {
                return NodeOperation.PROCESS;
            }

            return NodeOperation.DESCEND;
        }

        @Override
        public NodeOperation startArray(String containerKey) {
            incrementArrayDepth();

            if (getArrayDepth() > 1 && !key.equals(containerKey)) {
                decrementArrayDepth();
                return NodeOperation.SKIP;
            }

            if (getArrayDepth() > 1 && containerKey.equals(key)) {
                decrementArrayDepth();
                return NodeOperation.PROCESS;
            }

            return NodeOperation.DESCEND;
        }
    }

    @Test
    public void splitClosesInputStreamWhenStreamIsClosed() throws Exception {
        JSONSplitter<StringHandle> splitter = JSONSplitter.makeArraySplitter();
        CloseTrackingInputStream input = new CloseTrackingInputStream(new ByteArrayInputStream(largeJsonArray()));

        try (Stream<StringHandle> stream = splitter.split(input)) {
            assertTrue(stream.findFirst().isPresent());
            assertFalse(input.closed.get(), "The input stream should remain open until the stream is closed");
        }

        assertTrue(input.closed.get(), "Closing the stream should close the underlying input stream");
    }

    @Test
    public void splitWriteOperationsClosesInputStreamWhenStreamIsClosed() throws Exception {
        JSONSplitter<StringHandle> splitter = JSONSplitter.makeArraySplitter();
        CloseTrackingInputStream input = new CloseTrackingInputStream(new ByteArrayInputStream(largeJsonArray()));

        try (Stream<DocumentWriteOperation> stream = splitter.splitWriteOperations(input)) {
            assertTrue(stream.findFirst().isPresent());
            assertFalse(input.closed.get(), "The input stream should remain open until the stream is closed");
        }

        assertTrue(input.closed.get(), "Closing the stream should close the underlying input stream");
    }

    // Large enough that the parser does not reach the end of the input (and auto-close it) after the first element.
    private static byte[] largeJsonArray() {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < 5000; i++) {
            json.append(i > 0 ? "," : "").append("{\"record\":").append(i).append("}");
        }
        return json.append("]").toString().getBytes(StandardCharsets.UTF_8);
    }

    private static class CloseTrackingInputStream extends InputStream {
        final AtomicBoolean closed = new AtomicBoolean(false);
        private final InputStream delegate;

        CloseTrackingInputStream(InputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public int read() throws IOException {
            return delegate.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return delegate.read(b, off, len);
        }

        @Override
        public void close() throws IOException {
            closed.set(true);
            delegate.close();
        }
    }
}
