package com.marklogic.client.functionaltest;

import static org.junit.Assert.*;

import javax.annotation.Resource.AuthenticationType;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;

public class TestRuntimeDBselection extends BasicJavaClientREST {
	private static String dbName = "TestRuntimeDB";
	private static String [] fNames = {"TestRuntimeDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private  DatabaseClient client ;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("In setup");
		 setupJavaRESTServer(dbName, fNames[0], restServerName,restPort,false);
	     createUserRolesWithPrevilages("test-eval","xdbc:eval", "xdbc:eval-in","xdmp:eval-in","any-uri","xdbc:invoke");
	     createRESTUser("eval-user", "x", "test-eval","rest-admin","rest-writer","rest-reader");
	    
		 System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down" );
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		deleteRESTUser("eval-user");
		deleteUserRole("test-eval");
	}

	@Test
	public void testRuntimeDBclientWithDefaultUser() throws Exception {
		associateRESTServerWithDefaultUser(restServerName,"eval-user","application-level");
		client = DatabaseClientFactory.newClient("localhost", restPort,dbName);
		String insertJSON = "xdmp:document-insert(\"test2.json\",object-node {\"test\":\"hello\"})";
		client.newServerEval().xquery(insertJSON).eval();
		String query1 = "fn:count(fn:doc())";
		int response = client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
		assertEquals("count of documents ",1,response);
		String query2 ="declareUpdate();xdmp.documentDelete(\"test2.json\");";
		client.newServerEval().javascript(query2).eval();
		int response2 = client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
		assertEquals("count of documents ",0,response2);
		client.release();
		associateRESTServerWithDefaultUser(restServerName,"nobody","digest");
	}
	//Issue 184 exists 
	@Test
	public void testRuntimeDBclientWithDifferentAuthType() throws Exception {
		associateRESTServerWithDefaultUser(restServerName,"nobody","basic");
		client = DatabaseClientFactory.newClient("localhost", restPort,dbName,"eval-user","x",Authentication.BASIC);
		String insertJSON = "xdmp:document-insert(\"test2.json\",object-node {\"test\":\"hello\"})";
		client.newServerEval().xquery(insertJSON).eval();
		String query1 = "fn:count(fn:doc())";
		int response = client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
		assertEquals("count of documents ",1,response);
		String query2 ="declareUpdate();xdmp.documentDelete(\"test2.json\");";
		client.newServerEval().javascript(query2).eval();
		int response2 = client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
		assertEquals("count of documents ",0,response2);
		client.release();
		associateRESTServerWithDefaultUser(restServerName,"nobody","digest");
	}
	//issue 141 user with no privileges for eval
	@Test(expected=FailedRequestException.class)
	public void testRuntimeDBclientWithNoPrivUser() throws Exception {
		
		client = DatabaseClientFactory.newClient("localhost", restPort,dbName,"rest-admin","x",Authentication.BASIC);
		String insertJSON = "xdmp:document-insert(\"test2.json\",object-node {\"test\":\"hello\"})";
		client.newServerEval().xquery(insertJSON).eval();
		String query1 = "fn:count(fn:doc())";
		int response = client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
		assertEquals("count of documents ",1,response);
		String query2 ="declareUpdate();xdmp.documentDelete(\"test2.json\");";
		client.newServerEval().javascript(query2).eval();
		int response2 = client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
		assertEquals("count of documents ",0,response2);
		client.release();
		
	}
}
