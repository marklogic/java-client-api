package com.marklogic.client.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.marklogic.client.BinaryDocument;
import com.marklogic.client.GenericDocument;
import com.marklogic.client.JSONDocument;
import com.marklogic.client.TextDocument;
import com.marklogic.client.XMLDocument;

public class DatabaseClientTest {
/*
	@Test
	public void testOpenTransaction() {
		fail("Not yet implemented");
	}
 */

	@Test
	public void testNewDocument() {
		GenericDocument doc = Common.client.newDocument("/some/doc.unknown");
		assertNotNull("Client could not create generic document", doc);
	}

	@Test
	public void testNewBinaryDocument() {
		BinaryDocument doc = Common.client.newBinaryDocument("/some/doc.png");
		assertNotNull("Client could not create binary document", doc);
	}

	@Test
	public void testNewJSONDocument() {
		JSONDocument doc = Common.client.newJSONDocument("/some/doc.json");
		assertNotNull("Client could not create JSON document", doc);
	}

	@Test
	public void testNewTextDocument() {
		TextDocument doc = Common.client.newTextDocument("/some/doc.txt");
		assertNotNull("Client could not create text document", doc);
	}

	@Test
	public void testNewXMLDocument() {
		XMLDocument doc = Common.client.newXMLDocument("/some/doc.xml");
		assertNotNull("Client could not create XML document", doc);
	}

/*
	@Test
	public void testNewLogger() {
		fail("Not yet implemented");
	}

	@Test
	public void testNewQueryManager() {
		fail("Not yet implemented");
	}

	@Test
	public void testNewQueryOptionsManager() {
		fail("Not yet implemented");
	}
 */
}
