/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.rows;

import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.test.Common;
import com.marklogic.client.type.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class RowRecordTest {
  protected static RowManager  rowMgr = null;
  protected static PlanBuilder p      = null;

  protected static Map<String,ItemVal> datatypedValues = null;
  @BeforeAll
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
  @AfterAll
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
        assertEquals(
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
    assertTrue( rowItr.hasNext());

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
        assertEquals(expectedStr, actualStr);

        RowRecord.ColumnKind expectedKind = RowRecord.ColumnKind.ATOMIC_VALUE;
        RowRecord.ColumnKind actualKind   =
          useKey ? row.getKind(key) : row.getKind(col);
        assertEquals(expectedKind, actualKind);

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
            assertEquals(
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
                assertNotNull(
                  useKey ? row.getValueAs(key, expectedClass) : row.getValueAs(col, expectedClass));

                @SuppressWarnings("unchecked")
                Class<? extends XsAnyAtomicTypeVal> expectedInterface =
                  (Class<? extends XsAnyAtomicTypeVal>) Class.forName("com.marklogic.client.type.Xs"+name);

                assertNotNull(
                  useKey ? row.getValueAs(key, expectedInterface) : row.getValueAs(col, expectedInterface));
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            }
            break;
        }

        switch(key) {
          case "boolean":
            assertEquals(((XsBooleanVal) expected).getBoolean(),
              useKey ? row.getBoolean(key) : row.getBoolean(col));
            break;
          case "byte":
            assertEquals(((XsByteVal) expected).getByte(),
              useKey ? row.getByte(key) : row.getByte(col));
            break;
          case "double":
            assertEquals(((XsDoubleVal) expected).getDouble(),
              useKey ? row.getDouble(key) : row.getDouble(col),
              0.1);
            break;
          case "float":
            assertEquals(((XsFloatVal) expected).getFloat(),
              useKey ? row.getFloat(key) : row.getFloat(col),
              0.1);
            break;
          case "int":
            assertEquals(((XsIntVal) expected).getInt(),
              useKey ? row.getInt(key) : row.getInt(col));
            break;
          case "long":
            assertEquals(((XsLongVal) expected).getLong(),
              useKey ? row.getLong(key) : row.getLong(col));
            break;
          case "short":
            assertEquals(((XsShortVal) expected).getShort(),
              useKey ? row.getShort(key) : row.getShort(col));
            break;
        }
      }
    });

    assertFalse( rowItr.hasNext());
    rowSet.close();
  }

  @Test
  public void aliasTest() throws IOException {
    PlanBuilder.ModifyPlan plan =
      p.fromView("opticUnitTest", "musician_ml10")
        .orderBy(p.col("lastName"))
        .limit(1);

    RowSet<RowRecord>   rowSet = rowMgr.resultRows(plan);
    Iterator<RowRecord> rowItr = rowSet.iterator();
    assertTrue( rowItr.hasNext());

    RowRecord row = rowItr.next();

    for (String colName: new String[]{"opticUnitTest.musician_ml10.lastName", "musician_ml10.lastName", "lastName"}) {
      RowRecord.ColumnKind expectedKind = RowRecord.ColumnKind.ATOMIC_VALUE;
      RowRecord.ColumnKind actualKind   = row.getKind(colName);
      assertEquals(expectedKind, actualKind);

      String datatype = row.getDatatype(colName);
      assertEquals("xs:string", datatype);

      String value = row.getString(colName);
      assertEquals("Armstrong", value);
    }

    assertFalse( rowItr.hasNext());
    rowSet.close();
  }

  @Test
  public void toStringTest() throws IOException {
    Set<String> expected = new HashSet<>();
    expected.add("bool:{kind: \"ATOMIC_VALUE\", type: \"xs:boolean\", value: true},");
    expected.add("dec:{kind: \"ATOMIC_VALUE\", type: \"xs:decimal\", value: 3.3},");
    expected.add("int:{kind: \"ATOMIC_VALUE\", type: \"xs:integer\", value: 2},");
    expected.add("str:{kind: \"ATOMIC_VALUE\", type: \"xs:string\", value: \"string four\"},");

    Map<String,Object> literalRow = new HashMap<>();
    literalRow.put("bool", true);
    literalRow.put("int",  2);
    literalRow.put("dec",  3.3);
    literalRow.put("str",  "string four");

    @SuppressWarnings("unchecked")
    PlanBuilder.ModifyPlan plan = p.fromLiterals(new Map[]{literalRow});

    RowSet<RowRecord>   rowSet = rowMgr.resultRows(plan);
    Iterator<RowRecord> rowItr = rowSet.iterator();
    assertTrue( rowItr.hasNext());

    RowRecord row = rowItr.next();

    Set<String> actual = new BufferedReader(new StringReader(row.toString()))
      .lines()
      .map(line -> line.trim())
      .filter(line -> (!"{".equals(line) && !"}".equals(line)))
      .map(line -> (line.endsWith(",") ? line : line.concat(",")))
      .collect(Collectors.toSet());

    assertTrue( expected.equals(actual));

    rowSet.close();
  }
  // Note: content payloads covered in RowManagerTest
}
