package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.MarkLogicVersion;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
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

    protected static DatabaseClient client;
    protected static DatabaseClient schemasClient;
    protected static DatabaseClient adminModulesClient;

    protected static boolean isML11OrHigher;
    private static String original_http_port;

    @BeforeClass
    public static void initializeClients() throws Exception {
        loadGradleProperties();

        // Until all the tests can use the same ml-gradle-deployed app server, we need to have separate ports - one
        // for "slow" tests that setup a new app server, and one for "fast" tests that use the deployed one
        original_http_port = http_port;
        http_port = fast_http_port;
        isML11OrHigher = MarkLogicVersion.getMarkLogicVersion(connectAsAdmin()).getMajor() >= 11;
        final String schemasDbName = "java-functest-schemas";
        final String modulesDbName = "java-unittest-modules";
        if (IsSecurityEnabled()) {
            schemasClient = getDatabaseClientOnDatabase(getRestServerHostName(), getRestServerPort(), schemasDbName, OPTIC_USER, OPTIC_USER_PASSWORD, getConnType());
            client = getDatabaseClient(OPTIC_USER, OPTIC_USER_PASSWORD, getConnType());
            adminModulesClient = getDatabaseClientOnDatabase(getRestServerHostName(), getRestServerPort(), modulesDbName, getAdminUser(), getAdminPassword(), getConnType());
        } else {
            schemasClient = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), schemasDbName,
                new DatabaseClientFactory.DigestAuthContext(OPTIC_USER, OPTIC_USER_PASSWORD));
            client = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(),
                new DatabaseClientFactory.DigestAuthContext(OPTIC_USER, OPTIC_USER_PASSWORD));
            adminModulesClient = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), modulesDbName,
                new DatabaseClientFactory.DigestAuthContext(getAdminUser(), getAdminPassword()));
        }

        // Required to ensure that tests using the "/ext/" prefix work reliably. Expand to other directories as needed.
        adminModulesClient.newServerEval()
            .xquery("cts:uris((), (), cts:directory-query('/ext/', 'infinity')) ! xdmp:document-delete(.)").evalAs(String.class);

        // Clear the content and schemas databases so that the subclass can start with a "fresh" setup without any
        // data leftover from a previous test
        Stream.of(client, schemasClient).forEach(c -> deleteDocuments(c));
    }

    protected static void deleteDocuments(DatabaseClient client) {
        client.newServerEval().xquery("cts:uris((), (), cts:true-query()) ! xdmp:document-delete(.)").evalAs(String.class);
    }

    @AfterClass
    public static void classTearDown() {
        http_port = original_http_port;
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
        return DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(),
            new DatabaseClientFactory.DigestAuthContext("rest-writer", "x"), getConnType());
    }

    protected static DatabaseClient connectAsAdmin() {
        return DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(),
            new DatabaseClientFactory.DigestAuthContext(getAdminUser(), getAdminPassword()), getConnType());
    }
}
