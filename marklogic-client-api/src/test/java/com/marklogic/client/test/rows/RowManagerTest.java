/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.rows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.*;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.row.*;
import com.marklogic.client.row.RowManager.RowSetPart;
import com.marklogic.client.row.RowManager.RowStructure;
import com.marklogic.client.row.RowRecord.ColumnKind;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML11;
import com.marklogic.client.type.*;
import com.marklogic.client.util.EditableNamespaceContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.jupiter.api.Assertions.*;

public class RowManagerTest {
  private static String[]             uris           = null;
  private static String[]             docs           = null;
  private static Map<String,Object>[] litRows        = null;
  private static Map<String,Object>[] groupableRows  = null;
  private static Map<String,Object>[] numberRows  = null;
  private static String[][]           triples        = null;
  private static RowStructure[]       rowstructs     = null;
  private static RowSetPart[]         datatypeStyles = null;

  private final static String VIEW_NAME = "musician_ml10";

  @SuppressWarnings("unchecked")
  @BeforeAll
  public static void beforeClass() throws IOException, InterruptedException {
    uris = new String[]{"/rowtest/docJoin1.json", "/rowtest/docJoin1.xml", "/rowtest/docJoin1.txt", "/rowtest/embedded.xml"};
    docs = new String[]{
      "{\"a\":{\"b\":[\"c\", 4]}}",
      "<a><b>c</b>4</a>",
      "a b c 4",
      "<doc xmlns:sem=\"http://marklogic.com/semantics\">\n" +
              "    <hello>world</hello>\n" +
              "    <sem:triple>\n" +
              "    <sem:subject>http://marklogicsparql.com/id#5555</sem:subject>\n" +
              "    <sem:predicate>http://marklogicsparql.com/addressbook#firstName</sem:predicate>\n" +
              "    <sem:object datatype=\"http://www.w3.org/2001/XMLSchema#string\">Jim</sem:object>\n" +
              "    </sem:triple>\n" +
              "    <sem:triple>\n" +
              "    <sem:subject>http://marklogicsparql.com/id#5555</sem:subject>\n" +
              "    <sem:predicate>http://marklogicsparql.com/addressbook#firstName</sem:predicate>\n" +
              "    <sem:object datatype=\"http://www.w3.org/2001/XMLSchema#string\">Jim</sem:object>\n" +
              "    </sem:triple>\n" +
              "    </doc>"
    };

    litRows = new Map[3];

    Map<String,Object>   row  = new HashMap<>();
    row.put("rowNum", 1);
    row.put("city",   "New York");
    row.put("temp",   "82");
    row.put("uri",    uris[0]);
    litRows[0] = row;

    row = new HashMap<>();
    row.put("rowNum", 2);
    row.put("city",   "Seattle");
    row.put("temp",   "72");
    row.put("uri",    uris[1]);
    litRows[1] = row;

    row = new HashMap<>();
    row.put("rowNum", 3);
    row.put("city",   "Phoenix");
    row.put("temp",   "92");
    row.put("uri",    uris[2]);
    litRows[2] = row;

    groupableRows = new Map[3];

    row = new HashMap<>();
    row.put("c1", "11");
    row.put("c2", "21");
    row.put("v",  "2");
    groupableRows[0] = row;

    row = new HashMap<>();
    row.put("c1", "12");
    row.put("c2", "21");
    row.put("v",  "2");
    groupableRows[1] = row;

    row = new HashMap<>();
    row.put("c1", "12");
    row.put("c2", "22");
    row.put("v",  "2");
    groupableRows[2] = row;

    numberRows = new Map[3];
    row = new HashMap<>();
    row.put("r", 3);
    row.put("c1", "a");
    row.put("c2",  "x");
    numberRows[0] = row;

    row = new HashMap<>();
    row.put("r", 5);
    row.put("c1", "b");
    row.put("c2",  "x");
    numberRows[1] = row;

    row = new HashMap<>();
    row.put("r", 7);
    row.put("c1", "a");
    numberRows[2] = row;

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
      "<metadata xmlns=\"\" name=\"key\">value</metadata>\n"+
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
        .add(uris[3], new StringHandle(docs[3]).withFormat(Format.XML))
        .add("/rowtest/triples1.xml", new StringHandle(triplesXML).withFormat(Format.TEXT))
    );

    rowstructs     = new RowStructure[]{ RowStructure.OBJECT, RowStructure.ARRAY };
    datatypeStyles = new RowSetPart[]{   RowSetPart.ROWS,     RowSetPart.HEADER  };

