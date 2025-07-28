/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io.marker;

import java.lang.reflect.Array;

/**
 * A Bufferable Content Handle provides an adapter for a content
 * representation that can be read multiple times for purposes
 * such as resending input when retrying after a failed request.
 * @param <C> the handled content representation
 * @param <R> the serialization when reading the content from a response
 */
public interface BufferableContentHandle<C, R> extends BufferableHandle, ContentHandle<C> {
    /**
     * Converts the serialization to the content representation.
     * @param serialization a serialization of the content
     * @return the content representation
     */
    C toContent(R serialization);

    /**
     * Converts a byte serialization to the content representation.
     * @param buffer  the byte serialization
     * @return  the content representation
     */
    C bytesToContent(byte[] buffer);
    /**
     * Converts the content representation to bytes.
     * @param content the content
     * @return  the byte serialization of the content
     */
    byte[] contentToBytes(C content);

    @Override
    BufferableContentHandle<C, R> newHandle();

    @Override
    default BufferableContentHandle<C, R> newHandle(C content) {
        BufferableContentHandle<C, R> handle = newHandle();
        handle.set(content);
        return handle;
    }

    /**
     * Constructs an uninitialized array with the specified length with items
     * of the same content representation.
     * @param length the number of positions in the array
     * @return the uninitialized array
     */
    @SuppressWarnings("unchecked")
    default BufferableContentHandle<C,R>[] newHandleArray(int length) {
        if (length < 0) throw new IllegalArgumentException("handle array length less than zero: "+length);
        return (BufferableContentHandle<C,R>[]) Array.newInstance(this.getClass(), length);
    }

    /**
     * Provides a handle that can resend the content.
     * @param content the content
     * @return the resendable handle
     */
    BufferableContentHandle<?,?> resendableHandleFor(C content);

    /**
     * Provides an array of handles that can resend an array of content.
     * @param content the array of content
     * @return the array of resendable handles
     */
    default BufferableContentHandle<?,?>[] resendableHandleFor(C[] content) {
        if (content == null) return null;
        BufferableContentHandle<?,?>[] result = new BufferableContentHandle<?,?>[content.length];
        for (int i=0; i < content.length; i++) {
            result[i] = resendableHandleFor(content[i]);
        }
        return result;
    }
}
