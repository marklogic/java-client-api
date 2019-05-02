package com.marklogic.client.test.dataservices;

import static org.hamcrest.core.StringStartsWith.startsWith;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.dataservices.CallBatcher;
import com.marklogic.client.dataservices.CallManager;
import com.marklogic.client.dataservices.CallManager.OneCallEvent;
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
        endpointUtil.setupSingleEndpointWithForestParam(docMgr, docMeta, "withForestParam", "string", "float", false, true, "string");
        endpointUtil.setupSingleEndpointWithForestParam(docMgr, docMeta, "withForestParamNotString", "double", "float", false, true, "string");
        endpointUtil.setupSingleEndpointWithForestParam(docMgr, docMeta, "withForestParamIsMultiple", "string", "float", true, true, "string");
        adminClient.release();
        
        callableEndpoint = endpointUtil.makeCallableEndpoint("withForestParam");
        caller = endpointUtil.makeOneCaller(callableEndpoint, String.class);
    }
    
    @Test
    public void emptyForestNameTest() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(startsWith("Forest name cannot be null or empty."));
        batcher = caller.batcher().forArgsGenerator(result -> (result == null) ? caller.args().param("param1", 1.1) : result.getArgs(), null);
    }
    
    @Test
    public void forestParamNameTypeNotStringTest() {
        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint("withForestParamNotString");
        CallManager.OneCaller<String> caller = endpointUtil.makeOneCaller(callableEndpoint, String.class);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(startsWith("Forest name parameter cannot be multiple and needs to be a string."));
        caller.batcher().forArgsGenerator(result -> (result == null) ? caller.args().param("forestParamName", "test") : result.getArgs(), "forestParamName");
    }
    
    @Test
    public void forestParamNameWithMultipleReturn() {
        CallManager.CallableEndpoint callableEndpoint = endpointUtil.makeCallableEndpoint("withForestParamIsMultiple");
        CallManager.ManyCaller<String> caller = endpointUtil.makeManyCaller(callableEndpoint, String.class);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(startsWith("Forest name parameter cannot be multiple and needs to be a string."));
        caller.batcher().forArgsGenerator(result -> (result == null) ? caller.args().param("forestParamName", "test") : result.getArgs(), "forestParamName");
    }
    
    @Test
    public void endpointReceivesForestName() {
        class Output{
            String expectedOutput;
        }
        final Output output = new Output();
        batcher = caller.batcher().forArgsGenerator(result -> (result == null) ? caller.args().param("forestParamName", "test") : result.getArgs(), "forestParamName")
                .onCallSuccess(event-> {output.expectedOutput = event.getItem();});
        batcher.startJob();
        batcher.flushAndWait();
        batcher.stopJob();
        
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
