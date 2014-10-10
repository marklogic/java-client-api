package com.marklogic.javaclient;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.pojo.PojoQueryBuilder.Operator;

public class TestPOJOQueryBuilderContainerQuery extends BasicJavaClientREST {

	private static String dbName = "TestPOJOQueryBuilderContainerQuerySearchDB";
	private static String [] fNames = {"TestPOJOQueryBuilderContainserQuerySearchDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private  DatabaseClient client ;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//						System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
		BasicJavaClientREST.setDatabaseProperties(dbName, "trailing-wildcard-searches", true);
		BasicJavaClientREST.setDatabaseProperties(dbName, "word-positions", true);
		BasicJavaClientREST.setDatabaseProperties(dbName, "element-word-positions", true);
		BasicJavaClientREST.addRangePathIndex(dbName, "long", "com.marklogic.javaclient.Artifact/inventory", "", "reject",true);
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

		Artifact cogs = new Artifact();
		cogs.setId(counter);
		if( counter % 5 == 0){
			cogs.setName("Cogs special");
			if(counter % 2 ==0){
				Company acme = new Company();
				acme.setName("Acme special, Inc.");
				acme.setWebsite("http://www.acme special.com");
				acme.setLatitude(41.998+counter);
				acme.setLongitude(-87.966+counter);
				cogs.setManufacturer(acme);

			}else{
				Company widgets = new Company();
				widgets.setName("Widgets counter Inc.");
				widgets.setWebsite("http://www.widgets counter.com");
				widgets.setLatitude(41.998+counter);
				widgets.setLongitude(-87.966+counter);
				cogs.setManufacturer(widgets);
			}
		}else{
			cogs.setName("Cogs "+counter);
			if(counter % 2 ==0){
				Company acme = new Company();
				acme.setName("Acme "+counter+", Inc.");
				acme.setWebsite("http://www.acme"+counter+".com");
				acme.setLatitude(41.998+counter);
				acme.setLongitude(-87.966+counter);
				cogs.setManufacturer(acme);

			}else{
				Company widgets = new Company();
				widgets.setName("Widgets "+counter+", Inc.");
				widgets.setWebsite("http://www.widgets"+counter+".com");
				widgets.setLatitude(41.998+counter);
				widgets.setLongitude(-87.966+counter);
				cogs.setManufacturer(widgets);
			}
		}
		cogs.setInventory(1000+counter);
		return cogs;
	}
	public void validateArtifact(Artifact art)
	{
		assertNotNull("Artifact object should never be Null",art);
		assertNotNull("Id should never be Null",art.id);
		assertTrue("Inventry is always greater than 1000", art.getInventory()>1000);
	}
	public void loadSimplePojos(PojoRepository products)
	{
		for(int i=1;i<111;i++){
			if(i%2==0){
				products.write(this.getArtifact(i),"even","numbers");
			}
			else {
				products.write(this.getArtifact(i),"odd","numbers");
			}
		}
	}
	// Below scenario is to test the ContainerQuery with term query 
	@Test
	public void testPOJOContainerQuerySearchWithWord() {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;
		this.loadSimplePojos(products);
		String[] searchOptions ={"case-sensitive","wildcarded","min-occurs=2"};
		PojoQueryBuilder qb = products.getQueryBuilder();
	  	QueryDefinition qd = qb.containerQuery("manufacturer", qb.term("counter"));
		JacksonHandle jh = new JacksonHandle();
		products.setPageLength(5);
		p = products.search(qd, 1,jh);
		assertEquals("total no of pages",3,p.getTotalPages());
		System.out.println(jh.get().toString());
		
		long pageNo=1,count=0;
		do{
			count =0;
			p = products.search(qd,pageNo);
			while(p.hasNext()){
				Artifact a =p.next();
				validateArtifact(a);
				assertTrue("Verifying document id is part of the search ids",a.getId()%5==0);
				assertTrue("Verifying Manufacurer has term counter",a.getManufacturer().getName().contains("counter"));
				count++;
				System.out.println(a.getManufacturer().getName());
			}
			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();
		}while(!p.isLastPage() && pageNo<=p.getTotalSize());
		assertEquals("page number after the loop",3,p.getPageNumber());
		assertEquals("total no of pages",3,p.getTotalPages());
		assertEquals("page length from search handle",5,jh.get().path("page-length").asInt());
		assertEquals("Total results from search handle",11,jh.get().path("total").asInt());
	
	}
	//Below scenario is to test container query builder with wild card options in word query
	@Test
	public void testPOJOwordSearchWithContainerQueryBuilder() {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;
		this.loadSimplePojos(products);
		String[] searchOptions ={"case-sensitive","wildcarded","max-occurs=1"};
		PojoQueryBuilder qb = products.getQueryBuilder();
		String[] searchNames = {"special"};
		QueryDefinition qd =qb.containerQueryBuilder("manufacturer", Company.class).word("name",searchOptions,1.0,searchNames);
		
		JacksonHandle jh = new JacksonHandle();
		products.setPageLength(11);
		p = products.search(qd, 1,jh);
;
		System.out.println(jh.get().toString());
		long pageNo=1,count=0;
		do{
			count =0;
			p = products.search(qd,pageNo);
			while(p.hasNext()){
				Artifact a =p.next();
				validateArtifact(a);
				assertTrue("Verifying document id is part of the search ids",a.getId()%5==0);
				count++;
				System.out.println(a.getName());
			}
			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();
		}while(!p.isLastPage() && pageNo<=p.getTotalSize());
		assertEquals("page number after the loop",1,p.getPageNumber());
		assertEquals("total no of pages",1,p.getTotalPages());
		assertEquals("page length from search handle",11,jh.get().path("page-length").asInt());
		assertEquals("Total results from search handle",11,jh.get().path("total").asInt());
	}
	
