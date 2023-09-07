package com.marklogic.client;

import com.marklogic.client.impl.FailedRequest;

/**
 * Represents a "RESTAPI-CONTENTNOVERSION" error from the REST API that can occur when using optimistic locking.
 *
 * @since 6.3.0
 */
public class ContentNoVersionException extends FailedRequestException {

	public ContentNoVersionException(String message, FailedRequest failedRequest) {
		super(message, failedRequest);
	}

}
