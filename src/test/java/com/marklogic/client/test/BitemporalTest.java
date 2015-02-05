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

import java.util.Calendar;

import javax.xml.bind.DatatypeConverter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.TemporalOperator;
import com.marklogic.client.query.StructuredQueryDefinition;

public class BitemporalTest {
	// src/test/resources/bootstrap.xqy is run by src/test/resources/boot-test.sh
	// and sets up the "temporal-collection" and required underlying axes
	// system-axis and valid-axis which have required underlying range indexes
	// system-start, system-end, valid-start, and valid-end
	static String temporalCollection = "temporal-collection";
	static String uniqueTerm = "temporalDoc";
	static String docId = "test-" + uniqueTerm + ".xml";

	@BeforeClass
    public static void beforeClass() {
        Common.connect();
        //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
    }
    @AfterClass
    public static void afterClass() {
        Common.release();
        cleanUp();
    }

	@Test
	public void test1() throws Exception {

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
		XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
		StringHandle handle1 = new StringHandle(version1).withFormat(Format.XML);
		docMgr.write(docId, null, handle1, null, null, temporalCollection);
		StringHandle handle2 = new StringHandle(version2).withFormat(Format.XML);
		docMgr.write(docId, null, handle2, null, null, temporalCollection);
		StringHandle handle3 = new StringHandle(version3).withFormat(Format.XML);
		docMgr.write(docId, null, handle3, null, null, temporalCollection);
		StringHandle handle4 = new StringHandle(version4).withFormat(Format.XML);
		docMgr.write(docId, null, handle4, null, null, temporalCollection);

		DocumentPage readResults = docMgr.read(docId); 
		assertEquals("Wrong number of results", 1, readResults.size());
		DocumentRecord latestDoc = readResults.next();
		assertEquals("Document uri wrong", docId, latestDoc.getUri());
		
		QueryManager queryMgr = Common.client.newQueryManager();
		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition termQuery = sqb.term(uniqueTerm);
		long start = 1;
		DocumentPage termQueryResults = docMgr.search(termQuery, start);
		assertEquals("Wrong number of results", 4, termQueryResults.size());

		// temporal-collection is configured to automatically advance lsqt every 1 second
		// so we'll sleep for 2 seconds to make sure lsqt has advanced beyond the lsqt
		// when we inserted our documents
		Thread.sleep(2000);
		StructuredQueryDefinition currentQuery = sqb.temporalLsqtQuery(temporalCollection, null, 1);
		StructuredQueryDefinition currentDocQuery = sqb.and(termQuery, currentQuery);
		DocumentPage currentDocQueryResults = docMgr.search(currentDocQuery, start);
		assertEquals("Wrong number of results", 4, currentDocQueryResults.size());

		StructuredQueryBuilder.Axis validAxis = sqb.axis("valid-axis");
		Calendar start1 = DatatypeConverter.parseDateTime("2014-08-19T00:00:00Z");
		Calendar end1   = DatatypeConverter.parseDateTime("2014-08-19T00:00:04Z");
		StructuredQueryBuilder.Period period1 = sqb.period(start1, end1);
		StructuredQueryDefinition periodQuery1 = sqb.and(sqb.term(uniqueTerm),
			sqb.temporalPeriodRange(validAxis, TemporalOperator.ALN_CONTAINED_BY, period1));
		DocumentPage periodQuery1Results = docMgr.search(periodQuery1, start);
		assertEquals("Wrong number of results", 1, periodQuery1Results.size());

		Calendar start2 = DatatypeConverter.parseDateTime("2014-08-19T00:00:04Z");
		Calendar end2   = DatatypeConverter.parseDateTime("2014-08-19T00:00:07Z");
		StructuredQueryBuilder.Period period2 = sqb.period(start2, end2);
		StructuredQueryDefinition periodQuery2 = sqb.and(sqb.term(uniqueTerm),
			sqb.temporalPeriodRange(validAxis, TemporalOperator.ALN_CONTAINED_BY, period2));
		DocumentPage periodQuery2Results = docMgr.search(periodQuery2, start);
		assertEquals("Wrong number of results", 1, periodQuery2Results.size());
		latestDoc = periodQuery2Results.next();
		assertEquals("Document uri wrong", docId, latestDoc.getUri());

		StructuredQueryBuilder.Axis systemAxis = sqb.axis("system-axis");
		StructuredQueryDefinition periodCompareQuery1 = sqb.and(sqb.term(uniqueTerm),
			sqb.temporalPeriodCompare(systemAxis, TemporalOperator.ALN_AFTER, validAxis));
		DocumentPage periodCompareQuery1Results = docMgr.search(periodCompareQuery1, start);
		assertEquals("Wrong number of results", 4, periodCompareQuery1Results.size());

		StructuredQueryDefinition periodCompareQuery2 = sqb.and(sqb.term(uniqueTerm),
			sqb.temporalPeriodCompare(systemAxis, TemporalOperator.ALN_BEFORE, validAxis));
		DocumentPage periodCompareQuery2Results = docMgr.search(periodCompareQuery2, start);
		assertEquals("Wrong number of results", 0, periodCompareQuery2Results.size());

	}

	static public void cleanUp() {
		DatabaseClient client = DatabaseClientFactory.newClient(Common.HOST, Common.PORT, "admin", "admin", DatabaseClientFactory.Authentication.DIGEST);
		QueryManager queryMgr = client.newQueryManager();
		queryMgr.setPageLength(1000);
		QueryDefinition query = queryMgr.newStringDefinition();
		query.setCollections(docId);
//		DeleteQueryDefinition deleteQuery = client.newQueryManager().newDeleteDefinition();
//		deleteQuery.setCollections(temporalCollection);
//		client.newQueryManager().delete(deleteQuery);
		SearchHandle handle = queryMgr.search(query, new SearchHandle());
		MatchDocumentSummary[] docs = handle.getMatchResults();
		for ( MatchDocumentSummary doc : docs ) {
			client.newXMLDocumentManager().delete(doc.getUri());
		}
	}
}
