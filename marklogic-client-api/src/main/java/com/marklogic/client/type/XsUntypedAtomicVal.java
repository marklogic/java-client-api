/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * An instance of a server untyped atomic value.
 */
public interface XsUntypedAtomicVal extends XsAnyAtomicTypeVal, XsUntypedAtomicSeqVal, PlanParamBindingVal {
    public String getString();
}
