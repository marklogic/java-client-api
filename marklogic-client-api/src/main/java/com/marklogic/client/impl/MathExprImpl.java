/*
 * Copyright 2016-2019 MarkLogic Corporation
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

import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.JsonArraySeqExpr;
import com.marklogic.client.type.XsAnyAtomicTypeExpr;
import com.marklogic.client.type.XsAnyAtomicTypeSeqExpr;
import com.marklogic.client.type.XsDoubleExpr;
import com.marklogic.client.type.XsDoubleSeqExpr;
import com.marklogic.client.type.XsIntegerExpr;
import com.marklogic.client.type.XsNumericExpr;
import com.marklogic.client.type.XsStringExpr;
import com.marklogic.client.type.XsStringSeqExpr;

import com.marklogic.client.type.ServerExpression;
import com.marklogic.client.type.MathLinearModelExpr;
import com.marklogic.client.type.MathLinearModelSeqExpr;

import com.marklogic.client.expression.MathExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class MathExprImpl implements MathExpr {

  final static XsExprImpl xs = XsExprImpl.xs;

  final static MathExprImpl math = new MathExprImpl();

  MathExprImpl() {
  }

    
  @Override
  public XsDoubleExpr acos(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for acos() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "acos", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr asin(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for asin() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "asin", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr atan(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for atan() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "atan", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr atan2(ServerExpression y, double x) {
    return atan2(y, xs.doubleVal(x));
  }

  
  @Override
  public XsDoubleExpr atan2(ServerExpression y, ServerExpression x) {
    if (y == null) {
      throw new IllegalArgumentException("y parameter for atan2() cannot be null");
    }
    if (x == null) {
      throw new IllegalArgumentException("x parameter for atan2() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "atan2", new Object[]{ y, x });
  }

  
  @Override
  public XsDoubleExpr ceil(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for ceil() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "ceil", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr correlation(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("math", "correlation", new Object[]{ arg });
  }

  
  @Override
  public XsDoubleExpr cos(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for cos() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "cos", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr cosh(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for cosh() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "cosh", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr cot(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for cot() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "cot", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr covariance(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("math", "covariance", new Object[]{ arg });
  }

  
  @Override
  public XsDoubleExpr covarianceP(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("math", "covariance-p", new Object[]{ arg });
  }

  
  @Override
  public XsDoubleExpr degrees(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for degrees() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "degrees", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr exp(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for exp() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "exp", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr fabs(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for fabs() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "fabs", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr floor(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for floor() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "floor", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr fmod(ServerExpression x, double y) {
    return fmod(x, xs.doubleVal(y));
  }

  
  @Override
  public XsDoubleExpr fmod(ServerExpression x, ServerExpression y) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for fmod() cannot be null");
    }
    if (y == null) {
      throw new IllegalArgumentException("y parameter for fmod() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "fmod", new Object[]{ x, y });
  }

  
  @Override
  public ItemSeqExpr frexp(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for frexp() cannot be null");
    }
    return new BaseTypeImpl.ItemSeqCallImpl("math", "frexp", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr ldexp(ServerExpression y, long i) {
    return ldexp(y, xs.integer(i));
  }

  
  @Override
  public XsDoubleExpr ldexp(ServerExpression y, ServerExpression i) {
    if (y == null) {
      throw new IllegalArgumentException("y parameter for ldexp() cannot be null");
    }
    if (i == null) {
      throw new IllegalArgumentException("i parameter for ldexp() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "ldexp", new Object[]{ y, i });
  }

  
  @Override
  public MathLinearModelExpr linearModel(ServerExpression arg) {
    return new LinearModelCallImpl("math", "linear-model", new Object[]{ arg });
  }

  
  @Override
  public XsDoubleSeqExpr linearModelCoeff(ServerExpression linearModel) {
    if (linearModel == null) {
      throw new IllegalArgumentException("linearModel parameter for linearModelCoeff() cannot be null");
    }
    return new XsExprImpl.DoubleSeqCallImpl("math", "linear-model-coeff", new Object[]{ linearModel });
  }

  
  @Override
  public XsDoubleExpr linearModelIntercept(ServerExpression linearModel) {
    if (linearModel == null) {
      throw new IllegalArgumentException("linearModel parameter for linearModelIntercept() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "linear-model-intercept", new Object[]{ linearModel });
  }

  
  @Override
  public XsDoubleExpr linearModelRsquared(ServerExpression linearModel) {
    if (linearModel == null) {
      throw new IllegalArgumentException("linearModel parameter for linearModelRsquared() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "linear-model-rsquared", new Object[]{ linearModel });
  }

  
  @Override
  public XsDoubleExpr log(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for log() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "log", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr log10(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for log10() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "log10", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr median(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("math", "median", new Object[]{ arg });
  }

  
  @Override
  public XsAnyAtomicTypeSeqExpr mode(ServerExpression arg) {
    return new XsExprImpl.AnyAtomicTypeSeqCallImpl("math", "mode", new Object[]{ arg });
  }

  
  @Override
  public XsAnyAtomicTypeSeqExpr mode(ServerExpression arg, String options) {
    return mode(arg, (options == null) ? (XsStringExpr) null : xs.string(options));
  }

  
  @Override
  public XsAnyAtomicTypeSeqExpr mode(ServerExpression arg, ServerExpression options) {
    return new XsExprImpl.AnyAtomicTypeSeqCallImpl("math", "mode", new Object[]{ arg, options });
  }

  
  @Override
  public XsDoubleSeqExpr modf(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for modf() cannot be null");
    }
    return new XsExprImpl.DoubleSeqCallImpl("math", "modf", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr percentRank(ServerExpression arg, String value) {
    return percentRank(arg, (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value));
  }

  
  @Override
  public XsDoubleExpr percentRank(ServerExpression arg, ServerExpression value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for percentRank() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "percent-rank", new Object[]{ arg, value });
  }

  
  @Override
  public XsDoubleExpr percentRank(ServerExpression arg, String value, String options) {
    return percentRank(arg, (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value), (options == null) ? (XsStringExpr) null : xs.string(options));
  }

  
  @Override
  public XsDoubleExpr percentRank(ServerExpression arg, ServerExpression value, ServerExpression options) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for percentRank() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "percent-rank", new Object[]{ arg, value, options });
  }

  
  @Override
  public XsDoubleSeqExpr percentile(ServerExpression arg, double p) {
    return percentile(arg, xs.doubleVal(p));
  }

  
  @Override
  public XsDoubleSeqExpr percentile(ServerExpression arg, ServerExpression p) {
    return new XsExprImpl.DoubleSeqCallImpl("math", "percentile", new Object[]{ arg, p });
  }

  
  @Override
  public XsDoubleExpr pi() {
    return new XsExprImpl.DoubleCallImpl("math", "pi", new Object[]{  });
  }

  
  @Override
  public XsDoubleExpr pow(ServerExpression x, double y) {
    return pow(x, xs.doubleVal(y));
  }

  
  @Override
  public XsDoubleExpr pow(ServerExpression x, ServerExpression y) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for pow() cannot be null");
    }
    if (y == null) {
      throw new IllegalArgumentException("y parameter for pow() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "pow", new Object[]{ x, y });
  }

  
  @Override
  public XsDoubleExpr radians(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for radians() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "radians", new Object[]{ x });
  }

  
  @Override
  public XsIntegerExpr rank(ServerExpression arg1, String arg2) {
    return rank(arg1, (arg2 == null) ? (XsAnyAtomicTypeExpr) null : xs.string(arg2));
  }

  
  @Override
  public XsIntegerExpr rank(ServerExpression arg1, ServerExpression arg2) {
    if (arg2 == null) {
      throw new IllegalArgumentException("arg2 parameter for rank() cannot be null");
    }
    return new XsExprImpl.IntegerCallImpl("math", "rank", new Object[]{ arg1, arg2 });
  }

  
  @Override
  public XsIntegerExpr rank(ServerExpression arg1, String arg2, String options) {
    return rank(arg1, (arg2 == null) ? (XsAnyAtomicTypeExpr) null : xs.string(arg2), (options == null) ? (XsStringExpr) null : xs.string(options));
  }

  
  @Override
  public XsIntegerExpr rank(ServerExpression arg1, ServerExpression arg2, ServerExpression options) {
    if (arg2 == null) {
      throw new IllegalArgumentException("arg2 parameter for rank() cannot be null");
    }
    return new XsExprImpl.IntegerCallImpl("math", "rank", new Object[]{ arg1, arg2, options });
  }

  
  @Override
  public XsDoubleExpr sin(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for sin() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "sin", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr sinh(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for sinh() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "sinh", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr sqrt(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for sqrt() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "sqrt", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr stddev(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("math", "stddev", new Object[]{ arg });
  }

  
  @Override
  public XsDoubleExpr stddevP(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("math", "stddev-p", new Object[]{ arg });
  }

  
  @Override
  public XsDoubleExpr tan(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for tan() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "tan", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr tanh(ServerExpression x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for tanh() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "tanh", new Object[]{ x });
  }

  
  @Override
  public XsNumericExpr trunc(ServerExpression arg) {
    return new XsExprImpl.NumericCallImpl("math", "trunc", new Object[]{ arg });
  }

  
  @Override
  public XsNumericExpr trunc(ServerExpression arg, long n) {
    return trunc(arg, xs.integer(n));
  }

  
  @Override
  public XsNumericExpr trunc(ServerExpression arg, ServerExpression n) {
    if (n == null) {
      throw new IllegalArgumentException("n parameter for trunc() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("math", "trunc", new Object[]{ arg, n });
  }

  
  @Override
  public XsDoubleExpr variance(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("math", "variance", new Object[]{ arg });
  }

  
  @Override
  public XsDoubleExpr varianceP(ServerExpression arg) {
    return new XsExprImpl.DoubleCallImpl("math", "variance-p", new Object[]{ arg });
  }

  @Override
  public MathLinearModelSeqExpr linearModelSeq(MathLinearModelExpr... items) {
    return new LinearModelSeqListImpl(items);
  }
  static class LinearModelSeqListImpl extends BaseTypeImpl.ServerExpressionListImpl implements MathLinearModelSeqExpr {
    LinearModelSeqListImpl(Object[] items) {
      super(items);
    }
  }
  static class LinearModelSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements MathLinearModelSeqExpr {
    LinearModelSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class LinearModelCallImpl extends BaseTypeImpl.ServerExpressionCallImpl implements MathLinearModelExpr {
    LinearModelCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  }
