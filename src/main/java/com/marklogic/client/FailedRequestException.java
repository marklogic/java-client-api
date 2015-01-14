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
 * A FailedRequestException is used to capture and report on problems
 * from the REST API.  This class is a semantically thin one, meaning that it is used as
 * catch-all for various things that can go wrong on the REST server.
 */
@SuppressWarnings("serial")
public class FailedRequestException extends MarkLogicServerException {
	
	public FailedRequestException(String message) {
		super(message);
	}
	
	public FailedRequestException(String localMessage, FailedRequest failedRequest) {
		super(localMessage, failedRequest);
	}
}
