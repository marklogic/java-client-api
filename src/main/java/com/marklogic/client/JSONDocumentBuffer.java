package com.marklogic.client;

import com.marklogic.client.docio.JSONReadHandle;
import com.marklogic.client.docio.JSONWriteHandle;

public interface JSONDocumentBuffer extends AbstractDocumentBuffer<JSONReadHandle, JSONWriteHandle> {
	public String getLanguage();
    public void setLanguage(String language);
}
