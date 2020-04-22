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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.OutputEndpoint;
import com.marklogic.client.io.JacksonHandle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class OutputEndpointImplTest {
    static ObjectNode apiObj;
    static String apiName = "outputCallerImpl.api";
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
    public void testOutputCallerImpl() throws IOException {
        String endpointState = "{\"endpoint\":1}";
        String workUnit = "{\"workUnit\":\"/workUnit1\"}";

        OutputEndpoint caller = OutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        InputStream[] resultArray = caller.call(caller.newCallContext().withEndpointState(IOTestUtil.asInputStream(endpointState))
                .withSessionState(caller.newSessionState())
                .withWorkUnit(IOTestUtil.asInputStream(workUnit)));
        assertNotNull(resultArray);

        ObjectNode resultObj = IOTestUtil.mapper.readValue(resultArray[0], ObjectNode.class);
        assertNotNull(resultObj);
    }

    @Test
    public void testOutputCallerImplWithNullCallcontextEndpointState() throws IOException {
        String workUnit = "{\"collection\":\"/dataset1\"}";

        OutputEndpoint caller = OutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        InputStream[] resultArray = caller.call(caller.newCallContext()
                .withSessionState(caller.newSessionState())
                .withWorkUnit(IOTestUtil.asInputStream(workUnit)));
        assertNotNull(resultArray);

        ObjectNode resultObj = IOTestUtil.mapper.readValue(resultArray[0], ObjectNode.class);
        assertNull(resultObj.get("endpointState"));
    }

    @Test
    public void testOutputCallerImplWithNullCallcontextSession() throws IOException {
        String workUnit = "{\"workUnit\":\"/1\"}";
        OutputEndpoint caller = OutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        InputStream[] resultArray = caller.call(caller.newCallContext().withEndpointState(IOTestUtil.asInputStream("{\"endpoint\":1}"))
                .withWorkUnit(IOTestUtil.asInputStream(workUnit)));
        assertNotNull(resultArray);
        ObjectNode resultObj = IOTestUtil.mapper.readValue(resultArray[0], ObjectNode.class);
        assertNull(resultObj.get("session"));
    }

    @Test
    public void testOutputCallerImplWithEmptyCallcontext() throws IOException {
        OutputEndpoint caller = OutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        InputStream[] resultArray = caller.call( caller.newCallContext());
        assertNotNull(resultArray);
        ObjectNode resultObj = IOTestUtil.mapper.readValue(resultArray[0], ObjectNode.class);
        assertNotNull(resultObj);
        assertTrue("Result object is empty", resultObj.size() != 0);
    }

    @AfterClass
    public static void cleanup() {
        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }
}
