/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest.search;

import com.marklogic.client.fastfunctest.AbstractFunctionalTest;
import com.marklogic.client.io.*;
import com.marklogic.client.query.*;
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;

import static org.custommonkey.xmlunit.XMLAssert.*;


class SearchWithPageLengthTest extends AbstractFunctionalTest {

	static final QueryManager queryMgr = client.newQueryManager();
	StringQueryDefinition qd = queryMgr.newStringDefinition();

	@BeforeEach
	public void testSetup() throws FileNotFoundException {
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		for (String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/return-results-false/", "XML");
		}
	}

	@AfterEach
	public void testCleanUp() {
		deleteDocuments(connectAsAdmin());
	}

	@Test
	void testSearchPageLengthZero()  {
		queryMgr.setPageLength(0);
		qd.setCriteria("Bush");
		SearchHandle resultsHandle = new SearchHandle();
		queryMgr.search(qd, resultsHandle);

		assertEquals(0, resultsHandle.getPageLength());
		assertEquals("The server allows for a zero page length, which results in no results being returned.",
			0, resultsHandle.getMatchResults().length);
	}


	@Test
	void testSearchPageLengthNegative() {
		queryMgr.setPageLength(-1);
		qd.setCriteria("Bush");
		SearchHandle resultsHandle = new SearchHandle();
		queryMgr.search(qd, resultsHandle);

		assertEquals("Negative page lengths are not sent to the server, so the default page length of 10 should be used.",
			10, resultsHandle.getPageLength());
		assertEquals(2, resultsHandle.getMatchResults().length);
	}
}
