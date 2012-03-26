package com.marklogic.client;

import com.marklogic.client.config.search.SearchOptions;
import com.marklogic.client.io.marker.SearchOptionsReadHandle;
import com.marklogic.client.io.marker.SearchOptionsWriteHandle;

public interface QueryOptionsManager {
	
	/**
	 * Generate a new, empty SearchOptions configuration.
	 * @return an empty SearchOptions object.
	 */
	public SearchOptions newOptions();
	
	/**
	 * Create a SearchOptions configuration by reading it
	 * from the REST Server by name.
	 * @param name
	 * @return the SearchOptions holding the search configuration
	 */
    public <T extends SearchOptionsReadHandle> T readOptions(String name, T searchOptionsHandle);
    
    /**
     * Write a SearchOptions configuration to the REST
     * server using a specific name.
     * @param name
     * @param options
     */
    public <T extends SearchOptionsWriteHandle> void writeOptions(String name, T searchOptionsHandle);
    
    /**
     * Remove a search configuration from the REST server.
     * @param name
     */
    public void deleteOptions(String name);

}
