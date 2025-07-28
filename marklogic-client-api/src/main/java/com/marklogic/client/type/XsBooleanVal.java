/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server boolean value.
 */
public interface XsBooleanVal extends XsAnyAtomicTypeVal, XsBooleanSeqVal, PlanParamBindingVal {
    public boolean getBoolean();
}
