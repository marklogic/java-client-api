/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.test;

import com.marklogic.client.io.Format;
import com.marklogic.client.test.junit5.RequiresML11;
import com.marklogic.client.test.junit5.RequiresML11OrLower;
import com.marklogic.client.test.junit5.RequiresML12;
import com.marklogic.client.type.ServerExpression;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// IMPORTANT: Do not edit. This file is generated.
// Exception - some of these tests cannot pass on ML <= 10. Those have been modified to not run unless ML is >= 11.
// Other tests have been disabled to not run on ML >= 12. Getting those tests to pass would require running the Optic
// code generator workspace on src/main/java, which would then prevent many of the tests from passing on ML <= 11.
public class PlanGeneratedTest extends PlanGeneratedBase {

    @Test
    public void testCtsBox4Exec() {
        executeTester("testCtsBox4", p.cts.box(p.col("1"), p.col("2"), p.col("3"), p.col("4")), false, "cts:box", null, null, "[1, 2, 3, 4]", new ServerExpression[]{ p.xs.doubleVal(1), p.xs.doubleVal(2), p.xs.doubleVal(3), p.xs.doubleVal(4) });
    }

    @Test
    public void testCtsBoxEast1Exec() {
        executeTester("testCtsBoxEast1", p.cts.boxEast(p.col("1")), false, null, null, null, "4", new ServerExpression[]{ p.cts.box(1, 2, 3, 4) });
    }

    @Test
    public void testCtsBoxNorth1Exec() {
        executeTester("testCtsBoxNorth1", p.cts.boxNorth(p.col("1")), false, null, null, null, "3", new ServerExpression[]{ p.cts.box(1, 2, 3, 4) });
    }

    @Test
    public void testCtsBoxSouth1Exec() {
        executeTester("testCtsBoxSouth1", p.cts.boxSouth(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.cts.box(1, 2, 3, 4) });
    }

    @Test
    public void testCtsBoxWest1Exec() {
        executeTester("testCtsBoxWest1", p.cts.boxWest(p.col("1")), false, null, null, null, "2", new ServerExpression[]{ p.cts.box(1, 2, 3, 4) });
    }

    @Test
    public void testCtsCircle2Exec() {
        executeTester("testCtsCircle2", p.cts.circle(p.col("1"), p.col("2")), false, "cts:circle", null, null, "@1.2 1,2", new ServerExpression[]{ p.xs.doubleVal(1.2), p.cts.point(1, 2) });
    }

