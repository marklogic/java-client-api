package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import com.marklogic.client.query.StructuredQueryDefinition;

public class TestPOJOWithStrucQD extends BasicJavaClientREST {
	private static String dbName = "TestPOJOStrucQDSearchDB";
	private static String [] fNames = {"TestPOJOStrucQDSearchDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private  DatabaseClient client ;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
		addRangePathIndex(dbName, "long", "com.marklogic.javaclient.Artifact/inventory", "", "ignore");
		addRangePathIndex(dbName, "string", "com.marklogic.javaclient.Artifact/manufacturer/com.marklogic.javaclient.Company/name", "http://marklogic.com/collation/", "ignore");
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
	@Test
	public void testPOJOSearchWithoutSearchHandle() {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;
		this.loadSimplePojos(products);
	
		StructuredQueryBuilder qb = new StructuredQueryBuilder();
		StructuredQueryDefinition q1 =qb.andNot(qb.term("cogs"),qb.term("special"));
		StructuredQueryDefinition qd = qb.and(q1,qb.collection("odd"));
		products.setPageLength(11);
		p = products.search(qd, 1);
		assertEquals("total no of pages",4,p.getTotalPages());
//		System.out.println(p.getTotalPages());
		long pageNo=1,count=0;
		do{
			count =0;

			p = products.search(qd,pageNo);

			while(p.iterator().hasNext()){
				Artifact a =p.iterator().next();
				validateArtifact(a);
				assertFalse("Verifying document with special is not there",a.getId()%5==0);
				assertTrue("Artifact Id is odd", a.getId()%2!=0);
				assertTrue("Company name contains widgets",a.getManufacturer().getName().contains("Widgets"));
				count++;
				//				System.out.println(a.getId()+" "+a.getManufacturer().getName() +"  "+count);
			}
			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();
		}while(!p.isLastPage() && pageNo<p.getTotalSize());
		assertEquals("page number after the loop",4,p.getPageNumber());
		assertEquals("total no of pages",4,p.getTotalPages());
	}
@Test
	public void testPOJOSearchWithSearchHandle() {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;
		this.loadSimplePojos(products);
		
		StructuredQueryBuilder qb = new StructuredQueryBuilder();
		StructuredQueryDefinition q1 =qb.range(qb.pathIndex("com.marklogic.javaclient.Artifact/inventory"), "xs:long",Operator.GT, 1010);
		StructuredQueryDefinition qd = qb.and(q1,qb.range(qb.pathIndex("com.marklogic.javaclient.Artifact/inventory"), "xs:long",Operator.LE, 1110),qb.collection("even"));
		SearchHandle results = new SearchHandle();
		products.setPageLength(10);
		p = products.search(qd, 1,results);
		assertEquals("total no of pages",5,p.getTotalPages());
		System.out.println(p.getTotalPages());
		long pageNo=1,count=0;
		do{
			count =0;
			p = products.search(qd,pageNo,results);

			while(p.iterator().hasNext()){
				Artifact a =p.iterator().next();
				validateArtifact(a);
				assertTrue("Enventory lies between 1010 to 1110", a.getInventory()>1010 && a.getInventory()<=1110);
				assertTrue("Artifact Id is even", a.getId()%2==0);
				assertTrue("Company name contains Acme",a.getManufacturer().getName().contains("Acme"));
				count++;
				//				System.out.println(a.getId()+" "+a.getManufacturer().getName() +"  "+count);
			}
			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();
			MatchDocumentSummary[] mds =results.getMatchResults();
			assertEquals("Size of the results summary",10,mds.length);
			for(MatchDocumentSummary md:mds){
				assertTrue("every uri should contain the class name",md.getUri().contains("Artifact"));
			}
			String[] facetNames = results.getFacetNames();
			for(String fname:facetNames){
				System.out.println(fname);
			}
			assertEquals("Total resulr from search handle ",50,results.getTotalResults());
			assertTrue("Search Handle metric results ",results.getMetrics().getTotalTime()>0);
		}while(!p.isLastPage() && pageNo<p.getTotalSize());
		assertEquals("Page start check",41,p.getStart());
		assertEquals("page number after the loop",5,p.getPageNumber());
		assertEquals("total no of pages",5,p.getTotalPages());
	}
	@Test
	public void testPOJOSearchWithJacksonHandle() {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;
		this.loadSimplePojos(products);
		;
		StructuredQueryBuilder qb = new StructuredQueryBuilder();
		StructuredQueryDefinition q1 =qb.containerQuery(qb.jsonProperty("name"),qb.term("special") );
		StructuredQueryDefinition qd = qb.and(q1,qb.word(qb.jsonProperty("name"), "acme"));
		JacksonHandle results = new JacksonHandle();
		p = products.search(qd, 1,results);
		products.setPageLength(11);
		assertEquals("total no of pages",1,p.getTotalPages());
		System.out.println(p.getTotalPages()+results.get().toString());
		long pageNo=1,count=0;
		do{
			count =0;
			p = products.search(qd,pageNo,results);

			while(p.iterator().hasNext()){
				Artifact a =p.iterator().next();
				validateArtifact(a);
				count++;
				assertTrue("Manufacture name starts with acme",a.getManufacturer().getName().contains("Acme"));
				assertTrue("Artifact name contains",a.getName().contains("special"));

			}
			assertEquals("Page size",count,p.size());
			pageNo=pageNo+p.getPageSize();

			assertEquals("Page start from search handls vs page methods",results.get().get("start").asLong(),p.getStart() );
			assertEquals("Format in the search handle","json",results.get().withArray("results").get(1).path("format").asText());
			assertTrue("Uri in search handle contains Artifact",results.get().withArray("results").get(1).path("uri").asText().contains("Artifact"));
			//			System.out.println(results.get().toString());
		}while(!p.isLastPage() && pageNo<p.getTotalSize());
		assertTrue("search handle has metrics",results.get().has("metrics"));
		assertEquals("Total from search handle",11,results.get().get("total").asInt());
		assertEquals("page number after the loop",1,p.getPageNumber());
		assertEquals("total no of pages",1,p.getTotalPages());
	}
	//Searching for Id as Number in JSON using value query 
	@Test
	public void testPOJOSearchWithStringHandle() throws JsonProcessingException, IOException {
		PojoRepository<Artifact,Long> products = client.newPojoRepository(Artifact.class, Long.class);
		PojoPage<Artifact> p;
		this.loadSimplePojos(products);
		StructuredQueryBuilder qb = new StructuredQueryBuilder();
		StructuredQueryDefinition qd =qb.value(qb.jsonProperty("id"), 5,10,15,20,25,30);
//		StructuredQueryDefinition qd = qb.and(q1,qb.range(qb.pathIndex("com.marklogic.javaclient.Artifact/inventory"), "xs:long",Operator.LE, 1110),qb.collection("even"));
	
		StringHandle results = new StringHandle();
		JacksonHandle jh = new JacksonHandle();
		p = products.search(qd, 1,jh);
		
		long pageNo=1,count=0;
		do{
			count =0;
			p = products.search(qd,pageNo,results.withFormat(Format.JSON));

			while(p.iterator().hasNext()){
				Artifact a =p.iterator().next();
				validateArtifact(a);
				count++;
			}
			assertEquals("Page total results",count,p.getTotalSize());
			pageNo=pageNo+p.getPageSize();
//					System.out.println(results.get().toString());
		}while(!p.isLastPage() && pageNo<p.getTotalSize());
		assertFalse("String handle is not empty",results.get().isEmpty());
		assertTrue("String handle contains results",results.get().contains("results"));
		assertTrue("String handle contains format",results.get().contains("\"format\":\"json\""));
//		String expected= jh.get().toString();
//		System.out.println(results.get().contains("\"format\":\"json\"")+ expected);
		ObjectMapper mapper = new ObjectMapper();
//		String expected= "{\"snippet-format\":\"snippet\",\"total\":1,\"start\":1,\"page-length\":50,\"results\":[{\"index\":1,\"uri\":\"com.marklogic.javaclient.Artifact/2.json\",\"path\":\"fn:doc(\\\"com.marklogic.javaclient.Artifact/2.json\\\")\",\"score\":55936,\"confidence\":0.4903799,\"fitness\":0.8035046,\"href\":\"/v1/documents?uri=com.marklogic.javaclient.Artifact%2F2.json\",\"mimetype\":\"application/json\",\"format\":\"json\",\"matches\":[{\"path\":\"fn:doc(\\\"com.marklogic.javaclient.Artifact/2.json\\\")\",\"match-text\":[]}]}],\"qtext\":\"cogs 2\",\"metrics\":{\"query-resolution-time\":\"PT0.004S\",\"snippet-resolution-time\":\"PT0S\",\"total-time\":\"PT0.005S\"}}";
//		JsonNode expNode = mapper.readTree(expected).get("results").iterator().next().get("matches");
		JsonNode actNode = mapper.readTree(results.get()).get("total");
//		System.out.println(expNode.equals(actNode)+"\n"+ expNode.toString()+"\n"+actNode.toString());
		
		assertEquals("Total search results resulted are ",6,actNode.asInt() );
	}

	
}
