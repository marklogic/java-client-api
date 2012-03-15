package com.marklogic.client.test;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.TextDocumentManager;
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
		DocumentIdentifier docId = new DocumentIdentifier(uri);
		TextDocumentManager docMgr = Common.client.newTextDocumentManager();
		assertTrue("Non-existent document appears to exists", !docMgr.exists(docId));
		docMgr.write(docId,new StringHandle().on("A simple text document"));
		assertTrue("Existent document doesn't appear to exist", docMgr.exists(docId));
		docMgr.delete(docId);
	}

	@Test
	public void testDelete() {
		String uri = "/test/testDelete1.txt";
		DocumentIdentifier docId = new DocumentIdentifier(uri);
		TextDocumentManager docMgr = Common.client.newTextDocumentManager();
		docMgr.write(docId, new StringHandle().on("A simple text document"));
		String text = docMgr.read(docId, new StringHandle()).get();
		assertTrue("Could not create document for deletion", text != null && text.length() > 0);
		docMgr.delete(docId);
		text = null;
		boolean hadException = false;
		try {
			text = docMgr.read(docId, new StringHandle()).get();
		} catch (Exception ex) {
			hadException = true;
		}
		assertTrue("Could not delete document", text == null && hadException);
	}

/*
	@Test
	public void testReadWriteMetadata() {
		String uri = "/test/testMetadataXML1.txt";
		DocumentIdentifier docId = new DocumentIdentifier(uri);
		TextDocumentManager docMgr = Common.client.newTextDocumentManager();
		docMgr.write(docId, new StringHandle().on("A simple text document"));
		String temporaryTest = docMgr.readMetadata(docId, new StringHandle()).get();
		assertTrue("Could not get document metadata as XML", temporaryTest != null && temporaryTest.length() > 0);

// TODO: server is producing invalid XML
//		Document domDocument = docMgr.readMetadataAsXML(new DOMHandle()).get();
//		assertTrue("Could not get document metadata as XML", domDocument != null);
// TODO: verify collections, permissions, properties, and quality; modify and write
//		assertTrue("Could not get document metadata as XML", domDocument.getElementsByTagNameNS("", "") != null);
	}

	@Test
	public void testResetMetadata() {
		fail("Not yet implemented");
	}
 */

}
