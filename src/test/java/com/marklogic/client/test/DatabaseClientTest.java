package com.marklogic.client.test;

import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.BinaryDocumentBuffer;
import com.marklogic.client.GenericDocumentBuffer;
import com.marklogic.client.JSONDocumentBuffer;
import com.marklogic.client.TextDocumentBuffer;
import com.marklogic.client.XMLDocumentBuffer;

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
		GenericDocumentBuffer doc = Common.client.newDocumentBuffer("/some/doc.unknown");
		assertNotNull("Client could not create generic document", doc);
	}

	@Test
	public void testNewBinaryDocument() {
		BinaryDocumentBuffer doc = Common.client.newBinaryDocumentBuffer("/some/doc.png");
		assertNotNull("Client could not create binary document", doc);
	}

	@Test
	public void testNewJSONDocument() {
		JSONDocumentBuffer doc = Common.client.newJSONDocumentBuffer("/some/doc.json");
		assertNotNull("Client could not create JSON document", doc);
	}

	@Test
	public void testNewTextDocument() {
		TextDocumentBuffer doc = Common.client.newTextDocumentBuffer("/some/doc.txt");
		assertNotNull("Client could not create text document", doc);
	}

	@Test
	public void testNewXMLDocument() {
		XMLDocumentBuffer doc = Common.client.newXMLDocumentBuffer("/some/doc.xml");
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
