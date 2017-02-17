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

import com.marklogic.client.type.XsStringExpr;

import com.marklogic.client.type.RdfLangStringExpr;
import com.marklogic.client.type.RdfLangStringSeqExpr;

// IMPORTANT: Do not edit. This file is generated. 
public interface RdfExpr extends RdfValue {
    /**
    * Returns an <code>rdf:langString</code> value with the given value and language tag. The <code>rdf:langString</code> type extends <code>xs:string</code>, and represents a language tagged string in RDF. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/rdf:langString" target="mlserverdoc">rdf:langString</a>
    * @param string  The lexical value.
    * @param lang  The language.
    * @return  a RdfLangStringExpr expression
    */
    public RdfLangStringExpr langString(XsStringExpr string, String lang);
    /**
    * Returns an <code>rdf:langString</code> value with the given value and language tag. The <code>rdf:langString</code> type extends <code>xs:string</code>, and represents a language tagged string in RDF. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/rdf:langString" target="mlserverdoc">rdf:langString</a>
    * @param string  The lexical value.
    * @param lang  The language.
    * @return  a RdfLangStringExpr expression
    */
    public RdfLangStringExpr langString(XsStringExpr string, XsStringExpr lang);
    /**
    * Returns the language of an <code>rdf:langString</code> value. <p>This function is a built-in.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/rdf:langString-language" target="mlserverdoc">rdf:langString-language</a>
    * @param val  The rdf:langString value.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr langStringLanguage(RdfLangStringExpr val);
    public RdfLangStringSeqExpr langStringSeq(RdfLangStringExpr... items);

}
