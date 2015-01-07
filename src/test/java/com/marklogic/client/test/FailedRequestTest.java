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
package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.config.QueryOptions.Facets;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.QueryOptionsHandle;

@SuppressWarnings("deprecation")
public class FailedRequestTest {

	@Test
	public void testFailedRequest()
	throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException {
		Common.connect();
		//System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		QueryOptionsManager mgr = Common.client.newServerConfigManager()
				.newQueryOptionsManager();

		try {
			mgr.writeOptions("testempty", new QueryOptionsHandle());
		} catch (ForbiddenUserException e) {
			assertEquals(
					"Local message: User is not allowed to write /config/query. Server Message: You do not have permission to this method and URL.",
					e.getMessage());
			assertEquals(403, e.getFailedRequest().getStatusCode());
			assertEquals("Forbidden", e.getFailedRequest().getStatus());
		}
		Common.connectAdmin();
		mgr = Common.client.newServerConfigManager().newQueryOptionsManager();

		Common.client.newServerConfigManager().setQueryOptionValidation(true);

		QueryOptionsHandle handle;
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		// make an invalid options node
		handle = new QueryOptionsHandle().withConstraints(
				builder.constraint("blah",
						builder.collection("S", Facets.UNFACETED)),
				builder.constraint("blah",
						builder.collection("D", Facets.FACETED)));

		try {
			mgr.writeOptions("testempty", handle);
		} catch (FailedRequestException e) {
			assertEquals(
					"Local message: /config/query write failed: Bad Request. Server Message: RESTAPI-INVALIDCONTENT: (err:FOER0000) Invalid content: Operation results in invalid Options: Operator or constraint name \"blah\" is used more than once (must be unique).",
					e.getMessage());
			assertEquals(400, e.getFailedRequest().getStatusCode());
			assertEquals("Bad Request", e.getFailedRequest().getStatus());
			assertEquals("RESTAPI-INVALIDCONTENT", e.getFailedRequest()
					.getMessageCode());
		}

	}


	@Test
	public void testErrorOnNonREST()
	throws ForbiddenUserException {
		DatabaseClient badClient = DatabaseClientFactory.newClient(Common.HOST,
				8001, Common.USERNAME, Common.PASSWORD, Authentication.DIGEST);
		ServerConfigurationManager serverConfig = badClient
				.newServerConfigManager();

		try {
			serverConfig.readConfiguration();
		} catch (FailedRequestException e) {

		
			assertEquals(
					"Local message: config/properties read failed: Not Found. Server Message: Server (not a REST instance?) did not respond with an expected REST Error message.",
					e.getMessage());
			assertEquals(404, e.getFailedRequest().getStatusCode());
			assertEquals("UNKNOWN", e.getFailedRequest().getStatus());
		}

	}

}
