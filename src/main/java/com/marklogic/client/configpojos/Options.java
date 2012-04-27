package com.marklogic.client.configpojos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.w3c.dom.Element;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace=Options.SEARCH_NS, name="options")
public class Options {

	public static final String SEARCH_NS = "http://marklogic.com/appservices/search";
	// Boolean options
	@XmlElement(namespace=Options.SEARCH_NS, name="return-facets")
	private Boolean returnFacets;
	@XmlElement(namespace=Options.SEARCH_NS, name="return-constraints")
	private Boolean returnConstraints;
	@XmlElement(namespace=Options.SEARCH_NS, name="return-metrics")
	private Boolean returnMetrics;
	@XmlElement(namespace=Options.SEARCH_NS, name="return-plan")
	private Boolean returnPlan;
	@XmlElement(namespace=Options.SEARCH_NS, name="return-qtext")
	private Boolean returnQtext;
	@XmlElement(namespace=Options.SEARCH_NS, name="return-query")
	private Boolean returnQuery;
	@XmlElement(namespace=Options.SEARCH_NS, name="return-results")
	private Boolean returnResults;
	@XmlElement(namespace=Options.SEARCH_NS, name="return-similar")
	private Boolean returnSimilar;
	@XmlElement(namespace=Options.SEARCH_NS, name="debug")
	private Boolean debug;
	@XmlElement(namespace=Options.SEARCH_NS, name="searchable-expression")
	private String searchableExpression;
	@XmlElement(namespace=Options.SEARCH_NS, name="term")
	private Term term;
	@XmlElement(namespace=Options.SEARCH_NS, name="transform-results")
	private TransformResults transformResults;
	@XmlElement(namespace=Options.SEARCH_NS, name="fragment-scope")
	private String fragmentScope; // TODO fragment scope enum
	@XmlElement(namespace=Options.SEARCH_NS, name="concurrency-level")
	private Integer concurrencyLevel;
	@XmlElement(namespace=Options.SEARCH_NS, name="page-length")
	private Long pageLength;
	@XmlElement(namespace=Options.SEARCH_NS, name="quality-weight")
	private Double qualityWeight;
	@XmlElement(namespace=Options.SEARCH_NS, name = "forest")
	private List<Long> forests;
	@XmlElement(namespace=Options.SEARCH_NS, name = "search-option")
	private List<String> searchOptions;
	@XmlElement(namespace=Options.SEARCH_NS, name = "constraint")
	private List<Constraint> constraints;
	@XmlElement(namespace=Options.SEARCH_NS, name = "operator")
	private List<Operator> operators;
	@XmlElement(namespace=Options.SEARCH_NS, name = "sort-order")
	private List<SortOrder> sortOrders;
	@XmlElement(namespace=Options.SEARCH_NS, name = "suggestion-source")
	private List<SuggestionSource> suggestionSources;
	@XmlElement(namespace=Options.SEARCH_NS, name="additional-query")
    private AnyElement additionalQuery;
	@XmlElement(namespace=Options.SEARCH_NS, name="grammar")
	private Grammar grammar;
	@XmlElement(namespace=Options.SEARCH_NS, name="default-suggestion-source")
	private DefaultSuggestionSource defaultSuggestionSource;

	public Options() {
		// options that can have more than one cardinality
		// are in lists.
		constraints = new ArrayList<Constraint>();
		operators = new ArrayList<Operator>();
		sortOrders = new ArrayList<SortOrder>();
		suggestionSources = new ArrayList<SuggestionSource>();
		// queryAnnotations = new ArrayList<QueryAnnotation>();

		// options that can have one-or-none and are complex
		// and options that are simple types
		// need no initialization.

	}

	public Boolean isReturnFacets() {
		return returnFacets;
	}

	public void setReturnFacets(Boolean returnFacets) {
		this.returnFacets = returnFacets;
	}

	public Boolean isReturnConstraints() {
		return returnConstraints;
	}

	public void setReturnConstraints(Boolean returnConstraints) {
		this.returnConstraints = returnConstraints;
	}

	public Boolean isReturnMetrics() {
		return returnMetrics;
	}

	public void setReturnMetrics(Boolean returnMetrics) {
		this.returnMetrics = returnMetrics;
	}

	public Boolean isReturnPlan() {
		return returnPlan;
	}

	public void setReturnPlan(Boolean returnPlan) {
		this.returnPlan = returnPlan;
	}

	public Boolean isReturnQtext() {
		return returnQtext;
	}

	public void setReturnQtext(Boolean returnQtext) {
		this.returnQtext = returnQtext;
	}

	public Boolean isReturnQuery() {
		return returnQuery;
	}

	public void setReturnQuery(Boolean returnQuery) {
		this.returnQuery = returnQuery;
	}

	public Boolean isReturnResults() {
		return returnResults;
	}

	public void setReturnResults(Boolean returnResults) {
		this.returnResults = returnResults;
	}

	public Boolean isReturnSimilar() {
		return returnSimilar;
	}

	public void setReturnSimilar(Boolean returnSimilar) {
		this.returnSimilar = returnSimilar;
	}

	public Boolean isDebug() {
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

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	public TransformResults getTransformResults() {
		return transformResults;
	}

	public void setTransformResults(TransformResults transformResults) {
		this.transformResults = transformResults;
	}

	public String getFragmentScope() {
		return fragmentScope;
	}

	public void setFragmentScope(String fragmentScope) {
		this.fragmentScope = fragmentScope;
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

	public List<String> getSearchOptions() {
		return searchOptions;
	}

	public void setSearchOptions(List<String> searchOptions) {
		this.searchOptions = searchOptions;
	}

	public <T extends Constraint> List<T> getConstraints() {
		if (constraints == null) {
			return new ArrayList<T>();
		} else {
			return (List<T>) constraints;
		}
	}

	public List<Operator> getOperators() {
		return operators;
	}

	public void setOperators(List<Operator> operators) {
		this.operators = operators;
	}

	public List<SortOrder> getSortOrders() {
		return sortOrders;
	}

	public void setSortOrders(List<SortOrder> sortOrders) {
		this.sortOrders = sortOrders;
	}

	public List<SuggestionSource> getSuggestionSources() {
		return suggestionSources;
	}

	public void setSuggestionSources(List<SuggestionSource> suggestionSources) {
		this.suggestionSources = suggestionSources;
	}

	public Element getAdditionalQuery() {
		return additionalQuery.getValue();
	}

	public void setAdditionalQuery(Element additionalQuery) {
		this.additionalQuery = new AnyElement(additionalQuery);
	}

	public Grammar getGrammar() {
		return grammar;
	}

	public void setGrammar(Grammar grammar) {
		this.grammar = grammar;
	}

	public void setDefaultSuggestionSource(DefaultSuggestionSource dss) {
		this.defaultSuggestionSource = dss;
	}

}
