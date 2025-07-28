/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server byte value.
 */
public interface XsByteVal extends XsShortVal, XsByteSeqVal, PlanParamBindingVal {
    public byte getByte();
}
