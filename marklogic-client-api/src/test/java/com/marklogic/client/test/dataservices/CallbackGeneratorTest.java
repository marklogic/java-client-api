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

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.dataservices.CallBatcher;
import com.marklogic.client.dataservices.CallBatcher.CallArgsGenerator;
import com.marklogic.client.dataservices.CallManager;
import com.marklogic.client.dataservices.CallManager.CallArgs;
import com.marklogic.client.dataservices.CallManager.CallEvent;
import com.marklogic.client.dataservices.CallManager.OneCallEvent;
import com.marklogic.client.dataservices.impl.CallBatcherImpl;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.test.Common;

public class CallbackGeneratorTest {

    private static DatabaseClient db = Common.connect();
    private final static String ENDPOINT_DIRECTORY = "/javaApi/test/callbackGenerator/";

    private static CallManager callMgr = CallManager.on(db);
    private static EndpointUtil endpointUtil = new EndpointUtil(callMgr, ENDPOINT_DIRECTORY);
    private static CallManager.CallableEndpoint callableEndpoint;
    private static CallManager.OneCaller<Double> caller;
    
    private static CallBatcher<Void, OneCallEvent<Double>> batcher;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setup() {
    
        DatabaseClient adminClient = Common.newServerAdminClient("java-unittest-modules");
        JSONDocumentManager docMgr = adminClient.newJSONDocumentManager();

        DocumentMetadataHandle docMeta = new DocumentMetadataHandle();
        docMeta.getPermissions().add("rest-reader", DocumentMetadataHandle.Capability.EXECUTE);
        endpointUtil.setupEndpointSingleRequired(docMgr, docMeta, "singleAtomic", "double");
        adminClient.release();
        
        callableEndpoint = endpointUtil.makeCallableEndpoint("singleAtomic");
        caller = endpointUtil.makeOneCaller(callableEndpoint, Double.class);
       
    }

    @Test
    public void forArgsGeneratorTest() {
        List<Double> outputValues = new ArrayList<Double>();
        class Output {
            List<Double> inputValues = new ArrayList<Double>();
            int i;
            Output() {
                inputValues = Stream
                        .iterate(10.0, last -> last - Double.valueOf(1))
                        .limit(10)
                        .collect(Collectors.toList());
                Collections.sort(inputValues);
                i = 0;
            }
        }
        final Output output = new Output();
        batcher = caller
                .batcher()
                .forArgsGenerator(result -> (result == null || output.i<output.inputValues.size()) ? caller.args().param("param1", output.inputValues.get(output.i)) : null)
                .onCallSuccess(event -> {
                    output.i+= 1;
                    outputValues.add(event.getItem());
                    })
                .onCallFailure((event, throwable) -> throwable.printStackTrace());

        batcher.startJob();
        batcher.awaitCompletion();
        batcher.stopJob();
        Collections.sort(outputValues);
        assertEquals("forArgsGenerator input not equal to output.", outputValues, output.inputValues);
    }
    
    @Test
    public void forArgsGeneratorWithInputTest() {

        batcher = caller
                .batcher()
                .forArgsGenerator(result -> (result == null) ? caller.args().param("param1", 1.1) : result.getArgs());

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(startsWith("Cannot call add() when supplying arguments with generator."));
        batcher.add(null);
    }
    
    @Test
    public void callbatcherInitializeTest() {
        InitializeTest initializeTest = new InitializeTest();
        expectedException.expect(MarkLogicInternalException.class);
        expectedException.expectMessage(startsWith("Unsupported implementation of call arguments."));
        batcher = caller
                .batcher()
                .forArgsGenerator(result -> initializeTest.apply(result));
        ((CallBatcherImpl) batcher).start(null);
    }
    
    private class InitializeTest implements CallArgsGenerator<CallManager.CallEvent> {

        @Override
        public CallArgs apply(CallEvent t) {
            return new CallArgs() {
                
                @Override
                public CallArgs param(String name, XMLStreamReader[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, XMLStreamReader value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, XMLEventReader[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, XMLEventReader value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, String[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, String value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Source[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Source value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Reader[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Reader value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, OffsetTime[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, OffsetTime value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, OffsetDateTime[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, OffsetDateTime value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Long[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Long value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, LocalTime[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, LocalTime value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, LocalDateTime[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, LocalDateTime value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, LocalDate[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, LocalDate value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, JsonParser[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, JsonParser value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, JsonNode[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, JsonNode value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Integer[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Integer value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, InputStream[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, InputStream value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, InputSource[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, InputSource value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Float[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Float value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, File[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, File value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Duration[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Duration value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Double[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Double value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Document[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Document value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Date[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Date value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, byte[][] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, byte[] value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Boolean[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, Boolean value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, BigDecimal[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, BigDecimal value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, AbstractWriteHandle[] values) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public CallArgs param(String name, AbstractWriteHandle value) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public String[] getAssignedParamNames() {
                    // TODO Auto-generated method stub
                    return null;
                }
            };
        }
        
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
