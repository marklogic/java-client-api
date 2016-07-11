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

// TODO: single import
import com.marklogic.client.expression.BaseType;
import com.marklogic.client.expression.Xs;

import com.marklogic.client.expression.Sql;
import com.marklogic.client.expression.Xs;
 import com.marklogic.client.expression.BaseType;
 import com.marklogic.client.impl.XsExprImpl;
 import com.marklogic.client.impl.BaseTypeImpl;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class SqlExprImpl implements Sql {
    private Xs xs = null;
    public SqlExprImpl(Xs xs) {
        this.xs = xs;
    }
     @Override
        public Xs.IntegerExpr bitLength() {
        return new XsExprImpl.IntegerCallImpl("sql", "bit-length", new Object[]{  });
    }
    @Override
        public Xs.IntegerExpr bitLength(String arg1) {
        return bitLength((arg1 == null) ? null : xs.string(arg1)); 
    }
    @Override
        public Xs.IntegerExpr bitLength(Xs.StringExpr arg1) {
        return new XsExprImpl.IntegerCallImpl("sql", "bit-length", new Object[]{ arg1 });
    }
    @Override
        public Sql.GenericDateTimeExpr dateadd(String datepart, int number, Sql.GenericDateTimeExpr date) {
        return dateadd(xs.string(datepart), xs.intVal(number), date); 
    }
    @Override
        public Sql.GenericDateTimeExpr dateadd(Xs.StringExpr datepart, Xs.IntExpr number, Sql.GenericDateTimeExpr date) {
        return new SqlExprImpl.GenericDateTimeCallImpl("sql", "dateadd", new Object[]{ datepart, number, date });
    }
    @Override
        public Xs.IntegerExpr datediff(String datepart, Sql.GenericDateTimeExpr startdate, Sql.GenericDateTimeExpr enddate) {
        return datediff(xs.string(datepart), startdate, enddate); 
    }
    @Override
        public Xs.IntegerExpr datediff(Xs.StringExpr datepart, Sql.GenericDateTimeExpr startdate, Sql.GenericDateTimeExpr enddate) {
        return new XsExprImpl.IntegerCallImpl("sql", "datediff", new Object[]{ datepart, startdate, enddate });
    }
    @Override
        public Xs.IntegerExpr datepart(String datepart, Sql.GenericDateTimeExpr date) {
        return datepart(xs.string(datepart), date); 
    }
    @Override
        public Xs.IntegerExpr datepart(Xs.StringExpr datepart, Sql.GenericDateTimeExpr date) {
        return new XsExprImpl.IntegerCallImpl("sql", "datepart", new Object[]{ datepart, date });
    }
    @Override
        public Xs.IntegerExpr day(Sql.GenericDateTimeExpr arg1) {
        return new XsExprImpl.IntegerCallImpl("sql", "day", new Object[]{ arg1 });
    }
    @Override
        public Xs.StringExpr dayname(Sql.GenericDateTimeExpr arg1) {
        return new XsExprImpl.StringCallImpl("sql", "dayname", new Object[]{ arg1 });
    }
    @Override
        public Xs.IntegerExpr hours(Sql.GenericDateTimeExpr arg1) {
        return new XsExprImpl.IntegerCallImpl("sql", "hours", new Object[]{ arg1 });
    }
    @Override
        public Xs.StringExpr insert(String arg1, Xs.NumericExpr arg2, Xs.NumericExpr arg3, String arg4) {
        return insert(xs.string(arg1), arg2, arg3, xs.string(arg4)); 
    }
    @Override
        public Xs.StringExpr insert(Xs.StringExpr arg1, Xs.NumericExpr arg2, Xs.NumericExpr arg3, Xs.StringExpr arg4) {
        return new XsExprImpl.StringCallImpl("sql", "insert", new Object[]{ arg1, arg2, arg3, arg4 });
    }
    @Override
        public Xs.UnsignedIntExpr instr(String arg1, String arg2) {
        return instr(xs.string(arg1), xs.string(arg2)); 
    }
    @Override
        public Xs.UnsignedIntExpr instr(Xs.StringExpr arg1, Xs.StringExpr arg2) {
        return new XsExprImpl.UnsignedIntCallImpl("sql", "instr", new Object[]{ arg1, arg2 });
    }
    @Override
        public Xs.StringExpr left(BaseType.ItemSeqExpr arg1, Xs.NumericExpr arg2) {
        return new XsExprImpl.StringCallImpl("sql", "left", new Object[]{ arg1, arg2 });
    }
    @Override
        public Xs.StringExpr ltrim(String arg1) {
        return ltrim(xs.string(arg1)); 
    }
    @Override
        public Xs.StringExpr ltrim(Xs.StringExpr arg1) {
        return new XsExprImpl.StringCallImpl("sql", "ltrim", new Object[]{ arg1 });
    }
    @Override
        public Xs.StringExpr ltrim(String arg1, String arg2) {
        return ltrim(xs.string(arg1), xs.string(arg2)); 
    }
    @Override
        public Xs.StringExpr ltrim(Xs.StringExpr arg1, Xs.StringExpr arg2) {
        return new XsExprImpl.StringCallImpl("sql", "ltrim", new Object[]{ arg1, arg2 });
    }
    @Override
        public Xs.IntegerExpr minutes(Sql.GenericDateTimeExpr arg1) {
        return new XsExprImpl.IntegerCallImpl("sql", "minutes", new Object[]{ arg1 });
    }
    @Override
        public Xs.IntegerExpr month(Sql.GenericDateTimeExpr arg1) {
        return new XsExprImpl.IntegerCallImpl("sql", "month", new Object[]{ arg1 });
    }
    @Override
        public Xs.StringExpr monthname(Sql.GenericDateTimeExpr arg1) {
        return new XsExprImpl.StringCallImpl("sql", "monthname", new Object[]{ arg1 });
    }
    @Override
        public Xs.IntegerExpr octetLength() {
        return new XsExprImpl.IntegerCallImpl("sql", "octet-length", new Object[]{  });
    }
    @Override
        public Xs.IntegerExpr octetLength(String arg1) {
        return octetLength((arg1 == null) ? null : xs.string(arg1)); 
    }
    @Override
        public Xs.IntegerExpr octetLength(Xs.StringExpr arg1) {
        return new XsExprImpl.IntegerCallImpl("sql", "octet-length", new Object[]{ arg1 });
    }
    @Override
        public Xs.IntegerExpr quarter(Sql.GenericDateTimeExpr arg1) {
        return new XsExprImpl.IntegerCallImpl("sql", "quarter", new Object[]{ arg1 });
    }
    @Override
        public Xs.UnsignedLongExpr rand() {
        return new XsExprImpl.UnsignedLongCallImpl("sql", "rand", new Object[]{  });
    }
    @Override
        public Xs.UnsignedLongExpr rand(Xs.UnsignedLongExpr arg1) {
        return new XsExprImpl.UnsignedLongCallImpl("sql", "rand", new Object[]{ arg1 });
    }
    @Override
        public Xs.StringExpr repeat(BaseType.ItemSeqExpr arg1, Xs.NumericExpr arg2) {
        return new XsExprImpl.StringCallImpl("sql", "repeat", new Object[]{ arg1, arg2 });
    }
    @Override
        public Xs.StringExpr right(BaseType.ItemSeqExpr arg1, Xs.NumericExpr arg2) {
        return new XsExprImpl.StringCallImpl("sql", "right", new Object[]{ arg1, arg2 });
    }
    @Override
        public Xs.StringExpr rtrim(String arg1) {
        return rtrim(xs.string(arg1)); 
    }
    @Override
        public Xs.StringExpr rtrim(Xs.StringExpr arg1) {
        return new XsExprImpl.StringCallImpl("sql", "rtrim", new Object[]{ arg1 });
    }
    @Override
        public Xs.StringExpr rtrim(String arg1, String arg2) {
        return rtrim(xs.string(arg1), xs.string(arg2)); 
    }
    @Override
        public Xs.StringExpr rtrim(Xs.StringExpr arg1, Xs.StringExpr arg2) {
        return new XsExprImpl.StringCallImpl("sql", "rtrim", new Object[]{ arg1, arg2 });
    }
    @Override
        public Xs.DecimalExpr seconds(Sql.GenericDateTimeExpr arg1) {
        return new XsExprImpl.DecimalCallImpl("sql", "seconds", new Object[]{ arg1 });
    }
    @Override
        public BaseType.ItemSeqExpr sign(Xs.NumericExpr arg1) {
        return new BaseTypeImpl.ItemSeqCallImpl("sql", "sign", new Object[]{ arg1 });
    }
    @Override
        public Xs.StringExpr space(Xs.NumericExpr arg1) {
        return new XsExprImpl.StringCallImpl("sql", "space", new Object[]{ arg1 });
    }
    @Override
        public Sql.GenericDateTimeExpr timestampadd(String arg1, int arg2, Sql.GenericDateTimeExpr arg3) {
        return timestampadd(xs.string(arg1), xs.intVal(arg2), arg3); 
    }
    @Override
        public Sql.GenericDateTimeExpr timestampadd(Xs.StringExpr arg1, Xs.IntExpr arg2, Sql.GenericDateTimeExpr arg3) {
        return new SqlExprImpl.GenericDateTimeCallImpl("sql", "timestampadd", new Object[]{ arg1, arg2, arg3 });
    }
    @Override
        public Xs.IntegerExpr timestampdiff(String arg1, Sql.GenericDateTimeExpr arg2, Sql.GenericDateTimeExpr arg3) {
        return timestampdiff(xs.string(arg1), arg2, arg3); 
    }
    @Override
        public Xs.IntegerExpr timestampdiff(Xs.StringExpr arg1, Sql.GenericDateTimeExpr arg2, Sql.GenericDateTimeExpr arg3) {
        return new XsExprImpl.IntegerCallImpl("sql", "timestampdiff", new Object[]{ arg1, arg2, arg3 });
    }
    @Override
        public Xs.StringExpr trim(String arg1) {
        return trim(xs.string(arg1)); 
    }
    @Override
        public Xs.StringExpr trim(Xs.StringExpr arg1) {
        return new XsExprImpl.StringCallImpl("sql", "trim", new Object[]{ arg1 });
    }
    @Override
        public Xs.StringExpr trim(String arg1, String arg2) {
        return trim(xs.string(arg1), xs.string(arg2)); 
    }
    @Override
        public Xs.StringExpr trim(Xs.StringExpr arg1, Xs.StringExpr arg2) {
        return new XsExprImpl.StringCallImpl("sql", "trim", new Object[]{ arg1, arg2 });
    }
    @Override
        public Xs.IntegerExpr week(Sql.GenericDateTimeExpr arg1) {
        return new XsExprImpl.IntegerCallImpl("sql", "week", new Object[]{ arg1 });
    }
    @Override
        public Xs.IntegerExpr weekday(Sql.GenericDateTimeExpr arg1) {
        return new XsExprImpl.IntegerCallImpl("sql", "weekday", new Object[]{ arg1 });
    }
    @Override
        public Xs.IntegerExpr year(Sql.GenericDateTimeExpr arg1) {
        return new XsExprImpl.IntegerCallImpl("sql", "year", new Object[]{ arg1 });
    }
    @Override
        public Xs.IntegerExpr yearday(Sql.GenericDateTimeExpr arg1) {
        return new XsExprImpl.IntegerCallImpl("sql", "yearday", new Object[]{ arg1 });
    }     @Override
    public Sql.CollatedStringSeqExpr collatedString(Sql.CollatedStringExpr... items) {
        return new SqlExprImpl.CollatedStringSeqListImpl(items);
    }
     @Override
    public Sql.GenericDateTimeSeqExpr genericDateTime(Sql.GenericDateTimeExpr... items) {
        return new SqlExprImpl.GenericDateTimeSeqListImpl(items);
    }
        static class CollatedStringSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CollatedStringSeqExpr {
            CollatedStringSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class CollatedStringSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CollatedStringSeqExpr {
            CollatedStringSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class CollatedStringCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CollatedStringExpr {
            CollatedStringCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class GenericDateTimeSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements GenericDateTimeSeqExpr {
            GenericDateTimeSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class GenericDateTimeSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements GenericDateTimeSeqExpr {
            GenericDateTimeSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class GenericDateTimeCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements GenericDateTimeExpr {
            GenericDateTimeCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }

}
