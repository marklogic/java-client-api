/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.expression.PlanBuilder.ModifyPlan;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RawQueryDSLPlan;
import com.marklogic.client.row.RawSQLPlan;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.type.PlanCondition;
import com.marklogic.client.type.PlanExprColSeq;

import java.util.concurrent.TimeUnit;

/**
 * Coordinates threads to export all of the rows from a view in batches.
 *
 * <p>To construct a RowBatcher, use the
 * {@link DataMovementManager#newRowBatcher(ContentHandle) DataMovementManager.newRowBatcher()}
 * factory method. You pass a sample handle (that is, an adapter for the Java class that stores
 * a batch of retrieved rows). The sample handle must implement both the
 * {@link ContentHandle ContentHandle}
 * and
 * {@link com.marklogic.client.io.marker.StructureReadHandle StructuredReadHandle}
 * interfaces.  Because RowBatcher can retrieve rows in multiple formats, you must specify
 * the format or mime type on the sample handle if the handle can be used for multiple row formats.
 * The RowBatcher takes a generic type for the Java class adapted by the sample handle.
 * </p>
 *
 * <p>The following example constructs a RowBatcher for retrieving the rows in CSV format
 * as a Java String. The factory call passes a StringHandle configured with the appropriate
 * format and mime type:</p>
 *
 * <pre>{@code
 *    RowBatcher<String> rowBatcher = dataMovementMgr.newRowBatcher(
 *        new StringHandle().withFormat(Format.TEXT).withMimetype("text/csv")
 *        );
 *}</pre>
 *
 * <p>After constructing the RowBatcher, use the
 * {@link RowBatcher#getRowManager() getRowManager()}
 * method to get the RowManager for the rows. You can use the RowManager's
 * {@link RowManager#setDatatypeStyle(RowManager.RowSetPart) setDatatypeStyle()}
 * method to emit data types in the header and
 * {@link RowManager#setRowStructureStyle(RowManager.RowStructure) setRowStructureStyle()}
 * method to emit rows as objects or arrays. The RowManager's
 * {@link RowManager#newPlanBuilder() newPlanBuilder()}
 * method provides a factory for constructing a {@link PlanBuilder}.</p>
 *
 * <p>Use the RowManager's {@link PlanBuilder} to build a plan for retrieving the rows from a view.
 *The plan must have the following characteristics:</p>
 * <ul>
 * <li>Must start with a {@link PlanBuilder#fromView(String, String) fromView()} operation that
 * specifies the view with the rows to be exported.</li>
 * <li>May filter the exported rows with a
 * {@link ModifyPlan#where(PlanCondition) where()} operation
 * prior to any joins.</li>
 * <li>May project columns from the exported rows with a
 * {@link ModifyPlan#select(PlanExprColSeq) select()} operation
 * prior to any joins.</li>
 * <li>Must not limit, group, or sort over the exported view (either before or after any joins).</li>
 * <li>May join other views with the exported view, applying any operation to a joined view prior to the join.</li>
 * <li>May join documents or uris with the exported view.</li>
 * <li>Must not specify parameter placeholders.</li>
 * </ul>
 *
 * <p>Pass the built plan to the {@link RowBatcher#withBatchView(PlanBuilder.ModifyPlan) withBatchView()}
 * method to initialize the RowBatcher with the plan.</p>
 *
 * <p>Specify the number of threads for retrieving rows with the
 * {@link RowBatcher#withThreadCount(int) withThreadCount()}
 * method and the number of rows in a batch with the
 * {@link RowBatcher#withBatchSize(int) withBatchSize()}
 * method.</p>
 *
 * <p>Specify a success listener for processing each batch of rows retrieved from the server.
 * The RowBatcher passes a response event to the success listener.
 * The success listener calls the
 * {@link RowBatchSuccessListener.RowBatchResponseEvent#getRowsDoc() RowBatchResponseEvent.getRowsDoc()}
 * method to to get the rows in the batch. The rows are returned as an instance of the Java
 * class adapted by the sample handle passed to the factory that constructs the RowBatcher
 * (that is, the generic type of the RowBatcher).</p>
 *
 * <pre>{@code
 *    RowBatcher<String> rowBatcher = ...construct the row batcher...;
 *    rowBatcher.onSuccess(event -> {
 *        String rowBatch = event.getRowsDoc();
 *        ...process the batch of rows...
 *        });
 *}</pre>
 *
 * <p>Specify a failure listener to handle any errors during retrieval.</p>
 *
 * @param <T> the Java class that stores a batch of retrieved roles
 * @see <a href="https://github.com/marklogic/java-client-api/wiki/Row-Batcher">Exporting a TDE View</a>
 */
public interface RowBatcher<T> extends Batcher {
    /**
     * Gets the RowManager for retrieving rows.
     * @return the RowManager for retrieving rows
     */
    RowManager getRowManager();

    /**
     * Specifies the plan for getting rows from a view
     * with PlanBuilder.
     * @see RowManager#newPlanBuilder()
     * @see PlanBuilder#fromView(String, String)
     * @param viewPlan the PlanBuilder view providing the rows exported by the RowBatcher
     * @return the RowBatcher for chaining other initializations
     */
    RowBatcher<T> withBatchView(PlanBuilder.ModifyPlan viewPlan);
    /**
     * Specifies the plan for getting rows from a view
     * from a serialized AST in JSON format.
     * @see RowManager#newRawPlanDefinition(JSONWriteHandle)
     * @param viewPlan the raw AST for the rows exported by the RowBatcher
     * @return the RowBatcher for chaining other initializations
     */
    RowBatcher<T> withBatchView(RawPlanDefinition viewPlan);
    /**
     * Specifies the plan for getting rows from a view
     * from a serialized Query DSL in JavaScript format.
     * @see RowManager#newRawQueryDSLPlan(TextWriteHandle)
     * @param viewPlan the raw Query DSL for the rows exported by the RowBatcher
     * @return the RowBatcher for chaining other initializations
     */
    RowBatcher<T> withBatchView(RawQueryDSLPlan viewPlan);

