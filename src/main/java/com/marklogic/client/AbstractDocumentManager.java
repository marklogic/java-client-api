package com.marklogic.client;

import java.util.Map;
import java.util.Set;

import com.marklogic.client.docio.AbstractReadHandle;
import com.marklogic.client.docio.AbstractWriteHandle;
import com.marklogic.client.docio.JSONReadHandle;
import com.marklogic.client.docio.JSONWriteHandle;
import com.marklogic.client.docio.MetadataReadHandle;
import com.marklogic.client.docio.MetadataWriteHandle;
import com.marklogic.client.docio.XMLReadHandle;
import com.marklogic.client.docio.XMLWriteHandle;

public interface AbstractDocumentManager<R extends AbstractReadHandle, W extends AbstractWriteHandle> {
    public enum Metadata {
        ALL, COLLECTIONS, PERMISSIONS, PROPERTIES, QUALITY, NONE;
    }
    // whether all permissions and properties are replaced or only named permissions and properties
    public enum MetadataUpdate {
        REPLACE_ALL, REPLACE_NAMED;
    }

    // select categories of metadata to read, write, or reset
    public Set<Metadata> getMetadataCategories();
    public void setMetadataCategories(Set<Metadata> categories);
    public void setMetadataCategories(Metadata... categories);

    // check document existence and get length
    public boolean exists(DocumentIdentifier docId);
    public boolean exists(DocumentIdentifier docId, Transaction transaction);
 
    // content with optional metadata
    public <T extends R> T read(DocumentIdentifier docId, T contentHandle);
    public <T extends R> T read(DocumentIdentifier docId, MetadataReadHandle metadataHandle, T contentHandle);
    public <T extends R> T read(DocumentIdentifier docId, T contentHandle, Transaction transaction);
    public <T extends R> T read(DocumentIdentifier docId, MetadataReadHandle metadataHandle, T contentHandle, Transaction transaction);
    public void write(DocumentIdentifier docId, W contentHandle);
    public void write(DocumentIdentifier docId, MetadataWriteHandle metadata, W contentHandle);
    public void write(DocumentIdentifier docId, W contentHandle, Transaction transaction);
    public void write(DocumentIdentifier docId, MetadataWriteHandle metadata, W contentHandle, Transaction transaction);
    public void delete(DocumentIdentifier docId);
    public void delete(DocumentIdentifier docId, Transaction transaction);

    // metadata only
    public <T extends MetadataReadHandle> T readMetadata(DocumentIdentifier docId, T metadataHandle);
    public <T extends MetadataReadHandle> T readMetadata(DocumentIdentifier docId, T metadataHandle, Transaction transaction);
    public void writeMetadata(DocumentIdentifier docId, MetadataWriteHandle metadataHandle);
    public void writeMetadata(DocumentIdentifier docId, MetadataWriteHandle metadataHandle, Transaction transaction);
    public void writeDefaultMetadata(DocumentIdentifier docId);
    public void writeDefaultMetadata(DocumentIdentifier docId, Transaction transaction);

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
