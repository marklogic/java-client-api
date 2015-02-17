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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.admin.config.QueryOptions;
import com.marklogic.client.admin.config.QueryOptions.Facets;
import com.marklogic.client.admin.config.QueryOptions.FragmentScope;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.impl.HandleAccessor;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.util.EditableNamespaceContext;

@SuppressWarnings("deprecation")
public class QOPathIndexTest {
    @SuppressWarnings("unused")
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
        QueryOptionsHandle options = new QueryOptionsHandle()
        		.withConstraints(
        				builder.constraint("t",
        						builder.range(
        								builder.pathIndex("/doc/para/title", 
        										null, 
        										builder.stringRangeType(QueryOptions.DEFAULT_COLLATION)))))
        				.withSearchableExpression(
        						builder.searchableExpression("/path/to/test"));

        String xml = options.toString();

        Document doc = XMLUnit.buildControlDocument(xml);

        String value = xpathEngine.evaluate("/search:options/search:constraint/search:range/search:path-index", doc);

        assertEquals("Path index is correct", "/doc/para/title", value);

        value = xpathEngine.evaluate("/search:options/search:searchable-expression", doc);

        assertEquals("Searchable expression is correct", "/path/to/test", value);
    }

    @Test
    public void pathIndexNS() throws IOException, ParserConfigurationException, SAXException, XpathException {
    	QueryOptionsHandle options = new QueryOptionsHandle()
				.withConstraints(builder.constraint("t",
                        builder.range( 
                        		builder.pathIndex("/y:doc/para/title", 
                        				builder.namespaces(builder.ns("y", "testing")),
                        				builder.stringRangeType(QueryOptions.DEFAULT_COLLATION)),
                        				Facets.FACETED, FragmentScope.DOCUMENTS,
                        				null)))
                .withSearchableExpression(        				
                        builder.searchableExpression("/x:path/to/test", builder.ns("x", "ab'c'")));

        String xml = options.toString();

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
    	QueryOptionsHandle options = new QueryOptionsHandle()
		.withConstraints(builder.constraint("t",
                        builder.range(
                                builder.pathIndex("/y:doc/para/title", builder.namespaces(builder.ns("y", "testing")),
                                builder.stringRangeType(QueryOptions.DEFAULT_COLLATION)),
                                Facets.FACETED, null,
                                null)))
                 .withSearchableExpression(builder.searchableExpression("/x:path/to/test", builder.ns("x", "ab'c'")));

        options.setSearchableExpression("//my:elements");

        EditableNamespaceContext context = new EditableNamespaceContext();
        context.setNamespaceURI("my", "http://example.com");

        options.setSearchableExpressionNamespaceContext(context);

        String xml = options.toString();

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

        QueryOptionsHandle handle = new QueryOptionsHandle();

        HandleAccessor.receiveContent(handle, in);

        String expression = handle.getSearchableExpression();
        NamespaceContext nscontext = handle.getSearchableExpressionNamespaceContext();

        assertEquals("Correct namespace binding for searchable-expression", nscontext.getNamespaceURI("x"), "ab'c'");
        assertEquals("Correct searchable-expression", expression, "/x:path/to/test");

        String resultxml = handle.toString();
        Document doc = XMLUnit.buildControlDocument(resultxml);

        String value = xpathEngine.evaluate("/search:options/search:constraint/search:range/search:path-index/namespace::foo", doc);

        assertEquals("Namespace foo is in scope", "testing", value);

        value = xpathEngine.evaluate("/search:options/search:searchable-expression/namespace::x", doc);

        assertEquals("Namespace x is in scope", "ab'c'", value);
  }
}
