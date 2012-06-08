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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.marklogic.client.ExtensionMetadata;
import com.marklogic.client.ResourceExtensionsManager;
import com.marklogic.client.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.MethodType;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.StringHandle;

public class ResourceExtensionsTest {
	final static String RESOURCE_NAME = "testresource";
	final static String XQUERY_FILE   = RESOURCE_NAME + ".xqy"; 

	static private String resourceServices;

	@BeforeClass
	public static void beforeClass() throws IOException {
		Common.connectAdmin();
		resourceServices = Common.testFileToString(XQUERY_FILE);
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
		resourceServices = null;
	}

	static ExtensionMetadata makeMetadata() {
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Test Resource Services");
		metadata.setDescription("This library supports all methods on the test resource");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");
		return metadata;
	}

	static MethodParameters[] makeParameters() {
		MethodType[] methods = MethodType.values();
		MethodParameters[] params = new MethodParameters[methods.length];
		for (int i=0; i < methods.length; i++) {
			params[i] = new MethodParameters(methods[i]);
			params[i].put("value", "xs:boolean");
		}
		return params;
	}

	@Test
	public void testResourceServiceExtension() throws XpathException {
		ResourceExtensionsManager extensionMgr =
			Common.client.newServerConfigManager().newResourceExtensionsManager();

		StringHandle handle = new StringHandle();

		ExtensionMetadata metadata = makeMetadata();
		MethodParameters[] params = makeParameters();

		handle.set(resourceServices);
		extensionMgr.writeServices(RESOURCE_NAME, handle, metadata, params);

		extensionMgr.readServices(RESOURCE_NAME, handle);
		assertEquals("Failed to retrieve resource services", resourceServices, handle.get());

		Document result = extensionMgr.listServices(new DOMHandle()).get();
		assertNotNull("Failed to retrieve resource services list", result);
		assertXpathEvaluatesTo("1", "count(/*[local-name(.) = 'resources']/*[local-name(.) = 'resource']/*[local-name(.) = 'name'])", result);

		extensionMgr.deleteServices(RESOURCE_NAME);
		extensionMgr.readServices(RESOURCE_NAME, handle);
		assertNull("Failed to delete resource services", handle.get());
	}
}
