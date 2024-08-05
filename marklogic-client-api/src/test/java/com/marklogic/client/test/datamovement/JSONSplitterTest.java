/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.test.datamovement;

import com.marklogic.client.datamovement.JSONSplitter;
import com.marklogic.client.datamovement.NodeOperation;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
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

        JSONSplitter splitter = JSONSplitter.makeArraySplitter();
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

        JSONSplitter splitter = JSONSplitter.makeArraySplitter();
        FileInputStream fileInputStream = new FileInputStream(new File(jsonObjectFile));
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

        JSONSplitter splitter = JSONSplitter.makeArraySplitter();
        FileInputStream fileInputStream = new FileInputStream(new File(jsonMultiArrayFile));
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
        JSONSplitter splitter = JSONSplitter.makeArraySplitter();
        FileInputStream fileInputStream = new FileInputStream(new File(jsonArrayFile));
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

        JSONSplitter splitter = JSONSplitter.makeArraySplitter();
        FileInputStream fileInputStream = new FileInputStream(new File(jsonArrayFile));
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

        JSONSplitter splitter = JSONSplitter.makeArraySplitter();
        FileInputStream fileInputStream = new FileInputStream(new File(jsonArrayFile));
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
        JSONSplitter splitter = new JSONSplitter(visitor);
        FileInputStream fileInputStream = new FileInputStream(new File(jsonCustMultiArrayFile));
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
        JSONSplitter splitter = new JSONSplitter(visitor);
        FileInputStream fileInputStream = new FileInputStream(new File(jsonObjectFile));
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
}
