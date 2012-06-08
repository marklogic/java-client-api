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
package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.EditableNamespaceContext;
import com.marklogic.client.QueryManager;
import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.ServerConfigurationManager;
import com.marklogic.client.config.QueryOptions;
import com.marklogic.client.config.QueryOptions.FragmentScope;
import com.marklogic.client.config.QueryOptions.Heatmap;
import com.marklogic.client.config.QueryOptions.QueryAnnotation;
import com.marklogic.client.config.QueryOptions.QueryCollection;
import com.marklogic.client.config.QueryOptions.QueryConstraint;
import com.marklogic.client.config.QueryOptions.QueryCustom;
import com.marklogic.client.config.QueryOptions.QueryDefaultSuggestionSource;
import com.marklogic.client.config.QueryOptions.QueryElementQuery;
import com.marklogic.client.config.QueryOptions.QueryGeospatialAttributePair;
import com.marklogic.client.config.QueryOptions.QueryGeospatialElement;
import com.marklogic.client.config.QueryOptions.QueryGeospatialElementPair;
import com.marklogic.client.config.QueryOptions.QueryGrammar;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryJoiner;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryJoiner.Comparator;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryJoiner.JoinerApply;
import com.marklogic.client.config.QueryOptions.QueryGrammar.QueryStarter;
import com.marklogic.client.config.QueryOptions.QueryGrammar.Tokenize;
import com.marklogic.client.config.QueryOptions.QueryOperator;
import com.marklogic.client.config.QueryOptions.QueryProperties;
import com.marklogic.client.config.QueryOptions.QueryRange;
import com.marklogic.client.config.QueryOptions.QueryRange.Bucket;
import com.marklogic.client.config.QueryOptions.QueryRange.ComputedBucket;
import com.marklogic.client.config.QueryOptions.QueryRange.ComputedBucket.AnchorValue;
import com.marklogic.client.config.QueryOptions.QuerySortOrder;
import com.marklogic.client.config.QueryOptions.QuerySortOrder.Direction;
import com.marklogic.client.config.QueryOptions.QueryState;
import com.marklogic.client.config.QueryOptions.QuerySuggestionSource;
import com.marklogic.client.config.QueryOptions.QueryTerm;
import com.marklogic.client.config.QueryOptions.QueryTerm.TermApply;
import com.marklogic.client.config.QueryOptions.QueryTransformResults;
import com.marklogic.client.config.QueryOptions.QueryValue;
import com.marklogic.client.config.QueryOptions.QueryValues;
import com.marklogic.client.config.QueryOptions.QueryWord;
import com.marklogic.client.config.QueryOptions.XQueryExtension;
import com.marklogic.client.config.QueryOptionsBuilder;
import com.marklogic.client.impl.QueryOptionsTransformExtractNS;
import com.marklogic.client.impl.QueryOptionsTransformInjectNS;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.HandleAccessor;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.QueryOptionsListHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.TuplesHandle;
import com.marklogic.client.io.marker.QueryOptionsReadHandle;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class QOPathIndexTest {
    private static final Logger logger = (Logger) LoggerFactory
            .getLogger(QOPathIndexTest.class);

    private static QueryOptionsBuilder builder;
    private static XpathEngine xpathEngine;

    @BeforeClass
    public static void beforeClass() {
        Common.connectAdmin();
        builder = new QueryOptionsBuilder();

        HashMap<String,String> xpathNS = new HashMap<String, String>();
        xpathNS.put("search", "http://marklogic.com/appservices/search");
        xpathNS.put("foo", "testing");
        xpathNS.put("x", "ab'c'");
        SimpleNamespaceContext xpathNsContext = new SimpleNamespaceContext(xpathNS);

        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setNormalize(true);
        XMLUnit.setNormalizeWhitespace(true);
        XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);

        xpathEngine = XMLUnit.newXpathEngine();
        xpathEngine.setNamespaceContext(xpathNsContext);
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void pathIndexNoNS() throws IOException, ParserConfigurationException, SAXException, XpathException {
        QueryOptionsHandle options = new QueryOptionsHandle();
        options.build(builder.searchableExpression("/path/to/test"),
                builder.constraint("t",
                    builder.range(true, builder.type("xs:string"),
                    builder.pathIndex("/doc/para/title")))
                );

        String xml = options.toXMLString();

        Document doc = XMLUnit.buildControlDocument(xml);

        String value = xpathEngine.evaluate("/search:options/search:constraint/search:range/search:path-index", doc);

        assertEquals("Path index is correct", "/doc/para/title", value);

        value = xpathEngine.evaluate("/search:options/search:searchable-expression", doc);

        assertEquals("Searchable expression is correct", "/path/to/test", value);
    }

    @Test
    public void pathIndexNS() throws IOException, ParserConfigurationException, SAXException, XpathException {
        QueryOptionsHandle options = new QueryOptionsHandle();
        options.build(builder.searchableExpression("/x:path/to/test", builder.namespace("x", "ab'c'")),
                builder.constraint("t",
                        builder.range(true, builder.type("xs:string"),
                                builder.pathIndex("/y:doc/para/title", builder.namespace("y", "testing"))))
        );

        String xml = options.toXMLString();

        Document doc = XMLUnit.buildControlDocument(xml);

        String value = xpathEngine.evaluate("/search:options/search:constraint/search:range/search:path-index", doc);

        assertEquals("Path index is correct", "/y:doc/para/title", value);

        value = xpathEngine.evaluate("/search:options/search:searchable-expression", doc);

        assertEquals("Searchable expression is correct", "/x:path/to/test", value);

        value = xpathEngine.evaluate("/search:options/search:constraint/search:range/search:path-index/namespace::y", doc);

        assertEquals("Namespace y is in scope", "testing", value);

        value = xpathEngine.evaluate("/search:options/search:searchable-expression/namespace::x", doc);

        assertEquals("Namespace x is in scope", "ab'c'", value);
    }

    @Test
    public void setSE() throws IOException, ParserConfigurationException, SAXException, XpathException {
        QueryOptionsHandle options = new QueryOptionsHandle();
        options.build(builder.searchableExpression("/x:path/to/test", builder.namespace("x", "ab'c'")),
                builder.constraint("t",
                        builder.range(true, builder.type("xs:string"),
                                builder.pathIndex("/y:doc/para/title", builder.namespace("y", "testing"))))
        );

        options.setSearchableExpression("//my:elements");

        EditableNamespaceContext context = new EditableNamespaceContext();
        context.setNamespaceURI("my", "http://example.com");

        options.setSearchableExpressionNamespaceContext(context);

        String xml = options.toXMLString();

        Document doc = XMLUnit.buildControlDocument(xml);

        String value = xpathEngine.evaluate("/search:options/search:searchable-expression", doc);

        assertEquals("Searchable expression is correct", "//my:elements", value);

        value = xpathEngine.evaluate("/search:options/search:searchable-expression/namespace::my", doc);

        assertEquals("Namespace my is in scope", "http://example.com", value);
    }

    @Test
    public void roundTrip() throws IOException, ParserConfigurationException, SAXException, XpathException {
        String xml = "<ns2:options xmlns:ns2=\"http://marklogic.com/appservices/search\">\n" +
                "    <ns2:constraint name=\"t\">\n" +
                "        <ns2:range type=\"xs:string\" facet=\"true\">\n" +
                "            <ns2:path-index xmlns:foo='testing'>/doc/foo:para/title</ns2:path-index>\n" +
                "        </ns2:range>\n" +
                "    </ns2:constraint>\n" +
                "    <ns2:searchable-expression xmlns=\"http://marklogic.com/appservices/search\" xmlns:x=\"ab'c'\">/x:path/to/test</ns2:searchable-expression>\n" +
                "</ns2:options>\n";

        InputStream in = new ByteArrayInputStream(xml.getBytes("utf-8"));

        QueryOptionsManager mgr =
        	Common.client.newServerConfigManager().newQueryOptionsManager();

        QueryOptionsHandle handle = new QueryOptionsHandle();

        HandleAccessor.receiveContent(handle, in);

        String expression = handle.getSearchableExpression();
        NamespaceContext nscontext = handle.getSearchableExpressionNamespaceContext();

        assertEquals("Correct namespace binding for searchable-expression", nscontext.getNamespaceURI("x"), "ab'c'");
        assertEquals("Correct searchable-expression", expression, "/x:path/to/test");

        String resultxml = handle.toXMLString();
        Document doc = XMLUnit.buildControlDocument(resultxml);

        String value = xpathEngine.evaluate("/search:options/search:constraint/search:range/search:path-index/namespace::foo", doc);

        assertEquals("Namespace foo is in scope", "testing", value);

        value = xpathEngine.evaluate("/search:options/search:searchable-expression/namespace::x", doc);

        assertEquals("Namespace x is in scope", "ab'c'", value);
  }
}
