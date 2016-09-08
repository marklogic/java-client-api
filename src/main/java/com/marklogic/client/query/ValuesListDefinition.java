/*
 * Copyright 2012-2016 MarkLogic Corporation
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
 * A ValuesListDefinition is the base type for a values or tuples list.
 */
public interface ValuesListDefinition {
    /**
     * Returns the name of the options node associated with this query.
     * @return The name of the options node.
     */
    String getOptionsName();

    /**
     * Set the name of the options node to be used for this query.
     * @param optname The name of the options node.
     */
    void setOptionsName(String optname);
}

