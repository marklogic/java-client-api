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

public interface RowBatchFailureListener extends BatchFailureListener<RowBatchFailureListener.RowBatchFailureEvent> {
    interface RowBatchFailureEvent extends BatchEvent {
        BatchFailureDisposition getDisposition();
        RowBatchFailureEvent withDisposition(BatchFailureDisposition disposition);
        int getMaxRetries();
        RowBatchFailureEvent withMaxRetries(int maxRetries);

        String getLowerBound();
        String getUpperBound();
        int getBatchRetries();
        long getFailedJobBatches();
    }

    enum BatchFailureDisposition {
        RETRY, SKIP, STOP;
    }

    void processFailure(RowBatchFailureEvent batch, Throwable throwable);
}
