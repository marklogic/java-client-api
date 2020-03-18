package com.marklogic.client.test.datamovement;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.marklogic.client.datamovement.JSONSplitter;
import com.marklogic.client.datamovement.NodeOperation;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.stream.Stream;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JSONSplitterTest {
    static final private String jsonObjectFile = "src/test/resources/data" + File.separator + "JsonSplitterObject.json";
    static final private String jsonArrayFile = "src/test/resources/data" + File.separator + "JsonSplitterArray.json";
    static final private String jsonMultiArrayFile = "src/test/resources/data" + File.separator + "JsonMultiDiArray.json";
    static final private String jsonCustMultiArrayFile = "src/test/resources/data" + File.separator + "jsonCustMultiArray.json";
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
    public void testJSONSplitterWrite() throws Exception {

        JSONSplitter splitter = JSONSplitter.makeArraySplitter();
        FileInputStream fileInputStream = new FileInputStream(new File(jsonArrayFile));
        Stream<DocumentWriteOperation> contentStream = splitter.splitWriteOperations(new JsonFactory().createParser(fileInputStream));
        assertNotNull(contentStream);

        Iterator<DocumentWriteOperation> itr = contentStream.iterator();
        int i = 0;
        while (itr.hasNext()) {
            DocumentWriteOperation docOp = itr.next();
            String uri = docOp.getUri();
            assertNotNull(docOp.getUri());

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

    static public class MultiArrayVisitor extends JSONSplitter.Visitor {

        @Override
        public NodeOperation startArray(String containerKey) {
            arrayDepth++;
            return NodeOperation.DESCEND;
        }

        @Override
        public StringHandle makeBufferedHandle(JsonParser containerParser) {
            if (containerParser == null) {
                throw new IllegalArgumentException("JsonParser cannot be null");
            }
            String content = serialize(containerParser);
            return new StringHandle(content).withFormat(Format.JSON);
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

    static public class KeyVisitor extends JSONSplitter.Visitor {

        private String key;

        public KeyVisitor(String key) {
            this.key = key;
        }

        @Override
        public NodeOperation startObject(String containerKey) {
            if (arrayDepth > 0 && !key.equals(containerKey)) {
                return NodeOperation.SKIP;
            }

            if (arrayDepth > 0 && containerKey.equals(key)) {
                return NodeOperation.PROCESS;
            }

            return NodeOperation.DESCEND;
        }

        @Override
        public NodeOperation startArray(String containerKey) {
            arrayDepth++;

            if (arrayDepth > 1 && !key.equals(containerKey)) {
                arrayDepth--;
                return NodeOperation.SKIP;
            }

            if (arrayDepth > 1 && containerKey.equals(key)) {
                arrayDepth--;
                return NodeOperation.PROCESS;
            }

            return NodeOperation.DESCEND;
        }

        @Override
        public void endArray(String containerKey) {
            arrayDepth--;
        }

        @Override
        public StringHandle makeBufferedHandle(JsonParser containerParser) {
            if (containerParser == null) {
                throw new IllegalArgumentException("JsonParser cannot be null");
            }
            String content = serialize(containerParser);
            return new StringHandle(content).withFormat(Format.JSON);
        }
    }
}
