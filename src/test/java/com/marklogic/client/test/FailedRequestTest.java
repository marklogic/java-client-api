/*
 * Copyright 2012 MarkLogic Corporation
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.config.QueryOptions.Facets;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.QueryOptionsHandle;

public class FailedRequestTest {

	@Before 
	@After
	public void setXMLErrors() {
		
		Common.connectAdmin();
		ServerConfigurationManager serverConfig = Common.client.newServerConfigManager();
		serverConfig = Common.client.newServerConfigManager();

		serverConfig.setErrorFormat(Format.XML);
		serverConfig.writeConfiguration();

		serverConfig.readConfiguration();

		
		
	}
	
	
	@Test
	public void testFailedRequest() {
		Common.connect();
		QueryOptionsManager mgr = Common.client.newServerConfigManager()
				.newQueryOptionsManager();

		try {
			mgr.writeOptions("testempty", new QueryOptionsHandle());
		} catch (ForbiddenUserException e) {
			assertEquals(
					"Local message: User is not allowed to write /config/query. Server Message: You do not have permission to this method and URL",
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
				builder.constraint("blah", builder.collection("S", Facets.UNFACETED)),
				builder.constraint("blah", builder.collection("D", Facets.FACETED)));

		try {
			mgr.writeOptions("testempty", handle);
		} catch (FailedRequestException e) {
			assertEquals(
					"Local message: /config/query write failed: Bad Request. Server Message: RESTAPI-INVALIDCONTENT: (err:FOER0000) Invalid content: Operation results in invalid Options: Operator or constraint name \"blah\" is used more than once (must be unique).",
					e.getMessage());
			assertEquals(400, e.getFailedRequest().getStatusCode());
			assertEquals("Bad Request", e.getFailedRequest().getStatus());
			assertEquals("RESTAPI-INVALIDCONTENT", e.getFailedRequest().getMessageCode());
		}

	}
	
	@Test
	public void testJSONFailedRequest() {
		Common.connectAdmin();
		ServerConfigurationManager serverConfig = Common.client.newServerConfigManager();

		serverConfig.setErrorFormat(Format.JSON);
		serverConfig.writeConfiguration();

		serverConfig.readConfiguration();
		assertEquals(Format.JSON, serverConfig.getErrorFormat());
		
		try {
			serverConfig.setErrorFormat(Format.BINARY);
			fail("Error format cannot be binary");
		} 
		catch (IllegalArgumentException e) {
			//pass
		}
		
		testFailedRequest();
	
	}
}
