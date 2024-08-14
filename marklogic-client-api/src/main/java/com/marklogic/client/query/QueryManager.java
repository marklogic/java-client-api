/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

import javax.xml.namespace.QName;

import com.marklogic.client.Transaction;
import com.marklogic.client.expression.*;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.CtsQueryWriteHandle;
import com.marklogic.client.io.marker.QueryOptionsListReadHandle;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.TuplesReadHandle;
import com.marklogic.client.io.marker.ValuesListReadHandle;
import com.marklogic.client.io.marker.ValuesReadHandle;
import com.marklogic.client.util.RequestLogger;

/**
 * A Query Manager supports searching documents and retrieving values and
 * tuples from lexicons. Query configuration depends on the kind of query
 * definition used, the parameters to that definition, and the server-side
 * options used. Server-side options can be constructed and written to
 * the server with a QueryOptionsManager. Once stored on the server, a
 * set of named query options can be used by any query.
 */
public interface QueryManager {
  /**
   * The default maximum number of documents in a page of search results.
   */
  long DEFAULT_PAGE_LENGTH = -1;
  /**
   * The offset of the first result in the default page.
   */
  long START = 1;

  /**
   * The view produced by a query.
   */
  public enum QueryView {
    /**
     * The view specified by the query options.
     */
    DEFAULT,
    /**
     * A list of result documents with snippets, extracted metadata,
     * and so on.
     */
    RESULTS,
    /**
     * Values from different constraints for the matched documents.
     */
    FACETS,
    /**
     * Basic summary information about the matched documents.
     */
    METADATA,
    /**
     * All views that the query can produce.
     */
    ALL;
  }

  /**
   * Returns the maximum number of documents that can appear in any page of query results.
   * @return the maximum number of results
   */
  long getPageLength();

  /**
   * Specifies the maximum number of documents that can appear in any page of the query results,
   * overriding any maximum specified in the query options.
   * @param length	the maximum number of results
   */
  void setPageLength(long length);

  /**
   * Returns the type of view results produced by queries.
   * @return	the view type for the queries
   */
  QueryView getView();

  /**
   * Specifies the type of view results produced by queries.
   * @param view	the view type for the queries
   */
  void setView(QueryView view);

  /**
   * Creates a query definition based on a string and the default
   * query options.  The string
   * has a simple grammar for specifying constraint values
   * for indexes and can be supplied by an end-user in a web form.
   * @return	the string query definition
   */
  StringQueryDefinition newStringDefinition();

  /**
   * Creates a query definition based on a string and on named query options
   * saved previously.
   * @param optionsName	the name of the query options
   * @return	the string query definition
   */
  StringQueryDefinition newStringDefinition(String optionsName);

  /**
   * Creates a suggestion definition based on a single string for completion,
   * using the default options node.
   * @return	the suggest definition.
   */
  SuggestDefinition newSuggestDefinition();

  /**
   * Creates a suggestion definition based on a query options name.
   * @param optionsName	the name of the query options
   * @return	the suggest definition.
   */
  SuggestDefinition newSuggestDefinition(String optionsName);


  /**
   * Creates a suggestion definition based on a single string for completion,
   * using the default options node.
   * @param suggestString A string as input for completion suggestions.
   * @param optionsName	the name of the query options
   * @return	the suggest definition.
   */
  SuggestDefinition newSuggestDefinition(String suggestString, String optionsName);

  /**
   * Creates a query definition based on a structure that identifies
   * clauses and conjunctions and the default query options.
   * @return	the structured query definition
   */
  StructuredQueryBuilder newStructuredQueryBuilder();
  /**
   * Creates a query definition based on a structure and on named
   * query options saved previously.
   * @param optionsName	the name of the query options
   * @return	the structured query definition
   */
  StructuredQueryBuilder newStructuredQueryBuilder(String optionsName);

  /**
   * Creates a query definition for deleting documents.
   * @return	the deletion query definition
   */
  DeleteQueryDefinition newDeleteDefinition();

