/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

/**
 * Provides a callback (typically as a lambda) to process an exception
 * when trying to retrieve a batch of rows for a view.
 */
public interface RowBatchFailureListener extends BatchFailureListener<RowBatchFailureListener.RowBatchFailureEvent> {
    /**
     * An exception which occurred when attempting to retrieve a batch of rows
     * for a view.
     */
    interface RowBatchFailureEvent extends BatchEvent {
        /**
         * Whether the RowBatcher should retry the attempt to retrieve the
         * batch of rows, skip the batch of rows, or stop the job.
         * @return how the RowBatcher should dispose of the failure
         */
        BatchFailureDisposition getDisposition();
        /**
         * Specifies whether to retry the attempt to retrieve the batch of rows,
         * skip the batch of rows, or stop the job.
         * @param disposition how the RowBatcher should dispose of the failure
         * @return  the failure event for chaining other configuration
         */
        RowBatchFailureEvent withDisposition(BatchFailureDisposition disposition);
        /**
         * The number of retries before RowBatcher skips the batch.
         * @return the maximum number of retries
         */
        int getMaxRetries();
        /**
         * Specifies the number of retries before skipping the batch.
         * The RowBatcher notifies the failure listener after each retry
         * until the limit is reached.
         * @param maxRetries the maximum number of retries
         * @return  the failure event for chaining other configuration
         */
        RowBatchFailureEvent withMaxRetries(int maxRetries);

        /**
         * The lower boundary for the requested batch of rows (useful
         * primarily for logging).
         * @return the lower boundary
         */
        String getLowerBound();
        /**
         * The upper boundary for the requested batch of rows (useful
         * primarily for logging).
         * @return the upper boundary
         */
        String getUpperBound();
        /**
         * The number of retries so far for this batch of rows.
         * @return the number of retries
         */
        int getBatchRetries();
        /**
         * The total number of batches skipped so far during the job.
         * @return the number of skipped batches
         */
        long getFailedJobBatches();
    }

    /**
     * Specifies how the RowBatcher should respond to the failure
     * to retrieve a batch of rows.
     */
    enum BatchFailureDisposition {
        /**
         * Attempt to retrieve the batch rows again. This disposition
         * is appropriate only if the exception indicates that the
         * request might succeed if repeated.
         */
        RETRY,
        /**
         * Ignore the exception and try to get another batch of rows
         * without retrieving the current batch of rows. This disposition
         * is appropriate only if the exception indicates an issue
         * specific to the batch of rows.
         */
        SKIP,
        /**
         * Stop the job, retrieving no more rows. This disposition
         * is appropriate if the exception indicates that attempts
         * to retrieve batches of rows for a view are likely to fail.
         */
        STOP;
    }

    /**
     *
     * @param batch the batch of items that failed processing
     * @param throwable the exception that caused the failure
     */
    void processFailure(RowBatchFailureEvent batch, Throwable throwable);
}