    QueryManager queryMgr = Common.client.newQueryManager();
    DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
    deleteQuery.setDirectory("/testFromDocUrisWithDirectoryQuery/");
    queryMgr.delete(deleteQuery);
    deleteQuery.setDirectory("/testFromDocUrisWithDirectoryQueryNew/");
    queryMgr.delete(deleteQuery);
  }
  @AfterAll
  public static void afterClass() {
  }

  @Test
  public void testResultDoc() throws IOException, XPathExpressionException {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanBuilder.ExportablePlan builtPlan =
      p.fromLiterals(litRows)
        .orderBy(p.col("rowNum"))
        .where(p.eq(p.col("city"), p.xs.string("Seattle")))
        .select(p.colSeq("rowNum", "temp"));

    StringHandle planHandle = builtPlan.export(new StringHandle()).withFormat(Format.JSON);
    RawPlanDefinition rawPlan = rowMgr.newRawPlanDefinition(planHandle);

    ObjectMapper mapper = new ObjectMapper();

    for (PlanBuilder.Plan plan: new PlanBuilder.Plan[]{builtPlan, rawPlan}) {
      for (RowStructure rowstruct: rowstructs) {
        rowMgr.setRowStructureStyle(rowstruct);

        for (RowSetPart datatypeStyle: datatypeStyles) {
          rowMgr.setDatatypeStyle(datatypeStyle);

          if (rowstruct == RowStructure.OBJECT) {
            try (ReaderHandle readerHandle = new ReaderHandle()) {
              rowMgr.resultDoc(plan, readerHandle.withMimetype("text/csv"));

              try (LineNumberReader lineReader = new LineNumberReader(readerHandle.get())) {
                String line = lineReader.readLine();
                String[] cols = line.split(",");
                assertArrayEquals( cols, new String[]{"rowNum","temp"});

                line = lineReader.readLine();
                cols = line.split(",");
                assertArrayEquals( cols, new String[]{"2","72"});
              }
            }
          }

          DOMHandle domHandle = initNamespaces(rowMgr.resultDoc(plan, new DOMHandle()));
// domHandle.write(System.out);

          NodeList testList = domHandle.evaluateXPath("/table:table/table:columns/table:column", NodeList.class);
          assertEquals( 2, testList.getLength());
          Element testElement = (Element) testList.item(0);
          assertEquals( "rowNum", testElement.getAttribute("name"));
          if (datatypeStyle == RowSetPart.HEADER) {
            assertEquals(  "xs:integer", testElement.getAttribute("type"));
          }
          testElement = (Element) testList.item(1);
          assertEquals( "temp", testElement.getAttribute("name"));
          if (datatypeStyle == RowSetPart.HEADER) {
            assertEquals(  "xs:string", testElement.getAttribute("type"));
          }

          testList = domHandle.evaluateXPath("/table:table/table:rows/table:row", NodeList.class);
          assertEquals( 1, testList.getLength());

          testList = domHandle.evaluateXPath("/table:table/table:rows/table:row[1]/table:cell", NodeList.class);
          checkSingleRow(testList, datatypeStyle);

          JacksonHandle handle = rowMgr.resultDoc(plan, new JacksonHandle());
// handle.write(System.out);

          JsonNode testNode = handle.get();

          JsonNode rowsNode = null;
          switch(rowstruct) {
            case OBJECT:
              checkHeader(testNode.findValue("columns"), datatypeStyle);

              rowsNode = testNode.findValue("rows");
              assertEquals( 1, rowsNode.size());

              checkSingleRow(rowsNode.get(0), rowstruct, datatypeStyle);
              break;
            case ARRAY:
              checkHeader(testNode.get(0), datatypeStyle);

              rowsNode = testNode.get(1);
              assertEquals( 2, rowsNode.size());

              checkSingleRow(rowsNode, rowstruct, datatypeStyle);
              break;
            default:
              throw new IllegalArgumentException("unknown case for RowStructure: "+rowstruct);
          }

          try (ReaderHandle readerHandle = new ReaderHandle()) {
            rowMgr.resultDoc(plan, readerHandle.withMimetype("application/json-seq"));

            try (LineNumberReader lineReader = new LineNumberReader(readerHandle.get())) {
              int i=0;
              for (String line = null; (line = lineReader.readLine()) != null; i++) {
                line = line.trim();
// System.out.println(line);
                testNode = mapper.readTree(line);
                switch(i) {
                  case 0:
                    switch(rowstruct) {
                      case OBJECT:
                        checkHeader(testNode.findValue("columns"), datatypeStyle);
                        break;
                      case ARRAY:
                        checkHeader(testNode, datatypeStyle);
                        break;
                    }
                    break;
                  case 1:
                    checkSingleRow(testNode, rowstruct, datatypeStyle);
                    break;
                }
              }
              assertEquals(2, i);
            }
          }
        }
      }
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
        .select(p.colSeq("rowNum", "temp"));

    StringHandle planHandle = builtPlan.export(new StringHandle()).withFormat(Format.JSON);
    RawPlanDefinition rawPlan = rowMgr.newRawPlanDefinition(planHandle);

    for (PlanBuilder.Plan plan: new PlanBuilder.Plan[]{builtPlan, rawPlan}) {
      for (RowStructure rowstruct: rowstructs) {
        rowMgr.setRowStructureStyle(rowstruct);

        for (RowSetPart datatypeStyle: datatypeStyles) {
          rowMgr.setDatatypeStyle(datatypeStyle);

          xmlhandle: {
            RowSet<DOMHandle> xmlRowSet = rowMgr.resultRows(plan, new DOMHandle());
            checkHeader("XML", xmlRowSet, datatypeStyle);

            Iterator<DOMHandle> xmlRowItr = xmlRowSet.iterator();
            assertTrue( xmlRowItr.hasNext());
            DOMHandle xmlRow = initNamespaces(xmlRowItr.next());
            checkSingleRow(xmlRow.evaluateXPath("/table:row/table:cell", NodeList.class), datatypeStyle);
            assertFalse( xmlRowItr.hasNext());

            xmlRowSet.close();
          }

          xmlshortcut: {
            RowSet<Document> xmlRowSetAs = rowMgr.resultRowsAs(plan, Document.class);
            checkHeader("XML", xmlRowSetAs, datatypeStyle);

            Iterator<Document> xmlRowItrAs = xmlRowSetAs.iterator();
            assertTrue( xmlRowItrAs.hasNext());
            DOMHandle xmlRow = initNamespaces(new DOMHandle().with(xmlRowItrAs.next()));
            checkSingleRow(xmlRow.evaluateXPath("/table:row/table:cell", NodeList.class), datatypeStyle);
            assertFalse( xmlRowItrAs.hasNext());

            xmlRowSetAs.close();
          }

          jsonhandle: {
            RowSet<JacksonHandle> jsonRowSet = rowMgr.resultRows(plan, new JacksonHandle());
            checkHeader("JSON", jsonRowSet, datatypeStyle);

            Iterator<JacksonHandle> jsonRowItr = jsonRowSet.iterator();
            assertTrue( jsonRowItr.hasNext());
            JacksonHandle jsonRow = jsonRowItr.next();
// jsonRow.write(System.out);

            checkSingleRow(jsonRow.get(), rowstruct, datatypeStyle);
            assertFalse( jsonRowItr.hasNext());

            jsonRowSet.close();
          }

          jsonshortcut: {
            RowSet<JsonNode> jsonRowSetAs = rowMgr.resultRowsAs(plan, JsonNode.class);
            checkHeader("JSON", jsonRowSetAs, datatypeStyle);

            Iterator<JsonNode> jsonRowItrAs = jsonRowSetAs.iterator();
            assertTrue( jsonRowItrAs.hasNext());
            checkSingleRow(jsonRowItrAs.next(), rowstruct, datatypeStyle);
            assertFalse( jsonRowItrAs.hasNext());

            jsonRowSetAs.close();
          }

          rowrecord: {
            RowSet<RowRecord> recordRowSet = rowMgr.resultRows(plan);
            Iterator<RowRecord> recordRowItr = recordRowSet.iterator();
            assertTrue( recordRowItr.hasNext());
            RowRecord recordRow = recordRowItr.next();
            checkSingleRow(recordRow);
            assertFalse( recordRowItr.hasNext());

            recordRowSet.close();
          }
        }
      }
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
        .joinDoc(p.col("doc"), p.col("uri"))
// NOTE: workaround for server bug 44875
        .where(p.cts.notQuery(p.cts.elementQuery(p.xs.QName("prop:properties"), p.cts.trueQuery())))
        .orderBy(p.col("rowNum"))
        .select(p.colSeq("rowNum", "uri", "doc"));

    StringHandle planHandle = builtPlan.export(new StringHandle()).withFormat(Format.JSON);
    RawPlanDefinition rawPlan = rowMgr.newRawPlanDefinition(planHandle);
    String[] colNames = {"rowNum", "uri", "doc"};
    for (PlanBuilder.Plan plan: new PlanBuilder.Plan[]{builtPlan, rawPlan}) {
      RowSet<DOMHandle> xmlRowSet = rowMgr.resultRows(plan, new DOMHandle());
      checkColumnNames(colNames, xmlRowSet);
      checkXMLDocRows(xmlRowSet);
      xmlRowSet.close();

      RowSet<JacksonHandle> jsonRowSet = rowMgr.resultRows(plan, new JacksonHandle());
      checkColumnNames(colNames, jsonRowSet);
      checkJSONDocRows(jsonRowSet);
      jsonRowSet.close();

      RowSet<RowRecord> recordRowSet = rowMgr.resultRows(plan);
      checkColumnNames(colNames, recordRowSet);
      checkRecordDocRows(recordRowSet);
      recordRowSet.close();
    }
  }
  @Test
  public void testView() {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.ExportablePlan builtPlan =
            p.fromView("opticUnitTest", VIEW_NAME)
                    .where(
                            p.cts.andQuery(
                                    p.cts.jsonPropertyWordQuery("instrument", "trumpet"),
                                    p.cts.jsonPropertyWordQuery(p.xs.string("lastName"), p.xs.stringSeq("Armstrong", "Davis"))
                            )
                    )
                    .select(null, "")
                    .orderBy(p.col("lastName"));

    testViewRows(rowMgr.resultRows(builtPlan));
  }
  private void testViewRows(RowSet<RowRecord> rows) {
    String[] lastName  = {"Armstrong",  "Davis"};
    String[] firstName = {"Louis",      "Miles"};
    String[] dob       = {"1901-08-04", "1926-05-26"};

    int rowNum = 0;
    for (RowRecord row: rows) {
      assertEquals(lastName[rowNum],  row.getString("lastName"));
      assertEquals(firstName[rowNum], row.getString("firstName"));
      assertEquals(dob[rowNum],       row.getString("dob"));
      rowNum++;
    }
    assertEquals( 2, rowNum);
  }

	@Test
	void testSQL() {
		final String query = "select * from opticUnitTest.musician_ml10";
		RowManager mgr = Common.client.newRowManager();

		RowSet<RowRecord> rows = mgr.resultRows(mgr.newPlanBuilder().fromSql(query));
		assertEquals(4, rows.stream().count());

		JsonNode doc = mgr.resultDoc(mgr.newPlanBuilder().fromSql(query), new JacksonHandle()).get();
		assertEquals(3, doc.get("columns").size());
		assertEquals(4, doc.get("rows").size());
	}

	@Test
	void sqlNoRows() {
		final String query = "select * from opticUnitTest.musician_ml10 where lastName = 'NOT_FOUND'";
		RowManager mgr = Common.client.newRowManager();

		RowSet<RowRecord> rows = mgr.resultRows(mgr.newPlanBuilder().fromSql(query));
		assertEquals(0, rows.stream().count());

		JsonNode doc = mgr.resultDoc(mgr.newPlanBuilder().fromSql(query), new JacksonHandle()).get();
		assertNull(doc);
	}

	// This is not passing on MarkLogic 10 for unknown reasons. Runs fine on MarkLogic 11.
	@ExtendWith(RequiresML11.class)
	@Test
	public void testSQLNoResults() {
		RowManager rowManager = Common.client.newRowManager();

		RowSet<RowRecord> rowSet = rowManager.resultRows(rowManager.newPlanBuilder()
			.fromSql("select * from opticUnitTest.musician_ml10 where lastName = 'Armstrong'"));
		assertEquals(1, rowSet.stream().count());

		rowSet = rowManager.resultRows(rowManager.newPlanBuilder()
			.fromSql("select * from opticUnitTest.musician_ml10 where lastName = 'x'"));
		assertEquals(0, rowSet.stream().count());
	}

  @Test
  @ExtendWith(RequiresML11.class)
  public void testErrorWhileStreamingRows() {
	  if (Common.USE_REVERSE_PROXY_SERVER) {
		  // Different kind of error is thrown when using reverse proxy.
		  return;
	  }

    final String validQueryThatEventuallyThrowsAnError = "select case " +
        "when lastName = 'Byron' then fn_error(fn_qname('', 'SQL-TABLENOTFOUND'), 'Internal Server Error') end, " +
        "opticUnitTest.musician.* from (select * from opticUnitTest.musician order by lastName)";

    RowManager rowManager = Common.client.newRowManager();
    PlanBuilder.ModifyPlan plan = rowManager.newPlanBuilder().fromSql(validQueryThatEventuallyThrowsAnError);

	if (Common.getMarkLogicVersion().getMajor() >= 12) {
		FailedRequestException ex = assertThrows(FailedRequestException.class, () -> rowManager.resultRows(plan),
			"The SQL query is designed to not immediately fail - it will immediately return a 200 status code to the " +
				"Java Client because the query itself can be executed - but will fail later as it streams rows; " +
				"specifically, it will fail on the second row, which is the 'Byron' row. " +
				"If chunking is configured correctly for the /v1/rows requests - i.e. if the " +
				"'TE' header is present - then ML should return trailers in the HTTP response named 'ml-error-code' and " +
				"'ml-error-message'. Those are intended to indicate that while a 200 was returned, an error occurred later " +
				"while streaming data back. The Java Client is then expected to detect those trailers and throw a " +
				"FailedRequestException. If the Java Client does not do that, then no exception will be thrown and this " +
				"assertion will fail.");

    assertEquals(500, ex.getServerStatusCode(),
		"A 500 is expected, even though ML immediately returned a 200 before it started streaming any data " +
			"back; a 500 is used instead of a 400 here as we don't have a reliable way of knowing if the error " +
			"occurred due to a bad request by the user, since the query was valid in the sense that it could be " +
			"executed");

	  // For 11-nightly, changed these to be less precise as the server error message is free to change between minor
	  // releases, thus making any equality assertions very fragile.
	  assertTrue(ex.getServerMessage().contains("SQL-TABLENOTFOUND"),
		  "The server error message is expected to be the value of the 'ml-error-message' trailer");
	  assertTrue(
		  ex.getMessage().contains("SQL-TABLENOTFOUND"),
		  "The exception message is expected to be a formatted message containing the values of the 'ml-error-code' and " +
			  "'ml-error-message' trailers");

	} else {
		// For unknown reasons in MarkLogic 11, the invalid query immediately causes an IO error, while on MarkLogic 12,
		// the expected exception is thrown.
		MarkLogicIOException ex = assertThrows(MarkLogicIOException.class, () -> rowManager.resultRows(plan));
		assertTrue(ex.getMessage().contains("unexpected end of stream"), "Unexpected error: " + ex.getMessage());
	}
  }

  @Test
  public void testSearch() {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanSystemColumn viewDocId = p.fragmentIdCol("viewDocId");

    PlanBuilder.ExportablePlan builtPlan =
            p.fromSearch(p.cts.jsonPropertyValueQuery("instrument", "trumpet"))
             .joinInner(
                 p.fromView("opticUnitTest", VIEW_NAME, "", viewDocId),
                 p.on(p.fragmentIdCol("fragmentId"), viewDocId)
                 )
             .orderBy(p.col("lastName"));

    String[] lastName  = {"Armstrong",  "Davis"};
    String[] firstName = {"Louis",      "Miles"};

    int rowNum = 0;
    for (RowRecord row: rowMgr.resultRows(builtPlan)) {
// System.out.println(row.toString());
      assertEquals(lastName[rowNum],  row.getString("lastName"));
      assertEquals(firstName[rowNum], row.getString("firstName"));

      double score = row.getDouble("score");
      assertTrue(score > 0);

      rowNum++;
    }
    assertEquals( 2, rowNum);
  }
  @Test
  public void testSearchDocs() {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.ExportablePlan builtPlan =
            p.fromSearchDocs(p.cts.wordQuery("trumpet"))
             .orderBy(p.desc("score"));

    double lastScore = Double.MAX_VALUE;
    int rowNum = 0;
    for (RowRecord row: rowMgr.resultRows(builtPlan)) {
// System.out.println(row.toString());
      double score = row.getDouble("score");
      assertTrue(score > 0);
      assertTrue(lastScore >= score);
      lastScore = score;

      String uri = row.getString("uri");
      assertNotNull( uri);
      assertTrue(uri.matches("^/optic/test/musician[0-9]+.json$"));

      JsonNode doc = row.getContent("doc", new JacksonHandle()).get();
      assertNotNull( doc);
      assertTrue(doc.has("musician"));

      rowNum++;
    }
    assertEquals( 2, rowNum);
  }
  @Test
  public void testJoinSrcDoc() throws IOException {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.ExportablePlan builtPlan =
      p.fromView("opticUnitTest", VIEW_NAME, "", p.fragmentIdCol("musicianDocId"))
        .joinDoc(p.col("musicianDoc"), p.fragmentIdCol("musicianDocId"))
        .orderBy(p.col("lastName"))
        .select(
          p.col("lastName"), p.col("firstName"),
          p.as("instruments",  p.xpath("musicianDoc", "/musician/instrument"))
        )
        .where(p.fn.exists(p.fn.indexOf(p.col("instruments"), p.xs.string("trumpet"))));

    String[] lastName  = {"Armstrong",  "Davis"};
    String[] firstName = {"Louis",      "Miles"};

    int rowNum = 0;
    for (RowRecord row: rowMgr.resultRows(builtPlan)) {
      assertEquals(lastName[rowNum],  row.getString("lastName"));
      assertEquals(firstName[rowNum], row.getString("firstName"));

      String instruments = row.getString("instruments");
      assertNotNull(instruments);
      assertTrue(instruments.contains("trumpet"));
      rowNum++;
    }
    assertEquals( 2, rowNum);
  }
  @Test
  public void testJoinDocUri() throws IOException {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.ExportablePlan builtPlan =
      p.fromView("opticUnitTest", VIEW_NAME, "", p.fragmentIdCol("musicianDocId"))
        .joinDocUri(p.col("musicianDocUri"), p.fragmentIdCol("musicianDocId"))
        .orderBy(p.col("lastName"))
        .select(p.col("lastName"), p.col("firstName"), p.col("musicianDocUri"))
        .limit(2);

    String[] lastName       = {"Armstrong",                  "Byron"};
    String[] firstName      = {"Louis",                      "Don"};
    String[] musicianDocUri = {"/optic/test/musician1.json", "/optic/test/musician2.json"};

    int rowNum = 0;
    for (RowRecord row: rowMgr.resultRows(builtPlan)) {
      assertEquals(lastName[rowNum],       row.getString("lastName"));
      assertEquals(firstName[rowNum],      row.getString("firstName"));
      assertEquals(musicianDocUri[rowNum], row.getString("musicianDocUri"));
      rowNum++;
    }
    assertEquals( 2, rowNum);
  }
  @Test
  public void testTriples() {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanPrefixer rowGraph = p.prefixer("http://example.org/rowgraph");

    PlanBuilder.ExportablePlan plan =
      p.fromTriples(
        p.pattern(
          p.col("subject"),
          rowGraph.iri("p1"), // equivalent to: p.sem.iri("http://example.org/rowgraph/p1")
          p.col("object")
        ),
        (String) null,
        (String) null,
        PlanTripleOption.DEDUPLICATED
      )
        .where(
          p.sem.store(p.xs.string("document"), p.cts.elementValueQuery(p.xs.QName("metadata"), p.xs.string("value")))
        )
        .orderBy(p.colSeq("subject", "object"));

    int rowNum = 0;
    for (RowRecord row: rowMgr.resultRows(plan)) {
      int testRow = rowNum;
      switch(rowNum) {
        case 1: testRow = 2; break;
      }

      assertEquals(triples[testRow][0], row.getString("subject"));
      assertEquals(triples[testRow][2], row.getString("object"));
      rowNum++;
    }
    assertEquals( 2, rowNum);
  }
  @Test
  public void testLexicons() throws IOException, XPathExpressionException {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    Map<String, CtsReferenceExpr> lexicons = new HashMap<>();
    lexicons.put("uri", p.cts.uriReference());
    lexicons.put("int", p.cts.elementReference(p.xs.QName("int")));

    PlanBuilder.ExportablePlan plan =
      p.fromLexicons(lexicons)
        .orderBy(p.colSeq("int", "uri"))
        .select(p.colSeq("int",  "uri"));

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
      assertEquals(expectedInts[rowNum], row.getInt("int"));
      assertEquals(expectedUris[rowNum], row.getString("uri"));
      rowNum++;
    }
    assertEquals( expectedInts.length, rowNum);
  }
  @Test
  public void testGroupByUnionWithGroup() {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.ExportablePlan builtPlan =
           p.fromLiterals(groupableRows)
            .bindAs("v", p.xs.intExpr(p.col("v")))
            .groupByUnion(p.groupSeq(p.group(), p.group("c1", "c2")), p.aggregateSeq(
                p.hasGroupKey("c1Flag", "c1"),
                p.hasGroupKey("c2Flag", "c2"),
                p.count("count"),
                p.sum("sum","v")
                ))
            .orderBy(p.colSeq("c1Flag", "c2Flag", "c1", "c2"));

    int[] c1Flag = {0,0,0,1};
    int[] c2Flag = {0,0,0,1};
    String[] c1  = {"11","12","12",null};
    String[] c2  = {"21","21","22",null};
    int[] count  = {1,1,1,3};
    int[] sum    = {2,2,2,6};

    checkGroupByUnion(rowMgr, builtPlan, c1Flag, c2Flag, c1, c2, count, sum);
  }
  @Test
  public void testGroupByUnionWithRollup() {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.ExportablePlan builtPlan =
           p.fromLiterals(groupableRows)
            .bindAs("v", p.xs.intExpr(p.col("v")))
            .groupByUnion(p.rollup("c1", "c2"), p.aggregateSeq(
                p.hasGroupKey("c1Flag", "c1"),
                p.hasGroupKey("c2Flag", "c2"),
                p.count("count"),
                p.sum("sum","v")
                ))
            .orderBy(p.colSeq("c1Flag", "c2Flag", "c1", "c2"));

    int[] c1Flag = {0,0,0,0,0,1};
    int[] c2Flag = {0,0,0,1,1,1};
    String[] c1  = {"11","12","12","11","12",null};
    String[] c2  = {"21","21","22",null,null,null};
    int[] count  = {1,1,1,1,2,3};
    int[] sum    = {2,2,2,2,4,6};

    checkGroupByUnion(rowMgr, builtPlan, c1Flag, c2Flag, c1, c2, count, sum);
  }
  @Test
  public void testGroupByUnionWithCube() {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.ExportablePlan builtPlan =
            p.fromLiterals(groupableRows)
                    .bindAs("v", p.xs.intExpr(p.col("v")))
                    .groupByUnion(p.cube("c1", "c2"), p.aggregateSeq(
                            p.hasGroupKey("c1Flag", "c1"),
                            p.hasGroupKey("c2Flag", "c2"),
                            p.count("count"),
                            p.sum("sum","v")
                    ))
                    .orderBy(p.colSeq("c1Flag", "c2Flag", "c1", "c2"));

    int[] c1Flag = {0,0,0,0,0,1,1,1};
    int[] c2Flag = {0,0,0,1,1,0,0,1};
    String[] c1  = {"11","12","12","11","12",null,null,null};
    String[] c2  = {"21","21","22",null,null,"21","22",null};
    int[] count  = {1,1,1,1,2,2,1,3};
    int[] sum    = {2,2,2,2,4,4,2,6};

    checkGroupByUnion(rowMgr, builtPlan, c1Flag, c2Flag, c1, c2, count, sum);
  }
  private void checkGroupByUnion(
          RowManager rowMgr, PlanBuilder.ExportablePlan builtPlan,
          int[] c1Flag, int[] c2Flag, String[] c1, String[] c2, int[] count, int[] sum
  ) {
    int rowNum = 0;
    for (RowRecord row: rowMgr.resultRows(builtPlan)) {
// System.out.println(row.toString());
      assertEquals( c1Flag[rowNum], row.getInt("c1Flag"));
      assertEquals( c2Flag[rowNum], row.getInt("c2Flag"));
      assertEquals( c1[rowNum], row.getString("c1"));
      assertEquals( c2[rowNum], row.getString("c2"));
      assertEquals( count[rowNum], row.getInt("count"));
      assertEquals( sum[rowNum], row.getInt("sum"));
      rowNum++;
    }
    assertEquals( count.length, rowNum);
  }
  @Test
  public void testArrayContainer() {
    Set<String> c1Expect = new HashSet<>();
    Set<String> c2Expect = new HashSet<>();
    Set<String> vExpect  = new HashSet<>();
    for (Map<String,Object> row: groupableRows) {
      c1Expect.add((String) row.get("c1"));
      c2Expect.add((String) row.get("c2"));
      vExpect.add((String) row.get("v"));
    }

    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.ExportablePlan builtPlan =
            p.fromLiterals(groupableRows)
             .groupBy(null, p.aggregateSeq(
                     p.arrayAggregate("c1a", "c1"),
                     p.arrayAggregate("c2a", "c2"),
                     p.arrayAggregate("va",  "v")));

    Iterator<RowRecord> rows = rowMgr.resultRows(builtPlan).iterator();
    assertTrue( rows.hasNext());
    RowRecord row = rows.next();
    assertFalse( rows.hasNext());
// System.out.println(row);

    arrayTestimpl("c1 unequal", c1Expect, row, "c1a");
    arrayTestimpl("c2 unequal", c2Expect, row, "c2a");
    arrayTestimpl("v unequal",  vExpect,  row, "va");
  }
  private void arrayTestimpl(String msg, Set<String> expect, RowRecord row, String arrayName) {
    arrayTestimpl(msg, expect, row.getContainer(arrayName));
    arrayTestimpl(msg, expect, row.getContainer(arrayName, new JacksonHandle()).get());
    arrayTestimpl(msg, expect, row.getContainerAs(arrayName, JsonNode.class));
  }
  private void arrayTestimpl(String msg, Set<String> expect, JsonNode arrayNode) {
    Set<String> actual = new HashSet<>();
    arrayNode.iterator().forEachRemaining(node -> actual.add(node.textValue()));
    assertEquals(expect, actual, msg);
  }
  @Test
  public void testObjectContainer() {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.ExportablePlan builtPlan =
            p.fromLiterals(groupableRows)
             .orderBy(p.colSeq("c1", "c2"))
             .select(p.as("o", p.jsonObject(
                     p.prop("c1p", p.col("c1")),
                     p.prop("c2p", p.col("c2")),
                     p.prop("vp",  p.col("v"))
             )));

    int rowNum = 0;
    for (RowRecord row: rowMgr.resultRows(builtPlan)) {
// System.out.println(row);
      objectTestimpl(groupableRows[rowNum], row, "o");
      rowNum++;
    }
    assertEquals( groupableRows.length, rowNum);
  }
  private void objectTestimpl(Map<String,Object> expected, RowRecord row, String objectName) {
    objectTestimpl(expected, row.getContainer(objectName));
    objectTestimpl(expected, row.getContainer(objectName, new JacksonHandle()).get());
    objectTestimpl(expected, row.getContainerAs(objectName, JsonNode.class));
  }
  private void objectTestimpl(Map<String,Object> expected, JsonNode actual) {
    assertEquals( expected.get("c1"), actual.get("c1p").textValue());
    assertEquals( expected.get("c2"), actual.get("c2p").textValue());
    assertEquals( expected.get("v"), actual.get("vp").textValue());
  }
  @Test
  public void testGroupToArrays() {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.ExportablePlan builtPlan =
           p.fromLiterals(groupableRows)
            .bindAs("v", p.xs.intExpr(p.col("v")))
            .groupToArrays(p.namedGroupSeq(
                    p.namedGroup("empty"),
                    p.col("c1"),
                    p.group("c2"),
                    p.namedGroup("all", p.colSeq("c1", "c2"))
                ), p.aggregateSeq(
                    p.count("count"),
                    p.sum("sum","v")
                ));

    Iterator<RowRecord> rows = rowMgr.resultRows(builtPlan).iterator();
    assertTrue( rows.hasNext());
    RowRecord row = rows.next();
    assertFalse( rows.hasNext());
// System.out.println(row);

    ObjectMapper mapper = new ObjectMapper();

    JsonNode expect =
            mapper.createArrayNode()
                  .add(mapper.createObjectNode()
                             .put("count", 3).put("sum", 6));
    JsonNode actual = row.getContainer("empty");
    assertEquals( expect, actual);

    expect = mapper.createArrayNode()
                   .add(mapper.createObjectNode()
                              .put("c1", "11").put("count", 1).put("sum", 2))
                   .add(mapper.createObjectNode()
                              .put("c1", "12").put("count", 2).put("sum", 4));
    actual = row.getContainer("group1");
    assertEquals( expect, actual);

    expect = mapper.createArrayNode()
                   .add(mapper.createObjectNode()
                              .put("c2", "21").put("count", 2).put("sum", 4))
                   .add(mapper.createObjectNode()
                              .put("c2", "22").put("count", 1).put("sum", 2));
    actual = row.getContainer("group2");
    assertEquals( expect, actual);

    expect = mapper.createArrayNode()
                   .add(mapper.createObjectNode()
                              .put("c1", "11").put("c2", "21")
                              .put("count", 1).put("sum", 2))
                   .add(mapper.createObjectNode()
                              .put("c1", "12").put("c2", "21")
                              .put("count", 1).put("sum", 2))
                   .add(mapper.createObjectNode()
                              .put("c1", "12").put("c2", "22")
                              .put("count", 1).put("sum", 2));
    actual = row.getContainer("all");
    assertEquals( expect, actual);
  }
  @Test
  public void testFacetBy() {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.ExportablePlan builtPlan =
           p.fromLiterals(groupableRows)
            .bindAs("v", p.xs.intExpr(p.col("v")))
            .facetBy(p.colSeq("c1", "c2"));

    Iterator<RowRecord> rows = rowMgr.resultRows(builtPlan).iterator();
    assertTrue( rows.hasNext());
    RowRecord row = rows.next();
    assertFalse( rows.hasNext());
// System.out.println(row);

    ObjectMapper mapper = new ObjectMapper();

    JsonNode expect =
             mapper.createArrayNode()
                   .add(mapper.createObjectNode().put("c1", "11").put("count", 1))
                   .add(mapper.createObjectNode().put("c1", "12").put("count", 2));
    JsonNode actual = row.getContainer("group0");
    assertEquals( expect, actual);

    expect = mapper.createArrayNode()
                   .add(mapper.createObjectNode().put("c2", "21").put("count", 2))
                   .add(mapper.createObjectNode().put("c2", "22").put("count", 1));
    actual = row.getContainer("group1");
    assertEquals( expect, actual);
  }
  @Test
  public void testBindAs() {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.ExportablePlan plan =
           p.fromLiterals(litRows)
            .orderBy(p.col("rowNum"))
            .where(p.eq(p.col("city"), p.xs.string("Seattle")))
            .select(p.colSeq("rowNum", "city", "temp"))
            .bindAs("divided",   p.divide(p.xs.intExpr(p.col("temp")), p.xs.intVal(9)))
            .bindAs("compared",  p.eq(p.col("rowNum"), p.xs.intVal(2)))
            .bindAs("concatted", p.fn.concat(p.col("city"), p.xs.string(", WA")));

    Iterator<RowRecord> rows = rowMgr.resultRows(plan).iterator();
    assertTrue( rows.hasNext());
    RowRecord row = rows.next();
    assertFalse( rows.hasNext());
// System.out.println(row.toString());
    assertEquals(           2,             row.getInt("rowNum"));
    assertEquals(             "Seattle",     row.getString("city"));
    assertEquals(             "72",          row.getString("temp"));
    assertEquals(   8,             row.getInt("divided"));
    assertEquals(  true,          row.getBoolean("compared"));
    assertEquals( "Seattle, WA", row.getString("concatted"));
  }
  @Test
  public void testParams() throws IOException, XPathExpressionException {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanParamExpr cityParam  = p.param("city");
    PlanParamExpr limitParam = p.param("limit");

    PlanBuilder.ExportablePlan builtPlan =
      p.fromLiterals(litRows)
        .orderBy(p.col("rowNum"))
        .where(p.eq(p.col("city"), cityParam))
        .select(p.colSeq("rowNum", "temp"))
        .limit(limitParam);

    RowSet<RowRecord> recordRowSet = rowMgr.resultRows(
      builtPlan.bindParam(cityParam, "Seattle").bindParam(limitParam, 1)
    );

    Iterator<RowRecord> recordRowItr = recordRowSet.iterator();
    assertTrue( recordRowItr.hasNext());
    RowRecord recordRow = recordRowItr.next();
    checkSingleRow(recordRow);
    assertFalse( recordRowItr.hasNext());

    recordRowSet.close();
  }
  @Test
  public void testCaseWhenElse() throws IOException {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanColumn rowNum = p.col("rowNum");

    PlanBuilder.ExportablePlan builtPlan =
      p.fromLiterals(litRows)
        .select(rowNum, p.as("cased", p.caseExpr(
          p.when(p.eq(p.col("rowNum"), p.xs.intVal(2)), p.xs.string("second")),
          p.when(p.eq(p.col("rowNum"), p.xs.intVal(3)), p.xs.string("third")),
          p.elseExpr(p.xs.string("otherwise")
          ))))
        .orderBy(rowNum);

    RowSet<RowRecord> recordRowSet = rowMgr.resultRows(builtPlan);

    Iterator<RowRecord> recordRowItr = recordRowSet.iterator();
    assertTrue( recordRowItr.hasNext());
    RowRecord recordRow = recordRowItr.next();
    assertEquals( 1,           recordRow.getInt("rowNum"));
    assertEquals(   "otherwise", recordRow.getString("cased"));

    assertTrue( recordRowItr.hasNext());
    recordRow = recordRowItr.next();
    assertEquals( 2,        recordRow.getInt("rowNum"));
    assertEquals(   "second", recordRow.getString("cased"));

    assertTrue( recordRowItr.hasNext());
    recordRow = recordRowItr.next();
    assertEquals( 3,       recordRow.getInt("rowNum"));
    assertEquals(   "third", recordRow.getString("cased"));

    assertFalse( recordRowItr.hasNext());

    recordRowSet.close();
  }
  @Test
  public void testNodes() throws IOException, XPathExpressionException {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.ExportablePlan builtPlan =
      p.fromLiterals(litRows)
        .where(p.lt(p.col("rowNum"), p.xs.intVal(3)))
        .select(p.as("node", p.sem.ifExpr(p.eq(p.col("rowNum"), p.xs.intVal(1)),
          p.jsonDocument(
            p.jsonObject(
              p.prop("p1", p.jsonString("s")),
              p.prop("p2", p.jsonArray(
                p.jsonString(p.col("city"))
              ))
            )
          ),
          p.xmlDocument(
            p.xmlElement("e",
              p.xmlAttribute("a", "s"),
              p.xmlText(p.col("city"))
            )
          )
          )),
          p.as("jxpath", p.xpath("node", "/p2")),
          p.as("xxpath", p.xpath("node", "/e/text()")));

    RowSet<RowRecord> recordRowSet = rowMgr.resultRows(builtPlan);

    Iterator<RowRecord> recordRowItr = recordRowSet.iterator();
    assertTrue( recordRowItr.hasNext());
    RowRecord recordRow = recordRowItr.next();
    assertTrue( recordRow.containsKey("node"));
    assertEquals( RowRecord.ColumnKind.CONTENT, recordRow.getKind("node"));
    assertEquals( "application/json", recordRow.getContentMimetype("node"));
    JacksonHandle jsonNode = recordRow.getContent("node", new JacksonHandle());
    JsonNode jsonRoot = jsonNode.get();
    assertEquals( "s", jsonRoot.findValue("p1").asText());
    assertEquals( "New York", jsonRoot.findValue("p2").get(0).asText());
    assertTrue( recordRow.containsKey("jxpath"));
    assertEquals( "New York", recordRow.getContentAs("jxpath", String.class));
    assertEquals( RowRecord.ColumnKind.NULL, recordRow.getKind("xxpath"));

    assertTrue( recordRowItr.hasNext());
    recordRow = recordRowItr.next();
    assertTrue( recordRow.containsKey("node"));
    assertEquals( RowRecord.ColumnKind.CONTENT, recordRow.getKind("node"));
    assertEquals( "application/xml", recordRow.getContentMimetype("node"));
    DOMHandle xmlNode = recordRow.getContent("node", new DOMHandle());
    Element xmlRoot = xmlNode.get().getDocumentElement();
    assertEquals( "e", xmlRoot.getLocalName());
    assertEquals( "s", xmlRoot.getAttribute("a"));
    assertEquals( "Seattle", xmlRoot.getTextContent());
    assertEquals( RowRecord.ColumnKind.NULL, recordRow.getKind("jxpath"));
    assertTrue( recordRow.containsKey("xxpath"));
    assertEquals( "Seattle", recordRow.getContentAs("xxpath", String.class));

    assertFalse( recordRowItr.hasNext());

    recordRowSet.close();
  }
  @Test
  public void testAggregates() throws IOException {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    @SuppressWarnings("unchecked")
    Map<String,Object>[] testRows = new Map[5];

    Map<String,Object> row = new HashMap<>();
    row.put("rowNum", 1);
    row.put("group",  1);
    row.put("val",    "a");
    testRows[0] = row;

    row = new HashMap<>();
    row.put("rowNum", 2);
    row.put("group",  1);
    row.put("val",    "b");
    testRows[1] = row;

    row = new HashMap<>();
    row.put("rowNum", 3);
    row.put("group",  1);
    row.put("val",    "a");
    testRows[2] = row;

    row = new HashMap<>();
    row.put("rowNum", 4);
    row.put("group",  2);
    row.put("val",    "c");
    testRows[3] = row;

    row = new HashMap<>();
    row.put("rowNum", 5);
    row.put("group",  2);
    row.put("val",    "d");
    testRows[4] = row;

    PlanBuilder.ExportablePlan builtPlan =
      p.fromLiterals(testRows)
        .groupBy(p.col("group"), p.groupConcat("vals", "val",
          p.groupConcatOptions("-", PlanValueOption.DISTINCT)
        ))
        .orderBy(p.col("group"));

    RowSet<RowRecord> recordRowSet = rowMgr.resultRows(builtPlan);

    Iterator<RowRecord> recordRowItr = recordRowSet.iterator();
    assertTrue( recordRowItr.hasNext());
    RowRecord recordRow = recordRowItr.next();
    assertEquals( 1,     recordRow.getInt("group"));
    assertEquals(  "a-b", recordRow.getString("vals"));

    recordRow = recordRowItr.next();
    assertEquals( 2,     recordRow.getInt("group"));
    assertEquals(  "c-d", recordRow.getString("vals"));

    assertFalse( recordRowItr.hasNext());

    recordRowSet.close();
  }

  @Test
  public void testMapper() {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.ExportablePlan builtPlan =
      p.fromLiterals(litRows)
        .select(p.colSeq("rowNum", "city"))
        .orderBy(p.col("rowNum"))
        .limit(3)
        .map(p.resolveFunction(p.xs.QName("secondsMapper"), "/etc/optic/test/processors.sjs"));

    for (RowRecord row: rowMgr.resultRows(builtPlan)) {
      assertNotNull(row.getInt("rowNum"));
      assertNotNull(row.getString("city"));
      int seconds = row.getInt("seconds");
      assertTrue(0 <= seconds && seconds < 60);
    }

    builtPlan =
      p.fromLiterals(litRows)
        .select(p.colSeq("rowNum", "city"))
        .orderBy(p.col("rowNum"))
        .limit(3)
        .map(p.resolveFunction(
          p.xs.QName(new QName("http://marklogic.com/optic/test/processors", "seconds-mapper")),
          p.xs.string("/etc/optic/test/processors.xqy")
        ));

    for (RowRecord row: rowMgr.resultRows(builtPlan)) {
      assertNotNull(row.getInt("rowNum"));
      assertNotNull(row.getString("city"));
      int seconds = row.getInt("seconds");
      assertTrue(0 <= seconds && seconds < 60);
    }
  }

  @Test
  public void testColumnInfo() {
    RowManager rowMgr = Common.client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanBuilder.PreparePlan builtPlan =
        p.fromView("opticUnitTest", VIEW_NAME)
            .where(
                p.cts.andQuery(
                    p.cts.jsonPropertyWordQuery("instrument", "trumpet"),
                    p.cts.jsonPropertyWordQuery(p.xs.string("lastName"), p.xs.stringSeq("Armstrong", "Davis"))
                )
            )
            .orderBy(p.col("lastName"));

    String[] expectedColumnInfos = new String[]{
        "{\"schema\":\"opticUnitTest\", \"view\":\"musician_ml10\", \"column\":\"lastName\", \"type\":\"string\"",
        "{\"schema\":\"opticUnitTest\", \"view\":\"musician_ml10\", \"column\":\"firstName\", \"type\":\"string\"",
        "{\"schema\":\"opticUnitTest\", \"view\":\"musician_ml10\", \"column\":\"dob\", \"type\":\"date\"",
        "{\"schema\":\"opticUnitTest\", \"view\":\"musician_ml10\", \"column\":\"rowid\", \"type\":\"rowid\""
    };

    String stringHandleResult = rowMgr.columnInfo(builtPlan, new StringHandle()).get();
    String stringResult = rowMgr.columnInfoAs(builtPlan, String.class);
    for (String columnInfo : expectedColumnInfos) {
      assertTrue(stringHandleResult.contains(columnInfo));
      assertTrue(stringResult.contains(columnInfo));
    }
  }

  @Test
  public void testGenerateView() {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.PreparePlan builtPlan =
          p.fromView("opticUnitTest", VIEW_NAME)
           .where(
              p.cts.andQuery(
                 p.cts.jsonPropertyWordQuery("instrument", "trumpet"),
                 p.cts.jsonPropertyWordQuery(p.xs.string("lastName"), p.xs.stringSeq("Armstrong", "Davis"))
                 )
              )
           .orderBy(p.col("lastName"));

    Document xmlRoot = rowMgr.generateView(builtPlan, "opticUnitTest", "musicianView", new DOMHandle()).get();
    assertNotNull(xmlRoot);
    xmlRoot = rowMgr.generateViewAs(builtPlan, "opticUnitTest", "musicianView", Document.class);
    assertNotNull(xmlRoot);
  }
  @Test
  public void testExplain() throws IOException {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.ExportablePlan builtPlan = p.fromLiterals(litRows);

    JsonNode jsonRoot = rowMgr.explain(builtPlan, new JacksonHandle()).get();
    assertNotNull(jsonRoot);
    jsonRoot = rowMgr.explainAs(builtPlan, JsonNode.class);
    assertNotNull(jsonRoot);

    Document xmlRoot = rowMgr.explain(builtPlan, new DOMHandle()).get();
    assertNotNull(xmlRoot);
    xmlRoot = rowMgr.explainAs(builtPlan, Document.class);
    assertNotNull(xmlRoot);

    String stringRoot = rowMgr.explain(builtPlan, new StringHandle()).get();
    assertNotNull(new ObjectMapper().readTree(stringRoot));
  }
  private DOMHandle initNamespaces(DOMHandle handle) {
    EditableNamespaceContext namespaces = new EditableNamespaceContext();
    namespaces.setNamespaceURI("table", "http://marklogic.com/table");

    handle.getXPathProcessor().setNamespaceContext(namespaces);

    return handle;
  }
  @Test
  public void testRawSQL() throws IOException {
    String plan = "SELECT *\n" +
            "FROM opticUnitTest.musician_ml10 AS ''\n" +
            "WHERE lastName IN ('Armstrong', 'Davis')" +
            "ORDER BY lastName;\n";

    RowManager rowMgr = Common.client.newRowManager();

    RawPlan builtPlan = rowMgr.newRawSQLPlan(new StringHandle(plan));
    testViewRows(rowMgr.resultRows(builtPlan));

    String stringRoot = rowMgr.explain(builtPlan, new StringHandle()).get();
    assertNotNull(new ObjectMapper().readTree(stringRoot));
  }
  @Test
  public void testRawSPARQLSelect() throws IOException {
    String plan = "PREFIX rg: <http://example.org/rowgraph/>\n" +
            "SELECT ?graph ?object1 ?object2\n" +
            "WHERE {?graph rg:p1 ?object1 ; rg:p2 ?object2}\n" +
            "ORDER BY ?graph";

    String[] graph   = {"http://example.org/rowgraph/s1", "http://example.org/rowgraph/s2"};
    String[] object1 = {"http://example.org/rowgraph/o1", "http://example.org/rowgraph/o3"};
    String[] object2 = {"http://example.org/rowgraph/o2", "http://example.org/rowgraph/o4"};

    RowManager rowMgr = Common.client.newRowManager();
    RawPlan builtPlan = rowMgr.newRawSPARQLSelectPlan(new StringHandle(plan));
    List<RowRecord> rows = rowMgr.resultRows(builtPlan).stream().collect(Collectors.toList());

    if (Common.getMarkLogicVersion().getMajor() >= 11) {
      assertEquals(18, rows.size(),
		  "Starting in ML 11, dedup is off by default, so 18 rows are expected");
    } else {
      assertEquals(2, rows.size(),
		  "In ML 10, dedup is on by default, so only 2 rows are expected");
    }

    String stringRoot = rowMgr.explain(builtPlan, new StringHandle()).get();
    assertNotNull(new ObjectMapper().readTree(stringRoot));
  }
  @Test
  public void testRawQueryDSL() throws IOException {
    String plan =
            "op.fromView('opticUnitTest', 'musician_ml10')\n" +
            "  .where(cts.andQuery([\n"+
            "       cts.jsonPropertyWordQuery('instrument', 'trumpet'),\n"+
            "       cts.jsonPropertyWordQuery('lastName', ['Armstrong', 'Davis'])\n"+
            "       ]))\n"+
            "  .select(null, '')\n"+
            "  .orderBy('lastName');\n";

    RowManager rowMgr = Common.client.newRowManager();

    RawPlan builtPlan = rowMgr.newRawQueryDSLPlan(new StringHandle(plan));
    testViewRows(rowMgr.resultRows(builtPlan));

    String stringRoot = rowMgr.explain(builtPlan, new StringHandle()).get();
    assertNotNull(new ObjectMapper().readTree(stringRoot));
  }
  @Test
  public void testSparqlOptions() throws IOException {
    String selectStmt = "PREFIX ad: <http://marklogicsparql.com/addressbook#> " +
            "SELECT ?firstName " +
            "WHERE {<#5555> ad:firstName ?firstName .}";

    RowManager rowMgr = Common.client.newRowManager();
    PlanBuilder pb = rowMgr.newPlanBuilder();
    PlanSparqlOptions options = pb.sparqlOptions().withDeduplicated(false).withBase("http://marklogicsparql.com/id#");
    PlanBuilder.ModifyPlan plan = pb.fromSparql(selectStmt, "sparql", options);
    JacksonHandle jacksonHandle = new JacksonHandle();
    jacksonHandle.setMimetype("application/json");
    rowMgr.resultDoc(plan, jacksonHandle);

    JsonNode jsonBindingsNodes = jacksonHandle.get().path("rows");
    JsonNode node = jsonBindingsNodes.path(0);
    assertEquals( 6, jsonBindingsNodes.size());
    assertEquals( "Jim", node.path("sparql.firstName").path("value").asText());
  }
  @Test
  public void testSampleBy() throws IOException {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanSampleByOptions options = p.sampleByOptions().withLimit(2);

    PlanBuilder.ExportablePlan builtPlan =
            p.fromView("opticUnitTest", VIEW_NAME)
                    .sampleBy(options);

    RowSet<RowRecord> rows = rowMgr.resultRows(builtPlan);
    long count = rows.stream().count();
    assertEquals(2, count);
  }
  @Test
  public void testSampleByNoArg() throws IOException {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();

    PlanBuilder.ExportablePlan builtPlan =
            p.fromView("opticUnitTest", VIEW_NAME)
                    .sampleBy();

    RowSet<RowRecord> rows = rowMgr.resultRows(builtPlan);
    long count = rows.stream().count();
    assertEquals(4, count);
  }

  @Test
  @Disabled("Waiting on a fix for https://bugtrack.marklogic.com/58233")
  public void testBug58233() {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder op = rowMgr.newPlanBuilder();

    // This succeeds
    rowMgr.resultRows(op
        .fromLiterals(numberRows)
        .facetBy(
            op.namedGroupSeq(op.bucketGroup(op.xs.string("r"), op.col("r"), op.xs.integerSeq(2, 4)))
        )).stream().collect(Collectors.toList());

    // But this fails when namedGroupSeq is not included
    rowMgr.resultRows(op
        .fromLiterals(numberRows)
        .facetBy(
            op.bucketGroup(op.xs.string("r"), op.col("r"), op.xs.integerSeq(2, 4))
        )).stream().collect(Collectors.toList());
  }


  @Test
  public void testBucketGroup() {
    RowManager rowMgr = Common.client.newRowManager();

    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanBuilder.ExportablePlan builtPlan =
            p.fromLiterals(numberRows)
                    .groupToArrays(
                            p.bucketGroup(p.xs.string("r"), p.col("r"), p.xs.integerSeq(2,4)),
                            p.count("numRows")
            );

    Iterator<RowRecord> rows = rowMgr.resultRows(builtPlan).iterator();
    assertTrue( rows.hasNext());
    RowRecord row = rows.next();
    assertFalse( rows.hasNext());

    ObjectMapper mapper = new ObjectMapper();

    JsonNode expect =
            mapper.createArrayNode()
                    .add(mapper.createObjectNode()
                            .put("r_bucket", 1).put("numRows", 1))
                    .add(mapper.createObjectNode()
                            .put("r_bucket", 2).put("numRows", 2));
    JsonNode actual = row.getContainer("r");
    assertEquals( expect, actual);
  }

  @Test
  @ExtendWith(RequiresML11.class)
  public void testFromDocUrisWithWordQuery() {
    RowManager rowMgr = Common.client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanBuilder.ExportablePlan builtPlan = p.fromDocUris(p.cts.wordQuery("trumpet"), "");

    Set<String> uriSet = new HashSet<>();
    uriSet.add("/optic/test/musician4.json");
    uriSet.add("/optic/test/musician1.json");
    Iterator<RowRecord> rows = rowMgr.resultRows(builtPlan).iterator();
    while (rows.hasNext()){
      String temp = rows.next().get("uri").toString().replaceAll("^\"|\"$", "");
      assertTrue(uriSet.contains(temp));
      uriSet.remove(temp);
    }
    assertTrue(uriSet.size() == 0);
  }

  @Test
  @ExtendWith(RequiresML11.class)
  public void testFromDocUrisWithDirectoryQuery() {
    RowManager rowMgr = Common.client.newRowManager();
    PlanBuilder p = rowMgr.newPlanBuilder();
    PlanBuilder.ExportablePlan builtPlan = p.fromDocUris(p.cts.directoryQuery("/testFromDocUrisWithDirectoryQuery/"), "");
    DocumentMetadataHandle meta = new DocumentMetadataHandle().withCollections("testcollection");
    DataMovementManager moveMgr = Common.client.newDataMovementManager();
    WriteBatcher batcher = moveMgr.newWriteBatcher();
    batcher.addAs("/testFromDocUrisWithDirectoryQuery/doc1.txt", meta,"This is doc1");
    batcher.addAs("/testFromDocUrisWithDirectoryQuery/doc2.txt", meta, "This is doc2");
    batcher.addAs("/testFromDocUrisWithDirectoryQuery/doc3.txt", meta, "This is doc3");
    batcher.addAs("/testFromDocUrisWithDirectoryQueryNew/doc4.txt", meta, "This is doc4");
    batcher.flushAndWait();

    Set<String> uriSet = new HashSet<>();
    uriSet.add("/testFromDocUrisWithDirectoryQuery/doc1.txt");
    uriSet.add("/testFromDocUrisWithDirectoryQuery/doc2.txt");
    uriSet.add("/testFromDocUrisWithDirectoryQuery/doc3.txt");
    uriSet.add("/testFromDocUrisWithDirectoryQueryNew/doc4.txt");
    Iterator<RowRecord> rows = rowMgr.resultRows(builtPlan).iterator();
    while (rows.hasNext()){
      String temp = rows.next().get("uri").toString().replaceAll("^\"|\"$", "");
      assertTrue(uriSet.contains(temp));
      uriSet.remove(temp);
    }
    assertTrue(uriSet.size() == 1 && uriSet.contains("/testFromDocUrisWithDirectoryQueryNew/doc4.txt"));
  }

  private void checkSingleRow(NodeList row, RowSetPart datatypeStyle) {
    assertEquals( 2, row.getLength());
    Element testElement = (Element) row.item(0);
    assertEquals(  "rowNum", testElement.getAttribute("name"));
    if (datatypeStyle == RowSetPart.ROWS) {
      assertEquals(  "xs:integer", testElement.getAttribute("type"));
    }
    assertEquals( "2",      testElement.getTextContent());
    testElement = (Element) row.item(1);
    assertEquals(  "temp", testElement.getAttribute("name"));
    if (datatypeStyle == RowSetPart.ROWS) {
      assertEquals(  "xs:string", testElement.getAttribute("type"));
    }
    assertEquals( "72",   testElement.getTextContent());
  }
  private void checkHeader(String format, RowSet<?> rowset, RowSetPart datatypeStyle) {
    String[] columnNames = rowset.getColumnNames();
    assertEquals(2, columnNames.length);
    checkFirstHeader(  format, columnNames[0] );
    checkSecondHeader( format, columnNames[1] );
    if (datatypeStyle == RowSetPart.HEADER) {
      String[] columnTypes = rowset.getColumnTypes();
      assertEquals(2, columnTypes.length);
      checkFirstType(  format, columnTypes[0] );
      checkSecondType( format, columnTypes[1] );
    }
  }
  private void checkHeader(JsonNode header, RowSetPart datatypeStyle) {
    assertEquals( 2, header.size());

    switch(datatypeStyle) {
      case ROWS:
// [{"name":"rowNum"},{"name":"temp"}]
        checkFirstHeader(  header.get(0) );
        checkSecondHeader( header.get(1) );
        break;
      case HEADER:
// [{"name":"rowNum","type":"xs:integer"},{"name":"temp","type":"xs:string"}]
        checkFirstTypedHeader(  header.get(0) );
        checkSecondTypedHeader( header.get(1) );
        break;
      default:
        throw new IllegalArgumentException("unknown case for RowSetPart: "+datatypeStyle);
    }
  }
  private void checkSingleRow(JsonNode row, RowStructure rowstruct, RowSetPart datatypeStyle) {
    switch(datatypeStyle) {
      case ROWS:
        switch(rowstruct) {
          case OBJECT:
// {"rowNum":{"type":"xs:integer","value":2},"temp":{"type":"xs:string","value":"72"}}
            checkFirstObject(  row.get("rowNum") );
            checkSecondObject( row.get("temp")   );
            break;
          case ARRAY:
// [{"type":"xs:integer","value":2},{"type":"xs:string","value":"72"}]
            checkFirstObject(  row.get(0) );
            checkSecondObject( row.get(1) );
            break;
          default:
            throw new IllegalArgumentException("unknown case for RowStructure: "+rowstruct);
        }
        break;
      case HEADER:
        switch(rowstruct) {
          case OBJECT:
// {"rowNum":2,"temp":"72"}
            checkFirstValue("JSON",  row.get("rowNum").asText() );
            checkSecondValue("JSON", row.get("temp").asText()   );
            break;
          case ARRAY:
// [2,"72"]
            checkFirstValue("JSON",  row.get(0).asText() );
            checkSecondValue("JSON", row.get(1).asText() );
            break;
          default:
            throw new IllegalArgumentException("unknown case for RowStructure: "+rowstruct);
        }
        break;
      default:
        throw new IllegalArgumentException("unknown case for RowSetPart: "+datatypeStyle);
    }
  }
  private void checkFirstTypedHeader(JsonNode colNode) {
    checkFirstType("JSON", colNode.get("type").asText());
    checkFirstHeader(colNode);
  }
  private void checkSecondTypedHeader(JsonNode colNode) {
    checkSecondType("JSON", colNode.get("type").asText());
    checkSecondHeader(colNode);
  }
  private void checkFirstHeader(JsonNode colNode) {
    checkFirstHeader("JSON", colNode.get("name").asText());
  }
  private void checkSecondHeader(JsonNode colNode) {
    checkSecondHeader("JSON", colNode.get("name").asText());
  }
  private void checkFirstHeader(String format, String name) {
    assertEquals("rowNum", name, "unexpected first header name in "+format);
  }
  private void checkSecondHeader(String format, String name) {
    assertEquals("temp",  name, "unexpected second header name in "+format);
  }
  private void checkFirstObject(JsonNode object) {
    checkFirstType("JSON", object.get("type").asText());
    checkFirstValue("JSON", object.get("value").asText());
  }
  private void checkSecondObject(JsonNode object) {
    checkSecondType("JSON", object.get("type").asText());
    checkSecondValue("JSON", object.get("value").asText());
  }
  private void checkFirstType(String format, String type) {
    assertEquals("xs:integer",  type, "unexpected first binding type in "+format);
  }
  private void checkFirstValue(String format, String value) {
    assertEquals("2",  value, "unexpected first binding value in "+format);
  }
  private void checkSecondType(String format, String type) {
    assertEquals("xs:string",  type, "unexpected second binding type in "+format);
  }
  private void checkSecondValue(String format, String value) {
    assertEquals("72",  value, "unexpected second binding value in "+format);
  }
  private void checkSingleRow(RowRecord row) {
    assertEquals( "2",  row.getString("rowNum"));

    assertEquals( "72", row.getString("temp"));
  }
  private void checkColumnNames(String[] colNames, RowSet<?> rowSet) {
    assertArrayEquals( colNames, rowSet.getColumnNames());
  }
  private void checkXMLDocRows(RowSet<DOMHandle> rowSet)
    throws XPathExpressionException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, SAXException, IOException
  {
    int rowCount = 0;
    for (DOMHandle xmlRow: rowSet) {
      xmlRow = initNamespaces(xmlRow);

      NodeList row = xmlRow.evaluateXPath("/table:row/table:cell", NodeList.class);

      rowCount++;

      assertEquals( 3, row.getLength());

      Element testElement = (Element) row.item(0);
      assertEquals( "rowNum",  testElement.getAttribute("name"));
      assertEquals( String.valueOf(rowCount), testElement.getTextContent());

      testElement = (Element) row.item(1);
      assertEquals( "uri",               testElement.getAttribute("name"));
      assertEquals( uris[rowCount - 1], testElement.getTextContent());

      testElement = (Element) row.item(2);
      assertEquals( "doc", testElement.getAttribute("name"));
      if (uris[rowCount - 1].endsWith(".xml")) {
        assertXMLEqual("unexpected third binding value in XML",
          docs[rowCount - 1], nodeToString(testElement.getElementsByTagName("a").item(0))
        );
      } else {
        assertEquals( docs[rowCount - 1], testElement.getTextContent());
      }
    }
    assertEquals( 3, rowCount);
  }
  private void checkJSONDocRows(RowSet<JacksonHandle> rowSet) {
    int rowCount = 0;
    for (JacksonHandle jsonRow: rowSet) {
      JsonNode row = jsonRow.get();

      rowCount++;

      String value = row.findValue("rowNum").findValue("value").asText();
      assertEquals( String.valueOf(rowCount), value);

      value = row.findValue("uri").findValue("value").asText();
      assertEquals( uris[rowCount - 1], value);

      if (uris[rowCount - 1].endsWith(".json")) {
        value = row.findValue("doc").findValue("value").toString();
        assertEquals( docs[rowCount - 1].replace(" ", ""), value);
      } else {
        value = row.findValue("doc").findValue("value").asText();
        assertEquals( docs[rowCount - 1], value);
      }
    }
    assertEquals( 3, rowCount);
  }
  private void checkRecordDocRows(RowSet<RowRecord> rowSet) throws SAXException, IOException {
    int rowCount = 0;
    for (RowRecord row: rowSet) {
      rowCount++;

      assertEquals( ColumnKind.ATOMIC_VALUE, row.getKind("rowNum"));
      assertEquals( "xs:integer", row.getDatatype("rowNum"));
      assertEquals( rowCount, row.getInt("rowNum"));

      assertEquals( ColumnKind.ATOMIC_VALUE, row.getKind("uri"));
      assertEquals( "xs:string", row.getDatatype("uri"));
      assertEquals( uris[rowCount - 1], row.getString("uri"));

      assertEquals( ColumnKind.CONTENT, row.getKind("doc"));
      String doc = row.getContent("doc", new StringHandle()).get();
      if (uris[rowCount - 1].endsWith(".json")) {
        assertEquals(     Format.JSON,        row.getContentFormat("doc"));
        assertEquals(   "application/json", row.getContentMimetype("doc"));
        assertEquals( docs[rowCount - 1], doc);
      } else if (uris[rowCount - 1].endsWith(".xml")) {
        assertEquals(      Format.XML,         row.getContentFormat("doc"));
        assertEquals(    "application/xml",  row.getContentMimetype("doc"));
        assertXMLEqual("unexpected third binding XML value in row record", docs[rowCount - 1], doc);
      } else {
        assertEquals(   Format.TEXT,        row.getContentFormat("doc"));
        assertEquals( "text/plain",       row.getContentMimetype("doc"));
        assertEquals(    docs[rowCount - 1], doc);
      }
    }
    assertEquals( 3, rowCount);
  }
  private String nodeToString(Node node) throws TransformerException, TransformerFactoryConfigurationError
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
