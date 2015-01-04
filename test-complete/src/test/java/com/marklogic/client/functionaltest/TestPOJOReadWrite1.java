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

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoRepository;

public class TestPOJOReadWrite1 extends BasicJavaClientREST {
	private static String dbName = "TestPOJORWDB";
	private static String [] fNames = {"TestPOJORWDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private  DatabaseClient client ;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down" );
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}

	@Before
	public void setUp() throws Exception {
		client = DatabaseClientFactory.newClient("localhost", restPort, "rest-admin", "x", Authentication.DIGEST);
	}

	@After
	public void tearDown() throws Exception {
		// release client
		client.release();
	}

	public Artifact getArtifact(int counter){
		Company acme = new Company();
		acme.setName("Acme "+counter+", Inc.");
		acme.setWebsite("http://www.acme"+counter+".com");
		acme.setLatitude(41.998+counter);
		acme.setLongitude(-87.966+counter);
		Artifact cogs = new Artifact();
		cogs.setId(counter);
		cogs.setName("Cogs "+counter);
		cogs.setManufacturer(acme);
		cogs.setInventory(1000+counter);

		return cogs;
	}
    public void validateArtifact(Artifact art)
    {
    assertNotNull("Artifact object should never be Null",art);
    assertNotNull("Id should never be Null",art.id);
    assertTrue("Inventry is always greater than 1000", art.getInventory()>1000);
    }
    //This test is to persist a simple design model objects in ML, read from ML, delete all
	@Test
	public void testPOJOWrite() {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		products.deleteAll();
		//Load more than 100 objects
		for(int i=1;i<112;i++){
			products.write(this.getArtifact(i));
		}
		assertEquals("Total number of object recods",111, products.count());
		for(long i=1;i<112;i++){
			assertTrue("Product id "+i+" does not exist",products.exists(i));
			this.validateArtifact(products.read(i));
		}
		products.deleteAll();
		for(long i=1;i<112;i++){
			assertFalse("Product id exists ?",products.exists(i));
		}
		
	}
	//Issue 192 describes the use case 
	@Test(expected = ResourceNotFoundException.class)
	public void testPOJOReadInvalidId() {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		products.deleteAll();
		//Load more than 100 objects
		for(int i=1;i<112;i++){
			products.write(this.getArtifact(i));
		}
		assertEquals("Total number of object recods",111, products.count());
		for(long i=1143;i<1193;i++){
				this.validateArtifact(products.read(i));
		}
		products.deleteAll();
		for(long i=1;i<112;i++){
			assertFalse("Product id exists ?",products.exists(i));
		}
		
	}
	//This test is to persist objects into different collections, read documents based on Id and delete single object based on Id
	@Test
	public void testPOJOWriteWithCollection() {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		//Load more than 110 objects into different collections
		products.deleteAll();
		for(int i=112;i<222;i++){
			if(i%2==0){
			products.write(this.getArtifact(i),"even","numbers");
			}
			else {
				products.write(this.getArtifact(i),"odd","numbers");
			}
		}
		assertEquals("Total number of object recods",110, products.count("numbers"));
		assertEquals("Collection even count",55,products.count("even"));
		assertEquals("Collection odd count",55,products.count("odd"));
		for(long i=112;i<222;i++){
			// validate all the records inserted are readable
			assertTrue("Product id "+i+" does not exist",products.exists(i));
			this.validateArtifact(products.read(i));
		}
		products.delete((long)112);
		assertFalse("Product id 112 exists ?",products.exists((long)112));
		products.deleteAll();
		//see any document exists
		for(long i=112;i<222;i++){
			assertFalse("Product id "+i+" exists ?",products.exists(i));
		}
		//see if it complains when there are no records
		products.delete((long)112);
		products.deleteAll();
	}
	//This test is to read objects into pojo page based on Ids ,it has a scenario for Issue 192
		// until #103 is resolved	
	@Test
		public void testPOJOWriteWithPojoPage() {
			PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
			//Load more than 110 objects into different collections
			products.deleteAll();
			Long[] ids= new Long[112];
			int j=0;
			for(int i=222;i<333;i++){
				ids[j] =(long) i;j++;
				if(i%2==0){
				products.write(this.getArtifact(i),"even","numbers");
				}
				else {
					products.write(this.getArtifact(i),"odd","numbers");
				}
			}
			ids[j]=(long)1234234;j++;
			assertEquals("Total number of object recods",111, products.count("numbers"));
			assertEquals("Collection even count",56,products.count("even"));
			assertEquals("Collection odd count",55,products.count("odd"));
			
			System.out.println("Default Page length setting on docMgr :"+products.getPageLength());
			assertEquals("Default setting for page length",50,products.getPageLength());
		
//			assertEquals("explicit setting for page length",1,products.getPageLength());
			PojoPage<Artifact> p= products.read(ids);
			// test for page methods
			System.out.println("Total number of estimated results:"+p.getTotalSize()+ids.length);
			System.out.println("Total number of estimated pages :"+p.getTotalPages());
			long pageNo=1,count=0;
			while(p.hasNext()){
				this.validateArtifact(p.next());
				count++;
			}
			assertEquals("document count", 111,count);
			products.deleteAll();
			//see any document exists
			for(long i=112;i<222;i++){
				assertFalse("Product id "+i+" exists ?",products.exists(i));
			}
			//see if it complains when there are no records
		}
	@Test
	public void testPOJOWriteWithPojoPageReadAll() {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		//Load more than 110 objects into different collections
		products.deleteAll();
		for(int i=222;i<333;i++){
		if(i%2==0){
			products.write(this.getArtifact(i),"even","numbers");
			}
		else {
				products.write(this.getArtifact(i),"odd","numbers");
			}
		}
		assertEquals("Total number of object recods",111, products.count("numbers"));
		assertEquals("Collection even count",56,products.count("even"));
		assertEquals("Collection odd count",55,products.count("odd"));
		
		System.out.println("Default Page length setting on docMgr :"+products.getPageLength());
		assertEquals("Default setting for page length",50,products.getPageLength());
		products.setPageLength(1);
		assertEquals("explicit setting for page length",1,products.getPageLength());
		PojoPage<Artifact> p= products.readAll(1);
		// test for page methods
		assertEquals("Number of records",1,p.size());
		System.out.println("Page size"+p.size());
		assertEquals("Starting record in first page ",1,p.getStart());
		System.out.println("Starting record in first page "+p.getStart());
		
		assertEquals("Total number of estimated results:",111,p.getTotalSize());
		System.out.println("Total number of estimated results:"+p.getTotalSize());
		assertEquals("Total number of estimated pages :",111,p.getTotalPages());
		System.out.println("Total number of estimated pages :"+p.getTotalPages());
		assertTrue("Is this First page :",p.isFirstPage());
		assertFalse("Is this Last page :",p.isLastPage());
		assertTrue("Is this First page has content:",p.hasContent());
		//		Need the Issue #75 to be fixed  
		assertFalse("Is first page has previous page ?",p.hasPreviousPage());
		long pageNo=1,count=0;
		do{
			count=0;
			p= products.readAll(pageNo);
			
			if(pageNo >1){ 
				assertFalse("Is this first Page", p.isFirstPage());
				assertTrue("Is page has previous page ?",p.hasPreviousPage());
			}
			
		while(p.iterator().hasNext()){
			this.validateArtifact(p.iterator().next());
			count++;
		}
		assertEquals("document count", p.size(),count);
		
		pageNo = pageNo + p.getPageSize();
		}while(!(p.isLastPage()) && pageNo < p.getTotalSize());
		assertTrue("page count is 111 ",pageNo == p.getTotalPages());
		assertTrue("Page has previous page ?",p.hasPreviousPage());
		assertEquals("page size", 1,p.getPageSize());
		assertEquals("document count", 111,p.getTotalSize());

		products.deleteAll();
		p= products.readAll(1);
		assertFalse("Page has any records ?",p.hasContent());
		
		
	}
}
