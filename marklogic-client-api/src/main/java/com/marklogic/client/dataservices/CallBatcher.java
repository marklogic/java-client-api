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
package com.marklogic.client.dataservices;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.marklogic.client.datamovement.Batcher;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.ForestConfiguration;
import com.marklogic.client.datamovement.JobTicket;

public interface CallBatcher<W,E extends CallManager.CallEvent> extends Batcher {
	
    // fluent configuration consistent with other batchers
    CallBatcher<W,E> onCallSuccess(CallSuccessListener<E> listener);
    CallBatcher<W,E> onCallFailure(CallFailureListener    listener);
    CallBatcher<W,E> withBatchSize(int batchSize);
    CallBatcher<W,E> withForestConfig(ForestConfiguration forestConfig);
    CallBatcher<W,E> withJobId(String jobId);
    CallBatcher<W,E> withJobName(String jobName);
    CallBatcher<W,E> withThreadCount(int threadCount);

    // setters and getters consistent with other batchers
    CallSuccessListener<E>[] getCallSuccessListeners();
    CallFailureListener[]    getCallFailureListeners();

    DataMovementManager getDataMovementManager();

    void setCallSuccessListeners(CallSuccessListener<E>... listeners);
    void setCallFailureListeners(CallFailureListener...    listeners);

    // input queuing consistent with other batchers
    CallBatcher<W,E> add(W input);
    void addAll(Stream<W> input);

    // job management consistent with other batchers
    void awaitCompletion();
    void awaitCompletion(long timeout, TimeUnit unit);
    void flushAndWait();
    void flushAsync();
    JobTicket getJobTicket();
    boolean isStopped();

    // failure recovery consistent with other batchers
    void retry(E event);
    void retryWithFailureListeners(E event);

    interface CallBatcherBuilder<E extends CallManager.CallEvent> {
        CallBatcherBuilder<E> defaultArgs(CallManager.CallArgs args);

        CallBatcher<CallManager.CallArgs,E> forArgs();
    }
}
