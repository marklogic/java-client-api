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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicBindingException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.config.CountedDistinctValue;
import com.marklogic.client.config.ValuesBuilder;
import com.marklogic.client.config.ValuesDefinition;
import com.marklogic.client.config.ValuesResults;
import com.marklogic.client.io.marker.OperationNotSupported;
import com.marklogic.client.io.marker.ValuesReadHandle;

public class ValuesHandle
	extends BaseHandle<InputStream, OperationNotSupported>
	implements ValuesReadHandle, ValuesResults
{
    static final private Logger logger = LoggerFactory.getLogger(DOMHandle.class);

    private ValuesBuilder.Values valuesHolder;
    private JAXBContext jc;
    private Unmarshaller unmarshaller;

    private ValuesDefinition valuesdef = null;

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

    @Override
    public void setFormat(Format format) {
        if (format != Format.XML)
            new IllegalArgumentException("ValuesHandle supports the XML format only");
    }

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
        }
    }

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

}
