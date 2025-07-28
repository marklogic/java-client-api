/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.expression;

import com.marklogic.client.type.XsAnyAtomicTypeSeqVal;
import com.marklogic.client.type.XsAnyAtomicTypeVal;
import com.marklogic.client.type.XsAnyURIVal;
import com.marklogic.client.type.XsBooleanVal;
import com.marklogic.client.type.XsDateTimeVal;
import com.marklogic.client.type.XsDateVal;
import com.marklogic.client.type.XsDayTimeDurationVal;
import com.marklogic.client.type.XsDecimalVal;
import com.marklogic.client.type.XsDoubleVal;
import com.marklogic.client.type.XsDurationVal;
import com.marklogic.client.type.XsIntegerSeqVal;
import com.marklogic.client.type.XsIntegerVal;
import com.marklogic.client.type.XsQNameVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;
import com.marklogic.client.type.XsTimeVal;

import com.marklogic.client.type.ServerExpression;

// IMPORTANT: Do not edit. This file is generated.

/**
 * Builds expressions to call functions in the fn server library for a row
 * pipeline.
 */
public interface FnExpr {
    /**
  * Returns the absolute value of arg. If arg is negative returns -arg otherwise returns arg. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type.
  *
  * <a name="ml-server-type-abs"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:abs" target="mlserverdoc">fn:abs</a> server function.
  * @param arg  A numeric value.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public ServerExpression abs(ServerExpression arg);
/**
  * Adjusts an xs:date value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:date without a timezone. Otherwise, returns an xs:date with a timezone. For purposes of timezone adjustment, an xs:date is treated as an xs:dateTime with time 00:00:00.
  *
  * <a name="ml-server-type-adjust-date-to-timezone"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:adjust-date-to-timezone" target="mlserverdoc">fn:adjust-date-to-timezone</a> server function.
  * @param arg  The date to adjust to the new timezone.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a> server data type
  */
  public ServerExpression adjustDateToTimezone(ServerExpression arg);
/**
  * Adjusts an xs:date value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:date without a timezone. Otherwise, returns an xs:date with a timezone. For purposes of timezone adjustment, an xs:date is treated as an xs:dateTime with time 00:00:00.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:adjust-date-to-timezone" target="mlserverdoc">fn:adjust-date-to-timezone</a> server function.
  * @param arg  The date to adjust to the new timezone.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @param timezone  The new timezone for the date.  (of <a href="{@docRoot}/doc-files/types/xs_dayTimeDuration.html">xs:dayTimeDuration</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a> server data type
  */
  public ServerExpression adjustDateToTimezone(ServerExpression arg, String timezone);
/**
  * Adjusts an xs:date value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:date without a timezone. Otherwise, returns an xs:date with a timezone. For purposes of timezone adjustment, an xs:date is treated as an xs:dateTime with time 00:00:00.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:adjust-date-to-timezone" target="mlserverdoc">fn:adjust-date-to-timezone</a> server function.
  * @param arg  The date to adjust to the new timezone.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @param timezone  The new timezone for the date.  (of <a href="{@docRoot}/doc-files/types/xs_dayTimeDuration.html">xs:dayTimeDuration</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a> server data type
  */
  public ServerExpression adjustDateToTimezone(ServerExpression arg, ServerExpression timezone);
/**
  * Adjusts an xs:dateTime value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:dateTime without a timezone. Otherwise, returns an xs:dateTime with a timezone.
  *
  * <a name="ml-server-type-adjust-dateTime-to-timezone"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:adjust-dateTime-to-timezone" target="mlserverdoc">fn:adjust-dateTime-to-timezone</a> server function.
  * @param arg  The dateTime to adjust to the new timezone.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression adjustDateTimeToTimezone(ServerExpression arg);
/**
  * Adjusts an xs:dateTime value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:dateTime without a timezone. Otherwise, returns an xs:dateTime with a timezone.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:adjust-dateTime-to-timezone" target="mlserverdoc">fn:adjust-dateTime-to-timezone</a> server function.
  * @param arg  The dateTime to adjust to the new timezone.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @param timezone  The new timezone for the dateTime.  (of <a href="{@docRoot}/doc-files/types/xs_dayTimeDuration.html">xs:dayTimeDuration</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression adjustDateTimeToTimezone(ServerExpression arg, String timezone);
/**
  * Adjusts an xs:dateTime value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:dateTime without a timezone. Otherwise, returns an xs:dateTime with a timezone.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:adjust-dateTime-to-timezone" target="mlserverdoc">fn:adjust-dateTime-to-timezone</a> server function.
  * @param arg  The dateTime to adjust to the new timezone.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @param timezone  The new timezone for the dateTime.  (of <a href="{@docRoot}/doc-files/types/xs_dayTimeDuration.html">xs:dayTimeDuration</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression adjustDateTimeToTimezone(ServerExpression arg, ServerExpression timezone);
/**
  * Adjusts an xs:time value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:time without a timezone. Otherwise, returns an xs:time with a timezone.
  *
  * <a name="ml-server-type-adjust-time-to-timezone"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:adjust-time-to-timezone" target="mlserverdoc">fn:adjust-time-to-timezone</a> server function.
  * @param arg  The time to adjust to the new timezone.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a> server data type
  */
  public ServerExpression adjustTimeToTimezone(ServerExpression arg);
/**
  * Adjusts an xs:time value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:time without a timezone. Otherwise, returns an xs:time with a timezone.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:adjust-time-to-timezone" target="mlserverdoc">fn:adjust-time-to-timezone</a> server function.
  * @param arg  The time to adjust to the new timezone.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @param timezone  The new timezone for the date.  (of <a href="{@docRoot}/doc-files/types/xs_dayTimeDuration.html">xs:dayTimeDuration</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a> server data type
  */
  public ServerExpression adjustTimeToTimezone(ServerExpression arg, String timezone);
/**
  * Adjusts an xs:time value to a specific timezone, or to no timezone at all. If timezone is the empty sequence, returns an xs:time without a timezone. Otherwise, returns an xs:time with a timezone.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:adjust-time-to-timezone" target="mlserverdoc">fn:adjust-time-to-timezone</a> server function.
  * @param arg  The time to adjust to the new timezone.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @param timezone  The new timezone for the date.  (of <a href="{@docRoot}/doc-files/types/xs_dayTimeDuration.html">xs:dayTimeDuration</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a> server data type
  */
  public ServerExpression adjustTimeToTimezone(ServerExpression arg, ServerExpression timezone);
/**
  * The result of the function is a new element node whose string value is the original string, but which contains markup to show which parts of the input match the regular expression.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:analyze-string" target="mlserverdoc">fn:analyze-string</a> server function.
  * @param in  The string to start with.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param regex  The regular expression pattern to match.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a> server data type
  */
  public ServerExpression analyzeString(String in, String regex);
/**
  * The result of the function is a new element node whose string value is the original string, but which contains markup to show which parts of the input match the regular expression.
  *
  * <a name="ml-server-type-analyze-string"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:analyze-string" target="mlserverdoc">fn:analyze-string</a> server function.
  * @param in  The string to start with.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param regex  The regular expression pattern to match.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a> server data type
  */
  public ServerExpression analyzeString(ServerExpression in, ServerExpression regex);
/**
  * The result of the function is a new element node whose string value is the original string, but which contains markup to show which parts of the input match the regular expression.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:analyze-string" target="mlserverdoc">fn:analyze-string</a> server function.
  * @param in  The string to start with.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param regex  The regular expression pattern to match.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in http://www.w3.org/TR/xpath-functions/#flags.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a> server data type
  */
  public ServerExpression analyzeString(String in, String regex, String flags);
/**
  * The result of the function is a new element node whose string value is the original string, but which contains markup to show which parts of the input match the regular expression.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:analyze-string" target="mlserverdoc">fn:analyze-string</a> server function.
  * @param in  The string to start with.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param regex  The regular expression pattern to match.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in http://www.w3.org/TR/xpath-functions/#flags.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a> server data type
  */
  public ServerExpression analyzeString(ServerExpression in, ServerExpression regex, ServerExpression flags);
/**
  * Returns the average of the values in the input sequence arg, that is, the sum of the values divided by the number of values.
  *
  * <a name="ml-server-type-avg"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:avg" target="mlserverdoc">fn:avg</a> server function.
  * @param arg  The sequence of values to average.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public ServerExpression avg(ServerExpression arg);
/**
  * Returns the value of the base-uri property for the specified node. If the node is part of a document and does not have a base-uri attribute explicitly set, fn:base-uri typically returns the URI of the document in which the node resides.
  *
  * <a name="ml-server-type-base-uri"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:base-uri" target="mlserverdoc">fn:base-uri</a> server function.
  * @param arg  The node whose base-uri is to be returned.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyURI.html">xs:anyURI</a> server data type
  */
  public ServerExpression baseUri(ServerExpression arg);
/**
  * Computes the effective boolean value of the sequence arg. See Section 2.4.3 Effective Boolean Value[XP].
  *
  * <a name="ml-server-type-boolean"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:boolean" target="mlserverdoc">fn:boolean</a> server function.
  * @param arg  A sequence of items.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression booleanExpr(ServerExpression arg);
/**
  * Returns the smallest (closest to negative infinity) number with no fractional part that is not less than the value of arg. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type.
  *
  * <a name="ml-server-type-ceiling"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:ceiling" target="mlserverdoc">fn:ceiling</a> server function.
  * @param arg  A numeric value.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public ServerExpression ceiling(ServerExpression arg);
/**
  * Returns true if the specified parameters are the same Unicode code point, otherwise returns false. The codepoints are compared according to the Unicode code point collation (http://www.w3.org/2005/xpath-functions/collation/codepoint).
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:codepoint-equal" target="mlserverdoc">fn:codepoint-equal</a> server function.
  * @param comparand1  A string to be compared.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param comparand2  A string to be compared.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression codepointEqual(ServerExpression comparand1, String comparand2);
/**
  * Returns true if the specified parameters are the same Unicode code point, otherwise returns false. The codepoints are compared according to the Unicode code point collation (http://www.w3.org/2005/xpath-functions/collation/codepoint).
  *
  * <a name="ml-server-type-codepoint-equal"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:codepoint-equal" target="mlserverdoc">fn:codepoint-equal</a> server function.
  * @param comparand1  A string to be compared.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param comparand2  A string to be compared.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression codepointEqual(ServerExpression comparand1, ServerExpression comparand2);
/**
  * Creates an xs:string from a sequence of Unicode code points. Returns the zero-length string if arg is the empty sequence. If any of the code points in arg is not a legal XML character, an error is raised.
  *
  * <a name="ml-server-type-codepoints-to-string"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:codepoints-to-string" target="mlserverdoc">fn:codepoints-to-string</a> server function.
  * @param arg  A sequence of Unicode code points.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression codepointsToString(ServerExpression arg);
/**
  * Returns -1, 0, or 1, depending on whether the value of the comparand1 is respectively less than, equal to, or greater than the value of comparand2, according to the rules of the collation that is used.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:compare" target="mlserverdoc">fn:compare</a> server function.
  * @param comparand1  A string to be compared.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param comparand2  A string to be compared.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression compare(ServerExpression comparand1, String comparand2);
/**
  * Returns -1, 0, or 1, depending on whether the value of the comparand1 is respectively less than, equal to, or greater than the value of comparand2, according to the rules of the collation that is used.
  *
  * <a name="ml-server-type-compare"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:compare" target="mlserverdoc">fn:compare</a> server function.
  * @param comparand1  A string to be compared.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param comparand2  A string to be compared.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression compare(ServerExpression comparand1, ServerExpression comparand2);
/**
  * Returns -1, 0, or 1, depending on whether the value of the comparand1 is respectively less than, equal to, or greater than the value of comparand2, according to the rules of the collation that is used.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:compare" target="mlserverdoc">fn:compare</a> server function.
  * @param comparand1  A string to be compared.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param comparand2  A string to be compared.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression compare(ServerExpression comparand1, String comparand2, String collation);
/**
  * Returns -1, 0, or 1, depending on whether the value of the comparand1 is respectively less than, equal to, or greater than the value of comparand2, according to the rules of the collation that is used.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:compare" target="mlserverdoc">fn:compare</a> server function.
  * @param comparand1  A string to be compared.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param comparand2  A string to be compared.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression compare(ServerExpression comparand1, ServerExpression comparand2, ServerExpression collation);
/**
  * Returns the xs:string that is the concatenation of the values of the specified parameters. Accepts two or more xs:anyAtomicType arguments and casts them to xs:string. If any of the parameters is the empty sequence, the parameter is treated as the zero-length string.
  *
  * <a name="ml-server-type-concat"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:concat" target="mlserverdoc">fn:concat</a> server function.
  * @param parameter1  A value.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression concat(ServerExpression... parameter1);
/**
  * Returns true if the first parameter contains the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:contains" target="mlserverdoc">fn:contains</a> server function.
  * @param parameter1  The string from which to test.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param parameter2  The string to test for existence in the first parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression contains(ServerExpression parameter1, String parameter2);
/**
  * Returns true if the first parameter contains the string from the second parameter, otherwise returns false.
  *
  * <a name="ml-server-type-contains"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:contains" target="mlserverdoc">fn:contains</a> server function.
  * @param parameter1  The string from which to test.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param parameter2  The string to test for existence in the first parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression contains(ServerExpression parameter1, ServerExpression parameter2);
/**
  * Returns true if the first parameter contains the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:contains" target="mlserverdoc">fn:contains</a> server function.
  * @param parameter1  The string from which to test.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param parameter2  The string to test for existence in the first parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression contains(ServerExpression parameter1, String parameter2, String collation);
/**
  * Returns true if the first parameter contains the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:contains" target="mlserverdoc">fn:contains</a> server function.
  * @param parameter1  The string from which to test.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param parameter2  The string to test for existence in the first parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression contains(ServerExpression parameter1, ServerExpression parameter2, ServerExpression collation);
/**
  * Returns the number of items in the value of arg.
  *
  * <a name="ml-server-type-count"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:count" target="mlserverdoc">fn:count</a> server function.
  * @param arg  The sequence of items to count.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression count(ServerExpression arg);
/**
  * Returns the number of items in the value of arg.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:count" target="mlserverdoc">fn:count</a> server function.
  * @param arg  The sequence of items to count.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param maximum  The maximum value of the count to return. MarkLogic Server will stop count when the $maximum value is reached and return the $maximum value. This is an extension to the W3C standard fn:count function.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression count(ServerExpression arg, double maximum);
/**
  * Returns the number of items in the value of arg.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:count" target="mlserverdoc">fn:count</a> server function.
  * @param arg  The sequence of items to count.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param maximum  The maximum value of the count to return. MarkLogic Server will stop count when the $maximum value is reached and return the $maximum value. This is an extension to the W3C standard fn:count function.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression count(ServerExpression arg, ServerExpression maximum);
/**
  * Returns xs:date(fn:current-dateTime()). This is an xs:date (with timezone) that is current at some time during the evaluation of a query or transformation in which fn:current-date() is executed. This function is *stable*. The precise instant during the query or transformation represented by the value of fn:current-date() is *implementation dependent*.
  *
  * <a name="ml-server-type-current-date"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:current-date" target="mlserverdoc">fn:current-date</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a> server data type
  */
  public ServerExpression currentDate();
/**
  * Returns the current dateTime value (with timezone) from the dynamic context. (See Section C.2 Dynamic Context Components[XP].) This is an xs:dateTime that is current at some time during the evaluation of a query or transformation in which fn:current-dateTime() is executed. This function is *stable*. The precise instant during the query or transformation represented by the value of fn:current-dateTime() is *implementation dependent*.
  *
  * <a name="ml-server-type-current-dateTime"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:current-dateTime" target="mlserverdoc">fn:current-dateTime</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression currentDateTime();
/**
  * Returns xs:time(fn:current-dateTime()). This is an xs:time (with timezone) that is current at some time during the evaluation of a query or transformation in which fn:current-time() is executed. This function is *stable*. The precise instant during the query or transformation represented by the value of fn:current-time() is *implementation dependent*.
  *
  * <a name="ml-server-type-current-time"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:current-time" target="mlserverdoc">fn:current-time</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a> server data type
  */
  public ServerExpression currentTime();
/**
  * Returns an xs:dateTime value created by combining an xs:date and an xs:time.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:dateTime" target="mlserverdoc">fn:dateTime</a> server function.
  * @param arg1  The date to be combined with the time argument.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @param arg2  The time to be combined with the date argument.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression dateTime(ServerExpression arg1, String arg2);
/**
  * Returns an xs:dateTime value created by combining an xs:date and an xs:time.
  *
  * <a name="ml-server-type-dateTime"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:dateTime" target="mlserverdoc">fn:dateTime</a> server function.
  * @param arg1  The date to be combined with the time argument.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @param arg2  The time to be combined with the date argument.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a> server data type
  */
  public ServerExpression dateTime(ServerExpression arg1, ServerExpression arg2);
/**
  * Returns an xs:integer between 1 and 31, both inclusive, representing the day component in the localized value of arg.
  *
  * <a name="ml-server-type-day-from-date"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:day-from-date" target="mlserverdoc">fn:day-from-date</a> server function.
  * @param arg  The date whose day component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression dayFromDate(ServerExpression arg);
/**
  * Returns an xs:integer between 1 and 31, both inclusive, representing the day component in the localized value of arg.
  *
  * <a name="ml-server-type-day-from-dateTime"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:day-from-dateTime" target="mlserverdoc">fn:day-from-dateTime</a> server function.
  * @param arg  The dateTime whose day component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression dayFromDateTime(ServerExpression arg);
/**
  * Returns an xs:integer representing the days component in the canonical lexical representation of the value of arg. The result may be negative.
  *
  * <a name="ml-server-type-days-from-duration"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:days-from-duration" target="mlserverdoc">fn:days-from-duration</a> server function.
  * @param arg  The duration whose day component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_duration.html">xs:duration</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression daysFromDuration(ServerExpression arg);
/**
  * This function assesses whether two sequences are deep-equal to each other. To be deep-equal, they must contain items that are pairwise deep-equal; and for two items to be deep-equal, they must either be atomic values that compare equal, or nodes of the same kind, with the same name, whose children are deep-equal. This is defined in more detail below. The collation argument identifies a collation which is used at all levels of recursion when strings are compared (but not when names are compared), according to the rules in 7.3.1 Collations.
  *
  * <a name="ml-server-type-deep-equal"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:deep-equal" target="mlserverdoc">fn:deep-equal</a> server function.
  * @param parameter1  The first sequence of items, each item should be an atomic value or node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param parameter2  The sequence of items to compare to the first sequence of items, again each item should be an atomic value or node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression deepEqual(ServerExpression parameter1, ServerExpression parameter2);
/**
  * This function assesses whether two sequences are deep-equal to each other. To be deep-equal, they must contain items that are pairwise deep-equal; and for two items to be deep-equal, they must either be atomic values that compare equal, or nodes of the same kind, with the same name, whose children are deep-equal. This is defined in more detail below. The collation argument identifies a collation which is used at all levels of recursion when strings are compared (but not when names are compared), according to the rules in 7.3.1 Collations.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:deep-equal" target="mlserverdoc">fn:deep-equal</a> server function.
  * @param parameter1  The first sequence of items, each item should be an atomic value or node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param parameter2  The sequence of items to compare to the first sequence of items, again each item should be an atomic value or node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression deepEqual(ServerExpression parameter1, ServerExpression parameter2, String collation);
/**
  * This function assesses whether two sequences are deep-equal to each other. To be deep-equal, they must contain items that are pairwise deep-equal; and for two items to be deep-equal, they must either be atomic values that compare equal, or nodes of the same kind, with the same name, whose children are deep-equal. This is defined in more detail below. The collation argument identifies a collation which is used at all levels of recursion when strings are compared (but not when names are compared), according to the rules in 7.3.1 Collations.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:deep-equal" target="mlserverdoc">fn:deep-equal</a> server function.
  * @param parameter1  The first sequence of items, each item should be an atomic value or node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param parameter2  The sequence of items to compare to the first sequence of items, again each item should be an atomic value or node.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression deepEqual(ServerExpression parameter1, ServerExpression parameter2, ServerExpression collation);
/**
  * Returns the value of the default collation property from the static context. Components of the static context are discussed in Section C.1 Static Context Components[XP].
  *
  * <a name="ml-server-type-default-collation"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:default-collation" target="mlserverdoc">fn:default-collation</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression defaultCollation();
/**
  * Returns the sequence that results from removing from arg all but one of a set of values that are eq to one other. Values that cannot be compared, i.e. the eq operator is not defined for their types, are considered to be distinct. Values of type xs:untypedAtomic are compared as if they were of type xs:string. The order in which the sequence of values is returned is implementation dependent.
  *
  * <a name="ml-server-type-distinct-values"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:distinct-values" target="mlserverdoc">fn:distinct-values</a> server function.
  * @param arg  A sequence of items.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public ServerExpression distinctValues(ServerExpression arg);
/**
  * Returns the sequence that results from removing from arg all but one of a set of values that are eq to one other. Values that cannot be compared, i.e. the eq operator is not defined for their types, are considered to be distinct. Values of type xs:untypedAtomic are compared as if they were of type xs:string. The order in which the sequence of values is returned is implementation dependent.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:distinct-values" target="mlserverdoc">fn:distinct-values</a> server function.
  * @param arg  A sequence of items.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public ServerExpression distinctValues(ServerExpression arg, String collation);
/**
  * Returns the sequence that results from removing from arg all but one of a set of values that are eq to one other. Values that cannot be compared, i.e. the eq operator is not defined for their types, are considered to be distinct. Values of type xs:untypedAtomic are compared as if they were of type xs:string. The order in which the sequence of values is returned is implementation dependent.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:distinct-values" target="mlserverdoc">fn:distinct-values</a> server function.
  * @param arg  A sequence of items.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public ServerExpression distinctValues(ServerExpression arg, ServerExpression collation);
/**
  * Returns the value of the document-uri property for the specified node. If the node is a document node, then the value returned is the URI of the document. If the node is not a document node, then fn:document-uri returns the empty sequence.
  *
  * <a name="ml-server-type-document-uri"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:document-uri" target="mlserverdoc">fn:document-uri</a> server function.
  * @param arg  The node whose document-uri is to be returned.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyURI.html">xs:anyURI</a> server data type
  */
  public ServerExpression documentUri(ServerExpression arg);
/**
  * If the value of arg is the empty sequence, the function returns true; otherwise, the function returns false.
  *
  * <a name="ml-server-type-empty"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:empty" target="mlserverdoc">fn:empty</a> server function.
  * @param arg  A sequence to test.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression empty(ServerExpression arg);
/**
  * Invertible function that escapes characters required to be escaped inside path segments of URIs.
  *
  * <a name="ml-server-type-encode-for-uri"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:encode-for-uri" target="mlserverdoc">fn:encode-for-uri</a> server function.
  * @param uriPart  A string representing an unescaped URI.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression encodeForUri(ServerExpression uriPart);
/**
  * Returns true if the first parameter ends with the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:ends-with" target="mlserverdoc">fn:ends-with</a> server function.
  * @param parameter1  The parameter from which to test.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param parameter2  The string to test whether it is at the end of the first parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression endsWith(ServerExpression parameter1, String parameter2);
/**
  * Returns true if the first parameter ends with the string from the second parameter, otherwise returns false.
  *
  * <a name="ml-server-type-ends-with"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:ends-with" target="mlserverdoc">fn:ends-with</a> server function.
  * @param parameter1  The parameter from which to test.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param parameter2  The string to test whether it is at the end of the first parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression endsWith(ServerExpression parameter1, ServerExpression parameter2);
/**
  * Returns true if the first parameter ends with the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:ends-with" target="mlserverdoc">fn:ends-with</a> server function.
  * @param parameter1  The parameter from which to test.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param parameter2  The string to test whether it is at the end of the first parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression endsWith(ServerExpression parameter1, String parameter2, String collation);
/**
  * Returns true if the first parameter ends with the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:ends-with" target="mlserverdoc">fn:ends-with</a> server function.
  * @param parameter1  The parameter from which to test.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param parameter2  The string to test whether it is at the end of the first parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression endsWith(ServerExpression parameter1, ServerExpression parameter2, ServerExpression collation);
/**
  * %-escapes everything except printable ASCII characters.
  *
  * <a name="ml-server-type-escape-html-uri"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:escape-html-uri" target="mlserverdoc">fn:escape-html-uri</a> server function.
  * @param uriPart  A string representing an unescaped URI.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression escapeHtmlUri(ServerExpression uriPart);
/**
  * If the value of arg is not the empty sequence, the function returns true; otherwise, the function returns false.
  *
  * <a name="ml-server-type-exists"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:exists" target="mlserverdoc">fn:exists</a> server function.
  * @param arg  A sequence to test.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression exists(ServerExpression arg);
/**
  * Returns the xs:boolean value false. Equivalent to xs:boolean("0").
  *
  * <a name="ml-server-type-false"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:false" target="mlserverdoc">fn:false</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression falseExpr();
/**
  * Returns the largest (closest to positive infinity) number with no fractional part that is not greater than the value of arg. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type.
  *
  * <a name="ml-server-type-floor"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:floor" target="mlserverdoc">fn:floor</a> server function.
  * @param arg  A numeric value.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public ServerExpression floor(ServerExpression arg);
/**
  * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a> server function.
  * @param value  The given date $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @param picture  The desired string representation of the given date $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatDate(ServerExpression value, String picture);
/**
  * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  *
  * <a name="ml-server-type-format-date"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a> server function.
  * @param value  The given date $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @param picture  The desired string representation of the given date $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatDate(ServerExpression value, ServerExpression picture);
/**
  * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a> server function.
  * @param value  The given date $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @param picture  The desired string representation of the given date $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the date $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatDate(ServerExpression value, String picture, String language);
/**
  * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a> server function.
  * @param value  The given date $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @param picture  The desired string representation of the given date $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the date $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatDate(ServerExpression value, ServerExpression picture, ServerExpression language);
/**
  * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a> server function.
  * @param value  The given date $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @param picture  The desired string representation of the given date $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the date $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatDate(ServerExpression value, String picture, String language, String calendar);
/**
  * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a> server function.
  * @param value  The given date $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @param picture  The desired string representation of the given date $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the date $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatDate(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression calendar);
/**
  * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a> server function.
  * @param value  The given date $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @param picture  The desired string representation of the given date $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the date $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param country  $country is used the specification to take into account country specific string representation.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatDate(ServerExpression value, String picture, String language, String calendar, String country);
/**
  * Returns a formatted date value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-date" target="mlserverdoc">fn:format-date</a> server function.
  * @param value  The given date $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @param picture  The desired string representation of the given date $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the date $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param country  $country is used the specification to take into account country specific string representation.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatDate(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression calendar, ServerExpression country);
/**
  * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a> server function.
  * @param value  The given dateTime $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @param picture  The desired string representation of the given dateTime $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatDateTime(ServerExpression value, String picture);
/**
  * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  *
  * <a name="ml-server-type-format-dateTime"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a> server function.
  * @param value  The given dateTime $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @param picture  The desired string representation of the given dateTime $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatDateTime(ServerExpression value, ServerExpression picture);
/**
  * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a> server function.
  * @param value  The given dateTime $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @param picture  The desired string representation of the given dateTime $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the dateTime $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatDateTime(ServerExpression value, String picture, String language);
/**
  * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a> server function.
  * @param value  The given dateTime $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @param picture  The desired string representation of the given dateTime $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the dateTime $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatDateTime(ServerExpression value, ServerExpression picture, ServerExpression language);
/**
  * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a> server function.
  * @param value  The given dateTime $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @param picture  The desired string representation of the given dateTime $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the dateTime $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatDateTime(ServerExpression value, String picture, String language, String calendar);
/**
  * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a> server function.
  * @param value  The given dateTime $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @param picture  The desired string representation of the given dateTime $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the dateTime $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatDateTime(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression calendar);
/**
  * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a> server function.
  * @param value  The given dateTime $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @param picture  The desired string representation of the given dateTime $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the dateTime $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param country  $country is used the specification to take into account country specific string representation.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatDateTime(ServerExpression value, String picture, String language, String calendar, String country);
/**
  * Returns a formatted dateTime value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-dateTime" target="mlserverdoc">fn:format-dateTime</a> server function.
  * @param value  The given dateTime $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @param picture  The desired string representation of the given dateTime $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the dateTime $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param country  $country is used the specification to take into account country specific string representation.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatDateTime(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression calendar, ServerExpression country);
/**
  * Returns a formatted string representation of value argument based on the supplied picture. An optional decimal format name may also be supplied for interpretation of the picture string. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-number" target="mlserverdoc">fn:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given number $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the format-number picture string, see http://www.w3.org/TR/xslt20/#function-format-number.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, String picture);
/**
  * Returns a formatted string representation of value argument based on the supplied picture. An optional decimal format name may also be supplied for interpretation of the picture string. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  *
  * <a name="ml-server-type-format-number"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-number" target="mlserverdoc">fn:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given number $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the format-number picture string, see http://www.w3.org/TR/xslt20/#function-format-number.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, ServerExpression picture);
/**
  * Returns a formatted string representation of value argument based on the supplied picture. An optional decimal format name may also be supplied for interpretation of the picture string. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-number" target="mlserverdoc">fn:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given number $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the format-number picture string, see http://www.w3.org/TR/xslt20/#function-format-number.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param decimalFormatName  Represents a named  instruction. It is used to assign values to the variables mentioned above based on the picture string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, String picture, String decimalFormatName);
/**
  * Returns a formatted string representation of value argument based on the supplied picture. An optional decimal format name may also be supplied for interpretation of the picture string. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-number" target="mlserverdoc">fn:format-number</a> server function.
  * @param value  The given numeric $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param picture  The desired string representation of the given number $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the format-number picture string, see http://www.w3.org/TR/xslt20/#function-format-number.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param decimalFormatName  Represents a named  instruction. It is used to assign values to the variables mentioned above based on the picture string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatNumber(ServerExpression value, ServerExpression picture, ServerExpression decimalFormatName);
/**
  * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a> server function.
  * @param value  The given time $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @param picture  The desired string representation of the given time $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatTime(ServerExpression value, String picture);
/**
  * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  *
  * <a name="ml-server-type-format-time"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a> server function.
  * @param value  The given time $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @param picture  The desired string representation of the given time $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatTime(ServerExpression value, ServerExpression picture);
/**
  * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a> server function.
  * @param value  The given time $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @param picture  The desired string representation of the given time $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the time $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatTime(ServerExpression value, String picture, String language);
/**
  * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a> server function.
  * @param value  The given time $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @param picture  The desired string representation of the given time $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the time $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatTime(ServerExpression value, ServerExpression picture, ServerExpression language);
/**
  * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a> server function.
  * @param value  The given time $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @param picture  The desired string representation of the given time $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the time $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatTime(ServerExpression value, String picture, String language, String calendar);
/**
  * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a> server function.
  * @param value  The given time $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @param picture  The desired string representation of the given time $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the time $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatTime(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression calendar);
/**
  * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a> server function.
  * @param value  The given time $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @param picture  The desired string representation of the given time $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the time $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param country  $country is used the specification to take into account country specific string representation.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatTime(ServerExpression value, String picture, String language, String calendar, String country);
/**
  * Returns a formatted time value based on the picture argument. This is an XSLT function, and it is available in XSLT, XQuery 1.0-ml, and Server-Side JavaScript.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:format-time" target="mlserverdoc">fn:format-time</a> server function.
  * @param value  The given time $value that needs to be formatted.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @param picture  The desired string representation of the given time $value. The picture string is a sequence of characters, in which the characters represent variables such as, decimal-separator-sign, grouping-sign, zero-digit-sign, digit-sign, pattern-separator, percent sign and per-mille-sign. For details on the picture string, see http://www.w3.org/TR/xslt20/#date-picture-string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param language  The desired language for string representation of the time $value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param calendar  The only calendar supported at this point is "Gregorian" or "AD".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param country  $country is used the specification to take into account country specific string representation.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression formatTime(ServerExpression value, ServerExpression picture, ServerExpression language, ServerExpression calendar, ServerExpression country);
/**
  * Returns a string that uniquely identifies a given node.
  *
  * <a name="ml-server-type-generate-id"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:generate-id" target="mlserverdoc">fn:generate-id</a> server function.
  * @param node  The node whose ID will be generated.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression generateId(ServerExpression node);
/**
  * Returns the first item in a sequence. For more details, see XPath 3.0 Functions and Operators.
  *
  * <a name="ml-server-type-head"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:head" target="mlserverdoc">fn:head</a> server function.
  * @param seq  A sequence of items.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression head(ServerExpression seq);
/**
  * Returns an xs:integer between 0 and 23, both inclusive, representing the hours component in the localized value of arg.
  *
  * <a name="ml-server-type-hours-from-dateTime"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:hours-from-dateTime" target="mlserverdoc">fn:hours-from-dateTime</a> server function.
  * @param arg  The dateTime whose hours component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression hoursFromDateTime(ServerExpression arg);
/**
  * Returns an xs:integer representing the hours component in the canonical lexical representation of the value of arg. The result may be negative.
  *
  * <a name="ml-server-type-hours-from-duration"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:hours-from-duration" target="mlserverdoc">fn:hours-from-duration</a> server function.
  * @param arg  The duration whose hour component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_duration.html">xs:duration</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression hoursFromDuration(ServerExpression arg);
/**
  * Returns an xs:integer between 0 and 23, both inclusive, representing the value of the hours component in the localized value of arg.
  *
  * <a name="ml-server-type-hours-from-time"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:hours-from-time" target="mlserverdoc">fn:hours-from-time</a> server function.
  * @param arg  The time whose hours component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression hoursFromTime(ServerExpression arg);
/**
  * Returns the value of the implicit timezone property from the dynamic context. Components of the dynamic context are discussed in Section C.2 Dynamic Context Components[XP].
  *
  * <a name="ml-server-type-implicit-timezone"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:implicit-timezone" target="mlserverdoc">fn:implicit-timezone</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dayTimeDuration.html">xs:dayTimeDuration</a> server data type
  */
  public ServerExpression implicitTimezone();
/**
  * Returns the prefixes of the in-scope namespaces for element. For namespaces that have a prefix, it returns the prefix as an xs:NCName. For the default namespace, which has no prefix, it returns the zero-length string.
  *
  * <a name="ml-server-type-in-scope-prefixes"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:in-scope-prefixes" target="mlserverdoc">fn:in-scope-prefixes</a> server function.
  * @param element  The element whose in-scope prefixes will be returned.  (of <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression inScopePrefixes(ServerExpression element);
/**
  * Returns a sequence of positive integers giving the positions within the sequence seqParam of items that are equal to srchParam.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:index-of" target="mlserverdoc">fn:index-of</a> server function.
  * @param seqParam  A sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param srchParam  A value to find on the list.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression indexOf(ServerExpression seqParam, String srchParam);
/**
  * Returns a sequence of positive integers giving the positions within the sequence seqParam of items that are equal to srchParam.
  *
  * <a name="ml-server-type-index-of"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:index-of" target="mlserverdoc">fn:index-of</a> server function.
  * @param seqParam  A sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param srchParam  A value to find on the list.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression indexOf(ServerExpression seqParam, ServerExpression srchParam);
/**
  * Returns a sequence of positive integers giving the positions within the sequence seqParam of items that are equal to srchParam.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:index-of" target="mlserverdoc">fn:index-of</a> server function.
  * @param seqParam  A sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param srchParam  A value to find on the list.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param collationLiteral  A collation identifier.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression indexOf(ServerExpression seqParam, String srchParam, String collationLiteral);
/**
  * Returns a sequence of positive integers giving the positions within the sequence seqParam of items that are equal to srchParam.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:index-of" target="mlserverdoc">fn:index-of</a> server function.
  * @param seqParam  A sequence of values.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param srchParam  A value to find on the list.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param collationLiteral  A collation identifier.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression indexOf(ServerExpression seqParam, ServerExpression srchParam, ServerExpression collationLiteral);
/**
  * Returns a new sequence constructed from the value of target with the value of inserts inserted at the position specified by the value of position. (The value of target is not affected by the sequence construction.)
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:insert-before" target="mlserverdoc">fn:insert-before</a> server function.
  * @param target  The sequence of items into which new items will be inserted.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param position  The position in the target sequence at which the new items will be added.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @param inserts  The items to insert into the target sequence.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression insertBefore(ServerExpression target, long position, ServerExpression inserts);
/**
  * Returns a new sequence constructed from the value of target with the value of inserts inserted at the position specified by the value of position. (The value of target is not affected by the sequence construction.)
  *
  * <a name="ml-server-type-insert-before"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:insert-before" target="mlserverdoc">fn:insert-before</a> server function.
  * @param target  The sequence of items into which new items will be inserted.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param position  The position in the target sequence at which the new items will be added.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @param inserts  The items to insert into the target sequence.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression insertBefore(ServerExpression target, ServerExpression position, ServerExpression inserts);
/**
  * Idempotent function that escapes non-URI characters.
  *
  * <a name="ml-server-type-iri-to-uri"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:iri-to-uri" target="mlserverdoc">fn:iri-to-uri</a> server function.
  * @param uriPart  A string representing an unescaped URI.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression iriToUri(ServerExpression uriPart);
/**
  * This function tests whether the language of node, or the context node if the second argument is omitted, as specified by xml:lang attributes is the same as, or is a sublanguage of, the language specified by testlang. The language of the argument node, or the context node if the second argument is omitted, is determined by the value of the xml:lang attribute on the node, or, if the node has no such attribute, by the value of the xml:lang attribute on the nearest ancestor of the node that has an xml:lang attribute. If there is no such ancestor, then the function returns false
  *
  * <a name="ml-server-type-lang"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:lang" target="mlserverdoc">fn:lang</a> server function.
  * @param testlang  The language against which to test the node.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param node  The node to test.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression lang(ServerExpression testlang, ServerExpression node);
/**
  * Returns the local part of the name of arg as an xs:string that will either be the zero-length string or will have the lexical form of an xs:NCName.
  *
  * <a name="ml-server-type-local-name"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:local-name" target="mlserverdoc">fn:local-name</a> server function.
  * @param arg  The node whose local name is to be returned.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression localName(ServerExpression arg);
/**
  * Returns an xs:NCName representing the local part of arg. If arg is the empty sequence, returns the empty sequence.
  *
  * <a name="ml-server-type-local-name-from-QName"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:local-name-from-QName" target="mlserverdoc">fn:local-name-from-QName</a> server function.
  * @param arg  A qualified name.  (of <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_NCName.html">xs:NCName</a> server data type
  */
  public ServerExpression localNameFromQName(ServerExpression arg);
/**
  * Returns the specified string converting all of the characters to lower-case characters. If a character does not have a corresponding lower-case character, then the original character is returned. The lower-case characters are determined using the Unicode Case Mappings.
  *
  * <a name="ml-server-type-lower-case"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:lower-case" target="mlserverdoc">fn:lower-case</a> server function.
  * @param string  The string to convert.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression lowerCase(ServerExpression string);
/**
  * Returns true if the specified input matches the specified pattern, otherwise returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:matches" target="mlserverdoc">fn:matches</a> server function.
  * @param input  The input from which to match.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param pattern  The regular expression to match.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression matches(ServerExpression input, String pattern);
/**
  * Returns true if the specified input matches the specified pattern, otherwise returns false.
  *
  * <a name="ml-server-type-matches"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:matches" target="mlserverdoc">fn:matches</a> server function.
  * @param input  The input from which to match.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param pattern  The regular expression to match.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression matches(ServerExpression input, ServerExpression pattern);
/**
  * Returns true if the specified input matches the specified pattern, otherwise returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:matches" target="mlserverdoc">fn:matches</a> server function.
  * @param input  The input from which to match.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param pattern  The regular expression to match.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in http://www.w3.org/TR/xpath-functions/#flags.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression matches(ServerExpression input, String pattern, String flags);
/**
  * Returns true if the specified input matches the specified pattern, otherwise returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:matches" target="mlserverdoc">fn:matches</a> server function.
  * @param input  The input from which to match.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param pattern  The regular expression to match.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in http://www.w3.org/TR/xpath-functions/#flags.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression matches(ServerExpression input, ServerExpression pattern, ServerExpression flags);
/**
  * Selects an item from the input sequence arg whose value is greater than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent.
  *
  * <a name="ml-server-type-max"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:max" target="mlserverdoc">fn:max</a> server function.
  * @param arg  The sequence of values whose maximum will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public ServerExpression max(ServerExpression arg);
/**
  * Selects an item from the input sequence arg whose value is greater than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:max" target="mlserverdoc">fn:max</a> server function.
  * @param arg  The sequence of values whose maximum will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public ServerExpression max(ServerExpression arg, String collation);
/**
  * Selects an item from the input sequence arg whose value is greater than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:max" target="mlserverdoc">fn:max</a> server function.
  * @param arg  The sequence of values whose maximum will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public ServerExpression max(ServerExpression arg, ServerExpression collation);
/**
  * Selects an item from the input sequence arg whose value is less than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent.
  *
  * <a name="ml-server-type-min"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:min" target="mlserverdoc">fn:min</a> server function.
  * @param arg  The sequence of values whose minimum will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public ServerExpression min(ServerExpression arg);
/**
  * Selects an item from the input sequence arg whose value is less than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:min" target="mlserverdoc">fn:min</a> server function.
  * @param arg  The sequence of values whose minimum will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public ServerExpression min(ServerExpression arg, String collation);
/**
  * Selects an item from the input sequence arg whose value is less than or equal to the value of every other item in the input sequence. If there are two or more such items, then the specific item whose value is returned is implementation dependent.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:min" target="mlserverdoc">fn:min</a> server function.
  * @param arg  The sequence of values whose minimum will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public ServerExpression min(ServerExpression arg, ServerExpression collation);
/**
  * Returns an xs:integer value between 0 and 59, both inclusive, representing the minute component in the localized value of arg.
  *
  * <a name="ml-server-type-minutes-from-dateTime"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:minutes-from-dateTime" target="mlserverdoc">fn:minutes-from-dateTime</a> server function.
  * @param arg  The dateTime whose minutes component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression minutesFromDateTime(ServerExpression arg);
/**
  * Returns an xs:integer representing the minutes component in the canonical lexical representation of the value of arg. The result may be negative.
  *
  * <a name="ml-server-type-minutes-from-duration"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:minutes-from-duration" target="mlserverdoc">fn:minutes-from-duration</a> server function.
  * @param arg  The duration whose minute component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_duration.html">xs:duration</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression minutesFromDuration(ServerExpression arg);
/**
  * Returns an xs:integer value between 0 to 59, both inclusive, representing the value of the minutes component in the localized value of arg.
  *
  * <a name="ml-server-type-minutes-from-time"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:minutes-from-time" target="mlserverdoc">fn:minutes-from-time</a> server function.
  * @param arg  The time whose minutes component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression minutesFromTime(ServerExpression arg);
/**
  * Returns an xs:integer between 1 and 12, both inclusive, representing the month component in the localized value of arg.
  *
  * <a name="ml-server-type-month-from-date"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:month-from-date" target="mlserverdoc">fn:month-from-date</a> server function.
  * @param arg  The date whose month component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression monthFromDate(ServerExpression arg);
/**
  * Returns an xs:integer between 1 and 12, both inclusive, representing the month component in the localized value of arg.
  *
  * <a name="ml-server-type-month-from-dateTime"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:month-from-dateTime" target="mlserverdoc">fn:month-from-dateTime</a> server function.
  * @param arg  The dateTime whose month component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression monthFromDateTime(ServerExpression arg);
/**
  * Returns an xs:integer representing the months component in the canonical lexical representation of the value of arg. The result may be negative.
  *
  * <a name="ml-server-type-months-from-duration"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:months-from-duration" target="mlserverdoc">fn:months-from-duration</a> server function.
  * @param arg  The duration whose month component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_duration.html">xs:duration</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression monthsFromDuration(ServerExpression arg);
/**
  * Returns the name of a node, as an xs:string that is either the zero-length string, or has the lexical form of an xs:QName.
  *
  * <a name="ml-server-type-name"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:name" target="mlserverdoc">fn:name</a> server function.
  * @param arg  The node whose name is to be returned.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression name(ServerExpression arg);
/**
  * Returns the namespace URI of the xs:QName of the node specified by arg.
  *
  * <a name="ml-server-type-namespace-uri"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:namespace-uri" target="mlserverdoc">fn:namespace-uri</a> server function.
  * @param arg  The node whose namespace URI is to be returned.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyURI.html">xs:anyURI</a> server data type
  */
  public ServerExpression namespaceUri(ServerExpression arg);
/**
  * Returns the namespace URI of one of the in-scope namespaces for element, identified by its namespace prefix.
  *
  * <a name="ml-server-type-namespace-uri-for-prefix"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:namespace-uri-for-prefix" target="mlserverdoc">fn:namespace-uri-for-prefix</a> server function.
  * @param prefix  A namespace prefix to look up.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param element  An element node providing namespace context.  (of <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyURI.html">xs:anyURI</a> server data type
  */
  public ServerExpression namespaceUriForPrefix(ServerExpression prefix, ServerExpression element);
/**
  * Returns the namespace URI for arg as an xs:string. If arg is the empty sequence, the empty sequence is returned. If arg is in no namespace, the zero-length string is returned.
  *
  * <a name="ml-server-type-namespace-uri-from-QName"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:namespace-uri-from-QName" target="mlserverdoc">fn:namespace-uri-from-QName</a> server function.
  * @param arg  A qualified name.  (of <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyURI.html">xs:anyURI</a> server data type
  */
  public ServerExpression namespaceUriFromQName(ServerExpression arg);
/**
  * Summary: Returns an xs:boolean indicating whether the argument node is "nilled". If the argument is not an element node, returns the empty sequence. If the argument is the empty sequence, returns the empty sequence. For element nodes, true() is returned if the element is nilled, otherwise false().
  *
  * <a name="ml-server-type-nilled"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:nilled" target="mlserverdoc">fn:nilled</a> server function.
  * @param arg  The node to test for nilled status.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression nilled(ServerExpression arg);
/**
  * Returns an expanded-QName for node kinds that can have names. For other kinds of nodes it returns the empty sequence. If arg is the empty sequence, the empty sequence is returned.
  *
  * <a name="ml-server-type-node-name"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:node-name" target="mlserverdoc">fn:node-name</a> server function.
  * @param arg  The node whose name is to be returned.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a> server data type
  */
  public ServerExpression nodeName(ServerExpression arg);
/**
  * Returns the specified string with normalized whitespace, which strips off any leading or trailing whitespace and replaces any other sequences of more than one whitespace characters with a single space character (#x20).
  *
  * <a name="ml-server-type-normalize-space"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:normalize-space" target="mlserverdoc">fn:normalize-space</a> server function.
  * @param input  The string from which to normalize whitespace.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression normalizeSpace(ServerExpression input);
/**
  * Return the argument normalized according to the normalization criteria for a normalization form identified by the value of normalizationForm. The effective value of the normalizationForm is computed by removing leading and trailing blanks, if present, and converting to upper case.
  *
  * <a name="ml-server-type-normalize-unicode"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:normalize-unicode" target="mlserverdoc">fn:normalize-unicode</a> server function.
  * @param arg  The string to normalize.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression normalizeUnicode(ServerExpression arg);
/**
  * Return the argument normalized according to the normalization criteria for a normalization form identified by the value of normalizationForm. The effective value of the normalizationForm is computed by removing leading and trailing blanks, if present, and converting to upper case.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:normalize-unicode" target="mlserverdoc">fn:normalize-unicode</a> server function.
  * @param arg  The string to normalize.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param normalizationForm  The form under which to normalize the specified string: NFC, NFD, NFKC, or NFKD.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression normalizeUnicode(ServerExpression arg, String normalizationForm);
/**
  * Return the argument normalized according to the normalization criteria for a normalization form identified by the value of normalizationForm. The effective value of the normalizationForm is computed by removing leading and trailing blanks, if present, and converting to upper case.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:normalize-unicode" target="mlserverdoc">fn:normalize-unicode</a> server function.
  * @param arg  The string to normalize.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param normalizationForm  The form under which to normalize the specified string: NFC, NFD, NFKC, or NFKD.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression normalizeUnicode(ServerExpression arg, ServerExpression normalizationForm);
/**
  * Returns true if the effective boolean value is false, and false if the effective boolean value is true. The arg parameter is first reduced to an effective boolean value by applying the fn:boolean function.
  *
  * <a name="ml-server-type-not"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:not" target="mlserverdoc">fn:not</a> server function.
  * @param arg  The expression to negate.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression not(ServerExpression arg);
/**
  * Returns the value indicated by arg or, if arg is not specified, the context item after atomization, converted to an xs:double. If arg is the empty sequence or if arg or the context item cannot be converted to an xs:double, the xs:double value NaN is returned. If the context item is undefined an error is raised: [err:XPDY0002].
  *
  * <a name="ml-server-type-number"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:number" target="mlserverdoc">fn:number</a> server function.
  * @param arg  The value to be returned as an xs:double value.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression number(ServerExpression arg);
/**
  * Returns an xs:NCName representing the prefix of arg. The empty sequence is returned if arg is the empty sequence or if the value of arg contains no prefix.
  *
  * <a name="ml-server-type-prefix-from-QName"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:prefix-from-QName" target="mlserverdoc">fn:prefix-from-QName</a> server function.
  * @param arg  A qualified name.  (of <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_NCName.html">xs:NCName</a> server data type
  */
  public ServerExpression prefixFromQName(ServerExpression arg);
/**
  * Returns an xs:QName with the namespace URI given in paramURI. If paramURI is the zero-length string or the empty sequence, it represents "no namespace"; in this case, if the value of paramQName contains a colon (:), an error is raised [err:FOCA0002]. The prefix (or absence of a prefix) in paramQName is retained in the returned xs:QName value. The local name in the result is taken from the local part of paramQName.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:QName" target="mlserverdoc">fn:QName</a> server function.
  * @param paramURI  A namespace URI, as a string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param paramQName  A lexical qualified name (xs:QName), a string of the form "prefix:localname" or "localname".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a> server data type
  */
  public ServerExpression QName(ServerExpression paramURI, String paramQName);
/**
  * Returns an xs:QName with the namespace URI given in paramURI. If paramURI is the zero-length string or the empty sequence, it represents "no namespace"; in this case, if the value of paramQName contains a colon (:), an error is raised [err:FOCA0002]. The prefix (or absence of a prefix) in paramQName is retained in the returned xs:QName value. The local name in the result is taken from the local part of paramQName.
  *
  * <a name="ml-server-type-QName"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:QName" target="mlserverdoc">fn:QName</a> server function.
  * @param paramURI  A namespace URI, as a string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param paramQName  A lexical qualified name (xs:QName), a string of the form "prefix:localname" or "localname".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a> server data type
  */
  public ServerExpression QName(ServerExpression paramURI, ServerExpression paramQName);
/**
  * Returns a new sequence constructed from the value of target with the item at the position specified by the value of position removed.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:remove" target="mlserverdoc">fn:remove</a> server function.
  * @param target  The sequence of items from which items will be removed.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param position  The position in the target sequence from which the items will be removed.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression remove(ServerExpression target, long position);
/**
  * Returns a new sequence constructed from the value of target with the item at the position specified by the value of position removed.
  *
  * <a name="ml-server-type-remove"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:remove" target="mlserverdoc">fn:remove</a> server function.
  * @param target  The sequence of items from which items will be removed.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param position  The position in the target sequence from which the items will be removed.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression remove(ServerExpression target, ServerExpression position);
/**
  * Returns a string constructed by replacing the specified pattern on the input string with the specified replacement string.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:replace" target="mlserverdoc">fn:replace</a> server function.
  * @param input  The string to start with.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param pattern  The regular expression pattern to match. If the pattern does not match the $input string, the function will return the $input string unchanged.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param replacement  The regular expression pattern to replace the $pattern with. It can also be a capture expression (for more details, see http://www.w3.org/TR/xpath-functions/#func-replace).  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression replace(ServerExpression input, String pattern, String replacement);
/**
  * Returns a string constructed by replacing the specified pattern on the input string with the specified replacement string.
  *
  * <a name="ml-server-type-replace"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:replace" target="mlserverdoc">fn:replace</a> server function.
  * @param input  The string to start with.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param pattern  The regular expression pattern to match. If the pattern does not match the $input string, the function will return the $input string unchanged.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param replacement  The regular expression pattern to replace the $pattern with. It can also be a capture expression (for more details, see http://www.w3.org/TR/xpath-functions/#func-replace).  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression replace(ServerExpression input, ServerExpression pattern, ServerExpression replacement);
/**
  * Returns a string constructed by replacing the specified pattern on the input string with the specified replacement string.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:replace" target="mlserverdoc">fn:replace</a> server function.
  * @param input  The string to start with.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param pattern  The regular expression pattern to match. If the pattern does not match the $input string, the function will return the $input string unchanged.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param replacement  The regular expression pattern to replace the $pattern with. It can also be a capture expression (for more details, see http://www.w3.org/TR/xpath-functions/#func-replace).  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in http://www.w3.org/TR/xpath-functions/#flags.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression replace(ServerExpression input, String pattern, String replacement, String flags);
/**
  * Returns a string constructed by replacing the specified pattern on the input string with the specified replacement string.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:replace" target="mlserverdoc">fn:replace</a> server function.
  * @param input  The string to start with.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param pattern  The regular expression pattern to match. If the pattern does not match the $input string, the function will return the $input string unchanged.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param replacement  The regular expression pattern to replace the $pattern with. It can also be a capture expression (for more details, see http://www.w3.org/TR/xpath-functions/#func-replace).  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in http://www.w3.org/TR/xpath-functions/#flags.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression replace(ServerExpression input, ServerExpression pattern, ServerExpression replacement, ServerExpression flags);
/**
  * Returns an xs:QName value (that is, an expanded QName) by taking an xs:string that has the lexical form of an xs:QName (a string in the form "prefix:local-name" or "local-name") and resolving it using the in-scope namespaces for a given element.
  *
  * <a name="ml-server-type-resolve-QName"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:resolve-QName" target="mlserverdoc">fn:resolve-QName</a> server function.
  * @param qname  A string of the form "prefix:local-name".  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param element  An element providing the in-scope namespaces to use to resolve the qualified name.  (of <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_QName.html">xs:QName</a> server data type
  */
  public ServerExpression resolveQName(ServerExpression qname, ServerExpression element);
/**
  * Resolves a relative URI against an absolute URI. If base is specified, the URI is resolved relative to that base. If base is not specified, the base is set to the base-uri property from the static context, if the property exists; if it does not exist, an error is thrown.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:resolve-uri" target="mlserverdoc">fn:resolve-uri</a> server function.
  * @param relative  A URI reference to resolve against the base.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param base  An absolute URI to use as the base of the resolution.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyURI.html">xs:anyURI</a> server data type
  */
  public ServerExpression resolveUri(ServerExpression relative, String base);
/**
  * Resolves a relative URI against an absolute URI. If base is specified, the URI is resolved relative to that base. If base is not specified, the base is set to the base-uri property from the static context, if the property exists; if it does not exist, an error is thrown.
  *
  * <a name="ml-server-type-resolve-uri"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:resolve-uri" target="mlserverdoc">fn:resolve-uri</a> server function.
  * @param relative  A URI reference to resolve against the base.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param base  An absolute URI to use as the base of the resolution.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyURI.html">xs:anyURI</a> server data type
  */
  public ServerExpression resolveUri(ServerExpression relative, ServerExpression base);
/**
  * Reverses the order of items in a sequence. If $arg is the empty sequence, the empty sequence is returned.
  *
  * <a name="ml-server-type-reverse"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:reverse" target="mlserverdoc">fn:reverse</a> server function.
  * @param target  The sequence of items to be reversed.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression reverse(ServerExpression target);
/**
  * Returns the root of the tree to which arg belongs. This will usually, but not necessarily, be a document node.
  *
  * <a name="ml-server-type-root"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:root" target="mlserverdoc">fn:root</a> server function.
  * @param arg  The node whose root node will be returned.  (of <a href="{@docRoot}/doc-files/types/node.html">node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/node.html">node</a> server data type
  */
  public ServerExpression root(ServerExpression arg);
/**
  * Returns the number with no fractional part that is closest to the argument. If there are two such numbers, then the one that is closest to positive infinity is returned. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type.
  *
  * <a name="ml-server-type-round"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:round" target="mlserverdoc">fn:round</a> server function.
  * @param arg  A numeric value to round.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public ServerExpression round(ServerExpression arg);
/**
  * The value returned is the nearest (that is, numerically closest) numeric to arg that is a multiple of ten to the power of minus precision. If two such values are equally near (e.g. if the fractional part in arg is exactly .500...), returns the one whose least significant digit is even. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type.
  *
  * <a name="ml-server-type-round-half-to-even"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:round-half-to-even" target="mlserverdoc">fn:round-half-to-even</a> server function.
  * @param arg  A numeric value to round.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public ServerExpression roundHalfToEven(ServerExpression arg);
/**
  * The value returned is the nearest (that is, numerically closest) numeric to arg that is a multiple of ten to the power of minus precision. If two such values are equally near (e.g. if the fractional part in arg is exactly .500...), returns the one whose least significant digit is even. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:round-half-to-even" target="mlserverdoc">fn:round-half-to-even</a> server function.
  * @param arg  A numeric value to round.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param precision  The precision to which to round the value.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public ServerExpression roundHalfToEven(ServerExpression arg, long precision);
/**
  * The value returned is the nearest (that is, numerically closest) numeric to arg that is a multiple of ten to the power of minus precision. If two such values are equally near (e.g. if the fractional part in arg is exactly .500...), returns the one whose least significant digit is even. If type of arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type of the result is the same as the type of arg. If the type of arg is a type derived from one of the numeric types, the result is an instance of the base numeric type.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:round-half-to-even" target="mlserverdoc">fn:round-half-to-even</a> server function.
  * @param arg  A numeric value to round.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param precision  The precision to which to round the value.  (of <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a> server data type
  */
  public ServerExpression roundHalfToEven(ServerExpression arg, ServerExpression precision);
/**
  * Returns an xs:decimal value between 0 and 60.999..., both inclusive representing the seconds and fractional seconds in the localized value of arg. Note that the value can be greater than 60 seconds to accommodate occasional leap seconds used to keep human time synchronized with the rotation of the planet.
  *
  * <a name="ml-server-type-seconds-from-dateTime"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:seconds-from-dateTime" target="mlserverdoc">fn:seconds-from-dateTime</a> server function.
  * @param arg  The dateTime whose seconds component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_decimal.html">xs:decimal</a> server data type
  */
  public ServerExpression secondsFromDateTime(ServerExpression arg);
/**
  * Returns an xs:decimal representing the seconds component in the canonical lexical representation of the value of arg. The result may be negative.
  *
  * <a name="ml-server-type-seconds-from-duration"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:seconds-from-duration" target="mlserverdoc">fn:seconds-from-duration</a> server function.
  * @param arg  The duration whose minute component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_duration.html">xs:duration</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_decimal.html">xs:decimal</a> server data type
  */
  public ServerExpression secondsFromDuration(ServerExpression arg);
/**
  * Returns an xs:decimal value between 0 and 60.999..., both inclusive, representing the seconds and fractional seconds in the localized value of arg. Note that the value can be greater than 60 seconds to accommodate occasional leap seconds used to keep human time synchronized with the rotation of the planet.
  *
  * <a name="ml-server-type-seconds-from-time"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:seconds-from-time" target="mlserverdoc">fn:seconds-from-time</a> server function.
  * @param arg  The time whose seconds component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_decimal.html">xs:decimal</a> server data type
  */
  public ServerExpression secondsFromTime(ServerExpression arg);
/**
  * Returns true if the first parameter starts with the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:starts-with" target="mlserverdoc">fn:starts-with</a> server function.
  * @param parameter1  The string from which to test.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param parameter2  The string to test whether it is at the beginning of the first parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression startsWith(ServerExpression parameter1, String parameter2);
/**
  * Returns true if the first parameter starts with the string from the second parameter, otherwise returns false.
  *
  * <a name="ml-server-type-starts-with"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:starts-with" target="mlserverdoc">fn:starts-with</a> server function.
  * @param parameter1  The string from which to test.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param parameter2  The string to test whether it is at the beginning of the first parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression startsWith(ServerExpression parameter1, ServerExpression parameter2);
/**
  * Returns true if the first parameter starts with the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:starts-with" target="mlserverdoc">fn:starts-with</a> server function.
  * @param parameter1  The string from which to test.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param parameter2  The string to test whether it is at the beginning of the first parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression startsWith(ServerExpression parameter1, String parameter2, String collation);
/**
  * Returns true if the first parameter starts with the string from the second parameter, otherwise returns false.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:starts-with" target="mlserverdoc">fn:starts-with</a> server function.
  * @param parameter1  The string from which to test.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param parameter2  The string to test whether it is at the beginning of the first parameter.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression startsWith(ServerExpression parameter1, ServerExpression parameter2, ServerExpression collation);
/**
  * Returns the value of arg represented as an xs:string. If no argument is supplied, this function returns the string value of the context item (.).
  *
  * <a name="ml-server-type-string"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:string" target="mlserverdoc">fn:string</a> server function.
  * @param arg  The item to be rendered as a string.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression string(ServerExpression arg);
/**
  * Returns an xs:string created by concatenating the members of the parameter1 sequence using parameter2 as a separator. If the value of $arg2 is the zero-length string, then the members of parameter1 are concatenated without a separator.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:string-join" target="mlserverdoc">fn:string-join</a> server function.
  * @param parameter1  A sequence of strings.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param parameter2  A separator string to concatenate between the items in $parameter1.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression stringJoin(ServerExpression parameter1, String parameter2);
/**
  * Returns an xs:string created by concatenating the members of the parameter1 sequence using parameter2 as a separator. If the value of $arg2 is the zero-length string, then the members of parameter1 are concatenated without a separator.
  *
  * <a name="ml-server-type-string-join"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:string-join" target="mlserverdoc">fn:string-join</a> server function.
  * @param parameter1  A sequence of strings.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param parameter2  A separator string to concatenate between the items in $parameter1.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression stringJoin(ServerExpression parameter1, ServerExpression parameter2);
/**
  * Returns an integer representing the length of the specified string. The length is 1-based, so a string that is one character long returns a value of 1.
  *
  * <a name="ml-server-type-string-length"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:string-length" target="mlserverdoc">fn:string-length</a> server function.
  * @param sourceString  The string to calculate the length.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression stringLength(ServerExpression sourceString);
/**
  * Returns the sequence of Unicode code points that constitute an xs:string. If arg is a zero-length string or the empty sequence, the empty sequence is returned.
  *
  * <a name="ml-server-type-string-to-codepoints"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:string-to-codepoints" target="mlserverdoc">fn:string-to-codepoints</a> server function.
  * @param arg  A string.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression stringToCodepoints(ServerExpression arg);
/**
  * Returns the contiguous sequence of items in the value of sourceSeq beginning at the position indicated by the value of startingLoc and continuing for the number of items indicated by the value of length.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:subsequence" target="mlserverdoc">fn:subsequence</a> server function.
  * @param sourceSeq  The sequence of items from which a subsequence will be selected.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param startingLoc  The starting position of the start of the subsequence.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression subsequence(ServerExpression sourceSeq, double startingLoc);
/**
  * Returns the contiguous sequence of items in the value of sourceSeq beginning at the position indicated by the value of startingLoc and continuing for the number of items indicated by the value of length.
  *
  * <a name="ml-server-type-subsequence"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:subsequence" target="mlserverdoc">fn:subsequence</a> server function.
  * @param sourceSeq  The sequence of items from which a subsequence will be selected.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param startingLoc  The starting position of the start of the subsequence.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression subsequence(ServerExpression sourceSeq, ServerExpression startingLoc);
/**
  * Returns the contiguous sequence of items in the value of sourceSeq beginning at the position indicated by the value of startingLoc and continuing for the number of items indicated by the value of length.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:subsequence" target="mlserverdoc">fn:subsequence</a> server function.
  * @param sourceSeq  The sequence of items from which a subsequence will be selected.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param startingLoc  The starting position of the start of the subsequence.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param length  The length of the subsequence.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression subsequence(ServerExpression sourceSeq, double startingLoc, double length);
/**
  * Returns the contiguous sequence of items in the value of sourceSeq beginning at the position indicated by the value of startingLoc and continuing for the number of items indicated by the value of length.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:subsequence" target="mlserverdoc">fn:subsequence</a> server function.
  * @param sourceSeq  The sequence of items from which a subsequence will be selected.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param startingLoc  The starting position of the start of the subsequence.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param length  The length of the subsequence.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression subsequence(ServerExpression sourceSeq, ServerExpression startingLoc, ServerExpression length);
/**
  * Returns a substring starting from the startingLoc and continuing for length characters.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:substring" target="mlserverdoc">fn:substring</a> server function.
  * @param sourceString  The string from which to create a substring.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param startingLoc  The number of characters from the start of the $sourceString.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression substring(ServerExpression sourceString, double startingLoc);
/**
  * Returns a substring starting from the startingLoc and continuing for length characters.
  *
  * <a name="ml-server-type-substring"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:substring" target="mlserverdoc">fn:substring</a> server function.
  * @param sourceString  The string from which to create a substring.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param startingLoc  The number of characters from the start of the $sourceString.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression substring(ServerExpression sourceString, ServerExpression startingLoc);
/**
  * Returns a substring starting from the startingLoc and continuing for length characters.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:substring" target="mlserverdoc">fn:substring</a> server function.
  * @param sourceString  The string from which to create a substring.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param startingLoc  The number of characters from the start of the $sourceString.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param length  The number of characters beyond the $startingLoc.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression substring(ServerExpression sourceString, double startingLoc, double length);
/**
  * Returns a substring starting from the startingLoc and continuing for length characters.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:substring" target="mlserverdoc">fn:substring</a> server function.
  * @param sourceString  The string from which to create a substring.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param startingLoc  The number of characters from the start of the $sourceString.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param length  The number of characters beyond the $startingLoc.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression substring(ServerExpression sourceString, ServerExpression startingLoc, ServerExpression length);
/**
  * Returns the substring created by taking all of the input characters that occur after the specified after characters.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:substring-after" target="mlserverdoc">fn:substring-after</a> server function.
  * @param input  The string from which to create the substring.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param after  The string after which the substring is created.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression substringAfter(ServerExpression input, String after);
/**
  * Returns the substring created by taking all of the input characters that occur after the specified after characters.
  *
  * <a name="ml-server-type-substring-after"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:substring-after" target="mlserverdoc">fn:substring-after</a> server function.
  * @param input  The string from which to create the substring.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param after  The string after which the substring is created.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression substringAfter(ServerExpression input, ServerExpression after);
/**
  * Returns the substring created by taking all of the input characters that occur after the specified after characters.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:substring-after" target="mlserverdoc">fn:substring-after</a> server function.
  * @param input  The string from which to create the substring.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param after  The string after which the substring is created.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression substringAfter(ServerExpression input, String after, String collation);
/**
  * Returns the substring created by taking all of the input characters that occur after the specified after characters.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:substring-after" target="mlserverdoc">fn:substring-after</a> server function.
  * @param input  The string from which to create the substring.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param after  The string after which the substring is created.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression substringAfter(ServerExpression input, ServerExpression after, ServerExpression collation);
/**
  * Returns the substring created by taking all of the input characters that occur before the specified before characters.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:substring-before" target="mlserverdoc">fn:substring-before</a> server function.
  * @param input  The string from which to create the substring.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param before  The string before which the substring is created.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression substringBefore(ServerExpression input, String before);
/**
  * Returns the substring created by taking all of the input characters that occur before the specified before characters.
  *
  * <a name="ml-server-type-substring-before"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:substring-before" target="mlserverdoc">fn:substring-before</a> server function.
  * @param input  The string from which to create the substring.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param before  The string before which the substring is created.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression substringBefore(ServerExpression input, ServerExpression before);
/**
  * Returns the substring created by taking all of the input characters that occur before the specified before characters.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:substring-before" target="mlserverdoc">fn:substring-before</a> server function.
  * @param input  The string from which to create the substring.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param before  The string before which the substring is created.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression substringBefore(ServerExpression input, String before, String collation);
/**
  * Returns the substring created by taking all of the input characters that occur before the specified before characters.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:substring-before" target="mlserverdoc">fn:substring-before</a> server function.
  * @param input  The string from which to create the substring.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param before  The string before which the substring is created.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param collation  The optional name of a valid collation URI. For information on the collation URI syntax, see the Search Developer's Guide.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression substringBefore(ServerExpression input, ServerExpression before, ServerExpression collation);
/**
  * Returns a value obtained by adding together the values in arg. If zero is not specified, then the value returned for an empty sequence is the xs:integer value 0. If zero is specified, then the value returned for an empty sequence is zero.
  *
  * <a name="ml-server-type-sum"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:sum" target="mlserverdoc">fn:sum</a> server function.
  * @param arg  The sequence of values to be summed.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public ServerExpression sum(ServerExpression arg);
/**
  * Returns a value obtained by adding together the values in arg. If zero is not specified, then the value returned for an empty sequence is the xs:integer value 0. If zero is specified, then the value returned for an empty sequence is zero.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:sum" target="mlserverdoc">fn:sum</a> server function.
  * @param arg  The sequence of values to be summed.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param zero  The value to return as zero if the input sequence is the empty sequence. This parameter is not available in the 0.9-ml XQuery dialect.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public ServerExpression sum(ServerExpression arg, String zero);
/**
  * Returns a value obtained by adding together the values in arg. If zero is not specified, then the value returned for an empty sequence is the xs:integer value 0. If zero is specified, then the value returned for an empty sequence is zero.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:sum" target="mlserverdoc">fn:sum</a> server function.
  * @param arg  The sequence of values to be summed.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @param zero  The value to return as zero if the input sequence is the empty sequence. This parameter is not available in the 0.9-ml XQuery dialect.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a> server data type
  */
  public ServerExpression sum(ServerExpression arg, ServerExpression zero);
/**
  * Returns all but the first item in a sequence. For more details, see XPath 3.0 Functions and Operators.
  *
  * <a name="ml-server-type-tail"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:tail" target="mlserverdoc">fn:tail</a> server function.
  * @param seq  The function value.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression tail(ServerExpression seq);
/**
  * Returns the timezone component of arg if any. If arg has a timezone component, then the result is an xs:dayTimeDuration that indicates deviation from UTC; its value may range from +14:00 to -14:00 hours, both inclusive. Otherwise, the result is the empty sequence.
  *
  * <a name="ml-server-type-timezone-from-date"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:timezone-from-date" target="mlserverdoc">fn:timezone-from-date</a> server function.
  * @param arg  The date whose timezone component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dayTimeDuration.html">xs:dayTimeDuration</a> server data type
  */
  public ServerExpression timezoneFromDate(ServerExpression arg);
/**
  * Returns the timezone component of arg if any. If arg has a timezone component, then the result is an xs:dayTimeDuration that indicates deviation from UTC; its value may range from +14:00 to -14:00 hours, both inclusive. Otherwise, the result is the empty sequence.
  *
  * <a name="ml-server-type-timezone-from-dateTime"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:timezone-from-dateTime" target="mlserverdoc">fn:timezone-from-dateTime</a> server function.
  * @param arg  The dateTime whose timezone component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dayTimeDuration.html">xs:dayTimeDuration</a> server data type
  */
  public ServerExpression timezoneFromDateTime(ServerExpression arg);
/**
  * Returns the timezone component of arg if any. If arg has a timezone component, then the result is an xs:dayTimeDuration that indicates deviation from UTC; its value may range from +14:00 to -14:00 hours, both inclusive. Otherwise, the result is the empty sequence.
  *
  * <a name="ml-server-type-timezone-from-time"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:timezone-from-time" target="mlserverdoc">fn:timezone-from-time</a> server function.
  * @param arg  The time whose timezone component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_time.html">xs:time</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_dayTimeDuration.html">xs:dayTimeDuration</a> server data type
  */
  public ServerExpression timezoneFromTime(ServerExpression arg);
/**
  * Returns a sequence of strings constructed by breaking the specified input into substrings separated by the specified pattern. The specified pattern is not returned as part of the returned items.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:tokenize" target="mlserverdoc">fn:tokenize</a> server function.
  * @param input  The string to tokenize.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param pattern  The regular expression pattern from which to separate the tokens.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression tokenize(ServerExpression input, String pattern);
/**
  * Returns a sequence of strings constructed by breaking the specified input into substrings separated by the specified pattern. The specified pattern is not returned as part of the returned items.
  *
  * <a name="ml-server-type-tokenize"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:tokenize" target="mlserverdoc">fn:tokenize</a> server function.
  * @param input  The string to tokenize.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param pattern  The regular expression pattern from which to separate the tokens.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression tokenize(ServerExpression input, ServerExpression pattern);
/**
  * Returns a sequence of strings constructed by breaking the specified input into substrings separated by the specified pattern. The specified pattern is not returned as part of the returned items.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:tokenize" target="mlserverdoc">fn:tokenize</a> server function.
  * @param input  The string to tokenize.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param pattern  The regular expression pattern from which to separate the tokens.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in http://www.w3.org/TR/xpath-functions/#flags.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression tokenize(ServerExpression input, String pattern, String flags);
/**
  * Returns a sequence of strings constructed by breaking the specified input into substrings separated by the specified pattern. The specified pattern is not returned as part of the returned items.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:tokenize" target="mlserverdoc">fn:tokenize</a> server function.
  * @param input  The string to tokenize.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param pattern  The regular expression pattern from which to separate the tokens.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param flags  The flag representing how to interpret the regular expression. One of "s", "m", "i", or "x", as defined in http://www.w3.org/TR/xpath-functions/#flags.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression tokenize(ServerExpression input, ServerExpression pattern, ServerExpression flags);
/**
  * Returns a string where every character in src that occurs in some position in the mapString is translated into the transString character in the corresponding location of the mapString character.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:translate" target="mlserverdoc">fn:translate</a> server function.
  * @param src  The string to translate characters.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param mapString  The string representing characters to be translated.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param transString  The string representing the characters to which the $mapString characters are translated.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression translate(ServerExpression src, String mapString, String transString);
/**
  * Returns a string where every character in src that occurs in some position in the mapString is translated into the transString character in the corresponding location of the mapString character.
  *
  * <a name="ml-server-type-translate"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:translate" target="mlserverdoc">fn:translate</a> server function.
  * @param src  The string to translate characters.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param mapString  The string representing characters to be translated.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param transString  The string representing the characters to which the $mapString characters are translated.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression translate(ServerExpression src, ServerExpression mapString, ServerExpression transString);
/**
  * Returns the xs:boolean value true. Equivalent to xs:boolean("1").
  *
  * <a name="ml-server-type-true"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:true" target="mlserverdoc">fn:true</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a> server data type
  */
  public ServerExpression trueExpr();
/**
  * Returns the items of sourceSeq in an implementation dependent order.
  *
  * <a name="ml-server-type-unordered"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:unordered" target="mlserverdoc">fn:unordered</a> server function.
  * @param sourceSeq  The sequence of items.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression unordered(ServerExpression sourceSeq);
/**
  * Returns the specified string converting all of the characters to upper-case characters. If a character does not have a corresponding upper-case character, then the original character is returned. The upper-case characters are determined using the Unicode Case Mappings.
  *
  * <a name="ml-server-type-upper-case"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:upper-case" target="mlserverdoc">fn:upper-case</a> server function.
  * @param string  The string to upper-case.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression upperCase(ServerExpression string);
/**
  * Returns an xs:integer representing the year component in the localized value of arg. The result may be negative.
  *
  * <a name="ml-server-type-year-from-date"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:year-from-date" target="mlserverdoc">fn:year-from-date</a> server function.
  * @param arg  The date whose year component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_date.html">xs:date</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression yearFromDate(ServerExpression arg);
/**
  * Returns an xs:integer representing the year component in the localized value of arg. The result may be negative.
  *
  * <a name="ml-server-type-year-from-dateTime"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:year-from-dateTime" target="mlserverdoc">fn:year-from-dateTime</a> server function.
  * @param arg  The dateTime whose year component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_dateTime.html">xs:dateTime</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression yearFromDateTime(ServerExpression arg);
/**
  * Returns an xs:integer representing the years component in the canonical lexical representation of the value of arg. The result may be negative.
  *
  * <a name="ml-server-type-years-from-duration"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/fn:years-from-duration" target="mlserverdoc">fn:years-from-duration</a> server function.
  * @param arg  The duration whose year component will be returned.  (of <a href="{@docRoot}/doc-files/types/xs_duration.html">xs:duration</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_integer.html">xs:integer</a> server data type
  */
  public ServerExpression yearsFromDuration(ServerExpression arg);
}
