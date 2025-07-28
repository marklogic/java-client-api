/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server int value.
 */
public interface XsIntVal extends XsLongVal, XsIntSeqVal, PlanParamBindingVal {
    public int getInt();
}
