/*
 * Copyright 2014-2015 MarkLogic Corporation
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import com.marklogic.client.query.StructuredQueryDefinition;
public class TestBug18801 extends BasicJavaClientREST {

	private static String dbName = "Bug18801DB";
	private static String [] fNames = {"Bug18801DB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  // Adding a wait for cluster restart from a prior test.
	  waitForServerRestart();
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}

@Test
public void testDefaultFacetValue() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
{	
	System.out.println("Running testDefaultFacetValue");
	
	String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

	DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
			
	// set query option validation to true
	ServerConfigurationManager srvMgr = client.newServerConfigManager();
	srvMgr.readConfiguration();
	srvMgr.setQueryOptionValidation(true);
	srvMgr.writeConfiguration();
	
	// write docs
	for(String filename : filenames)
	{
		writeDocumentUsingInputStreamHandle(client, filename, "/def-facet/", "XML");
	}

	// create query options manager
	QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
	
	// create query options builder		
	String opts = new StringBuilder()
	                  .append("<search:options xmlns:search=\"http://marklogic.com/appservices/search\">")
                      .append("<search:constraint name=\"pop\">")
                      .append("<search:range type=\"xs:int\">")
                      .append("<search:element name=\"popularity\" ns=\"\"/>")
                      .append("</search:range>")
                      .append("</search:constraint>")
                      .append("</search:options>").toString();
    // build and write query options with new handle
    optionsMgr.writeOptions("FacetValueOpt", new StringHandle(opts));
    
    // read query option
 	StringHandle readHandle = new StringHandle();
 	readHandle.setFormat(Format.XML);
 	optionsMgr.readOptions("FacetValueOpt", readHandle);
 	String output = readHandle.get();
 	System.out.println(output);

 	// create query manager
 	QueryManager queryMgr = client.newQueryManager();
 				
 	// create query def
 	StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder("FacetValueOpt");
 	StructuredQueryDefinition queryFinal = qb.rangeConstraint("pop", Operator.EQ, "5");
 		
 	// create handle
 	DOMHandle resultsHandle = new DOMHandle();
 	queryMgr.search(queryFinal, resultsHandle);
 		
 	// get the result
 	Document resultDoc = resultsHandle.get();
 	//System.out.println(convertXMLDocumentToString(resultDoc)); 
 	
 	assertXpathEvaluatesTo("pop", "string(//*[local-name()='response']//*[local-name()='facet']//@*[local-name()='name'])", resultDoc);
 	assertXpathEvaluatesTo("3", "string(//*[local-name()='response']//*[local-name()='facet']/*[local-name()='facet-value']//@*[local-name()='count'])", resultDoc);
 	
	// release client
	client.release();		
}
	@AfterClass	
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,restServerName);
		
	}
}
