/*
 * Copyright (c) 2023 MarkLogic Corporation
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

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.impl.AbstractQueryDefinition;
import com.marklogic.client.impl.RawQueryDefinitionImpl;
import com.marklogic.client.impl.XmlFactories;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.OperationNotSupported;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;
import com.marklogic.client.util.EditableNamespaceContext;
import com.marklogic.client.util.IterableNamespaceContext;
import jakarta.xml.bind.DatatypeConverter;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Templates;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * StructuredQueryBuilder builds a query for documents in the database.
 */
public class StructuredQueryBuilder {
  public static final String SEARCH_API_NS="http://marklogic.com/appservices/search";

  /*
   * This map is used to prevent reuse of reserved prefixes in path expressions.
   */
  final static private Map<String,String> reserved = new HashMap<>();
  static {
    reserved.put("search", SEARCH_API_NS);
    reserved.put("xsi",  XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
    reserved.put("xs",   XMLConstants.W3C_XML_SCHEMA_NS_URI);
  };

  private static Templates extractor;

  private String builderOptionsURI = null;
  /**
   * Used only for serializing StructuredQueryDefinitions.
   */
  private IterableNamespaceContext namespaces;

  /**
   * Control over ordering for use in near queries.
   */
  public enum Ordering {
    ORDERED, UNORDERED;
  }

  /**
   * Geospatial operators to be used in geospatial region index queries
   */
  public enum GeospatialOperator {
    EQUALS, DISJOINT, TOUCHES, CONTAINS, COVERS,
    INTERSECTS, WITHIN, COVEREDBY, CROSSES, OVERLAPS;

    @Override
    public String toString() {
      switch(this) {
        case COVEREDBY:
          return "covered-by";
        default:
          return this.name().toLowerCase();
      }
    }
  }
  /**
   * A comparison operator for use in range queries.
   */
  public enum Operator {
    LT, LE, GT, GE, EQ, NE;
  }

  /**
   * Whether a query should search the document or its associated properties.
   */
  public enum FragmentScope {
    DOCUMENTS, PROPERTIES;
  }

  /**
   * CoordinateSystem provides a list of all known coordinate systems and it
   * also provides a capability to add new CoordinateSystems in the future
   */
  public static final class CoordinateSystem {
    private static ConcurrentMap<String, CoordinateSystem> coordMap = new ConcurrentHashMap<String, CoordinateSystem>();
    private String coordinateSystem;
    private boolean doublePrecision = false;

    /**
     * Coordinate System mapping to "wgs84"
     */
    public static final CoordinateSystem WGS84 = new CoordinateSystem("wgs84");
    /**
     * Coordinate System mapping to "wgs84/double"
     */
    public static final CoordinateSystem WGS84DOUBLE = new CoordinateSystem("wgs84", true);
    /**
     * Coordinate System mapping to "etrs89"
     */
    public static final CoordinateSystem ETRS89 = new CoordinateSystem("etrs89");
    /**
     * Coordinate System mapping to "etrs89/double"
     */
    public static final CoordinateSystem ETRS89DOUBLE = new CoordinateSystem("etrs89", true);
    /**
     * Coordinate System mapping to "raw"
     */
    public static final CoordinateSystem RAW = new CoordinateSystem("raw");
    /**
     * Coordinate System mapping to "raw/double"
     */
    public static final CoordinateSystem RAWDOUBLE = new CoordinateSystem("raw", true);

    private CoordinateSystem(String coordinateSystem) {
      this.coordinateSystem = coordinateSystem;
    }
    private CoordinateSystem(String coordinateSystem, boolean isDoublePrecision) {
      this(coordinateSystem);
      this.doublePrecision = isDoublePrecision;
    }

    /**
     * This method creates a CoordinateSystem with the specified string
     * and sets doublePrecision to false
     * @param coordinateSystem the name of the CoordinateSystem like wgs84, etrs89
     * @return the instance of CoordinateSystem created/already present
     */
    public static CoordinateSystem getOther(String coordinateSystem) {
      return getOther(coordinateSystem, false);
    }

    /**
     * This method creates a CoordinateSystem with the specified string
     * and sets doublePrecision to the specified isDoublePrecision boolean
     * @param coordinateSystem the name of the CoordinateSystem like wgs84, etrs89
     * @param isDoublePrecision the value that should be set for doublePrecision
     * @return the instance of CoordinateSystem created/already present
     */
    public static CoordinateSystem getOther(String coordinateSystem, boolean isDoublePrecision) {
      String key = coordinateSystem+isDoublePrecision;
      coordMap.putIfAbsent(key, new CoordinateSystem(coordinateSystem, isDoublePrecision));
      return coordMap.get(key);
    }

    public String getCoordinateSystem() {
      return coordinateSystem;
    }

    public boolean getDoublePrecision() {
      return doublePrecision;
    }

    public String toString() {
      if(doublePrecision) {
        return coordinateSystem+"/double";
      } else return coordinateSystem;
    }
  }

  /**
   * A TextIndex can be used for word and value queries.
   */
  public interface TextIndex {
  }
  /**
   * A RangeIndex can be used for range queries.  The range index
   * must be defined in the database configuration.
   */
  public interface RangeIndex {
  }
  /**
   * A GeospatialIndex can be used for geospatial queries.  The
   * geospatial index must be defined in the database configuration.
   */
  public interface GeospatialIndex {
  }
  /**
   * A GeospatialRangeIndex can be used for geospatial region index queries. The
   * geospatial region index must be defined in the database configuration.
   */
  public interface GeospatialRegionIndex {
  }
  /**
   * A ContainerIndex can be used for container queries.
   */
  public interface ContainerIndex {
  }

  /**
   * An Element represents an element in database documents.
   */
  public interface Element extends ContainerIndex, RangeIndex, TextIndex {
  }
  /**
   * An Attribute represents an attribute in database documents.
   */
  public interface Attribute {
  }
  /**
   * An ElementAttribute represents an attribute on an element
   * in database documents.
   */
  public interface ElementAttribute extends RangeIndex, TextIndex {
  }
  /**
   * A Field represents a field defined in the database configuration.
   */
  public interface Field extends RangeIndex, TextIndex {
  }
  /**
   * A JSONProperty represents a key in JSON database documents.
   */
  public interface JSONProperty extends Element, ContainerIndex, RangeIndex, TextIndex {
  }
  /**
   * A PathIndex represents an index defined with an XPath
   * in the database configuration.
   */
  public interface PathIndex extends RangeIndex {
  }

  /**
   * Zero-argument constructor.
   */
  public StructuredQueryBuilder() {
    this((IterableNamespaceContext) null);
  }
  /**
   * Constructs a query builder for queries against the options
   * persisted with the specified name.  The query can include
   * constraint queries for any constraints defined by the options.
   * @param optionsName    the name of the persisted query options
   */
  public StructuredQueryBuilder(String optionsName) {
    this((IterableNamespaceContext) null);
    builderOptionsURI = optionsName;
  }
  /**
   * Constructs a query builder for queries using the specified
   * namespace bindings.
   * @param namespaces    the bindings of prefixes and namespaces
   */
  public StructuredQueryBuilder(IterableNamespaceContext namespaces) {
    super();
    setNamespaces(namespaces);
  }
  /**
   * Constructs a query builder for queries against the options
   * persisted with the specified name using the specified
   * namespace bindings.  The query can include constraint queries
   * for any constraints defined by the options.
   * @param optionsName    the name of the persisted query options
   * @param namespaces    the bindings of prefixes and namespaces
   */
  public StructuredQueryBuilder(String optionsName, IterableNamespaceContext namespaces) {
    this(namespaces);
    builderOptionsURI = optionsName;
  }

  /**
   * Builds a structured query in XML from the list of query definitions.
   * The structured query can be passed to the search() method of QueryManager.
   * @param queries    the query definitions
   * @return    the structured query
   */
  public RawStructuredQueryDefinition build(StructuredQueryDefinition... queries) {
    checkQueries(queries);
    return new RawQueryDefinitionImpl.Structured(
      new StructuredQueryXMLWriter(queries), builderOptionsURI
    );
  }

  /**
   * Defines an AND query over the list of query definitions.
   * @param queries    the query definitions
   * @return    the StructuredQueryDefinition for the AND query
   */
  public StructuredQueryDefinition and(StructuredQueryDefinition... queries) {
    checkQueries(queries);
    return new AndQuery(queries);
  }

  /**
   * Defines an OR query over the list of query definitions.
   * @param queries    the query definitions
   * @return    the StructuredQueryDefinition for the OR query
   */
  public StructuredQueryDefinition or(StructuredQueryDefinition... queries) {
    checkQueries(queries);
    return new OrQuery(queries);
  }

  /**
   * Defines a NOT query for a query definition. To negate
   * a list of query definitions, define an AND or
   * OR query over the list and define the NOT query over
   * the AND or OR query.
   * @param query    the query definition
   * @return    the StructuredQueryDefinition for the NOT query
   */
  public StructuredQueryDefinition not(StructuredQueryDefinition query) {
    checkQuery(query);
    return new NotQuery(query);
  }

  /**
   * Defines an AND NOT query combining a positive and negative
   * query. You can use an AND or OR query over a list of query
   * definitions as the positive or negative query.
   * @param positive    the positive query definition
   * @param negative    the negative query definition
   * @return    the StructuredQueryDefinition for the AND NOT query
   */
  public StructuredQueryDefinition andNot(StructuredQueryDefinition positive, StructuredQueryDefinition negative) {
    checkQuery(positive);
    checkQuery(negative);
    return new AndNotQuery(positive, negative);
  }

  /**
   * Defines a NEAR query over the list of query definitions
   * with default parameters.
   * @param queries    the query definitions
   * @return    the StructuredQueryDefinition for the NEAR query
   */
  public StructuredQueryDefinition near(StructuredQueryDefinition... queries) {
    checkQueries(queries);
    return new NearQuery(queries);
  }

  /**
   * Defines a NEAR query over the list of query definitions
   * with specified parameters.
   * @param maximumDistance the maximum distance (in number of words) between any two matching
   *   queries
   * @param weight    the weight for the query
   * @param order    the ordering for the query terms
   * @param queries    the query definitions
   * @return    the StructuredQueryDefinition for the NEAR query
   */
  public StructuredQueryDefinition near(int maximumDistance, double weight, Ordering order,
    StructuredQueryDefinition... queries)
  {
    checkQueries(queries);
    return new NearQuery(null, maximumDistance, weight, order, queries);
  }

  /**
   * Defines a NEAR query over the list of query definitions
   * with specified parameters.
   * @param minimumDistance the minimum distance (in number of words) between any two matching
   *   queries
   * @param maximumDistance the maximum distance (in number of words) between any two matching
   *   queries
   * @param weight    the weight for the query
   * @param order    the ordering for the query terms
   * @param queries    the query definitions
   * @return    the StructuredQueryDefinition for the NEAR query
   */
  public StructuredQueryDefinition near(int minimumDistance, int maximumDistance, double weight,
    Ordering order, StructuredQueryDefinition...  queries)
  {
    checkQueries(queries);
    return new NearQuery(minimumDistance, maximumDistance, weight, order, queries);
  }

  /**
   * Associates a query with the content of documents (as opposed to
   * the properties of documents).
   * @param query    the query definition
   * @return    the StructuredQueryDefinition for the document fragment query
   */
  public StructuredQueryDefinition documentFragment(StructuredQueryDefinition query) {
    checkQuery(query);
    return new DocumentFragmentQuery(query);
  }

  /**
   * Associates a query with the properties of documents (as opposed to
   * the content of documents).
   * @param query    the query definition
   * @return    the StructuredQueryDefinition for the properties query
   */
  public StructuredQueryDefinition properties(StructuredQueryDefinition query) {
    checkQuery(query);
    return new PropertiesQuery(query);
  }

  /**
   * Associates a query with durable locks on documents (as opposed to
   * the content or properties of documents). Such lock fragments are
   * created with xdmp:lock-acquire().
   * @param query    the query definition
   * @return    the StructuredQueryDefinition for the locks query
   */
  public StructuredQueryDefinition locks(StructuredQueryDefinition query) {
    checkQuery(query);
    return new LocksQuery(query);
  }

  /**
   * Matches a query within the substructure contained by an element or JSON property.
   * @param index    the element or JSON property
   * @param query    the query over the contained substructure
   * @return    the StructuredQueryDefinition for the container query
   */
  public StructuredQueryDefinition containerQuery(ContainerIndex index, StructuredQueryDefinition query) {
    checkQuery(query);
    return new ContainerQuery(index, query);
  }

  /**
   * Matches documents belonging to at least one
   * of the criteria collections.
   * @param uris    the identifiers for the criteria collections
   * @return    the StructuredQueryDefinition for the collection query
   */
  public StructuredQueryDefinition collection(String... uris) {
    return new CollectionQuery(uris);
  }

  /**
   * Matches documents at the specified depth within at least one
   * of the criteria directories.
   * @param isInfinite    true to match a document at any level of depth
   * @param uris    the identifiers for the criteria directories
   * @return    the StructuredQueryDefinition for the directory query
   */
  public StructuredQueryDefinition directory(boolean isInfinite, String... uris) {
    return new DirectoryQuery(isInfinite, uris);
  }

  /**
   * Matches the specified documents.
   * @param uris    the identifiers for the documents
   * @return    the StructuredQueryDefinition for the document query
   */
  public StructuredQueryDefinition document(String... uris) {
    return new DocumentQuery(uris);
  }


  /**
   * Matches documents containing the specified terms.
   * @param terms    the possible terms to match
   * @return    the StructuredQueryDefinition for the term query
   */
  public StructuredQueryDefinition term(String... terms) {
    return new TermQuery(null, terms);
  }
  /**
   * Matches documents containing the specified terms, modifying
   * the contribution of the match to the score with the weight.
   * @param weight    the multiplier for the match in the document ranking
   * @param terms    the possible terms to match
   * @return    the StructuredQueryDefinition for the term query
   */
  public StructuredQueryDefinition term(double weight, String... terms) {
    return new TermQuery(weight, terms);
  }

  /**
   * Matches an element, attribute, JSON property, or field
   * that has a value with the same string value as at least one
   * of the criteria values.
   * @param index    the value container
   * @param values    the possible values to match
   * @return    the StructuredQueryDefinition for the value query
   */
  public StructuredQueryDefinition value(TextIndex index, String... values) {
    return new ValueQuery(index, null, null, null, values);
  }
  /**
   * Matches a JSON property that has a value with the same boolean value
   * as at least one of the criteria values.  Note this method will not match
   * any XML node.
   * @param index    the value container
   * @param value    either true or false
   * @return    the StructuredQueryDefinition for the value query
   */
  public StructuredQueryDefinition value(TextIndex index, Boolean value) {
    return new ValueQuery(index, null, null, null, new Object[] {value});
  }
  /**
   * Matches an JSON property that has a value with the same numeric
   * value as at least one of the criteria values.  Note this method will not
   * match any XML node.
   * @param index    the value container
   * @param values    the possible values to match
   * @return    the StructuredQueryDefinition for the value query
   */
  public StructuredQueryDefinition value(TextIndex index, Number... values) {
    return new ValueQuery(index, null, null, null, values);
  }
  /**
   * Matches an element, attribute, JSON property, or field
   * that has a value with the same string value as at least one
   * of the criteria values.
   * @param index    the value container
   * @param scope    whether the query matches the document content or properties
   * @param options    options for fine tuning the query
   * @param weight    the multiplier for the match in the document ranking
   * @param values    the possible values to match
   * @return    the StructuredQueryDefinition for the value query
   */
  public StructuredQueryDefinition value(TextIndex index, FragmentScope scope, String[] options, double weight, String... values) {
    return new ValueQuery(index, scope, options, weight, values);
  }
  /**
   * Matches a JSON property that has a value with the same boolean
   * value as at least one of the criteria values.  Note this method will not
   * match any XML node.
   * @param index    the value container
   * @param scope    whether the query matches the document content or properties
   * @param options    options for fine tuning the query
   * @param weight    the multiplier for the match in the document ranking
   * @param value        either true or false
   * @return    the StructuredQueryDefinition for the value query
   */
  public StructuredQueryDefinition value(TextIndex index, FragmentScope scope, String[] options, double weight, Boolean value) {
    return new ValueQuery(index, scope, options, weight, new Object[] {value});
  }
  /**
   * Matches a JSON property that has a value with the same numeric
   * value as at least one of the criteria values.  Note this method will not
   * match any XML node.
   * @param index    the value container
   * @param scope    whether the query matches the document content or properties
   * @param options    options for fine tuning the query
   * @param weight    the multiplier for the match in the document ranking
   * @param values    the possible values to match
   * @return    the StructuredQueryDefinition for the value query
   */
  public StructuredQueryDefinition value(TextIndex index, FragmentScope scope, String[] options, double weight, Number... values) {
    return new ValueQuery(index, scope, options, weight, values);
  }

  /**
   * Matches an element, attribute, JSON property, or field
   * that has at least one of the criteria words.
   * @param index    the word container
   * @param words    the possible words to match
   * @return    the StructuredQueryDefinition for the word query
   */
  public StructuredQueryDefinition word(TextIndex index, String... words) {
    return new WordQuery(index, null, null, null, words);
  }
  /**
   * Matches an element, attribute, JSON property, or field
   * that has at least one of the criteria words.
   * @param index    the word container
   * @param scope    whether the query matches the document content or properties
   * @param options    options for fine tuning the query
   * @param weight    the multiplier for the match in the document ranking
   * @param words    the possible words to match
   * @return    the StructuredQueryDefinition for the word query
   */
  public StructuredQueryDefinition word(TextIndex index, FragmentScope scope,
                                        String[] options, double weight, String... words) {
    return new WordQuery(index, scope, options, weight, words);
  }

  /**
   * Matches an element, attribute, JSON property, field, or path
   * whose value that has the correct datatyped comparison with
   * one of the criteria values.
   * @param index    the range container
   * @param type    the datatype of the container and specified values
   * @param operator    the comparison with the criteria values
   * @param values    the possible datatyped values for the comparison
   * @return    the StructuredQueryDefinition for the range query
   */
  public StructuredQueryDefinition range(RangeIndex index, String type,
                                         Operator operator, Object... values) {
    return new RangeQuery(index, type, null, null, null, null, operator, values);
  }
  /**
   * Matches an element, attribute, JSON property, field, or path
   * whose value that has the correct datatyped comparison with
   * one of the criteria values.
   * @param index    the range container
   * @param type    the datatype of the container and specified values
   * @param collation    the identifier for the strategy for comparing types
   * @param operator    the comparison with the criteria values
   * @param values    the possible datatyped values for the comparison
   * @return    the StructuredQueryDefinition for the range query
   */
  public StructuredQueryDefinition range(RangeIndex index, String type, String collation,
                                         Operator operator, Object... values) {
    return new RangeQuery(index, type, collation, null, null, null, operator, values);
  }
  /**
   * Matches an element, attribute, JSON property, field, or path
   * whose value that has the correct datatyped comparison with
   * one of the criteria values.
   * @param index    the range container
   * @param type    the datatype of the container and specified values
   * @param collation    the identifier for the strategy for comparing types
   * @param scope    whether the query matches the document content or properties
   * @param operator    the comparison with the criteria values
   * @param values    the possible datatyped values for the comparison
   * @return    the StructuredQueryDefinition for the range query
   */
  public StructuredQueryDefinition range(RangeIndex index, String type, String collation,
                                         FragmentScope scope, Operator operator,
                                         Object... values) {
    return new RangeQuery(index, type, collation, scope, null, null, operator, values);
  }
  /**
   * Matches an element, attribute, JSON property, field, or path
   * whose value that has the correct datatyped comparison with
   * one of the criteria values.
   * @param index    the range container
   * @param type    the datatype of the container and specified values
   * @param options    options for fine tuning the query
   * @param operator    the comparison with the criteria values
   * @param values    the possible datatyped values for the comparison
   * @return    the StructuredQueryDefinition for the range query
   */
  public StructuredQueryDefinition range(RangeIndex index, String type, String[] options, Operator operator,
                                         Object... values) {
    return new RangeQuery(index, type, null, null, options, null, operator, values);
  }
  /**
   * Matches an element, attribute, JSON property, field, or path
   * whose value that has the correct datatyped comparison with
   * one of the criteria values.
   * @param index    the range container
   * @param type    the datatype of the container and specified values
   * @param options    options for fine tuning the query
   * @param weight    the multiplier for the match in the document ranking
   * @param operator    the comparison with the criteria values
   * @param values    the possible datatyped values for the comparison
   * @return    the StructuredQueryDefinition for the range query
   */
  public StructuredQueryDefinition range(RangeIndex index, String type, String[] options, double weight,
                                         Operator operator, Object... values) {
    return new RangeQuery(index, type, null, null, options, weight, operator, values);
  }
  /**
   * Matches an element, attribute, JSON property, field, or path
   * whose value that has the correct datatyped comparison with
   * one of the criteria values.
   * @param index    the range container
   * @param type    the datatype of the container and specified values
   * @param collation    the identifier for the strategy for comparing types
   * @param options    options for fine tuning the query
   * @param operator    the comparison with the criteria values
   * @param values    the possible datatyped values for the comparison
   * @return    the StructuredQueryDefinition for the range query
   */
  public StructuredQueryDefinition range(RangeIndex index, String type, String collation, String[] options,
                                         Operator operator, Object... values) {
    return new RangeQuery(index, type, collation, null, options, null, operator, values);
  }
  /**
   * Matches an element, attribute, JSON property, field, or path
   * whose value that has the correct datatyped comparison with
   * one of the criteria values.
   * @param index    the range container
   * @param type    the datatype of the container and specified values
   * @param collation    the identifier for the strategy for comparing types
   * @param options    options for fine tuning the query
   * @param weight    the multiplier for the match in the document ranking
   * @param operator    the comparison with the criteria values
   * @param values    the possible datatyped values for the comparison
   * @return    the StructuredQueryDefinition for the range query
   */
  public StructuredQueryDefinition range(RangeIndex index, String type, String collation, String[] options,
                                         double weight, Operator operator, Object... values) {
    return new RangeQuery(index, type, collation, null, options, weight, operator, values);
  }
  /**
   * Matches an element, attribute, JSON property, field, or path
   * whose value that has the correct datatyped comparison with
   * one of the criteria values.
   * @param index    the range container
   * @param type    the datatype of the container and specified values
   * @param collation    the identifier for the strategy for comparing types
   * @param scope    whether the query matches the document content or properties
   * @param options    options for fine tuning the query
   * @param operator    the comparison with the criteria values
   * @param values    the possible datatyped values for the comparison
   * @return    the StructuredQueryDefinition for the range query
   */
  public StructuredQueryDefinition range(RangeIndex index, String type, String collation,
                                         FragmentScope scope, String[] options, Operator operator,
                                         Object... values) {
    return new RangeQuery(index, type, collation, scope, options, null, operator, values);
  }
  /**
   * Matches an element, attribute, JSON property, field, or path
   * whose value that has the correct datatyped comparison with
   * one of the criteria values.
   * @param index    the range container
   * @param type    the datatype of the container and specified values
   * @param collation    the identifier for the strategy for comparing types
   * @param scope    whether the query matches the document content or properties
   * @param options    options for fine tuning the query
   * @param weight    the multiplier for the match in the document ranking
   * @param operator    the comparison with the criteria values
   * @param values    the possible datatyped values for the comparison
   * @return    the StructuredQueryDefinition for the range query
   */
  public StructuredQueryDefinition range(RangeIndex index, String type, String collation,
                                         FragmentScope scope, String[] options, double weight,
                                         Operator operator, Object... values) {
    return new RangeQuery(index, type, collation, scope, options, weight, operator, values);
  }

  /**
   * Matches an element, element pair, element attribute, pair, or path
   * specifying a geospatial point that appears within one of the criteria regions.
   * @param index    the container for the coordinates of the geospatial point
   * @param regions    the possible regions containing the point
   * @return    the StructuredQueryDefinition for the geospatial query
   */
  public StructuredQueryDefinition geospatial(GeospatialIndex index, Region... regions) {
    checkRegions(regions);
    return new GeospatialPointQuery(index, null, regions, null, null);
  }
  /**
   * Matches an element, element pair, element attribute, pair, or path
   * specifying a geospatial point that appears within one of the criteria regions.
   * @param index    the container for the coordinates of the geospatial point
   * @param scope    whether the query matches the document content or properties
   * @param options    options for fine tuning the query
   * @param regions    the possible regions containing the point
   * @return    the StructuredQueryDefinition for the geospatial query
   */
  public StructuredQueryDefinition geospatial(GeospatialIndex index, FragmentScope scope,
                                              String[] options, Region... regions) {
    checkRegions(regions);
    return new GeospatialPointQuery(index, scope, regions, options, null);
  }
  /**
   * Matches an element, element pair, element attribute, pair, or path
   * specifying a geospatial point that appears within one of the criteria regions.
   * @param index    the container for the coordinates of the geospatial point
   * @param scope    whether the query matches the document content or properties
   * @param options    options for fine tuning the query
   * @param weight    the multiplier for the match in the document ranking
   * @param regions    the possible regions containing the point
   * @return    the StructuredQueryDefinition for the geospatial query
   */
  public StructuredQueryDefinition geospatial(GeospatialIndex index, FragmentScope scope,
                                              String[] options, Double weight, Region... regions) {
    checkRegions(regions);
    return new GeospatialPointQuery(index, scope, regions, options, weight);
  }

  /**
   * Matches a path specifying a geospatial region, which is indexed via
   * geospatial region index, that has the relationship given by the operator
   * with at least one of the criteria regions.
   * @param index    the container for the geospatial regions
   * @param operator    the geospatial operator to be applied with the regions in the
   *                  index and the specified regions
   * @param regions    the possible regions containing the region
   * @return    the StructuredQueryDefinition for the geospatial query
   */
  public StructuredQueryDefinition geospatial(GeospatialRegionIndex index, GeospatialOperator operator, Region... regions) {
    checkRegions(regions);
    return new GeospatialRegionQuery((GeoRegionPathImpl)index, operator, null, regions, null, null);
  }

  /**
   * Matches a path specifying a geospatial region, which is indexed via
   * geospatial region index, that has the relationship given by the operator
   * with at least one of the criteria regions.
   * @param index    the container for the geospatial regions
   * @param operator    the geospatial operator to be applied with the regions in the
   *                  index and the specified regions
   * @param scope    whether the query matches the document content or properties
   * @param options    options for fine tuning the query
   * @param regions    the possible regions containing the region
   * @return    the StructuredQueryDefinition for the geospatial query
   */
  public StructuredQueryDefinition geospatial(GeospatialRegionIndex index, GeospatialOperator operator,
                                              FragmentScope scope, String[] options, Region... regions) {
    checkRegions(regions);
    return new GeospatialRegionQuery((GeoRegionPathImpl)index, operator, scope, regions, options, null);
  }
  /**
   * Matches a path specifying a geospatial region, which is indexed via
   * geospatial region index, that has the relationship given by the operator
   * with at least one of the criteria regions.
   * @param index    the container for the geospatial regions
   * @param operator    the geospatial operator to be applied with the regions in the
   *                  index and the specified regions
   * @param scope    whether the query matches the document content or properties
   * @param options    options for fine tuning the query
   * @param weight    the multiplier for the match in the document ranking
   * @param regions    the possible regions containing the region
   * @return    the StructuredQueryDefinition for the geospatial query
   */
  public StructuredQueryDefinition geospatial(GeospatialRegionIndex index, GeospatialOperator operator,
                                              FragmentScope scope, String[] options, Double weight, Region... regions) {
    checkRegions(regions);
    return new GeospatialRegionQuery((GeoRegionPathImpl)index, operator, scope, regions, options, weight);
  }

  /**
   * Identifies a namespaced element to match with a query.
   * @param qname    the name of the element
   * @return    the element identifier
   */
  public Element element(QName qname) {
    return new ElementImpl(qname);
  }
  /**
   * Identifies a simple element to match with a query.
   * @param name    the name of the element
   * @return    the element identifier
   */
  public Element element(String name) {
    return new ElementImpl(name);
  }
  /**
   * Identifies a namespaced attribute to match with a query.
   * @param qname    the name of the attribute
   * @return    the attribute identifier
   */
  public Attribute attribute(QName qname) {
    return new AttributeImpl(qname);
  }
  /**
   * Identifies a simple attribute to match with a query.
   * @param name    the name of the attribute
   * @return    the attribute identifier
   */
  public Attribute attribute(String name) {
    return new AttributeImpl(name);
  }
  /**
   * Identifies an element having an attribute to match with a query.
   * @param element    the element identifier
   * @param attribute    the attribute identifier
   * @return    the identifier for the element-attribute pair
   */
  public ElementAttribute elementAttribute(Element element, Attribute attribute) {
    return new ElementAttributeImpl(element, attribute);
  }
  /**
   * Identifies a field to match with a query.  Fields are defined
   * in the configuration for the database.
   * @param name    the name of the field
   * @return    the identifier for the field
   */
  public Field field(String name) {
    return new FieldImpl(name);
  }
  /**
   * Identifies a JSON property to match with a query.
   * @param name    the name of the JSON property
   * @return    the identifier for the JSON property
   */
  public JSONProperty jsonProperty(String name) {
    return new JSONPropertyImpl(name);
  }
  /**
   * Identifies a path index to match with a query.
   * @param path    the indexed path
   * @return    the identifier for the path index
   */
  public PathIndex pathIndex(String path) {
    return new PathIndexImpl(path);
  }
  /**
   * Identifies a json property whose text has the point format latitude and longitude
   * coordinates to match with a geospatial query.
   * @param jsonProperty    the json property containing the geospatial coordinates
   * @return    the specification for the index on the geospatial coordinates
   */
  public GeospatialIndex geoJSONProperty(JSONProperty jsonProperty) {
    if ( jsonProperty == null ) throw new IllegalArgumentException("jsonProperty cannot be null");
    return new GeoJSONPropertyImpl(jsonProperty);
  }
  /**
   * Identifies a parent json property with a child json property whose text has
   * the latitude and longitude coordinates to match with a geospatial query.
   * @param parent    the parent of the json property with the coordinates
   * @param jsonProperty    the json property containing the geospatial coordinates
   * @return    the specification for the index on the geospatial coordinates
   */
  public GeospatialIndex geoJSONProperty(JSONProperty parent, JSONProperty jsonProperty) {
    if ( parent == null ) throw new IllegalArgumentException("parent cannot be null");
    if ( jsonProperty == null ) throw new IllegalArgumentException("jsonProperty cannot be null");
    return new GeoJSONPropertyImpl(parent, jsonProperty);
  }
  /**
   * Identifies a parent json property with child latitude and longitude json properties
   * to match with a geospatial query.
   * @param parent    the parent json property of lat and lon
   * @param lat    the json property with the latitude coordinate
   * @param lon    the json property with the longitude coordinate
   * @return    the specification for the index on the geospatial coordinates
   */
  public GeospatialIndex geoJSONPropertyPair(JSONProperty parent, JSONProperty lat, JSONProperty lon) {
    if ( parent == null ) throw new IllegalArgumentException("parent cannot be null");
    if ( lat == null )    throw new IllegalArgumentException("lat cannot be null");
    if ( lon == null )    throw new IllegalArgumentException("lon cannot be null");
    return new GeoJSONPropertyPairImpl(parent, lat, lon);
  }
  /**
   * Identifies an element whose text has the latitude and longitude
   * coordinates to match with a geospatial query.
   * @param element    the element containing the geospatial coordinates
   * @return    the specification for the index on the geospatial coordinates
   */
  public GeospatialIndex geoElement(Element element) {
    return new GeoElementImpl(element);
  }
  /**
   * Identifies a parent element with a child element whose text has
   * the latitude and longitude coordinates to match with a geospatial query.
   * @param parent    the parent of the element with the coordinates
   * @param element    the element containing the geospatial coordinates
   * @return    the specification for the index on the geospatial coordinates
   */
  public GeospatialIndex geoElement(Element parent, Element element) {
    return new GeoElementImpl(parent, element);
  }
  /**
   * Identifies a parent element with child latitude and longitude elements
   * to match with a geospatial query.
   * @param parent    the parent of the element with the coordinates
   * @param lat    the element with the latitude coordinate
   * @param lon    the element with the longitude coordinate
   * @return    the specification for the index on the geospatial coordinates
   */
  public GeospatialIndex geoElementPair(Element parent, Element lat, Element lon) {
    return new GeoElementPairImpl(parent, lat, lon);
  }
  /**
   * Identifies a parent element with child latitude and longitude attributes
   * to match with a geospatial query.
   * @param parent    the parent of the element with the coordinates
   * @param lat    the attribute with the latitude coordinate
   * @param lon    the attribute with the longitude coordinate
   * @return    the specification for the index on the geospatial coordinates
   */
  public GeospatialIndex geoAttributePair(Element parent, Attribute lat, Attribute lon) {
    return new GeoAttributePairImpl(parent, lat, lon);
  }
  /**
   * Identifies a path with the latitude and longitude to match
   * with a geospatial query.
   * @param pathIndex    the indexed path
   * @return    the specification for the index on the geospatial coordinates
   */
  public GeospatialIndex geoPath(PathIndex pathIndex) {
    return new GeoPointPathImpl(pathIndex);
  }

  /**
   * Identifies a path with regions to match
   * with a geospatial query.
   * @param pathIndex    the indexed path
   * @return    the specification for the index on the geospatial region
   */
  public GeospatialRegionIndex geoRegionPath(PathIndex pathIndex) {
    return new GeoRegionPathImpl(pathIndex);
  }

  /**
   * Identifies a path with regions to match
   * with a geospatial query.
   * @param pathIndex    the indexed path
   * @param coordinateSystem  the coordinate system used along with precision info
   *                          used for the index
   * @return    the specification for the index on the geospatial region
   */
  public GeospatialRegionIndex geoRegionPath(PathIndex pathIndex , CoordinateSystem coordinateSystem) {
    return new GeoRegionPathImpl(pathIndex, coordinateSystem);
  }

  /**
   * Specifies a geospatial point.
   * @param latitude    the latitude coordinate
   * @param longitude    the longitude coordinate
   * @return    the definition of the point
   */
  public Point point(double latitude, double longitude) {
    return new PointImpl(latitude, longitude);
  }

  /**
   * Specifies a geospatial region as a circle,
   * supplying coordinates for the center.
   * @param latitude    the latitude coordinate of the center
   * @param longitude    the longitude coordinate of the center
   * @param radius    the radius of the circle
   * @return    the definition of the circle
   */
  public Circle circle(double latitude, double longitude, double radius) {
    return new CircleImpl(latitude, longitude, radius);
  }
  /**
   * Specifies a geospatial region as a circle,
   * supplying a point for the center.
   * @param center    the point defining the center
   * @param radius    the radius of the circle
   * @return    the definition of the circle
   */
  public Circle circle(Point center, double radius) {
    return new CircleImpl(center.getLatitude(), center.getLongitude(), radius);
  }

  /**
   * Specifies a geospatial region as a box, supplying
   * the coordinates for the perimeter.
   * @param south    the latitude of the south coordinate
   * @param west    the longitude of the west coordinate
   * @param north    the latitude of the north coordinate
   * @param east    the longitude of the east coordinate
   * @return    the definition of the box
   */
  public Box box(double south, double west, double north, double east) {
    return new BoxImpl(south, west, north, east);
  }

  /**
   * Specifies a geospatial region as an arbitrary polygon.
   * @param points    the list of points defining the perimeter of the region
   * @return    the definition of the polygon
   */
  public Polygon polygon(Point... points) {
    return new PolygonImpl(points);
  }

  /**
   * Matches a query within the substructure of the container specified
   * by the constraint.
   * @param constraintName    the constraint definition
   * @param query    the query definition
   * @return    the StructuredQueryDefinition for the element constraint query
   */
  public StructuredQueryDefinition containerConstraint(String constraintName, StructuredQueryDefinition query) {
    checkQuery(query);
    return new ContainerConstraintQuery(constraintName, query);
  }

  /**
   * Associates a query with the properties of documents (as opposed to
   * the content of documents) with the specified constraint.
   * @param constraintName    the constraint definition
   * @param query    the query definition
   * @return    the StructuredQueryDefinition for the properties constraint query
   */
  public StructuredQueryDefinition propertiesConstraint(String constraintName, StructuredQueryDefinition query) {
    checkQuery(query);
    return new PropertiesConstraintQuery(constraintName, query);
  }

  /**
   * Matches documents belonging to at least one
   * of the criteria collections with the specified constraint.
   * @param constraintName    the constraint definition
   * @param uris    the identifiers for the criteria collections
   * @return    the StructuredQueryDefinition for the collection constraint query
   */
  public StructuredQueryDefinition collectionConstraint(String constraintName, String... uris) {
    return new CollectionConstraintQuery(constraintName, uris);
  }

  /**
   * Matches the container specified by the constraint when it
   * has a value with the same string value as at least one
   * of the criteria values.
   * @param constraintName    the constraint definition
   * @param values    the possible values to match
   * @return    the StructuredQueryDefinition for the value constraint query
   */
  public StructuredQueryDefinition valueConstraint(String constraintName, String... values) {
    return new ValueConstraintQuery(constraintName, values);
  }
  /**
   * Matches the container specified by the constraint when it
   * has a value with the same string value as at least one
   * of the criteria values.
   * @param constraintName    the constraint definition
   * @param weight    the multiplier for the match in the document ranking
   * @param values    the possible values to match
   * @return    the StructuredQueryDefinition for the value constraint query
   */
  public StructuredQueryDefinition valueConstraint(String constraintName, double weight, String... values) {
    return new ValueConstraintQuery(constraintName, weight, values);
  }

  /**
   * Matches the container specified by the constraint when it
   * has at least one of the criteria words.
   * @param constraintName    the constraint definition
   * @param words    the possible words to match
   * @return    the StructuredQueryDefinition for the word constraint query
   */
  public StructuredQueryDefinition wordConstraint(String constraintName, String... words) {
    return new WordConstraintQuery(constraintName, words);
  }
  /**
   * Matches the container specified by the constraint when it
   * has at least one of the criteria words.
   * @param constraintName    the constraint definition
   * @param weight    the multiplier for the match in the document ranking
   * @param words    the possible words to match
   * @return    the StructuredQueryDefinition for the word constraint query
   */
  public StructuredQueryDefinition wordConstraint(String constraintName, double weight, String... words) {
    return new WordConstraintQuery(constraintName, weight, words);
  }

  /**
   * Matches the container specified by the constraint
   * whose value that has the correct datatyped comparison with
   * one of the criteria values.
   * @param constraintName    the constraint definition
   * @param operator    the comparison with the criteria values
   * @param values    the possible datatyped values for the comparison
   * @return    the StructuredQueryDefinition for the range constraint query
   */
  public StructuredQueryDefinition rangeConstraint(String constraintName, Operator operator, String... values) {
    return new RangeConstraintQuery(constraintName, operator, values);
  }

  /**
   * Matches the container specified by the constraint
   * whose geospatial point appears within one of the criteria regions.
   * @param constraintName    the constraint definition
   * @param regions    the possible regions containing the point
   * @return    the StructuredQueryDefinition for the geospatial constraint query
   */
  public GeospatialConstraintQuery geospatialConstraint(String constraintName, Region... regions) {
    checkRegions(regions);
    return new GeospatialConstraintQuery(constraintName, regions);
  }

  /**
   * Matches the container specified by the constraint
   * whose geospatial region appears within one of the criteria regions.
   * @param constraintName    the constraint definition
   * @param operator    the geospatial operator to be applied with the regions
   *                  in the index and the criteria regions
   * @param regions    the possible regions containing the point
   * @return    the StructuredQueryDefinition for the geospatial constraint query
   */
  public StructuredQueryDefinition geospatialRegionConstraint(String constraintName, GeospatialOperator operator, Region... regions) {
    checkRegions(regions);
    return new GeospatialRegionConstraintQuery(constraintName, operator, regions);
  }

  /**
   * Matches documents as specified by a constraint that implements
   * a custom query parameterized with the supplied text.
   * @param constraintName    the constraint definition
   * @param text    the input to the custom query
   * @return    the StructuredQueryDefinition for the custom constraint query
   */
  public StructuredQueryDefinition customConstraint(String constraintName, String... text) {
    return new CustomConstraintQuery(constraintName, text);
  }

    /* ************************************************************************************* */

  protected abstract class AbstractStructuredQuery
    extends AbstractQueryDefinition
    implements StructuredQueryDefinition {
    private String criteria = null;

    public AbstractStructuredQuery() {
      optionsUri = builderOptionsURI;
    }

    @Override
    public String getCriteria() {
      return criteria;
    }

    @Override
    public void setCriteria(String criteria) {
      this.criteria = criteria;
    }

    @Override
    public AbstractStructuredQuery withCriteria(String criteria) {
      setCriteria(criteria);
      return this;
    }

	@Override
	public void serialize(XMLStreamWriter serializer) throws XMLStreamException {
		innerSerialize(serializer);
	}

    @Override
    public String serialize() {
      return serializeQueries(this);
    }

    public abstract void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException;

    @Override
    public boolean canSerializeQueryAsJSON() {
      return StructuredQueryBuilder.this.getNamespaces() == null && getOptionsName() == null;
    }
  }

  protected class AndQuery
    extends AbstractStructuredQuery {
    private StructuredQueryDefinition[] queries;

    public AndQuery(StructuredQueryDefinition... queries) {
      super();
      this.queries = queries;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeQueryList(serializer, "and-query", convertQueries(queries));
    }
  }

  protected class OrQuery
    extends AbstractStructuredQuery {
    private StructuredQueryDefinition[] queries;

    public OrQuery(StructuredQueryDefinition... queries) {
      super();
      this.queries = queries;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeQueryList(serializer, "or-query", convertQueries(queries));
    }
  }

  protected class NotQuery
    extends AbstractStructuredQuery {
    private StructuredQueryDefinition query;

    public NotQuery(StructuredQueryDefinition query) {
      super();
      this.query = query;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeQuery(serializer, "not-query", (AbstractStructuredQuery) query);
    }
  }

  private class NotInQuery
    extends AbstractStructuredQuery {
    private StructuredQueryDefinition positive;
    private StructuredQueryDefinition negative;

    public NotInQuery(StructuredQueryDefinition positive, StructuredQueryDefinition negative) {
      super();
      this.positive = positive;
      this.negative = negative;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "not-in-query");
      writeQuery(serializer, "positive-query", (AbstractStructuredQuery) positive);
      writeQuery(serializer, "negative-query", (AbstractStructuredQuery) negative);
      serializer.writeEndElement();
    }
  }

