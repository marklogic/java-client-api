/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.DocumentPatchBuilder.PathLanguage;
import com.marklogic.client.document.DocumentPatchBuilder.Position;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.fastfunctest.AbstractFunctionalTest;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.rest.util.Fragment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPointInTimeQuery extends AbstractFunctionalTest {

	@BeforeAll
	public static void setUp() throws Exception {
		setMergeTimestamp(DB_NAME, "-600000000");
	}

	@AfterAll
	public static void afterAll() {
		setMergeTimestamp(DB_NAME, "0");
	}

	/*
	 * This test verifies if fragments are available for a document that is
	 * inserted and then updated when merge time is set to 60 seconds and both
	 * insert and update are within that time period.
	 *
	 * Git Issue 457 needs to be completed in order for this test to be fleshed
	 * completly.
	 *
	 * Insert doc Verify fragment counts Update the document Verify read with
	 * Point In Time Stamp Verify fragment counts. Update again. Verify read with
	 * Point In Time Stamp again Verify fragment counts second time
	 */
	@Test
	public void testAInsertAndUpdateJson() throws KeyManagementException, NoSuchAlgorithmException, IOException {
		String[] filenames = {"json-original.json"};

		DatabaseClient client = getDatabaseClient("rest-evaluator", "x", getConnType());

		DatabaseCounts originalCounts = getFragmentCounts();

		// write docs and save the timestamps in the array

		for (String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "JSON");
		}
		DocumentManager docMgrIns = client.newJSONDocumentManager();
		// create handle
		JacksonHandle jacksonHandle = new JacksonHandle();

		// Read the document with timestamp.
		docMgrIns.read("/partial-update/" + filenames[0], jacksonHandle);

		// Make sure we have the original document and document count is 1

		long insTimeStamp = jacksonHandle.getServerTimestamp();
		System.out.println("Point in Time Stamp after the initial insert " + insTimeStamp);
		DatabaseCounts dbCounts = getFragmentCounts();
		System.out.println("Fragment counts after initial insert: " + dbCounts);
		assertEquals(originalCounts.activeFragments + 1, dbCounts.activeFragments);
		assertEquals(originalCounts.deletedFragments, dbCounts.deletedFragments);

		// Update the doc. Insert a fragment.
		ObjectMapper mapper = new ObjectMapper();

		String docId = "/partial-update/json-original.json";
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		patchBldr.pathLanguage(PathLanguage.JSONPATH);

		ObjectNode fragmentNode = mapper.createObjectNode();
		ObjectNode fragmentNode1 = mapper.createObjectNode();
		ObjectNode fragmentNode2 = mapper.createObjectNode();

		fragmentNode.put("insertedKey", 9);
		fragmentNode1.put("original", true);
		fragmentNode2.put("modified", false);

		String fragment = mapper.writeValueAsString(fragmentNode);
		String fragment1 = mapper.writeValueAsString(fragmentNode1);
		String fragment2 = mapper.writeValueAsString(fragmentNode2);

		String jsonpath = new String("$.employees[2]");
		patchBldr.insertFragment(jsonpath, Position.AFTER, fragment);
		patchBldr.insertFragment("$.employees[2]", Position.AFTER, fragment1);

		DocumentPatchHandle patchHandle = patchBldr.build();
		docMgr.patch(docId, patchHandle);

		// Now read using a handle and time value in insTimeStamp. The updates
		// should not be present.
		StringHandle shReadInsTS = new StringHandle();
		shReadInsTS.setPointInTimeQueryTimestamp(insTimeStamp);
		String insTS = docMgr.read(docId, shReadInsTS).get();
		System.out.println(insTS);
		assertFalse(insTS.contains("{\"insertedKey\":9}"));
		assertFalse(insTS.contains("{\"original\":true}"));

		// Now read using a handle without a Point In Time value set on the handle.
		// Should return the latest document.
		String content = docMgr.read(docId, new StringHandle()).get();

		System.out.println(content);

		assertTrue(content.contains("{\"insertedKey\":9}"));
		assertTrue(content.contains("{\"original\":true}"));

		// Read the document with timestamp. Todo QUERY DB for document at a Point
		// In Time. Git Issue 457
		// Verify that the test can validate the first original docs only within the
		// -60 (time interval)

		// Sleep for some time. Read again with new point In time.
		// Verify that we get updated document.

		docMgrIns.read("/partial-update/" + filenames[0], jacksonHandle);

		long firstUpdTimeStamp = jacksonHandle.getPointInTimeQueryTimestamp();
		System.out.println("Point in Time Stamp after the first update " + firstUpdTimeStamp);

		// Verify the counts
		dbCounts = getFragmentCounts();
		System.out.println("Fragment counts after first update: " + dbCounts);
		assertEquals(originalCounts.activeFragments + 1, dbCounts.activeFragments);
		assertEquals(originalCounts.deletedFragments + 1, dbCounts.deletedFragments);

		// Insert / update the document again

		patchBldr.insertFragment("$.employees[0]", Position.AFTER, fragment2);

		patchHandle = patchBldr.build();
		docMgr.patch(docId, patchHandle);

		content = docMgr.read(docId, new StringHandle()).get();
		System.out.println(content);
		assertTrue(content.contains("{\"modified\":false}"));

		// Read the document with timestamp. Todo QUERY DB for document at a Point
		// In Time. Git Issue 457
		// Verify that the test can validate the first original docs only within the
		// -60 (time interval)

		// Sleep for some time. Read again with new point In time.
		// Verify that we get updated document.

		docMgrIns.read("/partial-update/" + filenames[0], jacksonHandle);

		long secondUpdTimeStamp = jacksonHandle.getPointInTimeQueryTimestamp();
		System.out.println("Point in Time Stamp after the first update " + secondUpdTimeStamp);

		dbCounts = getFragmentCounts();
		System.out.println("Fragment counts after second update: " + dbCounts);
		assertEquals(originalCounts.activeFragments + 1, dbCounts.activeFragments);
		assertEquals(originalCounts.deletedFragments + 2, dbCounts.deletedFragments);

		client.release();
	}

	static class DatabaseCounts {
		public int activeFragments;
		public int deletedFragments;

		@Override
		public String toString() {
			return "active=" + activeFragments + "; deleted=" + deletedFragments;
		}
	}

	private DatabaseCounts getFragmentCounts() {
		Fragment doc = newManageClient().getXml("/manage/v2/databases/" + DB_NAME + "?view=counts&format=xml");
		DatabaseCounts dbCounts = new DatabaseCounts();
		dbCounts.activeFragments = Integer.parseInt(doc.getElementValue("/db:database-counts/db:count-properties/db:active-fragments"));
		dbCounts.deletedFragments = Integer.parseInt(doc.getElementValue("/db:database-counts/db:count-properties/db:deleted-fragments"));
		return dbCounts;
	}
}
