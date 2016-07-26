/*
 * Copyright 2016 MarkLogic Corporation
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

import com.marklogic.client.type.JsonArrayExpr;
 import com.marklogic.client.type.XsUnsignedLongExpr;
 import com.marklogic.client.type.NodeElementExpr;
 import com.marklogic.client.type.XsNumericExpr;
 import com.marklogic.client.type.JsonArraySeqExpr;
 import com.marklogic.client.type.XsBooleanExpr;
 import com.marklogic.client.type.ItemExpr;
 import com.marklogic.client.type.ItemSeqExpr;


// IMPORTANT: Do not edit. This file is generated. 
public interface Json {
    public JsonArrayExpr array();
    public JsonArrayExpr array(NodeElementExpr array);
    public XsUnsignedLongExpr arraySize(JsonArrayExpr array);
    public ItemSeqExpr arrayValues(JsonArrayExpr array);
    public ItemSeqExpr arrayValues(JsonArrayExpr array, boolean flatten);
    public ItemSeqExpr arrayValues(JsonArrayExpr array, XsBooleanExpr flatten);
    public JsonArrayExpr arrayWith(JsonArrayExpr arg1, ItemSeqExpr arg2);
    public JsonArrayExpr subarray(JsonArrayExpr array, XsNumericExpr startingLoc);
    public JsonArrayExpr subarray(JsonArrayExpr array, XsNumericExpr startingLoc, XsNumericExpr length);
    public JsonArrayExpr toArray();
    public JsonArrayExpr toArray(ItemSeqExpr items);
    public JsonArrayExpr toArray(ItemSeqExpr items, XsNumericExpr limit);
    public JsonArrayExpr toArray(ItemSeqExpr items, XsNumericExpr limit, ItemExpr zero);     public JsonArraySeqExpr array(JsonArrayExpr... items);

}
