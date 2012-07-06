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
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.EditableNamespaceContext;
import com.marklogic.client.NamespacesManager;

public class NamespacesManagerTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connectAdmin();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testWriteReadPrefix() {
		NamespacesManager nsMgr =
			Common.client.newServerConfigManager().newNamespacesManager();
		
		nsMgr.updatePrefix("dc", "http://purl.org/dc/terms/");

		String nsUri = nsMgr.readPrefix("dc");
		assertEquals("Could not read namespace", nsUri, "http://purl.org/dc/terms/");

		nsMgr.updatePrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		nsMgr.updatePrefix("skos", "http://www.w3.org/2004/02/skos/core#");

		EditableNamespaceContext context = (EditableNamespaceContext) nsMgr.readAll();

		int initialSize = context.size();
		assertTrue("Failed to retrieve three namespaces", initialSize >= 3);
		assertEquals("Did not retrieve RDF namespace", 
				"http://purl.org/dc/terms/",
				context.get("dc"));
		assertEquals("Did not retrieve RDF namespace", 
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#",
				context.get("rdf"));
		assertEquals("Did not retrieve SKOS namespace", 
				"http://www.w3.org/2004/02/skos/core#",
				context.get("skos"));

		nsMgr.updatePrefix("dc", "http://diverted/category/");

		nsUri = nsMgr.readPrefix("dc");
		assertEquals("Could not read namespace", nsUri, "http://diverted/category/");

		nsMgr.deletePrefix("dc");
		context = (EditableNamespaceContext) nsMgr.readAll();
		// assumes no concurrent deletes
		assertEquals("Failed to delete namespace", initialSize - 1, context.size());

		nsMgr.deleteAll();
		context = (EditableNamespaceContext) nsMgr.readAll();
		assertTrue("Failed to delete all namespaces",
				context == null || context.size() == 0);
	}
}
