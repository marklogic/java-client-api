/**
 * <p>The MarkLogic Data Movement SDK supports long-running write, read,
 * delete, or transform jobs.  Long-running write jobs are enabled by {@link
 * com.marklogic.client.datamovement.WriteBatcher}.
 * Long-running read, delete, or transform jobs are enabled by {@link
 * com.marklogic.client.datamovement.QueryBatcher} which can perform actions
 * {@link com.marklogic.client.datamovement.DataMovementManager#newQueryBatcher(
 * com.marklogic.client.query.StructuredQueryDefinition) on all uris matching a query} or
 * {@link com.marklogic.client.datamovement.DataMovementManager#newQueryBatcher(
 * java.util.Iterator) on all uris provided by an Iterator&lt;String&gt;}.</p>
 *
 * Features:
 *
 * <ul>
 *   <li>WriteBatcher
 *   <ul>
 *     <li>batches documents for
 *       <a href="http://docs.marklogic.com/guide/java/bulk">bulk write</a>
 *       but improves on performance because it
 *     <ul>
 *         <li>writes with many parallel threads
 *         <li>writes round-robin to all hosts in the cluster with forests for
 *           the specified database
 *     </ul>
 *     <li>one instance safely receives calls to {@link
 *       com.marklogic.client.datamovement.WriteBatcher#add add} from many
 *       threads
 *     <li>supports transforms, metadata, and temporal collections
 *   </ul>
 *   <li>QueryBatcher
 *   <ul>
 *     <li>offers high-performance import from sources not supported by
 *       <a href="https://developer.marklogic.com/products/mlcp">mlcp</a>
 *     <li>runs provided code on a set of uris (common use cases include but
 *         are not limited to export, delete, and transform)
 *     <li>provided code can leverage the full feature set of the Java Client API
 *     <li>uris usually are matches to a query
 *     <li>for corner cases uris can be provided by an Iterator&lt;String&gt;
 *     <li>paginates through query matches for best scalability
 *     <li>paginates with many threads for increased throughput
 *     <li>directly queries each host in the cluster with forests for
 *         the specified database
 *   </ul>
 * </ul>
 *
 * <a name="provided"></a>
 * <h2>Using Provided Listeners</h2>
 *
 * <p>When using QueryBatcher, your custom listeners provided to {@link
 * com.marklogic.client.datamovement.QueryBatcher#onUrisReady onUrisReady} can do
 * anything with each batch of uris and will usually use the
 * <a href="http://docs.marklogic.com/guide/java">MarkLogic Java Client API</a>
 * to do things. However, to simplify common use cases, the
 * following listeners are also provided:</p>
 *
 * <pre>
 *   {@link com.marklogic.client.datamovement.ApplyTransformListener}  - Modifies documents in-place in the database by applying a {@link com.marklogic.client.document.ServerTransform server-side transform}
 *   {@link com.marklogic.client.datamovement.ExportListener}          - Downloads each document for further processing in Java
 *   {@link com.marklogic.client.datamovement.ExportToWriterListener}  - Downloads each document and writes it to a Writer (could be a file, HTTP response, in-memory Writer, etc.
 *   {@link com.marklogic.client.datamovement.DeleteListener}          - Deletes each batch of documents from the server
 *   {@link com.marklogic.client.datamovement.UrisToWriterListener}    - Writes each uri to a Writer (could be a file, HTTP response, etc.).
 * </pre>
 *
 *
 * <h2>Using QueryBatcher</h2>
 *
 * <p>When you need to perform actions on server documents beyond what can be
 * done with the <a href="#provided">provided listeners</a>, register your
 * custom code with onUrisReady and your code will be run for each batch of
 * uris.</p>
 *
 * For Example:
 * <pre>{@code
 *     QueryBatcher qhb = dataMovementManager.newQueryBatcher(query)
 *         .withBatchSize(1000)
 *         .withThreadCount(20)
           .withConsistentSnapshot()
 *         .onUrisReady(batch -> {
 *             for ( String uri : batch.getItems() ) {
 *                 if ( uri.endsWith(".txt") ) {
 *                     client.newDocumentManager().delete(uri);
 *                 }
 *             }
 *         })
 *         .onQueryFailure(queryBatchException -> queryBatchException.printStackTrace());
 *     JobTicket ticket = dataMovementManager.startJob(qhb);
 *     qhb.awaitCompletion();
 *     dataMovementManager.stopJob(ticket);
 *}</pre>
 *
 *
 * <h2>Using WriteBatcher</h2>
 *
 * <p>When you need to write a very large volume of documents and
 * <a href="https://developer.marklogic.com/products/mlcp">mlcp</a>
 * cannot meet your requirements, use WriteBatcher.</p>
 *
 * For Example:
 * <pre>{@code
 *     WriteBatcher whb = dataMovementManager.newWriteBatcher()
 *         .withBatchSize(100)
 *         .withThreadCount(20)
 *         .onBatchSuccess(batch -> {
 *             logger.debug("batch # {}, so far: {}", batch.getJobBatchNumber(), batch.getJobResultsSoFar());
 *         })
 *         .onBatchFailure((batch,throwable) -> throwable.printStackTrace() );
 *     JobTicket ticket = dataMovementManager.startJob(whb);
 *     // the add or addAs methods could be called in separate threads on the
 *     // single whb instance
 *     whb.add  ("doc1.txt", new StringHandle("doc1 contents"));
 *     whb.addAs("doc2.txt", "doc2 contents");
 *
 *     whb.flushAndWait(); // send the two docs even though they're not a full batch
 *     dataMovementManager.stopJob(ticket);
 *}</pre>
 *
 *
 * <a name="lsnrs"></a>
 * <h2>Writing Custom Listeners</h2>
 *
 * <p>As demonstrated above, listeners should be added to each instance of
 * QueryBatcher or WriteBatcher.  Ad-hoc listeners can be written as Java 8
 * lambda expressions.  More sophisticated custom listeners can implement the
 * appropriate listener interface or extend one of the
 * <a href="#provided">provided listeners listed above</a>.</p>
 *
 * <p>QueryBatchListener (onUrisReady) instances are necessary to do something
 * with the uris fetched by QueryBatcher.  What a custom QueryBatchListener
 * does is completely up to it, but any operation which operates on uris
 * offered by any part of the Java Client API could be used, as could any read
 * or write to an external system.  QueryFailureListener (onQueryFailure)
 * instances handle any exceptions encoutnered fetching the uris.
 * WriteBatchListener (onBatchSuccess) instances handle any custom tracking
 * requirements during a WriteBatcher job.  WriteFailureListener
 * (onBatchFailure) instances handle any exceptions encountered writing the
 * batches formed from docs send to the WriteBatcher instance.  See the
 * javadocs for each <a href="#provided">provided listener</a> for an explantion of the
 * various listeners that can be registered for it to call.  See javadocs, the
 * <a href="http://docs.marklogic.com/guide/java">Java Application Developer's Guide</a>,
 * <a href="https://github.com/marklogic/java-client-api">source code for provided listeners</a>,
 * <a
 * href="https://github.com/marklogic/java-client-api/tree/master/examples/src/main/java/com/marklogic/client/example/cookbook/datamovement"
 * >cookbook examples</a>, and
 * <a
 * href="https://github.com/marklogic/java-client-api/tree/master/marklogic-client-api/src/test/java/com/marklogic/client/test/datamovement"
 * >unit tests</a>
 * for more examples of listener implementation ideas.</p>
 *
 *
 * <h2>Listners Must Be Thread-Safe</h2>
 *
 * <p>Since listeners are called asynchronously by all threads in the pool inside
 * the QueryBatcher or WriteBatcher instance, they must only perform
 * thread-safe operations.  For example, accumulating to a collection should
 * only be done with collections wrapped as
 * {@link java.util.Collections#synchronizedCollection synchronized Collections}
 * rather than directly using un-synchronized collections such as HashMap or
 * ArrayList which are not thread-safe.  Similarly, accumulating to a string
 * should use StringBuffer insted of StringBuilder since StringBuffer is
 * synchronized (and thus thread-safe).  We also recommend {@link
 * java.util.concurrent.atomic java.util.concurrent.atomic classes}.</p>
 *
 * <p>Listeners should handle their own exceptions as described below in
 * <a href="#errs">Handling Exceptions in Listeners</a>.</p>
 *
 *
 * <a name="errs"></a>
 * <h2>Handling Exceptions in Listeners</h2>
 *
 * Since listeners are called asynchrounously, external exception handling
 * cannot wrap the call in a try-catch block.  Instead, a listener can and
 * should handle its own exceptions by wrapping the calls in its body in a
 * try-catch block.  When any listener does not handle its own exceptions and
 * throws any exception (Throwable), the exception is logged at error level
 * with a call like:
 *
 * <pre>{@code
 *     logger.error("Exception thrown by an onBatchSuccess listener", throwable);
 *}</pre>
 *
 * <p>This achieves logging of exceptions without allowing them to prevent the job
 * from continuing.</p>
 *
 * <p>A QueryFailureListener or WriteFailureListener will not be notified of
 * exceptions thrown by other listeners.  Instead, these failure listeners are
 * notified exclusively of exceptions in the operation of QueryBatcher or
 * WriteBatcher.</p>
 *
 * <p>If you wish a custom QueryBatchListener or WriteBatchListener to trap its
 * own exceptions and pass them along to callbacks registered with it for
 * exception handling, it can of course do that in a custom way.  Examples of
 * this pattern can be seen in the interface of
 * {@link com.marklogic.client.datamovement.ApplyTransformListener}.</p>
 *
 * <h2>Pre-installed Listeners</h2>
 *
 * <p>Every time you create a new QueryBatcher or WriteBatcher it comes with some
 * pre-installed listeners such as
 * {@link com.marklogic.client.datamovement.HostAvailabilityListener} and a
 * listener to track counts for JobReport.  If you wish to remove these
 * listeners and their associated functionality call one of the following:
 * {@link com.marklogic.client.datamovement.QueryBatcher#setUrisReadyListeners
 * setUrisReadyListeners}, {@link
 * com.marklogic.client.datamovement.QueryBatcher#setQueryFailureListeners
 * setQueryFailureListeners}, {@link
 * com.marklogic.client.datamovement.WriteBatcher#setBatchSuccessListeners
 * setBatchSuccessListeners}, or {@link
 * com.marklogic.client.datamovement.WriteBatcher#setBatchFailureListeners
 * setBatchFailureListeners}.  Obviously, removing the functionality of
 * HostAvailabilityListener means it won't do its job of handling black-listing
 * hosts or retrying batches that occur when a host is unavailable.  And
 * removing the functionality of the listeners that track counts for JobReport
 * means JobReport should no longer be used.  If you would just like to change
 * the settings on HostAvailabilityListener or NoResponseListener, you can do
 * something like the following:</p>
 *
 * <pre>{@code
 *    HostAvailabilityListener.getInstance(batcher)
 *      .withSuspendTimeForHostUnavailable(Duration.ofMinutes(60))
 *      .withMinHosts(2);
 *}</pre>
 *
 *
 * <h2>Enable Logging</h2>
 *
 * <p>We have made efforts to provide helpful logging as you use QueryBatcher and
 * WriteBatcher.  Please make sure to enable your slf4j-compliant
 * <a href="../../../../overview-summary.html#logging">logging framework</a>.</p>
 *
 * <br><br><br><br><br><br><br><br><br><br><br><br><br><br>
 * <br><br><br><br><br><br><br><br><br><br><br><br><br><br>
 * <br><br><br><br><br><br><br><br><br><br><br><br><br><br>
 */
/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;
