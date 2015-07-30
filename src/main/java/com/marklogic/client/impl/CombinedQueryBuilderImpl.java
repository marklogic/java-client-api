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

import javax.xml.XMLConstants;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.query.StructuredQueryDefinition;

public class CombinedQueryBuilderImpl implements CombinedQueryBuilder {
    
    public class CombinedQueryDefinitionImpl 
    extends AbstractQueryDefinition
    implements CombinedQueryDefinition
    {
        private StructuredQueryDefinition structuredQuery;
        private RawStructuredQueryDefinition rawQuery;
        private QueryOptionsWriteHandle options;
        private String qtext;
        private String sparql;
        private Format format;

        public CombinedQueryDefinitionImpl(StructuredQueryDefinition structuredQuery,
            QueryOptionsWriteHandle options, String qtext, String sparql)
        {
            this.structuredQuery = structuredQuery;
            this.options = options;
            this.qtext = qtext;
            this.sparql = sparql;
            this.format = Format.XML;
        }

        public CombinedQueryDefinitionImpl(RawStructuredQueryDefinition rawQuery,
            QueryOptionsWriteHandle options, String qtext, String sparql)
        {
            this.rawQuery = rawQuery;
            this.options = options;
            this.qtext = qtext;
            this.sparql = sparql;
            this.format = HandleAccessor.as(rawQuery.getHandle()).getFormat();
            if ( format != Format.XML && format != Format.JSON ) {
                throw new IllegalArgumentException("Format of rawQuery must be XML or JSON");
            }
        }

        public String serialize() {
            return CombinedQueryBuilderImpl.this.serialize(this);
        }

        @Override
        public Format getFormat() {
            return format;
        }

    }

    public CombinedQueryDefinition combine(StructuredQueryDefinition structuredQuery, String qtext) {
        return new CombinedQueryDefinitionImpl(structuredQuery, null, qtext, null);
    }
    public CombinedQueryDefinition combine(StructuredQueryDefinition structuredQuery,
        QueryOptionsWriteHandle options)
    {
        return new CombinedQueryDefinitionImpl(structuredQuery, options, null, null);
    }
    public CombinedQueryDefinition combine(StructuredQueryDefinition structuredQuery,
        QueryOptionsWriteHandle options, String qtext)
    {
        return new CombinedQueryDefinitionImpl(structuredQuery, options, qtext, null);
    }
    public CombinedQueryDefinition combine(StructuredQueryDefinition structuredQuery,
        QueryOptionsWriteHandle options, String qtext, String sparql)
    {
        return new CombinedQueryDefinitionImpl(structuredQuery, options, qtext, sparql);
    }

    public CombinedQueryDefinition combine(RawStructuredQueryDefinition rawQuery, String qtext) {
        return new CombinedQueryDefinitionImpl(rawQuery, null, qtext, null);
    }
    public CombinedQueryDefinition combine(RawStructuredQueryDefinition rawQuery,
        QueryOptionsWriteHandle options)
    {
        return new CombinedQueryDefinitionImpl(rawQuery, options, null, null);
    }
    public CombinedQueryDefinition combine(RawStructuredQueryDefinition rawQuery,
        QueryOptionsWriteHandle options, String qtext)
    {
        return new CombinedQueryDefinitionImpl(rawQuery, options, qtext, null);
    }
    public CombinedQueryDefinition combine(RawStructuredQueryDefinition rawQuery,
        QueryOptionsWriteHandle options, String qtext, String sparql)
    {
        return new CombinedQueryDefinitionImpl(rawQuery, options, qtext, sparql);
    }

