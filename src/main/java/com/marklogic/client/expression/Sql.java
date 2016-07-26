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

import com.marklogic.client.type.XsStringExpr;
 import com.marklogic.client.type.SqlCollatedStringExpr;
 import com.marklogic.client.type.XsIntegerExpr;
 import com.marklogic.client.type.XsUnsignedLongExpr;
 import com.marklogic.client.type.XsIntExpr;
 import com.marklogic.client.type.SqlGenericDateTimeExpr;
 import com.marklogic.client.type.XsUnsignedIntExpr;
 import com.marklogic.client.type.SqlCollatedStringSeqExpr;
 import com.marklogic.client.type.XsNumericExpr;
 import com.marklogic.client.type.XsDecimalExpr;
 import com.marklogic.client.type.SqlGenericDateTimeSeqExpr;
 import com.marklogic.client.type.ItemSeqExpr;


// IMPORTANT: Do not edit. This file is generated. 
public interface Sql {
    public XsIntegerExpr bitLength();
    public XsIntegerExpr bitLength(String arg1);
    public XsIntegerExpr bitLength(XsStringExpr arg1);
    public SqlGenericDateTimeExpr dateadd(String datepart, int number, SqlGenericDateTimeExpr date);
    public SqlGenericDateTimeExpr dateadd(XsStringExpr datepart, XsIntExpr number, SqlGenericDateTimeExpr date);
    public XsIntegerExpr datediff(String datepart, SqlGenericDateTimeExpr startdate, SqlGenericDateTimeExpr enddate);
    public XsIntegerExpr datediff(XsStringExpr datepart, SqlGenericDateTimeExpr startdate, SqlGenericDateTimeExpr enddate);
    public XsIntegerExpr datepart(String datepart, SqlGenericDateTimeExpr date);
    public XsIntegerExpr datepart(XsStringExpr datepart, SqlGenericDateTimeExpr date);
    public XsIntegerExpr day(SqlGenericDateTimeExpr arg1);
    public XsStringExpr dayname(SqlGenericDateTimeExpr arg1);
    public XsIntegerExpr hours(SqlGenericDateTimeExpr arg1);
    public XsStringExpr insert(String arg1, XsNumericExpr arg2, XsNumericExpr arg3, String arg4);
    public XsStringExpr insert(XsStringExpr arg1, XsNumericExpr arg2, XsNumericExpr arg3, XsStringExpr arg4);
    public XsUnsignedIntExpr instr(String arg1, String arg2);
    public XsUnsignedIntExpr instr(XsStringExpr arg1, XsStringExpr arg2);
    public XsStringExpr left(ItemSeqExpr arg1, XsNumericExpr arg2);
    public XsStringExpr ltrim(String arg1);
    public XsStringExpr ltrim(XsStringExpr arg1);
    public XsStringExpr ltrim(String arg1, String arg2);
    public XsStringExpr ltrim(XsStringExpr arg1, XsStringExpr arg2);
    public XsIntegerExpr minutes(SqlGenericDateTimeExpr arg1);
    public XsIntegerExpr month(SqlGenericDateTimeExpr arg1);
    public XsStringExpr monthname(SqlGenericDateTimeExpr arg1);
    public XsIntegerExpr octetLength();
    public XsIntegerExpr octetLength(String arg1);
    public XsIntegerExpr octetLength(XsStringExpr arg1);
    public XsIntegerExpr quarter(SqlGenericDateTimeExpr arg1);
    public XsUnsignedLongExpr rand();
    public XsUnsignedLongExpr rand(XsUnsignedLongExpr arg1);
    public XsStringExpr repeat(ItemSeqExpr arg1, XsNumericExpr arg2);
    public XsStringExpr right(ItemSeqExpr arg1, XsNumericExpr arg2);
    public XsStringExpr rtrim(String arg1);
    public XsStringExpr rtrim(XsStringExpr arg1);
    public XsStringExpr rtrim(String arg1, String arg2);
    public XsStringExpr rtrim(XsStringExpr arg1, XsStringExpr arg2);
    public XsDecimalExpr seconds(SqlGenericDateTimeExpr arg1);
    public ItemSeqExpr sign(XsNumericExpr arg1);
    public XsStringExpr space(XsNumericExpr arg1);
    public SqlGenericDateTimeExpr timestampadd(String arg1, int arg2, SqlGenericDateTimeExpr arg3);
    public SqlGenericDateTimeExpr timestampadd(XsStringExpr arg1, XsIntExpr arg2, SqlGenericDateTimeExpr arg3);
    public XsIntegerExpr timestampdiff(String arg1, SqlGenericDateTimeExpr arg2, SqlGenericDateTimeExpr arg3);
    public XsIntegerExpr timestampdiff(XsStringExpr arg1, SqlGenericDateTimeExpr arg2, SqlGenericDateTimeExpr arg3);
    public XsStringExpr trim(String arg1);
    public XsStringExpr trim(XsStringExpr arg1);
    public XsStringExpr trim(String arg1, String arg2);
    public XsStringExpr trim(XsStringExpr arg1, XsStringExpr arg2);
    public XsIntegerExpr week(SqlGenericDateTimeExpr arg1);
    public XsIntegerExpr weekday(SqlGenericDateTimeExpr arg1);
    public XsIntegerExpr year(SqlGenericDateTimeExpr arg1);
    public XsIntegerExpr yearday(SqlGenericDateTimeExpr arg1);     public SqlCollatedStringSeqExpr collatedString(SqlCollatedStringExpr... items);
     public SqlGenericDateTimeSeqExpr genericDateTime(SqlGenericDateTimeExpr... items);

}
