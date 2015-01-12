/*
 * Copyright 2014-2015 MarkLogic Corporation
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

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
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
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
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
		//			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
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
		docMgr.setNonDocumentFormat(Format.XML);
		assertEquals("format set on document manager","XML",docMgr.getNonDocumentFormat().toString());
		assertEquals("Queryview set on document manager ","RESULTS" ,docMgr.getSearchView().toString());
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
		System.out.println("Is this First page :"+page.isFirstPage()+page.getPageNumber());//this is bug
		System.out.println("Is this Last page :"+page.isLastPage());
		System.out.println("Is this First page has content:"+page.hasContent());
		assertTrue("Is this First page :",page.isFirstPage());//this is bug
		assertFalse("Is this Last page :",page.isLastPage());
		assertTrue("Is this First page has content:",page.hasContent());
		//		Need the Issue #75 to be fixed  
		assertFalse("Is first page has previous page ?",page.hasPreviousPage());
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
//		assertTrue("page count is 101 ",pageNo == page.getTotalPages());
		assertTrue("Page has previous page ?",page.hasPreviousPage());
		assertEquals("page size", 1,page.getPageSize());
		assertEquals("document count", 101,page.getTotalSize());
		page= docMgr.search(qd, 102);
		assertFalse("Page has any records ?",page.hasContent());
	}
	//This test is trying to set the setResponse to JSON on DocumentManager and use search handle which only work with XML
	@Test(expected = UnsupportedOperationException.class)
	public void testBulkSearchSQDwithWrongResponseFormat() throws Exception {
		loadTxtDocuments();
		TextDocumentManager docMgr = client.newTextDocumentManager();
		QueryManager queryMgr = client.newQueryManager();
		StringQueryDefinition qd = queryMgr.newStringDefinition();
		qd.setCriteria("bar");
		docMgr.setNonDocumentFormat(Format.JSON);
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
	//Testing issue 192
	@Test
	public void testBulkSearchSQDwithNoResults() throws Exception {
		loadTxtDocuments();
		TextDocumentManager docMgr = client.newTextDocumentManager();
		QueryManager queryMgr = client.newQueryManager();
		StringQueryDefinition qd = queryMgr.newStringDefinition();
		qd.setCriteria("zzzz");
		SearchHandle results = new SearchHandle();
		DocumentPage page= docMgr.search(qd, 1,results);
		assertFalse("Should return no results",page.hasNext());


	}
	//This test has set response to JSON and pass StringHandle with format as JSON, expectint it to work, logged an issue 82
	@Test
	public void testBulkSearchSQDwithResponseFormatandStringHandle() throws Exception{
		int count =1;
		loadTxtDocuments();
		loadJSONDocuments();
		TextDocumentManager docMgr = client.newTextDocumentManager();

		QueryManager queryMgr = client.newQueryManager();
		StringQueryDefinition qd = queryMgr.newStringDefinition();
		qd.setCriteria("bar");


		docMgr.setNonDocumentFormat(Format.JSON);
		docMgr.setSearchView(QueryView.METADATA);
		docMgr.setMetadataCategories(Metadata.PERMISSIONS);

		StringHandle results = new StringHandle().withFormat(Format.JSON);
		DocumentPage page= docMgr.search(qd, 1,results);
		DocumentMetadataHandle mh = new DocumentMetadataHandle();
		while(page.hasNext()){
			DocumentRecord rec = page.next();
			validateRecord(rec,Format.TEXT);
			docMgr.readMetadata(rec.getUri(),mh);
			assertTrue("Records has permissions? ",mh.getPermissions().containsKey("flexrep-eval"));
			assertTrue("Record has collections ?",mh.getCollections().isEmpty());
			count++;
		}
		assertFalse("Search handle contains",results.get().isEmpty());


	}
	//This test is testing SearchView options and search handle
	@Test
	public void testBulkSearchSQDwithJSONResponseFormat() throws Exception{

		loadTxtDocuments();
		loadJSONDocuments();
		TextDocumentManager docMgr = client.newTextDocumentManager();

		QueryManager queryMgr = client.newQueryManager();
		StringQueryDefinition qd = queryMgr.newStringDefinition();
		qd.setCriteria("woof");
		docMgr.setNonDocumentFormat(Format.JSON);

		docMgr.setSearchView(QueryView.FACETS);
		JacksonHandle jh = new JacksonHandle();
		DocumentPage page= docMgr.search(qd, 1,jh);

		//		System.out.println(jh.get().toString());
		assertTrue("Searh response has entry for facets",jh.get().has("facets"));
		assertFalse("Searh response has entry for results",jh.get().has("results"));//Issue 84 is tracking this
		assertFalse("Searh response has entry for metrics",jh.get().has("metrics"));

		docMgr.setSearchView(QueryView.RESULTS);
		page= docMgr.search(qd, 1,jh);

		assertFalse("Searh response has entry for facets",jh.get().has("facets"));
		assertTrue("Searh response has entry for results",jh.get().has("results"));
		assertFalse("Searh response has entry for metrics",jh.get().has("metrics"));//Issue 84 is tracking this

		docMgr.setSearchView(QueryView.METADATA);
		page= docMgr.search(qd, 1,jh);

		assertFalse("Searh response has entry for facets",jh.get().has("facets"));
		assertFalse("Searh response has entry for results",jh.get().has("results"));
		assertTrue("Searh response has entry for metrics",jh.get().has("metrics"));

		docMgr.setSearchView(QueryView.ALL);
		page= docMgr.search(qd, 1,jh);

		assertTrue("Searh response has entry for facets",jh.get().has("facets"));
		assertTrue("Searh response has entry for results",jh.get().has("results"));
		assertTrue("Searh response has entry for metrics",jh.get().has("metrics"));

		queryMgr.setView(QueryView.FACETS);
		queryMgr.search(qd, jh);
		System.out.println(jh.get().toString());

	}

	//This test is to verify the transactions, verifies the search works with transaction before commit, after rollback and after commit
	@Test
	public void testBulkSearchSQDwithTransactionsandDOMHandle() throws Exception{
		TextDocumentManager docMgr = client.newTextDocumentManager();
		DOMHandle results = new DOMHandle();
		QueryManager queryMgr = client.newQueryManager();
		StringQueryDefinition qd = queryMgr.newStringDefinition();
		qd.setCriteria("thought");
		Transaction t= client.openTransaction();
		try{
			loadTxtDocuments();
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

		DocumentPage page= docMgr.search(qd, 1,results);
		System.out.println(this.convertXMLDocumentToString(results.get()));

		assertNull("Total search results after rollback are ",results.get().getElementsByTagNameNS("*", "response").item(0).getAttributes().getNamedItem("total"));

	}
	
	
	/* Searching for boolean and string in XML element using value query.
	 * Purpose: To validate QueryBuilder's new value methods (in 8.0) in XML document using an element.
	 * 
	 * Load a file that has a boolean value in a XML attribute and use query def to search on that boolean value
	 *  
	 * Methods used : value(StructuredQueryBuilder.TextIndex index, boolean)
	 *                value(StructuredQueryBuilder.TextIndex index, String)
	 */
	@Test  
	public void testQueryBuilderValueWithBooleanAndString() throws XpathException, SAXException, IOException {
		
		String docId[] = {"play-persons.xml"};
		
		TextDocumentManager docMgr = client.newTextDocumentManager();
		QueryManager queryMgr = client.newQueryManager();
		DocumentWriteSet writeset = docMgr.newWriteSet();
		
		// Put meta-data
		
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		metadataHandle.getCollections().addAll("my-collection1","my-collection2");
		metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
		metadataHandle.getProperties().put("reviewed", true);
		metadataHandle.getProperties().put("myString", "foo");
		metadataHandle.getProperties().put("myInteger", 10);
		metadataHandle.getProperties().put("myDecimal", 34.56678);
		metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
		metadataHandle.setQuality(23);

		writeset.addDefault(metadataHandle);
		
		// Create a new document using StringHandle
		StringBuffer strBuf = new StringBuffer(); 
		
		strBuf.append("<PLAY>");
		strBuf.append("<TITLE>All's Well That Ends Well</TITLE>");
		strBuf.append("<PERSONAE>");
		strBuf.append("<TITLE>Dramatis Personae</TITLE>");
		
		strBuf.append("<PGROUP>");
		strBuf.append("<subgroup>true</subgroup>");
		
		strBuf.append("<PERSONA>KING OF FRANCE</PERSONA>");
		strBuf.append("<PERSONA>DUKE OF FLORENCE</PERSONA>");
		strBuf.append("<PERSONA>BERTRAM, Count of Rousillon.</PERSONA>");
		strBuf.append("<PERSONA>LAFEU, an old lord.</PERSONA>");
		strBuf.append("</PGROUP>");
		
		strBuf.append("<PGROUP>");
		strBuf.append("<subgroup>false</subgroup>");
		
		strBuf.append("<PERSONA>PAROLLES, a follower of Bertram.</PERSONA>");
		strBuf.append("<PERSONA>A Page. </PERSONA>");
		strBuf.append("</PGROUP>");
		
		strBuf.append("<PGROUP>");
		strBuf.append("<subgroup>false</subgroup>");
		strBuf.append("<PERSONA>COUNTESS OF ROUSILLON, mother to Bertram. </PERSONA>");
		strBuf.append("<PERSONA>HELENA, a gentlewoman protected by the Countess.</PERSONA>");
		strBuf.append("<PERSONA>An old Widow of Florence. </PERSONA>");
		strBuf.append("<PERSONA>DIANA, daughter to the Widow.</PERSONA>");
		strBuf.append("</PGROUP>");
		
		strBuf.append("<PGROUP>");
		strBuf.append("<subgroup>false</subgroup>");
		strBuf.append("<PERSONA>VIOLENTA</PERSONA>");
		strBuf.append("<PERSONA>MARIANA</PERSONA>");
		strBuf.append("<GRPDESCR>neighbours and friends to the Widow.</GRPDESCR>");
		strBuf.append("</PGROUP>");
		
		strBuf.append("<PERSONA>Lords, Officers, Soldiers, &amp;c., French and Florentine.</PERSONA>");
		strBuf.append("</PERSONAE>");
		strBuf.append("</PLAY>");
				
		writeset.add("/1/"+docId[0], new StringHandle().with(strBuf.toString()));
		docMgr.write(writeset);
				
		docMgr.write(writeset);

		// Search for the range with attribute value true in rangeRelativeBucketConstraintOpt.xml document.
		StructuredQueryBuilder qb = new StructuredQueryBuilder();
		
		// Build an object that represents StructuredQueryBuilder.ElementAttribute for use in values method
		// that is of type StructuredQueryBuilder.TextIndex
		
		QueryDefinition qd = qb.value(qb.element("subgroup"), false);
				
		// Create handle for the result
		StringHandle resultsHandle = new StringHandle().withFormat(Format.XML);
		queryMgr.search(qd, resultsHandle);

		// Get the result
		String resultDoc = resultsHandle.get();
		
		System.out.println(resultDoc);
		//Verify that search response has found 1 element attribute 
		assertXpathEvaluatesTo("fn:doc(\"/1/play-persons.xml\")", "string(//*[local-name()='response']//*[local-name()='result']//@*[local-name()='path'])", resultDoc);
		assertXpathEvaluatesTo("3", "count(//*[local-name()='response']//*[local-name()='match'])", resultDoc);
		
		// Search for the following royal (XML ELEMENT) in all-well.xml document.
		StructuredQueryBuilder qbStr = new StructuredQueryBuilder();
		QueryDefinition qdStr = qbStr.value(qbStr.element("PERSONA"), "KING OF FRANCE","DUKE OF FLORENCE", "BERTRAM, Count of Rousillon.","LAFEU, an old lord.");

		// Create handle for the result
		StringHandle resultsHandleStr = new StringHandle().withFormat(Format.XML);
		queryMgr.search(qdStr, resultsHandleStr);

		// Get the result
		String resultDocStr = resultsHandleStr.get();

		System.out.println(resultDocStr);
		//Verify that search response has found 4 PERSONA elements under /PLAY/PERSONAE
		assertXpathEvaluatesTo("fn:doc(\"/1/play-persons.xml\")", "string(//*[local-name()='response']//*[local-name()='result']//@*[local-name()='path'])", resultDocStr);
		assertXpathEvaluatesTo("4", "count(//*[local-name()='response']//*[local-name()='match'])", resultDocStr);
	}
}
