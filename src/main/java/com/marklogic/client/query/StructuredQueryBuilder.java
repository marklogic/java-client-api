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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.impl.AbstractQueryDefinition;
import com.marklogic.client.impl.RawQueryDefinitionImpl;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.OperationNotSupported;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;
import com.marklogic.client.util.EditableNamespaceContext;
import com.marklogic.client.util.IterableNamespaceContext;

/**
 * StructuredQueryBuilder builds a query for documents in the database.
 * Several concrete the query definition classes are now deprecated because you can
 * instead use the more general {@link StructuredQueryDefinition StructuredQueryDefinition} interface
 * as the type for query definitions.  For instance, here is a
 * forward-compatible approach for capturing an AND query definition
 * in a variable:
 * <pre>StructuredQueryDefinition andQry = structuredQueryBuilder.and(... query definitions ...);</pre>
 */
public class StructuredQueryBuilder {
	final static private String SEARCH_API_NS="http://marklogic.com/appservices/search";

	/*
	 * This map is used to prevent reuse of reserved prefixes in path expressions.
	 */
	final static private Map<String,String> reserved = new HashMap<String,String>();
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
     * A comparison operator for use in range queries.
     */
	public enum Operator {
        LT, LE, GT, GE, EQ, NE;
    }
    
    /**
     * Whether a query should search the document or its associated properties.
     * The value "DOCUMENT" is here for backward-compatibility but deprecated
     * in favor of DOCUMENTS.
     */
    public enum FragmentScope {
    	@Deprecated DOCUMENT, DOCUMENTS, PROPERTIES;
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
     * @param optionsName	the name of the persisted query options
     */
    public StructuredQueryBuilder(String optionsName) {
    	this((IterableNamespaceContext) null);
        builderOptionsURI = optionsName;
    }
    /**
     * Constructs a query builder for queries using the specified
     * namespace bindings.
     * @param namespaces	the bindings of prefixes and namespaces
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
     * @param optionsName	the name of the persisted query options
     * @param namespaces	the bindings of prefixes and namespaces
     */
    public StructuredQueryBuilder(String optionsName, IterableNamespaceContext namespaces) {
    	this(namespaces);
        builderOptionsURI = optionsName;
    }

    /**
     * Builds a structured query in XML from the list of query definitions.
     * The structured query can be passed to the search() method of QueryManager.
     * @param queries	the query definitions
     * @return	the structured query
     */
    public RawStructuredQueryDefinition build(StructuredQueryDefinition... queries) {
    	checkQueries(queries);
		return new RawQueryDefinitionImpl.Structured(
				new StructuredQueryXMLWriter(queries), builderOptionsURI
				);
    }

    /**
     * Defines an AND query over the list of query definitions.
     * @param queries	the query definitions
     * @return	the StructuredQueryDefinition for the AND query
     */
    public AndQuery and(StructuredQueryDefinition... queries) {
    	checkQueries(queries);
        return new AndQuery(queries);
    }

    /**
     * Defines an OR query over the list of query definitions.
     * @param queries	the query definitions
     * @return	the StructuredQueryDefinition for the OR query
     */
    public OrQuery or(StructuredQueryDefinition... queries) {
    	checkQueries(queries);
    	return new OrQuery(queries);
    }

    /**
     * Defines a NOT query for a query definition. To negate
     * a list of query definitions, define an AND or
     * OR query over the list and define the NOT query over
     * the AND or OR query.
     * @param query	the query definition
     * @return	the StructuredQueryDefinition for the NOT query
     */
    public NotQuery not(StructuredQueryDefinition query) {
    	checkQuery(query);
        return new NotQuery(query);
    }

    /**
     * Defines an AND NOT query combining a positive and negative
     * query. You can use an AND or OR query over a list of query
     * definitions as the positive or negative query.
     * @param positive	the positive query definition
     * @param negative	the negative query definition
     * @return	the StructuredQueryDefinition for the AND NOT query
     */
    public AndNotQuery andNot(StructuredQueryDefinition positive, StructuredQueryDefinition negative) {
    	checkQuery(positive);
    	checkQuery(negative);
        return new AndNotQuery(positive, negative);
    }

    /**
     * Defines a NEAR query over the list of query definitions
     * with default parameters.
     * @param queries	the query definitions
     * @return	the StructuredQueryDefinition for the NEAR query
     */
    public NearQuery near(StructuredQueryDefinition... queries) {
    	checkQueries(queries);
        return new NearQuery(queries);
    }

    /**
     * Defines a NEAR query over the list of query definitions
     * with specified parameters.
     * @param distance	the proximity for the query terms
     * @param weight	the weight for the query
     * @param order	the ordering for the query terms
     * @param queries	the query definitions
     * @return	the StructuredQueryDefinition for the NEAR query
     */
    public NearQuery near(int distance, double weight, Ordering order, StructuredQueryDefinition... queries) {
    	checkQueries(queries);
        return new NearQuery(distance, weight, order, queries);
    }

    /**
     * Associates a query with the content of documents (as opposed to
     * the properties of documents).
     * @param query	the query definition
     * @return	the StructuredQueryDefinition for the document fragment query
     */
    public DocumentFragmentQuery documentFragment(StructuredQueryDefinition query) {
    	checkQuery(query);
        return new DocumentFragmentQuery(query);
    }

    /**
     * Associates a query with the properties of documents (as opposed to
     * the content of documents).
     * @param query	the query definition
     * @return	the StructuredQueryDefinition for the properties query
     */
    public PropertiesQuery properties(StructuredQueryDefinition query) {
    	checkQuery(query);
        return new PropertiesQuery(query);
    }

    /**
     * Associates a query with durable locks on documents (as opposed to
     * the content or properties of documents). Such lock fragments are
     * created with xdmp:lock-acquire().
     * @param query	the query definition
     * @return	the StructuredQueryDefinition for the locks query
     */
    public LocksQuery locks(StructuredQueryDefinition query) {
    	checkQuery(query);
        return new LocksQuery(query);
    }

    /**
     * Matches a query within the substructure contained by an element or JSON key.
     * @param index	the element or JSON key
     * @param query	the query over the contained substructure
     * @return	the StructuredQueryDefinition for the container query
     */
    public StructuredQueryDefinition containerQuery(ContainerIndex index, StructuredQueryDefinition query) {
    	checkQuery(query);
        return new ContainerQuery(index, query);
    }

    /**
     * Matches documents belonging to at least one
     * of the criteria collections.
     * @param uris	the identifiers for the criteria collections
     * @return	the StructuredQueryDefinition for the collection query
     */
    public CollectionQuery collection(String... uris) {
        return new CollectionQuery(uris);
    }

    /**
     * Matches documents at the specified depth within at least one
     * of the criteria directories.
     * @param isInfinite	true to match a document at any level of depth
     * @param uris	the identifiers for the criteria directories
     * @return	the StructuredQueryDefinition for the directory query
     */
    public DirectoryQuery directory(boolean isInfinite, String... uris) {
        return new DirectoryQuery(isInfinite, uris);
    }

