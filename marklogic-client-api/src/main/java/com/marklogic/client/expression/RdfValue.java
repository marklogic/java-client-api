/*
 * Copyright (c) 2019 MarkLogic Corporation
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

import com.marklogic.client.type.RdfLangStringSeqVal;
import com.marklogic.client.type.RdfLangStringVal;

/**
 * RdfValue takes Java values and constructs atomic values and
 * sequences of atomic values with an rdf data type
 * 
 * The typed values can then be passed to expression functions
 * for execution on the server.
 */
public interface RdfValue {
    /**
     * Takes a language-specific string and constructs an rdf:langString value
     * @param string	the string in the language
     * @param lang	the identifier for the language 
     * @return	a value with an rdf:langString data type
     */
    public RdfLangStringVal    langString(String string, String lang);
    /**
     * Takes any number of language-specific strings and constructs an rdf:langString sequence
     * @param langStrings	the language-specific strings
     * @return	a value sequence with an rdf:langString data type
     */
    public RdfLangStringSeqVal langStringSeq(RdfLangStringVal... langStrings);
}
