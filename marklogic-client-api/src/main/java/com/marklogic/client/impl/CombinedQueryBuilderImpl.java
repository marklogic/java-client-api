/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawQueryDefinition;
import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;

public class CombinedQueryBuilderImpl implements CombinedQueryBuilder {

  public class CombinedQueryDefinitionImpl
    extends AbstractQueryDefinition
    implements CombinedQueryDefinition
  {
    private StructuredQueryDefinition structuredQuery;
    private RawQueryDefinition rawQuery;
    private QueryOptionsWriteHandle options;
    private String qtext;
    private String sparql;
    private Format format;

    public CombinedQueryDefinitionImpl(StructuredQueryDefinition structuredQuery,
                                       QueryOptionsWriteHandle options, String qtext, String sparql)
    {
      if ( structuredQuery instanceof RawQueryDefinition ) {
        init((RawQueryDefinition) structuredQuery, options, qtext, sparql);
      } else {
        this.structuredQuery = structuredQuery;
        this.options = options;
        this.qtext = qtext;
        this.sparql = sparql;
        this.format = Format.XML;
      }
    }

    public CombinedQueryDefinitionImpl(RawQueryDefinition rawQuery,
                                       QueryOptionsWriteHandle options, String qtext, String sparql)
    {
      init(rawQuery, options, qtext, sparql);
    }


    public void init(RawQueryDefinition rawQuery,
                     QueryOptionsWriteHandle options, String qtext, String sparql)
    {
      this.rawQuery = rawQuery;
      this.options = options;
      this.qtext = qtext;
      this.sparql = sparql;
      // if a query has been supplied, it's either in JSON or in XML
      if (rawQuery != null) {
        this.format = HandleAccessor.as(rawQuery.getHandle()).getFormat();
      } else {
        if (options != null) {
          this.format = HandleAccessor.as(options).getFormat();
        } else {
          // there's only qtext -- we choose format.
          this.format = Format.JSON;
        }
      }
      if ( format != Format.XML && format != Format.JSON ) {
        throw new IllegalArgumentException("Format of rawQuery must be XML or JSON");
      }
    }

    @Override
    public String serialize() {
      return CombinedQueryBuilderImpl.this.serialize(this);
    }

    @Override
    public Format getFormat() {
      return format;
    }

    @Override
    public boolean canSerializeQueryAsJSON() {
      return getFormat() == Format.JSON && getOptionsName() == null;
    }
  }

  @Override
  public CombinedQueryDefinition combine(StructuredQueryDefinition structuredQuery, String qtext) {
    return new CombinedQueryDefinitionImpl(structuredQuery, null, qtext, null);
  }
  @Override
  public CombinedQueryDefinition combine(StructuredQueryDefinition structuredQuery,
                                         QueryOptionsWriteHandle options)
  {
    return new CombinedQueryDefinitionImpl(structuredQuery, options, null, null);
  }
  @Override
  public CombinedQueryDefinition combine(StructuredQueryDefinition structuredQuery,
                                         QueryOptionsWriteHandle options, String qtext)
  {
    return new CombinedQueryDefinitionImpl(structuredQuery, options, qtext, null);
  }
  @Override
  public CombinedQueryDefinition combine(StructuredQueryDefinition structuredQuery,
                                         QueryOptionsWriteHandle options, String qtext, String sparql)
  {
    return new CombinedQueryDefinitionImpl(structuredQuery, options, qtext, sparql);
  }

  @Override
  public CombinedQueryDefinition combine(RawStructuredQueryDefinition rawQuery, String qtext) {
    return new CombinedQueryDefinitionImpl(rawQuery, null, qtext, null);
  }
  @Override
  public CombinedQueryDefinition combine(RawStructuredQueryDefinition rawQuery,
                                         QueryOptionsWriteHandle options)
  {
    return new CombinedQueryDefinitionImpl(rawQuery, options, null, null);
  }
  @Override
  public CombinedQueryDefinition combine(RawStructuredQueryDefinition rawQuery,
                                         QueryOptionsWriteHandle options, String qtext)
  {
    return new CombinedQueryDefinitionImpl(rawQuery, options, qtext, null);
  }
  @Override
  public CombinedQueryDefinition combine(RawStructuredQueryDefinition rawQuery,
                                         QueryOptionsWriteHandle options, String qtext, String sparql)
  {
    return new CombinedQueryDefinitionImpl(rawQuery, options, qtext, sparql);
  }

