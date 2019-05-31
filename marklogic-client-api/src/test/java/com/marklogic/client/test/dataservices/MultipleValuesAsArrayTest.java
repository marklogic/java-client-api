/*
 * Copyright 2018-2019 MarkLogic Corporation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.dataservices.impl.CallBatcher;
import com.marklogic.client.dataservices.impl.CallManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.test.Common;

public class MultipleValuesAsArrayTest {

    private final static String ENDPOINT_DIRECTORY = "/javaApi/test/multipleValuesAsArrayTest/";

    private static DatabaseClient db = Common.connect();
    private static CallManager callMgr = CallManager.on(db);
    private static EndpointUtil endpointUtil = new EndpointUtil(callMgr, ENDPOINT_DIRECTORY);
    private static CallManager.CallableEndpoint callableEndpoint;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setup() {
        DatabaseClient adminClient = Common.newServerAdminClient("java-unittest-modules");
        JSONDocumentManager docMgr = adminClient.newJSONDocumentManager();

        DocumentMetadataHandle docMeta = new DocumentMetadataHandle();

        docMeta.getPermissions().add("rest-reader", DocumentMetadataHandle.Capability.EXECUTE);
        endpointUtil.setupEndpointMultipleRequired(docMgr, docMeta, "textDocument");

        adminClient.release();
    }

    @AfterClass
    public static void release() {
        DatabaseClient adminClient = Common.newServerAdminClient("java-unittest-modules");

        QueryManager queryMgr = adminClient.newQueryManager();
        DeleteQueryDefinition deletedef = queryMgr.newDeleteDefinition();
        deletedef.setDirectory(ENDPOINT_DIRECTORY);
        queryMgr.delete(deletedef);

        adminClient.release();
    }

    @Test
    public void copyToBytesHandleTest() {
        BufferableHandle handle = null;
        assertNull(NodeConverter.copyToBytesHandle(handle));

        BufferableHandle[] handleArray = null;
        assertNull(NodeConverter.copyToBytesHandle(handleArray));

        handleArray = new BufferableHandle[1];
        handleArray[0] = handle;
        assertNotNull(NodeConverter.copyToBytesHandle(handleArray));
        assertNull(NodeConverter.copyToBytesHandle(handleArray)[0]);
        
        BufferableHandle[] newHandleArray = {new StringHandle("test1"), new StringHandle("test2"), new StringHandle("test3")};
        BufferableHandle[] output = NodeConverter.copyToBytesHandle(newHandleArray);
        assertNotNull(output);
        assertTrue("First value in array not as expected", output[0].toString().equals("test1"));
        assertTrue("Second value in array not as expected", output[1].toString().equals("test2"));
        assertTrue("Third value in array not as expected", output[2].toString().equals("test3"));
    }

    @Test
    public void MultipleValueArrayTest() throws IOException {

        callableEndpoint = endpointUtil.makeCallableEndpoint("textDocument");
        class Output {
            int counter = 0;
            CallManager.CallArgs args;
        }
        String s1 = "{\"key1\":\"value1\"}";
        String s2 = "{\"key2\":\"value2\"}";
        final Output output = new Output();

        ReaderHandle[] handles = { (new ReaderHandle(new StringReader(s1))), (new ReaderHandle(new StringReader(s2))) };

        CallManager.ManyCaller<Reader> manyCaller = callableEndpoint.returningMany(Reader.class);

        CallBatcher<Void, CallBatcher.ManyCallEvent<Reader>> batcher = manyCaller.batcher().forArgsGenerator(result -> {
            if (result == null && output.counter == 0) {
                output.counter++;
                output.args = manyCaller.args().param("param1", handles);
                return output.args;
            } else if (output.counter == 1) {
                output.counter++;
                assertEquals(output.args, result.getArgs());
                return result.getArgs();
            } else {
                assertEquals(output.args, result.getArgs());
                return null;
            }
        }).onCallSuccess(event -> {
            assertEquals(output.args, event.getArgs());
        }).onCallFailure((event, throwable) -> {
            throwable.printStackTrace();
            fail("Batcher failed");
        });
        batcher.startJob();
        batcher.flushAndWait();
        batcher.stopJob();
        
        assertTrue("Counter value not as expected.", output.counter == 2);
    }
    
    @Test
    public void MultipleValueArrayWithReaderInputTest() throws IOException {

        callableEndpoint = endpointUtil.makeCallableEndpoint("textDocument");
        class Output {
            int counter = 0;
            CallManager.CallArgs args;
        }
        String s1 = "{\"key1\":\"value1\"}";
        String s2 = "{\"key2\":\"value2\"}";
        final Output output = new Output();

        Reader[] input = { new StringReader(s1), new StringReader(s2)};

        CallManager.ManyCaller<Reader> manyCaller = callableEndpoint.returningMany(Reader.class);

        CallBatcher<Void, CallBatcher.ManyCallEvent<Reader>> batcher = manyCaller.batcher().forArgsGenerator(result -> {
            if (result == null && output.counter == 0) {
                output.counter++;
                output.args = manyCaller.args().param("param1", input);
                return output.args;
            } else if (output.counter == 1) {
                output.counter++;
                assertEquals(output.args, result.getArgs());
                return result.getArgs();
            } else {
                assertEquals(output.args, result.getArgs());
                return null;
            }
        }).onCallSuccess(event -> {
            assertEquals(output.args, event.getArgs());
        }).onCallFailure((event, throwable) -> {
            throwable.printStackTrace();
            fail("Batcher failed");
        });
        batcher.startJob();
        batcher.flushAndWait();
        batcher.stopJob();

        assertTrue("Counter value not as expected.", output.counter == 2);
    }
    
}
