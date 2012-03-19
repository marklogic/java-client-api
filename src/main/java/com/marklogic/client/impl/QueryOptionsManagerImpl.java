package com.marklogic.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.config.search.SearchOptions;

public class QueryOptionsManagerImpl implements QueryOptionsManager {


	static final private Logger logger = LoggerFactory.getLogger(AbstractDocumentImpl.class);

	private RESTServices services;
	
	public QueryOptionsManagerImpl(RESTServices services) {
		this.services = services;
	}
	
	@Override
	public SearchOptions readOptions(String name) {
		return services.get(name);
	}

	@Override
	public void writeOptions(String name, SearchOptions options) {
		services.put(name, options);
	}

	@Override
	public void deleteOptions(String name) {
		services.delete(name);
	}

}
