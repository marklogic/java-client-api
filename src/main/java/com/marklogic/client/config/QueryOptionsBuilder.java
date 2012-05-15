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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.config.QueryOptionsBuilder.QueryGrammar.QueryJoiner;
import com.marklogic.client.config.QueryOptionsBuilder.QueryGrammar.QueryJoiner.Comparator;
import com.marklogic.client.config.QueryOptionsBuilder.QueryGrammar.QueryJoiner.JoinerApply;
import com.marklogic.client.config.QueryOptionsBuilder.QueryGrammar.QueryStarter;
import com.marklogic.client.config.QueryOptionsBuilder.QueryGrammar.QueryStarter.StarterApply;
import com.marklogic.client.config.QueryOptionsBuilder.QueryGrammar.Tokenize;
import com.marklogic.client.config.QueryOptionsBuilder.QueryRange.Bucket;
import com.marklogic.client.config.QueryOptionsBuilder.QuerySortOrder.Direction;
import com.marklogic.client.config.QueryOptionsBuilder.QueryTerm.TermApply;

/**
 * Builder of QueryOptions objects, which are used to configure MarkLogic
 * runtime search, lexicon, structured queries and key/value queries.
 * 
 */
public final class QueryOptionsBuilder {

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(namespace = QueryOptions.SEARCH_NS, name = "options")
	public static final class QueryOptions extends BaseQueryOptionConfiguration implements QueryAnnotations {

