package com.marklogic.client;

import com.marklogic.client.abstractio.AbstractContentHandle;
import com.marklogic.client.abstractio.AbstractReadHandle;
import com.marklogic.client.abstractio.AbstractWriteHandle;

public interface AbstractDocument<N extends AbstractContentHandle, R extends AbstractReadHandle, W extends AbstractWriteHandle> {
    public enum Metadata {
        ALL, COLLECTIONS, PERMISSIONS, PROPERTIES, QUALITY, NONE;
    }

    // factory method for content handles
    public <T extends N> T newHandle(Class<T> as);
 
    public boolean exists();
 
    // content with optional metadata
    public <T extends R> T read(Class<T> handleAs, Metadata... categories);
    public <T extends R> T read(Class<T> handleAs, Transaction transaction, Metadata... categories);
    public void write(W handle);
    public void write(W handle, Transaction transaction);
    public void delete();
    public void delete(Transaction transaction);
 
    // properties of the document including metadata but not content
    public String getUri();
    public void setUri(String uri);
    public DocumentCollections getCollections();
    public void setCollections(DocumentCollections collections);
    public DocumentPermissions getPermissions();
    public void setPermissions(DocumentPermissions permissions);
    public DocumentProperties getProperties();
    public void setProperties(DocumentProperties properties);
    public int getQuality();
    public void setQuality(int quality);
 
    // optimistic locking
    public boolean isVersionMatched();
    public void setVersionMatched(boolean match);
 
    // for debugging client requests
    public void startLogging(RequestLogger logger);
    public void stopLogging();
}
