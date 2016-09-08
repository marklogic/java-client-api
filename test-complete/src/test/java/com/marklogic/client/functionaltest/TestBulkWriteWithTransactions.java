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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import java.util.Map;

public class TestBulkWriteWithTransactions extends BasicJavaClientREST {

	private static final int BATCH_SIZE=100;
	private static final String DIRECTORY ="/bulkTransacton/";
	private static String dbName = "TestBulkWriteWithTransactionDB";
	private static String [] fNames = {"TestBulkWriteWithTransactionDB-1"};
	
	
	private  DatabaseClient client ;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("In Setup");
		configureRESTServer(dbName, fNames);
		createRESTUser("app-user", "password","rest-writer","rest-reader","flexrep-eval"  );
		createRESTUserWithPermissions("usr1", "password",getPermissionNode("flexrep-eval",Capability.READ),getCollectionNode("http://permission-collections/"), "rest-writer","rest-reader" );
		
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down" );
		cleanupRESTServer(dbName, fNames);
		deleteRESTUser("app-user");
		deleteRESTUser("usr1");
	}
	@Before
	public void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception {

		// create new connection for each test below
		client = getDatabaseClient("usr1", "password", Authentication.DIGEST);
	}
	@After
	public void tearDown() throws Exception {
		System.out.println("Running clear script");	
		// release client
		client.release();
	}
	public DocumentMetadataHandle setMetadata(){
		// create and initialize a handle on the metadata
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		metadataHandle.getCollections().addAll("my-collection1","my-collection2");
		metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
		metadataHandle.getProperties().put("reviewed", true);
		metadataHandle.getProperties().put("myString", "foo");
		metadataHandle.getProperties().put("myInteger", 10);
		metadataHandle.getProperties().put("myDecimal", 34.56678);
		metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
		metadataHandle.setQuality(23);
		return	metadataHandle;
	}
	public void validateMetadata(DocumentMetadataHandle mh){

		// get metadata values
		DocumentProperties properties = mh.getProperties();
		DocumentPermissions permissions = mh.getPermissions();
		DocumentCollections collections = mh.getCollections();

		// Properties
		String actualProperties = getDocumentPropertiesString(properties);
		boolean result = actualProperties.contains("size:5|");

		assertTrue("Document properties count", result);

		// Permissions
		 String expectedPermissions1 = "size:4|flexrep-eval:[READ]|rest-reader:[READ]|app-user:[UPDATE, READ]|rest-writer:[UPDATE]|";
		 	 
		 //Split up app-user permissions because the list of permissions are not ordered.
		String[] appUserPermissions = getDocumentPermissionsString(permissions).split("app-user:");
		String[] actualPermissions = appUserPermissions[0].split("\\|");
		System.out.println(actualPermissions.toString());
		assertTrue("Document permissions difference",appUserPermissions[1].contains("UPDATE, READ") || appUserPermissions[1].contains("READ, UPDATE"));
		
		assertTrue("Document permissions difference", expectedPermissions1.contains(actualPermissions[0]));
		assertTrue("Document permissions difference", expectedPermissions1.contains(actualPermissions[1]));
		assertTrue("Document permissions difference", expectedPermissions1.contains(actualPermissions[2]));
		
		// Collections 
		String expectedCollections = "size:2|my-collection1|my-collection2|";
		String[] actualCollections = getDocumentCollectionsString(collections).split("\\|");;
		
		assertTrue("Document collections difference", expectedCollections.contains(actualCollections[0]));
		assertTrue("Document collections difference", expectedCollections.contains(actualCollections[1]));
		assertTrue("Document collections difference", expectedCollections.contains(actualCollections[2]));

	}	
	public void validateDefaultMetadata(DocumentMetadataHandle mh){

		// get metadata values
		DocumentPermissions permissions = mh.getPermissions();
		DocumentCollections collections = mh.getCollections();

		// Permissions

		String expectedPermissions1 = "size:3|flexrep-eval:[READ]|rest-reader:[READ]|rest-writer:[UPDATE]|";
		String[] actualPermissions = getDocumentPermissionsString(permissions).split("\\|");
		assertTrue("Document permissions difference", expectedPermissions1.contains(actualPermissions[0]));
		assertTrue("Document permissions difference", expectedPermissions1.contains(actualPermissions[1]));
		assertTrue("Document permissions difference", expectedPermissions1.contains(actualPermissions[2]));
		assertTrue("Document permissions difference", expectedPermissions1.contains(actualPermissions[3]));

		// Collections 
		String expectedCollections = "size:1|http://permission-collections/|";
		String[] actualCollections = getDocumentCollectionsString(collections).split("\\|");

		assertTrue("Document collections difference", expectedCollections.contains(actualCollections[0]));
		assertTrue("Document collections difference", expectedCollections.contains(actualCollections[1]));
		
		//	    System.out.println(actualPermissions);
	}
	public void validateRecord(DocumentRecord record,Format type) {

		assertNotNull("DocumentRecord should never be null", record);
		assertNotNull("Document uri should never be null", record.getUri());
		assertTrue("Document uri should start with " + DIRECTORY, record.getUri().startsWith(DIRECTORY));
		assertEquals("All records are expected to be in same format", type, record.getFormat());
	}
