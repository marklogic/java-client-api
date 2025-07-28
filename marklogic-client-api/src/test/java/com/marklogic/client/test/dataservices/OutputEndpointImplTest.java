/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.dataservices;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.OutputCaller;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class OutputEndpointImplTest {
    static ObjectNode apiObj;
    static String apiName = "outputCallerImpl.api";
    static String scriptPath;
    static String apiPath;

    @BeforeAll
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
        assertTrue(resultObj.size() != 0);
    }

    @AfterAll
    public static void cleanup() {
        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }
}
