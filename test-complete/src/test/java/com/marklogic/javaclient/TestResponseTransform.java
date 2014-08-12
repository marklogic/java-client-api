package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;

import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.StringHandle;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
public class TestResponseTransform extends BasicJavaClientREST {

	private static String dbName = "TestResponseTransformDB";
	private static String [] fNames = {"TestResponseTransformDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

@BeforeClass	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testResponseTransform() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testResponseTransform");
		
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
			writeDocumentUsingInputStreamHandle(client, filename, "/response-transform/", "XML");
		}
		
		// set the transform		
		// create a manager for transform extensions
		TransformExtensionsManager transMgr = client.newServerConfigManager().newTransformExtensionsManager();

		// specify metadata about the transform extension
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Search-Response-TO-HTML XSLT Transform");
		metadata.setDescription("This plugin transforms a Search Response document to HTML");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");
		
		// get the transform file
		File transformFile = new File("src/junit/com/marklogic/javaclient/transforms/search2html.xsl");
		
		FileHandle transformHandle = new FileHandle(transformFile);
		
		// write the transform
		transMgr.writeXSLTransform("search2html", transformHandle, metadata);
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOption.xml");
		
		String combinedQuery = convertFileToString(file);
		
		// create a handle for the search criteria
		StringHandle rawHandle = new StringHandle(combinedQuery);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
        RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);
        querydef.setResponseTransform(new ServerTransform("search2html"));
		
		// create result handle
        StringHandle resultsHandle = new StringHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		String resultDoc = resultsHandle.get();
		
		System.out.println(resultDoc);
		
		assertTrue("transform on title is not found", resultDoc.contains("<title>Custom Search Results</title>"));
		assertTrue("transform on header is not found", resultDoc.contains("MyURI"));
		assertTrue("transform on doc return is not found", resultDoc.contains("<td align=\"left\">/response-transform/constraint5.xml</td>"));
	    		
		transMgr.deleteTransform("search2html");
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testResponseTransformInvalid() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testResponseTransformInvalid");
		
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
			writeDocumentUsingInputStreamHandle(client, filename, "/response-transform/", "XML");
		}
		
		// set the transform		
		// create a manager for transform extensions
		TransformExtensionsManager transMgr = client.newServerConfigManager().newTransformExtensionsManager();

		// specify metadata about the transform extension
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Search-Response-TO-HTML XSLT Transform");
		metadata.setDescription("This plugin transforms a Search Response document to HTML");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");
		
		// get the transform file
		File transformFile = new File("src/junit/com/marklogic/javaclient/transforms/search2html.xsl");
		
		FileHandle transformHandle = new FileHandle(transformFile);
		
		// write the transform
		transMgr.writeXSLTransform("search2html", transformHandle, metadata);
		
		// get the combined query
        File file = new File("src/junit/com/marklogic/javaclient/combined/combinedQueryOption.xml");
		
		String combinedQuery = convertFileToString(file);
		
		// create a handle for the search criteria
		StringHandle rawHandle = new StringHandle(combinedQuery);
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create a search definition based on the handle
        RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(rawHandle);
        querydef.setResponseTransform(new ServerTransform("foo"));
		
		// create result handle
        StringHandle resultsHandle = new StringHandle();
        
        String exception = "";
        
        try
        {
        	queryMgr.search(querydef, resultsHandle);
        } catch(Exception e)
        {
        	exception = e.toString();
        	System.out.println(exception);
        }
        
        String expectedException = "Local message: search failed: Bad Request. Server Message: XDMP-MODNOTFOUND: (err:XQST0059) Module /marklogic.rest.transform/foo/assets/transform.xqy not found";
        assertTrue("exception is not thrown", exception.contains(expectedException));
	    //bug 22356
        assertTrue("Value should be null", resultsHandle.get()==null);
        
		transMgr.deleteTransform("search2html");
		
		// release client
		client.release();		
	}
	
@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames,restServerName);
	}
}
