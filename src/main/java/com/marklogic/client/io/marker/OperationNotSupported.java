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
package com.marklogic.client.io.marker;

/**
 * A handle uses OperationNotSupported as a read or write interface
 * when the handle does not support that operation.  For instance,
 * {@link com.marklogic.client.io.SearchHandle} uses OperationNotSupported
 * as its write interface because because a SearchHandle can be used only
 * to read search results and not to write anything to the database.
 */
public class OperationNotSupported {
	private OperationNotSupported() {
	}
}
