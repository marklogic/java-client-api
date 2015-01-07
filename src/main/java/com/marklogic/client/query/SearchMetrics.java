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
 * A SearchMetrics object represents the query metrics returned by a search.
 */
public interface SearchMetrics {
    /**
     * Returns the query resolution time in milliseconds.
     * @return The query resolution time.
     */
    public long getQueryResolutionTime();

    /**
     * Returns the facet resolution time in milliseconds.
     * @return The facet resolution time.
     */
    public long getFacetResolutionTime();

    /**
     * Returns the snippet resolution time in milliseconds.
     * @return The snippet resolution time.
     */
    public long getSnippetResolutionTime();

    /**
     * Returns the metadata resolution time in milliseconds.
     * @return The metadata resolution time.
     */
    public long getMetadataResolutionTime();

    /**
     * Returns the total time taken by the query in milliseconds.
     * @return The total time.
     */
    public long getTotalTime();
}
