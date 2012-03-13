package com.marklogic.client.impl;

import java.util.Map;
import java.util.Set;

import com.marklogic.client.DocumentCollections;
import com.marklogic.client.DocumentPermissions;
import com.marklogic.client.DocumentProperties;
import com.marklogic.client.JSONDocumentBuffer;
import com.marklogic.client.RequestLogger;
import com.marklogic.client.Transaction;
import com.marklogic.client.docio.JSONReadHandle;
import com.marklogic.client.docio.JSONWriteHandle;
import com.marklogic.client.docio.XMLReadHandle;
import com.marklogic.client.docio.XMLWriteHandle;

public class JSONDocumentImpl
    extends AbstractDocumentImpl<JSONReadHandle, JSONWriteHandle> 
    implements JSONDocumentBuffer
{
	JSONDocumentImpl(RESTServices services, String uri) {
		super(services, uri);
		setMimetype("application/json");
	}

	private String language;
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}

}
