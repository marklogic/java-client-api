/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * An instance of a server GYearMonth value.
 */
public interface XsGYearMonthVal extends XsAnyAtomicTypeVal, XsGYearMonthSeqVal, PlanParamBindingVal {
    public XMLGregorianCalendar getXMLGregorianCalendar();
}
