/*
 * Copyright 2014-2016 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marklogic.client.functionaltest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.namespace.QName;
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
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.Transaction;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
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
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.QueryManager.QueryView;

public class TestBulkSearchWithKeyValQueryDef extends BasicJavaClientREST {

	private static final int BATCH_SIZE=100;
	private static final String DIRECTORY ="/bulkSearch/";
	private static String dbName = "TestBulkSearchKVDDB";
	private static String [] fNames = {"TestBulkSearchKVDDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private  DatabaseClient client ;
/* this test is commented out untill we make decision in Issue 88
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
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		//		 create new connection for each test below
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
	public void testBulkSearchKVDwithDifferentPageSizes() throws IOException, ParserConfigurationException, SAXException, TransformerException {
		int count;
		loadXMLDocuments();
		//Creating a XML document manager for bulk search
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		//using QueryManger for query definition and set the search criteria
		QueryManager queryMgr = client.newQueryManager();
		KeyValueQueryDefinition kvqd = queryMgr.newKeyValueDefinition();
		kvqd.put(queryMgr.newElementLocator(new QName("foo")), "bar");
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
		DocumentPage page= docMgr.search(kvqd, 0);
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
			page = docMgr.search(kvqd, pageNo,sh);
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
		assertEquals("document count", 101,page.getTotalSize());
		page= docMgr.search(kvqd, 102);
		assertFalse("Page has any records ?",page.hasContent());
	}
	//This test is trying to set the setResponse to JSON on DocumentManager and use search handle which only work with XML
	@Test(expected = UnsupportedOperationException.class)
	public void testBulkSearchKVDwithWrongResponseFormat() throws Exception {

		TextDocumentManager docMgr = client.newTextDocumentManager();
		QueryManager queryMgr = client.newQueryManager();
		StringQueryDefinition qd = queryMgr.newStringDefinition();
		qd.setCriteria("bar");
		docMgr.setResponseFormat(Format.TEXT);
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
	//This test has set response to JSON and pass StringHandle with format as JSON, expectint it to work, logged an issue 82
	@Test
	public void testBulkSearchKVDwithResponseFormatandStringHandle() throws Exception{
		int count =1;
		loadJSONDocuments();
		JSONDocumentManager docMgr = client.newJSONDocumentManager();

		QueryManager queryMgr = client.newQueryManager();
		KeyValueQueryDefinition kvqd = queryMgr.newKeyValueDefinition();
		kvqd.put(queryMgr.newKeyLocator("says"), "woof");
		SearchHandle sh=queryMgr.search(kvqd, new SearchHandle());
		System.out.println(sh.getTotalResults());
		StringQueryDefinition qd = queryMgr.newStringDefinition();
		qd.setCriteria("woof");
		 sh=queryMgr.search(qd, new SearchHandle());
		System.out.println(sh.getTotalResults());
		docMgr.setResponseFormat(Format.JSON);
		docMgr.setSearchView(QueryView.METADATA);
		docMgr.setMetadataCategories(Metadata.PERMISSIONS);

		StringHandle results = new StringHandle().withFormat(Format.JSON);
		DocumentPage page= docMgr.search(kvqd, 1,results);
		DocumentMetadataHandle mh = new DocumentMetadataHandle();
		while(page.hasNext()){
			DocumentRecord rec = page.next();
			validateRecord(rec,Format.JSON);
			docMgr.readMetadata(rec.getUri(),mh);
			assertTrue("Records has permissions? ",mh.getPermissions().containsKey("flexrep-eval"));
			assertTrue("Record has collections ?",mh.getCollections().isEmpty());
			count++;
		}
		assertFalse("Search handle contains",results.get().isEmpty());

	}
	//This test is testing SearchView options and search handle
	@Test
	public void testBulkSearchKVDwithJSONResponseFormat() throws Exception{


		loadJSONDocuments();
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		QueryManager queryMgr = client.newQueryManager();
		KeyValueQueryDefinition kvqd = queryMgr.newKeyValueDefinition();
		kvqd.put(queryMgr.newKeyLocator("says"), "woof");
		docMgr.setResponseFormat(Format.JSON);

		docMgr.setSearchView(QueryView.FACETS);
		JacksonHandle jh = new JacksonHandle();
		DocumentPage page= docMgr.search(kvqd, 1,jh);

		//		System.out.println(jh.get().toString());
		assertTrue("Searh response has entry for facets",jh.get().has("facets"));
		assertTrue("Searh response has entry for facets",jh.get().has("results"));//Issue 84 is tracking this
		assertTrue("Searh response has entry for facets",jh.get().has("metrics"));

		docMgr.setSearchView(QueryView.RESULTS);
		page= docMgr.search(kvqd, 1,jh);

		assertFalse("Searh response has entry for facets",jh.get().has("facets"));
		assertTrue("Searh response has entry for facets",jh.get().has("results"));
		assertTrue("Searh response has entry for facets",jh.get().has("metrics"));//Issue 84 is tracking this

		docMgr.setSearchView(QueryView.METADATA);
		page= docMgr.search(kvqd, 1,jh);

		assertFalse("Searh response has entry for facets",jh.get().has("facets"));
		assertTrue("Searh response has entry for facets",jh.get().has("results"));
		assertTrue("Searh response has entry for facets",jh.get().has("metrics"));

		docMgr.setSearchView(QueryView.ALL);
		page= docMgr.search(kvqd, 1,jh);

		assertTrue("Searh response has entry for facets",jh.get().has("facets"));
		assertTrue("Searh response has entry for facets",jh.get().has("results"));
		assertTrue("Searh response has entry for facets",jh.get().has("metrics"));

		queryMgr.setView(QueryView.FACETS);
		queryMgr.search(kvqd, jh);
		System.out.println(jh.get().toString());

	}

	//This test is to verify the transactions, verifies the search works with transaction before commit, after rollback and after commit
	@Test
	public void testBulkSearchKVDwithTransactionsandDOMHandle() throws Exception{
		TextDocumentManager docMgr = client.newTextDocumentManager();
		DOMHandle results = new DOMHandle();
		QueryManager queryMgr = client.newQueryManager();
		KeyValueQueryDefinition kvqd = queryMgr.newKeyValueDefinition();
		kvqd.put(queryMgr.newElementLocator(new QName("foo")), "thought");
		Transaction t= client.openTransaction();
		try{
			this.loadXMLDocuments();
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

			DocumentPage page= docMgr.search(kvqd, 1,results,t);
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

		DocumentPage page= docMgr.search(kvqd, 1,results);
		assertEquals("Total search results after rollback are ","0",results.get().getElementsByTagNameNS("*", "response").item(0).getAttributes().getNamedItem("total").getNodeValue());

	}

*/
}
