package com.marklogic.javaclient;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.config.QueryOptions;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.QueryOptionsHandle;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
public class TestAppServicesRangePathIndexConstraint extends BasicJavaClientREST {

	private static String dbName = "AppServicesPathIndexConstraintDB";
	private static String [] fNames = {"AppServicesPathIndexConstraintDB-1"};
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
@SuppressWarnings("deprecation")
@Test
	public void testPathIndex() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testPathIndex");
		
		String[] filenames = {"pathindex1.xml", "pathindex2.xml"};
		String queryOptionName = "pathIndexConstraintOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/path-index-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("pindex:Aries");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/path-index-constraint/pathindex1.xml", "string(//*[local-name()='result'][1]//@*[local-name()='uri'])", resultDoc);
		assertXpathEvaluatesTo("/path-index-constraint/pathindex2.xml", "string(//*[local-name()='result'][2]//@*[local-name()='uri'])", resultDoc);
		
		// release client
		client.release();
		
		// ***********************************************
		// *** Running test path index with constraint ***
		// ***********************************************
		
        System.out.println("Running testPathIndexWithConstraint");

		client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/path-index-constraint/", "XML");
		}
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
				
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
				
		// create query options handle
		QueryOptionsHandle handle = new QueryOptionsHandle();
		
		handle.withConstraints(builder.constraint("lastname", builder.word(builder.elementTermIndex(new QName("ln")))), builder.constraint("pindex", builder.range(builder.pathIndex("/Employee/fn", null, builder.stringRangeType(QueryOptions.DEFAULT_COLLATION)))));
		
		// write query options
        optionsMgr.writeOptions("PathIndexWithConstraint", handle);
		
        // create query manager
     	queryMgr = client.newQueryManager();
     	
     	// create query def
     	querydef = queryMgr.newStringDefinition("PathIndexWithConstraint");
        querydef.setCriteria("pindex:Aries AND lastname:Yuwono");
        
     	StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder("PathIndexWithConstraint");
     	StructuredQueryDefinition queryPathIndex = qb.rangeConstraint("pindex", Operator.EQ, "Aries");
     	StructuredQueryDefinition queryWord = qb.wordConstraint("lastname", "Yuwono");
     	StructuredQueryDefinition queryFinal = qb.and(queryPathIndex, queryWord);
     	
     	// create handle
     	resultsHandle = new DOMHandle();
     	queryMgr.search(queryFinal, resultsHandle);
     	
     	resultDoc = resultsHandle.get();
     	
     	assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/path-index-constraint/pathindex1.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
				
		// release client
		client.release();

		// ***********************************************
		// *** Running test path index on int ***
		// ***********************************************
		
        System.out.println("Running testPathIndexOnInt");
        
        String[] filenames2 = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames2)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/path-index-constraint/", "XML");
		}
		
		// create query options manager
		optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
				
		// create query options builder
		builder = new QueryOptionsBuilder();
				
		// create query options handle
		handle = new QueryOptionsHandle();
		
		handle.withTransformResults(builder.rawResults());
		handle.withConfiguration(builder.configure().debug(true).returnMetrics(false).returnQtext(false));
		handle.withConstraints(builder.constraint("amount", builder.range(builder.pathIndex("//@amt", null, builder.rangeType("xs:decimal")))), builder.constraint("pop", builder.range(builder.pathIndex("/root/popularity", null, builder.rangeType("xs:int")))));
		
		// write query options
        optionsMgr.writeOptions("PathIndexWithConstraint", handle);
		
        // create query manager
     	queryMgr = client.newQueryManager();
   
        // create query builder
     	qb = queryMgr.newStructuredQueryBuilder("PathIndexWithConstraint");
     	StructuredQueryDefinition queryPathIndex1 = qb.rangeConstraint("pop", Operator.EQ, "5");
     	StructuredQueryDefinition queryPathIndex2 = qb.rangeConstraint("amount", Operator.EQ, "0.1");
     	queryFinal = qb.and(queryPathIndex1, queryPathIndex2);
     	
     	// create handle
     	resultsHandle = new DOMHandle();
     	queryMgr.search(queryFinal, resultsHandle);
     	
     	resultDoc = resultsHandle.get();
     	
     	assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/path-index-constraint/constraint1.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
				
		// release client
		client.release();		
	}
	
		
	/*
	@SuppressWarnings("deprecation")
	@Test
	public void testPathIndexWithConstraint() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testPathIndexWithConstraint");
		
		String[] filenames = {"pathindex1.xml", "pathindex2.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/path-index-constraint/", "XML");
		}
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
				
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
				
		// create query options handle
		QueryOptionsHandle handle = new QueryOptionsHandle();
		
		handle.withConstraints(builder.constraint("lastname", builder.word(builder.elementTermIndex(new QName("ln")))), builder.constraint("pindex", builder.range(builder.pathIndex("/Employee/fn", null, builder.stringRangeType(QueryOptions.DEFAULT_COLLATION)))));
		
		// write query options
        optionsMgr.writeOptions("PathIndexWithConstraint", handle);
        System.out.println(optionsMgr.readOptions("PathIndexWithConstraint", new StringHandle()).get());
		
        // create query manager
     	QueryManager queryMgr = client.newQueryManager();
     	
     	// create query def
     	StringQueryDefinition querydef = queryMgr.newStringDefinition("PathIndexWithConstraint");
        querydef.setCriteria("pindex:Aries AND lastname:Yuwono");
        
     	StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder("PathIndexWithConstraint");
     	StructuredQueryDefinition queryPathIndex = qb.rangeConstraint("pindex", Operator.EQ, "Aries");
     	StructuredQueryDefinition queryWord = qb.wordConstraint("lastname", "Yuwono");
     	StructuredQueryDefinition queryFinal = qb.and(queryPathIndex, queryWord);
     	
     	// create handle
     	DOMHandle resultsHandle = new DOMHandle();
     	queryMgr.search(queryFinal, resultsHandle);
     	
     	Document resultDoc = resultsHandle.get();
     	
     	System.out.println(convertXMLDocumentToString(resultDoc));
				
		// release client
		client.release();		
	}
    */
@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,  restServerName);
	}
}
