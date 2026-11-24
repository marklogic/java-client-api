/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.util.RequestLogger;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

import java.io.IOException;
import java.io.OutputStream;

class StreamingOutputImpl extends RequestBody {

	private final OutputStreamSender handle;
	private final RequestLogger logger;
	private final MediaType contentType;
	private final boolean resendable;

	/**
	 * @param resendable the originating handle's {@code isResendable()}; if false, the content is assumed to be
	 *                   consumed by the first write and this body is reported to OkHttp as one-shot
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
	public boolean isOneShot() {
		// Using OkHttp's own contract means that a MultipartBody containing this body is also reported as one-shot, which
		// OkHttp and RetryIOExceptionInterceptor both honor.
		return !resendable;
	}
}
