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
package com.marklogic.client.row;

import com.marklogic.client.io.marker.TextWriteHandle;

/**
 * A Raw Query DSL Plan provides access to a plan
 * expressed in JavaScript syntax.
 */
public interface RawQueryDSLPlan extends RawPlan {
    /**
     * Returns the handle for the text of the JavaScript representation of the Query DSL.
     * @return	the text handle for the JavaScript serialization
     */
    TextWriteHandle getHandle();
    /**
     * Specifies the handle for the text of the JavaScript representation of the Query DSL.
     * @param handle	the text handle for the JavaScript serialization
     */
    void setHandle(TextWriteHandle handle);
    /**
     * Assigns the handle and returns the raw plan as a convenience.
     * @param handle	the text handle for the JavaScript serialization
     * @return	this raw plan object
     */
    RawQueryDSLPlan withHandle(TextWriteHandle handle);
}
