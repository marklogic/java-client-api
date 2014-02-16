/*
 * Copyright 2012-2014 MarkLogic Corporation
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
package com.marklogic.client.admin;

import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.QueryOptionsListReadHandle;
import com.marklogic.client.io.marker.QueryOptionsReadHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;

/**
 * A QueryOptionsManager support database operations on QueryOptionsHandle instances.
 * 
 */
public interface QueryOptionsManager {
	
	
	/**
	 * Fetch a query options configuration from the REST Server by name.
	 * <p>
	 * Use a QueryOptionsHandle object for access to the configuration with Java.
	 * 
	 * @param name Name of options configuration stored on MarkLogic REST instance.
	 * @param queryOptionsHandle an object into which to fetch the query options.
	 * @param <T> A set of classes able to read query configurations from the database.
	 * @return A an object holding the query configurations
	 *
	 */
    public <T extends QueryOptionsReadHandle> T readOptions(String name, T queryOptionsHandle);
    
	
    /**
     * Write a named QueryOptions configuration to the REST server.
     * @param name name given to the QueryOptions for use in runtime queries.
     * @param queryOptionsHandle an object able to serialize a QueryOptions configuration.
     */
    public void writeOptions(String name, QueryOptionsWriteHandle queryOptionsHandle);
    
    /**
     * Remove a search configuration from the REST server.
     * @param name name of query options to remove from the REST server.
     */
    public void deleteOptions(String name);

    /**
     * Retrieves the list of available named query options.
     * @param listHandle a handle for reading the list of name options
     * @return the handle populated with the names
     */
    public <T extends QueryOptionsListReadHandle> T optionsList(T listHandle);


}
