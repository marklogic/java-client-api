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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.SessionState;
import com.marklogic.client.dataservices.ExecEndpoint;
import com.marklogic.client.io.marker.JSONWriteHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

final public class ExecEndpointImpl extends IOEndpointImpl implements ExecEndpoint {
    private static Logger logger = LoggerFactory.getLogger(ExecEndpointImpl.class);
    private ExecCallerImpl caller;

    public ExecEndpointImpl(DatabaseClient client, JSONWriteHandle apiDecl) {
        this(client, new ExecCallerImpl(apiDecl));
    }
    private ExecEndpointImpl(DatabaseClient client, ExecCallerImpl caller) {
        super(client, caller);
        this.caller = caller;
    }

    private ExecCallerImpl getCaller() {
        return this.caller;
    }

    @Override
    public InputStream call(InputStream endpointState, SessionState session, InputStream workUnit) {
        checkAllowedArgs(endpointState, session, workUnit);
        return getCaller().call(getClient(), endpointState, session, workUnit);
    }

    @Override
    public ExecEndpoint.BulkExecCaller bulkCaller() {
        return new BulkExecCallerImpl(this);
    }

    final static class BulkExecCallerImpl
            extends IOEndpointImpl.BulkIOEndpointCallerImpl
            implements ExecEndpoint.BulkExecCaller
    {
        private ExecEndpointImpl endpoint;

        private BulkExecCallerImpl(ExecEndpointImpl endpoint) {
            super(endpoint);
            this.endpoint = endpoint;
        }

        private ExecEndpointImpl getEndpoint() {
            return this.endpoint;
        }

        @Override
        public void awaitCompletion() {
            setPhase(WorkPhase.RUNNING);
            logger.trace("exec running endpoint={} work={}", getEndpointPath(), getWorkUnit());
            calling: while (true) {
                InputStream output = null;
                try {
                    logger.trace("exec calling endpoint={} count={} state={}",
                            getEndpointPath(), getCallCount(), getEndpointState());
// TODO: use byte[] for IO internally (and InputStream externally)
                    output = getEndpoint().getCaller().call(
                            getClient(), getEndpointState(), getSession(), getWorkUnit()
                    );
                    incrementCallCount();
                } catch(Throwable throwable) {
                    // TODO: logging
                    throw new RuntimeException("error while calling "+getEndpoint().getEndpointPath(), throwable);
                }
// TODO -- retry with new session if times out

                if (allowsEndpointState()) {
                    setEndpointState(output);
                }

                switch(getPhase()) {
                    case INTERRUPTING:
                        setPhase(WorkPhase.INTERRUPTED);
                        logger.info("exec interrupted endpoint={} count={} work={}",
                                getEndpointPath(), getCallCount(), getWorkUnit());
                        break calling;
                    case RUNNING:
                        if (output == null) {
                            setPhase(WorkPhase.COMPLETED);
                            logger.info("exec completed endpoint={} count={} work={}",
                                    getEndpointPath(), getCallCount(), getWorkUnit());
                            break calling;
                        }
                        break;
                    default:
                        throw new MarkLogicInternalException(
                                "unexpected state for "+getEndpointPath()+" during loop: "+getPhase().name()
                        );
                }
            }
        }

    }
}
