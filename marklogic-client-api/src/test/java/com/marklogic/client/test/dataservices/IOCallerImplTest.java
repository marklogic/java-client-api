/*
 * Copyright (c) 2023 MarkLogic Corporation
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
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class IOCallerImplTest {
    @Test
    public void testExecCallerImpl() throws IOException {
        String apiName = "execCallerImpl.api";

        ObjectNode apiObj     = IOTestUtil.readApi(apiName);
        String     scriptPath = IOTestUtil.getScriptPath(apiObj);
        String     apiPath    = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);

        String endpointState = getEndpointState();
        String endpointConstants = getEndpointConstants();

        ExecEndpoint caller = ExecEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        InputStream result = caller.call(
                asInputStream(endpointState), caller.newSessionState(), asInputStream(endpointConstants)
        );

        checkNoInputState(endpointState, endpointConstants, result);

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
        String        endpointConstants      = getEndpointConstants();
        InputStream[] input         = IOTestUtil.asInputStreamArray(
                "{\"docNum\":1, \"docName\":\"alpha\"}",
                "{\"docNum\":2, \"docName\":\"beta\"}"
        );

        InputEndpoint caller = InputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        InputStream result = caller.call(
                asInputStream(endpointState), caller.newSessionState(), asInputStream(endpointConstants), input
        );

        checkInputState(endpointState, endpointConstants, result);

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
        String        endpointConstants      = getEndpointConstants();
        InputStream[] input         = IOTestUtil.asInputStreamArray(
                "{\"docNum\":1, \"docName\":\"alpha\"}",
                "{\"docNum\":2, \"docName\":\"beta\"}"
        );

        InputOutputEndpoint caller = InputOutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        InputStream[] results = caller.call(asInputStream(endpointState), caller.newSessionState(), asInputStream(endpointConstants), input
        );

        checkResults(endpointState, endpointConstants, results);

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
        String endpointConstants      = getEndpointConstants();

        OutputEndpoint caller = OutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        InputStream[] results = caller.call(
                asInputStream(endpointState), caller.newSessionState(), asInputStream(endpointConstants)
        );

        checkResults(endpointState, endpointConstants, results);

        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }

    // conveniences
    private String getEndpointState() {
        return "{\"offset\":0}";
    }
    private String getEndpointConstants() {
        return "{\"collection\":\"/dataset1\"}";
    }
    private InputStream asInputStream(String value) {
        return new ByteArrayInputStream(value.getBytes());
    }
    private void checkResults(String endpointState, String endpointConstants, InputStream[] results) throws IOException {
        assertNotNull(results);

        assertEquals( 3, results.length);

        checkNoInputState(endpointState, endpointConstants, results[0]);
        checkDoc1(results[1]);
        checkDoc2(results[2]);
    }
    private void checkNoInputState(String endpointState, String endpointConstants, InputStream result) throws IOException {
        ObjectNode resultObj = checkResult(result);
        checkEndpointState(endpointState, resultObj);
        checkendpointConstants(endpointConstants,           resultObj);
    }
    private void checkInputState(String endpointState, String endpointConstants, InputStream result) throws IOException {
        ObjectNode resultObj = checkResult(result);

        checkEndpointState(endpointState, resultObj);
        checkendpointConstants(endpointConstants,           resultObj);

        JsonNode input = resultObj.get("input");
        assertNotNull( input);
        assertTrue( input.isArray());
        assertEquals( 2, input.size());
        checkDoc1(input.get(0));
        checkDoc2(input.get(1));
    }
    private void checkEndpointState(String endpointState, ObjectNode result) {
        checkContainer("endpointState", endpointState, result);
    }
    private void checkendpointConstants(String endpointConstants, ObjectNode result) {
        checkContainer("endpointConstants", endpointConstants, result);
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
        assertNotNull( result);
        return IOTestUtil.mapper.readValue(result, ObjectNode.class);
    }
    private void checkContainer(String propName, String expected, ObjectNode result) {
        JsonNode actual = result.get(propName);
        assertNotNull(actual);
        assertEquals(expected, actual.toString(), "mismatch for property "+propName);
    }
    private void checkValue(String propName, int expected, JsonNode result) {
        JsonNode actual = result.get(propName);
        assertNotNull(actual);
        assertEquals(expected, actual.asInt());
    }
    private void checkValue(String propName, String expected, JsonNode result) {
        JsonNode actual = result.get(propName);
        assertNotNull(actual);
        assertEquals(expected, actual.asText(), "mismatch for property "+propName);
    }
}
