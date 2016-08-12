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

import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.impl.BaseTypeImpl.BaseArgImpl;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.type.ItemSeqExpr;

public class PlanGeneratedBase {
	protected static PlanBuilder p = null;

	protected static Pattern quotedNonstring = null;
	protected static Pattern integerDecimal  = null;
	protected static Pattern arraySequence   = null;
	protected static Pattern trimmedDate     = null;
	protected static Pattern trimmedDatetime = null;
	protected static Pattern trimmedGMonth   = null;

	@BeforeClass
	public static void beforeClass() {
        Common.connect();
		RowManager rowMgr = Common.client.newRowManager();

		p = rowMgr.newPlanBuilder();

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
	}
	@AfterClass
	public static void afterClass() {
		p = null;

		Common.release();
	}

	protected void exportTester(String testName, String expected, ItemSeqExpr expression) {
		expected =   arraySequence.matcher(expected).replaceAll("$1$2$3");
		expected = quotedNonstring.matcher(expected).replaceAll("$1");
		expected =     trimmedDate.matcher(expected).replaceAll("$1$2");
		expected = trimmedDatetime.matcher(expected).replaceAll("$1$2");

		String actual = ((BaseArgImpl) expression).exportAst(new StringBuilder()).toString();
		actual =  integerDecimal.matcher(actual).replaceAll("$1");
		actual = quotedNonstring.matcher(actual).replaceAll("$1");
		actual =     trimmedDate.matcher(actual).replaceAll("$1$2");
		actual =   trimmedGMonth.matcher(actual).replaceAll("$1$2");

		assertEquals(testName, expected, actual);
	}
}
