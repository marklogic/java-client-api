package com.marklogic.javaclient;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;

import java.io.FileNotFoundException;

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
public class TestQueryOptionBuilderSearchableExpression extends BasicJavaClientREST {

	private static String dbName = "TestQueryOptionBuilderSearchableExpressionDB";
	private static String [] fNames = {"TestQueryOptionBuilderSearchableExpressionDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

@BeforeClass public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0],restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testSearchableExpressionChildAxis() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testSearchableExpressionChildAxis");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/search-expr-child-axis/", "XML");
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
              .withTransformResults(builder.rawResults())
              .withSearchableExpression(builder.searchableExpression("//root/child::p"));
             
        // write query options
        optionsMgr.writeOptions("SearchableExpressionChildAxis", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions("SearchableExpressionChildAxis", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    //String expectedOutput = "{\"options\":{\"sort-order\":[{\"direction\":\"descending\", \"score\":null},{\"type\":\"xs:decimal\", \"direction\":\"ascending\", \"attribute\":{\"ns\":\"\", \"name\":\"amt\"}, \"element\":{\"ns\":\"http:\\/\\/cloudbank.com\", \"name\":\"price\"}}], \"return-metrics\":false, \"return-qtext\":false, \"transform-results\":{\"apply\":\"raw\"}}}";
	    //assertTrue("Query Options in json is incorrect", output.contains(expectedOutput));
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchableExpressionChildAxis");
		querydef.setCriteria("bush");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("The Bush article described a device called a Memex.", "string(//*[local-name()='result'][1]//*[local-name()='p'])", resultDoc);
		assertXpathEvaluatesTo("Vannevar Bush wrote an article for The Atlantic Monthly", "string(//*[local-name()='result'][2]//*[local-name()='p'])", resultDoc);
                
		// release client
	    client.release();	
	} 


@SuppressWarnings("deprecation")
@Test	public void testSearchableExpressionDescendantAxis() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testSearchableExpressionDescendantAxis");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/search-expr-desc-axis/", "XML");
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
              .withTransformResults(builder.rawResults())
              .withSearchableExpression(builder.searchableExpression("/root/descendant::title"));
             
        // write query options
        optionsMgr.writeOptions("SearchableExpressionDescendantAxis", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.XML);
		optionsMgr.readOptions("SearchableExpressionDescendantAxis", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    //String expectedOutput = "{\"options\":{\"sort-order\":[{\"direction\":\"descending\", \"score\":null},{\"type\":\"xs:decimal\", \"direction\":\"ascending\", \"attribute\":{\"ns\":\"\", \"name\":\"amt\"}, \"element\":{\"ns\":\"http:\\/\\/cloudbank.com\", \"name\":\"price\"}}], \"return-metrics\":false, \"return-qtext\":false, \"transform-results\":{\"apply\":\"raw\"}}}";
	    //assertTrue("Query Options in json is incorrect", output.contains(expectedOutput));
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchableExpressionDescendantAxis");
		querydef.setCriteria("bush OR memex");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("The Bush article", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("Vannevar Bush", "string(//*[local-name()='result'][2]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("The memex", "string(//*[local-name()='result'][3]//*[local-name()='title'])", resultDoc);
                
		// release client
	    client.release();	
	}


@SuppressWarnings("deprecation")
@Test	public void testSearchableExpressionOrOperator() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testSearchableExpressionOrOperator");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/search-expr-or-op/", "XML");
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
              .withTransformResults(builder.snippetTransform(30, 4, 200))
              .withSearchableExpression(builder.searchableExpression("//(title|id)"));
             
        // write query options
        optionsMgr.writeOptions("SearchableExpressionOrOperator", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.XML);
		optionsMgr.readOptions("SearchableExpressionOrOperator", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    //String expectedOutput = "{\"options\":{\"sort-order\":[{\"direction\":\"descending\", \"score\":null},{\"type\":\"xs:decimal\", \"direction\":\"ascending\", \"attribute\":{\"ns\":\"\", \"name\":\"amt\"}, \"element\":{\"ns\":\"http:\\/\\/cloudbank.com\", \"name\":\"price\"}}], \"return-metrics\":false, \"return-qtext\":false, \"transform-results\":{\"apply\":\"raw\"}}}";
	    //assertTrue("Query Options in json is incorrect", output.contains(expectedOutput));
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchableExpressionOrOperator");
		querydef.setCriteria("bush OR 0011");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("Bush", "string(//*[local-name()='result'][1]//*[local-name()='highlight'])", resultDoc);
		assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][2]//*[local-name()='highlight'])", resultDoc);
		assertXpathEvaluatesTo("Bush", "string(//*[local-name()='result'][3]//*[local-name()='highlight'])", resultDoc);
                
		// release client
	    client.release();	
	} 


@SuppressWarnings("deprecation")
@Test	public void testSearchableExpressionDescendantOrSelf() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testSearchableExpressionDescendantOrSelf");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/search-expr-desc-or-self/", "XML");
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
              .withTransformResults(builder.snippetTransform(30, 10, 200))
              .withSearchableExpression(builder.searchableExpression("/descendant-or-self::root"));
             
        // write query options
        optionsMgr.writeOptions("SearchableExpressionDescendantOrSelf", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.XML);
		optionsMgr.readOptions("SearchableExpressionDescendantOrSelf", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    //String expectedOutput = "{\"options\":{\"sort-order\":[{\"direction\":\"descending\", \"score\":null},{\"type\":\"xs:decimal\", \"direction\":\"ascending\", \"attribute\":{\"ns\":\"\", \"name\":\"amt\"}, \"element\":{\"ns\":\"http:\\/\\/cloudbank.com\", \"name\":\"price\"}}], \"return-metrics\":false, \"return-qtext\":false, \"transform-results\":{\"apply\":\"raw\"}}}";
	    //assertTrue("Query Options in json is incorrect", output.contains(expectedOutput));
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchableExpressionDescendantOrSelf");
		querydef.setCriteria("Bush");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		//assertXpathEvaluatesTo("Vannevar <search:highlight>Bush</search:highlight>", "string(//*[local-name()='result'][1]//*[local-name()='match'][1])", resultDoc);
		//assertXpathEvaluatesTo("Vannevar <search:highlight>Bush</search:highlight> wrote an article for The Atlantic Monthly", "string(//*[local-name()='result'][1]//*[local-name()='match'][2])", resultDoc);
		//assertXpathEvaluatesTo("The <search:highlight>Bush</search:highlight> article", "string(//*[local-name()='result'][2]//*[local-name()='match'][1])", resultDoc);
		//assertXpathEvaluatesTo("The <search:highlight>Bush</search:highlight> article described a device called a Memex.", "string(//*[local-name()='result'][2]//*[local-name()='match'][2])", resultDoc);
                
		// release client
	    client.release();	
	} 
	

