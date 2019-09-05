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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.ExecEndpoint;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JacksonHandle;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class BulkIOEndpointTest {
    @Test
    public void testBulkExecCallerImpl() throws IOException {
        String apiName = "bulkExecCaller.api";

        String finalStateUri = "/marklogic/ds/test/bulkExecCallerFinalState.json";

        int workMax = 15;

        String endpointState = "{\"next\":5}";
        String workUnit      = "{\"max\":"+workMax+"}";

        ObjectNode apiObj     = IOTestUtil.readApi(apiName);
        String     scriptPath = IOTestUtil.getScriptPath(apiObj);
        String     apiPath    = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);

        ExecEndpoint endpoint = ExecEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));

        ExecEndpoint.BulkExecCaller bulkCaller = endpoint.bulkCaller();
        bulkCaller.setEndpointState(new ByteArrayInputStream(endpointState.getBytes()));
        bulkCaller.setWorkUnit(new ByteArrayInputStream(workUnit.getBytes()));
        bulkCaller.awaitCompletion();

        JSONDocumentManager docMgr = IOTestUtil.db.newJSONDocumentManager();

        JsonNode finalState = docMgr.read(finalStateUri, new JacksonHandle()).get();
        assertNotNull("null final state", finalState);
        assertTrue("final state not object", finalState.isObject());

        JsonNode finalNext = finalState.get("next");
        assertNotNull("null final next", finalNext);
        assertTrue("final next not number", finalNext.isNumber());
        assertEquals("mismatch on final next", workMax, finalNext.asInt());

        docMgr.delete(finalStateUri);

        IOTestUtil.modMgr.delete(scriptPath, apiPath);
    }
}