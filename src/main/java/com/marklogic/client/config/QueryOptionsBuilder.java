/*
 * Copyright 2012 MarkLogic Corporation
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
package com.marklogic.client.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.config.QueryOptions.Aggregate;
import com.marklogic.client.config.QueryOptions.AnyElement;
import com.marklogic.client.config.QueryOptions.Attribute;
import com.marklogic.client.config.QueryOptions.BaseConstraintItem;
import com.marklogic.client.config.QueryOptions.BaseQueryOptionConfiguration;
import com.marklogic.client.config.QueryOptions.ConstraintValue;
import com.marklogic.client.config.QueryOptions.Element;
import com.marklogic.client.config.QueryOptions.Field;
import com.marklogic.client.config.QueryOptions.FragmentScope;
import com.marklogic.client.config.QueryOptions.Heatmap;
import com.marklogic.client.config.QueryOptions.JsonKey;
import com.marklogic.client.config.QueryOptions.PathIndex;
import com.marklogic.client.config.QueryOptions.QNameExtractor;
import com.marklogic.client.config.QueryOptions.QueryAnnotation;
import com.marklogic.client.config.QueryOptions.QueryCollection;
import com.marklogic.client.config.QueryOptions.QueryConstraint;
import com.marklogic.client.config.QueryOptions.QueryCustom;
import com.marklogic.client.config.QueryOptions.QuerySearchableExpression;
import com.marklogic.client.config.QueryOptions.QueryCustom.FinishFacet;
import com.marklogic.client.config.QueryOptions.QueryCustom.Parse;
import com.marklogic.client.config.QueryOptions.QueryCustom.StartFacet;
import com.marklogic.client.config.QueryOptions.QueryDefaultSuggestionSource;
import com.marklogic.client.config.QueryOptions.QueryElementQuery;
import com.marklogic.client.config.QueryOptions.QueryExtractMetadata;
import com.marklogic.client.config.QueryOptions.QueryGeospatial;
import com.marklogic.client.config.QueryOptions.QueryGeospatialAttributePair;
import com.marklogic.client.config.QueryOptions.QueryGeospatialElement;
import com.marklogic.client.config.QueryOptions.QueryGeospatialElementPair;
import com.marklogic.client.config.QueryOptions.QueryGrammar;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryJoiner;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryJoiner.Comparator;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryJoiner.JoinerApply;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryStarter;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryStarter.StarterApply;
import com.marklogic.client.config.QueryOptions.QueryGrammar.Tokenize;
import com.marklogic.client.config.QueryOptions.QueryOperator;
import com.marklogic.client.config.QueryOptions.QueryProperties;
import com.marklogic.client.config.QueryOptions.QueryRange;
import com.marklogic.client.config.QueryOptions.QueryRange.Bucket;
import com.marklogic.client.config.QueryOptions.QueryRange.ComputedBucket;
import com.marklogic.client.config.QueryOptions.QueryRange.ComputedBucket.AnchorValue;
import com.marklogic.client.config.QueryOptions.QuerySortOrder;
import com.marklogic.client.config.QueryOptions.QueryState;
import com.marklogic.client.config.QueryOptions.QuerySuggestionSource;
import com.marklogic.client.config.QueryOptions.QueryTerm;
import com.marklogic.client.config.QueryOptions.QueryTerm.TermApply;
import com.marklogic.client.config.QueryOptions.QueryTransformResults;
import com.marklogic.client.config.QueryOptions.QueryUri;
import com.marklogic.client.config.QueryOptions.QueryValue;
import com.marklogic.client.config.QueryOptions.QueryValues;
import com.marklogic.client.config.QueryOptions.QueryWord;
import com.marklogic.client.config.QueryOptions.WordLexicon;
import com.marklogic.client.config.QueryOptions.XQueryExtension;

/**
 * Builder of QueryOptions objects, which are used to configure MarkLogic
 * runtime search, lexicon, structured queries and key/value queries.
 * 
 */
public final class QueryOptionsBuilder {

	/**
	 * An option passed to a query configuration affect faceting behavior.
	 */
	public class FacetOption extends TextOption<String> implements
			QueryRangeItem, QueryCustomItem, QueryGeospatialItem {

		@Override
		public void build(QueryCustom custom) {
			custom.addFacetOption(this.getValue());
		}

		@Override
		public void build(QueryGeospatial geospatial) {
			geospatial.addFacetOption(this.getValue());
		}

		@Override
		public void build(QueryRange range) {
			range.addFacetOption(this.getValue());
		}

	}

	/**
	 * An option passed to a query configuration to affect geospatial searches.
	 */
	public class GeospatialOption extends TextOption<String> implements
			QueryGeospatialItem {

		@Override
		public void build(QueryGeospatial geospatial) {
			geospatial.addGeoOption(this.getValue());
		}
	}

	/**
	 * Marks classes that can be annotated with XML elements.
	 */
	public interface QueryAnnotations {

		public void addElementAsAnnotation(org.w3c.dom.Element element);

		public void deleteAnnotations();

		public List<QueryAnnotation> getAnnotations();

		void addAnnotation(QueryAnnotation queryAnnotation);

	}

	/**
	 * Marks objects that comprise QueryConstraints
	 */
	public interface QueryConstraintItem extends QueryTermItem {

	}

	/**
	 * Marks classes that comprise QueryCustom sources.
	 */
	public interface QueryCustomItem {
		public void build(QueryCustom custom);
	}

	/**
	 * Marks objects that comprise QueryExtractMetadata configurations.
	 */
	public interface QueryExtractMetadataItem {
		public void build(QueryExtractMetadata extractMetadata);
	}

	/**
	 * Marks objects that comprise QueryGeospatial configurations.
	 */
	public interface QueryGeospatialItem {
		public void build(QueryGeospatial geospatial);
	}

