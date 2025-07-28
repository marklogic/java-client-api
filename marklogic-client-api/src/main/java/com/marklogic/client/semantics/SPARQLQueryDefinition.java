/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.semantics;

import java.util.Locale;

import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.query.QueryDefinition;

/**
 * <p>Represents a SPARQL query.</p>
 *
 * <p>For details about RDF, SPARQL, and semantics in MarkLogic see the <a
 * href="https://docs.marklogic.com/guide/semantics" target="_top">Semantics
 * Developer's Guide</a>.
 */
public interface SPARQLQueryDefinition extends QueryDefinition {
  /** Set the SPARQL query or update statement
   *
   * @param sparql the SPARQL statement
   */
  void setSparql(String sparql);

  /** Set the SPARQL query or update statement
   *
   * @param sparql the SPARQL statement
   */
  void setSparql(TextWriteHandle sparql);

  /** Get the SPARQL query or update statement
   *
   * @return the SPARQL statement
   */
  String getSparql();

  /** Set the SPARQL query or update statement
   *
   * @param sparql the SPARQL statement
   *
   * @return this instance (for method chaining)
   */
  SPARQLQueryDefinition withSparql(String sparql);

  /** Set the SPARQL query or update statement
   *
   * @param sparql the SPARQL statement
   *
   * @return this instance (for method chaining)
   */
  SPARQLQueryDefinition withSparql(TextWriteHandle sparql);

  /** Set the child SPARQLBindings instance.
   *
   * @param bindings bindings to set as the child SPARQLBindings instance
   */
  void setBindings(SPARQLBindings bindings);

  /** Get the child SPARQLBindings instance (normally populated by calls to
   * withBinding methods).
   *
   * @return the bindings set on this instance
   */
  SPARQLBindings getBindings();

  /** <p>Bind a variable of type iri.</p>
   *
   * @param name the bound variable name
   * @param value the iri value
   *
   * @return this instance (for method chaining)
   */
  SPARQLQueryDefinition withBinding(String name, String value);

  /** <p>Bind a variable of specified type.</p>
   *
   * @param name the bound variable name
   * @param value the value of the literal
   * @param type the literal type
   *
   * @return this instance (for method chaining)
   */
  SPARQLQueryDefinition withBinding(String name, String value, RDFTypes type);

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
  SPARQLQueryDefinition withBinding(String name, String value, Locale languageTag);

  /** <p>Remove all variable bindings from the child SPARQLBindings instance.</p>
   *
   * @return this instance (for method chaining)
   */
  SPARQLQueryDefinition clearBindings();

  /** <p>For use with {@link SPARQLQueryManager#executeUpdate SPARQL update},
   * where specified permissions will apply to any records created by the
   * update.  Create a GraphPermissions builder object with the specified
   * role and capabilities.</p>
   *
   * <p>For example:</p>
   *
   * <pre>    String sparqlUpdate = "INSERT DATA { &lt;a&gt; &lt;b&gt; &lt;c&gt; }";
   *    SPARQLQueryDefinition qdef = sparqlMgr.newQueryDefinition(sparqlUpdate);
   *    qdef.setUpdatePermissions(sparqlMgr.permission("rest-reader", Capability.UPDATE));
   *    sparqlMgr.executeUpdate(qdef);</pre>
   *
   * @param permissions the permissions (use {@link SPARQLQueryManager#permission} to create)
   */
  void setUpdatePermissions(GraphPermissions permissions);

  /** Get any permissions set on this instance.  This does not get any info
   * from the database.
   *
   * @return the permissions set on this instance
   */
  GraphPermissions getUpdatePermissions();

  /** Calls {@link #setUpdatePermissions} then returns this instance for
   * method chaining.
   *
   * @param role the name of the role receiving these capabilities
   * @param capability the capabilities (READ, UPDATE, or EXECUTE) granted to this role
   *
   * @return this instance (for method chaining)
   */
  SPARQLQueryDefinition withUpdatePermission(String role, Capability capability);

  String getBaseUri();

  /** set the base IRI for the query
   * @param uri the base uri
   */
  void setBaseUri(String uri);

  String[] getDefaultGraphUris();
  /** Set the URI of the graph or graphs to use as the default graph. Use
   * with SPARQL query only. If this parameter is used with SPARQL Update, it
   * will cause an exception.
   *
   * @param uris the default graph uris
   */
  void setDefaultGraphUris(String... uris);

  String[] getNamedGraphUris();
  /** Set the URI of a named graph or graphs to include in the query or
   * update operation.  Use with SPARQL query only. If this parameter is used
   * with SPARQL Update, it will cause an exception.
   * @param uris the named graph uris
   */
  void setNamedGraphUris(String... uris);

  String[] getUsingGraphUris();
  /** Set the URI of the graph or graphs to address as part of a SPARQL
   * update operation. Use with SPARQL Update only. If this parameter is used
   * with SPARQL query, it will cause an exception.
   *
   * @param uris the graph uris
   */
  void setUsingGraphUris(String... uris);

  String[] getUsingNamedGraphUris();
  /** Set the URI of a named graph or graphs to address as part of a SPARQL
   * update operation. Use with SPARQL Update only. If this parameter is used
   * with SPARQL query, it will cause an exception.
   *
   * @param uris the named graph uris
   */
  void setUsingNamedGraphUris(String... uris);

  /** Set the search query used to constrain the set of documents included in
   * the SPARQL query. Only meant to query unmanaged triples.  The behavior
   * is unspecified if used to query managed triples.
   *
   * @param query the constraining query
   */
  void setConstrainingQueryDefinition(QueryDefinition query);
  QueryDefinition getConstrainingQueryDefinition();
  /** Set the search query used to constrain the set of documents included in
   * the SPARQL query. Only meant to query unmanaged triples.  The behavior
   * is unspecified if used to query managed triples.
   *
   * @param query the query to use to constrain
   *
   * @return this instance (for method chaining)
   */
  SPARQLQueryDefinition withConstrainingQuery(QueryDefinition query);

  /** Set the name of rulesets to include for inferring triples. Ruleset
   * names can be those of the built-in rulesets, or user-managed rulesets
   * stored in the schemas database.
   *
   * @param ruleset the names of the rulesets to use
   */
  void setRulesets(SPARQLRuleset... ruleset);
  SPARQLRuleset[] getRulesets();
  /** Set the name of rulesets to include for inferring triples. Ruleset
   * names can be those of the built-in rulesets, or user-managed rulesets
   * stored in the schemas database.
   *
   * @param ruleset the name of the ruleset to use
   *
   * @return this instance (for method chaining)
   */
  SPARQLQueryDefinition withRuleset(SPARQLRuleset ruleset);

  /** Set whether to include database-default inference or not. Default is true.
   *
   * @param include whether to include or not
   *
   */
  void setIncludeDefaultRulesets(Boolean include);
  Boolean getIncludeDefaultRulesets();
  /** Set whether to include database-default inference or not. Default is true.
   *
   * @param include whether to include or not
   *
   * @return this instance (for method chaining)
   */
  SPARQLQueryDefinition withIncludeDefaultRulesets(Boolean include);

  int getOptimizeLevel();
  /** Set a number indicating how much time for the query engine to spend
   * analyzing a query. (See <a href="http://docs.marklogic.com/sem:sparql">sem:sparql</a>
   * in the server-side XQuery API docs)
   *
   * @param level the optimization level
   */
  void setOptimizeLevel(int level);
}
