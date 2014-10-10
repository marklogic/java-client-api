/*
 * Copyright 2012-2014 MarkLogic Corporation
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

import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.TemporalOperator;
import com.marklogic.client.query.StructuredQueryDefinition;

public class BitemporalTest {
    @BeforeClass
    public static void beforeClass() {
        Common.connect();
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
    }
    @AfterClass
    public static void afterClass() {
        Common.release();
    }

	@Test
	public void test1() {
		// bootstrap sets up the "temporal-collection" and required underlying axes
		// system-axis and valid-axis which have required underlying range indexes
		// system-start, system-end, valid-start, and valid-end
		String temporalCollection = "temporal-collection";
		String uniqueTerm = "temporalDoc1";
		String version1 = "<test>" +
				uniqueTerm + " version1" +
				"<system-start></system-start>" +
				"<system-end></system-end>" +
				"<valid-start>2014-08-19T00:00:00</valid-start>" +
				"<valid-end>2014-08-19T00:00:01</valid-end>" +
    		"</test>";
		String version2 = "<test>" +
				uniqueTerm + " version2" +
				"<system-start></system-start>" +
				"<system-end></system-end>" +
				"<valid-start>2014-08-19T00:00:02</valid-start>" +
				"<valid-end>2014-08-19T00:00:03</valid-end>" +
    		"</test>";
		String version3 = "<test>" +
				uniqueTerm + " version3" +
				"<system-start></system-start>" +
				"<system-end></system-end>" +
				"<valid-start>2014-08-19T00:00:03</valid-start>" +
				"<valid-end>2014-08-19T00:00:04</valid-end>" +
    		"</test>";
		String version4 = "<test>" +
				uniqueTerm + " version4" +
				"<system-start></system-start>" +
				"<system-end></system-end>" +
				"<valid-start>2014-08-19T00:00:05</valid-start>" +
				"<valid-end>2014-08-19T00:00:06</valid-end>" +
    		"</test>";
		String docId = "test-temporal1.xml";
		XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
		StringHandle handle1 = new StringHandle(version1).withFormat(Format.XML);
		docMgr.write(docId, null, handle1, null, null, temporalCollection, null);
		StringHandle handle2 = new StringHandle(version2).withFormat(Format.XML);
		docMgr.write(docId, null, handle2, null, null, temporalCollection, null);
		StringHandle handle3 = new StringHandle(version3).withFormat(Format.XML);
		docMgr.write(docId, null, handle3, null, null, temporalCollection, null);
		StringHandle handle4 = new StringHandle(version4).withFormat(Format.XML);
		docMgr.write(docId, null, handle4, null, null, temporalCollection, null);

		/*
		DocumentPage readResults = docMgr.read(null, null, temporalCollection, new String[] {docId}); 
		assertEquals("Wrong number of results", 1, readResults.size());
		DocumentRecord latestDoc = readResults.next();
		assertEquals("Document uri wrong", docId, latestDoc.getUri());
		*/

		QueryManager queryMgr = Common.client.newQueryManager();
		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition termQuery = sqb.term(uniqueTerm);
		long start = 1;
		DocumentPage termQueryResults = docMgr.search(termQuery, start);
		assertEquals("Wrong number of results", 4, termQueryResults.size());

		StructuredQueryDefinition currentQuery = sqb.temporalCurrent(temporalCollection, null, 1);
		StructuredQueryDefinition currentDocQuery = sqb.and(termQuery, currentQuery);
		DocumentPage currentDocQueryResults = docMgr.search(currentDocQuery, start);
		assertEquals("Wrong number of results", 1, currentDocQueryResults.size());
		DocumentRecord latestDoc = currentDocQueryResults.next();
		assertEquals("Document uri wrong", docId, latestDoc.getUri());

		StructuredQueryBuilder.Axis validAxis = sqb.axis("valid-axis");
		Calendar start1 = DatatypeConverter.parseDateTime("2014-08-19T00:00:00");
		Calendar end1 = DatatypeConverter.parseDateTime("2014-08-19T00:00:04");
		StructuredQueryBuilder.Period period1 = sqb.period(start1, end1);
		StructuredQueryDefinition periodQuery1 = 
			sqb.temporalPeriodRange(validAxis, TemporalOperator.ALN_CONTAINED_BY, period1);
		DocumentPage periodQuery1Results = docMgr.search(currentDocQuery, start);
		assertEquals("Wrong number of results", 3, periodQuery1Results.size());

		Calendar start2 = DatatypeConverter.parseDateTime("2014-08-19T00:00:05");
		Calendar end2 = DatatypeConverter.parseDateTime("2014-08-19T00:00:06");
		StructuredQueryBuilder.Period period2 = sqb.period(start2, end2);
		StructuredQueryDefinition periodQuery2 = 
			sqb.temporalPeriodRange(validAxis, TemporalOperator.ALN_CONTAINED_BY, period2);
		DocumentPage periodQuery2Results = docMgr.search(currentDocQuery, start);
		assertEquals("Wrong number of results", 1, periodQuery2Results.size());
		latestDoc = periodQuery2Results.next();
		assertEquals("Document uri wrong", docId, latestDoc.getUri());

		StructuredQueryBuilder.Axis systemAxis = sqb.axis("system-axis");
		StructuredQueryDefinition periodCompareQuery1 = 
			sqb.temporalPeriodCompare(systemAxis, TemporalOperator.ALN_AFTER, validAxis);
		DocumentPage periodCompareQuery1Results = docMgr.search(periodCompareQuery1, start);
		assertEquals("Wrong number of results", 4, periodCompareQuery1Results.size());

		StructuredQueryDefinition periodCompareQuery2 = 
			sqb.temporalPeriodCompare(systemAxis, TemporalOperator.ALN_BEFORE, validAxis);
		DocumentPage periodCompareQuery2Results = docMgr.search(periodCompareQuery2, start);
		assertEquals("Wrong number of results", 0, periodCompareQuery2Results.size());
	}

}
