/*
 * Copyright (c) 2019 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

public class BulkIOEndpointTest {
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testBulkExecCallerImpl() throws IOException {
        String apiName = "bulkExecCaller.api";

        String finalStateUri = "/marklogic/ds/test/bulkExecCallerFinalState.json";

        int nextStart = 5;
        int workMax = 15;

        String endpointState = "{\"next\":"+nextStart+"}";
        String workUnit      = "{\"max\":"+workMax+"}";

        ObjectNode apiObj     = IOTestUtil.readApi(apiName);
        String     scriptPath = IOTestUtil.getScriptPath(apiObj);
        String     apiPath    = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);

        ExecCaller endpoint = ExecCaller.on(IOTestUtil.db, new JacksonHandle(apiObj));

        ExecCaller.BulkExecCaller bulkCaller = endpoint.bulkCaller(endpoint.newCallContext()
                .withWorkUnitAs(workUnit)
                .withEndpointStateAs(endpointState));
        bulkCaller.awaitCompletion();

        JSONDocumentManager docMgr = IOTestUtil.db.newJSONDocumentManager();

        JsonNode finalState = docMgr.read(finalStateUri, new JacksonHandle()).get();
        assertNotNull("null final state", finalState);
        assertTrue("final state not object", finalState.isObject());

        JsonNode finalNext = finalState.get("next");
        assertNotNull("null final next", finalNext);
        assertTrue("final next not number", finalNext.isNumber());
        assertEquals("mismatch on final next", workMax, finalNext.asInt());

        JsonNode finalMax = finalState.get("workMax");
        assertNotNull("null final max", finalMax);
        assertTrue("final max not number", finalMax.isNumber());
        assertEquals("mismatch on final max", workMax, finalMax.asInt());

        JsonNode sessionCounter = finalState.get("sessionCounter");
        assertNotNull("null final sessionCounter", sessionCounter);
        assertTrue("final sessionCounter not number", sessionCounter.isNumber());
        assertEquals("mismatch on final sessionCounter", (workMax - nextStart) - 1, sessionCounter.asInt());

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
        String              workUnit      = "{\"max\":"+workMax+"}";
        Set<String>         input         = IOTestUtil.setOf( // Set.of(
                "{\"docNum\":1, \"docName\":\"alpha\"}",
                "{\"docNum\":2, \"docName\":\"beta\"}",
                "{\"docNum\":3, \"docName\":\"gamma\"}"
        );
        Set<String> output = new HashSet<>();

        InputOutputCaller<InputStream, InputStream> endpoint =
                InputOutputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle(), new InputStreamHandle()
                );

        InputOutputCaller.BulkInputOutputCaller<InputStream, InputStream> bulkCaller =
                endpoint.bulkCaller(endpoint.newCallContext()
                        .withEndpointStateAs(endpointState)
                        .withWorkUnitAs(workUnit));
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

        ObjectNode finalState = mapper.readValue(bulkCaller.getEndpointState(), ObjectNode.class);

        assertEquals("mismatch between input and output size", input.size(), output.size());
        assertEquals("mismatch between input and output elements", input, output);

        assertNotNull("null final state", finalState);
        assertTrue("final state not object", finalState.isObject());

        JsonNode finalNext = finalState.get("next");
        assertNotNull("null final next", finalNext);
        assertTrue("final next not number", finalNext.isNumber());
        assertEquals("mismatch on final next", workMax, finalNext.asInt());

        JsonNode finalMax = finalState.get("workMax");
        assertNotNull("null final max", finalMax);
        assertTrue("final max not number", finalMax.isNumber());
        assertEquals("mismatch on final max", workMax, finalMax.asInt());

        JsonNode sessionCounter = finalState.get("sessionCounter");
        assertNotNull("null final sessionCounter", sessionCounter);
        assertTrue("final sessionCounter not number", sessionCounter.isNumber());
        assertEquals("mismatch on final sessionCounter", callCount - 1, sessionCounter.asInt());

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
        String              workUnit      = "{\"max\":"+workMax+",\"collection\":\"bulkInputOutputTest_1\"}";

        String              endpointState1 = "{\"next\":"+1+"}";
        String              workUnit1      = "{\"max\":"+4+",\"collection\":\"bulkInputOutputTest_2\"}";
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
                .withWorkUnitAs(workUnit),
                endpoint.newCallContext()
                .withEndpointStateAs(endpointState1)
                .withWorkUnitAs(workUnit1)};

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

        assertEquals("mismatch between input and output size"+input+":"+output, input.size(), output.size());
        assertEquals("mismatch between input and output elements", input, output);

        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }

    @Test
    public void testBulkExecCallerImplWithMultipleCallContexts() throws IOException {
        String apiName = "bulkIOExecCaller.api";

        String finalStateUri = "/marklogic/ds/test/bulkIOExecCaller.json";

        int nextStart = 5;
        int workMax = 15;

        String endpointState = "{\"next\":"+nextStart+"}";
        String workUnit      = "{\"max\":"+workMax+"}";

        String endpointState1 = "{\"next\":"+16+"}";
        String workUnit1      = "{\"max\":"+26+"}";

        ObjectNode apiObj     = IOTestUtil.readApi(apiName);
        String     scriptPath = IOTestUtil.getScriptPath(apiObj);
        String     apiPath    = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);

        ExecCaller endpoint = ExecCaller.on(IOTestUtil.db, new JacksonHandle(apiObj));

        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withWorkUnitAs(workUnit)
                .withEndpointStateAs(endpointState),
                endpoint.newCallContext()
                        .withWorkUnitAs(workUnit1)
                        .withEndpointStateAs(endpointState1)};
        ExecCaller.BulkExecCaller bulkCaller = endpoint.bulkCaller(callContextArray);
        bulkCaller.awaitCompletion();

        JSONDocumentManager docMgr = IOTestUtil.db.newJSONDocumentManager();

        JsonNode finalState = docMgr.read(finalStateUri, new JacksonHandle()).get();
        assertNotNull("null final state", finalState);
        assertTrue("final state not object", finalState.isObject());

        docMgr.delete(finalStateUri);

        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }
}