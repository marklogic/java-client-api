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
 * An IO Exception indicates that there was a problem on input or output (similar
 * to a java.lang.IOException but defined as a runtime rather than checked
 * exception).
 */
@SuppressWarnings("serial")
public class MarkLogicIOException extends RuntimeException {


	public MarkLogicIOException(String message) {
		super(message);
	}

	public MarkLogicIOException(Throwable cause) {
		super(cause);
	}

	public MarkLogicIOException(String message, Throwable cause) {
		super(message, cause);
	}

}
