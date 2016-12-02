/**
 * The MarkLogic Data Movement SDK supports long-running write, read,
 * delete, or transform jobs.  Long-running write jobs are enabled by {@link
 * com.marklogic.client.datamovement.WriteBatcher}.
 * Long-running read, delete, or transform jobs are enabled by {@link
 * com.marklogic.client.datamovement.QueryBatcher} which can perform actions
 * {@link com.marklogic.client.datamovement.DataMovementManager#newQueryBatcher(
 * com.marklogic.client.query.StructuredQueryDefinition) on all uris matching a query} or
 * {@link com.marklogic.client.datamovement.DataMovementManager#newQueryBatcher(
 * java.util.Iterator) on all uris provided by an Iterator&lt;String&gt;}.
 *
 * Features:
 *
 * * WriteBatcher
 *     * batches documents for [bulk write][] but improves on performance because it
 *         * writes with many parallel threads
 *         * writes round-robin to all hosts in the cluster with forests for
 *           the specified database
 *     * one instance safely receives calls to {@link
 *       com.marklogic.client.datamovement.WriteBatcher#add add} from many
 *       threads
 *     * supports transforms, metadata, and temporal collections
 * * QueryBatcher
 *     * offers high-performance import from sources not supported by [mlcp][]
 *     * runs provided code on a set of uris (common use cases include but
 *         are not limited to export, delete, and transform)
 *     * provided code can leverage the full feature set of the Java Client API
 *     * uris usually are matches to a query
 *     * for corner cases uris can be provided by an Iterator&lt;String&gt;
 *     * paginates through query matches for best scalability
 *     * paginates with many threads for increased throughput
 *     * directly queries each host in the cluster with forests for
 *         the specified database
 *
 * [bulk write]: http://docs.marklogic.com/guide/java/bulk
 *
 * <a name="provided"></a>
 * # Using Provided Listeners
 *
 * When using QueryBatcher, your custom listeners provided to {@link
 * com.marklogic.client.datamovement.QueryBatcher#onUrisReady onUrisReady} can do
 * anything with each batch of uris and will usually use the [MarkLogic Java
 * Client API][] to do things. However, to simplify common use cases, the
 * following listeners are also provided:
 *
 * | ---                                                       | ---                                                    |
 * | {@link com.marklogic.client.datamovement.ApplyTransformListener} | Modifies documents in-place in the database by applying a {@link com.marklogic.client.document.ServerTransform server-side transform} |
 * | {@link com.marklogic.client.datamovement.ExportListener}         | Downloads each document for further processing in Java |
 * | {@link com.marklogic.client.datamovement.ExportToWriterListener} | Downloads each document and writes it to a Writer (could be a file, HTTP response, in-memory Writer, etc. |
 * | {@link com.marklogic.client.datamovement.DeleteListener}         | Deletes each batch of documents from the server |
 *
 * [MarkLogic Java Client API]: http://docs.marklogic.com/guide/java
 *
 *
 * # Using QueryBatcher
 *
 * When you need to perform actions on server documents beyond what can be
 * done with the [provided listeners](#provided), register your
 * custom code with onUrisReady and your code will be run for each batch of
 * uris.
 *
 *
 * For Example:
 * ```java
 *     QueryBatcher qhb = dataMovementManager.newQueryBatcher(query)
 *         .withBatchSize(1000)
 *         .withThreadCount(20)
           .withConsistentSnapshot()
 *         .onUrisReady(batch -&gt; {
 *             for ( String uri : batch.getItems() ) {
 *                 if ( uri.endsWith(".txt") ) {
 *                     client.newDocumentManager().delete(uri);
 *                 }
 *             }
 *         })
 *         .onQueryFailure(queryBatchException -&gt; queryBatchException.printStackTrace());
 *     JobTicket ticket = dataMovementManager.startJob(qhb);
 *     qhb.awaitCompletion();
 *     dataMovementManager.stopJob(ticket);
 * ```
 *
 *
 *
 *
 *
 * # Using WriteBatcher
 *
 * When you need to write a very large volume of documents and
 * [mlcp][] cannot meet
 * your requirements, use WriteBatcher.
 *
 * For Example:
 *
 * ```java
 *     WriteBatcher whb = dataMovementManager.newWriteBatcher()
 *         .withBatchSize(100)
 *         .withThreadCount(20)
 *         .onBatchSuccess(batch -&gt; {
 *             logger.debug("batch # {}, so far: {}", batch.getJobBatchNumber(), batch.getJobResultsSoFar());
 *         })
 *         .onBatchFailure((batch,throwable) -&gt; throwable.printStackTrace() );
 *     JobTicket ticket = dataMovementManager.startJob(whb);
 *     // the add or addAs methods could be called in separate threads on the
 *     // single whb instance
 *     whb.add  ("doc1.txt", new StringHandle("doc1 contents"));
 *     whb.addAs("doc2.txt", "doc2 contents");
 *
 *     whb.flushAndWait(); // send the two docs even though they're not a full batch
 *     dataMovementManager.stopJob(ticket);
 * ```
 * [mlcp]: https://developer.marklogic.com/products/mlcp
 */
/*
 * Copyright 2015-2016 MarkLogic Corporation
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
package com.marklogic.client.datamovement;
