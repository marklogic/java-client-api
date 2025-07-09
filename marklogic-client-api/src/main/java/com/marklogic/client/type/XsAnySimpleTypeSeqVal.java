/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * A sequence of server simple values.
 */
public interface XsAnySimpleTypeSeqVal extends ItemSeqVal {
    public XsAnySimpleTypeVal[] getAnySimpleTypeItems();
}
