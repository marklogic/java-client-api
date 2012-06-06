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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
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

    @BeforeClass
    public static void beforeClass() {
        Common.connectAdmin();
        builder = new QueryOptionsBuilder();
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void pathIndexNoNS() throws IOException, ParserConfigurationException, SAXException {
        /*
        <options xmlns="http://marklogic.com/appservices/search">
          <constraint name="t">
            <range type="xs:string" facet="true">
              <path-index>/doc/para/title</path-index>
            </range>
        </constraint>
         */

        QueryOptionsHandle options = new QueryOptionsHandle();
        options.build(builder.searchableExpression("/x:path/to/test", builder.namespace("x", "ab'c'")),
                builder.constraint("t",
                    builder.range(true, builder.type("xs:string"),
                    builder.pathIndex("/x:doc/para/title")))
                );

        System.err.println(options.toXMLString());
    }

    @Test
    public void roundTrip() throws IOException, ParserConfigurationException, SAXException {
        String xml = "<ns2:options xmlns:ns2=\"http://marklogic.com/appservices/search\">\n" +
                "    <ns2:constraint name=\"t\">\n" +
                "        <ns2:range type=\"xs:string\" facet=\"true\">\n" +
                "            <ns2:path-index>/doc/para/title</ns2:path-index>\n" +
                "        </ns2:range>\n" +
                "    </ns2:constraint>\n" +
                "    <ns2:searchable-expression xmlns=\"http://marklogic.com/appservices/search\" xmlns:x=\"ab'c'\">/x:path/to/test</ns2:searchable-expression>\n" +
                "</ns2:options>\n";

        InputStream in = new ByteArrayInputStream(xml.getBytes("utf-8"));

        QueryOptionsManager mgr =
        	Common.client.newServerConfigManager().newQueryOptionsManager();

        QueryOptionsHandle handle = new QueryOptionsHandle();

        HandleAccessor.receiveContent(handle, in);

        //System.err.println(handle.toXMLString());
  }

}
