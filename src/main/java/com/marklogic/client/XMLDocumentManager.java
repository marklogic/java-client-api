package com.marklogic.client;

import com.marklogic.client.io.DBResolver;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * A XML Document Manager supports database operations on XML documents.
 */
public interface XMLDocumentManager extends AbstractDocumentManager<XMLReadHandle, XMLWriteHandle> {
    public enum DocumentRepair {
        FULL, NONE;
    }

    public DBResolver newDBResolver();

    public DocumentRepair getDocumentRepair();
    public void setDocumentRepair(DocumentRepair policy);
}
