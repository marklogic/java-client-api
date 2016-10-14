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

import org.junit.Test;

// IMPORTANT: Do not edit. This file is generated.
public class PlanGeneratedTest extends PlanGeneratedBase {

    @Test
    public void testCtsStem1Exp() {
        exportTester("testCtsStem1", p.cts.stem(p.xs.string("ran")), "{\"ns\":\"cts\", \"fn\":\"stem\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"ran\"]}]}");
    }

    @Test
    public void testCtsStem1Exec() {
        executeTester("testCtsStem1", p.cts.stem(p.xs.string("ran")), "\"run\"");
    }

    @Test
    public void testCtsStem2Exp() {
        exportTester("testCtsStem2", p.cts.stem(p.xs.string("ran"), p.xs.string("en")), "{\"ns\":\"cts\", \"fn\":\"stem\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"ran\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en\"]}]}");
    }

    @Test
    public void testCtsStem2Exec() {
        executeTester("testCtsStem2", p.cts.stem(p.xs.string("ran"), p.xs.string("en")), "\"run\"");
    }

    @Test
    public void testCtsTokenize1Exp() {
        exportTester("testCtsTokenize1", p.cts.tokenize(p.xs.string("a-b c")), "{\"ns\":\"cts\", \"fn\":\"tokenize\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a-b c\"]}]}");
    }

    @Test
    public void testCtsTokenize1Exec() {
        executeTester("testCtsTokenize1", p.cts.tokenize(p.xs.string("a-b c")), "(cts:word(\"a\"), cts:punctuation(\"-\"), cts:word(\"b\"), cts:space(\" \"), cts:word(\"c\"))");
    }

    @Test
    public void testCtsTokenize2Exp() {
        exportTester("testCtsTokenize2", p.cts.tokenize(p.xs.string("a-b c"), p.xs.string("en")), "{\"ns\":\"cts\", \"fn\":\"tokenize\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a-b c\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en\"]}]}");
    }

    @Test
    public void testCtsTokenize2Exec() {
        executeTester("testCtsTokenize2", p.cts.tokenize(p.xs.string("a-b c"), p.xs.string("en")), "(cts:word(\"a\"), cts:punctuation(\"-\"), cts:word(\"b\"), cts:space(\" \"), cts:word(\"c\"))");
    }

    @Test
    public void testFnAbs1Exp() {
        exportTester("testFnAbs1", p.fn.abs(p.xs.doubleVal(-11)), "{\"ns\":\"fn\", \"fn\":\"abs\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[-11]}]}");
    }

    @Test
    public void testFnAbs1Exec() {
        executeTester("testFnAbs1", p.fn.abs(p.xs.doubleVal(-11)), "xs:double(\"11\")");
    }

    @Test
    public void testFnAdjustDateTimeToTimezone1Exp() {
        exportTester("testFnAdjustDateTimeToTimezone1", p.fn.adjustDateTimeToTimezone(p.xs.dateTime("2016-01-02T10:09:08Z")), "{\"ns\":\"fn\", \"fn\":\"adjust-dateTime-to-timezone\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}");
    }

    @Test
    public void testFnAdjustDateTimeToTimezone1Exec() {
        executeTester("testFnAdjustDateTimeToTimezone1", p.fn.adjustDateTimeToTimezone(p.xs.dateTime("2016-01-02T10:09:08Z")), "xs:dateTime(\"2016-01-02T06:09:08-04:00\")");
    }

    @Test
    public void testFnAdjustDateTimeToTimezone2Exp() {
        exportTester("testFnAdjustDateTimeToTimezone2", p.fn.adjustDateTimeToTimezone(p.xs.dateTime("2016-01-02T10:09:08Z"), p.xs.dayTimeDuration("-PT10H")), "{\"ns\":\"fn\", \"fn\":\"adjust-dateTime-to-timezone\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}, {\"ns\":\"xs\", \"fn\":\"dayTimeDuration\", \"args\":[\"-PT10H\"]}]}");
    }

    @Test
    public void testFnAdjustDateTimeToTimezone2Exec() {
        executeTester("testFnAdjustDateTimeToTimezone2", p.fn.adjustDateTimeToTimezone(p.xs.dateTime("2016-01-02T10:09:08Z"), p.xs.dayTimeDuration("-PT10H")), "xs:dateTime(\"2016-01-02T00:09:08-10:00\")");
    }

    @Test
    public void testFnAdjustDateToTimezone1Exp() {
        exportTester("testFnAdjustDateToTimezone1", p.fn.adjustDateToTimezone(p.xs.date("2016-01-02")), "{\"ns\":\"fn\", \"fn\":\"adjust-date-to-timezone\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}]}");
    }

    @Test
    public void testFnAdjustDateToTimezone1Exist() {
        executeTester("testFnAdjustDateToTimezone1", p.fn.adjustDateToTimezone(p.xs.date("2016-01-02")));
    }

    @Test
    public void testFnAdjustDateToTimezone2Exp() {
        exportTester("testFnAdjustDateToTimezone2", p.fn.adjustDateToTimezone(p.xs.date("2016-01-02"), p.xs.dayTimeDuration("-PT10H")), "{\"ns\":\"fn\", \"fn\":\"adjust-date-to-timezone\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}, {\"ns\":\"xs\", \"fn\":\"dayTimeDuration\", \"args\":[\"-PT10H\"]}]}");
    }

    @Test
    public void testFnAdjustDateToTimezone2Exist() {
        executeTester("testFnAdjustDateToTimezone2", p.fn.adjustDateToTimezone(p.xs.date("2016-01-02"), p.xs.dayTimeDuration("-PT10H")));
    }

    @Test
    public void testFnAdjustTimeToTimezone1Exp() {
        exportTester("testFnAdjustTimeToTimezone1", p.fn.adjustTimeToTimezone(p.xs.time("10:09:08Z")), "{\"ns\":\"fn\", \"fn\":\"adjust-time-to-timezone\", \"args\":[{\"ns\":\"xs\", \"fn\":\"time\", \"args\":[\"10:09:08Z\"]}]}");
    }

    @Test
    public void testFnAdjustTimeToTimezone1Exec() {
        executeTester("testFnAdjustTimeToTimezone1", p.fn.adjustTimeToTimezone(p.xs.time("10:09:08Z")), "xs:time(\"06:09:08-04:00\")");
    }

    @Test
    public void testFnAdjustTimeToTimezone2Exp() {
        exportTester("testFnAdjustTimeToTimezone2", p.fn.adjustTimeToTimezone(p.xs.time("10:09:08Z"), p.xs.dayTimeDuration("-PT10H")), "{\"ns\":\"fn\", \"fn\":\"adjust-time-to-timezone\", \"args\":[{\"ns\":\"xs\", \"fn\":\"time\", \"args\":[\"10:09:08Z\"]}, {\"ns\":\"xs\", \"fn\":\"dayTimeDuration\", \"args\":[\"-PT10H\"]}]}");
    }

    @Test
    public void testFnAdjustTimeToTimezone2Exec() {
        executeTester("testFnAdjustTimeToTimezone2", p.fn.adjustTimeToTimezone(p.xs.time("10:09:08Z"), p.xs.dayTimeDuration("-PT10H")), "xs:time(\"00:09:08-10:00\")");
    }

    @Test
    public void testFnAnalyzeString2Exp() {
        exportTester("testFnAnalyzeString2", p.fn.analyzeString(p.xs.string("aXbyc"), p.xs.string("[xy]")), "{\"ns\":\"fn\", \"fn\":\"analyze-string\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"aXbyc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"[xy]\"]}]}");
    }

    @Test
    public void testFnAnalyzeString2Exec() {
        executeTester("testFnAnalyzeString2", p.fn.analyzeString(p.xs.string("aXbyc"), p.xs.string("[xy]")), "<s:analyze-string-result xmlns:s=\"http://www.w3.org/2005/xpath-functions\"><s:non-match>aXb</s:non-match><s:match>y</s:match><s:non-match>c</s:non-match></s:analyze-string-result>");
    }

    @Test
    public void testFnAnalyzeString3Exp() {
        exportTester("testFnAnalyzeString3", p.fn.analyzeString(p.xs.string("aXbyc"), p.xs.string("[xy]"), p.xs.string("i")), "{\"ns\":\"fn\", \"fn\":\"analyze-string\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"aXbyc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"[xy]\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"i\"]}]}");
    }

    @Test
    public void testFnAnalyzeString3Exec() {
        executeTester("testFnAnalyzeString3", p.fn.analyzeString(p.xs.string("aXbyc"), p.xs.string("[xy]"), p.xs.string("i")), "<s:analyze-string-result xmlns:s=\"http://www.w3.org/2005/xpath-functions\"><s:non-match>a</s:non-match><s:match>X</s:match><s:non-match>b</s:non-match><s:match>y</s:match><s:non-match>c</s:non-match></s:analyze-string-result>");
    }

    @Test
    public void testFnAvg1Exp() {
        exportTester("testFnAvg1", p.fn.avg(p.xs.doubleVals(2, 4, 6, 8)), "{\"ns\":\"fn\", \"fn\":\"avg\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[4]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[6]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[8]}]]}");
    }

    @Test
    public void testFnAvg1Exec() {
        executeTester("testFnAvg1", p.fn.avg(p.xs.doubleVals(2, 4, 6, 8)), "xs:double(\"5\")");
    }

    @Test
    public void testFnBoolean1Exp() {
        exportTester("testFnBoolean1", p.fn.booleanExpr(p.xs.string("abc")), "{\"ns\":\"fn\", \"fn\":\"boolean\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}");
    }

    @Test
    public void testFnBoolean1Exec() {
        executeTester("testFnBoolean1", p.fn.booleanExpr(p.xs.string("abc")), "fn:true()");
    }

    @Test
    public void testFnCeiling1Exp() {
        exportTester("testFnCeiling1", p.fn.ceiling(p.xs.doubleVal(1.3)), "{\"ns\":\"fn\", \"fn\":\"ceiling\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.3]}]}");
    }

    @Test
    public void testFnCeiling1Exec() {
        executeTester("testFnCeiling1", p.fn.ceiling(p.xs.doubleVal(1.3)), "xs:double(\"2\")");
    }

