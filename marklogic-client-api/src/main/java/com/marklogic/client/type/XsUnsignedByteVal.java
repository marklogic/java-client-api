/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

// Java 8 introduced Integer and Long methods on unsigned ints and longs

/**
 * An instance of a server unsigned byte value.
 */
public interface XsUnsignedByteVal extends XsUnsignedShortVal, XsUnsignedByteSeqVal, PlanParamBindingVal {
    public byte getByte();
}
