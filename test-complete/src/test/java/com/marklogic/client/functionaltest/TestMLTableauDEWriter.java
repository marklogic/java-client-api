package com.marklogic.client.functionaltest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.DigestAuthContext;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.example.cookbook.datamovement.ExtractViaTemplateListener;
import com.marklogic.client.example.cookbook.datamovement.TypedRow;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.moveToCommunity.WriteRowToTableauConsumer;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.tableausoftware.common.Type;

/* 1) Download the Windows / Linux Tableau Java/C++ client zip file
 * 2) Extract the zip file into a local folder
 * 3) Add the path to "bin folder inside the extracted folder" to the PATH environment variable so that Extract can work.
 *    In Eclipse see Run / Debug configurations->Environment variable.
 * 4) In Eclipse link the Jars externally to the test project's build path. 
 *    Do a new build (with ML Tableau classes) of java client API SNAPSHOT.jar (or a release build)  
 * 5) If mvn/gradle is used to build and run tests then do the following:
 *    a)  Install all Tableau Jars extracted from zip file into local Maven repository using "mvn command".
 *        Run the command from a GitBash or shell. Note change the Artifact Id as appropriate for each Jar.
 *        You need to use the Artifact Id and version in your test (or other) project's POM.xml / build.gradle file for dependency management.
 *        mvn install:install-file -Dfile=/C/SWSetup/TableauWin64API/Java/jna.jar \
 *                                 -Dmaven.repo.local=/C/Users/ageorge/.m2/repository \
 *                                 -DgroupId=com.tableau -DartifactId=tableau-jna -Dversion=10.3.7 -Dpackaging=jar
 *     b) If you need to build MarkLogic Java Client API Jar with ML Tableau extract classes included then modify the java Client API POM.xml file
 *        
 *        Add to the dependency section for all Tableau jars using: *        
 *        <dependency>
            <groupId>com.tableau</groupId>
            <artifactId>tableau-jna</artifactId>
            <version>10.3.7</version>
            <scope>provided</scope>
          </dependency> 
          
          Run "mvn clean", "mvn compile" and "mvn install" commands to install new ML Java Client API Jar into repo.
 *     c) If needed, update your test project's POM.xml or build.gradle to pull in the Tableau Jars from the repository.
 */


public class TestMLTableauDEWriter extends BasicJavaClientREST {

	private static String dbName = "TestMLTableauDEWriterDB";
	private static String schemadbName = "TestMLTableauDEWriterDB";
	private static String[] fNames = { "TestMLTableauDEWriterDB-1" };	

	private static DatabaseClient client;
	private static DataMovementManager dmManager;
	private static String datasource = "src/test/java/com/marklogic/client/functionaltest/data/optics/";
	
