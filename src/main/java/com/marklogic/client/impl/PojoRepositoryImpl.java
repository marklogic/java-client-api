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
package com.marklogic.client.impl;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicBindingException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonDatabindHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.pojo.PojoQueryDefinition;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.QueryManager.QueryView;
import com.sun.jersey.api.client.ClientHandlerException;

public class PojoRepositoryImpl<T, ID extends Serializable>
    implements PojoRepository<T, ID>
{
    private static final Pattern getterPattern = Pattern.compile("^(get|is)(.)(.*)");
    private final String EXTENSION = ".json";

    private DatabaseClient client;
    private Class<T> entityClass;
    @SuppressWarnings("unused")
    private Class<ID> idClass;
    private JSONDocumentManager docMgr;
    private PojoQueryBuilder<T> qb;
    private Method idMethod;
    private Field idProperty;
    @SuppressWarnings("unused")
    private String idPropertyName;
    private static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static SimpleDateFormat simpleDateFormat8601;
    static {
        try {
            simpleDateFormat8601 = new SimpleDateFormat(ISO_8601_FORMAT);
        // Java 1.6 doesn't yet know about X (ISO 8601 format)
        } catch (IllegalArgumentException e) {
            if ( "Illegal pattern character 'X'".equals(e.getMessage()) ) {
                simpleDateFormat8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            }
        }
    }
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

    @Override
    public void write(T entity) {
        write(entity, null, (String[]) null);
    }
    @Override
    public void write(T entity, String... collections) {
        write(entity, null, collections);
    }
    @Override
    public void write(T entity, Transaction transaction) {
        write(entity, transaction, (String[]) null);
    }
    @Override
    public void write(T entity, Transaction transaction, String... collections) {
        if ( entity == null ) return;
        JacksonDatabindHandle<T> contentHandle = new JacksonDatabindHandle<T>(entity);
        contentHandle.setMapper(objectMapper); 
        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
        metadataHandle = metadataHandle.withCollections(entityClass.getName());
        if ( collections != null && collections.length > 0 ) {
            metadataHandle = metadataHandle.withCollections(collections);
        }
        DocumentWriteSet writeSet = docMgr.newWriteSet();
        writeSet.add(getDocumentUri(entity), metadataHandle, contentHandle);
        try {
            docMgr.write(writeSet, transaction);
        } catch(ClientHandlerException e) {
            checkForEmptyBeans(e);
            throw e;
        }
    }

    private void checkForEmptyBeans(Throwable e) {
        Throwable cause = e.getCause();
        if ( cause != null ) {
            if ( cause instanceof JsonMappingException && 
                 cause.getMessage() != null &&
                 cause.getMessage().contains("SerializationFeature.FAIL_ON_EMPTY_BEANS") )
            {
                throw new MarkLogicBindingException(
                    "Each of your pojo beans and descendent beans must have public fields or paired get/set methods",
                    cause);
            } else {
                checkForEmptyBeans(cause);
            }
        }
    }


    @Override
    public boolean exists(ID id) {
        return docMgr.exists(createUri(id)) != null;
    }

    @Override
    public boolean exists(ID id, Transaction transaction) {
        return docMgr.exists(createUri(id), transaction) != null;
    }

    @Override
    public long count() {
        return count((PojoQueryDefinition) null, null);
    }

    @Override
    public long count(String... collections) {
        return count(collections, null);
    }

    @Override
    public long count(PojoQueryDefinition query) {
        return count((PojoQueryDefinition) null, null);
    }
  
    @Override
    public long count(Transaction transaction) {
        return count((PojoQueryDefinition) null, transaction);
    }

    @Override
    public long count(String[] collections, Transaction transaction) {
        if ( collections != null && collections.length > 0 ) {
            if ( collections.length > 1 || collections[0] != null ) {
                return count(qb.collection(collections), transaction);
            }
        }
        return count((PojoQueryDefinition) null, transaction);
    }

    @Override
    public long count(PojoQueryDefinition query, Transaction transaction) {
        long pageLength = getPageLength();
        setPageLength(0);
        PojoPage<T> page = search(query, 1, transaction);
        setPageLength(pageLength);
        return page.getTotalSize();
    }

    @Override
    public void delete(ID... ids) {
        delete(ids, null);
    }

    @Override
    public void delete(ID[] ids, Transaction transaction) {
        for ( ID id : ids ) {
            docMgr.delete(createUri(id), transaction);
        }
    }

    @Override
    public void deleteAll() {
        deleteAll(null);
    }

    @Override
    public void deleteAll(Transaction transaction) {
        QueryManager queryMgr = client.newQueryManager();
        DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
        deleteQuery.setCollections(entityClass.getName());
        queryMgr.delete(deleteQuery, transaction);
    }
    /* REST API does not currently support DELETE /search with multiple collection arguments
    @Override
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
  
    @Override
    public T read(ID id) {
        return read(id, null);
    }
    @Override
    public T read(ID id, Transaction transaction) {
        ArrayList<ID> ids = new ArrayList<ID>();
        ids.add(id);
        @SuppressWarnings("unchecked")
        PojoPage<T> page = read(ids.toArray((ID[])new Serializable[0]), transaction);
        if ( page == null || page.hasNext() == false ) {
            throw new ResourceNotFoundException("Could not find document of type " +
                entityClass.getName() + " with id " + id);
        }
        return page.next();
    }
    @Override
    public PojoPage<T> read(ID[] ids) {
        return read(ids, null);
    }
    @Override
    public PojoPage<T> read(ID[] ids, Transaction transaction) {
        ArrayList<String> uris = new ArrayList<String>();
        for ( ID id : ids ) {
            uris.add(createUri(id));
        }
        DocumentPage docPage = (DocumentPage) docMgr.read(transaction, uris.toArray(new String[0]));
        PojoPage<T> pojoPage = new PojoPageImpl<T>(docPage, entityClass);
        return pojoPage;
    }
    @Override
    public PojoPage<T> readAll(long start) {
        return search(null, start, null, null);
    }
    @Override
    public PojoPage<T> readAll(long start, Transaction transaction) {
        return search(null, start, null, transaction);
    }

    @Override
    public PojoPage<T> search(long start, String... collections) {
        return search(qb.collection(collections), start, null, null);
    }
    @Override
    public PojoPage<T> search(long start, Transaction transaction, String... collections) {
        return search(qb.collection(collections), start, null, transaction);
    }

    @Override
    public PojoPage<T> search(PojoQueryDefinition query, long start) {
        return search(query, start, null, null);
    }
    @Override
    public PojoPage<T> search(PojoQueryDefinition query, long start, Transaction transaction) {
        return search(query, start, null, transaction);
    }
    @Override
    public PojoPage<T> search(PojoQueryDefinition query, long start, SearchReadHandle searchHandle) {
        return search(query, start, searchHandle, null);
    }
    @Override
    public PojoPage<T> search(PojoQueryDefinition query, long start, SearchReadHandle searchHandle, Transaction transaction) {
        if ( searchHandle != null ) {
            HandleAccessor.checkHandle(searchHandle, "search");
            if (searchHandle instanceof SearchHandle) {
                SearchHandle responseHandle = (SearchHandle) searchHandle;
                if ( docMgr instanceof DocumentManagerImpl ) {
                    responseHandle.setHandleRegistry(((DocumentManagerImpl<?, ?>) docMgr).getHandleRegistry());
                }
                responseHandle.setQueryCriteria(query);
            }
        }

        DocumentPage docPage = docMgr.search(wrapQuery(query), start, searchHandle, transaction);
        PojoPage<T> pojoPage = new PojoPageImpl<T>(docPage, entityClass);
        return pojoPage;
    }
 
    @Override
    public PojoQueryBuilder<T> getQueryBuilder() {
        return qb;
    }

    @Override
    public long getPageLength() {
        return docMgr.getPageLength();
    }
    @Override
    public void setPageLength(long length) {
        docMgr.setPageLength(length);
    }
    
    public QueryView getSearchView() {
        return docMgr.getSearchView();
    }

    public void setSearchView(QueryView view) {
        docMgr.setSearchView(view);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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

    @Override
    public String getDocumentUri(T entity) {
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
                    if ( property.hasSetter() ) {
                        Method setter = property.getSetter().getAnnotated();
                        if ( setter.getAnnotation(Id.class) != null ) {
                            idPropertyName = property.getName();
                            idMethod = getter;
                            break;
                        }
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
                    Class<?>[] parameters = method.getParameterTypes();
                    if ( ! Modifier.isPublic(method.getModifiers()) ) {
                        throw new IllegalStateException("Your method, " + method.getName() +
                            ", annotated with com.marklogic.client.pojo.annotation.Id " + 
                            " must be public");
                    }
                    if ( parameters == null || parameters.length == 0 ) {
                        Matcher matcher = getterPattern.matcher(method.getName());
                        if ( matcher.matches() ) {
                            idPropertyName = matcher.group(2).toLowerCase() + matcher.group(3);
                            idMethod = method;
                            break;
                        } else {
                            throw new IllegalStateException("Your no-args method, " + method.getName() +
                                ", annotated with com.marklogic.client.pojo.annotation.Id " + 
                                " must be a proper getter method and begin with \"get\" or \"is\"");
                        }
                    } else {
                        Matcher getterMatcher = getterPattern.matcher(method.getName());
                        if ( getterMatcher.matches() ) {
                            throw new IllegalStateException("Your getter method, " + method.getName() +
                                ", annotated with com.marklogic.client.pojo.annotation.Id " + 
                                " must not require any arguments");
                        } else if ( method.getName().startsWith("set") ) {
                            throw new MarkLogicInternalException("Your setter method, " + method.getName() +
                                ", annotated with com.marklogic.client.pojo.annotation.Id " +
                                "was not found by Jackson for some reason.  Please report this to " +
                                "MarkLogic support.");
                        } else {
                            throw new IllegalStateException("Your setter method, " + method.getName() +
                                ", annotated with com.marklogic.client.pojo.annotation.Id " + 
                                " must be a proper setter method (beginning with \"set\")");
                        }
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

    @SuppressWarnings("unchecked")
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
