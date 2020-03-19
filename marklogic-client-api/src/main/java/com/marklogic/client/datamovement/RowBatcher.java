/*
 * Copyright 2020 MarkLogic Corporation
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
    long getRowEstimate();
}


