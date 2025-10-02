/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.bitemporal.TemporalDescriptor;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.*;
import com.marklogic.client.query.StructuredQueryBuilder.TemporalOperator;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.temporal.TemporalCollectionLSQTManager;
import jakarta.xml.bind.DatatypeConverter;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.util.Calendar;
import java.util.Random;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.jupiter.api.Assertions.*;

class BitemporalTest {

	static String temporalCollection = "temporal-collection";
	static XMLDocumentManager docMgr;
	static QueryManager queryMgr;
	static String uniqueBulkTerm = "temporalBulkDoc" + new Random().nextInt(10000);
	static String uniqueTerm = "temporalDoc" + new Random().nextInt(10000);
	static String docId = "test-" + uniqueTerm + ".xml";

	@BeforeEach
	void setup() {
		Common.connect();
		docMgr = Common.client.newXMLDocumentManager();
		queryMgr = Common.client.newQueryManager();
	}

	@AfterEach
	void teardown() {
		try (DatabaseClient client = Common.newServerAdminClient()) {
			QueryManager queryMgr = client.newQueryManager();
			queryMgr.setPageLength(1000);
			QueryDefinition query = queryMgr.newStringDefinition();
			query.setCollections(temporalCollection);
			SearchHandle handle = queryMgr.search(query, new SearchHandle());
			MatchDocumentSummary[] docs = handle.getMatchResults();
			for (MatchDocumentSummary doc : docs) {
				if (!(temporalCollection + ".lsqt").equals(doc.getUri())) {
					client.newXMLDocumentManager().delete(doc.getUri());
				}
			}
		}
	}

	@Test
	void writeTemporalDoc() {
		String contents = """
			<test>
				<system-start></system-start>
				<system-end></system-end>
				<valid-start>2014-08-19T00:00:00Z</valid-start>
				<valid-end>2014-08-19T00:00:01Z</valid-end>
			</test>""";

		TemporalDescriptor desc = docMgr.create(docMgr.newDocumentUriTemplate("xml"),
			null, new StringHandle(contents), null, null, temporalCollection);
		assertNotNull(desc);
		assertNotNull(desc.getUri());
		assertTrue(desc.getUri().endsWith(".xml"));

		String lastWriteTimestamp = desc.getTemporalSystemTime();
		Calendar lastWriteTime = DatatypeConverter.parseDateTime(lastWriteTimestamp);
		assertNotNull(lastWriteTime);
	}

	@Test
	void writeTwoVersionsOfFourDocuments() throws XpathException {
		String prefix = "test_" + uniqueBulkTerm;
		String doc1 = """
			<test>
			%s doc1
			<system-start></system-start>
			<system-end></system-end>
			<valid-start>2014-08-19T00:00:00Z</valid-start>
			<valid-end>2014-08-19T00:00:01Z</valid-end>
			</test>""".formatted(uniqueBulkTerm);

		String doc2 = """
			<test>
			%s doc2
			<system-start></system-start>
			<system-end></system-end>
			<valid-start>2014-08-19T00:00:02Z</valid-start>
			<valid-end>2014-08-19T00:00:03Z</valid-end>
			</test>""".formatted(uniqueBulkTerm);

		String doc3 = """
			<test>
			%s doc3
			<system-start></system-start>
			<system-end></system-end>
			<valid-start>2014-08-19T00:00:03Z</valid-start>
			<valid-end>2014-08-19T00:00:04Z</valid-end>
			</test>""".formatted(uniqueBulkTerm);

		String doc4 = """
			<test>
			%s doc4
			<system-start></system-start>
			<system-end></system-end>
			<valid-start>2014-08-19T00:00:05Z</valid-start>
			<valid-end>2014-08-19T00:00:06Z</valid-end>
			</test>""".formatted(uniqueBulkTerm);

		DocumentWriteSet writeSet = docMgr.newWriteSet();
		writeSet.add(prefix + "_1.xml", new StringHandle(doc1));
		writeSet.add(prefix + "_2.xml", new StringHandle(doc2));
		writeSet.add(prefix + "_3.xml", new StringHandle(doc3));
		writeSet.add(prefix + "_4.xml", new StringHandle(doc4));
		docMgr.write(writeSet, null, null, temporalCollection);

		// do it one more time so we have two versions of each
		writeSet = docMgr.newWriteSet();
		writeSet.add(prefix + "_1.xml", new StringHandle(doc1));
		writeSet.add(prefix + "_2.xml", new StringHandle(doc2));
		writeSet.add(prefix + "_3.xml", new StringHandle(doc3));
		writeSet.add(prefix + "_4.xml", new StringHandle(doc4));
		docMgr.write(writeSet, null, null, temporalCollection);

		StringQueryDefinition query = queryMgr.newStringDefinition().withCriteria(uniqueBulkTerm);
		try (DocumentPage page = docMgr.search(query, 0)) {
			assertEquals(8, page.size());
			for (DocumentRecord record : page) {
				Document doc = record.getContentAs(Document.class);
				if (record.getUri().startsWith(prefix + "_1")) {
					assertXpathEvaluatesTo("2014-08-19T00:00:00Z", "//valid-start", doc);
					continue;
				} else if (record.getUri().startsWith(prefix + "_2")) {
					assertXpathEvaluatesTo("2014-08-19T00:00:02Z", "//valid-start", doc);
					continue;
				} else if (record.getUri().startsWith(prefix + "_3")) {
					assertXpathEvaluatesTo("2014-08-19T00:00:03Z", "//valid-start", doc);
					continue;
				} else if (record.getUri().startsWith(prefix + "_4")) {
					assertXpathEvaluatesTo("2014-08-19T00:00:05Z", "//valid-start", doc);
					continue;
				}
				throw new IllegalStateException("Unexpected doc:[" + record.getUri() + "]");
			}
		}
	}

