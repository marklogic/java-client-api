/*
 * Copyright 2012-2013 MarkLogic Corporation
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

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import com.marklogic.client.query.StructuredQueryDefinition;

public class StructuredQueryBuilderTest {
    @Test
    public void testBuilder() throws IOException, SAXException, ParserConfigurationException {
        StructuredQueryBuilder qb = new StructuredQueryBuilder();
        StructuredQueryDefinition t, u, v, m;

        InputStream fileInputStream = new FileInputStream("src/test/resources/search.xsd");
        StreamSource[] sources = new StreamSource[1];
        sources[0] = new StreamSource(fileInputStream);

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(sources);

        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        parserFactory.setSchema(schema);

        ParseHandler handler = new ParseHandler();

        SAXParser parser = parserFactory.newSAXParser();
        StringInputStream xml = null;

        t = qb.and(qb.term("one"), qb.term("two"), qb.term("three"));
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><and-query><term-query><text>one</text></term-query><term-query><text>two</text></term-query><term-query><text>three</text></term-query></and-query></query>", q);
        }

        u = qb.or(qb.term("one"), qb.term("two"), qb.term("three"));
        for (String q: new String[]{u.serialize(), qb.build(u).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
        			XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><or-query><term-query><text>one</text></term-query><term-query><text>two</text></term-query><term-query><text>three</text></term-query></or-query></query>", q);
        }

        v = qb.or(t, u);
        for (String q: new String[]{v.serialize(), qb.build(v).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\">"
                + "<or-query>"
                + "<and-query><term-query><text>one</text></term-query><term-query><text>two</text></term-query><term-query><text>three</text></term-query></and-query>"
                + "<or-query><term-query><text>one</text></term-query><term-query><text>two</text></term-query><term-query><text>three</text></term-query></or-query>"
                + "</or-query></query>", q);
        }

        t = qb.and(qb.term("one", "two", "three"));
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><and-query><term-query><text>one</text><text>two</text><text>three</text></term-query></and-query></query>", q);
        }

        t = qb.and(qb.term(3.0, "one", "two", "three"));
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><and-query><term-query><text>one</text><text>two</text><text>three</text><weight>3.0</weight></term-query></and-query></query>", q);
        }

        t = qb.not(qb.term("one"));
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><not-query><term-query><text>one</text></term-query></not-query></query>", q);
        }

        t = qb.near(qb.term("one"));
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><near-query><term-query><text>one</text></term-query></near-query></query>", q);
        }

        t = qb.near(4, 2.3, StructuredQueryBuilder.Ordering.UNORDERED, qb.term("two"));
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><near-query><term-query><text>two</text></term-query>"
                + "<ordered>false</ordered><distance>4</distance><distance-weight>2.3</distance-weight></near-query></query>", q);
        }

        t = qb.term("leaf3");
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><term-query><text>leaf3</text></term-query></query>", q);
        }

        t = qb.andNot(qb.term("one"), qb.term("two"));
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><and-not-query>"
                + "<positive-query><term-query><text>one</text></term-query></positive-query>"
                + "<negative-query><term-query><text>two</text></term-query></negative-query>"
                + "</and-not-query></query>", q);
        }

        t = qb.documentFragment(qb.document("/some/uri.xml"));
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><document-fragment-query><document-query><uri>/some/uri.xml</uri></document-query></document-fragment-query></query>", q);
        }

        t = qb.properties(qb.directory(false, "/dir1", "dir2"));
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><properties-query><directory-query><uri>/dir1</uri><uri>dir2</uri><infinite>false</infinite></directory-query></properties-query></query>", q);
        }

        t = qb.locks(qb.term("one"));
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><locks-query><term-query><text>one</text></term-query></locks-query></query>", q);
        }

        t = qb.elementConstraint("name", qb.term("one"));
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><element-constraint-query><constraint-name>name</constraint-name><term-query><text>one</text></term-query></element-constraint-query></query>", q);
        }

        t = qb.propertiesConstraint("name", qb.term("one"));
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><properties-constraint-query><constraint-name>name</constraint-name><term-query><text>one</text></term-query></properties-constraint-query></query>", q);
        }

        t = qb.collectionConstraint("name", "c1", "c2");
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><collection-constraint-query><constraint-name>name</constraint-name><uri>c1</uri><uri>c2</uri></collection-constraint-query></query>", q);
        }

        t = qb.valueConstraint("name", "one");
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><value-constraint-query><constraint-name>name</constraint-name><text>one</text></value-constraint-query></query>", q);
        }

        m = qb.value(qb.element("name"), "one");
        for (String q: new String[]{qb.build(m).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><value-query><element ns=\"\" name=\"name\"></element><text>one</text></value-query></query>", q);
        }

        t = qb.valueConstraint("name", 2.0, "one");
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><value-constraint-query><constraint-name>name</constraint-name><text>one</text><weight>2.0</weight></value-constraint-query></query>", q);
        }

        m = qb.value(qb.element("name"), null, null, 2.0, "one");
        for (String q: new String[]{qb.build(m).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><value-query><element ns=\"\" name=\"name\"></element><text>one</text><weight>2.0</weight></value-query></query>", q);
        }

        t = qb.wordConstraint("name", "one");
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><word-constraint-query><constraint-name>name</constraint-name><text>one</text></word-constraint-query></query>", q);
        }

        m = qb.word(qb.element("name"), "one");
        for (String q: new String[]{qb.build(m).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><word-query><element ns=\"\" name=\"name\"></element><text>one</text></word-query></query>", q);
        }

        t = qb.wordConstraint("name", 2.0, "one");
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><word-constraint-query><constraint-name>name</constraint-name><text>one</text><weight>2.0</weight></word-constraint-query></query>", q);
        }

        m = qb.word(qb.element("name"), null, null, 2.0, "one");
        for (String q: new String[]{qb.build(m).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><word-query><element ns=\"\" name=\"name\"></element><text>one</text><weight>2.0</weight></word-query></query>", q);
        }

        t = qb.rangeConstraint("name", Operator.GE, "value1", "value2");
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><range-constraint-query><constraint-name>name</constraint-name><value>value1</value><value>value2</value><range-operator>GE</range-operator></range-constraint-query></query>", q);
        }

        m = qb.range(qb.element("name"), "xs:string", Operator.GE, "value1", "value2");
        for (String q: new String[]{qb.build(m).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><range-query type=\"xs:string\"><element ns=\"\" name=\"name\"></element><value>value1</value><value>value2</value><range-operator>GE</range-operator></range-query></query>", q);
        }

        t = qb.geospatialConstraint("geo", qb.box(1, 2, 3, 4), qb.circle(0, 0, 100), qb.point(5, 6),
                qb.polygon(qb.point(1, 2), qb.point(2, 3), qb.point(3, 4), qb.point(4, 1)));
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\">"
                + "<geospatial-constraint-query>"
                + "<constraint-name>geo</constraint-name>"
                + "<box><south>1.0</south><west>2.0</west><north>3.0</north><east>4.0</east></box>"
                + "<circle><radius>100.0</radius><point><latitude>0.0</latitude><longitude>0.0</longitude></point></circle>"
                + "<point><latitude>5.0</latitude><longitude>6.0</longitude></point>"
                + "<polygon><point><latitude>1.0</latitude><longitude>2.0</longitude></point>"
                + "<point><latitude>2.0</latitude><longitude>3.0</longitude></point>"
                + "<point><latitude>3.0</latitude><longitude>4.0</longitude></point>"
                + "<point><latitude>4.0</latitude><longitude>1.0</longitude></point>"
                + "</polygon></geospatial-constraint-query></query>",
                q);
        }

        m = qb.geospatial(
        		qb.geoElementPair(qb.element("parent"), qb.element("lat"), qb.element("lon")),
        		qb.box(1, 2, 3, 4), qb.circle(0, 0, 100), qb.point(5, 6),
                qb.polygon(qb.point(1, 2), qb.point(2, 3), qb.point(3, 4), 
                qb.point(4, 1)));
        for (String q: new String[]{qb.build(m).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\">"
                + "<geo-elem-pair-query>"
                + "<parent ns=\"\" name=\"parent\"></parent>"+"<lat ns=\"\" name=\"lat\"></lat>"+"<lon ns=\"\" name=\"lon\"></lon>"
                + "<box><south>1.0</south><west>2.0</west><north>3.0</north><east>4.0</east></box>"
                + "<circle><radius>100.0</radius><point><latitude>0.0</latitude><longitude>0.0</longitude></point></circle>"
                + "<point><latitude>5.0</latitude><longitude>6.0</longitude></point>"
                + "<polygon><point><latitude>1.0</latitude><longitude>2.0</longitude></point>"
                + "<point><latitude>2.0</latitude><longitude>3.0</longitude></point>"
                + "<point><latitude>3.0</latitude><longitude>4.0</longitude></point>"
                + "<point><latitude>4.0</latitude><longitude>1.0</longitude></point>"
                + "</polygon></geo-elem-pair-query></query>",
                q);
        }

        t = qb.customConstraint("name", "one", "two");
        for (String q: new String[]{t.serialize(), qb.build(t).toString()}) {
        	xml = new StringInputStream(q);
        	parser.parse(xml, handler);
        	assertEquals("<?xml version=\"1.0\" ?><query xmlns=\"http://marklogic.com/appservices/search\" xmlns:xs=\""+
    				XMLConstants.W3C_XML_SCHEMA_NS_URI+"\"><custom-constraint-query><constraint-name>name</constraint-name><text>one</text><text>two</text></custom-constraint-query></query>", q);
        }

        // verify backward compatible behaviour
        String inner = null;
        inner = qb.valueConstraint("name", "one").innerSerialize();
        assertEquals("<value-constraint-query><constraint-name>name</constraint-name><text>one</text></value-constraint-query>", inner);
        inner = qb.point(5, 6).serialize();
        assertEquals("<point><latitude>5.0</latitude><longitude>6.0</longitude></point>", inner);
    }

    private class ParseHandler extends DefaultHandler {
        public void fatalError(SAXParseException spe) throws SAXParseException {
            throw spe;
        }

        public void error(SAXParseException spe) throws SAXParseException {
            throw spe;
        }

        public void warning(SAXParseException spe) throws SAXParseException {
            throw spe;
        }
    }
}
