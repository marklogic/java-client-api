/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.expression;

import com.marklogic.client.type.XsAnyAtomicTypeSeqVal;
import com.marklogic.client.type.XsDoubleVal;
import com.marklogic.client.type.XsFloatVal;
import com.marklogic.client.type.XsStringVal;
import com.marklogic.client.type.XsUnsignedIntVal;
import com.marklogic.client.type.XsUnsignedLongVal;

import com.marklogic.client.type.ServerExpression;

// IMPORTANT: Do not edit. This file is generated.

/**
 * Builds expressions to call functions in the vec server library for a row
 * pipeline.
 */
public interface VecExpr {
    /**
  * Returns the sum of two vectors. The vectors must be of the same dimension.
  *
  * <a name="ml-server-type-add"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:add" target="mlserverdoc">vec:add</a> server function.
  * @param vector1  The first addend vector.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @param vector2  The second addend vector.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a> server data type
  */
  public ServerExpression add(ServerExpression vector1, ServerExpression vector2);
/**
  * Returns a vector value by decoding the base64 encoded string input.
  *
  * <a name="ml-server-type-base64-decode"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:base64-decode" target="mlserverdoc">vec:base64-decode</a> server function.
  * @param base64Vector  The base64 binary encoded string vector to decode.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a> server data type
  */
  public ServerExpression base64Decode(ServerExpression base64Vector);
/**
  * Returns the base64 encoding of the vector. Useful for compressing a high-dimensional float vector represented as a string into fewer characters.
  *
  * <a name="ml-server-type-base64-encode"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:base64-encode" target="mlserverdoc">vec:base64-encode</a> server function.
  * @param vector1  The vector to base64 encode.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression base64Encode(ServerExpression vector1);
/**
  * Returns the cosine similarity between two vectors. The vectors must be of the same dimension.
  *
  * <a name="ml-server-type-cosine-similarity"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:cosine-similarity" target="mlserverdoc">vec:cosine-similarity</a> server function.
  * @param vector1  The vector from which to calculate the cosine similarity with vector2.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @param vector2  The vector from which to calculate the cosine similarity with vector1.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression cosineSimilarity(ServerExpression vector1, ServerExpression vector2);
/**
  * Returns the dimension of the vector passed in.
  *
  * <a name="ml-server-type-dimension"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:dimension" target="mlserverdoc">vec:dimension</a> server function.
  * @param vector1  The vector to find the dimension of.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a> server data type
  */
  public ServerExpression dimension(ServerExpression vector1);
/**
  * Returns the dot product between two vectors. The vectors must be of the same dimension. Use this function to calculate similarity between vectors if you are certain they are both of magnitude 1.
  *
  * <a name="ml-server-type-dot-product"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:dot-product" target="mlserverdoc">vec:dot-product</a> server function.
  * @param vector1  The vector from which to calculate the dot product with vector2.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @param vector2  The vector from which to calculate the dot product with vector1.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression dotProduct(ServerExpression vector1, ServerExpression vector2);
/**
  * Returns the Euclidean distance between two vectors. The vectors must be of the same dimension.
  *
  * <a name="ml-server-type-euclidean-distance"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:euclidean-distance" target="mlserverdoc">vec:euclidean-distance</a> server function.
  * @param vector1  The vector from which to calculate the Euclidean distance to vector2.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @param vector2  The vector from which to calculate the Euclidean distance to vector1.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression euclideanDistance(ServerExpression vector1, ServerExpression vector2);
/**
  * Returns the element at the k-th index of the vector.
  *
  * <a name="ml-server-type-get"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:get" target="mlserverdoc">vec:get</a> server function.
  * @param vector1  The vector to grab the k-th element of.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @param k  The zero-based index of vector1 to return. If k is greater than the number of elements in the vector, throw VEC-OUTOFBOUNDS.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_float.html">xs:float</a> server data type
  */
  public ServerExpression get(ServerExpression vector1, ServerExpression k);
/**
  * Returns the magnitude of the vector.
  *
  * <a name="ml-server-type-magnitude"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:magnitude" target="mlserverdoc">vec:magnitude</a> server function.
  * @param vector1  The vector to find the magnitude of.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  */
  public ServerExpression magnitude(ServerExpression vector1);
/**
  * Returns the vector passed in, normalized to a length of 1.
  *
  * <a name="ml-server-type-normalize"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:normalize" target="mlserverdoc">vec:normalize</a> server function.
  * @param vector1  The vector to normalize.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a> server data type
  */
  public ServerExpression normalize(ServerExpression vector1);
/**
  * Returns the difference of two vectors. The vectors must be of the same dimension.
  *
  * <a name="ml-server-type-subtract"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:subtract" target="mlserverdoc">vec:subtract</a> server function.
  * @param vector1  The minuend vector.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @param vector2  The subtrahend vector.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a> server data type
  */
  public ServerExpression subtract(ServerExpression vector1, ServerExpression vector2);
/**
  * Returns a subvector of the input vector, starting at the specified index, with the specified length (optional).
  *
  * <a name="ml-server-type-subvector"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:subvector" target="mlserverdoc">vec:subvector</a> server function.
  * @param vector  The input vector.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @param start  The zero-based index of the input vector from which to start (inclusive).  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a> server data type
  */
  public ServerExpression subvector(ServerExpression vector, ServerExpression start);
/**
  * Returns a subvector of the input vector, starting at the specified index, with the specified length (optional).
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:subvector" target="mlserverdoc">vec:subvector</a> server function.
  * @param vector  The input vector.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @param start  The zero-based index of the input vector from which to start (inclusive).  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param length  The length of the subvector. If not provided, returns a subvector beginning from index at start to the end of the input vector. If length is greater than the number of elements left in the input vector, throw VEC-OUTOFBOUNDS.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a> server data type
  */
  public ServerExpression subvector(ServerExpression vector, ServerExpression start, ServerExpression length);
/**
  * Returns a vector value.
  *
  * <a name="ml-server-type-vector"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:vector" target="mlserverdoc">vec:vector</a> server function.
  * @param values  The value(s) to create the vector from. Can be a sequence or json:array of integer or floating-point numbers. Also accepts a string that has the format of a JSON array of Numbers. Also accepts a string that was created by vec:base64-encode().  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a> server data type
  */
  public ServerExpression vector(ServerExpression values);
/**
  * A helper function that returns a hybrid score using a cts score and a vector similarity calculation result. You can tune the effect of the vector similarity on the score using the similarityWeight option. The ideal value for similarityWeight depends on your application.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:vector-score" target="mlserverdoc">vec:vector-score</a> server function.
  * @param score  The cts:score of the matching document.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param similarity  The similarity between the vector in the matching document and the query vector. The result of a call to ovec:cosine-similarity(). In the case that the vectors are normalized, pass ovec:dot-product(). Note that vec:euclidean-distance() should not be used here.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression vectorScore(ServerExpression score, double similarity);
/**
  * A helper function that returns a hybrid score using a cts score and a vector similarity calculation result. You can tune the effect of the vector similarity on the score using the similarityWeight option. The ideal value for similarityWeight depends on your application.
  *
  * <a name="ml-server-type-vector-score"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:vector-score" target="mlserverdoc">vec:vector-score</a> server function.
  * @param score  The cts:score of the matching document.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param similarity  The similarity between the vector in the matching document and the query vector. The result of a call to ovec:cosine-similarity(). In the case that the vectors are normalized, pass ovec:dot-product(). Note that vec:euclidean-distance() should not be used here.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression vectorScore(ServerExpression score, ServerExpression similarity);
/**
  * A helper function that returns a hybrid score using a cts score and a vector similarity calculation result. You can tune the effect of the vector similarity on the score using the similarityWeight option. The ideal value for similarityWeight depends on your application.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:vector-score" target="mlserverdoc">vec:vector-score</a> server function.
  * @param score  The cts:score of the matching document.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param similarity  The similarity between the vector in the matching document and the query vector. The result of a call to ovec:cosine-similarity(). In the case that the vectors are normalized, pass ovec:dot-product(). Note that vec:euclidean-distance() should not be used here.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param similarityWeight  The weight of the vector similarity on the score. The default value is 0.1. If 0.0 is passed in, vector similarity has no effect. If passed a value less than 0.0 or greater than 1.0, throw VEC-VECTORSCORE.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression vectorScore(ServerExpression score, double similarity, double similarityWeight);
/**
  * A helper function that returns a hybrid score using a cts score and a vector similarity calculation result. You can tune the effect of the vector similarity on the score using the similarityWeight option. The ideal value for similarityWeight depends on your application.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:vector-score" target="mlserverdoc">vec:vector-score</a> server function.
  * @param score  The cts:score of the matching document.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param similarity  The similarity between the vector in the matching document and the query vector. The result of a call to ovec:cosine-similarity(). In the case that the vectors are normalized, pass ovec:dot-product(). Note that vec:euclidean-distance() should not be used here.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param similarityWeight  The weight of the vector similarity on the score. The default value is 0.1. If 0.0 is passed in, vector similarity has no effect. If passed a value less than 0.0 or greater than 1.0, throw VEC-VECTORSCORE.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression vectorScore(ServerExpression score, ServerExpression similarity, ServerExpression similarityWeight);
}
