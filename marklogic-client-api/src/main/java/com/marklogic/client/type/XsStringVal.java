/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server string value.
 */
public interface XsStringVal extends XsAnyAtomicTypeVal, XsStringSeqVal, PlanGroupConcatOption, PlanParamBindingVal {
    public String getString();
}