/*
This is a basic test with transaction, and doing commit
*/
	@Test
public void testBulkWritewithTransactionCommit() throws KeyManagementException, NoSuchAlgorithmException, Exception {

		int count=1;
		Transaction t= client.openTransaction();
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		Map<String,String> map= new HashMap<>();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		for(int i =0;i<102;i++){
			writeset.add(DIRECTORY+"first"+i+".xml", new DOMHandle(getDocumentContent("This is so first"+i)));
			map.put(DIRECTORY+"first"+i+".xml", convertXMLDocumentToString(getDocumentContent("This is so first"+i)));
			if(count%BATCH_SIZE == 0){
				docMgr.write(writeset,t);
				writeset = docMgr.newWriteSet();
			}
			count++;
		}
		if(count%BATCH_SIZE > 0){
			docMgr.write(writeset,t);
		}
		String uris[] = new String[102];
		for(int i =0;i<102;i++){
			uris[i]=DIRECTORY+"first"+i+".xml";
		}
		try{t.commit();
		count=0;
		DocumentPage page = docMgr.read(uris);
		DOMHandle dh = new DOMHandle();
		while(page.hasNext()){
			DocumentRecord rec = page.next();
			validateRecord(rec,Format.XML);
			rec.getContent(dh);
			assertEquals("Comparing the content :",map.get(rec.getUri()),convertXMLDocumentToString(dh.get()));
			count++;
		}
		}catch(Exception e)
		{System.out.println(e.getMessage());throw e;}
		assertEquals("document count", 102,count); 

	}
	/*
	 * This test is trying to bulk insert documents with transaction open and not commited and try to read documents before commit.
	 */
@Test
public void testBulkWritewithTransactionsNoCommit() throws KeyManagementException, NoSuchAlgorithmException, Exception {
		int count=1;boolean tstatus =true;
		Transaction t1 = client.openTransaction();
		try{ 
			XMLDocumentManager docMgr = client.newXMLDocumentManager();
			Map<String,String> map= new HashMap<>();
			DocumentWriteSet writeset =docMgr.newWriteSet();
			for(int i =0;i<102;i++){
				writeset.add(DIRECTORY+"bar"+i+".xml", new DOMHandle(getDocumentContent("This is so foo"+i)));
				map.put(DIRECTORY+"bar"+i+".xml", convertXMLDocumentToString(getDocumentContent("This is so foo"+i)));
				if(count%BATCH_SIZE == 0){
					docMgr.write(writeset,t1);
					writeset = docMgr.newWriteSet();
				}
				count++;
			}
			if(count%BATCH_SIZE > 0){
				docMgr.write(writeset,t1);
			}
			String uris[] = new String[102];
			for(int i =0;i<102;i++){
				uris[i]=DIRECTORY+"bar"+i+".xml";
			}

			count=0;
			DocumentPage page = docMgr.read(t1,uris);
			DOMHandle dh = new DOMHandle();
			while(page.hasNext()){
				DocumentRecord rec = page.next();
				validateRecord(rec,Format.XML);
				rec.getContent(dh);
				assertEquals("Comparing the content :",map.get(rec.getUri()),convertXMLDocumentToString(dh.get()));
				count++;
			}

			assertEquals("document count", 102,count);
			t1.rollback();
			tstatus=false;
			count=0;
			page = docMgr.read(uris);
			dh = new DOMHandle();
			while(page.hasNext()){
				DocumentRecord rec = page.next();
				validateRecord(rec,Format.XML);
				rec.getContent(dh);
				assertEquals("Comparing the content :",map.get(rec.getUri()),convertXMLDocumentToString(dh.get()));
				count++;
			}

			assertEquals("document count", 0,count);
		
		}catch(Exception e){
			System.out.println(e.getMessage());
			tstatus=true;
			throw e;
		}
		finally{
			if(tstatus) {t1.rollback();}
		}
	}
