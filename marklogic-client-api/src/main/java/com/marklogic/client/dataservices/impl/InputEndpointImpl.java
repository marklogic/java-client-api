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
package com.marklogic.client.dataservices.impl;

import java.io.InputStream;
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
		newCallContext().withEndpointState(endpointState).withSessionState(session)
				.withWorkUnit(workUnit);
		return call(newCallContext(), input);
	}

	@Override
	@Deprecated
	public BulkInputCaller bulkCaller() {
		return bulkCaller(newCallContext());
	}

	@Override
	public InputStream call(CallContext callContext, InputStream[] input) {
		checkAllowedArgs(callContext.getEndpointState(), callContext.getSessionState(), callContext.getWorkUnit());
		return getCaller().arrayCall(getClient(), callContext.getEndpointState(), callContext.getSessionState(),
				callContext.getWorkUnit(), input);
	}

	@Override
	public BulkInputCaller bulkCaller(CallContext callContext) {
		return new BulkInputCallerImpl(this, getBatchSize(), newCallContext());
	}

	@Override
	public BulkInputCaller bulkCaller(CallContext[] callContexts) {
		return null;
	}

	@Override
	public BulkInputCaller bulkCaller(CallContext[] callContexts, int threadCount) {
		return null;
	}

	@Override
	public CallContext newCallContext() {
		return new CallContextImpl(this);
	}

	final static class BulkInputCallerImpl extends IOEndpointImpl.BulkIOEndpointCallerImpl
			implements InputEndpoint.BulkInputCaller {

		private InputEndpointImpl endpoint;
		private int batchSize;
		private LinkedBlockingQueue<InputStream> queue;
		public CallContext callContext;
		private ErrorListener errorListener;

		private BulkInputCallerImpl(InputEndpointImpl endpoint, int batchSize, CallContext callContext) {
			super(endpoint);
			this.endpoint = endpoint;
			this.batchSize = batchSize;
			this.queue = new LinkedBlockingQueue<InputStream>();
			this.callContext = callContext;
		}

		private InputEndpointImpl getEndpoint() {
			return endpoint;
		}
		private int getBatchSize() {
			return batchSize;
		}
		private LinkedBlockingQueue<InputStream> getQueue() {
			return queue;
		}

		@Override
		public void accept(InputStream input) {
			boolean hasBatch = queueInput(input, getQueue(), getBatchSize());
			if (hasBatch)
			    processInput();
		}
		@Override
		public void acceptAll(InputStream[] input) {
			boolean hasBatch = queueAllInput(input, getQueue(), getBatchSize());
			if (hasBatch)
				processInput();
		}

		@Override
		public void setErrorListener(ErrorListener errorListener) {
			this.errorListener = errorListener;
		}

		@Override
		public void awaitCompletion() {
			if (getQueue() == null)
				return;

			while (!getQueue().isEmpty()) {
				processInput();
			}
		}

		private void processInput() {
			logger.trace("input endpoint running endpoint={} count={} state={}", getEndpointPath(), getCallCount(),
					callContext.getEndpointState());

			InputStream output = null;
			try {
				output = getEndpoint().getCaller().arrayCall(
						getClient(), callContext.getEndpointState(), callContext.getSessionState(),
						callContext.getWorkUnit(), getInputBatch(getQueue(), getBatchSize())
				);
			} catch (Throwable throwable) {
				throw new RuntimeException("error while calling "+getEndpoint().getEndpointPath(), throwable);
			}

			incrementCallCount();

			if (allowsEndpointState()) {
				callContext.withEndpointState(output);
			}
		}

		static class ErrorListenerImpl implements BulkInputCaller.ErrorListener {

			@Override
			public BulkIOEndpointCaller.ErrorDisposition processError(int retryCount, Throwable throwable, CallContext callContext, InputStream[] input) {
				return null;
			}
		}
	}
}
