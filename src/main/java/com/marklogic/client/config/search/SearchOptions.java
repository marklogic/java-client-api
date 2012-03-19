package com.marklogic.client.config.search;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.security.action.GetBooleanAction;

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
		@SuppressWarnings("rawtypes")
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

	private List<Object> getUnboundOptions(String localName) {
		List<Object> options = this.jaxbOptions.getQueryOptions();
		List<Object> conformingOptions = new ArrayList<Object>();
		for (Object option : options) {
			if (option.getClass() == javax.xml.bind.JAXBElement.class) {
				@SuppressWarnings("unchecked")
				JAXBElement<Object> jaxbElement = (JAXBElement<Object>) option;
				if (jaxbElement.getName().equals(
						new QName("http://marklogic.com/appservices/search",
								localName))) {
					conformingOptions.add(option);
				}
			}
		}
		return conformingOptions;
	}

	@SuppressWarnings("unchecked")
	private Object getAtomicOption(String optionName) {
		List<Object> opts = this.getUnboundOptions(optionName);
		if (opts.size() == 0) {
			return false;
		} else {
			return ((JAXBElement<Object>) opts.get(0)).getValue();
		}
	}

	@SuppressWarnings("unchecked")
	private String getStringOption(String optionName) {
		List<Object> opts = this.getUnboundOptions(optionName);
		if (opts.size() == 0) {
			return null;
		} else {
			return ((JAXBElement<String>) opts.get(0)).getValue();
		}
	}

	private void setAtomicOption(String optionName, Object value, Class clazz) {
		List<Object> existingFlagAsList = this.getUnboundOptions(optionName);
		if (existingFlagAsList.size() == 0) {
			JAXBElement<Object> newElement = new JAXBElement<Object>(new QName(
					"http://marklogic.com/appservices/search", optionName),
					clazz, value);
			logger.debug("Here is the new element " + newElement.getName()
					+ " and its value " + newElement.getValue());
			this.jaxbOptions.getQueryOptions().add(newElement);
		} else {
			@SuppressWarnings("unchecked")
			JAXBElement<Object> existingOption = (JAXBElement<Object>) existingFlagAsList
					.get(0);
			existingOption.setValue(value);
		}
	}

	/*
	 * private void setStringOption(String optionName, String value) {
	 * List<Object> existingOptionsAsList = this.getUnboundOptions(optionName);
	 * if (existingOptionsAsList.size() == 0) { JAXBElement<String> newElement =
	 * new JAXBElement<String>( new
	 * QName("http://marklogic.com/appservices/search", optionName),
	 * String.class, new String(value)); logger.debug("Here is the new element "
	 * + newElement.getName() + " and its value " + newElement.getValue());
	 * this.jaxbOptions.getQueryOptions().add(newElement); } else {
	 * 
	 * @SuppressWarnings("unchecked") JAXBElement<String> existingOption =
	 * (JAXBElement<String>) existingOptionsAsList .get(0);
	 * existingOption.setValue(value); } }
	 */

	/*********************************************
	 * Methods for accessing simple atomic options
	 ********************************************* 
	 */

	public boolean getReturnFacets() {
		return (Boolean) getAtomicOption("return-facets");
	};

	public void setReturnFacets(boolean returnFacets) {
		setAtomicOption("return-facets", returnFacets, Boolean.class);
	}

	public void add(SearchOption searchOption) {
		jaxbOptions.getQueryOptions().add(searchOption.asJaxbObject());
	}

	public boolean getReturnConstraints() {
		return (Boolean) getAtomicOption("return-constraints");
	};

	public void setReturnConstraints(boolean returnConstraints) {
		setAtomicOption("return-constraints", returnConstraints,
				Boolean.class);
	};

	public boolean getReturnMetrics() {
		return (Boolean) getAtomicOption("return-metrics");
	};

	public void setReturnMetrics(boolean returnMetrics) {
		setAtomicOption("return-metrics", returnMetrics, Boolean.class);
	}

	public boolean getReturnPlan() {
		return (Boolean) getAtomicOption("return-plan");
	};

	public void setReturnPlan(boolean returnPlan) {
		setAtomicOption("return-plan", returnPlan, Boolean.class);
	};

	public boolean getReturnQText() {
		return (Boolean) getAtomicOption("return-qtext");
	};

	public void setReturnQueryText(boolean returnQueryText) {
		setAtomicOption("return-qtext", returnQueryText, Boolean.class);
	};

	public boolean getReturnResults() {
		return (Boolean) getAtomicOption("return-results");
	};

	public void setReturnResults(boolean returnResults) {
		setAtomicOption("return-results", returnResults, Boolean.class);
	};

	public boolean getReturnSimilar() {
		return (Boolean) getAtomicOption("return-similar");
	};

	public void setReturnSimilar(boolean returnSimilar) {
		setAtomicOption("return-similar", returnSimilar, Boolean.class);

	};

	public boolean getDebug() {
		return (Boolean) getAtomicOption("debug");
	};

	public void setDebug(boolean debug) {
		setAtomicOption("debug", debug, Boolean.class);
	};

	public String getFragmentScope() {
		return (String) getAtomicOption("fragment-scope");
	};

	public void setFragmentScope(String fragmentScope) {
		setAtomicOption("fragment-scope", fragmentScope, String.class);
	};

	public int getConcurrencyLevel() {
		return (Integer) getAtomicOption("concurrency-level");
	};

	public void setConcurrencyLevel(int concurrencyLevel) {
		setAtomicOption("concurrency-level", concurrencyLevel, Integer.class);
	};

	public long getPageLength() {
		return (Long) getAtomicOption("page-length");
	};

	public void setPageLength(long pageLength) {
		setAtomicOption("page-length", pageLength, Long.class);

	};

	public double getQualityWeight() {
		return (Double) getAtomicOption("quality-weight");
	};

	public void setQualityWeight(double qualityWeight) {
		setAtomicOption("quality-weight", qualityWeight, Double.class);
	};
}
