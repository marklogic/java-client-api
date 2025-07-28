/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server string value.
 */
public interface XsStringVal extends XsAnyAtomicTypeVal, XsStringSeqVal, PlanGroupConcatOption, PlanParamBindingVal {
    public String getString();
}
