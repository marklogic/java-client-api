package com.marklogic.client;

import java.io.OutputStream;

public interface DatabaseClient {
    public Transaction openTransaction();
    
    // factory methods for documents
    public GenericDocument newDocument(String uri);
    public BinaryDocument  newBinaryDocument(String uri);
    public JSONDocument    newJSONDocument(String uri);
    public TextDocument    newTextDocument(String uri);
    public XMLDocument     newXMLDocument(String uri);
 
    public RequestLogger newLogger(OutputStream out);
 
    public QueryManager        newQueryManager();
    public QueryOptionsManager newQueryOptionsManager();
 
    public void release();
}
