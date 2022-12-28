package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicVersion;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * Abstract class for functional tests that depend on the test app in ./marklogic-client-api/src/test, which is
 * deployed via ml-gradle. These tests are intended to be far faster than the other functional tests, as they do not
 * need to spend considerable time (usually over a minute) setting up and then tearing down a complete test app.
 */
public abstract class AbstractFunctionalTest extends BasicJavaClientREST {

    protected final static String OPTIC_USER = "opticUser";
    protected final static String OPTIC_USER_PASSWORD = "0pt1c";
    protected final static String DB_NAME = "java-functest";

    protected static DatabaseClient client;
    protected static DatabaseClient schemasClient;
    protected static DatabaseClient adminModulesClient;

    protected static boolean isML11OrHigher;
    private static String originalHttpPort;
	private static String originalRestServerName;

    @BeforeAll
    public static void initializeClients() throws Exception {
        loadGradleProperties();

        // Until all the tests can use the same ml-gradle-deployed app server, we need to have separate ports - one
        // for "slow" tests that setup a new app server, and one for "fast" tests that use the deployed one
        originalHttpPort = http_port;
        http_port = fast_http_port;
		originalRestServerName = restServerName;
		restServerName = "java-functest";

        MarkLogicVersion version = MarkLogicVersion.getMarkLogicVersion(connectAsAdmin());
        System.out.println("ML version: " + version.getVersionString());
        isML11OrHigher = version.getMajor() >= 11;
        final String schemasDbName = "java-functest-schemas";
        final String modulesDbName = "java-unittest-modules";
        if (IsSecurityEnabled()) {
            schemasClient = getDatabaseClientOnDatabase(getRestServerHostName(), getRestServerPort(), schemasDbName, OPTIC_USER, OPTIC_USER_PASSWORD, getConnType());
            client = getDatabaseClient(OPTIC_USER, OPTIC_USER_PASSWORD, getConnType());
            adminModulesClient = getDatabaseClientOnDatabase(getRestServerHostName(), getRestServerPort(), modulesDbName, getAdminUser(), getAdminPassword(), getConnType());
        } else {
            schemasClient = newClient(getRestServerHostName(), getRestServerPort(), schemasDbName,
                newSecurityContext(OPTIC_USER, OPTIC_USER_PASSWORD));
            client = newClient(getRestServerHostName(), getRestServerPort(), null,
                newSecurityContext(OPTIC_USER, OPTIC_USER_PASSWORD));
            adminModulesClient = newClient(getRestServerHostName(), getRestServerPort(), modulesDbName,
                newSecurityContext(getAdminUser(), getAdminPassword()));
        }

        // Required to ensure that tests using the "/ext/" prefix work reliably. Expand to other directories as needed.
        adminModulesClient.newServerEval()
            .xquery("cts:uris((), (), cts:directory-query('/ext/', 'infinity')) ! xdmp:document-delete(.)").evalAs(String.class);

        // Clear the content and schemas databases so that the subclass can start with a "fresh" setup without any
        // data leftover from a previous test
        Stream.of(connectAsAdmin(), schemasClient).forEach(c -> deleteDocuments(c));
    }

    protected static void deleteDocuments(DatabaseClient client) {
        client.newServerEval().xquery("cts:uris((), (), cts:true-query()) ! xdmp:document-delete(.)").evalAs(String.class);
    }

    @AfterAll
    public static void classTearDown() {
        http_port = originalHttpPort;
		restServerName = originalRestServerName;
        client.release();
        schemasClient.release();
    }

    protected static void loadFileToDB(DatabaseClient client, String filename, String uri, String type, String[] collections) throws IOException, ParserConfigurationException,
        SAXException {
        // create doc manager
        DocumentManager docMgr = null;
        docMgr = documentMgrSelector(client, docMgr, type);

        final String datasource = "src/test/java/com/marklogic/client/functionaltest/data/optics/";
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
    }

