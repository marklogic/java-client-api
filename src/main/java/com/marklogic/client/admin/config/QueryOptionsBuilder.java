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
package com.marklogic.client.admin.config;

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import com.marklogic.client.admin.config.support.GeospatialSpec;
import com.marklogic.client.impl.RangeSpecImpl;
import org.w3c.dom.Element;

import com.marklogic.client.admin.config.QueryOptions.Aggregate;
import com.marklogic.client.admin.config.QueryOptions.AttributeValue;
import com.marklogic.client.admin.config.QueryOptions.ConstraintValue;
import com.marklogic.client.admin.config.QueryOptions.ElementValue;
import com.marklogic.client.admin.config.QueryOptions.ExpressionNamespaceBinding;
import com.marklogic.client.admin.config.QueryOptions.ExpressionNamespaceBindings;
import com.marklogic.client.admin.config.QueryOptions.Facets;
import com.marklogic.client.admin.config.QueryOptions.Field;
import com.marklogic.client.admin.config.QueryOptions.FragmentScope;
import com.marklogic.client.admin.config.QueryOptions.Heatmap;
import com.marklogic.client.admin.config.QueryOptions.JsonKey;
import com.marklogic.client.admin.config.QueryOptions.MarkLogicQName;
import com.marklogic.client.admin.config.QueryOptions.PathIndex;
import com.marklogic.client.admin.config.QueryOptions.QueryAdditionalQuery;
import com.marklogic.client.admin.config.QueryOptions.QueryCollection;
import com.marklogic.client.admin.config.QueryOptions.QueryConstraint;
import com.marklogic.client.admin.config.QueryOptions.QueryCustom;
import com.marklogic.client.admin.config.QueryOptions.QueryElementQuery;
import com.marklogic.client.admin.config.QueryOptions.QueryExtractMetadata;
import com.marklogic.client.admin.config.QueryOptions.QueryGeospatial;
import com.marklogic.client.admin.config.QueryOptions.QueryGeospatialAttributePair;
import com.marklogic.client.admin.config.QueryOptions.QueryGeospatialElement;
import com.marklogic.client.admin.config.QueryOptions.QueryGeospatialElementPair;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar.QueryJoiner;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar.QueryJoiner.Comparator;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar.QueryJoiner.JoinerApply;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar.QueryStarter;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar.QueryStarter.StarterApply;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar.Tokenize;
import com.marklogic.client.admin.config.QueryOptions.QueryOperator;
import com.marklogic.client.admin.config.QueryOptions.QueryProperties;
import com.marklogic.client.admin.config.QueryOptions.QueryRange;
import com.marklogic.client.admin.config.QueryOptions.QueryRange.Bucket;
import com.marklogic.client.admin.config.QueryOptions.QueryRange.ComputedBucket;
import com.marklogic.client.admin.config.QueryOptions.QueryRange.ComputedBucket.AnchorValue;
import com.marklogic.client.admin.config.QueryOptions.QuerySearchableExpression;
import com.marklogic.client.admin.config.QueryOptions.QuerySortOrder;
import com.marklogic.client.admin.config.QueryOptions.QuerySortOrder.Direction;
import com.marklogic.client.admin.config.QueryOptions.QueryState;
import com.marklogic.client.admin.config.QueryOptions.QueryTerm;
import com.marklogic.client.admin.config.QueryOptions.QueryTerm.TermApply;
import com.marklogic.client.admin.config.QueryOptions.QueryTransformResults;
import com.marklogic.client.admin.config.QueryOptions.QueryTuples;
import com.marklogic.client.admin.config.QueryOptions.QueryValue;
import com.marklogic.client.admin.config.QueryOptions.QueryValues;
import com.marklogic.client.admin.config.QueryOptions.QueryWord;
import com.marklogic.client.admin.config.QueryOptions.XQueryExtension;
import com.marklogic.client.admin.config.support.Buckets;
import com.marklogic.client.admin.config.support.ConstraintSource;
import com.marklogic.client.admin.config.support.GeospatialIndexType;
import com.marklogic.client.impl.GeospatialSpecImpl;
import com.marklogic.client.admin.config.support.HeatmapAndOptions;
import com.marklogic.client.admin.config.support.IndexSpecImpl;
import com.marklogic.client.admin.config.support.MetadataExtract;
import com.marklogic.client.admin.config.support.QueryOptionsConfiguration;
import com.marklogic.client.admin.config.support.QueryUri;
import com.marklogic.client.admin.config.support.RangeIndexType;
import com.marklogic.client.admin.config.support.RangeSpec;
import com.marklogic.client.admin.config.support.TermSource;
import com.marklogic.client.admin.config.support.TermSpec;
import com.marklogic.client.admin.config.support.TupleSource;
import com.marklogic.client.impl.Utilities;

/**
 * @deprecated Use a JSON or XML 
 * {@link com.marklogic.client.io.marker.StructureWriteHandle write handle} or
 * {@link com.marklogic.client.io.marker.StructureReadHandle read handle}
 * implementation instead of this class to write or read
 * query options.  For instance:
 * <pre>{@code
 *  String opts = new StringBuilder()
 *       .append("<options xmlns=\"http://marklogic.com/appservices/search\">")
 *       .append(    "<debug>true</debug>")
 *       .append("</options>")
 *       .toString();
 *  optsMgr.writeOptions("debug", new StringHandle(opts)); }</pre>
 * or
 * <pre>{@code
 *  String opts = "{\"options\":{\"debug\":true}}";
 *  optsMgr.writeOptions("debug", new StringHandle(opts).withFormat(Format.JSON)); }</pre>
 */
@Deprecated
public class QueryOptionsBuilder {

	/**
	 * Create a new QueryOptionsConfiguration object, which can be used to
	 * fluently set various configuration options.
	 * 
	 * @return A new empty QueryOptionsConfiguration
	 */
	public QueryOptionsConfiguration configure() {
		return new QueryOptionsConfiguration();
	}

	/**
	 * Builds a constraint object with the supplied name and source.
	 * 
	 * @param name
	 *            Name of the constraint. Name is used as a reference from other
	 *            configuration options, and also within search strings provided
	 *            by the end user.
	 * @param source
	 *            Source of data for the constraint.
	 * @return A QueryConstraint object for use in 
     * building {@link com.marklogic.client.admin.config.QueryOptions}
	 *         configurations
	 */
	public QueryConstraint constraint(String name, ConstraintSource source) {
		QueryConstraint constraint = new QueryConstraint(name);
		constraint.setSource(source);
		return constraint;
	}

