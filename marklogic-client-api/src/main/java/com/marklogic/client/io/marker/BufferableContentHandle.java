/*
 * Copyright (c) 2019 MarkLogic Corporation
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
package com.marklogic.client.io.marker;

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

    /**
     * Provides a handle that can resend the content.
     * @param content the content
     * @return the resendable handle
     */
    BufferableContentHandle resendableHandleFor(C content);
    /**
     * Provides an array of handles that can resend an array of content.
     * @param content the array of content
     * @return the array of resendable handles
     */
    default BufferableContentHandle[] resendableHandleFor(C[] content) {
        if (content == null) return null;
        BufferableContentHandle[] result = new BufferableContentHandle[content.length];
        for (int i=0; i < content.length; i++) {
            result[i] = resendableHandleFor(content[i]);
        }
        return result;
    }

}
