/*
 * Copyright 2016-2019 MarkLogic Corporation
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

import com.marklogic.client.type.ElementNodeExpr;
import com.marklogic.client.type.ItemExpr;
import com.marklogic.client.type.ItemSeqExpr;
import com.marklogic.client.type.XsBooleanExpr;
import com.marklogic.client.type.XsNumericExpr;
import com.marklogic.client.type.XsStringSeqExpr;
import com.marklogic.client.type.XsUnsignedLongExpr;

import com.marklogic.client.type.ServerExpression;
import com.marklogic.client.type.JsonArrayExpr;
import com.marklogic.client.type.JsonArraySeqExpr;
import com.marklogic.client.type.JsonObjectExpr;
import com.marklogic.client.type.JsonObjectSeqExpr;

// IMPORTANT: Do not edit. This file is generated. 

/**
 * Builds expressions to call functions in the json server library for a row
 * pipeline.
 */
public interface JsonExpr {
    /**
  * Creates a (JSON) array, which is like a sequence of values, but allows for nesting.
  *
  * <a name="ml-server-type-array"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:array" target="mlserverdoc">json:array</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public JsonArrayExpr array();
/**
  * Creates a (JSON) array, which is like a sequence of values, but allows for nesting.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:array" target="mlserverdoc">json:array</a> server function.
  * @param array  A serialized array element.  (of <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public JsonArrayExpr array(ServerExpression array);
/**
  * Returns the size of the array.
  *
  * <a name="ml-server-type-array-size"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:array-size" target="mlserverdoc">json:array-size</a> server function.
  * @param array  An array.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public XsUnsignedLongExpr arraySize(ServerExpression array);
/**
  * Returns the array values as an XQuery sequence.
  *
  * <a name="ml-server-type-array-values"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:array-values" target="mlserverdoc">json:array-values</a> server function.
  * @param array  An array.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ItemSeqExpr arrayValues(ServerExpression array);
/**
  * Returns the array values as an XQuery sequence.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:array-values" target="mlserverdoc">json:array-values</a> server function.
  * @param array  An array.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @param flatten  Include values from subarrays in the sequence. The default is false, meaning that subarrays are returned as array values.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ItemSeqExpr arrayValues(ServerExpression array, boolean flatten);
/**
  * Returns the array values as an XQuery sequence.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:array-values" target="mlserverdoc">json:array-values</a> server function.
  * @param array  An array.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @param flatten  Include values from subarrays in the sequence. The default is false, meaning that subarrays are returned as array values.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ItemSeqExpr arrayValues(ServerExpression array, ServerExpression flatten);
/**
  * Creates a JSON object, which is a kind of map with a fixed and ordered set of keys.
  *
  * <a name="ml-server-type-object"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:object" target="mlserverdoc">json:object</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_object.html">json:object</a> server data type
  */
  public JsonObjectExpr object();
/**
  * Creates a JSON object, which is a kind of map with a fixed and ordered set of keys.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:object" target="mlserverdoc">json:object</a> server function.
  * @param map  A serialized JSON object.  (of <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_object.html">json:object</a> server data type
  */
  public JsonObjectExpr object(ServerExpression map);
/**
  * Creates a JSON object.
  *
  * <a name="ml-server-type-object-define"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:object-define" target="mlserverdoc">json:object-define</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_object.html">json:object</a> server data type
  */
  public JsonObjectExpr objectDefine();
/**
  * Creates a JSON object.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:object-define" target="mlserverdoc">json:object-define</a> server function.
  * @param keys  The sequence of keys in this object.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_object.html">json:object</a> server data type
  */
  public JsonObjectExpr objectDefine(ServerExpression keys);
/**
  * Extract a subarray from an array, producing a new array. The second and third arguments to this function operate similarly to those of fn:subsequence for XQuery sequences.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:subarray" target="mlserverdoc">json:subarray</a> server function.
  * @param array  An array.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @param startingLoc  The starting position of the start of the subarray.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public JsonArrayExpr subarray(ServerExpression array, double startingLoc);
/**
  * Extract a subarray from an array, producing a new array. The second and third arguments to this function operate similarly to those of fn:subsequence for XQuery sequences.
  *
  * <a name="ml-server-type-subarray"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:subarray" target="mlserverdoc">json:subarray</a> server function.
  * @param array  An array.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @param startingLoc  The starting position of the start of the subarray.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public JsonArrayExpr subarray(ServerExpression array, ServerExpression startingLoc);
/**
  * Extract a subarray from an array, producing a new array. The second and third arguments to this function operate similarly to those of fn:subsequence for XQuery sequences.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:subarray" target="mlserverdoc">json:subarray</a> server function.
  * @param array  An array.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @param startingLoc  The starting position of the start of the subarray.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param length  The length of the subarray.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public JsonArrayExpr subarray(ServerExpression array, double startingLoc, double length);
/**
  * Extract a subarray from an array, producing a new array. The second and third arguments to this function operate similarly to those of fn:subsequence for XQuery sequences.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:subarray" target="mlserverdoc">json:subarray</a> server function.
  * @param array  An array.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @param startingLoc  The starting position of the start of the subarray.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param length  The length of the subarray.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public JsonArrayExpr subarray(ServerExpression array, ServerExpression startingLoc, ServerExpression length);
/**
  * Constructs json:array from a sequence of items.
  *
  * <a name="ml-server-type-to-array"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public JsonArrayExpr toArray();
/**
  * Constructs json:array from a sequence of items.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a> server function.
  * @param items  A sequence of items.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public JsonArrayExpr toArray(ServerExpression items);
/**
  * Constructs json:array from a sequence of items.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a> server function.
  * @param items  A sequence of items.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param limit  The size of the array to construct. If the size is less than the length of the item sequence, only as "limit" items are put into the array. If the size is more than the length of the sequence, the array is filled with null values up to the limit.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public JsonArrayExpr toArray(ServerExpression items, double limit);
/**
  * Constructs json:array from a sequence of items.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a> server function.
  * @param items  A sequence of items.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param limit  The size of the array to construct. If the size is less than the length of the item sequence, only as "limit" items are put into the array. If the size is more than the length of the sequence, the array is filled with null values up to the limit.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public JsonArrayExpr toArray(ServerExpression items, ServerExpression limit);
/**
  * Constructs json:array from a sequence of items.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a> server function.
  * @param items  A sequence of items.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param limit  The size of the array to construct. If the size is less than the length of the item sequence, only as "limit" items are put into the array. If the size is more than the length of the sequence, the array is filled with null values up to the limit.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param zero  The value to use to pad out the array, if necessary. By default the empty sequence is used.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public JsonArrayExpr toArray(ServerExpression items, double limit, ServerExpression zero);
/**
  * Constructs json:array from a sequence of items.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a> server function.
  * @param items  A sequence of items.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param limit  The size of the array to construct. If the size is less than the length of the item sequence, only as "limit" items are put into the array. If the size is more than the length of the sequence, the array is filled with null values up to the limit.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param zero  The value to use to pad out the array, if necessary. By default the empty sequence is used.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public JsonArrayExpr toArray(ServerExpression items, ServerExpression limit, ServerExpression zero);
/**
  * Constructs a sequence of JsonArrayExpr items.
  * @param items  the JsonArrayExpr items collected by the sequence
  * @return  a JsonArraySeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public JsonArraySeqExpr arraySeq(JsonArrayExpr... items);
 
/**
  * Constructs a sequence of JsonObjectExpr items.
  * @param items  the JsonObjectExpr items collected by the sequence
  * @return  a JsonObjectSeqExpr sequence
  * @deprecated (as of 4.2) construct a {@link com.marklogic.client.type.ServerExpression} sequence with <a href="PlanBuilderBase.html#ml-server-expression-sequence">PlanBuilder.seq()</a>
  */
  public JsonObjectSeqExpr objectSeq(JsonObjectExpr... items);

}
