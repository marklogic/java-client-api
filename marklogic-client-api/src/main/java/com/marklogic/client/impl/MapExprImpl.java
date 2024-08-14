/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.impl;

import com.marklogic.client.type.XsBooleanVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsStringVal;
import com.marklogic.client.type.XsUnsignedIntVal;

import com.marklogic.client.type.ServerExpression;

import com.marklogic.client.expression.MapExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class MapExprImpl implements MapExpr {

  final static XsExprImpl xs = XsExprImpl.xs;

  final static MapExprImpl map = new MapExprImpl();

  MapExprImpl() {
  }


  @Override
  public ServerExpression contains(ServerExpression map, String key) {
    return contains(map, (key == null) ? (ServerExpression) null : xs.string(key));
  }


  @Override
  public ServerExpression contains(ServerExpression map, ServerExpression key) {
    if (map == null) {
      throw new IllegalArgumentException("map parameter for contains() cannot be null");
    }
    if (key == null) {
      throw new IllegalArgumentException("key parameter for contains() cannot be null");
    }
    return new XsExprImpl.BooleanCallImpl("map", "contains", new Object[]{ map, key });
  }


  @Override
  public ServerExpression count(ServerExpression map) {
    if (map == null) {
      throw new IllegalArgumentException("map parameter for count() cannot be null");
    }
    return new XsExprImpl.UnsignedIntCallImpl("map", "count", new Object[]{ map });
  }


  @Override
  public ServerExpression entry(ServerExpression key, ServerExpression value) {
    if (key == null) {
      throw new IllegalArgumentException("key parameter for entry() cannot be null");
    }
    return new MapCallImpl("map", "entry", new Object[]{ key, value });
  }


  @Override
  public ServerExpression get(ServerExpression map, String key) {
    return get(map, (key == null) ? (ServerExpression) null : xs.string(key));
  }


  @Override
  public ServerExpression get(ServerExpression map, ServerExpression key) {
    if (map == null) {
      throw new IllegalArgumentException("map parameter for get() cannot be null");
    }
    if (key == null) {
      throw new IllegalArgumentException("key parameter for get() cannot be null");
    }
    return new BaseTypeImpl.ItemSeqCallImpl("map", "get", new Object[]{ map, key });
  }


  @Override
  public ServerExpression keys(ServerExpression map) {
    if (map == null) {
      throw new IllegalArgumentException("map parameter for keys() cannot be null");
    }
    return new XsExprImpl.StringSeqCallImpl("map", "keys", new Object[]{ map });
  }


  @Override
  public ServerExpression map() {
    return new MapCallImpl("map", "map", new Object[]{  });
  }


  @Override
  public ServerExpression map(ServerExpression map) {
    if (map == null) {
      throw new IllegalArgumentException("map parameter for map() cannot be null");
    }
    return new MapCallImpl("map", "map", new Object[]{ map });
  }


  @Override
  public ServerExpression newExpr() {
    return new MapCallImpl("map", "new", new Object[]{  });
  }


  @Override
  public ServerExpression newExpr(ServerExpression maps) {
    return new MapCallImpl("map", "new", new Object[]{ maps });
  }

  static class MapSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    MapSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class MapCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    MapCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  }