	/**
	 * Builds a QueryValues object. QueryValues are used to specify lists of
	 * values from various lexical sources on the MarkLogic server.
	 * 
	 * @param name
	 *            Name for the QueryValues object. Used as a reference in URL
	 *            patterns.
	 * @param source
	 *            Specifies source of data for values. Built from either a
	 *            collection or uri lexicon, a range index, field, or geospatial
	 *            index.
	 * @param valuesOptions
	 *            A list of string options to values calls.
	 * @return A QueryValues object for use in values() or tuples() builder method.
	 */
	public QueryValues values(String name, TupleSource source,
			String... valuesOptions) {
		return values(name, source, null, valuesOptions);
	}

	/**
	 * Builds a QueryValues object, with an included aggregate specification.
	 * QueryValues are used to specify lists of values from various lexical
	 * sources on the MarkLogic server.
	 * 
	 * @param name
	 *            Name for the QueryValues object. Used as a reference in URL
	 *            patterns.
	 * @param source
	 *            Specifies source of data for values. Built from either a
	 *            collection or uri lexicon, a range index, field, or geospatial
	 *            index.
	 * @param aggregate
	 *            A reference to either a built-in aggregate function or a
	 *            user-defined function (UDF).
	 * @param valuesOptions
	 *            A list of string options to values calls.
	 * @return A QueryValues object for use in values() or tuples() builder method.
	 */
	public QueryValues values(String name, TupleSource source,
			Aggregate aggregate, String... valuesOptions) {
		QueryValues v = new QueryValues();
		v.setName(name);
		v.setAggregate(aggregate);
		source.build(v);
		v.setValuesOptions(Arrays.asList(valuesOptions));
		return v;
	}

	/**
	 * Builds a QueryTuples object. QueryTuples are used to specify lists of
	 * co-occurring values from lexicon sources on the MarkLogic server.
	 * 
	 * @param name
	 *            Name for the QueryTuples object. Used as a reference in URL
	 *            patterns.
	 * @param sources
	 *            Specifies sources of data for tuples. Built from a combination
	 *            of two or more collection or uri lexicons, range indexes,
	 *            fields, or geospatial indexes.
	 * @param valuesOptions
	 *            A list of string options to values calls.
	 * @return A QueryTuples object for use in values() or tuples() builder method.
	 */
	public QueryTuples tuples(String name, List<TupleSource> sources,
			String... valuesOptions) {
		return tuples(name, sources, null, valuesOptions);
	}

	/**
	 * Builds a QueryTuples object. QueryTuples are used to specify lists of
	 * co-occurring values from lexicon sources on the MarkLogic server.
	 * 
	 * @param name
	 *            Name for the QueryTuples object. Used as a reference in URL
	 *            patterns.
	 * @param sources
	 *            Specifies sources of data for tuples. Built from a combination
	 *            of two or more collection or uri lexicons, range indexes,
	 *            fields, or geospatial indexes.
	 * @param aggregate
	 *            A reference to either a built-in aggregate function or a
	 *            user-defined function (UDF).
	 * @param valuesOptions
	 *            A list of string options to values calls.
	 * @return A QueryTuples object for use in values() or tuples() builder method.
	 */
	public QueryTuples tuples(String name, List<TupleSource> sources,
			Aggregate aggregate, String... valuesOptions) {
		QueryTuples tuples = new QueryTuples();
		tuples.setName(name);
		tuples.setAggregate(aggregate);
		for (TupleSource t : sources) {
			t.build(tuples);
		}
		return tuples;
	}

	/**
	 * Builds an operator for use in a QueryOptions configuration.
	 * 
	 * @param name
	 *            Name of the operator. Used in search strings by end-users.
	 * @param states
	 *            A list of states applied when using this operator. Composed
	 *            with the state builder methods.
	 * @return a QueryOperator object for use in the operators builder method.
	 */
	public QueryOperator operator(String name, QueryState... states) {
		QueryOperator operator = new QueryOperator();
		operator.setName(name);
		for (QueryState state : states) {
			operator.addState(state);
		}
		return operator;
	}

	/**
	 * Builds a list of sort orders.
	 * 
	 * @param sortOrders 0 or more QuerySortOrder objects
	 * @return a List of QuerySortOrder objects for use in stateFeatures method.
	 */
	public List<QuerySortOrder> sortOrders(QuerySortOrder... sortOrders) {
		return Arrays.asList(sortOrders);
	}

	
	/**
	 * Builds a QuerySortOrder configuration from a TermSpec.  This method uses no types, and the default sort order is ascending.  Use the three-argument method to specify type and direction as needed.
	 * @param indexSpec Contains data source for the sort order.
	 * @return A QuerySortOrder object for use in the sortOrders(QuerySortOrder...) method.
	 */
	public QuerySortOrder sortOrder(TermSpec indexSpec) {
		QuerySortOrder sortOrder = new QuerySortOrder();
		indexSpec.build(sortOrder);
		return sortOrder;
	}

	/**
	 * Builds a QuerySortOrder configuration from a RangeSpec, and a Direction.
	 * RangeSpec contains typing and collation information for sorting.
	 * 
	 * @param indexSpec
	 *            Contains data source for the sort order as well as typing and
	 *            collation information as appropriate.
	 * @return A QuerySortOrder object for use in the
	 *         sortOrders(QuerySortOrder...) method.
	 */
	public QuerySortOrder sortOrder(RangeSpec indexSpec, Direction direction) {
		QuerySortOrder sortOrder = new QuerySortOrder();
		indexSpec.build(sortOrder);
		sortOrder.setType(indexSpec.getType());
		sortOrder.setCollation(indexSpec.getCollation());
		sortOrder.setDirection(direction);
		return sortOrder;
	}

	/**
	 * Builds a QuerySortOrder for sorting by search relevance score, and a
	 * Direction.
	 * 
	 * @param direction
	 *            Either ascending or descending.
	 * @return A QuerySortOrder object for use in the
	 *         sortOrders(QuerySortOrder...) method.
	 */
	public QuerySortOrder sortByScore(Direction direction) {
		QuerySortOrder sortOrder = new QuerySortOrder();
		sortOrder.setScore();
		sortOrder.setDirection(direction);
		return sortOrder;
	}

	/**
	 * incorporates a cts:query expression into the search.
	 * 
	 * @param ctsQuery
	 *            A MarkLogic cts:query element, as a string, to be ANDed with
	 *            the runtime search options.  The string must be valid XML in the 
	 *            http://marklogic.com/cts namespace.
	 * @return A QueryAdditionalQuery object to be used in QueryOptions or
	 *         QueryState composition.
	 */
	public QueryAdditionalQuery additionalQuery(String ctsQuery) {
		return new QueryAdditionalQuery(Utilities.domElement(ctsQuery));
	}

