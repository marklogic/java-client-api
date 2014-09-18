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

import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.TextIndex;
import com.marklogic.client.query.StructuredQueryDefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
 
public class PojoQueryBuilderImpl<T> extends StructuredQueryBuilder implements PojoQueryBuilder<T> {
    private HashMap<String, Class> types = new HashMap<String, Class>();
    private HashMap<String, String> rangeIndextypes = new HashMap<String, String>();
    private Class<?> clazz;
    private String classWrapper;
    private boolean wrapQueries = false;

    public PojoQueryBuilderImpl(Class<T> clazz) {
        super();
        if ( clazz == null ) throw new IllegalArgumentException("clazz cannot be null");
        this.clazz = clazz;
        this.classWrapper = clazz.getName();
    }

    public PojoQueryBuilderImpl(Class<T> clazz, boolean wrapQueries) {
        this(clazz);
        this.wrapQueries = wrapQueries;
    }

    private StructuredQueryBuilder.PathIndex pojoPropertyPath(String pojoProperty) {
        //return pathIndex("*[local-name()=\"" + classWrapper + "\"]/" + pojoProperty);
        return pathIndex(classWrapper + "/" + pojoProperty);
    }
 
    public StructuredQueryDefinition containerQuery(String pojoProperty, StructuredQueryDefinition query) {
        if ( wrapQueries ) {
            return super.containerQuery(jsonProperty(classWrapper),
                super.containerQuery(jsonProperty(pojoProperty), query));
        } else {
            return super.containerQuery(jsonProperty(pojoProperty), query);
        }
    }
    @Override
    public StructuredQueryDefinition containerQuery(StructuredQueryDefinition query) {
        return super.containerQuery(jsonProperty(classWrapper), query);
    }
    public PojoQueryBuilder          containerQueryBuilder(String pojoProperty) {
        return new PojoQueryBuilderImpl(getType(pojoProperty), true);
    }
    @Override
    public StructuredQueryBuilder.GeospatialIndex
        geoPair(String latitudePropertyName, String longitudePropertyName)
    {
        return geoElementPair(jsonProperty(classWrapper), jsonProperty(latitudePropertyName), jsonProperty(longitudePropertyName));
    }
    public StructuredQueryBuilder.GeospatialIndex geoProperty(String pojoProperty) {
        return geoElement(jsonProperty(pojoProperty));
    }
    public StructuredQueryBuilder.GeospatialIndex geoPath(String pojoProperty) {
        return geoPath(pojoPropertyPath(pojoProperty));
    }
    public StructuredQueryDefinition range(String pojoProperty,
        StructuredQueryBuilder.Operator operator, Object... values)
    {
        return range(pojoPropertyPath(pojoProperty), getRangeIndexType(pojoProperty), operator, values);
    }
    public StructuredQueryDefinition range(String pojoProperty, String[] options,
        StructuredQueryBuilder.Operator operator, Object... values)
    {
        return range(pojoPropertyPath(pojoProperty), getRangeIndexType(pojoProperty), options,
            operator, values);
    }
    public StructuredQueryDefinition value(String pojoProperty, String... values) {
        if ( wrapQueries ) {
            return super.containerQuery(jsonProperty(classWrapper),
                value(jsonProperty(pojoProperty), values));
        } else {
            return value(jsonProperty(pojoProperty), values);
        }
    }
    public StructuredQueryDefinition value(String pojoProperty, Boolean value) {
        if ( wrapQueries ) {
            return super.containerQuery(jsonProperty(classWrapper),
                value(jsonProperty(pojoProperty), value));
        } else {
            return value(jsonProperty(pojoProperty), value);
        }
    }
    public StructuredQueryDefinition value(String pojoProperty, Number... values) {
        if ( wrapQueries ) {
            return super.containerQuery(jsonProperty(classWrapper),
                value(jsonProperty(pojoProperty), values));
        } else {
            return value(jsonProperty(pojoProperty), values);
        }
    }
    public StructuredQueryDefinition value(String pojoProperty, String[] options,
        double weight, String... values)
    {
        if ( wrapQueries ) {
            return super.containerQuery(jsonProperty(classWrapper),
                value(jsonProperty(pojoProperty), null, options, weight, values));
        } else {
            return value(jsonProperty(pojoProperty), null, options, weight, values);
        }
    }
    public StructuredQueryDefinition value(String pojoProperty, String[] options,
        double weight, Boolean value)
    {
        if ( wrapQueries ) {
            return super.containerQuery(jsonProperty(classWrapper),
                value(jsonProperty(pojoProperty), null, options, weight, value));
        } else {
            return value(jsonProperty(pojoProperty), null, options, weight, value);
        }
    }
    public StructuredQueryDefinition value(String pojoProperty, String[] options,
        double weight, Number... values)
    {
        if ( wrapQueries ) {
            return super.containerQuery(jsonProperty(classWrapper),
                value(jsonProperty(pojoProperty), null, options, weight, values));
        } else {
            return value(jsonProperty(pojoProperty), null, options, weight, values);
        }
    }
    public StructuredQueryDefinition word(String pojoProperty, String... words) {
        if ( wrapQueries ) {
            return super.containerQuery(jsonProperty(classWrapper),
                super.word(jsonProperty(pojoProperty), words));
        } else {
            return super.word(jsonProperty(pojoProperty), words);
        }
    }
    public StructuredQueryDefinition word(String pojoProperty, String[] options,
        double weight, String... words)
    {
        if ( wrapQueries ) {
            return super.containerQuery(jsonProperty(classWrapper),
                super.word(jsonProperty(pojoProperty), null, options, weight, words));
        } else {
            return super.word(jsonProperty(pojoProperty), null, options, weight, words);
        }
    }

