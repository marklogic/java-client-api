package com.marklogic.client.test.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatchException;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.datamovement.impl.QueryBatcherImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConcurrencyTest {

    private Logger logger = LoggerFactory.getLogger(QueryBatcherTest.class);
    private static DatabaseClient client = Common.connect();
    private static DataMovementManager moveMgr = client.newDataMovementManager();


    @AfterAll
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

        assertTrue( outputUris.size() == totalCount);

/*        QueryBatcher queryBatcherAfter = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("ConcurrencyTest"));
        AtomicInteger minAfter = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger maxAfter = new AtomicInteger(0);
        queryBatcherAfter.withBatchSize(batchSize, docToUriBatchRatio)
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
        dmManager.stopJob(queryBatcherAfter);*/

        assertTrue(max.get() <= forest_count * docToUriBatchRatio);
        //assertTrue(maxAfter.get() <= forest_count * docToUriBatchRatio);
    }
}

