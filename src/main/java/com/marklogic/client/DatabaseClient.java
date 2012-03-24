package com.marklogic.client;

import java.io.OutputStream;

/**
 * A Database Client maintains a connection to a database and
 * instantiates document and query managers and other objects
 * that access the database using the connection.
 */
public interface DatabaseClient {
    /**
     * Starts a transaction.  You can pass the transaction to the read(), write(), or delete() methods
     * of a document manager or the search() method of a query manager to perform operations within a
     * multistatement transaction.
     * 
     * To call openTransaction(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @return
     * @throws ForbiddenUserException
     * @throws FailedRequestException
     */
	public Transaction openTransaction() throws ForbiddenUserException, FailedRequestException;

	public DocumentIdentifier newDocId(String uri);

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
