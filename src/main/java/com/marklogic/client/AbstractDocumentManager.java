package com.marklogic.client;

import java.util.Map;
import java.util.Set;

import com.marklogic.client.docio.AbstractReadHandle;
import com.marklogic.client.docio.AbstractWriteHandle;
import com.marklogic.client.docio.MetadataReadHandle;
import com.marklogic.client.docio.MetadataWriteHandle;

/**
 * A Document Manager provides database operations on a document.
 * 
 * @param <R> the set of handles for reading the document content from the database
 * @param <W> the set of handles for writing the document content to the database
 */
public interface AbstractDocumentManager<R extends AbstractReadHandle, W extends AbstractWriteHandle> {
    /**
     * The Metadata enumeration specifies the categories of metadata read from or written to the database.
     */
	public enum Metadata {
        ALL, COLLECTIONS, PERMISSIONS, PROPERTIES, QUALITY;
    }

	// whether all permissions and properties are replaced or only named permissions and properties
    public enum MetadataUpdate {
        REPLACE_ALL, REPLACE_NAMED;
    }

    /**
     * Checks whether a document exists and gets its length and format
     * 
     * @param docId
     * @return
     */
    public boolean exists(DocumentIdentifier docId);
    /**
     * Checks whether a document exists in an open transaction and gets its length and format
     * 
     * @param docId
     * @param transaction
     * @return
     */
    public boolean exists(DocumentIdentifier docId, Transaction transaction);
 
    /**
     * Reads the document content from the database in the representation provided by the handle
     * 
     * @param <T>
     * @param docId
     * @param contentHandle
     * @return
     */
    public <T extends R> T read(DocumentIdentifier docId, T contentHandle);
    /**
     * Reads the document metadata and content from the database in the representations provided by the handles
     * 
     * @param <T>
     * @param docId
     * @param metadataHandle
     * @param contentHandle
     * @return
     */
    public <T extends R> T read(DocumentIdentifier docId, MetadataReadHandle metadataHandle, T contentHandle);
    /**
     * Reads the document content from an open database transaction in the representation provided by the handle
     * 
     * @param <T>
     * @param docId
     * @param contentHandle
     * @param transaction
     * @return
     */
    public <T extends R> T read(DocumentIdentifier docId, T contentHandle, Transaction transaction);
    /**
     * Reads the document metadata and content from an open database transaction in the representations provided by the handles
     * 
     * @param <T>
     * @param docId
     * @param metadataHandle
     * @param contentHandle
     * @param transaction
     * @return
     */
    public <T extends R> T read(DocumentIdentifier docId, MetadataReadHandle metadataHandle, T contentHandle, Transaction transaction);

    /**
     * Writes the document content to the database from the representation provided by the handle
     * 
     * @param docId
     * @param contentHandle
     */
    public void write(DocumentIdentifier docId, W contentHandle);
    /**
     * Writes the document metadata and content to the database from the representations provided by the handles
     * 
     * @param docId
     * @param metadata
     * @param contentHandle
     */
    public void write(DocumentIdentifier docId, MetadataWriteHandle metadata, W contentHandle);
    /**
     * Writes the document content to an open database transaction from the representation provided by the handle
     * 
     * @param docId
     * @param contentHandle
     * @param transaction
     */
    public void write(DocumentIdentifier docId, W contentHandle, Transaction transaction);
    /**
     * Writes the document metadata and content to an open database transaction from the representations provided by the handles
     * 
     * @param docId
     * @param metadata
     * @param contentHandle
     * @param transaction
     */
    public void write(DocumentIdentifier docId, MetadataWriteHandle metadata, W contentHandle, Transaction transaction);

    /**
     * Deletes the document metadata and content from the database
     * 
     * @param docId
     */
    public void delete(DocumentIdentifier docId);
    /**
     * Deletes the document metadata and content from an open database transaction
     * 
     * @param docId
     * @param transaction
     */
    public void delete(DocumentIdentifier docId, Transaction transaction);

    /**
     * Reads the document metadata from the database in the representation provided by the handle
     * 
     * @param <T>
     * @param docId
     * @param metadataHandle
     * @return
     */
    public <T extends MetadataReadHandle> T readMetadata(DocumentIdentifier docId, T metadataHandle);
    /**
     * Reads the document metadata from an open database transaction in the representation provided by the handle
     * 
     * @param <T>
     * @param docId
     * @param metadataHandle
     * @param transaction
     * @return
     */
    public <T extends MetadataReadHandle> T readMetadata(DocumentIdentifier docId, T metadataHandle, Transaction transaction);

    /**
     * Writes the document metadata to the database from the representation provided by the handle
     * 
     * @param docId
     * @param metadataHandle
     */
    public void writeMetadata(DocumentIdentifier docId, MetadataWriteHandle metadataHandle);
    /**
     * Writes the document metadata to an open database transaction from the representation provided by the handle
     * 
     * @param docId
     * @param metadataHandle
     * @param transaction
     */
    public void writeMetadata(DocumentIdentifier docId, MetadataWriteHandle metadataHandle, Transaction transaction);

    /**
     * Reverts the document metadata in the database to the defaults
     * 
     * @param docId
     */
    public void writeDefaultMetadata(DocumentIdentifier docId);
    /**
     * Reverts the document metadata in an open database transaction to the defaults
     * 
     * @param docId
     * @param transaction
     */
    public void writeDefaultMetadata(DocumentIdentifier docId, Transaction transaction);

    /**
     * Gets the format of the managed documents
     * 
     * @return
     */
    public Format getContentFormat();

    /**
     * Returns the categories of metadata to read or write
     * 
     * @return
     */
    public Set<Metadata> getMetadataCategories();
    /**
     * Specifies the categories of metadata to read or write
     * 
     * @param categories
     */
    public void setMetadataCategories(Set<Metadata> categories);
    /**
     * Specifies the categories of metadata to read or write
     * 
     * @param categories
     */
    public void setMetadataCategories(Metadata... categories);

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
