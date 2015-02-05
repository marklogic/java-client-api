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
import static org.junit.Assert.assertTrue;

import java.io.IOException;

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
import com.marklogic.client.io.TuplesHandle;
import com.marklogic.client.query.AggregateResult;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.Tuple;
import com.marklogic.client.query.TypedDistinctValue;
import com.marklogic.client.query.ValuesDefinition;
import com.marklogic.client.query.ValuesMetrics;

public class TuplesHandleTest {
	
	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(TuplesHandleTest.class);

	
    private static final String options =
            "<?xml version='1.0'?>"+
                    "<options xmlns=\"http://marklogic.com/appservices/search\">"+
                    "<values name=\"grandchild\">"+
                    "<range type=\"xs:string\">"+
                    "<element ns=\"\" name=\"grandchild\"/>"+
                    "</range>"+
                    "<values-option>limit=2</values-option>"+
                    "</values>"+
                    "<tuples name=\"co\">"+
                    "<range type=\"xs:double\">"+
                    "<element ns=\"\" name=\"double\"/>"+
                    "</range>"+
                    "<range type=\"xs:int\">"+
                    "<element ns=\"\" name=\"int\"/>"+
                    "</range>"+
                    "</tuples>"+
                    "<tuples name=\"n-way\">"+
                    "<range type=\"xs:double\">"+
                    "<element ns=\"\" name=\"double\"/>"+
                    "</range>"+
                    "<range type=\"xs:int\">"+
                    "<element ns=\"\" name=\"int\"/>"+
                    "</range>"+
                    "<range type=\"xs:string\">"+
                    "<element ns=\"\" name=\"string\"/>"+
                    "</range>"+
                    "<values-option>ascending</values-option>"+
                    "</tuples>"+
                    "<return-metrics>true</return-metrics>"+
                    "<return-values>true</return-values>"+
                    "</options>";

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
    	QueryOptionsManager optionsMgr = Common.client.newServerConfigManager().newQueryOptionsManager();
    	optionsMgr.writeOptions("valuesoptions2", new StringHandle(options));

    	logger.debug(options.toString());
    	
    	QueryManager queryMgr = Common.client.newQueryManager();

        ValuesDefinition vdef = queryMgr.newValuesDefinition("co", "valuesoptions2");
        vdef.setAggregate("correlation", "covariance");

        TuplesHandle t = queryMgr.tuples(vdef, new TuplesHandle());

        AggregateResult[] agg = t.getAggregates();
        assertEquals("Two aggregates are expected", 2, agg.length);

        double cov = t.getAggregate("covariance").get("xs:double", Double.class);
        assertTrue("The covariance is between 1.551 and 1.552",
                cov > 1.551 && cov < 1.552);

        Tuple[] tuples = t.getTuples();
        assertEquals("Twelve tuples are expected", 12, tuples.length);
        assertEquals("The tuples are named 'co'", "co", t.getName());

        ValuesMetrics metrics = t.getMetrics();
        assertTrue("The values resolution time is >= 0", metrics.getValuesResolutionTime() >= 0);
        assertTrue("The aggregate resolution time is >= 0", metrics.getAggregateResolutionTime() >= 0);

