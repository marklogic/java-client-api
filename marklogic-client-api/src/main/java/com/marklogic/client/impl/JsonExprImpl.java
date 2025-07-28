/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.impl;

import com.marklogic.client.type.XsBooleanVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsUnsignedLongVal;

import com.marklogic.client.type.ServerExpression;

import com.marklogic.client.expression.JsonExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class JsonExprImpl implements JsonExpr {

  final static XsExprImpl xs = XsExprImpl.xs;

  final static JsonExprImpl json = new JsonExprImpl();

  JsonExprImpl() {
  }


  @Override
  public ServerExpression array() {
    return new ArrayCallImpl("json", "array", new Object[]{  });
  }


  @Override
  public ServerExpression array(ServerExpression array) {
    if (array == null) {
      throw new IllegalArgumentException("array parameter for array() cannot be null");
    }
    return new ArrayCallImpl("json", "array", new Object[]{ array });
  }


  @Override
  public ServerExpression arraySize(ServerExpression array) {
    return new XsExprImpl.UnsignedLongCallImpl("json", "array-size", new Object[]{ array });
  }


  @Override
  public ServerExpression arrayValues(ServerExpression array) {
    if (array == null) {
      throw new IllegalArgumentException("array parameter for arrayValues() cannot be null");
    }
    return new BaseTypeImpl.ItemSeqCallImpl("json", "array-values", new Object[]{ array });
  }


  @Override
  public ServerExpression arrayValues(ServerExpression array, boolean flatten) {
    return arrayValues(array, xs.booleanVal(flatten));
  }


  @Override
  public ServerExpression arrayValues(ServerExpression array, ServerExpression flatten) {
    if (array == null) {
      throw new IllegalArgumentException("array parameter for arrayValues() cannot be null");
    }
    return new BaseTypeImpl.ItemSeqCallImpl("json", "array-values", new Object[]{ array, flatten });
  }


  @Override
  public ServerExpression object() {
    return new ObjectCallImpl("json", "object", new Object[]{  });
  }


  @Override
  public ServerExpression object(ServerExpression map) {
    if (map == null) {
      throw new IllegalArgumentException("map parameter for object() cannot be null");
    }
    return new ObjectCallImpl("json", "object", new Object[]{ map });
  }


  @Override
  public ServerExpression objectDefine() {
    return new ObjectCallImpl("json", "object-define", new Object[]{  });
  }


  @Override
  public ServerExpression objectDefine(ServerExpression keys) {
    return new ObjectCallImpl("json", "object-define", new Object[]{ keys });
  }


  @Override
  public ServerExpression subarray(ServerExpression array, double startingLoc) {
    return subarray(array, xs.doubleVal(startingLoc));
  }


  @Override
  public ServerExpression subarray(ServerExpression array, ServerExpression startingLoc) {
    if (array == null) {
      throw new IllegalArgumentException("array parameter for subarray() cannot be null");
    }
    if (startingLoc == null) {
      throw new IllegalArgumentException("startingLoc parameter for subarray() cannot be null");
    }
    return new ArrayCallImpl("json", "subarray", new Object[]{ array, startingLoc });
  }


  @Override
  public ServerExpression subarray(ServerExpression array, double startingLoc, double length) {
    return subarray(array, xs.doubleVal(startingLoc), xs.doubleVal(length));
  }


  @Override
  public ServerExpression subarray(ServerExpression array, ServerExpression startingLoc, ServerExpression length) {
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
  public ServerExpression toArray() {
    return new ArrayCallImpl("json", "to-array", new Object[]{  });
  }


  @Override
  public ServerExpression toArray(ServerExpression items) {
    return new ArrayCallImpl("json", "to-array", new Object[]{ items });
  }


  @Override
  public ServerExpression toArray(ServerExpression items, double limit) {
    return toArray(items, xs.doubleVal(limit));
  }


  @Override
  public ServerExpression toArray(ServerExpression items, ServerExpression limit) {
    return new ArrayCallImpl("json", "to-array", new Object[]{ items, limit });
  }


  @Override
  public ServerExpression toArray(ServerExpression items, double limit, ServerExpression zero) {
    return toArray(items, xs.doubleVal(limit), zero);
  }


  @Override
  public ServerExpression toArray(ServerExpression items, ServerExpression limit, ServerExpression zero) {
    return new ArrayCallImpl("json", "to-array", new Object[]{ items, limit, zero });
  }

  static class ArraySeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    ArraySeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class ArrayCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    ArrayCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  static class ObjectSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    ObjectSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class ObjectCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    ObjectCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  }
