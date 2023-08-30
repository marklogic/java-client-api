/*
 * Copyright (c) 2023 MarkLogic Corporation
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

import com.marklogic.client.type.XsStringVal;

import com.marklogic.client.type.ServerExpression;

// IMPORTANT: Do not edit. This file is generated. 

/**
 * Builds expressions to call functions in the rdf server library for a row
 * pipeline and constructs client values with rdf.* server types.
 */
public interface RdfExpr extends RdfValue {
    /**
  * Returns an rdf:langString value with the given value and language tag. The rdf:langString type extends xs:string, and represents a language tagged string in RDF.  This function is a built-in.
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/rdf:langString" target="mlserverdoc">rdf:langString</a> server function.
  * @param string  The lexical value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param lang  The language.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/rdf_langString.html">rdf:langString</a> server data type
  */
  public ServerExpression langString(ServerExpression string, String lang);
/**
  * Returns an rdf:langString value with the given value and language tag. The rdf:langString type extends xs:string, and represents a language tagged string in RDF.  This function is a built-in.
  *
  * <a name="ml-server-type-langString"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/rdf:langString" target="mlserverdoc">rdf:langString</a> server function.
  * @param string  The lexical value.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @param lang  The language.  (of <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/rdf_langString.html">rdf:langString</a> server data type
  */
  public ServerExpression langString(ServerExpression string, ServerExpression lang);
/**
  * Returns the language of an rdf:langString value. This function is a built-in.
  *
  * <a name="ml-server-type-langString-language"></a>
  
  * <p>
  * Provides a client interface to the <a href="http://docs.marklogic.com/rdf:langString-language" target="mlserverdoc">rdf:langString-language</a> server function.
  * @param val  The rdf:langString value.  (of <a href="{@docRoot}/doc-files/types/rdf_langString.html">rdf:langString</a>)
  * @return  a server expression with the <a href="{@docRoot}/doc-files/types/xs_string.html">xs:string</a> server data type
  */
  public ServerExpression langStringLanguage(ServerExpression val);
}
