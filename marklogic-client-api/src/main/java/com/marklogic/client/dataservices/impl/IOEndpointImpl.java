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
import com.marklogic.client.dataservices.IOEndpoint;
import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.io.marker.BufferableHandle;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.function.Consumer;

abstract class IOEndpointImpl implements IOEndpoint {
    private DatabaseClient client;
    private IOCallerImpl   caller;

    public IOEndpointImpl(DatabaseClient client, IOCallerImpl caller) {
        if (client == null)
            throw new IllegalArgumentException("null client");
        if (caller == null)
            throw new IllegalArgumentException("null caller");
        this.client = client;
        this.caller = caller;
    }

    DatabaseClient getClient() {
        return this.client;
    }

    private IOCallerImpl getCaller() {
        return this.caller;
    }

    @Override
    public String getEndpointPath() {
        return getCaller().getEndpointPath();
    }
    @Override
    public boolean allowsEndpointState() {
        return (getCaller().getEndpointStateParamdef() != null);
    }
    @Override
    public boolean allowsWorkUnit() {
        return (getCaller().getWorkUnitParamdef() != null);
    }
    @Override
    public boolean allowsInput() {
        return (getCaller().getInputParamdef() != null);
    }

    boolean allowsSession() {
        return (getCaller().getSessionParamdef() != null);
    }

    static abstract class BulkIOEndpointCallerImpl implements IOEndpoint.BulkIOEndpointCaller {
        enum WorkPhase {
            INITIALIZING, RUNNING, INTERRUPTING, INTERRUPTED, COMPLETED;
        }

        private WorkPhase phase = WorkPhase.INITIALIZING;

        private IOEndpointImpl endpoint;
        private byte[]         endpointState;
        private byte[]         workUnit;

        BulkIOEndpointCallerImpl(IOEndpointImpl endpoint) {
            if (endpoint == null)
                throw new IllegalArgumentException("null endpoint definition");
            this.endpoint = endpoint;
        }

        private IOEndpointImpl getEndpoint() {
            return this.endpoint;
        }

        String getEndpointPath() {
            return getEndpoint().getEndpointPath();
        }

        boolean allowsEndpointState() {
            return getEndpoint().allowsEndpointState();
        }
        @Override
        public InputStream getEndpointState() {
            return (this.endpointState == null) ? null : new ByteArrayInputStream(this.endpointState);
        }
        @Override
        public void setEndpointState(byte[] endpointState) {
            if (allowsEndpointState())
                this.endpointState = endpointState;
            else if (endpointState != null)
                throw new IllegalArgumentException("endpoint state not accepted by endpoint: "+ getEndpointPath());
        }
        @Override
        public void setEndpointState(InputStream endpointState) {
            setEndpointState(NodeConverter.InputStreamToBytes(endpointState));
        }
        @Override
        public void setEndpointState(BufferableHandle endpointState) {
            setEndpointState((endpointState == null) ? null : endpointState.toBuffer());
        }

        boolean allowsWorkUnit() {
            return getEndpoint().allowsWorkUnit();
        }
        @Override
        public InputStream getWorkUnit() {
            return (this.workUnit == null) ? null : new ByteArrayInputStream(this.workUnit);
        }
        @Override
        public void setWorkUnit(byte[] workUnit) {
            if (allowsWorkUnit())
                this.workUnit = workUnit;
            else if (workUnit != null)
                throw new IllegalArgumentException("work unit not accepted by endpoint: "+ getEndpointPath());
        }
        @Override
        public void setWorkUnit(InputStream workUnit) {
            setWorkUnit(NodeConverter.InputStreamToBytes(workUnit));
        }
        @Override
        public void setWorkUnit(BufferableHandle workUnit) {
            setWorkUnit((workUnit == null) ? null : workUnit.toBuffer());
        }

        boolean allowsSession() {
            return getEndpoint().allowsSession();
        }

        boolean allowsInput() {
            return getEndpoint().allowsInput();
        }

        WorkPhase getPhase() {
            return this.phase;
        }
        void setPhase(WorkPhase phase) {
            this.phase = phase;
        }
        @Override
        public void interrupt() {
            if (this.phase == WorkPhase.RUNNING)
                setPhase(WorkPhase.INTERRUPTING);
        }
    }
}