        optionsMgr.deleteOptions("valuesoptions2");
    }

    @Test
    public void testCoVariances()
    throws IOException, ParserConfigurationException, SAXException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
        QueryOptionsManager optionsMgr = Common.client.newServerConfigManager().newQueryOptionsManager();
        optionsMgr.writeOptions("valuesoptions3", new StringHandle(options));

        QueryManager queryMgr = Common.client.newQueryManager();

        ValuesDefinition vdef = queryMgr.newValuesDefinition("co", "valuesoptions3");

        TuplesHandle t = queryMgr.tuples(vdef, new TuplesHandle());

        Tuple[] tuples = t.getTuples();
        assertEquals("Twelve tuples are expected", 12, tuples.length);
        assertEquals("The tuples are named 'co'", "co", t.getName());

        ValuesMetrics metrics = t.getMetrics();
        assertTrue("The values resolution time is >= 0", metrics.getValuesResolutionTime() >= 0);
        // Restore after bug:18747 is fixed
        // assertEquals("The aggregate resolution time is -1 (absent)", metrics.getAggregateResolutionTime(), -1);

        optionsMgr.deleteOptions("valuesoptions3");
    }

    @Test
    public void testValuesHandle()
    throws IOException, ParserConfigurationException, SAXException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
        QueryOptionsManager optionsMgr = Common.client.newServerConfigManager().newQueryOptionsManager();
        optionsMgr.writeOptions("valuesoptions", new StringHandle(options));

        QueryManager queryMgr = Common.client.newQueryManager();

        ValuesDefinition vdef = queryMgr.newValuesDefinition("co", "valuesoptions");

        TuplesHandle t = queryMgr.tuples(vdef, new TuplesHandle());

        Tuple[] tuples = t.getTuples();
        assertEquals("Twelve tuples are expected", 12, tuples.length);
        assertEquals("The tuples are named 'co'", "co", t.getName());

        TypedDistinctValue[] dv = tuples[0].getValues();

        assertEquals("Two values per tuple expected", 2, dv.length);
        assertEquals("First is long", "xs:double",  dv[0].getType());
        assertEquals("Second is int", "xs:int", dv[1].getType());
        assertEquals("Frequency is 1", 1, tuples[0].getCount());
        assertEquals("First value",  1.1, (double) dv[0].get(Double.class), 0.01);
        assertEquals("Second value", (int) 1, (int) dv[1].get(Integer.class));

        optionsMgr.deleteOptions("valuesoptions");
    }
    
    @Test
    public void testNWayTuples()
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
    	 QueryOptionsManager optionsMgr = Common.client.newServerConfigManager().newQueryOptionsManager();
         optionsMgr.writeOptions("valuesoptions", new StringHandle(options));

         QueryManager queryMgr = Common.client.newQueryManager();

         ValuesDefinition vdef = queryMgr.newValuesDefinition("n-way", "valuesoptions");

         TuplesHandle t = queryMgr.tuples(vdef, new TuplesHandle());
         
         Tuple[] tuples = t.getTuples();
         assertEquals("Four tuples are expected", 4, tuples.length);
         assertEquals("The tuples are named 'n-way'", "n-way", t.getName());

         TypedDistinctValue[] dv = tuples[0].getValues();

         assertEquals("Three values per tuple expected", 3, dv.length);
         assertEquals("First is long", "xs:double",  dv[0].getType());
         assertEquals("Second is int", "xs:int", dv[1].getType());
         assertEquals("Third is string", "xs:string", dv[2].getType());
         assertEquals("Frequency is 1", 1, tuples[0].getCount());
         assertEquals("First value",  1.1, (double) dv[0].get(Double.class), 0.01);
         assertEquals("Second value", (int) 1, (int) dv[1].get(Integer.class));
         assertEquals("Third value", "Alaska", (String) dv[2].get(String.class));
         optionsMgr.deleteOptions("valuesoptions");
    }
    
    @Test
    public void testPagingTuples()
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
        QueryOptionsManager optionsMgr = Common.client.newServerConfigManager().newQueryOptionsManager();
        optionsMgr.writeOptions("valuesoptions", new StringHandle(options));

        QueryManager queryMgr = Common.client.newQueryManager();
        queryMgr.setPageLength(6);

        ValuesDefinition vdef = queryMgr.newValuesDefinition("co", "valuesoptions");

        TuplesHandle t = queryMgr.tuples(vdef, new TuplesHandle(), 3);

        Tuple[] tuples = t.getTuples();
        assertEquals("Six tuples are expected", 6, tuples.length);

    	TypedDistinctValue[] values = tuples[0].getValues();
    	String value = values[0].get(Double.class)+" | "+values[1].get(Integer.class);
        assertEquals("The first tuple is '1.2 | 3'", "1.2 | 3", value);

        values = tuples[5].getValues();
        value =
        	values[0].get(Double.class).toString()
        	+ " | "
        	+ values[1].get(Integer.class).toString();
        assertEquals("The last tuple is '2.2 | 4'", "2.2 | 4", value);

        optionsMgr.deleteOptions("valuesoptions");
    }
}
