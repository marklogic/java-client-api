/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.impl;

import com.marklogic.client.type.XsAnyAtomicTypeSeqVal;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.type.XsBooleanVal;
import com.marklogic.client.type.XsDecimalVal;
import com.marklogic.client.type.XsIntegerVal;
import com.marklogic.client.type.XsIntVal;
import com.marklogic.client.type.XsStringVal;
import com.marklogic.client.type.XsUnsignedIntVal;
import com.marklogic.client.type.XsUnsignedLongVal;

import com.marklogic.client.type.ServerExpression;

import com.marklogic.client.expression.SqlExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class SqlExprImpl implements SqlExpr {

  final static XsExprImpl xs = XsExprImpl.xs;

  final static SqlExprImpl sql = new SqlExprImpl();

  SqlExprImpl() {
  }


  @Override
  public ServerExpression bitLength(ServerExpression str) {
    return new XsExprImpl.IntegerCallImpl("sql", "bit-length", new Object[]{ str });
  }


  @Override
  public ServerExpression bucket(ServerExpression bucketEdgesParam, String srchParam) {
    return bucket(bucketEdgesParam, (srchParam == null) ? (ServerExpression) null : xs.string(srchParam));
  }


  @Override
  public ServerExpression bucket(ServerExpression bucketEdgesParam, ServerExpression srchParam) {
    if (srchParam == null) {
      throw new IllegalArgumentException("srchParam parameter for bucket() cannot be null");
    }
    return new XsExprImpl.UnsignedLongCallImpl("sql", "bucket", new Object[]{ bucketEdgesParam, srchParam });
  }


  @Override
  public ServerExpression bucket(ServerExpression bucketEdgesParam, String srchParam, String collationLiteral) {
    return bucket(bucketEdgesParam, (srchParam == null) ? (ServerExpression) null : xs.string(srchParam), (collationLiteral == null) ? (ServerExpression) null : xs.string(collationLiteral));
  }


  @Override
  public ServerExpression bucket(ServerExpression bucketEdgesParam, ServerExpression srchParam, ServerExpression collationLiteral) {
    if (srchParam == null) {
      throw new IllegalArgumentException("srchParam parameter for bucket() cannot be null");
    }
    if (collationLiteral == null) {
      throw new IllegalArgumentException("collationLiteral parameter for bucket() cannot be null");
    }
    return new XsExprImpl.UnsignedLongCallImpl("sql", "bucket", new Object[]{ bucketEdgesParam, srchParam, collationLiteral });
  }


  @Override
  public ServerExpression collatedString(ServerExpression string, String collationURI) {
    return collatedString(string, (collationURI == null) ? (ServerExpression) null : xs.string(collationURI));
  }


  @Override
  public ServerExpression collatedString(ServerExpression string, ServerExpression collationURI) {
    if (string == null) {
      throw new IllegalArgumentException("string parameter for collatedString() cannot be null");
    }
    if (collationURI == null) {
      throw new IllegalArgumentException("collationURI parameter for collatedString() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("sql", "collated-string", new Object[]{ string, collationURI });
  }


  @Override
  public ServerExpression dateadd(ServerExpression datepart, int number, ServerExpression date) {
    return dateadd(datepart, xs.intVal(number), date);
  }


  @Override
  public ServerExpression dateadd(ServerExpression datepart, ServerExpression number, ServerExpression date) {
    return new BaseTypeImpl.ItemCallImpl("sql", "dateadd", new Object[]{ datepart, number, date });
  }


  @Override
  public ServerExpression datediff(ServerExpression datepart, ServerExpression startdate, ServerExpression enddate) {
    return new XsExprImpl.IntegerCallImpl("sql", "datediff", new Object[]{ datepart, startdate, enddate });
  }


  @Override
  public ServerExpression datepart(ServerExpression datepart, ServerExpression date) {
    if (datepart == null) {
      throw new IllegalArgumentException("datepart parameter for datepart() cannot be null");
    }
    if (date == null) {
      throw new IllegalArgumentException("date parameter for datepart() cannot be null");
    }
    return new XsExprImpl.IntegerCallImpl("sql", "datepart", new Object[]{ datepart, date });
  }


  @Override
  public ServerExpression day(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("sql", "day", new Object[]{ arg });
  }


  @Override
  public ServerExpression dayname(ServerExpression arg) {
    return new XsExprImpl.StringCallImpl("sql", "dayname", new Object[]{ arg });
  }


  @Override
  public ServerExpression glob(ServerExpression input, String pattern) {
    return glob(input, (pattern == null) ? (ServerExpression) null : xs.string(pattern));
  }


  @Override
  public ServerExpression glob(ServerExpression input, ServerExpression pattern) {
    if (pattern == null) {
      throw new IllegalArgumentException("pattern parameter for glob() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("sql", "glob", new Object[]{ input, pattern });
  }


  @Override
  public ServerExpression hours(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("sql", "hours", new Object[]{ arg });
  }


  @Override
  public ServerExpression ifnull(ServerExpression expr1, ServerExpression expr2) {
    return new XsExprImpl.AnyAtomicTypeCallImpl("sql", "ifnull", new Object[]{ expr1, expr2 });
  }


  @Override
  public ServerExpression insert(ServerExpression str, double start, double length, String str2) {
    return insert(str, xs.doubleVal(start), xs.doubleVal(length), (str2 == null) ? (ServerExpression) null : xs.string(str2));
  }


  @Override
  public ServerExpression insert(ServerExpression str, ServerExpression start, ServerExpression length, ServerExpression str2) {
    if (str == null) {
      throw new IllegalArgumentException("str parameter for insert() cannot be null");
    }
    if (start == null) {
      throw new IllegalArgumentException("start parameter for insert() cannot be null");
    }
    if (length == null) {
      throw new IllegalArgumentException("length parameter for insert() cannot be null");
    }
    if (str2 == null) {
      throw new IllegalArgumentException("str2 parameter for insert() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("sql", "insert", new Object[]{ str, start, length, str2 });
  }


  @Override
  public ServerExpression instr(ServerExpression str, String n) {
    return instr(str, (n == null) ? (ServerExpression) null : xs.string(n));
  }


  @Override
  public ServerExpression instr(ServerExpression str, ServerExpression n) {
    if (str == null) {
      throw new IllegalArgumentException("str parameter for instr() cannot be null");
    }
    if (n == null) {
      throw new IllegalArgumentException("n parameter for instr() cannot be null");
    }
    return new XsExprImpl.UnsignedIntCallImpl("sql", "instr", new Object[]{ str, n });
  }


  @Override
  public ServerExpression left(ServerExpression str, double n) {
    return left(str, xs.doubleVal(n));
  }


  @Override
  public ServerExpression left(ServerExpression str, ServerExpression n) {
    return new XsExprImpl.StringCallImpl("sql", "left", new Object[]{ str, n });
  }


  @Override
  public ServerExpression like(ServerExpression input, String pattern) {
    return like(input, (pattern == null) ? (ServerExpression) null : xs.string(pattern));
  }


  @Override
  public ServerExpression like(ServerExpression input, ServerExpression pattern) {
    if (pattern == null) {
      throw new IllegalArgumentException("pattern parameter for like() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("sql", "like", new Object[]{ input, pattern });
  }


  @Override
  public ServerExpression like(ServerExpression input, String pattern, String escape) {
    return like(input, (pattern == null) ? (ServerExpression) null : xs.string(pattern), (escape == null) ? (ServerExpression) null : xs.string(escape));
  }


  @Override
  public ServerExpression like(ServerExpression input, ServerExpression pattern, ServerExpression escape) {
    if (pattern == null) {
      throw new IllegalArgumentException("pattern parameter for like() cannot be null");
    }
    if (escape == null) {
      throw new IllegalArgumentException("escape parameter for like() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("sql", "like", new Object[]{ input, pattern, escape });
  }


  @Override
  public ServerExpression ltrim(ServerExpression str) {
    return new XsExprImpl.StringCallImpl("sql", "ltrim", new Object[]{ str });
  }


  @Override
  public ServerExpression minutes(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("sql", "minutes", new Object[]{ arg });
  }


  @Override
  public ServerExpression month(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("sql", "month", new Object[]{ arg });
  }


  @Override
  public ServerExpression monthname(ServerExpression arg) {
    return new XsExprImpl.StringCallImpl("sql", "monthname", new Object[]{ arg });
  }


  @Override
  public ServerExpression nullif(ServerExpression expr1, ServerExpression expr2) {
    return new XsExprImpl.AnyAtomicTypeCallImpl("sql", "nullif", new Object[]{ expr1, expr2 });
  }


  @Override
  public ServerExpression octetLength(ServerExpression x) {
    return new XsExprImpl.IntegerCallImpl("sql", "octet-length", new Object[]{ x });
  }


  @Override
  public ServerExpression quarter(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("sql", "quarter", new Object[]{ arg });
  }


  @Override
  public ServerExpression rand(ServerExpression n) {
    if (n == null) {
      throw new IllegalArgumentException("n parameter for rand() cannot be null");
    }
    return new XsExprImpl.UnsignedLongCallImpl("sql", "rand", new Object[]{ n });
  }


  @Override
  public ServerExpression repeat(ServerExpression str, double n) {
    return repeat(str, xs.doubleVal(n));
  }


  @Override
  public ServerExpression repeat(ServerExpression str, ServerExpression n) {
    if (n == null) {
      throw new IllegalArgumentException("n parameter for repeat() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("sql", "repeat", new Object[]{ str, n });
  }


  @Override
  public ServerExpression right(ServerExpression str, double n) {
    return right(str, xs.doubleVal(n));
  }


  @Override
  public ServerExpression right(ServerExpression str, ServerExpression n) {
    return new XsExprImpl.StringCallImpl("sql", "right", new Object[]{ str, n });
  }


  @Override
  public ServerExpression rowID(ServerExpression arg1) {
    return new RowIDCallImpl("sql", "rowID", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression rtrim(ServerExpression str) {
    return new XsExprImpl.StringCallImpl("sql", "rtrim", new Object[]{ str });
  }


  @Override
  public ServerExpression seconds(ServerExpression arg) {
    return new XsExprImpl.DecimalCallImpl("sql", "seconds", new Object[]{ arg });
  }


  @Override
  public ServerExpression sign(ServerExpression x) {
    return new XsExprImpl.NumericCallImpl("sql", "sign", new Object[]{ x });
  }


  @Override
  public ServerExpression soundex(ServerExpression arg) {
    return new XsExprImpl.StringCallImpl("sql", "soundex", new Object[]{ arg });
  }


  @Override
  public ServerExpression space(ServerExpression n) {
    return new XsExprImpl.StringCallImpl("sql", "space", new Object[]{ n });
  }


  @Override
  public ServerExpression strpos(ServerExpression target, String test) {
    return strpos(target, (test == null) ? (ServerExpression) null : xs.string(test));
  }


  @Override
  public ServerExpression strpos(ServerExpression target, ServerExpression test) {
    return new XsExprImpl.IntegerCallImpl("sql", "strpos", new Object[]{ target, test });
  }


  @Override
  public ServerExpression strpos(ServerExpression target, String test, String collation) {
    return strpos(target, (test == null) ? (ServerExpression) null : xs.string(test), (collation == null) ? (ServerExpression) null : xs.string(collation));
  }


  @Override
  public ServerExpression strpos(ServerExpression target, ServerExpression test, ServerExpression collation) {
    return new XsExprImpl.IntegerCallImpl("sql", "strpos", new Object[]{ target, test, collation });
  }


  @Override
  public ServerExpression timestampadd(ServerExpression dateTimeType, int value, ServerExpression timestamp) {
    return timestampadd(dateTimeType, xs.intVal(value), timestamp);
  }


  @Override
  public ServerExpression timestampadd(ServerExpression dateTimeType, ServerExpression value, ServerExpression timestamp) {
    return new BaseTypeImpl.ItemCallImpl("sql", "timestampadd", new Object[]{ dateTimeType, value, timestamp });
  }


  @Override
  public ServerExpression timestampdiff(ServerExpression dateTimeType, ServerExpression timestamp1, ServerExpression timestamp2) {
    return new XsExprImpl.IntegerCallImpl("sql", "timestampdiff", new Object[]{ dateTimeType, timestamp1, timestamp2 });
  }


  @Override
  public ServerExpression trim(ServerExpression str) {
    if (str == null) {
      throw new IllegalArgumentException("str parameter for trim() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("sql", "trim", new Object[]{ str });
  }


  @Override
  public ServerExpression week(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("sql", "week", new Object[]{ arg });
  }


  @Override
  public ServerExpression weekday(ServerExpression arg1) {
    return new XsExprImpl.IntegerCallImpl("sql", "weekday", new Object[]{ arg1 });
  }


  @Override
  public ServerExpression year(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("sql", "year", new Object[]{ arg });
  }


  @Override
  public ServerExpression yearday(ServerExpression arg) {
    return new XsExprImpl.IntegerCallImpl("sql", "yearday", new Object[]{ arg });
  }

  static class RowIDSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    RowIDSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class RowIDCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    RowIDCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  }
