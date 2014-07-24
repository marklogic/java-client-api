package com.marklogic.javaclient;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.io.DOMHandle;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
public class TestStructuredQueryMildNot extends BasicJavaClientREST {

	private static String dbName = "TestStructuredQueryMildNotDB";
	private static String [] fNames = {"TestStructuredQueryMildNotDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

@BeforeClass public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testStructuredQueryMildNot() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testStructuredQueryMildNot");
		
		String[] filenames = {"mildnot1.xml"};
		String queryOptionName = "mildNotOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/structured-query-mild-not/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
		StructuredQueryDefinition termQuery1 = qb.term("summer");
		StructuredQueryDefinition termQuery2 = qb.term("time");
		StructuredQueryDefinition notInFinalQuery = qb.notIn(termQuery1, termQuery2);
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(notInFinalQuery, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		//assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		//assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
	    //String expectedSearchReport = "(cts:search(fn:collection(), cts:and-query(cts:or-query((cts:element-value-query(fn:QName(\"\", \"id\"), \"00*2\", (\"lang=en\"), 1), cts:element-value-query(fn:QName(\"\", \"id\"), \"0??6\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\"), 1))[1 to 10]";
		
		//assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}
	
@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}
}
