package com.marklogic.client;

import java.io.OutputStream;

public interface DatabaseClient {
    public Transaction openTransaction();

    // factory methods for documents
    public GenericDocumentManager newDocumentManager();
    public BinaryDocumentManager  newBinaryDocumentManager();
    public JSONDocumentManager    newJSONDocumentManager();
    public TextDocumentManager    newTextDocumentManager();
    public XMLDocumentManager     newXMLDocumentManager();
 
    public RequestLogger newLogger(OutputStream out);
 
    public QueryManager        newQueryManager();
    public QueryOptionsManager newQueryOptionsManager();
 
    public void release();
}
