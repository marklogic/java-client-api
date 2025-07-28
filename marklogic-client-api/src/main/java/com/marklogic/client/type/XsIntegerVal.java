/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

import java.math.BigInteger;

/**
 * An instance of a server integer value.
 */
public interface XsIntegerVal extends XsDecimalVal, XsIntegerSeqVal, PlanParamBindingVal {
    public BigInteger getBigInteger();
}
