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
import com.marklogic.client.dataservices.IOEndpoint;
import com.marklogic.client.dataservices.OutputCaller;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ErrorListenerOutputEndpointTest {
    static ObjectNode apiObj;
    static String apiName = "errorListenerbulkIOOutputCaller.api";
    static String scriptPath;
    static String apiPath;
    static JSONDocumentManager docMgr;
    static Set<String> expected = new HashSet<>();

    private static final String collectionName_1 = "bulkOutputTest_1";
    private static final String collectionName_2 = "bulkOutputTest_2";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setup() throws Exception {
        docMgr = IOTestUtil.db.newJSONDocumentManager();
        apiObj = IOTestUtil.readApi(apiName);
        scriptPath = IOTestUtil.getScriptPath(apiObj);
        apiPath = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);
        writeDocuments(10,20, collectionName_1);
        writeDocuments(30,50, collectionName_2);
    }

    @Test
    public void bulkOutputCallerTestWithRetry() {

        String endpointState1 = "{\"next\":"+1+"}";
        String endpointConstants1      = "{\"max\":11,\"limit\":2,\"collection\":\"bulkOutputTest_1\"}";

        String endpointState2 = "{\"next\":"+1+"}";
        String endpointConstants2      =  "{\"max\":11,\"limit\":2,\"collection\":\"bulkOutputTest_2\"}";
        OutputCaller<InputStream> endpoint = OutputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle());
        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withEndpointStateAs(endpointState2)
                .withEndpointConstantsAs(endpointConstants2), endpoint.newCallContext()
                .withEndpointStateAs(endpointState1)
                .withEndpointConstantsAs(endpointConstants1)};
        OutputCaller.BulkOutputCaller<InputStream> bulkCaller = endpoint.bulkCaller(callContextArray);
        Set<String> actual = new ConcurrentSkipListSet<>();
        final AtomicBoolean duplicated = new AtomicBoolean(false);
        final AtomicBoolean exceptional = new AtomicBoolean(false);
        OutputCaller.BulkOutputCaller.ErrorListener errorListener =
                (retryCount, throwable, callContext)
                        -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.RETRY;
        bulkCaller.setErrorListener(errorListener);
        bulkCaller.setOutputListener(output -> {
            try {
                String serialized = IOTestUtil.mapper.readValue(output, ObjectNode.class).toString();
                if (actual.contains(serialized)) {
                    duplicated.compareAndSet(false, true);
                } else {
                    actual.add(serialized);
                }
            } catch (Exception e) {
                e.printStackTrace();
                exceptional.compareAndSet(false, true);
            }
        });

        bulkCaller.awaitCompletion();
        assertEquals("exceptions on calls", false, exceptional.get());
        assertEquals("duplicate output", false, duplicated.get());
        assertEquals("unexpected output count", 30, actual.size());
        assertEquals("unexpected output values", expected, actual);
    }

    @Test
    public void bulkOutputCallerTestWithSkip() throws Exception {

        String endpointState1 = "{\"next\":"+1+"}";
        String endpointConstants1      = "{\"max\":11,\"limit\":2,\"collection\":\"bulkOutputTest_1\"}";

        String endpointState2 = "{\"next\":"+1+"}";
        String endpointConstants2      =  "{\"max\":11,\"limit\":2,\"collection\":\"bulkOutputTest_2\"}";
        OutputCaller<InputStream> endpoint = OutputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle());
        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withEndpointStateAs(endpointState2)
                .withEndpointConstantsAs(endpointConstants2), endpoint.newCallContext()
                .withEndpointStateAs(endpointState1)
                .withEndpointConstantsAs(endpointConstants1)};
        OutputCaller.BulkOutputCaller<InputStream> bulkCaller = endpoint.bulkCaller(callContextArray);
        Set<String> actual = new ConcurrentSkipListSet<>();
        final AtomicBoolean duplicated = new AtomicBoolean(false);
        final AtomicBoolean exceptional = new AtomicBoolean(false);
        OutputCaller.BulkOutputCaller.ErrorListener errorListener =
                (retryCount, throwable, callContext)
                        -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.SKIP_CALL;
        bulkCaller.setErrorListener(errorListener);
        bulkCaller.setOutputListener(output -> {
            try {
                String serialized = IOTestUtil.mapper.readValue(output, ObjectNode.class).toString();
                if (actual.contains(serialized)) {
                    duplicated.compareAndSet(false, true);
                } else {
                    actual.add(serialized);
                }
            } catch (Exception e) {
                e.printStackTrace();
                exceptional.compareAndSet(false, true);
            }
        });

        bulkCaller.awaitCompletion();
        assertEquals("exceptions on calls", false, exceptional.get());
        assertEquals("duplicate output", false, duplicated.get());
        assertTrue("unexpected output count", actual.size() >= 20);
    }

    @Test
    public void bulkOutputCallerTestWithStop() {

        String endpointState1 = "{\"next\":"+1+"}";
        String endpointConstants1      = "{\"max\":11,\"limit\":2,\"collection\":\"bulkOutputTest_1\"}";

        String endpointState2 = "{\"next\":"+1+"}";
        String endpointConstants2      =  "{\"max\":11,\"limit\":2,\"collection\":\"bulkOutputTest_2\"}";
        OutputCaller<InputStream> endpoint = OutputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle());
        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withEndpointStateAs(endpointState2)
                .withEndpointConstantsAs(endpointConstants2), endpoint.newCallContext()
                .withEndpointStateAs(endpointState1)
                .withEndpointConstantsAs(endpointConstants1)};
        OutputCaller.BulkOutputCaller<InputStream> bulkCaller = endpoint.bulkCaller(callContextArray);
        Set<String> actual = new ConcurrentSkipListSet<>();
        final AtomicBoolean duplicated = new AtomicBoolean(false);
        final AtomicBoolean exceptional = new AtomicBoolean(false);
        OutputCaller.BulkOutputCaller.ErrorListener errorListener =
                (retryCount, throwable, callContext)
                        -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.STOP_ALL_CALLS;
        bulkCaller.setErrorListener(errorListener);
        bulkCaller.setOutputListener(output -> {
            try {
                String serialized = IOTestUtil.mapper.readValue(output, ObjectNode.class).toString();
                if (actual.contains(serialized)) {
                    duplicated.compareAndSet(false, true);
                } else {
                    actual.add(serialized);
                }
            } catch (Exception e) {
                e.printStackTrace();
                exceptional.compareAndSet(false, true);
            }
        });

        bulkCaller.awaitCompletion();
        assertEquals("exceptions on calls", false, exceptional.get());
        assertEquals("duplicate output", false, duplicated.get());
        assertTrue("unexpected output count", actual.size() < 30);
    }

    @AfterClass
    public static void cleanup() {

        IOTestUtil.modMgr.delete(scriptPath, apiPath);
        QueryManager queryMgr = IOTestUtil.db.newQueryManager();
        DeleteQueryDefinition deletedef = queryMgr.newDeleteDefinition();
        deletedef.setCollections(collectionName_1);
        queryMgr.delete(deletedef);
        deletedef.setCollections(collectionName_2);
        queryMgr.delete(deletedef);

    }

    private static void writeDocuments(int startCount,int endCount, String collection) {
        JSONDocumentManager manager = IOTestUtil.db.newJSONDocumentManager();
        DocumentMetadataHandle metadata = new DocumentMetadataHandle().withCollections(collection);

        for(int i=startCount;i<endCount;i++) {
            StringHandle data = new StringHandle("{\"docNum\":"+i+",\"docName\":\"doc"+i+"\"}");
            manager.write("/test/"+collection+"/"+i+".json", metadata, data);
            expected.add(data.toString());
        }
    }
}
