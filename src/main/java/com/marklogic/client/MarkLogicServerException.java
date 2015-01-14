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

import com.marklogic.client.impl.FailedRequest;

/**
 * Abstract class that implements functionality for errors returned from a MarkLogic REST API instance.
 * A REST instance wraps error messages into an XML or JSON structure that can be parsed and turned into
 * the FailedRequest Java object.  The MarkLogicServerException contains the FailedRequest object which
 * is incorporated into getMessage() or can be examined via getFailedRequest()
 * 
 * @see FailedRequest
 *
 */
@SuppressWarnings("serial")
public abstract class MarkLogicServerException extends RuntimeException {
	// NOTE:  to verify that exceptions are declared, switch extends
	// temporarily from RuntimeException to Exception
	private FailedRequest failedRequest;
	
	/**
	 * @param localMessage
	 * @param failedRequest
	 */
	public MarkLogicServerException(String localMessage, FailedRequest failedRequest) {
		super(localMessage);
		this.failedRequest = failedRequest;
	}

	public MarkLogicServerException(String localMessage) {
		super(localMessage);
	}
	
	@Override
	public String getMessage() {
		if (super.getMessage() != null && failedRequest != null) {
			return "Local message: " + super.getMessage() + ". Server Message: " + failedRequest.getMessage();
		}
		else if (failedRequest != null) {
			return failedRequest.getMessage();
		}
		else return super.getMessage();
	}
	
	public FailedRequest getFailedRequest() {
		return failedRequest;
	}
}

