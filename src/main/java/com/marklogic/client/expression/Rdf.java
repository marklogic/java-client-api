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
 import com.marklogic.client.expression.Sql;


// IMPORTANT: Do not edit. This file is generated. 
public interface Rdf {
    public Rdf.LangStringExpr langString(String string, String lang);
    public Rdf.LangStringExpr langString(Xs.StringExpr string, Xs.StringExpr lang);
    public Sql.CollatedStringExpr langStringLanguage(Rdf.LangStringExpr val);     public Rdf.LangStringSeqExpr langString(Rdf.LangStringExpr... items);
        public interface LangStringSeqExpr extends BaseType.ItemSeqExpr { }
        public interface LangStringExpr extends LangStringSeqExpr, BaseType.ItemExpr { }

}
