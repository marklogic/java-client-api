/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.util;

/**
 * Provides public constants for configuring MarkLogic Java Client logging behaviour.
 *
 * <h2>OkHttp Network Logging</h2>
 *
 * <p>The Java Client uses OkHttp for all HTTP communication with MarkLogic. Optional network
 * traffic logging is controlled by two Java system properties:
 *
 * <ul>
 *   <li>{@link #OKHTTP_NETWORK_LEVEL} — controls the verbosity of logged HTTP traffic.</li>
 *   <li>{@link #OKHTTP_NETWORK_OUTPUT} — controls where log lines are written.</li>
 * </ul>
 *
 * <p>When the output is set to {@code LOGGER}, log lines are written to the SLF4J logger
 * category {@link #OKHTTP_NETWORK_LOGGER} at {@code INFO} level. Configure this category
 * in your logging framework to enable network diagnostics without enabling debug logging
 * for unrelated client internals. For example, in {@code logback.xml}:
 *
 * <pre>{@code
 * <logger name="marklogic.okhttp.network" level="INFO"/>
 * }</pre>
 *
 * <p>Or in a Spring Boot {@code application.properties} file:
 *
 * <pre>{@code
 * logging.level.marklogic.okhttp.network=INFO
 * }</pre>
 *
 * <p><strong>Security warning:</strong> {@code Authorization} and {@code x-auth-token} header
 * values are automatically redacted from all log output. However, {@code HEADERS} and
 * {@code BODY} levels may expose other sensitive data including MarkLogic document content.
 * Never enable {@code HEADERS} or {@code BODY} in a production environment.
 *
 * @since 8.2.0
 */
public final class LoggingUtil {

	/**
	 * Name of the Java system property that controls the verbosity of OkHttp network logging.
	 * Value: {@code "marklogic.okhttp.network.level"}
	 *
	 * <p>Accepted values (case-insensitive): {@code BASIC}, {@code HEADERS}, {@code BODY},
	 * {@code NONE}. Setting this property to any recognised value activates the network
	 * logging interceptor.
	 *
	 * <p><strong>Security warning:</strong> {@code HEADERS} and {@code BODY} levels log HTTP
	 * request and response headers and/or bodies, which may contain MarkLogic credentials,
	 * OAuth/SAML tokens, or sensitive document content. These levels must <em>never</em> be
	 * enabled in production environments. {@code Authorization} and {@code x-auth-token}
	 * header values are automatically redacted, but body content is not.
	 */
	public static final String OKHTTP_NETWORK_LEVEL = "marklogic.okhttp.network.level";

	/**
	 * Name of the Java system property that controls where OkHttp network log lines are written.
	 * Value: {@code "marklogic.okhttp.network.output"}
	 *
	 * <p>Accepted values (case-insensitive):
	 * <ul>
	 *   <li>{@code LOGGER} — writes to the SLF4J logger category {@link #OKHTTP_NETWORK_LOGGER}
	 *       at {@code INFO} level.</li>
	 *   <li>{@code STDERR} — writes to {@code System.err}.</li>
	 *   <li>unset — writes to {@code System.out}.</li>
	 * </ul>
	 */
	public static final String OKHTTP_NETWORK_OUTPUT = "marklogic.okhttp.network.output";

	/**
	 * The SLF4J logger category used for OkHttp network traffic when
	 * {@link #OKHTTP_NETWORK_OUTPUT} is set to {@code LOGGER}.
	 * Value: {@code "marklogic.okhttp.network"}
	 *
	 * <p>Configure this category at {@code INFO} level in your logging framework to enable
	 * network traffic logging. Example {@code logback.xml} configuration:
	 * <pre>{@code
	 * <logger name="marklogic.okhttp.network" level="INFO"/>
	 * }</pre>
	 *
	 * <p>Example Spring Boot {@code application.properties}:
	 * <pre>{@code
	 * logging.level.marklogic.okhttp.network=INFO
	 * }</pre>
	 */
	public static final String OKHTTP_NETWORK_LOGGER = "marklogic.okhttp.network";

	private LoggingUtil() {
	}
}
