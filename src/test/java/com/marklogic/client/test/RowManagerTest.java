/*
 * Copyright 2016-2017 MarkLogic Corporation
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowManager.RowSetPart;
import com.marklogic.client.row.RowManager.RowStructure;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowRecord.ColumnKind;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.type.CtsReferenceExpr;
import com.marklogic.client.type.PlanColumn;
import com.marklogic.client.type.PlanParamExpr;
import com.marklogic.client.type.PlanPrefixer;
import com.marklogic.client.type.PlanTripleOption;
import com.marklogic.client.type.PlanValueOption;
import com.marklogic.client.util.EditableNamespaceContext;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.InternalError;

public class RowManagerTest {
    private static String[]             uris           = null;
    private static String[]             docs           = null;
    private static Map<String,Object>[] litRows        = null;
    private static String[][]           triples        = null;
    private static RowStructure[]       rowstructs     = null;
    private static RowSetPart[]         datatypeStyles = null;

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
                  .add("/rowtest/triples1.xml", new StringHandle(triplesXML).withFormat(Format.TEXT))
              );

        rowstructs     = new RowStructure[]{ RowStructure.OBJECT, RowStructure.ARRAY };
        datatypeStyles = new RowSetPart[]{   RowSetPart.ROWS,     RowSetPart.HEADER  };
    }
    @AfterClass
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
        
        for (PlanBuilder.Plan plan: new PlanBuilder.Plan[]{builtPlan, rawPlan}) {
            try (ReaderHandle readerHandle = new ReaderHandle()) {
// TODO: also ARRAY
                rowMgr.setRowStructureStyle(RowStructure.OBJECT);
// TODO: also HEADER
                rowMgr.setDatatypeStyle(RowSetPart.ROWS);

                rowMgr.resultDoc(plan, readerHandle.withMimetype("text/csv"));
                
                try (LineNumberReader lineReader = new LineNumberReader(readerHandle.get())) {
                    String[] cols = lineReader.readLine().split(",");
                    assertArrayEquals("unexpected header", cols, new String[]{"rowNum","temp"});
                    
                    cols = lineReader.readLine().split(",");
                    assertArrayEquals("unexpected data", cols, new String[]{"2","72"});
                }
            }

            for (RowStructure rowstruct: rowstructs) {
                rowMgr.setRowStructureStyle(rowstruct);

                for (RowSetPart datatypeStyle: datatypeStyles) {
                    rowMgr.setDatatypeStyle(datatypeStyle);
                    
                    DOMHandle domHandle = initNamespaces(rowMgr.resultDoc(plan, new DOMHandle()));
// domHandle.write(System.out);

                    NodeList testList = domHandle.evaluateXPath("/table:table/table:columns/table:column", NodeList.class);
                    assertEquals("unexpected header count in XML", 2, testList.getLength());
                    Element testElement = (Element) testList.item(0);
                    assertEquals("unexpected first header name in XML", "rowNum", testElement.getAttribute("name"));
                    if (datatypeStyle == RowSetPart.HEADER) {
                        assertEquals("unexpected first header type in XML",  "xs:integer", testElement.getAttribute("type"));
                    }
                    testElement = (Element) testList.item(1);
                    assertEquals("unexpected second header name in XML", "temp", testElement.getAttribute("name"));
                    if (datatypeStyle == RowSetPart.HEADER) {
                        assertEquals("unexpected second header type in XML",  "xs:string", testElement.getAttribute("type"));
                    }

                    testList = domHandle.evaluateXPath("/table:table/table:rows/table:row", NodeList.class);
                    assertEquals("unexpected row count in XML", 1, testList.getLength());

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
                        assertEquals("unexpected row count in JSON", 1, rowsNode.size());

                        checkSingleRow(rowsNode.get(0), rowstruct, datatypeStyle);
                        break;
                    case ARRAY:
                        checkHeader(testNode.get(0), datatypeStyle);

                        rowsNode = testNode.get(1);
                        assertEquals("unexpected row count in JSON", 2, rowsNode.size());

                        checkSingleRow(rowsNode, rowstruct, datatypeStyle);
                        break;
                    default:
                        throw new InternalError("unknown case for RowStructure: "+rowstruct);
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
                        assertTrue("no XML row to iterate", xmlRowItr.hasNext());
                        DOMHandle xmlRow = initNamespaces(xmlRowItr.next());
                        checkSingleRow(xmlRow.evaluateXPath("/table:row/table:cell", NodeList.class), datatypeStyle);
                        assertFalse("expected one XML row", xmlRowItr.hasNext());
            
                        xmlRowSet.close();
                    }

                    xmlshortcut: {
                        RowSet<Document> xmlRowSetAs = rowMgr.resultRowsAs(plan, Document.class);
                        checkHeader("XML", xmlRowSetAs, datatypeStyle);

                        Iterator<Document> xmlRowItrAs = xmlRowSetAs.iterator();
                        assertTrue("no XML rows as to iterate", xmlRowItrAs.hasNext());
                        DOMHandle xmlRow = initNamespaces(new DOMHandle().with(xmlRowItrAs.next()));
                        checkSingleRow(xmlRow.evaluateXPath("/table:row/table:cell", NodeList.class), datatypeStyle);
                        assertFalse("expected one XML row", xmlRowItrAs.hasNext());

                        xmlRowSetAs.close();
                    }

                    jsonhandle: {
                        RowSet<JacksonHandle> jsonRowSet = rowMgr.resultRows(plan, new JacksonHandle());
                        checkHeader("JSON", jsonRowSet, datatypeStyle);

                        Iterator<JacksonHandle> jsonRowItr = jsonRowSet.iterator();
                        assertTrue("no JSON row to iterate", jsonRowItr.hasNext());
                        JacksonHandle jsonRow = jsonRowItr.next();
// jsonRow.write(System.out);

                        checkSingleRow(jsonRow.get(), rowstruct, datatypeStyle);
                        assertFalse("expected one JSON row", jsonRowItr.hasNext());

                        jsonRowSet.close();
                    }

                    jsonshortcut: {
                        RowSet<JsonNode> jsonRowSetAs = rowMgr.resultRowsAs(plan, JsonNode.class);
                        checkHeader("JSON", jsonRowSetAs, datatypeStyle);

                        Iterator<JsonNode> jsonRowItrAs = jsonRowSetAs.iterator();
                        assertTrue("no JSON row to iterate", jsonRowItrAs.hasNext());
                        checkSingleRow(jsonRowItrAs.next(), rowstruct, datatypeStyle);
                        assertFalse("expected one JSON row", jsonRowItrAs.hasNext());

                        jsonRowSetAs.close();
                    }

                     rowrecord: {
                         RowSet<RowRecord> recordRowSet = rowMgr.resultRows(plan);
                         Iterator<RowRecord> recordRowItr = recordRowSet.iterator();
                         assertTrue("no record row to iterate", recordRowItr.hasNext());
                         RowRecord recordRow = recordRowItr.next();
                         checkSingleRow(recordRow);
                         assertFalse("expected one record row", recordRowItr.hasNext());

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
                p.fromView("opticUnitTest", "musician")
                 .where(
                      p.cts.andQuery(
                          p.cts.jsonPropertyWordQuery("instrument", "trumpet"),
                          p.cts.jsonPropertyWordQuery(p.xs.string("lastName"),   p.xs.stringSeq("Armstrong", "Davis"))
                          )
                      )
                  .select(null, "") 
                  .orderBy(p.col("lastName"));

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
    public void testJoinSrcDoc() throws IOException {
        RowManager rowMgr = Common.client.newRowManager();

        PlanBuilder p = rowMgr.newPlanBuilder();

        PlanBuilder.ExportablePlan builtPlan =
                p.fromView("opticUnitTest", "musician", "", p.fragmentIdCol("musicianDocId"))
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
            assertEquals("unexpected lastName value in row record "+rowNum,  lastName[rowNum],  row.getString("lastName"));
            assertEquals("unexpected firstName value in row record "+rowNum, firstName[rowNum], row.getString("firstName"));

            String instruments = row.getContent("instruments", new StringHandle()).get();
            assertNotNull("null instrucments value in row record "+rowNum,    instruments);
            assertTrue("unexpected instrucments value in row record "+rowNum, instruments.contains("trumpet"));
            rowNum++;
        }
        assertEquals("unexpected count of result records", 2, rowNum);
    }
    @Test
    public void testJoinDocUri() throws IOException {
        RowManager rowMgr = Common.client.newRowManager();

        PlanBuilder p = rowMgr.newPlanBuilder();

        PlanBuilder.ExportablePlan builtPlan =
                p.fromView("opticUnitTest", "musician", "", p.fragmentIdCol("musicianDocId"))
                 .joinDocUri(p.col("musicianDocUri"), p.fragmentIdCol("musicianDocId"))
                 .orderBy(p.col("lastName"))
                 .select(p.col("lastName"), p.col("firstName"), p.col("musicianDocUri"))
                 .limit(2);

        String[] lastName       = {"Armstrong",                  "Byron"};
        String[] firstName      = {"Louis",                      "Don"};
        String[] musicianDocUri = {"/optic/test/musician1.json", "/optic/test/musician2.json"};

        int rowNum = 0;
        for (RowRecord row: rowMgr.resultRows(builtPlan)) {
            assertEquals("unexpected lastName value in row record "+rowNum,       lastName[rowNum],       row.getString("lastName"));
            assertEquals("unexpected firstName value in row record "+rowNum,      firstName[rowNum],      row.getString("firstName"));
            assertEquals("unexpected musicianDocUri value in row record "+rowNum, musicianDocUri[rowNum], row.getString("musicianDocUri"));
            rowNum++;
        }
        assertEquals("unexpected count of result records", 2, rowNum);
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
        assertTrue("no record row to iterate", recordRowItr.hasNext());
        RowRecord recordRow = recordRowItr.next();
        checkSingleRow(recordRow);
        assertFalse("expected one record row", recordRowItr.hasNext());

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
        assertTrue("no first node row", recordRowItr.hasNext());
        RowRecord recordRow = recordRowItr.next();
        assertEquals("first row num", 1,           recordRow.getInt("rowNum"));
        assertEquals("first cased",   "otherwise", recordRow.getString("cased"));

        assertTrue("no second node row", recordRowItr.hasNext());
        recordRow = recordRowItr.next();
        assertEquals("second row num", 2,        recordRow.getInt("rowNum"));
        assertEquals("second cased",   "second", recordRow.getString("cased"));

        assertTrue("no third node row", recordRowItr.hasNext());
        recordRow = recordRowItr.next();
        assertEquals("third row num", 3,       recordRow.getInt("rowNum"));
        assertEquals("third cased",   "third", recordRow.getString("cased"));

        assertFalse("expected three record rows", recordRowItr.hasNext());

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
        assertTrue("no JSON node row", recordRowItr.hasNext());
        RowRecord recordRow = recordRowItr.next();
        assertTrue("no JSON node value", recordRow.containsKey("node"));
        assertEquals("wrong JSON kind", RowRecord.ColumnKind.CONTENT, recordRow.getKind("node"));
        assertEquals("no JSON mime type", "application/json", recordRow.getContentMimetype("node"));
        JacksonHandle jsonNode = recordRow.getContent("node", new JacksonHandle());
        JsonNode jsonRoot = jsonNode.get();
        assertEquals("constructed JSON property p1", "s", jsonRoot.findValue("p1").asText());
        assertEquals("constructed JSON property p2", "New York", jsonRoot.findValue("p2").get(0).asText());
        assertTrue("no JSON XPath value", recordRow.containsKey("jxpath"));
        assertEquals("wrong JSON XPathValue", "New York", recordRow.getContentAs("jxpath", String.class));
        assertEquals("wrong XML XPathValue", RowRecord.ColumnKind.NULL, recordRow.getKind("xxpath"));

        assertTrue("no XML node row", recordRowItr.hasNext());
        recordRow = recordRowItr.next();
        assertTrue("no XML node value", recordRow.containsKey("node"));
        assertEquals("wrong XML kind", RowRecord.ColumnKind.CONTENT, recordRow.getKind("node"));
        assertEquals("no XML mime type", "application/xml", recordRow.getContentMimetype("node"));
        DOMHandle xmlNode = recordRow.getContent("node", new DOMHandle());
        Element xmlRoot = xmlNode.get().getDocumentElement();
        assertEquals("constructed XML element name", "e", xmlRoot.getLocalName());
        assertEquals("constructed XML attribute", "s", xmlRoot.getAttribute("a"));
        assertEquals("constructed XML text", "Seattle", xmlRoot.getTextContent());
        assertEquals("wrong JSON XPathValue", RowRecord.ColumnKind.NULL, recordRow.getKind("jxpath"));
        assertTrue("no XML XPath value", recordRow.containsKey("xxpath"));
        assertEquals("wrong XML XPathValue", "Seattle", recordRow.getContentAs("xxpath", String.class));

        assertFalse("expected two rows", recordRowItr.hasNext());

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
        assertTrue("no first group row", recordRowItr.hasNext());
        RowRecord recordRow = recordRowItr.next();
        assertEquals("first group", 1,     recordRow.getInt("group"));
        assertEquals("first vals",  "a-b", recordRow.getString("vals"));

        recordRow = recordRowItr.next();
        assertEquals("second group", 2,     recordRow.getInt("group"));
        assertEquals("second vals",  "c-d", recordRow.getString("vals"));

        assertFalse("expected two rows", recordRowItr.hasNext());

        recordRowSet.close();
    }
    @Test
    public void testMapper() throws IOException, XPathExpressionException {
        RowManager rowMgr = Common.client.newRowManager();

        PlanBuilder p = rowMgr.newPlanBuilder();

        PlanBuilder.ExportablePlan builtPlan =
                p.fromLiterals(litRows)
                 .select(p.colSeq("rowNum", "city"))
                 .orderBy(p.col("rowNum"))
                 .limit(3)
                 .map(p.resolveFunction(p.xs.QName("secondsMapper"), "/etc/optic/test/processors.sjs"));

        int rowNum = 0;
        for (RowRecord row: rowMgr.resultRows(builtPlan)) {
            assertNotNull("null rowNum value in row record "+rowNum, row.getInt("rowNum"));
            assertNotNull("null city value in row record "+rowNum, row.getString("city"));
            int seconds = row.getInt("seconds");
            assertTrue("unexpected seconds value in row record "+rowNum,  0 <= seconds && seconds < 60);
            rowNum++;
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

        rowNum = 0;
        for (RowRecord row: rowMgr.resultRows(builtPlan)) {
            assertNotNull("null rowNum value in row record "+rowNum, row.getInt("rowNum"));
            assertNotNull("null city value in row record "+rowNum, row.getString("city"));
            int seconds = row.getInt("seconds");
            assertTrue("unexpected seconds value in row record "+rowNum,  0 <= seconds && seconds < 60);
            rowNum++;
        }
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
    private void checkSingleRow(NodeList row, RowSetPart datatypeStyle) {
        assertEquals("unexpected column count in XML", 2, row.getLength());
        Element testElement = (Element) row.item(0);
        assertEquals("unexpected first binding name in XML",  "rowNum", testElement.getAttribute("name"));
        if (datatypeStyle == RowSetPart.ROWS) {
            assertEquals("unexpected first binding type in XML",  "xs:integer", testElement.getAttribute("type"));
        }
        assertEquals("unexpected first binding value in XML", "2",      testElement.getTextContent());
        testElement = (Element) row.item(1);
        assertEquals("unexpected second binding name in XML",  "temp", testElement.getAttribute("name"));
        if (datatypeStyle == RowSetPart.ROWS) {
            assertEquals("unexpected second binding type in XML",  "xs:string", testElement.getAttribute("type"));
        }
        assertEquals("unexpected second binding value in XML", "72",   testElement.getTextContent());
    }
    private void checkHeader(String format, RowSet<?> rowset, RowSetPart datatypeStyle) {
        String[] columnNames = rowset.getColumnNames();
        assertEquals("unexpected column name count in "+format, 2, columnNames.length);
        checkFirstHeader(  format, columnNames[0] );
        checkSecondHeader( format, columnNames[1] );
        if (datatypeStyle == RowSetPart.HEADER) {
            String[] columnTypes = rowset.getColumnTypes();
            assertEquals("unexpected column type count in "+format, 2, columnTypes.length);
            checkFirstType(  format, columnTypes[0] );
            checkSecondType( format, columnTypes[1] );
        }
    }
    private void checkHeader(JsonNode header, RowSetPart datatypeStyle) {
        assertEquals("unexpected header count in JSON", 2, header.size());

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
            throw new InternalError("unknown case for RowSetPart: "+datatypeStyle);
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
                throw new InternalError("unknown case for RowStructure: "+rowstruct);
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
                throw new InternalError("unknown case for RowStructure: "+rowstruct);
            }
            break;
        default:
            throw new InternalError("unknown case for RowSetPart: "+datatypeStyle);
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
        assertEquals("unexpected first header name in "+format,  "rowNum", name);
    }
    private void checkSecondHeader(String format, String name) {
        assertEquals("unexpected second header name in "+format, "temp",  name);
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
        assertEquals("unexpected first binding type in "+format, "xs:integer",  type);
    }
    private void checkFirstValue(String format, String value) {
        assertEquals("unexpected first binding value in "+format, "2",  value);
    }
    private void checkSecondType(String format, String type) {
        assertEquals("unexpected second binding type in "+format, "xs:string",  type);
    }
    private void checkSecondValue(String format, String value) {
        assertEquals("unexpected second binding value in "+format, "72",  value);
    }
    private void checkSingleRow(RowRecord row) {
        assertEquals("unexpected first binding value in row record", "2",  row.getString("rowNum"));

        assertEquals("unexpected first binding value in row record", "72", row.getString("temp"));
    }
    private void checkColumnNames(String[] colNames, RowSet<?> rowSet) {
        assertArrayEquals("unmatched column names", colNames, rowSet.getColumnNames());
    }
    private void checkXMLDocRows(RowSet<DOMHandle> rowSet)
    throws XPathExpressionException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, SAXException, IOException
    {
        int rowCount = 0;
        for (DOMHandle xmlRow: rowSet) {
            xmlRow = initNamespaces(xmlRow);

            NodeList row = xmlRow.evaluateXPath("/table:row/table:cell", NodeList.class);

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
            assertEquals("unexpected first binding datatype in row record", "xs:integer", row.getDatatype("rowNum"));
            assertEquals("unexpected first binding value in row record", rowCount, row.getInt("rowNum"));

            assertEquals("unexpected second binding kind in row record", ColumnKind.ATOMIC_VALUE, row.getKind("uri"));
            assertEquals("unexpected second binding datatype in row record", "xs:string", row.getDatatype("uri"));
            assertEquals("unexpected second binding value in row record", uris[rowCount - 1], row.getString("uri"));

            assertEquals("unexpected third binding kind in row record", ColumnKind.CONTENT, row.getKind("doc"));
            String doc = row.getContent("doc", new StringHandle()).get();
            if (uris[rowCount - 1].endsWith(".json")) {
                assertEquals("unexpected third binding format in row record",     Format.JSON,        row.getContentFormat("doc"));
                assertEquals("unexpected third binding mimetype in row record",   "application/json", row.getContentMimetype("doc"));
                assertEquals("unexpected third binding JSON value in row record", docs[rowCount - 1], doc);
            } else if (uris[rowCount - 1].endsWith(".xml")) {
                assertEquals("unexpected third binding format in row record",      Format.XML,         row.getContentFormat("doc"));
                assertEquals("unexpected third binding mimetype in row record",    "application/xml",  row.getContentMimetype("doc"));
                assertXMLEqual("unexpected third binding XML value in row record", docs[rowCount - 1], doc);
            } else {
                assertEquals("unexpected third binding format in row record",   Format.TEXT,        row.getContentFormat("doc"));
                assertEquals("unexpected third binding mimetype in row record", "text/plain",       row.getContentMimetype("doc"));
                assertEquals("unexpected third binding value in row record",    docs[rowCount - 1], doc);
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
