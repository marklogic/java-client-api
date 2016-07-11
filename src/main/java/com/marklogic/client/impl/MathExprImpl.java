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

import com.marklogic.client.expression.Math;
import com.marklogic.client.expression.Xs;
 import com.marklogic.client.expression.BaseType;
 import com.marklogic.client.expression.Json;
 import com.marklogic.client.impl.XsExprImpl;
 import com.marklogic.client.impl.BaseTypeImpl;
 import com.marklogic.client.impl.JsonExprImpl;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class MathExprImpl implements Math {
    private Xs xs = null;
    public MathExprImpl(Xs xs) {
        this.xs = xs;
    }
     @Override
        public Xs.DoubleExpr acos(double x) {
        return acos(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr acos(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "acos", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr asin(double x) {
        return asin(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr asin(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "asin", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr atan(double x) {
        return atan(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr atan(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "atan", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr atan2(double y, double x) {
        return atan2(xs.doubleVal(y), xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr atan2(Xs.DoubleExpr y, Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "atan2", new Object[]{ y, x });
    }
    @Override
        public Xs.DoubleExpr ceil(double x) {
        return ceil(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr ceil(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "ceil", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr correlation(Json.ArraySeqExpr arg) {
        return new XsExprImpl.DoubleCallImpl("math", "correlation", new Object[]{ arg });
    }
    @Override
        public Xs.DoubleExpr cos(double x) {
        return cos(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr cos(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "cos", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr cosh(double x) {
        return cosh(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr cosh(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "cosh", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr cot(double x) {
        return cot(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr cot(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "cot", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr covariance(Json.ArraySeqExpr arg) {
        return new XsExprImpl.DoubleCallImpl("math", "covariance", new Object[]{ arg });
    }
    @Override
        public Xs.DoubleExpr covarianceP(Json.ArraySeqExpr arg) {
        return new XsExprImpl.DoubleCallImpl("math", "covariance-p", new Object[]{ arg });
    }
    @Override
        public Xs.DoubleExpr degrees(double x) {
        return degrees(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr degrees(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "degrees", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr exp(double x) {
        return exp(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr exp(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "exp", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr fabs(double x) {
        return fabs(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr fabs(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "fabs", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr floor(double x) {
        return floor(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr floor(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "floor", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr fmod(double x, double y) {
        return fmod(xs.doubleVal(x), xs.doubleVal(y)); 
    }
    @Override
        public Xs.DoubleExpr fmod(Xs.DoubleExpr x, Xs.DoubleExpr y) {
        return new XsExprImpl.DoubleCallImpl("math", "fmod", new Object[]{ x, y });
    }
    @Override
        public BaseType.ItemSeqExpr frexp(double x) {
        return frexp(xs.doubleVal(x)); 
    }
    @Override
        public BaseType.ItemSeqExpr frexp(Xs.DoubleExpr x) {
        return new BaseTypeImpl.ItemSeqCallImpl("math", "frexp", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr ldexp(double y, Xs.IntegerExpr i) {
        return ldexp(xs.doubleVal(y), i); 
    }
    @Override
        public Xs.DoubleExpr ldexp(Xs.DoubleExpr y, Xs.IntegerExpr i) {
        return new XsExprImpl.DoubleCallImpl("math", "ldexp", new Object[]{ y, i });
    }
    @Override
        public Math.LinearModelExpr linearModel(Json.ArraySeqExpr arg) {
        return new MathExprImpl.LinearModelCallImpl("math", "linear-model", new Object[]{ arg });
    }
    @Override
        public Xs.DoubleSeqExpr linearModelCoeff(Math.LinearModelExpr linearModel) {
        return new XsExprImpl.DoubleSeqCallImpl("math", "linear-model-coeff", new Object[]{ linearModel });
    }
    @Override
        public Xs.DoubleExpr linearModelIntercept(Math.LinearModelExpr linearModel) {
        return new XsExprImpl.DoubleCallImpl("math", "linear-model-intercept", new Object[]{ linearModel });
    }
    @Override
        public Xs.DoubleExpr linearModelRsquared(Math.LinearModelExpr linearModel) {
        return new XsExprImpl.DoubleCallImpl("math", "linear-model-rsquared", new Object[]{ linearModel });
    }
    @Override
        public Xs.DoubleExpr log(double x) {
        return log(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr log(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "log", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr log10(double x) {
        return log10(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr log10(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "log10", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr median(double... arg) {
        return median(xs.doubleVals(arg)); 
    }
    @Override
        public Xs.DoubleExpr median(Xs.DoubleSeqExpr arg) {
        return new XsExprImpl.DoubleCallImpl("math", "median", new Object[]{ arg });
    }
    @Override
        public Xs.AnyAtomicTypeSeqExpr mode(Xs.AnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.AnyAtomicTypeSeqCallImpl("math", "mode", new Object[]{ arg });
    }
    @Override
        public Xs.AnyAtomicTypeSeqExpr mode(Xs.AnyAtomicTypeSeqExpr arg, String... options) {
        return mode(arg, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public Xs.AnyAtomicTypeSeqExpr mode(Xs.AnyAtomicTypeSeqExpr arg, Xs.StringSeqExpr options) {
        return new XsExprImpl.AnyAtomicTypeSeqCallImpl("math", "mode", new Object[]{ arg, options });
    }
    @Override
        public Xs.DoubleSeqExpr modf(double x) {
        return modf(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleSeqExpr modf(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleSeqCallImpl("math", "modf", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr percentRank(Xs.AnyAtomicTypeSeqExpr arg, Xs.AnyAtomicTypeExpr value) {
        return new XsExprImpl.DoubleCallImpl("math", "percent-rank", new Object[]{ arg, value });
    }
    @Override
        public Xs.DoubleExpr percentRank(Xs.AnyAtomicTypeSeqExpr arg, Xs.AnyAtomicTypeExpr value, String... options) {
        return percentRank(arg, value, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public Xs.DoubleExpr percentRank(Xs.AnyAtomicTypeSeqExpr arg, Xs.AnyAtomicTypeExpr value, Xs.StringSeqExpr options) {
        return new XsExprImpl.DoubleCallImpl("math", "percent-rank", new Object[]{ arg, value, options });
    }
    @Override
        public Xs.DoubleSeqExpr percentile(double arg, double... p) {
        return percentile(xs.doubleVals(arg), xs.doubleVals(p)); 
    }
    @Override
        public Xs.DoubleSeqExpr percentile(Xs.DoubleSeqExpr arg, Xs.DoubleSeqExpr p) {
        return new XsExprImpl.DoubleSeqCallImpl("math", "percentile", new Object[]{ arg, p });
    }
    @Override
    public Xs.DoubleExpr pi() {
        return new XsExprImpl.DoubleCallImpl("math", "pi", null);
    }
    @Override
        public Xs.DoubleExpr pow(double x, double y) {
        return pow(xs.doubleVal(x), xs.doubleVal(y)); 
    }
    @Override
        public Xs.DoubleExpr pow(Xs.DoubleExpr x, Xs.DoubleExpr y) {
        return new XsExprImpl.DoubleCallImpl("math", "pow", new Object[]{ x, y });
    }
    @Override
        public Xs.DoubleExpr radians(double x) {
        return radians(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr radians(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "radians", new Object[]{ x });
    }
    @Override
        public Xs.IntegerExpr rank(Xs.AnyAtomicTypeSeqExpr arg1, Xs.AnyAtomicTypeExpr arg2) {
        return new XsExprImpl.IntegerCallImpl("math", "rank", new Object[]{ arg1, arg2 });
    }
    @Override
        public Xs.IntegerExpr rank(Xs.AnyAtomicTypeSeqExpr arg1, Xs.AnyAtomicTypeExpr arg2, String... options) {
        return rank(arg1, arg2, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public Xs.IntegerExpr rank(Xs.AnyAtomicTypeSeqExpr arg1, Xs.AnyAtomicTypeExpr arg2, Xs.StringSeqExpr options) {
        return new XsExprImpl.IntegerCallImpl("math", "rank", new Object[]{ arg1, arg2, options });
    }
    @Override
        public Xs.DoubleExpr sin(double x) {
        return sin(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr sin(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "sin", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr sinh(double x) {
        return sinh(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr sinh(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "sinh", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr sqrt(double x) {
        return sqrt(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr sqrt(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "sqrt", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr stddev(double... arg) {
        return stddev(xs.doubleVals(arg)); 
    }
    @Override
        public Xs.DoubleExpr stddev(Xs.DoubleSeqExpr arg) {
        return new XsExprImpl.DoubleCallImpl("math", "stddev", new Object[]{ arg });
    }
    @Override
        public Xs.DoubleExpr stddevP(double... arg) {
        return stddevP(xs.doubleVals(arg)); 
    }
    @Override
        public Xs.DoubleExpr stddevP(Xs.DoubleSeqExpr arg) {
        return new XsExprImpl.DoubleCallImpl("math", "stddev-p", new Object[]{ arg });
    }
    @Override
        public Xs.DoubleExpr tan(double x) {
        return tan(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr tan(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "tan", new Object[]{ x });
    }
    @Override
        public Xs.DoubleExpr tanh(double x) {
        return tanh(xs.doubleVal(x)); 
    }
    @Override
        public Xs.DoubleExpr tanh(Xs.DoubleExpr x) {
        return new XsExprImpl.DoubleCallImpl("math", "tanh", new Object[]{ x });
    }
    @Override
        public Xs.NumericExpr trunc(Xs.NumericExpr arg) {
        return new XsExprImpl.NumericCallImpl("math", "trunc", new Object[]{ arg });
    }
    @Override
        public Xs.NumericExpr trunc(Xs.NumericExpr arg, Xs.IntegerExpr n) {
        return new XsExprImpl.NumericCallImpl("math", "trunc", new Object[]{ arg, n });
    }
    @Override
        public Xs.DoubleExpr variance(double... arg) {
        return variance(xs.doubleVals(arg)); 
    }
    @Override
        public Xs.DoubleExpr variance(Xs.DoubleSeqExpr arg) {
        return new XsExprImpl.DoubleCallImpl("math", "variance", new Object[]{ arg });
    }
    @Override
        public Xs.DoubleExpr varianceP(double... arg) {
        return varianceP(xs.doubleVals(arg)); 
    }
    @Override
        public Xs.DoubleExpr varianceP(Xs.DoubleSeqExpr arg) {
        return new XsExprImpl.DoubleCallImpl("math", "variance-p", new Object[]{ arg });
    }     @Override
    public Math.LinearModelSeqExpr linearModel(Math.LinearModelExpr... items) {
        return new MathExprImpl.LinearModelSeqListImpl(items);
    }
        static class LinearModelSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements LinearModelSeqExpr {
            LinearModelSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class LinearModelSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements LinearModelSeqExpr {
            LinearModelSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class LinearModelCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements LinearModelExpr {
            LinearModelCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }

}
