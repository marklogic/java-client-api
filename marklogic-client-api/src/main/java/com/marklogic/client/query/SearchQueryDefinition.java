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
package com.marklogic.client.query;

import com.marklogic.client.document.ServerTransform;

/**
 * A SearchQueryDefinition represents the common parts of all kinds of searches that can be performed.
 */
public interface SearchQueryDefinition {
    /**
     * Returns the name of the query options used for this query.
     * @return The options name.
     */
    String getOptionsName();

    /**
     * Sets the name of the query options to be used for this query.
     *
     * If no query options node with the specified name exists, the search will fail.
     *
     * @param name The name of the saved query options node on the server.
     */
    void setOptionsName(String name);

    /**
     * Returns the transform that modifies responses to this query
     * on the server.
     * @return The transform.
     */
    ServerTransform getResponseTransform();

    /**
     * Specifies a transform that modifies responses to this query
     * on the server.
     * @param transform	A server transform to modify the query response.
     */
    void setResponseTransform(ServerTransform transform);
}
