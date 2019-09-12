/*
 * Copyright 2019 MarkLogic Corporation
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
package com.marklogic.client.dataservices;

import java.io.InputStream;
import java.util.stream.Stream;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.dataservices.impl.InputEndpointImpl;
import com.marklogic.client.io.marker.JSONWriteHandle;

public interface InputEndpoint extends IOEndpoint {
	static InputEndpoint on(DatabaseClient client, JSONWriteHandle apiDecl) {
		return new InputEndpointImpl(client, apiDecl);
	}

	void call(InputStream workUnit, Stream<InputStream> input);

	BulkInputCaller bulkCaller();

	interface BulkInputCaller extends IOEndpoint.BulkIOEndpointCaller {
		void accept(InputStream input);
	}
}
