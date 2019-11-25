package com.marklogic.client.test.dataservices;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.dataservices.OutputEndpoint;
import com.marklogic.client.dataservices.impl.OutputEndpointImpl;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MarklogicSourceTask extends SourceTask {
    private static final Logger logger = LoggerFactory.getLogger(MarklogicSourceTask.class);

    static ObjectNode apiObj;
    static String apiName = "output2.api";
    static String scriptPath;
    static String apiPath;
    OutputEndpoint.BulkOutputCaller bulkCaller;
    int sourceOffset; // more info here
    @Override
    public String version() {
        return "2.3.0";
    }

    @Override
    public void start(Map<String, String> props) {
        logger.info("Starting");
        try {
            apiName = props.get("apiName");
            apiObj = IOTestUtil.readApi(apiName);
            scriptPath = IOTestUtil.getScriptPath(apiObj);
            apiPath = IOTestUtil.getApiPath(scriptPath);
            IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);
            String forestName = props.get("forestName");
           String endpointState = "{\"next\":"+1+"}";
            String workUnit      = "{\"limit\":"+100+"}"+"{\"forestName\":"+forestName+"}";
            bulkCaller = OutputEndpoint.on(IOTestUtil.db, new JacksonHandle(apiObj)).bulkCaller();
            bulkCaller.setEndpointState(new ByteArrayInputStream(endpointState.getBytes()));
            bulkCaller.setWorkUnit(new ByteArrayInputStream(workUnit.getBytes()));
        sourceOffset = 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {

        List<SourceRecord> sourceRecord = new ArrayList<>();
        try {

            InputStream[] output = bulkCaller.next();
            Map<String, String> sourcePartition = new HashMap<>();
            sourcePartition.put("db", "java-unittest");
            // one sourcerecord per output
          //  SourceRecord sr = new SourceRecord(sourcePartition, sourceOffset++, "test", Schema.STRING_SCHEMA, output);
          //  return List.of(sr);

            for(InputStream i:output) {
                SourceRecord sr = new SourceRecord(sourcePartition, null, "test", Schema.STRING_SCHEMA, i);
                sourceRecord.add(sr);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return sourceRecord;
    }

    @Override
    public void stop() {
        logger.info("Stopping");
        IOTestUtil.db.release();
    }
}
