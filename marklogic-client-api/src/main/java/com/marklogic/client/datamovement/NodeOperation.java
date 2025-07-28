/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.datamovement;

/**
 * Different operations to traverse the tree
 * DESCENT tells the application to go down the tree
 * SKIP tells the application to skip current branch
 * PROCESS tells the application to process current branch
 */
public enum NodeOperation {
    DESCEND, SKIP, PROCESS;
}
