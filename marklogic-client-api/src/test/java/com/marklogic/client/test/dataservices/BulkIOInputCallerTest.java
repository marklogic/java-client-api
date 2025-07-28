/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.dataservices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.IOEndpoint;
import com.marklogic.client.dataservices.InputCaller;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class BulkIOInputCallerTest {

    static ObjectNode[] apiObj = new ObjectNode[2];
    static String[] apiNames = new String[]{"bulkIOInputCaller.api", "bulkIOAnyDocumentInputCaller.api"};
    static String scriptPath;
    static String apiPath;
    static JSONDocumentManager docMgr;
    int counter = 0;
    static Map<String, Integer> map;

    @BeforeAll
    public static void setup() throws Exception {
        docMgr = IOTestUtil.db.newJSONDocumentManager();
        for (int i = 0; i < apiNames.length; i++) {
            String apiName = apiNames[i];
            apiObj[i] = IOTestUtil.readApi(apiName);
            scriptPath = IOTestUtil.getScriptPath(apiObj[i]);
            apiPath = IOTestUtil.getApiPath(scriptPath);
            IOTestUtil.load(apiName, apiObj[i], scriptPath, apiPath);
        }
        map = new HashMap<>();
    }
    @Test
    public void bulkInputEndpointTestWithMultipleCallContexts() {

        String endpointState = "{\"next\":"+1+"}";
        String endpointConstants      = "{\"max\":6,\"collection\":\"bulkInputTest_1\"}";

        String endpointState1 = "{\"next\":"+1+"}";
        String endpointConstants1      = "{\"max\":6,\"collection\":\"bulkInputTest_2\"}";

        InputCaller<InputStream> loadEndpt = InputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj[0]), new InputStreamHandle());
        IOEndpoint.CallContext[] callContextArray = {loadEndpt.newCallContext()
                .withEndpointStateAs(endpointState)
                .withEndpointConstantsAs(endpointConstants), loadEndpt.newCallContext()
                .withEndpointStateAs(endpointState1)
                .withEndpointConstantsAs(endpointConstants1)};
        InputCaller.BulkInputCaller<InputStream> loader = loadEndpt.bulkCaller(callContextArray);

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
        assertEquals(4, counter);
        assertTrue(
                map.get("bulkInputTest_1") >= 1);
        assertTrue(
                map.get("bulkInputTest_2") >= 1);
    }

	@Disabled("Fails intermittently on Jenkins, never locally. Will revisit if we make enhancements to Bulk DS.")
    @Test
    public void bulkInputEndpointTestWithAnyDocument() {

        String endpointState = "{\"next\":"+1+"}";
        String endpointConstants      = "{\"max\":6,\"collection\":\"bulkInputTest_1\"}";

        String endpointState1 = "{\"next\":"+1+"}";
        String endpointConstants1      = "{\"max\":6,\"collection\":\"bulkInputTest_2\"}";

        InputCaller<StringHandle> loadEndpt = InputCaller.onHandles(IOTestUtil.db, new JacksonHandle(apiObj[1]), new StringHandle());
        IOEndpoint.CallContext[] callContextArray = {loadEndpt.newCallContext()
                .withEndpointStateAs(endpointState)
                .withEndpointConstantsAs(endpointConstants), loadEndpt.newCallContext()
                .withEndpointStateAs(endpointState1)
                .withEndpointConstantsAs(endpointConstants1)};
        InputCaller.BulkInputCaller<StringHandle> loader = loadEndpt.bulkCaller(callContextArray);

        Set<String> input         = IOTestUtil.setOf( // Set.of(
                "{\"docNum\":1, \"docName\":\"doc1\"}",
                "{\"docNum\":2, \"docName\":\"doc2\"}",
                "{\"docNum\":3, \"docName\":\"doc3\"}",
                "{\"docNum\":4, \"docName\":\"doc4\"}",
                "{\"docNum\":5, \"docName\":\"doc5\"}",
                "{\"docNum\":6, \"docName\":\"doc6\"}",
                "{\"docNum\":7, \"docName\":\"doc7\"}",
                "{\"docNum\":8, \"docName\":\"doc8\"}"
        );

        input.stream().forEach(value -> {
            //System.out.println("adding "+value);
            loader.accept(new StringHandle(value).withFormat(Format.JSON));
        });
        loader.awaitCompletion();
        checkDocuments("bulkInputTest_1");
        checkDocuments("bulkInputTest_2");
        assertTrue( counter == 4);
        assertTrue(
                map.get("bulkInputTest_1") >= 1);
        assertTrue(
                map.get("bulkInputTest_2") >= 1);
    }

    @AfterAll
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
                assertNotNull(doc);
                assertEquals( i, doc.get("state").get("next").asInt());
                assertEquals( 6, doc.get("work").get("max").asInt());
                counter++;
                map.put(collection, map.get(collection)!=null?map.get(collection)+1:1);
            }
        }
    }
}
