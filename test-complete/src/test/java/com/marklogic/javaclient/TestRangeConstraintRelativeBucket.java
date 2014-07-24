package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.IOException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.javaclient.BasicJavaClientREST;
import org.junit.*;
public class TestRangeConstraintRelativeBucket extends BasicJavaClientREST {
	static String filenames[] = {"bbq1.xml", "bbq2.xml", "bbq3.xml", "bbq4.xml", "bbq5.xml"};
	static String queryOptionName = "rangeRelativeBucketConstraintOpt.xml"; 
	private static String dbName = "RangeConstraintRelBucketDB";
	private static String [] fNames = {"RangeConstraintRelBucketDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	
@BeforeClass	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
		addRangeElementAttributeIndex(dbName, "dateTime", "http://example.com", "entry", "", "date");
	}


@SuppressWarnings("deprecation")
@Test	public void testRangeConstraintRelativeBucket() throws IOException
	{
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename:filenames)
		{
			writeDocumentReaderHandle(client, filename, "/range-constraint-rel-bucket/", "XML");
		}
						
		// write the query options to the database
		setQueryOption(client, queryOptionName);
							
		// run the search
		SearchHandle resultsHandle = runSearch(client, queryOptionName, "date:older");
		
		// search result
		String result = "Matched "+resultsHandle.getTotalResults();
		String expectedResult = "Matched 5";
		assertEquals("Document match difference", expectedResult, result);
		
		// release client
		client.release();
	}
	
@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}
}
