/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client;

import com.marklogic.client.impl.FailedRequest;

/**
 * Represents a "RESTAPI-CONTENTWRONGVERSION" error from the REST API that can occur when using optimistic locking.
 *
 * @since 6.3.0
 */
public class ContentWrongVersionException extends FailedRequestException {

	public ContentWrongVersionException(String message, FailedRequest failedRequest) {
		super(message, failedRequest);
	}

}
