package com.marklogic.javaclient;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.marklogic.client.query.QueryManager;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathNotExists;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;

public class TestAppServicesCollectionConstraint extends BasicJavaClientREST {

//	private String serverName = "";
	private static String dbName = "AppServicesCollectionConstraintDB";
	private static String [] fNames = {"AppServicesCollectionConstraintDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	 setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}
@After
public  void testCleanUp() throws Exception
{
	clearDB(8011);
	System.out.println("Running clear script");
}

	//@Test
	public void testWithFacet() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testWithFacet");
		
		String filename1 = "constraint1.xml";
		String filename2 = "constraint2.xml";
		String filename3 = "constraint3.xml";
		String filename4 = "constraint4.xml";
		String filename5 = "constraint5.xml";
		String queryOptionName = "collectionConstraintWithFacetOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);

	    // create and initialize a handle on the metadata
	    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();
		
	    // set the metadata
	    metadataHandle1.getCollections().addAll("http://test.com/set1");
	    metadataHandle1.getCollections().addAll("http://test.com/set5");
	    metadataHandle2.getCollections().addAll("http://test.com/set1");
	    metadataHandle3.getCollections().addAll("http://test.com/set3");
	    metadataHandle4.getCollections().addAll("http://test.com/set3/set3-1");
	    metadataHandle5.getCollections().addAll("http://test.com/set1");
	    metadataHandle5.getCollections().addAll("http://test.com/set5");

	    // write docs
	    writeDocumentUsingInputStreamHandle(client, filename1, "/collection-constraint/", metadataHandle1, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename2, "/collection-constraint/", metadataHandle2, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename3, "/collection-constraint/", metadataHandle3, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename4, "/collection-constraint/", metadataHandle4, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename5, "/collection-constraint/", metadataHandle5, "XML");
	    
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("coll:set3");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("For 1945", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("1", "string(//*[local-name()='facet-value']//@*[local-name()='count'])", resultDoc);
		assertXpathEvaluatesTo("set3", "string(//*[local-name()='facet-value'])", resultDoc);
	    
		String expectedSearchReport = "(cts:search(fn:collection(), cts:collection-query(\"http://test.com/set3\"), (\"score-logtfidf\",\"faceted\"), 1))[1 to 10]";
		
		assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}

	
	@Test
	public void testWithNoFacet() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testWithNoFacet");
		
		String filename1 = "constraint1.xml";
		String filename2 = "constraint2.xml";
		String filename3 = "constraint3.xml";
		String filename4 = "constraint4.xml";
		String filename5 = "constraint5.xml";
		String queryOptionName = "collectionConstraintWithNoFacetOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);

	    // create and initialize a handle on the metadata
	    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();
		
	    // set the metadata
	    metadataHandle1.getCollections().addAll("http://test.com/set1");
	    metadataHandle1.getCollections().addAll("http://test.com/set5");
	    metadataHandle2.getCollections().addAll("http://test.com/set1");
	    metadataHandle3.getCollections().addAll("http://test.com/set3");
	    metadataHandle4.getCollections().addAll("http://test.com/set3/set3-1");
	    metadataHandle5.getCollections().addAll("http://test.com/set1");
	    metadataHandle5.getCollections().addAll("http://test.com/set5");

	    // write docs
	    writeDocumentUsingInputStreamHandle(client, filename1, "/collection-constraint/", metadataHandle1, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename2, "/collection-constraint/", metadataHandle2, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename3, "/collection-constraint/", metadataHandle3, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename4, "/collection-constraint/", metadataHandle4, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename5, "/collection-constraint/", metadataHandle5, "XML");
	    
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("coll:set3");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
	
			System.out.println(convertXMLDocumentToString(resultDoc));	
//		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
//		assertXpathEvaluatesTo("For 1945", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
		assertXpathNotExists("//search:facet-value[@count='1']", resultDoc);
	    
		String expectedSearchReport = "(cts:search(fn:collection(), cts:collection-query(\"http://test.com/set3\"), (\"score-logtfidf\"), 1))[1 to 10]";
		
		assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}

	
//	@Test
	public void testWithWordConstraintAndGoogleGrammar() throws IOException, ParserConfigurationException, SAXException, XpathException
	{	
		System.out.println("Running testWithWordConstraintAndGoogleGrammar");
		
		String filename1 = "constraint1.xml";
		String filename2 = "constraint2.xml";
		String filename3 = "constraint3.xml";
		String filename4 = "constraint4.xml";
		String filename5 = "constraint5.xml";
		String queryOptionName = "collectionConstraintWithWordConstraintAndGoogleGrammarOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);

	    // create and initialize a handle on the metadata
	    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();
		
	    // set the metadata
	    metadataHandle1.getCollections().addAll("http://test.com/set1");
	    metadataHandle1.getCollections().addAll("http://test.com/set5");
	    metadataHandle2.getCollections().addAll("http://test.com/set1");
	    metadataHandle3.getCollections().addAll("http://test.com/set3");
	    metadataHandle4.getCollections().addAll("http://test.com/set3/set3-1");
	    metadataHandle5.getCollections().addAll("http://test.com/set1");
	    metadataHandle5.getCollections().addAll("http://test.com/set5");

	    // write docs
	    writeDocumentUsingInputStreamHandle(client, filename1, "/collection-constraint/", metadataHandle1, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename2, "/collection-constraint/", metadataHandle2, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename3, "/collection-constraint/", metadataHandle3, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename4, "/collection-constraint/", metadataHandle4, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename5, "/collection-constraint/", metadataHandle5, "XML");
	    
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("(coll:set1 AND coll:set5) AND -intitle:memex");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("Vannevar Bush", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
	    
		String expectedSearchReport = "(cts:search(fn:collection(), cts:and-query((cts:collection-query(\"http://test.com/set1\"), cts:collection-query(\"http://test.com/set5\"), cts:not-query(cts:element-word-query(fn:QName(\"\", \"title\"), \"memex\", (\"lang=en\"), 1), 1)), ()), (\"score-logtfidf\"), 1))[1 to 10]";
		
		assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}
@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,  restServerName);

	}
}
