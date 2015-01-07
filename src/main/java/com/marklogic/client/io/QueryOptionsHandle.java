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
package com.marklogic.client.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.marklogic.client.util.EditableNamespaceContext;
import com.marklogic.client.MarkLogicBindingException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.admin.config.QueryOptions;
import com.marklogic.client.admin.config.QueryOptions.ExpressionNamespaceBinding;
import com.marklogic.client.admin.config.QueryOptions.ExpressionNamespaceBindings;
import com.marklogic.client.admin.config.QueryOptions.FragmentScope;
import com.marklogic.client.admin.config.QueryOptions.QueryAdditionalQuery;
import com.marklogic.client.admin.config.QueryOptions.QueryAnnotation;
import com.marklogic.client.admin.config.QueryOptions.QueryConstraint;
import com.marklogic.client.admin.config.QueryOptions.QueryDefaultSuggestionSource;
import com.marklogic.client.admin.config.QueryOptions.QueryExtractMetadata;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar;
import com.marklogic.client.admin.config.QueryOptions.QueryOperator;
import com.marklogic.client.admin.config.QueryOptions.QuerySearchableExpression;
import com.marklogic.client.admin.config.QueryOptions.QuerySortOrder;
import com.marklogic.client.admin.config.QueryOptions.QuerySuggestionSource;
import com.marklogic.client.admin.config.QueryOptions.QueryTerm;
import com.marklogic.client.admin.config.QueryOptions.QueryTransformResults;
import com.marklogic.client.admin.config.QueryOptions.QueryTuples;
import com.marklogic.client.admin.config.QueryOptions.QueryValues;
import com.marklogic.client.admin.config.support.Annotatable;
import com.marklogic.client.admin.config.support.QueryOptionsConfiguration;
import com.marklogic.client.impl.QueryOptionsTransformExtractNS;
import com.marklogic.client.impl.QueryOptionsTransformInjectNS;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.QueryOptionsReadHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;

/**
 * A QueryOptionsHandle is used to configure query configurations.
 * 
 * <p>Use a QueryOptionsHandle if you want to use Java to configure and manage
 * MarkLogic query configurations, for search, value lookups, and facets.</p>
 * 
 * <p>Read an options node from MarkLogic with</p>
 * 
 * <pre>QueryOptionsHandle handle = QueryOptionsManager.readOptions(name, new QueryOptionsHandle());</pre>
 * 
 * <p>or construct a fresh empty one (which is not a valid configuration without further building)</p>
 * 
 * <pre>QueryOptionsHandle handle = new QueryOptionsHandle();</pre>
 *  
 * <p>Build up options to a handle using fluent setter methods</p>
 * 
 * <pre>handle.withConstraints(...).withTerm(...).withOperators(...)</pre>
 * 
 * <p>and constructed items from {@link com.marklogic.client.admin.config.QueryOptionsBuilder}.</p>
 * 
 */
