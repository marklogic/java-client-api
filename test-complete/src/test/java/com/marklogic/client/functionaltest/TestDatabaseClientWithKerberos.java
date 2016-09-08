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

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.SSLContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import sun.security.krb5.KrbException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.Transaction;
import com.marklogic.client.DatabaseClientFactory.KerberosAuthContext;
import com.marklogic.client.DatabaseClientFactory.BasicAuthContext;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.alerting.RuleDefinition;
import com.marklogic.client.alerting.RuleDefinitionList;
import com.marklogic.client.alerting.RuleManager;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.eval.EvalResult.Type;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonDatabindHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.XMLStreamReaderHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.pojo.PojoPage;
import com.marklogic.client.pojo.PojoQueryBuilder;
import com.marklogic.client.pojo.PojoQueryDefinition;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.pojo.util.GenerateIndexConfig;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.util.RequestLogger;
import java.util.Map;

public class TestDatabaseClientWithKerberos extends BasicJavaClientREST {

	private static String dbName = "TestDatabaseClientWithKerberosDB";
	private static String [] fNames = {"TestDatabaseClientWithKerberosDB-1"};
	private static final int BATCH_SIZE=100;
	private static final String DIRECTORY ="/bulkTransform/";
	
	private static String appServerHostName;
	private static int appServerHostPort = 8021;
		
	private static String UberdbName = "UberDatabaseClientConnectionDB";
	private static String UberDefaultdbName = "Documents";
	private static String [] UberfNames = {"UberDatabaseClientConnectionDB-1"};
	private static int Uberport = 8000;
	private static String UberrestServerName = "App-Services";
	
	private static String appServerName = "REST-Java-Client-API-ServerKerberos";
	// External security name to be set for the App Server.
	private static String extSecurityName = "KerberosExtSec";
	private static String kdcPrincipalUser = "user2@MLTEST1.LOCAL";
	
	private  DatabaseClient client ;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("In setup");
		loadGradleProperties();
		appServerHostName = getRestAppServerHostName();
		
		setupJavaRESTServer(dbName, fNames[0], appServerName, appServerHostPort);
		createAutomaticGeoIndex();
		
