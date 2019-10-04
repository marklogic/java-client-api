package com.marklogic.client.test.dataservices;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.impl.InputCallerImpl;
import com.marklogic.client.io.JacksonHandle;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
        Stream<InputStream> input = Stream.of(asInputStream("{docNum:1, docName:\"doc1\"}"),
                asInputStream("{docNum:2, docName:\"doc2\"}"));

        InputCallerImpl caller = new InputCallerImpl(new JacksonHandle(apiObj));
        InputStream result = caller.call(IOTestUtil.db, asInputStream(endpointState), caller.newSessionState(),
                asInputStream(workUnit), input);
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
        Stream<InputStream> input = Stream.of(asInputStream("{docNum:1, docName:\"doc1\"}"),
                asInputStream("{docNum:2, docName:\"doc2\"}"));

        InputCallerImpl caller = new InputCallerImpl(new JacksonHandle(apiObj));
        InputStream result = caller.call(IOTestUtil.db, null, caller.newSessionState(),
                asInputStream(workUnit), input);
        assertNotNull(result);

        ObjectNode resultObj = IOTestUtil.mapper.readValue(result, ObjectNode.class);
        assertNull(resultObj.get("endpointState"));
    }

    @Test
    public void testInputCallerImplWithNullSession() throws IOException {

        String workUnit = "{\"collection\":\"/dataset1\"}";
        Stream<InputStream> input = Stream.of(asInputStream("{docNum:1, docName:\"doc1\"}"),
                asInputStream("{docNum:2, docName:\"doc2\"}"));

        InputCallerImpl caller = new InputCallerImpl(new JacksonHandle(apiObj));
        InputStream result = caller.call(IOTestUtil.db, asInputStream("{\"endpoint\":0}"), null,
                asInputStream(workUnit), input);
        assertNotNull(result);

        ObjectNode resultObj = IOTestUtil.mapper.readValue(result, ObjectNode.class);
        assertNull(resultObj.get("session"));

    }

    @Test
    public void testInputCallerImplWithNullInput() throws IOException {

        String workUnit = "{\"collection\":\"/dataset1\"}";
        Stream<InputStream> input = Stream.of(asInputStream("{docNum:1, docName:\"doc1\"}"),
                asInputStream("{docNum:2, docName:\"doc2\"}"));

        InputCallerImpl caller = new InputCallerImpl(new JacksonHandle(apiObj));
        InputStream result = caller.call(IOTestUtil.db, asInputStream("{\"endpoint\":0}"), caller.newSessionState(),
                asInputStream(workUnit), null);
        assertNotNull(result);

        ObjectNode resultObj = IOTestUtil.mapper.readValue(result, ObjectNode.class);
        assertNull(resultObj.get("input"));

    }

    @Test
    public void testInputCallerImplWithNull() throws IOException {

        String workUnit = "{\"collection\":\"/dataset1\"}";
        Stream<InputStream> input = Stream.of(asInputStream("{docNum:1, docName:\"doc1\"}"),
                asInputStream("{docNum:2, docName:\"doc2\"}"));

        InputCallerImpl caller = new InputCallerImpl(new JacksonHandle(apiObj));
        InputStream result = caller.call(IOTestUtil.db, null, null,
                null, null);
        assertNotNull(result);

        ObjectNode resultObj = IOTestUtil.mapper.readValue(result, ObjectNode.class);
        assertNotNull(resultObj);
        assertTrue("Result object is not empty", resultObj.size() == 0);

    }

    @AfterClass
    public static void cleanup() {
        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }

    private InputStream asInputStream(String value) {
        return new ByteArrayInputStream(value.getBytes());
    }
}
