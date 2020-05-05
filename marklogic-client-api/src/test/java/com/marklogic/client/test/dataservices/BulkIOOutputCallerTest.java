package com.marklogic.client.test.dataservices;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.IOEndpoint;
import com.marklogic.client.dataservices.OutputEndpoint;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class BulkIOOutputCallerTest {
    static ObjectNode apiObj;
    static String apiName = "bulkIOOutputCaller.api";
    static String scriptPath;
    static String apiPath;
    static JSONDocumentManager docMgr;
    static List<String> list = new ArrayList<>();

    private static String collectionName_1 = "bulkOutputTest_1";
    private static String collectionName_2 = "bulkOutputTest_2";

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
        writeDocuments(30,40, collectionName_2);
    }

    @Test
    public void bulkOutputCallerTestWithMultipleCallContexts() throws Exception {

        String endpointState1 = "{\"next\":"+1+"}";
        String workUnit1      = "{\"max\":6,\"limit\":5,\"collection\":\"bulkOutputTest_1\"}";

        String endpointState2 = "{\"next\":"+1+"}";
        String workUnit2      =  "{\"max\":6,\"limit\":5,\"collection\":\"bulkOutputTest_2\"}";
        OutputEndpoint endpoint = OutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        IOEndpoint.CallContext[] callContextArray = {endpoint.newCallContext()
                .withEndpointState(new ByteArrayInputStream(endpointState2.getBytes()))
                .withWorkUnit(new ByteArrayInputStream(workUnit2.getBytes())), endpoint.newCallContext()
                .withEndpointState(new ByteArrayInputStream(endpointState1.getBytes()))
                .withWorkUnit(new ByteArrayInputStream(workUnit1.getBytes()))};
        OutputEndpoint.BulkOutputCaller bulkCaller = endpoint.bulkCaller(callContextArray);
        class Output {
            int counter =0;
        }
        Output output = new Output();
        bulkCaller.setOutputListener(i-> {
            try {
                assertTrue(list.contains(IOTestUtil.mapper.readValue(i, ObjectNode.class).toString()));
                output.counter++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        bulkCaller.awaitCompletion();
        assertTrue(output.counter == 20);
    }

    @AfterClass
    public static void cleanup() {

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
            list.add(data.toString());
        }
    }
}
