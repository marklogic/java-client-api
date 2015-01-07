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
package com.marklogic.client.pojo.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.marklogic.client.pojo.annotation.GeospatialLatitude;
import com.marklogic.client.pojo.annotation.GeospatialLongitude;
import com.marklogic.client.pojo.annotation.GeospatialPathIndexProperty;
import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.pojo.annotation.PathIndexProperty;

/** <p>Generates a MarkLogic index configuration file in JSON format describing the indexes 
 * required by the annotations on the specific classes.  
 * The output can be used by an administrator to create indexes in the database
 * using the Management REST API.</p>
 *
 * <p><b>WARNING!</b> Applying this generated index file via management API will overwrite
 * existing indexes!  Only use this as-is if there are no other indexes on your
 * database that you want to keep.  To add these indexes to other indexes please get
 * the configuration for existing indexes from the management API and add these to that
 * file before applying.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 *     java com.marklogic.client.pojo.util.GenerateIndexConfig 
 *       -classes "com.marklogic.client.test.City com.marklogic.client.test.Country" 
 *       -file cityIndexes.json
 *
 *     curl -i --digest --user admin:admin \
 *       -H 'Content-Type: application/json' \
 *       -d '@cityIndexes.json' \
 *       -X PUT 'http://localhost:8002/manage/LATEST/databases/java-unittest/properties'
 * </pre>
 */