    /**
     * Matches documents at the specified depth within at least one
     * of the criteria directories.
     * @param depth specifies how many subdirectories deep to traverse  
     * A value of 1 means to exclude subdirectories.
     * @param uris	the identifiers for the criteria directories
     * @return	the StructuredQueryDefinition for the directory query
     */
    public DirectoryQuery directory(int depth, String... uris) {
		return new DirectoryQuery(depth, uris);
    }
    
    /**
     * Matches the specified documents.
     * @param uris	the identifiers for the documents
     * @return	the StructuredQueryDefinition for the document query
     */
    public DocumentQuery document(String... uris) {
        return new DocumentQuery(uris);
    }
    
	 
    /**
     * Matches documents containing the specified terms.
     * @param terms	the possible terms to match
     * @return	the StructuredQueryDefinition for the term query
     */
    public TermQuery term(String... terms) {
        return new TermQuery(null, terms);
    }
    /**
     * Matches documents containing the specified terms, modifying
     * the contribution of the match to the score with the weight.
     * @param weight	the multiplier for the match in the document ranking
     * @param terms	the possible terms to match
     * @return	the StructuredQueryDefinition for the term query
     */
    public TermQuery term(double weight, String... terms) {
        return new TermQuery(weight, terms);
    }

    /**
     * Matches an element, attribute, json key, or field
     * that has a value with the same string value as at least one
     * of the criteria values.
     * @param index	the value container
     * @param values	the possible values to match
     * @return	the StructuredQueryDefinition for the value query
     */
    public StructuredQueryDefinition value(TextIndex index, String... values) {
        return new ValueQuery(index, null, null, null, values);
    }
    /**
     * Matches an element, attribute, json key, or field
     * that has a value with the same boolean value as at least one
     * of the criteria values.
     * @param index	the value container
     * @param value	either true or false
     * @return	the StructuredQueryDefinition for the value query
     */
    public StructuredQueryDefinition value(TextIndex index, Boolean value) {
        return new ValueQuery(index, null, null, null, new Object[] {value});
    }
    /**
     * Matches an element, attribute, json key, or field
     * that has a value with the same numeric value as at least one
     * of the criteria values.
     * @param index	the value container
     * @param values	the possible values to match
     * @return	the StructuredQueryDefinition for the value query
     */
    public StructuredQueryDefinition value(TextIndex index, Number... values) {
        return new ValueQuery(index, null, null, null, values);
    }
    /**
     * Matches an element, attribute, json key, or field
     * that has a value with the same string value as at least one
     * of the criteria values.
     * @param index	the value container
     * @param scope	whether the query matches the document content or properties
     * @param options	options for fine tuning the query
     * @param weight	the multiplier for the match in the document ranking
     * @param values	the possible values to match
     * @return	the StructuredQueryDefinition for the value query
     */
    public StructuredQueryDefinition value(TextIndex index, FragmentScope scope, String[] options, double weight, String... values) {
        return new ValueQuery(index, scope, options, weight, values);
    }
    /**
     * Matches an element, attribute, json key, or field
     * that has a value with the same boolean value as at least one
     * of the criteria values.
     * @param index	the value container
     * @param scope	whether the query matches the document content or properties
     * @param options	options for fine tuning the query
     * @param weight	the multiplier for the match in the document ranking
     * @param value		either true or false
     * @return	the StructuredQueryDefinition for the value query
     */
    public StructuredQueryDefinition value(TextIndex index, FragmentScope scope, String[] options, double weight, Boolean value) {
        return new ValueQuery(index, scope, options, weight, new Object[] {value});
    }
    /**
     * Matches an element, attribute, json key, or field
     * that has a value with the same numeric value as at least one
     * of the criteria values.
     * @param index	the value container
     * @param scope	whether the query matches the document content or properties
     * @param options	options for fine tuning the query
     * @param weight	the multiplier for the match in the document ranking
     * @param values	the possible values to match
     * @return	the StructuredQueryDefinition for the value query
     */
    public StructuredQueryDefinition value(TextIndex index, FragmentScope scope, String[] options, double weight, Number... values) {
        return new ValueQuery(index, scope, options, weight, values);
    }

    /**
     * Matches an element, attribute, json key, or field
     * that has at least one of the criteria words.
     * @param index	the word container
     * @param words	the possible words to match
     * @return	the StructuredQueryDefinition for the word query
     */
    public StructuredQueryDefinition word(TextIndex index, String... words) {
        return new WordQuery(index, null, null, null, words);
    }
    /**
     * Matches an element, attribute, json key, or field
     * that has at least one of the criteria words.
     * @param index	the word container
     * @param scope	whether the query matches the document content or properties
     * @param options	options for fine tuning the query
     * @param weight	the multiplier for the match in the document ranking
     * @param words	the possible words to match
     * @return	the StructuredQueryDefinition for the word query
     */
    public StructuredQueryDefinition word(TextIndex index, FragmentScope scope,
    		String[] options, double weight, String... words) {
        return new WordQuery(index, scope, options, weight, words);
    }

    /**
     * Matches an element, attribute, json key, field, or path
     * whose value that has the correct datatyped comparison with 
     * one of the criteria values.
     * @param index	the range container
     * @param type	the datatype of the container and specified values
     * @param operator	the comparison with the criteria values
     * @param values	the possible datatyped values for the comparison
     * @return	the StructuredQueryDefinition for the range query
     */
    public StructuredQueryDefinition range(RangeIndex index, String type, 
    		Operator operator, Object... values) {
        return new RangeQuery(index, type, null, null, null, operator, values);
    }
    /**
     * Matches an element, attribute, json key, field, or path
     * whose value that has the correct datatyped comparison with 
     * one of the criteria values.
     * @param index	the range container
     * @param type
     * @param collation	the identifier for the strategy for comparing types 
     * @param operator	the comparison with the criteria values
     * @param values	the possible datatyped values for the comparison
     * @return	the StructuredQueryDefinition for the range query
     */
    public StructuredQueryDefinition range(RangeIndex index, String type, String collation,
    		Operator operator, Object... values) {
        return new RangeQuery(index, type, collation, null, null, operator, values);
    }
    /**
     * Matches an element, attribute, json key, field, or path
     * whose value that has the correct datatyped comparison with 
     * one of the criteria values.
     * @param index	the range container
     * @param type
     * @param collation	the identifier for the strategy for comparing types 
     * @param scope	whether the query matches the document content or properties
     * @param operator	the comparison with the criteria values
     * @param values	the possible datatyped values for the comparison
     * @return	the StructuredQueryDefinition for the range query
     */
    public StructuredQueryDefinition range(RangeIndex index, String type, String collation,
    		FragmentScope scope, Operator operator,
    		Object... values) {
        return new RangeQuery(index, type, collation, scope, null, operator, values);
    }
    /**
     * Matches an element, attribute, json key, field, or path
     * whose value that has the correct datatyped comparison with 
     * one of the criteria values.
     * @param index	the range container
     * @param type	the datatype of the container and specified values
     * @param options	options for fine tuning the query
     * @param operator	the comparison with the criteria values
     * @param values	the possible datatyped values for the comparison
     * @return	the StructuredQueryDefinition for the range query
     */
    public StructuredQueryDefinition range(RangeIndex index, String type, String[] options, Operator operator,
    		Object... values) {
        return new RangeQuery(index, type, null, null, options, operator, values);
    }
    /**
     * Matches an element, attribute, json key, field, or path
     * whose value that has the correct datatyped comparison with 
     * one of the criteria values.
     * @param index	the range container
     * @param type
     * @param collation	the identifier for the strategy for comparing types 
     * @param options	options for fine tuning the query
     * @param operator	the comparison with the criteria values
     * @param values	the possible datatyped values for the comparison
     * @return	the StructuredQueryDefinition for the range query
     */
    public StructuredQueryDefinition range(RangeIndex index, String type, String collation,
    		String[] options, Operator operator, Object... values) {
        return new RangeQuery(index, type, collation, null, options, operator, values);
    }
    /**
     * Matches an element, attribute, json key, field, or path
     * whose value that has the correct datatyped comparison with 
     * one of the criteria values.
     * @param index	the range container
     * @param type
     * @param collation	the identifier for the strategy for comparing types 
     * @param scope	whether the query matches the document content or properties
     * @param options	options for fine tuning the query
     * @param operator	the comparison with the criteria values
     * @param values	the possible datatyped values for the comparison
     * @return	the StructuredQueryDefinition for the range query
     */
    public StructuredQueryDefinition range(RangeIndex index, String type, String collation,
    		FragmentScope scope, String[] options, Operator operator,
    		Object... values) {
        return new RangeQuery(index, type, collation, scope, options, operator, values);
    }

