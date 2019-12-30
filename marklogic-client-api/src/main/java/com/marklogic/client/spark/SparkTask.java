package com.marklogic.client.spark;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.dataservices.OutputEndpoint;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.kafka.Common;
import com.marklogic.client.kafka.IOTestUtil;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class SparkTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SparkTask.class);

    static ObjectNode apiObj;
    static String apiName;
    static String scriptPath;
    static String apiPath;
    OutputEndpoint.BulkOutputCaller bulkCaller;
    DatabaseClient db;
    int recordCount;
    int id;
    SparkSession spark;
    String productName;
    DatabaseClient restClient;
    DatabaseClient noRestClient;
    String endPointModulePath;
    String endPointModuleName;
    String modelPath;
    String includeTraining;

    public SparkTask(String forestName) {
        try {
            apiName = "bulkOutputCallerNext.api";
            db = DatabaseClientFactory.newClient("localhost", 8012,
                    new DatabaseClientFactory.DigestAuthContext("admin", "admin"),
                    Common.CONNECTION_TYPE);
            apiObj = IOTestUtil.readApi(apiName);
            scriptPath = IOTestUtil.getScriptPath(apiObj);
            apiPath = IOTestUtil.getApiPath(scriptPath);
            IOTestUtil.load(apiName, apiObj, scriptPath, apiPath);
            String endpointState = "{\"next\":"+1+"}";
            String workUnit      = "{\"limit\":"+10+", \"forestName\":\""+forestName+"\"}";
            bulkCaller = OutputEndpoint.on(db, new JacksonHandle(apiObj)).bulkCaller();
            bulkCaller.setEndpointState(new ByteArrayInputStream(endpointState.getBytes()));
            bulkCaller.setWorkUnit(new ByteArrayInputStream(workUnit.getBytes()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        InputStream[] output = bulkCaller.next();
        while(output !=null && output.length!=0) {
            try {
                logger.info("Number of records pulled = " + output.length);
                for (InputStream i : output) {
                    System.out.println(IOTestUtil.mapper.readValue(i, ObjectNode.class).toString());
                }
                output = bulkCaller.next();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } ;
        System.out.println("success");
    }
}
