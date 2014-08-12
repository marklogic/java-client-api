package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.xml.sax.SAXException;



import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.XMLStreamReaderHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import org.junit.*;
public class TestLinkResultDocuments extends BasicJavaClientREST {

	private static String dbName = "TestLinkResultDocuments";
	private static String [] fNames = {"TestLinkResultDocuments-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	//  assocXDBCServer(serverName, dbName);
    //  assocRESTServer(restServerName, dbName);
	  setupAppServicesConstraint(dbName);
	}

@SuppressWarnings("deprecation")
@Test
	public void testMimeType() throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running TestLinkResultDocuments");
		
		String[] filenames = {"constraint4.xml", "binary.jpg", "constraint4.json"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			if (filename.contains("xml")){
				writeDocumentUsingInputStreamHandle(client, filename, "/mime-type/", "XML");
			}
			else if (filename.contains("json")){
				writeDocumentUsingInputStreamHandle(client, filename, "/mime-type/", "JSON");
			}
			else if (filename.contains("jpg")){ 
				writeDocumentUsingInputStreamHandle(client, filename, "/mime-type/", "Binary");
			}
		}
		
		// set query option
		setQueryOption(client,"LinkResultDocumentsOpt.xml");
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("LinkResultDocumentsOpt.xml");
		querydef.setCriteria("5");
	//	QueryOptionsHandle as = new QueryOptionsHandle();
		
		// create result handle
		SearchHandle resultsHandle = queryMgr.search(querydef, new SearchHandle()); 
		