  /**
   * Creates a query definition for retrieving values based on
   * a named constraint on an index and the default query options.
   * @param name	the index constraint
   * @return	the values query definition
   */
  ValuesDefinition newValuesDefinition(String name);

  /**
   * Creates a query definition for retrieving values based on
   * a named constraint and on named query options saved previously.
   * @param name	the index constraint
   * @param optionsName	the name of the query options
   * @return	the values query definition
   */
  ValuesDefinition newValuesDefinition(String name, String optionsName);

  /**
   * Creates a query definition for retrieving the list of available
   * named lexicon configurations from the default query options.
   * @return the values list definition
   */
  ValuesListDefinition newValuesListDefinition();

  /**
   * Creates a query definition for retrieving the list of available
   * named lexicon configurations from the named query options.
   * @param optionsName the name of the query options
   * @return the values list definition
   */
  ValuesListDefinition newValuesListDefinition(String optionsName);

  /**
   * Searches documents based on query criteria and, potentially, previously
   * saved query options.
   * @param querydef	the definition of query criteria and query options
   * @param searchHandle	a handle for reading the results from the search
   * @param <T> the type of SearchReadHandle to return
   * @return	the handle populated with the results from the search
   */
  <T extends SearchReadHandle> T search(SearchQueryDefinition querydef, T searchHandle);
  /**
   * Searches documents based on query criteria and, potentially, previously
   * saved query options.
   * @param querydef	the definition of query criteria and query options
   * @param searchHandle	a handle for reading the results from the search
   * @param <T> the type of SearchReadHandle to return
   * @param forestName a forest to limit this search
   * @return	the handle populated with the results from the search
   */
  <T extends SearchReadHandle> T search(SearchQueryDefinition querydef, T searchHandle, String forestName);
  /**
   * Searches documents based on query criteria and, potentially, previously
   * saved query options starting with the specified page listing
   * document results.
   * @param querydef	the definition of query criteria and query options
   * @param searchHandle	a handle for reading the results from the search
   * @param start	the offset of the first document in the page (where 1 is the first result)
   * @param <T> the type of SearchReadHandle to return
   * @return	the handle populated with the results from the search
   */
  <T extends SearchReadHandle> T search(SearchQueryDefinition querydef, T searchHandle, long start);
  /**
   * Searches documents based on query criteria and, potentially, previously
   * saved query options starting with the specified page listing
   * document results.
   * @param querydef	the definition of query criteria and query options
   * @param searchHandle	a handle for reading the results from the search
   * @param start	the offset of the first document in the page (where 1 is the first result)
   * @param <T> the type of SearchReadHandle to return
   * @param forestName a forest to limit this search
   * @return	the handle populated with the results from the search
   */
  <T extends SearchReadHandle> T search(SearchQueryDefinition querydef, T searchHandle, long start, String forestName);
  /**
   * Searches documents based on query criteria and, potentially, previously
   * saved query options.  The search includes documents modified by the
   * transaction and ignores documents deleted by the transaction.
   * @param querydef	the definition of query criteria and query options
   * @param searchHandle	a handle for reading the results from the search
   * @param transaction	a open transaction for matching documents
   * @param <T> the type of SearchReadHandle to return
   * @return	the handle populated with the results from the search
   */
  <T extends SearchReadHandle> T search(SearchQueryDefinition querydef, T searchHandle, Transaction transaction);
  /**
   * Searches documents based on query criteria and, potentially, previously
   * saved query options.  The search includes documents modified by the
   * transaction and ignores documents deleted by the transaction.
   * @param querydef	the definition of query criteria and query options
   * @param searchHandle	a handle for reading the results from the search
   * @param transaction	a open transaction for matching documents
   * @param <T> the type of SearchReadHandle to return
   * @param forestName a forest to limit this search
   * @return	the handle populated with the results from the search
   */
  <T extends SearchReadHandle> T search(SearchQueryDefinition querydef, T searchHandle, Transaction transaction, String forestName);
  /**
   * Searches documents based on query criteria and, potentially, previously
   * saved query options starting with the specified page listing
   * document results.  The search includes documents modified by the
   * transaction and ignores documents deleted by the transaction.
   * @param querydef	the definition of query criteria and query options
   * @param searchHandle	a handle for reading the results from the search
   * @param start	the offset of the first document in the page (where 1 is the first result)
   * @param transaction	a open transaction for matching documents
   * @param <T> the type of SearchReadHandle to return
   * @return	the handle populated with the results from the search
   */
  <T extends SearchReadHandle> T search(SearchQueryDefinition querydef, T searchHandle, long start, Transaction transaction);
  /**
   * Searches documents based on query criteria and, potentially, previously
   * saved query options starting with the specified page listing
   * document results.  The search includes documents modified by the
   * transaction and ignores documents deleted by the transaction.
   * @param querydef	the definition of query criteria and query options
   * @param searchHandle	a handle for reading the results from the search
   * @param start	the offset of the first document in the page (where 1 is the first result)
   * @param transaction	a open transaction for matching documents
   * @param <T> the type of SearchReadHandle to return
   * @param forestName a forest to limit this search
   * @return	the handle populated with the results from the search
   */
  <T extends SearchReadHandle> T search(SearchQueryDefinition querydef, T searchHandle, long start, Transaction transaction, String forestName);