    /**
     * Matches an element, element pair, element attribute, pair, or path
     * specifying a geospatial point that appears within one of the criteria regions.
     * @param index	the container for the coordinates of the geospatial point
     * @param regions	the possible regions containing the point
     * @return	the StructuredQueryDefinition for the geospatial query
     */
    public StructuredQueryDefinition geospatial(GeospatialIndex index, Region... regions) {
    	checkRegions(regions);
        return new GeospatialQuery(index, null, regions, null);
    }
    /**
     * Matches an element, element pair, element attribute, pair, or path
     * specifying a geospatial point that appears within one of the criteria regions.
     * @param index	the container for the coordinates of the geospatial point
     * @param scope	whether the query matches the document content or properties
     * @param options	options for fine tuning the query
     * @param regions	the possible regions containing the point
     * @return	the StructuredQueryDefinition for the geospatial query
     */
    public StructuredQueryDefinition geospatial(GeospatialIndex index, FragmentScope scope, String[] options, Region... regions) {
    	checkRegions(regions);
        return new GeospatialQuery(index, scope, regions, options);
    }

    /**
     * Identifies a namespaced element to match with a query.
     * @param qname	the name of the element
     * @return	the element identifier
     */
    public Element element(QName qname) {
    	return new ElementImpl(qname);
    }
    /**
     * Identifies a simple element to match with a query.
     * @param name	the name of the element
     * @return	the element identifier
     */
    public Element element(String name) {
    	return new ElementImpl(name);
    }
    /**
     * Identifies a namespaced attribute to match with a query.
     * @param qname	the name of the attribute 
     * @return	the attribute identifier
     */
    public Attribute attribute(QName qname) {
    	return new AttributeImpl(qname);
    }
    /**
     * Identifies a simple attribute to match with a query.
     * @param name	the name of the attribute
     * @return	the attribute identifier
     */
    public Attribute attribute(String name) {
    	return new AttributeImpl(name);
    }
    /**
     * Identifies an element having an attribute to match with a query.
     * @param element	the element identifier
     * @param attribute	the attribute identifier
     * @return	the identifier for the element-attribute pair
     */
    public ElementAttribute elementAttribute(Element element, Attribute attribute) {
    	return new ElementAttributeImpl(element, attribute);
    }
    /**
     * Identifies a field to match with a query.  Fields are defined
     * in the configuration for the database. 
     * @param name	the name of the field
     * @return	the identifier for the field
     */
    public Field field(String name) {
    	return new FieldImpl(name);
    }
    /**
     * Identifies a JSON key to match with a query.
     * @param name	the name of the JSON key
     * @return	the identifier for the JSON key
     */
    public JSONProperty jsonProperty(String name) {
    	return new JSONPropertyImpl(name);
    }
    /**
     * Identifies a path index to match with a query.
     * @param path	the indexed path
     * @return	the identifier for the path index 
     */
    public PathIndex pathIndex(String path) {
    	return new PathIndexImpl(path);
    }
    /**
     * Identifies an element whose text has the latitude and longitude
     * coordinates to match with a geospatial query.
     * @param element	the element containing the geospatial coordinates
     * @return	the specification for the index on the geospatial coordinates
     */
    public GeospatialIndex geoElement(Element element) {
    	return new GeoElementImpl(element);
    }
    /**
     * Identifies a parent element with a child element whose text has
     * the latitude and longitude coordinates to match with a geospatial query.
     * @param parent	the parent of the element with the coordinates
     * @param element	the element containing the geospatial coordinates
     * @return	the specification for the index on the geospatial coordinates
     */
    public GeospatialIndex geoElement(Element parent, Element element) {
    	return new GeoElementImpl(parent, element);
    }
    /**
     * Identifies a parent element with child latitude and longitude elements
     * to match with a geospatial query.
     * @param parent	the parent of the element with the coordinates
     * @param lat	the element with the latitude coordinate
     * @param lon	the element with the longitude coordinate
     * @return	the specification for the index on the geospatial coordinates
     */
    public GeospatialIndex geoElementPair(Element parent, Element lat, Element lon) {
    	return new GeoElementPairImpl(parent, lat, lon);
    }
    /**
     * Identifies a parent element with child latitude and longitude attributes
     * to match with a geospatial query.
     * @param parent	the parent of the element with the coordinates
     * @param lat	the attribute with the latitude coordinate
     * @param lon	the attribute with the longitude coordinate
     * @return	the specification for the index on the geospatial coordinates
     */
    public GeospatialIndex geoAttributePair(Element parent, Attribute lat, Attribute lon) {
    	return new GeoAttributePairImpl(parent, lat, lon);
    }
    /**
     * Identifies a path with the latitude and longitude to match
     * with a geospatial query.
     * @param pathIndex	the indexed path
     * @return	the specification for the index on the geospatial coordinates
     */
    public GeospatialIndex geoPath(PathIndex pathIndex) {
    	return new GeoPathImpl(pathIndex);
    }

    /**
     * Specifies a geospatial point.
     * @param latitude	the latitude coordinate
     * @param longitude	the longitude coordinate
     * @return	the definition of the point
     */
    public Point point(double latitude, double longitude) {
        return new Point(latitude, longitude);
    }

    /**
     * Specifies a geospatial region as a circle,
     * supplying coordinates for the center.
     * @param latitude	the latitude coordinate of the center
     * @param longitude	the longitude coordinate of the center
     * @param radius	the radius of the circle
     * @return	the definition of the circle
     */
    public Circle circle(double latitude, double longitude, double radius) {
        return new Circle(latitude, longitude, radius);
    }
    /**
     * Specifies a geospatial region as a circle,
     * supplying a point for the center.
     * @param center	the point defining the center
     * @param radius	the radius of the circle
     * @return	the definition of the circle
     */
    public Circle circle(Point center, double radius) {
        return new Circle(center.getLatitude(), center.getLongitude(), radius);
    }

