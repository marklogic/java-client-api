package com.marklogic.client.test.dataservices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.ExecEndpoint;
import com.marklogic.client.dataservices.IOEndpoint;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JacksonHandle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class ErrorListenerExecEndpointTest {
    static ObjectNode apiObj;
    static String apiName = "errorListenerBulkIOExecCaller.api";
    static String scriptPath;
    static String apiPath;
    static JSONDocumentManager docMgr;
    static String finalStateUri1 = "/marklogic/ds/test/errorListenerbulkIOExecCallerbulkExecTest_1.json";
    static String finalStateUri2 = "/marklogic/ds/test/errorListenerbulkIOExecCallerbulkExecTest_2.json";

    @BeforeClass
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
        String workUnit      = "{\"max\":15,\"collection\":\"bulkExecTest_1\"}";

        String endpointState1 = "{\"next\":16}";
        String workUnit1      = "{\"max\":26,\"collection\":\"bulkExecTest_2\"}";

        ExecEndpoint endpoint = ExecEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));

        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withWorkUnit(new ByteArrayInputStream(workUnit.getBytes()))
                .withEndpointState(new ByteArrayInputStream(endpointState.getBytes())),
                endpoint.newCallContext()
                        .withWorkUnit(new ByteArrayInputStream(workUnit1.getBytes()))
                        .withEndpointState(new ByteArrayInputStream(endpointState1.getBytes()))};
        ExecEndpoint.BulkExecCaller bulkCaller = endpoint.bulkCaller(callContextArray);

        ExecEndpoint.BulkExecCaller.ErrorListener errorListener =
                (retryCount, throwable, callContext)
                        -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.RETRY;
        bulkCaller.setErrorListener(errorListener);
        bulkCaller.awaitCompletion();

        JSONDocumentManager docMgr = IOTestUtil.db.newJSONDocumentManager();

        JsonNode finalState1 = docMgr.read(finalStateUri1, new JacksonHandle()).get();
        JsonNode finalState2 = docMgr.read(finalStateUri2, new JacksonHandle()).get();
        assertEquals("finalState1 is wrong, should be 15, but " + finalState1.get("state").get("next").asText(),
                finalState1.get("state").get("next").asText(), "15");
        assertEquals("finalState2 is wrong, should be 26, but " + finalState2.get("state").get("next").asText(),
                finalState2.get("state").get("next").asText(), "26");
        assertNotNull("null final state", finalState1);
        assertTrue("final state not object", finalState1.isObject());
        assertNotNull("null final state", finalState2);
        assertTrue("final state not object", finalState2.isObject());
    }

    @Test
    public void testBulkExecCallerImplWithSkip() throws IOException {
        String endpointState = "{\"next\":5}";
        String workUnit      = "{\"max\":15,\"collection\":\"bulkExecTest_1\"}";

        String endpointState1 = "{\"next\":16}";
        String workUnit1      = "{\"max\":26,\"collection\":\"bulkExecTest_2\"}";

        ExecEndpoint endpoint = ExecEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));

        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withWorkUnit(new ByteArrayInputStream(workUnit.getBytes()))
                .withEndpointState(new ByteArrayInputStream(endpointState.getBytes())),
                endpoint.newCallContext()
                        .withWorkUnit(new ByteArrayInputStream(workUnit1.getBytes()))
                        .withEndpointState(new ByteArrayInputStream(endpointState1.getBytes()))};
        ExecEndpoint.BulkExecCaller bulkCaller = endpoint.bulkCaller(callContextArray);

        ExecEndpoint.BulkExecCaller.ErrorListener errorListener =
                (retryCount, throwable, callContext)
                        -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.SKIP_CALL;
        bulkCaller.setErrorListener(errorListener);
        bulkCaller.awaitCompletion();

        JSONDocumentManager docMgr = IOTestUtil.db.newJSONDocumentManager();

        JsonNode finalState1 = docMgr.read(finalStateUri1, new JacksonHandle()).get();
        JsonNode finalState2 = docMgr.read(finalStateUri2, new JacksonHandle()).get();
        assertTrue("finalState1 is wrong, should less than 15, but " + finalState1.get("state").get("next").asText(),
                Integer.valueOf(finalState1.get("state").get("next").asText()) < 15);
        assertEquals("finalState2 is wrong, should be 26, but " + finalState2.get("state").get("next").asText(),
                finalState2.get("state").get("next").asText(), "26");
        assertNotNull("null final state", finalState1);
        assertTrue("final state not object", finalState1.isObject());
        assertNotNull("null final state", finalState2);
        assertTrue("final state not object", finalState2.isObject());
    }

    @Test
    public void testBulkExecCallerImplWithStop() throws IOException {
        String endpointState = "{\"next\":5}";
        String workUnit      = "{\"max\":15,\"collection\":\"bulkExecTest_1\"}";

        String endpointState1 = "{\"next\":16}";
        String workUnit1      = "{\"max\":26,\"collection\":\"bulkExecTest_2\"}";

        ExecEndpoint endpoint = ExecEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));

        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withWorkUnit(new ByteArrayInputStream(workUnit.getBytes()))
                .withEndpointState(new ByteArrayInputStream(endpointState.getBytes())),
                endpoint.newCallContext()
                        .withWorkUnit(new ByteArrayInputStream(workUnit1.getBytes()))
                        .withEndpointState(new ByteArrayInputStream(endpointState1.getBytes()))};
        ExecEndpoint.BulkExecCaller bulkCaller = endpoint.bulkCaller(callContextArray);

        ExecEndpoint.BulkExecCaller.ErrorListener errorListener =
                (retryCount, throwable, callContext)
                        -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.STOP_ALL_CALLS;
        bulkCaller.setErrorListener(errorListener);
        bulkCaller.awaitCompletion();

        JSONDocumentManager docMgr = IOTestUtil.db.newJSONDocumentManager();

        JsonNode finalState1 = docMgr.read(finalStateUri1, new JacksonHandle()).get();
        JsonNode finalState2 = docMgr.read(finalStateUri2, new JacksonHandle()).get();
        assertTrue("finalState1 is wrong, should be less than 15, but " + finalState1.get("state").get("next").asText(),
                Integer.valueOf(finalState1.get("state").get("next").asText()) < 15);
        assertTrue("finalState2 is wrong, should be less than 26, but " + finalState2.get("state").get("next").asText(),
                Integer.valueOf(finalState2.get("state").get("next").asText()) <= 26);
        assertNotNull("null final state", finalState1);
        assertTrue("final state not object", finalState1.isObject());
        assertNotNull("null final state", finalState2);
        assertTrue("final state not object", finalState2.isObject());
    }

    @AfterClass
    public static void cleanup() {
        docMgr.delete(finalStateUri1);
        docMgr.delete(finalStateUri2);
        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }
}
