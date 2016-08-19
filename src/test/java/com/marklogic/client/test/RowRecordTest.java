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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.type.ItemVal;
import com.marklogic.client.type.PlanExprCol;
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
// TODO:
//		datatypedValues.put("qname",             p.xs.qname("http://a", "a", "b"));
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

		Common.release();
	}

	@SuppressWarnings("incomplete-switch")
	@Test
	public void testDatatypeRoundtrip() throws Exception {
		Map<String,Object> literalRow = new HashMap<String,Object>();
		literalRow.put("rowId", 1);

		PlanExprCol[] cols = (PlanExprCol[])
			datatypedValues
		    	.entrySet()
		    	.stream()
		    	.map(entry -> p.as(entry.getKey(), entry.getValue()))
		    	.toArray(size -> new PlanExprCol[size]);

		@SuppressWarnings("unchecked")
		PlanBuilder.ModifyPlan plan = p.fromLiterals(new Map[]{literalRow}).select(cols);

		RowSet<RowRecord>   rowSet = rowMgr.resultRows(plan);
		Iterator<RowRecord> rowItr = rowSet.iterator();
		assertTrue("no row to test for datatypes", rowItr.hasNext());

		RowRecord row = rowItr.next();
		datatypedValues.forEach((key,expected) -> {
			boolean isFloatingPoint = (expected instanceof XsDoubleVal || expected instanceof XsFloatVal);

			String expectedStr = expected.toString();
			String actualStr   = row.getString(key);
			if (isFloatingPoint && expectedStr.length() < actualStr.length()) {
				actualStr = actualStr.substring(0, expectedStr.length());
			}
			assertEquals("string comparison for: "+key, expectedStr, actualStr);

			RowRecord.ColumnKind expectedKind =
				key.equals("iri") ? RowRecord.ColumnKind.URI : RowRecord.ColumnKind.ATOMIC_VALUE;
			RowRecord.ColumnKind actualKind   = row.getKind(key);
			assertEquals("column kind for: "+key, expectedKind, actualKind);

			QName actualDatatype = row.getAtomicDatatype(key);
			switch(actualKind) {
			case ATOMIC_VALUE:
				String expectedTypeName = key;
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
				case "langString":
					expectedTypeName = "string";
					break;
				}
				assertEquals("column datatype for: "+key,
						new QName("http://www.w3.org/2001/XMLSchema", expectedTypeName),
						actualDatatype
						);
				break;
			case URI:
				assertNull("uri column kind with non-null datatype for: "+key, actualDatatype);
				break;
			}

			if (expected instanceof XsAnyAtomicTypeVal) {
				@SuppressWarnings("unchecked")
				Class<? extends XsAnyAtomicTypeVal> expectedClass = (Class<? extends XsAnyAtomicTypeVal>) expected.getClass();
				String name = expectedClass.getSimpleName();
				name = name.substring(0, name.length() - "Impl".length());
				try {
					assertNotNull("null value for: "+key, row.getValueAs(key, expectedClass));

					@SuppressWarnings("unchecked")
					Class<? extends XsAnyAtomicTypeVal> expectedInterface =
							(Class<? extends XsAnyAtomicTypeVal>) Class.forName("com.marklogic.client.type.Xs"+name);
					assertNotNull("null value for: "+key, row.getValueAs(key, expectedInterface));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			// TODO: sem.iri and rdf.langStr

			switch(key) {
			case "boolean":
				assertEquals("boolean overload", ((XsBooleanVal) expected).getBoolean(), row.getBoolean(key));
				break;
			case "byte":
				assertEquals("byte overload", ((XsByteVal) expected).getByte(), row.getByte(key));
				break;
			case "double":
				assertEquals("double overload", ((XsDoubleVal) expected).getDouble(), row.getDouble(key), 0.1);
				break;
			case "float":
				assertEquals("float overload", ((XsFloatVal) expected).getFloat(), row.getFloat(key), 0.1);
				break;
			case "int":
				assertEquals("int overload", ((XsIntVal) expected).getInt(), row.getInt(key));
				break;
			case "long":
				assertEquals("long overload", ((XsLongVal) expected).getLong(), row.getLong(key));
				break;
			case "short":
				assertEquals("short overload", ((XsShortVal) expected).getShort(), row.getShort(key));
				break;
			}
		});
// TODO: content

		assertFalse("too many results for datatypes", rowItr.hasNext());
        rowSet.close();
	}
}
