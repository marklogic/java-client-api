package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.Transaction;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class TestBiTemporal extends BasicJavaClientREST{

	private static String dbName = "TestBiTemporalJava";
	private static String [] fNames = {"TestBiTemporalJava-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private  DatabaseClient client ;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
		ConnectedRESTQA.addRangeElementIndex(dbName, "dateTime", "", "javaERISystemStart");
		ConnectedRESTQA.addRangeElementIndex(dbName, "dateTime", "", "javaERISystemEnd");
		ConnectedRESTQA.addRangeElementIndex(dbName, "dateTime", "", "javaERIValidStart");
		ConnectedRESTQA.addRangeElementIndex(dbName, "dateTime", "", "javaERIValidEnd");
		
		// Temporal axis must be created before temporal collection associated with those axes is created
		ConnectedRESTQA.addElementRangeIndexTemporalAxis(dbName, "javaERISystem", "", "javaERISystemStart", "", "javaERISystemEnd");
		ConnectedRESTQA.addElementRangeIndexTemporalAxis(dbName, "javaERIValid", "", "javaERIValidStart", "", "javaERIValidEnd");
		ConnectedRESTQA.addElementRangeIndexTemporalCollection(dbName, "javaERITemporalCollection", "javaERISystem", "javaERIValid");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down" );
		
		// Temporal collection needs to be delete before temporal axis associated with it can be deleted
		ConnectedRESTQA.deleteElementRangeIndexTemporalCollection(dbName, "javaERITemporalCollection");
		ConnectedRESTQA.deleteElementRangeIndexTemporalAxis(dbName, "javaERIValid");
		ConnectedRESTQA.deleteElementRangeIndexTemporalAxis(dbName, "javaERISystem");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}

	@Before
	public void setUp() throws Exception {
		client = DatabaseClientFactory.newClient("localhost", restPort, "rest-admin", "x", Authentication.DIGEST);
	}

	@After
	public void tearDown() throws Exception {
		// release client
		client.release();
	}
	
	@Test
	public void test() { }
}