@SuppressWarnings("deprecation")
@Test	public void testSearchableExpressionFunction() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testSearchableExpressionFunction");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/search-expr-func/", "XML");
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
              .withTransformResults(builder.snippetTransform(30, 10, 200))
              .withSearchableExpression(builder.searchableExpression("//p[contains(.,\"groundbreaking\")]"));
             
        // write query options
        optionsMgr.writeOptions("SearchableExpressionFunction", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.XML);
		optionsMgr.readOptions("SearchableExpressionFunction", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    //String expectedOutput = "{\"options\":{\"sort-order\":[{\"direction\":\"descending\", \"score\":null},{\"type\":\"xs:decimal\", \"direction\":\"ascending\", \"attribute\":{\"ns\":\"\", \"name\":\"amt\"}, \"element\":{\"ns\":\"http:\\/\\/cloudbank.com\", \"name\":\"price\"}}], \"return-metrics\":false, \"return-qtext\":false, \"transform-results\":{\"apply\":\"raw\"}}}";
	    //assertTrue("Query Options in json is incorrect", output.contains(expectedOutput));
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchableExpressionFunction");
		querydef.setCriteria("atlantic");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/search-expr-func/constraint3.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
                
		// release client
	    client.release();	
	} 
		
@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,restServerName);
		
	}
}