  protected class AndNotQuery
    extends AbstractStructuredQuery {
    private StructuredQueryDefinition positive;
    private StructuredQueryDefinition negative;

    public AndNotQuery(StructuredQueryDefinition positive, StructuredQueryDefinition negative) {
      super();
      this.positive = positive;
      this.negative = negative;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "and-not-query");
      writeQuery(serializer, "positive-query", (AbstractStructuredQuery) positive);
      writeQuery(serializer, "negative-query", (AbstractStructuredQuery) negative);
      serializer.writeEndElement();
    }
  }

  private class BoostQuery
    extends AbstractStructuredQuery {
    private StructuredQueryDefinition matchingQuery;
    private StructuredQueryDefinition boostingQuery;

    public BoostQuery(StructuredQueryDefinition matchingQuery, StructuredQueryDefinition boostingQuery) {
      super();
      this.matchingQuery = matchingQuery;
      this.boostingQuery = boostingQuery;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "boost-query");
      writeQuery(serializer, "matching-query", (AbstractStructuredQuery) matchingQuery);
      writeQuery(serializer, "boosting-query", (AbstractStructuredQuery) boostingQuery);
      serializer.writeEndElement();
    }
  }


  protected class DocumentQuery
    extends AbstractStructuredQuery {
    private String[] uris = null;

    public DocumentQuery(String... uris) {
      super();
      this.uris = uris;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "document-query");
      writeTextList(serializer, "uri", uris);
      serializer.writeEndElement();
    }
  }

  protected class TermQuery
    extends AbstractStructuredQuery {
    private String[] terms = null;
    private Double weight = 0.0;

    public TermQuery(Double weight, String... terms) {
      super();
      this.weight = weight;
      this.terms = terms;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "term-query");
      writeTextList(serializer, "text", terms);
      writeText(serializer, "weight", weight);
      serializer.writeEndElement();
    }
  }

  protected class NearQuery extends AbstractStructuredQuery {
    private Integer minimumDistance;
    private Integer maximumDistance;
    private Double weight;
    private Ordering order;
    private StructuredQueryDefinition[] queries;

    public NearQuery(StructuredQueryDefinition... queries) {
      super();
      this.queries = queries;
    }

    public NearQuery(Integer minimumDistance, Integer maximumDistance, Double weight,
      Ordering order, StructuredQueryDefinition... queries)
    {
      this.minimumDistance = minimumDistance;
      this.maximumDistance = maximumDistance;
      this.weight = weight;
      this.order = order;
      this.queries = queries;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "near-query");
      writeQueryList(serializer, queries);
      if (order != null) {
        writeSearchElement(serializer, "ordered");
        serializer.writeCharacters(
          Boolean.toString(order == Ordering.ORDERED));
        serializer.writeEndElement();
      }
      writeText(serializer, "distance", maximumDistance);
      writeText(serializer, "minimum-distance", minimumDistance);
      writeText(serializer, "distance-weight", weight);
      serializer.writeEndElement();
    }
  }

  protected class CollectionQuery
    extends AbstractStructuredQuery {
    private String uris[] = null;

    public CollectionQuery(String... uris) {
      super();
      this.uris = uris;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "collection-query");
      writeTextList(serializer, "uri", uris);
      serializer.writeEndElement();
    }
  }

  protected class DirectoryQuery
    extends AbstractStructuredQuery {
    private String uris[];
    private Boolean isInfinite;
    private Integer depth;

    public DirectoryQuery(Boolean isInfinite, String... uris) {
      super();
      this.isInfinite = isInfinite;
      this.uris = uris;
      this.depth = null;
    }

    DirectoryQuery(Integer depth, String... uris) {
      super();
      this.isInfinite = false;
      this.uris = uris;
      this.depth = depth;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "directory-query");
      if (depth != null) {
        serializer.writeAttribute("depth", Integer.toString(depth));
      }
      writeTextList(serializer, "uri", uris);
      writeText(serializer, "infinite", isInfinite);
      serializer.writeEndElement();
    }
  }

  protected class DocumentFragmentQuery
    extends AbstractStructuredQuery {
    private StructuredQueryDefinition query;

    public DocumentFragmentQuery(StructuredQueryDefinition query) {
      super();
      this.query = query;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeQuery(serializer, "document-fragment-query", (AbstractStructuredQuery) query);
    }
  }

  protected class PropertiesQuery
    extends AbstractStructuredQuery {
    private StructuredQueryDefinition query;

    public PropertiesQuery(StructuredQueryDefinition query) {
      super();
      this.query = query;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeQuery(serializer, "properties-fragment-query", (AbstractStructuredQuery) query);
    }
  }

  protected class LocksQuery
    extends AbstractStructuredQuery {
    private StructuredQueryDefinition query;

    public LocksQuery(StructuredQueryDefinition query) {
      super();
      this.query = query;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeQuery(serializer, "locks-fragment-query", (AbstractStructuredQuery) query);
    }
  }




  /**
   * Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
   * as the type for instances of ContainerConstraintQuery.
   */
  private class ContainerConstraintQuery
    extends AbstractStructuredQuery {
    private String name;
    private StructuredQueryDefinition query;

    /**
     * Use the containerConstraint() builder method of StructuredQueryBuilder
     * and type the object as an instance of the StructuredQueryDefinition interface.
     */
    public ContainerConstraintQuery(String constraintName, StructuredQueryDefinition query) {
      super();
      name = constraintName;
      this.query = query;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "container-constraint-query");
      writeText(serializer, "constraint-name", name);
      writeQuery(serializer, query);
      serializer.writeEndElement();
    }
  }

  protected class PropertiesConstraintQuery
    extends AbstractStructuredQuery {
    private String name;
    private StructuredQueryDefinition query;

    public PropertiesConstraintQuery(String constraintName, StructuredQueryDefinition query) {
      super();
      name = constraintName;
      this.query = query;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "properties-constraint-query");
      writeText(serializer, "constraint-name", name);
      writeQuery(serializer, query);
      serializer.writeEndElement();
    }
  }

  protected class CollectionConstraintQuery
    extends AbstractStructuredQuery {
    String name = null;
    String[] uris = null;

    public CollectionConstraintQuery(String constraintName, String... uris) {
      super();
      name = constraintName;
      this.uris = uris;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "collection-constraint-query");
      writeText(serializer, "constraint-name", name);
      writeTextList(serializer, "uri", uris);
      serializer.writeEndElement();
    }
  }

  protected class ValueConstraintQuery
    extends AbstractStructuredQuery {
    String name;
    String[] values;
    Double weight;

    public ValueConstraintQuery(String constraintName, String... values) {
      super();
      name = constraintName;
      this.values = values;
    }

    public ValueConstraintQuery(String constraintName, Double weight, String... values) {
      name = constraintName;
      this.values = values;
      this.weight = weight;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "value-constraint-query");
      writeText(serializer, "constraint-name", name);
      writeTextList(serializer, "text", values);
      writeText(serializer, "weight", weight);
      serializer.writeEndElement();
    }
  }

  protected class WordConstraintQuery
    extends AbstractStructuredQuery {
    String name;
    String[] words;
    Double weight;

    public WordConstraintQuery(String constraintName, String... words) {
      super();
      name = constraintName;
      this.words = words;
    }

    public WordConstraintQuery(String constraintName, Double weight, String... words) {
      name = constraintName;
      this.words = words;
      this.weight = weight;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "word-constraint-query");
      writeText(serializer, "constraint-name", name);
      writeTextList(serializer, "text", words);
      writeText(serializer, "weight", weight);
      serializer.writeEndElement();
    }
  }

  protected class RangeConstraintQuery
    extends AbstractStructuredQuery {
    String name = null;
    String[] values = null;
    Operator operator = null;

    public RangeConstraintQuery(String constraintName, Operator operator, String... values) {
      super();
      name = constraintName;
      this.values = values;
      this.operator = operator;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "range-constraint-query");
      writeText(serializer, "constraint-name", name);
      writeTextList(serializer, "value", values);
      writeText(serializer, "range-operator", operator);
      serializer.writeEndElement();
    }
  }

  protected class GeospatialConstraintQuery
    extends AbstractStructuredQuery {
    String name = null;
    Region[] regions = null;

    public GeospatialConstraintQuery(String constraintName, Region... regions) {
      super();
      name = constraintName;
      this.regions = regions;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "geospatial-constraint-query");
      writeText(serializer, "constraint-name", name);
      for (Region region : regions) {
        ((RegionImpl) region).innerSerialize(serializer);
      }
      serializer.writeEndElement();
    }
  }

  protected class GeospatialRegionConstraintQuery extends AbstractStructuredQuery {
    String name = null;
    Region[] regions = null;
    GeospatialOperator operator;

    public GeospatialRegionConstraintQuery(String constraintName, GeospatialOperator operator, Region... regions) {
      super();
      name = constraintName;
      this.regions = regions;
      this.operator = operator;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "geo-region-constraint-query");
      writeText(serializer, "constraint-name", name);
      writeText(serializer, "geospatial-operator", operator.toString());
      for (Region region : regions) {
        ((RegionImpl) region).innerSerialize(serializer);
      }
      serializer.writeEndElement();
    }
  }

  protected class CustomConstraintQuery
    extends AbstractStructuredQuery {
    private String terms[] = null;
    private String name = null;

    public CustomConstraintQuery(String constraintName, String... terms) {
      super();
      name = constraintName;
      this.terms = terms;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "custom-constraint-query");
      writeText(serializer, "constraint-name", name);
      writeTextList(serializer, "text", terms);
      serializer.writeEndElement();
    }
  }

  protected class ContainerQuery
    extends AbstractStructuredQuery {
    private ContainerIndex index;
    private StructuredQueryDefinition query;
    ContainerQuery(ContainerIndex index, StructuredQueryDefinition query) {
      this.index = index;
      this.query = query;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "container-query");
      ((IndexImpl) index).innerSerialize(serializer);
      writeQuery(serializer, query);
      serializer.writeEndElement();
    }
  }

  abstract class TextQuery
    extends AbstractStructuredQuery {
    TextIndex     index;
    FragmentScope scope;
    String[]      values;
    String[]      options;
    Double        weight;
    TextQuery(TextIndex index, FragmentScope scope,
              String[] options, Double weight, String[] values) {
      this.index   = index;
      this.scope   = scope;
      this.options = options;
      this.weight  = weight;
      this.values  = values;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      ((IndexImpl) index).innerSerialize(serializer);
      if (scope != null) {
        if (scope == FragmentScope.DOCUMENTS) {
          writeText(serializer, "fragment-scope", "documents");
        }
        else {
          writeText(serializer, "fragment-scope",
            scope.toString().toLowerCase());
        }
      }
      writeTextList(serializer, "text", values);
      writeTextList(serializer, "term-option", options);
      writeText(serializer, "weight", weight);
    }
  }

  protected class ValueQuery
    extends AbstractStructuredQuery {
    TextIndex     index;
    FragmentScope scope;
    String[]      options;
    Double        weight;
    Object[]      values;
    ValueQuery(TextIndex index, FragmentScope scope,
               String[] options, Double weight, Object[] values) {
      this.index   = index;
      this.scope   = scope;
      this.options = options;
      this.weight  = weight;
      this.values  = values;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "value-query");
      if ( values != null && values.length > 0 ) {
        if ( values[0] == null ) {
          serializer.writeAttribute("type", "null");
        } else if ( values[0] instanceof String ) {
          serializer.writeAttribute("type", "string");
        } else if ( values[0] instanceof Number ) {
          serializer.writeAttribute("type", "number");
        } else if ( values[0] instanceof Boolean ) {
          serializer.writeAttribute("type", "boolean");
        }
      }
      ((IndexImpl) index).innerSerialize(serializer);
      if (scope != null) {
        if (scope == FragmentScope.DOCUMENTS) {
          writeText(serializer, "fragment-scope", "documents");
        }
        else {
          writeText(serializer, "fragment-scope",
            scope.toString().toLowerCase());
        }
      }
      if ( values != null ) {
        for ( Object value: values ) {
          if ( value == null ) {
          } else {
            writeText(serializer, "text", value);
          }
        }
      }
      writeTextList(serializer, "term-option", options);
      writeText(serializer, "weight", weight);
      serializer.writeEndElement();
    }
  }

  // QUESTION: why collation on word but not values?
  protected class WordQuery
    extends TextQuery {
    WordQuery(TextIndex index, FragmentScope scope, String[] options,
              Double weight, String[] values) {
      super(index, scope, options, weight, values);
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "word-query");
      super.innerSerialize(serializer);
      serializer.writeEndElement();
    }
  }

  protected class RangeQuery
    extends AbstractStructuredQuery {
    RangeIndex    index;
    FragmentScope scope;
    String        type;
    String        collation;
    String[]      options;
    Double        weight;
    Operator      operator;
    String[]      values;
    RangeQuery(RangeIndex index, String type, String collation,
               FragmentScope scope, String[] rangeOptions, Double weight,
               Operator operator, Object[] values) {
      this.index     = index;
      this.type      = type;
      this.collation = collation;
      this.scope     = scope;
      this.options   = rangeOptions;
      this.weight    = weight;
      this.operator  = operator;
      this.values    = new String[values.length];
      for (int i=0; i < values.length; i++) {
        Object value = values[i];
        this.values[i] = formatValue(value, type);
      }
    }

    String formatValue(Object value, String type) {
      if ( value == null ) {
        return "null";
      }
      Class<?> valClass = value.getClass();
      if ( String.class.isAssignableFrom(valClass) ) {
        return (String) value;
      } else if ( type != null &&
        ( type.endsWith("date") || type.endsWith("dateTime") || type.endsWith("time") ) &&
        ( Date.class.isAssignableFrom(valClass) || Calendar.class.isAssignableFrom(valClass) ) )
      {
        if ( Date.class.isAssignableFrom(valClass) ) {
          Calendar cal = Calendar.getInstance();
          cal.setTime((Date) value);
          value = cal;
        }
        if ( type.endsWith("date") ) {
          return DatatypeConverter.printDate((Calendar) value);
        } else if ( type.endsWith("dateTime") ) {
          return DatatypeConverter.printDateTime((Calendar) value);
        } else if ( type.endsWith("time") ) {
          return DatatypeConverter.printTime((Calendar) value);
        }
      }
      return value.toString();
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "range-query");
      if (type != null) {
        serializer.writeAttribute("type", type);
        if (collation!= null) {
          serializer.writeAttribute("collation", collation);
        }
      }
      ((IndexImpl) index).innerSerialize(serializer);
      if (scope != null) {
        writeText(serializer, "fragment-scope",
          scope.toString().toLowerCase());
      }
      writeTextList(serializer, "value", values);
      if (operator != null) {
        writeText(serializer, "range-operator",
          operator.toString().toUpperCase());
      }
      writeTextList(serializer, "range-option", options);
      writeText(serializer, "weight", weight);
      serializer.writeEndElement();
    }
  }

  abstract class GeospatialBaseQuery extends AbstractStructuredQuery {
    FragmentScope scope;
    Region[] regions;
    String[] options;
    Double   weight;

    GeospatialBaseQuery(FragmentScope scope, Region[] regions, String[] options, Double weight) {
      this.scope = scope;
      this.regions = regions;
      this.options = options;
      this.weight = weight;
    }
  }

  protected class GeospatialPointQuery
    extends GeospatialBaseQuery {
    GeospatialIndex index;
    GeospatialPointQuery(GeospatialIndex index, FragmentScope scope, Region[] regions,
                         String[] options, Double weight) {
      super(scope, regions, options, weight);
      this.index   = index;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      String elemName = null;
      if (index instanceof GeoJSONPropertyImpl)
        elemName = "geo-json-property-query";
      else if (index instanceof GeoJSONPropertyPairImpl)
        elemName = "geo-json-property-pair-query";
      else if (index instanceof GeoElementImpl)
        elemName = "geo-elem-query";
      else if (index instanceof GeoElementPairImpl)
        elemName = "geo-elem-pair-query";
      else if (index instanceof GeoAttributePairImpl)
        elemName = "geo-attr-pair-query";
      else if (index instanceof GeoPointPathImpl) {
        elemName = "geo-path-query";
      }
      else
        throw new IllegalStateException(
          "unknown index class: "+index.getClass().getName());

      writeSearchElement(serializer, elemName);
      ((IndexImpl) index).innerSerialize(serializer);
      if (scope != null) {
        writeText(serializer, "fragment-scope",
          scope.toString().toLowerCase());
      }
      writeTextList(serializer, "geo-option", options);
      writeText(serializer, "weight", weight);
      for (Region region : regions) {
        ((RegionImpl) region).innerSerialize(serializer);
      }
      serializer.writeEndElement();
    }
  }

  protected class GeospatialRegionQuery extends GeospatialBaseQuery {
    GeoRegionPathImpl index;
    GeospatialOperator operator;

    GeospatialRegionQuery(GeoRegionPathImpl index, GeospatialOperator operator, FragmentScope scope,
                          Region[] regions, String[] options, Double weight) {
      super(scope, regions, options, weight);
      this.index = index;
      this.operator = operator;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      String elemName = "geo-region-path-query";
      writeSearchElement(serializer, elemName);
      if(index.coordinateSystem != null) {
        serializer.writeAttribute("coord", index.coordinateSystem.toString());
      }
      ((IndexImpl) index).innerSerialize(serializer);
      writeText(serializer, "geospatial-operator", operator.toString());
      if (scope != null) {
        writeText(serializer, "fragment-scope", scope.toString().toLowerCase());
      }
      writeTextList(serializer, "geo-option", options);
      writeText(serializer, "weight", weight);
      for (Region region : regions) {
        ((RegionImpl) region).innerSerialize(serializer);
      }
      serializer.writeEndElement();
    }
  }

  protected class TemporalAxis implements Axis {
    private String name;
    TemporalAxis(String name) {
      this.name = name;
    }
    @Override
    public String toString() {
      return name;
    }
  }

  protected class TemporalPeriod
    extends AbstractStructuredQuery implements Period {
    private String formattedStart;
    private String formattedEnd;
    private String[] options;
    TemporalPeriod(String start, String end) {
      this.formattedStart = start;
      this.formattedEnd = end;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "period");
      writeText(serializer, "period-start", formattedStart);
      writeText(serializer, "period-end", formattedEnd);
      serializer.writeEndElement();
    }
  }

  protected class TemporalPeriodRangeQuery
    extends AbstractStructuredQuery {
    private Axis[] axes;
    private TemporalOperator operator;
    private Period[] periods;
    private String[] options;
    TemporalPeriodRangeQuery(Axis[] axes, TemporalOperator operator, Period[] periods, String... options) {
      this.axes = axes;
      this.operator = operator;
      this.periods = periods;
      this.options = options;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "period-range-query");
      writeTextList(serializer, "axis", axes);
      writeText(serializer, "temporal-operator", operator.toString().toLowerCase());
      for ( Period period : periods ) {
        ((TemporalPeriod) period).innerSerialize(serializer);
      }
      writeTextList(serializer, "query-option", options);
      serializer.writeEndElement();
    }
  }

  protected class TemporalPeriodCompareQuery
    extends AbstractStructuredQuery {
    private Axis axis1;
    private TemporalOperator operator;
    private Axis axis2;
    private String[] options;
    TemporalPeriodCompareQuery(Axis axis1, TemporalOperator operator, Axis axis2, String[] options) {
      this.axis1 = axis1;
      this.operator = operator;
      this.axis2 = axis2;
      this.options = options;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "period-compare-query");
      writeText(serializer, "axis1", axis1);
      writeText(serializer, "temporal-operator", operator.toString().toLowerCase());
      writeText(serializer, "axis2", axis2);
      writeTextList(serializer, "query-option", options);
      serializer.writeEndElement();
    }
  }

  protected class TemporalLsqtQuery
    extends AbstractStructuredQuery {
    private String temporalCollection;
    private String formattedTimestamp = null;
    private double weight;
    private String[] options;
    TemporalLsqtQuery(String temporalCollection, String timestamp, double weight, String[] options) {
      this.temporalCollection = temporalCollection;
      if ( timestamp != null ) {
        this.formattedTimestamp = timestamp;
      }
      this.weight = weight;
      this.options = options;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "lsqt-query");
      writeText(serializer, "temporal-collection", temporalCollection);
      if ( formattedTimestamp != null && formattedTimestamp.length() > 0 ) {
        writeText(serializer, "timestamp", formattedTimestamp);
      }
      writeText(serializer, "weight", weight);
      writeTextList(serializer, "query-option", options);
      serializer.writeEndElement();
    }
  }

  protected class TimeQuery extends AbstractStructuredQuery {
	  private String formattedTimestamp = null;
	  private String startElement = null;
	  TimeQuery(String timestamp, String startElement) {
		  if (timestamp == null || timestamp.length() == 0) {
			  throw new IllegalArgumentException("timestamp cannot be null or empty.");
		  }
		  this.formattedTimestamp = timestamp;
		  this.startElement = startElement;
	  }
	  @Override
	  public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
		  writeSearchElement(serializer, startElement);
		  if (formattedTimestamp != null && formattedTimestamp.length() > 0) {
			  writeText(serializer, "timestamp", formattedTimestamp);
		  }
		  serializer.writeEndElement();
	  }
  }

    /* ************************************************************************************* */

  protected abstract class IndexImpl {
    public abstract void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException;
  }
  protected class ElementImpl extends IndexImpl implements Element {
    String name;
    QName qname;
    ElementImpl(QName qname) {
      this.qname = qname;
    }
    ElementImpl(String name) {
      this.name = name;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      serializeNamedIndex(serializer, "element", qname, name);
    }
  }
  protected class AttributeImpl extends IndexImpl implements Attribute {
    String name;
    QName qname;
    AttributeImpl(QName qname) {
      this.qname = qname;
    }
    AttributeImpl(String name) {
      this.name = name;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      serializeNamedIndex(serializer, "attribute", qname, name);
    }
  }
  protected class ElementAttributeImpl extends IndexImpl implements ElementAttribute {
    Element   element;
    Attribute attribute;
    ElementAttributeImpl(Element element, Attribute attribute) {
      this.element   = element;
      this.attribute = attribute;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      ((IndexImpl) element).innerSerialize(serializer);
      ((IndexImpl) attribute).innerSerialize(serializer);
    }
  }
  protected class FieldImpl extends IndexImpl implements Field {
    String name;
    FieldImpl(String name) {
      this.name = name;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "field");
      serializer.writeAttribute("name", name);
      serializer.writeEndElement();
    }
  }
  protected class JSONPropertyImpl extends IndexImpl implements JSONProperty {
    String name;
    JSONPropertyImpl(String name) {
      this.name = name;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeText(serializer, "json-property", name);
    }
  }
  protected class PathIndexImpl extends IndexImpl implements PathIndex {
    String path;
    PathIndexImpl(String path) {
      this.path = path;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeText(serializer, "path-index", path);
    }
  }
  protected class GeoJSONPropertyImpl extends IndexImpl implements GeospatialIndex {
    JSONProperty parent;
    JSONProperty jsonProperty;
    GeoJSONPropertyImpl(JSONProperty jsonProperty) {
      super();
      this.jsonProperty = jsonProperty;
    }
    GeoJSONPropertyImpl(JSONProperty parent, JSONProperty jsonProperty) {
      this(jsonProperty);
      this.parent  = parent;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      if (parent != null && parent instanceof JSONPropertyImpl) {
        JSONPropertyImpl parentImpl  = (JSONPropertyImpl) parent;
        writeText(serializer, "parent-property", parentImpl.name);
      }
      JSONPropertyImpl jsonPropertyImpl = (JSONPropertyImpl) jsonProperty;
      writeText(serializer, "json-property", jsonPropertyImpl.name);
    }
  }
  protected class GeoJSONPropertyPairImpl extends IndexImpl implements GeospatialIndex {
    JSONProperty parent;
    JSONProperty lat;
    JSONProperty lon;
    GeoJSONPropertyPairImpl(JSONProperty parent, JSONProperty lat, JSONProperty lon) {
      this.parent = parent;
      this.lat    = lat;
      this.lon    = lon;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      JSONPropertyImpl parentImpl = (JSONPropertyImpl) parent;
      JSONPropertyImpl latImpl    = (JSONPropertyImpl) lat;
      JSONPropertyImpl lonImpl    = (JSONPropertyImpl) lon;
      writeText(serializer, "parent-property", parentImpl.name);
      writeText(serializer, "lat-property", latImpl.name);
      writeText(serializer, "lon-property", lonImpl.name);
    }
  }
  protected class GeoElementImpl extends IndexImpl implements GeospatialIndex {
    Element parent;
    Element element;
    GeoElementImpl(Element element) {
      super();
      this.element = element;
    }
    GeoElementImpl(Element parent, Element element) {
      this(element);
      this.parent  = parent;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      if (parent != null && parent instanceof ElementImpl) {
        ElementImpl parentImpl  = (ElementImpl) parent;
        serializeNamedIndex(serializer, "parent",  parentImpl.qname,  parentImpl.name);
      } else if (parent != null && parent instanceof IndexImpl) {
        IndexImpl parentImpl  = (IndexImpl) parent;
        parentImpl.innerSerialize(serializer);
      }
      if ( element instanceof ElementImpl ) {
        ElementImpl elementImpl = (ElementImpl) element;
        serializeNamedIndex(serializer, "element", elementImpl.qname, elementImpl.name);
      } else if ( element instanceof IndexImpl ) {
        IndexImpl indexImpl  = (IndexImpl) element;
        indexImpl.innerSerialize(serializer);
      }
    }
  }
  protected class GeoElementPairImpl extends IndexImpl implements GeospatialIndex {
    Element parent;
    Element lat;
    Element lon;
    GeoElementPairImpl(Element parent, Element lat, Element lon) {
      this.parent = parent;
      this.lat    = lat;
      this.lon    = lon;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      ElementImpl parentImpl = (ElementImpl) parent;
      ElementImpl latImpl    = (ElementImpl) lat;
      ElementImpl lonImpl    = (ElementImpl) lon;
      serializeNamedIndex(serializer, "parent", parentImpl.qname, parentImpl.name);
      serializeNamedIndex(serializer, "lat", latImpl.qname, latImpl.name);
      serializeNamedIndex(serializer, "lon", lonImpl.qname, lonImpl.name);
    }
  }
  protected class GeoAttributePairImpl extends IndexImpl implements GeospatialIndex {
    Element   parent;
    Attribute lat;
    Attribute lon;
    GeoAttributePairImpl(Element parent, Attribute lat, Attribute lon) {
      this.parent = parent;
      this.lat    = lat;
      this.lon    = lon;
    }
    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      ElementImpl   parentImpl = (ElementImpl) parent;
      AttributeImpl latImpl    = (AttributeImpl) lat;
      AttributeImpl lonImpl    = (AttributeImpl) lon;
      serializeNamedIndex(serializer, "parent", parentImpl.qname, parentImpl.name);
      serializeNamedIndex(serializer, "lat", latImpl.qname, latImpl.name);
      serializeNamedIndex(serializer, "lon", lonImpl.qname, lonImpl.name);
    }
  }

  private class GeoBasePathImpl extends IndexImpl {
    PathIndex pathIndex;

    GeoBasePathImpl(PathIndex pathIndex) {
      this.pathIndex = pathIndex;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      PathIndexImpl pathIndexImpl = (PathIndexImpl) pathIndex;
      pathIndexImpl.innerSerialize(serializer);
      ;
    }
  }

  private class GeoPointPathImpl extends GeoBasePathImpl implements GeospatialIndex {
    GeoPointPathImpl(PathIndex pathIndex) {
      super(pathIndex);
    }
  }

  private class GeoRegionPathImpl extends GeoBasePathImpl implements GeospatialRegionIndex {
    CoordinateSystem coordinateSystem = null;

    GeoRegionPathImpl(PathIndex pathIndex) {
      super(pathIndex);
    }

    GeoRegionPathImpl(PathIndex pathIndex, CoordinateSystem coordinateSystem) {
      super(pathIndex);
      this.coordinateSystem = coordinateSystem;
    }
  }

  /**
   * A region matched by a geospatial query.
   */
  public interface Region extends StructuredQueryDefinition {
  }

  protected abstract class RegionImpl extends AbstractStructuredQuery {
  }

  public interface Point extends Region {
    double getLatitude();
    double getLongitude();
    String serialize();
    void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException;
  }

  protected class PointImpl extends RegionImpl implements Point {
    private double lat = 0.0;
    private double lon = 0.0;

    public PointImpl(double latitude, double longitude) {
      lat = latitude;
      lon = longitude;
    }

    public double getLatitude() {
      return lat;
    }
    public double getLongitude() {
      return lon;
    }

    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "point");
      writeText(serializer, "latitude", String.valueOf(lat));
      writeText(serializer, "longitude", String.valueOf(lon));
      serializer.writeEndElement();
    }
  }

  public interface Circle extends Region {}

  protected class CircleImpl extends RegionImpl implements Circle {
    private Point center = null;
    private double radius = 0.0;

    public CircleImpl(double latitude, double longitude, double radius) {
      center = new PointImpl(latitude, longitude);
      this.radius = radius;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "circle");
      writeText(serializer, "radius", String.valueOf(radius));
      center.innerSerialize(serializer);
      serializer.writeEndElement();
    }
  }

  public interface Box extends Region {}

  protected class BoxImpl extends RegionImpl implements Box {
    private double south, west, north, east;

    public BoxImpl(double south, double west, double north, double east) {
      this.south = south;
      this.west = west;
      this.north = north;
      this.east = east;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "box");
      writeText(serializer, "south", String.valueOf(south));
      writeText(serializer, "west",  String.valueOf(west));
      writeText(serializer, "north", String.valueOf(north));
      writeText(serializer, "east",  String.valueOf(east));
      serializer.writeEndElement();
    }
  }

  public interface Polygon extends Region {}

  protected class PolygonImpl extends RegionImpl implements Polygon {
    private Point[] points;

    public PolygonImpl(Point... points) {
      this.points = points;
    }

    @Override
    public void innerSerialize(XMLStreamWriter serializer) throws XMLStreamException {
      writeSearchElement(serializer, "polygon");
      for (Point point: points) {
        point.innerSerialize(serializer);
      }
      serializer.writeEndElement();
    }
  }

  private void checkQueries(StructuredQueryDefinition... queries) {
    if (queries != null) {
      for (StructuredQueryDefinition query: queries) {
        checkQuery(query);
      }
    }
  }
  private void checkQuery(StructuredQueryDefinition query) {
    if (query != null && !AbstractStructuredQuery.class.isAssignableFrom(
      query.getClass()))
    {
      throw new IllegalArgumentException(
        "Only built queries are supported: "+
          query.getClass().getName());
    }
  }
  private void checkRegions(Region... regions) {
    if (regions != null) {
      for (Region region: regions) {
        checkRegion(region);
      }
    }
  }
  private void checkRegion(Region region) {
    if (region != null && !RegionImpl.class.isAssignableFrom(
      region.getClass()))
    {
      throw new IllegalArgumentException(
        "Only built regions are supported: "+
          region.getClass().getName());
    }
  }

  static private XMLStreamWriter makeSerializer(OutputStream out) {
    XMLOutputFactory factory = XmlFactories.getOutputFactory();

    try {
      XMLStreamWriter serializer = factory.createXMLStreamWriter(out, "UTF-8");

      serializer.setDefaultNamespace(SEARCH_API_NS);
      serializer.setPrefix("xs",  XMLConstants.W3C_XML_SCHEMA_NS_URI);

      return serializer;
    } catch (Exception e) {
      throw new MarkLogicIOException(e);
    }
  }
  private String serializeRegions(RegionImpl... regions) {
    return serializeQueriesImpl((Object[]) regions);
  }

  private String serializeQueries(AbstractStructuredQuery... queries) {
    return serializeQueriesImpl((Object[]) queries);
  }

  private String serializeQueriesImpl(Object... objects) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      writeStructuredQueryImpl(baos, objects);
      return baos.toString("UTF-8");
    } catch (Exception e) {
      throw new MarkLogicIOException(e);
    }
  }
  private void writeStructuredQuery(OutputStream out, AbstractStructuredQuery... queries) {
    writeStructuredQueryImpl(out, (Object[]) queries);
  }
  private void writeStructuredQueryImpl(OutputStream out, Object... objects) {
    try {
      XMLStreamWriter serializer = makeSerializer(out);

// omit the XML prolog
//            serializer.writeStartDocument();
      writeSearchElement(serializer, "query");

      if (objects != null) {
        if (objects instanceof AbstractStructuredQuery[]) {
          if (namespaces != null) {
            for (String prefix : namespaces.getAllPrefixes()) {
              serializer.writeNamespace(prefix, namespaces.getNamespaceURI(prefix));
            }
          }
          for (AbstractStructuredQuery query: (AbstractStructuredQuery[]) objects) {
            query.innerSerialize(serializer);
          }
        } else if (objects instanceof RegionImpl[]) {
          for (RegionImpl region: (RegionImpl[]) objects) {
            region.innerSerialize(serializer);
          }
        }
      }

      serializer.writeEndElement();
//            serializer.writeEndDocument();
      serializer.flush();
      serializer.close();
    } catch (Exception e) {
      throw new MarkLogicIOException(e);
    }
  }

  static private void serializeNamedIndex(XMLStreamWriter serializer,
                                          String elemName, QName qname, String name)
    throws XMLStreamException
  {
    writeSearchElement(serializer, elemName);
    if (qname != null) {
      String ns = qname.getNamespaceURI();
      serializer.writeAttribute("ns", (ns != null) ? ns : "");
      serializer.writeAttribute("name", qname.getLocalPart());
    } else {
      serializer.writeAttribute("ns", "");
      serializer.writeAttribute("name", name);
    }
    serializer.writeEndElement();
  }

  static private void writeText(XMLStreamWriter serializer,
                                String container, Object object)
    throws XMLStreamException
  {
    if (object == null) {
      return;
    }
    writeSearchElement(serializer, container);
    serializer.writeCharacters(
      (object instanceof String) ?
        (String) object : object.toString()
    );
    serializer.writeEndElement();
  }
  static private void writeTextList(XMLStreamWriter serializer,
                                    String container, Object[] objects)
    throws XMLStreamException
  {
    if (objects == null) {
      return;
    }
    for (Object object: objects) {
      if ( object == null ) continue;
      writeSearchElement(serializer, container);
      serializer.writeCharacters(
        (object instanceof String) ?
          (String) object : object.toString()
      );
      serializer.writeEndElement();
    }
  }
  static private void writeQuery(XMLStreamWriter serializer, StructuredQueryDefinition query)
    throws XMLStreamException
  {
    ((AbstractStructuredQuery) query).innerSerialize(serializer);
  }
  static private void writeQueryList(XMLStreamWriter serializer,
                                     StructuredQueryDefinition... queries)
    throws XMLStreamException
  {
    if (queries == null) {
      return;
    }

    for (AbstractStructuredQuery query: convertQueries(queries)) {
      query.innerSerialize(serializer);
    }
  }
  static private void writeQuery(XMLStreamWriter serializer,
                                 String container, AbstractStructuredQuery query)
    throws XMLStreamException
  {
    writeSearchElement(serializer, container);
    if (query != null) {
      query.innerSerialize(serializer);
    }
    serializer.writeEndElement();
  }
  static private void writeQueryList(XMLStreamWriter serializer,
                                     String container, AbstractStructuredQuery... queries)
    throws XMLStreamException
  {
    writeSearchElement(serializer, container);
    if (queries != null) {
      for (AbstractStructuredQuery query: queries) {
        query.innerSerialize(serializer);
      }
    }
    serializer.writeEndElement();
  }
  static private AbstractStructuredQuery[] convertQueries(StructuredQueryDefinition... queries) {
    if (queries == null) {
      return null;
    }

    AbstractStructuredQuery[] innerQueries = new AbstractStructuredQuery[queries.length];
    for (int i=0; i < queries.length; i++) {
      innerQueries[i] = (AbstractStructuredQuery) queries[i];
    }
    return innerQueries;
  }

  class StructuredQueryXMLWriter
    extends BaseHandle<OperationNotSupported, OutputStreamSender>
    implements StructureWriteHandle, XMLWriteHandle, BufferableHandle, OutputStreamSender
  {
    StructuredQueryDefinition[] queries;
    StructuredQueryXMLWriter(StructuredQueryDefinition[] queries) {
      super();
      super.setResendable(true);
      super.setFormat(Format.XML);
      this.queries = queries;
    }
    @Override
    protected OutputStreamSender sendContent() {
      return this;
    }
    @Override
    public void setFormat(Format format) {
      if (format != Format.XML)
        throw new IllegalArgumentException("StructuredQueryWriter supports the XML format only");
    }
    @Override
    public void fromBuffer(byte[] buffer) {
      throw new UnsupportedOperationException("Cannot set StructuredQueryWriter from buffer");
    }
    @Override
    public byte[] toBuffer() {
      try {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        write(buffer);

        return buffer.toByteArray();
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      }
    }

    @Override
    public String toString() {
      try {
        return new String(toBuffer(), "UTF-8");
      } catch (IOException e) {
        throw new MarkLogicIOException(e);
      }
    }

    @Override
    public void write(OutputStream out) throws IOException {
      writeStructuredQuery(out, convertQueries(queries));
    }
  }

  /**
   * Converts the list of options used for a range query to an array
   * as a convenience.
   * @param options    the list of range query options
   * @return    the range query options as an array
   */
  public String[] rangeOptions(String... options) {
    return options;
  }

  /**
   * Defines a boost query for the matching and boosting query definitions.  The matching
   * or boosting query definitions can each be an AND or OR query definition for complex
   * combinations of criteria.
   * @param matchingQuery the query definition that filters documents
   * @param boostingQuery    the query definition that increases the rank for some filtered documents
   * @return    the StructuredQueryDefinition for the boost query
   */
  public StructuredQueryDefinition boost(StructuredQueryDefinition matchingQuery, StructuredQueryDefinition boostingQuery) {
    return new BoostQuery(matchingQuery, boostingQuery);
  }
  /**
   * Defines a not-in query for the positive and negative query definitions.  These query definitions
   * can each be an AND or OR query definition for complex combinations of criteria.
   * @param positive the query definition that includes documents
   * @param negative the query definition that excludes documents
   * @return    the StructuredQueryDefinition for the not-in query
   */
  public StructuredQueryDefinition notIn(StructuredQueryDefinition positive, StructuredQueryDefinition negative) {
    return new NotInQuery(positive, negative);
  }

  /**
   * Gets the namespace bindings used for the query.
   * @return    the namespace bindings
   */
  public IterableNamespaceContext getNamespaces() {
    return namespaces;
  }

  /**
   * Specifies the namespace bindings used for the query.  You can use
   * the {@link com.marklogic.client.util.EditableNamespaceContext EditableNamespaceContext}
   * class to instantiate a set of bindings between namespace prefixes and Uris.
   * @param namespaces    the namespace bindings
   */
  public void setNamespaces(IterableNamespaceContext namespaces) {
    EditableNamespaceContext newNamespaces = makeNamespaces();
    if (namespaces != null) {
      for (String prefix: namespaces.getAllPrefixes()) {
        String nsUri = namespaces.getNamespaceURI(prefix);
        if (!newNamespaces.containsKey(prefix)) {
          newNamespaces.put(prefix, nsUri);
        } else if (!nsUri.equals(newNamespaces.getNamespaceURI(prefix))) {
          throw new IllegalArgumentException(
            "Cannot change namespace URI for prefix: "+prefix);
        }
      }
    }
    this.namespaces = newNamespaces;
  }
  private EditableNamespaceContext makeNamespaces() {
    EditableNamespaceContext newNamespaces = new EditableNamespaceContext();
    for (Map.Entry<String, String> entry: reserved.entrySet()) {
      newNamespaces.put(entry.getKey(), entry.getValue());
    }
    return newNamespaces;
  }

  /**
   * The Allen and ISO SQL 2011 temporal operators available for use in
   * {@link #temporalPeriodRange temporalPeriodRange}
   * or {@link #temporalPeriodCompare temporalPeriodCompare} queries.
   * @see <a href="http://docs.marklogic.com/guide/temporal/searching#id_78584">
   * Temporal Developer's Guide -&gt; Period Comparison Operators</a>
   */
  public enum TemporalOperator {
    ALN_EQUALS,
    ALN_CONTAINS,
    ALN_CONTAINED_BY,
    ALN_MEETS,
    ALN_MET_BY,
    ALN_BEFORE,
    ALN_AFTER,
    ALN_STARTS,
    ALN_STARTED_BY,
    ALN_FINISHES,
    ALN_FINISHED_BY,
    ALN_OVERLAPS,
    ALN_OVERLAPPED_BY,
    ISO_CONTAINS,
    ISO_OVERLAPS,
    ISO_SUCCEEDS,
    ISO_PRECEDES,
    ISO_IMM_SUCCEEDS,
    ISO_IMM_PRECEDES,
    ISO_EQUALS;
  };
  /**
   * An axis for use in {@link #temporalPeriodRange temporalPeriodRange}
   * or {@link #temporalPeriodCompare temporalPeriodCompare} queries.
   */
  public interface Axis {};
  /**
   * A temporal period for use in {@link #temporalPeriodRange temporalPeriodRange}
   * queries.
   */
  public interface Period {};

  /**
   * Identify an axis for use in {@link #temporalPeriodRange temporalPeriodRange}
   * or {@link #temporalPeriodCompare temporalPeriodCompare} queries.
   * @param name the name of the axis as configured in the server
   * @return a temporal axis
   */
  public StructuredQueryBuilder.Axis axis(String name) {
    return new TemporalAxis(name);
  }

  /**
   * Construct a temporal period for use in {@link #temporalPeriodRange temporalPeriodRange}
   * queries.
   * @param start the start date/time for this period
   * @param end   the end date/time for this period
   * @return a temporal period
   */
  public StructuredQueryBuilder.Period period(Calendar start, Calendar end) {
    return new TemporalPeriod(DatatypeConverter.printDateTime(start),
      DatatypeConverter.printDateTime(end));
  }

  /**
   * Construct a temporal period for use in {@link #temporalPeriodRange temporalPeriodRange}
   * queries.
   * @param start the start date/time for this period, in ISO 8601 format
   * @param end   the end date/time for this period, in ISO 8601 format
   * @return a temporal period
   */
  public StructuredQueryBuilder.Period period(String start, String end) {
    return new TemporalPeriod(start, end);
  }

  /**
   * Matches documents that have a value in the specified axis that matches the specified
   * period using the specified operator.
   * @param axis the axis of document temporal values used to determine which documents have
   *        values that match this query
   * @param operator the operator used to determine if values in the axis match the specified period
   * @param period the period considered using the operator
   * @param options string options from the list for
   *     <a href="http://docs.marklogic.com/cts:period-range-query">cts:period-range-query calls</a>
   * @return a query to filter by comparing a temporal axis to period values
   * @see <a href="http://docs.marklogic.com/cts:period-range-query">cts:period-range-query</a>
   * @see <a href="http://docs.marklogic.com/guide/search-dev/structured-query#id_91434">
   *      Structured Queries: period-range-query</a>
   */
  public StructuredQueryDefinition temporalPeriodRange(Axis axis, TemporalOperator operator,
                                                       Period period, String... options)
  {
    if ( axis == null ) throw new IllegalArgumentException("axis cannot be null");
    if ( period == null ) throw new IllegalArgumentException("period cannot be null");
    return temporalPeriodRange(new Axis[] {axis}, operator, new Period[] {period}, options);
  }

  /**
   * Matches documents that have a value in the specified axis that matches the specified
   * periods using the specified operator.
   * @param axes the set of axes of document temporal values used to determine which documents have
   *        values that match this query
   * @param operator the operator used to determine if values in the axis match the specified period
   * @param periods the periods considered using the operator.  When multiple periods are specified,
   *     the query matches if a value matches any period.
   * @param options string options from the list for
   *     <a href="http://docs.marklogic.com/cts:period-range-query">cts:period-range-query calls</a>
   * @return a query to filter by comparing a temporal axis to period values
   * @see <a href="http://docs.marklogic.com/cts:period-range-query">cts:period-range-query</a>
   * @see <a href="http://docs.marklogic.com/guide/search-dev/structured-query#id_91434">
   *      Structured Queries: period-range-query</a>
   */
  public StructuredQueryDefinition temporalPeriodRange(Axis[] axes, TemporalOperator operator,
                                                       Period[] periods, String... options)
  {
    if ( axes == null ) throw new IllegalArgumentException("axes cannot be null");
    if ( operator == null ) throw new IllegalArgumentException("operator cannot be null");
    if ( periods == null ) throw new IllegalArgumentException("periods cannot be null");
    return new TemporalPeriodRangeQuery(axes, operator, periods, options);
  }

  /**
   * Matches documents that have a relevant pair of period values. Values from axis1 must match
   * values from axis2 using the specified operator.
   * @param axis1 the first axis of document temporal values
   *        values that match this query
   * @param operator the operator used to determine if values in the axis match the specified period
   * @param axis2 the second axis of document temporal values
   * @param options string options from the list for
   *     <a href="http://docs.marklogic.com/cts:period-compare-query">cts:period-compare-query calls</a>
   * @return a query to filter by comparing temporal axes
   * @see <a href="http://docs.marklogic.com/cts:period-compare-query">cts:period-compare-query</a>
   * @see <a href="http://docs.marklogic.com/guide/search-dev/structured-query#id_19798">
   *      Structured Queries: period-compare-query</a>
   */
  public StructuredQueryDefinition temporalPeriodCompare(Axis axis1, TemporalOperator operator,
                                                         Axis axis2, String... options)
  {
    if ( axis1 == null ) throw new IllegalArgumentException("axis1 cannot be null");
    if ( operator == null ) throw new IllegalArgumentException("operator cannot be null");
    if ( axis2 == null ) throw new IllegalArgumentException("axis2 cannot be null");
    return new TemporalPeriodCompareQuery(axis1, operator, axis2, options);
  }

  /**
   * Matches documents with LSQT prior to timestamp
   * @param temporalCollection the temporal collection to query
   * @param time documents with lsqt equal to or prior to this timestamp will match
   * @param weight the weight for this query
   * @param options string options from the list for
   *     <a href="http://docs.marklogic.com/cts:lsqt-query">cts:lsqt-query calls</a>
   * @return a query to filter by lsqt
   * @see <a href="http://docs.marklogic.com/cts:lsqt-query">cts:lsqt-query</a>
   * @see <a href="http://docs.marklogic.com/guide/search-dev/structured-query#id_85930">
   *      Structured Queries: lsqt-query</a>
   */
  public StructuredQueryDefinition temporalLsqtQuery(String temporalCollection, Calendar time,
                                                     double weight, String... options)
  {
    if ( temporalCollection == null ) throw new IllegalArgumentException("temporalCollection cannot be null");
    return new TemporalLsqtQuery(temporalCollection, DatatypeConverter.printDateTime(time), weight, options);
  }

  /**
   * Matches documents with LSQT prior to timestamp
   * @param temporalCollection the temporal collection to query
   * @param timestamp timestamp in ISO 8601 format - documents with lsqt equal to or
   *        prior to this timestamp will match
   * @param weight the weight for this query
   * @param options string options from the list for
   *     <a href="http://docs.marklogic.com/cts:lsqt-query">cts:lsqt-query calls</a>
   * @return a query to filter by lsqt
   * @see <a href="http://docs.marklogic.com/cts:lsqt-query">cts:lsqt-query</a>
   * @see <a href="http://docs.marklogic.com/guide/search-dev/structured-query#id_85930">
   *      Structured Queries: lsqt-query</a>
   */
  public StructuredQueryDefinition temporalLsqtQuery(String temporalCollection, String timestamp,
                                                     double weight, String... options)
  {
    if ( temporalCollection == null ) throw new IllegalArgumentException("temporalCollection cannot be null");
    return new TemporalLsqtQuery(temporalCollection, timestamp, weight, options);
  }

  /**
   * Matches documents with timestamp prior to the given timestamp.
   * @param timestamp time in ISO 8601 format - documents with timestamp equal to or
   *        prior to this timestamp will match
   * @return a query to filter.
   */
  public StructuredQueryDefinition beforeQuery(long timestamp)
  {
	  if (timestamp == 0) throw new IllegalArgumentException("timestamp cannot be zero");
	  return new TimeQuery(Long.toUnsignedString(timestamp), "before-query");
  }

  /**
   * Matches documents with timestamp after the given timestamp.
   * @param timestamp time in ISO 8601 format - documents with timestamp after this time will match.
   * @return a query to filter.
   */
  public StructuredQueryDefinition afterQuery(long timestamp)
  {
	  if (timestamp == 0) throw new IllegalArgumentException("timestamp cannot be zero");
	  return new TimeQuery(Long.toUnsignedString(timestamp), "after-query");
  }

	/**
	 * Convenience method for writing an element in the "search" namespace.
	 *
	 * @param serializer
	 * @param elementName
	 * @throws XMLStreamException
	 */
	private static void writeSearchElement(XMLStreamWriter serializer, String elementName) throws XMLStreamException {
		serializer.writeStartElement(SEARCH_API_NS, elementName);
	}
}