public class GenerateIndexConfig {
    /**
     * Reads the annotations from the specified classes and generates MarkLogic index 
     * configurations specified by the annotations. Accepts the 
     * following options:
     *   -classes: a space-separated list of java pojo classes visible on the classpath
     *   -file: a file path to write with the output (otherwise uses standard out)
     * @throws IOException if an error occurs reading the classes or writing the output
     * @throws IllegalStateException if errors are found in your annotations
     * @throws ClassNotFoundException if the classes are not found on the classpath
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String[] classes = new String[] {};
        Writer out = null;
        for (int i=0; i < args.length; i++) {
            String name = args[i];
            if (name.startsWith("-") && name.length() > 1 && ++i < args.length) {
                String argValue = args[i];
                if ( "-classes".equals(name) ) {
                    classes = argValue.split("\\s+");
                } else if ( "-file".equals(name) ) {
                    out= new FileWriter(argValue);
                }
            }
        }
        if ( out == null ) out = new OutputStreamWriter(System.out);

        ObjectMapper mapper = new ObjectMapper();
        generateConfig(classes, mapper, out);
    }

    private static class AnnotationFound<T extends Annotation> {
        T annotation;
        String foundMessage;
    }

    private static class PathIndexFound {
        String fullyQualifiedClassName;
        String propertyName;
        PathIndexProperty.ScalarType scalarType;
        String foundMessage;
        String getPath() { return fullyQualifiedClassName + "/" + propertyName; }
    }

    private static class GeoPathIndexFound {
        String fullyQualifiedClassName;
        String propertyName;
        String foundMessage;
        String getPath() { return fullyQualifiedClassName + "/" + propertyName; }
    }

    private static class GeoPairFound {
        String fullyQualifiedClassName;
        String latitudeName;
        String longitudeName;
        String latitudeFoundMessage;
        String longitudeFoundMessage;
    }

    private static void generateConfig(String[] classes, ObjectMapper mapper, Writer out) 
        throws ClassNotFoundException, IOException 
    {
        ArrayList<PathIndexFound> paths = new ArrayList<PathIndexFound>();
        ArrayList<GeoPathIndexFound> geoPaths = new ArrayList<GeoPathIndexFound>();
        ArrayList<GeoPairFound> geoPairs = new ArrayList<GeoPairFound>();
        for ( String className : classes ) {
            Class<?> clazz = ClassUtil.findClass(className);
            SerializationConfig serializationConfig = new ObjectMapper().getSerializationConfig();
            JavaType javaType = serializationConfig.constructType(clazz);
            BeanDescription beanDescription = serializationConfig.introspect(javaType);
            List<BeanPropertyDefinition> properties = beanDescription.findProperties();
            GeoPairFound geoPair = new GeoPairFound();
            for ( BeanPropertyDefinition property : properties ) {
                AnnotationFound<Id> idAnnotation = getAnnotation(property, Id.class);
                if ( idAnnotation != null ) {
                }
                AnnotationFound<PathIndexProperty> pathIndexAnnotation = getAnnotation(property, PathIndexProperty.class);
                if ( pathIndexAnnotation != null ) {
                    PathIndexFound found = new PathIndexFound();
                    found.fullyQualifiedClassName = clazz.getName();
                    found.propertyName = property.getName();
                    found.scalarType = pathIndexAnnotation.annotation.scalarType();
                    found.foundMessage = pathIndexAnnotation.foundMessage;
                    paths.add(found);
                }
                AnnotationFound<GeospatialPathIndexProperty> geoPathAnnotation = 
                    getAnnotation(property, GeospatialPathIndexProperty.class);
                if ( geoPathAnnotation != null ) {
                    GeoPathIndexFound found = new GeoPathIndexFound();
                    found.fullyQualifiedClassName = clazz.getName();
                    found.propertyName = property.getName();
                    found.foundMessage = geoPathAnnotation.foundMessage;
                    geoPaths.add(found);
                }
                AnnotationFound<GeospatialLatitude> geoLatAnnotation = getAnnotation(property, GeospatialLatitude.class);
                if ( geoLatAnnotation != null ) {
                    if ( geoPair.latitudeName != null ) errorExactPair(className);
                    errorIfPairAlreadyFound(geoPair);
                    geoPair.fullyQualifiedClassName = clazz.getName();
                    geoPair.latitudeName = property.getName();
                    geoPair.latitudeFoundMessage = geoLatAnnotation.foundMessage;
                }
                AnnotationFound<GeospatialLongitude> geoLonAnnotation = getAnnotation(property, GeospatialLongitude.class);
                if ( geoLonAnnotation != null ) {
                    if ( geoPair.longitudeName != null ) errorExactPair(className);
                    errorIfPairAlreadyFound(geoPair);
                    geoPair.fullyQualifiedClassName = clazz.getName();
                    geoPair.longitudeName = property.getName();
                    geoPair.longitudeFoundMessage = geoLonAnnotation.foundMessage;
                }
            }
            if ( isPairComplete(geoPair) ) {
                geoPairs.add(geoPair);
            } else if ( isPairPartial(geoPair) ) {
                errorExactPair(className);
            }
        }
        JsonGenerator config = mapper.getFactory().createGenerator(out);
        config.useDefaultPrettyPrinter();
        config.writeStartObject();
        generatePathIndexes(paths, config);
        generateGeoPathIndexes(geoPaths, config);
        generateGeoPairIndexes(geoPairs, config);
        config.writeEndObject();
        config.flush();
        config.close();
    }

    private static void errorExactPair(String className) {
        throw new IllegalStateException("Error processing class '" + className + "'. Each class with @GeospatialLatitude or " +
            "@GeospatialLongitude annotations must have exactly one of each");
    }

    private static void errorIfPairAlreadyFound(GeoPairFound geoPair) {
        if ( isPairComplete(geoPair) ) {
            throw new IllegalStateException("Each class can have a maximum of one @GeospatialLatitude and " +
                "one @GeospatialLongitude annotation");
        }
    }

    private static boolean isPairComplete(GeoPairFound geoPair) {
        return geoPair != null && geoPair.latitudeName != null && geoPair.longitudeName != null;
    }

    private static boolean isPairPartial(GeoPairFound geoPair) {
        return geoPair != null && (geoPair.latitudeName != null || geoPair.longitudeName != null);
    }

    private static void generatePathIndexes(List<PathIndexFound> paths, JsonGenerator config) throws IOException {
        config.writeArrayFieldStart("range-path-index");
        for ( PathIndexFound found : paths ) {
            //System.err.println("found " + found.propertyName + " " + found.foundMessage);
            config.writeStartObject();
            config.writeStringField("path-expression", found.getPath());
            config.writeStringField("scalar-type", "" + found.scalarType.toString());
            if ( PathIndexProperty.ScalarType.STRING == found.scalarType ) {
                config.writeStringField("collation", "http://marklogic.com/collation/");
            // TODO: remove this else clause once https://bugtrack.marklogic.com/30043 is fixed
            } else {
                config.writeStringField("collation", "");
            }
            config.writeStringField("range-value-positions", "false");
            config.writeStringField("invalid-values", "ignore");
            config.writeEndObject();
        }
        config.writeEndArray();
    }

    private static void generateGeoPathIndexes(List<GeoPathIndexFound> geoPaths, JsonGenerator config) throws IOException {
        config.writeArrayFieldStart("geospatial-path-index");
        for ( GeoPathIndexFound found : geoPaths ) {
            //System.err.println("found " + found.propertyName + " " + found.foundMessage);
            config.writeStartObject();
            config.writeStringField("path-expression", found.getPath());
            config.writeStringField("coordinate-system", "wgs84");
            config.writeStringField("point-format", "point");
            config.writeStringField("range-value-positions", "false");
            config.writeStringField("invalid-values", "ignore");
            config.writeEndObject();
        }
        config.writeEndArray();
    }

    private static void generateGeoPairIndexes(List<GeoPairFound> geoPairs, JsonGenerator config) throws IOException {
        config.writeArrayFieldStart("geospatial-element-pair-index");
        for ( GeoPairFound found : geoPairs ) {
            config.writeStartObject();
            config.writeStringField("parent-namespace-uri", "");
            config.writeStringField("parent-localname", found.fullyQualifiedClassName);
            //System.err.println("found " + found.latitudeName + " " + found.latitudeFoundMessage);
            config.writeStringField("latitude-namespace-uri", "");
            config.writeStringField("latitude-localname", found.latitudeName);
            //System.err.println("found " + found.longitudeName + " " + found.longitudeFoundMessage);
            config.writeStringField("longitude-namespace-uri", "");
            config.writeStringField("longitude-localname", found.longitudeName);
            config.writeStringField("coordinate-system", "wgs84");
            config.writeStringField("range-value-positions", "false");
            config.writeStringField("invalid-values", "ignore");
            config.writeEndObject();
        }
        config.writeEndArray();
    }

    private static <T extends Annotation> AnnotationFound<T> getAnnotation(BeanPropertyDefinition property, Class<T> annotation) {
        String annotationName = "@" + annotation.getSimpleName();
        if ( property.hasConstructorParameter() ) {
            AnnotatedParameter parameter = property.getConstructorParameter();
            T constructorAnnotation = parameter.getAnnotation(annotation);
            if ( constructorAnnotation != null ) {
                AnnotationFound<T> found = new AnnotationFound<T>();
                found.annotation = constructorAnnotation;
                found.foundMessage = annotationName + " on constructor parameter '" + 
                    parameter.getRawType().getName() + " " + parameter.getName() + "'";
                return found;
            }
        } 
        if ( property.hasField() ) {
            T fieldAnnotation = property.getField().getAnnotation(annotation);
            if ( fieldAnnotation != null ) {
                AnnotationFound<T> found = new AnnotationFound<T>();
                found.annotation = fieldAnnotation;
                found.foundMessage = annotationName + " on field '" + property.getField().getName() + "'";
                return found;
            }
        }
        if ( property.hasGetter() ) {
            // I have to use getMember because Jackson returns annotation whether it's on the getter or setter
            T getterAnnotation = property.getGetter().getMember().getAnnotation(annotation);
            if ( getterAnnotation != null ) {
                AnnotationFound<T> found = new AnnotationFound<T>();
                found.annotation = getterAnnotation;
                found.foundMessage = annotationName + " on method '" + property.getGetter().getName() + "'";
                return found;
            }
        }
        if ( property.hasSetter() ) {
            // I have to use getMember because Jackson returns annotation whether it's on the getter or setter
            T setterAnnotation = property.getSetter().getMember().getAnnotation(annotation);
            if ( setterAnnotation != null ) {
                AnnotationFound<T> found = new AnnotationFound<T>();
                found.annotation = setterAnnotation;
                found.foundMessage = annotationName + " on method '" + property.getSetter().getName() + "'";
                return found;
            }
        }
        return null;
    }
}
