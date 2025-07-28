/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server long value.
 */
public interface XsLongVal extends XsIntegerVal, XsLongSeqVal, PlanParamBindingVal {
    public long getLong();
}