		public static final String SEARCH_NS = "http://marklogic.com/appservices/search";
		// Boolean options
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-facets")
		private Boolean returnFacets;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-constraintOptions")
		private Boolean returnConstraints;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-metrics")
		private Boolean returnMetrics;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-plan")
		private Boolean returnPlan;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-qtext")
		private Boolean returnQtext;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-query")
		private Boolean returnQuery;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-results")
		private Boolean returnResults;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-similar")
		private Boolean returnSimilar;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-aggregates")
		private Boolean returnAggregates;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-values")
		private Boolean returnValues;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-frequencies")
		private Boolean returnFrequencies;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "debug")
		private Boolean debug;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "searchable-expression")
		private String searchableExpression;
		@SuppressWarnings("rawtypes")
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "term")
		private QueryTerm termConfig;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "transform-results")
		private QueryTransformResults transformResultsOption;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "fragment-scope")
		private String fragmentScope; // TODO fragment scope enum
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "concurrency-level")
		private Integer concurrencyLevel;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "page-length")
		private Long pageLength;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "quality-weight")
		private Double qualityWeight;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "forest")
		private List<Long> forests;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "search-option")
		private List<String> searchOptions;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "constraint")
		private List<QueryConstraint> queryConstraints;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "operator")
		private List<QueryOperator> operatorOptions;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "sort-order")
		private List<QuerySortOrder> sortOrders;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "suggestion-source")
		private List<QuerySuggestionSource> suggestionSourceOptions;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "additional-query")
		private AnyElement additionalQuery;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "grammar")
		private QueryGrammar grammarOption;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "default-suggestion-source")
		private QueryDefaultSuggestionSource defaultSuggestionSource;

		public QueryOptions() {
			// options that can have more than one cardinality
			// are in lists.
			queryConstraints = new ArrayList<QueryConstraint>();
			operatorOptions = new ArrayList<QueryOperator>();
			sortOrders = new ArrayList<QuerySortOrder>();
			suggestionSourceOptions = new ArrayList<QuerySuggestionSource>();
			forests = new ArrayList<Long>();
			searchOptions = new ArrayList<String>();

		}

		public Boolean getReturnFacets() {
			return returnFacets;
		}

		public void setReturnFacets(Boolean returnFacets) {
			this.returnFacets = returnFacets;
		}
		

		public Boolean getReturnFrequencies() {
			return returnFrequencies;
		}

		public void setReturnFrequencies(Boolean returnFrequencies) {
			this.returnFrequencies = returnFrequencies;
		}

		public Boolean getReturnValues() {
			return returnValues;
		}

		public void setReturnValues(Boolean returnValues) {
			this.returnValues = returnValues;
		}
		

		public Boolean getReturnAggregates() {
			return returnAggregates;
		}

		public void setReturnAggregates(Boolean returnAggregates) {
			this.returnAggregates = returnAggregates;
		}
		public Boolean getReturnConstraints() {
			return returnConstraints;
		}

		public void setReturnConstraints(Boolean returnConstraints) {
			this.returnConstraints = returnConstraints;
		}

		public Boolean getReturnMetrics() {
			return returnMetrics;
		}

		public void setReturnMetrics(Boolean returnMetrics) {
			this.returnMetrics = returnMetrics;
		}

		public Boolean getReturnPlan() {
			return returnPlan;
		}

		public void setReturnPlan(Boolean returnPlan) {
			this.returnPlan = returnPlan;
		}

		public Boolean getReturnQtext() {
			return returnQtext;
		}

		public void setReturnQtext(Boolean returnQtext) {
			this.returnQtext = returnQtext;
		}

		public Boolean getReturnQuery() {
			return returnQuery;
		}

		public void setReturnQuery(Boolean returnQuery) {
			this.returnQuery = returnQuery;
		}

		public Boolean getReturnResults() {
			return returnResults;
		}

		public void setReturnResults(Boolean returnResults) {
			this.returnResults = returnResults;
		}

		public Boolean getReturnSimilar() {
			return returnSimilar;
		}

		public void setReturnSimilar(Boolean returnSimilar) {
			this.returnSimilar = returnSimilar;
		}

		public Boolean getDebug() {
			return debug;
		}

		public void setDebug(Boolean debug) {
			this.debug = debug;
		}

		public String getSearchableExpression() {
			return searchableExpression;
		}

		public void setSearchableExpression(String searchableExpression) {
			this.searchableExpression = searchableExpression;
		}

		@SuppressWarnings("rawtypes")
		public QueryTerm getTerm() {
			return termConfig;
		}

		@SuppressWarnings("rawtypes")
		public void setTerm(QueryTerm termConfig) {
			this.termConfig = termConfig;
		}

		public QueryTransformResults getTransformResults() {
			return transformResultsOption;
		}

		public void setTransformResults(
				QueryTransformResults transformResultsOption) {
			this.transformResultsOption = transformResultsOption;
		}

		public String getFragmentScope() {
			return fragmentScope;
		}

		public void setFragmentScope(FragmentScope fragmentScope) {
			this.fragmentScope = fragmentScope.toString().toLowerCase();
		}

		public Integer getConcurrencyLevel() {
			return concurrencyLevel;
		}

		public void setConcurrencyLevel(Integer concurrencyLevel) {
			this.concurrencyLevel = concurrencyLevel;
		}

		public Long getPageLength() {
			return pageLength;
		}

		public void setPageLength(Long pageLength) {
			this.pageLength = pageLength;
		}

		public Double getQualityWeight() {
			return qualityWeight;
		}

		public void setQualityWeight(Double qualityWeight) {
			this.qualityWeight = qualityWeight;
		}

		public List<Long> getForests() {
			return forests;
		}

		public void setForests(List<Long> forests) {
			this.forests = forests;
		}
		
		public void addForest(Long forest) {
			this.forests.add(forest);
		}

		public List<String> getSearchOptions() {
			return searchOptions;
		}

		public void setSearchOptions(List<String> searchOptions) {
			this.searchOptions = searchOptions;
		}
		

		public void addSearchOption(String searchOption) {
			this.searchOptions.add(searchOption);
		}

		public List<QueryConstraint> getQueryConstraints() {
			if (queryConstraints == null) {
				return new ArrayList<QueryConstraint>();
			} else {
				return queryConstraints;
			}
		}

		public List<QueryOperator> getQueryOperators() {
			return operatorOptions;
		}

		public void setOperators(List<QueryOperator> operatorOptions) {
			this.operatorOptions = operatorOptions;
		}

		public List<QuerySortOrder> getSortOrders() {
			return sortOrders;
		}

		public void setSortOrders(List<QuerySortOrder> sortOrders) {
			this.sortOrders = sortOrders;
		}

		public List<QuerySuggestionSource> getSuggestionSources() {
			return suggestionSourceOptions;
		}

		public void setSuggestionSources(
				List<QuerySuggestionSource> suggestionSourceOptions) {
			this.suggestionSourceOptions = suggestionSourceOptions;
		}

		public org.w3c.dom.Element getAdditionalQuery() {
			return additionalQuery.getValue();
		}

		public void setAdditionalQuery(org.w3c.dom.Element additionalQuery) {
			this.additionalQuery = new AnyElement(additionalQuery);
		}

		public QueryGrammar getGrammar() {
			return grammarOption;
		}

		public void setGrammar(QueryGrammar grammarOption) {
			this.grammarOption = grammarOption;
		}

		public void setDefaultSuggestionSource(QueryDefaultSuggestionSource dss) {
			this.defaultSuggestionSource = dss;
		}

		@Override
		public List<QueryAnnotation> getAnnotations() {
			return super.getAnnotations();
		}

		@Override
		public void addElementAsAnnotation(org.w3c.dom.Element element) {
			this.addElementAsAnnotation(element);
		}

		@Override
		public void deleteAnnotations() {
			this.deleteAnnotations();
		}

		public void addAnnotation(QueryAnnotation queryAnnotation) {
			this.getAnnotations().add(queryAnnotation);
		}
	}

	/**
	 * Wraps any element, for those places in the Search API schema where any
	 * XML element may be used.
	 * 
	 * @see com.marklogic.client.configpojos.QueryGrammar
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class AnyElement implements QueryOptionsItem {

		@XmlAnyElement
		private org.w3c.dom.Element element;

		public AnyElement() {
		}

		public AnyElement(org.w3c.dom.Element ctsQuery) {
			element = ctsQuery;
		}

		public org.w3c.dom.Element getValue() {
			return element;
		}

		@Override
		public void build(QueryOptions options) {
			options.setAdditionalQuery(element);
		}

	}

	public static class Attribute extends MarkLogicQName implements
			QueryWordItem, QueryRangeItem {

		public Attribute() {

		}

		public Attribute(String ns, String name) {
			super(ns, name);
		}
	}

	/**
	 * Each constraint in the MarkLogic Search API is of a certain type. This
	 * class is the root of the class hierarchy of Range, WordOption,
	 * ValueOption, etc. Note: It contains convenience methods for helping with
	 * index definitions, which are not applicable to all constraint types.
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static abstract class BaseConstraintItem<T extends BaseConstraintItem<T>>
			extends BaseQueryOptionConfiguration implements
			QuerySuggestionSourceItem {

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "attribute")
		private MarkLogicQName attributeReference;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "element")
		private MarkLogicQName elementReference;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "field")
		private Field fieldReference;

		public BaseConstraintItem() {
		}

		public QName getAttribute() {
			return attributeReference.asQName();
		}

		public QName getElement() {
			return elementReference.asQName();
		}

		public String getFieldName() {
			return this.fieldReference.getName();
		}

		public void setAttribute(Attribute attribute) {
			this.attributeReference = attribute;
		}

		/**
		 * Add a reference to an element to this ConstraintBase
		 */
		public void setElement(Element element) {
			this.elementReference = element;
		}

		public void setField(Field field) {
			this.fieldReference = field;
		}

		@Override
		public void build(QuerySuggestionSource suggestionSource) {
			suggestionSource.setImplementation(this);
		}
	}

	/**
	 * Models a constraint on collectionOption URI.
	 * 
	 */
	@XmlRootElement(name = "collectionOption")
	public static class QueryCollection extends
			FacetableConstraintConfiguration<QueryCollection> {

		/**
		 * This value is removed from collectionOption URIs when creating facet
		 * labels.
		 */
		@XmlAttribute
		private String prefix;

		/**
		 * 
		 * @return The prefix to be removed from collectionOption URIs in
		 *         generating facet labels.
		 */

		public String getPrefix() {
			return prefix;
		}

		/**
		 * Set the collectionOption prefix, returning the modified
		 * CollectionOption object.
		 * 
		 * @param prefix
		 *            The prefix to be removed from collectionOption URIs in
		 *            generating facet labels.
		 */
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryConstraintConfigurationBag extends
			BaseQueryOptionConfiguration {
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "collection")
		private QueryCollection collection;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "custom")
		private QueryCustom custom;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "element-query")
		private QueryElementQuery elementQuery;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-attr-pair")
		private GeoAttrPair geoAttrPair;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-elem")
		private GeoElement geoElem;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-elem-pair")
		private GeoElementPair geoElemPair;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "properties")
		private QueryProperties properties;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "range")
		private QueryRange range;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "value")
		private QueryValue value;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "word")
		private QueryWord word;

		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public <T extends BaseConstraintItem> T getConstraintConfiguration() {
			if (collection != null) {
				return (T) collection;
			} else if (value != null) {
				return (T) value;
			} else if (range != null) {
				return (T) range;
			} else if (word != null) {
				return (T) word;
			} else if (elementQuery != null) {
				return (T) elementQuery;
			} else if (properties != null) {
				return (T) properties;
			} else if (custom != null) {
				return (T) custom;
			} else if (geoElem != null) {
				return (T) geoElem;
			} else if (geoAttrPair != null) {
				return (T) geoAttrPair;
			} else if (geoElemPair != null) {
				return (T) geoElemPair;
			}
			return null;
		}

		@SuppressWarnings("rawtypes")
		public <T extends BaseConstraintItem> void setImplementation(
				T constraintDefinition) {
			if (constraintDefinition.getClass() == QueryCollection.class) {
				collection = (QueryCollection) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryValue.class) {
				value = (QueryValue) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryWord.class) {
				word = (QueryWord) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryRange.class) {
				range = (QueryRange) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryElementQuery.class) {
				elementQuery = (QueryElementQuery) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryProperties.class) {
				properties = (QueryProperties) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryCustom.class) {
				custom = (QueryCustom) constraintDefinition;
			} else if (constraintDefinition.getClass() == GeoElement.class) {
				geoElem = (GeoElement) constraintDefinition;
			} else if (constraintDefinition.getClass() == GeoAttrPair.class) {
				geoAttrPair = (GeoAttrPair) constraintDefinition;
			} else if (constraintDefinition.getClass() == GeoElementPair.class) {
				geoElemPair = (GeoElementPair) constraintDefinition;
			}
		}
	}

	/**
	 * Models a constraint node in Search API configuration.
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryConstraint extends BaseQueryOptionConfiguration
			implements QueryOptionsItem, QueryAnnotations {

		@XmlAttribute
		private String name;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "collection")
		private QueryCollection collection;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "custom")
		private QueryCustom custom;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "element-query")
		private QueryElementQuery elementQuery;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-attr-pair")
		private GeoAttrPair geoAttrPair;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-elem")
		private GeoElement geoElem;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-elem-pair")
		private GeoElementPair geoElemPair;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "properties")
		private QueryProperties properties;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "range")
		private QueryRange range;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "value")
		private QueryValue value;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "word")
		private QueryWord word;

		public <T extends BaseConstraintItem> T getConstraintConfiguration() {
			if (collection != null) {
				return (T) collection;
			} else if (value != null) {
				return (T) value;
			} else if (range != null) {
				return (T) range;
			} else if (word != null) {
				return (T) word;
			} else if (elementQuery != null) {
				return (T) elementQuery;
			} else if (properties != null) {
				return (T) properties;
			} else if (custom != null) {
				return (T) custom;
			} else if (geoElem != null) {
				return (T) geoElem;
			} else if (geoAttrPair != null) {
				return (T) geoAttrPair;
			} else if (geoElemPair != null) {
				return (T) geoElemPair;
			}
			return null;
		}

		public <T extends BaseConstraintItem> void setImplementation(
				T constraintDefinition) {
			if (constraintDefinition.getClass() == QueryCollection.class) {
				collection = (QueryCollection) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryValue.class) {
				value = (QueryValue) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryWord.class) {
				word = (QueryWord) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryRange.class) {
				range = (QueryRange) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryElementQuery.class) {
				elementQuery = (QueryElementQuery) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryProperties.class) {
				properties = (QueryProperties) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryCustom.class) {
				custom = (QueryCustom) constraintDefinition;
			} else if (constraintDefinition.getClass() == GeoElement.class) {
				geoElem = (GeoElement) constraintDefinition;
			} else if (constraintDefinition.getClass() == GeoAttrPair.class) {
				geoAttrPair = (GeoAttrPair) constraintDefinition;
			} else if (constraintDefinition.getClass() == GeoElementPair.class) {
				geoElemPair = (GeoElementPair) constraintDefinition;
			}
		}

		public QueryConstraint() {
			super();
		}

		public QueryConstraint(String name) {
			this();
			setName(name);
		}

		@Override
		public List<QueryAnnotation> getAnnotations() {
			return super.getAnnotations();
		}

		@Override
		public void addElementAsAnnotation(org.w3c.dom.Element element) {
			super.addElementAsAnnotation(element);
		}

		@Override
		public void deleteAnnotations() {
			super.deleteAnnotations();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public void build(QueryOptions options) {
			options.getQueryConstraints().add(this);
		}

	}

	@XmlRootElement(name = "custom")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryCustom extends BaseConstraintItem<QueryCustom>
			implements QueryAnnotations {

		@XmlAttribute(name = "facet")
		private boolean doFacets;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "finish-facet")
		private XQueryExtension finishFacet;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "parse")
		private XQueryExtension parse;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "start-facet")
		private XQueryExtension startFacet;

		public QueryCustom(boolean doFacets, XQueryExtension parseExtension) {
			this.doFacets = doFacets;
			this.parse = parseExtension;
		}

		public QueryCustom(boolean doFacets, XQueryExtension parse,
				XQueryExtension start, XQueryExtension finish) {
			this.doFacets = doFacets;
			this.parse = parse;
			this.startFacet = start;
			this.finishFacet = finish;
		}

		QueryCustom() {
		}

		public boolean getDoFacets() {
			return doFacets;
		}

		public ExtensionPoint getFinishFacet() {
			return finishFacet;
		}

		public ExtensionPoint getParse() {
			return parse;
		}

		public ExtensionPoint getStartFacet() {
			return startFacet;
		}

		@Override
		public List<QueryAnnotation> getAnnotations() {
			return super.getAnnotations();
		}

		@Override
		public void addElementAsAnnotation(org.w3c.dom.Element element) {
			super.addElementAsAnnotation(element);
		}

		@Override
		public void deleteAnnotations() {
			super.deleteAnnotations();
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "default-suggestion-source")
	public final static class QueryDefaultSuggestionSource extends
			QuerySuggestionSource implements QueryOptionsItem {

		@Override
		public void build(QueryOptions options) {
			options.setDefaultSuggestionSource(this);
		}

		public QueryDefaultSuggestionSource() {
			super();
		}

	}

	public static class Element extends MarkLogicQName implements
			IndexReference {

		public Element() {

		}

		public Element(String ns, String name) {
			super(ns, name);
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "element-query")
	public static class QueryElementQuery extends
			BaseConstraintItem<QueryElementQuery> implements
			QueryConstraintItem {

		@XmlAttribute
		private String name;

		@XmlAttribute
		private String ns;

		public String getName() {
			return name;
		}

		public String getNs() {
			return ns;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setNs(String ns) {
			this.ns = ns;
		}

	}

	/**
	 * Defines how to locate an XQuery module and function for use as a Search
	 * API extension point.
	 * 
	 * Modules in XQuery have a "namespace URI" to identify them, and a path at
	 * which to locate the module file. Within the module, each function name is
	 * string. Together these three Strings find an extension point installed in
	 * an XQuery module.
	 */
	public interface ExtensionPoint {

		public String getApply();

		public String getAt();

		public String getNs();

		public void setApply(String apply);

		public void setAt(String at);

		public void setNs(String ns);

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public abstract static class FacetableConstraintConfiguration<T extends FacetableConstraintConfiguration<T>>
			extends BaseConstraintItem<T> {

		@XmlAttribute
		private String collation;

		@XmlAttribute(name = "facet")
		private boolean doFacets;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "facet-option")
		private List<String> facetOptions;

		@XmlAttribute
		private QName type;

		public FacetableConstraintConfiguration() {
			facetOptions = new ArrayList<String>();
		}

		/**
		 * Add a facet option to this constraint type, returning this.
		 * 
		 * @param facetOption
		 * @return this object, for further fluent setters.
		 */
		public void addFacetOption(String facetOption) {
			this.facetOptions.add(facetOption);
		}

		/**
		 * Perform facets on this constraint.
		 * 
		 * @param doFacets
		 *            set to true to configure facets, false otherwise.
		 */
		public void doFacets(boolean doFacets) {
			this.doFacets = doFacets;
		}

		/**
		 * @return true if this constraint is configured for facets. False
		 *         otherwise.
		 */
		public boolean getDoFacets() {
			return doFacets;
		}

		/**
		 * get the list of facet options.
		 * 
		 * @return
		 */
		public List<String> getFacetOptions() {
			return facetOptions;
		}

		public void setCollation(String collation) {
			this.collation = collation;
		}

		public void setFacetOptions(List<String> facetOptions) {
			this.facetOptions = new ArrayList<String>();
			for (String option : facetOptions) {
				this.facetOptions.add(option);
			}
		}

		public void setType(QName type) {
			this.type = type;
		}
	}

	public static class FacetOption extends TextOption<String> implements
			QueryRangeItem {

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Field implements QueryWordItem, QueryRangeItem,
			IndexReference {

		@XmlAttribute
		private String name;

		public Field() {

		}

		public Field(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public enum FragmentScope {

		DOCUMENTS, PROPERTIES;

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "geo-attr-pair")
	public static class GeoAttrPair extends
			GeospatialConstraintConfiguration<GeoAttrPair> {

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "geo-elem")
	public static class GeoElement extends
			GeospatialConstraintConfiguration<GeoElement> {

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "geo-elem-pair")
	public static class GeoElementPair extends
			GeospatialConstraintConfiguration<GeoElementPair> {

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private abstract static class GeospatialConstraintConfiguration<T extends GeospatialConstraintConfiguration<T>>
			extends BaseConstraintItem<T> {

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-option")
		private List<String> geoOptions;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "heatmap")
		private Heatmap heatmap;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "lat")
		private MarkLogicQName latitude;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "lon")
		private MarkLogicQName longitude;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "parent")
		private MarkLogicQName parent;

		public Heatmap getHeatmap() {
			return heatmap;
		}

		public MarkLogicQName getLatitude() {
			return latitude;
		}

		public MarkLogicQName getLongitude() {
			return longitude;
		}

		public MarkLogicQName getParent() {
			return parent;
		}

		@SuppressWarnings("unchecked")
		public void setGeoOptions(String geoOption) {
			this.geoOptions.add(geoOption);
		}

		public void setHeatmap(Heatmap heatmap) {
			this.heatmap = heatmap;
		}

		public void setLatitude(MarkLogicQName latitude) {
			this.latitude = latitude;
		}

		public void setLongitude(MarkLogicQName longitude) {
			this.longitude = longitude;
		}

		public void setParent(MarkLogicQName parent) {
			this.parent = parent;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class QueryGrammar implements QueryOptionsItem {

		@XmlAccessorType(XmlAccessType.FIELD)
		public final static class QueryStarter implements GrammarItem {

			public enum StarterApply {

				GROUPING, PREFIX;

				String toXMLString() {
					return this.toString().toLowerCase().replace("_", "-");
				}

				static StarterApply fromXMLString(String xmlString) {
					return StarterApply.valueOf(xmlString.toUpperCase()
							.replace("-", "_"));
				}
			}

			@XmlAttribute
			private String apply;
			@XmlAttribute
			private String delimiter;
			@XmlAttribute
			private QName element;
			@XmlAttribute
			private String options;
			@XmlValue
			private String starterText;
			@XmlAttribute
			private int strength;

			@XmlAttribute
			private String tokenize;

			public QueryStarter() {

			}

			public QueryStarter(String text) {
				this.starterText = text;
			}

			public StarterApply getApply() {
				return StarterApply.fromXMLString(apply);
			}

			public String getDelimiter() {
				return delimiter;
			}

			public QName getElement() {
				return element;
			}

			public String getOptions() {
				return options;
			}

			public String getStarterText() {
				return starterText;
			}

			public int getStrength() {
				return strength;
			}

			public Tokenize getTokenize() {
				return Tokenize.fromXMLString(tokenize);
			}

			public void setApply(StarterApply apply) {
				this.apply = apply.toXMLString();
			}

			public void setApply(String apply) {
				this.apply = apply;
			}

			public void setDelimiter(String delimiter) {
				this.delimiter = delimiter;
			}

			public void setElement(QName element) {
				this.element = element;
			}

			public void setOptions(String options) {
				this.options = options;
			}

			public void setStrength(int strength) {
				this.strength = strength;
			}

			public void setText(String text) {
				this.starterText = text;
			}

			public void setTokenize(String tokenize) {
				this.tokenize = tokenize;
			}

			public QueryStarter withDelimiter(String delimiter) {
				this.delimiter = delimiter;
				return this;
			}

			public QueryStarter withStrength(int strength) {
				this.strength = strength;
				return this;
			}

		}

		public enum Tokenize {

			WORD, DEFAULT;

			String toXMLString() {
				return this.toString().toLowerCase().replace("_", "-");
			}

			static Tokenize fromXMLString(String xmlString) {
				return Tokenize.valueOf(xmlString.toUpperCase().replace("-",
						"_"));
			}

		}

		@XmlAccessorType(XmlAccessType.FIELD)
		public static final class QueryJoiner implements GrammarItem {

			public enum Comparator {
				EQ, GE, GT, LE, LT, NE;
			}

			public enum JoinerApply {

				CONSTRAINT, INFIX, NEAR2, PREFIX;

				static JoinerApply fromXMLString(String xmlString) {
					return JoinerApply.valueOf(xmlString.toUpperCase()
							.replace("-", "_"));
				}

				String toXMLString() {
					return this.toString().toLowerCase().replace("_", "-");
				}
			}
			
			@XmlAttribute
			private String apply;

			@XmlAttribute
			private String comparator;

			@XmlAttribute
			private int consume;

			@XmlAttribute
			private String delimiter;
			@XmlAttribute
			private QName element;
			@XmlValue
			private String joinerText;
			@XmlAttribute
			private String options;
			@XmlAttribute
			private int strength;
			@XmlAttribute
			private String tokenize;

			public QueryJoiner() {
			}

			public QueryJoiner(String joinerText) {
				this.joinerText = joinerText;
			}

			public String getApply() {
				return apply;
			};

			public Comparator getComparator() {
				return Comparator.valueOf(this.comparator);
			}

			public int getConsume() {
				return consume;
			}

			public String getDelimiter() {
				return delimiter;
			}

			public QName getElement() {
				return element;
			}

			public String getJoinerText() {
				return joinerText;
			}

			public String getOptions() {
				return options;
			}

			public int getStrength() {
				return strength;
			}

			public String getTokenize() {
				return tokenize;
			}

			public void setApply(JoinerApply apply) {
				this.apply = apply.toXMLString();
			}

			public void setApply(String apply) {
				this.apply = apply;
			}

			public void setCompare(Comparator comparator) {
				this.comparator = comparator.toString();
			}

			public void setConsume(int consume) {
				this.consume = consume;
			}

			public void setElement(QName qName) {
				this.element = qName;
			}

			public void setStrength(int strength) {
				this.strength = strength;
			}

			public void setTokenize(Tokenize tokenize) {
				this.tokenize = tokenize.toXMLString();
			}

		}

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "implicit")
		private AnyElement implicit;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "joiner")
		private List<QueryJoiner> joiners;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "quotation")
		private String quotation;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "starter")
		private List<QueryStarter> starters;

		public QueryGrammar() {
			joiners = new ArrayList<QueryJoiner>();
			starters = new ArrayList<QueryStarter>();
		}

		public void addJoiner(QueryJoiner joiner) {
			this.joiners.add(joiner);
		}

		public void addStarter(QueryStarter starter) {
			this.starters.add(starter);
		}

		public org.w3c.dom.Element getImplicit() {
			return implicit.getValue();
		}

		public List<QueryJoiner> getJoiners() {
			return joiners;
		}

		public String getQuotation() {
			return quotation;
		}

		public List<QueryStarter> getStarters() {
			return starters;
		}

		public void setImplicit(org.w3c.dom.Element implicit) {
			this.implicit = new AnyElement(implicit);
		}

		public void setQuotation(String quotation) {
			this.quotation = quotation;
		}

		@Override
		public void build(QueryOptions options) {
			options.setGrammar(this);
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Heatmap {

		@XmlAttribute
		private double e;
		@XmlAttribute
		private int latdivs;
		@XmlAttribute
		private int londivs;
		@XmlAttribute
		private double n;
		@XmlAttribute
		private double s;
		@XmlAttribute
		private double w;

		public double getE() {
			return e;
		}

		public int getLatdivs() {
			return latdivs;
		}

		public int getLondivs() {
			return londivs;
		}

		public double getN() {
			return n;
		}

		public double getS() {
			return s;
		}

		public double getW() {
			return w;
		}

		public Heatmap setE(double e) {
			this.e = e;
			return this;
		}

		public Heatmap setLatdivs(int latdivs) {
			this.latdivs = latdivs;
			return this;
		}

		public Heatmap setLondivs(int londivs) {
			this.londivs = londivs;
			return this;
		}

		public Heatmap setN(double n) {
			this.n = n;
			return this;
		}

		public Heatmap setS(double s) {
			this.s = s;
			return this;
		}

		public Heatmap setW(double w) {
			this.w = w;
			return this;
		}
	}


	@XmlAccessorType(XmlAccessType.FIELD)
	public static class MarkLogicQName implements IndexReference {

		@XmlAttribute
		private String name;
		@XmlAttribute
		private String ns;

		public MarkLogicQName() {

		}

		public MarkLogicQName(String ns, String name) {
			this.ns = ns;
			this.name = name;
		}

		public QName asQName() {
			return new QName(getNs(), getName());
		}

		public String getName() {
			return name;
		}

		public String getNs() {
			return ns;
		}

	}

	/**
	 * Represents how query terms are to be combined.
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryOperator extends BaseQueryOptionConfiguration
			implements QueryOptionsItem, QueryAnnotations {

		@XmlAttribute
		private String name;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "state")
		private List<QueryState> states;

		public QueryOperator() {
			states = new ArrayList<QueryState>();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<QueryState> getStates() {
			return states;
		}

		public void addState(QueryState state) {
			states.add(state);
		}

		@Override
		public void build(QueryOptions options) {
			options.getQueryOperators().add(this);
		}

		@Override
		public List<QueryAnnotation> getAnnotations() {
			return super.getAnnotations();
		}

		@Override
		public void addElementAsAnnotation(org.w3c.dom.Element element) {
			super.addElementAsAnnotation(element);
		}

		@Override
		public void deleteAnnotations() {
			super.deleteAnnotations();
		}
	}

	/**
	 * Corresponds to the &lt;properties&gt; constraint type in the MarkLogic
	 * Search API
	 * 
	 */
	@XmlRootElement(name = "properties")
	@XmlAccessorType(XmlAccessType.FIELD)
	public final static class QueryProperties extends
			BaseConstraintItem<QueryProperties> implements QueryConstraintItem {

		public QueryProperties() {
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryAnnotation implements QueryStateItem,
			QueryOptionsItem {

		@XmlAnyElement
		private List<org.w3c.dom.Element> annotations;

		public QueryAnnotation() {
			annotations = new ArrayList<org.w3c.dom.Element>();
		}

		public List<org.w3c.dom.Element> getAll() {
			return annotations;
		}

		public org.w3c.dom.Element get(int i) {
			return annotations.get(i);
		}

		public void add(org.w3c.dom.Element value) {
			this.annotations.add(value);
		}

		@Override
		public void build(QueryOptions options) {
			options.addAnnotation(this);
		}

	}

	@XmlRootElement(name = "range")
	@XmlAccessorType(XmlAccessType.FIELD)
	/**
	 * 
	 *
	 */
	public final static class QueryRange extends
			FacetableConstraintConfiguration<QueryRange> implements Indexable,
			QuerySuggestionSourceItem, QueryConstraintItem {

		/**
		 * Models a bucket on a range constraint whose values are anchored to
		 * time, and computed based on the current time.
		 * 
		 * @see com.marklogic.client.configpojos.Range
		 * 
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class QueryComputedBucket implements QueryRangeItem {

			/**
			 * Defines values for use in computed buckets anchored to time.
			 * 
			 * @see com.marklogic.client.configpojos.QueryComputedBucket
			 * 
			 */
			public static enum AnchorValue {

				NOW, START_OF_DAY, START_OF_MONTH, START_OF_YEAR;
				static AnchorValue fromXmlString(String xmlString) {
					return AnchorValue.valueOf(xmlString.toUpperCase().replace(
							"-", "_"));
				}

				String toXmlString() {
					return this.toString().toLowerCase().replace("_", "-");
				}

			}

			/**
			 * A value for anchoring this computed bucket.
			 * 
			 * @see com.marklogic.client.configpojos.AnchorValue
			 */
			@XmlAttribute(name = "anchor")
			private String anchor;

			/**
			 * The low end of the bucket's range. Stands for
			 * "greater than or equal to".
			 */
			@XmlAttribute
			private String ge;

			/**
			 * A value for anchoring the "greate than or equal" value for this
			 * computed bucket.
			 * 
			 * @see com.marklogic.client.configpojos.AnchorValue
			 */
			@XmlAttribute(name = "ge-anchor")
			private String geAnchor;

			/**
			 * The textual label for the bucket.
			 */
			@XmlValue
			private String label;

			/**
			 * The high end of the bucket's range. Stands for "less than"
			 */
			@XmlAttribute
			private String lt;

			/**
			 * A value for anchoring the "less than" value for this computed
			 * bucket.
			 * 
			 * @see com.marklogic.client.configpojos.AnchorValue
			 */
			@XmlAttribute(name = "lt-anchor")
			private String ltAnchor;

			/**
			 * A unique name to reference this bucket.
			 */
			@XmlAttribute
			private String name;

			public String getAnchor() {
				return anchor;
			}

			public AnchorValue getAnchorValue() {
				return AnchorValue.fromXmlString(anchor);
			}

			public String getGe() {
				return ge;
			}

			public String getGeAnchor() {
				return geAnchor;
			}

			public String getLabel() {
				return label;
			}

			public String getLt() {
				return lt;
			}

			public String getLtAnchor() {
				return ltAnchor;
			}

			public String getName() {
				return name;
			}

			public void setAnchor(AnchorValue anchorValue) {
				this.anchor = anchorValue.toXmlString();
			}

			public void setContent(String content) {
				this.label = content;
			}

			public void setGe(String ge) {
				this.ge = ge;
			}

			public void setLt(String lt) {
				this.lt = lt;
			}

			public void setName(String name) {
				this.name = name;
			}

		}

		/**
		 * Configures a range, for use in grouping range index values in facets.
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Bucket implements QueryRangeItem {

			/**
			 * The textual label for the bucket.
			 */
			@XmlValue
			private String content;
			/**
			 * The low end of the bucket's range. Stands for
			 * "greater than or equal to".
			 */
			@XmlAttribute
			private String ge;
			/**
			 * The high end of the bucket's range. Stands for "less than"
			 */
			@XmlAttribute
			private String lt;
			/**
			 * A unique name to reference this bucket.
			 */
			@XmlAttribute
			private String name;

			public String getContent() {
				return content;
			}

			public String getGe() {
				return ge;
			}

			public String getLt() {
				return lt;
			}

			public String getName() {
				return name;
			}

			public void setContent(String content) {
				this.content = content;
			}

			public void setGe(String ge) {
				this.ge = ge;
			}

			public void setLt(String lt) {
				this.lt = lt;
			}

			public void setName(String name) {
				this.name = name;
			}

		}

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "bucket")
		private List<Bucket> buckets;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "computed-bucket")
		private List<QueryComputedBucket> computedBuckets;

		public QueryRange() {
			buckets = new ArrayList<Bucket>();
			computedBuckets = new ArrayList<QueryComputedBucket>();
		}

		/**
		 * Add a bucket to this RangeOption's buckets.
		 * 
		 * @param bucket
		 *            a Bucket object for use with this RangeOption
		 */
		public void addBucket(Bucket bucket) {
			buckets.add(bucket);
		}

		public QueryRange addComputedBucket(QueryComputedBucket computedBucket) {
			this.computedBuckets.add(computedBucket);
			return this;
		}

		/**
		 * remove all computed and defined buckets from a RangeOption
		 */
		public void deleteBuckets() {
			this.computedBuckets = new ArrayList<QueryComputedBucket>();
			this.buckets = new ArrayList<Bucket>();
		}

		/**
		 * get the list of buckets for this RangeOption
		 * 
		 * @return
		 */
		public List<Bucket> getBuckets() {
			return buckets;
		}

		/**
		 * @return this RangeOptions's List of ComputedBuckets
		 */
		public List<QueryComputedBucket> getComputedBuckets() {
			return computedBuckets;
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QuerySortOrder extends BaseQueryOptionConfiguration
			implements Indexable, QueryStateItem, QueryOptionsItem,
			QueryAnnotations {

		public enum Direction implements QuerySortOrderItem {
			ASCENDING, DESCENDING;

			static Tokenize fromXMLString(String xmlString) {
				return Tokenize.valueOf(xmlString.toUpperCase().replace("-",
						"_"));
			}

			String toXMLString() {
				return this.toString().toLowerCase().replace("_", "-");
			}
		}

		public enum Score implements QuerySortOrderItem {
			YES
		};

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "attribute")
		private Attribute attributeReference;

		@XmlAttribute
		private String collation;

		@XmlAttribute
		private String direction;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "element")
		private Element elementReference;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "field")
		private Field fieldReference;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "score")
		private String score;

		@XmlAttribute
		private QName type;

		@Override
		public QName getAttribute() {
			return attributeReference.asQName();
		}

		@Override
		public QName getElement() {
			return elementReference.asQName();
		}

		@Override
		public String getFieldName() {
			return this.fieldReference.getName();
		}

		@Override
		public void setAttribute(Attribute attribute) {
			this.attributeReference = attribute;
		}

		@Override
		public List<QueryAnnotation> getAnnotations() {
			return super.getAnnotations();
		}

		@Override
		public void addElementAsAnnotation(org.w3c.dom.Element element) {
			super.addElementAsAnnotation(element);
		}

		@Override
		public void deleteAnnotations() {
			super.deleteAnnotations();
		}

		public void setCollation(String collation) {
			this.collation = collation;
		}

		public void setDirection(Direction direction) {
			this.direction = direction.toXMLString();
		}

		@Override
		public void setElement(Element element) {
			this.elementReference = element;
		}

		@Override
		public void setField(Field field) {
			this.fieldReference = field;
		}

		public void setScore() {
			score = "";
		}

		public void setType(QName type) {
			this.type = type;
		}

		@Override
		public void build(QueryOptions options) {
			options.getSortOrders().add(this);
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public final static class QueryState extends BaseQueryOptionConfiguration
			implements QueryAnnotations {

		@XmlAttribute(name = "name")
		private String name;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "sort-order")
		private List<QuerySortOrder> sortOrders;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "additional-query")
		private AnyElement additionalQuery;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "annotation")
		private List<QueryAnnotation> annotations;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "debug")
		private Boolean debug;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "forest")
		private List<Long> forests;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "page-length")
		private Long pageLength;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "quality-weight")
		private Double qualityWeight;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "search-option")
		private List<String> searchOptions;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "searchable-expression")
		private String searchableExpression;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "transform-results")
		private QueryTransformResults transformResultsOption;

		public QueryState() {
			sortOrders = new ArrayList<QuerySortOrder>();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<QuerySortOrder> getSortOrders() {
			return sortOrders;
		}

		public void addSortOrder(QuerySortOrder sortOrder) {
			sortOrders.add(sortOrder);
		}

		public void deleteSortOrders() {
			sortOrders = new ArrayList<QuerySortOrder>();
		}

		public org.w3c.dom.Element getAdditionalQuery() {
			return additionalQuery.getValue();
		}

		public void setAdditionalQuery(org.w3c.dom.Element element) {
			additionalQuery = new AnyElement(element);
		}

		public void addOption(QueryStateItem option) {
			if (option instanceof org.w3c.dom.Element) {
				setAdditionalQuery((org.w3c.dom.Element) option);
			} else if (option instanceof QuerySortOrder) {
				addSortOrder((QuerySortOrder) option);
			} else if (option instanceof QueryAnnotation) {
				getAnnotations().add((QueryAnnotation) option);
			} else {
			}
		}

		@Override
		public List<QueryAnnotation> getAnnotations() {
			return super.getAnnotations();
		}

		@Override
		public void addElementAsAnnotation(org.w3c.dom.Element element) {
			super.addElementAsAnnotation(element);
		}

		@Override
		public void deleteAnnotations() {
			super.deleteAnnotations();
		}
	}

	public interface QueryStateItem {

	}

	public static class SuggestionSourceOption extends TextOption<String> implements
			QuerySuggestionSourceItem {

		@Override
		public void build(QuerySuggestionSource suggestionSource) {
			suggestionSource.getSuggestionOptions().add(this.getValue());
		}

	}

	public abstract static class BaseQueryOptionConfiguration {

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "annotation", required = false)
		private List<QueryAnnotation> annotations;

		BaseQueryOptionConfiguration() {
			this.annotations = new ArrayList<QueryAnnotation>();
		}

		protected List<QueryAnnotation> getAnnotations() {
			return annotations;
		}

		protected void addElementAsAnnotation(org.w3c.dom.Element element) {
			QueryAnnotation annotation = new QueryAnnotation();
			annotation.add(element);
			annotations.add(annotation);
		}

		protected void deleteAnnotations() {
			annotations = new ArrayList<QueryAnnotation>();
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "suggestion-source")
	public static class QuerySuggestionSource extends
			BaseQueryOptionConfiguration implements QueryOptionsItem,
			QueryAnnotations {

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-options", required = false)
		private List<String> geoOptions;

		@XmlAttribute
		private String ref; // TODO in check options, validity of ref

		@XmlElement(name = "suggestion-option")
		private List<String> suggestionOptions;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "word-lexicon")
		private WordLexicon wordLexicon;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "collection")
		private QueryCollection collection;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "custom")
		private QueryCustom custom;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "element-query")
		private QueryElementQuery elementQuery;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-attr-pair")
		private GeoAttrPair geoAttrPair;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-elem")
		private GeoElement geoElem;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-elem-pair")
		private GeoElementPair geoElemPair;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "properties")
		private QueryProperties properties;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "range")
		private QueryRange range;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "value")
		private QueryValue value;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "word")
		private QueryWord word;

		public <T extends BaseConstraintItem> T getConstraintConfiguration() {
			if (collection != null) {
				return (T) collection;
			} else if (value != null) {
				return (T) value;
			} else if (range != null) {
				return (T) range;
			} else if (word != null) {
				return (T) word;
			} else if (elementQuery != null) {
				return (T) elementQuery;
			} else if (properties != null) {
				return (T) properties;
			} else if (custom != null) {
				return (T) custom;
			} else if (geoElem != null) {
				return (T) geoElem;
			} else if (geoAttrPair != null) {
				return (T) geoAttrPair;
			} else if (geoElemPair != null) {
				return (T) geoElemPair;
			}
			return null;
		}

		public <T extends BaseConstraintItem> void setImplementation(
				T constraintDefinition) {
			if (constraintDefinition.getClass() == QueryCollection.class) {
				collection = (QueryCollection) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryValue.class) {
				value = (QueryValue) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryWord.class) {
				word = (QueryWord) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryRange.class) {
				range = (QueryRange) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryElementQuery.class) {
				elementQuery = (QueryElementQuery) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryProperties.class) {
				properties = (QueryProperties) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryCustom.class) {
				custom = (QueryCustom) constraintDefinition;
			} else if (constraintDefinition.getClass() == GeoElement.class) {
				geoElem = (GeoElement) constraintDefinition;
			} else if (constraintDefinition.getClass() == GeoAttrPair.class) {
				geoAttrPair = (GeoAttrPair) constraintDefinition;
			} else if (constraintDefinition.getClass() == GeoElementPair.class) {
				geoElemPair = (GeoElementPair) constraintDefinition;
			}
		}

		public QuerySuggestionSource() {
			this.suggestionOptions = new ArrayList<String>();
		}

		public void addSuggestionOption(String suggestionOption) {
			this.suggestionOptions.add(suggestionOption);
		}

		public void deleteSuggestionOptions() {
			suggestionOptions = new ArrayList<String>();
		}

		public List<String> getSuggestionOptions() {
			return suggestionOptions;
		}

		@Override
		public List<QueryAnnotation> getAnnotations() {
			return this.getAnnotations();
		}

		@Override
		public void addElementAsAnnotation(org.w3c.dom.Element element) {
			this.addElementAsAnnotation(element);
		}

		@Override
		public void deleteAnnotations() {
			this.deleteAnnotations();
		}

		public void setWordLexicon(WordLexicon wordLexicon) {
			this.wordLexicon = wordLexicon;
		}

		public WordLexicon getWordLexicon() {
			return wordLexicon;
		}

		@Override
		public void build(QueryOptions options) {
			options.getSuggestionSources().add(this);
		}

	}

	/**
	 * Term defines how default search terms (those without without a
	 * corresponding constraint) are handled by the Search API
	 * 
	 * @see com.marklogic.client.configpojos.QueryConstraint
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryTerm<T extends BaseConstraintItem> implements
			QueryOptionsItem {

		public enum TermApply implements QueryTermItem {

			ALL_RESULTS, NO_RESULTS;

			public static TermApply fromXmlString(String xmlString) {
				return TermApply.valueOf(xmlString.toUpperCase().replace("-",
						"_"));
			}

			public String toXmlString() {
				return this.toString().toLowerCase().replace("_", "-");
			}

		}

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "default")
		// TODO formally is QueryWord|QueryValue|QueryRange
		private QueryConstraintConfigurationBag defaultConstraint;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "empty")
		private XQueryExtension empty;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "term-option")
		private List<String> termOptions;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "weight")
		private double weight;

		private XQueryExtension xQueryExtension;

		public QueryTerm() {
			this.defaultConstraint = new QueryConstraintConfigurationBag();
		}

		public T getConstraintConfiguration() {
			return defaultConstraint.getConstraintConfiguration();
		}

		public void setConstraintItem(T constraintConfiguration) {
			this.defaultConstraint.setImplementation(constraintConfiguration);
		}

		public TermApply getEmptyApply() {
			return TermApply.fromXmlString(empty.getApply());
		}

		public XQueryExtension getTermFunction() {
			return xQueryExtension;
		}

		public List<String> getTermOptions() {
			return termOptions;
		}

		public void setEmptyApply(TermApply termApply) {
			empty = new XQueryExtension();
			empty.setApply(termApply.toXmlString());
		}

		@Override
		public void build(QueryOptions options) {
			options.setTerm(this);
		}

		// TODO annotations
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	// TODO examine schema for this -- not sufficient for all the use cases we
	// have.
	public static class QueryTransformResults implements ExtensionPoint,
			QueryOptionsItem {

		@XmlAttribute
		private String apply;
		@XmlAttribute
		private String at;
		@XmlAnyElement
		private List<Element> children;
		@XmlAttribute
		private String ns;

		public QueryTransformResults() {

		}

		public String getApply() {
			return apply;
		}

		public String getAt() {
			return at;
		}

		public String getNs() {
			return ns;
		}

		public void setApply(String apply) {
			this.apply = apply;
		}

		public void setAt(String at) {
			this.at = at;
		}

		public void setNs(String ns) {
			this.ns = ns;
		}

		@Override
		public void build(QueryOptions options) {
			options.setTransformResults(this);
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "value")
	public final static class QueryValue extends BaseConstraintItem<QueryValue>
			implements Indexable, QueryConstraintItem {

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class WordLexicon implements QuerySuggestionSourceItem {

		@XmlAttribute
		private String collation;

		@XmlElement(name = "fragment-scope")
		private FragmentScope fragmentScope;

		public String getCollation() {
			return collation;
		}

		public void setCollation(String collation) {
			this.collation = collation;
		}

		public FragmentScope getFragmentScope() {
			return fragmentScope;
		}

		public void setFragmentScope(FragmentScope fragmentScope) {
			this.fragmentScope = fragmentScope;
		}

		@Override
		public void build(QuerySuggestionSource suggestionSource) {
			suggestionSource.setWordLexicon(this);
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "word")
	public final static class QueryWord extends BaseConstraintItem<QueryWord>
			implements Indexable, QueryTermItem, QueryValueItem,
			QueryRangeItem, QueryConstraintItem {

	}

	/**
	 * Models elements that locate XQuery functions with use of "ns", "apply"
	 * and "at" attributes.
	 * 
	 * @see com.marklogic.client.configpojos.QueryCustom
	 * @see com.marklogic.client.configpojos.TermConfig
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class XQueryExtension implements ExtensionPoint {

		/**
		 * Denotes a function within the XQuery module specified by "ns" and
		 * "at"
		 */
		@XmlAttribute
		private String apply;
		/**
		 * Where to find the XQuery module on the filesystem.
		 */
		@XmlAttribute
		private String at;
		/**
		 * The namespace URI of an XQuery module.
		 */
		@XmlAttribute
		private String ns;

		public String getApply() {
			return apply;
		}

		public String getAt() {
			return at;
		}

		public String getNs() {
			return ns;
		}

		public void setApply(String apply) {
			this.apply = apply;
		}

		public void setAt(String at) {
			this.at = at;
		}

		public void setNs(String ns) {
			this.ns = ns;
		}

	}

	private abstract static class TextOption<T extends Object> {

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

	static final private Logger logger = LoggerFactory
			.getLogger(QueryOptionsBuilder.class);

	private static DocumentBuilderFactory factory;

	public QueryAnnotation annotation(String xmlString) {
		QueryAnnotation annotation = new QueryAnnotation();
		annotation.add(domElement(xmlString));
		return annotation;
	}

	public Attribute attribute(String name) {
		return new Attribute("", name);
	}

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
	 * @return a new bucket.
	 */
	public Bucket bucket(String name, String label, String ge, String lt) {
		Bucket bucket = new Bucket();
		bucket.setName(name);
		bucket.setContent(label);
		bucket.setGe(ge);
		bucket.setLt(lt);
		return bucket;
	}

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

	@SuppressWarnings("rawtypes")
	public QueryConstraint constraint(String name,
			BaseConstraintItem constraintPart) {
		QueryConstraint constraintOption = new QueryConstraint(name);
		constraintOption.setImplementation(constraintPart);
		return constraintOption;
	}

	/**
	 * Build a queryCustom constraint with facet option. Needs three xquery
	 * functions to implement at facet: parse, start-facet, and finish-facet
	 */
	public QueryCustom customFacet(ExtensionPoint parseFunction,
			ExtensionPoint startFacet, ExtensionPoint finishFacet) {

		XQueryExtension parse = new XQueryExtension();
		XQueryExtension start = new XQueryExtension();
		XQueryExtension finish = new XQueryExtension();

		parse.setApply(parseFunction.getApply());
		start.setApply(startFacet.getApply());
		finish.setApply(finishFacet.getApply());

		parse.setNs(parseFunction.getNs());
		start.setNs(startFacet.getNs());
		finish.setNs(finishFacet.getNs());

		parse.setAt(parseFunction.getAt());
		start.setAt(startFacet.getAt());
		finish.setAt(finishFacet.getAt());
		return new QueryCustom(true, parse, start, finish);

	}

	/**
	 * Add a queryCustom constraint option without a facet. A queryCustom
	 * constraint without facets needs one XQuery function, to parse the
	 * constraint.
	 */
	public QueryCustom customParse(ExtensionPoint extension) {
		XQueryExtension parse = new XQueryExtension();
		parse.setApply(extension.getApply());
		parse.setNs(extension.getNs());
		parse.setAt(extension.getAt());
		return new QueryCustom(false, parse);
	}

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
	 * Construct a dom Element from a string.
	 * 
	 * @param string
	 *            XML for an element.
	 * @return w3c.dom.Element representation of this XML String.
	 */
	public  org.w3c.dom.Element domElement(String xmlString) {
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

	public Element element(String name) {
		return new Element(null, name);
	}

	public Element element(String ns, String name) {
		return new Element(ns, name);
	}

	/**
	 * 
	 */
	public TermApply empty(TermApply termApply) {
		return termApply;
	}

	public ExtensionPoint extension(String apply, String ns, String at) {
		XQueryExtension locator = new XQueryExtension();
		locator.setApply(apply);
		locator.setAt(at);
		locator.setNs(ns);
		return locator;
	}

	public Boolean facet(Boolean facetValue) {
		return facetValue;
	}

	public FacetOption facetOption(String facetOption) {
		FacetOption fo = new FacetOption();
		fo.setValue(facetOption);
		return fo;
	}

	public Field field(String name) {
		return new Field(name);
	}

	public AnyElement additionalQuery(org.w3c.dom.Element element) {
		return new AnyElement(element);
	}

	/**
	 * Construct a new GrammarOption
	 * 
	 * @param quotation
	 *            optional string to qualify quotations in the search grammar.
	 * @param implicit
	 *            optional cts:element to wrap all searches implicitly.
	 * @param grammarItems
	 *            0..* QueryStarters and/or 0..* QueryJoiners to refine the
	 *            search grammar.
	 * @return
	 */
	public QueryGrammar grammar(String quotation, org.w3c.dom.Element implicit,
			GrammarItem... grammarItems) {
		QueryGrammar grammar = new QueryGrammar();
		grammar.setQuotation(quotation);
		grammar.setImplicit(implicit);
		for (GrammarItem grammarItem : grammarItems) {
			if (grammarItem instanceof QueryStarter) {
				QueryStarter starter = (QueryStarter) grammarItem;
				grammar.addStarter(starter);
			} else if (grammarItem instanceof QueryJoiner) {
				QueryJoiner joiner = (QueryJoiner) grammarItem;
				grammar.addJoiner(joiner);
			}
		}
		return grammar;
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

	public GrammarItem joiner(String joinerText, int strength,
			JoinerApply apply, Comparator comparator, Tokenize tokenize) {
		QueryJoiner joiner = joiner(joinerText, strength, apply, null,
				tokenize, null);
		joiner.setCompare(comparator);
		return joiner;
	}

	public GrammarItem joiner(String joinerText, int strength,
			JoinerApply apply, QName element, Tokenize token) {
		return joiner(joinerText, strength, apply, element, token, null);
	}

	public QueryJoiner joiner(String joinerText, int strength,
			JoinerApply apply, QName element, Tokenize tokenize, Integer consume) {
		QueryJoiner joiner = new QueryJoiner(joinerText);
		joiner.setStrength(strength);
		joiner.setApply(apply);
		joiner.setElement(element);
		if (tokenize != null) {
			joiner.setTokenize(tokenize);
		}
		;
		if (consume != null) {
			joiner.setConsume(consume);
		}
		;
		return joiner;
	}

	public QueryOperator operator(String name, QueryState... states) {
		QueryOperator operator = new QueryOperator();
		operator.setName(name);
		for (QueryState state : states) {
			operator.addState(state);
		}
		return operator;
	}

	public QueryOptions options(QueryOptionsItem... options) {
		QueryOptions queryOptions = new QueryOptions();
		for (QueryOptionsItem option : options) {
			option.build(queryOptions);
		}
		return queryOptions;
	}

	public QueryProperties properties() {
		return new QueryProperties();
	}

	public String quotation(String quotation) {
		return quotation;
	}

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
			if (option instanceof Bucket) {
				rangeOption.addBucket((Bucket) option);
			} else if (option instanceof IndexReference) {
				addIndexElement(rangeOption, (IndexReference) option);
			}
		}
		return rangeOption;
	}

	public QuerySortOrderItem score() {
		return QuerySortOrder.Score.YES;
	}

	public QueryOptionsTextItem<Boolean> returnFacets(Boolean returnFacets) {
		return new QueryOptionsTextItem<Boolean>("setReturnFacets",
				returnFacets);
	}

	public QueryOptionsTextItem<Boolean> returnMetrics(Boolean returnFacets) {
		return new QueryOptionsTextItem<Boolean>("setReturnMetrics",
				returnFacets);
	}

	public QueryOptionsTextItem<Boolean> returnResults(Boolean returnFacets) {
		return new QueryOptionsTextItem<Boolean>("setReturnResults",
				returnFacets);
	}

	public QueryOptionsTextItem<FragmentScope> fragmentScope(FragmentScope scope) {
		return new QueryOptionsTextItem<FragmentScope>("setFragmentScope",
				scope);
	}

	public QueryOptionsTextItem<Boolean> returnConstraints(
			Boolean returnConstraints) {
		return new QueryOptionsTextItem<Boolean>("setReturnConstraints",
				returnConstraints);
	}
	public QueryOptionsTextItem<Boolean> returnAggregates(
			Boolean returnAggregates) {
		return new QueryOptionsTextItem<Boolean>("setReturnAggregates",
				returnAggregates);
	}

	public QueryOptionsTextItem<Boolean> returnValues(
			Boolean returnValues) {
		return new QueryOptionsTextItem<Boolean>("setReturnValues",
				returnValues);
	}

	public QueryOptionsTextItem<Boolean> returnQtext(
			Boolean returnQtext) {
		return new QueryOptionsTextItem<Boolean>("setReturnQtext",
				returnQtext);
	}

	public QueryOptionsTextItem<Boolean> returnFrequencies(
			Boolean returnFrequencies) {
		return new QueryOptionsTextItem<Boolean>("setReturnFrequencies",
				returnFrequencies);
	}
	public QueryOptionsTextItem<Boolean> returnPlan(Boolean returnPlan) {
		return new QueryOptionsTextItem<Boolean>("setReturnPlan", returnPlan);
	}

	public QueryOptionsTextItem<Boolean> returnQuery(Boolean returnQuery) {
		return new QueryOptionsTextItem<Boolean>("setReturnQuery", returnQuery);
	}

	public QueryOptionsTextItem<Boolean> returnSimilar(Boolean returnSimilar) {
		return new QueryOptionsTextItem<Boolean>("setReturnSimilar", returnSimilar);
	}

	public QueryOptionsTextItem<Boolean> debug(Boolean debug) {
		return new QueryOptionsTextItem<Boolean>("setDebug", debug);
	}

	public QueryOptionsTextItem<String> searchableExpression(
			String searchableExpression) {
		return new QueryOptionsTextItem<String>("setSearchableExpression",
				searchableExpression);
	}

	public QueryOptionsTextItem<Integer> concurrencyLevel(
			Integer concurrencyLevel) {
		return new QueryOptionsTextItem<Integer>("setConcurrencyLevel",
				concurrencyLevel);
	}

	public QueryOptionsTextItem<String> searchOption(
			String searchOption) {
		return new QueryOptionsTextItem<String>("addSearchOption",
				searchOption);
	}

	public QueryOptionsTextItem<Long> forest(
			Long forest) {
		return new QueryOptionsTextItem<Long>("addForest",
				forest);
	}

	public QueryOptionsTextItem<Long> pageLength(
			Long pageLength) {
		return new QueryOptionsTextItem<Long>("setPageLength",
				pageLength);
	}

	public QueryOptionsTextItem<Double> qualityWeight(
			Double qualityWeight) {
		return new QueryOptionsTextItem<Double>("setQualityWeight",
				qualityWeight);
	}

	private static class QueryOptionsTextItem<T extends Object> implements
			QueryOptionsItem {

		private T state;
		private String methodName;

		public QueryOptionsTextItem(String methodName, T value) {
			this.state = (T) value;
			this.methodName = methodName;
		}

		@Override
		public void build(QueryOptions options) {
			try {
				Method method = options.getClass().getMethod(this.methodName,
						state.getClass());
				method.invoke(options, this.state);
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

	public QuerySortOrder sortOrder(String type, String collation,
			QuerySortOrderItem... options) {
		QuerySortOrder so = new QuerySortOrder();
		so.setType(new QName(type));
		so.setCollation(collation);

		for (QuerySortOrderItem option : options) {
			if (option instanceof IndexReference) {
				addIndexElement(so, (IndexReference) option);
			} else if (option instanceof QuerySortOrder.Score) {
				so.setScore();
			} else if (option instanceof Direction) {
				so.setDirection((Direction) option);
			}
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

	public QueryState state(String stateName, QueryStateItem... options) {
		QueryState state = new QueryState();
		state.setName(stateName);
		for (QueryStateItem option : options) {
			state.addOption(option);
		}
		return state;

	}

	public SuggestionSourceOption suggestionSource(String suggestionOption) {
		SuggestionSourceOption so = new SuggestionSourceOption();
		so.setValue(suggestionOption);
		return so;
	}

	/**
	 * Build a SuggestionSource from one or more classes that implement
	 * SuggestSourceOption
	 * 
	 * @param options
	 * @return
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
	 * Build a term configuration object.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
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

	public QName type(String type) {
		return new QName(type);
	}

	public QueryTransformResults transformResults(String apply) {
		QueryTransformResults t = new QueryTransformResults();
		t.setApply(apply);
		return t;
	}

	public QueryTransformResults transformResultsOption(ExtensionPoint extension) {
		QueryTransformResults t = new QueryTransformResults();
		t.setApply(extension.getApply());
		t.setNs(extension.getNs());
		t.setAt(extension.getAt());
		return t;
	}

	public QueryValue value(QueryValueItem... valueOptions) {
		QueryValue vo = new QueryValue();
		for (QueryValueItem option : valueOptions) {
			if (option instanceof IndexReference) {
				addIndexElement(vo, (IndexReference) option);
			}
		}
		// TODO finish
		return vo;
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

	public QueryWord word(QueryWordItem... wordOptions) {
		QueryWord wo = new QueryWord();
		for (QueryWordItem option : wordOptions) {
			if (option instanceof IndexReference) {
				addIndexElement(wo, (IndexReference) option);
			}
		}
		// TODO finish
		return wo;
	}

	private void addIndexElement(Indexable indexable, IndexReference ref) {
		if (ref instanceof Field) {
			indexable.setField((Field) ref);
		} else if (ref instanceof Element) {
			indexable.setElement((Element) ref);
		} else if (ref instanceof Attribute) {
			indexable.setAttribute((Attribute) ref);
		}
	}

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

public interface GrammarItem {

}

public interface IndexReference extends QueryWordItem, QueryRangeItem, QueryValueItem, QuerySortOrderItem {


}
public interface Indexable {

	/**
	 * Add a reference to an element to this ConstraintBase
	 */
	public void setElement(Element element);

	public void setAttribute(Attribute attribute);

	public QName getAttribute();

	public QName getElement();

	public String getFieldName();

	public void setField(Field field);
}
public interface QueryAnnotations {


	public List<QueryAnnotation> getAnnotations();
    public void addElementAsAnnotation(org.w3c.dom.Element element);
    public void deleteAnnotations();

}

public interface QueryConstraintItem extends QueryTermItem {

}
public interface QueryOptionsItem {

	public void build(QueryOptions options);
}
public interface QueryRangeItem {

}
public interface QuerySortOrderItem {

}
public interface QueryWordItem {

}
public interface QueryValueItem {

}
public interface QueryTermItem {

}
public interface QuerySuggestionSourceItem {

	void build(QuerySuggestionSource suggestionSource);

}
}
