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
import com.marklogic.client.impl.ValuesListBuilder;
import com.marklogic.client.io.marker.OperationNotSupported;
import com.marklogic.client.io.marker.ValuesListReadHandle;
import com.marklogic.client.query.ValuesListResults;

/**
 * A ValuesListHandle represents a list of available named lexicon configurations
 * from saved query options.
 */
public class ValuesListHandle
	extends BaseHandle<InputStream, OperationNotSupported>
	implements ValuesListReadHandle, ValuesListResults
{
    static final private Logger logger = LoggerFactory.getLogger(ValuesListHandle.class);

    private ValuesListBuilder.ValuesList valuesHolder;
    private JAXBContext jc;
    private Unmarshaller unmarshaller;

    String optionsName = null;

    /**
     * The constructor.
     */
    public ValuesListHandle() {
    	super();
    	super.setFormat(Format.XML);

        try {
            jc = JAXBContext.newInstance(ValuesListBuilder.ValuesList.class);
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
        	throw new IllegalArgumentException("ValuesListHandle supports the XML format only");
    }

    /**
     * Fluent setter for the format associated with this handle.
     *
     * This handle only supports XML.
     *
     * @param format The format, which must be Format.XML or an exception will be raised.
     * @return The QueryOptionsListHandle instance on which this method was called.
     */
    public ValuesListHandle withFormat(Format format) {
        setFormat(format);
        return this;
    }

    /**
     * Returns the name of the query options used for this query.
     * @return The options name.
     */
    public String getOptionsName() {
        return optionsName;
    }

    /**
     * Sets the name of the query options to be used for this query.
     *
     * If no query options node with the specified name exists, the query will fail.
     *
     * @param name The name of the saved query options node on the server.
     */
    public void setOptionsName(String name) {
        optionsName = name;
    }

    @Override
    protected Class<InputStream> receiveAs() {
        return InputStream.class;
    }

    @Override
    protected void receiveContent(InputStream content) {
        try {
            valuesHolder = (ValuesListBuilder.ValuesList) unmarshaller.unmarshal(
            		new InputStreamReader(content, "UTF-8")
            		);
		} catch (UnsupportedEncodingException e) {
			logger.error("Failed to unmarshall values list",e);
			throw new MarkLogicIOException(e);
        } catch (JAXBException e) {
			logger.error("Failed to unmarshall values list",e);
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
     * Returns a HashMap of the named lexicon configurations.
     *
     * The keys are the names of the lexicons, the values are the corresponding URIs on the server.
     *
     * @return The map of names to URIs.
     */
    @Override
    public HashMap<String, String> getValuesMap() {
        return valuesHolder.getValuesMap();
    }
}
