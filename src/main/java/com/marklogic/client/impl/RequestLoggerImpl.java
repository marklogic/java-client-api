package com.marklogic.client.impl;

import java.io.OutputStream;
import java.io.PrintStream;

import com.marklogic.client.RequestLogger;

public class RequestLoggerImpl implements RequestLogger {
	private PrintStream out;
	private boolean     enabled = true;

	public RequestLoggerImpl(OutputStream out) {
		this.out = new PrintStream(out);
	}

	public PrintStream getPrintStream() {
		return out;
	}

	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		if (out == null && enabled == true)
			throw new RuntimeException("Cannot enable closed request logger");
		this.enabled = enabled;
	}

	public void close() {
		if (out == null) return;
		out.close();
		out = null;
		enabled = false;
	}

}