	/**
	 * Defines an XPath expression to be used as the context for all searches.
	 * 
	 * @param searchableExpression
	 *            XPath expression to locate nodes in the search database.
	 * @param bindings
	 *            An array of bindings of prefix to namespace uri. Used to
	 *            evaluate prefixes in the searchableExpression XPath string.
	 * @return A QuerySearchableExpression object. Include in options using
	 *         withSearchableExpression
	 */
	public QuerySearchableExpression searchableExpression(
			String searchableExpression, ExpressionNamespaceBinding... bindings) {
		return new QueryOptions.QuerySearchableExpression(searchableExpression,
				bindings);
	}

	/**
	 * Builds a configuration for extracting metadata from search results.
	 * 
	 * @param extractions
	 *            Zero or more metadata specifictions to include in results
	 * @return An object to be included in a {@link com.marklogic.client.admin.config.QueryOptions} configuration.
	 */
	public QueryExtractMetadata extractMetadata(MetadataExtract... extractions) {
		QueryExtractMetadata extractMetadata = new QueryExtractMetadata();
		for (MetadataExtract item : extractions) {
			item.build(extractMetadata);
		}
		return extractMetadata;
	}

	/**
	 * Configures search results to return raw XML documents
	 * 
	 * @return A configuration object to be included in {@link com.marklogic.client.admin.config.QueryOptions} with
	 *         withTransformResults
	 */
	public QueryTransformResults rawResults() {
		QueryTransformResults t = new QueryTransformResults();
		t.setApply("raw");
		return t;
	}

	/**
	 * Configures search results to return no result node. Search results do
	 * contain document paths and/or identifiers.
	 * 
	 * @return A configuration object to be included in {@link com.marklogic.client.admin.config.QueryOptions} with
	 *         withTransformResults
	 */
	public QueryTransformResults emptySnippets() {
		QueryTransformResults t = new QueryTransformResults();
		t.setApply("empty-snippet");
		return t;
	}

	/**
	 * Configures search results to include data from properties fragments.
	 * 
	 * @param preferredElements
	 *            Zero or more elements from the properties fragment to include.
	 *            If none are specified, last-modified is used.
	 * @return A configuration object to be included in {@link com.marklogic.client.admin.config.QueryOptions} with
	 *         withTransformResults
	 */
	public QueryTransformResults metadataSnippetTransform(
			QName... preferredElements) {
		QueryTransformResults t = new QueryTransformResults();
		t.setApply("metadata-snippet");
		for (QName element : preferredElements) {
			t.addPreferredElement(new MarkLogicQName(element.getNamespaceURI(),
					element.getLocalPart()));
		}
		return t;
	}

	/**
	 * Configures the way default snippets look in search results.
	 * 
	 * @param perMatchTokens
	 *            Maximum number of tokens (typically words) per matching node
	 *            that surround the highlighted term(s) in the snippet.
	 * @param maxMatches
	 *            The maximum number of nodes containing a highlighted term that
	 *            will display in the snippet.
	 * @param maxSnippetChars
	 *            Limit total snippet size to this many characters.
	 * @param preferredElements
	 *            Zero or more elements that the snippet algorithm looks in
	 *            first to find matches.
	 * @return A configuration object to be included in {@link com.marklogic.client.admin.config.QueryOptions} with
	 *         withTransformResults
	 */
	public QueryTransformResults snippetTransform(Integer perMatchTokens,
			Integer maxMatches, Integer maxSnippetChars,
			QName... preferredElements) {
		QueryTransformResults t = new QueryTransformResults();
		t.setApply("snippet");
		t.setPerMatchTokens(perMatchTokens);
		t.setMaxMatches(maxMatches);
		t.setMaxSnippetChars(maxSnippetChars);
		for (QName element : preferredElements) {
			t.addPreferredElement(new MarkLogicQName(element.getNamespaceURI(),
					element.getLocalPart()));
		}
		return t;
	}

	/**
	 * Transform results with a custom XQuery extension
	 * 
	 * @param extension
	 *            An XQuery extension, build with QueryOptionsBuilder.extension
	 * @return A configuration object to be included in {@link com.marklogic.client.admin.config.QueryOptions} with
	 *         withTransformResults
	 */
	public QueryTransformResults transformResults(XQueryExtension extension) {
		QueryTransformResults t = new QueryTransformResults();
		t.setApply(extension.getApply());
		t.setNs(extension.getNs());
		t.setAt(extension.getAt());
		return t;
	}

	/**
	 * Builds the definition of a query grammar. Use this builder to create
	 * modifications to how the server interprets the syntax of search strings.
	 * Since each option is independent of the other, each of the arguments to
	 * this function may be null.
	 * 
	 * @param starters
	 *            List of starter configurations, probably returned by the
	 *            starter builder method. Starters define how individual search
	 *            terms are interpreted.
	 * @param joiners
	 *            List of starter configurations, probably returned by the
	 *            joiner builder method. Joiners define how terms are grouped
	 *            together into an overall search expression.
	 * @param quotation
	 * @param implicit
	 * @return A QueryGrammar instance for use in composing 
     * {@link com.marklogic.client.admin.config.QueryOptions}
	 */
	public QueryGrammar grammar(List<QueryStarter> starters,
			List<QueryJoiner> joiners, String quotation, Element implicit) {
		QueryGrammar grammar = new QueryGrammar();
		grammar.setStarters(starters);
		grammar.setJoiners(joiners);
		grammar.setQuotation(quotation);
		grammar.setImplicit(implicit);
		return grammar;
	}

	/**
	 * Builds a term configuration with an extension.
	 * 
	 * @param extension
	 *            An XQuery module that generates terms (?)
	 * @return A QueryTerm instance to be included in query configurations using
	 *         {@link com.marklogic.client.io.QueryOptionsHandle}.withTerm()
	 */
	public QueryTerm term(XQueryExtension extension) {
		QueryTerm term = new QueryTerm();
		term.setTermFunction(extension);
		return term;
	}

	/**
	 * Builds a term configuration with an source for default searching.
	 * 
	 * @param empty
	 *            Specifies what to do with an empty search. An empty search can
	 *            mean either all results or no results.
	 * @param defaultSource
	 *            The source for searches with no named constraint applied.
	 * @param termOptions
	 *            Options that fine-tune behavior of a term configuration.
	 *            Valid term options are:  
	 *            <pre>
case-sensitive
diacritic-sensitive
diacritic-insensitive
punctuation-sensitive
punctuation-insensitive
whitespace-sensitive
whitespace-insensitive
stemmed
unstemmed
wildcarded
unwilcarded
exact
lang=iso639code</pre>
	 * @return A QueryTerm object to be included in query configurations using
	 *         QueryOptionsHandle.withTerm
	 */
	public QueryTerm term(TermApply empty, TermSource defaultSource,
			String... termOptions) {
		QueryTerm term = new QueryTerm();
		term.setEmptyApply(empty);
		term.setSource(defaultSource);
		term.setTermOptions(Arrays.asList(termOptions));
		return term;
	}

