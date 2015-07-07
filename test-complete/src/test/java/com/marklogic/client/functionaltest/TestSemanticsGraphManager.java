package com.marklogic.client.functionaltest;

import org.junit.*;
import org.junit.runners.MethodSorters;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSemanticsGraphManager extends BasicJavaClientREST {

	private static String dbName = "SemanticsDB";
	private static String [] fNames = {"SemanticsDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	  private static int uberPort = 8000;
	 private DatabaseClient adminClient = null;
	  private DatabaseClient writerClient = null;
	  private DatabaseClient readerClient = null;
	  private DatabaseClient evalClient = null;
	  
	@BeforeClass 
	public static void setUpBeforeClass() throws Exception 
	{
		System.out.println("In setup");

		setupJavaRESTServer(dbName, fNames[0],  restServerName,8011);
		setupAppServicesConstraint(dbName);
		enableCollectionLexicon(dbName);
		enableTripleIndex(dbName);
	}

	
	 @AfterClass
	  public static void tearDownAfterClass() throws Exception {
	    System.out.println("In tear down");

	    // Delete database first. Otherwise axis and collection cannot be deleted
	    tearDownJavaRESTServer(dbName, fNames, restServerName);
	    deleteRESTUser("eval-user");
	    deleteUserRole("test-eval");
	  }
	
	@After
	public void testCleanUp() throws Exception
	{
		clearDB(restPort);
		adminClient.release();
		System.out.println("Running clear script");
	}
	
	
	@Before
	  public void setUp() throws Exception {
	    createUserRolesWithPrevilages("test-eval","xdbc:eval", "xdbc:eval-in","xdmp:eval-in","any-uri","xdbc:invoke");
	    createRESTUser("eval-user", "x", "test-eval","rest-admin","rest-writer","rest-reader");
	    adminClient = DatabaseClientFactory.newClient("localhost", restPort, dbName,
	        "rest-admin", "x", Authentication.DIGEST);
	    writerClient = DatabaseClientFactory.newClient("localhost", restPort, dbName,
	        "rest-writer", "x", Authentication.DIGEST);
	    readerClient = DatabaseClientFactory.newClient("localhost", restPort, dbName,
	        "rest-reader", "x", Authentication.DIGEST);   
	    evalClient = DatabaseClientFactory.newClient("localhost", uberPort, dbName,
	        "eval-user", "x", Authentication.DIGEST);           
	  }

	 
	
}