	@BeforeClass
	public static void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception
	{
		System.out.println("In TestMLTableauDEWriter setup");		

		configureRESTServer(dbName, fNames);

		// Add new range elements into this array
		String[][] rangeElements = {
				// { scalar-type, namespace-uri, localname, collation,
				// range-value-positions, invalid-values }
				// If there is a need to add additional fields, then add them to the end
				// of each array
				// and pass empty strings ("") into an array where the additional field
				// does not have a value.
				// For example : as in namespace, collections below.
				// Add new RangeElementIndex as an array below.
				{ "string", "", "city", "http://marklogic.com/collation/", "false", "reject" },
				{ "int", "", "popularity", "", "false", "reject" },
				{ "int", "", "id", "", "false", "reject" },
				{ "double", "", "distance", "", "false", "reject" },
				{ "date", "", "date", "", "false", "reject" },
				{ "string", "", "cityName", "http://marklogic.com/collation/", "false", "reject" },
				{ "string", "", "cityTeam", "http://marklogic.com/collation/", "false", "reject" },
				{ "long", "", "cityPopulation", "", "false", "reject" }
		};

		// Insert the range indices
		addRangeElementIndex(dbName, rangeElements);

		// Insert word lexicon.
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode mainNode = mapper.createObjectNode();
		ObjectNode wordLexicon = mapper.createObjectNode();
		ArrayNode childArray = mapper.createArrayNode();
		ObjectNode childNodeObject = mapper.createObjectNode();

		childNodeObject.put("namespace-uri", "");
		childNodeObject.put("localname", "city");
		childNodeObject.put("collation", "http://marklogic.com/collation/");
		childArray.add(childNodeObject);
		mainNode.withArray("element-word-lexicon").add(childArray);

		setDatabaseProperties(dbName, "element-word-lexicon", mainNode);

		// Enable triple index.
		enableTripleIndex(dbName);
		waitForServerRestart();
		// Enable collection lexicon.
		enableCollectionLexicon(dbName);
		// Enable uri lexicon.
		setDatabaseProperties(dbName, "uri-lexicon", true);		

		if (IsSecurityEnabled()) {			
			client = getDatabaseClient("admin", "admin", Authentication.DIGEST);
		}
		else {			
			client = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), new DigestAuthContext("admin", "admin"));
		}
		dmManager = client.newDataMovementManager();

		// Install the TDE templates
		// loadFileToDB(client, filename, docURI, collection, document format)
		loadFileToDB(client, "masterDetail.tdex", "masterDetail.tde", "XML", new String[] { "http://marklogic.com/xdmp/tde" });
		loadFileToDB(client, "masterDetail2.tdej", "masterDetail2.tde", "JSON", new String[] { "http://marklogic.com/xdmp/tde" });
		loadFileToDB(client, "masterDetail3.tdej", "masterDetail3.tde", "JSON", new String[] { "http://marklogic.com/xdmp/tde" });
		loadFileToDB(client, "masterDetail4.tdej", "masterDetail4.tde", "JSON", new String[] { "http://marklogic.com/xdmp/tde" });

		// Load XML data files.
		loadFileToDB(client, "masterDetail.xml", "/optic/view/test/masterDetail.xml", "XML", new String[] { "/optic/view/test" });
		loadFileToDB(client, "playerTripleSet.xml", "/optic/triple/test/playerTripleSet.xml", "XML", new String[] { "/optic/player/triple/test" });
		loadFileToDB(client, "teamTripleSet.xml", "/optic/triple/test/teamTripleSet.xml", "XML", new String[] { "/optic/team/triple/test" });
		loadFileToDB(client, "otherPlayerTripleSet.xml", "/optic/triple/test/otherPlayerTripleSet.xml", "XML", new String[] { "/optic/other/player/triple/test" });
		loadFileToDB(client, "doc4.xml", "/optic/lexicon/test/doc4.xml", "XML", new String[] { "/optic/lexicon/test" });
		loadFileToDB(client, "doc5.xml", "/optic/lexicon/test/doc5.xml", "XML", new String[] { "/optic/lexicon/test" });

		// Load JSON data files.
		loadFileToDB(client, "masterDetail2.json", "/optic/view/test/masterDetail2.json", "JSON", new String[] { "/optic/view/test" });
		loadFileToDB(client, "masterDetail3.json", "/optic/view/test/masterDetail3.json", "JSON", new String[] { "/optic/view/test" });
		loadFileToDB(client, "masterDetail4.json", "/optic/view/test/masterDetail4.json", "JSON", new String[] { "/optic/view/test" });
		loadFileToDB(client, "masterDetail5.json", "/optic/view/test/masterDetail5.json", "JSON", new String[] { "/optic/view/test" });

		loadFileToDB(client, "doc1.json", "/optic/lexicon/test/doc1.json", "JSON", new String[] { "/other/coll1", "/other/coll2" });
		loadFileToDB(client, "doc2.json", "/optic/lexicon/test/doc2.json", "JSON", new String[] { "/optic/lexicon/test" });
		loadFileToDB(client, "doc3.json", "/optic/lexicon/test/doc3.json", "JSON", new String[] { "/optic/lexicon/test" });

		loadFileToDB(client, "city1.json", "/optic/lexicon/test/city1.json", "JSON", new String[] { "/optic/lexicon/test" });
		loadFileToDB(client, "city2.json", "/optic/lexicon/test/city2.json", "JSON", new String[] { "/optic/lexicon/test" });
		loadFileToDB(client, "city3.json", "/optic/lexicon/test/city3.json", "JSON", new String[] { "/optic/lexicon/test" });
		loadFileToDB(client, "city4.json", "/optic/lexicon/test/city4.json", "JSON", new String[] { "/optic/lexicon/test" });
		loadFileToDB(client, "city5.json", "/optic/lexicon/test/city5.json", "JSON", new String[] { "/optic/lexicon/test" });
		System.out.println("End of SETUP method");		
	}

	/**
	 * Write document using DOMHandle
	 * 
	 * @param client
	 * @param filename
	 * @param uri
	 * @param type
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */

	public static void loadFileToDB(DatabaseClient client, String filename, String uri, String type, String[] collections) throws IOException, ParserConfigurationException,
	SAXException
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentMgrSelector(client, docMgr, type);

		File file = new File(datasource + filename);
		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);

		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		for (String coll : collections)
			metadataHandle.getCollections().addAll(coll.toString());

		// write the document content
		DocumentWriteSet writeset = docMgr.newWriteSet();
		writeset.addDefault(metadataHandle);
		writeset.add(uri, handle);

		docMgr.write(writeset);

		System.out.println("Write " + uri + " to database");
	}

	/**
	 * Function to select and create document manager based on the type
	 * 
	 * @param client
	 * @param docMgr
	 * @param type
	 * @return
	 */
	public static DocumentManager documentMgrSelector(DatabaseClient client, DocumentManager docMgr, String type) {
		// create doc manager
		switch (type) {
		case "XML":
			docMgr = client.newXMLDocumentManager();
			break;
		case "Text":
			docMgr = client.newTextDocumentManager();
			break;
		case "JSON":
			docMgr = client.newJSONDocumentManager();
			break;
		case "Binary":
			docMgr = client.newBinaryDocumentManager();
			break;
		case "JAXB":
			docMgr = client.newXMLDocumentManager();
			break;
		default:
			System.out.println("Invalid type");
			break;
		}
		return docMgr;
	}

	// Test with a single nested templates file.
	@Test
	public void testOneNestedTemplatesFile() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testOneNestedTemplatesFile method");
		
		String extractedOutFileName = datasource + "TableauOutput.tde";
		String checkedInFile = datasource + "TableauInput.tde";
		
		WriteRowToTableauConsumer tableauWriter = null;
		ExtractViaTemplateListener extractor = null;
		
		StringBuilder batchResults = new StringBuilder();
		StringBuilder batchDetails = new StringBuilder();
		StringBuilder secondConsumerDetails = new StringBuilder();
		try {
			// Delete any existing extracted file
			if (new File(extractedOutFileName).exists()) new File(extractedOutFileName).delete();

			QueryManager queryMgr = client.newQueryManager();
			tableauWriter = new WriteRowToTableauConsumer(extractedOutFileName)
					.withColumn("id", Type.INTEGER)
					.withColumn("name", Type.UNICODE_STRING)
					.withColumn("date", Type.UNICODE_STRING)
					.withColumn("amount", Type.DOUBLE)
					.withColumn("masterId", Type.INTEGER)
					.withColumn("color", Type.UNICODE_STRING);

			extractor = new ExtractViaTemplateListener();	    
			extractor.withTemplate("masterDetail4.tde");

			StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();			
			StructuredQueryDefinition querydef = qb.range(qb.element("id"), "xs:integer", Operator.GE, 100);

			Consumer<TypedRow> consumer = row-> {
				// Iterate over the rows and do the JUnit asserts.
				System.out.print("A new row received");
				System.out.print(" "+ row.toString());

				secondConsumerDetails.append(row.toString());				
			};

			QueryBatcher queryBatcher = dmManager.newQueryBatcher(querydef)
					.onUrisReady(extractor.onTypedRowReady(tableauWriter)
							// Use the second consumer to do the asserts in the
							// lambda.
							.onTypedRowReady(consumer))

					.onUrisReady(batch -> {
						for (String str : batch.getItems()) {
							batchResults.append(str).append('|');
							// Batch details
							batchDetails.append(batch.getJobBatchNumber()).append('|')
							.append(batch.getJobResultsSoFar()).append('|')
							.append(batch.getForestBatchNumber());
						}
					});

			dmManager.startJob(queryBatcher);
			queryBatcher.awaitCompletion();	    	
		}
		catch(Exception ex) {
			System.out.println("Exceptions thrown " + ex.toString());
		}
		finally {
			if (tableauWriter != null) {				
				tableauWriter.close();
				try {
					extractor.close();
				} catch (Exception e) {
					System.out.println("Exceptions thrown during ExtractViaTemplateListener close.");
					e.printStackTrace();
				}
			}
		}

		// Validate the bytes of the output and input (checked into repo) files
		byte[] inputBytes = Files.readAllBytes(new File(checkedInFile).toPath());
		byte[] extractedBytes = Files.readAllBytes(new File(extractedOutFileName).toPath());

		assertEquals("Tableau extracted " + extractedOutFileName + " file size didn't match " + checkedInFile,
				inputBytes.length, extractedBytes.length);
		// Verify the results
		System.out.println("batchResults are " + batchResults.toString());
		System.out.println("batchDetails are " + batchDetails.toString());
		// Verify the URIs.
		String[] res = batchResults.toString().split("\\|");
		assertTrue("URI returned not correct", res[0].contains("/optic/view/test/masterDetail4.json") ||
				res[0].contains("/optic/view/test/masterDetail5.json"));
		assertTrue("URI returned not correct", res[1].contains("/optic/view/test/masterDetail4.json") ||
				res[1].contains("/optic/view/test/masterDetail5.json"));
		// Verify that rows are returned from both templates of the TDE file.
		String rowDetails = secondConsumerDetails.toString();
		assertTrue("Row returned not correct", rowDetails.contains("id=100, name=Master 100"));
		assertTrue("Row returned not correct", rowDetails.contains("id=200, name=Master 200, date=2016-04-02"));
		assertTrue("Row returned not correct", rowDetails.contains("id=100, name=Detail 100, masterId=100, amount=64.33, color=red"));
		assertTrue("Row returned not correct", rowDetails.contains("id=200, name=Detail 200, masterId=200, amount=89.36, color=blue"));
		assertTrue("Row returned not correct", rowDetails.contains("id=300, name=Detail 300, masterId=200, amount=72.9, color=yellow"));
		assertTrue("Row returned not correct", rowDetails.contains("id=400, name=Detail 400, masterId=200, amount=164.33, color=purple"));
		assertTrue("Row returned not correct", rowDetails.contains("id=500, name=Detail 500, masterId=100, amount=189.36, color=gold"));
		assertTrue("Row returned not correct", rowDetails.contains("id=600, name=Detail 600, masterId=100, amount=172.9, color=white"));
	}
	
	// Test with a multiple nested templates file.
	@Test
	public void testMultipleNestedTemplatesFiles() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testMultipleNestedTemplatesFiles method");		

		String extractedOutFileName = datasource + "TableauOutput.tde";
		String checkedInFile = datasource + "TableauInput.tde";

		WriteRowToTableauConsumer tableauWriter = null;
		ExtractViaTemplateListener extractor = null;

		StringBuilder batchResults = new StringBuilder();
		StringBuilder batchDetails = new StringBuilder();
		StringBuilder secondConsumerDetails = new StringBuilder();
		try {
			// Delete any existing extracted file
			if (new File(extractedOutFileName).exists()) new File(extractedOutFileName).delete();
			
			QueryManager queryMgr = client.newQueryManager();
			tableauWriter = new WriteRowToTableauConsumer(extractedOutFileName)
					.withColumn("id", Type.INTEGER)
					.withColumn("name", Type.UNICODE_STRING)
					.withColumn("date", Type.UNICODE_STRING)
					.withColumn("amount", Type.DOUBLE)
					.withColumn("masterId", Type.INTEGER)
					.withColumn("color", Type.UNICODE_STRING);

			extractor = new ExtractViaTemplateListener();	    
			extractor.withTemplate("masterDetail4.tde").withTemplate("masterDetail2.tde");
			
			StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();			
			StructuredQueryDefinition querydef = qb.range(qb.element("id"), "xs:integer", Operator.GE, 100);

			Consumer<TypedRow> consumer = row-> {
				// Iterate over the rows and do the JUnit asserts.
				System.out.print("A new row received");
				System.out.print(" "+ row.toString());

				secondConsumerDetails.append(row.toString());				
			};

			QueryBatcher queryBatcher = dmManager.newQueryBatcher(querydef)
					.onUrisReady(extractor.onTypedRowReady(tableauWriter)
							// Use the second consumer to do the asserts in the
							// lambda.
							.onTypedRowReady(consumer))

					.onUrisReady(batch -> {
						for (String str : batch.getItems()) {
							batchResults.append(str).append('|');
							// Batch details
							batchDetails.append(batch.getJobBatchNumber()).append('|')
							.append(batch.getJobResultsSoFar()).append('|')
							.append(batch.getForestBatchNumber());
						}
					});

			dmManager.startJob(queryBatcher);
			queryBatcher.awaitCompletion();	    	
		}
		catch(Exception ex) {
			System.out.println("Exceptions thrown " + ex.toString());
		}
		finally {
			if (tableauWriter != null) {				
				tableauWriter.close();
				try {
					extractor.close();
				} catch (Exception e) {
					System.out.println("Exceptions thrown during ExtractViaTemplateListener close.");
					e.printStackTrace();
				}
			}
		}

		// Validate the bytes of the output and input (checked into repo) files
		byte[] inputBytes = Files.readAllBytes(new File(checkedInFile).toPath());
		byte[] extractedBytes = Files.readAllBytes(new File(extractedOutFileName).toPath());

		assertEquals("Tableau extracted " + extractedOutFileName + " file size didn't match " + checkedInFile,
				inputBytes.length, extractedBytes.length);
		// Verify the results
		System.out.println("batchResults are " + batchResults.toString());
		System.out.println("batchDetails are " + batchDetails.toString());
		// Verify the URIs.
		String[] res = batchResults.toString().split("\\|");
		assertTrue("URI returned not correct", res[0].contains("/optic/view/test/masterDetail4.json") ||
				res[0].contains("/optic/view/test/masterDetail5.json"));
		assertTrue("URI returned not correct", res[1].contains("/optic/view/test/masterDetail4.json") ||
				res[1].contains("/optic/view/test/masterDetail5.json"));
		// Verify that rows are returned from both templates of the TDE file.
		String rowDetails = secondConsumerDetails.toString();
		assertTrue("Row returned not correct", rowDetails.contains("id=100, name=Master 100"));
		assertTrue("Row returned not correct", rowDetails.contains("id=200, name=Master 200, date=2016-04-02"));
		assertTrue("Row returned not correct", rowDetails.contains("id=100, name=Detail 100, masterId=100, amount=64.33, color=red"));
		assertTrue("Row returned not correct", rowDetails.contains("id=200, name=Detail 200, masterId=200, amount=89.36, color=blue"));
		assertTrue("Row returned not correct", rowDetails.contains("id=300, name=Detail 300, masterId=200, amount=72.9, color=yellow"));
		assertTrue("Row returned not correct", rowDetails.contains("id=400, name=Detail 400, masterId=200, amount=164.33, color=purple"));
		assertTrue("Row returned not correct", rowDetails.contains("id=500, name=Detail 500, masterId=100, amount=189.36, color=gold"));
		assertTrue("Row returned not correct", rowDetails.contains("id=600, name=Detail 600, masterId=100, amount=172.9, color=white"));
	}
		
	@Test
	  public void testIncorrectColumnType() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testIncorrectColumnType method");		

		String extractedOutFileName = datasource + "TableauOutput.tde";		
		WriteRowToTableauConsumer tableauWriter = null;
		StringBuilder acceptResults = new StringBuilder();

		// Delete any existing extracted file
		if (new File(extractedOutFileName).exists()) new File(extractedOutFileName).delete();
		
		PlanBuilder pb = client.newRowManager().newPlanBuilder();

		QueryManager queryMgr = client.newQueryManager();
		tableauWriter = new WriteRowToTableauConsumer(extractedOutFileName)
				.withColumn("id", Type.DOUBLE)
				.withColumn("name", Type.UNICODE_STRING)
				.withColumn("date", Type.UNICODE_STRING)
				.withColumn("amount", Type.DOUBLE)
				.withColumn("masterId", Type.INTEGER)
				.withColumn("color", Type.UNICODE_STRING);

		ExtractViaTemplateListener extractor = new ExtractViaTemplateListener();	    
		extractor.withTemplate("masterDetail4.tde");

		TypedRow incorrectType = new TypedRow("name", "Master 100");
		incorrectType.put("id", pb.xs.intVal(1));

		try {
			tableauWriter.accept(incorrectType);				
		}			
		catch(Exception ex) {
			System.out.println("Exceptions thrown " + ex.toString());
			acceptResults.append(ex.toString());
		}
		finally {
			if (tableauWriter != null) {
				tableauWriter.close();
				try {
					extractor.close();
				} catch (Exception e) {
					System.out.println("Exceptions thrown during ExtractViaTemplateListener close.");
					e.printStackTrace();
				}
			}
		}
		String res = acceptResults.toString();
		assertTrue("URI returned not correct", res.contains("java.lang.IllegalStateException"));
		assertTrue("URI returned not correct", res.contains("Column \"id\" type DOUBLE is incompatible with data type"));
	}
	
	// Test with different number of columns for WriteRowToTableauConsumer instance
	@Test
	  public void testNumberOfColumns() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testNumberOfColumns method");		

		String extractedOutFileName = datasource + "TableauOutput.tde";
		
		WriteRowToTableauConsumer tableauWriter = null;
		ExtractViaTemplateListener extractor = null;
		
		StringBuilder batchResults = new StringBuilder();
		StringBuilder batchDetails = new StringBuilder();
		StringBuilder secondConsumerDetails = new StringBuilder();
		
		QueryManager queryMgr = client.newQueryManager();
		try {
			// Delete any existing extracted file
			if (new File(extractedOutFileName).exists()) new File(extractedOutFileName).delete();
		
			// No columns specified.
			tableauWriter = new WriteRowToTableauConsumer(extractedOutFileName);

			extractor = new ExtractViaTemplateListener();	    
			extractor.withTemplate("masterDetail4.tde");

			StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();			
			StructuredQueryDefinition querydef = qb.range(qb.element("id"), "xs:integer", Operator.GE, 100);

			Consumer<TypedRow> consumer = row-> {
				// Iterate over the rows and do the JUnit asserts.
				System.out.print("A new row received ");
				System.out.print(" "+ row.toString());

				secondConsumerDetails.append(row.toString());				
			};

			QueryBatcher queryBatcher = dmManager.newQueryBatcher(querydef)
					.onUrisReady(extractor.onTypedRowReady(tableauWriter)
							// Use the second consumer to do the asserts in the
							// lambda.
							.onTypedRowReady(consumer))

					.onUrisReady(batch -> {
						for (String str : batch.getItems()) {
							batchResults.append(str).append('|');
							// Batch details
							batchDetails.append(batch.getJobBatchNumber()).append('|')
							.append(batch.getJobResultsSoFar()).append('|')
							.append(batch.getForestBatchNumber());
						}
					});

			dmManager.startJob(queryBatcher);
			queryBatcher.awaitCompletion();	    	
		}
		catch(Exception ex) {
			System.out.println("Exceptions thrown " + ex.toString());
		}
		finally {
			if (tableauWriter != null) {				
				tableauWriter.close();
				try {
					extractor.close();
				} catch (Exception e) {
					System.out.println("Exceptions thrown during ExtractViaTemplateListener close.");
					e.printStackTrace();
				}
			}
		}
		
		// Verify the results. The results from second consumer will remain the same. Tableau o/p will be different.
		// Columns from first rows will be in the Tableau output.
		System.out.println("batchResults are " + batchResults.toString());
		System.out.println("batchDetails are " + batchDetails.toString());
		// Verify the URIs.
		String[] res = batchResults.toString().split("\\|");
		assertTrue("URI returned not correct", res[0].contains("/optic/view/test/masterDetail4.json") ||
				res[0].contains("/optic/view/test/masterDetail5.json"));
		assertTrue("URI returned not correct", res[1].contains("/optic/view/test/masterDetail4.json") ||
				res[1].contains("/optic/view/test/masterDetail5.json"));
		// Verify that rows are returned from both templates of the TDE file.
		String rowDetails = secondConsumerDetails.toString();
		assertTrue("Row returned not correct", rowDetails.contains("id=100, name=Master 100"));
		assertTrue("Row returned not correct", rowDetails.contains("id=200, name=Master 200, date=2016-04-02"));
		assertTrue("Row returned not correct", rowDetails.contains("id=100, name=Detail 100, masterId=100, amount=64.33, color=red"));
		assertTrue("Row returned not correct", rowDetails.contains("id=200, name=Detail 200, masterId=200, amount=89.36, color=blue"));
		assertTrue("Row returned not correct", rowDetails.contains("id=300, name=Detail 300, masterId=200, amount=72.9, color=yellow"));
		assertTrue("Row returned not correct", rowDetails.contains("id=400, name=Detail 400, masterId=200, amount=164.33, color=purple"));
		assertTrue("Row returned not correct", rowDetails.contains("id=500, name=Detail 500, masterId=100, amount=189.36, color=gold"));
		assertTrue("Row returned not correct", rowDetails.contains("id=600, name=Detail 600, masterId=100, amount=172.9, color=white"));
	}
	
	// Test with different number of columns for WriteRowToTableauConsumer instance
	@Test
	public void testInCorrectNoTemplate() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testInCorrectNoTemplate method");

		String extractedOutFileName = datasource + "TableauOutput.tde";

		WriteRowToTableauConsumer tableauWriter = null;
		ExtractViaTemplateListener extractor = null;

		StringBuilder batchResults = new StringBuilder();
		
		QueryManager queryMgr = client.newQueryManager();
		try {
			// Delete any existing extracted file
			if (new File(extractedOutFileName).exists()) new File(extractedOutFileName).delete();

			tableauWriter = new WriteRowToTableauConsumer(extractedOutFileName)
					.withColumn("id", Type.INTEGER)
					.withColumn("name", Type.UNICODE_STRING)
					.withColumn("date", Type.UNICODE_STRING)
					.withColumn("amount", Type.DOUBLE)
					.withColumn("masterId", Type.INTEGER)
					.withColumn("color", Type.UNICODE_STRING);

			extractor = new ExtractViaTemplateListener();	    
			extractor.withTemplate("AAA.tde");

			StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();			
			StructuredQueryDefinition querydef = qb.range(qb.element("id"), "xs:integer", Operator.GE, 100);

			QueryBatcher queryBatcher = dmManager.newQueryBatcher(querydef)
					.onUrisReady(extractor.onFailure((batch, throwable) -> {
						System.out.println("Exceptions thrown from Extractor " + throwable.toString());
						batchResults.append(throwable.toString());
					}));

			dmManager.startJob(queryBatcher);
			queryBatcher.awaitCompletion();	    	
		}
		catch(Exception ex) {
			System.out.println("Exceptions thrown " + ex.toString());
		}
		finally {
			if (tableauWriter != null) {				
				tableauWriter.close();
				try {
					extractor.close();
				} catch (Exception e) {
					System.out.println("Exceptions thrown during ExtractViaTemplateListener close.");
					e.printStackTrace();
				}
			}
		}
		String strResults = batchResults.toString();
		assertTrue("Exception not correct", strResults.contains("com.marklogic.client.ResourceNotFoundException"));
		assertTrue("Exception not correct", strResults.contains("Resource or document does not exist"));
		assertTrue("Exception not correct", strResults.contains("Cannot find template: AAA.tde"));
	}
	
	// Test with a multiple nested templates and multiple extractors to a Tableau Extractor file.
	@Test
	public void testMultipleExtractors() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
	{
		System.out.println("In testMultipleExtractors method");

		String extractedOutFileName = datasource + "TableauOutput.tde";
		String extractedOutFileName1 = datasource + "TableauOutput1.tde";
		String extractedOutFileName2 = datasource + "TableauOutput2.tde";

		WriteRowToTableauConsumer tableauWriter1 = null;
		WriteRowToTableauConsumer tableauWriter2 = null;
		ExtractViaTemplateListener extractor1 = null;
		ExtractViaTemplateListener extractor2 = null;

		StringBuilder batchResults = new StringBuilder();
		StringBuilder failureDetails = new StringBuilder();
		StringBuilder secondConsumerDetails = new StringBuilder();
		try {
			// Delete any existing extracted file
			if (new File(extractedOutFileName).exists()) new File(extractedOutFileName).delete();
			if (new File(extractedOutFileName1).exists()) new File(extractedOutFileName1).delete();
			if (new File(extractedOutFileName2).exists()) new File(extractedOutFileName2).delete();

			QueryManager queryMgr = client.newQueryManager();
						
			tableauWriter1 = new WriteRowToTableauConsumer(extractedOutFileName1)
					.withColumn("id", Type.INTEGER)
					.withColumn("name", Type.UNICODE_STRING);
			tableauWriter2 = new WriteRowToTableauConsumer(extractedOutFileName2)
					.withColumn("date", Type.UNICODE_STRING)
					.withColumn("amount", Type.DOUBLE)
					.withColumn("masterId", Type.INTEGER)
					.withColumn("color", Type.UNICODE_STRING);
			
			
			extractor1 = new ExtractViaTemplateListener();	    
			extractor1.withTemplate("masterDetail4.tde").withTemplate("masterDetail2.tde");
			
			extractor2 = new ExtractViaTemplateListener();	    
			extractor2.withTemplate("masterDetail4.tde").withTemplate("masterDetail2.tde");

			StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();			
			StructuredQueryDefinition querydef = qb.range(qb.element("id"), "xs:integer", Operator.GE, 100);

			Consumer<TypedRow> consumer = row-> {
				// Iterate over the rows and do the JUnit asserts.
				System.out.print("A new row received");
				System.out.print(" "+ row.toString());

				secondConsumerDetails.append(row.toString());				
			};
			
			// Verify with two consumers on one file
			String sameFileExcep = null;
			ExtractViaTemplateListener extractor3 = new ExtractViaTemplateListener();	    
			extractor3.withTemplate("masterDetail4.tde").withTemplate("masterDetail2.tde");
			ExtractViaTemplateListener extractor4 = new ExtractViaTemplateListener();	    
			extractor4.withTemplate("masterDetail4.tde").withTemplate("masterDetail2.tde");
			WriteRowToTableauConsumer tableauWriter3 = null;
			WriteRowToTableauConsumer tableauWriter4 = null;

			try {							
				tableauWriter3 = new WriteRowToTableauConsumer(extractedOutFileName)
						.withColumn("id", Type.INTEGER)
						.withColumn("name", Type.UNICODE_STRING);
				tableauWriter4 = new WriteRowToTableauConsumer(extractedOutFileName)
						.withColumn("date", Type.UNICODE_STRING)
						.withColumn("amount", Type.DOUBLE)
						.withColumn("masterId", Type.INTEGER)
						.withColumn("color", Type.UNICODE_STRING);
				QueryBatcher queryBatcherErr = dmManager.newQueryBatcher(querydef)
						.onUrisReady(extractor3.onTypedRowReady(tableauWriter3)
								.onFailure((batch, throwable) -> {

									System.out.println("Exceptions thrown from Extractor with  queryBatcherErr" + throwable.toString());
									failureDetails.append(throwable.toString());
								}))
						.onUrisReady(extractor4.onTypedRowReady(tableauWriter4));
				dmManager.startJob(queryBatcherErr);
				queryBatcherErr.awaitCompletion();	

			} catch (Exception e) {
				sameFileExcep = e.getMessage();
				System.out.println(sameFileExcep.toString());
				String str = "Filename \"" + extractedOutFileName + "\" already exists";
				assertTrue("Exception not correct", sameFileExcep.contains(str));
			}
			finally {
				if (tableauWriter3 != null) {				
					tableauWriter3.close();
					try {
						extractor3.close();
					} catch (Exception e) {
						System.out.println("Exceptions thrown during ExtractViaTemplateListener 3 close.");
						e.printStackTrace();
					}
				}
				if (tableauWriter4 != null) {				
					tableauWriter4.close();
					try {
						extractor4.close();
					} catch (Exception e) {
						System.out.println("Exceptions thrown during ExtractViaTemplateListener 4 close.");
						e.printStackTrace();
					}
				}
			}
			// Multiple extractors working into a TDE file
			
			// Writer1 
			QueryBatcher queryBatcher = dmManager.newQueryBatcher(querydef)
					.onUrisReady(extractor1.onTypedRowReady(tableauWriter1)
							.onFailure((batch, throwable) -> {
								// In ideal case there should not be any errors / exceptions.
								System.out.println("Exceptions thrown from Extractor " + throwable.toString());
								failureDetails.append(throwable.toString());
							})
						
							// Use the second consumer to do the asserts in the
							// lambda.
							.onTypedRowReady(consumer))

					.onUrisReady(batch -> {
						for (String str : batch.getItems()) {
							batchResults.append(str).append('|');
						}
					});
			// Writer2
			queryBatcher.onUrisReady(extractor2.onTypedRowReady(tableauWriter2));

			dmManager.startJob(queryBatcher);
			queryBatcher.awaitCompletion();	    	
		}
		catch(Exception ex) {
			System.out.println("Exceptions thrown " + ex.toString());
		}
		finally {
			if (tableauWriter1 != null) {				
				tableauWriter1.close();
				try {
					extractor1.close();
				} catch (Exception e) {
					System.out.println("Exceptions thrown during ExtractViaTemplateListener close.");
					e.printStackTrace();
				}
			}
			if (tableauWriter2 != null) {				
				tableauWriter2.close();
				try {
					extractor2.close();
				} catch (Exception e) {
					System.out.println("Exceptions thrown during ExtractViaTemplateListener close.");
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("Failure Listenerd details (if any)  are " + failureDetails.toString());
		assertTrue("Tableau extract failed due to errors/exception ",  failureDetails.toString().isEmpty());

		// Validate the bytes of the output files. Visual inspection using Tableau Desktop software done on both files.
		byte[] extractedBytes1 = Files.readAllBytes(new File(extractedOutFileName1).toPath());
		byte[] extractedBytes2 = Files.readAllBytes(new File(extractedOutFileName2).toPath());
		
		assertTrue("Tableau extracted " + extractedOutFileName1 + " file available",  extractedBytes1.length > 0);
		assertTrue("Tableau extracted " + extractedOutFileName2 + " file available",  extractedBytes2.length > 0);
		System.out.println("batchResults are " + batchResults.toString());
		
		// Verify the URIs.
		String[] res = batchResults.toString().split("\\|");
		assertTrue("URI returned not correct", res[0].contains("/optic/view/test/masterDetail4.json") ||
				res[0].contains("/optic/view/test/masterDetail5.json"));
		assertTrue("URI returned not correct", res[1].contains("/optic/view/test/masterDetail4.json") ||
				res[1].contains("/optic/view/test/masterDetail5.json"));
		// Verify that rows are returned from both templates of the TDE file.
		String rowDetails = secondConsumerDetails.toString();
		assertTrue("Row returned not correct", rowDetails.contains("id=100, name=Master 100"));
		assertTrue("Row returned not correct", rowDetails.contains("id=200, name=Master 200, date=2016-04-02"));
		assertTrue("Row returned not correct", rowDetails.contains("id=100, name=Detail 100, masterId=100, amount=64.33, color=red"));
		assertTrue("Row returned not correct", rowDetails.contains("id=200, name=Detail 200, masterId=200, amount=89.36, color=blue"));
		assertTrue("Row returned not correct", rowDetails.contains("id=300, name=Detail 300, masterId=200, amount=72.9, color=yellow"));
		assertTrue("Row returned not correct", rowDetails.contains("id=400, name=Detail 400, masterId=200, amount=164.33, color=purple"));
		assertTrue("Row returned not correct", rowDetails.contains("id=500, name=Detail 500, masterId=100, amount=189.36, color=gold"));
		assertTrue("Row returned not correct", rowDetails.contains("id=600, name=Detail 600, masterId=100, amount=172.9, color=white"));
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		System.out.println("In tear down");		
		// release client
		client.release();
		cleanupRESTServer(dbName, fNames);
	}
}
