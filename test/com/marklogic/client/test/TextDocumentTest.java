package com.marklogic.client.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.marklogic.client.TextDocument;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.InputStreamHandle;

public class TextDocumentTest {
	private TextDocument doc;
	@Before
	public void setup() {
		doc = Common.client.newTextDocument("/sample/second.txt");
	}
	@After
	public void teardown() {
		doc = null;
	}

	@Test
	public void testNewHandle() {
		BytesHandle bytesHandle = doc.newHandle(BytesHandle.class);
		assertNotNull("Text document could not create bytes handle", bytesHandle);
		InputStreamHandle inputStreamHandle = doc.newHandle(InputStreamHandle.class);
		assertNotNull("Text document could not create input stream handle", inputStreamHandle);
	}

/*
	@Test
	public void testExists() {
		fail("Not yet implemented");
	}
 */

	@Test
	public void testReadClassOfTMetadataArray() throws IOException {
		BytesHandle bytesHandle = doc.read(BytesHandle.class);
		assertTrue("Text document read null bytes handle", bytesHandle != null);
		assertTrue("Text document read 0 bytes", bytesHandle.get().length > 0);
		InputStreamHandle inputStreamHandle = doc.read(InputStreamHandle.class);
		assertNotNull("Text document could read null input stream handle",inputStreamHandle);
		byte[] b = Common.streamToBytes(inputStreamHandle.get());
		assertTrue("Text document could read empty input stream",b.length > 0);
	}

/*
	@Test
	public void testReadClassOfTTransactionMetadataArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testWriteW() {
		fail("Not yet implemented");
	}

	@Test
	public void testWriteWTransaction() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteTransaction() {
		fail("Not yet implemented");
	}
 */

}
