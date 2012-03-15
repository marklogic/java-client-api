package com.marklogic.client;

import com.marklogic.client.docio.XMLReadHandle;
import com.marklogic.client.docio.XMLWriteHandle;

public interface XMLDocumentManager extends AbstractDocumentManager<XMLReadHandle, XMLWriteHandle> {
    public enum DocumentRepair {
        FULL, NONE;
    }
 
    public DocumentRepair getDocumentRepair();
    public void setDocumentRepair(DocumentRepair policy);
}
