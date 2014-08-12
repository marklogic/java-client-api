package com.marklogic.javaclient;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;

import java.io.FileNotFoundException;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar.QueryJoiner.JoinerApply;
import com.marklogic.client.admin.config.QueryOptions.QueryGrammar.Tokenize;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.impl.Utilities;
import com.marklogic.client.io.Format;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.*;
public class TestQueryOptionBuilderGrammar extends BasicJavaClientREST {

	private static String dbName = "TestQueryOptionBuilderGrammarDB";
	private static String [] fNames = {"TestQueryOptionBuilderGrammarDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testGrammarOperatorQuotation() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testGrammarOperatorQuotation");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/gramar-op-quote/", "XML");
		}
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        // build query options
        handle.withConfiguration(builder.configure()
                		.returnMetrics(false)
                		.returnQtext(false))
              .withTransformResults(builder.rawResults())
              .withGrammar(builder.grammar(builder.starters(builder.starterGrouping("(", 30, ")"),
            		                                        builder.starterPrefix("-", 40, "cts:not-query")),	
            		                       builder.joiners(builder.joiner("OR", 20, JoinerApply.INFIX, "cts:or-query", Tokenize.WORD),
            		                                       builder.joiner("AND", 30, JoinerApply.INFIX, "cts:and-query", Tokenize.WORD),
            		                                       builder.joiner(":", 50, JoinerApply.CONSTRAINT)),
            		                       "\"",
            		                       Utilities.domElement("<cts:and-query strength=\"20\" xmlns:cts=\"http://marklogic.com/cts\"/>")));
             
        // write query options
        optionsMgr.writeOptions("GrammarOperatorQuotation", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions("GrammarOperatorQuotation", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("GrammarOperatorQuotation");
	    querydef.setCriteria("1945 OR \"Atlantic Monthly\"");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0113", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
                
		// release client
	    client.release();	
	} 
	

@SuppressWarnings("deprecation")
@Test	public void testGrammarTwoWordsSpace() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testGrammarTwoWordsSpace");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/gramar-two-words-space/", "XML");
		}
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        // build query options
        handle.withConfiguration(builder.configure()
                		.returnMetrics(false)
                		.returnQtext(false))
              .withTransformResults(builder.rawResults())
              .withGrammar(builder.grammar(builder.starters(builder.starterGrouping("(", 30, ")"),
            		                                        builder.starterPrefix("-", 40, "cts:not-query")),	
            		                       builder.joiners(builder.joiner("OR", 20, JoinerApply.INFIX, "cts:or-query", Tokenize.WORD),
            		                                       builder.joiner("AND", 30, JoinerApply.INFIX, "cts:and-query", Tokenize.WORD),
            		                                       builder.joiner(":", 50, JoinerApply.CONSTRAINT)),
            		                       "\"",
            		                       Utilities.domElement("<cts:and-query strength=\"20\" xmlns:cts=\"http://marklogic.com/cts\"/>")));
             
        // write query options
        optionsMgr.writeOptions("GrammarTwoWordsSpace", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions("GrammarTwoWordsSpace", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("GrammarTwoWordsSpace");
	    querydef.setCriteria("\"Atlantic Monthly\" \"Bush\"");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0011", "string(//*[local-name()='result']//*[local-name()='id'])", resultDoc);
                
		// release client
	    client.release();	
	} 


@SuppressWarnings("deprecation")
@Test	public void testGrammarPrecedenceAndNegate() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testGrammarPrecedenceAndNegate");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/gramar-two-words-space/", "XML");
		}
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        // build query options
        handle.withConfiguration(builder.configure()
                		.returnMetrics(false)
                		.returnQtext(false))
              .withTransformResults(builder.rawResults())
              .withGrammar(builder.grammar(builder.starters(builder.starterGrouping("(", 30, ")"),
            		                                        builder.starterPrefix("-", 40, "cts:not-query")),	
            		                       builder.joiners(builder.joiner("OR", 10, JoinerApply.INFIX, "cts:or-query", Tokenize.WORD),
            		                                       builder.joiner("AND", 20, JoinerApply.INFIX, "cts:and-query", Tokenize.WORD),
            		                                       builder.joiner(":", 50, JoinerApply.CONSTRAINT)),
            		                       "\"",
            		                       Utilities.domElement("<cts:and-query strength=\"20\" xmlns:cts=\"http://marklogic.com/cts\"/>")));
             
        // write query options
        optionsMgr.writeOptions("GrammarPrecedenceAndNegate", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions("GrammarPrecedenceAndNegate", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("GrammarPrecedenceAndNegate");
	    querydef.setCriteria("-bush AND -memex");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0024", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0113", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
                
		// release client
	    client.release();	
	}
		

@SuppressWarnings("deprecation")
@Test	public void testGrammarConstraint() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testGrammarConstraint");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/gramar-two-words-space/", "XML");
		}
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        // build query options
        handle.withConfiguration(builder.configure()
                		.returnMetrics(false)
                		.returnQtext(false))
              .withTransformResults(builder.rawResults())
              .withConstraints(builder.constraint("intitle", builder.word(builder.elementTermIndex(new QName("title")))))
              .withGrammar(builder.grammar(builder.starters(builder.starterGrouping("(", 30, ")"),
            		                                        builder.starterPrefix("-", 20, "cts:not-query")),	
            		                       builder.joiners(builder.joiner("OR", 20, JoinerApply.INFIX, "cts:or-query", Tokenize.WORD),
            		                                       builder.joiner("AND", 30, JoinerApply.INFIX, "cts:and-query", Tokenize.WORD),
            		                                       builder.joiner(":", 50, JoinerApply.CONSTRAINT)),
            		                       "\"",
            		                       Utilities.domElement("<cts:and-query strength=\"20\" xmlns:cts=\"http://marklogic.com/cts\"/>")));
             
        // write query options
        optionsMgr.writeOptions("GrammarConstraint", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.JSON);
		optionsMgr.readOptions("GrammarConstraint", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("GrammarConstraint");
	    querydef.setCriteria("intitle:Vannevar AND served");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		resultsHandle.setFormat(Format.XML);
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0024", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
                
		// release client
	    client.release();	
	}

@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		
	}
}
