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

import com.marklogic.client.type.ItemSeqExpr;
 import com.marklogic.client.type.JsonArraySeqExpr;
 import com.marklogic.client.type.MathLinearModelExpr;
 import com.marklogic.client.type.MathLinearModelSeqExpr;
 import com.marklogic.client.type.XsAnyAtomicTypeExpr;
 import com.marklogic.client.type.XsAnyAtomicTypeSeqExpr;
 import com.marklogic.client.type.XsDoubleExpr;
 import com.marklogic.client.type.XsDoubleSeqExpr;
 import com.marklogic.client.type.XsIntegerExpr;
 import com.marklogic.client.type.XsNumericExpr;
 import com.marklogic.client.type.XsStringSeqExpr;


// IMPORTANT: Do not edit. This file is generated. 
public interface MathExpr {
    public XsDoubleExpr acos(XsDoubleExpr x);
    public XsDoubleExpr asin(XsDoubleExpr x);
    public XsDoubleExpr atan(XsDoubleExpr x);
    public XsDoubleExpr atan2(XsDoubleExpr y, double x);
    public XsDoubleExpr atan2(XsDoubleExpr y, XsDoubleExpr x);
    public XsDoubleExpr ceil(XsDoubleExpr x);
    public XsDoubleExpr correlation(JsonArraySeqExpr arg);
    public XsDoubleExpr cos(XsDoubleExpr x);
    public XsDoubleExpr cosh(XsDoubleExpr x);
    public XsDoubleExpr cot(XsDoubleExpr x);
    public XsDoubleExpr covariance(JsonArraySeqExpr arg);
    public XsDoubleExpr covarianceP(JsonArraySeqExpr arg);
    public XsDoubleExpr degrees(XsDoubleExpr x);
    public XsDoubleExpr exp(XsDoubleExpr x);
    public XsDoubleExpr fabs(XsDoubleExpr x);
    public XsDoubleExpr floor(XsDoubleExpr x);
    public XsDoubleExpr fmod(XsDoubleExpr x, double y);
    public XsDoubleExpr fmod(XsDoubleExpr x, XsDoubleExpr y);
    public ItemSeqExpr frexp(XsDoubleExpr x);
    public XsDoubleExpr ldexp(XsDoubleExpr y, XsIntegerExpr i);
    public MathLinearModelExpr linearModel(JsonArraySeqExpr arg);
    public XsDoubleSeqExpr linearModelCoeff(MathLinearModelExpr linearModel);
    public XsDoubleExpr linearModelIntercept(MathLinearModelExpr linearModel);
    public XsDoubleExpr linearModelRsquared(MathLinearModelExpr linearModel);
    public XsDoubleExpr log(XsDoubleExpr x);
    public XsDoubleExpr log10(XsDoubleExpr x);
    public XsDoubleExpr median(XsDoubleSeqExpr arg);
    public XsAnyAtomicTypeSeqExpr mode(XsAnyAtomicTypeSeqExpr arg);
    public XsAnyAtomicTypeSeqExpr mode(XsAnyAtomicTypeSeqExpr arg, String options);
    public XsAnyAtomicTypeSeqExpr mode(XsAnyAtomicTypeSeqExpr arg, XsStringSeqExpr options);
    public XsDoubleSeqExpr modf(XsDoubleExpr x);
    public XsDoubleExpr percentRank(XsAnyAtomicTypeSeqExpr arg, XsAnyAtomicTypeExpr value);
    public XsDoubleExpr percentRank(XsAnyAtomicTypeSeqExpr arg, XsAnyAtomicTypeExpr value, String options);
    public XsDoubleExpr percentRank(XsAnyAtomicTypeSeqExpr arg, XsAnyAtomicTypeExpr value, XsStringSeqExpr options);
    public XsDoubleSeqExpr percentile(XsDoubleSeqExpr arg, XsDoubleSeqExpr p);
    public XsDoubleExpr pi();
    public XsDoubleExpr pow(XsDoubleExpr x, double y);
    public XsDoubleExpr pow(XsDoubleExpr x, XsDoubleExpr y);
    public XsDoubleExpr radians(XsDoubleExpr x);
    public XsIntegerExpr rank(XsAnyAtomicTypeSeqExpr arg1, XsAnyAtomicTypeExpr arg2);
    public XsIntegerExpr rank(XsAnyAtomicTypeSeqExpr arg1, XsAnyAtomicTypeExpr arg2, String options);
    public XsIntegerExpr rank(XsAnyAtomicTypeSeqExpr arg1, XsAnyAtomicTypeExpr arg2, XsStringSeqExpr options);
    public XsDoubleExpr sin(XsDoubleExpr x);
    public XsDoubleExpr sinh(XsDoubleExpr x);
    public XsDoubleExpr sqrt(XsDoubleExpr x);
    public XsDoubleExpr stddev(XsDoubleSeqExpr arg);
    public XsDoubleExpr stddevP(XsDoubleSeqExpr arg);
    public XsDoubleExpr tan(XsDoubleExpr x);
    public XsDoubleExpr tanh(XsDoubleExpr x);
    public XsNumericExpr trunc(XsNumericExpr arg);
    public XsNumericExpr trunc(XsNumericExpr arg, XsIntegerExpr n);
    public XsDoubleExpr variance(XsDoubleSeqExpr arg);
    public XsDoubleExpr varianceP(XsDoubleSeqExpr arg);     public MathLinearModelSeqExpr linearModel(MathLinearModelExpr... items);

}
