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

import javax.xml.namespace.QName;

import com.marklogic.client.Transaction;
import com.marklogic.client.io.Format;
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
    static final public long DEFAULT_PAGE_LENGTH = -1;
    /**
     * The offset of the first result in the default page.
     */
    static final public long START = 1;

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
    public long getPageLength();

    /**
     * Specifies the maximum number of documents that can appear in any page of the query results,
     * overriding any maximum specified in the query options.
     * @param length	the maximum number of results
     */
    public void setPageLength(long length);

    /**
     * Returns the type of view results produced by queries.
     * @return	the view type for the queries
     */
    public QueryView getView();

    /**
     * Specifies the type of view results produced by queries.
     * @param view	the view type for the queries
     */
    public void setView(QueryView view);

    /**
     * Creates a query definition based on a string and the default
     * query options.  The string
     * has a simple grammar for specifying constraint values
     * for indexes and can be supplied by an end-user in a web form.
     * @return	the string query definition
     */
    public StringQueryDefinition newStringDefinition();

    /**
     * Creates a query definition based on a string and on named query options
     * saved previously.
     * @param optionsName	the name of the query options
     * @return	the string query definition
     */
    public StringQueryDefinition newStringDefinition(String optionsName);

    /**
     * @deprecated Use {@link RawQueryByExampleDefinition Query By Example} instead for easy-to-write and much more full-featured key/value search.
     * <br><br>
     *
     * Creates a query definition based on a locator such as a JSON key,
     * element name, or element and attribute name and the default query
     * options.
     * @return	the key-value query definition
     */
    @Deprecated
    public KeyValueQueryDefinition newKeyValueDefinition();

    /**
     * @deprecated Use {@link RawQueryByExampleDefinition Query By Example} instead for easy-to-write and much more full-featured key/value search.
     * <br><br>
     *
     * Creates a query definition based on a locator and on named
     * query options saved previously.
     * @param optionsName	the name of the query options
     * @return	the key-value query definition
     */
    @Deprecated
    public KeyValueQueryDefinition newKeyValueDefinition(String optionsName);

    /**
     * Creates a suggestion definition based on a single string for completion,
     * using the default options node.
     * @return	the suggest definition.
     */
    public SuggestDefinition newSuggestDefinition();
    
    /**
     * Creates a suggestion definition based on a query options name.
     * @param optionsName	the name of the query options
     * @return	the suggest definition.
     */
    public SuggestDefinition newSuggestDefinition(String optionsName);
    

    /**
     * Creates a suggestion definition based on a single string for completion,
     * using the default options node.
     * @param suggestString A string as input for completion suggestions.
     * @param optionsName	the name of the query options
     * @return	the suggest definition.
     */
    public SuggestDefinition newSuggestDefinition(String suggestString, String optionsName);
  
    /**
     * Creates a query definition based on a structure that identifies
     * clauses and conjunctions and the default query options.
     * @return	the structured query definition
     */
    public StructuredQueryBuilder newStructuredQueryBuilder();
    /**
     * Creates a query definition based on a structure and on named
     * query options saved previously.
     * @param optionsName	the name of the query options
     * @return	the structured query definition
     */
    public StructuredQueryBuilder newStructuredQueryBuilder(String optionsName);

    /**
     * Creates a query definition for deleting documents.
     * @return	the deletion query definition
     */
    public DeleteQueryDefinition newDeleteDefinition();

    /**
     * Creates a query definition for retrieving values based on
     * a named constraint on an index and the default query options.
     * @param name	the index constraint
     * @return	the values query definition
     */
    public ValuesDefinition newValuesDefinition(String name);

    /**
     * Creates a query definition for retrieving values based on
     * a named constraint and on named query options saved previously.
     * @param name	the index constraint
     * @param optionsName	the name of the query options
     * @return	the values query definition
     */
    public ValuesDefinition newValuesDefinition(String name, String optionsName);

    /**
     * Creates a query definition for retrieving the list of available
     * named lexicon configurations from the default query options.
     * @return the values list definition
     */
    public ValuesListDefinition newValuesListDefinition();

    /**
     * Creates a query definition for retrieving the list of available
     * named lexicon configurations from the named query options.
     * @param optionsName the name of the query options
     * @return the values list definition
     */
    public ValuesListDefinition newValuesListDefinition(String optionsName);

    /**
     * @deprecated Use {@link RawQueryByExampleDefinition Query By Example} instead for easy-to-write and much more full-featured key/value search.
     * <br><br>
     *
     * Creates a locator for a key-value query based on an element name,
     * which may have namespace.
     * @param element	the element name
     * @return	the locator for a key-value query
     */
    @Deprecated
    public ElementLocator newElementLocator(QName element);

    /**
     * @deprecated Use {@link RawQueryByExampleDefinition Query By Example} instead for easy-to-write and much more full-featured key/value search.
     * <br><br>
     *
     * Creates a locator for a key-value query based on an element name
     * and attribute name, either or both of which may have a namespace.
     * @param element	the element name
     * @param attribute	the attribute name
     * @return	the locator for a key-value query
     */
    @Deprecated
    public ElementLocator newElementLocator(QName element, QName attribute);

    /**
     * @deprecated Use {@link RawQueryByExampleDefinition Query By Example} instead for easy-to-write and much more full-featured key/value search.
     * <br><br>
     *
     * Creates a locator for a key-value query based on a JSON key.
     * @param key	the JSON key
     * @return	the locator for a key-value query
     */
    @Deprecated
    public KeyLocator newKeyLocator(String key);

    /**
     * Searches documents based on query criteria and, potentially, previously
     * saved query options.
     * @param querydef	the definition of query criteria and query options
     * @param searchHandle	a handle for reading the results from the search
     * @return	the handle populated with the results from the search
     */
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle);
    /**
     * Searches documents based on query criteria and, potentially, previously
     * saved query options starting with the specified page listing 
     * document results.
     * @param querydef	the definition of query criteria and query options
     * @param searchHandle	a handle for reading the results from the search
     * @param start	the offset of the first document in the page (where 1 is the first result)
     * @return	the handle populated with the results from the search
     */
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle, long start);
    /**
     * Searches documents based on query criteria and, potentially, previously
     * saved query options.  The search includes documents modified by the
     * transaction and ignores documents deleted by the transaction.
     * @param querydef	the definition of query criteria and query options
     * @param searchHandle	a handle for reading the results from the search
     * @param transaction	a open transaction for matching documents
     * @return	the handle populated with the results from the search
     */
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle, Transaction transaction);
    /**
     * Searches documents based on query criteria and, potentially, previously
     * saved query options starting with the specified page listing 
     * document results.  The search includes documents modified by the
     * transaction and ignores documents deleted by the transaction.
     * @param querydef	the definition of query criteria and query options
     * @param searchHandle	a handle for reading the results from the search
     * @param start	the offset of the first document in the page (where 1 is the first result)
     * @param transaction	a open transaction for matching documents
     * @return	the handle populated with the results from the search
     */
    public <T extends SearchReadHandle> T search(QueryDefinition querydef, T searchHandle, long start, Transaction transaction);

    /**
     * Queries the REST server for suggested string completions based on
     * values in the SuggestionDefinition.  The list of strings returned by
     * this function can be used to provide possible values for completing
     * a string search.
     */
    public String[] suggest(SuggestDefinition suggestionDef);
    
    /**
     * Deletes documents based on the query criteria.
     * @param querydef	the definition of query criteria
     */
    public void delete(DeleteQueryDefinition querydef);

    /**
     * Deletes documents based on the query criteria as part
     * of the specified transaction.
     * @param querydef	the definition of query criteria
     * @param transaction	a open transaction for the delete operation
     */
    public void delete(DeleteQueryDefinition querydef, Transaction transaction);

    /**
     * Retrieves values from indexes based on query criteria and, potentially,
     * previously saved query options.
     * @param valdef	the definition of query criteria and query options
     * @param valueHandle	a handle for reading the values for the matched documents
     * @return	the handle populated with the values from the index
     */
    public <T extends ValuesReadHandle> T values(ValuesDefinition valdef, T valueHandle);

    /**
     * Retrieves values from indexes based on query criteria and, potentially,
     * previously saved query options.
     * @param valdef	the definition of query criteria and query options
     * @param valueHandle	a handle for reading the values for the matched documents
     * @param start	the offset of the first returned result (where 1 is the first value)
     * @return	the handle populated with the values from the index
     */
    public <T extends ValuesReadHandle> T values(ValuesDefinition valdef, T valueHandle, long start);

    /**
     * Retrieves values from indexes based on query criteria and, potentially,
     * previously saved query options.  The query includes documents modified
     * by the transaction and ignores documents deleted by the transaction.
     * @param valdef	the definition of query criteria and query options
     * @param valueHandle	a handle for reading the values for the matched documents
     * @param transaction	a open transaction for matching documents
     * @return	the handle populated with the values from the index
     */
    public <T extends ValuesReadHandle> T values(ValuesDefinition valdef, T valueHandle, Transaction transaction);

    /**
     * Retrieves values from indexes based on query criteria and, potentially,
     * previously saved query options.  The query includes documents modified
     * by the transaction and ignores documents deleted by the transaction.
     * @param valdef	the definition of query criteria and query options
     * @param valueHandle	a handle for reading the values for the matched documents
     * @param start	the offset of the first returned result (where 1 is the first value)
     * @param transaction	a open transaction for matching documents
     * @return	the handle populated with the values from the index
     */
    public <T extends ValuesReadHandle> T values(ValuesDefinition valdef, T valueHandle, long start, Transaction transaction);

    /**
     * Retrieves combinations of values for the same document from indexes
     * based on query criteria and, potentially, previously saved query options.  
     * @param valdef	the definition of query criteria and query options
     * @param tupleHandle	a handle for reading the tuples for the matched documents
     * @return	the handle populated with the tuples from the index
     */
    public <T extends TuplesReadHandle> T tuples(ValuesDefinition valdef, T tupleHandle);

    /**
     * Retrieves combinations of values for the same document from indexes
     * based on query criteria and, potentially, previously saved query options.  
     * @param valdef	the definition of query criteria and query options
     * @param tupleHandle	a handle for reading the tuples for the matched documents
     * @param start	the offset of the first returned result (where 1 is the first tuple)
     * @return	the handle populated with the tuples from the index
     */
    public <T extends TuplesReadHandle> T tuples(ValuesDefinition valdef, T tupleHandle, long start);

    /**
     * Retrieves combinations of values for the same document from indexes
     * based on query criteria and, potentially, previously saved query options.
     * The query includes documents modified by the transaction and ignores
     * documents deleted by the transaction.  
     * @param valdef	the definition of query criteria and query options
     * @param tupleHandle	a handle for reading the tuples for the matched documents
     * @param transaction	a open transaction for matching documents
     * @return	the handle populated with the tuples from the index
     */
    public <T extends TuplesReadHandle> T tuples(ValuesDefinition valdef, T tupleHandle, Transaction transaction);

    /**
     * Retrieves combinations of values for the same document from indexes
     * based on query criteria and, potentially, previously saved query options.
     * The query includes documents modified by the transaction and ignores
     * documents deleted by the transaction.  
     * @param valdef	the definition of query criteria and query options
     * @param tupleHandle	a handle for reading the tuples for the matched documents
     * @param start	the offset of the first returned result (where 1 is the first tuple)
     * @param transaction	a open transaction for matching documents
     * @return	the handle populated with the tuples from the index
     */
    public <T extends TuplesReadHandle> T tuples(ValuesDefinition valdef, T tupleHandle, long start, Transaction transaction);

    /**
     * Retrieves the list of available named lexicon configurations from the
     * values list definition and, potentially, previously saved query options.
     * @param valdef the definition of the query criteria and options
     * @param valueHandle a handle for reading the list of names lexicon configurations
     * @return the handle populated with the names
     */
    public <T extends ValuesListReadHandle> T valuesList(ValuesListDefinition valdef, T valueHandle);

    /**
     * Retrieves the list of available named lexicon configurations from the
     * values list definition and, potentially, previously saved query options.
     * The query includes options modified by the transaction and ignores
     * options deleted by the transaction.
     * @param valdef the definition of the query criteria and options
     * @param valueHandle a handle for reading the list of names lexicon configurations
     * @param transaction	a open transaction for matching documents
     * @return the handle populated with the names
     */
    public <T extends ValuesListReadHandle> T valuesList(ValuesListDefinition valdef, T valueHandle, Transaction transaction);

    /**
     * Retrieves the list of available named query options.
     * @param listHandle a handle for reading the list of name options
     * @return the handle populated with the names
     */
    public <T extends QueryOptionsListReadHandle> T optionsList(T listHandle);

    /**
     * Retrieves the list of available named query options.
     * The query includes options modified by the transaction and ignores
     * options deleted by the transaction.
     * @param valueHandle a handle for reading the list of name options
     * @param transaction	a open transaction for matching documents
     * @return the handle populated with the names
     */
    public <T extends QueryOptionsListReadHandle> T optionsList(T valueHandle, Transaction transaction);

    /**
     * The findOne method is a convenience.
     * It searches documents based on query criteria and, potentially, previously
     * saved query options. It returns the MatchDocumentSummary of the first
     * search result.
     * @param querydef	the definition of query criteria and query options
     * @return the summary of the first search result or null if there are no results
     */
    public MatchDocumentSummary findOne(QueryDefinition querydef);

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
    public MatchDocumentSummary findOne(QueryDefinition querydef, Transaction transaction);

	/**
     * Converts a query by example into a combined query that expresses the criteria
     * as a structured search.
     * @param query	the query by example
     * @param convertedHandle
     * @return	the handle populated with the combined query
     */
    public <T extends StructureReadHandle> T convert(RawQueryByExampleDefinition query, T convertedHandle);
    /**
     * Checks a query by example for mistakes in expressing the criteria.
     * @param query	the query by example
     * @param reportHandle	a handle for reading the validation report 
     * @return	the handle populated with the validation report
     */
    public <T extends StructureReadHandle> T validate(RawQueryByExampleDefinition query, T reportHandle);

    /**
     * Starts debugging client requests. You can suspend and resume debugging output
     * using the methods of the logger.
     * 
     * @param logger	the logger that receives debugging output
     */
    public void startLogging(RequestLogger logger);

    /**
     *  Stops debugging client requests.
     */
    public void stopLogging();

	/**
     * Defines a combined query from a JSON or XML representation provided as an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param format	whether the format of the representation is JSON or XML
	 * @param rawQuery	an IO representation of the JSON or XML combined query
     * @return a QueryDefinition for use of the combined query
	 */
	public RawCombinedQueryDefinition newRawCombinedQueryDefinitionAs(Format format, Object rawQuery);
	/**
     * Defines a combined query from a JSON or XML representation provided as an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param format	whether the format of the representation is JSON or XML
	 * @param rawQuery	an IO representation of the JSON or XML combined query
     * @param optionsName the name of a persisted query options configuration
     * @return a QueryDefinition for use of the combined query
	 */
	public RawCombinedQueryDefinition newRawCombinedQueryDefinitionAs(Format format, Object rawQuery, String optionsName);

    /**
     * Defines a combined query from a JSON or XML representation.
     * @param handle a handle for a JSON or XML combined query
     * @return a QueryDefinition for use of the combined query
     */
	public RawCombinedQueryDefinition newRawCombinedQueryDefinition(StructureWriteHandle handle);
    /**
     * Defines a combined query from a JSON or XML representation.
     * @param handle a handle for a JSON or XML combined query
     * @param optionsName the name of a persisted query options configuration
     * @return a QueryDefinition for use of the combined query
     */
	public RawCombinedQueryDefinition newRawCombinedQueryDefinition(StructureWriteHandle handle, String optionsName);

	/**
     * Defines a structured query from a JSON or XML representation provided as an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param format	whether the format of the representation is JSON or XML
	 * @param rawQuery	an IO representation of the JSON or XML structured query
     * @return a QueryDefinition for use of the structured query.
	 */
	public RawStructuredQueryDefinition newRawStructuredQueryDefinitionAs(Format format, Object rawQuery);
	/**
     * Defines a structured query from a JSON or XML representation provided as an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param format	whether the format of the representation is JSON or XML
	 * @param rawQuery	an IO representation of the JSON or XML structured query
     * @param optionsName the name of a persisted query options configuration
     * @return a QueryDefinition for use of the structured query.
	 */
	public RawStructuredQueryDefinition newRawStructuredQueryDefinitionAs(Format format, Object rawQuery, String optionsName);

	/**
     * Defines a structured query from a JSON or XML representation.
     * @param handle a handle for a JSON or XML structured query
     * @return a QueryDefinition for use of the structured query
     */
	public RawStructuredQueryDefinition newRawStructuredQueryDefinition(StructureWriteHandle handle);
    /**
     * Defines a structured query from a JSON or XML representation.
     * @param handle a handle for a JSON or XML structured query
     * @param optionsName the name of a persisted query options configuration
     * @return a QueryDefinition for use of the structured query
     */
	public RawStructuredQueryDefinition newRawStructuredQueryDefinition(StructureWriteHandle handle, String optionsName);

	/**
     * Defines a simple query by example from a JSON or XML representation provided as an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param format	whether the format of the representation is JSON or XML
	 * @param rawQuery	an IO representation of the query by example
     * @return a QueryDefinition for use of the query by example
	 */
	public RawQueryByExampleDefinition newRawQueryByExampleDefinitionAs(Format format, Object rawQuery);
	/**
     * Defines a simple query by example from a JSON or XML representation provided as an object of an IO class.
     * 
     * The IO class must have been registered before creating the database client.
     * By default, standard Java IO classes for document content are registered.
     * 
     * @param format	whether the format of the representation is JSON or XML
	 * @param rawQuery	an IO representation of the query by example
     * @param optionsName the name of a persisted query options configuration
     * @return a QueryDefinition for use of the query by example
	 */
	public RawQueryByExampleDefinition newRawQueryByExampleDefinitionAs(Format format, Object rawQuery, String optionsName);

    /**
     * Defines a simple query by example from a JSON or XML representation.
     * @param handle a handle for a JSON or XML query by example.
     * @return a QueryDefinition for use of the query by example.
     */
	public RawQueryByExampleDefinition newRawQueryByExampleDefinition(StructureWriteHandle handle);
    /**
     * Defines a simple query by example from a JSON or XML representation.
     * @param handle a handle for a JSON or XML query by example.
     * @param optionsName The name of a persisted query options configuration
     * @return a QueryDefinition for use of the query by example.
     */
	public RawQueryByExampleDefinition newRawQueryByExampleDefinition(StructureWriteHandle handle, String optionsName);
}
