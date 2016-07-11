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
package com.marklogic.client.expression;

// TODO: single import
import com.marklogic.client.expression.BaseType;

import com.marklogic.client.expression.Xs;
 import com.marklogic.client.expression.BaseType;


// IMPORTANT: Do not edit. This file is generated. 
public interface Fn {
    public Xs.NumericExpr abs(Xs.NumericExpr arg);
    public Xs.DateExpr adjustDateToTimezone(Xs.DateExpr arg);
    public Xs.DateExpr adjustDateToTimezone(Xs.DateExpr arg, Xs.DayTimeDurationExpr timezone);
    public Xs.DateTimeExpr adjustDateTimeToTimezone(Xs.DateTimeExpr arg);
    public Xs.DateTimeExpr adjustDateTimeToTimezone(Xs.DateTimeExpr arg, Xs.DayTimeDurationExpr timezone);
    public Xs.TimeExpr adjustTimeToTimezone(Xs.TimeExpr arg);
    public Xs.TimeExpr adjustTimeToTimezone(Xs.TimeExpr arg, Xs.DayTimeDurationExpr timezone);
    public BaseType.ElementExpr analyzeString(String in, String regex);
    public BaseType.ElementExpr analyzeString(Xs.StringExpr in, Xs.StringExpr regex);
    public BaseType.ElementExpr analyzeString(String in, String regex, String flags);
    public BaseType.ElementExpr analyzeString(Xs.StringExpr in, Xs.StringExpr regex, Xs.StringExpr flags);
    public Xs.AnyAtomicTypeExpr avg(Xs.AnyAtomicTypeSeqExpr arg);
    public Xs.AnyURIExpr baseUri(BaseType.NodeExpr arg);
    public Xs.BooleanExpr booleanExpr(BaseType.ItemSeqExpr arg);
    public Xs.NumericExpr ceiling(Xs.NumericExpr arg);
    public Xs.BooleanExpr codepointEqual(String comparand1, String comparand2);
    public Xs.BooleanExpr codepointEqual(Xs.StringExpr comparand1, Xs.StringExpr comparand2);
    public Xs.StringExpr codepointsToString(Xs.IntegerSeqExpr arg);
    public Xs.IntegerExpr compare(String comparand1, String comparand2);
    public Xs.IntegerExpr compare(Xs.StringExpr comparand1, Xs.StringExpr comparand2);
    public Xs.IntegerExpr compare(String comparand1, String comparand2, String collation);
    public Xs.IntegerExpr compare(Xs.StringExpr comparand1, Xs.StringExpr comparand2, Xs.StringExpr collation);
    public Xs.StringExpr concat(Xs.AnyAtomicTypeExpr... parameter1);
    public Xs.IntegerExpr count(BaseType.ItemSeqExpr arg);
    public Xs.IntegerExpr count(BaseType.ItemSeqExpr arg, double maximum);
    public Xs.IntegerExpr count(BaseType.ItemSeqExpr arg, Xs.DoubleExpr maximum);
    public Xs.DateExpr currentDate();
    public Xs.DateTimeExpr currentDateTime();
    public Xs.TimeExpr currentTime();
    public Xs.BooleanExpr contains(String parameter1, String parameter2);
    public Xs.BooleanExpr contains(Xs.StringExpr parameter1, Xs.StringExpr parameter2);
    public Xs.BooleanExpr contains(String parameter1, String parameter2, String collation);
    public Xs.BooleanExpr contains(Xs.StringExpr parameter1, Xs.StringExpr parameter2, Xs.StringExpr collation);
    public Xs.IntegerExpr dayFromDate(Xs.DateExpr arg);
    public Xs.IntegerExpr dayFromDateTime(Xs.DateTimeExpr arg);
    public Xs.IntegerExpr daysFromDuration(Xs.DurationExpr arg);
    public Xs.BooleanExpr deepEqual(BaseType.ItemSeqExpr parameter1, BaseType.ItemSeqExpr parameter2);
    public Xs.BooleanExpr deepEqual(BaseType.ItemSeqExpr parameter1, BaseType.ItemSeqExpr parameter2, String collation);
    public Xs.BooleanExpr deepEqual(BaseType.ItemSeqExpr parameter1, BaseType.ItemSeqExpr parameter2, Xs.StringExpr collation);
    public Xs.StringExpr defaultCollation();
    public Xs.AnyAtomicTypeSeqExpr distinctValues(Xs.AnyAtomicTypeSeqExpr arg);
    public Xs.AnyAtomicTypeSeqExpr distinctValues(Xs.AnyAtomicTypeSeqExpr arg, String collation);
    public Xs.AnyAtomicTypeSeqExpr distinctValues(Xs.AnyAtomicTypeSeqExpr arg, Xs.StringExpr collation);
    public Xs.AnyURIExpr documentUri(BaseType.NodeExpr arg);
    public Xs.BooleanExpr empty(BaseType.ItemSeqExpr arg);
    public Xs.StringExpr encodeForUri(String uriPart);
    public Xs.StringExpr encodeForUri(Xs.StringExpr uriPart);
    public Xs.BooleanExpr endsWith(String parameter1, String parameter2);
    public Xs.BooleanExpr endsWith(Xs.StringExpr parameter1, Xs.StringExpr parameter2);
    public Xs.BooleanExpr endsWith(String parameter1, String parameter2, String collation);
    public Xs.BooleanExpr endsWith(Xs.StringExpr parameter1, Xs.StringExpr parameter2, Xs.StringExpr collation);
    public Xs.StringExpr escapeHtmlUri(String uriPart);
    public Xs.StringExpr escapeHtmlUri(Xs.StringExpr uriPart);
    public Xs.BooleanExpr exists(BaseType.ItemSeqExpr arg);
    public Xs.BooleanExpr falseExpr();
    public Xs.NumericExpr floor(Xs.NumericExpr arg);
    public Xs.StringExpr formatDate(Xs.DateExpr value, String picture);
    public Xs.StringExpr formatDate(Xs.DateExpr value, Xs.StringExpr picture);
    public Xs.StringExpr formatDateTime(Xs.DateTimeExpr value, String picture);
    public Xs.StringExpr formatDateTime(Xs.DateTimeExpr value, Xs.StringExpr picture);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture, String decimalFormatName);
    public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture, Xs.StringExpr decimalFormatName);
    public Xs.StringExpr formatTime(Xs.TimeExpr value, String picture);
    public Xs.StringExpr formatTime(Xs.TimeExpr value, Xs.StringExpr picture);
    public Xs.StringExpr generateId(BaseType.NodeExpr node);
    public BaseType.ItemExpr head(BaseType.ItemSeqExpr arg1);
    public Xs.IntegerExpr hoursFromDateTime(Xs.DateTimeExpr arg);
    public Xs.IntegerExpr hoursFromDuration(Xs.DurationExpr arg);
    public Xs.IntegerExpr hoursFromTime(Xs.TimeExpr arg);
    public Xs.DayTimeDurationExpr implicitTimezone();
    public Xs.IntegerSeqExpr indexOf(Xs.AnyAtomicTypeSeqExpr seqParam, Xs.AnyAtomicTypeExpr srchParam);
    public Xs.IntegerSeqExpr indexOf(Xs.AnyAtomicTypeSeqExpr seqParam, Xs.AnyAtomicTypeExpr srchParam, String collationLiteral);
    public Xs.IntegerSeqExpr indexOf(Xs.AnyAtomicTypeSeqExpr seqParam, Xs.AnyAtomicTypeExpr srchParam, Xs.StringExpr collationLiteral);
    public Xs.StringSeqExpr inScopePrefixes(BaseType.ElementExpr element);
    public BaseType.ItemSeqExpr insertBefore(BaseType.ItemSeqExpr target, Xs.IntegerExpr position, BaseType.ItemSeqExpr inserts);
    public Xs.StringExpr iriToUri(String uriPart);
    public Xs.StringExpr iriToUri(Xs.StringExpr uriPart);
    public Xs.BooleanExpr lang(String testlang, BaseType.NodeExpr node);
    public Xs.BooleanExpr lang(Xs.StringExpr testlang, BaseType.NodeExpr node);
    public Xs.StringExpr localName(BaseType.NodeExpr arg);
    public Xs.NCNameExpr localNameFromQName(Xs.QNameExpr arg);
    public Xs.StringExpr lowerCase(String string);
    public Xs.StringExpr lowerCase(Xs.StringExpr string);
    public Xs.BooleanExpr matches(String input, String pattern);
    public Xs.BooleanExpr matches(Xs.StringExpr input, Xs.StringExpr pattern);
    public Xs.BooleanExpr matches(String input, String pattern, String flags);
    public Xs.BooleanExpr matches(Xs.StringExpr input, Xs.StringExpr pattern, Xs.StringExpr flags);
    public Xs.AnyAtomicTypeExpr max(Xs.AnyAtomicTypeSeqExpr arg);
    public Xs.AnyAtomicTypeExpr max(Xs.AnyAtomicTypeSeqExpr arg, String collation);
    public Xs.AnyAtomicTypeExpr max(Xs.AnyAtomicTypeSeqExpr arg, Xs.StringExpr collation);
    public Xs.AnyAtomicTypeExpr min(Xs.AnyAtomicTypeSeqExpr arg);
    public Xs.AnyAtomicTypeExpr min(Xs.AnyAtomicTypeSeqExpr arg, String collation);
    public Xs.AnyAtomicTypeExpr min(Xs.AnyAtomicTypeSeqExpr arg, Xs.StringExpr collation);
    public Xs.IntegerExpr minutesFromDateTime(Xs.DateTimeExpr arg);
    public Xs.IntegerExpr minutesFromDuration(Xs.DurationExpr arg);
    public Xs.IntegerExpr minutesFromTime(Xs.TimeExpr arg);
    public Xs.IntegerExpr monthFromDate(Xs.DateExpr arg);
    public Xs.IntegerExpr monthFromDateTime(Xs.DateTimeExpr arg);
    public Xs.IntegerExpr monthsFromDuration(Xs.DurationExpr arg);
    public Xs.StringExpr name(BaseType.NodeExpr arg);
    public Xs.AnyURIExpr namespaceUri(BaseType.NodeExpr arg);
    public Xs.AnyURIExpr namespaceUriForPrefix(String prefix, BaseType.ElementExpr element);
    public Xs.AnyURIExpr namespaceUriForPrefix(Xs.StringExpr prefix, BaseType.ElementExpr element);
    public Xs.AnyURIExpr namespaceUriFromQName(Xs.QNameExpr arg);
    public Xs.BooleanExpr nilled();
    public Xs.BooleanExpr nilled(BaseType.NodeExpr arg);
    public Xs.QNameExpr nodeName();
    public Xs.QNameExpr nodeName(BaseType.NodeExpr arg);
    public Xs.StringExpr normalizeSpace(String input);
    public Xs.StringExpr normalizeSpace(Xs.StringExpr input);
    public Xs.StringExpr normalizeUnicode(String arg);
    public Xs.StringExpr normalizeUnicode(Xs.StringExpr arg);
    public Xs.StringExpr normalizeUnicode(String arg, String normalizationForm);
    public Xs.StringExpr normalizeUnicode(Xs.StringExpr arg, Xs.StringExpr normalizationForm);
    public Xs.BooleanExpr not(BaseType.ItemSeqExpr arg);
    public Xs.DoubleExpr number(Xs.AnyAtomicTypeExpr arg);
    public Xs.NCNameExpr prefixFromQName(Xs.QNameExpr arg);
    public Xs.QNameExpr QName(String paramURI, String paramQName);
    public Xs.QNameExpr QName(Xs.StringExpr paramURI, Xs.StringExpr paramQName);
    public BaseType.ItemSeqExpr remove(BaseType.ItemSeqExpr target, Xs.IntegerExpr position);
    public Xs.StringExpr replace(String input, String pattern, String replacement);
    public Xs.StringExpr replace(Xs.StringExpr input, Xs.StringExpr pattern, Xs.StringExpr replacement);
    public Xs.StringExpr replace(String input, String pattern, String replacement, String flags);
    public Xs.StringExpr replace(Xs.StringExpr input, Xs.StringExpr pattern, Xs.StringExpr replacement, Xs.StringExpr flags);
    public Xs.QNameExpr resolveQName(String qname, BaseType.ElementExpr element);
    public Xs.QNameExpr resolveQName(Xs.StringExpr qname, BaseType.ElementExpr element);
    public Xs.AnyURIExpr resolveUri(String relative, String base);
    public Xs.AnyURIExpr resolveUri(Xs.StringExpr relative, Xs.StringExpr base);
    public BaseType.ItemSeqExpr reverse(BaseType.ItemSeqExpr target);
    public BaseType.NodeExpr root(BaseType.NodeExpr arg);
    public Xs.NumericExpr round(Xs.NumericExpr arg);
    public Xs.NumericExpr roundHalfToEven(Xs.NumericExpr arg);
    public Xs.NumericExpr roundHalfToEven(Xs.NumericExpr arg, Xs.IntegerExpr precision);
    public Xs.DecimalExpr secondsFromDateTime(Xs.DateTimeExpr arg);
    public Xs.DecimalExpr secondsFromDuration(Xs.DurationExpr arg);
    public Xs.DecimalExpr secondsFromTime(Xs.TimeExpr arg);
    public Xs.BooleanExpr startsWith(String parameter1, String parameter2);
    public Xs.BooleanExpr startsWith(Xs.StringExpr parameter1, Xs.StringExpr parameter2);
    public Xs.BooleanExpr startsWith(String parameter1, String parameter2, String collation);
    public Xs.BooleanExpr startsWith(Xs.StringExpr parameter1, Xs.StringExpr parameter2, Xs.StringExpr collation);
    public Xs.StringExpr string(BaseType.ItemExpr arg);
    public Xs.StringExpr stringJoin(String... parameter1);
    public Xs.StringExpr stringJoin(Xs.StringSeqExpr parameter1);
    public Xs.StringExpr stringJoin(String parameter1, String parameter2);
    public Xs.StringExpr stringJoin(Xs.StringSeqExpr parameter1, Xs.StringExpr parameter2);
    public Xs.IntegerExpr stringLength(String sourceString);
    public Xs.IntegerExpr stringLength(Xs.StringExpr sourceString);
    public Xs.IntegerSeqExpr stringToCodepoints(String arg);
    public Xs.IntegerSeqExpr stringToCodepoints(Xs.StringExpr arg);
    public BaseType.ItemSeqExpr subsequence(BaseType.ItemSeqExpr sourceSeq, Xs.NumericExpr startingLoc);
    public BaseType.ItemSeqExpr subsequence(BaseType.ItemSeqExpr sourceSeq, Xs.NumericExpr startingLoc, Xs.NumericExpr length);
    public Xs.StringExpr substring(String sourceString, Xs.NumericExpr startingLoc);
    public Xs.StringExpr substring(Xs.StringExpr sourceString, Xs.NumericExpr startingLoc);
    public Xs.StringExpr substring(String sourceString, Xs.NumericExpr startingLoc, Xs.NumericExpr length);
    public Xs.StringExpr substring(Xs.StringExpr sourceString, Xs.NumericExpr startingLoc, Xs.NumericExpr length);
    public Xs.StringExpr substringAfter(String input, String after);
    public Xs.StringExpr substringAfter(Xs.StringExpr input, Xs.StringExpr after);
    public Xs.StringExpr substringAfter(String input, String after, String collation);
    public Xs.StringExpr substringAfter(Xs.StringExpr input, Xs.StringExpr after, Xs.StringExpr collation);
    public Xs.StringExpr substringBefore(String input, String before);
    public Xs.StringExpr substringBefore(Xs.StringExpr input, Xs.StringExpr before);
    public Xs.StringExpr substringBefore(String input, String before, String collation);
    public Xs.StringExpr substringBefore(Xs.StringExpr input, Xs.StringExpr before, Xs.StringExpr collation);
    public Xs.AnyAtomicTypeExpr sum(Xs.AnyAtomicTypeSeqExpr arg);
    public Xs.AnyAtomicTypeExpr sum(Xs.AnyAtomicTypeSeqExpr arg, Xs.AnyAtomicTypeExpr zero);
    public BaseType.ItemSeqExpr tail(BaseType.ItemSeqExpr seq);
    public Xs.DayTimeDurationExpr timezoneFromDate(Xs.DateExpr arg);
    public Xs.DayTimeDurationExpr timezoneFromDateTime(Xs.DateTimeExpr arg);
    public Xs.DayTimeDurationExpr timezoneFromTime(Xs.TimeExpr arg);
    public Xs.StringSeqExpr tokenize(String input, String pattern);
    public Xs.StringSeqExpr tokenize(Xs.StringExpr input, Xs.StringExpr pattern);
    public Xs.StringSeqExpr tokenize(String input, String pattern, String flags);
    public Xs.StringSeqExpr tokenize(Xs.StringExpr input, Xs.StringExpr pattern, Xs.StringExpr flags);
    public Xs.StringExpr translate(String src, String mapString, String transString);
    public Xs.StringExpr translate(Xs.StringExpr src, Xs.StringExpr mapString, Xs.StringExpr transString);
    public Xs.BooleanExpr trueExpr();
    public BaseType.ItemSeqExpr unordered(BaseType.ItemSeqExpr sourceSeq);
    public Xs.StringExpr upperCase(String string);
    public Xs.StringExpr upperCase(Xs.StringExpr string);
    public Xs.IntegerExpr yearFromDate(Xs.DateExpr arg);
    public Xs.IntegerExpr yearFromDateTime(Xs.DateTimeExpr arg);
    public Xs.IntegerExpr yearsFromDuration(Xs.DurationExpr arg);
}
