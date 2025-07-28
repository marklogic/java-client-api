/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server URI value.
 */
public interface XsAnyURIVal extends XsAnyAtomicTypeVal, XsAnyURISeqVal, PlanParamBindingVal {
    public String getString();
}
