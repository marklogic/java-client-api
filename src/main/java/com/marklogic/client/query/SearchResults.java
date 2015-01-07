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
     * Returns the query definition associated with this query.
     * @return The query definition.
     */
    public QueryDefinition getQueryCriteria();

    /**
     * Returns an estimate of the total number of results, which is accurate for unfiltered
     * queries. In unfiltered queries, documents are matched based entirely on indexes.
     * In filtered queries, documents are inspected during retrieval, which allows criteria
     * for which indexes don't exist but makes the final result count unpredictable.
     * @return The result estimate.
     */
    public long getTotalResults();

    /**
     * Returns the offset of the first result in the search reponse page.
     * @return	The first result number.
     */
    public long getStart();

    /**
     * Returns the maximum number of results in the search response page.
     * @return	The page size.
     */
    public int getPageLength();

    /**
     * The type of transform used to produce the result snippets.
     * @return	The snippet transform type
     */
    public String getSnippetTransformType();

    /**
     * Returns the search metrics.
     * @return The metrics.
     */
    public SearchMetrics          getMetrics();

    /**
     * Returns the match results.
     * @return The match results.
     */
    public MatchDocumentSummary[] getMatchResults();

    /**
     * Returns the array of facet results.
     * @return The facet results.
     */
    public FacetResult[]          getFacetResults();

    /**
     * Returns the facet results for the named facet.
     * @param name The facet name.
     * @return The facet results, or null if no facet with the specified name exists.
     */
    public FacetResult            getFacetResult(String name);

    /**
     * Returns the array of facet names returned by this search.
     * @return The array facet names.
     */
    public String[]               getFacetNames();

    /**
     * Returns the query plan.
     * @return The query plan.
     */
    public Document               getPlan();

    /**
     * Returns the array of warnings returned by this search.
     * @return The warnings.
     */
    public SearchHandle.Warning[] getWarnings();

    /**
     * Returns the array of reports returned by this search.
     * @return The reports.
     */
    public SearchHandle.Report[]  getReports();

    /**
     * Returns the array of constraint names used in the search
     * (if requested).
     * @return	The constraint names.
     */
	public String[] getConstraintNames();

    /**
     * Returns the named constraint used in the search
     * (if requested).
     * @param name	The constraint name.
     * @param handle	An XML handle for reading the constraint.
     * @return	The handle on the constraint.
     */
	public <T extends XMLReadHandle> T getConstraint(String name, T handle);

    /**
     * Returns an iterator over the constraints used in the search
     * (if requested).
     * @param handle	An XML handle for reading the constraints.
     * @return	An iterator that populates the handle with each constraint.
     */
	public <T extends XMLReadHandle> Iterator<T> getConstraintIterator(T handle);

    /**
     * Returns the plan for the search (if requested).
     * @param handle	An XML handle for reading the plan.
     * @return	The handle on the plan.
     */
	public <T extends XMLReadHandle> T getPlan(T handle);

	/**
	 * Returns the string query input (if provided and requested).
	 * @return	The string queries.
	 */
    public String[] getStringQueries();

	/**
	 * Returns the cts:query used in the search (if requested).
     * @param handle	An XML handle for reading the query.
     * @return	The handle on the query.
	 */
    public <T extends XMLReadHandle> T getQuery(T handle);
}

