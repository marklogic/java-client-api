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

package com.marklogic.client.expression;

import com.marklogic.client.type.XsAnyAtomicTypeSeqVal;
import com.marklogic.client.type.XsDoubleVal;
import com.marklogic.client.type.XsFloatVal;
import com.marklogic.client.type.XsStringVal;
import com.marklogic.client.type.XsUnsignedIntVal;

import com.marklogic.client.type.ServerExpression;

// IMPORTANT: Do not edit. This file is generated. 

/**
 * Builds expressions to call functions in the vec server library for a row
 * pipeline.
 */
public interface VecExpr {
    public ServerExpression add(ServerExpression vector1, ServerExpression vector2);
/**
  * Constructs a vector result by decoding the base64 binary input.
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
  * @param k  The zero-based index of vector1 to return.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
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
public ServerExpression subtract(ServerExpression vector1, ServerExpression vector2);
/**
  * Returns a subvector of the input vector, starting at the specified index and with the specified length.
  *
  * <a name="ml-server-type-subvector"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:subvector" target="mlserverdoc">vec:subvector</a> server function.
  * @param vector  The input vector.  (of <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a>)
  * @param start  The starting index of the subvector (inclusive).  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param length  The length of the subvector.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a> server data type
  */
  public ServerExpression subvector(ServerExpression vector, ServerExpression start, ServerExpression length);
/**
  * Returns a vector value.
  *
  * <a name="ml-server-type-vector"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:vector" target="mlserverdoc">vec:vector</a> server function.
  * @param values  The values to create the vector from. Can be a sequence or json:array of integer or floating-point numbers.  (of <a href="{@docRoot}/doc-files/types/xs_anyAtomicType.html">xs:anyAtomicType</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/vec_vector.html">vec:vector</a> server data type
  */
  public ServerExpression vector(ServerExpression values);
/**
  * A helper function that returns the hybrid score using a cts:score and a vector similarity function. You can tune the effect of the vector similarity on the score using the "similarity-weight" option.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:vector-score" target="mlserverdoc">vec:vector-score</a> server function.
  * @param score  The cts:score of the matching document.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param similarity  The similarity between the vector in the matching document and the query vector. The result of a call to ovec:cosine-similarity() or op.vec.cosineSimilarity(). In the case that the vectors are normalized, pass ovec:dot-product() or op.vec.dotProduct(). Note that vec:euclideanDistance() should not be used here.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a> server data type
  */
  public ServerExpression vectorScore(ServerExpression score, double similarity);
/**
  * A helper function that returns the hybrid score using a cts:score and a vector similarity function. You can tune the effect of the vector similarity on the score using the "similarity-weight" option.
  *
  * <a name="ml-server-type-vector-score"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:vector-score" target="mlserverdoc">vec:vector-score</a> server function.
  * @param score  The cts:score of the matching document.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param similarity  The similarity between the vector in the matching document and the query vector. The result of a call to ovec:cosine-similarity() or op.vec.cosineSimilarity(). In the case that the vectors are normalized, pass ovec:dot-product() or op.vec.dotProduct(). Note that vec:euclideanDistance() should not be used here.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a> server data type
  */
  public ServerExpression vectorScore(ServerExpression score, ServerExpression similarity);
/**
  * A helper function that returns the hybrid score using a cts:score and a vector similarity function. You can tune the effect of the vector similarity on the score using the "similarity-weight" option.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:vector-score" target="mlserverdoc">vec:vector-score</a> server function.
  * @param score  The cts:score of the matching document.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param similarity  The similarity between the vector in the matching document and the query vector. The result of a call to ovec:cosine-similarity() or op.vec.cosineSimilarity(). In the case that the vectors are normalized, pass ovec:dot-product() or op.vec.dotProduct(). Note that vec:euclideanDistance() should not be used here.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param similarityWeight  The weight of the vector similarity on the score. The default value is 1.0. Values less than or equal to zero have no effect.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a> server data type
  */
  public ServerExpression vectorScore(ServerExpression score, double similarity, double similarityWeight);
/**
  * A helper function that returns the hybrid score using a cts:score and a vector similarity function. You can tune the effect of the vector similarity on the score using the "similarity-weight" option.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/vec:vector-score" target="mlserverdoc">vec:vector-score</a> server function.
  * @param score  The cts:score of the matching document.  (of <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a>)
  * @param similarity  The similarity between the vector in the matching document and the query vector. The result of a call to ovec:cosine-similarity() or op.vec.cosineSimilarity(). In the case that the vectors are normalized, pass ovec:dot-product() or op.vec.dotProduct(). Note that vec:euclideanDistance() should not be used here.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @param similarityWeight  The weight of the vector similarity on the score. The default value is 1.0. Values less than or equal to zero have no effect.  (of <a href="{@docRoot}/doc-files/types/xs_double.html">xs:double</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedInt.html">xs:unsignedInt</a> server data type
  */
  public ServerExpression vectorScore(ServerExpression score, ServerExpression similarity, ServerExpression similarityWeight);
}