	/**
	 * Builds a term configuration with an source for default searching.
	 * 
	 * @param empty
	 *            Specifies what to do with an empty search. An empty search can
	 *            mean either all results or no results.
	 * @param defaultSourceName
	 *            The name of a constraint to use when no prefix is used for a
	 *            search term.
	 * @param termOptions
	 *            Options that fine-tune behavior of a term configuration.
	 * @return A QueryTerm object to be included in query configurations using
	 *         QueryOptionsHandle.withTerm
	 */
	public QueryTerm term(TermApply empty, String defaultSourceName,
			String... termOptions) {
		QueryTerm term = new QueryTerm();
		term.setEmptyApply(empty);
		term.setRef(defaultSourceName);
		term.setTermOptions(Arrays.asList(termOptions));
		return term;
	}
	
	/**
	 * Builds a term option that consists solely of valid term option strings.
	 * 
	 * @param termOptions
	 *            Options that fine-tune behavior of a term configuration.
	 * @return A QueryTerm object to be included in query configurations using
	 *         QueryOptionsHandle.withTerm
	 * 
	 */
	public QueryTerm term(String... termOptions) {
		QueryTerm term = new QueryTerm();
		term.setTermOptions(Arrays.asList(termOptions));
		return term;
	}
	

	/**
	 * Builds a new QueryProperties, which restricts a named constraint to data
	 * stored in the properties fragment.
	 * 
	 * @return a QueryProperties object for use in building a QueryConstraint
	 *         configuration.
	 */
	public ConstraintSource properties() {
		return new QueryProperties();
	}

	/**
	 * Builds a QueryCollection object to use the Collection URI lexicon as source of
	 * constraint values. This single-argument assumes that the server's collection
	 * lexicon is enabled, and that facets will be resolved in searches.
	 * 
	 * @param prefix
	 *            This value will be trimmed from the start of collection URIs
	 *            to provide more readable facet labels.
	 * 
	 * @return A QueryCollection object for use in building {@link com.marklogic.client.admin.config.QueryOptions}
	 *         configurations.
	 */
	public QueryCollection collection(String prefix) {
		return collection(prefix, null);
	}

	/**
	 * Builds a QueryCollection object to use the Collection URI lexicon as source of
	 * constraint values.
	 * 
	 * @param prefix
	 *            This value will be trimmed from the start of collection URIs
	 *            to provide more readable facet labels.
	 * @param isFaceted
	 *            Setting to true configures Search API to do facets on this
	 *            source.
	 * @param options
	 *            A list of facet options to configure the collection
	 *            constraint.  Valid facet options are:
	 *            <pre>
ascending
descending
empties
any
document
properties
locks
frequency-order
item-order
fragment-frequency
item-frequency
type=type
timezone=TZ
limit=N
sample=N
truncate=N
skip=N
score-logtfidf
score-logtf
score-simple
score-random
checked
unchecked
concurrent
map</pre>
	 * @return A QueryCollection object for use in building QueryOptions
	 *         configurations.
	 */
	public QueryCollection collection(String prefix, Facets isFaceted,
			String... options) {
		QueryCollection collection = new QueryCollection();
		if (isFaceted != null) {
			collection.doFacets(isFaceted == Facets.FACETED);
		}
		collection.setPrefix(prefix);
		for (String option : options) {
			collection.addFacetOption(option);
		}
		return collection;
	}

	/**
	 * Builds a QueryElementQuery object for use in constraining 
     * searches to a particular element.
	 * 
	 * @param qname
	 *            QName of the element for restricting QueryOptions.
	 * @return a QueryElementQuery object used for building a QueryConstraint
	 */
	public QueryElementQuery elementQuery(QName qname) {
		QueryElementQuery qeq = new QueryElementQuery();
		if (qname != null) {
			qeq.setNs(qname.getNamespaceURI());
			qeq.setName(qname.getLocalPart());
		}
		return qeq;
	}

	/**
	 * Builds a List of {@link com.marklogic.client.admin.config.support.TupleSource}s from 0..N Tuple Sources.
	 * 
	 * @param tupleSources
	 *            Zero to N TupleSources
	 * @return A List containing the {@link com.marklogic.client.admin.config.support.TupleSource} provided.
	 */
	public List<TupleSource> tupleSources(TupleSource... tupleSources) {
		return Arrays.asList(tupleSources);
	}

	/**
	 * Builds a range index specification for use in a constraint, value, tuple or term configuration.
	 * @param indexSpec Specification for this range index.  Built by one of the XRangeIndex builder methods.
	 * @return A QueryRange object for inclusion in constraint, term, tuple, or value methods.
	 */
	public QueryRange range(RangeSpec indexSpec) {
		return range(indexSpec, null, null, null);
	}

	/**
	 * Builds a range index specification for use in a constraint, values, tuples or term configuration.  This version is for faceted ranges.
	 * @param indexSpec Specification for this range index.  Built by one of the XRangeIndex builder methods.
	 * @param faceted A flag to determine whether this range is faceted or not.
	 * @param scope The optional fragment scope for this range.
	 * @param buckets a list of buckets or computed buckets for the facets
	 * @param facetOptions Zero or more options to tune the behavior of facets.
	 * @return A QueryRange object for inclusion in constraint, term, tuple, or value methods.
	 */
	
	public QueryRange range(RangeSpec indexSpec, Facets faceted,
			FragmentScope scope, List<Buckets> buckets, String... facetOptions) {
		QueryRange range = new QueryRange();
		indexSpec.build(range);
		if (faceted != null) {
			range.doFacets(faceted == Facets.FACETED);
		}
		range.setFragmentScope(scope);
		range.addBuckets(buckets);
		for (String option : facetOptions) {
			range.addFacetOption(option);
		}
		return range;
	}

	/**
	 * Builds a value index specification for use in a constraint, values, tuples or term configuration.
	 * @param indexSpec Specification for this range index.  Built by one of the XRangeIndex builder methods.
	 * @return A QueryRange object for inclusion in constraint, term, tuple, or value methods.
	 */
	public ConstraintSource value(TermSpec indexSpec) {
		return value(indexSpec, null, null);
	}

	/**
	 * Builds a value index specification for use in a constraint, values, tuples or term configuration.
	 * 
	 * Extra arguments provide refinement of the use of the terms.
	 * @param indexSpec Specification for this range index.  Built by one of the XRangeIndex builder methods.
	 * @param weight A decimal value to weight this index relative to others in the search results.
	 * @param scope The optional fragment scope for this range.
	 * @param termOptions Zero or more options to tune the behavior of facets.
	 * @return A QueryRange object for inclusion in constraint, term, tuple, or value methods.
	 */
	public ConstraintSource value(TermSpec indexSpec, Double weight,
			FragmentScope scope, String... termOptions) {
		QueryValue value = new QueryValue();
		indexSpec.build(value);
		value.setWeight(weight);
		value.setFragmentScope(scope);
		value.setTermOptions(Arrays.asList(termOptions));
		return value;
	}

