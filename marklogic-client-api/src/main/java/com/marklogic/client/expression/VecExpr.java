/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
 *
 * @since 7.0.0; requires MarkLogic 12 or higher.
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
  * Returns the cosine of the angle between two vectors. The vectors must be of the same dimension.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:cosine" target="mlserverdoc">vec:cosine</a> server function.
  * @param vector1  The vector from which to calculate the cosine with vector2.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @param vector2  The vector from which to calculate the cosine with vector1.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  * @since 7.2.0
  */
  public ServerExpression cosine(ServerExpression vector1, ServerExpression vector2);

/**
  * Returns the cosine distance between two vectors. The vectors must be of the same dimension.
  *
  * @param vector1 The vector from which to calculate the cosine distance with vector2.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @param vector2 The vector from which to calculate the cosine distance with vector1.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @return a server expression with the <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a> server data type
  * @since 7.2.0
  */
  public ServerExpression cosineDistance(ServerExpression vector1, ServerExpression vector2);

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
  * Returns a new vector which is a copy of the input vector with reduced precision. The precision reduction is achieved by clearing the bottom (32 - precision) bits of the mantissa for each dimension's float value. This can be useful for reducing storage requirements or for creating approximate vector representations.
  *
  * <a name="ml-server-type-precision"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:precision" target="mlserverdoc">vec:precision</a> server function.
  * @param vector  The input vector to reduce precision. Can be a vector or an empty sequence.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a> server data type
  * @since 8.1.0; requires MarkLogic 12.1 or higher.
  */
  public ServerExpression precision(ServerExpression vector);
/**
  * Returns a new vector which is a copy of the input vector with reduced precision. The precision reduction is achieved by clearing the bottom (32 - precision) bits of the mantissa for each dimension's float value. This can be useful for reducing storage requirements or for creating approximate vector representations.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:precision" target="mlserverdoc">vec:precision</a> server function.
  * @param vector  The input vector to reduce precision. Can be a vector or an empty sequence.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @param precision  The number of mantissa bits to preserve (9-32 inclusive). Default is 16. Higher values preserve more precision. If the value is outside the valid range, throw VEC-INVALIDPRECISION.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a> server data type
  * @since 8.1.0; requires MarkLogic 12.1 or higher.
  */
  public ServerExpression precision(ServerExpression vector, ServerExpression precision);
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
  * Returns a new vector which is a copy of the input vector with each element truncated to a specific number of digits.
  *
  * <a name="ml-server-type-trunc"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:trunc" target="mlserverdoc">vec:trunc</a> server function.
  * @param vector  The input vector to truncate.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a> server data type
  * @since 8.1.0; requires MarkLogic 12.1 or higher.
  */
  public ServerExpression trunc(ServerExpression vector);
/**
  * Returns a new vector which is a copy of the input vector with each element truncated to a specific number of digits.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:trunc" target="mlserverdoc">vec:trunc</a> server function.
  * @param vector  The input vector to truncate.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @param n  The numbers of decimal places to truncate to. The default is 0. Negative values cause that many digits to the left of the decimal point to be truncated.  (of <a href="{@docRoot}/doc-files/types/xs_int.html">xs:int</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a> server data type
  * @since 8.1.0; requires MarkLogic 12.1 or higher.
  */
  public ServerExpression trunc(ServerExpression vector, int n);
/**
  * Returns a new vector which is a copy of the input vector with each element truncated to a specific number of digits.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:trunc" target="mlserverdoc">vec:trunc</a> server function.
  * @param vector  The input vector to truncate.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @param n  The numbers of decimal places to truncate to. The default is 0. Negative values cause that many digits to the left of the decimal point to be truncated.  (of <a href="{@docRoot}/doc-files/types/xs_int.html">xs:int</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a> server data type
  * @since 8.1.0; requires MarkLogic 12.1 or higher.
  */
  public ServerExpression trunc(ServerExpression vector, ServerExpression n);
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
  * A helper function that returns a hybrid score using a cts score and a vector distance calculation result. You can tune the effect of the vector distance on the score using the distanceWeight option. The ideal value for distanceWeight depends on your application. The hybrid score is calculated using the formula: score = weight * annScore + (1 - weight) * ctsScore. - annScore is derived from the distance and distanceWeight, where a larger distanceWeight reduces the annScore for the same distance. - weight determines the contribution of the annScore and ctsScore to the final score. A weight of 0.5 balances both equally. This formula allows you to combine traditional cts scoring with vector-based distance scoring, providing a flexible way to rank results.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:vector-score" target="mlserverdoc">vec:vector-score</a> server function.
  * @param score  The cts:score of the matching document.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param distance  The distance between the vector in the matching document and the query vector. Examples, the result of a call to ovec:cosine-distance() or ovec:euclidean-distance().  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
 *
 * @since 8.1.0, requires MarkLogic 12.1
  */
  public ServerExpression vectorScore(ServerExpression score, double distance);
/**
  * A helper function that returns a hybrid score using a cts score and a vector distance calculation result. You can tune the effect of the vector distance on the score using the distanceWeight option. The ideal value for distanceWeight depends on your application. The hybrid score is calculated using the formula: score = weight * annScore + (1 - weight) * ctsScore. - annScore is derived from the distance and distanceWeight, where a larger distanceWeight reduces the annScore for the same distance. - weight determines the contribution of the annScore and ctsScore to the final score. A weight of 0.5 balances both equally. This formula allows you to combine traditional cts scoring with vector-based distance scoring, providing a flexible way to rank results.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:vector-score" target="mlserverdoc">vec:vector-score</a> server function.
  * @param score  The cts:score of the matching document.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param distance  The distance between the vector in the matching document and the query vector. Examples, the result of a call to ovec:cosine-distance() or ovec:euclidean-distance().  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
 * @since 8.1.0, requires MarkLogic 12.1
  */
  public ServerExpression vectorScore(ServerExpression score, ServerExpression distance);
