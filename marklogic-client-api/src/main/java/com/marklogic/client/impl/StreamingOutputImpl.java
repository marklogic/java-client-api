/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.impl.okhttp.RetryableRequestBody;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.util.RequestLogger;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

import java.io.IOException;
import java.io.OutputStream;

class StreamingOutputImpl extends RequestBody implements RetryableRequestBody {

	private OutputStreamSender handle;
	private RequestLogger logger;
	private MediaType contentType;
	private boolean resendable;

	/**
	 * Assumes the wrapped handle cannot be resent, which is the safe default for callers that have not determined
	 * whether the underlying content can be sent again (e.g. because it is backed by a one-shot stream).
	 */
	StreamingOutputImpl(OutputStreamSender handle, RequestLogger logger, MediaType contentType) {
		this(handle, logger, contentType, false);
	}

	/**
	 * @param resendable whether the wrapped handle's content can be written again if the request must be retried;
	 *                   should reflect the originating handle's own {@code isResendable()} value rather than
	 *                   always assuming the content is one-shot.
	 */
	StreamingOutputImpl(OutputStreamSender handle, RequestLogger logger, MediaType contentType, boolean resendable) {
		super();
		this.handle = handle;
		this.logger = logger;
		this.contentType = contentType;
		this.resendable = resendable;
	}

	@Override
	public MediaType contentType() {
		return contentType;
	}

	@Override
	public void writeTo(BufferedSink sink) throws IOException {
		OutputStream out = sink.outputStream();

		if (logger != null) {
			OutputStream tee = logger.getPrintStream();
			long max = logger.getContentMax();
			if (tee != null && max > 0) {
				handle.write(new OutputStreamTee(out, tee, max));

				return;
			}
		}

		handle.write(out);
	}

	@Override
	public boolean isRetryable() {
		// Added in 8.0.0. Originally always returned false, on the assumption that the wrapped stream is consumed
		// on first write. That is not true for handles whose content is fully buffered (e.g. JacksonHandle,
		// StringHandle) and which report isResendable() == true; for those, the content can safely be written
		// again, so we defer to the resendable flag supplied by the caller instead of hardcoding false.
		return resendable;
	}

	@Override
	public boolean isOneShot() {
		// Declares this body as one-shot via OkHttp's own contract so that when this body is nested inside another
		// RequestBody (e.g. a MultipartBody part for a Data Services call), OkHttp and RetryIOExceptionInterceptor
		// both recognize the outer body as non-retryable too; isRetryable() above is only checked when this is the
		// top-level request body and would otherwise be bypassed for a nested streaming part.
		return !resendable;
	}
}
