/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * One or more server values.
 */
public interface ItemSeqVal extends ServerExpression {
    public ItemVal[] getItems();
}
