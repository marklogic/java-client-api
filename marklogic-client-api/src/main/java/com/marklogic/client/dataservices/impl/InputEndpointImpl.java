/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.dataservices.impl;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

import com.marklogic.client.dataservices.InputCaller;
import com.marklogic.client.io.marker.BufferableContentHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.SessionState;
import com.marklogic.client.io.marker.JSONWriteHandle;

public class InputEndpointImpl<I,O> extends IOEndpointImpl<I,O> implements InputCaller<I> {
	private static final Logger logger = LoggerFactory.getLogger(InputEndpointImpl.class);

	private final InputCallerImpl<I,O> caller;
	private final int batchSize;

	public InputEndpointImpl(
        DatabaseClient client, JSONWriteHandle apiDecl, HandleProvider<I,O> handleProvider
	) {
		this(client, new InputCallerImpl<>(apiDecl, handleProvider));
	}
	private InputEndpointImpl(DatabaseClient client, InputCallerImpl<I,O> caller) {
		super(client, caller);
		this.caller = caller;
		this.batchSize = initBatchSize(caller);
	}

	private InputCallerImpl<I,O> getCaller() {
		return this.caller;
	}
	private int getBatchSize() {
		return this.batchSize;
	}

	@Override
	public void call(I[] input) {
		call(newCallContext(), input);
	}
	@Override
	public void call(CallContext callContext, I[] input) {
		InputCallerImpl<I,O> callerImpl = getCaller();
		BufferableContentHandle<?,?>[] inputHandles = callerImpl.bufferableInputHandleOn(input);
		callerImpl.arrayCall(getClient(), checkAllowedArgs(callContext), inputHandles);
	}

	@Override
	public BulkInputCaller<I> bulkCaller() {
		return new BulkInputCallerImpl<>(this);
	}
	@Override
	public BulkInputCaller<I> bulkCaller(CallContext callContext) {
		return new BulkInputCallerImpl<>(this, getBatchSize(), checkAllowedArgs(callContext));
	}
	@Override
	public BulkInputCaller<I> bulkCaller(CallContext[] callContexts) {
		if(callContexts == null || callContexts.length==0)
			throw new IllegalArgumentException("CallContext cannot be null or empty");
		return bulkCaller(callContexts, callContexts.length);
	}
	@Override
	public BulkInputCaller<I> bulkCaller(CallContext[] callContexts, int threadCount) {
		if(callContexts == null)
			throw new IllegalArgumentException("CallContext cannot be null.");
		if(threadCount > callContexts.length)
			throw new IllegalArgumentException("Thread count cannot be more than the callContext count.");

		switch(callContexts.length) {
			case 0: throw new IllegalArgumentException("CallContext cannot be empty");
			case 1: return new BulkInputCallerImpl<>(this, getBatchSize(), checkAllowedArgs(callContexts[0]));
			default: return new BulkInputCallerImpl<>(this, getBatchSize(), checkAllowedArgs(callContexts), threadCount);
		}
	}

