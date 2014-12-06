package com.marklogic.client.functionaltest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.pojo.PojoQueryDefinition;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.QueryDefinition;

public class TestPOJOQueryBuilderValueQuery extends BasicJavaClientREST {

	private static String dbName = "TestPOJOQueryBuilderValueSearchDB";
	private static String [] fNames = {"TestPOJOQueryBuilderValueSearchDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private  DatabaseClient client ;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		System.out.println("In setup");
	 setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
//		BasicJavaClientREST.setDatabaseProperties(dbName, "trailing-wildcard-searches", true);
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
	// Below scenario is to test the value query with numbers return correct results
	@Test
	public void testPOJOValueSearchWithNumbers() {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;

		this.loadSimplePojos(products);
		String[] searchOptions ={"case-sensitive","wildcarded","min-occurs=2"};
		PojoQueryBuilder qb = products.getQueryBuilder();
		Number[] searchIds = {5,10,15,20,25,30,35,40,45,50,55,60,65,70,75,80,85,90,95,100,105,110,115,120,121,122,123,124,125,126};
		PojoQueryDefinition qd = qb.value("id",searchOptions,-1.0,searchIds);

		JacksonHandle jh = new JacksonHandle();
		products.setPageLength(5);
		p = products.search(qd, 1,jh);
		assertEquals("total no of pages",5,p.getTotalPages());
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
			}
			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();
		}while(!p.isLastPage() && pageNo<p.getTotalSize());
		assertEquals("page number after the loop",5,p.getPageNumber());
		assertEquals("total no of pages",5,p.getTotalPages());
		assertEquals("page length from search handle",5,jh.get().path("page-length").asInt());
		assertEquals("Total results from search handle",22,jh.get().path("total").asInt());
	}
	//Below scenario is to test value query with wild cards in strings
	@Test
	public void testPOJOValueSearchWithStrings() {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;
		this.loadSimplePojos(products);
		String[] searchOptions ={"case-sensitive","wildcarded","min-occurs=1"};
		PojoQueryBuilder qb = products.getQueryBuilder();
		String[] searchNames = {"Acme spe* *","Widgets spe* *"};
		PojoQueryDefinition qd =qb.filteredQuery(qb.value("name",searchOptions,100.0,searchNames));
		JacksonHandle jh = new JacksonHandle();
		products.setPageLength(5);
		p = products.search(qd, 1,jh);

//		assertEquals("total no of pages",3,p.getTotalPages()); since page methods are estimates
		System.out.println(jh.get().toString());
		long pageNo=1,count=0;
		do{
			count =0;
			p = products.search(qd,pageNo);
			while(p.hasNext()){
				Artifact a =p.next();
				validateArtifact(a);
				assertTrue("Verifying document id is part of the search ids",a.getId()%5==0);
				assertTrue("Verifying document name has Acme/Wigets special",a.getManufacturer().getName().contains("Acme special")||a.getManufacturer().getName().contains("Widgets special"));
				count++;
				System.out.println(a.getId());
			}
			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();
		}while(!p.isLastPage() && pageNo<=p.getTotalSize());
		assertEquals("page length from search handle",5,jh.get().path("results").size());
//		assertEquals("Total results from search handle",11,jh.get().path("total").asInt());
	}
	//Below scenario is verifying value query from PojoBuilder that matches to no document
	//Issue 127 is logged for the below scenario
	@Test
	public void testPOJOValueSearchWithNoResults() throws Exception {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;
		this.loadSimplePojos(products);
		setupServerRequestLogging(client,true);
		String[] searchOptions ={"case-sensitive","wildcarded","min-occurs=2"};
		PojoQueryBuilder qb = products.getQueryBuilder();
		String[] searchNames = {"acme*"};
		PojoQueryDefinition qd =qb.filteredQuery( qb.value("name",searchOptions,100.0,searchNames));
		JacksonHandle jh = new JacksonHandle();
		products.setPageLength(5);
		p = products.search(qd, 1,jh);
		setupServerRequestLogging(client,false);
		System.out.println(jh.get().toString());
		assertEquals("total no of pages",0,p.getTotalPages());

		long pageNo=1,count=0;
		do{
			count =0;
			p = products.search(qd,pageNo);
			while(p.hasNext()){
				Artifact a =p.next();
				validateArtifact(a);
				assertTrue("Verifying document id is part of the search ids",a.getId()%5==0);
				count++;
				System.out.println(a.getId());
			}
			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();
		}while(!p.isLastPage() && pageNo<=p.getTotalSize());
		assertEquals("page number after the loop",0,p.getPageNumber());
		assertEquals("total no of pages",0,p.getTotalPages());
		assertEquals("page length from search handle",5,jh.get().path("page-length").asInt());
//		assertEquals("Total results from search handle",10,jh.get().path("total").asInt());
	}

	//Below scenario is to test word query without options
	@Test
	public void testPOJOWordQuerySearchWithoutOptions() {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;
		this.loadSimplePojos(products);
		//			String[] searchOptions ={"case-sensitive","min-occurs=2"};
		PojoQueryBuilder qb = products.getQueryBuilder();
		String[] searchNames = {"counter","special"};
		PojoQueryDefinition qd = qb.word("name",searchNames);
		JacksonHandle jh = new JacksonHandle();
		products.setPageLength(5);
		p = products.search(qd, 1,jh);
		System.out.println(jh.get().toString());
		assertEquals("total no of pages",5,p.getTotalPages());

		long pageNo=1,count=0;
		do{
			count =0;
			p = products.search(qd,pageNo);
			while(p.hasNext()){
				Artifact a =p.next();
				validateArtifact(a);
				assertTrue("Verifying document id is part of the search ids",a.getId()%5==0);
				assertTrue("Verifying document has word counter",a.getManufacturer().getName().contains("counter")||a.getManufacturer().getName().contains("special"));
				count++;
				System.out.println(a.getId());
			}
			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();
		}while(!p.isLastPage() && pageNo<=p.getTotalSize());
		assertEquals("page number after the loop",5,p.getPageNumber());
		assertEquals("total no of pages",5,p.getTotalPages());
		assertEquals("page length from search handle",5,jh.get().path("page-length").asInt());
		assertEquals("Total results from search handle",22,jh.get().path("total").asInt());
	}

	//Below scenario is verifying word query from PojoBuilder with options

	@Test
	public void testPOJOWordSearchWithOptions() throws Exception {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;
		this.loadSimplePojos(products);
		setupServerRequestLogging(client,true);
		String[] searchOptions ={"case-sensitive","wildcarded","min-occurs=1"};
		PojoQueryBuilder qb = products.getQueryBuilder();
		String[] searchNames = {"count*"};
		PojoQueryDefinition qd = qb.filteredQuery(qb.word("name",searchOptions,0.0,searchNames));
		JacksonHandle jh = new JacksonHandle();
		products.setPageLength(5);
		p = products.search(qd, 1,jh);
		setupServerRequestLogging(client,false);
		System.out.println(jh.get().toString());
//		assertEquals("total no of pages",3,p.getTotalPages());

		long pageNo=1,count=0;
		do{
			count =0;
			p = products.search(qd,pageNo,jh);
			while(p.hasNext()){
				Artifact a =p.next();
				validateArtifact(a);
				assertTrue("Verifying document id is part of the search ids",a.getId()%5==0);
				assertTrue("Verifying document has word counter",a.getManufacturer().getName().contains("counter"));
				count++;

			}
			System.out.println(jh.get().toString());
			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();
		}while(!p.isLastPage() && pageNo<=p.getTotalSize());
		assertEquals("page number after the loop",3,p.getPageNumber());
		assertEquals("total no of pages",3,p.getTotalPages());
		assertEquals("page length from search handle",5,jh.get().path("page-length").asInt());
		}

}
