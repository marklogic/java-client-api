package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.marklogic.client.TextDocument;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;

public class TextDocumentTest {
	// TODO: test Reader

	@Test
	public void testReadWrite() throws IOException {
		String uri = "/test/testWrite1.txt";
		String text = "A simple text document";
		TextDocument doc = Common.client.newTextDocument(uri);
		doc.write(new StringHandle().on(text));
		assertEquals("Text document write difference",text,doc.read(new StringHandle()).get());

		BytesHandle bytesHandle = new BytesHandle();
		doc.read(bytesHandle);
		assertEquals("Text document mismatch reading bytes", bytesHandle.get().length,text.length());

		InputStreamHandle inputStreamHandle = new InputStreamHandle();
		doc.read(inputStreamHandle);
		byte[] b = Common.streamToBytes(inputStreamHandle.get());
		assertEquals("Text document mismatch reading input stream",b.length,text.length());
	}

}
