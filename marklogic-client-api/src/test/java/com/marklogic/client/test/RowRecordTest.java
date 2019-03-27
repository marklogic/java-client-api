/*
 * Copyright 2016-2019 MarkLogic Corporation
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.type.ItemVal;
import com.marklogic.client.type.PlanExprCol;
import com.marklogic.client.type.PlanPrefixer;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.type.XsBooleanVal;
import com.marklogic.client.type.XsByteVal;
import com.marklogic.client.type.XsDoubleVal;
import com.marklogic.client.type.XsFloatVal;
import com.marklogic.client.type.XsIntVal;
import com.marklogic.client.type.XsLongVal;
import com.marklogic.client.type.XsShortVal;

public class RowRecordTest {
  protected static RowManager  rowMgr = null;
  protected static PlanBuilder p      = null;

  protected static Map<String,ItemVal> datatypedValues = null;
  @BeforeClass
  public static void beforeClass() {
    Common.connect();
    rowMgr = Common.client.newRowManager();

    p = rowMgr.newPlanBuilder();

    datatypedValues = new HashMap<String,ItemVal>();
    datatypedValues.put("anyURI",            p.xs.string("http://a/b?c#d"));
    datatypedValues.put("boolean",           p.xs.booleanVal(true));
    datatypedValues.put("byte",              p.xs.byteVal((byte) 1));
    datatypedValues.put("date",              p.xs.date("2016-01-02"));
    datatypedValues.put("dateTime",          p.xs.dateTime("2016-01-02T10:09:08Z"));
    datatypedValues.put("dayTimeDuration",   p.xs.dayTimeDuration("P3DT4H5M6S"));
    datatypedValues.put("decimal",           p.xs.decimal(1.2));
    datatypedValues.put("double",            p.xs.doubleVal(1.2));
    datatypedValues.put("float",             p.xs.floatVal((float) 1.2));
    datatypedValues.put("gDay",              p.xs.gDay("---02"));
    datatypedValues.put("gMonth",            p.xs.gMonth("--01"));
    datatypedValues.put("gMonthDay",         p.xs.gMonthDay("--01-02"));
    datatypedValues.put("gYear",             p.xs.gYear("2016"));
    datatypedValues.put("gYearMonth",        p.xs.gYearMonth("2016-01"));
    datatypedValues.put("int",               p.xs.intVal(1));
    datatypedValues.put("integer",           p.xs.integer(1));
    datatypedValues.put("long",              p.xs.longVal((long) 1));
// TODO: incorrect QName support
//        datatypedValues.put("QName",             p.xs.QName("http://a", "a", "b"));
    datatypedValues.put("short",             p.xs.shortVal((short) 1));
    datatypedValues.put("string",            p.xs.string("abc"));
    datatypedValues.put("unsignedInt",       p.xs.unsignedInt(1));
    datatypedValues.put("unsignedLong",      p.xs.unsignedLong(1));
    datatypedValues.put("time",              p.xs.time("10:09:08Z"));
    datatypedValues.put("yearMonthDuration", p.xs.yearMonthDuration("P1Y2M"));
    datatypedValues.put("langString",        p.rdf.langString("abc", "en"));
    datatypedValues.put("iri",               p.sem.iri("http://a/b"));
  }
  @AfterClass
  public static void afterClass() {
    p      = null;
    rowMgr = null;

  }

  @Test
  public void prefixerTest() {
    // verify consistency with SJS prefixer
    String[] prefixes = {"a", "a/", "a#", "a?"};
    String[] suffixes = {"b", "/b", "#b", "?b"};
    String[][] results = {
      {"a/b", "a/b", "a/b", "a/b"},
      {"a/b", "a/b", "a/b", "a/b"},
      {"a#b", "a#b", "a#b", "a#b"},
      {"a?b", "a?b", "a?b", "a?b"}
    };

    for (int prefix=0; prefix < prefixes.length; prefix++) {
      PlanPrefixer prefixer = p.prefixer(prefixes[prefix]);
      for (int suffix=0; suffix < suffixes.length; suffix++) {
        assertEquals("prefixer "+prefix+","+suffix,
          p.sem.iri(results[prefix][suffix]).getString(),
          prefixer.iri(suffixes[suffix]).getString()
        );
      }
    }
  }

  @SuppressWarnings("incomplete-switch")
  @Test
  public void testDatatypeRoundtrip() throws Exception {
    Map<String,Object> literalRow = new HashMap<String,Object>();
    literalRow.put("rowId", 1);

    Map<String,PlanExprCol> cols =
      datatypedValues
        .entrySet()
        .stream()
        .collect(Collectors.toMap(
          entry -> entry.getKey(),
          entry -> p.as(entry.getKey(), entry.getValue())
        ));

    @SuppressWarnings("unchecked")
    PlanBuilder.ModifyPlan plan = p.fromLiterals(new Map[]{literalRow}).select(
      cols.values().toArray(new PlanExprCol[cols.size()])
    );

    RowSet<RowRecord>   rowSet = rowMgr.resultRows(plan);
    Iterator<RowRecord> rowItr = rowSet.iterator();
    assertTrue("no row to test for datatypes", rowItr.hasNext());

    RowRecord row = rowItr.next();
    datatypedValues.forEach((key,expected) -> {
      boolean isFloatingPoint = (expected instanceof XsDoubleVal || expected instanceof XsFloatVal);
      PlanExprCol col = cols.get(key);
      for (boolean useKey: new boolean[]{true, false}) {
        String expectedStr = expected.toString();
        String actualStr   =
          useKey ? row.getString(key) : row.getString(col);
        if (isFloatingPoint && expectedStr.length() < actualStr.length()) {
          actualStr = actualStr.substring(0, expectedStr.length());
        }
        assertEquals("string comparison for: "+key, expectedStr, actualStr);

        RowRecord.ColumnKind expectedKind = RowRecord.ColumnKind.ATOMIC_VALUE;
        RowRecord.ColumnKind actualKind   =
          useKey ? row.getKind(key) : row.getKind(col);
        assertEquals("column kind for: "+key, expectedKind, actualKind);

        String actualDatatype =
          useKey ? row.getDatatype(key) : row.getDatatype(col);
        switch(actualKind) {
          case ATOMIC_VALUE:
            String expectedTypePrefix = "xs";
            String expectedTypeName   = key;
            switch(key) {
              case "double":
              case "float":
                expectedTypeName = "decimal";
                break;
              case "byte":
              case "int":
              case "long":
              case "short":
              case "unsignedByte":
              case "unsignedInt":
              case "unsignedLong":
              case "unsignedShort":
                expectedTypeName = "integer";
                break;
              case "anyURI":
                expectedTypeName = "string";
                break;
              case "qname":
                expectedTypeName = "QName";
                break;
              case "langString":
                expectedTypePrefix = "rdf";
                break;
              case "iri":
                expectedTypePrefix = "sem";
                break;
            }
            assertEquals("column datatype for: "+key,
              expectedTypePrefix+":"+expectedTypeName,
              actualDatatype
            );
            break;
        }

// TODO: testing on RdfLangString and SemIri
        switch (key) {
          case "langString":
          case "iri":
            break;
          default:
            if (expected instanceof XsAnyAtomicTypeVal && key != "langString") {
              @SuppressWarnings("unchecked")
              Class<? extends XsAnyAtomicTypeVal> expectedClass = (Class<? extends XsAnyAtomicTypeVal>) expected.getClass();
              String name = expectedClass.getSimpleName();
              name = name.substring(0, name.length() - "Impl".length());
              try {
                assertNotNull("null value for: "+key,
                  useKey ? row.getValueAs(key, expectedClass) : row.getValueAs(col, expectedClass));

                @SuppressWarnings("unchecked")
                Class<? extends XsAnyAtomicTypeVal> expectedInterface =
                  (Class<? extends XsAnyAtomicTypeVal>) Class.forName("com.marklogic.client.type.Xs"+name);

                assertNotNull("null value for: "+key,
                  useKey ? row.getValueAs(key, expectedInterface) : row.getValueAs(col, expectedInterface));
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            }
            break;
        }

        switch(key) {
          case "boolean":
            assertEquals("boolean overload", ((XsBooleanVal) expected).getBoolean(),
              useKey ? row.getBoolean(key) : row.getBoolean(col));
            break;
          case "byte":
            assertEquals("byte overload", ((XsByteVal) expected).getByte(),
              useKey ? row.getByte(key) : row.getByte(col));
            break;
          case "double":
            assertEquals("double overload", ((XsDoubleVal) expected).getDouble(),
              useKey ? row.getDouble(key) : row.getDouble(col),
              0.1);
            break;
          case "float":
            assertEquals("float overload", ((XsFloatVal) expected).getFloat(),
              useKey ? row.getFloat(key) : row.getFloat(col),
              0.1);
            break;
          case "int":
            assertEquals("int overload", ((XsIntVal) expected).getInt(),
              useKey ? row.getInt(key) : row.getInt(col));
            break;
          case "long":
            assertEquals("long overload", ((XsLongVal) expected).getLong(),
              useKey ? row.getLong(key) : row.getLong(col));
            break;
          case "short":
            assertEquals("short overload", ((XsShortVal) expected).getShort(),
              useKey ? row.getShort(key) : row.getShort(col));
            break;
        }
      }
    });

    assertFalse("too many results for datatypes", rowItr.hasNext());
    rowSet.close();
  }

  @Test
  public void aliasTest() throws IOException {
    PlanBuilder.ModifyPlan plan =
      p.fromView("opticUnitTest", "musician")
        .orderBy(p.col("lastName"))
        .limit(1);

    RowSet<RowRecord>   rowSet = rowMgr.resultRows(plan);
    Iterator<RowRecord> rowItr = rowSet.iterator();
    assertTrue("no row to test for datatypes", rowItr.hasNext());

    RowRecord row = rowItr.next();

    for (String colName: new String[]{"opticUnitTest.musician.lastName", "musician.lastName", "lastName"}) {
      RowRecord.ColumnKind expectedKind = RowRecord.ColumnKind.ATOMIC_VALUE;
      RowRecord.ColumnKind actualKind   = row.getKind(colName);
      assertEquals("kind for alias: "+colName, expectedKind, actualKind);

      String datatype = row.getDatatype(colName);
      assertEquals("datatype for alias: "+colName, "xs:string", datatype);

      String value = row.getString(colName);
      assertEquals("value for alias: "+colName, "Armstrong", value);
    }

    assertFalse("too many results for alias", rowItr.hasNext());
    rowSet.close();
  }

  @Test
  public void toStringTest() throws IOException {
    Set<String> expected = new HashSet<>();
    expected.add("bool:{kind: \"ATOMIC_VALUE\", type: \"xs:boolean\", value: true},");
    expected.add("dec:{kind: \"ATOMIC_VALUE\", type: \"xs:decimal\", value: 3.3},");
    expected.add("int:{kind: \"ATOMIC_VALUE\", type: \"xs:integer\", value: 2},");
    expected.add("str:{kind: \"ATOMIC_VALUE\", type: \"xs:string\", value: \"string four\"},");

    Map<String,Object> literalRow = new HashMap<String,Object>();
    literalRow.put("bool", true);
    literalRow.put("int",  2);
    literalRow.put("dec",  3.3);
    literalRow.put("str",  "string four");

    @SuppressWarnings("unchecked")
    PlanBuilder.ModifyPlan plan = p.fromLiterals(new Map[]{literalRow});

    RowSet<RowRecord>   rowSet = rowMgr.resultRows(plan);
    Iterator<RowRecord> rowItr = rowSet.iterator();
    assertTrue("no row to test for datatypes", rowItr.hasNext());

    RowRecord row = rowItr.next();

    Set<String> actual = new BufferedReader(new StringReader(row.toString()))
      .lines()
      .map(line -> line.trim())
      .filter(line -> (!"{".equals(line) && !"}".equals(line)))
      .map(line -> (line.endsWith(",") ? line : line.concat(",")))
      .collect(Collectors.toSet());

    assertTrue("stringified record", expected.equals(actual));

    rowSet.close();
  }
  // Note: content payloads covered in RowManagerTest
}