    public String getRangeIndexType(String propertyName) {
        // map java types to acceptable Range Index types
        String type = rangeIndextypes.get(propertyName);
        if ( type == null ) {
            Class propertyClass = getType(propertyName);
            if ( String.class.isAssignableFrom(propertyClass) ) {
                type = "xs:string";
            } else if ( Integer.TYPE.equals(propertyClass) ) {
                type = "xs:int";
            } else if ( Long.TYPE.equals(propertyClass) ) {
                type = "xs:long";
            } else if ( Float.TYPE.equals(propertyClass) ) {
                type = "xs:float";
            } else if ( Double.TYPE.equals(propertyClass) ) {
                type = "xs:double";
            } else if ( Number.class.isAssignableFrom(propertyClass) ) {
                type = "xs:decimal";
            } else if ( Date.class.isAssignableFrom(propertyClass) ) {
                type = "xs:dateTime";
            }
            if ( type == null ) {
                throw new IllegalArgumentException("Property " + propertyName + " is not a native Java type");
            }
            rangeIndextypes.put(propertyName, type);
        }
        return type;
    }

    public Class getType(String propertyName) {
        Class propertyClass = types.get(propertyName);
        if ( propertyClass == null ) {
            // figure out the type of the java property
            String initCapPojoProperty = propertyName.substring(0,1).toUpperCase() + 
                propertyName.substring(1);
            try {
                propertyClass = clazz.getField(propertyName).getType();
            } catch(NoSuchFieldException e) {
                Method getMethod = null;
                try {
                    getMethod = clazz.getMethod("get" + initCapPojoProperty);
                } catch (NoSuchMethodException e2) {
                    try {
                        getMethod = clazz.getMethod("is" + initCapPojoProperty);
                        if ( ! Boolean.class.isAssignableFrom(getMethod.getReturnType()) ) {
                            getMethod = null;
                        }
                    } catch (NoSuchMethodException e3) {}
                }
                if ( getMethod != null ) {
                    if ( Modifier.isStatic(getMethod.getModifiers()) ) {
                        throw new IllegalArgumentException("get" + initCapPojoProperty +
                            " cannot be static");
                    }
                    propertyClass = getMethod.getReturnType();
                    if ( propertyClass == Void.TYPE ) {
                        throw new IllegalArgumentException("get" + initCapPojoProperty +
                            " must not have return type void");
                    }
                } else {
                    String setMethodName = "set" + initCapPojoProperty;
                    for ( Method method : clazz.getMethods() ) {
                        if ( setMethodName.equals(method.getName()) ) {
                            Class[] parameters = method.getParameterTypes();
                            if ( parameters != null && parameters.length == 1 ) {
                                propertyClass = parameters[0];
                                break;
                            }
                        }
                    }
                }
            }
            if ( propertyClass == null ) {
                throw new IllegalArgumentException("property " + propertyName + " not found, get" + initCapPojoProperty +
                    " not found, and set" + initCapPojoProperty + " not found in class " + classWrapper);
            }
            types.put(propertyName, propertyClass);
        }
        return propertyClass;
    }
}
