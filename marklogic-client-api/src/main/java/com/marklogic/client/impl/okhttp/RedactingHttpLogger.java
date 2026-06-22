/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * An {@link HttpLoggingInterceptor.Logger} that redacts {@code Authorization}
 * and {@code x-auth-token} header values before writing each log message to
 * the configured output target (SLF4J logger, stderr, or stdout).
 *
 * <p><strong>Security warning:</strong> Even with header redaction,
 * {@code BODY}-level logging writes full HTTP request and response bodies to
 * the log target. This may include MarkLogic document content and must never
 * be enabled in production environments.
 */
public class RedactingHttpLogger implements HttpLoggingInterceptor.Logger {

	// Matches Authorization and x-auth-token header lines case-insensitively,
	// capturing the full value through end-of-line. Uses .* (not \S+) so that
	// multi-word values such as "Basic <token>", "Bearer <token>",
	// "Negotiate <token>", and "Digest username=..." are fully captured.
	// Java's . does not match \n, so adjacent lines are never affected.
	static final Pattern SENSITIVE_HEADER_REDACTION_PATTERN =
		Pattern.compile("(?i)(authorization|x-auth-token):.*");
	static final String SENSITIVE_HEADER_REDACTION_REPLACEMENT = "$1: [REDACTED]";

	private static final Logger logger = LoggerFactory.getLogger(RedactingHttpLogger.class);

	private final boolean useLogger;
	private final boolean useStdErr;

	public RedactingHttpLogger(boolean useLogger, boolean useStdErr) {
		this.useLogger = useLogger;
		this.useStdErr = useStdErr;
	}

	@Override
	public void log(String message) {
		String redacted = redactSensitiveHeaders(message);
		if (useLogger) {
			logger.debug(redacted);
		} else if (useStdErr) {
			System.err.println(redacted);
		} else {
			System.out.println(redacted);
		}
	}

	static String redactSensitiveHeaders(String message) {
		return SENSITIVE_HEADER_REDACTION_PATTERN.matcher(message)
			.replaceAll(SENSITIVE_HEADER_REDACTION_REPLACEMENT);
	}
}
