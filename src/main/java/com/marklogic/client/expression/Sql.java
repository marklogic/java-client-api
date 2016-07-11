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


// IMPORTANT: Do not edit. This file is generated. 
public interface Sql {
    public Xs.IntegerExpr bitLength();
    public Xs.IntegerExpr bitLength(String arg1);
    public Xs.IntegerExpr bitLength(Xs.StringExpr arg1);
    public Sql.GenericDateTimeExpr dateadd(String datepart, int number, Sql.GenericDateTimeExpr date);
    public Sql.GenericDateTimeExpr dateadd(Xs.StringExpr datepart, Xs.IntExpr number, Sql.GenericDateTimeExpr date);
    public Xs.IntegerExpr datediff(String datepart, Sql.GenericDateTimeExpr startdate, Sql.GenericDateTimeExpr enddate);
    public Xs.IntegerExpr datediff(Xs.StringExpr datepart, Sql.GenericDateTimeExpr startdate, Sql.GenericDateTimeExpr enddate);
    public Xs.IntegerExpr datepart(String datepart, Sql.GenericDateTimeExpr date);
    public Xs.IntegerExpr datepart(Xs.StringExpr datepart, Sql.GenericDateTimeExpr date);
    public Xs.IntegerExpr day(Sql.GenericDateTimeExpr arg1);
    public Xs.StringExpr dayname(Sql.GenericDateTimeExpr arg1);
    public Xs.IntegerExpr hours(Sql.GenericDateTimeExpr arg1);
    public Xs.StringExpr insert(String arg1, Xs.NumericExpr arg2, Xs.NumericExpr arg3, String arg4);
    public Xs.StringExpr insert(Xs.StringExpr arg1, Xs.NumericExpr arg2, Xs.NumericExpr arg3, Xs.StringExpr arg4);
    public Xs.UnsignedIntExpr instr(String arg1, String arg2);
    public Xs.UnsignedIntExpr instr(Xs.StringExpr arg1, Xs.StringExpr arg2);
    public Xs.StringExpr left(BaseType.ItemSeqExpr arg1, Xs.NumericExpr arg2);
    public Xs.StringExpr ltrim(String arg1);
    public Xs.StringExpr ltrim(Xs.StringExpr arg1);
    public Xs.StringExpr ltrim(String arg1, String arg2);
    public Xs.StringExpr ltrim(Xs.StringExpr arg1, Xs.StringExpr arg2);
    public Xs.IntegerExpr minutes(Sql.GenericDateTimeExpr arg1);
    public Xs.IntegerExpr month(Sql.GenericDateTimeExpr arg1);
    public Xs.StringExpr monthname(Sql.GenericDateTimeExpr arg1);
    public Xs.IntegerExpr octetLength();
    public Xs.IntegerExpr octetLength(String arg1);
    public Xs.IntegerExpr octetLength(Xs.StringExpr arg1);
    public Xs.IntegerExpr quarter(Sql.GenericDateTimeExpr arg1);
    public Xs.UnsignedLongExpr rand();
    public Xs.UnsignedLongExpr rand(Xs.UnsignedLongExpr arg1);
    public Xs.StringExpr repeat(BaseType.ItemSeqExpr arg1, Xs.NumericExpr arg2);
    public Xs.StringExpr right(BaseType.ItemSeqExpr arg1, Xs.NumericExpr arg2);
    public Xs.StringExpr rtrim(String arg1);
    public Xs.StringExpr rtrim(Xs.StringExpr arg1);
    public Xs.StringExpr rtrim(String arg1, String arg2);
    public Xs.StringExpr rtrim(Xs.StringExpr arg1, Xs.StringExpr arg2);
    public Xs.DecimalExpr seconds(Sql.GenericDateTimeExpr arg1);
    public BaseType.ItemSeqExpr sign(Xs.NumericExpr arg1);
    public Xs.StringExpr space(Xs.NumericExpr arg1);
    public Sql.GenericDateTimeExpr timestampadd(String arg1, int arg2, Sql.GenericDateTimeExpr arg3);
    public Sql.GenericDateTimeExpr timestampadd(Xs.StringExpr arg1, Xs.IntExpr arg2, Sql.GenericDateTimeExpr arg3);
    public Xs.IntegerExpr timestampdiff(String arg1, Sql.GenericDateTimeExpr arg2, Sql.GenericDateTimeExpr arg3);
    public Xs.IntegerExpr timestampdiff(Xs.StringExpr arg1, Sql.GenericDateTimeExpr arg2, Sql.GenericDateTimeExpr arg3);
    public Xs.StringExpr trim(String arg1);
    public Xs.StringExpr trim(Xs.StringExpr arg1);
    public Xs.StringExpr trim(String arg1, String arg2);
    public Xs.StringExpr trim(Xs.StringExpr arg1, Xs.StringExpr arg2);
    public Xs.IntegerExpr week(Sql.GenericDateTimeExpr arg1);
    public Xs.IntegerExpr weekday(Sql.GenericDateTimeExpr arg1);
    public Xs.IntegerExpr year(Sql.GenericDateTimeExpr arg1);
    public Xs.IntegerExpr yearday(Sql.GenericDateTimeExpr arg1);     public Sql.CollatedStringSeqExpr collatedString(Sql.CollatedStringExpr... items);
     public Sql.GenericDateTimeSeqExpr genericDateTime(Sql.GenericDateTimeExpr... items);
        public interface CollatedStringSeqExpr extends BaseType.ItemSeqExpr { }
        public interface CollatedStringExpr extends CollatedStringSeqExpr, BaseType.ItemExpr { }
         public interface GenericDateTimeSeqExpr extends BaseType.ItemSeqExpr { }
        public interface GenericDateTimeExpr extends GenericDateTimeSeqExpr, BaseType.ItemExpr { }

}
