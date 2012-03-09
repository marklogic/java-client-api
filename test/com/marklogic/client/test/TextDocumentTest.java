package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.marklogic.client.TextDocument;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;

public class TextDocumentTest {
	@Test
	public void testReadClassOfTMetadataArray() throws IOException {
		TextDocument doc = Common.client.newTextDocument("/sample/second.txt");
		BytesHandle bytesHandle = new BytesHandle();
		doc.read(bytesHandle);
		assertTrue("Text document read 0 bytes", bytesHandle.get().length > 0);
		InputStreamHandle inputStreamHandle = new InputStreamHandle();
		doc.read(inputStreamHandle);
		byte[] b = Common.streamToBytes(inputStreamHandle.get());
		assertTrue("Text document could read empty input stream",b.length > 0);
	}

/*
	@Test
	public void testReadClassOfTTransactionMetadataArray() {
		fail("Not yet implemented");
	}
 */

	@Test
	public void testWriteW() {
		String uri = "/test/testWrite1.txt";
		String text = "A simple text document";
		TextDocument doc = Common.client.newTextDocument(uri);
		StringHandle stringHandle = new StringHandle();
		stringHandle.set(text);
		doc.write(stringHandle);
		assertEquals("Text document write difference",text,doc.read(new StringHandle()).get());
	}

/*
	@Test
	public void testWriteWTransaction() {
		fail("Not yet implemented");
	}
 */

}
