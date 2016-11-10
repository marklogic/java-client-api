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

import com.marklogic.client.expression.XsExpr;
import com.marklogic.client.expression.XsValue;

import com.marklogic.client.expression.JsonExpr;
import com.marklogic.client.type.ElementNodeExpr;
 import com.marklogic.client.type.ItemExpr;
 import com.marklogic.client.type.ItemSeqExpr;
 import com.marklogic.client.type.JsonArrayExpr;
 import com.marklogic.client.type.JsonArraySeqExpr;
 import com.marklogic.client.type.JsonObjectExpr;
 import com.marklogic.client.type.JsonObjectSeqExpr;
 import com.marklogic.client.type.XsBooleanExpr;
 import com.marklogic.client.type.XsNumericExpr;
 import com.marklogic.client.type.XsStringSeqExpr;
 import com.marklogic.client.type.XsUnsignedLongExpr;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class JsonExprImpl implements JsonExpr {
    private XsExprImpl xs = null;
    public JsonExprImpl(XsExprImpl xs) {
        this.xs = xs;
    }
     @Override
        public JsonArrayExpr array() {
        return new JsonExprImpl.JsonArrayCallImpl("json", "array", new Object[]{  });
    }
    @Override
        public JsonArrayExpr array(ElementNodeExpr array) {
        return new JsonExprImpl.JsonArrayCallImpl("json", "array", new Object[]{ array });
    }
    @Override
        public XsUnsignedLongExpr arraySize(JsonArrayExpr array) {
        return new XsExprImpl.XsUnsignedLongCallImpl("json", "array-size", new Object[]{ array });
    }
    @Override
        public ItemSeqExpr arrayValues(JsonArrayExpr array) {
        return new BaseTypeImpl.ItemSeqCallImpl("json", "array-values", new Object[]{ array });
    }
    @Override
        public ItemSeqExpr arrayValues(JsonArrayExpr array, boolean flatten) {
        return arrayValues(array, xs.booleanVal(flatten)); 
    }
    @Override
        public ItemSeqExpr arrayValues(JsonArrayExpr array, XsBooleanExpr flatten) {
        return new BaseTypeImpl.ItemSeqCallImpl("json", "array-values", new Object[]{ array, flatten });
    }
    @Override
        public JsonObjectExpr object() {
        return new JsonExprImpl.JsonObjectCallImpl("json", "object", new Object[]{  });
    }
    @Override
        public JsonObjectExpr object(ElementNodeExpr map) {
        return new JsonExprImpl.JsonObjectCallImpl("json", "object", new Object[]{ map });
    }
    @Override
        public JsonObjectExpr objectDefine() {
        return new JsonExprImpl.JsonObjectCallImpl("json", "object-define", new Object[]{  });
    }
    @Override
        public JsonObjectExpr objectDefine(String keys) {
        return objectDefine((keys == null) ? null : xs.strings(keys)); 
    }
    @Override
        public JsonObjectExpr objectDefine(XsStringSeqExpr keys) {
        return new JsonExprImpl.JsonObjectCallImpl("json", "object-define", new Object[]{ keys });
    }
    @Override
        public JsonArrayExpr subarray(JsonArrayExpr array, XsNumericExpr startingLoc) {
        return new JsonExprImpl.JsonArrayCallImpl("json", "subarray", new Object[]{ array, startingLoc });
    }
    @Override
        public JsonArrayExpr subarray(JsonArrayExpr array, XsNumericExpr startingLoc, XsNumericExpr length) {
        return new JsonExprImpl.JsonArrayCallImpl("json", "subarray", new Object[]{ array, startingLoc, length });
    }
    @Override
        public JsonArrayExpr toArray() {
        return new JsonExprImpl.JsonArrayCallImpl("json", "to-array", new Object[]{  });
    }
    @Override
        public JsonArrayExpr toArray(ItemSeqExpr items) {
        return new JsonExprImpl.JsonArrayCallImpl("json", "to-array", new Object[]{ items });
    }
    @Override
        public JsonArrayExpr toArray(ItemSeqExpr items, XsNumericExpr limit) {
        return new JsonExprImpl.JsonArrayCallImpl("json", "to-array", new Object[]{ items, limit });
    }
    @Override
        public JsonArrayExpr toArray(ItemSeqExpr items, XsNumericExpr limit, ItemExpr zero) {
        return new JsonExprImpl.JsonArrayCallImpl("json", "to-array", new Object[]{ items, limit, zero });
    }     @Override
    public JsonArraySeqExpr array(JsonArrayExpr... items) {
        return new JsonArraySeqListImpl(items);
    }
     @Override
    public JsonObjectSeqExpr object(JsonObjectExpr... items) {
        return new JsonObjectSeqListImpl(items);
    }
        static class JsonArraySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements JsonArraySeqExpr {
            JsonArraySeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class JsonArraySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements JsonArraySeqExpr {
            JsonArraySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class JsonArrayCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements JsonArrayExpr {
            JsonArrayCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class JsonObjectSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements JsonObjectSeqExpr {
            JsonObjectSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class JsonObjectSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements JsonObjectSeqExpr {
            JsonObjectSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class JsonObjectCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements JsonObjectExpr {
            JsonObjectCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }

}
