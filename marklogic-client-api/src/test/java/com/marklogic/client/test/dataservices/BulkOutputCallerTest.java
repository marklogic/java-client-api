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
import com.marklogic.client.dataservices.OutputEndpoint;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JacksonHandle;
import org.hamcrest.core.StringContains;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;

import static org.junit.Assert.*;

public class BulkOutputCallerTest {
    static ObjectNode apiObj;
    static String apiName = "bulkOutputCallerImpl.api";
    static String scriptPath;
    static String apiPath;
    static JSONDocumentManager docMgr;
    static int count = 5;

    private static String collectionName = "bulkOutputTest";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setup() throws Exception {
        docMgr = IOTestUtil.db.newJSONDocumentManager();
        apiObj = IOTestUtil.readApi(apiName);
        scriptPath = IOTestUtil.getScriptPath(apiObj);
        apiPath = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);
        IOTestUtil.writeDocuments(count, collectionName);
    }
    @Test
    public void bulkOutputCallerWithNullConsumer() {
        OutputEndpoint loadEndpt = OutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        OutputEndpoint.BulkOutputCaller loader = loadEndpt.bulkCaller();

        expectedException.expect(IllegalStateException.class);
        expectedException.expect(new ThrowableMessageMatcher(new StringContains("Output consumer is null")));
        loader.awaitCompletion();
    }

    @Test
    public void bulkOutputCallerTest() throws Exception {
        String endpointState = "{\"next\":"+1+"}";
        String workUnit      = "{\"max\":"+6+"}";

        OutputEndpoint caller = OutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));

        InputStream[] resultArray = caller.call(IOTestUtil.asInputStream(endpointState), caller.newSessionState(),
                IOTestUtil.asInputStream(workUnit));

        assertNotNull(resultArray);
        assertTrue(resultArray.length-1 == count);
        List<String> list = new ArrayList<>();
        for(int i=1;i<resultArray.length;i++) {
            assertNotNull(resultArray[i]);
            list.add(IOTestUtil.mapper.readValue(resultArray[i], ObjectNode.class).toString());
        }
        String workUnit2      = "{\"max\":"+3+"}";
        OutputEndpoint.BulkOutputCaller bulkCaller = OutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj)).bulkCaller();
        bulkCaller.setEndpointState(new ByteArrayInputStream(endpointState.getBytes()));
        bulkCaller.setWorkUnit(new ByteArrayInputStream(workUnit2.getBytes()));
        class Output {
            int counter =0;
        }
        Output output = new Output();
        bulkCaller.setOutputListener(i-> {
            try {
                assertTrue(list.contains(IOTestUtil.mapper.readValue(i, ObjectNode.class).toString()));
                output.counter++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

       bulkCaller.awaitCompletion();
       assertTrue(output.counter == count);
    }

    @AfterClass
    public static void cleanup() {
        QueryManager queryMgr = IOTestUtil.db.newQueryManager();
        DeleteQueryDefinition deletedef = queryMgr.newDeleteDefinition();
        deletedef.setCollections(collectionName);
        queryMgr.delete(deletedef);
    }
}
