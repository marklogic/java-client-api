package com.marklogic.javaclient;

import java.io.IOException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.marklogic.client.query.QueryManager;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.util.EditableNamespaceContext;
import com.marklogic.client.io.DOMHandle;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.*;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
public class TestStructuredSearchGeo extends BasicJavaClientREST {

	private static String dbName = "TestStructuredSearchGeoDB";
	private static String [] fNames = {"TestStructuredSearchGeoDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

@BeforeClass public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesGeoConstraint(dbName);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testTestStructuredSearchGeo() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testTestStructuredSearchGeo");
		
		String queryOptionName = "geoConstraintOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		for(int i = 1; i <= 7; i++)
		{
			writeDocumentUsingInputStreamHandle(client, "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
		}
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
		StructuredQueryDefinition geoElementConstraintQuery = qb.geospatialConstraint("geo-elem", qb.point(12, 5));
		StructuredQueryDefinition termQuery = qb.term("bill_kara");
		StructuredQueryDefinition finalOrQuery = qb.or(geoElementConstraintQuery, termQuery);
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(finalOrQuery, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		//assertXpathEvaluatesTo("karl_kara 12,5 12,5 12 5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testTestStructuredSearchGeoBox() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testTestStructuredSearchGeoBox");
		
		String queryOptionName = "geoConstraintOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		loadGeoData();
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
		
		StructuredQueryDefinition geoElementConstraintQuery = qb.geospatialConstraint("geo-elem-child", qb.box(-12,-5,-11,-4));
		StructuredQueryDefinition termQuery = qb.term("karl_kara");
		StructuredQueryDefinition finalAndQuery = qb.and(geoElementConstraintQuery, termQuery);
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(finalAndQuery, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println("Output : " + convertXMLDocumentToString(resultDoc));
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("/geo-constraint/geo-constraint2.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
		
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testTestStructuredSearchGeoBoxAndPath() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testTestStructuredSearchGeoBoxAndPath" + "This test is for Bug : 22071 & 22136");
		
		String queryOptionName = "geoConstraintOpt.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// write docs
		loadGeoData();
		
		setQueryOption(client, queryOptionName);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
		
		StructuredQueryDefinition geoQuery = qb.geospatial(qb.geoPath(qb.pathIndex("/doc/g-elem-point")), qb.box(-12,-5,-11,-4));
		Collection<String> nameSpaceCollection = qb.getNamespaces().getAllPrefixes();
		assertEquals("getNamespace failed ",false, nameSpaceCollection.isEmpty());
		for(String prefix : nameSpaceCollection){
			System.out.println("Prefixes : "+prefix);
			System.out.println(qb.getNamespaces().getNamespaceURI(prefix));
			if (qb.getNamespaces().getNamespaceURI(prefix).contains("http://www.w3.org/2001/XMLSchema"))
			{
				EditableNamespaceContext namespaces = new EditableNamespaceContext();
				namespaces.put("new", "http://www.marklogic.com");
				qb.setNamespaces(namespaces);
				System.out.println(qb.getNamespaces().getNamespaceURI("new"));
			}
		}
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(geoQuery, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println("Output : " + convertXMLDocumentToString(resultDoc));
		assertXpathEvaluatesTo("4", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		
		// release client
		client.release();		
	}
	
@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}
}