@SuppressWarnings("deprecation")
public final class QueryOptionsHandle
	extends BaseHandle<InputStream, OutputStreamSender>
	implements OutputStreamSender, BufferableHandle,
		QueryOptionsReadHandle, QueryOptionsWriteHandle, Annotatable<QueryOptionsHandle>
{

	static final private Logger logger = LoggerFactory
			.getLogger(QueryOptionsHandle.class);

    private static SAXParserFactory pfactory = SAXParserFactory.newInstance();
    private static TransformerFactory tfactory = TransformerFactory.newInstance();

    static {
        pfactory.setValidating(false);
        pfactory.setNamespaceAware(true);
    }

    private JAXBContext jc;
	private Marshaller marshaller;
	private QueryOptions optionsHolder;
	private Unmarshaller unmarshaller;

	/**
	 * Construct a new empty QueryOptionsHandle object.
	 */
    public QueryOptionsHandle() {
		super.setFormat(Format.XML);
   		setResendable(true);
		optionsHolder = new QueryOptions();

        try {
            SAXParser parser = pfactory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            QueryOptionsTransformExtractNS transform = new QueryOptionsTransformExtractNS();
            transform.setParent(reader);

            jc = JAXBContext.newInstance(QueryOptions.class);
            marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException e) {
            throw new MarkLogicBindingException(e);
        } catch (SAXException e) {
            throw new MarkLogicBindingException(e);
        } catch (ParserConfigurationException e) {
            throw new MarkLogicBindingException(e);
        }
    }

	@Override
    /**
     * Add an annotation to the query options.
     * @param queryAnnotation The annotation, as an XML string
     */
	public QueryOptionsHandle annotate(String queryAnnotation) {
		optionsHolder.annotate(queryAnnotation);
		return this;
	}

    /**
     * Add a constraint to the query options.
     * @param constraint The constraint.
     */
	public void addConstraint(QueryConstraint constraint) {
		optionsHolder.getQueryConstraints().add(constraint);
	}

    /**
     * Add a forest constraint to the operator state.
     * @param forest The forest id number on the server.
     */
	public void addForest(long forest) {
		optionsHolder.getForests().add(forest);
	}

    /**
     * Add an operator to the query options.
     * @param operator The operator.
     */
	public void addOperator(QueryOperator operator) {
		optionsHolder.getQueryOperators().add(operator);
	}

    /**
     * Add a search option to the query options.
     * @param searchOption The option.
     */
	public void addSearchOption(String searchOption) {
		optionsHolder.getSearchOptions().add(searchOption);
	}

    /**
     * Add a suggestion source to the query options.
     * @param suggestionSource The source.
     */
	public void addSuggestionSource(QuerySuggestionSource suggestionSource) {
		optionsHolder.getSuggestionSources().add(suggestionSource);
	}

    /**
     * Add a values accessor to the query options.
     * @param values The values accessor.
     */
	public void addValues(QueryValues values) {
		optionsHolder.getQueryValues().add(values);
	}
	
    /**
     * Returns the additional query from a query options configuration.
     * @return The DOM representation of the additional query.
     */
	public Element getAdditionalQuery() {
		return optionsHolder.getAdditionalQuery();
	}

    /**
     * Returns the concurrency level from the query options configuration.
     * @return The concurrency level.
     */
	public int getConcurrencyLevel() {
		return optionsHolder.getConcurrencyLevel();
	}

    /**
     * Return the named query constraint from the query options.
     * @param constraintName The name of the constraint.
     * @return The named QueryConstraint or null if no such constraint exists.
     */
	public QueryConstraint getConstraint(String constraintName) {
		for (QueryConstraint constraintOption : getConstraints()) {
			if (constraintOption.getName().equals(constraintName)) {
				return constraintOption;
			}
		}
		return null;
	}

    /**
     * Returns a List of all the constraints in the query options.
     * @return The constraint list.
     */
	public List<QueryConstraint> getConstraints() {
		return optionsHolder.getQueryConstraints();
	}

    /**
     * Returns a List of all the annotations in the query options.
      * @return The annotations list.
     */
	public List<QueryAnnotation> getAnnotations() {
		return optionsHolder.getAnnotations();
	}

    /**
     * Returns the state of the Debug flag in the query options.
     * @return The state of the flag.
     */
	public Boolean getDebug() {
		return returnWithDefault(optionsHolder.getDebug(), false);
	}

    /**
     * Returns the default suggestion source in the query options.
     * @return The suggestion source.
     */
	public QueryDefaultSuggestionSource getDefaultSuggestionSource() {
		return optionsHolder.getDefaultSuggestionSource();
	}

    /**
     * Returns a List of the forest constraints.
     * @return A list of forest id numbers on the server.
     */
	public List<Long> getForests() {
		return optionsHolder.getForests();
	}

    /**
     * Returns the underlying format supported by the query options handle.
     * @return Format.XML; no other format is supported by this handle.
     */
	@Override
	public Format getFormat() {
		return Format.XML;
	}

    /**
     * Returns the fragment scope setting from the query options.
     * @return The scope setting.
     */
	public FragmentScope getFragmentScope() {
		return FragmentScope.valueOf(optionsHolder.getFragmentScope().toUpperCase());
	}

    /**
     * Returns the search grammar specified in the query options.
     * @return The grammar.
     */
	public QueryGrammar getGrammar() {
		return optionsHolder.getGrammar();
	}

    /**
     * Returns a List of the operators specified in the query options.
     * @return The list of operators.
     */
	public List<QueryOperator> getOperators() {
		return optionsHolder.getQueryOperators();
	}

    /**
     * Returns the underlying QueryOptions representation of the query options.
     * @return The query options.
     */
	public QueryOptions getOptions() {
		return optionsHolder;
	}

    /**
     * Returns the page length specified in the query options.
     * @return The page length.
     */
	public long getPageLength() {
		return optionsHolder.getPageLength();
	}

    /**
     * Returns the quality weight specified in the query options.
     * @return The quality weight.
     */
	public double getQualityWeight() {
		return optionsHolder.getQualityWeight();
	}

    /**
     * Returns a List of the values accessors in the query options.
     * @return The list of value accessors.
     */
	public List<QueryValues> getValues() {
		return optionsHolder.getQueryValues();
	}

    /**
     * Returns the named values accessor.
     * @param valuesName The name of a values accessor.
     * @return The named accessor or null if no such accessor exists.
     */
	public QueryValues getValues(String valuesName) {
		for (QueryValues values : getValues()) {
			if (values.getName().equals(valuesName)) {
				return values;
			}
		}
		return null;
	}

    /**
     * Returns a List of the tuples accessors in the query options.
     * @return The list of tuples acccessors.
     */
	public List<QueryTuples> getTuples() {
		return optionsHolder.getQueryTuples();
	}

    /**
     * Returns the named tuples accessor.
     * @param tuplesName The name of a tuples accessor.
     * @return The named accessor or null if no such accessor exists.
     */
	public QueryTuples getTuples(String tuplesName) {
		for (QueryTuples values : getTuples()) {
			if (values.getName().equals(tuplesName)) {
				return values;
			}
		}
		return null;
	}

    /**
     * Returns the return aggregates option from the query options.
     * @return The return aggregates option setting.
     */
	public Boolean getReturnAggregates() {
		return returnWithDefault(optionsHolder.getReturnAggregates(), false);
	}

    /**
     * Returns the return constraints option from the query options.
     * @return The return constraints option setting.
     */
    public Boolean getReturnConstraints() {
		return returnWithDefault(optionsHolder.getReturnConstraints(), false);
	}

    /**
     * Returns the return facets option from the query options.
     * @return The return facets option setting.
     */
    public Boolean getReturnFacets() {
		return returnWithDefault(optionsHolder.getReturnFacets(), true);
	}

    /**
     * Returns the return frequencies option from the query options.
     * @return The return frequencies option setting.
     */
    public Boolean getReturnFrequencies() {
		return returnWithDefault(optionsHolder.getReturnFrequencies(), true);
	}

    /**
     * Returns the return metrics option from the query options.
     * @return The return metrics option setting.
     */
    public Boolean getReturnMetrics() {
		return returnWithDefault(optionsHolder.getReturnMetrics(), true);
	}

    /**
     * Returns the return query plan option from the query options.
     * @return The return query plan option setting.
     */
    public Boolean getReturnPlan() {
		return returnWithDefault(optionsHolder.getReturnPlan(), false);
	}

    /**
     * Returns the return query text option from the query options.
     * @return The return query text option setting.
     */
    public Boolean getReturnQtext() {
		return returnWithDefault(optionsHolder.getReturnQtext(), false);
	}

    /**
     * Returns the return query option from the query options.
     * @return The return query option setting.
     */
    public Boolean getReturnQuery() {
		return returnWithDefault(optionsHolder.getReturnQuery(), false);
	}

    /**
     * Returns the return results option from the query options.
     * @return The return results option setting.
     */
    public Boolean getReturnResults() {
		return returnWithDefault(optionsHolder.getReturnResults(), true);
	}

    /**
     * Returns the return similar option from the query options.
     * @return The return similar option setting.
     */
    public Boolean getReturnSimilar() {
		return returnWithDefault(optionsHolder.getReturnSimilar(), false);
	}

    /**
     * Returns the return values option from the query options.
     * @return The return values option setting.
     */
    public Boolean getReturnValues() {
		return returnWithDefault(optionsHolder.getReturnValues(), true);
	}

    /**
     * Returns the namespace context associated with the searchable expression in the query options.
     * @return The namespace context.
     */
    public NamespaceContext getSearchableExpressionNamespaceContext() {
        return optionsHolder.getSearchableExpressionNamespaceContext();
    }

    /**
     * Returns the searchable expression in the query options.
     *
     * In order to evaluate the expression, you must also obtain the NamespaceContext associated
     * with the expression, see getSearchableExpression().
     *
     * @return The expression.
     */
	public String getSearchableExpression() {
		return optionsHolder.getSearchableExpression();
	}

    /**
     * Returns a List of the search options in the query options.
     * @return The list of options.
     */
	public List<String> getSearchOptions() {
		return optionsHolder.getSearchOptions();
	}

    /**
     * Returns a List of the sort orders specified in the query options.
     * @return The list of sort orders.
     */
	public List<QuerySortOrder> getSortOrders() {
		return optionsHolder.getSortOrders();
	}

    /**
     * Returns a List of the suggestion sources in the query options.
     * @return The list of sources.
     */
	public List<QuerySuggestionSource> getSuggestionSources() {
		return optionsHolder.getSuggestionSources();
	}

    /**
     * Returns the term associated specified in the query options.
     * @return The term.
     */
	public QueryTerm getTerm() {
		return optionsHolder.getTerm();
	}

    /**
     * Returns the transform results setting specified in the query options.
     * @return The transform results.
     */
	public QueryTransformResults getTransformResults() {
		return optionsHolder.getTransformResults();
	}

    /**
     * Sets the ctsQuery element in the query options.
     * @param ctsQuery A DOM node representation of the cts:query.
     */
	public void setAdditionalQuery(Element ctsQuery) {
		optionsHolder.setAdditionalQuery(ctsQuery);

	}

    /**
     * Sets the concurrency level in the query options.
     * @param concurrencyLevel The concurrency level.
     */
	public void setConcurrencyLevel(Integer concurrencyLevel) {
		optionsHolder.setConcurrencyLevel(concurrencyLevel);

	}

    /**
     * Sets the debug flag in the query options.
     * @param debug The flag.
     */
	public void setDebug(Boolean debug) {
		optionsHolder.setDebug(debug);

	}

    /**
     * Sets the default suggestion source in the query options.
     * @param defaultSuggestionSource The default source.
     */
	public void setDefaultSuggestionSource(QueryDefaultSuggestionSource defaultSuggestionSource) {
		optionsHolder.setDefaultSuggestionSource(defaultSuggestionSource);
	}

    /**
     * Sets a List of forests constraints in the query options.
     * @param forests A list of forest id numbers from the server.
     */
	public void setForests(List<Long> forests) {
		optionsHolder.setForests(forests);
	}

    /**
     * Set the format accepted by this handle.
     *
     * Only XML is accepted by this handle.
     *
     * @param format The format, which must Format.XML or an exception will be raised.
     */
	@Override
	public void setFormat(Format format) {
		if (format != Format.XML) {
            throw new IllegalArgumentException("QueryOptionsHandle supports the XML format only");
        }
	}

    /**
     * Sets the fragment scope in the query options.
     * @param fragmentScope The scope.
     */
	public void setFragmentScope(FragmentScope fragmentScope) {
		optionsHolder.setFragmentScope(fragmentScope);

	}

    /**
     * Sets the grammar in the query options.
     * @param grammar The grammar.
     */
	public void setGrammar(QueryGrammar grammar) {
		optionsHolder.setGrammar(grammar);
	}
	
    /**
     * Sets the grammar in the query options.
     * @param grammar The grammar.
     * @return This modified QueryOptionsHandle, for further fluent setting.
     */
	public QueryOptionsHandle withGrammar(QueryGrammar grammar) {
		optionsHolder.setGrammar(grammar);
		return this;
	}



    /**
     * Sets a List of operators in the query options.
     * @param operatorOptions The list of operators.
     */
	public void setOperators(List<QueryOperator> operatorOptions) {
		optionsHolder.setOperators(operatorOptions);
	}

    /**
     * Sets the page length in the query options.
     * @param pageLength The page length.
     */
	public void setPageLength(Long pageLength) {
		optionsHolder.setPageLength(pageLength);
	}

    /**
     * Sets a List of operators in the query options.
     * @param queryOperator The list of operators.
     * @return This modified QueryOptionsHandle, for further fluent setting.
     */
	public QueryOptionsHandle withOperators(QueryOperator... queryOperator) {
		optionsHolder.setOperators(Arrays.asList(queryOperator));
		return this;
	}

    /**
     * Sets a List of tuples in the query options.
     * @param tuples The list of tuples.
     */
	public void setTuples(List<QueryTuples> tuples) {
		optionsHolder.setQueryTuples(tuples);
	}
    
    /**
     * Sets a List of tuples in the query options.
     * @param tuples The list of tuples.
     * @return This modified QueryOptionsHandle, for further fluent setting.
     */
	public QueryOptionsHandle withTuples(QueryTuples... tuples) {
		optionsHolder.setQueryTuples(Arrays.asList(tuples));
		return this;
	}

    /**
     * Sets a List of values in the query options.
     * @param values The list of values.
     */
	public void setValues(List<QueryValues> values) {
		optionsHolder.setQueryValues(values);
	}

    /**
     * Sets a List of values in the query options.
     * @param values The list of values.
     * @return This modified QueryOptionsHandle, for further fluent setting.
     */
	public QueryOptionsHandle withValues(QueryValues... values) {
		optionsHolder.setQueryValues(Arrays.asList(values));
		return this;
	}
	

    /**
     * Sets the quality weight in the search options.
     * @param qualityWeight The weight.
     */
	public void setQualityWeight(Double qualityWeight) {
		optionsHolder.setQualityWeight(qualityWeight);
	}

    /**
     * Sets the return aggregates option in the query options.
     * @param returnAggregates The return aggregates option.
     */
	public void setReturnAggregates(Boolean returnAggregates) {
		optionsHolder.setReturnAggregates(returnAggregates);
	}

    /**
     * Sets the return constraints option in the query options.
     * @param returnConstraints The return constraints option.
     */
    public void setReturnConstraints(Boolean returnConstraints) {
		optionsHolder.setReturnConstraints(returnConstraints);
	}

    /**
     * Sets the return facets option in the query options.
     * @param returnFacets The return facets option.
     */
    public void setReturnFacets(Boolean returnFacets) {
		optionsHolder.setReturnFacets(returnFacets);
	}

    /**
     * Sets the return frequencies option in the query options.
     * @param returnFrequencies The return frequencies option.
     */
    public void setReturnFrequencies(Boolean returnFrequencies) {
		optionsHolder.setReturnFrequencies(returnFrequencies);
	}

    /**
     * Sets the return metric option in the query options.
     * @param returnMetrics The return metrics option.
     */
    public void setReturnMetrics(Boolean returnMetrics) {
		optionsHolder.setReturnMetrics(returnMetrics);
	}

    /**
     * Sets the return query option in the query options.
     * @param returnQuery The return query option.
     */
    public void setReturnQuery(Boolean returnQuery) {
		optionsHolder.setReturnQuery(returnQuery);
	}

    /**
     * Sets the return query plan  option in the query options.
     * @param returnPlan The return plan option.
     */
    public void setReturnPlan(Boolean returnPlan) {
		optionsHolder.setReturnPlan(returnPlan);
	}

    /**
     * Sets the return query text option in the query options.
     * @param returnQtext The return query text option.
     */
    public void setReturnQtext(Boolean returnQtext) {
		optionsHolder.setReturnQtext(returnQtext);
	}

    /**
     * Sets the return results option in the query options.
     * @param returnResults The return results option.
     */
    public void setReturnResults(Boolean returnResults) {
		optionsHolder.setReturnResults(returnResults);
	}

    /**
     * Sets the return similar option in the query options.
     * @param returnSimilar The return similar option.
     */
    public void setReturnSimilar(Boolean returnSimilar) {
		optionsHolder.setReturnSimilar(returnSimilar);
	}

    /**
     * Sets the return values option in the query options.
     * @param returnValues The return values option.
     */
    public void setReturnValues(Boolean returnValues) {
		optionsHolder.setReturnValues(returnValues);
	}

    /**
     * Sets the searchable expression in the query options.
     *
     * In order to be sure that the expression has the correct namespace bindings, you must always
     * specify both the expression and the namespace context of that expression, see
     * setSearchableExpressionNamespaceContext().
     *
     * @param searchableExpression The expression.
     */
	public void setSearchableExpression(String searchableExpression) {
        EditableNamespaceContext context = optionsHolder.getSearchableExpressionNamespaceContext();
        ExpressionNamespaceBindings bindings = new ExpressionNamespaceBindings();
        for (String prefix : context.getAllPrefixes()) {
            if (!"".equals(prefix)) {
            	bindings.addBinding(prefix, context.getNamespaceURI(prefix));
            }
        }
        ExpressionNamespaceBinding[] bindingsArray = bindings.toArray();
        optionsHolder.setSearchableExpression(new QuerySearchableExpression(searchableExpression, bindingsArray));
	}

    /**
     * Sets the namespace context to be associated with the searchable expression.
     * @param context The namespace context.
     */
    public void setSearchableExpressionNamespaceContext(EditableNamespaceContext context) {
        optionsHolder.setSearchableExpressionNamespaceContext(context);
        String expr = optionsHolder.getSearchableExpression();
        if (expr != null) {
            setSearchableExpression(expr);
        }
    }

    /**
     * Set a List of search options in the query options.
     * @param searchOptions The list of options.
     */
	public void setSearchOptions(List<String> searchOptions) {
		optionsHolder.setSearchOptions(searchOptions);
	}

    /**
     * Set a List of sort orders in the query options.
     * @param sortOrders The list of sort orders.
     */
	public void setSortOrders(List<QuerySortOrder> sortOrders) {
		optionsHolder.setSortOrders(sortOrders);
	}

    /**
     * Set a List of suggestion sources in the query options.
     * @param suggestionSources The list of sources.
     */
	public void setSuggestionSources(
			List<QuerySuggestionSource> suggestionSources) {
		optionsHolder.setSuggestionSources(suggestionSources);
	}

    /**
     * Sets the ctsQuery element in the query options.
     * @param additionalQuery An object representation of the cts:query.  Build with QueryOptionsBuilder.additionalQuery
     * @return this QueryOptionsHandle, for fluent setting.
     */
	public QueryOptionsHandle withAdditionalQuery(QueryAdditionalQuery additionalQuery) {
		setAdditionalQuery(additionalQuery.getValue());
		return this;
	}


    /**
     * Sets the searchable expression in the query options.
     *
     * @param searchableExpression The searchableExpression, in an object together with namespace bindings.
     * @return this QueryOptionsHandle, for fluent setting.
     */
    public QueryOptionsHandle withSearchableExpression(QuerySearchableExpression searchableExpression) {
    	optionsHolder.setSearchableExpression(searchableExpression);
    	return this;
    }

    /**
     * Set a List of sort orders in the query options.
     * @param sortOrders The list of sort orders.
     * @return this QueryOptionsHandle, for fluent setting.
     */
	public QueryOptionsHandle withSortOrders(QuerySortOrder... sortOrders) {
		optionsHolder.setSortOrders(Arrays.asList(sortOrders));
		return this;
    }

    /**
     * Set the term configuration in the query options.
     * @param termConfig The term configuration.
     */
	public void setTerm(QueryTerm termConfig) {
		optionsHolder.setTerm(termConfig);
	}

    /**
     * Set the transform results in the query options.
     * @param transformResults The transform results.
     */
	public void setTransformResults(QueryTransformResults transformResults) {
		optionsHolder.setTransformResults(transformResults);
    }


    /**
     * Set the term configuration in the query options.
     * @param term The term configuration.
     * @return this QueryOptionsHandle, for fluent setting.
     */
	public QueryOptionsHandle withTerm (QueryTerm term) {
		optionsHolder.setTerm(term);
		return this;
	}
	

    /**
     * Set the transform results in the query options.
     * @param transformResults The transform results.
     * @return this QueryOptionsHandle, for fluent setting.
     */
	public QueryOptionsHandle withTransformResults(QueryTransformResults transformResults) {
		optionsHolder.setTransformResults(transformResults);
		return this;
	}

    /**
     * Writes the query options node (as XML) to the specified output stream.
     * @param out the output stream receiving the node.
     * @throws IOException if there is an I/O error writing to that stream.
     */
	public void write(OutputStream out) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

		JAXBElement<QueryOptions> jaxbElement = new JAXBElement<QueryOptions>(
				new QName("http://marklogic.com/appservices/search", "options"),
				QueryOptions.class, optionsHolder);
		try {
			logger.debug("Begin write of QueryOptionsHandle");
            optionsHolder.patchBindings();

			marshaller.marshal(jaxbElement, baos);

            QueryOptionsTransformInjectNS itransform = new QueryOptionsTransformInjectNS();

            String xml = baos.toString();
            InputStream in = new ByteArrayInputStream(xml.getBytes("utf-8"));
            InputSource source = new InputSource(in);

            SAXParser parser = pfactory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            itransform.setParent(reader);

            Transformer transformer = tfactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");

            StreamResult result = new StreamResult(out);

            SAXSource saxSource = new SAXSource(itransform, source);
            transformer.transform(saxSource, result);
            logger.debug("End write of QueryOptionsHandle");       
        } catch (JAXBException e) {
            throw new MarkLogicBindingException(e);
        } catch (ParserConfigurationException e) {
            throw new MarkLogicBindingException(e);
        } catch (TransformerConfigurationException e) {
            throw new MarkLogicBindingException(e);
        } catch (TransformerException e) {
            throw new MarkLogicBindingException(e);
        } catch (SAXException e) {
            throw new MarkLogicBindingException(e);
        }
	}

	private Boolean returnWithDefault(Boolean returnValue, Boolean defaultValue) {
		if (returnValue == null) {
			return defaultValue;
		} else {
			return returnValue;
		}
	}
	
	/**
     * Loads a query options node from a byte array.
     *
     * The byte array must store the query options node in XML format in the UTF-8 encoding.
	 */
	@Override
	public void fromBuffer(byte[] buffer) {
		receiveContent(new ByteArrayInputStream(buffer));
	}

    /**
     * Returns the query options node in a byte array.
     *
     * @return A byte array containing the node in XML format in the UTF-8 encoding.
     */
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
	/**
	 * Returns the Query Options as an XML string.
	 */
	@Override
	public String toString() {
		try {
			return new String(toBuffer(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new MarkLogicIOException(e);
		}
	}

	@Override
	protected Class<InputStream> receiveAs() {
		return InputStream.class;
	}

	@Override
	protected void receiveContent(InputStream content) {
		try {
            SAXParser parser = pfactory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            Transformer transformer = tfactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");

            // This is kind of gross; TODO: investigate how to do this as a single filter

            QueryOptionsTransformExtractNS transform = new QueryOptionsTransformExtractNS();
            transform.setParent(reader);

            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            InputSource source = new InputSource(content);
            SAXSource saxSource = new SAXSource(transform, source);
            transformer.transform(saxSource, result);
            String xmlResult = sw.toString();
            InputStream in = new ByteArrayInputStream(xmlResult.getBytes("UTF-8"));
            optionsHolder = (QueryOptions) unmarshaller.unmarshal(in);
        } catch (JAXBException e) {
            throw new MarkLogicBindingException(e);
        } catch (ParserConfigurationException e) {
            throw new MarkLogicBindingException(e);
        } catch (TransformerConfigurationException e) {
            throw new MarkLogicBindingException(e);
        } catch (TransformerException e) {
            throw new MarkLogicBindingException(e);
        } catch (SAXException e) {
            throw new MarkLogicBindingException(e);
        } catch (UnsupportedEncodingException e) {
            // This can't actually happen...stupid checked exceptions
            throw new MarkLogicBindingException(e);
        } finally {
			try {
				content.close();
			} catch (IOException e) {
				// ignore.
			}
		}
	}
	
	protected OutputStreamSender sendContent() {
		return this;
	}

    /**
     * Returns the named query operator.
     * @param name The name of the operator.
     * @return The named operator or null if no such operator exists.
     */
	public QueryOperator getOperator(String name) {
		for (QueryOperator operator : optionsHolder.getQueryOperators()) {
			if (operator.getName().equals(name)) {
				return operator;
			}
		}
		return null;
	}

    /**
     * Returns the extract metadata setting from the options node.
     * @return The extract metadata setting.
     */
	public QueryExtractMetadata getExtractMetadata() {
		return optionsHolder.getExtractMetadata();
	}

    /**
     * Sets the extract metadata setting in the options node.
     * @param extractMetadata The extract metadata setting.
     */
	public void setExtractMetadata(QueryExtractMetadata extractMetadata) {
		optionsHolder.setExtractMetadata(extractMetadata);
    }

    /**
     * Sets the extract metadata setting in the options node.
     * @param extractMetadata The extract metadata setting.
     * @return this QueryOptionsHandle, for fluent setting.
     */
	public QueryOptionsHandle withExtractMetadata(QueryExtractMetadata extractMetadata) {
		optionsHolder.setExtractMetadata(extractMetadata);
		return this;
	}

    /**
     * Set a List of constraints in the query options.
     * @param constraints The list of constraints.
     */
	public void setConstraints(List<QueryConstraint> constraints) {
		optionsHolder.setConstraints(constraints);
	}

    /**
     * Set a List of constraints in the query options.
     * @param constraints The list of constraints.
     * @return this QueryOptionsHandle, for fluent setting.
     */
	public QueryOptionsHandle withConstraints(QueryConstraint... constraints) {
		optionsHolder.setConstraints(Arrays.asList(constraints));
		return this;
	}

    /**
     * Set common configuration objects for this handle, fluently.
     * @param configuration The configuration object containing common settings.
     * @return this QueryOptionsHandle, for fluent setting.
     */
	public QueryOptionsHandle withConfiguration(QueryOptionsConfiguration configuration) {
	    optionsHolder.setConcurrencyLevel(configuration.getConcurrencyLevel());
        optionsHolder.setDebug(configuration.getDebug());
        optionsHolder.setForests(configuration.getForests());
        optionsHolder.setFragmentScope(configuration.getFragmentScope());
        optionsHolder.setPageLength(configuration.getPageLength());
        optionsHolder.setQualityWeight(configuration.getQualityWeight());
        optionsHolder.setReturnAggregates(configuration.getReturnAggregates());
        optionsHolder.setReturnConstraints(configuration.getReturnConstraints());
        optionsHolder.setReturnFacets(configuration.getReturnFacets());
        optionsHolder.setReturnFrequencies(configuration.getReturnFrequencies());
        optionsHolder.setReturnMetrics(configuration.getReturnMetrics());
        optionsHolder.setReturnPlan(configuration.getReturnPlan());
        optionsHolder.setReturnQtext(configuration.getReturnQtext());
        optionsHolder.setReturnQuery(configuration.getReturnQuery());
        optionsHolder.setReturnResults(configuration.getReturnResults());
        optionsHolder.setReturnSimilar(configuration.getReturnSimilar());
        optionsHolder.setReturnValues(configuration.getReturnValues());
        optionsHolder.setSearchOptions(configuration.getSearchOptions());
		return this;
	}

	


}
