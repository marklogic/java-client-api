package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.Transaction;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;

public class TestBulkSearchWithStrucQueryDef extends BasicJavaClientREST{

	private static final int BATCH_SIZE=100;
	private static final String DIRECTORY ="/bulkSearch/";
	private static String dbName = "TestBulkSearchStrucQDDB";
	private static String [] fNames = {"TestBulkSearchStrucQDDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private  DatabaseClient client ;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
		setupAppServicesConstraint(dbName);
		createRESTUserWithPermissions("usr1", "password",getPermissionNode("flexrep-eval",Capability.READ),getCollectionNode("http://permission-collections/"), "rest-writer","rest-reader" );
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down" );
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		deleteRESTUser("usr1");
	}

	@Before
	public void setUp() throws Exception {
		// System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		// create new connection for each test below
		client = DatabaseClientFactory.newClient("localhost", restPort, "usr1", "password", Authentication.DIGEST);
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("Running clear script");	
		// release client
		client.release();
	}
	public void loadJSONDocuments() throws JsonProcessingException, IOException{
		int count=1;	 
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();

		HashMap<String,String> map= new HashMap<String,String>();

		for(int i =0;i<102;i++){
			JsonNode jn = new ObjectMapper().readTree("{\"animal\":\"dog"+i+"\", \"says\":\"woof\"}");
			JacksonHandle jh = new JacksonHandle();
			jh.set(jn);
			writeset.add(DIRECTORY+"dog"+i+".json",jh);
			map.put(DIRECTORY+"dog"+i+".json", jn.toString());
			if(count%BATCH_SIZE == 0){
				docMgr.write(writeset);
				writeset = docMgr.newWriteSet();
			}
			count++;
			//	      System.out.println(jn.toString());
		}
		if(count%BATCH_SIZE > 0){
			docMgr.write(writeset);
		}
	}
	public void validateRecord(DocumentRecord record,Format type) {

		assertNotNull("DocumentRecord should never be null", record);
		assertNotNull("Document uri should never be null", record.getUri());
		assertTrue("Document uri should start with " + DIRECTORY, record.getUri().startsWith(DIRECTORY));
		assertEquals("All records are expected to be in same format", type, record.getFormat());
		//        System.out.println(record.getMimetype());

	}

