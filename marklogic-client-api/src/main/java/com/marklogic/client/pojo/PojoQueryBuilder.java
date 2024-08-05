/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.pojo;

import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.pojo.annotation.GeospatialLatitude;
import com.marklogic.client.pojo.annotation.GeospatialLongitude;
import com.marklogic.client.pojo.annotation.GeospatialPathIndexProperty;
import com.marklogic.client.pojo.annotation.PathIndexProperty;
import com.marklogic.client.pojo.annotation.PathIndexProperty.ScalarType;
import com.marklogic.client.pojo.util.GenerateIndexConfig;

/** Specific to pojos yet similar to StructuredQueryBuilder, this class generates structured queries.
 * It adds convenience methods specific to working with pojos and does not replicate
 * StructuredQueryBuilder methods that don't make sense for pojos.
 * The goal of {@link com.marklogic.client.pojo the pojo facade} is to simplify working with
 * custom pojos. PojoQueryBuilder keeps all the powerful queries available via
 * StructuredQueryBuilder while enabling queries across objects persisted using
 * {@link PojoRepository}.
 *
 * <p>For methods which accept a "pojoProperty" argument we are refering to
 * properties appropriate for
 * <a href="http://docs.oracle.com/javase/tutorial/javabeans/">JavaBeans</a>,
 * including properties accessible via public getters and setters, or public fields.</p>
 *
 *
 * <p>Where StructuredQueryBuilder accepts StructuredQueryBuilder.TextIndex as a first argument
 * to
 * {@link StructuredQueryBuilder#value(StructuredQueryBuilder.TextIndex, String...) value(TextIndex, String...)}
 * and
 * {@link StructuredQueryBuilder#word(StructuredQueryBuilder.TextIndex, String...) word(TextIndex, String...)}
 * methods,
 * PojoQueryBuilder adds shortcut methods which accept as the first argument a String name of the
 * pojoProperty. Similarly, PojoQueryBuilder accepts String pojoProperty arguments wherever
 * StructuredQueryBuilder accepts StructuredQueryBuilder.Element,
 * StructuredQueryBuilder.Attribute, and StructuredQueryBuilder.PathIndex
 * as arguments to
 * {@link StructuredQueryBuilder#geoAttributePair(StructuredQueryBuilder.Element, StructuredQueryBuilder.Attribute,
 *   StructuredQueryBuilder.Attribute)
 *   geoAttributePair(Element, Attribute, Attribute)},
 * {@link StructuredQueryBuilder#geoElement(StructuredQueryBuilder.Element)
 *   geoElement(Element)},
 * {@link StructuredQueryBuilder#geoElement(StructuredQueryBuilder.Element, StructuredQueryBuilder.Element)
 *   geoElement(Element, Element)},
 * {@link StructuredQueryBuilder#geoElementPair(StructuredQueryBuilder.Element, StructuredQueryBuilder.Element,
 *   StructuredQueryBuilder.Element)
 *   geoElementPair(Element, Element, Element)},
 * {@link StructuredQueryBuilder#geoPath(StructuredQueryBuilder.PathIndex)
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
 *    PojoQueryBuilder<City> citiesQb = cities.getQueryBuilder();
 *    PojoQueryBuilder<Country> countriesQb = citiesQb.containerQueryBuilder("country", Country.class);
 *    QueryDefinition query = countriesQb.value("continent", "EU"); }</pre>
 */
public interface PojoQueryBuilder<T> {

  /** Copied directly from  {@link com.marklogic.client.query.StructuredQueryBuilder.Operator}.
   * A comparison operator for use in range queries.
   */
  public enum Operator {
    LT, LE, GT, GE, EQ, NE;
  }

  /**
   * @param pojoProperty the property container to match against
   * @param query the query to match within the container
   * @return a query matching pojos of type T containing the pojoProperty with contents
   *          or children matching the specified query
   */
  StructuredQueryDefinition containerQuery(String pojoProperty,
                                           StructuredQueryDefinition query);

