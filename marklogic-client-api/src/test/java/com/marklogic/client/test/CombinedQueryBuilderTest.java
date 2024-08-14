/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.impl.CombinedQueryBuilderImpl;
import com.marklogic.client.impl.CombinedQueryDefinition;
import com.marklogic.client.impl.XmlFactories;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;
import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import org.junit.jupiter.api.*;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class CombinedQueryBuilderTest {

  @BeforeAll
  public static void beforeClass() {
    Common.connect();
  }
  @AfterAll
  public static void afterClass() {
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
    writer.writeStartElement(StructuredQueryBuilder.SEARCH_API_NS, "search");
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
    writer.flush();
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
    writer.flush();
  }

  public XMLStreamWriter makeXMLStreamWriter(OutputStream out) throws XMLStreamException {
    XMLOutputFactory factory = XmlFactories.getOutputFactory();

    XMLStreamWriter writer = factory.createXMLStreamWriter(out, "UTF-8");
    writer.setDefaultNamespace("http://marklogic.com/appservices/search");
    return writer;
  }
}
