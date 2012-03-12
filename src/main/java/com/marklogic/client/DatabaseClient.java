package com.marklogic.client;

import java.io.OutputStream;

public interface DatabaseClient {
    public Transaction openTransaction();
    
    // factory methods for documents
    public GenericDocumentBuffer newDocumentBuffer(String uri);
    public BinaryDocumentBuffer  newBinaryDocumentBuffer(String uri);
    public JSONDocumentBuffer    newJSONDocumentBuffer(String uri);
    public TextDocumentBuffer    newTextDocumentBuffer(String uri);
    public XMLDocumentBuffer     newXMLDocumentBuffer(String uri);
 
    public RequestLogger newLogger(OutputStream out);
 
    public QueryManager        newQueryManager();
    public QueryOptionsManager newQueryOptionsManager();
 
    public void release();
}
