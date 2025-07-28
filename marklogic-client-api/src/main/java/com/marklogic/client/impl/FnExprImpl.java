/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.impl;

import com.marklogic.client.type.XsAnyAtomicTypeSeqVal;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.type.XsAnyURIVal;
import com.marklogic.client.type.XsBooleanVal;
import com.marklogic.client.type.XsDateTimeVal;
import com.marklogic.client.type.XsDateVal;
import com.marklogic.client.type.XsDayTimeDurationVal;
import com.marklogic.client.type.XsDecimalVal;
import com.marklogic.client.type.XsDoubleVal;
import com.marklogic.client.type.XsDurationVal;
import com.marklogic.client.type.XsIntegerSeqVal;
import com.marklogic.client.type.XsIntegerVal;
import com.marklogic.client.type.XsQNameVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;
import com.marklogic.client.type.XsTimeVal;

import com.marklogic.client.type.ServerExpression;

import com.marklogic.client.expression.FnExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class FnExprImpl implements FnExpr {

  final static XsExprImpl xs = XsExprImpl.xs;

  final static FnExprImpl fn = new FnExprImpl();

  FnExprImpl() {
  }


  @Override
  public ServerExpression abs(ServerExpression arg) {
    return new XsExprImpl.NumericCallImpl("fn", "abs", new Object[]{ arg });
  }


  @Override
  public ServerExpression adjustDateToTimezone(ServerExpression arg) {
    return new XsExprImpl.DateCallImpl("fn", "adjust-date-to-timezone", new Object[]{ arg });
  }


  @Override
  public ServerExpression adjustDateToTimezone(ServerExpression arg, String timezone) {
    return adjustDateToTimezone(arg, (timezone == null) ? (ServerExpression) null : xs.dayTimeDuration(timezone));
  }


  @Override
  public ServerExpression adjustDateToTimezone(ServerExpression arg, ServerExpression timezone) {
    return new XsExprImpl.DateCallImpl("fn", "adjust-date-to-timezone", new Object[]{ arg, timezone });
  }


  @Override
  public ServerExpression adjustDateTimeToTimezone(ServerExpression arg) {
    return new XsExprImpl.DateTimeCallImpl("fn", "adjust-dateTime-to-timezone", new Object[]{ arg });
  }


  @Override
  public ServerExpression adjustDateTimeToTimezone(ServerExpression arg, String timezone) {
    return adjustDateTimeToTimezone(arg, (timezone == null) ? (ServerExpression) null : xs.dayTimeDuration(timezone));
  }


  @Override
  public ServerExpression adjustDateTimeToTimezone(ServerExpression arg, ServerExpression timezone) {
    return new XsExprImpl.DateTimeCallImpl("fn", "adjust-dateTime-to-timezone", new Object[]{ arg, timezone });
  }


  @Override
  public ServerExpression adjustTimeToTimezone(ServerExpression arg) {
    return new XsExprImpl.TimeCallImpl("fn", "adjust-time-to-timezone", new Object[]{ arg });
  }


  @Override
  public ServerExpression adjustTimeToTimezone(ServerExpression arg, String timezone) {
    return adjustTimeToTimezone(arg, (timezone == null) ? (ServerExpression) null : xs.dayTimeDuration(timezone));
  }


  @Override
  public ServerExpression adjustTimeToTimezone(ServerExpression arg, ServerExpression timezone) {
    return new XsExprImpl.TimeCallImpl("fn", "adjust-time-to-timezone", new Object[]{ arg, timezone });
  }


  @Override
  public ServerExpression analyzeString(String in, String regex) {
    return analyzeString((in == null) ? (ServerExpression) null : xs.string(in), (regex == null) ? (ServerExpression) null : xs.string(regex));
  }


  @Override
  public ServerExpression analyzeString(ServerExpression in, ServerExpression regex) {
    if (regex == null) {
      throw new IllegalArgumentException("regex parameter for analyzeString() cannot be null");
    }
    return new BaseTypeImpl.ElementNodeCallImpl("fn", "analyze-string", new Object[]{ in, regex });
  }


  @Override
  public ServerExpression analyzeString(String in, String regex, String flags) {
    return analyzeString((in == null) ? (ServerExpression) null : xs.string(in), (regex == null) ? (ServerExpression) null : xs.string(regex), (flags == null) ? (ServerExpression) null : xs.string(flags));
  }


  @Override
  public ServerExpression analyzeString(ServerExpression in, ServerExpression regex, ServerExpression flags) {
    if (regex == null) {
      throw new IllegalArgumentException("regex parameter for analyzeString() cannot be null");
    }
    if (flags == null) {
      throw new IllegalArgumentException("flags parameter for analyzeString() cannot be null");
    }
    return new BaseTypeImpl.ElementNodeCallImpl("fn", "analyze-string", new Object[]{ in, regex, flags });
  }


  @Override
  public ServerExpression avg(ServerExpression arg) {
    return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "avg", new Object[]{ arg });
  }


  @Override
  public ServerExpression baseUri(ServerExpression arg) {
    return new XsExprImpl.AnyURICallImpl("fn", "base-uri", new Object[]{ arg });
  }


  @Override
  public ServerExpression booleanExpr(ServerExpression arg) {
    return new XsExprImpl.BooleanCallImpl("fn", "boolean", new Object[]{ arg });
  }


  @Override
  public ServerExpression ceiling(ServerExpression arg) {
    return new XsExprImpl.NumericCallImpl("fn", "ceiling", new Object[]{ arg });
  }


  @Override
  public ServerExpression codepointEqual(ServerExpression comparand1, String comparand2) {
    return codepointEqual(comparand1, (comparand2 == null) ? (ServerExpression) null : xs.string(comparand2));
  }


  @Override
  public ServerExpression codepointEqual(ServerExpression comparand1, ServerExpression comparand2) {
    return new XsExprImpl.BooleanCallImpl("fn", "codepoint-equal", new Object[]{ comparand1, comparand2 });
  }


  @Override
  public ServerExpression codepointsToString(ServerExpression arg) {
    return new XsExprImpl.StringCallImpl("fn", "codepoints-to-string", new Object[]{ arg });
  }


  @Override
  public ServerExpression compare(ServerExpression comparand1, String comparand2) {
    return compare(comparand1, (comparand2 == null) ? (ServerExpression) null : xs.string(comparand2));
  }


  @Override
  public ServerExpression compare(ServerExpression comparand1, ServerExpression comparand2) {
    return new XsExprImpl.IntegerCallImpl("fn", "compare", new Object[]{ comparand1, comparand2 });
  }


  @Override
  public ServerExpression compare(ServerExpression comparand1, String comparand2, String collation) {
    return compare(comparand1, (comparand2 == null) ? (ServerExpression) null : xs.string(comparand2), (collation == null) ? (ServerExpression) null : xs.string(collation));
  }


  @Override
  public ServerExpression compare(ServerExpression comparand1, ServerExpression comparand2, ServerExpression collation) {
    if (collation == null) {
      throw new IllegalArgumentException("collation parameter for compare() cannot be null");
    }
    return new XsExprImpl.IntegerCallImpl("fn", "compare", new Object[]{ comparand1, comparand2, collation });
  }


  @Override
  public ServerExpression concat(ServerExpression... parameter1) {
    return new XsExprImpl.StringCallImpl("fn", "concat", parameter1);
  }


  @Override
  public ServerExpression contains(ServerExpression parameter1, String parameter2) {
    return contains(parameter1, (parameter2 == null) ? (ServerExpression) null : xs.string(parameter2));
  }


  @Override
  public ServerExpression contains(ServerExpression parameter1, ServerExpression parameter2) {
    return new XsExprImpl.BooleanCallImpl("fn", "contains", new Object[]{ parameter1, parameter2 });
  }


  @Override
  public ServerExpression contains(ServerExpression parameter1, String parameter2, String collation) {
    return contains(parameter1, (parameter2 == null) ? (ServerExpression) null : xs.string(parameter2), (collation == null) ? (ServerExpression) null : xs.string(collation));
  }


  @Override
  public ServerExpression contains(ServerExpression parameter1, ServerExpression parameter2, ServerExpression collation) {
    if (collation == null) {
      throw new IllegalArgumentException("collation parameter for contains() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("fn", "contains", new Object[]{ parameter1, parameter2, collation });
  }


  @Override
  public ServerExpression count(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("fn", "count", new Object[]{ arg });
  }


  @Override
  public ServerExpression count(ServerExpression arg, double maximum) {
    return count(arg, xs.doubleVal(maximum));
  }


  @Override
  public ServerExpression count(ServerExpression arg, ServerExpression maximum) {
    return new XsExprImpl.IntegerCallImpl("fn", "count", new Object[]{ arg, maximum });
  }


  @Override
  public ServerExpression currentDate() {
    return new XsExprImpl.DateCallImpl("fn", "current-date", new Object[]{  });
  }


  @Override
  public ServerExpression currentDateTime() {
    return new XsExprImpl.DateTimeCallImpl("fn", "current-dateTime", new Object[]{  });
  }


  @Override
  public ServerExpression currentTime() {
    return new XsExprImpl.TimeCallImpl("fn", "current-time", new Object[]{  });
  }


  @Override
  public ServerExpression dateTime(ServerExpression arg1, String arg2) {
    return dateTime(arg1, (arg2 == null) ? (ServerExpression) null : xs.time(arg2));
  }


  @Override
  public ServerExpression dateTime(ServerExpression arg1, ServerExpression arg2) {
    return new XsExprImpl.DateTimeCallImpl("fn", "dateTime", new Object[]{ arg1, arg2 });
  }


  @Override
  public ServerExpression dayFromDate(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("fn", "day-from-date", new Object[]{ arg });
  }


  @Override
  public ServerExpression dayFromDateTime(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("fn", "day-from-dateTime", new Object[]{ arg });
  }


  @Override
  public ServerExpression daysFromDuration(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("fn", "days-from-duration", new Object[]{ arg });
  }


  @Override
  public ServerExpression deepEqual(ServerExpression parameter1, ServerExpression parameter2) {
    return new XsExprImpl.BooleanCallImpl("fn", "deep-equal", new Object[]{ parameter1, parameter2 });
  }


  @Override
  public ServerExpression deepEqual(ServerExpression parameter1, ServerExpression parameter2, String collation) {
    return deepEqual(parameter1, parameter2, (collation == null) ? (ServerExpression) null : xs.string(collation));
  }


  @Override
  public ServerExpression deepEqual(ServerExpression parameter1, ServerExpression parameter2, ServerExpression collation) {
    if (collation == null) {
      throw new IllegalArgumentException("collation parameter for deepEqual() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("fn", "deep-equal", new Object[]{ parameter1, parameter2, collation });
  }


  @Override
  public ServerExpression defaultCollation() {
    return new XsExprImpl.StringCallImpl("fn", "default-collation", new Object[]{  });
  }


  @Override
  public ServerExpression distinctValues(ServerExpression arg) {
    return new XsExprImpl.AnyAtomicTypeSeqCallImpl("fn", "distinct-values", new Object[]{ arg });
  }


  @Override
  public ServerExpression distinctValues(ServerExpression arg, String collation) {
    return distinctValues(arg, (collation == null) ? (ServerExpression) null : xs.string(collation));
  }


  @Override
  public ServerExpression distinctValues(ServerExpression arg, ServerExpression collation) {
    if (collation == null) {
      throw new IllegalArgumentException("collation parameter for distinctValues() cannot be null");
    }
    return new XsExprImpl.AnyAtomicTypeSeqCallImpl("fn", "distinct-values", new Object[]{ arg, collation });
  }


  @Override
  public ServerExpression documentUri(ServerExpression arg) {
    return new XsExprImpl.AnyURICallImpl("fn", "document-uri", new Object[]{ arg });
  }


  @Override
  public ServerExpression empty(ServerExpression arg) {
    return new XsExprImpl.BooleanCallImpl("fn", "empty", new Object[]{ arg });
  }


  @Override
  public ServerExpression encodeForUri(ServerExpression uriPart) {
    return new XsExprImpl.StringCallImpl("fn", "encode-for-uri", new Object[]{ uriPart });
  }


  @Override
  public ServerExpression endsWith(ServerExpression parameter1, String parameter2) {
    return endsWith(parameter1, (parameter2 == null) ? (ServerExpression) null : xs.string(parameter2));
  }


  @Override
  public ServerExpression endsWith(ServerExpression parameter1, ServerExpression parameter2) {
    return new XsExprImpl.BooleanCallImpl("fn", "ends-with", new Object[]{ parameter1, parameter2 });
  }


  @Override
  public ServerExpression endsWith(ServerExpression parameter1, String parameter2, String collation) {
    return endsWith(parameter1, (parameter2 == null) ? (ServerExpression) null : xs.string(parameter2), (collation == null) ? (ServerExpression) null : xs.string(collation));
  }


  @Override
  public ServerExpression endsWith(ServerExpression parameter1, ServerExpression parameter2, ServerExpression collation) {
    if (collation == null) {
      throw new IllegalArgumentException("collation parameter for endsWith() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("fn", "ends-with", new Object[]{ parameter1, parameter2, collation });
  }


  @Override
  public ServerExpression escapeHtmlUri(ServerExpression uriPart) {
    return new XsExprImpl.StringCallImpl("fn", "escape-html-uri", new Object[]{ uriPart });
  }


  @Override
  public ServerExpression exists(ServerExpression arg) {
    return new XsExprImpl.BooleanCallImpl("fn", "exists", new Object[]{ arg });
  }


  @Override
  public ServerExpression falseExpr() {
    return new XsExprImpl.BooleanCallImpl("fn", "false", new Object[]{  });
  }


  @Override
  public ServerExpression floor(ServerExpression arg) {
    return new XsExprImpl.NumericCallImpl("fn", "floor", new Object[]{ arg });
  }


  @Override
  public ServerExpression formatDate(ServerExpression value, String picture) {
    return formatDate(value, (picture == null) ? (ServerExpression) null : xs.string(picture));
  }


  @Override
  public ServerExpression formatDate(ServerExpression value, ServerExpression picture) {
    if (picture == null) {
      throw new IllegalArgumentException("picture parameter for formatDate() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "format-date", new Object[]{ value, picture });
  }


  @Override
  public ServerExpression formatDate(ServerExpression value, String picture, String language) {
    return formatDate(value, (picture == null) ? (ServerExpression) null : xs.string(picture), (language == null) ? (ServerExpression) null : xs.string(language));
  }


  @Override
  public ServerExpression formatDate(ServerExpression value, ServerExpression picture, ServerExpression language) {
    if (picture == null) {
      throw new IllegalArgumentException("picture parameter for formatDate() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "format-date", new Object[]{ value, picture, language });
  }


  @Override
  public ServerExpression formatDate(ServerExpression value, String picture, String language, String calendar) {
    return formatDate(value, (picture == null) ? (ServerExpression) null : xs.string(picture), (language == null) ? (ServerExpression) null : xs.string(language), (calendar == null) ? (ServerExpression) null : xs.string(calendar));
  }


  @Override
  public ServerExpression formatDate(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression calendar) {
    if (picture == null) {
      throw new IllegalArgumentException("picture parameter for formatDate() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "format-date", new Object[]{ value, picture, language, calendar });
  }


  @Override
  public ServerExpression formatDate(ServerExpression value, String picture, String language, String calendar, String country) {
    return formatDate(value, (picture == null) ? (ServerExpression) null : xs.string(picture), (language == null) ? (ServerExpression) null : xs.string(language), (calendar == null) ? (ServerExpression) null : xs.string(calendar), (country == null) ? (ServerExpression) null : xs.string(country));
  }


  @Override
  public ServerExpression formatDate(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression calendar, ServerExpression country) {
    if (picture == null) {
      throw new IllegalArgumentException("picture parameter for formatDate() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "format-date", new Object[]{ value, picture, language, calendar, country });
  }


  @Override
  public ServerExpression formatDateTime(ServerExpression value, String picture) {
    return formatDateTime(value, (picture == null) ? (ServerExpression) null : xs.string(picture));
  }


  @Override
  public ServerExpression formatDateTime(ServerExpression value, ServerExpression picture) {
    if (picture == null) {
      throw new IllegalArgumentException("picture parameter for formatDateTime() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "format-dateTime", new Object[]{ value, picture });
  }


  @Override
  public ServerExpression formatDateTime(ServerExpression value, String picture, String language) {
    return formatDateTime(value, (picture == null) ? (ServerExpression) null : xs.string(picture), (language == null) ? (ServerExpression) null : xs.string(language));
  }


  @Override
  public ServerExpression formatDateTime(ServerExpression value, ServerExpression picture, ServerExpression language) {
    if (picture == null) {
      throw new IllegalArgumentException("picture parameter for formatDateTime() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "format-dateTime", new Object[]{ value, picture, language });
  }


  @Override
  public ServerExpression formatDateTime(ServerExpression value, String picture, String language, String calendar) {
    return formatDateTime(value, (picture == null) ? (ServerExpression) null : xs.string(picture), (language == null) ? (ServerExpression) null : xs.string(language), (calendar == null) ? (ServerExpression) null : xs.string(calendar));
  }


  @Override
  public ServerExpression formatDateTime(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression calendar) {
    if (picture == null) {
      throw new IllegalArgumentException("picture parameter for formatDateTime() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "format-dateTime", new Object[]{ value, picture, language, calendar });
  }


  @Override
  public ServerExpression formatDateTime(ServerExpression value, String picture, String language, String calendar, String country) {
    return formatDateTime(value, (picture == null) ? (ServerExpression) null : xs.string(picture), (language == null) ? (ServerExpression) null : xs.string(language), (calendar == null) ? (ServerExpression) null : xs.string(calendar), (country == null) ? (ServerExpression) null : xs.string(country));
  }


  @Override
  public ServerExpression formatDateTime(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression calendar, ServerExpression country) {
    if (picture == null) {
      throw new IllegalArgumentException("picture parameter for formatDateTime() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "format-dateTime", new Object[]{ value, picture, language, calendar, country });
  }


  @Override
  public ServerExpression formatNumber(ServerExpression value, String picture) {
    return formatNumber(value, (picture == null) ? (ServerExpression) null : xs.string(picture));
  }


  @Override
  public ServerExpression formatNumber(ServerExpression value, ServerExpression picture) {
    if (picture == null) {
      throw new IllegalArgumentException("picture parameter for formatNumber() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "format-number", new Object[]{ value, picture });
  }


  @Override
  public ServerExpression formatNumber(ServerExpression value, String picture, String decimalFormatName) {
    return formatNumber(value, (picture == null) ? (ServerExpression) null : xs.string(picture), (decimalFormatName == null) ? (ServerExpression) null : xs.string(decimalFormatName));
  }


  @Override
  public ServerExpression formatNumber(ServerExpression value, ServerExpression picture, ServerExpression decimalFormatName) {
    if (picture == null) {
      throw new IllegalArgumentException("picture parameter for formatNumber() cannot be null");
    }
    if (decimalFormatName == null) {
      throw new IllegalArgumentException("decimalFormatName parameter for formatNumber() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "format-number", new Object[]{ value, picture, decimalFormatName });
  }


  @Override
  public ServerExpression formatTime(ServerExpression value, String picture) {
    return formatTime(value, (picture == null) ? (ServerExpression) null : xs.string(picture));
  }


  @Override
  public ServerExpression formatTime(ServerExpression value, ServerExpression picture) {
    if (picture == null) {
      throw new IllegalArgumentException("picture parameter for formatTime() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "format-time", new Object[]{ value, picture });
  }


  @Override
  public ServerExpression formatTime(ServerExpression value, String picture, String language) {
    return formatTime(value, (picture == null) ? (ServerExpression) null : xs.string(picture), (language == null) ? (ServerExpression) null : xs.string(language));
  }


  @Override
  public ServerExpression formatTime(ServerExpression value, ServerExpression picture, ServerExpression language) {
    if (picture == null) {
      throw new IllegalArgumentException("picture parameter for formatTime() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "format-time", new Object[]{ value, picture, language });
  }


  @Override
  public ServerExpression formatTime(ServerExpression value, String picture, String language, String calendar) {
    return formatTime(value, (picture == null) ? (ServerExpression) null : xs.string(picture), (language == null) ? (ServerExpression) null : xs.string(language), (calendar == null) ? (ServerExpression) null : xs.string(calendar));
  }


  @Override
  public ServerExpression formatTime(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression calendar) {
    if (picture == null) {
      throw new IllegalArgumentException("picture parameter for formatTime() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "format-time", new Object[]{ value, picture, language, calendar });
  }


  @Override
  public ServerExpression formatTime(ServerExpression value, String picture, String language, String calendar, String country) {
    return formatTime(value, (picture == null) ? (ServerExpression) null : xs.string(picture), (language == null) ? (ServerExpression) null : xs.string(language), (calendar == null) ? (ServerExpression) null : xs.string(calendar), (country == null) ? (ServerExpression) null : xs.string(country));
  }


  @Override
  public ServerExpression formatTime(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression calendar, ServerExpression country) {
    if (picture == null) {
      throw new IllegalArgumentException("picture parameter for formatTime() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "format-time", new Object[]{ value, picture, language, calendar, country });
  }


  @Override
  public ServerExpression generateId(ServerExpression node) {
    return new XsExprImpl.StringCallImpl("fn", "generate-id", new Object[]{ node });
  }


  @Override
  public ServerExpression head(ServerExpression seq) {
    return new BaseTypeImpl.ItemCallImpl("fn", "head", new Object[]{ seq });
  }


  @Override
  public ServerExpression hoursFromDateTime(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("fn", "hours-from-dateTime", new Object[]{ arg });
  }


  @Override
  public ServerExpression hoursFromDuration(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("fn", "hours-from-duration", new Object[]{ arg });
  }


  @Override
  public ServerExpression hoursFromTime(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("fn", "hours-from-time", new Object[]{ arg });
  }


  @Override
  public ServerExpression implicitTimezone() {
    return new XsExprImpl.DayTimeDurationCallImpl("fn", "implicit-timezone", new Object[]{  });
  }


  @Override
  public ServerExpression inScopePrefixes(ServerExpression element) {
    if (element == null) {
      throw new IllegalArgumentException("element parameter for inScopePrefixes() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("fn", "in-scope-prefixes", new Object[]{ element });
  }


  @Override
  public ServerExpression indexOf(ServerExpression seqParam, String srchParam) {
    return indexOf(seqParam, (srchParam == null) ? (ServerExpression) null : xs.string(srchParam));
  }


  @Override
  public ServerExpression indexOf(ServerExpression seqParam, ServerExpression srchParam) {
    if (srchParam == null) {
      throw new IllegalArgumentException("srchParam parameter for indexOf() cannot be null");
    }
    return new XsExprImpl.IntegerSeqCallImpl("fn", "index-of", new Object[]{ seqParam, srchParam });
  }


  @Override
  public ServerExpression indexOf(ServerExpression seqParam, String srchParam, String collationLiteral) {
    return indexOf(seqParam, (srchParam == null) ? (ServerExpression) null : xs.string(srchParam), (collationLiteral == null) ? (ServerExpression) null : xs.string(collationLiteral));
  }


  @Override
  public ServerExpression indexOf(ServerExpression seqParam, ServerExpression srchParam, ServerExpression collationLiteral) {
    if (srchParam == null) {
      throw new IllegalArgumentException("srchParam parameter for indexOf() cannot be null");
    }
    if (collationLiteral == null) {
      throw new IllegalArgumentException("collationLiteral parameter for indexOf() cannot be null");
    }
    return new XsExprImpl.IntegerSeqCallImpl("fn", "index-of", new Object[]{ seqParam, srchParam, collationLiteral });
  }


  @Override
  public ServerExpression insertBefore(ServerExpression target, long position, ServerExpression inserts) {
    return insertBefore(target, xs.integer(position), inserts);
  }


  @Override
  public ServerExpression insertBefore(ServerExpression target, ServerExpression position, ServerExpression inserts) {
    if (position == null) {
      throw new IllegalArgumentException("position parameter for insertBefore() cannot be null");
    }
    return new BaseTypeImpl.ItemSeqCallImpl("fn", "insert-before", new Object[]{ target, position, inserts });
  }


  @Override
  public ServerExpression iriToUri(ServerExpression uriPart) {
    return new XsExprImpl.StringCallImpl("fn", "iri-to-uri", new Object[]{ uriPart });
  }


  @Override
  public ServerExpression lang(ServerExpression testlang, ServerExpression node) {
    return new XsExprImpl.BooleanCallImpl("fn", "lang", new Object[]{ testlang, node });
  }


  @Override
  public ServerExpression localName(ServerExpression arg) {
    return new XsExprImpl.StringCallImpl("fn", "local-name", new Object[]{ arg });
  }


  @Override
  public ServerExpression localNameFromQName(ServerExpression arg) {
    return new XsExprImpl.NCNameCallImpl("fn", "local-name-from-QName", new Object[]{ arg });
  }


  @Override
  public ServerExpression lowerCase(ServerExpression string) {
    return new XsExprImpl.StringCallImpl("fn", "lower-case", new Object[]{ string });
  }


  @Override
  public ServerExpression matches(ServerExpression input, String pattern) {
    return matches(input, (pattern == null) ? (ServerExpression) null : xs.string(pattern));
  }


  @Override
  public ServerExpression matches(ServerExpression input, ServerExpression pattern) {
    if (pattern == null) {
      throw new IllegalArgumentException("pattern parameter for matches() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("fn", "matches", new Object[]{ input, pattern });
  }


  @Override
  public ServerExpression matches(ServerExpression input, String pattern, String flags) {
    return matches(input, (pattern == null) ? (ServerExpression) null : xs.string(pattern), (flags == null) ? (ServerExpression) null : xs.string(flags));
  }


  @Override
  public ServerExpression matches(ServerExpression input, ServerExpression pattern, ServerExpression flags) {
    if (pattern == null) {
      throw new IllegalArgumentException("pattern parameter for matches() cannot be null");
    }
    if (flags == null) {
      throw new IllegalArgumentException("flags parameter for matches() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("fn", "matches", new Object[]{ input, pattern, flags });
  }


  @Override
  public ServerExpression max(ServerExpression arg) {
    return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "max", new Object[]{ arg });
  }


  @Override
  public ServerExpression max(ServerExpression arg, String collation) {
    return max(arg, (collation == null) ? (ServerExpression) null : xs.string(collation));
  }


  @Override
  public ServerExpression max(ServerExpression arg, ServerExpression collation) {
    if (collation == null) {
      throw new IllegalArgumentException("collation parameter for max() cannot be null");
    }
    return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "max", new Object[]{ arg, collation });
  }


  @Override
  public ServerExpression min(ServerExpression arg) {
    return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "min", new Object[]{ arg });
  }


  @Override
  public ServerExpression min(ServerExpression arg, String collation) {
    return min(arg, (collation == null) ? (ServerExpression) null : xs.string(collation));
  }


  @Override
  public ServerExpression min(ServerExpression arg, ServerExpression collation) {
    if (collation == null) {
      throw new IllegalArgumentException("collation parameter for min() cannot be null");
    }
    return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "min", new Object[]{ arg, collation });
  }


  @Override
  public ServerExpression minutesFromDateTime(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("fn", "minutes-from-dateTime", new Object[]{ arg });
  }


  @Override
  public ServerExpression minutesFromDuration(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("fn", "minutes-from-duration", new Object[]{ arg });
  }


  @Override
  public ServerExpression minutesFromTime(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("fn", "minutes-from-time", new Object[]{ arg });
  }


  @Override
  public ServerExpression monthFromDate(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("fn", "month-from-date", new Object[]{ arg });
  }


  @Override
  public ServerExpression monthFromDateTime(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("fn", "month-from-dateTime", new Object[]{ arg });
  }


  @Override
  public ServerExpression monthsFromDuration(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("fn", "months-from-duration", new Object[]{ arg });
  }


  @Override
  public ServerExpression name(ServerExpression arg) {
    return new XsExprImpl.StringCallImpl("fn", "name", new Object[]{ arg });
  }


  @Override
  public ServerExpression namespaceUri(ServerExpression arg) {
    return new XsExprImpl.AnyURICallImpl("fn", "namespace-uri", new Object[]{ arg });
  }


  @Override
  public ServerExpression namespaceUriForPrefix(ServerExpression prefix, ServerExpression element) {
    if (element == null) {
      throw new IllegalArgumentException("element parameter for namespaceUriForPrefix() cannot be null");
    }
    return new XsExprImpl.AnyURICallImpl("fn", "namespace-uri-for-prefix", new Object[]{ prefix, element });
  }


  @Override
  public ServerExpression namespaceUriFromQName(ServerExpression arg) {
    return new XsExprImpl.AnyURICallImpl("fn", "namespace-uri-from-QName", new Object[]{ arg });
  }


  @Override
  public ServerExpression nilled(ServerExpression arg) {
    return new XsExprImpl.BooleanCallImpl("fn", "nilled", new Object[]{ arg });
  }


  @Override
  public ServerExpression nodeName(ServerExpression arg) {
    return new XsExprImpl.QNameCallImpl("fn", "node-name", new Object[]{ arg });
  }


  @Override
  public ServerExpression normalizeSpace(ServerExpression input) {
    return new XsExprImpl.StringCallImpl("fn", "normalize-space", new Object[]{ input });
  }


  @Override
  public ServerExpression normalizeUnicode(ServerExpression arg) {
    return new XsExprImpl.StringCallImpl("fn", "normalize-unicode", new Object[]{ arg });
  }


  @Override
  public ServerExpression normalizeUnicode(ServerExpression arg, String normalizationForm) {
    return normalizeUnicode(arg, (normalizationForm == null) ? (ServerExpression) null : xs.string(normalizationForm));
  }


  @Override
  public ServerExpression normalizeUnicode(ServerExpression arg, ServerExpression normalizationForm) {
    if (normalizationForm == null) {
      throw new IllegalArgumentException("normalizationForm parameter for normalizeUnicode() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "normalize-unicode", new Object[]{ arg, normalizationForm });
  }


  @Override
  public ServerExpression not(ServerExpression arg) {
    return new XsExprImpl.BooleanCallImpl("fn", "not", new Object[]{ arg });
  }


  @Override
  public ServerExpression number(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("fn", "number", new Object[]{ arg });
  }


  @Override
  public ServerExpression prefixFromQName(ServerExpression arg) {
    return new XsExprImpl.NCNameCallImpl("fn", "prefix-from-QName", new Object[]{ arg });
  }


  @Override
  public ServerExpression QName(ServerExpression paramURI, String paramQName) {
    return QName(paramURI, (paramQName == null) ? (ServerExpression) null : xs.string(paramQName));
  }


  @Override
  public ServerExpression QName(ServerExpression paramURI, ServerExpression paramQName) {
    if (paramQName == null) {
      throw new IllegalArgumentException("paramQName parameter for QName() cannot be null");
    }
    return new XsExprImpl.QNameCallImpl("fn", "QName", new Object[]{ paramURI, paramQName });
  }


  @Override
  public ServerExpression remove(ServerExpression target, long position) {
    return remove(target, xs.integer(position));
  }


  @Override
  public ServerExpression remove(ServerExpression target, ServerExpression position) {
    if (position == null) {
      throw new IllegalArgumentException("position parameter for remove() cannot be null");
    }
    return new BaseTypeImpl.ItemSeqCallImpl("fn", "remove", new Object[]{ target, position });
  }


  @Override
  public ServerExpression replace(ServerExpression input, String pattern, String replacement) {
    return replace(input, (pattern == null) ? (ServerExpression) null : xs.string(pattern), (replacement == null) ? (ServerExpression) null : xs.string(replacement));
  }


  @Override
  public ServerExpression replace(ServerExpression input, ServerExpression pattern, ServerExpression replacement) {
    if (pattern == null) {
      throw new IllegalArgumentException("pattern parameter for replace() cannot be null");
    }
    if (replacement == null) {
      throw new IllegalArgumentException("replacement parameter for replace() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "replace", new Object[]{ input, pattern, replacement });
  }


  @Override
  public ServerExpression replace(ServerExpression input, String pattern, String replacement, String flags) {
    return replace(input, (pattern == null) ? (ServerExpression) null : xs.string(pattern), (replacement == null) ? (ServerExpression) null : xs.string(replacement), (flags == null) ? (ServerExpression) null : xs.string(flags));
  }


  @Override
  public ServerExpression replace(ServerExpression input, ServerExpression pattern, ServerExpression replacement, ServerExpression flags) {
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
  public ServerExpression resolveQName(ServerExpression qname, ServerExpression element) {
    if (element == null) {
      throw new IllegalArgumentException("element parameter for resolveQName() cannot be null");
    }
    return new XsExprImpl.QNameCallImpl("fn", "resolve-QName", new Object[]{ qname, element });
  }


  @Override
  public ServerExpression resolveUri(ServerExpression relative, String base) {
    return resolveUri(relative, (base == null) ? (ServerExpression) null : xs.string(base));
  }


  @Override
  public ServerExpression resolveUri(ServerExpression relative, ServerExpression base) {
    if (base == null) {
      throw new IllegalArgumentException("base parameter for resolveUri() cannot be null");
    }
    return new XsExprImpl.AnyURICallImpl("fn", "resolve-uri", new Object[]{ relative, base });
  }


  @Override
  public ServerExpression reverse(ServerExpression target) {
    return new BaseTypeImpl.ItemSeqCallImpl("fn", "reverse", new Object[]{ target });
  }


  @Override
  public ServerExpression root(ServerExpression arg) {
    return new BaseTypeImpl.NodeCallImpl("fn", "root", new Object[]{ arg });
  }


  @Override
  public ServerExpression round(ServerExpression arg) {
    return new XsExprImpl.NumericCallImpl("fn", "round", new Object[]{ arg });
  }


  @Override
  public ServerExpression roundHalfToEven(ServerExpression arg) {
    return new XsExprImpl.NumericCallImpl("fn", "round-half-to-even", new Object[]{ arg });
  }


  @Override
  public ServerExpression roundHalfToEven(ServerExpression arg, long precision) {
    return roundHalfToEven(arg, xs.integer(precision));
  }


  @Override
  public ServerExpression roundHalfToEven(ServerExpression arg, ServerExpression precision) {
    if (precision == null) {
      throw new IllegalArgumentException("precision parameter for roundHalfToEven() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("fn", "round-half-to-even", new Object[]{ arg, precision });
  }


  @Override
  public ServerExpression secondsFromDateTime(ServerExpression arg) {
    return new XsExprImpl.DecimalCallImpl("fn", "seconds-from-dateTime", new Object[]{ arg });
  }


  @Override
  public ServerExpression secondsFromDuration(ServerExpression arg) {
    return new XsExprImpl.DecimalCallImpl("fn", "seconds-from-duration", new Object[]{ arg });
  }


  @Override
  public ServerExpression secondsFromTime(ServerExpression arg) {
    return new XsExprImpl.DecimalCallImpl("fn", "seconds-from-time", new Object[]{ arg });
  }


  @Override
  public ServerExpression startsWith(ServerExpression parameter1, String parameter2) {
    return startsWith(parameter1, (parameter2 == null) ? (ServerExpression) null : xs.string(parameter2));
  }


  @Override
  public ServerExpression startsWith(ServerExpression parameter1, ServerExpression parameter2) {
    return new XsExprImpl.BooleanCallImpl("fn", "starts-with", new Object[]{ parameter1, parameter2 });
  }


  @Override
  public ServerExpression startsWith(ServerExpression parameter1, String parameter2, String collation) {
    return startsWith(parameter1, (parameter2 == null) ? (ServerExpression) null : xs.string(parameter2), (collation == null) ? (ServerExpression) null : xs.string(collation));
  }


  @Override
  public ServerExpression startsWith(ServerExpression parameter1, ServerExpression parameter2, ServerExpression collation) {
    if (collation == null) {
      throw new IllegalArgumentException("collation parameter for startsWith() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("fn", "starts-with", new Object[]{ parameter1, parameter2, collation });
  }


  @Override
  public ServerExpression string(ServerExpression arg) {
    return new XsExprImpl.StringCallImpl("fn", "string", new Object[]{ arg });
  }


  @Override
  public ServerExpression stringJoin(ServerExpression parameter1, String parameter2) {
    return stringJoin(parameter1, (parameter2 == null) ? (ServerExpression) null : xs.string(parameter2));
  }


  @Override
  public ServerExpression stringJoin(ServerExpression parameter1, ServerExpression parameter2) {
    if (parameter2 == null) {
      throw new IllegalArgumentException("parameter2 parameter for stringJoin() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "string-join", new Object[]{ parameter1, parameter2 });
  }


  @Override
  public ServerExpression stringLength(ServerExpression sourceString) {
    return new XsExprImpl.IntegerCallImpl("fn", "string-length", new Object[]{ sourceString });
  }


  @Override
  public ServerExpression stringToCodepoints(ServerExpression arg) {
    return new XsExprImpl.IntegerSeqCallImpl("fn", "string-to-codepoints", new Object[]{ arg });
  }


  @Override
  public ServerExpression subsequence(ServerExpression sourceSeq, double startingLoc) {
    return subsequence(sourceSeq, xs.doubleVal(startingLoc));
  }


  @Override
  public ServerExpression subsequence(ServerExpression sourceSeq, ServerExpression startingLoc) {
    if (startingLoc == null) {
      throw new IllegalArgumentException("startingLoc parameter for subsequence() cannot be null");
    }
    return new BaseTypeImpl.ItemSeqCallImpl("fn", "subsequence", new Object[]{ sourceSeq, startingLoc });
  }


  @Override
  public ServerExpression subsequence(ServerExpression sourceSeq, double startingLoc, double length) {
    return subsequence(sourceSeq, xs.doubleVal(startingLoc), xs.doubleVal(length));
  }


  @Override
  public ServerExpression subsequence(ServerExpression sourceSeq, ServerExpression startingLoc, ServerExpression length) {
    if (startingLoc == null) {
      throw new IllegalArgumentException("startingLoc parameter for subsequence() cannot be null");
    }
    if (length == null) {
      throw new IllegalArgumentException("length parameter for subsequence() cannot be null");
    }
    return new BaseTypeImpl.ItemSeqCallImpl("fn", "subsequence", new Object[]{ sourceSeq, startingLoc, length });
  }


  @Override
  public ServerExpression substring(ServerExpression sourceString, double startingLoc) {
    return substring(sourceString, xs.doubleVal(startingLoc));
  }


  @Override
  public ServerExpression substring(ServerExpression sourceString, ServerExpression startingLoc) {
    if (startingLoc == null) {
      throw new IllegalArgumentException("startingLoc parameter for substring() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "substring", new Object[]{ sourceString, startingLoc });
  }


  @Override
  public ServerExpression substring(ServerExpression sourceString, double startingLoc, double length) {
    return substring(sourceString, xs.doubleVal(startingLoc), xs.doubleVal(length));
  }


  @Override
  public ServerExpression substring(ServerExpression sourceString, ServerExpression startingLoc, ServerExpression length) {
    if (startingLoc == null) {
      throw new IllegalArgumentException("startingLoc parameter for substring() cannot be null");
    }
    if (length == null) {
      throw new IllegalArgumentException("length parameter for substring() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "substring", new Object[]{ sourceString, startingLoc, length });
  }


  @Override
  public ServerExpression substringAfter(ServerExpression input, String after) {
    return substringAfter(input, (after == null) ? (ServerExpression) null : xs.string(after));
  }


  @Override
  public ServerExpression substringAfter(ServerExpression input, ServerExpression after) {
    return new XsExprImpl.StringCallImpl("fn", "substring-after", new Object[]{ input, after });
  }


  @Override
  public ServerExpression substringAfter(ServerExpression input, String after, String collation) {
    return substringAfter(input, (after == null) ? (ServerExpression) null : xs.string(after), (collation == null) ? (ServerExpression) null : xs.string(collation));
  }


  @Override
  public ServerExpression substringAfter(ServerExpression input, ServerExpression after, ServerExpression collation) {
    if (collation == null) {
      throw new IllegalArgumentException("collation parameter for substringAfter() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "substring-after", new Object[]{ input, after, collation });
  }


  @Override
  public ServerExpression substringBefore(ServerExpression input, String before) {
    return substringBefore(input, (before == null) ? (ServerExpression) null : xs.string(before));
  }


  @Override
  public ServerExpression substringBefore(ServerExpression input, ServerExpression before) {
    return new XsExprImpl.StringCallImpl("fn", "substring-before", new Object[]{ input, before });
  }


  @Override
  public ServerExpression substringBefore(ServerExpression input, String before, String collation) {
    return substringBefore(input, (before == null) ? (ServerExpression) null : xs.string(before), (collation == null) ? (ServerExpression) null : xs.string(collation));
  }


  @Override
  public ServerExpression substringBefore(ServerExpression input, ServerExpression before, ServerExpression collation) {
    if (collation == null) {
      throw new IllegalArgumentException("collation parameter for substringBefore() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "substring-before", new Object[]{ input, before, collation });
  }


  @Override
  public ServerExpression sum(ServerExpression arg) {
    return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "sum", new Object[]{ arg });
  }


  @Override
  public ServerExpression sum(ServerExpression arg, String zero) {
    return sum(arg, (zero == null) ? (ServerExpression) null : xs.string(zero));
  }


  @Override
  public ServerExpression sum(ServerExpression arg, ServerExpression zero) {
    return new XsExprImpl.AnyAtomicTypeCallImpl("fn", "sum", new Object[]{ arg, zero });
  }


  @Override
  public ServerExpression tail(ServerExpression seq) {
    return new BaseTypeImpl.ItemSeqCallImpl("fn", "tail", new Object[]{ seq });
  }


  @Override
  public ServerExpression timezoneFromDate(ServerExpression arg) {
    return new XsExprImpl.DayTimeDurationCallImpl("fn", "timezone-from-date", new Object[]{ arg });
  }


  @Override
  public ServerExpression timezoneFromDateTime(ServerExpression arg) {
    return new XsExprImpl.DayTimeDurationCallImpl("fn", "timezone-from-dateTime", new Object[]{ arg });
  }


  @Override
  public ServerExpression timezoneFromTime(ServerExpression arg) {
    return new XsExprImpl.DayTimeDurationCallImpl("fn", "timezone-from-time", new Object[]{ arg });
  }


  @Override
  public ServerExpression tokenize(ServerExpression input, String pattern) {
    return tokenize(input, (pattern == null) ? (ServerExpression) null : xs.string(pattern));
  }


  @Override
  public ServerExpression tokenize(ServerExpression input, ServerExpression pattern) {
    if (pattern == null) {
      throw new IllegalArgumentException("pattern parameter for tokenize() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("fn", "tokenize", new Object[]{ input, pattern });
  }


  @Override
  public ServerExpression tokenize(ServerExpression input, String pattern, String flags) {
    return tokenize(input, (pattern == null) ? (ServerExpression) null : xs.string(pattern), (flags == null) ? (ServerExpression) null : xs.string(flags));
  }


  @Override
  public ServerExpression tokenize(ServerExpression input, ServerExpression pattern, ServerExpression flags) {
    if (pattern == null) {
      throw new IllegalArgumentException("pattern parameter for tokenize() cannot be null");
    }
    if (flags == null) {
      throw new IllegalArgumentException("flags parameter for tokenize() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("fn", "tokenize", new Object[]{ input, pattern, flags });
  }


  @Override
  public ServerExpression translate(ServerExpression src, String mapString, String transString) {
    return translate(src, (mapString == null) ? (ServerExpression) null : xs.string(mapString), (transString == null) ? (ServerExpression) null : xs.string(transString));
  }


  @Override
  public ServerExpression translate(ServerExpression src, ServerExpression mapString, ServerExpression transString) {
    if (mapString == null) {
      throw new IllegalArgumentException("mapString parameter for translate() cannot be null");
    }
    if (transString == null) {
      throw new IllegalArgumentException("transString parameter for translate() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("fn", "translate", new Object[]{ src, mapString, transString });
  }


  @Override
  public ServerExpression trueExpr() {
    return new XsExprImpl.BooleanCallImpl("fn", "true", new Object[]{  });
  }


  @Override
  public ServerExpression unordered(ServerExpression sourceSeq) {
    return new BaseTypeImpl.ItemSeqCallImpl("fn", "unordered", new Object[]{ sourceSeq });
  }


  @Override
  public ServerExpression upperCase(ServerExpression string) {
    return new XsExprImpl.StringCallImpl("fn", "upper-case", new Object[]{ string });
  }


  @Override
  public ServerExpression yearFromDate(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("fn", "year-from-date", new Object[]{ arg });
  }


  @Override
  public ServerExpression yearFromDateTime(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("fn", "year-from-dateTime", new Object[]{ arg });
  }


  @Override
  public ServerExpression yearsFromDuration(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("fn", "years-from-duration", new Object[]{ arg });
  }

  }
