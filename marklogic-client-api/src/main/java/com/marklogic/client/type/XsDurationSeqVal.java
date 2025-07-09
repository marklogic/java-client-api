/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * A sequence of server duration values.
 * See {@link com.marklogic.client.expression.XsValue#dayTimeDurationSeq(String...)} and
 * {@link com.marklogic.client.expression.XsValue#yearMonthDurationSeq(String...)}.
 */
public interface XsDurationSeqVal extends XsAnyAtomicTypeSeqVal {
    public XsDurationVal[] getDurationItems();
}