  /**
   * Queries the REST server for suggested string completions based on
   * values in the SuggestionDefinition.  The list of strings returned by
   * this function can be used to provide possible values for completing
   * a string search.
   * @param suggestionDef the partial string to complete and suggest query options
   * @return the suggested string completions
   */
  String[] suggest(SuggestDefinition suggestionDef);

  /**
   * Deletes documents based on the query criteria.
   * @param querydef	the definition of query criteria
   */
  void delete(DeleteQueryDefinition querydef);

  /**
   * Deletes documents based on the query criteria as part
   * of the specified transaction.
   * @param querydef	the definition of query criteria
   * @param transaction	a open transaction for the delete operation
   */
  void delete(DeleteQueryDefinition querydef, Transaction transaction);

  /**
   * Retrieves values from indexes based on query criteria and, potentially,
   * previously saved query options.
   * @param valdef	the definition of query criteria and query options
   * @param valueHandle	a handle for reading the values for the matched documents
   * @param <T> the type of ValuesReadHandle to return
   * @return	the handle populated with the values from the index
   */
  <T extends ValuesReadHandle> T values(ValuesDefinition valdef, T valueHandle);

  /**
   * Retrieves values from indexes based on query criteria and, potentially,
   * previously saved query options.
   * @param valdef	the definition of query criteria and query options
   * @param valueHandle	a handle for reading the values for the matched documents
   * @param start	the offset of the first returned result (where 1 is the first value)
   * @param <T> the type of ValuesReadHandle to return
   * @return	the handle populated with the values from the index
   */
  <T extends ValuesReadHandle> T values(ValuesDefinition valdef, T valueHandle, long start);

  /**
   * Retrieves values from indexes based on query criteria and, potentially,
   * previously saved query options.  The query includes documents modified
   * by the transaction and ignores documents deleted by the transaction.
   * @param valdef	the definition of query criteria and query options
   * @param valueHandle	a handle for reading the values for the matched documents
   * @param transaction	a open transaction for matching documents
   * @param <T> the type of ValuesReadHandle to return
   * @return	the handle populated with the values from the index
   */
  <T extends ValuesReadHandle> T values(ValuesDefinition valdef, T valueHandle, Transaction transaction);

  /**
   * Retrieves values from indexes based on query criteria and, potentially,
   * previously saved query options.  The query includes documents modified
   * by the transaction and ignores documents deleted by the transaction.
   * @param valdef	the definition of query criteria and query options
   * @param valueHandle	a handle for reading the values for the matched documents
   * @param start	the offset of the first returned result (where 1 is the first value)
   * @param transaction	a open transaction for matching documents
   * @param <T> the type of ValuesReadHandle to return
   * @return	the handle populated with the values from the index
   */
  <T extends ValuesReadHandle> T values(ValuesDefinition valdef, T valueHandle, long start, Transaction transaction);

