/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.query;

/**
 * TupleResults represent the values returned by a tuple values query.
 */
public interface TuplesResults {
    /**
     * Returns the query used to locate these tuples.
     * @return The query definition.
     */
    public ValuesDefinition getQueryCriteria();

    /**
     * Returns the name of the tuples.
     * @return The name.
     */
    public String getName();

    /**
     * Returns an array of the aggregates.
     * @return The array of aggrgates.
     */
    public AggregateResult[] getAggregates();

    /**
     * Returns the aggregate result for the named aggregate function.
     * @param name The name of the aggregate function.
     * @return The corresponding aggregate, or null if no such aggregate exists.
     */
    public AggregateResult getAggregate(String name);

    /**
     * Returns an array of Tuples.
     * @return The array of tuples.
     */
    public Tuple[] getTuples();

    /**
     * Returns performance metrics about the query.
     * @return The metrics
     */
    public ValuesMetrics getMetrics();
}

