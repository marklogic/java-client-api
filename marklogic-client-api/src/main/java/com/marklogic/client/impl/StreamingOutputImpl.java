/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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

	StreamingOutputImpl(OutputStreamSender handle, RequestLogger logger, MediaType contentType) {
		super();
		this.handle = handle;
		this.logger = logger;
		this.contentType = contentType;
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
		// Added in 8.0.0; streaming output cannot be retried as the stream is consumed on first write.
		return false;
	}
}
