/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

import java.math.BigInteger;

/**
 * An instance of a server integer value.
 */
public interface XsIntegerVal extends XsDecimalVal, XsIntegerSeqVal, PlanParamBindingVal {
    public BigInteger getBigInteger();
}