	public void loadXMLDocuments() throws IOException, ParserConfigurationException, SAXException, TransformerException{
		int count=1;
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		for(int i =0;i<102;i++){

			writeset.add(DIRECTORY+"foo"+i+".xml", new DOMHandle(getDocumentContent("This is so foo with a bar "+i)));

			if(count%BATCH_SIZE == 0){
				docMgr.write(writeset);
				writeset = docMgr.newWriteSet();
			}
			count++;
		}
		if(count%BATCH_SIZE > 0){
			docMgr.write(writeset);
		}
	}
	@Test
	public void testBulkSearchSQDwithDifferentPageSizes() throws IOException, ParserConfigurationException, SAXException, TransformerException {
		int count;
		loadXMLDocuments();
		//Creating a txt document manager for bulk search
		TextDocumentManager docMgr = client.newTextDocumentManager();
		//using QueryManger for query definition and set the search criteria
		StructuredQueryBuilder qb = new StructuredQueryBuilder();
		StructuredQueryDefinition qd = qb.and(qb.term("foo","bar"));

		// set  document manager level settings for search response
		System.out.println("Default Page length setting on docMgr :"+docMgr.getPageLength());
		docMgr.setPageLength(1);
		docMgr.setSearchView(QueryView.RESULTS);
		docMgr.setResponseFormat(Format.XML);
		assertEquals("format set on document manager","XML",docMgr.getResponseFormat().toString());
		assertEquals("Queryview set on document manager ","RESULTS" ,docMgr.getSearchView().toString());
		assertEquals("Page length ",1,docMgr.getPageLength());
		// Search for documents where content has bar and get first result record, get search handle on it
		SearchHandle sh = new SearchHandle();
		DocumentPage page= docMgr.search(qd, 0);
		// test for page methods
		assertEquals("Number of records",1,page.size());
		assertEquals("Starting record in first page ",1,page.getStart());
		assertEquals("Total number of estimated results:",102,page.getTotalSize());
		assertEquals("Total number of estimated pages :",102,page.getTotalPages());
		// till the issue #78 get fixed
		assertFalse("Is this First page :",page.isFirstPage());//this is bug
		assertFalse("Is this Last page :",page.isLastPage());
		assertTrue("Is this First page has content:",page.hasContent());
		//		Need the Issue #75 to be fixed  
		assertTrue("Is first page has previous page ?",page.hasPreviousPage());
		//		
		long pageNo=1;
		do{
			count=0;
			page = docMgr.search(qd, pageNo,sh);
			if(pageNo >1){ 
				assertFalse("Is this first Page", page.isFirstPage());
				assertTrue("Is page has previous page ?",page.hasPreviousPage());
			}
			while(page.hasNext()){
				DocumentRecord rec = page.next();
				rec.getFormat();
				validateRecord(rec,Format.XML);
				//		System.out.println(rec.getUri());
				count++;
			}
			MatchDocumentSummary[] mds= sh.getMatchResults();
			assertEquals("Matched document count",1,mds.length);
			//since we set the query view to get only results, facet count supposed be 0
			assertEquals("Matched Facet count",0,sh.getFacetNames().length);

			assertEquals("document count", page.size(),count);
			//			assertEquals("Page Number #",pageNo,page.getPageNumber());
			pageNo = pageNo + page.getPageSize();
		}while(!page.isLastPage());
		assertTrue("page count is 101 ",pageNo == page.getTotalPages());
		assertTrue("Page has previous page ?",page.hasPreviousPage());
		assertEquals("page size", 1,page.getPageSize());
		assertEquals("document count", 102,page.getTotalSize());
		page= docMgr.search(qd, 103);
		assertFalse("Page has any records ?",page.hasContent());
	}

	//This test has set response to JSON and pass StringHandle with format as JSON, expectint it to work, logged an issue 82
	@Test
	public void testBulkSearchSQDwithResponseFormatandStringHandle() throws Exception{
		loadJSONDocuments();
		JSONDocumentManager docMgr = client.newJSONDocumentManager();

		QueryManager queryMgr = client.newQueryManager();
		StructuredQueryBuilder qb = new StructuredQueryBuilder();
		StructuredQueryDefinition qd = qb.and(qb.term("dog1","dog11"));
		queryMgr.search(qd, new SearchHandle());

		docMgr.setResponseFormat(Format.JSON);
		docMgr.setSearchView(QueryView.METADATA);
		docMgr.setMetadataCategories(Metadata.PERMISSIONS);

		StringHandle results = new StringHandle().withFormat(Format.JSON);
		DocumentPage page= docMgr.search(qd, 1,results);
		DocumentMetadataHandle mh = new DocumentMetadataHandle();
		while(page.hasNext()){
			DocumentRecord rec = page.next();
			validateRecord(rec,Format.JSON);
			docMgr.readMetadata(rec.getUri(),mh);
			assertTrue("Records has permissions? ",mh.getPermissions().containsKey("flexrep-eval"));
			assertTrue("Record has collections ?",mh.getCollections().isEmpty());
		}
		assertFalse("Search handle contains",results.get().isEmpty());


	}
	//This test is testing SearchView options and search handle
	@Test
	public void testBulkSearchSQDwithJSONResponseFormat() throws Exception{

		loadJSONDocuments();
		JSONDocumentManager docMgr = client.newJSONDocumentManager();

		QueryManager queryMgr = client.newQueryManager();
		StructuredQueryBuilder qb = new StructuredQueryBuilder();
		StructuredQueryDefinition qd = qb.and(qb.term("woof"));
		docMgr.setResponseFormat(Format.JSON);

		docMgr.setSearchView(QueryView.FACETS);
		JacksonHandle jh = new JacksonHandle();
		docMgr.search(qd, 1,jh);

		//		System.out.println(jh.get().toString());
		assertTrue("Searh response has entry for facets",jh.get().has("facets"));
		assertTrue("Searh response has entry for facets",jh.get().has("results"));//Issue 84 is tracking this
		assertTrue("Searh response has entry for facets",jh.get().has("metrics"));

		docMgr.setSearchView(QueryView.RESULTS);
		docMgr.search(qd, 1,jh);

		assertFalse("Searh response has entry for facets",jh.get().has("facets"));
		assertTrue("Searh response has entry for facets",jh.get().has("results"));
		assertTrue("Searh response has entry for facets",jh.get().has("metrics"));//Issue 84 is tracking this

		docMgr.setSearchView(QueryView.METADATA);
		docMgr.search(qd, 1,jh);

		assertFalse("Searh response has entry for facets",jh.get().has("facets"));
		assertTrue("Searh response has entry for facets",jh.get().has("results"));
		assertTrue("Searh response has entry for facets",jh.get().has("metrics"));

		docMgr.setSearchView(QueryView.ALL);
		docMgr.search(qd, 1,jh);

		assertTrue("Searh response has entry for facets",jh.get().has("facets"));
		assertTrue("Searh response has entry for facets",jh.get().has("results"));
		assertTrue("Searh response has entry for facets",jh.get().has("metrics"));

		queryMgr.setView(QueryView.FACETS);
		queryMgr.search(qd, jh);
		System.out.println(jh.get().toString());

	}

