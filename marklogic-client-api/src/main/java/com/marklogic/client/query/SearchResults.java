/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

import java.util.Iterator;

import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.marker.XMLReadHandle;

import org.w3c.dom.Document;

/**
 * The SearchResults represent the set of results returned by a search.
 */
public interface SearchResults {
  /**
   * Returns the search query definition associated with this query.
   * @return The search query definition.
   */
  SearchQueryDefinition getQueryCriteria();

  /**
   * Returns an estimate of the total number of results, which is accurate for unfiltered
   * queries. In unfiltered queries, documents are matched based entirely on indexes.
   * In filtered queries, documents are inspected during retrieval, which allows criteria
   * for which indexes don't exist but makes the final result count unpredictable.
   * @return The result estimate.
   */
  long getTotalResults();

  /**
   * Returns the offset of the first result in the search reponse page.
   * @return	The first result number.
   */
  long getStart();

  /**
   * Returns the maximum number of results in the search response page.
   * @return	The page size.
   */
  int getPageLength();

  /**
   * The type of transform used to produce the result snippets.
   * @return	The snippet transform type
   */
  String getSnippetTransformType();

  /**
   * Returns the search metrics.
   * @return The metrics.
   */
  SearchMetrics          getMetrics();

  /**
   * Returns the match results.
   * @return The match results.
   */
  MatchDocumentSummary[] getMatchResults();

  /**
   * Returns the array of facet results.
   * @return The facet results.
   */
  FacetResult[]          getFacetResults();

  /**
   * Returns the facet results for the named facet.
   * @param name The facet name.
   * @return The facet results, or null if no facet with the specified name exists.
   */
  FacetResult            getFacetResult(String name);

  /**
   * Returns the array of facet names returned by this search.
   * @return The array facet names.
   */
  String[]               getFacetNames();

  /**
   * Returns the query plan.
   * @return The query plan.
   */
  Document               getPlan();

  /**
   * Returns the array of warnings returned by this search.
   * @return The warnings.
   */
  SearchHandle.Warning[] getWarnings();

  /**
   * Returns the array of reports returned by this search.
   * @return The reports.
   */
  SearchHandle.Report[]  getReports();

  /**
   * Returns the array of constraint names used in the search
   * (if requested).
   * @return	The constraint names.
   */
  String[] getConstraintNames();

  /**
   * Returns the named constraint used in the search
   * (if requested).
   * @param name	The constraint name.
   * @param handle	An XML handle for reading the constraint.
   * @param <T> the type of XMLReadHandle to return
   * @return	The handle on the constraint.
   */
  <T extends XMLReadHandle> T getConstraint(String name, T handle);

  /**
   * Returns an iterator over the constraints used in the search
   * (if requested).
   * @param handle	An XML handle for reading the constraints.
   * @param <T> the type of XMLReadHandle's to return in the Iterator
   * @return	An iterator that populates the handle with each constraint.
   */
  <T extends XMLReadHandle> Iterator<T> getConstraintIterator(T handle);

  /**
   * Returns the plan for the search (if requested).
   * @param handle	An XML handle for reading the plan.
   * @param <T> the type of XMLReadHandle to return
   * @return	The handle on the plan.
   */
  <T extends XMLReadHandle> T getPlan(T handle);

  /**
   * Returns the string query input (if provided and requested).
   * @return	The string queries.
   */
  String[] getStringQueries();

  /**
   * Returns the cts:query used in the search (if requested).
   * @param handle	An XML handle for reading the query.
   * @param <T> the type of XMLReadHandle to return
   * @return	The handle on the query.
   */
  <T extends XMLReadHandle> T getQuery(T handle);
}
