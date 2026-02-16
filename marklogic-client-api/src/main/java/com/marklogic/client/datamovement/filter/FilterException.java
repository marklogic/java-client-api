/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.filter;

import com.marklogic.client.datamovement.DataMovementException;

/**
 * Any exception thrown by execution of a {@code DocumentWriteSetFilter} will be wrapped in this exception and
 * rethrown by the {@code WriteBatcher}, allowing failure listeners to distinguish filter exceptions from other
 * exceptions that may occur during batch processing.
 */
public class FilterException extends DataMovementException {

	public FilterException(String message, Throwable cause) {
		super(message, cause);
	}
}
