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
    public JsonArrayExpr array();
    public JsonArrayExpr array(ElementNodeExpr array);
    public XsUnsignedLongExpr arraySize(JsonArrayExpr array);
    public ItemSeqExpr arrayValues(JsonArrayExpr array);
    public ItemSeqExpr arrayValues(JsonArrayExpr array, boolean flatten);
    public ItemSeqExpr arrayValues(JsonArrayExpr array, XsBooleanExpr flatten);
    public JsonObjectExpr object();
    public JsonObjectExpr object(ElementNodeExpr map);
    public JsonObjectExpr objectDefine();
    public JsonObjectExpr objectDefine(XsStringSeqExpr keys);
    public JsonArrayExpr subarray(JsonArrayExpr array, double startingLoc);
    public JsonArrayExpr subarray(JsonArrayExpr array, XsNumericExpr startingLoc);
    public JsonArrayExpr subarray(JsonArrayExpr array, double startingLoc, double length);
    public JsonArrayExpr subarray(JsonArrayExpr array, XsNumericExpr startingLoc, XsNumericExpr length);
    public JsonArrayExpr toArray();
    public JsonArrayExpr toArray(ItemSeqExpr items);
    public JsonArrayExpr toArray(ItemSeqExpr items, double limit);
    public JsonArrayExpr toArray(ItemSeqExpr items, XsNumericExpr limit);
    public JsonArrayExpr toArray(ItemSeqExpr items, double limit, ItemExpr zero);
    public JsonArrayExpr toArray(ItemSeqExpr items, XsNumericExpr limit, ItemExpr zero);
    public JsonArraySeqExpr arraySeq(JsonArrayExpr... items);
 
    public JsonObjectSeqExpr objectSeq(JsonObjectExpr... items);

}
