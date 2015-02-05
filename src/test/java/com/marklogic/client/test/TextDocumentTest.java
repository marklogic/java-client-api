/*
 * Copyright 2012-2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.document.TextDocumentManager;
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
		String docId = "/test/testWrite1.txt";
		String text  = "A simple text document";

		TextDocumentManager docMgr = Common.client.newTextDocumentManager();
		docMgr.write(docId, new StringHandle().with(text));
		assertEquals("Text document write difference",text,docMgr.read(docId, new StringHandle()).get());

		BytesHandle bytesHandle = new BytesHandle();
		docMgr.read(docId, bytesHandle);
		assertEquals("Text document mismatch reading bytes", bytesHandle.get().length,text.length());

		InputStreamHandle inputStreamHandle = new InputStreamHandle();
		docMgr.read(docId, inputStreamHandle);
		byte[] b = Common.streamToBytes(inputStreamHandle.get());
		assertEquals("Text document mismatch reading input stream",new String(b),text);

		Reader reader = docMgr.read(docId, new ReaderHandle()).get();
		String s = Common.readerToString(reader);
		assertEquals("Text document mismatch with reader",s,text);

		File file = docMgr.read(docId, new FileHandle()).get();
		assertEquals("Text document mismatch with file",text.length(),file.length());
	}

}
