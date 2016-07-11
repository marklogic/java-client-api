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

// TODO: single import
import com.marklogic.client.expression.BaseType;

import com.marklogic.client.expression.Xs;
 import com.marklogic.client.expression.BaseType;


// IMPORTANT: Do not edit. This file is generated. 
public interface Json {
    public Json.ArrayExpr array();
    public Json.ArrayExpr array(BaseType.ElementExpr array);
    public Xs.UnsignedLongExpr arraySize(Json.ArrayExpr array);
    public BaseType.ItemSeqExpr arrayValues(Json.ArrayExpr array);
    public BaseType.ItemSeqExpr arrayValues(Json.ArrayExpr array, boolean flatten);
    public BaseType.ItemSeqExpr arrayValues(Json.ArrayExpr array, Xs.BooleanExpr flatten);
    public Json.ArrayExpr arrayWith(Json.ArrayExpr arg1, BaseType.ItemSeqExpr arg2);
    public Json.ArrayExpr subarray(Json.ArrayExpr array, Xs.NumericExpr startingLoc);
    public Json.ArrayExpr subarray(Json.ArrayExpr array, Xs.NumericExpr startingLoc, Xs.NumericExpr length);
    public Json.ArrayExpr toArray();
    public Json.ArrayExpr toArray(BaseType.ItemSeqExpr items);
    public Json.ArrayExpr toArray(BaseType.ItemSeqExpr items, Xs.NumericExpr limit);
    public Json.ArrayExpr toArray(BaseType.ItemSeqExpr items, Xs.NumericExpr limit, BaseType.ItemExpr zero);     public Json.ArraySeqExpr array(Json.ArrayExpr... items);
        public interface ArraySeqExpr extends BaseType.ItemSeqExpr { }
        public interface ArrayExpr extends ArraySeqExpr, BaseType.ItemExpr { }

}
