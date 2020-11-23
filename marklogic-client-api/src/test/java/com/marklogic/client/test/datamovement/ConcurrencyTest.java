package com.marklogic.client.test.datamovement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.datamovement.impl.QueryBatcherImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.*;
import com.marklogic.client.test.Common;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertTrue;

public class ConcurrencyTest {

    private Logger logger = LoggerFactory.getLogger(QueryBatcherTest.class);
    private static DatabaseClient client = Common.connect();
    private static DataMovementManager moveMgr = client.newDataMovementManager();


    @AfterClass
    public static void afterClass() {
        QueryManager queryMgr = client.newQueryManager();
        DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
        deleteQuery.setCollections("ConcurrencyTest");
        queryMgr.delete(deleteQuery);
    }

    @Test
    public void ConcurrencyTest() {
        DataMovementManager dmManager = client.newDataMovementManager();
        List<String> outputUris = Collections.synchronizedList(new ArrayList<String>());
        int batchSize = 20;
        int docToUriBatchRatio = 5;
        int totalCount = 1000;
        int threadThrottleFactor = 4;

        DocumentMetadataHandle documentMetadata = new DocumentMetadataHandle().withCollections("ConcurrencyTest");
        WriteBatcher batcher = moveMgr.newWriteBatcher().withDefaultMetadata(documentMetadata);
        int forests = batcher.getForestConfig().listForests().length;
        moveMgr.startJob(batcher);
        for(int i=0; i<totalCount; i++) {
            batcher.addAs("test"+i+".txt", new StringHandle().with("Test"+i));
        }
        batcher.flushAndWait();
        moveMgr.stopJob(batcher);

        QueryBatcher  queryBatcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("ConcurrencyTest"));
        int forest_count = queryBatcher.getForestConfig().listForests().length;
        AtomicInteger min = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger max = new AtomicInteger(0);
        queryBatcher.withBatchSize(batchSize, docToUriBatchRatio)
                .onUrisReady(batch -> {
                    outputUris.addAll(Arrays.asList(batch.getItems()));
                    ThreadPoolExecutor threadPool = ((QueryBatcherImpl) queryBatcher).getThreadPool();
                    int activeThreadCount = threadPool.getPoolSize();
                    if (activeThreadCount > max.get()) {
                        max.set(activeThreadCount);
                    }
                    if (activeThreadCount < min.get()) {
                        min.set(activeThreadCount);
                    }
                })
                .onQueryFailure((QueryBatchException failure) -> {
                    System.out.println(failure.getMessage());
                });

        dmManager.startJob(queryBatcher);
        queryBatcher.awaitCompletion();
        dmManager.stopJob(queryBatcher);

        assertTrue("Output list does not contain all number of outputs", outputUris.size() == totalCount);

        QueryBatcher queryBatcherAfter = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("ConcurrencyTest"));
        AtomicInteger minAfter = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger maxAfter = new AtomicInteger(0);
        queryBatcherAfter.withBatchSize(batchSize, docToUriBatchRatio, threadThrottleFactor)
                .onUrisReady(batch -> {
                    ThreadPoolExecutor threadPoolAfter = ((QueryBatcherImpl) queryBatcherAfter).getThreadPool();
                    int activeThreadCount = threadPoolAfter.getPoolSize();
                    if (activeThreadCount > maxAfter.get()) {
                        maxAfter.set(activeThreadCount);
                    }
                    if (activeThreadCount < minAfter.get()) {
                        minAfter.set(activeThreadCount);
                    }
                })
                .onQueryFailure((QueryBatchException failure) -> {
                    System.out.println(failure.getMessage());
                });

        dmManager.startJob(queryBatcherAfter);
        queryBatcherAfter.awaitCompletion();
        dmManager.stopJob(queryBatcherAfter);

        assertTrue(max.get() <= forest_count * docToUriBatchRatio);
        assertTrue(maxAfter.get() <= forest_count * (docToUriBatchRatio - threadThrottleFactor));
    }

    static void changeAssignmentPolicy(String value) throws IOException {

        InputStream getResponseStream = null;
        DefaultHttpClient defaultClient = null;

        String propertyName = "assignment-policy";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode mainNode = mapper.createObjectNode();
        ArrayNode childArray = mapper.createArrayNode();
        ObjectNode childNodeObject = mapper.createObjectNode();
        childNodeObject.put("assignment-policy-name", "bucket");
        childArray.add(childNodeObject);
        mainNode.withArray("assignment-policy").add(childArray);
        String dbName = "java-unittest";

        try {
            defaultClient = new DefaultHttpClient();
            defaultClient.getCredentialsProvider().setCredentials(new AuthScope(client.getHost(), 8002),
                    new UsernamePasswordCredentials("admin", "admin"));
            HttpGet getrequest = new HttpGet("http://" + client.getHost() + ":" + 8002 + "/manage/v2/databases/"
                    + dbName + "/properties?format=json");
            HttpResponse getResponse = defaultClient.execute(getrequest);
            getResponseStream = getResponse.getEntity().getContent();
            JsonNode jsonNode = mapper.readTree(getResponseStream);
            if (!jsonNode.isNull()) {
                if (!jsonNode.has(propertyName)) {
                    ((ObjectNode) jsonNode).putArray(propertyName).addAll(mainNode.withArray(propertyName));
                } else {
                    if (!jsonNode.path(propertyName).isArray()) {
                        ((ObjectNode) jsonNode).putAll(mainNode);
                    } else {
                        JsonNode member = jsonNode.withArray(propertyName);
                        if (mainNode.path(propertyName).isArray()) {
                            ((ArrayNode) member).addAll(mainNode.withArray(propertyName));
                        }
                    }
                }

                HttpPut put = new HttpPut("http://" + client.getHost() + ":" + 8002 + "/manage/v2/databases/" + dbName
                        + "/properties?format=json");
                put.addHeader("Content-type", "application/json");
                put.setEntity(new StringEntity(jsonNode.toString()));

                HttpResponse putResponse = defaultClient.execute(put);
                HttpEntity respEntity = putResponse.getEntity();
                if (respEntity != null) {
                    String content = EntityUtils.toString(respEntity);
                    System.out.println(content);
                }
            } else {
                System.out.println("REST call for database properties returned NULL "
                        + getResponse.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (getResponseStream != null)
                getResponseStream.close();
            defaultClient.getConnectionManager().shutdown();
        }
    }

}

