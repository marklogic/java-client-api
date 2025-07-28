/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server int value.
 */
public interface XsIntVal extends XsLongVal, XsIntSeqVal, PlanParamBindingVal {
    public int getInt();
}
