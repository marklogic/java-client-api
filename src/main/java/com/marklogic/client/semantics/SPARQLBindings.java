/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.semantics;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/** <p>Represents binding names and values to be sent with a SPARQL Query.
 * Available for situations where {@link SPARQLQueryDefinition#withBinding
 * SPARQLQueryDefinition.withBinding} methods are not enough.  For example
 * usage, see {@link SPARQLQueryManager}.</p>
 *
 * <p>For more about RDF literals, see <a
 * href="http://www.w3.org/TR/rdf11-concepts/#section-Graph-Literal"
 * target="_top">RDF 1.1 section 3.3</a>.
 *
 * <p>For details about RDF, SPARQL, and semantics in MarkLogic see the <a
 * href="https://docs.marklogic.com/guide/semantics" target="_top">Semantics
 * Developer's Guide</a>.
 */
public interface SPARQLBindings extends Map<String, List<SPARQLBinding>> {
    /** Bind a variable of type iri.
     * @param name the bound variable name
     * @param value the value of type iri
     *
     * @return this instance (for method chaining)
     */
    public SPARQLBindings bind(String name, String value);
    /** Bind a variable of specified type.
     * @param name the bound variable name
     * @param value the value of type iri
     * @param datatype the type
     *
     * @return this instance (for method chaining)
     */
    public SPARQLBindings bind(String name, String value, RDFTypes datatype);
    /** Bind a variable of type
     * http://www.w3.org/1999/02/22-rdf-syntax-ns#langString with the specified
     * language tag.  Note that we call <a
     * href="http://docs.oracle.com/javase/7/docs/api/java/util/Locale.html#toLanguageTag%28%29"
     * >Locale.toLanguageTag()</a>
     * to get compliant IETF BCP 47 language tags. */
    public SPARQLBindings bind(String name, String value, Locale languageTag);
};
