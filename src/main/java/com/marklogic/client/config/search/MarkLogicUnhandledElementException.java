package com.marklogic.client.config.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkLogicUnhandledElementException extends RuntimeException {


	Logger logger = (Logger) LoggerFactory
			.getLogger(MarkLogicUnhandledElementException.class);
	
	
	public MarkLogicUnhandledElementException(String name) {
		logger.error("Unexpected Element encountered when parsing search options.  Element JAXB Class: "+name );
		
	}

}
