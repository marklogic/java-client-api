/*
 * Copyright 2016-2018 MarkLogic Corporation
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
import com.marklogic.client.type.ItemExpr;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.XsBooleanExpr;
import com.marklogic.client.type.XsNumericExpr;
import com.marklogic.client.type.XsStringSeqExpr;
import com.marklogic.client.type.XsUnsignedLongExpr;

import com.marklogic.client.type.JsonArrayExpr;
import com.marklogic.client.type.JsonArraySeqExpr;
import com.marklogic.client.type.JsonObjectExpr;
import com.marklogic.client.type.JsonObjectSeqExpr;

import com.marklogic.client.expression.JsonExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class JsonExprImpl implements JsonExpr {

  final static XsExprImpl xs = XsExprImpl.xs;

  final static JsonExprImpl json = new JsonExprImpl();

  JsonExprImpl() {
  }

    
  @Override
  public JsonArrayExpr array() {
    return new ArrayCallImpl("json", "array", new Object[]{  });
  }

  
  @Override
  public JsonArrayExpr array(ElementNodeExpr array) {
    if (array == null) {
      throw new IllegalArgumentException("array parameter for array() cannot be null");
    }
    return new ArrayCallImpl("json", "array", new Object[]{ array });
  }

  
  @Override
  public XsUnsignedLongExpr arraySize(JsonArrayExpr array) {
    return new XsExprImpl.UnsignedLongCallImpl("json", "array-size", new Object[]{ array });
  }

  
  @Override
  public ItemSeqExpr arrayValues(JsonArrayExpr array) {
    if (array == null) {
      throw new IllegalArgumentException("array parameter for arrayValues() cannot be null");
    }
    return new BaseTypeImpl.ItemSeqCallImpl("json", "array-values", new Object[]{ array });
  }

  
  @Override
  public ItemSeqExpr arrayValues(JsonArrayExpr array, boolean flatten) {
    return arrayValues(array, xs.booleanVal(flatten));
  }

  
  @Override
  public ItemSeqExpr arrayValues(JsonArrayExpr array, XsBooleanExpr flatten) {
    if (array == null) {
      throw new IllegalArgumentException("array parameter for arrayValues() cannot be null");
    }
    return new BaseTypeImpl.ItemSeqCallImpl("json", "array-values", new Object[]{ array, flatten });
  }

  
  @Override
  public JsonObjectExpr object() {
    return new ObjectCallImpl("json", "object", new Object[]{  });
  }

  
  @Override
  public JsonObjectExpr object(ElementNodeExpr map) {
    if (map == null) {
      throw new IllegalArgumentException("map parameter for object() cannot be null");
    }
    return new ObjectCallImpl("json", "object", new Object[]{ map });
  }

  
  @Override
  public JsonObjectExpr objectDefine() {
    return new ObjectCallImpl("json", "object-define", new Object[]{  });
  }

  
  @Override
  public JsonObjectExpr objectDefine(XsStringSeqExpr keys) {
    return new ObjectCallImpl("json", "object-define", new Object[]{ keys });
  }

  
  @Override
  public JsonArrayExpr subarray(JsonArrayExpr array, double startingLoc) {
    return subarray(array, xs.doubleVal(startingLoc));
  }

  
  @Override
  public JsonArrayExpr subarray(JsonArrayExpr array, XsNumericExpr startingLoc) {
    if (array == null) {
      throw new IllegalArgumentException("array parameter for subarray() cannot be null");
    }
    if (startingLoc == null) {
      throw new IllegalArgumentException("startingLoc parameter for subarray() cannot be null");
    }
    return new ArrayCallImpl("json", "subarray", new Object[]{ array, startingLoc });
  }

  
  @Override
  public JsonArrayExpr subarray(JsonArrayExpr array, double startingLoc, double length) {
    return subarray(array, xs.doubleVal(startingLoc), xs.doubleVal(length));
  }

  
  @Override
  public JsonArrayExpr subarray(JsonArrayExpr array, XsNumericExpr startingLoc, XsNumericExpr length) {
    if (array == null) {
      throw new IllegalArgumentException("array parameter for subarray() cannot be null");
    }
    if (startingLoc == null) {
      throw new IllegalArgumentException("startingLoc parameter for subarray() cannot be null");
    }
    if (length == null) {
      throw new IllegalArgumentException("length parameter for subarray() cannot be null");
    }
    return new ArrayCallImpl("json", "subarray", new Object[]{ array, startingLoc, length });
  }

  
  @Override
  public JsonArrayExpr toArray() {
    return new ArrayCallImpl("json", "to-array", new Object[]{  });
  }

  
  @Override
  public JsonArrayExpr toArray(ItemSeqExpr items) {
    return new ArrayCallImpl("json", "to-array", new Object[]{ items });
  }

  
  @Override
  public JsonArrayExpr toArray(ItemSeqExpr items, double limit) {
    return toArray(items, xs.doubleVal(limit));
  }

  
  @Override
  public JsonArrayExpr toArray(ItemSeqExpr items, XsNumericExpr limit) {
    return new ArrayCallImpl("json", "to-array", new Object[]{ items, limit });
  }

  
  @Override
  public JsonArrayExpr toArray(ItemSeqExpr items, double limit, ItemExpr zero) {
    return toArray(items, xs.doubleVal(limit), zero);
  }

  
  @Override
  public JsonArrayExpr toArray(ItemSeqExpr items, XsNumericExpr limit, ItemExpr zero) {
    return new ArrayCallImpl("json", "to-array", new Object[]{ items, limit, zero });
  }

  @Override
  public JsonArraySeqExpr arraySeq(JsonArrayExpr... items) {
    return new ArraySeqListImpl(items);
  }
  static class ArraySeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements JsonArraySeqExpr {
    ArraySeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class ArraySeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements JsonArraySeqExpr {
    ArraySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class ArrayCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements JsonArrayExpr {
    ArrayCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
 
  @Override
  public JsonObjectSeqExpr objectSeq(JsonObjectExpr... items) {
    return new ObjectSeqListImpl(items);
  }
  static class ObjectSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements JsonObjectSeqExpr {
    ObjectSeqListImpl(Object[] items) {
      super(BaseTypeImpl.convertList(items));
    }
  }
  static class ObjectSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements JsonObjectSeqExpr {
    ObjectSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }
  static class ObjectCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements JsonObjectExpr {
    ObjectCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
    }
  }

  }
