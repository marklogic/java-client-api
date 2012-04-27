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
package com.marklogic.client;

import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.marker.QueryOptionsReadHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;

public interface QueryOptionsManager {
	
	
	/**
	 * Generate a new, empty QueryOptionsHandle configuration.
	 * @return an empty QueryOptionsHandle .
	 */
	public QueryOptionsHandle newOptions();
	
	/**
	 * Create a new QueryOptionsHandle by reading a named option
	 * from the REST server by name
	 * @param string
	 * @return
	 */
	public QueryOptionsHandle readOptions(String name);
	
	/**
	 * Create a QueryOptions configuration by reading it
	 * from the REST Server by name.
	 * @param name
	 * @return the QueryOptions holding the search configuration
	 */
    public <T extends QueryOptionsReadHandle> T readOptions(String name, T searchOptionsHandle);
    
	
    /**
     * Write a QueryOptions configuration to the REST
     * server using a specific name.
     * @param name
     * @param format TODO
     * @param options
     */
    public void writeOptions(String name, QueryOptionsWriteHandle searchOptionsHandle);
    
    /**
     * Remove a search configuration from the REST server.
     * @param name
     */
    public void deleteOptions(String name);


}
