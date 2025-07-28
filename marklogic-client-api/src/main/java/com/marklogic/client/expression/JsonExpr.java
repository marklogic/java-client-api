/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.expression;

import com.marklogic.client.type.XsBooleanVal;
import com.marklogic.client.type.XsStringSeqVal;
import com.marklogic.client.type.XsUnsignedLongVal;

import com.marklogic.client.type.ServerExpression;

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
  public ServerExpression array();
/**
  * Creates a (JSON) array, which is like a sequence of values, but allows for nesting.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:array" target="mlserverdoc">json:array</a> server function.
  * @param array  A serialized array element.  (of <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public ServerExpression array(ServerExpression array);
/**
  * Returns the size of the array.
  *
  * <a name="ml-server-type-array-size"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:array-size" target="mlserverdoc">json:array-size</a> server function.
  * @param array  An array.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_unsignedLong.html">xs:unsignedLong</a> server data type
  */
  public ServerExpression arraySize(ServerExpression array);
/**
  * Returns the array values as an XQuery sequence.
  *
  * <a name="ml-server-type-array-values"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:array-values" target="mlserverdoc">json:array-values</a> server function.
  * @param array  An array.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression arrayValues(ServerExpression array);
/**
  * Returns the array values as an XQuery sequence.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:array-values" target="mlserverdoc">json:array-values</a> server function.
  * @param array  An array.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @param flatten  Include values from subarrays in the sequence. The default is false, meaning that subarrays are returned as array values.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression arrayValues(ServerExpression array, boolean flatten);
/**
  * Returns the array values as an XQuery sequence.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:array-values" target="mlserverdoc">json:array-values</a> server function.
  * @param array  An array.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @param flatten  Include values from subarrays in the sequence. The default is false, meaning that subarrays are returned as array values.  (of <a href="{@docRoot}/doc-files/types/xs_boolean.html">xs:boolean</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/item.html">item</a> server data type
  */
  public ServerExpression arrayValues(ServerExpression array, ServerExpression flatten);
/**
  * Creates a JSON object, which is a kind of map with a fixed and ordered set of keys.
  *
  * <a name="ml-server-type-object"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:object" target="mlserverdoc">json:object</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_object.html">json:object</a> server data type
  */
  public ServerExpression object();
/**
  * Creates a JSON object, which is a kind of map with a fixed and ordered set of keys.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:object" target="mlserverdoc">json:object</a> server function.
  * @param map  A serialized JSON object.  (of <a href="{@docRoot}/doc-files/types/element-node.html">element-node</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_object.html">json:object</a> server data type
  */
  public ServerExpression object(ServerExpression map);
/**
  * Creates a JSON object.
  *
  * <a name="ml-server-type-object-define"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:object-define" target="mlserverdoc">json:object-define</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_object.html">json:object</a> server data type
  */
  public ServerExpression objectDefine();
/**
  * Creates a JSON object.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:object-define" target="mlserverdoc">json:object-define</a> server function.
  * @param keys  The sequence of keys in this object.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_object.html">json:object</a> server data type
  */
  public ServerExpression objectDefine(ServerExpression keys);
/**
  * Extract a subarray from an array, producing a new array. The second and third arguments to this function operate similarly to those of fn:subsequence for XQuery sequences.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:subarray" target="mlserverdoc">json:subarray</a> server function.
  * @param array  An array.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @param startingLoc  The starting position of the start of the subarray.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public ServerExpression subarray(ServerExpression array, double startingLoc);
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
  public ServerExpression subarray(ServerExpression array, ServerExpression startingLoc);
/**
  * Extract a subarray from an array, producing a new array. The second and third arguments to this function operate similarly to those of fn:subsequence for XQuery sequences.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:subarray" target="mlserverdoc">json:subarray</a> server function.
  * @param array  An array.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @param startingLoc  The starting position of the start of the subarray.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param length  The length of the subarray.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public ServerExpression subarray(ServerExpression array, double startingLoc, double length);
/**
  * Extract a subarray from an array, producing a new array. The second and third arguments to this function operate similarly to those of fn:subsequence for XQuery sequences.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:subarray" target="mlserverdoc">json:subarray</a> server function.
  * @param array  An array.  (of <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a>)
  * @param startingLoc  The starting position of the start of the subarray.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param length  The length of the subarray.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public ServerExpression subarray(ServerExpression array, ServerExpression startingLoc, ServerExpression length);
/**
  * Constructs a json:array from a sequence of items.
  *
  * <a name="ml-server-type-to-array"></a>

  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a> server function.
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public ServerExpression toArray();
/**
  * Constructs a json:array from a sequence of items.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a> server function.
  * @param items  The items to be used as elements in the constructed array.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public ServerExpression toArray(ServerExpression items);
/**
  * Constructs a json:array from a sequence of items.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a> server function.
  * @param items  The items to be used as elements in the constructed array.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param limit  The size of the array to construct. If the size is less than the length of the item sequence, only as "limit" items are put into the array. If the size is more than the length of the sequence, the array is filled with null values up to the limit.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public ServerExpression toArray(ServerExpression items, double limit);
/**
  * Constructs a json:array from a sequence of items.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a> server function.
  * @param items  The items to be used as elements in the constructed array.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param limit  The size of the array to construct. If the size is less than the length of the item sequence, only as "limit" items are put into the array. If the size is more than the length of the sequence, the array is filled with null values up to the limit.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public ServerExpression toArray(ServerExpression items, ServerExpression limit);
/**
  * Constructs a json:array from a sequence of items.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a> server function.
  * @param items  The items to be used as elements in the constructed array.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param limit  The size of the array to construct. If the size is less than the length of the item sequence, only as "limit" items are put into the array. If the size is more than the length of the sequence, the array is filled with null values up to the limit.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param zero  The value to use to pad out the array, if necessary. By default the empty sequence is used.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public ServerExpression toArray(ServerExpression items, double limit, ServerExpression zero);
/**
  * Constructs a json:array from a sequence of items.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a> server function.
  * @param items  The items to be used as elements in the constructed array.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @param limit  The size of the array to construct. If the size is less than the length of the item sequence, only as "limit" items are put into the array. If the size is more than the length of the sequence, the array is filled with null values up to the limit.  (of <a href="{@docRoot}/doc-files/types/xs_numeric.html">xs:numeric</a>)
  * @param zero  The value to use to pad out the array, if necessary. By default the empty sequence is used.  (of <a href="{@docRoot}/doc-files/types/item.html">item</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/json_array.html">json:array</a> server data type
  */
  public ServerExpression toArray(ServerExpression items, ServerExpression limit, ServerExpression zero);
}