//This test is trying load some documents, open a transaction, update only metadata,rollback transacton to see metadata info is not commited 
@Test public void testBulkWritewithMetadataTransactionNoCommit() throws KeyManagementException, NoSuchAlgorithmException, Exception {

		int count=1;
		boolean tstatus =true;
		Transaction t1 = client.openTransaction();
		try{ 
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		Map<String,String> map= new HashMap<>();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		for(int i =0;i<102;i++){
			writeset.add(DIRECTORY+"sec"+i+".xml", new DOMHandle(getDocumentContent("This is so sec"+i)));
			map.put(DIRECTORY+"sec"+i+".xml", convertXMLDocumentToString(getDocumentContent("This is so sec"+i)));
			if(count%BATCH_SIZE == 0){
				docMgr.write(writeset);
				writeset = docMgr.newWriteSet();
			}
			count++;
		}
		if(count%BATCH_SIZE > 0){
			docMgr.write(writeset);
		}
		count=1;
		Map<String,String> map2= new HashMap<>();
		DocumentMetadataHandle mh = setMetadata();
		for(int i =0;i<102;i++){
			writeset.add(DIRECTORY+"sec"+i+".xml",mh, new DOMHandle(getDocumentContent("This is with metadata"+i)));
			map2.put(DIRECTORY+"sec"+i+".xml", convertXMLDocumentToString(getDocumentContent("This is with metadata"+i)));
			if(count%BATCH_SIZE == 0){
				docMgr.write(writeset,t1);
				writeset = docMgr.newWriteSet();
			}
			count++;
		}
		if(count%BATCH_SIZE > 0){
			docMgr.write(writeset,t1);
		}
		String uris[] = new String[102];
		for(int i =0;i<102;i++){
			uris[i]=DIRECTORY+"sec"+i+".xml";
		}
		count=0;
		DocumentPage page = docMgr.read(t1,uris);
		DOMHandle dh = new DOMHandle();
		DocumentMetadataHandle mh2 = new DocumentMetadataHandle();
		while(page.hasNext()){
			DocumentRecord rec = page.next();
			validateRecord(rec,Format.XML);
			rec.getContent(dh);
			assertEquals("Comparing the content :",map2.get(rec.getUri()),convertXMLDocumentToString(dh.get()));
			docMgr.readMetadata(rec.getUri(), mh2,t1);
		    validateMetadata(mh2);
			count++;
		}
		assertEquals("document count", 102,count); 
		t1.rollback();
		tstatus=false;
		count=0;
		page = docMgr.read(uris);
		dh = new DOMHandle();
		mh2 = new DocumentMetadataHandle();
		while(page.hasNext()){
			DocumentRecord rec = page.next();
			validateRecord(rec,Format.XML);
			rec.getContent(dh);
			assertEquals("Comparing the content :",map.get(rec.getUri()),convertXMLDocumentToString(dh.get()));
			docMgr.readMetadata(rec.getUri(), mh2);
		    validateDefaultMetadata(mh2);
			count++;
		}
		assertEquals("document count", 102,count); 
		
		}catch(Exception e){
			System.out.println(e.getMessage());
			tstatus=true;
			throw e;
		}finally{
			if(tstatus){
				t1.rollback();
			}
		}
	
	}

@Test public void testBulkWritewithMetadataTransactioninDiffClientConnection() throws KeyManagementException, NoSuchAlgorithmException, Exception {

	int count=1;
	boolean tstatus =true;
	DatabaseClient c = getDatabaseClient("usr1", "password", Authentication.DIGEST);
	Transaction t1 = client.openTransaction();
	try{ 
	XMLDocumentManager docMgr = client.newXMLDocumentManager();
	Map<String,String> map= new HashMap<>();
	DocumentWriteSet writeset =docMgr.newWriteSet();
	DocumentMetadataHandle mh = setMetadata();
	for(int i =0;i<102;i++){
		writeset.add(DIRECTORY+"third"+i+".xml",mh, new DOMHandle(getDocumentContent("This is third"+i)));
		map.put(DIRECTORY+"third"+i+".xml", convertXMLDocumentToString(getDocumentContent("This is third"+i)));
		if(count%BATCH_SIZE == 0){
			docMgr.write(writeset,t1);
			writeset = docMgr.newWriteSet();
		}
		count++;
	}
	if(count%BATCH_SIZE > 0){
		docMgr.write(writeset,t1);
	}
	String uris[] = new String[102];
	for(int i =0;i<102;i++){
		uris[i]=DIRECTORY+"third"+i+".xml";
	}
	count=0;
	XMLDocumentManager dMgr = c.newXMLDocumentManager();
	DocumentPage page = dMgr.read(t1,uris);
	DOMHandle dh = new DOMHandle();
	DocumentMetadataHandle mh2 = new DocumentMetadataHandle();
	while(page.hasNext()){
		DocumentRecord rec = page.next();
		validateRecord(rec,Format.XML);
		rec.getContent(dh);
		assertEquals("Comparing the content :",map.get(rec.getUri()),convertXMLDocumentToString(dh.get()));
		dMgr.readMetadata(rec.getUri(), mh2,t1);
	    validateMetadata(mh2);
		count++;
	}
	assertEquals("document count", 102,count); 
	t1.commit();
	tstatus=false;
	
	}catch(Exception e){
		System.out.println(e.getMessage());
		tstatus=true;
		throw e;
	}finally{
		if(tstatus){
			t1.rollback();
		}
		c.release();
	}

}

