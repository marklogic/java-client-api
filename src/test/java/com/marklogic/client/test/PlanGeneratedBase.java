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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.impl.BaseTypeImpl.BaseArgImpl;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import com.marklogic.client.row.RowSet;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.XsStringExpr;

public class PlanGeneratedBase {
	protected static RowManager             rowMgr = null;
	protected static PlanBuilder            p      = null;
	protected static PlanBuilder.AccessPlan lit    = null;

	protected static Pattern quotedNonstring = null;
	protected static Pattern integerDecimal  = null;
	protected static Pattern arraySequence   = null;
	protected static Pattern trimmedDate     = null;
	protected static Pattern trimmedDatetime = null;
	protected static Pattern trimmedGMonth   = null;
	protected static Pattern deconstNumeric  = null;
	protected static Pattern deconstString   = null;
	protected static Pattern normalDate      = null;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void beforeClass() {
        Common.connect();
		rowMgr = Common.client.newRowManager();

		p = rowMgr.newPlanBuilder();

		Map<String,Object> row = new HashMap<String,Object>();
		row.put("rowId", 1);

		lit = p.fromLiterals(new Map[]{row});

		// insignificant variations in the serialization
		quotedNonstring = Pattern.compile("\"(-?\\d+(?:\\.\\d+)?|true|false)\"");
		integerDecimal  = Pattern.compile("\"(-?\\d+)\\.0+\"");
		arraySequence   = Pattern.compile("(\\[|\\s+)\\[(\\{[^}]+\\})\\]([,}\\]])");
		trimmedDate     = Pattern.compile(
"(\\{\"ns\":\"xs\", \"fn\":\"date\", \"args\":\\[\"\\d\\d\\d\\d-\\d\\d-\\d\\d)(?:-\\d\\d:\\d\\d|Z)(\"\\]\\})"
				);
		trimmedDatetime = Pattern.compile(
"(\\{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":\\[\"\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d)\\d\\d\\d(-\\d\\d:\\d\\d\"\\]\\})"
				);
		trimmedGMonth   = Pattern.compile(
"(\\{\"ns\":\"xs\", \"fn\":\"gMonth\", \"args\":\\[\"--\\d\\d)--(\"\\]\\})"
				);

		deconstNumeric  = Pattern.compile("xs:(?:byte|decimal|double|float|int|long|numeric|short|unsigned\\w+)\\(\"([^\"]*)\"\\)");
		deconstString   = Pattern.compile("xs:(?:anyURI|untypedAtomic)\\((\"[^\"]*\")\\)");
		normalDate      = Pattern.compile("(xs:date\\(\"\\d\\d\\d\\d-\\d\\d-\\d\\d)(?:-\\d\\d:\\d\\d|Z)(\"\\))");
	}
	@AfterClass
	public static void afterClass() {
		lit    = null;
		p      = null;
		rowMgr = null;

		Common.release();
	}

	protected void exportTester(String testName, ItemSeqExpr expression, String expected) {
		expected =   arraySequence.matcher(expected).replaceAll("$1$2$3");
		expected = quotedNonstring.matcher(expected).replaceAll("$1");
		expected =     trimmedDate.matcher(expected).replaceAll("$1$2");
		expected = trimmedDatetime.matcher(expected).replaceAll("$1$2");

		String actual = ((BaseArgImpl) expression).exportAst(new StringBuilder()).toString();
		actual =  integerDecimal.matcher(actual).replaceAll("$1");
		actual = quotedNonstring.matcher(actual).replaceAll("$1");
		actual =     trimmedDate.matcher(actual).replaceAll("$1$2");
// TODO: DELETE HERE AND ABOVE
//		actual =   trimmedGMonth.matcher(actual).replaceAll("$1$2");

		assertEquals(testName, expected, actual);
	}
	protected void executeTester(String testName, ItemSeqExpr expression) {
		executeTester(testName, expression, null);
	}
	protected void executeTester(String testName, ItemSeqExpr expression, String expected) {
		boolean withCompare = (expected != null);

		XsStringExpr testExpr = p.xdmp.describe(expression, null, null);

		PlanBuilder.ModifyPlan plan = lit.select(p.as("t", testExpr));

		RowSet<RowRecord>   rowSet = rowMgr.resultRows(plan);
		Iterator<RowRecord> rowItr = rowSet.iterator();
		assertTrue("no row to test for: "+testName, rowItr.hasNext());

		RowRecord row = rowItr.next();
		String actual = row.getString("t");
		if (withCompare) {
			expected = deconstNumeric.matcher(expected).replaceAll("$1");
			expected =  deconstString.matcher(expected).replaceAll("$1");
			expected =     normalDate.matcher(expected).replaceAll("$1$2");
			actual   = deconstNumeric.matcher(actual).replaceAll("$1");
			actual   =  deconstString.matcher(actual).replaceAll("$1");
			actual   =     normalDate.matcher(actual).replaceAll("$1$2");
	        assertEquals("unexpected result for: "+testName, expected, actual);
		} else {
			assertNotNull("no result for: "+testName, actual);
		}

		assertFalse("too many results for: "+testName, rowItr.hasNext());
		try {
	        rowSet.close();
		} catch(IOException e) {
			throw new MarkLogicIOException("close for: "+testName, e);
		}
	}
}
