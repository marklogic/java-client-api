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
import com.marklogic.client.pojo.PojoQueryDefinition;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.QueryDefinition;

public class TestPOJOQueryBuilderGeoQueries extends BasicJavaClientREST {

	private static String dbName = "TestPOJOQueryBuilderGeoQuerySearchDB";
	private static String [] fNames = {"TestPOJOQueryBuilderGeoQuerySearchDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private  DatabaseClient client ;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
						System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		System.out.println("In setup");
//		setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
//		BasicJavaClientREST.setDatabaseProperties(dbName, "trailing-wildcard-searches", true);
//		BasicJavaClientREST.setDatabaseProperties(dbName, "word-positions", true);
//		BasicJavaClientREST.setDatabaseProperties(dbName, "element-word-positions", true);
//		BasicJavaClientREST.addRangePathIndex(dbName, "long", "com.marklogic.javaclient.Artifact/manufacturer", "", "reject",true);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down" );
//		tearDownJavaRESTServer(dbName, fNames, restServerName);
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
	// Below scenario is to test the geoPair -130 
	@Test
	public void testPOJOGeoQuerySearchWithGeoPair() {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;
		this.loadSimplePojos(products);
		String[] searchOptions ={"case-sensitive","wildcarded","min-occurs=2"};
		PojoQueryBuilder qb = products.getQueryBuilder();
		PojoQueryBuilder containerQb = qb.containerQueryBuilder("manufacturer", Company.class);
	  	PojoQueryDefinition qd =containerQb.geospatial(containerQb.geoPair("latitude", "longitude"),containerQb.circle(51.998, -77.966, 1));

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
	@Test
	public void testPOJOGeoQuerySearchWithGeoPath() {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;
		this.loadSimplePojos(products);
		String[] searchOptions ={"case-sensitive","wildcarded","min-occurs=2"};
		PojoQueryBuilder qb = products.getQueryBuilder();
		PojoQueryBuilder containerQb = qb.containerQueryBuilder("manufacturer", Company.class);
	  	PojoQueryDefinition qd =containerQb.geospatial(containerQb.geoProperty("latLong"));

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
}
