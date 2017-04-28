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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.FragmentScope;
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import com.marklogic.client.query.StructuredQueryDefinition;

public class TestStandaloneQuery extends BasicJavaClientREST {

	private static String dbName = "TestStandaloneQueryDB";
	private static String [] fNames = {"TestStandaloneQueryDB-1"};
	

	@BeforeClass	
	public static void setUp() throws Exception 
	{
		System.out.println("In setup");
		configureRESTServer(dbName, fNames);
		setDatabaseProperties(dbName,"stemmed-searches","basic");
		setupAppServicesConstraint(dbName);
		addRangeElementAttributeIndex(dbName, "decimal", "http://cloudbank.com", "price", "", "amt", "http://marklogic.com/collation/");
	}

	@Test	
	public void testStandaloneWordQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testStandaloneWordQuery");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/standalone-query/", "XML");
		}

		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition t = qb.word(qb.element("id"), "0026");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println("Output of Search : "+convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertTrue("Proper result is not returned :", convertXMLDocumentToString(resultDoc).contains("<search:highlight>0026"));	
		
		// release client
		client.release();		
	}

	@Test	
	public void testStandaloneWordQueryEnhanced() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testStandaloneWordQueryEnhanced");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/standalone-query/", "XML");
		}

		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		//StructuredQueryDefinition t = qb.word(qb.element(new QName("http://cloudbank.com", "price")), "0026");
		String[] options = {"case-insensitive","stemmed","unwildcarded"};
		StructuredQueryDefinition t = qb.word(qb.elementAttribute(qb.element(new QName("http://cloudbank.com", "price")), qb.attribute("amt")), FragmentScope.DOCUMENTS, options ,.025 , "123.45");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		String output=convertXMLDocumentToString(resultDoc);
		System.out.println(output);
		System.out.println("Search Result : " + resultDoc.getDocumentURI());
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertTrue("Results are not proper",output.contains("uri=\"/standalone-query/constraint5.xml\"") );    		
		
		// release client
		client.release();		
	}

	@Test	
	public void testStandaloneRangeQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{
		System.out.println("Running testStandaloneRangeQuery");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/standalone-query/", "XML");
		}

		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition t = qb.range(qb.element("popularity"), "xs:integer", Operator.GE, 4);

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("4", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		
		// release client
		client.release();
	}

	@Test	
	public void testStandaloneRangeQueryEnhanced() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{
		System.out.println("Running testStandaloneRangeQueryEnhanced");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/standalone-query/", "XML");
		}

		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		String collation = "http://marklogic.com/collation/en";
		StructuredQueryDefinition t =qb.range(qb.element("popularity"), "xs:integer", collation, Operator.GE, 4);

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("4", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		
		// release client
		client.release();
	}

	@Test	
	public void testStandaloneRangeQueryEnhanced1() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{
		System.out.println("Running testStandaloneRangeQueryEnhanced1");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/standalone-query/", "XML");
		}

		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		String[] options = {"uncached","min-occurs=2"};
		StructuredQueryDefinition t =qb.range(qb.element("popularity"), "xs:integer", options, Operator.LE, 4);
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		
		// release client
		client.release();
	}

	@Test	
	public void testStandaloneRangeQueryEnhanced2() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{
		System.out.println("Running testStandaloneRangeQueryEnhanced2");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/standalone-query/", "XML");
		}

		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		String[] options = {"uncached","min-occurs=2"};
		String collation = "http://marklogic.com/collation/en";
		StructuredQueryDefinition t =qb.range(qb.element("popularity"), "xs:integer", collation, options, Operator.LT, 4);        
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		
		// release client
		client.release();
	}

	@Test	
	public void testStandaloneRangeQueryEnhanced3() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{
		System.out.println("Running testStandaloneRangeQueryEnhanced3");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/standalone-query/", "XML");
		}

		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		String collation = "http://marklogic.com/collation/en";
		StructuredQueryDefinition t =qb.range(qb.element("popularity"), "xs:integer", collation, FragmentScope.DOCUMENTS, Operator.GT, 4);
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		
		// release client
		client.release();
	}

	@Test	
	public void testStandaloneRangeQueryEnhanced4() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{
		System.out.println("Running testStandaloneRangeQueryEnhanced4");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/standalone-query/", "XML");
		}

		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		String collation = "http://marklogic.com/collation/en";
		String[] options = {"uncached","min-occurs=2"};

		StructuredQueryDefinition t =qb.range(qb.element("popularity"), "xs:integer", collation, FragmentScope.DOCUMENTS, options, Operator.NE, 4);
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("4", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		
		// release client
		client.release();
	}

	@Test	
	public void testStandaloneValueQueryOnAttribute() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testStandaloneValueQueryOnAttribute");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/standalone-query/", "XML");
		}

		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition t = qb.value(qb.elementAttribute(qb.element(new QName("http://cloudbank.com", "price")), qb.attribute("amt")), "0.1");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(t, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		
		// release client
		client.release();		
	}

	@Test	
	public void testStandaloneValueQueryOnAttributeEnhanced() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testStandaloneValueQueryOnAttributeEnhanced");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/standalone-query/", "XML");
		}

		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		//StructuredQueryDefinition t = qb.value(qb.elementAttribute(qb.element(new QName("http://cloudbank.com", "price")), qb.attribute("amt")), "0.1");
		String[] options = {"case-insensitive","stemmed","unwildcarded"};
		StructuredQueryDefinition t1 = qb.value(qb.elementAttribute(qb.element(new QName("http://cloudbank.com", "price")), qb.attribute("amt")), FragmentScope.DOCUMENTS, options , 3.0, "123.45");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(t1, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		String output = convertXMLDocumentToString(resultDoc);
		System.out.println(output);

		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertTrue("The result is not proper", output.contains("/standalone-query/constraint5.xml"));
		
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
