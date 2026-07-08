/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RedactingHttpLogger}.
 * Covers header redaction ({@link RedactingHttpLogger#redactSensitiveHeaders(String)})
 * and LOGGER-mode output category and level behaviour.
 * No MarkLogic instance or network connectivity is required.
 */
class RedactingHttpLoggerTest {

	@Test
	void basicAuthHeaderIsRedacted() {
		String message = "Authorization: Basic dXNlcjpwYXNzd29yZA==";
		String result = RedactingHttpLogger.redactSensitiveHeaders(message);
		assertEquals("Authorization: [REDACTED]", result);
		assertFalse(result.contains("dXNlcjpwYXNzd29yZA=="),
			"Base64 credential must not appear in redacted output");
	}

	@Test
	void bearerTokenIsRedacted() {
		String message = "Authorization: Bearer eyJhbGciOiJSUzI1NiJ9.payload.signature";
		String result = RedactingHttpLogger.redactSensitiveHeaders(message);
		assertEquals("Authorization: [REDACTED]", result);
		assertFalse(result.contains("eyJhbGciOiJSUzI1NiJ9"),
			"Bearer token must not appear in redacted output");
	}

	@Test
	void negotiateKerberosTokenIsRedacted() {
		String message = "Authorization: Negotiate YIIGmgYGKwYBBQUCBg==";
		String result = RedactingHttpLogger.redactSensitiveHeaders(message);
		assertEquals("Authorization: [REDACTED]", result);
		assertFalse(result.contains("YIIGmgYGKwYBBQUCBg=="));
	}

	@Test
	void samlTokenIsRedacted() {
		String message = "Authorization: SAML token=abc123samlvalue";
		String result = RedactingHttpLogger.redactSensitiveHeaders(message);
		assertEquals("Authorization: [REDACTED]", result);
		assertFalse(result.contains("abc123samlvalue"));
	}

	@Test
	void digestAuthIsRedacted() {
		String message = "Authorization: Digest username=\"user\", realm=\"MarkLogic\", nonce=\"abc\", uri=\"/v1/documents\"";
		String result = RedactingHttpLogger.redactSensitiveHeaders(message);
		assertEquals("Authorization: [REDACTED]", result);
	}

	@Test
	void xAuthTokenHeaderIsRedacted() {
		String message = "x-auth-token: someSessionTokenValue";
		String result = RedactingHttpLogger.redactSensitiveHeaders(message);
		assertEquals("x-auth-token: [REDACTED]", result);
		assertFalse(result.contains("someSessionTokenValue"));
	}

	@Test
	void headerNameMatchIsCaseInsensitive() {
		String upper = "AUTHORIZATION: Basic dXNlcjpwYXNzd29yZA==";
		String mixed = "Authorization: Basic dXNlcjpwYXNzd29yZA==";
		assertEquals("AUTHORIZATION: [REDACTED]", RedactingHttpLogger.redactSensitiveHeaders(upper));
		assertEquals("Authorization: [REDACTED]", RedactingHttpLogger.redactSensitiveHeaders(mixed));
	}

	@Test
	void nonSensitiveHeadersAreNotRedacted() {
		String message = "Content-Type: application/json";
		assertEquals(message, RedactingHttpLogger.redactSensitiveHeaders(message));
	}

	@Test
	void multiLineMessageRedactsOnlyAuthLines() {
		String message = "--> POST http://localhost:8000/v1/documents\n" +
			"Content-Type: application/json\n" +
			"Authorization: Basic dXNlcjpwYXNzd29yZA==\n" +
			"Content-Length: 42";
		String result = RedactingHttpLogger.redactSensitiveHeaders(message);
		assertTrue(result.contains("Content-Type: application/json"));
		assertTrue(result.contains("Authorization: [REDACTED]"));
		assertFalse(result.contains("dXNlcjpwYXNzd29yZA=="));
		assertTrue(result.contains("Content-Length: 42"));
	}

	@Test
	void loggerModeWritesToNetworkLoggerCategoryAtInfoLevel() {
		ch.qos.logback.classic.Logger networkLogger =
			(ch.qos.logback.classic.Logger) LoggerFactory.getLogger(RedactingHttpLogger.NETWORK_LOGGER_NAME);
		Level originalLevel = networkLogger.getLevel();
		boolean originalAdditive = networkLogger.isAdditive();
		networkLogger.setAdditive(false);
		networkLogger.setLevel(Level.INFO);

		ListAppender<ILoggingEvent> appender = new ListAppender<>();
		appender.start();
		networkLogger.addAppender(appender);

		try {
			new RedactingHttpLogger(true, false).log("GET http://localhost:8000/v1/documents HTTP/1.1");

			assertEquals(1, appender.list.size(), "Expected exactly one log event");
			ILoggingEvent event = appender.list.get(0);

			assertEquals(RedactingHttpLogger.NETWORK_LOGGER_NAME, event.getLoggerName(),
				"Log event must be published under " + RedactingHttpLogger.NETWORK_LOGGER_NAME);
			assertEquals(Level.INFO, event.getLevel(),
				"LOGGER-mode output must be emitted at INFO level");
		} finally {
			appender.stop();
			networkLogger.detachAppender(appender);
			networkLogger.setLevel(originalLevel);
			networkLogger.setAdditive(originalAdditive);
		}
	}

	@Test
	void loggerModeDoesNotRequireDebugOnImplPackage() {
		ch.qos.logback.classic.Logger implLogger =
			(ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.marklogic.client.impl.okhttp");
		Level originalImplLevel = implLogger.getLevel();
		implLogger.setLevel(Level.WARN);

		ch.qos.logback.classic.Logger networkLogger =
			(ch.qos.logback.classic.Logger) LoggerFactory.getLogger(RedactingHttpLogger.NETWORK_LOGGER_NAME);
		Level originalNetworkLevel = networkLogger.getLevel();
		networkLogger.setLevel(Level.INFO);

		ListAppender<ILoggingEvent> appender = new ListAppender<>();
		appender.start();
		networkLogger.addAppender(appender);

		try {
			new RedactingHttpLogger(true, false).log("GET http://localhost:8000/v1/documents HTTP/1.1");

			assertEquals(1, appender.list.size(),
				"LOGGER-mode output must be visible without enabling DEBUG on com.marklogic.client.impl.*");
		} finally {
			appender.stop();
			networkLogger.detachAppender(appender);
			networkLogger.setLevel(originalNetworkLevel);
			implLogger.setLevel(originalImplLevel);
		}
	}
}
