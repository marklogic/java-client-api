/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
