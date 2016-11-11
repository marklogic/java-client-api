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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import javax.xml.bind.DatatypeConverter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentMetadataPatchBuilder;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonDatabindHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;

public class TestBiTempMetaValues extends BasicJavaClientREST {

	private static String dbName = "TestBiTempMetaValues";
	private static String[] fNames = { "TestBiTempMetaValues-1" };

	private DatabaseClient writerClient = null;

	private final static String dateTimeDataTypeString = "dateTime";

	private final static String systemStartERIName = "javaSystemStartERI";
	private final static String systemEndERIName = "javaSystemEndERI";
	private final static String validStartERIName = "javaValidStartERI";
	private final static String validEndERIName = "javaValidEndERI";

	private final static String axisSystemName = "javaERISystemAxis";
	private final static String axisValidName = "javaERIValidAxis";

	private final static String temporalCollectionName = "javaERITemporalCollection";
	private final static String bulktemporalCollectionName = "bulkjavaERITemporalCollection";
	private final static String temporalLsqtCollectionName = "javaERILsqtTemporalCollection";

	private final static String systemNodeName = "System";
	private final static String validNodeName = "Valid";
	private final static String addressNodeName = "Address";
	private final static String uriNodeName = "uri";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("In setup");
		configureRESTServer(dbName, fNames);

		ConnectedRESTQA.addRangeElementIndex(dbName, dateTimeDataTypeString, "",
				systemStartERIName);
		ConnectedRESTQA.addRangeElementIndex(dbName, dateTimeDataTypeString, "",
				systemEndERIName);
		ConnectedRESTQA.addRangeElementIndex(dbName, dateTimeDataTypeString, "",
				validStartERIName);
		ConnectedRESTQA.addRangeElementIndex(dbName, dateTimeDataTypeString, "",
				validEndERIName);

