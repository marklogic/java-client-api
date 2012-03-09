package com.marklogic.client.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.marklogic.client.TextDocument;
import com.marklogic.client.io.StringHandle;

public class GenericDocumentTest {
/*
	@Test
	public void testExists() {
		fail("Not yet implemented");
	}
 */

	@Test
	public void testDelete() {
		String uri = "/test/testDelete1.txt";
		TextDocument doc = Common.client.newTextDocument(uri);
		doc.write(new StringHandle().on("A simple text document"));
		String text = doc.read(new StringHandle()).get();
		assertTrue("Could not create document for deletion", text != null && text.length() > 0);
		doc.delete();
		text = null;
		boolean hadException = false;
/*
		try {
			text = doc.read(new StringHandle()).get();
		} catch (Exception ex) {
			hadException = true;
		}
		assertTrue("Could not delete document", text == null && hadException);
 */
	}

/*
	@Test
	public void testReadMetadata() {
		fail("Not yet implemented");
	}

	@Test
	public void testWriteMetadata() {
		fail("Not yet implemented");
	}

	@Test
	public void testResetMetadata() {
		fail("Not yet implemented");
	}

	@Test
	public void testReadMetadataAsXMLT() {
		fail("Not yet implemented");
	}

	@Test
	public void testReadMetadataAsJSONT() {
		fail("Not yet implemented");
	}
 */

}
