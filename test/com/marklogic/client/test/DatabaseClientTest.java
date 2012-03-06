package com.marklogic.client.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.marklogic.client.BinaryDocument;
import com.marklogic.client.TextDocument;

public class DatabaseClientTest {
/*
	@Test
	public void testOpenTransaction() {
		fail("Not yet implemented");
	}
 */

/*
	@Test
	public void testNewDocument() {
		fail("Not yet implemented");
	}
 */

	@Test
	public void testNewBinaryDocument() {
		BinaryDocument doc = Common.client.newBinaryDocument("/some/doc.png");
		assertNotNull("Client could not create binary document", doc);
	}

/*
	@Test
	public void testNewJSONDocument() {
		fail("Not yet implemented");
	}
 */

	@Test
	public void testNewTextDocument() {
		TextDocument doc = Common.client.newTextDocument("/some/doc.txt");
		assertNotNull("Client could not create text document", doc);
	}

/*
	@Test
	public void testNewXMLDocument() {
		fail("Not yet implemented");
	}

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
