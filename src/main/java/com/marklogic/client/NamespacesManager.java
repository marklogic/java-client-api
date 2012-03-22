package com.marklogic.client;

/**
 * The NamespacesManager provides access and editing for the namespaces defined on the server.
 */
public interface NamespacesManager {
    public String readPrefix(String prefix);
    public EditableNamespaceContext readAll();
    public void addPrefix(String prefix, String namespaceURI);
    public void updatePrefix(String prefix, String namespaceURI);
    public void deletePrefix(String prefix);
    public void deleteAll();
}
