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

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.JacksonParserHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import com.marklogic.client.query.QueryManager.QueryView;

public class TestBulkSearchEWithQBE extends BasicJavaClientREST{
	private static final int BATCH_SIZE=100;
	private static final String DIRECTORY ="/bulkSearch/";
	private static String dbName = "TestBulkSearchQBEDB";
	private static String [] fNames = {"TestBulkSearchQBEDB-1"};
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
		loadTxtDocuments();
		loadXMLDocuments();
		loadJSONDocuments();
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
	public void loadXMLDocuments() throws IOException, ParserConfigurationException, SAXException, TransformerException{
		int count=1;
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		for(int i =0;i<102;i++){
			Document doc = this.getDocumentContent("This is so foo with a bar "+i);
			Element childElement = doc.createElement("author");
			childElement.appendChild(doc.createTextNode("rhiea"));
			doc.getElementsByTagName("foo").item(0).appendChild(childElement);
			writeset.add(DIRECTORY+"foo"+i+".xml", new DOMHandle(doc));

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
	public void loadJSONDocuments() throws JsonProcessingException, IOException{
		int count=1;	 
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();

		HashMap<String,String> map= new HashMap<String,String>();

		for(int i =0;i<102;i++){
			JsonNode jn = new ObjectMapper().readTree("{\"animal\":\"dog "+i+"\", \"says\":\"woof\"}");
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
	@Test
	public void testBulkSearchQBEWithXMLResponseFormat() throws IOException, ParserConfigurationException, SAXException, TransformerException, XpathException {
		int count;
		//Creating a xml document manager for bulk search
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		//using QBE for query definition and set the search criteria

		QueryManager queryMgr = client.newQueryManager();
		String queryAsString = 
				"<q:qbe xmlns:q=\"http://marklogic.com/appservices/querybyexample\"><q:query><foo><q:word>foo</q:word></foo></q:query></q:qbe>";
		RawQueryByExampleDefinition qd = queryMgr.newRawQueryByExampleDefinition(new StringHandle(queryAsString));

		// set  document manager level settings for search response
		docMgr.setPageLength(25);
		docMgr.setSearchView(QueryView.RESULTS);
		docMgr.setNonDocumentFormat(Format.XML);

		// Search for documents where content has bar and get first result record, get search handle on it,Use DOMHandle to read results
		DOMHandle dh = new DOMHandle();
		DocumentPage page;

		long pageNo=1;
		do{
			count=0;
			page = docMgr.search(qd, pageNo,dh);
			if(pageNo >1){ 
				assertFalse("Is this first Page", page.isFirstPage());
				assertTrue("Is page has previous page ?",page.hasPreviousPage());
			}
			while(page.hasNext()){
				DocumentRecord rec = page.next();
				rec.getFormat();
				validateRecord(rec,Format.XML);

				count++;
			}

			Document resultDoc = dh.get();
			assertXpathEvaluatesTo("xml", "string(//*[local-name()='result'][last()]//@*[local-name()='format'])", resultDoc);
			assertEquals("document count", page.size(),count);
			//			assertEquals("Page Number #",pageNo,page.getPageNumber());
			pageNo = pageNo + page.getPageSize();
		}while(!page.isLastPage() &&  page.hasContent() );
		assertEquals("page count is 5 ",5, page.getTotalPages());
		assertTrue("Page has previous page ?",page.hasPreviousPage());
		assertEquals("page size", 25,page.getPageSize());
		assertEquals("document count", 102,page.getTotalSize());

	}
	@Test
	public void testBulkSearchQBEWithJSONResponseFormat() throws IOException, ParserConfigurationException, SAXException, TransformerException {
		int count;

		//Creating a xml document manager for bulk search
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		//using QBE for query definition and set the search criteria

		QueryManager queryMgr = client.newQueryManager();
		String queryAsString = "{\"$query\": { \"says\": {\"$word\":\"woof\",\"$exact\": false}}}";
		RawQueryByExampleDefinition qd = queryMgr.newRawQueryByExampleDefinition(new StringHandle(queryAsString).withFormat(Format.JSON));

		// set  document manager level settings for search response
		docMgr.setPageLength(25);
		docMgr.setSearchView(QueryView.RESULTS);
		docMgr.setNonDocumentFormat(Format.JSON);

		// Search for documents where content has bar and get first result record, get search handle on it,Use DOMHandle to read results
		JacksonHandle sh = new JacksonHandle();
		DocumentPage page;

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
				validateRecord(rec,Format.JSON);
				System.out.println(rec.getContent(new StringHandle()).get().toString());
				count++;
			}
			assertTrue("Page start in results and on page",sh.get().get("start").asLong() == page.getStart());
			assertEquals("document count", page.size(),count);
			//			assertEquals("Page Number #",pageNo,page.getPageNumber());
			pageNo = pageNo + page.getPageSize();
		}while(!page.isLastPage() &&  page.hasContent() );

		assertEquals("page count is  ",5,page.getTotalPages());
		assertTrue("Page has previous page ?",page.hasPreviousPage());
		assertEquals("page size", 25,page.getPageSize());
		assertEquals("document count", 102,page.getTotalSize());

	}
	@Test
	public void testBulkSearchQBECombinedQuery() throws IOException, ParserConfigurationException, SAXException, TransformerException, XpathException {
		int count;

		//Creating a xml document manager for bulk search
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		//using QBE for query definition and set the search criteria

		QueryManager queryMgr = client.newQueryManager();

		String queryAsString = "<search:search "+
				"xmlns:search='http://marklogic.com/appservices/search'>"+
				"<search:query>"+
				"<search:term-query>"+
				"<search:text>bar</search:text>"+
				"</search:term-query>"+
				"<search:value-constraint-query>"+
				"<search:constraint-name>authorName</search:constraint-name>"+
				"<search:text>rhiea</search:text>"+
				"</search:value-constraint-query>"+
				"</search:query>"+
				"<search:options>"+
				"<search:constraint name='authorName'>"+
				"<search:value>"+
				"<search:element name='author' ns=''/>"+
				"</search:value>"+
				"</search:constraint>"+
				"</search:options>"+
				"</search:search>";
		RawCombinedQueryDefinition qd = queryMgr.newRawCombinedQueryDefinition(new StringHandle(queryAsString).withFormat(Format.XML));

		// set  document manager level settings for search response
		docMgr.setPageLength(25);
		docMgr.setSearchView(QueryView.RESULTS);

		// Search for documents where content has bar and get first result record, get search handle on it,Use DOMHandle to read results
		DOMHandle dh = new DOMHandle();
		DocumentPage page;

		long pageNo=1;
		do{
			count=0;
			page = docMgr.search(qd, pageNo,dh);
			if(pageNo >1){ 
				assertFalse("Is this first Page", page.isFirstPage());
				assertTrue("Is page has previous page ?",page.hasPreviousPage());
			}
			while(page.hasNext()){
				DocumentRecord rec = page.next();
				validateRecord(rec,Format.XML);
				count++;
			}
			Document resultDoc = dh.get();
			assertXpathEvaluatesTo("xml", "string(//*[local-name()='result'][last()]//@*[local-name()='format'])", resultDoc);
			assertEquals("document count", page.size(),count);
			//			assertEquals("Page Number #",pageNo,page.getPageNumber());
			pageNo = pageNo + page.getPageSize();
		}while(!page.isLastPage() &&  page.hasContent() );

		assertEquals("page count is  ",5,page.getTotalPages());
		assertTrue("Page has previous page ?",page.hasPreviousPage());
		assertEquals("page size", 25,page.getPageSize());
		assertEquals("document count", 102,page.getTotalSize());

	}
	@Test
	public void testBulkSearchQBEWithJSONCombinedQuery() throws IOException, ParserConfigurationException, SAXException, TransformerException {
		int count;

		//Creating a xml document manager for bulk search
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		//using QBE for query definition and set the search criteria

		QueryManager queryMgr = client.newQueryManager();
		String queryAsString ="{\"search\":{\"query\":{\"value-constraint-query\":{\"constraint-name\":\"animal\", \"text\":\"woof\"}}, \"options\":{\"constraint\":{\"name\":\"animal\", \"value\":{\"json-property\":\"says\"}}}}}"; 
	
		RawCombinedQueryDefinition qd = queryMgr.newRawCombinedQueryDefinition(new StringHandle(queryAsString).withFormat(Format.JSON));
		// set  document manager level settings for search response
		docMgr.setPageLength(25);
		docMgr.setSearchView(QueryView.RESULTS);
		docMgr.setNonDocumentFormat(Format.JSON);
		
		// Search for documents where content has woof and get first result record, 
		JacksonHandle sh = new JacksonHandle();
		queryMgr.search(qd, sh);
		System.out.println(sh.get().toString());
		DocumentPage page;

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
				validateRecord(rec,Format.JSON);
				count++;
			}
			assertTrue("Page start in results and on page",sh.get().get("start").asLong() == page.getStart());
			assertEquals("document count", page.size(),count);
			pageNo = pageNo + page.getPageSize();
		}while(!page.isLastPage() &&  page.hasContent() );
		System.out.println(sh.get().toString());
		assertEquals("page count is  ",5,page.getTotalPages());
		assertTrue("Page has previous page ?",page.hasPreviousPage());
		assertEquals("page size", 25,page.getPageSize());
		assertEquals("document count", 102,page.getTotalSize());

	}
	
