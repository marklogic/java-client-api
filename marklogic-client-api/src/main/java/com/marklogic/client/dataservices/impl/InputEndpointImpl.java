/*
 * Copyright (c) 2019 MarkLogic Corporation
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
package com.marklogic.client.dataservices.impl;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.SessionState;
import com.marklogic.client.dataservices.InputEndpoint;
import com.marklogic.client.io.marker.JSONWriteHandle;

public class InputEndpointImpl extends IOEndpointImpl implements InputEndpoint {
	private static Logger logger = LoggerFactory.getLogger(InputEndpointImpl.class);

	private InputCallerImpl caller;
	private int batchSize;

	public InputEndpointImpl(DatabaseClient client, JSONWriteHandle apiDecl) {
		this(client, new InputCallerImpl(apiDecl));
	}
	private InputEndpointImpl(DatabaseClient client, InputCallerImpl caller) {
		super(client, caller);
		this.caller = caller;
		this.batchSize = initBatchSize(caller);
	}

	private InputCallerImpl getCaller() {
		return this.caller;
	}
	private int getBatchSize() {
		return this.batchSize;
	}

	@Override
	public void call(InputStream[] input) {
		call(newCallContext(), input);
	}

	@Override
	@Deprecated
	public InputStream call(InputStream endpointState, SessionState session, InputStream workUnit, InputStream[] input) {
		CallContext callContext = newCallContext().withEndpointState(endpointState).withSessionState(session)
				.withWorkUnit(workUnit);
		call(callContext, input);
		return callContext.getEndpointState();
	}

	@Override
	@Deprecated
	public BulkInputCaller bulkCaller() {
		return bulkCaller(newCallContext());
	}

	@Override
	public void call(CallContext callContext, InputStream[] input) {
		checkAllowedArgs(callContext.getEndpointState(), callContext.getSessionState(), callContext.getWorkUnit());
		callContext.withEndpointState(getCaller().arrayCall(getClient(), callContext.getEndpointState(), callContext.getSessionState(),
				callContext.getWorkUnit(), input));
	}

	@Override
	public BulkInputCaller bulkCaller(CallContext callContext) {
		return new BulkInputCallerImpl(this, getBatchSize(), callContext);
	}

	@Override
	public BulkInputCaller bulkCaller(CallContext[] callContexts) {
		if(callContexts == null || callContexts.length==0)
			throw new IllegalArgumentException("CallContext cannot be null or empty");
		return bulkCaller(callContexts, callContexts.length);
	}

	@Override
	public BulkInputCaller bulkCaller(CallContext[] callContexts, int threadCount) {
		if(callContexts == null)
			throw new IllegalArgumentException("CallContext cannot be null.");
		if(threadCount > callContexts.length)
			throw new IllegalArgumentException("Thread count cannot be more than the callContext count.");

		switch(callContexts.length) {
			case 0: throw new IllegalArgumentException("CallContext cannot be empty");
			case 1: return new BulkInputCallerImpl(this, getBatchSize(), callContexts[0]);
			default: return new BulkInputCallerImpl(this, getBatchSize(), callContexts, threadCount);
		}
	}

	final static class BulkInputCallerImpl extends IOEndpointImpl.BulkIOEndpointCallerImpl
			implements InputEndpoint.BulkInputCaller {

		private InputEndpointImpl endpoint;
		private int batchSize;
		private LinkedBlockingQueue<InputStream> inputQueue;
		private ErrorListener errorListener;

		private BulkInputCallerImpl(InputEndpointImpl endpoint, int batchSize, CallContext callContext) {
			super(callContext);
			checkEndpoint(endpoint, "InputEndpointImpl");
			this.endpoint = endpoint;
			this.batchSize = batchSize;
			this.inputQueue = new LinkedBlockingQueue<>();
		}

		private BulkInputCallerImpl(InputEndpointImpl endpoint, int batchSize, CallContext[] callContexts, int threadCount) {
			super(callContexts, threadCount, (2*callContexts.length));
			checkEndpoint(endpoint, "InputEndpointImpl");
			this.endpoint = endpoint;
			this.batchSize = batchSize;
			this.inputQueue = new LinkedBlockingQueue<>();
		}

		private InputEndpointImpl getEndpoint() {
			return endpoint;
		}
		private int getBatchSize() {
			return batchSize;
		}
		private LinkedBlockingQueue<InputStream> getInputQueue() {
			return inputQueue;
		}

		@Override
		public void accept(InputStream input) {
			boolean hasBatch = queueInput(input, getInputQueue(), getBatchSize());
			if (hasBatch)
			    processInput();
		}
		@Override
		public void acceptAll(InputStream[] input) {
			boolean hasBatch = queueAllInput(input, getInputQueue(), getBatchSize());
			if (hasBatch)
				processInput();
		}

		@Override
		public void setErrorListener(ErrorListener errorListener) {
			this.errorListener = errorListener;
		}

		@Override
		public void awaitCompletion() {
			try {
				if (getInputQueue() == null)
					return;

				while (!getInputQueue().isEmpty()) {
					processInput();
				}
				if(getCallerThreadPoolExecutor() != null)
					getCallerThreadPoolExecutor().awaitTermination();
			} catch (Throwable throwable) {
				throw new RuntimeException("Error occurred while awaiting termination "+throwable.getMessage());
			}
		}
		private void processInput() {
			InputStream[] inputBatch = getInputBatch(getInputQueue(), getBatchSize());
			if(getCallContext()!=null)
				processInput(getCallContext(), inputBatch);
			// TODO : optimize the case of a single thread with a callContextQueue.
			else if(getCallContextQueue() != null && !getCallContextQueue().isEmpty()){
					BulkCallableImpl bulkCallableImpl = new BulkCallableImpl(this, inputBatch);
					submitTask(bulkCallableImpl);
			} else {
				throw new IllegalArgumentException("Cannot process input without Callcontext.");
			}
		}

		private void processInput(CallContext callContext, InputStream[] inputBatch) {
			logger.trace("input endpoint running endpoint={} count={} state={}", getEndpointPath(), getCallCount(),
					callContext.getEndpointState());

			InputStream output = null;
			try {
				output = getEndpoint().getCaller().arrayCall(
						getClient(), callContext.getEndpointState(), callContext.getSessionState(),
						callContext.getWorkUnit(), inputBatch
				);
			} catch (Throwable throwable) {
				throw new RuntimeException("error while calling "+getEndpoint().getEndpointPath(), throwable);
			}

			incrementCallCount();

			if (allowsEndpointState()) {
				callContext.withEndpointState(output);
			}
		}

		private class BulkCallableImpl implements Callable<Boolean> {
			private BulkInputCallerImpl bulkInputCallerImpl;
			private InputStream[] inputBatch;
			BulkCallableImpl(BulkInputCallerImpl bulkInputCallerImpl, InputStream[] inputBatch) {
				this.bulkInputCallerImpl = bulkInputCallerImpl;
				this.inputBatch = inputBatch;
			}
			@Override
			public Boolean call() {
				try {
					CallContext callContext = bulkInputCallerImpl.getCallContextQueue().poll();

					if(callContext != null) {
						bulkInputCallerImpl.processInput(callContext, inputBatch);
						bulkInputCallerImpl.getCallContextQueue().put(callContext);
					}
					else if(bulkInputCallerImpl.getCallContextQueue().isEmpty() &&
							getCallerThreadPoolExecutor().getActiveCount() == 0) {
						getCallerThreadPoolExecutor().shutdown();
					}
				} catch(Exception ex) {
					throw new InternalError("Error occurred while processing CallContext - "+ex.getMessage());
				}
				return true;
			}
		}
	}
}
