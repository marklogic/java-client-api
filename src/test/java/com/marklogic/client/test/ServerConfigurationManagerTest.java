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
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.ServerConfigurationManager.Policy;

public class ServerConfigurationManagerTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connectAdmin();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testSetGet() throws IOException {
		ServerConfigurationManager serverConfig = Common.client.newServerConfigManager();

		assertNull("Initial query option validation not null", serverConfig.getQueryOptionValidation());

		serverConfig.readConfiguration();
		Boolean initialOptionValid = serverConfig.getQueryOptionValidation();
		String  initialReadTrans   = serverConfig.getDefaultDocumentReadTransform();
		Boolean initialRequestLog  = serverConfig.getServerRequestLogging();
		Policy  initialVersionReq  = serverConfig.getContentVersionRequests();

		Boolean modOptionValid = initialOptionValid ? false : true;
		String  modReadTrans   = "modifiedReadTransform";
		Boolean modRequestLog  = initialOptionValid ? false : true;
		Policy  modVersionReq  = (initialVersionReq == Policy.OPTIONAL) ?
			Policy.REQUIRED : Policy.OPTIONAL;

		serverConfig = Common.client.newServerConfigManager();
		serverConfig.setQueryOptionValidation(modOptionValid);
		serverConfig.setDefaultDocumentReadTransform(modReadTrans);
		serverConfig.setServerRequestLogging(modRequestLog);
		serverConfig.setContentVersionRequests(modVersionReq);
		serverConfig.writeConfiguration();

		serverConfig = Common.client.newServerConfigManager();
		serverConfig.readConfiguration();
		assertEquals("Failed to change query options validation",
				modOptionValid, serverConfig.getQueryOptionValidation());
		assertEquals("Failed to change document read transform",
				modReadTrans,   serverConfig.getDefaultDocumentReadTransform());
		assertEquals("Failed to change server request logging",
				modRequestLog,  serverConfig.getServerRequestLogging());
		assertEquals("Failed to change content version requests",
				modVersionReq, serverConfig.getContentVersionRequests());
		
		serverConfig = Common.client.newServerConfigManager();
		serverConfig.setQueryOptionValidation(initialOptionValid);
		serverConfig.setDefaultDocumentReadTransform(initialReadTrans);
		serverConfig.setServerRequestLogging(initialRequestLog);
		serverConfig.setContentVersionRequests(initialVersionReq);
		serverConfig.writeConfiguration();
	}

}
