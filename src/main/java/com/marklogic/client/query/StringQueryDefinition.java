/*
 * Copyright 2012 MarkLogic Corporation
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
 * A StringQueryDefinition represents the criteria associated with a simple string query.
 */
public interface StringQueryDefinition extends QueryDefinition {
    /**
     * Returns the query criteria, that is the query string.
     * @return The query string.
     */
    public String getCriteria();

    /**
     * Sets the query criteria, that is the query string.
     * @param criteria The query string.
     */
    public void setCriteria(String criteria);
}
