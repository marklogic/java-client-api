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

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.Transaction;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.SourceHandle;
import java.util.Map;

public class TestBulkWriteWithTransformations extends BasicJavaClientREST{
	private static final int BATCH_SIZE=100;
	private static final String DIRECTORY ="/bulkTransform/";
	private static String dbName = "TestBulkWriteWithTransformDB";
	private static String [] fNames = {"TestBulkWriteWithTransformDB-1"};
	
	
	private  DatabaseClient client ;
	// Additional port to test for Uber port
    private static int uberPort = 8000;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("In setup");
		configureRESTServer(dbName, fNames);
		setupAppServicesConstraint(dbName);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down" );
		cleanupRESTServer(dbName, fNames);
		deleteRESTUser("eval-user");
		deleteUserRole("test-eval");
	}

	@Before
	public void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception {
//		 System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		// create new connection for each test below
		createUserRolesWithPrevilages("test-eval","xdbc:eval", "xdbc:eval-in","xdmp:eval-in","any-uri","xdbc:invoke");
	    createRESTUser("eval-user", "x", "test-eval","rest-admin","rest-writer","rest-reader");
		client = DatabaseClientFactory.newClient("localhost", uberPort, dbName, "eval-user", "x", Authentication.DIGEST);
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("Running clear script");	
		// release client
		client.release();
	}

	@Test
	public void testBulkLoadWithXSLTClientSideTransform() throws KeyManagementException, NoSuchAlgorithmException, Exception {
		String docId[] ={"/transform/emp.xml","/transform/food1.xml","/transform/food2.xml"};
		Source s[] = new Source[3];
		s[0] = new StreamSource("src/test/java/com/marklogic/client/functionaltest/data/employee.xml");
		s[1] = new StreamSource("src/test/java/com/marklogic/client/functionaltest/data/xml-original.xml");
		s[2] = new StreamSource("src/test/java/com/marklogic/client/functionaltest/data/xml-original-test.xml");
		// get the xslt
		Source xsl = new StreamSource("src/test/java/com/marklogic/client/functionaltest/data/employee-stylesheet.xsl");

		// create transformer
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(xsl);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		for(int i=0;i<3;i++){
			SourceHandle handle = new SourceHandle();
			handle.set(s[i]);
			// set the transformer
			handle.setTransformer(transformer);
			writeset.add(docId[i],handle);
			//Close handle.
			handle.close();
		}
		docMgr.write(writeset);
		FileHandle dh = new FileHandle();
		//		 DOMHandle dh = new DOMHandle();
		docMgr.read(docId[0], dh);
		Scanner scanner = new Scanner(dh.get()).useDelimiter("\\Z");
		String readContent = scanner.next();
		assertTrue("xml document contains firstname", readContent.contains("firstname"));
		docMgr.read(docId[1], dh);
		Scanner sc1 = new Scanner(dh.get()).useDelimiter("\\Z");
		readContent = sc1.next();
		assertTrue("xml document contains firstname", readContent.contains("firstname"));
		docMgr.read(docId[2], dh);
		Scanner sc2 = new Scanner(dh.get()).useDelimiter("\\Z");
		readContent = sc2.next();
		assertTrue("xml document contains firstname", readContent.contains("firstname"));

	}
	@Test
	public void testBulkLoadWithXQueryTransform() throws KeyManagementException, NoSuchAlgorithmException, Exception {

		TransformExtensionsManager transMgr = 
				client.newServerConfigManager().newTransformExtensionsManager();
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Adding attribute xquery Transform");
		metadata.setDescription("This plugin transforms an XML document by adding attribute to root node");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");
		// get the transform file
		File transformFile = new File("src/test/java/com/marklogic/client/functionaltest/transforms/add-attr-xquery-transform.xqy");
		FileHandle transformHandle = new FileHandle(transformFile);
		transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle, metadata);
		ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "English");
		int count=1;
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		Map<String,String> map= new HashMap<>();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		for(int i =0;i<102;i++){

			writeset.add(DIRECTORY+"foo"+i+".xml", new DOMHandle(getDocumentContent("This is so foo"+i)));
			map.put(DIRECTORY+"foo"+i+".xml", convertXMLDocumentToString(getDocumentContent("This is so foo"+i)));
			if(count%BATCH_SIZE == 0){
				docMgr.write(writeset,transform);
				writeset = docMgr.newWriteSet();
			}
			count++;
		}
		if(count%BATCH_SIZE > 0){
			docMgr.write(writeset,transform);
		}
		
		String uris[] = new String[102];
		for(int i =0;i<102;i++){
			uris[i]=DIRECTORY+"foo"+i+".xml";
		}
		count=0;
		DocumentPage page = docMgr.read(uris);
		DOMHandle dh = new DOMHandle();
		while(page.hasNext()){
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			assertTrue("Element has attribure ? :",dh.get().getElementsByTagName("foo").item(0).hasAttributes());
			count++;
		}

		assertEquals("document count", 102,count); 

	}
	
	/* This test is similar to testBulkLoadWithXQueryTransform and is used to validate Git Issue 396.
	 * 
	 * Verify that a ServerTransform object is passed along when in transactions.
	 */
	
	@Test
	public void testBulkXQYTransformWithTrans() throws KeyManagementException, NoSuchAlgorithmException, Exception {

		TransformExtensionsManager transMgr = 
				client.newServerConfigManager().newTransformExtensionsManager();
		Transaction tRollback = client.openTransaction();
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Adding attribute xquery Transform");
		metadata.setDescription("This plugin transforms an XML document by adding attribute to root node");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");
		// get the transform file
		File transformFile = new File("src/test/java/com/marklogic/client/functionaltest/transforms/add-attr-xquery-transform.xqy");
		FileHandle transformHandle = new FileHandle(transformFile);
		transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle, metadata);
		ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "testBulkXQYTransformWithTrans");
		int count=1;
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		Map<String,String> map= new HashMap<>();
		DocumentWriteSet writesetRollback = docMgr.newWriteSet();
		// Verify rollback with a smaller number of documents.
		for(int i = 0;i<12;i++){

			writesetRollback.add(DIRECTORY+"fooWithTrans"+i+".xml", new DOMHandle(getDocumentContent("This is so foo"+i)));
			map.put(DIRECTORY+"fooWithTrans"+i+".xml", convertXMLDocumentToString(getDocumentContent("This is so foo"+i)));
			if(count%10 == 0){
				docMgr.write(writesetRollback, transform, tRollback);
				writesetRollback = docMgr.newWriteSet();
			}
			count++;
		}
		if(count%10 > 0){
			docMgr.write(writesetRollback, transform, tRollback);
		}
		String uris[] = new String[102];
		for(int i =0;i<102;i++){
			uris[i]=DIRECTORY+"fooWithTrans"+i+".xml";
		}
				
		try {
			// Verify rollback on DocumentManager write method with transform. 
			tRollback.rollback();			
			DocumentPage pageRollback = docMgr.read(uris);
			assertEquals("Document count is not zero. Transaction did not rollback", 0, pageRollback.size());
			
			// Perform write with a commit.
			Transaction tCommit = client.openTransaction();
			DocumentWriteSet writeset = docMgr.newWriteSet();
			for(int i =0;i<102;i++){

				writeset.add(DIRECTORY+"fooWithTrans"+i+".xml", new DOMHandle(getDocumentContent("This is so foo"+i)));
				map.put(DIRECTORY+"fooWithTrans"+i+".xml", convertXMLDocumentToString(getDocumentContent("This is so foo"+i)));
				if(count%BATCH_SIZE == 0){
					docMgr.write(writeset, transform, tCommit);
					writeset = docMgr.newWriteSet();
				}
				count++;
			}
			if(count%BATCH_SIZE > 0){
				docMgr.write(writeset, transform, tCommit);
			}
			tCommit.commit();			
			count=0;
			DocumentPage page = docMgr.read(uris);
			DOMHandle dh = new DOMHandle();
			 // To verify that transformation did run on all docs.
			String verifyAttrValue = null;
			while(page.hasNext()){
				DocumentRecord rec = page.next();
				rec.getContent(dh);
				assertTrue("Element has attribure ? :",dh.get().getElementsByTagName("foo").item(0).hasAttributes());
				verifyAttrValue = dh.get().getElementsByTagName("foo").item(0).getAttributes().getNamedItem("Lang").getNodeValue();
				assertTrue("Server Transform did not go through ",verifyAttrValue.equalsIgnoreCase("testBulkXQYTransformWithTrans"));
				count++;
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());throw e;
		}		
		assertEquals("document count", 102,count); 
	}
		
	@Test
	public void testBulkReadWithXQueryTransform() throws KeyManagementException, NoSuchAlgorithmException, Exception {

		TransformExtensionsManager transMgr = 
				client.newServerConfigManager().newTransformExtensionsManager();
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Adding attribute xquery Transform");
		metadata.setDescription("This plugin transforms an XML document by adding attribute to root node");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");
		// get the transform file
		File transformFile = new File("src/test/java/com/marklogic/client/functionaltest/transforms/add-attr-xquery-transform.xqy");
		FileHandle transformHandle = new FileHandle(transformFile);
		transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle, metadata);
		ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "English");
		int count=1;
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		Map<String,String> map= new HashMap<>();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		for(int i =0;i<102;i++){

			writeset.add(DIRECTORY+"sec"+i+".xml", new DOMHandle(getDocumentContent("This is to read"+i)));
			map.put(DIRECTORY+"sec"+i+".xml", convertXMLDocumentToString(getDocumentContent("This is to read"+i)));
			if(count%BATCH_SIZE == 0){
				docMgr.write(writeset);
				writeset = docMgr.newWriteSet();
			}
			count++;
		}
		if(count%BATCH_SIZE > 0){
			docMgr.write(writeset);
		}
		
		String uris[] = new String[102];
		for(int i =0;i<102;i++){
			uris[i]=DIRECTORY+"sec"+i+".xml";
		}
		count=0;
		DocumentPage page = docMgr.read(transform,uris);
		DOMHandle dh = new DOMHandle();
		while(page.hasNext()){
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			assertTrue("Element has attribure ? :",dh.get().getElementsByTagName("foo").item(0).hasAttributes());
			count++;
		}

		assertEquals("document count", 102,count); 

	}

}
