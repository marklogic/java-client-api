package com.marklogic.client.config.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@SuppressWarnings("serial")
public class MarkLogicIOException extends RuntimeException {

	static final private Logger logger = LoggerFactory
			.getLogger(MarkLogicIOException.class);
	
	Exception thrown;
	
	public MarkLogicIOException(String string, Exception e) {
		thrown = e;
		logger.error(string);
		logger.error(e.getMessage());
		e.printStackTrace();
	}

	public MarkLogicIOException(String string) {
		logger.error(string);
	}

}
