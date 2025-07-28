/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.semantics;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.SPARQLResultsReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.TriplesReadHandle;
import com.marklogic.client.io.marker.XMLReadHandle;

/**
 * <p>A manager for executing SPARQL queries in MarkLogic Server and retrieving
 * the results.
 *
 * <p>For example perform a SPARQL SELECT:</p>
 *
 * <pre>    SPARQLQueryManager sparqlMgr = databaseClient.newSPARQLQueryManager();
 *    String sparql = "SELECT * WHERE { ?s ?p ?o } LIMIT 10";
 *    SPARQLQueryDefinition query = sparqlMgr.newQueryDefinition(sparql)
 *        .withBinding("o", "http://example.org/object1");
 *    JacksonHandle handle = new JacksonHandle();
 *    handle.setMimetype(SPARQLMimeTypes.SPARQL_JSON);
 *    JacksonHandle results = sparqlMgr.executeSelect(query, handle);
 *    JsonNode tuples = results.get().path("results").path("bindings");
 *    for ( JsonNode row : tuples ) {
 *        String s = row.path("s").path("value").asText();
 *        String p = row.path("p").path("value").asText();
 *        ...
 *    }</pre>
 *
 * <p>Or perform a SPARQL CONSTRUCT:</p>
 *
 * <pre>    String sparql = "CONSTRUCT { &lt;a&gt; &lt;b&gt; &lt;c&gt; } WHERE { ?s ?p ?o } LIMIT 10";
 *    SPARQLQueryDefinition query = sparqlMgr.newQueryDefinition(sparql);
 *    SPARQLBindings bindings = query.getBindings();
 *    query.setBindings(bindings.bind("o", "http://example.org/object1"));
 *    JacksonHandle handle = new JacksonHandle();
 *    handle.setMimetype(RDFMimeTypes.RDFJSON);
 *    JacksonHandle triples = sparqlMgr.executeConstruct(query, handle);
 *    JsonNode a = triples.get().path("a");
 *    JsonNode b = a.path("b");
 *    JsonNode c = b.get(0).path("value");</pre>
 *
 * <p>Each new instance of SPARQLQueryManager is created by
 * {@link DatabaseClient#newSPARQLQueryManager}.  While these examples use
 * JacksonHandle, any SPARQLResultsReadHandle may be used--including custom
 * handles.  For executeSelect {@link JSONReadHandle}s will need to use {@link
 * SPARQLMimeTypes#SPARQL_JSON} mimetype, and {@link XMLReadHandle}s will need
 * to use {@link SPARQLMimeTypes#SPARQL_XML} mimetype, other {@link
 * SPARQLResultsReadHandle}s accept any text and can therefore accept results
 * in any of the SPARQLMimeTypes.  For executeDescribe and executeConstruct
 * {@link JSONReadHandle}s will need to use {@link RDFMimeTypes#RDFJSON}
 * mimetype, and {@link XMLReadHandle}s will need to use {@link
 * RDFMimeTypes#RDFXML} mimetype, other {@link TriplesReadHandle}s accept any
 * text and can therefore accept results in any of the RDFMimeTypes.
 *
 * <p>SPARQLQueryManager is thread-safe other than setPageLength. In other
 * words the only state maintained by an instance is the page length.  Common
 * usage is to call setPageLength only once then use the instance across many
 * threads.  If you intend to call setPageLength from multiple threads, create
 * a new SPARQLQueryManager for each thread.</p>
 *
 * <p>For details about RDF, SPARQL, and semantics in MarkLogic see the
 * <a href="https://docs.marklogic.com/guide/semantics" target="_top">Semantics Developer's Guide</a>.
 */
public interface SPARQLQueryManager  {
  /** Instantiate a new SPARQLQueryDefinition.
   *
   * @return an empty SPARQLQueryDefinition
   */
  SPARQLQueryDefinition newQueryDefinition();

  /** Instantiate a new SPARQLQueryDefinition with provided SPARQL.
   *
   * @param sparql a sparql query as text
   * @return the query as SPARQLQueryDefinition
   */
  SPARQLQueryDefinition newQueryDefinition(String sparql);

  /** Instantiate a new SPARQLQueryDefinition with the SPARQL from
   * the provided TextWriteHandle.
   *
   * @param sparql the handle containing a sparql query as text
   * @return the query as SPARQLQueryDefinition
   */
  SPARQLQueryDefinition newQueryDefinition(TextWriteHandle sparql);

  /** Execute a SPARQL "SELECT" query.
   * @param qdef the query
   * @param handle the handle capable of reading {@link SPARQLMimeTypes sparql results}
   * @param <T> the type of SPARQLResultsReadHandle to return
   * @return the results in the provided SPARQLResultsReadHandle
   */
  <T extends SPARQLResultsReadHandle> T executeSelect(SPARQLQueryDefinition qdef, T handle);

  /** Execute a SPARQL "SELECT" query.
   * @param qdef the query
   * @param handle the handle capable of reading {@link SPARQLMimeTypes sparql results}
   * @param tx the transaction context for this operation
   * @param <T> the type of SPARQLResultsReadHandle to return
   * @return the results in the provided SPARQLResultsReadHandle
   */
  <T extends SPARQLResultsReadHandle> T executeSelect(SPARQLQueryDefinition qdef, T handle, Transaction tx);

