/*
 * Copyright (c) 2019 MarkLogic Corporation
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.Format;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.type.ServerExpression;
import com.marklogic.client.type.PlanExprCol;

public class PlanGeneratedBase {
  protected static RowManager             rowMgr = null;
  protected static PlanBuilder            p      = null;
  protected static PlanBuilder.AccessPlan lit    = null;

  @SuppressWarnings("unchecked")
  @BeforeClass
  public static void beforeClass() {
    Common.connect();
    rowMgr = Common.client.newRowManager();

    p = rowMgr.newPlanBuilder();

    Map<String,Object> row = new HashMap<String,Object>();
    row.put("rowId", 1);

    lit = p.fromLiterals(new Map[]{row});

  }
  @AfterClass
  public static void afterClass() {
    lit    = null;
    p      = null;
    rowMgr = null;

  }

  private PlanBuilder.ModifyPlan makePlan(ServerExpression[] expressions) {
    if (expressions == null || expressions.length == 0) {
      return lit;
    }

    PlanExprCol[] bindings = new PlanExprCol[expressions.length];
    for (int i=0; i < expressions.length; i++) {
      bindings[i] = p.as(String.valueOf(i + 1), expressions[i]);
    }

    return lit.select(bindings);
  }

  protected void executeTester(String testName, ServerExpression expression, boolean isVolatile, String type, String kind, Format format, String expected, ServerExpression[] expressions) {
    PlanBuilder.ModifyPlan plan = makePlan(expressions).select(p.as("t", expression));

System.out.println(plan.exportAs(String.class));

    RowSet<RowRecord>   rowSet = rowMgr.resultRows(plan);
    Iterator<RowRecord> rowItr = rowSet.iterator();
    assertTrue("no row to test for: "+testName, rowItr.hasNext());

    RowRecord row = rowItr.next();

    if (expected == null) {
      assertEquals(testName, RowRecord.ColumnKind.NULL, row.getKind("t"));
    } else if (isVolatile) {
      assertFalse(testName, row.getKind("t") == RowRecord.ColumnKind.NULL);
    } else if (format != null) {
      switch (format) {
        case JSON:
          checkJSON(testName, kind, expected.trim(), row);
          break;
        case XML:
          checkXML(testName, kind, expected.trim(), row);
          break;
        default:
          throw new IllegalArgumentException("unsupported format: "+format.toString());
      }
    } else {
      String actualVal = row.getString("t");
      assertEquals(testName, expected.trim(), actualVal.trim());
// TODO: assertions on type if set, allowing for fallback to base type
    }

    assertFalse("too many results for: "+testName, rowItr.hasNext());
    try {
      rowSet.close();
    } catch(IOException e) {
      throw new MarkLogicIOException("close for: "+testName, e);
    }
  }
  private void checkJSON(String testName, String kind, String expectedRaw, RowRecord row) {
    try {
// TODO: assertions on kind if set
      assertEquals(testName, Format.JSON, row.getContentFormat("t"));
      assertEquals(testName, "application/json", row.getContentMimetype("t"));
      if (expectedRaw.length() > 2) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode expected = mapper.readTree(expectedRaw);
        JsonNode actual   = row.getContentAs("t", JsonNode.class);
        assertEquals(testName, expected, actual);
      } else {
        assertEquals(testName, expectedRaw, row.getContentAs("t", String.class).trim());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  private void checkXML(String testName, String kind, String expectedRaw, RowRecord row) {
    try {
// TODO: assertions on kind if set
      assertEquals(testName, Format.XML, row.getContentFormat("t"));
      assertEquals(testName, "application/xml", row.getContentMimetype("t"));
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setCoalescing(true);
      factory.setNamespaceAware(true);
      factory.setValidating(false);
      DocumentBuilder builder = factory.newDocumentBuilder();

      Document expected = builder.parse(new ByteArrayInputStream(expectedRaw.getBytes()));
      Document actual   = row.getContentAs("t", Document.class);

      assertXMLEqual(testName, expected, actual);
    } catch (SAXException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }
}
