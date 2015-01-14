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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.MarkLogicBindingException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.impl.ValuesBuilder;
import com.marklogic.client.io.marker.OperationNotSupported;
import com.marklogic.client.io.marker.ValuesReadHandle;
import com.marklogic.client.query.AggregateResult;
import com.marklogic.client.query.CountedDistinctValue;
import com.marklogic.client.query.ValuesDefinition;
import com.marklogic.client.query.ValuesMetrics;
import com.marklogic.client.query.ValuesResults;

/**
 * A ValuesHandle represents a list of values or of tuples
 * (combination of values for the same document) retrieved
 * from the indexes.
 */
public class ValuesHandle
	extends BaseHandle<InputStream, OperationNotSupported>
	implements ValuesReadHandle, ValuesResults
{
    static final private Logger logger = LoggerFactory.getLogger(DOMHandle.class);

    private ValuesBuilder.Values valuesHolder;
    private JAXBContext jc;
    private Unmarshaller unmarshaller;
    private HashMap<String, AggregateResult> hashedAggregates = null;

    private ValuesDefinition valuesdef = null;

    /**
     * Zero-argument constructor.
     */
    public ValuesHandle() {
    	super();
    	super.setFormat(Format.XML);

        try {
            jc = JAXBContext.newInstance(ValuesBuilder.Values.class);
            unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException e) {
            throw new MarkLogicBindingException(e);
        }
    }

    /**
     * Sets the format associated with this handle.
     *
     * This handle only supports XML.
     *
     * @param format The format, which must be Format.XML or an exception will be raised.
     */
    @Override
    public void setFormat(Format format) {
        if (format != Format.XML)
        	throw new IllegalArgumentException("ValuesHandle supports the XML format only");
    }

    /**
     * Fluent setter for the format associated with this handle.
     *
     * This handle only supports XML.
     *
     * @param format The format, which must be Format.XML or an exception will be raised.
     * @return The SearchHandle instance on which this method was called.
     */
    public ValuesHandle withFormat(Format format) {
        setFormat(format);
        return this;
    }

    @Override
    protected Class<InputStream> receiveAs() {
        return InputStream.class;
    }

    @Override
    protected void receiveContent(InputStream content) {
        try {
            valuesHolder = (ValuesBuilder.Values) unmarshaller.unmarshal(
            		new InputStreamReader(content, "UTF-8")
            		);
		} catch (UnsupportedEncodingException e) {
			logger.error("Failed to unmarshall values",e);
			throw new MarkLogicIOException(e);
        } catch (JAXBException e) {
			logger.error("Failed to unmarshall values",e);
			throw new MarkLogicIOException(e);
        } finally {
			try {
				content.close();
			} catch (IOException e) {
				// ignore.
			}
		}
    }

    /**
     * Sets the query definition used when retrieving values.
     *
     * <p>Calling this method always deletes any cached values.</p>
     *
     * @param vdef The new ValuesDefinition
     */
    public void setQueryCriteria(ValuesDefinition vdef) {
        valuesdef = vdef;
    }

    @Override
    public ValuesDefinition getQueryCriteria() {
        return valuesdef;
    }

    @Override
    public String getName() {
        return valuesHolder.getName();
    }

    @Override
    public String getType() {
        return valuesHolder.getType();
    }

    @Override
    public CountedDistinctValue[] getValues() {
        return valuesHolder.getValues();
    }

    @Override
    public AggregateResult[] getAggregates() {
        return valuesHolder.getAggregates();
    }

    @Override
    public AggregateResult getAggregate(String name) {
        if (hashedAggregates == null) {
            hashedAggregates = new HashMap<String, AggregateResult> ();
            for (AggregateResult aggregate : valuesHolder.getAggregates()) {
                hashedAggregates.put(aggregate.getName(), aggregate);
            }
        }

        if (hashedAggregates.containsKey(name)) {
            return hashedAggregates.get(name);
        } else {
            return null;
        }
    }

    @Override
    public ValuesMetrics getMetrics() {
        return valuesHolder.getMetrics();
    }
}
