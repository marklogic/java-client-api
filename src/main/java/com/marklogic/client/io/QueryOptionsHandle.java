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
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.config.Constraint;
import com.marklogic.client.config.Grammar;
import com.marklogic.client.config.JAXBBackedQueryOption;
import com.marklogic.client.config.MarkLogicBindingException;
import com.marklogic.client.config.Operator;
import com.marklogic.client.config.QueryOptions;
import com.marklogic.client.config.SortOrder;
import com.marklogic.client.config.Term;
import com.marklogic.client.config.TransformResults;
import com.marklogic.client.config.impl.JAXBHelper;
import com.marklogic.client.config.search.jaxb.AdditionalQuery;
import com.marklogic.client.config.search.jaxb.Options;
import com.marklogic.client.io.marker.QueryOptionsReadHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;

public class QueryOptionsHandle implements QueryOptionsReadHandle<InputStream>,
		QueryOptionsWriteHandle<OutputStreamSender>, QueryOptions,
		OutputStreamSender {
	@SuppressWarnings("unused")
	static final private Logger logger = LoggerFactory
			.getLogger(QueryOptionsHandle.class);
	protected JAXBContext jc = null;
	protected Unmarshaller unmarshaller = null;
	protected Marshaller m = null;
	private Options jaxbOptions = null;
	private com.marklogic.client.config.search.jaxb.AdditionalQuery additionalQuery;

	public QueryOptionsHandle() {
		this.jaxbOptions = new Options();
		additionalQuery = new com.marklogic.client.config.search.jaxb.AdditionalQuery();
	}

	@Override
	public void add(JAXBBackedQueryOption jAXBBackedQueryOption) {
		jaxbOptions.getSearchOptions().add(jAXBBackedQueryOption.asJAXB());
	}

	@Override
	public Object asJAXB() {
		return jaxbOptions;
	}

	public QueryOptions get() {
		return this;
	}

	@Override
	public Element getAdditionalQuery() {
		return additionalQuery.getValue().getAny();
	}

	@Override
	public Integer getConcurrencyLevel() {
		return (Integer) JAXBHelper.getOneSimpleByElementName(this,
				"concurrency-level");
	}

	@Override
	public List<Constraint> getConstraints() {
		return JAXBHelper.getByClassName(this, Constraint.class);
	}

	@Override
	public Boolean getDebug() {
		return (Boolean) JAXBHelper.getOneSimpleByElementName(this, "debug");
	}

	@Override
	public List<Long> getForests() {
		return JAXBHelper.getSimpleByElementName(this, "forest");
	}

	@Override
	public Format getFormat() {
		return Format.XML;
	}

	@Override
	public String getFragmentScope() {
		return JAXBHelper.getOneSimpleByElementName(this, "fragment-scope");
	};

	@Override
	public Grammar getGrammar() {
		return JAXBHelper.getOneByClassName(this, Grammar.class);
	}

	@Override
	public List<Object> getJAXBChildren() {
		return jaxbOptions.getSearchOptions();
	}

	@Override
	public List<Operator> getOperators() {
		return JAXBHelper.getByClassName(this, Operator.class);
	};

	@Override
	public Long getPageLength() {
		return (Long) JAXBHelper.getOneSimpleByElementName(this, "page-length");
	};

	@Override
	public Double getQualityWeight() {
		return (Double) JAXBHelper.getOneSimpleByElementName(this,
				"quality-weight");
	};

	private Boolean returnWithDefault(Boolean returnValue, Boolean defaultValue) {
		if (returnValue == null) {
			return defaultValue;
		} else {
			return returnValue;
		}
	}

	@Override
	public Boolean getReturnConstraints() {
		return returnWithDefault(
				(Boolean) JAXBHelper.getOneSimpleByElementName(this,
						"return-constraints"), false);
	}

	@Override
	public Boolean getReturnFacets() {
		return returnWithDefault(
				(Boolean) JAXBHelper.getOneSimpleByElementName(this,
						"return-facets"), true);
	};

	@Override
	public Boolean getReturnMetrics() {
		return returnWithDefault(
				(Boolean) JAXBHelper.getOneSimpleByElementName(this,
						"return-metrics"), true);
	};

	@Override
	public Boolean getReturnPlan() {
		return returnWithDefault(
				(Boolean) JAXBHelper.getOneSimpleByElementName(this,
						"return-plan"), false);
	};

	@Override
	public Boolean getReturnQText() {
		return returnWithDefault(
				(Boolean) JAXBHelper.getOneSimpleByElementName(this,
						"return-qtext"), false);
	};

	@Override
	public Boolean getReturnResults() {
		return returnWithDefault(
				(Boolean) JAXBHelper.getOneSimpleByElementName(this,
						"return-results"), true);
	}

	@Override
	public Boolean getReturnSimilar() {
		return returnWithDefault(
				(Boolean) JAXBHelper.getOneSimpleByElementName(this,
						"return-similar"), false);
	}

	public String getSearchableExpression() {
		return (String) JAXBHelper.getOneSimpleByElementName(this,
				"searchable-expression");
	};

	@Override
	public List<String> getSearchOptions() {
		return JAXBHelper.getSimpleByElementName(this, "search-option");

	};

	@Override
	public List<SortOrder> getSortOrders() {
		return JAXBHelper.getByClassName(this, SortOrder.class);
	};

	@Override
	public Term getTerm() {
		return JAXBHelper.getOneByClassName(this, Term.class);
	};

	@Override
	public TransformResults getTransformResults() {
		return JAXBHelper.getOneByClassName(this, TransformResults.class);
	};

	public QueryOptionsHandle on(QueryOptions options) {
		set(options);
		return this;
	};

	@Override
	public Class<InputStream> receiveAs() {
		return InputStream.class;
	};

	@SuppressWarnings("unchecked")
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
			additionalQuery = (AdditionalQuery) JAXBHelper
					.getOneJAXBByElementName(this, "additional-query");
		} catch (JAXBException e) {
			throw new MarkLogicIOException(
					"Could not construct search results because of thrown JAXB Exception",
					e);
		}
	};

	public OutputStreamSender sendContent() {
		return this;
	};

	public void set(QueryOptions options) {
		this.jaxbOptions = (Options) options.asJAXB();
	};

	@Override
	public void setAdditionalQuery(Element ctsQuery) {
		additionalQuery.getValue().setAny(ctsQuery);
	};

	@Override
	public void setConcurrencyLevel(Integer concurrencyLevel) {
		JAXBHelper.setOneSimpleByElementName(this, "concurrency-level",
				concurrencyLevel);
	}

	@Override
	public void setDebug(Boolean debug) {
		JAXBHelper.setOneSimpleByElementName(this, "debug", debug);
	}

	@Override
	public void setForest(Long forest) {
		JAXBHelper.setOneSimpleByElementName(this, "forest", forest);
	}

	@Override
	public void setForests(List<Long> forests) {
		JAXBHelper.setSimpleByElementName(this, "forest", forests);
	}

	@Override
	public void setFormat(Format format) {
		if (format != Format.XML)
			new RuntimeException(
					"QueryOptionsHandle supports the XML format only");
	}

	@Override
	public void setFragmentScope(String fragmentScope) {
		JAXBHelper.setOneSimpleByElementName(this, "fragment-scope",
				fragmentScope);
	}

	@Override
	public void setPageLength(Long pageLength) {
		JAXBHelper.setOneSimpleByElementName(this, "page-length", pageLength);

	}

	@Override
	public void setQualityWeight(Double qualityWeight) {
		JAXBHelper.setOneSimpleByElementName(this, "quality-weight",
				qualityWeight);
	}

	@Override
	public void setReturnConstraints(Boolean returnConstraints) {
		JAXBHelper.setOneSimpleByElementName(this, "return-constraints",
				returnConstraints);
	}

	@Override
	public void setReturnFacets(Boolean returnFacets) {
		JAXBHelper.setOneSimpleByElementName(this, "return-facets",
				returnFacets);
	}

	@Override
	public void setReturnMetrics(Boolean returnMetrics) {
		JAXBHelper.setOneSimpleByElementName(this, "return-metrics",
				returnMetrics);
	}

	@Override
	public void setReturnPlan(Boolean returnPlan) {
		JAXBHelper.setOneSimpleByElementName(this, "return-plan", returnPlan);
	}

	@Override
	public void setReturnQueryText(Boolean returnQueryText) {
		JAXBHelper.setOneSimpleByElementName(this, "return-qtext",
				returnQueryText);
	}

	@Override
	public void setReturnResults(Boolean returnResults) {
		JAXBHelper.setOneSimpleByElementName(this, "return-results",
				returnResults);
	}

	@Override
	public void setReturnSimilar(Boolean returnSimilar) {
		JAXBHelper.setOneSimpleByElementName(this, "return-similar",
				returnSimilar);

	}

	public void setSearchableExpression(String searchableExpression) {
		JAXBHelper.setOneSimpleByElementName(this, "searchable-expression",
				searchableExpression);
	}

	@Override
	public void setSearchOptions(List<String> searchOptions) {
		JAXBHelper.setSimpleByElementName(this, "search-option", searchOptions);
	}

	@Override
	public void setTransformResults(TransformResults transformResults) {
		JAXBHelper.setOneByClassName(this, transformResults);
	}

	public String toString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			write(baos);
		} catch (IOException e) {
			throw new MarkLogicIOException(
					"Failed to make String representation of QueryOptions", e);
		}
		return baos.toString();
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

	@Override
	public void addQueryAnnotation(Element annotation) {
		
	}

	@Override
	public List<Element> getQueryAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

}
