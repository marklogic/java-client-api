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
package com.marklogic.client.config;

/**
 * ValuesResults represents one set of values from a values query.
 */
public interface ValuesResults {
    /**
     * Return the query definition for this set of results.
     * @return The query criteria.
     */
    public ValuesDefinition getQueryCriteria();

    /**
     * Returns the name of the values.
     * @return The name.
     */
    public String getName();

    /**
     * Returns the type of the values.
     * @return The type.
     */
    public Class getType();

    /**
     * Returns an array of the values.
     * @return The array of values.
     */
    public CountedDistinctValue[] getValues();
}

