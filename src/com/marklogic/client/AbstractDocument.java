package com.marklogic.client;

import com.marklogic.client.docio.AbstractReadHandle;
import com.marklogic.client.docio.AbstractWriteHandle;

public interface AbstractDocument<R extends AbstractReadHandle, W extends AbstractWriteHandle> {
    public enum Metadata {
        ALL, COLLECTIONS, PERMISSIONS, PROPERTIES, QUALITY, NONE;
    }

    public boolean exists();
 
    // content with optional metadata
    public <T extends R> T read(T handle, Metadata... categories);
    public <T extends R> T read(T handle, Transaction transaction, Metadata... categories);
    public void write(W handle);
    public void write(W handle, Transaction transaction);
    public void delete();
    public void delete(Transaction transaction);

    // metadata only
    public void readMetadata(Metadata... categories);
    public void readMetadata(Transaction transaction, Metadata... categories);
    public void writeMetadata();
    public void writeMetadata(Transaction transaction);

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
