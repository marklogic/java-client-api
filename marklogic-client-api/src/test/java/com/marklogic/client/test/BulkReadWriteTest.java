/*
 * Copyright (c) 2023 MarkLogic Corporation
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
package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.*;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.io.*;
import com.marklogic.client.query.*;
import com.marklogic.client.query.QueryManager.QueryView;
import org.junit.jupiter.api.*;
import org.w3c.dom.Document;

import jakarta.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** Loads data from cities15000.txt which contains every city above 15000 people, and adds
 * data from countryInfo.txt.
 **/
@TestMethodOrder(MethodOrderer.MethodName.class)
public class BulkReadWriteTest {
  private static final int BATCH_SIZE = 100;
  static final String DIRECTORY = "/cities/";
  private static final String COUNTRIES_FILE = "countryInfo.txt";
  private static final String CITIES_FILE = "cities_above_300K.txt";
  static final int RECORDS_EXPECTED = 1363;

  @BeforeAll
  public static void beforeClass() throws JAXBException {
    Common.connect();
    DatabaseClientFactory.getHandleRegistry().register(
      JAXBHandle.newFactory(City.class)
    );
  }
  @AfterAll
  public static void afterClass() {
    cleanUp();
  }

  interface CityWriter {
    public void addCity(City city);
    public void finishBatch();
    public void setNumRecords(int numWritten);
  }

  private static class BulkCityWriter implements CityWriter {
    private XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
    private DocumentWriteSet writeSet = docMgr.newWriteSet();

    BulkCityWriter() throws JAXBException {
    }

    @Override
    public void addCity(City city) {
      writeSet.addAs( DIRECTORY + city.getGeoNameId() + ".xml", city );
    }

    @Override
    public void finishBatch() {
      docMgr.write(writeSet);
      writeSet = docMgr.newWriteSet();
    }

    @Override
    public void setNumRecords(int numWritten) {
      assertEquals( RECORDS_EXPECTED, numWritten);
    }
  }

  static void loadCities(CityWriter cityWriter) throws Exception {
    // load all the countries into a HashMap (this isn't the big data set)
    // we'll attach country info to each city (that's the big data set)
    Map<String, Country> countries = new HashMap<>();
    System.out.println("Reading countries:" + BulkReadWriteTest.class.getResource(COUNTRIES_FILE));
    String line;
    try (BufferedReader countryReader = new BufferedReader(Common.testFileToReader(COUNTRIES_FILE, "UTF-8"))) {
      while ((line = countryReader.readLine()) != null ) {
        addCountry(line, countries);
      }
    }

    // write batches of cities combined with their country info
    System.out.println("Reading cities:" + BulkReadWriteTest.class.getResource(CITIES_FILE));
    line = null;
    try (BufferedReader cityReader = new BufferedReader(Common.testFileToReader(CITIES_FILE, "UTF-8"))) {
      int numWritten = 0;
      while ((line = cityReader.readLine()) != null ) {

        // instantiate the POJO for this city
        City city = newCity(line, countries);
        // let the implementation handle writing the city
        cityWriter.addCity(city);

        // when we have a full batch, write it out
        if ( ++numWritten % BATCH_SIZE == 0 ) {
          cityWriter.finishBatch();
        }
      }
      // if there are any leftovers, let's write this last batch
      if ( numWritten % BATCH_SIZE > 0 ) {
        cityWriter.finishBatch();
      }
      cityWriter.setNumRecords(numWritten);
    }
  }


  @Disabled
  @Test
  public void testA_BulkLoad() throws IOException, Exception {
    loadCities(new BulkCityWriter());
  }

