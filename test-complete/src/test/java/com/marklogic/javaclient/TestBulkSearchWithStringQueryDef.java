package com.marklogic.javaclient;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.query.StringQueryDefinition;

public class TestBulkSearchWithStringQueryDef extends BasicJavaClientREST{
	private static final int BATCH_SIZE=100;
	private static final String DIRECTORY ="/bulkSearch/";
	private static String dbName = "TestBulkSearchSQDDB";
	private static String [] fNames = {"TestBulkSearchSQDDB-1"};
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
		//	System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		// create new connection for each test below
		client = DatabaseClientFactory.newClient("localhost", restPort, "usr1", "password", Authentication.DIGEST);
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("Running clear script");	
		// release client
		client.release();
	}

	public void validateRecord(DocumentRecord record,Format type) {

		assertNotNull("DocumentRecord should never be null", record);
		assertNotNull("Document uri should never be null", record.getUri());
		assertTrue("Document uri should start with " + DIRECTORY, record.getUri().startsWith(DIRECTORY));
		assertEquals("All records are expected to be in same format", type, record.getFormat());
		//        System.out.println(record.getMimetype());

	}
	public void loadTxtDocuments(){
		int count =1;
		TextDocumentManager docMgr = client.newTextDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		for(int i =0;i<101;i++){
			writeset.add(DIRECTORY+"Textfoo"+i+".txt", new StringHandle().with("bar can be foo"+i));
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
	public void testBulkSearchSQDwithDifferentPageSizes() {
		int count;
		loadTxtDocuments();
		//Creating a txt document manager for bulk search
		TextDocumentManager docMgr = client.newTextDocumentManager();
		//using QueryManger for query definition and set the search criteria
		QueryManager queryMgr = client.newQueryManager();
		StringQueryDefinition qd = queryMgr.newStringDefinition();
		qd.setCriteria("bar");
		// set  document manager level settings for search response
		System.out.println("Default Page length setting on docMgr :"+docMgr.getPageLength());
		docMgr.setPageLength(1);
		docMgr.setSearchView(QueryView.RESULTS);
		docMgr.setResponseFormat(Format.XML);
		assertEquals("format set on document manager","XML",docMgr.getResponseFormat().toString());
		//		assertEquals("Queryview set on document manager ","RESULTS" ,docMgr.getSearchView().toString());
		assertEquals("Page length ",1,docMgr.getPageLength());
		// Search for documents where content has bar and get first result record, get search handle on it
		SearchHandle sh = new SearchHandle();
		DocumentPage page= docMgr.search(qd, 0);
		// test for page methods
		assertEquals("Number of records",1,page.size());
		assertEquals("Starting record in first page ",1,page.getStart());
		assertEquals("Total number of estimated results:",101,page.getTotalSize());
		assertEquals("Total number of estimated pages :",101,page.getTotalPages());
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
				validateRecord(rec,Format.TEXT);
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
		assertEquals("document count", 101,page.getTotalSize());
		page= docMgr.search(qd, 102);
		assertFalse("Page has any records ?",page.hasContent());
	}
	//This test is trying to set the setResponse to JSON on DocumentManager and use search handle which only work with XML
	@Test(expected = UnsupportedOperationException.class)
	public void testBulkSearchSQDwithWrongResponseFormat() {
		loadTxtDocuments();
		TextDocumentManager docMgr = client.newTextDocumentManager();
		QueryManager queryMgr = client.newQueryManager();
		StringQueryDefinition qd = queryMgr.newStringDefinition();
		qd.setCriteria("bar");
		System.out.println(docMgr.getResponseFormat().toString());
		docMgr.setResponseFormat(Format.JSON);
		SearchHandle results = new SearchHandle();
		DocumentPage page= docMgr.search(qd, 1,results);
		MatchDocumentSummary[] summaries = results.getMatchResults();
		for (MatchDocumentSummary summary : summaries ) {
			MatchLocation[] locations = summary.getMatchLocations();
			for (MatchLocation location : locations) {
				System.out.println(location.getAllSnippetText());
				// do something with the snippet text
			}
		}

	}
	//
	@Test
	public void testBulkSearchSQDwithResponseFormat() {
		int count =1;
		loadTxtDocuments();
		TextDocumentManager docMgr = client.newTextDocumentManager();
		docMgr.setResponseFormat(Format.TEXT);
		QueryManager queryMgr = client.newQueryManager();
		StringQueryDefinition qd = queryMgr.newStringDefinition();
		qd.setCriteria("bar");
		System.out.println(docMgr.getResponseFormat().toString());
		//		docMgr.setResponseFormat(Format.JSON);
		SearchHandle results = new SearchHandle();
		DocumentPage page= docMgr.search(qd, 1,results);
		MatchDocumentSummary[] summaries = results.getMatchResults();
		for (MatchDocumentSummary summary : summaries ) {
			MatchLocation[] locations = summary.getMatchLocations();
			for (MatchLocation location : locations) {
				System.out.println(summary.getUri());
				// do something with the snippet text
				System.out.println(location.getPath());
			}
		}

	}
}