    /**
     * Function to select and create document manager based on the type
     *
     * @param client
     * @param docMgr
     * @param type
     * @return
     */
    private static DocumentManager documentMgrSelector(DatabaseClient client, DocumentManager docMgr, String type) {
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

    protected final String toWKT(String latLon) {
        if (isML11OrHigher) {
            String[] parts = latLon.split(",");
            return "POINT(" + parts[1] + " " + parts[0] + ")";
        }
        return latLon;
    }

    protected static DatabaseClient connectAsRestWriter() {
        return newClient(getRestServerHostName(), getRestServerPort(),
            newSecurityContext("rest-writer", "x"), getConnType());
    }

    protected static DatabaseClient connectAsAdmin() {
        return newClient(getRestServerHostName(), getRestServerPort(),
            newSecurityContext(getAdminUser(), getAdminPassword()), getConnType());
    }

    protected static void removeFieldIndices() {
        ObjectNode config = new ObjectMapper().createObjectNode();
        config.put("database-name", DB_NAME);
        config.putArray("field");
        new DatabaseManager(newManageClient()).save(config.toString());
    }

    /**
     * Temporary patch for tests that use Optic's from-triples, which does not seem happy with the existence of one
     * or more of these fields. For example, the testRedactNumber test in TestOpticEnhancements fails because 6250 (!)
     * rows are returned instead of 2.
     */
    protected static void restoreFieldIndices() {
        String json = "[\n" +
            "    {\n" +
            "      \"field-name\": \"\",\n" +
            "      \"include-root\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"field-name\": \"pop\",\n" +
            "      \"include-root\": false,\n" +
            "      \"included-element\": [\n" +
            "        {\n" +
            "          \"namespace-uri\": \"\",\n" +
            "          \"localname\": \"popularity\",\n" +
            "          \"weight\": 2,\n" +
            "          \"attribute-namespace-uri\": \"\",\n" +
            "          \"attribute-localname\": \"\",\n" +
            "          \"attribute-value\": \"\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"field-name\": \"para\",\n" +
            "      \"include-root\": false,\n" +
            "      \"included-element\": [\n" +
            "        {\n" +
            "          \"namespace-uri\": \"\",\n" +
            "          \"localname\": \"p\",\n" +
            "          \"weight\": 5,\n" +
            "          \"attribute-namespace-uri\": \"\",\n" +
            "          \"attribute-localname\": \"\",\n" +
            "          \"attribute-value\": \"\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"field-name\": \"description\",\n" +
            "      \"include-root\": true,\n" +
            "      \"included-element\": [\n" +
            "        {\n" +
            "          \"namespace-uri\": \"\",\n" +
            "          \"localname\": \"description\",\n" +
            "          \"weight\": 1,\n" +
            "          \"attribute-namespace-uri\": \"\",\n" +
            "          \"attribute-localname\": \"\",\n" +
            "          \"attribute-value\": \"\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"field-name\": \"bbqtext\",\n" +
            "      \"include-root\": true,\n" +
            "      \"included-element\": [\n" +
            "        {\n" +
            "          \"namespace-uri\": \"http://example.com\",\n" +
            "          \"localname\": \"title\",\n" +
            "          \"weight\": 1,\n" +
            "          \"attribute-namespace-uri\": \"\",\n" +
            "          \"attribute-localname\": \"\",\n" +
            "          \"attribute-value\": \"\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"namespace-uri\": \"http://example.com\",\n" +
            "          \"localname\": \"abstract\",\n" +
            "          \"weight\": 1,\n" +
            "          \"attribute-namespace-uri\": \"\",\n" +
            "          \"attribute-localname\": \"\",\n" +
            "          \"attribute-value\": \"\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]";

        try {
            JsonNode fieldConfig = new ObjectMapper().readTree(json);
            ObjectNode config = new ObjectMapper().createObjectNode();
            config.put("database-name", DB_NAME);
            config.set("field", fieldConfig);
            new DatabaseManager(newManageClient()).save(config.toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to restore field config", e);
        }
    }

    public static void assertColumnInfosExist(String actualColumnInfo, ColumnInfo... expectedColumnInfos) {
        Stream
            .of(expectedColumnInfos)
            .map(columnInfo -> columnInfo.getExpectedJson())
            .forEach(expectedJson -> Assertions.assertTrue(
				actualColumnInfo.contains(expectedJson),
				"Did not find expected JSON: " + expectedJson + "; colInfo: " + actualColumnInfo
            ));
    }
}
