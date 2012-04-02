/*
 * Copyright 2012 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
