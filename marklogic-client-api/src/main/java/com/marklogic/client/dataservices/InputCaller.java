/*
 * Copyright (c) 2021 MarkLogic Corporation
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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.dataservices.impl.InputEndpointImpl;
import com.marklogic.client.io.marker.BufferableContentHandle;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

/**
 * Provides an interface for calling an endpoint that takes input data structures.
 *
 * @param <I>  The representation for document input
 * @see <a href="https://github.com/marklogic/java-client-api/wiki/Data-Services-for-IO#input-endpoints">Input endpoints</a>
 */
public interface InputCaller<I> extends IOEndpoint {
	/**
	 * Constructs an instance of the InputCaller interface.
	 * @param client  the database client to use for making calls
	 * @param apiDecl  the JSON api declaration specifying how to call the endpoint
	 * @param inputHandle  the handle for the representation of the input content (such as StringHandle)
	 * @param <I>  the input content representation (such as String)
	 * @return  the InputCaller instance for calling the endpoint.
	 */
	static <I> InputCaller<I> on(DatabaseClient client, JSONWriteHandle apiDecl, BufferableContentHandle<I,?> inputHandle) {
		return new InputEndpointImpl(client, apiDecl, false, inputHandle);
	}

	/**
	 * Makes one call to an endpoint that doesn't take endpoint constants, endpoint state, or a session.
	 * @param input  the request data sent to the endpoint
	 */
	void call(I[] input);
	/**
	 * Makes one call to the endpoint for the instance and sets the endpoint state in the Call Context.
	 * @param callContext  the context consisting of the optional endpointConstants, endpointState, and session
	 * @param input  the request data sent to the endpoint
	 */
	void call(CallContext callContext, I[] input);

	/**
	 * Constructs an instance of a bulk caller, which completes
	 * a unit of work by repeated calls to the endpoint.
	 * @return  the bulk caller for the input endpoint
	 */
	BulkInputCaller<I> bulkCaller();
	/**
	 * Constructs an instance of a bulk caller, which completes
	 * a unit of work by repeated calls to the endpoint. The calls occur in the current thread.
	 * @param  callContext the context consisting of the optional endpointConstants, endpointState, and session
	 * @return  the bulk caller for the input endpoint
	 */
	BulkInputCaller<I> bulkCaller(CallContext callContext);
	/**
	 * Constructs an instance of a bulk caller, which completes
	 * a unit of work by repeated calls to the endpoint. The calls occur in worker threads.
	 * @param  callContexts the collection of callContexts
	 * @return  the bulk caller for the input endpoint
	 */
	BulkInputCaller<I> bulkCaller(CallContext[] callContexts);
	/**
	 * Constructs an instance of a bulk caller, which completes
	 * a unit of work by repeated calls to the endpoint. The calls occur in worker threads.
	 * @param  callContexts the collection of callContexts
	 * @param threadCount the number of threads
	 * @return  the bulk caller for the input endpoint
	 */
	BulkInputCaller<I> bulkCaller(CallContext[] callContexts, int threadCount);

	/**
	 * Provides an interface for completing a unit of work
	 * by repeated calls to the input endpoint.
	 *
	 * @param <I>  The representation for document input
	 * @see <a href="https://github.com/marklogic/java-client-api/wiki/Bulk-Data-Services">Bulk Data Services</a>
	 */
	interface BulkInputCaller<I> extends BulkIOEndpointCaller {
		/**
		 * Accepts an input item for the endpoint.  Items are queued
		 * and submitted to the endpoint in batches.
		 * @param input  one input item
		 */
		void accept(I input);
		/**
		 * Accepts multiple input items for the endpoint.  Items are queued
		 * and submitted to the endpoint in batches.
		 * @param input  multiple input items.
		 */
		void acceptAll(I[] input);

		/**
		 * Provides a callback that specifies the disposition of a failed call.
		 * @param errorListener the lambda or other implementation of the error listener
		 */
		void setErrorListener(ErrorListener errorListener);

		/**
		 * A function implementation that specifies the disposition of a failed call.
		 */
		interface ErrorListener {
			/**
			 * The signature for the lambda or other implementation of the callback that specifies
			 * the disposition of a failed call.
			 *
			 * The input is typed with the BufferableHandle marker interface.  The actual class
			 * of the handle is
			 * <ul>
			 * <li>the same as the input handle provided when constructing the InputOutputCaller
			 * if the content representation is resendable for retry (as with String)</li>
			 * <li>a BytesHandle if the content must be buffered for retry (as with InputStream)</li>
			 * </ul>
			 * @param retryCount  the number of times the call with this input has been retried
			 * @param throwable  the error received
			 * @param callContext  the context for the call
			 * @param input  the input for the call
			 * @return  whether to retry the call, skip the call, or stop the job
			 */
			ErrorDisposition processError(
					int retryCount, Throwable throwable, CallContext callContext, BufferableHandle[] input
			);
		}
	}
}
