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

    public PojoQueryBuilderImpl(Class<T> clazz) {
        super();
        if ( clazz == null ) throw new IllegalArgumentException("clazz cannot be null");
        this.clazz = clazz;
        this.classWrapper = clazz.getName();
    }

    private StructuredQueryBuilder.PathIndex pojoFieldPath(String pojoField) {
        return pathIndex(classWrapper + "/" + pojoField);
    }
 
    public StructuredQueryDefinition containerQuery(String pojoField, StructuredQueryDefinition query) {
        return containerQuery(jsonProperty(pojoField), query);
    }
    @Override
    public StructuredQueryDefinition containerQuery(StructuredQueryDefinition query) {
        return containerQuery(jsonProperty(classWrapper), query);
    }
    public PojoQueryBuilder          containerQuery(String pojoField) {
        return new PojoQueryBuilderImpl(getType(pojoField));
    }
    @Override
    public StructuredQueryBuilder.GeospatialIndex
        geoPair(String latitudeFieldName, String longitudeFieldName)
    {
        return geoElementPair(jsonProperty(classWrapper), jsonProperty(latitudeFieldName), jsonProperty(longitudeFieldName));
    }
    public StructuredQueryBuilder.GeospatialIndex geoField(String pojoField) {
        return geoElement(jsonProperty(pojoField));
    }
    public StructuredQueryBuilder.GeospatialIndex geoPath(String pojoField) {
        return geoPath(pojoFieldPath(pojoField));
    }
    public StructuredQueryDefinition range(String pojoField,
        StructuredQueryBuilder.Operator operator, Object... values) {
        return range(pojoFieldPath(pojoField), getRangeIndexType(pojoField), operator, values);
    }
    public StructuredQueryDefinition range(String pojoField, String[] options,
        StructuredQueryBuilder.Operator operator, Object... values)
    {
        return range(pojoFieldPath(pojoField), getRangeIndexType(pojoField), options,
            operator, values);
    }
    public StructuredQueryDefinition value(String pojoField, String... values) {
        return value(jsonProperty(pojoField), values);
    }
    public StructuredQueryDefinition value(String pojoField, String[] options,
        double weight, String... values)
    {
        return value(jsonProperty(pojoField), null, options, weight, values);
    }
    public StructuredQueryDefinition word(String pojoField, String... words) {
        return word(jsonProperty(pojoField), words);
    }
    public StructuredQueryDefinition word(String pojoField, String[] options,
        double weight, String... words)
    {
        return word(jsonProperty(pojoField), null, options, weight, words);
    }
    public StructuredQueryDefinition word(String... words) {
        return word(words);
    }
    public StructuredQueryDefinition word(String[] options, double weight, String... words) {
        return word(options, weight, words);
    }

    public String getRangeIndexType(String fieldName) {
        // map java types to acceptable Range Index types
        String type = rangeIndextypes.get(fieldName);
        if ( type == null ) {
            Class fieldClass = getType(fieldName);
            if ( String.class.isAssignableFrom(fieldClass) ) {
                type = "string";
            } else if ( Integer.class.isAssignableFrom(fieldClass) ) {
                type = "int";
            } else if ( Long.class.isAssignableFrom(fieldClass) ) {
                type = "long";
            } else if ( Float.class.isAssignableFrom(fieldClass) ) {
                type = "float";
            } else if ( Double.class.isAssignableFrom(fieldClass) ) {
                type = "double";
            } else if ( Number.class.isAssignableFrom(fieldClass) ) {
                type = "decimal";
            } else if ( Date.class.isAssignableFrom(fieldClass) ) {
                type = "dateTime";
            }
            if ( type == null ) {
                throw new IllegalArgumentException("Field " + fieldName + " is not a native Java type");
            }
            rangeIndextypes.put(fieldName, type);
        }
        return type;
    }

    public Class getType(String fieldName) {
        Class fieldClass = types.get(fieldName);
        if ( fieldClass == null ) {
            // figure out the type of the java field
            String initCapPojoField = fieldName.substring(1,2).toUpperCase() + 
                fieldName.substring(2);
            try {
                fieldClass = clazz.getField(fieldName).getType();
            } catch(NoSuchFieldException e) {
                Method getMethod = null;
                try {
                    getMethod = clazz.getMethod("get" + initCapPojoField);
                } catch (NoSuchMethodException e2) {
                    try {
                        getMethod = clazz.getMethod("is" + initCapPojoField);
                        if ( ! Boolean.class.isAssignableFrom(getMethod.getReturnType()) ) {
                            getMethod = null;
                        }
                    } catch (NoSuchMethodException e3) {}
                }
                if ( getMethod != null ) {
                    if ( Modifier.isStatic(getMethod.getModifiers()) ) {
                        throw new IllegalArgumentException("get" + initCapPojoField +
                            " cannot be static");
                    }
                    fieldClass = getMethod.getReturnType();
                    if ( fieldClass == Void.TYPE ) {
                        throw new IllegalArgumentException("get" + initCapPojoField +
                            " must not have return type void");
                    }
                } else {
                    String setMethodName = "set" + initCapPojoField;
                    for ( Method method : clazz.getMethods() ) {
                        if ( setMethodName.equals(method.getName()) ) {
                            Class[] parameters = method.getParameterTypes();
                            if ( parameters != null && parameters.length == 1 ) {
                                fieldClass = parameters[0];
                                break;
                            }
                        }
                    }
                }
            }
            if ( fieldClass == null ) {
                throw new IllegalArgumentException("field " + fieldName + " not found, get" + initCapPojoField +
                    " not found, and set" + initCapPojoField + " not found in class " + classWrapper);
            }
            types.put(fieldName, fieldClass);
        }
        return fieldClass;
    }
}


