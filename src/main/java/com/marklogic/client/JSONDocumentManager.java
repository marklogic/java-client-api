package com.marklogic.client;

import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

/**
 * A JSON Document Manager supports database operations on JSON documents.
 */
public interface JSONDocumentManager extends AbstractDocumentManager<JSONReadHandle, JSONWriteHandle> {
	public String getLanguage();
    public void setLanguage(String language);
}