	public static class BulkInputCallerImpl<I,O> extends IOEndpointImpl.BulkIOEndpointCallerImpl<I,O>
			implements InputCaller.BulkInputCaller<I> {

		private final InputEndpointImpl<I,O> endpoint;
		private final int batchSize;
		private final LinkedBlockingQueue<I> inputQueue;
		private ErrorListener errorListener;

		public BulkInputCallerImpl(InputEndpointImpl<I,O> endpoint) {
			this(endpoint, endpoint.getBatchSize(), endpoint.checkAllowedArgs(endpoint.newCallContext()));
		}
		private BulkInputCallerImpl(InputEndpointImpl<I,O> endpoint, int batchSize, CallContextImpl<I,O> callContext) {
			super(endpoint, callContext);
			checkEndpoint(endpoint, "InputEndpointImpl");
			this.endpoint = endpoint;
			this.batchSize = batchSize;
			this.inputQueue = new LinkedBlockingQueue<>();
		}
		private BulkInputCallerImpl(
				InputEndpointImpl<I,O> endpoint, int batchSize, CallContextImpl<I,O>[] callContexts, int threadCount
		) {
			super(endpoint, callContexts, threadCount, (2*callContexts.length));
			this.endpoint = endpoint;
			this.batchSize = batchSize;
			this.inputQueue = new LinkedBlockingQueue<>();
		}

		private InputEndpointImpl<I,O> getEndpoint() {
			return endpoint;
		}
		private int getBatchSize() {
			return batchSize;
		}
		private LinkedBlockingQueue<I> getInputQueue() {
			return inputQueue;
		}

		@Override
		public void accept(I input) {
			boolean hasBatch = queueInput(input, getInputQueue(), getBatchSize());
			if (hasBatch)
			    processInput();
		}
		@Override
		public void acceptAll(I[] input) {
			boolean hasBatch = queueAllInput(input, getInputQueue(), getBatchSize());
			if (hasBatch)
				processInput();
		}

		private ErrorListener getErrorListener() {
			return this.errorListener;
		}

		@Override
		public void setErrorListener(ErrorListener errorListener) {
			this.errorListener = errorListener;
		}

		@Override
		public void awaitCompletion() {
			try {
				if (getInputQueue() != null) {
					while (!getInputQueue().isEmpty()) {
						processInput();
					}
				}

				// calling in concurrent threads
				if(getCallerThreadPoolExecutor() != null) {
					getCallerThreadPoolExecutor().shutdown();
					getCallerThreadPoolExecutor().awaitTermination();
				}
			} catch (Throwable throwable) {
				throw new RuntimeException("Error occurred while awaiting termination "+throwable.getMessage());
			}
		}
		private void processInput() {
			I[] inputBatch = getInputBatch(getInputQueue(), getBatchSize());
			if(getCallContext()!=null)
				processInput(getCallContext(), inputBatch);
			// TODO : optimize the case of a single thread with a callContextQueue.
			else if(getCallContextQueue() != null){
					BulkCallableImpl<I,O> bulkCallableImpl = new BulkCallableImpl<>(this, inputBatch);
					submitTask(bulkCallableImpl);
			} else {
				throw new IllegalArgumentException("Cannot process input without Callcontext.");
			}
		}

		private void processInput(CallContextImpl<I,O> callContext, I[] inputBatch) {
			logger.trace("input endpoint running endpoint={} count={} state={}", (callContext).getEndpoint().getEndpointPath(), getCallCount(),
					callContext.getEndpointState());
			InputCallerImpl<I,O> callerImpl = getEndpoint().getCaller();

			ErrorDisposition error = ErrorDisposition.RETRY;

			BufferableContentHandle<?,?>[] inputHandles = callerImpl.bufferableInputHandleOn(inputBatch);
			for (int retryCount = 0; retryCount < DEFAULT_MAX_RETRIES && error == ErrorDisposition.RETRY; retryCount++) {
				Throwable throwable = null;
				try {
					getEndpoint().getCaller().arrayCall(callContext.getClient(), callContext, inputHandles);
					incrementCallCount();
					return;
				} catch (Throwable catchedThrowable) {
					throwable = catchedThrowable;
				}

				if (throwable != null) {
					if (getErrorListener() == null) {
						logger.error("No error listener set. Stop all calls. " + getEndpoint().getEndpointPath(), throwable);
						error = ErrorDisposition.STOP_ALL_CALLS;
					} else {
						try {
							if (retryCount < DEFAULT_MAX_RETRIES - 1) {
								error = getErrorListener().processError(
										retryCount, throwable, callContext, inputHandles
								);
							} else {
								error = ErrorDisposition.SKIP_CALL;
							}

						} catch (Throwable throwable1) {
							logger.error("Error Listener failed with " , throwable1);
							error = ErrorDisposition.STOP_ALL_CALLS;
						}

						switch (error) {
							case RETRY:
								continue;

							case SKIP_CALL:
								return;

							case STOP_ALL_CALLS:
								if (getCallerThreadPoolExecutor() != null) {
									getCallerThreadPoolExecutor().shutdown();
								}
						}
					}
				}
			}
		}

		private static class BulkCallableImpl<I,O> implements Callable<Boolean> {
			private final BulkInputCallerImpl<I,O> bulkInputCallerImpl;
			private final I[] inputBatch;
			BulkCallableImpl(BulkInputCallerImpl<I,O> bulkInputCallerImpl, I[] inputBatch) {
				this.bulkInputCallerImpl = bulkInputCallerImpl;
				this.inputBatch = inputBatch;
			}
			@Override
			public Boolean call() {
				try {
					CallContextImpl<I,O> callContext = bulkInputCallerImpl.getCallContextQueue().take();

					bulkInputCallerImpl.processInput(callContext, inputBatch);
					bulkInputCallerImpl.getCallContextQueue().put(callContext);

				} catch(Throwable throwable) {
					throw new InternalError("Error occurred while processing CallContext - "+throwable.getMessage());
				}
				return true;
			}
		}
	}
}
