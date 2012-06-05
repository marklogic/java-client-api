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

import com.marklogic.client.DocumentDescriptor;
import com.marklogic.client.Format;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.config.CountedDistinctValue;
import com.marklogic.client.config.DistinctValue;
import com.marklogic.client.config.FacetHeatmapValue;
import com.marklogic.client.config.FacetResult;
import com.marklogic.client.config.FacetValue;
import com.marklogic.client.config.MarkLogicBindingException;
import com.marklogic.client.config.MatchDocumentSummary;
import com.marklogic.client.config.MatchLocation;
import com.marklogic.client.config.MatchSnippet;
import com.marklogic.client.config.QueryDefinition;
import com.marklogic.client.config.QueryOptionsBuilder;
import com.marklogic.client.config.SearchMetrics;
import com.marklogic.client.config.SearchResults;
import com.marklogic.client.config.ValuesBuilder;
import com.marklogic.client.config.ValuesDefinition;
import com.marklogic.client.config.ValuesResults;
import com.marklogic.client.io.marker.OperationNotSupported;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.io.marker.ValuesReadHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;

public class ValuesHandle
	extends BaseHandle<InputStream, OperationNotSupported>
	implements ValuesReadHandle, ValuesResults
{
    static final private Logger logger = LoggerFactory.getLogger(DOMHandle.class);

    private ValuesBuilder.Values valuesHolder;
    private ValuesBuilder valuesBuilder;
    private JAXBContext jc;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    private ValuesDefinition valuesdef = null;

    public ValuesHandle() {
    	super();
    	super.setFormat(Format.XML);

        valuesBuilder = new ValuesBuilder();

        try {
            jc = JAXBContext.newInstance(ValuesBuilder.Values.class);
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
            valuesHolder = (ValuesBuilder.Values) unmarshaller.unmarshal(content);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
    public Class getType() {
        return DistinctValue.getType(valuesHolder.getType());
    }

    @Override
    public CountedDistinctValue[] getValues() {
        return valuesHolder.getValues();
    }

}
