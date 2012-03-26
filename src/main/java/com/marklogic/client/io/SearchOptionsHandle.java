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
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.config.search.Constraint;
import com.marklogic.client.config.search.Grammar;
import com.marklogic.client.config.search.MarkLogicBindingException;
import com.marklogic.client.config.search.MarkLogicUnhandledElementException;
import com.marklogic.client.config.search.Operator;
import com.marklogic.client.config.search.SearchOption;
import com.marklogic.client.config.search.SearchOptions;
import com.marklogic.client.config.search.Term;
import com.marklogic.client.config.search.TransformResults;
import com.marklogic.client.config.search.impl.AdditionalQueryImpl;
import com.marklogic.client.config.search.impl.AnnotationImpl;
import com.marklogic.client.config.search.impl.CollectionConstraintImpl;
import com.marklogic.client.config.search.impl.CustomConstraintImpl;
import com.marklogic.client.config.search.impl.DefaultSuggestionSourceImpl;
import com.marklogic.client.config.search.impl.ElementQueryConstraintImpl;
import com.marklogic.client.config.search.impl.GrammarImpl;
import com.marklogic.client.config.search.impl.OperatorImpl;
import com.marklogic.client.config.search.impl.PropertiesConstraintImpl;
import com.marklogic.client.config.search.impl.RangeConstraintImpl;
import com.marklogic.client.config.search.impl.SortOrderImpl;
import com.marklogic.client.config.search.impl.SuggestionSourceImpl;
import com.marklogic.client.config.search.impl.TermImpl;
import com.marklogic.client.config.search.impl.TransformResultsImpl;
import com.marklogic.client.config.search.impl.ValueConstraintImpl;
import com.marklogic.client.config.search.impl.WordConstraintImpl;
import com.marklogic.client.config.search.jaxb.Collection;
import com.marklogic.client.config.search.jaxb.Custom;
import com.marklogic.client.config.search.jaxb.ElementQuery;
import com.marklogic.client.config.search.jaxb.Options;
import com.marklogic.client.config.search.jaxb.Properties;
import com.marklogic.client.config.search.jaxb.Range;
import com.marklogic.client.config.search.jaxb.Value;
import com.marklogic.client.config.search.jaxb.Word;
import com.marklogic.client.io.marker.OutputStreamSender;
import com.marklogic.client.io.marker.SearchOptionsReadHandle;
import com.marklogic.client.io.marker.SearchOptionsWriteHandle;

public class SearchOptionsHandle implements
		SearchOptionsReadHandle<InputStream>,
		SearchOptionsWriteHandle<OutputStreamSender>, SearchOptions,
		OutputStreamSender {
	static final private Logger logger = LoggerFactory
			.getLogger(SearchOptionsHandle.class);
	protected JAXBContext jc = null;
	protected Unmarshaller unmarshaller = null;
	protected Marshaller m = null;
	private Options jaxbOptions = null;

	public SearchOptionsHandle() {
		this.jaxbOptions = new Options();
	}

	@Override
	public Format getFormat() {
		return Format.XML;
	}

	@Override
	public void setFormat(Format format) {
		if (format != Format.XML)
			new RuntimeException("SearchHandle supports the XML format only");
	}

	@Override
	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}

	@Override
	public void receiveContent(InputStream content) {
		try {
			jc = JAXBContext
					.newInstance("com.marklogic.client.config.search.jaxb");
			unmarshaller = jc.createUnmarshaller();
			m = jc.createMarshaller();
			JAXBElement<Options> jaxbElement = (JAXBElement<Options>) unmarshaller
					.unmarshal(content);
			jaxbOptions = jaxbElement.getValue();
		} catch (JAXBException e) {
			throw new MarkLogicIOException(
					"Could not construct search results because of thrown JAXB Exception",
					e);
		}
	}

	public OutputStreamSender sendContent() {
		return this;
	}

	public SearchOptions get() {
		return this;
	}

	public void set(SearchOptions options) {
		this.jaxbOptions = options.getJAXBContent();
	}

	public SearchOptionsHandle on(SearchOptions options) {
		set(options);
		return this;
	}

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

	@Override
	public void write(OutputStream out) throws IOException {
		try {
			jc = JAXBContext
					.newInstance("com.marklogic.client.config.search.jaxb");
			unmarshaller = jc.createUnmarshaller();
			m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
			m.marshal(this.jaxbOptions, out);
		} catch (PropertyException e) {
			throw new MarkLogicBindingException(e);
		} catch (JAXBException e) {
			throw new MarkLogicBindingException(e);
		}

	}

	public String toString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			write(baos);
		} catch (IOException e) {
			throw new MarkLogicIOException(
					"Failed to make String representation of SearchOptions", e);
		}
		return baos.toString();
	}

	@Override
	public Options getJAXBContent() {
		return jaxbOptions;
	}

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
				JAXBElement<Object> constraintElement = (JAXBElement<Object>) constraintChildren
						.get(i);

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

	}

	
	
	
	@Override
	public List<String> getSearchOptions() {
		List<String> l = new ArrayList<String>();
		for (SearchOption option : getAll()) {
			if (option instanceof JAXBElement<?>) {
				JAXBElement<String> e = (JAXBElement<String>) option;
				if (e.getName() == new QName("http://marklogic.com/appservices/search", "search-option")) {
					l.add(e.getValue());
				}
			}
		}
		return l;
	}

	@Override
	public List<Constraint> getConstraints() {
		List<Constraint> l = new ArrayList<Constraint>();

		for (SearchOption option : getAll()) {
			if (option instanceof Constraint) {
				l.add((Constraint) option);
			}
		}
		return l;
	}
	
	@Override
	public Term getTerm() {
		for (SearchOption option : getAll()) {
			if (option instanceof Term) {
				return (Term) option;
			}
		}
		return null;
	}

	@Override
	public Grammar getGrammar() {
		for (SearchOption option : getAll()) {
			if (option instanceof Grammar) {
				return (Grammar) option;
			}
		}
		return null;
	}
	
	@Override
	public List<Operator> getOperators() {
		List<Operator> l = new ArrayList<Operator>();

		for (SearchOption option : getAll()) {
			if (option instanceof Operator) {
				l.add((Operator) option);
			}
		}
		return l;
	}

	@Override
	public TransformResults getTransformResults() {
		for (SearchOption option : getAll()) {
			if (option instanceof TransformResults) {
				return (TransformResults) option;
			}
		}
		return null;
	};

}
