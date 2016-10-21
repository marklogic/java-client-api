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

import com.marklogic.client.expression.MathExpr;
import com.marklogic.client.type.XsStringSeqExpr;
 import com.marklogic.client.type.XsIntegerExpr;
 import com.marklogic.client.type.XsAnyAtomicTypeExpr;
 import com.marklogic.client.type.XsDoubleSeqExpr;
 import com.marklogic.client.type.MathLinearModelExpr;
 import com.marklogic.client.type.XsDoubleExpr;
 import com.marklogic.client.type.JsonArraySeqExpr;
 import com.marklogic.client.type.XsAnyAtomicTypeSeqExpr;
 import com.marklogic.client.type.XsNumericExpr;
 import com.marklogic.client.type.MathLinearModelSeqExpr;
 import com.marklogic.client.type.ItemSeqExpr;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class MathExprImpl implements MathExpr {
    private XsExprImpl xs = null;
    public MathExprImpl(XsExprImpl xs) {
        this.xs = xs;
    }
     @Override
        public XsDoubleExpr acos(double x) {
        return acos(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr acos(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "acos", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr asin(double x) {
        return asin(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr asin(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "asin", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr atan(double x) {
        return atan(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr atan(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "atan", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr atan2(double y, double x) {
        return atan2(xs.doubleVal(y), xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr atan2(XsDoubleExpr y, XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "atan2", new Object[]{ y, x });
    }
    @Override
        public XsDoubleExpr ceil(double x) {
        return ceil(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr ceil(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "ceil", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr correlation(JsonArraySeqExpr arg) {
        return new XsExprImpl.XsDoubleCallImpl("math", "correlation", new Object[]{ arg });
    }
    @Override
        public XsDoubleExpr cos(double x) {
        return cos(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr cos(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "cos", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr cosh(double x) {
        return cosh(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr cosh(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "cosh", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr cot(double x) {
        return cot(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr cot(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "cot", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr covariance(JsonArraySeqExpr arg) {
        return new XsExprImpl.XsDoubleCallImpl("math", "covariance", new Object[]{ arg });
    }
    @Override
        public XsDoubleExpr covarianceP(JsonArraySeqExpr arg) {
        return new XsExprImpl.XsDoubleCallImpl("math", "covariance-p", new Object[]{ arg });
    }
    @Override
        public XsDoubleExpr degrees(double x) {
        return degrees(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr degrees(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "degrees", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr exp(double x) {
        return exp(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr exp(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "exp", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr fabs(double x) {
        return fabs(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr fabs(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "fabs", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr floor(double x) {
        return floor(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr floor(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "floor", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr fmod(double x, double y) {
        return fmod(xs.doubleVal(x), xs.doubleVal(y)); 
    }
    @Override
        public XsDoubleExpr fmod(XsDoubleExpr x, XsDoubleExpr y) {
        return new XsExprImpl.XsDoubleCallImpl("math", "fmod", new Object[]{ x, y });
    }
    @Override
        public ItemSeqExpr frexp(double x) {
        return frexp(xs.doubleVal(x)); 
    }
    @Override
        public ItemSeqExpr frexp(XsDoubleExpr x) {
        return new BaseTypeImpl.ItemSeqCallImpl("math", "frexp", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr ldexp(double y, XsIntegerExpr i) {
        return ldexp(xs.doubleVal(y), i); 
    }
    @Override
        public XsDoubleExpr ldexp(XsDoubleExpr y, XsIntegerExpr i) {
        return new XsExprImpl.XsDoubleCallImpl("math", "ldexp", new Object[]{ y, i });
    }
    @Override
        public MathLinearModelExpr linearModel(JsonArraySeqExpr arg) {
        return new MathExprImpl.MathLinearModelCallImpl("math", "linear-model", new Object[]{ arg });
    }
    @Override
        public XsDoubleSeqExpr linearModelCoeff(MathLinearModelExpr linearModel) {
        return new XsExprImpl.XsDoubleSeqCallImpl("math", "linear-model-coeff", new Object[]{ linearModel });
    }
    @Override
        public XsDoubleExpr linearModelIntercept(MathLinearModelExpr linearModel) {
        return new XsExprImpl.XsDoubleCallImpl("math", "linear-model-intercept", new Object[]{ linearModel });
    }
    @Override
        public XsDoubleExpr linearModelRsquared(MathLinearModelExpr linearModel) {
        return new XsExprImpl.XsDoubleCallImpl("math", "linear-model-rsquared", new Object[]{ linearModel });
    }
    @Override
        public XsDoubleExpr log(double x) {
        return log(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr log(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "log", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr log10(double x) {
        return log10(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr log10(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "log10", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr median(double... arg) {
        return median(xs.doubleVals(arg)); 
    }
    @Override
        public XsDoubleExpr median(XsDoubleSeqExpr arg) {
        return new XsExprImpl.XsDoubleCallImpl("math", "median", new Object[]{ arg });
    }
    @Override
        public XsAnyAtomicTypeSeqExpr mode(XsAnyAtomicTypeSeqExpr arg) {
        return new XsExprImpl.XsAnyAtomicTypeSeqCallImpl("math", "mode", new Object[]{ arg });
    }
    @Override
        public XsAnyAtomicTypeSeqExpr mode(XsAnyAtomicTypeSeqExpr arg, String... options) {
        return mode(arg, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public XsAnyAtomicTypeSeqExpr mode(XsAnyAtomicTypeSeqExpr arg, XsStringSeqExpr options) {
        return new XsExprImpl.XsAnyAtomicTypeSeqCallImpl("math", "mode", new Object[]{ arg, options });
    }
    @Override
        public XsDoubleSeqExpr modf(double x) {
        return modf(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleSeqExpr modf(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleSeqCallImpl("math", "modf", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr percentRank(XsAnyAtomicTypeSeqExpr arg, XsAnyAtomicTypeExpr value) {
        return new XsExprImpl.XsDoubleCallImpl("math", "percent-rank", new Object[]{ arg, value });
    }
    @Override
        public XsDoubleExpr percentRank(XsAnyAtomicTypeSeqExpr arg, XsAnyAtomicTypeExpr value, String... options) {
        return percentRank(arg, value, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public XsDoubleExpr percentRank(XsAnyAtomicTypeSeqExpr arg, XsAnyAtomicTypeExpr value, XsStringSeqExpr options) {
        return new XsExprImpl.XsDoubleCallImpl("math", "percent-rank", new Object[]{ arg, value, options });
    }
    @Override
        public XsDoubleSeqExpr percentile(double arg, double... p) {
        return percentile(xs.doubleVals(arg), xs.doubleVals(p)); 
    }
    @Override
        public XsDoubleSeqExpr percentile(XsDoubleSeqExpr arg, XsDoubleSeqExpr p) {
        return new XsExprImpl.XsDoubleSeqCallImpl("math", "percentile", new Object[]{ arg, p });
    }
    @Override
    public XsDoubleExpr pi() {
        return new XsExprImpl.XsDoubleCallImpl("math", "pi", null);
    }
    @Override
        public XsDoubleExpr pow(double x, double y) {
        return pow(xs.doubleVal(x), xs.doubleVal(y)); 
    }
    @Override
        public XsDoubleExpr pow(XsDoubleExpr x, XsDoubleExpr y) {
        return new XsExprImpl.XsDoubleCallImpl("math", "pow", new Object[]{ x, y });
    }
    @Override
        public XsDoubleExpr radians(double x) {
        return radians(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr radians(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "radians", new Object[]{ x });
    }
    @Override
        public XsIntegerExpr rank(XsAnyAtomicTypeSeqExpr arg1, XsAnyAtomicTypeExpr arg2) {
        return new XsExprImpl.XsIntegerCallImpl("math", "rank", new Object[]{ arg1, arg2 });
    }
    @Override
        public XsIntegerExpr rank(XsAnyAtomicTypeSeqExpr arg1, XsAnyAtomicTypeExpr arg2, String... options) {
        return rank(arg1, arg2, (options == null) ? null : xs.strings(options)); 
    }
    @Override
        public XsIntegerExpr rank(XsAnyAtomicTypeSeqExpr arg1, XsAnyAtomicTypeExpr arg2, XsStringSeqExpr options) {
        return new XsExprImpl.XsIntegerCallImpl("math", "rank", new Object[]{ arg1, arg2, options });
    }
    @Override
        public XsDoubleExpr sin(double x) {
        return sin(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr sin(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "sin", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr sinh(double x) {
        return sinh(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr sinh(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "sinh", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr sqrt(double x) {
        return sqrt(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr sqrt(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "sqrt", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr stddev(double... arg) {
        return stddev(xs.doubleVals(arg)); 
    }
    @Override
        public XsDoubleExpr stddev(XsDoubleSeqExpr arg) {
        return new XsExprImpl.XsDoubleCallImpl("math", "stddev", new Object[]{ arg });
    }
    @Override
        public XsDoubleExpr stddevP(double... arg) {
        return stddevP(xs.doubleVals(arg)); 
    }
    @Override
        public XsDoubleExpr stddevP(XsDoubleSeqExpr arg) {
        return new XsExprImpl.XsDoubleCallImpl("math", "stddev-p", new Object[]{ arg });
    }
    @Override
        public XsDoubleExpr tan(double x) {
        return tan(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr tan(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "tan", new Object[]{ x });
    }
    @Override
        public XsDoubleExpr tanh(double x) {
        return tanh(xs.doubleVal(x)); 
    }
    @Override
        public XsDoubleExpr tanh(XsDoubleExpr x) {
        return new XsExprImpl.XsDoubleCallImpl("math", "tanh", new Object[]{ x });
    }
    @Override
        public XsNumericExpr trunc(XsNumericExpr arg) {
        return new XsExprImpl.XsNumericCallImpl("math", "trunc", new Object[]{ arg });
    }
    @Override
        public XsNumericExpr trunc(XsNumericExpr arg, XsIntegerExpr n) {
        return new XsExprImpl.XsNumericCallImpl("math", "trunc", new Object[]{ arg, n });
    }
    @Override
        public XsDoubleExpr variance(double... arg) {
        return variance(xs.doubleVals(arg)); 
    }
    @Override
        public XsDoubleExpr variance(XsDoubleSeqExpr arg) {
        return new XsExprImpl.XsDoubleCallImpl("math", "variance", new Object[]{ arg });
    }
    @Override
        public XsDoubleExpr varianceP(double... arg) {
        return varianceP(xs.doubleVals(arg)); 
    }
    @Override
        public XsDoubleExpr varianceP(XsDoubleSeqExpr arg) {
        return new XsExprImpl.XsDoubleCallImpl("math", "variance-p", new Object[]{ arg });
    }     @Override
    public MathLinearModelSeqExpr linearModel(MathLinearModelExpr... items) {
        return new MathLinearModelSeqListImpl(items);
    }
        static class MathLinearModelSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements MathLinearModelSeqExpr {
            MathLinearModelSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class MathLinearModelSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements MathLinearModelSeqExpr {
            MathLinearModelSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class MathLinearModelCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements MathLinearModelExpr {
            MathLinearModelCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }

}