  @Disabled
  @Test
  public void testB_BulkRead() {
    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

    DocumentPage page = docMgr.read(DIRECTORY + "1016670.xml", DIRECTORY + "108410.xml", DIRECTORY + "1205733.xml");
    try {
      int numRead = 0;
      for ( DocumentRecord record : page ) {
        validateRecord(record);
        numRead++;
      }
      assertEquals( true, page.hasContent());
      assertEquals( 3, numRead);
      assertEquals( 3, page.size());
      assertEquals( false, page.hasPreviousPage());
      assertEquals( false, page.hasNextPage());
      assertEquals( true, page.isFirstPage());
      assertEquals( true, page.isLastPage());
      assertEquals( 1, page.getPageNumber());
      assertEquals( 3, page.getPageSize());
      assertEquals( 1, page.getStart());
      assertEquals( 1, page.getTotalPages());
      assertEquals( 3, page.getTotalSize());
    } finally {
      page.close();
    }

    // test reading a valid plus a non-existent document
    page = docMgr.read(DIRECTORY + "1016670.xml", "nonExistant.doc");
    try {
      assertEquals( true, page.hasContent());
      assertEquals( 1, page.size());
      assertEquals( DIRECTORY + "1016670.xml", page.next().getUri());
    } finally {
      page.close();
    }

    // test reading multiple non-existent documents
    boolean exceptionThrown = false;
    try {
      docMgr.read("nonExistant.doc", "nonExistant2.doc");
    } catch (ResourceNotFoundException e) {
      exceptionThrown = true;
    }
    assertFalse( exceptionThrown);

    // test reading a non-existent document (not actually a bulk operation)
    exceptionThrown = false;
    try {
      docMgr.read("nonExistant.doc", new StringHandle());
    } catch (ResourceNotFoundException e) {
      exceptionThrown = true;
    }
    assertTrue( exceptionThrown);
  }

  @Disabled
  @Test
  public void testC_BulkSearch() {
    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

    SearchHandle searchHandle = new SearchHandle();
    int pageLength = 100;
    docMgr.setPageLength(pageLength);
    DocumentPage page = docMgr.search(new StructuredQueryBuilder().directory(false, DIRECTORY), 1, searchHandle);
    try {
      for ( DocumentRecord record : page ) {
        validateRecord(record);
      }
      assertEquals( RECORDS_EXPECTED, page.getTotalSize());
      assertEquals( RECORDS_EXPECTED, searchHandle.getTotalResults());
      assertEquals( pageLength, searchHandle.getPageLength());
      assertEquals( true, page.hasContent());
      int expected = RECORDS_EXPECTED > pageLength ? pageLength : RECORDS_EXPECTED;
      assertEquals( expected, page.size());
      assertEquals( false, page.hasPreviousPage());
      assertEquals( RECORDS_EXPECTED > pageLength, page.hasNextPage());
      assertEquals( true, page.isFirstPage());
      assertEquals( page.hasNextPage() == false, page.isLastPage());
      assertEquals( 1, page.getPageNumber());
      assertEquals( pageLength, page.getPageSize());
      assertEquals( 1, page.getStart());
      double totalPagesExpected = Math.ceil((double) RECORDS_EXPECTED/(double) pageLength);
      assertEquals( totalPagesExpected, page.getTotalPages(), .01);
    } finally {
      page.close();
    }
  }

  @Test
  public void testD_JsonLoad() {
    JSONDocumentManager docMgr = Common.client.newJSONDocumentManager();

    StringHandle doc1 =
      new StringHandle("{\"animal\": \"dog\", \"says\": \"woof\"}").withFormat(Format.JSON);

    StringHandle doc2 =
      new StringHandle("{\"animal\": \"cat\", \"says\": \"meow\"}").withFormat(Format.JSON);

    StringHandle doc2Metadata =
      new StringHandle("{\"quality\" : 2}").withFormat(Format.JSON);

    DocumentWriteSet writeSet = docMgr.newWriteSet();
    writeSet.add("doc1.json", doc1);
    writeSet.add("doc2.json", doc2Metadata, doc2);

    docMgr.write(writeSet);

    docMgr.setMetadataCategories(Metadata.QUALITY);
    docMgr.setNonDocumentFormat(Format.JSON);
    DocumentPage documents = docMgr.read("doc1.json", "doc2.json");
    try {
      for ( DocumentRecord record : documents ) {
        JacksonHandle content = record.getContent(new JacksonHandle());
        JacksonHandle metadata = record.getMetadata(new JacksonHandle());
        if ( "doc1.json".equals(record.getUri()) ) {
          assertEquals( "dog", content.get().get("animal").textValue());
        } else if ( "doc2.json".equals(record.getUri()) ) {
          assertEquals( "cat", content.get().get("animal").textValue());
          assertEquals( 2, metadata.get().get("quality").intValue());
        }
      }
    } finally {
      documents.close();
    }
  }

