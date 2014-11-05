package com.marklogic.javaclient;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
import com.marklogic.client.pojo.util.GenerateIndexConfig;

/*
 * Purpose : To test the following data type range path index can be created in a database.
 * int
 * String
 * dateTime
 * daytimeduration* - This needs Java SE 8. Not tested in this class
 * URI
 * Numerals as Strings*
 */

public class TestAutomatedPathRangeIndex extends BasicJavaClientREST {
	private static String dbName = "TestAutomatedPathRangeIndexDB";
	private static String[] fNames = { "TestAutomatedPathRangeIndexDB-1" };
	private static String restServerName = "REST-Java-Client-API-Server";

	private static int restPort = 8011;
	private DatabaseClient client;

	/*
	 * This class is similar to the Artifact class. It is used to test path
	 * range index using the name field which has been annotated with @Id also.
	 */

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {		
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName, restPort);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down");
		//tearDownJavaRESTServer(dbName, fNames, restServerName);
	}

	@Before
	public void setUp() throws Exception {
		client = DatabaseClientFactory.newClient("localhost", restPort,
				"admin", "admin", Authentication.DIGEST);
	}

	@After
	public void tearDown() throws Exception {
		 //release client
		 client.release();
	}

	/*
	 * This Method takes the property name and value strings and verifies if
	 * propName exist and then extracts it from the response.
	 * The propValue string needs to be available in the extracted JsonNode's path-expression property.
	 */
	public static void validateRangePathIndexInDatabase(String dbName, String propName, String propValue) throws IOException {
		InputStream jsonstream = null;
		boolean propFound = false;
		String propertyAvailable = null;
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(
					new AuthScope("localhost", 8002),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet("http://localhost:8002"
					+ "/manage/v2/databases/" + dbName
					+ "/properties?format=json");
			HttpResponse response1 = client.execute(getrequest);
			jsonstream = response1.getEntity().getContent();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(jsonstream);

			if (!jnode.isNull()) {

				if (jnode.has(propName)) {
					propFound = true;
					List<JsonNode> jsonStringList = jnode.findValues(propName);
					
					String pathExpressValue = (jsonStringList.get(0)).findValues("path-expression").toString();
					String propValueJsonDecorated = "[\"" + propValue + "\"]";					
					
					boolean contains = pathExpressValue.contains(propValueJsonDecorated);
					propertyAvailable = contains==true ? (" contains " + propValue): (" does not contain " + propValue);						
					assertTrue(new StringBuffer("Database : " + dbName +  propertyAvailable).toString(), contains);
				}

			} else {
				assertTrue("Path range property not available or database properties not avilable",	propFound);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		}
	}
	
	/*
	 * This Method takes the property name and multiple value strings and verifies if
	 * propName exist and then extracts it from the response.
	 * The propValue string needs to be available in the extracted JsonNode's path-expression property.
	 */
	public static void validateMultipleRangePathIndexInDatabase(String dbName, String propName, String[] propValue) throws IOException {
		InputStream jsonstream = null;
		boolean propFound = false;
		String propertyAvailable1 = null;
		String propertyAvailable2 = null;
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(
					new AuthScope("localhost", 8002),
					new UsernamePasswordCredentials("admin", "admin"));
			HttpGet getrequest = new HttpGet("http://localhost:8002"
					+ "/manage/v2/databases/" + dbName
					+ "/properties?format=json");
			HttpResponse response1 = client.execute(getrequest);
			jsonstream = response1.getEntity().getContent();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(jsonstream);

			if (!jnode.isNull()) {

				if (jnode.has(propName)) {
					propFound = true;
					List<JsonNode> jsonStringList1 = jnode.findValues(propName);
					// Validate the first propValue at index 0
					String pathExpressValue1 = (jsonStringList1.get(0)).findValues("path-expression").toString();
					String propValueJsonDecorated1 = "[\"" + propValue[0];	
										
					boolean contains1 = pathExpressValue1.contains(propValueJsonDecorated1);
					propertyAvailable1 = contains1==true ? (" contains " + propValue[0]): (" does not contain " + propValue[0]);						
					assertTrue(new StringBuffer("Database : " + dbName +  propertyAvailable1).toString(), contains1);
					
					// Validate the second propValue at index 1. we have only two
					String pathExpressValue2 = (jsonStringList1.get(1)).findValues("path-expression").toString();
					String propValueJsonDecorated2 = propValue[1] + "\"]";					
					
					boolean contains2 = pathExpressValue2.contains(propValueJsonDecorated2);
					propertyAvailable2 = contains2==true ? (" contains " + propValue[1]): (" does not contain " + propValue[1]);						
					assertTrue(new StringBuffer("Database : " + dbName +  propertyAvailable2).toString(), contains2);
				}

			} else {
				assertTrue("Path range property not available or database properties not avilable",	propFound);
			}
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		}
	}
	
	@Test
	public void testArtifactIndexedOnInt() throws Exception {
		boolean succeeded = false;
		File jsonFile = null;
		try {
			GenerateIndexConfig.main(new String[] { "-classes",
					"com.marklogic.javaclient.ArtifactIndexedOnInt",
					"-file", "TestAutomatedPathRangeIndexInt.json" });

			jsonFile = new File("TestAutomatedPathRangeIndexInt.json");
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

			if (!jnode.isNull()) {
				setPathRangeIndexInDatabase(dbName, jnode);
				succeeded = true;
				validateRangePathIndexInDatabase(dbName, "range-path-index", "com.marklogic.javaclient.ArtifactIndexedOnInt/inventory");
			} else {
				assertTrue(
						"testArtifactIndexedOnInt - No Json node available to insert into database",
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

	@Test
	public void testArtifactIndexedOnString() throws Exception {
		boolean succeeded = false;
		File jsonFile = null;
		try {
			GenerateIndexConfig.main(new String[] { "-classes",
					"com.marklogic.javaclient.ArtifactIndexedOnString",
					"-file", "TestAutomatedPathRangeIndexString.json" });

			jsonFile = new File("TestAutomatedPathRangeIndexString.json");
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

			if (!jnode.isNull()) {
				setPathRangeIndexInDatabase(dbName, jnode);
				succeeded = true;
				validateRangePathIndexInDatabase(dbName, "range-path-index", "com.marklogic.javaclient.ArtifactIndexedOnString/name");
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
	
	@Test
	public void testArtifactIndexedOnDateTime() throws Exception {
		boolean succeeded = false;
		File jsonFile = null;
		try {
			GenerateIndexConfig.main(new String[] { "-classes",
					"com.marklogic.javaclient.ArtifactIndexedOnDateTime",
					"-file", "TestAutomatedPathRangeIndexDateTime.json" });

			jsonFile = new File("TestAutomatedPathRangeIndexDateTime.json");
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

			if (!jnode.isNull()) {
				setPathRangeIndexInDatabase(dbName, jnode);
				succeeded = true;
				validateRangePathIndexInDatabase(dbName, "range-path-index", "com.marklogic.javaclient.ArtifactIndexedOnDateTime/expiryDate");
			} else {
				assertTrue(
						"testArtifactIndexedOnDateTime - No Json node available to insert into database",
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
	
	@Test
	public void testArtifactIndexedOnAnyURI() throws Exception {
		boolean succeeded = false;
		File jsonFile = null;
		try {
			GenerateIndexConfig.main(new String[] { "-classes",
					"com.marklogic.javaclient.ArtifactIndexedOnUri",
					"-file", "TestAutomatedPathRangeIndexUri.json" });

			jsonFile = new File("TestAutomatedPathRangeIndexUri.json");
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

			if (!jnode.isNull()) {
				setPathRangeIndexInDatabase(dbName, jnode);
				succeeded = true;
				validateRangePathIndexInDatabase(dbName, "range-path-index", "com.marklogic.javaclient.ArtifactIndexedOnUri/artifactUri");
			} else {
				assertTrue(
						"testArtifactIndexedOnUri - No Json node available to insert into database",
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
	
	@Test
	public void testArtifactIndexedOnIntAsString() throws Exception {
		boolean succeeded = false;
		File jsonFile = null;
		try {
			GenerateIndexConfig.main(new String[] { "-classes",
					"com.marklogic.javaclient.ArtifactIndexedOnIntAsString",
					"-file", "TestAutomatedPathRangeIndexIntAsString.json" });

			jsonFile = new File("TestAutomatedPathRangeIndexIntAsString.json");
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

			if (!jnode.isNull()) {
				setPathRangeIndexInDatabase(dbName, jnode);
				succeeded = true;
				validateRangePathIndexInDatabase(dbName, "range-path-index", "com.marklogic.javaclient.ArtifactIndexedOnIntAsString/inventory");
			} else {
				assertTrue(
						"testArtifactIndexedOnIntAsString - No Json node available to insert into database",
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
	
	@Test
	public void testArtifactIndexedOnMultipleFields() throws Exception {
		boolean succeeded = false;
		File jsonFile = null;
		try {
			GenerateIndexConfig.main(new String[] { "-classes",
					"com.marklogic.javaclient.ArtifactIndexedOnMultipleFields",
					"-file", "TestAutomatedPathRangeIndexMultipleFields.json" });

			jsonFile = new File("TestAutomatedPathRangeIndexMultipleFields.json");
			//Array to hold the range path index values. Refer to the class for the annotated class members.
			String[] propValueStrArray = {"com.marklogic.javaclient.ArtifactIndexedOnMultipleFields/name",
					                      "com.marklogic.javaclient.ArtifactIndexedOnMultipleFields/inventory"};
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readValue(jsonFile, JsonNode.class);

			if (!jnode.isNull()) {
				setPathRangeIndexInDatabase(dbName, jnode);
				succeeded = true;
				validateMultipleRangePathIndexInDatabase(dbName, "range-path-index", propValueStrArray);
			} else {
				assertTrue(
						"testArtifactIndexedOnIntAsString - No Json node available to insert into database",
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
}
