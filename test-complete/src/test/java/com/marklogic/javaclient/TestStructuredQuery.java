
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
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
public class TestStructuredQuery extends BasicJavaClientREST {

	private static String dbName = "TestStructuredQueryDB";
	private static String [] fNames = {"TestStructuredQueryDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

@BeforeClass	public static void setUp() throws Exception 
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

@SuppressWarnings("deprecation")
@Test	public void testStructuredQuery() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testStructuredQuery");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "valueConstraintWildCardOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/structured-query/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
		StructuredQueryDefinition t = qb.valueConstraint("id", "0026");
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(t, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
	    		
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testStructuredQueryJSON() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testStructuredQueryJSON");
		
		String[] filenames = {"constraint1.json", "constraint2.json", "constraint3.json", "constraint4.json", "constraint5.json"};
		String queryOptionName = "valueConstraintWildCardOpt.json";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/structured-query/", "JSON");
		}
		setJSONQueryOption(client, queryOptionName);
//		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create value query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
		StructuredQueryDefinition t = qb.value(qb.jsonProperty("popularity"),"4");
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(t, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/structured-query/constraint2.json", "string(//*[local-name()='result'][1]//@*[local-name()='uri'])", resultDoc);
	    
		//create new word query def
		StructuredQueryDefinition t1 = qb.word(qb.jsonProperty("id"), "0012");
		// create handle
		DOMHandle resultsHandle1 = new DOMHandle();
		queryMgr.search(t1, resultsHandle1);
		
		// get the result
		Document resultDoc1 = resultsHandle1.get();
		System.out.println(convertXMLDocumentToString(resultDoc1));
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc1);
		assertXpathEvaluatesTo("/structured-query/constraint2.json", "string(//*[local-name()='result'][1]//@*[local-name()='uri'])", resultDoc1);
		
		//create new range word query def
		StructuredQueryDefinition t2 = qb.range(qb.jsonProperty("price"), "xs:integer", Operator.GE, "0.1");
		// create handle
		DOMHandle resultsHandle2 = new DOMHandle();
		queryMgr.search(t2, resultsHandle2);
		
		// get the result
		Document resultDoc2 = resultsHandle2.get();
		System.out.println(convertXMLDocumentToString(resultDoc2));
		assertXpathEvaluatesTo("5", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc2);
	
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testValueConstraintWildCard() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testValueConstraintWildCard");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "valueConstraintWildCardOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/structured-query/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
		StructuredQueryDefinition valueConstraintQuery1 = qb.valueConstraint("id", "00*2");
		StructuredQueryDefinition valueConstraintQuery2 = qb.valueConstraint("id", "0??6");
		StructuredQueryDefinition orFinalQuery = qb.or(valueConstraintQuery1, valueConstraintQuery2);
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(orFinalQuery, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
	    
		//String expectedSearchReport = "(cts:search(fn:collection(), cts:and-query(cts:or-query((cts:element-value-query(fn:QName(\"\", \"id\"), \"00*2\", (\"lang=en\"), 1), cts:element-value-query(fn:QName(\"\", \"id\"), \"0??6\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\"), 1))[1 to 10]";
		
		//assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testAndNotQuery() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testAndNotQuery");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "valueConstraintWildCardOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/structured-query-andnot/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
		StructuredQueryDefinition termQuery1 = qb.term("Atlantic");
		StructuredQueryDefinition termQuery2 = qb.term("Monthly");
		StructuredQueryDefinition termQuery3 = qb.term("Bush");
		StructuredQueryDefinition andQuery = qb.and(termQuery1, termQuery2);
		StructuredQueryDefinition andNotFinalQuery = qb.andNot(andQuery, termQuery3);
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(andNotFinalQuery, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0113", "string(//*[local-name()='result']//*[local-name()='id'])", resultDoc);
		//assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
	    
		//String expectedSearchReport = "(cts:search(fn:collection(), cts:and-query(cts:or-query((cts:element-value-query(fn:QName(\"\", \"id\"), \"00*2\", (\"lang=en\"), 1), cts:element-value-query(fn:QName(\"\", \"id\"), \"0??6\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\"), 1))[1 to 10]";
		
		//assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testNearQuery() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testNearQuery");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "valueConstraintWildCardOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/structured-query-near/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
		StructuredQueryDefinition termQuery1 = qb.term("Bush");
		StructuredQueryDefinition termQuery2 = qb.term("Atlantic");
		StructuredQueryDefinition nearQuery = qb.near(6, 1.0, StructuredQueryBuilder.Ordering.UNORDERED, termQuery1, termQuery2);
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(nearQuery, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0011", "string(//*[local-name()='result']//*[local-name()='id'])", resultDoc);
		//assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
	    
		//String expectedSearchReport = "(cts:search(fn:collection(), cts:and-query(cts:or-query((cts:element-value-query(fn:QName(\"\", \"id\"), \"00*2\", (\"lang=en\"), 1), cts:element-value-query(fn:QName(\"\", \"id\"), \"0??6\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\"), 1))[1 to 10]";
		
		//assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testDirectoryQuery() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testDirectoryQuery");
		
		String[] filenames1 = {"constraint1.xml", "constraint2.xml", "constraint3.xml"};
		String[] filenames2 = {"constraint4.xml", "constraint5.xml"};
		String queryOptionName = "valueConstraintWildCardOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
				
		// write docs
		for(String filename1 : filenames1)
		{
			writeDocumentUsingInputStreamHandle(client, filename1, "/dir1/dir2/", "XML");
		}
		
		// write docs
		for(String filename2 : filenames2)
		{
			writeDocumentUsingInputStreamHandle(client, filename2, "/dir3/dir4/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
		StructuredQueryDefinition termQuery = qb.term("Memex");
		StructuredQueryDefinition dirQuery = qb.directory(true, "/dir3/");
		StructuredQueryDefinition andFinalQuery = qb.and(termQuery, dirQuery);
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(andFinalQuery, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result']//*[local-name()='id'])", resultDoc);
	    
		//String expectedSearchReport = "(cts:search(fn:collection(), cts:and-query(cts:or-query((cts:element-value-query(fn:QName(\"\", \"id\"), \"00*2\", (\"lang=en\"), 1), cts:element-value-query(fn:QName(\"\", \"id\"), \"0??6\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\"), 1))[1 to 10]";
		
		//assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testDocumentQuery() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testDocumentQuery");
		
		String[] filenames1 = {"constraint1.xml", "constraint2.xml", "constraint3.xml"};
		String[] filenames2 = {"constraint4.xml", "constraint5.xml"};
		String queryOptionName = "valueConstraintWildCardOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
				
		// write docs
		for(String filename1 : filenames1)
		{
			writeDocumentUsingInputStreamHandle(client, filename1, "/dir1/dir2/", "XML");
		}
		
		// write docs
		for(String filename2 : filenames2)
		{
			writeDocumentUsingInputStreamHandle(client, filename2, "/dir3/dir4/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
		StructuredQueryDefinition termQuery = qb.term("Memex");
		StructuredQueryDefinition docQuery = qb.or(qb.document("/dir1/dir2/constraint2.xml"), qb.document("/dir3/dir4/constraint4.xml"), qb.document("/dir3/dir4/constraint5.xml"));
		StructuredQueryDefinition andFinalQuery = qb.and(termQuery, docQuery);
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(andFinalQuery, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
	    
		//String expectedSearchReport = "(cts:search(fn:collection(), cts:and-query(cts:or-query((cts:element-value-query(fn:QName(\"\", \"id\"), \"00*2\", (\"lang=en\"), 1), cts:element-value-query(fn:QName(\"\", \"id\"), \"0??6\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\"), 1))[1 to 10]";
		
		//assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testCollectionQuery() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testCollectionQuery");
		
		String filename1 = "constraint1.xml";
		String filename2 = "constraint2.xml";
		String filename3 = "constraint3.xml";
		String filename4 = "constraint4.xml";
		String filename5 = "constraint5.xml";
		String queryOptionName = "valueConstraintWildCardOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
				
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
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
		StructuredQueryDefinition termQuery = qb.term("Memex");
		StructuredQueryDefinition collQuery = qb.or(qb.collection("http://test.com/set1"), qb.collection("http://test.com/set3"));
		StructuredQueryDefinition andFinalQuery = qb.and(termQuery, collQuery);
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(andFinalQuery, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
	    
		//String expectedSearchReport = "(cts:search(fn:collection(), cts:and-query(cts:or-query((cts:element-value-query(fn:QName(\"\", \"id\"), \"00*2\", (\"lang=en\"), 1), cts:element-value-query(fn:QName(\"\", \"id\"), \"0??6\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\"), 1))[1 to 10]";
		
		//assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testContainerConstraintQuery() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testContainerConstraintQuery");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String queryOptionName = "containerConstraintOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/structured-query-container/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
		StructuredQueryDefinition containerQuery = qb.containerConstraint("title-contain", qb.term("Bush"));
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(containerQuery, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		//assertXpathEvaluatesTo("0011", "string(//*[local-name()='result']//*[local-name()='id'])", resultDoc);
		//assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
	    
		//String expectedSearchReport = "(cts:search(fn:collection(), cts:and-query(cts:or-query((cts:element-value-query(fn:QName(\"\", \"id\"), \"00*2\", (\"lang=en\"), 1), cts:element-value-query(fn:QName(\"\", \"id\"), \"0??6\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\"), 1))[1 to 10]";
		
		//assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
		
		// release client
		client.release();		
	}

@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,  restServerName);
	}
}