  private void validateRecord(DocumentRecord record) {
    assertNotNull( record);
    assertNotNull( record.getUri());
    assertTrue(record.getUri().startsWith(DIRECTORY));
    assertEquals( Format.XML, record.getFormat());
    if ( record.getUri().equals(DIRECTORY + "1205733.xml") ) {
      City chittagong = record.getContentAs(City.class);
      validateChittagong(chittagong);
    }
  }

  public static void validateChittagong(City chittagong) {
    assertEquals( "Chittagong", chittagong.getName());
    assertEquals( 22.3384, chittagong.getLatitude(), 0);
    assertEquals( 91.83168, chittagong.getLongitude(), 0);
    assertEquals( 3920222, chittagong.getPopulation());
    assertEquals( 15, chittagong.getElevation());
    assertEquals( "BDT", chittagong.getCurrencyCode());
    assertEquals( "Taka", chittagong.getCurrencyName());
  }

  @Test
  public void testE_TextLoad() {
    String docId[] = {"/foo/test/myFoo1.txt","/foo/test/myFoo2.txt","/foo/test/myFoo3.txt"};
    TextDocumentManager docMgr = Common.client.newTextDocumentManager();
    DocumentWriteSet writeset =docMgr.newWriteSet();

    writeset.add(docId[0], new StringHandle().with("This is so foo1"));
    writeset.add(docId[1], new StringHandle().with("This is so foo2"));
    writeset.add(docId[2], new StringHandle().with("This is so foo3"));
    docMgr.write(writeset);

    assertEquals( "This is so foo1", docMgr.read(docId[0], new StringHandle()).get());
    assertEquals( "This is so foo2", docMgr.read(docId[1], new StringHandle()).get());
    assertEquals( "This is so foo3", docMgr.read(docId[2], new StringHandle()).get());

    docMgr.delete(docId[0]);
    docMgr.delete(docId[1]);
    docMgr.delete(docId[2]);
  }

