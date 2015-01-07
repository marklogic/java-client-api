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

/**
 * Exception thrown when the server responds with HTTP status code 503
 * and a Retry-After header of 1 but the request is a PUT or POST
 * and the payload is streaming. 
 */
@SuppressWarnings("serial")
public class ResourceNotResendableException extends MarkLogicServerException {

	public ResourceNotResendableException(String message) {
		super(message);
	}

}
