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

import com.marklogic.client.query.CombinedQueryBuilder;
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
 * <p>For methods which accept a "pojoProperty" argument and for {@link #geoProperty geoProperty}, we are refering to 
 * properties appropriate for 
 * <a href="http://docs.oracle.com/javase/tutorial/javabeans/">JavaBeans</a>,
 * including properties accessible via public getters and setters, or public fields.</p>
 *
 *
 * <p>Where StructuredQueryBuilder accepts StructuredQueryBuilder.TextIndex as a first argument
 * to 
 * {@link #value(StructuredQueryBuilder.TextIndex, String...) value(TextIndex, String...)} 
 * and 
 * {@link #word(StructuredQueryBuilder.TextIndex, String...) word(TextIndex, String...)}  
 * methods,
 * PojoQueryBuilder adds shortcut methods which accept as the first argument a String name of the 
 * pojoProperty. Similarly, PojoQueryBuilder accepts String pojoProperty arguments wherever 
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
 * have a geoPosition property which is an object with latitude and longitude pojoProperty's 
 * (which persist as elements) you might query them thusly:</p>
 * <pre>{@code    StructuredQueryBuilder sqb = new StructuredQueryBuilder();
 *    StructuredQueryBuilder.GeospatialIndex geoIdx = sqb.geoElementPair(
 *      sqb.element("geoPosition"), sqb.element("latitude"), sqb.element("longitude"));}</pre>
 *
 * <p>But if you use {@link PojoRepository} to persist your pojos with a latitude and longitude
 * pojoProperty's, you can query them more simply:</p>
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
 * <p>That is, you have a pojo class City with a property "country" of type
 * Country, you could query properties on the nested country thusly:</p>
 * <pre>{@code    PojoRepository<City, Integer> cities = 
 *      databaseClient.newPojoRepository(City.class, Integer.class);
 *    PojoQueryBuilder citiesQb = cities.getQueryBuilder();
 *    PojoQueryBuilder countriesQb = citiesQb.containerQuery("country");
 *    QueryDefinition query = countriesQb.value("continent", "EU"); }</pre>
 */
public interface PojoQueryBuilder<T> extends CombinedQueryBuilder {

    /** @return a query matching pojos of type T containing the pojoProperty with contents
     *          or children matching the specified query */
    public StructuredQueryDefinition containerQuery(String pojoProperty,
        StructuredQueryDefinition query);

    /** Use this method to provide a query builder that can query a nested object within your pojo.
     * All other PojoQueryBuilder methods create queries for direct children of T which are native
     * types.  If a child of T is an object, and you need to query one of its children, this method
     * provides you a query builder that is specific to that child object.  To query further levels of 
     * nested objects you may use this method on the each returned PojoQueryBuilder which represents
     * one level deeper.
     * @return a PojoQueryBuilder for nested pojos of the type corresponding with pojoProperty */
    public <C> PojoQueryBuilder<C> containerQueryBuilder(String pojoProperty, Class<C> clazz);
    public StructuredQueryBuilder.GeospatialIndex
        geoPair(String latitudePropertyName, String longitudePropertyName);
    /**
     * @param pojoProperty the name of a field or JavaBean property (accessed via getter or setter) on class T
     */
    public StructuredQueryBuilder.GeospatialIndex
        geoProperty(String pojoProperty);
    public StructuredQueryBuilder.GeospatialIndex
        geoPath(String pojoProperty);
    public StructuredQueryDefinition range(String pojoProperty,
        StructuredQueryBuilder.Operator operator, Object... values);
    public StructuredQueryDefinition range(String pojoProperty, String[] options,
        StructuredQueryBuilder.Operator operator, Object... values);
    public StructuredQueryDefinition value(String pojoProperty, String... values);
    public StructuredQueryDefinition value(String pojoProperty, Boolean value);
    public StructuredQueryDefinition value(String pojoProperty, Number... values);
    public StructuredQueryDefinition value(String pojoProperty, String[] options,
        double weight, String... values);
    public StructuredQueryDefinition value(String pojoProperty, String[] options,
        double weight, Boolean value);
    public StructuredQueryDefinition value(String pojoProperty, String[] options,
        double weight, Number... values);
    public StructuredQueryDefinition word(String pojoProperty, String... words);
    public StructuredQueryDefinition word(String pojoProperty, String[] options,
        double weight, String... words);
}

