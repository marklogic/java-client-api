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

import com.marklogic.client.type.ItemExpr;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.XsDecimalExpr;
import com.marklogic.client.type.XsIntegerExpr;
import com.marklogic.client.type.XsIntExpr;
import com.marklogic.client.type.XsNumericExpr;
import com.marklogic.client.type.XsStringExpr;
import com.marklogic.client.type.XsUnsignedIntExpr;
import com.marklogic.client.type.XsUnsignedLongExpr;



// IMPORTANT: Do not edit. This file is generated. 

/**
 * Builds expressions to call functions in the sql server library for a row
 * pipeline.
 */
public interface SqlExpr {
    /**
    * Returns the length of the string "str" in bits.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:bit-length" target="mlserverdoc">sql:bit-length</a>
    * @param str  The string to be evaluated.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr bitLength(XsStringExpr str);
    /**
    * Returns an <code>rdf:collatedString</code> value with the given value and collation tag. The <code>rdf:collatedString</code> type extends <code>xs:string</code> , and represents a collation tagged string in RDF. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:collated-string" target="mlserverdoc">sql:collated-string</a>
    * @param string  The lexical value.
    * @param collationURI  The collation URI.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr collatedString(XsStringExpr string, String collationURI);
    /**
    * Returns an <code>rdf:collatedString</code> value with the given value and collation tag. The <code>rdf:collatedString</code> type extends <code>xs:string</code> , and represents a collation tagged string in RDF. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:collated-string" target="mlserverdoc">sql:collated-string</a>
    * @param string  The lexical value.
    * @param collationURI  The collation URI.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr collatedString(XsStringExpr string, XsStringExpr collationURI);
    /**
    * Returns a specified date with the specified number interval (signed integer) added to a specified datepart of that date 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:dateadd" target="mlserverdoc">sql:dateadd</a>
    * @param datepart  Is the part of date where the number will be added. The following table lists all valid datepart arguments. User-defined variable equivalents are not valid. The return data type is the data type of the date argument. <p>Options:</p><p> <code>datepart</code> parameter abbreviation includes:</p> <blockquote><dl> <dt>"year","yyyy","yy"</dt> <dd>The year part of the <code>date</code></dd> <dt>"quarter","qq","q"</dt> <dd>The quarter part of the <code>date</code></dd> <dt>"month","mm","m"</dt> <dd>The month part of the <code>date</code></dd> <dt>"dayofyear","dy","y"</dt> <dd>The day of the year from the <code>date</code></dd> <dt>"day","dd","d"</dt> <dd>The day of the month from the <code>date</code></dd> <dt>"week","wk","ww"</dt> <dd>The week of the year from the <code>date</code></dd> <dt>"weekday","dw"</dt> <dd>The day of the week from the <code>date</code></dd> <dt>"hour","hh"</dt> <dd>The hour of the day from the <code>date</code></dd> <dt>"minute","mi","n"</dt> <dd>The minute of the hour from the <code>date</code></dd> <dt>"second","ss","s"</dt> <dd>The second of the minute from the <code>date</code></dd> <dt>"millisecond","ms"</dt> <dd>The millisecond of the minute from the <code>date</code></dd> <dt>"microsecond","msc"</dt> <dd>The microsecond of the minute from the <code>date</code></dd> <dt>"nanosecond","ns"</dt> <dd>The nanosecond of the minute from the <code>date</code></dd> </dl></blockquote>
    * @param number  This number will be added to the datepart of the given date.
    * @param date  Is an expression that can be resolved to a time, date or datetime, value. date can be an expression, column expression, user-defined variable or string literal. startdate is subtracted from enddate.
    * @return  a ItemExpr expression
    */
    public ItemExpr dateadd(XsStringExpr datepart, int number, ItemExpr date);
    /**
    * Returns a specified date with the specified number interval (signed integer) added to a specified datepart of that date 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:dateadd" target="mlserverdoc">sql:dateadd</a>
    * @param datepart  Is the part of date where the number will be added. The following table lists all valid datepart arguments. User-defined variable equivalents are not valid. The return data type is the data type of the date argument. <p>Options:</p><p> <code>datepart</code> parameter abbreviation includes:</p> <blockquote><dl> <dt>"year","yyyy","yy"</dt> <dd>The year part of the <code>date</code></dd> <dt>"quarter","qq","q"</dt> <dd>The quarter part of the <code>date</code></dd> <dt>"month","mm","m"</dt> <dd>The month part of the <code>date</code></dd> <dt>"dayofyear","dy","y"</dt> <dd>The day of the year from the <code>date</code></dd> <dt>"day","dd","d"</dt> <dd>The day of the month from the <code>date</code></dd> <dt>"week","wk","ww"</dt> <dd>The week of the year from the <code>date</code></dd> <dt>"weekday","dw"</dt> <dd>The day of the week from the <code>date</code></dd> <dt>"hour","hh"</dt> <dd>The hour of the day from the <code>date</code></dd> <dt>"minute","mi","n"</dt> <dd>The minute of the hour from the <code>date</code></dd> <dt>"second","ss","s"</dt> <dd>The second of the minute from the <code>date</code></dd> <dt>"millisecond","ms"</dt> <dd>The millisecond of the minute from the <code>date</code></dd> <dt>"microsecond","msc"</dt> <dd>The microsecond of the minute from the <code>date</code></dd> <dt>"nanosecond","ns"</dt> <dd>The nanosecond of the minute from the <code>date</code></dd> </dl></blockquote>
    * @param number  This number will be added to the datepart of the given date.
    * @param date  Is an expression that can be resolved to a time, date or datetime, value. date can be an expression, column expression, user-defined variable or string literal. startdate is subtracted from enddate.
    * @return  a ItemExpr expression
    */
    public ItemExpr dateadd(XsStringExpr datepart, XsIntExpr number, ItemExpr date);
    /**
    * Returns the count (signed integer) of the specified datepart boundaries crossed between the specified startdate and enddate. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:datediff" target="mlserverdoc">sql:datediff</a>
    * @param datepart  Is the part of startdate and enddate that specifies the type of boundary crossed. The following table lists all valid datepart arguments. User-defined variable equivalents are not valid. <p>Options:</p><p> <code>datepart</code> parameter abbreviation includes:</p> <blockquote><dl> <dt>"year","yyyy","yy"</dt> <dd>The year part of the <code>date</code></dd> <dt>"quarter","qq","q"</dt> <dd>The quarter part of the <code>date</code></dd> <dt>"month","mm","m"</dt> <dd>The month part of the <code>date</code></dd> <dt>"dayofyear","dy","y"</dt> <dd>The day of the year from the <code>date</code></dd> <dt>"day","dd","d"</dt> <dd>The day of the month from the <code>date</code></dd> <dt>"week","wk","ww"</dt> <dd>The week of the year from the <code>date</code></dd> <dt>"weekday","dw"</dt> <dd>The day of the week from the <code>date</code></dd> <dt>"hour","hh"</dt> <dd>The hour of the day from the <code>date</code></dd> <dt>"minute","mi","n"</dt> <dd>The minute of the hour from the <code>date</code></dd> <dt>"second","ss","s"</dt> <dd>The second of the minute from the <code>date</code></dd> <dt>"millisecond","ms"</dt> <dd>The millisecond of the minute from the <code>date</code></dd> <dt>"microsecond","msc"</dt> <dd>The microsecond of the minute from the <code>date</code></dd> <dt>"nanosecond","ns"</dt> <dd>The nanosecond of the minute from the <code>date</code></dd> </dl></blockquote>
    * @param startdate  Is an expression that can be resolved to a time, date, datetime or value. date can be an expression, column expression, user-defined variable or string literal. <code>startdate</code> is subtracted from <code>enddate</code>.
    * @param enddate  Same as <code>startdate</code>.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr datediff(XsStringExpr datepart, ItemExpr startdate, ItemExpr enddate);
    /**
    * Returns an integer that represents the specified <code>datepart</code> of the specified <code>date</code>. <p> If <code>datepart</code> or <code>date</code> is the empty sequence, the function returns the empty sequence. 
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:datepart" target="mlserverdoc">sql:datepart</a>
    * @param datepart  The part of date that to be returned. <p>Options:</p><p> <code>datepart</code> parameter abbreviation includes:</p> <blockquote><dl> <dt>"year","yyyy","yy"</dt> <dd>The year part of the <code>date</code></dd> <dt>"quarter","qq","q"</dt> <dd>The quarter part of the <code>date</code></dd> <dt>"month","mm","m"</dt> <dd>The month part of the <code>date</code></dd> <dt>"dayofyear","dy","y"</dt> <dd>The day of the year from the <code>date</code></dd> <dt>"day","dd","d"</dt> <dd>The day of the month from the <code>date</code></dd> <dt>"week","wk","ww"</dt> <dd>The week of the year from the <code>date</code></dd> <dt>"weekday","dw"</dt> <dd>The day of the week from the <code>date</code></dd> <dt>"hour","hh"</dt> <dd>The hour of the day from the <code>date</code></dd> <dt>"minute","mi","n"</dt> <dd>The minute of the hour from the <code>date</code></dd> <dt>"second","ss","s"</dt> <dd>The second of the minute from the <code>date</code></dd> <dt>"millisecond","ms"</dt> <dd>The millisecond of the minute from the <code>date</code></dd> <dt>"microsecond","msc"</dt> <dd>The microsecond of the minute from the <code>date</code></dd> <dt>"nanosecond","ns"</dt> <dd>The nanosecond of the minute from the <code>date</code></dd> <dt>"TZoffset","tz"</dt> <dd>The timezone offset from the <code>date</code></dd> </dl></blockquote>
    * @param date  Is an expression that can be resolved to a xs:date, xs:time, xs:dateTime. <code>date</code> can be an expression, column expression,user-defined variable, or string literal.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr datepart(XsStringExpr datepart, ItemExpr date);
    public XsIntegerExpr day(ItemExpr arg1);
    public XsStringExpr dayname(ItemExpr arg1);
    public XsIntegerExpr hours(ItemExpr arg1);
    /**
    * Returns a string that that is the first argument with <var>length</var> characters removed starting at <var>start</var> and the second string has been inserted beginning at <var>start</var>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:insert" target="mlserverdoc">sql:insert</a>
    * @param str  The string to manipulate.
    * @param start  The starting position where characters will be inserted.
    * @param length  The number of characters to be removed.
    * @param str2  The string to insert.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr insert(XsStringExpr str, double start, double length, String str2);
    /**
    * Returns a string that that is the first argument with <var>length</var> characters removed starting at <var>start</var> and the second string has been inserted beginning at <var>start</var>.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:insert" target="mlserverdoc">sql:insert</a>
    * @param str  The string to manipulate.
    * @param start  The starting position where characters will be inserted.
    * @param length  The number of characters to be removed.
    * @param str2  The string to insert.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr insert(XsStringExpr str, XsNumericExpr start, XsNumericExpr length, XsStringExpr str2);
    /**
    * Find the starting location of a pattern in a string.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:instr" target="mlserverdoc">sql:instr</a>
    * @param str  The string to be evaluated.
    * @param n  The pattern to be evaluated.
    * @return  a XsUnsignedIntExpr expression
    */
    public XsUnsignedIntExpr instr(XsStringExpr str, String n);
    /**
    * Find the starting location of a pattern in a string.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:instr" target="mlserverdoc">sql:instr</a>
    * @param str  The string to be evaluated.
    * @param n  The pattern to be evaluated.
    * @return  a XsUnsignedIntExpr expression
    */
    public XsUnsignedIntExpr instr(XsStringExpr str, XsStringExpr n);
    /**
    * Returns a string that is the leftmost characters of the target string. The number of characters to return is specified by the second argument.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:left" target="mlserverdoc">sql:left</a>
    * @param str  The base string. If the value is not a string, its string value will be used.
    * @param n  The number of leftmost characters of the string to return.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr left(ItemSeqExpr str, double n);
    /**
    * Returns a string that is the leftmost characters of the target string. The number of characters to return is specified by the second argument.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:left" target="mlserverdoc">sql:left</a>
    * @param str  The base string. If the value is not a string, its string value will be used.
    * @param n  The number of leftmost characters of the string to return.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr left(ItemSeqExpr str, XsNumericExpr n);
    /**
    * Return a string that removes leading empty spaces in the input string.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:ltrim" target="mlserverdoc">sql:ltrim</a>
    * @param str  The string to be evaluated.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr ltrim(XsStringExpr str);
    public XsIntegerExpr minutes(ItemExpr arg1);
    public XsIntegerExpr month(ItemExpr arg1);
    public XsStringExpr monthname(ItemExpr arg1);
    /**
    * Returns the length of the string "str" in bits.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:octet-length" target="mlserverdoc">sql:octet-length</a>
    * @param x  The string to be evaluated.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr octetLength(XsStringExpr x);
    public XsIntegerExpr quarter(ItemExpr arg1);
    /**
    * Return a random number. This differs from xdmp:random in that the argument is a seed.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:rand" target="mlserverdoc">sql:rand</a>
    * @param n  The random seed. Currently this parameter is ignored.
    * @return  a XsUnsignedLongExpr expression
    */
    public XsUnsignedLongExpr rand(XsUnsignedLongExpr n);
    /**
    * Returns a string that concatenates the first argument as many times as specified by the second argument.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:repeat" target="mlserverdoc">sql:repeat</a>
    * @param str  The string to duplicate. If the value is not a string, its string value will be used.
    * @param n  The number of times to repeat the string.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr repeat(ItemSeqExpr str, double n);
    /**
    * Returns a string that concatenates the first argument as many times as specified by the second argument.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:repeat" target="mlserverdoc">sql:repeat</a>
    * @param str  The string to duplicate. If the value is not a string, its string value will be used.
    * @param n  The number of times to repeat the string.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr repeat(ItemSeqExpr str, XsNumericExpr n);
    /**
    * Returns a string that is the rightmost characters of the target string. The number of characters to return is specified by the second argument.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:right" target="mlserverdoc">sql:right</a>
    * @param str  The base string. If the value is not a string, its string value will be used.
    * @param n  The number of rightmost characters of the string to return.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr right(ItemSeqExpr str, double n);
    /**
    * Returns a string that is the rightmost characters of the target string. The number of characters to return is specified by the second argument.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:right" target="mlserverdoc">sql:right</a>
    * @param str  The base string. If the value is not a string, its string value will be used.
    * @param n  The number of rightmost characters of the string to return.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr right(ItemSeqExpr str, XsNumericExpr n);
    /**
    * Return a string that removes trailing empty spaces in the input string.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:rtrim" target="mlserverdoc">sql:rtrim</a>
    * @param str  The string to be evaluated.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr rtrim(XsStringExpr str);
    public XsDecimalExpr seconds(ItemExpr arg1);
    /**
    * Returns the sign of number x.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:sign" target="mlserverdoc">sql:sign</a>
    * @param x  The number to be evaluated.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr sign(XsNumericExpr x);
    /**
    * Returns a string that is the given number of spaces.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:space" target="mlserverdoc">sql:space</a>
    * @param n  The number of spaces to return as a string.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr space(XsNumericExpr n);
    /**
    * Returns a xs:string? timestamp created by adding a number to the given dateTimeType field of a given timestamp.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:timestampadd" target="mlserverdoc">sql:timestampadd</a>
    * @param dateTimeType  The dateTimeType of the timestamp where addition should take place. Available types are: <dl> <dt><p>SQL_TSI_FRAC_SECOND</p></dt> <dd>nano seconds</dd> <dt><p>SQL_TSI_SECOND</p></dt> <dd>seconds</dd> <dt><p>SQL_TSI_MINUTE</p></dt> <dd>minute</dd> <dt><p>SQL_TSI_HOUR</p></dt> <dd>hour</dd> <dt><p>SQL_TSI_DAY</p></dt> <dd>day</dd> <dt><p>SQL_TSI_WEEK</p></dt> <dd>week</dd> <dt><p>SQL_TSI_MONTH</p></dt> <dd>month</dd> <dt><p>SQL_TSI_QUARTER</p></dt> <dd>quarter</dd> <dt><p>SQL_TSI_YEAR</p></dt> <dd>year</dd> </dl>
    * @param value  The integer to add to the given dateTimeType field of the third parameter.
    * @param timestamp  The xs:dateTime timestamp to which addition has to take place.
    * @return  a ItemExpr expression
    */
    public ItemExpr timestampadd(XsStringExpr dateTimeType, int value, ItemExpr timestamp);
    /**
    * Returns a xs:string? timestamp created by adding a number to the given dateTimeType field of a given timestamp.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:timestampadd" target="mlserverdoc">sql:timestampadd</a>
    * @param dateTimeType  The dateTimeType of the timestamp where addition should take place. Available types are: <dl> <dt><p>SQL_TSI_FRAC_SECOND</p></dt> <dd>nano seconds</dd> <dt><p>SQL_TSI_SECOND</p></dt> <dd>seconds</dd> <dt><p>SQL_TSI_MINUTE</p></dt> <dd>minute</dd> <dt><p>SQL_TSI_HOUR</p></dt> <dd>hour</dd> <dt><p>SQL_TSI_DAY</p></dt> <dd>day</dd> <dt><p>SQL_TSI_WEEK</p></dt> <dd>week</dd> <dt><p>SQL_TSI_MONTH</p></dt> <dd>month</dd> <dt><p>SQL_TSI_QUARTER</p></dt> <dd>quarter</dd> <dt><p>SQL_TSI_YEAR</p></dt> <dd>year</dd> </dl>
    * @param value  The integer to add to the given dateTimeType field of the third parameter.
    * @param timestamp  The xs:dateTime timestamp to which addition has to take place.
    * @return  a ItemExpr expression
    */
    public ItemExpr timestampadd(XsStringExpr dateTimeType, XsIntExpr value, ItemExpr timestamp);
    public XsIntegerExpr timestampdiff(XsStringExpr arg1, ItemExpr arg2, ItemExpr arg3);
    /**
    * Return a string that removes leading empty spaces in the input string.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/sql:trim" target="mlserverdoc">sql:trim</a>
    * @param str  The string to be evaluated.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr trim(XsStringExpr str);
    public XsIntegerExpr week(ItemExpr arg1);
    public XsIntegerExpr weekday(ItemExpr arg1);
    public XsIntegerExpr year(ItemExpr arg1);
    public XsIntegerExpr yearday(ItemExpr arg1);
}
