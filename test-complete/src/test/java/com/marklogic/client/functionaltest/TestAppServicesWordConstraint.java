/*
 * Copyright 2014-2017 MarkLogic Corporation
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

package com.marklogic.client.functionaltest;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
public class TestAppServicesWordConstraint extends BasicJavaClientREST {

	private static String dbName = "AppServicesWordConstraintDB";
	private static String [] fNames = {"AppServicesWordConstraintDB-1"};
	
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  configureRESTServer(dbName, fNames);
	  setupAppServicesConstraint(dbName);
	  addRangeElementAttributeIndex(dbName, "decimal", "http://cloudbank.com", "price", "", "amt", "http://marklogic.com/collation/");
	}
@After
public  void testCleanUp() throws Exception
{
	clearDB();
	System.out.println("Running clear script");
}

@Test
	public void testWithElementAndAttributeIndex() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
	{	
		System.out.println("Running testWithElementAndAttributeIndex");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "wordConstraintWithElementAndAttributeIndexOpt.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/word-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("intitle:1945 OR inprice:12");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("For 1945", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("The Bush article", "string(//*[local-name()='result'][2]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("Vannevar served", "string(//*[local-name()='result'][3]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("1.23", "string(//*[local-name()='result'][1]//@*[local-name()='amt'])", resultDoc);
		assertXpathEvaluatesTo("0.12", "string(//*[local-name()='result'][2]//@*[local-name()='amt'])", resultDoc);
		assertXpathEvaluatesTo("12.34", "string(//*[local-name()='result'][3]//@*[local-name()='amt'])", resultDoc);
	    
		//String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:element-range-query(fn:QName(\"http://purl.org/dc/elements/1.1/\", \"date\"), \"=\", xs:date(\"2006-02-02\"), (), 1), cts:word-query(\"policymaker\", (\"lang=en\"), 1))), (\"score-logtfidf\"), 1))[1 to 10]";
		
		//assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}

@Test
	public void testWithNormalWordQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
	{	
		System.out.println("Running testWithNormalWordQuery");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "wordConstraintWithNormalWordQueryOpt.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/word-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("Memex  OR inprice:.12");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("The Bush article", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("The memex", "string(//*[local-name()='result'][2]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("0.12", "string(//*[local-name()='result'][1]//@*[local-name()='amt'])", resultDoc);
		assertXpathEvaluatesTo("123.45", "string(//*[local-name()='result'][2]//@*[local-name()='amt'])", resultDoc);
	    
		String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:word-query(\"Memex\", (\"lang=en\"), 1), cts:element-attribute-word-query(fn:QName(\"http://cloudbank.com\",\"price\"), fn:QName(\"\",\"amt\"), \".12\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\",cts:score-order(\"descending\")), 1))[1 to 10]";
		
		assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}

@Test
	public void testWithTermOptionCaseInsensitive() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
	{	
		System.out.println("Running testWithTermOptionCaseInsensitive");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "wordConstraintWithTermOptionCaseInsensitiveOpt.xml";

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/word-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("intitle:for  OR price:0.12");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("For 1945", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("The Bush article", "string(//*[local-name()='result'][2]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("1.23", "string(//*[local-name()='result'][1]//@*[local-name()='amt'])", resultDoc);
		assertXpathEvaluatesTo("0.12", "string(//*[local-name()='result'][2]//@*[local-name()='amt'])", resultDoc);
	    
		String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:element-word-query(fn:QName(\"\",\"title\"), \"for\", (\"case-insensitive\",\"lang=en\"), 1), cts:element-attribute-range-query(fn:QName(\"http://cloudbank.com\",\"price\"), fn:QName(\"\",\"amt\"), \"=\", 0.12, (), 1)), ()), (\"score-logtfidf\",\"faceted\",cts:score-order(\"descending\")), 1))[1 to 10]";
		
		assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}
@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		cleanupRESTServer(dbName, fNames);
		
	}
}