		setupAppServicesConstraint(dbName);
		// Create the External Security setting.
		createExternalSecurityForKerberos(appServerName, extSecurityName);
		// Associate the external security with the App Server.
		associateRESTServerWithKerberosExtSecurity(appServerName, extSecurityName);
		createUserRolesWithPrevilages("test-eval","xdbc:eval", "xdbc:eval-in","xdmp:eval-in","any-uri","xdbc:invoke");
		createRESTKerberosUser("user2", "MarkLogic200", kdcPrincipalUser, "admin", "test-eval");
		createRESTUser("rest-admin","x","rest-admin");
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down" );
		tearDownJavaRESTServer(dbName, fNames, appServerName);	
	}
	
	@Before
	public void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception {
		SSLContext sslcontext = null;

		if (IsSecurityEnabled()) {
			sslcontext = getSslContext();
			client = DatabaseClientFactory.newClient(
					getRestServerHostName(), getRestServerPort(),
					new KerberosAuthContext().withSSLContext(sslcontext));
		} else
			client = DatabaseClientFactory.newClient(appServerHostName,
					appServerHostPort, new KerberosAuthContext());
	}
	
	@After
	public void tearDown() throws Exception {
		// release client
		client.release();
	}
	
	public static void createAutomaticGeoIndex() throws KeyManagementException, NoSuchAlgorithmException, Exception {		
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
	
	public GeoSpecialArtifact getGeoArtifact(int counter) {

		GeoSpecialArtifact cogs = new GeoSpecialArtifact();
		cogs.setId(counter);
		if( counter % 5 == 0) {
			cogs.setName("Cogs special");
			if(counter % 2 ==0) {
				GeoCompany acme = new GeoCompany();
				acme.setName("Acme special, Inc.");
				acme.setState("Reno");
				acme.setLatitude(39.5272);
				acme.setLongitude(119.8219);
				acme.setLatLongPoint("39.5272 119.8219");
				cogs.setManufacturer(acme);

			} else {
				GeoCompany widgets = new GeoCompany();
				widgets.setName("Widgets counter Inc.");
				widgets.setState("Las Vegas");
				widgets.setLatitude(36.1215);
				widgets.setLongitude(115.1739);
				widgets.setLatLongPoint("36.1215 115.1739");
				cogs.setManufacturer(widgets);
			}
		} else {
			cogs.setName("Cogs "+counter);
			if(counter % 2 ==0){
				GeoCompany acme = new GeoCompany();
				acme.setName("Acme "+counter+", Inc.");
				acme.setState("Los Angles");
				acme.setLatitude(34.0500);
				acme.setLongitude(118.2500);
				acme.setLatLongPoint("34.0500 118.2500");
				cogs.setManufacturer(acme);

			} else {
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
	
	public void validateArtifact(GeoSpecialArtifact art) {
		assertNotNull("Artifact object should never be Null",art);
		assertNotNull("Id should never be Null",art.id);
		assertTrue("Inventry is always greater than 1000", art.getInventory()>1000);
	}
	
	public void loadSimplePojos(PojoRepository products) {
		for(int i=1;i<111;i++) {
			if(i%2==0) {
				products.write(this.getGeoArtifact(i),"even","numbers");
			}
			else {
				products.write(this.getGeoArtifact(i),"odd","numbers");
			}
		}
	}
	
	public void validateMetadata(DocumentMetadataHandle mh) {
		// get metadata values
		DocumentProperties properties = mh.getProperties();
		DocumentPermissions permissions = mh.getPermissions();
		DocumentCollections collections = mh.getCollections();

		// Properties
		// String expectedProperties =
		// "size:5|reviewed:true|myInteger:10|myDecimal:34.56678|myCalendar:2014|myString:foo|";
		String actualProperties = getDocumentPropertiesString(properties);
		boolean result = actualProperties.contains("size:5|");
		assertTrue("Document properties count", result);

		// Permissions
		String actualPermissions = getDocumentPermissionsString(permissions);
		System.out.println(actualPermissions);
         //size:3|rest-writer:[EXECUTE, READ, UPDATE]|rest-reader:[READ]|app-user:[READ, UPDATE]|
		assertTrue("Document permissions difference in size value",
				actualPermissions.contains("size:3"));		
		assertTrue("Document permissions difference in rest-reader permission",
				actualPermissions.contains("rest-reader:[READ]"));
		//Split up rest-writer:[READ, EXECUTE, UPDATE] string
	      String[] writerPerms = actualPermissions.split("rest-writer:\\[")[1].split("\\]")[0].split(",");
	      
	      assertTrue("Document permissions difference in rest-writer permission - first permission",
	    		  writerPerms[0].contains("UPDATE")||writerPerms[1].contains("UPDATE")||writerPerms[2].contains("UPDATE"));
	      assertTrue("Document permissions difference in rest-writer permission - second permission",
	    		  writerPerms[0].contains("EXECUTE")||writerPerms[1].contains("EXECUTE")||writerPerms[2].contains("EXECUTE"));
	      assertTrue("Document permissions difference in rest-writer permission - third permission",
	    		  writerPerms[0].contains("READ")||writerPerms[1].contains("READ")||writerPerms[2].contains("READ"));
		assertTrue(
				"Document permissions difference in app-user permissions",
				(actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions
						.contains("app-user:[READ, UPDATE]")));

		// Collections
		String actualCollections = getDocumentCollectionsString(collections);
		System.out.println(collections);

		assertTrue("Document collections difference in size value",
				actualCollections.contains("size:2"));
		assertTrue("my-collection1 not found",
				actualCollections.contains("my-collection1"));
		assertTrue("my-collection2 not found",
				actualCollections.contains("my-collection2"));
	}
	
	public DocumentMetadataHandle setMetadata() {
		// create and initialize a handle on the meta-data
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		metadataHandle.getCollections().addAll("my-collection1",
				"my-collection2");
		metadataHandle.getPermissions().add("app-user", Capability.UPDATE,
				Capability.READ);
		metadataHandle.getProperties().put("reviewed", true);
		metadataHandle.getProperties().put("myString", "foo");
		metadataHandle.getProperties().put("myInteger", 10);
		metadataHandle.getProperties().put("myDecimal", 34.56678);
		metadataHandle.getProperties().put("myCalendar",
				Calendar.getInstance().get(Calendar.YEAR));
		metadataHandle.setQuality(23);
		return metadataHandle;
	}
	
	void validateReturnTypes(EvalResultIterator evr) throws Exception {

		while (evr.hasNext()) {
			EvalResult er = evr.next();			
			if (er.getType().equals(Type.JSON)) {

				JacksonHandle jh = new JacksonHandle();
				jh = er.get(jh);

				if (jh.get().isArray()) {
					System.out.println("Type Array :" + jh.get().toString());
					assertEquals("array value at index 0 ", 1, jh.get().get(0)
							.asInt());
					assertEquals("array value at index 1 ", 2, jh.get().get(1)
							.asInt());
					assertEquals("array value at index 2 ", 3, jh.get().get(2)
							.asInt());
				} else if (jh.get().isObject()) {
					System.out.println("Type Object :" + jh.get().toString());
					if (jh.get().has("foo")) {
						assertNull("this object also has null node", jh.get()
								.get("testNull").textValue());
					} else if (jh.get().has("obj")) {
						assertEquals("Value of the object is ", "value", jh
								.get().get("obj").asText());
					} else {
						assertFalse("getting a wrong object ", true);
					}

				} else if (jh.get().isNumber()) {
					System.out.println("Type Number :" + jh.get().toString());
					assertEquals("Number value", 1, jh.get().asInt());
				} else if (jh.get().isNull()) {
					System.out.println("Type Null :" + jh.get().toString());
					assertNull("Returned Null", jh.get().textValue());
				} else if (jh.get().isBoolean()) {
					System.out.println("Type boolean :" + jh.get().toString());
					assertTrue("Boolean value returned false", jh.get()
							.asBoolean());
				} else {
					// System.out.println("Running into different types than expected");
					assertFalse("Running into different types than expected",
							true);
				}

			} else if (er.getType().equals(Type.TEXTNODE)) {
				assertTrue("document contains",
						er.getAs(String.class).equals("test1"));
				// System.out.println("type txt node :"+er.getAs(String.class));

			} else if (er.getType().equals(Type.BINARY)) {
				FileHandle fh = new FileHandle();
				fh = er.get(fh);
				// System.out.println("type binary :"+fh.get().length());
				assertEquals("files size", 2, fh.get().length());
			} else if (er.getType().equals(Type.BOOLEAN)) {
				assertTrue("Documents exist?", er.getBoolean());
				// System.out.println("type boolean:"+er.getBoolean());
			} else if (er.getType().equals(Type.INTEGER)) {
				System.out.println("type Integer: "
						+ er.getNumber().longValue());
				assertEquals("count of documents ", 31, er.getNumber()
						.intValue());
			} else if (er.getType().equals(Type.STRING)) {
				// There is git issue 152
				System.out.println("type string: " + er.getString());
				assertTrue("String?",er.getString().contains("true")||er.getString().contains("xml")
						||er.getString().contains("31") ||er.getString().contains("1.0471975511966"));

			} else if (er.getType().equals(Type.NULL)) {
				// There is git issue 151
				// assertNull(er.getAs(String.class));
				System.out.println("Testing is empty sequence is NUll?"
						+ er.getAs(String.class));
			} else if (er.getType().equals(Type.OTHER)) {
				// There is git issue 151
				System.out.println("Testing is Others? "
						+ er.getAs(String.class));
				// assertEquals("Returns OTHERs","xdmp:forest-restart#1",er.getString());

			} else if (er.getType().equals(Type.ANYURI)) {
				// System.out.println("Testing is AnyUri? "+er.getAs(String.class));
				assertEquals("Returns me a uri :", "test1.xml",
						er.getAs(String.class));

			} else if (er.getType().equals(Type.DATE)) {
				// System.out.println("Testing is DATE? "+er.getAs(String.class));
				assertEquals("Returns me a date :", "2002-03-07-07:00",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.DATETIME)) {
				// System.out.println("Testing is DATETIME? "+er.getAs(String.class));
				assertEquals("Returns me a dateTime :",
						"2010-01-06T18:13:50.874-07:00", er.getAs(String.class));

			} else if (er.getType().equals(Type.DECIMAL)) {
				// System.out.println("Testing is Decimal? "+er.getAs(String.class));
				assertEquals("Returns me a Decimal :", "1.0471975511966",
						er.getAs(String.class));

			} else if (er.getType().equals(Type.DOUBLE)) {
				// System.out.println("Testing is Double? "+er.getAs(String.class));
				assertEquals(1.0471975511966, er.getNumber().doubleValue(), 0);

			} else if (er.getType().equals(Type.DURATION)) {
				System.out.println("Testing is Duration? "
						+ er.getAs(String.class));
				// assertEquals("Returns me a Duration :",0.4903562,er.getNumber().floatValue());
			} else if (er.getType().equals(Type.FLOAT)) {
				// System.out.println("Testing is Float? "+er.getAs(String.class));
				assertEquals(20, er.getNumber().floatValue(), 0);
			} else if (er.getType().equals(Type.GDAY)) {
				// System.out.println("Testing is GDay? "+er.getAs(String.class));
				assertEquals("Returns me a GDAY :", "---01",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.GMONTH)) {
				// System.out.println("Testing is GMonth "+er.getAs(String.class));
				assertEquals("Returns me a GMONTH :", "--01",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.GMONTHDAY)) {
				// System.out.println("Testing is GMonthDay? "+er.getAs(String.class));
				assertEquals("Returns me a GMONTHDAY :", "--12-25-14:00",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.GYEAR)) {
				// System.out.println("Testing is GYear? "+er.getAs(String.class));
				assertEquals("Returns me a GYEAR :", "2005-12:00",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.GYEARMONTH)) {
				// System.out.println("Testing is GYearMonth?1976-02 "+er.getAs(String.class));
				assertEquals("Returns me a GYEARMONTH :", "1976-02",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.HEXBINARY)) {
				// System.out.println("Testing is HEXBINARY? "+er.getAs(String.class));
				assertEquals("Returns me a HEXBINARY :", "BEEF",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.QNAME)) {
				// System.out.println("Testing is QNAME integer"+er.getAs(String.class));
				assertEquals("Returns me a QNAME :", "integer",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.TIME)) {
				// System.out.println("Testing is TIME? "+er.getAs(String.class));
				assertEquals("Returns me a TIME :", "10:00:00",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.ATTRIBUTE)) {
				// System.out.println("Testing is ATTRIBUTE? "+er.getAs(String.class));
				assertEquals("Returns me a ATTRIBUTE :", "attribute",
						er.getAs(String.class));

			} else if (er.getType().equals(Type.PROCESSINGINSTRUCTION)) {
				// System.out.println("Testing is ProcessingInstructions? "+er.getAs(String.class));
				assertEquals("Returns me a PROCESSINGINSTRUCTION :",
						"<?processing instruction?>", er.getAs(String.class));
			} else if (er.getType().equals(Type.COMMENT)) {
				// System.out.println("Testing is Comment node? "+er.getAs(String.class));
				assertEquals("Returns me a COMMENT :", "<!--comment-->",
						er.getAs(String.class));
			} else if (er.getType().equals(Type.BASE64BINARY)) {
				// System.out.println("Testing is Base64Binary  "+er.getAs(String.class));
				assertEquals("Returns me a BASE64BINARY :", "DEADBEEF",
						er.getAs(String.class));
			} else {
				System.out
						.println("Got something which is not belongs to anytype we support "
								+ er.getAs(String.class));
				assertFalse("getting in else part, missing a type  ", true);
			}
		}
	}
	
	// Below scenario is to test the geoPair -130 
	
		//This test is to verify GeoPair query works fine, 
		// searching for lattitude and longitude of Reno
		@Test
		public void testPOJOGeoQuerySearchWithGeoPair() {
			System.out.println("Running testPOJOGeoQuerySearchWithGeoPair method");
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
			do {
				count =0;
				p = products.search(qd,pageNo);
				while(p.hasNext()) {
					GeoSpecialArtifact a =p.next();
					validateArtifact(a);
					assertTrue("Verifying document id is part of the search ids",a.getId()%5==0);
					assertEquals("Verifying Manufacurer is from state ","Reno",a.getManufacturer().getState());
					count++;
				}
				assertEquals("Page size",count,p.size());
				pageNo=pageNo+p.getPageSize();
			} while(!p.isLastPage() && pageNo<=p.getTotalSize());
			assertEquals("page number after the loop",3,p.getPageNumber());
			assertEquals("total no of pages",3,p.getTotalPages());
			assertEquals("page length from search handle",5,jh.get().path("page-length").asInt());
			assertEquals("Total results from search handle",11,jh.get().path("total").asInt());		
		}
	
	@Test  
	public void testWriteTextDoc() {
		System.out.println("Running testWriteTextDoc method");
		String docId = "/foo/test/myFoo.txt";
		TextDocumentManager docMgr = client.newTextDocumentManager();
		docMgr.write(docId, new StringHandle().with("This is so foo"));
		assertEquals("Text document write difference", "This is so foo", docMgr.read(docId, new StringHandle()).get());
	}
	
	@Test
	public void testBinaryCRUD() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException {		
		System.out.println("Running testBinaryCRUD");
		String filename = "Pandakarlino.jpg";
		String uri = "/write-bin-Bytehandle/";
		
		// write docs
		writeDocumentUsingBytesHandle(client, filename, uri, "Binary");
		
		// read docs
		BytesHandle contentHandle = readDocumentUsingBytesHandle(client, uri + filename,"Binary");
		
		// get the contents
		byte[] fileRead = contentHandle.get();
		
		// get the binary size
		long size = getBinarySizeFromByte(fileRead);
		long expectedSize = 17154;
		
		assertEquals("Binary size difference", expectedSize, size);

		// update the doc
	    // acquire the content for update
	    String updateFilename = "mlfavicon.png";
	    updateDocumentUsingByteHandle(client, updateFilename, uri + filename, "Binary");
	    
	    // read the document 
	    BytesHandle updateHandle = readDocumentUsingBytesHandle(client, uri+filename, "Binary");
		 
	    // get the contents
	    byte[] fileReadUpdate = updateHandle.get();
	    
	    // get the binary size
		long sizeUpdate = getBinarySizeFromByte(fileReadUpdate);
		//long expectedSizeUpdate = 3290;
		long expectedSizeUpdate = 3322;
		assertEquals("Binary size difference", expectedSizeUpdate, sizeUpdate);
		
		// delete the document
	    deleteDocument(client, uri + filename, "Binary");

		// read the deleted document
	    String exception = "";
	    try {
	    	readDocumentUsingInputStreamHandle(client, uri + filename, "Binary");
	    } catch (Exception e) { exception = e.toString(); }
	    
	    String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /write-bin-Bytehandle/Pandakarlino.jpg";
	    assertEquals("Document is not deleted", expectedException, exception);
	}
   
	@Test
	public void testWriteMultipleJSONDocsFromStrings() throws KeyManagementException, NoSuchAlgorithmException, Exception {
		System.out.println("Running testWriteMultipleJSONDocsFromStrings");		
		String docId[] = { "/iphone.json", "/imac.json", "/ipad.json" };
		String json1 = new String("{ \"name\":\"iPhone 6\" , \"industry\":\"Mobile Phone\" , \"description\":\"New iPhone 6\"}");
		String json2 = new String("{ \"name\":\"iMac\" , \"industry\":\"Desktop\", \"description\":\"Air Book OS X\" }");
		String json3 = new String("{ \"name\":\"iPad\" , \"industry\":\"Tablet\", \"description\":\"iPad Mini\" }");
		
		DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		docMgr.setMetadataCategories(Metadata.ALL);
		DocumentWriteSet writeset = docMgr.newWriteSet();
		// put meta-data
		DocumentMetadataHandle mh = setMetadata();
		
		JacksonDatabindHandle<String> handle1 = new JacksonDatabindHandle<String>(String.class);
		JacksonDatabindHandle<String> handle2 = new JacksonDatabindHandle<String>(String.class);
		JacksonDatabindHandle<String> handle3 = new JacksonDatabindHandle<String>(String.class);
		
		writeset.addDefault(mh);
		handle1.set(json1);
		handle2.set(json2);
		handle3.set(json3);
		
		writeset.add(docId[0], handle1);
		writeset.add(docId[1], handle2);
		writeset.add(docId[2], handle3);

		docMgr.write(writeset);
		
		//Read it back into JacksonDatabindHandle Product
		JacksonDatabindHandle<Product> jacksonDBReadHandle = new JacksonDatabindHandle<Product>(Product.class);
		docMgr.read(docId[0], jacksonDBReadHandle);
		Product product1 = (Product) jacksonDBReadHandle.get();
				
		assertTrue("Did not return a iPhone 6", product1.getName().equalsIgnoreCase("iPhone 6"));
		assertTrue("Did not return a Mobile Phone", product1.getIndustry().equalsIgnoreCase("Mobile Phone"));
		assertTrue("Did not return a Mobile Phone", product1.getDescription().equalsIgnoreCase("New iPhone 6"));
		
		docMgr.readMetadata(docId[0], mhRead);
		validateMetadata(mhRead);					
		
		docMgr.read(docId[1],jacksonDBReadHandle);
		Product product2 = (Product) jacksonDBReadHandle.get();
		assertTrue("Did not return a iMac", product2.getName().equalsIgnoreCase("iMac"));
		assertTrue("Did not return a Desktop", product2.getIndustry().equalsIgnoreCase("Desktop"));
		assertTrue("Did not return a Air Book OS X", product2.getDescription().equalsIgnoreCase("Air Book OS X"));
		
		docMgr.readMetadata(docId[1], mhRead);
		validateMetadata(mhRead);			
		
		docMgr.read(docId[2], jacksonDBReadHandle);
		Product product3 = (Product) jacksonDBReadHandle.get();
		assertTrue("Did not return a iPad", product3.getName().equalsIgnoreCase("iPad"));
		assertTrue("Did not return a Tablet", product3.getIndustry().equalsIgnoreCase("Tablet"));
		assertTrue("Did not return a iPad Mini", product3.getDescription().equalsIgnoreCase("iPad Mini"));
		
		docMgr.readMetadata(docId[2], mhRead);
		validateMetadata(mhRead);					
	}
	
	@Test	
	public void testXmlCRUD() throws KeyManagementException, NoSuchAlgorithmException, IOException,  SAXException, ParserConfigurationException, TransformerException, XMLStreamException
	{	
		String filename = "xml-original-test.xml";
		String uri = "/write-xml-XMLStreamReaderHandle/";

		System.out.println("Running testXmlCRUD");
		// write the doc
		writeDocumentReaderHandle(client, filename, uri, "XML");

		// read the document
		XMLStreamReaderHandle readHandle = readDocumentUsingXMLStreamReaderHandle(client, uri + filename, "XML");

		// access the document content
		XMLStreamReader fileRead = readHandle.get();
		String readContent = convertXMLStreamReaderToString(fileRead);

		// get xml document for expected result
		Document expectedDoc = expectedXMLDocument(filename);
		String expectedContent = convertXMLDocumentToString(expectedDoc);
		expectedContent = "null"+expectedContent.substring(expectedContent.indexOf("<name>")+6, expectedContent.indexOf("</name>"));
		assertEquals("Write XML difference", expectedContent,readContent);	    

		// delete the document
		deleteDocument(client, uri + filename, "XML");

		String exception = "";
		try {
			readDocumentReaderHandle(client, uri + filename, "XML");
		} 
		catch (Exception e) { exception = e.toString(); }

		String expectedException = "Could not read non-existent document";
		boolean documentIsDeleted = exception.contains(expectedException);
		assertTrue("Document is not deleted", documentIsDeleted);
	}
	
	@Test	
	public void testRollbackDeleteDocument() throws KeyManagementException, NoSuchAlgorithmException, ParserConfigurationException, SAXException, IOException
	{	
		System.out.println("Running testRollbackDeleteDocument");

		String filename = "bbq1.xml";
		String uri = "/tx-rollback/";
		File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

		// create transaction 1
		Transaction transaction1 = client.openTransaction();

		// create a manager for document
		DocumentManager docMgr = client.newDocumentManager();

		// create an identifier for the document
		String docId = uri + filename;

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		handle.setFormat(Format.XML);

		// write the document content
		docMgr.write(docId, handle, transaction1);

		// commit transaction
		transaction1.commit();		

		// create transaction 2
		Transaction transaction2 = client.openTransaction();

		// delete document
		docMgr.delete(docId, transaction2);

		transaction2.rollback();

		// read document
		FileHandle readHandle = new FileHandle();
		docMgr.read(docId, readHandle);
		File fileRead = readHandle.get();
		String readContent = convertFileToString(fileRead);

		// get xml document for expected result
		Document expectedDoc = expectedXMLDocument(filename);

		// convert actual string to xml doc
		Document readDoc = convertStringToXMLDocument(readContent);

		assertXMLEqual("Rollback on document delete failed", expectedDoc, readDoc);
	}
	
	@Test	
	public void testDocumentQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testDocumentQuery");

		String[] filenames1 = {"constraint1.xml", "constraint2.xml", "constraint3.xml"};
		String[] filenames2 = {"constraint4.xml", "constraint5.xml"};
		String queryOptionName = "valueConstraintWildCardOpt.xml";

		// set query option validation to true
		ServerConfigurationManager srvMgr = client.newServerConfigManager();
		srvMgr.readConfiguration();
		srvMgr.setQueryOptionValidation(true);
		srvMgr.writeConfiguration();

		// write docs
		for(String filename1 : filenames1) {
			writeDocumentUsingInputStreamHandle(client, filename1, "/dir1/dir2/", "XML");
		}

		// write docs
		for(String filename2 : filenames2) {
			writeDocumentUsingInputStreamHandle(client, filename2, "/dir3/dir4/", "XML");
		}

		setQueryOption(client, queryOptionName);

		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
		StructuredQueryDefinition termQuery = qb.term("Memex");
		StructuredQueryDefinition docQuery = qb.or(qb.document("/dir1/dir2/constraint2.xml"), qb.document("/dir3/dir4/constraint4.xml"), qb.document("/dir3/dir4/constraint5.xml"));
		StructuredQueryDefinition andFinalQuery = qb.and(termQuery, docQuery);

		// create handle
		DOMHandle resultsHandle = new DOMHandle();
		queryMgr.search(andFinalQuery, resultsHandle);

		// get the result
		Document resultDoc = resultsHandle.get();
		System.out.println(convertXMLDocumentToString(resultDoc));

		assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
		assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
		assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);	
	}
	
	@Test
	public void testBulkXQYTransformWithTrans() throws KeyManagementException, NoSuchAlgorithmException, Exception {
		System.out.println("Running testBulkXQYTransformWithTrans");
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
		for(int i = 0;i<12;i++) {

			writesetRollback.add(DIRECTORY+"fooWithTrans"+i+".xml", new DOMHandle(getDocumentContent("This is so foo"+i)));
			map.put(DIRECTORY+"fooWithTrans"+i+".xml", convertXMLDocumentToString(getDocumentContent("This is so foo"+i)));
			if(count%10 == 0) {
				docMgr.write(writesetRollback, transform, tRollback);
				writesetRollback = docMgr.newWriteSet();
			}
			count++;
		}
		if(count%10 > 0) {
			docMgr.write(writesetRollback, transform, tRollback);
		}
		String uris[] = new String[102];
		for(int i =0;i<102;i++) {
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
			for(int i =0;i<102;i++) {

				writeset.add(DIRECTORY+"fooWithTrans"+i+".xml", new DOMHandle(getDocumentContent("This is so foo"+i)));
				map.put(DIRECTORY+"fooWithTrans"+i+".xml", convertXMLDocumentToString(getDocumentContent("This is so foo"+i)));
				if(count%BATCH_SIZE == 0){
					docMgr.write(writeset, transform, tCommit);
					writeset = docMgr.newWriteSet();
				}
				count++;
			}
			if(count%BATCH_SIZE > 0) {
				docMgr.write(writeset, transform, tCommit);
			}
			tCommit.commit();			
			count=0;
			DocumentPage page = docMgr.read(uris);
			DOMHandle dh = new DOMHandle();
			 // To verify that transformation did run on all docs.
			String verifyAttrValue = null;
			while(page.hasNext()) {
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
	public void testRequestLogger() throws KeyManagementException, NoSuchAlgorithmException, IOException {	
		System.out.println("Running testRequestLogger");

		String filename = "bbq1.xml";
		String uri = "/request-logger/";

		File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

		// create transaction
		Transaction transaction = client.openTransaction();

		// create a manager for XML documents
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// create an identifier for the document
		String docId = uri + filename;

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);

		// create logger
		RequestLogger logger = client.newLogger(System.out);
		logger.setContentMax(RequestLogger.ALL_CONTENT);

		// start logging
		docMgr.startLogging(logger);

		// write the document content
		docMgr.write(docId, handle, transaction);

		// commit transaction
		transaction.commit();		

		// stop logging
		docMgr.stopLogging();

		String expectedContentMax = "9223372036854775807";
		assertEquals("Content log is not equal", expectedContentMax, Long.toString(logger.getContentMax()));
	}
	
	@Test
	public void testJSDifferentVariableTypesNoNullNodes() throws KeyManagementException, NoSuchAlgorithmException, Exception {
		System.out.println("Running testJSDifferentVariableTypesNoNullNodes");
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader("<foo attr=\"attribute\"><?processing instruction?><!--comment-->test1</foo>"));
		Document doc = db.parse(is);
		System.out.println(this.convertXMLDocumentToString(doc));
	 	try {
		String query1 = " var results = [];"
						+"var myString;"
						+"var myBool ;"
						+"var myInteger;"
						+"var myDecimal;"
						+"var myJsonObject;"
						+"var myNull;"
						+ "var myJsonArray;"						
						+ "results.push(myString,myBool,myInteger,myDecimal,myJsonObject,myJsonArray,myNull);"
						+"xdmp.arrayValues(results)";
		
		ServerEvaluationCall evl= client.newServerEval().javascript(query1);
		evl.addVariable("myString", "xml")
		.addVariable("myBool", true)
		.addVariable("myInteger", (int)31)
		.addVariable("myDecimal", (double)1.0471975511966)
		.addVariableAs("myJsonObject", new ObjectMapper().createObjectNode().put("foo", "v1").putNull("testNull"))
		.addVariableAs("myNull", (String) null)
		.addVariableAs("myJsonArray", new ObjectMapper().createArrayNode().add(1).add(2).add(3))
		;
		System.out.println(new ObjectMapper().createObjectNode().nullNode().toString());
		EvalResultIterator evr = evl.eval();
		this.validateReturnTypes(evr);
		
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Test	
	public void testRawAlert() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
	{	
		System.out.println("Running testRawAlert");

		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		// write docs
		for(String filename : filenames) {
			writeDocumentUsingInputStreamHandle(client, filename, "/raw-alert/", "XML");
		}

		// create a manager for configuring rules
		RuleManager ruleMgr = client.newRuleManager();

		// get the rule
		File file = new File("src/test/java/com/marklogic/client/functionaltest/rules/alertRule1.xml");

		// create a handle for the rule
		FileHandle writeHandle = new FileHandle(file);

		// write the rule to the database
		ruleMgr.writeRule("RULE-TEST-1", writeHandle);

		// create a manager for document search criteria
		QueryManager queryMgr = client.newQueryManager();

		// specify the search criteria for the documents
		String criteria = "atlantic";
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		querydef.setCriteria(criteria);

		// create a manager for matching rules
		RuleManager ruleMatchMgr = client.newRuleManager();

		// match the rules against the documents qualified by the criteria
		RuleDefinitionList matchedRules = ruleMatchMgr.match(querydef, new RuleDefinitionList());

		System.out.println(matchedRules.size());

		String expected = "";

		// iterate over the matched rules
		Iterator<RuleDefinition> ruleItr = matchedRules.iterator();
		while (ruleItr.hasNext()) {
			RuleDefinition rule = ruleItr.next();
			System.out.println(
					"document criteria "+criteria+" matched rule "+
							rule.getName()+" with metadata "+rule.getMetadata()
					);

			expected = expected + rule.getName() + " - " + rule.getMetadata() + " | ";
		}

		System.out.println(expected);
		assertTrue("incorrect rule", expected.contains("RULE-TEST-1 - {rule-number=one} |"));
	}
	
	// Access database on Uber port with specifying the database name.
	@Ignore	
	public void testUberClientWithDbName() throws IOException,  SAXException, ParserConfigurationException, KrbException {
		System.out.println("Running testUberClientWithDbName method");
		
		// Associate the external security with the App Server.
		try {
			createDB(UberdbName);
			createForest(UberfNames[0], UberdbName);
			
			setupAppServicesConstraint(UberdbName);
			
			associateRESTServerWithKerberosExtSecurity(UberrestServerName, extSecurityName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String filename = "xml-original-test.xml";
		String uri = "/write-xml-string/";		
		DatabaseClient clientUber = DatabaseClientFactory.newClient(getRestServerHostName(), 8000, UberdbName,  new KerberosAuthContext());				
		String exception = "";

		// write doc		
		writeDocumentUsingStringHandle(clientUber, filename, uri, "XML");		
		// read docs
		StringHandle contentHandle = readDocumentUsingStringHandle(clientUber, uri + filename, "XML");
		String readContent = contentHandle.get();

		// get xml document for expected result
		Document expectedDoc = expectedXMLDocument(filename);

		// convert actual string to xml doc
		Document readDoc = convertStringToXMLDocument(readContent);

		assertXMLEqual("Write XML difference", expectedDoc, readDoc);
		
		try {
			// Associate back to Digest on App-Services on port 8000.
			associateRESTServerWithDigestAuth(UberrestServerName);
			// Associate database to Documentst on App-Services.
			associateRESTServerWithDB(UberrestServerName, "Documents");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		deleteDB(UberdbName);
		deleteForest(UberfNames[0]);
		// release client		
		clientUber.release();
	}
	
	// Test DatabaseCLient with Basic Auth DIGEST auth type. Should expect an Exception.
	@Test
	public void testWriteWithBasicAuthDigest() {
		System.out.println("Running testWriteWithBasicAuthDigest method");
		String docId[] = { "/foo/test/myFoo1.txt", "/foo/test/myFoo2.txt",
		"/foo/test/myFoo3.txt" };
		String exception = "";
		DatabaseClient clientDigest = null;

		// create new connection for each test below

		try {
			clientDigest = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), new BasicAuthContext("rest-admin", "x"));

			TextDocumentManager docMgr = clientDigest.newTextDocumentManager();
			DocumentWriteSet writeset = docMgr.newWriteSet();

			writeset.add(docId[0], new StringHandle().with("This is so foo1"));
			writeset.add(docId[1], new StringHandle().with("This is so foo2"));
			writeset.add(docId[2], new StringHandle().with("This is so foo3"));

			docMgr.write(writeset);

			assertEquals("Text document write difference", "This is so foo1",
					docMgr.read(docId[0], new StringHandle()).get());
			assertEquals("Text document write difference", "This is so foo2",
					docMgr.read(docId[1], new StringHandle()).get());
			assertEquals("Text document write difference", "This is so foo3",
					docMgr.read(docId[2], new StringHandle()).get());

			// Bulk delete on TextDocumentManager
			docMgr.delete(docId[0], docId[1], docId[2]);
		} catch (Exception e) {
			exception = e.toString();
			System.out.println("Exception in Running testWriteWithDigest method");
			System.out.println("Exception is" + exception);
		}
		String expectedException = "com.marklogic.client.FailedRequestException: Local message: failed to apply resource at documents: Unauthorized. Server Message: Unauthorized";		                            
		boolean exceptionIsThrown = exception.contains(expectedException);
		assertTrue("Exception is not thrown", exceptionIsThrown);

		clientDigest.release();
	}
}
