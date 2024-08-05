/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.functionaltest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClient.ConnectionType;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.dataservices.*;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.*;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BulkIOCallersFnTest extends BasicJavaClientREST {
    private static String dbName = "DynamicIngestServicesDB";
    private static String[] fNames = { "DynamicIngestServices-1" };

    private static String dbNameMod = "DynamicIngestServicesDBModules";
    private static String[] fNamesMod = { "DynamicIngestServicesDBModules-1" };

    private static String modServerName = "App-Services";
    private static String user = "admin";
    private static String host = null;

    private static int modulesPort = 8000;
    private static int restTestport = 8093;
    private static String restServerName = "TestDynamicIngest";

    private static SecurityContext secContext = null;
    private static DatabaseClient dbclient = null;
    private static DatabaseClient schemaDBclient = null;
    // Path to input docs. Location  is src/test/java/com/marklogic/client/functionaltest. Reuse existing data files
    private static String DataConfigDirPath = null;
    // Path to input api and sjs files. Folder present within DataConfigDirPath
    private static String ApiConfigDirPath = null;
    private final String query1 = "fn:count(fn:doc())";

    // Ingest endpoint ConfigName
    private static String JsonIngestConfigName = "DynamicIngestServicesForJson";
    private static String XmlIngestConfigName = "DynamicIngestServicesForXml";
    private static String TextIngestConfigName = "DynamicIngestServicesForText";
    private static String BinIngestConfigName = "DynamicIngestServicesForBin";

    // AnyDocument endpoint Ingest and Egress Config Names
    private static String AnyDocumentIngestConfigName = "DynamicIngestServicesAnyDocument";
    private static String AnyDocumentEgressConfigName = "DynamicEgressServicesAnyDocument";

    // Egress endpoint ConfigName
    private static String JsonEgressConfigName = "DynamicEgressServicesForJson";

    // Egress error endpoint ConfigName
    private static String JsonEgressErrorConfigName = "DynamicEgressServicesForJsonError";
    // Ingest error endpoint ConfigName
    private static String JsonIngestErrorConfigName = "DynamicIngestServicesForTextError";

    // Ingest and Egress endpoint ConfigName
    private static String IngestEgressSessionFieldsConfigName = "DynamicIngestEgressSessionFields";

    // Exec endpoint ConfigName
    private static String ExecWithWorkUnitConfigName = "DynamicExecWithWorkUnit";
    // Verify Annotation endpoint ConfigName
    private static String BulkSizeVerifyConfigName = "BulkSizeCheckForJson";

    // Create an URI identifiers for modules document - Client API call will be to endpoint

    //Input URI
    private static String IngestServicesJsonURI = "/dynamic/fntest/DynamicIngestServices/json/";
    private static String IngestServicesXmlURI = "/dynamic/fntest/DynamicIngestServices/xml/";
    private static String IngestServicesTextURI = "/dynamic/fntest/DynamicIngestServices/text/";
    private static String IngestServicesBinURI = "/dynamic/fntest/DynamicIngestServices/bin/";
    private static String IngestServicesJsonErrorURI = "/dynamic/fntest/DynamicIngestServicesError/json/";
    // Any Document URIs
    private static String IngestServicesAnyDocumentURI = "/dynamic/fntest/DynamicIngestServices/any/";
    private static String EgressServicesAnyDocumentURI = "/dynamic/fntest/DynamicEgressServices/any/";
    //Output URI
    private static String EgressServicesJsonURI = "/dynamic/fntest/DynamicEgressServices/json/";
    private static String EgressServicesJsonErrorURI = "/dynamic/fntest/DynamicEgressServicesError/json/";
    // Input and Output URI
    private static String IngestEgressSessionFieldsURI = "/dynamic/fntest/DynamicIngestEgressSessionFields/json/";
    // Exec endpoint URI
    private static String ExecWithWorkUnitURI = "/dynamic/fntest/DynamicExecWithWorkUnit/json/";
    // Annotation size endpoint URI
    private static String BulkSizeCheckURI = "/dynamic/fntest/BulkSizeVerify/json/";

    @BeforeAll
    public static void setUp() throws Exception {
        System.out.println("In setup");
        loadGradleProperties();

        host = getServer();
        DataConfigDirPath = getDataConfigDirPath() + "/data/";
        ApiConfigDirPath = DataConfigDirPath + "api/";

        // Create an REST App Server to validate inserts
		/*if (IsSecurityEnabled())
			setupJavaRESTServer(dbName, fNames[0], restSslServerName, getRestServerPort());
		else*/
        createDB(dbName);
        createForest(fNames[0], dbName);
        setupJavaRESTServer(dbName, fNames[0], restServerName, restTestport);

        setupAppServicesConstraint(dbName);
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

                { "int", "", "popularity", "", "false", "reject" },
                { "int", "", "id", "", "false", "reject" }
        };

        // Insert the range indices
        addRangeElementIndex(dbName, rangeElements);

        createDB(dbNameMod);
        createForest(fNamesMod[0], dbNameMod);
        // Configure App-Services server so that we can write the (sjs or xqy) modules into modules DB.
        associateRESTServerWithDB(modServerName, dbNameMod);

        // Now change the modules database on the REST server to dbNameMod from the default.
        associateRESTServerWithModuleDB(restServerName, dbNameMod);

        createUserRolesWithPrevilages("apiRole", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke", "xdmp:eval", "xdmp:eval-in");
        createRESTUser("apiUser", "ap1U53r", "apiRole", "rest-admin", "rest-writer", "rest-reader",
                "rest-extension-user", "manage-user");
        createRESTUser("secondApiUser", "ap1U53r", "apiRole", "rest-admin", "rest-writer", "rest-reader",
                "rest-extension-user", "manage-user");
        createUserRolesWithPrevilages("ForbiddenRole", "any-uri");
        createRESTUser("ForbiddenUser", "ap1U53r", "apiRole", "rest-admin", "rest-writer", "rest-reader",
                "manage-user");
        secContext = newSecurityContext("admin", "admin");

        schemaDBclient = getDatabaseClientOnDatabase(host, modulesPort, dbNameMod, user, "admin", getConnType());

        TextDocumentManager docMgr = schemaDBclient.newTextDocumentManager();
        System.out.println("API Path  : " + ApiConfigDirPath  + "config : " + JsonIngestConfigName);
        File file = new File(ApiConfigDirPath + JsonIngestConfigName + ".sjs");

        // create a handle on the content
        FileHandle handle = new FileHandle(file);
        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
        metadataHandle.getPermissions().add("apiRole", Capability.UPDATE, Capability.READ, Capability.EXECUTE);
        System.out.println("URI : " + IngestServicesJsonURI + "config : " + JsonIngestConfigName);
        // write the document content
        docMgr.write(IngestServicesJsonURI + JsonIngestConfigName + ".sjs", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + JsonIngestConfigName + ".api");
        handle = new FileHandle(file);
        docMgr.write(IngestServicesJsonURI + JsonIngestConfigName +".api", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + AnyDocumentIngestConfigName + ".sjs");
        handle = new FileHandle(file);
        docMgr.write(IngestServicesAnyDocumentURI + AnyDocumentIngestConfigName +".sjs", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + AnyDocumentIngestConfigName + ".api");
        handle = new FileHandle(file);
        docMgr.write(IngestServicesAnyDocumentURI + AnyDocumentIngestConfigName +".api", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + AnyDocumentEgressConfigName + ".sjs");
        handle = new FileHandle(file);
        docMgr.write(EgressServicesAnyDocumentURI + AnyDocumentEgressConfigName +".sjs", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + AnyDocumentEgressConfigName + ".api");
        handle = new FileHandle(file);
        docMgr.write( EgressServicesAnyDocumentURI+ AnyDocumentEgressConfigName +".api", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + XmlIngestConfigName + ".sjs");
        handle = new FileHandle(file);
        docMgr.write(IngestServicesXmlURI + XmlIngestConfigName +".sjs", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + XmlIngestConfigName + ".api");
        handle = new FileHandle(file);
        docMgr.write(IngestServicesXmlURI + XmlIngestConfigName +".api", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + TextIngestConfigName + ".sjs");
        handle = new FileHandle(file);
        docMgr.write(IngestServicesTextURI + TextIngestConfigName +".sjs", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + TextIngestConfigName + ".api");
        handle = new FileHandle(file);
        docMgr.write(IngestServicesTextURI + TextIngestConfigName +".api", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + BinIngestConfigName + ".sjs");
        handle = new FileHandle(file);
        docMgr.write(IngestServicesBinURI + BinIngestConfigName +".sjs", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + BinIngestConfigName + ".api");
        handle = new FileHandle(file);
        docMgr.write(IngestServicesBinURI + BinIngestConfigName +".api", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + JsonEgressConfigName + ".sjs");
        handle = new FileHandle(file);
        docMgr.write(EgressServicesJsonURI + JsonEgressConfigName +".sjs", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + JsonEgressConfigName + ".api");
        handle = new FileHandle(file);
        docMgr.write(EgressServicesJsonURI + JsonEgressConfigName +".api", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + JsonEgressErrorConfigName + ".sjs");
        handle = new FileHandle(file);
        docMgr.write(EgressServicesJsonErrorURI + JsonEgressErrorConfigName +".sjs", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + JsonEgressErrorConfigName + ".api");
        handle = new FileHandle(file);
        docMgr.write(EgressServicesJsonErrorURI + JsonEgressErrorConfigName +".api", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + JsonIngestErrorConfigName + ".sjs");
        handle = new FileHandle(file);
        docMgr.write(IngestServicesJsonErrorURI + JsonIngestErrorConfigName +".sjs", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + JsonIngestErrorConfigName + ".api");
        handle = new FileHandle(file);
        docMgr.write(IngestServicesJsonErrorURI + JsonIngestErrorConfigName +".api", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + IngestEgressSessionFieldsConfigName + ".sjs");
        handle = new FileHandle(file);
        docMgr.write(IngestEgressSessionFieldsURI + IngestEgressSessionFieldsConfigName +".sjs", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + IngestEgressSessionFieldsConfigName + ".api");
        handle = new FileHandle(file);
        docMgr.write(IngestEgressSessionFieldsURI + IngestEgressSessionFieldsConfigName +".api", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + ExecWithWorkUnitConfigName + ".sjs");
        handle = new FileHandle(file);
        docMgr.write(ExecWithWorkUnitURI + ExecWithWorkUnitConfigName +".sjs", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + ExecWithWorkUnitConfigName + ".api");
        handle = new FileHandle(file);
        docMgr.write(ExecWithWorkUnitURI + ExecWithWorkUnitConfigName +".api", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + BulkSizeVerifyConfigName + ".sjs");
        handle = new FileHandle(file);
        docMgr.write(BulkSizeCheckURI + BulkSizeVerifyConfigName +".sjs", metadataHandle, handle);
        file = null;
        handle = null;

        file = new File(ApiConfigDirPath + BulkSizeVerifyConfigName + ".api");
        handle = new FileHandle(file);
        docMgr.write(BulkSizeCheckURI + BulkSizeVerifyConfigName +".api", metadataHandle, handle);
        file = null;
        handle = null;
    }

    @BeforeEach
    public void before() throws Exception {
        System.out.println("In before");
        dbclient = DatabaseClientFactory.newClient(host, restTestport, secContext, ConnectionType.DIRECT);
    }

    @AfterEach
    public void after() throws Exception {
        System.out.println("In after");
        // Delete all docs
        clearDB(restTestport);
        dbclient.release();
    }

    @AfterAll
    public static void tearDownAfterClass() throws Exception {
        System.out.println("In tear down");

        deleteUserRole("apiRole");
        deleteRESTUser("apiUser");
        deleteRESTUser("secondApiUser");
        deleteUserRole("ForbiddenRole");
        deleteRESTUser("ForbiddenUser");

        associateRESTServerWithDB(modServerName, "App-Services");

        associateRESTServerWithDB(restServerName, "Documents");
        associateRESTServerWithModuleDB(restServerName, "Modules");

        deleteDB(dbName);
        deleteForest(fNames[0]);
        deleteDB(dbNameMod);
        deleteForest(fNamesMod[0]);
        deleteDB("TestDynamicIngest-modules");
        deleteForest("TestDynamicIngest-modules-1");
        // release client
        dbclient.release();
    }

    // Use /dynamic/fntest/DynamicIngestServices/json/DynamicIngestServicesForJson.sjs endpoint to test JSON Documents ingest
    @Test
    public void TestIngestEgressOnJsonDocs() throws Exception {
        System.out.println("Running TestIngestEgressOnJsonDocs");
        StringBuilder batchResults = new StringBuilder();
        StringBuilder err = new StringBuilder();

        try {
            int startBatchIdx = 0;
            int maxDocSize = 5;
            StringBuilder retryBuf = new StringBuilder();

            ObjectMapper om = new ObjectMapper();
            File apiFile = new File(ApiConfigDirPath + JsonIngestConfigName + ".api");

            JsonNode api = om.readTree(new FileReader(apiFile));
            JacksonHandle jhAPI = new JacksonHandle(api);

            String state = "{\"next\":"+startBatchIdx+"}";
            String work = "{\"max\":"+maxDocSize+"}";

            InputCaller<InputStream> ingressEndpt = InputCaller.on(dbclient, jhAPI, new InputStreamHandle());
            InputCaller.BulkInputCaller<InputStream> inputbulkCaller = ingressEndpt.bulkCaller(ingressEndpt.newCallContext()
                    .withEndpointConstantsAs(work.getBytes())
                    .withEndpointStateAs(state));

            InputCaller.BulkInputCaller.ErrorListener InerrorListener =
                    (retryCount, throwable, callContext, inputHandles)
                            -> {
                for(BufferableHandle h:inputHandles) {
                    retryBuf.append(h.toString());
                }
                         return IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.RETRY;
                    };

            File file1 = new File(DataConfigDirPath + "constraint1.json");
            InputStream s1 = new FileInputStream(file1);
            File file2 = new File(DataConfigDirPath + "constraint2.json");
            InputStream s2 = new FileInputStream(file2);
            File file3 = new File(DataConfigDirPath + "constraint3.json");
            InputStream s3 = new FileInputStream(file3);
            File file4 = new File(DataConfigDirPath + "constraint4.json");
            InputStream s4 = new FileInputStream(file4);
            File file5 = new File(DataConfigDirPath + "constraint5.json");
            InputStream s5 = new FileInputStream(file5);

            Stream<InputStream> input = Stream.of(s1, s2, s3, s4, s5);
            input.forEach(inputbulkCaller::accept);
            inputbulkCaller.awaitCompletion();

            // Test Egress on Json docs and do the assert
            int batchStartIdx = 1;
            int retry = 1;
            String collName = "JsonIngressCollection"; // See Ingress module SJS doc insert()

            String returnIndex = "{\"returnIndex\":" + batchStartIdx + "}";
            String workParams = "{\"collectionName\":\""+collName +"\", \"max\" :5}";
            OutputCaller<InputStream> unloadEndpt = OutputCaller.on(dbclient, new FileHandle(new File(ApiConfigDirPath + JsonEgressConfigName + ".api")), new InputStreamHandle());

            IOEndpoint.CallContext callContextArray = unloadEndpt.newCallContext()
                    .withEndpointStateAs(returnIndex)
                    .withEndpointConstantsAs(workParams);

            OutputCaller.BulkOutputCaller<InputStream> outputBulkCaller = unloadEndpt.bulkCaller(callContextArray);
            OutputCaller.BulkOutputCaller.ErrorListener errorListener =
                    (retryCount, throwable, callContext)
                            -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.SKIP_CALL;

            outputBulkCaller.setOutputListener(record -> {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            String s = mapper.readValue(record, ObjectNode.class).toString();
                            batchResults.append(s);
                        } catch (IOException e) {
                            err.append(e.getMessage());
                            e.printStackTrace();
                        }
                    }
            );
            outputBulkCaller.setErrorListener(errorListener);
            outputBulkCaller.awaitCompletion();
            System.out.println("Unloader completed in TestIngestEgressOnJsonDocs");

        } catch (Exception e) {
            e.printStackTrace();
            err.append(e.getMessage());
        }
        finally {
            String res = batchResults.toString();
            // # of root elements should be 5.
            System.out.println("Batch results from TestIngestEgressOnJsonDocs " + res);

            assertTrue((res.split("\\btitle\\b").length -1) == 5);
            assertTrue((res.split("\\bwrote\\b").length - 1) == 1);
            assertTrue((res.split("\\bdescribed\\b").length - 1) == 1);
            assertTrue((res.split("\\bgroundbreaking\\b").length - 1) == 1);
            assertTrue((res.split("\\bintellectual\\b").length - 1) == 1);
            assertTrue((res.split("\\bunfortunately\\b").length - 1) == 1);
            assertTrue(err.toString().isEmpty());
            System.out.println("End of TestIngestEgressOnJsonDocs");
        }
    }

    // Use /dynamic/fntest/DynamicIngestEgressSessionFields/json/DynamicIngestEgressSessionFields.sjs endpoint to test JSON Documents ingest
    @Test
    public void TestIngestEgressSessionFields() throws Exception {
        System.out.println("Running TestIngestEgressSessionFields");
        StringBuilder batchResults = new StringBuilder();
        StringBuilder err = new StringBuilder();
        String collName = "JsonIngressEgressSessionCollection"; // See module SJS doc insert()
        try {

            ObjectMapper om = new ObjectMapper();
            File apiFile = new File(ApiConfigDirPath + IngestEgressSessionFieldsConfigName + ".api");

            JsonNode api = om.readTree(new FileReader(apiFile));
            JacksonHandle jhAPI = new JacksonHandle(api);

            int batchStartIdx = 0;

            String returnIndex = "{\"returnIndex\":" + batchStartIdx + "}";
            String workParams = "{\"collectionName\":\""+collName +"\", \"max\" :5}";

            InputOutputCaller<InputStream, InputStream> IOEndpt = InputOutputCaller.on(dbclient, jhAPI, new InputStreamHandle(), new InputStreamHandle());
            IOEndpoint.CallContext callContextArray = IOEndpt.newCallContext()
                    .withEndpointStateAs(returnIndex)
                    .withEndpointConstantsAs(workParams);

            InputOutputCaller.BulkInputOutputCaller<InputStream, InputStream> IObulkCaller = IOEndpt.bulkCaller(callContextArray);
            InputOutputCaller.BulkInputOutputCaller.ErrorListener errorListener =
                    (retryCount, throwable, callContext, input)
                            -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.SKIP_CALL;

            File file6 = new File(DataConfigDirPath + "constraint1.json");
            InputStream s1 = new FileInputStream(file6);
            File file7 = new File(DataConfigDirPath + "constraint2.json");
            InputStream s2 = new FileInputStream(file7);
            File file8 = new File(DataConfigDirPath + "constraint3.json");
            InputStream s3 = new FileInputStream(file8);
            File file9 = new File(DataConfigDirPath + "constraint4.json");
            InputStream s4 = new FileInputStream(file9);
            File file10 = new File(DataConfigDirPath + "constraint5.json");
            InputStream s5 = new FileInputStream(file10);

            // Test Egress on Json docs and do the assert
            Stream<InputStream> input = Stream.of(s1, s2, s3, s4, s5);

            IObulkCaller.setOutputListener(record -> {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            String s = mapper.readValue(record, ObjectNode.class).toString();
                            batchResults.append(s);
                        } catch (Exception e) {
                            err.append(e.getMessage());
                            e.printStackTrace();
                        }
                    }
            );
            IObulkCaller.setErrorListener(errorListener);
            input.forEach(IObulkCaller::accept);
            IObulkCaller.awaitCompletion();
            System.out.println("Egress on Json docs completed in TestIngestEgressSessionFields");
        } catch (Exception e) {
            e.printStackTrace();
            err.append(e.getMessage());
        }
        finally {
            String res = batchResults.toString();
            System.out.println(res);

            assertTrue(res.contains("This is the return doc content"));
            assertTrue(res.contains("Java Client API"));
            assertTrue(err.toString().isEmpty());

            QueryManager queryMgr = dbclient.newQueryManager();
            StructuredQueryBuilder qb =  new StructuredQueryBuilder();
            StructuredQueryDefinition qd = qb.collection("JsonIngressEgressSessionCollection");
            // create handle
            JacksonHandle resultsHandle = new JacksonHandle();
            queryMgr.search(qd, resultsHandle);

            // get the result
            JsonNode resultDoc = resultsHandle.get();
            int total = resultDoc.get("total").asInt();
            assertTrue(total == 5);

            System.out.println("End of TestIngestEgressSessionFields");
        }
    }

    // Test for 50000 json docs and default inputBatchSize annotation value

    // Use /dynamic/fntest/DynamicIngestServices/json/DynamicIngestServicesForJson.sjs endpoint to test JSON Documents ingest
    @Test
    public void TestIngestEgressOnLargeNumJsonDocs() throws Exception {
        System.out.println("Running TestIngestEgressOnLargeNumJsonDocs");
        try {
            int startBatchIdx = 0;
            int maxDocSize = 50000;

            ObjectMapper om = new ObjectMapper();
            File apiFile = new File(ApiConfigDirPath + JsonIngestConfigName + ".api");

            JsonNode api = om.readTree(new FileReader(apiFile));
            JacksonHandle jhAPI = new JacksonHandle(api);

            String state = "{\"next\":"+startBatchIdx+"}";
            String work = "{\"max\":"+maxDocSize+"}";

            InputCaller<InputStream> ingressEndpt = InputCaller.on(dbclient, jhAPI, new InputStreamHandle());
            InputCaller.BulkInputCaller<InputStream> inputbulkCaller = ingressEndpt.bulkCaller(ingressEndpt.newCallContext()
                    .withEndpointConstantsAs(work.getBytes())
                    .withEndpointStateAs(state));

            String docTemplate = ("{\"k1\":\"v");
            String closeStr = "\"}";

            Stream<ByteArrayInputStream> stream = Stream.generate(() -> new ByteArrayInputStream((docTemplate +
                    Double.toString(Math.random() * 1000) + closeStr).getBytes())).limit(maxDocSize);
            stream.forEach(inputbulkCaller::accept);
            inputbulkCaller.awaitCompletion();

            // Assert
            int docCnt = dbclient.newServerEval().xquery(query1).eval().next().getNumber().intValue();
            System.out.println("TestIngestEgressOnLargeNumJsonDocs - Doc count is " + docCnt);
            assertTrue(docCnt == maxDocSize + 1);

            // Verify the Annotation value stored within a doc.
            JSONDocumentManager mgr = dbclient.newJSONDocumentManager();
            JacksonHandle jh = new JacksonHandle();
            mgr.read("/api-default-bulk-size.json", jh);
            int nAnnotValue =  jh.get().get("length").asInt();
            // 100 is default value .api -> inputBatchSize property
            System.out.println("TestIngestEgressOnLargeNumJsonDocs - inputBatchSize property value is " + nAnnotValue);
            assertTrue(nAnnotValue == 100);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("End of TestIngestEgressOnLargeNumJsonDocs");
        }
    }

    // Use /dynamic/fntest/DynamicIngestServices/xml/DynamicIngestServicesForXml.sjs endpoint to test XML Documents ingest
    @Test
    public void TestIngestEgressOnXmlDocs() throws Exception {
        System.out.println("Running TestIngestEgressOnXmlDocs");
        try {
            int startBatchIdx = 0;
            int maxDocSize = 1000;

            ObjectMapper om = new ObjectMapper();
            File apiFile = new File(ApiConfigDirPath + XmlIngestConfigName + ".api");

            JsonNode api = om.readTree(new FileReader(apiFile));
            JacksonHandle jhAPI = new JacksonHandle(api);

            String state = "{\"next\":"+startBatchIdx+"}";
            String work = "{\"max\":"+maxDocSize+"}";

            InputCaller<InputStream> ingressEndpt = InputCaller.on(dbclient, jhAPI, new InputStreamHandle());
            InputCaller.BulkInputCaller<InputStream> inputbulkCaller = ingressEndpt.bulkCaller(ingressEndpt.newCallContext()
                    .withEndpointConstantsAs(work.getBytes())
                    .withEndpointStateAs(state));

            File file1 = new File(DataConfigDirPath + "constraint1.xml");
            InputStream s1 = new FileInputStream(file1);
            File file2 = new File(DataConfigDirPath + "constraint2.xml");
            InputStream s2 = new FileInputStream(file2);
            File file3 = new File(DataConfigDirPath + "constraint3.xml");
            InputStream s3 = new FileInputStream(file3);
            File file4 = new File(DataConfigDirPath + "constraint4.xml");
            InputStream s4 = new FileInputStream(file4);
            File file5 = new File(DataConfigDirPath + "constraint5.xml");
            InputStream s5 = new FileInputStream(file5);

            Stream<InputStream> input = Stream.of(s1, s2, s3, s4, s5);
            input.forEach(inputbulkCaller::accept);
            inputbulkCaller.awaitCompletion();

            int docCnt = dbclient.newServerEval().xquery(query1).eval().next().getNumber().intValue();
            System.out.println("No. of Xml docs is " + docCnt);
            assertTrue(docCnt == 5);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("End of TestIngestEgressOnXmlDocs");
        }
    }

    // Use /dynamic/fntest/DynamicIngestServices/text/DynamicIngestServicesForText.sjs endpoint to test Text Documents ingest
    @Test
    public void TestIngestEgressOnTextDocs() throws Exception {
        System.out.println("Running TestIngestEgressOnTextDocs");
        try {
            int startBatchIdx = 0;
            int maxDocSize = 100;

            ObjectMapper om = new ObjectMapper();
            File apiFile = new File(ApiConfigDirPath + TextIngestConfigName + ".api");

            JsonNode api = om.readTree(new FileReader(apiFile));
            JacksonHandle jhAPI = new JacksonHandle(api);

            String state = "{\"next\":"+startBatchIdx+"}";
            String work = "{\"max\":"+maxDocSize+"}";

            InputCaller<String> ingressEndpt = InputCaller.on(dbclient, jhAPI, new StringHandle());
            InputCaller.BulkInputCaller<String> inputbulkCaller = ingressEndpt.bulkCaller(ingressEndpt.newCallContext()
                    .withEndpointConstantsAs(work.getBytes())
                    .withEndpointStateAs(state));
            String[] strContent = { "This is first test document",
            "This is second test document",
            "This is third test document",
            "This is fourth test document",
            "This is fifth test document"
            };

            Stream<String> input = Stream.of(strContent);
            input.forEach(inputbulkCaller::accept);
            inputbulkCaller.awaitCompletion();

            assertTrue(dbclient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 5);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("End of TestIngestEgressOnTextDocs");
        }
    }

    // Use /dynamic/fntest/DynamicIngestServices/text/DynamicIngestServicesForTextError.sjs endpoint to test Text Documents ingest
    // Verify that STOP_ALL_CALLS in client does not throw NPE. Git # 1265
    @Test
    public void TestIngestOnTextDocsError() throws Exception {
        System.out.println("Running TestIngestOnTextDocsError");
        StringBuilder errorBuf = new StringBuilder();
        try {
            int startBatchIdx = 0;
            int maxDocSize = 100;

            ObjectMapper om = new ObjectMapper();
            File apiFile = new File(ApiConfigDirPath + JsonIngestErrorConfigName + ".api");

            JsonNode api = om.readTree(new FileReader(apiFile));
            JacksonHandle jhAPI = new JacksonHandle(api);

            String state = "{\"next\":"+startBatchIdx+"}";
            String work = "{\"max\":"+maxDocSize+"}";

            InputCaller<String> ingressEndpt = InputCaller.on(dbclient, jhAPI, new StringHandle());
            InputCaller.BulkInputCaller<String> inputbulkCaller = ingressEndpt.bulkCaller(ingressEndpt.newCallContext()
                    .withEndpointConstantsAs(work.getBytes())
                    .withEndpointStateAs(state));
            String[] strContent = { "This is first test document",
                    "This is second test document",
                    "This is third test document",
                    "This is fourth test document",
                    "This is fifth test document"
            };
            InputCaller.BulkInputCaller.ErrorListener InerrorListener =
                    (retryCount, throwable, callContext, inputHandles)
                            -> {
                        for(BufferableHandle h:inputHandles) {
                            errorBuf.append(h.toString());
                        }
                        return IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.STOP_ALL_CALLS;
                    };

            Stream<String> input = Stream.of(strContent);
            input.forEach(inputbulkCaller::accept);
            inputbulkCaller.setErrorListener(InerrorListener);
            inputbulkCaller.awaitCompletion();
            String errStr = errorBuf.toString();
            //System.out.println("Error buffer when STOP_ALL_CALLS " + errorBuf.toString());
            assertTrue(!errStr.contains("Exception"));
            assertTrue(errStr.isEmpty());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("End of TestIngestEgressOnTextDocs");
        }
    }

    // Use /dynamic/fntest/DynamicIngestServices/bin/DynamicIngestServicesForBin.sjs endpoint to test Binary Documents ingest
    @Test
    public void TestIngestEgressOnBinDocs() throws Exception {
        System.out.println("Running TestIngestEgressOnBinDocs");
        try {
            int startBatchIdx = 0;
            int maxDocSize = 100;
            String binFileName = "Pandakarlino.jpg";

            ObjectMapper om = new ObjectMapper();
            File apiFile = new File(ApiConfigDirPath + BinIngestConfigName + ".api");

            JsonNode api = om.readTree(new FileReader(apiFile));
            JacksonHandle jhAPI = new JacksonHandle(api);

            String state = "{\"next\":"+startBatchIdx+"}";
            String work = "{\"max\":"+maxDocSize+"}";

            InputCaller<InputStream> ingressEndpt = InputCaller.on(dbclient, jhAPI, new InputStreamHandle());
            InputCaller.BulkInputCaller<InputStream> inputbulkCaller = ingressEndpt.bulkCaller(ingressEndpt.newCallContext()
                    .withEndpointConstantsAs(work.getBytes())
                    .withEndpointStateAs(state));

            FileInputStream s1 = new FileInputStream(DataConfigDirPath + binFileName);
            FileInputStream s2 = new FileInputStream(DataConfigDirPath + binFileName);
            FileInputStream s3 = new FileInputStream(DataConfigDirPath + binFileName);
            FileInputStream s4 = new FileInputStream(DataConfigDirPath + binFileName);
            FileInputStream s5 = new FileInputStream(DataConfigDirPath + binFileName);

            Stream<InputStream> input = Stream.of(s1, s2, s3, s4, s5);
            input.forEach(inputbulkCaller::accept);
            inputbulkCaller.awaitCompletion();

            assertTrue(dbclient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 5);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("End of TestIngestEgressOnBinDocs");
        }
    }

    // Use /dynamic/fntest/DynamicExecWithWorkUnit/json/DynamicExecWithWorkUnit.sjs endpoint to test JSON Documents ingest
    @Test
    public void TestExecWithWorkUnit() throws Exception {
        System.out.println("Running TestExecWithWorkUnit");
        try {
            int startBatchIdx = 0;
            int maxDocSize = 5;
            String collName = "JsonIngressCollection"; // See Exec module SJS doc

            ObjectMapper om = new ObjectMapper();
            File apiInFile = new File(ApiConfigDirPath + JsonIngestConfigName + ".api");

            JsonNode apiIn = om.readTree(new FileReader(apiInFile));
            JacksonHandle jhInAPI = new JacksonHandle(apiIn);

            String state1 = "{\"next\":"+startBatchIdx+"}";
            String work1 = "{\"collectionName\":\"" + collName + "\", \"max\":\"" + maxDocSize + "\"}";

            InputCaller<InputStream> ingressEndpt = InputCaller.on(dbclient, jhInAPI, new InputStreamHandle());
            InputCaller.BulkInputCaller<InputStream> inputbulkCaller = ingressEndpt.bulkCaller(ingressEndpt.newCallContext()
                    .withEndpointConstantsAs(work1.getBytes())
                    .withEndpointStateAs(state1));

            String docTemplate = ("{\"k1\":\"v");
            String closeStr = "\"}";

            Stream<ByteArrayInputStream> stream = Stream.generate(() -> new ByteArrayInputStream((docTemplate +
                    Double.toString(Math.random() * 1000) + closeStr).getBytes())).limit(maxDocSize);
            stream.forEach(inputbulkCaller::accept);
            inputbulkCaller.awaitCompletion();

            // Exec endpoint
            File apiExecFile = new File(ApiConfigDirPath + ExecWithWorkUnitConfigName + ".api");

            JsonNode apiEx = om.readTree(new FileReader(apiExecFile));
            JacksonHandle jhExAPI = new JacksonHandle(apiEx);

            String state2 = "{\"start\":"+startBatchIdx+"}";
            String work2 = "{\"max\":"+ maxDocSize + ",\"path\":\"inputs\"" +
                    ",\"firstString\":\"This is first string\"" +
                    ",\"amount\":23.5"+
                    ",\"collectionName\":\""+ collName +"\"" +
                    "}";
            ExecCaller execEndpt = ExecCaller.on(dbclient, jhExAPI);

            ExecCaller.BulkExecCaller exebulkCaller = execEndpt.bulkCaller(execEndpt.newCallContext()
                    .withEndpointConstantsAs(work2)
                    .withEndpointStateAs(state2));

            exebulkCaller.awaitCompletion();

            QueryManager queryMgr = dbclient.newQueryManager();

            // create query def
            StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
            StructuredQueryDefinition qd = qb.collection(collName);

            // create handle
            JacksonHandle resultsHandle = new JacksonHandle();
            queryMgr.search(qd, resultsHandle);

            JsonNode results = resultsHandle.get();
            int total = results.get("total").asInt();

            assertEquals(5, total);

            Set<String> uriResulttSet = new TreeSet<String>();
            Set<String> uriExptdSet = new TreeSet<String>();

            uriExptdSet.add("/marklogic/ds/test/bulkInputCaller/5.json");
            uriExptdSet.add("/marklogic/ds/test/bulkInputCaller/4.json");
            uriExptdSet.add("/marklogic/ds/test/bulkInputCaller/3.json");
            uriExptdSet.add("/marklogic/ds/test/bulkInputCaller/2.json");
            uriExptdSet.add("/marklogic/ds/test/bulkInputCaller/1.json");

            uriResulttSet.add(results.get("results").get(0).get("uri").asText());
            uriResulttSet.add(results.get("results").get(1).get("uri").asText());
            uriResulttSet.add(results.get("results").get(2).get("uri").asText());
            uriResulttSet.add(results.get("results").get(3).get("uri").asText());
            uriResulttSet.add(results.get("results").get(4).get("uri").asText());
            assertTrue(uriExptdSet.containsAll(uriResulttSet));

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("End of TestExecWithWorkUnit");
        }
    }

    // Use /dynamic/fntest/DynamicIngestServices/json/DynamicIngestServicesForJson.sjs endpoint to test JSON Documents ingest
    // The api file contents are in a JSON node; not in a file.
    @Test
    public void TestBulkAnnotationSize() {
        System.out.println("Running TestBulkAnnotationSize");
        try {
            int startBatchIdx = 0;
            int maxDocSize = 29;

            ObjectMapper om = new ObjectMapper();
            File apiFile = new File(ApiConfigDirPath + BulkSizeVerifyConfigName + ".api");

            JsonNode apiNd = om.readTree(new FileReader(apiFile)); //om.readTree(api.toString());
            JacksonHandle jhAPI = new JacksonHandle(apiNd);

            String state = "{\"next\":"+startBatchIdx+"}";
            String work = "{\"max\":"+maxDocSize+"}";

            InputCaller<InputStream> ingressEndpt = InputCaller.on(dbclient, jhAPI, new InputStreamHandle());
            InputCaller.BulkInputCaller<InputStream> inputbulkCaller = ingressEndpt.bulkCaller(ingressEndpt.newCallContext()
                    .withEndpointConstantsAs(work.getBytes())
                    .withEndpointStateAs(state));

            String docTemplate = ("{\"k1\":\"v");
            String closeStr = "\"}";

            Stream<ByteArrayInputStream> stream = Stream.generate(() -> new ByteArrayInputStream((docTemplate +
                    Double.toString(Math.random() * 1000) + closeStr).getBytes())).limit(maxDocSize);
            stream.forEach(inputbulkCaller::accept);
            inputbulkCaller.awaitCompletion();

            // Assert
            int docCnt = dbclient.newServerEval().xquery(query1).eval().next().getNumber().intValue();
            System.out.println("TestBulkAnnotationSize - Doc count is " + docCnt);
            // One additional document holds the $bulk annotation value.
            assertTrue(docCnt == maxDocSize + 1);

            // Verify the Annotation value stored within a doc.
            JSONDocumentManager mgr = dbclient.newJSONDocumentManager();
            JacksonHandle jh = new JacksonHandle();
            mgr.read("/api-bulk-size.json", jh);
            int nAnnotValue =  jh.get().get("length").asInt();
            // 8 is stored in BulkSizeCheckForJson.api -> inputBatchSize property
            System.out.println("TestBulkAnnotationSize - inputBatchSize property value is " + nAnnotValue);
            assertTrue(nAnnotValue == 8);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("End of TestBulkAnnotationSize");
        }
    }

    // Verify errors from sjs module back to client.
    @Test
    public void TestIngestEgressOnJsonDocsError() throws Exception {
        System.out.println("Running TestIngestEgressOnJsonDocsError");
        StringBuilder batchResults = new StringBuilder();

        StringBuilder retryBuf = new StringBuilder();
        try {
            int startBatchIdx = 0;
            int maxDocSize = 5;

            ObjectMapper om = new ObjectMapper();
            File apiFile = new File(ApiConfigDirPath + JsonIngestConfigName + ".api");

            JsonNode api = om.readTree(new FileReader(apiFile));
            JacksonHandle jhAPI = new JacksonHandle(api);

            String state = "{\"next\":"+startBatchIdx+"}";
            String work = "{\"max\":"+maxDocSize+"}";

            InputCaller<InputStream> ingressEndpt = InputCaller.on(dbclient, jhAPI, new InputStreamHandle());
            InputCaller.BulkInputCaller<InputStream> inputbulkCaller = ingressEndpt.bulkCaller(ingressEndpt.newCallContext()
                    .withEndpointConstantsAs(work.getBytes())
                    .withEndpointStateAs(state));

            InputCaller.BulkInputCaller.ErrorListener InerrorListener =
                    (retryCount, throwable, callContext, inputHandles)
                            -> {
                        for(BufferableHandle h:inputHandles) {
                            retryBuf.append(h.toString());
                        }
                        return IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.RETRY;
                    };

            File file1 = new File(DataConfigDirPath + "constraint1.json");
            InputStream s1 = new FileInputStream(file1);
            File file2 = new File(DataConfigDirPath + "constraint2.json");
            InputStream s2 = new FileInputStream(file2);
            File file3 = new File(DataConfigDirPath + "constraint3.json");
            InputStream s3 = new FileInputStream(file3);
            File file4 = new File(DataConfigDirPath + "constraint4.json");
            InputStream s4 = new FileInputStream(file4);
            File file5 = new File(DataConfigDirPath + "constraint5.json");
            InputStream s5 = new FileInputStream(file5);

            Stream<InputStream> input = Stream.of(s1, s2, s3, s4, s5);
            input.forEach(inputbulkCaller::accept);
            inputbulkCaller.awaitCompletion();

            // Test Egress on Json docs and do the assert
            int batchStartIdx = 1;
            int retry = 1;
            String collName = "JsonIngressCollection"; // See Ingress module SJS doc insert()

            String returnIndex = "{\"returnIndex\":" + batchStartIdx + "}";
            String workParams = "{\"collectionName\":\""+collName +"\", \"max\" :5}";
            OutputCaller<InputStream> unloadEndpt = OutputCaller.on(dbclient, new FileHandle(new File(ApiConfigDirPath + JsonEgressErrorConfigName + ".api")), new InputStreamHandle());

            IOEndpoint.CallContext callContextArray = unloadEndpt.newCallContext()
                    .withEndpointStateAs(returnIndex)
                    .withEndpointConstantsAs(workParams);

            OutputCaller.BulkOutputCaller<InputStream> outputBulkCaller = unloadEndpt.bulkCaller(callContextArray);
            OutputCaller.BulkOutputCaller.ErrorListener errorListener =
                    (retryCount, throwable, callContext)
                            -> {
                        retryBuf.append(throwable.getMessage());
                        return IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.STOP_ALL_CALLS;
                    };

            outputBulkCaller.setOutputListener(record -> {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            String s = mapper.readValue(record, ObjectNode.class).toString();
                            batchResults.append(s);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
            outputBulkCaller.setErrorListener(errorListener);
            outputBulkCaller.awaitCompletion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            assertTrue(retryBuf.toString().contains("Internal Server Error."));
            System.out.println("Error buffer is " + retryBuf.toString());
            System.out.println("Unloader completed in TestIngestEgressOnJsonDocsError");
        }
    }

    /* Use /dynamic/fntest/DynamicIngestServicesAnyDocument/any/DynamicIngestServicesAnyDocument.sjs endpoint to test any documents ingestion
    SJS module groups documents in different collections on ingest.
    Test uses same egress endpoint with anyDocument data types to retrieve different doc types (json and xml)
    Was able to retrieve multiple doc types in a single call with all doc types being in one collection.
    We would need to inspect each retrieved doc content to know what doc type will be. Refer to readline used.
    */
    @Test
    public void TestIngestEgressOnAnyDocument() throws Exception {
        System.out.println("Running TestIngestEgressOnAnyDocument");
        StringBuilder batchResultsJson = new StringBuilder();
        StringBuilder batchResultsXml = new StringBuilder();
        StringBuilder err = new StringBuilder();

        String binFileName = "Pandakarlino.jpg";

        try {
            int startBatchIdx = 0;
            int maxDocSize = 10;
            StringBuilder retryBuf = new StringBuilder();

            ObjectMapper om = new ObjectMapper();
            File apiFile = new File(ApiConfigDirPath + AnyDocumentIngestConfigName + ".api");

            JsonNode api = om.readTree(new FileReader(apiFile));
            JacksonHandle jhAPI = new JacksonHandle(api);

            String state = "{\"next\":"+startBatchIdx+"}";
            String work = "{\"max\":"+maxDocSize+"}";

            InputCaller<InputStream> ingressEndpt = InputCaller.on(dbclient, jhAPI, new InputStreamHandle());
            InputCaller.BulkInputCaller<InputStream> inputbulkCaller = ingressEndpt.bulkCaller(ingressEndpt.newCallContext()
                    .withEndpointConstantsAs(work.getBytes())
                    .withEndpointStateAs(state));

            InputCaller.BulkInputCaller.ErrorListener InerrorListener =
                    (retryCount, throwable, callContext, inputHandles)
                            -> {
                        for(BufferableHandle h:inputHandles) {
                            retryBuf.append(h.toString());
                        }
                        return IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.RETRY;
                    };

            File file1 = new File(DataConfigDirPath + "constraint1.json");
            InputStream s1 = new FileInputStream(file1);
            File file2 = new File(DataConfigDirPath + "constraint2.json");
            InputStream s2 = new FileInputStream(file2);
            File file3 = new File(DataConfigDirPath + "constraint3.json");
            InputStream s3 = new FileInputStream(file3);
            File file4 = new File(DataConfigDirPath + "constraint4.json");
            InputStream s4 = new FileInputStream(file4);
            File file5 = new File(DataConfigDirPath + "constraint5.json");
            InputStream s5 = new FileInputStream(file5);
            File file6 = new File(DataConfigDirPath + "cardinal1.xml");
            InputStream s6 = new FileInputStream(file6);
            File file7 = new File(DataConfigDirPath + "cardinal3.xml");
            InputStream s7 = new FileInputStream(file7);

            FileInputStream s8 = new FileInputStream(DataConfigDirPath + binFileName);

            String[] strContent = { "This is first test document",
                                    "This is second test document"
            };
            InputStream s9 = new ByteArrayInputStream(strContent[0].getBytes(StandardCharsets.UTF_8));
            InputStream s10 = new ByteArrayInputStream(strContent[1].getBytes(StandardCharsets.UTF_8));

            Stream<InputStream> input = Stream.of(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10);
            inputbulkCaller.setErrorListener(InerrorListener);

            input.forEach(inputbulkCaller::accept);
            inputbulkCaller.awaitCompletion();

            // Test Egress on Json docs and do the assert
            int batchStartIdx = 1;
            int retry = 1;

            String collName = "AnyDocumentJSONCollection"; // See Ingress module SJS doc insert()
            String returnIndex = "{\"returnIndex\":" + batchStartIdx + "}";
            String workParamsJson = "{\"collectionName\":\""+collName +"\", \"max\" :10}";

            OutputCaller<InputStream> unloadEndpt = OutputCaller.on(dbclient, new FileHandle(new File(ApiConfigDirPath + AnyDocumentEgressConfigName + ".api")), new InputStreamHandle());

            // Handle JSON doc egress using endpoint
            IOEndpoint.CallContext callContextArrayJson = unloadEndpt.newCallContext()
                    .withEndpointStateAs(returnIndex)
                    .withEndpointConstantsAs(workParamsJson);

            OutputCaller.BulkOutputCaller<InputStream> outputBulkCallerJson = unloadEndpt.bulkCaller(callContextArrayJson);
            OutputCaller.BulkOutputCaller.ErrorListener errorListenerJson =
                    (retryCount, throwable, callContext)
                            -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.SKIP_CALL;

            outputBulkCallerJson.setOutputListener(record -> {
                try {
                    //To determine what is in the stream, is it json, xml, txt or binary
                    BufferedReader bufRdr = new BufferedReader(new InputStreamReader(record, StandardCharsets.UTF_8));
                    String chkContent = bufRdr.readLine();
                    if (chkContent.startsWith("{") || chkContent.startsWith("[")) {
                        // JSON content
                        System.out.println("JSON Content start line is " + chkContent);
                        batchResultsJson.append(chkContent);
                        String line;
                        while ((line = bufRdr.readLine()) != null) {
                            batchResultsJson.append(line);
                        }
                    }
                } catch (Exception ex) {
                    // Might be binary file stream
                    System.out.println("Exceptions from stream read back" + ex.getMessage());
                }
                batchResultsJson.append("|");
            }
            );
            outputBulkCallerJson.setErrorListener(errorListenerJson);
            outputBulkCallerJson.awaitCompletion();

            // Handle XML doc egress using same endpoint
            collName = "AnyDocumentXMLCollection";
            String workParamsXml = "{\"collectionName\":\""+collName +"\", \"max\" :10}";
            IOEndpoint.CallContext callContextArrayXml = unloadEndpt.newCallContext()
                    .withEndpointStateAs(returnIndex)
                    .withEndpointConstantsAs(workParamsXml);

            OutputCaller.BulkOutputCaller<InputStream> outputBulkCallerXml = unloadEndpt.bulkCaller(callContextArrayXml);
            OutputCaller.BulkOutputCaller.ErrorListener errorListenerXml =
                    (retryCount, throwable, callContext)
                            -> IOEndpoint.BulkIOEndpointCaller.ErrorDisposition.SKIP_CALL;

            outputBulkCallerXml.setOutputListener(record -> {
                        try {
                            //To determine what is in the stream, is it json, xml, txt or binary
                            BufferedReader bufRdr = new BufferedReader(new InputStreamReader(record, StandardCharsets.UTF_8));
                            String chkContent = bufRdr.readLine();
                            if (chkContent.startsWith("<")) {
                                // XML content
                                System.out.println("XML Content start line is " + chkContent);
                                batchResultsXml.append(chkContent);
                                String line;
                                while ((line = bufRdr.readLine()) != null) {
                                    batchResultsXml.append(line);
                                }
                            }
                        } catch (Exception ex) {
                            // Might be binary file stream
                            System.out.println("Exceptions from stream read back" + ex.getMessage());
                        }
                        batchResultsXml.append("|");
                    }
            );
            outputBulkCallerXml.setErrorListener(errorListenerXml);
            outputBulkCallerXml.awaitCompletion();
        } catch (Exception e) {
            e.printStackTrace();
            err.append(e.getMessage());
        }
        finally {
            String resJson = batchResultsJson.toString();
            String resXml = batchResultsXml.toString();
            // # of root elements should be 5.
            System.out.println("Json Batch results from TestIngestEgressOnAnyDocument " + resJson);
            System.out.println("Xml Batch results from TestIngestEgressOnAnyDocument " + resXml);
            // Verify using QueryManager
            QueryManager queryMgr = dbclient.newQueryManager();
            StructuredQueryBuilder qb =  new StructuredQueryBuilder();
            StructuredQueryDefinition qd = qb.collection("AnyDocumentJSONCollection");
            // create handle
            JacksonHandle resultsHandle = new JacksonHandle();
            queryMgr.search(qd, resultsHandle);

            // get the result
            JsonNode resultDoc = resultsHandle.get();
            System.out.println(resultDoc.asText());
            int total = resultDoc.get("total").asInt();
            assertTrue(total == 5);

            assertTrue((resJson.split("\\btitle\\b").length -1) == 5);
            assertTrue((resJson.split("\\bwrote\\b").length - 1) == 1);
            assertTrue((resJson.split("\\bdescribed\\b").length - 1) == 1);
            assertTrue((resJson.split("\\bgroundbreaking\\b").length - 1) == 1);
            assertTrue((resJson.split("\\bintellectual\\b").length - 1) == 1);
            assertTrue((resJson.split("\\bunfortunately\\b").length - 1) == 1);

            assertTrue(resXml.contains("baz"));
            assertTrue(resXml.contains("three"));
            assertTrue(err.toString().isEmpty());
            System.out.println("End of TestIngestEgressOnAnyDocument");
        }
    }
}
