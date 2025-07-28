/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.dataservices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.ExecCaller;
import com.marklogic.client.dataservices.IOEndpoint;
import com.marklogic.client.dataservices.InputOutputCaller;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class BulkIOEndpointTest {
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testBulkExecCallerImpl() throws IOException {
        String apiName = "bulkExecCaller.api";

        String finalStateUri = "/marklogic/ds/test/bulkExecCallerFinalState.json";

        int nextStart = 5;
        int workMax = 15;

        String endpointState = "{\"next\":"+nextStart+"}";
        String endpointConstants      = "{\"max\":"+workMax+"}";

        ObjectNode apiObj     = IOTestUtil.readApi(apiName);
        String     scriptPath = IOTestUtil.getScriptPath(apiObj);
        String     apiPath    = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);

        ExecCaller endpoint = ExecCaller.on(IOTestUtil.db, new JacksonHandle(apiObj));

        ExecCaller.BulkExecCaller bulkCaller = endpoint.bulkCaller(endpoint.newCallContext()
                .withEndpointConstantsAs(endpointConstants)
                .withEndpointStateAs(endpointState));
        bulkCaller.awaitCompletion();

        JSONDocumentManager docMgr = IOTestUtil.db.newJSONDocumentManager();

        JsonNode finalState = docMgr.read(finalStateUri, new JacksonHandle()).get();
        assertNotNull( finalState);
        assertTrue( finalState.isObject());

        JsonNode finalNext = finalState.get("next");
        assertNotNull( finalNext);
        assertTrue( finalNext.isNumber());
        assertEquals( workMax, finalNext.asInt());

        JsonNode finalMax = finalState.get("workMax");
        assertNotNull( finalMax);
        assertTrue( finalMax.isNumber());
        assertEquals( workMax, finalMax.asInt());

        JsonNode sessionCounter = finalState.get("sessionCounter");
        assertNotNull( sessionCounter);
        assertTrue( sessionCounter.isNumber());
        assertEquals( (workMax - nextStart) - 1, sessionCounter.asInt());

        docMgr.delete(finalStateUri);

        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }
    @Test
    public void testInputOutputCallerImpl() throws IOException {
        String apiName = "bulkInputOutputCaller.api";

        int nextStart = 1;
        int workMax   = 4;

        ObjectNode apiObj     = IOTestUtil.readApi(apiName);
        String     scriptPath = IOTestUtil.getScriptPath(apiObj);
        String     apiPath    = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);

        int batchSize = apiObj.get("$bulk").get("inputBatchSize").asInt();
        int callCount = (workMax - nextStart) / batchSize +
                (((workMax - nextStart) % batchSize) > 0 ? 1 : 0);

        String              endpointState = "{\"next\":"+nextStart+"}";
        String              endpointConstants      = "{\"max\":"+workMax+"}";
        Set<String>         input         = IOTestUtil.setOf( // Set.of(
                "{\"docNum\":1, \"docName\":\"alpha\"}",
                "{\"docNum\":2, \"docName\":\"beta\"}",
                "{\"docNum\":3, \"docName\":\"gamma\"}"
        );
        Set<String> output = new HashSet<>();

        InputOutputCaller<InputStream, InputStream> endpoint =
                InputOutputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle(), new InputStreamHandle()
                );

        IOEndpoint.CallContext callContext = endpoint.newCallContext()
                .withEndpointStateAs(endpointState)
                .withEndpointConstantsAs(endpointConstants);
        InputOutputCaller.BulkInputOutputCaller<InputStream, InputStream> bulkCaller =
                endpoint.bulkCaller(callContext);
        bulkCaller.setOutputListener(value -> {
            String v = NodeConverter.InputStreamToString(value);
            // System.out.println("received: "+v);
            output.add(v);
        });

        input.stream().forEach(value -> {
            // System.out.println("adding "+value);
            bulkCaller.accept(IOTestUtil.asInputStream(value));
        });
        bulkCaller.awaitCompletion();

        ObjectNode finalState = mapper.readValue(callContext.getEndpointState().get(), ObjectNode.class);

        assertEquals( input.size(), output.size());
        assertEquals( input, output);

        assertNotNull( finalState);
        assertTrue( finalState.isObject());

        JsonNode finalNext = finalState.get("next");
        assertNotNull( finalNext);
        assertTrue( finalNext.isNumber());
        assertEquals( workMax, finalNext.asInt());

        JsonNode finalMax = finalState.get("workMax");
        assertNotNull( finalMax);
        assertTrue( finalMax.isNumber());
        assertEquals( workMax, finalMax.asInt());

        JsonNode sessionCounter = finalState.get("sessionCounter");
        assertNotNull( sessionCounter);
        assertTrue( sessionCounter.isNumber());
        assertEquals( callCount - 1, sessionCounter.asInt());

        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }

    @Test
    public void testInputOutputCallerImplAnyDoc() throws IOException {
        String apiName = "bulkIOAnyDocumentInputOutputCaller.api";

        int nextStart = 1;
        int workMax   = 4;

        ObjectNode apiObj     = IOTestUtil.readApi(apiName);
        String     scriptPath = IOTestUtil.getScriptPath(apiObj);
        String     apiPath    = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);

        int batchSize = apiObj.get("$bulk").get("inputBatchSize").asInt();
        int callCount = (workMax - nextStart) / batchSize +
                (((workMax - nextStart) % batchSize) > 0 ? 1 : 0);

        String              endpointState = "{\"next\":"+nextStart+"}";
        String              endpointConstants      = "{\"max\":"+workMax+"}";
        Set<String>         input         = IOTestUtil.setOf( // Set.of(
                "{\"docNum\":1, \"docName\":\"alpha\"}",
                "{\"docNum\":2, \"docName\":\"beta\"}",
                "{\"docNum\":3, \"docName\":\"gamma\"}"
        );

        Set<String> output = new HashSet<>();
        InputOutputCaller<StringHandle, StringHandle> endpoint =
                InputOutputCaller.onHandles(IOTestUtil.db, new JacksonHandle(apiObj), new StringHandle(), new StringHandle());
        IOEndpoint.CallContext callContext = endpoint.newCallContext()
                .withEndpointStateAs(endpointState)
                .withEndpointConstantsAs(endpointConstants);
        InputOutputCaller.BulkInputOutputCaller<StringHandle, StringHandle> bulkCaller =
                endpoint.bulkCaller(callContext);
        bulkCaller.setOutputListener(value -> {
            String v = value.toString();
            //System.out.println("received: "+v);
            output.add(v);
        });

        input.stream().forEach(value -> {
            //System.out.println("adding "+value);
            bulkCaller.accept(new StringHandle(value).withFormat(Format.JSON));
        });

        bulkCaller.awaitCompletion();

        ObjectNode finalState = mapper.readValue(callContext.getEndpointState().get(), ObjectNode.class);

        assertEquals( input.size(), output.size());
        assertEquals( input, output);

        assertNotNull( finalState);
        assertTrue( finalState.isObject());

        JsonNode finalNext = finalState.get("next");
        assertNotNull( finalNext);
        assertTrue( finalNext.isNumber());
        assertEquals( workMax, finalNext.asInt());

        JsonNode finalMax = finalState.get("workMax");
        assertNotNull( finalMax);
        assertTrue( finalMax.isNumber());
        assertEquals( workMax, finalMax.asInt());

        JsonNode sessionCounter = finalState.get("sessionCounter");
        assertNotNull( sessionCounter);
        assertTrue( sessionCounter.isNumber());
        assertEquals( callCount - 1, sessionCounter.asInt());

        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }

    @Test
    public void testInputOutputCallerImplWithMultipleCallContexts() throws IOException {
        String apiName = "bulkIOInputOutputCaller.api";

        int nextStart = 1;
        int workMax   = 4;

        ObjectNode apiObj     = IOTestUtil.readApi(apiName);
        String     scriptPath = IOTestUtil.getScriptPath(apiObj);
        String     apiPath    = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);

        String              endpointState = "{\"next\":"+nextStart+"}";
        String              endpointConstants      = "{\"max\":"+workMax+",\"collection\":\"bulkInputOutputTest_1\"}";

        String              endpointState1 = "{\"next\":"+1+"}";
        String              endpointConstants1      = "{\"max\":"+4+",\"collection\":\"bulkInputOutputTest_2\"}";
        Set<String>         input          = IOTestUtil.setOf( // Set.of(
                "{\"docNum\":1, \"docName\":\"doc1\"}",
                "{\"docNum\":2, \"docName\":\"doc2\"}",
                "{\"docNum\":3, \"docName\":\"doc3\"}",
                "{\"docNum\":4, \"docName\":\"doc4\"}",
                "{\"docNum\":5, \"docName\":\"doc5\"}",
                "{\"docNum\":6, \"docName\":\"doc6\"}"
        );
        Set<String> output = new ConcurrentHashMap<String, Long>().keySet(1L);

        InputOutputCaller<InputStream,InputStream> endpoint = InputOutputCaller.on(
                IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle(), new InputStreamHandle()
        );
        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withEndpointStateAs(endpointState)
                .withEndpointConstantsAs(endpointConstants),
                endpoint.newCallContext()
                .withEndpointStateAs(endpointState1)
                .withEndpointConstantsAs(endpointConstants1)};

        InputOutputCaller.BulkInputOutputCaller<InputStream,InputStream> bulkCaller = endpoint.bulkCaller(callContextArray);
        bulkCaller.setOutputListener(value -> {
            String v = NodeConverter.InputStreamToString(value);
            // System.out.println("received: "+v);
            output.add(v);
            });

        input.stream().forEach(value -> {
            // System.out.println("adding: "+value);
            bulkCaller.accept(IOTestUtil.asInputStream(value));
        });
        bulkCaller.awaitCompletion();

        assertEquals(input.size(), output.size());
        assertEquals( input, output);

        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }

    @Test
    public void testBulkExecCallerImplWithMultipleCallContexts() throws IOException {
        String apiName = "bulkIOExecCaller.api";

        String finalStateUri = "/marklogic/ds/test/bulkIOExecCaller.json";

        int nextStart = 5;
        int workMax = 15;

        String endpointState = "{\"next\":"+nextStart+"}";
        String endpointConstants      = "{\"max\":"+workMax+"}";

        String endpointState1 = "{\"next\":"+16+"}";
        String endpointConstants1      = "{\"max\":"+26+"}";

        ObjectNode apiObj     = IOTestUtil.readApi(apiName);
        String     scriptPath = IOTestUtil.getScriptPath(apiObj);
        String     apiPath    = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);

        ExecCaller endpoint = ExecCaller.on(IOTestUtil.db, new JacksonHandle(apiObj));

        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withEndpointConstantsAs(endpointConstants)
                .withEndpointStateAs(endpointState),
                endpoint.newCallContext()
                        .withEndpointConstantsAs(endpointConstants1)
                        .withEndpointStateAs(endpointState1)};
        ExecCaller.BulkExecCaller bulkCaller = endpoint.bulkCaller(callContextArray);
        bulkCaller.awaitCompletion();

        JSONDocumentManager docMgr = IOTestUtil.db.newJSONDocumentManager();

        JsonNode finalState = docMgr.read(finalStateUri, new JacksonHandle()).get();
        assertNotNull( finalState);
        assertTrue( finalState.isObject());

        docMgr.delete(finalStateUri);

        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }
}
