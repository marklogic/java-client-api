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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.io.ValuesListHandle;
import com.marklogic.client.query.AggregateResult;
import com.marklogic.client.query.CountedDistinctValue;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.ValueQueryDefinition;
import com.marklogic.client.query.ValuesDefinition;
import com.marklogic.client.query.ValuesListDefinition;

public class ValuesHandleTest {


	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(ValuesHandleTest.class);
	
	@BeforeClass
    public static void beforeClass() {
        Common.connectAdmin();
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void testAggregates()
    throws IOException, ParserConfigurationException, SAXException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
    	String optionsName = makeValuesOptions();

    	QueryManager queryMgr = Common.client.newQueryManager();

        ValuesDefinition vdef = queryMgr.newValuesDefinition("double", optionsName);
        vdef.setAggregate("sum", "avg");
        vdef.setName("double");

        //ValuesListDefinition vldef = queryMgr.newValuesListDefinition("valuesoptions");
        //ValuesListHandle vlh = queryMgr.valuesList(vldef, new ValuesListHandle());

        ValuesHandle v = queryMgr.values(vdef, new ValuesHandle());

        AggregateResult[] agg = v.getAggregates();
        assertEquals("There should be 2 aggregates", 2, agg.length);
        double first  = agg[0].get("xs:double", Double.class);
        assertTrue("Aggregate 1 should be between 11.4 and 12",
                11.4 < first && first < 12.0);

        double second = agg[1].get("xs:double", Double.class);

        logger.debug("" + second);
        assertTrue("Aggregate 2 should be between 1.43 and 1.44",
        		1.43 < second && second < 1.44);

        Common.client.newServerConfigManager().newQueryOptionsManager().deleteOptions(optionsName);
    }

    @Test
    public void testCriteria()
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
    	String optionsName = makeValuesOptions();

    	QueryManager queryMgr = Common.client.newQueryManager();

        ValuesDefinition vdef = queryMgr.newValuesDefinition("double", optionsName);

        for (int i=0; i < 2; i++) {
        	ValueQueryDefinition vQuery = null;
        	switch (i) {
        	case 0:
        		StringQueryDefinition stringQuery = queryMgr.newStringDefinition();
        		stringQuery.setCriteria("10");
        		vQuery = stringQuery;
        		break;
        	case 1:
                StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(null);
                StructuredQueryDefinition t = qb.term("10");
                vQuery = t;
                break;
        	default:
        		assertTrue("test case error", false);
        	}
        	vdef.setQueryDefinition(vQuery);

        	ValuesHandle v = queryMgr.values(vdef, new ValuesHandle());
        	CountedDistinctValue dv[] = v.getValues();
        	assertNotNull("There should be values", dv);
        	assertEquals("There should be 3 values", 3, dv.length);
        }

        Common.client.newServerConfigManager().newQueryOptionsManager().deleteOptions(optionsName);
    }

    @Test
    public void testValuesHandle() throws IOException, ParserConfigurationException, SAXException {
        File f = new File("src/test/resources/values.xml");
        FileInputStream is = new FileInputStream(f);

        MyValuesHandle v = new MyValuesHandle();

        v.parseTestData(is);

        assertTrue("Name should be 'size'", "size".equals(v.getName()));
        assertEquals("Type should be 'xs:unsignedLong'", "xs:unsignedLong", v.getType());

        CountedDistinctValue dv[] = v.getValues();

        assertEquals("There should be 8 values", 8, dv.length);
        assertEquals("Frequency should be 1", 1, dv[0].getCount());
        assertEquals("Value should be 815", (long) 815, (long) dv[0].get(v.getType(), Long.class));
    }

    @Test
    public void testValuesListHandle() throws IOException, ParserConfigurationException, SAXException {
        File f = new File("src/test/resources/valueslist.xml");
        FileInputStream is = new FileInputStream(f);

        MyValuesListHandle v = new MyValuesListHandle();

        v.parseTestData(is);
        HashMap<String,String> map = v.getValuesMap();
        assertEquals("Map should contain two keys", map.size(), 2);
        assertEquals("Size should have this uri", map.get("size"), "/v1/values/size?options=photos");
    }


    @Test
    public void testValuesPaging() throws IOException, ParserConfigurationException, SAXException {
    	String optionsName = makeValuesOptions();

    	QueryManager queryMgr = Common.client.newQueryManager();
    	queryMgr.setPageLength(2);

        ValuesDefinition vdef = queryMgr.newValuesDefinition("double", optionsName);

        ValuesHandle v = queryMgr.values(vdef, new ValuesHandle(), 2);
        CountedDistinctValue dv[] = v.getValues();
        assertNotNull("There should be values", dv);
        assertEquals("There should be 2 values", 2, dv.length);

        assertEquals("The first value should be '1.2'",
        		dv[0].get("xs:double", Double.class).toString(), "1.2");
        assertEquals("The second value should be '2.2'",
        		dv[1].get("xs:double", Double.class).toString(), "2.2");

        Common.client.newServerConfigManager().newQueryOptionsManager().deleteOptions(optionsName);
    }

    // this test only works if you've loaded the 5min guide @Test
    public void serverValuesList() throws IOException, ParserConfigurationException, SAXException {
        String optionsName = "photos";

        QueryManager queryMgr = Common.client.newQueryManager();
        ValuesListDefinition vdef = queryMgr.newValuesListDefinition(optionsName);

        ValuesListHandle results = queryMgr.valuesList(vdef, new ValuesListHandle());
        assertNotNull(results);
        HashMap<String,String> map = results.getValuesMap();
        assertEquals("Map should contain two keys", map.size(), 2);
        assertEquals("Size should have this uri", map.get("size"), "/v1/values/size?options=photos");
        
        // test pagelength
        queryMgr.setPageLength(1L);
        results = queryMgr.valuesList(vdef, new ValuesListHandle());
        assertNotNull(results);
        map = results.getValuesMap();
        assertEquals("Map should contain one keys", map.size(), 1);
        
    }

    static public String makeValuesOptions()
    throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException {
    	String options = 
        	"<?xml version='1.0'?>"+
        	"<options xmlns=\"http://marklogic.com/appservices/search\">"+
        	  "<values name=\"grandchild\">"+
        	     "<range type=\"xs:string\">"+
        	        "<element ns=\"\" name=\"grandchild\"/>"+
        	     "</range>"+
        	  "</values>"+
        	  "<values name=\"double\">"+
        	     "<range type=\"xs:double\">"+
        	        "<element ns=\"\" name=\"double\"/>"+
        	     "</range>"+
        	  "</values>"+
        	  "<return-metrics>false</return-metrics>"+
        	"</options>";

    	QueryOptionsManager optionsMgr = Common.client.newServerConfigManager().newQueryOptionsManager();
    	optionsMgr.writeOptions("valuesoptions", new StringHandle(options));

    	return "valuesoptions";
    }

    static public class MyValuesHandle extends ValuesHandle {
        public void parseTestData(InputStream stream) {
            receiveContent(stream);
        }
    }

    static public class MyValuesListHandle extends ValuesListHandle {
        public void parseTestData(InputStream stream) {
            receiveContent(stream);
        }
    }
}
