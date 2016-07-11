package com.marklogic.client.test;

import static org.junit.Assert.*;

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
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.util.EditableNamespaceContext;

public class RowManagerTest {
	static Map<String,Object>[] rows = null;
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void beforeClass() {
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

        EditableNamespaceContext namespaces = new EditableNamespaceContext();
        namespaces.setNamespaceURI("sp", "http://www.w3.org/2005/sparql-results#");

        DOMHandle domHandle = rowMgr.resultDoc(plan, new DOMHandle());
        domHandle.getXPathProcessor().setNamespaceContext(namespaces);

        NodeList testList = domHandle.evaluateXPath("/sp:sparql/sp:head/sp:variable", NodeList.class);
        assertEquals("unexpected header count in XML", testList.getLength(), 2);
        Element testElement = (Element) testList.item(0);
        assertEquals("unexpected first header name in XML", testElement.getAttribute("name"), "rowNum");
        testElement = (Element) testList.item(1);
        assertEquals("unexpected second header name in XML", testElement.getAttribute("name"), "temp");

        testList = domHandle.evaluateXPath("/sp:sparql/sp:results/sp:result", NodeList.class);
        assertEquals("unexpected row count in XML", testList.getLength(), 1);

        testList = domHandle.evaluateXPath("/sp:sparql/sp:results/sp:result[1]/sp:binding", NodeList.class);
        assertEquals("unexpected column count in XML", testList.getLength(), 2);
        testElement = (Element) testList.item(0);
        assertEquals("unexpected first binding name in XML", testElement.getAttribute("name"), "rowNum");
        assertEquals("unexpected first binding value in XML", testElement.getTextContent(), "2");
        testElement = (Element) testList.item(1);
        assertEquals("unexpected second binding name in XML", testElement.getAttribute("name"), "temp");
        assertEquals("unexpected first binding value in XML", testElement.getTextContent(), "72");

        JsonNode testNode = rowMgr.resultDoc(plan, new JacksonHandle()).get();

        JsonNode arrayNode = testNode.findValue("vars");
        assertEquals("unexpected header count in JSON", arrayNode.size(), 2);
        
        assertEquals("unexpected first header name in JSON",  arrayNode.get(0).asText(), "rowNum");
        assertEquals("unexpected second header name in JSON", arrayNode.get(1).asText(), "temp");
        

        arrayNode = testNode.findValue("bindings");
        assertEquals("unexpected row count in JSON", arrayNode.size(), 1);

        String value = arrayNode.get(0).findValue("rowNum").findValue("value").asText();
        assertEquals("unexpected first binding value in JSON", value, "2");
        value = arrayNode.get(0).findValue("temp").findValue("value").asText();
        assertEquals("unexpected first binding value in JSON", value, "72");
	}
}
