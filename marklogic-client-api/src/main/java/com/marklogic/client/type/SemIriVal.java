/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a semantic IRI value.
 */
public interface SemIriVal extends XsAnyURIVal, SemIriSeqVal, PlanParamBindingVal {
    public String getString();
}