    /**
     * Specifies a geospatial region as a box, supplying
     * the coordinates for the perimeter.
     * @param south	the latitude of the south coordinate
     * @param west	the longitude of the west coordinate
     * @param north	the latitude of the north coordinate
     * @param east	the longitude of the east coordinate
     * @return	the definition of the box
     */
    public Box box(double south, double west, double north, double east) {
        return new Box(south, west, north, east);
    }

    /**
     * Specifies a geospatial region as an arbitrary polygon.
     * @param points	the list of points defining the perimeter of the region
     * @return	the definition of the polygon
     */
    public Polygon polygon(Point... points) {
        return new Polygon(points);
    }

    /**
     * Matches a query within the substructure of the container specified
     * by the constraint.
     * @param constraintName	the constraint definition
     * @param query	the query definition
     * @return	the StructuredQueryDefinition for the element constraint query
     */
    public StructuredQueryDefinition containerConstraint(String constraintName, StructuredQueryDefinition query) {
    	checkQuery(query);
        return new ContainerConstraintQuery(constraintName, query);
    }

    /**
     * @deprecated Matches a query within the substructure of the container specified
     * by the constraint.
     * This method is deprecated in favor of the more general
     * {@link #containerConstraint(String, StructuredQueryDefinition) containerConstraint()} method.
     * @param constraintName	the constraint definition
     * @param query	the query definition
     * @return	the StructuredQueryDefinition for the element constraint query
     */
	@Deprecated
    public ElementConstraintQuery elementConstraint(String constraintName, StructuredQueryDefinition query) {
    	checkQuery(query);
        return new ElementConstraintQuery(constraintName, query);
    }


    /**
     * Associates a query with the properties of documents (as opposed to
     * the content of documents) with the specified constraint.
     * @param constraintName	the constraint definition
     * @param query	the query definition
     * @return	the StructuredQueryDefinition for the properties constraint query
     */
    public PropertiesConstraintQuery propertiesConstraint(String constraintName, StructuredQueryDefinition query) {
    	checkQuery(query);
        return new PropertiesConstraintQuery(constraintName, query);
    }

    /**
     * Matches documents belonging to at least one
     * of the criteria collections with the specified constraint.
     * @param constraintName	the constraint definition
     * @param uris	the identifiers for the criteria collections
     * @return	the StructuredQueryDefinition for the collection constraint query
     */
    public CollectionConstraintQuery collectionConstraint(String constraintName, String... uris) {
        return new CollectionConstraintQuery(constraintName, uris);
    }

    /**
     * Matches the container specified by the constraint when it
     * has a value with the same string value as at least one
     * of the criteria values.
     * @param constraintName	the constraint definition
     * @param values	the possible values to match
     * @return	the StructuredQueryDefinition for the value constraint query
     */
    public ValueConstraintQuery valueConstraint(String constraintName, String... values) {
        return new ValueConstraintQuery(constraintName, values);
    }
    /**
     * Matches the container specified by the constraint when it
     * has a value with the same string value as at least one
     * of the criteria values.
     * @param constraintName	the constraint definition
     * @param weight	the multiplier for the match in the document ranking
     * @param values	the possible values to match
     * @return	the StructuredQueryDefinition for the value constraint query
     */
    public ValueConstraintQuery valueConstraint(String constraintName, double weight, String... values) {
        return new ValueConstraintQuery(constraintName, weight, values);
    }

    /**
     * Matches the container specified by the constraint when it
     * has at least one of the criteria words.
     * @param constraintName	the constraint definition
     * @param words	the possible words to match
     * @return	the StructuredQueryDefinition for the word constraint query
     */
    public WordConstraintQuery wordConstraint(String constraintName, String... words) {
        return new WordConstraintQuery(constraintName, words);
    }
    /**
     * Matches the container specified by the constraint when it
     * has at least one of the criteria words.
     * @param constraintName	the constraint definition
     * @param weight	the multiplier for the match in the document ranking
     * @param words	the possible words to match
     * @return	the StructuredQueryDefinition for the word constraint query
     */
    public WordConstraintQuery wordConstraint(String constraintName, double weight, String... words) {
        return new WordConstraintQuery(constraintName, weight, words);
    }

    /**
     * Matches the container specified by the constraint
     * whose value that has the correct datatyped comparison with 
     * one of the criteria values.
     * @param constraintName	the constraint definition
     * @param operator	the comparison with the criteria values
     * @param values	the possible datatyped values for the comparison
     * @return	the StructuredQueryDefinition for the range constraint query
     */
    public RangeConstraintQuery rangeConstraint(String constraintName, Operator operator, String... values) {
        return new RangeConstraintQuery(constraintName, operator, values);
    }

    /**
     * Matches the container specified by the constraint
     * whose geospatial point appears within one of the criteria regions.
     * @param constraintName	the constraint definition
     * @param regions	the possible regions containing the point
     * @return	the StructuredQueryDefinition for the geospatial constraint query
     */
    public GeospatialConstraintQuery geospatialConstraint(String constraintName, Region... regions) {
    	checkRegions(regions);
        return new GeospatialConstraintQuery(constraintName, regions);
    }

    /**
     * Matches documents as specified by a constraint that implements
     * a custom query parameterized with the supplied text.
     * @param constraintName	the constraint definition
     * @param text	the input to the custom query
     * @return	the StructuredQueryDefinition for the custom constraint query
     */
    public CustomConstraintQuery customConstraint(String constraintName, String... text) {
        return new CustomConstraintQuery(constraintName, text);
    }

    /* ************************************************************************************* */

    // TODO IN A FUTURE RELEASE:  remove the deprecated innerSerialize() method
	private abstract class AbstractStructuredQuery
    extends AbstractQueryDefinition
    implements StructuredQueryDefinition {
		public AbstractStructuredQuery() {
			optionsUri = builderOptionsURI;
		}

		public String serialize() {
			return serializeQueries(this);
        }

        /**
         * Returns the query as a partial string.  This method will be removed in a future
         * release.
         * @return	the query content
         */
        @Deprecated
        public String innerSerialize() {
        	return extractQueryContent(serializeQueries(this));
        }

        abstract void innerSerialize(XMLStreamWriter serializer) throws Exception;
    }

