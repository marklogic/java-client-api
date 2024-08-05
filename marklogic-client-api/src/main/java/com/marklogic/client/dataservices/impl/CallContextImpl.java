/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
    private BytesHandle endpointConstants;
    private SessionState session;

    CallContextImpl(IOEndpointImpl<I,O> endpoint, boolean legacyContext) {
        this.endpoint = endpoint;
        this.legacyContext = legacyContext;
        if (endpoint.allowsEndpointState()) {
            endpointState = new BytesHandle().withFormat(endpoint.getEndpointStateParamdef().getFormat());
        }
        if (endpoint.allowsEndpointConstants()) {
            endpointConstants = new BytesHandle().withFormat(endpoint.getEndpointConstantsParamdef().getFormat());
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
    String getEndpointConstantsParamName() {
        return isLegacyContext() ? "workUnit" : "endpointConstants";
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
    public BytesHandle getEndpointConstants() {
        return this.endpointConstants;
    }
    @Override
    public CallContextImpl<I,O> withEndpointConstants(BufferableHandle constants) {
        return assignBytes(this.endpointConstants, constants);
    }
    CallContextImpl<I,O> withEndpointConstantsAs(InputStream contants) {
        return assignBytes(this.endpointConstants, contants);
    }
    @Override
    public CallContextImpl<I,O> withEndpointConstantsAs(Object constants) {
        return assignBytes(this.endpointConstants, constants);
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
