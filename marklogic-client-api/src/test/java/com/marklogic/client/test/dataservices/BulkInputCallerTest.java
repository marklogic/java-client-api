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
import com.marklogic.client.dataservices.InputEndpoint;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JacksonHandle;
import org.hamcrest.core.StringContains;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;

import java.io.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class BulkInputCallerTest {
    static ObjectNode apiObj;
    static String apiName = "bulkInputCallerImpl.api";
    static String scriptPath;
    static String apiPath;
    static int workMax = 3;
    static int startValue = 1;
    static JSONDocumentManager docMgr;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setup() throws Exception {
        docMgr = IOTestUtil.db.newJSONDocumentManager();
        apiObj = IOTestUtil.readApi(apiName);
        scriptPath = IOTestUtil.getScriptPath(apiObj);
        apiPath = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);
    }

    @Test
    public void bulkInputEndpointTest() {
        String apiName = "bulkInputCallerImpl.api";

        String endpointState = "{\"next\":"+startValue+"}";
        String workUnit      = "{\"max\":"+workMax+"}";

        InputEndpoint loadEndpt = InputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));

        InputEndpoint.BulkInputCaller loader = loadEndpt.bulkCaller();
        loader.setEndpointState(new ByteArrayInputStream(endpointState.getBytes()));
        loader.setWorkUnit(new ByteArrayInputStream(workUnit.getBytes()));

        Stream<InputStream> input         = Stream.of(
                IOTestUtil.asInputStream("{\"docNum\":1, \"docName\":\"doc1\"}"),
                IOTestUtil.asInputStream("{\"docNum\":2, \"docName\":\"doc2\"}"),
                IOTestUtil.asInputStream("{\"docNum\":3, \"docName\":\"doc3\"}")
        );
        input.forEach(loader::accept);
        loader.awaitCompletion();

        for (int startNext=startValue; startNext < workMax; startNext++) {
            int endNext=startNext+1;
            String uri = "/marklogic/ds/test/bulkInputCaller/"+endNext+".json";
            JsonNode doc = docMgr.read(uri, new JacksonHandle()).get();
            assertNotNull("Could not find file "+uri, doc);
            assertEquals("state mismatch", endNext, doc.get("state").get("next").asInt());
            assertEquals("state mismatch", workMax, doc.get("work").get("max").asInt());
            JsonNode inputs = doc.get("inputs");
            int docCount = (endNext == workMax) ? 1 : 2;
            assertEquals("inputs mismatch", docCount, inputs.size());
            for (int j=0; j < docCount; j++) {
                int offset = j + (startNext * 2) - 1;
                JsonNode inputDoc = inputs.get(j);
                assertEquals("docNum mismatch", offset, inputDoc.get("docNum").asInt());
                assertEquals("docName mismatch", "doc"+offset, inputDoc.get("docName").asText());
            }
        }
    }

    @Test
    public void bulkInputEndpointInterruptTest() throws Exception {
        String apiName = "bulkInputCallerImpl.api";

        String endpointState = "{\"next\":"+startValue+"}";
        String workUnit      = "{\"max\":"+workMax+"}";
        ObjectNode apiObj     = IOTestUtil.readApi(apiName);
        String     scriptPath = IOTestUtil.getScriptPath(apiObj);
        String     apiPath    = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);
        InputEndpoint loadEndpt = InputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));

        InputEndpoint.BulkInputCaller loader = loadEndpt.bulkCaller();
        loader.setEndpointState(new ByteArrayInputStream(endpointState.getBytes()));
        loader.setWorkUnit(new ByteArrayInputStream(workUnit.getBytes()));

        Stream<InputStream> input         = Stream.of(
                IOTestUtil.asInputStream("{\"docNum\":1, \"docName\":\"doc1\"}"),
                IOTestUtil.asInputStream("{\"docNum\":2, \"docName\":\"doc2\"}"),
                IOTestUtil.asInputStream("{\"docNum\":3, \"docName\":\"doc3\"}")
        );

        Stream<InputStream> input2         = Stream.of(
                IOTestUtil.asInputStream("{\"docNum\":4, \"docName\":\"doc4\"}"),
                IOTestUtil.asInputStream("{\"docNum\":5, \"docName\":\"doc5\"}"),
                IOTestUtil.asInputStream("{\"docNum\":6, \"docName\":\"doc6\"}"));

        input.forEach(loader::accept);
        loader.interrupt();

        expectedException.expect(IllegalStateException.class);
        expectedException.expect(new ThrowableMessageMatcher(new StringContains("cannot accept more input as current phase is  INTERRUPTING")));
        input2.forEach(loader::accept);
    }

    @AfterClass
    public static void cleanup() {
        IOTestUtil.modMgr.delete(scriptPath, apiPath);
        for(int i=startValue+1; i<workMax; i++) {
            String uri = "/marklogic/ds/test/bulkInputCaller/" +i+".json";
            docMgr.delete(uri);
        }
    }
}
