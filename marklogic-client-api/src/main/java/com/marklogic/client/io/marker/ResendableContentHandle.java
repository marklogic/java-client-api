/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io.marker;

/**
 * A Resendable Content Handle provides an adapter for a content
 * representation that can be read multiple times for purposes
 * such as resending input when retrying after a failed request.
 * @param <C> the handled content representation
 * @param <R> the serialization when reading the content
 */
public interface ResendableContentHandle<C, R> extends BufferableContentHandle<C, R> {
    @Override
    ResendableContentHandle<C, R> newHandle();
    @Override
    ResendableContentHandle<C, R>[] newHandleArray(int length);

    @Override
    default ResendableContentHandle<C, R> newHandle(C content) {
        ResendableContentHandle<C, R> handle = newHandle();
        handle.set(content);
        return handle;
    }

    @Override
    default ResendableContentHandle<C,R> resendableHandleFor(C content) {
        return newHandle(content);
    }
}