	/**
	 * Builds a word index specification for use in a constraint or term configuration.
	 * @param indexSpec The index backing the word configuration.
	 * @return A QueryWord object for use in a constraint or term builder method.
	 */
	public QueryWord word(TermSpec indexSpec) {
		return word(indexSpec, null, null);
	}

	/**
	 * Builds a word index specification for use in a constraint or term configuration.
	 * 
	 * Extra arguments provide refinement of the use of the terms.
	 * @param indexSpec Specification for this range index.  Built by one of the XRangeIndex builder methods.
	 * @param weight A decimal value to weight this index relative to others in the search results.
	 * @param scope The optional fragment scope for this range.
	 * @param options Zero or more options to tune the behavior of facets.
	 * @return A QueryRange object for inclusion in constraint, term, tuple, or value methods.
	 */
	public QueryWord word(TermSpec indexSpec, Double weight,
			FragmentScope scope, String... options) {
		QueryWord word = new QueryWord();
		indexSpec.build(word);
		word.setWeight(weight);
		word.setFragmentScope(scope);
		word.setTermOptions(Arrays.asList(options));
		return word;
	}

	/**
	 * Defines an XQuery customization for specifying constraint values.
	 * 
	 * @param parse
	 *            Represents a custom XQuery extension (installed by an
	 *            administrator) for parsing data into constraint values.
	 * @param termOptions
	 *            Zero or more options for modifying the behavior of the parsed
	 *            terms.
	 * @return A ConstraintSource object for inclusion within a QueryConstraint.
	 */
	public ConstraintSource customParse(XQueryExtension parse,
			String... termOptions) {
		QueryCustom custom = new QueryCustom(true);
		custom.setParse(parse);
		custom.setTermOptions(Arrays.asList(termOptions));
		return custom;
	}

	/**
	 * 
	 * Defines an XQuery customization for specifying constraint faceting
	 * behavior.
	 * 
	 * @param parse
	 *            An XQuery extension to parse the values provided.
	 * @param start
	 *            An XQuery extension to start categorizing facets.
	 * @param finish
	 *            An XQuery extension to finish categorizing facets.
	 * @param facetOptions
	 *            0 or more options to affect facet behavior.
	 * @return A ConstraintSource object for building QueryConstraints
	 */
	public ConstraintSource customFacet(XQueryExtension parse,
			XQueryExtension start, XQueryExtension finish,
			String... facetOptions) {
		QueryCustom custom = new QueryCustom(true);
		custom.setParse(parse);
		custom.setStartFacet(start);
		custom.setFinishFacet(finish);
		custom.setFacetOptions(Arrays.asList(facetOptions));
		return custom;
	}

	/**
	 * Builds a geospatial specification for a two-dimensional constraint.
	 * @param index The geospatial index backing this configuration.
	 * @param heatmap An optional heatmap for graphical display of results, with facet options bundled in.  Use heatmap builder method to provide this argument.
	 * @param geoOptions Zero or more options to fine-tune geographic behavior.
	 * @return An object for use in a constraint definition.
	 */
	public ConstraintSource geospatial(GeospatialSpec index,
			HeatmapAndOptions heatmap, String... geoOptions) {
		QueryGeospatial geospatial = null;
		if (index.getGeospatialIndexType() == GeospatialIndexType.ATTRIBUTE_PAIR) {
			geospatial = new QueryGeospatialAttributePair();
			geospatial.setLatitude(new MarkLogicQName(index.getLatitude()
					.getNamespaceURI(), index.getLatitude().getLocalPart()));
			geospatial.setLongitude(new MarkLogicQName(index.getLongitude()
					.getNamespaceURI(), index.getLongitude().getLocalPart()));
			geospatial.setParent(new MarkLogicQName(index.getParent()
					.getNamespaceURI(), index.getParent().getLocalPart()));
		} else if (index.getGeospatialIndexType() == GeospatialIndexType.ELEMENT_PAIR) {
			geospatial = new QueryGeospatialElementPair();
			geospatial.setLatitude(new MarkLogicQName(index.getLatitude()
					.getNamespaceURI(), index.getLatitude().getLocalPart()));
			geospatial.setLongitude(new MarkLogicQName(index.getLongitude()
					.getNamespaceURI(), index.getLongitude().getLocalPart()));
			geospatial.setParent(new MarkLogicQName(index.getParent()
					.getNamespaceURI(), index.getParent().getLocalPart()));
		} else if (index.getGeospatialIndexType() == GeospatialIndexType.ELEMENT) {
			geospatial = new QueryGeospatialElement();
			geospatial.setElement(new MarkLogicQName(index.getElement()
					.getNamespaceURI(), index.getElement().getLocalPart()));
		} else if (index.getGeospatialIndexType() == GeospatialIndexType.ELEMENT_CHILD) {
			geospatial = new QueryGeospatialElement();
			geospatial.setElement(new MarkLogicQName(index.getElement()
					.getNamespaceURI(), index.getElement().getLocalPart()));
			geospatial.setParent(new MarkLogicQName(index.getParent().getNamespaceURI(), index.getParent().getLocalPart()));
		}
		if (heatmap != null) {
			geospatial.setHeatmap(heatmap.getHeatmap());
		}
		if (geoOptions != null) {
			geospatial.setGeoOptions(Arrays.asList(geoOptions));
		}
		return geospatial;
	}

	/**
	 * Builds a geospatial specification for a two-dimensional constraint.
	 * @param index THe geospatial index definition for this constraint configuration.
	 * @param geoOptions Zero or more options to fine-tune the behavior of the geospatial constraint.
	 * @return An object for use in a constraint definition.
	 */
	public ConstraintSource geospatial(GeospatialSpec index,
			String... geoOptions) {
		return geospatial(index, null, geoOptions);
	}

	/**
	 * Builds an empty QueryState object with the provided name.
	 * 
	 * @param stateName
	 *            Describes how this state is exposed to the search string
	 *            interface.
	 * @return A QueryState object. Invoke some fluent setters from the new
	 *         object in order to have a meaningful state object.
	 */
	public QueryState state(String stateName) {
		QueryState state = new QueryState();
		state.setName(stateName);
		return state;
	}

	/**
	 * Builds an element index specification.
	 * 
	 * @param element
	 *            QName of an indexed element.
	 * @return A TermSpec object for use in constructing constraint and value
	 *         sources.
	 */
	public TermSpec elementTermIndex(QName element) {
		TermSpec index = new IndexSpecImpl();
		index.setElement(element);
		return index;
	}

