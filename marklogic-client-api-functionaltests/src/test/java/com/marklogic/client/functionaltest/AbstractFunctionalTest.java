package com.marklogic.client.functionaltest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.MarkLogicVersion;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
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
public class AbstractFunctionalTest extends BasicJavaClientREST {

    protected final static String OPTIC_USER = "opticUser";
    protected final static String OPTIC_USER_PASSWORD = "0pt1c";

    protected static DatabaseClient client;
    protected static DatabaseClient schemasClient;
    protected static boolean isML11OrHigher;

    @BeforeClass
    public static void initializeClients() throws Exception {
        loadGradleProperties();
        final String schemasDbName = "java-functest-schemas";
        if (IsSecurityEnabled()) {
            schemasClient = getDatabaseClientOnDatabase(getRestServerHostName(), getRestServerPort(), schemasDbName, OPTIC_USER, OPTIC_USER_PASSWORD, getConnType());
            client = getDatabaseClient(OPTIC_USER, OPTIC_USER_PASSWORD, getConnType());
        } else {
            schemasClient = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), schemasDbName,
                new DatabaseClientFactory.DigestAuthContext(OPTIC_USER, OPTIC_USER_PASSWORD));
            client = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(),
                new DatabaseClientFactory.DigestAuthContext(OPTIC_USER, OPTIC_USER_PASSWORD));
        }
        isML11OrHigher = MarkLogicVersion.getMarkLogicVersion(client).getMajor() >= 11;

        // Clear the content and schemas databases so that the subclass can start with a "fresh" setup without any
        // data leftover from a previous test
        Stream.of(client, schemasClient).forEach(c -> {
            c.newServerEval().xquery("cts:uris((), (), cts:true-query()) ! xdmp:document-delete(.)").evalAs(String.class);
        });
    }

    @AfterClass
    public static void clearContentAndSchemaDatabases() {
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

}
