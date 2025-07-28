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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BulkOutputCallerNext {
    static ObjectNode apiObj;
    static String apiName = "bulkOutputCallerNext.api";
    static String scriptPath;
    static String apiPath;
    static JSONDocumentManager docMgr;

    private static final String collectionName = "bulkOutputCallerNext";

    @BeforeAll
    public static void setup() throws Exception {
        docMgr = IOTestUtil.db.newJSONDocumentManager();
        apiObj = IOTestUtil.readApi(apiName);
        scriptPath = IOTestUtil.getScriptPath(apiObj);
        apiPath = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);
    }

    @Test
    public void bulkOutputCallerNextTest() throws Exception {
        String endpointState = "{\"next\":"+1+"}";
        String endpointConstants      = "{\"limit\":"+5+"}";

        IOTestUtil.writeDocuments(10,collectionName);

        OutputCaller<InputStream> endpoint = OutputCaller.on(IOTestUtil.db, new JacksonHandle(apiObj), new InputStreamHandle());
        OutputCaller.BulkOutputCaller<InputStream> bulkCaller = endpoint.bulkCaller(endpoint.newCallContext()
                .withEndpointStateAs(endpointState)
                .withEndpointConstantsAs(endpointConstants));
        InputStream[] outputArray = bulkCaller.next();
        assertNotNull(outputArray);
        assertTrue(outputArray.length == 5);

        InputStream[] outputArray1 = bulkCaller.next();
        assertNotNull(outputArray1);
        assertTrue(outputArray1.length == 5);
        assertFalse(outputArray.equals(outputArray1));

        List<String> outputList = new ArrayList<>();
        for(InputStream i: outputArray1) {
            outputList.add(IOTestUtil.mapper.readValue(i, ObjectNode.class).toString());
        }
        for(InputStream i: outputArray) {
            assertFalse(outputList.contains(IOTestUtil.mapper.readValue(i, ObjectNode.class).toString()));
        }
    }

    @AfterAll
    public static void cleanup() {

        QueryManager queryMgr = IOTestUtil.db.newQueryManager();
        DeleteQueryDefinition deletedef = queryMgr.newDeleteDefinition();
        deletedef.setCollections(collectionName);
        queryMgr.delete(deletedef);

    }
}
