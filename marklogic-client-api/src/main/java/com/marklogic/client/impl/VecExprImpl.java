/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */

package com.marklogic.client.impl;

import com.marklogic.client.type.XsAnyAtomicTypeSeqVal;
import com.marklogic.client.type.XsDoubleVal;
import com.marklogic.client.type.XsFloatVal;
import com.marklogic.client.type.XsStringVal;
import com.marklogic.client.type.XsUnsignedIntVal;
import com.marklogic.client.type.XsUnsignedLongVal;

import com.marklogic.client.type.ServerExpression;

import com.marklogic.client.expression.VecExpr;
import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.
class VecExprImpl implements VecExpr {

  final static XsExprImpl xs = XsExprImpl.xs;

  final static VecExprImpl vec = new VecExprImpl();

  VecExprImpl() {
  }

    
  @Override
  public ServerExpression add(ServerExpression vector1, ServerExpression vector2) {
    return new VectorCallImpl("vec", "add", new Object[]{ vector1, vector2 });
  }

  
  @Override
  public ServerExpression base64Decode(ServerExpression base64Vector) {
    return new VectorCallImpl("vec", "base64-decode", new Object[]{ base64Vector });
  }

  
  @Override
  public ServerExpression base64Encode(ServerExpression vector1) {
    return new XsExprImpl.StringCallImpl("vec", "base64-encode", new Object[]{ vector1 });
  }

  
  @Override
  public ServerExpression cosine(ServerExpression arg1, ServerExpression arg2) {
    return new XsExprImpl.DoubleCallImpl("vec", "cosine", new Object[]{ arg1, arg2 });
  }

  
  @Override
  public ServerExpression cosineDistance(ServerExpression arg1, ServerExpression arg2) {
    return new XsExprImpl.DoubleCallImpl("vec", "cosine-distance", new Object[]{ arg1, arg2 });
  }

  
  @Override
  public ServerExpression dimension(ServerExpression vector1) {
    return new XsExprImpl.UnsignedIntCallImpl("vec", "dimension", new Object[]{ vector1 });
  }

  
  @Override
  public ServerExpression dotProduct(ServerExpression vector1, ServerExpression vector2) {
    return new XsExprImpl.DoubleCallImpl("vec", "dot-product", new Object[]{ vector1, vector2 });
  }

  
  @Override
  public ServerExpression euclideanDistance(ServerExpression vector1, ServerExpression vector2) {
    return new XsExprImpl.DoubleCallImpl("vec", "euclidean-distance", new Object[]{ vector1, vector2 });
  }

  
  @Override
  public ServerExpression get(ServerExpression vector1, ServerExpression k) {
    return new XsExprImpl.FloatCallImpl("vec", "get", new Object[]{ vector1, k });
  }

  
  @Override
  public ServerExpression magnitude(ServerExpression vector1) {
    return new XsExprImpl.DoubleCallImpl("vec", "magnitude", new Object[]{ vector1 });
  }

  
  @Override
  public ServerExpression normalize(ServerExpression vector1) {
    return new VectorCallImpl("vec", "normalize", new Object[]{ vector1 });
  }

  
  @Override
  public ServerExpression precision(ServerExpression vector) {
    return new VectorCallImpl("vec", "precision", new Object[]{ vector });
  }

  
  @Override
  public ServerExpression precision(ServerExpression vector, ServerExpression precision) {
    return new VectorCallImpl("vec", "precision", new Object[]{ vector, precision });
  }

  
  @Override
  public ServerExpression subtract(ServerExpression vector1, ServerExpression vector2) {
    return new VectorCallImpl("vec", "subtract", new Object[]{ vector1, vector2 });
  }

  
  @Override
  public ServerExpression subvector(ServerExpression vector, ServerExpression start) {
    return new VectorCallImpl("vec", "subvector", new Object[]{ vector, start });
  }

  
  @Override
  public ServerExpression subvector(ServerExpression vector, ServerExpression start, ServerExpression length) {
    return new VectorCallImpl("vec", "subvector", new Object[]{ vector, start, length });
  }

  
  @Override
  public ServerExpression trunc(ServerExpression vector) {
    return new VectorCallImpl("vec", "trunc", new Object[]{ vector });
  }

  
  @Override
  public ServerExpression trunc(ServerExpression vector, int n) {
    return trunc(vector, xs.intVal(n));
  }

  
  @Override
  public ServerExpression trunc(ServerExpression vector, ServerExpression n) {
    return new VectorCallImpl("vec", "trunc", new Object[]{ vector, n });
  }

  
  @Override
  public ServerExpression vector(ServerExpression values) {
    return new VectorCallImpl("vec", "vector", new Object[]{ values });
  }

  
  @Override
  public ServerExpression vectorScore(ServerExpression score, double similarity) {
    return vectorScore(score, xs.doubleVal(similarity));
  }

  
  @Override
  public ServerExpression vectorScore(ServerExpression score, ServerExpression similarity) {
    return new XsExprImpl.UnsignedLongCallImpl("vec", "vector-score", new Object[]{ score, similarity });
  }

  
  @Override
  public ServerExpression vectorScore(ServerExpression score, double similarity, double similarityWeight) {
    return vectorScore(score, xs.doubleVal(similarity), xs.doubleVal(similarityWeight));
  }

  
  @Override
  public ServerExpression vectorScore(ServerExpression score, ServerExpression similarity, ServerExpression similarityWeight) {
    return new XsExprImpl.UnsignedLongCallImpl("vec", "vector-score", new Object[]{ score, similarity, similarityWeight });
  }

  static class VectorSeqCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    VectorSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }
  static class VectorCallImpl extends BaseTypeImpl.ServerExpressionCallImpl {
    VectorCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
      super(fnPrefix, fnName, fnArgs);
    }
  }

  }
