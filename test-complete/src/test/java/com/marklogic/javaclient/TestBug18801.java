package com.marklogic.javaclient;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import com.marklogic.client.query.StructuredQueryDefinition;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
public class TestBug18801 extends BasicJavaClientREST {

	private static String dbName = "Bug18801DB";
	private static String [] fNames = {"Bug18801DB-1"};
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
	public void testDefaultFacetValue() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testDefaultFacetValue");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
				
		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/def-facet/", "XML");
		}

		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        // build query options
        handle.withConstraints(builder.constraint("pop",
				builder.range(builder.elementRangeIndex(new QName("popularity"),
						builder.rangeType("xs:int")))));
        
        // write query options
        optionsMgr.writeOptions("FacetValueOpt", handle);
        
        // read query option
     	StringHandle readHandle = new StringHandle();
     	readHandle.setFormat(Format.XML);
     	optionsMgr.readOptions("FacetValueOpt", readHandle);
     	String output = readHandle.get();
     	System.out.println(output);

     	// create query manager
     	QueryManager queryMgr = client.newQueryManager();
     				
     	// create query def
     	StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder("FacetValueOpt");
     	StructuredQueryDefinition queryFinal = qb.rangeConstraint("pop", Operator.EQ, "5");
     		
     	// create handle
     	DOMHandle resultsHandle = new DOMHandle();
     	queryMgr.search(queryFinal, resultsHandle);
     		
     	// get the result
     	Document resultDoc = resultsHandle.get();
     	//System.out.println(convertXMLDocumentToString(resultDoc)); 
     	
     	assertXpathEvaluatesTo("pop", "string(//*[local-name()='response']//*[local-name()='facet']//@*[local-name()='name'])", resultDoc);
     	assertXpathEvaluatesTo("3", "string(//*[local-name()='response']//*[local-name()='facet']/*[local-name()='facet-value']//@*[local-name()='count'])", resultDoc);
     	
		// release client
		client.release();		
	}
	@AfterClass	
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,restServerName);
		
	}
}
