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

import com.marklogic.client.type.ItemSeqExpr;

// IMPORTANT: Do not edit. This file is generated.
public class PlanGeneratedTest extends PlanGeneratedBase {

    @Test
    public void testCtsStem1Exec() {
        executeTester("testCtsStem1", p.cts.stem(p.col("1")), "\"ran\"", new Object[]{p.xs.string("ran")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testCtsStem2Exec() {
        executeTester("testCtsStem2", p.cts.stem(p.col("1"), p.col("2")), "\"ran\"", new Object[]{p.xs.string("ran"), p.xs.string("en")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testCtsTokenize1Exec() {
        executeTester("testCtsTokenize1", p.cts.tokenize(p.col("1")), "(cts:word(\"a\"), cts:punctuation(\"-\"), cts:word(\"b\"), cts:space(\" \"), cts:word(\"c\"))", new Object[]{p.xs.string("a-b c")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testCtsTokenize2Exec() {
        executeTester("testCtsTokenize2", p.cts.tokenize(p.col("1"), p.col("2")), "(cts:word(\"a\"), cts:punctuation(\"-\"), cts:word(\"b\"), cts:space(\" \"), cts:word(\"c\"))", new Object[]{p.xs.string("a-b c"), p.xs.string("en")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnAbs1Exec() {
        executeTester("testFnAbs1", p.fn.abs(p.col("1")), "11", new Object[]{p.xs.doubleVal(-11)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnAdjustDateTimeToTimezone1Exec() {
        executeTester("testFnAdjustDateTimeToTimezone1", p.fn.adjustDateTimeToTimezone(p.col("1")), "xs:dateTime(\"2016-01-02T05:09:08-05:00\")", new Object[]{p.xs.dateTime("2016-01-02T10:09:08Z")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnAdjustDateTimeToTimezone2Exec() {
        executeTester("testFnAdjustDateTimeToTimezone2", p.fn.adjustDateTimeToTimezone(p.col("1"), p.col("2")), "xs:dateTime(\"2016-01-02T00:09:08-10:00\")", new Object[]{p.xs.dateTime("2016-01-02T10:09:08Z"), p.xs.dayTimeDuration("-PT10H")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnAdjustDateToTimezone1Exist() {
        executeTester("testFnAdjustDateToTimezone1", p.fn.adjustDateToTimezone(p.col("1")), null, new Object[]{p.xs.date("2016-01-02")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnAdjustDateToTimezone2Exist() {
        executeTester("testFnAdjustDateToTimezone2", p.fn.adjustDateToTimezone(p.col("1"), p.col("2")), null, new Object[]{p.xs.date("2016-01-02"), p.xs.dayTimeDuration("-PT10H")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnAdjustTimeToTimezone1Exec() {
        executeTester("testFnAdjustTimeToTimezone1", p.fn.adjustTimeToTimezone(p.col("1")), "xs:time(\"05:09:08-05:00\")", new Object[]{p.xs.time("10:09:08Z")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnAdjustTimeToTimezone2Exec() {
        executeTester("testFnAdjustTimeToTimezone2", p.fn.adjustTimeToTimezone(p.col("1"), p.col("2")), "xs:time(\"00:09:08-10:00\")", new Object[]{p.xs.time("10:09:08Z"), p.xs.dayTimeDuration("-PT10H")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnAnalyzeString2Exec() {
        executeTester("testFnAnalyzeString2", p.fn.analyzeString(p.col("1"), p.col("2")), "<s:analyze-string-result xmlns:s=\"http://www.w3.org/2005/xpath-functions\"><s:non-match>aXb</s:non-match><s:match>y</s:match><s:non-match>c</s:non-match></s:analyze-string-result>", new Object[]{p.xs.string("aXbyc"), p.xs.string("[xy]")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnAnalyzeString3Exec() {
        executeTester("testFnAnalyzeString3", p.fn.analyzeString(p.col("1"), p.col("2"), p.col("3")), "<s:analyze-string-result xmlns:s=\"http://www.w3.org/2005/xpath-functions\"><s:non-match>a</s:non-match><s:match>X</s:match><s:non-match>b</s:non-match><s:match>y</s:match><s:non-match>c</s:non-match></s:analyze-string-result>", new Object[]{p.xs.string("aXbyc"), p.xs.string("[xy]"), p.xs.string("i")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnBoolean1Exec() {
        executeTester("testFnBoolean1", p.fn.booleanExpr(p.col("1")), "fn:true()", new Object[]{p.xs.string("abc")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnCeiling1Exec() {
        executeTester("testFnCeiling1", p.fn.ceiling(p.col("1")), "2", new Object[]{p.xs.doubleVal(1.3)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnCodepointEqual2Exec() {
        executeTester("testFnCodepointEqual2", p.fn.codepointEqual(p.col("1"), p.col("2")), "fn:true()", new Object[]{p.xs.string("abc"), p.xs.string("abc")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnCompare2Exec() {
        executeTester("testFnCompare2", p.fn.compare(p.col("1"), p.col("2")), "1", new Object[]{p.xs.string("abz"), p.xs.string("aba")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnCompare3Exec() {
        executeTester("testFnCompare3", p.fn.compare(p.col("1"), p.col("2"), p.col("3")), "1", new Object[]{p.xs.string("abz"), p.xs.string("aba"), p.xs.string("http://marklogic.com/collation/")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnConcat2Exec() {
        executeTester("testFnConcat2", p.fn.concat(p.col("1"), p.col("2")), "\"ab\"", new Object[]{p.xs.string("a"), p.xs.string("b")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnConcat3Exec() {
        executeTester("testFnConcat3", p.fn.concat(p.col("1"), p.col("2"), p.col("3")), "\"abc\"", new Object[]{p.xs.string("a"), p.xs.string("b"), p.xs.string("c")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnContains2Exec() {
        executeTester("testFnContains2", p.fn.contains(p.col("1"), p.col("2")), "fn:true()", new Object[]{p.xs.string("abc"), p.xs.string("b")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnContains3Exec() {
        executeTester("testFnContains3", p.fn.contains(p.col("1"), p.col("2"), p.col("3")), "fn:true()", new Object[]{p.xs.string("abc"), p.xs.string("b"), p.xs.string("http://marklogic.com/collation/")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnCurrentDate0Exist() {
        executeTester("testFnCurrentDate0", p.fn.currentDate(), null, null, null);
    }

    @Test
    public void testFnCurrentDateTime0Exist() {
        executeTester("testFnCurrentDateTime0", p.fn.currentDateTime(), null, null, null);
    }

    @Test
    public void testFnCurrentTime0Exist() {
        executeTester("testFnCurrentTime0", p.fn.currentTime(), null, null, null);
    }

    @Test
    public void testFnDayFromDate1Exec() {
        executeTester("testFnDayFromDate1", p.fn.dayFromDate(p.col("1")), "2", new Object[]{p.xs.date("2016-01-02-03:04")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnDayFromDateTime1Exec() {
        executeTester("testFnDayFromDateTime1", p.fn.dayFromDateTime(p.col("1")), "2", new Object[]{p.xs.dateTime("2016-01-02T10:09:08Z")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnDaysFromDuration1Exec() {
        executeTester("testFnDaysFromDuration1", p.fn.daysFromDuration(p.col("1")), "3", new Object[]{p.xs.dayTimeDuration("P3DT4H5M6S")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnDeepEqual2Exec() {
        executeTester("testFnDeepEqual2", p.fn.deepEqual(p.col("1"), p.col("2")), "fn:true()", new Object[]{p.xs.string("abc"), p.xs.string("abc")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnDeepEqual3Exec() {
        executeTester("testFnDeepEqual3", p.fn.deepEqual(p.col("1"), p.col("2"), p.col("3")), "fn:true()", new Object[]{p.xs.string("abc"), p.xs.string("abc"), p.xs.string("http://marklogic.com/collation/")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnDefaultCollation0Exist() {
        executeTester("testFnDefaultCollation0", p.fn.defaultCollation(), null, null, null);
    }

    @Test
    public void testFnEmpty1Exec() {
        executeTester("testFnEmpty1", p.fn.empty(p.col("1")), "fn:false()", new Object[]{p.xs.doubleVal(1)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnEncodeForUri1Exec() {
        executeTester("testFnEncodeForUri1", p.fn.encodeForUri(p.col("1")), "\"http%3A%2F%2Fa%2Fb%3Fc%23d\"", new Object[]{p.xs.string("http://a/b?c#d")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnEndsWith2Exec() {
        executeTester("testFnEndsWith2", p.fn.endsWith(p.col("1"), p.col("2")), "fn:true()", new Object[]{p.xs.string("abc"), p.xs.string("c")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnEndsWith3Exec() {
        executeTester("testFnEndsWith3", p.fn.endsWith(p.col("1"), p.col("2"), p.col("3")), "fn:true()", new Object[]{p.xs.string("abc"), p.xs.string("c"), p.xs.string("http://marklogic.com/collation/")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnEscapeHtmlUri1Exec() {
        executeTester("testFnEscapeHtmlUri1", p.fn.escapeHtmlUri(p.col("1")), "\"http://a/b?c#d\"", new Object[]{p.xs.string("http://a/b?c#d")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnExists1Exec() {
        executeTester("testFnExists1", p.fn.exists(p.col("1")), "fn:true()", new Object[]{p.xs.doubleVal(1)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnFalse0Exist() {
        executeTester("testFnFalse0", p.fn.falseExpr(), null, null, null);
    }

    @Test
    public void testFnFloor1Exec() {
        executeTester("testFnFloor1", p.fn.floor(p.col("1")), "1", new Object[]{p.xs.doubleVal(1.7)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnFormatDate2Exec() {
        executeTester("testFnFormatDate2", p.fn.formatDate(p.col("1"), p.col("2")), "\"2016/01/02\"", new Object[]{p.xs.date("2016-01-02-03:04"), p.xs.string("[Y0001]/[M01]/[D01]")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnFormatDateTime2Exec() {
        executeTester("testFnFormatDateTime2", p.fn.formatDateTime(p.col("1"), p.col("2")), "\"2016/01/02 10:09:08:00\"", new Object[]{p.xs.dateTime("2016-01-02T10:09:08Z"), p.xs.string("[Y0001]/[M01]/[D01] [H01]:[m01]:[s01]:[f01]")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnFormatNumber2Exec() {
        executeTester("testFnFormatNumber2", p.fn.formatNumber(p.col("1"), p.col("2")), "\"1,234.50\"", new Object[]{p.xs.doubleVal(1234.5), p.xs.string("#,##0.00")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnFormatTime2Exec() {
        executeTester("testFnFormatTime2", p.fn.formatTime(p.col("1"), p.col("2")), "\"10:09:08:00\"", new Object[]{p.xs.time("10:09:08Z"), p.xs.string("[H01]:[m01]:[s01]:[f01]")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnHoursFromDateTime1Exec() {
        executeTester("testFnHoursFromDateTime1", p.fn.hoursFromDateTime(p.col("1")), "10", new Object[]{p.xs.dateTime("2016-01-02T10:09:08Z")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnHoursFromDuration1Exec() {
        executeTester("testFnHoursFromDuration1", p.fn.hoursFromDuration(p.col("1")), "4", new Object[]{p.xs.dayTimeDuration("P3DT4H5M6S")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnHoursFromTime1Exec() {
        executeTester("testFnHoursFromTime1", p.fn.hoursFromTime(p.col("1")), "10", new Object[]{p.xs.time("10:09:08Z")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnImplicitTimezone0Exist() {
        executeTester("testFnImplicitTimezone0", p.fn.implicitTimezone(), null, null, null);
    }

    @Test
    public void testFnIriToUri1Exec() {
        executeTester("testFnIriToUri1", p.fn.iriToUri(p.col("1")), "\"http://a/b?c#d\"", new Object[]{p.xs.string("http://a/b?c#d")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnLocalNameFromQName1Exist() {
        executeTester("testFnLocalNameFromQName1", p.fn.localNameFromQName(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.QName("abc")});
    }

    @Test
    public void testFnLowerCase1Exec() {
        executeTester("testFnLowerCase1", p.fn.lowerCase(p.col("1")), "\"abc\"", new Object[]{p.xs.string("ABC")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnMatches2Exec() {
        executeTester("testFnMatches2", p.fn.matches(p.col("1"), p.col("2")), "fn:false()", new Object[]{p.xs.string("abc"), p.xs.string("^.B")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnMatches3Exec() {
        executeTester("testFnMatches3", p.fn.matches(p.col("1"), p.col("2"), p.col("3")), "fn:true()", new Object[]{p.xs.string("abc"), p.xs.string("^.B"), p.xs.string("i")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnMinutesFromDateTime1Exec() {
        executeTester("testFnMinutesFromDateTime1", p.fn.minutesFromDateTime(p.col("1")), "9", new Object[]{p.xs.dateTime("2016-01-02T10:09:08Z")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnMinutesFromDuration1Exec() {
        executeTester("testFnMinutesFromDuration1", p.fn.minutesFromDuration(p.col("1")), "5", new Object[]{p.xs.dayTimeDuration("P3DT4H5M6S")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnMinutesFromTime1Exec() {
        executeTester("testFnMinutesFromTime1", p.fn.minutesFromTime(p.col("1")), "9", new Object[]{p.xs.time("10:09:08Z")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnMonthFromDate1Exec() {
        executeTester("testFnMonthFromDate1", p.fn.monthFromDate(p.col("1")), "1", new Object[]{p.xs.date("2016-01-02-03:04")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnMonthFromDateTime1Exec() {
        executeTester("testFnMonthFromDateTime1", p.fn.monthFromDateTime(p.col("1")), "1", new Object[]{p.xs.dateTime("2016-01-02T10:09:08Z")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnMonthsFromDuration1Exec() {
        executeTester("testFnMonthsFromDuration1", p.fn.monthsFromDuration(p.col("1")), "2", new Object[]{p.xs.yearMonthDuration("P1Y2M")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnNamespaceUriFromQName1Exist() {
        executeTester("testFnNamespaceUriFromQName1", p.fn.namespaceUriFromQName(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.QName("abc")});
    }

    @Test
    public void testFnNormalizeSpace1Exec() {
        executeTester("testFnNormalizeSpace1", p.fn.normalizeSpace(p.col("1")), "\"abc 123\"", new Object[]{p.xs.string(" abc  123 ")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnNormalizeUnicode1Exec() {
        executeTester("testFnNormalizeUnicode1", p.fn.normalizeUnicode(p.col("1")), "\" aBc \"", new Object[]{p.xs.string(" aBc ")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnNormalizeUnicode2Exec() {
        executeTester("testFnNormalizeUnicode2", p.fn.normalizeUnicode(p.col("1"), p.col("2")), "\" aBc \"", new Object[]{p.xs.string(" aBc "), p.xs.string("NFC")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnNot1Exec() {
        executeTester("testFnNot1", p.fn.not(p.col("1")), "fn:false()", new Object[]{p.xs.booleanVal(true)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnNumber1Exec() {
        executeTester("testFnNumber1", p.fn.number(p.col("1")), "1.1", new Object[]{p.xs.string("1.1")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnPrefixFromQName1Exist() {
        executeTester("testFnPrefixFromQName1", p.fn.prefixFromQName(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.QName("abc")});
    }

    @Test
    public void testFnQName2Exec() {
        executeTester("testFnQName2", p.fn.QName(p.col("1"), p.col("2")), "fn:QName(\"http://a/b\",\"c\")", new Object[]{p.xs.string("http://a/b"), p.xs.string("c")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnReplace3Exec() {
        executeTester("testFnReplace3", p.fn.replace(p.col("1"), p.col("2"), p.col("3")), "\"axc\"", new Object[]{p.xs.string("axc"), p.xs.string("^(.)X"), p.xs.string("$1b")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnReplace4Exec() {
        executeTester("testFnReplace4", p.fn.replace(p.col("1"), p.col("2"), p.col("3"), p.col("4")), "\"abc\"", new Object[]{p.xs.string("axc"), p.xs.string("^(.)X"), p.xs.string("$1b"), p.xs.string("i")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnResolveUri2Exec() {
        executeTester("testFnResolveUri2", p.fn.resolveUri(p.col("1"), p.col("2")), "xs:anyURI(\"http://a/b?c#d\")", new Object[]{p.xs.string("b?c#d"), p.xs.string("http://a/x")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnRound1Exec() {
        executeTester("testFnRound1", p.fn.round(p.col("1")), "2", new Object[]{p.xs.doubleVal(1.7)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnRoundHalfToEven1Exec() {
        executeTester("testFnRoundHalfToEven1", p.fn.roundHalfToEven(p.col("1")), "1234", new Object[]{p.xs.doubleVal(1234.5)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnRoundHalfToEven2Exec() {
        executeTester("testFnRoundHalfToEven2", p.fn.roundHalfToEven(p.col("1"), p.col("2")), "1200", new Object[]{p.xs.doubleVal(1234.5), p.xs.integer(-2)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnSecondsFromDateTime1Exec() {
        executeTester("testFnSecondsFromDateTime1", p.fn.secondsFromDateTime(p.col("1")), "8", new Object[]{p.xs.dateTime("2016-01-02T10:09:08Z")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnSecondsFromDuration1Exec() {
        executeTester("testFnSecondsFromDuration1", p.fn.secondsFromDuration(p.col("1")), "6", new Object[]{p.xs.dayTimeDuration("P3DT4H5M6S")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnSecondsFromTime1Exec() {
        executeTester("testFnSecondsFromTime1", p.fn.secondsFromTime(p.col("1")), "8", new Object[]{p.xs.time("10:09:08Z")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnStartsWith2Exec() {
        executeTester("testFnStartsWith2", p.fn.startsWith(p.col("1"), p.col("2")), "fn:true()", new Object[]{p.xs.string("abc"), p.xs.string("a")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnStartsWith3Exec() {
        executeTester("testFnStartsWith3", p.fn.startsWith(p.col("1"), p.col("2"), p.col("3")), "fn:true()", new Object[]{p.xs.string("abc"), p.xs.string("a"), p.xs.string("http://marklogic.com/collation/")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnString1Exec() {
        executeTester("testFnString1", p.fn.string(p.col("1")), "\"1\"", new Object[]{p.xs.doubleVal(1)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnStringLength1Exec() {
        executeTester("testFnStringLength1", p.fn.stringLength(p.col("1")), "3", new Object[]{p.xs.string("abc")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnStringToCodepoints1Exec() {
        executeTester("testFnStringToCodepoints1", p.fn.stringToCodepoints(p.col("1")), "(97, 98, 99)", new Object[]{p.xs.string("abc")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnSubstring2Exec() {
        executeTester("testFnSubstring2", p.fn.substring(p.col("1"), p.col("2")), "\"bcd\"", new Object[]{p.xs.string("abcd"), p.xs.doubleVal(2)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnSubstring3Exec() {
        executeTester("testFnSubstring3", p.fn.substring(p.col("1"), p.col("2"), p.col("3")), "\"bc\"", new Object[]{p.xs.string("abcd"), p.xs.doubleVal(2), p.xs.doubleVal(2)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnSubstringAfter2Exec() {
        executeTester("testFnSubstringAfter2", p.fn.substringAfter(p.col("1"), p.col("2")), "\"cd\"", new Object[]{p.xs.string("abcd"), p.xs.string("ab")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnSubstringAfter3Exec() {
        executeTester("testFnSubstringAfter3", p.fn.substringAfter(p.col("1"), p.col("2"), p.col("3")), "\"cd\"", new Object[]{p.xs.string("abcd"), p.xs.string("ab"), p.xs.string("http://marklogic.com/collation/")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnSubstringBefore2Exec() {
        executeTester("testFnSubstringBefore2", p.fn.substringBefore(p.col("1"), p.col("2")), "\"ab\"", new Object[]{p.xs.string("abcd"), p.xs.string("cd")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnSubstringBefore3Exec() {
        executeTester("testFnSubstringBefore3", p.fn.substringBefore(p.col("1"), p.col("2"), p.col("3")), "\"ab\"", new Object[]{p.xs.string("abcd"), p.xs.string("cd"), p.xs.string("http://marklogic.com/collation/")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnTimezoneFromDate1Exist() {
        executeTester("testFnTimezoneFromDate1", p.fn.timezoneFromDate(p.col("1")), null, new Object[]{p.xs.date("2016-01-02-03:04")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnTimezoneFromDateTime1Exec() {
        executeTester("testFnTimezoneFromDateTime1", p.fn.timezoneFromDateTime(p.col("1")), "xs:dayTimeDuration(\"PT0S\")", new Object[]{p.xs.dateTime("2016-01-02T10:09:08Z")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnTimezoneFromTime1Exec() {
        executeTester("testFnTimezoneFromTime1", p.fn.timezoneFromTime(p.col("1")), "xs:dayTimeDuration(\"PT0S\")", new Object[]{p.xs.time("10:09:08Z")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnTokenize2Exec() {
        executeTester("testFnTokenize2", p.fn.tokenize(p.col("1"), p.col("2")), "\"axbxc\"", new Object[]{p.xs.string("axbxc"), p.xs.string("X")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnTokenize3Exec() {
        executeTester("testFnTokenize3", p.fn.tokenize(p.col("1"), p.col("2"), p.col("3")), "(\"a\", \"b\", \"c\")", new Object[]{p.xs.string("axbxc"), p.xs.string("X"), p.xs.string("i")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnTranslate3Exec() {
        executeTester("testFnTranslate3", p.fn.translate(p.col("1"), p.col("2"), p.col("3")), "\"abcd\"", new Object[]{p.xs.string("axcy"), p.xs.string("xy"), p.xs.string("bd")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnTrue0Exist() {
        executeTester("testFnTrue0", p.fn.trueExpr(), null, null, null);
    }

    @Test
    public void testFnUnordered1Exist() {
        executeTester("testFnUnordered1", p.fn.unordered(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc")});
    }

    @Test
    public void testFnUpperCase1Exec() {
        executeTester("testFnUpperCase1", p.fn.upperCase(p.col("1")), "\"ABC\"", new Object[]{p.xs.string("abc")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnYearFromDate1Exec() {
        executeTester("testFnYearFromDate1", p.fn.yearFromDate(p.col("1")), "2016", new Object[]{p.xs.date("2016-01-02-03:04")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnYearFromDateTime1Exec() {
        executeTester("testFnYearFromDateTime1", p.fn.yearFromDateTime(p.col("1")), "2016", new Object[]{p.xs.dateTime("2016-01-02T10:09:08Z")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testFnYearsFromDuration1Exec() {
        executeTester("testFnYearsFromDuration1", p.fn.yearsFromDuration(p.col("1")), "1", new Object[]{p.xs.yearMonthDuration("P1Y2M")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathAcos1Exec() {
        executeTester("testMathAcos1", p.math.acos(p.col("1")), "1.0471975511966", new Object[]{p.xs.doubleVal(0.5)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathAsin1Exec() {
        executeTester("testMathAsin1", p.math.asin(p.col("1")), "0.523598775598299", new Object[]{p.xs.doubleVal(0.5)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathAtan1Exec() {
        executeTester("testMathAtan1", p.math.atan(p.col("1")), "1.26262701154934", new Object[]{p.xs.doubleVal(3.14159)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathAtan22Exec() {
        executeTester("testMathAtan22", p.math.atan2(p.col("1"), p.col("2")), "1.42732303452594", new Object[]{p.xs.doubleVal(36.23), p.xs.doubleVal(5.234)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathCeil1Exec() {
        executeTester("testMathCeil1", p.math.ceil(p.col("1")), "2", new Object[]{p.xs.doubleVal(1.3)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathCos1Exec() {
        executeTester("testMathCos1", p.math.cos(p.col("1")), "0.00442569798805079", new Object[]{p.xs.doubleVal(11)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathCosh1Exec() {
        executeTester("testMathCosh1", p.math.cosh(p.col("1")), "29937.0708659498", new Object[]{p.xs.doubleVal(11)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathCot1Exec() {
        executeTester("testMathCot1", p.math.cot(p.col("1")), "1.31422390103306", new Object[]{p.xs.doubleVal(19.5)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathDegrees1Exec() {
        executeTester("testMathDegrees1", p.math.degrees(p.col("1")), "90.0000000000002", new Object[]{p.xs.doubleVal(1.5707963267949)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathExp1Exec() {
        executeTester("testMathExp1", p.math.exp(p.col("1")), "1.10517091807565", new Object[]{p.xs.doubleVal(0.1)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathFabs1Exec() {
        executeTester("testMathFabs1", p.math.fabs(p.col("1")), "4.013", new Object[]{p.xs.doubleVal(4.013)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathFloor1Exec() {
        executeTester("testMathFloor1", p.math.floor(p.col("1")), "1", new Object[]{p.xs.doubleVal(1.7)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathFmod2Exec() {
        executeTester("testMathFmod2", p.math.fmod(p.col("1"), p.col("2")), "1", new Object[]{p.xs.doubleVal(10), p.xs.doubleVal(3)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathFrexp1Exec() {
        executeTester("testMathFrexp1", p.math.frexp(p.col("1")), "(xs:double(\"0.625\"), 4)", new Object[]{p.xs.doubleVal(10)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathLdexp2Exec() {
        executeTester("testMathLdexp2", p.math.ldexp(p.col("1"), p.col("2")), "1364.992", new Object[]{p.xs.doubleVal(1.333), p.xs.integer(10)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathLog1Exec() {
        executeTester("testMathLog1", p.math.log(p.col("1")), "6.90775527898214", new Object[]{p.xs.doubleVal(1000)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathLog101Exec() {
        executeTester("testMathLog101", p.math.log10(p.col("1")), "3", new Object[]{p.xs.doubleVal(1000)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathMedian1Exist() {
        executeTester("testMathMedian1", p.math.median(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.doubleVal(1.2)});
    }

    @Test
    public void testMathModf1Exec() {
        executeTester("testMathModf1", p.math.modf(p.col("1")), "(xs:double(\"0.333\"), xs:double(\"1\"))", new Object[]{p.xs.doubleVal(1.333)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathPi0Exist() {
        executeTester("testMathPi0", p.math.pi(), null, null, null);
    }

    @Test
    public void testMathPow2Exec() {
        executeTester("testMathPow2", p.math.pow(p.col("1"), p.col("2")), "1024", new Object[]{p.xs.doubleVal(2), p.xs.doubleVal(10)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathRadians1Exec() {
        executeTester("testMathRadians1", p.math.radians(p.col("1")), "1.5707963267949", new Object[]{p.xs.doubleVal(90)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathSin1Exec() {
        executeTester("testMathSin1", p.math.sin(p.col("1")), "0.928959715003869", new Object[]{p.xs.doubleVal(1.95)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathSinh1Exec() {
        executeTester("testMathSinh1", p.math.sinh(p.col("1")), "3.44320675450139", new Object[]{p.xs.doubleVal(1.95)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathSqrt1Exec() {
        executeTester("testMathSqrt1", p.math.sqrt(p.col("1")), "2", new Object[]{p.xs.doubleVal(4)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathStddev1Exist() {
        executeTester("testMathStddev1", p.math.stddev(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.doubleVal(1.2)});
    }

    @Test
    public void testMathStddevP1Exist() {
        executeTester("testMathStddevP1", p.math.stddevP(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.doubleVal(1.2)});
    }

    @Test
    public void testMathTan1Exec() {
        executeTester("testMathTan1", p.math.tan(p.col("1")), "0.760905351982977", new Object[]{p.xs.doubleVal(19.5)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathTanh1Exec() {
        executeTester("testMathTanh1", p.math.tanh(p.col("1")), "0.739783051274004", new Object[]{p.xs.doubleVal(0.95)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathTrunc1Exec() {
        executeTester("testMathTrunc1", p.math.trunc(p.col("1")), "123", new Object[]{p.xs.doubleVal(123.456)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathTrunc2Exec() {
        executeTester("testMathTrunc2", p.math.trunc(p.col("1"), p.col("2")), "123.45", new Object[]{p.xs.doubleVal(123.456), p.xs.integer(2)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testMathVariance1Exist() {
        executeTester("testMathVariance1", p.math.variance(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.doubleVal(1.2)});
    }

    @Test
    public void testMathVarianceP1Exist() {
        executeTester("testMathVarianceP1", p.math.varianceP(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.doubleVal(1.2)});
    }

    @Test
    public void testRdfLangString2Exec() {
        executeTester("testRdfLangString2", p.rdf.langString("abc", "en"), "rdf:langString(\"abc\", \"en\")", null, null);
    }

    @Test
    public void testRdfLangStringLanguage1Exist() {
        executeTester("testRdfLangStringLanguage1", p.rdf.langStringLanguage(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.rdf.langString("abc", "en")});
    }

    @Test
    public void testSemBnode0Exist() {
        executeTester("testSemBnode0", p.sem.bnode(), null, new Object[]{}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSemBnode1Exist() {
        executeTester("testSemBnode1", p.sem.bnode(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc")});
    }

    @Test
    public void testSemCoalesce2Exec() {
        executeTester("testSemCoalesce2", p.sem.coalesce(p.col("1"), p.col("2")), "\"a\"", new Object[]{p.xs.string("a"), p.xs.string("b")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSemCoalesce3Exec() {
        executeTester("testSemCoalesce3", p.sem.coalesce(p.col("1"), p.col("2"), p.col("3")), "\"a\"", new Object[]{p.xs.string("a"), p.xs.string("b"), p.xs.string("c")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSemDatatype1Exec() {
        executeTester("testSemDatatype1", p.sem.datatype(p.col("1")), "sem:iri(\"http://www.w3.org/2001/XMLSchema#string\")", new Object[]{p.xs.string("a")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSemIf3Exec() {
        executeTester("testSemIf3", p.sem.ifExpr(p.col("1"), p.col("2"), p.col("3")), "\"a\"", new Object[]{p.xs.booleanVal(true), p.xs.string("a"), p.xs.string("b")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSemInvalid2Exist() {
        executeTester("testSemInvalid2", p.sem.invalid(p.col("1"), p.col("2")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc"), p.sem.iri("http://a/b")});
    }

    @Test
    public void testSemIri1Exec() {
        executeTester("testSemIri1", p.sem.iri("http://a/b"), "sem:iri(\"http://a/b\")", null, null);
    }

    @Test
    public void testSemIriToQName1Exec() {
        executeTester("testSemIriToQName1", p.sem.iriToQName(p.col("1")), "fn:QName(\"http://a/\",\"b\")", new Object[]{p.xs.string("http://a/b")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSemIsBlank1Exec() {
        executeTester("testSemIsBlank1", p.sem.isBlank(p.col("1")), "fn:false()", new Object[]{p.xs.doubleVal(1)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSemIsIRI1Exec() {
        executeTester("testSemIsIRI1", p.sem.isIRI(p.col("1")), "fn:false()", new Object[]{p.xs.doubleVal(1)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSemIsLiteral1Exec() {
        executeTester("testSemIsLiteral1", p.sem.isLiteral(p.col("1")), "fn:true()", new Object[]{p.xs.doubleVal(1)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSemIsNumeric1Exec() {
        executeTester("testSemIsNumeric1", p.sem.isNumeric(p.col("1")), "fn:false()", new Object[]{p.xs.string("a")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSemLang1Exec() {
        executeTester("testSemLang1", p.sem.lang(p.col("1")), "\"\"", new Object[]{p.xs.string("abc")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSemLangMatches2Exist() {
        executeTester("testSemLangMatches2", p.sem.langMatches(p.col("1"), p.col("2")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc"), p.xs.string("abc")});
    }

    @Test
    public void testSemQNameToIri1Exist() {
        executeTester("testSemQNameToIri1", p.sem.QNameToIri(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.QName("abc")});
    }

    @Test
    public void testSemRandom0Exist() {
        executeTester("testSemRandom0", p.sem.random(), null, null, null);
    }

    @Test
    public void testSemSameTerm2Exec() {
        executeTester("testSemSameTerm2", p.sem.sameTerm(p.col("1"), p.col("2")), "fn:true()", new Object[]{p.xs.doubleVal(1), p.xs.doubleVal(1)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSemTimezoneString1Exec() {
        executeTester("testSemTimezoneString1", p.sem.timezoneString(p.col("1")), "\"Z\"", new Object[]{p.xs.dateTime("2016-01-02T10:09:08Z")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSemTypedLiteral2Exist() {
        executeTester("testSemTypedLiteral2", p.sem.typedLiteral(p.col("1"), p.col("2")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc"), p.sem.iri("http://a/b")});
    }

    @Test
    public void testSemUnknown2Exist() {
        executeTester("testSemUnknown2", p.sem.unknown(p.col("1"), p.col("2")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc"), p.sem.iri("http://a/b")});
    }

    @Test
    public void testSemUuid0Exist() {
        executeTester("testSemUuid0", p.sem.uuid(), null, null, null);
    }

    @Test
    public void testSemUuidString0Exist() {
        executeTester("testSemUuidString0", p.sem.uuidString(), null, null, null);
    }

    @Test
    public void testSpellDoubleMetaphone1Exec() {
        executeTester("testSpellDoubleMetaphone1", p.spell.doubleMetaphone(p.col("1")), "(\"smo\", \"xmt\")", new Object[]{p.xs.string("smith")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSpellLevenshteinDistance2Exec() {
        executeTester("testSpellLevenshteinDistance2", p.spell.levenshteinDistance(p.col("1"), p.col("2")), "1", new Object[]{p.xs.string("cat"), p.xs.string("cats")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSpellRomanize1Exist() {
        executeTester("testSpellRomanize1", p.spell.romanize(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc")});
    }

    @Test
    public void testSqlBitLength0Exist() {
        executeTester("testSqlBitLength0", p.sql.bitLength(), null, new Object[]{}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSqlBitLength1Exist() {
        executeTester("testSqlBitLength1", p.sql.bitLength(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc")});
    }

    @Test
    public void testSqlInsert4Exec() {
        executeTester("testSqlInsert4", p.sql.insert(p.col("1"), p.col("2"), p.col("3"), p.col("4")), "\"abcdef\"", new Object[]{p.xs.string("axxxf"), p.xs.doubleVal(2), p.xs.doubleVal(3), p.xs.string("bcde")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSqlInstr2Exec() {
        executeTester("testSqlInstr2", p.sql.instr(p.col("1"), p.col("2")), "3", new Object[]{p.xs.string("abcde"), p.xs.string("cd")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSqlLeft2Exec() {
        executeTester("testSqlLeft2", p.sql.left(p.col("1"), p.col("2")), "\"abc\"", new Object[]{p.xs.string("abcde"), p.xs.doubleVal(3)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSqlLtrim1Exist() {
        executeTester("testSqlLtrim1", p.sql.ltrim(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc")});
    }

    @Test
    public void testSqlLtrim2Exist() {
        executeTester("testSqlLtrim2", p.sql.ltrim(p.col("1"), p.col("2")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc"), p.xs.string("abc")});
    }

    @Test
    public void testSqlOctetLength0Exist() {
        executeTester("testSqlOctetLength0", p.sql.octetLength(), null, new Object[]{}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSqlOctetLength1Exist() {
        executeTester("testSqlOctetLength1", p.sql.octetLength(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc")});
    }

    @Test
    public void testSqlRand0Exist() {
        executeTester("testSqlRand0", p.sql.rand(), null, new Object[]{}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSqlRand1Exist() {
        executeTester("testSqlRand1", p.sql.rand(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.unsignedLong(1)});
    }

    @Test
    public void testSqlRepeat2Exec() {
        executeTester("testSqlRepeat2", p.sql.repeat(p.col("1"), p.col("2")), "\"abcabc\"", new Object[]{p.xs.string("abc"), p.xs.doubleVal(2)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSqlRight2Exec() {
        executeTester("testSqlRight2", p.sql.right(p.col("1"), p.col("2")), "\"cde\"", new Object[]{p.xs.string("abcde"), p.xs.doubleVal(3)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSqlRtrim1Exist() {
        executeTester("testSqlRtrim1", p.sql.rtrim(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc")});
    }

    @Test
    public void testSqlRtrim2Exist() {
        executeTester("testSqlRtrim2", p.sql.rtrim(p.col("1"), p.col("2")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc"), p.xs.string("abc")});
    }

    @Test
    public void testSqlSign1Exec() {
        executeTester("testSqlSign1", p.sql.sign(p.col("1")), "-1", new Object[]{p.xs.doubleVal(-3)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testSqlSpace1Exist() {
        executeTester("testSqlSpace1", p.sql.space(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.doubleVal(1.2)});
    }

    @Test
    public void testSqlTrim1Exist() {
        executeTester("testSqlTrim1", p.sql.trim(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc")});
    }

    @Test
    public void testSqlTrim2Exist() {
        executeTester("testSqlTrim2", p.sql.trim(p.col("1"), p.col("2")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc"), p.xs.string("abc")});
    }

    @Test
    public void testXdmpAdd642Exec() {
        executeTester("testXdmpAdd642", p.xdmp.add64(p.col("1"), p.col("2")), "579", new Object[]{p.xs.unsignedLong(123), p.xs.unsignedLong(456)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpAnd642Exec() {
        executeTester("testXdmpAnd642", p.xdmp.and64(p.col("1"), p.col("2")), "2", new Object[]{p.xs.unsignedLong(255), p.xs.unsignedLong(2)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpBase64Decode1Exec() {
        executeTester("testXdmpBase64Decode1", p.xdmp.base64Decode(p.col("1")), "\"slings and arrows of outrageous fortune\"", new Object[]{p.xs.string("c2xpbmdzIGFuZCBhcnJvd3Mgb2Ygb3V0cmFnZW91cyBmb3J0dW5l")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpBase64Encode1Exec() {
        executeTester("testXdmpBase64Encode1", p.xdmp.base64Encode(p.col("1")), "\"c2xpbmdzIGFuZCBhcnJvd3Mgb2Ygb3V0cmFnZW91cyBmb3J0dW5l\"", new Object[]{p.xs.string("slings and arrows of outrageous fortune")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpCastableAs3Exec() {
        executeTester("testXdmpCastableAs3", p.xdmp.castableAs(p.col("1"), p.col("2"), p.col("3")), "fn:true()", new Object[]{p.xs.string("http://www.w3.org/2001/XMLSchema"), p.xs.string("int"), p.xs.string("1")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpCrypt2Exec() {
        executeTester("testXdmpCrypt2", p.xdmp.crypt(p.col("1"), p.col("2")), "\"arQEnpM6JHR8vY4n3e5gr0\"", new Object[]{p.xs.string("123abc"), p.xs.string("admin")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpCrypt21Exist() {
        executeTester("testXdmpCrypt21", p.xdmp.crypt2(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc")});
    }

    @Test
    public void testXdmpDaynameFromDate1Exec() {
        executeTester("testXdmpDaynameFromDate1", p.xdmp.daynameFromDate(p.col("1")), "\"Saturday\"", new Object[]{p.xs.date("2016-01-02")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpDecodeFromNCName1Exec() {
        executeTester("testXdmpDecodeFromNCName1", p.xdmp.decodeFromNCName(p.col("1")), "\"A Name\"", new Object[]{p.xs.string("A_20_Name")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpDescribe1Exist() {
        executeTester("testXdmpDescribe1", p.xdmp.describe(p.col("1")), null, new Object[]{p.xs.string("123456")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpDescribe2Exist() {
        executeTester("testXdmpDescribe2", p.xdmp.describe(p.col("1"), p.col("2")), null, new Object[]{p.xs.string("123456"), p.xs.unsignedInt(2)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpDescribe3Exist() {
        executeTester("testXdmpDescribe3", p.xdmp.describe(p.col("1"), p.col("2"), p.col("3")), null, new Object[]{p.xs.string("123456"), p.xs.unsignedInt(2), p.xs.unsignedInt(3)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpDiacriticLess1Exist() {
        executeTester("testXdmpDiacriticLess1", p.xdmp.diacriticLess(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc")});
    }

    @Test
    public void testXdmpEncodeForNCName1Exec() {
        executeTester("testXdmpEncodeForNCName1", p.xdmp.encodeForNCName(p.col("1")), "\"A_20_Name\"", new Object[]{p.xs.string("A Name")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpFormatNumber1Exec() {
        executeTester("testXdmpFormatNumber1", p.xdmp.formatNumber(p.col("1")), "\"9\"", new Object[]{p.xs.doubleVal(9)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpFormatNumber2Exec() {
        executeTester("testXdmpFormatNumber2", p.xdmp.formatNumber(p.col("1"), p.col("2")), "\"9\"", new Object[]{p.xs.doubleVal(9), p.xs.string("W")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpFormatNumber3Exec() {
        executeTester("testXdmpFormatNumber3", p.xdmp.formatNumber(p.col("1"), p.col("2"), p.col("3")), "\"NINE\"", new Object[]{p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpFormatNumber4Exec() {
        executeTester("testXdmpFormatNumber4", p.xdmp.formatNumber(p.col("1"), p.col("2"), p.col("3"), p.col("4")), "\"NINE\"", new Object[]{p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string("")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpFormatNumber5Exec() {
        executeTester("testXdmpFormatNumber5", p.xdmp.formatNumber(p.col("1"), p.col("2"), p.col("3"), p.col("4"), p.col("5")), "\"NINE\"", new Object[]{p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string("")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpFormatNumber6Exec() {
        executeTester("testXdmpFormatNumber6", p.xdmp.formatNumber(p.col("1"), p.col("2"), p.col("3"), p.col("4"), p.col("5"), p.col("6")), "\"NINE\"", new Object[]{p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string(""), p.xs.string("")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpFormatNumber7Exec() {
        executeTester("testXdmpFormatNumber7", p.xdmp.formatNumber(p.col("1"), p.col("2"), p.col("3"), p.col("4"), p.col("5"), p.col("6"), p.col("7")), "\"NINE\"", new Object[]{p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string(""), p.xs.string(""), p.xs.string(",")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpFormatNumber8Exec() {
        executeTester("testXdmpFormatNumber8", p.xdmp.formatNumber(p.col("1"), p.col("2"), p.col("3"), p.col("4"), p.col("5"), p.col("6"), p.col("7"), p.col("8")), "\"NINE\"", new Object[]{p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string(""), p.xs.string(""), p.xs.string(","), p.xs.integer(3)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpGetCurrentUser0Exist() {
        executeTester("testXdmpGetCurrentUser0", p.xdmp.getCurrentUser(), null, null, null);
    }

    @Test
    public void testXdmpHash321Exec() {
        executeTester("testXdmpHash321", p.xdmp.hash32(p.col("1")), "4229403455", new Object[]{p.xs.string("abc")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpHash641Exec() {
        executeTester("testXdmpHash641", p.xdmp.hash64(p.col("1")), "xs:unsignedLong(\"13056678368508584127\")", new Object[]{p.xs.string("abc")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpHexToInteger1Exec() {
        executeTester("testXdmpHexToInteger1", p.xdmp.hexToInteger(p.col("1")), "1311768467294899695", new Object[]{p.xs.string("1234567890abcdef")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpHmacMd52Exec() {
        executeTester("testXdmpHmacMd52", p.xdmp.hmacMd5(p.col("1"), p.col("2")), "\"debda77b7cc3e7a10ee70104e6717a6b\"", new Object[]{p.xs.string("abc"), p.xs.string("def")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpHmacMd53Exec() {
        executeTester("testXdmpHmacMd53", p.xdmp.hmacMd5(p.col("1"), p.col("2"), p.col("3")), "\"3r2ne3zD56EO5wEE5nF6aw==\"", new Object[]{p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpHmacSha12Exec() {
        executeTester("testXdmpHmacSha12", p.xdmp.hmacSha1(p.col("1"), p.col("2")), "\"12554eabbaf7e8e12e4737020f987ca7901016e5\"", new Object[]{p.xs.string("abc"), p.xs.string("def")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpHmacSha13Exec() {
        executeTester("testXdmpHmacSha13", p.xdmp.hmacSha1(p.col("1"), p.col("2"), p.col("3")), "\"ElVOq7r36OEuRzcCD5h8p5AQFuU=\"", new Object[]{p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpHmacSha2562Exec() {
        executeTester("testXdmpHmacSha2562", p.xdmp.hmacSha256(p.col("1"), p.col("2")), "\"20ebc0f09344470134f35040f63ea98b1d8e414212949ee5c500429d15eab081\"", new Object[]{p.xs.string("abc"), p.xs.string("def")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpHmacSha2563Exec() {
        executeTester("testXdmpHmacSha2563", p.xdmp.hmacSha256(p.col("1"), p.col("2"), p.col("3")), "\"IOvA8JNERwE081BA9j6pix2OQUISlJ7lxQBCnRXqsIE=\"", new Object[]{p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpHmacSha5122Exec() {
        executeTester("testXdmpHmacSha5122", p.xdmp.hmacSha512(p.col("1"), p.col("2")), "\"bf93c3deee1eb6660ec00820a285327b3e8b775f641fd7f2ea321b6a241afe7b49a5cca81d2e8e1d206bd3379530e2d9ad3a7b2cc54ca66ea3352ebfee3862e5\"", new Object[]{p.xs.string("abc"), p.xs.string("def")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpHmacSha5123Exec() {
        executeTester("testXdmpHmacSha5123", p.xdmp.hmacSha512(p.col("1"), p.col("2"), p.col("3")), "\"v5PD3u4etmYOwAggooUyez6Ld19kH9fy6jIbaiQa/ntJpcyoHS6OHSBr0zeVMOLZrTp7LMVMpm6jNS6/7jhi5Q==\"", new Object[]{p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpInitcap1Exec() {
        executeTester("testXdmpInitcap1", p.xdmp.initcap(p.col("1")), "\"Abc\"", new Object[]{p.xs.string("abc")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpIntegerToHex1Exec() {
        executeTester("testXdmpIntegerToHex1", p.xdmp.integerToHex(p.col("1")), "\"7b\"", new Object[]{p.xs.integer(123)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpIntegerToOctal1Exec() {
        executeTester("testXdmpIntegerToOctal1", p.xdmp.integerToOctal(p.col("1")), "\"173\"", new Object[]{p.xs.integer(123)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpKeyFromQName1Exist() {
        executeTester("testXdmpKeyFromQName1", p.xdmp.keyFromQName(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.QName("abc")});
    }

    @Test
    public void testXdmpLshift642Exec() {
        executeTester("testXdmpLshift642", p.xdmp.lshift64(p.col("1"), p.col("2")), "1020", new Object[]{p.xs.unsignedLong(255), p.xs.longVal(2)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpMd51Exec() {
        executeTester("testXdmpMd51", p.xdmp.md5(p.col("1")), "\"900150983cd24fb0d6963f7d28e17f72\"", new Object[]{p.xs.string("abc")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpMd52Exec() {
        executeTester("testXdmpMd52", p.xdmp.md5(p.col("1"), p.col("2")), "\"kAFQmDzST7DWlj99KOF/cg==\"", new Object[]{p.xs.string("abc"), p.xs.string("base64")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpMonthNameFromDate1Exec() {
        executeTester("testXdmpMonthNameFromDate1", p.xdmp.monthNameFromDate(p.col("1")), "\"January\"", new Object[]{p.xs.date("2016-01-02")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpMul642Exec() {
        executeTester("testXdmpMul642", p.xdmp.mul64(p.col("1"), p.col("2")), "56088", new Object[]{p.xs.unsignedLong(123), p.xs.unsignedLong(456)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpNot641Exec() {
        executeTester("testXdmpNot641", p.xdmp.not64(p.col("1")), "xs:unsignedLong(\"18446744073709551360\")", new Object[]{p.xs.unsignedLong(255)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpOctalToInteger1Exec() {
        executeTester("testXdmpOctalToInteger1", p.xdmp.octalToInteger(p.col("1")), "2739128", new Object[]{p.xs.string("12345670")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpOr642Exec() {
        executeTester("testXdmpOr642", p.xdmp.or64(p.col("1"), p.col("2")), "255", new Object[]{p.xs.unsignedLong(255), p.xs.unsignedLong(2)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpParseDateTime2Exec() {
        executeTester("testXdmpParseDateTime2", p.xdmp.parseDateTime(p.col("1"), p.col("2")), "xs:dateTime(\"2016-01-06T20:13:50.874-05:00\")", new Object[]{p.xs.string("[Y0001]-[M01]-[D01]T[h01]:[m01]:[s01].[f1][Z]"), p.xs.string("2016-01-06T17:13:50.873594-08:00")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpParseYymmdd2Exec() {
        executeTester("testXdmpParseYymmdd2", p.xdmp.parseYymmdd(p.col("1"), p.col("2")), "xs:dateTime(\"2016-01-06T20:13:50.874-05:00\")", new Object[]{p.xs.string("yyyy-MM-ddThh:mm:ss.Sz"), p.xs.string("2016-01-06T17:13:50.873594-8.00")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpPosition2Exec() {
        executeTester("testXdmpPosition2", p.xdmp.position(p.col("1"), p.col("2")), "0", new Object[]{p.xs.string("abcdef"), p.xs.string("cd")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpPosition3Exec() {
        executeTester("testXdmpPosition3", p.xdmp.position(p.col("1"), p.col("2"), p.col("3")), "0", new Object[]{p.xs.string("abcdef"), p.xs.string("cd"), p.xs.string("http://marklogic.com/collation/")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpQNameFromKey1Exec() {
        executeTester("testXdmpQNameFromKey1", p.xdmp.QNameFromKey(p.col("1")), "fn:QName(\"http://a/b\",\"c\")", new Object[]{p.xs.string("{http://a/b}c")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpQuarterFromDate1Exec() {
        executeTester("testXdmpQuarterFromDate1", p.xdmp.quarterFromDate(p.col("1")), "1", new Object[]{p.xs.date("2016-01-02")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpRandom0Exist() {
        executeTester("testXdmpRandom0", p.xdmp.random(), null, new Object[]{}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpRandom1Exist() {
        executeTester("testXdmpRandom1", p.xdmp.random(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.unsignedLong(1)});
    }

    @Test
    public void testXdmpResolveUri2Exec() {
        executeTester("testXdmpResolveUri2", p.xdmp.resolveUri(p.col("1"), p.col("2")), "xs:anyURI(\"/a/b?c#d\")", new Object[]{p.xs.string("b?c#d"), p.xs.string("/a/x")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpRshift642Exec() {
        executeTester("testXdmpRshift642", p.xdmp.rshift64(p.col("1"), p.col("2")), "63", new Object[]{p.xs.unsignedLong(255), p.xs.longVal(2)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpSha11Exec() {
        executeTester("testXdmpSha11", p.xdmp.sha1(p.col("1")), "\"a9993e364706816aba3e25717850c26c9cd0d89d\"", new Object[]{p.xs.string("abc")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpSha12Exec() {
        executeTester("testXdmpSha12", p.xdmp.sha1(p.col("1"), p.col("2")), "\"qZk+NkcGgWq6PiVxeFDCbJzQ2J0=\"", new Object[]{p.xs.string("abc"), p.xs.string("base64")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpSha2561Exec() {
        executeTester("testXdmpSha2561", p.xdmp.sha256(p.col("1")), "\"ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad\"", new Object[]{p.xs.string("abc")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpSha2562Exec() {
        executeTester("testXdmpSha2562", p.xdmp.sha256(p.col("1"), p.col("2")), "\"ungWv48Bz+pBQUDeXa4iI7ADYaOWF3qctBD/YfIAFa0=\"", new Object[]{p.xs.string("abc"), p.xs.string("base64")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpSha3841Exec() {
        executeTester("testXdmpSha3841", p.xdmp.sha384(p.col("1")), "\"cb00753f45a35e8bb5a03d699ac65007272c32ab0eded1631a8b605a43ff5bed8086072ba1e7cc2358baeca134c825a7\"", new Object[]{p.xs.string("abc")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpSha3842Exec() {
        executeTester("testXdmpSha3842", p.xdmp.sha384(p.col("1"), p.col("2")), "\"ywB1P0WjXou1oD1pmsZQBycsMqsO3tFjGotgWkP/W+2AhgcroefMI1i67KE0yCWn\"", new Object[]{p.xs.string("abc"), p.xs.string("base64")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpSha5121Exec() {
        executeTester("testXdmpSha5121", p.xdmp.sha512(p.col("1")), "\"ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f\"", new Object[]{p.xs.string("abc")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpSha5122Exec() {
        executeTester("testXdmpSha5122", p.xdmp.sha512(p.col("1"), p.col("2")), "\"3a81oZNherrMQXNJriBBMRLm+k6JqX6iCp7u5ktV05ohkpkqJ0/BqDa6PCOj/uu9RU1EI2Q86A4qmslPpUyknw==\"", new Object[]{p.xs.string("abc"), p.xs.string("base64")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpStep642Exec() {
        executeTester("testXdmpStep642", p.xdmp.step64(p.col("1"), p.col("2")), "8966314677", new Object[]{p.xs.unsignedLong(123), p.xs.unsignedLong(456)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpStrftime2Exec() {
        executeTester("testXdmpStrftime2", p.xdmp.strftime(p.col("1"), p.col("2")), "\"Wed, 06 Jan 2016 20:13:50\"", new Object[]{p.xs.string("%a, %d %b %Y %H:%M:%S"), p.xs.dateTime("2016-01-06T17:13:50.873594-08:00")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpTimestampToWallclock1Exist() {
        executeTester("testXdmpTimestampToWallclock1", p.xdmp.timestampToWallclock(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.unsignedLong(1)});
    }

    @Test
    public void testXdmpToJson1Exist() {
        executeTester("testXdmpToJson1", p.xdmp.toJson(p.col("1")), null, new Object[]{}, new ItemSeqExpr[]{p.xs.string("abc")});
    }

    @Test
    public void testXdmpType1Exec() {
        executeTester("testXdmpType1", p.xdmp.type(p.col("1")), "fn:QName(\"http://www.w3.org/2001/XMLSchema\",\"string\")", new Object[]{p.xs.string("a")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpUrlDecode1Exec() {
        executeTester("testXdmpUrlDecode1", p.xdmp.urlDecode(p.col("1")), "\"a b\"", new Object[]{p.xs.string("a+b")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpUrlEncode1Exec() {
        executeTester("testXdmpUrlEncode1", p.xdmp.urlEncode(p.col("1")), "\"a+b\"", new Object[]{p.xs.string("a b")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpWallclockToTimestamp1Exist() {
        executeTester("testXdmpWallclockToTimestamp1", p.xdmp.wallclockToTimestamp(p.col("1")), null, new Object[]{p.xs.dateTime("2016-01-06T17:13:50.873594-08:00")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpWeekdayFromDate1Exec() {
        executeTester("testXdmpWeekdayFromDate1", p.xdmp.weekdayFromDate(p.col("1")), "6", new Object[]{p.xs.date("2016-01-02")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpWeekFromDate1Exec() {
        executeTester("testXdmpWeekFromDate1", p.xdmp.weekFromDate(p.col("1")), "53", new Object[]{p.xs.date("2016-01-02")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpXor642Exec() {
        executeTester("testXdmpXor642", p.xdmp.xor64(p.col("1"), p.col("2")), "253", new Object[]{p.xs.unsignedLong(255), p.xs.unsignedLong(2)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXdmpYeardayFromDate1Exec() {
        executeTester("testXdmpYeardayFromDate1", p.xdmp.yeardayFromDate(p.col("1")), "2", new Object[]{p.xs.date("2016-01-02")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXsAnyURI1Exec() {
        executeTester("testXsAnyURI1", p.xs.anyURI("http://a/b?c#d"), "xs:anyURI(\"http://a/b?c#d\")", null, null);
    }

    @Test
    public void testXsBoolean1Exec() {
        executeTester("testXsBoolean1", p.xs.booleanVal(true), "fn:true()", null, null);
    }

    @Test
    public void testXsByte1Exec() {
        executeTester("testXsByte1", p.xs.byteVal((byte) 1), "1", null, null);
    }

    @Test
    public void testXsDate1Exec() {
        executeTester("testXsDate1", p.xs.date("2016-01-02"), "xs:date(\"2016-01-02\")", null, null);
    }

    @Test
    public void testXsDateTime1Exec() {
        executeTester("testXsDateTime1", p.xs.dateTime("2016-01-02T10:09:08Z"), "xs:dateTime(\"2016-01-02T10:09:08Z\")", null, null);
    }

    @Test
    public void testXsDayTimeDuration1Exec() {
        executeTester("testXsDayTimeDuration1", p.xs.dayTimeDuration("P3DT4H5M6S"), "xs:dayTimeDuration(\"P3DT4H5M6S\")", null, null);
    }

    @Test
    public void testXsDecimal1Exec() {
        executeTester("testXsDecimal1", p.xs.decimal(1.2), "1.2", null, null);
    }

    @Test
    public void testXsDouble1Exec() {
        executeTester("testXsDouble1", p.xs.doubleVal(1.2), "1.2", null, null);
    }

    @Test
    public void testXsDuration1Exec() {
        executeTester("testXsDuration1", p.xs.duration(p.col("1")), "xs:duration(\"P1Y2M\")", new Object[]{p.xs.string("P1Y2M")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXsFloat1Exec() {
        executeTester("testXsFloat1", p.xs.floatVal((float) 1), "1", null, null);
    }

    @Test
    public void testXsGDay1Exec() {
        executeTester("testXsGDay1", p.xs.gDay("---02"), "xs:gDay(\"---02\")", null, null);
    }

    @Test
    public void testXsGMonth1Exec() {
        executeTester("testXsGMonth1", p.xs.gMonth("--01"), "xs:gMonth(\"--01\")", null, null);
    }

    @Test
    public void testXsGMonthDay1Exec() {
        executeTester("testXsGMonthDay1", p.xs.gMonthDay("--01-02"), "xs:gMonthDay(\"--01-02\")", null, null);
    }

    @Test
    public void testXsGYear1Exec() {
        executeTester("testXsGYear1", p.xs.gYear("2016"), "xs:gYear(\"2016\")", null, null);
    }

    @Test
    public void testXsGYearMonth1Exec() {
        executeTester("testXsGYearMonth1", p.xs.gYearMonth("2016-01"), "xs:gYearMonth(\"2016-01\")", null, null);
    }

    @Test
    public void testXsInt1Exec() {
        executeTester("testXsInt1", p.xs.intVal(1), "1", null, null);
    }

    @Test
    public void testXsInteger1Exec() {
        executeTester("testXsInteger1", p.xs.integer(1), "1", null, null);
    }

    @Test
    public void testXsLanguage1Exec() {
        executeTester("testXsLanguage1", p.xs.language(p.col("1")), "xs:language(\"en-US\")", new Object[]{p.xs.string("en-US")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXsLong1Exec() {
        executeTester("testXsLong1", p.xs.longVal(1), "1", null, null);
    }

    @Test
    public void testXsName1Exec() {
        executeTester("testXsName1", p.xs.Name(p.col("1")), "xs:Name(\"a:b:c\")", new Object[]{p.xs.string("a:b:c")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXsNCName1Exec() {
        executeTester("testXsNCName1", p.xs.NCName(p.col("1")), "xs:NCName(\"a-b-c\")", new Object[]{p.xs.string("a-b-c")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXsNegativeInteger1Exec() {
        executeTester("testXsNegativeInteger1", p.xs.negativeInteger(p.col("1")), "-1", new Object[]{p.xs.doubleVal(-1)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXsNMTOKEN1Exec() {
        executeTester("testXsNMTOKEN1", p.xs.NMTOKEN(p.col("1")), "xs:NMTOKEN(\"a:b:c\")", new Object[]{p.xs.string("a:b:c")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXsNonNegativeInteger1Exec() {
        executeTester("testXsNonNegativeInteger1", p.xs.nonNegativeInteger(p.col("1")), "0", new Object[]{p.xs.string("0")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXsNonPositiveInteger1Exec() {
        executeTester("testXsNonPositiveInteger1", p.xs.nonPositiveInteger(p.col("1")), "0", new Object[]{p.xs.string("0")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXsNormalizedString1Exec() {
        executeTester("testXsNormalizedString1", p.xs.normalizedString(p.col("1")), "xs:normalizedString(\"a b c\")", new Object[]{p.xs.string("a b c")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXsPositiveInteger1Exec() {
        executeTester("testXsPositiveInteger1", p.xs.positiveInteger(p.col("1")), "1", new Object[]{p.xs.doubleVal(1)}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXsQName1Exec() {
        executeTester("testXsQName1", p.xs.QName("abc"), "fn:QName(\"\",\"abc\")", null, null);
    }

    @Test
    public void testXsShort1Exec() {
        executeTester("testXsShort1", p.xs.shortVal((short) 1), "1", null, null);
    }

    @Test
    public void testXsString1Exec() {
        executeTester("testXsString1", p.xs.string("abc"), "\"abc\"", null, null);
    }

    @Test
    public void testXsTime1Exec() {
        executeTester("testXsTime1", p.xs.time("10:09:08Z"), "xs:time(\"10:09:08Z\")", null, null);
    }

    @Test
    public void testXsToken1Exec() {
        executeTester("testXsToken1", p.xs.token(p.col("1")), "xs:token(\"a b c\")", new Object[]{p.xs.string("a b c")}, new ItemSeqExpr[]{});
    }

    @Test
    public void testXsUnsignedByte1Exec() {
        executeTester("testXsUnsignedByte1", p.xs.unsignedByte((byte) 1), "1", null, null);
    }

    @Test
    public void testXsUnsignedInt1Exec() {
        executeTester("testXsUnsignedInt1", p.xs.unsignedInt(1), "1", null, null);
    }

    @Test
    public void testXsUnsignedLong1Exec() {
        executeTester("testXsUnsignedLong1", p.xs.unsignedLong(1), "1", null, null);
    }

    @Test
    public void testXsUnsignedShort1Exec() {
        executeTester("testXsUnsignedShort1", p.xs.unsignedShort((short) 1), "1", null, null);
    }

    @Test
    public void testXsUntypedAtomic1Exec() {
        executeTester("testXsUntypedAtomic1", p.xs.untypedAtomic("abc"), "\"abc\"", null, null);
    }

    @Test
    public void testXsYearMonthDuration1Exec() {
        executeTester("testXsYearMonthDuration1", p.xs.yearMonthDuration("P1Y2M"), "xs:yearMonthDuration(\"P1Y2M\")", null, null);
    }

    @Test
    public void testOpAdd2Exist() {
        executeTester("testOpAdd2", p.add(p.col("1"), p.col("2")), null, new Object[]{p.xs.intVal(1), p.xs.intVal(2)}, null);
    }

    @Test
    public void testOpAdd3Exist() {
        executeTester("testOpAdd3", p.add(p.col("1"), p.col("2"), p.col("3")), null, new Object[]{p.xs.intVal(1), p.xs.intVal(2), p.xs.intVal(3)}, null);
    }

    @Test
    public void testOpAnd2Exist() {
        executeTester("testOpAnd2", p.and(p.col("1"), p.col("2")), null, new Object[]{p.xs.booleanVal(true), p.xs.booleanVal(true)}, null);
    }

    @Test
    public void testOpAnd3Exist() {
        executeTester("testOpAnd3", p.and(p.col("1"), p.col("2"), p.col("3")), null, new Object[]{p.xs.booleanVal(true), p.xs.booleanVal(true), p.xs.booleanVal(true)}, null);
    }

    @Test
    public void testOpDivide2Exist() {
        executeTester("testOpDivide2", p.divide(p.col("1"), p.col("2")), null, new Object[]{p.xs.intVal(6), p.xs.intVal(2)}, null);
    }

    @Test
    public void testOpEq2Exist() {
        executeTester("testOpEq2", p.eq(p.col("1"), p.col("2")), null, new Object[]{p.xs.intVal(1), p.xs.intVal(1)}, null);
    }

    @Test
    public void testOpGe2Exist() {
        executeTester("testOpGe2", p.ge(p.col("1"), p.col("2")), null, new Object[]{p.xs.intVal(1), p.xs.intVal(1)}, null);
    }

    @Test
    public void testOpGt2Exist() {
        executeTester("testOpGt2", p.gt(p.col("1"), p.col("2")), null, new Object[]{p.xs.intVal(2), p.xs.intVal(1)}, null);
    }

    @Test
    public void testOpLe2Exist() {
        executeTester("testOpLe2", p.le(p.col("1"), p.col("2")), null, new Object[]{p.xs.intVal(1), p.xs.intVal(1)}, null);
    }

    @Test
    public void testOpLt2Exist() {
        executeTester("testOpLt2", p.lt(p.col("1"), p.col("2")), null, new Object[]{p.xs.intVal(1), p.xs.intVal(2)}, null);
    }

    @Test
    public void testOpMultiply2Exist() {
        executeTester("testOpMultiply2", p.multiply(p.col("1"), p.col("2")), null, new Object[]{p.xs.intVal(2), p.xs.intVal(3)}, null);
    }

    @Test
    public void testOpMultiply3Exist() {
        executeTester("testOpMultiply3", p.multiply(p.col("1"), p.col("2"), p.col("3")), null, new Object[]{p.xs.intVal(2), p.xs.intVal(3), p.xs.intVal(4)}, null);
    }

    @Test
    public void testOpNe2Exist() {
        executeTester("testOpNe2", p.ne(p.col("1"), p.col("2")), null, new Object[]{p.xs.intVal(1), p.xs.intVal(2)}, null);
    }

    @Test
    public void testOpNot1Exist() {
        executeTester("testOpNot1", p.not(p.col("1")), null, new Object[]{p.xs.booleanVal(false)}, null);
    }

    @Test
    public void testOpOr2Exist() {
        executeTester("testOpOr2", p.or(p.col("1"), p.col("2")), null, new Object[]{p.xs.booleanVal(false), p.xs.booleanVal(true)}, null);
    }

    @Test
    public void testOpOr3Exist() {
        executeTester("testOpOr3", p.or(p.col("1"), p.col("2"), p.col("3")), null, new Object[]{p.xs.booleanVal(false), p.xs.booleanVal(true), p.xs.booleanVal(false)}, null);
    }

    @Test
    public void testOpSubtract2Exist() {
        executeTester("testOpSubtract2", p.subtract(p.col("1"), p.col("2")), null, new Object[]{p.xs.intVal(3), p.xs.intVal(2)}, null);
    }
}