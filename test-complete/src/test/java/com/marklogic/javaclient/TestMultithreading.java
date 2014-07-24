package com.marklogic.javaclient;

import static org.junit.Assert.*;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.TextDocumentManager;
import org.junit.*;
public class TestMultithreading extends BasicJavaClientREST {
	
	private static String dbName = "TestMultithreadingDB";
	private static String [] fNames = {"TestMultithreadingDBDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	}

@SuppressWarnings("deprecation")
@Test
	public void testMultithreading() throws InterruptedException
	{
		ThreadClass dt1 = new ThreadClass("Thread A");
        ThreadClass dt2 = new ThreadClass("Thread B");

        dt1.start(); // this will start thread of object 1
        dt2.start(); // this will start thread of object 2
        dt2.join();
        
        DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-reader", "x", Authentication.DIGEST);
        TextDocumentManager docMgr = client.newTextDocumentManager();
        
        for (int i = 1; i <= 5; i++)
        {
        	String expectedUri = "/multithread-content-A/filename" + i + ".txt";
        	String docUri = docMgr.exists("/multithread-content-A/filename" + i + ".txt").getUri();
        	assertEquals("URI is not found", expectedUri, docUri);
        }
        
        for (int i = 1; i <= 5; i++)
        {
        	String expectedUri = "/multithread-content-B/filename" + i + ".txt";
        	String docUri = docMgr.exists("/multithread-content-B/filename" + i + ".txt").getUri();
        	assertEquals("URI is not found", expectedUri, docUri);
        }
        
        // release client
        client.release();
   }
	
	/*public void testMultithreadingSearchAndWrite() throws InterruptedException
	{
		System.out.println("testMultithreadingSearchAndWrite");
		
		ThreadWrite tw1 = new ThreadWrite("Write Thread");
		ThreadSearch ts1 = new ThreadSearch("Search Thread");

        tw1.start(); 
        ts1.start();
        tw1.join();
        ts1.join();
        
        DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
        TextDocumentManager docMgr = client.newTextDocumentManager();
        
        for (int i = 1; i <= 15; i++)
        {
        	String expectedUri = "/multithread-write/filename" + i + ".xml";
        	String docUri = docMgr.exists("/multithread-write/filename" + i + ".xml").getUri();
        	assertEquals("URI is not found", expectedUri, docUri);
        }
        
        for(int x = 0; x <= 9; x++)
        {
        	System.out.println(ts1.totalResultsArray[x]);
        	assertTrue("Search result is 0", ts1.totalResultsArray[x] != 0);
        }
        
        // release client
        client.release();
   }*/

@SuppressWarnings("deprecation")
@Test
	public void testMultithreadingMultipleSearch() throws InterruptedException
	{
		System.out.println("testMultithreadingMultipleSearch");
		
		ThreadWrite tw1 = new ThreadWrite("Write Thread");
		tw1.start();
		tw1.join();
		
		ThreadSearch ts1 = new ThreadSearch("Search Thread 1");
		ThreadSearch ts2 = new ThreadSearch("Search Thread 2");
		ThreadSearch ts3 = new ThreadSearch("Search Thread 3");
		ThreadSearch ts4 = new ThreadSearch("Search Thread 4");
		ThreadSearch ts5 = new ThreadSearch("Search Thread 5");

        ts1.start();
        ts2.start();
        ts3.start();
        ts4.start();
        ts5.start();
        
        ts1.join();
        ts2.join();
        ts3.join();
        ts4.join();
        ts5.join();
        
        long totalAllDocumentsReturned = ts1.totalAllResults + ts2.totalAllResults + ts3.totalAllResults + ts4.totalAllResults + ts5.totalAllResults;
        assertTrue("Documents count is incorrect", totalAllDocumentsReturned == 750);
   }
	@AfterClass	
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}

}


