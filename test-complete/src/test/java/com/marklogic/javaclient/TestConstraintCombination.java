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
import com.marklogic.client.io.DocumentMetadataHandle;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
public class TestConstraintCombination extends BasicJavaClientREST {

	private static String dbName = "ConstraintCombinationDB";
	private static String [] fNames = {"ConstraintCombinationDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  addRangeElementAttributeIndex(dbName, "dateTime", "http://example.com", "entry", "", "date");
	  addRangeElementIndex(dbName, "int", "http://example.com", "scoville");
	  addRangeElementIndex(dbName, "decimal", "http://example.com", "rating");
	  addField(dbName, "bbqtext");
	  includeElementField(dbName, "bbqtext", "http://example.com", "title");
	  includeElementField(dbName, "bbqtext", "http://example.com", "abstract");
	  enableCollectionLexicon(dbName);
	  enableTrailingWildcardSearches(dbName);
	}

@SuppressWarnings("deprecation")
@Test
	public void testConstraintCombination() throws IOException, ParserConfigurationException, SAXException, XpathException
	{	
		System.out.println("Running testConstraintCombination");
		
		String filename1 = "bbq1.xml";
		String filename2 = "bbq2.xml";
		String filename3 = "bbq3.xml";
		String filename4 = "bbq4.xml";
		String filename5 = "bbq5.xml";
		
		String queryOptionName = "constraintCombinationOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
	    // create and initialize a handle on the metadata
	    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();
		
	    // set the metadata
	    metadataHandle1.getCollections().addAll("http://bbq.com/contributor/AuntSally");
	    metadataHandle2.getCollections().addAll("http://bbq.com/contributor/BigTex");
	    metadataHandle3.getCollections().addAll("http://bbq.com/contributor/Dubois");
	    metadataHandle4.getCollections().addAll("http://bbq.com/contributor/BigTex");
	    metadataHandle5.getCollections().addAll("http://bbq.com/contributor/Dorothy");
	    
		// write docs
	    writeDocumentUsingInputStreamHandle(client, filename1, "/combination-constraint/", metadataHandle1, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename2, "/combination-constraint/", metadataHandle2, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename3, "/combination-constraint/", metadataHandle3, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename4, "/combination-constraint/", metadataHandle4, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename5, "/combination-constraint/", metadataHandle5, "XML");
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("intitle:BBQ AND flavor:smok* AND heat:moderate AND contributor:AuntSally AND (summary:Southern AND summary:classic)");
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/combination-constraint/bbq1.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
		assertXpathEvaluatesTo("1", "string(//*[local-name()='facet']//*[local-name()='facet-value']//@*[local-name()='count'])", resultDoc);
		assertXpathEvaluatesTo("Moderate (500 - 2500)", "string(//*[local-name()='facet']//*[local-name()='facet-value'])", resultDoc);
	    
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test
	public void testConstraintCombinationWordAndCollection() throws IOException, ParserConfigurationException, SAXException, XpathException
	{	
		System.out.println("Running testConstraintCombinationWordAndCollection");
		
		String filename1 = "bbq1.xml";
		String filename2 = "bbq2.xml";
		String filename3 = "bbq3.xml";
		String filename4 = "bbq4.xml";
		String filename5 = "bbq5.xml";
		
		String queryOptionName = "constraintCombinationOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
	    // create and initialize a handle on the metadata
	    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();
		
	    // set the metadata
	    metadataHandle1.getCollections().addAll("http://bbq.com/contributor/AuntSally");
	    metadataHandle2.getCollections().addAll("http://bbq.com/contributor/BigTex");
	    metadataHandle3.getCollections().addAll("http://bbq.com/contributor/Dubois");
	    metadataHandle4.getCollections().addAll("http://bbq.com/contributor/BigTex");
	    metadataHandle5.getCollections().addAll("http://bbq.com/contributor/Dorothy");
	    
		// write docs
	    writeDocumentUsingInputStreamHandle(client, filename1, "/combination-constraint/", metadataHandle1, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename2, "/combination-constraint/", metadataHandle2, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename3, "/combination-constraint/", metadataHandle3, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename4, "/combination-constraint/", metadataHandle4, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename5, "/combination-constraint/", metadataHandle5, "XML");
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("intitle:pigs contributor:BigTex");
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/combination-constraint/bbq4.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
		
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test
	public void testConstraintCombinationFieldAndRange() throws IOException, ParserConfigurationException, SAXException, XpathException
	{	
		System.out.println("testConstraintCombinationFieldAndRange");
		
		String filename1 = "bbq1.xml";
		String filename2 = "bbq2.xml";
		String filename3 = "bbq3.xml";
		String filename4 = "bbq4.xml";
		String filename5 = "bbq5.xml";
		
		String queryOptionName = "constraintCombinationOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
	    // create and initialize a handle on the metadata
	    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
	    DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();
		
	    // set the metadata
	    metadataHandle1.getCollections().addAll("http://bbq.com/contributor/AuntSally");
	    metadataHandle2.getCollections().addAll("http://bbq.com/contributor/BigTex");
	    metadataHandle3.getCollections().addAll("http://bbq.com/contributor/Dubois");
	    metadataHandle4.getCollections().addAll("http://bbq.com/contributor/BigTex");
	    metadataHandle5.getCollections().addAll("http://bbq.com/contributor/Dorothy");
	    
		// write docs
	    writeDocumentUsingInputStreamHandle(client, filename1, "/combination-constraint/", metadataHandle1, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename2, "/combination-constraint/", metadataHandle2, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename3, "/combination-constraint/", metadataHandle3, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename4, "/combination-constraint/", metadataHandle4, "XML");
	    writeDocumentUsingInputStreamHandle(client, filename5, "/combination-constraint/", metadataHandle5, "XML");
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("summary:Louisiana AND summary:sweet heat:moderate");
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/combination-constraint/bbq3.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
		
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