	/**
	 * Builds an element range index specification.
	 * 
	 * @param element
	 *            QName of an indexed element.
	 * @param type
	 *            An object that encapsulates typing and collation information
	 *            for the range index.
	 * @return a RangeSpec object for use in building range index constraints.
	 */
	public RangeSpec elementRangeIndex(QName element, RangeIndexType type) {
		RangeSpec index = new RangeSpecImpl();
		index.setElement(element);
		index.setType(type.getType());
		index.setCollation(type.getCollation());
		return index;
	}

	
	/**
	 * Builds an element-attribute index specification.
	 *
	 * @param element QName of the attribute's parent element.
	 * @param attribute QName of the indexed attribute.
	 * @return A TermSpec object for use in constructing constraint and value sources.
	 */
	public TermSpec elementAttributeTermIndex(QName element, QName attribute) {
		TermSpec index = new IndexSpecImpl();
		index.setElement(element);
		index.setAttribute(attribute);
		return index;
	}

	/**
	 * Builds an element-attribute range index specification.
	 * 
	 * @param element
	 *            QName of an indexed element.
	 * @param attribute
	 *            QName of the indexed attribute.
	 * @param type
	 *            An object that encapsulates typing and collation information
	 *            for the range index.
	 * @return a RangeSpec object for use in building range index constraints.
	 */
	public RangeSpec elementAttributeRangeIndex(QName element, QName attribute,
			RangeIndexType type) {
		RangeSpec index = new RangeSpecImpl();
		index.setAttribute(attribute);
		index.setElement(element);
		index.setType(type.getType());
		index.setCollation(type.getCollation());
		return index;
	}

	/**
	 * Builds a field index specification.
	 * 
	 * @param fieldName
	 *            The name of an indexed field.
	 * @return A TermSpec object for use in constructing constraint and value
	 *         sources.
	 */
	public TermSpec fieldTermIndex(String fieldName) {
		TermSpec index = new IndexSpecImpl();
		index.setField(fieldName);
		return index;
	}

	/**
	 * Builds a field range index specification.
	 * 
	 * @param fieldName
	 *            Name of the indexed field
	 * @param type
	 *            An object that encapsulates typing and collation information
	 *            for the range index.
	 * @return a RangeSpec object for use in building range index constraints.
	 */
	public RangeSpec fieldRangeIndex(String fieldName, RangeIndexType type) {
		RangeSpec index = new RangeSpecImpl();
		index.setField(fieldName);
		index.setType(type.getType());
		index.setCollation(type.getCollation());
		return index;
	}

	/**
	 * Builds a JSON index specification.
	 * 
	 * @param jsonKey
	 *            The name of the indexed JSON key.
	 * @return A TermSpec object for use in constructing constraint and value
	 *         sources.
	 */
	public TermSpec jsonTermIndex(String jsonKey) {
		TermSpec index = new IndexSpecImpl();
		index.setJsonKey(jsonKey);
		return index;
	}

	/**
	 * Builds a jsonKey range index specification.
	 * 
	 * @param jsonKey
	 *            Name of the indexed key
	 * @param type
	 *            An object that encapsulates typing and collation information
	 *            for the range index.
	 * @return a RangeSpec object for use in building range index constraints.
	 */
	public RangeSpec jsonRangeIndex(String jsonKey, RangeIndexType type) {
		RangeSpec index = new RangeSpecImpl();
		index.setJsonKey(jsonKey);
		index.setType(type.getType());
		index.setCollation(type.getCollation());
		return index;
	}

	/**
	 * Builds a new PathIndex object. Use this method when type is not required.
	 * 
	 * @param xPath
	 *            XPath expression to locate nodes in the search database. Must
	 *            match path specification of a configured index on the server.
	 * @param bindings
	 *            An array of bindings of prefix to namespace uri. Used to
	 *            evaluate prefixes in the searchableExpression XPath string.
	 * @return a RangeSpec object for use in building range index constraints.
	 */
	public RangeSpec pathIndex(String xPath,
			ExpressionNamespaceBinding... bindings) {
		return pathIndex(xPath, null, null);
	}

	/**
	 * Builds a new PathIndex object.
	 * 
	 * @param xPath
	 *            XPath expression to locate nodes in the search database. Must
	 *            match path specification of a configured index on the server.
	 * @param bindings
	 *            An array of bindings of prefix to namespace uri. Used to
	 *            evaluate prefixes in the searchableExpression XPath string.
	 * @param type
	 *            An object that encapsulates typing and collation information
	 *            for the range index.
	 * @return a RangeSpec object for use in building range index constraints.
	 */
	public RangeSpec pathIndex(String xPath,
			ExpressionNamespaceBindings bindings, RangeIndexType type) {
		PathIndex index;
		if (bindings == null) {
			index = new PathIndex(xPath);
		} else {
			index = new PathIndex(xPath, bindings.toArray());
		}
		RangeSpecImpl impl = new RangeSpecImpl();
		impl.setPathIndex(index);
		if (type != null) {
			impl.setType(type.getType());
			impl.setCollation(type.getCollation());
		}
		return impl;
	}

	/**
	 * Builds a geospatial index from data where latitude and longitude are
	 * stored as XML attributes.
	 * 
	 * @param parent
	 *            QName of the element that is a parent to the latitude and
	 *            longitude attributes.
	 * @param latitudeAttribute
	 *            QName of the XML Attribute containing latitude.
	 * @param longitudeAttribute
	 *            QName of the XML Attribute constraint longitude.
	 * @return A GeospatialIndex object for use in constructing QueryConstraints
	 */
	public GeospatialSpec attributePairGeospatialIndex(QName parent,
			QName latitudeAttribute, QName longitudeAttribute) {
		GeospatialSpec index = new GeospatialSpecImpl();
		index.setLatitude(latitudeAttribute);
		index.setLongitude(longitudeAttribute);
		index.setParent(parent);
		index.setGeospatialIndexType(GeospatialIndexType.ATTRIBUTE_PAIR);
		return index;
	}

	/**
	 * Builds a geospatial index from data where latitude and longitude are
	 * stored as text in XML elements.
	 * 
	 * @param parent
	 *            QName of the element that is a parent to the latitude and
	 *            longitude elements.
	 * @param latitudeElement
	 *            QName of the XML Element containing latitude.
	 * @param longitudeElement
	 *            QName of the XML Element constraint longitude.
	 * @return A GeospatialIndex object for use in constructing QueryConstraints
	 */
	public GeospatialSpec elementPairGeospatialIndex(QName parent,
			QName latitudeElement, QName longitudeElement) {
		GeospatialSpec index = new GeospatialSpecImpl();
		index.setLatitude(latitudeElement);
		index.setLongitude(longitudeElement);
		index.setParent(parent);
		index.setGeospatialIndexType(GeospatialIndexType.ELEMENT_PAIR);
		return index;
	}

