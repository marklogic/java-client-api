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

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.dom.DOMSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.SourceHandle;
import com.marklogic.client.io.StringHandle;
import javax.xml.transform.Source;

/*
 * This test is designed to to test simple bulk writes with different types of Managers and different content type like JSON,text,binary,XMl
 * 
 *  TextDocumentManager
 *  XMLDocumentManager
 *  BinaryDocumentManager
 *  JSONDocumentManager
 *  GenericDocumentManager
 */



public class TestBulkWriteSample1 extends BasicJavaClientREST  {


	private static String dbName = "TestBulkWriteSampleDB";
	private static String [] fNames = {"TestBulkWriteSampleDB-1"};
	
	
	private  DatabaseClient client ;
	@BeforeClass
	public static void setUp() throws Exception 
	{
		System.out.println("In setup");
		configureRESTServer(dbName, fNames);

		//To enable client side http logging

		//		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		//	   System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");   
		//       setupAppServicesConstraint(dbName);	  

	}

	@Before  public void testSetup() throws KeyManagementException, NoSuchAlgorithmException, Exception
	{
		// create new connection for each test below
		client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);
	}
	@After
	public  void testCleanUp() throws Exception
	{
		System.out.println("Running clear script");	
		// release client
		client.release();


	}
	/*
	 * This is cloned in github with tracking bug #27685 
	 * https://github.com/marklogic/java-client-api/issues/23
	 * 
	 * This test uses StringHandle to load 3 text documents, writes to database using bulk write set.
	 * Verified by reading individual documents
	 */
	@Test  
	public void testWriteMultipleTextDoc() 
	{

		String docId[] = {"/foo/test/myFoo1.txt","/foo/test/myFoo2.txt","/foo/test/myFoo3.txt"};

		TextDocumentManager docMgr = client.newTextDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();

		writeset.add(docId[0], new StringHandle().with("This is so foo1"));
		writeset.add(docId[1], new StringHandle().with("This is so foo2"));
		writeset.add(docId[2], new StringHandle().with("This is so foo3"));

		docMgr.write(writeset);

		assertEquals("Text document write difference", "This is so foo1", docMgr.read(docId[0], new StringHandle()).get());
		assertEquals("Text document write difference", "This is so foo2", docMgr.read(docId[1], new StringHandle()).get());
		assertEquals("Text document write difference", "This is so foo3", docMgr.read(docId[2], new StringHandle()).get());
		
		//Bulk delete on TextDocumentManager
		docMgr.delete(docId[0],docId[1],docId[2]);
	}
	
	/*
	 * This test uses DOMHandle to load 3 xml documents, writes to database using bulk write set.
	 * Verified by reading individual documents
	 */
	@Test  
	public void testWriteMultipleXMLDoc() throws KeyManagementException, NoSuchAlgorithmException, Exception  
	{


		String docId[] = {"/foo/test/Foo1.xml","/foo/test/Foo2.xml","/foo/test/Foo3.xml"};
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();

		writeset.add(docId[0], new DOMHandle(getDocumentContent("This is so foo1")));
		writeset.add(docId[1], new DOMHandle().with(getDocumentContent("This is so foo2")));
		writeset.add(docId[2], new DOMHandle().with(getDocumentContent("This is so foo3")));

		docMgr.write(writeset);

		DOMHandle dh = new DOMHandle();
		docMgr.read(docId[0], dh);  


		assertEquals("xml document write difference", "This is so foo1",dh.get().getChildNodes().item(0).getTextContent());
		docMgr.read(docId[1], dh);
		assertEquals("xml document write difference", "This is so foo2", dh.get().getChildNodes().item(0).getTextContent());
		docMgr.read(docId[2], dh);
		assertEquals("xml document write difference", "This is so foo3", dh.get().getChildNodes().item(0).getTextContent());
		
		//Bulk delete on XMLDocumentManager
		docMgr.delete(docId[0],docId[1],docId[2]);
	}
	/*
	 * This test uses FileHandle to load 3 binary documents with same URI, writes to database using bulk write set.
	 * Expecting an exception.
	 */
	@Test (expected = FailedRequestException.class) 
	public void testWriteMultipleSameBinaryDoc() throws KeyManagementException, NoSuchAlgorithmException, Exception  
	{
		String docId[] = {"Pandakarlino.jpg","mlfavicon.png"};

		BinaryDocumentManager docMgr = client.newBinaryDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		File file1= null;
		file1 = new File("src/test/java/com/marklogic/client/functionaltest/data/"  + docId[0]);
		FileHandle handle1 = new FileHandle(file1);
		writeset.add("/1/"+docId[0],handle1.withFormat(Format.BINARY));
		writeset.add("/1/"+docId[0],handle1.withFormat(Format.BINARY));

		docMgr.write(writeset);

	}
	/*
	 * This test uses FileHandle to load 3 binary documents, writes to database using bulk write set.
	 * Verified by reading individual documents
	 */
	@Test 
	public void testWriteMultipleBinaryDoc() throws KeyManagementException, NoSuchAlgorithmException, Exception  
	{
		String docId[] = {"Pandakarlino.jpg","mlfavicon.png"};

		BinaryDocumentManager docMgr = client.newBinaryDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		File file1= null,file2=null;
		file1 = new File("src/test/java/com/marklogic/client/functionaltest/data/" + docId[0]);
		FileHandle handle1 = new FileHandle(file1);
		writeset.add("/1/"+docId[0],handle1.withFormat(Format.BINARY));
		writeset.add("/2/"+docId[0],handle1.withFormat(Format.BINARY));
		file2 = new File("src/test/java/com/marklogic/client/functionaltest/data/"  + docId[1]);
		FileHandle handle2 = new FileHandle(file2);
		writeset.add("/1/"+docId[1],handle2.withFormat(Format.BINARY));
		writeset.add("/2/"+docId[1],handle2.withFormat(Format.BINARY));

		docMgr.write(writeset);
		long fsize1 = file1.length(),fsize2 = file2.length();


		FileHandle readHandle1 = new FileHandle();
		docMgr.read("/1/"+docId[0],readHandle1);

		FileHandle readHandle2 = new FileHandle();
		docMgr.read("/1/"+docId[1],readHandle2);
		System.out.println(file1.getName()+":"+fsize1+" "+readHandle1.get().getName()+":"+readHandle1.get().length());
		System.out.println(file2.getName()+":"+fsize2+" "+readHandle2.get().getName()+":"+readHandle2.get().length());
		assertEquals("Size of the  File 1"+docId[0],fsize1,readHandle1.get().length());
		assertEquals("Size of the  File 1"+docId[1],fsize2,readHandle2.get().length());
		
		//Bulk delete test - Git 284
		String doc0 = "/1/"+docId[0];
		String doc1 = "/2/"+docId[0];
		String doc2 = "/1/"+docId[1];
		String doc3 = "/2/"+docId[1];
		
		docMgr.delete(doc0,doc1,doc2,doc3);
	}

	/*
	 * This test uses ReaderHandle to load 3 JSON documents, writes to database using bulk write set.
	 * Verified by reading individual documents

	 */
	@Test
	public void testWriteMultipleJSONDocs() throws KeyManagementException, NoSuchAlgorithmException, Exception  
	{
		String docId[] = {"/a.json","/b.json","/c.json"};
		String json1 = new String("{\"animal\":\"dog\", \"says\":\"woof\"}");
		String json2 = new String("{\"animal\":\"cat\", \"says\":\"meow\"}");
		String json3 =  new String("{\"animal\":\"rat\", \"says\":\"keek\"}");
		Reader strReader = new StringReader(json1);
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		writeset.add(docId[0],new ReaderHandle(strReader).withFormat(Format.JSON));
		writeset.add(docId[1],new ReaderHandle(new StringReader(json2)));
		writeset.add(docId[2],new ReaderHandle(new StringReader(json3)));

		docMgr.write(writeset);

		ReaderHandle r1 = new ReaderHandle();
		docMgr.read(docId[0],r1);
		BufferedReader bfr =  new BufferedReader(r1.get());
		assertEquals(json1,bfr.readLine());
		docMgr.read(docId[1],r1);
		assertEquals("Json File Content"+docId[1],json2,new BufferedReader(r1.get()).readLine());
		docMgr.read(docId[2],r1);
		assertEquals("Json File Content"+docId[2],json3,new BufferedReader(r1.get()).readLine());
		bfr.close();
	}
	@Test
	public void testWriteMultipleJAXBDocs() throws KeyManagementException, NoSuchAlgorithmException, Exception  
	{
		String docId[] ={"/jaxb/iphone.xml","/jaxb/ipad.xml","/jaxb/ipod.xml"};
		Product product1 = new Product();
		product1.setName("iPhone");
		product1.setIndustry("Hardware");
		product1.setDescription("Very cool Iphone");
		Product product2 = new Product();
		product2.setName("iPad");
		product2.setIndustry("Hardware");
		product2.setDescription("Very cool Ipad");
		Product product3 = new Product();
		product3.setName("iPod");
		product3.setIndustry("Hardware");
		product3.setDescription("Very cool Ipod");
		JAXBContext context = JAXBContext.newInstance(Product.class);
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		//		 JAXBHandle contentHandle = new JAXBHandle(context);
		//		 contentHandle.set(product1);
		writeset.add(docId[0],new JAXBHandle<Product>(context).with(product1));
		writeset.add(docId[1],new JAXBHandle<Product>(context).with(product2));
		writeset.add(docId[2],new JAXBHandle<Product>(context).with(product3));

		docMgr.write(writeset);

		DOMHandle dh = new DOMHandle();
		docMgr.read(docId[0], dh);

		assertEquals("xml document write difference", "Very cool Iphone",dh.get().getChildNodes().item(0).getChildNodes().item(1).getTextContent());
		docMgr.read(docId[1], dh);
		assertEquals("xml document write difference", "Very cool Ipad", dh.get().getChildNodes().item(0).getChildNodes().item(1).getTextContent());
		docMgr.read(docId[2], dh);
		assertEquals("xml document write difference", "Very cool Ipod", dh.get().getChildNodes().item(0).getChildNodes().item(1).getTextContent());
	}
	/*
	 * This test uses GenericManager to load all different document types
	 * This test has a bug logged in github with tracking Issue#33
	 * 
	 */
	@Test 
	public void testWriteGenericDocMgr() throws KeyManagementException, NoSuchAlgorithmException, Exception  
	{
		String docId[] = {"Pandakarlino.jpg","mlfavicon.png"};

		GenericDocumentManager docMgr = client.newDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();

		File file1= null;
		file1 = new File("src/test/java/com/marklogic/client/functionaltest/data/"  + docId[0]);
		FileInputStream fis = new FileInputStream(file1);
		InputStreamHandle handle1 = new InputStreamHandle(fis);
		handle1.setFormat(Format.BINARY);

		writeset.add("/generic/"+docId[0],handle1);

		JacksonHandle jh = new JacksonHandle();
		ObjectMapper objM = new ObjectMapper();
		JsonNode jn = objM.readTree(new String("{\"animal\":\"dog\", \"says\":\"woof\"}"));
		jh.set(jn);
		jh.setFormat(Format.JSON);

		writeset.add("/generic/dog.json",jh);

		String foo1 = "This is foo1 of byte Array";
		byte[] ba = foo1.getBytes();
		BytesHandle bh = new BytesHandle(ba);
		bh.setFormat(Format.TEXT);

		writeset.add("/generic/foo1.txt",bh);

		Source ds = new DOMSource(getDocumentContent("This is so foo1"));
		SourceHandle sh = new SourceHandle();
		sh.set(ds);
		sh.setFormat(Format.XML);

		writeset.add("/generic/foo.xml",sh);

		docMgr.write(writeset);

		FileHandle rh = new FileHandle();

		docMgr.read("/generic/"+docId[0],rh);
		assertEquals("Size of the  File /generic/"+docId[0],file1.length(),rh.get().length());		 
		System.out.println(rh.get().getName()+":"+rh.get().length()+"\n");

		docMgr.read("/generic/foo.xml",rh);
		BufferedReader br=new BufferedReader(new FileReader(rh.get()));
		br.readLine();
		assertEquals("xml document write difference", "<foo>This is so foo1</foo>", br.readLine() );
		docMgr.read("/generic/foo1.txt",rh);
		br.close();
		br=new BufferedReader(new FileReader(rh.get()));
		assertEquals("txt document write difference", foo1, br.readLine() );
		br.close();
		docMgr.read("/generic/dog.json",rh);
		br=new BufferedReader(new FileReader(rh.get()));
		assertEquals("Json document write difference", "{\"animal\":\"dog\", \"says\":\"woof\"}", br.readLine() );
		br.close();
		fis.close();
		
		//Bulk delete on GenericDocumentManager - Git 284
		String doc1 = "/generic/"+docId[0];
		docMgr.delete(doc1,"/generic/foo.xml","/generic/foo1.txt", "/generic/dog.json");
		sh.close();
	}

	@Test
	public void testWriteMultipleJacksonPoJoDocs() throws KeyManagementException, NoSuchAlgorithmException, Exception  
	{
		String docId[] ={"/jack/iphone.json","/jack/ipad.json","/jack/ipod.json"};
		Product product1 = new Product();
		product1.setName("iPhone");
		product1.setIndustry("Hardware");
		product1.setDescription("Very cool Iphone");
		Product product2 = new Product();
		product2.setName("iPad");
		product2.setIndustry("Hardware");
		product2.setDescription("Very cool Ipad");
		Product product3 = new Product();
		product3.setName("iPod");
		product3.setIndustry("Hardware");
		product3.setDescription("Very cool Ipod");
		JacksonHandle writeHandle = new JacksonHandle();
		JsonNode writeDocument = writeHandle.getMapper().convertValue(product1, JsonNode.class);
		writeHandle.set(writeDocument);
		JsonNode writeDocument2 = writeHandle.getMapper().convertValue(product2, JsonNode.class);
		JsonNode writeDocument3 = writeHandle.getMapper().convertValue(product3, JsonNode.class);
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();

		writeset.add(docId[0],writeHandle);
		writeset.add(docId[1],new JacksonHandle().with(writeDocument2));
		writeset.add(docId[2],new JacksonHandle().with(writeDocument3));
		docMgr.write(writeset);

		JacksonHandle jh = new JacksonHandle();
		docMgr.read(docId[0], jh);		 		  
		String exp="{\"name\":\"iPhone\",\"industry\":\"Hardware\",\"description\":\"Very cool Iphone\"}";
		JSONAssert.assertEquals(exp,jh.get().toString() , false);

		docMgr.read(docId[1], jh);
		exp="{\"name\":\"iPad\",\"industry\":\"Hardware\",\"description\":\"Very cool Ipad\"}";
		JSONAssert.assertEquals(exp,jh.get().toString() , false);

		docMgr.read(docId[2], jh);
		exp="{\"name\":\"iPod\",\"industry\":\"Hardware\",\"description\":\"Very cool Ipod\"}";
		JSONAssert.assertEquals(exp,jh.get().toString() , false);
		
		//Test bulk delete on JSONDocumentManager
		docMgr.delete(docId[0],docId[1],docId[2]); 

	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void testJAXBDocsBulkDelete() throws KeyManagementException, NoSuchAlgorithmException, Exception  
	{
		String docId[] ={"/jaxb/iphone.xml","/jaxb/ipad.xml","/jaxb/ipod.xml"};
		Product product1 = new Product();
		product1.setName("iPhone");
		product1.setIndustry("Hardware");
		product1.setDescription("Very cool Iphone");
		Product product2 = new Product();
		product2.setName("iPad");
		product2.setIndustry("Hardware");
		product2.setDescription("Very cool Ipad");
		Product product3 = new Product();
		product3.setName("iPod");
		product3.setIndustry("Hardware");
		product3.setDescription("Very cool Ipod");
		JAXBContext context = JAXBContext.newInstance(Product.class);
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		
		writeset.add(docId[0],new JAXBHandle<Product>(context).with(product1));
		writeset.add(docId[1],new JAXBHandle<Product>(context).with(product2));
		writeset.add(docId[2],new JAXBHandle<Product>(context).with(product3));

		docMgr.write(writeset);

		DOMHandle dh = new DOMHandle();
		docMgr.read(docId[0], dh);

		assertEquals("xml document write difference", "Very cool Iphone",dh.get().getChildNodes().item(0).getChildNodes().item(1).getTextContent());
		docMgr.read(docId[1], dh);
		assertEquals("xml document write difference", "Very cool Ipad", dh.get().getChildNodes().item(0).getChildNodes().item(1).getTextContent());
		docMgr.read(docId[2], dh);
		assertEquals("xml document write difference", "Very cool Ipod", dh.get().getChildNodes().item(0).getChildNodes().item(1).getTextContent());
		
		//Bulk delete on XMLDocumentManager
		docMgr.delete(docId[0],docId[1],docId[2]);
				
		// Try reading back the deleted document. 
		DOMHandle rhDeleted = new DOMHandle();
		
		//Make sure that bulk delete indeed worked. Should throw ResourceNotFoundException.
		docMgr.read("/generic/"+docId[0],rhDeleted);		
	}
	@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down" );
		cleanupRESTServer(dbName, fNames);
	}
}