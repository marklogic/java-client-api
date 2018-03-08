package com.marklogic.client.datamovement;

/**
 * Runs processEvent on the QueryBatcher and it is a generic listener which can
 * be registered and run when you deal at the Batcher level. One such example is
 * it can be registered with onJobCompletion and it can be run after the Batcher
 * finishes its execution i.e. retrieve all the URIs matching the query and
 * execute all the listeners associated.
 *
 * 
 * @see #processEvent processEvent
 */
public interface QueryBatcherListener 
{
  /**
   * <p>The method called by QueryBatcher to run your
   * custom code at the QueryBatcher level.  
   * You usually implement this as a lambda expression.</p>
   *
   * For example, see the lambda expression passed to onJobCompletion:
   *
   * <pre>{@code
   *     QueryBatcher qhb = dataMovementManager.newQueryBatcher(query)
   *         .withBatchSize(1000)
   *         .withThreadCount(20)
   *         .onUrisReady(batch -> {
   *             for ( String uri : batch.getItems() ) {
   *                 if ( uri.endsWith(".txt") ) {
   *                     batch.getClient().newDocumentManager().delete(uri);
   *                 }
   *             }
   *         })
   *         .onQueryFailure(queryBatchException -> queryBatchException.printStackTrace())
   *         .onJobCompletion(batcher -> {
   *            JobReport report = new JobReportImpl(batcher);
   *            System.out.println("Success Batch count " + report.getSuccessBatchesCount());
   *         });
   *     JobTicket ticket = dataMovementManager.startJob(qhb);
   *     qhb.awaitCompletion();
   *     dataMovementManager.stopJob(ticket);
   *}</pre>
   *
   * @param batcher the QueryBatcher associated with the listener
   */
  void processEvent(QueryBatcher batcher);
}
