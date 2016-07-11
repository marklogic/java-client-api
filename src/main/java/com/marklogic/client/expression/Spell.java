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


// IMPORTANT: Do not edit. This file is generated. 
public interface Spell {
    public Xs.StringSeqExpr doubleMetaphone(String word);
    public Xs.StringSeqExpr doubleMetaphone(Xs.StringExpr word);
    public Xs.IntegerExpr levenshteinDistance(String str1, String str2);
    public Xs.IntegerExpr levenshteinDistance(Xs.StringExpr str1, Xs.StringExpr str2);
    public Xs.StringExpr romanize(String string);
    public Xs.StringExpr romanize(Xs.StringExpr string);
}
