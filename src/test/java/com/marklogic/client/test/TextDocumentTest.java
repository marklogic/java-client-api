package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.TextDocumentBuffer;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;

public class TextDocumentTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testReadWrite() throws IOException {
		String uri = "/test/testWrite1.txt";
		String text = "A simple text document";
		TextDocumentBuffer doc = Common.client.newTextDocumentBuffer(uri);
		doc.write(new StringHandle().on(text));
		assertEquals("Text document write difference",text,doc.read(new StringHandle()).get());

		BytesHandle bytesHandle = new BytesHandle();
		doc.read(bytesHandle);
		assertEquals("Text document mismatch reading bytes", bytesHandle.get().length,text.length());

		InputStreamHandle inputStreamHandle = new InputStreamHandle();
		doc.read(inputStreamHandle);
		byte[] b = Common.streamToBytes(inputStreamHandle.get());
		assertEquals("Text document mismatch reading input stream",new String(b),text);

		Reader reader = doc.read(new ReaderHandle()).get();
		String s = Common.readerToString(reader);
		assertEquals("Text document mismatch with reader",s,text);

		File file = doc.read(new FileHandle()).get();
		assertEquals("Text document mismatch with file",text.length(),file.length());
	}

}
