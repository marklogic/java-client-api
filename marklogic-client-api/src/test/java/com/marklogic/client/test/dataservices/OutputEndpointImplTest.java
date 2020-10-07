/*
 * Copyright (c) 2020 MarkLogic Corporation
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
import com.marklogic.client.dataservices.OutputCaller;
import com.marklogic.client.io.InputStreamHandle;
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
        String endpointConstants = "{\"endpointConstants\":\"/endpointConstants1\"}";

        OutputCaller<InputStream> caller = OutputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle());
        InputStream[] resultArray = caller.call(caller.newCallContext().withEndpointStateAs(endpointState)
                .withSessionState(caller.newSessionState())
                .withEndpointConstantsAs(endpointConstants));
        assertNotNull(resultArray);

        ObjectNode resultObj = IOTestUtil.mapper.readValue(resultArray[0], ObjectNode.class);
        assertNotNull(resultObj);
    }

    @Test
    public void testOutputCallerImplWithNullCallcontextEndpointState() throws IOException {
        String endpointConstants = "{\"collection\":\"/dataset1\"}";

        OutputCaller<InputStream> caller = OutputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle());
        InputStream[] resultArray = caller.call(caller.newCallContext()
                .withSessionState(caller.newSessionState())
                .withEndpointConstantsAs(endpointConstants));
        assertNotNull(resultArray);

        ObjectNode resultObj = IOTestUtil.mapper.readValue(resultArray[0], ObjectNode.class);
        assertNull(resultObj.get("endpointState"));
    }

    @Test
    public void testOutputCallerImplWithNullCallcontextSession() throws IOException {
        String endpointConstants = "{\"endpointConstants\":\"/1\"}";
        OutputCaller<InputStream> caller = OutputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle());
        InputStream[] resultArray = caller.call(caller.newCallContext().withEndpointStateAs("{\"endpoint\":1}")
                .withEndpointConstantsAs(endpointConstants));
        assertNotNull(resultArray);
        ObjectNode resultObj = IOTestUtil.mapper.readValue(resultArray[0], ObjectNode.class);
        assertNull(resultObj.get("session"));
    }

    @Test
    public void testOutputCallerImplWithEmptyCallcontext() throws IOException {
        OutputCaller<InputStream> caller = OutputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle());
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
