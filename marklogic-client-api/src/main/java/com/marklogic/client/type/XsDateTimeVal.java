/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

import java.util.Calendar;

/**
 * An instance of a server datetime value.
 */
public interface XsDateTimeVal extends XsAnyAtomicTypeVal, XsDateTimeSeqVal, PlanParamBindingVal {
    // follows JAXB rather than XQJ, which uses XMLGregorianCalendar
    public Calendar getCalendar();
}
