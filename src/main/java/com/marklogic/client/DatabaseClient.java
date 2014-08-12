/*
 * Copyright 2012-2014 MarkLogic Corporation
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
import java.io.Serializable;

import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.extensions.ResourceManager;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.alerting.RuleManager;
import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.pojo.PojoRepository;

/**
 * A Database Client instantiates document and query managers and other objects
 * with shared access to a database.
 */
public interface DatabaseClient {
    /**
     * Starts a transaction.  You can pass the transaction to the read(), write(), or delete() methods
     * of a document manager or the search() method of a query manager to perform operations within a
     * multistatement transaction.
     * 
     * To call openTransaction(), an application must authenticate as rest-writer or rest-admin.
     * 
     * @return	a Transaction object identifying and supporting operations on the transaction
     */
	public Transaction openTransaction() throws ForbiddenUserException, FailedRequestException;
	/**
	 * Starts a transaction with the specified name, which makes the transaction easier to recognize
	 * when you get status reports.
	 * 
	 * @param name	the transaction name
     * @return	a Transaction object identifying and supporting operations on the transaction
	 */
	public Transaction openTransaction(String name) throws ForbiddenUserException, FailedRequestException;
	/**
	 * Starts a transaction with the specified name and time limit. If the transaction is not committed
	 * or rolled back within the specified time, the transaction rolls back automatically.
	 * 
	 * @param name	the transaction name
	 * @param timeLimit	the number of the transaction in seconds
     * @return	a Transaction object identifying and supporting operations on the transaction
	 */
	public Transaction openTransaction(String name, int timeLimit) throws ForbiddenUserException, FailedRequestException;

    /**
     * Creates a document manager for documents with unknown or heterogeneous formats.
     * @return	a manager supporting generic operations on documents
     */
    public GenericDocumentManager newDocumentManager();
    /**
     * Creates a document manager for documents with a binary format such as images.
     * @return	a manager supporting operations on binary documents
     */
    public BinaryDocumentManager newBinaryDocumentManager();
    /**
     * Creates a document manager for documents containing a JSON structure.
     * @return	a manager supporting operations on JSON documents
     */
    public JSONDocumentManager newJSONDocumentManager();
    /**
     * Creates a document manager for documents containing unstructured text.
     * @return	a manager supporting operations on text documents
     */
    public TextDocumentManager newTextDocumentManager();
    /**
     * Creates a document manager for documents containing XML.
     * @return	a manager supporting operations on XMLdocuments
     */
    public XMLDocumentManager newXMLDocumentManager();

    /**
     * Creates a manager for querying the database.
     * @return	a manager supporting search operations and lookup of values and tuples in indexes (also known as lexicons)
     */
    public QueryManager newQueryManager();

    /**
     * Creates a manager for building rules and rules-matching applications.
     * @return a manager for supporting rules and rule-match operations.
     */
    public RuleManager newRuleManager();
    
    /**
     * Creates a manager for configuring the REST server for the database. The
     * ServerConfigurationManager can persist query options and transforms or
     * set properties of the server. The application must have rest-admin
     * privilieges to use the ServerConfigurationManager.
     * 
     * @return	a manager for the server properties or administrative resources
     */
    public ServerConfigurationManager newServerConfigManager();

    /**
     * Creates a PojoRepository specific to the specified class and its id type. 
     * The PojoRepository provides a facade for persisting, retrieving, and
     * querying data contained in Java objects.  Annotations are required to
     * identify the id field and any fields for which you wish to create indexes.
     *
     * @param clazz the class type for this PojoRepository to handle
     * @param idClass the class type of the id field for this clazz, must obviously 
     *          be Serializable or we'll struggle to marshall it
     * @return the initialized PojoRepository
     **/
    public <T, ID extends Serializable> PojoRepository<T, ID> newPojoRepository(Class<T> clazz, Class<ID> idClass);

    /**
     * Initializes a manager for a extension resource.
     * 
     * @param resourceName	the name of the extension resource
     * @param resourceManager	the manager for the extension resource
     * @return	the initialized resource manager
     */
    public <T extends ResourceManager> T init(String resourceName, T resourceManager);

    /**
     * Initializes a manager for a extension resource.
     * 
     * @param resourceName	the name of the extension resource
     * @param resourceManager	the manager for the extension resource
     * @param scriptLanguage	the script language for the extension resource, either XQUERY (default) or JAVASCRIPT
     * @return	the initialized resource manager
     */
    public <T extends ResourceManager> T init(String resourceName, T resourceManager, 
	  ExtensionMetadata.ScriptLanguage scriptLanguage);

    /**
     * Creates a logger for document and query requests.  To merge the logging output
     * with the output from other loggers, pass the output stream used by the other
     * loggers.
     * 
     * @param out	the output stream for the logging output
     * @return	the logger for client requests
     */
    public RequestLogger newLogger(OutputStream out);

    /**
     * Closes the database client and releases associated resources.  After the client is closed,
     * document and query managers can no longer access the database.
     */
    public void release();

    /**
     * Returns the client object from the library that implements communication with the
     * server.  You should call this method only when you need short-term workarounds such
     * as configuring communication with the server.  The client implementation object and
     * library may change without notice or be removed without replacement in a future release.
     * 
     * In addition, your changes to the configuration of the client implementation object
     * could impair the operation of the MarkLogic Java Client API.  In short, the client
     * implementation object should be used only on an interim basis by experts who test
     * thoroughly to avoid unwanted side effects.
     * 
     * You can call the getClass().getName() and getClass().getPackage().getName() to discover
     * the class of the current implementation object.
     * @return	the object implementing communication with the server 
     */
    public Object getClientImplementation();
}
