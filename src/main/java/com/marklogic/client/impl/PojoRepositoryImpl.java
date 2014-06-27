package com.marklogic.client.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonPojoHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.query.StructuredQueryDefinition;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PojoRepositoryImpl<T, ID extends Serializable>
    implements PojoRepository<T, ID>
{
    private final String EXTENSION = ".json";

    private DatabaseClient client;
    private Class<T> entityClass;
    private Class<ID> idClass;
    private JSONDocumentManager docMgr;
    private PojoQueryBuilder<T> qb;
    private Method idMethod;
    private Field idField;
    private String idFieldName;

    public  PojoRepositoryImpl(DatabaseClient client, Class<T> entityClass, Class<ID> idClass) {
        this.client = client;
        this.entityClass = entityClass;
        this.idClass = idClass;
        this.docMgr = client.newJSONDocumentManager();
        this.qb = new PojoQueryBuilderImpl<T>(entityClass);
        findId();
        if ( idMethod == null && idField == null ) {
            throw new IllegalArgumentException("Your class " + entityClass.getName() +
                " does not have a method or field annotated with com.marklogic.client.pojo.annotation.Id");
        }
    }

    public void write(T entity) {
        write(entity, null, null);
    }
    public void write(T entity, String... collections) {
        write(entity, null, collections);
    }
    public void write(T entity, Transaction transaction) {
        write(entity, transaction, null);
    }
    public void write(T entity, Transaction transaction, String... collections) {
        if ( entity == null ) return;
        JacksonPojoHandle contentHandle = new JacksonPojoHandle(entity);
        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
        metadataHandle = metadataHandle.withCollections(entityClass.getName());
        if ( collections != null && collections.length > 0 ) {
            metadataHandle = metadataHandle.withCollections(collections);
        }
        DocumentWriteSet writeSet = docMgr.newWriteSet();
        writeSet.add(createUri(entity), metadataHandle, contentHandle);
        docMgr.write(writeSet, transaction);
    }

    public boolean exists(ID id) {
        return docMgr.exists(createUri(id)) != null;
    }

    public long count() {
        return count((String) null);
    }

    public long count(String... collections) {
        if ( collections == null ) return 0l;
        return count(qb.collection(collections));
    }
    public long count(QueryDefinition query) {
        long pageLength = getPageLength();
        setPageLength(0);
        DocumentPage page = docMgr.search(wrapQuery(query), 1);
        setPageLength(pageLength);
        return page.getTotalSize();
    }
  
    public void delete(ID... ids) {
    }
    public void delete(String... collections) {
    }
  
    public T read(ID id) {
        return read(id, null, null);
    }
    public T read(ID id, String... collections) {
        return read(id, null, collections);
    }
    public T read(ID id, Transaction transaction) {
        return read(id, transaction, null);
    }
    public T read(ID id, Transaction transaction, String... collections) {
        PojoPage<T> page = read(transaction, collections, id);
        if ( page == null ) return null;
        Iterator<T> iterator = page.iterator();
        if ( iterator.hasNext() ) return iterator.next();
        return null;
    }
    public PojoPage<T> read(ID... ids) {
        return read(null, null, ids);
    }
    public PojoPage<T> read(Transaction transaction, ID... ids) {
        return read(transaction, null, ids);
    }
    public PojoPage<T> read(Transaction transaction, String[] collections, ID... ids) {
        long pageLength = getPageLength();
        QueryDefinition query = null;
        if ( ids != null ) {
            long tempPageLength = pageLength;
            tempPageLength = ids.length;
            setPageLength(tempPageLength);
            if ( ids.length == 1 ) {
                query = qb.value(idFieldName, String.valueOf(ids[0]));
            } else {
                ArrayList<StructuredQueryDefinition> idQueries =
                    new ArrayList<StructuredQueryDefinition>(ids.length);
                for ( ID id : ids ) {
                    idQueries.add( qb.value(idFieldName, String.valueOf(id)) );
                }
                query = qb.and( idQueries.toArray(new StructuredQueryDefinition[0]));
            }
            if ( collections != null ) query.setCollections(collections);
        } else {
            if ( collections != null ) query = qb.collection(collections);
        }
        PojoPage page = search(wrapQuery(query), 1, null, transaction);
        setPageLength(pageLength);
        return page;
    }
    public PojoPage<T> read(long start) {
        return search(null, start, null, null);
    }
    public PojoPage<T> read(long start, Transaction transaction) {
        return search(null, start, null, transaction);
    }

    public PojoPage<T> search(long start, String... collections) {
        return search(qb.collection(collections), start, null, null);
    }
    public PojoPage<T> search(long start, Transaction transaction, String... collections) {
        return search(qb.collection(collections), start, null, transaction);
    }

    public PojoPage<T> search(QueryDefinition query, long start) {
        return search(query, start, null, null);
    }
    public PojoPage<T> search(QueryDefinition query, long start, Transaction transaction) {
        return search(query, start, null, transaction);
    }
    public PojoPage<T> search(QueryDefinition query, long start, SearchReadHandle searchHandle) {
        return search(query, start, searchHandle, null);
    }
    public PojoPage<T> search(QueryDefinition query, long start, SearchReadHandle searchHandle, Transaction transaction) {
        Format docMgrFormat = docMgr.getResponseFormat();
        if ( searchHandle != null ) {
            HandleImplementation searchBase = HandleAccessor.checkHandle(searchHandle, "search");
            if (searchHandle instanceof SearchHandle) {
                SearchHandle responseHandle = (SearchHandle) searchHandle;
                if ( docMgr instanceof DocumentManagerImpl ) {
                    responseHandle.setHandleRegistry(((DocumentManagerImpl) docMgr).getHandleRegistry());
                }
                responseHandle.setQueryCriteria(query);
            }
            docMgr.setResponseFormat(searchBase.getFormat());
        }

        String tid = transaction == null ? null : transaction.getTransactionId();
        // we don't need any metadata
        Set metadata = null;
        DocumentPage docPage = docMgr.search(wrapQuery(query), start, searchHandle, transaction);
        docMgr.setResponseFormat(docMgrFormat);
        PojoPage<T> pojoPage = new PojoPageImpl(docPage, entityClass);
        return pojoPage;
    }
 
    public long getPageLength() {
        return docMgr.getPageLength();
    }
    public void setPageLength(long length) {
        docMgr.setPageLength(length);
    }
    
    public QueryView getSearchView() {
        return docMgr.getSearchView();
    }

    public void setSearchView(QueryView view) {
        docMgr.setSearchView(view);
    }

    public void defineIdField(String fieldName) {
    }
 
    public DatabaseClient getDatabaseClient() {
        return client;
    }

    private QueryDefinition wrapQuery(QueryDefinition query) {
        if ( query == null ) {
            return qb.collection(entityClass.getName());
        } else {
            List<String> collections = Arrays.asList(query.getCollections());
            HashSet<String> collectionSet = new HashSet<String>(collections);
            collectionSet.add(entityClass.getName());
            query.setCollections(collectionSet.toArray(new String[0]));
            return query;
        }
    }

    private String createUri(T entity) {
        return createUri(getId(entity));
    }

    private String createUri(ID id) {
        if ( id == null ) {
            throw new IllegalStateException("id cannot be null");
        }
        try {
            return entityClass.getName() + "/" + URLEncoder.encode(id.toString(), "UTF-8") + EXTENSION;
        } catch (UnsupportedEncodingException e) {
            throw new MarkLogicInternalException(e);
        }
    }

    private void findId() {
        if ( idMethod == null && idField == null ) {
            for ( Method method : entityClass.getDeclaredMethods() ) {
                if ( method.isAnnotationPresent(Id.class) ) {
                    Class[] parameters = method.getParameterTypes();
                    if ( ! Modifier.isPublic(method.getModifiers()) ) {
                        throw new IllegalStateException("Your getter method, " + method.getName() +
                            ", annotated with com.marklogic.client.pojo.annotation.Id " + 
                            " must be public");
                    }
                    if ( parameters == null || parameters.length == 0 ) {
                        Pattern pattern = Pattern.compile("^(get|is)(.)(.*)");
                        Matcher matcher = pattern.matcher(method.getName());
                        if ( matcher.matches() ) {
                            idFieldName = matcher.group(2).toLowerCase() + matcher.group(3);
                            idMethod = method;
                            break;
                        } else {
                            throw new IllegalStateException("Your getter method, " + method.getName() +
                                ", annotated with com.marklogic.client.pojo.annotation.Id " + 
                                " must be a proper getter method and begin with \"get\" or \"is\"");
                        }
                    } else {
                        throw new IllegalStateException("Your getter method, " + method.getName() +
                            ", annotated with com.marklogic.client.pojo.annotation.Id " + 
                            " must not require any arguments");
                    }
                }
            }
            if ( idMethod == null ) {
                for ( Field field : entityClass.getDeclaredFields() ) {
                    if ( field.isAnnotationPresent(Id.class) ) {
                        if ( ! Modifier.isPublic(field.getModifiers()) ) {
                            throw new IllegalStateException("Your field, " + field.getName() +
                                ", annotated with com.marklogic.client.pojo.annotation.Id " + 
                                " must be public");
                        }
                        idField = field;
                        break;
                    }
                }
            }
        }
    }

    private ID getId(T entity) {
        findId();
        if ( idMethod != null ) {
            try {
                return (ID) idMethod.invoke(entity);
            } catch (Exception e) {
                throw new IllegalStateException("Error invoking " + entityClass.getName() + " method " +
                    idMethod.getName(), e);
            }
        } else if ( idField != null ) {
            try {
                return (ID) idField.get(entity);
            } catch (Exception e) {
                throw new IllegalStateException("Error retrieving " + entityClass.getName() + " field " +
                    idField.getName(), e);
            }
        } else {
            throw new IllegalArgumentException("Your class " + entityClass.getName() +
                " does not have a method or field annotated with com.marklogic.client.pojo.annotation.Id");
        }
    }
}