  /** Use this method to provide a query builder that can query a nested object within your pojo.
   * All other PojoQueryBuilder methods create queries for direct children of T which are native
   * types.  If a child of T is a pojo, and you need to query one of its children, this method
   * provides you a query builder that is specific to that child object.  To query further levels of
   * nested objects you may use this method on the each returned PojoQueryBuilder which represents
   * one level deeper.
   * @param pojoProperty the name of a field or JavaBean property (accessed via getter or setter) on class T
   *     that is a pojo of type clazz
   * @param clazz the class type of the nested pojo
   * @param <C> the type of the class contained by pojoProperty
   * @return a PojoQueryBuilder for nested pojos of the type corresponding with pojoProperty
   */
  <C> PojoQueryBuilder<C> containerQueryBuilder(String pojoProperty, Class<C> clazz);
  /**
   * For use in a {@link #geospatial geospatial} query, reference a pair of properties.  These properties
   * should ideally have Geospatial Element Pair Indexes configured in the database.  For help
   * creating these indexes, see {@link GenerateIndexConfig}, {@link GeospatialLatitude}, and
   * {@link GeospatialLongitude}.
   * @param latitudePropertyName the name of a field or JavaBean property (accessed via getter or setter)
   *     ideally annotated with {@literal @}{@link GeospatialLatitude}
   * @param longitudePropertyName the name of a field or JavaBean property (accessed via getter or setter)
   *     ideally annotated with {@literal @}{@link GeospatialLongitude}
   * @return the geospatial element pair range query
   */
  StructuredQueryBuilder.GeospatialIndex
  geoPair(String latitudePropertyName, String longitudePropertyName);
    /* no reason to expose geoProperty for now because it's redundant with geoPath
    public StructuredQueryBuilder.GeospatialIndex
        geoProperty(String pojoProperty);
    */
  /**
   * For use in a {@link #geospatial geospatial} query, reference a geo property which has
   * a corresponding Geospatial Path Range Index configured in the database.   For help
   * creating this index, see {@link GenerateIndexConfig} and {@link GeospatialPathIndexProperty}.
   * @param pojoProperty the name of a field or JavaBean property (accessed via getter or setter) on class T
   *     ideally annotated with {@literal @}{@link GeospatialPathIndexProperty}
   * @return the geospatial path range query
   */
  StructuredQueryBuilder.GeospatialIndex geoPath(String pojoProperty);
  /**
   * Query a Path Range Index configured in the database for a pojo property.  Make sure the datatype for
   * your values parameter match the {@link ScalarType datatype} configured.  For help
   * creating this index, see {@link GenerateIndexConfig} and {@link PathIndexProperty}.
   * @param pojoProperty the name of a field or JavaBean property (accessed via getter or setter) on class T
   *     annotated with {@literal @}{@link PathIndexProperty}
   * @param operator the operator used to compare property values with passed values
   * @param values the possible datatyped values for the comparison.  Make sure the datatypes
   *     match the {@link ScalarType datatype} configured.
   * @return the range query
   */
  StructuredQueryDefinition range(String pojoProperty,
                                  PojoQueryBuilder.Operator operator, Object... values);
  /**
   * Query a Path Range Index configured in the database for a pojo property.  Make sure the datatype for
   * your values parameter match the {@link ScalarType datatype} configured.  For help
   * creating this index, see {@link GenerateIndexConfig} and {@link PathIndexProperty}.
   * @param pojoProperty the name of a field or JavaBean property (accessed via getter or setter) on class T
   *     annotated with {@literal @}{@link PathIndexProperty}
   * @param options
   *     <a href="http://docs.marklogic.com/cts:path-range-query#options">options</a> for fine tuning the query
   * @param operator the operator used to compare property values with passed values
   * @param values the possible datatyped values for the comparison.  Make sure the datatypes
   *     match the {@link ScalarType datatype} configured.
   * @return the range query
   */
  StructuredQueryDefinition range(String pojoProperty, String[] options,
                                  PojoQueryBuilder.Operator operator, Object... values);
  /**
   * Filter search results by properties matching specified values.
   * @param pojoProperty the name of a field or JavaBean property (accessed via getter or setter) on class T
   * @param values match a persisted pojo of type T if it has the property with any of the values
   * @return the value query
   */
  StructuredQueryDefinition value(String pojoProperty, String... values);
  /**
   * Filter search results by properties matching specified value.
   * @param pojoProperty the name of a field or JavaBean property (accessed via getter or setter) on class T
   * @param value match a persisted pojo of type T if it has the property with the boolean value
   * @return the value query
   */
  StructuredQueryDefinition value(String pojoProperty, Boolean value);
  /**
   * Filter search results by properties matching specified values.
   * @param pojoProperty the name of a field or JavaBean property (accessed via getter or setter) on class T
   * @param values match a persisted pojo of type T if it has the property with any of the numeric values
   * @return the value query
   */
  StructuredQueryDefinition value(String pojoProperty, Number... values);
  /**
   * Filter search results by properties matching specified values.
   * @param pojoProperty the name of a field or JavaBean property (accessed via getter or setter) on class T
   * @param options
   *     <a href="http://docs.marklogic.com/cts:json-property-value-query#options">options</a> for fine tuning the query
   * @param weight the multiplier for the match in the document ranking
   * @param values match a persisted pojo of type T if it has the property with any of the values
   * @return the value query
   */
  StructuredQueryDefinition value(String pojoProperty, String[] options,
                                  double weight, String... values);
  /**
   * Filter search results by properties matching specified values.
   * @param pojoProperty the name of a field or JavaBean property (accessed via getter or setter) on class T
   * @param options
   *     <a href="http://docs.marklogic.com/cts:json-property-value-query#options">options</a> for fine tuning the query
   * @param weight the multiplier for the match in the document ranking
   * @param value match a persisted pojo of type T if it has the property with the boolean value
   * @return the value query
   */
  StructuredQueryDefinition value(String pojoProperty, String[] options,
                                  double weight, Boolean value);
  /**
   * Filter search results by properties matching specified values.
   * @param pojoProperty the name of a field or JavaBean property (accessed via getter or setter) on class T
   * @param options
   *     <a href="http://docs.marklogic.com/cts:json-property-value-query#options">options</a> for fine tuning the query
   * @param weight the multiplier for the match in the document ranking
   * @param values match a persisted pojo of type T if it has the property with any of the numeric values
   * @return the value query
   */
  StructuredQueryDefinition value(String pojoProperty, String[] options,
                                  double weight, Number... values);
  /**
   * Filter search results by properties with at least one of the specified words or phrases.
   * @param pojoProperty the name of a field or JavaBean property (accessed via getter or setter) on class T
   * @param words match a persisted pojo of type T if it has the property with any of the words or phrases
   * @return the word query
   */
  StructuredQueryDefinition word(String pojoProperty, String... words);
  /**
   * Filter search results by properties with at least one of the specified words or phrases.
   * @param pojoProperty the name of a field or JavaBean property (accessed via getter or setter) on class T
   * @param options
   *     <a href="http://docs.marklogic.com/cts:word-query#options">options</a> for fine tuning the query
   * @param weight the multiplier for the match in the document ranking
   * @param words match a persisted pojo of type T if it has the property with any of the words or phrases
   * @return the word query
   */
  StructuredQueryDefinition word(String pojoProperty, String[] options,
                                 double weight, String... words);
  /** Copied directly from  {@link StructuredQueryBuilder#and StructuredQuerybuilder.and}.
   * Defines an AND query over the list of query definitions.
   * @param queries	the query definitions
   * @return	the StructuredQueryDefinition for the AND query
   */
  StructuredQueryDefinition and(StructuredQueryDefinition... queries);
  /** Copied directly from  {@link StructuredQueryBuilder#andNot StructuredQuerybuilder.andNot}.
   * Defines an AND NOT query combining a positive and negative
   * query. You can use an AND or OR query over a list of query
   * definitions as the positive or negative query.
   * @param positive	the positive query definition
   * @param negative	the negative query definition
   * @return	the StructuredQueryDefinition for the AND NOT query
   */
  StructuredQueryDefinition andNot(StructuredQueryDefinition positive, StructuredQueryDefinition negative);
  /** Copied directly from  {@link StructuredQueryBuilder#boost StructuredQuerybuilder.boost}.
   * Defines a boost query for the matching and boosting query definitions.  The matching
   * or boosting query definitions can each be an AND or OR query definition for complex
   * combinations of criteria.
   * @param matchingQuery the query definition that filters documents
   * @param boostingQuery	the query definition that increases the rank for some filtered documents
   * @return	the StructuredQueryDefinition for the boost query
   */
  StructuredQueryDefinition boost(StructuredQueryDefinition matchingQuery, StructuredQueryDefinition boostingQuery);
  /** Copied directly from  {@link StructuredQueryBuilder#box StructuredQuerybuilder.box}.
   * Specifies a geospatial region as a box, supplying
   * the coordinates for the perimeter.
   * @param south	the latitude of the south coordinate
   * @param west	the longitude of the west coordinate
   * @param north	the latitude of the north coordinate
   * @param east	the longitude of the east coordinate
   * @return	the definition of the box
   */
  StructuredQueryBuilder.Region box(double south, double west, double north, double east);
  /** Copied directly from  {@link StructuredQueryBuilder#build StructuredQuerybuilder.build}.
   * Builds a structured query in XML from the list of query definitions.
   * The structured query can be passed to the search() method of QueryManager.
   * @param queries	the query definitions
   * @return	the structured query
   */
  RawStructuredQueryDefinition build(StructuredQueryDefinition... queries);
  /** Copied directly from  {@link StructuredQueryBuilder#circle(double, double, double) StructuredQuerybuilder.circle(double, double, double)}.
   * Specifies a geospatial region as a circle,
   * supplying coordinates for the center.
   * @param latitude	the latitude coordinate of the center
   * @param longitude	the longitude coordinate of the center
   * @param radius	the radius of the circle
   * @return	the definition of the circle
   */
  StructuredQueryBuilder.Region circle(double latitude, double longitude, double radius);
  /** Copied directly from  {@link StructuredQueryBuilder#circle(StructuredQueryBuilder.Point, double) StructuredQuerybuilder.circle(StructuredQueryBuilder.Point, double)}.
   * Specifies a geospatial region as a circle,
   * supplying a point for the center.
   * @param center	the point defining the center
   * @param radius	the radius of the circle
   * @return	the definition of the circle
   */
  StructuredQueryBuilder.Region circle(StructuredQueryBuilder.Point center, double radius);
  /** Copied directly from  {@link StructuredQueryBuilder#collection(String...) StructuredQuerybuilder.collection(String...)}.
   * Matches documents belonging to at least one
   * of the criteria collections.
   * @param uris	the identifiers for the criteria collections
   * @return	the StructuredQueryDefinition for the collection query
   */
  StructuredQueryDefinition collection(String... uris);
  /** Copied from {@link StructuredQueryBuilder#geospatial(StructuredQueryBuilder.GeospatialIndex, StructuredQueryBuilder.FragmentScope, String[], StructuredQueryBuilder.Region...) StructuredQuerybuilder.geospatial(StructuredQueryBuilder.GeospatialIndex, StructuredQueryBuilder.FragmentScope, String[], StructuredQueryBuilder.Region...)} but without StructuredQueryBuilder.FragmentScope.
   * Matches an element, element pair, element attribute, pair, or path
   * specifying a geospatial point that appears within one of the criteria regions.
   * @param index	the container for the coordinates of the geospatial point
   * @param options	options for fine tuning the query
   * @param regions	the possible regions containing the point
   * @return	the StructuredQueryDefinition for the geospatial query
   */
  StructuredQueryDefinition geospatial(StructuredQueryBuilder.GeospatialIndex index, String[] options, StructuredQueryBuilder.Region... regions);
  /** Copied directly from  {@link StructuredQueryBuilder#geospatial(StructuredQueryBuilder.GeospatialIndex, StructuredQueryBuilder.Region...) StructuredQuerybuilder.geospatial(StructuredQueryBuilder.GeospatialIndex, StructuredQueryBuilder.Region...)}.
   * Matches an element, element pair, element attribute, pair, or path
   * specifying a geospatial point that appears within one of the criteria regions.
   * @param index	the container for the coordinates of the geospatial point
   * @param regions	the possible regions containing the point
   * @return	the StructuredQueryDefinition for the geospatial query
   */
  StructuredQueryDefinition geospatial(StructuredQueryBuilder.GeospatialIndex index, StructuredQueryBuilder.Region... regions);
  /** Copied directly from  {@link StructuredQueryBuilder#near(int, double, StructuredQueryBuilder.Ordering, StructuredQueryDefinition...) StructuredQuerybuilder.near(int, double, StructuredQueryBuilder.Ordering, StructuredQueryDefinition...)}.
   * Defines a NEAR query over the list of query definitions
   * with specified parameters.
   * @param distance	the proximity for the query terms
   * @param weight	the weight for the query
   * @param order	the ordering for the query terms
   * @param queries	the query definitions
   * @return	the StructuredQueryDefinition for the NEAR query
   */
  StructuredQueryDefinition near(int distance, double weight, StructuredQueryBuilder.Ordering order, StructuredQueryDefinition... queries);
  /** Copied directly from  {@link StructuredQueryBuilder#near(StructuredQueryDefinition...) StructuredQuerybuilder.near(StructuredQueryDefinition...)}.
   * Defines a NEAR query over the list of query definitions
   * with default parameters.
   * @param queries	the query definitions
   * @return	the StructuredQueryDefinition for the NEAR query
   */
  StructuredQueryDefinition near(StructuredQueryDefinition... queries);
  /** Copied directly from  {@link StructuredQueryBuilder#not StructuredQuerybuilder.not}.
   * Defines a NOT query for a query definition. To negate
   * a list of query definitions, define an AND or
   * OR query over the list and define the NOT query over
   * the AND or OR query.
   * @param query	the query definition
   * @return	the StructuredQueryDefinition for the NOT query
   */
  StructuredQueryDefinition not(StructuredQueryDefinition query);
  /** Copied directly from  {@link StructuredQueryBuilder#notIn StructuredQuerybuilder.and}.
   * Defines a not-in query for the positive and negative query definitions.  These query definitions
   * can each be an AND or OR query definition for complex combinations of criteria.
   * @param positive the query definition that includes documents
   * @param negative the query definition that excludes documents
   * @return	the StructuredQueryDefinition for the not-in query
   */
  StructuredQueryDefinition notIn(StructuredQueryDefinition positive, StructuredQueryDefinition negative);
  /** Copied directly from  {@link StructuredQueryBuilder#or StructuredQuerybuilder.and}.
   * Defines an OR query over the list of query definitions.
   * @param queries	the query definitions
   * @return	the StructuredQueryDefinition for the OR query
   */
  StructuredQueryDefinition or(StructuredQueryDefinition... queries);
  /** Copied directly from  {@link StructuredQueryBuilder#point StructuredQuerybuilder.point}.
   * Specifies a geospatial point.
   * @param latitude	the latitude coordinate
   * @param longitude	the longitude coordinate
   * @return	the definition of the point
   */
  StructuredQueryBuilder.Region point(double latitude, double longitude);
  /** Copied directly from  {@link StructuredQueryBuilder#polygon StructuredQuerybuilder.polygon}.
   * Specifies a geospatial region as an arbitrary polygon.
   * @param points	the list of points defining the perimeter of the region
   * @return	the definition of the polygon
   */
  StructuredQueryBuilder.Region polygon(StructuredQueryBuilder.Point... points);
  /** Copied directly from  {@link StructuredQueryBuilder#term(double, String...) StructuredQuerybuilder.term(double, String...)}.
   * Matches documents containing the specified terms, modifying
   * the contribution of the match to the score with the weight.
   * @param weight	the multiplier for the match in the document ranking
   * @param terms	the possible terms to match
   * @return	the StructuredQueryDefinition for the term query
   */
  StructuredQueryDefinition term(double weight, String... terms);
  /** Copied directly from  {@link StructuredQueryBuilder#term(String...) StructuredQuerybuilder.term(String...)}.
   * Matches documents containing the specified terms.
   * @param terms	the possible terms to match
   * @return	the StructuredQueryDefinition for the term query
   */
  StructuredQueryDefinition term(String... terms);
  /** Wraps the structured query into a combined query with options containing
   * <a href="https://docs.marklogic.com/guide/search-dev/appendixa#id_77801">
   *     &lt;search-option&gt;filtered&lt;/search-option&gt;</a> so results are accurate
   * though slower.
   *
   * @param query the query to mark as filtered
   * @return a QueryDefinition that can be used with PojoRepository.search()
   *         (a REST combined query under the hood)
   */
  PojoQueryDefinition filteredQuery(StructuredQueryDefinition query);
}
