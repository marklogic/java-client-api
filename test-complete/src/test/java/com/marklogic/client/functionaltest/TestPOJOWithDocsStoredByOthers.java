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

import java.util.Calendar;

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
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.pojo.annotation.Id;

public class TestPOJOWithDocsStoredByOthers extends BasicJavaClientREST {

	private static String dbName = "TestPOJOWithDocsStoredByOthersDB";
	private static String[] fNames = { "TestPOJOWithDocsStoredByOthersDB-1" };
	private static String restServerName = "REST-Java-Client-API-Server";

	private static int restPort = 8011;
	private DatabaseClient client;

	/*
	 * This class is used as a POJO class to read documents stored as a JSON.
	 * Class member name has been annotated with @Id. 
	 * annotated.
	 */
	public static class SmallArtifactIdInSuper {
		@Id
		public String name;
		private long id;
		private int inventory;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public int getInventory() {
			return inventory;
		}

		public void setInventory(int inventory) {
			this.inventory = inventory;
		}
	}
	
	/*
	 * This class is used as a POJO class to read documents stored as a POJO.
	 * Class member name has been annotated with @Id in the super class. 
	 * Has an additional string member.
	 */
	public static class SmallArtifactNoId extends SmallArtifactIdInSuper {
		private String originCountry;

		public String getOriginCountry() {
			return originCountry;
		}

		public void setOriginCountry(String originCountry) {
			this.originCountry = originCountry;
		}
		
	}
	
	/*
	 * This class is used as a POJO class to read documents stored as a POJO.
	 * Class member name has been annotated with @Id in the super class and also in this class. 
	 * Has an additional string member.
	 */
	public static class SmallArtifactIdInSuperAndSub extends SmallArtifactIdInSuper {
		@Id
		public String name;
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		private String originCountry;

		public String getOriginCountry() {
			return originCountry;
		}

		public void setOriginCountry(String originCountry) {
			this.originCountry = originCountry;
		}
		
	}
	
	/*
	 * This class is used as a POJO class to read documents stored as a JSON.
	 * Used to test different access specifiers
	 * Private Class member name has been annotated with @Id. 
	 * annotated.
	 */
	public static class SmallArtifactPrivate {
		private String name;
		private long id;
		private int inventory;
		@Id
		public String getName() {
			return name;
		}
		@Id
		public void setName(String name) {
			this.name = name;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public int getInventory() {
			return inventory;
		}

		public void setInventory(int inventory) {
			this.inventory = inventory;
		}
	}
	
	/*
	 * This class is used as a POJO class to read documents stored as a JSON.
	 * Used to test different access specifiers
	 * Private Class member name has been annotated with @Id. 
	 * annotated.
	 */
	public static class SmallArtifactPublic extends  SmallArtifactPrivate {
		public String name;
		private String originCountry;
        
		@Id
		public String getName() {
			return name;
		}
       
		@Id
		public void setName(String name) {
			this.name = name;
		}
		

		public String getOriginCountry() {
			return originCountry;
		}

		public void setOriginCountry(String originCountry) {
			this.originCountry = originCountry;
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire",
		// "debug");
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName, restPort);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}

	@Before
	public void setUp() throws Exception {
		client = DatabaseClientFactory.newClient("localhost", restPort,
				"rest-admin", "x", Authentication.DIGEST);
	}

