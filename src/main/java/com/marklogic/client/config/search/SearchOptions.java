package com.marklogic.client.config.search;

import java.io.ByteArrayOutputStream;
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

import com.marklogic.client.config.search.jaxb.Options;
import com.marklogic.client.config.search.jaxb.Range;
import com.marklogic.client.config.search.jaxb.Value;
import com.marklogic.client.config.search.jaxb.Word;

public class SearchOptions {

	protected JAXBContext jc;
	protected Unmarshaller unmarshaller;
	protected Marshaller m;

	private Options jaxbOptions;

	Logger logger = (Logger) LoggerFactory.getLogger(SearchOptions.class);

	private List<Constraint> constraints;

	public SearchOptions() {
		this.jaxbOptions = new Options();
		try {
			jc = JAXBContext
					.newInstance("com.marklogic.client.config.search.jaxb");
			unmarshaller = jc.createUnmarshaller();
			m = jc.createMarshaller();
		} catch (JAXBException e) {
			throw new MarkLogicIOException(
					"Could not initialize Java Binding because of thrown JAXB Exception",
					e);
		}

	};

	public SearchOptions(InputStream is) throws JAXBException {
		this();
		@SuppressWarnings("unchecked")
		JAXBElement<Options> jaxb = (JAXBElement<Options>) unmarshaller
				.unmarshal(is);
		jaxbOptions = (Options) jaxb.getValue();
	}

	public void writeTo(OutputStream os) throws JAXBException {
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(this.jaxbOptions, os);
	}

	public String toString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			this.writeTo(baos);
		} catch (JAXBException e) {
			throw new MarkLogicIOException(
					"Could not serialize XML from java due to thrown JAXBException.",
					e);
		}
		return baos.toString();
	}

	public List<SearchOption> getAll() {
		List<SearchOption> options = new ArrayList<SearchOption>();
		for (Object ot : this.jaxbOptions.getQueryOptions()) {
			if (ot.getClass().getPackage().toString()
					.contains("com.marklogic.client.config.search.jaxb")) {
				logger.debug("Instantiating POJO class to wrap");
				SearchOption newOption = this.newQueryOption(ot);
				// add all options that are handled by POJOs to the return
				// lists.
				if (newOption != null) {
					options.add(this.newQueryOption(ot));
				}
			} else {
				// TODO nothing?
			}
		}
		return options;
	};

	private SearchOption newQueryOption(Object ot) {
		logger.debug("Making new query option for object of class "
				+ ot.getClass().getName());
		Class clazz = ot.getClass();
		if (clazz == com.marklogic.client.config.search.jaxb.Term.class) {
			return new Term((com.marklogic.client.config.search.jaxb.Term) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.Grammar.class) {
			return new Grammar(
					(com.marklogic.client.config.search.jaxb.Grammar) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.Constraint.class) {
			com.marklogic.client.config.search.jaxb.Constraint constraint = (com.marklogic.client.config.search.jaxb.Constraint) ot;
			Object constraintSpec = constraint.getConstraint().get(0);
			if (constraintSpec.getClass() == com.marklogic.client.config.search.jaxb.Range.class) {
				return new RangeConstraint(constraint.getName(),
						(Range) constraintSpec);
			} else if (constraintSpec.getClass() == com.marklogic.client.config.search.jaxb.Value.class) {
				return new ValueConstraint(constraint.getName(),
						(Value) constraintSpec);
			} else if (constraintSpec.getClass() == com.marklogic.client.config.search.jaxb.Word.class) {
				return new WordConstraint(constraint.getName(),
						(Word) constraintSpec);
			}
		} else if (clazz == com.marklogic.client.config.search.jaxb.Operator.class) {
			return new Operator(
					(com.marklogic.client.config.search.jaxb.Operator) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.TransformResults.class) {
			return new TransformResults(
					(com.marklogic.client.config.search.jaxb.TransformResults) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.DefaultSuggestionSource.class) {
			return new DefaultSuggestionSource(
					(com.marklogic.client.config.search.jaxb.DefaultSuggestionSource) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.SuggestionSource.class) {
			return new SuggestionSource(
					(com.marklogic.client.config.search.jaxb.SuggestionSource) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.Annotation.class) {
			return new Annotation(
					(com.marklogic.client.config.search.jaxb.Annotation) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.SortOrder.class) {
			return new SortOrder(
					(com.marklogic.client.config.search.jaxb.SortOrder) ot);
		} else if (clazz == javax.xml.bind.JAXBElement.class) {

		} else {
			throw new MarkLogicUnhandledElementException(ot.getClass()
					.getName());
		}
		return null;

	};

	@SuppressWarnings("unchecked")
	public boolean getReturnFacets() {
		List<Object> opts = this.getUnboundOptions("return-facets");
		if (opts.size() == 0) {
			return false;
		} else {
			return ((JAXBElement<Boolean>) opts.get(0)).getValue();
		}
	};

	private List<Object> getUnboundOptions(String localName) {
		List<Object> options = this.jaxbOptions.getQueryOptions();
		List<Object> conformingOptions = new ArrayList<Object>();
		for (Object option : options) {
			if (option.getClass() == javax.xml.bind.JAXBElement.class) {
				JAXBElement jaxbElement = (JAXBElement<Object>) option;
				if (jaxbElement.getName().equals(
						new QName("http://marklogic.com/appservices/search",
								localName))) {
					conformingOptions.add(option);
				}
			}
		}
		return conformingOptions;
	}

	public void setReturnFacets(boolean returnFacets) {
		List<Object> existingReturnFacetsList = this
				.getUnboundOptions("return-facets");
		if (existingReturnFacetsList.size() == 0) {
			JAXBElement<Boolean> newElement = new JAXBElement<Boolean>(
					new QName("http://marklogic.com/appservices/search",
							"return-facets"), Boolean.class, new Boolean(
							returnFacets));
			logger.debug("Here is the new element " + newElement.getName()
					+ " and its value " + newElement.getValue());
			this.jaxbOptions.getQueryOptions().add(newElement);
		} else {
			JAXBElement<Boolean> existingReturnFacets = (JAXBElement<Boolean>) existingReturnFacetsList
					.get(0);
			existingReturnFacets.setValue(returnFacets);
		}
	};
	/*
	 * public boolean getReturnConstraints() { ... }; public void
	 * setReturnConstraints(boolean returnFacets) { ... }; public boolean
	 * getReturnMetrics() { ... }; public void setReturnMetrics(boolean
	 * returnFacets) { ... }' public boolean getReturnPlan() { ... }; public
	 * void setReturnPlan(boolean returnFacets) { ... }; public boolean
	 * getReturnQText() { ... }; public void setReturnQText(boolean
	 * returnFacets) { ... }; public boolean getReturnResults() { ... }; public
	 * void setReturnResults(boolean returnFacets) { ... }; public boolean
	 * getReturnSimilar() { ... }; public void setReturnSimilar(boolean
	 * returnSimilar) { ... };
	 * 
	 * 
	 * public boolean getDebug() { ... }; public void setDebug(boolean debug) {
	 * ... }; public int getConcurrencyLevel() { ... }; public void
	 * setConcurrencyLevel(int concurrencyLevel) { ... }; public String
	 * getFragmentScope() { ... }; public void setFragmentScope(String
	 * fragmentScope) { ... }; public int getConcurrencyLevel() { ... }; public
	 * void setConcurrencyLevel(int concurrencyLevel) { ... }; public long
	 * getPageLength() { ... }; public void setPageLength(long pageLength) { ...
	 * }; public double getQualityWeight() { ... }; public void
	 * setQualityWeight(double qualityWeight) { ... };
	 */
}
