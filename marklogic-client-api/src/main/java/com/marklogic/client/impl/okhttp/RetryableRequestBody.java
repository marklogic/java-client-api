/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

/**
 * Interface for RequestBody implementations to signal whether they can be retried after an IOException.
 * This is used by RetryIOExceptionInterceptor to determine if a failed request can be retried.
 * Added in 8.0.0.
 *
 * @deprecated since 8.3.0; override {@link okhttp3.RequestBody#isOneShot()} instead, as it is also honored by OkHttp
 * itself and propagates through a {@code MultipartBody}. The request bodies in this library no longer implement this
 * interface. RetryIOExceptionInterceptor still honors it until it is removed.
 */
@Deprecated
public interface RetryableRequestBody {
	/**
	 * @return false if this request body cannot be retried (e.g., because it consumes a stream that can only be
	 * read once); true if it can be safely retried.
	 */
	boolean isRetryable();
}
