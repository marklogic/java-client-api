package com.marklogic.client;

import java.io.OutputStream;

/**
 * A Database Client maintains a connection to a database and
 * instantiates document and query managers and other objects
 * that access the database using the connection.
 */
public interface DatabaseClient {
    public Transaction openTransaction();

	public DocumentIdentifier newDocumentIdentifier(String uri);

    // factory methods for document managers
    public GenericDocumentManager newDocumentManager();
    public BinaryDocumentManager  newBinaryDocumentManager();
    public JSONDocumentManager    newJSONDocumentManager();
    public TextDocumentManager    newTextDocumentManager();
    public XMLDocumentManager     newXMLDocumentManager();
 
    public RequestLogger newLogger(OutputStream out);
 
    public QueryManager        newQueryManager();
    public QueryOptionsManager newQueryOptionsManager();

    public NamespacesManager newNamespacesManager();

    /**
     * Closes a database connection.  After the connection is closed,
     * document and query managers can no longer access the database.
     */
    public void release();
}
