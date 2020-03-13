package com.marklogic.client.datamovement;

import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.marker.StructureReadHandle;

import java.util.concurrent.TimeUnit;

public interface RowBatcher<T extends StructureReadHandle> extends Batcher {

    RowBatcher<T> withBatchView(PlanBuilder.ModifyPlan viewPlan);

    @Override
    RowBatcher<T> withBatchSize(int batchSize);

    RowBatcher<T> onSuccess(RowBatchSuccessListener<T> listener);
    RowBatcher<T> onFailure(RowBatchFailureListener listener);

    @Override
    RowBatcher<T> withJobId(String jobId);

    @Override
    RowBatcher<T> withJobName(String jobName);

    RowBatcher<T> withConsistentSnapshot();

    RowBatchSuccessListener<T>[] getSuccessListeners();
    RowBatchFailureListener[] getFailureListeners();

    void setSuccessListeners(RowBatchSuccessListener<T>... listeners);
    void setFailureListeners(RowBatchFailureListener... listeners);

    boolean awaitCompletion();
    boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException;

    JobTicket getJobTicket();
    boolean isStopped();

    void retry(RowBatchRequestEvent event);
    void retryWithFailureListeners(RowBatchRequestEvent event);
}


