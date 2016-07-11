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
package com.marklogic.client.impl;

// TODO: single import
import com.marklogic.client.expression.BaseType;
import com.marklogic.client.expression.Xs;

import com.marklogic.client.expression.Fn;
import com.marklogic.client.expression.Xs;
 import com.marklogic.client.expression.BaseType;
 import com.marklogic.client.impl.XsExprImpl;
 import com.marklogic.client.impl.BaseTypeImpl;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class FnExprImpl implements Fn {
    private Xs xs = null;
    public FnExprImpl(Xs xs) {
        this.xs = xs;
    }
     @Override
        public Xs.NumericExpr abs(Xs.NumericExpr arg) {
        return new XsExprImpl.NumericCallImpl("fn", "abs", new Object[]{ arg });
    }
    @Override
        public Xs.DateExpr adjustDateToTimezone(Xs.DateExpr arg) {
        return new XsExprImpl.DateCallImpl("fn", "adjust-date-to-timezone", new Object[]{ arg });
    }
    @Override
        public Xs.DateExpr adjustDateToTimezone(Xs.DateExpr arg, Xs.DayTimeDurationExpr timezone) {
        return new XsExprImpl.DateCallImpl("fn", "adjust-date-to-timezone", new Object[]{ arg, timezone });
    }
    @Override
        public Xs.DateTimeExpr adjustDateTimeToTimezone(Xs.DateTimeExpr arg) {
        return new XsExprImpl.DateTimeCallImpl("fn", "adjust-dateTime-to-timezone", new Object[]{ arg });
    }
    @Override
        public Xs.DateTimeExpr adjustDateTimeToTimezone(Xs.DateTimeExpr arg, Xs.DayTimeDurationExpr timezone) {
        return new XsExprImpl.DateTimeCallImpl("fn", "adjust-dateTime-to-timezone", new Object[]{ arg, timezone });
    }
    @Override
        public Xs.TimeExpr adjustTimeToTimezone(Xs.TimeExpr arg) {
        return new XsExprImpl.TimeCallImpl("fn", "adjust-time-to-timezone", new Object[]{ arg });
    }
    @Override
        public Xs.TimeExpr adjustTimeToTimezone(Xs.TimeExpr arg, Xs.DayTimeDurationExpr timezone) {
        return new XsExprImpl.TimeCallImpl("fn", "adjust-time-to-timezone", new Object[]{ arg, timezone });
    }
    @Override
        public BaseType.ElementExpr analyzeString(String in, String regex) {
        return analyzeString((in == null) ? null : xs.string(in), xs.string(regex)); 
    }
    @Override
        public BaseType.ElementExpr analyzeString(Xs.StringExpr in, Xs.StringExpr regex) {
        return new BaseTypeImpl.ElementCallImpl("fn", "analyze-string", new Object[]{ in, regex });
    }
    @Override
        public BaseType.ElementExpr analyzeString(String in, String regex, String flags) {
        return analyzeString((in == null) ? null : xs.string(in), xs.string(regex), xs.string(flags)); 
    }
    @Override
        public BaseType.ElementExpr analyzeString(Xs.StringExpr in, Xs.StringExpr regex, Xs.StringExpr flags) {
        return new BaseTypeImpl.ElementCallImpl("fn", "analyze-string", new Object[]{ in, regex, flags });
    }
    @Override
        public Xs.AnyAtomicTypeExpr avg(Xs.AnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "avg", new Object[]{ arg });
    }
    @Override
        public Xs.AnyURIExpr baseUri(BaseType.NodeExpr arg) {
        return new XsExprImpl.AnyURICallImpl("fn", "base-uri", new Object[]{ arg });
    }
    @Override
        public Xs.BooleanExpr booleanExpr(BaseType.ItemSeqExpr arg) {
        return new XsExprImpl.BooleanCallImpl("fn", "boolean", new Object[]{ arg });
    }
    @Override
        public Xs.NumericExpr ceiling(Xs.NumericExpr arg) {
        return new XsExprImpl.NumericCallImpl("fn", "ceiling", new Object[]{ arg });
    }
    @Override
        public Xs.BooleanExpr codepointEqual(String comparand1, String comparand2) {
        return codepointEqual((comparand1 == null) ? null : xs.string(comparand1), (comparand2 == null) ? null : xs.string(comparand2)); 
    }
    @Override
        public Xs.BooleanExpr codepointEqual(Xs.StringExpr comparand1, Xs.StringExpr comparand2) {
        return new XsExprImpl.BooleanCallImpl("fn", "codepoint-equal", new Object[]{ comparand1, comparand2 });
    }
    @Override
        public Xs.StringExpr codepointsToString(Xs.IntegerSeqExpr arg) {
        return new XsExprImpl.StringCallImpl("fn", "codepoints-to-string", new Object[]{ arg });
    }
    @Override
        public Xs.IntegerExpr compare(String comparand1, String comparand2) {
        return compare((comparand1 == null) ? null : xs.string(comparand1), (comparand2 == null) ? null : xs.string(comparand2)); 
    }
    @Override
        public Xs.IntegerExpr compare(Xs.StringExpr comparand1, Xs.StringExpr comparand2) {
        return new XsExprImpl.IntegerCallImpl("fn", "compare", new Object[]{ comparand1, comparand2 });
    }
    @Override
        public Xs.IntegerExpr compare(String comparand1, String comparand2, String collation) {
        return compare((comparand1 == null) ? null : xs.string(comparand1), (comparand2 == null) ? null : xs.string(comparand2), xs.string(collation)); 
    }
    @Override
        public Xs.IntegerExpr compare(Xs.StringExpr comparand1, Xs.StringExpr comparand2, Xs.StringExpr collation) {
        return new XsExprImpl.IntegerCallImpl("fn", "compare", new Object[]{ comparand1, comparand2, collation });
    }
    @Override
        public Xs.StringExpr concat(Xs.AnyAtomicTypeExpr... parameter1) {
        return new XsExprImpl.StringCallImpl("fn", "concat", parameter1);
    }
    @Override
        public Xs.IntegerExpr count(BaseType.ItemSeqExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "count", new Object[]{ arg });
    }
    @Override
        public Xs.IntegerExpr count(BaseType.ItemSeqExpr arg, double maximum) {
        return count(arg, xs.doubleVal(maximum)); 
    }
    @Override
        public Xs.IntegerExpr count(BaseType.ItemSeqExpr arg, Xs.DoubleExpr maximum) {
        return new XsExprImpl.IntegerCallImpl("fn", "count", new Object[]{ arg, maximum });
    }
    @Override
    public Xs.DateExpr currentDate() {
        return new XsExprImpl.DateCallImpl("fn", "current-date", null);
    }
    @Override
    public Xs.DateTimeExpr currentDateTime() {
        return new XsExprImpl.DateTimeCallImpl("fn", "current-dateTime", null);
    }
    @Override
    public Xs.TimeExpr currentTime() {
        return new XsExprImpl.TimeCallImpl("fn", "current-time", null);
    }
    @Override
        public Xs.BooleanExpr contains(String parameter1, String parameter2) {
        return contains((parameter1 == null) ? null : xs.string(parameter1), (parameter2 == null) ? null : xs.string(parameter2)); 
    }
    @Override
        public Xs.BooleanExpr contains(Xs.StringExpr parameter1, Xs.StringExpr parameter2) {
        return new XsExprImpl.BooleanCallImpl("fn", "contains", new Object[]{ parameter1, parameter2 });
    }
    @Override
        public Xs.BooleanExpr contains(String parameter1, String parameter2, String collation) {
        return contains((parameter1 == null) ? null : xs.string(parameter1), (parameter2 == null) ? null : xs.string(parameter2), xs.string(collation)); 
    }
    @Override
        public Xs.BooleanExpr contains(Xs.StringExpr parameter1, Xs.StringExpr parameter2, Xs.StringExpr collation) {
        return new XsExprImpl.BooleanCallImpl("fn", "contains", new Object[]{ parameter1, parameter2, collation });
    }
    @Override
        public Xs.IntegerExpr dayFromDate(Xs.DateExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "day-from-date", new Object[]{ arg });
    }
    @Override
        public Xs.IntegerExpr dayFromDateTime(Xs.DateTimeExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "day-from-dateTime", new Object[]{ arg });
    }
    @Override
        public Xs.IntegerExpr daysFromDuration(Xs.DurationExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "days-from-duration", new Object[]{ arg });
    }
    @Override
        public Xs.BooleanExpr deepEqual(BaseType.ItemSeqExpr parameter1, BaseType.ItemSeqExpr parameter2) {
        return new XsExprImpl.BooleanCallImpl("fn", "deep-equal", new Object[]{ parameter1, parameter2 });
    }
    @Override
        public Xs.BooleanExpr deepEqual(BaseType.ItemSeqExpr parameter1, BaseType.ItemSeqExpr parameter2, String collation) {
        return deepEqual(parameter1, parameter2, xs.string(collation)); 
    }
    @Override
        public Xs.BooleanExpr deepEqual(BaseType.ItemSeqExpr parameter1, BaseType.ItemSeqExpr parameter2, Xs.StringExpr collation) {
        return new XsExprImpl.BooleanCallImpl("fn", "deep-equal", new Object[]{ parameter1, parameter2, collation });
    }
    @Override
    public Xs.StringExpr defaultCollation() {
        return new XsExprImpl.StringCallImpl("fn", "default-collation", null);
    }
    @Override
        public Xs.AnyAtomicTypeSeqExpr distinctValues(Xs.AnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.AnyAtomicTypeSeqCallImpl("fn", "distinct-values", new Object[]{ arg });
    }
    @Override
        public Xs.AnyAtomicTypeSeqExpr distinctValues(Xs.AnyAtomicTypeSeqExpr arg, String collation) {
        return distinctValues(arg, xs.string(collation)); 
    }
    @Override
        public Xs.AnyAtomicTypeSeqExpr distinctValues(Xs.AnyAtomicTypeSeqExpr arg, Xs.StringExpr collation) {
        return new XsExprImpl.AnyAtomicTypeSeqCallImpl("fn", "distinct-values", new Object[]{ arg, collation });
    }
    @Override
        public Xs.AnyURIExpr documentUri(BaseType.NodeExpr arg) {
        return new XsExprImpl.AnyURICallImpl("fn", "document-uri", new Object[]{ arg });
    }
    @Override
        public Xs.BooleanExpr empty(BaseType.ItemSeqExpr arg) {
        return new XsExprImpl.BooleanCallImpl("fn", "empty", new Object[]{ arg });
    }
    @Override
        public Xs.StringExpr encodeForUri(String uriPart) {
        return encodeForUri((uriPart == null) ? null : xs.string(uriPart)); 
    }
    @Override
        public Xs.StringExpr encodeForUri(Xs.StringExpr uriPart) {
        return new XsExprImpl.StringCallImpl("fn", "encode-for-uri", new Object[]{ uriPart });
    }
    @Override
        public Xs.BooleanExpr endsWith(String parameter1, String parameter2) {
        return endsWith((parameter1 == null) ? null : xs.string(parameter1), (parameter2 == null) ? null : xs.string(parameter2)); 
    }
    @Override
        public Xs.BooleanExpr endsWith(Xs.StringExpr parameter1, Xs.StringExpr parameter2) {
        return new XsExprImpl.BooleanCallImpl("fn", "ends-with", new Object[]{ parameter1, parameter2 });
    }
    @Override
        public Xs.BooleanExpr endsWith(String parameter1, String parameter2, String collation) {
        return endsWith((parameter1 == null) ? null : xs.string(parameter1), (parameter2 == null) ? null : xs.string(parameter2), xs.string(collation)); 
    }
    @Override
        public Xs.BooleanExpr endsWith(Xs.StringExpr parameter1, Xs.StringExpr parameter2, Xs.StringExpr collation) {
        return new XsExprImpl.BooleanCallImpl("fn", "ends-with", new Object[]{ parameter1, parameter2, collation });
    }
    @Override
        public Xs.StringExpr escapeHtmlUri(String uriPart) {
        return escapeHtmlUri((uriPart == null) ? null : xs.string(uriPart)); 
    }
    @Override
        public Xs.StringExpr escapeHtmlUri(Xs.StringExpr uriPart) {
        return new XsExprImpl.StringCallImpl("fn", "escape-html-uri", new Object[]{ uriPart });
    }
    @Override
        public Xs.BooleanExpr exists(BaseType.ItemSeqExpr arg) {
        return new XsExprImpl.BooleanCallImpl("fn", "exists", new Object[]{ arg });
    }
    @Override
    public Xs.BooleanExpr falseExpr() {
        return new XsExprImpl.BooleanCallImpl("fn", "false", null);
    }
    @Override
        public Xs.NumericExpr floor(Xs.NumericExpr arg) {
        return new XsExprImpl.NumericCallImpl("fn", "floor", new Object[]{ arg });
    }
    @Override
        public Xs.StringExpr formatDate(Xs.DateExpr value, String picture) {
        return formatDate(value, xs.string(picture)); 
    }
    @Override
        public Xs.StringExpr formatDate(Xs.DateExpr value, Xs.StringExpr picture) {
        return new XsExprImpl.StringCallImpl("fn", "format-date", new Object[]{ value, picture });
    }
    @Override
        public Xs.StringExpr formatDateTime(Xs.DateTimeExpr value, String picture) {
        return formatDateTime(value, xs.string(picture)); 
    }
    @Override
        public Xs.StringExpr formatDateTime(Xs.DateTimeExpr value, Xs.StringExpr picture) {
        return new XsExprImpl.StringCallImpl("fn", "format-dateTime", new Object[]{ value, picture });
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture) {
        return formatNumber(value, xs.string(picture)); 
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture) {
        return new XsExprImpl.StringCallImpl("fn", "format-number", new Object[]{ value, picture });
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, String picture, String decimalFormatName) {
        return formatNumber(value, xs.string(picture), xs.string(decimalFormatName)); 
    }
    @Override
        public Xs.StringExpr formatNumber(Xs.NumericSeqExpr value, Xs.StringExpr picture, Xs.StringExpr decimalFormatName) {
        return new XsExprImpl.StringCallImpl("fn", "format-number", new Object[]{ value, picture, decimalFormatName });
    }
    @Override
        public Xs.StringExpr formatTime(Xs.TimeExpr value, String picture) {
        return formatTime(value, xs.string(picture)); 
    }
    @Override
        public Xs.StringExpr formatTime(Xs.TimeExpr value, Xs.StringExpr picture) {
        return new XsExprImpl.StringCallImpl("fn", "format-time", new Object[]{ value, picture });
    }
    @Override
        public Xs.StringExpr generateId(BaseType.NodeExpr node) {
        return new XsExprImpl.StringCallImpl("fn", "generate-id", new Object[]{ node });
    }
    @Override
        public BaseType.ItemExpr head(BaseType.ItemSeqExpr arg1) {
        return new BaseTypeImpl.ItemCallImpl("fn", "head", new Object[]{ arg1 });
    }
    @Override
        public Xs.IntegerExpr hoursFromDateTime(Xs.DateTimeExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "hours-from-dateTime", new Object[]{ arg });
    }
    @Override
        public Xs.IntegerExpr hoursFromDuration(Xs.DurationExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "hours-from-duration", new Object[]{ arg });
    }
    @Override
        public Xs.IntegerExpr hoursFromTime(Xs.TimeExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "hours-from-time", new Object[]{ arg });
    }
    @Override
    public Xs.DayTimeDurationExpr implicitTimezone() {
        return new XsExprImpl.DayTimeDurationCallImpl("fn", "implicit-timezone", null);
    }
    @Override
        public Xs.IntegerSeqExpr indexOf(Xs.AnyAtomicTypeSeqExpr seqParam, Xs.AnyAtomicTypeExpr srchParam) {
        return new XsExprImpl.IntegerSeqCallImpl("fn", "index-of", new Object[]{ seqParam, srchParam });
    }
    @Override
        public Xs.IntegerSeqExpr indexOf(Xs.AnyAtomicTypeSeqExpr seqParam, Xs.AnyAtomicTypeExpr srchParam, String collationLiteral) {
        return indexOf(seqParam, srchParam, xs.string(collationLiteral)); 
    }
    @Override
        public Xs.IntegerSeqExpr indexOf(Xs.AnyAtomicTypeSeqExpr seqParam, Xs.AnyAtomicTypeExpr srchParam, Xs.StringExpr collationLiteral) {
        return new XsExprImpl.IntegerSeqCallImpl("fn", "index-of", new Object[]{ seqParam, srchParam, collationLiteral });
    }
    @Override
        public Xs.StringSeqExpr inScopePrefixes(BaseType.ElementExpr element) {
        return new XsExprImpl.StringSeqCallImpl("fn", "in-scope-prefixes", new Object[]{ element });
    }
    @Override
        public BaseType.ItemSeqExpr insertBefore(BaseType.ItemSeqExpr target, Xs.IntegerExpr position, BaseType.ItemSeqExpr inserts) {
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "insert-before", new Object[]{ target, position, inserts });
    }
    @Override
        public Xs.StringExpr iriToUri(String uriPart) {
        return iriToUri((uriPart == null) ? null : xs.string(uriPart)); 
    }
    @Override
        public Xs.StringExpr iriToUri(Xs.StringExpr uriPart) {
        return new XsExprImpl.StringCallImpl("fn", "iri-to-uri", new Object[]{ uriPart });
    }
    @Override
        public Xs.BooleanExpr lang(String testlang, BaseType.NodeExpr node) {
        return lang((testlang == null) ? null : xs.string(testlang), node); 
    }
    @Override
        public Xs.BooleanExpr lang(Xs.StringExpr testlang, BaseType.NodeExpr node) {
        return new XsExprImpl.BooleanCallImpl("fn", "lang", new Object[]{ testlang, node });
    }
    @Override
        public Xs.StringExpr localName(BaseType.NodeExpr arg) {
        return new XsExprImpl.StringCallImpl("fn", "local-name", new Object[]{ arg });
    }
    @Override
        public Xs.NCNameExpr localNameFromQName(Xs.QNameExpr arg) {
        return new XsExprImpl.NCNameCallImpl("fn", "local-name-from-QName", new Object[]{ arg });
    }
    @Override
        public Xs.StringExpr lowerCase(String string) {
        return lowerCase((string == null) ? null : xs.string(string)); 
    }
    @Override
        public Xs.StringExpr lowerCase(Xs.StringExpr string) {
        return new XsExprImpl.StringCallImpl("fn", "lower-case", new Object[]{ string });
    }
    @Override
        public Xs.BooleanExpr matches(String input, String pattern) {
        return matches((input == null) ? null : xs.string(input), xs.string(pattern)); 
    }
    @Override
        public Xs.BooleanExpr matches(Xs.StringExpr input, Xs.StringExpr pattern) {
        return new XsExprImpl.BooleanCallImpl("fn", "matches", new Object[]{ input, pattern });
    }
    @Override
        public Xs.BooleanExpr matches(String input, String pattern, String flags) {
        return matches((input == null) ? null : xs.string(input), xs.string(pattern), xs.string(flags)); 
    }
    @Override
        public Xs.BooleanExpr matches(Xs.StringExpr input, Xs.StringExpr pattern, Xs.StringExpr flags) {
        return new XsExprImpl.BooleanCallImpl("fn", "matches", new Object[]{ input, pattern, flags });
    }
    @Override
        public Xs.AnyAtomicTypeExpr max(Xs.AnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "max", new Object[]{ arg });
    }
    @Override
        public Xs.AnyAtomicTypeExpr max(Xs.AnyAtomicTypeSeqExpr arg, String collation) {
        return max(arg, xs.string(collation)); 
    }
    @Override
        public Xs.AnyAtomicTypeExpr max(Xs.AnyAtomicTypeSeqExpr arg, Xs.StringExpr collation) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "max", new Object[]{ arg, collation });
    }
    @Override
        public Xs.AnyAtomicTypeExpr min(Xs.AnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "min", new Object[]{ arg });
    }
    @Override
        public Xs.AnyAtomicTypeExpr min(Xs.AnyAtomicTypeSeqExpr arg, String collation) {
        return min(arg, xs.string(collation)); 
    }
    @Override
        public Xs.AnyAtomicTypeExpr min(Xs.AnyAtomicTypeSeqExpr arg, Xs.StringExpr collation) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "min", new Object[]{ arg, collation });
    }
    @Override
        public Xs.IntegerExpr minutesFromDateTime(Xs.DateTimeExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "minutes-from-dateTime", new Object[]{ arg });
    }
    @Override
        public Xs.IntegerExpr minutesFromDuration(Xs.DurationExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "minutes-from-duration", new Object[]{ arg });
    }
    @Override
        public Xs.IntegerExpr minutesFromTime(Xs.TimeExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "minutes-from-time", new Object[]{ arg });
    }
    @Override
        public Xs.IntegerExpr monthFromDate(Xs.DateExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "month-from-date", new Object[]{ arg });
    }
    @Override
        public Xs.IntegerExpr monthFromDateTime(Xs.DateTimeExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "month-from-dateTime", new Object[]{ arg });
    }
    @Override
        public Xs.IntegerExpr monthsFromDuration(Xs.DurationExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "months-from-duration", new Object[]{ arg });
    }
    @Override
        public Xs.StringExpr name(BaseType.NodeExpr arg) {
        return new XsExprImpl.StringCallImpl("fn", "name", new Object[]{ arg });
    }
    @Override
        public Xs.AnyURIExpr namespaceUri(BaseType.NodeExpr arg) {
        return new XsExprImpl.AnyURICallImpl("fn", "namespace-uri", new Object[]{ arg });
    }
    @Override
        public Xs.AnyURIExpr namespaceUriForPrefix(String prefix, BaseType.ElementExpr element) {
        return namespaceUriForPrefix((prefix == null) ? null : xs.string(prefix), element); 
    }
    @Override
        public Xs.AnyURIExpr namespaceUriForPrefix(Xs.StringExpr prefix, BaseType.ElementExpr element) {
        return new XsExprImpl.AnyURICallImpl("fn", "namespace-uri-for-prefix", new Object[]{ prefix, element });
    }
    @Override
        public Xs.AnyURIExpr namespaceUriFromQName(Xs.QNameExpr arg) {
        return new XsExprImpl.AnyURICallImpl("fn", "namespace-uri-from-QName", new Object[]{ arg });
    }
    @Override
        public Xs.BooleanExpr nilled() {
        return new XsExprImpl.BooleanCallImpl("fn", "nilled", new Object[]{  });
    }
    @Override
        public Xs.BooleanExpr nilled(BaseType.NodeExpr arg) {
        return new XsExprImpl.BooleanCallImpl("fn", "nilled", new Object[]{ arg });
    }
    @Override
        public Xs.QNameExpr nodeName() {
        return new XsExprImpl.QNameCallImpl("fn", "node-name", new Object[]{  });
    }
    @Override
        public Xs.QNameExpr nodeName(BaseType.NodeExpr arg) {
        return new XsExprImpl.QNameCallImpl("fn", "node-name", new Object[]{ arg });
    }
    @Override
        public Xs.StringExpr normalizeSpace(String input) {
        return normalizeSpace((input == null) ? null : xs.string(input)); 
    }
    @Override
        public Xs.StringExpr normalizeSpace(Xs.StringExpr input) {
        return new XsExprImpl.StringCallImpl("fn", "normalize-space", new Object[]{ input });
    }
    @Override
        public Xs.StringExpr normalizeUnicode(String arg) {
        return normalizeUnicode((arg == null) ? null : xs.string(arg)); 
    }
    @Override
        public Xs.StringExpr normalizeUnicode(Xs.StringExpr arg) {
        return new XsExprImpl.StringCallImpl("fn", "normalize-unicode", new Object[]{ arg });
    }
    @Override
        public Xs.StringExpr normalizeUnicode(String arg, String normalizationForm) {
        return normalizeUnicode((arg == null) ? null : xs.string(arg), xs.string(normalizationForm)); 
    }
    @Override
        public Xs.StringExpr normalizeUnicode(Xs.StringExpr arg, Xs.StringExpr normalizationForm) {
        return new XsExprImpl.StringCallImpl("fn", "normalize-unicode", new Object[]{ arg, normalizationForm });
    }
    @Override
        public Xs.BooleanExpr not(BaseType.ItemSeqExpr arg) {
        return new XsExprImpl.BooleanCallImpl("fn", "not", new Object[]{ arg });
    }
    @Override
        public Xs.DoubleExpr number(Xs.AnyAtomicTypeExpr arg) {
        return new XsExprImpl.DoubleCallImpl("fn", "number", new Object[]{ arg });
    }
    @Override
        public Xs.NCNameExpr prefixFromQName(Xs.QNameExpr arg) {
        return new XsExprImpl.NCNameCallImpl("fn", "prefix-from-QName", new Object[]{ arg });
    }
    @Override
        public Xs.QNameExpr QName(String paramURI, String paramQName) {
        return QName((paramURI == null) ? null : xs.string(paramURI), xs.string(paramQName)); 
    }
    @Override
        public Xs.QNameExpr QName(Xs.StringExpr paramURI, Xs.StringExpr paramQName) {
        return new XsExprImpl.QNameCallImpl("fn", "QName", new Object[]{ paramURI, paramQName });
    }
    @Override
        public BaseType.ItemSeqExpr remove(BaseType.ItemSeqExpr target, Xs.IntegerExpr position) {
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "remove", new Object[]{ target, position });
    }
    @Override
        public Xs.StringExpr replace(String input, String pattern, String replacement) {
        return replace((input == null) ? null : xs.string(input), xs.string(pattern), xs.string(replacement)); 
    }
    @Override
        public Xs.StringExpr replace(Xs.StringExpr input, Xs.StringExpr pattern, Xs.StringExpr replacement) {
        return new XsExprImpl.StringCallImpl("fn", "replace", new Object[]{ input, pattern, replacement });
    }
    @Override
        public Xs.StringExpr replace(String input, String pattern, String replacement, String flags) {
        return replace((input == null) ? null : xs.string(input), xs.string(pattern), xs.string(replacement), xs.string(flags)); 
    }
    @Override
        public Xs.StringExpr replace(Xs.StringExpr input, Xs.StringExpr pattern, Xs.StringExpr replacement, Xs.StringExpr flags) {
        return new XsExprImpl.StringCallImpl("fn", "replace", new Object[]{ input, pattern, replacement, flags });
    }
    @Override
        public Xs.QNameExpr resolveQName(String qname, BaseType.ElementExpr element) {
        return resolveQName((qname == null) ? null : xs.string(qname), element); 
    }
    @Override
        public Xs.QNameExpr resolveQName(Xs.StringExpr qname, BaseType.ElementExpr element) {
        return new XsExprImpl.QNameCallImpl("fn", "resolve-QName", new Object[]{ qname, element });
    }
    @Override
        public Xs.AnyURIExpr resolveUri(String relative, String base) {
        return resolveUri((relative == null) ? null : xs.string(relative), xs.string(base)); 
    }
    @Override
        public Xs.AnyURIExpr resolveUri(Xs.StringExpr relative, Xs.StringExpr base) {
        return new XsExprImpl.AnyURICallImpl("fn", "resolve-uri", new Object[]{ relative, base });
    }
    @Override
        public BaseType.ItemSeqExpr reverse(BaseType.ItemSeqExpr target) {
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "reverse", new Object[]{ target });
    }
    @Override
        public BaseType.NodeExpr root(BaseType.NodeExpr arg) {
        return new BaseTypeImpl.NodeCallImpl("fn", "root", new Object[]{ arg });
    }
    @Override
        public Xs.NumericExpr round(Xs.NumericExpr arg) {
        return new XsExprImpl.NumericCallImpl("fn", "round", new Object[]{ arg });
    }
    @Override
        public Xs.NumericExpr roundHalfToEven(Xs.NumericExpr arg) {
        return new XsExprImpl.NumericCallImpl("fn", "round-half-to-even", new Object[]{ arg });
    }
    @Override
        public Xs.NumericExpr roundHalfToEven(Xs.NumericExpr arg, Xs.IntegerExpr precision) {
        return new XsExprImpl.NumericCallImpl("fn", "round-half-to-even", new Object[]{ arg, precision });
    }
    @Override
        public Xs.DecimalExpr secondsFromDateTime(Xs.DateTimeExpr arg) {
        return new XsExprImpl.DecimalCallImpl("fn", "seconds-from-dateTime", new Object[]{ arg });
    }
    @Override
        public Xs.DecimalExpr secondsFromDuration(Xs.DurationExpr arg) {
        return new XsExprImpl.DecimalCallImpl("fn", "seconds-from-duration", new Object[]{ arg });
    }
    @Override
        public Xs.DecimalExpr secondsFromTime(Xs.TimeExpr arg) {
        return new XsExprImpl.DecimalCallImpl("fn", "seconds-from-time", new Object[]{ arg });
    }
    @Override
        public Xs.BooleanExpr startsWith(String parameter1, String parameter2) {
        return startsWith((parameter1 == null) ? null : xs.string(parameter1), (parameter2 == null) ? null : xs.string(parameter2)); 
    }
    @Override
        public Xs.BooleanExpr startsWith(Xs.StringExpr parameter1, Xs.StringExpr parameter2) {
        return new XsExprImpl.BooleanCallImpl("fn", "starts-with", new Object[]{ parameter1, parameter2 });
    }
    @Override
        public Xs.BooleanExpr startsWith(String parameter1, String parameter2, String collation) {
        return startsWith((parameter1 == null) ? null : xs.string(parameter1), (parameter2 == null) ? null : xs.string(parameter2), xs.string(collation)); 
    }
    @Override
        public Xs.BooleanExpr startsWith(Xs.StringExpr parameter1, Xs.StringExpr parameter2, Xs.StringExpr collation) {
        return new XsExprImpl.BooleanCallImpl("fn", "starts-with", new Object[]{ parameter1, parameter2, collation });
    }
    @Override
        public Xs.StringExpr string(BaseType.ItemExpr arg) {
        return new XsExprImpl.StringCallImpl("fn", "string", new Object[]{ arg });
    }
    @Override
        public Xs.StringExpr stringJoin(String... parameter1) {
        return stringJoin((parameter1 == null) ? null : xs.strings(parameter1)); 
    }
    @Override
        public Xs.StringExpr stringJoin(Xs.StringSeqExpr parameter1) {
        return new XsExprImpl.StringCallImpl("fn", "string-join", new Object[]{ parameter1 });
    }
    @Override
        public Xs.StringExpr stringJoin(String parameter1, String parameter2) {
        return stringJoin((parameter1 == null) ? null : xs.strings(parameter1), xs.string(parameter2)); 
    }
    @Override
        public Xs.StringExpr stringJoin(Xs.StringSeqExpr parameter1, Xs.StringExpr parameter2) {
        return new XsExprImpl.StringCallImpl("fn", "string-join", new Object[]{ parameter1, parameter2 });
    }
    @Override
        public Xs.IntegerExpr stringLength(String sourceString) {
        return stringLength((sourceString == null) ? null : xs.string(sourceString)); 
    }
    @Override
        public Xs.IntegerExpr stringLength(Xs.StringExpr sourceString) {
        return new XsExprImpl.IntegerCallImpl("fn", "string-length", new Object[]{ sourceString });
    }
    @Override
        public Xs.IntegerSeqExpr stringToCodepoints(String arg) {
        return stringToCodepoints((arg == null) ? null : xs.string(arg)); 
    }
    @Override
        public Xs.IntegerSeqExpr stringToCodepoints(Xs.StringExpr arg) {
        return new XsExprImpl.IntegerSeqCallImpl("fn", "string-to-codepoints", new Object[]{ arg });
    }
    @Override
        public BaseType.ItemSeqExpr subsequence(BaseType.ItemSeqExpr sourceSeq, Xs.NumericExpr startingLoc) {
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "subsequence", new Object[]{ sourceSeq, startingLoc });
    }
    @Override
        public BaseType.ItemSeqExpr subsequence(BaseType.ItemSeqExpr sourceSeq, Xs.NumericExpr startingLoc, Xs.NumericExpr length) {
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "subsequence", new Object[]{ sourceSeq, startingLoc, length });
    }
    @Override
        public Xs.StringExpr substring(String sourceString, Xs.NumericExpr startingLoc) {
        return substring((sourceString == null) ? null : xs.string(sourceString), startingLoc); 
    }
    @Override
        public Xs.StringExpr substring(Xs.StringExpr sourceString, Xs.NumericExpr startingLoc) {
        return new XsExprImpl.StringCallImpl("fn", "substring", new Object[]{ sourceString, startingLoc });
    }
    @Override
        public Xs.StringExpr substring(String sourceString, Xs.NumericExpr startingLoc, Xs.NumericExpr length) {
        return substring((sourceString == null) ? null : xs.string(sourceString), startingLoc, length); 
    }
    @Override
        public Xs.StringExpr substring(Xs.StringExpr sourceString, Xs.NumericExpr startingLoc, Xs.NumericExpr length) {
        return new XsExprImpl.StringCallImpl("fn", "substring", new Object[]{ sourceString, startingLoc, length });
    }
    @Override
        public Xs.StringExpr substringAfter(String input, String after) {
        return substringAfter((input == null) ? null : xs.string(input), (after == null) ? null : xs.string(after)); 
    }
    @Override
        public Xs.StringExpr substringAfter(Xs.StringExpr input, Xs.StringExpr after) {
        return new XsExprImpl.StringCallImpl("fn", "substring-after", new Object[]{ input, after });
    }
    @Override
        public Xs.StringExpr substringAfter(String input, String after, String collation) {
        return substringAfter((input == null) ? null : xs.string(input), (after == null) ? null : xs.string(after), xs.string(collation)); 
    }
    @Override
        public Xs.StringExpr substringAfter(Xs.StringExpr input, Xs.StringExpr after, Xs.StringExpr collation) {
        return new XsExprImpl.StringCallImpl("fn", "substring-after", new Object[]{ input, after, collation });
    }
    @Override
        public Xs.StringExpr substringBefore(String input, String before) {
        return substringBefore((input == null) ? null : xs.string(input), (before == null) ? null : xs.string(before)); 
    }
    @Override
        public Xs.StringExpr substringBefore(Xs.StringExpr input, Xs.StringExpr before) {
        return new XsExprImpl.StringCallImpl("fn", "substring-before", new Object[]{ input, before });
    }
    @Override
        public Xs.StringExpr substringBefore(String input, String before, String collation) {
        return substringBefore((input == null) ? null : xs.string(input), (before == null) ? null : xs.string(before), xs.string(collation)); 
    }
    @Override
        public Xs.StringExpr substringBefore(Xs.StringExpr input, Xs.StringExpr before, Xs.StringExpr collation) {
        return new XsExprImpl.StringCallImpl("fn", "substring-before", new Object[]{ input, before, collation });
    }
    @Override
        public Xs.AnyAtomicTypeExpr sum(Xs.AnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "sum", new Object[]{ arg });
    }
    @Override
        public Xs.AnyAtomicTypeExpr sum(Xs.AnyAtomicTypeSeqExpr arg, Xs.AnyAtomicTypeExpr zero) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "sum", new Object[]{ arg, zero });
    }
    @Override
        public BaseType.ItemSeqExpr tail(BaseType.ItemSeqExpr seq) {
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "tail", new Object[]{ seq });
    }
    @Override
        public Xs.DayTimeDurationExpr timezoneFromDate(Xs.DateExpr arg) {
        return new XsExprImpl.DayTimeDurationCallImpl("fn", "timezone-from-date", new Object[]{ arg });
    }
    @Override
        public Xs.DayTimeDurationExpr timezoneFromDateTime(Xs.DateTimeExpr arg) {
        return new XsExprImpl.DayTimeDurationCallImpl("fn", "timezone-from-dateTime", new Object[]{ arg });
    }
    @Override
        public Xs.DayTimeDurationExpr timezoneFromTime(Xs.TimeExpr arg) {
        return new XsExprImpl.DayTimeDurationCallImpl("fn", "timezone-from-time", new Object[]{ arg });
    }
    @Override
        public Xs.StringSeqExpr tokenize(String input, String pattern) {
        return tokenize((input == null) ? null : xs.string(input), xs.string(pattern)); 
    }
    @Override
        public Xs.StringSeqExpr tokenize(Xs.StringExpr input, Xs.StringExpr pattern) {
        return new XsExprImpl.StringSeqCallImpl("fn", "tokenize", new Object[]{ input, pattern });
    }
    @Override
        public Xs.StringSeqExpr tokenize(String input, String pattern, String flags) {
        return tokenize((input == null) ? null : xs.string(input), xs.string(pattern), xs.string(flags)); 
    }
    @Override
        public Xs.StringSeqExpr tokenize(Xs.StringExpr input, Xs.StringExpr pattern, Xs.StringExpr flags) {
        return new XsExprImpl.StringSeqCallImpl("fn", "tokenize", new Object[]{ input, pattern, flags });
    }
    @Override
        public Xs.StringExpr translate(String src, String mapString, String transString) {
        return translate((src == null) ? null : xs.string(src), xs.string(mapString), xs.string(transString)); 
    }
    @Override
        public Xs.StringExpr translate(Xs.StringExpr src, Xs.StringExpr mapString, Xs.StringExpr transString) {
        return new XsExprImpl.StringCallImpl("fn", "translate", new Object[]{ src, mapString, transString });
    }
    @Override
    public Xs.BooleanExpr trueExpr() {
        return new XsExprImpl.BooleanCallImpl("fn", "true", null);
    }
    @Override
        public BaseType.ItemSeqExpr unordered(BaseType.ItemSeqExpr sourceSeq) {
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "unordered", new Object[]{ sourceSeq });
    }
    @Override
        public Xs.StringExpr upperCase(String string) {
        return upperCase((string == null) ? null : xs.string(string)); 
    }
    @Override
        public Xs.StringExpr upperCase(Xs.StringExpr string) {
        return new XsExprImpl.StringCallImpl("fn", "upper-case", new Object[]{ string });
    }
    @Override
        public Xs.IntegerExpr yearFromDate(Xs.DateExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "year-from-date", new Object[]{ arg });
    }
    @Override
        public Xs.IntegerExpr yearFromDateTime(Xs.DateTimeExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "year-from-dateTime", new Object[]{ arg });
    }
    @Override
        public Xs.IntegerExpr yearsFromDuration(Xs.DurationExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "years-from-duration", new Object[]{ arg });
    }
}
