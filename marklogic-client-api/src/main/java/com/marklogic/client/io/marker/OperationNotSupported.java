/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.io.marker;

/**
 * A handle uses OperationNotSupported as a read or write interface
 * when the handle does not support that operation.  For instance,
 * {@link com.marklogic.client.io.SearchHandle} uses OperationNotSupported
 * as its write interface because because a SearchHandle can be used only
 * to read search results and not to write anything to the database.
 */
public class OperationNotSupported {
  private OperationNotSupported() {
  }
}
