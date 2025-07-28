/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.dataservices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.InputCaller;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BulkInputCallerTest {
    static ObjectNode apiObj;
    static String apiName = "bulkInputCallerImpl.api";
    static String scriptPath;
    static String apiPath;
    static int workMax = 3;
    static int startValue = 1;
    static JSONDocumentManager docMgr;

    @BeforeAll
    public static void setup() throws Exception {
        docMgr = IOTestUtil.db.newJSONDocumentManager();
        apiObj = IOTestUtil.readApi(apiName);
        scriptPath = IOTestUtil.getScriptPath(apiObj);
        apiPath = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);
    }

    @Test
    public void bulkInputEndpointTest() {

        String endpointState = "{\"next\":"+startValue+"}";
        String endpointConstants      = "{\"max\":"+workMax+"}";

        InputCaller<InputStream> loadEndpt = InputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle());

        InputCaller.BulkInputCaller<InputStream> loader = loadEndpt.bulkCaller(loadEndpt.newCallContext()
                .withEndpointStateAs(endpointState)
                .withEndpointConstantsAs(endpointConstants.getBytes()));

        Stream<InputStream> input         = Stream.of(
                IOTestUtil.asInputStream("{\"docNum\":1, \"docName\":\"doc1\"}"),
                IOTestUtil.asInputStream("{\"docNum\":2, \"docName\":\"doc2\"}"),
                IOTestUtil.asInputStream("{\"docNum\":3, \"docName\":\"doc3\"}")
        );
        input.forEach(loader::accept);
        loader.awaitCompletion();

        for (int startNext=startValue; startNext < workMax; startNext++) {
            int endNext=startNext+1;
            String uri = "/marklogic/ds/test/bulkInputCaller/"+endNext+".json";
            JsonNode doc = docMgr.read(uri, new JacksonHandle()).get();
            assertNotNull(doc, "Could not find file "+uri);
            assertEquals(endNext, doc.get("state").get("next").asInt());
            assertEquals(workMax, doc.get("work").get("max").asInt());
            JsonNode inputs = doc.get("inputs");
            int docCount = (endNext == workMax) ? 1 : 2;
            assertEquals(docCount, inputs.size());
            for (int j=0; j < docCount; j++) {
                int offset = j + (startNext * 2) - 1;
                JsonNode inputDoc = inputs.get(j);
                assertEquals(offset, inputDoc.get("docNum").asInt());
                assertEquals("doc"+offset, inputDoc.get("docName").asText());
            }
        }
    }

    @Test
    public void bulkInputEndpointInterruptTest() throws Exception {
        String apiName = "bulkInputCallerImpl.api";

        String endpointState = "{\"next\":"+startValue+"}";
        String endpointConstants      = "{\"max\":"+workMax+"}";
        ObjectNode apiObj     = IOTestUtil.readApi(apiName);
        String     scriptPath = IOTestUtil.getScriptPath(apiObj);
        String     apiPath    = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);
        InputCaller<InputStream> loadEndpt = InputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle());

        InputCaller.BulkInputCaller<InputStream> loader = loadEndpt.bulkCaller(loadEndpt.newCallContext()
                .withEndpointConstantsAs(endpointConstants)
                .withEndpointStateAs(endpointState));

        Stream<InputStream> input         = Stream.of(
                IOTestUtil.asInputStream("{\"docNum\":1, \"docName\":\"doc1\"}"),
                IOTestUtil.asInputStream("{\"docNum\":2, \"docName\":\"doc2\"}"),
                IOTestUtil.asInputStream("{\"docNum\":3, \"docName\":\"doc3\"}")
        );

        Stream<InputStream> input2         = Stream.of(
                IOTestUtil.asInputStream("{\"docNum\":4, \"docName\":\"doc4\"}"),
                IOTestUtil.asInputStream("{\"docNum\":5, \"docName\":\"doc5\"}"),
                IOTestUtil.asInputStream("{\"docNum\":6, \"docName\":\"doc6\"}"));

        input.forEach(loader::accept);
        loader.interrupt();
        assertThrows(IllegalStateException.class, () -> input2.forEach(loader::accept));
    }

    @AfterAll
    public static void cleanup() {
        IOTestUtil.modMgr.delete(scriptPath, apiPath);
        for(int i=startValue+1; i<workMax; i++) {
            String uri = "/marklogic/ds/test/bulkInputCaller/" +i+".json";
            docMgr.delete(uri);
        }
    }
}
