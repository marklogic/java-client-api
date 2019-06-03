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
import com.marklogic.client.dataservices.impl.CallBatcher;
import com.marklogic.client.dataservices.impl.CallBatcher.CallArgsGenerator;
import com.marklogic.client.dataservices.impl.CallManager;
import com.marklogic.client.dataservices.impl.CallManager.CallArgs;
import com.marklogic.client.dataservices.impl.CallBatcher.CallEvent;
import com.marklogic.client.dataservices.impl.CallBatcher.OneCallEvent;
import com.marklogic.client.dataservices.impl.CallBatcherImpl;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
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
        final List<Double> inputValues = Stream
                .iterate(10.0, last -> last - Double.valueOf(1))
                .limit(10)
                .collect(Collectors.toList());
        Collections.sort(inputValues);
        class Output {
            int i = 0;
        }
        final Output output = new Output();
        batcher = caller
                .batcher()
                .forArgsGenerator(result -> (result == null || output.i<inputValues.size()) ? caller.args().param("param1", inputValues.get(output.i)) : null)
                .onCallSuccess(event -> {
                    output.i+= 1;
                    outputValues.add(event.getItem());
                    })
                .onCallFailure((event, throwable) -> throwable.printStackTrace());

        batcher.startJob();
        batcher.awaitCompletion();
        batcher.stopJob();
        Collections.sort(outputValues);
        assertEquals("forArgsGenerator input not equal to output.", outputValues, inputValues);
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
    
    private class InitializeTest implements CallArgsGenerator<CallBatcher.CallEvent> {

        @Override
        public CallArgs apply(CallEvent t) {
            return new CallArgs() {
                @Override
                public CallArgs param(String name, XMLStreamReader[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, XMLStreamReader value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, XMLEventReader[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, XMLEventReader value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, String[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, String value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Source[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Source value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Reader[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Reader value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, OffsetTime[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, OffsetTime value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, OffsetDateTime[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, OffsetDateTime value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Long[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Long value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, LocalTime[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, LocalTime value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, LocalDateTime[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, LocalDateTime value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, LocalDate[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, LocalDate value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, JsonParser[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, JsonParser value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, JsonNode[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, JsonNode value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Integer[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Integer value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, InputStream[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, InputStream value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, InputSource[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, InputSource value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Float[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Float value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, File[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, File value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Duration[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Duration value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Double[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Double value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Document[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Document value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Date[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Date value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, byte[][] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, byte[] value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Boolean[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, Boolean value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, BigDecimal[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, BigDecimal value) {
                    return null;
                }
                @Override
                public CallArgs param(String name, BufferableHandle[] values) {
                    return null;
                }
                @Override
                public CallArgs param(String name, BufferableHandle value) {
                    return null;
                }
                @Override
                public String[] getAssignedParamNames() {
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
