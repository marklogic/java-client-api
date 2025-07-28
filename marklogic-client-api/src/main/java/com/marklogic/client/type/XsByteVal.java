/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server byte value.
 */
public interface XsByteVal extends XsShortVal, XsByteSeqVal, PlanParamBindingVal {
    public byte getByte();
}