  @Test
  public void testF_TextLoadWithTransform() throws IOException {
    DatabaseClient restAdminClient = Common.connectRestAdmin();
    restAdminClient.newServerConfigManager().newTransformExtensionsManager().writeXQueryTransformAs(
      TransformExtensionsTest.XQUERY_NAME,
      TransformExtensionsTest.makeXQueryMetadata(),
      Common.testFileToString(TransformExtensionsTest.XQUERY_FILE)
    );

    String docId[] = {"/foo/test/myFoo1.xml","/foo/test/myFoo2.xml","/foo/test/myFoo3.xml"};
    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
    ServerTransform transform = new ServerTransform(TransformExtensionsTest.XQUERY_NAME)
      .addParameter("value", "true");
    docMgr.setWriteTransform(transform);
    DocumentWriteSet writeset =docMgr.newWriteSet();

    writeset.add(docId[0], new StringHandle().with("<xml><a/></xml>").withFormat(Format.XML));
    writeset.add(docId[1], new StringHandle().with("this is so foo").withFormat(Format.TEXT));
    docMgr.write(writeset);

    // clear out the write transform
    docMgr.setWriteTransform(null);
    docMgr.write(docId[2], new StringHandle().with("<xml><a/></xml>").withFormat(Format.XML));

    String TEST_NS = "http://marklogic.com/rest-api/test/transform";
    // validate that the write tranform worked
    Document doc0 = docMgr.read(docId[0], new DOMHandle()).get();
	  assertEquals("true",doc0.getDocumentElement().getAttributeNS(TEST_NS, "transformed"));

	  // validate that the write transform didn't touch the text file
	  assertEquals("this is so foo", docMgr.read(docId[1], new StringHandle()).get());

	  // validate that the read transform worked
	  // without the read transform, there is no "transformed" attribute
	  Document doc2 = docMgr.read(docId[2], new DOMHandle()).get();
	  assertEquals("", doc2.getDocumentElement().getAttributeNS(TEST_NS, "transformed"));

	  // with the read transform, this should now have the "transformed" attribute
	  doc2 = docMgr.read(docId[2], new DOMHandle(), transform).get();
	  assertEquals("true",doc2.getDocumentElement().getAttributeNS(TEST_NS, "transformed"));

	  // reading with bulk API, but no read transform there is no "transformed" attribute
	  doc2 = docMgr.read(docId[2]).next().getContent(new DOMHandle()).get();
	  assertEquals("", doc2.getDocumentElement().getAttributeNS(TEST_NS, "transformed"));

	  // reading with bulk API, the transform should work the same
	  docMgr.setReadTransform(transform);
	  doc2 = docMgr.read(docId[2]).next().getContent(new DOMHandle()).get();
	  assertEquals("true",doc2.getDocumentElement().getAttributeNS(TEST_NS, "transformed"));

	  // searching with bulk API and DocumentManager.setReadTransform,
	  // the transform should work the same on the matching documents
	  docMgr.setReadTransform(transform);
	  QueryDefinition query = new StructuredQueryBuilder().document(docId[2]);
	  try ( DocumentPage page = docMgr.search(query, 1) ) {
		  doc2 = page.next().getContent(new DOMHandle()).get();
		  assertEquals("true",doc2.getDocumentElement().getAttributeNS(TEST_NS, "transformed"));
	  }

	  // searching with bulk API and both DocumentManager.setReadTransform
	  // and QueryDefinition.setResponseTransform,
	  // the transform should work the same on the matching documents
	  docMgr.setReadTransform(transform);
	  query.setResponseTransform(transform);
	  try ( DocumentPage page = docMgr.search(query, 1) ) {
		  doc2 = page.next().getContent(new DOMHandle()).get();
		  assertEquals("true",doc2.getDocumentElement().getAttributeNS(TEST_NS, "transformed"));
	  }

	  // searching with bulk API and DocumentManager.setReadTransform,
	  // the transform should work the same on the matching documents
	  docMgr.setReadTransform(null);
	  query.setResponseTransform(transform);
	  try ( DocumentPage page = docMgr.search(query, 1) ) {
		  doc2 = page.next().getContent(new DOMHandle()).get();
		  assertEquals("true",doc2.getDocumentElement().getAttributeNS(TEST_NS, "transformed"));
	  }

	  // searching with bulk API, the transform should work the same on the matching documents
	  // and the search response
	  query.setResponseTransform(transform);
	  DOMHandle results = new DOMHandle();
	  try ( DocumentPage page = docMgr.search(query, 1, results) ) {
		  doc2 = page.next().getContent(new DOMHandle()).get();
		  assertEquals("true",doc2.getDocumentElement().getAttributeNS(TEST_NS, "transformed"));
		  assertEquals("true",results.get().getDocumentElement().getAttributeNS(TEST_NS, "transformed"));
	  }

    docMgr.delete(docId[0]);
    docMgr.delete(docId[1]);
    docMgr.delete(docId[2]);
  }

