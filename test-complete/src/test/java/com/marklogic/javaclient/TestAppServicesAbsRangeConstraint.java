package com.marklogic.javaclient;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.io.DOMHandle;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import org.custommonkey.xmlunit.exceptions.XpathException;

import org.junit.*;



public class TestAppServicesAbsRangeConstraint extends BasicJavaClientREST  {

	
	private static String dbName = "AbsRangeConstraintDB";
	private static String [] fNames = {"AbsRangeConstraintDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}

	@Test
	public void testWithVariousGrammarAndWordQuery() throws IOException, ParserConfigurationException, SAXException, XpathException
	{	
		System.out.println("Running testWithVariousGrammarAndWordQuery");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "absRangeConstraintWithVariousGrammarAndWordQueryOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/abs-range-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("(pop:high OR pop:medium) AND price:medium AND intitle:served");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("Vannevar served", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("12.34", "string(//*[local-name()='result'][1]//@*[local-name()='amt'])", resultDoc);
		assertXpathEvaluatesTo("5", "string(//*[local-name()='result'][1]//*[local-name()='popularity'])", resultDoc);
		assertXpathEvaluatesTo("1", "string(//*[local-name()='facet-value']//@*[local-name()='count'])", resultDoc);
		assertXpathEvaluatesTo("High", "string(//*[local-name()='facet-value'])", resultDoc);
	    
		//String expectedSearchReport = "(cts:search(fn:collection(), cts:and-query((cts:or-query((cts:element-range-query(fn:QName(\"\", \"popularity\"), \"&gt;=\", xs:int(\"5\"), (), 1), cts:and-query((cts:element-range-query(fn:QName(\"\", \"popularity\"), \"&gt;=\", xs:int(\"3\"), (), 1), cts:element-range-query(fn:QName(\"\", \"popularity\"), \"&lt;\", xs:int(\"5\"), (), 1)), ()))), cts:element-attribute-range-query(fn:QName(\"http://cloudbank.com\", \"price\"), fn:QName(\"\", \"amt\"), \"&gt;=\", 3.0, (), 1), cts:element-attribute-range-query(fn:QName(\"http://cloudbank.com\", \"price\"), fn:QName(\"\", \"amt\"), \"&lt;\", 14.0, (), 1), cts:element-word-query(fn:QName(\"\", \"title\"), \"served\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\"), 1))[1 to 10]";
		
		//assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}
	
	@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
//		super.tearDown();
	}
}
