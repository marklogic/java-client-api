package com.marklogic.client.impl;

import java.io.PrintStream;

import com.marklogic.client.RequestLogger;

abstract class AbstractLoggingManager {
	protected RequestLogger requestLogger;

	AbstractLoggingManager() {
		super();
	}

	public void startLogging(RequestLogger logger) {
		requestLogger = logger;
	}
	public void stopLogging() {
		if (requestLogger == null) return;

		PrintStream out = requestLogger.getPrintStream();
		if (out != null) out.flush();

		requestLogger = null;
	}
	protected boolean isLoggerEnabled() {
		if (requestLogger != null)
			return requestLogger.isEnabled();

		return false;
	}
	protected PrintStream getLogger() {
		if (requestLogger == null)
			return null;

		return requestLogger.getPrintStream();
	}
}
