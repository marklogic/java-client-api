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

import java.util.HashMap;

/**
 * This interface supports access to the list of named query options provided by the server.
 */
public interface QueryOptionsListResults {
    /**
     * Returns a HashMap of the named query options from the server.
     *
     * The keys are the names of the query options, the values are the corresponding URIs on the server.
     *
     * @return The map of names to URIs.
     */
    public HashMap<String, String> getValuesMap();
}


