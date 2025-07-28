/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.type;

/**
 * One or more server values.
 */
public interface ItemSeqVal extends ServerExpression {
    public ItemVal[] getItems();
}
