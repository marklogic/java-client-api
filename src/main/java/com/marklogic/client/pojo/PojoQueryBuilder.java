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

import javax.xml.namespace.QName;

import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.util.IterableNamespaceContext;
 
/** Extends StructuredQueryBuilder with convenience methods specific to working with pojos.
 * The goal of {@link com.marklogic.client.pojo the pojo facade} is to simplify working with 
 * custom pojos. PojoQueryBuilder keeps all the powerful queries available via 
 * StructuredQueryBuilder while enabling queries across objects persisted using 
 * {@link PojoRepository}.
 *
 * <p>For methods which accept a "pojoField" argument and for {@link #geoField geoField}, we are refering to 
 * fields (or properties) appropriate for 
 * <a href="http://docs.oracle.com/javase/tutorial/javabeans/">JavaBeans</a>,
 * including fields accessible via public getters and setters, or public fields.</p>
 *
 *
 * <p>Where StructuredQueryBuilder accepts StructuredQueryBuilder.TextIndex as a first argument
 * to 
 * {@link #value(StructuredQueryBuilder.TextIndex, String...) value(TextIndex, String...)} 
 * and 
 * {@link #word(StructuredQueryBuilder.TextIndex, String...) word(TextIndex, String...)}  
 * methods,
 * PojoQueryBuilder adds shortcut methods which accept as the first argument a String name of the 
 * pojoField. Similarly, PojoQueryBuilder accepts String pojoField arguments wherever 
 * StructuredQueryBuilder accepts StructuredQueryBuilder.Element,
 * StructuredQueryBuilder.Attribute, and StructuredQueryBuilder.PathIndex
 * as arguments to 
 * {@link #geoAttributePair(StructuredQueryBuilder.Element, StructuredQueryBuilder.Attribute,
 *   StructuredQueryBuilder.Attribute)
 *   geoAttributePair(Element, Attribute, Attribute)}, 
 * {@link #geoElement(StructuredQueryBuilder.Element)
 *   geoElement(Element)}, 
 * {@link #geoElement(StructuredQueryBuilder.Element, StructuredQueryBuilder.Element)
 *   geoElement(Element, Element)}, 
 * {@link #geoElementPair(StructuredQueryBuilder.Element, StructuredQueryBuilder.Element,
 *   StructuredQueryBuilder.Element)
 *   geoElementPair(Element, Element, Element)}, 
 * {@link #geoPath(StructuredQueryBuilder.PathIndex)
 *   geoPath(PathIndex)}
 * </p>
 *
 * <p>Here are a couple examples.  Without the pojo facade you might persist your products using
 * {@link com.marklogic.client.io.JacksonDatabindHandle JacksonDatabindHandle} and query the
 * json property thusly:</p>
 * <pre>{@code    StructuredQueryBuilder sqb = new StructuredQueryBuilder();
 *    QueryDefinition query = sqb.value(sqb.jsonProperty("productId"), 12345);}</pre>
 *
 * <p>If you use {@link PojoRepository} to persist your products, you can query more simply:</p>
 * <pre>{@code    PojoQueryBuilder pqb = pojoRepository.getQueryBuilder();
 *    QueryDefinition query = pqb.value("productId", 12345);}</pre>
 *
 * <p>Similarly, without the pojo facade you might persist your pojos using 
 * {@link com.marklogic.client.io.JAXBHandle JAXBHandle} and if they
 * have a geoPosition field which is an object with latitude and longitude pojoFields 
 * (which persist as elements) you might query them thusly:</p>
 * <pre>{@code    StructuredQueryBuilder sqb = new StructuredQueryBuilder();
 *    StructuredQueryBuilder.GeospatialIndex geoIdx = sqb.geoElementPair(
 *      sqb.element("geoPosition"), sqb.element("latitude"), sqb.element("longitude"));}</pre>
 *
 * <p>But if you use {@link PojoRepository} to persist your pojos with a latitude and longitude
 * pojoFields, you can query them more simply:</p>
 * <pre>{@code    PojoQueryBuilder pqb = pojoRepository.getQueryBuilder();
 *    StructuredQueryBuilder.GeospatialIndex geoIdx = 
 *      pqb.geoPair("latitude", "longitude");}</pre>
 *
 * <p>As custom pojos may have nested pojos, PojoQueryBuilder also makes it easy to query
 * those nested pojos.  For example, if you had the following classes:</p>
 * <pre>    class City {
 *     {@literal @}Id int id;
 *      Country country;
 *      int getId();
 *      void setId(int id);
 *      Country getCountry();
 *      void setCountry(Country country);
 *    }
 *    class Country {
 *      String continent;
 *      String getContinent();
 *      void setContinent();
 *    }</pre>
 *
 * <p>That is, you have a pojo class City with a field "country" of type
 * Country, you could query fields on the nested country thusly:</p>
 * <pre>{@code    PojoRepository<City, Integer> cities = 
 *      databaseClient.newPojoRepository(City.class, Integer.class);
 *    PojoQueryBuilder citiesQb = cities.getQueryBuilder();
 *    PojoQueryBuilder countriesQb = citiesQb.containerQuery("country");
 *    QueryDefinition query = countriesQb.value("continent", "EU"); }</pre>
 */
public interface PojoQueryBuilder<T> {
    public StructuredQueryDefinition containerQuery(String pojoField,
        StructuredQueryDefinition query);
    public StructuredQueryDefinition containerQuery(StructuredQueryDefinition query);
    public PojoQueryBuilder<T>          containerQuery(String pojoField);
    public StructuredQueryBuilder.GeospatialIndex
        geoPair(String latitudeFieldName, String longitudeFieldName);
    /**
     * NOTE: Since the pojo facade abstracts away the persistence details, "field" here refers 
     * to a pojoField like all other convenience methods in PojoQueryBuilder,not
     * a MarkLogic field specified on the server.
     * @param pojoField the name of a field (or getter or setter) on class T
     */
    public StructuredQueryBuilder.GeospatialIndex
        geoField(String pojoField);
    public StructuredQueryBuilder.GeospatialIndex
        geoPath(String pojoField);
    public StructuredQueryDefinition range(String pojoField,
        StructuredQueryBuilder.Operator operator, Object... values);
    public StructuredQueryDefinition range(String pojoField, String[] options,
        StructuredQueryBuilder.Operator operator, Object... values);
    public StructuredQueryDefinition value(String pojoField, String... values);
    public StructuredQueryDefinition value(String pojoField, Boolean value);
    public StructuredQueryDefinition value(String pojoField, Number... values);
    public StructuredQueryDefinition value(String pojoField, String[] options,
        double weight, String... values);
    public StructuredQueryDefinition value(String pojoField, String[] options,
        double weight, Boolean value);
    public StructuredQueryDefinition value(String pojoField, String[] options,
        double weight, Number... values);
    public StructuredQueryDefinition word(String pojoField, String... words);
    public StructuredQueryDefinition word(String pojoField, String[] options,
        double weight, String... words);

    // All following method signatures copied from StructuredQueryBuilder since it has no interface we can extend
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

