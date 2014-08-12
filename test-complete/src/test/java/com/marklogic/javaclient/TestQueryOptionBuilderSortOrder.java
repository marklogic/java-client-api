package com.marklogic.javaclient;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.Format;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.admin.config.QueryOptions.QuerySortOrder.Direction;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.*;
public class TestQueryOptionBuilderSortOrder extends BasicJavaClientREST {

	private static String dbName = "TestQueryOptionBuilderSortOrderDB";
	private static String [] fNames = {"TestQueryOptionBuilderSortOrderDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

@BeforeClass	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testSortOrderDescendingScore() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testSortOrderDescendingScore");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/sort-desc-score/", "XML");
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
              .withSortOrders(builder.sortByScore(Direction.DESCENDING));
               
        // write query options
        optionsMgr.writeOptions("SortOrderDescendingScore", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.XML);
		optionsMgr.readOptions("SortOrderDescendingScore", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("SortOrderDescendingScore");
		querydef.setCriteria("bush OR memex");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][3]//*[local-name()='id'])", resultDoc);
                
		// release client
	    client.release();	
	}


@SuppressWarnings("deprecation")
@Test	public void testSortOrderPrimaryDescScoreSecondaryAscDate() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testSortOrderPrimaryDescScoreSecondaryAscDate");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/sort-desc-score-asc-date/", "XML");
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
              .withSortOrders(builder.sortByScore(Direction.DESCENDING), builder.sortOrder(builder.elementRangeIndex(new QName("http://purl.org/dc/elements/1.1/", "date"), builder.rangeType("xs:date")), Direction.ASCENDING));
             
        // write query options
        optionsMgr.writeOptions("SortOrderPrimaryDescScoreSecondaryAscDate", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions("SortOrderPrimaryDescScoreSecondaryAscDate", readHandle);
	    String output = readHandle.get();
	    System.out.println(output + "testSortOrderPrimaryDescScoreSecondaryAscDate");
	    
	    //String expectedOutput = "{\"options\":{\"sort-order\":[{\"direction\":\"descending\", \"score\":null},{\"direction\":\"ascending\", \"type\":\"xs:date\", \"element\":{\"name\":\"date\",\"ns\":\"http:\\/\\/purl.org\\/dc\\/elements\\/1.1\\/\"}}], \"return-metrics\":false, \"return-qtext\":false, \"transform-results\":{\"apply\":\"raw\"}}}";
	      String expectedOutput = "{\"options\":{\"return-metrics\":false, \"return-qtext\":false, \"sort-order\":[{\"direction\":\"descending\", \"score\":null}, {\"type\":\"xs:date\", \"direction\":\"ascending\", \"element\":{\"ns\":\"http:\\/\\/purl.org\\/dc\\/elements\\/1.1\\/\", \"name\":\"date\"}}], \"transform-results\":{\"apply\":\"raw\"}}}";
	    assertTrue("Query Options in json is incorrect", output.contains(expectedOutput));
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("SortOrderPrimaryDescScoreSecondaryAscDate");
		querydef.setCriteria("bush OR memex");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][3]//*[local-name()='id'])", resultDoc);
                
		// release client
	    client.release();	
	}


@SuppressWarnings("deprecation")
@Test	public void testMultipleSortOrder() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testMultipleSortOrder");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/mult-sort-order/", "XML");
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
              .withSortOrders(builder.sortByScore(Direction.DESCENDING), 
            		          builder.sortOrder(builder.elementRangeIndex(new QName("", "popularity"), builder.rangeType("xs:int")), Direction.ASCENDING),
            		          builder.sortOrder(builder.elementRangeIndex(new QName("", "title"), builder.rangeType("xs:string")), Direction.DESCENDING));
             
        // write query options
        optionsMgr.writeOptions("MultipleSortOrder", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions("MultipleSortOrder", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    String expectedOutput = "{\"options\":{\"return-metrics\":false, \"return-qtext\":false, \"sort-order\":[{\"direction\":\"descending\", \"score\":null}, {\"type\":\"xs:int\", \"direction\":\"ascending\", \"element\":{\"ns\":\"\", \"name\":\"popularity\"}}, {\"type\":\"xs:string\", \"direction\":\"descending\", \"element\":{\"ns\":\"\", \"name\":\"title\"}}], \"transform-results\":{\"apply\":\"raw\"}}}";
	    assertTrue("Query Options in json is incorrect", output.contains(expectedOutput));
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("MultipleSortOrder");
		querydef.setCriteria("Vannevar OR memex");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("4", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0024", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][3]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][4]//*[local-name()='id'])", resultDoc);
                
		// release client
	    client.release();	
	} 


@SuppressWarnings("deprecation")
@Test	public void testSortOrderAttribute() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testSortOrderAttribute");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/attr-sort-order/", "XML");
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
              .withSortOrders(builder.sortByScore(Direction.DESCENDING), 
            		          builder.sortOrder(builder.elementAttributeRangeIndex(new QName("http://cloudbank.com", "price"), new QName("", "amt"), builder.rangeType("xs:decimal")), Direction.ASCENDING));
             
        // write query options
        optionsMgr.writeOptions("SortOrderAttribute", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions("SortOrderAttribute", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    String expectedOutput = "{\"options\":{\"return-metrics\":false, \"return-qtext\":false, \"sort-order\":[{\"direction\":\"descending\", \"score\":null}, {\"type\":\"xs:decimal\", \"direction\":\"ascending\", \"attribute\":{\"ns\":\"\", \"name\":\"amt\"}, \"element\":{\"ns\":\"http:\\/\\/cloudbank.com\", \"name\":\"price\"}}], \"transform-results\":{\"apply\":\"raw\"}}}";
	    assertTrue("Query Options in json is incorrect", output.contains(expectedOutput));
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("SortOrderAttribute");
		querydef.setCriteria("Bush OR Memex");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][3]//*[local-name()='id'])", resultDoc);
                
		// release client
	    client.release();	
	}

@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,restServerName);
	}
}
