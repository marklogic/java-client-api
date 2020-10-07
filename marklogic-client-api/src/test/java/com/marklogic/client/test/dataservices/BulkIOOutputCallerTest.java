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

public class BulkIOOutputCallerTest {
    static ObjectNode apiObj;
    static String apiName = "bulkIOOutputCaller.api";
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
        writeDocuments(30,40, collectionName_2);
    }

    @Test
    public void bulkOutputCallerTestWithMultipleCallContexts() {

        String endpointState1 = "{\"next\":"+1+"}";
        String endpointConstants1      = "{\"max\":6,\"limit\":5,\"collection\":\"bulkOutputTest_1\"}";

        String endpointState2 = "{\"next\":"+1+"}";
        String endpointConstants2      =  "{\"max\":6,\"limit\":5,\"collection\":\"bulkOutputTest_2\"}";
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
        assertEquals("unexpected output count", expected.size(), actual.size());
        assertEquals("unexpected output values", expected, actual);
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
