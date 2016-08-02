/*
 * Copyright 2016 MarkLogic Corporation
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

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowRecord.ColumnKind;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.type.CtsReferenceExpr;
import com.marklogic.client.type.PlanParam;
import com.marklogic.client.util.EditableNamespaceContext;

public class RowManagerTest {
    private static String[]             uris    = null;
    private static String[]             docs    = null;
    private static Map<String,Object>[] litRows = null;
    private static String[][]           triples = null;
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void beforeClass() throws IOException, InterruptedException {
        uris = new String[]{"/rowtest/docJoin1.json", "/rowtest/docJoin1.xml", "/rowtest/docJoin1.txt"};
        docs = new String[]{
        		"{\"a\":{\"b\":[\"c\", 4]}}",
        		"<a><b>c</b>4</a>",
        		"a b c 4"
        		};

		litRows = new Map[3];

		Map<String,Object>   row  = new HashMap<String,Object>();
		row.put("rowNum", 1);
		row.put("city",   "New York");
		row.put("temp",   "82");
		row.put("uri",    uris[0]);
		litRows[0] = row;

		row = new HashMap<String,Object>();
		row.put("rowNum", 2);
		row.put("city",   "Seattle");
		row.put("temp",   "72");
		row.put("uri",    uris[1]);
		litRows[1] = row;

		row = new HashMap<String,Object>();
		row.put("rowNum", 3);
		row.put("city",   "Phoenix");
		row.put("temp",   "92");
		row.put("uri",    uris[2]);
		litRows[2] = row;

	    triples = new String[][]{
	    		new String[]{"http://example.org/rowgraph/s1", "http://example.org/rowgraph/p1", "http://example.org/rowgraph/o1"},
	    		new String[]{"http://example.org/rowgraph/s1", "http://example.org/rowgraph/p2", "http://example.org/rowgraph/o2"},
	    		new String[]{"http://example.org/rowgraph/s2", "http://example.org/rowgraph/p1", "http://example.org/rowgraph/o3"},
	    		new String[]{"http://example.org/rowgraph/s2", "http://example.org/rowgraph/p2", "http://example.org/rowgraph/o4"}
	        };

        Common.connect();

        @SuppressWarnings("rawtypes")
		DocumentManager docMgr = Common.client.newDocumentManager();

        String triplesXML =
        	"<sem:triples xmlns:sem=\"http://marklogic.com/semantics\">\n"+
        	String.join("\n", (String[]) Arrays
        		.stream(triples)
        		.map(triple ->
                	"<sem:triple>"+
	                "<sem:subject>"+triple[0]+"</sem:subject>"+
    	            "<sem:predicate>"+triple[1]+"</sem:predicate>"+
        	        "<sem:object>"+triple[2]+"</sem:object>"+
            	    "</sem:triple>"
                	)
	        	.toArray(size -> new String[size])
    	    	)+
        	"</sem:triples>";
        docMgr.write(
        	docMgr.newWriteSet()
                  .add(uris[0], new StringHandle(docs[0]).withFormat(Format.JSON))
                  .add(uris[1], new StringHandle(docs[1]).withFormat(Format.XML))
                  .add(uris[2], new StringHandle(docs[2]).withFormat(Format.TEXT))
                  .add("/rowtest/triples1.xml", new StringHandle(triplesXML).withFormat(Format.TEXT))
              );
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testResultDoc() throws IOException, XPathExpressionException {
		RowManager rowMgr = Common.client.newRowManager();

		PlanBuilder p = rowMgr.newPlanBuilder();
		PlanBuilder.ExportablePlan builtPlan =
				p.fromLiterals(litRows)
				 .orderBy(p.col("rowNum"))
				 .where(p.eq(p.col("city"), p.xs.string("Seattle")))
				 .select(p.cols("rowNum", "temp"));

		StringHandle planHandle = builtPlan.export(new StringHandle()).withFormat(Format.JSON);
		RawPlanDefinition rawPlan = rowMgr.newRawPlanDefinition(planHandle);
		
		for (PlanBuilder.Plan plan: new PlanBuilder.Plan[]{builtPlan, rawPlan}) {
			ReaderHandle readerHandle = new ReaderHandle();

			rowMgr.resultDoc(plan, readerHandle.withMimetype("text/csv"));

			LineNumberReader lineReader = new LineNumberReader(readerHandle.get());

			String[] cols = lineReader.readLine().split(",");
			assertArrayEquals("unexpected header", cols, new String[]{"rowNum","temp"});

			cols = lineReader.readLine().split(",");
			assertArrayEquals("unexpected data", cols, new String[]{"2","72"});

			lineReader.close();
			readerHandle.close();

	        DOMHandle domHandle = initNamespaces(rowMgr.resultDoc(plan, new DOMHandle()));

	        NodeList testList = domHandle.evaluateXPath("/sp:sparql/sp:head/sp:variable", NodeList.class);
	        assertEquals("unexpected header count in XML", 2, testList.getLength());
	        Element testElement = (Element) testList.item(0);
	        assertEquals("unexpected first header name in XML", "rowNum", testElement.getAttribute("name"));
	        testElement = (Element) testList.item(1);
	        assertEquals("unexpected second header name in XML", "temp", testElement.getAttribute("name"));

	        testList = domHandle.evaluateXPath("/sp:sparql/sp:results/sp:result", NodeList.class);
	        assertEquals("unexpected row count in XML", 1, testList.getLength());

	        testList = domHandle.evaluateXPath("/sp:sparql/sp:results/sp:result[1]/sp:binding", NodeList.class);
	        checkSingleRow(testList);

	        JsonNode testNode = rowMgr.resultDoc(plan, new JacksonHandle()).get();

	        JsonNode arrayNode = testNode.findValue("vars");
	        assertEquals("unexpected header count in JSON", 2, arrayNode.size());
	        
	        assertEquals("unexpected first header name in JSON",  "rowNum", arrayNode.get(0).asText());
	        assertEquals("unexpected second header name in JSON", "temp",   arrayNode.get(1).asText());
	        

	        arrayNode = testNode.findValue("bindings");
	        assertEquals("unexpected row count in JSON", 1, arrayNode.size());

	        checkSingleRow(arrayNode.get(0));
		}
	}
	@Test
	public void testResultRows() throws IOException, XPathExpressionException {
		RowManager rowMgr = Common.client.newRowManager();

		PlanBuilder p = rowMgr.newPlanBuilder();
		PlanBuilder.ExportablePlan builtPlan =
				p.fromLiterals(litRows)
				 .orderBy(p.col("rowNum"))
				 .where(p.eq(p.col("city"), p.xs.string("Seattle")))
				 .select(p.cols("rowNum", "temp"));

		StringHandle planHandle = builtPlan.export(new StringHandle()).withFormat(Format.JSON);
		RawPlanDefinition rawPlan = rowMgr.newRawPlanDefinition(planHandle);
		
		for (PlanBuilder.Plan plan: new PlanBuilder.Plan[]{builtPlan, rawPlan}) {
			RowSet<DOMHandle> xmlRowSet = rowMgr.resultRows(plan, new DOMHandle());

			Iterator<DOMHandle> xmlRowItr = xmlRowSet.iterator();
			assertTrue("no XML row to iterate", xmlRowItr.hasNext());
			DOMHandle xmlRow = initNamespaces(xmlRowItr.next());
	        checkSingleRow(xmlRow.evaluateXPath("/sp:result/sp:binding", NodeList.class));
	        assertFalse("expected one XML row", xmlRowItr.hasNext());

	        xmlRowSet.close();

			RowSet<JacksonHandle> jsonRowSet = rowMgr.resultRows(plan, new JacksonHandle());

			Iterator<JacksonHandle> jsonRowItr = jsonRowSet.iterator();
			assertTrue("no JSON row to iterate", jsonRowItr.hasNext());
			JacksonHandle jsonRow = jsonRowItr.next();
	        checkSingleRow(jsonRow.get());
	        assertFalse("expected one JSON row", jsonRowItr.hasNext());

	        jsonRowSet.close();

			RowSet<RowRecord> recordRowSet = rowMgr.resultRows(plan);
			Iterator<RowRecord> recordRowItr = recordRowSet.iterator();
			assertTrue("no record row to iterate", recordRowItr.hasNext());
			RowRecord recordRow = recordRowItr.next();
			checkSingleRow(recordRow);
	        assertFalse("expected one record row", recordRowItr.hasNext());

	        recordRowSet.close();
		}
	}
	@Test
	public void testResultRowDocs()
	throws IOException, XPathExpressionException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, SAXException
	{
		RowManager rowMgr = Common.client.newRowManager();

		PlanBuilder p = rowMgr.newPlanBuilder();
		PlanBuilder.ExportablePlan builtPlan =
				p.fromLiterals(litRows)
				 .orderBy(p.col("rowNum"))
				 .joinLeftOuterDoc("doc", "uri")
				 .select(p.cols("rowNum", "uri", "doc"));

		StringHandle planHandle = builtPlan.export(new StringHandle()).withFormat(Format.JSON);
		RawPlanDefinition rawPlan = rowMgr.newRawPlanDefinition(planHandle);
		
		for (PlanBuilder.Plan plan: new PlanBuilder.Plan[]{builtPlan, rawPlan}) {
			RowSet<DOMHandle> xmlRowSet = rowMgr.resultRows(plan, new DOMHandle());
	        checkXMLDocRows(xmlRowSet);
	        xmlRowSet.close();

			RowSet<JacksonHandle> jsonRowSet = rowMgr.resultRows(plan, new JacksonHandle());
	        checkJSONDocRows(jsonRowSet);
	        jsonRowSet.close();

			RowSet<RowRecord> recordRowSet = rowMgr.resultRows(plan);
			checkRecordDocRows(recordRowSet);
			recordRowSet.close();
		}
	}
	@Test
	public void testView() {
		RowManager rowMgr = Common.client.newRowManager();

		PlanBuilder p = rowMgr.newPlanBuilder();

		PlanBuilder.ExportablePlan builtPlan =
				p.fromView("opticUnitTest", "musician", null, null,
						p.cts.jsonPropertyWordQuery(p.xs.string("instrument"), p.xs.string("trumpet"))
						)
				  .select(null, "") 
				  .orderBy("lastName");

        String[] lastName  = {"Armstrong",  "Davis"};
        String[] firstName = {"Louis",      "Miles"};
        String[] dob       = {"1901-08-04", "1926-05-26"};

        int rowNum = 0;
		for (RowRecord row: rowMgr.resultRows(builtPlan)) {
	        assertEquals("unexpected lastName value in row record "+rowNum,  lastName[rowNum],  row.getString("lastName"));
	        assertEquals("unexpected firstName value in row record "+rowNum, firstName[rowNum], row.getString("firstName"));
	        assertEquals("unexpected dob value in row record "+rowNum,       dob[rowNum],       row.getString("dob"));
			rowNum++;
		}
        assertEquals("unexpected count of result records", 2, rowNum);
	}
    @Test
	public void testTriples() {
		RowManager rowMgr = Common.client.newRowManager();

		PlanBuilder p = rowMgr.newPlanBuilder();

		PlanBuilder.ExportablePlan plan =
			p.fromTriples(
				p.pattern(
						p.col("subject"),
						p.sem.iri("http://example.org/rowgraph/p1"),
						p.col("object")
						)
				)
			 .orderBy("subject", "object");

		int rowNum = 0;
		for (RowRecord row: rowMgr.resultRows(plan)) {
			int testRow = rowNum;
			switch(rowNum) {
			case 1: testRow = 2; break;
			}

			assertEquals("unexpected subject value in row record "+rowNum, triples[testRow][0], row.getString("subject"));
	        assertEquals("unexpected object  value in row record "+rowNum, triples[testRow][2], row.getString("object"));
			rowNum++;
		}
        assertEquals("unexpected count of result records", 2, rowNum);
	}
    @Test
	public void testLexicons() throws IOException, XPathExpressionException {
		RowManager rowMgr = Common.client.newRowManager();

		PlanBuilder p = rowMgr.newPlanBuilder();

		Map<String, CtsReferenceExpr> lexicons = new HashMap<String, CtsReferenceExpr>();
		lexicons.put("uri", p.cts.uriReference());
		lexicons.put("int", p.cts.elementReference(p.xs.qname("int")));

		PlanBuilder.ExportablePlan plan =
				p.fromLexicons(lexicons)
				 .orderBy(p.cols("int", "uri"))
				 .select(p.cols("int",  "uri"));

		int[]    expectedInts = {
			1,
			3,
			3,
			3,
			4,
			10,
			10,
			20,
			30,
			};
		String[] expectedUris = {
			"/sample/tuples-test1.xml",
			"/sample/tuples-test2.xml",
			"/sample/tuples-test3.xml",
			"/sample/tuples-test4.xml",
			"/sample/lexicon-test4.xml",
			"/sample/lexicon-test3.xml",
			"/sample/lexicon-test4.xml",
			"/sample/lexicon-test3.xml",
			"/sample/lexicon-test4.xml"
			};

		int rowNum = 0;
		for (RowRecord row: rowMgr.resultRows(plan)) {
	        assertEquals("unexpected int value in row record "+rowNum, expectedInts[rowNum], row.getInt("int"));
	        assertEquals("unexpected uri value in row record "+rowNum, expectedUris[rowNum], row.getString("uri"));
			rowNum++;
		}
        assertEquals("unexpected count of result records", expectedInts.length, rowNum);
	}
	@Test
	public void testParams() throws IOException, XPathExpressionException {
		RowManager rowMgr = Common.client.newRowManager();

		PlanBuilder p = rowMgr.newPlanBuilder();

		PlanParam cityParam  = p.param("city");
		PlanParam limitParam = p.param("limit");

		PlanBuilder.ExportablePlan builtPlan =
				p.fromLiterals(litRows)
				 .orderBy(p.col("rowNum"))
				 .where(p.eq(p.col("city"), cityParam))
				 .select(p.cols("rowNum", "temp"))
				 .limit(limitParam);

		RowSet<RowRecord> recordRowSet = rowMgr.resultRows(
				builtPlan.bindParam(cityParam, "Seattle").bindParam(limitParam, 1)
				);

		Iterator<RowRecord> recordRowItr = recordRowSet.iterator();
		assertTrue("no record row to iterate", recordRowItr.hasNext());
		RowRecord recordRow = recordRowItr.next();
		checkSingleRow(recordRow);
        assertFalse("expected one record row", recordRowItr.hasNext());

        recordRowSet.close();
	}
	private DOMHandle initNamespaces(DOMHandle handle) {
        EditableNamespaceContext namespaces = new EditableNamespaceContext();
        namespaces.setNamespaceURI("sp", "http://www.w3.org/2005/sparql-results#");

        handle.getXPathProcessor().setNamespaceContext(namespaces);

        return handle;
	}
	private void checkSingleRow(NodeList row) {
        assertEquals("unexpected column count in XML", 2, row.getLength());
        Element testElement = (Element) row.item(0);
        assertEquals("unexpected first binding name in XML",  "rowNum", testElement.getAttribute("name"));
        assertEquals("unexpected first binding value in XML", "2",      testElement.getTextContent());
        testElement = (Element) row.item(1);
        assertEquals("unexpected second binding name in XML",  "temp", testElement.getAttribute("name"));
        assertEquals("unexpected second binding value in XML", "72",   testElement.getTextContent());
	}
	private void checkSingleRow(JsonNode row) {
        String value = row.findValue("rowNum").findValue("value").asText();
        assertEquals("unexpected first binding value in JSON", "2",  value);
        value = row.findValue("temp").findValue("value").asText();
        assertEquals("unexpected first binding value in JSON", "72", value);
	}
	private void checkSingleRow(RowRecord row) {
        assertEquals("unexpected first binding value in row record", "2",  row.getString("rowNum"));

        assertEquals("unexpected first binding value in row record", "72", row.getString("temp"));
	}
	private void checkXMLDocRows(RowSet<DOMHandle> rowSet)
	throws XPathExpressionException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, SAXException, IOException
	{
		int rowCount = 0;
		for (DOMHandle xmlRow: rowSet) {
			xmlRow = initNamespaces(xmlRow);

			NodeList row = xmlRow.evaluateXPath("/sp:result/sp:binding", NodeList.class);

	        rowCount++;

			assertEquals("unexpected column count in XML", 3, row.getLength());

	        Element testElement = (Element) row.item(0);
	        assertEquals("unexpected first binding name in XML", "rowNum",  testElement.getAttribute("name"));
	        assertEquals("unexpected first binding value in XML", String.valueOf(rowCount), testElement.getTextContent());

	        testElement = (Element) row.item(1);
	        assertEquals("unexpected second binding name in XML", "uri",               testElement.getAttribute("name"));
	        assertEquals("unexpected second binding value in XML", uris[rowCount - 1], testElement.getTextContent());

	        testElement = (Element) row.item(2);
	        assertEquals("unexpected third binding name in XML", "doc", testElement.getAttribute("name"));
	        if (uris[rowCount - 1].endsWith(".xml")) {
	        	assertXMLEqual("unexpected third binding value in XML",
	        			docs[rowCount - 1], nodeToString(testElement.getElementsByTagName("a").item(0))
	        			);
	        } else {
		        assertEquals("unexpected third binding value in XML", docs[rowCount - 1], testElement.getTextContent());
	        }
		}
        assertEquals("row count for XML document join", 3, rowCount);
	}
	private void checkJSONDocRows(RowSet<JacksonHandle> rowSet) {
		int rowCount = 0;
		for (JacksonHandle jsonRow: rowSet) {
			JsonNode row = jsonRow.get();

			rowCount++;

	        String value = row.findValue("rowNum").findValue("value").asText();
	        assertEquals("unexpected first binding value in JSON", String.valueOf(rowCount), value);

	        value = row.findValue("uri").findValue("value").asText();
	        assertEquals("unexpected second binding value in JSON", uris[rowCount - 1], value);

	        if (uris[rowCount - 1].endsWith(".json")) {
		        value = row.findValue("doc").findValue("value").toString();
		        assertEquals("unexpected third binding value in JSON", docs[rowCount - 1].replace(" ", ""), value);
	        } else {
		        value = row.findValue("doc").findValue("value").asText();
		        assertEquals("unexpected third binding value in JSON", docs[rowCount - 1], value);
	        }
		}
        assertEquals("row count for JSON document join", 3, rowCount);
	}
	private void checkRecordDocRows(RowSet<RowRecord> rowSet) throws SAXException, IOException {
		int rowCount = 0;
		for (RowRecord row: rowSet) {
			rowCount++;

			assertEquals("unexpected first binding kind in row record", ColumnKind.ATOMIC_VALUE, row.getKind("rowNum"));
			assertEquals("unexpected first binding datatype in row record", "integer", row.getAtomicDatatype("rowNum").getLocalPart());
			assertEquals("unexpected first binding value in row record", rowCount, row.getInt("rowNum"));

			assertEquals("unexpected second binding kind in row record", ColumnKind.ATOMIC_VALUE, row.getKind("uri"));
			assertEquals("unexpected second binding datatype in row record", "string", row.getAtomicDatatype("uri").getLocalPart());
	        assertEquals("unexpected second binding value in row record", uris[rowCount - 1], row.getString("uri"));

			assertEquals("unexpected third binding kind in row record", ColumnKind.CONTENT, row.getKind("doc"));
    		String doc = row.getContent("doc", new StringHandle()).get();
	        if (uris[rowCount - 1].endsWith(".json")) {
				assertEquals("unexpected third binding mimetype in row record", "application/json", row.getContentMimetype("doc"));
		        assertEquals("unexpected third binding JSON value in row record", docs[rowCount - 1], doc);
	        } else if (uris[rowCount - 1].endsWith(".xml")) {
				assertEquals("unexpected third binding mimetype in row record", "application/xml", row.getContentMimetype("doc"));
	        	assertXMLEqual("unexpected third binding XML value in row record", docs[rowCount - 1], doc);
	        } else {
				assertEquals("unexpected third binding mimetype in row record", "text/plain", row.getContentMimetype("doc"));
		        assertEquals("unexpected third binding value in row record", docs[rowCount - 1], doc);
	        }
		}
        assertEquals("row count for record document join", 3, rowCount);
	}
	private String nodeToString(Node node)
	throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError
	{
		StringWriter sw = new StringWriter();
		Transformer  tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		tf.transform(
				new DOMSource(node), new StreamResult(sw)
				);
		return sw.toString();
	}
}
