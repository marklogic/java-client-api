/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.query;

import com.marklogic.client.io.marker.StructureReadHandle;

/** Access the extracted XML or JSON node using any StructureReadHandle
 * or class registered by a ContentHandle. */
public interface ExtractedItem {
    /** Get the item using the specified handle. */
    public <T extends StructureReadHandle> T get(T handle);
    /** Get the item using the handle registered for the specified class. */
    public <T> T getAs(Class<T> as);
}

