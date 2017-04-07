/*
 * Copyright 2016-2017 MarkLogic Corporation
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

import com.marklogic.client.type.ElementNodeExpr;
import com.marklogic.client.type.ItemExpr;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.NodeExpr;
import com.marklogic.client.type.XsAnyAtomicTypeExpr;
import com.marklogic.client.type.XsAnyAtomicTypeSeqExpr;
import com.marklogic.client.type.XsAnyURIExpr;
import com.marklogic.client.type.XsBooleanExpr;
import com.marklogic.client.type.XsDateExpr;
import com.marklogic.client.type.XsDateTimeExpr;
import com.marklogic.client.type.XsDayTimeDurationExpr;
import com.marklogic.client.type.XsDecimalExpr;
import com.marklogic.client.type.XsDoubleExpr;
import com.marklogic.client.type.XsDurationExpr;
import com.marklogic.client.type.XsIntegerExpr;
import com.marklogic.client.type.XsIntegerSeqExpr;
import com.marklogic.client.type.XsNCNameExpr;
import com.marklogic.client.type.XsNumericExpr;
import com.marklogic.client.type.XsNumericSeqExpr;
import com.marklogic.client.type.XsQNameExpr;
import com.marklogic.client.type.XsStringExpr;
import com.marklogic.client.type.XsStringSeqExpr;
import com.marklogic.client.type.XsTimeExpr;



import com.marklogic.client.expression.FnExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class FnExprImpl implements FnExpr {

    final static XsExprImpl xs = XsExprImpl.xs;

    final static FnExprImpl fn = new FnExprImpl();

    FnExprImpl() {
    }

    
    @Override
    public XsNumericExpr abs(XsNumericExpr arg) {
        return new XsExprImpl.NumericCallImpl("fn", "abs", new Object[]{ arg });
    }

    
    @Override
    public XsDateExpr adjustDateToTimezone(XsDateExpr arg) {
        return new XsExprImpl.DateCallImpl("fn", "adjust-date-to-timezone", new Object[]{ arg });
    }

    
    @Override
    public XsDateExpr adjustDateToTimezone(XsDateExpr arg, String timezone) {
        return adjustDateToTimezone(arg, (timezone == null) ? (XsDayTimeDurationExpr) null : xs.dayTimeDuration(timezone));
    }

    
    @Override
    public XsDateExpr adjustDateToTimezone(XsDateExpr arg, XsDayTimeDurationExpr timezone) {
        return new XsExprImpl.DateCallImpl("fn", "adjust-date-to-timezone", new Object[]{ arg, timezone });
    }

    
    @Override
    public XsDateTimeExpr adjustDateTimeToTimezone(XsDateTimeExpr arg) {
        return new XsExprImpl.DateTimeCallImpl("fn", "adjust-dateTime-to-timezone", new Object[]{ arg });
    }

    
    @Override
    public XsDateTimeExpr adjustDateTimeToTimezone(XsDateTimeExpr arg, String timezone) {
        return adjustDateTimeToTimezone(arg, (timezone == null) ? (XsDayTimeDurationExpr) null : xs.dayTimeDuration(timezone));
    }

    
    @Override
    public XsDateTimeExpr adjustDateTimeToTimezone(XsDateTimeExpr arg, XsDayTimeDurationExpr timezone) {
        return new XsExprImpl.DateTimeCallImpl("fn", "adjust-dateTime-to-timezone", new Object[]{ arg, timezone });
    }

    
    @Override
    public XsTimeExpr adjustTimeToTimezone(XsTimeExpr arg) {
        return new XsExprImpl.TimeCallImpl("fn", "adjust-time-to-timezone", new Object[]{ arg });
    }

    
    @Override
    public XsTimeExpr adjustTimeToTimezone(XsTimeExpr arg, String timezone) {
        return adjustTimeToTimezone(arg, (timezone == null) ? (XsDayTimeDurationExpr) null : xs.dayTimeDuration(timezone));
    }

    
    @Override
    public XsTimeExpr adjustTimeToTimezone(XsTimeExpr arg, XsDayTimeDurationExpr timezone) {
        return new XsExprImpl.TimeCallImpl("fn", "adjust-time-to-timezone", new Object[]{ arg, timezone });
    }

    
    @Override
    public ElementNodeExpr analyzeString(String in, String regex) {
        return analyzeString((in == null) ? (XsStringExpr) null : xs.string(in), (regex == null) ? (XsStringExpr) null : xs.string(regex));
    }

    
    @Override
    public ElementNodeExpr analyzeString(XsStringExpr in, XsStringExpr regex) {
        if (regex == null) {
            throw new IllegalArgumentException("regex parameter for analyzeString() cannot be null");
        }
        return new BaseTypeImpl.ElementNodeCallImpl("fn", "analyze-string", new Object[]{ in, regex });
    }

    
    @Override
    public ElementNodeExpr analyzeString(String in, String regex, String flags) {
        return analyzeString((in == null) ? (XsStringExpr) null : xs.string(in), (regex == null) ? (XsStringExpr) null : xs.string(regex), (flags == null) ? (XsStringExpr) null : xs.string(flags));
    }

    
    @Override
    public ElementNodeExpr analyzeString(XsStringExpr in, XsStringExpr regex, XsStringExpr flags) {
        if (regex == null) {
            throw new IllegalArgumentException("regex parameter for analyzeString() cannot be null");
        }
        if (flags == null) {
            throw new IllegalArgumentException("flags parameter for analyzeString() cannot be null");
        }
        return new BaseTypeImpl.ElementNodeCallImpl("fn", "analyze-string", new Object[]{ in, regex, flags });
    }

    
    @Override
    public XsAnyAtomicTypeExpr avg(XsAnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "avg", new Object[]{ arg });
    }

    
    @Override
    public XsAnyURIExpr baseUri(NodeExpr arg) {
        return new XsExprImpl.AnyURICallImpl("fn", "base-uri", new Object[]{ arg });
    }

    
    @Override
    public XsBooleanExpr booleanExpr(ItemSeqExpr arg) {
        return new XsExprImpl.BooleanCallImpl("fn", "boolean", new Object[]{ arg });
    }

    
    @Override
    public XsNumericExpr ceiling(XsNumericExpr arg) {
        return new XsExprImpl.NumericCallImpl("fn", "ceiling", new Object[]{ arg });
    }

    
    @Override
    public XsBooleanExpr codepointEqual(XsStringExpr comparand1, String comparand2) {
        return codepointEqual(comparand1, (comparand2 == null) ? (XsStringExpr) null : xs.string(comparand2));
    }

    
    @Override
    public XsBooleanExpr codepointEqual(XsStringExpr comparand1, XsStringExpr comparand2) {
        return new XsExprImpl.BooleanCallImpl("fn", "codepoint-equal", new Object[]{ comparand1, comparand2 });
    }

    
    @Override
    public XsStringExpr codepointsToString(XsIntegerSeqExpr arg) {
        return new XsExprImpl.StringCallImpl("fn", "codepoints-to-string", new Object[]{ arg });
    }

    
    @Override
    public XsIntegerExpr compare(XsStringExpr comparand1, String comparand2) {
        return compare(comparand1, (comparand2 == null) ? (XsStringExpr) null : xs.string(comparand2));
    }

    
    @Override
    public XsIntegerExpr compare(XsStringExpr comparand1, XsStringExpr comparand2) {
        return new XsExprImpl.IntegerCallImpl("fn", "compare", new Object[]{ comparand1, comparand2 });
    }

    
    @Override
    public XsIntegerExpr compare(XsStringExpr comparand1, String comparand2, String collation) {
        return compare(comparand1, (comparand2 == null) ? (XsStringExpr) null : xs.string(comparand2), (collation == null) ? (XsStringExpr) null : xs.string(collation));
    }

    
    @Override
    public XsIntegerExpr compare(XsStringExpr comparand1, XsStringExpr comparand2, XsStringExpr collation) {
        if (collation == null) {
            throw new IllegalArgumentException("collation parameter for compare() cannot be null");
        }
        return new XsExprImpl.IntegerCallImpl("fn", "compare", new Object[]{ comparand1, comparand2, collation });
    }

    
    @Override
    public XsStringExpr concat(XsAnyAtomicTypeExpr... parameter1) {
        return new XsExprImpl.StringCallImpl("fn", "concat", parameter1);
    }

    
    @Override
    public XsBooleanExpr contains(XsStringExpr parameter1, String parameter2) {
        return contains(parameter1, (parameter2 == null) ? (XsStringExpr) null : xs.string(parameter2));
    }

    
    @Override
    public XsBooleanExpr contains(XsStringExpr parameter1, XsStringExpr parameter2) {
        return new XsExprImpl.BooleanCallImpl("fn", "contains", new Object[]{ parameter1, parameter2 });
    }

    
    @Override
    public XsBooleanExpr contains(XsStringExpr parameter1, String parameter2, String collation) {
        return contains(parameter1, (parameter2 == null) ? (XsStringExpr) null : xs.string(parameter2), (collation == null) ? (XsStringExpr) null : xs.string(collation));
    }

    
    @Override
    public XsBooleanExpr contains(XsStringExpr parameter1, XsStringExpr parameter2, XsStringExpr collation) {
        if (collation == null) {
            throw new IllegalArgumentException("collation parameter for contains() cannot be null");
        }
        return new XsExprImpl.BooleanCallImpl("fn", "contains", new Object[]{ parameter1, parameter2, collation });
    }

    
    @Override
    public XsIntegerExpr count(ItemSeqExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "count", new Object[]{ arg });
    }

    
    @Override
    public XsIntegerExpr count(ItemSeqExpr arg, double maximum) {
        return count(arg, xs.doubleVal(maximum));
    }

    
    @Override
    public XsIntegerExpr count(ItemSeqExpr arg, XsDoubleExpr maximum) {
        return new XsExprImpl.IntegerCallImpl("fn", "count", new Object[]{ arg, maximum });
    }

    
    @Override
    public XsDateExpr currentDate() {
        return new XsExprImpl.DateCallImpl("fn", "current-date", new Object[]{  });
    }

    
    @Override
    public XsDateTimeExpr currentDateTime() {
        return new XsExprImpl.DateTimeCallImpl("fn", "current-dateTime", new Object[]{  });
    }

    
    @Override
    public XsTimeExpr currentTime() {
        return new XsExprImpl.TimeCallImpl("fn", "current-time", new Object[]{  });
    }

    
    @Override
    public XsIntegerExpr dayFromDate(XsDateExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "day-from-date", new Object[]{ arg });
    }

    
    @Override
    public XsIntegerExpr dayFromDateTime(XsDateTimeExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "day-from-dateTime", new Object[]{ arg });
    }

    
    @Override
    public XsIntegerExpr daysFromDuration(XsDurationExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "days-from-duration", new Object[]{ arg });
    }

    
    @Override
    public XsBooleanExpr deepEqual(ItemSeqExpr parameter1, ItemSeqExpr parameter2) {
        return new XsExprImpl.BooleanCallImpl("fn", "deep-equal", new Object[]{ parameter1, parameter2 });
    }

    
    @Override
    public XsBooleanExpr deepEqual(ItemSeqExpr parameter1, ItemSeqExpr parameter2, String collation) {
        return deepEqual(parameter1, parameter2, (collation == null) ? (XsStringExpr) null : xs.string(collation));
    }

    
    @Override
    public XsBooleanExpr deepEqual(ItemSeqExpr parameter1, ItemSeqExpr parameter2, XsStringExpr collation) {
        if (collation == null) {
            throw new IllegalArgumentException("collation parameter for deepEqual() cannot be null");
        }
        return new XsExprImpl.BooleanCallImpl("fn", "deep-equal", new Object[]{ parameter1, parameter2, collation });
    }

    
    @Override
    public XsStringExpr defaultCollation() {
        return new XsExprImpl.StringCallImpl("fn", "default-collation", new Object[]{  });
    }

    
    @Override
    public XsAnyAtomicTypeSeqExpr distinctValues(XsAnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.AnyAtomicTypeSeqCallImpl("fn", "distinct-values", new Object[]{ arg });
    }

    
    @Override
    public XsAnyAtomicTypeSeqExpr distinctValues(XsAnyAtomicTypeSeqExpr arg, String collation) {
        return distinctValues(arg, (collation == null) ? (XsStringExpr) null : xs.string(collation));
    }

    
    @Override
    public XsAnyAtomicTypeSeqExpr distinctValues(XsAnyAtomicTypeSeqExpr arg, XsStringExpr collation) {
        if (collation == null) {
            throw new IllegalArgumentException("collation parameter for distinctValues() cannot be null");
        }
        return new XsExprImpl.AnyAtomicTypeSeqCallImpl("fn", "distinct-values", new Object[]{ arg, collation });
    }

    
    @Override
    public XsAnyURIExpr documentUri(NodeExpr arg) {
        return new XsExprImpl.AnyURICallImpl("fn", "document-uri", new Object[]{ arg });
    }

    
    @Override
    public XsBooleanExpr empty(ItemSeqExpr arg) {
        return new XsExprImpl.BooleanCallImpl("fn", "empty", new Object[]{ arg });
    }

    
    @Override
    public XsStringExpr encodeForUri(XsStringExpr uriPart) {
        return new XsExprImpl.StringCallImpl("fn", "encode-for-uri", new Object[]{ uriPart });
    }

    
    @Override
    public XsBooleanExpr endsWith(XsStringExpr parameter1, String parameter2) {
        return endsWith(parameter1, (parameter2 == null) ? (XsStringExpr) null : xs.string(parameter2));
    }

    
    @Override
    public XsBooleanExpr endsWith(XsStringExpr parameter1, XsStringExpr parameter2) {
        return new XsExprImpl.BooleanCallImpl("fn", "ends-with", new Object[]{ parameter1, parameter2 });
    }

    
    @Override
    public XsBooleanExpr endsWith(XsStringExpr parameter1, String parameter2, String collation) {
        return endsWith(parameter1, (parameter2 == null) ? (XsStringExpr) null : xs.string(parameter2), (collation == null) ? (XsStringExpr) null : xs.string(collation));
    }

    
    @Override
    public XsBooleanExpr endsWith(XsStringExpr parameter1, XsStringExpr parameter2, XsStringExpr collation) {
        if (collation == null) {
            throw new IllegalArgumentException("collation parameter for endsWith() cannot be null");
        }
        return new XsExprImpl.BooleanCallImpl("fn", "ends-with", new Object[]{ parameter1, parameter2, collation });
    }

    
    @Override
    public XsStringExpr escapeHtmlUri(XsStringExpr uriPart) {
        return new XsExprImpl.StringCallImpl("fn", "escape-html-uri", new Object[]{ uriPart });
    }

    
    @Override
    public XsBooleanExpr exists(ItemSeqExpr arg) {
        return new XsExprImpl.BooleanCallImpl("fn", "exists", new Object[]{ arg });
    }

    
    @Override
    public XsBooleanExpr falseExpr() {
        return new XsExprImpl.BooleanCallImpl("fn", "false", new Object[]{  });
    }

    
    @Override
    public XsNumericExpr floor(XsNumericExpr arg) {
        return new XsExprImpl.NumericCallImpl("fn", "floor", new Object[]{ arg });
    }

    
    @Override
    public XsStringExpr formatDate(XsDateExpr value, String picture) {
        return formatDate(value, (picture == null) ? (XsStringExpr) null : xs.string(picture));
    }

    
    @Override
    public XsStringExpr formatDate(XsDateExpr value, XsStringExpr picture) {
        if (picture == null) {
            throw new IllegalArgumentException("picture parameter for formatDate() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "format-date", new Object[]{ value, picture });
    }

    
    @Override
    public XsStringExpr formatDate(XsDateExpr value, String picture, String language) {
        return formatDate(value, (picture == null) ? (XsStringExpr) null : xs.string(picture), (language == null) ? (XsStringExpr) null : xs.string(language));
    }

    
    @Override
    public XsStringExpr formatDate(XsDateExpr value, XsStringExpr picture, XsStringExpr language) {
        if (picture == null) {
            throw new IllegalArgumentException("picture parameter for formatDate() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "format-date", new Object[]{ value, picture, language });
    }

    
    @Override
    public XsStringExpr formatDate(XsDateExpr value, String picture, String language, String calendar) {
        return formatDate(value, (picture == null) ? (XsStringExpr) null : xs.string(picture), (language == null) ? (XsStringExpr) null : xs.string(language), (calendar == null) ? (XsStringExpr) null : xs.string(calendar));
    }

    
    @Override
    public XsStringExpr formatDate(XsDateExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar) {
        if (picture == null) {
            throw new IllegalArgumentException("picture parameter for formatDate() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "format-date", new Object[]{ value, picture, language, calendar });
    }

    
    @Override
    public XsStringExpr formatDate(XsDateExpr value, String picture, String language, String calendar, String country) {
        return formatDate(value, (picture == null) ? (XsStringExpr) null : xs.string(picture), (language == null) ? (XsStringExpr) null : xs.string(language), (calendar == null) ? (XsStringExpr) null : xs.string(calendar), (country == null) ? (XsStringExpr) null : xs.string(country));
    }

    
    @Override
    public XsStringExpr formatDate(XsDateExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar, XsStringExpr country) {
        if (picture == null) {
            throw new IllegalArgumentException("picture parameter for formatDate() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "format-date", new Object[]{ value, picture, language, calendar, country });
    }

    
    @Override
    public XsStringExpr formatDateTime(XsDateTimeExpr value, String picture) {
        return formatDateTime(value, (picture == null) ? (XsStringExpr) null : xs.string(picture));
    }

    
    @Override
    public XsStringExpr formatDateTime(XsDateTimeExpr value, XsStringExpr picture) {
        if (picture == null) {
            throw new IllegalArgumentException("picture parameter for formatDateTime() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "format-dateTime", new Object[]{ value, picture });
    }

    
    @Override
    public XsStringExpr formatDateTime(XsDateTimeExpr value, String picture, String language) {
        return formatDateTime(value, (picture == null) ? (XsStringExpr) null : xs.string(picture), (language == null) ? (XsStringExpr) null : xs.string(language));
    }

    
    @Override
    public XsStringExpr formatDateTime(XsDateTimeExpr value, XsStringExpr picture, XsStringExpr language) {
        if (picture == null) {
            throw new IllegalArgumentException("picture parameter for formatDateTime() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "format-dateTime", new Object[]{ value, picture, language });
    }

    
    @Override
    public XsStringExpr formatDateTime(XsDateTimeExpr value, String picture, String language, String calendar) {
        return formatDateTime(value, (picture == null) ? (XsStringExpr) null : xs.string(picture), (language == null) ? (XsStringExpr) null : xs.string(language), (calendar == null) ? (XsStringExpr) null : xs.string(calendar));
    }

    
    @Override
    public XsStringExpr formatDateTime(XsDateTimeExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar) {
        if (picture == null) {
            throw new IllegalArgumentException("picture parameter for formatDateTime() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "format-dateTime", new Object[]{ value, picture, language, calendar });
    }

    
    @Override
    public XsStringExpr formatDateTime(XsDateTimeExpr value, String picture, String language, String calendar, String country) {
        return formatDateTime(value, (picture == null) ? (XsStringExpr) null : xs.string(picture), (language == null) ? (XsStringExpr) null : xs.string(language), (calendar == null) ? (XsStringExpr) null : xs.string(calendar), (country == null) ? (XsStringExpr) null : xs.string(country));
    }

    
    @Override
    public XsStringExpr formatDateTime(XsDateTimeExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar, XsStringExpr country) {
        if (picture == null) {
            throw new IllegalArgumentException("picture parameter for formatDateTime() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "format-dateTime", new Object[]{ value, picture, language, calendar, country });
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture) {
        return formatNumber(value, (picture == null) ? (XsStringExpr) null : xs.string(picture));
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture) {
        if (picture == null) {
            throw new IllegalArgumentException("picture parameter for formatNumber() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "format-number", new Object[]{ value, picture });
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, String picture, String decimalFormatName) {
        return formatNumber(value, (picture == null) ? (XsStringExpr) null : xs.string(picture), (decimalFormatName == null) ? (XsStringExpr) null : xs.string(decimalFormatName));
    }

    
    @Override
    public XsStringExpr formatNumber(XsNumericSeqExpr value, XsStringExpr picture, XsStringExpr decimalFormatName) {
        if (picture == null) {
            throw new IllegalArgumentException("picture parameter for formatNumber() cannot be null");
        }
        if (decimalFormatName == null) {
            throw new IllegalArgumentException("decimalFormatName parameter for formatNumber() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "format-number", new Object[]{ value, picture, decimalFormatName });
    }

    
    @Override
    public XsStringExpr formatTime(XsTimeExpr value, String picture) {
        return formatTime(value, (picture == null) ? (XsStringExpr) null : xs.string(picture));
    }

    
    @Override
    public XsStringExpr formatTime(XsTimeExpr value, XsStringExpr picture) {
        if (picture == null) {
            throw new IllegalArgumentException("picture parameter for formatTime() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "format-time", new Object[]{ value, picture });
    }

    
    @Override
    public XsStringExpr formatTime(XsTimeExpr value, String picture, String language) {
        return formatTime(value, (picture == null) ? (XsStringExpr) null : xs.string(picture), (language == null) ? (XsStringExpr) null : xs.string(language));
    }

    
    @Override
    public XsStringExpr formatTime(XsTimeExpr value, XsStringExpr picture, XsStringExpr language) {
        if (picture == null) {
            throw new IllegalArgumentException("picture parameter for formatTime() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "format-time", new Object[]{ value, picture, language });
    }

    
    @Override
    public XsStringExpr formatTime(XsTimeExpr value, String picture, String language, String calendar) {
        return formatTime(value, (picture == null) ? (XsStringExpr) null : xs.string(picture), (language == null) ? (XsStringExpr) null : xs.string(language), (calendar == null) ? (XsStringExpr) null : xs.string(calendar));
    }

    
    @Override
    public XsStringExpr formatTime(XsTimeExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar) {
        if (picture == null) {
            throw new IllegalArgumentException("picture parameter for formatTime() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "format-time", new Object[]{ value, picture, language, calendar });
    }

    
    @Override
    public XsStringExpr formatTime(XsTimeExpr value, String picture, String language, String calendar, String country) {
        return formatTime(value, (picture == null) ? (XsStringExpr) null : xs.string(picture), (language == null) ? (XsStringExpr) null : xs.string(language), (calendar == null) ? (XsStringExpr) null : xs.string(calendar), (country == null) ? (XsStringExpr) null : xs.string(country));
    }

    
    @Override
    public XsStringExpr formatTime(XsTimeExpr value, XsStringExpr picture, XsStringExpr language, XsStringExpr calendar, XsStringExpr country) {
        if (picture == null) {
            throw new IllegalArgumentException("picture parameter for formatTime() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "format-time", new Object[]{ value, picture, language, calendar, country });
    }

    
    @Override
    public XsStringExpr generateId(NodeExpr node) {
        return new XsExprImpl.StringCallImpl("fn", "generate-id", new Object[]{ node });
    }

    
    @Override
    public ItemExpr head(ItemSeqExpr seq) {
        return new BaseTypeImpl.ItemCallImpl("fn", "head", new Object[]{ seq });
    }

    
    @Override
    public XsIntegerExpr hoursFromDateTime(XsDateTimeExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "hours-from-dateTime", new Object[]{ arg });
    }

    
    @Override
    public XsIntegerExpr hoursFromDuration(XsDurationExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "hours-from-duration", new Object[]{ arg });
    }

    
    @Override
    public XsIntegerExpr hoursFromTime(XsTimeExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "hours-from-time", new Object[]{ arg });
    }

    
    @Override
    public XsDayTimeDurationExpr implicitTimezone() {
        return new XsExprImpl.DayTimeDurationCallImpl("fn", "implicit-timezone", new Object[]{  });
    }

    
    @Override
    public XsStringSeqExpr inScopePrefixes(ElementNodeExpr element) {
        if (element == null) {
            throw new IllegalArgumentException("element parameter for inScopePrefixes() cannot be null");
        }
        return new XsExprImpl.StringSeqCallImpl("fn", "in-scope-prefixes", new Object[]{ element });
    }

    
    @Override
    public XsIntegerSeqExpr indexOf(XsAnyAtomicTypeSeqExpr seqParam, String srchParam) {
        return indexOf(seqParam, (srchParam == null) ? (XsAnyAtomicTypeExpr) null : xs.string(srchParam));
    }

    
    @Override
    public XsIntegerSeqExpr indexOf(XsAnyAtomicTypeSeqExpr seqParam, XsAnyAtomicTypeExpr srchParam) {
        if (srchParam == null) {
            throw new IllegalArgumentException("srchParam parameter for indexOf() cannot be null");
        }
        return new XsExprImpl.IntegerSeqCallImpl("fn", "index-of", new Object[]{ seqParam, srchParam });
    }

    
    @Override
    public XsIntegerSeqExpr indexOf(XsAnyAtomicTypeSeqExpr seqParam, String srchParam, String collationLiteral) {
        return indexOf(seqParam, (srchParam == null) ? (XsAnyAtomicTypeExpr) null : xs.string(srchParam), (collationLiteral == null) ? (XsStringExpr) null : xs.string(collationLiteral));
    }

    
    @Override
    public XsIntegerSeqExpr indexOf(XsAnyAtomicTypeSeqExpr seqParam, XsAnyAtomicTypeExpr srchParam, XsStringExpr collationLiteral) {
        if (srchParam == null) {
            throw new IllegalArgumentException("srchParam parameter for indexOf() cannot be null");
        }
        if (collationLiteral == null) {
            throw new IllegalArgumentException("collationLiteral parameter for indexOf() cannot be null");
        }
        return new XsExprImpl.IntegerSeqCallImpl("fn", "index-of", new Object[]{ seqParam, srchParam, collationLiteral });
    }

    
    @Override
    public ItemSeqExpr insertBefore(ItemSeqExpr target, long position, ItemSeqExpr inserts) {
        return insertBefore(target, xs.integer(position), inserts);
    }

    
    @Override
    public ItemSeqExpr insertBefore(ItemSeqExpr target, XsIntegerExpr position, ItemSeqExpr inserts) {
        if (position == null) {
            throw new IllegalArgumentException("position parameter for insertBefore() cannot be null");
        }
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "insert-before", new Object[]{ target, position, inserts });
    }

    
    @Override
    public XsStringExpr iriToUri(XsStringExpr uriPart) {
        return new XsExprImpl.StringCallImpl("fn", "iri-to-uri", new Object[]{ uriPart });
    }

    
    @Override
    public XsBooleanExpr lang(XsStringExpr testlang, NodeExpr node) {
        return new XsExprImpl.BooleanCallImpl("fn", "lang", new Object[]{ testlang, node });
    }

    
    @Override
    public XsStringExpr localName(NodeExpr arg) {
        return new XsExprImpl.StringCallImpl("fn", "local-name", new Object[]{ arg });
    }

    
    @Override
    public XsNCNameExpr localNameFromQName(XsQNameExpr arg) {
        return new XsExprImpl.NCNameCallImpl("fn", "local-name-from-QName", new Object[]{ arg });
    }

    
    @Override
    public XsStringExpr lowerCase(XsStringExpr string) {
        return new XsExprImpl.StringCallImpl("fn", "lower-case", new Object[]{ string });
    }

    
    @Override
    public XsBooleanExpr matches(XsStringExpr input, String pattern) {
        return matches(input, (pattern == null) ? (XsStringExpr) null : xs.string(pattern));
    }

    
    @Override
    public XsBooleanExpr matches(XsStringExpr input, XsStringExpr pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern parameter for matches() cannot be null");
        }
        return new XsExprImpl.BooleanCallImpl("fn", "matches", new Object[]{ input, pattern });
    }

    
    @Override
    public XsBooleanExpr matches(XsStringExpr input, String pattern, String flags) {
        return matches(input, (pattern == null) ? (XsStringExpr) null : xs.string(pattern), (flags == null) ? (XsStringExpr) null : xs.string(flags));
    }

    
    @Override
    public XsBooleanExpr matches(XsStringExpr input, XsStringExpr pattern, XsStringExpr flags) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern parameter for matches() cannot be null");
        }
        if (flags == null) {
            throw new IllegalArgumentException("flags parameter for matches() cannot be null");
        }
        return new XsExprImpl.BooleanCallImpl("fn", "matches", new Object[]{ input, pattern, flags });
    }

    
    @Override
    public XsAnyAtomicTypeExpr max(XsAnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "max", new Object[]{ arg });
    }

    
    @Override
    public XsAnyAtomicTypeExpr max(XsAnyAtomicTypeSeqExpr arg, String collation) {
        return max(arg, (collation == null) ? (XsStringExpr) null : xs.string(collation));
    }

    
    @Override
    public XsAnyAtomicTypeExpr max(XsAnyAtomicTypeSeqExpr arg, XsStringExpr collation) {
        if (collation == null) {
            throw new IllegalArgumentException("collation parameter for max() cannot be null");
        }
        return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "max", new Object[]{ arg, collation });
    }

    
    @Override
    public XsAnyAtomicTypeExpr min(XsAnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "min", new Object[]{ arg });
    }

    
    @Override
    public XsAnyAtomicTypeExpr min(XsAnyAtomicTypeSeqExpr arg, String collation) {
        return min(arg, (collation == null) ? (XsStringExpr) null : xs.string(collation));
    }

    
    @Override
    public XsAnyAtomicTypeExpr min(XsAnyAtomicTypeSeqExpr arg, XsStringExpr collation) {
        if (collation == null) {
            throw new IllegalArgumentException("collation parameter for min() cannot be null");
        }
        return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "min", new Object[]{ arg, collation });
    }

    
    @Override
    public XsIntegerExpr minutesFromDateTime(XsDateTimeExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "minutes-from-dateTime", new Object[]{ arg });
    }

    
    @Override
    public XsIntegerExpr minutesFromDuration(XsDurationExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "minutes-from-duration", new Object[]{ arg });
    }

    
    @Override
    public XsIntegerExpr minutesFromTime(XsTimeExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "minutes-from-time", new Object[]{ arg });
    }

    
    @Override
    public XsIntegerExpr monthFromDate(XsDateExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "month-from-date", new Object[]{ arg });
    }

    
    @Override
    public XsIntegerExpr monthFromDateTime(XsDateTimeExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "month-from-dateTime", new Object[]{ arg });
    }

    
    @Override
    public XsIntegerExpr monthsFromDuration(XsDurationExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "months-from-duration", new Object[]{ arg });
    }

    
    @Override
    public XsStringExpr name(NodeExpr arg) {
        return new XsExprImpl.StringCallImpl("fn", "name", new Object[]{ arg });
    }

    
    @Override
    public XsAnyURIExpr namespaceUri(NodeExpr arg) {
        return new XsExprImpl.AnyURICallImpl("fn", "namespace-uri", new Object[]{ arg });
    }

    
    @Override
    public XsAnyURIExpr namespaceUriForPrefix(XsStringExpr prefix, ElementNodeExpr element) {
        if (element == null) {
            throw new IllegalArgumentException("element parameter for namespaceUriForPrefix() cannot be null");
        }
        return new XsExprImpl.AnyURICallImpl("fn", "namespace-uri-for-prefix", new Object[]{ prefix, element });
    }

    
    @Override
    public XsAnyURIExpr namespaceUriFromQName(XsQNameExpr arg) {
        return new XsExprImpl.AnyURICallImpl("fn", "namespace-uri-from-QName", new Object[]{ arg });
    }

    
    @Override
    public XsBooleanExpr nilled(NodeExpr arg) {
        return new XsExprImpl.BooleanCallImpl("fn", "nilled", new Object[]{ arg });
    }

    
    @Override
    public XsQNameExpr nodeName(NodeExpr arg) {
        return new XsExprImpl.QNameCallImpl("fn", "node-name", new Object[]{ arg });
    }

    
    @Override
    public XsStringExpr normalizeSpace(XsStringExpr input) {
        return new XsExprImpl.StringCallImpl("fn", "normalize-space", new Object[]{ input });
    }

    
    @Override
    public XsStringExpr normalizeUnicode(XsStringExpr arg) {
        return new XsExprImpl.StringCallImpl("fn", "normalize-unicode", new Object[]{ arg });
    }

    
    @Override
    public XsStringExpr normalizeUnicode(XsStringExpr arg, String normalizationForm) {
        return normalizeUnicode(arg, (normalizationForm == null) ? (XsStringExpr) null : xs.string(normalizationForm));
    }

    
    @Override
    public XsStringExpr normalizeUnicode(XsStringExpr arg, XsStringExpr normalizationForm) {
        if (normalizationForm == null) {
            throw new IllegalArgumentException("normalizationForm parameter for normalizeUnicode() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "normalize-unicode", new Object[]{ arg, normalizationForm });
    }

    
    @Override
    public XsBooleanExpr not(ItemSeqExpr arg) {
        return new XsExprImpl.BooleanCallImpl("fn", "not", new Object[]{ arg });
    }

    
    @Override
    public XsDoubleExpr number(XsAnyAtomicTypeExpr arg) {
        return new XsExprImpl.DoubleCallImpl("fn", "number", new Object[]{ arg });
    }

    
    @Override
    public XsNCNameExpr prefixFromQName(XsQNameExpr arg) {
        return new XsExprImpl.NCNameCallImpl("fn", "prefix-from-QName", new Object[]{ arg });
    }

    
    @Override
    public XsQNameExpr QName(XsStringExpr paramURI, String paramQName) {
        return QName(paramURI, (paramQName == null) ? (XsStringExpr) null : xs.string(paramQName));
    }

    
    @Override
    public XsQNameExpr QName(XsStringExpr paramURI, XsStringExpr paramQName) {
        if (paramQName == null) {
            throw new IllegalArgumentException("paramQName parameter for QName() cannot be null");
        }
        return new XsExprImpl.QNameCallImpl("fn", "QName", new Object[]{ paramURI, paramQName });
    }

    
    @Override
    public ItemSeqExpr remove(ItemSeqExpr target, long position) {
        return remove(target, xs.integer(position));
    }

    
    @Override
    public ItemSeqExpr remove(ItemSeqExpr target, XsIntegerExpr position) {
        if (position == null) {
            throw new IllegalArgumentException("position parameter for remove() cannot be null");
        }
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "remove", new Object[]{ target, position });
    }

    
    @Override
    public XsStringExpr replace(XsStringExpr input, String pattern, String replacement) {
        return replace(input, (pattern == null) ? (XsStringExpr) null : xs.string(pattern), (replacement == null) ? (XsStringExpr) null : xs.string(replacement));
    }

    
    @Override
    public XsStringExpr replace(XsStringExpr input, XsStringExpr pattern, XsStringExpr replacement) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern parameter for replace() cannot be null");
        }
        if (replacement == null) {
            throw new IllegalArgumentException("replacement parameter for replace() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "replace", new Object[]{ input, pattern, replacement });
    }

    
    @Override
    public XsStringExpr replace(XsStringExpr input, String pattern, String replacement, String flags) {
        return replace(input, (pattern == null) ? (XsStringExpr) null : xs.string(pattern), (replacement == null) ? (XsStringExpr) null : xs.string(replacement), (flags == null) ? (XsStringExpr) null : xs.string(flags));
    }

    
    @Override
    public XsStringExpr replace(XsStringExpr input, XsStringExpr pattern, XsStringExpr replacement, XsStringExpr flags) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern parameter for replace() cannot be null");
        }
        if (replacement == null) {
            throw new IllegalArgumentException("replacement parameter for replace() cannot be null");
        }
        if (flags == null) {
            throw new IllegalArgumentException("flags parameter for replace() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "replace", new Object[]{ input, pattern, replacement, flags });
    }

    
    @Override
    public XsQNameExpr resolveQName(XsStringExpr qname, ElementNodeExpr element) {
        if (element == null) {
            throw new IllegalArgumentException("element parameter for resolveQName() cannot be null");
        }
        return new XsExprImpl.QNameCallImpl("fn", "resolve-QName", new Object[]{ qname, element });
    }

    
    @Override
    public XsAnyURIExpr resolveUri(XsStringExpr relative, String base) {
        return resolveUri(relative, (base == null) ? (XsStringExpr) null : xs.string(base));
    }

    
    @Override
    public XsAnyURIExpr resolveUri(XsStringExpr relative, XsStringExpr base) {
        if (base == null) {
            throw new IllegalArgumentException("base parameter for resolveUri() cannot be null");
        }
        return new XsExprImpl.AnyURICallImpl("fn", "resolve-uri", new Object[]{ relative, base });
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
        return new XsExprImpl.NumericCallImpl("fn", "round", new Object[]{ arg });
    }

    
    @Override
    public XsNumericExpr roundHalfToEven(XsNumericExpr arg) {
        return new XsExprImpl.NumericCallImpl("fn", "round-half-to-even", new Object[]{ arg });
    }

    
    @Override
    public XsNumericExpr roundHalfToEven(XsNumericExpr arg, long precision) {
        return roundHalfToEven(arg, xs.integer(precision));
    }

    
    @Override
    public XsNumericExpr roundHalfToEven(XsNumericExpr arg, XsIntegerExpr precision) {
        if (precision == null) {
            throw new IllegalArgumentException("precision parameter for roundHalfToEven() cannot be null");
        }
        return new XsExprImpl.NumericCallImpl("fn", "round-half-to-even", new Object[]{ arg, precision });
    }

    
    @Override
    public XsDecimalExpr secondsFromDateTime(XsDateTimeExpr arg) {
        return new XsExprImpl.DecimalCallImpl("fn", "seconds-from-dateTime", new Object[]{ arg });
    }

    
    @Override
    public XsDecimalExpr secondsFromDuration(XsDurationExpr arg) {
        return new XsExprImpl.DecimalCallImpl("fn", "seconds-from-duration", new Object[]{ arg });
    }

    
    @Override
    public XsDecimalExpr secondsFromTime(XsTimeExpr arg) {
        return new XsExprImpl.DecimalCallImpl("fn", "seconds-from-time", new Object[]{ arg });
    }

    
    @Override
    public XsBooleanExpr startsWith(XsStringExpr parameter1, String parameter2) {
        return startsWith(parameter1, (parameter2 == null) ? (XsStringExpr) null : xs.string(parameter2));
    }

    
    @Override
    public XsBooleanExpr startsWith(XsStringExpr parameter1, XsStringExpr parameter2) {
        return new XsExprImpl.BooleanCallImpl("fn", "starts-with", new Object[]{ parameter1, parameter2 });
    }

    
    @Override
    public XsBooleanExpr startsWith(XsStringExpr parameter1, String parameter2, String collation) {
        return startsWith(parameter1, (parameter2 == null) ? (XsStringExpr) null : xs.string(parameter2), (collation == null) ? (XsStringExpr) null : xs.string(collation));
    }

    
    @Override
    public XsBooleanExpr startsWith(XsStringExpr parameter1, XsStringExpr parameter2, XsStringExpr collation) {
        if (collation == null) {
            throw new IllegalArgumentException("collation parameter for startsWith() cannot be null");
        }
        return new XsExprImpl.BooleanCallImpl("fn", "starts-with", new Object[]{ parameter1, parameter2, collation });
    }

    
    @Override
    public XsStringExpr string(ItemExpr arg) {
        return new XsExprImpl.StringCallImpl("fn", "string", new Object[]{ arg });
    }

    
    @Override
    public XsStringExpr stringJoin(XsStringSeqExpr parameter1, String parameter2) {
        return stringJoin(parameter1, (parameter2 == null) ? (XsStringExpr) null : xs.string(parameter2));
    }

    
    @Override
    public XsStringExpr stringJoin(XsStringSeqExpr parameter1, XsStringExpr parameter2) {
        if (parameter2 == null) {
            throw new IllegalArgumentException("parameter2 parameter for stringJoin() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "string-join", new Object[]{ parameter1, parameter2 });
    }

    
    @Override
    public XsIntegerExpr stringLength(XsStringExpr sourceString) {
        return new XsExprImpl.IntegerCallImpl("fn", "string-length", new Object[]{ sourceString });
    }

    
    @Override
    public XsIntegerSeqExpr stringToCodepoints(XsStringExpr arg) {
        return new XsExprImpl.IntegerSeqCallImpl("fn", "string-to-codepoints", new Object[]{ arg });
    }

    
    @Override
    public ItemSeqExpr subsequence(ItemSeqExpr sourceSeq, double startingLoc) {
        return subsequence(sourceSeq, xs.doubleVal(startingLoc));
    }

    
    @Override
    public ItemSeqExpr subsequence(ItemSeqExpr sourceSeq, XsNumericExpr startingLoc) {
        if (startingLoc == null) {
            throw new IllegalArgumentException("startingLoc parameter for subsequence() cannot be null");
        }
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "subsequence", new Object[]{ sourceSeq, startingLoc });
    }

    
    @Override
    public ItemSeqExpr subsequence(ItemSeqExpr sourceSeq, double startingLoc, double length) {
        return subsequence(sourceSeq, xs.doubleVal(startingLoc), xs.doubleVal(length));
    }

    
    @Override
    public ItemSeqExpr subsequence(ItemSeqExpr sourceSeq, XsNumericExpr startingLoc, XsNumericExpr length) {
        if (startingLoc == null) {
            throw new IllegalArgumentException("startingLoc parameter for subsequence() cannot be null");
        }
        if (length == null) {
            throw new IllegalArgumentException("length parameter for subsequence() cannot be null");
        }
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "subsequence", new Object[]{ sourceSeq, startingLoc, length });
    }

    
    @Override
    public XsStringExpr substring(XsStringExpr sourceString, double startingLoc) {
        return substring(sourceString, xs.doubleVal(startingLoc));
    }

    
    @Override
    public XsStringExpr substring(XsStringExpr sourceString, XsNumericExpr startingLoc) {
        if (startingLoc == null) {
            throw new IllegalArgumentException("startingLoc parameter for substring() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "substring", new Object[]{ sourceString, startingLoc });
    }

    
    @Override
    public XsStringExpr substring(XsStringExpr sourceString, double startingLoc, double length) {
        return substring(sourceString, xs.doubleVal(startingLoc), xs.doubleVal(length));
    }

    
    @Override
    public XsStringExpr substring(XsStringExpr sourceString, XsNumericExpr startingLoc, XsNumericExpr length) {
        if (startingLoc == null) {
            throw new IllegalArgumentException("startingLoc parameter for substring() cannot be null");
        }
        if (length == null) {
            throw new IllegalArgumentException("length parameter for substring() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "substring", new Object[]{ sourceString, startingLoc, length });
    }

    
    @Override
    public XsStringExpr substringAfter(XsStringExpr input, String after) {
        return substringAfter(input, (after == null) ? (XsStringExpr) null : xs.string(after));
    }

    
    @Override
    public XsStringExpr substringAfter(XsStringExpr input, XsStringExpr after) {
        return new XsExprImpl.StringCallImpl("fn", "substring-after", new Object[]{ input, after });
    }

    
    @Override
    public XsStringExpr substringAfter(XsStringExpr input, String after, String collation) {
        return substringAfter(input, (after == null) ? (XsStringExpr) null : xs.string(after), (collation == null) ? (XsStringExpr) null : xs.string(collation));
    }

    
    @Override
    public XsStringExpr substringAfter(XsStringExpr input, XsStringExpr after, XsStringExpr collation) {
        if (collation == null) {
            throw new IllegalArgumentException("collation parameter for substringAfter() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "substring-after", new Object[]{ input, after, collation });
    }

    
    @Override
    public XsStringExpr substringBefore(XsStringExpr input, String before) {
        return substringBefore(input, (before == null) ? (XsStringExpr) null : xs.string(before));
    }

    
    @Override
    public XsStringExpr substringBefore(XsStringExpr input, XsStringExpr before) {
        return new XsExprImpl.StringCallImpl("fn", "substring-before", new Object[]{ input, before });
    }

    
    @Override
    public XsStringExpr substringBefore(XsStringExpr input, String before, String collation) {
        return substringBefore(input, (before == null) ? (XsStringExpr) null : xs.string(before), (collation == null) ? (XsStringExpr) null : xs.string(collation));
    }

    
    @Override
    public XsStringExpr substringBefore(XsStringExpr input, XsStringExpr before, XsStringExpr collation) {
        if (collation == null) {
            throw new IllegalArgumentException("collation parameter for substringBefore() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "substring-before", new Object[]{ input, before, collation });
    }

    
    @Override
    public XsAnyAtomicTypeExpr sum(XsAnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "sum", new Object[]{ arg });
    }

    
    @Override
    public XsAnyAtomicTypeExpr sum(XsAnyAtomicTypeSeqExpr arg, String zero) {
        return sum(arg, (zero == null) ? (XsAnyAtomicTypeExpr) null : xs.string(zero));
    }

    
    @Override
    public XsAnyAtomicTypeExpr sum(XsAnyAtomicTypeSeqExpr arg, XsAnyAtomicTypeExpr zero) {
        return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "sum", new Object[]{ arg, zero });
    }

    
    @Override
    public ItemSeqExpr tail(ItemSeqExpr seq) {
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "tail", new Object[]{ seq });
    }

    
    @Override
    public XsDayTimeDurationExpr timezoneFromDate(XsDateExpr arg) {
        return new XsExprImpl.DayTimeDurationCallImpl("fn", "timezone-from-date", new Object[]{ arg });
    }

    
    @Override
    public XsDayTimeDurationExpr timezoneFromDateTime(XsDateTimeExpr arg) {
        return new XsExprImpl.DayTimeDurationCallImpl("fn", "timezone-from-dateTime", new Object[]{ arg });
    }

    
    @Override
    public XsDayTimeDurationExpr timezoneFromTime(XsTimeExpr arg) {
        return new XsExprImpl.DayTimeDurationCallImpl("fn", "timezone-from-time", new Object[]{ arg });
    }

    
    @Override
    public XsStringSeqExpr tokenize(XsStringExpr input, String pattern) {
        return tokenize(input, (pattern == null) ? (XsStringExpr) null : xs.string(pattern));
    }

    
    @Override
    public XsStringSeqExpr tokenize(XsStringExpr input, XsStringExpr pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern parameter for tokenize() cannot be null");
        }
        return new XsExprImpl.StringSeqCallImpl("fn", "tokenize", new Object[]{ input, pattern });
    }

    
    @Override
    public XsStringSeqExpr tokenize(XsStringExpr input, String pattern, String flags) {
        return tokenize(input, (pattern == null) ? (XsStringExpr) null : xs.string(pattern), (flags == null) ? (XsStringExpr) null : xs.string(flags));
    }

    
    @Override
    public XsStringSeqExpr tokenize(XsStringExpr input, XsStringExpr pattern, XsStringExpr flags) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern parameter for tokenize() cannot be null");
        }
        if (flags == null) {
            throw new IllegalArgumentException("flags parameter for tokenize() cannot be null");
        }
        return new XsExprImpl.StringSeqCallImpl("fn", "tokenize", new Object[]{ input, pattern, flags });
    }

    
    @Override
    public XsStringExpr translate(XsStringExpr src, String mapString, String transString) {
        return translate(src, (mapString == null) ? (XsStringExpr) null : xs.string(mapString), (transString == null) ? (XsStringExpr) null : xs.string(transString));
    }

    
    @Override
    public XsStringExpr translate(XsStringExpr src, XsStringExpr mapString, XsStringExpr transString) {
        if (mapString == null) {
            throw new IllegalArgumentException("mapString parameter for translate() cannot be null");
        }
        if (transString == null) {
            throw new IllegalArgumentException("transString parameter for translate() cannot be null");
        }
        return new XsExprImpl.StringCallImpl("fn", "translate", new Object[]{ src, mapString, transString });
    }

    
    @Override
    public XsBooleanExpr trueExpr() {
        return new XsExprImpl.BooleanCallImpl("fn", "true", new Object[]{  });
    }

    
    @Override
    public ItemSeqExpr unordered(ItemSeqExpr sourceSeq) {
        return new BaseTypeImpl.ItemSeqCallImpl("fn", "unordered", new Object[]{ sourceSeq });
    }

    
    @Override
    public XsStringExpr upperCase(XsStringExpr string) {
        return new XsExprImpl.StringCallImpl("fn", "upper-case", new Object[]{ string });
    }

    
    @Override
    public XsIntegerExpr yearFromDate(XsDateExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "year-from-date", new Object[]{ arg });
    }

    
    @Override
    public XsIntegerExpr yearFromDateTime(XsDateTimeExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "year-from-dateTime", new Object[]{ arg });
    }

    
    @Override
    public XsIntegerExpr yearsFromDuration(XsDurationExpr arg) {
        return new XsExprImpl.IntegerCallImpl("fn", "years-from-duration", new Object[]{ arg });
    }

    }
