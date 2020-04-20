/*
 * Copyright (c) 2019 MarkLogic Corporation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;

/*
 * This test is designed to to test simple bulk reads with different types of Managers and different content type like JSON,text,binary,XMl by passing set of uris
 * 
 *  TextDocumentManager
 *  XMLDocumentManager
 *  BinaryDocumentManager
 *  JSONDocumentManager
 *  GenericDocumentManager
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBulkReadSample1 extends BasicJavaClientREST {

  private static final int BATCH_SIZE = 100;
  private static final String DIRECTORY = "/bulkread/";
  private static String dbName = "TestBulkReadSampleDB";
  private static String[] fNames = { "TestBulkReadSampleDB-1" };
  private static int ndocCount = 0;

  private static DatabaseClient client;

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    setupAppServicesConstraint(dbName);
    
	  if (isLBHost()) {
		  ndocCount = 5;
	  }
	  else {
		  ndocCount = 10; 
	  }
  }

  /*
   * 
   * Use StringHandle to load 102 text documents using bulk write set. Test Bulk
   * Read to see you can read all the documents?
   */
  @Test
  public void test1ReadMultipleTextDoc() throws Exception
  {
	  System.out.println("Inside test1ReadMultipleTextDoc");
    try {
		int count = 1;
		client = getDatabaseClient("rest-admin", "x", getConnType());
		
		TextDocumentManager docMgr = client.newTextDocumentManager();
		DocumentWriteSet writeset = docMgr.newWriteSet();

		for (int i = 0; i < 102; i++) {
		  writeset.add(DIRECTORY + "foo" + i + ".txt", new StringHandle().with("This is so foo" + i));
		  if (count % BATCH_SIZE == 0) {
		    docMgr.write(writeset);
		    writeset = docMgr.newWriteSet();
		  }
		  count++;
		}
		if (count % BATCH_SIZE > 0) {
		  docMgr.write(writeset);
		}
		waitForPropertyPropagate();
		String uris[] = new String[102];
		for (int i = 0; i < 102; i++) {
		  uris[i] = DIRECTORY + "foo" + i + ".txt";
		}
		count = 0;
		DocumentPage page = docMgr.read(uris);
		while (page.hasNext()) {
		  DocumentRecord rec = page.next();
		  validateRecord(rec, Format.TEXT);
		  count++;
		}
		System.out.println("Document count test1ReadMultipleTextDoc " + count);
		assertEquals("document count", 102, count);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    finally {
    	client.release();
    }
  }

  /*
   * This test uses DOMHandle to do bulk write 102 xml documents, and does a
   * bulk read from database. Verified by reading individual documents
   */
  @Test
  public void test2ReadMultipleXMLDoc() throws KeyManagementException, NoSuchAlgorithmException, Exception
  {
	  System.out.println("Inside test2ReadMultipleXMLDoc");
    try {
		int count = 1;
		client = getDatabaseClient("rest-admin", "x", getConnType());
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		Map<String, String> map = new HashMap<>();
		DocumentWriteSet writeset = docMgr.newWriteSet();
		for (int i = 0; i < 102; i++) {

		  writeset.add(DIRECTORY + "foo" + i + ".xml", new DOMHandle(getDocumentContent("This is so foo" + i)));
		  map.put(DIRECTORY + "foo" + i + ".xml", convertXMLDocumentToString(getDocumentContent("This is so foo" + i)));
		  if (count % BATCH_SIZE == 0) {
		    docMgr.write(writeset);
		    writeset = docMgr.newWriteSet();
		  }
		  count++;
		}
		if (count % BATCH_SIZE > 0) {
		  docMgr.write(writeset);
		}
		waitForPropertyPropagate();
		String uris[] = new String[102];
		for (int i = 0; i < 102; i++) {
		  uris[i] = DIRECTORY + "foo" + i + ".xml";
		}
		count = 0;
		DocumentPage page = docMgr.read(uris);
		DOMHandle dh = new DOMHandle();
		while (page.hasNext()) {
		  DocumentRecord rec = page.next();
		  validateRecord(rec, Format.XML);
		  rec.getContent(dh);
		  assertEquals("Comparing the content :", map.get(rec.getUri()), convertXMLDocumentToString(dh.get()));
		  count++;
		}
		System.out.println("Document count test2ReadMultipleXMLDoc " + count);
		assertEquals("document count", 102, count);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    finally {
    	client.release();
    }
  }

  /*
   * This test uses FileHandle to bulkload 102 binary documents,test bulk read
   * from database.
   */
  @Test
  public void test3ReadMultipleBinaryDoc() throws KeyManagementException, NoSuchAlgorithmException, Exception
  {
	  System.out.println("Inside test3ReadMultipleBinaryDoc");
	  
    try {
		String docId[] = { "Sega-4MB.jpg" };
		int count = 1;
		client = getDatabaseClient("rest-admin", "x", getConnType());
		BinaryDocumentManager docMgr = client.newBinaryDocumentManager();
		DocumentWriteSet writeset = docMgr.newWriteSet();
		File file1 = null;
		file1 = new File("src/test/java/com/marklogic/client/functionaltest/data/" + docId[0]);
		FileHandle h1 = new FileHandle(file1);
		for (int i = 0; i < ndocCount; i++) {
		  writeset.add(DIRECTORY + "binary" + i + ".jpg", h1);
		 // if (count % BATCH_SIZE == 0) {
		    docMgr.write(writeset);
		    writeset = docMgr.newWriteSet();
		 // }
		  count++;
		}
		/*if (count % BATCH_SIZE > 0) {
		  docMgr.write(writeset);
		}*/
		
		String uris[] = new String[ndocCount];
		for (int i = 0; i < ndocCount; i++) {
		  uris[i] = DIRECTORY + "binary" + i + ".jpg";
		}
		count = 0;
		FileHandle rh = new FileHandle();
		DocumentPage page = docMgr.read(uris);
		while (page.hasNext()) {
		  DocumentRecord rec = page.next();
		  validateRecord(rec, Format.BINARY);
		  rec.getContent(rh);
		  assertEquals("Content length :", file1.length(), rh.get().length());
		  count++;
		}
		assertEquals("document count", ndocCount, count);
		// Testing the multiple same uris will not read multiple records
		
		for (int i = 0; i < ndocCount; i++) {
		  uris[i] = DIRECTORY + "binary" + 1 + ".jpg";
		}
		count = 0;
		page = docMgr.read(uris);
		while (page.hasNext()) {
		  DocumentRecord rec = page.next();
		  validateRecord(rec, Format.BINARY);
		  count++;
		}
		System.out.println("Document count test3ReadMultipleBinaryDoc " + count);
		assertEquals("document count", 1, count);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    finally {
    	client.release();
    }
  }

  /*
   * Load 102 JSON documents using JacksonHandle, do a bulk read. Verify by
   * reading individual documents This test has a bug logged in github with
   * tracking Issue#33
   */
  @Test
  public void test4WriteMultipleJSONDocs() throws KeyManagementException, NoSuchAlgorithmException, Exception
  {
	  System.out.println("Inside test4WriteMultipleJSONDocs");
    try {
		int count = 1;
		client = getDatabaseClient("rest-admin", "x", getConnType());
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		DocumentWriteSet writeset = docMgr.newWriteSet();

		Map<String, String> map = new HashMap<>();

		for (int i = 0; i < 102; i++) {
		  JsonNode jn = new ObjectMapper().readTree("{\"animal" + i + "\":\"dog" + i + "\", \"says\":\"woof\"}");
		  JacksonHandle jh = new JacksonHandle();
		  jh.set(jn);
		  writeset.add(DIRECTORY + "dog" + i + ".json", jh);
		  map.put(DIRECTORY + "dog" + i + ".json", jn.toString());
		  if (count % BATCH_SIZE == 0) {
		    docMgr.write(writeset);
		    writeset = docMgr.newWriteSet();
		  }
		  count++;
		  // System.out.println(jn.toString());
		}
		if (count % BATCH_SIZE > 0) {
		  docMgr.write(writeset);
		}
		waitForPropertyPropagate();
		
		String uris[] = new String[103];
		for (int i = 0; i < 102; i++) {
		  uris[i] = DIRECTORY + "dog" + i + ".json";
		}
		uris[102] = "junkURL/test.json";
		count = 0;
		DocumentPage page = docMgr.read(uris);
		DocumentRecord rec;
		JacksonHandle jh = new JacksonHandle();
		while (page.hasNext()) {
		  rec = page.next();
		  validateRecord(rec, Format.JSON);
		  rec.getContent(jh);
		  assertEquals("Comparing the content :", map.get(rec.getUri()), jh.get().toString());
		  count++;
		}
		System.out.println("Document count test4WriteMultipleJSONDocs " + count);
		assertEquals("document count", 102, count);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    finally {
    	client.release();
    }
  }

  /*
   * This test uses GenericManager to load all different document types This
   * test has a bug logged in github with tracking Issue#33
   */
  @Test
  public void test5WriteGenericDocMgr() throws KeyManagementException, NoSuchAlgorithmException, Exception
  {
	  System.out.println("Inside test5WriteGenericDocMgr");
    try {
    	client = getDatabaseClient("rest-admin", "x", getConnType());
		GenericDocumentManager docMgr = client.newDocumentManager();
		int countXML = 0, countJson = 0, countJpg = 0, countTEXT = 0;
		String uris[] = new String[50];
		int count = 0;

		XMLDocumentManager xmldocMgr = client.newXMLDocumentManager();
		DocumentWriteSet writesetXML = xmldocMgr.newWriteSet();
		for (int i = 0; i < 10; i++) {
		writesetXML.add(DIRECTORY + "foo" + i + ".xml", new DOMHandle(getDocumentContent("This is so foo" + i)));
		uris[count++] =  DIRECTORY + "foo" + i + ".xml";
		} 
		xmldocMgr.write(writesetXML);

		TextDocumentManager txtdocMgr = client.newTextDocumentManager();
		DocumentWriteSet writesetTXT = txtdocMgr.newWriteSet();

		for (int i = 0; i < 10; i++) {
		writesetTXT.add(DIRECTORY + "foo" + i + ".txt", new StringHandle().with("This is so foo" + i));
		uris[count++] =  DIRECTORY + "foo" + i + ".txt";    
		}
		txtdocMgr.write(writesetTXT);

		BinaryDocumentManager bindocMgr = client.newBinaryDocumentManager();
		DocumentWriteSet writesetBIN = bindocMgr.newWriteSet();
		String docId[] = { "Sega-4MB.jpg" };

		File file1 = null;
				file1 = new File("src/test/java/com/marklogic/client/functionaltest/data/" + docId[0]);
		FileHandle h1 = new FileHandle(file1);
		for (int i = 0; i < 10; i++) {
		writesetBIN.add(DIRECTORY + "binary" + i + ".jpg", h1); 
		uris[count++] = DIRECTORY + "binary" + i + ".jpg";   
		}
		bindocMgr.write(writesetBIN);

		JSONDocumentManager jsondocMgr = client.newJSONDocumentManager();
		DocumentWriteSet writesetJSON = jsondocMgr.newWriteSet();

		for (int i = 0; i < 10; i++) {
		JsonNode jn = new ObjectMapper().readTree("{\"animal" + i + "\":\"dog" + i + "\", \"says\":\"woof\"}");
		JacksonHandle jh = new JacksonHandle();
		jh.set(jn);
		writesetJSON.add(DIRECTORY + "dog" + i + ".json", jh);
		uris[count++] = DIRECTORY + "dog" + i + ".json";
		}
		jsondocMgr.write(writesetJSON);
		
		// for(String uri:uris){System.out.println(uri);}
		DocumentPage page = docMgr.read(uris);

		while (page.hasNext()) {
		  DocumentRecord rec = page.next();
		  switch (rec.getFormat())
		  {
		    case XML:
		      countXML++;
		      break;
		    case TEXT:
		      countTEXT++;
		      break;
		    case JSON:
		      countJson++;
		      break;
		    case BINARY:
		      countJpg++;
		      break;
		    default:
		      break;
		  }
		  validateRecord(rec, rec.getFormat());
		}
		System.out.println("xml :" + countXML + "TXT :" + countTEXT + " json :" + countJpg + " " + countJson);
		System.out.println("Document countXML test5WriteGenericDocMgr " + countXML);
		System.out.println("Document countTEXT test5WriteGenericDocMgr " + countTEXT);
		System.out.println("Document countJpg test5WriteGenericDocMgr " + countJpg);
		System.out.println("Document countJson test5WriteGenericDocMgr " + countJson);
		assertEquals("xml document count", 10, countXML);
		assertEquals("text document count", 10, countTEXT);
		assertEquals("binary document count", isLBHost()?1:10, countJpg);
		assertEquals("Json document count", 10, countJson);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    finally {
    	client.release();
    }
  }

  // test for Issue# 107
  @Test
  public void test6CloseMethodforReadMultipleDoc() throws KeyManagementException, NoSuchAlgorithmException, Exception
  {
	  System.out.println("Inside test6CloseMethodforReadMultipleDoc");
    int count = 1;
    DocumentPage page;
    try {
		client = getDatabaseClient("rest-admin", "x", getConnType());
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		DocumentWriteSet writeset = docMgr.newWriteSet();

		Map<String, String> map = new HashMap<>();
		for (int j = 0; j < 102; j++) {
		  for (int i = 0; i < 10; i++) {
		    JsonNode jn = new ObjectMapper().readTree("{\"animal" + i + "\":\"dog" + i + "\", \"says\":\"woof\"}");
		    JacksonHandle jh = new JacksonHandle();
		    jh.set(jn);
		    writeset.add(DIRECTORY + j + "/cm-dog" + i + ".json", jh);
		    map.put(DIRECTORY + j + "/cm-dog" + i + ".json", jn.toString());
		  }
		  docMgr.write(writeset);
		  writeset = docMgr.newWriteSet();
		}
		waitForPropertyPropagate();

		String uris[] = new String[100];
		for (int j = 0; j < 102; j++) {
		  for (int i = 0; i < 10; i++) {
		    uris[i] = DIRECTORY + j + "/cm-dog" + i + ".json";
		  }
		  count = 0;
		  page = docMgr.read(uris);

		  DocumentRecord rec;
		  JacksonHandle jh = new JacksonHandle();
		  while (page.hasNext()) {
		    rec = page.next();
		    validateRecord(rec, Format.JSON);
		    rec.getContent(jh);
		    assertEquals("Comparing the content :", map.get(rec.getUri()), jh.get().toString());
		    count++;
		  }
		  page.close();		  
		}
		// validateRecord(rec,Format.JSON);
		System.out.println("Document count test6CloseMethodforReadMultipleDoc " + count);
		assertEquals("document count", 10, count);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    finally {
    	client.release();
    }
  }

  @AfterClass
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }

  public void validateRecord(DocumentRecord record, Format type) {
    assertNotNull("DocumentRecord should never be null", record);
    assertNotNull("Document uri should never be null", record.getUri());
    assertTrue("Document uri should start with " + DIRECTORY, record.getUri().startsWith(DIRECTORY));
    assertEquals("All records are expected to be in same format", type, record.getFormat());
  }
}