    @Test
    public void testFnCodepointEqual2Exp() {
        exportTester("testFnCodepointEqual2", p.fn.codepointEqual(p.xs.string("abc"), p.xs.string("abc")), "{\"ns\":\"fn\", \"fn\":\"codepoint-equal\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testFnCodepointEqual2Exec() {
        executeTester("testFnCodepointEqual2", p.fn.codepointEqual(p.xs.string("abc"), p.xs.string("abc")), "fn:true()");
    }

    @Test
    public void testFnCodepointsToString1Exp() {
        exportTester("testFnCodepointsToString1", p.fn.codepointsToString(p.xs.integers(97, 98, 99)), "{\"ns\":\"fn\", \"fn\":\"codepoints-to-string\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[97]}, {\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[98]}, {\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[99]}]]}");
    }

    @Test
    public void testFnCodepointsToString1Exec() {
        executeTester("testFnCodepointsToString1", p.fn.codepointsToString(p.xs.integers(97, 98, 99)), "\"abc\"");
    }

    @Test
    public void testFnCompare2Exp() {
        exportTester("testFnCompare2", p.fn.compare(p.xs.string("abz"), p.xs.string("aba")), "{\"ns\":\"fn\", \"fn\":\"compare\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abz\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"aba\"]}]}");
    }

    @Test
    public void testFnCompare2Exec() {
        executeTester("testFnCompare2", p.fn.compare(p.xs.string("abz"), p.xs.string("aba")), "1");
    }

    @Test
    public void testFnCompare3Exp() {
        exportTester("testFnCompare3", p.fn.compare(p.xs.string("abz"), p.xs.string("aba"), p.xs.string("http://marklogic.com/collation/")), "{\"ns\":\"fn\", \"fn\":\"compare\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abz\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"aba\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}");
    }

    @Test
    public void testFnCompare3Exec() {
        executeTester("testFnCompare3", p.fn.compare(p.xs.string("abz"), p.xs.string("aba"), p.xs.string("http://marklogic.com/collation/")), "1");
    }

    @Test
    public void testFnConcat2Exp() {
        exportTester("testFnConcat2", p.fn.concat(p.xs.string("a"), p.xs.string("b")), "{\"ns\":\"fn\", \"fn\":\"concat\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}]}");
    }

    @Test
    public void testFnConcat2Exec() {
        executeTester("testFnConcat2", p.fn.concat(p.xs.string("a"), p.xs.string("b")), "\"ab\"");
    }

    @Test
    public void testFnConcat3Exp() {
        exportTester("testFnConcat3", p.fn.concat(p.xs.string("a"), p.xs.string("b"), p.xs.string("c")), "{\"ns\":\"fn\", \"fn\":\"concat\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]}");
    }

    @Test
    public void testFnConcat3Exec() {
        executeTester("testFnConcat3", p.fn.concat(p.xs.string("a"), p.xs.string("b"), p.xs.string("c")), "\"abc\"");
    }

    @Test
    public void testFnContains2Exp() {
        exportTester("testFnContains2", p.fn.contains(p.xs.string("abc"), p.xs.string("b")), "{\"ns\":\"fn\", \"fn\":\"contains\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}]}");
    }

    @Test
    public void testFnContains2Exec() {
        executeTester("testFnContains2", p.fn.contains(p.xs.string("abc"), p.xs.string("b")), "fn:true()");
    }

    @Test
    public void testFnContains3Exp() {
        exportTester("testFnContains3", p.fn.contains(p.xs.string("abc"), p.xs.string("b"), p.xs.string("http://marklogic.com/collation/")), "{\"ns\":\"fn\", \"fn\":\"contains\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}");
    }

    @Test
    public void testFnContains3Exec() {
        executeTester("testFnContains3", p.fn.contains(p.xs.string("abc"), p.xs.string("b"), p.xs.string("http://marklogic.com/collation/")), "fn:true()");
    }

    @Test
    public void testFnCount1Exp() {
        exportTester("testFnCount1", p.fn.count(p.xs.doubleVals(1, 2, 3)), "{\"ns\":\"fn\", \"fn\":\"count\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3]}]]}");
    }

    @Test
    public void testFnCount1Exec() {
        executeTester("testFnCount1", p.fn.count(p.xs.doubleVals(1, 2, 3)), "xs:unsignedLong(\"3\")");
    }

    @Test
    public void testFnCount2Exp() {
        exportTester("testFnCount2", p.fn.count(p.xs.doubleVals(1, 2, 3), p.xs.doubleVal(4)), "{\"ns\":\"fn\", \"fn\":\"count\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3]}], {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[4]}]}");
    }

    @Test
    public void testFnCount2Exec() {
        executeTester("testFnCount2", p.fn.count(p.xs.doubleVals(1, 2, 3), p.xs.doubleVal(4)), "xs:unsignedLong(\"3\")");
    }

    @Test
    public void testFnCurrentDate0Exp() {
        exportTester("testFnCurrentDate0", p.fn.currentDate(), "{\"ns\":\"fn\", \"fn\":\"current-date\", \"args\":[]}");
    }

    @Test
    public void testFnCurrentDate0Exist() {
        executeTester("testFnCurrentDate0", p.fn.currentDate());
    }

    @Test
    public void testFnCurrentDateTime0Exp() {
        exportTester("testFnCurrentDateTime0", p.fn.currentDateTime(), "{\"ns\":\"fn\", \"fn\":\"current-dateTime\", \"args\":[]}");
    }

    @Test
    public void testFnCurrentDateTime0Exist() {
        executeTester("testFnCurrentDateTime0", p.fn.currentDateTime());
    }

    @Test
    public void testFnCurrentTime0Exp() {
        exportTester("testFnCurrentTime0", p.fn.currentTime(), "{\"ns\":\"fn\", \"fn\":\"current-time\", \"args\":[]}");
    }

    @Test
    public void testFnCurrentTime0Exist() {
        executeTester("testFnCurrentTime0", p.fn.currentTime());
    }

    @Test
    public void testFnDayFromDate1Exp() {
        exportTester("testFnDayFromDate1", p.fn.dayFromDate(p.xs.date("2016-01-02-03:04")), "{\"ns\":\"fn\", \"fn\":\"day-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02-03:04\"]}]}");
    }

    @Test
    public void testFnDayFromDate1Exec() {
        executeTester("testFnDayFromDate1", p.fn.dayFromDate(p.xs.date("2016-01-02-03:04")), "2");
    }

    @Test
    public void testFnDayFromDateTime1Exp() {
        exportTester("testFnDayFromDateTime1", p.fn.dayFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")), "{\"ns\":\"fn\", \"fn\":\"day-from-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}");
    }

    @Test
    public void testFnDayFromDateTime1Exec() {
        executeTester("testFnDayFromDateTime1", p.fn.dayFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")), "2");
    }

    @Test
    public void testFnDaysFromDuration1Exp() {
        exportTester("testFnDaysFromDuration1", p.fn.daysFromDuration(p.xs.dayTimeDuration("P3DT4H5M6S")), "{\"ns\":\"fn\", \"fn\":\"days-from-duration\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dayTimeDuration\", \"args\":[\"P3DT4H5M6S\"]}]}");
    }

    @Test
    public void testFnDaysFromDuration1Exec() {
        executeTester("testFnDaysFromDuration1", p.fn.daysFromDuration(p.xs.dayTimeDuration("P3DT4H5M6S")), "3");
    }

    @Test
    public void testFnDeepEqual2Exp() {
        exportTester("testFnDeepEqual2", p.fn.deepEqual(p.xs.string("abc"), p.xs.string("abc")), "{\"ns\":\"fn\", \"fn\":\"deep-equal\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}");
    }

    @Test
    public void testFnDeepEqual2Exist() {
        executeTester("testFnDeepEqual2", p.fn.deepEqual(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testFnDeepEqual3Exp() {
        exportTester("testFnDeepEqual3", p.fn.deepEqual(p.xs.string("abc"), p.xs.string("abc"), p.xs.string("abc")), "{\"ns\":\"fn\", \"fn\":\"deep-equal\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testFnDeepEqual3Exist() {
        executeTester("testFnDeepEqual3", p.fn.deepEqual(p.xs.string("abc"), p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testFnDefaultCollation0Exp() {
        exportTester("testFnDefaultCollation0", p.fn.defaultCollation(), "{\"ns\":\"fn\", \"fn\":\"default-collation\", \"args\":[]}");
    }

    @Test
    public void testFnDefaultCollation0Exist() {
        executeTester("testFnDefaultCollation0", p.fn.defaultCollation());
    }

    @Test
    public void testFnDistinctValues1Exp() {
        exportTester("testFnDistinctValues1", p.fn.distinctValues(p.xs.strings("a", "b", "b", "c")), "{\"ns\":\"fn\", \"fn\":\"distinct-values\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]]}");
    }

    @Test
    public void testFnDistinctValues1Exec() {
        executeTester("testFnDistinctValues1", p.fn.distinctValues(p.xs.strings("a", "b", "b", "c")), "(\"a\", \"b\", \"c\")");
    }

    @Test
    public void testFnDistinctValues2Exp() {
        exportTester("testFnDistinctValues2", p.fn.distinctValues(p.xs.strings("a", "b", "b", "c"), p.xs.string("http://marklogic.com/collation/")), "{\"ns\":\"fn\", \"fn\":\"distinct-values\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}");
    }

    @Test
    public void testFnDistinctValues2Exec() {
        executeTester("testFnDistinctValues2", p.fn.distinctValues(p.xs.strings("a", "b", "b", "c"), p.xs.string("http://marklogic.com/collation/")), "(\"a\", \"b\", \"c\")");
    }

    @Test
    public void testFnEmpty1Exp() {
        exportTester("testFnEmpty1", p.fn.empty(p.xs.doubleVal(1)), "{\"ns\":\"fn\", \"fn\":\"empty\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}]]}");
    }

    @Test
    public void testFnEmpty1Exec() {
        executeTester("testFnEmpty1", p.fn.empty(p.xs.doubleVal(1)), "fn:false()");
    }

    @Test
    public void testFnEncodeForUri1Exp() {
        exportTester("testFnEncodeForUri1", p.fn.encodeForUri(p.xs.string("http://a/b?c#d")), "{\"ns\":\"fn\", \"fn\":\"encode-for-uri\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://a/b?c#d\"]}]}");
    }

    @Test
    public void testFnEncodeForUri1Exec() {
        executeTester("testFnEncodeForUri1", p.fn.encodeForUri(p.xs.string("http://a/b?c#d")), "\"http%3A%2F%2Fa%2Fb%3Fc%23d\"");
    }

    @Test
    public void testFnEndsWith2Exp() {
        exportTester("testFnEndsWith2", p.fn.endsWith(p.xs.string("abc"), p.xs.string("c")), "{\"ns\":\"fn\", \"fn\":\"ends-with\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]}");
    }

    @Test
    public void testFnEndsWith2Exec() {
        executeTester("testFnEndsWith2", p.fn.endsWith(p.xs.string("abc"), p.xs.string("c")), "fn:true()");
    }

    @Test
    public void testFnEndsWith3Exp() {
        exportTester("testFnEndsWith3", p.fn.endsWith(p.xs.string("abc"), p.xs.string("c"), p.xs.string("http://marklogic.com/collation/")), "{\"ns\":\"fn\", \"fn\":\"ends-with\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}");
    }

    @Test
    public void testFnEndsWith3Exec() {
        executeTester("testFnEndsWith3", p.fn.endsWith(p.xs.string("abc"), p.xs.string("c"), p.xs.string("http://marklogic.com/collation/")), "fn:true()");
    }

    @Test
    public void testFnEscapeHtmlUri1Exp() {
        exportTester("testFnEscapeHtmlUri1", p.fn.escapeHtmlUri(p.xs.string("http://a/b?c#d")), "{\"ns\":\"fn\", \"fn\":\"escape-html-uri\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://a/b?c#d\"]}]}");
    }

    @Test
    public void testFnEscapeHtmlUri1Exec() {
        executeTester("testFnEscapeHtmlUri1", p.fn.escapeHtmlUri(p.xs.string("http://a/b?c#d")), "\"http://a/b?c#d\"");
    }

    @Test
    public void testFnExists1Exp() {
        exportTester("testFnExists1", p.fn.exists(p.xs.doubleVal(1)), "{\"ns\":\"fn\", \"fn\":\"exists\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}]]}");
    }

    @Test
    public void testFnExists1Exec() {
        executeTester("testFnExists1", p.fn.exists(p.xs.doubleVal(1)), "fn:true()");
    }

    @Test
    public void testFnFalse0Exp() {
        exportTester("testFnFalse0", p.fn.falseExpr(), "{\"ns\":\"fn\", \"fn\":\"false\", \"args\":[]}");
    }

    @Test
    public void testFnFalse0Exist() {
        executeTester("testFnFalse0", p.fn.falseExpr());
    }

    @Test
    public void testFnFloor1Exp() {
        exportTester("testFnFloor1", p.fn.floor(p.xs.doubleVal(1.7)), "{\"ns\":\"fn\", \"fn\":\"floor\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.7]}]}");
    }

    @Test
    public void testFnFloor1Exec() {
        executeTester("testFnFloor1", p.fn.floor(p.xs.doubleVal(1.7)), "xs:double(\"1\")");
    }

    @Test
    public void testFnFormatDate2Exp() {
        exportTester("testFnFormatDate2", p.fn.formatDate(p.xs.date("2016-01-02-03:04"), p.xs.string("[Y0001]/[M01]/[D01]")), "{\"ns\":\"fn\", \"fn\":\"format-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02-03:04\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"[Y0001]/[M01]/[D01]\"]}]}");
    }

    @Test
    public void testFnFormatDate2Exec() {
        executeTester("testFnFormatDate2", p.fn.formatDate(p.xs.date("2016-01-02-03:04"), p.xs.string("[Y0001]/[M01]/[D01]")), "\"2016/01/02\"");
    }

    @Test
    public void testFnFormatDateTime2Exp() {
        exportTester("testFnFormatDateTime2", p.fn.formatDateTime(p.xs.dateTime("2016-01-02T10:09:08Z"), p.xs.string("[Y0001]/[M01]/[D01] [H01]:[m01]:[s01]:[f01]")), "{\"ns\":\"fn\", \"fn\":\"format-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"[Y0001]/[M01]/[D01] [H01]:[m01]:[s01]:[f01]\"]}]}");
    }

    @Test
    public void testFnFormatDateTime2Exec() {
        executeTester("testFnFormatDateTime2", p.fn.formatDateTime(p.xs.dateTime("2016-01-02T10:09:08Z"), p.xs.string("[Y0001]/[M01]/[D01] [H01]:[m01]:[s01]:[f01]")), "\"2016/01/02 10:09:08:00\"");
    }

    @Test
    public void testFnFormatNumber2Exp() {
        exportTester("testFnFormatNumber2", p.fn.formatNumber(p.xs.doubleVal(1234.5), p.xs.string("#,##0.00")), "{\"ns\":\"fn\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1234.5]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"#,##0.00\"]}]}");
    }

    @Test
    public void testFnFormatNumber2Exec() {
        executeTester("testFnFormatNumber2", p.fn.formatNumber(p.xs.doubleVal(1234.5), p.xs.string("#,##0.00")), "\"1,234.50\"");
    }

    @Test
    public void testFnFormatTime2Exp() {
        exportTester("testFnFormatTime2", p.fn.formatTime(p.xs.time("10:09:08Z"), p.xs.string("[H01]:[m01]:[s01]:[f01]")), "{\"ns\":\"fn\", \"fn\":\"format-time\", \"args\":[{\"ns\":\"xs\", \"fn\":\"time\", \"args\":[\"10:09:08Z\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"[H01]:[m01]:[s01]:[f01]\"]}]}");
    }

    @Test
    public void testFnFormatTime2Exec() {
        executeTester("testFnFormatTime2", p.fn.formatTime(p.xs.time("10:09:08Z"), p.xs.string("[H01]:[m01]:[s01]:[f01]")), "\"10:09:08:00\"");
    }

    @Test
    public void testFnHead1Exp() {
        exportTester("testFnHead1", p.fn.head(p.xs.strings("a", "b", "c")), "{\"ns\":\"fn\", \"fn\":\"head\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]]}");
    }

    @Test
    public void testFnHead1Exec() {
        executeTester("testFnHead1", p.fn.head(p.xs.strings("a", "b", "c")), "\"a\"");
    }

    @Test
    public void testFnHoursFromDateTime1Exp() {
        exportTester("testFnHoursFromDateTime1", p.fn.hoursFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")), "{\"ns\":\"fn\", \"fn\":\"hours-from-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}");
    }

    @Test
    public void testFnHoursFromDateTime1Exec() {
        executeTester("testFnHoursFromDateTime1", p.fn.hoursFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")), "10");
    }

    @Test
    public void testFnHoursFromDuration1Exp() {
        exportTester("testFnHoursFromDuration1", p.fn.hoursFromDuration(p.xs.dayTimeDuration("P3DT4H5M6S")), "{\"ns\":\"fn\", \"fn\":\"hours-from-duration\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dayTimeDuration\", \"args\":[\"P3DT4H5M6S\"]}]}");
    }

    @Test
    public void testFnHoursFromDuration1Exec() {
        executeTester("testFnHoursFromDuration1", p.fn.hoursFromDuration(p.xs.dayTimeDuration("P3DT4H5M6S")), "4");
    }

    @Test
    public void testFnHoursFromTime1Exp() {
        exportTester("testFnHoursFromTime1", p.fn.hoursFromTime(p.xs.time("10:09:08Z")), "{\"ns\":\"fn\", \"fn\":\"hours-from-time\", \"args\":[{\"ns\":\"xs\", \"fn\":\"time\", \"args\":[\"10:09:08Z\"]}]}");
    }

    @Test
    public void testFnHoursFromTime1Exec() {
        executeTester("testFnHoursFromTime1", p.fn.hoursFromTime(p.xs.time("10:09:08Z")), "10");
    }

    @Test
    public void testFnImplicitTimezone0Exp() {
        exportTester("testFnImplicitTimezone0", p.fn.implicitTimezone(), "{\"ns\":\"fn\", \"fn\":\"implicit-timezone\", \"args\":[]}");
    }

    @Test
    public void testFnImplicitTimezone0Exist() {
        executeTester("testFnImplicitTimezone0", p.fn.implicitTimezone());
    }

    @Test
    public void testFnIndexOf2Exp() {
        exportTester("testFnIndexOf2", p.fn.indexOf(p.xs.strings("a", "b", "c"), p.xs.string("b")), "{\"ns\":\"fn\", \"fn\":\"index-of\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}]}");
    }

    @Test
    public void testFnIndexOf2Exec() {
        executeTester("testFnIndexOf2", p.fn.indexOf(p.xs.strings("a", "b", "c"), p.xs.string("b")), "xs:unsignedLong(\"2\")");
    }

    @Test
    public void testFnIndexOf3Exp() {
        exportTester("testFnIndexOf3", p.fn.indexOf(p.xs.strings("a", "b", "c"), p.xs.string("b"), p.xs.string("http://marklogic.com/collation/")), "{\"ns\":\"fn\", \"fn\":\"index-of\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}");
    }

    @Test
    public void testFnIndexOf3Exec() {
        executeTester("testFnIndexOf3", p.fn.indexOf(p.xs.strings("a", "b", "c"), p.xs.string("b"), p.xs.string("http://marklogic.com/collation/")), "xs:unsignedLong(\"2\")");
    }

    @Test
    public void testFnInsertBefore3Exp() {
        exportTester("testFnInsertBefore3", p.fn.insertBefore(p.xs.strings("a", "b", "e", "f"), p.xs.integer(3), p.xs.strings("c", "d")), "{\"ns\":\"fn\", \"fn\":\"insert-before\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"e\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"f\"]}], {\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[3]}, [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"d\"]}]]}");
    }

    @Test
    public void testFnInsertBefore3Exec() {
        executeTester("testFnInsertBefore3", p.fn.insertBefore(p.xs.strings("a", "b", "e", "f"), p.xs.integer(3), p.xs.strings("c", "d")), "(\"a\", \"b\", \"c\", \"d\", \"e\", \"f\")");
    }

    @Test
    public void testFnIriToUri1Exp() {
        exportTester("testFnIriToUri1", p.fn.iriToUri(p.xs.string("http://a/b?c#d")), "{\"ns\":\"fn\", \"fn\":\"iri-to-uri\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://a/b?c#d\"]}]}");
    }

    @Test
    public void testFnIriToUri1Exec() {
        executeTester("testFnIriToUri1", p.fn.iriToUri(p.xs.string("http://a/b?c#d")), "\"http://a/b?c#d\"");
    }

    @Test
    public void testFnLocalNameFromQName1Exp() {
        exportTester("testFnLocalNameFromQName1", p.fn.localNameFromQName(p.xs.qname("abc")), "{\"ns\":\"fn\", \"fn\":\"local-name-from-QName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"QName\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testFnLocalNameFromQName1Exist() {
        executeTester("testFnLocalNameFromQName1", p.fn.localNameFromQName(p.xs.qname("abc")));
    }

    @Test
    public void testFnLowerCase1Exp() {
        exportTester("testFnLowerCase1", p.fn.lowerCase(p.xs.string("ABC")), "{\"ns\":\"fn\", \"fn\":\"lower-case\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"ABC\"]}]}");
    }

    @Test
    public void testFnLowerCase1Exec() {
        executeTester("testFnLowerCase1", p.fn.lowerCase(p.xs.string("ABC")), "\"abc\"");
    }

    @Test
    public void testFnMatches2Exp() {
        exportTester("testFnMatches2", p.fn.matches(p.xs.string("abc"), p.xs.string("^.B")), "{\"ns\":\"fn\", \"fn\":\"matches\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"^.B\"]}]}");
    }

    @Test
    public void testFnMatches2Exec() {
        executeTester("testFnMatches2", p.fn.matches(p.xs.string("abc"), p.xs.string("^.B")), "fn:false()");
    }

    @Test
    public void testFnMatches3Exp() {
        exportTester("testFnMatches3", p.fn.matches(p.xs.string("abc"), p.xs.string("^.B"), p.xs.string("i")), "{\"ns\":\"fn\", \"fn\":\"matches\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"^.B\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"i\"]}]}");
    }

    @Test
    public void testFnMatches3Exec() {
        executeTester("testFnMatches3", p.fn.matches(p.xs.string("abc"), p.xs.string("^.B"), p.xs.string("i")), "fn:true()");
    }

    @Test
    public void testFnMax1Exp() {
        exportTester("testFnMax1", p.fn.max(p.xs.strings("a", "b", "c")), "{\"ns\":\"fn\", \"fn\":\"max\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]]}");
    }

    @Test
    public void testFnMax1Exec() {
        executeTester("testFnMax1", p.fn.max(p.xs.strings("a", "b", "c")), "\"c\"");
    }

    @Test
    public void testFnMin1Exp() {
        exportTester("testFnMin1", p.fn.min(p.xs.strings("a", "b", "c")), "{\"ns\":\"fn\", \"fn\":\"min\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]]}");
    }

    @Test
    public void testFnMin1Exec() {
        executeTester("testFnMin1", p.fn.min(p.xs.strings("a", "b", "c")), "\"a\"");
    }

    @Test
    public void testFnMinutesFromDateTime1Exp() {
        exportTester("testFnMinutesFromDateTime1", p.fn.minutesFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")), "{\"ns\":\"fn\", \"fn\":\"minutes-from-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}");
    }

    @Test
    public void testFnMinutesFromDateTime1Exec() {
        executeTester("testFnMinutesFromDateTime1", p.fn.minutesFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")), "9");
    }

    @Test
    public void testFnMinutesFromDuration1Exp() {
        exportTester("testFnMinutesFromDuration1", p.fn.minutesFromDuration(p.xs.dayTimeDuration("P3DT4H5M6S")), "{\"ns\":\"fn\", \"fn\":\"minutes-from-duration\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dayTimeDuration\", \"args\":[\"P3DT4H5M6S\"]}]}");
    }

    @Test
    public void testFnMinutesFromDuration1Exec() {
        executeTester("testFnMinutesFromDuration1", p.fn.minutesFromDuration(p.xs.dayTimeDuration("P3DT4H5M6S")), "5");
    }

    @Test
    public void testFnMinutesFromTime1Exp() {
        exportTester("testFnMinutesFromTime1", p.fn.minutesFromTime(p.xs.time("10:09:08Z")), "{\"ns\":\"fn\", \"fn\":\"minutes-from-time\", \"args\":[{\"ns\":\"xs\", \"fn\":\"time\", \"args\":[\"10:09:08Z\"]}]}");
    }

    @Test
    public void testFnMinutesFromTime1Exec() {
        executeTester("testFnMinutesFromTime1", p.fn.minutesFromTime(p.xs.time("10:09:08Z")), "9");
    }

    @Test
    public void testFnMonthFromDate1Exp() {
        exportTester("testFnMonthFromDate1", p.fn.monthFromDate(p.xs.date("2016-01-02-03:04")), "{\"ns\":\"fn\", \"fn\":\"month-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02-03:04\"]}]}");
    }

    @Test
    public void testFnMonthFromDate1Exec() {
        executeTester("testFnMonthFromDate1", p.fn.monthFromDate(p.xs.date("2016-01-02-03:04")), "1");
    }

    @Test
    public void testFnMonthFromDateTime1Exp() {
        exportTester("testFnMonthFromDateTime1", p.fn.monthFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")), "{\"ns\":\"fn\", \"fn\":\"month-from-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}");
    }

    @Test
    public void testFnMonthFromDateTime1Exec() {
        executeTester("testFnMonthFromDateTime1", p.fn.monthFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")), "1");
    }

    @Test
    public void testFnMonthsFromDuration1Exp() {
        exportTester("testFnMonthsFromDuration1", p.fn.monthsFromDuration(p.xs.yearMonthDuration("P1Y2M")), "{\"ns\":\"fn\", \"fn\":\"months-from-duration\", \"args\":[{\"ns\":\"xs\", \"fn\":\"yearMonthDuration\", \"args\":[\"P1Y2M\"]}]}");
    }

    @Test
    public void testFnMonthsFromDuration1Exec() {
        executeTester("testFnMonthsFromDuration1", p.fn.monthsFromDuration(p.xs.yearMonthDuration("P1Y2M")), "2");
    }

    @Test
    public void testFnNamespaceUriFromQName1Exp() {
        exportTester("testFnNamespaceUriFromQName1", p.fn.namespaceUriFromQName(p.xs.qname("abc")), "{\"ns\":\"fn\", \"fn\":\"namespace-uri-from-QName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"QName\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testFnNamespaceUriFromQName1Exist() {
        executeTester("testFnNamespaceUriFromQName1", p.fn.namespaceUriFromQName(p.xs.qname("abc")));
    }

    @Test
    public void testFnNormalizeSpace1Exp() {
        exportTester("testFnNormalizeSpace1", p.fn.normalizeSpace(p.xs.string(" abc  123 ")), "{\"ns\":\"fn\", \"fn\":\"normalize-space\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\" abc  123 \"]}]}");
    }

    @Test
    public void testFnNormalizeSpace1Exec() {
        executeTester("testFnNormalizeSpace1", p.fn.normalizeSpace(p.xs.string(" abc  123 ")), "\"abc 123\"");
    }

    @Test
    public void testFnNormalizeUnicode1Exp() {
        exportTester("testFnNormalizeUnicode1", p.fn.normalizeUnicode(p.xs.string(" aBc ")), "{\"ns\":\"fn\", \"fn\":\"normalize-unicode\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\" aBc \"]}]}");
    }

    @Test
    public void testFnNormalizeUnicode1Exec() {
        executeTester("testFnNormalizeUnicode1", p.fn.normalizeUnicode(p.xs.string(" aBc ")), "\" aBc \"");
    }

    @Test
    public void testFnNormalizeUnicode2Exp() {
        exportTester("testFnNormalizeUnicode2", p.fn.normalizeUnicode(p.xs.string(" aBc "), p.xs.string("NFC")), "{\"ns\":\"fn\", \"fn\":\"normalize-unicode\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\" aBc \"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"NFC\"]}]}");
    }

    @Test
    public void testFnNormalizeUnicode2Exec() {
        executeTester("testFnNormalizeUnicode2", p.fn.normalizeUnicode(p.xs.string(" aBc "), p.xs.string("NFC")), "\" aBc \"");
    }

    @Test
    public void testFnNot1Exp() {
        exportTester("testFnNot1", p.fn.not(p.xs.booleanVal(true)), "{\"ns\":\"fn\", \"fn\":\"not\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[true]}]]}");
    }

    @Test
    public void testFnNot1Exec() {
        executeTester("testFnNot1", p.fn.not(p.xs.booleanVal(true)), "fn:false()");
    }

    @Test
    public void testFnNumber1Exp() {
        exportTester("testFnNumber1", p.fn.number(p.xs.string("1.1")), "{\"ns\":\"fn\", \"fn\":\"number\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"1.1\"]}]}");
    }

    @Test
    public void testFnNumber1Exec() {
        executeTester("testFnNumber1", p.fn.number(p.xs.string("1.1")), "xs:double(\"1.1\")");
    }

    @Test
    public void testFnPrefixFromQName1Exp() {
        exportTester("testFnPrefixFromQName1", p.fn.prefixFromQName(p.xs.qname("abc")), "{\"ns\":\"fn\", \"fn\":\"prefix-from-QName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"QName\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testFnPrefixFromQName1Exist() {
        executeTester("testFnPrefixFromQName1", p.fn.prefixFromQName(p.xs.qname("abc")));
    }

    @Test
    public void testFnQName2Exp() {
        exportTester("testFnQName2", p.fn.QName(p.xs.string("http://a/b"), p.xs.string("c")), "{\"ns\":\"fn\", \"fn\":\"QName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://a/b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]}");
    }

    @Test
    public void testFnQName2Exec() {
        executeTester("testFnQName2", p.fn.QName(p.xs.string("http://a/b"), p.xs.string("c")), "fn:QName(\"http://a/b\",\"c\")");
    }

    @Test
    public void testFnRemove2Exp() {
        exportTester("testFnRemove2", p.fn.remove(p.xs.strings("a", "b", "x", "c"), p.xs.integer(3)), "{\"ns\":\"fn\", \"fn\":\"remove\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"x\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}], {\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[3]}]}");
    }

    @Test
    public void testFnRemove2Exec() {
        executeTester("testFnRemove2", p.fn.remove(p.xs.strings("a", "b", "x", "c"), p.xs.integer(3)), "(\"a\", \"b\", \"c\")");
    }

    @Test
    public void testFnReplace3Exp() {
        exportTester("testFnReplace3", p.fn.replace(p.xs.string("axc"), p.xs.string("^(.)X"), p.xs.string("$1b")), "{\"ns\":\"fn\", \"fn\":\"replace\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"axc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"^(.)X\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"$1b\"]}]}");
    }

    @Test
    public void testFnReplace3Exec() {
        executeTester("testFnReplace3", p.fn.replace(p.xs.string("axc"), p.xs.string("^(.)X"), p.xs.string("$1b")), "\"axc\"");
    }

    @Test
    public void testFnReplace4Exp() {
        exportTester("testFnReplace4", p.fn.replace(p.xs.string("axc"), p.xs.string("^(.)X"), p.xs.string("$1b"), p.xs.string("i")), "{\"ns\":\"fn\", \"fn\":\"replace\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"axc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"^(.)X\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"$1b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"i\"]}]}");
    }

    @Test
    public void testFnReplace4Exec() {
        executeTester("testFnReplace4", p.fn.replace(p.xs.string("axc"), p.xs.string("^(.)X"), p.xs.string("$1b"), p.xs.string("i")), "\"abc\"");
    }

    @Test
    public void testFnResolveUri2Exp() {
        exportTester("testFnResolveUri2", p.fn.resolveUri(p.xs.string("b?c#d"), p.xs.string("http://a/x")), "{\"ns\":\"fn\", \"fn\":\"resolve-uri\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b?c#d\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://a/x\"]}]}");
    }

    @Test
    public void testFnResolveUri2Exec() {
        executeTester("testFnResolveUri2", p.fn.resolveUri(p.xs.string("b?c#d"), p.xs.string("http://a/x")), "xs:anyURI(\"http://a/b?c#d\")");
    }

    @Test
    public void testFnReverse1Exp() {
        exportTester("testFnReverse1", p.fn.reverse(p.xs.strings("c", "b", "a")), "{\"ns\":\"fn\", \"fn\":\"reverse\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}]]}");
    }

    @Test
    public void testFnReverse1Exec() {
        executeTester("testFnReverse1", p.fn.reverse(p.xs.strings("c", "b", "a")), "(\"a\", \"b\", \"c\")");
    }

    @Test
    public void testFnRound1Exp() {
        exportTester("testFnRound1", p.fn.round(p.xs.doubleVal(1.7)), "{\"ns\":\"fn\", \"fn\":\"round\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.7]}]}");
    }

    @Test
    public void testFnRound1Exec() {
        executeTester("testFnRound1", p.fn.round(p.xs.doubleVal(1.7)), "xs:double(\"2\")");
    }

    @Test
    public void testFnRoundHalfToEven1Exp() {
        exportTester("testFnRoundHalfToEven1", p.fn.roundHalfToEven(p.xs.doubleVal(1234.5)), "{\"ns\":\"fn\", \"fn\":\"round-half-to-even\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1234.5]}]}");
    }

    @Test
    public void testFnRoundHalfToEven1Exec() {
        executeTester("testFnRoundHalfToEven1", p.fn.roundHalfToEven(p.xs.doubleVal(1234.5)), "xs:double(\"1234\")");
    }

    @Test
    public void testFnRoundHalfToEven2Exp() {
        exportTester("testFnRoundHalfToEven2", p.fn.roundHalfToEven(p.xs.doubleVal(1234.5), p.xs.integer(-2)), "{\"ns\":\"fn\", \"fn\":\"round-half-to-even\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1234.5]}, {\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[-2]}]}");
    }

    @Test
    public void testFnRoundHalfToEven2Exec() {
        executeTester("testFnRoundHalfToEven2", p.fn.roundHalfToEven(p.xs.doubleVal(1234.5), p.xs.integer(-2)), "xs:double(\"1200\")");
    }

    @Test
    public void testFnSecondsFromDateTime1Exp() {
        exportTester("testFnSecondsFromDateTime1", p.fn.secondsFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")), "{\"ns\":\"fn\", \"fn\":\"seconds-from-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}");
    }

    @Test
    public void testFnSecondsFromDateTime1Exec() {
        executeTester("testFnSecondsFromDateTime1", p.fn.secondsFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")), "8.0");
    }

    @Test
    public void testFnSecondsFromDuration1Exp() {
        exportTester("testFnSecondsFromDuration1", p.fn.secondsFromDuration(p.xs.dayTimeDuration("P3DT4H5M6S")), "{\"ns\":\"fn\", \"fn\":\"seconds-from-duration\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dayTimeDuration\", \"args\":[\"P3DT4H5M6S\"]}]}");
    }

    @Test
    public void testFnSecondsFromDuration1Exec() {
        executeTester("testFnSecondsFromDuration1", p.fn.secondsFromDuration(p.xs.dayTimeDuration("P3DT4H5M6S")), "6.0");
    }

    @Test
    public void testFnSecondsFromTime1Exp() {
        exportTester("testFnSecondsFromTime1", p.fn.secondsFromTime(p.xs.time("10:09:08Z")), "{\"ns\":\"fn\", \"fn\":\"seconds-from-time\", \"args\":[{\"ns\":\"xs\", \"fn\":\"time\", \"args\":[\"10:09:08Z\"]}]}");
    }

    @Test
    public void testFnSecondsFromTime1Exec() {
        executeTester("testFnSecondsFromTime1", p.fn.secondsFromTime(p.xs.time("10:09:08Z")), "8.0");
    }

    @Test
    public void testFnStartsWith2Exp() {
        exportTester("testFnStartsWith2", p.fn.startsWith(p.xs.string("abc"), p.xs.string("a")), "{\"ns\":\"fn\", \"fn\":\"starts-with\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}]}");
    }

    @Test
    public void testFnStartsWith2Exec() {
        executeTester("testFnStartsWith2", p.fn.startsWith(p.xs.string("abc"), p.xs.string("a")), "fn:true()");
    }

    @Test
    public void testFnStartsWith3Exp() {
        exportTester("testFnStartsWith3", p.fn.startsWith(p.xs.string("abc"), p.xs.string("a"), p.xs.string("http://marklogic.com/collation/")), "{\"ns\":\"fn\", \"fn\":\"starts-with\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}");
    }

    @Test
    public void testFnStartsWith3Exec() {
        executeTester("testFnStartsWith3", p.fn.startsWith(p.xs.string("abc"), p.xs.string("a"), p.xs.string("http://marklogic.com/collation/")), "fn:true()");
    }

    @Test
    public void testFnString1Exp() {
        exportTester("testFnString1", p.fn.string(p.xs.doubleVal(1)), "{\"ns\":\"fn\", \"fn\":\"string\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}]}");
    }

    @Test
    public void testFnString1Exec() {
        executeTester("testFnString1", p.fn.string(p.xs.doubleVal(1)), "\"1\"");
    }

    @Test
    public void testFnStringJoin1Exp() {
        exportTester("testFnStringJoin1", p.fn.stringJoin(p.xs.strings("a", "b", "c")), "{\"ns\":\"fn\", \"fn\":\"string-join\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]]}");
    }

    @Test
    public void testFnStringJoin1Exec() {
        executeTester("testFnStringJoin1", p.fn.stringJoin(p.xs.strings("a", "b", "c")), "\"abc\"");
    }

    @Test
    public void testFnStringJoin2Exp() {
        exportTester("testFnStringJoin2", p.fn.stringJoin(p.xs.strings("a", "b", "c"), p.xs.string("+")), "{\"ns\":\"fn\", \"fn\":\"string-join\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"+\"]}]}");
    }

    @Test
    public void testFnStringJoin2Exec() {
        executeTester("testFnStringJoin2", p.fn.stringJoin(p.xs.strings("a", "b", "c"), p.xs.string("+")), "\"a+b+c\"");
    }

    @Test
    public void testFnStringLength1Exp() {
        exportTester("testFnStringLength1", p.fn.stringLength(p.xs.string("abc")), "{\"ns\":\"fn\", \"fn\":\"string-length\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testFnStringLength1Exec() {
        executeTester("testFnStringLength1", p.fn.stringLength(p.xs.string("abc")), "3");
    }

    @Test
    public void testFnStringToCodepoints1Exp() {
        exportTester("testFnStringToCodepoints1", p.fn.stringToCodepoints(p.xs.string("abc")), "{\"ns\":\"fn\", \"fn\":\"string-to-codepoints\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testFnStringToCodepoints1Exec() {
        executeTester("testFnStringToCodepoints1", p.fn.stringToCodepoints(p.xs.string("abc")), "(97, 98, 99)");
    }

    @Test
    public void testFnSubsequence2Exp() {
        exportTester("testFnSubsequence2", p.fn.subsequence(p.xs.strings("a", "b", "c", "d", "e"), p.xs.doubleVal(2)), "{\"ns\":\"fn\", \"fn\":\"subsequence\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"d\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"e\"]}], {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}]}");
    }

    @Test
    public void testFnSubsequence2Exec() {
        executeTester("testFnSubsequence2", p.fn.subsequence(p.xs.strings("a", "b", "c", "d", "e"), p.xs.doubleVal(2)), "(\"b\", \"c\", \"d\", \"e\")");
    }

    @Test
    public void testFnSubsequence3Exp() {
        exportTester("testFnSubsequence3", p.fn.subsequence(p.xs.strings("a", "b", "c", "d", "e"), p.xs.doubleVal(2), p.xs.doubleVal(3)), "{\"ns\":\"fn\", \"fn\":\"subsequence\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"d\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"e\"]}], {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3]}]}");
    }

    @Test
    public void testFnSubsequence3Exec() {
        executeTester("testFnSubsequence3", p.fn.subsequence(p.xs.strings("a", "b", "c", "d", "e"), p.xs.doubleVal(2), p.xs.doubleVal(3)), "(\"b\", \"c\", \"d\")");
    }

    @Test
    public void testFnSubstring2Exp() {
        exportTester("testFnSubstring2", p.fn.substring(p.xs.string("abcd"), p.xs.doubleVal(2)), "{\"ns\":\"fn\", \"fn\":\"substring\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcd\"]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}]}");
    }

    @Test
    public void testFnSubstring2Exec() {
        executeTester("testFnSubstring2", p.fn.substring(p.xs.string("abcd"), p.xs.doubleVal(2)), "\"bcd\"");
    }

    @Test
    public void testFnSubstring3Exp() {
        exportTester("testFnSubstring3", p.fn.substring(p.xs.string("abcd"), p.xs.doubleVal(2), p.xs.doubleVal(2)), "{\"ns\":\"fn\", \"fn\":\"substring\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcd\"]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}]}");
    }

    @Test
    public void testFnSubstring3Exec() {
        executeTester("testFnSubstring3", p.fn.substring(p.xs.string("abcd"), p.xs.doubleVal(2), p.xs.doubleVal(2)), "\"bc\"");
    }

    @Test
    public void testFnSubstringAfter2Exp() {
        exportTester("testFnSubstringAfter2", p.fn.substringAfter(p.xs.string("abcd"), p.xs.string("ab")), "{\"ns\":\"fn\", \"fn\":\"substring-after\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcd\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"ab\"]}]}");
    }

    @Test
    public void testFnSubstringAfter2Exec() {
        executeTester("testFnSubstringAfter2", p.fn.substringAfter(p.xs.string("abcd"), p.xs.string("ab")), "\"cd\"");
    }

    @Test
    public void testFnSubstringAfter3Exp() {
        exportTester("testFnSubstringAfter3", p.fn.substringAfter(p.xs.string("abcd"), p.xs.string("ab"), p.xs.string("http://marklogic.com/collation/")), "{\"ns\":\"fn\", \"fn\":\"substring-after\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcd\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"ab\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}");
    }

    @Test
    public void testFnSubstringAfter3Exec() {
        executeTester("testFnSubstringAfter3", p.fn.substringAfter(p.xs.string("abcd"), p.xs.string("ab"), p.xs.string("http://marklogic.com/collation/")), "\"cd\"");
    }

    @Test
    public void testFnSubstringBefore2Exp() {
        exportTester("testFnSubstringBefore2", p.fn.substringBefore(p.xs.string("abcd"), p.xs.string("cd")), "{\"ns\":\"fn\", \"fn\":\"substring-before\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcd\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"cd\"]}]}");
    }

    @Test
    public void testFnSubstringBefore2Exec() {
        executeTester("testFnSubstringBefore2", p.fn.substringBefore(p.xs.string("abcd"), p.xs.string("cd")), "\"ab\"");
    }

    @Test
    public void testFnSubstringBefore3Exp() {
        exportTester("testFnSubstringBefore3", p.fn.substringBefore(p.xs.string("abcd"), p.xs.string("cd"), p.xs.string("http://marklogic.com/collation/")), "{\"ns\":\"fn\", \"fn\":\"substring-before\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcd\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"cd\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}");
    }

    @Test
    public void testFnSubstringBefore3Exec() {
        executeTester("testFnSubstringBefore3", p.fn.substringBefore(p.xs.string("abcd"), p.xs.string("cd"), p.xs.string("http://marklogic.com/collation/")), "\"ab\"");
    }

    @Test
    public void testFnSum1Exp() {
        exportTester("testFnSum1", p.fn.sum(p.xs.doubleVals(1, 2, 3)), "{\"ns\":\"fn\", \"fn\":\"sum\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3]}]]}");
    }

    @Test
    public void testFnSum1Exec() {
        executeTester("testFnSum1", p.fn.sum(p.xs.doubleVals(1, 2, 3)), "xs:double(\"6\")");
    }

    @Test
    public void testFnTail1Exp() {
        exportTester("testFnTail1", p.fn.tail(p.xs.strings("a", "b", "c")), "{\"ns\":\"fn\", \"fn\":\"tail\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]]}");
    }

    @Test
    public void testFnTail1Exec() {
        executeTester("testFnTail1", p.fn.tail(p.xs.strings("a", "b", "c")), "(\"b\", \"c\")");
    }

    @Test
    public void testFnTimezoneFromDate1Exp() {
        exportTester("testFnTimezoneFromDate1", p.fn.timezoneFromDate(p.xs.date("2016-01-02-03:04")), "{\"ns\":\"fn\", \"fn\":\"timezone-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02-03:04\"]}]}");
    }

    @Test
    public void testFnTimezoneFromDate1Exist() {
        executeTester("testFnTimezoneFromDate1", p.fn.timezoneFromDate(p.xs.date("2016-01-02-03:04")));
    }

    @Test
    public void testFnTimezoneFromDateTime1Exp() {
        exportTester("testFnTimezoneFromDateTime1", p.fn.timezoneFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")), "{\"ns\":\"fn\", \"fn\":\"timezone-from-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}");
    }

    @Test
    public void testFnTimezoneFromDateTime1Exec() {
        executeTester("testFnTimezoneFromDateTime1", p.fn.timezoneFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")), "xs:dayTimeDuration(\"PT0S\")");
    }

    @Test
    public void testFnTimezoneFromTime1Exp() {
        exportTester("testFnTimezoneFromTime1", p.fn.timezoneFromTime(p.xs.time("10:09:08Z")), "{\"ns\":\"fn\", \"fn\":\"timezone-from-time\", \"args\":[{\"ns\":\"xs\", \"fn\":\"time\", \"args\":[\"10:09:08Z\"]}]}");
    }

    @Test
    public void testFnTimezoneFromTime1Exec() {
        executeTester("testFnTimezoneFromTime1", p.fn.timezoneFromTime(p.xs.time("10:09:08Z")), "xs:dayTimeDuration(\"PT0S\")");
    }

    @Test
    public void testFnTokenize2Exp() {
        exportTester("testFnTokenize2", p.fn.tokenize(p.xs.string("axbxc"), p.xs.string("X")), "{\"ns\":\"fn\", \"fn\":\"tokenize\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"axbxc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"X\"]}]}");
    }

    @Test
    public void testFnTokenize2Exec() {
        executeTester("testFnTokenize2", p.fn.tokenize(p.xs.string("axbxc"), p.xs.string("X")), "\"axbxc\"");
    }

    @Test
    public void testFnTokenize3Exp() {
        exportTester("testFnTokenize3", p.fn.tokenize(p.xs.string("axbxc"), p.xs.string("X"), p.xs.string("i")), "{\"ns\":\"fn\", \"fn\":\"tokenize\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"axbxc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"X\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"i\"]}]}");
    }

    @Test
    public void testFnTokenize3Exec() {
        executeTester("testFnTokenize3", p.fn.tokenize(p.xs.string("axbxc"), p.xs.string("X"), p.xs.string("i")), "(\"a\", \"b\", \"c\")");
    }

    @Test
    public void testFnTranslate3Exp() {
        exportTester("testFnTranslate3", p.fn.translate(p.xs.string("axcy"), p.xs.string("xy"), p.xs.string("bd")), "{\"ns\":\"fn\", \"fn\":\"translate\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"axcy\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"xy\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"bd\"]}]}");
    }

    @Test
    public void testFnTranslate3Exec() {
        executeTester("testFnTranslate3", p.fn.translate(p.xs.string("axcy"), p.xs.string("xy"), p.xs.string("bd")), "\"abcd\"");
    }

    @Test
    public void testFnTrue0Exp() {
        exportTester("testFnTrue0", p.fn.trueExpr(), "{\"ns\":\"fn\", \"fn\":\"true\", \"args\":[]}");
    }

    @Test
    public void testFnTrue0Exist() {
        executeTester("testFnTrue0", p.fn.trueExpr());
    }

    @Test
    public void testFnUnordered1Exp() {
        exportTester("testFnUnordered1", p.fn.unordered(p.xs.string("abc")), "{\"ns\":\"fn\", \"fn\":\"unordered\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}");
    }

    @Test
    public void testFnUnordered1Exist() {
        executeTester("testFnUnordered1", p.fn.unordered(p.xs.string("abc")));
    }

    @Test
    public void testFnUpperCase1Exp() {
        exportTester("testFnUpperCase1", p.fn.upperCase(p.xs.string("abc")), "{\"ns\":\"fn\", \"fn\":\"upper-case\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testFnUpperCase1Exec() {
        executeTester("testFnUpperCase1", p.fn.upperCase(p.xs.string("abc")), "\"ABC\"");
    }

    @Test
    public void testFnYearFromDate1Exp() {
        exportTester("testFnYearFromDate1", p.fn.yearFromDate(p.xs.date("2016-01-02-03:04")), "{\"ns\":\"fn\", \"fn\":\"year-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02-03:04\"]}]}");
    }

    @Test
    public void testFnYearFromDate1Exec() {
        executeTester("testFnYearFromDate1", p.fn.yearFromDate(p.xs.date("2016-01-02-03:04")), "2016");
    }

    @Test
    public void testFnYearFromDateTime1Exp() {
        exportTester("testFnYearFromDateTime1", p.fn.yearFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")), "{\"ns\":\"fn\", \"fn\":\"year-from-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}");
    }

    @Test
    public void testFnYearFromDateTime1Exec() {
        executeTester("testFnYearFromDateTime1", p.fn.yearFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")), "2016");
    }

    @Test
    public void testFnYearsFromDuration1Exp() {
        exportTester("testFnYearsFromDuration1", p.fn.yearsFromDuration(p.xs.yearMonthDuration("P1Y2M")), "{\"ns\":\"fn\", \"fn\":\"years-from-duration\", \"args\":[{\"ns\":\"xs\", \"fn\":\"yearMonthDuration\", \"args\":[\"P1Y2M\"]}]}");
    }

    @Test
    public void testFnYearsFromDuration1Exec() {
        executeTester("testFnYearsFromDuration1", p.fn.yearsFromDuration(p.xs.yearMonthDuration("P1Y2M")), "1");
    }

    @Test
    public void testJsonToArray0Exp() {
        exportTester("testJsonToArray0", p.json.toArray(), "{\"ns\":\"json\", \"fn\":\"to-array\", \"args\":[]}");
    }

    @Test
    public void testJsonToArray0Exist() {
        executeTester("testJsonToArray0", p.json.toArray());
    }

    @Test
    public void testJsonToArray1Exp() {
        exportTester("testJsonToArray1", p.json.toArray(p.xs.string("abc")), "{\"ns\":\"json\", \"fn\":\"to-array\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}");
    }

    @Test
    public void testJsonToArray1Exist() {
        executeTester("testJsonToArray1", p.json.toArray(p.xs.string("abc")));
    }

    @Test
    public void testJsonToArray2Exp() {
        exportTester("testJsonToArray2", p.json.toArray(p.xs.string("abc"), p.xs.doubleVal(1.2)), "{\"ns\":\"json\", \"fn\":\"to-array\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]}");
    }

    @Test
    public void testJsonToArray2Exist() {
        executeTester("testJsonToArray2", p.json.toArray(p.xs.string("abc"), p.xs.doubleVal(1.2)));
    }

    @Test
    public void testJsonToArray3Exp() {
        exportTester("testJsonToArray3", p.json.toArray(p.xs.string("abc"), p.xs.doubleVal(1.2), p.xs.string("abc")), "{\"ns\":\"json\", \"fn\":\"to-array\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testJsonToArray3Exist() {
        executeTester("testJsonToArray3", p.json.toArray(p.xs.string("abc"), p.xs.doubleVal(1.2), p.xs.string("abc")));
    }

    @Test
    public void testMathAcos1Exp() {
        exportTester("testMathAcos1", p.math.acos(p.xs.doubleVal(0.5)), "{\"ns\":\"math\", \"fn\":\"acos\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[0.5]}]}");
    }

    @Test
    public void testMathAcos1Exec() {
        executeTester("testMathAcos1", p.math.acos(p.xs.doubleVal(0.5)), "xs:double(\"1.0471975511966\")");
    }

    @Test
    public void testMathAsin1Exp() {
        exportTester("testMathAsin1", p.math.asin(p.xs.doubleVal(0.5)), "{\"ns\":\"math\", \"fn\":\"asin\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[0.5]}]}");
    }

    @Test
    public void testMathAsin1Exec() {
        executeTester("testMathAsin1", p.math.asin(p.xs.doubleVal(0.5)), "xs:double(\"0.523598775598299\")");
    }

    @Test
    public void testMathAtan1Exp() {
        exportTester("testMathAtan1", p.math.atan(p.xs.doubleVal(3.14159)), "{\"ns\":\"math\", \"fn\":\"atan\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3.14159]}]}");
    }

    @Test
    public void testMathAtan1Exec() {
        executeTester("testMathAtan1", p.math.atan(p.xs.doubleVal(3.14159)), "xs:double(\"1.26262701154934\")");
    }

    @Test
    public void testMathAtan22Exp() {
        exportTester("testMathAtan22", p.math.atan2(p.xs.doubleVal(36.23), p.xs.doubleVal(5.234)), "{\"ns\":\"math\", \"fn\":\"atan2\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[36.23]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[5.234]}]}");
    }

    @Test
    public void testMathAtan22Exec() {
        executeTester("testMathAtan22", p.math.atan2(p.xs.doubleVal(36.23), p.xs.doubleVal(5.234)), "xs:double(\"1.42732303452594\")");
    }

    @Test
    public void testMathCeil1Exp() {
        exportTester("testMathCeil1", p.math.ceil(p.xs.doubleVal(1.3)), "{\"ns\":\"math\", \"fn\":\"ceil\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.3]}]}");
    }

    @Test
    public void testMathCeil1Exec() {
        executeTester("testMathCeil1", p.math.ceil(p.xs.doubleVal(1.3)), "xs:double(\"2\")");
    }

    @Test
    public void testMathCos1Exp() {
        exportTester("testMathCos1", p.math.cos(p.xs.doubleVal(11)), "{\"ns\":\"math\", \"fn\":\"cos\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[11]}]}");
    }

    @Test
    public void testMathCos1Exec() {
        executeTester("testMathCos1", p.math.cos(p.xs.doubleVal(11)), "xs:double(\"0.00442569798805079\")");
    }

    @Test
    public void testMathCosh1Exp() {
        exportTester("testMathCosh1", p.math.cosh(p.xs.doubleVal(11)), "{\"ns\":\"math\", \"fn\":\"cosh\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[11]}]}");
    }

    @Test
    public void testMathCosh1Exec() {
        executeTester("testMathCosh1", p.math.cosh(p.xs.doubleVal(11)), "xs:double(\"29937.0708659498\")");
    }

    @Test
    public void testMathCot1Exp() {
        exportTester("testMathCot1", p.math.cot(p.xs.doubleVal(19.5)), "{\"ns\":\"math\", \"fn\":\"cot\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[19.5]}]}");
    }

    @Test
    public void testMathCot1Exec() {
        executeTester("testMathCot1", p.math.cot(p.xs.doubleVal(19.5)), "xs:double(\"1.31422390103306\")");
    }

    @Test
    public void testMathDegrees1Exp() {
        exportTester("testMathDegrees1", p.math.degrees(p.xs.doubleVal(1.5707963267949)), "{\"ns\":\"math\", \"fn\":\"degrees\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.5707963267949]}]}");
    }

    @Test
    public void testMathDegrees1Exec() {
        executeTester("testMathDegrees1", p.math.degrees(p.xs.doubleVal(1.5707963267949)), "xs:double(\"90.0000000000002\")");
    }

    @Test
    public void testMathExp1Exp() {
        exportTester("testMathExp1", p.math.exp(p.xs.doubleVal(0.1)), "{\"ns\":\"math\", \"fn\":\"exp\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[0.1]}]}");
    }

    @Test
    public void testMathExp1Exec() {
        executeTester("testMathExp1", p.math.exp(p.xs.doubleVal(0.1)), "xs:double(\"1.10517091807565\")");
    }

    @Test
    public void testMathFabs1Exp() {
        exportTester("testMathFabs1", p.math.fabs(p.xs.doubleVal(4.013)), "{\"ns\":\"math\", \"fn\":\"fabs\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[4.013]}]}");
    }

    @Test
    public void testMathFabs1Exec() {
        executeTester("testMathFabs1", p.math.fabs(p.xs.doubleVal(4.013)), "xs:double(\"4.013\")");
    }

    @Test
    public void testMathFloor1Exp() {
        exportTester("testMathFloor1", p.math.floor(p.xs.doubleVal(1.7)), "{\"ns\":\"math\", \"fn\":\"floor\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.7]}]}");
    }

    @Test
    public void testMathFloor1Exec() {
        executeTester("testMathFloor1", p.math.floor(p.xs.doubleVal(1.7)), "xs:double(\"1\")");
    }

    @Test
    public void testMathFmod2Exp() {
        exportTester("testMathFmod2", p.math.fmod(p.xs.doubleVal(10), p.xs.doubleVal(3)), "{\"ns\":\"math\", \"fn\":\"fmod\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[10]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3]}]}");
    }

    @Test
    public void testMathFmod2Exec() {
        executeTester("testMathFmod2", p.math.fmod(p.xs.doubleVal(10), p.xs.doubleVal(3)), "xs:double(\"1\")");
    }

    @Test
    public void testMathFrexp1Exp() {
        exportTester("testMathFrexp1", p.math.frexp(p.xs.doubleVal(10)), "{\"ns\":\"math\", \"fn\":\"frexp\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[10]}]}");
    }

    @Test
    public void testMathFrexp1Exec() {
        executeTester("testMathFrexp1", p.math.frexp(p.xs.doubleVal(10)), "(xs:double(\"0.625\"), 4)");
    }

    @Test
    public void testMathLdexp2Exp() {
        exportTester("testMathLdexp2", p.math.ldexp(p.xs.doubleVal(1.333), p.xs.integer(10)), "{\"ns\":\"math\", \"fn\":\"ldexp\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.333]}, {\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[10]}]}");
    }

    @Test
    public void testMathLdexp2Exec() {
        executeTester("testMathLdexp2", p.math.ldexp(p.xs.doubleVal(1.333), p.xs.integer(10)), "xs:double(\"1364.992\")");
    }

    @Test
    public void testMathLog1Exp() {
        exportTester("testMathLog1", p.math.log(p.xs.doubleVal(1000)), "{\"ns\":\"math\", \"fn\":\"log\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1000]}]}");
    }

    @Test
    public void testMathLog1Exec() {
        executeTester("testMathLog1", p.math.log(p.xs.doubleVal(1000)), "xs:double(\"6.90775527898214\")");
    }

    @Test
    public void testMathLog101Exp() {
        exportTester("testMathLog101", p.math.log10(p.xs.doubleVal(1000)), "{\"ns\":\"math\", \"fn\":\"log10\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1000]}]}");
    }

    @Test
    public void testMathLog101Exec() {
        executeTester("testMathLog101", p.math.log10(p.xs.doubleVal(1000)), "xs:double(\"3\")");
    }

    @Test
    public void testMathMedian1Exp() {
        exportTester("testMathMedian1", p.math.median(p.xs.doubleVal(1.2)), "{\"ns\":\"math\", \"fn\":\"median\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]]}");
    }

    @Test
    public void testMathMedian1Exist() {
        executeTester("testMathMedian1", p.math.median(p.xs.doubleVal(1.2)));
    }

    @Test
    public void testMathMode1Exp() {
        exportTester("testMathMode1", p.math.mode(p.xs.string("abc")), "{\"ns\":\"math\", \"fn\":\"mode\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}");
    }

    @Test
    public void testMathMode1Exist() {
        executeTester("testMathMode1", p.math.mode(p.xs.string("abc")));
    }

    @Test
    public void testMathMode2Exp() {
        exportTester("testMathMode2", p.math.mode(p.xs.string("abc"), p.xs.string("abc")), "{\"ns\":\"math\", \"fn\":\"mode\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}");
    }

    @Test
    public void testMathMode2Exist() {
        executeTester("testMathMode2", p.math.mode(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testMathModf1Exp() {
        exportTester("testMathModf1", p.math.modf(p.xs.doubleVal(1.333)), "{\"ns\":\"math\", \"fn\":\"modf\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.333]}]}");
    }

    @Test
    public void testMathModf1Exec() {
        executeTester("testMathModf1", p.math.modf(p.xs.doubleVal(1.333)), "(xs:double(\"0.333\"), xs:double(\"1\"))");
    }

    @Test
    public void testMathPercentile2Exp() {
        exportTester("testMathPercentile2", p.math.percentile(p.xs.doubleVal(1.2), p.xs.doubleVal(1.2)), "{\"ns\":\"math\", \"fn\":\"percentile\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}], [{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]]}");
    }

    @Test
    public void testMathPercentile2Exist() {
        executeTester("testMathPercentile2", p.math.percentile(p.xs.doubleVal(1.2), p.xs.doubleVal(1.2)));
    }

    @Test
    public void testMathPercentRank2Exp() {
        exportTester("testMathPercentRank2", p.math.percentRank(p.xs.string("abc"), p.xs.string("abc")), "{\"ns\":\"math\", \"fn\":\"percent-rank\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testMathPercentRank2Exist() {
        executeTester("testMathPercentRank2", p.math.percentRank(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testMathPercentRank3Exp() {
        exportTester("testMathPercentRank3", p.math.percentRank(p.xs.string("abc"), p.xs.string("abc"), p.xs.string("abc")), "{\"ns\":\"math\", \"fn\":\"percent-rank\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}");
    }

    @Test
    public void testMathPercentRank3Exist() {
        executeTester("testMathPercentRank3", p.math.percentRank(p.xs.string("abc"), p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testMathPi0Exp() {
        exportTester("testMathPi0", p.math.pi(), "{\"ns\":\"math\", \"fn\":\"pi\", \"args\":[]}");
    }

    @Test
    public void testMathPi0Exist() {
        executeTester("testMathPi0", p.math.pi());
    }

    @Test
    public void testMathPow2Exp() {
        exportTester("testMathPow2", p.math.pow(p.xs.doubleVal(2), p.xs.doubleVal(10)), "{\"ns\":\"math\", \"fn\":\"pow\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[10]}]}");
    }

    @Test
    public void testMathPow2Exec() {
        executeTester("testMathPow2", p.math.pow(p.xs.doubleVal(2), p.xs.doubleVal(10)), "xs:double(\"1024\")");
    }

    @Test
    public void testMathRadians1Exp() {
        exportTester("testMathRadians1", p.math.radians(p.xs.doubleVal(90)), "{\"ns\":\"math\", \"fn\":\"radians\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[90]}]}");
    }

    @Test
    public void testMathRadians1Exec() {
        executeTester("testMathRadians1", p.math.radians(p.xs.doubleVal(90)), "xs:double(\"1.5707963267949\")");
    }

    @Test
    public void testMathRank2Exp() {
        exportTester("testMathRank2", p.math.rank(p.xs.string("abc"), p.xs.string("abc")), "{\"ns\":\"math\", \"fn\":\"rank\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testMathRank2Exist() {
        executeTester("testMathRank2", p.math.rank(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testMathRank3Exp() {
        exportTester("testMathRank3", p.math.rank(p.xs.string("abc"), p.xs.string("abc"), p.xs.string("abc")), "{\"ns\":\"math\", \"fn\":\"rank\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}");
    }

    @Test
    public void testMathRank3Exist() {
        executeTester("testMathRank3", p.math.rank(p.xs.string("abc"), p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testMathSin1Exp() {
        exportTester("testMathSin1", p.math.sin(p.xs.doubleVal(1.95)), "{\"ns\":\"math\", \"fn\":\"sin\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.95]}]}");
    }

    @Test
    public void testMathSin1Exec() {
        executeTester("testMathSin1", p.math.sin(p.xs.doubleVal(1.95)), "xs:double(\"0.928959715003869\")");
    }

    @Test
    public void testMathSinh1Exp() {
        exportTester("testMathSinh1", p.math.sinh(p.xs.doubleVal(1.95)), "{\"ns\":\"math\", \"fn\":\"sinh\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.95]}]}");
    }

    @Test
    public void testMathSinh1Exec() {
        executeTester("testMathSinh1", p.math.sinh(p.xs.doubleVal(1.95)), "xs:double(\"3.44320675450139\")");
    }

    @Test
    public void testMathSqrt1Exp() {
        exportTester("testMathSqrt1", p.math.sqrt(p.xs.doubleVal(4)), "{\"ns\":\"math\", \"fn\":\"sqrt\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[4]}]}");
    }

    @Test
    public void testMathSqrt1Exec() {
        executeTester("testMathSqrt1", p.math.sqrt(p.xs.doubleVal(4)), "xs:double(\"2\")");
    }

    @Test
    public void testMathStddev1Exp() {
        exportTester("testMathStddev1", p.math.stddev(p.xs.doubleVal(1.2)), "{\"ns\":\"math\", \"fn\":\"stddev\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]]}");
    }

    @Test
    public void testMathStddev1Exist() {
        executeTester("testMathStddev1", p.math.stddev(p.xs.doubleVal(1.2)));
    }

    @Test
    public void testMathStddevP1Exp() {
        exportTester("testMathStddevP1", p.math.stddevP(p.xs.doubleVal(1.2)), "{\"ns\":\"math\", \"fn\":\"stddev-p\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]]}");
    }

    @Test
    public void testMathStddevP1Exist() {
        executeTester("testMathStddevP1", p.math.stddevP(p.xs.doubleVal(1.2)));
    }

    @Test
    public void testMathTan1Exp() {
        exportTester("testMathTan1", p.math.tan(p.xs.doubleVal(19.5)), "{\"ns\":\"math\", \"fn\":\"tan\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[19.5]}]}");
    }

    @Test
    public void testMathTan1Exec() {
        executeTester("testMathTan1", p.math.tan(p.xs.doubleVal(19.5)), "xs:double(\"0.760905351982977\")");
    }

    @Test
    public void testMathTanh1Exp() {
        exportTester("testMathTanh1", p.math.tanh(p.xs.doubleVal(0.95)), "{\"ns\":\"math\", \"fn\":\"tanh\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[0.95]}]}");
    }

    @Test
    public void testMathTanh1Exec() {
        executeTester("testMathTanh1", p.math.tanh(p.xs.doubleVal(0.95)), "xs:double(\"0.739783051274004\")");
    }

    @Test
    public void testMathTrunc1Exp() {
        exportTester("testMathTrunc1", p.math.trunc(p.xs.doubleVal(123.456)), "{\"ns\":\"math\", \"fn\":\"trunc\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[123.456]}]}");
    }

    @Test
    public void testMathTrunc1Exec() {
        executeTester("testMathTrunc1", p.math.trunc(p.xs.doubleVal(123.456)), "xs:double(\"123\")");
    }

    @Test
    public void testMathTrunc2Exp() {
        exportTester("testMathTrunc2", p.math.trunc(p.xs.doubleVal(123.456), p.xs.integer(2)), "{\"ns\":\"math\", \"fn\":\"trunc\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[123.456]}, {\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[2]}]}");
    }

    @Test
    public void testMathTrunc2Exec() {
        executeTester("testMathTrunc2", p.math.trunc(p.xs.doubleVal(123.456), p.xs.integer(2)), "xs:double(\"123.45\")");
    }

    @Test
    public void testMathVariance1Exp() {
        exportTester("testMathVariance1", p.math.variance(p.xs.doubleVal(1.2)), "{\"ns\":\"math\", \"fn\":\"variance\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]]}");
    }

    @Test
    public void testMathVariance1Exist() {
        executeTester("testMathVariance1", p.math.variance(p.xs.doubleVal(1.2)));
    }

    @Test
    public void testMathVarianceP1Exp() {
        exportTester("testMathVarianceP1", p.math.varianceP(p.xs.doubleVal(1.2)), "{\"ns\":\"math\", \"fn\":\"variance-p\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]]}");
    }

    @Test
    public void testMathVarianceP1Exist() {
        executeTester("testMathVarianceP1", p.math.varianceP(p.xs.doubleVal(1.2)));
    }

    @Test
    public void testRdfLangString2Exp() {
        exportTester("testRdfLangString2", p.rdf.langString("abc", "en"), "{\"ns\":\"rdf\", \"fn\":\"langString\", \"args\":[\"abc\", \"en\"]}");
    }

    @Test
    public void testRdfLangString2Exec() {
        executeTester("testRdfLangString2", p.rdf.langString("abc", "en"), "rdf:langString(\"abc\", \"en\")");
    }

    @Test
    public void testRdfLangStringLanguage1Exp() {
        exportTester("testRdfLangStringLanguage1", p.rdf.langStringLanguage(p.rdf.langString("abc", "en")), "{\"ns\":\"rdf\", \"fn\":\"langString-language\", \"args\":[{\"ns\":\"rdf\", \"fn\":\"langString\", \"args\":[\"abc\", \"en\"]}]}");
    }

    @Test
    public void testRdfLangStringLanguage1Exist() {
        executeTester("testRdfLangStringLanguage1", p.rdf.langStringLanguage(p.rdf.langString("abc", "en")));
    }

    @Test
    public void testSemBnode0Exp() {
        exportTester("testSemBnode0", p.sem.bnode(), "{\"ns\":\"sem\", \"fn\":\"bnode\", \"args\":[]}");
    }

    @Test
    public void testSemBnode0Exist() {
        executeTester("testSemBnode0", p.sem.bnode());
    }

    @Test
    public void testSemBnode1Exp() {
        exportTester("testSemBnode1", p.sem.bnode(p.xs.string("abc")), "{\"ns\":\"sem\", \"fn\":\"bnode\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testSemBnode1Exist() {
        executeTester("testSemBnode1", p.sem.bnode(p.xs.string("abc")));
    }

    @Test
    public void testSemCoalesce2Exp() {
        exportTester("testSemCoalesce2", p.sem.coalesce(p.xs.string("a"), p.xs.string("b")), "{\"ns\":\"sem\", \"fn\":\"coalesce\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}], [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}]]}");
    }

    @Test
    public void testSemCoalesce2Exec() {
        executeTester("testSemCoalesce2", p.sem.coalesce(p.xs.string("a"), p.xs.string("b")), "\"a\"");
    }

    @Test
    public void testSemCoalesce3Exp() {
        exportTester("testSemCoalesce3", p.sem.coalesce(p.xs.string("a"), p.xs.string("b"), p.xs.string("c")), "{\"ns\":\"sem\", \"fn\":\"coalesce\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}], [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}], [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]]}");
    }

    @Test
    public void testSemCoalesce3Exec() {
        executeTester("testSemCoalesce3", p.sem.coalesce(p.xs.string("a"), p.xs.string("b"), p.xs.string("c")), "\"a\"");
    }

    @Test
    public void testSemDatatype1Exp() {
        exportTester("testSemDatatype1", p.sem.datatype(p.xs.string("a")), "{\"ns\":\"sem\", \"fn\":\"datatype\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}]}");
    }

    @Test
    public void testSemDatatype1Exec() {
        executeTester("testSemDatatype1", p.sem.datatype(p.xs.string("a")), "sem:iri(\"http://www.w3.org/2001/XMLSchema#string\")");
    }

    @Test
    public void testSemIf3Exp() {
        exportTester("testSemIf3", p.sem.ifExpr(p.xs.booleanVal(true), p.xs.string("a"), p.xs.string("b")), "{\"ns\":\"sem\", \"fn\":\"if\", \"args\":[{\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[true]}, [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}], [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}]]}");
    }

    @Test
    public void testSemIf3Exec() {
        executeTester("testSemIf3", p.sem.ifExpr(p.xs.booleanVal(true), p.xs.string("a"), p.xs.string("b")), "\"a\"");
    }

    @Test
    public void testSemInvalid2Exp() {
        exportTester("testSemInvalid2", p.sem.invalid(p.xs.string("abc"), p.sem.iri("http://a/b")), "{\"ns\":\"sem\", \"fn\":\"invalid\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"sem\", \"fn\":\"iri\", \"args\":[\"http://a/b\"]}]}");
    }

    @Test
    public void testSemInvalid2Exist() {
        executeTester("testSemInvalid2", p.sem.invalid(p.xs.string("abc"), p.sem.iri("http://a/b")));
    }

    @Test
    public void testSemIri1Exp() {
        exportTester("testSemIri1", p.sem.iri("http://a/b"), "{\"ns\":\"sem\", \"fn\":\"iri\", \"args\":[\"http://a/b\"]}");
    }

    @Test
    public void testSemIri1Exec() {
        executeTester("testSemIri1", p.sem.iri("http://a/b"), "sem:iri(\"http://a/b\")");
    }

    @Test
    public void testSemIriToQName1Exp() {
        exportTester("testSemIriToQName1", p.sem.iriToQName(p.xs.string("abc")), "{\"ns\":\"sem\", \"fn\":\"iri-to-QName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testSemIriToQName1Exist() {
        executeTester("testSemIriToQName1", p.sem.iriToQName(p.xs.string("abc")));
    }

    @Test
    public void testSemIsBlank1Exp() {
        exportTester("testSemIsBlank1", p.sem.isBlank(p.xs.doubleVal(1)), "{\"ns\":\"sem\", \"fn\":\"isBlank\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}]}");
    }

    @Test
    public void testSemIsBlank1Exec() {
        executeTester("testSemIsBlank1", p.sem.isBlank(p.xs.doubleVal(1)), "fn:false()");
    }

    @Test
    public void testSemIsIRI1Exp() {
        exportTester("testSemIsIRI1", p.sem.isIRI(p.xs.doubleVal(1)), "{\"ns\":\"sem\", \"fn\":\"isIRI\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}]}");
    }

    @Test
    public void testSemIsIRI1Exec() {
        executeTester("testSemIsIRI1", p.sem.isIRI(p.xs.doubleVal(1)), "fn:false()");
    }

    @Test
    public void testSemIsLiteral1Exp() {
        exportTester("testSemIsLiteral1", p.sem.isLiteral(p.xs.doubleVal(1)), "{\"ns\":\"sem\", \"fn\":\"isLiteral\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}]}");
    }

    @Test
    public void testSemIsLiteral1Exec() {
        executeTester("testSemIsLiteral1", p.sem.isLiteral(p.xs.doubleVal(1)), "fn:true()");
    }

    @Test
    public void testSemIsNumeric1Exp() {
        exportTester("testSemIsNumeric1", p.sem.isNumeric(p.xs.string("a")), "{\"ns\":\"sem\", \"fn\":\"isNumeric\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}]}");
    }

    @Test
    public void testSemIsNumeric1Exec() {
        executeTester("testSemIsNumeric1", p.sem.isNumeric(p.xs.string("a")), "fn:false()");
    }

    @Test
    public void testSemLang1Exp() {
        exportTester("testSemLang1", p.sem.lang(p.xs.string("abc")), "{\"ns\":\"sem\", \"fn\":\"lang\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testSemLang1Exec() {
        executeTester("testSemLang1", p.sem.lang(p.xs.string("abc")), "\"\"");
    }

    @Test
    public void testSemLangMatches2Exp() {
        exportTester("testSemLangMatches2", p.sem.langMatches(p.xs.string("abc"), p.xs.string("abc")), "{\"ns\":\"sem\", \"fn\":\"langMatches\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testSemLangMatches2Exist() {
        executeTester("testSemLangMatches2", p.sem.langMatches(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testSemQNameToIri1Exp() {
        exportTester("testSemQNameToIri1", p.sem.QNameToIri(p.xs.qname("abc")), "{\"ns\":\"sem\", \"fn\":\"QName-to-iri\", \"args\":[{\"ns\":\"xs\", \"fn\":\"QName\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testSemQNameToIri1Exist() {
        executeTester("testSemQNameToIri1", p.sem.QNameToIri(p.xs.qname("abc")));
    }

    @Test
    public void testSemRandom0Exp() {
        exportTester("testSemRandom0", p.sem.random(), "{\"ns\":\"sem\", \"fn\":\"random\", \"args\":[]}");
    }

    @Test
    public void testSemRandom0Exist() {
        executeTester("testSemRandom0", p.sem.random());
    }

    @Test
    public void testSemSameTerm2Exp() {
        exportTester("testSemSameTerm2", p.sem.sameTerm(p.xs.doubleVal(1), p.xs.doubleVal(1)), "{\"ns\":\"sem\", \"fn\":\"sameTerm\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}]}");
    }

    @Test
    public void testSemSameTerm2Exec() {
        executeTester("testSemSameTerm2", p.sem.sameTerm(p.xs.doubleVal(1), p.xs.doubleVal(1)), "fn:true()");
    }

    @Test
    public void testSemTimezoneString1Exp() {
        exportTester("testSemTimezoneString1", p.sem.timezoneString(p.xs.dateTime("2016-01-02T10:09:08Z")), "{\"ns\":\"sem\", \"fn\":\"timezone-string\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}");
    }

    @Test
    public void testSemTimezoneString1Exec() {
        executeTester("testSemTimezoneString1", p.sem.timezoneString(p.xs.dateTime("2016-01-02T10:09:08Z")), "\"Z\"");
    }

    @Test
    public void testSemTypedLiteral2Exp() {
        exportTester("testSemTypedLiteral2", p.sem.typedLiteral(p.xs.string("abc"), p.sem.iri("http://a/b")), "{\"ns\":\"sem\", \"fn\":\"typed-literal\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"sem\", \"fn\":\"iri\", \"args\":[\"http://a/b\"]}]}");
    }

    @Test
    public void testSemTypedLiteral2Exist() {
        executeTester("testSemTypedLiteral2", p.sem.typedLiteral(p.xs.string("abc"), p.sem.iri("http://a/b")));
    }

    @Test
    public void testSemUnknown2Exp() {
        exportTester("testSemUnknown2", p.sem.unknown(p.xs.string("abc"), p.sem.iri("http://a/b")), "{\"ns\":\"sem\", \"fn\":\"unknown\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"sem\", \"fn\":\"iri\", \"args\":[\"http://a/b\"]}]}");
    }

    @Test
    public void testSemUnknown2Exist() {
        executeTester("testSemUnknown2", p.sem.unknown(p.xs.string("abc"), p.sem.iri("http://a/b")));
    }

    @Test
    public void testSemUuid0Exp() {
        exportTester("testSemUuid0", p.sem.uuid(), "{\"ns\":\"sem\", \"fn\":\"uuid\", \"args\":[]}");
    }

    @Test
    public void testSemUuid0Exist() {
        executeTester("testSemUuid0", p.sem.uuid());
    }

    @Test
    public void testSemUuidString0Exp() {
        exportTester("testSemUuidString0", p.sem.uuidString(), "{\"ns\":\"sem\", \"fn\":\"uuid-string\", \"args\":[]}");
    }

    @Test
    public void testSemUuidString0Exist() {
        executeTester("testSemUuidString0", p.sem.uuidString());
    }

    @Test
    public void testSpellDoubleMetaphone1Exp() {
        exportTester("testSpellDoubleMetaphone1", p.spell.doubleMetaphone(p.xs.string("smith")), "{\"ns\":\"spell\", \"fn\":\"double-metaphone\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"smith\"]}]}");
    }

    @Test
    public void testSpellDoubleMetaphone1Exec() {
        executeTester("testSpellDoubleMetaphone1", p.spell.doubleMetaphone(p.xs.string("smith")), "(\"smo\", \"xmt\")");
    }

    @Test
    public void testSpellLevenshteinDistance2Exp() {
        exportTester("testSpellLevenshteinDistance2", p.spell.levenshteinDistance(p.xs.string("cat"), p.xs.string("cats")), "{\"ns\":\"spell\", \"fn\":\"levenshtein-distance\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"cat\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"cats\"]}]}");
    }

    @Test
    public void testSpellLevenshteinDistance2Exec() {
        executeTester("testSpellLevenshteinDistance2", p.spell.levenshteinDistance(p.xs.string("cat"), p.xs.string("cats")), "1");
    }

    @Test
    public void testSpellRomanize1Exp() {
        exportTester("testSpellRomanize1", p.spell.romanize(p.xs.string("abc")), "{\"ns\":\"spell\", \"fn\":\"romanize\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testSpellRomanize1Exist() {
        executeTester("testSpellRomanize1", p.spell.romanize(p.xs.string("abc")));
    }

    @Test
    public void testSqlBitLength0Exp() {
        exportTester("testSqlBitLength0", p.sql.bitLength(), "{\"ns\":\"sql\", \"fn\":\"bit-length\", \"args\":[]}");
    }

    @Test
    public void testSqlBitLength0Exist() {
        executeTester("testSqlBitLength0", p.sql.bitLength());
    }

    @Test
    public void testSqlBitLength1Exp() {
        exportTester("testSqlBitLength1", p.sql.bitLength(p.xs.string("abc")), "{\"ns\":\"sql\", \"fn\":\"bit-length\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testSqlBitLength1Exist() {
        executeTester("testSqlBitLength1", p.sql.bitLength(p.xs.string("abc")));
    }

    @Test
    public void testSqlInsert4Exp() {
        exportTester("testSqlInsert4", p.sql.insert(p.xs.string("axxxf"), p.xs.doubleVal(2), p.xs.doubleVal(3), p.xs.string("bcde")), "{\"ns\":\"sql\", \"fn\":\"insert\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"axxxf\"]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"bcde\"]}]}");
    }

    @Test
    public void testSqlInsert4Exec() {
        executeTester("testSqlInsert4", p.sql.insert(p.xs.string("axxxf"), p.xs.doubleVal(2), p.xs.doubleVal(3), p.xs.string("bcde")), "\"abcdef\"");
    }

    @Test
    public void testSqlInstr2Exp() {
        exportTester("testSqlInstr2", p.sql.instr(p.xs.string("abcde"), p.xs.string("cd")), "{\"ns\":\"sql\", \"fn\":\"instr\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcde\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"cd\"]}]}");
    }

    @Test
    public void testSqlInstr2Exec() {
        executeTester("testSqlInstr2", p.sql.instr(p.xs.string("abcde"), p.xs.string("cd")), "xs:unsignedInt(\"3\")");
    }

    @Test
    public void testSqlLeft2Exp() {
        exportTester("testSqlLeft2", p.sql.left(p.xs.string("abcde"), p.xs.doubleVal(3)), "{\"ns\":\"sql\", \"fn\":\"left\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcde\"]}], {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3]}]}");
    }

    @Test
    public void testSqlLeft2Exec() {
        executeTester("testSqlLeft2", p.sql.left(p.xs.string("abcde"), p.xs.doubleVal(3)), "\"abc\"");
    }

    @Test
    public void testSqlLtrim1Exp() {
        exportTester("testSqlLtrim1", p.sql.ltrim(p.xs.string("abc")), "{\"ns\":\"sql\", \"fn\":\"ltrim\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testSqlLtrim1Exist() {
        executeTester("testSqlLtrim1", p.sql.ltrim(p.xs.string("abc")));
    }

    @Test
    public void testSqlLtrim2Exp() {
        exportTester("testSqlLtrim2", p.sql.ltrim(p.xs.string("abc"), p.xs.string("abc")), "{\"ns\":\"sql\", \"fn\":\"ltrim\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testSqlLtrim2Exist() {
        executeTester("testSqlLtrim2", p.sql.ltrim(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testSqlOctetLength0Exp() {
        exportTester("testSqlOctetLength0", p.sql.octetLength(), "{\"ns\":\"sql\", \"fn\":\"octet-length\", \"args\":[]}");
    }

    @Test
    public void testSqlOctetLength0Exist() {
        executeTester("testSqlOctetLength0", p.sql.octetLength());
    }

    @Test
    public void testSqlOctetLength1Exp() {
        exportTester("testSqlOctetLength1", p.sql.octetLength(p.xs.string("abc")), "{\"ns\":\"sql\", \"fn\":\"octet-length\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testSqlOctetLength1Exist() {
        executeTester("testSqlOctetLength1", p.sql.octetLength(p.xs.string("abc")));
    }

    @Test
    public void testSqlRand0Exp() {
        exportTester("testSqlRand0", p.sql.rand(), "{\"ns\":\"sql\", \"fn\":\"rand\", \"args\":[]}");
    }

    @Test
    public void testSqlRand0Exist() {
        executeTester("testSqlRand0", p.sql.rand());
    }

    @Test
    public void testSqlRand1Exp() {
        exportTester("testSqlRand1", p.sql.rand(p.xs.unsignedLong(1)), "{\"ns\":\"sql\", \"fn\":\"rand\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[1]}]}");
    }

    @Test
    public void testSqlRand1Exist() {
        executeTester("testSqlRand1", p.sql.rand(p.xs.unsignedLong(1)));
    }

    @Test
    public void testSqlRepeat2Exp() {
        exportTester("testSqlRepeat2", p.sql.repeat(p.xs.string("abc"), p.xs.doubleVal(2)), "{\"ns\":\"sql\", \"fn\":\"repeat\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}]}");
    }

    @Test
    public void testSqlRepeat2Exec() {
        executeTester("testSqlRepeat2", p.sql.repeat(p.xs.string("abc"), p.xs.doubleVal(2)), "\"abcabc\"");
    }

    @Test
    public void testSqlRight2Exp() {
        exportTester("testSqlRight2", p.sql.right(p.xs.string("abcde"), p.xs.doubleVal(3)), "{\"ns\":\"sql\", \"fn\":\"right\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcde\"]}], {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3]}]}");
    }

    @Test
    public void testSqlRight2Exec() {
        executeTester("testSqlRight2", p.sql.right(p.xs.string("abcde"), p.xs.doubleVal(3)), "\"cde\"");
    }

    @Test
    public void testSqlRtrim1Exp() {
        exportTester("testSqlRtrim1", p.sql.rtrim(p.xs.string("abc")), "{\"ns\":\"sql\", \"fn\":\"rtrim\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testSqlRtrim1Exist() {
        executeTester("testSqlRtrim1", p.sql.rtrim(p.xs.string("abc")));
    }

    @Test
    public void testSqlRtrim2Exp() {
        exportTester("testSqlRtrim2", p.sql.rtrim(p.xs.string("abc"), p.xs.string("abc")), "{\"ns\":\"sql\", \"fn\":\"rtrim\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testSqlRtrim2Exist() {
        executeTester("testSqlRtrim2", p.sql.rtrim(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testSqlSign1Exp() {
        exportTester("testSqlSign1", p.sql.sign(p.xs.doubleVal(-3)), "{\"ns\":\"sql\", \"fn\":\"sign\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[-3]}]}");
    }

    @Test
    public void testSqlSign1Exec() {
        executeTester("testSqlSign1", p.sql.sign(p.xs.doubleVal(-3)), "xs:double(\"-1\")");
    }

    @Test
    public void testSqlSpace1Exp() {
        exportTester("testSqlSpace1", p.sql.space(p.xs.doubleVal(1.2)), "{\"ns\":\"sql\", \"fn\":\"space\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]}");
    }

    @Test
    public void testSqlSpace1Exist() {
        executeTester("testSqlSpace1", p.sql.space(p.xs.doubleVal(1.2)));
    }

    @Test
    public void testSqlTrim1Exp() {
        exportTester("testSqlTrim1", p.sql.trim(p.xs.string("abc")), "{\"ns\":\"sql\", \"fn\":\"trim\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testSqlTrim1Exist() {
        executeTester("testSqlTrim1", p.sql.trim(p.xs.string("abc")));
    }

    @Test
    public void testSqlTrim2Exp() {
        exportTester("testSqlTrim2", p.sql.trim(p.xs.string("abc"), p.xs.string("abc")), "{\"ns\":\"sql\", \"fn\":\"trim\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testSqlTrim2Exist() {
        executeTester("testSqlTrim2", p.sql.trim(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testXdmpAdd642Exp() {
        exportTester("testXdmpAdd642", p.xdmp.add64(p.xs.unsignedLong(123), p.xs.unsignedLong(456)), "{\"ns\":\"xdmp\", \"fn\":\"add64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[123]}, {\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[456]}]}");
    }

    @Test
    public void testXdmpAdd642Exec() {
        executeTester("testXdmpAdd642", p.xdmp.add64(p.xs.unsignedLong(123), p.xs.unsignedLong(456)), "xs:unsignedLong(\"579\")");
    }

    @Test
    public void testXdmpAnd642Exp() {
        exportTester("testXdmpAnd642", p.xdmp.and64(p.xs.unsignedLong(255), p.xs.unsignedLong(2)), "{\"ns\":\"xdmp\", \"fn\":\"and64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[255]}, {\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[2]}]}");
    }

    @Test
    public void testXdmpAnd642Exec() {
        executeTester("testXdmpAnd642", p.xdmp.and64(p.xs.unsignedLong(255), p.xs.unsignedLong(2)), "xs:unsignedLong(\"2\")");
    }

    @Test
    public void testXdmpBase64Decode1Exp() {
        exportTester("testXdmpBase64Decode1", p.xdmp.base64Decode(p.xs.string("c2xpbmdzIGFuZCBhcnJvd3Mgb2Ygb3V0cmFnZW91cyBmb3J0dW5l")), "{\"ns\":\"xdmp\", \"fn\":\"base64-decode\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c2xpbmdzIGFuZCBhcnJvd3Mgb2Ygb3V0cmFnZW91cyBmb3J0dW5l\"]}]}");
    }

    @Test
    public void testXdmpBase64Decode1Exec() {
        executeTester("testXdmpBase64Decode1", p.xdmp.base64Decode(p.xs.string("c2xpbmdzIGFuZCBhcnJvd3Mgb2Ygb3V0cmFnZW91cyBmb3J0dW5l")), "\"slings and arrows of outrageous fortune\"");
    }

    @Test
    public void testXdmpBase64Encode1Exp() {
        exportTester("testXdmpBase64Encode1", p.xdmp.base64Encode(p.xs.string("slings and arrows of outrageous fortune")), "{\"ns\":\"xdmp\", \"fn\":\"base64-encode\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"slings and arrows of outrageous fortune\"]}]}");
    }

    @Test
    public void testXdmpBase64Encode1Exec() {
        executeTester("testXdmpBase64Encode1", p.xdmp.base64Encode(p.xs.string("slings and arrows of outrageous fortune")), "\"c2xpbmdzIGFuZCBhcnJvd3Mgb2Ygb3V0cmFnZW91cyBmb3J0dW5l\"");
    }

    @Test
    public void testXdmpCastableAs3Exp() {
        exportTester("testXdmpCastableAs3", p.xdmp.castableAs(p.xs.string("http://www.w3.org/2001/XMLSchema"), p.xs.string("int"), p.xs.string("1")), "{\"ns\":\"xdmp\", \"fn\":\"castable-as\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://www.w3.org/2001/XMLSchema\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"int\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"1\"]}]}");
    }

    @Test
    public void testXdmpCastableAs3Exec() {
        executeTester("testXdmpCastableAs3", p.xdmp.castableAs(p.xs.string("http://www.w3.org/2001/XMLSchema"), p.xs.string("int"), p.xs.string("1")), "fn:true()");
    }

    @Test
    public void testXdmpCrypt2Exp() {
        exportTester("testXdmpCrypt2", p.xdmp.crypt(p.xs.string("123abc"), p.xs.string("admin")), "{\"ns\":\"xdmp\", \"fn\":\"crypt\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"123abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"admin\"]}]}");
    }

    @Test
    public void testXdmpCrypt2Exec() {
        executeTester("testXdmpCrypt2", p.xdmp.crypt(p.xs.string("123abc"), p.xs.string("admin")), "\"arQEnpM6JHR8vY4n3e5gr0\"");
    }

    @Test
    public void testXdmpCrypt21Exp() {
        exportTester("testXdmpCrypt21", p.xdmp.crypt2(p.xs.string("abc")), "{\"ns\":\"xdmp\", \"fn\":\"crypt2\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testXdmpCrypt21Exist() {
        executeTester("testXdmpCrypt21", p.xdmp.crypt2(p.xs.string("abc")));
    }

    @Test
    public void testXdmpDaynameFromDate1Exp() {
        exportTester("testXdmpDaynameFromDate1", p.xdmp.daynameFromDate(p.xs.date("2016-01-02")), "{\"ns\":\"xdmp\", \"fn\":\"dayname-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}]}");
    }

    @Test
    public void testXdmpDaynameFromDate1Exec() {
        executeTester("testXdmpDaynameFromDate1", p.xdmp.daynameFromDate(p.xs.date("2016-01-02")), "\"Saturday\"");
    }

    @Test
    public void testXdmpDecodeFromNCName1Exp() {
        exportTester("testXdmpDecodeFromNCName1", p.xdmp.decodeFromNCName(p.xs.string("A_20_Name")), "{\"ns\":\"xdmp\", \"fn\":\"decode-from-NCName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"A_20_Name\"]}]}");
    }

    @Test
    public void testXdmpDecodeFromNCName1Exec() {
        executeTester("testXdmpDecodeFromNCName1", p.xdmp.decodeFromNCName(p.xs.string("A_20_Name")), "\"A Name\"");
    }

    @Test
    public void testXdmpDescribe1Exp() {
        exportTester("testXdmpDescribe1", p.xdmp.describe(p.xs.string("123456")), "{\"ns\":\"xdmp\", \"fn\":\"describe\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"123456\"]}]]}");
    }

    @Test
    public void testXdmpDescribe1Exec() {
        executeTester("testXdmpDescribe1", p.xdmp.describe(p.xs.string("123456")), "\"&quot;123456&quot;\"");
    }

    @Test
    public void testXdmpDescribe2Exp() {
        exportTester("testXdmpDescribe2", p.xdmp.describe(p.xs.string("123456"), p.xs.unsignedInt(2)), "{\"ns\":\"xdmp\", \"fn\":\"describe\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"123456\"]}], {\"ns\":\"xs\", \"fn\":\"unsignedInt\", \"args\":[2]}]}");
    }

    @Test
    public void testXdmpDescribe2Exec() {
        executeTester("testXdmpDescribe2", p.xdmp.describe(p.xs.string("123456"), p.xs.unsignedInt(2)), "\"&quot;123456&quot;\"");
    }

    @Test
    public void testXdmpDescribe3Exp() {
        exportTester("testXdmpDescribe3", p.xdmp.describe(p.xs.string("123456"), p.xs.unsignedInt(2), p.xs.unsignedInt(3)), "{\"ns\":\"xdmp\", \"fn\":\"describe\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"123456\"]}], {\"ns\":\"xs\", \"fn\":\"unsignedInt\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"unsignedInt\", \"args\":[3]}]}");
    }

    @Test
    public void testXdmpDescribe3Exec() {
        executeTester("testXdmpDescribe3", p.xdmp.describe(p.xs.string("123456"), p.xs.unsignedInt(2), p.xs.unsignedInt(3)), "\"&quot;123456&quot;\"");
    }

    @Test
    public void testXdmpDiacriticLess1Exp() {
        exportTester("testXdmpDiacriticLess1", p.xdmp.diacriticLess(p.xs.string("abc")), "{\"ns\":\"xdmp\", \"fn\":\"diacritic-less\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testXdmpDiacriticLess1Exist() {
        executeTester("testXdmpDiacriticLess1", p.xdmp.diacriticLess(p.xs.string("abc")));
    }

    @Test
    public void testXdmpEncodeForNCName1Exp() {
        exportTester("testXdmpEncodeForNCName1", p.xdmp.encodeForNCName(p.xs.string("A Name")), "{\"ns\":\"xdmp\", \"fn\":\"encode-for-NCName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"A Name\"]}]}");
    }

    @Test
    public void testXdmpEncodeForNCName1Exec() {
        executeTester("testXdmpEncodeForNCName1", p.xdmp.encodeForNCName(p.xs.string("A Name")), "\"A_20_Name\"");
    }

    @Test
    public void testXdmpFormatNumber1Exp() {
        exportTester("testXdmpFormatNumber1", p.xdmp.formatNumber(p.xs.doubleVal(9)), "{\"ns\":\"xdmp\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[9]}]]}");
    }

    @Test
    public void testXdmpFormatNumber1Exec() {
        executeTester("testXdmpFormatNumber1", p.xdmp.formatNumber(p.xs.doubleVal(9)), "\"9\"");
    }

    @Test
    public void testXdmpFormatNumber2Exp() {
        exportTester("testXdmpFormatNumber2", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W")), "{\"ns\":\"xdmp\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[9]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"W\"]}]}");
    }

    @Test
    public void testXdmpFormatNumber2Exec() {
        executeTester("testXdmpFormatNumber2", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W")), "\"9\"");
    }

    @Test
    public void testXdmpFormatNumber3Exp() {
        exportTester("testXdmpFormatNumber3", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en")), "{\"ns\":\"xdmp\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[9]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"W\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en\"]}]}");
    }

    @Test
    public void testXdmpFormatNumber3Exec() {
        executeTester("testXdmpFormatNumber3", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en")), "\"NINE\"");
    }

    @Test
    public void testXdmpFormatNumber4Exp() {
        exportTester("testXdmpFormatNumber4", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string("")), "{\"ns\":\"xdmp\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[9]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"W\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}]}");
    }

    @Test
    public void testXdmpFormatNumber4Exec() {
        executeTester("testXdmpFormatNumber4", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string("")), "\"NINE\"");
    }

    @Test
    public void testXdmpFormatNumber5Exp() {
        exportTester("testXdmpFormatNumber5", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string("")), "{\"ns\":\"xdmp\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[9]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"W\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}]}");
    }

    @Test
    public void testXdmpFormatNumber5Exec() {
        executeTester("testXdmpFormatNumber5", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string("")), "\"NINE\"");
    }

    @Test
    public void testXdmpFormatNumber6Exp() {
        exportTester("testXdmpFormatNumber6", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string(""), p.xs.string("")), "{\"ns\":\"xdmp\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[9]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"W\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}]}");
    }

    @Test
    public void testXdmpFormatNumber6Exec() {
        executeTester("testXdmpFormatNumber6", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string(""), p.xs.string("")), "\"NINE\"");
    }

    @Test
    public void testXdmpFormatNumber7Exp() {
        exportTester("testXdmpFormatNumber7", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string(""), p.xs.string(""), p.xs.string(",")), "{\"ns\":\"xdmp\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[9]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"W\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\",\"]}]}");
    }

    @Test
    public void testXdmpFormatNumber7Exec() {
        executeTester("testXdmpFormatNumber7", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string(""), p.xs.string(""), p.xs.string(",")), "\"NINE\"");
    }

    @Test
    public void testXdmpFormatNumber8Exp() {
        exportTester("testXdmpFormatNumber8", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string(""), p.xs.string(""), p.xs.string(","), p.xs.integer(3)), "{\"ns\":\"xdmp\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[9]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"W\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\",\"]}, {\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[3]}]}");
    }

    @Test
    public void testXdmpFormatNumber8Exec() {
        executeTester("testXdmpFormatNumber8", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string(""), p.xs.string(""), p.xs.string(","), p.xs.integer(3)), "\"NINE\"");
    }

    @Test
    public void testXdmpGetCurrentUser0Exp() {
        exportTester("testXdmpGetCurrentUser0", p.xdmp.getCurrentUser(), "{\"ns\":\"xdmp\", \"fn\":\"get-current-user\", \"args\":[]}");
    }

    @Test
    public void testXdmpGetCurrentUser0Exist() {
        executeTester("testXdmpGetCurrentUser0", p.xdmp.getCurrentUser());
    }

    @Test
    public void testXdmpHash321Exp() {
        exportTester("testXdmpHash321", p.xdmp.hash32(p.xs.string("abc")), "{\"ns\":\"xdmp\", \"fn\":\"hash32\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testXdmpHash321Exec() {
        executeTester("testXdmpHash321", p.xdmp.hash32(p.xs.string("abc")), "xs:unsignedInt(\"4229403455\")");
    }

    @Test
    public void testXdmpHash641Exp() {
        exportTester("testXdmpHash641", p.xdmp.hash64(p.xs.string("abc")), "{\"ns\":\"xdmp\", \"fn\":\"hash64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testXdmpHash641Exec() {
        executeTester("testXdmpHash641", p.xdmp.hash64(p.xs.string("abc")), "xs:unsignedLong(\"13056678368508584127\")");
    }

    @Test
    public void testXdmpHexToInteger1Exp() {
        exportTester("testXdmpHexToInteger1", p.xdmp.hexToInteger(p.xs.string("1234567890abcdef")), "{\"ns\":\"xdmp\", \"fn\":\"hex-to-integer\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"1234567890abcdef\"]}]}");
    }

    @Test
    public void testXdmpHexToInteger1Exec() {
        executeTester("testXdmpHexToInteger1", p.xdmp.hexToInteger(p.xs.string("1234567890abcdef")), "1311768467294899695");
    }

    @Test
    public void testXdmpHmacMd52Exp() {
        exportTester("testXdmpHmacMd52", p.xdmp.hmacMd5(p.xs.string("abc"), p.xs.string("def")), "{\"ns\":\"xdmp\", \"fn\":\"hmac-md5\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"def\"]}]}");
    }

    @Test
    public void testXdmpHmacMd52Exec() {
        executeTester("testXdmpHmacMd52", p.xdmp.hmacMd5(p.xs.string("abc"), p.xs.string("def")), "\"debda77b7cc3e7a10ee70104e6717a6b\"");
    }

    @Test
    public void testXdmpHmacMd53Exp() {
        exportTester("testXdmpHmacMd53", p.xdmp.hmacMd5(p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64")), "{\"ns\":\"xdmp\", \"fn\":\"hmac-md5\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"def\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}");
    }

    @Test
    public void testXdmpHmacMd53Exec() {
        executeTester("testXdmpHmacMd53", p.xdmp.hmacMd5(p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64")), "\"3r2ne3zD56EO5wEE5nF6aw==\"");
    }

    @Test
    public void testXdmpHmacSha12Exp() {
        exportTester("testXdmpHmacSha12", p.xdmp.hmacSha1(p.xs.string("abc"), p.xs.string("def")), "{\"ns\":\"xdmp\", \"fn\":\"hmac-sha1\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"def\"]}]}");
    }

    @Test
    public void testXdmpHmacSha12Exec() {
        executeTester("testXdmpHmacSha12", p.xdmp.hmacSha1(p.xs.string("abc"), p.xs.string("def")), "\"12554eabbaf7e8e12e4737020f987ca7901016e5\"");
    }

    @Test
    public void testXdmpHmacSha13Exp() {
        exportTester("testXdmpHmacSha13", p.xdmp.hmacSha1(p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64")), "{\"ns\":\"xdmp\", \"fn\":\"hmac-sha1\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"def\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}");
    }

    @Test
    public void testXdmpHmacSha13Exec() {
        executeTester("testXdmpHmacSha13", p.xdmp.hmacSha1(p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64")), "\"ElVOq7r36OEuRzcCD5h8p5AQFuU=\"");
    }

    @Test
    public void testXdmpHmacSha2562Exp() {
        exportTester("testXdmpHmacSha2562", p.xdmp.hmacSha256(p.xs.string("abc"), p.xs.string("def")), "{\"ns\":\"xdmp\", \"fn\":\"hmac-sha256\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"def\"]}]}");
    }

    @Test
    public void testXdmpHmacSha2562Exec() {
        executeTester("testXdmpHmacSha2562", p.xdmp.hmacSha256(p.xs.string("abc"), p.xs.string("def")), "\"20ebc0f09344470134f35040f63ea98b1d8e414212949ee5c500429d15eab081\"");
    }

    @Test
    public void testXdmpHmacSha2563Exp() {
        exportTester("testXdmpHmacSha2563", p.xdmp.hmacSha256(p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64")), "{\"ns\":\"xdmp\", \"fn\":\"hmac-sha256\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"def\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}");
    }

    @Test
    public void testXdmpHmacSha2563Exec() {
        executeTester("testXdmpHmacSha2563", p.xdmp.hmacSha256(p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64")), "\"IOvA8JNERwE081BA9j6pix2OQUISlJ7lxQBCnRXqsIE=\"");
    }

    @Test
    public void testXdmpHmacSha5122Exp() {
        exportTester("testXdmpHmacSha5122", p.xdmp.hmacSha512(p.xs.string("abc"), p.xs.string("def")), "{\"ns\":\"xdmp\", \"fn\":\"hmac-sha512\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"def\"]}]}");
    }

    @Test
    public void testXdmpHmacSha5122Exec() {
        executeTester("testXdmpHmacSha5122", p.xdmp.hmacSha512(p.xs.string("abc"), p.xs.string("def")), "\"bf93c3deee1eb6660ec00820a285327b3e8b775f641fd7f2ea321b6a241afe7b49a5cca81d2e8e1d206bd3379530e2d9ad3a7b2cc54ca66ea3352ebfee3862e5\"");
    }

    @Test
    public void testXdmpHmacSha5123Exp() {
        exportTester("testXdmpHmacSha5123", p.xdmp.hmacSha512(p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64")), "{\"ns\":\"xdmp\", \"fn\":\"hmac-sha512\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"def\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}");
    }

    @Test
    public void testXdmpHmacSha5123Exec() {
        executeTester("testXdmpHmacSha5123", p.xdmp.hmacSha512(p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64")), "\"v5PD3u4etmYOwAggooUyez6Ld19kH9fy6jIbaiQa/ntJpcyoHS6OHSBr0zeVMOLZrTp7LMVMpm6jNS6/7jhi5Q==\"");
    }

    @Test
    public void testXdmpInitcap1Exp() {
        exportTester("testXdmpInitcap1", p.xdmp.initcap(p.xs.string("abc")), "{\"ns\":\"xdmp\", \"fn\":\"initcap\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testXdmpInitcap1Exec() {
        executeTester("testXdmpInitcap1", p.xdmp.initcap(p.xs.string("abc")), "\"Abc\"");
    }

    @Test
    public void testXdmpIntegerToHex1Exp() {
        exportTester("testXdmpIntegerToHex1", p.xdmp.integerToHex(p.xs.integer(123)), "{\"ns\":\"xdmp\", \"fn\":\"integer-to-hex\", \"args\":[{\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[123]}]}");
    }

    @Test
    public void testXdmpIntegerToHex1Exec() {
        executeTester("testXdmpIntegerToHex1", p.xdmp.integerToHex(p.xs.integer(123)), "\"7b\"");
    }

    @Test
    public void testXdmpIntegerToOctal1Exp() {
        exportTester("testXdmpIntegerToOctal1", p.xdmp.integerToOctal(p.xs.integer(123)), "{\"ns\":\"xdmp\", \"fn\":\"integer-to-octal\", \"args\":[{\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[123]}]}");
    }

    @Test
    public void testXdmpIntegerToOctal1Exec() {
        executeTester("testXdmpIntegerToOctal1", p.xdmp.integerToOctal(p.xs.integer(123)), "\"173\"");
    }

    @Test
    public void testXdmpKeyFromQName1Exp() {
        exportTester("testXdmpKeyFromQName1", p.xdmp.keyFromQName(p.xs.qname("abc")), "{\"ns\":\"xdmp\", \"fn\":\"key-from-QName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"QName\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testXdmpKeyFromQName1Exist() {
        executeTester("testXdmpKeyFromQName1", p.xdmp.keyFromQName(p.xs.qname("abc")));
    }

    @Test
    public void testXdmpLshift642Exp() {
        exportTester("testXdmpLshift642", p.xdmp.lshift64(p.xs.unsignedLong(255), p.xs.longVal(2)), "{\"ns\":\"xdmp\", \"fn\":\"lshift64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[255]}, {\"ns\":\"xs\", \"fn\":\"long\", \"args\":[2]}]}");
    }

    @Test
    public void testXdmpLshift642Exec() {
        executeTester("testXdmpLshift642", p.xdmp.lshift64(p.xs.unsignedLong(255), p.xs.longVal(2)), "xs:unsignedLong(\"1020\")");
    }

    @Test
    public void testXdmpMd51Exp() {
        exportTester("testXdmpMd51", p.xdmp.md5(p.xs.string("abc")), "{\"ns\":\"xdmp\", \"fn\":\"md5\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testXdmpMd51Exec() {
        executeTester("testXdmpMd51", p.xdmp.md5(p.xs.string("abc")), "\"900150983cd24fb0d6963f7d28e17f72\"");
    }

    @Test
    public void testXdmpMd52Exp() {
        exportTester("testXdmpMd52", p.xdmp.md5(p.xs.string("abc"), p.xs.string("base64")), "{\"ns\":\"xdmp\", \"fn\":\"md5\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}");
    }

    @Test
    public void testXdmpMd52Exec() {
        executeTester("testXdmpMd52", p.xdmp.md5(p.xs.string("abc"), p.xs.string("base64")), "\"kAFQmDzST7DWlj99KOF/cg==\"");
    }

    @Test
    public void testXdmpMonthNameFromDate1Exp() {
        exportTester("testXdmpMonthNameFromDate1", p.xdmp.monthNameFromDate(p.xs.date("2016-01-02")), "{\"ns\":\"xdmp\", \"fn\":\"month-name-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}]}");
    }

    @Test
    public void testXdmpMonthNameFromDate1Exec() {
        executeTester("testXdmpMonthNameFromDate1", p.xdmp.monthNameFromDate(p.xs.date("2016-01-02")), "\"January\"");
    }

    @Test
    public void testXdmpMul642Exp() {
        exportTester("testXdmpMul642", p.xdmp.mul64(p.xs.unsignedLong(123), p.xs.unsignedLong(456)), "{\"ns\":\"xdmp\", \"fn\":\"mul64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[123]}, {\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[456]}]}");
    }

    @Test
    public void testXdmpMul642Exec() {
        executeTester("testXdmpMul642", p.xdmp.mul64(p.xs.unsignedLong(123), p.xs.unsignedLong(456)), "xs:unsignedLong(\"56088\")");
    }

    @Test
    public void testXdmpNot641Exp() {
        exportTester("testXdmpNot641", p.xdmp.not64(p.xs.unsignedLong(255)), "{\"ns\":\"xdmp\", \"fn\":\"not64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[255]}]}");
    }

    @Test
    public void testXdmpNot641Exec() {
        executeTester("testXdmpNot641", p.xdmp.not64(p.xs.unsignedLong(255)), "xs:unsignedLong(\"18446744073709551360\")");
    }

    @Test
    public void testXdmpOctalToInteger1Exp() {
        exportTester("testXdmpOctalToInteger1", p.xdmp.octalToInteger(p.xs.string("12345670")), "{\"ns\":\"xdmp\", \"fn\":\"octal-to-integer\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"12345670\"]}]}");
    }

    @Test
    public void testXdmpOctalToInteger1Exec() {
        executeTester("testXdmpOctalToInteger1", p.xdmp.octalToInteger(p.xs.string("12345670")), "2739128");
    }

    @Test
    public void testXdmpOr642Exp() {
        exportTester("testXdmpOr642", p.xdmp.or64(p.xs.unsignedLong(255), p.xs.unsignedLong(2)), "{\"ns\":\"xdmp\", \"fn\":\"or64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[255]}, {\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[2]}]}");
    }

    @Test
    public void testXdmpOr642Exec() {
        executeTester("testXdmpOr642", p.xdmp.or64(p.xs.unsignedLong(255), p.xs.unsignedLong(2)), "xs:unsignedLong(\"255\")");
    }

    @Test
    public void testXdmpParseDateTime2Exp() {
        exportTester("testXdmpParseDateTime2", p.xdmp.parseDateTime(p.xs.string("[Y0001]-[M01]-[D01]T[h01]:[m01]:[s01].[f1][Z]"), p.xs.string("2016-01-06T17:13:50.873594-08:00")), "{\"ns\":\"xdmp\", \"fn\":\"parse-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"[Y0001]-[M01]-[D01]T[h01]:[m01]:[s01].[f1][Z]\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"2016-01-06T17:13:50.873594-08:00\"]}]}");
    }

    @Test
    public void testXdmpParseDateTime2Exec() {
        executeTester("testXdmpParseDateTime2", p.xdmp.parseDateTime(p.xs.string("[Y0001]-[M01]-[D01]T[h01]:[m01]:[s01].[f1][Z]"), p.xs.string("2016-01-06T17:13:50.873594-08:00")), "xs:dateTime(\"2016-01-06T21:13:50.874-04:00\")");
    }

    @Test
    public void testXdmpParseYymmdd2Exp() {
        exportTester("testXdmpParseYymmdd2", p.xdmp.parseYymmdd(p.xs.string("yyyy-MM-ddThh:mm:ss.Sz"), p.xs.string("2016-01-06T17:13:50.873594-8.00")), "{\"ns\":\"xdmp\", \"fn\":\"parse-yymmdd\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"yyyy-MM-ddThh:mm:ss.Sz\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"2016-01-06T17:13:50.873594-8.00\"]}]}");
    }

    @Test
    public void testXdmpParseYymmdd2Exec() {
        executeTester("testXdmpParseYymmdd2", p.xdmp.parseYymmdd(p.xs.string("yyyy-MM-ddThh:mm:ss.Sz"), p.xs.string("2016-01-06T17:13:50.873594-8.00")), "xs:dateTime(\"2016-01-06T21:13:50.874-04:00\")");
    }

    @Test
    public void testXdmpPosition2Exp() {
        exportTester("testXdmpPosition2", p.xdmp.position(p.xs.string("abcdef"), p.xs.string("cd")), "{\"ns\":\"xdmp\", \"fn\":\"position\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcdef\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"cd\"]}]}");
    }

    @Test
    public void testXdmpPosition2Exec() {
        executeTester("testXdmpPosition2", p.xdmp.position(p.xs.string("abcdef"), p.xs.string("cd")), "0");
    }

    @Test
    public void testXdmpPosition3Exp() {
        exportTester("testXdmpPosition3", p.xdmp.position(p.xs.string("abcdef"), p.xs.string("cd"), p.xs.string("http://marklogic.com/collation/")), "{\"ns\":\"xdmp\", \"fn\":\"position\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcdef\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"cd\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}");
    }

    @Test
    public void testXdmpPosition3Exec() {
        executeTester("testXdmpPosition3", p.xdmp.position(p.xs.string("abcdef"), p.xs.string("cd"), p.xs.string("http://marklogic.com/collation/")), "0");
    }

    @Test
    public void testXdmpQNameFromKey1Exp() {
        exportTester("testXdmpQNameFromKey1", p.xdmp.QNameFromKey(p.xs.string("{http://a/b}c")), "{\"ns\":\"xdmp\", \"fn\":\"QName-from-key\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"{http://a/b}c\"]}]}");
    }

    @Test
    public void testXdmpQNameFromKey1Exec() {
        executeTester("testXdmpQNameFromKey1", p.xdmp.QNameFromKey(p.xs.string("{http://a/b}c")), "fn:QName(\"http://a/b\",\"c\")");
    }

    @Test
    public void testXdmpQuarterFromDate1Exp() {
        exportTester("testXdmpQuarterFromDate1", p.xdmp.quarterFromDate(p.xs.date("2016-01-02")), "{\"ns\":\"xdmp\", \"fn\":\"quarter-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}]}");
    }

    @Test
    public void testXdmpQuarterFromDate1Exec() {
        executeTester("testXdmpQuarterFromDate1", p.xdmp.quarterFromDate(p.xs.date("2016-01-02")), "1");
    }

    @Test
    public void testXdmpRandom0Exp() {
        exportTester("testXdmpRandom0", p.xdmp.random(), "{\"ns\":\"xdmp\", \"fn\":\"random\", \"args\":[]}");
    }

    @Test
    public void testXdmpRandom0Exist() {
        executeTester("testXdmpRandom0", p.xdmp.random());
    }

    @Test
    public void testXdmpRandom1Exp() {
        exportTester("testXdmpRandom1", p.xdmp.random(p.xs.unsignedLong(1)), "{\"ns\":\"xdmp\", \"fn\":\"random\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[1]}]}");
    }

    @Test
    public void testXdmpRandom1Exist() {
        executeTester("testXdmpRandom1", p.xdmp.random(p.xs.unsignedLong(1)));
    }

    @Test
    public void testXdmpResolveUri2Exp() {
        exportTester("testXdmpResolveUri2", p.xdmp.resolveUri(p.xs.string("b?c#d"), p.xs.string("/a/x")), "{\"ns\":\"xdmp\", \"fn\":\"resolve-uri\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b?c#d\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"/a/x\"]}]}");
    }

    @Test
    public void testXdmpResolveUri2Exec() {
        executeTester("testXdmpResolveUri2", p.xdmp.resolveUri(p.xs.string("b?c#d"), p.xs.string("/a/x")), "xs:anyURI(\"/a/b?c#d\")");
    }

    @Test
    public void testXdmpRshift642Exp() {
        exportTester("testXdmpRshift642", p.xdmp.rshift64(p.xs.unsignedLong(255), p.xs.longVal(2)), "{\"ns\":\"xdmp\", \"fn\":\"rshift64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[255]}, {\"ns\":\"xs\", \"fn\":\"long\", \"args\":[2]}]}");
    }

    @Test
    public void testXdmpRshift642Exec() {
        executeTester("testXdmpRshift642", p.xdmp.rshift64(p.xs.unsignedLong(255), p.xs.longVal(2)), "xs:unsignedLong(\"63\")");
    }

    @Test
    public void testXdmpSha11Exp() {
        exportTester("testXdmpSha11", p.xdmp.sha1(p.xs.string("abc")), "{\"ns\":\"xdmp\", \"fn\":\"sha1\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testXdmpSha11Exec() {
        executeTester("testXdmpSha11", p.xdmp.sha1(p.xs.string("abc")), "\"a9993e364706816aba3e25717850c26c9cd0d89d\"");
    }

    @Test
    public void testXdmpSha12Exp() {
        exportTester("testXdmpSha12", p.xdmp.sha1(p.xs.string("abc"), p.xs.string("base64")), "{\"ns\":\"xdmp\", \"fn\":\"sha1\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}");
    }

    @Test
    public void testXdmpSha12Exec() {
        executeTester("testXdmpSha12", p.xdmp.sha1(p.xs.string("abc"), p.xs.string("base64")), "\"qZk+NkcGgWq6PiVxeFDCbJzQ2J0=\"");
    }

    @Test
    public void testXdmpSha2561Exp() {
        exportTester("testXdmpSha2561", p.xdmp.sha256(p.xs.string("abc")), "{\"ns\":\"xdmp\", \"fn\":\"sha256\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testXdmpSha2561Exec() {
        executeTester("testXdmpSha2561", p.xdmp.sha256(p.xs.string("abc")), "\"ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad\"");
    }

    @Test
    public void testXdmpSha2562Exp() {
        exportTester("testXdmpSha2562", p.xdmp.sha256(p.xs.string("abc"), p.xs.string("base64")), "{\"ns\":\"xdmp\", \"fn\":\"sha256\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}");
    }

    @Test
    public void testXdmpSha2562Exec() {
        executeTester("testXdmpSha2562", p.xdmp.sha256(p.xs.string("abc"), p.xs.string("base64")), "\"ungWv48Bz+pBQUDeXa4iI7ADYaOWF3qctBD/YfIAFa0=\"");
    }

    @Test
    public void testXdmpSha3841Exp() {
        exportTester("testXdmpSha3841", p.xdmp.sha384(p.xs.string("abc")), "{\"ns\":\"xdmp\", \"fn\":\"sha384\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testXdmpSha3841Exec() {
        executeTester("testXdmpSha3841", p.xdmp.sha384(p.xs.string("abc")), "\"cb00753f45a35e8bb5a03d699ac65007272c32ab0eded1631a8b605a43ff5bed8086072ba1e7cc2358baeca134c825a7\"");
    }

    @Test
    public void testXdmpSha3842Exp() {
        exportTester("testXdmpSha3842", p.xdmp.sha384(p.xs.string("abc"), p.xs.string("base64")), "{\"ns\":\"xdmp\", \"fn\":\"sha384\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}");
    }

    @Test
    public void testXdmpSha3842Exec() {
        executeTester("testXdmpSha3842", p.xdmp.sha384(p.xs.string("abc"), p.xs.string("base64")), "\"ywB1P0WjXou1oD1pmsZQBycsMqsO3tFjGotgWkP/W+2AhgcroefMI1i67KE0yCWn\"");
    }

    @Test
    public void testXdmpSha5121Exp() {
        exportTester("testXdmpSha5121", p.xdmp.sha512(p.xs.string("abc")), "{\"ns\":\"xdmp\", \"fn\":\"sha512\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testXdmpSha5121Exec() {
        executeTester("testXdmpSha5121", p.xdmp.sha512(p.xs.string("abc")), "\"ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f\"");
    }

    @Test
    public void testXdmpSha5122Exp() {
        exportTester("testXdmpSha5122", p.xdmp.sha512(p.xs.string("abc"), p.xs.string("base64")), "{\"ns\":\"xdmp\", \"fn\":\"sha512\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}");
    }

    @Test
    public void testXdmpSha5122Exec() {
        executeTester("testXdmpSha5122", p.xdmp.sha512(p.xs.string("abc"), p.xs.string("base64")), "\"3a81oZNherrMQXNJriBBMRLm+k6JqX6iCp7u5ktV05ohkpkqJ0/BqDa6PCOj/uu9RU1EI2Q86A4qmslPpUyknw==\"");
    }

    @Test
    public void testXdmpStep642Exp() {
        exportTester("testXdmpStep642", p.xdmp.step64(p.xs.unsignedLong(123), p.xs.unsignedLong(456)), "{\"ns\":\"xdmp\", \"fn\":\"step64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[123]}, {\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[456]}]}");
    }

    @Test
    public void testXdmpStep642Exec() {
        executeTester("testXdmpStep642", p.xdmp.step64(p.xs.unsignedLong(123), p.xs.unsignedLong(456)), "xs:unsignedLong(\"8966314677\")");
    }

    @Test
    public void testXdmpStrftime2Exp() {
        exportTester("testXdmpStrftime2", p.xdmp.strftime(p.xs.string("%a, %d %b %Y %H:%M:%S"), p.xs.dateTime("2016-01-06T17:13:50.873594-08:00")), "{\"ns\":\"xdmp\", \"fn\":\"strftime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"%a, %d %b %Y %H:%M:%S\"]}, {\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-06T17:13:50.873594-08:00\"]}]}");
    }

    @Test
    public void testXdmpStrftime2Exec() {
        executeTester("testXdmpStrftime2", p.xdmp.strftime(p.xs.string("%a, %d %b %Y %H:%M:%S"), p.xs.dateTime("2016-01-06T17:13:50.873594-08:00")), "\"Wed, 06 Jan 2016 20:13:50\"");
    }

    @Test
    public void testXdmpTimestampToWallclock1Exp() {
        exportTester("testXdmpTimestampToWallclock1", p.xdmp.timestampToWallclock(p.xs.unsignedLong(1)), "{\"ns\":\"xdmp\", \"fn\":\"timestamp-to-wallclock\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[1]}]}");
    }

    @Test
    public void testXdmpTimestampToWallclock1Exist() {
        executeTester("testXdmpTimestampToWallclock1", p.xdmp.timestampToWallclock(p.xs.unsignedLong(1)));
    }

    @Test
    public void testXdmpToJson1Exp() {
        exportTester("testXdmpToJson1", p.xdmp.toJson(p.xs.string("abc")), "{\"ns\":\"xdmp\", \"fn\":\"to-json\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}");
    }

    @Test
    public void testXdmpToJson1Exist() {
        executeTester("testXdmpToJson1", p.xdmp.toJson(p.xs.string("abc")));
    }

    @Test
    public void testXdmpType1Exp() {
        exportTester("testXdmpType1", p.xdmp.type(p.xs.string("a")), "{\"ns\":\"xdmp\", \"fn\":\"type\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}]}");
    }

    @Test
    public void testXdmpType1Exec() {
        executeTester("testXdmpType1", p.xdmp.type(p.xs.string("a")), "fn:QName(\"http://www.w3.org/2001/XMLSchema\",\"string\")");
    }

    @Test
    public void testXdmpUrlDecode1Exp() {
        exportTester("testXdmpUrlDecode1", p.xdmp.urlDecode(p.xs.string("a+b")), "{\"ns\":\"xdmp\", \"fn\":\"url-decode\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a+b\"]}]}");
    }

    @Test
    public void testXdmpUrlDecode1Exec() {
        executeTester("testXdmpUrlDecode1", p.xdmp.urlDecode(p.xs.string("a+b")), "\"a b\"");
    }

    @Test
    public void testXdmpUrlEncode1Exp() {
        exportTester("testXdmpUrlEncode1", p.xdmp.urlEncode(p.xs.string("a b")), "{\"ns\":\"xdmp\", \"fn\":\"url-encode\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a b\"]}]}");
    }

    @Test
    public void testXdmpUrlEncode1Exec() {
        executeTester("testXdmpUrlEncode1", p.xdmp.urlEncode(p.xs.string("a b")), "\"a+b\"");
    }

    @Test
    public void testXdmpWallclockToTimestamp1Exp() {
        exportTester("testXdmpWallclockToTimestamp1", p.xdmp.wallclockToTimestamp(p.xs.dateTime("2016-01-06T17:13:50.873594-08:00")), "{\"ns\":\"xdmp\", \"fn\":\"wallclock-to-timestamp\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-06T17:13:50.873594-08:00\"]}]}");
    }

    @Test
    public void testXdmpWallclockToTimestamp1Exist() {
        executeTester("testXdmpWallclockToTimestamp1", p.xdmp.wallclockToTimestamp(p.xs.dateTime("2016-01-06T17:13:50.873594-08:00")));
    }

    @Test
    public void testXdmpWeekdayFromDate1Exp() {
        exportTester("testXdmpWeekdayFromDate1", p.xdmp.weekdayFromDate(p.xs.date("2016-01-02")), "{\"ns\":\"xdmp\", \"fn\":\"weekday-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}]}");
    }

    @Test
    public void testXdmpWeekdayFromDate1Exec() {
        executeTester("testXdmpWeekdayFromDate1", p.xdmp.weekdayFromDate(p.xs.date("2016-01-02")), "6");
    }

    @Test
    public void testXdmpWeekFromDate1Exp() {
        exportTester("testXdmpWeekFromDate1", p.xdmp.weekFromDate(p.xs.date("2016-01-02")), "{\"ns\":\"xdmp\", \"fn\":\"week-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}]}");
    }

    @Test
    public void testXdmpWeekFromDate1Exec() {
        executeTester("testXdmpWeekFromDate1", p.xdmp.weekFromDate(p.xs.date("2016-01-02")), "53");
    }

    @Test
    public void testXdmpXor642Exp() {
        exportTester("testXdmpXor642", p.xdmp.xor64(p.xs.unsignedLong(255), p.xs.unsignedLong(2)), "{\"ns\":\"xdmp\", \"fn\":\"xor64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[255]}, {\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[2]}]}");
    }

    @Test
    public void testXdmpXor642Exec() {
        executeTester("testXdmpXor642", p.xdmp.xor64(p.xs.unsignedLong(255), p.xs.unsignedLong(2)), "xs:unsignedLong(\"253\")");
    }

    @Test
    public void testXdmpYeardayFromDate1Exp() {
        exportTester("testXdmpYeardayFromDate1", p.xdmp.yeardayFromDate(p.xs.date("2016-01-02")), "{\"ns\":\"xdmp\", \"fn\":\"yearday-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}]}");
    }

    @Test
    public void testXdmpYeardayFromDate1Exec() {
        executeTester("testXdmpYeardayFromDate1", p.xdmp.yeardayFromDate(p.xs.date("2016-01-02")), "2");
    }

    @Test
    public void testXsAnyURI1Exp() {
        exportTester("testXsAnyURI1", p.xs.anyURI("http://a/b?c#d"), "{\"ns\":\"xs\", \"fn\":\"anyURI\", \"args\":[\"http://a/b?c#d\"]}");
    }

    @Test
    public void testXsAnyURI1Exec() {
        executeTester("testXsAnyURI1", p.xs.anyURI("http://a/b?c#d"), "xs:anyURI(\"http://a/b?c#d\")");
    }

    @Test
    public void testXsBase64Binary1Exp() {
        exportTester("testXsBase64Binary1", p.xs.base64Binary(p.xs.string("abc")), "{\"ns\":\"xs\", \"fn\":\"base64Binary\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testXsBoolean1Exp() {
        exportTester("testXsBoolean1", p.xs.booleanVal(true), "{\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[true]}");
    }

    @Test
    public void testXsBoolean1Exec() {
        executeTester("testXsBoolean1", p.xs.booleanVal(true), "fn:true()");
    }

    @Test
    public void testXsByte1Exp() {
        exportTester("testXsByte1", p.xs.byteVal((byte) 1), "{\"ns\":\"xs\", \"fn\":\"byte\", \"args\":[1]}");
    }

    @Test
    public void testXsByte1Exec() {
        executeTester("testXsByte1", p.xs.byteVal((byte) 1), "xs:byte(\"1\")");
    }

    @Test
    public void testXsDate1Exp() {
        exportTester("testXsDate1", p.xs.date("2016-01-02"), "{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}");
    }

    @Test
    public void testXsDate1Exec() {
        executeTester("testXsDate1", p.xs.date("2016-01-02"), "xs:date(\"2016-01-02\")");
    }

    @Test
    public void testXsDateTime1Exp() {
        exportTester("testXsDateTime1", p.xs.dateTime("2016-01-02T10:09:08Z"), "{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}");
    }

    @Test
    public void testXsDateTime1Exec() {
        executeTester("testXsDateTime1", p.xs.dateTime("2016-01-02T10:09:08Z"), "xs:dateTime(\"2016-01-02T10:09:08Z\")");
    }

    @Test
    public void testXsDayTimeDuration1Exp() {
        exportTester("testXsDayTimeDuration1", p.xs.dayTimeDuration("P3DT4H5M6S"), "{\"ns\":\"xs\", \"fn\":\"dayTimeDuration\", \"args\":[\"P3DT4H5M6S\"]}");
    }

    @Test
    public void testXsDayTimeDuration1Exec() {
        executeTester("testXsDayTimeDuration1", p.xs.dayTimeDuration("P3DT4H5M6S"), "xs:dayTimeDuration(\"P3DT4H5M6S\")");
    }

    @Test
    public void testXsDecimal1Exp() {
        exportTester("testXsDecimal1", p.xs.decimal(1.2), "{\"ns\":\"xs\", \"fn\":\"decimal\", \"args\":[1.2]}");
    }

    @Test
    public void testXsDecimal1Exec() {
        executeTester("testXsDecimal1", p.xs.decimal(1.2), "1.2");
    }

    @Test
    public void testXsDouble1Exp() {
        exportTester("testXsDouble1", p.xs.doubleVal(1.2), "{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}");
    }

    @Test
    public void testXsDouble1Exec() {
        executeTester("testXsDouble1", p.xs.doubleVal(1.2), "xs:double(\"1.2\")");
    }

    @Test
    public void testXsDuration1Exp() {
        exportTester("testXsDuration1", p.xs.duration(p.xs.string("P1Y2M")), "{\"ns\":\"xs\", \"fn\":\"duration\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"P1Y2M\"]}]}");
    }

    @Test
    public void testXsDuration1Exec() {
        executeTester("testXsDuration1", p.xs.duration(p.xs.string("P1Y2M")), "xs:duration(\"P1Y2M\")");
    }

    @Test
    public void testXsFloat1Exp() {
        exportTester("testXsFloat1", p.xs.floatVal((float) 1), "{\"ns\":\"xs\", \"fn\":\"float\", \"args\":[1]}");
    }

    @Test
    public void testXsFloat1Exec() {
        executeTester("testXsFloat1", p.xs.floatVal((float) 1), "xs:float(\"1\")");
    }

    @Test
    public void testXsGDay1Exp() {
        exportTester("testXsGDay1", p.xs.gDay("---02"), "{\"ns\":\"xs\", \"fn\":\"gDay\", \"args\":[\"---02\"]}");
    }

    @Test
    public void testXsGDay1Exec() {
        executeTester("testXsGDay1", p.xs.gDay("---02"), "xs:gDay(\"---02\")");
    }

    @Test
    public void testXsGMonth1Exp() {
        exportTester("testXsGMonth1", p.xs.gMonth("--01"), "{\"ns\":\"xs\", \"fn\":\"gMonth\", \"args\":[\"--01\"]}");
    }

    @Test
    public void testXsGMonth1Exec() {
        executeTester("testXsGMonth1", p.xs.gMonth("--01"), "xs:gMonth(\"--01\")");
    }

    @Test
    public void testXsGMonthDay1Exp() {
        exportTester("testXsGMonthDay1", p.xs.gMonthDay("--01-02"), "{\"ns\":\"xs\", \"fn\":\"gMonthDay\", \"args\":[\"--01-02\"]}");
    }

    @Test
    public void testXsGMonthDay1Exec() {
        executeTester("testXsGMonthDay1", p.xs.gMonthDay("--01-02"), "xs:gMonthDay(\"--01-02\")");
    }

    @Test
    public void testXsGYear1Exp() {
        exportTester("testXsGYear1", p.xs.gYear("2016"), "{\"ns\":\"xs\", \"fn\":\"gYear\", \"args\":[\"2016\"]}");
    }

    @Test
    public void testXsGYear1Exec() {
        executeTester("testXsGYear1", p.xs.gYear("2016"), "xs:gYear(\"2016\")");
    }

    @Test
    public void testXsGYearMonth1Exp() {
        exportTester("testXsGYearMonth1", p.xs.gYearMonth("2016-01"), "{\"ns\":\"xs\", \"fn\":\"gYearMonth\", \"args\":[\"2016-01\"]}");
    }

    @Test
    public void testXsGYearMonth1Exec() {
        executeTester("testXsGYearMonth1", p.xs.gYearMonth("2016-01"), "xs:gYearMonth(\"2016-01\")");
    }

    @Test
    public void testXsHexBinary1Exp() {
        exportTester("testXsHexBinary1", p.xs.hexBinary(p.xs.string("abc")), "{\"ns\":\"xs\", \"fn\":\"hexBinary\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}");
    }

    @Test
    public void testXsInt1Exp() {
        exportTester("testXsInt1", p.xs.intVal(1), "{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[1]}");
    }

    @Test
    public void testXsInt1Exec() {
        executeTester("testXsInt1", p.xs.intVal(1), "xs:int(\"1\")");
    }

    @Test
    public void testXsInteger1Exp() {
        exportTester("testXsInteger1", p.xs.integer(1), "{\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[1]}");
    }

    @Test
    public void testXsInteger1Exec() {
        executeTester("testXsInteger1", p.xs.integer(1), "1");
    }

    @Test
    public void testXsLanguage1Exp() {
        exportTester("testXsLanguage1", p.xs.language(p.xs.string("en-US")), "{\"ns\":\"xs\", \"fn\":\"language\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en-US\"]}]}");
    }

    @Test
    public void testXsLanguage1Exec() {
        executeTester("testXsLanguage1", p.xs.language(p.xs.string("en-US")), "xs:language(\"en-US\")");
    }

    @Test
    public void testXsLong1Exp() {
        exportTester("testXsLong1", p.xs.longVal(1), "{\"ns\":\"xs\", \"fn\":\"long\", \"args\":[1]}");
    }

    @Test
    public void testXsLong1Exec() {
        executeTester("testXsLong1", p.xs.longVal(1), "xs:long(\"1\")");
    }

    @Test
    public void testXsName1Exp() {
        exportTester("testXsName1", p.xs.Name(p.xs.string("a:b:c")), "{\"ns\":\"xs\", \"fn\":\"Name\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a:b:c\"]}]}");
    }

    @Test
    public void testXsName1Exec() {
        executeTester("testXsName1", p.xs.Name(p.xs.string("a:b:c")), "xs:Name(\"a:b:c\")");
    }

    @Test
    public void testXsNCName1Exp() {
        exportTester("testXsNCName1", p.xs.NCName(p.xs.string("a-b-c")), "{\"ns\":\"xs\", \"fn\":\"NCName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a-b-c\"]}]}");
    }

    @Test
    public void testXsNCName1Exec() {
        executeTester("testXsNCName1", p.xs.NCName(p.xs.string("a-b-c")), "xs:NCName(\"a-b-c\")");
    }

    @Test
    public void testXsNegativeInteger1Exp() {
        exportTester("testXsNegativeInteger1", p.xs.negativeInteger(p.xs.doubleVal(-1)), "{\"ns\":\"xs\", \"fn\":\"negativeInteger\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[-1]}]}");
    }

    @Test
    public void testXsNegativeInteger1Exec() {
        executeTester("testXsNegativeInteger1", p.xs.negativeInteger(p.xs.doubleVal(-1)), "xs:negativeInteger(\"-1\")");
    }

    @Test
    public void testXsNMTOKEN1Exp() {
        exportTester("testXsNMTOKEN1", p.xs.NMTOKEN(p.xs.string("a:b:c")), "{\"ns\":\"xs\", \"fn\":\"NMTOKEN\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a:b:c\"]}]}");
    }

    @Test
    public void testXsNMTOKEN1Exec() {
        executeTester("testXsNMTOKEN1", p.xs.NMTOKEN(p.xs.string("a:b:c")), "xs:NMTOKEN(\"a:b:c\")");
    }

    @Test
    public void testXsNonNegativeInteger1Exp() {
        exportTester("testXsNonNegativeInteger1", p.xs.nonNegativeInteger(p.xs.string("0")), "{\"ns\":\"xs\", \"fn\":\"nonNegativeInteger\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"0\"]}]}");
    }

    @Test
    public void testXsNonNegativeInteger1Exec() {
        executeTester("testXsNonNegativeInteger1", p.xs.nonNegativeInteger(p.xs.string("0")), "xs:nonNegativeInteger(\"0\")");
    }

    @Test
    public void testXsNonPositiveInteger1Exp() {
        exportTester("testXsNonPositiveInteger1", p.xs.nonPositiveInteger(p.xs.string("0")), "{\"ns\":\"xs\", \"fn\":\"nonPositiveInteger\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"0\"]}]}");
    }

    @Test
    public void testXsNonPositiveInteger1Exec() {
        executeTester("testXsNonPositiveInteger1", p.xs.nonPositiveInteger(p.xs.string("0")), "xs:nonPositiveInteger(\"0\")");
    }

    @Test
    public void testXsNormalizedString1Exp() {
        exportTester("testXsNormalizedString1", p.xs.normalizedString(p.xs.string("a b c")), "{\"ns\":\"xs\", \"fn\":\"normalizedString\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a b c\"]}]}");
    }

    @Test
    public void testXsNormalizedString1Exec() {
        executeTester("testXsNormalizedString1", p.xs.normalizedString(p.xs.string("a b c")), "xs:normalizedString(\"a b c\")");
    }

    @Test
    public void testXsNumeric1Exp() {
        exportTester("testXsNumeric1", p.xs.numeric(p.xs.doubleVal(1.2)), "{\"ns\":\"xs\", \"fn\":\"numeric\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]}");
    }

    @Test
    public void testXsPositiveInteger1Exp() {
        exportTester("testXsPositiveInteger1", p.xs.positiveInteger(p.xs.doubleVal(1)), "{\"ns\":\"xs\", \"fn\":\"positiveInteger\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}]}");
    }

    @Test
    public void testXsPositiveInteger1Exec() {
        executeTester("testXsPositiveInteger1", p.xs.positiveInteger(p.xs.doubleVal(1)), "xs:positiveInteger(\"1\")");
    }

    @Test
    public void testXsQName1Exp() {
        exportTester("testXsQName1", p.xs.qname("abc"), "{\"ns\":\"xs\", \"fn\":\"QName\", \"args\":[\"abc\"]}");
    }

    @Test
    public void testXsQName1Exec() {
        executeTester("testXsQName1", p.xs.qname("abc"), "fn:QName(\"\",\"abc\")");
    }

    @Test
    public void testXsShort1Exp() {
        exportTester("testXsShort1", p.xs.shortVal((short) 1), "{\"ns\":\"xs\", \"fn\":\"short\", \"args\":[1]}");
    }

    @Test
    public void testXsShort1Exec() {
        executeTester("testXsShort1", p.xs.shortVal((short) 1), "xs:short(\"1\")");
    }

    @Test
    public void testXsString1Exp() {
        exportTester("testXsString1", p.xs.string("abc"), "{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}");
    }

    @Test
    public void testXsString1Exec() {
        executeTester("testXsString1", p.xs.string("abc"), "\"abc\"");
    }

    @Test
    public void testXsTime1Exp() {
        exportTester("testXsTime1", p.xs.time("10:09:08Z"), "{\"ns\":\"xs\", \"fn\":\"time\", \"args\":[\"10:09:08Z\"]}");
    }

    @Test
    public void testXsTime1Exec() {
        executeTester("testXsTime1", p.xs.time("10:09:08Z"), "xs:time(\"10:09:08Z\")");
    }

    @Test
    public void testXsToken1Exp() {
        exportTester("testXsToken1", p.xs.token(p.xs.string("a b c")), "{\"ns\":\"xs\", \"fn\":\"token\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a b c\"]}]}");
    }

    @Test
    public void testXsToken1Exec() {
        executeTester("testXsToken1", p.xs.token(p.xs.string("a b c")), "xs:token(\"a b c\")");
    }

    @Test
    public void testXsUnsignedByte1Exp() {
        exportTester("testXsUnsignedByte1", p.xs.unsignedByte((byte) 1), "{\"ns\":\"xs\", \"fn\":\"unsignedByte\", \"args\":[1]}");
    }

    @Test
    public void testXsUnsignedByte1Exec() {
        executeTester("testXsUnsignedByte1", p.xs.unsignedByte((byte) 1), "xs:unsignedByte(\"1\")");
    }

    @Test
    public void testXsUnsignedInt1Exp() {
        exportTester("testXsUnsignedInt1", p.xs.unsignedInt(1), "{\"ns\":\"xs\", \"fn\":\"unsignedInt\", \"args\":[1]}");
    }

    @Test
    public void testXsUnsignedInt1Exec() {
        executeTester("testXsUnsignedInt1", p.xs.unsignedInt(1), "xs:unsignedInt(\"1\")");
    }

    @Test
    public void testXsUnsignedLong1Exp() {
        exportTester("testXsUnsignedLong1", p.xs.unsignedLong(1), "{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[1]}");
    }

    @Test
    public void testXsUnsignedLong1Exec() {
        executeTester("testXsUnsignedLong1", p.xs.unsignedLong(1), "xs:unsignedLong(\"1\")");
    }

    @Test
    public void testXsUnsignedShort1Exp() {
        exportTester("testXsUnsignedShort1", p.xs.unsignedShort((short) 1), "{\"ns\":\"xs\", \"fn\":\"unsignedShort\", \"args\":[1]}");
    }

    @Test
    public void testXsUnsignedShort1Exec() {
        executeTester("testXsUnsignedShort1", p.xs.unsignedShort((short) 1), "xs:unsignedShort(\"1\")");
    }

    @Test
    public void testXsUntypedAtomic1Exp() {
        exportTester("testXsUntypedAtomic1", p.xs.untypedAtomic("abc"), "{\"ns\":\"xs\", \"fn\":\"untypedAtomic\", \"args\":[\"abc\"]}");
    }

    @Test
    public void testXsUntypedAtomic1Exec() {
        executeTester("testXsUntypedAtomic1", p.xs.untypedAtomic("abc"), "xs:untypedAtomic(\"abc\")");
    }

    @Test
    public void testXsYearMonthDuration1Exp() {
        exportTester("testXsYearMonthDuration1", p.xs.yearMonthDuration("P1Y2M"), "{\"ns\":\"xs\", \"fn\":\"yearMonthDuration\", \"args\":[\"P1Y2M\"]}");
    }

    @Test
    public void testXsYearMonthDuration1Exec() {
        executeTester("testXsYearMonthDuration1", p.xs.yearMonthDuration("P1Y2M"), "xs:yearMonthDuration(\"P1Y2M\")");
    }

    @Test
    public void testOpAdd2Exp() {
        exportTester("testOpAdd2", p.add(p.xs.intVal(1), p.xs.intVal(2)), "{\"ns\":\"op\", \"fn\":\"add\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}]}");
    }

    @Test
    public void testOpAdd2Exec() {
        executeTester("testOpAdd2", p.add(p.xs.intVal(1), p.xs.intVal(2)), "3");
    }

    @Test
    public void testOpAdd3Exp() {
        exportTester("testOpAdd3", p.add(p.xs.intVal(1), p.xs.intVal(2), p.xs.intVal(3)), "{\"ns\":\"op\", \"fn\":\"add\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"3\"]}]}");
    }

    @Test
    public void testOpAdd3Exec() {
        executeTester("testOpAdd3", p.add(p.xs.intVal(1), p.xs.intVal(2), p.xs.intVal(3)), "6");
    }

    @Test
    public void testOpAnd2Exp() {
        exportTester("testOpAnd2", p.and(p.xs.booleanVal(true), p.xs.booleanVal(true)), "{\"ns\":\"op\", \"fn\":\"and\", \"args\":[{\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"true\"]}, {\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"true\"]}]}");
    }

    @Test
    public void testOpAnd2Exec() {
        executeTester("testOpAnd2", p.and(p.xs.booleanVal(true), p.xs.booleanVal(true)), "fn:true()");
    }

    @Test
    public void testOpAnd3Exp() {
        exportTester("testOpAnd3", p.and(p.xs.booleanVal(true), p.xs.booleanVal(true), p.xs.booleanVal(true)), "{\"ns\":\"op\", \"fn\":\"and\", \"args\":[{\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"true\"]}, {\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"true\"]}, {\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"true\"]}]}");
    }

    @Test
    public void testOpAnd3Exec() {
        executeTester("testOpAnd3", p.and(p.xs.booleanVal(true), p.xs.booleanVal(true), p.xs.booleanVal(true)), "fn:true()");
    }

    @Test
    public void testOpDivide2Exp() {
        exportTester("testOpDivide2", p.divide(p.xs.intVal(6), p.xs.intVal(2)), "{\"ns\":\"op\", \"fn\":\"divide\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"6\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}]}");
    }

    @Test
    public void testOpDivide2Exec() {
        executeTester("testOpDivide2", p.divide(p.xs.intVal(6), p.xs.intVal(2)), "3");
    }

    @Test
    public void testOpEq2Exp() {
        exportTester("testOpEq2", p.eq(p.xs.intVal(1), p.xs.intVal(1)), "{\"ns\":\"op\", \"fn\":\"eq\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}]}");
    }

    @Test
    public void testOpEq2Exec() {
        executeTester("testOpEq2", p.eq(p.xs.intVal(1), p.xs.intVal(1)), "fn:true()");
    }

    @Test
    public void testOpGe2Exp() {
        exportTester("testOpGe2", p.ge(p.xs.intVal(1), p.xs.intVal(1)), "{\"ns\":\"op\", \"fn\":\"ge\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}]}");
    }

    @Test
    public void testOpGe2Exec() {
        executeTester("testOpGe2", p.ge(p.xs.intVal(1), p.xs.intVal(1)), "fn:true()");
    }

    @Test
    public void testOpGt2Exp() {
        exportTester("testOpGt2", p.gt(p.xs.intVal(2), p.xs.intVal(1)), "{\"ns\":\"op\", \"fn\":\"gt\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}]}");
    }

    @Test
    public void testOpGt2Exec() {
        executeTester("testOpGt2", p.gt(p.xs.intVal(2), p.xs.intVal(1)), "fn:true()");
    }

    @Test
    public void testOpLe2Exp() {
        exportTester("testOpLe2", p.le(p.xs.intVal(1), p.xs.intVal(1)), "{\"ns\":\"op\", \"fn\":\"le\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}]}");
    }

    @Test
    public void testOpLe2Exec() {
        executeTester("testOpLe2", p.le(p.xs.intVal(1), p.xs.intVal(1)), "fn:true()");
    }

    @Test
    public void testOpLt2Exp() {
        exportTester("testOpLt2", p.lt(p.xs.intVal(1), p.xs.intVal(2)), "{\"ns\":\"op\", \"fn\":\"lt\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}]}");
    }

    @Test
    public void testOpLt2Exec() {
        executeTester("testOpLt2", p.lt(p.xs.intVal(1), p.xs.intVal(2)), "fn:true()");
    }

    @Test
    public void testOpMultiply2Exp() {
        exportTester("testOpMultiply2", p.multiply(p.xs.intVal(2), p.xs.intVal(3)), "{\"ns\":\"op\", \"fn\":\"multiply\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"3\"]}]}");
    }

    @Test
    public void testOpMultiply2Exec() {
        executeTester("testOpMultiply2", p.multiply(p.xs.intVal(2), p.xs.intVal(3)), "6");
    }

    @Test
    public void testOpMultiply3Exp() {
        exportTester("testOpMultiply3", p.multiply(p.xs.intVal(2), p.xs.intVal(3), p.xs.intVal(4)), "{\"ns\":\"op\", \"fn\":\"multiply\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"3\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"4\"]}]}");
    }

    @Test
    public void testOpMultiply3Exec() {
        executeTester("testOpMultiply3", p.multiply(p.xs.intVal(2), p.xs.intVal(3), p.xs.intVal(4)), "24");
    }

    @Test
    public void testOpNe2Exp() {
        exportTester("testOpNe2", p.ne(p.xs.intVal(1), p.xs.intVal(2)), "{\"ns\":\"op\", \"fn\":\"ne\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}]}");
    }

    @Test
    public void testOpNe2Exec() {
        executeTester("testOpNe2", p.ne(p.xs.intVal(1), p.xs.intVal(2)), "fn:true()");
    }

    @Test
    public void testOpNot1Exp() {
        exportTester("testOpNot1", p.not(p.xs.booleanVal(false)), "{\"ns\":\"op\", \"fn\":\"not\", \"args\":[{\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"false\"]}]}");
    }

    @Test
    public void testOpNot1Exec() {
        executeTester("testOpNot1", p.not(p.xs.booleanVal(false)), "fn:true()");
    }

    @Test
    public void testOpOr2Exp() {
        exportTester("testOpOr2", p.or(p.xs.booleanVal(false), p.xs.booleanVal(true)), "{\"ns\":\"op\", \"fn\":\"or\", \"args\":[{\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"false\"]}, {\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"true\"]}]}");
    }

    @Test
    public void testOpOr2Exec() {
        executeTester("testOpOr2", p.or(p.xs.booleanVal(false), p.xs.booleanVal(true)), "fn:true()");
    }

    @Test
    public void testOpOr3Exp() {
        exportTester("testOpOr3", p.or(p.xs.booleanVal(false), p.xs.booleanVal(true), p.xs.booleanVal(false)), "{\"ns\":\"op\", \"fn\":\"or\", \"args\":[{\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"false\"]}, {\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"true\"]}, {\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"false\"]}]}");
    }

    @Test
    public void testOpOr3Exec() {
        executeTester("testOpOr3", p.or(p.xs.booleanVal(false), p.xs.booleanVal(true), p.xs.booleanVal(false)), "fn:true()");
    }

    @Test
    public void testOpSubtract2Exp() {
        exportTester("testOpSubtract2", p.subtract(p.xs.intVal(3), p.xs.intVal(2)), "{\"ns\":\"op\", \"fn\":\"subtract\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"3\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}]}");
    }

    @Test
    public void testOpSubtract2Exec() {
        executeTester("testOpSubtract2", p.subtract(p.xs.intVal(3), p.xs.intVal(2)), "1");
    }
}