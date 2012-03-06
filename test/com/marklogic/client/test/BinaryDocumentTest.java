package com.marklogic.client.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.marklogic.client.BinaryDocument;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.InputStreamHandle;

public class BinaryDocumentTest {
	private BinaryDocument doc;

	@Before
	public void setup() {
		doc = Common.client.newBinaryDocument("/sample/databases-icon-mini.png");
	}
	@After
	public void teardown() {
		doc = null;
	}

	@Test
	public void testNewHandle() {
		BytesHandle bytesHandle = doc.newHandle(BytesHandle.class);
		assertNotNull("Binary document could not create bytes handle", bytesHandle);
		InputStreamHandle inputStreamHandle = doc.newHandle(InputStreamHandle.class);
		assertNotNull("Binary document could not create input stream handle", inputStreamHandle);
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
		assertTrue("Binary document read null bytes handle", bytesHandle != null);
		assertTrue("Binary document read 0 bytes", bytesHandle.get().length > 0);
		InputStreamHandle inputStreamHandle = doc.read(InputStreamHandle.class);
		assertNotNull("Binary document read null input stream handle",inputStreamHandle);
		byte[] b = Common.streamToBytes(inputStreamHandle.get());
		assertTrue("Binary document read binary empty input stream",b.length > 0);
	}

/*
	@Test
	public void testReadClassOfTTransactionMetadataArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testReadClassOfTLongLongMetadataArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testReadClassOfTLongLongTransactionMetadataArray() {
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
