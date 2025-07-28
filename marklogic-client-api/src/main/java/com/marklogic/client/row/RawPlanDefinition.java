/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
