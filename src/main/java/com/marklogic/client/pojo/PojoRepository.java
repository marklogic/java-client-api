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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.SearchReadHandle;

import java.io.Serializable;

public interface PojoRepository<T, ID extends Serializable> {
    public void write(T entity);
    public void write(T entity, String... collections);
    public void write(T entity, Transaction transaction);
    public void write(T entity, Transaction transaction, String... collections);

    /** @return true if a document exists in the database with the id */
    public boolean exists(ID id);

    /** @return the number of documents of type T persisted in the database */
    public long count();

    /** @return the number of documents of type T persisted in the database with at least 
     * one of the criteria collections*/
    public long count(String... collection);

    /** @return the number of documents of type T persisted in the database which match
     * the query */
    public long count(PojoQueryDefinition query);
  
    /** Deletes from the database the documents with the corresponding ids */
    public void delete(ID... ids);

    /** Deletes from the database all documents of type T persisted by the pojo facade */
    public void deleteAll();

    /* REST API does not currently support DELETE /search with multiple collection arguments
    public void deleteAll(String... collections);
    */
  
    public T read(ID id);
    public T read(ID id, Transaction transaction);
    public PojoPage<T> read(ID[] ids);
    public PojoPage<T> read(ID[] ids, Transaction transaction);
    public PojoPage<T> readAll(long start);
    public PojoPage<T> readAll(long start, Transaction transaction);
 
    public PojoPage<T> search(long start, String... collections);
    public PojoPage<T> search(long start, Transaction transaction, String... collections);
    public PojoPage<T> search(PojoQueryDefinition query, long start);
    public PojoPage<T> search(PojoQueryDefinition query, long start, Transaction transaction);
    public PojoPage<T> search(PojoQueryDefinition query, long start, SearchReadHandle searchHandle);
    public PojoPage<T> search(PojoQueryDefinition query, long start, SearchReadHandle searchHandle, Transaction transaction);
 
    public PojoQueryBuilder<T> getQueryBuilder();

    public long getPageLength(); // default: 50
    public void setPageLength(long length);
    
    public void defineIdProperty(String fieldName);
 
    public DatabaseClient getDatabaseClient();
}
