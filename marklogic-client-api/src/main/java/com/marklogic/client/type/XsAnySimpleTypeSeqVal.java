/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * A sequence of server simple values.
 */
public interface XsAnySimpleTypeSeqVal extends ItemSeqVal {
    public XsAnySimpleTypeVal[] getAnySimpleTypeItems();
}