	/**
	 * Builds a geospatial index from data where latitude and longitude are
	 * stored together in one XML element.
	 * 
	 * @param geospatialElement
	 *            QName of the element that stores geographic coordinates. The
	 *            coordinates are comma-delimited. Default is that latitude
	 *            precedes longitude. If longitude precedes latitude in your
	 *            data, use a geo-option called "long-lat-points" on the
	 *            enclosing geospatial builder method.
	 * @return A GeospatialIndex object for use in constructing
	 *         ConstraintSources
	 */
	public GeospatialSpec elementGeospatialIndex(QName geospatialElement) {
		GeospatialSpecImpl index = new GeospatialSpecImpl();
		index.setElement(geospatialElement);
		index.setGeospatialIndexType(GeospatialIndexType.ELEMENT);
		return index;
	}
	
	/**
	 * Builds a geospatial index from data where latitude and longitude
	 * are encoded together in one element, and are children of
	 * a specified element.
	 * @param parent QName of the element that is the parent of geospatial coordinates.
	 * @param geospatialElement 
	 * 			QName of the element that contains coordinates. The
	 *            coordinates are comma-delimited. Default is that latitude
	 *            precedes longitude. If longitude precedes latitude in your
	 *            data, use a geo-option called "long-lat-points" on the
	 *            enclosing geospatial builder method.
	 */
	public GeospatialSpec elementChildGeospatialIndex(QName parent, QName geospatialElement) {
		GeospatialSpecImpl index = new GeospatialSpecImpl();
		index.setParent(parent);
		index.setElement(geospatialElement);
		index.setGeospatialIndexType(GeospatialIndexType.ELEMENT_CHILD);
		return index;
	}
	/**
	 * Builds a heatmap specification. Heatmaps are used to visualize the
	 * distribution of geographic data in space. They are defined by their
	 * bounding box, as well as the number of vertical and horizontal divisions
	 * within the heatmap.
	 * 
	 * @param south
	 *            Southern boundary of the heatmap.
	 * @param west
	 *            Western boundary of the heatmap.
	 * @param north
	 *            Northern boundary of the heatmap.
	 * @param east
	 *            Eastern boundary of the heatmap.
	 * @param latitudeDivs
	 *            Number of regions along the north-south axis..
	 * @param longitudeDivs
	 *            Number of regions along the east-west axis.
	 * @param options
	 *            0 or more string options to affect the heatmap.
	 * @return A HeatmapAndOptions object for use in constructing geospatial constraints.
	 */
	public HeatmapAndOptions heatmap(double south, double west, double north,
			double east, int latitudeDivs, int longitudeDivs, String... options) {
		HeatmapAndOptions heatmapAndOptions = new HeatmapAndOptions();
		Heatmap heatmap = new Heatmap();
		heatmap.setN(north);
		heatmap.setS(south);
		heatmap.setW(west);
		heatmap.setE(east);
		heatmap.setLatdivs(latitudeDivs);
		heatmap.setLondivs(longitudeDivs);
		heatmapAndOptions.setHeatmap(heatmap);
		heatmapAndOptions.setFacetOptions(Arrays.asList(options));
		return heatmapAndOptions;
	}

	/**
	 * Builds an object that binds a prefix to a namespace uri. Used in
	 * searchable experssions and path index specifications.
	 * 
	 * @param prefix
	 *            A prefix for us in xPath expressions.
	 * @param namespaceURI
	 *            A namespace URI to be bound to the above prefix.
	 * @return An object for use in constructing searchable expressions or path
	 *         index specifications.
	 */
	public ExpressionNamespaceBinding ns(String prefix, String namespaceURI) {
		return new ExpressionNamespaceBinding(prefix, namespaceURI);
	}

	/**
	 * Builds a new bucket for use in a {@link com.marklogic.client.admin.config.QueryOptions.QueryRange} configuration
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
	 * Builds a new bucket for use in a {@link com.marklogic.client.admin.config.QueryOptions.QueryRange}
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
	 * Constructs an argument to be used for building ExtractMetadata
	 * configuration.
	 * 
	 * @param constraintName
	 *            Name of a constraint defined elsewhere in the search options.
	 * @return A Constraint Value object for use in building ExtractMetadata.
	 */
	public ConstraintValue constraintValue(String constraintName) {
		return new ConstraintValue(constraintName);
	}

	/**
	 * Builds a reference to the document uri lexicon, for use in values and
	 * tuples specification.
	 * 
	 * @return A reference to the document uri lexicon.
	 */
	public QueryUri uri() {
		return QueryUri.YES;
	}

	/**
	 * Builds a field specification with the given name.
	 * <p>
	 * Field with given name must exist on the REST server to be used.
	 * 
	 * @param name
	 *            Name of a field from the REST server
	 * @return Field object for use in building or extractMetadata sources.
	 */
	public Field field(String name) {
		return new Field(name);
	}

	/**
	 * Builds an aggregate specification for use in configuring tuples or
	 * values. Use this method for MarkLogic builtin aggregate functions.
	 * 
	 * @param aggregateFunctionName
	 *            The name of a builtin aggregate function.
	 * @return An object for use in building tuples or values query options.
	 */
	public Aggregate aggregate(String aggregateFunctionName) {
		Aggregate a = new Aggregate();
		a.setApply(aggregateFunctionName);
		return a;
	}

