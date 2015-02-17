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
 * A RawQueryDefinition provides access to a query
 * in a JSON or XML representation.
 */
public interface RawQueryDefinition extends QueryDefinition {
	/**
	 * Returns the handle for the JSON or XML representation of the query.
	 * @return	the JSON or XML handle.
	 */
	public StructureWriteHandle getHandle();

	/**
	 * Specifies the handle for the JSON or XML representation of the query.
	 * @param handle	the JSON or XML handle.
	 */
	public void setHandle(StructureWriteHandle handle);
}
