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
package com.marklogic.client.query;

import javax.xml.namespace.QName;

import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.util.IterableNamespaceContext;
 
public interface CombinedQueryBuilder<T> {
    public StructuredQueryDefinition and(StructuredQueryDefinition... queries);
    public StructuredQueryDefinition andNot(StructuredQueryDefinition positive, StructuredQueryDefinition negative);
    public StructuredQueryBuilder.Attribute attribute(QName qname);
    public StructuredQueryBuilder.Attribute attribute(String name);
    public StructuredQueryDefinition boost(StructuredQueryDefinition matchingQuery, StructuredQueryDefinition boostingQuery);
    public StructuredQueryBuilder.Region box(double south, double west, double north, double east);
    public RawStructuredQueryDefinition build(StructuredQueryDefinition... queries);
    public StructuredQueryBuilder.Region circle(double latitude, double longitude, double radius);
    public StructuredQueryBuilder.Region circle(StructuredQueryBuilder.Point center, double radius);
    public StructuredQueryDefinition collection(String... uris);
    public StructuredQueryDefinition collectionConstraint(String constraintName, String... uris);
    public StructuredQueryDefinition containerConstraint(String constraintName, StructuredQueryDefinition query);
    public StructuredQueryDefinition containerQuery(StructuredQueryBuilder.ContainerIndex index, StructuredQueryDefinition query);
    public StructuredQueryDefinition customConstraint(String constraintName, String... text);
    public StructuredQueryDefinition directory(boolean isInfinite, String... uris);
    public StructuredQueryDefinition directory(int depth, String... uris);
    public StructuredQueryDefinition document(String... uris);
    public StructuredQueryDefinition documentFragment(StructuredQueryDefinition query);
    public StructuredQueryBuilder.Element element(QName qname);
    public StructuredQueryBuilder.Element element(String name);
    public StructuredQueryBuilder.ElementAttribute elementAttribute(StructuredQueryBuilder.Element element, StructuredQueryBuilder.Attribute attribute);
    public StructuredQueryDefinition elementConstraint(String constraintName, StructuredQueryDefinition query);
    public StructuredQueryBuilder.Field field(String name);
    public StructuredQueryBuilder.GeospatialIndex geoAttributePair(StructuredQueryBuilder.Element parent, StructuredQueryBuilder.Attribute lat, StructuredQueryBuilder.Attribute lon);
    public StructuredQueryBuilder.GeospatialIndex geoElement(StructuredQueryBuilder.Element element);
    public StructuredQueryBuilder.GeospatialIndex geoElement(StructuredQueryBuilder.Element parent, StructuredQueryBuilder.Element element);
    public StructuredQueryBuilder.GeospatialIndex geoElementPair(StructuredQueryBuilder.Element parent, StructuredQueryBuilder.Element lat, StructuredQueryBuilder.Element lon);
    public StructuredQueryBuilder.GeospatialIndex geoPath(StructuredQueryBuilder.PathIndex pathIndex);
    public StructuredQueryDefinition geospatial(StructuredQueryBuilder.GeospatialIndex index, StructuredQueryBuilder.FragmentScope scope, String[] options, StructuredQueryBuilder.Region... regions);
    public StructuredQueryDefinition geospatial(StructuredQueryBuilder.GeospatialIndex index, StructuredQueryBuilder.Region... regions);
    public StructuredQueryDefinition geospatialConstraint(String constraintName, StructuredQueryBuilder.Region... regions);
    public IterableNamespaceContext getNamespaces();
    public StructuredQueryDefinition locks(StructuredQueryDefinition query);
    public StructuredQueryDefinition near(int distance, double weight, StructuredQueryBuilder.Ordering order, StructuredQueryDefinition... queries);
    public StructuredQueryDefinition near(StructuredQueryDefinition... queries);
    public StructuredQueryDefinition not(StructuredQueryDefinition query);
    public StructuredQueryDefinition notIn(StructuredQueryDefinition positive, StructuredQueryDefinition negative);
    public StructuredQueryDefinition or(StructuredQueryDefinition... queries);
    public StructuredQueryBuilder.PathIndex pathIndex(String path);
    public StructuredQueryBuilder.Region point(double latitude, double longitude);
    public StructuredQueryBuilder.Region polygon(StructuredQueryBuilder.Point... points);
    public StructuredQueryDefinition properties(StructuredQueryDefinition query);
    public StructuredQueryDefinition propertiesConstraint(String constraintName, StructuredQueryDefinition query);
    public StructuredQueryDefinition range(StructuredQueryBuilder.RangeIndex index, String type, String[] options, StructuredQueryBuilder.Operator operator, Object... values);
    public StructuredQueryDefinition range(StructuredQueryBuilder.RangeIndex index, String type, String collation, String[] options, StructuredQueryBuilder.Operator operator, Object... values);
    public StructuredQueryDefinition range(StructuredQueryBuilder.RangeIndex index, String type, String collation, StructuredQueryBuilder.FragmentScope scope, String[] options, StructuredQueryBuilder.Operator operator, Object... values);
    public StructuredQueryDefinition range(StructuredQueryBuilder.RangeIndex index, String type, String collation, StructuredQueryBuilder.FragmentScope scope, StructuredQueryBuilder.Operator operator, Object... values);
    public StructuredQueryDefinition range(StructuredQueryBuilder.RangeIndex index, String type, String collation, StructuredQueryBuilder.Operator operator, Object... values);
    public StructuredQueryDefinition range(StructuredQueryBuilder.RangeIndex index, String type, StructuredQueryBuilder.Operator operator, Object... values);
    public StructuredQueryDefinition rangeConstraint(String constraintName, StructuredQueryBuilder.Operator operator, String... values);
    public String[] rangeOptions(String... options);
    public void setNamespaces(IterableNamespaceContext namespaces);
    public StructuredQueryDefinition term(double weight, String... terms);
    public StructuredQueryDefinition term(String... terms);
    public StructuredQueryDefinition value(StructuredQueryBuilder.TextIndex index, String... values);
    public StructuredQueryDefinition value(StructuredQueryBuilder.TextIndex index, StructuredQueryBuilder.FragmentScope scope, String[] options, double weight, String... values);
    public StructuredQueryDefinition valueConstraint(String constraintName, double weight, String... values);
    public StructuredQueryDefinition valueConstraint(String constraintName, String... values);
    public StructuredQueryDefinition word(StructuredQueryBuilder.TextIndex index, String... words);
    public StructuredQueryDefinition word(StructuredQueryBuilder.TextIndex index, StructuredQueryBuilder.FragmentScope scope, String[] options, double weight, String... words);
    public StructuredQueryDefinition wordConstraint(String constraintName, double weight, String... words);
    public StructuredQueryDefinition wordConstraint(String constraintName, String... words);
}