	//Below scenario is verifying range query from PojoBuilder 
	
	@Test
	public void testPOJORangeSearch() throws Exception {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;
		this.loadSimplePojos(products);
		PojoQueryBuilder qb = products.getQueryBuilder();
		QueryDefinition qd = qb.range("inventory", Operator.GE,1055);
		JacksonHandle jh = new JacksonHandle();
		products.setPageLength(56);
		p = products.search(qd, 1,jh);
		assertEquals("total no of pages",1,p.getTotalPages());

		long pageNo=1,count=0;
		do{
			count =0;
			p = products.search(qd,pageNo);
			while(p.hasNext()){
				Artifact a =p.next();
				validateArtifact(a);
				assertTrue("Verifying document id is part of the search ids",a.getId()>=55);
				count++;
			}
			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();
		}while(!p.isLastPage() && pageNo<=p.getTotalSize());
		assertEquals("page number after the loop",1,p.getPageNumber());
		assertEquals("total no of pages",1,p.getTotalPages());
		assertEquals("page length from search handle",56,jh.get().path("page-length").asInt());
		assertEquals("Total results from search handle",56,jh.get().path("total").asInt());
	}

	//Below scenario is to test range query with options
	@Test
	public void testPOJORangeQuerySearchWithOptions() {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;
		this.loadSimplePojos(products);
		String[] searchOptions ={"uncached","min-occurs=1"};
		PojoQueryBuilder qb = products.getQueryBuilder();
		String[] searchNames = {"counter","special"};
		QueryDefinition qd = qb.range("inventory",searchOptions,Operator.LE,1054);
		JacksonHandle jh = new JacksonHandle();
		products.setPageLength(55);
		p = products.search(qd, 1,jh);
		System.out.println(jh.get().toString());
		assertEquals("total no of pages",1,p.getTotalPages());

		long pageNo=1,count=0;
		do{
			count =0;
			p = products.search(qd,pageNo);
			while(p.hasNext()){
				Artifact a =p.next();
				validateArtifact(a);
				assertTrue("Verifying document id is part of the search ids",a.getId()<=54);
				count++;
			}
			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();
		}while(!p.isLastPage() && pageNo<=p.getTotalSize());
		assertEquals("page number after the loop",1,p.getPageNumber());
		assertEquals("total no of pages",1,p.getTotalPages());
		assertEquals("page length from search handle",55,jh.get().path("page-length").asInt());
		assertEquals("Total results from search handle",54,jh.get().path("total").asInt());
	}

	//Below scenario is verifying and query with all pojo builder methods

	@Test
	public void testPOJOWordSearchWithOptions() throws Exception {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;
		this.loadSimplePojos(products);
		
		String[] searchOptions ={"case-sensitive","wildcarded","max-occurs=1"};
		String[] rangeOptions ={"uncached","min-occurs=1"};
		PojoQueryBuilder qb = products.getQueryBuilder();
		String[] searchNames = {"Acm*"};
		QueryDefinition qd = qb.and(qb.andNot(qb.word("name",searchOptions, 1.0,searchNames),
											  qb.containerQueryBuilder("manufacturer", Company.class).value("name","Acme special, Inc.") ),
									qb.range("inventory",rangeOptions,Operator.LT,1101));
				
		JacksonHandle jh = new JacksonHandle();
		products.setPageLength(25);
		p = products.search(qd, 1,jh);
		System.out.println(jh.get().toString());
		long pageNo=1,count=0;
		do{
			count =0;
			p = products.search(qd,pageNo);
			while(p.hasNext()){
				Artifact a =p.next();
				validateArtifact(a);
				assertTrue("Verifying document id is part of the search ids",a.getId()<1101);
				assertFalse("Verifying document has word counter",a.getManufacturer().getName().contains("special"));
				count++;

			}
			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();
		}while(!p.isLastPage() && pageNo<=p.getTotalSize());
		assertEquals("page number after the loop",2,p.getPageNumber());
		assertEquals("total no of pages",2,p.getTotalPages());
		assertEquals("page length from search handle",25,jh.get().path("page-length").asInt());
		assertEquals("Total results from search handle",40,jh.get().path("total").asInt());
	}

}
