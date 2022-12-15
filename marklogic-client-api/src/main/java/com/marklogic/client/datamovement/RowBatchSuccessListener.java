/*
 * Copyright (c) 2022 MarkLogic Corporation
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
