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
 * SPARQLQueryDefinition.withBinding} methods are not enough.
 *
 * <p>Example matching an iri:</p>
 *
 * <pre>    graphMgr.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
 *    graphMgr.writeAs("http://example.org",
 *        "&lt;http://example.org/s1&gt; &lt;http://example.org/p1&gt; &lt;http://example.org/object1&gt; .\n" +
 *        "&lt;http://example.org/s2&gt; &lt;http://example.org/p2&gt; \"object2\" .\n" +
 *        "&lt;http://example.org/s3&gt; &lt;http://example.org/p3&gt; \"object3\"@en .");
 *    String select = "SELECT * WHERE { ?s ?p ?o }";
 *    SPARQLQueryDefinition qdef = sparqlMgr.newQueryDefinition(select);
 *    SPARQLBindings bindings = qdef.getBindings();
 *    bindings.bind("o", "http://example.org/object1");
 *    JacksonHandle results = sparqlMgr.executeSelect(qdef, new JacksonHandle());</pre>
 *
 * <p>Example matching a literal of rdf data type string (re-using data and variables above):</p>
 *
 * <pre>    qdef = sparqlMgr.newQueryDefinition(select);
 *    bindings = qdef.getBindings();
 *    bindings.bind("o", "object2", RDFTypes.STRING);
 *    results = sparqlMgr.executeSelect(qdef, new JacksonHandle());</pre>
 *
 *
 * <p>Example matching a string with a language tag (re-using data and variables above):</p>
 *
 * <pre>    qdef = sparqlMgr.newQueryDefinition(select);
 *    bindings = qdef.getBindings();
 *    bindings.bind("o", "object3", new Locale("en"));
 *    results = sparqlMgr.executeSelect(qdef, new JacksonHandle());</pre>
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
    /** <p>Bind a variable of type iri.</p>
     *
     * @param name the bound variable name
     * @param value the iri value
     *
     * @return this instance (for method chaining)
     */
    public SPARQLBindings bind(String name, String value);

    /** <p>Bind a variable of specified type.</p>
     *
     * @param name the bound variable name
     * @param value the value of the literal
     * @param datatype the literal type
     *
     * @return this instance (for method chaining)
     */
    public SPARQLBindings bind(String name, String value, RDFTypes datatype);

    /** <p>Bind a variable of type
     * http://www.w3.org/1999/02/22-rdf-syntax-ns#langString with the specified
     * language tag.  Note that we call {@link Locale#toLanguageTag}
     * to get compliant IETF BCP 47 language tags.</p>
     *
     * @param name the bound variable name
     * @param value the value as a string
     * @param languageTag the language and regional modifiers compliant with BCP-47
     *
     * @return this instance (for method chaining)
     */
    public SPARQLBindings bind(String name, String value, Locale languageTag);
};
