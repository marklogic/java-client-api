/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

import com.marklogic.client.io.marker.StructureWriteHandle;

/**
 * A RawQueryByExampleDefinition provides access to a simple
 * query by example in an JSON or XML representation. For instance:
 *
 *     QueryManager queryMgr = databaseClient.newQueryManager();
 *     String rawJSONQuery = "{ \"$query\": { \"author\": \"Mark Twain\" } }";
 *     StringHandle qbeHandle = new StringHandle(rawJSONQuery).withFormat(Format.JSON);
 *     RawQueryByExampleDefinition query = queryMgr.newRawQueryByExampleDefinition(qbeHandle, "myoptions");
 *     SearchHandle resultsHandle = queryMgr.search(query, new SearchHandle());
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
  String QBE_NS = "http://marklogic.com/appservices/querybyexample";

  /**
   * Specifies the handle for the JSON or XML representation
   * of a query by example and returns the query definition.
   * @param handle	the JSON or XML handle.
   * @return	the query definition.
   */
  RawQueryByExampleDefinition withHandle(StructureWriteHandle handle);
}
