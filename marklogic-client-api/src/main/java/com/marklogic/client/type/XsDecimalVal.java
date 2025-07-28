/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

import java.math.BigDecimal;

/**
 * An instance of a server decimal value.
 */
public interface XsDecimalVal extends XsAnyAtomicTypeVal, XsNumericVal, XsDecimalSeqVal, PlanParamBindingVal {
    public BigDecimal getBigDecimal();
}
