/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server unsigned long value.
 */
public interface XsUnsignedLongVal extends XsNonNegativeIntegerVal, XsUnsignedLongSeqVal, PlanParamBindingVal {
    public long getLong();
}