@Test (expected = FailedRequestException.class)
public void testBulkWritewithTransactionCommitTimeOut() throws KeyManagementException, NoSuchAlgorithmException, Exception {

	int count=1;
	String docId[] = {"Sega-4MB.jpg"};
	//	boolean tstatus =false;
	Transaction t= client.openTransaction("timeoutTrans",1);
	BinaryDocumentManager docMgr = client.newBinaryDocumentManager();

	DocumentWriteSet writeset =docMgr.newWriteSet();
	File file1= null;
	file1 = new File("src/test/java/com/marklogic/client/functionaltest/data/" + docId[0]);
	FileHandle h1 = new FileHandle(file1);
	for(int i =0;i<102;i++){
		writeset.add(DIRECTORY+"binary"+i+".jpg", h1);
		if(count%BATCH_SIZE == 0){
			docMgr.write(writeset,t);
			writeset = docMgr.newWriteSet();
		}
		count++;
	}
	if(count%BATCH_SIZE > 0){
		docMgr.write(writeset,t);
	}
	t.commit();
	String uris[] = new String[102];
	for(int i =0;i<102;i++){
		uris[i]=DIRECTORY+"binary"+i+".jpg";
	}
	count=0;
	FileHandle rh = new FileHandle();
	DocumentPage page = docMgr.read(uris);
	while(page.hasNext()){
		DocumentRecord rec = page.next();
		validateRecord(rec,Format.BINARY);
		rec.getContent(rh);
		assertEquals("Content length :",file1.length(),rh.get().length());
		count++;
	}
	assertEquals("document count", 102,count); 
}

/*
 * Purpose: To test bulk delete within a transaction.
 * 
 * Test bulk insert documents with transaction open
 * and then perform a bulk delete.
 * Read documents before and after delete.
 */
@Test
	public void testBulkWriteDeleteWithTransactions() throws KeyManagementException, NoSuchAlgorithmException, Exception {
		int count = 1;
		boolean tstatus = true;
		Transaction t1 = client.openTransaction();
		try {
			XMLDocumentManager docMgr = client.newXMLDocumentManager();
			Map<String, String> map = new HashMap<>();
			DocumentWriteSet writeset = docMgr.newWriteSet();
			for (int i = 0; i < 102; i++) {
				writeset.add(DIRECTORY + "bar" + i + ".xml", new DOMHandle(
						getDocumentContent("This is so foo" + i)));
				map.put(DIRECTORY + "bar" + i + ".xml",
						convertXMLDocumentToString(getDocumentContent("This is so foo"
								+ i)));
				if (count % BATCH_SIZE == 0) {
					docMgr.write(writeset, t1);
					writeset = docMgr.newWriteSet();
				}
				count++;
			}
			if (count % BATCH_SIZE > 0) {
				docMgr.write(writeset, t1);
			}
			String uris[] = new String[102];
			for (int i = 0; i < 102; i++) {
				uris[i] = DIRECTORY + "bar" + i + ".xml";
			}

			count = 0;
			DocumentPage page = docMgr.read(t1, uris);
			DOMHandle dh = new DOMHandle();
			while (page.hasNext()) {
				DocumentRecord rec = page.next();
				validateRecord(rec, Format.XML);
				rec.getContent(dh);
				assertEquals("Comparing the content :", map.get(rec.getUri()),
						convertXMLDocumentToString(dh.get()));
				count++;
			}

			assertEquals("document count", 102, count);
			// Perform a bulk delete.
			docMgr.delete(t1, uris);
			
			count = 0;
			page = docMgr.read(uris);
			dh = new DOMHandle();
			while (page.hasNext()) {
				DocumentRecord rec = page.next();
				validateRecord(rec, Format.XML);
				rec.getContent(dh);
				assertEquals("Comparing the content :", map.get(rec.getUri()),
						convertXMLDocumentToString(dh.get()));
				count++;
			}

			assertEquals("document count", 0, count);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			tstatus = true;
			throw e;
		} finally {
			if (tstatus) {
				t1.rollback();
			}
		}
	}

}
