package com.marklogic.client.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.dataservices.OutputEndpoint;
import com.marklogic.client.io.JacksonHandle;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MarklogicSourceTask extends SourceTask {
    private static final Logger logger = LoggerFactory.getLogger(MarklogicSourceTask.class);

    static ObjectNode apiObj;
    static String apiName;
    static String scriptPath;
    static String apiPath;
    OutputEndpoint.BulkOutputCaller bulkCaller;
    int sourceOffset; // more info here
    DatabaseClient db;
    final static ObjectMapper mapper = new ObjectMapper();
    Map<String, Integer> strOffset = new HashMap<>();

  //  final public static DatabaseClient.ConnectionType CONNECTION_TYPE =
       //     DatabaseClient.ConnectionType.valueOf(System.getProperty("TEST_CONNECT_TYPE", "DIRECT"));
    @Override
    public String version() {
        return "2.3.0";
    }

    @Override
    public void start(Map<String, String> props) {
        logger.info("*******************Starting******************");
        try {
            apiName = "bulkOutputCallerNext.api";
            logger.info("******************* apiname ****************** "+ apiName);
            logger.info("******************* properties ****************** "+ props);
            db = DatabaseClientFactory.newClient(props.get("ml.connection.host"), 8012,
                    new DatabaseClientFactory.DigestAuthContext(props.get("ml.connection.username"), props.get("ml.connection.password")),
                    Common.CONNECTION_TYPE);
            apiObj = IOTestUtil.readApi(apiName);
            scriptPath = IOTestUtil.getScriptPath(apiObj);
            apiPath = IOTestUtil.getApiPath(scriptPath);
            IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);
            String forestName = props.get("forestName");
            logger.info("******************* forestName ****************** "+ forestName);
           String endpointState = "{\"next\":"+1+"}";
            String workUnit      = "{\"limit\":"+100+", \"forestName\":\""+forestName+"\"}";
           // String workUnit      = "{\"limit\":"+100+"}";
           // String workUnit      = "{\"forestName\":\""+forestName+"\"}";
            bulkCaller = OutputEndpoint.on(db, new JacksonHandle(apiObj)).bulkCaller();
            bulkCaller.setEndpointState(new ByteArrayInputStream(endpointState.getBytes()));
            bulkCaller.setWorkUnit(new ByteArrayInputStream(workUnit.getBytes()));
            strOffset.put("record",0);
        //sourceOffset = 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        logger.info("******************* Polling ******************");
        List<SourceRecord> sourceRecord = new ArrayList<>();
        try {
            InputStream[] output;

            output = bulkCaller.next();
            logger.info("++++++++++++++++ number of records pulled +++++++++++++++ "+output.length);
            if(output == null || output.length==0) {
                System.exit(0);
            }
            Map<String, String> sourcePartition = new HashMap<>();
            sourcePartition.put("db", "java-unittest");
            // one sourcerecord per output
          //  SourceRecord sr = new SourceRecord(sourcePartition, sourceOffset++, "test", Schema.STRING_SCHEMA, output);
          //  return List.of(sr);
            for(InputStream i:output) {
                int rec = strOffset.get("record");
                strOffset.put("record", rec++);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                i.transferTo(baos);
                InputStream recordContent = new ByteArrayInputStream(baos.toByteArray());
              //  InputStream secondClone = new ByteArrayInputStream(baos.toByteArray());
                SourceRecord sr = new SourceRecord(sourcePartition, strOffset, "marklogic", Schema.STRING_SCHEMA, recordContent);
                sourceRecord.add(sr);
                logger.info("record pulled is - "+new String(baos.toByteArray()));
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return sourceRecord;
    }

    @Override
    public void stop() {
        logger.info("Stopping");
        //db.release();
       // this
    }
}
