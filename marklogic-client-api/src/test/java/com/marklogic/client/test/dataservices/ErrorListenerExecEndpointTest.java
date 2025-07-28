/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.dataservices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.ExecCaller;
import com.marklogic.client.dataservices.IOEndpoint;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JacksonHandle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorListenerExecEndpointTest {
    static ObjectNode apiObj;
    static String apiName = "errorListenerBulkIOExecCaller.api";
    static String scriptPath;
    static String apiPath;
    static JSONDocumentManager docMgr;
    static String finalStateUri1 = "/marklogic/ds/test/errorListenerbulkIOExecCallerbulkExecTest_1.json";
    static String finalStateUri2 = "/marklogic/ds/test/errorListenerbulkIOExecCallerbulkExecTest_2.json";

    @BeforeAll
    public static void setup() throws Exception {
        docMgr = IOTestUtil.db.newJSONDocumentManager();
        apiObj = IOTestUtil.readApi(apiName);
        scriptPath = IOTestUtil.getScriptPath(apiObj);
        apiPath = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);

    }

    @Test
    public void testBulkExecCallerImplWithRetry() throws IOException {
        String endpointState = "{\"next\":5}";
        String endpointConstants      = "{\"max\":15,\"collection\":\"bulkExecTest_1\"}";

        String endpointState1 = "{\"next\":16}";
        String endpointConstants1      = "{\"max\":26,\"collection\":\"bulkExecTest_2\"}";

        ExecCaller endpoint = ExecCaller.on(IOTestUtil.db, new JacksonHandle(apiObj));

        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withEndpointConstantsAs(endpointConstants)
                .withEndpointStateAs(endpointState),
                endpoint.newCallContext()
                        .withEndpointConstantsAs(endpointConstants1)
                        .withEndpointStateAs(endpointState1)};
        ExecCaller.BulkExecCaller bulkCaller = endpoint.bulkCaller(callContextArray);

        ExecCaller.BulkExecCaller.ErrorListener errorListener =
                (retryCount, throwable, callContext)
                        -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.RETRY;
        bulkCaller.setErrorListener(errorListener);
        bulkCaller.awaitCompletion();

        JSONDocumentManager docMgr = IOTestUtil.db.newJSONDocumentManager();

        JsonNode finalState1 = docMgr.read(finalStateUri1, new JacksonHandle()).get();
        JsonNode finalState2 = docMgr.read(finalStateUri2, new JacksonHandle()).get();
        assertEquals(
                finalState1.get("state").get("next").asText(), "15");
        assertEquals(
                finalState2.get("state").get("next").asText(), "26");
        assertNotNull( finalState1);
        assertTrue( finalState1.isObject());
        assertNotNull( finalState2);
        assertTrue( finalState2.isObject());
    }

    @Test
    public void testBulkExecCallerImplWithSkip() throws IOException {
        String endpointState = "{\"next\":5}";
        String endpointConstants      = "{\"max\":15,\"collection\":\"bulkExecTest_1\"}";

        String endpointState1 = "{\"next\":16}";
        String endpointConstants1      = "{\"max\":26,\"collection\":\"bulkExecTest_2\"}";

        ExecCaller endpoint = ExecCaller.on(IOTestUtil.db, new JacksonHandle(apiObj));

        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withEndpointConstantsAs(endpointConstants)
                .withEndpointStateAs(endpointState),
                endpoint.newCallContext()
                        .withEndpointConstantsAs(endpointConstants1)
                        .withEndpointStateAs(endpointState1)};
        ExecCaller.BulkExecCaller bulkCaller = endpoint.bulkCaller(callContextArray);

        ExecCaller.BulkExecCaller.ErrorListener errorListener =
                (retryCount, throwable, callContext)
                        -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.SKIP_CALL;
        bulkCaller.setErrorListener(errorListener);
        bulkCaller.awaitCompletion();

        JSONDocumentManager docMgr = IOTestUtil.db.newJSONDocumentManager();

        JsonNode finalState1 = docMgr.read(finalStateUri1, new JacksonHandle()).get();
        JsonNode finalState2 = docMgr.read(finalStateUri2, new JacksonHandle()).get();
        assertTrue(Integer.valueOf(finalState1.get("state").get("next").asText()) < 15);
        assertEquals(finalState2.get("state").get("next").asText(), "26");
        assertNotNull( finalState1);
        assertTrue( finalState1.isObject());
        assertNotNull( finalState2);
        assertTrue( finalState2.isObject());
    }

    @Test
    public void testBulkExecCallerImplWithStop() throws IOException {
        String endpointState = "{\"next\":5}";
        String endpointConstants      = "{\"max\":15,\"collection\":\"bulkExecTest_1\"}";

        String endpointState1 = "{\"next\":16}";
        String endpointConstants1      = "{\"max\":26,\"collection\":\"bulkExecTest_2\"}";

        ExecCaller endpoint = ExecCaller.on(IOTestUtil.db, new JacksonHandle(apiObj));

        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withEndpointConstantsAs(endpointConstants)
                .withEndpointStateAs(endpointState),
                endpoint.newCallContext()
                        .withEndpointConstantsAs(endpointConstants1)
                        .withEndpointStateAs(endpointState1)};
        ExecCaller.BulkExecCaller bulkCaller = endpoint.bulkCaller(callContextArray);

        ExecCaller.BulkExecCaller.ErrorListener errorListener =
                (retryCount, throwable, callContext)
                        -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.STOP_ALL_CALLS;
        bulkCaller.setErrorListener(errorListener);
        bulkCaller.awaitCompletion();

        JSONDocumentManager docMgr = IOTestUtil.db.newJSONDocumentManager();

        JsonNode finalState1 = docMgr.read(finalStateUri1, new JacksonHandle()).get();
        JsonNode finalState2 = docMgr.read(finalStateUri2, new JacksonHandle()).get();
        assertTrue(finalState1.get("state").get("next").asInt() <= 15);
        assertTrue(finalState2.get("state").get("next").asInt() <= 26);
        assertNotNull( finalState1);
        assertTrue( finalState1.isObject());
        assertNotNull( finalState2);
        assertTrue( finalState2.isObject());
    }

    @AfterEach
    public void clean() {
        docMgr.delete(finalStateUri1);
        docMgr.delete(finalStateUri2);
    }

    @AfterAll
    public static void cleanup() {
        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }
}
