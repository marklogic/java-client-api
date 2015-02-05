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
 * A RawQueryByExampleDefinition provides access to a simple
 * query by example in an JSON or XML representation. For instance:
 * <pre>{@code
 *QueryManager queryMgr = databaseClient.newQueryManager();
 *String rawXMLQuery = "{ \"$query\": { \"author\": \"Mark Twain\" } }";
 *StringHandle qbeHandle = new StringHandle(rawXMLQuery).withFormat(Format.JSON);
 *RawQueryByExampleDefinition query = queryMgr.newRawQueryByExampleDefinition(qbeHandle, "myoptions");
 *SearchHandle resultsHandle = queryMgr.search(query, new SearchHandle());
 *}</pre>
 *
 * @see QueryManager#newRawCombinedQueryDefinitionAs(Format, Object)
 * @see QueryManager#newRawCombinedQueryDefinitionAs(Format, Object, String)
 * @see QueryManager#newRawQueryByExampleDefinition(StructureWriteHandle)
 * @see QueryManager#newRawQueryByExampleDefinition(StructureWriteHandle, String)
 * @see <a href="http://docs.marklogic.com/guide/java">MarkLogic Java Application Developer's Guide</a>
 *      &gt; <a href="http://docs.marklogic.com/guide/java/searches">Searching</a> 
 *      &gt; <a href="http://docs.marklogic.com/guide/java/searches#id_33275">Prototype a Query Using Query By Example</a>
 */
public interface RawQueryByExampleDefinition extends RawQueryDefinition {
	/**
	 * The namespace of the built-in vocabulary for an Query by Example
	 * in XML format.
	 */
	final public static String QBE_NS =
		"http://marklogic.com/appservices/querybyexample";

	/**
	 * Specifies the handle for the JSON or XML representation
	 * of a query by example and returns the query definition.
	 * @param handle	the JSON or XML handle.
	 * @return	the query definition.
	 */
	public RawQueryByExampleDefinition withHandle(StructureWriteHandle handle);
}
