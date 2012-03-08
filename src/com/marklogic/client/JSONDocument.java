package com.marklogic.client;

import com.marklogic.client.docio.JSONReadHandle;
import com.marklogic.client.docio.JSONWriteHandle;

public interface JSONDocument extends AbstractDocument<JSONReadHandle, JSONWriteHandle> {
	public String getLanguage();
    public void setLanguage(String language);
}
