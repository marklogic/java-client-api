/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * An instance of a server GMonth value.
 */
public interface XsGMonthVal extends XsAnyAtomicTypeVal, XsGMonthSeqVal, PlanParamBindingVal {
    public XMLGregorianCalendar getXMLGregorianCalendar();
}
