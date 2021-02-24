/*
 * Copyright (c) 2019-2020 MarkLogic Corporation
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

import com.marklogic.client.io.marker.JSONWriteHandle;

/**
 * A Raw Plan Definition provides access to a plan
 * in a JSON serialization of the exported
 * AST (Abstract Syntax Tree) for the plan.
 */
public interface RawPlanDefinition extends RawPlan {
    /**
     * Returns the handle for the JSON representation of the AST for the plan.
     * @return	the JSON handle
     */
    JSONWriteHandle getHandle();
    /**
     * Specifies the handle for the JSON representation of the AST for the plan.
     * @param handle	the JSON handle
     */
    void setHandle(JSONWriteHandle handle);
    /**
     * Assigns the handle and returns the raw plan as a convenience.
     * @param handle	the JSON handle
     * @return	this raw plan object
     */
    RawPlanDefinition withHandle(JSONWriteHandle handle);
}
