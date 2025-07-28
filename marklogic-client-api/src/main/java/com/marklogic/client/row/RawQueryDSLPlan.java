/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
