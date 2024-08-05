/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.dataservices.impl;

import com.marklogic.client.impl.RESTServices;
import com.marklogic.client.io.marker.BufferableContentHandle;

public interface HandleProvider<I, O> {
    BufferableContentHandle<?, ?> getInputHandle();
    BufferableContentHandle<?, ?> getOutputHandle();
    I[] newInputArray(int length);
    O[] newOutputArray(int length);
    BufferableContentHandle<?, ?>[] bufferableInputHandleOn(I[] input);
    O[] outputAsArray(CallContextImpl<I, O> callCtxt, RESTServices.MultipleCallResponse response);

    class ContentHandleProvider<I,O> implements HandleProvider<I,O> {
        private final BufferableContentHandle<I,?> inputHandle;
        private final BufferableContentHandle<O,?> outputHandle;
        public ContentHandleProvider(BufferableContentHandle<I,?> inputHandle, BufferableContentHandle<O,?> outputHandle) {
            this.inputHandle  = inputHandle;
            this.outputHandle = outputHandle;
        }
        @Override
        public BufferableContentHandle<I,?> getInputHandle() {
            return inputHandle;
        }
        @Override
        public BufferableContentHandle<O,?> getOutputHandle() {
            return outputHandle;
        }
        @Override
        public I[] newInputArray(int length) {
            if (inputHandle == null) {
                throw new IllegalStateException("No input handle provided");
            }
            return inputHandle.newArray(length);
        }
        @Override
        public O[] newOutputArray(int length) {
            if (outputHandle == null) {
                throw new IllegalStateException("No output handle provided");
            }
            return outputHandle.newArray(length);
        }
        @Override
        public BufferableContentHandle<?,?>[] bufferableInputHandleOn(I[] input) {
            if (inputHandle == null) {
                throw new IllegalStateException("No input handle provided");
            }
            return inputHandle.resendableHandleFor(input);
        }
        @Override
        public O[] outputAsArray(CallContextImpl<I,O> callCtxt, RESTServices.MultipleCallResponse response) {
            if (outputHandle == null) {
                throw new IllegalStateException("No output handle provided");
            }
            return response.asArrayOfContent(
                    callCtxt.isLegacyContext() ? null : callCtxt.getEndpointState(), outputHandle
            );
        }
    }

    class DirectHandleProvider<IC,IR,OC,OR>
            implements HandleProvider<BufferableContentHandle<IC,IR>, BufferableContentHandle<OC,OR>> {
        private final BufferableContentHandle<IC,IR> inputHandle;
        private final BufferableContentHandle<OC,OR> outputHandle;
        public DirectHandleProvider(BufferableContentHandle<IC,IR> inputHandle, BufferableContentHandle<OC,OR> outputHandle) {
            this.inputHandle  = inputHandle;
            this.outputHandle = outputHandle;
        }
        @Override
        public BufferableContentHandle<IC,IR> getInputHandle() {
            return inputHandle;
        }
        @Override
        public BufferableContentHandle<OC,OR> getOutputHandle() {
            return outputHandle;
        }
        @Override
        public BufferableContentHandle<IC,IR>[] newInputArray(int length) {
            if (inputHandle == null) {
                throw new IllegalStateException("No input handle provided");
            }
            return inputHandle.newHandleArray(length);
        }
        @Override
        public BufferableContentHandle<OC,OR>[] newOutputArray(int length) {
            if (outputHandle == null) {
                throw new IllegalStateException("No output handle provided");
            }
            return outputHandle.newHandleArray(length);
        }
        @Override
        public BufferableContentHandle<IC,IR>[] bufferableInputHandleOn(BufferableContentHandle<IC,IR>[] input) {
            return input;
        }
        @Override
        public BufferableContentHandle<OC,OR>[] outputAsArray(
                CallContextImpl<BufferableContentHandle<IC,IR>, BufferableContentHandle<OC,OR>> callCtxt,
                RESTServices.MultipleCallResponse response) {
            if (outputHandle == null) {
                throw new IllegalStateException("No output handle provided");
            }
            return response.asArrayOfHandles(callCtxt.getEndpointState(), outputHandle);
        }
    }
}
