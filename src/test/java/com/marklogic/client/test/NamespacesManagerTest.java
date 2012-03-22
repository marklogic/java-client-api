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
		NamespacesManager nsMgr = Common.client.newNamespacesManager();
		
		nsMgr.addPrefix("dc", "http://purl.org/dc/terms/");

		String nsUri = nsMgr.readPrefix("dc");
		assertEquals("Could not read namespace", nsUri, "http://purl.org/dc/terms/");

		nsMgr.addPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		nsMgr.addPrefix("skos", "http://www.w3.org/2004/02/skos/core#");

		EditableNamespaceContext context = nsMgr.readAll();
		assertEquals("Failed to retrieve three namespaces", 3, context.size());
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
		context = nsMgr.readAll();
		assertEquals("Failed to delete namespace", 2, context.size());

		nsMgr.deleteAll();
		context = nsMgr.readAll();
		assertTrue("Failed to delete all namespaces",
				context == null || context.size() == 0);
	}
}