		// get the result
		for (MatchDocumentSummary result : resultsHandle.getMatchResults()) 
		{
			System.out.println(result.getMimeType()+ ": Mime Type");
			System.out.println(result.getPath()+ ": Path");
			System.out.println(result.getFormat()+ ": Format");
			System.out.println(result.getUri()+ ": Uri");
			assertTrue("Uri is Wrong", result.getPath().contains("/mime-type/constraint4.json")||result.getPath().contains("/mime-type/constraint4.xml"));
		} 
		
		
		XMLStreamReaderHandle shandle = queryMgr.search(querydef, new XMLStreamReaderHandle());
		String resultDoc2 = shandle.toString();
		System.out.println("Statics : \n"+resultDoc2);
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testResultDecorator() throws IOException {
			
		System.out.println("Running testResultDecorator");
		
		String[] filenames = {"constraint4.xml", "binary.jpg", "constraint3.json"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			if (filename.contains("xml")){
				writeDocumentUsingInputStreamHandle(client, filename, "/mime-type/", "XML");
			}
			else if (filename.contains("json")){
				writeDocumentUsingInputStreamHandle(client, filename, "/mime-type/", "JSON");
			}
			else if (filename.contains("jpg")){ 
				writeDocumentUsingInputStreamHandle(client, filename, "/mime-type/", "Binary");
			}
		}
		try{
		String OS = System.getProperty("os.name");
		System.out.println("OS name : "+ OS);
		File source = null;
		File target = null;
		if (OS.contains("Windows 7")){
			 source = new File("C:/builds/winnt/HEAD/xcc/api_tests/src/test/java/com/marklogic/javaclient/data/result-decorator-test.xqy");
		     target = new File("C:/Program Files/MarkLogic/Modules/MarkLogic/appservices/search/result-decorator-test.xqy");
		}
		else if (OS.contains("Mac OS X")) {
			source = new File("/space/builder/builds/macosx-64/HEAD/xcc/api_tests/src/test/java/com/marklogic/javaclient/data/result-decorator-test.xqy");
            target = new File("/Users/buildermac/Library/MarkLogic/Modules/MarkLogic/appservices/search/result-decorator-test.xqy");
		}
		else if (OS.contains("Linux")) {
			source = new File("/space/builder/builds/macosx/HEAD/xcc/api_tests/src/test/java/com/marklogic/javaclient/data/result-decorator-test.xqy");
            target = new File("/opt/MarkLogic/Modules/MarkLogic/appservices/search/result-decorator-test.xqy");
		}
		
		System.out.println(source.exists());
	    System.out.println(target.exists());
	    if (target.exists()){
	    	target.delete();
	    }
	    copyWithChannels(source, target, true);
		// set query option
		setQueryOption(client,"LinkResultDocumentsOptDecorator.xml");
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("LinkResultDocumentsOptDecorator.xml");
		querydef.setCriteria("5");
		
		// create result handle
		SearchHandle resultsHandle = queryMgr.search(querydef, new SearchHandle()); 
		
		// get the result
		for (MatchDocumentSummary result : resultsHandle.getMatchResults()) 
		{
			System.out.println(result.getMimeType()+ ": Mime Type");
			System.out.println(result.getPath()+ ": Path");
			System.out.println(result.getFormat()+ ": Format");
			System.out.println(result.getUri()+ ": Uri");
		}
		XMLStreamReaderHandle shandle = queryMgr.search(querydef, new XMLStreamReaderHandle());
		String resultDoc2 = shandle.toString();
		System.out.println("Statics : \n"+resultDoc2);
		//libsMgr.delete(Path);

		}
		catch(Exception e){
			e.printStackTrace();
		}
		// release client
		
		client.release();	
		
	}

@SuppressWarnings("deprecation")
@Test	public void testResultDecoratorNoMimeType() throws IOException {
		
		System.out.println("Running testResultDecoratorNoMimeType");
		
		String[] filenames = {"constraint4.xml", "binary.jpg", "constraint4.json"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			if (filename.contains("xml")){
				writeDocumentUsingInputStreamHandle(client, filename, "/mime-type/", "XML");
			}
			else if (filename.contains("json")){
				writeDocumentUsingInputStreamHandle(client, filename, "/mime-type/", "JSON");
			}
			else if (filename.contains("jpg")){ 
				writeDocumentUsingInputStreamHandle(client, filename, "/mime-type/", "Binary");
			}
		}
		try{	
		String OS = System.getProperty("os.name");
		System.out.println("OS name : "+ OS);
		File source = null;
		File target = null;
		if (OS.contains("Windows 7")){
			 source = new File("C:/builds/winnt/HEAD/xcc/api_tests/src/test/java/com/marklogic/javaclient/data/result-decorator-test.xqy");
		     target = new File("C:/Program Files/MarkLogic/Modules/MarkLogic/appservices/search/result-decorator-test.xqy");
		}
		else if (OS.contains("Mac OS X")) {
			source = new File("/space/builder/builds/macosx-64/HEAD/xcc/api_tests/src/test/java/com/marklogic/javaclient/data/result-decorator-test.xqy");
            target = new File("/Users/buildermac/Library/MarkLogic/Modules/MarkLogic/appservices/search/result-decorator-test.xqy");
		}
		else if (OS.contains("Linux")) {
			source = new File("/space/builder/builds/macosx/HEAD/xcc/api_tests/src/test/java/com/marklogic/javaclient/data/result-decorator-test.xqy");
            target = new File("/opt/MarkLogic/Modules/MarkLogic/appservices/search/result-decorator-test.xqy");
		}
		
		System.out.println(source.exists());
	    System.out.println(target.exists());
	    if (target.exists()){
	    	target.delete();
	    }
	    copyWithChannels(source, target, true);
		// set query option
		setQueryOption(client,"LinkResultDocumentsOptDecorator1.xml");
		
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("LinkResultDocumentsOptDecorator1.xml");
		querydef.setCriteria("5");
		
		// create result handle
		SearchHandle resultsHandle = queryMgr.search(querydef, new SearchHandle()); 
		
		// get the result
		for (MatchDocumentSummary result : resultsHandle.getMatchResults()) 
		{
			System.out.println(result.getMimeType()+ ": Mime Type");
			System.out.println(result.getPath()+ ": Path");
			System.out.println(result.getFormat()+ ": Format");
			System.out.println(result.getUri()+ ": Uri");
		}
		XMLStreamReaderHandle shandle = queryMgr.search(querydef, new XMLStreamReaderHandle());
		String resultDoc2 = shandle.toString();
		System.out.println("Statics : \n"+resultDoc2);
		//libsMgr.delete(Path);

		}
		catch(Exception e){
			e.printStackTrace();
		}
		// release client
		
		client.release();	
		
	}
@AfterClass
	public static void tearDown() throws Exception
	{
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}
}