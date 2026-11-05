/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
import com.marklogic.client.test.AbstractClientTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

public class BulkIOInputCallerTest extends AbstractClientTest {

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

    @Test
    public void bulkInputEndpointTestWithAnyDocument() {
        final String collection1 = "bulkInputAnyDocTest_1";
        final String collection2 = "bulkInputAnyDocTest_2";
        final String endpointState = "{\"next\":1}";

        InputCaller<StringHandle> loadEndpt = InputCaller.onHandles(IOTestUtil.db, new JacksonHandle(apiObj[1]), new StringHandle());
        IOEndpoint.CallContext[] callContextArray = {
            loadEndpt.newCallContext()
                .withEndpointStateAs(endpointState)
                .withEndpointConstantsAs("{\"max\":6,\"collection\":\"" + collection1 + "\"}"),
            loadEndpt.newCallContext()
                .withEndpointStateAs(endpointState)
                .withEndpointConstantsAs("{\"max\":6,\"collection\":\"" + collection2 + "\"}")
        };
        InputCaller.BulkInputCaller<StringHandle> loader = loadEndpt.bulkCaller(callContextArray);

        Set<Integer> expectedDocNums = new TreeSet<>();
        for (int i = 1; i <= 8; i++) {
            expectedDocNums.add(i);
            loader.accept(new StringHandle("{\"docNum\":" + i + ", \"docName\":\"doc" + i + "\"}").withFormat(Format.JSON));
        }
        loader.awaitCompletion();

        // How the batches are spread across the two call contexts depends on thread timing, so only assert on the
        // union of the inputs that were written, not on which collection or URI received them.
        List<Integer> actualDocNums = new ArrayList<>();
        for (String collection : new String[]{collection1, collection2}) {
            for (int next = 2; next <= 6; next++) {
                String uri = "/marklogic/ds/test/bulkInputCaller/" + collection + "/" + next + ".json";
                if (docMgr.exists(uri) != null) {
                    JsonNode doc = docMgr.read(uri, new JacksonHandle()).get();
                    assertEquals(next, doc.get("state").get("next").asInt());
                    assertEquals(6, doc.get("work").get("max").asInt());
                    doc.get("inputs").forEach(input -> actualDocNums.add(input.get("docNum").asInt()));
                }
            }
        }
        Collections.sort(actualDocNums);
        assertEquals(new ArrayList<>(expectedDocNums), actualDocNums,
            "Every input should have been written exactly once, regardless of how batches were distributed");
    }

    @AfterAll
    public static void cleanup(){
        IOTestUtil.modMgr.delete(scriptPath, apiPath);
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
