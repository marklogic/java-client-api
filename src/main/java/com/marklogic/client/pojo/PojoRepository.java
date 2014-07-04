package com.marklogic.client.pojo;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.query.QueryDefinition;

import java.io.Serializable;

public interface PojoRepository<T, ID extends Serializable> {
    public void write(T entity);
    public void write(T entity, String... collections);
    public void write(T entity, Transaction transaction);
    public void write(T entity, Transaction transaction, String... collections);

    public boolean exists(ID id);

    public long count();
    public long count(String... collection);
    public long count(QueryDefinition query);
  
    public void delete(ID... ids);
    public void delete(String... collections);
  
    public T read(ID id);
    public T read(ID id, String... collections);
    public T read(ID id, Transaction transaction);
    public T read(ID id, Transaction transaction, String... collections);
    public PojoPage<T> read(ID... ids);
    public PojoPage<T> read(Transaction transaction, ID... ids);
    public PojoPage<T> read(long start);
    public PojoPage<T> read(long start, Transaction transaction);
 
    public PojoPage<T> search(long start, String... collections);
    public PojoPage<T> search(long start, Transaction transaction, String... collections);
    public PojoPage<T> search(QueryDefinition query, long start);
    public PojoPage<T> search(QueryDefinition query, long start, Transaction transaction);
    public PojoPage<T> search(QueryDefinition query, long start, SearchReadHandle searchHandle);
    public PojoPage<T> search(QueryDefinition query, long start, SearchReadHandle searchHandle, Transaction transaction);
 
    public PojoQueryBuilder getQueryBuilder();

    public long getPageLength(); // default: 50
    public void setPageLength(long length);
    
    public void defineIdField(String fieldName);
 
    public DatabaseClient getDatabaseClient();
}
