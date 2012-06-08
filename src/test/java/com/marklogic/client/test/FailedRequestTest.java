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

import org.junit.Test;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.config.QueryOptionsBuilder;
import com.marklogic.client.io.QueryOptionsHandle;

public class FailedRequestTest {

	@Test
	public void testFailedRequest() {
		Common.connect();
		QueryOptionsManager mgr = Common.client.newServerConfigManager()
				.newQueryOptionsManager();

		try {
			mgr.writeOptions("testempty", new QueryOptionsHandle());
		} catch (ForbiddenUserException e) {
			assertEquals(
					"Local message: User is not allowed to write /config/queryServer Message: You do not have permission to this method and URL",
					e.getMessage());
			assertEquals(403, e.getFailedRequest().getStatusCode());
			assertEquals("Forbidden", e.getFailedRequest().getStatus());
		}
		Common.connectAdmin();
		mgr = Common.client.newServerConfigManager().newQueryOptionsManager();

		Common.client.newServerConfigManager().setQueryOptionValidation(true);
		
		QueryOptionsHandle handle = new QueryOptionsHandle();
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		// make an invalid options node
		handle.build(
				builder.constraint("blah", builder.collection(false, "S")),
				builder.constraint("blah", builder.collection(true, "D")));

		try {
			mgr.writeOptions("testempty", handle);
		} catch (FailedRequestException e) {
			assertEquals(
					"Local message: /config/query write failed: Bad RequestServer Message: RESTAPI-INVALIDCONTENT: (err:FOER0000) Invalid content: Operation results in invalid Options: Operator or constraint name \"blah\" is used more than once (must be unique).",
					e.getMessage());
			assertEquals(400, e.getFailedRequest().getStatusCode());
			assertEquals("Bad Request", e.getFailedRequest().getStatus());
			assertEquals("RESTAPI-INVALIDCONTENT", e.getFailedRequest().getMessageCode());
		}

	}
}
