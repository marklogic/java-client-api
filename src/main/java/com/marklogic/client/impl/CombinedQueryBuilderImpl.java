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
package com.marklogic.client.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;
import com.marklogic.client.query.StructuredQueryDefinition;

public class CombinedQueryBuilderImpl implements CombinedQueryBuilder {
    public class CombinedQueryDefinitionImpl 
    extends AbstractQueryDefinition
    implements CombinedQueryDefinition
    {
        private StructuredQueryDefinition query;
        private QueryOptionsWriteHandle options;
        private String qtext;

        public CombinedQueryDefinitionImpl(StructuredQueryDefinition query, QueryOptionsWriteHandle options, String qtext) {
            this.query = query;
            this.options = options;
            this.qtext = qtext;
        }

        public String serialize() {
            return CombinedQueryBuilderImpl.this.serialize(this);
        }

    }

    public CombinedQueryDefinition combine(StructuredQueryDefinition query, String qtext) {
        return new CombinedQueryDefinitionImpl(query, null, qtext);
    }
    public CombinedQueryDefinition combine(StructuredQueryDefinition query, QueryOptionsWriteHandle options) {
        return new CombinedQueryDefinitionImpl(query, options, null);
    }
    public CombinedQueryDefinition combine(StructuredQueryDefinition query, QueryOptionsWriteHandle options, String qtext) {
        return new CombinedQueryDefinitionImpl(query, options, qtext);
    }

    private String serialize(CombinedQueryDefinitionImpl combinedQueryDefinitionImpl) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            writeCombinedQuery(baos, combinedQueryDefinitionImpl);
            return baos.toString("UTF-8");
        } catch (Exception e) {
            throw new MarkLogicIOException(e);
        }
    }

    private XMLStreamWriter makeSerializer(OutputStream out) {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);

        try {
            XMLStreamWriter serializer = factory.createXMLStreamWriter(out, "UTF-8");

            serializer.setDefaultNamespace("http://marklogic.com/appservices/search");
            serializer.setPrefix("xs",  XMLConstants.W3C_XML_SCHEMA_NS_URI);

            return serializer;
        } catch (Exception e) {
            throw new MarkLogicIOException(e);
        }
    }

    private void writeCombinedQuery(OutputStream out, CombinedQueryDefinitionImpl combinedQueryDefinitionImpl) {
        try {
            String qtext = combinedQueryDefinitionImpl.qtext;
            StructuredQueryDefinition query = combinedQueryDefinitionImpl.query;
            QueryOptionsWriteHandle options = combinedQueryDefinitionImpl.options;
            XMLStreamWriter serializer = makeSerializer(out);

            serializer.writeStartDocument();
            serializer.writeStartElement("search");
            if ( qtext != null ) {
                serializer.writeStartElement("qtext");
                serializer.writeCharacters(qtext);
                serializer.writeEndElement();
            } else {
                serializer.writeCharacters("");
            }
            serializer.flush();
            if ( query != null ) {
                String structure = query.serialize();
                out.write(structure.getBytes("UTF-8"));
                out.flush();
            }
            if ( options != null ) {
                HandleImplementation handleBase = HandleAccessor.as(options);
                Object value = handleBase.sendContent();
                if ( value instanceof OutputStreamSender ) {
                    ((OutputStreamSender) value).write(out);
                } else {
                    out.write(value.toString().getBytes("UTF-8"));
                }
                out.flush();
            }

            serializer.writeEndElement();
            serializer.writeEndDocument();
            serializer.flush();
            serializer.close();
        } catch (Exception e) {
            throw new MarkLogicIOException(e);
        }
    }
}
