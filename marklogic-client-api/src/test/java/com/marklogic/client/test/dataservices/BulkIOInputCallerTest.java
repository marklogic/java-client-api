package com.marklogic.client.test.dataservices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.IOEndpoint;
import com.marklogic.client.dataservices.InputEndpoint;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JacksonHandle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class BulkIOInputCallerTest {

    static ObjectNode apiObj;
    static String apiName = "bulkIOInputCaller.api";
    static String scriptPath;
    static String apiPath;
    static JSONDocumentManager docMgr;
    int counter = 0;
    static Map<String, Integer> map;

    @BeforeClass
    public static void setup() throws Exception {
        docMgr = IOTestUtil.db.newJSONDocumentManager();
        apiObj = IOTestUtil.readApi(apiName);
        scriptPath = IOTestUtil.getScriptPath(apiObj);
        apiPath = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);
        map = new HashMap<>();
    }
    @Test
    public void bulkInputEndpointTestWithMultipleCallContexts() {

        String endpointState = "{\"next\":"+1+"}";
        String workUnit      = "{\"max\":6,\"collection\":\"bulkInputTest_1\"}";

        String endpointState1 = "{\"next\":"+1+"}";
        String workUnit1      = "{\"max\":6,\"collection\":\"bulkInputTest_2\"}";

        InputEndpoint loadEndpt = InputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        IOEndpoint.CallContext[] callContextArray = {loadEndpt.newCallContext()
                .withEndpointState(new ByteArrayInputStream(endpointState.getBytes()))
                .withWorkUnit(new ByteArrayInputStream(workUnit.getBytes())), loadEndpt.newCallContext()
                .withEndpointState(new ByteArrayInputStream(endpointState1.getBytes()))
                .withWorkUnit(new ByteArrayInputStream(workUnit1.getBytes()))};
        InputEndpoint.BulkInputCaller loader = loadEndpt.bulkCaller(callContextArray);

        Stream<InputStream> input         = Stream.of(
                IOTestUtil.asInputStream("{\"docNum\":1, \"docName\":\"doc1\"}"),
                IOTestUtil.asInputStream("{\"docNum\":2, \"docName\":\"doc2\"}"),
                IOTestUtil.asInputStream("{\"docNum\":3, \"docName\":\"doc3\"}"),
                IOTestUtil.asInputStream("{\"docNum\":4, \"docName\":\"doc4\"}"),
                IOTestUtil.asInputStream("{\"docNum\":5, \"docName\":\"doc5\"}"),
                IOTestUtil.asInputStream("{\"docNum\":6, \"docName\":\"doc6\"}"),
                IOTestUtil.asInputStream("{\"docNum\":7, \"docName\":\"doc7\"}"),
                IOTestUtil.asInputStream("{\"docNum\":8, \"docName\":\"doc8\"}")
        );
        input.forEach(loader::accept);
        loader.awaitCompletion();
        checkDocuments("bulkInputTest_1");
        checkDocuments("bulkInputTest_2");
        assertTrue("Number of documents written not as expected.", counter == 4);
        assertTrue("No documents written by first callContext in - bulkInputTest_1 collection.",
                map.get("bulkInputTest_1") >= 1);
        assertTrue("No documents written by second callContext in - bulkInputTest_2 collection.",
                map.get("bulkInputTest_2") >= 1);
    }

    @AfterClass
    public static void cleanup(){
        IOTestUtil.modMgr.delete(scriptPath, apiPath);
        for(int i=2; i<5; i++) {
            String uri = "/marklogic/ds/test/bulkInputCaller/bulkInputTest_1/" +i+".json";
            docMgr.delete(uri);
            uri = "/marklogic/ds/test/bulkInputCaller/bulkInputTest_2/" +i+".json";
            docMgr.delete(uri);
        }
    }

    private void checkDocuments(String collection) {

        for(int i=2; i<5; i++) {
            String uri = "/marklogic/ds/test/bulkInputCaller/"+collection+"/"+i+".json";
            if(docMgr.exists(uri)!=null) {
                JsonNode doc = docMgr.read(uri, new JacksonHandle()).get();
                assertNotNull("Could not find file "+uri, doc);
                assertEquals("state mismatch", i, doc.get("state").get("next").asInt());
                assertEquals("state mismatch", 6, doc.get("work").get("max").asInt());
                counter++;
                map.put(collection, map.get(collection)!=null?map.get(collection)+1:1);
            }
        }
    }
}
