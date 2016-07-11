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
package com.marklogic.client.expression;

// TODO: single import
import com.marklogic.client.expression.BaseType;

import com.marklogic.client.expression.Xs;
 import com.marklogic.client.expression.BaseType;
 import com.marklogic.client.expression.Json;


// IMPORTANT: Do not edit. This file is generated. 
public interface Math {
    public Xs.DoubleExpr acos(double x);
    public Xs.DoubleExpr acos(Xs.DoubleExpr x);
    public Xs.DoubleExpr asin(double x);
    public Xs.DoubleExpr asin(Xs.DoubleExpr x);
    public Xs.DoubleExpr atan(double x);
    public Xs.DoubleExpr atan(Xs.DoubleExpr x);
    public Xs.DoubleExpr atan2(double y, double x);
    public Xs.DoubleExpr atan2(Xs.DoubleExpr y, Xs.DoubleExpr x);
    public Xs.DoubleExpr ceil(double x);
    public Xs.DoubleExpr ceil(Xs.DoubleExpr x);
    public Xs.DoubleExpr correlation(Json.ArraySeqExpr arg);
    public Xs.DoubleExpr cos(double x);
    public Xs.DoubleExpr cos(Xs.DoubleExpr x);
    public Xs.DoubleExpr cosh(double x);
    public Xs.DoubleExpr cosh(Xs.DoubleExpr x);
    public Xs.DoubleExpr cot(double x);
    public Xs.DoubleExpr cot(Xs.DoubleExpr x);
    public Xs.DoubleExpr covariance(Json.ArraySeqExpr arg);
    public Xs.DoubleExpr covarianceP(Json.ArraySeqExpr arg);
    public Xs.DoubleExpr degrees(double x);
    public Xs.DoubleExpr degrees(Xs.DoubleExpr x);
    public Xs.DoubleExpr exp(double x);
    public Xs.DoubleExpr exp(Xs.DoubleExpr x);
    public Xs.DoubleExpr fabs(double x);
    public Xs.DoubleExpr fabs(Xs.DoubleExpr x);
    public Xs.DoubleExpr floor(double x);
    public Xs.DoubleExpr floor(Xs.DoubleExpr x);
    public Xs.DoubleExpr fmod(double x, double y);
    public Xs.DoubleExpr fmod(Xs.DoubleExpr x, Xs.DoubleExpr y);
    public BaseType.ItemSeqExpr frexp(double x);
    public BaseType.ItemSeqExpr frexp(Xs.DoubleExpr x);
    public Xs.DoubleExpr ldexp(double y, Xs.IntegerExpr i);
    public Xs.DoubleExpr ldexp(Xs.DoubleExpr y, Xs.IntegerExpr i);
    public Math.LinearModelExpr linearModel(Json.ArraySeqExpr arg);
    public Xs.DoubleSeqExpr linearModelCoeff(Math.LinearModelExpr linearModel);
    public Xs.DoubleExpr linearModelIntercept(Math.LinearModelExpr linearModel);
    public Xs.DoubleExpr linearModelRsquared(Math.LinearModelExpr linearModel);
    public Xs.DoubleExpr log(double x);
    public Xs.DoubleExpr log(Xs.DoubleExpr x);
    public Xs.DoubleExpr log10(double x);
    public Xs.DoubleExpr log10(Xs.DoubleExpr x);
    public Xs.DoubleExpr median(double... arg);
    public Xs.DoubleExpr median(Xs.DoubleSeqExpr arg);
    public Xs.AnyAtomicTypeSeqExpr mode(Xs.AnyAtomicTypeSeqExpr arg);
    public Xs.AnyAtomicTypeSeqExpr mode(Xs.AnyAtomicTypeSeqExpr arg, String... options);
    public Xs.AnyAtomicTypeSeqExpr mode(Xs.AnyAtomicTypeSeqExpr arg, Xs.StringSeqExpr options);
    public Xs.DoubleSeqExpr modf(double x);
    public Xs.DoubleSeqExpr modf(Xs.DoubleExpr x);
    public Xs.DoubleExpr percentRank(Xs.AnyAtomicTypeSeqExpr arg, Xs.AnyAtomicTypeExpr value);
    public Xs.DoubleExpr percentRank(Xs.AnyAtomicTypeSeqExpr arg, Xs.AnyAtomicTypeExpr value, String... options);
    public Xs.DoubleExpr percentRank(Xs.AnyAtomicTypeSeqExpr arg, Xs.AnyAtomicTypeExpr value, Xs.StringSeqExpr options);
    public Xs.DoubleSeqExpr percentile(double arg, double... p);
    public Xs.DoubleSeqExpr percentile(Xs.DoubleSeqExpr arg, Xs.DoubleSeqExpr p);
    public Xs.DoubleExpr pi();
    public Xs.DoubleExpr pow(double x, double y);
    public Xs.DoubleExpr pow(Xs.DoubleExpr x, Xs.DoubleExpr y);
    public Xs.DoubleExpr radians(double x);
    public Xs.DoubleExpr radians(Xs.DoubleExpr x);
    public Xs.IntegerExpr rank(Xs.AnyAtomicTypeSeqExpr arg1, Xs.AnyAtomicTypeExpr arg2);
    public Xs.IntegerExpr rank(Xs.AnyAtomicTypeSeqExpr arg1, Xs.AnyAtomicTypeExpr arg2, String... options);
    public Xs.IntegerExpr rank(Xs.AnyAtomicTypeSeqExpr arg1, Xs.AnyAtomicTypeExpr arg2, Xs.StringSeqExpr options);
    public Xs.DoubleExpr sin(double x);
    public Xs.DoubleExpr sin(Xs.DoubleExpr x);
    public Xs.DoubleExpr sinh(double x);
    public Xs.DoubleExpr sinh(Xs.DoubleExpr x);
    public Xs.DoubleExpr sqrt(double x);
    public Xs.DoubleExpr sqrt(Xs.DoubleExpr x);
    public Xs.DoubleExpr stddev(double... arg);
    public Xs.DoubleExpr stddev(Xs.DoubleSeqExpr arg);
    public Xs.DoubleExpr stddevP(double... arg);
    public Xs.DoubleExpr stddevP(Xs.DoubleSeqExpr arg);
    public Xs.DoubleExpr tan(double x);
    public Xs.DoubleExpr tan(Xs.DoubleExpr x);
    public Xs.DoubleExpr tanh(double x);
    public Xs.DoubleExpr tanh(Xs.DoubleExpr x);
    public Xs.NumericExpr trunc(Xs.NumericExpr arg);
    public Xs.NumericExpr trunc(Xs.NumericExpr arg, Xs.IntegerExpr n);
    public Xs.DoubleExpr variance(double... arg);
    public Xs.DoubleExpr variance(Xs.DoubleSeqExpr arg);
    public Xs.DoubleExpr varianceP(double... arg);
    public Xs.DoubleExpr varianceP(Xs.DoubleSeqExpr arg);     public Math.LinearModelSeqExpr linearModel(Math.LinearModelExpr... items);
        public interface LinearModelSeqExpr extends BaseType.ItemSeqExpr { }
        public interface LinearModelExpr extends LinearModelSeqExpr, BaseType.ItemExpr { }

}
