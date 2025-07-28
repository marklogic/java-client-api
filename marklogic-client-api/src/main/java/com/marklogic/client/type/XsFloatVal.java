/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server float value.
 */
public interface XsFloatVal extends XsAnyAtomicTypeVal, XsNumericVal, XsFloatSeqVal, PlanParamBindingVal {
    public float getFloat();
}
