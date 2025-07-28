/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.dataservices;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.IOEndpoint;
import com.marklogic.client.dataservices.InputOutputCaller;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorListenerInputOutputEndpointTest {
    static ObjectNode apiObj;
    static String apiName = "errorListenerBulkIOInputOutputCaller.api";
    static String scriptPath;
    static String apiPath;
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
    public void testInputOutputCallerWithRetry() {


        String              endpointState = "{\"next\":1}";
        String              endpointConstants      = "{\"collection\":\"bulkInputOutputTest_1\"}";

        String              endpointState1 = "{\"next\":1}";
        String              endpointConstants1      = "{\"collection\":\"bulkInputOutputTest_2\"}";
        Set<String>         input          = IOTestUtil.setOf(
                "{\"docNum\":1, \"docName\":\"doc1\"}",
                "{\"docNum\":2, \"docName\":\"doc2\"}",
                "{\"docNum\":3, \"docName\":\"doc3\"}",
                "{\"docNum\":4, \"docName\":\"doc4\"}",
                "{\"docNum\":5, \"docName\":\"doc5\"}",
                "{\"docNum\":6, \"docName\":\"doc6\"}",
                "{\"docNum\":7, \"docName\":\"doc7\"}",
                "{\"docNum\":8, \"docName\":\"doc8\"}",
                "{\"docNum\":9, \"docName\":\"doc9\"}",
                "{\"docNum\":10, \"docName\":\"doc10\"}",
                "{\"docNum\":11, \"docName\":\"doc11\"}",
                "{\"docNum\":12, \"docName\":\"doc12\"}",
                "{\"docNum\":13, \"docName\":\"doc13\"}",
                "{\"docNum\":14, \"docName\":\"doc14\"}",
                "{\"docNum\":15, \"docName\":\"doc15\"}",
                "{\"docNum\":16, \"docName\":\"doc16\"}"
        );
        Set<String> output = new ConcurrentHashMap<String, Long>().keySet(1L);

        InputOutputCaller<InputStream,InputStream> endpoint = InputOutputCaller.on(
                IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle(), new InputStreamHandle());
        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withEndpointStateAs(endpointState)
                .withEndpointConstantsAs(endpointConstants),
                endpoint.newCallContext()
                        .withEndpointStateAs(endpointState1)
                        .withEndpointConstantsAs(endpointConstants1)};
        InputOutputCaller.BulkInputOutputCaller<InputStream,InputStream> bulkCaller = endpoint.bulkCaller(callContextArray);


        InputOutputCaller.BulkInputOutputCaller.ErrorListener errorListener =
                (retryCount, throwable, callContext, inputHandles) -> {
            // System.out.println("retry count is "+retryCount);
            return IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.RETRY;
                };
        bulkCaller.setErrorListener(errorListener);


        bulkCaller.setOutputListener(value -> {
            String v = NodeConverter.InputStreamToString(value);
            output.add(v);
            // System.out.println("value is "+v);
        });

        input.stream().forEach(value -> bulkCaller.accept(IOTestUtil.asInputStream(value)));
        bulkCaller.awaitCompletion();

        assertEquals(input.size(), output.size());
        assertEquals( input, output);
        assertTrue( output.size() == 16);
    }

    @Test
    public void testInputOutputCallerWithSkip() {


        String              endpointState = "{\"next\":1}";
        String              endpointConstants      = "{\"errorMin\":5,\"errorMax\":9,\"collection\":\"bulkInputOutputTest_1\"}";

        String              endpointState1 = "{\"next\":1}";
        String              endpointConstants1      = "{\"errorMin\":5,\"errorMax\":9,\"collection\":\"bulkInputOutputTest_2\"}";
        Set<String>         input          = IOTestUtil.setOf(
                "{\"docNum\":1, \"docName\":\"doc1\"}",
                "{\"docNum\":2, \"docName\":\"doc2\"}",
                "{\"docNum\":3, \"docName\":\"doc3\"}",
                "{\"docNum\":4, \"docName\":\"doc4\"}",
                "{\"docNum\":5, \"docName\":\"doc5\"}",
                "{\"docNum\":6, \"docName\":\"doc6\"}",
                "{\"docNum\":7, \"docName\":\"doc7\"}",
                "{\"docNum\":8, \"docName\":\"doc8\"}",
                "{\"docNum\":9, \"docName\":\"doc9\"}",
                "{\"docNum\":10, \"docName\":\"doc10\"}",
                "{\"docNum\":11, \"docName\":\"doc11\"}",
                "{\"docNum\":12, \"docName\":\"doc12\"}",
                "{\"docNum\":13, \"docName\":\"doc13\"}",
                "{\"docNum\":14, \"docName\":\"doc14\"}",
                "{\"docNum\":15, \"docName\":\"doc15\"}",
                "{\"docNum\":16, \"docName\":\"doc16\"}"
        );
        Set<String> output = new ConcurrentHashMap<String, Long>().keySet(1L);

        InputOutputCaller<InputStream,InputStream> endpoint = InputOutputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle(), new InputStreamHandle());
        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withEndpointStateAs(endpointState)
                .withEndpointConstantsAs(endpointConstants),
                endpoint.newCallContext()
                        .withEndpointStateAs(endpointState1)
                        .withEndpointConstantsAs(endpointConstants1)};
        InputOutputCaller.BulkInputOutputCaller<InputStream,InputStream> bulkCaller = endpoint.bulkCaller(callContextArray);


        InputOutputCaller.BulkInputOutputCaller.ErrorListener errorListener =
                (retryCount, throwable, callContext, inputHandles)
                        -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.SKIP_CALL;
        bulkCaller.setErrorListener(errorListener);


        bulkCaller.setOutputListener(value -> {output.add(NodeConverter.InputStreamToString(value));
            //System.out.println("value is "+value);
        });

        input.stream().forEach(value -> bulkCaller.accept(IOTestUtil.asInputStream(value)));
        bulkCaller.awaitCompletion();
        assertTrue(output.size() < 16 && output.size() >= 8, "wrong output size, output = " + output);
    }

    @Test
    public void testInputOutputCallerWithStop() throws IOException {


        String              endpointState = "{\"next\":1}";
        String              endpointConstants      = "{\"errorMin\":5,\"errorMax\":9,\"collection\":\"bulkInputOutputTest_1\"}";

        String              endpointState1 = "{\"next\":1}";
        String              endpointConstants1      = "{\"errorMin\":5,\"errorMax\":9,\"collection\":\"bulkInputOutputTest_2\"}";
        Set<String>         input          = IOTestUtil.setOf(
                "{\"docNum\":1, \"docName\":\"doc1\"}",
                "{\"docNum\":2, \"docName\":\"doc2\"}",
                "{\"docNum\":3, \"docName\":\"doc3\"}",
                "{\"docNum\":4, \"docName\":\"doc4\"}",
                "{\"docNum\":5, \"docName\":\"doc5\"}",
                "{\"docNum\":6, \"docName\":\"doc6\"}",
                "{\"docNum\":7, \"docName\":\"doc7\"}",
                "{\"docNum\":8, \"docName\":\"doc8\"}",
                "{\"docNum\":9, \"docName\":\"doc9\"}",
                "{\"docNum\":10, \"docName\":\"doc10\"}",
                "{\"docNum\":11, \"docName\":\"doc11\"}",
                "{\"docNum\":12, \"docName\":\"doc12\"}",
                "{\"docNum\":13, \"docName\":\"doc13\"}",
                "{\"docNum\":14, \"docName\":\"doc14\"}",
                "{\"docNum\":15, \"docName\":\"doc15\"}",
                "{\"docNum\":16, \"docName\":\"doc16\"}"
        );
        Set<String> output = new ConcurrentHashMap<String, Long>().keySet(1L);

        InputOutputCaller<InputStream,InputStream> endpoint = InputOutputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle(), new InputStreamHandle());
        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withEndpointStateAs(endpointState)
                .withEndpointConstantsAs(endpointConstants),
                endpoint.newCallContext()
                        .withEndpointStateAs(endpointState1)
                        .withEndpointConstantsAs(endpointConstants1)};
        InputOutputCaller.BulkInputOutputCaller<InputStream,InputStream> bulkCaller = endpoint.bulkCaller(callContextArray);


        InputOutputCaller.BulkInputOutputCaller.ErrorListener errorListener =
                (retryCount, throwable, callContext, inputHandles)
                        -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.STOP_ALL_CALLS;
        bulkCaller.setErrorListener(errorListener);


        bulkCaller.setOutputListener(value -> {output.add(NodeConverter.InputStreamToString(value));
            //System.out.println("value is "+value);
        });

        input.stream().forEach(value -> bulkCaller.accept(IOTestUtil.asInputStream(value)));
        bulkCaller.awaitCompletion();
        assertTrue(output.size() < 16 && output.size() >= 0, "wrong output size, output = " + output);
    }

    @AfterAll
    public static void cleanup() {
        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }

}
