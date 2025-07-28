/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server hex binary value.
 */
public interface XsHexBinaryVal extends XsAnyAtomicTypeVal, XsHexBinarySeqVal {
    public byte[] getBytes();
}
