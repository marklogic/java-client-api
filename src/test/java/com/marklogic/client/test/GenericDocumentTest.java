package com.marklogic.client.test;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.marklogic.client.AbstractDocumentManager.Metadata;
import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.TextDocumentManager;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.docio.StructureFormat;
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

	@Test
	public void testReadWriteMetadata() {
		String uri = "/test/testMetadataXML1.xml";
		String content = "<?xml version='1.0' encoding='UTF-8'?>\n"+
			"<root mode='mixed' xml:lang='en'>\n"+
			"<child mode='basic'>value</child>\n"+
			"A simple XML document\n"+
			"</root>\n";
		DocumentIdentifier docId = new DocumentIdentifier(uri);
		XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
		docMgr.write(docId, new StringHandle().on(content));

		docMgr.setMetadataCategories(Metadata.ALL);

		Document domMetadata = docMgr.readMetadata(docId, new DOMHandle()).get();
		assertTrue("Could not get document metadata as XML", domMetadata != null);

		StringHandle jsonStringHandle = new StringHandle();
		jsonStringHandle.setFormat(StructureFormat.JSON);
		String stringMetadata = docMgr.readMetadata(docId, jsonStringHandle).get();
		assertTrue("Could not get document metadata as JSON", stringMetadata != null || stringMetadata.length() == 0);

// TODO: verify collections, permissions, properties, and quality; modify and write
	}

/*
	@Test
	public void testResetMetadata() {
		fail("Not yet implemented");
	}
 */

}