    /**
     * Enables retrieval of rows that were  present in the view
     * at the time that the first batch was retrieved, ignoring
     * subsequent changes to the view.
     * @return the RowBatcher for chaining other initializations
     */
    RowBatcher<T> withConsistentSnapshot();

    /**
     * Supplies a callback function (typically, a lambda) for
     * processing the batch of rows. The callback receives a
     * {@link RowBatchSuccessListener.RowBatchResponseEvent RowBatchResponseEvent}
     * parameter and can call the event's
     * {@link RowBatchSuccessListener.RowBatchResponseEvent#getRowsDoc() getRowsDoc()}
     * method to get the rows as an instance of the Java class
     * adapted by the sample handle used to construct the RowBatcher.
     * @param listener  The callback function that receives the rows
     * @return the RowBatcher for chaining other initializations
     */
    RowBatcher<T> onSuccess(RowBatchSuccessListener<T> listener);
    /**
     * Supplies a callback function (typically, a lambda) for
     * logging and specifying the disposition of errors. The
     * callback receives a
     * {@link RowBatchFailureListener.RowBatchFailureEvent RowBatchFailureEvent}
     * parameter for inspecting the number of retries of the
     * error and overall failures and for setting the disposition
     * of the error.
     * @param listener The callback function that receives any errors
     * @return the RowBatcher for chaining other initializations
     */
    RowBatcher<T> onFailure(RowBatchFailureListener listener);

    /**
     * Specifies the number of rows in each batch retrieved from the view.
     * @param batchSize the number of rows in a batch
     * @return the RowBatcher for chaining other initializations
     */
    @Override
    RowBatcher<T> withBatchSize(int batchSize);
    /**
     * Specifies the forest configuration, which also identifies
     * the enodes for the cluster when not using a load balancer.
     * @param forestConfig the updated forest configuration
     * @return the RowBatcher for chaining other initializations
     */
    @Override
    RowBatcher<T> withForestConfig(ForestConfiguration forestConfig);
    /**
     * Specifies the identifier for the job executed by the RowBatcher.
     * @param jobId the unique id you would like to assign to this job
     * @return the RowBatcher for chaining other initializations
     */
    @Override
    RowBatcher<T> withJobId(String jobId);
    /**
     * Specifies the name for the job executed by the RowBatcher.
     * @param jobName the name you would like to assign to this job
     * @return the RowBatcher for chaining other initializations
     */
    @Override
    RowBatcher<T> withJobName(String jobName);
    /**
     * Specifies how many batches of rows to retrieve concurrently
     * from the view.
     * @param threadCount the number of threads to use in this Batcher
     * @return the RowBatcher for chaining other initializations
     */
    @Override
    RowBatcher<T> withThreadCount(int threadCount);

    /**
     * Gets the callback functions for successfully retrieved rows.
     * @return the success listeners
     */
    RowBatchSuccessListener<T>[] getSuccessListeners();
    /**
     * Gets the callback functions for errors.
     * @return the failure listeners
     */
    RowBatchFailureListener[] getFailureListeners();
    /**
     * Specifies the callback functions for successfully retrieved
     * rows when more than one callback function is needed.
     * @param listeners the success listeners
     */
    void setSuccessListeners(RowBatchSuccessListener<T>... listeners);
    /**
     * Specifies the callback functions for errors when more than
     * one callback function is needed.
     * @param listeners the failure listeners
     */
    void setFailureListeners(RowBatchFailureListener... listeners);

    /**
     * Suspends execution of the current thread until either all rows have been
     * retrieved from the view or the job is stopped.
     * @return true if all batches were processed
     */
    boolean awaitCompletion();
    /**
     * Suspends execution of the current thread until either all rows have been
     * retrieved from the view, the job is stopped, or a timeout expires.
     * @param timeout the amount for the timeout
     * @param unit the unit of measure for the amount
     * @return true if all batches were processed
     * @throws InterruptedException on interruption before the job finishes or timeout expires
     */
    boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * After the job is started, provides an estimate of the
     * total number of rows to be exported from the view.
     * To estimate progress, the row estimate can be compared
     * to the rows retrieved so far, which can be estimated with
     * {@link RowBatcher#getBatchCount() getBatchCount()} *
     * {@link Batcher#getBatchSize() getBatchSize()}
     * .
     * @return  the estimate of the view rows
     */
    long getRowEstimate();
    /**
     * The total number of batches of rows retrieved from the view.
     * @return the number of row batches
     */
    long getBatchCount();
    /**
     * The number of batches that the RowBatcher failed to retrieve
     * from the view.
     * @return the number of row batches
     */
    long getFailedBatches();

    /**
     * If {@code withConsistentSnapshot} was used before starting the job, will return the MarkLogic server timestamp
     * associated with the snapshot. Returns null otherwise.
     *
     * @return the timestamp or null
     */
    Long getServerTimestamp();
}
