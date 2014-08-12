package com.marklogic.javaclient;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.marklogic.client.query.AggregateResult;
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.ValuesDefinition;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.ValuesHandle;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
public class TestBug18724 extends BasicJavaClientREST {

	private static String dbName = "Bug18724DB";
	private static String [] fNames = {"Bug18724DB-1"};
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
	public void testDefaultStringSearch() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testDefaultStringSearch");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// create transaction
		Transaction transaction1 = client.openTransaction();
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/def-string-search/", transaction1, "XML");
		}
		
		// commit transaction
		transaction1.commit();
		
		// create transaction
		Transaction transaction2 = client.openTransaction();
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		querydef.setCriteria("0012");
				
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle, transaction2);
		
		// commit transaction
		transaction2.commit();
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result']//*[local-name()='highlight'])", resultDoc);
		
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	
	public void testDefaultKeyValueSearch() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testDefaultKeyValueSearch");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// create transaction
		Transaction transaction1 = client.openTransaction();
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/def-keyvalue-search/", transaction1, "XML");
		}
		
		// commit transaction
		transaction1.commit();
		
		// create transaction
		Transaction transaction2 = client.openTransaction();
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition
		KeyValueQueryDefinition querydef = queryMgr.newKeyValueDefinition();
		querydef.put(queryMgr.newElementLocator(new QName("id")), "0012");
				
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle, transaction2);
		
		// commit transaction
		transaction2.commit();
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result']//*[local-name()='highlight'])", resultDoc);
		
		// release client
		client.release();		
	}
	
	/*public void testDefaultStructuredQueryBuilderSearch() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testDefaultStructuredQueryBuilderSearch");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/def-query-builder-search/", "XML");
		}
		
		// create transaction
		Transaction transaction2 = client.openTransaction();
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition termQuery = qb.term("0012");
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(termQuery, resultsHandle, transaction2);
		
		// commit transaction
		transaction2.commit();
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result']//*[local-name()='highlight'])", resultDoc);
		
		// release client
		client.release();		
	}*/
	
	/*public void testDefaultValuesSearch() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testDefaultValuesSearch");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// create transaction
		Transaction transaction1 = client.openTransaction();
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/def-values-search/", transaction1, "XML");
		}
		
		// commit transaction
		transaction1.commit();
		
		// create transaction
		Transaction transaction2 = client.openTransaction();
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		ValuesDefinition queryDef = queryMgr.newValuesDefinition("popularity");
		queryDef.setAggregate("sum");
		//queryDef.setName("popularity");
		
		// create handle
		ValuesHandle valuesHandle = new ValuesHandle();
		queryMgr.values(queryDef, valuesHandle, transaction2);
		
		// commit transaction
		transaction2.commit();
		
		// get the result
		//Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
        AggregateResult[] agg = valuesHandle.getAggregates();
        System.out.println(agg.length);
        int first  = agg[0].get("xs:int", Integer.class);
        System.out.println(first);
        
		//assertXpathEvaluatesTo("1", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
		//assertXpathEvaluatesTo("0012", "string(//*[local-name()='result']//*[local-name()='highlight'])", resultDoc);
		
		// release client
		client.release();		
	}*/
	@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		
	}
}