	/**
	 * Marks objects that comprise QueryGrammar configurations.
	 */
	public interface QueryGrammarItem {

	}

	/**
	 * Marks classes that comprise the top level QueryOptions configuration.
	 */
	public interface QueryOptionsItem {

		public void build(QueryOptions options);
	}

	/**
	 * Marks classes that comprise QueryRange configurations.
	 */
	public interface QueryRangeItem {

		public void build(QueryRange range);

	}

	/**
	 * Marks classes that comprise QuerySortOrder configurations.
	 */
	public interface QuerySortOrderItem {

		public void build(QuerySortOrder sortOrder);
	}

	/**
	 * Marks objects that comprise QueryState configurations.
	 */
	public interface QueryStateItem {

	}

	/**
	 * Tags classes that comprise QuerySuggestionSource configurations.
	 */
	public interface QuerySuggestionSourceItem {

		void build(QuerySuggestionSource suggestionSource);

	}

	/**
	 * Marks classes that comprise QueryTerm configurations
	 */
	public interface QueryTermItem {

	}

	/**
	 * Configuration Items that build to make {@link QueryValue} objects.
	 */
	public interface QueryValueItem {

		public void build(QueryValue value);

	}

	/**
	 * Marks components that configure QueryValues objects.
	 */
	public interface QueryValuesItem {
		public void build(QueryValues values);
	}

	/**
	 * Marks classes that comprise QueryWord configurations.
	 * 
	 */
	public interface QueryWordItem {

		public void build(QueryWord word);
	}

	/**
	 * An option passed to query configurations to affect suggestion sources.
	 */
	public class SuggestionSourceOption extends TextOption<String> implements
			QuerySuggestionSourceItem {

		@Override
		public void build(QuerySuggestionSource suggestionSource) {
			suggestionSource.getSuggestionOptions().add(this.getValue());
		}

	}

	/**
	 * An option passed to query configurations to affect term queries.
	 */
	public class TermOption extends TextOption<String> implements
			QueryTermItem, QueryWordItem, QueryValueItem, QueryCustomItem {

		@Override
		public void build(QueryCustom custom) {
			custom.addTermOption(this.getValue());
		}

		@Override
		public void build(QueryValue value) {
			value.addTermOption(this.getValue());
		}

		@Override
		public void build(QueryWord word) {
			word.addTermOption(this.getValue());
		}
	}

	/**
	 * An option passed to a query configuration to affect values
	 * configurations.
	 */
	public class ValuesOption extends TextOption<String> implements
			QueryValuesItem {

		@Override
		public void build(QueryValues values) {
			values.addValuesOption(this.getValue());
		}

	}

	/**
	 * Wraps a value given to weight search configurations relative to each
	 * other.
	 */
	public class Weight extends TextOption<Double> implements QueryTermItem,
			QueryWordItem, QueryValueItem {

		@Override
		public void build(QueryValue value) {
			value.setWeight(this.getValue());
		}

		@Override
		public void build(QueryWord word) {
			word.setWeight(this.getValue());
		}

	}

	private class QueryOptionsTextItem<T extends Object> implements
			QueryOptionsItem, QueryWordItem, QueryValueItem {

		private String methodName;
		private T state;

		public QueryOptionsTextItem(String methodName, T value) {
			this.state = value;
			this.methodName = methodName;
		}

		@Override
		public void build(QueryOptions options) {
			this.innerBuild(options);
		}

		@Override
		public void build(QueryValue value) {
			this.innerBuild(value);
		}

		@Override
		public void build(QueryWord word) {
			this.innerBuild(word);
		}

		private void innerBuild(BaseQueryOptionConfiguration anyOption) {
			try {
				Method method = anyOption.getClass().getMethod(this.methodName,
						state.getClass());
				method.invoke(anyOption, this.state);
			} catch (SecurityException e) {
				throw new MarkLogicInternalException(
						"Introspection problem building QueryOptions", e);
			} catch (NoSuchMethodException e) {
				throw new MarkLogicInternalException(
						"Introspection problem building QueryOptions", e);
			} catch (IllegalArgumentException e) {
				throw new MarkLogicInternalException(
						"Introspection problem building QueryOptions", e);
			} catch (IllegalAccessException e) {
				throw new MarkLogicInternalException(
						"Introspection problem building QueryOptions", e);
			} catch (InvocationTargetException e) {
				throw new MarkLogicInternalException(
						"Introspection problem building QueryOptions", e);
			}
		}

		private void innerBuild(QueryOptions anyOption) {
			try {
				Method method = anyOption.getClass().getMethod(this.methodName,
						state.getClass());
				method.invoke(anyOption, this.state);
			} catch (SecurityException e) {
				throw new MarkLogicInternalException(
						"Introspection problem building QueryOptions", e);
			} catch (NoSuchMethodException e) {
				throw new MarkLogicInternalException(
						"Introspection problem building QueryOptions", e);
			} catch (IllegalArgumentException e) {
				throw new MarkLogicInternalException(
						"Introspection problem building QueryOptions", e);
			} catch (IllegalAccessException e) {
				throw new MarkLogicInternalException(
						"Introspection problem building QueryOptions", e);
			} catch (InvocationTargetException e) {
				throw new MarkLogicInternalException(
						"Introspection problem building QueryOptions", e);
			}
		}
	}

	public class QueryCollation extends TextOption<String> implements
			QueryRangeItem {

		public QueryCollation(String collationValue) {
			this.setValue(collationValue);
		}

		@Override
		public void build(QueryRange range) {
			range.setCollation(this.getValue());
		}

	}

	private abstract class TextOption<T extends Object> {

		private T value;

		public TextOption() {
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}
	}

	interface Indexable {

		public QName getAttribute();

		public QName getElement();

		public String getFieldName();

		public void setAttribute(Attribute attribute);

		public void setElement(Element element);

		public void setField(Field field);