  /**
   * Retrieves combinations of values for the same document from indexes
   * based on query criteria and, potentially, previously saved query options.
   * @param valdef	the definition of query criteria and query options
   * @param tupleHandle	a handle for reading the tuples for the matched documents
   * @param <T> the type of TuplesReadHandle to return
   * @return	the handle populated with the tuples from the index
   */
  <T extends TuplesReadHandle> T tuples(ValuesDefinition valdef, T tupleHandle);

  /**
   * Retrieves combinations of values for the same document from indexes
   * based on query criteria and, potentially, previously saved query options.
   * @param valdef	the definition of query criteria and query options
   * @param tupleHandle	a handle for reading the tuples for the matched documents
   * @param start	the offset of the first returned result (where 1 is the first tuple)
   * @param <T> the type of TuplesReadHandle to return
   * @return	the handle populated with the tuples from the index
   */
  <T extends TuplesReadHandle> T tuples(ValuesDefinition valdef, T tupleHandle, long start);

  /**
   * Retrieves combinations of values for the same document from indexes
   * based on query criteria and, potentially, previously saved query options.
   * The query includes documents modified by the transaction and ignores
   * documents deleted by the transaction.
   * @param valdef	the definition of query criteria and query options
   * @param tupleHandle	a handle for reading the tuples for the matched documents
   * @param transaction	a open transaction for matching documents
   * @param <T> the type of TuplesReadHandle to return
   * @return	the handle populated with the tuples from the index
   */
  <T extends TuplesReadHandle> T tuples(ValuesDefinition valdef, T tupleHandle, Transaction transaction);

  /**
   * Retrieves combinations of values for the same document from indexes
   * based on query criteria and, potentially, previously saved query options.
   * The query includes documents modified by the transaction and ignores
   * documents deleted by the transaction.
   * @param valdef	the definition of query criteria and query options
   * @param tupleHandle	a handle for reading the tuples for the matched documents
   * @param start	the offset of the first returned result (where 1 is the first tuple)
   * @param transaction	a open transaction for matching documents
   * @param <T> the type of TuplesReadHandle to return
   * @return	the handle populated with the tuples from the index
   */
  <T extends TuplesReadHandle> T tuples(ValuesDefinition valdef, T tupleHandle, long start, Transaction transaction);

  /**
   * Retrieves the list of available named lexicon configurations from the
   * values list definition and, potentially, previously saved query options.
   * @param valdef the definition of the query criteria and options
   * @param valueHandle a handle for reading the list of names lexicon configurations
   * @param <T> the type of ValuesListReadHandle to return
   * @return the handle populated with the names
   */
  <T extends ValuesListReadHandle> T valuesList(ValuesListDefinition valdef, T valueHandle);

  /**
   * Retrieves the list of available named lexicon configurations from the
   * values list definition and, potentially, previously saved query options.
   * The query includes options modified by the transaction and ignores
   * options deleted by the transaction.
   * @param valdef the definition of the query criteria and options
   * @param valueHandle a handle for reading the list of names lexicon configurations
   * @param transaction	a open transaction for matching documents
   * @param <T> the type of ValuesListReadHandle to return
   * @return the handle populated with the names
   */
  <T extends ValuesListReadHandle> T valuesList(ValuesListDefinition valdef, T valueHandle, Transaction transaction);

  /**
   * Retrieves the list of available named query options.
   * @param listHandle a handle for reading the list of name options
   * @param <T> the type of QueryOptionsListReadHandle to return
   * @return the handle populated with the names
   */
  <T extends QueryOptionsListReadHandle> T optionsList(T listHandle);

