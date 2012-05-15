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

import java.io.ByteArrayInputStream;
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
import com.marklogic.client.config.QueryOptionsBuilder;
import com.marklogic.client.config.QueryOptionsBuilder.FragmentScope;
import com.marklogic.client.config.QueryOptionsBuilder.QueryConstraint;
import com.marklogic.client.config.QueryOptionsBuilder.QueryGrammar;
import com.marklogic.client.config.QueryOptionsBuilder.QueryOperator;
import com.marklogic.client.config.QueryOptionsBuilder.QueryOptions;
import com.marklogic.client.config.QueryOptionsBuilder.QueryOptionsItem;
import com.marklogic.client.config.QueryOptionsBuilder.QuerySortOrder;
import com.marklogic.client.config.QueryOptionsBuilder.QuerySuggestionSource;
import com.marklogic.client.config.QueryOptionsBuilder.QueryTerm;
import com.marklogic.client.config.QueryOptionsBuilder.QueryTransformResults;
import com.marklogic.client.io.marker.QueryOptionsReadHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;

public final class QueryOptionsHandle extends
		BaseHandle<InputStream, OutputStreamSender> implements
		OutputStreamSender, QueryOptionsReadHandle, QueryOptionsWriteHandle {

	@SuppressWarnings("unused")
	static final private Logger logger = LoggerFactory
			.getLogger(QueryOptionsHandle.class);

	private QueryOptions optionsHolder;
	private QueryOptionsBuilder optionsBuilder;
	private JAXBContext jc;
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	@Override
	public Format getFormat() {
		return Format.XML;
	}

	@Override
	protected Class<InputStream> receiveAs() {
		return InputStream.class;
	};

	@Override
	protected void receiveContent(InputStream content) {
		try {
			optionsHolder = (QueryOptions) unmarshaller.unmarshal(content);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};

	public void write(OutputStream out) throws IOException {
		JAXBElement<QueryOptions> jaxbElement = new JAXBElement<QueryOptions>(
				new QName("http://marklogic.com/appservices/search", "options"),
				QueryOptions.class, optionsHolder);
		try {
			marshaller.marshal(jaxbElement, out);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected OutputStreamSender sendContent() {
		return this;
	};

	private Boolean returnWithDefault(Boolean returnValue, Boolean defaultValue) {
		if (returnValue == null) {
			return defaultValue;
		} else {
			return returnValue;
		}
	}

	/*
	 * Getters
	 */
	public Boolean getReturnFacets() {
		return returnWithDefault(optionsHolder.getReturnFacets(), true);
	}

	public Boolean getReturnMetrics() {
		return returnWithDefault(optionsHolder.getReturnMetrics(), true);
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

	public Boolean getDebug() {
		return returnWithDefault(optionsHolder.getDebug(), false);
	}

	public Boolean getReturnConstraints() {
		return returnWithDefault(optionsHolder.getReturnConstraints(), false);
	}

	public Boolean getReturnPlan() {
		return returnWithDefault(optionsHolder.getReturnPlan(), false);
	}

	public String getSearchableExpression() {
		return optionsHolder.getSearchableExpression();
	}

	public QueryTerm getTerm() {
		return optionsHolder.getTerm();
	}

	public QueryTransformResults getTransformResults() {
		return optionsHolder.getTransformResults();
	}

	public String getFragmentScope() {
		return optionsHolder.getFragmentScope();
	}

	public int getConcurrencyLevel() {
		return optionsHolder.getConcurrencyLevel();
	}

	public long getPageLength() {
		return optionsHolder.getPageLength();
	}

	public double getQualityWeight() {
		return optionsHolder.getQualityWeight();
	}

	public List<Long> getForests() {
		return optionsHolder.getForests();
	}

	public List<String> getSearchOptions() {
		return optionsHolder.getSearchOptions();
	}

	public List<QueryOperator> getOperators() {
		return optionsHolder.getQueryOperators();
	}

	public List<QuerySortOrder> getSortOrders() {
		return optionsHolder.getSortOrders();
	}

	public List<QuerySuggestionSource> getSuggestionSources() {
		return optionsHolder.getSuggestionSources();
	}

	public Element getAdditionalQuery() {
		return optionsHolder.getAdditionalQuery();
	}

	public QueryGrammar getGrammar() {
		return optionsHolder.getGrammar();
	}

	/*
	 * Bean Setters
	 */
	public void setAdditionalQuery(Element ctsQuery) {
		optionsHolder.setAdditionalQuery(ctsQuery);

	};

	public void setConcurrencyLevel(Integer concurrencyLevel) {
		optionsHolder.setConcurrencyLevel(concurrencyLevel);

	}

	public void setDebug(Boolean debug) {
		optionsHolder.setDebug(debug);

	}

	public void setForests(List<Long> forests) {
		optionsHolder.setForests(forests);

	}

	public void setFragmentScope(FragmentScope fragmentScope) {
		optionsHolder.setFragmentScope(fragmentScope);

	}

	public void setPageLength(Long pageLength) {
		optionsHolder.setPageLength(pageLength);

	}

	public void setQualityWeight(Double qualityWeight) {
		optionsHolder.setQualityWeight(qualityWeight);

	}

	public void setReturnConstraints(Boolean returnConstraints) {
		optionsHolder.setReturnConstraints(returnConstraints);

	}

	public void setReturnFacets(Boolean returnFacets) {
		optionsHolder.setReturnFacets(returnFacets);

	}

	public void setReturnMetrics(Boolean returnMetrics) {
		optionsHolder.setReturnMetrics(returnMetrics);

	}

	public void setReturnPlan(Boolean returnPlan) {
		optionsHolder.setReturnPlan(returnPlan);

	}

	public void setReturnQueryText(Boolean returnQueryText) {
		optionsHolder.setReturnQtext(returnQueryText);

	}

	public void setReturnResults(Boolean returnResults) {
		optionsHolder.setReturnResults(returnResults);

	}

	public void setReturnSimilar(Boolean returnSimilar) {
		optionsHolder.setReturnSimilar(returnSimilar);

	}

	public void setSearchableExpression(String searchableExpression) {
		optionsHolder.setSearchableExpression(searchableExpression);

	}

	public void setSearchOptions(List<String> searchOptions) {
		optionsHolder.setSearchOptions(searchOptions);

	}

	public void setTransformResults(QueryTransformResults transformResultsOption) {
		optionsHolder.setTransformResults(transformResultsOption);

	}


	@Override
	public void setFormat(Format format) {
		if (format != Format.XML)
			new RuntimeException(
					"QueryOptionsHandle supports the XML format only");
	}


	public QueryOptionsHandle() {
		// FIXME make static?
		optionsBuilder = new QueryOptionsBuilder();
		optionsHolder = optionsBuilder.options();

		try {
			jc = JAXBContext.newInstance(QueryOptions.class);
			marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			unmarshaller = jc.createUnmarshaller();
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",
					new SearchPrefixMapper());
		} catch (JAXBException e) {
			throw new MarkLogicBindingException(e);
		}

	}

	public QueryOptions getOptions() {
		return optionsHolder;
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
	
	
	public List<QueryConstraint> getConstraints() {
		return optionsHolder.getQueryConstraints();
	}

	public QueryConstraint getConstraint(String constraintName) {
		for (QueryConstraint constraintOption : getConstraints()) {
			if (constraintOption.getName().equals(constraintName)) {
				return constraintOption;
			}
		}
		return null;
	}
	
	/**
	 * Add more QueryOptionsItems to a QueryOptionsHandle using a QueryOptionsBuilder
	 * @param options 0 or more QueryOptionsItems
	 * @return the resulting updated QueryOptionsHandle
	 */
	public QueryOptionsHandle build(QueryOptionsItem... options) {
		for (QueryOptionsItem option : options) {
			option.build(optionsHolder);
		}
		return this;
	}

}
