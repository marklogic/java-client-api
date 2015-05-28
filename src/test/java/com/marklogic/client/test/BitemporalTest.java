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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;

import javax.xml.bind.DatatypeConverter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.bitemporal.TemporalDescriptor;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.TemporalOperator;
import com.marklogic.client.query.StructuredQueryDefinition;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BitemporalTest {
	// src/test/resources/bootstrap.xqy is run by src/test/resources/boot-test.sh
	// and sets up the "temporal-collection" and required underlying axes
	// system-axis and valid-axis which have required underlying range indexes
	// system-start, system-end, valid-start, and valid-end
	static String temporalCollection = "temporal-collection";
	static XMLDocumentManager docMgr;
	static QueryManager queryMgr;
	static String uniqueBulkTerm = "temporalBulkDoc";
	static String uniqueTerm = "temporalDoc";
	static String docId = "test-" + uniqueTerm + ".xml";

	@BeforeClass
    public static void beforeClass() {
        Common.connect();
        //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
        docMgr = Common.client.newXMLDocumentManager();
        queryMgr = Common.client.newQueryManager();
    }
    @AfterClass
    public static void afterClass() {
        Common.release();
        cleanUp();
    }

    public static void writeBulkDocs() {
    }

	@Test
	public void a_testCreate() throws Exception {
		String contents = "<test>" +
				"<system-start></system-start>" +
				"<system-end></system-end>" +
				"<valid-start>2014-08-19T00:00:00Z</valid-start>" +
				"<valid-end>2014-08-19T00:00:01Z</valid-end>" +
    		"</test>";
		TemporalDescriptor desc = docMgr.create(docMgr.newDocumentUriTemplate("xml"), 
			null, new StringHandle(contents), null, null, temporalCollection);
		assertNotNull("Missing TemporalDescriptor from create", desc);
		assertNotNull(desc.getUri());
		assertTrue(desc.getUri().endsWith(".xml"));
		String lastWriteTimestamp = desc.getTemporalSystemTime();
		Calendar lastWriteTime = DatatypeConverter.parseDateTime(lastWriteTimestamp);
		assertNotNull(lastWriteTime);
    }

	@Test
	public void b_testBulk() throws Exception {
		String prefix = "test_" + uniqueBulkTerm;
		String doc1 = "<test>" +
				uniqueBulkTerm + " doc1" +
				"<system-start></system-start>" +
				"<system-end></system-end>" +
				"<valid-start>2014-08-19T00:00:00Z</valid-start>" +
				"<valid-end>2014-08-19T00:00:01Z</valid-end>" +
			"</test>";
		String doc2 = "<test>" +
				uniqueBulkTerm + " doc2" +
				"<system-start></system-start>" +
				"<system-end></system-end>" +
				"<valid-start>2014-08-19T00:00:02Z</valid-start>" +
				"<valid-end>2014-08-19T00:00:03Z</valid-end>" +
			"</test>";
		String doc3 = "<test>" +
				uniqueBulkTerm + " doc3" +
				"<system-start></system-start>" +
				"<system-end></system-end>" +
				"<valid-start>2014-08-19T00:00:03Z</valid-start>" +
				"<valid-end>2014-08-19T00:00:04Z</valid-end>" +
			"</test>";
		String doc4 = "<test>" +
				uniqueBulkTerm + " doc4" +
				"<system-start></system-start>" +
				"<system-end></system-end>" +
				"<valid-start>2014-08-19T00:00:05Z</valid-start>" +
				"<valid-end>2014-08-19T00:00:06Z</valid-end>" +
			"</test>";
		DocumentWriteSet writeSet = docMgr.newWriteSet();
		writeSet.add(prefix + "_1.xml", new StringHandle(doc1).withFormat(Format.XML));
		writeSet.add(prefix + "_2.xml", new StringHandle(doc2).withFormat(Format.XML));
		writeSet.add(prefix + "_3.xml", new StringHandle(doc3).withFormat(Format.XML));
		writeSet.add(prefix + "_4.xml", new StringHandle(doc4).withFormat(Format.XML));
		docMgr.write(writeSet, null, null, temporalCollection);
		// do it one more time so we have two versions of each
		writeSet = docMgr.newWriteSet();
		writeSet.add(prefix + "_1.xml", new StringHandle(doc1).withFormat(Format.XML));
		writeSet.add(prefix + "_2.xml", new StringHandle(doc2).withFormat(Format.XML));
		writeSet.add(prefix + "_3.xml", new StringHandle(doc3).withFormat(Format.XML));
		writeSet.add(prefix + "_4.xml", new StringHandle(doc4).withFormat(Format.XML));
		docMgr.write(writeSet, null, null, temporalCollection);

		StringQueryDefinition query = queryMgr.newStringDefinition().withCriteria(uniqueBulkTerm);
		DocumentPage page = docMgr.search(query, 0);
		assertEquals("Wrong number of results", 8, page.size());
		for ( DocumentRecord record : page ) {
			Document doc = record.getContentAs(Document.class);
			if ( record.getUri().startsWith(prefix + "_1") ) {
				assertXpathEvaluatesTo("2014-08-19T00:00:00Z", "//valid-start", doc);
				continue;
			} else if ( record.getUri().startsWith(prefix + "_2") ) {
				assertXpathEvaluatesTo("2014-08-19T00:00:02Z", "//valid-start", doc);
				continue;
			} else if ( record.getUri().startsWith(prefix + "_3") ) {
				assertXpathEvaluatesTo("2014-08-19T00:00:03Z", "//valid-start", doc);
				continue;
			} else if ( record.getUri().startsWith(prefix + "_4") ) {
				assertXpathEvaluatesTo("2014-08-19T00:00:05Z", "//valid-start", doc);
				continue;
			}
			throw new IllegalStateException("Unexpected doc:[" + record.getUri() + "]");
		}
    }

	@Test
	public void c_testOther() throws Exception {

		String version1 = "<test>" +
				uniqueTerm + " version1" +
				"<system-start></system-start>" +
				"<system-end></system-end>" +
				"<valid-start>2014-08-19T00:00:00Z</valid-start>" +
				"<valid-end>2014-08-19T00:00:01Z</valid-end>" +
    		"</test>";
		String version2 = "<test>" +
				uniqueTerm + " version2" +
				"<system-start></system-start>" +
				"<system-end></system-end>" +
				"<valid-start>2014-08-19T00:00:02Z</valid-start>" +
				"<valid-end>2014-08-19T00:00:03Z</valid-end>" +
    		"</test>";
		String version3 = "<test>" +
				uniqueTerm + " version3" +
				"<system-start></system-start>" +
				"<system-end></system-end>" +
				"<valid-start>2014-08-19T00:00:03Z</valid-start>" +
				"<valid-end>2014-08-19T00:00:04Z</valid-end>" +
    		"</test>";
		String version4 = "<test>" +
				uniqueTerm + " version4" +
				"<system-start></system-start>" +
				"<system-end></system-end>" +
				"<valid-start>2014-08-19T00:00:05Z</valid-start>" +
				"<valid-end>2014-08-19T00:00:06Z</valid-end>" +
    		"</test>";

		// write four versions of the same document
		StringHandle handle1 = new StringHandle(version1).withFormat(Format.XML);
		docMgr.write(docId, null, handle1, null, null, temporalCollection);
		StringHandle handle2 = new StringHandle(version2).withFormat(Format.XML);
		docMgr.write(docId, null, handle2, null, null, temporalCollection);
		StringHandle handle3 = new StringHandle(version3).withFormat(Format.XML);
		TemporalDescriptor desc = docMgr.write(docId, null, handle3, null, null, temporalCollection);
		assertNotNull("Missing TemporalDescriptor from write", desc);
		assertEquals(docId, desc.getUri());
		String thirdWriteTimestamp = desc.getTemporalSystemTime();
		Calendar thirdWriteTime = DatatypeConverter.parseDateTime(thirdWriteTimestamp);
		assertNotNull(thirdWriteTime);
		// add one millisecond since server precision is more precise, so Java truncates
		// fractions of a second here.  Adding one millisecond ensures we're above the 
		// system time for the last write for the document
		thirdWriteTime.roll(Calendar.MILLISECOND, true);

		StringHandle handle4 = new StringHandle(version4).withFormat(Format.XML);
		docMgr.write(docId, null, handle4, null, null, temporalCollection);

		// make sure non-temporal document read only returns the latest version
		DocumentPage readResults = docMgr.read(docId); 
		assertEquals("Wrong number of results", 1, readResults.size());
		DocumentRecord latestDoc = readResults.next();
		assertEquals("Document uri wrong", docId, latestDoc.getUri());
		
		// make sure a simple term query returns all versions of bulk and other docs
		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition termsQuery = 
			sqb.or( sqb.term(uniqueTerm), sqb.term(uniqueBulkTerm) );
		long start = 1;
		DocumentPage termQueryResults = docMgr.search(termsQuery, start);
		assertEquals("Wrong number of results", 12, termQueryResults.size());

		// temporal-collection is configured to automatically advance lsqt every 1 second
		// so we'll sleep for 2 seconds to make sure lsqt has advanced beyond the lsqt
		// when we inserted our documents
		Thread.sleep(2000);

		// query with lsqt of last inserted document
		// will match the first three versions -- not the last because it's equal to
		// not greater than the timestamp of this lsqt query
		StructuredQueryDefinition currentQuery = sqb.temporalLsqtQuery(temporalCollection, thirdWriteTime, 1);
		StructuredQueryDefinition currentDocQuery = sqb.and(termsQuery, currentQuery);
		DocumentPage currentDocQueryResults = docMgr.search(currentDocQuery, start);
		assertEquals("Wrong number of results", 11, currentDocQueryResults.size());

		// query with blank lsqt indicating current time
		// will match all four versions
		currentQuery = sqb.temporalLsqtQuery(temporalCollection, "", 1);
		currentDocQuery = sqb.and(termsQuery, currentQuery);
		currentDocQueryResults = docMgr.search(currentDocQuery, start);
		assertEquals("Wrong number of results", 12, currentDocQueryResults.size());

		StructuredQueryBuilder.Axis validAxis = sqb.axis("valid-axis");

		// create a time axis to query the versions against
		Calendar start1 = DatatypeConverter.parseDateTime("2014-08-19T00:00:00Z");
		Calendar end1   = DatatypeConverter.parseDateTime("2014-08-19T00:00:04Z");
		StructuredQueryBuilder.Period period1 = sqb.period(start1, end1);

		// find all documents contained in the time range of our query axis
		StructuredQueryDefinition periodQuery1 = sqb.and(termsQuery,
			sqb.temporalPeriodRange(validAxis, TemporalOperator.ALN_CONTAINED_BY, period1));
		DocumentPage periodQuery1Results = docMgr.search(periodQuery1, start);
		assertEquals("Wrong number of results", 3, periodQuery1Results.size());

		// create a second time axis to query the versions against
		Calendar start2 = DatatypeConverter.parseDateTime("2014-08-19T00:00:04Z");
		Calendar end2   = DatatypeConverter.parseDateTime("2014-08-19T00:00:07Z");
		StructuredQueryBuilder.Period period2 = sqb.period(start2, end2);

		// find all documents contained in the time range of our second query axis
		StructuredQueryDefinition periodQuery2 = sqb.and(termsQuery,
			sqb.temporalPeriodRange(validAxis, TemporalOperator.ALN_CONTAINED_BY, period2));
		DocumentPage periodQuery2Results = docMgr.search(periodQuery2, start);
		assertEquals("Wrong number of results", 3, periodQuery2Results.size());
		for ( DocumentRecord result : periodQuery2Results ) {
			if ( docId.equals(result.getUri()) ) {
				continue;
			} else if ( result.getUri().startsWith("test_" + uniqueBulkTerm + "_4") ) {
				continue;
			}
			fail("Unexpected uri for ALN_CONTAINED_BY test:" + result.getUri());
		}

		// find all documents where valid time is after system time in the document
		StructuredQueryBuilder.Axis systemAxis = sqb.axis("system-axis");
		StructuredQueryDefinition periodCompareQuery1 = sqb.and(termsQuery,
			sqb.temporalPeriodCompare(systemAxis, TemporalOperator.ALN_AFTER, validAxis));
		DocumentPage periodCompareQuery1Results = docMgr.search(periodCompareQuery1, start);
		assertEquals("Wrong number of results", 12, periodCompareQuery1Results.size());

		// find all documents where valid time is before system time in the document
		StructuredQueryDefinition periodCompareQuery2 = sqb.and(termsQuery,
			sqb.temporalPeriodCompare(systemAxis, TemporalOperator.ALN_BEFORE, validAxis));
		DocumentPage periodCompareQuery2Results = docMgr.search(periodCompareQuery2, start);
		assertEquals("Wrong number of results", 0, periodCompareQuery2Results.size());

		// check that we get a system time when we delete
		desc = docMgr.delete(docId, null, temporalCollection);
		assertNotNull("Missing TemporalDescriptor from delete", desc);
		assertEquals(docId, desc.getUri());
		assertNotNull("Missing temporalSystemTime from delete", desc.getTemporalSystemTime());

	}

	static public void cleanUp() {
		DatabaseClient client = DatabaseClientFactory.newClient(Common.HOST, Common.PORT, "admin", "admin", DatabaseClientFactory.Authentication.DIGEST);
		QueryManager queryMgr = client.newQueryManager();
		queryMgr.setPageLength(1000);
		QueryDefinition query = queryMgr.newStringDefinition();
		query.setCollections(temporalCollection);
//		DeleteQueryDefinition deleteQuery = client.newQueryManager().newDeleteDefinition();
//		deleteQuery.setCollections(temporalCollection);
//		client.newQueryManager().delete(deleteQuery);
		SearchHandle handle = queryMgr.search(query, new SearchHandle());
		MatchDocumentSummary[] docs = handle.getMatchResults();
		for ( MatchDocumentSummary doc : docs ) {
			if ( ! (temporalCollection + ".lsqt").equals(doc.getUri()) ) {
				client.newXMLDocumentManager().delete(doc.getUri());
			}
		}
	}
}
