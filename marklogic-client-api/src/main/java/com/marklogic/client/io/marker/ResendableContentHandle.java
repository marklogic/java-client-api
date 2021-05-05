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
 * A Resendable Content Handle provides an adapter for a content
 * representation that can be read multiple times for purposes
 * such as resending input when retrying after a failed request.
 * @param <C> the handled content representation
 * @param <R> the serialization when reading the content
 */
public interface ResendableContentHandle<C, R> extends BufferableContentHandle<C, R> {
    @Override
    default BufferableContentHandle resendableHandleFor(C content) {
        return (BufferableContentHandle) newHandle(content);
    }
}
