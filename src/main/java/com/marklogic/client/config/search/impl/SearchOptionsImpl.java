package com.marklogic.client.config.search.impl;

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

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.config.search.MarkLogicUnhandledElementException;
import com.marklogic.client.config.search.SearchOption;
import com.marklogic.client.config.search.SearchOptions;
import com.marklogic.client.config.search.jaxb.Collection;
import com.marklogic.client.config.search.jaxb.Custom;
import com.marklogic.client.config.search.jaxb.ElementQuery;
import com.marklogic.client.config.search.jaxb.Options;
import com.marklogic.client.config.search.jaxb.Properties;
import com.marklogic.client.config.search.jaxb.Range;
import com.marklogic.client.config.search.jaxb.Value;
import com.marklogic.client.config.search.jaxb.Word;

public class SearchOptionsImpl implements SearchOptions {

	protected JAXBContext jc;
	protected Unmarshaller unmarshaller;
	protected Marshaller m;

	private Options jaxbOptions;

	Logger logger = (Logger) LoggerFactory.getLogger(SearchOptionsImpl.class);

	public SearchOptionsImpl() {
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

	public SearchOptionsImpl(InputStream is) throws JAXBException {
		this();
		@SuppressWarnings("unchecked")
		JAXBElement<Options> jaxb = (JAXBElement<Options>) unmarshaller
				.unmarshal(is);
		jaxbOptions = (Options) jaxb.getValue();
	}

	@Override
	public void writeTo(OutputStream os) throws JAXBException {
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(this.jaxbOptions, os);
	}

	@Override
	public String toString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			this.writeTo(baos);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new MarkLogicIOException(
					"Could not serialize XML from java due to thrown JAXBException.",
					e);
		}
		return baos.toString();
	}

	@Override
	public List<SearchOption> getAll() {
		List<SearchOption> options = new ArrayList<SearchOption>();
		for (Object ot : this.jaxbOptions.getSearchOptions()) {
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
			return new TermImpl(
					(com.marklogic.client.config.search.jaxb.Term) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.Grammar.class) {
			return new GrammarImpl(
					(com.marklogic.client.config.search.jaxb.Grammar) ot);
		} else if (ot instanceof com.marklogic.client.config.search.jaxb.Constraint) {
			com.marklogic.client.config.search.jaxb.Constraint constraint = (com.marklogic.client.config.search.jaxb.Constraint) ot;
			List<Object> constraintChildren = constraint.getConstraint();
			for (int i = 0; i < constraintChildren.size(); i++) {
				@SuppressWarnings("unchecked")
				JAXBElement<Object> constraintElement = (JAXBElement<Object>) constraintChildren.get(i);

				Object constraintSpec = constraintElement.getValue();
				logger.debug("Class of constraintSpec to dispatch "
						+ constraintSpec.getClass());
				if (constraintSpec instanceof com.marklogic.client.config.search.jaxb.Range) {
					return new RangeConstraintImpl(constraint,
							(Range) constraintSpec);
				} else if (constraintSpec instanceof com.marklogic.client.config.search.jaxb.Collection) {
					return new CollectionConstraintImpl(constraint,
							(Collection) constraintSpec);
				} else if (constraintSpec.getClass() == com.marklogic.client.config.search.jaxb.Value.class) {
					return new ValueConstraintImpl(constraint,
							(Value) constraintSpec);
				} else if (constraintSpec.getClass() == com.marklogic.client.config.search.jaxb.Word.class) {
					return new WordConstraintImpl(constraint,
							(Word) constraintSpec);
				} else if (constraintSpec instanceof com.marklogic.client.config.search.jaxb.ElementQuery) {
					return new ElementQueryConstraintImpl(constraint,
							(ElementQuery) constraintSpec);
				} else if (constraintSpec instanceof com.marklogic.client.config.search.jaxb.Properties) {
					return new PropertiesConstraintImpl(constraint,
							(Properties) constraintSpec);
				} else if (constraintSpec instanceof com.marklogic.client.config.search.jaxb.Custom) {
					return new CustomConstraintImpl(constraint,
							(Custom) constraintSpec);
				} else if (constraintSpec instanceof com.marklogic.client.config.search.jaxb.Annotation) {
					return new AnnotationImpl(
							(com.marklogic.client.config.search.jaxb.Annotation) constraintSpec);
				}
			}
		} else if (clazz == com.marklogic.client.config.search.jaxb.Operator.class) {
			return new OperatorImpl(
					(com.marklogic.client.config.search.jaxb.Operator) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.TransformResults.class) {
			return new TransformResultsImpl(
					(com.marklogic.client.config.search.jaxb.TransformResults) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.DefaultSuggestionSource.class) {
			return new DefaultSuggestionSourceImpl(
					(com.marklogic.client.config.search.jaxb.DefaultSuggestionSource) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.SuggestionSource.class) {
			return new SuggestionSourceImpl(
					(com.marklogic.client.config.search.jaxb.SuggestionSource) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.Annotation.class) {
			return new AnnotationImpl(
					(com.marklogic.client.config.search.jaxb.Annotation) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.SortOrder.class) {
			return new SortOrderImpl(
					(com.marklogic.client.config.search.jaxb.SortOrder) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.AdditionalQuery.class) {
			return new AdditionalQueryImpl(
					(com.marklogic.client.config.search.jaxb.AdditionalQuery) ot);
		} else if (clazz == javax.xml.bind.JAXBElement.class) {

		} else {
			throw new MarkLogicUnhandledElementException(ot.getClass()
					.getName());
		}
		return null;

	};

	private List<Object> getUnboundOptions(String localName) {
		List<Object> options = this.jaxbOptions.getSearchOptions();
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

	private void setAtomicOption(String optionName, Object value, Class clazz) {
		List<Object> existingFlagAsList = this.getUnboundOptions(optionName);
		if (existingFlagAsList.size() == 0) {
			JAXBElement<Object> newElement = new JAXBElement<Object>(new QName(
					"http://marklogic.com/appservices/search", optionName),
					clazz, value);
			logger.debug("Here is the new element " + newElement.getName()
					+ " and its value " + newElement.getValue());
			this.jaxbOptions.getSearchOptions().add(newElement);
		} else {
			@SuppressWarnings("unchecked")
			JAXBElement<Object> existingOption = (JAXBElement<Object>) existingFlagAsList
					.get(0);
			existingOption.setValue(value);
		}
	}

	/*********************************************
	 * Methods for accessing simple atomic options
	 ********************************************* 
	 */

	@Override
	public boolean getReturnFacets() {
		return (Boolean) getAtomicOption("return-facets");
	};

	@Override
	public void setReturnFacets(boolean returnFacets) {
		setAtomicOption("return-facets", returnFacets, Boolean.class);
	}

	@Override
	public void add(SearchOption searchOption) {
		jaxbOptions.getSearchOptions().add(searchOption.asJaxbObject());
	}

	@Override
	public boolean getReturnConstraints() {
		return (Boolean) getAtomicOption("return-constraints");
	};

	@Override
	public void setReturnConstraints(boolean returnConstraints) {
		setAtomicOption("return-constraints", returnConstraints, Boolean.class);
	};

	@Override
	public boolean getReturnMetrics() {
		return (Boolean) getAtomicOption("return-metrics");
	};

	@Override
	public void setReturnMetrics(boolean returnMetrics) {
		setAtomicOption("return-metrics", returnMetrics, Boolean.class);
	}

	@Override
	public boolean getReturnPlan() {
		return (Boolean) getAtomicOption("return-plan");
	};

	@Override
	public void setReturnPlan(boolean returnPlan) {
		setAtomicOption("return-plan", returnPlan, Boolean.class);
	};

	@Override
	public boolean getReturnQText() {
		return (Boolean) getAtomicOption("return-qtext");
	};

	@Override
	public void setReturnQueryText(boolean returnQueryText) {
		setAtomicOption("return-qtext", returnQueryText, Boolean.class);
	};

	@Override
	public boolean getReturnResults() {
		return (Boolean) getAtomicOption("return-results");
	};

	@Override
	public void setReturnResults(boolean returnResults) {
		setAtomicOption("return-results", returnResults, Boolean.class);
	};

	@Override
	public boolean getReturnSimilar() {
		return (Boolean) getAtomicOption("return-similar");
	};

	@Override
	public void setReturnSimilar(boolean returnSimilar) {
		setAtomicOption("return-similar", returnSimilar, Boolean.class);

	};

	@Override
	public boolean getDebug() {
		return (Boolean) getAtomicOption("debug");
	};

	@Override
	public void setDebug(boolean debug) {
		setAtomicOption("debug", debug, Boolean.class);
	};

	@Override
	public String getFragmentScope() {
		return (String) getAtomicOption("fragment-scope");
	};

	@Override
	public void setFragmentScope(String fragmentScope) {
		setAtomicOption("fragment-scope", fragmentScope, String.class);
	};

	@Override
	public int getConcurrencyLevel() {
		return (Integer) getAtomicOption("concurrency-level");
	};

	@Override
	public void setConcurrencyLevel(int concurrencyLevel) {
		setAtomicOption("concurrency-level", concurrencyLevel, Integer.class);
	};

	@Override
	public long getPageLength() {
		return (Long) getAtomicOption("page-length");
	};

	@Override
	public void setPageLength(long pageLength) {
		setAtomicOption("page-length", pageLength, Long.class);

	};

	@Override
	public double getQualityWeight() {
		return (Double) getAtomicOption("quality-weight");
	};

	@Override
	public void setQualityWeight(double qualityWeight) {
		setAtomicOption("quality-weight", qualityWeight, Double.class);
	}

	@Override
	public List<SearchOption> getByClassName(Class clazz) {
		List<SearchOption> toReturn = new ArrayList<SearchOption>();
		for (SearchOption option : getAll()) {
			if (option.getClass() == clazz) {
				toReturn.add(option);
			}
		}
		return toReturn;
	}

}