  /** Execute a SPARQL "SELECT" query.
   * @param qdef the query
   * @param handle the handle capable of reading {@link SPARQLMimeTypes sparql results}
   * @param start when paging through results, the first result of this page--must be &gt; 0.
   *     Use together with {@link #setPageLength}.
   * @param <T> the type of SPARQLResultsReadHandle to return
   * @return the results in the provided SPARQLResultsReadHandle
   */
  <T extends SPARQLResultsReadHandle> T executeSelect(SPARQLQueryDefinition qdef, T handle, long start);

  /** Execute a SPARQL "SELECT" query.
   * @param qdef the query
   * @param handle the handle capable of reading {@link SPARQLMimeTypes sparql results}
   * @param start when paging through results, the first result of this page--must be &gt; 0.
   *     Use together with {@link #setPageLength}.
   * @param tx the transaction context for this operation
   * @param <T> the type of SPARQLResultsReadHandle to return
   * @return the results in the provided SPARQLResultsReadHandle
   */
  <T extends SPARQLResultsReadHandle> T executeSelect(SPARQLQueryDefinition qdef, T handle, long start, Transaction tx);

  /** @return the currently set pageLength or -1 if no page length has been set */
  long getPageLength();

  /** Set a page length for all SPARQL "SELECT" queries sent by this instance.
   * @param pageLength the non-negative number of results per page
   */
  void setPageLength(long pageLength);

  /** Reset this instance to have no page length set. */
  void clearPageLength();

  /** Execute a SPARQL "CONSTRUCT" statement.
   * @param qdef the SPARQL "CONSTRUCT" statement
   * @param handle the handle capable of reading {@link RDFMimeTypes triples or quads results}
   * @param <T> the type of TriplesReadHandle to return
   * @return the results in the provided TriplesReadHandle
   */
  <T extends TriplesReadHandle> T executeConstruct(SPARQLQueryDefinition qdef, T handle);

  /** Execute a SPARQL "CONSTRUCT" statement.
   * @param qdef the SPARQL "CONSTRUCT" statement
   * @param handle the handle capable of reading {@link RDFMimeTypes triples or quads results}
   * @param tx the transaction context for this query
   * @param <T> the type of TriplesReadHandle to return
   * @return the results in the provided TriplesReadHandle
   */
  <T extends TriplesReadHandle> T executeConstruct(SPARQLQueryDefinition qdef, T handle, Transaction tx);

  /** Execute a SPARQL "DESCRIBE" query (which implements the Concise Bounded Description specification).
   * @param qdef the query
   * @param handle the handle capable of reading {@link RDFMimeTypes triples or quads results}
   * @param <T> the type of TriplesReadHandle to return
   * @return the results in the provided TriplesReadHandle
   */
  <T extends TriplesReadHandle> T executeDescribe(SPARQLQueryDefinition qdef, T handle);

  /** Execute a SPARQL "DESCRIBE" query (which implements the Concise Bounded Description specification).
   * @param qdef the query
   * @param handle the handle capable of reading {@link RDFMimeTypes triples or quads results}
   * @param tx the transaction context for this query
   * @param <T> the type of TriplesReadHandle to return
   * @return the results in the provided TriplesReadHandle
   */
  <T extends TriplesReadHandle> T executeDescribe(SPARQLQueryDefinition qdef, T handle, Transaction tx);

  /** Execute a SPARQL "ASK" statement.
   * @param qdef the SPARQL "CONSTRUCT" statement
   * @return the answer as Boolean
   */
  Boolean executeAsk(SPARQLQueryDefinition qdef);

  /** Execute a SPARQL "ASK" statement.
   * @param qdef the SPARQL "CONSTRUCT" statement
   * @param tx the transaction context for this query
   * @return the answer as Boolean
   */
  Boolean executeAsk(SPARQLQueryDefinition qdef, Transaction tx);

  /** Execute a SPARQL update statement.  For an example of using with
   * permisisons see {@link #permission}.
   *
   * @param qdef the SPARQL update statement
   */
  void executeUpdate(SPARQLQueryDefinition qdef);

  /** Execute a SPARQL update statement.  For an example of using with
   * permisisons see {@link #permission}.
   *
   * @param qdef the SPARQL update statement
   * @param tx the transaction context for this operation
   */
  void executeUpdate(SPARQLQueryDefinition qdef, Transaction tx);


  /** <p>For use with SPARQL update, where specified permissions will apply
   * to any records created by the update.  Create a GraphPermissions builder
   * object with the specified role and capabilities.</p>
   *
   * <p>For example:</p>
   *
   * <pre>    String sparqlUpdate = "INSERT DATA { &lt;a&gt; &lt;b&gt; &lt;c&gt; }";
   *    SPARQLQueryDefinition qdef = sparqlMgr.newQueryDefinition(sparqlUpdate);
   *    qdef.setUpdatePermissions(sparqlMgr.permission("rest-reader", Capability.UPDATE));
   *    sparqlMgr.executeUpdate(qdef);</pre>
   *
   * @param role the name of the role receiving these capabilities
   * @param capabilities the capabilities (READ, UPDATE, or EXECUTE) granted to this role
   * @return the new GraphPermissions object with these capabilities set
   */
  GraphPermissions permission(String role, Capability... capabilities);

}
