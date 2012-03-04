package com.marklogic.client;

import com.marklogic.client.abstractio.XMLContentHandle;
import com.marklogic.client.abstractio.XMLReadHandle;
import com.marklogic.client.abstractio.XMLWriteHandle;

public interface XMLDocument extends AbstractDocument<XMLContentHandle, XMLReadHandle, XMLWriteHandle> {
    public enum DocumentRepair {
        FULL, NONE;
    }
 
    public DocumentRepair getDocumentRepair();
    public void setDocumentRepair(DocumentRepair policy);
}
