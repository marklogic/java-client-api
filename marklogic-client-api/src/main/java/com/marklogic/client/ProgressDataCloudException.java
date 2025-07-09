/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client;

/**
 * Added in 7.2.0 to more easily identify runtime exceptions pertaining to issues with connecting and/or
 * authenticating with ProgressDataCloud.
 *
 * @since 7.2.0
 */
public class ProgressDataCloudException extends RuntimeException {

	public ProgressDataCloudException(String message) {
		super(message);
	}

	public ProgressDataCloudException(String message, Throwable cause) {
		super(message, cause);
	}
}
