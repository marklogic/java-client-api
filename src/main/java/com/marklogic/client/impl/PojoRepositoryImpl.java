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
package com.marklogic.client.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonDatabindHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.pojo.PojoQueryDefinition;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
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
    private Field idProperty;
    private String idPropertyName;
    private static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static SimpleDateFormat simpleDateFormat8601 = new SimpleDateFormat(ISO_8601_FORMAT);
    static { simpleDateFormat8601.setTimeZone(TimeZone.getTimeZone("UTC")); }
    private ObjectMapper objectMapper = new ObjectMapper()
        // if we don't do the next two lines Jackson will automatically close our streams which is undesirable
        .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
        .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false)
        // we do the next two so dates are written in xs:dateTime format
        // which makes them ready for range indexes in MarkLogic Server
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .setDateFormat(simpleDateFormat8601)
        // enableDefaultTyping just means include types in the serialized output
        // we need this to do strongly-typed queries 
        .enableDefaultTyping(
            // ObjectMapper.DefaultTyping.NON_FINAL means that typing in serialized output
            // for all non-final types except the "natural" types (String, Boolean, Integer, Double), 
            // which can be correctly inferred from JSON; as well as for all arrays of non-final types.
            ObjectMapper.DefaultTyping.NON_FINAL, 
            // JsonTypeInfo.As.WRAPPER_OBJECT means add a type wrapper around the data so then
            // our strongly-typed queries can use parent-child scoped queries or path index queries
            JsonTypeInfo.As.WRAPPER_OBJECT);
    PojoRepositoryImpl(DatabaseClient client, Class<T> entityClass) {
        this.client = client;
        this.entityClass = entityClass;
        this.idClass = null;
        this.docMgr = client.newJSONDocumentManager();
        this.qb = new PojoQueryBuilderImpl<T>(entityClass);
    }

    PojoRepositoryImpl(DatabaseClient client, Class<T> entityClass, Class<ID> idClass) {
        this(client, entityClass);
        this.idClass = idClass;
        findId();
        if ( idMethod == null && idProperty == null ) {
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
        JacksonDatabindHandle contentHandle = new JacksonDatabindHandle(entity);
        contentHandle.setMapper(objectMapper); 
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
        return count((PojoQueryDefinition) null);
    }

    public long count(String... collections) {
        if ( collections != null && collections.length > 0 ) {
            if ( collections.length > 1 || collections[0] != null ) {
                return count(qb.collection(collections));
            }
        }
        return count((PojoQueryDefinition) null);
    }
    public long count(PojoQueryDefinition query) {
        long pageLength = getPageLength();
        setPageLength(0);
        PojoPage page = search(query, 1);
        setPageLength(pageLength);
        return page.getTotalSize();
    }
  
    public void delete(ID... ids) {
        for ( ID id : ids ) {
            docMgr.delete(createUri(id));
        }
    }
    public void deleteAll() {
        QueryManager queryMgr = client.newQueryManager();
        DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
        deleteQuery.setCollections(entityClass.getName());
        queryMgr.delete(deleteQuery);
    }
    /* REST API does not currently support DELETE /search with multiple collection arguments
    public void deleteAll(String... collections) {
        if ( collections == null || collections.length == 0 ) {
            throw new IllegalArgumentException("You must specify at least one collection");
        } else if ( collections[0] == null ) {
            throw new IllegalArgumentException("Collection argument must not be null");
        }
        QueryManager queryMgr = client.newQueryManager();
        DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
        deleteQuery.setCollections(collections);
        queryMgr.delete((DeleteQueryDefinition) wrapQuery(deleteQuery));
    }
    */
  
    public T read(ID id) {
        return read(id, null);
    }
    public T read(ID id, Transaction transaction) {
        ArrayList<ID> ids = new ArrayList<ID>();
        ids.add(id);
        PojoPage<T> page = read(ids.toArray((ID[])new Serializable[0]), transaction);
        if ( page == null ) return null;
        if ( page.hasNext() ) return page.next();
        return null;
    }
    public PojoPage<T> read(ID[] ids) {
        return read(ids, null);
    }
    public PojoPage<T> read(ID[] ids, Transaction transaction) {
        ArrayList<String> uris = new ArrayList<String>();
        for ( ID id : ids ) {
            uris.add(createUri(id));
        }
        DocumentPage docPage = (DocumentPage) docMgr.read(transaction, uris.toArray(new String[0]));
        PojoPage<T> pojoPage = new PojoPageImpl(docPage, entityClass);
        return pojoPage;
    }
    public PojoPage<T> readAll(long start) {
        return search(null, start, null, null);
    }
    public PojoPage<T> readAll(long start, Transaction transaction) {
        return search(null, start, null, transaction);
    }

    public PojoPage<T> search(long start, String... collections) {
        return search(qb.collection(collections), start, null, null);
    }
    public PojoPage<T> search(long start, Transaction transaction, String... collections) {
        return search(qb.collection(collections), start, null, transaction);
    }

    public PojoPage<T> search(PojoQueryDefinition query, long start) {
        return search(query, start, null, null);
    }
    public PojoPage<T> search(PojoQueryDefinition query, long start, Transaction transaction) {
        return search(query, start, null, transaction);
    }
    public PojoPage<T> search(PojoQueryDefinition query, long start, SearchReadHandle searchHandle) {
        return search(query, start, searchHandle, null);
    }
    public PojoPage<T> search(PojoQueryDefinition query, long start, SearchReadHandle searchHandle, Transaction transaction) {
        if ( searchHandle != null ) {
            HandleImplementation searchBase = HandleAccessor.checkHandle(searchHandle, "search");
            if (searchHandle instanceof SearchHandle) {
                SearchHandle responseHandle = (SearchHandle) searchHandle;
                if ( docMgr instanceof DocumentManagerImpl ) {
                    responseHandle.setHandleRegistry(((DocumentManagerImpl) docMgr).getHandleRegistry());
                }
                responseHandle.setQueryCriteria(query);
            }
        }

        String tid = transaction == null ? null : transaction.getTransactionId();
        DocumentPage docPage = docMgr.search(wrapQuery(query), start, searchHandle, transaction);
        PojoPage<T> pojoPage = new PojoPageImpl(docPage, entityClass);
        return pojoPage;
    }
 
    public PojoQueryBuilder getQueryBuilder() {
        return qb;
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

    public void defineIdProperty(String propertyName) {
    }
 
    public DatabaseClient getDatabaseClient() {
        return client;
    }

    private PojoQueryDefinition wrapQuery(PojoQueryDefinition query) {
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
        if ( idMethod == null && idProperty == null ) {
            SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
            JavaType javaType = serializationConfig.constructType(entityClass);
            BeanDescription beanDescription = serializationConfig.introspect(javaType);
            List<BeanPropertyDefinition> properties = beanDescription.findProperties();
            for ( BeanPropertyDefinition property : properties ) {
                /* Constructor parameters don't work because they give us no value accessor
                if ( property.hasConstructorParameter() ) {
                    AnnotatedParameter parameter = property.getConstructorParameter();
                    if ( parameter.getAnnotation(Id.class) != null ) {
                        idPropertyName = property.getName();
                    }
                }
                */
                if ( property.hasField() ) {
                    Field field = property.getField().getAnnotated();
                    if ( field.getAnnotation(Id.class) != null ) {
                        idPropertyName = property.getName();
                        idProperty = field;
                        break;
                    }
                }
                if ( property.hasGetter() ) {
                    Method getter = property.getGetter().getAnnotated();
                    if ( getter.getAnnotation(Id.class) != null ) {
                        idPropertyName = property.getName();
                        idMethod = getter;
                        break;
                    }
                }
                // setter only doesn't work because it gives us no value accessor
            }
        }
        // Jackson's introspect approach should find it, but our old approach below 
        // gives some helpful errors
        if ( idMethod == null && idProperty == null ) {
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
                            idPropertyName = matcher.group(2).toLowerCase() + matcher.group(3);
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
                        idProperty = field;
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
        } else if ( idProperty != null ) {
            try {
                return (ID) idProperty.get(entity);
            } catch (Exception e) {
                throw new IllegalStateException("Error retrieving " + entityClass.getName() + " field " +
                    idProperty.getName(), e);
            }
        } else {
            throw new IllegalArgumentException("Your class " + entityClass.getName() +
                " does not have a method or field annotated with com.marklogic.client.pojo.annotation.Id");
        }
    }
}
