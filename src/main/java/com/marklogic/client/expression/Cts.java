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


// IMPORTANT: Do not edit. This file is generated. 
public interface Cts extends CtsQuery {
    public XsStringSeqExpr stem(String text);
    public XsStringSeqExpr stem(XsStringExpr text);
    public XsStringSeqExpr stem(String text, String language);
    public XsStringSeqExpr stem(XsStringExpr text, XsStringExpr language);
    public XsStringSeqExpr tokenize(String text);
    public XsStringSeqExpr tokenize(XsStringExpr text);
    public XsStringSeqExpr tokenize(String text, String language);
    public XsStringSeqExpr tokenize(XsStringExpr text, XsStringExpr language);
    public XsStringSeqExpr tokenize(String text, String language, String field);
    public XsStringSeqExpr tokenize(XsStringExpr text, XsStringExpr language, XsStringExpr field);
}
