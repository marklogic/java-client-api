/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server unsigned int value.
 */
public interface XsUnsignedIntVal extends XsUnsignedLongVal, XsUnsignedIntSeqVal, PlanParamBindingVal {
    public int getInt();
}