	@Test
	void lsqtTest() {
		// Due to bug MLE-24511 where LSQT properties aren't updated correctly in ml-gradle 6.0.0, we need to manually
		// deploy them for this test.
		ManageClient manageClient = Common.newManageClient();
		TemporalCollectionLSQTManager mgr = new TemporalCollectionLSQTManager(manageClient, "java-unittest", "temporal-collection");
		String payload = """
						{
				"lsqt-enabled": true,
				"automation": {
					"enabled": true,
					"period": 5000
				}
			}
			""";
		mgr.save(payload);

		String version1 = """
			<test>
			%s version1
			<system-start></system-start>
			<system-end></system-end>
			<valid-start>2014-08-19T00:00:00Z</valid-start>
			<valid-end>2014-08-19T00:00:01Z</valid-end>
			</test>""".formatted(uniqueTerm);

		String version2 = """
			<test>
			%s version2
			<system-start></system-start>
			<system-end></system-end>
			<valid-start>2014-08-19T00:00:02Z</valid-start>
			<valid-end>2014-08-19T00:00:03Z</valid-end>
			</test>""".formatted(uniqueTerm);

		String version3 = """
			<test>
			%s version3
			<system-start></system-start>
			<system-end></system-end>
			<valid-start>2014-08-19T00:00:03Z</valid-start>
			<valid-end>2014-08-19T00:00:04Z</valid-end>
			</test>""".formatted(uniqueTerm);

		String version4 = """
			<test>
			%s version4
			<system-start></system-start>
			<system-end></system-end>
			<valid-start>2014-08-19T00:00:05Z</valid-start>
			<valid-end>2014-08-19T00:00:06Z</valid-end>
			</test>""".formatted(uniqueTerm);

		// write four versions of the same document
		StringHandle handle1 = new StringHandle(version1).withFormat(Format.XML);
		docMgr.write(docId, null, handle1, null, null, temporalCollection);
		StringHandle handle2 = new StringHandle(version2).withFormat(Format.XML);
		docMgr.write(docId, null, handle2, null, null, temporalCollection);
		StringHandle handle3 = new StringHandle(version3).withFormat(Format.XML);
		TemporalDescriptor desc = docMgr.write(docId, null, handle3, null, null, temporalCollection);

		assertNotNull(desc);
		assertEquals(docId, desc.getUri());
		String thirdWriteTimestamp = desc.getTemporalSystemTime();
		assertNotNull(thirdWriteTimestamp);

		StringHandle handle4 = new StringHandle(version4).withFormat(Format.XML);
		docMgr.write(docId, null, handle4, null, null, temporalCollection);

		// make sure non-temporal document read only returns the latest version
		try (DocumentPage readResults = docMgr.read(docId)) {
			assertEquals(1, readResults.size());
			DocumentRecord latestDoc = readResults.next();
			assertEquals(docId, latestDoc.getUri());
		}

		// make sure a simple term query returns all versions of bulk and other docs
		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition termsQuery =
			sqb.or(sqb.term(uniqueTerm), sqb.term(uniqueBulkTerm));
		long start = 1;
		try (DocumentPage termQueryResults = docMgr.search(termsQuery, start)) {
			assertEquals(4, termQueryResults.size());
		}

		StructuredQueryDefinition currentQuery = sqb.temporalLsqtQuery(temporalCollection, thirdWriteTimestamp, 1);
		StructuredQueryDefinition currentDocQuery = sqb.and(termsQuery, currentQuery);

		final StructuredQueryDefinition queryThatWillFail = currentDocQuery;
		FailedRequestException ex = assertThrows(FailedRequestException.class, () -> docMgr.search(queryThatWillFail, start));
		String message = ex.getMessage();
		assertTrue(message.contains("TEMPORAL"), "The query should fail, but the actual error code " +
			"depends on the MarkLogic version. Prior to 12.1, the code was TEMPORAL-GTLSQT. " +
			"On the develop branch for 12.1, it's TEMPORAL-NOLSQT. Actual message: " + message);

		try (DatabaseClient client = Common.newServerAdminClient()) {
			client.newXMLDocumentManager().advanceLsqt(temporalCollection);
		}

		// query again with lsqt of last inserted document
		// will match the first three versions -- not the last because it's equal to
		// not greater than the timestamp of this lsqt query
		try (DocumentPage currentDocQueryResults = docMgr.search(currentDocQuery, start)) {
			assertEquals(3, currentDocQueryResults.size());
		}

		// query with blank lsqt indicating current time
		// will match all four versions
		currentQuery = sqb.temporalLsqtQuery(temporalCollection, "", 1);
		currentDocQuery = sqb.and(termsQuery, currentQuery);
		try (DocumentPage currentDocQueryResults = docMgr.search(currentDocQuery, start)) {
			assertEquals(4, currentDocQueryResults.size());
		}

		StructuredQueryBuilder.Axis validAxis = sqb.axis("valid-axis");

		// create a time axis to query the versions against
		Calendar start1 = DatatypeConverter.parseDateTime("2014-08-19T00:00:00Z");
		Calendar end1 = DatatypeConverter.parseDateTime("2014-08-19T00:00:04Z");
		StructuredQueryBuilder.Period period1 = sqb.period(start1, end1);

		// find all documents contained in the time range of our query axis
		StructuredQueryDefinition periodQuery1 = sqb.and(termsQuery,
			sqb.temporalPeriodRange(validAxis, TemporalOperator.ALN_CONTAINED_BY, period1));
		try (DocumentPage periodQuery1Results = docMgr.search(periodQuery1, start)) {
			assertEquals(1, periodQuery1Results.size());
		}

		// create a second time axis to query the versions against
		Calendar start2 = DatatypeConverter.parseDateTime("2014-08-19T00:00:04Z");
		Calendar end2 = DatatypeConverter.parseDateTime("2014-08-19T00:00:07Z");
		StructuredQueryBuilder.Period period2 = sqb.period(start2, end2);

		// find all documents contained in the time range of our second query axis
		StructuredQueryDefinition periodQuery2 = sqb.and(termsQuery,
			sqb.temporalPeriodRange(validAxis, TemporalOperator.ALN_CONTAINED_BY, period2));
		try (DocumentPage periodQuery2Results = docMgr.search(periodQuery2, start)) {
			assertEquals(1, periodQuery2Results.size());
			for (DocumentRecord result : periodQuery2Results) {
				if (docId.equals(result.getUri())) {
					continue;
				} else if (result.getUri().startsWith("test_" + uniqueBulkTerm + "_4")) {
					continue;
				}
				fail("Unexpected uri for ALN_CONTAINED_BY test:" + result.getUri());
			}
		}

		// find all documents where valid time is after system time in the document
		StructuredQueryBuilder.Axis systemAxis = sqb.axis("system-axis");
		StructuredQueryDefinition periodCompareQuery1 = sqb.and(termsQuery,
			sqb.temporalPeriodCompare(systemAxis, TemporalOperator.ALN_AFTER, validAxis));
		try (DocumentPage periodCompareQuery1Results = docMgr.search(periodCompareQuery1, start)) {
			assertEquals(4, periodCompareQuery1Results.size());
		}

		// find all documents where valid time is before system time in the document
		StructuredQueryDefinition periodCompareQuery2 = sqb.and(termsQuery,
			sqb.temporalPeriodCompare(systemAxis, TemporalOperator.ALN_BEFORE, validAxis));
		try (DocumentPage periodCompareQuery2Results = docMgr.search(periodCompareQuery2, start)) {
			assertEquals(0, periodCompareQuery2Results.size());
		}

		// check that we get a system time when we delete
		desc = docMgr.delete(docId, null, temporalCollection);
		assertNotNull(desc);
		assertEquals(docId, desc.getUri());
		assertNotNull(desc.getTemporalSystemTime());
	}
}
