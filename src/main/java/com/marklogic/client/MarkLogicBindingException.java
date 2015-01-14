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
package com.marklogic.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Binding Exception indicates a problem converting between a Java object
 * and XML representation of the data.  The exception may indicate an internal
 * error.  Please contact MarkLogic support.
 */
@SuppressWarnings("serial")
public class MarkLogicBindingException extends RuntimeException {

	static final private Logger logger = LoggerFactory
			.getLogger(MarkLogicBindingException.class);
	
	public MarkLogicBindingException(String message, Throwable e) {
		super(message, e);
	}

	public MarkLogicBindingException(Exception e) {
		super(e);
		e.printStackTrace();
	}

	public MarkLogicBindingException(String msg) {
		logger.error(msg);
	}

}