		public void setPath(PathIndex path);

		public void setJsonKey(JsonKey jsonKey);
	}

	/**
	 * Tags classes that define index references.
	 */
	interface IndexReference extends QueryWordItem, QueryRangeItem,
			QueryValueItem, QuerySortOrderItem {

	}

	public static class NamespaceBinding {
		String prefix = null;
		String uri = null;

		public NamespaceBinding(String prefix, String uri) {
			this.prefix = prefix;
			this.uri = uri;
		}

		public String getPrefix() {
			return prefix;
		}

		public String getNamespaceUri() {
			return uri;
		}
	}

	private static DocumentBuilderFactory factory;

	private static final Logger logger = LoggerFactory
			.getLogger(QueryOptionsBuilder.class);

	private static DocumentBuilderFactory getFactory()
			throws ParserConfigurationException {
		if (factory == null)
			factory = makeDocumentBuilderFactory();
		return factory;
	}

	private static DocumentBuilderFactory makeDocumentBuilderFactory()
			throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		return factory;
	}

	/**
	 * Construct an additional query to be used in Search expression.
	 * 
	 * @param element
	 *            A DOM element of a MarkLogic >cts:query/>
	 * @return A component for use in a QueryOptionsBuilder expression.
	 */
	public AnyElement additionalQuery(org.w3c.dom.Element element) {
		return new AnyElement(element);
	}

	/**
	 * Construct an additional query to be used in Search expression.
	 * 
	 * @param xmlString
	 *            An XML string representing a MarkLogic <cts:query/>
	 * @return A component for use in a QueryOptionsBuilder expression.
	 */
	public AnyElement additionalQuery(String xmlString) {
		org.w3c.dom.Element element = domElement(xmlString);
		return new AnyElement("additional-query", element);
	}

	public Aggregate aggregate(String aggregate) {
		Aggregate a = new Aggregate();
		a.setApply(aggregate);
		return a;
	}

	/**
	 * Build an annoatation from a valid XML string
	 * 
	 * @param xmlString
	 *            A valid XML string containing an annotation.
	 * @return A QueryAnnotation object for use in constructing query
	 *         configurations.
	 */
	public QueryAnnotation annotation(String xmlString) {
		QueryAnnotation annotation = new QueryAnnotation();
		annotation.add(domElement(xmlString));
		return annotation;
	}

	/**
	 * Build an Attribute object with no namespace declaration.
	 * 
	 * @param name
	 *            local name for attribute.
	 * @return Attribute object for building query configurations.
	 */
	public Attribute attribute(String name) {
		return new Attribute("", name);
	}

	/**
	 * Build an Attribute object with a namepsace declaration.
	 * 
	 * @param ns
	 *            namespace of the attribute.
	 * @param name
	 *            local name of the attribute.
	 * @return Attribute object for building query configurations.
	 */
	public Attribute attribute(String ns, String name) {
		return new Attribute(ns, name);
	}

	/**
	 * Build a new bucket for use in a RangeOption
	 * 
	 * @param name
	 *            Name of bucket, for use in query strings.
	 * @param label
	 *            Label of bucket when displayed in facet results.
	 * @param ge
	 *            Upper bound of bucket
	 * @param lt
	 *            Lower bound of bucket
	 * @return a new Bucket for use in building QueryRange objects.
	 */
	public Bucket bucket(String name, String label, String ge, String lt) {
		Bucket bucket = new Bucket();
		bucket.setName(name);
		bucket.setContent(label);
		bucket.setGe(ge);
		bucket.setLt(lt);
		return bucket;
	}

	/**
	 * Builds a default collation object for use in Range index definitions.
	 * 
	 */
	public QueryRangeItem collation(String collationName) {
		return new QueryCollation(collationName);
	}

	/**
	 * Builds a QueryCollection object to use the Collection URIs as source of
	 * constraint values.
	 * 
	 * @param facets
	 *            Setting to true configures Search API to do facets on this
	 *            source.
	 * @param prefix
	 *            This value will be trimmed from the start of collection URIs
	 *            to provide more readable facet labels.
	 * @param facetOptions
	 *            A list of facet options to configure the collection
	 *            constraint.
	 * @return A QueryCollection object for use in building QueryOptions
	 *         configurations.
	 */
	public QueryCollection collection(boolean facets, String prefix,
			FacetOption... facetOptions) {
		QueryCollection collectionOption = new QueryCollection();
		collectionOption.doFacets(facets);
		collectionOption.setPrefix(prefix);
		for (FacetOption option : facetOptions) {
			collectionOption.addFacetOption(option.getValue());
		}
		return collectionOption;
	}

	/**
	 * Build a new bucket for use in a RangeOption
	 * 
	 * @param name
	 *            Name of bucket, for use in query strings.
	 * @param label
	 *            Label of bucket when displayed in facet results.
	 * @param ge
	 *            Upper bound of bucket, relative to anchor.
	 * @param lt
	 *            Lower bound of bucket, relative to anchor.
	 * @param anchor
	 *            Value to provide anchor for relative terms used in ge and lt.
	 * @return a new ComputedBucket for use in building QueryRange objects.
	 */

	public ComputedBucket computedBucket(String name, String label, String ge,
			String lt, AnchorValue anchor) {
		ComputedBucket bucket = new ComputedBucket();
		bucket.setName(name);
		bucket.setLabel(label);
		bucket.setGe(ge);
		bucket.setLt(lt);
		bucket.setAnchor(anchor);
		return bucket;
	}

	/**
	 * Set the maximum number of threads used to resolve facets. The default is
	 * 8, which specifies that at most 8 threads will be used concurrently to
	 * resolve facets.
	 * 
	 * @param concurrencyLevel
	 *            integer to set concurrency level.
	 * @return A QueryOptionsTextItem for use in building QueryOptions
	 *         configurations.
	 */
	public QueryOptionsTextItem<Integer> concurrencyLevel(
			Integer concurrencyLevel) {
		return new QueryOptionsTextItem<Integer>("setConcurrencyLevel",
				concurrencyLevel);
	}

	/**
	 * Builds a constraint object with the supplied name and source.
	 * 
	 * @param name
	 *            Name of the constraint, to be used in search strings.
	 * @param constraintSource
	 *            Source of data for the constraint.
	 * @return A QueryConstraint object for use in building QueryOptions
	 *         configurations
	 */
	public QueryConstraint constraint(String name,
			BaseConstraintItem constraintSource, QueryAnnotation... annotations) {
		QueryConstraint constraintOption = new QueryConstraint(name);
		constraintOption.setSource(constraintSource);
		for (QueryAnnotation annotation : annotations) {
			constraintOption.addAnnotation(annotation);
		}
		return constraintOption;
	}

	/**
	 * Build a QueryCustom source for constraining queries.
	 * <p>
	 * This function is for constructing faceted custom constraints, and will
	 * need three three XQuery function extensions.
	 * 
	 * @param options
	 *            Three of these parameters must provide a parse, startFacet,
	 *            and finishFacet XQueryFunctionExtension. Additionally,
	 *            QueryAnnotations can be added to this builder function.
	 * @return a QueryCustom object for use in building a QueryConstraint
	 */
	public QueryCustom customFacet(QueryCustomItem... options) {
		QueryCustom custom = new QueryCustom(true);

		// , parse, start, finish);
		for (QueryCustomItem option : options) {
			option.build(custom);
		}
		return custom;
	}

	/**
	 * Build a QueryCustom source for constraining queries.
	 * <p>
	 * This function is for constructing non-faceted custom constraints.
	 * 
	 * @param extension
	 *            One argument must supply a parse XQueryFunctionExtensions.
	 * @return a QueryCustom object for use in building a QueryConstraint
	 */
	public QueryCustom customParse(XQueryExtension extension) {
		Parse parse = new Parse(extension.getApply(), extension.getNs(),
				extension.getAt());
		QueryCustom custom = new QueryCustom(false);
		custom.setParse(parse);
		return custom;
	}

	public QueryOptionsTextItem<Boolean> debug(Boolean debug) {
		return new QueryOptionsTextItem<Boolean>("setDebug", debug);
	}

	/**
	 * Build a default location to find suggestion sources
	 * 
	 * @param options
	 * @return a {@link QueryDefaultSuggestionSource} object for constructing
	 *         QueryOptions configurations.
	 */
	public QueryDefaultSuggestionSource defaultSuggestionSource(
			QuerySuggestionSourceItem... options) {
		QueryDefaultSuggestionSource suggestionSource = new QueryDefaultSuggestionSource();
		for (QuerySuggestionSourceItem option : options) {
			if (option instanceof QueryRange) {
				suggestionSource.setImplementation((QueryRange) option);
			} else if (option instanceof SuggestionSourceOption) {
				SuggestionSourceOption so = (SuggestionSourceOption) option;
				suggestionSource.addSuggestionOption(so.getValue());
			} else if (option instanceof WordLexicon) {
				suggestionSource.setWordLexicon((WordLexicon) option);
			}
		}
		return suggestionSource;
	}

	/**
	 * Construct a dom Element from a string. A utility function for creating
	 * DOM elements when needed for other builder functions.
	 * 
	 * @param xmlString
	 *            XML for an element.
	 * @return w3c.dom.Element representation the provided XML.
	 */
	public static org.w3c.dom.Element domElement(String xmlString) {
		org.w3c.dom.Element element = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(
					xmlString.getBytes());
			element = getFactory().newDocumentBuilder().parse(bais)
					.getDocumentElement();
		} catch (SAXException e) {
			logger.error("SAX Exception thrown creating element from string");
			throw new MarkLogicIOException(
					"Could not make Element from xmlString" + xmlString, e);
		} catch (IOException e) {
			logger.error("IOException thrown creating element from string");
			throw new MarkLogicIOException(
					"Could not make Element from xmlString" + xmlString, e);
		} catch (ParserConfigurationException e) {
			logger.error("ParserConfigurationException thrown creating element from string");
			throw new MarkLogicIOException(
					"Could not make Element from xmlString" + xmlString, e);
		}
		return element;
	}

	/**
	 * Build a new Element object for use in Index definitions (sources for
	 * constraints, terms, and suggestion sources) This builder function builds
	 * Element objects with no namespace declataration.
	 * 
	 * @param name
	 *            local name of an element specification.
	 * @return an Element object for use in constructing IndexReferences (and in
	 *         a few other places).
	 */
	public Element element(String name) {
		return new Element(null, name);
	}

	/**
	 * Build a new Element object for use in Index definitions (sources for
	 * constraints, terms, and suggestion sources) This builder function builds
	 * Element objects with a namespace declataration.
	 * 
	 * @param ns
	 *            Namespace URI for this element specification.
	 * @param name
	 *            local name of an element specification.
	 * @return an Element object for use in constructing IndexReferences (and in
	 *         a few other places).
	 */
	public Element element(String ns, String name) {
		return new Element(ns, name);
	}

	/**
	 * Build a new PathIndex object
	 */
	public PathIndex pathIndex(String text) {
		return new PathIndex(text);
	}

	/**
	 * Build a new PathIndex object
	 */
	public PathIndex pathIndex(String text, NamespaceBinding... nsbindings) {
		return new PathIndex(text, nsbindings);
	}

	/**
	 * Builds a QueryElementQuery object for use in constraining QueryOptions
	 * configuration to a particular element.
	 * 
	 * @param ns
	 *            Namespace of the element for restricting QueryOptions.
	 * @param name
	 *            Local name of the element for restricting QueryOptions.
	 * @return a QueryElementQuery object used for building a QueryConstraint
	 */
	public QueryElementQuery elementQuery(String ns, String name) {
		QueryElementQuery qeq = new QueryElementQuery();
		qeq.setNs(ns);
		qeq.setName(name);
		return qeq;
	}

	/**
	 * Builds a flag for how to interpret an empty search string. Used in
	 * QueryTerm configurations.
	 * 
	 * @param termApply
	 *            TermApply.ALL_RESULTS means an empty search retrieves
	 *            everything. TermApply.NO_RESULTS means an empty search string
	 *            returns nothing.
	 * @return A TermApply object for use in building QueryTerm configurations.
	 */
	public TermApply empty(TermApply termApply) {
		return termApply;
	}

	/**
	 * Builds a reference to an XQuery extension point.
	 * <p>
	 * To locate XQuery extensions , you need three values. Generally these
	 * values are to be provided by a MarkLogic administrator who has installed
	 * the modules.
	 * 
	 * @param apply
	 *            Name of function to apply.
	 * @param ns
	 *            Namespace of module in which to locate the function.
	 * @param at
	 *            Location on the modules search path at which to find the
	 *            function.
	 * @return an XQueryExtension object used to build custom contraints or
	 *         result transformations.
	 */
	public XQueryExtension extension(String apply, String ns, String at) {
		XQueryExtension locator = new XQueryExtension();
		locator.setApply(apply);
		locator.setAt(at);
		locator.setNs(ns);
		return locator;
	}

	/**
	 * Build a FacetOption for use in facetable constraints.
	 * 
	 * @param facetOption
	 * @return A FacetOption object for use in modifying facetable constraint
	 *         sources.
	 */
	public FacetOption facetOption(String facetOption) {
		FacetOption fo = new FacetOption();
		fo.setValue(facetOption);
		return fo;
	}

	/**
	 * Builds a field specification with the given name.
	 * <p>
	 * Field with given name must exist on the REST server to be used.
	 * 
	 * @param name
	 *            Name of a field from the REST server
	 * @return Field object for use in building constraint or value sources.
	 */
	public Field field(String name) {
		return new Field(name);
	}

	/**
	 * Builds an XQueryExtension to be used in a custom constraint to locate
	 * where facets end.
	 * 
	 * @param apply
	 *            Name of function to apply.
	 * @param ns
	 *            Namespace of module in which to locate the function.
	 * @param at
	 *            Location on the modules search path at which to find the
	 *            function.
	 * @return A FinishFacet object for use in building QueryCustom objects.
	 */
	public FinishFacet finishFacet(String apply, String ns, String at) {
		return new FinishFacet(apply, ns, at);
	}

	public QueryOptionsTextItem<Long> forest(Long forest) {
		return new QueryOptionsTextItem<Long>("addForest", forest);
	}

	public QueryOptionsTextItem<FragmentScope> fragmentScope(FragmentScope scope) {
		return new QueryOptionsTextItem<FragmentScope>("setFragmentScope",
				scope);
	}

	public GeospatialOption geoOption(String geoOption) {
		GeospatialOption go = new GeospatialOption();
		go.setValue(geoOption);
		return go;
	}

	/**
	 * Build a configuration to use a Geospatial attribute Pair Index.
	 * 
	 * @param parent
	 *            Element that is parent to both the latitude and longitude
	 *            elements
	 * @param latitudeAttribute
	 *            the attribute holding latitude values
	 * @param longitudeAttribute
	 *            the attribute holding longitude values
	 * @param options
	 *            Optional GeoOptions, FacetOptions a Heatmap.
	 * @return A component for use in a QueryOptionsBuilder expression.
	 */
	public QueryGeospatialAttributePair geospatialAttributePair(Element parent,
			Attribute latitudeAttribute, Attribute longitudeAttribute,
			QueryGeospatialItem... options) {
		QueryGeospatialAttributePair qga = new QueryGeospatialAttributePair();
		qga.setParent(parent);
		qga.setLatitude(latitudeAttribute);
		qga.setLongitude(longitudeAttribute);
		for (QueryGeospatialItem option : options) {
			option.build(qga);
		}
		return qga;
	}

	/**
	 * Build a configuration to use a Geospatial Element Index.
	 * 
	 * @param geoSpatialIndexElement
	 *            Builder Expression for an element that has latitude and
	 *            longitude values.
	 * @param options
	 *            Optional GeoOptions, FacetOptions a Heatmap.
	 * @return A component for use in a QueryOptionsBuilder expression.
	 */
	public QueryGeospatialElement geospatialElement(
			Element geoSpatialIndexElement, GeospatialOption... options) {
		QueryGeospatialElement qge = new QueryGeospatialElement();
		qge.setElement(geoSpatialIndexElement);
		for (GeospatialOption option : options) {
			option.build(qge);
		}
		return qge;
	}

	/**
	 * Build a configuration to use a Geospatial attribute Pair Index.
	 * 
	 * @param parent
	 *            Element that is parent to both the latitude and longitude
	 *            elements
	 * @param latitudeElement
	 *            the element holding latitude values
	 * @param longitudeElement
	 *            the element holding longitude values
	 * @param options
	 *            Optional GeoOptions, FacetOptions a Heatmap.
	 * @return A component for use in a QueryOptionsBuilder expression.
	 */
	public QueryGeospatialElementPair geospatialElementPair(Element parent,
			Element latitudeElement, Element longitudeElement,
			QueryGeospatialItem... options) {
		QueryGeospatialElementPair qga = new QueryGeospatialElementPair();
		qga.setParent(parent);
		qga.setLatitude(latitudeElement);
		qga.setLongitude(longitudeElement);
		for (QueryGeospatialItem option : options) {
			option.build(qga);
		}
		return qga;
	}

	/**
	 * Construct a new GrammarOption
	 * 
	 * @param quotation
	 *            optional string to qualify quotations in the search grammar.
	 * @param implicit
	 *            optional cts:element to wrap all searches implicitly. Set to
	 *            null for no implicit query.
	 * @param grammarItems
	 *            0..* QueryStarters and/or 0..* QueryJoiners to refine the
	 *            search grammar.
	 * @return A component for use in a QueryOptionsBuilder expression.
	 */
	public QueryGrammar grammar(String quotation, org.w3c.dom.Element implicit,
			QueryGrammarItem... grammarItems) {
		QueryGrammar grammar = new QueryGrammar();
		grammar.setQuotation(quotation);
		grammar.setImplicit(implicit);
		for (QueryGrammarItem queryGrammarItem : grammarItems) {
			if (queryGrammarItem instanceof QueryStarter) {
				QueryStarter starter = (QueryStarter) queryGrammarItem;
				grammar.addStarter(starter);
			} else if (queryGrammarItem instanceof QueryJoiner) {
				QueryJoiner joiner = (QueryJoiner) queryGrammarItem;
				grammar.addJoiner(joiner);
			}
		}
		return grammar;
	}

	/**
	 * Build a heatmap for use in a Geospatial Constraint.
	 * 
	 * @param south
	 *            southern boundary of heatmap in decimal degrees.
	 * @param west
	 *            western boundary of heatmap in decimal degrees.
	 * @param north
	 *            northern boundary of heatmap in decimal degrees.
	 * @param east
	 *            eastern boundary of heatmap in decimal degrees.
	 * @param latitudeDivs
	 *            number of divisions along the latitudinal axis.
	 * @param longitudeDivs
	 *            number of divisions along the longitudinal axis.
	 * @return a Heatmap used to build a Geospatial Constraint definition.
	 */
	public Heatmap heatmap(double south, double west, double north,
			double east, int latitudeDivs, int longitudeDivs) {
		Heatmap heatmap = new Heatmap();
		heatmap.setN(north);
		heatmap.setS(south);
		heatmap.setW(west);
		heatmap.setE(east);
		heatmap.setLatdivs(latitudeDivs);
		heatmap.setLondivs(longitudeDivs);
		return heatmap;
	}

	/**
	 * Create a joiner with just three arguments, such as this one:
	 * &lt;search:joiner strength="50"
	 * apply="constraint"&gt;:&lt;/search:joiner&gt;
	 * 
	 * @param joinerText
	 *            Text of the joiner (here, ":")
	 * @param strength
	 *            Strength of this joiner in relation to other joiners.
	 * @param apply
	 *            Scope of joiner application.
	 * @return QueryJoiner object for use in a Grammar Option
	 */
	public QueryJoiner joiner(String joinerText, int strength, JoinerApply apply) {
		return joiner(joinerText, strength, apply, null, null, null);
	}

	/**
	 * Build a joiner for use in a Search API QueryGrammar
	 * 
	 * @param joinerText
	 *            Text of the joiner.
	 * @param strength
	 *            Strength of this joiner relative to others.
	 * @param apply
	 *            Enum to specify how the joiner fits into the Search grammar.
	 * @param comparator
	 *            Enum to define semantics of the joiner
	 * @param tokenize
	 *            Enum to specify how the joiner tokenizes the search string.
	 * @return An object for use in constructing QueryGrammar configurations.
	 */
	public QueryGrammarItem joiner(String joinerText, int strength,
			JoinerApply apply, Comparator comparator, Tokenize tokenize) {
		QueryJoiner joiner = joiner(joinerText, strength, apply, null,
				tokenize, null);
		joiner.setCompare(comparator);
		return joiner;
	}

	/**
	 * Build a joiner for use in a Search API QueryGrammar
	 * 
	 * @param joinerText
	 *            Text of the joiner.
	 * @param strength
	 *            Strength of this joiner relative to others.
	 * @param apply
	 *            Enum to specify how the joiner fits into the Search grammar.
	 * @param element
	 *            QName of a cts query. This joiner encapsulates the cts query.
	 * @param token
	 *            Enum to specify how the joiner tokenizes the search string.
	 * @return An object for use in constructing QueryGrammar configurations.
	 */
	public QueryGrammarItem joiner(String joinerText, int strength,
			JoinerApply apply, QName element, Tokenize token) {
		return joiner(joinerText, strength, apply, element, token, null);
	}

	/**
	 * A third method for creating joiners. This method includes 'consume' which
	 * specifies how far to extend the scope of a cts:near-query query.
	 * 
	 * @param joinerText
	 *            Text of the joiner.
	 * @param strength
	 *            Strength of this joiner relative to others.
	 * @param apply
	 *            Enum to specify how the joiner fits into the Search grammar.
	 * @param element
	 *            QName of a cts query. This builder function requires
	 *            "cts:near-query".
	 * @param tokenize
	 *            Enum to specify how the joiner tokenizes the search string.
	 * @param consume
	 *            How many tokens to consume for evaluating the near-query
	 * @return An object for use in constructing QueryGrammar configurations.
	 */
	public QueryJoiner joiner(String joinerText, int strength,
			JoinerApply apply, QName element, Tokenize tokenize, Integer consume) {
		QueryJoiner joiner = new QueryJoiner(joinerText);
		joiner.setStrength(strength);
		joiner.setApply(apply);
		joiner.setElement(element);
		if (tokenize != null) {
			joiner.setTokenize(tokenize);
		}

		if (consume != null) {
			joiner.setConsume(consume);
		}

		return joiner;
	}

	/**
	 * Build an operator for use in a QueryGrammar configuration.
	 * 
	 * @param name
	 *            Name of the operator. Used in search strings by end-users of
	 *            the Search application.
	 * @param states
	 *            A number of states applied when using this operator.
	 * @return a QueryOperator object for use in building QueryGrammar
	 *         configurations.
	 */
	public QueryOperator operator(String name, QueryState... states) {
		QueryOperator operator = new QueryOperator();
		operator.setName(name);
		for (QueryState state : states) {
			operator.addState(state);
		}
		return operator;
	}

	public QueryOptionsTextItem<Long> pageLength(Long pageLength) {
		return new QueryOptionsTextItem<Long>("setPageLength", pageLength);
	}

	/**
	 * Builds an XQueryExtension to be used in a custom constraint to parse
	 * values into buckets.
	 * 
	 * @param apply
	 *            Name of function to apply.
	 * @param ns
	 *            Namespace of module in which to locate the function.
	 * @param at
	 *            Location on the modules search path at which to find the
	 *            function.
	 * @return A Parse object for use in building QueryCustom objects.
	 */
	public Parse parse(String apply, String ns, String at) {
		return new Parse(apply, ns, at);
	}

	/**
	 * Build a new QueryProperties, which restricts a named constraint to data
	 * stored in the properties fragment.
	 * 
	 * @return a QueryProperties object for use in building a QueryConstraint
	 *         configuration.
	 */
	public QueryProperties properties() {
		return new QueryProperties();
	}

	public QueryOptionsTextItem<Double> qualityWeight(Double qualityWeight) {
		return new QueryOptionsTextItem<Double>("setQualityWeight",
				qualityWeight);
	}

	/**
	 * @param hasFacets
	 * @param type
	 * @param options
	 * @return
	 */
	public QueryRange range(Boolean hasFacets, QName type,
			QueryRangeItem... options) {
		return range(hasFacets, type, null, options);
	}

	public QueryRange range(Boolean hasFacets, QName type, String collation,
			QueryRangeItem... options) {
		QueryRange rangeOption = new QueryRange();
		rangeOption.doFacets(hasFacets);
		rangeOption.setType(type);
		rangeOption.setCollation(collation);
		for (QueryRangeItem option : options) {
			option.build(rangeOption);
		}
		return rangeOption;
	}

	public QueryOptionsTextItem<Boolean> returnAggregates(
			Boolean returnAggregates) {
		return new QueryOptionsTextItem<Boolean>("setReturnAggregates",
				returnAggregates);
	}

	public QueryOptionsTextItem<Boolean> returnConstraints(
			Boolean returnConstraints) {
		return new QueryOptionsTextItem<Boolean>("setReturnConstraints",
				returnConstraints);
	}

	public QueryOptionsTextItem<Boolean> returnFacets(Boolean returnFacets) {
		return new QueryOptionsTextItem<Boolean>("setReturnFacets",
				returnFacets);
	}

	public QueryOptionsTextItem<Boolean> returnFrequencies(
			Boolean returnFrequencies) {
		return new QueryOptionsTextItem<Boolean>("setReturnFrequencies",
				returnFrequencies);
	}

	public QueryOptionsTextItem<Boolean> returnMetrics(Boolean returnFacets) {
		return new QueryOptionsTextItem<Boolean>("setReturnMetrics",
				returnFacets);
	}

	public QueryOptionsTextItem<Boolean> returnPlan(Boolean returnPlan) {
		return new QueryOptionsTextItem<Boolean>("setReturnPlan", returnPlan);
	}

	public QueryOptionsTextItem<Boolean> returnQtext(Boolean returnQtext) {
		return new QueryOptionsTextItem<Boolean>("setReturnQtext", returnQtext);
	}

	public QueryOptionsTextItem<Boolean> returnQuery(Boolean returnQuery) {
		return new QueryOptionsTextItem<Boolean>("setReturnQuery", returnQuery);
	}

	public QueryOptionsTextItem<Boolean> returnResults(Boolean returnFacets) {
		return new QueryOptionsTextItem<Boolean>("setReturnResults",
				returnFacets);
	}

	public QueryOptionsTextItem<Boolean> returnSimilar(Boolean returnSimilar) {
		return new QueryOptionsTextItem<Boolean>("setReturnSimilar",
				returnSimilar);
	}

	public QueryOptionsTextItem<Boolean> returnValues(Boolean returnValues) {
		return new QueryOptionsTextItem<Boolean>("setReturnValues",
				returnValues);
	}

	public QuerySortOrderItem score() {
		return QuerySortOrder.Score.YES;
	}

	/**
	 * Use an XML string to set the underlying searchable expression for a
	 * QueryOptions configuration.
	 * <p>
	 * Searchable Expression narrows a search. Supply any valid element with a
	 * searchable expression in it. You'll need an element wrapper so as to
	 * declare any namespace prefixes you use in the expression. Only the
	 * namespace declarations and text of the element are used in configuring
	 * the search options.
	 * 
	 * @param searchableExpression
	 *            String representation of a searchable XQuery expression, with
	 *            namespaces declared in an arbitrary element wrapper.
	 * @return Component for use in a QueryOptionsBuilder expression.
	 */
	public QuerySearchableExpression searchableExpression(
			String searchableExpression, NamespaceBinding... bindings) {
		return new QueryOptions.QuerySearchableExpression(searchableExpression, bindings);
	}

	public NamespaceBinding namespace(String prefix, String uri) {
		return new NamespaceBinding(prefix, uri);
	}

	public QueryOptionsTextItem<String> searchOption(String searchOption) {
		return new QueryOptionsTextItem<String>("addSearchOption", searchOption);
	}

	public QuerySortOrder sortOrder(String type, String collation,
			QuerySortOrderItem... options) {
		QuerySortOrder so = new QuerySortOrder();
		so.setType(new QName(type));
		so.setCollation(collation);

		for (QuerySortOrderItem option : options) {
			option.build(so);

		}
		return so;
	}

	public QueryStarter starterGrouping(String text, int strength,
			String delimiter) {
		QueryStarter starter = new QueryStarter();
		starter.setText(text);
		starter.setStrength(strength);
		starter.setApply(StarterApply.GROUPING);
		starter.setDelimiter(delimiter);
		return starter;
	}

	public QueryStarter starterPrefix(String text, int strength, QName element) {
		QueryStarter starter = new QueryStarter();
		starter.setText(text);
		starter.setStrength(strength);
		starter.setApply(StarterApply.PREFIX);
		starter.setElement(element);
		return starter;
	}

	/**
	 * Builds an XQueryExtension to be used in a custom constraint to locate
	 * where facets start.
	 * 
	 * @param apply
	 *            Name of function to apply.
	 * @param ns
	 *            Namespace of module in which to locate the function.
	 * @param at
	 *            Location on the modules search path at which to find the
	 *            function.
	 * @return A StartFacet object for use in building QueryCustom objects.
	 */
	public StartFacet startFacet(String apply, String ns, String at) {
		return new StartFacet(apply, ns, at);
	}

	public QueryState state(String stateName, QueryStateItem... options) {
		QueryState state = new QueryState();
		state.setName(stateName);
		for (QueryStateItem option : options) {
			state.addOption(option);
		}
		return state;

	}

	/**
	 * Build a QuerySuggestionSource to provide suggestions for type-ahead in
	 * search applications.
	 * 
	 * @param options
	 *            of items that can comprise a QuerySuggestionSource
	 * @return a {@link QuerySuggestionSource} for use in building
	 *         {@link QueryOptions}
	 */
	public QuerySuggestionSource suggestionSource(
			QuerySuggestionSourceItem... options) {
		QuerySuggestionSource suggestionSource = new QuerySuggestionSource();
		for (QuerySuggestionSourceItem option : options) {
			option.build(suggestionSource);
		}
		return suggestionSource;
	}

	/**
	 * Build a suggestion source by referencing a named constraint elsewhere in
	 * the QueryOptions
	 * 
	 * @param constraintReference
	 *            name of a constraint.
	 * @return A {@link QuerySuggestionSource} for use in building
	 *         {@link QueryOptions}
	 */
	public QuerySuggestionSource suggestionSource(String constraintReference,
			QuerySuggestionSourceItem... options) {
		QuerySuggestionSource suggestionSource = new QuerySuggestionSource();
		suggestionSource.setRef(constraintReference);
		for (QuerySuggestionSourceItem option : options) {
			option.build(suggestionSource);
		}
		return suggestionSource;
	}

	public SuggestionSourceOption suggestionSourceOption(String suggestionOption) {
		SuggestionSourceOption so = new SuggestionSourceOption();
		so.setValue(suggestionOption);
		return so;
	}

	/**
	 * Build a term configuration object.
	 */
	public QueryTerm term(QueryTermItem... termOptions) {
		QueryTerm term = new QueryTerm();
		for (QueryTermItem option : termOptions) {
			if (option instanceof TermApply) {
				term.setEmptyApply((TermApply) option);
			}
			if (option instanceof BaseConstraintItem) {
				term.setConstraintItem((BaseConstraintItem) option);
			}
		}
		return term;
	}

	public TermOption termOption(String term) {
		TermOption to = new TermOption();
		to.setValue(term);
		return to;
	}

	public QueryTransformResults transformResults(String apply) {
		QueryTransformResults t = new QueryTransformResults();
		t.setApply(apply);
		return t;
	}

	public QueryTransformResults transformResults(XQueryExtension extension) {
		QueryTransformResults t = new QueryTransformResults();
		t.setApply(extension.getApply());
		t.setNs(extension.getNs());
		t.setAt(extension.getAt());
		return t;
	}

	public QName type(String type) {
		return new QName(type);
	}

	public QueryUri uri() {
		return QueryUri.YES;
	}

	public QueryValue value(QueryValueItem... valueOptions) {
		QueryValue vo = new QueryValue();
		for (QueryValueItem option : valueOptions) {
			option.build(vo);
		}
		return vo;
	}

	public QueryValues values(String name, QueryValuesItem... valueOptions) {
		QueryValues v = new QueryValues();
		v.setName(name);
		for (QueryValuesItem option : valueOptions) {
			option.build(v);
		}
		return v;
	}

	public ValuesOption valuesOption(String valuesOption) {
		ValuesOption vo = new ValuesOption();
		vo.setValue(valuesOption);
		return vo;
	}

	public Weight weight(Double weight) {
		Weight w = new Weight();
		w.setValue(weight);
		return w;
	}

	public QueryWord word(QueryWordItem... wordOptions) {
		QueryWord wo = new QueryWord();
		for (QueryWordItem option : wordOptions) {
			option.build(wo);
		}
		return wo;
	}

	public WordLexicon wordLexicon() {
		return new WordLexicon();
	}

	public WordLexicon wordLexicon(String collation) {
		WordLexicon lex = new WordLexicon();
		lex.setCollation(collation);
		return lex;
	}

	public WordLexicon wordLexicon(String collation, FragmentScope scope) {
		WordLexicon lex = new WordLexicon();
		lex.setCollation(collation);
		lex.setFragmentScope(scope);
		return lex;
	}

	public ConstraintValue constraintValue(String constraintReference) {
		return new ConstraintValue(constraintReference);
	}

	public JsonKey jsonkey(String name) {
		return new JsonKey(name);
	}

	public QNameExtractor qname(String elemNs, String elemName, String attrNs,
			String attrName) {
		QNameExtractor qname = new QNameExtractor();
		qname.setAttrName(attrName);
		qname.setAttrNs(attrNs);
		qname.setElemName(elemName);
		qname.setElemNs(elemNs);
		return qname;
	}

	public QueryOptionsItem extractMetadata(
			QueryExtractMetadataItem... extractMetadataItems) {
		QueryExtractMetadata extractMetadata = new QueryExtractMetadata();
		for (QueryExtractMetadataItem item : extractMetadataItems) {
			item.build(extractMetadata);
		}
		return extractMetadata;
	}
}
