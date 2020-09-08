/*
 * Copyright (c) 2020 MarkLogic Corporation
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
import com.marklogic.client.SessionState;
import com.marklogic.client.dataservices.IOEndpoint;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.impl.Utilities;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.ContentHandle;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

class CallContextImpl<I,O> implements IOEndpoint.CallContext {
    private final IOEndpointImpl<I,O> endpoint;
    private final boolean legacyContext;

    private BytesHandle endpointState;
    private BytesHandle workUnit;
    private SessionState session;

    CallContextImpl(IOEndpointImpl<I,O> endpoint, boolean legacyContext) {
        this.endpoint = endpoint;
        this.legacyContext = legacyContext;
        if (endpoint.allowsEndpointState()) {
            endpointState = new BytesHandle().withFormat(endpoint.getEndpointStateParamdef().getFormat());
        }
        if (endpoint.allowsWorkUnit()) {
            workUnit = new BytesHandle().withFormat(endpoint.getWorkUnitParamdef().getFormat());
        }
    }

    IOEndpointImpl<I,O> getEndpoint() {
        return endpoint;
    }

    DatabaseClient getClient() {
        return getEndpoint().getClient();
    }

    boolean isLegacyContext() {
        return legacyContext;
    }

    @Override
    public BytesHandle getEndpointState() {
        return this.endpointState;
    }
    InputStream getEndpointStateAsInputStream() {
        byte[] bytes = this.endpointState.get();
        return (bytes == null || bytes.length == 0) ? null : new ByteArrayInputStream(bytes);
    }
    @Override
    public CallContextImpl<I,O> withEndpointState(BufferableHandle endpointState) {
        return assignBytes(this.endpointState, endpointState);
    }
    CallContextImpl<I,O> withEndpointStateAs(InputStream endpointState) {
        return assignBytes(this.endpointState, endpointState);
    }
    @Override
    public CallContextImpl<I,O> withEndpointStateAs(Object endpointState) {
        return assignBytes(this.endpointState, endpointState);
    }

    @Override
    public BytesHandle getWorkUnit() {
        return this.workUnit;
    }
    @Override
    public CallContextImpl<I,O> withWorkUnit(BufferableHandle workUnit) {
        return assignBytes(this.workUnit, workUnit);
    }
    CallContextImpl<I,O> withWorkUnitAs(InputStream workUnit) {
        return assignBytes(this.workUnit, workUnit);
    }
    @Override
    public CallContextImpl<I,O> withWorkUnitAs(Object workUnit) {
        return assignBytes(this.workUnit, workUnit);
    }

    @Override
    public SessionState getSessionState() {
        return this.session;
    }
    @Override
    public CallContextImpl<I,O> withSessionState(SessionState sessionState) {
        this.session = sessionState;
        return this;
    }

    private CallContextImpl<I,O> assignBytes(BytesHandle bytesHandle, byte[] bytes) {
        if (bytesHandle == null) {
            throw new IllegalArgumentException("cannot configure");
        }
        bytesHandle.set((bytes == null || bytes.length == 0) ? null : bytes);
        return this;
    }
    private CallContextImpl<I,O> assignBytes(BytesHandle bytesHandle, BufferableHandle bufferableHandle) {
        return assignBytes(bytesHandle, (bufferableHandle == null) ? null : bufferableHandle.toBuffer());
    }
    private CallContextImpl<I,O> assignBytes(BytesHandle bytesHandle, InputStream inputStream) {
        return assignBytes(bytesHandle, NodeConverter.InputStreamToBytes(inputStream));
    }
    private CallContextImpl<I,O> assignBytes(BytesHandle bytesHandle, Object content) {
        if (content == null) {
            return assignBytes(bytesHandle, (byte[]) null);
        } else if (content instanceof byte[]) {
            return assignBytes(bytesHandle, (byte[]) content);
        }

        Class<?> as = content.getClass();
        ContentHandle<?> handle =
                ((DatabaseClientImpl) getClient()).getHandleRegistry().makeHandle(as);

        Utilities.setHandleContent(handle, content);
        if (!(handle instanceof BufferableHandle)) {
            throw new IllegalArgumentException("content handle must be bufferable");
        }

        return assignBytes(bytesHandle, (BufferableHandle) handle);
    }
}
