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

import com.marklogic.client.expression.Xs;
import com.marklogic.client.expression.XsValue;

import com.marklogic.client.expression.Sql;
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

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class SqlExprImpl implements Sql {
    private XsExprImpl xs = null;
    public SqlExprImpl(XsExprImpl xs) {
        this.xs = xs;
    }
     @Override
        public XsIntegerExpr bitLength() {
        return new XsExprImpl.XsIntegerCallImpl("sql", "bit-length", new Object[]{  });
    }
    @Override
        public XsIntegerExpr bitLength(String arg1) {
        return bitLength((arg1 == null) ? null : xs.string(arg1)); 
    }
    @Override
        public XsIntegerExpr bitLength(XsStringExpr arg1) {
        return new XsExprImpl.XsIntegerCallImpl("sql", "bit-length", new Object[]{ arg1 });
    }
    @Override
        public SqlGenericDateTimeExpr dateadd(String datepart, int number, SqlGenericDateTimeExpr date) {
        return dateadd(xs.string(datepart), xs.intVal(number), date); 
    }
    @Override
        public SqlGenericDateTimeExpr dateadd(XsStringExpr datepart, XsIntExpr number, SqlGenericDateTimeExpr date) {
        return new SqlExprImpl.SqlGenericDateTimeCallImpl("sql", "dateadd", new Object[]{ datepart, number, date });
    }
    @Override
        public XsIntegerExpr datediff(String datepart, SqlGenericDateTimeExpr startdate, SqlGenericDateTimeExpr enddate) {
        return datediff(xs.string(datepart), startdate, enddate); 
    }
    @Override
        public XsIntegerExpr datediff(XsStringExpr datepart, SqlGenericDateTimeExpr startdate, SqlGenericDateTimeExpr enddate) {
        return new XsExprImpl.XsIntegerCallImpl("sql", "datediff", new Object[]{ datepart, startdate, enddate });
    }
    @Override
        public XsIntegerExpr datepart(String datepart, SqlGenericDateTimeExpr date) {
        return datepart(xs.string(datepart), date); 
    }
    @Override
        public XsIntegerExpr datepart(XsStringExpr datepart, SqlGenericDateTimeExpr date) {
        return new XsExprImpl.XsIntegerCallImpl("sql", "datepart", new Object[]{ datepart, date });
    }
    @Override
        public XsIntegerExpr day(SqlGenericDateTimeExpr arg1) {
        return new XsExprImpl.XsIntegerCallImpl("sql", "day", new Object[]{ arg1 });
    }
    @Override
        public XsStringExpr dayname(SqlGenericDateTimeExpr arg1) {
        return new XsExprImpl.XsStringCallImpl("sql", "dayname", new Object[]{ arg1 });
    }
    @Override
        public XsIntegerExpr hours(SqlGenericDateTimeExpr arg1) {
        return new XsExprImpl.XsIntegerCallImpl("sql", "hours", new Object[]{ arg1 });
    }
    @Override
        public XsStringExpr insert(String arg1, XsNumericExpr arg2, XsNumericExpr arg3, String arg4) {
        return insert(xs.string(arg1), arg2, arg3, xs.string(arg4)); 
    }
    @Override
        public XsStringExpr insert(XsStringExpr arg1, XsNumericExpr arg2, XsNumericExpr arg3, XsStringExpr arg4) {
        return new XsExprImpl.XsStringCallImpl("sql", "insert", new Object[]{ arg1, arg2, arg3, arg4 });
    }
    @Override
        public XsUnsignedIntExpr instr(String arg1, String arg2) {
        return instr(xs.string(arg1), xs.string(arg2)); 
    }
    @Override
        public XsUnsignedIntExpr instr(XsStringExpr arg1, XsStringExpr arg2) {
        return new XsExprImpl.XsUnsignedIntCallImpl("sql", "instr", new Object[]{ arg1, arg2 });
    }
    @Override
        public XsStringExpr left(ItemSeqExpr arg1, XsNumericExpr arg2) {
        return new XsExprImpl.XsStringCallImpl("sql", "left", new Object[]{ arg1, arg2 });
    }
    @Override
        public XsStringExpr ltrim(String arg1) {
        return ltrim(xs.string(arg1)); 
    }
    @Override
        public XsStringExpr ltrim(XsStringExpr arg1) {
        return new XsExprImpl.XsStringCallImpl("sql", "ltrim", new Object[]{ arg1 });
    }
    @Override
        public XsStringExpr ltrim(String arg1, String arg2) {
        return ltrim(xs.string(arg1), xs.string(arg2)); 
    }
    @Override
        public XsStringExpr ltrim(XsStringExpr arg1, XsStringExpr arg2) {
        return new XsExprImpl.XsStringCallImpl("sql", "ltrim", new Object[]{ arg1, arg2 });
    }
    @Override
        public XsIntegerExpr minutes(SqlGenericDateTimeExpr arg1) {
        return new XsExprImpl.XsIntegerCallImpl("sql", "minutes", new Object[]{ arg1 });
    }
    @Override
        public XsIntegerExpr month(SqlGenericDateTimeExpr arg1) {
        return new XsExprImpl.XsIntegerCallImpl("sql", "month", new Object[]{ arg1 });
    }
    @Override
        public XsStringExpr monthname(SqlGenericDateTimeExpr arg1) {
        return new XsExprImpl.XsStringCallImpl("sql", "monthname", new Object[]{ arg1 });
    }
    @Override
        public XsIntegerExpr octetLength() {
        return new XsExprImpl.XsIntegerCallImpl("sql", "octet-length", new Object[]{  });
    }
    @Override
        public XsIntegerExpr octetLength(String arg1) {
        return octetLength((arg1 == null) ? null : xs.string(arg1)); 
    }
    @Override
        public XsIntegerExpr octetLength(XsStringExpr arg1) {
        return new XsExprImpl.XsIntegerCallImpl("sql", "octet-length", new Object[]{ arg1 });
    }
    @Override
        public XsIntegerExpr quarter(SqlGenericDateTimeExpr arg1) {
        return new XsExprImpl.XsIntegerCallImpl("sql", "quarter", new Object[]{ arg1 });
    }
    @Override
        public XsUnsignedLongExpr rand() {
        return new XsExprImpl.XsUnsignedLongCallImpl("sql", "rand", new Object[]{  });
    }
    @Override
        public XsUnsignedLongExpr rand(XsUnsignedLongExpr arg1) {
        return new XsExprImpl.XsUnsignedLongCallImpl("sql", "rand", new Object[]{ arg1 });
    }
    @Override
        public XsStringExpr repeat(ItemSeqExpr arg1, XsNumericExpr arg2) {
        return new XsExprImpl.XsStringCallImpl("sql", "repeat", new Object[]{ arg1, arg2 });
    }
    @Override
        public XsStringExpr right(ItemSeqExpr arg1, XsNumericExpr arg2) {
        return new XsExprImpl.XsStringCallImpl("sql", "right", new Object[]{ arg1, arg2 });
    }
    @Override
        public XsStringExpr rtrim(String arg1) {
        return rtrim(xs.string(arg1)); 
    }
    @Override
        public XsStringExpr rtrim(XsStringExpr arg1) {
        return new XsExprImpl.XsStringCallImpl("sql", "rtrim", new Object[]{ arg1 });
    }
    @Override
        public XsStringExpr rtrim(String arg1, String arg2) {
        return rtrim(xs.string(arg1), xs.string(arg2)); 
    }
    @Override
        public XsStringExpr rtrim(XsStringExpr arg1, XsStringExpr arg2) {
        return new XsExprImpl.XsStringCallImpl("sql", "rtrim", new Object[]{ arg1, arg2 });
    }
    @Override
        public XsDecimalExpr seconds(SqlGenericDateTimeExpr arg1) {
        return new XsExprImpl.XsDecimalCallImpl("sql", "seconds", new Object[]{ arg1 });
    }
    @Override
        public ItemSeqExpr sign(XsNumericExpr arg1) {
        return new BaseTypeImpl.ItemSeqCallImpl("sql", "sign", new Object[]{ arg1 });
    }
    @Override
        public XsStringExpr space(XsNumericExpr arg1) {
        return new XsExprImpl.XsStringCallImpl("sql", "space", new Object[]{ arg1 });
    }
    @Override
        public SqlGenericDateTimeExpr timestampadd(String arg1, int arg2, SqlGenericDateTimeExpr arg3) {
        return timestampadd(xs.string(arg1), xs.intVal(arg2), arg3); 
    }
    @Override
        public SqlGenericDateTimeExpr timestampadd(XsStringExpr arg1, XsIntExpr arg2, SqlGenericDateTimeExpr arg3) {
        return new SqlExprImpl.SqlGenericDateTimeCallImpl("sql", "timestampadd", new Object[]{ arg1, arg2, arg3 });
    }
    @Override
        public XsIntegerExpr timestampdiff(String arg1, SqlGenericDateTimeExpr arg2, SqlGenericDateTimeExpr arg3) {
        return timestampdiff(xs.string(arg1), arg2, arg3); 
    }
    @Override
        public XsIntegerExpr timestampdiff(XsStringExpr arg1, SqlGenericDateTimeExpr arg2, SqlGenericDateTimeExpr arg3) {
        return new XsExprImpl.XsIntegerCallImpl("sql", "timestampdiff", new Object[]{ arg1, arg2, arg3 });
    }
    @Override
        public XsStringExpr trim(String arg1) {
        return trim(xs.string(arg1)); 
    }
    @Override
        public XsStringExpr trim(XsStringExpr arg1) {
        return new XsExprImpl.XsStringCallImpl("sql", "trim", new Object[]{ arg1 });
    }
    @Override
        public XsStringExpr trim(String arg1, String arg2) {
        return trim(xs.string(arg1), xs.string(arg2)); 
    }
    @Override
        public XsStringExpr trim(XsStringExpr arg1, XsStringExpr arg2) {
        return new XsExprImpl.XsStringCallImpl("sql", "trim", new Object[]{ arg1, arg2 });
    }
    @Override
        public XsIntegerExpr week(SqlGenericDateTimeExpr arg1) {
        return new XsExprImpl.XsIntegerCallImpl("sql", "week", new Object[]{ arg1 });
    }
    @Override
        public XsIntegerExpr weekday(SqlGenericDateTimeExpr arg1) {
        return new XsExprImpl.XsIntegerCallImpl("sql", "weekday", new Object[]{ arg1 });
    }
    @Override
        public XsIntegerExpr year(SqlGenericDateTimeExpr arg1) {
        return new XsExprImpl.XsIntegerCallImpl("sql", "year", new Object[]{ arg1 });
    }
    @Override
        public XsIntegerExpr yearday(SqlGenericDateTimeExpr arg1) {
        return new XsExprImpl.XsIntegerCallImpl("sql", "yearday", new Object[]{ arg1 });
    }     @Override
    public SqlCollatedStringSeqExpr collatedString(SqlCollatedStringExpr... items) {
        return new SqlCollatedStringSeqListImpl(items);
    }
     @Override
    public SqlGenericDateTimeSeqExpr genericDateTime(SqlGenericDateTimeExpr... items) {
        return new SqlGenericDateTimeSeqListImpl(items);
    }
        static class SqlCollatedStringSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements SqlCollatedStringSeqExpr {
            SqlCollatedStringSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class SqlCollatedStringSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SqlCollatedStringSeqExpr {
            SqlCollatedStringSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class SqlCollatedStringCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SqlCollatedStringExpr {
            SqlCollatedStringCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class SqlGenericDateTimeSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements SqlGenericDateTimeSeqExpr {
            SqlGenericDateTimeSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class SqlGenericDateTimeSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SqlGenericDateTimeSeqExpr {
            SqlGenericDateTimeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class SqlGenericDateTimeCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements SqlGenericDateTimeExpr {
            SqlGenericDateTimeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }

}
