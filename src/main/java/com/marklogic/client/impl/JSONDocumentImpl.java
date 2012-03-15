package com.marklogic.client.impl;

import com.marklogic.client.JSONDocumentManager;
import com.marklogic.client.docio.JSONReadHandle;
import com.marklogic.client.docio.JSONWriteHandle;

public class JSONDocumentImpl
    extends AbstractDocumentImpl<JSONReadHandle, JSONWriteHandle> 
    implements JSONDocumentManager
{
	JSONDocumentImpl(RESTServices services) {
		super(services,"application/json");
	}

	private String language;
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}

}