    @Test
    public void testCtsPartOfSpeech1Exec() {
        executeTester("testCtsPartOfSpeech1", p.cts.partOfSpeech(p.col("1")), false, null, null, null, "", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testCtsPoint2Exec() {
        executeTester("testCtsPoint2", p.cts.point(p.col("1"), p.col("2")), false, "cts:point", null, null, "1,2", new ServerExpression[]{ p.xs.doubleVal(1), p.xs.doubleVal(2) });
    }

    @Test
    public void testCtsPointLatitude1Exec() {
        executeTester("testCtsPointLatitude1", p.cts.pointLatitude(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.cts.point(1, 2) });
    }

    @Test
    public void testCtsPointLongitude1Exec() {
        executeTester("testCtsPointLongitude1", p.cts.pointLongitude(p.col("1")), false, null, null, null, "2", new ServerExpression[]{ p.cts.point(1, 2) });
    }

    @Test
    public void testCtsStem1Exec() {
        executeTester("testCtsStem1", p.cts.stem(p.col("1")), false, null, null, null, "run", new ServerExpression[]{ p.xs.string("ran") });
    }

    @Test
    public void testCtsStem2Exec() {
        executeTester("testCtsStem2", p.cts.stem(p.col("1"), p.col("2")), false, null, null, null, "run", new ServerExpression[]{ p.xs.string("ran"), p.xs.string("en") });
    }

    @Test
    public void testCtsTokenize1Exec() {
        executeTester("testCtsTokenize1", p.cts.tokenize(p.col("1")), false, null, null, Format.JSON, "[\"a\", \"-\", \"b\", \" \", \"c\"]", new ServerExpression[]{ p.xs.string("a-b c") });
    }

    @Test
    public void testCtsTokenize2Exec() {
        executeTester("testCtsTokenize2", p.cts.tokenize(p.col("1"), p.col("2")), false, null, null, Format.JSON, "[\"a\", \"-\", \"b\", \" \", \"c\"]", new ServerExpression[]{ p.xs.string("a-b c"), p.xs.string("en") });
    }

    @Test
    public void testFnAbs1Exec() {
        executeTester("testFnAbs1", p.fn.abs(p.col("1")), false, null, null, null, "11", new ServerExpression[]{ p.xs.doubleVal(-11) });
    }

    @Test
    public void testFnAdjustDateTimeToTimezone1Exec() {
        executeTester("testFnAdjustDateTimeToTimezone1", p.fn.adjustDateTimeToTimezone(p.col("1")), true, "xs:dateTime", null, null, "2016-01-02T03:09:08-07:00", new ServerExpression[]{ p.xs.dateTime("2016-01-02T10:09:08Z") });
    }

    @Test
    public void testFnAdjustDateTimeToTimezone2Exec() {
        executeTester("testFnAdjustDateTimeToTimezone2", p.fn.adjustDateTimeToTimezone(p.col("1"), p.col("2")), true, "xs:dateTime", null, null, "2016-01-02T00:09:08-10:00", new ServerExpression[]{ p.xs.dateTime("2016-01-02T10:09:08Z"), p.xs.dayTimeDuration("-PT10H") });
    }

    @Test
    public void testFnAdjustDateToTimezone1Exec() {
        executeTester("testFnAdjustDateToTimezone1", p.fn.adjustDateToTimezone(p.col("1")), true, "xs:date", null, null, "2016-01-02-07:00", new ServerExpression[]{ p.xs.date("2016-01-02") });
    }

    @Test
    public void testFnAdjustDateToTimezone2Exec() {
        executeTester("testFnAdjustDateToTimezone2", p.fn.adjustDateToTimezone(p.col("1"), p.col("2")), true, "xs:date", null, null, "2016-01-02-10:00", new ServerExpression[]{ p.xs.date("2016-01-02"), p.xs.dayTimeDuration("-PT10H") });
    }

    @Test
    public void testFnAdjustTimeToTimezone1Exec() {
        executeTester("testFnAdjustTimeToTimezone1", p.fn.adjustTimeToTimezone(p.col("1")), true, "xs:time", null, null, "03:09:08-07:00", new ServerExpression[]{ p.xs.time("10:09:08Z") });
    }

    @Test
    public void testFnAdjustTimeToTimezone2Exec() {
        executeTester("testFnAdjustTimeToTimezone2", p.fn.adjustTimeToTimezone(p.col("1"), p.col("2")), true, "xs:time", null, null, "00:09:08-10:00", new ServerExpression[]{ p.xs.time("10:09:08Z"), p.xs.dayTimeDuration("-PT10H") });
    }

    @Test
    public void testFnAnalyzeString2Exec() {
        executeTester("testFnAnalyzeString2", p.fn.analyzeString(p.col("1"), p.col("2")), false, null, "element", Format.XML, "<s:analyze-string-result xmlns:s=\"http://www.w3.org/2005/xpath-functions\"><s:non-match>aXb</s:non-match><s:match>y</s:match><s:non-match>c</s:non-match></s:analyze-string-result>", new ServerExpression[]{ p.xs.string("aXbyc"), p.xs.string("[xy]") });
    }

    @Test
    public void testFnAnalyzeString3Exec() {
        executeTester("testFnAnalyzeString3", p.fn.analyzeString(p.col("1"), p.col("2"), p.col("3")), false, null, "element", Format.XML, "<s:analyze-string-result xmlns:s=\"http://www.w3.org/2005/xpath-functions\"><s:non-match>a</s:non-match><s:match>X</s:match><s:non-match>b</s:non-match><s:match>y</s:match><s:non-match>c</s:non-match></s:analyze-string-result>", new ServerExpression[]{ p.xs.string("aXbyc"), p.xs.string("[xy]"), p.xs.string("i") });
    }

    @Test
    public void testFnAvg1Exec() {
        executeTester("testFnAvg1", p.fn.avg(p.col("1")), false, null, null, null, "5", new ServerExpression[]{ p.xs.doubleSeq(p.xs.doubleVal(2), p.xs.doubleVal(4), p.xs.doubleVal(6), p.xs.doubleVal(8)) });
    }

    @Test
    public void testFnBooleanExpr1Exec() {
        executeTester("testFnBooleanExpr1", p.fn.booleanExpr(p.col("1")), false, null, null, null, "true", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testFnCeiling1Exec() {
        executeTester("testFnCeiling1", p.fn.ceiling(p.col("1")), false, null, null, null, "2", new ServerExpression[]{ p.xs.doubleVal(1.3) });
    }

    @Test
    public void testFnCodepointEqual2Exec() {
        executeTester("testFnCodepointEqual2", p.fn.codepointEqual(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("abc") });
    }

    @Test
    public void testFnCodepointsToString1Exec() {
        executeTester("testFnCodepointsToString1", p.fn.codepointsToString(p.col("1")), false, null, null, null, "abc", new ServerExpression[]{ p.xs.integerSeq(p.xs.integer(97), p.xs.integer(98), p.xs.integer(99)) });
    }

    @Test
    public void testFnCompare2Exec() {
        executeTester("testFnCompare2", p.fn.compare(p.col("1"), p.col("2")), false, null, null, null, "1", new ServerExpression[]{ p.xs.string("abz"), p.xs.string("aba") });
    }

    @Test
    public void testFnCompare3Exec() {
        executeTester("testFnCompare3", p.fn.compare(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "1", new ServerExpression[]{ p.xs.string("abz"), p.xs.string("aba"), p.xs.string("http://marklogic.com/collation/") });
    }

    @Test
    public void testFnConcat2Exec() {
        executeTester("testFnConcat2", p.fn.concat(p.col("1"), p.col("2")), false, null, null, null, "ab", new ServerExpression[]{ p.xs.string("a"), p.xs.string("b") });
    }

    @Test
    public void testFnConcat3Exec() {
        executeTester("testFnConcat3", p.fn.concat(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "abc", new ServerExpression[]{ p.xs.string("a"), p.xs.string("b"), p.xs.string("c") });
    }

    @Test
    public void testFnContains2Exec() {
        executeTester("testFnContains2", p.fn.contains(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("b") });
    }

    @Test
    public void testFnContains3Exec() {
        executeTester("testFnContains3", p.fn.contains(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "true", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("b"), p.xs.string("http://marklogic.com/collation/") });
    }

    @Test
    public void testFnCount1Exec() {
        executeTester("testFnCount1", p.fn.count(p.col("1")), false, null, null, null, "3", new ServerExpression[]{ p.xs.doubleSeq(p.xs.doubleVal(1), p.xs.doubleVal(2), p.xs.doubleVal(3)) });
    }

    @Test
    public void testFnCount2Exec() {
        executeTester("testFnCount2", p.fn.count(p.col("1"), p.col("2")), false, null, null, null, "3", new ServerExpression[]{ p.xs.doubleSeq(p.xs.doubleVal(1), p.xs.doubleVal(2), p.xs.doubleVal(3)), p.xs.doubleVal(4) });
    }

    @Test
    public void testFnCurrentDate0Exec() {
        executeTester("testFnCurrentDate0", p.fn.currentDate(), true, "xs:date", null, null, "2024-09-11-07:00", new ServerExpression[]{  });
    }

    @Test
    public void testFnCurrentDateTime0Exec() {
        executeTester("testFnCurrentDateTime0", p.fn.currentDateTime(), true, "xs:dateTime", null, null, "2024-09-11T02:45:08.551523-07:00", new ServerExpression[]{  });
    }

    @Test
    public void testFnCurrentTime0Exec() {
        executeTester("testFnCurrentTime0", p.fn.currentTime(), true, "xs:time", null, null, "02:45:08-07:00", new ServerExpression[]{  });
    }

    @Test
    public void testFnDateTime2Exec() {
        executeTester("testFnDateTime2", p.fn.dateTime(p.col("1"), p.col("2")), false, "xs:dateTime", null, null, "2016-01-02T10:09:08Z", new ServerExpression[]{ p.xs.date("2016-01-02Z"), p.xs.time("10:09:08Z") });
    }

    @Test
    public void testFnDayFromDate1Exec() {
        executeTester("testFnDayFromDate1", p.fn.dayFromDate(p.col("1")), false, null, null, null, "2", new ServerExpression[]{ p.xs.date("2016-01-02-03:04") });
    }

    @Test
    public void testFnDayFromDateTime1Exec() {
        executeTester("testFnDayFromDateTime1", p.fn.dayFromDateTime(p.col("1")), false, null, null, null, "2", new ServerExpression[]{ p.xs.dateTime("2016-01-02T10:09:08Z") });
    }

    @Test
    public void testFnDaysFromDuration1Exec() {
        executeTester("testFnDaysFromDuration1", p.fn.daysFromDuration(p.col("1")), false, null, null, null, "3", new ServerExpression[]{ p.xs.dayTimeDuration("P3DT4H5M6S") });
    }

    @Test
    public void testFnDeepEqual2Exec() {
        executeTester("testFnDeepEqual2", p.fn.deepEqual(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("abc") });
    }

    @Test
    public void testFnDeepEqual3Exec() {
        executeTester("testFnDeepEqual3", p.fn.deepEqual(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "true", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("abc"), p.xs.string("http://marklogic.com/collation/") });
    }

    @Test
    public void testFnDefaultCollation0Exec() {
        executeTester("testFnDefaultCollation0", p.fn.defaultCollation(), true, null, null, null, "http://marklogic.com/collation/codepoint", new ServerExpression[]{  });
    }

    @Test
    public void testFnDistinctValues1Exec() {
        executeTester("testFnDistinctValues1", p.fn.distinctValues(p.col("1")), false, null, null, Format.JSON, "[\"a\", \"b\", \"c\"]", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("a"), p.xs.string("b"), p.xs.string("b"), p.xs.string("c")) });
    }

    @Test
    public void testFnDistinctValues2Exec() {
        executeTester("testFnDistinctValues2", p.fn.distinctValues(p.col("1"), p.col("2")), false, null, null, Format.JSON, "[\"a\", \"b\", \"c\"]", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("a"), p.xs.string("b"), p.xs.string("b"), p.xs.string("c")), p.xs.string("http://marklogic.com/collation/") });
    }

    @Test
    public void testFnEmpty1Exec() {
        executeTester("testFnEmpty1", p.fn.empty(p.col("1")), false, null, null, null, "false", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testFnEncodeForUri1Exec() {
        executeTester("testFnEncodeForUri1", p.fn.encodeForUri(p.col("1")), false, null, null, null, "http%3A%2F%2Fa%2Fb%3Fc%23d", new ServerExpression[]{ p.xs.string("http://a/b?c#d") });
    }

    @Test
    public void testFnEndsWith2Exec() {
        executeTester("testFnEndsWith2", p.fn.endsWith(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("c") });
    }

    @Test
    public void testFnEndsWith3Exec() {
        executeTester("testFnEndsWith3", p.fn.endsWith(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "true", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("c"), p.xs.string("http://marklogic.com/collation/") });
    }

    @Test
    public void testFnEscapeHtmlUri1Exec() {
        executeTester("testFnEscapeHtmlUri1", p.fn.escapeHtmlUri(p.col("1")), false, null, null, null, "http://a/b?c#d", new ServerExpression[]{ p.xs.string("http://a/b?c#d") });
    }

    @Test
    public void testFnExists1Exec() {
        executeTester("testFnExists1", p.fn.exists(p.col("1")), false, null, null, null, "true", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testFnFalseExpr0Exec() {
        executeTester("testFnFalseExpr0", p.fn.falseExpr(), false, null, null, null, "false", new ServerExpression[]{  });
    }

    @Test
    public void testFnFloor1Exec() {
        executeTester("testFnFloor1", p.fn.floor(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleVal(1.7) });
    }

    @Test
    public void testFnFormatDate2Exec() {
        executeTester("testFnFormatDate2", p.fn.formatDate(p.col("1"), p.col("2")), false, null, null, null, "2016/01/02", new ServerExpression[]{ p.xs.date("2016-01-02-03:04"), p.xs.string("[Y0001]/[M01]/[D01]") });
    }

    @Test
    public void testFnFormatDateTime2Exec() {
        executeTester("testFnFormatDateTime2", p.fn.formatDateTime(p.col("1"), p.col("2")), false, null, null, null, "2016/01/02 10:09:08:00", new ServerExpression[]{ p.xs.dateTime("2016-01-02T10:09:08Z"), p.xs.string("[Y0001]/[M01]/[D01] [H01]:[m01]:[s01]:[f01]") });
    }

    @Test
    public void testFnFormatNumber2Exec() {
        executeTester("testFnFormatNumber2", p.fn.formatNumber(p.col("1"), p.col("2")), false, null, null, null, "1,234.50", new ServerExpression[]{ p.xs.doubleVal(1234.5), p.xs.string("#,##0.00") });
    }

    @Test
    public void testFnFormatTime2Exec() {
        executeTester("testFnFormatTime2", p.fn.formatTime(p.col("1"), p.col("2")), false, null, null, null, "10:09:08:00", new ServerExpression[]{ p.xs.time("10:09:08Z"), p.xs.string("[H01]:[m01]:[s01]:[f01]") });
    }

	@ExtendWith(RequiresML11OrLower.class)
    @Test
    public void testFnHead1ExecForML11OrLower() {
        executeTester("testFnHead1", p.fn.head(p.col("1")), false, null, null, null, "a", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("a"), p.xs.string("b"), p.xs.string("c")) });
    }

	@ExtendWith(RequiresML12.class)
	@Test
	public void testFnHead1Exec() {
		executeTester("testFnHead1", p.fn.head(p.col("1")), false, null, null, Format.JSON, null, new ServerExpression[]{ p.xs.stringSeq(p.xs.string("a"), p.xs.string("b"), p.xs.string("c")) });
	}

    @Test
    public void testFnHoursFromDateTime1Exec() {
        executeTester("testFnHoursFromDateTime1", p.fn.hoursFromDateTime(p.col("1")), false, null, null, null, "10", new ServerExpression[]{ p.xs.dateTime("2016-01-02T10:09:08Z") });
    }

    @Test
    public void testFnHoursFromDuration1Exec() {
        executeTester("testFnHoursFromDuration1", p.fn.hoursFromDuration(p.col("1")), false, null, null, null, "4", new ServerExpression[]{ p.xs.dayTimeDuration("P3DT4H5M6S") });
    }

    @Test
    public void testFnHoursFromTime1Exec() {
        executeTester("testFnHoursFromTime1", p.fn.hoursFromTime(p.col("1")), false, null, null, null, "10", new ServerExpression[]{ p.xs.time("10:09:08Z") });
    }

    @Test
    public void testFnImplicitTimezone0Exec() {
        executeTester("testFnImplicitTimezone0", p.fn.implicitTimezone(), true, "xs:dayTimeDuration", null, null, "-PT7H", new ServerExpression[]{  });
    }

    @Test
    public void testFnIndexOf2Exec() {
        executeTester("testFnIndexOf2", p.fn.indexOf(p.col("1"), p.col("2")), false, null, null, null, "2", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("a"), p.xs.string("b"), p.xs.string("c")), p.xs.string("b") });
    }

    @Test
    public void testFnIndexOf3Exec() {
        executeTester("testFnIndexOf3", p.fn.indexOf(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "2", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("a"), p.xs.string("b"), p.xs.string("c")), p.xs.string("b"), p.xs.string("http://marklogic.com/collation/") });
    }

    @Test
    public void testFnInsertBefore3Exec() {
        executeTester("testFnInsertBefore3", p.fn.insertBefore(p.col("1"), p.col("2"), p.col("3")), false, null, null, Format.JSON, "[\"a\", \"b\", \"c\", \"d\", \"e\", \"f\"]", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("a"), p.xs.string("b"), p.xs.string("e"), p.xs.string("f")), p.xs.integer(3), p.xs.stringSeq(p.xs.string("c"), p.xs.string("d")) });
    }

    @Test
    public void testFnIriToUri1Exec() {
        executeTester("testFnIriToUri1", p.fn.iriToUri(p.col("1")), false, null, null, null, "http://a/b?c#d", new ServerExpression[]{ p.xs.string("http://a/b?c#d") });
    }

    @Test
    public void testFnLocalNameFromQName1Exec() {
        executeTester("testFnLocalNameFromQName1", p.fn.localNameFromQName(p.col("1")), false, null, null, null, "abc", new ServerExpression[]{ p.xs.QName("abc") });
    }

    @Test
    public void testFnLowerCase1Exec() {
        executeTester("testFnLowerCase1", p.fn.lowerCase(p.col("1")), false, null, null, null, "abc", new ServerExpression[]{ p.xs.string("ABC") });
    }

    @Test
    public void testFnMatches2Exec() {
        executeTester("testFnMatches2", p.fn.matches(p.col("1"), p.col("2")), false, null, null, null, "false", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("^.B") });
    }

    @Test
    public void testFnMatches3Exec() {
        executeTester("testFnMatches3", p.fn.matches(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "true", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("^.B"), p.xs.string("i") });
    }

    @Test
    public void testFnMax1Exec() {
        executeTester("testFnMax1", p.fn.max(p.col("1")), false, null, null, null, "c", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("a"), p.xs.string("b"), p.xs.string("c")) });
    }

    @Test
    public void testFnMax2Exec() {
        executeTester("testFnMax2", p.fn.max(p.col("1"), p.col("2")), false, null, null, null, "c", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("a"), p.xs.string("b"), p.xs.string("c")), p.xs.string("http://marklogic.com/collation/") });
    }

    @Test
    public void testFnMin1Exec() {
        executeTester("testFnMin1", p.fn.min(p.col("1")), false, null, null, null, "a", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("a"), p.xs.string("b"), p.xs.string("c")) });
    }

    @Test
    public void testFnMin2Exec() {
        executeTester("testFnMin2", p.fn.min(p.col("1"), p.col("2")), false, null, null, null, "a", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("a"), p.xs.string("b"), p.xs.string("c")), p.xs.string("http://marklogic.com/collation/") });
    }

    @Test
    public void testFnMinutesFromDateTime1Exec() {
        executeTester("testFnMinutesFromDateTime1", p.fn.minutesFromDateTime(p.col("1")), false, null, null, null, "9", new ServerExpression[]{ p.xs.dateTime("2016-01-02T10:09:08Z") });
    }

    @Test
    public void testFnMinutesFromDuration1Exec() {
        executeTester("testFnMinutesFromDuration1", p.fn.minutesFromDuration(p.col("1")), false, null, null, null, "5", new ServerExpression[]{ p.xs.dayTimeDuration("P3DT4H5M6S") });
    }

    @Test
    public void testFnMinutesFromTime1Exec() {
        executeTester("testFnMinutesFromTime1", p.fn.minutesFromTime(p.col("1")), false, null, null, null, "9", new ServerExpression[]{ p.xs.time("10:09:08Z") });
    }

    @Test
    public void testFnMonthFromDate1Exec() {
        executeTester("testFnMonthFromDate1", p.fn.monthFromDate(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.date("2016-01-02-03:04") });
    }

    @Test
    public void testFnMonthFromDateTime1Exec() {
        executeTester("testFnMonthFromDateTime1", p.fn.monthFromDateTime(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.dateTime("2016-01-02T10:09:08Z") });
    }

    @Test
    public void testFnMonthsFromDuration1Exec() {
        executeTester("testFnMonthsFromDuration1", p.fn.monthsFromDuration(p.col("1")), false, null, null, null, "2", new ServerExpression[]{ p.xs.yearMonthDuration("P1Y2M") });
    }

    @Test
    public void testFnNamespaceUriFromQName1Exec() {
        executeTester("testFnNamespaceUriFromQName1", p.fn.namespaceUriFromQName(p.col("1")), false, "xs:anyURI", null, null, "", new ServerExpression[]{ p.xs.QName("abc") });
    }

    @Test
    public void testFnNormalizeSpace1Exec() {
        executeTester("testFnNormalizeSpace1", p.fn.normalizeSpace(p.col("1")), false, null, null, null, "abc 123", new ServerExpression[]{ p.xs.string(" abc  123 ") });
    }

    @Test
    public void testFnNormalizeUnicode1Exec() {
        executeTester("testFnNormalizeUnicode1", p.fn.normalizeUnicode(p.col("1")), false, null, null, null, "aBc ", new ServerExpression[]{ p.xs.string(" aBc ") });
    }

    @Test
    public void testFnNormalizeUnicode2Exec() {
        executeTester("testFnNormalizeUnicode2", p.fn.normalizeUnicode(p.col("1"), p.col("2")), false, null, null, null, "aBc ", new ServerExpression[]{ p.xs.string(" aBc "), p.xs.string("NFC") });
    }

    @Test
    public void testFnNot1Exec() {
        executeTester("testFnNot1", p.fn.not(p.col("1")), false, null, null, null, "false", new ServerExpression[]{ p.xs.booleanVal(true) });
    }

    @Test
    public void testFnNumber1Exec() {
        executeTester("testFnNumber1", p.fn.number(p.col("1")), false, null, null, null, "1.1", new ServerExpression[]{ p.xs.string("1.1") });
    }

	@ExtendWith(RequiresML11OrLower.class)
    @Test
    public void testFnPrefixFromQName1ExecForML11OrLower() {
        executeTester("testFnPrefixFromQName1", p.fn.prefixFromQName(p.col("1")), false, null, null, Format.JSON, null, new ServerExpression[]{ p.xs.QName("abc") });
    }

	@ExtendWith(RequiresML12.class)
	@Test
	public void testFnPrefixFromQName1Exec() {
		executeTester("testFnPrefixFromQName1", p.fn.prefixFromQName(p.col("1")), false, null, null, null, "", new ServerExpression[]{ p.xs.QName("abc") });
	}

    @Test
    public void testFnQName2Exec() {
        executeTester("testFnQName2", p.fn.QName(p.col("1"), p.col("2")), false, "xs:QName", null, null, "c", new ServerExpression[]{ p.xs.string("http://a/b"), p.xs.string("c") });
    }

    @Test
    public void testFnRemove2Exec() {
        executeTester("testFnRemove2", p.fn.remove(p.col("1"), p.col("2")), false, null, null, Format.JSON, "[\"a\", \"b\", \"c\"]", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("a"), p.xs.string("b"), p.xs.string("x"), p.xs.string("c")), p.xs.integer(3) });
    }

    @Test
    public void testFnReplace3Exec() {
        executeTester("testFnReplace3", p.fn.replace(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "axc", new ServerExpression[]{ p.xs.string("axc"), p.xs.string("^(.)X"), p.xs.string("$1b") });
    }

    @Test
    public void testFnReplace4Exec() {
        executeTester("testFnReplace4", p.fn.replace(p.col("1"), p.col("2"), p.col("3"), p.col("4")), false, null, null, null, "abc", new ServerExpression[]{ p.xs.string("axc"), p.xs.string("^(.)X"), p.xs.string("$1b"), p.xs.string("i") });
    }

    @Test
    public void testFnResolveUri2Exec() {
        executeTester("testFnResolveUri2", p.fn.resolveUri(p.col("1"), p.col("2")), false, "xs:anyURI", null, null, "http://a/b?c#d", new ServerExpression[]{ p.xs.string("b?c#d"), p.xs.string("http://a/x") });
    }

    @Test
    public void testFnReverse1Exec() {
        executeTester("testFnReverse1", p.fn.reverse(p.col("1")), false, null, null, Format.JSON, "[\"a\", \"b\", \"c\"]", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("c"), p.xs.string("b"), p.xs.string("a")) });
    }

    @Test
    public void testFnRound1Exec() {
        executeTester("testFnRound1", p.fn.round(p.col("1")), false, null, null, null, "2", new ServerExpression[]{ p.xs.doubleVal(1.7) });
    }

    @Test
    public void testFnRoundHalfToEven1Exec() {
        executeTester("testFnRoundHalfToEven1", p.fn.roundHalfToEven(p.col("1")), false, null, null, null, "1234", new ServerExpression[]{ p.xs.doubleVal(1234.5) });
    }

    @Test
    public void testFnRoundHalfToEven2Exec() {
        executeTester("testFnRoundHalfToEven2", p.fn.roundHalfToEven(p.col("1"), p.col("2")), false, null, null, null, "1200", new ServerExpression[]{ p.xs.doubleVal(1234.5), p.xs.integer(-2) });
    }

    @Test
    public void testFnSecondsFromDateTime1Exec() {
        executeTester("testFnSecondsFromDateTime1", p.fn.secondsFromDateTime(p.col("1")), false, null, null, null, "8", new ServerExpression[]{ p.xs.dateTime("2016-01-02T10:09:08Z") });
    }

    @Test
    public void testFnSecondsFromDuration1Exec() {
        executeTester("testFnSecondsFromDuration1", p.fn.secondsFromDuration(p.col("1")), false, null, null, null, "6", new ServerExpression[]{ p.xs.dayTimeDuration("P3DT4H5M6S") });
    }

    @Test
    public void testFnSecondsFromTime1Exec() {
        executeTester("testFnSecondsFromTime1", p.fn.secondsFromTime(p.col("1")), false, null, null, null, "8", new ServerExpression[]{ p.xs.time("10:09:08Z") });
    }

    @Test
    public void testFnStartsWith2Exec() {
        executeTester("testFnStartsWith2", p.fn.startsWith(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("a") });
    }

    @Test
    public void testFnStartsWith3Exec() {
        executeTester("testFnStartsWith3", p.fn.startsWith(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "true", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("a"), p.xs.string("http://marklogic.com/collation/") });
    }

    @Test
    public void testFnString1Exec() {
        executeTester("testFnString1", p.fn.string(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testFnStringJoin2Exec() {
        executeTester("testFnStringJoin2", p.fn.stringJoin(p.col("1"), p.col("2")), false, null, null, null, "a+b+c", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("a"), p.xs.string("b"), p.xs.string("c")), p.xs.string("+") });
    }

    @Test
    public void testFnStringLength1Exec() {
        executeTester("testFnStringLength1", p.fn.stringLength(p.col("1")), false, null, null, null, "3", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testFnStringToCodepoints1Exec() {
        executeTester("testFnStringToCodepoints1", p.fn.stringToCodepoints(p.col("1")), false, null, null, Format.JSON, "[97, 98, 99]", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testFnSubsequence2Exec() {
        executeTester("testFnSubsequence2", p.fn.subsequence(p.col("1"), p.col("2")), false, null, null, Format.JSON, "[\"b\", \"c\", \"d\", \"e\"]", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("a"), p.xs.string("b"), p.xs.string("c"), p.xs.string("d"), p.xs.string("e")), p.xs.doubleVal(2) });
    }

    @Test
    public void testFnSubsequence3Exec() {
        executeTester("testFnSubsequence3", p.fn.subsequence(p.col("1"), p.col("2"), p.col("3")), false, null, null, Format.JSON, "[\"b\", \"c\", \"d\"]", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("a"), p.xs.string("b"), p.xs.string("c"), p.xs.string("d"), p.xs.string("e")), p.xs.doubleVal(2), p.xs.doubleVal(3) });
    }

    @Test
    public void testFnSubstring2Exec() {
        executeTester("testFnSubstring2", p.fn.substring(p.col("1"), p.col("2")), false, null, null, null, "bcd", new ServerExpression[]{ p.xs.string("abcd"), p.xs.doubleVal(2) });
    }

    @Test
    public void testFnSubstring3Exec() {
        executeTester("testFnSubstring3", p.fn.substring(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "bc", new ServerExpression[]{ p.xs.string("abcd"), p.xs.doubleVal(2), p.xs.doubleVal(2) });
    }

    @Test
    public void testFnSubstringAfter2Exec() {
        executeTester("testFnSubstringAfter2", p.fn.substringAfter(p.col("1"), p.col("2")), false, null, null, null, "cd", new ServerExpression[]{ p.xs.string("abcd"), p.xs.string("ab") });
    }

    @Test
    public void testFnSubstringAfter3Exec() {
        executeTester("testFnSubstringAfter3", p.fn.substringAfter(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "cd", new ServerExpression[]{ p.xs.string("abcd"), p.xs.string("ab"), p.xs.string("http://marklogic.com/collation/") });
    }

    @Test
    public void testFnSubstringBefore2Exec() {
        executeTester("testFnSubstringBefore2", p.fn.substringBefore(p.col("1"), p.col("2")), false, null, null, null, "ab", new ServerExpression[]{ p.xs.string("abcd"), p.xs.string("cd") });
    }

    @Test
    public void testFnSubstringBefore3Exec() {
        executeTester("testFnSubstringBefore3", p.fn.substringBefore(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "ab", new ServerExpression[]{ p.xs.string("abcd"), p.xs.string("cd"), p.xs.string("http://marklogic.com/collation/") });
    }

    @Test
    public void testFnSum1Exec() {
        executeTester("testFnSum1", p.fn.sum(p.col("1")), false, null, null, null, "6", new ServerExpression[]{ p.xs.doubleSeq(p.xs.doubleVal(1), p.xs.doubleVal(2), p.xs.doubleVal(3)) });
    }

    @Test
    public void testFnTail1Exec() {
        executeTester("testFnTail1", p.fn.tail(p.col("1")), false, null, null, Format.JSON, "[\"b\", \"c\"]", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("a"), p.xs.string("b"), p.xs.string("c")) });
    }

    @Test
    public void testFnTimezoneFromDate1Exec() {
        executeTester("testFnTimezoneFromDate1", p.fn.timezoneFromDate(p.col("1")), true, "xs:dayTimeDuration", null, null, "-PT3H4M", new ServerExpression[]{ p.xs.date("2016-01-02-03:04") });
    }

    @Test
    public void testFnTimezoneFromDateTime1Exec() {
        executeTester("testFnTimezoneFromDateTime1", p.fn.timezoneFromDateTime(p.col("1")), true, "xs:dayTimeDuration", null, null, "PT0S", new ServerExpression[]{ p.xs.dateTime("2016-01-02T10:09:08Z") });
    }

    @Test
    public void testFnTimezoneFromTime1Exec() {
        executeTester("testFnTimezoneFromTime1", p.fn.timezoneFromTime(p.col("1")), true, "xs:dayTimeDuration", null, null, "PT0S", new ServerExpression[]{ p.xs.time("10:09:08Z") });
    }

    @Test
    public void testFnTokenize2Exec() {
        executeTester("testFnTokenize2", p.fn.tokenize(p.col("1"), p.col("2")), false, null, null, null, "axbxc", new ServerExpression[]{ p.xs.string("axbxc"), p.xs.string("X") });
    }

    @Test
    public void testFnTokenize3Exec() {
        executeTester("testFnTokenize3", p.fn.tokenize(p.col("1"), p.col("2"), p.col("3")), false, null, null, Format.JSON, "[\"a\", \"b\", \"c\"]", new ServerExpression[]{ p.xs.string("axbxc"), p.xs.string("X"), p.xs.string("i") });
    }

    @Test
    public void testFnTranslate3Exec() {
        executeTester("testFnTranslate3", p.fn.translate(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "abcd", new ServerExpression[]{ p.xs.string("axcy"), p.xs.string("xy"), p.xs.string("bd") });
    }

    @Test
    public void testFnTrueExpr0Exec() {
        executeTester("testFnTrueExpr0", p.fn.trueExpr(), false, null, null, null, "true", new ServerExpression[]{  });
    }

    @Test
    public void testFnUpperCase1Exec() {
        executeTester("testFnUpperCase1", p.fn.upperCase(p.col("1")), false, null, null, null, "ABC", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testFnYearFromDate1Exec() {
        executeTester("testFnYearFromDate1", p.fn.yearFromDate(p.col("1")), false, null, null, null, "2016", new ServerExpression[]{ p.xs.date("2016-01-02-03:04") });
    }

    @Test
    public void testFnYearFromDateTime1Exec() {
        executeTester("testFnYearFromDateTime1", p.fn.yearFromDateTime(p.col("1")), false, null, null, null, "2016", new ServerExpression[]{ p.xs.dateTime("2016-01-02T10:09:08Z") });
    }

    @Test
    public void testFnYearsFromDuration1Exec() {
        executeTester("testFnYearsFromDuration1", p.fn.yearsFromDuration(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.yearMonthDuration("P1Y2M") });
    }

    @Test
    public void testGeoArcIntersection4Exec() {
        executeTester("testGeoArcIntersection4", p.geo.arcIntersection(p.col("1"), p.col("2"), p.col("3"), p.col("4")), false, "cts:point", null, null, "1,2", new ServerExpression[]{ p.cts.point(1, 2), p.cts.point(1, 2), p.cts.point(1, 2), p.cts.point(1, 2) });
    }

    @Test
    public void testGeoArcIntersection5Exec() {
        executeTester("testGeoArcIntersection5", p.geo.arcIntersection(p.col("1"), p.col("2"), p.col("3"), p.col("4"), p.col("5")), false, "cts:point", null, null, "1,2", new ServerExpression[]{ p.cts.point(1, 2), p.cts.point(1, 2), p.cts.point(1, 2), p.cts.point(1, 2), p.xs.string("precision=float") });
    }

    @Test
    public void testGeoBearing2Exec() {
        executeTester("testGeoBearing2", p.geo.bearing(p.col("1"), p.col("2")), false, null, null, null, "0.787914357069962", new ServerExpression[]{ p.cts.point(1, 2), p.cts.point(3, 4) });
    }

    @Test
    public void testGeoBearing3Exec() {
        executeTester("testGeoBearing3", p.geo.bearing(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "0.787914357069962", new ServerExpression[]{ p.cts.point(1, 2), p.cts.point(3, 4), p.xs.string("precision=float") });
    }

    @Test
    public void testGeoDestination3Exec() {
        executeTester("testGeoDestination3", p.geo.destination(p.col("1"), p.col("2"), p.col("3")), false, "cts:point", null, null, "1.0063286,2.0161717", new ServerExpression[]{ p.cts.point(1, 2), p.xs.doubleVal(1.2), p.xs.doubleVal(1.2) });
    }

    @Test
    public void testGeoDestination4Exec() {
        executeTester("testGeoDestination4", p.geo.destination(p.col("1"), p.col("2"), p.col("3"), p.col("4")), false, "cts:point", null, null, "1.0063286,2.0161717", new ServerExpression[]{ p.cts.point(1, 2), p.xs.doubleVal(1.2), p.xs.doubleVal(1.2), p.xs.string("precision=float") });
    }

    @Test
    public void testGeoDistance2Exec() {
        executeTester("testGeoDistance2", p.geo.distance(p.col("1"), p.col("2")), false, null, null, null, "0", new ServerExpression[]{ p.cts.point(1, 2), p.cts.point(1, 2) });
    }

    @Test
    public void testGeoDistance3Exec() {
        executeTester("testGeoDistance3", p.geo.distance(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "0", new ServerExpression[]{ p.cts.point(1, 2), p.cts.point(1, 2), p.xs.string("precision=float") });
    }

    @Test
    public void testGeoDistanceConvert3Exec() {
        executeTester("testGeoDistanceConvert3", p.geo.distanceConvert(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "1.609344", new ServerExpression[]{ p.xs.doubleVal(1), p.xs.string("miles"), p.xs.string("km") });
    }

    @Test
    public void testGeoEllipsePolygon5Exec() {
        executeTester("testGeoEllipsePolygon5", p.geo.ellipsePolygon(p.col("1"), p.col("2"), p.col("3"), p.col("4"), p.col("5")), false, "cts:polygon", null, null, "1.0063287,2.0162783 0.98657537,2.0110099 0.98537451,1.9905261 1.0043854,1.983135 1.0173359,1.9990506 1.0063287,2.0162783", new ServerExpression[]{ p.cts.point(1, 2), p.xs.doubleVal(1.2), p.xs.doubleVal(1.2), p.xs.doubleVal(1.2), p.xs.doubleVal(1.2) });
    }

    @Test
    public void testGeoEllipsePolygon6Exec() {
        executeTester("testGeoEllipsePolygon6", p.geo.ellipsePolygon(p.col("1"), p.col("2"), p.col("3"), p.col("4"), p.col("5"), p.col("6")), false, "cts:polygon", null, null, "1.0063287,2.0162783 0.98657537,2.0110099 0.98537451,1.9905261 1.0043854,1.983135 1.0173359,1.9990506 1.0063287,2.0162783", new ServerExpression[]{ p.cts.point(1, 2), p.xs.doubleVal(1.2), p.xs.doubleVal(1.2), p.xs.doubleVal(1.2), p.xs.doubleVal(1.2), p.xs.string("precision=float") });
    }

    @Test
	@ExtendWith(RequiresML11.class)
    public void testGeoGeohashDecode1Exec() {
		executeTester("testGeoGeohashDecode1", p.geo.geohashDecode(p.col("1")), false, "cts:box", null, null, "[-90, -180, 90, 180]", new ServerExpression[]{p.xs.string("abc")});
    }

    @Test
    public void testGeoGeohashDecodePoint1Exec() {
        executeTester("testGeoGeohashDecodePoint1", p.geo.geohashDecodePoint(p.col("1")), false, "cts:point", null, null, "1.0025024,2.0050049", new ServerExpression[]{ p.xs.string("s01mtw") });
    }

    @Test
    public void testGeoGeohashNeighbors1Exec() {
        executeTester("testGeoGeohashNeighbors1", p.geo.geohashNeighbors(p.col("1")), false, null, null, Format.JSON, "{\"NE\":\"s01mtz\", \"S\":\"s01mtt\", \"E\":\"s01mty\", \"W\":\"s01mtq\", \"SW\":\"s01mtm\", \"N\":\"s01mtx\", \"SE\":\"s01mtv\", \"NW\":\"s01mtr\"}", new ServerExpression[]{ p.xs.string("s01mtw") });
    }

    @Test
    public void testGeoGeohashPrecisionDimensions1Exec() {
        executeTester("testGeoGeohashPrecisionDimensions1", p.geo.geohashPrecisionDimensions(p.col("1")), false, null, null, Format.JSON, "[0.17578125, 0.3515625]", new ServerExpression[]{ p.xs.integer(4) });
    }

    @Test
	@ExtendWith(RequiresML11.class)
    public void testGeoGeohashSubhashes1Exec() {
        executeTester("testGeoGeohashSubhashes1", p.geo.geohashSubhashes(p.col("1")), false, null, null, Format.JSON, "[\"s01mtw0\", \"s01mtw1\", \"s01mtw2\", \"s01mtw3\", \"s01mtw4\", \"s01mtw5\", \"s01mtw6\", \"s01mtw7\", \"s01mtw8\", \"s01mtw9\", \"s01mtwb\", \"s01mtwc\", \"s01mtwd\", \"s01mtwe\", \"s01mtwf\", \"s01mtwg\", \"s01mtwh\", \"s01mtwj\", \"s01mtwk\", \"s01mtwm\", \"s01mtwn\", \"s01mtwp\", \"s01mtwq\", \"s01mtwr\", \"s01mtws\", \"s01mtwt\", \"s01mtwu\", \"s01mtwv\", \"s01mtww\", \"s01mtwx\", \"s01mtwy\", \"s01mtwz\"]", new ServerExpression[]{ p.xs.string("s01mtw") });
    }

    @Test
    public void testGeoGeohashSubhashes2Exec() {
        executeTester("testGeoGeohashSubhashes2", p.geo.geohashSubhashes(p.col("1"), p.col("2")), false, null, null, Format.JSON, "[\"s01mtwh\", \"s01mtwj\", \"s01mtwk\", \"s01mtwm\", \"s01mtwn\", \"s01mtwp\", \"s01mtwq\", \"s01mtwr\", \"s01mtws\", \"s01mtwt\", \"s01mtwu\", \"s01mtwv\", \"s01mtww\", \"s01mtwx\", \"s01mtwy\", \"s01mtwz\"]", new ServerExpression[]{ p.xs.string("s01mtw"), p.xs.string("S") });
    }

    @Test
	@ExtendWith(RequiresML11.class)
    public void testGeoParseWkt1Exec() {
        executeTester("testGeoParseWkt1", p.geo.parseWkt(p.col("1")), false, "cts:linestring", null, null, "LINESTRING(-112.25 47.100002,-112.3 47.100002,-112.39999 47.199997)", new ServerExpression[]{ p.xs.string("LINESTRING(-112.25 47.1,-112.3 47.1,-112.4 47.2)") });
    }

    @Test
    public void testGeoValidateWkt1Exec() {
        executeTester("testGeoValidateWkt1", p.geo.validateWkt(p.col("1")), false, null, null, null, "true", new ServerExpression[]{ p.xs.string("POINT(2 1)") });
    }

    @Test
    public void testJsonArray0Exec() {
        executeTester("testJsonArray0", p.json.array(), false, null, null, Format.JSON, "[]", new ServerExpression[]{  });
    }

    @Test
    public void testJsonToArray0Exec() {
        executeTester("testJsonToArray0", p.json.toArray(), false, null, null, Format.JSON, "[]", new ServerExpression[]{  });
    }

    @Test
    public void testMapEntry2Exec() {
        executeTester("testMapEntry2", p.map.entry(p.col("1"), p.col("2")), false, null, null, Format.JSON, "{\"one\":\"two\"}", new ServerExpression[]{ p.xs.string("one"), p.xs.string("two") });
    }

    @Test
    public void testMapMap0Exec() {
        executeTester("testMapMap0", p.map.map(), false, null, null, Format.JSON, "{}", new ServerExpression[]{  });
    }

    @Test
    public void testMapNewExpr0Exec() {
        executeTester("testMapNewExpr0", p.map.newExpr(), false, null, null, Format.JSON, "{}", new ServerExpression[]{  });
    }

    @Test
    public void testMathAcos1Exec() {
        executeTester("testMathAcos1", p.math.acos(p.col("1")), false, null, null, null, "1.0471975511966", new ServerExpression[]{ p.xs.doubleVal(0.5) });
    }

    @Test
    public void testMathAsin1Exec() {
        executeTester("testMathAsin1", p.math.asin(p.col("1")), false, null, null, null, "0.523598775598299", new ServerExpression[]{ p.xs.doubleVal(0.5) });
    }

    @Test
    public void testMathAtan1Exec() {
        executeTester("testMathAtan1", p.math.atan(p.col("1")), false, null, null, null, "1.26262701154934", new ServerExpression[]{ p.xs.doubleVal(3.14159) });
    }

    @Test
    public void testMathAtan22Exec() {
        executeTester("testMathAtan22", p.math.atan2(p.col("1"), p.col("2")), false, null, null, null, "1.42732303452594", new ServerExpression[]{ p.xs.doubleVal(36.23), p.xs.doubleVal(5.234) });
    }

    @Test
    public void testMathCeil1Exec() {
        executeTester("testMathCeil1", p.math.ceil(p.col("1")), false, null, null, null, "2", new ServerExpression[]{ p.xs.doubleVal(1.3) });
    }

    @Test
    public void testMathCos1Exec() {
        executeTester("testMathCos1", p.math.cos(p.col("1")), false, null, null, null, "0.00442569798805079", new ServerExpression[]{ p.xs.doubleVal(11) });
    }

    @Test
    public void testMathCosh1Exec() {
        executeTester("testMathCosh1", p.math.cosh(p.col("1")), false, null, null, null, "29937.0708659498", new ServerExpression[]{ p.xs.doubleVal(11) });
    }

    @Test
    public void testMathCot1Exec() {
        executeTester("testMathCot1", p.math.cot(p.col("1")), false, null, null, null, "1.31422390103306", new ServerExpression[]{ p.xs.doubleVal(19.5) });
    }

    @Test
    public void testMathDegrees1Exec() {
        executeTester("testMathDegrees1", p.math.degrees(p.col("1")), false, null, null, null, "90.0000000000002", new ServerExpression[]{ p.xs.doubleVal(1.5707963267949) });
    }

    @Test
    public void testMathExp1Exec() {
        executeTester("testMathExp1", p.math.exp(p.col("1")), false, null, null, null, "1.10517091807565", new ServerExpression[]{ p.xs.doubleVal(0.1) });
    }

    @Test
    public void testMathFabs1Exec() {
        executeTester("testMathFabs1", p.math.fabs(p.col("1")), false, null, null, null, "4.013", new ServerExpression[]{ p.xs.doubleVal(4.013) });
    }

    @Test
    public void testMathFloor1Exec() {
        executeTester("testMathFloor1", p.math.floor(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleVal(1.7) });
    }

    @Test
    public void testMathFmod2Exec() {
        executeTester("testMathFmod2", p.math.fmod(p.col("1"), p.col("2")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleVal(10), p.xs.doubleVal(3) });
    }

    @Test
    public void testMathFrexp1Exec() {
        executeTester("testMathFrexp1", p.math.frexp(p.col("1")), false, null, null, Format.JSON, "[0.625, 4]", new ServerExpression[]{ p.xs.doubleVal(10) });
    }

    @Test
    public void testMathLdexp2Exec() {
        executeTester("testMathLdexp2", p.math.ldexp(p.col("1"), p.col("2")), false, null, null, null, "1364.992", new ServerExpression[]{ p.xs.doubleVal(1.333), p.xs.integer(10) });
    }

    @Test
    public void testMathLog1Exec() {
        executeTester("testMathLog1", p.math.log(p.col("1")), false, null, null, null, "6.90775527898214", new ServerExpression[]{ p.xs.doubleVal(1000) });
    }

    @Test
    public void testMathLog101Exec() {
        executeTester("testMathLog101", p.math.log10(p.col("1")), false, null, null, null, "3", new ServerExpression[]{ p.xs.doubleVal(1000) });
    }

    @Test
    public void testMathMedian1Exec() {
        executeTester("testMathMedian1", p.math.median(p.col("1")), false, null, null, null, "1.2", new ServerExpression[]{ p.xs.doubleVal(1.2) });
    }

    @Test
    public void testMathMode1Exec() {
        executeTester("testMathMode1", p.math.mode(p.col("1")), false, null, null, null, "abc", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("abc"), p.xs.string("abc"), p.xs.string("def")) });
    }

    @Test
    public void testMathMode2Exec() {
        executeTester("testMathMode2", p.math.mode(p.col("1"), p.col("2")), false, null, null, null, "abc", new ServerExpression[]{ p.xs.stringSeq(p.xs.string("abc"), p.xs.string("abc"), p.xs.string("def")), p.xs.string("collation=http://marklogic.com/collation/") });
    }

    @Test
    public void testMathModf1Exec() {
        executeTester("testMathModf1", p.math.modf(p.col("1")), false, null, null, Format.JSON, "[0.333, 1]", new ServerExpression[]{ p.xs.doubleVal(1.333) });
    }

    @Test
    public void testMathPercentile2Exec() {
        executeTester("testMathPercentile2", p.math.percentile(p.col("1"), p.col("2")), false, null, null, Format.JSON, "[1.5, 3.5]", new ServerExpression[]{ p.xs.doubleSeq(p.xs.doubleVal(2), p.xs.doubleVal(3), p.xs.doubleVal(1), p.xs.doubleVal(4)), p.xs.doubleSeq(p.xs.doubleVal(0.25), p.xs.doubleVal(0.75)) });
    }

    @Test
    public void testMathPercentRank2Exec() {
        executeTester("testMathPercentRank2", p.math.percentRank(p.col("1"), p.col("2")), false, null, null, null, "0.833333333333333", new ServerExpression[]{ p.xs.doubleSeq(p.xs.doubleVal(1), p.xs.doubleVal(7), p.xs.doubleVal(5), p.xs.doubleVal(5), p.xs.doubleVal(10), p.xs.doubleVal(9)), p.xs.doubleVal(9) });
    }

    @Test
    public void testMathPercentRank3Exec() {
        executeTester("testMathPercentRank3", p.math.percentRank(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "0.333333333333333", new ServerExpression[]{ p.xs.doubleSeq(p.xs.doubleVal(1), p.xs.doubleVal(7), p.xs.doubleVal(5), p.xs.doubleVal(5), p.xs.doubleVal(10), p.xs.doubleVal(9)), p.xs.doubleVal(9), p.xs.string("descending") });
    }

    @Test
    public void testMathPi0Exec() {
        executeTester("testMathPi0", p.math.pi(), false, null, null, null, "3.14159265358979", new ServerExpression[]{  });
    }

    @Test
    public void testMathPow2Exec() {
        executeTester("testMathPow2", p.math.pow(p.col("1"), p.col("2")), false, null, null, null, "1024", new ServerExpression[]{ p.xs.doubleVal(2), p.xs.doubleVal(10) });
    }

    @Test
    public void testMathRadians1Exec() {
        executeTester("testMathRadians1", p.math.radians(p.col("1")), false, null, null, null, "1.5707963267949", new ServerExpression[]{ p.xs.doubleVal(90) });
    }

    @Test
    public void testMathRank2Exec() {
        executeTester("testMathRank2", p.math.rank(p.col("1"), p.col("2")), false, null, null, null, "5", new ServerExpression[]{ p.xs.doubleSeq(p.xs.doubleVal(1), p.xs.doubleVal(7), p.xs.doubleVal(5), p.xs.doubleVal(5), p.xs.doubleVal(10), p.xs.doubleVal(9)), p.xs.doubleVal(9) });
    }

    @Test
    public void testMathRank3Exec() {
        executeTester("testMathRank3", p.math.rank(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "2", new ServerExpression[]{ p.xs.doubleSeq(p.xs.doubleVal(1), p.xs.doubleVal(7), p.xs.doubleVal(5), p.xs.doubleVal(5), p.xs.doubleVal(10), p.xs.doubleVal(9)), p.xs.doubleVal(9), p.xs.string("descending") });
    }

    @Test
    public void testMathSin1Exec() {
        executeTester("testMathSin1", p.math.sin(p.col("1")), false, null, null, null, "0.928959715003869", new ServerExpression[]{ p.xs.doubleVal(1.95) });
    }

    @Test
    public void testMathSinh1Exec() {
        executeTester("testMathSinh1", p.math.sinh(p.col("1")), false, null, null, null, "3.44320675450139", new ServerExpression[]{ p.xs.doubleVal(1.95) });
    }

    @Test
    public void testMathSqrt1Exec() {
        executeTester("testMathSqrt1", p.math.sqrt(p.col("1")), false, null, null, null, "2", new ServerExpression[]{ p.xs.doubleVal(4) });
    }

    @Test
    public void testMathStddev1Exec() {
        executeTester("testMathStddev1", p.math.stddev(p.col("1")), false, null, null, Format.JSON, null, new ServerExpression[]{ p.xs.doubleVal(1.2) });
    }

    @Test
    public void testMathStddevP1Exec() {
        executeTester("testMathStddevP1", p.math.stddevP(p.col("1")), false, null, null, null, "0", new ServerExpression[]{ p.xs.doubleVal(1.2) });
    }

    @Test
    public void testMathTan1Exec() {
        executeTester("testMathTan1", p.math.tan(p.col("1")), false, null, null, null, "0.760905351982977", new ServerExpression[]{ p.xs.doubleVal(19.5) });
    }

    @Test
    public void testMathTanh1Exec() {
        executeTester("testMathTanh1", p.math.tanh(p.col("1")), false, null, null, null, "0.739783051274004", new ServerExpression[]{ p.xs.doubleVal(0.95) });
    }

    @Test
    public void testMathTrunc1Exec() {
        executeTester("testMathTrunc1", p.math.trunc(p.col("1")), false, null, null, null, "123", new ServerExpression[]{ p.xs.doubleVal(123.456) });
    }

    @Test
    public void testMathTrunc2Exec() {
        executeTester("testMathTrunc2", p.math.trunc(p.col("1"), p.col("2")), false, null, null, null, "123.45", new ServerExpression[]{ p.xs.doubleVal(123.456), p.xs.integer(2) });
    }

    @Test
    public void testMathVariance1Exec() {
        executeTester("testMathVariance1", p.math.variance(p.col("1")), false, null, null, Format.JSON, null, new ServerExpression[]{ p.xs.doubleVal(1.2) });
    }

    @Test
    public void testMathVarianceP1Exec() {
        executeTester("testMathVarianceP1", p.math.varianceP(p.col("1")), false, null, null, null, "0", new ServerExpression[]{ p.xs.doubleVal(1.2) });
    }

    @Test
    public void testRdfLangString2Exec() {
        executeTester("testRdfLangString2", p.rdf.langString(p.col("1"), p.col("2")), false, "rdf:langString", null, null, "abc", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("en") });
    }

    @Test
    public void testRdfLangStringLanguage1Exec() {
        executeTester("testRdfLangStringLanguage1", p.rdf.langStringLanguage(p.col("1")), false, null, null, null, "en", new ServerExpression[]{ p.rdf.langString("abc", "en") });
    }

    @Test
    public void testSemBnode0Exec() {
        executeTester("testSemBnode0", p.sem.bnode(), true, "sem:blank", null, null, "_:bnode1706937960796523528", new ServerExpression[]{  });
    }

    @Test
    public void testSemCoalesce2Exec() {
        executeTester("testSemCoalesce2", p.sem.coalesce(p.col("1"), p.col("2")), false, null, null, null, "a", new ServerExpression[]{ p.xs.string("a"), p.xs.string("b") });
    }

    @Test
    public void testSemCoalesce3Exec() {
        executeTester("testSemCoalesce3", p.sem.coalesce(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "a", new ServerExpression[]{ p.xs.string("a"), p.xs.string("b"), p.xs.string("c") });
    }

    @Test
    public void testSemDatatype1Exec() {
        executeTester("testSemDatatype1", p.sem.datatype(p.col("1")), false, "sem:iri", null, null, "http://www.w3.org/2001/XMLSchema#string", new ServerExpression[]{ p.xs.string("a") });
    }

    @Test
    public void testSemDefaultGraphIri0Exec() {
        executeTester("testSemDefaultGraphIri0", p.sem.defaultGraphIri(), false, "sem:iri", null, null, "http://marklogic.com/semantics#default-graph", new ServerExpression[]{  });
    }

    @Test
    public void testSemIfExpr3Exec() {
        executeTester("testSemIfExpr3", p.sem.ifExpr(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "a", new ServerExpression[]{ p.xs.booleanVal(true), p.xs.string("a"), p.xs.string("b") });
    }

    @Test
    public void testSemInvalid2Exec() {
        executeTester("testSemInvalid2", p.sem.invalid(p.col("1"), p.col("2")), false, "sem:unknown", null, null, "abc", new ServerExpression[]{ p.xs.string("abc"), p.sem.iri("http://a/b") });
    }

    @Test
    public void testSemIri1Exec() {
        executeTester("testSemIri1", p.sem.iri(p.col("1")), false, "sem:iri", null, null, "http://a/b", new ServerExpression[]{ p.xs.string("http://a/b") });
    }

    @Test
    public void testSemIriToQName1Exec() {
        executeTester("testSemIriToQName1", p.sem.iriToQName(p.col("1")), false, "xs:QName", null, null, "b", new ServerExpression[]{ p.xs.string("http://a/b") });
    }

    @Test
    public void testSemIsBlank1Exec() {
        executeTester("testSemIsBlank1", p.sem.isBlank(p.col("1")), false, null, null, null, "false", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testSemIsIRI1Exec() {
        executeTester("testSemIsIRI1", p.sem.isIRI(p.col("1")), false, null, null, null, "false", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testSemIsLiteral1Exec() {
        executeTester("testSemIsLiteral1", p.sem.isLiteral(p.col("1")), false, null, null, null, "true", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testSemIsNumeric1Exec() {
        executeTester("testSemIsNumeric1", p.sem.isNumeric(p.col("1")), false, null, null, null, "false", new ServerExpression[]{ p.xs.string("a") });
    }

    @Test
    public void testSemLang1Exec() {
        executeTester("testSemLang1", p.sem.lang(p.col("1")), false, null, null, null, "", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testSemLangMatches2Exec() {
        executeTester("testSemLangMatches2", p.sem.langMatches(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("abc") });
    }

    @Test
    public void testSemQNameToIri1Exec() {
        executeTester("testSemQNameToIri1", p.sem.QNameToIri(p.col("1")), false, "sem:iri", null, null, "abc", new ServerExpression[]{ p.xs.QName("abc") });
    }

    @Test
    public void testSemRandom0Exec() {
        executeTester("testSemRandom0", p.sem.random(), true, null, null, null, "0.873418109880463", new ServerExpression[]{  });
    }

    @Test
    public void testSemSameTerm2Exec() {
        executeTester("testSemSameTerm2", p.sem.sameTerm(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.doubleVal(1), p.xs.doubleVal(1) });
    }

    @Test
    public void testSemTimezoneString1Exec() {
        executeTester("testSemTimezoneString1", p.sem.timezoneString(p.col("1")), false, null, null, null, "Z", new ServerExpression[]{ p.xs.dateTime("2016-01-02T10:09:08Z") });
    }

    @Test
    public void testSemTypedLiteral2Exec() {
        executeTester("testSemTypedLiteral2", p.sem.typedLiteral(p.col("1"), p.col("2")), false, "sem:unknown", null, null, "abc", new ServerExpression[]{ p.xs.string("abc"), p.sem.iri("http://a/b") });
    }

    @Test
    public void testSemUnknown2Exec() {
        executeTester("testSemUnknown2", p.sem.unknown(p.col("1"), p.col("2")), false, "sem:unknown", null, null, "abc", new ServerExpression[]{ p.xs.string("abc"), p.sem.iri("http://a/b") });
    }

    @Test
    public void testSemUuid0Exec() {
        executeTester("testSemUuid0", p.sem.uuid(), true, "sem:iri", null, null, "urn:uuid:67b7b233-fd07-4ce4-b4a3-370357d13a03", new ServerExpression[]{  });
    }

    @Test
    public void testSemUuidString0Exec() {
        executeTester("testSemUuidString0", p.sem.uuidString(), true, null, null, null, "ed495c7f-e087-4393-9385-ce6ab410d31d", new ServerExpression[]{  });
    }

    @Test
    public void testSpellDoubleMetaphone1Exec() {
        executeTester("testSpellDoubleMetaphone1", p.spell.doubleMetaphone(p.col("1")), false, null, null, Format.JSON, "[\"smo\", \"xmt\"]", new ServerExpression[]{ p.xs.string("smith") });
    }

    @Test
    public void testSpellLevenshteinDistance2Exec() {
        executeTester("testSpellLevenshteinDistance2", p.spell.levenshteinDistance(p.col("1"), p.col("2")), false, null, null, null, "1", new ServerExpression[]{ p.xs.string("cat"), p.xs.string("cats") });
    }

    @Test
    public void testSpellRomanize1Exec() {
        executeTester("testSpellRomanize1", p.spell.romanize(p.col("1")), false, null, null, null, "abc", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testSqlBitLength1Exec() {
        executeTester("testSqlBitLength1", p.sql.bitLength(p.col("1")), false, null, null, null, "24", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testSqlBucket2Exec() {
        executeTester("testSqlBucket2", p.sql.bucket(p.col("1"), p.col("2")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleSeq(p.xs.doubleVal(2), p.xs.doubleVal(4)), p.xs.doubleVal(3) });
    }

    @Test
    public void testSqlCollatedString2Exec() {
        executeTester("testSqlCollatedString2", p.sql.collatedString(p.col("1"), p.col("2")), false, "sql:collated-string", null, null, "a", new ServerExpression[]{ p.xs.string("a"), p.xs.string("http://marklogic.com/collation/") });
    }

    @Test
    public void testSqlDateadd3Exec() {
        executeTester("testSqlDateadd3", p.sql.dateadd(p.col("1"), p.col("2"), p.col("3")), false, "xs:dateTime", null, null, "2016-01-05T10:09:08Z", new ServerExpression[]{ p.xs.string("day"), p.xs.intVal(3), p.xs.string("2016-01-02T10:09:08Z") });
    }

    @Test
    public void testSqlDatediff3Exec() {
        executeTester("testSqlDatediff3", p.sql.datediff(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "3", new ServerExpression[]{ p.xs.string("day"), p.xs.string("2016-01-02T10:09:08Z"), p.xs.string("2016-01-05T10:09:08Z") });
    }

    @Test
    public void testSqlDatepart2Exec() {
        executeTester("testSqlDatepart2", p.sql.datepart(p.col("1"), p.col("2")), false, null, null, null, "5", new ServerExpression[]{ p.xs.string("day"), p.xs.string("2016-01-05T10:09:08Z") });
    }

    @Test
    public void testSqlDay1Exec() {
        executeTester("testSqlDay1", p.sql.day(p.col("1")), false, null, null, null, "2", new ServerExpression[]{ p.xs.string("2016-01-02") });
    }

    @Test
    public void testSqlDayname1Exec() {
        executeTester("testSqlDayname1", p.sql.dayname(p.col("1")), false, null, null, null, "Saturday", new ServerExpression[]{ p.xs.string("2016-01-02") });
    }

    @Test
    public void testSqlGlob2Exec() {
        executeTester("testSqlGlob2", p.sql.glob(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.string("abcdefg"), p.xs.string("a??d*g") });
    }

    @Test
    public void testSqlHours1Exec() {
        executeTester("testSqlHours1", p.sql.hours(p.col("1")), false, null, null, null, "10", new ServerExpression[]{ p.xs.string("10:09:08") });
    }

    @Test
    public void testSqlIfnull2Exec() {
        executeTester("testSqlIfnull2", p.sql.ifnull(p.col("1"), p.col("2")), false, null, null, null, "a", new ServerExpression[]{ p.xs.string("a"), p.xs.string("b") });
    }

    @Test
    public void testSqlInsert4Exec() {
        executeTester("testSqlInsert4", p.sql.insert(p.col("1"), p.col("2"), p.col("3"), p.col("4")), false, null, null, null, "abcdef", new ServerExpression[]{ p.xs.string("axxxf"), p.xs.doubleVal(2), p.xs.doubleVal(3), p.xs.string("bcde") });
    }

    @Test
    public void testSqlInstr2Exec() {
        executeTester("testSqlInstr2", p.sql.instr(p.col("1"), p.col("2")), false, null, null, null, "3", new ServerExpression[]{ p.xs.string("abcde"), p.xs.string("cd") });
    }

    @Test
    public void testSqlLeft2Exec() {
        executeTester("testSqlLeft2", p.sql.left(p.col("1"), p.col("2")), false, null, null, null, "abc", new ServerExpression[]{ p.xs.string("abcde"), p.xs.doubleVal(3) });
    }

    @Test
    public void testSqlLike2Exec() {
        executeTester("testSqlLike2", p.sql.like(p.col("1"), p.col("2")), false, null, null, null, "false", new ServerExpression[]{ p.xs.string("abcdefg%h"), p.xs.string("a__d%g!%h") });
    }

    @Test
    public void testSqlLike3Exec() {
        executeTester("testSqlLike3", p.sql.like(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "true", new ServerExpression[]{ p.xs.string("abcdefg%h"), p.xs.string("a__d%g!%h"), p.xs.string("!") });
    }

    @Test
    public void testSqlLtrim1Exec() {
        executeTester("testSqlLtrim1", p.sql.ltrim(p.col("1")), false, null, null, null, "abc", new ServerExpression[]{ p.xs.string(" abc") });
    }

    @Test
    public void testSqlMinutes1Exec() {
        executeTester("testSqlMinutes1", p.sql.minutes(p.col("1")), false, null, null, null, "9", new ServerExpression[]{ p.xs.string("10:09:08") });
    }

    @Test
    public void testSqlMonth1Exec() {
        executeTester("testSqlMonth1", p.sql.month(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.string("2016-01-02") });
    }

    @Test
    public void testSqlMonthname1Exec() {
        executeTester("testSqlMonthname1", p.sql.monthname(p.col("1")), false, null, null, null, "January", new ServerExpression[]{ p.xs.string("2016-01-02") });
    }

    @Test
    public void testSqlNullif2Exec() {
        executeTester("testSqlNullif2", p.sql.nullif(p.col("1"), p.col("2")), false, null, null, null, "a", new ServerExpression[]{ p.xs.string("a"), p.xs.string("b") });
    }

    @Test
    public void testSqlOctetLength1Exec() {
        executeTester("testSqlOctetLength1", p.sql.octetLength(p.col("1")), false, null, null, null, "3", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testSqlQuarter1Exec() {
        executeTester("testSqlQuarter1", p.sql.quarter(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.string("2016-01-02") });
    }

    @Test
    public void testSqlRand1Exec() {
        executeTester("testSqlRand1", p.sql.rand(p.col("1")), true, "xs:unsignedLong", null, null, "5115111156722013996", new ServerExpression[]{ p.xs.unsignedLong(1) });
    }

    @Test
    public void testSqlRepeat2Exec() {
        executeTester("testSqlRepeat2", p.sql.repeat(p.col("1"), p.col("2")), false, null, null, null, "abcabc", new ServerExpression[]{ p.xs.string("abc"), p.xs.doubleVal(2) });
    }

    @Test
    public void testSqlRight2Exec() {
        executeTester("testSqlRight2", p.sql.right(p.col("1"), p.col("2")), false, null, null, null, "cde", new ServerExpression[]{ p.xs.string("abcde"), p.xs.doubleVal(3) });
    }

    @Test
    public void testSqlRtrim1Exec() {
        executeTester("testSqlRtrim1", p.sql.rtrim(p.col("1")), false, null, null, null, "abc", new ServerExpression[]{ p.xs.string("abc ") });
    }

    @Test
    public void testSqlSeconds1Exec() {
        executeTester("testSqlSeconds1", p.sql.seconds(p.col("1")), false, null, null, null, "8", new ServerExpression[]{ p.xs.string("10:09:08") });
    }

    @Test
    public void testSqlSign1Exec() {
        executeTester("testSqlSign1", p.sql.sign(p.col("1")), false, null, null, null, "-1", new ServerExpression[]{ p.xs.doubleVal(-3) });
    }

    @Test
    public void testSqlSoundex1Exec() {
        executeTester("testSqlSoundex1", p.sql.soundex(p.col("1")), false, null, null, null, "W630", new ServerExpression[]{ p.xs.string("word") });
    }

    @Test
    public void testSqlSpace1Exec() {
        executeTester("testSqlSpace1", p.sql.space(p.col("1")), false, null, null, null, "", new ServerExpression[]{ p.xs.doubleVal(2) });
    }

    @Test
    public void testSqlStrpos2Exec() {
        executeTester("testSqlStrpos2", p.sql.strpos(p.col("1"), p.col("2")), false, null, null, null, "2", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("b") });
    }

    @Test
    public void testSqlStrpos3Exec() {
        executeTester("testSqlStrpos3", p.sql.strpos(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "2", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("b"), p.xs.string("http://marklogic.com/collation/") });
    }

    @Test
    public void testSqlTrim1Exec() {
        executeTester("testSqlTrim1", p.sql.trim(p.col("1")), false, null, null, null, "abc", new ServerExpression[]{ p.xs.string(" abc ") });
    }

    @Test
    public void testSqlWeek1Exec() {
        executeTester("testSqlWeek1", p.sql.week(p.col("1")), false, null, null, null, "53", new ServerExpression[]{ p.xs.string("2016-01-02") });
    }

    @Test
    public void testSqlWeekday1Exec() {
        executeTester("testSqlWeekday1", p.sql.weekday(p.col("1")), false, null, null, null, "6", new ServerExpression[]{ p.xs.string("2016-01-02") });
    }

    @Test
    public void testSqlYear1Exec() {
        executeTester("testSqlYear1", p.sql.year(p.col("1")), false, null, null, null, "2016", new ServerExpression[]{ p.xs.string("2016-01-02") });
    }

    @Test
    public void testSqlYearday1Exec() {
        executeTester("testSqlYearday1", p.sql.yearday(p.col("1")), false, null, null, null, "2", new ServerExpression[]{ p.xs.string("2016-01-02") });
    }

    @Test
    public void testXdmpAdd642Exec() {
        executeTester("testXdmpAdd642", p.xdmp.add64(p.col("1"), p.col("2")), false, null, null, null, "579", new ServerExpression[]{ p.xs.unsignedLong(123), p.xs.unsignedLong(456) });
    }

    @Test
    public void testXdmpAnd642Exec() {
        executeTester("testXdmpAnd642", p.xdmp.and64(p.col("1"), p.col("2")), false, null, null, null, "2", new ServerExpression[]{ p.xs.unsignedLong(255), p.xs.unsignedLong(2) });
    }

    @Test
    public void testXdmpBase64Decode1Exec() {
        executeTester("testXdmpBase64Decode1", p.xdmp.base64Decode(p.col("1")), false, null, null, null, "hello, world", new ServerExpression[]{ p.xs.string("aGVsbG8sIHdvcmxk") });
    }

    @Test
    public void testXdmpBase64Encode1Exec() {
        executeTester("testXdmpBase64Encode1", p.xdmp.base64Encode(p.col("1")), false, null, null, null, "aGVsbG8sIHdvcmxk", new ServerExpression[]{ p.xs.string("hello, world") });
    }

    @Test
    public void testXdmpCastableAs3Exec() {
        executeTester("testXdmpCastableAs3", p.xdmp.castableAs(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "true", new ServerExpression[]{ p.xs.string("http://www.w3.org/2001/XMLSchema"), p.xs.string("int"), p.xs.string("1") });
    }

    @Test
    public void testXdmpCrypt2Exec() {
        executeTester("testXdmpCrypt2", p.xdmp.crypt(p.col("1"), p.col("2")), false, null, null, null, "arQEnpM6JHR8vY4n3e5gr0", new ServerExpression[]{ p.xs.string("123abc"), p.xs.string("admin") });
    }

    @Test
    public void testXdmpCrypt21Exec() {
        executeTester("testXdmpCrypt21", p.xdmp.crypt2(p.col("1")), true, null, null, null, "$256$jBIG2/HIzq9t7u4CCVe4E0$256$L4sEAEm0ISFIAiPI", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testXdmpDaynameFromDate1Exec() {
        executeTester("testXdmpDaynameFromDate1", p.xdmp.daynameFromDate(p.col("1")), false, null, null, null, "Saturday", new ServerExpression[]{ p.xs.date("2016-01-02") });
    }

    @Test
    public void testXdmpDecodeFromNCName1Exec() {
        executeTester("testXdmpDecodeFromNCName1", p.xdmp.decodeFromNCName(p.col("1")), false, null, null, null, "A Name", new ServerExpression[]{ p.xs.string("A_20_Name") });
    }

    @Test
    public void testXdmpDescribe1Exec() {
        executeTester("testXdmpDescribe1", p.xdmp.describe(p.col("1")), false, null, null, null, "\"123456\"", new ServerExpression[]{ p.xs.string("123456") });
    }

    @Test
    public void testXdmpDescribe2Exec() {
        executeTester("testXdmpDescribe2", p.xdmp.describe(p.col("1"), p.col("2")), false, null, null, null, "\"123456\"", new ServerExpression[]{ p.xs.string("123456"), p.xs.unsignedInt(2) });
    }

    @Test
    public void testXdmpDescribe3Exec() {
        executeTester("testXdmpDescribe3", p.xdmp.describe(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "\"123456\"", new ServerExpression[]{ p.xs.string("123456"), p.xs.unsignedInt(2), p.xs.unsignedInt(3) });
    }

    @Test
    public void testXdmpDiacriticLess1Exec() {
        executeTester("testXdmpDiacriticLess1", p.xdmp.diacriticLess(p.col("1")), false, null, null, null, "abc", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testXdmpEncodeForNCName1Exec() {
        executeTester("testXdmpEncodeForNCName1", p.xdmp.encodeForNCName(p.col("1")), false, null, null, null, "A_20_Name", new ServerExpression[]{ p.xs.string("A Name") });
    }

    @Test
    public void testXdmpFormatNumber1Exec() {
        executeTester("testXdmpFormatNumber1", p.xdmp.formatNumber(p.col("1")), false, null, null, null, "9", new ServerExpression[]{ p.xs.doubleVal(9) });
    }

    @Test
    public void testXdmpFormatNumber2Exec() {
        executeTester("testXdmpFormatNumber2", p.xdmp.formatNumber(p.col("1"), p.col("2")), false, null, null, null, "9", new ServerExpression[]{ p.xs.doubleVal(9), p.xs.string("W") });
    }

    @Test
    public void testXdmpFormatNumber3Exec() {
        executeTester("testXdmpFormatNumber3", p.xdmp.formatNumber(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "NINE", new ServerExpression[]{ p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en") });
    }

    @Test
    public void testXdmpFormatNumber4Exec() {
        executeTester("testXdmpFormatNumber4", p.xdmp.formatNumber(p.col("1"), p.col("2"), p.col("3"), p.col("4")), false, null, null, null, "NINE", new ServerExpression[]{ p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string("") });
    }

    @Test
    public void testXdmpFormatNumber5Exec() {
        executeTester("testXdmpFormatNumber5", p.xdmp.formatNumber(p.col("1"), p.col("2"), p.col("3"), p.col("4"), p.col("5")), false, null, null, null, "NINE", new ServerExpression[]{ p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string("") });
    }

    @Test
    public void testXdmpFormatNumber6Exec() {
        executeTester("testXdmpFormatNumber6", p.xdmp.formatNumber(p.col("1"), p.col("2"), p.col("3"), p.col("4"), p.col("5"), p.col("6")), false, null, null, null, "NINE", new ServerExpression[]{ p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string(""), p.xs.string("") });
    }

    @Test
    public void testXdmpFormatNumber7Exec() {
        executeTester("testXdmpFormatNumber7", p.xdmp.formatNumber(p.col("1"), p.col("2"), p.col("3"), p.col("4"), p.col("5"), p.col("6"), p.col("7")), false, null, null, null, "NINE", new ServerExpression[]{ p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string(""), p.xs.string(""), p.xs.string(",") });
    }

    @Test
    public void testXdmpFormatNumber8Exec() {
        executeTester("testXdmpFormatNumber8", p.xdmp.formatNumber(p.col("1"), p.col("2"), p.col("3"), p.col("4"), p.col("5"), p.col("6"), p.col("7"), p.col("8")), false, null, null, null, "NINE", new ServerExpression[]{ p.xs.doubleVal(9), p.xs.string("W"), p.xs.string("en"), p.xs.string(""), p.xs.string(""), p.xs.string(""), p.xs.string(","), p.xs.integer(3) });
    }

    @Test
    public void testXdmpGetCurrentUser0Exec() {
        executeTester("testXdmpGetCurrentUser0", p.xdmp.getCurrentUser(), true, null, null, null, "admin", new ServerExpression[]{  });
    }

    @Test
    public void testXdmpHash321Exec() {
        executeTester("testXdmpHash321", p.xdmp.hash32(p.col("1")), false, null, null, null, "4229403455", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testXdmpHash641Exec() {
        executeTester("testXdmpHash641", p.xdmp.hash64(p.col("1")), false, "xs:unsignedLong", null, null, "13056678368508584127", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testXdmpHexToInteger1Exec() {
        executeTester("testXdmpHexToInteger1", p.xdmp.hexToInteger(p.col("1")), false, "xs:integer", null, null, "1311768467294899695", new ServerExpression[]{ p.xs.string("1234567890abcdef") });
    }

    @Test
    public void testXdmpHmacMd52Exec() {
        executeTester("testXdmpHmacMd52", p.xdmp.hmacMd5(p.col("1"), p.col("2")), false, null, null, null, "debda77b7cc3e7a10ee70104e6717a6b", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("def") });
    }

    @Test
    public void testXdmpHmacMd53Exec() {
        executeTester("testXdmpHmacMd53", p.xdmp.hmacMd5(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "3r2ne3zD56EO5wEE5nF6aw==", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64") });
    }

    @Test
    public void testXdmpHmacSha12Exec() {
        executeTester("testXdmpHmacSha12", p.xdmp.hmacSha1(p.col("1"), p.col("2")), false, null, null, null, "12554eabbaf7e8e12e4737020f987ca7901016e5", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("def") });
    }

    @Test
    public void testXdmpHmacSha13Exec() {
        executeTester("testXdmpHmacSha13", p.xdmp.hmacSha1(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "ElVOq7r36OEuRzcCD5h8p5AQFuU=", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64") });
    }

    @Test
    public void testXdmpHmacSha2562Exec() {
        executeTester("testXdmpHmacSha2562", p.xdmp.hmacSha256(p.col("1"), p.col("2")), false, null, null, null, "20ebc0f09344470134f35040f63ea98b1d8e414212949ee5c500429d15eab081", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("def") });
    }

    @Test
    public void testXdmpHmacSha2563Exec() {
        executeTester("testXdmpHmacSha2563", p.xdmp.hmacSha256(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "IOvA8JNERwE081BA9j6pix2OQUISlJ7lxQBCnRXqsIE=", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64") });
    }

    @Test
    public void testXdmpHmacSha5122Exec() {
        executeTester("testXdmpHmacSha5122", p.xdmp.hmacSha512(p.col("1"), p.col("2")), false, null, null, null, "bf93c3deee1eb6660ec00820a285327b3e8b775f641fd7f2ea321b6a241afe7b49a5cca81d2e8e1d206bd3379530e2d9ad3a7b2cc54ca66ea3352ebfee3862e5", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("def") });
    }

    @Test
    public void testXdmpHmacSha5123Exec() {
        executeTester("testXdmpHmacSha5123", p.xdmp.hmacSha512(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "v5PD3u4etmYOwAggooUyez6Ld19kH9fy6jIbaiQa/ntJpcyoHS6OHSBr0zeVMOLZrTp7LMVMpm6jNS6/7jhi5Q==", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("def"), p.xs.string("base64") });
    }

    @Test
    public void testXdmpInitcap1Exec() {
        executeTester("testXdmpInitcap1", p.xdmp.initcap(p.col("1")), false, null, null, null, "Abc", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testXdmpIntegerToHex1Exec() {
        executeTester("testXdmpIntegerToHex1", p.xdmp.integerToHex(p.col("1")), false, null, null, null, "7b", new ServerExpression[]{ p.xs.integer(123) });
    }

    @Test
    public void testXdmpIntegerToOctal1Exec() {
        executeTester("testXdmpIntegerToOctal1", p.xdmp.integerToOctal(p.col("1")), false, null, null, null, "173", new ServerExpression[]{ p.xs.integer(123) });
    }

    @Test
    public void testXdmpKeyFromQName1Exec() {
        executeTester("testXdmpKeyFromQName1", p.xdmp.keyFromQName(p.col("1")), false, "xs:NCName", null, null, "abc", new ServerExpression[]{ p.xs.QName("abc") });
    }

    @Test
    public void testXdmpLshift642Exec() {
        executeTester("testXdmpLshift642", p.xdmp.lshift64(p.col("1"), p.col("2")), false, null, null, null, "1020", new ServerExpression[]{ p.xs.unsignedLong(255), p.xs.longVal(2) });
    }

    @Test
    public void testXdmpMd51Exec() {
        executeTester("testXdmpMd51", p.xdmp.md5(p.col("1")), false, null, null, null, "900150983cd24fb0d6963f7d28e17f72", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testXdmpMd52Exec() {
        executeTester("testXdmpMd52", p.xdmp.md5(p.col("1"), p.col("2")), false, null, null, null, "kAFQmDzST7DWlj99KOF/cg==", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("base64") });
    }

    @Test
    public void testXdmpMonthNameFromDate1Exec() {
        executeTester("testXdmpMonthNameFromDate1", p.xdmp.monthNameFromDate(p.col("1")), false, null, null, null, "January", new ServerExpression[]{ p.xs.date("2016-01-02") });
    }

    @Test
    public void testXdmpMul642Exec() {
        executeTester("testXdmpMul642", p.xdmp.mul64(p.col("1"), p.col("2")), false, null, null, null, "56088", new ServerExpression[]{ p.xs.unsignedLong(123), p.xs.unsignedLong(456) });
    }

    @Test
    public void testXdmpNot641Exec() {
        executeTester("testXdmpNot641", p.xdmp.not64(p.col("1")), false, "xs:unsignedLong", null, null, "18446744073709551360", new ServerExpression[]{ p.xs.unsignedLong(255) });
    }

    @Test
    public void testXdmpOctalToInteger1Exec() {
        executeTester("testXdmpOctalToInteger1", p.xdmp.octalToInteger(p.col("1")), false, null, null, null, "2739128", new ServerExpression[]{ p.xs.string("12345670") });
    }

    @Test
    public void testXdmpOr642Exec() {
        executeTester("testXdmpOr642", p.xdmp.or64(p.col("1"), p.col("2")), false, null, null, null, "255", new ServerExpression[]{ p.xs.unsignedLong(255), p.xs.unsignedLong(2) });
    }

    @Test
    public void testXdmpParseDateTime2Exec() {
        executeTester("testXdmpParseDateTime2", p.xdmp.parseDateTime(p.col("1"), p.col("2")), true, "xs:dateTime", null, null, "2016-01-06T18:13:50.873-07:00", new ServerExpression[]{ p.xs.string("[Y0001]-[M01]-[D01]T[h01]:[m01]:[s01].[f1][Z]"), p.xs.string("2016-01-06T17:13:50.873594-08:00") });
    }

    @Test
    public void testXdmpParseYymmdd2Exec() {
        executeTester("testXdmpParseYymmdd2", p.xdmp.parseYymmdd(p.col("1"), p.col("2")), true, "xs:dateTime", null, null, "2016-01-06T18:13:50.873-07:00", new ServerExpression[]{ p.xs.string("yyyy-MM-ddThh:mm:ss.Sz"), p.xs.string("2016-01-06T17:13:50.873594-8.00") });
    }

    @Test
    public void testXdmpPosition2Exec() {
        executeTester("testXdmpPosition2", p.xdmp.position(p.col("1"), p.col("2")), false, null, null, null, "0", new ServerExpression[]{ p.xs.string("abcdef"), p.xs.string("cd") });
    }

    @Test
    public void testXdmpPosition3Exec() {
        executeTester("testXdmpPosition3", p.xdmp.position(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "0", new ServerExpression[]{ p.xs.string("abcdef"), p.xs.string("cd"), p.xs.string("http://marklogic.com/collation/") });
    }

    @Test
    public void testXdmpQNameFromKey1Exec() {
        executeTester("testXdmpQNameFromKey1", p.xdmp.QNameFromKey(p.col("1")), false, "xs:QName", null, null, "c", new ServerExpression[]{ p.xs.string("{http://a/b}c") });
    }

    @Test
    public void testXdmpQuarterFromDate1Exec() {
        executeTester("testXdmpQuarterFromDate1", p.xdmp.quarterFromDate(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.date("2016-01-02") });
    }

    @Test
    public void testXdmpRandom0Exec() {
        executeTester("testXdmpRandom0", p.xdmp.random(), true, "xs:unsignedLong", null, null, "10921725801440392827", new ServerExpression[]{  });
    }

    @Test
    public void testXdmpRandom1Exec() {
        executeTester("testXdmpRandom1", p.xdmp.random(p.col("1")), true, null, null, null, "1", new ServerExpression[]{ p.xs.unsignedLong(1) });
    }

    @Test
    public void testXdmpResolveUri2Exec() {
        executeTester("testXdmpResolveUri2", p.xdmp.resolveUri(p.col("1"), p.col("2")), false, "xs:anyURI", null, null, "/a/b?c#d", new ServerExpression[]{ p.xs.string("b?c#d"), p.xs.string("/a/x") });
    }

    @Test
    public void testXdmpRshift642Exec() {
        executeTester("testXdmpRshift642", p.xdmp.rshift64(p.col("1"), p.col("2")), false, null, null, null, "63", new ServerExpression[]{ p.xs.unsignedLong(255), p.xs.longVal(2) });
    }

    @Test
    public void testXdmpSha11Exec() {
        executeTester("testXdmpSha11", p.xdmp.sha1(p.col("1")), false, null, null, null, "a9993e364706816aba3e25717850c26c9cd0d89d", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testXdmpSha12Exec() {
        executeTester("testXdmpSha12", p.xdmp.sha1(p.col("1"), p.col("2")), false, null, null, null, "qZk+NkcGgWq6PiVxeFDCbJzQ2J0=", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("base64") });
    }

    @Test
    public void testXdmpSha2561Exec() {
        executeTester("testXdmpSha2561", p.xdmp.sha256(p.col("1")), false, null, null, null, "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testXdmpSha2562Exec() {
        executeTester("testXdmpSha2562", p.xdmp.sha256(p.col("1"), p.col("2")), false, null, null, null, "ungWv48Bz+pBQUDeXa4iI7ADYaOWF3qctBD/YfIAFa0=", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("base64") });
    }

    @Test
    public void testXdmpSha3841Exec() {
        executeTester("testXdmpSha3841", p.xdmp.sha384(p.col("1")), false, null, null, null, "cb00753f45a35e8bb5a03d699ac65007272c32ab0eded1631a8b605a43ff5bed8086072ba1e7cc2358baeca134c825a7", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testXdmpSha3842Exec() {
        executeTester("testXdmpSha3842", p.xdmp.sha384(p.col("1"), p.col("2")), false, null, null, null, "ywB1P0WjXou1oD1pmsZQBycsMqsO3tFjGotgWkP/W+2AhgcroefMI1i67KE0yCWn", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("base64") });
    }

    @Test
    public void testXdmpSha5121Exec() {
        executeTester("testXdmpSha5121", p.xdmp.sha512(p.col("1")), false, null, null, null, "ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testXdmpSha5122Exec() {
        executeTester("testXdmpSha5122", p.xdmp.sha512(p.col("1"), p.col("2")), false, null, null, null, "3a81oZNherrMQXNJriBBMRLm+k6JqX6iCp7u5ktV05ohkpkqJ0/BqDa6PCOj/uu9RU1EI2Q86A4qmslPpUyknw==", new ServerExpression[]{ p.xs.string("abc"), p.xs.string("base64") });
    }

    @Test
    public void testXdmpStep642Exec() {
        executeTester("testXdmpStep642", p.xdmp.step64(p.col("1"), p.col("2")), false, null, null, null, "8966314677", new ServerExpression[]{ p.xs.unsignedLong(123), p.xs.unsignedLong(456) });
    }

    @Test
    public void testXdmpStrftime2Exec() {
        executeTester("testXdmpStrftime2", p.xdmp.strftime(p.col("1"), p.col("2")), true, null, null, null, "Wed, 06 Jan 2016 17:13:50", new ServerExpression[]{ p.xs.string("%a, %d %b %Y %H:%M:%S"), p.xs.dateTime("2016-01-06T17:13:50.873594-08:00") });
    }

    @Test
    public void testXdmpTimestampToWallclock1Exec() {
        executeTester("testXdmpTimestampToWallclock1", p.xdmp.timestampToWallclock(p.col("1")), true, "xs:dateTime", null, null, "1969-12-31T16:00:00.0000001", new ServerExpression[]{ p.xs.unsignedLong(1) });
    }

    @Test
    public void testXdmpType1Exec() {
        executeTester("testXdmpType1", p.xdmp.type(p.col("1")), false, "xs:QName", null, null, "string", new ServerExpression[]{ p.xs.string("a") });
    }

    @Test
	@ExtendWith(RequiresML11.class)
    public void testXdmpUnquote1Exec() {
		executeTester("testXdmpUnquote1", p.xdmp.unquote(p.col("1")), false, null, "array", Format.JSON, "[123]", new ServerExpression[]{p.xs.string("[123]")});
    }

    @Test
    public void testXdmpUriContentType1Exec() {
        executeTester("testXdmpUriContentType1", p.xdmp.uriContentType(p.col("1")), false, null, null, null, "application/json", new ServerExpression[]{ p.xs.string("a.json") });
    }

    @Test
    public void testXdmpUriFormat1Exec() {
        executeTester("testXdmpUriFormat1", p.xdmp.uriFormat(p.col("1")), false, null, null, null, "json", new ServerExpression[]{ p.xs.string("a.json") });
    }

    @Test
    public void testXdmpUrlDecode1Exec() {
        executeTester("testXdmpUrlDecode1", p.xdmp.urlDecode(p.col("1")), false, null, null, null, "a b", new ServerExpression[]{ p.xs.string("a+b") });
    }

    @Test
    public void testXdmpUrlEncode1Exec() {
        executeTester("testXdmpUrlEncode1", p.xdmp.urlEncode(p.col("1")), false, null, null, null, "a+b", new ServerExpression[]{ p.xs.string("a b") });
    }

    @Test
    public void testXdmpWallclockToTimestamp1Exec() {
        executeTester("testXdmpWallclockToTimestamp1", p.xdmp.wallclockToTimestamp(p.col("1")), true, "xs:unsignedLong", null, null, "14521292308735940", new ServerExpression[]{ p.xs.dateTime("2016-01-06T17:13:50.873594-08:00") });
    }

    @Test
    public void testXdmpWeekdayFromDate1Exec() {
        executeTester("testXdmpWeekdayFromDate1", p.xdmp.weekdayFromDate(p.col("1")), false, null, null, null, "6", new ServerExpression[]{ p.xs.date("2016-01-02") });
    }

    @Test
    public void testXdmpWeekFromDate1Exec() {
        executeTester("testXdmpWeekFromDate1", p.xdmp.weekFromDate(p.col("1")), false, null, null, null, "53", new ServerExpression[]{ p.xs.date("2016-01-02") });
    }

    @Test
    public void testXdmpXor642Exec() {
        executeTester("testXdmpXor642", p.xdmp.xor64(p.col("1"), p.col("2")), false, null, null, null, "253", new ServerExpression[]{ p.xs.unsignedLong(255), p.xs.unsignedLong(2) });
    }

    @Test
    public void testXdmpYeardayFromDate1Exec() {
        executeTester("testXdmpYeardayFromDate1", p.xdmp.yeardayFromDate(p.col("1")), false, null, null, null, "2", new ServerExpression[]{ p.xs.date("2016-01-02") });
    }

    @Test
    public void testXsAnyURI1Exec() {
        executeTester("testXsAnyURI1", p.xs.anyURI(p.col("1")), false, null, null, null, "http://a/b?c#d", new ServerExpression[]{ p.xs.string("http://a/b?c#d") });
    }

    @Test
    public void testXsBase64Binary1Exec() {
        executeTester("testXsBase64Binary1", p.xs.base64Binary(p.col("1")), false, "xs:base64Binary", null, null, "aGVsbG8sIHdvcmxk", new ServerExpression[]{ p.xs.string("aGVsbG8sIHdvcmxk") });
    }

    @Test
    public void testXsBooleanExpr1Exec() {
        executeTester("testXsBooleanExpr1", p.xs.booleanExpr(p.col("1")), false, null, null, null, "true", new ServerExpression[]{ p.xs.booleanVal(true) });
    }

    @Test
    public void testXsByteExpr1Exec() {
        executeTester("testXsByteExpr1", p.xs.byteExpr(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testXsDate1Exec() {
        executeTester("testXsDate1", p.xs.date(p.col("1")), false, "xs:date", null, null, "2016-01-02Z", new ServerExpression[]{ p.xs.string("2016-01-02Z") });
    }

    @Test
    public void testXsDateTime1Exec() {
        executeTester("testXsDateTime1", p.xs.dateTime(p.col("1")), false, "xs:dateTime", null, null, "2016-01-02T10:09:08Z", new ServerExpression[]{ p.xs.string("2016-01-02T10:09:08Z") });
    }

    @Test
    public void testXsDayTimeDuration1Exec() {
        executeTester("testXsDayTimeDuration1", p.xs.dayTimeDuration(p.col("1")), false, "xs:dayTimeDuration", null, null, "P3DT4H5M6S", new ServerExpression[]{ p.xs.string("P3DT4H5M6S") });
    }

    @Test
    public void testXsDecimal1Exec() {
        executeTester("testXsDecimal1", p.xs.decimal(p.col("1")), false, null, null, null, "1.2", new ServerExpression[]{ p.xs.doubleVal(1.2) });
    }

    @Test
    public void testXsDoubleExpr1Exec() {
        executeTester("testXsDoubleExpr1", p.xs.doubleExpr(p.col("1")), false, null, null, null, "1.2", new ServerExpression[]{ p.xs.doubleVal(1.2) });
    }

    @Test
    public void testXsFloatExpr1Exec() {
        executeTester("testXsFloatExpr1", p.xs.floatExpr(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testXsGDay1Exec() {
        executeTester("testXsGDay1", p.xs.gDay(p.col("1")), false, "xs:gDay", null, null, "---02", new ServerExpression[]{ p.xs.string("---02") });
    }

    @Test
    public void testXsGMonth1Exec() {
        executeTester("testXsGMonth1", p.xs.gMonth(p.col("1")), false, "xs:gMonth", null, null, "--01", new ServerExpression[]{ p.xs.string("--01") });
    }

    @Test
    public void testXsGMonthDay1Exec() {
        executeTester("testXsGMonthDay1", p.xs.gMonthDay(p.col("1")), false, "xs:gMonthDay", null, null, "--01-02", new ServerExpression[]{ p.xs.string("--01-02") });
    }

    @Test
    public void testXsGYear1Exec() {
        executeTester("testXsGYear1", p.xs.gYear(p.col("1")), false, "xs:gYear", null, null, "2016", new ServerExpression[]{ p.xs.string("2016") });
    }

    @Test
    public void testXsGYearMonth1Exec() {
        executeTester("testXsGYearMonth1", p.xs.gYearMonth(p.col("1")), false, "xs:gYearMonth", null, null, "2016-01", new ServerExpression[]{ p.xs.string("2016-01") });
    }

    @Test
    public void testXsHexBinary1Exec() {
        executeTester("testXsHexBinary1", p.xs.hexBinary(p.col("1")), false, "xs:hexBinary", null, null, "68656C6C6F2C20776F726C64", new ServerExpression[]{ p.xs.string("68656c6c6f2c20776f726c64") });
    }

    @Test
    public void testXsInteger1Exec() {
        executeTester("testXsInteger1", p.xs.integer(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testXsIntExpr1Exec() {
        executeTester("testXsIntExpr1", p.xs.intExpr(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testXsLanguage1Exec() {
        executeTester("testXsLanguage1", p.xs.language(p.col("1")), false, null, null, null, "en-US", new ServerExpression[]{ p.xs.string("en-US") });
    }

    @Test
    public void testXsLongExpr1Exec() {
        executeTester("testXsLongExpr1", p.xs.longExpr(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testXsName1Exec() {
        executeTester("testXsName1", p.xs.Name(p.col("1")), false, null, null, null, "a:b:c", new ServerExpression[]{ p.xs.string("a:b:c") });
    }

    @Test
    public void testXsNCName1Exec() {
        executeTester("testXsNCName1", p.xs.NCName(p.col("1")), false, null, null, null, "a-b-c", new ServerExpression[]{ p.xs.string("a-b-c") });
    }

	@ExtendWith(RequiresML11OrLower.class)
    @Test
    public void testXsNegativeInteger1Exec() {
        executeTester("testXsNegativeInteger1", p.xs.negativeInteger(p.col("1")), false, null, null, null, "-1", new ServerExpression[]{ p.xs.doubleVal(-1) });
    }

    @Test
    public void testXsNMTOKEN1Exec() {
        executeTester("testXsNMTOKEN1", p.xs.NMTOKEN(p.col("1")), false, null, null, null, "a:b:c", new ServerExpression[]{ p.xs.string("a:b:c") });
    }

	@ExtendWith(RequiresML11OrLower.class)
    @Test
    public void testXsNonNegativeInteger1Exec() {
        executeTester("testXsNonNegativeInteger1", p.xs.nonNegativeInteger(p.col("1")), false, null, null, null, "0", new ServerExpression[]{ p.xs.string("0") });
    }

	@ExtendWith(RequiresML11OrLower.class)
    @Test
    public void testXsNonPositiveInteger1Exec() {
        executeTester("testXsNonPositiveInteger1", p.xs.nonPositiveInteger(p.col("1")), false, null, null, null, "0", new ServerExpression[]{ p.xs.string("0") });
    }

    @Test
    public void testXsNormalizedString1Exec() {
        executeTester("testXsNormalizedString1", p.xs.normalizedString(p.col("1")), false, null, null, null, "a b c", new ServerExpression[]{ p.xs.string("a b c") });
    }

    @Test
    public void testXsNumeric1Exec() {
        executeTester("testXsNumeric1", p.xs.numeric(p.col("1")), false, null, null, null, "1.2", new ServerExpression[]{ p.xs.doubleVal(1.2) });
    }

	@ExtendWith(RequiresML11OrLower.class)
    @Test
    public void testXsPositiveInteger1Exec() {
        executeTester("testXsPositiveInteger1", p.xs.positiveInteger(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testXsQName1Exec() {
        executeTester("testXsQName1", p.xs.QName(p.col("1")), false, "xs:QName", null, null, "abc", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testXsShortExpr1Exec() {
        executeTester("testXsShortExpr1", p.xs.shortExpr(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testXsString1Exec() {
        executeTester("testXsString1", p.xs.string(p.col("1")), false, null, null, null, "abc", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testXsTime1Exec() {
        executeTester("testXsTime1", p.xs.time(p.col("1")), false, "xs:time", null, null, "10:09:08Z", new ServerExpression[]{ p.xs.string("10:09:08Z") });
    }

    @Test
    public void testXsToken1Exec() {
        executeTester("testXsToken1", p.xs.token(p.col("1")), false, null, null, null, "a b c", new ServerExpression[]{ p.xs.string("a b c") });
    }

    @Test
    public void testXsUnsignedByte1Exec() {
        executeTester("testXsUnsignedByte1", p.xs.unsignedByte(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testXsUnsignedInt1Exec() {
        executeTester("testXsUnsignedInt1", p.xs.unsignedInt(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testXsUnsignedLong1Exec() {
        executeTester("testXsUnsignedLong1", p.xs.unsignedLong(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testXsUnsignedShort1Exec() {
        executeTester("testXsUnsignedShort1", p.xs.unsignedShort(p.col("1")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleVal(1) });
    }

    @Test
    public void testXsUntypedAtomic1Exec() {
        executeTester("testXsUntypedAtomic1", p.xs.untypedAtomic(p.col("1")), false, null, null, null, "abc", new ServerExpression[]{ p.xs.string("abc") });
    }

    @Test
    public void testXsYearMonthDuration1Exec() {
        executeTester("testXsYearMonthDuration1", p.xs.yearMonthDuration(p.col("1")), false, "xs:yearMonthDuration", null, null, "P1Y2M", new ServerExpression[]{ p.xs.string("P1Y2M") });
    }

    @Test
    public void testOperatorAdd2Exec() {
        executeTester("testOperatorAdd2", p.add(p.col("1"), p.col("2")), false, null, null, null, "3", new ServerExpression[]{ p.xs.doubleVal(1), p.xs.doubleVal(2) });
    }

    @Test
    public void testOperatorAdd3Exec() {
        executeTester("testOperatorAdd3", p.add(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "6", new ServerExpression[]{ p.xs.doubleVal(1), p.xs.doubleVal(2), p.xs.doubleVal(3) });
    }

    @Test
    public void testOperatorAnd2Exec() {
        executeTester("testOperatorAnd2", p.and(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.booleanVal(true), p.xs.booleanVal(true) });
    }

    @Test
    public void testOperatorAnd3Exec() {
        executeTester("testOperatorAnd3", p.and(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "true", new ServerExpression[]{ p.xs.booleanVal(true), p.xs.booleanVal(true), p.xs.booleanVal(true) });
    }

    @Test
    public void testOperatorDivide2Exec() {
        executeTester("testOperatorDivide2", p.divide(p.col("1"), p.col("2")), false, null, null, null, "3", new ServerExpression[]{ p.xs.doubleVal(6), p.xs.doubleVal(2) });
    }

    @Test
    public void testOperatorEq2Exec() {
        executeTester("testOperatorEq2", p.eq(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.doubleVal(1), p.xs.doubleVal(1) });
    }

    @Test
    public void testOperatorEq3Exec() {
        executeTester("testOperatorEq3", p.eq(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "true", new ServerExpression[]{ p.xs.doubleVal(1), p.xs.doubleVal(1), p.xs.doubleVal(1) });
    }

    @Test
    public void testOperatorGe2Exec() {
        executeTester("testOperatorGe2", p.ge(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.doubleVal(1), p.xs.doubleVal(1) });
    }

    @Test
    public void testOperatorGt2Exec() {
        executeTester("testOperatorGt2", p.gt(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.doubleVal(2), p.xs.doubleVal(1) });
    }

    @Test
    public void testOperatorIn2Exec() {
        executeTester("testOperatorIn2", p.in(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.doubleVal(2), p.xs.doubleSeq(p.xs.doubleVal(1), p.xs.doubleVal(2), p.xs.doubleVal(3)) });
    }

    @Test
    public void testOperatorLe2Exec() {
        executeTester("testOperatorLe2", p.le(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.doubleVal(1), p.xs.doubleVal(1) });
    }

    @Test
    public void testOperatorLt2Exec() {
        executeTester("testOperatorLt2", p.lt(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.doubleVal(1), p.xs.doubleVal(2) });
    }

    @Test
    public void testOperatorMultiply2Exec() {
        executeTester("testOperatorMultiply2", p.multiply(p.col("1"), p.col("2")), false, null, null, null, "6", new ServerExpression[]{ p.xs.doubleVal(2), p.xs.doubleVal(3) });
    }

    @Test
    public void testOperatorMultiply3Exec() {
        executeTester("testOperatorMultiply3", p.multiply(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "24", new ServerExpression[]{ p.xs.doubleVal(2), p.xs.doubleVal(3), p.xs.doubleVal(4) });
    }

    @Test
    public void testOperatorNe2Exec() {
        executeTester("testOperatorNe2", p.ne(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.doubleVal(1), p.xs.doubleVal(2) });
    }

    @Test
    public void testOperatorNot1Exec() {
        executeTester("testOperatorNot1", p.not(p.col("1")), false, null, null, null, "true", new ServerExpression[]{ p.xs.booleanVal(false) });
    }

    @Test
    public void testOperatorOr2Exec() {
        executeTester("testOperatorOr2", p.or(p.col("1"), p.col("2")), false, null, null, null, "true", new ServerExpression[]{ p.xs.booleanVal(false), p.xs.booleanVal(true) });
    }

    @Test
    public void testOperatorOr3Exec() {
        executeTester("testOperatorOr3", p.or(p.col("1"), p.col("2"), p.col("3")), false, null, null, null, "true", new ServerExpression[]{ p.xs.booleanVal(false), p.xs.booleanVal(true), p.xs.booleanVal(false) });
    }

    @Test
    public void testOperatorSubtract2Exec() {
        executeTester("testOperatorSubtract2", p.subtract(p.col("1"), p.col("2")), false, null, null, null, "1", new ServerExpression[]{ p.xs.doubleVal(3), p.xs.doubleVal(2) });
    }
}
