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
package com.marklogic.client.test.extra;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.extra.gson.GSONHandle;
import com.marklogic.client.test.Common;

public class GSONHandleTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testReadWrite() {
		// create an identifier for the database document
		String docId = "/example/gson-test.json";

		// create a manager for JSON documents
		JSONDocumentManager docMgr = Common.client.newJSONDocumentManager();

		// construct a GSON JSON structure
		JsonObject childObj = new JsonObject();
		childObj.addProperty("key", "value");
		JsonArray childArray = new JsonArray();
		childArray.add(new JsonPrimitive("item1"));
		childArray.add(new JsonPrimitive("item2"));
		JsonObject writeRoot = new JsonObject();
		writeRoot.add("object", childObj);
		writeRoot.add("array",  childArray);

		// create a handle for the JSON structure
		GSONHandle writeHandle = new GSONHandle(writeRoot);

		// write the document to the database
		docMgr.write(docId, writeHandle);

		// create a handle to receive the database content as a GSON structure
		GSONHandle readHandle = new GSONHandle();

		// read the document content from the database as a GSON structure
		docMgr.read(docId, readHandle);

		// access the document content
		JsonObject readRoot = readHandle.get().getAsJsonObject();
		assertNotNull("Wrote null JSON structure", readRoot);
		assertTrue("JSON structures not equal",
				readRoot.equals(writeRoot));

		// delete the document
		docMgr.delete(docId);
	}
}