		// Temporal axis must be created before temporal collection associated with
		// those axes is created
		ConnectedRESTQA.addElementRangeIndexTemporalAxis(dbName, axisSystemName,
				"", systemStartERIName, "", systemEndERIName);
		ConnectedRESTQA.addElementRangeIndexTemporalAxis(dbName, axisValidName, "",
				validStartERIName, "", validEndERIName);
		ConnectedRESTQA.addElementRangeIndexTemporalCollection(dbName,
				temporalCollectionName, axisSystemName, axisValidName);
		ConnectedRESTQA.addElementRangeIndexTemporalCollection(dbName,
				bulktemporalCollectionName, axisSystemName, axisValidName);
		ConnectedRESTQA.addElementRangeIndexTemporalCollection(dbName,
				temporalLsqtCollectionName, axisSystemName, axisValidName);
		ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
				temporalLsqtCollectionName, true);
		Thread.sleep(10);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down");

		// Delete database first. Otherwise axis and collection cannot be deleted
		cleanupRESTServer(dbName, fNames);
		deleteRESTUser("eval-user");
		deleteUserRole("test-eval");

		// Temporal collection needs to be delete before temporal axis associated
		// with it can be deleted
		ConnectedRESTQA.deleteElementRangeIndexTemporalCollection("Documents",
				temporalLsqtCollectionName);
		ConnectedRESTQA.deleteElementRangeIndexTemporalCollection("Documents",
				temporalCollectionName);
		ConnectedRESTQA.deleteElementRangeIndexTemporalCollection("Documents",
				bulktemporalCollectionName);
		ConnectedRESTQA.deleteElementRangeIndexTemporalAxis("Documents",
				axisValidName);
		ConnectedRESTQA.deleteElementRangeIndexTemporalAxis("Documents",
				axisSystemName);
	}

	@Before
	public void setUp() throws Exception {
		createUserRolesWithPrevilages("test-eval","xdbc:eval", "xdbc:eval-in","xdmp:eval-in","any-uri","xdbc:invoke","temporal:statement-set-system-time");
		createRESTUser("eval-user", "x", "test-eval","rest-admin","rest-writer","rest-reader");
		int restPort = getRestServerPort();
		writerClient = getDatabaseClientOnDatabase("localhost", restPort, dbName, "eval-user", "x", Authentication.DIGEST);             
	}

	@After
	public void tearDown() throws Exception {
		clearDB();
	}

	public DocumentMetadataHandle setMetadata(boolean update) {
		// create and initialize a handle on the meta-data
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

		if (update) {
			metadataHandle.getCollections().addAll("updateCollection");
			metadataHandle.getProperties().put("published", true);

			metadataHandle.getPermissions().add("app-user", Capability.UPDATE,
					Capability.READ);

			metadataHandle.setQuality(99);
		} else {
			metadataHandle.getCollections().addAll("insertCollection");
			metadataHandle.getProperties().put("reviewed", true);

			metadataHandle.getPermissions().add("app-user", Capability.UPDATE,
					Capability.READ, Capability.EXECUTE);

			metadataHandle.setQuality(11);
		}

		metadataHandle.getProperties().put("myString", "foo");
		metadataHandle.getProperties().put("myInteger", 10);
		metadataHandle.getProperties().put("myDecimal", 34.56678);
		metadataHandle.getProperties().put("myCalendar",
				Calendar.getInstance().get(Calendar.YEAR));

		return metadataHandle;
	}

	private JacksonDatabindHandle<ObjectNode> getJSONDocumentHandle(
			String startValidTime, String endValidTime, String address, String uri)
					throws Exception {

		// Setup for JSON document
		/**
		 * 
     { "System": { systemStartERIName : "", systemEndERIName : "", }, "Valid":
		 * { validStartERIName: "2001-01-01T00:00:00", validEndERIName:
		 * "2011-12-31T23:59:59" }, "Address": "999 Skyway Park", "uri":
		 * "javaSingleDoc1.json" }
		 */

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();

		// Set system time values
		ObjectNode system = mapper.createObjectNode();

		system.put(systemStartERIName, "");
		system.put(systemEndERIName, "");
		rootNode.set(systemNodeName, system);

		// Set valid time values
		ObjectNode valid = mapper.createObjectNode();

		valid.put(validStartERIName, startValidTime);
		valid.put(validEndERIName, endValidTime);
		rootNode.set(validNodeName, valid);

		// Set Address
		rootNode.put(addressNodeName, address);

		// Set uri
		rootNode.put(uriNodeName, uri);

		System.out.println(rootNode.toString());

		JacksonDatabindHandle<ObjectNode> handle = new JacksonDatabindHandle<>(
				ObjectNode.class).withFormat(Format.JSON);
		handle.set(rootNode);

		return handle;
	}

	@Test
	// Test bitemporal patchbuilder add Metadata Value works with a JSON document  
	public void testPatchWithAddMetaData() throws Exception {

		System.out.println("Inside testPatchWithAddMetaData");
		ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
		        temporalLsqtCollectionName, true);
		
		Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");

		String docId = "javaSingleJSONDoc.json";
		JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00", 
				"2011-12-31T23:59:59", 
				"999 Skyway Park - JSON",
				docId
				);

		JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();

		// put meta-data
		docMgr.setMetadataCategories(Metadata.ALL);
		DocumentMetadataHandle mh = setMetadata(false);
		docMgr.write(docId, mh, handle, null, null, temporalLsqtCollectionName, insertTime);

		// Apply the patch
		XMLDocumentManager xmlDocMgr = writerClient.newXMLDocumentManager();

		DocumentMetadataPatchBuilder patchBldrXML = xmlDocMgr.newPatchBuilder(Format.XML);
		patchBldrXML.addMetadataValue("MLVersion", "MarkLogic 9.0");
		patchBldrXML.addCollection("/document/collection3");
		patchBldrXML.addPermission("admin", Capability.READ);
		patchBldrXML.addPropertyValue("Hello","Hi");
		DocumentPatchHandle patchHandleXML = patchBldrXML.build();

		xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML);

		String contentMetadataXML = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Changing "+ contentMetadataXML);

		// Verify that patch succeeded.
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion\">MarkLogic 9.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Add Permission Values", contentMetadataXML.contains("<rapi:role-name>admin</rapi:role-name>"));
		assertTrue("Patch did not succeed - Property", contentMetadataXML.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
		assertTrue("Patch did not succeed - Collection", contentMetadataXML.contains("<rapi:collection>/document/collection3</rapi:collection>"));

		// Add the new meta data with JSON
		JSONDocumentManager jsonDocMgr = writerClient.newJSONDocumentManager();

		DocumentMetadataPatchBuilder patchBldrJson = jsonDocMgr.newPatchBuilder(Format.JSON);
		patchBldrJson.addMetadataValue("MLVersionJson", "MarkLogic 9.0 Json");
		patchBldrJson.addCollection("/document/collection3Json");

		DocumentPatchHandle patchHandleJSON = patchBldrJson.build();
		//xmlDocMgr.patch(docId, patchHandle);
		jsonDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleJSON);

		String contentMetadataJson = jsonDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Changing "+ contentMetadataJson);

		// Verify the first patch's contents.

		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion\">MarkLogic 9.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Add Permission Values", contentMetadataXML.contains("<rapi:role-name>admin</rapi:role-name>"));
		assertTrue("Patch did not succeed - Property", contentMetadataXML.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
		assertTrue("Patch did not succeed - Collection", contentMetadataXML.contains("<rapi:collection>/document/collection3</rapi:collection>"));

		// Verify that second patch with JSON succeeded.
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataJson.contains("<rapi:metadata-value key=\"MLVersionJson\">MarkLogic 9.0 Json</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Add Permission Values", contentMetadataJson.contains("<rapi:role-name>admin</rapi:role-name>"));
		assertTrue("Patch did not succeed - Property", contentMetadataJson.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
		assertTrue("Patch did not succeed - Collection", contentMetadataJson.contains("<rapi:collection>/document/collection3Json</rapi:collection>"));

		// Add multiple values at the same time.
		DocumentMetadataPatchBuilder patchBldrXMLMul = xmlDocMgr.newPatchBuilder(Format.XML);
		patchBldrXMLMul.addMetadataValue("MlClientProg1", "Java");
		patchBldrXMLMul.addMetadataValue("MlClientProg2", "Node/SJS");

		DocumentPatchHandle patchHandleXMLMul = patchBldrXMLMul.build();

		xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXMLMul);

		String contentMetadataXMLMul = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Changing "+ contentMetadataXMLMul);

		// Verify that patch succeeded.
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXMLMul.contains("<rapi:metadata-value key=\"MLVersion\">MarkLogic 9.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXMLMul.contains("<rapi:metadata-value key=\"MlClientProg1\">Java</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXMLMul.contains("<rapi:metadata-value key=\"MlClientProg2\">Node/SJS</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Add Permission Values", contentMetadataXMLMul.contains("<rapi:role-name>admin</rapi:role-name>"));
		assertTrue("Patch did not succeed - Property", contentMetadataXMLMul.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
		assertTrue("Patch did not succeed - Collection", contentMetadataXMLMul.contains("<rapi:collection>/document/collection3</rapi:collection>"));
	}

	/* Meta Data key and value have same string.
	 * Add same key value in another patch with new key value.
	 */
	@Test 
	public void testPatchWithAddMetaDataNeg() throws Exception {

		System.out.println("Inside testPatchWithAddMetaDataNeg");
		ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
		        temporalLsqtCollectionName, true);
		
		Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");

		String docId = "javaSingleJSONDoc.json";
		JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00", 
				"2011-12-31T23:59:59", 
				"999 Skyway Park - JSON",
				docId
				);

		JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();

		// put meta-data
		docMgr.setMetadataCategories(Metadata.ALL);
		DocumentMetadataHandle mh = setMetadata(false);
		docMgr.write(docId, mh, handle, null, null, temporalLsqtCollectionName, insertTime);

		// Apply the patch
		XMLDocumentManager xmlDocMgr = writerClient.newXMLDocumentManager();

		DocumentMetadataPatchBuilder patchBldrXML = xmlDocMgr.newPatchBuilder(Format.XML);
		patchBldrXML.addMetadataValue("MLVersion", "MLVersion");
		patchBldrXML.addCollection("/document/collection3");
		patchBldrXML.addPermission("admin", Capability.READ);
		patchBldrXML.addPropertyValue("Hello","Hi");
		DocumentPatchHandle patchHandleXML = patchBldrXML.build();

		xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML);

		String contentMetadataXML = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Changing "+ contentMetadataXML);

		// Verify that patch succeeded.
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion\">MLVersion</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Add Permission Values", contentMetadataXML.contains("<rapi:role-name>admin</rapi:role-name>"));
		assertTrue("Patch did not succeed - Property", contentMetadataXML.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
		assertTrue("Patch did not succeed - Collection", contentMetadataXML.contains("<rapi:collection>/document/collection3</rapi:collection>"));

		DocumentMetadataPatchBuilder patchBldrXML2 = xmlDocMgr.newPatchBuilder(Format.XML);

		//Do an add with same Key with another key value.
		patchBldrXML2.addMetadataValue("MLVersion", "MLVersionNew");
		DocumentPatchHandle patchHandleXML2 = patchBldrXML2.build();
		xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML2);
		String contentMetadataXML2 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Changing "+ contentMetadataXML2);

		// Verify that patch succeeded. Seems to work. Replaces the key value.
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML2.contains("<rapi:metadata-value key=\"MLVersion\">MLVersionNew</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Add Permission Values", contentMetadataXML2.contains("<rapi:role-name>admin</rapi:role-name>"));	  
	}

	/* Delete Meta Data key.
	 * Delete non-existing  key.
	 * Delete multiple keys.
	 */
	@Test
	// Test bitemporal patchbuilder add Metadata Value works with a JSON document  
	public void testPatchWithDelete() throws Exception {

		System.out.println("Inside testPatchWithDelete");
		ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
		        temporalLsqtCollectionName, true);
		
		Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");

		String docId = "javaSingleJSONDoc.json";
		JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00", 
				"2011-12-31T23:59:59", 
				"999 Skyway Park - JSON",
				docId
				);

		JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();

		// put meta-data
		docMgr.setMetadataCategories(Metadata.ALL);
		DocumentMetadataHandle mh = setMetadata(false);
		docMgr.write(docId, mh, handle, null, null, temporalLsqtCollectionName, insertTime);

		// Apply the patch
		XMLDocumentManager xmlDocMgr = writerClient.newXMLDocumentManager();

		DocumentMetadataPatchBuilder patchBldrXML = xmlDocMgr.newPatchBuilder(Format.XML);
		patchBldrXML.addMetadataValue("MLVersion", "9.0");
		patchBldrXML.addCollection("/document/collection3");
		patchBldrXML.addPermission("admin", Capability.READ);
		patchBldrXML.addPropertyValue("Hello","Hi");
		DocumentPatchHandle patchHandleXML = patchBldrXML.build();

		xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML);

		String contentMetadataXML = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Changing "+ contentMetadataXML);

		// Verify that patch succeeded.
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion\">9.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Permission Values", contentMetadataXML.contains("<rapi:role-name>admin</rapi:role-name>"));
		assertTrue("Patch did not succeed - Property", contentMetadataXML.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
		assertTrue("Patch did not succeed - Collection", contentMetadataXML.contains("<rapi:collection>/document/collection3</rapi:collection>"));

		DocumentMetadataPatchBuilder patchBldrXML2 = xmlDocMgr.newPatchBuilder(Format.XML);
		//Do an delete with key value.
		patchBldrXML2.deleteMetadataValue("MLVersion");
		DocumentPatchHandle patchHandleXML2 = patchBldrXML2.build();
		xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML2);
		String contentMetadataXML2 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Changing "+ contentMetadataXML2);

		// Verify that patch succeeded. Seems to work. Replaces the key value.
		assertFalse("Patch did not succeed - Meta data Delete Values", contentMetadataXML2.contains("<rapi:metadata-value key=\"MLVersion\">9.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Add Permission Values", contentMetadataXML2.contains("<rapi:role-name>admin</rapi:role-name>"));

		// Add back the same key
		DocumentMetadataPatchBuilder patchBldrXML3 = xmlDocMgr.newPatchBuilder(Format.XML);
		patchBldrXML3.addMetadataValue("MLVersion", "10.0");
		patchBldrXML3.addMetadataValue("MLVersion11", "11.0");
		patchBldrXML3.addMetadataValue("MLVersion12", "12.0");
		DocumentPatchHandle patchHandleXML3 = patchBldrXML3.build();
		xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML3);
		String contentMetadataXML3 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Changing "+ contentMetadataXML3);

		// Verify that patch succeeded.
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML3.contains("<rapi:metadata-value key=\"MLVersion\">10.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML3.contains("<rapi:metadata-value key=\"MLVersion11\">11.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML3.contains("<rapi:metadata-value key=\"MLVersion12\">12.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Permission Values", contentMetadataXML3.contains("<rapi:role-name>admin</rapi:role-name>"));
		assertTrue("Patch did not succeed - Property", contentMetadataXML3.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
		assertTrue("Patch did not succeed - Collection", contentMetadataXML3.contains("<rapi:collection>/document/collection3</rapi:collection>"));

		// Delete non existent key
		DocumentMetadataPatchBuilder patchBldrXML4 = xmlDocMgr.newPatchBuilder(Format.XML);
		patchBldrXML4.deleteMetadataValue("notfound");
		DocumentPatchHandle patchHandleXML4 = patchBldrXML4.build();
		xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML4);
		String contentMetadataXML4 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Changing "+ contentMetadataXML4);

		// Verify that patch did not delete existing values..
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML4.contains("<rapi:metadata-value key=\"MLVersion\">10.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML4.contains("<rapi:metadata-value key=\"MLVersion11\">11.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML4.contains("<rapi:metadata-value key=\"MLVersion12\">12.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Permission Values", contentMetadataXML4.contains("<rapi:role-name>admin</rapi:role-name>"));
		assertTrue("Patch did not succeed - Property", contentMetadataXML4.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
		assertTrue("Patch did not succeed - Collection", contentMetadataXML4.contains("<rapi:collection>/document/collection3</rapi:collection>"));

		// Delete multiple keys.
		DocumentMetadataPatchBuilder patchBldrXML5 = xmlDocMgr.newPatchBuilder(Format.XML);
		patchBldrXML5.deleteMetadataValue("MLVersion11");
		patchBldrXML5.deleteMetadataValue("MLVersion12");
		DocumentPatchHandle patchHandleXML5 = patchBldrXML5.build();
		xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML5);
		String contentMetadataXML5 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Changing "+ contentMetadataXML5);
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML5.contains("<rapi:metadata-value key=\"MLVersion\">10.0</rapi:metadata-value>"));
		assertFalse("Patch did not succeed - Meta data Values", contentMetadataXML5.contains("<rapi:metadata-value key=\"MLVersion11\">11.0</rapi:metadata-value>"));
		assertFalse("Patch did not succeed - Meta data Values", contentMetadataXML5.contains("<rapi:metadata-value key=\"MLVersion12\">12.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Permission Values", contentMetadataXML5.contains("<rapi:role-name>admin</rapi:role-name>"));
		assertTrue("Patch did not succeed - Property", contentMetadataXML5.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
		assertTrue("Patch did not succeed - Collection", contentMetadataXML5.contains("<rapi:collection>/document/collection3</rapi:collection>"));
	}

	/* Replace Meta Data key.
	 * Replace non-existing  key.
	 * Replace multiple keys.
	 * Perform add new, replace in same patch
	 */
	@Test
	// Test bitemporal patchbuilder add Metadata Value works with a JSON document  
	public void testPatchWithReplace() throws Exception {

		System.out.println("Inside testPatchWithReplace");
		ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
		        temporalLsqtCollectionName, true);
		
		Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");

		String docId = "javaSingleJSONDoc.json";
		JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00", 
				"2011-12-31T23:59:59", 
				"999 Skyway Park - JSON",
				docId
				);

		JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();

		// put meta-data
		docMgr.setMetadataCategories(Metadata.ALL);
		DocumentMetadataHandle mh = setMetadata(false);
		docMgr.write(docId, mh, handle, null, null, temporalLsqtCollectionName, insertTime);

		// Apply the patch
		XMLDocumentManager xmlDocMgr = writerClient.newXMLDocumentManager();

		DocumentMetadataPatchBuilder patchBldrXML = xmlDocMgr.newPatchBuilder(Format.XML);
		patchBldrXML.addMetadataValue("MLVersion", "9.0");
		patchBldrXML.addMetadataValue("MLVersion10", "9.0");
		patchBldrXML.addMetadataValue("MLVersion11", "9.0");
		patchBldrXML.addMetadataValue("MLVersion12", "12.0");
		patchBldrXML.addCollection("/document/collection3");
		patchBldrXML.addPermission("admin", Capability.READ);
		patchBldrXML.addPropertyValue("Hello","Hi");
		DocumentPatchHandle patchHandleXML = patchBldrXML.build();

		xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML);

		String contentMetadataXML = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Changing "+ contentMetadataXML);

		// Verify that patch succeeded.
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion\">9.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion10\">9.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion11\">9.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML.contains("<rapi:metadata-value key=\"MLVersion12\">12.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Permission Values", contentMetadataXML.contains("<rapi:role-name>admin</rapi:role-name>"));
		assertTrue("Patch did not succeed - Property", contentMetadataXML.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
		assertTrue("Patch did not succeed - Collection", contentMetadataXML.contains("<rapi:collection>/document/collection3</rapi:collection>"));

		DocumentMetadataPatchBuilder patchBldrXML2 = xmlDocMgr.newPatchBuilder(Format.XML);
		//Do a multiple replace with key value.
		patchBldrXML2.replaceMetadataValue("MLVersion10", "10.0");
		patchBldrXML2.replaceMetadataValue("MLVersion11", "11.0");
		DocumentPatchHandle patchHandleXML2 = patchBldrXML2.build();
		xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML2);
		String contentMetadataXML2 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Changing "+ contentMetadataXML2);

		// Verify that patch succeeded. 
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML2.contains("<rapi:metadata-value key=\"MLVersion\">9.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML2.contains("<rapi:metadata-value key=\"MLVersion10\">10.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML2.contains("<rapi:metadata-value key=\"MLVersion11\">11.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML2.contains("<rapi:metadata-value key=\"MLVersion12\">12.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Permission Values", contentMetadataXML2.contains("<rapi:role-name>admin</rapi:role-name>"));
		assertTrue("Patch did not succeed - Property", contentMetadataXML2.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
		assertTrue("Patch did not succeed - Collection", contentMetadataXML2.contains("<rapi:collection>/document/collection3</rapi:collection>"));

		// Replace non existent key
		DocumentMetadataPatchBuilder patchBldrXML3 = xmlDocMgr.newPatchBuilder(Format.XML);
		patchBldrXML3.replaceMetadataValue("notfound", "unknown");
		DocumentPatchHandle patchHandleXML3 = patchBldrXML3.build();
		xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML3);
		String contentMetadataXML3 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Changing "+ contentMetadataXML3);

		// Verify that none of the other values are incorrect or affected.
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML3.contains("<rapi:metadata-value key=\"MLVersion\">9.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML3.contains("<rapi:metadata-value key=\"MLVersion10\">10.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML3.contains("<rapi:metadata-value key=\"MLVersion11\">11.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML3.contains("<rapi:metadata-value key=\"MLVersion12\">12.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Permission Values", contentMetadataXML3.contains("<rapi:role-name>admin</rapi:role-name>"));
		assertTrue("Patch did not succeed - Property", contentMetadataXML3.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
		assertTrue("Patch did not succeed - Collection", contentMetadataXML3.contains("<rapi:collection>/document/collection3</rapi:collection>"));

		// Perform add new, replace in same patch
		DocumentMetadataPatchBuilder patchBldrXML4 = xmlDocMgr.newPatchBuilder(Format.XML);
		patchBldrXML4.addMetadataValue("NewAndReplace", "Added");
		patchBldrXML4.replaceMetadataValue("NewAndReplace", "Added and Replaced");
		DocumentPatchHandle patchHandleXML4 = patchBldrXML4.build();
		xmlDocMgr.patch(docId, temporalLsqtCollectionName, patchHandleXML4);
		String contentMetadataXML4 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Changing "+ contentMetadataXML4);

		// Verify that none of the other values are incorrect or affected.
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML4.contains("<rapi:metadata-value key=\"MLVersion\">9.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML4.contains("<rapi:metadata-value key=\"MLVersion10\">10.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML4.contains("<rapi:metadata-value key=\"MLVersion11\">11.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML4.contains("<rapi:metadata-value key=\"MLVersion12\">12.0</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Meta data Values", contentMetadataXML4.contains("<rapi:metadata-value key=\"NewAndReplace\">Added</rapi:metadata-value>"));
		assertTrue("Patch did not succeed - Permission Values", contentMetadataXML4.contains("<rapi:role-name>admin</rapi:role-name>"));
		assertTrue("Patch did not succeed - Property", contentMetadataXML4.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
		assertTrue("Patch did not succeed - Collection", contentMetadataXML4.contains("<rapi:collection>/document/collection3</rapi:collection>"));
	}

	/* insertFragement.
	 * Insert Fragments in JSON and XML BiTemporal Docs,
	 */
	@Test
	// Test bitemporal patchbuilder add Metadata Value works with a JSON document  
	public void testInsertFragement() throws Exception {

		// TODO Once inertFragment is implemented - write tests

		/*System.out.println("Inside testInsertFragement");
		 * ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName,
        temporalLsqtCollectionName, true);
		 
	  Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");

	  String docId = "javaSingleJSONDoc.json";
	  JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle("2001-01-01T00:00:00", 
			  "2011-12-31T23:59:59", 
			  "999 Skyway Park - JSON",
			  docId
			  );

	  JSONDocumentManager docMgr = writerClient.newJSONDocumentManager();

	  // put meta-data
	  docMgr.setMetadataCategories(Metadata.ALL);
	  DocumentMetadataHandle mh = setMetadata(false);
	  docMgr.write(docId, mh, handle, null, null, temporalLsqtCollectionName, insertTime);

	  // Apply the patch
	  XMLDocumentManager xmlDocMgr = writerClient.newXMLDocumentManager();

	  DocumentMetadataPatchBuilder patchBldrXML = xmlDocMgr.newPatchBuilder(Format.XML);*/

	}

}