	/*
	 * This test method verifies if JacksonParserHandle class supports SearchReadHandle.
	 * Verifies Git Issue 116. The test functionality is same as testBulkSearchQBEWithJSONResponseFormat.
	 */
	
	@Test
	public void testBulkSearchQBEResponseInParserHandle() throws IOException, ParserConfigurationException, SAXException, TransformerException {
		int count;

		//Creating a xml document manager for bulk search
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		//using QBE for query definition and set the search criteria

		QueryManager queryMgr = client.newQueryManager();
		String queryAsString = "{\"$query\": { \"says\": {\"$word\":\"woof\",\"$exact\": false}}}";
		RawQueryByExampleDefinition qd = queryMgr.newRawQueryByExampleDefinition(new StringHandle(queryAsString).withFormat(Format.JSON));

		// set  document manager level settings for search response
		docMgr.setPageLength(25);
		docMgr.setSearchView(QueryView.RESULTS);
		docMgr.setNonDocumentFormat(Format.JSON);

		// Search for documents where content has bar and get first result record, get JacksonParserHandle on it,Use DOMHandle to read results
		JacksonParserHandle  sh = new JacksonParserHandle();
		DocumentPage page;

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
				validateRecord(rec,Format.JSON);
				System.out.println(rec.getContent(new StringHandle()).get().toString());
				count++;
			}
			
			assertEquals("document count", page.size(),count);
			pageNo = pageNo + page.getPageSize();
		}while(!page.isLastPage() &&  page.hasContent() );
		
		ObjectMapper mapper = new ObjectMapper();
		JsonParser  jsonParser = sh.get();
		
		JsonNode jnode = null;
		jnode = mapper.readValue(jsonParser,JsonNode.class);
		
		assertTrue("Page start in results and on page", jnode.get("start").asLong() == page.getStart());

		assertEquals("page count is  ",5,page.getTotalPages());
		assertTrue("Page has previous page ?",page.hasPreviousPage());
		assertEquals("page size", 25,page.getPageSize());
		assertEquals("document count", 102,page.getTotalSize());
		sh.close();
	}
}
