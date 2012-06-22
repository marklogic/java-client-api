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
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

import com.marklogic.client.EditableNamespaceContext;
import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicBindingException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.config.QueryOptions;
import com.marklogic.client.config.QueryOptions.FragmentScope;
import com.marklogic.client.config.QueryOptions.QueryAnnotation;
import com.marklogic.client.config.QueryOptions.QueryConstraint;
import com.marklogic.client.config.QueryOptions.QueryDefaultSuggestionSource;
import com.marklogic.client.config.QueryOptions.QueryExtractMetadata;
import com.marklogic.client.config.QueryOptions.QueryGrammar;
import com.marklogic.client.config.QueryOptions.QueryOperator;
import com.marklogic.client.config.QueryOptions.QuerySearchableExpression;
import com.marklogic.client.config.QueryOptions.QuerySortOrder;
import com.marklogic.client.config.QueryOptions.QuerySuggestionSource;
import com.marklogic.client.config.QueryOptions.QueryTerm;
import com.marklogic.client.config.QueryOptions.QueryTransformResults;
import com.marklogic.client.config.QueryOptions.QueryValues;
import com.marklogic.client.config.QueryOptionsBuilder.NamespaceBinding;
import com.marklogic.client.config.QueryOptionsBuilder.QueryOptionsItem;
import com.marklogic.client.impl.QueryOptionsTransformExtractNS;
import com.marklogic.client.impl.QueryOptionsTransformInjectNS;
import com.marklogic.client.io.marker.QueryOptionsReadHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;

/**
 * 
 * A QueryOptionsHandle is used to configure query configurations.
 * 
 * Use a QueryOptionsHandle if you want to use Java to configure and manage
 * MarkLogic query configurations, for search, value lookups, and facets.
 * 
 * Read an options node from MarkLogic with 
 * 
 * QueryOptionsHandle handle = QueryOptionsManager.readOptions(name, new QueryOptionsHandle());
 * 
 * or construct a fresh empty one (which is not a valid configuration without further building)
 * 
 * QueryOptionsHandle handle = new QueryOptionsHandle();
 *  
 * Build up options to a handle using 
 * 
 * handle.build()
 * 
 * and constructed items from QueryOptionsBuilder.
 * 
 */
public final class QueryOptionsHandle extends
		BaseHandle<InputStream, OutputStreamSender> implements
		OutputStreamSender, QueryOptionsReadHandle, QueryOptionsWriteHandle {

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
	 * Add more QueryOptionsItems to a QueryOptionsHandle using a QueryOptionsBuilder.
	 * 
	 * @param options 0 or more QueryOptionsItems
	 * @return the resulting updated QueryOptionsHandle
	 */
	public QueryOptionsHandle build(QueryOptionsItem... options) {
		for (QueryOptionsItem option : options) {
			if (logger.isDebugEnabled())
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

    public NamespaceContext getSearchableExpressionNamespaceContext() {
        return optionsHolder.getSearchableExpressionNamespaceContext();
    }

	public String getSearchableExpression() {
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

	public void setSearchableExpression(String searchableExpression) {
        //String sexml = "<searchable-expression xmlns=\"http://marklogic.com/appservices/search\"";
        EditableNamespaceContext context = optionsHolder.getSearchableExpressionNamespaceContext();
        List<NamespaceBinding> bindings = new ArrayList<NamespaceBinding>();
        for (String prefix : context.getAllPrefixes()) {
            if (!"".equals(prefix)) {
            	bindings.add(new NamespaceBinding(prefix, context.getNamespaceURI(prefix)));
                //sexml += " xmlns:" + prefix + "=\"" + context.getNamespaceURI(prefix) + "\"";
            }
        }
        //sexml += "/>";
        //org.w3c.dom.Element se = QueryOptionsBuilder.domElement(sexml);
        //se.setTextContent(searchableExpression);
        NamespaceBinding[] bindingsArray = (NamespaceBinding[]) bindings.toArray(new NamespaceBinding[] {});
        optionsHolder.setSearchableExpression(new QuerySearchableExpression(searchableExpression, bindingsArray));
	}

    public void setSearchableExpressionNamespaceContext(EditableNamespaceContext context) {
        optionsHolder.setSearchableExpressionNamespaceContext(context);
        String expr = optionsHolder.getSearchableExpression();
        if (expr != null) {
            setSearchableExpression(expr);
        }
    }

	public void setSearchOptions(List<String> searchOptions) {
		optionsHolder.setSearchOptions(searchOptions);

	}


	public void setSortOrders(List<QuerySortOrder> sortOrders) {
		optionsHolder.setSortOrders(sortOrders);
	}
	

	public void setSuggestionSources(
			List<QuerySuggestionSource> suggestionSources) {
		optionsHolder.setSuggestionSources(suggestionSources);
	}
	

	public void setTerm(QueryTerm termConfig) {
		optionsHolder.setTerm(termConfig);
	}
	
	public void setTransformResults(QueryTransformResults transformResults) {
		optionsHolder.setTransformResults(transformResults);

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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

		JAXBElement<QueryOptions> jaxbElement = new JAXBElement<QueryOptions>(
				new QName("http://marklogic.com/appservices/search", "options"),
				QueryOptions.class, optionsHolder);
		try {
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
        }
	}
	
	protected OutputStreamSender sendContent() {
		return this;
	}

	public QueryOperator getOperator(String name) {
		for (QueryOperator operator : optionsHolder.getQueryOperators()) {
			if (operator.getName().equals(name)) {
				return operator;
			}
		}
		return null;
	}

	public QueryExtractMetadata getExtractMetadata() {
		return optionsHolder.getExtractMetadata();
	}

	public void setExtractMetadata(QueryExtractMetadata extractMetadata) {
		optionsHolder.setExtractMetadata(extractMetadata);
	}
}