	/**
	 * Builds an aggregate specification for use in configuring tuples or
	 * values. Use this method to access user-defined functions (UDFs) on the
	 * server.
	 * 
	 * @param aggregateFunctionName
	 *            The name of a builtin aggregate function.
	 * @param aggregatePlugin
	 *            The name of the plugin that supplies the user-defined
	 *            functino.
	 * @return An object for use in building tuples or values query options.
	 */
	public Aggregate aggregate(String aggregateFunctionName,
			String aggregatePlugin) {
		Aggregate a = new Aggregate();
		a.setApply(aggregateFunctionName);
		a.setUdf(aggregatePlugin);
		return a;
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
	 * Builds a range index type for strings.  Use QueryOptions.DEFAULT_COLLATION for the system default.  Otherwise consult your MarkLogic adminstrator.
	 * @param collation The collation to use for a range index.
	 * @return An object to configure range indexes along with data source information.
	 */
	public RangeIndexType stringRangeType(String collation) {
		RangeIndexType rangeIndexType = new RangeIndexType("xs:string");
		rangeIndexType.setCollation(collation);
		return rangeIndexType;
	}

	/**
	 * Builds a range index type for types other than xs:string.
	 * @param type The XML schema type backing the index.  This is a string in "xs:string" form.
	 * @return An object to configure range indexes along with data source information.
	 */
	public RangeIndexType rangeType(String type) {
		return new RangeIndexType(type);
	}

	/**
	 * Builds a list of buckets from definitions built by either bucket or computedBucket
	 * @param buckets Zero or more buckets
	 * @return A list of buckets for configuring a faceted search.
	 */
	public List<Buckets> buckets(Buckets... buckets) {
		return Arrays.asList(buckets);
	}

	/**
	 * Builds a specification to retrieve values from an element.
	 * @param elemName QName of the source element.
	 * @return An object for use in the extractMetadata method
	 */
	public ElementValue elementValue(QName elemName) {
		ElementValue qname = new ElementValue();
		qname.setElemName(elemName.getLocalPart());
		qname.setElemNs(elemName.getNamespaceURI());
		return qname;
	}

	/**
	 * Builds a specification to retrieve values from an attribute into the 
	 * extract-metadata section of search results.
	 * @param elemName QName of the source attribute's parent element.
	 * @param attrName QName of the attribute.
	 * @return An object for use in the extractMetadata method
	 */
	public AttributeValue attributeValue(QName elemName, QName attrName) {
		AttributeValue qname = new AttributeValue();
		qname.setAttrName(attrName.getLocalPart());
		qname.setAttrNs(attrName.getNamespaceURI());
		qname.setElemName(elemName.getLocalPart());
		qname.setElemNs(elemName.getNamespaceURI());
		return qname;
	}

	/**
	 * Builds a specification to retrieve values from a json key into the 
	 * extract-metadata section of search results.
	 * @param keyName Name of the source json key.
	 * @return An object for use in the extractMetadata method
	 */
	public JsonKey jsonValue(String keyName) {
		return new JsonKey(keyName);
	}

	/**
	 * Builds a list of starters for use in grammars.
	 * @param starters Zero or more starters, created with the starterGrouping or starterPrefix methods.
	 * @return A list of QueryStarters
	 */
	public List<QueryStarter> starters(QueryStarter... starters) {
		return Arrays.asList(starters);
	}

	/**
	 * Builds a starter used for grouping terms.
	 * @param text The text that starts a group of terms.
	 * @param strength The strength of this starter relative to other parts of the grammar.
	 * @param delimiter The text that delimits terms.
	 * @return A starter to be included in a grammar builder method call.
	 */
	public QueryStarter starterGrouping(String text, int strength,
			String delimiter) {
		QueryStarter starter = new QueryStarter();
		starter.setStarterText(text);
		starter.setStrength(strength);
		starter.setApply(StarterApply.GROUPING);
		starter.setDelimiter(delimiter);
		return starter;
	}

	/**
	 * Builds a starter used for prefixing terms.
	 * @param text The text that prefixes a term.
	 * @param strength The strength of this starter relative to other parts of the grammar.
     * @param element A String of form "cts:query", which is the name of cts:query element.
	 * @return A starter to be included in a grammar builder method call.
	 */
	public QueryStarter starterPrefix(String text, int strength, String element) {
		QueryStarter starter = new QueryStarter();
		starter.setStarterText(text);
		starter.setStrength(strength);
		starter.setApply(StarterApply.PREFIX);
		starter.setElement(element);
		return starter;
	}


	/**
	 * Builds a list of {@link com.marklogic.client.admin.config.QueryOptions.QueryGrammar.QueryJoiner} 
     * for use in constructing {@link com.marklogic.client.admin.config.QueryOptions.QueryGrammar}
     * instances.
	 * @param joiners Zero or more joiners, created with the joiner methods.
	 * @return A list of QueryJoiners
	 */
	public List<QueryJoiner> joiners(QueryJoiner... joiners) {
		return Arrays.asList(joiners);
	}

	/**
	 * Builds a QueryJoiner for use in a Search API 
     * {@link com.marklogic.client.admin.config.QueryOptions.QueryGrammar}.
	 * This method is for a simple joiner with just three arguments.
     * <pre>
	 * &lt;search:joiner strength="50"
	 * apply="constraint"&gt;:&lt;/search:joiner&gt;
     * </pre>
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
	 * Builds a QueryJoiner for use in a Search API 
     * {@link com.marklogic.client.admin.config.QueryOptions.QueryGrammar}
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
	public QueryJoiner joiner(String joinerText, int strength,
			JoinerApply apply, Comparator comparator, Tokenize tokenize) {
		QueryJoiner joiner = joiner(joinerText, strength, apply, null,
				tokenize, null);
		joiner.setCompare(comparator);
		return joiner;
	}

	/**
	 * Builds a QueryJoiner for use in a Search API 
     * {@link com.marklogic.client.admin.config.QueryOptions.QueryGrammar}
	 * 
	 * @param joinerText
	 *            Text of the joiner.
	 * @param strength
	 *            Strength of this joiner relative to others.
	 * @param apply
	 *            Enum to specify how the joiner fits into the Search grammar.
     * @param element 
     *            A String of form "cts:query", which is the name of cts:query element.
	 * @param token
	 *            Enum to specify how the joiner tokenizes the search string.
	 * @return An object for use in constructing QueryGrammar configurations.
	 */
	public QueryJoiner joiner(String joinerText, int strength,
			JoinerApply apply, String element, Tokenize token) {
		return joiner(joinerText, strength, apply, element, token, null);
	}

	/**
	 * Builds a more complex QueryJoiner for use in a Search API 
     * {@link com.marklogic.client.admin.config.QueryOptions.QueryGrammar}
	 * This method includes 'consume' which
	 * specifies how far to extend the scope of a cts:near-query query.
	 * 
	 * @param joinerText
	 *            Text of the joiner.
	 * @param strength
	 *            Strength of this joiner relative to others.
	 * @param apply
	 *            Enum to specify how the joiner fits into the Search grammar.
     * @param element 
     *            A String of form "cts:query", which is the name of cts:query element.
	 * @param tokenize
	 *            Enum to specify how the joiner tokenizes the search string.
	 * @param consume
	 *            How many tokens to consume for evaluating the near-query
	 * @return An object for use in constructing QueryGrammar configurations.
	 */
	public QueryJoiner joiner(String joinerText, int strength,
			JoinerApply apply, String element, Tokenize tokenize, Integer consume) {
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
	 * Builds a list of namespace/prefix bindings, as part of a {@link com.marklogic.client.admin.config.QueryOptions.PathIndex}
     * or {@link com.marklogic.client.admin.config.QueryOptions.QuerySearchableExpression} configuration.
	 * @param namespaces Zero or more namespace/prefix bindings
	 * @return An object that encapusulates N namespace/prefix bindings.  Used in path indexes and searchable expression.
	 */
	public ExpressionNamespaceBindings namespaces(
			ExpressionNamespaceBinding... namespaces) {
		ExpressionNamespaceBindings bindings = new ExpressionNamespaceBindings();
		for (ExpressionNamespaceBinding b : namespaces) {
			bindings.addBinding(b.getPrefix(), b.getNamespaceURI());
		}
		return bindings;
	}

}
