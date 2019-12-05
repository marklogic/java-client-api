/*
 * Copyright 2019 MarkLogic Corporation
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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.ExecEndpoint;
import com.marklogic.client.dataservices.InputEndpoint;
import com.marklogic.client.dataservices.InputOutputEndpoint;
import com.marklogic.client.dataservices.OutputEndpoint;
import com.marklogic.client.io.JacksonHandle;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class IOCallerImplTest {
    @Test
    public void testExecCallerImpl() throws IOException {
        String apiName = "execCallerImpl.api";

        ObjectNode apiObj     = IOTestUtil.readApi(apiName);
        String     scriptPath = IOTestUtil.getScriptPath(apiObj);
        String     apiPath    = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);

        String endpointState = getEndpointState();
        String workUnit      = getWorkUnit();

        ExecEndpoint caller = ExecEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        InputStream result = caller.call(
                asInputStream(endpointState), caller.newSessionState(), asInputStream(workUnit)
        );

        checkNoInputState(endpointState, workUnit, result);

        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }
    @Test
    public void testInputCallerImpl() throws IOException {
        String apiName = "inputCallerImpl.api";

        ObjectNode apiObj     = IOTestUtil.readApi(apiName);
        String     scriptPath = IOTestUtil.getScriptPath(apiObj);
        String     apiPath    = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);

        String        endpointState = getEndpointState();
        String        workUnit      = getWorkUnit();
        InputStream[] input         = IOTestUtil.asInputStreamArray(
                "{\"docNum\":1, \"docName\":\"alpha\"}",
                "{\"docNum\":2, \"docName\":\"beta\"}"
        );

        InputEndpoint caller = InputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        InputStream result = caller.call(
                asInputStream(endpointState), caller.newSessionState(), asInputStream(workUnit), input
        );

        checkInputState(endpointState, workUnit, result);

        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }
    @Test
    public void testInputOutputCallerImpl() throws IOException {
        String apiName = "inputOutputCallerImpl.api";

        ObjectNode apiObj     = IOTestUtil.readApi(apiName);
        String     scriptPath = IOTestUtil.getScriptPath(apiObj);
        String     apiPath    = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);

        String        endpointState = getEndpointState();
        String        workUnit      = getWorkUnit();
        InputStream[] input         = IOTestUtil.asInputStreamArray(
                "{\"docNum\":1, \"docName\":\"alpha\"}",
                "{\"docNum\":2, \"docName\":\"beta\"}"
        );

        InputOutputEndpoint caller = InputOutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        InputStream[] results = caller.call(asInputStream(endpointState), caller.newSessionState(), asInputStream(workUnit), input
        );

        checkResults(endpointState, workUnit, results);

        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }
    @Test
    public void testOutputCallerImpl() throws IOException {
        String apiName = "outputCallerImpl.api";

        ObjectNode apiObj     = IOTestUtil.readApi(apiName);
        String     scriptPath = IOTestUtil.getScriptPath(apiObj);
        String     apiPath    = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);

        String endpointState = getEndpointState();
        String workUnit      = getWorkUnit();

        OutputEndpoint caller = OutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        InputStream[] results = caller.call(
                asInputStream(endpointState), caller.newSessionState(), asInputStream(workUnit)
        );

        checkResults(endpointState, workUnit, results);

        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }

    // conveniences
    private String getEndpointState() {
        return "{\"offset\":0}";
    }
    private String getWorkUnit() {
        return "{\"collection\":\"/dataset1\"}";
    }
    private InputStream asInputStream(String value) {
        return new ByteArrayInputStream(value.getBytes());
    }
    private void checkResults(String endpointState, String workUnit, InputStream[] results) throws IOException {
        assertNotNull("null result Stream<InputStream>", results);

        assertEquals("mismatch for result size", 3, results.length);

        checkNoInputState(endpointState, workUnit, results[0]);
        checkDoc1(results[1]);
        checkDoc2(results[2]);
    }
    private void checkNoInputState(String endpointState, String workUnit, InputStream result) throws IOException {
        ObjectNode resultObj = checkResult(result);
        checkEndpointState(endpointState, resultObj);
        checkWorkUnit(workUnit,           resultObj);
    }
    private void checkInputState(String endpointState, String workUnit, InputStream result) throws IOException {
        ObjectNode resultObj = checkResult(result);

        checkEndpointState(endpointState, resultObj);
        checkWorkUnit(workUnit,           resultObj);

        JsonNode input = resultObj.get("input");
        assertNotNull("null for property input", input);
        assertTrue("property input is not array", input.isArray());
        assertEquals("mismatch for input size", 2, input.size());
        checkDoc1(input.get(0));
        checkDoc2(input.get(1));
    }
    private void checkEndpointState(String endpointState, ObjectNode result) throws IOException {
        checkContainer("endpointState", endpointState, result);
    }
    private void checkWorkUnit(String workUnit, ObjectNode result) throws IOException {
        checkContainer("workUnit", workUnit, result);
    }
    private void checkDoc1(InputStream doc) throws IOException {
        checkDoc1(checkResult(doc));
    }
    private void checkDoc1(JsonNode doc) {
        checkValue("docNum",  1,       doc);
        checkValue("docName", "alpha", doc);
    }
    private void checkDoc2(InputStream doc) throws IOException {
        checkDoc2(checkResult(doc));
    }
    private void checkDoc2(JsonNode doc) {
        checkValue("docNum",  2,       doc);
        checkValue("docName", "beta",  doc);
    }
    private ObjectNode checkResult(InputStream result) throws IOException {
        assertNotNull("null InputStream", result);
        return IOTestUtil.mapper.readValue(result, ObjectNode.class);
    }
    private void checkContainer(String propName, String expected, ObjectNode result) {
        JsonNode actual = result.get(propName);
        assertNotNull("null for property "+propName, actual);
        assertEquals("mismatch for property "+propName, expected, actual.toString());
    }
    private void checkValue(String propName, int expected, JsonNode result) {
        JsonNode actual = result.get(propName);
        assertNotNull("null for property "+propName, actual);
        assertEquals("mismatch for property "+propName, expected, actual.asInt());
    }
    private void checkValue(String propName, String expected, JsonNode result) {
        JsonNode actual = result.get(propName);
        assertNotNull("null for property "+propName, actual);
        assertEquals("mismatch for property "+propName, expected, actual.asText());
    }
}
