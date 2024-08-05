/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.extra.httpclient;

import org.apache.http.client.HttpClient;

import com.marklogic.client.DatabaseClientFactory.ClientConfigurator;

/**
 * Provides configuration for the HttpClient communications library.
 * The configurator executes during creation of a database client
 * after the built-in configuration completes.  Use this class only
 * if testing demonstrates that your environment requires a different
 * HTTP configuration than the default HTTP configuration.
 *
 * Note:  If the API moves to a different HTTP communications library or
 * a different protocol, the configurator will no longer be called.
 *
 * @see com.marklogic.client.DatabaseClientFactory#addConfigurator(ClientConfigurator)
 */
public interface HttpClientConfigurator extends ClientConfigurator<HttpClient> {
}
