/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.impl;

import com.marklogic.client.type.XsAnyAtomicTypeSeqVal;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.type.XsDoubleSeqVal;
import com.marklogic.client.type.XsDoubleVal;
import com.marklogic.client.type.XsIntegerVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;

import com.marklogic.client.type.ServerExpression;

import com.marklogic.client.expression.MathExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class MathExprImpl implements MathExpr {

  final static XsExprImpl xs = XsExprImpl.xs;

  final static MathExprImpl math = new MathExprImpl();

  MathExprImpl() {
  }


  @Override
  public ServerExpression acos(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for acos() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "acos", new Object[]{ x });
  }


  @Override
  public ServerExpression asin(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for asin() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "asin", new Object[]{ x });
  }


  @Override
  public ServerExpression atan(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for atan() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "atan", new Object[]{ x });
  }


  @Override
  public ServerExpression atan2(ServerExpression y, double x) {
    return atan2(y, xs.doubleVal(x));
  }


  @Override
  public ServerExpression atan2(ServerExpression y, ServerExpression x) {
    if (y == null) {
      throw new IllegalArgumentException("y parameter for atan2() cannot be null");
    }
    if (x == null) {
      throw new IllegalArgumentException("x parameter for atan2() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "atan2", new Object[]{ y, x });
  }


  @Override
  public ServerExpression ceil(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for ceil() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "ceil", new Object[]{ x });
  }


  @Override
  public ServerExpression correlation(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("math", "correlation", new Object[]{ arg });
  }


  @Override
  public ServerExpression cos(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for cos() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "cos", new Object[]{ x });
  }


  @Override
  public ServerExpression cosh(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for cosh() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "cosh", new Object[]{ x });
  }


  @Override
  public ServerExpression cot(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for cot() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "cot", new Object[]{ x });
  }


  @Override
  public ServerExpression covariance(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("math", "covariance", new Object[]{ arg });
  }


  @Override
  public ServerExpression covarianceP(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("math", "covariance-p", new Object[]{ arg });
  }


  @Override
  public ServerExpression degrees(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for degrees() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "degrees", new Object[]{ x });
  }


  @Override
  public ServerExpression exp(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for exp() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "exp", new Object[]{ x });
  }


  @Override
  public ServerExpression fabs(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for fabs() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "fabs", new Object[]{ x });
  }


  @Override
  public ServerExpression floor(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for floor() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "floor", new Object[]{ x });
  }


  @Override
  public ServerExpression fmod(ServerExpression x, double y) {
    return fmod(x, xs.doubleVal(y));
  }


  @Override
  public ServerExpression fmod(ServerExpression x, ServerExpression y) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for fmod() cannot be null");
    }
    if (y == null) {
      throw new IllegalArgumentException("y parameter for fmod() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "fmod", new Object[]{ x, y });
  }


  @Override
  public ServerExpression frexp(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for frexp() cannot be null");
    }
    return new BaseTypeImpl.ItemSeqCallImpl("math", "frexp", new Object[]{ x });
  }


  @Override
  public ServerExpression ldexp(ServerExpression y, long i) {
    return ldexp(y, xs.integer(i));
  }


  @Override
  public ServerExpression ldexp(ServerExpression y, ServerExpression i) {
    if (y == null) {
      throw new IllegalArgumentException("y parameter for ldexp() cannot be null");
    }
    if (i == null) {
      throw new IllegalArgumentException("i parameter for ldexp() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "ldexp", new Object[]{ y, i });
  }


  @Override
  public ServerExpression linearModel(ServerExpression arg) {
    return new LinearModelCallImpl("math", "linear-model", new Object[]{ arg });
  }


  @Override
  public ServerExpression linearModelCoeff(ServerExpression linearModel) {
    if (linearModel == null) {
      throw new IllegalArgumentException("linearModel parameter for linearModelCoeff() cannot be null");
    }
    return new XsExprImpl.DoubleSeqCallImpl("math", "linear-model-coeff", new Object[]{ linearModel });
  }


  @Override
  public ServerExpression linearModelIntercept(ServerExpression linearModel) {
    if (linearModel == null) {
      throw new IllegalArgumentException("linearModel parameter for linearModelIntercept() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "linear-model-intercept", new Object[]{ linearModel });
  }


  @Override
  public ServerExpression linearModelRsquared(ServerExpression linearModel) {
    if (linearModel == null) {
      throw new IllegalArgumentException("linearModel parameter for linearModelRsquared() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "linear-model-rsquared", new Object[]{ linearModel });
  }


  @Override
  public ServerExpression log(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for log() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "log", new Object[]{ x });
  }


  @Override
  public ServerExpression log10(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for log10() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "log10", new Object[]{ x });
  }


  @Override
  public ServerExpression median(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("math", "median", new Object[]{ arg });
  }


  @Override
  public ServerExpression mode(ServerExpression arg) {
    return new XsExprImpl.AnyAtomicTypeSeqCallImpl("math", "mode", new Object[]{ arg });
  }


  @Override
  public ServerExpression mode(ServerExpression arg, String options) {
    return mode(arg, (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression mode(ServerExpression arg, ServerExpression options) {
    return new XsExprImpl.AnyAtomicTypeSeqCallImpl("math", "mode", new Object[]{ arg, options });
  }


  @Override
  public ServerExpression modf(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for modf() cannot be null");
    }
    return new XsExprImpl.DoubleSeqCallImpl("math", "modf", new Object[]{ x });
  }


  @Override
  public ServerExpression percentRank(ServerExpression arg, String value) {
    return percentRank(arg, (value == null) ? (ServerExpression) null : xs.string(value));
  }


  @Override
  public ServerExpression percentRank(ServerExpression arg, ServerExpression value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for percentRank() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "percent-rank", new Object[]{ arg, value });
  }


  @Override
  public ServerExpression percentRank(ServerExpression arg, String value, String options) {
    return percentRank(arg, (value == null) ? (ServerExpression) null : xs.string(value), (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression percentRank(ServerExpression arg, ServerExpression value, ServerExpression options) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for percentRank() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "percent-rank", new Object[]{ arg, value, options });
  }


  @Override
  public ServerExpression percentile(ServerExpression arg, double p) {
    return percentile(arg, xs.doubleVal(p));
  }


  @Override
  public ServerExpression percentile(ServerExpression arg, ServerExpression p) {
    return new XsExprImpl.DoubleSeqCallImpl("math", "percentile", new Object[]{ arg, p });
  }


  @Override
  public ServerExpression pi() {
    return new XsExprImpl.DoubleCallImpl("math", "pi", new Object[]{  });
  }


  @Override
  public ServerExpression pow(ServerExpression x, double y) {
    return pow(x, xs.doubleVal(y));
  }


  @Override
  public ServerExpression pow(ServerExpression x, ServerExpression y) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for pow() cannot be null");
    }
    if (y == null) {
      throw new IllegalArgumentException("y parameter for pow() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "pow", new Object[]{ x, y });
  }


  @Override
  public ServerExpression radians(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for radians() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "radians", new Object[]{ x });
  }


  @Override
  public ServerExpression rank(ServerExpression arg1, String arg2) {
    return rank(arg1, (arg2 == null) ? (ServerExpression) null : xs.string(arg2));
  }


  @Override
  public ServerExpression rank(ServerExpression arg1, ServerExpression arg2) {
    if (arg2 == null) {
      throw new IllegalArgumentException("arg2 parameter for rank() cannot be null");
    }
    return new XsExprImpl.IntegerCallImpl("math", "rank", new Object[]{ arg1, arg2 });
  }


  @Override
  public ServerExpression rank(ServerExpression arg1, String arg2, String options) {
    return rank(arg1, (arg2 == null) ? (ServerExpression) null : xs.string(arg2), (options == null) ? (ServerExpression) null : xs.string(options));
  }


  @Override
  public ServerExpression rank(ServerExpression arg1, ServerExpression arg2, ServerExpression options) {
    if (arg2 == null) {
      throw new IllegalArgumentException("arg2 parameter for rank() cannot be null");
    }
    return new XsExprImpl.IntegerCallImpl("math", "rank", new Object[]{ arg1, arg2, options });
  }


  @Override
  public ServerExpression sin(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for sin() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "sin", new Object[]{ x });
  }


  @Override
  public ServerExpression sinh(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for sinh() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "sinh", new Object[]{ x });
  }


  @Override
  public ServerExpression sqrt(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for sqrt() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "sqrt", new Object[]{ x });
  }


  @Override
  public ServerExpression stddev(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("math", "stddev", new Object[]{ arg });
  }


  @Override
  public ServerExpression stddevP(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("math", "stddev-p", new Object[]{ arg });
  }


  @Override
  public ServerExpression tan(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for tan() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "tan", new Object[]{ x });
  }


  @Override
  public ServerExpression tanh(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for tanh() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "tanh", new Object[]{ x });
  }


  @Override
  public ServerExpression trunc(ServerExpression arg) {
    return new XsExprImpl.NumericCallImpl("math", "trunc", new Object[]{ arg });
  }


  @Override
  public ServerExpression trunc(ServerExpression arg, long n) {
    return trunc(arg, xs.integer(n));
  }


  @Override
  public ServerExpression trunc(ServerExpression arg, ServerExpression n) {
    if (n == null) {
      throw new IllegalArgumentException("n parameter for trunc() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("math", "trunc", new Object[]{ arg, n });
  }


  @Override
  public ServerExpression variance(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("math", "variance", new Object[]{ arg });
  }


  @Override
  public ServerExpression varianceP(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("math", "variance-p", new Object[]{ arg });
  }

  static class LinearModelSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    LinearModelSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class LinearModelCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    LinearModelCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  }
