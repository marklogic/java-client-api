package com.marklogic.client.test;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.marklogic.client.TextDocumentBuffer;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.StringHandle;

public class GenericDocumentTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testExists() {
		String uri = "/test/testExists1.txt";
		TextDocumentBuffer doc = Common.client.newTextDocumentBuffer(uri);
		assertTrue("Non-existent document appears to exists", !doc.exists());
		doc.write(new StringHandle().on("A simple text document"));
		assertTrue("Existent document doesn't appear to exist", doc.exists());
		doc.delete();
	}

	@Test
	public void testDelete() {
		String uri = "/test/testDelete1.txt";
		TextDocumentBuffer doc = Common.client.newTextDocumentBuffer(uri);
		doc.write(new StringHandle().on("A simple text document"));
		String text = doc.read(new StringHandle()).get();
		assertTrue("Could not create document for deletion", text != null && text.length() > 0);
		doc.delete();
		text = null;
		boolean hadException = false;
		try {
			text = doc.read(new StringHandle()).get();
		} catch (Exception ex) {
			hadException = true;
		}
		assertTrue("Could not delete document", text == null && hadException);
	}

/*
	@Test
	public void testReadWriteMetadata() {
		fail("Not yet implemented");
	}

	@Test
	public void testResetMetadata() {
		fail("Not yet implemented");
	}
 */

	@Test
	public void testReadWriteMetadataAsXML() {
		String uri = "/test/testMetadataXML1.txt";
		TextDocumentBuffer doc = Common.client.newTextDocumentBuffer(uri);
		doc.write(new StringHandle().on("A simple text document"));
		String temporaryTest = doc.readMetadataAsXML(new StringHandle()).get();
		assertTrue("Could not get document metadata as XML", temporaryTest != null && temporaryTest.length() > 0);

// TODO: server is producing invalid XML
//		Document domDocument = doc.readMetadataAsXML(new DOMHandle()).get();
//		assertTrue("Could not get document metadata as XML", domDocument != null);
// TODO: verify collections, permissions, properties, and quality; modify and write
//		assertTrue("Could not get document metadata as XML", domDocument.getElementsByTagNameNS("", "") != null);
	}

/*
	@Test
	public void testReadWriteMetadataAsJSON() {
		fail("Not yet implemented");
	}
 */

}
