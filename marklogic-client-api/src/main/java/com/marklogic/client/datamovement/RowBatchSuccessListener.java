/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

/**
 * Provides a callback (typically as a lambda) to process a batch of rows
 * retrieved for a view.
 *
 * <p>The rows are returned as an instance of the Java class adapted by
 * the sample handle passed to the factory that constructs the RowBatcher
 * (that is, the generic type of the RowBatcher).</p>
 *
 * @param <T>  the Java class that stores a batch of retrieved roles
 */
public interface RowBatchSuccessListener<T> extends BatchListener<RowBatchSuccessListener.RowBatchResponseEvent<T>> {
    /**
     * A batch of rows retrieved for a view.
     *
     * <p>The rows are returned as an instance of the Java class adapted by
     * the sample handle passed to the factory that constructs the RowBatcher
     * (that is, the generic type of the RowBatcher).</p>
     *
     * @param <T>  the Java class that stores a batch of retrieved roles
     */
    interface RowBatchResponseEvent<T> extends BatchEvent {
        /**
         * The lower boundary for the batch of rows (useful
         * primarily for logging).
         * @return the lower boundary
         */
        String getLowerBound();
        /**
         * The upper boundary for the batch of rows (useful
         * primarily for logging).
         * @return the upper boundary
         */
        String getUpperBound();
        /**
         * The batch of rows retrieved for a view as an instance of the Java
         * class adapted by the sample handle passed to the factory that
         * constructs the RowBatcher.
         * @return the rows
         */
        T getRowsDoc();
    }
}
