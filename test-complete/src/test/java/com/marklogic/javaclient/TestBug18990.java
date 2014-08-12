package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
public class TestBug18990 extends BasicJavaClientREST {

	private static String dbName = "TestBug18990DB";
	private static String [] fNames = {"TestBug18990DB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}

@SuppressWarnings("deprecation")
@Test
	public void testBug18990() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testBug18990");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "valueConstraintWildCardOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// set query option validation to true and server logger to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setServerRequestLogging(true);
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/bug18990/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
		StructuredQueryDefinition valueConstraintQuery1 = qb.valueConstraint("id", "00*2");
		StructuredQueryDefinition valueConstraintQuery2 = qb.valueConstraint("id", "0??6");
		StructuredQueryDefinition orFinalQuery = qb.or(valueConstraintQuery1, valueConstraintQuery2);
		
		// create handle
		StringHandle resultsHandle = new StringHandle().withFormat(Format.JSON);
		queryMgr.search(orFinalQuery, resultsHandle);
		
		// get the result
		String resultDoc = resultsHandle.get();
		
		System.out.println(resultDoc);
		
	    assertTrue("Result in json is not correct", resultDoc.contains("{\"snippet-format\":\"raw\",\"total\":4,\"start\":1,\"page-length\":10,\"results\":[{\"index\":1,\"uri\":\"/bug18990/constraint5.xml\""));
		
	    // turn off server logger
	    srvMgr.setServerRequestLogging(false);
	    srvMgr.writeConfiguration();
	    
		// release client
		client.release();		
	}
	@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}
}
