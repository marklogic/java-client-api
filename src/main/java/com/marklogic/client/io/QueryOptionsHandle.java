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
import java.util.ArrayList;
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
import com.marklogic.client.configpojos.Constraint;
import com.marklogic.client.configpojos.ConstraintDefinition;
import com.marklogic.client.configpojos.DefaultSuggestionSource;
import com.marklogic.client.configpojos.Grammar;
import com.marklogic.client.configpojos.Operator;
import com.marklogic.client.configpojos.Options;
import com.marklogic.client.configpojos.Range;
import com.marklogic.client.configpojos.SortOrder;
import com.marklogic.client.configpojos.SuggestionSource;
import com.marklogic.client.configpojos.Term;
import com.marklogic.client.configpojos.TransformResults;
import com.marklogic.client.io.marker.QueryOptionsReadHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;

public final class QueryOptionsHandle extends
		BaseHandle<InputStream, OutputStreamSender> implements
		OutputStreamSender, QueryOptionsReadHandle, QueryOptionsWriteHandle {

	@SuppressWarnings("unused")
	static final private Logger logger = LoggerFactory
			.getLogger(QueryOptionsHandle.class);

	private Options optionsHolder;
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
	public void receiveContent(InputStream content) {
		try {
			optionsHolder = (Options) unmarshaller.unmarshal(content);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};

	public void write(OutputStream out) throws IOException {
		JAXBElement<Options> jaxbElement = new JAXBElement<Options>(new QName(
				"http://marklogic.com/appservices/search", "options"),
				Options.class, optionsHolder);
		try {
			marshaller.marshal(jaxbElement, out);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public OutputStreamSender sendContent() {
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
	public Boolean isReturnFacets() {
		return returnWithDefault(optionsHolder.isReturnFacets(), true);
	}

	public Boolean isReturnMetrics() {
		return returnWithDefault(optionsHolder.isReturnMetrics(), true);
	}

	public Boolean isReturnQtext() {
		return returnWithDefault(optionsHolder.isReturnQtext(), false);
	}

	public Boolean isReturnQuery() {
		return returnWithDefault(optionsHolder.isReturnQuery(), false);
	}

	public Boolean isReturnResults() {
		return returnWithDefault(optionsHolder.isReturnResults(), true);
	}

	public Boolean isReturnSimilar() {
		return returnWithDefault(optionsHolder.isReturnSimilar(), false);
	}

	public Boolean isDebug() {
		return returnWithDefault(optionsHolder.isDebug(), false);
	}

	public Boolean isReturnConstraints() {
		return returnWithDefault(optionsHolder.isReturnConstraints(), false);
	}

	public Boolean isReturnPlan() {
		return returnWithDefault(optionsHolder.isReturnPlan(), false);
	}

	public String getSearchableExpression() {
		return optionsHolder.getSearchableExpression();
	}

	public Term getTerm() {
		return optionsHolder.getTerm();
	}

	public TransformResults getTransformResults() {
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

	public List<Operator> getOperators() {
		return optionsHolder.getOperators();
	}

	public List<SortOrder> getSortOrders() {
		return optionsHolder.getSortOrders();
	}

	public List<SuggestionSource> getSuggestionSources() {
		return optionsHolder.getSuggestionSources();
	}

	public Element getAdditionalQuery() {
		return optionsHolder.getAdditionalQuery();
	}

	public Grammar getGrammar() {
		return optionsHolder.getGrammar();
	}

	/*
	 * Fluent setters
	 */
	public QueryOptionsHandle withAdditionalQuery(Element ctsQuery) {
		optionsHolder.setAdditionalQuery(ctsQuery);
		return this;
	};

	public QueryOptionsHandle withConcurrencyLevel(Integer concurrencyLevel) {
		optionsHolder.setConcurrencyLevel(concurrencyLevel);
		return this;
	}

	public QueryOptionsHandle withDebug(Boolean debug) {
		optionsHolder.setDebug(debug);
		return this;
	}

	public QueryOptionsHandle withForests(List<Long> forests) {
		optionsHolder.setForests(forests);
		return this;
	}

	public QueryOptionsHandle withForest(Long forest) {
		List<Long> forests = new ArrayList<Long>();
		forests.add(forest);
		optionsHolder.setForests(forests);
		return this;
	}

	@Override
	public void setFormat(Format format) {
		if (format != Format.XML)
			new RuntimeException(
					"QueryOptionsHandle supports the XML format only");
	}

	public QueryOptionsHandle withFragmentScope(String fragmentScope) {
		optionsHolder.setFragmentScope(fragmentScope);
		return this;
	}

	public QueryOptionsHandle withPageLength(Long pageLength) {
		optionsHolder.setPageLength(pageLength);
		return this;
	}

	public QueryOptionsHandle withQualityWeight(Double qualityWeight) {
		optionsHolder.setQualityWeight(qualityWeight);
		return this;
	}

	public QueryOptionsHandle withReturnConstraints(Boolean returnConstraints) {
		optionsHolder.setReturnConstraints(returnConstraints);
		return this;
	}

	public QueryOptionsHandle withReturnFacets(Boolean returnFacets) {
		optionsHolder.setReturnFacets(returnFacets);
		return this;
	}

	public QueryOptionsHandle withReturnMetrics(Boolean returnMetrics) {
		optionsHolder.setReturnMetrics(returnMetrics);
		return this;
	}

	public QueryOptionsHandle withReturnPlan(Boolean returnPlan) {
		optionsHolder.setReturnPlan(returnPlan);
		return this;
	}

	public QueryOptionsHandle withReturnQueryText(Boolean returnQueryText) {
		optionsHolder.setReturnQtext(returnQueryText);
		return this;
	}

	public QueryOptionsHandle withReturnResults(Boolean returnResults) {
		optionsHolder.setReturnResults(returnResults);
		return this;
	}

	public QueryOptionsHandle withReturnSimilar(Boolean returnSimilar) {
		optionsHolder.setReturnSimilar(returnSimilar);
		return this;

	}

	public QueryOptionsHandle withSearchableExpression(
			String searchableExpression) {
		optionsHolder.setSearchableExpression(searchableExpression);
		return this;
	}

	public QueryOptionsHandle withSearchOptions(List<String> searchOptions) {
		optionsHolder.setSearchOptions(searchOptions);
		return this;
	}

	public QueryOptionsHandle withTransformResults(
			TransformResults transformResults) {
		optionsHolder.setTransformResults(transformResults);
		return this;
	}

	public String toString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			write(baos);
		} catch (IOException e) {
			throw new MarkLogicIOException(
					"Failed to make String representation of QueryOptionsHandle",
					e);
		}
		return baos.toString();
	}

	public QueryOptionsHandle() {
		optionsHolder = new Options();

		try {
			jc = JAXBContext.newInstance(Options.class);
			marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			unmarshaller = jc.createUnmarshaller();
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",
					new SearchPrefixMapper());
		} catch (JAXBException e) {
			throw new MarkLogicBindingException(e);
		}

	}

	public Options getOptions() {
		return optionsHolder;
	}

	public QueryOptionsHandle with(QueryOptionsHandle options) {
		this.optionsHolder = options.optionsHolder;
		return this;
	}

	public <T extends ConstraintDefinition<T>> List<T> getConstraintDefinitionsByClassName(
			Class<T> class1) {
		List<T> options = new ArrayList<T>();
		for (Constraint constraint : optionsHolder.getConstraints()) {
			T impl = constraint.getConstraintDefinition();
			if (impl != null && impl.getClass() == class1) {
				options.add(impl);
			}
		}
		return options;
	}

	public List<Constraint> getConstraints() {
		return optionsHolder.getConstraints();
	}

	public QueryOptionsHandle withConstraint(Constraint constraint) {
		optionsHolder.getConstraints().add(constraint);
		return this;
	}

	public QueryOptionsHandle withSuggestionSource(SuggestionSource ss) {
		optionsHolder.getSuggestionSources().add(ss);
		return this;
	}

	public QueryOptionsHandle withDefaultSuggestionSource(DefaultSuggestionSource dss) {
		optionsHolder.setDefaultSuggestionSource(dss);
		return this;
	}

	/**
	 * Convenience method for adding children of constraints, with a constraint
	 * that has been embedded in the ConstraintDefinition object.
	 * 
	 * @param range
	 */
	// TODO review this handling of way constrain
	// TODO t
	public QueryOptionsHandle withConstraintDefinition(ConstraintDefinition constraintDefinition) {
		if (constraintDefinition.getConstraint() != null) {
			this.withConstraint(constraintDefinition.getConstraint());
		}if (constraintDefinition.getSuggestionSource() != null) {
			this.withSuggestionSource(constraintDefinition.getSuggestionSource());
		}
		return this;
	}

	public QueryOptionsHandle withOperator(Operator operator) {
		optionsHolder.getOperators().add(operator);
		return this;
	}

	public QueryOptionsHandle withGrammar(Grammar g) {
		optionsHolder.setGrammar(g);
		return this;
	}

	public QueryOptionsHandle withTerm(Term term) {
		optionsHolder.setTerm(term);
		return this;
	}

}
