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
    public void testCtsStem1() {
        exportTester("testCtsStem1", "{\"ns\":\"cts\", \"fn\":\"stem\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"ran\"]}]}", p.cts.stem(p.xs.string("ran")));
    }

    @Test
    public void testCtsStem2() {
        exportTester("testCtsStem2", "{\"ns\":\"cts\", \"fn\":\"stem\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"ran\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en\"]}]}", p.cts.stem(p.xs.string("ran"), p.xs.string("en")));
    }

    @Test
    public void testCtsTokenize1() {
        exportTester("testCtsTokenize1", "{\"ns\":\"cts\", \"fn\":\"tokenize\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a-b c\"]}]}", p.cts.tokenize(p.xs.string("a-b c")));
    }

    @Test
    public void testCtsTokenize2() {
        exportTester("testCtsTokenize2", "{\"ns\":\"cts\", \"fn\":\"tokenize\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a-b c\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en\"]}]}", p.cts.tokenize(p.xs.string("a-b c"), p.xs.string("en")));
    }

    @Test
    public void testFnAbs1() {
        exportTester("testFnAbs1", "{\"ns\":\"fn\", \"fn\":\"abs\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[-11]}]}", p.fn.abs(p.xs.doubleVal(-11)));
    }

    @Test
    public void testFnAdjustDateTimeToTimezone1() {
        exportTester("testFnAdjustDateTimeToTimezone1", "{\"ns\":\"fn\", \"fn\":\"adjust-dateTime-to-timezone\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}", p.fn.adjustDateTimeToTimezone(p.xs.dateTime("2016-01-02T10:09:08Z")));
    }

    @Test
    public void testFnAdjustDateTimeToTimezone2() {
        exportTester("testFnAdjustDateTimeToTimezone2", "{\"ns\":\"fn\", \"fn\":\"adjust-dateTime-to-timezone\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}, {\"ns\":\"xs\", \"fn\":\"dayTimeDuration\", \"args\":[\"-PT10H\"]}]}", p.fn.adjustDateTimeToTimezone(p.xs.dateTime("2016-01-02T10:09:08Z"), p.xs.dayTimeDuration("-PT10H")));
    }

    @Test
    public void testFnAdjustDateToTimezone1() {
        exportTester("testFnAdjustDateToTimezone1", "{\"ns\":\"fn\", \"fn\":\"adjust-date-to-timezone\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}]}", p.fn.adjustDateToTimezone(p.xs.date("2016-01-02")));
    }

    @Test
    public void testFnAdjustDateToTimezone2() {
        exportTester("testFnAdjustDateToTimezone2", "{\"ns\":\"fn\", \"fn\":\"adjust-date-to-timezone\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}, {\"ns\":\"xs\", \"fn\":\"dayTimeDuration\", \"args\":[\"-PT10H\"]}]}", p.fn.adjustDateToTimezone(p.xs.date("2016-01-02"), p.xs.dayTimeDuration("-PT10H")));
    }

    @Test
    public void testFnAdjustTimeToTimezone1() {
        exportTester("testFnAdjustTimeToTimezone1", "{\"ns\":\"fn\", \"fn\":\"adjust-time-to-timezone\", \"args\":[{\"ns\":\"xs\", \"fn\":\"time\", \"args\":[\"10:09:08Z\"]}]}", p.fn.adjustTimeToTimezone(p.xs.time("10:09:08Z")));
    }

    @Test
    public void testFnAdjustTimeToTimezone2() {
        exportTester("testFnAdjustTimeToTimezone2", "{\"ns\":\"fn\", \"fn\":\"adjust-time-to-timezone\", \"args\":[{\"ns\":\"xs\", \"fn\":\"time\", \"args\":[\"10:09:08Z\"]}, {\"ns\":\"xs\", \"fn\":\"dayTimeDuration\", \"args\":[\"-PT10H\"]}]}", p.fn.adjustTimeToTimezone(p.xs.time("10:09:08Z"), p.xs.dayTimeDuration("-PT10H")));
    }

    @Test
    public void testFnAnalyzeString2() {
        exportTester("testFnAnalyzeString2", "{\"ns\":\"fn\", \"fn\":\"analyze-string\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"aXbyc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"[xy]\"]}]}", p.fn.analyzeString(p.xs.string("aXbyc"), p.xs.string("[xy]")));
    }

    @Test
    public void testFnAnalyzeString3() {
        exportTester("testFnAnalyzeString3", "{\"ns\":\"fn\", \"fn\":\"analyze-string\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"aXbyc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"[xy]\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"i\"]}]}", p.fn.analyzeString(p.xs.string("aXbyc"), p.xs.string("[xy]"), p.xs.string("i")));
    }

    @Test
    public void testFnAvg1() {
        exportTester("testFnAvg1", "{\"ns\":\"fn\", \"fn\":\"avg\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[4]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[6]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[8]}]]}", p.fn.avg(p.xs.doubleVals(2, 4, 6, 8)));
    }

    @Test
    public void testFnBoolean1() {
        exportTester("testFnBoolean1", "{\"ns\":\"fn\", \"fn\":\"boolean\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}", p.fn.booleanExpr(p.xs.string("abc")));
    }

    @Test
    public void testFnCeiling1() {
        exportTester("testFnCeiling1", "{\"ns\":\"fn\", \"fn\":\"ceiling\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.3]}]}", p.fn.ceiling(p.xs.doubleVal(1.3)));
    }

    @Test
    public void testFnCodepointEqual2() {
        exportTester("testFnCodepointEqual2", "{\"ns\":\"fn\", \"fn\":\"codepoint-equal\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.fn.codepointEqual(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testFnCodepointsToString1() {
        exportTester("testFnCodepointsToString1", "{\"ns\":\"fn\", \"fn\":\"codepoints-to-string\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[97]}, {\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[98]}, {\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[99]}]]}", p.fn.codepointsToString(p.xs.integers(97, 98, 99)));
    }

    @Test
    public void testFnCompare2() {
        exportTester("testFnCompare2", "{\"ns\":\"fn\", \"fn\":\"compare\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abz\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"aba\"]}]}", p.fn.compare(p.xs.string("abz"), p.xs.string("aba")));
    }

    @Test
    public void testFnCompare3() {
        exportTester("testFnCompare3", "{\"ns\":\"fn\", \"fn\":\"compare\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abz\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"aba\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}", p.fn.compare(p.xs.string("abz"), p.xs.string("aba"), p.xs.string("http://marklogic.com/collation/")));
    }

    @Test
    public void testFnConcat2() {
        exportTester("testFnConcat2", "{\"ns\":\"fn\", \"fn\":\"concat\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}]}", p.fn.concat(p.xs.string("a"), p.xs.string("b")));
    }

    @Test
    public void testFnConcat3() {
        exportTester("testFnConcat3", "{\"ns\":\"fn\", \"fn\":\"concat\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]}", p.fn.concat(p.xs.string("a"), p.xs.string("b"), p.xs.string("c")));
    }

    @Test
    public void testFnContains2() {
        exportTester("testFnContains2", "{\"ns\":\"fn\", \"fn\":\"contains\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}]}", p.fn.contains(p.xs.string("abc"), p.xs.string("b")));
    }

    @Test
    public void testFnContains3() {
        exportTester("testFnContains3", "{\"ns\":\"fn\", \"fn\":\"contains\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}", p.fn.contains(p.xs.string("abc"), p.xs.string("b"), p.xs.string("http://marklogic.com/collation/")));
    }

    @Test
    public void testFnCount1() {
        exportTester("testFnCount1", "{\"ns\":\"fn\", \"fn\":\"count\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3]}]]}", p.fn.count(p.xs.doubleVals(1, 2, 3)));
    }

    @Test
    public void testFnCount2() {
        exportTester("testFnCount2", "{\"ns\":\"fn\", \"fn\":\"count\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3]}], {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[4]}]}", p.fn.count(p.xs.doubleVals(1, 2, 3), p.xs.doubleVal(4)));
    }

    @Test
    public void testFnCurrentDate0() {
        exportTester("testFnCurrentDate0", "{\"ns\":\"fn\", \"fn\":\"current-date\", \"args\":[]}", p.fn.currentDate());
    }

    @Test
    public void testFnCurrentDateTime0() {
        exportTester("testFnCurrentDateTime0", "{\"ns\":\"fn\", \"fn\":\"current-dateTime\", \"args\":[]}", p.fn.currentDateTime());
    }

    @Test
    public void testFnCurrentTime0() {
        exportTester("testFnCurrentTime0", "{\"ns\":\"fn\", \"fn\":\"current-time\", \"args\":[]}", p.fn.currentTime());
    }

    @Test
    public void testFnDayFromDate1() {
        exportTester("testFnDayFromDate1", "{\"ns\":\"fn\", \"fn\":\"day-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02-03:04\"]}]}", p.fn.dayFromDate(p.xs.date("2016-01-02-03:04")));
    }

    @Test
    public void testFnDayFromDateTime1() {
        exportTester("testFnDayFromDateTime1", "{\"ns\":\"fn\", \"fn\":\"day-from-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}", p.fn.dayFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")));
    }

    @Test
    public void testFnDaysFromDuration1() {
        exportTester("testFnDaysFromDuration1", "{\"ns\":\"fn\", \"fn\":\"days-from-duration\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dayTimeDuration\", \"args\":[\"P3DT4H5M6S\"]}]}", p.fn.daysFromDuration(p.xs.dayTimeDuration("P3DT4H5M6S")));
    }

    @Test
    public void testFnDeepEqual2() {
        exportTester("testFnDeepEqual2", "{\"ns\":\"fn\", \"fn\":\"deep-equal\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}", p.fn.deepEqual(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testFnDeepEqual3() {
        exportTester("testFnDeepEqual3", "{\"ns\":\"fn\", \"fn\":\"deep-equal\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.fn.deepEqual(p.xs.string("abc"), p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testFnDefaultCollation0() {
        exportTester("testFnDefaultCollation0", "{\"ns\":\"fn\", \"fn\":\"default-collation\", \"args\":[]}", p.fn.defaultCollation());
    }

    @Test
    public void testFnDistinctValues1() {
        exportTester("testFnDistinctValues1", "{\"ns\":\"fn\", \"fn\":\"distinct-values\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]]}", p.fn.distinctValues(p.xs.strings("a", "b", "b", "c")));
    }

    @Test
    public void testFnDistinctValues2() {
        exportTester("testFnDistinctValues2", "{\"ns\":\"fn\", \"fn\":\"distinct-values\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}", p.fn.distinctValues(p.xs.strings("a", "b", "b", "c"), p.xs.string("http://marklogic.com/collation/")));
    }

    @Test
    public void testFnEmpty1() {
        exportTester("testFnEmpty1", "{\"ns\":\"fn\", \"fn\":\"empty\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}]]}", p.fn.empty(p.xs.doubleVal(1)));
    }

    @Test
    public void testFnEncodeForUri1() {
        exportTester("testFnEncodeForUri1", "{\"ns\":\"fn\", \"fn\":\"encode-for-uri\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://a/b?c#d\"]}]}", p.fn.encodeForUri(p.xs.string("http://a/b?c#d")));
    }

    @Test
    public void testFnEndsWith2() {
        exportTester("testFnEndsWith2", "{\"ns\":\"fn\", \"fn\":\"ends-with\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]}", p.fn.endsWith(p.xs.string("abc"), p.xs.string("c")));
    }

    @Test
    public void testFnEndsWith3() {
        exportTester("testFnEndsWith3", "{\"ns\":\"fn\", \"fn\":\"ends-with\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}", p.fn.endsWith(p.xs.string("abc"), p.xs.string("c"), p.xs.string("http://marklogic.com/collation/")));
    }

    @Test
    public void testFnEscapeHtmlUri1() {
        exportTester("testFnEscapeHtmlUri1", "{\"ns\":\"fn\", \"fn\":\"escape-html-uri\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://a/b?c#d\"]}]}", p.fn.escapeHtmlUri(p.xs.string("http://a/b?c#d")));
    }

    @Test
    public void testFnExists1() {
        exportTester("testFnExists1", "{\"ns\":\"fn\", \"fn\":\"exists\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}]]}", p.fn.exists(p.xs.doubleVal(1)));
    }

    @Test
    public void testFnFalse0() {
        exportTester("testFnFalse0", "{\"ns\":\"fn\", \"fn\":\"false\", \"args\":[]}", p.fn.falseExpr());
    }

    @Test
    public void testFnFloor1() {
        exportTester("testFnFloor1", "{\"ns\":\"fn\", \"fn\":\"floor\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.7]}]}", p.fn.floor(p.xs.doubleVal(1.7)));
    }

    @Test
    public void testFnFormatDate2() {
        exportTester("testFnFormatDate2", "{\"ns\":\"fn\", \"fn\":\"format-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02-03:04\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"[Y0001]/[M01]/[D01]\"]}]}", p.fn.formatDate(p.xs.date("2016-01-02-03:04"), p.xs.string("[Y0001]/[M01]/[D01]")));
    }

    @Test
    public void testFnFormatDateTime2() {
        exportTester("testFnFormatDateTime2", "{\"ns\":\"fn\", \"fn\":\"format-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"[Y0001]/[M01]/[D01] [H01]:[m01]:[s01]:[f01]\"]}]}", p.fn.formatDateTime(p.xs.dateTime("2016-01-02T10:09:08Z"), p.xs.string("[Y0001]/[M01]/[D01] [H01]:[m01]:[s01]:[f01]")));
    }

    @Test
    public void testFnFormatNumber2() {
        exportTester("testFnFormatNumber2", "{\"ns\":\"fn\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1234.5]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"#,##0.00\"]}]}", p.fn.formatNumber(p.xs.doubleVal(1234.5), p.xs.string("#,##0.00")));
    }

    @Test
    public void testFnFormatTime2() {
        exportTester("testFnFormatTime2", "{\"ns\":\"fn\", \"fn\":\"format-time\", \"args\":[{\"ns\":\"xs\", \"fn\":\"time\", \"args\":[\"10:09:08Z\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"[H01]:[m01]:[s01]:[f01]\"]}]}", p.fn.formatTime(p.xs.time("10:09:08Z"), p.xs.string("[H01]:[m01]:[s01]:[f01]")));
    }

    @Test
    public void testFnHead1() {
        exportTester("testFnHead1", "{\"ns\":\"fn\", \"fn\":\"head\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]]}", p.fn.head(p.xs.strings("a", "b", "c")));
    }

    @Test
    public void testFnHoursFromDateTime1() {
        exportTester("testFnHoursFromDateTime1", "{\"ns\":\"fn\", \"fn\":\"hours-from-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}", p.fn.hoursFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")));
    }

    @Test
    public void testFnHoursFromDuration1() {
        exportTester("testFnHoursFromDuration1", "{\"ns\":\"fn\", \"fn\":\"hours-from-duration\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dayTimeDuration\", \"args\":[\"P3DT4H5M6S\"]}]}", p.fn.hoursFromDuration(p.xs.dayTimeDuration("P3DT4H5M6S")));
    }

    @Test
    public void testFnHoursFromTime1() {
        exportTester("testFnHoursFromTime1", "{\"ns\":\"fn\", \"fn\":\"hours-from-time\", \"args\":[{\"ns\":\"xs\", \"fn\":\"time\", \"args\":[\"10:09:08Z\"]}]}", p.fn.hoursFromTime(p.xs.time("10:09:08Z")));
    }

    @Test
    public void testFnImplicitTimezone0() {
        exportTester("testFnImplicitTimezone0", "{\"ns\":\"fn\", \"fn\":\"implicit-timezone\", \"args\":[]}", p.fn.implicitTimezone());
    }

    @Test
    public void testFnIndexOf2() {
        exportTester("testFnIndexOf2", "{\"ns\":\"fn\", \"fn\":\"index-of\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}]}", p.fn.indexOf(p.xs.strings("a", "b", "c"), p.xs.string("b")));
    }

    @Test
    public void testFnIndexOf3() {
        exportTester("testFnIndexOf3", "{\"ns\":\"fn\", \"fn\":\"index-of\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}", p.fn.indexOf(p.xs.strings("a", "b", "c"), p.xs.string("b"), p.xs.string("http://marklogic.com/collation/")));
    }

    @Test
    public void testFnInsertBefore3() {
        exportTester("testFnInsertBefore3", "{\"ns\":\"fn\", \"fn\":\"insert-before\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"e\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"f\"]}], {\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[3]}, [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"d\"]}]]}", p.fn.insertBefore(p.xs.strings("a", "b", "e", "f"), p.xs.integer(3), p.xs.strings("c", "d")));
    }

    @Test
    public void testFnIriToUri1() {
        exportTester("testFnIriToUri1", "{\"ns\":\"fn\", \"fn\":\"iri-to-uri\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://a/b?c#d\"]}]}", p.fn.iriToUri(p.xs.string("http://a/b?c#d")));
    }

    @Test
    public void testFnLocalNameFromQName1() {
        exportTester("testFnLocalNameFromQName1", "{\"ns\":\"fn\", \"fn\":\"local-name-from-QName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"QName\", \"args\":[\"abc\"]}]}", p.fn.localNameFromQName(p.xs.qname("abc")));
    }

    @Test
    public void testFnLowerCase1() {
        exportTester("testFnLowerCase1", "{\"ns\":\"fn\", \"fn\":\"lower-case\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"ABC\"]}]}", p.fn.lowerCase(p.xs.string("ABC")));
    }

    @Test
    public void testFnMatches2() {
        exportTester("testFnMatches2", "{\"ns\":\"fn\", \"fn\":\"matches\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"^.B\"]}]}", p.fn.matches(p.xs.string("abc"), p.xs.string("^.B")));
    }

    @Test
    public void testFnMatches3() {
        exportTester("testFnMatches3", "{\"ns\":\"fn\", \"fn\":\"matches\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"^.B\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"i\"]}]}", p.fn.matches(p.xs.string("abc"), p.xs.string("^.B"), p.xs.string("i")));
    }

    @Test
    public void testFnMax1() {
        exportTester("testFnMax1", "{\"ns\":\"fn\", \"fn\":\"max\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]]}", p.fn.max(p.xs.strings("a", "b", "c")));
    }

    @Test
    public void testFnMin1() {
        exportTester("testFnMin1", "{\"ns\":\"fn\", \"fn\":\"min\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]]}", p.fn.min(p.xs.strings("a", "b", "c")));
    }

    @Test
    public void testFnMinutesFromDateTime1() {
        exportTester("testFnMinutesFromDateTime1", "{\"ns\":\"fn\", \"fn\":\"minutes-from-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}", p.fn.minutesFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")));
    }

    @Test
    public void testFnMinutesFromDuration1() {
        exportTester("testFnMinutesFromDuration1", "{\"ns\":\"fn\", \"fn\":\"minutes-from-duration\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dayTimeDuration\", \"args\":[\"P3DT4H5M6S\"]}]}", p.fn.minutesFromDuration(p.xs.dayTimeDuration("P3DT4H5M6S")));
    }

    @Test
    public void testFnMinutesFromTime1() {
        exportTester("testFnMinutesFromTime1", "{\"ns\":\"fn\", \"fn\":\"minutes-from-time\", \"args\":[{\"ns\":\"xs\", \"fn\":\"time\", \"args\":[\"10:09:08Z\"]}]}", p.fn.minutesFromTime(p.xs.time("10:09:08Z")));
    }

    @Test
    public void testFnMonthFromDate1() {
        exportTester("testFnMonthFromDate1", "{\"ns\":\"fn\", \"fn\":\"month-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02-03:04\"]}]}", p.fn.monthFromDate(p.xs.date("2016-01-02-03:04")));
    }

    @Test
    public void testFnMonthFromDateTime1() {
        exportTester("testFnMonthFromDateTime1", "{\"ns\":\"fn\", \"fn\":\"month-from-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}", p.fn.monthFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")));
    }

    @Test
    public void testFnMonthsFromDuration1() {
        exportTester("testFnMonthsFromDuration1", "{\"ns\":\"fn\", \"fn\":\"months-from-duration\", \"args\":[{\"ns\":\"xs\", \"fn\":\"yearMonthDuration\", \"args\":[\"P1Y2M\"]}]}", p.fn.monthsFromDuration(p.xs.yearMonthDuration("P1Y2M")));
    }

    @Test
    public void testFnNamespaceUriFromQName1() {
        exportTester("testFnNamespaceUriFromQName1", "{\"ns\":\"fn\", \"fn\":\"namespace-uri-from-QName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"QName\", \"args\":[\"abc\"]}]}", p.fn.namespaceUriFromQName(p.xs.qname("abc")));
    }

    @Test
    public void testFnNormalizeSpace1() {
        exportTester("testFnNormalizeSpace1", "{\"ns\":\"fn\", \"fn\":\"normalize-space\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\" abc  123 \"]}]}", p.fn.normalizeSpace(p.xs.string(" abc  123 ")));
    }

    @Test
    public void testFnNormalizeUnicode1() {
        exportTester("testFnNormalizeUnicode1", "{\"ns\":\"fn\", \"fn\":\"normalize-unicode\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\" aBc \"]}]}", p.fn.normalizeUnicode(p.xs.string(" aBc ")));
    }

    @Test
    public void testFnNormalizeUnicode2() {
        exportTester("testFnNormalizeUnicode2", "{\"ns\":\"fn\", \"fn\":\"normalize-unicode\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\" aBc \"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"NFC\"]}]}", p.fn.normalizeUnicode(p.xs.string(" aBc "), p.xs.string("NFC")));
    }

    @Test
    public void testFnNot1() {
        exportTester("testFnNot1", "{\"ns\":\"fn\", \"fn\":\"not\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[true]}]]}", p.fn.not(p.xs.booleanVal(true)));
    }

    @Test
    public void testFnNumber1() {
        exportTester("testFnNumber1", "{\"ns\":\"fn\", \"fn\":\"number\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"1.1\"]}]}", p.fn.number(p.xs.string("1.1")));
    }

    @Test
    public void testFnPrefixFromQName1() {
        exportTester("testFnPrefixFromQName1", "{\"ns\":\"fn\", \"fn\":\"prefix-from-QName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"QName\", \"args\":[\"abc\"]}]}", p.fn.prefixFromQName(p.xs.qname("abc")));
    }

    @Test
    public void testFnQName2() {
        exportTester("testFnQName2", "{\"ns\":\"fn\", \"fn\":\"QName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://a/b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]}", p.fn.QName(p.xs.string("http://a/b"), p.xs.string("c")));
    }

    @Test
    public void testFnRemove2() {
        exportTester("testFnRemove2", "{\"ns\":\"fn\", \"fn\":\"remove\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"x\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}], {\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[3]}]}", p.fn.remove(p.xs.strings("a", "b", "x", "c"), p.xs.integer(3)));
    }

    @Test
    public void testFnReplace3() {
        exportTester("testFnReplace3", "{\"ns\":\"fn\", \"fn\":\"replace\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"axc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"^(.)X\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"$1b\"]}]}", p.fn.replace(p.xs.string("axc"), p.xs.string("^(.)X"), p.xs.string("$1b")));
    }

    @Test
    public void testFnReplace4() {
        exportTester("testFnReplace4", "{\"ns\":\"fn\", \"fn\":\"replace\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"axc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"^(.)X\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"$1b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"i\"]}]}", p.fn.replace(p.xs.string("axc"), p.xs.string("^(.)X"), p.xs.string("$1b"), p.xs.string("i")));
    }

    @Test
    public void testFnResolveUri2() {
        exportTester("testFnResolveUri2", "{\"ns\":\"fn\", \"fn\":\"resolve-uri\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b?c#d\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://a/x\"]}]}", p.fn.resolveUri(p.xs.string("b?c#d"), p.xs.string("http://a/x")));
    }

    @Test
    public void testFnReverse1() {
        exportTester("testFnReverse1", "{\"ns\":\"fn\", \"fn\":\"reverse\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}]]}", p.fn.reverse(p.xs.strings("c", "b", "a")));
    }

    @Test
    public void testFnRound1() {
        exportTester("testFnRound1", "{\"ns\":\"fn\", \"fn\":\"round\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.7]}]}", p.fn.round(p.xs.doubleVal(1.7)));
    }

    @Test
    public void testFnRoundHalfToEven1() {
        exportTester("testFnRoundHalfToEven1", "{\"ns\":\"fn\", \"fn\":\"round-half-to-even\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1234.5]}]}", p.fn.roundHalfToEven(p.xs.doubleVal(1234.5)));
    }

    @Test
    public void testFnRoundHalfToEven2() {
        exportTester("testFnRoundHalfToEven2", "{\"ns\":\"fn\", \"fn\":\"round-half-to-even\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1234.5]}, {\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[-2]}]}", p.fn.roundHalfToEven(p.xs.doubleVal(1234.5), p.xs.integer(-2)));
    }

    @Test
    public void testFnSecondsFromDateTime1() {
        exportTester("testFnSecondsFromDateTime1", "{\"ns\":\"fn\", \"fn\":\"seconds-from-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}", p.fn.secondsFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")));
    }

    @Test
    public void testFnSecondsFromDuration1() {
        exportTester("testFnSecondsFromDuration1", "{\"ns\":\"fn\", \"fn\":\"seconds-from-duration\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dayTimeDuration\", \"args\":[\"P3DT4H5M6S\"]}]}", p.fn.secondsFromDuration(p.xs.dayTimeDuration("P3DT4H5M6S")));
    }

    @Test
    public void testFnSecondsFromTime1() {
        exportTester("testFnSecondsFromTime1", "{\"ns\":\"fn\", \"fn\":\"seconds-from-time\", \"args\":[{\"ns\":\"xs\", \"fn\":\"time\", \"args\":[\"10:09:08Z\"]}]}", p.fn.secondsFromTime(p.xs.time("10:09:08Z")));
    }

    @Test
    public void testFnStartsWith2() {
        exportTester("testFnStartsWith2", "{\"ns\":\"fn\", \"fn\":\"starts-with\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}]}", p.fn.startsWith(p.xs.string("abc"), p.xs.string("a")));
    }

    @Test
    public void testFnStartsWith3() {
        exportTester("testFnStartsWith3", "{\"ns\":\"fn\", \"fn\":\"starts-with\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}", p.fn.startsWith(p.xs.string("abc"), p.xs.string("a"), p.xs.string("http://marklogic.com/collation/")));
    }

    @Test
    public void testFnString1() {
        exportTester("testFnString1", "{\"ns\":\"fn\", \"fn\":\"string\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}]}", p.fn.string(p.xs.doubleVal(1)));
    }

    @Test
    public void testFnStringJoin1() {
        exportTester("testFnStringJoin1", "{\"ns\":\"fn\", \"fn\":\"string-join\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]]}", p.fn.stringJoin(p.xs.strings("a", "b", "c")));
    }

    @Test
    public void testFnStringJoin2() {
        exportTester("testFnStringJoin2", "{\"ns\":\"fn\", \"fn\":\"string-join\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"+\"]}]}", p.fn.stringJoin(p.xs.strings("a", "b", "c"), p.xs.string("+")));
    }

    @Test
    public void testFnStringLength1() {
        exportTester("testFnStringLength1", "{\"ns\":\"fn\", \"fn\":\"string-length\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.fn.stringLength(p.xs.string("abc")));
    }

    @Test
    public void testFnStringToCodepoints1() {
        exportTester("testFnStringToCodepoints1", "{\"ns\":\"fn\", \"fn\":\"string-to-codepoints\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.fn.stringToCodepoints(p.xs.string("abc")));
    }

    @Test
    public void testFnSubsequence2() {
        exportTester("testFnSubsequence2", "{\"ns\":\"fn\", \"fn\":\"subsequence\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"d\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"e\"]}], {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}]}", p.fn.subsequence(p.xs.strings("a", "b", "c", "d", "e"), p.xs.doubleVal(2)));
    }

    @Test
    public void testFnSubsequence3() {
        exportTester("testFnSubsequence3", "{\"ns\":\"fn\", \"fn\":\"subsequence\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"d\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"e\"]}], {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3]}]}", p.fn.subsequence(p.xs.strings("a", "b", "c", "d", "e"), p.xs.doubleVal(2), p.xs.doubleVal(3)));
    }

    @Test
    public void testFnSubstring2() {
        exportTester("testFnSubstring2", "{\"ns\":\"fn\", \"fn\":\"substring\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcd\"]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}]}", p.fn.substring(p.xs.string("abcd"), p.xs.doubleVal(2)));
    }

    @Test
    public void testFnSubstring3() {
        exportTester("testFnSubstring3", "{\"ns\":\"fn\", \"fn\":\"substring\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcd\"]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}]}", p.fn.substring(p.xs.string("abcd"), p.xs.doubleVal(2), p.xs.doubleVal(2)));
    }

    @Test
    public void testFnSubstringAfter2() {
        exportTester("testFnSubstringAfter2", "{\"ns\":\"fn\", \"fn\":\"substring-after\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcd\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"ab\"]}]}", p.fn.substringAfter(p.xs.string("abcd"), p.xs.string("ab")));
    }

    @Test
    public void testFnSubstringAfter3() {
        exportTester("testFnSubstringAfter3", "{\"ns\":\"fn\", \"fn\":\"substring-after\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcd\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"ab\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}", p.fn.substringAfter(p.xs.string("abcd"), p.xs.string("ab"), p.xs.string("http://marklogic.com/collation/")));
    }

    @Test
    public void testFnSubstringBefore2() {
        exportTester("testFnSubstringBefore2", "{\"ns\":\"fn\", \"fn\":\"substring-before\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcd\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"cd\"]}]}", p.fn.substringBefore(p.xs.string("abcd"), p.xs.string("cd")));
    }

    @Test
    public void testFnSubstringBefore3() {
        exportTester("testFnSubstringBefore3", "{\"ns\":\"fn\", \"fn\":\"substring-before\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcd\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"cd\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}", p.fn.substringBefore(p.xs.string("abcd"), p.xs.string("cd"), p.xs.string("http://marklogic.com/collation/")));
    }

    @Test
    public void testFnSum1() {
        exportTester("testFnSum1", "{\"ns\":\"fn\", \"fn\":\"sum\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3]}]]}", p.fn.sum(p.xs.doubleVals(1, 2, 3)));
    }

    @Test
    public void testFnTail1() {
        exportTester("testFnTail1", "{\"ns\":\"fn\", \"fn\":\"tail\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]]}", p.fn.tail(p.xs.strings("a", "b", "c")));
    }

    @Test
    public void testFnTimezoneFromDate1() {
        exportTester("testFnTimezoneFromDate1", "{\"ns\":\"fn\", \"fn\":\"timezone-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02-03:04\"]}]}", p.fn.timezoneFromDate(p.xs.date("2016-01-02-03:04")));
    }

    @Test
    public void testFnTimezoneFromDateTime1() {
        exportTester("testFnTimezoneFromDateTime1", "{\"ns\":\"fn\", \"fn\":\"timezone-from-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}", p.fn.timezoneFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")));
    }

    @Test
    public void testFnTimezoneFromTime1() {
        exportTester("testFnTimezoneFromTime1", "{\"ns\":\"fn\", \"fn\":\"timezone-from-time\", \"args\":[{\"ns\":\"xs\", \"fn\":\"time\", \"args\":[\"10:09:08Z\"]}]}", p.fn.timezoneFromTime(p.xs.time("10:09:08Z")));
    }

    @Test
    public void testFnTokenize2() {
        exportTester("testFnTokenize2", "{\"ns\":\"fn\", \"fn\":\"tokenize\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"axbxc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"X\"]}]}", p.fn.tokenize(p.xs.string("axbxc"), p.xs.string("X")));
    }

    @Test
    public void testFnTokenize3() {
        exportTester("testFnTokenize3", "{\"ns\":\"fn\", \"fn\":\"tokenize\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"axbxc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"X\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"i\"]}]}", p.fn.tokenize(p.xs.string("axbxc"), p.xs.string("X"), p.xs.string("i")));
    }

    @Test
    public void testFnTranslate3() {
        exportTester("testFnTranslate3", "{\"ns\":\"fn\", \"fn\":\"translate\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"axcy\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"xy\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"bd\"]}]}", p.fn.translate(p.xs.string("axcy"), p.xs.string("xy"), p.xs.string("bd")));
    }

    @Test
    public void testFnTrue0() {
        exportTester("testFnTrue0", "{\"ns\":\"fn\", \"fn\":\"true\", \"args\":[]}", p.fn.trueExpr());
    }

    @Test
    public void testFnUnordered1() {
        exportTester("testFnUnordered1", "{\"ns\":\"fn\", \"fn\":\"unordered\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}", p.fn.unordered(p.xs.string("abc")));
    }

    @Test
    public void testFnUpperCase1() {
        exportTester("testFnUpperCase1", "{\"ns\":\"fn\", \"fn\":\"upper-case\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.fn.upperCase(p.xs.string("abc")));
    }

    @Test
    public void testFnYearFromDate1() {
        exportTester("testFnYearFromDate1", "{\"ns\":\"fn\", \"fn\":\"year-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02-03:04\"]}]}", p.fn.yearFromDate(p.xs.date("2016-01-02-03:04")));
    }

    @Test
    public void testFnYearFromDateTime1() {
        exportTester("testFnYearFromDateTime1", "{\"ns\":\"fn\", \"fn\":\"year-from-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}", p.fn.yearFromDateTime(p.xs.dateTime("2016-01-02T10:09:08Z")));
    }

    @Test
    public void testFnYearsFromDuration1() {
        exportTester("testFnYearsFromDuration1", "{\"ns\":\"fn\", \"fn\":\"years-from-duration\", \"args\":[{\"ns\":\"xs\", \"fn\":\"yearMonthDuration\", \"args\":[\"P1Y2M\"]}]}", p.fn.yearsFromDuration(p.xs.yearMonthDuration("P1Y2M")));
    }

    @Test
    public void testJsonToArray0() {
        exportTester("testJsonToArray0", "{\"ns\":\"json\", \"fn\":\"to-array\", \"args\":[]}", p.json.toArray());
    }

    @Test
    public void testJsonToArray1() {
        exportTester("testJsonToArray1", "{\"ns\":\"json\", \"fn\":\"to-array\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}", p.json.toArray(p.xs.string("abc")));
    }

    @Test
    public void testJsonToArray2() {
        exportTester("testJsonToArray2", "{\"ns\":\"json\", \"fn\":\"to-array\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]}", p.json.toArray(p.xs.string("abc"), p.xs.doubleVal(1.2)));
    }

    @Test
    public void testJsonToArray3() {
        exportTester("testJsonToArray3", "{\"ns\":\"json\", \"fn\":\"to-array\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.json.toArray(p.xs.string("abc"), p.xs.doubleVal(1.2), p.xs.string("abc")));
    }

    @Test
    public void testMathAcos1() {
        exportTester("testMathAcos1", "{\"ns\":\"math\", \"fn\":\"acos\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[0.5]}]}", p.math.acos(p.xs.doubleVal(0.5)));
    }

    @Test
    public void testMathAsin1() {
        exportTester("testMathAsin1", "{\"ns\":\"math\", \"fn\":\"asin\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[0.5]}]}", p.math.asin(p.xs.doubleVal(0.5)));
    }

    @Test
    public void testMathAtan1() {
        exportTester("testMathAtan1", "{\"ns\":\"math\", \"fn\":\"atan\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3.14159]}]}", p.math.atan(p.xs.doubleVal(3.14159)));
    }

    @Test
    public void testMathAtan22() {
        exportTester("testMathAtan22", "{\"ns\":\"math\", \"fn\":\"atan2\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[36.23]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[5.234]}]}", p.math.atan2(p.xs.doubleVal(36.23), p.xs.doubleVal(5.234)));
    }

    @Test
    public void testMathCeil1() {
        exportTester("testMathCeil1", "{\"ns\":\"math\", \"fn\":\"ceil\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.3]}]}", p.math.ceil(p.xs.doubleVal(1.3)));
    }

    @Test
    public void testMathCos1() {
        exportTester("testMathCos1", "{\"ns\":\"math\", \"fn\":\"cos\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[11]}]}", p.math.cos(p.xs.doubleVal(11)));
    }

    @Test
    public void testMathCosh1() {
        exportTester("testMathCosh1", "{\"ns\":\"math\", \"fn\":\"cosh\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[11]}]}", p.math.cosh(p.xs.doubleVal(11)));
    }

    @Test
    public void testMathCot1() {
        exportTester("testMathCot1", "{\"ns\":\"math\", \"fn\":\"cot\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[19.5]}]}", p.math.cot(p.xs.doubleVal(19.5)));
    }

    @Test
    public void testMathDegrees1() {
        exportTester("testMathDegrees1", "{\"ns\":\"math\", \"fn\":\"degrees\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.5707963267949]}]}", p.math.degrees(p.xs.doubleVal(1.5707963267949)));
    }

    @Test
    public void testMathExp1() {
        exportTester("testMathExp1", "{\"ns\":\"math\", \"fn\":\"exp\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[0.1]}]}", p.math.exp(p.xs.doubleVal(0.1)));
    }

    @Test
    public void testMathFabs1() {
        exportTester("testMathFabs1", "{\"ns\":\"math\", \"fn\":\"fabs\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[4.013]}]}", p.math.fabs(p.xs.doubleVal(4.013)));
    }

    @Test
    public void testMathFloor1() {
        exportTester("testMathFloor1", "{\"ns\":\"math\", \"fn\":\"floor\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.7]}]}", p.math.floor(p.xs.doubleVal(1.7)));
    }

    @Test
    public void testMathFmod2() {
        exportTester("testMathFmod2", "{\"ns\":\"math\", \"fn\":\"fmod\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[10]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3]}]}", p.math.fmod(p.xs.doubleVal(10), p.xs.doubleVal(3)));
    }

    @Test
    public void testMathFrexp1() {
        exportTester("testMathFrexp1", "{\"ns\":\"math\", \"fn\":\"frexp\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[10]}]}", p.math.frexp(p.xs.doubleVal(10)));
    }

    @Test
    public void testMathLdexp2() {
        exportTester("testMathLdexp2", "{\"ns\":\"math\", \"fn\":\"ldexp\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.333]}, {\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[10]}]}", p.math.ldexp(p.xs.doubleVal(1.333), p.xs.integer(10)));
    }

    @Test
    public void testMathLog1() {
        exportTester("testMathLog1", "{\"ns\":\"math\", \"fn\":\"log\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1000]}]}", p.math.log(p.xs.doubleVal(1000)));
    }

    @Test
    public void testMathLog101() {
        exportTester("testMathLog101", "{\"ns\":\"math\", \"fn\":\"log10\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1000]}]}", p.math.log10(p.xs.doubleVal(1000)));
    }

    @Test
    public void testMathMedian1() {
        exportTester("testMathMedian1", "{\"ns\":\"math\", \"fn\":\"median\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]]}", p.math.median(p.xs.doubleVal(1.2)));
    }

    @Test
    public void testMathMode1() {
        exportTester("testMathMode1", "{\"ns\":\"math\", \"fn\":\"mode\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}", p.math.mode(p.xs.string("abc")));
    }

    @Test
    public void testMathMode2() {
        exportTester("testMathMode2", "{\"ns\":\"math\", \"fn\":\"mode\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}", p.math.mode(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testMathModf1() {
        exportTester("testMathModf1", "{\"ns\":\"math\", \"fn\":\"modf\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.333]}]}", p.math.modf(p.xs.doubleVal(1.333)));
    }

    @Test
    public void testMathPercentile2() {
        exportTester("testMathPercentile2", "{\"ns\":\"math\", \"fn\":\"percentile\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}], [{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]]}", p.math.percentile(p.xs.doubleVal(1.2), p.xs.doubleVal(1.2)));
    }

    @Test
    public void testMathPercentRank2() {
        exportTester("testMathPercentRank2", "{\"ns\":\"math\", \"fn\":\"percent-rank\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.math.percentRank(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testMathPercentRank3() {
        exportTester("testMathPercentRank3", "{\"ns\":\"math\", \"fn\":\"percent-rank\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}", p.math.percentRank(p.xs.string("abc"), p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testMathPi0() {
        exportTester("testMathPi0", "{\"ns\":\"math\", \"fn\":\"pi\", \"args\":[]}", p.math.pi());
    }

    @Test
    public void testMathPow2() {
        exportTester("testMathPow2", "{\"ns\":\"math\", \"fn\":\"pow\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[10]}]}", p.math.pow(p.xs.doubleVal(2), p.xs.doubleVal(10)));
    }

    @Test
    public void testMathRadians1() {
        exportTester("testMathRadians1", "{\"ns\":\"math\", \"fn\":\"radians\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[90]}]}", p.math.radians(p.xs.doubleVal(90)));
    }

    @Test
    public void testMathRank2() {
        exportTester("testMathRank2", "{\"ns\":\"math\", \"fn\":\"rank\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.math.rank(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testMathRank3() {
        exportTester("testMathRank3", "{\"ns\":\"math\", \"fn\":\"rank\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}", p.math.rank(p.xs.string("abc"), p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testMathSin1() {
        exportTester("testMathSin1", "{\"ns\":\"math\", \"fn\":\"sin\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.95]}]}", p.math.sin(p.xs.doubleVal(1.95)));
    }

    @Test
    public void testMathSinh1() {
        exportTester("testMathSinh1", "{\"ns\":\"math\", \"fn\":\"sinh\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.95]}]}", p.math.sinh(p.xs.doubleVal(1.95)));
    }

    @Test
    public void testMathSqrt1() {
        exportTester("testMathSqrt1", "{\"ns\":\"math\", \"fn\":\"sqrt\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[4]}]}", p.math.sqrt(p.xs.doubleVal(4)));
    }

    @Test
    public void testMathStddev1() {
        exportTester("testMathStddev1", "{\"ns\":\"math\", \"fn\":\"stddev\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]]}", p.math.stddev(p.xs.doubleVal(1.2)));
    }

    @Test
    public void testMathStddevP1() {
        exportTester("testMathStddevP1", "{\"ns\":\"math\", \"fn\":\"stddev-p\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]]}", p.math.stddevP(p.xs.doubleVal(1.2)));
    }

    @Test
    public void testMathTan1() {
        exportTester("testMathTan1", "{\"ns\":\"math\", \"fn\":\"tan\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[19.5]}]}", p.math.tan(p.xs.doubleVal(19.5)));
    }

    @Test
    public void testMathTanh1() {
        exportTester("testMathTanh1", "{\"ns\":\"math\", \"fn\":\"tanh\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[0.95]}]}", p.math.tanh(p.xs.doubleVal(0.95)));
    }

    @Test
    public void testMathTrunc1() {
        exportTester("testMathTrunc1", "{\"ns\":\"math\", \"fn\":\"trunc\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[123.456]}]}", p.math.trunc(p.xs.doubleVal(123.456)));
    }

    @Test
    public void testMathTrunc2() {
        exportTester("testMathTrunc2", "{\"ns\":\"math\", \"fn\":\"trunc\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[123.456]}, {\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[2]}]}", p.math.trunc(p.xs.doubleVal(123.456), p.xs.integer(2)));
    }

    @Test
    public void testMathVariance1() {
        exportTester("testMathVariance1", "{\"ns\":\"math\", \"fn\":\"variance\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]]}", p.math.variance(p.xs.doubleVal(1.2)));
    }

    @Test
    public void testMathVarianceP1() {
        exportTester("testMathVarianceP1", "{\"ns\":\"math\", \"fn\":\"variance-p\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]]}", p.math.varianceP(p.xs.doubleVal(1.2)));
    }

    @Test
    public void testRdfLangString2() {
        exportTester("testRdfLangString2", "{\"ns\":\"rdf\", \"fn\":\"langString\", \"args\":[\"abc\", \"en\"]}", p.rdf.langString("abc", "en"));
    }

    @Test
    public void testRdfLangStringLanguage1() {
        exportTester("testRdfLangStringLanguage1", "{\"ns\":\"rdf\", \"fn\":\"langString-language\", \"args\":[{\"ns\":\"rdf\", \"fn\":\"langString\", \"args\":[\"abc\", \"en\"]}]}", p.rdf.langStringLanguage(p.rdf.langString("abc", "en")));
    }

    @Test
    public void testSemBnode0() {
        exportTester("testSemBnode0", "{\"ns\":\"sem\", \"fn\":\"bnode\", \"args\":[]}", p.sem.bnode());
    }

    @Test
    public void testSemBnode1() {
        exportTester("testSemBnode1", "{\"ns\":\"sem\", \"fn\":\"bnode\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.sem.bnode(p.xs.string("abc")));
    }

    @Test
    public void testSemCoalesce2() {
        exportTester("testSemCoalesce2", "{\"ns\":\"sem\", \"fn\":\"coalesce\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}], [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}]]}", p.sem.coalesce(p.xs.string("a"), p.xs.string("b")));
    }

    @Test
    public void testSemCoalesce3() {
        exportTester("testSemCoalesce3", "{\"ns\":\"sem\", \"fn\":\"coalesce\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}], [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}], [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c\"]}]]}", p.sem.coalesce(p.xs.string("a"), p.xs.string("b"), p.xs.string("c")));
    }

    @Test
    public void testSemDatatype1() {
        exportTester("testSemDatatype1", "{\"ns\":\"sem\", \"fn\":\"datatype\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}]}", p.sem.datatype(p.xs.string("a")));
    }

    @Test
    public void testSemDefaultGraphIri0() {
        exportTester("testSemDefaultGraphIri0", "{\"ns\":\"sem\", \"fn\":\"default-graph-iri\", \"args\":[]}", p.sem.defaultGraphIri());
    }

    @Test
    public void testSemIf3() {
        exportTester("testSemIf3", "{\"ns\":\"sem\", \"fn\":\"if\", \"args\":[{\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[true]}, [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}], [{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b\"]}]]}", p.sem.ifExpr(p.xs.booleanVal(true), p.xs.string("a"), p.xs.string("b")));
    }

    @Test
    public void testSemInvalid2() {
        exportTester("testSemInvalid2", "{\"ns\":\"sem\", \"fn\":\"invalid\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"sem\", \"fn\":\"iri\", \"args\":[\"http://a/b\"]}]}", p.sem.invalid(p.xs.string("abc"), p.sem.iri("http://a/b")));
    }

    @Test
    public void testSemIri1() {
        exportTester("testSemIri1", "{\"ns\":\"sem\", \"fn\":\"iri\", \"args\":[\"http://a/b\"]}", p.sem.iri("http://a/b"));
    }

    @Test
    public void testSemIriToQName1() {
        exportTester("testSemIriToQName1", "{\"ns\":\"sem\", \"fn\":\"iri-to-QName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.sem.iriToQName(p.xs.string("abc")));
    }

    @Test
    public void testSemIsBlank1() {
        exportTester("testSemIsBlank1", "{\"ns\":\"sem\", \"fn\":\"isBlank\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}]}", p.sem.isBlank(p.xs.doubleVal(1)));
    }

    @Test
    public void testSemIsIRI1() {
        exportTester("testSemIsIRI1", "{\"ns\":\"sem\", \"fn\":\"isIRI\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}]}", p.sem.isIRI(p.xs.doubleVal(1)));
    }

    @Test
    public void testSemIsLiteral1() {
        exportTester("testSemIsLiteral1", "{\"ns\":\"sem\", \"fn\":\"isLiteral\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}]}", p.sem.isLiteral(p.xs.doubleVal(1)));
    }

    @Test
    public void testSemIsNumeric1() {
        exportTester("testSemIsNumeric1", "{\"ns\":\"sem\", \"fn\":\"isNumeric\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}]}", p.sem.isNumeric(p.xs.string("a")));
    }

    @Test
    public void testSemLang1() {
        exportTester("testSemLang1", "{\"ns\":\"sem\", \"fn\":\"lang\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.sem.lang(p.xs.string("abc")));
    }

    @Test
    public void testSemLangMatches2() {
        exportTester("testSemLangMatches2", "{\"ns\":\"sem\", \"fn\":\"langMatches\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.sem.langMatches(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testSemQNameToIri1() {
        exportTester("testSemQNameToIri1", "{\"ns\":\"sem\", \"fn\":\"QName-to-iri\", \"args\":[{\"ns\":\"xs\", \"fn\":\"QName\", \"args\":[\"abc\"]}]}", p.sem.QNameToIri(p.xs.qname("abc")));
    }

    @Test
    public void testSemRandom0() {
        exportTester("testSemRandom0", "{\"ns\":\"sem\", \"fn\":\"random\", \"args\":[]}", p.sem.random());
    }

    @Test
    public void testSemSameTerm2() {
        exportTester("testSemSameTerm2", "{\"ns\":\"sem\", \"fn\":\"sameTerm\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}]}", p.sem.sameTerm(p.xs.doubleVal(1), p.xs.doubleVal(1)));
    }

    @Test
    public void testSemTimezoneString1() {
        exportTester("testSemTimezoneString1", "{\"ns\":\"sem\", \"fn\":\"timezone-string\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}]}", p.sem.timezoneString(p.xs.dateTime("2016-01-02T10:09:08Z")));
    }

    @Test
    public void testSemTypedLiteral2() {
        exportTester("testSemTypedLiteral2", "{\"ns\":\"sem\", \"fn\":\"typed-literal\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"sem\", \"fn\":\"iri\", \"args\":[\"http://a/b\"]}]}", p.sem.typedLiteral(p.xs.string("abc"), p.sem.iri("http://a/b")));
    }

    @Test
    public void testSemUnknown2() {
        exportTester("testSemUnknown2", "{\"ns\":\"sem\", \"fn\":\"unknown\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"sem\", \"fn\":\"iri\", \"args\":[\"http://a/b\"]}]}", p.sem.unknown(p.xs.string("abc"), p.sem.iri("http://a/b")));
    }

    @Test
    public void testSemUuid0() {
        exportTester("testSemUuid0", "{\"ns\":\"sem\", \"fn\":\"uuid\", \"args\":[]}", p.sem.uuid());
    }

    @Test
    public void testSemUuidString0() {
        exportTester("testSemUuidString0", "{\"ns\":\"sem\", \"fn\":\"uuid-string\", \"args\":[]}", p.sem.uuidString());
    }

    @Test
    public void testSpellDoubleMetaphone1() {
        exportTester("testSpellDoubleMetaphone1", "{\"ns\":\"spell\", \"fn\":\"double-metaphone\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"smith\"]}]}", p.spell.doubleMetaphone(p.xs.string("smith")));
    }

    @Test
    public void testSpellLevenshteinDistance2() {
        exportTester("testSpellLevenshteinDistance2", "{\"ns\":\"spell\", \"fn\":\"levenshtein-distance\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"cat\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"cats\"]}]}", p.spell.levenshteinDistance(p.xs.string("cat"), p.xs.string("cats")));
    }

    @Test
    public void testSpellRomanize1() {
        exportTester("testSpellRomanize1", "{\"ns\":\"spell\", \"fn\":\"romanize\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.spell.romanize(p.xs.string("abc")));
    }

    @Test
    public void testSqlBitLength0() {
        exportTester("testSqlBitLength0", "{\"ns\":\"sql\", \"fn\":\"bit-length\", \"args\":[]}", p.sql.bitLength());
    }

    @Test
    public void testSqlBitLength1() {
        exportTester("testSqlBitLength1", "{\"ns\":\"sql\", \"fn\":\"bit-length\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.sql.bitLength(p.xs.string("abc")));
    }

    @Test
    public void testSqlInsert4() {
        exportTester("testSqlInsert4", "{\"ns\":\"sql\", \"fn\":\"insert\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"axxxf\"]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"bcde\"]}]}", p.sql.insert(p.xs.string("axxxf"), p.xs.doubleVal(2), p.xs.doubleVal(3), p.xs.string("bcde")));
    }

    @Test
    public void testSqlInstr2() {
        exportTester("testSqlInstr2", "{\"ns\":\"sql\", \"fn\":\"instr\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcde\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"cd\"]}]}", p.sql.instr(p.xs.string("abcde"), p.xs.string("cd")));
    }

    @Test
    public void testSqlLeft2() {
        exportTester("testSqlLeft2", "{\"ns\":\"sql\", \"fn\":\"left\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcde\"]}], {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3]}]}", p.sql.left(p.xs.string("abcde"), p.xs.doubleVal(3)));
    }

    @Test
    public void testSqlLtrim1() {
        exportTester("testSqlLtrim1", "{\"ns\":\"sql\", \"fn\":\"ltrim\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.sql.ltrim(p.xs.string("abc")));
    }

    @Test
    public void testSqlLtrim2() {
        exportTester("testSqlLtrim2", "{\"ns\":\"sql\", \"fn\":\"ltrim\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.sql.ltrim(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testSqlOctetLength0() {
        exportTester("testSqlOctetLength0", "{\"ns\":\"sql\", \"fn\":\"octet-length\", \"args\":[]}", p.sql.octetLength());
    }

    @Test
    public void testSqlOctetLength1() {
        exportTester("testSqlOctetLength1", "{\"ns\":\"sql\", \"fn\":\"octet-length\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.sql.octetLength(p.xs.string("abc")));
    }

    @Test
    public void testSqlRand0() {
        exportTester("testSqlRand0", "{\"ns\":\"sql\", \"fn\":\"rand\", \"args\":[]}", p.sql.rand());
    }

    @Test
    public void testSqlRand1() {
        exportTester("testSqlRand1", "{\"ns\":\"sql\", \"fn\":\"rand\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[1]}]}", p.sql.rand(p.xs.unsignedLong(1)));
    }

    @Test
    public void testSqlRepeat2() {
        exportTester("testSqlRepeat2", "{\"ns\":\"sql\", \"fn\":\"repeat\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}], {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[2]}]}", p.sql.repeat(p.xs.string("abc"), p.xs.doubleVal(2)));
    }

    @Test
    public void testSqlRight2() {
        exportTester("testSqlRight2", "{\"ns\":\"sql\", \"fn\":\"right\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcde\"]}], {\"ns\":\"xs\", \"fn\":\"double\", \"args\":[3]}]}", p.sql.right(p.xs.string("abcde"), p.xs.doubleVal(3)));
    }

    @Test
    public void testSqlRtrim1() {
        exportTester("testSqlRtrim1", "{\"ns\":\"sql\", \"fn\":\"rtrim\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.sql.rtrim(p.xs.string("abc")));
    }

    @Test
    public void testSqlRtrim2() {
        exportTester("testSqlRtrim2", "{\"ns\":\"sql\", \"fn\":\"rtrim\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.sql.rtrim(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testSqlSign1() {
        exportTester("testSqlSign1", "{\"ns\":\"sql\", \"fn\":\"sign\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[-3]}]}", p.sql.sign(p.xs.doubleVal(-3)));
    }

    @Test
    public void testSqlSpace1() {
        exportTester("testSqlSpace1", "{\"ns\":\"sql\", \"fn\":\"space\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]}", p.sql.space(p.xs.doubleVal(1.2)));
    }

    @Test
    public void testSqlTrim1() {
        exportTester("testSqlTrim1", "{\"ns\":\"sql\", \"fn\":\"trim\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.sql.trim(p.xs.string("abc")));
    }

    @Test
    public void testSqlTrim2() {
        exportTester("testSqlTrim2", "{\"ns\":\"sql\", \"fn\":\"trim\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.sql.trim(p.xs.string("abc"), p.xs.string("abc")));
    }

    @Test
    public void testXdmpAdd642() {
        exportTester("testXdmpAdd642", "{\"ns\":\"xdmp\", \"fn\":\"add64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[123]}, {\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[456]}]}", p.xdmp.add64(p.xs.unsignedLong(123), p.xs.unsignedLong(456)));
    }

    @Test
    public void testXdmpAnd642() {
        exportTester("testXdmpAnd642", "{\"ns\":\"xdmp\", \"fn\":\"and64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[255]}, {\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[2]}]}", p.xdmp.and64(p.xs.unsignedLong(255), p.xs.unsignedLong(2)));
    }

    @Test
    public void testXdmpBase64Decode1() {
        exportTester("testXdmpBase64Decode1", "{\"ns\":\"xdmp\", \"fn\":\"base64-decode\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"c2xpbmdzIGFuZCBhcnJvd3Mgb2Ygb3V0cmFnZW91cyBmb3J0dW5l\"]}]}", p.xdmp.base64Decode(p.xs.string("c2xpbmdzIGFuZCBhcnJvd3Mgb2Ygb3V0cmFnZW91cyBmb3J0dW5l")));
    }

    @Test
    public void testXdmpBase64Encode1() {
        exportTester("testXdmpBase64Encode1", "{\"ns\":\"xdmp\", \"fn\":\"base64-encode\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"slings and arrows of outrageous fortune\"]}]}", p.xdmp.base64Encode(p.xs.string("slings and arrows of outrageous fortune")));
    }

    @Test
    public void testXdmpCastableAs3() {
        exportTester("testXdmpCastableAs3", "{\"ns\":\"xdmp\", \"fn\":\"castable-as\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://www.w3.org/2001/XMLSchema\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"int\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"1\"]}]}", p.xdmp.castableAs(p.xs.string("http://www.w3.org/2001/XMLSchema"), p.xs.string("int"), p.xs.string("1")));
    }

    @Test
    public void testXdmpCrypt2() {
        exportTester("testXdmpCrypt2", "{\"ns\":\"xdmp\", \"fn\":\"crypt\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"123abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"admin\"]}]}", p.xdmp.crypt(p.xs.string("123abc"), p.xs.string("admin")));
    }

    @Test
    public void testXdmpCrypt21() {
        exportTester("testXdmpCrypt21", "{\"ns\":\"xdmp\", \"fn\":\"crypt2\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.xdmp.crypt2(p.xs.string("abc")));
    }

    @Test
    public void testXdmpDaynameFromDate1() {
        exportTester("testXdmpDaynameFromDate1", "{\"ns\":\"xdmp\", \"fn\":\"dayname-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}]}", p.xdmp.daynameFromDate(p.xs.date("2016-01-02")));
    }

    @Test
    public void testXdmpDecodeFromNCName1() {
        exportTester("testXdmpDecodeFromNCName1", "{\"ns\":\"xdmp\", \"fn\":\"decode-from-NCName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"A_20_Name\"]}]}", p.xdmp.decodeFromNCName(p.xs.string("A_20_Name")));
    }

    @Test
    public void testXdmpDescribe1() {
        exportTester("testXdmpDescribe1", "{\"ns\":\"xdmp\", \"fn\":\"describe\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"123456\"]}]]}", p.xdmp.describe(p.xs.string("123456")));
    }

    @Test
    public void testXdmpDescribe2() {
        exportTester("testXdmpDescribe2", "{\"ns\":\"xdmp\", \"fn\":\"describe\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"123456\"]}], {\"ns\":\"xs\", \"fn\":\"unsignedInt\", \"args\":[2]}]}", p.xdmp.describe(p.xs.string("123456"), p.xs.unsignedInt(2)));
    }

    @Test
    public void testXdmpDescribe3() {
        exportTester("testXdmpDescribe3", "{\"ns\":\"xdmp\", \"fn\":\"describe\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"123456\"]}], {\"ns\":\"xs\", \"fn\":\"unsignedInt\", \"args\":[2]}, {\"ns\":\"xs\", \"fn\":\"unsignedInt\", \"args\":[3]}]}", p.xdmp.describe(p.xs.string("123456"), p.xs.unsignedInt(2), p.xs.unsignedInt(3)));
    }

    @Test
    public void testXdmpDiacriticLess1() {
        exportTester("testXdmpDiacriticLess1", "{\"ns\":\"xdmp\", \"fn\":\"diacritic-less\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.xdmp.diacriticLess(p.xs.string("abc")));
    }

    @Test
    public void testXdmpEncodeForNCName1() {
        exportTester("testXdmpEncodeForNCName1", "{\"ns\":\"xdmp\", \"fn\":\"encode-for-NCName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"A Name\"]}]}", p.xdmp.encodeForNCName(p.xs.string("A Name")));
    }

    @Test
    public void testXdmpFormatNumber1() {
        exportTester("testXdmpFormatNumber1", "{\"ns\":\"xdmp\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[9]}]]}", p.xdmp.formatNumber(p.xs.doubleVal(9)));
    }

    @Test
    public void testXdmpFormatNumber2() {
        exportTester("testXdmpFormatNumber2", "{\"ns\":\"xdmp\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[9]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"W\"]}]}", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W")));
    }

    @Test
    public void testXdmpFormatNumber3() {
        exportTester("testXdmpFormatNumber3", "{\"ns\":\"xdmp\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[9]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"W\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en\"]}]}", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en")));
    }

    @Test
    public void testXdmpFormatNumber4() {
        exportTester("testXdmpFormatNumber4", "{\"ns\":\"xdmp\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[9]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"W\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}]}", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string("")));
    }

    @Test
    public void testXdmpFormatNumber5() {
        exportTester("testXdmpFormatNumber5", "{\"ns\":\"xdmp\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[9]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"W\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}]}", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string("")));
    }

    @Test
    public void testXdmpFormatNumber6() {
        exportTester("testXdmpFormatNumber6", "{\"ns\":\"xdmp\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[9]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"W\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}]}", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string(""), p.xs.string("")));
    }

    @Test
    public void testXdmpFormatNumber7() {
        exportTester("testXdmpFormatNumber7", "{\"ns\":\"xdmp\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[9]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"W\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\",\"]}]}", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string(""), p.xs.string(""), p.xs.string(",")));
    }

    @Test
    public void testXdmpFormatNumber8() {
        exportTester("testXdmpFormatNumber8", "{\"ns\":\"xdmp\", \"fn\":\"format-number\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[9]}], {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"W\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\",\"]}, {\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[3]}]}", p.xdmp.formatNumber(p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string(""), p.xs.string(""), p.xs.string(","), p.xs.integer(3)));
    }

    @Test
    public void testXdmpGetCurrentUser0() {
        exportTester("testXdmpGetCurrentUser0", "{\"ns\":\"xdmp\", \"fn\":\"get-current-user\", \"args\":[]}", p.xdmp.getCurrentUser());
    }

    @Test
    public void testXdmpHash321() {
        exportTester("testXdmpHash321", "{\"ns\":\"xdmp\", \"fn\":\"hash32\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.xdmp.hash32(p.xs.string("abc")));
    }

    @Test
    public void testXdmpHash641() {
        exportTester("testXdmpHash641", "{\"ns\":\"xdmp\", \"fn\":\"hash64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.xdmp.hash64(p.xs.string("abc")));
    }

    @Test
    public void testXdmpHexToInteger1() {
        exportTester("testXdmpHexToInteger1", "{\"ns\":\"xdmp\", \"fn\":\"hex-to-integer\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"1234567890abcdef\"]}]}", p.xdmp.hexToInteger(p.xs.string("1234567890abcdef")));
    }

    @Test
    public void testXdmpHmacMd52() {
        exportTester("testXdmpHmacMd52", "{\"ns\":\"xdmp\", \"fn\":\"hmac-md5\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"def\"]}]}", p.xdmp.hmacMd5(p.xs.string("abc"), p.xs.string("def")));
    }

    @Test
    public void testXdmpHmacMd53() {
        exportTester("testXdmpHmacMd53", "{\"ns\":\"xdmp\", \"fn\":\"hmac-md5\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"def\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}", p.xdmp.hmacMd5(p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64")));
    }

    @Test
    public void testXdmpHmacSha12() {
        exportTester("testXdmpHmacSha12", "{\"ns\":\"xdmp\", \"fn\":\"hmac-sha1\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"def\"]}]}", p.xdmp.hmacSha1(p.xs.string("abc"), p.xs.string("def")));
    }

    @Test
    public void testXdmpHmacSha13() {
        exportTester("testXdmpHmacSha13", "{\"ns\":\"xdmp\", \"fn\":\"hmac-sha1\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"def\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}", p.xdmp.hmacSha1(p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64")));
    }

    @Test
    public void testXdmpHmacSha2562() {
        exportTester("testXdmpHmacSha2562", "{\"ns\":\"xdmp\", \"fn\":\"hmac-sha256\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"def\"]}]}", p.xdmp.hmacSha256(p.xs.string("abc"), p.xs.string("def")));
    }

    @Test
    public void testXdmpHmacSha2563() {
        exportTester("testXdmpHmacSha2563", "{\"ns\":\"xdmp\", \"fn\":\"hmac-sha256\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"def\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}", p.xdmp.hmacSha256(p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64")));
    }

    @Test
    public void testXdmpHmacSha5122() {
        exportTester("testXdmpHmacSha5122", "{\"ns\":\"xdmp\", \"fn\":\"hmac-sha512\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"def\"]}]}", p.xdmp.hmacSha512(p.xs.string("abc"), p.xs.string("def")));
    }

    @Test
    public void testXdmpHmacSha5123() {
        exportTester("testXdmpHmacSha5123", "{\"ns\":\"xdmp\", \"fn\":\"hmac-sha512\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"def\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}", p.xdmp.hmacSha512(p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64")));
    }

    @Test
    public void testXdmpInitcap1() {
        exportTester("testXdmpInitcap1", "{\"ns\":\"xdmp\", \"fn\":\"initcap\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.xdmp.initcap(p.xs.string("abc")));
    }

    @Test
    public void testXdmpIntegerToHex1() {
        exportTester("testXdmpIntegerToHex1", "{\"ns\":\"xdmp\", \"fn\":\"integer-to-hex\", \"args\":[{\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[123]}]}", p.xdmp.integerToHex(p.xs.integer(123)));
    }

    @Test
    public void testXdmpIntegerToOctal1() {
        exportTester("testXdmpIntegerToOctal1", "{\"ns\":\"xdmp\", \"fn\":\"integer-to-octal\", \"args\":[{\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[123]}]}", p.xdmp.integerToOctal(p.xs.integer(123)));
    }

    @Test
    public void testXdmpKeyFromQName1() {
        exportTester("testXdmpKeyFromQName1", "{\"ns\":\"xdmp\", \"fn\":\"key-from-QName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"QName\", \"args\":[\"abc\"]}]}", p.xdmp.keyFromQName(p.xs.qname("abc")));
    }

    @Test
    public void testXdmpLshift642() {
        exportTester("testXdmpLshift642", "{\"ns\":\"xdmp\", \"fn\":\"lshift64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[255]}, {\"ns\":\"xs\", \"fn\":\"long\", \"args\":[2]}]}", p.xdmp.lshift64(p.xs.unsignedLong(255), p.xs.longVal(2)));
    }

    @Test
    public void testXdmpMd51() {
        exportTester("testXdmpMd51", "{\"ns\":\"xdmp\", \"fn\":\"md5\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.xdmp.md5(p.xs.string("abc")));
    }

    @Test
    public void testXdmpMd52() {
        exportTester("testXdmpMd52", "{\"ns\":\"xdmp\", \"fn\":\"md5\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}", p.xdmp.md5(p.xs.string("abc"), p.xs.string("base64")));
    }

    @Test
    public void testXdmpMonthNameFromDate1() {
        exportTester("testXdmpMonthNameFromDate1", "{\"ns\":\"xdmp\", \"fn\":\"month-name-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}]}", p.xdmp.monthNameFromDate(p.xs.date("2016-01-02")));
    }

    @Test
    public void testXdmpMul642() {
        exportTester("testXdmpMul642", "{\"ns\":\"xdmp\", \"fn\":\"mul64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[123]}, {\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[456]}]}", p.xdmp.mul64(p.xs.unsignedLong(123), p.xs.unsignedLong(456)));
    }

    @Test
    public void testXdmpNot641() {
        exportTester("testXdmpNot641", "{\"ns\":\"xdmp\", \"fn\":\"not64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[255]}]}", p.xdmp.not64(p.xs.unsignedLong(255)));
    }

    @Test
    public void testXdmpOctalToInteger1() {
        exportTester("testXdmpOctalToInteger1", "{\"ns\":\"xdmp\", \"fn\":\"octal-to-integer\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"12345670\"]}]}", p.xdmp.octalToInteger(p.xs.string("12345670")));
    }

    @Test
    public void testXdmpOr642() {
        exportTester("testXdmpOr642", "{\"ns\":\"xdmp\", \"fn\":\"or64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[255]}, {\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[2]}]}", p.xdmp.or64(p.xs.unsignedLong(255), p.xs.unsignedLong(2)));
    }

    @Test
    public void testXdmpParseDateTime2() {
        exportTester("testXdmpParseDateTime2", "{\"ns\":\"xdmp\", \"fn\":\"parse-dateTime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"[Y0001]-[M01]-[D01]T[h01]:[m01]:[s01].[f1][Z]\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"2016-01-06T17:13:50.873594-08:00\"]}]}", p.xdmp.parseDateTime(p.xs.string("[Y0001]-[M01]-[D01]T[h01]:[m01]:[s01].[f1][Z]"), p.xs.string("2016-01-06T17:13:50.873594-08:00")));
    }

    @Test
    public void testXdmpParseYymmdd2() {
        exportTester("testXdmpParseYymmdd2", "{\"ns\":\"xdmp\", \"fn\":\"parse-yymmdd\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"yyyy-MM-ddThh:mm:ss.Sz\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"2016-01-06T17:13:50.873594-8.00\"]}]}", p.xdmp.parseYymmdd(p.xs.string("yyyy-MM-ddThh:mm:ss.Sz"), p.xs.string("2016-01-06T17:13:50.873594-8.00")));
    }

    @Test
    public void testXdmpPosition2() {
        exportTester("testXdmpPosition2", "{\"ns\":\"xdmp\", \"fn\":\"position\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcdef\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"cd\"]}]}", p.xdmp.position(p.xs.string("abcdef"), p.xs.string("cd")));
    }

    @Test
    public void testXdmpPosition3() {
        exportTester("testXdmpPosition3", "{\"ns\":\"xdmp\", \"fn\":\"position\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abcdef\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"cd\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"http://marklogic.com/collation/\"]}]}", p.xdmp.position(p.xs.string("abcdef"), p.xs.string("cd"), p.xs.string("http://marklogic.com/collation/")));
    }

    @Test
    public void testXdmpQNameFromKey1() {
        exportTester("testXdmpQNameFromKey1", "{\"ns\":\"xdmp\", \"fn\":\"QName-from-key\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"{http://a/b}c\"]}]}", p.xdmp.QNameFromKey(p.xs.string("{http://a/b}c")));
    }

    @Test
    public void testXdmpQuarterFromDate1() {
        exportTester("testXdmpQuarterFromDate1", "{\"ns\":\"xdmp\", \"fn\":\"quarter-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}]}", p.xdmp.quarterFromDate(p.xs.date("2016-01-02")));
    }

    @Test
    public void testXdmpRandom0() {
        exportTester("testXdmpRandom0", "{\"ns\":\"xdmp\", \"fn\":\"random\", \"args\":[]}", p.xdmp.random());
    }

    @Test
    public void testXdmpRandom1() {
        exportTester("testXdmpRandom1", "{\"ns\":\"xdmp\", \"fn\":\"random\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[1]}]}", p.xdmp.random(p.xs.unsignedLong(1)));
    }

    @Test
    public void testXdmpResolveUri2() {
        exportTester("testXdmpResolveUri2", "{\"ns\":\"xdmp\", \"fn\":\"resolve-uri\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"b?c#d\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"/a/x\"]}]}", p.xdmp.resolveUri(p.xs.string("b?c#d"), p.xs.string("/a/x")));
    }

    @Test
    public void testXdmpRshift642() {
        exportTester("testXdmpRshift642", "{\"ns\":\"xdmp\", \"fn\":\"rshift64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[255]}, {\"ns\":\"xs\", \"fn\":\"long\", \"args\":[2]}]}", p.xdmp.rshift64(p.xs.unsignedLong(255), p.xs.longVal(2)));
    }

    @Test
    public void testXdmpSha11() {
        exportTester("testXdmpSha11", "{\"ns\":\"xdmp\", \"fn\":\"sha1\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.xdmp.sha1(p.xs.string("abc")));
    }

    @Test
    public void testXdmpSha12() {
        exportTester("testXdmpSha12", "{\"ns\":\"xdmp\", \"fn\":\"sha1\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}", p.xdmp.sha1(p.xs.string("abc"), p.xs.string("base64")));
    }

    @Test
    public void testXdmpSha2561() {
        exportTester("testXdmpSha2561", "{\"ns\":\"xdmp\", \"fn\":\"sha256\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.xdmp.sha256(p.xs.string("abc")));
    }

    @Test
    public void testXdmpSha2562() {
        exportTester("testXdmpSha2562", "{\"ns\":\"xdmp\", \"fn\":\"sha256\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}", p.xdmp.sha256(p.xs.string("abc"), p.xs.string("base64")));
    }

    @Test
    public void testXdmpSha3841() {
        exportTester("testXdmpSha3841", "{\"ns\":\"xdmp\", \"fn\":\"sha384\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.xdmp.sha384(p.xs.string("abc")));
    }

    @Test
    public void testXdmpSha3842() {
        exportTester("testXdmpSha3842", "{\"ns\":\"xdmp\", \"fn\":\"sha384\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}", p.xdmp.sha384(p.xs.string("abc"), p.xs.string("base64")));
    }

    @Test
    public void testXdmpSha5121() {
        exportTester("testXdmpSha5121", "{\"ns\":\"xdmp\", \"fn\":\"sha512\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.xdmp.sha512(p.xs.string("abc")));
    }

    @Test
    public void testXdmpSha5122() {
        exportTester("testXdmpSha5122", "{\"ns\":\"xdmp\", \"fn\":\"sha512\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"base64\"]}]}", p.xdmp.sha512(p.xs.string("abc"), p.xs.string("base64")));
    }

    @Test
    public void testXdmpStep642() {
        exportTester("testXdmpStep642", "{\"ns\":\"xdmp\", \"fn\":\"step64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[123]}, {\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[456]}]}", p.xdmp.step64(p.xs.unsignedLong(123), p.xs.unsignedLong(456)));
    }

    @Test
    public void testXdmpStrftime2() {
        exportTester("testXdmpStrftime2", "{\"ns\":\"xdmp\", \"fn\":\"strftime\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"%a, %d %b %Y %H:%M:%S\"]}, {\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-06T17:13:50.873594-08:00\"]}]}", p.xdmp.strftime(p.xs.string("%a, %d %b %Y %H:%M:%S"), p.xs.dateTime("2016-01-06T17:13:50.873594-08:00")));
    }

    @Test
    public void testXdmpTimestampToWallclock1() {
        exportTester("testXdmpTimestampToWallclock1", "{\"ns\":\"xdmp\", \"fn\":\"timestamp-to-wallclock\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[1]}]}", p.xdmp.timestampToWallclock(p.xs.unsignedLong(1)));
    }

    @Test
    public void testXdmpToJson1() {
        exportTester("testXdmpToJson1", "{\"ns\":\"xdmp\", \"fn\":\"to-json\", \"args\":[[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]]}", p.xdmp.toJson(p.xs.string("abc")));
    }

    @Test
    public void testXdmpType1() {
        exportTester("testXdmpType1", "{\"ns\":\"xdmp\", \"fn\":\"type\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a\"]}]}", p.xdmp.type(p.xs.string("a")));
    }

    @Test
    public void testXdmpUrlDecode1() {
        exportTester("testXdmpUrlDecode1", "{\"ns\":\"xdmp\", \"fn\":\"url-decode\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a+b\"]}]}", p.xdmp.urlDecode(p.xs.string("a+b")));
    }

    @Test
    public void testXdmpUrlEncode1() {
        exportTester("testXdmpUrlEncode1", "{\"ns\":\"xdmp\", \"fn\":\"url-encode\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a b\"]}]}", p.xdmp.urlEncode(p.xs.string("a b")));
    }

    @Test
    public void testXdmpWallclockToTimestamp1() {
        exportTester("testXdmpWallclockToTimestamp1", "{\"ns\":\"xdmp\", \"fn\":\"wallclock-to-timestamp\", \"args\":[{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-06T17:13:50.873594-08:00\"]}]}", p.xdmp.wallclockToTimestamp(p.xs.dateTime("2016-01-06T17:13:50.873594-08:00")));
    }

    @Test
    public void testXdmpWeekdayFromDate1() {
        exportTester("testXdmpWeekdayFromDate1", "{\"ns\":\"xdmp\", \"fn\":\"weekday-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}]}", p.xdmp.weekdayFromDate(p.xs.date("2016-01-02")));
    }

    @Test
    public void testXdmpWeekFromDate1() {
        exportTester("testXdmpWeekFromDate1", "{\"ns\":\"xdmp\", \"fn\":\"week-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}]}", p.xdmp.weekFromDate(p.xs.date("2016-01-02")));
    }

    @Test
    public void testXdmpXor642() {
        exportTester("testXdmpXor642", "{\"ns\":\"xdmp\", \"fn\":\"xor64\", \"args\":[{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[255]}, {\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[2]}]}", p.xdmp.xor64(p.xs.unsignedLong(255), p.xs.unsignedLong(2)));
    }

    @Test
    public void testXdmpYeardayFromDate1() {
        exportTester("testXdmpYeardayFromDate1", "{\"ns\":\"xdmp\", \"fn\":\"yearday-from-date\", \"args\":[{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}]}", p.xdmp.yeardayFromDate(p.xs.date("2016-01-02")));
    }

    @Test
    public void testXsAnyURI1() {
        exportTester("testXsAnyURI1", "{\"ns\":\"xs\", \"fn\":\"anyURI\", \"args\":[\"http://a/b?c#d\"]}", p.xs.anyURI("http://a/b?c#d"));
    }

    @Test
    public void testXsBase64Binary1() {
        exportTester("testXsBase64Binary1", "{\"ns\":\"xs\", \"fn\":\"base64Binary\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.xs.base64Binary(p.xs.string("abc")));
    }

    @Test
    public void testXsBoolean1() {
        exportTester("testXsBoolean1", "{\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[true]}", p.xs.booleanVal(true));
    }

    @Test
    public void testXsByte1() {
        exportTester("testXsByte1", "{\"ns\":\"xs\", \"fn\":\"byte\", \"args\":[1]}", p.xs.byteVal((byte) 1));
    }

    @Test
    public void testXsDate1() {
        exportTester("testXsDate1", "{\"ns\":\"xs\", \"fn\":\"date\", \"args\":[\"2016-01-02\"]}", p.xs.date("2016-01-02"));
    }

    @Test
    public void testXsDateTime1() {
        exportTester("testXsDateTime1", "{\"ns\":\"xs\", \"fn\":\"dateTime\", \"args\":[\"2016-01-02T10:09:08Z\"]}", p.xs.dateTime("2016-01-02T10:09:08Z"));
    }

    @Test
    public void testXsDayTimeDuration1() {
        exportTester("testXsDayTimeDuration1", "{\"ns\":\"xs\", \"fn\":\"dayTimeDuration\", \"args\":[\"P3DT4H5M6S\"]}", p.xs.dayTimeDuration("P3DT4H5M6S"));
    }

    @Test
    public void testXsDecimal1() {
        exportTester("testXsDecimal1", "{\"ns\":\"xs\", \"fn\":\"decimal\", \"args\":[1.2]}", p.xs.decimal(1.2));
    }

    @Test
    public void testXsDouble1() {
        exportTester("testXsDouble1", "{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}", p.xs.doubleVal(1.2));
    }

    @Test
    public void testXsDuration1() {
        exportTester("testXsDuration1", "{\"ns\":\"xs\", \"fn\":\"duration\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"P1Y2M\"]}]}", p.xs.duration(p.xs.string("P1Y2M")));
    }

    @Test
    public void testXsFloat1() {
        exportTester("testXsFloat1", "{\"ns\":\"xs\", \"fn\":\"float\", \"args\":[1]}", p.xs.floatVal((float) 1));
    }

    @Test
    public void testXsGDay1() {
        exportTester("testXsGDay1", "{\"ns\":\"xs\", \"fn\":\"gDay\", \"args\":[\"---02\"]}", p.xs.gDay("---02"));
    }

    @Test
    public void testXsGMonth1() {
        exportTester("testXsGMonth1", "{\"ns\":\"xs\", \"fn\":\"gMonth\", \"args\":[\"--01\"]}", p.xs.gMonth("--01"));
    }

    @Test
    public void testXsGMonthDay1() {
        exportTester("testXsGMonthDay1", "{\"ns\":\"xs\", \"fn\":\"gMonthDay\", \"args\":[\"--01-02\"]}", p.xs.gMonthDay("--01-02"));
    }

    @Test
    public void testXsGYear1() {
        exportTester("testXsGYear1", "{\"ns\":\"xs\", \"fn\":\"gYear\", \"args\":[\"2016\"]}", p.xs.gYear("2016"));
    }

    @Test
    public void testXsGYearMonth1() {
        exportTester("testXsGYearMonth1", "{\"ns\":\"xs\", \"fn\":\"gYearMonth\", \"args\":[\"2016-01\"]}", p.xs.gYearMonth("2016-01"));
    }

    @Test
    public void testXsHexBinary1() {
        exportTester("testXsHexBinary1", "{\"ns\":\"xs\", \"fn\":\"hexBinary\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}]}", p.xs.hexBinary(p.xs.string("abc")));
    }

    @Test
    public void testXsInt1() {
        exportTester("testXsInt1", "{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[1]}", p.xs.intVal(1));
    }

    @Test
    public void testXsInteger1() {
        exportTester("testXsInteger1", "{\"ns\":\"xs\", \"fn\":\"integer\", \"args\":[1]}", p.xs.integer(1));
    }

    @Test
    public void testXsLanguage1() {
        exportTester("testXsLanguage1", "{\"ns\":\"xs\", \"fn\":\"language\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"en-US\"]}]}", p.xs.language(p.xs.string("en-US")));
    }

    @Test
    public void testXsLong1() {
        exportTester("testXsLong1", "{\"ns\":\"xs\", \"fn\":\"long\", \"args\":[1]}", p.xs.longVal(1));
    }

    @Test
    public void testXsName1() {
        exportTester("testXsName1", "{\"ns\":\"xs\", \"fn\":\"Name\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a:b:c\"]}]}", p.xs.Name(p.xs.string("a:b:c")));
    }

    @Test
    public void testXsNCName1() {
        exportTester("testXsNCName1", "{\"ns\":\"xs\", \"fn\":\"NCName\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a-b-c\"]}]}", p.xs.NCName(p.xs.string("a-b-c")));
    }

    @Test
    public void testXsNegativeInteger1() {
        exportTester("testXsNegativeInteger1", "{\"ns\":\"xs\", \"fn\":\"negativeInteger\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[-1]}]}", p.xs.negativeInteger(p.xs.doubleVal(-1)));
    }

    @Test
    public void testXsNMTOKEN1() {
        exportTester("testXsNMTOKEN1", "{\"ns\":\"xs\", \"fn\":\"NMTOKEN\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a:b:c\"]}]}", p.xs.NMTOKEN(p.xs.string("a:b:c")));
    }

    @Test
    public void testXsNonNegativeInteger1() {
        exportTester("testXsNonNegativeInteger1", "{\"ns\":\"xs\", \"fn\":\"nonNegativeInteger\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"0\"]}]}", p.xs.nonNegativeInteger(p.xs.string("0")));
    }

    @Test
    public void testXsNonPositiveInteger1() {
        exportTester("testXsNonPositiveInteger1", "{\"ns\":\"xs\", \"fn\":\"nonPositiveInteger\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"0\"]}]}", p.xs.nonPositiveInteger(p.xs.string("0")));
    }

    @Test
    public void testXsNormalizedString1() {
        exportTester("testXsNormalizedString1", "{\"ns\":\"xs\", \"fn\":\"normalizedString\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a b c\"]}]}", p.xs.normalizedString(p.xs.string("a b c")));
    }

    @Test
    public void testXsNumeric1() {
        exportTester("testXsNumeric1", "{\"ns\":\"xs\", \"fn\":\"numeric\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1.2]}]}", p.xs.numeric(p.xs.doubleVal(1.2)));
    }

    @Test
    public void testXsPositiveInteger1() {
        exportTester("testXsPositiveInteger1", "{\"ns\":\"xs\", \"fn\":\"positiveInteger\", \"args\":[{\"ns\":\"xs\", \"fn\":\"double\", \"args\":[1]}]}", p.xs.positiveInteger(p.xs.doubleVal(1)));
    }

    @Test
    public void testXsQName1() {
        exportTester("testXsQName1", "{\"ns\":\"xs\", \"fn\":\"QName\", \"args\":[\"abc\"]}", p.xs.qname("abc"));
    }

    @Test
    public void testXsShort1() {
        exportTester("testXsShort1", "{\"ns\":\"xs\", \"fn\":\"short\", \"args\":[1]}", p.xs.shortVal((short) 1));
    }

    @Test
    public void testXsString1() {
        exportTester("testXsString1", "{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"abc\"]}", p.xs.string("abc"));
    }

    @Test
    public void testXsTime1() {
        exportTester("testXsTime1", "{\"ns\":\"xs\", \"fn\":\"time\", \"args\":[\"10:09:08Z\"]}", p.xs.time("10:09:08Z"));
    }

    @Test
    public void testXsToken1() {
        exportTester("testXsToken1", "{\"ns\":\"xs\", \"fn\":\"token\", \"args\":[{\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"a b c\"]}]}", p.xs.token(p.xs.string("a b c")));
    }

    @Test
    public void testXsUnsignedByte1() {
        exportTester("testXsUnsignedByte1", "{\"ns\":\"xs\", \"fn\":\"unsignedByte\", \"args\":[1]}", p.xs.unsignedByte((byte) 1));
    }

    @Test
    public void testXsUnsignedInt1() {
        exportTester("testXsUnsignedInt1", "{\"ns\":\"xs\", \"fn\":\"unsignedInt\", \"args\":[1]}", p.xs.unsignedInt(1));
    }

    @Test
    public void testXsUnsignedLong1() {
        exportTester("testXsUnsignedLong1", "{\"ns\":\"xs\", \"fn\":\"unsignedLong\", \"args\":[1]}", p.xs.unsignedLong(1));
    }

    @Test
    public void testXsUnsignedShort1() {
        exportTester("testXsUnsignedShort1", "{\"ns\":\"xs\", \"fn\":\"unsignedShort\", \"args\":[1]}", p.xs.unsignedShort((short) 1));
    }

    @Test
    public void testXsUntypedAtomic1() {
        exportTester("testXsUntypedAtomic1", "{\"ns\":\"xs\", \"fn\":\"untypedAtomic\", \"args\":[\"abc\"]}", p.xs.untypedAtomic("abc"));
    }

    @Test
    public void testXsYearMonthDuration1() {
        exportTester("testXsYearMonthDuration1", "{\"ns\":\"xs\", \"fn\":\"yearMonthDuration\", \"args\":[\"P1Y2M\"]}", p.xs.yearMonthDuration("P1Y2M"));
    }

    @Test
    public void testOpAdd2() {
        exportTester("testOpAdd2", "{\"ns\":\"op\", \"fn\":\"add\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}]}", p.add(p.xs.intVal(1), p.xs.intVal(2)));
    }

    @Test
    public void testOpAdd3() {
        exportTester("testOpAdd3", "{\"ns\":\"op\", \"fn\":\"add\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"3\"]}]}", p.add(p.xs.intVal(1), p.xs.intVal(2), p.xs.intVal(3)));
    }

    @Test
    public void testOpAnd2() {
        exportTester("testOpAnd2", "{\"ns\":\"op\", \"fn\":\"and\", \"args\":[{\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"true\"]}, {\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"true\"]}]}", p.and(p.xs.booleanVal(true), p.xs.booleanVal(true)));
    }

    @Test
    public void testOpAnd3() {
        exportTester("testOpAnd3", "{\"ns\":\"op\", \"fn\":\"and\", \"args\":[{\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"true\"]}, {\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"true\"]}, {\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"true\"]}]}", p.and(p.xs.booleanVal(true), p.xs.booleanVal(true), p.xs.booleanVal(true)));
    }

    @Test
    public void testOpDivide2() {
        exportTester("testOpDivide2", "{\"ns\":\"op\", \"fn\":\"divide\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"6\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}]}", p.divide(p.xs.intVal(6), p.xs.intVal(2)));
    }

    @Test
    public void testOpEq2() {
        exportTester("testOpEq2", "{\"ns\":\"op\", \"fn\":\"eq\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}]}", p.eq(p.xs.intVal(1), p.xs.intVal(1)));
    }

    @Test
    public void testOpGe2() {
        exportTester("testOpGe2", "{\"ns\":\"op\", \"fn\":\"ge\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}]}", p.ge(p.xs.intVal(1), p.xs.intVal(1)));
    }

    @Test
    public void testOpGt2() {
        exportTester("testOpGt2", "{\"ns\":\"op\", \"fn\":\"gt\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}]}", p.gt(p.xs.intVal(2), p.xs.intVal(1)));
    }

    @Test
    public void testOpLe2() {
        exportTester("testOpLe2", "{\"ns\":\"op\", \"fn\":\"le\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}]}", p.le(p.xs.intVal(1), p.xs.intVal(1)));
    }

    @Test
    public void testOpLt2() {
        exportTester("testOpLt2", "{\"ns\":\"op\", \"fn\":\"lt\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}]}", p.lt(p.xs.intVal(1), p.xs.intVal(2)));
    }

    @Test
    public void testOpMultiply2() {
        exportTester("testOpMultiply2", "{\"ns\":\"op\", \"fn\":\"multiply\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"3\"]}]}", p.multiply(p.xs.intVal(2), p.xs.intVal(3)));
    }

    @Test
    public void testOpMultiply3() {
        exportTester("testOpMultiply3", "{\"ns\":\"op\", \"fn\":\"multiply\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"3\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"4\"]}]}", p.multiply(p.xs.intVal(2), p.xs.intVal(3), p.xs.intVal(4)));
    }

    @Test
    public void testOpNe2() {
        exportTester("testOpNe2", "{\"ns\":\"op\", \"fn\":\"ne\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"1\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}]}", p.ne(p.xs.intVal(1), p.xs.intVal(2)));
    }

    @Test
    public void testOpNot1() {
        exportTester("testOpNot1", "{\"ns\":\"op\", \"fn\":\"not\", \"args\":[{\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"false\"]}]}", p.not(p.xs.booleanVal(false)));
    }

    @Test
    public void testOpOr2() {
        exportTester("testOpOr2", "{\"ns\":\"op\", \"fn\":\"or\", \"args\":[{\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"false\"]}, {\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"true\"]}]}", p.or(p.xs.booleanVal(false), p.xs.booleanVal(true)));
    }

    @Test
    public void testOpOr3() {
        exportTester("testOpOr3", "{\"ns\":\"op\", \"fn\":\"or\", \"args\":[{\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"false\"]}, {\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"true\"]}, {\"ns\":\"xs\", \"fn\":\"boolean\", \"args\":[\"false\"]}]}", p.or(p.xs.booleanVal(false), p.xs.booleanVal(true), p.xs.booleanVal(false)));
    }

    @Test
    public void testOpSubtract2() {
        exportTester("testOpSubtract2", "{\"ns\":\"op\", \"fn\":\"subtract\", \"args\":[{\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"3\"]}, {\"ns\":\"xs\", \"fn\":\"int\", \"args\":[\"2\"]}]}", p.subtract(p.xs.intVal(3), p.xs.intVal(2)));
    }
}