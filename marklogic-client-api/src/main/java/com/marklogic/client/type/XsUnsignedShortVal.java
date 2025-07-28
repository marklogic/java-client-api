/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server unsigned short value.
 */
public interface XsUnsignedShortVal extends XsUnsignedIntVal, XsUnsignedShortSeqVal, PlanParamBindingVal {
    public short getShort();
}