	@After
	public void tearDown() throws Exception {
		// release client
		client.release();
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

	/*
	 * This method is used when there is a need to validate SmallArtifact.
	 */
	public void validateSmallArtifact(SmallArtifactIdInSuper artifact) {
		assertNotNull("Artifact object should never be Null", artifact);
		assertNotNull("Id should never be Null", artifact.id);
		assertEquals("Id of the object is ", -99, artifact.getId());
		assertEquals("Name of the object is ", "SmallArtifact",
				artifact.getName());
		assertEquals("Inventory of the object is ", 1000,
				artifact.getInventory());
	}
	
	/*
	 * This method is used when there is a need to validate SmallArtifactSuper to test annotation only in super class.
	 */
	public void validateSmallArtifactSuper(SmallArtifactNoId artifact) {
		assertNotNull("Artifact object should never be Null", artifact);
		assertNotNull("Id should never be Null", artifact.getId());
		assertEquals("Id of the object is ", 0, artifact.getId());
		assertEquals("Name of the object is ", "SmallArtifactInSuperOnly",
				artifact.getName());
		assertEquals("Inventory of the object is ", 1000,
				artifact.getInventory());
		assertEquals("Country of origin of the object is ", "USA",
				artifact.getOriginCountry());
	}
	
	/*
	 * This method is used when there is a need to validate SmallArtifactSuperAndSub to test annotation in super class
	 * and in sub class.
	 */
	public void validateSmallArtifactSuperAndSub(SmallArtifactIdInSuperAndSub artifact) {
		assertNotNull("Artifact object should never be Null", artifact);
		assertNotNull("Id should never be Null", artifact.getId());
		assertEquals("Id of the object is ", -100, artifact.getId());
		assertEquals("Name of the object is ", "SmallArtifactInSuperAndSub",
				artifact.getName());
		assertEquals("Inventory of the object is ", 1000,
				artifact.getInventory());
		assertEquals("Country of origin of the object is ", "USA",
				artifact.getOriginCountry());
	}
	
	/*
	 * This method is used when there is a need to validate SmallArtifactPublic to test annotation in super class
	 * and in sub class.
	 */
	public void validateSmallArtifactDiffAccessSpec(SmallArtifactPublic artifact) {
		assertNotNull("Artifact object should never be Null", artifact);
		assertNotNull("Id should never be Null", artifact.getId());
		assertEquals("Id of the object is ", -100, artifact.getId());
		assertEquals("Name of the object is ", "SmallArtifactDiffAccess",
				artifact.getName());
		assertEquals("Inventory of the object is ", 1000,
				artifact.getInventory());
		assertEquals("Country of origin of the object is ", "USA",
				artifact.getOriginCountry());
	}
	
	
	/*
	 * This method is used when there is a need to validate SmallArtifactIdInSuper to test annotation in super class
	 * and in sub class.
	 */
	public void validateSubObjReferencedbySuperClassvariable(SmallArtifactIdInSuper artifact) {
		assertNotNull("Artifact object should never be Null", artifact);
		assertNotNull("Id should never be Null", artifact.getId());
		assertEquals("Id of the object is ", -100, artifact.getId());
		assertEquals("Name of the object is ", "SmallArtifactNoId",
				artifact.getName());
		assertEquals("Inventory of the object is ", 1000,
				artifact.getInventory());		
	}
	
	/*
	 * This method is used when there is a need to validate SmallArtifactIdInSuperAndSub to test annotation in super class
	 * and in sub class.
	 */
	public void validateSubObjReferencedbySuperClassvariableOne(SmallArtifactIdInSuperAndSub artifact) {
		assertNotNull("Artifact object should never be Null", artifact);
		assertNotNull("Id should never be Null", artifact.getId());
		assertEquals("Id of the object is ", -100, artifact.getId());
		assertEquals("Name of the object is ", "SmallArtifactIdInSuperAndSub",
				artifact.getName());
		assertEquals("Inventory of the object is ", 1000,
				artifact.getInventory());		
	}

	/*
	 * Purpose : This test is to validate read documents with valid POJO
	 * specific URI and has invalid POJO collection Uses SmallArtifact class
	 * which has @Id on the name methods. Test result expectations are: read
	 * should return a null since the POJO internal content and document's
	 * are different.
	 * 
	 * Current results (10/13/2014) are: 
	 * java.lang.IllegalArgumentException: Invalid type id 'junk' (for id type 'Id.class'): no such class found
	 * Issue 136 might solve this also.
	 */

	@Test(expected=IllegalArgumentException.class)
	public void testPOJOReadDocStoredWithInvalidContent() throws Exception {

		String docId[] = { "com.marklogic.client.functionaltest.TestPOJOWithDocsStoredByOthers$SmallArtifactIdInSuper/SmallArtifactIdInSuper.json" };
		String json1 = new String(
				"{\"junk\":"
						+ "{\"name\": \"SmallArtifactIdInSuper\",\"id\": -99, \"inventory\": 1000}}");
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		docMgr.setMetadataCategories(Metadata.ALL);
		DocumentWriteSet writeset = docMgr.newWriteSet();
		// put meta-data
		DocumentMetadataHandle mh = setMetadata();

		ObjectMapper mapper = new ObjectMapper();

		JacksonHandle jacksonHandle1 = new JacksonHandle();

		JsonNode junkNode = mapper.readTree(json1);
		jacksonHandle1.set(junkNode);
		jacksonHandle1.withFormat(Format.JSON);

		writeset.addDefault(mh);
		writeset.add(docId[0], jacksonHandle1);

		docMgr.write(writeset);

		PojoRepository<SmallArtifactIdInSuper, String> pojoReposSmallArtifact = client
				.newPojoRepository(SmallArtifactIdInSuper.class, String.class);
		String artifactName = new String("SmallArtifactIdInSuper");

		SmallArtifactIdInSuper artifact1 = pojoReposSmallArtifact.read(artifactName);
		validateSmallArtifact(artifact1);
	}

	/*
	 * Purpose : This test is to validate read documents with valid POJO
	 * specific URI, and type and has missing POJO fields Uses SmallArtifact
	 * class which has @Id on the name methods. Inventory bean property is
	 * missing from document insert. Test result expectations: The POJO object
	 * returned should be defaulting to Java default for the type (int) for
	 * inventory field.
	 */
	@Test
	public void testPOJOReadDocStoredWithNoBeanProperty() throws Exception {

		String docId[] = { "com.marklogic.client.functionaltest.TestPOJOWithDocsStoredByOthers$SmallArtifactIdInSuper/SmallArtifactIdInSuper.json" };
		String json1 = new String(
				"{\"com.marklogic.client.functionaltest.TestPOJOWithDocsStoredByOthers$SmallArtifactIdInSuper\":"
						+ "{\"name\": \"SmallArtifactIdInSuper\",\"id\": -99}}");

		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		docMgr.setMetadataCategories(Metadata.ALL);
		DocumentWriteSet writeset = docMgr.newWriteSet();
		// put meta-data
		DocumentMetadataHandle mh = setMetadata();

		ObjectMapper mapper = new ObjectMapper();

		JacksonHandle jacksonHandle1 = new JacksonHandle();

		JsonNode noInventoryNode = mapper.readTree(json1);
		jacksonHandle1.set(noInventoryNode);
		jacksonHandle1.withFormat(Format.JSON);

		writeset.addDefault(mh);
		writeset.add(docId[0], jacksonHandle1);

		docMgr.write(writeset);

		PojoRepository<SmallArtifactIdInSuper, String> pojoReposSmallArtifact = client
				.newPojoRepository(SmallArtifactIdInSuper.class, String.class);
		String artifactName = new String("SmallArtifactIdInSuper");

		// Validate the SmallArtifactIdInSuper read back.
		SmallArtifactIdInSuper artifact = pojoReposSmallArtifact.read(artifactName);
		assertNotNull("Artifact object should never be Null", artifact);
		assertNotNull("Id should never be Null", artifact.id);
		assertEquals("Id of the object is ", -99, artifact.getId());
		assertEquals("Name of the object is ", "SmallArtifactIdInSuper",
				artifact.getName());
		assertEquals("Inventory of the object is ", 0, artifact.getInventory());
	}

	/*
	 * Purpose : This test is to validate read documents with valid POJO
	 * specific URI and has invalid data-types for one of the bean property.
	 * Uses SmallArtifact class which has @Id on the name methods. Test result
	 * expectations are: read should return ResourceNotFoundException exception.
	 * Field inventory has a String 
	 */

	@Test(expected=ResourceNotFoundException.class)
	public void testPOJOReadDocStoredWithInvalidDataType() throws Exception {

		String docId[] = { "com.marklogic.client.functionaltest.TestPOJOWithDocsStoredByOthers$SmallArtifact/SmallArtifact.json" };
		String json1 = new String(
				"{\"com.marklogic.client.functionaltest.TestPOJOWithDocsStoredByOthers$SmallArtifact\":"
						+ "{\"name\": \"SmallArtifact\",\"id\": -99, \"inventory\": \"String\"}}");
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		docMgr.setMetadataCategories(Metadata.ALL);
		DocumentWriteSet writeset = docMgr.newWriteSet();
		// put meta-data
		DocumentMetadataHandle mh = setMetadata();
		// add to POJO URI collection. 
		mh.getCollections().addAll("com.marklogic.client.functionaltest.TestPOJOWithDocsStoredByOthers$SmallArtifact/SmallArtifact.json",
				                   "com.marklogic.client.functionaltest.TestPOJOWithDocsStoredByOthers$SmallArtifact/SmallArtifact.json");

		ObjectMapper mapper = new ObjectMapper();

		JacksonHandle jacksonHandle1 = new JacksonHandle();

		JsonNode invalidDataTypeNode = mapper.readTree(json1);
		jacksonHandle1.set(invalidDataTypeNode);
		jacksonHandle1.withFormat(Format.JSON);

		writeset.addDefault(mh);
		writeset.add(docId[0], jacksonHandle1);

		docMgr.write(writeset);

		PojoRepository<SmallArtifactIdInSuper, String> pojoReposSmallArtifact = client
				.newPojoRepository(SmallArtifactIdInSuper.class, String.class);
		String artifactName = new String("SmallArtifact");

		@SuppressWarnings("unused")
		SmallArtifactIdInSuper artifact1 = pojoReposSmallArtifact.read(artifactName);	
	}
	
	/*
	 * Purpose : This test is to validate Test creating an @id in super class only
	 * SmallArtifactSuper class which has @Id only on the name class member of the super class SmallArtifact. 
	 * 
	 * Current results (10/13/2014) are: 
	 * java.lang.IllegalArgumentException: 
	 * Your class com.marklogic.client.functionaltest.TestPOJOWithDocsStoredByOthers$SmallArtifactSuper does not have a method or field annotated with com.marklogic.client.pojo.annotation.Id
	 * 
	 */
	@Test
	public void testPOJOWriteReadSuper() throws Exception {
		
	PojoRepository<SmallArtifactNoId, String> pojoReposSmallArtifact = client
				.newPojoRepository(SmallArtifactNoId.class, String.class);
		String artifactName = new String("SmallArtifactInSuperOnly");

		SmallArtifactNoId art = new SmallArtifactNoId();
		art.setId(0);
		art.setInventory(1000);
		art.setName(artifactName);
		art.setOriginCountry("USA");
		
		// Load the object into database
		pojoReposSmallArtifact.write(art,"com.marklogic.client.functionaltest.TestPOJOMissingIdGetSetMethod$SmallArtifactSuper/SmallArtifactSuper.json");
						
		SmallArtifactNoId artifact1 = pojoReposSmallArtifact.read(artifactName);
		assertTrue("GetId for String incorrect", "SmallArtifactInSuperOnly".equalsIgnoreCase(pojoReposSmallArtifact.getId(artifact1)));
		validateSmallArtifactSuper(artifact1);			
	}
	
	/*
	 * Purpose : This test is to validate Test creating an @id in super class and sub class.
	 * Both SmallArtifactSuperAndSub and SmallArtifact class have a similar @Id on the name class member. 
	 * 
	 * Current results (10/13/2014) are: Read works fine.
	 * 
	 */
	@Test
	public void testPOJOWriteReadSuperAndSub() throws Exception {
		
	PojoRepository<SmallArtifactIdInSuperAndSub, String> pojoReposSmallArtifact = client
				.newPojoRepository(SmallArtifactIdInSuperAndSub.class, String.class);
		String artifactName = new String("SmallArtifactInSuperAndSub");

		SmallArtifactIdInSuperAndSub art = new SmallArtifactIdInSuperAndSub();
		art.setId(-100);
		art.setInventory(1000);
		art.setName(artifactName);
		art.setOriginCountry("USA");
						
		// Load the object into database
		pojoReposSmallArtifact.write(art,"com.marklogic.client.functionaltest.TestPOJOMissingIdGetSetMethod$SmallArtifactSuper/SmallArtifactSuper.json");
						
		SmallArtifactIdInSuperAndSub artifact1 = pojoReposSmallArtifact.read(artifactName);
		assertEquals("Repository getId value incorrect", artifact1.getName(), pojoReposSmallArtifact.getId(artifact1).toString());
		validateSmallArtifactSuperAndSub(artifact1);			
	}
	
	/*
	 * Purpose : This test is to validate creating an @id in super class and sub class that has different
	 * access specifiers.
	 * Both SmallArtifactSuperAndSub and SmallArtifact class have a similar @Id on the name class member. 
	 * 
	 * Current results (10/13/2014) are: Read works fine.
	 * 
	 */
	@Test
	public void testPOJOWriteReadDiffAccessSpecifiers() throws Exception {
		
	PojoRepository<SmallArtifactPublic, String> pojoReposSmallArtifact = client
				.newPojoRepository(SmallArtifactPublic.class, String.class);
		String artifactName = new String("SmallArtifactDiffAccess");

		SmallArtifactPublic art = new SmallArtifactPublic();
		art.setId(-100);
		art.setInventory(1000);
		art.setName(artifactName);
		art.setOriginCountry("USA");
				
		// Load the object into database
		pojoReposSmallArtifact.write(art,"com.marklogic.client.functionaltest.TestPOJOMissingIdGetSetMethod$SmallArtifactSuper/SmallArtifactSuper.json");
						
		SmallArtifactPublic artifact1 = pojoReposSmallArtifact.read(artifactName);
		validateSmallArtifactDiffAccessSpec(artifact1);			
	}
	
	/*
	 * Purpose : This test is to validate creating an sub class which is referenced by a Super class variable type.
	 * Both SmallArtifactIdInSuper and SmallArtifactNoId classes are used.
	 * POJO repository cannot read back the sub class.
	 * 
	 *  PojoRepository is on the super class.
	 */
	
	@Test
	public void testPOJOSubObjReferencedBySuperClassVariable() throws Exception {
		
	PojoRepository<SmallArtifactIdInSuper, String> pojoReposSmallArtifact = client
				.newPojoRepository(SmallArtifactIdInSuper.class, String.class);
		String artifactName = new String("SmallArtifactNoId");

		SmallArtifactIdInSuper art = new SmallArtifactNoId();
		art.setId(-100);
		art.setInventory(1000);
		art.setName(artifactName);
				
		// Load the object into database
		pojoReposSmallArtifact.write(art,"SubClassObjectReferencedBySuperClassVariable");
		
		// POJO repository cannot read back the sub class. Compiler complains.
		SmallArtifactIdInSuper artifact1 = pojoReposSmallArtifact.read(artifactName);
		validateSubObjReferencedbySuperClassvariable(artifact1);			
	}
	
	/*
	 * Purpose : This test is to validate creating an sub class which is referenced by a Super class variable type.
	 * Both SmallArtifactIdInSuper and SmallArtifactNoId classes are used.
	 * This is a variation of testPOJOSubObjReferencedBySuperClassVariable()
	 * 
	 * PojoRepository is on the sub class.
	 */
	
	@Test
	public void testPOJOSubObjReferencedBySuperClassVariableOne() throws Exception {
		
	PojoRepository<SmallArtifactIdInSuperAndSub, String> pojoReposSmallArtifact = client
				.newPojoRepository(SmallArtifactIdInSuperAndSub.class, String.class);
		String artifactName = new String("SmallArtifactIdInSuperAndSub");

		SmallArtifactIdInSuper art = new SmallArtifactIdInSuperAndSub();
		art.setId(-100);
		art.setInventory(1000);
		art.setName(artifactName);
				
		// Load the object into database
		// POJO repository cannot write using super class reference class. Needs an explicit cast else compiler complains.
		pojoReposSmallArtifact.write((SmallArtifactIdInSuperAndSub)art,"SubClassObjectReferencedBySuperClassVariableOne");
				
		SmallArtifactIdInSuperAndSub artifact1 = pojoReposSmallArtifact.read(artifactName);
		validateSubObjReferencedbySuperClassvariableOne(artifact1);			
	}
	
}
