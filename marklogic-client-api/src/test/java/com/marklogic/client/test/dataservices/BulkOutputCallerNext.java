package com.marklogic.client.test.dataservices;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.OutputEndpoint;
import com.marklogic.client.dataservices.impl.OutputEndpointImpl;
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
import java.util.stream.Stream;

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

        OutputEndpointImpl.BulkOutputCaller bulkCaller = OutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj)).bulkCaller();
        bulkCaller.setEndpointState(new ByteArrayInputStream(endpointState.getBytes()));
        bulkCaller.setWorkUnit(new ByteArrayInputStream(workUnit.getBytes()));
        Stream<InputStream> output = bulkCaller.next();
        assertNotNull(output);
        InputStream[] outputArray = output.toArray(size -> new InputStream[size]);
        assertTrue(outputArray.length == 5);

        output = bulkCaller.next();
        assertNotNull(output);
        InputStream[] outputArray1 = output.toArray(size -> new InputStream[size]);
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