	//This test is to verify the transactions, verifies the search works with transaction before commit, after rollback and after commit
	@Test
	public void testBulkSearchSQDwithTransactionsandDOMHandle() throws Exception{
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DOMHandle results = new DOMHandle();
		StructuredQueryBuilder qb = new StructuredQueryBuilder();
		StructuredQueryDefinition qd = qb.and(qb.term("much","thought"));
		Transaction t= client.openTransaction();
		try{
			int count=1;
			XMLDocumentManager xmldocMgr = client.newXMLDocumentManager();
			DocumentWriteSet writeset =xmldocMgr.newWriteSet();
			for(int i =0;i<102;i++){
				writeset.add(DIRECTORY+"boo"+i+".xml", new DOMHandle(getDocumentContent("This is so too much thought "+i)));
				if(count%BATCH_SIZE == 0){
					xmldocMgr.write(writeset,t);
					writeset = xmldocMgr.newWriteSet();
				}
				count++;
			}
			if(count%BATCH_SIZE > 0){
				xmldocMgr.write(writeset,t);
			}
			count=0;
			docMgr.setSearchView(QueryView.RESULTS);		

			DocumentPage page= docMgr.search(qd, 1,results,t);
			while(page.hasNext()){
				DocumentRecord rec = page.next();

				validateRecord(rec,Format.XML);
				count++;
			}
			assertTrue("Page has conttent :",page.hasContent());
			assertEquals("Total search results before transaction rollback are ","102",results.get().getElementsByTagNameNS("*", "response").item(0).getAttributes().getNamedItem("total").getNodeValue());
			//			System.out.println(results.get().getElementsByTagNameNS("*", "response").item(0).getAttributes().getNamedItem("total").getNodeValue());

		}catch(Exception e){ throw e;}
		finally{t.rollback();}

		docMgr.search(qd, 1,results);
		assertEquals("Total search results after rollback are ","0",results.get().getElementsByTagNameNS("*", "response").item(0).getAttributes().getNamedItem("total").getNodeValue());

	}
	//This test is to verify RAW XML structured Query
	@Test
	public void testBulkSearchRawXMLStrucQD() throws Exception{
//		setAutomaticDirectoryCreation(dbName,"automatic");
		setMaintainLastModified(dbName,true);
		this.loadJSONDocuments();
		this.loadXMLDocuments();
		GenericDocumentManager docMgr= client.newDocumentManager();
		QueryManager queryMgr = client.newQueryManager();
		String rawXMLQuery =
				"<search:query "+
						"xmlns:search='http://marklogic.com/appservices/search'>"+
						" <search:or-query><search:term-query>"+
						"<search:text>bar</search:text>"+
						"</search:term-query>"+
						"<search:term-query>"+
						"<search:text>woof</search:text>"+
						"</search:term-query> </search:or-query>"+
						"</search:query>";
		StringHandle rh = new StringHandle(rawXMLQuery);
		RawStructuredQueryDefinition qd =
				queryMgr.newRawStructuredQueryDefinition(rh);
		DOMHandle dh = new DOMHandle();
		DocumentPage page= docMgr.search(qd, 1,dh);
		DocumentMetadataHandle mh = new DocumentMetadataHandle();

		int count=1;
		while(count < 4)
		{
			page= docMgr.search(qd, count,dh);

			while(page.hasNext()){
				DocumentRecord rec = page.next();
				if(rec.getMimetype().contains("xml")){
					validateRecord(rec,Format.XML);
				}
				else{
					validateRecord(rec,Format.JSON);
				}
				docMgr.readMetadata(rec.getUri(),mh);
				assertTrue("Records has permissions? ",mh.getPermissions().containsKey("flexrep-eval"));
				assertFalse("Record has collections ?",mh.getCollections().isEmpty());

			}
			System.out.println(this.convertXMLDocumentToString(dh.get()));
			assertEquals("Total search results before transaction rollback are ","204",dh.get().getElementsByTagNameNS("*", "response").item(0).getAttributes().getNamedItem("total").getNodeValue());
			count++;

		}

	}
	//This test is to verify RAW JSON structured query
	@Test
	public void testBulkSearchRawJSONStrucQD() throws Exception{
//		setAutomaticDirectoryCreation(dbName,"automatic");
		setMaintainLastModified(dbName,true);
		this.loadJSONDocuments();
		this.loadXMLDocuments();
		GenericDocumentManager docMgr= client.newDocumentManager();
		QueryManager queryMgr = client.newQueryManager();
		JacksonHandle jh = new JacksonHandle();
		ObjectMapper mapper = new ObjectMapper();
		//	constructing JSON representation of Raw JSON Structured Query

		ObjectNode txtNode = mapper.createObjectNode();
		txtNode.putArray("text").add("woof");
		ObjectNode termQNode = mapper.createObjectNode();
		termQNode.set("term-query", txtNode);
		ObjectNode queriesArrayNode = mapper.createObjectNode();
		queriesArrayNode.putArray("queries").add(termQNode);

		ObjectNode txtNode2 = mapper.createObjectNode();
		txtNode2.putArray("text").add("bar");
		ObjectNode termQNode2 = mapper.createObjectNode();
		termQNode2.set("term-query", txtNode2);
		queriesArrayNode.withArray("queries").add(termQNode2);

		ObjectNode orQueryNode = mapper.createObjectNode();
		orQueryNode.set("or-query",queriesArrayNode );

		ObjectNode queryArrayNode = mapper.createObjectNode();
		queryArrayNode.putArray("queries").add(orQueryNode);
		ObjectNode mainNode = mapper.createObjectNode();
		mainNode.set("query", queryArrayNode);
		jh.set(mainNode);
		RawStructuredQueryDefinition qd =
				queryMgr.newRawStructuredQueryDefinition(jh);
		System.out.println(jh.get().toString());
		docMgr.setResponseFormat(Format.JSON);
		JacksonHandle results = new JacksonHandle();
		DocumentPage page= docMgr.search(qd, 1,results);
		DocumentMetadataHandle mh = new DocumentMetadataHandle();

		int count=1;
		while(count < 4)
		{
			page= docMgr.search(qd, count,results);

			while(page.hasNext()){
				DocumentRecord rec = page.next();
				if(rec.getMimetype().contains("xml")){
					validateRecord(rec,Format.XML);
				}
				else{
					validateRecord(rec,Format.JSON);
				}
				docMgr.readMetadata(rec.getUri(),mh);
				assertTrue("Records has permissions? ",mh.getPermissions().containsKey("flexrep-eval"));
				assertFalse("Record has collections ?",mh.getCollections().isEmpty());

			}

			count++;
		}
		System.out.println(results.get().toString());
		assertEquals("Total search results before transaction rollback are ","204",results.get().get("total").asText());
	}
}
