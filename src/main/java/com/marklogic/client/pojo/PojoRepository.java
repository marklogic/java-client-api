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
package com.marklogic.client.pojo;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.query.QueryManager;

import java.io.Serializable;

/** PojoRepository is the central class for the Pojo Facade.  It supports CRUD operations
 * and search.  Each PojoRepository instance operates on only one pojo class.  Create new
 * PojoRepository instances based on your custom pojo type like so:
 * <pre>    public class MyClass {
 *        {@literal @}Id
 *        public Integer getMyId() {
 *            ...
 *        }
 *        public void setMyId(Integer integer) {
 *            ...
 *        }
 *        ...
 *    }
 *    ...
 *    DatabaseClient client = ...;
 *    PojoRepository<MyClass, Integer> myClassRepo = 
 *        client.newPojoRepository(MyClass.class, Integer.class);</pre>
 *
 * Where MyClass is your custom pojo type, and myId is the bean property of type Integer
 * marked with the 
 * {@literal @}{@link Id Id annotation}.  The 
 * {@literal @}Id annotaiton can be attached to a public field or a public getter or a 
 * public setter.  The bean property marked with {@literal @}Id must be a native type or 
 * {@link java.io.Serializable} class and must contain an 
 * identifier value unique across all persisted instances of that
 * type or the instance will overwrite the persisted instance with the same identifier.
 *
 * The current implementation of the Pojo Facade uses 
 * <a href="https://github.com/FasterXML/jackson-databind/">Jackson databind</a> for serialization
 * and deserialization to json.  Thus only classes which can be serialized and deserialized 
 * directly by Jackson can be serialized by the Pojo Facade.  Every bean property
 * including the one marked with {@literal @}Id must either expose a public field or both a public 
 * getter and a public setter.  To test if your class can be directly serialized and 
 * deserialized by Jackson, perform the following:
 * <pre>    ObjectMapper objectMapper = new ObjectMapper();
 *    String value = objectMapper.writeValueAsString(myObjectIn);
 *    MyClass myObjectOut = objectMapper.readValue(value, MyClass.class);</pre>
 *
 * If that works but the configured objectMapper in the Pojo Facade is different and not
 * working, you can troubleshoot by directly accessing the objectMapper used by the Pojo 
 * Facade using an unsupported internal method attached to the current implementataion: 
 * <a 
 * href="https://github.com/marklogic/java-client-api/blob/master/src/main/java/com/marklogic/client/impl/PojoRepositoryImpl.java"
 * >com.marklogic.client.impl.PojoRepositoryImpl</a>.
 * <pre>    ObjectMapper objectMapper = ((PojoRepositoryImpl) myClassRepo).getObjectMapper();</pre>
 *
 * If your class has properties which are classes (non-native types) they will be automatically 
 * serialized and deserialized, but cannot be written, read, or searched directly.  If you 
 * wish to directly write, read, or search another class, create a new instance of 
 * PojoRepository specific to that class.
 *
 * Since PojoRepository stores in JSON format, which limits number precision to 15
 * significant digits (IEEE754 double precision), you will lose precision on numbers
 * longer than 15 significant digits.  If you desire larger numbers with no loss of
 * precision, use Strings to persist those numbers.
 */
