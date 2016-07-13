package com.marklogic.client.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.util.EditableNamespaceContext;

public class RowManagerTest {
    private static Map<String,Object>[] rows = null;
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void beforeClass() throws IOException, InterruptedException {
		rows = new Map[3];
		Map<String,Object>   row  = new HashMap<String,Object>();
		row.put("rowNum", 1);
		row.put("city",   "New York");
		row.put("temp",   "82");
		rows[0] = row;
		row = new HashMap<String,Object>();
		row.put("rowNum", 2);
		row.put("city",   "Seattle");
		row.put("temp",   "72");
		rows[1] = row;
		row = new HashMap<String,Object>();
		row.put("rowNum", 3);
		row.put("city",   "Phoenix");
		row.put("temp",   "92");
		rows[2] = row;

/* TODO: document join from literals via uri or maybe eval user?
    private static final String MASTER_DETAIL_TDE_FILE  = "masterDetail.tdex";
    private static final String MASTER_DETAIL_DATA_FILE = "masterDetail.xml";

		BufferedReader         fileReader = null;
		XMLDocumentManager     docMgr     = null;
		DocumentMetadataHandle docMeta    = null;

		Common.connectDatabase("Schemas");
        fileReader = new BufferedReader(Common.testFileToReader(MASTER_DETAIL_TDE_FILE, "UTF-8"));
        docMgr = Common.client.newXMLDocumentManager();
        docMeta = new DocumentMetadataHandle();
        docMeta.getCollections().add("http://marklogic.com/xdmp/tde");
        docMgr.write("/optic/test/masterDetail.tdex", docMeta, new ReaderHandle(fileReader));
        fileReader.close();
		Common.release();

        Common.connect();
        fileReader = new BufferedReader(Common.testFileToReader(MASTER_DETAIL_DATA_FILE, "UTF-8"));
        docMgr     = Common.client.newXMLDocumentManager();
        docMeta    = new DocumentMetadataHandle();
        docMeta.getCollections().add("/optic/test");
        docMgr.write("/optic/test/masterDetail.xml",  docMeta, new ReaderHandle(fileReader));
        fileReader.close();

        // wait for reindexing
        Thread.sleep(1000);
 */
        Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testResultDoc() throws IOException, XPathExpressionException {
		RowManager rowMgr = Common.client.newRowManager();

		PlanBuilder p = rowMgr.newPlanBuilder();

		PlanBuilder.ExportablePlan plan =
				p.fromLiterals(rows)
				 .where(p.eq(p.col("city"), p.xs.string("Seattle")))
				 .select(p.cols("rowNum", "temp"));

		ReaderHandle readerHandle = new ReaderHandle();

		rowMgr.resultDoc(plan, readerHandle.withMimetype("text/csv"));

		LineNumberReader lineReader = new LineNumberReader(readerHandle.get());

		String[] cols = lineReader.readLine().split(",");
		assertArrayEquals("unexpected header", new String[]{"rowNum","temp"}, cols);

		cols = lineReader.readLine().split(",");
		assertArrayEquals("unexpected data", new String[]{"2","72"}, cols);

		lineReader.close();
		readerHandle.close();

        DOMHandle domHandle = initNamespaces(rowMgr.resultDoc(plan, new DOMHandle()));

        NodeList testList = domHandle.evaluateXPath("/sp:sparql/sp:head/sp:variable", NodeList.class);
        assertEquals("unexpected header count in XML", testList.getLength(), 2);
        Element testElement = (Element) testList.item(0);
        assertEquals("unexpected first header name in XML", testElement.getAttribute("name"), "rowNum");
        testElement = (Element) testList.item(1);
        assertEquals("unexpected second header name in XML", testElement.getAttribute("name"), "temp");

        testList = domHandle.evaluateXPath("/sp:sparql/sp:results/sp:result", NodeList.class);
        assertEquals("unexpected row count in XML", testList.getLength(), 1);

        testList = domHandle.evaluateXPath("/sp:sparql/sp:results/sp:result[1]/sp:binding", NodeList.class);
        checkSingleRow(testList);

        JsonNode testNode = rowMgr.resultDoc(plan, new JacksonHandle()).get();

        JsonNode arrayNode = testNode.findValue("vars");
        assertEquals("unexpected header count in JSON", arrayNode.size(), 2);
        
        assertEquals("unexpected first header name in JSON",  arrayNode.get(0).asText(), "rowNum");
        assertEquals("unexpected second header name in JSON", arrayNode.get(1).asText(), "temp");
        

        arrayNode = testNode.findValue("bindings");
        assertEquals("unexpected row count in JSON", arrayNode.size(), 1);

        checkSingleRow(arrayNode.get(0));
	}
	@Test
	public void testResultRows() throws IOException, XPathExpressionException {
		RowManager rowMgr = Common.client.newRowManager();

		PlanBuilder p = rowMgr.newPlanBuilder();

		PlanBuilder.ExportablePlan plan =
				p.fromLiterals(rows)
				 .where(p.eq(p.col("city"), p.xs.string("Seattle")))
				 .select(p.cols("rowNum", "temp"));

		RowSet<DOMHandle> xmlRowSet = rowMgr.resultRows(plan, new DOMHandle());

		Iterator<DOMHandle> xmlRowItr = xmlRowSet.iterator();
		DOMHandle xmlRow = initNamespaces(xmlRowItr.next());
        checkSingleRow(xmlRow.evaluateXPath("/sp:result/sp:binding", NodeList.class));
        assertFalse("expected one XML row", xmlRowItr.hasNext());

        xmlRowSet.close();

		RowSet<JacksonHandle> jsonRowSet = rowMgr.resultRows(plan, new JacksonHandle());

		Iterator<JacksonHandle> jsonRowItr = jsonRowSet.iterator();
		JacksonHandle jsonRow = jsonRowItr.next();
        checkSingleRow(jsonRow.get());
        assertFalse("expected one JSON row", jsonRowItr.hasNext());

        jsonRowSet.close();
	}
	private DOMHandle initNamespaces(DOMHandle handle) {
        EditableNamespaceContext namespaces = new EditableNamespaceContext();
        namespaces.setNamespaceURI("sp", "http://www.w3.org/2005/sparql-results#");

        handle.getXPathProcessor().setNamespaceContext(namespaces);

        return handle;
	}
	private void checkSingleRow(NodeList row) {
        assertEquals("unexpected column count in XML", row.getLength(), 2);
        Element testElement = (Element) row.item(0);
        assertEquals("unexpected first binding name in XML", testElement.getAttribute("name"), "rowNum");
        assertEquals("unexpected first binding value in XML", testElement.getTextContent(), "2");
        testElement = (Element) row.item(1);
        assertEquals("unexpected second binding name in XML", testElement.getAttribute("name"), "temp");
        assertEquals("unexpected first binding value in XML", testElement.getTextContent(), "72");
	}
	private void checkSingleRow(JsonNode row) {
        String value = row.findValue("rowNum").findValue("value").asText();
        assertEquals("unexpected first binding value in JSON", value, "2");
        value = row.findValue("temp").findValue("value").asText();
        assertEquals("unexpected first binding value in JSON", value, "72");
	}
}
