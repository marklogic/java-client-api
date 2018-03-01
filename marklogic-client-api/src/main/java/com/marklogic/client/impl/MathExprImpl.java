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
  public XsDoubleExpr acos(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for acos() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "acos", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr asin(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for asin() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "asin", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr atan(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for atan() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "atan", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr atan2(XsDoubleExpr y, double x) {
    return atan2(y, xs.doubleVal(x));
  }

  
  @Override
  public XsDoubleExpr atan2(XsDoubleExpr y, XsDoubleExpr x) {
    if (y == null) {
      throw new IllegalArgumentException("y parameter for atan2() cannot be null");
    }
    if (x == null) {
      throw new IllegalArgumentException("x parameter for atan2() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "atan2", new Object[]{ y, x });
  }

  
  @Override
  public XsDoubleExpr ceil(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for ceil() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "ceil", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr correlation(JsonArraySeqExpr arg) {
    return new XsExprImpl.DoubleCallImpl("math", "correlation", new Object[]{ arg });
  }

  
  @Override
  public XsDoubleExpr cos(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for cos() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "cos", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr cosh(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for cosh() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "cosh", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr cot(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for cot() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "cot", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr covariance(JsonArraySeqExpr arg) {
    return new XsExprImpl.DoubleCallImpl("math", "covariance", new Object[]{ arg });
  }

  
  @Override
  public XsDoubleExpr covarianceP(JsonArraySeqExpr arg) {
    return new XsExprImpl.DoubleCallImpl("math", "covariance-p", new Object[]{ arg });
  }

  
  @Override
  public XsDoubleExpr degrees(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for degrees() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "degrees", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr exp(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for exp() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "exp", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr fabs(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for fabs() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "fabs", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr floor(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for floor() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "floor", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr fmod(XsDoubleExpr x, double y) {
    return fmod(x, xs.doubleVal(y));
  }

  
  @Override
  public XsDoubleExpr fmod(XsDoubleExpr x, XsDoubleExpr y) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for fmod() cannot be null");
    }
    if (y == null) {
      throw new IllegalArgumentException("y parameter for fmod() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "fmod", new Object[]{ x, y });
  }

  
  @Override
  public ItemSeqExpr frexp(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for frexp() cannot be null");
    }
    return new BaseTypeImpl.ItemSeqCallImpl("math", "frexp", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr ldexp(XsDoubleExpr y, long i) {
    return ldexp(y, xs.integer(i));
  }

  
  @Override
  public XsDoubleExpr ldexp(XsDoubleExpr y, XsIntegerExpr i) {
    if (y == null) {
      throw new IllegalArgumentException("y parameter for ldexp() cannot be null");
    }
    if (i == null) {
      throw new IllegalArgumentException("i parameter for ldexp() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "ldexp", new Object[]{ y, i });
  }

  
  @Override
  public MathLinearModelExpr linearModel(JsonArraySeqExpr arg) {
    return new LinearModelCallImpl("math", "linear-model", new Object[]{ arg });
  }

  
  @Override
  public XsDoubleSeqExpr linearModelCoeff(MathLinearModelExpr linearModel) {
    if (linearModel == null) {
      throw new IllegalArgumentException("linearModel parameter for linearModelCoeff() cannot be null");
    }
    return new XsExprImpl.DoubleSeqCallImpl("math", "linear-model-coeff", new Object[]{ linearModel });
  }

  
  @Override
  public XsDoubleExpr linearModelIntercept(MathLinearModelExpr linearModel) {
    if (linearModel == null) {
      throw new IllegalArgumentException("linearModel parameter for linearModelIntercept() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "linear-model-intercept", new Object[]{ linearModel });
  }

  
  @Override
  public XsDoubleExpr linearModelRsquared(MathLinearModelExpr linearModel) {
    if (linearModel == null) {
      throw new IllegalArgumentException("linearModel parameter for linearModelRsquared() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "linear-model-rsquared", new Object[]{ linearModel });
  }

  
  @Override
  public XsDoubleExpr log(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for log() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "log", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr log10(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for log10() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "log10", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr median(XsDoubleSeqExpr arg) {
    return new XsExprImpl.DoubleCallImpl("math", "median", new Object[]{ arg });
  }

  
  @Override
  public XsAnyAtomicTypeSeqExpr mode(XsAnyAtomicTypeSeqExpr arg) {
    return new XsExprImpl.AnyAtomicTypeSeqCallImpl("math", "mode", new Object[]{ arg });
  }

  
  @Override
  public XsAnyAtomicTypeSeqExpr mode(XsAnyAtomicTypeSeqExpr arg, String options) {
    return mode(arg, (options == null) ? (XsStringExpr) null : xs.string(options));
  }

  
  @Override
  public XsAnyAtomicTypeSeqExpr mode(XsAnyAtomicTypeSeqExpr arg, XsStringSeqExpr options) {
    return new XsExprImpl.AnyAtomicTypeSeqCallImpl("math", "mode", new Object[]{ arg, options });
  }

  
  @Override
  public XsDoubleSeqExpr modf(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for modf() cannot be null");
    }
    return new XsExprImpl.DoubleSeqCallImpl("math", "modf", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr percentRank(XsAnyAtomicTypeSeqExpr arg, String value) {
    return percentRank(arg, (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value));
  }

  
  @Override
  public XsDoubleExpr percentRank(XsAnyAtomicTypeSeqExpr arg, XsAnyAtomicTypeExpr value) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for percentRank() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "percent-rank", new Object[]{ arg, value });
  }

  
  @Override
  public XsDoubleExpr percentRank(XsAnyAtomicTypeSeqExpr arg, String value, String options) {
    return percentRank(arg, (value == null) ? (XsAnyAtomicTypeExpr) null : xs.string(value), (options == null) ? (XsStringExpr) null : xs.string(options));
  }

  
  @Override
  public XsDoubleExpr percentRank(XsAnyAtomicTypeSeqExpr arg, XsAnyAtomicTypeExpr value, XsStringSeqExpr options) {
    if (value == null) {
      throw new IllegalArgumentException("value parameter for percentRank() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "percent-rank", new Object[]{ arg, value, options });
  }

  
  @Override
  public XsDoubleSeqExpr percentile(XsDoubleSeqExpr arg, double p) {
    return percentile(arg, xs.doubleVal(p));
  }

  
  @Override
  public XsDoubleSeqExpr percentile(XsDoubleSeqExpr arg, XsDoubleSeqExpr p) {
    return new XsExprImpl.DoubleSeqCallImpl("math", "percentile", new Object[]{ arg, p });
  }

  
  @Override
  public XsDoubleExpr pi() {
    return new XsExprImpl.DoubleCallImpl("math", "pi", new Object[]{  });
  }

  
  @Override
  public XsDoubleExpr pow(XsDoubleExpr x, double y) {
    return pow(x, xs.doubleVal(y));
  }

  
  @Override
  public XsDoubleExpr pow(XsDoubleExpr x, XsDoubleExpr y) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for pow() cannot be null");
    }
    if (y == null) {
      throw new IllegalArgumentException("y parameter for pow() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "pow", new Object[]{ x, y });
  }

  
  @Override
  public XsDoubleExpr radians(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for radians() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "radians", new Object[]{ x });
  }

  
  @Override
  public XsIntegerExpr rank(XsAnyAtomicTypeSeqExpr arg1, String arg2) {
    return rank(arg1, (arg2 == null) ? (XsAnyAtomicTypeExpr) null : xs.string(arg2));
  }

  
  @Override
  public XsIntegerExpr rank(XsAnyAtomicTypeSeqExpr arg1, XsAnyAtomicTypeExpr arg2) {
    if (arg2 == null) {
      throw new IllegalArgumentException("arg2 parameter for rank() cannot be null");
    }
    return new XsExprImpl.IntegerCallImpl("math", "rank", new Object[]{ arg1, arg2 });
  }

  
  @Override
  public XsIntegerExpr rank(XsAnyAtomicTypeSeqExpr arg1, String arg2, String options) {
    return rank(arg1, (arg2 == null) ? (XsAnyAtomicTypeExpr) null : xs.string(arg2), (options == null) ? (XsStringExpr) null : xs.string(options));
  }

  
  @Override
  public XsIntegerExpr rank(XsAnyAtomicTypeSeqExpr arg1, XsAnyAtomicTypeExpr arg2, XsStringSeqExpr options) {
    if (arg2 == null) {
      throw new IllegalArgumentException("arg2 parameter for rank() cannot be null");
    }
    return new XsExprImpl.IntegerCallImpl("math", "rank", new Object[]{ arg1, arg2, options });
  }

  
  @Override
  public XsDoubleExpr sin(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for sin() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "sin", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr sinh(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for sinh() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "sinh", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr sqrt(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for sqrt() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "sqrt", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr stddev(XsDoubleSeqExpr arg) {
    return new XsExprImpl.DoubleCallImpl("math", "stddev", new Object[]{ arg });
  }

  
  @Override
  public XsDoubleExpr stddevP(XsDoubleSeqExpr arg) {
    return new XsExprImpl.DoubleCallImpl("math", "stddev-p", new Object[]{ arg });
  }

  
  @Override
  public XsDoubleExpr tan(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for tan() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "tan", new Object[]{ x });
  }

  
  @Override
  public XsDoubleExpr tanh(XsDoubleExpr x) {
    if (x == null) {
      throw new IllegalArgumentException("x parameter for tanh() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("math", "tanh", new Object[]{ x });
  }

  
  @Override
  public XsNumericExpr trunc(XsNumericExpr arg) {
    return new XsExprImpl.NumericCallImpl("math", "trunc", new Object[]{ arg });
  }

  
  @Override
  public XsNumericExpr trunc(XsNumericExpr arg, long n) {
    return trunc(arg, xs.integer(n));
  }

  
  @Override
  public XsNumericExpr trunc(XsNumericExpr arg, XsIntegerExpr n) {
    if (n == null) {
      throw new IllegalArgumentException("n parameter for trunc() cannot be null");
    }
    return new XsExprImpl.NumericCallImpl("math", "trunc", new Object[]{ arg, n });
  }

  
  @Override
  public XsDoubleExpr variance(XsDoubleSeqExpr arg) {
    return new XsExprImpl.DoubleCallImpl("math", "variance", new Object[]{ arg });
  }

  
  @Override
  public XsDoubleExpr varianceP(XsDoubleSeqExpr arg) {
    return new XsExprImpl.DoubleCallImpl("math", "variance-p", new Object[]{ arg });
  }

  @Override
  public MathLinearModelSeqExpr linearModelSeq(MathLinearModelExpr... items) {
    return new LinearModelSeqListImpl(items);
  }
  static class LinearModelSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements MathLinearModelSeqExpr {
    LinearModelSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class LinearModelSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements MathLinearModelSeqExpr {
    LinearModelSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class LinearModelCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements MathLinearModelExpr {
    LinearModelCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }

  }
