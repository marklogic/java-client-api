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

import com.marklogic.client.type.XsStringExpr;
 import com.marklogic.client.type.XsStringSeqExpr;
 import com.marklogic.client.type.XsIntegerExpr;


// IMPORTANT: Do not edit. This file is generated. 
public interface SpellExpr {
    public XsStringSeqExpr doubleMetaphone(String word);
    public XsStringSeqExpr doubleMetaphone(XsStringExpr word);
    public XsIntegerExpr levenshteinDistance(String str1, String str2);
    public XsIntegerExpr levenshteinDistance(XsStringExpr str1, XsStringExpr str2);
    public XsStringExpr romanize(String string);
    public XsStringExpr romanize(XsStringExpr string);
}
