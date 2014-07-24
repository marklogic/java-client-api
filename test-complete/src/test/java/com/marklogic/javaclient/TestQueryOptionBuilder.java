package com.marklogic.javaclient;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.config.QueryOptions.Facets;
import com.marklogic.client.admin.config.QueryOptions.FragmentScope;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.*;
public class TestQueryOptionBuilder extends BasicJavaClientREST {

	private static String dbName = "TestQueryOptionBuilderDB";
	private static String [] fNames = {"TestQueryOptionBuilderDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testValueConstraintWildcard() throws FileNotFoundException, XpathException
	{	
		System.out.println("Running testValueConstraintWildcard");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/value-constraint-query-builder/", "XML");
		}
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        // build query options
        handle.withConstraints( builder.constraint("id", 
                builder.value(builder.elementTermIndex(new QName("id")))))
                .withConfiguration(builder.configure()
                		.returnMetrics(false)
                		.returnQtext(false)
                		.debug(true))
                .withTransformResults(builder.rawResults());
               
        // write query options
        optionsMgr.writeOptions("ValueConstraintWildcard", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.XML);
		optionsMgr.readOptions("ValueConstraintWildcard", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("ValueConstraintWildcard");
		querydef.setCriteria("id:00*2 OR id:0??6");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);
	    
		String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:element-value-query(fn:QName(\"\", \"id\"), \"00*2\", (\"lang=en\"), 1), cts:element-value-query(fn:QName(\"\", \"id\"), \"0??6\", (\"lang=en\"), 1))), (\"score-logtfidf\"), 1))[1 to 10]";
		
		assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
                
		// release client
	    client.release();	
	}


@SuppressWarnings("deprecation")
@Test	public void testWordConstraintNormalWordQuery() throws FileNotFoundException, XpathException
	{	
		System.out.println("Running testWordConstraintNormalWordQuery");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/word-constraint-query-builder/", "XML");
		}
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        // build query options
        handle.withConstraints(builder.constraint("intitle", 
                        			builder.word(builder.elementTermIndex(new QName("title")))),
                        		builder.constraint("inprice",
                        			builder.word(builder.elementAttributeTermIndex(
                        					new QName("http://cloudbank.com", "price"),
                        					new QName("amt")))))
                .withConfiguration(builder.configure()
                		.returnMetrics(false)
                		.returnQtext(false)
                		.debug(true))
                .withTransformResults(builder.rawResults());
               
		
        // write query options
        optionsMgr.writeOptions("WordConstraintNormalWordQuery", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.XML);
		optionsMgr.readOptions("WordConstraintNormalWordQuery", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("WordConstraintNormalWordQuery");
		querydef.setCriteria("Memex  OR inprice:.12");

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("The Bush article", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("The memex", "string(//*[local-name()='result'][2]//*[local-name()='title'])", resultDoc);
		assertXpathEvaluatesTo("0.12", "string(//*[local-name()='result'][1]//@*[local-name()='amt'])", resultDoc);
		assertXpathEvaluatesTo("123.45", "string(//*[local-name()='result'][2]//@*[local-name()='amt'])", resultDoc);
	    
		String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:word-query(\"Memex\", (\"lang=en\"), 1), cts:element-attribute-word-query(fn:QName(\"http://cloudbank.com\", \"price\"), fn:QName(\"\", \"amt\"), \".12\", (\"lang=en\"), 1))), (\"score-logtfidf\"), 1))[1 to 10]";
		
		assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
                
		// release client
	    client.release();	
	}
	

@SuppressWarnings("deprecation")
@Test	public void testAllConstraintsWithStringSearch() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testAllConstraintsWithStringSearch");
		
		String filename1 = "constraint1.xml";
		String filename2 = "constraint2.xml";
		String filename3 = "constraint3.xml";
		String filename4 = "constraint4.xml";
		String filename5 = "constraint5.xml";
				
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
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        // build query options		
	
        handle.
                withConstraints(
                		builder.constraint("id", 
                				builder.value(builder.elementTermIndex(new QName("id")))),
                		builder.constraint("date",
                				builder.range(
                						builder.elementRangeIndex(new QName("http://purl.org/dc/elements/1.1/", "date"), 
                								builder.rangeType("xs:date")))),                                
                		builder.constraint("coll", 
                				builder.collection("http://test.com/", Facets.FACETED)),
                		builder.constraint("para",
                				builder.word(builder.fieldTermIndex("para"), null, null, "case-insensitive")),
                		builder.constraint("intitle",
                				builder.word(builder.elementTermIndex(new QName("title")))),
                		builder.constraint("price",
                				builder.range(builder.elementAttributeRangeIndex(new QName("http://cloudbank.com", "price"),
                						new QName("amt"), builder.rangeType("xs:decimal")),
                						Facets.UNFACETED,
                						null,
                						builder.buckets(
                								builder.bucket("high", "High", "120", null),
                								builder.bucket("medium", "Medium", "3", "14"),
                								builder.bucket("low", "Low", "0", "2")))),
                		builder.constraint("pop",
                				builder.range(builder.elementRangeIndex(new QName("popularity"),
                						builder.rangeType("xs:int")),
                						Facets.FACETED,
                						null,
                						builder.buckets(builder.bucket("high", "High", "5", null),
                                      builder.bucket("medium", "Medium", "3", "5"),
                                      builder.bucket("low", "Low", "1", "3")))))
                 .withConfiguration(builder.configure()
                		 .returnMetrics(false)
                		 .returnQtext(false)
                		 .debug(true))
                 .withTransformResults(builder.rawResults());
                		 
                		
        // write query options
        optionsMgr.writeOptions("AllConstraintsWithStringSearch", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.XML);
		optionsMgr.readOptions("AllConstraintsWithStringSearch", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
		
		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("AllConstraintsWithStringSearch");
		querydef.setCriteria("(coll:set1 AND coll:set5) AND -intitle:memex AND (pop:high OR pop:medium) AND price:low AND id:**11 AND date:2005-01-01 AND (para:Bush AND -para:memex)");
				
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(querydef, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("Vannevar Bush", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
                        
		// release client
	    client.release();	
	}


@SuppressWarnings("deprecation")
@Test	public void testAllConstraintsWithStructuredSearch() throws FileNotFoundException, XpathException, TransformerException
	{	
		System.out.println("Running testAllConstraintsWithStructuredSearch");
		
		String filename1 = "constraint1.xml";
		String filename2 = "constraint2.xml";
		String filename3 = "constraint3.xml";
		String filename4 = "constraint4.xml";
		String filename5 = "constraint5.xml";
				
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
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        // build query options		
	
        handle.
        withConstraints(
        		builder.constraint("id", 
        				builder.value(builder.elementTermIndex(new QName("id")))),
        		builder.constraint("date",
        				builder.range(
        						builder.elementRangeIndex(new QName("http://purl.org/dc/elements/1.1/", "date"), 
        								builder.rangeType("xs:date")))),                                
        		builder.constraint("coll", 
        				builder.collection("http://test.com/", Facets.FACETED)),
        		builder.constraint("para",
        				builder.word(builder.fieldTermIndex("para"), null, null, "case-insensitive")),
        		builder.constraint("intitle",
        				builder.word(builder.elementTermIndex(new QName("title")))),
        		builder.constraint("price",
        				builder.range(builder.elementAttributeRangeIndex(new QName("http://cloudbank.com", "price"),
        						new QName("amt"), builder.rangeType("xs:decimal")),
        						Facets.UNFACETED,
        						null,
        						builder.buckets(
        								builder.bucket("high", "High", "120", null),
        								builder.bucket("medium", "Medium", "3", "14"),
        								builder.bucket("low", "Low", "0", "2")))),
        		builder.constraint("pop",
        				builder.range(builder.elementRangeIndex(new QName("popularity"),
        						builder.rangeType("xs:int")),
        						Facets.FACETED,
        						null,
        						builder.buckets(builder.bucket("high", "High", "5", null),
                              builder.bucket("medium", "Medium", "3", "5"),
                              builder.bucket("low", "Low", "1", "3")))))
         .withConfiguration(builder.configure()
        		 .returnMetrics(false)
        		 .returnQtext(false)
        		 .debug(true))
         .withTransformResults(builder.rawResults());
       
        
        // write query options
        optionsMgr.writeOptions("AllConstraintsWithStructuredSearch", handle);
        
        // read query option
		StringHandle readHandle = new StringHandle();
		readHandle.setFormat(Format.XML);
		optionsMgr.readOptions("AllConstraintsWithStructuredSearch", readHandle);
	    String output = readHandle.get();
	    System.out.println(output);
	    
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
				
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder("AllConstraintsWithStructuredSearch");
		StructuredQueryDefinition query1 = qb.and(qb.collectionConstraint("coll", "set1"), qb.collectionConstraint("coll", "set5"));
		StructuredQueryDefinition query2 = qb.not(qb.wordConstraint("intitle", "memex"));
		StructuredQueryDefinition query3 = qb.valueConstraint("id", "**11");
		StructuredQueryDefinition query4 = qb.rangeConstraint("date", StructuredQueryBuilder.Operator.EQ, "2005-01-01");
		StructuredQueryDefinition query5 = qb.and(qb.wordConstraint("para", "Bush"), qb.not(qb.wordConstraint("para", "memex")));
		StructuredQueryDefinition query6 = qb.rangeConstraint("price", StructuredQueryBuilder.Operator.EQ, "low");
		StructuredQueryDefinition query7 = qb.or(qb.rangeConstraint("pop", StructuredQueryBuilder.Operator.EQ, "high"), qb.rangeConstraint("pop", StructuredQueryBuilder.Operator.EQ, "medium"));
		StructuredQueryDefinition queryFinal = qb.and(query1, query2, query3, query4, query5, query6, query7);
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(queryFinal, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("Vannevar Bush", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
                        
		// release client
	    client.release();	
	}


@SuppressWarnings("deprecation")
@Test	public void testExtractMetadataWithStructuredSearch() throws XpathException, TransformerException, ParserConfigurationException, SAXException, IOException
	{	
		System.out.println("testExtractMetadataWithStructuredSearch");
		
		String filename = "xml-original.xml";
		String uri = "/extract-metadata/";
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		ServerConfigurationManager scMgr = client.newServerConfigManager();
		scMgr.setServerRequestLogging(true);
		scMgr.writeConfiguration();
		
		// get the original metadata
		Document docMetadata = getXMLMetadata("metadata-original.xml");
		
		// create doc manager
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		
	    // write the doc
	    writeDocumentUsingInputStreamHandle(client, filename, uri, "XML");

		// create handle to write metadata
		DOMHandle writeMetadataHandle = new DOMHandle();
		writeMetadataHandle.set(docMetadata);
		
		// create doc id
		String docId = uri + filename;
	    	    
	    // write metadata
	    docMgr.writeMetadata(docId, writeMetadataHandle);
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        // build query options
        handle.withConfiguration(builder.configure().fragmentScope(FragmentScope.PROPERTIES));
        handle.withExtractMetadata(builder.extractMetadata(builder.elementValue(new QName("", "Author")), builder.elementValue(new QName("", "AppName"))));
        //handle.withExtractMetadata(builder.extractMetadata(builder.elementValue(new QName("", "Author")), builder.constraintValue("appname")));
        handle.withConstraints(builder.constraint("appname", builder.word(builder.elementTermIndex(new QName("AppName")))));
        
        // write query options
        optionsMgr.writeOptions("ExtractMetadataWithStructuredSearch", handle);
        
        // read query option
     	StringHandle readHandle = new StringHandle();
     	readHandle.setFormat(Format.XML);
     	optionsMgr.readOptions("ExtractMetadataWithStructuredSearch", readHandle);
     	String output = readHandle.get();
     	System.out.println(output);
        
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
				
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder("ExtractMetadataWithStructuredSearch");
		StructuredQueryDefinition queryTerm1 = qb.term("MarkLogic");
		StructuredQueryDefinition queryTerm2 = qb.term("Microsoft");
		//StructuredQueryDefinition queryWord = qb.wordConstraint("appname", "Microsoft");
		StructuredQueryDefinition queryFinal = qb.and(queryTerm1, queryTerm2);
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(queryFinal, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		
		assertXpathEvaluatesTo("MarkLogic", "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='Author'])", resultDoc);
		assertXpathEvaluatesTo("Microsoft Office Word", "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='AppName'])", resultDoc);
                        
		// release client
	    client.release();	
	}
	
	// See bug 18361
	/*public void testExtractMetadataWithStructuredSearchAndConstraint() throws XpathException, TransformerException, ParserConfigurationException, SAXException, IOException
	{	
		System.out.println("testExtractMetadataWithStructuredSearchAndConstraint");
		
		String filename = "xml-original.xml";
		String uri = "/extract-metadata/";
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		ServerConfigurationManager scMgr = client.newServerConfigManager();
		scMgr.setServerRequestLogging(true);
		scMgr.writeConfiguration();
		
		// get the original metadata
		Document docMetadata = getXMLMetadata("metadata-original.xml");
		
		// create doc manager
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		
	    // write the doc
	    writeDocumentUsingInputStreamHandle(client, filename, uri, "XML");

		// create handle to write metadata
		DOMHandle writeMetadataHandle = new DOMHandle();
		writeMetadataHandle.set(docMetadata);
		
		// create doc id
		String docId = uri + filename;
	    	    
	    // write metadata
	    docMgr.writeMetadata(docId, writeMetadataHandle);
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        // build query options
        handle.withConfiguration(builder.configure().fragmentScope(FragmentScope.PROPERTIES));
        handle.withExtractMetadata(builder.extractMetadata(builder.elementValue(new QName("", "Author")), builder.constraintValue("appname")));
        handle.withConstraints(builder.constraint("appname", builder.word(builder.elementTermIndex(new QName("AppName")))));
        
        // write query options
        optionsMgr.writeOptions("ExtractMetadataWithStructuredSearchAndConstraint", handle);
        
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
				
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder("ExtractMetadataWithStructuredSearchAndConstraint");
		StructuredQueryDefinition queryTerm = qb.term("MarkLogic");
		StructuredQueryDefinition queryWord = qb.wordConstraint("appname", "Microsoft");
		StructuredQueryDefinition queryFinal = qb.and(queryTerm, queryWord);
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(queryFinal, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));
		
		//assertXpathEvaluatesTo("MarkLogic", "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='Author'])", resultDoc);
		//assertXpathEvaluatesTo("Microsoft Office Word", "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='AppName'])", resultDoc);
                        
		// release client
	    client.release();	
	}*/


@SuppressWarnings("deprecation")
@Test	public void testExtractMetadataWithStructuredSearchAndRangeConstraint() throws XpathException, TransformerException, ParserConfigurationException, SAXException, IOException
	{	
		System.out.println("testExtractMetadataWithStructuredSearchAndRangeConstraint");
		
		String filename = "xml-original.xml";
		String uri = "/extract-metadata/";
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		ServerConfigurationManager scMgr = client.newServerConfigManager();
		scMgr.setServerRequestLogging(true);
		scMgr.writeConfiguration();
		
		// get the original metadata
		Document docMetadata = getXMLMetadata("metadata-original.xml");
		
		// create doc manager
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		
	    // write the doc
	    writeDocumentUsingInputStreamHandle(client, filename, uri, "XML");

		// create handle to write metadata
		DOMHandle writeMetadataHandle = new DOMHandle();
		writeMetadataHandle.set(docMetadata);
		
		// create doc id
		String docId = uri + filename;
	    	    
	    // write metadata
	    docMgr.writeMetadata(docId, writeMetadataHandle);
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        // build query options
        handle.withConfiguration(builder.configure().fragmentScope(FragmentScope.PROPERTIES));
        handle.withExtractMetadata(builder.extractMetadata(builder.elementValue(new QName("", "Author")), builder.constraintValue("pop")));
        //handle.withConstraints(builder.constraint("appname", builder.word(builder.elementTermIndex(new QName("AppName")))));
        handle.withConstraints(builder.constraint("pop",
				builder.range(builder.elementRangeIndex(new QName("popularity"),
						builder.rangeType("xs:int")),
						Facets.FACETED,
						null,
						builder.buckets(builder.bucket("high", "High", "5", null),
                      builder.bucket("medium", "Medium", "3", "5"),
                      builder.bucket("low", "Low", "1", "3")))));
        
        // write query options
        optionsMgr.writeOptions("ExtractMetadataWithStructuredSearchAndRangeConstraint", handle);
        
        // read query option
     	StringHandle readHandle = new StringHandle();
     	readHandle.setFormat(Format.XML);
     	optionsMgr.readOptions("ExtractMetadataWithStructuredSearchAndRangeConstraint", readHandle);
     	String output = readHandle.get();
     	System.out.println(output);
        
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
				
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder("ExtractMetadataWithStructuredSearchAndRangeConstraint");
		StructuredQueryDefinition queryFinal = qb.rangeConstraint("pop", StructuredQueryBuilder.Operator.EQ, "high");
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(queryFinal, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("MarkLogic", "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='Author'])", resultDoc);
		assertXpathEvaluatesTo("5", "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='constraint-meta'])", resultDoc);
		//assertXpathEvaluatesTo("Microsoft Office Word", "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='AppName'])", resultDoc);
                        
		// release client
	    client.release();	
	}
	

@SuppressWarnings("deprecation")
@Test	public void testDocumentLevelMetadata() throws XpathException, TransformerException, ParserConfigurationException, SAXException, IOException
	{	
		System.out.println("testDocumentLevelMetadata");
		
		String filename = "xml-original.xml";
		String uri = "/extract-metadata/";
				
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		ServerConfigurationManager scMgr = client.newServerConfigManager();
		scMgr.setServerRequestLogging(true);
		scMgr.writeConfiguration();
		
		// get the original metadata
		Document docMetadata = getXMLMetadata("metadata-original.xml");
		
		// create doc manager
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		
	    // write the doc
	    writeDocumentUsingInputStreamHandle(client, filename, uri, "XML");

		// create handle to write metadata
		DOMHandle writeMetadataHandle = new DOMHandle();
		writeMetadataHandle.set(docMetadata);
		
		// create doc id
		String docId = uri + filename;
	    	    
	    // write metadata
	    docMgr.writeMetadata(docId, writeMetadataHandle);
		
		// create query options manager
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		
		// create query options builder
		QueryOptionsBuilder builder = new QueryOptionsBuilder();
		
		// create query options handle
        QueryOptionsHandle handle = new QueryOptionsHandle();
        
        // build query options
        handle.withConfiguration(builder.configure().fragmentScope(FragmentScope.DOCUMENTS));
        handle.withExtractMetadata(builder.extractMetadata(builder.elementValue(new QName("", "Author")), builder.constraintValue("pop"), builder.elementValue(new QName("", "name"))));
        //handle.withConstraints(builder.constraint("appname", builder.word(builder.elementTermIndex(new QName("AppName")))));
        handle.withConstraints(builder.constraint("pop",
				builder.range(builder.elementRangeIndex(new QName("popularity"),
						builder.rangeType("xs:int")),
						Facets.FACETED,
						null,
						builder.buckets(builder.bucket("high", "High", "5", null),
                      builder.bucket("medium", "Medium", "3", "5"),
                      builder.bucket("low", "Low", "1", "3")))));
        
        // write query options
        optionsMgr.writeOptions("DocumentLevelMetadata", handle);
        
        // read query option
     	StringHandle readHandle = new StringHandle();
     	readHandle.setFormat(Format.XML);
     	optionsMgr.readOptions("DocumentLevelMetadata", readHandle);
     	String output = readHandle.get();
     	System.out.println(output);
        
	    // create query manager
		QueryManager queryMgr = client.newQueryManager();
				
		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder("DocumentLevelMetadata");
		StructuredQueryDefinition queryFinal = qb.term("noodle");
		
		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(queryFinal, resultsHandle);
		
		// get the result
		Document resultDoc = resultsHandle.get();
		//System.out.println(convertXMLDocumentToString(resultDoc));
		
		assertXpathEvaluatesTo("noodle", "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='name'])", resultDoc);
		//assertXpathEvaluatesTo("5", "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='constraint-meta'])", resultDoc);
		//assertXpathEvaluatesTo("Microsoft Office Word", "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='AppName'])", resultDoc);
                        
		// release client
	    client.release();	
	}
	
@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		
	}
}
