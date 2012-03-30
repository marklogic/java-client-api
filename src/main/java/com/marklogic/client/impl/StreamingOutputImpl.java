package com.marklogic.client.impl;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.core.StreamingOutput;

import com.marklogic.client.RequestLogger;
import com.marklogic.client.io.OutputStreamSender;

class StreamingOutputImpl implements StreamingOutput {
	private OutputStreamSender handle;
	private RequestLogger      logger;

	StreamingOutputImpl(OutputStreamSender handle, RequestLogger logger) {
		super();
		this.handle = handle;
		this.logger = logger;
	}

	public void write(OutputStream out) throws IOException {
		if (logger != null) {
			OutputStream tee = logger.getPrintStream();
			long         max = logger.getContentMax();
			if (tee != null && max > 0) {
				handle.write(new OutputStreamTee(out, tee, max));

				return;
			}
		}

		handle.write(out);
	}
}
