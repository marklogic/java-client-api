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

import com.marklogic.client.QueryManager;
import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.config.MatchDocumentSummary;
import com.marklogic.client.config.MatchLocation;
import com.marklogic.client.config.QueryOptions.QueryRange;
import com.marklogic.client.config.QueryOptions.QueryTransformResults;
import com.marklogic.client.config.QueryOptionsBuilder;
import com.marklogic.client.config.StringQueryDefinition;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.SearchHandle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class Bug17833 {
	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(QueryOptionsHandleTest.class);
	
    @BeforeClass
    public static void beforeClass() {
        Common.connectAdmin();
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void testStringSearch() throws IOException, ParserConfigurationException, SAXException {
        String optionsName = "geo";

        // Get back facets...
        QueryOptionsHandle options = new QueryOptionsHandle();
        QueryOptionsBuilder builder = new QueryOptionsBuilder();
        options.build(
                builder.constraint("places",
                builder.geospatialElement(builder.element("http://marklogic.com/ns/test/places", "place"))));

        QueryOptionsManager queryOptionsMgr =
        	Common.client.newServerConfigManager().newQueryOptionsManager();

        logger.error(options.toXMLString());

        queryOptionsMgr.writeOptions(optionsName, options);

        QueryManager queryMgr = Common.client.newQueryManager();
        StringQueryDefinition qdef = queryMgr.newStringDefinition(optionsName);
        qdef.setCriteria("places:\"12,A\"");

        SearchHandle results = queryMgr.search(qdef, new SearchHandle());
        assertNotNull(results);
    }
}
