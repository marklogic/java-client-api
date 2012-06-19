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
import com.marklogic.client.MarkLogicBindingException;
import com.marklogic.client.config.QueryDefinition;
import com.marklogic.client.config.Tuple;
import com.marklogic.client.config.TuplesBuilder;
import com.marklogic.client.config.TuplesResults;
import com.marklogic.client.config.ValuesDefinition;
import com.marklogic.client.io.marker.OperationNotSupported;
import com.marklogic.client.io.marker.TuplesReadHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

public class TuplesHandle
	extends BaseHandle<InputStream, OperationNotSupported>
	implements TuplesReadHandle, TuplesResults
{
    static final private Logger logger = LoggerFactory.getLogger(DOMHandle.class);

    private TuplesBuilder.Tuples tuplesHolder;
    private TuplesBuilder tuplesBuilder;
    private JAXBContext jc;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    private ValuesDefinition valdef = null;

    public TuplesHandle() {
    	super();
    	super.setFormat(Format.XML);

        tuplesBuilder = new TuplesBuilder();

        try {
            jc = JAXBContext.newInstance(TuplesBuilder.Tuples.class);
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
            new IllegalArgumentException("ValuesHandle supports the XML format only");
    }

    public TuplesHandle withFormat(Format format) {
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
            tuplesHolder = (TuplesBuilder.Tuples) unmarshaller.unmarshal(content);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public ValuesDefinition getQueryCriteria() {
        return valdef;
    }

    @Override
    public String[] getConstraintNames() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getConstraintTypes() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Tuple[] getTuples() {
        return tuplesHolder.getTuples();
    }

}
