package com.marklogic.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class MarkLogicBindingException extends RuntimeException {

	static final private Logger logger = LoggerFactory
			.getLogger(MarkLogicBindingException.class);
	
	public MarkLogicBindingException(Exception e) {
		e.printStackTrace();
	}

	public MarkLogicBindingException(String msg) {
		logger.error(msg);
	}

}
