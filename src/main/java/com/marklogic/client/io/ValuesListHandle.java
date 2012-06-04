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

import com.marklogic.client.Format;
import com.marklogic.client.config.CountedDistinctValue;
import com.marklogic.client.config.DistinctValue;
import com.marklogic.client.config.MarkLogicBindingException;
import com.marklogic.client.config.ValuesBuilder;
import com.marklogic.client.config.ValuesDefinition;
import com.marklogic.client.config.ValuesListBuilder;
import com.marklogic.client.config.ValuesListResults;
import com.marklogic.client.config.ValuesResults;
import com.marklogic.client.io.marker.OperationNotSupported;
import com.marklogic.client.io.marker.ValuesListReadHandle;
import com.marklogic.client.io.marker.ValuesReadHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.HashMap;

public class ValuesListHandle
	extends BaseHandle<InputStream, OperationNotSupported>
	implements ValuesListReadHandle, ValuesListResults
{
    static final private Logger logger = LoggerFactory.getLogger(ValuesListHandle.class);

    private ValuesListBuilder.ValuesList valuesHolder;
    private ValuesListBuilder valuesBuilder;
    private JAXBContext jc;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    String optionsName = null;

    public ValuesListHandle() {
    	super();
    	super.setFormat(Format.XML);

        valuesBuilder = new ValuesListBuilder();

        try {
            jc = JAXBContext.newInstance(ValuesListBuilder.ValuesList.class);
            marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException e) {
            throw new MarkLogicBindingException(e);
        }
    }

    @Override
    public void setFormat(Format format) {
        if (format != Format.XML)
            new IllegalArgumentException("ValuesListHandle supports the XML format only");
    }

    public ValuesListHandle withFormat(Format format) {
        setFormat(format);
        return this;
    }

    public String getOptionsName() {
        return optionsName;
    }

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
            valuesHolder = (ValuesListBuilder.ValuesList) unmarshaller.unmarshal(content);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public HashMap<String, String> getValuesMap() {
        return valuesHolder.getValuesMap();
    }
}