/**
  * A helper function that returns a hybrid score using a cts score and a vector distance calculation result. You can tune the effect of the vector distance on the score using the distanceWeight option. The ideal value for distanceWeight depends on your application. The hybrid score is calculated using the formula: score = weight * annScore + (1 - weight) * ctsScore. - annScore is derived from the distance and distanceWeight, where a larger distanceWeight reduces the annScore for the same distance. - weight determines the contribution of the annScore and ctsScore to the final score. A weight of 0.5 balances both equally. This formula allows you to combine traditional cts scoring with vector-based distance scoring, providing a flexible way to rank results.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:vector-score" target="mlserverdoc">vec:vector-score</a> server function.
  * @param score  The cts:score of the matching document.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param distance  The distance between the vector in the matching document and the query vector. Examples, the result of a call to ovec:cosine-distance() or ovec:euclidean-distance().  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param distanceWeight  The weight of the vector distance on the annScore. This value is a positive coefficient that scales the distance. A larger distanceWeight produces a lower annScore for the same distance. The default value is 1.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression vectorScore(ServerExpression score, double distance, double distanceWeight);
/**
  * A helper function that returns a hybrid score using a cts score and a vector distance calculation result. You can tune the effect of the vector distance on the score using the distanceWeight option. The ideal value for distanceWeight depends on your application. The hybrid score is calculated using the formula: score = weight * annScore + (1 - weight) * ctsScore. - annScore is derived from the distance and distanceWeight, where a larger distanceWeight reduces the annScore for the same distance. - weight determines the contribution of the annScore and ctsScore to the final score. A weight of 0.5 balances both equally. This formula allows you to combine traditional cts scoring with vector-based distance scoring, providing a flexible way to rank results.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:vector-score" target="mlserverdoc">vec:vector-score</a> server function.
  * @param score  The cts:score of the matching document.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param distance  The distance between the vector in the matching document and the query vector. Examples, the result of a call to ovec:cosine-distance() or ovec:euclidean-distance().  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param distanceWeight  The weight of the vector distance on the annScore. This value is a positive coefficient that scales the distance. A larger distanceWeight produces a lower annScore for the same distance. The default value is 1.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression vectorScore(ServerExpression score, ServerExpression distance, ServerExpression distanceWeight);
/**
  * A helper function that returns a hybrid score using a cts score and a vector distance calculation result. You can tune the effect of the vector distance on the score using the distanceWeight option. The ideal value for distanceWeight depends on your application. The hybrid score is calculated using the formula: score = weight * annScore + (1 - weight) * ctsScore. - annScore is derived from the distance and distanceWeight, where a larger distanceWeight reduces the annScore for the same distance. - weight determines the contribution of the annScore and ctsScore to the final score. A weight of 0.5 balances both equally. This formula allows you to combine traditional cts scoring with vector-based distance scoring, providing a flexible way to rank results.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:vector-score" target="mlserverdoc">vec:vector-score</a> server function.
  * @param score  The cts:score of the matching document.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param distance  The distance between the vector in the matching document and the query vector. Examples, the result of a call to ovec:cosine-distance() or ovec:euclidean-distance().  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param distanceWeight  The weight of the vector distance on the annScore. This value is a positive coefficient that scales the distance. A larger distanceWeight produces a lower annScore for the same distance. The default value is 1.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param weight  The weight of the annScore in the final hybrid score. This value is a coefficient between 0 and 1, where 0 gives full weight to the cts score and 1 gives full weight to the annScore. The default value is 0.5.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression vectorScore(ServerExpression score, double distance, double distanceWeight, double weight);
/**
  * A helper function that returns a hybrid score using a cts score and a vector distance calculation result. You can tune the effect of the vector distance on the score using the distanceWeight option. The ideal value for distanceWeight depends on your application. The hybrid score is calculated using the formula: score = weight * annScore + (1 - weight) * ctsScore. - annScore is derived from the distance and distanceWeight, where a larger distanceWeight reduces the annScore for the same distance. - weight determines the contribution of the annScore and ctsScore to the final score. A weight of 0.5 balances both equally. This formula allows you to combine traditional cts scoring with vector-based distance scoring, providing a flexible way to rank results.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:vector-score" target="mlserverdoc">vec:vector-score</a> server function.
  * @param score  The cts:score of the matching document.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param distance  The distance between the vector in the matching document and the query vector. Examples, the result of a call to ovec:cosine-distance() or ovec:euclidean-distance().  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param distanceWeight  The weight of the vector distance on the annScore. This value is a positive coefficient that scales the distance. A larger distanceWeight produces a lower annScore for the same distance. The default value is 1.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param weight  The weight of the annScore in the final hybrid score. This value is a coefficient between 0 and 1, where 0 gives full weight to the cts score and 1 gives full weight to the annScore. The default value is 0.5.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression vectorScore(ServerExpression score, ServerExpression distance, ServerExpression distanceWeight, ServerExpression weight);
}
