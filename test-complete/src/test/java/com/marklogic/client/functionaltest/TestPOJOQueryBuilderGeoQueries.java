package com.marklogic.client.functionaltest;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.functionaltest.TestPOJOSpecialCharRead.SpecialArtifact;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.pojo.PojoQueryDefinition;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.pojo.annotation.GeospatialLatitude;
import com.marklogic.client.pojo.annotation.GeospatialLongitude;
import com.marklogic.client.pojo.annotation.GeospatialPathIndexProperty;
import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.client.pojo.util.GenerateIndexConfig;
import com.marklogic.client.query.QueryDefinition;

public class TestPOJOQueryBuilderGeoQueries extends BasicJavaClientREST {

	private static String dbName = "TestPOJOQueryBuilderGeoQuerySearchDB";
	private static String [] fNames = {"TestPOJOQueryBuilderGeoQuerySearchDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private  DatabaseClient client ;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
		createAutomaticGeoIndex();
		
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
	
//	public static class SpecialGeoArtifact {
//		    
//		public String name;
//		@Id	
//		public long id;
//		private GeoCompany manufacturer;
//		private int inventory;
//
//		public long getId() {
//			return id;
//		}
//		public SpecialGeoArtifact setId(long id) {
//			this.id= id; return this;
//		}
//		public String getName() {
//			return name;
//		}
//		public SpecialGeoArtifact setName(String name) {
//			this.name = name; return this;
//		}
//		public GeoCompany getManufacturer() {
//			return manufacturer;
//		}
//		public SpecialGeoArtifact setManufacturer(GeoCompany manufacturer) {
//			this.manufacturer= manufacturer; return this;
//		}
//		public int getInventory() {
//			return inventory;
//		}
//		public SpecialGeoArtifact setInventory(int inventory) {
//			this.inventory= inventory; return this;
//		}
//	}
	 
//Issue 195	
//public static class GeoCompany {
//		
//	    private String name;
//	    private String state;
//        @GeospatialLatitude 
//	    public double latitude;
//	    @GeospatialLongitude
//	    public double longitude;
////	    @GeospatialPathIndexProperty
//	    public String latlongPoint;
//	    
//	    public String getName() {
//	        return name;
//	    }
//	    @Id
//	    public GeoCompany setName(String name) {
//	        this.name = name; return this;
//	    }
//	    public String getState() {
//	        return state;
//	    }
//	    public GeoCompany setState(String state) {
//	        this.state = state; return this;
//	    }
//	    @GeospatialLatitude 
//	    public double getLatitude() {
//	        return latitude;
//	    }
//	    
//	    public GeoCompany setLatitude(double latitude) {
//	        this.latitude = latitude; return this;
//	    }
//	   
//	    public GeoCompany setLatLongPoint(String latlong) {
//	        this.latlongPoint = latlong; return this;
//	    }
//	   
//	    public String getLatLongPoint(){
//	    	return this.latlongPoint;
//	    }
//	   // @GeospatialLongitude
//	    public double getLongitude() {
//	        return longitude;
//	    }
//	    
//	    public GeoCompany setLongitude(double longitude) {
//	        this.longitude = longitude; return this;
//	    }
//	 
//	}
//	
	public static void createAutomaticGeoIndex() throws Exception {
		boolean succeeded = false;
		File jsonFile = null;
		try {
			GenerateIndexConfig.main(new String[] { "-classes",
					"com.marklogic.client.functionaltest.GeoCompany",
					"-file", "TestAutomatedGeoPathRangeIndex.json" });

			jsonFile = new File("TestAutomatedGeoPathRangeIndex.json");
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

			if (!jnode.isNull()) {
				setPathRangeIndexInDatabase(dbName, jnode);
				succeeded = true;
				} else {
				assertTrue(
						"testArtifactIndexedOnString - No Json node available to insert into database",
						succeeded);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				jsonFile.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public GeoSpecialArtifact getGeoArtifact(int counter){

		GeoSpecialArtifact cogs = new GeoSpecialArtifact();
		cogs.setId(counter);
		if( counter % 5 == 0){
			cogs.setName("Cogs special");
			if(counter % 2 ==0){
				GeoCompany acme = new GeoCompany();
				acme.setName("Acme special, Inc.");
				acme.setState("Reno");
				acme.setLatitude(39.5272);
				acme.setLongitude(119.8219);
				acme.setLatLongPoint("39.5272 119.8219");
				cogs.setManufacturer(acme);

			}else{
				GeoCompany widgets = new GeoCompany();
				widgets.setName("Widgets counter Inc.");
				widgets.setState("Las Vegas");
				widgets.setLatitude(36.1215);
				widgets.setLongitude(115.1739);
				widgets.setLatLongPoint("36.1215 115.1739");
				cogs.setManufacturer(widgets);
			}
		}else{
			cogs.setName("Cogs "+counter);
			if(counter % 2 ==0){
				GeoCompany acme = new GeoCompany();
				acme.setName("Acme "+counter+", Inc.");
				acme.setState("Los Angles");
				acme.setLatitude(34.0500);
				acme.setLongitude(118.2500);
				acme.setLatLongPoint("34.0500 118.2500");
				cogs.setManufacturer(acme);

			}else{
				GeoCompany widgets = new GeoCompany();
				widgets.setName("Widgets "+counter+", Inc.");
				widgets.setState("San Fransisco");
				widgets.setLatitude(37.7833);
				widgets.setLongitude(122.4167);
				widgets.setLatLongPoint("37.7833 122.4167");
				cogs.setManufacturer(widgets);
			}
		}
		cogs.setInventory(1000+counter);
		return cogs;
	}
	public void validateArtifact(GeoSpecialArtifact art)
	{
		assertNotNull("Artifact object should never be Null",art);
		assertNotNull("Id should never be Null",art.id);
		assertTrue("Inventry is always greater than 1000", art.getInventory()>1000);
	}
	public void loadSimplePojos(PojoRepository products)
	{
		for(int i=1;i<111;i++){
			if(i%2==0){
				products.write(this.getGeoArtifact(i),"even","numbers");
			}
			else {
				products.write(this.getGeoArtifact(i),"odd","numbers");
			}
		}
	}
	// Below scenario is to test the geoPair -130 
	
	//This test is to verify GeoPair query works fine, 
	// searching for lattitud and longitude of Reno
	@Test
	public void testPOJOGeoQuerySearchWithGeoPair() {
		PojoRepository<GeoSpecialArtifact,Long> products = client.newPojoRepository(GeoSpecialArtifact.class, Long.class);
		PojoPage<GeoSpecialArtifact> p;
		this.loadSimplePojos(products);
		
		PojoQueryBuilder qb = products.getQueryBuilder();
		PojoQueryBuilder containerQb = qb.containerQueryBuilder("manufacturer",GeoCompany.class);
	  	PojoQueryDefinition qd =containerQb.geospatial(containerQb.geoPair("latitude", "longitude"),containerQb.circle(39.5272, 119.8219, 1));

		JacksonHandle jh = new JacksonHandle();
		products.setPageLength(5);
		p = products.search(qd, 1,jh);
		System.out.println(jh.get().toString());
		assertEquals("total no of pages",3,p.getTotalPages());
		System.out.println(jh.get().toString());
		
		long pageNo=1,count=0;
		do{
			count =0;
			p = products.search(qd,pageNo);
			while(p.hasNext()){
				GeoSpecialArtifact a =p.next();
				validateArtifact(a);
				assertTrue("Verifying document id is part of the search ids",a.getId()%5==0);
				assertEquals("Verifying Manufacurer is from state ","Reno",a.getManufacturer().getState());
				count++;
			}
			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();
		}while(!p.isLastPage() && pageNo<=p.getTotalSize());
		assertEquals("page number after the loop",3,p.getPageNumber());
		assertEquals("total no of pages",3,p.getTotalPages());
		assertEquals("page length from search handle",5,jh.get().path("page-length").asInt());
		assertEquals("Total results from search handle",11,jh.get().path("total").asInt());
	
	}
   // This test is to verify GeoProperty query works fine
	@Test
	public void testPOJOGeoQuerySearchWithGeoProperty() {
		PojoRepository<GeoSpecialArtifact,Long> products = client.newPojoRepository(GeoSpecialArtifact.class, Long.class);
		PojoPage<GeoSpecialArtifact> p;
		this.loadSimplePojos(products);
		
		PojoQueryBuilder qb = products.getQueryBuilder();
		PojoQueryBuilder containerQb = qb.containerQueryBuilder("manufacturer",GeoCompany.class);
	  	PojoQueryDefinition qd =containerQb.filteredQuery(containerQb.geospatial(containerQb.geoPath("latlongPoint"),containerQb.circle(36.1215, 115.1739, 1)));

		JacksonHandle jh = new JacksonHandle();
		products.setPageLength(5);
		p = products.search(qd, 1,jh);
		System.out.println(jh.get().toString());
	
		long pageNo=1,count=0;
		do{
			count =0;
			p = products.search(qd,pageNo);
			while(p.hasNext()){
				GeoSpecialArtifact a =p.next();
				validateArtifact(a);
				assertTrue("Verifying document id is part of the search ids",a.getId()%5==0);
				assertEquals("Verifying Manufacurer is from state ","Las Vegas",a.getManufacturer().getState());
				count++;
			}
			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();
		}while(!p.isLastPage() && pageNo<=p.getTotalSize());
		assertEquals("Total results from search handle",5,jh.get().path("results").size());
	
	}

	//This test is to verify GeoPath query works fine, 
		// searching for lattitud and longitude of Reno
		@Test
		public void testPOJOGeoQuerySearchWithGeoPath() {
			PojoRepository<GeoSpecialArtifact,Long> products = client.newPojoRepository(GeoSpecialArtifact.class, Long.class);
			PojoPage<GeoSpecialArtifact> p;
			this.loadSimplePojos(products);
			
			PojoQueryBuilder qb = products.getQueryBuilder();
			PojoQueryBuilder containerQb = qb.containerQueryBuilder("manufacturer",GeoCompany.class);
		  	PojoQueryDefinition qd =containerQb.geospatial(containerQb.geoPath("latlongPoint"),containerQb.circle(34.0500, 118.2500, 1));

			JacksonHandle jh = new JacksonHandle();
			products.setPageLength(15);
			p = products.search(qd, 1,jh);
			System.out.println(jh.get().toString());
			assertEquals("total no of pages",3,p.getTotalPages());
			System.out.println(jh.get().toString());
			
			long pageNo=1,count=0;
			do{
				count =0;
				p = products.search(qd,pageNo);
				while(p.hasNext()){
					GeoSpecialArtifact a =p.next();
					validateArtifact(a);
					assertTrue("Verifying document id is part of the search ids",a.getId()%2==0);
					assertEquals("Verifying Manufacurer is from state ","Los Angles",a.getManufacturer().getState());
					count++;
				}
				assertEquals("Page size",count,p.size());
				pageNo=pageNo+p.getPageSize();
			}while(!p.isLastPage() && pageNo<=p.getTotalSize());
			assertEquals("page number after the loop",3,p.getPageNumber());
			assertEquals("total no of pages",3,p.getTotalPages());
			assertEquals("page length from search handle",15,jh.get().path("page-length").asInt());
			assertEquals("Total results from search handle",44,jh.get().path("total").asInt());
		
		}
}
