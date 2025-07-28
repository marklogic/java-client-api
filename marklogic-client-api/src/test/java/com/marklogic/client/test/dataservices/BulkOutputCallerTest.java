/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.dataservices;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.OutputCaller;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BulkOutputCallerTest {
    static ObjectNode apiObj;
    static String apiName = "bulkOutputCallerImpl.api";
    static String scriptPath;
    static String apiPath;
    static JSONDocumentManager docMgr;
    static int count = 5;

    private static final String collectionName = "bulkOutputTest";

    @BeforeAll
    public static void setup() throws Exception {
        docMgr = IOTestUtil.db.newJSONDocumentManager();
        apiObj = IOTestUtil.readApi(apiName);
        scriptPath = IOTestUtil.getScriptPath(apiObj);
        apiPath = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);
        IOTestUtil.writeDocuments(count, collectionName);
    }
    @Test
    public void bulkOutputCallerWithNullConsumer() {
        OutputCaller<InputStream> loadEndpt = OutputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle());
        OutputCaller.BulkOutputCaller<InputStream> loader = loadEndpt.bulkCaller(loadEndpt.newCallContext());
        assertThrows(IllegalStateException.class, () -> loader.awaitCompletion());
    }

    @Test
    public void bulkOutputCallerTest() throws Exception {
        String endpointState = "{\"next\":"+1+"}";
        String endpointConstants      = "{\"max\":"+6+"}";

        OutputCaller<InputStream> caller = OutputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle());

        InputStream[] resultArray = caller.call(caller.newCallContext()
                        .withEndpointStateAs(endpointState)
                .withSessionState(caller.newSessionState())
                .withEndpointConstantsAs(endpointConstants));

        assertNotNull(resultArray);
        assertTrue(resultArray.length-1 == count-1);
        List<String> list = new ArrayList<>();
        for(int i=0;i<resultArray.length;i++) {
            assertNotNull(resultArray[i]);
            list.add(IOTestUtil.mapper.readValue(resultArray[i], ObjectNode.class).toString());
        }
        String endpointConstants2      = "{\"max\":"+3+"}";
        OutputCaller<InputStream> endpoint = OutputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle());
        OutputCaller.BulkOutputCaller<InputStream> bulkCaller = endpoint.bulkCaller(endpoint.newCallContext()
                .withEndpointStateAs(endpointState)
                .withEndpointConstantsAs(endpointConstants2));
        class Output {
            int counter =0;
        }
        Output output = new Output();
        bulkCaller.setOutputListener(i-> {
            try {
                assertTrue(list.contains(IOTestUtil.mapper.readValue(i, ObjectNode.class).toString()));
                output.counter++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

       bulkCaller.awaitCompletion();
       assertTrue(output.counter == count);
    }

    @AfterAll
    public static void cleanup() {
        QueryManager queryMgr = IOTestUtil.db.newQueryManager();
        DeleteQueryDefinition deletedef = queryMgr.newDeleteDefinition();
        deletedef.setCollections(collectionName);
        queryMgr.delete(deletedef);
    }
}
