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
}
