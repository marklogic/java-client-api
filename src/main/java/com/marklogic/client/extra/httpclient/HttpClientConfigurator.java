/*
 * Copyright 2012-2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
