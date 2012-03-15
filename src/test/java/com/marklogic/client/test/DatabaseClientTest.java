package com.marklogic.client.test;

import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.BinaryDocumentManager;
import com.marklogic.client.GenericDocumentManager;
import com.marklogic.client.JSONDocumentManager;
import com.marklogic.client.TextDocumentManager;
import com.marklogic.client.XMLDocumentManager;

public class DatabaseClientTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

/*
	@Test
	public void testOpenTransaction() {
		fail("Not yet implemented");
	}
 */

	@Test
	public void testNewDocument() {
		GenericDocumentManager doc = Common.client.newDocumentManager();
		assertNotNull("Client could not create generic document", doc);
	}

	@Test
	public void testNewBinaryDocument() {
		BinaryDocumentManager doc = Common.client.newBinaryDocumentManager();
		assertNotNull("Client could not create binary document", doc);
	}

	@Test
	public void testNewJSONDocument() {
		JSONDocumentManager doc = Common.client.newJSONDocumentManager();
		assertNotNull("Client could not create JSON document", doc);
	}

	@Test
	public void testNewTextDocument() {
		TextDocumentManager doc = Common.client.newTextDocumentManager();
		assertNotNull("Client could not create text document", doc);
	}

	@Test
	public void testNewXMLDocument() {
		XMLDocumentManager doc = Common.client.newXMLDocumentManager();
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
