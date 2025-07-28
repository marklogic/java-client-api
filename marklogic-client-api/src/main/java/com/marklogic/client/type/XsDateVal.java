/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

import java.util.Calendar;

/**
 * An instance of a server date value.
 */
public interface XsDateVal extends XsAnyAtomicTypeVal, XsDateSeqVal, PlanParamBindingVal {
    // follows JAXB rather than XQJ, which uses XMLGregorianCalendar
    public Calendar getCalendar();
}
