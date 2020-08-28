package com.marklogic.client.test.dataservices;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.IOEndpoint;
import com.marklogic.client.dataservices.InputOutputEndpoint;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.io.JacksonHandle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ErrorListenerInputOutputEndpointTest {
    static ObjectNode apiObj;
    static String apiName = "errorListenerBulkIOInputOutputCaller.api";
    static String scriptPath;
    static String apiPath;
    static JSONDocumentManager docMgr;

    @BeforeClass
    public static void setup() throws Exception {
        docMgr = IOTestUtil.db.newJSONDocumentManager();
        apiObj = IOTestUtil.readApi(apiName);
        scriptPath = IOTestUtil.getScriptPath(apiObj);
        apiPath = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);
    }

    @Test
    public void testInputOutputCallerWithRetry() throws IOException {


        String              endpointState = "{\"next\":1}";
        String              workUnit      = "{\"max\":4,\"collection\":\"bulkInputOutputTest_1\"}";

        String              endpointState1 = "{\"next\":1}";
        String              workUnit1      = "{\"max\":4,\"collection\":\"bulkInputOutputTest_2\"}";
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

        InputOutputEndpoint endpoint = InputOutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withEndpointState(new ByteArrayInputStream(endpointState.getBytes()))
                .withWorkUnit(new ByteArrayInputStream(workUnit.getBytes())),
                endpoint.newCallContext()
                        .withEndpointState(new ByteArrayInputStream(endpointState1.getBytes()))
                        .withWorkUnit(new ByteArrayInputStream(workUnit1.getBytes()))};
        InputOutputEndpoint.BulkInputOutputCaller bulkCaller = endpoint.bulkCaller(callContextArray);


        InputOutputEndpoint.BulkInputOutputCaller.ErrorListener errorListener =
                (retryCount, throwable, callContext, inputStreams)
                        -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.RETRY;
        bulkCaller.setErrorListener(errorListener);


        bulkCaller.setOutputListener(value -> {output.add(NodeConverter.InputStreamToString(value));
            //System.out.println("value is "+value);
        });

        input.stream().forEach(value -> bulkCaller.accept(IOTestUtil.asInputStream(value)));
        bulkCaller.awaitCompletion();

        //assertEquals("mismatch between input and output size"+input+":"+output, input.size(), output.size());
        //assertEquals("mismatch between input and output elements", input, output);
        assertTrue("wrong output size", output.size() <= 16 && output.size() >= 8);
    }

    @Test
    public void testInputOutputCallerWithSkip() throws IOException {


        String              endpointState = "{\"next\":1}";
        String              workUnit      = "{\"max\":4,\"collection\":\"bulkInputOutputTest_1\"}";

        String              endpointState1 = "{\"next\":1}";
        String              workUnit1      = "{\"max\":4,\"collection\":\"bulkInputOutputTest_2\"}";
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

        InputOutputEndpoint endpoint = InputOutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withEndpointState(new ByteArrayInputStream(endpointState.getBytes()))
                .withWorkUnit(new ByteArrayInputStream(workUnit.getBytes())),
                endpoint.newCallContext()
                        .withEndpointState(new ByteArrayInputStream(endpointState1.getBytes()))
                        .withWorkUnit(new ByteArrayInputStream(workUnit1.getBytes()))};
        InputOutputEndpoint.BulkInputOutputCaller bulkCaller = endpoint.bulkCaller(callContextArray);


        InputOutputEndpoint.BulkInputOutputCaller.ErrorListener errorListener =
                (retryCount, throwable, callContext, inputStreams)
                        -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.SKIP_CALL;
        bulkCaller.setErrorListener(errorListener);


        bulkCaller.setOutputListener(value -> {output.add(NodeConverter.InputStreamToString(value));
            //System.out.println("value is "+value);
        });

        input.stream().forEach(value -> bulkCaller.accept(IOTestUtil.asInputStream(value)));
        bulkCaller.awaitCompletion();
        assertTrue("wrong output size, output = " + output, output.size() < 16 && output.size() >= 8);
    }

    @Test
    public void testInputOutputCallerWithStop() throws IOException {


        String              endpointState = "{\"next\":1}";
        String              workUnit      = "{\"max\":4,\"collection\":\"bulkInputOutputTest_1\"}";

        String              endpointState1 = "{\"next\":1}";
        String              workUnit1      = "{\"max\":4,\"collection\":\"bulkInputOutputTest_2\"}";
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

        InputOutputEndpoint endpoint = InputOutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withEndpointState(new ByteArrayInputStream(endpointState.getBytes()))
                .withWorkUnit(new ByteArrayInputStream(workUnit.getBytes())),
                endpoint.newCallContext()
                        .withEndpointState(new ByteArrayInputStream(endpointState1.getBytes()))
                        .withWorkUnit(new ByteArrayInputStream(workUnit1.getBytes()))};
        InputOutputEndpoint.BulkInputOutputCaller bulkCaller = endpoint.bulkCaller(callContextArray);


        InputOutputEndpoint.BulkInputOutputCaller.ErrorListener errorListener =
                (retryCount, throwable, callContext, inputStreams)
                        -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.STOP_ALL_CALLS;
        bulkCaller.setErrorListener(errorListener);


        bulkCaller.setOutputListener(value -> {output.add(NodeConverter.InputStreamToString(value));
            //System.out.println("value is "+value);
        });

        input.stream().forEach(value -> bulkCaller.accept(IOTestUtil.asInputStream(value)));
        bulkCaller.awaitCompletion();
        assertTrue("wrong output size, output = " + output, output.size() < 16 && output.size() >= 0);
    }

    @AfterClass
    public static void cleanup() {
        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }

}
