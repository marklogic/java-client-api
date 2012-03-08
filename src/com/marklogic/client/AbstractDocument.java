package com.marklogic.client;

import java.util.Map;
import java.util.Set;

import com.marklogic.client.docio.AbstractReadHandle;
import com.marklogic.client.docio.AbstractWriteHandle;
import com.marklogic.client.docio.JSONReadHandle;
import com.marklogic.client.docio.JSONWriteHandle;
import com.marklogic.client.docio.XMLReadHandle;
import com.marklogic.client.docio.XMLWriteHandle;

public interface AbstractDocument<R extends AbstractReadHandle, W extends AbstractWriteHandle> {
    public enum Metadata {
        ALL, COLLECTIONS, PERMISSIONS, PROPERTIES, QUALITY, NONE;
    }
    // whether all permissions and properties are replaced or only named permissions and properties
    public enum MetadataUpdate {
        REPLACE_ALL, REPLACE_NAMED;
    }

    // select categories of metadata to read, write, or reset
    public Set<Metadata> getProcessedMetadata();
    public void setProcessedMetadata(Set<Metadata> categories);
    public void setProcessedMetadata(Metadata... categories);

    // check document existence and get length
    public boolean exists();
 
    // content with optional metadata
    public <T extends R> T read(T handle);
    public <T extends R> T read(T handle, Transaction transaction);
    public void write(W handle);
    public void write(W handle, Transaction transaction);
    public void delete();
    public void delete(Transaction transaction);

    // metadata only
    public void readMetadata();
    public void readMetadata(Transaction transaction);
    public void writeMetadata();
    public void writeMetadata(Transaction transaction);
    public void resetMetadata();
    public void resetMetadata(Transaction transaction);

    public <T extends XMLReadHandle> T readMetadataAsXML(T handle);
    public <T extends XMLReadHandle> T readMetadataAsXML(T handle, Transaction transaction);
    public void writeMetadataAsXML(XMLWriteHandle handle);
    public void writeMetadataAsXML(XMLWriteHandle handle, Transaction transaction);
 
    public <T extends JSONReadHandle> T readMetadataAsJSON(T handle);
    public <T extends JSONReadHandle> T readMetadataAsJSON(T handle, Transaction transaction);
    public void writeMetadataAsJSON(JSONWriteHandle handle);
    public void writeMetadataAsJSON(JSONWriteHandle handle, Transaction transaction);

    // properties of the document including metadata but not content
    public String getUri();
    public void setUri(String uri);
    public int getByteLength();
    public String getMimetype();
    public void setMimetype(String mimetype);

    public DocumentCollections getCollections();
    public void setCollections(DocumentCollections collections);
    public void setCollections(String... collections);
    public DocumentPermissions getPermissions();
    public void setPermissions(DocumentPermissions permissions);
    public DocumentProperties getProperties();
    public void setProperties(DocumentProperties properties);
    public int getQuality();
    public void setQuality(int quality);

    public String getReadTransformName();
    public void setReadTransformName(String name);
    public Map<String,String> getReadTransformParameters();
    public void setReadTransformParameters(Map<String,String> parameters);
 
    public String getWriteTransformName();
    public void setWriteTransformName(String name);
    public Map<String,String> getWriteTransformParameters();
    public void setWriteTransformParameters(Map<String,String> parameters);
 
    public String getForestName();
    public void setForestName(String forestName);

    public MetadataUpdate getMetadataUpdatePolicy();
    public void SetMetadataUpdatePolicy(MetadataUpdate policy);

    // optimistic locking
    public boolean isVersionMatched();
    public void setVersionMatched(boolean match);
 
    // for debugging client requests
    public void startLogging(RequestLogger logger);
    public void stopLogging();
}
