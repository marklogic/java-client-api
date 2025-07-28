/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

// IMPORTANT: Do not edit. This file is generated.

/**
 * A sequence of server decimal values.
 * See {@link com.marklogic.client.expression.XsValue#decimalSeq(String...)}
 */
public interface XsDecimalSeqVal extends XsAnyAtomicTypeSeqVal, XsNumericSeqVal {
    public XsDecimalVal[] getDecimalItems();
}
