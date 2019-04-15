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

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

import com.marklogic.client.datamovement.Batcher;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.ForestConfiguration;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.dataservices.CallManager.CallArgs;
import com.marklogic.client.dataservices.CallManager.CallEvent;

/**
 * A CallBatcher executes multiple concurrent calls to a Data Service endpoint
 * for bulk ingest, egress, or reprocessing.
 *
 * You can pass the arguments for each call, pass the values to batch
 * for a parameter, or supply a generator function that produces the arguments
 * for each call.
 *
 * You register a CallSuccessListener to process results from successful calls
 * and a CallFailureListener to handle any failed calls.
 *
 * You can only call configuration methods before starting the job and
 * providing input.
 *
 * You construct a caller using CallManager and then call the batcher()
 * method on the caller to build a CallBatcher.
 * @param <W>  the type of the input passed to the add() method of the CallBatcher
 * @param <E>  the CallEvent subinterface for the results of a successful call
 */
public interface CallBatcher<W,E extends CallManager.CallEvent> extends Batcher {
    /**
     * Registers a lambda function for processing the CallEvent for a successful call.
     * @param listener  the lambda function for a successful result
     * @return  the CallBatcher for chaining with other configuration methods
     */
    CallBatcher<W,E> onCallSuccess(CallSuccessListener<E> listener);
    /**
     * Registers a lambda function for handling a failed call.  The function
     * receives the input arguments for the call and the exception
     * @param listener  the lambda function for a failure
     * @return  the CallBatcher for chaining with other configuration methods
     */
    CallBatcher<W,E> onCallFailure(CallFailureListener listener);
    /**
     * Specifies the size of the batch when passing input for a single
     * parameter. The batch size must be 1 or greater in this case.
     *
     * For all other input, the batch size must be exactly one.
     * @param batchSize the batch size
     * @return  the CallBatcher for chaining with other configuration methods
     */
    CallBatcher<W,E> withBatchSize(int batchSize);
    /**
     * Specifies the forests when partitioning work based on forests.
     * @param forestConfig the updated list of forests with thier hosts
     * @return  the CallBatcher for chaining with other configuration methods
     */
    CallBatcher<W,E> withForestConfig(ForestConfiguration forestConfig);
    /**
     * Assigns an identifier to the job that identifies a set of calls
     * to the Data Service endpoint.
     * @param jobId the unique id you would like to assign to this job
     * @return  the CallBatcher for chaining with other configuration methods
     */
    CallBatcher<W,E> withJobId(String jobId);
    /**
     * Assigns a name to the job that identifies a set of calls
     * to the Data Service endpoint.
     * @param jobName the name you would like to assign to this job
     * @return  the CallBatcher for chaining with other configuration methods
     */
    CallBatcher<W,E> withJobName(String jobName);
    /**
     * Specifies the number of concurrent calls to make
     * to the Data Service endpoint.
     * @param threadCount the number of threads to use
     * @return  the CallBatcher for chaining with other configuration methods
     */
    CallBatcher<W,E> withThreadCount(int threadCount);
    /**
     * Specifies default values for parameters when input for the parameters
     * is not otherwise provided.
     *
     * You use the args() method of the caller to construct arguments.
     *
     * Specifying defaults can be particularly useful when batching the values
     * for a single parameter.
     * @param args  one or more parameters for the caller
     * @return  the CallBatcher for chaining with other configuration methods
     */
    CallBatcher<W,E> withDefaultArgs(CallManager.CallArgs args);

    /**
     * Gets the listeners that process results for successful calls.
     * @return the listeners
     */
    CallSuccessListener<E>[] getCallSuccessListeners();
    /**
     * Gets the listeners that handle any failed calls.
     * @return the listeners
     */
    CallFailureListener[] getCallFailureListeners();

    /**
     * Gets the manager for reporting and other operations on the job.
     * @return  the manager for the job
     */
    DataMovementManager getDataMovementManager();
    /**
     * Starts the job.  Thereafter, the CallBatcher accepts input
     * and starts making concurrent calls in multiple threads.
     *
     * This method is a convenience for calling the equivalent
     * method on the DataMovementManager with the CallBatcher.
     * @return the ticket associated with this execution of the job
     */
    JobTicket startJob();
    /**
     * Stops the job.  Thereafter, the CallBatcher stops making
     * calls.  To finish all queued calls, execute flushAndWait()
     * before stopping the job.
     *
     * This method is a convenience for calling the equivalent
     * method on the DataMovementManager with the CallBatcher.
     */
    void stopJob();

