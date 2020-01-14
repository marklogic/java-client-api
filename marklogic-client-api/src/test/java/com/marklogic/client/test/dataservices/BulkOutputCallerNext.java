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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.OutputEndpoint;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class BulkOutputCallerNext {
    static ObjectNode apiObj;
    static String apiName = "bulkOutputCallerNext.api";
    static String scriptPath;
    static String apiPath;
    static JSONDocumentManager docMgr;

    private static String collectionName = "bulkOutputCallerNext";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
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
        String workUnit      = "{\"limit\":"+5+"}";

        IOTestUtil.writeDocuments(10,collectionName);

        OutputEndpoint.BulkOutputCaller bulkCaller = OutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj)).bulkCaller();
        bulkCaller.setEndpointState(new ByteArrayInputStream(endpointState.getBytes()));
        bulkCaller.setWorkUnit(new ByteArrayInputStream(workUnit.getBytes()));
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

    @AfterClass
    public static void cleanup() {

        QueryManager queryMgr = IOTestUtil.db.newQueryManager();
        DeleteQueryDefinition deletedef = queryMgr.newDeleteDefinition();
        deletedef.setCollections(collectionName);
        queryMgr.delete(deletedef);

    }
}
