/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.*;
import com.marklogic.client.io.marker.TriplesReadHandle;
import com.marklogic.client.semantics.Capability;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.GraphPermissions;
import com.marklogic.client.semantics.RDFMimeTypes;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.Iterator;



@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestSemanticsGraphManager extends AbstractFunctionalTest {

  // private static final String DEFAULT_GRAPH =
  // "http://replace.defaultGraphValue.here/";
  private static GraphManager gmWriter;
  private static GraphManager gmReader;

  private DatabaseClient adminClient = null;
  private DatabaseClient writerClient = null;
  private DatabaseClient readerClient = null;
  private static String datasource = "src/test/java/com/marklogic/client/functionaltest/data/semantics/";

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    System.out.println("In tear down");
    deleteRESTUser("eval-user");
    deleteUserRole("test-eval");
    deleteUserRole("test-perm2");
  }

  @AfterEach
  public void testCleanUp() throws Exception {
    deleteDocuments(connectAsAdmin());
    adminClient.release();
    writerClient.release();
    readerClient.release();
    System.out.println("Running clear script");
  }

  @BeforeEach
  public void setUp() throws Exception {
    createUserRolesWithPrevilages("test-eval", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createRESTUser("eval-user", "x", "test-eval", "rest-admin", "rest-writer", "rest-reader");
    adminClient = newClientAsUser("rest-admin", "x");
    writerClient = newClientAsUser("rest-writer", "x");
    readerClient = newClientAsUser("rest-reader", "x");
    gmWriter = writerClient.newGraphManager();
    gmReader = readerClient.newGraphManager();
  }

  /*
   * Write Triples using Write user & StringHandle with mime-Type = n-triples
   * Get the list of Graphs from the DB with Read User Iterate through the list
   * of graphs to validate the Graph Delete Graph using URI Validate the graph
   * doesn't exist in DB
   */
  @Test
  public void testListUris_readUser() {
    Exception exp = null;
    String ntriple5 = "<http://example.org/s5> <http://example.com/p2> <http://example.org/o2> .";
    gmWriter.write("htp://test.sem.graph/G1", new StringHandle(ntriple5).withMimetype("application/n-triples"));
    Iterator<String> iter = gmReader.listGraphUris();
    while (iter.hasNext()) {
      String uri = iter.next();
      assertNotNull(uri);
      assertNotEquals("", uri);
      System.out.println("DEBUG: [GraphsTest] uri =[" + uri + "]");
    }
    try {
      gmWriter.delete("htp://test.sem.graph/G1");
      gmWriter.read("htp://test.sem.graph/G1", new StringHandle());
    } catch (Exception e) {
      exp = e;
    }
    assertTrue(
        exp.toString().contains("Could not read resource at graphs."));

    // Delete non existing graph

    try {
      gmWriter.delete("htp://test.sem.graph/G1");
    } catch (Exception e) {
      exp = e;
    }
    assertTrue(
        exp.toString().contains("Could not delete resource at graphs"),
		"Deleting non-existing Graph did not throw expected Exception:: http://bugtrack.marklogic.com/35064 , Received ::" + exp);
  }

  /*
   * Write Triples using Write user & StringHandle with mime-Type = n-triples
   * Get the list of Graphs from the DB with Write User Iterate through the list
   * of graphs to validate the Graph
   */

  @Test
  public void testListUris_writeUser() throws Exception {
    String ntriple5 = "<http://example.org/s22> <http://example.com/p22> <http://example.org/o22> .";

    gmWriter.write("htp://test.sem.graph/G2", new StringHandle(ntriple5)

        .withMimetype("application/n-triples"));
    try {
      Iterator<String> iter = gmWriter.listGraphUris();
      while (iter.hasNext()) {
        String uri = iter.next();
        assertNotNull(uri);
        assertNotEquals("", uri);
        System.out.println("DEBUG: [GraphsTest] uri =[" + uri + "]");
      }
    } catch (Exception e) {
    }
  }

  /*
   * Write Triples using Read user & StringHandle with mime-Type = n-triples
   * Catch the Exception and validate ForbiddenUser Exception with the Message.
   */
  @Test
  public void testWriteTriples_ReadUser() throws Exception {
    Exception exp = null;
    String ntriple5 = "<http://example.org/s33> <http://example.com/p33> <http://example.org/o33> .";

    try {
      gmReader.write("htp://test.sem.graph/G3", new StringHandle(ntriple5).withMimetype("application/n-triples"));

    } catch (Exception e) {
      exp = e;
    }
    assertTrue(
        exp.toString()
            .contains(
                "com.marklogic.client.ForbiddenUserException: Local message: User is not allowed to write resource at graphs. Server Message: "
                    + "You do not have permission to this method and URL"));

  }

  /*
   * Write Triples using Write user,Delete Graph using Read User Catch exception
   * and validate the correct Error message can also use RDFMimeTypes.NTRIPLES
   */
  @Test
  public void testWriteTriples_DeletereadUser() throws Exception {
    Exception exp = null;
    boolean exceptionThrown = false;
    String ntriple5 = "<http://example.org/s44> <http://example.com/p44> <http://example.org/o44> .";

    try {
      gmWriter.write("htp://test.sem.graph/G4", new StringHandle(ntriple5).withMimetype("application/n-triples"));
      gmReader.delete("htp://test.sem.graph/G4");

    } catch (Exception e) {
      exp = e;
      exceptionThrown = true;
    }
    if (exceptionThrown)
      assertTrue(
          exp.toString()
              .contains(
                  "com.marklogic.client.ForbiddenUserException: Local message: User is not allowed to delete resource at graphs. Server Message: "
                      + "You do not have permission to this method and URL"));
    assertTrue( exceptionThrown);

  }

  /*
   * Write & Read triples of type N-Triples using File & String handles
   */

  @Test
  public void testWrite_nTriples_FileHandle() throws Exception {
    File file = new File(datasource + "5triples.nt");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    gmWriter.write("htp://test.sem.graph/n", filehandle.withMimetype("application/n-triples"));
    StringHandle handle = gmWriter.read("htp://test.sem.graph/n", new StringHandle().withMimetype(RDFMimeTypes.NTRIPLES));
    assertTrue( handle.toString().contains("<http://jibbering.com/foaf/santa.rdf#bod>"));

  }

  /*
   * Write & Read RDF using ReaderHandle Validate the content in DB by
   * converting into String.
   */

  @Test
  public void testWrite_rdfxml_FileHandle() throws Exception {
    BufferedReader buffreader = new BufferedReader(new FileReader(datasource + "semantics.rdf"));
    ReaderHandle handle = new ReaderHandle();
    handle.set(buffreader);
    gmWriter.write("http://test.reader.handle/bufferreadtrig", handle.withMimetype(RDFMimeTypes.RDFXML));
    buffreader.close();
    ReaderHandle read = gmWriter.read("http://test.reader.handle/bufferreadtrig", new ReaderHandle().withMimetype(RDFMimeTypes.RDFXML));
    Reader readFile = read.get();
    String readContent = convertReaderToString(readFile);
    assertTrue(
        readContent.contains("http://www.daml.org/2001/12/factbook/vi#A113932"));
    handle.close();
    read.close();
  }

  /*
   * Write & Read triples of type ttl using bytehandle
   */
  @Test
  public void testWrite_ttl_FileHandle() throws Exception {
    File file = new File(datasource + "relative3.ttl");
    FileInputStream fileinputstream = new FileInputStream(file);
    ByteArrayOutputStream byteoutputstream = new ByteArrayOutputStream();
    byte[] buf = new byte[1024];
    // Get triples into bytes from file
    for (int readNum; (readNum = fileinputstream.read(buf)) != -1;) {
      byteoutputstream.write(buf, 0, readNum);
    }
    byte[] bytes = byteoutputstream.toByteArray();
    fileinputstream.close();
    byteoutputstream.close();
    BytesHandle contentHandle = new BytesHandle();
    contentHandle.set(bytes);
    // write triples in bytes to DB
    gmWriter.write("http://test.turtle.com/byteHandle", contentHandle.withMimetype(RDFMimeTypes.TURTLE));
    BytesHandle byteHandle = gmWriter.read("http://test.turtle.com/byteHandle", new BytesHandle().withMimetype(RDFMimeTypes.TURTLE));
    byte[] readInBytes = byteHandle.get();
    String readInString = new String(readInBytes);
    assertTrue( readInString.contains("#relativeIRI"));
  }

  /*
   * Read & Write triples using File Handle and parse the read results into json
   * using Jackson json node.
   */
  @Test
  public void testWrite_rdfjson_FileHandle() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    new ObjectMapper();
    File file = new File(datasource + "relative6.json");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    gmWriter.write("htp://test.sem.graph/rdfjson", filehandle.withMimetype("application/rdf+json"));
    FileHandle handle = new FileHandle();
    handle.setMimetype(RDFMimeTypes.RDFJSON);
    handle = gmWriter.read("htp://test.sem.graph/rdfjson", handle);
    File readFile = handle.get();
    JsonNode readContent = mapper.readTree(readFile);
    // Verify read content with inserted content
    assertTrue( readContent.toString()
        .contains("http://purl.org/dc/elements/1.1/title"));
  }

  // Write & Read triples of type N3 using ByteHandle
  @Test
  public void testWrite_n3_BytesHandle() throws Exception {
    File file = new File(datasource + "relative7.n3");
    FileInputStream fileinputstream = new FileInputStream(file);
    ByteArrayOutputStream byteoutputstream = new ByteArrayOutputStream();
    byte[] buf = new byte[1024];
    // Get triples into bytes from file
    for (int readNum; (readNum = fileinputstream.read(buf)) != -1;) {
      byteoutputstream.write(buf, 0, readNum);
    }
    byte[] bytes = byteoutputstream.toByteArray();
    fileinputstream.close();
    byteoutputstream.close();
    BytesHandle contentHandle = new BytesHandle();
    contentHandle.set(bytes);
    // write triples in bytes to DB
    gmWriter.write("http://test.n3.com/byte", contentHandle.withMimetype(RDFMimeTypes.N3));
    InputStreamHandle read = null;
    InputStream fileRead = null;
    InputStreamHandle ins = null;
	try {
		 ins = new InputStreamHandle().withMimetype(RDFMimeTypes.N3);
		 read = gmWriter.read("http://test.n3.com/byte", ins);

    fileRead = read.get();
    String readContent = convertInputStreamToString(fileRead);
    assertTrue(readContent.contains("/publications/journals/Journal1/1940"));
	} catch (Exception e) {
		e.printStackTrace();
	}
	finally {
		if (read != null)
			read.close();
		if (ins != null)
			ins.close();
		if (fileRead != null)
			fileRead.close();
	}
  }

  @Test
  public void testWrite_nquads_FileHandle() throws Exception {
    File file = new File(datasource + "relative2.nq");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    gmWriter.write("htp://test.sem.graph/nquads", filehandle.withMimetype("application/n-quads"));
    StringHandle handle = gmWriter.read("htp://test.sem.graph/nquads", new StringHandle().withMimetype(RDFMimeTypes.NQUADS));
    assertTrue( handle.toString().contains("<#electricVehicle2>"));
  }

  /*
   * Write same Triples into same graph twice & Read Triples of type Trig using
   * FileHandle Should not insert duplicate and Read should not result in
   * duplicates
   */

  @Test
  public void testWrite_trig_FileHandle() throws Exception {
    File file = new File(datasource + "semantics.trig");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    gmWriter.write("htp://test.sem.graph/trig", filehandle.withMimetype("application/trig"));
    gmWriter.write("htp://test.sem.graph/trig", filehandle.withMimetype("application/trig"));
    FileHandle handle = gmWriter.read("htp://test.sem.graph/trig", new FileHandle());
    File readFile = handle.get();
    String expectedContent = convertFileToString(readFile);
    assertTrue(
        expectedContent.contains("http://www.example.org/exampleDocument#Monica"));

  }

  /*
   * Open a Transaction Write tripleXML into DB using FileHandle & rest-writer
   * using the transaction Read the Graph and validate Graph exists in DB using
   * the same transaction Delete the Graph within the same transaction Read and
   * validate the delete by catching the exception Finally if the Transaction is
   * open Perform Rollback
   */

  @Test
  public void testWrite_triplexml_FileHandle() throws Exception {
    Transaction trx = writerClient.openTransaction();
    GraphManager gmWriter = writerClient.newGraphManager();
    File file = new File(datasource + "relative5.xml");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    // Write Graph using transaction
    gmWriter.write("htp://test.sem.graph/tripxml", filehandle.withMimetype(RDFMimeTypes.TRIPLEXML), trx);
    // Validate graph written to DB by reading within the same transaction
    StringHandle handle = gmWriter.read("htp://test.sem.graph/tripxml", new StringHandle().withMimetype(RDFMimeTypes.TRIPLEXML), trx);
    assertTrue( handle.toString().contains("Anna's Homepage"));
    // Delete Graph in the same transaction
    gmWriter.delete("htp://test.sem.graph/tripxml", trx);
    // Validate Graph is deleted
    try {
      gmWriter.read("htp://test.sem.graph/tripxml", new StringHandle(), trx);
      trx.commit();
      trx = null;
    } catch (ResourceNotFoundException e) {
      System.out.println(e);
    } finally {
      if (trx != null) {
        trx.rollback();
        trx = null;
      }

    }

  }

  /*
   * Insert rdf triples intoDB which does not have base URI defined validate
   * Undeclared base URI exception is thrown
   */

  @Test
  // GitIssue #319
  public void testWrite_rdfxml_WrongTriplesSchema() throws Exception {
    Exception exp = null;
    File file = new File(datasource + "relative4.rdf");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    try {
      gmWriter.write("htp://test.sem.graph/rdf", filehandle.withMimetype(RDFMimeTypes.RDFXML));
    } catch (Exception e) {
      exp = e;
    }
    assertTrue(exp.toString().contains("XDMP-BASEURI (err:FOER0000): Undeclared base URI"));

  }

  /*
   * -ve case to validate non-iri triples insert and read should throw
   * SEM-NOTRDF exception Note:: In RDF, the subject and predicate can only be
   * of type IRI
   */
  // Git issue #322 , server returns 500 error code
  @Test
  public void testRead_nonirirdfxml_FileHandle() throws Exception {
    StringHandle handle = new StringHandle();
    File file = new File(datasource + "non-iri.xml");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    gmWriter.write("htp://test.sem.graph/tripxmlnoniri", filehandle.withMimetype(RDFMimeTypes.TRIPLEXML));
    try {

      handle = gmWriter.read("htp://test.sem.graph/tripxmlnoniri", new StringHandle());

    } catch (Exception e) {
      assertTrue(e.toString().contains("SEM-NOTRDF") && e != null);
    }
    assertTrue( handle.get().toString().contains("5.11"));
  }

  /*
   * Write & Read NON -RDF format triples
   */
  @Test
  public void testRead_nonirin3_FileHandle() throws Exception {
    StringHandle handle = new StringHandle();
    File file = new File(datasource + "non-iri.n3");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    gmWriter.write("htp://test.sem.graph/n3noniri", filehandle.withMimetype(RDFMimeTypes.N3));
    try {

      handle = gmWriter.read("htp://test.sem.graph/n3noniri", new StringHandle().withMimetype(RDFMimeTypes.N3));
    } catch (Exception e) {
      assertTrue(e.toString().contains("SEM-NOTRDF") && e != null);

    }
    assertTrue( handle.get().toString().contains("p0:named_graph"));
  }

  /*
   * ReadAs & WriteAs N-Triples using File Type
   */
  @Test
  public void testReadAs_WriteAs() throws Exception {
    File file = new File(datasource + "semantics.nt");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    gmWriter.writeAs("htp://test.sem.graph/ntAs", filehandle.withMimetype(RDFMimeTypes.NTRIPLES));
    File read = gmWriter.readAs("htp://test.sem.graph/ntAs", File.class);
    String expectedContent = convertFileToString(read);
    assertTrue(expectedContent.contains("http://www.w3.org/2001/sw/RDFCore/ntriples/"));
  }

  /*
   * ReadAs & WriteAs N-Triples using File Type within a Transaction
   */
  @Test
  public void testWriteAs_ReadAs_FileHandle() throws Exception {
    Transaction trx = writerClient.openTransaction();
    GraphManager gmWriter = writerClient.newGraphManager();
    File file = new File(datasource + "semantics.ttl");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    // Write Graph using transaction
    gmWriter.writeAs("htp://test.sem.graph/ttlas", filehandle.withMimetype(RDFMimeTypes.TURTLE), trx);
    // Validate graph written to DB by reading within the same transaction
    File readFile = gmWriter.readAs("htp://test.sem.graph/ttlas", File.class, trx);
    String expectedContent = convertFileToString(readFile);
    assertTrue( expectedContent.contains("http://www.w3.org/2004/02/skos/core#Concept"));
    // Delete Graph in the same transaction
    gmWriter.delete("htp://test.sem.graph/ttlas", trx);
    trx.commit();
    // Validate Graph is deleted
    try {
      trx = writerClient.openTransaction();
      String readContent = gmWriter.readAs("htp://test.sem.graph/ttlas", String.class, trx);
      trx.commit();
      trx = null;
      assertTrue(readContent == null);
    } catch (ResourceNotFoundException e) {
      System.out.println(e);
    } finally {
      if (trx != null) {
        trx.rollback();
        trx = null;
      }

    }

  }

  /*
   * Merge Triples of same mime type Validate the triples are inserted correctly
   * & MergeAs & QuadsWriteHandle
   */
  @Test
  public void testMerge_trig_FileHandle() throws Exception {
    File file = new File(datasource + "trig.trig");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    File file2 = new File(datasource + "semantics.trig");
    FileHandle filehandle2 = new FileHandle();
    filehandle2.set(file2);
    gmWriter.write("htp://test.sem.graph/trigMerge", filehandle.withMimetype(RDFMimeTypes.TRIG));
    gmWriter.merge("htp://test.sem.graph/trigMerge", filehandle2.withMimetype(RDFMimeTypes.TRIG));
    FileHandle handle = gmWriter.read("htp://test.sem.graph/trigMerge", new FileHandle());
    File readFile = handle.get();
    String expectedContent = convertFileToString(readFile);
    assertTrue(
        expectedContent.contains("http://www.example.org/exampleDocument#Monica")
            && expectedContent.contains("http://purl.org/dc/elements/1.1/publisher"));
  }

  /*
   * Merge Triples of different mime type's Validate the triples are inserted
   * correctly
   */

  @Test
  public void testMerge_jsonxml_FileHandle() throws Exception {
    File file = new File(datasource + "bug25348.json");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    File file2 = new File(datasource + "relative5.xml");
    FileHandle filehandle2 = new FileHandle();
    filehandle2.set(file2);
    gmWriter.write("htp://test.sem.graph/trigM", filehandle.withMimetype(RDFMimeTypes.RDFJSON));
    gmWriter.merge("htp://test.sem.graph/trigM", filehandle2.withMimetype(RDFMimeTypes.TRIPLEXML));
    FileHandle handle = gmWriter.read("htp://test.sem.graph/trigM", new FileHandle().withMimetype(RDFMimeTypes.RDFJSON));
    File readFile = handle.get();
    String expectedContent = convertFileToString(readFile);
    assertTrue( expectedContent.contains("http://example.com/ns/person#firstName")
        && expectedContent.contains("Anna's Homepage"));
  }

  /*
   * WriteAs,MergeAs & ReadAs Triples of different mimetypes
   */
  @Test
  public void testMergeAs_jsonxml_FileHandle() throws Exception {
    gmWriter.setDefaultMimetype(RDFMimeTypes.RDFJSON);
    File file = new File(datasource + "bug25348.json");
    FileInputStream fis = new FileInputStream(file);
    Object graphData = convertInputStreamToString(fis);
    fis.close();
    gmWriter.writeAs("htp://test.sem.graph/jsonMergeAs", graphData);
    gmWriter.setDefaultMimetype(RDFMimeTypes.TRIPLEXML);
    File file2 = new File(datasource + "relative5.xml");
    Object graphData2 = convertFileToString(file2);
    gmWriter.mergeAs("htp://test.sem.graph/jsonMergeAs", graphData2);
    File readFile = gmWriter.readAs("htp://test.sem.graph/jsonMergeAs", File.class);
    String expectedContent = convertFileToString(readFile);
    assertTrue( expectedContent.contains("http://example.com/ns/person#firstName")
        && expectedContent.contains("Anna's Homepage"));
  }

  /*
   * WriteAs,MergeAs & ReadAs Triples of different mimetypes within a
   * Transaction & QuadsWriteHandle
   */

  @Test
  public void testMergeAs_jsonxml_Trx_FileHandle() throws Exception {
    Transaction trx = writerClient.openTransaction();
    GraphManager gmWriter = writerClient.newGraphManager();
    gmWriter.setDefaultMimetype(RDFMimeTypes.RDFJSON);
    File file = new File(datasource + "bug25348.json");
    FileInputStream fis = new FileInputStream(file);
    Object graphData = convertInputStreamToString(fis);
    fis.close();
    gmWriter.writeAs("htp://test.sem.graph/jsonMergeAsTrx", graphData, trx);
    File file2 = new File(datasource + "relative6.json");
    Object graphData2 = convertFileToString(file2);
    gmWriter.mergeAs("htp://test.sem.graph/jsonMergeAsTrx", graphData2, trx);
    File readFile = gmWriter.readAs("htp://test.sem.graph/jsonMergeAsTrx", File.class, trx);
    String expectedContent = convertFileToString(readFile);
    assertTrue( expectedContent.contains("http://example.com/ns/person#firstName")
        && expectedContent.contains("Anna's Homepage"));
    try {
      trx.commit();
      trx = null;
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (trx != null) {
        trx.rollback();
        trx = null;
      }
    }
  }

  /*
   * Merge Triples into Graph which already has the same triples Inserts two
   * documents into DB Read the graph , should not contain duplicate triples
   */

  @Test
  public void testMergeSameDoc_jsonxml_FileHandle() throws Exception {
    gmWriter.setDefaultMimetype(RDFMimeTypes.RDFJSON);
    File file = new File(datasource + "bug25348.json");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    gmWriter.write("htp://test.sem.graph/trigMSameDoc", filehandle);
    gmWriter.merge("htp://test.sem.graph/trigMSameDoc", filehandle);
    FileHandle handle = gmWriter.read("htp://test.sem.graph/trigMSameDoc", new FileHandle());
    File readFile = handle.get();
    String expectedContent = convertFileToString(readFile);
    assertTrue(
        expectedContent
            .equals("{\"http://example.com/ns/directory#m\":{\"http://example.com/ns/person#firstName\":[{\"value\":\"Michelle\", \""
                + "type\":\"literal\", \"datatype\":\"http://www.w3.org/2001/XMLSchema#string\"}]}}"));
  }

  /*
   * Merge & Delete With in Transaction
   */
  @Test
  public void testWriteMergeDelete_Trx() throws Exception {
    Transaction trx = writerClient.openTransaction();
    GraphManager gmWriter = writerClient.newGraphManager();
    File file = new File(datasource + "triplexml1.xml");
    gmWriter.write("htp://test.sem.graph/mergetrx", new FileHandle(file).withMimetype(RDFMimeTypes.TRIPLEXML), trx);
    file = new File(datasource + "bug25348.json");
    gmWriter.merge("htp://test.sem.graph/mergetrx", new FileHandle(file).withMimetype(RDFMimeTypes.RDFJSON), trx);
    FileHandle handle = gmWriter.read("htp://test.sem.graph/mergetrx", new FileHandle(), trx);
    File readFile = handle.get();
    String expectedContent = convertFileToString(readFile);
    try {
      assertTrue(
          expectedContent.contains("Michelle") && expectedContent.contains("Anna's Homepage"));
      gmWriter.delete("htp://test.sem.graph/mergetrx", trx);
      trx.commit();
      trx = null;
      StringHandle readContent = gmWriter.read("htp://test.sem.graph/mergetrx", new StringHandle(), trx);
      assertTrue(readContent == null);
    } catch (Exception e) {
      assertTrue( e.toString().contains("ResourceNotFoundException"));
    } finally {
      if (trx != null)
        trx.commit();
      trx = null;
    }
  }

  /*
   * Write and read triples into ML default graph
   */
  @Test
  public void testWriteRead_defaultGraph() throws FileNotFoundException {
    File file = new File(datasource + "semantics.trig");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    gmWriter.write(GraphManager.DEFAULT_GRAPH, filehandle.withMimetype(RDFMimeTypes.TRIG));
    FileHandle handle = gmWriter.read(GraphManager.DEFAULT_GRAPH, new FileHandle());
    GraphPermissions permissions = gmWriter.getPermissions(GraphManager.DEFAULT_GRAPH);
    System.out.println(permissions);
    assertEquals(Capability.UPDATE, permissions.get("rest-writer").iterator().next());
    assertEquals(Capability.READ, permissions.get("rest-reader").iterator().next());
    gmWriter.deletePermissions(GraphManager.DEFAULT_GRAPH);
    permissions = gmWriter.getPermissions(GraphManager.DEFAULT_GRAPH);
    System.out.println(permissions);
    assertEquals(Capability.UPDATE, permissions.get("rest-writer").iterator().next());
    File readFile = handle.get();
    String expectedContent = convertFileToString(readFile);
    System.out.println(gmWriter.listGraphUris().next().toString());
    assertTrue(gmWriter.listGraphUris().next().toString().equals("http://marklogic.com/semantics#default-graph"));
    assertTrue(expectedContent.contains("http://www.example.org/exampleDocument#Monica"));
  }

  /*
   * Write Triples of Type JSON Merge NTriples into the same graph and validate
   * ReplaceGraphs with File handle & Nquads mime-type and validate &
   * DeleteGraphs and validate ResourceNotFound Exception
   */
  @Test
  public void testMergeReplace_quads() throws FileNotFoundException, InterruptedException {
    String uri = "http://test.sem.quads/json-quads";
    String ntriple6 = "<http://example.org/s6> <http://example.com/mergeQuadP> <http://example.org/o2> <http://test.sem.quads/json-quads>.";
    File file = new File(datasource + "bug25348.json");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    gmWriter.write(uri, filehandle.withMimetype(RDFMimeTypes.RDFJSON));
    gmWriter.mergeGraphs(new StringHandle(ntriple6).withMimetype(RDFMimeTypes.NQUADS));
    FileHandle handle = gmWriter.read(uri, new FileHandle());
    File readFile = handle.get();
    String expectedContent = convertFileToString(readFile);
    assertTrue( expectedContent.contains("<http://example.com/mergeQuadP"));

    file = new File(datasource + "relative2.nq");
    gmWriter.replaceGraphs(new FileHandle(file).withMimetype(RDFMimeTypes.NQUADS));
    uri = "http://originalGraph";
    StringHandle readQuads = gmWriter.read(uri, new StringHandle());
    assertTrue( readQuads.toString().contains("#electricVehicle2"));
    gmWriter.deleteGraphs();

    try {
      StringHandle readContent = gmWriter.read(uri, new StringHandle());
      assertTrue( readContent.get() == null);

    } catch (Exception e) {
      assertTrue( e.toString().contains("ResourceNotFoundException"));
    }
  }

  /*
   * Replace with Large Number(600+) of Graphs of type nquads and validate Merge
   * different quads with different graph and validate deletegraphs and validate
   * resourcenotfound exception
   */

  @Test
  public void testMergeReplaceAs_Quads() throws Exception {
    gmWriter.setDefaultMimetype(RDFMimeTypes.NQUADS);
    File file = new File(datasource + "semantics.nq");
    gmWriter.replaceGraphsAs(file);
    String uri = "http://en.wikipedia.org/wiki/Alexander_I_of_Serbia?oldid=492189987#absolute-line=1";
    StringHandle readQuads = gmWriter.read(uri, new StringHandle());
    assertTrue( readQuads.toString().contains("http://dbpedia.org/ontology/Monarch"));

    file = new File(datasource + "relative2.nq");
    gmWriter.mergeGraphsAs(file);
    uri = "http://originalGraph";
    readQuads = gmWriter.read(uri, new StringHandle());
    assertTrue( readQuads.toString().contains("#electricVehicle2"));

    gmWriter.deleteGraphs();
    try {
      StringHandle readContent = gmWriter.read(uri, new StringHandle());
      assertTrue( readContent.get() == null);

    } catch (Exception e) {

      assertTrue( e.toString().contains("ResourceNotFoundException"));
    }
  }

  @Test
  public void testThingsAs() throws Exception {
    gmWriter.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
    File file = new File(datasource + "relative1.nt");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    gmWriter.write("http://test.things.com/", filehandle);
    String things = gmWriter.thingsAs(String.class, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    assertTrue(
        things.equals("<#electricVehicle2> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://people.aifb.kit.edu/awa/2011/smartgrid/schema/smartgrid#ElectricVehicle> ."));
  }

  @Test
  public void testThings_file() throws Exception {
    gmWriter.setDefaultMimetype(RDFMimeTypes.TRIPLEXML);
    String tripleGraphUri = "http://test.things.com/file";
    File file = new File(datasource + "relative5.xml");
    gmWriter.write(tripleGraphUri, new FileHandle(file));
    TriplesReadHandle things = gmWriter.things(new StringHandle(), "about");
    assertTrue(things.toString().contains("<about> <http://purl.org/dc/elements/1.1/title> \"Anna's Homepage\" ."));
    gmWriter.delete(tripleGraphUri);
  }

  @Test
  public void testWrite_rdfjson_JacksonHandle() throws Exception {
    File file = new File(datasource + "relative6.json");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    gmWriter.write("htp://test.sem.graph/rdfjson", filehandle.withMimetype("application/rdf+json"));
    JacksonHandle handle1 = gmWriter.read("htp://test.sem.graph/rdfjson", new JacksonHandle());
    JsonNode readFile = handle1.get();
    assertTrue( readFile.toString().contains("http://purl.org/dc/elements/1.1/title"));
  }

  @Test
  public void testWrite_rdfjson_TripleReadHandle() throws Exception {
    File file = new File(datasource + "relative6.json");
    FileHandle filehandle = new FileHandle();
    filehandle.set(file);
    gmWriter.write("htp://test.sem.graph/rdfjson", filehandle.withMimetype("application/rdf+json"));
    TriplesReadHandle handle1 = gmWriter.read("htp://test.sem.graph/rdfjson", new JacksonHandle());
    assertTrue( handle1.toString().contains("http://purl.org/dc/elements/1.1/title"));
  }

  @Test
  public void testThings_fileNomatch() throws Exception {
    gmWriter.setDefaultMimetype(RDFMimeTypes.TRIPLEXML);
    String tripleGraphUri = "http://test.things.com/file";
    File file = new File(datasource + "relative5.xml");
    gmWriter.write(tripleGraphUri, new FileHandle(file));
    Exception exp = null;
    try {
      TriplesReadHandle things = gmWriter.things(new StringHandle(), "noMatch");
      assertTrue(things == null);
    } catch (Exception e) {
      exp = e;
    }
    assertTrue(exp.toString().contains("ResourceNotFoundException"));
    gmWriter.delete(tripleGraphUri);
  }

  @Test
  public void testThings_Differentmimetypes() throws Exception {
    gmWriter.setDefaultMimetype(RDFMimeTypes.TRIPLEXML);
    String tripleGraphUri = "http://test.things.com/multiple";
    File file = new File(datasource + "relative5.xml");
    gmWriter.write(tripleGraphUri, new FileHandle(file));
    file = new File(datasource + "semantics.ttl");
    gmWriter.merge(tripleGraphUri, new FileHandle(file).withMimetype(RDFMimeTypes.TURTLE));
    StringHandle things = gmWriter.things(new StringHandle(), "http://dbpedia.org/resource/Hadoop");
    assertTrue(things.get().contains("Apache Hadoop"));
    gmWriter.delete(tripleGraphUri);
  }

  // TODO:: Re-write this Method into multiple tests after 8.0-4 release
  @Test
  public void testPermissions_noTrx() throws Exception {
    File file = new File(datasource + "semantics.rdf");
    FileHandle handle = new FileHandle();
    handle.set(file);
    String uri = "test_permissions11";
    // Create Role
    createUserRolesWithPrevilages("test-perm");
    // Create User with Above Role
    createRESTUser("perm-user", "x", "test-perm");
    DatabaseClient permUser = newClientAsUser("perm-user", "x");
    // Create GraphManager with Above client
    GraphManager gmTestPerm = permUser.newGraphManager();
    // Set Update Capability for the Created User
    GraphPermissions perms = gmTestPerm.permission("test-perm", Capability.UPDATE);
    // Write Graph with triples into DB
    gmTestPerm.write(uri, handle.withMimetype(RDFMimeTypes.RDFXML), perms);
    waitForPropertyPropagate();
    // Get PErmissions for the User and Validate
    System.out.println("Permissions after create , Should not see Execute" + gmTestPerm.getPermissions(uri));
    perms = gmTestPerm.getPermissions(uri);
    for (Capability capability : perms.get("test-perm")) {
      assertTrue(capability == Capability.UPDATE);
    }

    // Create another Role to check for builder style permissions support.
    createUserRolesWithPrevilages("test-perm2");

    // Set Capability for the User
    perms = gmTestPerm.permission("test-perm", Capability.EXECUTE).permission("test-perm2", Capability.EXECUTE).permission("test-perm2", Capability.READ);
    // Merge Permissions
    gmTestPerm.mergePermissions(uri, perms);
    // gmTestPerm.writePermissions(uri, perms);
    // Get Permissions And Validate
    perms = gmTestPerm.getPermissions(uri);
    System.out.println("Permissions after setting execute , Should  see Execute & Update" + gmTestPerm.getPermissions(uri));
    for (Capability capability : perms.get("test-perm")) {
      assertTrue(capability == Capability.UPDATE
          || capability == Capability.EXECUTE);
    }
    for (Capability capability : perms.get("test-perm2")) {
      assertTrue(capability == Capability.READ
          || capability == Capability.EXECUTE);
    }
    assertTrue( perms.get("test-perm").size() == 2);
    assertTrue( perms.get("test-perm2").size() == 2);

    // Write Read permission to uri and validate permissions are overwritten
    // with write
    perms = gmTestPerm.permission("test-perm", Capability.READ);
    gmTestPerm.write(uri, handle.withMimetype(RDFMimeTypes.RDFXML), perms);
    for (Capability capability : perms.get("test-perm")) {
      assertTrue(capability == Capability.READ);
    }
    assertTrue( perms.get("test-perm").size() == 1);

    // Delete Permissions and Validate
    gmTestPerm.deletePermissions(uri);
    perms = gmTestPerm.getPermissions(uri);
    assertNull(perms.get("test-perm"));
    assertNull(perms.get("test-perm2"));

    // Set and Write Execute Permission
    perms = gmTestPerm.permission("test-perm", Capability.EXECUTE);

    gmTestPerm.writePermissions(uri, perms);
    // Read and Validate triples
    try {
      gmTestPerm.read(uri, handle.withMimetype(RDFMimeTypes.RDFXML));
      ReaderHandle read = gmTestPerm.read(uri, new ReaderHandle());
      Reader readFile = read.get();
      String readContent = convertReaderToString(readFile);
      assertTrue(readContent.contains("http://www.daml.org/2001/12/factbook/vi#A113932"));
    } catch (Exception e) {
      System.out.println("Tried to Read  and validate triples, should  see this exception ::" + e);
    }
    System.out.println("Permissions after setting execute , Should  see Execute & Update & Read " + gmTestPerm.getPermissions(uri));

    // delete all capabilities
    gmTestPerm.deletePermissions(uri);

    System.out.println("Capabilities after delete , Should not see Execute" + gmTestPerm.getPermissions(uri));

    // set Update and perform Read
    perms = gmTestPerm.permission("test-perm", Capability.UPDATE);
    gmTestPerm.mergePermissions(uri, perms);
    try {
      gmTestPerm.read(uri, new ReaderHandle());
    } catch (Exception e) {
      System.out.println(" Should receive unauthorized exception ::" + e);
    }

    // Delete
    gmTestPerm.deletePermissions(uri);

    // Set to Read and Perform Merge
    perms = gmTestPerm.permission("test-perm", Capability.READ);
    gmTestPerm.mergePermissions(uri, perms);
    try {
      gmTestPerm.merge(uri, handle);
    } catch (Exception e) {
      System.out.println(" Should receive unauthorized exception ::" + e);
    }
    // Read and validate triples
    ReaderHandle read = gmTestPerm.read(uri, new ReaderHandle());
    Reader readFile = read.get();
    String readContent = convertReaderToString(readFile);
    assertTrue(readContent.contains("http://www.daml.org/2001/12/factbook/vi#A113932"));

    // Delete the role
    deleteUserRole("test-perm2");
  }

  /*
   * -ve cases for permission outside transaction and after rollback of
   * transaction
   */
  @Test
  public void testPermissions_withtrxNeg() throws Exception {
    File file = new File(datasource + "semantics.rdf");
    FileHandle handle = new FileHandle();
    handle.set(file);
    String uri = "test_permissions12";
    // Create Role
    createUserRolesWithPrevilages("test-perm");
    // Create User with Above Role
    createRESTUser("perm-user", "x", "test-perm");
    DatabaseClient permUser = newClientAsUser("perm-user", "x");
    // Create GraphManager with Above client

    Transaction trx = permUser.openTransaction();
    try {
      GraphManager gmTestPerm = permUser.newGraphManager();
      // Set Update Capability for the Created User
      GraphPermissions perms = gmTestPerm.permission("test-perm", Capability.UPDATE);
      gmTestPerm.write(uri, handle.withMimetype(RDFMimeTypes.RDFXML), trx);
      trx.commit();

      trx = permUser.openTransaction();
      gmTestPerm.mergePermissions(uri, perms, trx);
      // Validate test-perm role not available outside transaction
      GraphPermissions perm = gmTestPerm.getPermissions(uri);
      System.out.println("OUTSIDE TRX , SHOULD NOT SEE test-perm EXECUTE" + perm);
      assertNull(perm.get("test-perm"));
      perms = gmTestPerm.getPermissions(uri, trx);
      assertTrue( perms.get("test-perm").contains(Capability.UPDATE));
      trx.rollback();
      trx = null;
      perms = gmTestPerm.getPermissions(uri);
      assertNull(perm.get("test-perm"));
    } catch (Exception e) {

    } finally {
      if (trx != null) {
        trx.commit();
        trx = null;
      }
    }

  }

  // TODO:: Re-write this Method into multiple tests after 8.0-4 release
  @Test
  public void testPermissions_withTrx() throws Exception {
    File file = new File(datasource + "semantics.rdf");
    FileHandle handle = new FileHandle();
    handle.set(file);
    String uri = "test_permissions12";
    // Create Role
    createUserRolesWithPrevilages("test-perm");
    // Create User with Above Role
    createRESTUser("perm-user", "x", "test-perm");
    DatabaseClient permUser = newClientAsUser("perm-user", "x");

    // Create GraphManager with Above client
    Transaction trx = permUser.openTransaction();
    GraphManager gmTestPerm = permUser.newGraphManager();
    // Set Update Capability for the Created User
    GraphPermissions perms = gmTestPerm.permission("test-perm", Capability.UPDATE);
    // Write Graph with triples into DB
    try {
      gmTestPerm.write(uri, handle.withMimetype(RDFMimeTypes.RDFXML), perms, trx);
      // Get PErmissions for the User and Validate
      System.out.println("Permissions after create , Should not see Execute" + gmTestPerm.getPermissions(uri, trx));
      perms = gmTestPerm.getPermissions(uri, trx);
      for (Capability capability : perms.get("test-perm")) {
        assertTrue(capability == Capability.UPDATE);
      }

      // Set Capability for the User
      perms = gmTestPerm.permission("test-perm", Capability.EXECUTE);
      // Merge Permissions
      gmTestPerm.mergePermissions(uri, perms, trx);
      // Get Permissions And Validate
      perms = gmTestPerm.getPermissions(uri, trx);
      System.out.println("Permissions after setting execute , Should  see Execute & Update" + gmTestPerm.getPermissions(uri, trx));
      for (Capability capability : perms.get("test-perm")) {
        assertTrue(capability == Capability.UPDATE || capability == Capability.EXECUTE);
      }

      // Validate write with Update and Execute permissions
      try {
        gmTestPerm.write(uri, handle.withMimetype(RDFMimeTypes.RDFXML), perms, trx);
      } catch (Exception e) {
        System.out.println("Tried to Write Here ::" + e);//
      }
      System.out.println("Permissions after setting execute , Should  see Execute & Update" + gmTestPerm.getPermissions(uri, trx));

      // Delete Permissions and Validate
      gmTestPerm.deletePermissions(uri, trx);
      try {
        perms = gmTestPerm.getPermissions(uri, trx);
      } catch (Exception e) {
        System.out
            .println("Permissions after setting execute , Should  see Execute & Update" + gmTestPerm.getPermissions(uri, trx));
      }
      assertNull(perms.get("test-perm"));

      // Set and Write Execute Permission
      perms = gmTestPerm.permission("test-perm", Capability.EXECUTE, Capability.READ);

      gmTestPerm.writePermissions(uri, perms, trx);
      gmTestPerm.write(uri, handle.withMimetype(RDFMimeTypes.RDFXML), perms, trx);
      waitForPropertyPropagate();

      // Read and Validate triples
      try {
        ReaderHandle read = gmTestPerm.read(uri, new ReaderHandle().withMimetype(RDFMimeTypes.RDFXML), trx);

        Reader readFile = read.get();
        StringBuilder readContentSB = new StringBuilder();
        BufferedReader br = null;
		try {
			br = new BufferedReader(readFile);
			String line = null;
			while ((line = br.readLine()) != null)
				readContentSB.append(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
        br.close();
		}
		String readContent = readContentSB.toString();
		System.out.println("Triples read back from Server is " + readContent);
        assertTrue(readContent.contains("http://www.daml.org/2001/12/factbook/vi#A113932"));
      } catch (Exception e) {
        System.out.println("Tried to Read  and validate triples, should not see this exception ::" + e);
      }
      System.out.println("Permissions after setting execute , Should  see Execute & Read "
          + gmTestPerm.getPermissions(uri, trx));

      // delete all capabilities
      gmTestPerm.deletePermissions(uri, trx);

      System.out.println("Capabilities after delete , Should not see Execute" + gmTestPerm.getPermissions(uri, trx));

      // set Update and perform Read
      perms = gmTestPerm.permission("test-perm", Capability.UPDATE);
      gmTestPerm.mergePermissions(uri, perms, trx);
      try {
        gmTestPerm.read(uri, new ReaderHandle(), trx);
      } catch (Exception e) {
        System.out.println(" Should receive unauthorized exception ::" + e);
      }

      // Delete
      gmTestPerm.deletePermissions(uri, trx);

      // Set to Read and Perform Merge
      perms = gmTestPerm.permission("test-perm", Capability.READ);

      gmTestPerm.mergePermissions(uri, perms, trx);
      perms = gmTestPerm.permission("test-perm", Capability.READ);
      gmTestPerm.mergePermissions(uri, perms, trx);
      try {
        gmTestPerm.merge(uri, handle, trx);
      } catch (Exception e) {
        System.out.println(" Should receive unauthorized exception ::" + e);
      }
      // Read and validate triples
      ReaderHandle read = gmTestPerm.read(uri, new ReaderHandle(), trx);
      Reader readFile = read.get();
      String readContent = convertReaderToString(readFile);
      assertTrue(readContent.contains("http://www.daml.org/2001/12/factbook/vi#A113932"));
      trx.commit();
      trx = null;

    } catch (Exception e) {

    } finally {
      if (trx != null) {
        trx.commit();
        trx = null;
      }
    }
  }

  /*
   * Write Triples of Type JSON Merge NTriples into the same graph and validate
   * mergeGraphs with transactions.
   *
   * Merge within same write transaction Write and merge Triples within
   * different transactions. Commit the merge transaction Write and merge
   * Triples within different transactions. Rollback the merge transaction Write
   * and merge Triples within different transactions. Rollback the merge
   * transaction and then commit.
   */
  @Test
  public void testMergeGraphWithTransaction() throws FileNotFoundException, InterruptedException {
    String uri = "http://test.sem.quads/json-quads";
    Transaction trxIn = writerClient.openTransaction();
    Transaction trxInMergeGraph = null;
    Transaction trxDelIn = null;
    try {
      String ntriple6 = "<http://example.org/s6> <http://example.com/mergeQuadP> <http://example.org/o2> <http://test.sem.quads/json-quads>.";
      File file = new File(datasource + "bug25348.json");
      FileHandle filehandle = new FileHandle();
      filehandle.set(file);

      // Using client write and merge Triples within same transaction.
      gmWriter.write(uri, filehandle.withMimetype(RDFMimeTypes.RDFJSON), trxIn);
      // Merge Graphs inside the transaction.
      gmWriter.mergeGraphs(new StringHandle(ntriple6).withMimetype(RDFMimeTypes.NQUADS), trxIn);
      trxIn.commit();
      FileHandle handle = gmWriter.read(uri, new FileHandle());
      File readFile = handle.get();
      String expectedContent = convertFileToString(readFile);
      assertTrue( expectedContent.contains("<http://example.com/ns/person#firstName"));
      assertTrue( expectedContent.contains("<http://example.com/mergeQuadP"));
      trxIn = null;
      handle = null;
      readFile = null;
      expectedContent = null;

      // Delete Graphs inside the transaction.
      trxDelIn = writerClient.openTransaction();
      gmWriter.delete(uri, trxDelIn);
      trxDelIn.commit();
      trxDelIn = null;

      // Using client write and merge Triples within different transactions.
      // Commit the merge transaction
      trxIn = writerClient.openTransaction();
      trxInMergeGraph = writerClient.openTransaction();

      gmWriter.write(uri, filehandle.withMimetype(RDFMimeTypes.RDFJSON), trxIn);
      trxIn.commit();
      // Make sure that original triples are available.
      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);
      assertTrue( expectedContent.contains("<http://example.com/ns/person#firstName"));
      handle = null;
      readFile = null;
      expectedContent = null;

      // Merge Graphs inside another transaction.
      gmWriter.mergeGraphs(new StringHandle(ntriple6).withMimetype(RDFMimeTypes.NQUADS), trxInMergeGraph);
      trxInMergeGraph.commit();

      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);
      assertTrue( expectedContent.contains("<http://example.com/ns/person#firstName"));
      assertTrue( expectedContent.contains("<http://example.com/mergeQuadP"));

      trxIn = null;
      trxInMergeGraph = null;
      handle = null;
      readFile = null;
      expectedContent = null;
      trxDelIn = null;

      // Delete Graphs inside the transaction.
      trxDelIn = writerClient.openTransaction();
      gmWriter.delete(uri, trxDelIn);
      trxDelIn.commit();
      trxDelIn = null;

      // Using client write and merge Triples within different transaction.
      // Rollback the merge transaction.
      trxIn = writerClient.openTransaction();
      trxInMergeGraph = writerClient.openTransaction();

      gmWriter.write(uri, filehandle.withMimetype(RDFMimeTypes.RDFJSON), trxIn);
      trxIn.commit();

      // Make sure that original triples are available.
      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);
      assertTrue( expectedContent.contains("<http://example.com/ns/person#firstName"));
      handle = null;
      readFile = null;
      expectedContent = null;
      // Merge Graphs inside the transaction.
      gmWriter.mergeGraphs(new StringHandle(ntriple6).withMimetype(RDFMimeTypes.NQUADS), trxInMergeGraph);
      trxInMergeGraph.rollback();
      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);

      // Verify if original quad is available.
      assertTrue( expectedContent.contains("<http://example.com/ns/person#firstName"));
      assertFalse(expectedContent.contains("<http://example.com/mergeQuadP"));

      trxIn = null;
      trxInMergeGraph = null;
      handle = null;
      readFile = null;
      expectedContent = null;

      // Delete Graphs inside the transaction.
      trxDelIn = writerClient.openTransaction();
      gmWriter.delete(uri, trxDelIn);
      trxDelIn.commit();
      trxDelIn = null;

      // Using client write and merge Triples within different transaction.
      // Rollback the merge transaction and then commit.
      trxIn = writerClient.openTransaction();
      trxInMergeGraph = writerClient.openTransaction();

      gmWriter.write(uri, filehandle.withMimetype(RDFMimeTypes.RDFJSON), trxIn);
      trxIn.commit();

      // Make sure that original triples are available.
      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);
      assertTrue( expectedContent.contains("<http://example.com/ns/person#firstName"));
      handle = null;
      readFile = null;
      expectedContent = null;

      // Merge Graphs inside the transaction.
      gmWriter.mergeGraphs(new StringHandle(ntriple6).withMimetype(RDFMimeTypes.NQUADS), trxInMergeGraph);
      // Rollback the merge.
      trxInMergeGraph.rollback();
      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);

      // Verify if original quad is available.
      assertTrue( expectedContent.contains("<http://example.com/ns/person#firstName"));
      assertFalse(expectedContent.contains("<http://example.com/mergeQuadP"));

      trxIn = null;
      trxInMergeGraph = null;
      handle = null;
      readFile = null;
      expectedContent = null;
      trxInMergeGraph = writerClient.openTransaction();
      gmWriter.mergeGraphs(new StringHandle(ntriple6).withMimetype(RDFMimeTypes.NQUADS), trxInMergeGraph);
      // Commit the merge.
      trxInMergeGraph.commit();

      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);
      // Verify if original quad is available.
      assertTrue( expectedContent.contains("<http://example.com/ns/person#firstName"));
      assertTrue( expectedContent.contains("<http://example.com/mergeQuadP"));

      // Delete Graphs inside the transaction.
      trxDelIn = writerClient.openTransaction();
      gmWriter.delete(uri, trxDelIn);
      trxDelIn.commit();
      waitForPropertyPropagate();
      trxDelIn = null;
      trxIn = null;
      trxInMergeGraph = null;
      handle = null;
      readFile = null;
      expectedContent = null;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (trxIn != null) {
        trxIn.rollback();
        trxIn = null;
      }
      if (trxDelIn != null) {
        trxDelIn.rollback();
        trxDelIn = null;
      }
      if (trxInMergeGraph != null) {
        trxInMergeGraph.rollback();
        trxInMergeGraph = null;
      }
    }
  }

  /*
   * Write Triples of Type JSON replace NTriples into the same graph and
   * validate replaceGraphs with transactions.
   *
   * Replace within same write transaction Write and replace Triples within
   * different transactions. Commit the replace transaction Write and replace
   * Triples within different transactions. Rollback the replace transaction
   * Write and replace Triples within different transactions. Rollback the
   * replace transaction and then commit.
   */
  @Test
  public void testReplaceGraphWithTransaction() throws FileNotFoundException, InterruptedException {
    String uri = "http://test.sem.quads/json-quads";
    Transaction trxIn = writerClient.openTransaction();
    Transaction trxDelIn = null;
    Transaction trxInReplaceGraph = null;

    try {
      String ntriple6 = "<http://example.org/s6> <http://example.com/mergeQuadP> <http://example.org/o2> <http://test.sem.quads/json-quads>.";
      File file = new File(datasource + "bug25348.json");
      FileHandle filehandle = new FileHandle();
      filehandle.set(file);

      // Using client write and replace Triples within same transaction.
      gmWriter.write(uri, filehandle.withMimetype(RDFMimeTypes.RDFJSON), trxIn);
      // Replace Graphs inside the transaction.
      gmWriter.replaceGraphs(new StringHandle(ntriple6).withMimetype(RDFMimeTypes.NQUADS), trxIn);
      trxIn.commit();
      FileHandle handle = gmWriter.read(uri, new FileHandle());
      File readFile = handle.get();
      String expectedContent = convertFileToString(readFile);

      // Should not contain original triples
      assertFalse( expectedContent.contains("http://example.com/ns/person#firstName"));
      // Should contain new triples
      assertTrue( expectedContent.contains("<http://example.com/mergeQuadP"));

      trxIn = null;
      handle = null;
      readFile = null;
      expectedContent = null;

      // Delete Graphs inside the transaction.
      trxDelIn = writerClient.openTransaction();
      gmWriter.delete(uri, trxDelIn);
      trxDelIn.commit();
      trxDelIn = null;

      // Using client write and replace Triples within different transactions.
      // Commit the replace transaction
      trxIn = writerClient.openTransaction();

      gmWriter.write(uri, filehandle.withMimetype(RDFMimeTypes.RDFJSON), trxIn);
      trxIn.commit();
      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);
      // Should contain original triples
      assertTrue( expectedContent.contains("http://example.com/ns/person#firstName"));

      // Replace Graphs inside another transaction.
      trxInReplaceGraph = writerClient.openTransaction();
      gmWriter.replaceGraphs(new StringHandle(ntriple6).withMimetype(RDFMimeTypes.NQUADS), trxInReplaceGraph);
      trxInReplaceGraph.commit();
      handle = null;
      readFile = null;
      expectedContent = null;

      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);
      // Should not contain original triples
      assertFalse( expectedContent.contains("http://example.com/ns/person#firstName"));
      // Should contain new triples
      assertTrue( expectedContent.contains("<http://example.com/mergeQuadP"));

      trxIn = null;
      trxInReplaceGraph = null;
      handle = null;
      readFile = null;
      expectedContent = null;
      trxDelIn = null;

      // Delete Graphs inside the transaction.
      trxDelIn = writerClient.openTransaction();
      gmWriter.delete(uri, trxDelIn);
      trxDelIn.commit();

      // Using client write and replace Triples within different transaction.
      // Rollback the replace transaction.
      trxIn = writerClient.openTransaction();
      trxInReplaceGraph = writerClient.openTransaction();

      gmWriter.write(uri, filehandle.withMimetype(RDFMimeTypes.RDFJSON), trxIn);
      trxIn.commit();
      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);
      // Should contain original triples
      assertTrue( expectedContent.contains("http://example.com/ns/person#firstName"));

      handle = null;
      readFile = null;
      expectedContent = null;

      // Replace Graphs inside the transaction.
      gmWriter.replaceGraphs(new StringHandle(ntriple6).withMimetype(RDFMimeTypes.NQUADS), trxInReplaceGraph);
      trxInReplaceGraph.rollback();

      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);

      // Should not contain original triples
      assertTrue( expectedContent.contains("http://example.com/ns/person#firstName"));
      // Should contain new triples
      assertFalse( expectedContent.contains("<http://example.com/mergeQuadP"));

      trxIn = null;
      trxInReplaceGraph = null;
      handle = null;
      readFile = null;
      expectedContent = null;

      // Delete Graphs inside the transaction.
      trxDelIn = writerClient.openTransaction();
      gmWriter.delete(uri, trxDelIn);
      trxDelIn.commit();
      trxDelIn = null;

      // Using client write and replace Triples within different transaction.
      // Rollback the replace transaction and then commit.
      trxIn = writerClient.openTransaction();
      trxInReplaceGraph = writerClient.openTransaction();

      gmWriter.write(uri, filehandle.withMimetype(RDFMimeTypes.RDFJSON), trxIn);
      trxIn.commit();
      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);
      // Should contain original triples
      assertTrue( expectedContent.contains("http://example.com/ns/person#firstName"));
      handle = null;
      readFile = null;
      expectedContent = null;
      // Replace Graphs inside the transaction.
      gmWriter.replaceGraphs(new StringHandle(ntriple6).withMimetype(RDFMimeTypes.NQUADS), trxInReplaceGraph);
      trxInReplaceGraph.rollback();
      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);

      // Should not contain original triples
      assertTrue( expectedContent.contains("http://example.com/ns/person#firstName"));
      // Should contain new triples
      assertFalse( expectedContent.contains("<http://example.com/mergeQuadP"));

      trxIn = null;
      trxInReplaceGraph = null;
      handle = null;
      readFile = null;
      expectedContent = null;

      trxInReplaceGraph = writerClient.openTransaction();
      gmWriter.replaceGraphs(new StringHandle(ntriple6).withMimetype(RDFMimeTypes.NQUADS), trxInReplaceGraph);
      trxInReplaceGraph.commit();

      handle = gmWriter.read(uri, new FileHandle());
      readFile = handle.get();
      expectedContent = convertFileToString(readFile);
      // Should not contain original triples
      assertFalse( expectedContent.contains("http://example.com/ns/person#firstName"));
      // Should contain new triples
      assertTrue( expectedContent.contains("<http://example.com/mergeQuadP"));

      // Delete Graphs inside the transaction.
      trxDelIn = writerClient.openTransaction();
      gmWriter.delete(uri, trxDelIn);
      trxDelIn.commit();
      waitForPropertyPropagate();
      trxDelIn = null;
      handle = null;
      readFile = null;
      expectedContent = null;
      trxInReplaceGraph = null;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (trxIn != null)
      {
        trxIn.rollback();
        trxIn = null;
      }
      if (trxDelIn != null)
      {
        trxDelIn.rollback();
        trxDelIn = null;
      }
      if (trxInReplaceGraph != null)
      {
        trxInReplaceGraph.rollback();
        trxInReplaceGraph = null;
      }
    }
  }
}
