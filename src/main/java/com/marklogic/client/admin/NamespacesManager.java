/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.admin;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.ResourceNotFoundException;

import javax.xml.namespace.NamespaceContext;

/**
 * The NamespacesManager provides access and editing for the namespaces defined on the server.
 * 
 * To use NamespacesManager, an application must authenticate as rest-admin.
 */
public interface NamespacesManager {
	/**
	 * Returns the namespace URI bound to the specified prefix on the server.
	 * @param prefix	the prefix for the binding
	 * @return	the namespace URI
	 */
    public String readPrefix(String prefix) throws ForbiddenUserException, FailedRequestException;
    /**
     * Reads all of the namespace bindings from the server.
     * @return	a namespace context with the bindings
     */
    public NamespaceContext readAll() throws ForbiddenUserException, FailedRequestException;
    /**
     * Creates a namespace binding on the server.
     * @param prefix	the prefix bound to the URI
     * @param namespaceURI	the URI bound to the prefix
     */
    public void addPrefix(String prefix, String namespaceURI) throws ForbiddenUserException, FailedRequestException;
    /**
     * Writes a namespace binding on the server.
     * @param prefix	the prefix bound to the URI
     * @param namespaceURI	the URI bound to the prefix
     */
    public void updatePrefix(String prefix, String namespaceURI) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Deletes a namespace binding on the server.
     * @param prefix	the prefix bound to the URI
     */
    public void deletePrefix(String prefix) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    /**
     * Deletes all namespace bindings on the server.
     */
    public void deleteAll() throws ForbiddenUserException, FailedRequestException;
    /**
     * Starts debugging client requests. You can suspend and resume debugging output
     * using the methods of the logger.
     * 
     * @param logger	the logger that receives debugging output
     */
    public void startLogging(RequestLogger logger);
    /**
     *  Stops debugging client requests.
     */
    public void stopLogging();
}
