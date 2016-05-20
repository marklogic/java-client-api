/*
 * Copyright 2014-2016 MarkLogic Corporation
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

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
public class TestBug18724 extends BasicJavaClientREST {

	private static String dbName = "Bug18724DB";
	private static String [] fNames = {"Bug18724DB-1"};
	
	
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  configureRESTServer(dbName, fNames);
	  setupAppServicesConstraint(dbName);
	}
@After
public  void testCleanUp() throws Exception
{
	clearDB();
	System.out.println("Running clear script");
}

@Test
	public void testDefaultStringSearch() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testDefaultStringSearch");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);
		
		// create transaction
		Transaction transaction1 = client.openTransaction();
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/def-string-search/", transaction1, "XML");
		}
		
		// commit transaction
		transaction1.commit();
		
		// create transaction
		Transaction transaction2 = client.openTransaction();
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		querydef.setCriteria("0012");
				
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle, transaction2);
		
		// commit transaction
		transaction2.commit();
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result']//*[local-name()='highlight'])", resultDoc);
		
		// release client
		client.release();		
	}

@Test	
	public void testDefaultKeyValueSearch() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testDefaultKeyValueSearch");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);
		
		// create transaction
		Transaction transaction1 = client.openTransaction();
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/def-keyvalue-search/", transaction1, "XML");
		}
		
		// commit transaction
		transaction1.commit();
		
		// create transaction
		Transaction transaction2 = client.openTransaction();
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition
		KeyValueQueryDefinition querydef = queryMgr.newKeyValueDefinition();
		querydef.put(queryMgr.newElementLocator(new QName("id")), "0012");
				
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle, transaction2);
		
		// commit transaction
		transaction2.commit();
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result']//*[local-name()='highlight'])", resultDoc);
		
		// release client
		client.release();		
	}
	
	/*public void testDefaultStructuredQueryBuilderSearch() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testDefaultStructuredQueryBuilderSearch");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/def-query-builder-search/", "XML");
		}
		
		// create transaction
		Transaction transaction2 = client.openTransaction();
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition termQuery = qb.term("0012");
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(termQuery, resultsHandle, transaction2);
		
		// commit transaction
		transaction2.commit();
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result']//*[local-name()='highlight'])", resultDoc);
		
		// release client
		client.release();		
	}*/
	
	/*public void testDefaultValuesSearch() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testDefaultValuesSearch");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);
		
		// create transaction
		Transaction transaction1 = client.openTransaction();
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/def-values-search/", transaction1, "XML");
		}
		
		// commit transaction
		transaction1.commit();
		
		// create transaction
		Transaction transaction2 = client.openTransaction();
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		ValuesDefinition queryDef = queryMgr.newValuesDefinition("popularity");
		queryDef.setAggregate("sum");
		//queryDef.setName("popularity");
		
		// create handle
		ValuesHandle valuesHandle = new ValuesHandle();
		queryMgr.values(queryDef, valuesHandle, transaction2);
		
		// commit transaction
		transaction2.commit();
		
		// get the result
		//Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
        AggregateResult[] agg = valuesHandle.getAggregates();
        System.out.println(agg.length);
        int first  = agg[0].get("xs:int", Integer.class);
        System.out.println(first);
        
		//assertXpathEvaluatesTo("1", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
		//assertXpathEvaluatesTo("0012", "string(//*[local-name()='result']//*[local-name()='highlight'])", resultDoc);
		
		// release client
		client.release();		
	}*/
	@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		cleanupRESTServer(dbName, fNames);
		
	}
}
