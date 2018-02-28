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
package com.marklogic.client.impl;

import com.marklogic.client.type.ElementNodeExpr;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.XsBooleanExpr;
import com.marklogic.client.type.XsStringExpr;
import com.marklogic.client.type.XsStringSeqExpr;
import com.marklogic.client.type.XsUnsignedIntExpr;

import com.marklogic.client.type.MapMapExpr;
import com.marklogic.client.type.MapMapSeqExpr;

import com.marklogic.client.expression.MapExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class MapExprImpl implements MapExpr {

  final static XsExprImpl xs = XsExprImpl.xs;

  final static MapExprImpl map = new MapExprImpl();

  MapExprImpl() {
  }

    
  @Override
  public XsBooleanExpr contains(MapMapExpr map, String key) {
    return contains(map, (key == null) ? (XsStringExpr) null : xs.string(key));
  }

  
  @Override
  public XsBooleanExpr contains(MapMapExpr map, XsStringExpr key) {
    if (map == null) {
      throw new IllegalArgumentException("map parameter for contains() cannot be null");
    }
    if (key == null) {
      throw new IllegalArgumentException("key parameter for contains() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("map", "contains", new Object[]{ map, key });
  }

  
  @Override
  public XsUnsignedIntExpr count(MapMapExpr map) {
    if (map == null) {
      throw new IllegalArgumentException("map parameter for count() cannot be null");
    }
    return new XsExprImpl.UnsignedIntCallImpl("map", "count", new Object[]{ map });
  }

  
  @Override
  public MapMapExpr entry(XsStringExpr key, ItemSeqExpr value) {
    if (key == null) {
      throw new IllegalArgumentException("key parameter for entry() cannot be null");
    }
    return new MapCallImpl("map", "entry", new Object[]{ key, value });
  }

  
  @Override
  public ItemSeqExpr get(MapMapExpr map, String key) {
    return get(map, (key == null) ? (XsStringExpr) null : xs.string(key));
  }

  
  @Override
  public ItemSeqExpr get(MapMapExpr map, XsStringExpr key) {
    if (map == null) {
      throw new IllegalArgumentException("map parameter for get() cannot be null");
    }
    if (key == null) {
      throw new IllegalArgumentException("key parameter for get() cannot be null");
    }
    return new BaseTypeImpl.ItemSeqCallImpl("map", "get", new Object[]{ map, key });
  }

  
  @Override
  public XsStringSeqExpr keys(MapMapExpr map) {
    if (map == null) {
      throw new IllegalArgumentException("map parameter for keys() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("map", "keys", new Object[]{ map });
  }

  
  @Override
  public MapMapExpr map() {
    return new MapCallImpl("map", "map", new Object[]{  });
  }

  
  @Override
  public MapMapExpr map(ElementNodeExpr map) {
    if (map == null) {
      throw new IllegalArgumentException("map parameter for map() cannot be null");
    }
    return new MapCallImpl("map", "map", new Object[]{ map });
  }

  @Override
  public MapMapSeqExpr mapSeq(MapMapExpr... items) {
    return new MapSeqListImpl(items);
  }
  static class MapSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements MapMapSeqExpr {
    MapSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class MapSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements MapMapSeqExpr {
    MapSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class MapCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements MapMapExpr {
    MapCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }

  }
