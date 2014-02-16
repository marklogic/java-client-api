/*
 * Copyright 2012-2014 MarkLogic Corporation
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

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
    @BeforeClass
    public static void beforeClass() {
        Common.connectAdmin();
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void testAggregates() throws IOException, ParserConfigurationException, SAXException {
    	String options =
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
        	  "<return-metrics>false</return-metrics>"+
        	  "<return-values>false</return-values>"+
        	"</options>";

    	QueryOptionsManager optionsMgr = Common.client.newServerConfigManager().newQueryOptionsManager();
    	optionsMgr.writeOptions("valuesoptions2", new StringHandle(options));

    	QueryManager queryMgr = Common.client.newQueryManager();

        ValuesDefinition vdef = queryMgr.newValuesDefinition("co", "valuesoptions2");
        vdef.setAggregate("correlation", "covariance");

        TuplesHandle t = queryMgr.tuples(vdef, new TuplesHandle());

        AggregateResult[] agg = t.getAggregates();
        assertEquals("Two aggregates are expected", 2, agg.length);

        double cov = t.getAggregate("covariance").get("xs:double", Double.class);
        System.out.println(cov);
        assertTrue("The covariance is between 1.551 and 1.552",
                cov > 1.551 && cov < 1.552);

        optionsMgr.deleteOptions("valuesoptions3");
    }

    @Test
    public void testValuesHandle2() throws IOException, ParserConfigurationException, SAXException {
        File f = new File("src/test/resources/tuples2.xml");
        FileInputStream is = new FileInputStream(f);

        MyTuplesHandle t = new MyTuplesHandle();
        t.parseTestData(is);

        Tuple[] tuples = t.getTuples();
        assertEquals("Nine tuples are expected", 9, tuples.length);
        assertEquals("The tuples are named 'co'", "co", t.getName());

        ValuesMetrics metrics = t.getMetrics();
        assertTrue("The values resolution time is greater than 0", metrics.getValuesResolutionTime() > 0);
        assertEquals("The aggregate resolution time is -1 (absent)", metrics.getAggregateResolutionTime(), -1);
    }

    @Test
    public void testValuesHandle() throws IOException, ParserConfigurationException, SAXException {
        File f = new File("src/test/resources/tuples.xml");
        FileInputStream is = new FileInputStream(f);

        MyTuplesHandle t = new MyTuplesHandle();
        t.parseTestData(is);

        Tuple[] tuples = t.getTuples();

        assertEquals("Four tuples expected", 4, tuples.length);

        TypedDistinctValue[] dv = tuples[0].getValues();

        assertEquals("Two values per tuple expected", 2, dv.length);
        assertEquals("First is long", "xs:unsignedLong",  dv[0].getType());
        assertEquals("Second is string", "xs:string", dv[1].getType());
        assertEquals("Frequency is 1", 1, tuples[0].getCount());
        assertEquals("First value", (long) 45375, (long) dv[0].get(Long.class));
        assertEquals("Second value", "1/160", dv[1].get(String.class));
    }

    public class MyTuplesHandle extends TuplesHandle {
        public void parseTestData(InputStream stream) {
            receiveContent(stream);
        }
    }
}
