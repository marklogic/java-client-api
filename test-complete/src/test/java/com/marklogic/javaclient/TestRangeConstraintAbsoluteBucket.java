package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.IOException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.javaclient.BasicJavaClientREST;
import org.junit.*;
public class TestRangeConstraintAbsoluteBucket extends BasicJavaClientREST{
	static String filenames[] = {"bbq1.xml", "bbq2.xml", "bbq3.xml", "bbq4.xml", "bbq5.xml"};
	static String queryOptionName = "rangeAbsoluteBucketConstraintOpt.xml"; 
	private static String dbName = "RangeConstraintAbsBucketDB";
	private static String [] fNames = {"RangeConstraintAbsBucketDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	
@BeforeClass	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
		addRangeElementIndex(dbName, "int", "http://example.com", "scoville");
	}


@SuppressWarnings("deprecation")
@Test	public void testRangeConstraintAbsoluteBucket() throws IOException
	{
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename:filenames)
		{
			writeDocumentReaderHandle(client, filename, "/range-constraint-abs-bucket/", "XML");
		}
						
		// write the query options to the database
		setQueryOption(client, queryOptionName);
							
		// run the search
		SearchHandle resultsHandle = runSearch(client, queryOptionName, "heat:moderate");
		
		// search result
		String searchResult = returnSearchResult(resultsHandle);
						
		String expectedSearchResult = "|Matched 1 locations in /range-constraint-abs-bucket/bbq1.xml|Matched 1 locations in /range-constraint-abs-bucket/bbq3.xml|Matched 1 locations in /range-constraint-abs-bucket/bbq5.xml";
		assertEquals("Search result difference", expectedSearchResult, searchResult);
						
		// release client
		client.release();
	}
	
@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);

	}
}
