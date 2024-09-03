/*
 * Copyright (c) 2024 MarkLogic Corporation
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

import com.marklogic.client.type.XsAnyAtomicTypeSeqVal;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.type.XsDoubleSeqVal;
import com.marklogic.client.type.XsDoubleVal;
import com.marklogic.client.type.XsIntegerVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;

import com.marklogic.client.type.ServerExpression;

// IMPORTANT: Do not edit. This file is generated. 

/**
 * Builds expressions to call functions in the math server library for a row
 * pipeline.
 */
public interface MathExpr {
    /**
  * Returns the arc cosine of x, in radians, in the range from 0 to pi (inclusive).
  *
  * <a name="ml-server-type-acos"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:acos" target="mlserverdoc">math:acos</a> server function.
  * @param x  The fraction to be evaluated. Must be in the range of -1 to +1 (inclusive).  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression acos(ServerExpression x);
/**
  * Returns the arc sine of x, in radians, in the range from -pi/2 to +pi/2 (inclusive).
  *
  * <a name="ml-server-type-asin"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:asin" target="mlserverdoc">math:asin</a> server function.
  * @param x  The fraction to be evaluated. Must be in the range of -1 to +1 (inclusive).  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression asin(ServerExpression x);
/**
  * Returns the arc tangent of x, in radians. in the range from -pi/2 to +pi/2 (inclusive).
  *
  * <a name="ml-server-type-atan"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:atan" target="mlserverdoc">math:atan</a> server function.
  * @param x  The floating point number to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression atan(ServerExpression x);
/**
  * Returns the arc tangent of y/x, in radians, in the range from -pi/2 to +pi/2 (inclusive), using the signs of y and x to determine the appropriate quadrant.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:atan2" target="mlserverdoc">math:atan2</a> server function.
  * @param y  The floating point dividend.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param x  The floating point divisor.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression atan2(ServerExpression y, double x);
/**
  * Returns the arc tangent of y/x, in radians, in the range from -pi/2 to +pi/2 (inclusive), using the signs of y and x to determine the appropriate quadrant.
  *
  * <a name="ml-server-type-atan2"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:atan2" target="mlserverdoc">math:atan2</a> server function.
  * @param y  The floating point dividend.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param x  The floating point divisor.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression atan2(ServerExpression y, ServerExpression x);
/**
  * Returns the smallest integer greater than or equal to x.
  *
  * <a name="ml-server-type-ceil"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:ceil" target="mlserverdoc">math:ceil</a> server function.
  * @param x  The floating point number to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression ceil(ServerExpression x);
/**
  * Returns the Pearson correlation coefficient of a data set. The size of the input array should be 2. The function eliminates all pairs for which either the first element or the second element is empty. After the elimination, if the length of the input is less than 2, the function returns the empty sequence. After the elimination, if the standard deviation of the first column or the standard deviation of the second column is 0, the function returns the empty sequence.
  *
  * <a name="ml-server-type-correlation"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:correlation" target="mlserverdoc">math:correlation</a> server function.
  * @param arg  The input data set. Each array should contain a pair of values.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression correlation(ServerExpression arg);
/**
  * Returns the cosine of x, in the range from -1 to +1 (inclusive).
  *
  * <a name="ml-server-type-cos"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:cos" target="mlserverdoc">math:cos</a> server function.
  * @param x  The floating point number to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression cos(ServerExpression x);
/**
  * Returns the hyperbolic cosine of x.
  *
  * <a name="ml-server-type-cosh"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:cosh" target="mlserverdoc">math:cosh</a> server function.
  * @param x  The floating point number to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression cosh(ServerExpression x);
/**
  * Returns the cotangent of x.
  *
  * <a name="ml-server-type-cot"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:cot" target="mlserverdoc">math:cot</a> server function.
  * @param x  The floating point number to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression cot(ServerExpression x);
/**
  * Returns the sample covariance of a data set. The size of the input array should be 2. The function eliminates all pairs for which either the first element or the second element is empty. After the elimination, if the length of the input is less than 2, the function returns the empty sequence.  
  *
  * <a name="ml-server-type-covariance"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:covariance" target="mlserverdoc">math:covariance</a> server function.
  * @param arg  The input data set. Each array should contain a pair of values.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression covariance(ServerExpression arg);
/**
  * Returns the population covariance of a data set. The size of the input array should be 2. The function eliminates all pairs for which either the first element or the second element is empty. After the elimination, if the length of the input is 0, the function returns the empty sequence.  
  *
  * <a name="ml-server-type-covariance-p"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:covariance-p" target="mlserverdoc">math:covariance-p</a> server function.
  * @param arg  The input data set. Each array should contain a pair of values.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression covarianceP(ServerExpression arg);
/**
  * Returns numeric expression converted from radians to degrees.
  *
  * <a name="ml-server-type-degrees"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:degrees" target="mlserverdoc">math:degrees</a> server function.
  * @param x  An angle expressed in radians.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression degrees(ServerExpression x);
/**
  * Returns e (approximately 2.71828182845905) to the xth power.
  *
  * <a name="ml-server-type-exp"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:exp" target="mlserverdoc">math:exp</a> server function.
  * @param x  The exponent to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression exp(ServerExpression x);
/**
  * Returns the absolute value of x.
  *
  * <a name="ml-server-type-fabs"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:fabs" target="mlserverdoc">math:fabs</a> server function.
  * @param x  The floating point number to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression fabs(ServerExpression x);
/**
  * Returns the largest integer less than or equal to x.
  *
  * <a name="ml-server-type-floor"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:floor" target="mlserverdoc">math:floor</a> server function.
  * @param x  The floating point number to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression floor(ServerExpression x);
/**
  * Returns the remainder of x/y.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:fmod" target="mlserverdoc">math:fmod</a> server function.
  * @param x  The floating point dividend.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param y  The floating point divisor.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression fmod(ServerExpression x, double y);
/**
  * Returns the remainder of x/y.
  *
  * <a name="ml-server-type-fmod"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:fmod" target="mlserverdoc">math:fmod</a> server function.
  * @param x  The floating point dividend.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param y  The floating point divisor.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression fmod(ServerExpression x, ServerExpression y);
/**
  * Returns x broken up into mantissa and exponent, where x = mantissa*2^exponent.
  *
  * <a name="ml-server-type-frexp"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:frexp" target="mlserverdoc">math:frexp</a> server function.
  * @param x  The exponent to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression frexp(ServerExpression x);
/**
  * Returns x*2^i.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:ldexp" target="mlserverdoc">math:ldexp</a> server function.
  * @param y  The floating-point number to be multiplied.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param i  The exponent integer.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression ldexp(ServerExpression y, long i);
/**
  * Returns x*2^i.
  *
  * <a name="ml-server-type-ldexp"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:ldexp" target="mlserverdoc">math:ldexp</a> server function.
  * @param y  The floating-point number to be multiplied.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param i  The exponent integer.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression ldexp(ServerExpression y, ServerExpression i);
/**
  * Returns a linear model that fits the given data set. The size of the input array should be 2, as currently only simple linear regression model is supported. The first element of the array should be the value of the dependent variable while the other element should be the value of the independent variable. 
  *
  * <a name="ml-server-type-linear-model"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:linear-model" target="mlserverdoc">math:linear-model</a> server function.
  * @param arg  The input data set. Each array should contain a pair of values.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/math_linear-model.html">math:linear-model</a> server data type
  */
  public ServerExpression linearModel(ServerExpression arg);
/**
  * Returns the coefficients of the linear model. Currently only simple linear regression model is supported so the return should contain only one coefficient (also called "slope").
  *
  * <a name="ml-server-type-linear-model-coeff"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:linear-model-coeff" target="mlserverdoc">math:linear-model-coeff</a> server function.
  * @param linearModel  A linear model.  (of <a href="{@docRoot}/doc-files/types/math_linear-model.html">math:linear-model</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression linearModelCoeff(ServerExpression linearModel);
/**
  * Returns the intercept of the linear model.
  *
  * <a name="ml-server-type-linear-model-intercept"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:linear-model-intercept" target="mlserverdoc">math:linear-model-intercept</a> server function.
  * @param linearModel  A linear model.  (of <a href="{@docRoot}/doc-files/types/math_linear-model.html">math:linear-model</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression linearModelIntercept(ServerExpression linearModel);
/**
  * Returns the R^2 value of the linear model.
  *
  * <a name="ml-server-type-linear-model-rsquared"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:linear-model-rsquared" target="mlserverdoc">math:linear-model-rsquared</a> server function.
  * @param linearModel  A linear model.  (of <a href="{@docRoot}/doc-files/types/math_linear-model.html">math:linear-model</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression linearModelRsquared(ServerExpression linearModel);
/**
  * Returns the base-e logarithm of x.
  *
  * <a name="ml-server-type-log"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:log" target="mlserverdoc">math:log</a> server function.
  * @param x  The floating point number to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression log(ServerExpression x);
/**
  * Returns the base-10 logarithm of x.
  *
  * <a name="ml-server-type-log10"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:log10" target="mlserverdoc">math:log10</a> server function.
  * @param x  The floating point number to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression log10(ServerExpression x);
/**
  * Returns the median of a sequence of values. The function returns the empty sequence if the input is the empty sequence.
  *
  * <a name="ml-server-type-median"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:median" target="mlserverdoc">math:median</a> server function.
  * @param arg  The sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression median(ServerExpression arg);
/**
  * Returns the mode of a sequence. The mode is the value that occurs most frequently in a data set. If no value occurs more than once in the data set, the function returns the empty sequence. If the input is the empty sequence, the function returns the empty sequence. 
  *
  * <a name="ml-server-type-mode"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:mode" target="mlserverdoc">math:mode</a> server function.
  * @param arg  The sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public ServerExpression mode(ServerExpression arg);
/**
  * Returns the mode of a sequence. The mode is the value that occurs most frequently in a data set. If no value occurs more than once in the data set, the function returns the empty sequence. If the input is the empty sequence, the function returns the empty sequence. 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:mode" target="mlserverdoc">math:mode</a> server function.
  * @param arg  The sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param options  Options. The default is ().  Options include:  "collation=URI" Applies only when $arg is of the xs:string type. If no specified, the default collation is used. "coordinate-system=name" Applies only when $arg is of the cts:point type. If no specified, the default coordinate system is used.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public ServerExpression mode(ServerExpression arg, String options);
/**
  * Returns the mode of a sequence. The mode is the value that occurs most frequently in a data set. If no value occurs more than once in the data set, the function returns the empty sequence. If the input is the empty sequence, the function returns the empty sequence. 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:mode" target="mlserverdoc">math:mode</a> server function.
  * @param arg  The sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param options  Options. The default is ().  Options include:  "collation=URI" Applies only when $arg is of the xs:string type. If no specified, the default collation is used. "coordinate-system=name" Applies only when $arg is of the cts:point type. If no specified, the default coordinate system is used.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public ServerExpression mode(ServerExpression arg, ServerExpression options);
/**
  * Returns x broken up into fraction and integer. x = fraction+integer.
  *
  * <a name="ml-server-type-modf"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:modf" target="mlserverdoc">math:modf</a> server function.
  * @param x  The floating point number to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression modf(ServerExpression x);
/**
  * Returns the rank of a value in a data set as a percentage of the data set. If the given value is not equal to any item in the sequence, the function returns the empty sequence. See math:rank.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:percent-rank" target="mlserverdoc">math:percent-rank</a> server function.
  * @param arg  The sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param value  The value to be "ranked".  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression percentRank(ServerExpression arg, String value);
/**
  * Returns the rank of a value in a data set as a percentage of the data set. If the given value is not equal to any item in the sequence, the function returns the empty sequence. See math:rank.
  *
  * <a name="ml-server-type-percent-rank"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:percent-rank" target="mlserverdoc">math:percent-rank</a> server function.
  * @param arg  The sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param value  The value to be "ranked".  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression percentRank(ServerExpression arg, ServerExpression value);
/**
  * Returns the rank of a value in a data set as a percentage of the data set. If the given value is not equal to any item in the sequence, the function returns the empty sequence. See math:rank.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:percent-rank" target="mlserverdoc">math:percent-rank</a> server function.
  * @param arg  The sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param value  The value to be "ranked".  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param options  Options. The default is ().  Options include:  "ascending"(default) Rank the value as if the sequence was sorted in ascending order.  "descending" Rank the value as if the sequence was sorted in descending order.  "collation=URI" Applies only when $arg is of the xs:string type. If no specified, the default collation is used. "coordinate-system=name" Applies only when $arg is of the cts:point type. If no specified, the default coordinate system is used.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression percentRank(ServerExpression arg, String value, String options);
/**
  * Returns the rank of a value in a data set as a percentage of the data set. If the given value is not equal to any item in the sequence, the function returns the empty sequence. See math:rank.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:percent-rank" target="mlserverdoc">math:percent-rank</a> server function.
  * @param arg  The sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param value  The value to be "ranked".  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param options  Options. The default is ().  Options include:  "ascending"(default) Rank the value as if the sequence was sorted in ascending order.  "descending" Rank the value as if the sequence was sorted in descending order.  "collation=URI" Applies only when $arg is of the xs:string type. If no specified, the default collation is used. "coordinate-system=name" Applies only when $arg is of the cts:point type. If no specified, the default coordinate system is used.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression percentRank(ServerExpression arg, ServerExpression value, ServerExpression options);
/**
  * Returns a sequence of percentile(s) given a sequence of percentage(s). The function returns the empty sequence if either arg or p is the empty sequence.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:percentile" target="mlserverdoc">math:percentile</a> server function.
  * @param arg  The sequence of values to calculate the percentile(s) on.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param p  The sequence of percentage(s).  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression percentile(ServerExpression arg, double p);
/**
  * Returns a sequence of percentile(s) given a sequence of percentage(s). The function returns the empty sequence if either arg or p is the empty sequence.
  *
  * <a name="ml-server-type-percentile"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:percentile" target="mlserverdoc">math:percentile</a> server function.
  * @param arg  The sequence of values to calculate the percentile(s) on.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param p  The sequence of percentage(s).  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression percentile(ServerExpression arg, ServerExpression p);
/**
  * Returns the value of pi.
  *
  * <a name="ml-server-type-pi"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:pi" target="mlserverdoc">math:pi</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression pi();
/**
  * Returns x^y.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:pow" target="mlserverdoc">math:pow</a> server function.
  * @param x  The floating-point base number.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param y  The exponent to be applied to x.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression pow(ServerExpression x, double y);
/**
  * Returns x^y.
  *
  * <a name="ml-server-type-pow"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:pow" target="mlserverdoc">math:pow</a> server function.
  * @param x  The floating-point base number.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param y  The exponent to be applied to x.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression pow(ServerExpression x, ServerExpression y);
/**
  * Returns numeric expression converted from degrees to radians.
  *
  * <a name="ml-server-type-radians"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:radians" target="mlserverdoc">math:radians</a> server function.
  * @param x  An angle expressed in degrees.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression radians(ServerExpression x);
/**
  * Returns the rank of a value in a data set. Ranks are skipped in the event of ties. If the given value is not equal to any item in the sequence, the function returns the empty sequence. The function can be used on numeric values, xs:yearMonthDuration, xs:dayTimeDuration, xs:string, xs:anyURI, xs:date, xs:dateTime, xs:time, and cts:point.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:rank" target="mlserverdoc">math:rank</a> server function.
  * @param arg1  The sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param arg2  The value to be "ranked".  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression rank(ServerExpression arg1, String arg2);
/**
  * Returns the rank of a value in a data set. Ranks are skipped in the event of ties. If the given value is not equal to any item in the sequence, the function returns the empty sequence. The function can be used on numeric values, xs:yearMonthDuration, xs:dayTimeDuration, xs:string, xs:anyURI, xs:date, xs:dateTime, xs:time, and cts:point.
  *
  * <a name="ml-server-type-rank"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:rank" target="mlserverdoc">math:rank</a> server function.
  * @param arg1  The sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param arg2  The value to be "ranked".  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression rank(ServerExpression arg1, ServerExpression arg2);
/**
  * Returns the rank of a value in a data set. Ranks are skipped in the event of ties. If the given value is not equal to any item in the sequence, the function returns the empty sequence. The function can be used on numeric values, xs:yearMonthDuration, xs:dayTimeDuration, xs:string, xs:anyURI, xs:date, xs:dateTime, xs:time, and cts:point.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:rank" target="mlserverdoc">math:rank</a> server function.
  * @param arg1  The sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param arg2  The value to be "ranked".  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param options  Options. The default is ().  Options include:  "ascending"(default) Rank the value as if the sequence was sorted in ascending order.  "descending" Rank the value as if the sequence was sorted in descending order.  "collation=URI" Applies only when $arg is of the xs:string type. If no specified, the default collation is used. "coordinate-system=name" Applies only when $arg is of the cts:point type. If no specified, the default coordinate system is used.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression rank(ServerExpression arg1, String arg2, String options);
/**
  * Returns the rank of a value in a data set. Ranks are skipped in the event of ties. If the given value is not equal to any item in the sequence, the function returns the empty sequence. The function can be used on numeric values, xs:yearMonthDuration, xs:dayTimeDuration, xs:string, xs:anyURI, xs:date, xs:dateTime, xs:time, and cts:point.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:rank" target="mlserverdoc">math:rank</a> server function.
  * @param arg1  The sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param arg2  The value to be "ranked".  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param options  Options. The default is ().  Options include:  "ascending"(default) Rank the value as if the sequence was sorted in ascending order.  "descending" Rank the value as if the sequence was sorted in descending order.  "collation=URI" Applies only when $arg is of the xs:string type. If no specified, the default collation is used. "coordinate-system=name" Applies only when $arg is of the cts:point type. If no specified, the default coordinate system is used.   (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression rank(ServerExpression arg1, ServerExpression arg2, ServerExpression options);
/**
  * Returns the sine of x, in the range from -1 to +1 (inclusive).
  *
  * <a name="ml-server-type-sin"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:sin" target="mlserverdoc">math:sin</a> server function.
  * @param x  The floating point number to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression sin(ServerExpression x);
/**
  * Returns the hyperbolic sine of x.
  *
  * <a name="ml-server-type-sinh"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:sinh" target="mlserverdoc">math:sinh</a> server function.
  * @param x  The floating point number to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression sinh(ServerExpression x);
/**
  * Returns the square root of x.
  *
  * <a name="ml-server-type-sqrt"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:sqrt" target="mlserverdoc">math:sqrt</a> server function.
  * @param x  The floating point number to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression sqrt(ServerExpression x);
/**
  * Returns the sample standard deviation of a sequence of values. The function returns the empty sequence if the length of the input sequence is less than 2.
  *
  * <a name="ml-server-type-stddev"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:stddev" target="mlserverdoc">math:stddev</a> server function.
  * @param arg  The sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression stddev(ServerExpression arg);
/**
  * Returns the standard deviation of a population. The function returns the empty sequence if the input is the empty sequence.
  *
  * <a name="ml-server-type-stddev-p"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:stddev-p" target="mlserverdoc">math:stddev-p</a> server function.
  * @param arg  The sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression stddevP(ServerExpression arg);
/**
  * Returns the tangent of x.
  *
  * <a name="ml-server-type-tan"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:tan" target="mlserverdoc">math:tan</a> server function.
  * @param x  The floating point number to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression tan(ServerExpression x);
/**
  * Returns the hyperbolic tangent of x, in the range from -1 to +1 (inclusive).
  *
  * <a name="ml-server-type-tanh"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:tanh" target="mlserverdoc">math:tanh</a> server function.
  * @param x  The floating point number to be evaluated.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression tanh(ServerExpression x);
/**
  * Returns the number truncated to a certain number of decimal places. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. 
  *
  * <a name="ml-server-type-trunc"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:trunc" target="mlserverdoc">math:trunc</a> server function.
  * @param arg  A numeric value to truncate.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public ServerExpression trunc(ServerExpression arg);
/**
  * Returns the number truncated to a certain number of decimal places. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:trunc" target="mlserverdoc">math:trunc</a> server function.
  * @param arg  A numeric value to truncate.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param n  The numbers of decimal places to truncate to. The default is 0. Negative values cause that many digits to the left of the decimal point to be truncated.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public ServerExpression trunc(ServerExpression arg, long n);
/**
  * Returns the number truncated to a certain number of decimal places. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type. 
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:trunc" target="mlserverdoc">math:trunc</a> server function.
  * @param arg  A numeric value to truncate.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param n  The numbers of decimal places to truncate to. The default is 0. Negative values cause that many digits to the left of the decimal point to be truncated.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public ServerExpression trunc(ServerExpression arg, ServerExpression n);
/**
  * Returns the sample variance of a sequence of values. The function returns the empty sequence if the length of the input sequence is less than 2.
  *
  * <a name="ml-server-type-variance"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:variance" target="mlserverdoc">math:variance</a> server function.
  * @param arg  The sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression variance(ServerExpression arg);
/**
  * Returns the population variance of a sequence of values. The function returns the empty sequence if the input is the empty sequence.
  *
  * <a name="ml-server-type-variance-p"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/math:variance-p" target="mlserverdoc">math:variance-p</a> server function.
  * @param arg  The sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression varianceP(ServerExpression arg);
}