  @Override
  public CombinedQueryDefinition combine(RawCombinedQueryDefinition rawQuery, String qtext)
  {
    return new CombinedQueryDefinitionImpl(rawQuery, null, qtext, null);
  }
  @Override
  public CombinedQueryDefinition combine(RawCombinedQueryDefinition rawQuery,
                                         QueryOptionsWriteHandle options)
  {
    return new CombinedQueryDefinitionImpl(rawQuery, options, null, null);
  }
  @Override
  public CombinedQueryDefinition combine(RawCombinedQueryDefinition rawQuery,
                                         QueryOptionsWriteHandle options, String qtext)
  {
    return new CombinedQueryDefinitionImpl(rawQuery, options, qtext, null);
  }
  @Override
  public CombinedQueryDefinition combine(RawCombinedQueryDefinition rawQuery,
                                         QueryOptionsWriteHandle options, String qtext, String sparql)
  {
    return new CombinedQueryDefinitionImpl(rawQuery, options, qtext, sparql);
  }

  private String serialize(CombinedQueryDefinitionImpl qdef) {
    try {
      if ( qdef.format != null ) {
        if ( Format.XML == qdef.format ) {
          return makeXMLCombinedQuery(qdef);
        } else if ( Format.JSON == qdef.format ) {
          return makeJSONCombinedQuery(qdef);
        } else {
          throw new IllegalStateException("A RawStructuredQueryDefinition must " +
            "be XML or JSON, not " + qdef.format);
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
      if ( qdef.sparql != null ) searchNode.put("sparql", qdef.sparql);
      if ( qdef.qtext != null ) searchNode.put("qtext", qdef.qtext);
      if ( qdef.options != null ) {
        HandleImplementation optionsBase = HandleAccessor.as(qdef.options);
        if ( Format.JSON != optionsBase.getFormat() ) {
          throw new IllegalStateException("Cannot combine a JSON-format structured " +
            "query with " + optionsBase.getFormat() + "-format options");
        }
        String json = HandleAccessor.contentAsString(qdef.options);
        JsonNode optionsNode = mapper.readTree(json);
        searchNode.replace("options", optionsNode.get("options"));
      }
      if ( qdef.rawQuery != null ) {
        String json = HandleAccessor.contentAsString(qdef.rawQuery.getHandle());
        JsonNode rawQueryNode = mapper.readTree(json);
        JsonNode queryNode = rawQueryNode.get("query");
        if ( queryNode == null ) queryNode = rawQueryNode.path("search").get("query");
        if ( queryNode != null ) searchNode.replace("query", queryNode);
        if ( qdef.options == null ) {
          JsonNode optionsNode = rawQueryNode.path("search").get("options");
          if ( optionsNode != null ) searchNode.replace("options", optionsNode);
        }
        if ( qdef.qtext == null ) {
          JsonNode qtextNode = rawQueryNode.path("search").get("qtext");
          if ( qtextNode != null ) searchNode.replace("qtext", qtextNode);
        }
        if ( qdef.sparql == null ) {
          JsonNode sparqlNode = rawQueryNode.path("search").get("sparql");
          if ( sparqlNode != null ) searchNode.replace("sparql", sparqlNode);
        }
      }
      return rootNode.toString();
    } catch (Exception e) {
      throw new MarkLogicIOException(e);
    }
  }

  private XMLStreamWriter makeXMLSerializer(OutputStream out) {
    XMLOutputFactory factory = XmlFactories.getOutputFactory();

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
      RawQueryDefinition rawQuery = qdef.rawQuery;
      QueryOptionsWriteHandle options = qdef.options;
      String sparql = qdef.sparql;
      if ( rawQuery != null && rawQuery instanceof RawCombinedQueryDefinition ) {
        CombinedQueryDefinitionImpl combinedQdef =
          parseCombinedQuery((RawCombinedQueryDefinition) rawQuery);
        rawQuery = combinedQdef.rawQuery;
        if ( qtext == null   ) qtext   = combinedQdef.qtext;
        if ( options == null ) options = combinedQdef.options;
        if ( sparql == null  ) sparql  = combinedQdef.sparql;
      }

      XMLStreamWriter serializer = makeXMLSerializer(out);

      serializer.writeStartDocument();
      serializer.writeStartElement(StructuredQueryBuilder.SEARCH_API_NS, "search");
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
      if ( structure != null && structure.length() > 0 ) {
        out.write(structure.getBytes("UTF-8"));
      }
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

  private CombinedQueryDefinitionImpl parseCombinedQuery(RawCombinedQueryDefinition qdef) {
    DOMHandle handle = new DOMHandle();
    HandleAccessor.receiveContent(handle, HandleAccessor.contentAsString(qdef.getHandle()));
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

    nl = combinedQueryXml.getElementsByTagNameNS("http://marklogic.com/appservices/search", "sparql");
    n = nl.item(0);
    String sparql = null;
    if (n != null) {
      sparql = lsSerializer.writeToString(n);
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
    return new CombinedQueryDefinitionImpl(structuredQueryDefinition,
      optionsHandle, qtext, sparql);
  }
}
