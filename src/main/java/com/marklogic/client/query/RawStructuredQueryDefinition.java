/*
 * Copyright 2013-2015 MarkLogic Corporation
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
package com.marklogic.client.query;

import com.marklogic.client.io.marker.StructureWriteHandle;

/**
 * A RawStructuredQueryDefinition provides access to a structured query
 * in a JSON or XML representation.
 */
public interface RawStructuredQueryDefinition extends RawQueryDefinition {
	/**
	 * Specifies the handle for the JSON or XML representation
	 * of a structured query and returns the query definition.
	 * @param handle	the JSON or XML handle.
	 * @return	the query definition.
	 */
	public RawStructuredQueryDefinition withHandle(StructureWriteHandle handle);
}
