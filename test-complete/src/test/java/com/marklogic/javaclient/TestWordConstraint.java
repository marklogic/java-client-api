package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.IOException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.javaclient.BasicJavaClientREST;
import org.junit.*;
public class TestWordConstraint extends BasicJavaClientREST {
	
	static String filenames[] = {"word-constraint-doc1.xml", "word-constraint-doc2.xml"};
	static String queryOptionName = "wordConstraintOpt.xml"; 
	private static String dbName = "WordConstraintDB";
	private static String [] fNames = {"WordConstraintDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	
@BeforeClass	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0],restServerName,8011);
	}
	
@SuppressWarnings("deprecation")
@Test	public void testElementWordConstraint() throws IOException
	{
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename:filenames)
		{
			writeDocumentReaderHandle(client, filename, "/word-constraint/", "XML");
		}

						
		// write the query options to the database
		setQueryOption(client, queryOptionName);
							
		// run the search
		SearchHandle resultsHandle = runSearch(client, queryOptionName, "my-element-word:paris");
		
		// search result
		String searchResult = returnSearchResult(resultsHandle);
						
		String expectedSearchResult = "|Matched 1 locations in /word-constraint/word-constraint-doc1.xml";
		
		System.out.println(searchResult);
		
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

