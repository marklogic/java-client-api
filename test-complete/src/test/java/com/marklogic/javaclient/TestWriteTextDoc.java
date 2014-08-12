package com.marklogic.javaclient;

import static org.junit.Assert.*;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;

import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.StringHandle;
import org.junit.*;
public class TestWriteTextDoc extends BasicJavaClientREST
{

	@BeforeClass public static void setUp() throws Exception 
		{
		  System.out.println("In setup");
		  setupJavaRESTServerWithDB( "REST-Java-Client-API-Server", 8011);
		 
		}
	
	@SuppressWarnings("deprecation")
	@Test  public void testWriteTextDoc()  
  {
    DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "admin", "admin", Authentication.DIGEST);

    String docId = "/foo/test/myFoo.txt";
    TextDocumentManager docMgr = client.newTextDocumentManager();
    docMgr.write(docId, new StringHandle().with("This is so foo"));
    assertEquals("Text document write difference", "This is so foo", docMgr.read(docId, new StringHandle()).get());
  }
	@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		deleteRESTServerWithDB("REST-Java-Client-API-Server");
	}
}