  @Test
  public void testG_DefaultMetadata() throws Throwable {
    // Synthesize input content
    StringHandle doc1 = new StringHandle(
      "{\"number\": 1}").withFormat(Format.JSON);
    StringHandle doc2 = new StringHandle(
      "{\"number\": 2}").withFormat(Format.JSON);
    StringHandle doc3 = new StringHandle(
      "{\"number\": 3}").withFormat(Format.JSON);
    StringHandle doc4 = new StringHandle(
      "{\"number\": 4}").withFormat(Format.JSON);
    StringHandle doc5 = new StringHandle(
      "{\"number\": 5}").withFormat(Format.JSON);
    StringHandle doc6 = new StringHandle(
      "{\"number\": 6}").withFormat(Format.JSON);
    StringHandle doc7 = new StringHandle(
      "{\"number\": 7}").withFormat(Format.JSON);
    StringHandle doc8 = new StringHandle(
      "{\"number\": 8}").withFormat(Format.JSON);

    // Synthesize input metadata
    DocumentMetadataHandle defaultMetadata1 =
      new DocumentMetadataHandle().withQuality(1);
    DocumentMetadataHandle defaultMetadata2 =
      new DocumentMetadataHandle().withQuality(2);
    DocumentMetadataHandle docSpecificMetadata =
      new DocumentMetadataHandle().withCollections("myCollection");

    // Create and build up the batch
    JSONDocumentManager jdm = Common.client.newJSONDocumentManager();
    DocumentWriteSet batch = jdm.newWriteSet();

    // use system default metadata
    batch.add("doc1.json", doc1);       // system default metadata

    // using batch default metadata
    batch.addDefault(defaultMetadata1);
    batch.add("doc2.json", doc2);       // batch default metadata
    batch.add("doc3.json", docSpecificMetadata, doc3);
    batch.add("doc4.json", doc4);       // batch default metadata

    // replace batch default metadata with new metadata
    batch.addDefault(defaultMetadata2);
    batch.add("doc5.json", doc5);       // batch default

    // replace default metadata with blank metadata (back to system defaults)
    batch.disableDefault();
    batch.add("doc6.json", doc6);       // system default metadata
    batch.addDefault(defaultMetadata1);
    batch.add("doc7.json", doc7);       // batch default metadata
    batch.disableDefault();
    batch.add("doc8.json", doc8);       // system default metadata

    // Execute the write operation
    jdm.write(batch);

    // Check the results
    assertEquals( 0,
      jdm.readMetadata("doc1.json", new DocumentMetadataHandle()).getQuality());
    assertEquals( defaultMetadata1.getQuality(),
      jdm.readMetadata("doc2.json", new DocumentMetadataHandle()).getQuality());

    DocumentMetadataHandle doc3Metadata =
      jdm.readMetadata("doc3.json", new DocumentMetadataHandle());
    assertEquals(0, doc3Metadata.getQuality(),
		"Doc3 should have the system default document quality (0) because quality " +
			"was not included in the document-specific metadata.");
    Set collections = doc3Metadata.getCollections();
    assertEquals( 1, collections.size());
    assertEquals("myCollection", collections.iterator().next());

    // let's check getting content with just quality in the metadata
    jdm.setMetadataCategories(Metadata.QUALITY);
    DocumentPage documents = jdm.read("doc4.json", "doc5.json");
    try {
      for ( DocumentRecord doc: documents ) {
        DocumentMetadataHandle metadata = doc.getMetadata(new DocumentMetadataHandle());
        StringHandle content = doc.getContent(new StringHandle());
        if ( "doc4.json".equals(doc.getUri()) ) {
          assertEquals( 1,
            metadata.getQuality());
          assertTrue( content.get().matches("\\{\"number\": ?4\\}"));
        } else if ( "doc5.json".equals(doc.getUri()) ) {
          assertEquals( 2,
            metadata.getQuality());
          assertTrue( content.get().matches("\\{\"number\": ?5\\}"));
        }
      }
    } finally {
      documents.close();
    }

    // now try with just metadata
    documents = jdm.readMetadata("doc6.json", "doc7.json", "doc8.json");
    try {
      for ( DocumentRecord doc: documents ) {
        DocumentMetadataHandle metadata = doc.getMetadata(new DocumentMetadataHandle());
        if ( "doc6.json".equals(doc.getUri()) ) {
          assertEquals( 0,
            metadata.getQuality());
        } else if ( "doc7.json".equals(doc.getUri()) ) {
          assertEquals( 1,
            metadata.getQuality());
        } else if ( "doc8.json".equals(doc.getUri()) ) {
          assertEquals( 0,
            metadata.getQuality());
        }
      }
    } finally {
      documents.close();
    }
    String[] uris = new String[] { "doc1.json", "doc2.json", "doc3.json", "doc4.json", "doc5.json",
      "doc6.json", "doc7.json", "doc8.json"};
    Transaction t1 = Common.client.openTransaction();
    try {
      // delete from within the transaction
      jdm.delete(t1, uris);
      // read from outside any transaction (the docs are still there)
      documents = jdm.read(uris);
      documents.close();
      assertEquals(8, documents.size());
      // read from inside the transaction (the docs are gone)
      documents = jdm.read(t1, uris);
      documents.close();
      assertEquals(0, documents.size());
    } finally {
      t1.commit();
    }
  }

