package com.marklogic.client.example.cookbook.datamovement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.datamovement.Batch;
import com.marklogic.client.datamovement.BatchFailureListener;
import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.datamovement.QueryBatchListener;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;

/**
 * Takes in a Function which takes QueryBatch as argument and converts it into a
 * Plan and then iterates the row set returned by the constructed Plan and sends 
 * each RowRecord to any listeners registered with {@link #onRowRecordReady
 * onRowRecordReady} for further processing or writing to any target supported
 * by Java
 *
 * For example:
 *
 *     Function&lt;QueryBatch, PlanBuilder.Plan&gt; fn = batch -&gt; {
 *        PlanBuilder.Plan plan = convertToOpticPlan(batch); 
 *        return plan;
 *     }
 *     
 *     where "convertToOpticPlan" is your custom code which converts the QueryBatch 
 *     into Optic plan from which records can be retrieved.
 *     
 *     RowManager rowMgr = client.newRowManager()
 *     QueryBatcher exportOpticBatcher = moveMgr.newQueryBatcher(query)
 *         .onUrisReady(
 *           new OpticExportListener(fn, rowMgr)
 *               .onRowRecordReady(record -&gt; {
 *                 logger.debug(record.toString());
 *               })
 *         )
 *         .onQueryFailure(exception -&gt; exception.printStackTrace());
 *
 *     JobTicket ticket = moveMgr.startJob(exportBatcher);
 *     exportBatcher.awaitCompletion();
 *     moveMgr.stopJob(ticket);
 */

public class OpticExportListener implements QueryBatchListener {
  private static Logger logger = LoggerFactory.getLogger(OpticExportListener.class);
  protected Function<QueryBatch, PlanBuilder.Plan> exportFunction;
  protected RowManager rowManager;
  private List<Consumer<RowRecord>> opticExportListeners = new ArrayList<>();
  private List<BatchFailureListener<Batch<String>>> opticFailureListeners = new ArrayList<>();

  public OpticExportListener(Function<QueryBatch, PlanBuilder.Plan> function, RowManager rowManager) {
    this.exportFunction = function;
    this.rowManager = rowManager;
  }

  /**
   * This is the method QueryBatcher calls for OpticExportListener to do its
   * thing. You should not need to call it.
   *
   * @param batch the batch of uris and some metadata about the current status
   *          of the job
   */
  @Override
  public void processEvent(QueryBatch batch) {
    try {
      PlanBuilder.Plan exportPlan = exportFunction.apply(batch);
      for (RowRecord record : rowManager.resultRows(exportPlan)) {
        for (Consumer<RowRecord> listener : opticExportListeners) {
          try {
            listener.accept(record);
          } catch (Throwable t) {
            logger.error("Exception thrown by an onRowRecordReady listener ", t);
          }
        }
      }
    } catch (Throwable t) {
      for (BatchFailureListener<Batch<String>> listener : opticFailureListeners) {
        try {
          listener.processFailure(batch, t);
        } catch (Throwable t2) {
          logger.error("Exception thrown by an onBatchFailure listener", t2);
        }
      }
    }
  }

  /**
   * Adds a listener to process each retrieved RowRecord, which is the way users
   * of OpticExportListener can provide custom code to export records from the
   * constructed Optic Plan.
   *
   * @param listener the code which will process each RowRecord of the Optic API
   * @return this instance for method chaining
   *
   * @see Consumer
   * @see RowRecord
   */
  public OpticExportListener onRowRecordReady(Consumer<RowRecord> listener) {
    opticExportListeners.add(listener);
    return this;
  }

  /**
   * When a batch fails or a callback throws an Exception, run this listener
   * code. Multiple listeners can be registered with this method.
   *
   * @param listener the code to run when a failure occurs
   * @return this instance for method chaining
   */
  public OpticExportListener onBatchFailure(BatchFailureListener<Batch<String>> listener) {
    opticFailureListeners.add(listener);
    return this;
  }

  protected List<BatchFailureListener<Batch<String>>> getFailureListeners() {
    return opticFailureListeners;
  }
}