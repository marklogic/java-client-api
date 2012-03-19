package com.marklogic.client.impl;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import com.marklogic.client.io.marker.OutputStreamSender;

class StreamingOutputImpl implements StreamingOutput {
	private OutputStreamSender handle;

	StreamingOutputImpl(OutputStreamSender handle) {
		super();
		this.handle = handle;
	}

	public void write(OutputStream out) throws IOException, WebApplicationException {
		handle.write(out);
	}

}
