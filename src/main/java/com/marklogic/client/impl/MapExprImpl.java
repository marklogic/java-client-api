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

import com.marklogic.client.expression.Map;
import com.marklogic.client.type.XsStringExpr;
 import com.marklogic.client.type.XsStringSeqExpr;
 import com.marklogic.client.type.NodeElementExpr;
 import com.marklogic.client.type.XsUnsignedIntExpr;
 import com.marklogic.client.type.MapMapExpr;
 import com.marklogic.client.type.XsBooleanExpr;
 import com.marklogic.client.type.ItemExpr;
 import com.marklogic.client.type.ItemSeqExpr;
 import com.marklogic.client.type.MapMapSeqExpr;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class MapExprImpl implements Map {
    private XsExprImpl xs = null;
    public MapExprImpl(XsExprImpl xs) {
        this.xs = xs;
    }
     @Override
        public XsBooleanExpr contains(MapMapExpr map, String key) {
        return contains(map, xs.string(key)); 
    }
    @Override
        public XsBooleanExpr contains(MapMapExpr map, XsStringExpr key) {
        return new XsExprImpl.XsBooleanCallImpl("map", "contains", new Object[]{ map, key });
    }
    @Override
        public XsUnsignedIntExpr count(MapMapExpr map) {
        return new XsExprImpl.XsUnsignedIntCallImpl("map", "count", new Object[]{ map });
    }
    @Override
        public MapMapExpr entry(String key, ItemExpr... value) {
        return entry(xs.string(key), BaseTypeImpl.items(value)); 
    }
    @Override
        public MapMapExpr entry(XsStringExpr key, ItemSeqExpr value) {
        return new MapExprImpl.MapMapCallImpl("map", "entry", new Object[]{ key, value });
    }
    @Override
        public ItemSeqExpr get(MapMapExpr map, String key) {
        return get(map, xs.string(key)); 
    }
    @Override
        public ItemSeqExpr get(MapMapExpr map, XsStringExpr key) {
        return new BaseTypeImpl.ItemSeqCallImpl("map", "get", new Object[]{ map, key });
    }
    @Override
        public XsStringSeqExpr keys(MapMapExpr map) {
        return new XsExprImpl.XsStringSeqCallImpl("map", "keys", new Object[]{ map });
    }
    @Override
        public MapMapExpr map() {
        return new MapExprImpl.MapMapCallImpl("map", "map", new Object[]{  });
    }
    @Override
        public MapMapExpr map(NodeElementExpr map) {
        return new MapExprImpl.MapMapCallImpl("map", "map", new Object[]{ map });
    }     @Override
    public MapMapSeqExpr map(MapMapExpr... items) {
        return new MapMapSeqListImpl(items);
    }
        static class MapMapSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements MapMapSeqExpr {
            MapMapSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class MapMapSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements MapMapSeqExpr {
            MapMapSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class MapMapCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements MapMapExpr {
            MapMapCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }

}
