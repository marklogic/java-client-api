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

import com.marklogic.client.expression.Json;
import com.marklogic.client.expression.Xs;
 import com.marklogic.client.expression.BaseType;
 import com.marklogic.client.impl.XsExprImpl;
 import com.marklogic.client.impl.BaseTypeImpl;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class JsonExprImpl implements Json {
    private Xs xs = null;
    public JsonExprImpl(Xs xs) {
        this.xs = xs;
    }
     @Override
        public Json.ArrayExpr array() {
        return new JsonExprImpl.ArrayCallImpl("json", "array", new Object[]{  });
    }
    @Override
        public Json.ArrayExpr array(BaseType.ElementExpr array) {
        return new JsonExprImpl.ArrayCallImpl("json", "array", new Object[]{ array });
    }
    @Override
        public Xs.UnsignedLongExpr arraySize(Json.ArrayExpr array) {
        return new XsExprImpl.UnsignedLongCallImpl("json", "array-size", new Object[]{ array });
    }
    @Override
        public BaseType.ItemSeqExpr arrayValues(Json.ArrayExpr array) {
        return new BaseTypeImpl.ItemSeqCallImpl("json", "array-values", new Object[]{ array });
    }
    @Override
        public BaseType.ItemSeqExpr arrayValues(Json.ArrayExpr array, boolean flatten) {
        return arrayValues(array, xs.booleanVal(flatten)); 
    }
    @Override
        public BaseType.ItemSeqExpr arrayValues(Json.ArrayExpr array, Xs.BooleanExpr flatten) {
        return new BaseTypeImpl.ItemSeqCallImpl("json", "array-values", new Object[]{ array, flatten });
    }
    @Override
        public Json.ArrayExpr arrayWith(Json.ArrayExpr arg1, BaseType.ItemSeqExpr arg2) {
        return new JsonExprImpl.ArrayCallImpl("json", "array-with", new Object[]{ arg1, arg2 });
    }
    @Override
        public Json.ArrayExpr subarray(Json.ArrayExpr array, Xs.NumericExpr startingLoc) {
        return new JsonExprImpl.ArrayCallImpl("json", "subarray", new Object[]{ array, startingLoc });
    }
    @Override
        public Json.ArrayExpr subarray(Json.ArrayExpr array, Xs.NumericExpr startingLoc, Xs.NumericExpr length) {
        return new JsonExprImpl.ArrayCallImpl("json", "subarray", new Object[]{ array, startingLoc, length });
    }
    @Override
        public Json.ArrayExpr toArray() {
        return new JsonExprImpl.ArrayCallImpl("json", "to-array", new Object[]{  });
    }
    @Override
        public Json.ArrayExpr toArray(BaseType.ItemSeqExpr items) {
        return new JsonExprImpl.ArrayCallImpl("json", "to-array", new Object[]{ items });
    }
    @Override
        public Json.ArrayExpr toArray(BaseType.ItemSeqExpr items, Xs.NumericExpr limit) {
        return new JsonExprImpl.ArrayCallImpl("json", "to-array", new Object[]{ items, limit });
    }
    @Override
        public Json.ArrayExpr toArray(BaseType.ItemSeqExpr items, Xs.NumericExpr limit, BaseType.ItemExpr zero) {
        return new JsonExprImpl.ArrayCallImpl("json", "to-array", new Object[]{ items, limit, zero });
    }     @Override
    public Json.ArraySeqExpr array(Json.ArrayExpr... items) {
        return new JsonExprImpl.ArraySeqListImpl(items);
    }
        static class ArraySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements ArraySeqExpr {
            ArraySeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class ArraySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements ArraySeqExpr {
            ArraySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class ArrayCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements ArrayExpr {
            ArrayCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }

}
