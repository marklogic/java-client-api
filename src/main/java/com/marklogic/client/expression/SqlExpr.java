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
public interface SqlExpr {
    public XsIntegerExpr bitLength(XsStringExpr str);
    public ItemExpr dateadd(XsStringExpr datepart, int number, ItemExpr date);
    public ItemExpr dateadd(XsStringExpr datepart, XsIntExpr number, ItemExpr date);
    public XsIntegerExpr datediff(XsStringExpr datepart, ItemExpr startdate, ItemExpr enddate);
    public XsIntegerExpr datepart(XsStringExpr datepart, ItemExpr date);
    public XsIntegerExpr day(ItemExpr arg1);
    public XsStringExpr dayname(ItemExpr arg1);
    public XsIntegerExpr hours(ItemExpr arg1);
    public XsStringExpr insert(XsStringExpr str, double start, double length, String str2);
    public XsStringExpr insert(XsStringExpr str, XsNumericExpr start, XsNumericExpr length, XsStringExpr str2);
    public XsUnsignedIntExpr instr(XsStringExpr str, String n);
    public XsUnsignedIntExpr instr(XsStringExpr str, XsStringExpr n);
    public XsStringExpr left(ItemSeqExpr str, double n);
    public XsStringExpr left(ItemSeqExpr str, XsNumericExpr n);
    public XsStringExpr ltrim(XsStringExpr str);
    public XsIntegerExpr minutes(ItemExpr arg1);
    public XsIntegerExpr month(ItemExpr arg1);
    public XsStringExpr monthname(ItemExpr arg1);
    public XsIntegerExpr octetLength(XsStringExpr x);
    public XsIntegerExpr quarter(ItemExpr arg1);
    public XsUnsignedLongExpr rand(XsUnsignedLongExpr n);
    public XsStringExpr repeat(ItemSeqExpr str, double n);
    public XsStringExpr repeat(ItemSeqExpr str, XsNumericExpr n);
    public XsStringExpr right(ItemSeqExpr str, double n);
    public XsStringExpr right(ItemSeqExpr str, XsNumericExpr n);
    public XsStringExpr rtrim(XsStringExpr str);
    public XsDecimalExpr seconds(ItemExpr arg1);
    public ItemSeqExpr sign(XsNumericExpr x);
    public XsStringExpr space(XsNumericExpr n);
    public ItemExpr timestampadd(XsStringExpr dateTimeType, int value, ItemExpr timestamp);
    public ItemExpr timestampadd(XsStringExpr dateTimeType, XsIntExpr value, ItemExpr timestamp);
    public XsIntegerExpr timestampdiff(XsStringExpr arg1, ItemExpr arg2, ItemExpr arg3);
    public XsStringExpr trim(XsStringExpr str);
    public XsIntegerExpr week(ItemExpr arg1);
    public XsIntegerExpr weekday(ItemExpr arg1);
    public XsIntegerExpr year(ItemExpr arg1);
    public XsIntegerExpr yearday(ItemExpr arg1);
}
