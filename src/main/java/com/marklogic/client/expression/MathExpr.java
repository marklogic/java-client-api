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
package com.marklogic.client.expression;

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

// IMPORTANT: Do not edit. This file is generated. 

/**
 * Builds expressions to call functions in the math server library for a row
 * pipeline.
 */
public interface MathExpr {
    /**
    * Returns the arc cosine of x, in radians, in the range from 0 to pi (inclusive).
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:acos" target="mlserverdoc">math:acos</a>
    * @param x  The fraction to be evaluated. Must be in the range of -1 to +1 (inclusive).
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr acos(XsDoubleExpr x);
    /**
    * Returns the arc sine of x, in radians, in the range from -pi/2 to +pi/2 (inclusive).
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:asin" target="mlserverdoc">math:asin</a>
    * @param x  The fraction to be evaluated. Must be in the range of -1 to +1 (inclusive).
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr asin(XsDoubleExpr x);
    /**
    * Returns the arc tangent of x, in radians. in the range from -pi/2 to +pi/2 (inclusive).
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:atan" target="mlserverdoc">math:atan</a>
    * @param x  The floating point number to be evaluated.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr atan(XsDoubleExpr x);
    /**
    * Returns the arc tangent of y/x, in radians, in the range from -pi/2 to +pi/2 (inclusive), using the signs of y and x to determine the apropriate quadrant.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:atan2" target="mlserverdoc">math:atan2</a>
    * @param y  The floating point dividend.
    * @param x  The floating point divisor.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr atan2(XsDoubleExpr y, double x);
    /**
    * Returns the arc tangent of y/x, in radians, in the range from -pi/2 to +pi/2 (inclusive), using the signs of y and x to determine the apropriate quadrant.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:atan2" target="mlserverdoc">math:atan2</a>
    * @param y  The floating point dividend.
    * @param x  The floating point divisor.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr atan2(XsDoubleExpr y, XsDoubleExpr x);
    /**
    * Returns the smallest integer greater than or equal to x.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:ceil" target="mlserverdoc">math:ceil</a>
    * @param x  The floating point number to be evaluated.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr ceil(XsDoubleExpr x);
    /**
    * Returns the Pearson correlation coefficient of a data set. The size of the input array should be 2. The function eliminates all pairs for which either the first element or the second element is empty. After the elimination, if the length of the input is less than 2, the function returns the empty sequence. After the elimination, if the standard deviation of the first column or the standard deviation of the second column is 0, the function returns the empty sequence.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:correlation" target="mlserverdoc">math:correlation</a>
    * @param arg  The input data set. Each array should contain a pair of values.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr correlation(JsonArraySeqExpr arg);
    /**
    * Returns the cosine of x, in the range from -1 to +1 (inclusive).
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:cos" target="mlserverdoc">math:cos</a>
    * @param x  The floating point number to be evaluated.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr cos(XsDoubleExpr x);
    /**
    * Returns the hyperbolic cosine of x.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:cosh" target="mlserverdoc">math:cosh</a>
    * @param x  The floating point number to be evaluated.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr cosh(XsDoubleExpr x);
    /**
    * Returns the cotangent of x.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:cot" target="mlserverdoc">math:cot</a>
    * @param x  The floating point number to be evaluated.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr cot(XsDoubleExpr x);
    /**
    *  Returns the sample covariance of a data set. The size of the input array should be 2. The function eliminates all pairs for which either the first element or the second element is empty. After the elimination, if the length of the input is less than 2, the function returns the empty sequence.  <p>For the version of this that uses range indexes, see <a>cts:covariance</a>. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:covariance" target="mlserverdoc">math:covariance</a>
    * @param arg  The input data set. Each array should contain a pair of values.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr covariance(JsonArraySeqExpr arg);
    /**
    *  Returns the population covariance of a data set. The size of the input array should be 2. The function eliminates all pairs for which either the first element or the second element is empty. After the elimination, if the length of the input is 0, the function returns the empty sequence.  <p>For the version of this that uses range indexes, see <a>cts:covariance-p</a>. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:covariance-p" target="mlserverdoc">math:covariance-p</a>
    * @param arg  The input data set. Each array should contain a pair of values.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr covarianceP(JsonArraySeqExpr arg);
    /**
    * Returns numeric expression converted from radians to degrees.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:degrees" target="mlserverdoc">math:degrees</a>
    * @param x  An angle expressed in radians.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr degrees(XsDoubleExpr x);
    /**
    * Returns e (approximately 2.71828182845905) to the xth power.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:exp" target="mlserverdoc">math:exp</a>
    * @param x  The exponent to be evaluated.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr exp(XsDoubleExpr x);
    /**
    * Returns the absolute value of x.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:fabs" target="mlserverdoc">math:fabs</a>
    * @param x  The floating point number to be evaluated.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr fabs(XsDoubleExpr x);
    /**
    * Returns the largest integer less than or equal to x.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:floor" target="mlserverdoc">math:floor</a>
    * @param x  The floating point number to be evaluated.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr floor(XsDoubleExpr x);
    /**
    * Returns the remainder of x/y.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:fmod" target="mlserverdoc">math:fmod</a>
    * @param x  The floating point dividend.
    * @param y  The floating point divisor.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr fmod(XsDoubleExpr x, double y);
    /**
    * Returns the remainder of x/y.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:fmod" target="mlserverdoc">math:fmod</a>
    * @param x  The floating point dividend.
    * @param y  The floating point divisor.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr fmod(XsDoubleExpr x, XsDoubleExpr y);
    /**
    * Returns x broken up into mantissa and exponent, where x = mantissa*2^exponent.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:frexp" target="mlserverdoc">math:frexp</a>
    * @param x  The exponent to be evaluated.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr frexp(XsDoubleExpr x);
    /**
    * Returns x*2^i.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:ldexp" target="mlserverdoc">math:ldexp</a>
    * @param y  The floating-point number to be multiplied.
    * @param i  The exponent integer.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr ldexp(XsDoubleExpr y, long i);
    /**
    * Returns x*2^i.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:ldexp" target="mlserverdoc">math:ldexp</a>
    * @param y  The floating-point number to be multiplied.
    * @param i  The exponent integer.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr ldexp(XsDoubleExpr y, XsIntegerExpr i);
    /**
    * Returns a linear model that fits the given data set. The size of the input array should be 2, as currently only simple linear regression model is supported. The first element of the array should be the value of the dependent variable while the other element should be the value of the independent variable. <p>The function eliminates all pairs for which either the first element or the second element is empty. After the elimination, if the length of the input is less than 2, the function returns the empty sequence. After the elimination, if the standard deviation of the independent variable is 0, the function returns a linear model with intercept = the mean of the dependent variable, coefficients = NaN and r-squared = NaN. After the elimination, if the standard deviation of the dependent variable is 0, the function returns a linear model with r-squared = NaN. <p>For the version of this function that uses Range Indexes, see <a>cts:linear-model</a>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:linear-model" target="mlserverdoc">math:linear-model</a>
    * @param arg  The input data set. Each array should contain a pair of values.
    * @return  a MathLinearModelExpr expression
    */
    public MathLinearModelExpr linearModel(JsonArraySeqExpr arg);
    /**
    * Returns the coefficients of the linear model. Currently only simple linear regression model is supported so the return should contain only one coefficient (also called "slope").
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:linear-model-coeff" target="mlserverdoc">math:linear-model-coeff</a>
    * @param linearModel  A linear model.
    * @return  a XsDoubleSeqExpr expression sequence
    */
    public XsDoubleSeqExpr linearModelCoeff(MathLinearModelExpr linearModel);
    /**
    * Returns the intercept of the linear model.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:linear-model-intercept" target="mlserverdoc">math:linear-model-intercept</a>
    * @param linearModel  A linear model.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr linearModelIntercept(MathLinearModelExpr linearModel);
    /**
    * Returns the R^2 value of the linear model.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:linear-model-rsquared" target="mlserverdoc">math:linear-model-rsquared</a>
    * @param linearModel  A linear model.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr linearModelRsquared(MathLinearModelExpr linearModel);
    /**
    * Returns the base-e logarithm of x.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:log" target="mlserverdoc">math:log</a>
    * @param x  The floating point number to be evaluated.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr log(XsDoubleExpr x);
    /**
    * Returns the base-10 logarithm of x.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:log10" target="mlserverdoc">math:log10</a>
    * @param x  The floating point number to be evaluated.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr log10(XsDoubleExpr x);
    /**
    * Returns the median of a sequence of values. The function returns the empty sequence if the input is the empty sequence.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:median" target="mlserverdoc">math:median</a>
    * @param arg  The sequence of values.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr median(XsDoubleSeqExpr arg);
    /**
    *  Returns the mode of a sequence. The mode is the value that occurs most frequently in a data set. If no value occurs more than once in the data set, the function returns the empty sequence. If the input is the empty sequence, the function returns the empty sequence. <p> Note that a data set can have multiple “modes”. The order of multiple modes in the returned sequence is undefined. <p> Also note that values from a lexicon lookup are repeated <code>cts:frequency</code> times before calculating the mode. <p> The function can be used on numeric values, <code>xs:yearMonthDuration</code>, <code>xs:dayTimeDuration</code>, <code>xs:string</code>, <code>xs:anyURI</code>, <code>xs:date</code>, <code>xs:dateTime</code>, <code>xs:time</code>, and <code>cts:point</code>. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:mode" target="mlserverdoc">math:mode</a>
    * @param arg  The sequence of values.
    * @return  a XsAnyAtomicTypeSeqExpr expression sequence
    */
    public XsAnyAtomicTypeSeqExpr mode(XsAnyAtomicTypeSeqExpr arg);
    /**
    *  Returns the mode of a sequence. The mode is the value that occurs most frequently in a data set. If no value occurs more than once in the data set, the function returns the empty sequence. If the input is the empty sequence, the function returns the empty sequence. <p> Note that a data set can have multiple “modes”. The order of multiple modes in the returned sequence is undefined. <p> Also note that values from a lexicon lookup are repeated <code>cts:frequency</code> times before calculating the mode. <p> The function can be used on numeric values, <code>xs:yearMonthDuration</code>, <code>xs:dayTimeDuration</code>, <code>xs:string</code>, <code>xs:anyURI</code>, <code>xs:date</code>, <code>xs:dateTime</code>, <code>xs:time</code>, and <code>cts:point</code>. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:mode" target="mlserverdoc">math:mode</a>
    * @param arg  The sequence of values.
    * @param options  Options. The default is (). <p> Options include:</p> <blockquote><dl> <dt>"collation=<em>URI</em>"</dt> <dd>Applies only when $arg is of the xs:string type. If no specified, the default collation is used.</dd> <dt>"coordinate-system=<em>name</em>"</dt> <dd>Applies only when $arg is of the cts:point type. If no specified, the default coordinate system is used.</dd> </dl></blockquote>
    * @return  a XsAnyAtomicTypeSeqExpr expression sequence
    */
    public XsAnyAtomicTypeSeqExpr mode(XsAnyAtomicTypeSeqExpr arg, String options);
    /**
    *  Returns the mode of a sequence. The mode is the value that occurs most frequently in a data set. If no value occurs more than once in the data set, the function returns the empty sequence. If the input is the empty sequence, the function returns the empty sequence. <p> Note that a data set can have multiple “modes”. The order of multiple modes in the returned sequence is undefined. <p> Also note that values from a lexicon lookup are repeated <code>cts:frequency</code> times before calculating the mode. <p> The function can be used on numeric values, <code>xs:yearMonthDuration</code>, <code>xs:dayTimeDuration</code>, <code>xs:string</code>, <code>xs:anyURI</code>, <code>xs:date</code>, <code>xs:dateTime</code>, <code>xs:time</code>, and <code>cts:point</code>. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:mode" target="mlserverdoc">math:mode</a>
    * @param arg  The sequence of values.
    * @param options  Options. The default is (). <p> Options include:</p> <blockquote><dl> <dt>"collation=<em>URI</em>"</dt> <dd>Applies only when $arg is of the xs:string type. If no specified, the default collation is used.</dd> <dt>"coordinate-system=<em>name</em>"</dt> <dd>Applies only when $arg is of the cts:point type. If no specified, the default coordinate system is used.</dd> </dl></blockquote>
    * @return  a XsAnyAtomicTypeSeqExpr expression sequence
    */
    public XsAnyAtomicTypeSeqExpr mode(XsAnyAtomicTypeSeqExpr arg, XsStringSeqExpr options);
    /**
    * Returns x broken up into fraction and integer. x = fraction+integer.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:modf" target="mlserverdoc">math:modf</a>
    * @param x  The floating point number to be evaluated.
    * @return  a XsDoubleSeqExpr expression sequence
    */
    public XsDoubleSeqExpr modf(XsDoubleExpr x);
    /**
    * Returns the rank of a value in a data set as a percentage of the data set. If the given value is not equal to any item in the sequence, the function returns the empty sequence. See <code>math:rank</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:percent-rank" target="mlserverdoc">math:percent-rank</a>
    * @param arg  The sequence of values.
    * @param value  The value to be "ranked".
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr percentRank(XsAnyAtomicTypeSeqExpr arg, String value);
    /**
    * Returns the rank of a value in a data set as a percentage of the data set. If the given value is not equal to any item in the sequence, the function returns the empty sequence. See <code>math:rank</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:percent-rank" target="mlserverdoc">math:percent-rank</a>
    * @param arg  The sequence of values.
    * @param value  The value to be "ranked".
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr percentRank(XsAnyAtomicTypeSeqExpr arg, XsAnyAtomicTypeExpr value);
    /**
    * Returns the rank of a value in a data set as a percentage of the data set. If the given value is not equal to any item in the sequence, the function returns the empty sequence. See <code>math:rank</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:percent-rank" target="mlserverdoc">math:percent-rank</a>
    * @param arg  The sequence of values.
    * @param value  The value to be "ranked".
    * @param options  Options. The default is (). <p> Options include:</p> <blockquote><dl> <dt>"ascending"(default)</dt> <dd>Rank the value as if the sequence was sorted in ascending order. </dd> <dt>"descending"</dt> <dd>Rank the value as if the sequence was sorted in descending order. </dd> <dt>"collation=<em>URI</em>"</dt> <dd>Applies only when $arg is of the xs:string type. If no specified, the default collation is used.</dd> <dt>"coordinate-system=<em>name</em>"</dt> <dd>Applies only when $arg is of the cts:point type. If no specified, the default coordinate system is used.</dd> </dl></blockquote>
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr percentRank(XsAnyAtomicTypeSeqExpr arg, String value, String options);
    /**
    * Returns the rank of a value in a data set as a percentage of the data set. If the given value is not equal to any item in the sequence, the function returns the empty sequence. See <code>math:rank</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:percent-rank" target="mlserverdoc">math:percent-rank</a>
    * @param arg  The sequence of values.
    * @param value  The value to be "ranked".
    * @param options  Options. The default is (). <p> Options include:</p> <blockquote><dl> <dt>"ascending"(default)</dt> <dd>Rank the value as if the sequence was sorted in ascending order. </dd> <dt>"descending"</dt> <dd>Rank the value as if the sequence was sorted in descending order. </dd> <dt>"collation=<em>URI</em>"</dt> <dd>Applies only when $arg is of the xs:string type. If no specified, the default collation is used.</dd> <dt>"coordinate-system=<em>name</em>"</dt> <dd>Applies only when $arg is of the cts:point type. If no specified, the default coordinate system is used.</dd> </dl></blockquote>
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr percentRank(XsAnyAtomicTypeSeqExpr arg, XsAnyAtomicTypeExpr value, XsStringSeqExpr options);
    /**
    * Returns a sequence of percentile(s) given a sequence of percentage(s). The function returns the empty sequence if either <code>arg</code> or <code>p</code> is the empty sequence.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:percentile" target="mlserverdoc">math:percentile</a>
    * @param arg  The sequence of values to calculate the percentile(s) on.
    * @param p  The sequence of percentage(s).
    * @return  a XsDoubleSeqExpr expression sequence
    */
    public XsDoubleSeqExpr percentile(XsDoubleSeqExpr arg, double p);
    /**
    * Returns a sequence of percentile(s) given a sequence of percentage(s). The function returns the empty sequence if either <code>arg</code> or <code>p</code> is the empty sequence.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:percentile" target="mlserverdoc">math:percentile</a>
    * @param arg  The sequence of values to calculate the percentile(s) on.
    * @param p  The sequence of percentage(s).
    * @return  a XsDoubleSeqExpr expression sequence
    */
    public XsDoubleSeqExpr percentile(XsDoubleSeqExpr arg, XsDoubleSeqExpr p);
    /**
    * Returns the value of pi.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:pi" target="mlserverdoc">math:pi</a>
    
    */
    public XsDoubleExpr pi();
    /**
    * Returns x^y.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:pow" target="mlserverdoc">math:pow</a>
    * @param x  The floating-point base number.
    * @param y  The exponent to be applied to x.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr pow(XsDoubleExpr x, double y);
    /**
    * Returns x^y.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:pow" target="mlserverdoc">math:pow</a>
    * @param x  The floating-point base number.
    * @param y  The exponent to be applied to x.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr pow(XsDoubleExpr x, XsDoubleExpr y);
    /**
    * Returns numeric expression converted from degrees to radians.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:radians" target="mlserverdoc">math:radians</a>
    * @param x  An angle expressed in degrees.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr radians(XsDoubleExpr x);
    /**
    * Returns the rank of a value in a data set. Ranks are skipped in the event of ties. If the given value is not equal to any item in the sequence, the function returns the empty sequence. The function can be used on numeric values, <code>xs:yearMonthDuration</code>, <code>xs:dayTimeDuration</code>, <code>xs:string</code>, <code>xs:anyURI</code>, <code>xs:date</code>, <code>xs:dateTime</code>, <code>xs:time</code>, and <code>cts:point</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:rank" target="mlserverdoc">math:rank</a>
    * @param arg1  The sequence of values.
    * @param arg2  The value to be "ranked".
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr rank(XsAnyAtomicTypeSeqExpr arg1, String arg2);
    /**
    * Returns the rank of a value in a data set. Ranks are skipped in the event of ties. If the given value is not equal to any item in the sequence, the function returns the empty sequence. The function can be used on numeric values, <code>xs:yearMonthDuration</code>, <code>xs:dayTimeDuration</code>, <code>xs:string</code>, <code>xs:anyURI</code>, <code>xs:date</code>, <code>xs:dateTime</code>, <code>xs:time</code>, and <code>cts:point</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:rank" target="mlserverdoc">math:rank</a>
    * @param arg1  The sequence of values.
    * @param arg2  The value to be "ranked".
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr rank(XsAnyAtomicTypeSeqExpr arg1, XsAnyAtomicTypeExpr arg2);
    /**
    * Returns the rank of a value in a data set. Ranks are skipped in the event of ties. If the given value is not equal to any item in the sequence, the function returns the empty sequence. The function can be used on numeric values, <code>xs:yearMonthDuration</code>, <code>xs:dayTimeDuration</code>, <code>xs:string</code>, <code>xs:anyURI</code>, <code>xs:date</code>, <code>xs:dateTime</code>, <code>xs:time</code>, and <code>cts:point</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:rank" target="mlserverdoc">math:rank</a>
    * @param arg1  The sequence of values.
    * @param arg2  The value to be "ranked".
    * @param options  Options. The default is (). <p> Options include:</p> <blockquote><dl> <dt>"ascending"(default)</dt> <dd>Rank the value as if the sequence was sorted in ascending order. </dd> <dt>"descending"</dt> <dd>Rank the value as if the sequence was sorted in descending order. </dd> <dt>"collation=<em>URI</em>"</dt> <dd>Applies only when $arg is of the xs:string type. If no specified, the default collation is used.</dd> <dt>"coordinate-system=<em>name</em>"</dt> <dd>Applies only when $arg is of the cts:point type. If no specified, the default coordinate system is used.</dd> </dl></blockquote>
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr rank(XsAnyAtomicTypeSeqExpr arg1, String arg2, String options);
    /**
    * Returns the rank of a value in a data set. Ranks are skipped in the event of ties. If the given value is not equal to any item in the sequence, the function returns the empty sequence. The function can be used on numeric values, <code>xs:yearMonthDuration</code>, <code>xs:dayTimeDuration</code>, <code>xs:string</code>, <code>xs:anyURI</code>, <code>xs:date</code>, <code>xs:dateTime</code>, <code>xs:time</code>, and <code>cts:point</code>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:rank" target="mlserverdoc">math:rank</a>
    * @param arg1  The sequence of values.
    * @param arg2  The value to be "ranked".
    * @param options  Options. The default is (). <p> Options include:</p> <blockquote><dl> <dt>"ascending"(default)</dt> <dd>Rank the value as if the sequence was sorted in ascending order. </dd> <dt>"descending"</dt> <dd>Rank the value as if the sequence was sorted in descending order. </dd> <dt>"collation=<em>URI</em>"</dt> <dd>Applies only when $arg is of the xs:string type. If no specified, the default collation is used.</dd> <dt>"coordinate-system=<em>name</em>"</dt> <dd>Applies only when $arg is of the cts:point type. If no specified, the default coordinate system is used.</dd> </dl></blockquote>
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr rank(XsAnyAtomicTypeSeqExpr arg1, XsAnyAtomicTypeExpr arg2, XsStringSeqExpr options);
    /**
    * Returns the sine of x, in the range from -1 to +1 (inclusive).
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:sin" target="mlserverdoc">math:sin</a>
    * @param x  The floating point number to be evaluated.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr sin(XsDoubleExpr x);
    /**
    * Returns the hyperbolic sine of x.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:sinh" target="mlserverdoc">math:sinh</a>
    * @param x  The floating point number to be evaluated.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr sinh(XsDoubleExpr x);
    /**
    * Returns the square root of x.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:sqrt" target="mlserverdoc">math:sqrt</a>
    * @param x  The floating point number to be evaluated.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr sqrt(XsDoubleExpr x);
    /**
    * Returns the sample standard deviation of a sequence of values. The function returns the empty sequence if the length of the input sequence is less than 2.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:stddev" target="mlserverdoc">math:stddev</a>
    * @param arg  The sequence of values.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr stddev(XsDoubleSeqExpr arg);
    /**
    * Returns the standard deviation of a population. The function returns the empty sequence if the input is the empty sequence.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:stddev-p" target="mlserverdoc">math:stddev-p</a>
    * @param arg  The sequence of values.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr stddevP(XsDoubleSeqExpr arg);
    /**
    * Returns the tangent of x.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:tan" target="mlserverdoc">math:tan</a>
    * @param x  The floating point number to be evaluated.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr tan(XsDoubleExpr x);
    /**
    * Returns the hyperbolic tangent of x, in the range from -1 to +1 (inclusive).
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:tanh" target="mlserverdoc">math:tanh</a>
    * @param x  The floating point number to be evaluated.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr tanh(XsDoubleExpr x);
    /**
    *  Returns the number truncated to a certain number of decimal places. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. <p> For xs:float and xs:double arguments, if the argument is positive infinity, then positive infinity is returned. If the argument is negative infinity, then negative infinity is returned. If the argument is positive zero, then positive zero is returned. If the argument is negative zero, then negative zero is returned. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:trunc" target="mlserverdoc">math:trunc</a>
    * @param arg  A numeric value to truncate.
    * @return  a XsNumericExpr expression
    */
    public XsNumericExpr trunc(XsNumericExpr arg);
    /**
    *  Returns the number truncated to a certain number of decimal places. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. <p> For xs:float and xs:double arguments, if the argument is positive infinity, then positive infinity is returned. If the argument is negative infinity, then negative infinity is returned. If the argument is positive zero, then positive zero is returned. If the argument is negative zero, then negative zero is returned. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:trunc" target="mlserverdoc">math:trunc</a>
    * @param arg  A numeric value to truncate.
    * @param n  The numbers of decimal places to truncate to. The default is 0. Negative values cause that many digits to the left of the decimal point to be truncated.
    * @return  a XsNumericExpr expression
    */
    public XsNumericExpr trunc(XsNumericExpr arg, long n);
    /**
    *  Returns the number truncated to a certain number of decimal places. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. <p> For xs:float and xs:double arguments, if the argument is positive infinity, then positive infinity is returned. If the argument is negative infinity, then negative infinity is returned. If the argument is positive zero, then positive zero is returned. If the argument is negative zero, then negative zero is returned. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:trunc" target="mlserverdoc">math:trunc</a>
    * @param arg  A numeric value to truncate.
    * @param n  The numbers of decimal places to truncate to. The default is 0. Negative values cause that many digits to the left of the decimal point to be truncated.
    * @return  a XsNumericExpr expression
    */
    public XsNumericExpr trunc(XsNumericExpr arg, XsIntegerExpr n);
    /**
    * Returns the sample variance of a sequence of values. The function returns the empty sequence if the length of the input sequence is less than 2.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:variance" target="mlserverdoc">math:variance</a>
    * @param arg  The sequence of values.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr variance(XsDoubleSeqExpr arg);
    /**
    * Returns the population variance of a sequence of values. The function returns the empty sequence if the input is the empty sequence.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/math:variance-p" target="mlserverdoc">math:variance-p</a>
    * @param arg  The sequence of values.
    * @return  a XsDoubleExpr expression
    */
    public XsDoubleExpr varianceP(XsDoubleSeqExpr arg);
    /**
     * Constructs a sequence of MathLinearModelExpr items.
     */
    public MathLinearModelSeqExpr linearModelSeq(MathLinearModelExpr... items);

}
