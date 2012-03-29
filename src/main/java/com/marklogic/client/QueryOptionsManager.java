package com.marklogic.client;

import com.marklogic.client.config.search.QueryOptions;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.marker.QueryOptionsReadHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;

public interface QueryOptionsManager {
	
	/**
	 * Generate a new, empty QueryOptions configuration.
	 * @return an empty QueryOptions object.
	 */
	public QueryOptions newOptions();
	
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
     * @param options
     */
    public void writeOptions(String name, QueryOptionsWriteHandle searchOptionsHandle);
    
    /**
     * Remove a search configuration from the REST server.
     * @param name
     */
    public void deleteOptions(String name);

}
