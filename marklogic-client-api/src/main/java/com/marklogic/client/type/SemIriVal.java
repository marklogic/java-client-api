/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a semantic IRI value.
 */
public interface SemIriVal extends XsAnyURIVal, SemIriSeqVal, PlanParamBindingVal {
    public String getString();
}
