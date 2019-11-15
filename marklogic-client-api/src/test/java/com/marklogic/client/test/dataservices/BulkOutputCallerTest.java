package com.marklogic.client.test.dataservices;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.OutputEndpoint;
import com.marklogic.client.dataservices.impl.OutputCallerImpl;
import com.marklogic.client.dataservices.impl.OutputEndpointImpl;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import org.hamcrest.core.StringContains;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;

import static org.junit.Assert.*;

public class BulkOutputCallerTest {
    static ObjectNode apiObj;
    static String apiName = "bulkOutputCallerImpl.api";
    static String scriptPath;
    static String apiPath;
    static JSONDocumentManager docMgr;
    static int count = 5;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setup() throws Exception {
        docMgr = IOTestUtil.db.newJSONDocumentManager();
        apiObj = IOTestUtil.readApi(apiName);
        scriptPath = IOTestUtil.getScriptPath(apiObj);
        apiPath = IOTestUtil.getApiPath(scriptPath);
        IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);
        writeDocuments(count);
    }
    @Test
    public void bulkOutputCallerWithNullConsumer() {
        OutputEndpoint loadEndpt = OutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj));
        OutputEndpoint.BulkOutputCaller loader = loadEndpt.bulkCaller();

        expectedException.expect(IllegalStateException.class);
        expectedException.expect(new ThrowableMessageMatcher(new StringContains("Output consumer is null")));
        loader.awaitCompletion();
    }

    @Test
    public void bulkOutputCallerTest() throws Exception {
        String endpointState = "{\"next\":"+1+"}";
        String workUnit      = "{\"max\":"+6+"}";

        OutputCallerImpl caller = new OutputCallerImpl(new JacksonHandle(apiObj));

        Stream<InputStream> result = caller.streamCall(IOTestUtil.db, IOTestUtil.asInputStream(endpointState), caller.newSessionState(),
                IOTestUtil.asInputStream(workUnit));

        InputStream[] resultArray = result.toArray(size -> new InputStream[size]);
        assertNotNull(resultArray);
        assertTrue(resultArray.length-1 == count);
        List<String> list = new ArrayList<>();
        for(int i=1;i<resultArray.length;i++) {
            assertNotNull(resultArray[i]);
            list.add(IOTestUtil.mapper.readValue(resultArray[i], ObjectNode.class).toString());
        }
        String workUnit2      = "{\"max\":"+3+"}";
        OutputEndpointImpl.BulkOutputCaller bulkCaller = OutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj)).bulkCaller();
        bulkCaller.setEndpointState(new ByteArrayInputStream(endpointState.getBytes()));
        bulkCaller.setWorkUnit(new ByteArrayInputStream(workUnit2.getBytes()));
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

    @Test
    public void bulkOutputCallerNextTest() throws Exception {
        String endpointState = "{\"next\":"+1+"}";
        String workUnit      = "{\"max\":"+6+"}";

        writeDocuments(10);
        count = 10;

        OutputCallerImpl caller = new OutputCallerImpl(new JacksonHandle(apiObj));

        Stream<InputStream> result = caller.streamCall(IOTestUtil.db, IOTestUtil.asInputStream(endpointState), caller.newSessionState(),
                IOTestUtil.asInputStream(workUnit));

        InputStream[] resultArray = result.toArray(size -> new InputStream[size]);
        assertNotNull(resultArray);
        assertTrue(resultArray.length-1 == count);
        List<String> list = new ArrayList<>();
        for(int i=1;i<resultArray.length;i++) {
            assertNotNull(resultArray[i]);
            list.add(IOTestUtil.mapper.readValue(resultArray[i], ObjectNode.class).toString());
        }

        OutputEndpointImpl.BulkOutputCaller bulkCaller = OutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj)).bulkCaller();
        bulkCaller.setEndpointState(new ByteArrayInputStream(endpointState.getBytes()));
        bulkCaller.setWorkUnit(new ByteArrayInputStream(workUnit.getBytes()));
        for (int j=0; j<4; j++) {
            Stream<InputStream> output = bulkCaller.next();
            assertNotNull(output);
            InputStream[] outputArray = output.toArray(size -> new InputStream[size]);
            assertTrue("Output Array from Bulk Output Endpoint does not contain all the documents written.", outputArray.length == count);
            for (InputStream i : outputArray) {
                assertNotNull(i);
                String data = IOTestUtil.mapper.readValue(i, ObjectNode.class).toString();
                assertTrue("List does not contain " + data, list.contains(data));
            }
        }
    }

    @AfterClass
    public static void cleanup() {
        QueryManager queryMgr = IOTestUtil.db.newQueryManager();
        DeleteQueryDefinition deletedef = queryMgr.newDeleteDefinition();
        deletedef.setCollections("bulkOutputTest");
        queryMgr.delete(deletedef);
    }

    private static void writeDocuments(int count) {
        JSONDocumentManager manager = IOTestUtil.db.newJSONDocumentManager();
        DocumentMetadataHandle metadata = new DocumentMetadataHandle().withCollections("bulkOutputTest");

        for(int i=1;i<=count;i++) {
            StringHandle data = new StringHandle("{\"docNum\":"+i+", \"docName\":\"doc"+i+"\"}");
            manager.write("/test"+i+".json", metadata, data);
        }
    }
}
