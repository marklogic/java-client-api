package com.marklogic.client.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.marklogic.client.BinaryDocument;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.InputStreamHandle;

public class BinaryDocumentTest {

	@Test
	public void testReadClassOfTMetadataArray() throws IOException {
		BinaryDocument doc = Common.client.newBinaryDocument("/sample/databases-icon-mini.png");
		BytesHandle bytesHandle = new BytesHandle();
		doc.read(bytesHandle);
		assertTrue("Binary document read 0 bytes", bytesHandle.get().length > 0);
		InputStreamHandle inputStreamHandle = new InputStreamHandle();
		doc.read(inputStreamHandle);
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
 */

/*
	@Test
	public void testWriteW() {
		fail("Not yet implemented");
	}

	@Test
	public void testWriteWTransaction() {
		fail("Not yet implemented");
	}
 */
}
