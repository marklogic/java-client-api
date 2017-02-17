/*
 * Copyright 2016-2017 MarkLogic Corporation
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

import com.marklogic.client.type.JsonArrayExpr;
import com.marklogic.client.type.JsonArraySeqExpr;
import com.marklogic.client.type.JsonObjectExpr;
import com.marklogic.client.type.JsonObjectSeqExpr;

// IMPORTANT: Do not edit. This file is generated. 
public interface JsonExpr {
    /**
    * Creates a (JSON) array, which is like a sequence of values, but allows for nesting.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:array" target="mlserverdoc">json:array</a>
    * @return  a JsonArrayExpr expression
    */
    public JsonArrayExpr array();
    /**
    * Creates a (JSON) array, which is like a sequence of values, but allows for nesting.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:array" target="mlserverdoc">json:array</a>
    * @param array  A serialized array element.
    * @return  a JsonArrayExpr expression
    */
    public JsonArrayExpr array(ElementNodeExpr array);
    /**
    * Returns the size of the array.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:array-size" target="mlserverdoc">json:array-size</a>
    * @param array  An array.
    * @return  a XsUnsignedLongExpr expression
    */
    public XsUnsignedLongExpr arraySize(JsonArrayExpr array);
    /**
    * Returns the array values as an XQuery sequence.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:array-values" target="mlserverdoc">json:array-values</a>
    * @param array  An array.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr arrayValues(JsonArrayExpr array);
    /**
    * Returns the array values as an XQuery sequence.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:array-values" target="mlserverdoc">json:array-values</a>
    * @param array  An array.
    * @param flatten  Include values from subarrays in the sequence. The default is false, meaning that subarrays are returned as array values.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr arrayValues(JsonArrayExpr array, boolean flatten);
    /**
    * Returns the array values as an XQuery sequence.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:array-values" target="mlserverdoc">json:array-values</a>
    * @param array  An array.
    * @param flatten  Include values from subarrays in the sequence. The default is false, meaning that subarrays are returned as array values.
    * @return  a ItemSeqExpr expression sequence
    */
    public ItemSeqExpr arrayValues(JsonArrayExpr array, XsBooleanExpr flatten);
    /**
    * Creates a JSON object, which is a kind of map with a fixed and ordered set of keys.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:object" target="mlserverdoc">json:object</a>
    * @return  a JsonObjectExpr expression
    */
    public JsonObjectExpr object();
    /**
    * Creates a JSON object, which is a kind of map with a fixed and ordered set of keys.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:object" target="mlserverdoc">json:object</a>
    * @param map  A serialized JSON object.
    * @return  a JsonObjectExpr expression
    */
    public JsonObjectExpr object(ElementNodeExpr map);
    /**
    * Creates a JSON object.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:object-define" target="mlserverdoc">json:object-define</a>
    * @return  a JsonObjectExpr expression
    */
    public JsonObjectExpr objectDefine();
    /**
    * Creates a JSON object.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:object-define" target="mlserverdoc">json:object-define</a>
    * @param keys  The sequence of keys in this object.
    * @return  a JsonObjectExpr expression
    */
    public JsonObjectExpr objectDefine(XsStringSeqExpr keys);
    /**
    * Extract a subarray from an array, producing a new array. The second and third arguments to this function operate similarly to those of fn:subsequence for XQuery sequences.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:subarray" target="mlserverdoc">json:subarray</a>
    * @param array  An array.
    * @param startingLoc  The starting position of the start of the subarray.
    * @return  a JsonArrayExpr expression
    */
    public JsonArrayExpr subarray(JsonArrayExpr array, double startingLoc);
    /**
    * Extract a subarray from an array, producing a new array. The second and third arguments to this function operate similarly to those of fn:subsequence for XQuery sequences.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:subarray" target="mlserverdoc">json:subarray</a>
    * @param array  An array.
    * @param startingLoc  The starting position of the start of the subarray.
    * @return  a JsonArrayExpr expression
    */
    public JsonArrayExpr subarray(JsonArrayExpr array, XsNumericExpr startingLoc);
    /**
    * Extract a subarray from an array, producing a new array. The second and third arguments to this function operate similarly to those of fn:subsequence for XQuery sequences.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:subarray" target="mlserverdoc">json:subarray</a>
    * @param array  An array.
    * @param startingLoc  The starting position of the start of the subarray.
    * @param length  The length of the subarray.
    * @return  a JsonArrayExpr expression
    */
    public JsonArrayExpr subarray(JsonArrayExpr array, double startingLoc, double length);
    /**
    * Extract a subarray from an array, producing a new array. The second and third arguments to this function operate similarly to those of fn:subsequence for XQuery sequences.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:subarray" target="mlserverdoc">json:subarray</a>
    * @param array  An array.
    * @param startingLoc  The starting position of the start of the subarray.
    * @param length  The length of the subarray.
    * @return  a JsonArrayExpr expression
    */
    public JsonArrayExpr subarray(JsonArrayExpr array, XsNumericExpr startingLoc, XsNumericExpr length);
    /**
    * Constructs an array from a sequence of items.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a>
    * @return  a JsonArrayExpr expression
    */
    public JsonArrayExpr toArray();
    /**
    * Constructs an array from a sequence of items.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a>
    * @param items  A sequence of items.
    * @return  a JsonArrayExpr expression
    */
    public JsonArrayExpr toArray(ItemSeqExpr items);
    /**
    * Constructs an array from a sequence of items.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a>
    * @param items  A sequence of items.
    * @param limit  The size of the array to construct. If the size is less than the length of the item sequence, only as "limit" items are put into the array. If the size is more than the length of the sequence, the array is filled with null values up to the limit.
    * @return  a JsonArrayExpr expression
    */
    public JsonArrayExpr toArray(ItemSeqExpr items, double limit);
    /**
    * Constructs an array from a sequence of items.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a>
    * @param items  A sequence of items.
    * @param limit  The size of the array to construct. If the size is less than the length of the item sequence, only as "limit" items are put into the array. If the size is more than the length of the sequence, the array is filled with null values up to the limit.
    * @return  a JsonArrayExpr expression
    */
    public JsonArrayExpr toArray(ItemSeqExpr items, XsNumericExpr limit);
    /**
    * Constructs an array from a sequence of items.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a>
    * @param items  A sequence of items.
    * @param limit  The size of the array to construct. If the size is less than the length of the item sequence, only as "limit" items are put into the array. If the size is more than the length of the sequence, the array is filled with null values up to the limit.
    * @param zero  The value to use to pad out the array, if necessary. By default the empty sequence is used.
    * @return  a JsonArrayExpr expression
    */
    public JsonArrayExpr toArray(ItemSeqExpr items, double limit, ItemExpr zero);
    /**
    * Constructs an array from a sequence of items.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/json:to-array" target="mlserverdoc">json:to-array</a>
    * @param items  A sequence of items.
    * @param limit  The size of the array to construct. If the size is less than the length of the item sequence, only as "limit" items are put into the array. If the size is more than the length of the sequence, the array is filled with null values up to the limit.
    * @param zero  The value to use to pad out the array, if necessary. By default the empty sequence is used.
    * @return  a JsonArrayExpr expression
    */
    public JsonArrayExpr toArray(ItemSeqExpr items, XsNumericExpr limit, ItemExpr zero);
    public JsonArraySeqExpr arraySeq(JsonArrayExpr... items);
 
    public JsonObjectSeqExpr objectSeq(JsonObjectExpr... items);

}