  @Test
  public void test_78() {
    String DIRECTORY ="/test_78/";
    int BATCH_SIZE=10;
    int count =1;
    TextDocumentManager docMgr = Common.client.newTextDocumentManager();
    DocumentWriteSet writeset =docMgr.newWriteSet();
    for(int i =0;i<11;i++){
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
    //using QueryManger for query definition and set the search criteria
    QueryManager queryMgr = Common.client.newQueryManager();
    try {
      StringQueryDefinition qd = queryMgr.newStringDefinition();
      qd.setCriteria("bar");
      qd.setDirectory(DIRECTORY);
      // set  document manager level settings for search response
      System.out.println("Default Page length setting on docMgr :"+docMgr.getPageLength());
      docMgr.setPageLength(1);
      docMgr.setSearchView(QueryView.RESULTS);
      docMgr.setNonDocumentFormat(Format.XML);
      assertEquals("XML",docMgr.getNonDocumentFormat().toString());
      assertEquals("RESULTS" ,docMgr.getSearchView().toString());
      assertEquals(1,docMgr.getPageLength());
      // Search for documents where content has bar and get first result record, get search handle on it
      SearchHandle sh = new SearchHandle();
      DocumentPage page= docMgr.search(qd, 1);
      try {
        // test for page methods
        assertEquals(1,page.size());
        assertEquals(1,page.getStart());
        assertEquals(11,page.getTotalSize());
        assertEquals(11,page.getTotalPages());
        assertTrue(page.isFirstPage());
        assertFalse(page.isLastPage());
        assertTrue(page.hasContent());
        assertFalse(page.hasPreviousPage());
      } finally {
        page.close();
      }

      long start=1;
      do{
        count=0;
        page = docMgr.search(qd, start,sh);
        try {
          if(start >1){
            assertFalse( page.isFirstPage());
            assertTrue(page.hasPreviousPage());
          }
          while(page.hasNext()){
            page.next();
            count++;
          }
          MatchDocumentSummary[] mds= sh.getMatchResults();
          assertEquals(1,mds.length);
          //since we set the query view to get only results, facet count supposed be 0
          assertEquals(0,sh.getFacetNames().length);

          assertEquals( page.size(),count);
          if (page.isLastPage()) {
            assertEquals(start, page.getTotalPages());
            assertTrue(page.hasPreviousPage());
            assertEquals( 1,page.getPageSize());
            assertEquals( 11,page.getTotalSize());
          } else {
            start = start + page.getPageSize();
          }
        } finally {
          page.close();
        }
      }while(!page.isLastPage());
      page= docMgr.search(qd, 12);
      try {
        assertFalse(page.hasContent());
      } finally {
        page.close();
      }
    } finally {
      DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
      deleteQuery.setDirectory(DIRECTORY);
      queryMgr.delete(deleteQuery);
    }
  }

  @Test
  public void test_171() throws Exception{
    DatabaseClient client = Common.newEvalClient("Documents");
    int count=1;
    boolean tstatus =true;
    String directory = "/test_bulk_171/";
    Transaction t1 = client.openTransaction();
    try{
      QueryManager queryMgr = client.newQueryManager();
      DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
      deleteQuery.setDirectory(directory);
      queryMgr.delete(deleteQuery);

      XMLDocumentManager docMgr = client.newXMLDocumentManager();
      Map<String,String> map= new HashMap<>();
      DocumentWriteSet writeset =docMgr.newWriteSet();
      for(int i =0;i<2;i++) {
        String contents = "<xml>test" + i + "</xml>";
        String docId = directory + "sec"+i+".xml";
        writeset.add(docId, new StringHandle(contents).withFormat(Format.XML));
        map.put(docId, contents);
        if(count%100 == 0){
          docMgr.write(writeset,t1);
          writeset = docMgr.newWriteSet();
        }
        count++;
      }
      if(count%100 > 0){
        docMgr.write(writeset,t1);
      }

      QueryDefinition directoryQuery = queryMgr.newStringDefinition();
      directoryQuery.setDirectory(directory);
      SearchHandle outOfTransactionResults = queryMgr.search(directoryQuery, new SearchHandle());

      SearchHandle inTransactionResults    = queryMgr.search(directoryQuery, new SearchHandle(), t1);

      assertEquals(0,outOfTransactionResults.getTotalResults());
      assertEquals( 2,   inTransactionResults.getTotalResults());

      long start = 2;
      SearchHandle page2 = queryMgr.search(directoryQuery, new SearchHandle(), start, t1);
      assertEquals( 1, page2.getMatchResults().length);

    }catch(Exception e){
      System.out.println(e.getMessage());
      tstatus=true;
      throw e;
    }finally{
      if(tstatus){
        t1.rollback();
      }
      client.release();
    }
  }

  @Test
  public void test_218() throws Exception{
    int count=1;
    boolean committed = false;
    String directory = "/test_bulk_218/";
    DatabaseClient client2 = Common.newEvalClient("Documents");
    Transaction t1 = client2.openTransaction();
    QueryManager queryMgr2 = client2.newQueryManager();
    QueryManager queryMgr1 = Common.client.newQueryManager();

    try{
      XMLDocumentManager docMgr = client2.newXMLDocumentManager();
      DocumentWriteSet writeset =docMgr.newWriteSet();
      for(int i =0;i<12;i++){
        String contents = "<xml>test" + i + "</xml>";
        String docId = directory + "sec"+i+".xml";
        writeset.add(docId, new StringHandle(contents).withFormat(Format.XML));
        if(count%10 == 0){
          docMgr.write(writeset,t1);
          writeset = docMgr.newWriteSet();
        }
        count++;
      }
      if(count%10 > 0){
        docMgr.write(writeset,t1);
      }
      t1.commit();
      committed=true;

      QueryDefinition directoryQuery = queryMgr2.newStringDefinition();
      directoryQuery.setDirectory(directory);

      QueryDefinition directoryQuery1 = queryMgr1.newStringDefinition();
      directoryQuery1.setDirectory(directory);

      SearchHandle inRuntimeDbResults = queryMgr2.search(directoryQuery, new SearchHandle());
      SearchHandle inDefaultDbResults = queryMgr1.search(directoryQuery1, new SearchHandle());

      assertEquals( 12, inRuntimeDbResults.getTotalResults());
      assertEquals( 0,  inDefaultDbResults.getTotalResults());
    }catch(Exception e){
      System.out.println(e.getMessage());
      if(! committed){
        t1.rollback();
      }
      throw e;
    } finally {

      DeleteQueryDefinition deleteQuery = queryMgr2.newDeleteDefinition();
      deleteQuery.setDirectory(directory);
      queryMgr2.delete(deleteQuery);

      client2.release();
    }
  }

  @Test
  public void test_issue_623() {
    String uniqueDir = "BulkReadWriteTest_" + new Random().nextInt(10000) + "/";
    List<String> uris = new ArrayList<>();
    uris.add(uniqueDir + "test_with_ampersand.txt?a=b&c=d");
    uris.add(uniqueDir + "test+with+plus.txt");
    uris.add(uniqueDir + "test/with/forwardslash.txt");
    uris.add(uniqueDir + "test.with.dot.txt");
    uris.add(uniqueDir + "test_with!every@thing#else$*()-_[]:',~.txt");
    uris.add(uniqueDir + "test_with;.txt");

    test_issue_623_body( Common.client.newTextDocumentManager(), uris, "$0" );
    test_issue_623_body( Common.client.newBinaryDocumentManager(), uris, "$0" );
    test_issue_623_body( Common.client.newXMLDocumentManager(), uris,
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xml>$0</xml>" );
    test_issue_623_body( Common.client.newJSONDocumentManager(), uris, "[\"$0\"]" );
  }

  private void test_issue_623_body(DocumentManager docMgr, List<String> uris, String regex) {
    // test with 0 args
    boolean writeSuccess = false;
    try { docMgr.write(null); } catch (IllegalArgumentException e) { writeSuccess = true; }
    assertTrue(writeSuccess);

    boolean readSuccess = false;
    try { docMgr.read(new String[0]); } catch (IllegalArgumentException e) { readSuccess = true; }
    assertTrue(readSuccess);

    boolean deleteSuccess = false;
    try { docMgr.delete(new String[0]); } catch (IllegalArgumentException e) { deleteSuccess = true; }
    assertTrue(deleteSuccess);

    for ( String uri : uris ) {
      String contents = URLEncoder.encode(uri).replaceFirst(".*", regex);

      // test with 1 arg
      docMgr.write(uri, new StringHandle(contents));
      assertEquals(contents, docMgr.read(uri).nextContent(new StringHandle()).get());
      docMgr.delete(uri);
      verifyDeleted(docMgr, uri);

      // test with writeSet
      DocumentWriteSet writeSet = docMgr.newWriteSet();
      writeSet.add(uri, new StringHandle(contents));
      docMgr.write(writeSet);
      DocumentPage docs = docMgr.read(new String[] {uri});
      assertEquals(1, docs.size());
      DocumentRecord doc = docs.next();
      assertEquals(uri, doc.getUri());
      assertEquals(contents, doc.getContent(new StringHandle()).get());
      docMgr.delete(new String[] {uri});
      verifyDeleted(docMgr, uri);
    }
  }

  @Test
  // https://github.com/marklogic/java-client-api/issues/759
  public void test_issue_759() throws Exception {
    DocumentManager docMgr = Common.client.newDocumentManager();
    String[] uris = new String[150];
    for ( int i=0; i < 102; i++ ) {
      String mapDocId = "/" + Integer.toString(i);
      uris[i] = mapDocId;
    }
    docMgr.read(uris);
  }

  private void verifyDeleted(DocumentManager docMgr, String uri) {
    try {
      docMgr.read(uri, new StringHandle());
    } catch(ResourceNotFoundException e) {
      // success!
      return;
    }
    fail("Read of document with uri=[" + uri + "] should have thrown ResourceNotFoundException");
  }

  private static void addCountry(String line, Map<String, Country> countries) {
    // skip comment lines
    if ( line.startsWith("#") ) return;

    // otherwise split on tabs and populate a country object
    String[] fields = line.split("	");
    String isoCode = fields[0];
    countries.put(isoCode, new Country()
      .setIsoCode( isoCode )
      .setName( fields[4] )
      .setContinent( fields[8] )
      .setCurrencyCode( fields[10] )
      .setCurrencyName( fields[11] )
    );
  }

  public static Country getCountry(String isoCode, Map<String, Country> countries) {
    return countries.get(isoCode);
  }

  public static City newCity(String line, Map<String, Country> countries) {
    String[] fields = line.split("	");
    try {
      City city = new City()
        .setGeoNameId( Integer.parseInt(fields[0]) )
        .setName( fields[1] )
        .setAsciiName( fields[2] )
        .setAlternateNames( fields[3].split(",") );
      if ( !fields[4].equals("") ) city.setLatitude( Double.parseDouble(fields[4]) );
      if ( !fields[5].equals("") ) city.setLongitude( Double.parseDouble(fields[5]) );
      if ( !fields[4].equals("") && !fields[5].equals("") ) {
        city.setLatLong( fields[4] + " " + fields[5] );
      }
      if ( !fields[14].equals("") ) city.setPopulation( Long.parseLong(fields[14]) );
      if ( !fields[16].equals("") ) city.setElevation( Integer.parseInt(fields[16]) );
      if ( !fields[8].equals("") ) {
        String isoCode = fields[8];
        Country country = getCountry(isoCode, countries);
        city.setCountryIsoCode(isoCode);
        city.setCountryName( country.getName()) ;
        city.setContinent( country.getContinent() );
        city.setCurrencyCode( country.getCurrencyCode() );
        city.setCurrencyName( country.getCurrencyName() );
      }
      return city;
    } catch (Throwable e) {
      System.err.println("Error parsing line:[" + line + "]");
      throw new IllegalStateException(e);
    }
  }

  public static void cleanUp() {
    QueryManager queryMgr = Common.client.newQueryManager();
    DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
    deleteQuery.setDirectory("/cities/");
    queryMgr.delete(deleteQuery);
  }
}
