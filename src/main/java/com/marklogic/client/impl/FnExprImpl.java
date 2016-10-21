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

import com.marklogic.client.expression.XsExpr;
import com.marklogic.client.expression.XsValue;

import com.marklogic.client.expression.FnExpr;
import com.marklogic.client.type.XsNCNameExpr;
 import com.marklogic.client.type.XsStringSeqExpr;
 import com.marklogic.client.type.XsTimeExpr;
 import com.marklogic.client.type.XsIntegerExpr;
 import com.marklogic.client.type.NodeExpr;
 import com.marklogic.client.type.XsDoubleExpr;
 import com.marklogic.client.type.XsNumericSeqExpr;
 import com.marklogic.client.type.XsDayTimeDurationExpr;
 import com.marklogic.client.type.ElementNodeExpr;
 import com.marklogic.client.type.XsDecimalExpr;
 import com.marklogic.client.type.XsDurationExpr;
 import com.marklogic.client.type.XsStringExpr;
 import com.marklogic.client.type.XsAnyAtomicTypeExpr;
 import com.marklogic.client.type.XsAnyURIExpr;
 import com.marklogic.client.type.XsAnyAtomicTypeSeqExpr;
 import com.marklogic.client.type.XsNumericExpr;
 import com.marklogic.client.type.XsDateExpr;
 import com.marklogic.client.type.XsQNameExpr;
 import com.marklogic.client.type.XsBooleanExpr;
 import com.marklogic.client.type.XsIntegerSeqExpr;
 import com.marklogic.client.type.ItemExpr;
 import com.marklogic.client.type.XsDateTimeExpr;
 import com.marklogic.client.type.ItemSeqExpr;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class FnExprImpl implements FnExpr {
    private XsExprImpl xs = null;
    public FnExprImpl(XsExprImpl xs) {
        this.xs = xs;
    }
     @Override
        public XsNumericExpr abs(XsNumericExpr arg) {
        return new XsExprImpl.XsNumericCallImpl("fn", "abs", new Object[]{ arg });
    }
    @Override
        public XsDateExpr adjustDateToTimezone(XsDateExpr arg) {
        return new XsExprImpl.XsDateCallImpl("fn", "adjust-date-to-timezone", new Object[]{ arg });
    }
    @Override
        public XsDateExpr adjustDateToTimezone(XsDateExpr arg, XsDayTimeDurationExpr timezone) {
        return new XsExprImpl.XsDateCallImpl("fn", "adjust-date-to-timezone", new Object[]{ arg, timezone });
    }
    @Override
        public XsDateTimeExpr adjustDateTimeToTimezone(XsDateTimeExpr arg) {
        return new XsExprImpl.XsDateTimeCallImpl("fn", "adjust-dateTime-to-timezone", new Object[]{ arg });
    }
    @Override
        public XsDateTimeExpr adjustDateTimeToTimezone(XsDateTimeExpr arg, XsDayTimeDurationExpr timezone) {
        return new XsExprImpl.XsDateTimeCallImpl("fn", "adjust-dateTime-to-timezone", new Object[]{ arg, timezone });
    }
    @Override
        public XsTimeExpr adjustTimeToTimezone(XsTimeExpr arg) {
        return new XsExprImpl.XsTimeCallImpl("fn", "adjust-time-to-timezone", new Object[]{ arg });
    }
    @Override
        public XsTimeExpr adjustTimeToTimezone(XsTimeExpr arg, XsDayTimeDurationExpr timezone) {
        return new XsExprImpl.XsTimeCallImpl("fn", "adjust-time-to-timezone", new Object[]{ arg, timezone });
    }
    @Override
        public ElementNodeExpr analyzeString(String in, String regex) {
        return analyzeString(xs.string(in), xs.string(regex)); 
    }
    @Override
        public ElementNodeExpr analyzeString(XsStringExpr in, XsStringExpr regex) {
        return new BaseTypeImpl.ElementNodeCallImpl("fn", "analyze-string", new Object[]{ in, regex });
    }
    @Override
        public ElementNodeExpr analyzeString(String in, String regex, String flags) {
        return analyzeString(xs.string(in), xs.string(regex), (flags == null) ? null : xs.string(flags)); 
    }
    @Override
        public ElementNodeExpr analyzeString(XsStringExpr in, XsStringExpr regex, XsStringExpr flags) {
        return new BaseTypeImpl.ElementNodeCallImpl("fn", "analyze-string", new Object[]{ in, regex, flags });
    }
    @Override
        public XsAnyAtomicTypeExpr avg(XsAnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.XsAnyAtomicTypeCallImpl("fn", "avg", new Object[]{ arg });
    }
    @Override
        public XsAnyURIExpr baseUri(NodeExpr arg) {
        return new XsExprImpl.XsAnyURICallImpl("fn", "base-uri", new Object[]{ arg });
    }
    @Override
        public XsBooleanExpr booleanExpr(ItemSeqExpr arg) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "boolean", new Object[]{ arg });
    }
    @Override
        public XsNumericExpr ceiling(XsNumericExpr arg) {
        return new XsExprImpl.XsNumericCallImpl("fn", "ceiling", new Object[]{ arg });
    }
    @Override
        public XsBooleanExpr codepointEqual(String comparand1, String comparand2) {
        return codepointEqual(xs.string(comparand1), xs.string(comparand2)); 
    }
    @Override
        public XsBooleanExpr codepointEqual(XsStringExpr comparand1, XsStringExpr comparand2) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "codepoint-equal", new Object[]{ comparand1, comparand2 });
    }
    @Override
        public XsStringExpr codepointsToString(XsIntegerSeqExpr arg) {
        return new XsExprImpl.XsStringCallImpl("fn", "codepoints-to-string", new Object[]{ arg });
    }
    @Override
        public XsIntegerExpr compare(String comparand1, String comparand2) {
        return compare(xs.string(comparand1), xs.string(comparand2)); 
    }
    @Override
        public XsIntegerExpr compare(XsStringExpr comparand1, XsStringExpr comparand2) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "compare", new Object[]{ comparand1, comparand2 });
    }
    @Override
        public XsIntegerExpr compare(String comparand1, String comparand2, String collation) {
        return compare(xs.string(comparand1), xs.string(comparand2), (collation == null) ? null : xs.string(collation)); 
    }
    @Override
        public XsIntegerExpr compare(XsStringExpr comparand1, XsStringExpr comparand2, XsStringExpr collation) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "compare", new Object[]{ comparand1, comparand2, collation });
    }
    @Override
        public XsStringExpr concat(XsAnyAtomicTypeExpr... parameter1) {
        return new XsExprImpl.XsStringCallImpl("fn", "concat", parameter1);
    }
    @Override
        public XsIntegerExpr count(ItemSeqExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "count", new Object[]{ arg });
    }
    @Override
        public XsIntegerExpr count(ItemSeqExpr arg, double maximum) {
        return count(arg, xs.doubleVal(maximum)); 
    }
    @Override
        public XsIntegerExpr count(ItemSeqExpr arg, XsDoubleExpr maximum) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "count", new Object[]{ arg, maximum });
    }
    @Override
    public XsDateExpr currentDate() {
        return new XsExprImpl.XsDateCallImpl("fn", "current-date", null);
    }
    @Override
    public XsDateTimeExpr currentDateTime() {
        return new XsExprImpl.XsDateTimeCallImpl("fn", "current-dateTime", null);
    }
    @Override
    public XsTimeExpr currentTime() {
        return new XsExprImpl.XsTimeCallImpl("fn", "current-time", null);
    }
    @Override
        public XsBooleanExpr contains(String parameter1, String parameter2) {
        return contains(xs.string(parameter1), xs.string(parameter2)); 
    }
    @Override
        public XsBooleanExpr contains(XsStringExpr parameter1, XsStringExpr parameter2) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "contains", new Object[]{ parameter1, parameter2 });
    }
    @Override
        public XsBooleanExpr contains(String parameter1, String parameter2, String collation) {
        return contains(xs.string(parameter1), xs.string(parameter2), (collation == null) ? null : xs.string(collation)); 
    }
    @Override
        public XsBooleanExpr contains(XsStringExpr parameter1, XsStringExpr parameter2, XsStringExpr collation) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "contains", new Object[]{ parameter1, parameter2, collation });
    }
    @Override
        public XsIntegerExpr dayFromDate(XsDateExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "day-from-date", new Object[]{ arg });
    }
    @Override
        public XsIntegerExpr dayFromDateTime(XsDateTimeExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "day-from-dateTime", new Object[]{ arg });
    }
    @Override
        public XsIntegerExpr daysFromDuration(XsDurationExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "days-from-duration", new Object[]{ arg });
    }
    @Override
        public XsBooleanExpr deepEqual(ItemSeqExpr parameter1, ItemSeqExpr parameter2) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "deep-equal", new Object[]{ parameter1, parameter2 });
    }
    @Override
        public XsBooleanExpr deepEqual(ItemSeqExpr parameter1, ItemSeqExpr parameter2, String collation) {
        return deepEqual(parameter1, parameter2, (collation == null) ? null : xs.string(collation)); 
    }
    @Override
        public XsBooleanExpr deepEqual(ItemSeqExpr parameter1, ItemSeqExpr parameter2, XsStringExpr collation) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "deep-equal", new Object[]{ parameter1, parameter2, collation });
    }
    @Override
    public XsStringExpr defaultCollation() {
        return new XsExprImpl.XsStringCallImpl("fn", "default-collation", null);
    }
    @Override
        public XsAnyAtomicTypeSeqExpr distinctValues(XsAnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.XsAnyAtomicTypeSeqCallImpl("fn", "distinct-values", new Object[]{ arg });
    }
    @Override
        public XsAnyAtomicTypeSeqExpr distinctValues(XsAnyAtomicTypeSeqExpr arg, String collation) {
        return distinctValues(arg, (collation == null) ? null : xs.string(collation)); 
    }
    @Override
        public XsAnyAtomicTypeSeqExpr distinctValues(XsAnyAtomicTypeSeqExpr arg, XsStringExpr collation) {
        return new XsExprImpl.XsAnyAtomicTypeSeqCallImpl("fn", "distinct-values", new Object[]{ arg, collation });
    }
    @Override
        public XsAnyURIExpr documentUri(NodeExpr arg) {
        return new XsExprImpl.XsAnyURICallImpl("fn", "document-uri", new Object[]{ arg });
    }
    @Override
        public XsBooleanExpr empty(ItemSeqExpr arg) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "empty", new Object[]{ arg });
    }
    @Override
        public XsStringExpr encodeForUri(String uriPart) {
        return encodeForUri(xs.string(uriPart)); 
    }
    @Override
        public XsStringExpr encodeForUri(XsStringExpr uriPart) {
        return new XsExprImpl.XsStringCallImpl("fn", "encode-for-uri", new Object[]{ uriPart });
    }
    @Override
        public XsBooleanExpr endsWith(String parameter1, String parameter2) {
        return endsWith(xs.string(parameter1), xs.string(parameter2)); 
    }
    @Override
        public XsBooleanExpr endsWith(XsStringExpr parameter1, XsStringExpr parameter2) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "ends-with", new Object[]{ parameter1, parameter2 });
    }
    @Override
        public XsBooleanExpr endsWith(String parameter1, String parameter2, String collation) {
        return endsWith(xs.string(parameter1), xs.string(parameter2), (collation == null) ? null : xs.string(collation)); 
    }
    @Override
        public XsBooleanExpr endsWith(XsStringExpr parameter1, XsStringExpr parameter2, XsStringExpr collation) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "ends-with", new Object[]{ parameter1, parameter2, collation });
    }
    @Override
        public XsStringExpr escapeHtmlUri(String uriPart) {
        return escapeHtmlUri(xs.string(uriPart)); 
    }
    @Override
        public XsStringExpr escapeHtmlUri(XsStringExpr uriPart) {
        return new XsExprImpl.XsStringCallImpl("fn", "escape-html-uri", new Object[]{ uriPart });
    }
    @Override
        public XsBooleanExpr exists(ItemSeqExpr arg) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "exists", new Object[]{ arg });
    }
    @Override
    public XsBooleanExpr falseExpr() {
        return new XsExprImpl.XsBooleanCallImpl("fn", "false", null);
    }
    @Override
        public XsNumericExpr floor(XsNumericExpr arg) {
        return new XsExprImpl.XsNumericCallImpl("fn", "floor", new Object[]{ arg });
    }
    @Override
        public XsStringExpr formatDate(XsDateExpr value, String picture) {
        return formatDate(value, xs.string(picture)); 
    }
    @Override
        public XsStringExpr formatDate(XsDateExpr value, XsStringExpr picture) {
        return new XsExprImpl.XsStringCallImpl("fn", "format-date", new Object[]{ value, picture });
    }
    @Override
        public XsStringExpr formatDateTime(XsDateTimeExpr value, String picture) {
        return formatDateTime(value, xs.string(picture)); 
    }
    @Override
        public XsStringExpr formatDateTime(XsDateTimeExpr value, XsStringExpr picture) {
        return new XsExprImpl.XsStringCallImpl("fn", "format-dateTime", new Object[]{ value, picture });
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture) {
        return formatNumber(value, xs.string(picture)); 
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture) {
        return new XsExprImpl.XsStringCallImpl("fn", "format-number", new Object[]{ value, picture });
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String decimalFormatName) {
        return formatNumber(value, xs.string(picture), (decimalFormatName == null) ? null : xs.string(decimalFormatName)); 
    }
    @Override
        public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr decimalFormatName) {
        return new XsExprImpl.XsStringCallImpl("fn", "format-number", new Object[]{ value, picture, decimalFormatName });
    }
    @Override
        public XsStringExpr formatTime(XsTimeExpr value, String picture) {
        return formatTime(value, xs.string(picture)); 
    }
    @Override
        public XsStringExpr formatTime(XsTimeExpr value, XsStringExpr picture) {
        return new XsExprImpl.XsStringCallImpl("fn", "format-time", new Object[]{ value, picture });
    }
    @Override
        public XsStringExpr generateId(NodeExpr node) {
        return new XsExprImpl.XsStringCallImpl("fn", "generate-id", new Object[]{ node });
    }
    @Override
        public ItemExpr head(ItemSeqExpr arg1) {
        return new BaseTypeImpl.ItemCallImpl("fn", "head", new Object[]{ arg1 });
    }
    @Override
        public XsIntegerExpr hoursFromDateTime(XsDateTimeExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "hours-from-dateTime", new Object[]{ arg });
    }
    @Override
        public XsIntegerExpr hoursFromDuration(XsDurationExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "hours-from-duration", new Object[]{ arg });
    }
    @Override
        public XsIntegerExpr hoursFromTime(XsTimeExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "hours-from-time", new Object[]{ arg });
    }
    @Override
    public XsDayTimeDurationExpr implicitTimezone() {
        return new XsExprImpl.XsDayTimeDurationCallImpl("fn", "implicit-timezone", null);
    }
    @Override
        public XsIntegerSeqExpr indexOf(XsAnyAtomicTypeSeqExpr seqParam, XsAnyAtomicTypeExpr srchParam) {
        return new XsExprImpl.XsIntegerSeqCallImpl("fn", "index-of", new Object[]{ seqParam, srchParam });
    }
    @Override
        public XsIntegerSeqExpr indexOf(XsAnyAtomicTypeSeqExpr seqParam, XsAnyAtomicTypeExpr srchParam, String collationLiteral) {
        return indexOf(seqParam, srchParam, (collationLiteral == null) ? null : xs.string(collationLiteral)); 
    }
    @Override
        public XsIntegerSeqExpr indexOf(XsAnyAtomicTypeSeqExpr seqParam, XsAnyAtomicTypeExpr srchParam, XsStringExpr collationLiteral) {
        return new XsExprImpl.XsIntegerSeqCallImpl("fn", "index-of", new Object[]{ seqParam, srchParam, collationLiteral });
    }
    @Override
        public XsStringSeqExpr inScopePrefixes(ElementNodeExpr element) {
        return new XsExprImpl.XsStringSeqCallImpl("fn", "in-scope-prefixes", new Object[]{ element });
    }
    @Override
        public ItemSeqExpr insertBefore(ItemSeqExpr target, XsIntegerExpr position, ItemSeqExpr inserts) {
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "insert-before", new Object[]{ target, position, inserts });
    }
    @Override
        public XsStringExpr iriToUri(String uriPart) {
        return iriToUri(xs.string(uriPart)); 
    }
    @Override
        public XsStringExpr iriToUri(XsStringExpr uriPart) {
        return new XsExprImpl.XsStringCallImpl("fn", "iri-to-uri", new Object[]{ uriPart });
    }
    @Override
        public XsBooleanExpr lang(String testlang, NodeExpr node) {
        return lang(xs.string(testlang), node); 
    }
    @Override
        public XsBooleanExpr lang(XsStringExpr testlang, NodeExpr node) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "lang", new Object[]{ testlang, node });
    }
    @Override
        public XsStringExpr localName(NodeExpr arg) {
        return new XsExprImpl.XsStringCallImpl("fn", "local-name", new Object[]{ arg });
    }
    @Override
        public XsNCNameExpr localNameFromQName(XsQNameExpr arg) {
        return new XsExprImpl.XsNCNameCallImpl("fn", "local-name-from-QName", new Object[]{ arg });
    }
    @Override
        public XsStringExpr lowerCase(String string) {
        return lowerCase(xs.string(string)); 
    }
    @Override
        public XsStringExpr lowerCase(XsStringExpr string) {
        return new XsExprImpl.XsStringCallImpl("fn", "lower-case", new Object[]{ string });
    }
    @Override
        public XsBooleanExpr matches(String input, String pattern) {
        return matches(xs.string(input), xs.string(pattern)); 
    }
    @Override
        public XsBooleanExpr matches(XsStringExpr input, XsStringExpr pattern) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "matches", new Object[]{ input, pattern });
    }
    @Override
        public XsBooleanExpr matches(String input, String pattern, String flags) {
        return matches(xs.string(input), xs.string(pattern), (flags == null) ? null : xs.string(flags)); 
    }
    @Override
        public XsBooleanExpr matches(XsStringExpr input, XsStringExpr pattern, XsStringExpr flags) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "matches", new Object[]{ input, pattern, flags });
    }
    @Override
        public XsAnyAtomicTypeExpr max(XsAnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.XsAnyAtomicTypeCallImpl("fn", "max", new Object[]{ arg });
    }
    @Override
        public XsAnyAtomicTypeExpr max(XsAnyAtomicTypeSeqExpr arg, String collation) {
        return max(arg, (collation == null) ? null : xs.string(collation)); 
    }
    @Override
        public XsAnyAtomicTypeExpr max(XsAnyAtomicTypeSeqExpr arg, XsStringExpr collation) {
        return new XsExprImpl.XsAnyAtomicTypeCallImpl("fn", "max", new Object[]{ arg, collation });
    }
    @Override
        public XsAnyAtomicTypeExpr min(XsAnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.XsAnyAtomicTypeCallImpl("fn", "min", new Object[]{ arg });
    }
    @Override
        public XsAnyAtomicTypeExpr min(XsAnyAtomicTypeSeqExpr arg, String collation) {
        return min(arg, (collation == null) ? null : xs.string(collation)); 
    }
    @Override
        public XsAnyAtomicTypeExpr min(XsAnyAtomicTypeSeqExpr arg, XsStringExpr collation) {
        return new XsExprImpl.XsAnyAtomicTypeCallImpl("fn", "min", new Object[]{ arg, collation });
    }
    @Override
        public XsIntegerExpr minutesFromDateTime(XsDateTimeExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "minutes-from-dateTime", new Object[]{ arg });
    }
    @Override
        public XsIntegerExpr minutesFromDuration(XsDurationExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "minutes-from-duration", new Object[]{ arg });
    }
    @Override
        public XsIntegerExpr minutesFromTime(XsTimeExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "minutes-from-time", new Object[]{ arg });
    }
    @Override
        public XsIntegerExpr monthFromDate(XsDateExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "month-from-date", new Object[]{ arg });
    }
    @Override
        public XsIntegerExpr monthFromDateTime(XsDateTimeExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "month-from-dateTime", new Object[]{ arg });
    }
    @Override
        public XsIntegerExpr monthsFromDuration(XsDurationExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "months-from-duration", new Object[]{ arg });
    }
    @Override
        public XsStringExpr name(NodeExpr arg) {
        return new XsExprImpl.XsStringCallImpl("fn", "name", new Object[]{ arg });
    }
    @Override
        public XsAnyURIExpr namespaceUri(NodeExpr arg) {
        return new XsExprImpl.XsAnyURICallImpl("fn", "namespace-uri", new Object[]{ arg });
    }
    @Override
        public XsAnyURIExpr namespaceUriForPrefix(String prefix, ElementNodeExpr element) {
        return namespaceUriForPrefix(xs.string(prefix), element); 
    }
    @Override
        public XsAnyURIExpr namespaceUriForPrefix(XsStringExpr prefix, ElementNodeExpr element) {
        return new XsExprImpl.XsAnyURICallImpl("fn", "namespace-uri-for-prefix", new Object[]{ prefix, element });
    }
    @Override
        public XsAnyURIExpr namespaceUriFromQName(XsQNameExpr arg) {
        return new XsExprImpl.XsAnyURICallImpl("fn", "namespace-uri-from-QName", new Object[]{ arg });
    }
    @Override
        public XsBooleanExpr nilled() {
        return new XsExprImpl.XsBooleanCallImpl("fn", "nilled", new Object[]{  });
    }
    @Override
        public XsBooleanExpr nilled(NodeExpr arg) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "nilled", new Object[]{ arg });
    }
    @Override
        public XsQNameExpr nodeName() {
        return new XsExprImpl.XsQNameCallImpl("fn", "node-name", new Object[]{  });
    }
    @Override
        public XsQNameExpr nodeName(NodeExpr arg) {
        return new XsExprImpl.XsQNameCallImpl("fn", "node-name", new Object[]{ arg });
    }
    @Override
        public XsStringExpr normalizeSpace(String input) {
        return normalizeSpace(xs.string(input)); 
    }
    @Override
        public XsStringExpr normalizeSpace(XsStringExpr input) {
        return new XsExprImpl.XsStringCallImpl("fn", "normalize-space", new Object[]{ input });
    }
    @Override
        public XsStringExpr normalizeUnicode(String arg) {
        return normalizeUnicode(xs.string(arg)); 
    }
    @Override
        public XsStringExpr normalizeUnicode(XsStringExpr arg) {
        return new XsExprImpl.XsStringCallImpl("fn", "normalize-unicode", new Object[]{ arg });
    }
    @Override
        public XsStringExpr normalizeUnicode(String arg, String normalizationForm) {
        return normalizeUnicode(xs.string(arg), (normalizationForm == null) ? null : xs.string(normalizationForm)); 
    }
    @Override
        public XsStringExpr normalizeUnicode(XsStringExpr arg, XsStringExpr normalizationForm) {
        return new XsExprImpl.XsStringCallImpl("fn", "normalize-unicode", new Object[]{ arg, normalizationForm });
    }
    @Override
        public XsBooleanExpr not(ItemSeqExpr arg) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "not", new Object[]{ arg });
    }
    @Override
        public XsDoubleExpr number(XsAnyAtomicTypeExpr arg) {
        return new XsExprImpl.XsDoubleCallImpl("fn", "number", new Object[]{ arg });
    }
    @Override
        public XsNCNameExpr prefixFromQName(XsQNameExpr arg) {
        return new XsExprImpl.XsNCNameCallImpl("fn", "prefix-from-QName", new Object[]{ arg });
    }
    @Override
        public XsQNameExpr QName(String paramURI, String paramQName) {
        return QName(xs.string(paramURI), xs.string(paramQName)); 
    }
    @Override
        public XsQNameExpr QName(XsStringExpr paramURI, XsStringExpr paramQName) {
        return new XsExprImpl.XsQNameCallImpl("fn", "QName", new Object[]{ paramURI, paramQName });
    }
    @Override
        public ItemSeqExpr remove(ItemSeqExpr target, XsIntegerExpr position) {
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "remove", new Object[]{ target, position });
    }
    @Override
        public XsStringExpr replace(String input, String pattern, String replacement) {
        return replace(xs.string(input), xs.string(pattern), xs.string(replacement)); 
    }
    @Override
        public XsStringExpr replace(XsStringExpr input, XsStringExpr pattern, XsStringExpr replacement) {
        return new XsExprImpl.XsStringCallImpl("fn", "replace", new Object[]{ input, pattern, replacement });
    }
    @Override
        public XsStringExpr replace(String input, String pattern, String replacement, String flags) {
        return replace(xs.string(input), xs.string(pattern), xs.string(replacement), (flags == null) ? null : xs.string(flags)); 
    }
    @Override
        public XsStringExpr replace(XsStringExpr input, XsStringExpr pattern, XsStringExpr replacement, XsStringExpr flags) {
        return new XsExprImpl.XsStringCallImpl("fn", "replace", new Object[]{ input, pattern, replacement, flags });
    }
    @Override
        public XsQNameExpr resolveQName(String qname, ElementNodeExpr element) {
        return resolveQName(xs.string(qname), element); 
    }
    @Override
        public XsQNameExpr resolveQName(XsStringExpr qname, ElementNodeExpr element) {
        return new XsExprImpl.XsQNameCallImpl("fn", "resolve-QName", new Object[]{ qname, element });
    }
    @Override
        public XsAnyURIExpr resolveUri(String relative, String base) {
        return resolveUri(xs.string(relative), xs.string(base)); 
    }
    @Override
        public XsAnyURIExpr resolveUri(XsStringExpr relative, XsStringExpr base) {
        return new XsExprImpl.XsAnyURICallImpl("fn", "resolve-uri", new Object[]{ relative, base });
    }
    @Override
        public ItemSeqExpr reverse(ItemSeqExpr target) {
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "reverse", new Object[]{ target });
    }
    @Override
        public NodeExpr root(NodeExpr arg) {
        return new BaseTypeImpl.NodeCallImpl("fn", "root", new Object[]{ arg });
    }
    @Override
        public XsNumericExpr round(XsNumericExpr arg) {
        return new XsExprImpl.XsNumericCallImpl("fn", "round", new Object[]{ arg });
    }
    @Override
        public XsNumericExpr roundHalfToEven(XsNumericExpr arg) {
        return new XsExprImpl.XsNumericCallImpl("fn", "round-half-to-even", new Object[]{ arg });
    }
    @Override
        public XsNumericExpr roundHalfToEven(XsNumericExpr arg, XsIntegerExpr precision) {
        return new XsExprImpl.XsNumericCallImpl("fn", "round-half-to-even", new Object[]{ arg, precision });
    }
    @Override
        public XsDecimalExpr secondsFromDateTime(XsDateTimeExpr arg) {
        return new XsExprImpl.XsDecimalCallImpl("fn", "seconds-from-dateTime", new Object[]{ arg });
    }
    @Override
        public XsDecimalExpr secondsFromDuration(XsDurationExpr arg) {
        return new XsExprImpl.XsDecimalCallImpl("fn", "seconds-from-duration", new Object[]{ arg });
    }
    @Override
        public XsDecimalExpr secondsFromTime(XsTimeExpr arg) {
        return new XsExprImpl.XsDecimalCallImpl("fn", "seconds-from-time", new Object[]{ arg });
    }
    @Override
        public XsBooleanExpr startsWith(String parameter1, String parameter2) {
        return startsWith(xs.string(parameter1), xs.string(parameter2)); 
    }
    @Override
        public XsBooleanExpr startsWith(XsStringExpr parameter1, XsStringExpr parameter2) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "starts-with", new Object[]{ parameter1, parameter2 });
    }
    @Override
        public XsBooleanExpr startsWith(String parameter1, String parameter2, String collation) {
        return startsWith(xs.string(parameter1), xs.string(parameter2), (collation == null) ? null : xs.string(collation)); 
    }
    @Override
        public XsBooleanExpr startsWith(XsStringExpr parameter1, XsStringExpr parameter2, XsStringExpr collation) {
        return new XsExprImpl.XsBooleanCallImpl("fn", "starts-with", new Object[]{ parameter1, parameter2, collation });
    }
    @Override
        public XsStringExpr string(ItemExpr arg) {
        return new XsExprImpl.XsStringCallImpl("fn", "string", new Object[]{ arg });
    }
    @Override
        public XsStringExpr stringJoin(String... parameter1) {
        return stringJoin(xs.strings(parameter1)); 
    }
    @Override
        public XsStringExpr stringJoin(XsStringSeqExpr parameter1) {
        return new XsExprImpl.XsStringCallImpl("fn", "string-join", new Object[]{ parameter1 });
    }
    @Override
        public XsStringExpr stringJoin(XsStringSeqExpr parameter1, XsStringExpr parameter2) {
        return new XsExprImpl.XsStringCallImpl("fn", "string-join", new Object[]{ parameter1, parameter2 });
    }
    @Override
        public XsIntegerExpr stringLength(String sourceString) {
        return stringLength(xs.string(sourceString)); 
    }
    @Override
        public XsIntegerExpr stringLength(XsStringExpr sourceString) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "string-length", new Object[]{ sourceString });
    }
    @Override
        public XsIntegerSeqExpr stringToCodepoints(String arg) {
        return stringToCodepoints(xs.string(arg)); 
    }
    @Override
        public XsIntegerSeqExpr stringToCodepoints(XsStringExpr arg) {
        return new XsExprImpl.XsIntegerSeqCallImpl("fn", "string-to-codepoints", new Object[]{ arg });
    }
    @Override
        public ItemSeqExpr subsequence(ItemSeqExpr sourceSeq, XsNumericExpr startingLoc) {
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "subsequence", new Object[]{ sourceSeq, startingLoc });
    }
    @Override
        public ItemSeqExpr subsequence(ItemSeqExpr sourceSeq, XsNumericExpr startingLoc, XsNumericExpr length) {
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "subsequence", new Object[]{ sourceSeq, startingLoc, length });
    }
    @Override
        public XsStringExpr substring(String sourceString, XsNumericExpr startingLoc) {
        return substring(xs.string(sourceString), startingLoc); 
    }
    @Override
        public XsStringExpr substring(XsStringExpr sourceString, XsNumericExpr startingLoc) {
        return new XsExprImpl.XsStringCallImpl("fn", "substring", new Object[]{ sourceString, startingLoc });
    }
    @Override
        public XsStringExpr substring(String sourceString, XsNumericExpr startingLoc, XsNumericExpr length) {
        return substring(xs.string(sourceString), startingLoc, length); 
    }
    @Override
        public XsStringExpr substring(XsStringExpr sourceString, XsNumericExpr startingLoc, XsNumericExpr length) {
        return new XsExprImpl.XsStringCallImpl("fn", "substring", new Object[]{ sourceString, startingLoc, length });
    }
    @Override
        public XsStringExpr substringAfter(String input, String after) {
        return substringAfter(xs.string(input), xs.string(after)); 
    }
    @Override
        public XsStringExpr substringAfter(XsStringExpr input, XsStringExpr after) {
        return new XsExprImpl.XsStringCallImpl("fn", "substring-after", new Object[]{ input, after });
    }
    @Override
        public XsStringExpr substringAfter(String input, String after, String collation) {
        return substringAfter(xs.string(input), xs.string(after), (collation == null) ? null : xs.string(collation)); 
    }
    @Override
        public XsStringExpr substringAfter(XsStringExpr input, XsStringExpr after, XsStringExpr collation) {
        return new XsExprImpl.XsStringCallImpl("fn", "substring-after", new Object[]{ input, after, collation });
    }
    @Override
        public XsStringExpr substringBefore(String input, String before) {
        return substringBefore(xs.string(input), xs.string(before)); 
    }
    @Override
        public XsStringExpr substringBefore(XsStringExpr input, XsStringExpr before) {
        return new XsExprImpl.XsStringCallImpl("fn", "substring-before", new Object[]{ input, before });
    }
    @Override
        public XsStringExpr substringBefore(String input, String before, String collation) {
        return substringBefore(xs.string(input), xs.string(before), (collation == null) ? null : xs.string(collation)); 
    }
    @Override
        public XsStringExpr substringBefore(XsStringExpr input, XsStringExpr before, XsStringExpr collation) {
        return new XsExprImpl.XsStringCallImpl("fn", "substring-before", new Object[]{ input, before, collation });
    }
    @Override
        public XsAnyAtomicTypeExpr sum(XsAnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.XsAnyAtomicTypeCallImpl("fn", "sum", new Object[]{ arg });
    }
    @Override
        public XsAnyAtomicTypeExpr sum(XsAnyAtomicTypeSeqExpr arg, XsAnyAtomicTypeExpr zero) {
        return new XsExprImpl.XsAnyAtomicTypeCallImpl("fn", "sum", new Object[]{ arg, zero });
    }
    @Override
        public ItemSeqExpr tail(ItemSeqExpr seq) {
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "tail", new Object[]{ seq });
    }
    @Override
        public XsDayTimeDurationExpr timezoneFromDate(XsDateExpr arg) {
        return new XsExprImpl.XsDayTimeDurationCallImpl("fn", "timezone-from-date", new Object[]{ arg });
    }
    @Override
        public XsDayTimeDurationExpr timezoneFromDateTime(XsDateTimeExpr arg) {
        return new XsExprImpl.XsDayTimeDurationCallImpl("fn", "timezone-from-dateTime", new Object[]{ arg });
    }
    @Override
        public XsDayTimeDurationExpr timezoneFromTime(XsTimeExpr arg) {
        return new XsExprImpl.XsDayTimeDurationCallImpl("fn", "timezone-from-time", new Object[]{ arg });
    }
    @Override
        public XsStringSeqExpr tokenize(String input, String pattern) {
        return tokenize(xs.string(input), xs.string(pattern)); 
    }
    @Override
        public XsStringSeqExpr tokenize(XsStringExpr input, XsStringExpr pattern) {
        return new XsExprImpl.XsStringSeqCallImpl("fn", "tokenize", new Object[]{ input, pattern });
    }
    @Override
        public XsStringSeqExpr tokenize(String input, String pattern, String flags) {
        return tokenize(xs.string(input), xs.string(pattern), (flags == null) ? null : xs.string(flags)); 
    }
    @Override
        public XsStringSeqExpr tokenize(XsStringExpr input, XsStringExpr pattern, XsStringExpr flags) {
        return new XsExprImpl.XsStringSeqCallImpl("fn", "tokenize", new Object[]{ input, pattern, flags });
    }
    @Override
        public XsStringExpr translate(String src, String mapString, String transString) {
        return translate(xs.string(src), xs.string(mapString), xs.string(transString)); 
    }
    @Override
        public XsStringExpr translate(XsStringExpr src, XsStringExpr mapString, XsStringExpr transString) {
        return new XsExprImpl.XsStringCallImpl("fn", "translate", new Object[]{ src, mapString, transString });
    }
    @Override
    public XsBooleanExpr trueExpr() {
        return new XsExprImpl.XsBooleanCallImpl("fn", "true", null);
    }
    @Override
        public ItemSeqExpr unordered(ItemSeqExpr sourceSeq) {
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "unordered", new Object[]{ sourceSeq });
    }
    @Override
        public XsStringExpr upperCase(String string) {
        return upperCase(xs.string(string)); 
    }
    @Override
        public XsStringExpr upperCase(XsStringExpr string) {
        return new XsExprImpl.XsStringCallImpl("fn", "upper-case", new Object[]{ string });
    }
    @Override
        public XsIntegerExpr yearFromDate(XsDateExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "year-from-date", new Object[]{ arg });
    }
    @Override
        public XsIntegerExpr yearFromDateTime(XsDateTimeExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "year-from-dateTime", new Object[]{ arg });
    }
    @Override
        public XsIntegerExpr yearsFromDuration(XsDurationExpr arg) {
        return new XsExprImpl.XsIntegerCallImpl("fn", "years-from-duration", new Object[]{ arg });
    }
}