  /**
   * Retrieves the list of available named query options.
   * The query includes options modified by the transaction and ignores
   * options deleted by the transaction.
   * @param valueHandle a handle for reading the list of name options
   * @param transaction	a open transaction for matching documents
   * @param <T> the type of QueryOptionsListReadHandle to return
   * @return the handle populated with the names
   */
  <T extends QueryOptionsListReadHandle> T optionsList(T valueHandle, Transaction transaction);

  /**
   * The findOne method is a convenience.
   * It searches documents based on query criteria and, potentially, previously
   * saved query options. It returns the MatchDocumentSummary of the first
   * search result.
   * @param querydef	the definition of query criteria and query options
   * @return the summary of the first search result or null if there are no results
   */
  MatchDocumentSummary findOne(QueryDefinition querydef);

  /**
   * The findOne method is a convenience.
   * It searches documents based on query criteria and, potentially, previously
   * saved query options. It returns the MatchDocumentSummary of the first
   * search result.
   * The search includes documents modified by the
   * transaction and ignores documents deleted by the transaction.
   * @param querydef	the definition of query criteria and query options
   * @param transaction	a open transaction for matching documents
   * @return the summary of the first search result or null if there are no results
   */
  MatchDocumentSummary findOne(QueryDefinition querydef, Transaction transaction);

  /**
   * Sends a query by example to the server to convert into a combined query
   * that expresses the criteria as a structured search.
   * @param query	the query by example
   * @param convertedHandle the container to use for the new converted query
   * @param <T> the type of StructureReadHandle to return
   * @return	the handle populated with the combined query
   */
  <T extends StructureReadHandle> T convert(RawQueryByExampleDefinition query, T convertedHandle);
  /**
   * Checks a query by example for mistakes in expressing the criteria.
   * @param query	the query by example
   * @param reportHandle	a handle for reading the validation report
   * @param <T> the type of StructureReadHandle to return
   * @return	the handle populated with the validation report
   */
  <T extends StructureReadHandle> T validate(RawQueryByExampleDefinition query, T reportHandle);

  /**
   * Starts debugging client requests. You can suspend and resume debugging output
   * using the methods of the logger.
   *
   * @param logger	the logger that receives debugging output
   */
  void startLogging(RequestLogger logger);

  /**
   *  Stops debugging client requests.
   */
  void stopLogging();

  /**
   * Returns a new CtsSearchBuilder.
   * @return a CtsQueryBuilder
   */
  CtsQueryBuilder newCtsSearchBuilder();

  /**
   * Defines a combined query from a JSON or XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param format	whether the format of the representation is JSON or XML
   * @param rawQuery	an IO representation of the JSON or XML combined query
   * @return a QueryDefinition for use of the combined query
   */
  RawCombinedQueryDefinition newRawCombinedQueryDefinitionAs(Format format, Object rawQuery);
  /**
   * Defines a combined query from a JSON or XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param format	whether the format of the representation is JSON or XML
   * @param rawQuery	an IO representation of the JSON or XML combined query
   * @param optionsName the name of a persisted query options configuration
   * @return a QueryDefinition for use of the combined query
   */
  RawCombinedQueryDefinition newRawCombinedQueryDefinitionAs(Format format, Object rawQuery, String optionsName);

  /**
   * Defines a combined query from a JSON or XML representation.
   * @param handle a handle for a JSON or XML combined query
   * @return a QueryDefinition for use of the combined query
   */
  RawCombinedQueryDefinition newRawCombinedQueryDefinition(StructureWriteHandle handle);
  /**
   * Defines a combined query from a JSON or XML representation.
   * @param handle a handle for a JSON or XML combined query
   * @param optionsName the name of a persisted query options configuration
   * @return a QueryDefinition for use of the combined query
   */
  RawCombinedQueryDefinition newRawCombinedQueryDefinition(StructureWriteHandle handle, String optionsName);

