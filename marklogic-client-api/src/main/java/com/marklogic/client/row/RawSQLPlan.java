/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.row;

import com.marklogic.client.io.marker.TextWriteHandle;

/**
 * A Raw SQL Plan provides access to a plan
 * expressed as an SQL SELECT statement.  An SQL query can only
 * represent a subset of the capabilities of a plan.
 */
public interface RawSQLPlan extends RawPlan {
    /**
     * Returns the handle for the text of the SQL query.
     * @return	the text handle for the SQL query
     */
    TextWriteHandle getHandle();
    /**
     * Specifies the handle for the text of the SQL query.
     * @param handle	the text handle for the SQL query
     */
    void setHandle(TextWriteHandle handle);
    /**
     * Assigns the handle and returns the raw plan as a convenience.
     * @param handle	the text handle for the SQL query
     * @return	this raw plan object
     */
    RawSQLPlan withHandle(TextWriteHandle handle);
}
