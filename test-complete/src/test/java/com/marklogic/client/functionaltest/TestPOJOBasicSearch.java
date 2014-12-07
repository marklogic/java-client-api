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
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoRepository;

public class TestPOJOBasicSearch extends BasicJavaClientREST {
	private static String dbName = "TestPOJObasicSearchDB";
	private static String [] fNames = {"TestPOJObasicSearchDB-1"};
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
 
	//This test is to search objects under different collections, read documents to validate
	@Test
	public void testPOJOSearchWithCollections() {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;
		//Load more than 111 objects into different collections
		for(int i=1;i<112;i++){
			if(i%2==0){
			products.write(this.getArtifact(i),"even","numbers");
			}
			else {
				products.write(this.getArtifact(i),"odd","numbers");
			}
		}
		assertEquals("Total number of object recods",111, products.count("numbers"));
		assertEquals("Collection even count",55,products.count("even"));
		assertEquals("Collection odd count",56,products.count("odd"));
		
		products.setPageLength(5);
		long pageNo=1,count=0;
		do{
			count =0;
			p = products.search(pageNo, "even");
			while(p.iterator().hasNext()){
				Artifact a =p.iterator().next();
				validateArtifact(a);
				assertTrue("Artifact Id is even", a.getId()%2==0);
				count++;
			}
			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();
		}while(!p.isLastPage() && pageNo<p.getTotalSize());
		assertEquals("total no of pages",11,p.getTotalPages());
		pageNo=1;
		do{
			count =0;
			p = products.search(1, "odd");
			while(p.iterator().hasNext()){
				Artifact a =p.iterator().next();
				assertTrue("Artifact Id is even", a.getId()%2 !=0);
				validateArtifact(a);
				products.delete(a.getId());
				count++;
			}
//			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();
			
		}while(!p.isLastPage() );
		
		assertEquals("Total no of documents left",55,products.count());
		products.deleteAll();
		//see any document exists
		assertFalse("all the documents are deleted",products.exists((long)12));
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
		PojoPage<Artifact> p= products.search(1,"even","odd");
		// test for page methods
		assertEquals("Number of records",1,p.size());
		System.out.println("Page size"+p.size());
		assertEquals("Starting record in first page ",1,p.getStart());
		System.out.println("Starting record in first page "+p.getStart());
		
		assertEquals("Total number of estimated results:",111,p.getTotalSize());
		System.out.println("Total number of estimated results:"+p.getTotalSize());
		assertEquals("Total number of estimated pages :",111,p.getTotalPages());
		System.out.println("Total number of estimated pages :"+p.getTotalPages());
		assertTrue("Is this First page :",p.isFirstPage());//this is bug
		assertFalse("Is this Last page :",p.isLastPage());
		assertTrue("Is this First page has content:",p.hasContent());
		//		Need the Issue #75 to be fixed  
		assertFalse("Is first page has previous page ?",p.hasPreviousPage());
		long pageNo=1,count=0;
		do{
			count=0;
			p= products.search(pageNo,"numbers");
			
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
//		assertTrue("page count is 111 ",pageNo == p.getTotalPages());
		assertTrue("Page has previous page ?",p.hasPreviousPage());
		assertEquals("page size", 1,p.getPageSize());
		assertEquals("document count", 111,p.getTotalSize());

		products.deleteAll();
		p= products.readAll(1);
		assertFalse("Page has any records ?",p.hasContent());
		
		
	}
}
