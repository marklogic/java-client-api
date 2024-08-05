/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io.marker;

import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.BytesHandle;

/**
 * A Streaming Content Handle provides an adapter for a streaming content
 * representation to make it possible to construct a bufferable content
 * representation so the content can be read multiple times for purposes
 * such as resending input when retrying after a
 * failed request.
 * @param <C> the handled content representation
 * @param <R> the serialization when reading the content
 */
public interface StreamingContentHandle<C, R> extends BufferableContentHandle<C, R> {
    @Override
    default BufferableContentHandle<?,?> resendableHandleFor(C content) {
        return new BytesHandle(contentToBytes(content))
                .withFormat(((BaseHandle<R,?>) this).getFormat());
    }
}
