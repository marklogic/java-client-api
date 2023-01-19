/*
 * Copyright (c) 2022 MarkLogic Corporation
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
package com.marklogic.client.extra.okhttpclient;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.impl.okhttp.OkHttpUtil;
import okhttp3.OkHttpClient;

/**
 * Exposes the mechanism for constructing an {@code OkHttpClient.Builder} in the same fashion as when a
 * {@code DatabaseClient} is constructed. Primarily intended for reuse in the ml-app-deployer library. If the
 * Java Client moves to a different HTTP client library, this will no longer work.
 *
 * @since 6.1.0
 */
public interface OkHttpClientBuilderFactory {

	static OkHttpClient.Builder newOkHttpClientBuilder(String host, int port, DatabaseClientFactory.SecurityContext securityContext) {
		return OkHttpUtil.newOkHttpClientBuilder(host, port, securityContext);
	}
}
