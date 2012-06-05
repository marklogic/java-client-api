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
package com.marklogic.client.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.config.MarkLogicBindingException;
import com.marklogic.client.config.QueryOptions;
import com.marklogic.client.config.QueryOptions.FragmentScope;
import com.marklogic.client.config.QueryOptions.QueryAnnotation;
import com.marklogic.client.config.QueryOptions.QueryConstraint;
import com.marklogic.client.config.QueryOptions.QueryDefaultSuggestionSource;
import com.marklogic.client.config.QueryOptions.QueryGrammar;
import com.marklogic.client.config.QueryOptions.QueryOperator;
import com.marklogic.client.config.QueryOptions.QuerySortOrder;
import com.marklogic.client.config.QueryOptions.QuerySuggestionSource;
import com.marklogic.client.config.QueryOptions.QueryTerm;
import com.marklogic.client.config.QueryOptions.QueryTransformResults;
import com.marklogic.client.config.QueryOptions.QueryValues;
import com.marklogic.client.config.QueryOptionsBuilder.QueryOptionsItem;
import com.marklogic.client.io.marker.QueryOptionsReadHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;

public final class QueryOptionsHandle extends
		BaseHandle<InputStream, OutputStreamSender> implements
		OutputStreamSender, QueryOptionsReadHandle, QueryOptionsWriteHandle {

	static final private Logger logger = LoggerFactory
			.getLogger(QueryOptionsHandle.class);

	private JAXBContext jc;
	private Marshaller marshaller;
	private QueryOptions optionsHolder;
	private Unmarshaller unmarshaller;

	public QueryOptionsHandle() {
		super.setFormat(Format.XML);
		optionsHolder = new QueryOptions();

        try {
            jc = JAXBContext.newInstance(QueryOptions.class);
            marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException e) {
            throw new MarkLogicBindingException(e);
        }
    }

	/*
	 * Adders
	 */
	public void addAnnotation(QueryAnnotation queryAnnotation) {
		optionsHolder.addAnnotation(queryAnnotation);
	};

	public void addConstraint(QueryConstraint constraint) {
		optionsHolder.getQueryConstraints().add(constraint);
	};

	public void addForest(long forest) {
		optionsHolder.getForests().add(forest);
	}

	public void addOperator(QueryOperator operator) {
		optionsHolder.getQueryOperators().add(operator);
	};

	public void addSearchOption(String searchOption) {
		optionsHolder.getSearchOptions().add(searchOption);
	}
	
	public void addSuggestionSource(QuerySuggestionSource suggestionSource) {
		optionsHolder.getSuggestionSources().add(suggestionSource);
	}
	public void addValues(QueryValues values) {
		optionsHolder.getQueryValues().add(values);
	}
	/**
	 * Add more QueryOptionsItems to a QueryOptionsHandle using a QueryOptionsBuilder
	 * @param options 0 or more QueryOptionsItems
	 * @return the resulting updated QueryOptionsHandle
	 */
	public QueryOptionsHandle build(QueryOptionsItem... options) {
		for (QueryOptionsItem option : options) {
			logger.debug(option.getClass().getName());
			option.build(optionsHolder);
		}
		return this;
	}
	public Element getAdditionalQuery() {
		return optionsHolder.getAdditionalQuery();
	}
	public int getConcurrencyLevel() {
		return optionsHolder.getConcurrencyLevel();
	}
	public QueryConstraint getConstraint(String constraintName) {
		for (QueryConstraint constraintOption : getConstraints()) {
			if (constraintOption.getName().equals(constraintName)) {
				return constraintOption;
			}
		}
		return null;
	}
	public List<QueryConstraint> getConstraints() {
		return optionsHolder.getQueryConstraints();
	}

	
	/*
	 * Getters
	 */
	public List<QueryAnnotation> getAnnotations() {
		return optionsHolder.getAnnotations();
	}
	public Boolean getDebug() {
		return returnWithDefault(optionsHolder.getDebug(), false);
	}

	public QueryDefaultSuggestionSource getDefaultSuggestionSource() {
		return optionsHolder.getDefaultSuggestionSource();
	}

	public List<Long> getForests() {
		return optionsHolder.getForests();
	}

	@Override
	public Format getFormat() {
		return Format.XML;
	}

	public FragmentScope getFragmentScope() {
		return FragmentScope.valueOf(optionsHolder.getFragmentScope().toUpperCase());
	}

	public QueryGrammar getGrammar() {
		return optionsHolder.getGrammar();
	}

	public List<QueryOperator> getOperators() {
		return optionsHolder.getQueryOperators();
	}

	public QueryOptions getOptions() {
		return optionsHolder;
	}

	public long getPageLength() {
		return optionsHolder.getPageLength();
	}

	public double getQualityWeight() {
		return optionsHolder.getQualityWeight();
	}

	public List<QueryValues> getValues() {
		return optionsHolder.getQueryValues();
	}

	public QueryValues getValues(String valuesName) {
		for (QueryValues values : getValues()) {
			if (values.getName().equals(valuesName)) {
				return values;
			}
		}
		return null;
	}

	public Boolean getReturnAggregates() {
		return returnWithDefault(optionsHolder.getReturnAggregates(), false);
	}
	
	public Boolean getReturnConstraints() {
		return returnWithDefault(optionsHolder.getReturnConstraints(), false);
	}

	public Boolean getReturnFacets() {
		return returnWithDefault(optionsHolder.getReturnFacets(), true);
	}

	public Boolean getReturnFrequencies() {
		return returnWithDefault(optionsHolder.getReturnFrequencies(), true);
	}

	public Boolean getReturnMetrics() {
		return returnWithDefault(optionsHolder.getReturnMetrics(), true);
	}

	public Boolean getReturnPlan() {
		return returnWithDefault(optionsHolder.getReturnPlan(), false);
	}

	public Boolean getReturnQtext() {
		return returnWithDefault(optionsHolder.getReturnQtext(), false);
	}

	public Boolean getReturnQuery() {
		return returnWithDefault(optionsHolder.getReturnQuery(), false);
	}

	public Boolean getReturnResults() {
		return returnWithDefault(optionsHolder.getReturnResults(), true);
	}

	public Boolean getReturnSimilar() {
		return returnWithDefault(optionsHolder.getReturnSimilar(), false);
	}

	public Boolean getReturnValues() {
		return returnWithDefault(optionsHolder.getReturnValues(), true);
	}

	public org.w3c.dom.Element getSearchableExpression() {
		return optionsHolder.getSearchableExpression();
	}
	
	public List<String> getSearchOptions() {
		return optionsHolder.getSearchOptions();
	}
	public List<QuerySortOrder> getSortOrders() {
		return optionsHolder.getSortOrders();
	}
	public List<QuerySuggestionSource> getSuggestionSources() {
		return optionsHolder.getSuggestionSources();
	}

	public QueryTerm getTerm() {
		return optionsHolder.getTerm();
	}

	public QueryTransformResults getTransformResults() {
		return optionsHolder.getTransformResults();
	}

	/*
	 * Bean Setters
	 */
	public void setAdditionalQuery(Element ctsQuery) {
		optionsHolder.setAdditionalQuery(ctsQuery);

	}

	public void setConcurrencyLevel(Integer concurrencyLevel) {
		optionsHolder.setConcurrencyLevel(concurrencyLevel);

	}

	public void setDebug(Boolean debug) {
		optionsHolder.setDebug(debug);

	}

	public void setDefaultSuggestionSource(QueryDefaultSuggestionSource defaultSuggestionSource) {
		optionsHolder.setDefaultSuggestionSource(defaultSuggestionSource);
	}
	
	public void setForests(List<Long> forests) {
		optionsHolder.setForests(forests);
	}

	@Override
	public void setFormat(Format format) {
		if (format != Format.XML) {
            new RuntimeException("QueryOptionsHandle supports the XML format only");
        }
	}

	public void setFragmentScope(FragmentScope fragmentScope) {
		optionsHolder.setFragmentScope(fragmentScope);

	}

	public void setGrammar(QueryGrammar grammar) {
		optionsHolder.setGrammar(grammar);
	}

	public void setOperators(List<QueryOperator> operatorOptions) {
		optionsHolder.setOperators(operatorOptions);
	}

	public void setPageLength(Long pageLength) {
		optionsHolder.setPageLength(pageLength);

	}

	public void setQualityWeight(Double qualityWeight) {
		optionsHolder.setQualityWeight(qualityWeight);

	}

	public void setQueryValues(List<QueryValues> values) {
		optionsHolder.setQueryValues(values);
	}
	
	public void setReturnAggregates(Boolean returnAggregates) {
		optionsHolder.setReturnAggregates(returnAggregates);
	}
	
	public void setReturnConstraints(Boolean returnConstraints) {
		optionsHolder.setReturnConstraints(returnConstraints);

	}

	public void setReturnFacets(Boolean returnFacets) {
		optionsHolder.setReturnFacets(returnFacets);
	}

	public void setReturnFrequencies(Boolean returnFrequencies) {
		optionsHolder.setReturnFrequencies(returnFrequencies);
	}
	
	public void setReturnMetrics(Boolean returnMetrics) {
		optionsHolder.setReturnMetrics(returnMetrics);

	}
	public void setReturnQuery(Boolean returnQuery) {
		optionsHolder.setReturnQuery(returnQuery);
	}
	

	public void setReturnPlan(Boolean returnPlan) {
		optionsHolder.setReturnPlan(returnPlan);

	}

	public void setReturnQtext(Boolean returnQtext) {
		optionsHolder.setReturnQtext(returnQtext);

	}

	public void setReturnResults(Boolean returnResults) {
		optionsHolder.setReturnResults(returnResults);

	}

	public void setReturnSimilar(Boolean returnSimilar) {
		optionsHolder.setReturnSimilar(returnSimilar);

	}

	public void setReturnValues(Boolean returnValues) {
		optionsHolder.setReturnValues(returnValues);
	}

	public void setSearchableExpression(org.w3c.dom.Element searchableExpression) {
		optionsHolder.setSearchableExpression(searchableExpression);

	}


	public void setSearchOptions(List<String> searchOptions) {
		optionsHolder.setSearchOptions(searchOptions);

	}


	public void setSortOrders(List<QuerySortOrder> sortOrders) {
		optionsHolder.setSortOrders(sortOrders);
	}
	

	public void setSuggestionSources(
			List<QuerySuggestionSource> suggestionSourceOptions) {
		optionsHolder.setSuggestionSources(suggestionSourceOptions);
	}
	

	public void setTerm(QueryTerm termConfig) {
		optionsHolder.setTerm(termConfig);
	}
	
	public void setTransformResults(QueryTransformResults transformResultsOption) {
		optionsHolder.setTransformResults(transformResultsOption);

	}

	public String toXMLString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			this.write(baos);
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		}
		return baos.toString();
	}
	
	
	public void write(OutputStream out) throws IOException {
		JAXBElement<QueryOptions> jaxbElement = new JAXBElement<QueryOptions>(
				new QName("http://marklogic.com/appservices/search", "options"),
				QueryOptions.class, optionsHolder);
		try {
			marshaller.marshal(jaxbElement, out);
		} catch (JAXBException e) {
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
	
	@Override
	protected Class<InputStream> receiveAs() {
		return InputStream.class;
	}

	@Override
	protected void receiveContent(InputStream content) {
		try {
			optionsHolder = (QueryOptions) unmarshaller.unmarshal(content);
		} catch (JAXBException e) {
			
			throw new MarkLogicBindingException(e);
		}
	}
	
	protected OutputStreamSender sendContent() {
		return this;
	}

}
