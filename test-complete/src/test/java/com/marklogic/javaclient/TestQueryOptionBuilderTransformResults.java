package com.marklogic.javaclient;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.Format;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.*;
public class TestQueryOptionBuilderTransformResults extends BasicJavaClientREST {

	private static String dbName = "TestQueryOptionBuilderTransformResultsDB";
	private static String [] fNames = {"TestQueryOptionBuilderTransformResultsDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

@BeforeClass	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testTransformResuleWithSnippetFunction() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testTransformResuleWithSnippetFunction");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/trans-res-with-snip-func/", "XML");
		}
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        // build query options
        handle.withConfiguration(builder.configure()
                		.returnMetrics(false)
                		.returnQtext(false))
              .withTransformResults(builder.snippetTransform(30, 4, 200, new QName("ns", "elem")));
               
        // write query options
        optionsMgr.writeOptions("TransformResuleWithSnippetFunction", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions("TransformResuleWithSnippetFunction", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("TransformResuleWithSnippetFunction");
		querydef.setCriteria("Atlantic groundbreaking");

		// create handle
		StringHandle resultsHandle = new StringHandle();
		resultsHandle.setFormat(Format.JSON);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		String resultDoc = resultsHandle.get();
		System.out.println(resultDoc);
		
		String expectedResult = "{\"snippet-format\":\"snippet\",\"total\":1,\"start\":1,\"page-length\":10,\"results\":[{\"index\":1,\"uri\":\"/trans-res-with-snip-func/constraint3.xml\"";
		assertTrue("Result is wrong", resultDoc.contains(expectedResult));
		                
		// release client
	    client.release();	
	}


@SuppressWarnings("deprecation")
@Test	public void testTransformResuleWithEmptySnippetFunction() throws XpathException, TransformerException, SAXException, IOException
	{	
		System.out.println("Running testTransformResuleWithEmptySnippetFunction");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/trans-res-with-emp-snip-func/", "XML");
		}
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        // build query options
        handle.withConfiguration(builder.configure()
                		.returnMetrics(false)
                		.returnQtext(false))
              .withTransformResults(builder.emptySnippets());
               
        // write query options
        optionsMgr.writeOptions("TransformResuleWithEmptySnippetFunction", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.XML);
		optionsMgr.readOptions("TransformResuleWithEmptySnippetFunction", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("TransformResuleWithEmptySnippetFunction");
		querydef.setCriteria("Atlantic groundbreaking");

		// create handle
		StringHandle resultsHandle = new StringHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		String resultDoc = resultsHandle.get();
		System.out.println(resultDoc);
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/trans-res-with-emp-snip-func/constraint3.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
		//assertXpathEvaluatesTo("groundbreaking", "string(//*[local-name()='result']//*[local-name()='highlight'][2])", resultDoc);
                
		// release client
	    client.release();	
	}

@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		
	}
}
