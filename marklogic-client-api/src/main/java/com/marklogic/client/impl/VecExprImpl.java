/*
 * Copyright (c) 2024 MarkLogic Corporation
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

import com.marklogic.client.type.XsAnyAtomicTypeSeqVal;
import com.marklogic.client.type.XsDoubleVal;
import com.marklogic.client.type.XsFloatVal;
import com.marklogic.client.type.XsStringVal;
import com.marklogic.client.type.XsUnsignedIntVal;

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
    if (vector1 == null) {
      throw new IllegalArgumentException("vector1 parameter for add() cannot be null");
    }
    if (vector2 == null) {
      throw new IllegalArgumentException("vector2 parameter for add() cannot be null");
    }
    return new VectorCallImpl("vec", "add", new Object[]{ vector1, vector2 });
  }

  
  @Override
  public ServerExpression base64Decode(ServerExpression base64Vector) {
    if (base64Vector == null) {
      throw new IllegalArgumentException("base64Vector parameter for base64Decode() cannot be null");
    }
    return new VectorCallImpl("vec", "base64-decode", new Object[]{ base64Vector });
  }

  
  @Override
  public ServerExpression base64Encode(ServerExpression vector1) {
    if (vector1 == null) {
      throw new IllegalArgumentException("vector1 parameter for base64Encode() cannot be null");
    }
    return new XsExprImpl.StringCallImpl("vec", "base64-encode", new Object[]{ vector1 });
  }

  
  @Override
  public ServerExpression cosineSimilarity(ServerExpression vector1, ServerExpression vector2) {
    if (vector1 == null) {
      throw new IllegalArgumentException("vector1 parameter for cosineSimilarity() cannot be null");
    }
    if (vector2 == null) {
      throw new IllegalArgumentException("vector2 parameter for cosineSimilarity() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("vec", "cosine-similarity", new Object[]{ vector1, vector2 });
  }

  
  @Override
  public ServerExpression dimension(ServerExpression vector1) {
    if (vector1 == null) {
      throw new IllegalArgumentException("vector1 parameter for dimension() cannot be null");
    }
    return new XsExprImpl.UnsignedIntCallImpl("vec", "dimension", new Object[]{ vector1 });
  }

  
  @Override
  public ServerExpression dotProduct(ServerExpression vector1, ServerExpression vector2) {
    if (vector1 == null) {
      throw new IllegalArgumentException("vector1 parameter for dotProduct() cannot be null");
    }
    if (vector2 == null) {
      throw new IllegalArgumentException("vector2 parameter for dotProduct() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("vec", "dot-product", new Object[]{ vector1, vector2 });
  }

  
  @Override
  public ServerExpression euclideanDistance(ServerExpression vector1, ServerExpression vector2) {
    if (vector1 == null) {
      throw new IllegalArgumentException("vector1 parameter for euclideanDistance() cannot be null");
    }
    if (vector2 == null) {
      throw new IllegalArgumentException("vector2 parameter for euclideanDistance() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("vec", "euclidean-distance", new Object[]{ vector1, vector2 });
  }

  
  @Override
  public ServerExpression get(ServerExpression vector1, ServerExpression k) {
    if (vector1 == null) {
      throw new IllegalArgumentException("vector1 parameter for get() cannot be null");
    }
    if (k == null) {
      throw new IllegalArgumentException("k parameter for get() cannot be null");
    }
    return new XsExprImpl.FloatCallImpl("vec", "get", new Object[]{ vector1, k });
  }

  
  @Override
  public ServerExpression magnitude(ServerExpression vector1) {
    if (vector1 == null) {
      throw new IllegalArgumentException("vector1 parameter for magnitude() cannot be null");
    }
    return new XsExprImpl.DoubleCallImpl("vec", "magnitude", new Object[]{ vector1 });
  }

  
  @Override
  public ServerExpression normalize(ServerExpression vector1) {
    if (vector1 == null) {
      throw new IllegalArgumentException("vector1 parameter for normalize() cannot be null");
    }
    return new VectorCallImpl("vec", "normalize", new Object[]{ vector1 });
  }

  
  @Override
  public ServerExpression subtract(ServerExpression vector1, ServerExpression vector2) {
    if (vector1 == null) {
      throw new IllegalArgumentException("vector1 parameter for subtract() cannot be null");
    }
    if (vector2 == null) {
      throw new IllegalArgumentException("vector2 parameter for subtract() cannot be null");
    }
    return new VectorCallImpl("vec", "subtract", new Object[]{ vector1, vector2 });
  }

  
  @Override
  public ServerExpression subvector(ServerExpression vector, ServerExpression start, ServerExpression length) {
    if (vector == null) {
      throw new IllegalArgumentException("vector parameter for subvector() cannot be null");
    }
    if (start == null) {
      throw new IllegalArgumentException("start parameter for subvector() cannot be null");
    }
    if (length == null) {
      throw new IllegalArgumentException("length parameter for subvector() cannot be null");
    }
    return new VectorCallImpl("vec", "subvector", new Object[]{ vector, start, length });
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
    if (score == null) {
      throw new IllegalArgumentException("score parameter for vectorScore() cannot be null");
    }
    if (similarity == null) {
      throw new IllegalArgumentException("similarity parameter for vectorScore() cannot be null");
    }
    return new XsExprImpl.UnsignedIntCallImpl("vec", "vector-score", new Object[]{ score, similarity });
  }

  
  @Override
  public ServerExpression vectorScore(ServerExpression score, double similarity, double similarityWeight) {
    return vectorScore(score, xs.doubleVal(similarity), xs.doubleVal(similarityWeight));
  }

  
  @Override
  public ServerExpression vectorScore(ServerExpression score, ServerExpression similarity, ServerExpression similarityWeight) {
    if (score == null) {
      throw new IllegalArgumentException("score parameter for vectorScore() cannot be null");
    }
    if (similarity == null) {
      throw new IllegalArgumentException("similarity parameter for vectorScore() cannot be null");
    }
    return new XsExprImpl.UnsignedIntCallImpl("vec", "vector-score", new Object[]{ score, similarity, similarityWeight });
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
