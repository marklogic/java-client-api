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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.dataservices.CallBatcher;
import com.marklogic.client.dataservices.CallManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.test.Common;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class CallBatchedParamTest {
    private final static String ENDPOINT_DIRECTORY = "/javaApi/test/callBatchedParam/";

    private static DatabaseClient db = Common.connect();
    private static CallManager callMgr = CallManager.on(db);
    private static EndpointUtil endpointUtil = new EndpointUtil(callMgr, ENDPOINT_DIRECTORY);

    private ObjectMapper mapper = new ObjectMapper();


    @BeforeClass
    public static void setup() {
        DatabaseClient adminClient = Common.newServerAdminClient("java-unittest-modules");
        JSONDocumentManager docMgr = adminClient.newJSONDocumentManager();

        DocumentMetadataHandle docMeta = new DocumentMetadataHandle();

        docMeta.getPermissions().add("rest-reader", DocumentMetadataHandle.Capability.EXECUTE);
        endpointUtil.setupEndpointMultipleRequired(docMgr, docMeta, "double");
        endpointUtil.setupEndpointMultipleRequired(docMgr, docMeta, "jsonDocument");

        endpointUtil.setupEndpointSingleRequired(docMgr, docMeta, "singleAtomic", "dateTime");
        endpointUtil.setupEndpointSingleRequired(docMgr, docMeta, "singleNode", "object");

        endpointUtil.setupTwoParamEndpoint(
                docMgr, docMeta, "twoAtomic", "decimal", "date", true
        );
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
    public void multipleAtomicSingleParamTest() {
        String functionName = "double";

        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint(functionName);

        CallManager.ManyCaller<Double> caller = endpointUtil.makeManyCaller(callableEndpoint, Double.class);

        int max = 300;

        Set<Double> input = Stream
                .iterate(1.1, last -> last + 1)
                .limit(max)
                .collect(Collectors.toSet());

        SortedSet<Double> output = new TreeSet<>();

        final IntCounter batchCount = new IntCounter();

        CallBatcher<Double, CallManager.ManyCallEvent<Double>> batcher = caller
                .batcher()
                .forBatchedParam("param1", Double.class)
                .onCallSuccess(event -> {
                    event.getItems().forEach(item -> output.add(item));
                    batchCount.value++;
                    })
                .onCallFailure((event, throwable) -> {
                    throwable.printStackTrace();
                    fail("atomic batcher failed");
                    });

        batcher.addAll(input.stream());
        batcher.flushAndWait();
        batcher.stopJob();

        // System.out.println(output.stream().map(i -> i.toString()).collect(Collectors.joining(", ")));

        assertEquals("for single batched parameter with multiple atomic values, output not equal to input", input, output);
        assertEquals("incorrect batch count for single batched parameter with multiple atomic values", 3, batchCount.value);
    }
    @Test
    public void singleAtomicSingleParamTest() {
        String functionName = "singleAtomic";

        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint(functionName);

        CallManager.OneCaller<LocalDateTime> caller = endpointUtil.makeOneCaller(callableEndpoint, LocalDateTime.class);

        int max = 9;

        Set<LocalDateTime> input = Stream
                .iterate(1, last -> last + 1)
                .limit(max)
                .map(num -> LocalDateTime.parse("2018-0"+num+"-02T10:09:08"))
                .collect(Collectors.toSet());

        SortedSet<LocalDateTime> output = new TreeSet<>();

        final IntCounter batchCount = new IntCounter();

        CallBatcher<LocalDateTime, CallManager.OneCallEvent<LocalDateTime>> batcher = caller
                .batcher()
                .forBatchedParam("param1", LocalDateTime.class)
                .onCallSuccess(event -> {
                    output.add(event.getItem());
                    batchCount.value++;
                    })
                .onCallFailure((event, throwable) -> {
                    throwable.printStackTrace();
                    fail("node batcher failed");
                    });

        batcher.addAll(input.stream());
        batcher.flushAndWait();
        batcher.stopJob();

        // System.out.println(output.stream().map(i -> i.toString()).collect(Collectors.joining(", ")));

        assertEquals("for single batched parameter with single atomic values, output not equal to input", input, output);
        assertEquals("incorrect batch count for single batched parameter with single atomic values", max, batchCount.value);
    }
    @Test
    public void multipleNodeSingleParamTest() {
        String functionName = "jsonDocument";

        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint(functionName);

        CallManager.ManyCaller<JsonNode> caller = endpointUtil.makeManyCaller(callableEndpoint, JsonNode.class);

        int max = 300;

        Set<Integer> input = Stream
                .iterate(1, last -> last + 1)
                .limit(max)
                .collect(Collectors.toSet());

        SortedSet<Integer> output = new TreeSet<>();

        final IntCounter batchCount = new IntCounter();

        CallBatcher<JsonNode, CallManager.ManyCallEvent<JsonNode>> batcher = caller
                .batcher()
                .forBatchedParam("param1", JsonNode.class)
                .onCallSuccess(event -> {
                    event.getItems().forEach(item ->  output.add(item.get("i").asInt()));
                    batchCount.value++;
                    })
                .onCallFailure((event, throwable) -> {
                    throwable.printStackTrace();
                    fail("node batcher failed");
                    });

        batcher.addAll(input.stream().map(i -> mapper.createObjectNode().put("i", i)));
        batcher.flushAndWait();
        batcher.stopJob();

        // System.out.println(output.stream().map(i -> i.toString()).collect(Collectors.joining(", ")));

        assertEquals("for single batched parameter with multiple node values, output not equal to input", input, output);
        assertEquals("incorrect batch count for single batched parameter with multiple node values", 3, batchCount.value);
    }
    @Test
    public void singleNodeSingleParamTest() {
        String functionName = "singleNode";

        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint(functionName);

        CallManager.OneCaller<JsonNode> caller = endpointUtil.makeOneCaller(callableEndpoint, JsonNode.class);

        int max = 9;

        Set<Integer> input = Stream
                .iterate(1, last -> last + 1)
                .limit(max)
                .collect(Collectors.toSet());

        SortedSet<Integer> output = new TreeSet<>();

        final IntCounter batchCount = new IntCounter();

        CallBatcher<JsonNode, CallManager.OneCallEvent<JsonNode>> batcher = caller
                .batcher()
                .forBatchedParam("param1", JsonNode.class)
                .onCallSuccess(event -> {
                    output.add(event.getItem().get("i").asInt());
                    batchCount.value++;
                    })
                .onCallFailure((event, throwable) -> {
                    throwable.printStackTrace();
                    fail("node batcher failed");
                    });

        batcher.addAll(input.stream().map(i -> mapper.createObjectNode().put("i", i)));
        batcher.flushAndWait();
        batcher.stopJob();

        // System.out.println(output.stream().map(i -> i.toString()).collect(Collectors.joining(", ")));

        assertEquals("for single batched parameter with single node values, output not equal to input", input, output);
        assertEquals("incorrect batch count for single batched parameter with single node values", max, batchCount.value);
    }
    @Test
    public void multipleAtomicDefaultedParamTest() {
        String functionName = "twoAtomic";

        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint(functionName);

        CallManager.ManyCaller<BigDecimal> caller = endpointUtil.makeManyCaller(callableEndpoint, BigDecimal.class);

        int max = 300;

        Set<BigDecimal> input = Stream
                .iterate(1, last -> last + 1)
                .limit(max)
                .map(i -> new BigDecimal(i+".1"))
                .collect(Collectors.toSet());

        SortedSet<BigDecimal> output = new TreeSet<>();

        final String[] expectedParamNames = new String[]{"param1", "param2"};

        final IntCounter batchCount = new IntCounter();

        CallBatcher<BigDecimal, CallManager.ManyCallEvent<BigDecimal>> batcher = caller
                .batcher()
                .forBatchedParam("param1", BigDecimal.class)
                .withdefaultArgs(caller.args().param("param2", LocalDate.parse("2019-01-02")))
                .onCallSuccess(event -> {
                    String[] paramNames = event.getArgs().getAssignedParamNames();
                    assertEquals("param count not 2", 2, paramNames.length);
                    Arrays.sort(paramNames);
                    assertArrayEquals("param names not equal", expectedParamNames, paramNames);
                    event.getItems().forEach(item -> output.add(item));
                    batchCount.value++;
                })
                .onCallFailure((event, throwable) -> {
                    throwable.printStackTrace();
                    fail("atomic batcher failed");
                });

        batcher.addAll(input.stream());
        batcher.flushAndWait();
        batcher.stopJob();

        // System.out.println(output.stream().map(i -> i.toString()).collect(Collectors.joining(", ")));

        assertEquals("for defaulted batched parameter with multiple atomic values, output not equal to input", input, output);
        assertEquals("incorrect batch count for defaulted batched parameter with multiple atomic values", 3, batchCount.value);
    }
    @Test
    public void queuingParamTest() {
        String functionName = "double";

        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint(functionName);

        CallManager.ManyCaller<Double> caller = endpointUtil.makeManyCaller(callableEndpoint, Double.class);

        int max = 300;

        SortedSet<Double> input = Stream
                .iterate(1.1, last -> last + 1)
                .limit(max)
                .collect(Collectors.toCollection(TreeSet::new));

        SortedSet<Double> output = new TreeSet<>();

        final IntCounter batchCount = new IntCounter();

        CallBatcher<Double, CallManager.ManyCallEvent<Double>> batcher = caller
                .batcher()
                .forBatchedParam("param1", Double.class)
                .onCallSuccess(event -> {
                    event.getItems().forEach(item -> output.add(item));
                    batchCount.value++;
                })
                .onCallFailure((event, throwable) -> {
                    throwable.printStackTrace();
                    fail("atomic batcher failed");
                });

        batcher.addAll(input.subSet(1.1, 111.1).stream());
        batcher.flushAndWait();
        assertEquals("incorrect output size for first flush", 110, output.size());
        assertEquals("incorrect batch count for first flush", 2, batchCount.value);

        batcher.addAll(input.subSet(111.1, 221.1).stream());
        batcher.awaitCompletion();
        assertEquals("incorrect output size for second await", 210, output.size());
        assertEquals("incorrect batch count for second await", 3, batchCount.value);

        batcher.addAll(input.tailSet(221.1).stream());
        batcher.flushAndWait();
        batcher.stopJob();

        // System.out.println(output.stream().map(i -> i.toString()).collect(Collectors.joining(", ")));

        assertEquals("final output not equal to input", input, output);
        assertEquals("incorrect final batch count", 4, batchCount.value);
    }
    // TODO: other data types and client representations
    // TODO: negative cases such as missing required parameters and invalid parameters

    static class IntCounter {
        int value = 0;
    }
}