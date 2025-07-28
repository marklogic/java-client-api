/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server base 64 binary value.
 */
public interface XsBase64BinaryVal extends XsAnyAtomicTypeVal, XsBase64BinarySeqVal {
    public byte[] getBytes();
}
