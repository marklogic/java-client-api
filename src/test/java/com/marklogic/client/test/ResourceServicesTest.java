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

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.marklogic.client.Format;
import com.marklogic.client.RequestParameters;
import com.marklogic.client.ResourceExtensionsManager;
import com.marklogic.client.ResourceManager;
import com.marklogic.client.ResourceServices;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.StringHandle;

public class ResourceServicesTest {
	static private String resourceServices;

	@BeforeClass
	public static void beforeClass() throws IOException {
		Common.connectAdmin();
		resourceServices = Common.testFileToString(ResourceExtensionsTest.XQUERY_FILE);
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
		resourceServices = null;
	}

	@Test
	public void testResourceServices() throws XpathException {
		ResourceExtensionsManager extensionMgr =
			Common.client.newServerConfigManager().newResourceExtensionsManager();

		extensionMgr.writeServices(
				ResourceExtensionsTest.RESOURCE_NAME,
				new StringHandle().withFormat(Format.XML).with(resourceServices),
				ResourceExtensionsTest.makeMetadata(),
				ResourceExtensionsTest.makeParameters()
				);

		SimpleResourceManager resourceMgr =
			Common.client.init(ResourceExtensionsTest.RESOURCE_NAME, new SimpleResourceManager());

		RequestParameters params = new RequestParameters();
		params.put("value", "true");

		Document result = resourceMgr.getResourceServices().get(params, new DOMHandle()).get();
		assertNotNull("Failed to get resource service with single document", result);
		assertXpathEvaluatesTo("true", "/read-doc/param", result);

		DOMHandle[] readHandles = new DOMHandle[2];
		readHandles[0] = new DOMHandle();
		readHandles[1] = new DOMHandle();

		resourceMgr.getResourceServices().get(params, readHandles);
		result = readHandles[0].get();
		assertNotNull("Failed to get resource service with multiple documents", result);
		assertXpathEvaluatesTo("true", "/read-doc/param", result);
		result = readHandles[1].get();
		assertNotNull("Failed to get resource service with multiple documents", result);
		assertXpathEvaluatesTo("true", "/read-multi-doc/multi-param", result);

		StringHandle writeHandle =
			new StringHandle().withFormat(Format.XML).with("<input-doc>true</input-doc>");

		result = resourceMgr.getResourceServices().put(params, writeHandle, new DOMHandle()).get();
		assertNotNull("Failed to put resource service with a single document", result);
		assertXpathEvaluatesTo("true", "/wrote-doc/param", result);
		assertXpathEvaluatesTo("true", "/wrote-doc/input-doc", result);

		StringHandle[] writeHandles = new StringHandle[2];
		writeHandles[0] = new StringHandle().withFormat(Format.XML).with("<input-doc>true</input-doc>");
		writeHandles[1] = new StringHandle().withFormat(Format.XML).with("<multi-input-doc>true</multi-input-doc>");

		result = resourceMgr.getResourceServices().put(params, writeHandles, new DOMHandle()).get();
		assertNotNull("Failed to put resource service with multiple documents", result);
		assertXpathEvaluatesTo("true", "/wrote-doc/param", result);
		assertXpathEvaluatesTo("true", "/wrote-doc/input-doc", result);
		assertXpathEvaluatesTo("true", "/wrote-doc/multi-input-doc", result);

		result = resourceMgr.getResourceServices().post(params, writeHandle, new DOMHandle()).get();
		assertNotNull("Failed to post resource service with a single document", result);
		assertXpathEvaluatesTo("true", "/applied-doc/param", result);
		assertXpathEvaluatesTo("true", "/applied-doc/input-doc", result);

		resourceMgr.getResourceServices().post(params, writeHandles, readHandles);
		result = readHandles[0].get();
		assertNotNull("Failed to post resource service with multiple documents", result);
		assertXpathEvaluatesTo("true", "/applied-doc/param", result);
		result = readHandles[1].get();
		assertNotNull("Failed to post resource service with multiple documents", result);
		assertXpathEvaluatesTo("true", "/applied-multi-doc/multi-param", result);

		result = resourceMgr.getResourceServices().delete(params, new DOMHandle()).get();
		assertNotNull("Failed to delete resource service with a single document", result);
		assertXpathEvaluatesTo("true", "/deleted-doc/param", result);

		extensionMgr.deleteServices(ResourceExtensionsTest.RESOURCE_NAME);
	}

	class SimpleResourceManager extends ResourceManager {
		public SimpleResourceManager() {
			super();
		}
		// a real ResourceManager would provide a facade over services
		public ResourceServices getResourceServices() {
			return getServices();
		}
	}
}
