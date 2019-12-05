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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.InputEndpoint;
import com.marklogic.client.io.JacksonHandle;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class InputEndpointImplTest {

    static ObjectNode apiObj;
    static String apiName = "inputCallerImpl.api";
    static String scriptPath;
    static String apiPath;

    @BeforeClass
    public static void setup() throws Exception {
        apiObj = IOTestUtil.readApi(apiName);
        scriptPath = IOTestUtil.getScriptPath(apiObj);
        apiPath = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);
    }
    @Test
    public void testInputCallerImpl() throws IOException {

        String endpointState = "{\"offset\":0}";
        String workUnit = "{\"collection\":\"/dataset1\"}";
        InputStream[] input = IOTestUtil.asInputStreamArray("{\"docNum\":1, \"docName\":\"doc1\"}",
                "{\"docNum\":2, \"docName\":\"doc2\"}"
        );

        InputEndpoint caller = InputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));;
        InputStream result = caller.call(IOTestUtil.asInputStream(endpointState), caller.newSessionState(),
                IOTestUtil.asInputStream(workUnit), input);
        assertNotNull(result);

        ObjectNode resultObj = IOTestUtil.mapper.readValue(result, ObjectNode.class);
        assertNotNull(resultObj);
        assertEquals("Endpoint value not as expected.",endpointState, resultObj.get("endpointState").toString());
        assertEquals("Workunit not as expected.",workUnit, resultObj.get("workUnit").toString());
        assertTrue(resultObj.get("input").get(0).toString().contains("doc1"));
        assertTrue(resultObj.get("input").get(1).toString().contains("doc2"));
    }

    @Test
    public void testInputCallerImplWithNullEndpointState() throws IOException {

        String workUnit = "{\"collection\":\"/dataset1\"}";
        InputStream[] input = IOTestUtil.asInputStreamArray("{\"docNum\":1, \"docName\":\"doc1\"}",
                "{\"docNum\":2, \"docName\":\"doc2\"}");

        InputEndpoint caller = InputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));;
        InputStream result = caller.call(null, caller.newSessionState(),
                IOTestUtil.asInputStream(workUnit), input);
        assertNotNull(result);

        ObjectNode resultObj = IOTestUtil.mapper.readValue(result, ObjectNode.class);
        assertNull(resultObj.get("endpointState"));
    }

    @Test
    public void testInputCallerImplWithNullSession() throws IOException {

        String workUnit = "{\"collection\":\"/dataset1\"}";
        InputStream[] input = IOTestUtil.asInputStreamArray("{\"docNum\":1, \"docName\":\"doc1\"}",
                "{\"docNum\":2, \"docName\":\"doc2\"}");

        InputEndpoint caller = InputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));;
        InputStream result = caller.call(IOTestUtil.asInputStream("{\"endpoint\":0}"), null,
                IOTestUtil.asInputStream(workUnit), input);
        assertNotNull(result);

        ObjectNode resultObj = IOTestUtil.mapper.readValue(result, ObjectNode.class);
        assertNull(resultObj.get("session"));
    }

    @Test
    public void testInputCallerImplWithNullInput() throws IOException {

        String workUnit = "{\"collection\":\"/dataset1\"}";
        InputStream[] input = IOTestUtil.asInputStreamArray("{\"docNum\":1, \"docName\":\"doc1\"}",
                "{\"docNum\":2, \"docName\":\"doc2\"}");

        InputEndpoint caller = InputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));;
        InputStream result = caller.call(IOTestUtil.asInputStream("{\"endpoint\":0}"), caller.newSessionState(),
                IOTestUtil.asInputStream(workUnit), null);
        assertNotNull(result);

        ObjectNode resultObj = IOTestUtil.mapper.readValue(result, ObjectNode.class);
        assertNull(resultObj.get("input"));

    }

    @Test
    public void testInputCallerImplWithNull() throws IOException {

        String workUnit = "{\"collection\":\"/dataset1\"}";
        InputStream[] input = IOTestUtil.asInputStreamArray("{\"docNum\":1, \"docName\":\"doc1\"}",
                "{\"docNum\":2, \"docName\":\"doc2\"}");

        InputEndpoint caller = InputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));;
        InputStream result = caller.call(null, null, null, null);
        assertNotNull(result);

        ObjectNode resultObj = IOTestUtil.mapper.readValue(result, ObjectNode.class);
        assertNotNull(resultObj);
        assertTrue("Result object is not empty", resultObj.size() == 0);

    }

    @AfterClass
    public static void cleanup() {
        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }
}
