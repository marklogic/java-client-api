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
package com.marklogic.client.admin;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.QueryOptionsListReadHandle;
import com.marklogic.client.io.marker.QueryOptionsReadHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;

/**
 * A QueryOptionsManager support database operations on QueryOptionsHandle instances.
 * 
 */
public interface QueryOptionsManager {
    /**
     * Retrieves the list of available named query options in a JSON or
     * XML representation provided as an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param format	whether to provide the list in a JSON or XML representation
     * @param as	the IO class for reading the list of options
	 * @return	an object of the IO class with the option names
     */
	public <T> T optionsListAs(Format format, Class<T> as)
		throws ForbiddenUserException, FailedRequestException;
    /**
     * Retrieves the list of available named query options in a JSON or
     * XML representation provided as an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param listHandle a handle for reading the list of name options
     * @return the handle populated with the names
     */
    public <T extends QueryOptionsListReadHandle> T optionsList(T listHandle)
    	throws ForbiddenUserException, FailedRequestException;

    /**
	 * Fetch a query options configuration from the REST Server by name.
     * 
	 * @param name the name of options configuration stored on MarkLogic REST instance.
     * @param format	whether to provide the options in a JSON or XML representation
     * @param as	the IO class for reading the query options
	 * @return an object of the IO class with the query options
     */
	public <T> T readOptionsAs(String name, Format format, Class<T> as)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
	/**
	 * Fetch a query options configuration from the REST Server by name.
	 * <p>
	 * Use a QueryOptionsHandle object for access to the configuration with Java.
	 * 
	 * @param name the name of options configuration stored on MarkLogic REST instance.
	 * @param queryOptionsHandle an object into which to fetch the query options.
	 * @param <T> a set of classes able to read query configurations from the database.
	 * @return an object holding the query configurations
	 */
    public <T extends QueryOptionsReadHandle> T readOptions(String name, T queryOptionsHandle)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /**
     * Write a named QueryOptions configuration to the REST server in a JSON or
     * XML representation provided as an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param name name given to the QueryOptions for use in runtime queries
     * @param format	whether the options are provided in a JSON or XML representation
     * @param queryOptions	an IO representation of the JSON or XML query options
     */
    public void writeOptionsAs(String name, Format format, Object queryOptions)
    	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
    /**
     * Write a named QueryOptions configuration to the REST server.
     * @param name name given to the QueryOptions for use in runtime queries
     * @param queryOptionsHandle an object able to serialize a QueryOptions configuration
     */
    public void writeOptions(String name, QueryOptionsWriteHandle queryOptionsHandle)
    	throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException;
    
    /**
     * Remove a search configuration from the REST server.
     * @param name name of query options to remove from the REST server.
     */
    public void deleteOptions(String name)
    	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
}