    /**
     * Specifies all listeners that should receive result events
     * for successful calls.
     * @param listeners  the listeners
     */
    void setCallSuccessListeners(CallSuccessListener<E>... listeners);
    /**
     * Specifies all listeners that should receive error events
     * for any failed calls.
     * @param listeners
     */
    void setCallFailureListeners(CallFailureListener... listeners);

    /**
     * Input for calls to the endpoint.
     *
     * When the CallBatcher batches the values for one parameter,
     * the input is a value.
     *
     * When the CallBatcher takes complete arguments for calls,
     * the input is a CallArgs object constructed with the caller.
     * @param input  one input for a call
     * @return  the CallBatcher for chaining with other add() calls
     */
    CallBatcher<W,E> add(W input);
    /**
     * Input for calls to the endpoint.
     *
     * This method is a convenience that calls add() for each item
     * in the stream.
     * @param input  a stream of input items for calls
     */
    void addAll(Stream<W> input);

    /**
     * Suspends the current thread until queued calls have all
     * been tried.
     *
     * This method doesn't flush the value queue when batching
     * values for a parameter. Call flushAndWait() to flush
     * the value queue as well.
     *
     * The method returns true unless the wait timed out or was
     * interrupted.
     * @return  true if all calls were tried
     */
    boolean awaitCompletion();
    /**
     * Suspends the current thread until queued calls have all
     * been tried, specifying the length of the wai
     * @param timeout  the maximum timeout
     * @param unit  the unit of the timeout argument
     * @return  true if all calls were tried
     */
    boolean awaitCompletion(long timeout, TimeUnit unit);
    /**
     * Suspends the current thread until all input has been
     * processed.
     *
     * When batching values for a parameter, this method
     * flushes the value queue, making a call with a partial
     * batch if necessary and then waits until all queued calls
     * have finished.
     *
     * For other input approaches, this method is identifical
     * to awaitCompletion().
     */
    void flushAndWait();
    /**
     * Queues calls with pending input without waiting
     * for queued calls to execute.
     *
     * This method is useful only when batching values
     * for a parameter.
     */
    void flushAsync();

    /**
     * Gets the ticket assigned to the current job when the job
     * was started.
     * @return  the ticket
     */
    JobTicket getJobTicket();
    /**
     * confirms whether the job is still running or has stopped.
     * @return  whether the job is stopped
     */
    boolean isStopped();

    /**
     * Tries recover from a failed call to the Data Service endpoint
     * by making the call again with the same input.
     *
     * If the call fails, the event is not passed to the failure
     * listeners.
     * @param event  the event for the previous call as received by a failure listener
     */
    void retry(E event);
    /**
     * Tries recover from a failed call to the Data Service endpoint
     * by making the call again with the same input, notifying the
     * failure listeners if the retry fails.
     * @param event  the event for the previous call as received by a failure listener
     */
    void retryWithFailureListeners(E event);

    /**
     * Builds a CallBatcher by specifying whether to pass the arguments for each call,
     * pass the values to batch for a parameter, or execute a generator function
     * to produce the arguments for each call.
     *
     * You construct a caller for a Data Services endpoint using CallManager and
     * then call the batcher() method on the caller to use the CallBatcherBuilder.
     * @param <E>  the CallEvent subinterface for the results of a successful call
     */
    interface CallBatcherBuilder<E extends CallManager.CallEvent> {
        /**
         * Builds a CallBatcher that takes input values for a parameter, making
         * a call after accumulating a batch of values for the parameter.
         *
         * You pass the values to the add() method of the built CallBatcher.
         * @param paramName  the name of the parameter receiving the values
         * @param as  the class specifying the type of the input values
         * @param <W>  the type of the input values
         * @return  a CallBatcher that accepts the input and makes calls with the caller
         */
        <W> CallBatcher<W,E> forBatchedParam(String paramName, Class<W> as);
        /**
         * Builds a CallBatcher that takes input arguments, making a call
         * for each input item.
         *
         * You use the args() method of the caller to construct the arguments
         * to pass to the add() method of the built CallBatcher.
         * @return  a CallBatcher that accepts the input and makes calls with the caller
         */
        CallBatcher<CallManager.CallArgs,E> forArgs();
        
        CallBatcher<CallArgs,E> forArgsGenerator(CallArgsGenerator<E> generator);
    }
    
    @FunctionalInterface
    public interface CallArgsGenerator<E extends CallEvent> extends Function<E, CallArgs> {
    }
}
