/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.extra.okhttpclient;

import okhttp3.OkHttpClient;

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
public interface OkHttpClientConfigurator extends ClientConfigurator<OkHttpClient.Builder> {
}
