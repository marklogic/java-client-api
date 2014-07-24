package com.marklogic.javaclient;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.Format;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.*;
public class TestQueryOptionBuilderSearchOptions extends BasicJavaClientREST {

	private static String dbName = "TestQueryOptionBuilderSearchOptionsDB";
	private static String [] fNames = {"TestQueryOptionBuilderSearchOptionsDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

@BeforeClass	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	 setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testSearchOptions1() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testSearchOptions1");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/search-ops-1/", "XML");
		}
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        List<String> listOfSearchOptions = new ArrayList<String> ();
        listOfSearchOptions.add("checked");
        listOfSearchOptions.add("filtered");
        listOfSearchOptions.add("score-simple");
        
        // build query options
        handle.withConfiguration(builder.configure()
                		.returnMetrics(false)
                		.returnQtext(false)
                		.debug(true))
              .withTransformResults(builder.rawResults())
              .setSearchOptions(listOfSearchOptions);
             
        // write query options
        optionsMgr.writeOptions("SearchOptions1", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions("SearchOptions1", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchOptions1");
	    querydef.setCriteria("bush");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
        String expectedSearchReport = "(cts:search(fn:collection(), cts:word-query(\"bush\", (\"lang=en\"), 1), (\"checked\",\"filtered\",\"score-simple\"), 1))[1 to 10]";
		
		assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
                
		// release client
	    client.release();	
	}
	

@SuppressWarnings("deprecation")
@Test	public void testSearchOptions2() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testSearchOptions2");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/search-ops-2/", "XML");
		}
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        List<String> listOfSearchOptions = new ArrayList<String> ();
        listOfSearchOptions.add("unchecked");
        listOfSearchOptions.add("unfiltered");
        listOfSearchOptions.add("score-logtfidf");
        
        // build query options
        handle.withConfiguration(builder.configure()
                		.returnMetrics(false)
                		.returnQtext(false)
                		.debug(true))
              .withTransformResults(builder.rawResults())
              .setSearchOptions(listOfSearchOptions);
             
        // write query options
        optionsMgr.writeOptions("SearchOptions2", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions("SearchOptions2", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchOptions2");
	    querydef.setCriteria("bush");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
        String expectedSearchReport = "(cts:search(fn:collection(), cts:word-query(\"bush\", (\"lang=en\"), 1), (\"unchecked\",\"unfiltered\",\"score-logtfidf\"), 1))[1 to 10]";
		
		assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
                
		// release client
	    client.release();	
	} 
	
@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		
	}
}
