/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.row;

import com.marklogic.client.io.marker.TextWriteHandle;

/**
 * A Raw SPARQL Select Plan provides access to a plan
 * expressed as a SPARQL SELECT statement.  A SPARQL query can only
 * represent a subset of the capabilities of a plan.
 */
public interface RawSPARQLSelectPlan extends RawPlan {
    /**
     * Returns the handle for the text of the SPARQL query.
     * @return	the text handle for the SPARQL query
     */
    TextWriteHandle getHandle();
    /**
     * Specifies the handle for the text of the SPARQL query.
     * @param handle	the text handle for the SPARQL query
     */
    void setHandle(TextWriteHandle handle);
    /**
     * Assigns the handle and returns the raw plan as a convenience.
     * @param handle	the text handle for the SPARQL query
     * @return	this raw plan object
     */
    RawSPARQLSelectPlan withHandle(TextWriteHandle handle);
}