    // TODO IN A FUTURE RELEASE:  change the visibility of the deprecated
    // classes from public to package and change the return type of the
	// builder methods to StructuredQueryDefinition

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of AndQuery.
     */
    @Deprecated
    public class AndQuery
    extends AbstractStructuredQuery {
    	private StructuredQueryDefinition[] queries;

        /**
         * @deprecated Use the and() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
        public AndQuery(StructuredQueryDefinition... queries) {
            super();
            this.queries = queries;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	writeQueryList(serializer, "and-query", convertQueries(queries));
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface as the type for instances of OrQuery.
     */
    @Deprecated
    public class OrQuery
    extends AbstractStructuredQuery {
    	private StructuredQueryDefinition[] queries;

        /**
         * @deprecated Use the or() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
    	public OrQuery(StructuredQueryDefinition... queries) {
            super();
            this.queries = queries;
        }

        @Override
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	writeQueryList(serializer, "or-query", convertQueries(queries));
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface as the type for instances of NotQuery.
     */
    @Deprecated
    public class NotQuery
    extends AbstractStructuredQuery {
    	private StructuredQueryDefinition query;

        /**
         * @deprecated Use the not() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
    	@Deprecated
    	public NotQuery(StructuredQueryDefinition query) {
            super();
            this.query = query;
        }

    	@Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	writeQuery(serializer, "not-query", (AbstractStructuredQuery) query);
        }
    }

    private class NotInQuery
    extends AbstractStructuredQuery {
    	private StructuredQueryDefinition positive;
    	private StructuredQueryDefinition negative;

        /**
         * @deprecated Use the notIn() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
    	public NotInQuery(StructuredQueryDefinition positive, StructuredQueryDefinition negative) {
            super();
            this.positive = positive;
            this.negative = negative;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("not-in-query");
        	writeQuery(serializer, "positive-query", (AbstractStructuredQuery) positive);
        	writeQuery(serializer, "negative-query", (AbstractStructuredQuery) negative);
        	serializer.writeEndElement();
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of AndNotQuery.
     */
    @Deprecated
    public class AndNotQuery
    extends AbstractStructuredQuery {
    	private StructuredQueryDefinition positive;
    	private StructuredQueryDefinition negative;

        /**
         * @deprecated Use the andNot() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
    	public AndNotQuery(StructuredQueryDefinition positive, StructuredQueryDefinition negative) {
            super();
            this.positive = positive;
            this.negative = negative;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("and-not-query");
        	writeQuery(serializer, "positive-query", (AbstractStructuredQuery) positive);
        	writeQuery(serializer, "negative-query", (AbstractStructuredQuery) negative);
        	serializer.writeEndElement();
        }
    }

    private class BoostQuery
    extends AbstractStructuredQuery {
    	private StructuredQueryDefinition matchingQuery;
    	private StructuredQueryDefinition boostingQuery;

        /**
         * @deprecated Use the boost() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
    	public BoostQuery(StructuredQueryDefinition matchingQuery, StructuredQueryDefinition boostingQuery) {
            super();
            this.matchingQuery = matchingQuery;
            this.boostingQuery = boostingQuery;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("boost-query");
        	writeQuery(serializer, "matching-query", (AbstractStructuredQuery) matchingQuery);
        	writeQuery(serializer, "boosting-query", (AbstractStructuredQuery) boostingQuery);
        	serializer.writeEndElement();
        }
    }
    
    
    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of DocumentQuery.
     */
    @Deprecated
    public class DocumentQuery
    extends AbstractStructuredQuery {
        private String[] uris = null;

        /**
         * @deprecated Use the document() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
        public DocumentQuery(String... uris) {
            super();
            this.uris = uris;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("document-query");
        	writeTextList(serializer, "uri", uris);
        	serializer.writeEndElement();
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of TermQuery.
     */
    @Deprecated
    public class TermQuery
    extends AbstractStructuredQuery {
        private String[] terms = null;
        private Double weight = 0.0;

        /**
         * @deprecated Use the term() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
        public TermQuery(Double weight, String... terms) {
            super();
            this.weight = weight;
            this.terms = terms;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("term-query");
        	writeTextList(serializer, "text", terms);
        	writeText(serializer, "weight", weight);
        	serializer.writeEndElement();
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of NearQuery.
     */
    @Deprecated
    public class NearQuery
    extends AbstractStructuredQuery {
        private Integer distance;
        private Double weight;
        private Ordering order;
        private StructuredQueryDefinition[] queries;

        /**
         * @deprecated Use the near() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
    	public NearQuery(StructuredQueryDefinition... queries) {
            super();
            this.queries = queries;
        }

        public NearQuery(Integer distance, Double weight, Ordering order, StructuredQueryDefinition... queries) {
            this.distance = distance;
            this.weight = weight;
            this.order = order;
            this.queries = queries;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("near-query");
        	writeQueryList(serializer, queries);
            if (order != null) {
    			serializer.writeStartElement("ordered");
    			serializer.writeCharacters(
    					Boolean.toString(order == Ordering.ORDERED));
    			serializer.writeEndElement();
            }
        	writeText(serializer, "distance", distance);
        	writeText(serializer, "distance-weight", weight);
        	serializer.writeEndElement();
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of CollectionQuery.
     */
    @Deprecated
    public class CollectionQuery
    extends AbstractStructuredQuery {
        private String uris[] = null;

        /**
         * @deprecated Use the collection() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
    	public CollectionQuery(String... uris) {
            super();
            this.uris = uris;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("collection-query");
        	writeTextList(serializer, "uri", uris);
        	serializer.writeEndElement();
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of DirectoryQuery.
     */
    @Deprecated
    public class DirectoryQuery
    extends AbstractStructuredQuery {
        private String uris[];
        private Boolean isInfinite;
        private Integer depth;

        /**
         * @deprecated Use the directory() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
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
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("directory-query");
        	if (depth != null) {
        		serializer.writeAttribute("depth", Integer.toString(depth));
        	}
        	writeTextList(serializer, "uri", uris);
        	writeText(serializer, "infinite", isInfinite);
        	serializer.writeEndElement();
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of DocumentFragmentQuery.
     */
    @Deprecated
    public class DocumentFragmentQuery
    extends AbstractStructuredQuery {
    	private StructuredQueryDefinition query;

        /**
         * @deprecated Use the documentFragment() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
    	public DocumentFragmentQuery(StructuredQueryDefinition query) {
            super();
            this.query = query;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
			writeQuery(serializer, "document-fragment-query", (AbstractStructuredQuery) query);
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of PropertiesQuery.
     */
    @Deprecated
    public class PropertiesQuery
    extends AbstractStructuredQuery {
    	private StructuredQueryDefinition query;

        /**
         * @deprecated Use the properties() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
    	public PropertiesQuery(StructuredQueryDefinition query) {
            super();
            this.query = query;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
			writeQuery(serializer, "properties-query", (AbstractStructuredQuery) query);
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of LocksQuery.
     */
    @Deprecated
    public class LocksQuery
    extends AbstractStructuredQuery {
    	private StructuredQueryDefinition query;

        /**
         * @deprecated Use the locks() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
    	public LocksQuery(StructuredQueryDefinition query) {
            super();
            this.query = query;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
			writeQuery(serializer, "locks-query", (AbstractStructuredQuery) query);
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
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("container-constraint-query");
        	writeText(serializer, "constraint-name", name);
        	writeQuery(serializer, query);
        	serializer.writeEndElement();
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of ElementConstraintQuery.
     */
    @Deprecated
    public class ElementConstraintQuery
    extends AbstractStructuredQuery {
        private String name;
        private StructuredQueryDefinition query;

        /**
         * @deprecated Use the elementConstraint() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
    	public ElementConstraintQuery(String constraintName, StructuredQueryDefinition query) {
            super();
            name = constraintName;
            this.query = query;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("element-constraint-query");
        	writeText(serializer, "constraint-name", name);
        	writeQuery(serializer, query);
        	serializer.writeEndElement();
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of PropertiesConstraintQuery.
     */
    @Deprecated
    public class PropertiesConstraintQuery
    extends AbstractStructuredQuery {
        private String name;
        private StructuredQueryDefinition query;

        /**
         * @deprecated Use the propertiesConstraint() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
    	public PropertiesConstraintQuery(String constraintName, StructuredQueryDefinition query) {
            super();
            name = constraintName;
            this.query = query;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("properties-constraint-query");
        	writeText(serializer, "constraint-name", name);
        	writeQuery(serializer, query);
        	serializer.writeEndElement();
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of CollectionConstraintQuery.
     */
    @Deprecated
    public class CollectionConstraintQuery
    extends AbstractStructuredQuery {
        String name = null;
        String[] uris = null;

        /**
         * @deprecated Use the collectionConstraint() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
    	public CollectionConstraintQuery(String constraintName, String... uris) {
            super();
            name = constraintName;
            this.uris = uris;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("collection-constraint-query");
        	writeText(serializer, "constraint-name", name);
        	writeTextList(serializer, "uri", uris);
        	serializer.writeEndElement();
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of ValueConstraintQuery.
     */
    @Deprecated
    public class ValueConstraintQuery
    extends AbstractStructuredQuery {
        String name;
        String[] values;
        Double weight;

        /**
         * @deprecated Use the valueConstraint() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
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
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("value-constraint-query");
        	writeText(serializer, "constraint-name", name);
        	writeTextList(serializer, "text", values);
        	writeText(serializer, "weight", weight);
        	serializer.writeEndElement();
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of WordConstraintQuery.
     */
    @Deprecated
    public class WordConstraintQuery
    extends AbstractStructuredQuery {
        String name;
        String[] words;
        Double weight;

        /**
         * @deprecated Use the wordConstraint() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
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
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("word-constraint-query");
        	writeText(serializer, "constraint-name", name);
        	writeTextList(serializer, "text", words);
        	writeText(serializer, "weight", weight);
        	serializer.writeEndElement();
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of RangeConstraintQuery.
     */
    @Deprecated
    public class RangeConstraintQuery
    extends AbstractStructuredQuery {
        String name = null;
        String[] values = null;
        Operator operator = null;

        /**
         * @deprecated Use the rangeConstraint() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
    	public RangeConstraintQuery(String constraintName, Operator operator, String... values) {
            super();
            name = constraintName;
            this.values = values;
            this.operator = operator;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("range-constraint-query");
        	writeText(serializer, "constraint-name", name);
        	writeTextList(serializer, "value", values);
        	writeText(serializer, "range-operator", operator);
        	serializer.writeEndElement();
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of GeospatialConstraintQuery.
     */
    @Deprecated
    public class GeospatialConstraintQuery
    extends AbstractStructuredQuery {
        String name = null;
        Region[] regions = null;

        /**
         * @deprecated Use the geospatialConstraint() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
    	public GeospatialConstraintQuery(String constraintName, Region... regions) {
            super();
            name = constraintName;
            this.regions = regions;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("geospatial-constraint-query");
        	writeText(serializer, "constraint-name", name);
            for (Region region : regions) {
            	((RegionImpl) region).innerSerialize(serializer);
            }
        	serializer.writeEndElement();
        }
    }

    /**
     * @deprecated Use the {@link StructuredQueryDefinition StructuredQueryDefinition} interface
     * as the type for instances of CustomConstraintQuery.
     */
    @Deprecated
    public class CustomConstraintQuery
    extends AbstractStructuredQuery {
        private String terms[] = null;
        private String name = null;

        /**
         * @deprecated Use the customConstraint() builder method of StructuredQueryBuilder
         * and type the object as an instance of the StructuredQueryDefinition interface.
         */
        @Deprecated
    	public CustomConstraintQuery(String constraintName, String... terms) {
            super();
            name = constraintName;
            this.terms = terms;
        }

        @Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("custom-constraint-query");
        	writeText(serializer, "constraint-name", name);
        	writeTextList(serializer, "text", terms);
        	serializer.writeEndElement();
        }
    }

    class ContainerQuery
    extends AbstractStructuredQuery {
    	private ContainerIndex index;
    	private StructuredQueryDefinition query;
    	ContainerQuery(ContainerIndex index, StructuredQueryDefinition query) {
    		this.index = index;
    		this.query = query;
    	}
    	@Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
    		serializer.writeStartElement("container-query");
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
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
    		((IndexImpl) index).innerSerialize(serializer);
    		if (scope != null) {
    			if (scope == FragmentScope.DOCUMENT) {
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

    class ValueQuery
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
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
            serializer.writeStartElement("value-query");
            ((IndexImpl) index).innerSerialize(serializer);
            if (scope != null) {
                if (scope == FragmentScope.DOCUMENT) {
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
                        serializer.writeEmptyElement("null");
                    } else if ( value instanceof String ) {
                        writeText(serializer, "text", value);
                    } else if ( value instanceof Number ) {
                        writeText(serializer, "number", value);
                    } else if ( value instanceof Boolean ) {
                        writeText(serializer, "boolean", value);
                    }
                }
            }
            writeTextList(serializer, "term-option", options);
            writeText(serializer, "weight", weight);
            serializer.writeEndElement();
        }
    }

    // QUESTION: why collation on word but not values?
    class WordQuery
    extends TextQuery {
    	WordQuery(TextIndex index, FragmentScope scope, String[] options,
    			Double weight, String[] values) {
    		super(index, scope, options, weight, values);
    	}
    	@Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
    		serializer.writeStartElement("word-query");
    		super.innerSerialize(serializer);
    		serializer.writeEndElement();
        }
    }

        
    class RangeQuery 
    extends AbstractStructuredQuery {
    	RangeIndex    index;
		FragmentScope scope;
		String        type;
		String        collation;
		String[]      options;
    	Operator      operator;
    	String[]      values;
    	RangeQuery(RangeIndex index, String type, String collation, 
    			FragmentScope scope, String[] rangeOptions, Operator operator, 
    			Object[] values) {
    		this.index     = index;
    		this.type      = type;
    		this.collation = collation;
    		this.scope     = scope;
    		this.options = rangeOptions;
    		this.operator  = operator;
    		this.values    = new String[values.length];
    		for (int i=0; i < values.length; i++) {
    			Object value = values[i];
    			this.values[i] = (value instanceof String) ?
    					(String) value : value.toString();
   			}
    	}
    	@Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
    		serializer.writeStartElement("range-query");
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
    		serializer.writeEndElement();
        }
    }

    class GeospatialQuery
    extends AbstractStructuredQuery {
    	GeospatialIndex index;
		FragmentScope   scope;
    	Region[]        regions;
        String[]        options;
        GeospatialQuery(GeospatialIndex index, FragmentScope scope, Region[] regions,
        		String[] options) {
        	this.index   = index;
    		this.scope   = scope;
        	this.regions = regions;
    		this.options = options;
        }
        @Override
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	String elemName = null;
        	if (index instanceof GeoElementImpl)
        		elemName = "geo-elem-query";
        	else if (index instanceof GeoElementPairImpl)
        		elemName = "geo-elem-pair-query";
        	else if (index instanceof GeoAttributePairImpl)
        		elemName = "geo-attr-pair-query";
        	else if (index instanceof GeoPathImpl)
        		elemName = "geo-path-query";
        	else
        		throw new IllegalStateException(
        				"unknown index class: "+index.getClass().getName());

    		serializer.writeStartElement(elemName);
    		((IndexImpl) index).innerSerialize(serializer);
    		if (scope != null) {
        		writeText(serializer, "fragment-scope",
        				scope.toString().toLowerCase());
    		}
     		writeTextList(serializer, "geo-option", options);
            for (Region region : regions) {
            	((RegionImpl) region).innerSerialize(serializer);
            }
           serializer.writeEndElement();
        }
    }

    /* ************************************************************************************* */

    abstract class IndexImpl {
        abstract void innerSerialize(XMLStreamWriter serializer) throws Exception;
    }
    class ElementImpl extends IndexImpl implements Element {
    	String name;
    	QName qname;
    	ElementImpl(QName qname) {
    		this.qname = qname;
    	}
    	ElementImpl(String name) {
    		this.name = name;
    	}
        @Override
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializeNamedIndex(serializer, "element", qname, name);
        }
    }
    class AttributeImpl extends IndexImpl implements Attribute {
    	String name;
    	QName qname;
    	AttributeImpl(QName qname) {
    		this.qname = qname;
    	}
    	AttributeImpl(String name) {
    		this.name = name;
    	}
        @Override
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializeNamedIndex(serializer, "attribute", qname, name);
        }
    }
    class ElementAttributeImpl extends IndexImpl implements ElementAttribute {
    	Element   element;
    	Attribute attribute;
    	ElementAttributeImpl(Element element, Attribute attribute) {
    		this.element   = element;
    		this.attribute = attribute;
    	}
        @Override
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	((IndexImpl) element).innerSerialize(serializer);
        	((IndexImpl) attribute).innerSerialize(serializer);
        }
    }
    class FieldImpl extends IndexImpl implements Field {
    	String name;
    	FieldImpl(String name) {
    		this.name = name;
    	}
        @Override
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("field");
        	serializer.writeAttribute("name", name);
        	serializer.writeEndElement();
        }
    }
    class JSONPropertyImpl extends IndexImpl implements JSONProperty {
    	String name;
    	JSONPropertyImpl(String name) {
    		this.name = name;
    	}
        @Override
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
    		writeText(serializer, "json-property", name);
        }
    }
    class PathIndexImpl extends IndexImpl implements PathIndex {
    	String path;
    	PathIndexImpl(String path) {
    		this.path = path;
    	}
        @Override
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
    		writeText(serializer, "path-index", path);
        }
    }
    class GeoElementImpl extends IndexImpl implements GeospatialIndex {
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
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
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
    class GeoElementPairImpl extends IndexImpl implements GeospatialIndex {
    	Element parent;
    	Element lat;
    	Element lon;
    	GeoElementPairImpl(Element parent, Element lat, Element lon) {
    		this.parent = parent;
    		this.lat    = lat;
    		this.lon    = lon;
    	}
        @Override
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	ElementImpl parentImpl = (ElementImpl) parent;
        	ElementImpl latImpl    = (ElementImpl) lat;
        	ElementImpl lonImpl    = (ElementImpl) lon;
        	serializeNamedIndex(serializer, "parent", parentImpl.qname, parentImpl.name);
        	serializeNamedIndex(serializer, "lat", latImpl.qname, latImpl.name);
        	serializeNamedIndex(serializer, "lon", lonImpl.qname, lonImpl.name);
        }
    } 
    class GeoAttributePairImpl extends IndexImpl implements GeospatialIndex {
    	Element   parent;
    	Attribute lat;
    	Attribute lon;
    	GeoAttributePairImpl(Element parent, Attribute lat, Attribute lon) {
    		this.parent = parent;
    		this.lat    = lat;
    		this.lon    = lon;
    	}
        @Override
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	ElementImpl   parentImpl = (ElementImpl) parent;
        	AttributeImpl latImpl    = (AttributeImpl) lat;
        	AttributeImpl lonImpl    = (AttributeImpl) lon;
        	serializeNamedIndex(serializer, "parent", parentImpl.qname, parentImpl.name);
        	serializeNamedIndex(serializer, "lat", latImpl.qname, latImpl.name);
            serializeNamedIndex(serializer, "lon", lonImpl.qname, lonImpl.name);
        }
    }
    
    private class GeoPathImpl extends IndexImpl implements GeospatialIndex {
    	PathIndex pathIndex;
    	GeoPathImpl(PathIndex pathIndex) {
    		this.pathIndex = pathIndex;
    	}
    	@Override
    	void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	PathIndexImpl pathIndexImpl = (PathIndexImpl) pathIndex;
        	pathIndexImpl.innerSerialize(serializer);       	;
    	}
    }

    /**
     * A region matched by a geospatial query.
     */
    public interface Region {
        // TODO IN A FUTURE RELEASE:  remove the deprecated serialize() method

    	/**
         * @deprecated Returns the region as a partial string.  This method will be removed in a future
         * release.
         * @return	the query content identifying a region
         */
    	@Deprecated
    	public abstract String serialize();
    }

    // TODO IN A FUTURE RELEASE:  separate a public Point interface
    // from a package PointImpl class

    abstract class RegionImpl  {
        /**
         * @deprecated Returns the region as a partial string.  This method will be removed in a future
         * release.
         * @return	the query content identifying a region
         */
    	@Deprecated
        public String serialize() {
        	return extractQueryContent(serializeRegions(this));
        }
        abstract void innerSerialize(XMLStreamWriter serializer) throws Exception;
    }

    /**
     * @deprecated Treat the Point class as an interface that extends Region.
     */
    @Deprecated
    public class Point extends RegionImpl implements Region {
        private double lat = 0.0;
        private double lon = 0.0;

        /**
         * @deprecated Use the point() builder method of StructuredQueryBuilder.
         */
        @Deprecated
        public Point(double latitude, double longitude) {
            lat = latitude;
            lon = longitude;
        }

        @Deprecated
        public double getLatitude() {
            return lat;
        }
        @Deprecated
        public double getLongitude() {
            return lon;
        }

        @Override
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("point");
        	writeText(serializer, "latitude", String.valueOf(lat));
        	writeText(serializer, "longitude", String.valueOf(lon));
        	serializer.writeEndElement();
        }
    }

    // TODO IN A FUTURE RELEASE:  change the visibility of the deprecated
    // classes from public to package and change the return type of the builder
    // methods to Region

    /**
     * @deprecated Use the Region interface as the type for instances of Circle.
     */
    @Deprecated
    public class Circle extends RegionImpl implements Region {
        private Point center = null;
        private double radius = 0.0;

        /**
         * @deprecated Use the circle() builder method of StructuredQueryBuilder
         * and type the object as an instance of the Region interface.
         */
        @Deprecated
        public Circle(double latitude, double longitude, double radius) {
            center = new Point(latitude, longitude);
            this.radius = radius;
        }

        @Override
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("circle");
        	writeText(serializer, "radius", String.valueOf(radius));
        	center.innerSerialize(serializer);
        	serializer.writeEndElement();
        }
    }

    /**
     * @deprecated Use the Region interface as the type for instances of Box.
     */
    @Deprecated
    public class Box extends RegionImpl implements Region {
        private double south, west, north, east;

        /**
         * @deprecated Use the box() builder method of StructuredQueryBuilder
         * and type the object as an instance of the Region interface.
         */
        @Deprecated
        public Box(double south, double west, double north, double east) {
            this.south = south;
            this.west = west;
            this.north = north;
            this.east = east;
        }

        @Override
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("box");
        	writeText(serializer, "south", String.valueOf(south));
        	writeText(serializer, "west",  String.valueOf(west));
        	writeText(serializer, "north", String.valueOf(north));
        	writeText(serializer, "east",  String.valueOf(east));
        	serializer.writeEndElement();
        }
    }

    /**
     * @deprecated Use the Region interface as the type for instances of Polygon.
     */
    @Deprecated
    public class Polygon extends RegionImpl implements Region {
        private Point[] points;

        /**
         * @deprecated Use the polygon() builder method of StructuredQueryBuilder
         * and type the object as an instance of the Region interface.
         */
        @Deprecated
        public Polygon(Point... points) {
            this.points = points;
        }

        @Override
        void innerSerialize(XMLStreamWriter serializer) throws Exception {
        	serializer.writeStartElement("polygon");
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
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);

		try {
			XMLStreamWriter serializer = factory.createXMLStreamWriter(out, "UTF-8");

			serializer.setDefaultNamespace("http://marklogic.com/appservices/search");
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
//			serializer.writeStartDocument();
			serializer.writeStartElement("query");
			
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
//			serializer.writeEndDocument();
			serializer.flush();
			serializer.close();
		} catch (Exception e) {
			throw new MarkLogicIOException(e);
		}
    }

    // TODO IN A FUTURE RELEASE:  remove the transformation once the deprecated serialization
    // methods are removed
    static private Transformer makeExtractorTransformer() {
		try {
	    	if (extractor == null) {
	        	extractor = TransformerFactory.newInstance().newTemplates(new StreamSource(new StringReader(
	        			"<xsl:stylesheet version='1.0'"+
	        			    " xmlns:xsl='http://www.w3.org/1999/XSL/Transform'"+
	        			    " xmlns:search='http://marklogic.com/appservices/search'"+
	        			    " xmlns:xs='http://www.w3.org/2001/XMLSchema'"+
	        			    " exclude-result-prefixes='search xs'>"+
	        			"<xsl:output method='xml' omit-xml-declaration='yes'/>"+
	        			"<xsl:template match='/'>"+
	        				"<xsl:apply-templates select='*/node()'/>"+
	        			"</xsl:template>"+
	        			"<xsl:template match='search:*'>"+
        					"<xsl:element name='{local-name(.)}'>"+
        						"<xsl:copy-of select='@*'/>"+
    	        				"<xsl:apply-templates select='node()'/>"+
        					"</xsl:element>"+
        				"</xsl:template>"+
	        			"<xsl:template match='*'>"+
    						"<xsl:copy>"+
    							"<xsl:copy-of select='@*'/>"+
	        					"<xsl:apply-templates select='node()'/>"+
	        				"</xsl:copy>"+
    					"</xsl:template>"+
	        			"<xsl:template match='node()'>"+
	        				"<xsl:copy-of select='.'/>"+
	        			"</xsl:template>"+
	        			"</xsl:stylesheet>"
	        			)));
	    	}
			return extractor.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new MarkLogicIOException(e);
		}
    }
    static private String extractQueryContent(String query) {
    	if (query == null) {
    		return null;
    	}
    	try {
        	StringWriter writer = new StringWriter();
			makeExtractorTransformer().transform(
					new StreamSource(new StringReader(query)),
					new StreamResult(writer)
				);
	    	return writer.toString();
		} catch (TransformerException e) {
			throw new MarkLogicIOException(e);
		}
    }

    static private void serializeNamedIndex(XMLStreamWriter serializer,
    		String elemName, QName qname, String name)
    throws Exception {
    	serializer.writeStartElement(elemName);
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
    throws Exception
    {
    	if (object == null) {
    		return;
    	}
   		serializer.writeStartElement(container);
   		serializer.writeCharacters(
   				(object instanceof String) ?
   				(String) object : object.toString()
   				);
   		serializer.writeEndElement();
    }
    static private void writeTextList(XMLStreamWriter serializer,
    		String container, Object[] objects)
    throws Exception
    {
    	if (objects == null) {
    		return;
    	}
		for (Object object: objects) {
			if ( object == null ) continue;
    		serializer.writeStartElement(container);
    		serializer.writeCharacters(
    				(object instanceof String) ?
    				(String) object : object.toString()
    				);
    		serializer.writeEndElement();
		}
	}
    static private void writeQuery(XMLStreamWriter serializer, StructuredQueryDefinition query)
    throws Exception
    {
    	((AbstractStructuredQuery) query).innerSerialize(serializer);
    }
    static private void writeQueryList(XMLStreamWriter serializer,
    		StructuredQueryDefinition... queries)
    throws Exception
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
    throws Exception
    {
    	serializer.writeStartElement(container);
    	if (query != null) {
    		query.innerSerialize(serializer);
    	}
        serializer.writeEndElement();
    }
    static private void writeQueryList(XMLStreamWriter serializer,
    		String container, AbstractStructuredQuery... queries)
    throws Exception
    {
    	serializer.writeStartElement(container);
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
     * @param options	the list of range query options
     * @return	the range query options as an array
     */
	public String[] rangeOptions(String... options) {
		return options;
	}

	/**
	 * Defines a boost query for the matching and boosting query definitions.  The matching
	 * or boosting query definitions can each be an AND or OR query definition for complex
	 * combinations of criteria.
	 * @param matchingQuery the query definition that filters documents
	 * @param boostingQuery	the query definition that increases the rank for some filtered documents
	 * @return	the StructuredQueryDefinition for the boost query
	 */
	public StructuredQueryDefinition boost(StructuredQueryDefinition matchingQuery, StructuredQueryDefinition boostingQuery) {
		return new BoostQuery(matchingQuery, boostingQuery);
	}
	/**
	 * Defines a not-in query for the positive and negative query definitions.  These query definitions
	 * can each be an AND or OR query definition for complex combinations of criteria.
	 * @param positive the query definition that includes documents
	 * @param negative the query definition that excludes documents
	 * @return	the StructuredQueryDefinition for the not-in query
	 */
	public StructuredQueryDefinition notIn(StructuredQueryDefinition positive, StructuredQueryDefinition negative) {
		return new NotInQuery(positive, negative);
	}

	/**
	 * Gets the namespace bindings used for the query.
	 * @return	the namespace bindings
	 */
	public IterableNamespaceContext getNamespaces() {
		return namespaces;
	}

	/**
	 * Specifies the namespace bindings used for the query.  You can use
	 * the {@link com.marklogic.client.util.EditableNamespaceContext EditableNamespaceContext}
	 * class to instantiate a set of bindings between namespace prefixes and Uris.
	 * @param namespaces	the namespace bindings
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
	
	
}