  /**
   * Defines a structured query from a JSON or XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param format	whether the format of the representation is JSON or XML
   * @param rawQuery	an IO representation of the JSON or XML structured query
   * @return a QueryDefinition for use of the structured query.
   */
  RawStructuredQueryDefinition newRawStructuredQueryDefinitionAs(Format format, Object rawQuery);
  /**
   * Defines a structured query from a JSON or XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param format	whether the format of the representation is JSON or XML
   * @param rawQuery	an IO representation of the JSON or XML structured query
   * @param optionsName the name of a persisted query options configuration
   * @return a QueryDefinition for use of the structured query.
   */
  RawStructuredQueryDefinition newRawStructuredQueryDefinitionAs(Format format, Object rawQuery, String optionsName);

  /**
   * Defines a structured query from a JSON or XML representation.
   * @param handle a handle for a JSON or XML structured query
   * @return a QueryDefinition for use of the structured query
   */
  RawStructuredQueryDefinition newRawStructuredQueryDefinition(StructureWriteHandle handle);
  /**
   * Defines a structured query from a JSON or XML representation.
   * @param handle a handle for a JSON or XML structured query
   * @param optionsName the name of a persisted query options configuration
   * @return a QueryDefinition for use of the structured query
   */
  RawStructuredQueryDefinition newRawStructuredQueryDefinition(StructureWriteHandle handle, String optionsName);

  /**
   * Defines a serialized cts query from a JSON or XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param format	whether the format of the representation is JSON or XML
   * @param rawQuery	an IO representation of the JSON or XML serialized cts query
   * @return a QueryDefinition for use of the serialized cts query.
   */
  RawCtsQueryDefinition newRawCtsQueryDefinitionAs(Format format, Object rawQuery);
  /**
   * Defines a serialized cts query from a JSON or XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param format	whether the format of the representation is JSON or XML
   * @param rawQuery	an IO representation of the JSON or XML serialized cts query
   * @param optionsName the name of a persisted query options configuration
   * @return a QueryDefinition for use of the serialized cts query.
   */
  RawCtsQueryDefinition newRawCtsQueryDefinitionAs(Format format, Object rawQuery, String optionsName);

  /**
   * Defines a serialized cts query from a JSON or XML representation.
   * @param handle a handle for a JSON or XML serialized cts query
   * @return a QueryDefinition for use of the serialized cts query
   */
  RawCtsQueryDefinition newRawCtsQueryDefinition(CtsQueryWriteHandle handle);
  /**
   * Defines a serialized cts query from a JSON or XML representation.
   * @param handle a handle for a JSON or XML serialized cts query
   * @param optionsName the name of a persisted query options configuration
   * @return a QueryDefinition for use of the serialized cts query
   */
  RawCtsQueryDefinition newRawCtsQueryDefinition(CtsQueryWriteHandle handle, String optionsName);

  /**
   * Defines a simple query by example from a JSON or XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param format	whether the format of the representation is JSON or XML
   * @param rawQuery	an IO representation of the query by example
   * @return a QueryDefinition for use of the query by example
   */
  RawQueryByExampleDefinition newRawQueryByExampleDefinitionAs(Format format, Object rawQuery);
  /**
   * Defines a simple query by example from a JSON or XML representation provided as an object of an IO class.
   *
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param format	whether the format of the representation is JSON or XML
   * @param rawQuery	an IO representation of the query by example
   * @param optionsName the name of a persisted query options configuration
   * @return a QueryDefinition for use of the query by example
   */
  RawQueryByExampleDefinition newRawQueryByExampleDefinitionAs(Format format, Object rawQuery, String optionsName);

  /**
   * Defines a simple query by example from a JSON or XML representation.
   * @param handle a handle for a JSON or XML query by example.
   * @return a QueryDefinition for use of the query by example.
   */
  RawQueryByExampleDefinition newRawQueryByExampleDefinition(StructureWriteHandle handle);
  /**
   * Defines a simple query by example from a JSON or XML representation.
   * @param handle a handle for a JSON or XML query by example.
   * @param optionsName The name of a persisted query options configuration
   * @return a QueryDefinition for use of the query by example.
   */
  RawQueryByExampleDefinition newRawQueryByExampleDefinition(StructureWriteHandle handle, String optionsName);
}