    private String serialize(CombinedQueryDefinitionImpl qdef) {
        try {
            if ( qdef.rawQuery != null ) {
                Format rawQueryFormat = HandleAccessor.as(qdef.rawQuery.getHandle()).getFormat();
                if ( Format.XML == rawQueryFormat ) {
                    return makeXMLCombinedQuery(qdef);
                } else if ( Format.JSON == rawQueryFormat ) {
                    return makeJSONCombinedQuery(qdef);
                } else {
                    throw new IllegalStateException("A RawStructuredQueryDefinition must " +
                        "be XML or JSON, not " + rawQueryFormat);
                }
            }
            return makeXMLCombinedQuery(qdef);
        } catch (Exception e) {
            throw new MarkLogicIOException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    private String makeJSONCombinedQuery(CombinedQueryDefinitionImpl qdef) {
        try {
            ObjectMapper mapper = new ObjectMapper()
                .configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(Feature.ALLOW_SINGLE_QUOTES, true);
            ObjectNode rootNode = mapper.createObjectNode();
            ObjectNode searchNode = mapper.createObjectNode();
            rootNode.replace("search", searchNode);
            searchNode.put("sparql", qdef.sparql);
            if ( qdef.options != null ) {
                HandleImplementation optionsBase = HandleAccessor.as(qdef.options);
                if ( Format.JSON != optionsBase.getFormat() ) {
                    throw new IllegalStateException("Cannot combine a JSON-format structured " +
                            "query with " + optionsBase.getFormat() + "-format options");
                }
                String json = HandleAccessor.contentAsString(qdef.options);
                JsonNode optionsNode;
                optionsNode = mapper.readTree(json);
                searchNode.replace("options", optionsNode.get("options"));
            }
            if ( qdef.qtext != null ) searchNode.put("qtext", qdef.qtext);
            if ( qdef.rawQuery != null ) {
                String json = HandleAccessor.contentAsString(qdef.rawQuery.getHandle());
                JsonNode optionsNode = mapper.readTree(json);
                searchNode.replace("query", optionsNode.get("query"));
            }
            return rootNode.toString();
        } catch (Exception e) {
            throw new MarkLogicIOException(e);
        }
    }

    private XMLStreamWriter makeXMLSerializer(OutputStream out) {
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

    private String makeXMLCombinedQuery(CombinedQueryDefinitionImpl qdef) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            String qtext = qdef.qtext;
            StructuredQueryDefinition structuredQuery = qdef.structuredQuery;
            RawStructuredQueryDefinition rawQuery = qdef.rawQuery;
            QueryOptionsWriteHandle options = qdef.options;
            String sparql = qdef.sparql;

            XMLStreamWriter serializer = makeXMLSerializer(out);

            serializer.writeStartDocument();
            serializer.writeStartElement("search");
            if ( qtext != null ) {
                serializer.writeStartElement("qtext");
                serializer.writeCharacters(qtext);
                serializer.writeEndElement();
            } else {
                serializer.writeCharacters("");
            }
            if ( sparql != null ) {
                serializer.writeStartElement("sparql");
                serializer.writeCharacters(sparql);
                serializer.writeEndElement();
            }
            serializer.flush();
            String structure = "";
            if ( structuredQuery != null ) structure = structuredQuery.serialize();
            if ( rawQuery != null ) structure = HandleAccessor.contentAsString(rawQuery.getHandle());
            out.write(structure.getBytes("UTF-8"));
            out.flush();
            if ( options != null ) {
                HandleImplementation handleBase = HandleAccessor.as(options);
                Object value = handleBase.sendContent();
                if ( value instanceof OutputStreamSender ) {
                    ((OutputStreamSender) value).write(out);
                } else {
                    out.write(HandleAccessor.contentAsString(options).getBytes("UTF-8"));
                }
                out.flush();
            }

            serializer.writeEndElement();
            serializer.writeEndDocument();
            serializer.flush();
            serializer.close();
            return out.toString("UTF-8");
        } catch (Exception e) {
            throw new MarkLogicIOException(e);
        }
    }
    
    public CombinedQueryDefinition mergeJSON(StructureWriteHandle input,
            String sparql) {
    
        JacksonHandle handle= new JacksonHandle();
        HandleAccessor.receiveContent(handle, HandleAccessor.contentAsString(input));
        JsonNode combinedQueryJson = handle.get();
        
        JsonNode optionsContents = combinedQueryJson.get("search").get("options");
        JacksonHandle optionsHandle = null;
        if (optionsContents != null) {
            ObjectNode rewrappedOptionsNode = JsonNodeFactory.instance.objectNode();
            ObjectNode optionsObject = rewrappedOptionsNode.putObject("options");
            optionsObject.setAll((ObjectNode) optionsContents);
            optionsHandle = new JacksonHandle();
            optionsHandle.set(rewrappedOptionsNode);
        }
        
        //TODO this could be more than one string...
        JsonNode qtextNode = combinedQueryJson.get("search").get("qtext");
        String qtext = null;
        if (qtextNode != null) {
            qtext = qtextNode.asText();
        }
        JsonNode structuredQuery = combinedQueryJson.get("search").get("query");
        JacksonHandle structuredQueryHandle = null;
        if (structuredQuery != null) {
            ObjectNode rewrappedStructuredQuery = JsonNodeFactory.instance.objectNode();
            ObjectNode structuredQueryObject = rewrappedStructuredQuery.putObject("query");
            structuredQueryObject.setAll((ObjectNode) structuredQuery);
            structuredQueryHandle = new JacksonHandle().with(rewrappedStructuredQuery);
        }
        RawStructuredQueryDefinition structuredQueryDefinition = 
                new RawQueryDefinitionImpl.Structured(structuredQueryHandle);
        return new CombinedQueryDefinitionImpl(structuredQueryDefinition, optionsHandle, qtext, sparql);
    }
    
    public CombinedQueryDefinition mergeXML(StructureWriteHandle input,
            String sparql) {
         DOMHandle handle = new DOMHandle();
         HandleAccessor.receiveContent(handle, HandleAccessor.contentAsString(input));
         Document combinedQueryXml = handle.get();
         DOMImplementationLS domImplementation = (DOMImplementationLS) combinedQueryXml.getImplementation();
         LSSerializer lsSerializer = domImplementation.createLSSerializer();
         lsSerializer.getDomConfig().setParameter("xml-declaration", false);

         NodeList nl = combinedQueryXml.getElementsByTagNameNS("http://marklogic.com/appservices/search", "options");
         Node n = nl.item(0);
         String options = null;
         StringHandle optionsHandle = null;
         if (n != null) {
             options = lsSerializer.writeToString(n);
             optionsHandle = new StringHandle(options).withFormat(Format.XML);
         }
        
         //TODO this could be more than one string...
         nl = combinedQueryXml.getElementsByTagNameNS("http://marklogic.com/appservices/search", "qtext");
         n = nl.item(0);
         String qtext = null;
         if (n != null) {
             qtext = lsSerializer.writeToString(n);
         }
         
         nl = combinedQueryXml.getElementsByTagNameNS("http://marklogic.com/appservices/search", "query");
         n = nl.item(0);
         String query = null;
         if (n != null) {
             query = lsSerializer.writeToString(nl.item(0));
         }
         StringHandle structuredQueryHandle = new StringHandle().with(query).withFormat(Format.XML);
         RawStructuredQueryDefinition structuredQueryDefinition = 
                 new RawQueryDefinitionImpl.Structured(structuredQueryHandle);
         return new CombinedQueryDefinitionImpl(structuredQueryDefinition, optionsHandle, qtext, sparql);
    
    }
}
