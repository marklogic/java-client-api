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

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.dataservices.CallBatcher;
import com.marklogic.client.dataservices.CallManager;
import com.marklogic.client.dataservices.CallBatcher.OneCallEvent;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.test.Common;

public class DataservicesPerForestTest {

    private final static String ENDPOINT_DIRECTORY = "/javaApi/test/dataservicesPerForest/";

    private static DatabaseClient db = Common.connect();
    private static CallManager callMgr = CallManager.on(db);
    private static EndpointUtil endpointUtil = new EndpointUtil(callMgr, ENDPOINT_DIRECTORY);
    private static CallBatcher<Void, OneCallEvent<String>> batcher;
    private static CallManager.OneCaller<String> caller;
    private static CallManager.CallableEndpoint callableEndpoint;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setup() {
        DatabaseClient adminClient = Common.newServerAdminClient("java-unittest-modules");
        JSONDocumentManager docMgr = adminClient.newJSONDocumentManager();

        DocumentMetadataHandle docMeta = new DocumentMetadataHandle();

        docMeta.getPermissions().add("rest-reader", DocumentMetadataHandle.Capability.EXECUTE);
        endpointUtil.setupSingleEndpointWithForestParam(docMgr, docMeta, "withForestParam", "string", null, false, false);
        endpointUtil.setupSingleEndpointWithForestParam(docMgr, docMeta, "withForestParamNotString", "double", "float", false, false);
        endpointUtil.setupSingleEndpointWithForestParam(docMgr, docMeta, "withForestParamIsMultiple", "string", "float", true, false);
        adminClient.release();
        
        callableEndpoint = endpointUtil.makeCallableEndpoint("withForestParam");
        caller = endpointUtil.makeOneCaller(callableEndpoint, String.class);
    }
    
    @Test
    public void emptyForestNameTest() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(startsWith("Forest name cannot be null or empty."));
        batcher = caller.batcher().forArgsGenerator((result -> (result == null) ? caller.args().param("forestParamName", 1.1) : null), null);
    }
    
    @Test
    public void forestParamNameTypeNotStringTest() {
        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint("withForestParamNotString");
        CallManager.OneCaller<String> caller = endpointUtil.makeOneCaller(callableEndpoint, String.class);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(startsWith("Forest name parameter cannot be multiple and needs to be a string."));
        caller.batcher().forArgsGenerator((result -> (result == null) ? caller.args().param("forestParamName", "test") : null), "forestParamName");
    }
    
    @Test
    public void forestParamNameWithMultiple() {
        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint("withForestParamIsMultiple");
        CallManager.ManyCaller<String> caller = endpointUtil.makeManyCaller(callableEndpoint, String.class);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(startsWith("Forest name parameter cannot be multiple and needs to be a string."));
        caller.batcher().forArgsGenerator((result -> (result == null) ? caller.args().param("forestParamName", "test") : null), "forestParamName");
    }
    
    @Test
    public void noForestParamNameValueTest() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(startsWith("Forest name parameter of caller cannot be null."));
        batcher = caller.batcher().forArgsGenerator(result -> (result == null) ? caller.args().param("forestParamName", "test") : null, "test");
    }
    
    @Test
    public void endpointReceivesForestName() {

        List<String> outputValues = new ArrayList<String>();
        
        batcher = caller.batcher().forArgsGenerator((result -> (result == null) ? caller.args().param("forestParamName", "test") : null), "forestParamName")
        .onCallSuccess(event-> {
                    outputValues.add(event.getItem());
                    })
                .onCallFailure((event, throwable) -> throwable.printStackTrace()
                ); 
        batcher.startJob();
        batcher.awaitCompletion();
        batcher.stopJob();
        
        assertNotNull(outputValues);
        assertTrue(outputValues.contains("java-unittest-1"));
        assertTrue(outputValues.contains("java-unittest-2"));
        assertTrue(outputValues.contains("java-unittest-3"));
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
}
