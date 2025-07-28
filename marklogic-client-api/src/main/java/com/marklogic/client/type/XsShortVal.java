/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server short value.
 */
public interface XsShortVal extends XsIntVal, XsShortSeqVal, PlanParamBindingVal {
    public short getShort();
}
