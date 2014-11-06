/*
 * Copyright 2012-2014 MarkLogic Corporation
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
 *        client.newPojoRepository(MyClass.class, Integer);</pre>
 *
 * Where MyClass is your custom pojo type, and myId is the bean property of type Integer
 * marked with the 
 * {@literal @}{@link com.marklogic.client.pojo.annotation.Id Id annotation}.  The 
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
 */
public interface PojoRepository<T, ID extends Serializable> {
    /** Write this instance to the database.  Uses the field marked with {@literal @}Id 
     * annotation to generate a unique uri for the document.  Adds a collection with the 
     * fully qualified class name.  Uses a particular configuration of 
     *  {@link com.fasterxml.jackson.databind.ObjectMapper ObjectMapper} to generate the
     *  serialized JSON format.
     */
    public void write(T entity)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /** Does everything in {@link #write(Object) write(T)} but also adds your collections to the 
     * persisted instance.
     */
    public void write(T entity, String... collections)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /** Does everything in {@link #write(Object) write(T)} but in your 
     * <a href="http://docs.marklogic.com/guide/app-dev/transactions">
     * multi-statement transaction</a> context.
     */
    public void write(T entity, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /** Does everything in {@link #write(Object) write(T)} but also adds your collections to the 
     * persisted instance and performs the write in your
     * <a href="http://docs.marklogic.com/guide/app-dev/transactions">
     * multi-statement transaction</a> context.
     * .
     */
    public void write(T entity, Transaction transaction, String... collections)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** @return true if a document exists in the database with the id */
    public boolean exists(ID id)
        throws ForbiddenUserException, FailedRequestException;

    /** @return in the context of transaction, true if a document exists in the database with 
     * the id */
    public boolean exists(ID id, Transaction transaction)
        throws ForbiddenUserException, FailedRequestException;

    /** @return the number of documents of type T persisted in the database */
    public long count()
        throws ForbiddenUserException, FailedRequestException;

    /** @return in the context of transaction, the number of documents of type T persisted in 
     * the database */
    public long count(Transaction transaction)
        throws ForbiddenUserException, FailedRequestException;

    /** @return the number of documents of type T persisted in the database with at least 
     * one of the criteria collections*/
    public long count(String... collection)
        throws ForbiddenUserException, FailedRequestException;

    /** @return in the context of transaction, the number of documents of type T persisted in 
     * the database with at least one of the criteria collections*/
    public long count(String[] collections, Transaction transaction)
        throws ForbiddenUserException, FailedRequestException;

    /** @return the number of documents of type T persisted in the database which match
     * the query */
    public long count(PojoQueryDefinition query)
        throws ForbiddenUserException, FailedRequestException;
  
    /** @return in the context of transaction, the number of documents of type T persisted in the 
     * database which match the query */
    public long count(PojoQueryDefinition query, Transaction transaction)
        throws ForbiddenUserException, FailedRequestException;
  
    /** Deletes from the database the documents with the corresponding ids */
    public void delete(ID... ids)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** As part of transaction, deletes from the database the documents with the corresponding ids */
    public void delete(ID[] ids, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** Deletes from the database all documents of type T persisted by the pojo facade */
    public void deleteAll()
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /** As part of transaction, deletes from the database all documents of type T persisted by 
     * the pojo facade */
    public void deleteAll(Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    /* REST API does not currently support DELETE /search with multiple collection arguments
    public void deleteAll(String... collections)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    */
  
    public T read(ID id)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public T read(ID id, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public PojoPage<T> read(ID[] ids)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public PojoPage<T> read(ID[] ids, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public PojoPage<T> readAll(long start)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public PojoPage<T> readAll(long start, Transaction transaction)
        throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
 
    public PojoPage<T> search(long start, String... collections)
        throws ForbiddenUserException, FailedRequestException;
    public PojoPage<T> search(long start, Transaction transaction, String... collections)
        throws ForbiddenUserException, FailedRequestException;
    public PojoPage<T> search(PojoQueryDefinition query, long start)
        throws ForbiddenUserException, FailedRequestException;
    public PojoPage<T> search(PojoQueryDefinition query, long start, Transaction transaction)
        throws ForbiddenUserException, FailedRequestException;
    public PojoPage<T> search(PojoQueryDefinition query, long start, SearchReadHandle searchHandle)
        throws ForbiddenUserException, FailedRequestException;
    public PojoPage<T> search(PojoQueryDefinition query, long start, SearchReadHandle searchHandle, Transaction transaction)
        throws ForbiddenUserException, FailedRequestException;
 
    public PojoQueryBuilder<T> getQueryBuilder();

    public long getPageLength(); // default: 50
    public void setPageLength(long length);
}
