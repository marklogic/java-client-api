/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

import javax.xml.datatype.Duration;

/**
 * An instance of a server year-month duration value.
 */
public interface XsYearMonthDurationVal extends XsDurationVal, XsYearMonthDurationSeqVal, PlanParamBindingVal {
    public Duration getDuration();
}
