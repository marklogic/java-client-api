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

/**
 * The NamespacesManager provides access and editing for the namespaces defined on the server.
 * 
 * To use NamespacesManager, an application must authenticate as rest-admin.
 */
public interface NamespacesManager {
    public String readPrefix(String prefix) throws ForbiddenUserException, FailedRequestException;
    public EditableNamespaceContext readAll() throws ForbiddenUserException, FailedRequestException;
    public void addPrefix(String prefix, String namespaceURI) throws ForbiddenUserException, FailedRequestException;
    public void updatePrefix(String prefix, String namespaceURI) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void deletePrefix(String prefix) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
    public void deleteAll() throws ForbiddenUserException, FailedRequestException;
    public void startLogging(RequestLogger logger);
    public void stopLogging();
}
