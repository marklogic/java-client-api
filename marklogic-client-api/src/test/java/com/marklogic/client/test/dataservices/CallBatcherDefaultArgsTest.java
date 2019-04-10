package com.marklogic.client.test.dataservices;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.hamcrest.core.StringStartsWith.startsWith;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.dataservices.CallBatcher;
import com.marklogic.client.dataservices.CallManager;
import com.marklogic.client.dataservices.CallManager.CallArgs;
import com.marklogic.client.dataservices.CallManager.ManyCallEvent;
import com.marklogic.client.dataservices.CallManager.OneCallEvent;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.test.Common;

public class CallBatcherDefaultArgsTest {

    private final static String ENDPOINT_DIRECTORY = "/javaApi/test/callBatchedParam/";

    private static DatabaseClient db = Common.connect();
    private static CallManager callMgr = CallManager.on(db);
    private static EndpointUtil endpointUtil = new EndpointUtil(callMgr, ENDPOINT_DIRECTORY);
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setup() {
        DatabaseClient adminClient = Common.newServerAdminClient("java-unittest-modules");
        JSONDocumentManager docMgr = adminClient.newJSONDocumentManager();

        DocumentMetadataHandle docMeta = new DocumentMetadataHandle();

        docMeta.getPermissions().add("rest-reader", DocumentMetadataHandle.Capability.EXECUTE);
        endpointUtil.setupEndpointMultipleRequired(docMgr, docMeta, "double");
        endpointUtil.setupEndpointMultipleRequired(docMgr, docMeta, "jsonDocument");

        endpointUtil.setupEndpointSingleRequired(docMgr, docMeta, "singleAtomic", "double");
        endpointUtil.setupEndpointSingleRequired(docMgr, docMeta, "singleNode", "object");
        endpointUtil.setupTwoParamEndpoint(docMgr, docMeta, "twoAtomic", "double", "date", true);
        endpointUtil.setupEndpointMultipleRequired(docMgr, docMeta, "float");
        endpointUtil.setupEndpointSingleNulled(docMgr, docMeta, "nullAtomic", "double");
        
        endpointUtil.setupTwoDifferentParamEndpoint(docMgr, docMeta, "twoDiffAtomic", "double", "float", true, false);
        endpointUtil.setupTwoDifferentParamEndpoint(docMgr, docMeta, "twoDiffAtomic2", "double", "float", true, true);
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
    public void singleAtomicCallFieldTest() {
        
        String functionName = "singleAtomic";

        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint(functionName);

        CallManager.OneCaller<Double> caller = endpointUtil.makeOneCaller(callableEndpoint, Double.class);

        class Output {
            Double expectedOutput = 0.0;
        }
        final Output output = new Output();
        CallBatcher<CallArgs, OneCallEvent<Double>> batcher = caller
                .batcher()
                .forArgs()
                .withDefaultArgs(caller.args().param("param1", 1.2))
                .onCallSuccess(event -> {
                    output.expectedOutput = event.getItem();
                    })
                .onCallFailure((event, throwable) -> {
                    throwable.printStackTrace();
                    fail("atomic batcher failed");
                    });

        batcher.add(caller.args());
        batcher.flushAndWait();

        assertEquals("for single atomic callField, output not equal to input", Double.valueOf(1.2), output.expectedOutput);
        
        batcher.add(caller.args().param("param1", 1.1));
        batcher.flushAndWait();
        
        assertEquals("for single atomic callField, output not equal to input", Double.valueOf(1.1), output.expectedOutput);
        batcher.getDataMovementManager().stopJob(batcher);
    }
    
    @Test
    public void singleNodeCallFieldTest() {
        String functionName = "singleNode";

        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint(functionName);

        CallManager.OneCaller<JsonNode> caller = endpointUtil.makeOneCaller(callableEndpoint, JsonNode.class);

        class Output {
            String expectedOutput;
        }

        final Output output = new Output();
        CallBatcher<CallArgs, OneCallEvent<JsonNode>> batcher = caller
                .batcher()
                .forArgs()
                .withDefaultArgs(caller.args().param("param1", new ObjectMapper().createObjectNode().put("param1", "value1")))
                .onCallSuccess(event -> {
                    output.expectedOutput = event.getItem().get("param1").textValue();
                    })
                .onCallFailure((event, throwable) -> {
                    throwable.printStackTrace();
                    fail("node batcher failed");
                    });

        batcher.add(caller.args());
        batcher.flushAndWait();
        batcher.getDataMovementManager().stopJob(batcher);
        assertEquals("for single node callField, output not equal to input", "value1", output.expectedOutput);
    }
    
    @Test
    public void multipleAtomicCallFieldTest() {
        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint("float");
        CallManager.ManyCaller<Float> manyCaller = callableEndpoint.returningMany(Float.class);
        Float[] values = {1.0f, 2.0f};
        
        class Output {
            Float[] expectedOutput;
        }

        final Output output = new Output();
        CallBatcher<CallManager.CallArgs,CallManager.ManyCallEvent<Float>> batcher = manyCaller.batcher().forArgs()
                .withDefaultArgs(manyCaller.args().param("param1", values))
                .onCallSuccess(event -> {
                    output.expectedOutput = event.getItems().toArray(Float[]::new);
                    })
                .onCallFailure((event, throwable) -> {
                    throwable.printStackTrace();
                    fail("atomic batcher failed");
                    });;
 
        batcher.add(manyCaller.args());
        batcher.flushAndWait();
        batcher.getDataMovementManager().stopJob(batcher);
        
        assertEquals("for multiple atomic callField, output not equal to input", values, output.expectedOutput);
    }
    
    @Test
    public void multipleNodeCallFieldTest() {
        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint("jsonDocument");
        CallManager.ManyCaller<JsonNode> manyCaller = callableEndpoint.returningMany(JsonNode.class);
        JsonNode[] values = {new ObjectMapper().createObjectNode().put("param1", "value1"), new ObjectMapper().createObjectNode().put("param2", "value2")};
        
        class Output {
            String[] expectedOutput = new String[2];
        }

        final Output output = new Output();
        CallBatcher<CallManager.CallArgs,CallManager.ManyCallEvent<JsonNode>> batcher = manyCaller.batcher().forArgs()
                .withDefaultArgs(manyCaller.args().param("param1", values))
                .onCallSuccess(event -> {
                    JsonNode[] outputs = event.getItems().toArray(JsonNode[]::new);
                    output.expectedOutput[0] = outputs[0].get("param1").textValue();
                    output.expectedOutput[1] = outputs[1].get("param2").textValue();
                    })
                .onCallFailure((event, throwable) -> {
                    throwable.printStackTrace();
                    fail("node batcher failed");
                    });;
 
        batcher.add(manyCaller.args());
        batcher.flushAndWait();
        batcher.getDataMovementManager().stopJob(batcher);
        
        assertEquals("for multiple atomic callField, output not equal to input", "value1", output.expectedOutput[0]);
        assertEquals("for multiple atomic callField, output not equal to input", "value2", output.expectedOutput[1]);
    }
    
    @Test
    public void multipleAtomicWithMultipleDefaultsTest() {
        String functionName = "twoAtomic";

        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint(functionName);

        CallManager.ManyCaller<Double> caller = endpointUtil.makeManyCaller(callableEndpoint, Double.class);

        class Output {
            Double[] expectedOutput;
        }
        final Output output = new Output();

        final String[] expectedParamNames = new String[]{"param1", "param2"};
        Double[] inputValues = {1.2, 2.4};

        CallBatcher<CallArgs, ManyCallEvent<Double>> batcher = caller
                .batcher()
                .forArgs()
                .withDefaultArgs(caller.args().param("param1", inputValues).param("param2", LocalDate.parse("2019-01-02")))
                .onCallSuccess(event -> {
                    String[] paramNames = event.getArgs().getAssignedParamNames();
                    assertEquals("param count not 2", 2, paramNames.length);
                    Arrays.sort(paramNames);
                    assertArrayEquals("param names not equal", expectedParamNames, paramNames);
                    output.expectedOutput = event.getItems().toArray(Double[]::new);
                })
                .onCallFailure((event, throwable) -> {
                    throwable.printStackTrace();
                    fail("atomic batcher failed");
                });

        batcher.add(caller.args());
        batcher.flushAndWait();
        batcher.getDataMovementManager().stopJob(batcher);

        assertEquals("for multiple atomic callField, output not equal to input", Double.valueOf(1.2), output.expectedOutput[0]);
        assertEquals("for multiple atomic callField, output not equal to input", Double.valueOf(2.4), output.expectedOutput[1]);
        
    }
    
    @Test
    public void multipleAtomicWithMissingParamsTest() {
        String functionName = "twoAtomic";

        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint(functionName);

        CallManager.ManyCaller<Double> caller = endpointUtil.makeManyCaller(callableEndpoint, Double.class);

        Double[] inputValues = {1.2, 2.4};
        CallBatcher<CallArgs, ManyCallEvent<Double>> batcher = caller
                .batcher()
                .forArgs()
                .withDefaultArgs(caller.args().param("param1", inputValues));

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(startsWith("twoAtomic.sjs called without some required parameters"));
        batcher.add(caller.args());
    }
    
    @Test
    public void singleAtomicWithDifferentParamType() throws Exception {
        
        String functionName = "singleAtomic";

        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint(functionName);

        CallManager.OneCaller<Double> caller = endpointUtil.makeOneCaller(callableEndpoint, Double.class);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(startsWith("Float value not accepted for param1 parameter"));
        
        caller.batcher().forArgs().withDefaultArgs(caller.args().param("param1", 1.2f));
    }
    
    @Test
    public void singleNodeWithDifferentEndpoint() {
        String functionName = "singleAtomic";

        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint(functionName);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(startsWith
                ("cannot convert server type double to primaryClient type com.fasterxml.jackson.databind.JsonNode"));
        endpointUtil.makeOneCaller(callableEndpoint, JsonNode.class);
    }
    
    @Test(expected = Test.None.class)
    public void singleAtomicNoRequiredParam() {
        String functionName = "nullAtomic";

        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint(functionName);
        
        CallManager.OneCaller<Double> caller = endpointUtil.makeOneCaller(callableEndpoint, Double.class);
        caller.batcher().forArgs().withDefaultArgs(caller.args().param("param1", 1.2));
    }
    
    @Test(expected = Test.None.class)
    public void singleAtomicDifferentParamsTest() {
        String functionName = "twoDiffAtomic";
        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint(functionName);

        CallManager.ManyCaller<Double> caller = endpointUtil.makeManyCaller(callableEndpoint, Double.class);

        Double[] inputValues = {1.2, 2.4};
        Float floatValue = null;

        caller.batcher().forArgs().withDefaultArgs(caller.args().param("param1", inputValues).param("param2", floatValue));

    }
    
    @Test
    public void singleAtomicDifferentParamsTest2() {
        String functionName = "twoDiffAtomic2";
        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint(functionName);

        CallManager.ManyCaller<Double> caller = endpointUtil.makeManyCaller(callableEndpoint, Double.class);

        Double[] inputValues = {1.2, 2.4};
        Float floatValue = null;

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(startsWith("null value for required parameter: param2"));
        caller.batcher().forArgs().withDefaultArgs(caller.args().param("param1", inputValues).param("param2", floatValue));
    }
 
}
