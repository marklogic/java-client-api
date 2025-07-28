/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

// IMPORTANT: Do not edit. This file is generated.

/**
 * A sequence of semantic IRI values.
 * See {@link com.marklogic.client.expression.SemExpr#iriSeq(String...)}
 */
public interface SemIriSeqVal extends XsAnyURISeqVal {
    public SemIriVal[] getIriItems();
}
