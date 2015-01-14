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
package com.marklogic.client.test;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.marklogic.client.impl.CombinedQueryBuilderImpl;
import com.marklogic.client.impl.CombinedQueryDefinition;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;
import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CombinedQueryBuilderTest {

    @BeforeClass
    public static void beforeClass() {
        Common.connect();
    }
    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void buildCombinedQuery() throws Exception {
        String qtext = "hello";
        String collection = "myCollection";

        QueryOptionsWriteHandle options = buildOptions();

        StructuredQueryBuilder sqb = Common.client.newQueryManager().newStructuredQueryBuilder();
        StructuredQueryDefinition structuredQuery = sqb.collection(collection);
            
        // NOTICE!! CombinedQueryBuilderImpl is for internal use only for now--it's undocmented and unsupported.
        CombinedQueryBuilderImpl cqb = new CombinedQueryBuilderImpl();
        CombinedQueryDefinition query = cqb.combine(structuredQuery, options, qtext);
        String expected = buildExpected(qtext, collection);
        String serialized = query.serialize();
        assertXMLEqual("Output not as expected", expected, serialized);
        
        PojoRepository<City, Integer> cities = Common.client.newPojoRepository(City.class, Integer.class);
        PojoQueryBuilder<City> qb = cities.getQueryBuilder();
        query = (CombinedQueryDefinition) qb.filteredQuery(qb.word("asciiName", new String[] {"wildcarded"}, 1, "Chittagong*"));
        expected = "<?xml version=\"1.0\" ?>" +
          "<search xmlns=\"http://marklogic.com/appservices/search\">" + 
    		"<query>" +
    			"<word-query>" +
	    			"<json-property>asciiName</json-property><text>Chittagong*</text>" +
	    			"<term-option>wildcarded</term-option><weight>1.0</weight>" +
				"</word-query>" +
			"</query>" +
			"<options><search-option>filtered</search-option></options>" +
			"</search>";
        serialized = query.serialize();
        assertXMLEqual("Output not as expected", expected, serialized);
    }

    public String buildExpected(String qtext, String collection) throws XMLStreamException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter writer = makeXMLStreamWriter(baos);
        writer.writeStartElement("search");
            writer.writeStartElement("qtext");
                writer.writeCharacters(qtext);
            writer.writeEndElement();
            writer.writeStartElement("query");
                writer.writeStartElement("collection-query");
                    writer.writeStartElement("uri");
                        writer.writeCharacters(collection);
                    writer.writeEndElement();
                writer.writeEndElement();
            writer.writeEndElement();
            writeOptions(writer);
        writer.writeEndElement();
        return baos.toString("UTF-8");
    }

    public QueryOptionsWriteHandle buildOptions() throws UnsupportedEncodingException, XMLStreamException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeOptions(makeXMLStreamWriter(baos));
        return new StringHandle(baos.toString("UTF-8")).withFormat(Format.XML);
    }

    public void writeOptions(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("options");
            writer.writeStartElement("search-option");
                writer.writeCharacters("filtered");
            writer.writeEndElement();
        writer.writeEndElement();
    }

    public XMLStreamWriter makeXMLStreamWriter(OutputStream out) throws XMLStreamException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);

        XMLStreamWriter writer = factory.createXMLStreamWriter(out, "UTF-8");
        writer.setDefaultNamespace("http://marklogic.com/appservices/search");
        return writer;
    }
}
