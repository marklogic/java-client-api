package com.marklogic.client;

public interface NamespacesManager {
    public String readPrefix(String prefix);
    public EditableNamespaceContext readAll();
    public void writePrefix(String prefix, String namespaceURI);
    public void deletePrefix(String prefix);
    public void deleteAll();
}