public interface PojoRepository<T, ID extends Serializable> {
    /** Write this instance to the database.  Uses the field marked with {@literal @}Id 
     * annotation to generate a unique uri for the document.  Adds a collection with the 
     * fully qualified class name.  Uses a particular configuration of 
     * {@link com.fasterxml.jackson.databind.ObjectMapper ObjectMapper} to generate the
     * serialized JSON format.
     * @param entity your pojo instance of the type managed by this PojoRepository
     */
    public void write(T entity)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /** Does everything in {@link #write(Object) write(T)} but also adds your collections to the 
     * persisted instance.
     * @param entity your pojo instance of the type managed by this PojoRepository
     * @param collections the collections to add to this instance in the database
     */
    public void write(T entity, String... collections)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /** Does everything in {@link #write(Object) write(T)} but in your 
     * <a href="http://docs.marklogic.com/guide/app-dev/transactions">
     * multi-statement transaction</a> context.
     * @param entity your pojo instance of the type managed by this PojoRepository
     * @param transaction the open transaction in which to write this instance
     */
    public void write(T entity, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /** Does everything in {@link #write(Object) write(T)} but also adds your
     * collections to the  persisted instance and performs the write in your
     * <a href="http://docs.marklogic.com/guide/app-dev/transactions">
     * multi-statement transaction</a> context.
     * .
     * @param entity your pojo instance of the type managed by this PojoRepository
     * @param transaction the open transaction in which to write this instance
     * @param collections the collections to add to this instance in the database
     */
    public void write(T entity, Transaction transaction, String... collections)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** True if a document exists in the database with the specified id
     * @param id the unique identifier of the pojo (the value of the field annotated with
     *      {@literal @}{@link Id Id})
     * @return true if a document exists in the database with the specified id
     */
    public boolean exists(ID id)
        throws ForbiddenUserException, FailedRequestException;

    /** True if in the context of transaction, a document exists in the database with
     * the specified id
     * @param id the unique identifier of the pojo (the value of the field annotated with
     *      {@literal @}{@link Id Id})
     * @param transaction the transaction in which this exists check is participating
     *      (Will open a read lock on the document. The read lock is released when the
     *      transaction is committed or rolled back.)
     * @return true if in the context of transaction, a document exists in the database
     *      with the specified id
     */
    public boolean exists(ID id, Transaction transaction)
        throws ForbiddenUserException, FailedRequestException;

    /** The number of documents of the type managed by this PojoRepository persisted
     * in the database
     * @return The number of documents of the type
     *      managed by this PojoRepository persisted in the database
     */
    public long count()
        throws ForbiddenUserException, FailedRequestException;

    /** In the context of transaction, the number of documents of the type managed by
     * this PojoRepository persisted in the database
     * @param transaction the transaction in which this count is participating
     *      (Will open a read lock on all matched documents. The read lock is released when the
     *      transaction is committed or rolled back.)
     * @return in the context of transaction, the number of documents of the type managed
     *      by this PojoRepository persisted in the database
     */
    public long count(Transaction transaction)
        throws ForbiddenUserException, FailedRequestException;

    /** The number of documents of the type managed by this PojoRepository persisted in
     * the database with at least one of the criteria collections
     * @param collections matches must belong to at least one of the specified collections
     * @return the number of documents of the type managed
     *      by this PojoRepository persisted in the database with at least one of the
     *      criteria collections
     */
    public long count(String... collections)
        throws ForbiddenUserException, FailedRequestException;

    /** In the context of transaction, the number of documents of the type managed by
     * this PojoRepository persisted in the database with at least one of the criteria
     * collections
     * @param collections matches must belong to at least one of the specified collections
     * @param transaction the transaction in which this count is participating
     *      (Will open a read lock on all matched documents. The read lock is released when the
     *      transaction is committed or rolled back.)
     * @return in the context of transaction, the number of documents of the type managed
     *      by this PojoRepository persisted in the database with at least one of the
     *      criteria collections
     */
    public long count(String[] collections, Transaction transaction)
        throws ForbiddenUserException, FailedRequestException;

    /** @return the number of documents of the type managed by this PojoRepository persisted in the database which match
     * the query */
    public long count(PojoQueryDefinition query)
        throws ForbiddenUserException, FailedRequestException;
  
    /** In the context of transaction, the number of documents of the type managed by
     * this PojoRepository persisted in the database which match the query
     * @param query the query which results much match (queries are run unfiltered by default)
     * @param transaction the transaction in which this count is participating
     *      (Will open a read lock on all matched documents. The read lock is released when the
     *      transaction is committed or rolled back.)
     * @return in the context of transaction, the number of documents of the type managed
     *      by this PojoRepository persisted in the database which match the query
     */
    public long count(PojoQueryDefinition query, Transaction transaction)
        throws ForbiddenUserException, FailedRequestException;
  
    /** Deletes from the database the documents with the corresponding ids */
    public void delete(ID... ids)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** As part of transaction, deletes from the database the documents with the corresponding ids */
    public void delete(ID[] ids, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** Deletes from the database all documents of the type managed by this PojoRepositoryof type T persisted by the pojo facade */
    public void deleteAll()
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** As part of transaction, deletes from the database all documents of the type managed by this PojoRepositoryof type T persisted by 
     * the pojo facade */
    public void deleteAll(Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /* REST API does not currently support DELETE /search with multiple collection arguments
    public void deleteAll(String... collections)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    */

    /** Read one persisted pojo by id and unmarshall its data into a new pojo instance.
     * @param id the unique identifier of the pojo (the value of the field annotated with
     *      {@literal @}{@link Id Id})
     * @return an instance of the correct type populated with the persisted data
     *      from the database
     */
    public T read(ID id)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /** Within an open transaction, read one persisted pojo by id and unmarshall its data
     * into a new pojo instance.
     * @param id the unique identifier of the pojo (the value of the field annotated with
     *      {@literal @}{@link Id Id})
     * @param transaction the transaction in which this read is participating
     *      (will open a read lock on each document matched that is released when the
     *      transaction is committed or rolled back)
     * @return an instance of the correct type populated with the persisted data
     *      from the database
     */
    public T read(ID id, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /** Read multiple persisted pojos by id and unmarshall their data into new pojo instances.
     * If at least one instance is found but others are not, ignores the instances not found.
     * While this returns a PojoPage, the PageSize will match the number of instances found,
     * and will ignore getPageLength().  To paginate, send a smaller set of ids at a time.
     * @param ids the unique identifiers of the pojos (the values of the field annotated with
     *      {@literal @}{@link Id Id})
     * @return a set of instances of the correct type populated with the persisted data.
     *      Since this call produces a finite set, only one page is returned and therefore
     *      PojoPage pagination methods will not be helpful as they would be from calls to search.
     */
    public PojoPage<T> read(ID[] ids)
        throws ForbiddenUserException, FailedRequestException;
    /** Within an open transaction,
     * read multiple persisted pojos and unmarshall their data into new pojo instances.
     * If at least one instance is found but others are not, ignores the instances not found.
     * While this returns a PojoPage, the PageSize will match the number of instances found,
     * and will ignore getPageLength().  To paginate, send a smaller set of ids at a time.
     * @param ids the unique identifiers of the pojos (the values of the field annotated with
     *      {@literal @}{@link Id Id})
     * @param transaction the transaction in which this read is participating
     *      (will open a read lock on each document matched that is released when the
     *      transaction is committed or rolled back)
     * @return a set of instances of the correct type populated with the persisted data.
     *      Since this call produces a finite set, only one page is returned and therefore
     *      PojoPage pagination methods will not be helpful as they would be from calls to search.
     */
    public PojoPage<T> read(ID[] ids, Transaction transaction)
        throws ForbiddenUserException, FailedRequestException;
    /** Read one page of persisted pojos of the type managed by this
     * PojoRepository and unmarshall their data into new pojo instances.
     * @param start the offset of the first document in the page (where 1 is the first result)
     * @return a page with a maximum of {@link #getPageLength()} instances of the correct
     *      type populated with the persisted data.
     *      Since this call may match a large set, only one page of {@link #getPageLength()}
     *      is returned just like calls to {@link #search(PojoQueryDefinition, long) search}.
     */    
    public PojoPage<T> readAll(long start)
        throws ForbiddenUserException, FailedRequestException;
    /** Within an open transaction, read one page of persisted pojos of the type managed by this
     * PojoRepository and unmarshall their data into new pojo instances.
     * @param start the offset of the first document in the page (where 1 is the first result)
     * @param transaction the transaction in which this read is participating
     *      (Will open a read lock on each document matched. The read lock is released when the
     *      transaction is committed or rolled back.)
     * @return a page with a maximum of {@link #getPageLength()} instances of the correct
     *      type populated with the persisted data.
     *      Since this call may match a large set, only one page of {@link #getPageLength()}
     *      is returned just like calls to {@link #search(PojoQueryDefinition, long) search}.
     */
    public PojoPage<T> readAll(long start, Transaction transaction)
        throws ForbiddenUserException, FailedRequestException;

    /** Find all persisted pojos of the type managed by this
     * PojoRepository also in one of the specified collections and unmarshall their data
     * into new pojo instances.
     * @param start the offset of the first document in the page (where 1 is the first result)
     * @param collections matches must belong to at least one of the specified collections
     * @return a page with a maximum of {@link #getPageLength()} instances of the correct
     *      type populated with the persisted data.
     *      Since this call may match a large set, only one page of {@link #getPageLength()}
     *      is returned and the total estimated number of results is available from
     *      {@link PojoPage#getTotalSize() PojoPage.getTotalSize()}.
     */
    public PojoPage<T> search(long start, String... collections)
        throws ForbiddenUserException, FailedRequestException;
    /** Within an open transaction, find all persisted pojos of the type managed by this
     * PojoRepository also in one of the specified collections and unmarshall their data
     * into new pojo instances.
     * @param start the offset of the first document in the page (where 1 is the first result)
     * @param transaction the transaction in which this search is participating
     *      (Will open a read lock on each document matched. The read lock is released when the
     *      transaction is committed or rolled back.)
     * @param collections matches must belong to at least one of the specified collections
     * @return a page with a maximum of {@link #getPageLength()} instances of the correct
     *      type populated with the persisted data.
     *      Since this call may match a large set, only one page of {@link #getPageLength()}
     *      is returned and the total estimated number of results is available from
     *      {@link PojoPage#getTotalSize() PojoPage.getTotalSize()}.
     */
    public PojoPage<T> search(long start, Transaction transaction, String... collections)
        throws ForbiddenUserException, FailedRequestException;
    /** Within an open transaction, search persisted pojos of the type managed by this
     * PojoRepository for matches to this query and unmarshall their data into new pojo instances.
     * If matches are returned which do not meet all the criteria, you may need to create
     * appropriate indexes in the server to run your query unfiltered or run your query filtered by
     * wrapping your query with {@link PojoQueryBuilder#filteredQuery filteredQuery}.
     * @param query the query which results much match (queries are run unfiltered by default)
     * @param start the offset of the first document in the page (where 1 is the first result)
     * @return a page with a maximum of {@link #getPageLength()} instances of the correct
     *      type populated with the persisted data.
     *      Since this call may match a large set, only one page of {@link #getPageLength()}
     *      is returned and the total estimated number of results is available from
     *      {@link PojoPage#getTotalSize() PojoPage.getTotalSize()}.
     */
    public PojoPage<T> search(PojoQueryDefinition query, long start)
        throws ForbiddenUserException, FailedRequestException;
    /** Within an open transaction, search persisted pojos of the type managed by this
     * PojoRepository for matches to this query and unmarshall their data into new pojo instances.
     * If matches are returned which do not meet all the criteria, you may need to create
     * appropriate indexes in the server to run your query unfiltered or run your query filtered by
     * wrapping your query with {@link PojoQueryBuilder#filteredQuery filteredQuery}.
     * @param query the query which results much match (queries are run unfiltered by default)
     * @param start the offset of the first document in the page (where 1 is the first result)
     * @param transaction the transaction in which this search is participating
     *      (Will open a read lock on each document matched. The read lock is released when the
     *      transaction is committed or rolled back.)
     * @return a page with a maximum of {@link #getPageLength()} instances of the correct
     *      type populated with the persisted data.
     *      Since this call may match a large set, only one page of {@link #getPageLength()}
     *      is returned and the total estimated number of results is available from
     *      {@link PojoPage#getTotalSize() PojoPage.getTotalSize()}.
     */
    public PojoPage<T> search(PojoQueryDefinition query, long start, Transaction transaction)
        throws ForbiddenUserException, FailedRequestException;
    public PojoPage<T> search(PojoQueryDefinition query, long start, SearchReadHandle searchHandle)
        throws ForbiddenUserException, FailedRequestException;
    /** Within an open transaction, search persisted pojos of the type managed by this
     * PojoRepository for matches to this query and unmarshall their data into new pojo instances.
     * If matches are returned which do not meet all the criteria, you may need to create
     * appropriate indexes in the server to run your query unfiltered or run your query filtered by
     * wrapping your query with {@link PojoQueryBuilder#filteredQuery filteredQuery}.
     * @param query the query which results much match (queries are run unfiltered by default)
     * @param start the offset of the first document in the page (where 1 is the first result)
     * @param searchHandle the handle to populate with a search results payload equivalent to
     *      one returned by
     *      {@link QueryManager#search(QueryDefinition, SearchReadHandle, long, Transaction)
     *      QueryManager.search}
     * @param transaction the transaction in which this search is participating
     *      (Will open a read lock on each document matched. The read lock is released when the
     *      transaction is committed or rolled back.)
     * @return a page with a maximum of {@link #getPageLength()} instances of the correct
     *      type populated with the persisted data.
     *      Since this call may match a large set, only one page of {@link #getPageLength()}
     *      is returned and the total estimated number of results is available from
     *      {@link PojoPage#getTotalSize() PojoPage.getTotalSize()}.
     */
    public PojoPage<T> search(PojoQueryDefinition query, long start, SearchReadHandle searchHandle, Transaction transaction)
        throws ForbiddenUserException, FailedRequestException;

    /*
     * Get the generated uri for this pojo.  This can be helpful if you need to know
     * the location of the persisted pojo in the server.  This method does not contact
     * the server, it just returns the uri it would generate with {@link #write write}.
     * @return the uri generated by PojoRepository when writing this pojo
     */
    public String getDocumentUri(T pojo);
 
    /** Get a PojoQueryBuilder for the type managed by this PojoRepository.
     * @return a PojoQueryBuilder for the type managed by this PojoRepository
     */
    public PojoQueryBuilder<T> getQueryBuilder();

    /** The number of instances per page returned when calling {@link #readAll readAll} or
     * {@link #search search} (Default: 50).
     */
    public long getPageLength();
    /** Set the number of instances per page returned when calling {@link #readAll readAll} or
     * {@link #search search}.
     */
    public void setPageLength(long length);
}
